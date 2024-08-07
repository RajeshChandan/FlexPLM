package com.sportmaster.wc.org;

import org.apache.log4j.Logger;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.OrgContainer;
import wt.org.*;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@SuppressWarnings("unused")
public class SMOrgTools {

    private static final Logger logger =Logger.getLogger(SMOrgTools.class);

    @SuppressWarnings({"deprecation", "DuplicatedCode"})
    private static DirectoryContextProvider getOrgContextProvider() throws WTException {

        QuerySpec qs = new QuerySpec(OrgContainer.class);
        SearchCondition sc = new SearchCondition(
                OrgContainer.class,
                "containerInfo.name",
                SearchCondition.EQUAL,
                "SportMaster"
        );
        qs.appendWhere(sc);
        QueryResult qr = PersistenceHelper.manager.find(qs);

        OrgContainer org = null;
        Object element;
        if (qr.hasMoreElements()) {
            element = qr.nextElement();
            if (element instanceof Persistable[]) {
                logger.debug("Obj is an Persistable[] instance");
                Persistable[] p = (Persistable[]) element;
                org = (OrgContainer) p[0];
            } else {
                logger.trace("Obj is an OrgContainer instance");
                org = (OrgContainer) element;
            }
        }

        if (org != null) return org.getContextProvider();

        logger.error("!!! Failed to get OrgContainer !!!");
        return null;
    }

    public static WTGroup getGroupByName(String groupName) throws WTException {
        return OrganizationServicesHelper.manager.getGroup(groupName, getOrgContextProvider());
    }

    public static Set<WTPrincipal> getGroupMembers(WTGroup group) throws WTException {

        if (group == null) return null;

        Enumeration<?> members = group.members();
        Set<WTPrincipal> result = new HashSet<>();
        for(Object obj: Collections.list(members)){
            if(obj instanceof WTPrincipal){
                result.add((WTPrincipal) obj);
            } else {
                logger.error("!!! Obj is not a WTPrincipal, but it's a " + obj.getClass());
            }
        }

        return result;
    }

    public static Set<WTUser> getGroupUsers(WTGroup group) throws WTException {

        return getGroupMembers(group).stream()
                .filter(WTUser.class::isInstance)
                .map(WTUser.class::cast)
                .collect(toSet());
    }

    /*
     * Experimental method.
     */
    public static Stream<WTPrincipal> groupMembers(WTGroup group) throws WTException {

        if (group == null) return null;

        //new SMOrgLogger(logger).logPrincipal(group);

        Enumeration<?> members = group.members();
        // ".filter(WTPrincipal.class::isInstance)" is just for any case
        return Collections.list(members).stream().filter(WTPrincipal.class::isInstance).map(WTPrincipal.class::cast);
    }
}
