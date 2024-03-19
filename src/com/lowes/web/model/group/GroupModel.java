package com.lowes.web.model.group;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.org.*;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/****
 * Standard Group model class has below functions
 *  - fetch groups members
 *  - find group bt name
 *  - Create a Group
 *
 * @author Rajesh Chandan Sahu (rajeshchandan.sahu@lowes.com)
 */
public class GroupModel {

    private static OrgContainer lowesOrg;

    /**
     * fetches members ofa group.
     *
     * @param group -  wtGroup Object
     * @return map
     * @throws WTException exception
     */
    public Map<String, WTPrincipal> getGroupMembers(WTGroup group) throws WTException {

        Map<String, WTPrincipal> members = new HashMap<>();
        Enumeration<?> member = group.members();

        while (member.hasMoreElements()) {

            WTPrincipal principal = (WTPrincipal) member.nextElement();
            if (principal instanceof WTUser) {
                WTUser user = (WTUser) principal;
                members.put(user.getName(), principal);
            }

        }
        return members;

    }

    public WTGroup findGroup(String groupName) throws WTException {

        WTGroup group = null;
        QuerySpec qs = new QuerySpec(WTGroup.class);
        int[] index0 = new int[]{0};
        SearchCondition sc = new SearchCondition(WTGroup.class, _WTPrincipal.NAME, SearchCondition.EQUAL, groupName, false);
        qs.appendWhere(sc, index0);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        if (qr.hasMoreElements()) {
            WTGroup wtGroup = (WTGroup) qr.nextElement();
            if (!(wtGroup instanceof WTOrganization))
                group = wtGroup;
        }
        return group;
    }

    public WTGroup createGroup(String groupName) throws WTException, WTPropertyVetoException {

        WTGroup group = null;

        OrgContainer org = getLowesOrg();
        if (Objects.nonNull(org)) {

            wt.inf.container.PrincipalSpec spec = new wt.inf.container.PrincipalSpec();
            spec.setContainerReference(WTContainerRef.newWTContainerRef(org));
            DirectoryContextProvider[] contexts = WTContainerHelper.service.getPublicContextProviders(spec);
            for (DirectoryContextProvider dcp : contexts) {
                dcp.setInternalGroupsSearchCriteria(null);
            }

            DirectoryContextProvider dcp = contexts[0];
            group = WTGroup.newWTGroup(groupName, dcp);
            group.setContainer(org);
            group.setDomainRef(org.getDomainRef());
            group = (WTGroup) OrganizationServicesHelper.manager.createPrincipal(group);
        }

        return group;

    }

    public static OrgContainer getLowesOrg() throws WTException {
        if (Objects.isNull(lowesOrg)) {
            setLowesOrg();
        }
        return lowesOrg;
    }

    private static void setLowesOrg() throws WTException {
        String targetOrgName = "lowes";
        Enumeration<?> organisations = OrganizationServicesHelper.manager.findLikeOrganizations(_WTPrincipal.NAME, targetOrgName, WTContainerHelper.getExchangeRef().getReferencedContainer().getContextProvider());
        WTOrganization targetOrg;
        if (organisations.hasMoreElements()) {
            targetOrg = (WTOrganization) organisations.nextElement();
            lowesOrg = WTContainerHelper.service.getOrgContainer(targetOrg);
        }
    }
}
