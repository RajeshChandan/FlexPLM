package com.sportmaster.wc.org;

import org.apache.log4j.Logger;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.inf.container.WTContainerService;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.util.WTException;
import wt.workflow.engine.WfActivity;
import wt.workflow.work.WorkItem;

import java.util.*;

import static com.sportmaster.wc.org.SMOrgTools.getGroupByName;
import static com.sportmaster.wc.org.SMRetailGroup.*;
import static java.util.Optional.*;

public class SMAccessControlService {

    private final Logger logger;

    public SMAccessControlService() {
        logger = Logger.getLogger(SMAccessControlService.class);
    }

    public SMAccessControlService(Logger logger) {
        this.logger = logger;
    }

    public boolean hasReassignAccess(WTPrincipalReference principalRef, WTPrincipalReference ownerRef) throws WTException {

        return hasReassignAccess(principalRef, ownerRef, getReassignAccessGroups());
    }

    boolean hasReassignAccess(WTPrincipalReference principalRef, WTPrincipalReference ownerRef, Map<WTGroup[], WTGroup> accessGroups) throws WTException {

        for (Map.Entry<WTGroup[], WTGroup> entry : accessGroups.entrySet()) {
            if (hasReassignAccess(principalRef, ownerRef, entry.getKey(), entry.getValue()))
                return true;
        }
        return false;
    }

    boolean hasReassignAccess(
            WTPrincipalReference principalRef, WTPrincipalReference ownerRef,
            WTGroup[] accessGroups, WTGroup resourceGroup
    ) throws WTException {

        boolean ownerIsMember = false;
        if (resourceGroup == null
                && accessGroups.length == 1
                && accessGroups[0].getName().equals(SM_CORE_TEAM.value)) {
            ownerIsMember = true;
        } else if(resourceGroup != null){
            ownerIsMember = resourceGroup.isMember(ownerRef.getPrincipal());
        }

        if (!ownerIsMember) return false;

        for (WTGroup accessGroup : accessGroups) {

            if (accessGroup.isMember(principalRef.getPrincipal())) {
                return true;
            }
        }
        return false;
    }

    private Map<SMRetailGroup[], SMRetailGroup> getReassignAccessGroupTypes() {

        Map<SMRetailGroup[], SMRetailGroup> map = new HashMap<>();
        map.put(new SMRetailGroup[]{FTW_GATE_KEEPER}, FPD);
        map.put(new SMRetailGroup[]{SEPD_GATE_KEEPER, SEPD_HEAD_PM, SEPD_HEAD}, SEPD);
        map.put(new SMRetailGroup[]{SM_CORE_TEAM}, null);
        return map;
    }

    private Map<WTGroup[], WTGroup> getReassignAccessGroups() throws WTException {

        Map<WTGroup[], WTGroup> result = new HashMap<>();

        for (Map.Entry<SMRetailGroup[], SMRetailGroup> accessGroupEntry :
                getReassignAccessGroupTypes().entrySet()) {
            List<WTGroup> accessGroups = new ArrayList<>();
            for (SMRetailGroup gr : accessGroupEntry.getKey()) {
                accessGroups.add(getGroupByName(gr.value));
            }
            int size = accessGroups.size();
            WTGroup resourceGroup = null;
            if (accessGroupEntry.getValue() != null)
                resourceGroup = getGroupByName(accessGroupEntry.getValue().value);
            result.put(accessGroups.toArray(new WTGroup[size]), resourceGroup);
        }
        return result;
    }

    public boolean isAdmin(WorkItem workItem, WTPrincipalReference userRef) throws WTException {

        return isAdmin(workItem, userRef.getPrincipal());
    }

    public boolean isAdmin(WorkItem workItem, WTPrincipal user) throws WTException {

        WfActivity wfActivity = (WfActivity) workItem.getSource().getObject();
        WTContainerRef containerRef = wfActivity.getContainerReference();

        return isAdmin(containerRef, user);
    }

    public boolean isAdmin(WTContainerRef containerRef, WTPrincipal user) throws WTException {

        return isAdmin(containerRef, user, WTContainerHelper.service);
    }

    public boolean isAdmin(WTContainerRef containerRef, WTPrincipal user, WTContainerService service) throws WTException {

        if (!(user instanceof WTUser)) {
            logger.error("!!! Expected user, but was: " + user.getClass().getSimpleName() + " !!!");
        }
        return service.isAdministrator(containerRef, user);
    }

}
