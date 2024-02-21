package com.lowes.web.services;

import com.lowes.web.model.group.GroupModel;
import com.lowes.web.model.user.UserModel;
import com.lowes.web.util.VendorContactsUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTCollection;
import wt.log4j.LogR;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/****
 * Remove, Update Vendor users.
 *
 * @author Rajesh Chandan Sahu (rajeshchandan.sahu@lowes.com)
 */
public class VendorContactsService {
    public static final String STATUS = "Status";
    private static final Logger logger = LogR.getLogger(VendorContactsService.class.getName());
    public static final String USER_NAME = "userName";

    public static final GroupModel groupService = new GroupModel();
    public static final UserModel usersService = new UserModel();

    /**
     * Remove, Update Vendor users.
     *
     * @param data - json input
     * @return - json Object
     * @throws WTException             - WTException
     * @throws WTPropertyVetoException - WTPropertyVetoException
     */
    public JSONObject addUpdateVendorContacts(JSONObject data) throws WTException, WTPropertyVetoException {

        logger.log(Level.DEBUG, "received input :{}", data);

        JSONObject response = new JSONObject();
        JSONObject groupJson = new JSONObject();
        JSONArray userJsonArray = new JSONArray();

        //PTC FlexPLM External License
        WTGroup licensors = VendorContactsUtil.getLicensors();
        logger.log(Level.DEBUG, "Find licensors group result :{}", licensors);

        //stops execution when unable to fetch license group
        if (Objects.isNull(licensors)) {
            response.put(STATUS, "Unable to proceed with PTC FlexPLM External License group");
            logger.log(Level.DEBUG, "Unable to proceed with PTC FlexPLM External License group");
            return response;
        }

        WTGroup vendorGroup = null;
        String vendorGroupName = String.valueOf(data.get("groupName"));
        groupJson.put("Group Name", vendorGroupName);

        //retrieving vendor group
        if (vendorGroupName != null && vendorGroupName.length() > 1) {
            vendorGroup = groupService.findGroup(vendorGroupName);
            logger.log(Level.DEBUG, "Find group result :{}", vendorGroup);
        }

        if (!Objects.isNull(vendorGroup)) {
            groupJson.put(STATUS, "Found Vendor Group, user info updated");
        }

        //creating new vendor Group, while group is not exist
        if (Objects.isNull(vendorGroup)) {
            vendorGroup = groupService.createGroup(vendorGroupName);
            WTGroup vendors = VendorContactsUtil.getVENDORS();
            vendors.addMember(vendorGroup);
            logger.log(Level.DEBUG, "Create Group result :{}", vendorGroup);
            groupJson.put(STATUS, "Successfully Created Vendor Group");
        }

        //stops execution while unable to process vendor group
        if (Objects.isNull(vendorGroup)) {
            response.put(STATUS, "Unable to proceed with group : " + vendorGroupName);
            logger.log(Level.DEBUG, "Unable to proceed with group :{}", vendorGroupName);
            return response;
        }

        //retrieving vendor group users.
        Map<String, WTPrincipal> vendorGrpUsers = groupService.getGroupMembers(vendorGroup);
        logger.log(Level.DEBUG, "Total Group member count : {}", vendorGrpUsers.size());

        JSONArray processUsers = new JSONArray();
        JSONArray users = (JSONArray) data.get("contacts");
        for (Object o : users) {
            JSONObject user = (JSONObject) o;
            String userName = (String) user.get(USER_NAME);

            logger.log(Level.DEBUG, "Processing user name : {}", userName);
            if (vendorGrpUsers.containsKey(userName)) {
                usersService.updateUser(user, (WTUser) vendorGrpUsers.get(userName));
                logger.log(Level.DEBUG, "User is already added skipping to next user : {}", userName);
                vendorGrpUsers.remove(userName);
                user.put(STATUS, "No Change, This User already present in Vendor group");
                userJsonArray.add(user);
            } else {
                processUsers.add(user);
            }
        }
        userJsonArray.addAll(processVendorContacts(processUsers, vendorGroup, licensors));

        removeUsersFromVGroup(vendorGrpUsers, vendorGroup, userJsonArray);

        groupJson.put("VendorUsers", userJsonArray);
        response.put("Data", groupJson);
        response.put("StatusCode", 200);
        response.put(STATUS, "Vendor Contacts Processed Successfully");

        return response;
    }

    private JSONArray processVendorContacts(JSONArray processUsers, WTGroup vendorGroup, WTGroup licensors) throws WTException, WTPropertyVetoException {

        WTUser wtUser;
        String status;

        List<WTPrincipal> users = new ArrayList<>();

        for (Object o : processUsers) {
            JSONObject user = (JSONObject) o;
            String userName = (String) user.get(USER_NAME);

            logger.log(Level.DEBUG, "Executing findUser() : {}", userName);
            wtUser = usersService.findUser(userName);
            logger.log(Level.DEBUG, "FindUser result : {}", wtUser);
            if (Objects.isNull(wtUser)) {
                logger.log(Level.DEBUG, "Creating a new User : {}", userName);
                wtUser = usersService.addUser(user);
                logger.log(Level.DEBUG, "Added User? {}", wtUser);
                users.add(wtUser);
                status = "user Created,";
            } else {
                wtUser = usersService.updateUser(user, wtUser);
                status = "user Updated,";
                users.add(wtUser);
            }
            user.put(STATUS, status);
        }
        WTPrincipal[] userAry = new WTPrincipal[users.size()];
        users.toArray(userAry);

        vendorGroup.addMembers(userAry);
        licensors.addMembers(userAry);

        for (Object o : processUsers) {

            JSONObject user = (JSONObject) o;
            status = (String) user.get(STATUS);
            status = status.concat(" added to vendor and external license group.");
            user.put(STATUS, status);
        }
        return processUsers;
    }

    private void removeUsersFromVGroup(Map<String, WTPrincipal> vendorGrpUsers, WTGroup vendorGroup, JSONArray userJsonArray) throws WTException {

        String groupName = vendorGroup.getName();
        Map<String, List<String>> userGroups = usersService.groupMembers(vendorGrpUsers.values());
        WTCollection userRemoval = new WTArrayList();
        for (Map.Entry<String, WTPrincipal> entry : vendorGrpUsers.entrySet()) {
            JSONObject userJson = new JSONObject();
            WTUser userObj = (WTUser) entry.getValue();
            String userName = userObj.getName();

            vendorGroup.removeMember(userObj);
            List<String> linkedGroups = userGroups.get(userName);
            logger.log(Level.DEBUG, "user name : {} , is part of groups : {}", userName, linkedGroups);

            boolean validRemoval = Objects.isNull(linkedGroups);
            if (Objects.nonNull(linkedGroups) && linkedGroups.size() == 1 && linkedGroups.get(0).equals(groupName)) {
                validRemoval = true;
            }

            if (validRemoval) {
                userJson.put(USER_NAME, entry.getKey());
                userJson.put("fullName", userObj.getFullName());
                userJson.put("email", userObj.getEMail());

                userRemoval.add(userObj);

                userJson.put(STATUS, "Removed legacy User from system");
                userJsonArray.add(userJson);
            }

        }
        //delete users
        usersService.removeUsers(userRemoval);

    }


}
