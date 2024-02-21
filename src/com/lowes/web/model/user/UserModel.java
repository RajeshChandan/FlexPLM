package com.lowes.web.model.user;

import com.lowes.web.util.AppUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTCollection;
import wt.log4j.LogR;
import wt.org.*;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.*;

/****
 * Standard  User model class has below functions
 *  - fetch user by name
 *  - Remove User, Users
 *  - fetch groups of users
 *  - create user
 *
 * @author Rajesh Chandan Sahu (rajeshchandan.sahu@lowes.com)
 */
public class UserModel {

    private static final Logger logger = LogR.getLogger(UserModel.class.getName());
    public static final String EMAIL = "email";

    public WTUser findUser(String userName) throws WTException {

        QuerySpec qs = new QuerySpec(WTUser.class);
        int[] index0 = new int[]{0};
        SearchCondition sc = new SearchCondition(WTUser.class, _WTPrincipal.NAME, SearchCondition.LIKE, AppUtil.queryLikeValueFormat(userName), false);
        qs.appendWhere(sc, index0);

        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        while (qr.hasMoreElements()) {
            WTUser user = (WTUser) qr.nextElement();
            if (userName.equalsIgnoreCase(user.getName()))
                return user;
        }

        return null;
    }

    public boolean removeUsers(WTUser ss) throws WTException {
        boolean status = false;
        WTUser deletedUser = OrganizationServicesHelper.manager.delete(ss);
        if (deletedUser != null) {
            status = true;
        }
        return status;
    }

    public void removeUsers(WTCollection users) throws WTException {
        OrganizationServicesHelper.manager.deleteUsers(users);
    }

    public Map<String, List<String>> groupMembers(Collection<WTPrincipal> users) throws WTException {

        Map<String, List<String>> userGroups = new HashMap<>();
        List<String> groupList;
        List<?> parentGroupsList = new ArrayList<>();
        WTGroup wtGroup;
        List<String> defaultGroups = Arrays.asList("Retail", "PTC FlexPLM External License");
        for (WTPrincipal user : users) {
            logger.log(Level.INFO, "Fetching Users Groups for :{}", user.getName());
            Enumeration<?> parentGroups = OrganizationServicesHelper.manager.parentGroups(user, false);
            if (Objects.nonNull(parentGroups)) {
                parentGroupsList = Collections.list(parentGroups);
            }
            logger.log(Level.INFO, "Fetching Users Groups size :{}", parentGroupsList.size());
            groupList = new ArrayList<>();
            for (Object obj : parentGroupsList) {
                logger.log(Level.INFO, "user {} , inside while look grp  {}", user.getName(), obj);
                WTPrincipalReference wtPrincipalReference = (WTPrincipalReference) obj;
                WTPrincipal localPrince = wtPrincipalReference.getPrincipal();
                if (localPrince instanceof WTGroup && !(localPrince instanceof WTOrganization)) {
                    wtGroup = (WTGroup) localPrince;
                    if (!defaultGroups.contains(wtGroup.getName()) && "Lowes".equals(wtGroup.getDomainRef().getName())) {
                        groupList.add(wtGroup.getName());
                    }
                }
            }
            if (!groupList.isEmpty()) {
                userGroups.put(user.getName(), groupList);
            }
        }

        return userGroups;
    }

    public WTUser addUser(JSONObject user) throws WTException, WTPropertyVetoException {

        String userName = (String) user.get("userName");
        String firstName = (String) user.get("firstName");
        String lastName = (String) user.get("lastName");
        String email = (String) user.get(EMAIL);

        WTUser wtUser = WTUser.newWTUser(userName);
        wtUser.setAllowLDAPSynchronization(true);
        wtUser.setOrganizationName("lowes");
        wtUser.setAuthenticationName(userName);


        HashMap<String, Object> map = new HashMap<>();
        map.put("preferredLanguage", new String[]{"en_US"});
        map.put("fullName", new String[]{firstName + " " + lastName});
        map.put("last", new String[]{lastName});
        map.put(EMAIL, new String[]{email});
        map.put("organizationName", new String[]{"lowes"});
        map.put("userPassword", new String[]{"plm"});
        wtUser.mapAttributes(map);

        return (WTUser) OrganizationServicesHelper.manager.createPrincipal(wtUser);

    }

    public WTUser updateUser(JSONObject user, WTUser userObj) throws WTException, WTPropertyVetoException {
        WTUser wtUser = userObj;
        String userName = (String) user.get("userName");
        String firstName = (String) user.get("firstName");
        String lastName = (String) user.get("lastName");
        String email = (String) user.get(EMAIL);
        String fullName = firstName + " " + lastName;

        String userNameFrmObj = userObj.getAuthenticationName();
        String fullNameFrmObj = userObj.getFullName();
        String lastNameFrmObj = userObj.getLast();
        String emailFrmObj = userObj.getEMail();

        if (userNameFrmObj.equals(userName) && (!fullName.equals(fullNameFrmObj) || !lastName.equals(lastNameFrmObj) || !email.equals(emailFrmObj))) {
            userObj.setFullName(fullName);
            userObj.setLast(lastName);
            userObj.setEMail(email);
            wtUser = (WTUser) OrganizationServicesHelper.manager.updatePrincipal(userObj);
        }
        return wtUser;
    }

}
