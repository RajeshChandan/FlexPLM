package com.lowes.web.util;

import com.lowes.web.model.group.GroupModel;
import wt.inf.container.OrgContainer;
import wt.inf.container.WTContainerHelper;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org._WTPrincipal;
import wt.util.WTException;

import java.util.Enumeration;
import java.util.Objects;

/****
 * Helper class of Vendor Contacts web Service.
 *
 * @author Rajesh Chandan Sahu (rajeshchandan.sahu@lowes.com)
 */
public class VendorContactsUtil {

    private VendorContactsUtil() {
    }

    private static WTGroup licensors;
    private static WTGroup VENDORS;
    private static OrgContainer lowesOrg;

    public static WTGroup getLicensors() throws WTException {
        if (Objects.isNull(licensors)) {
            setLicensors();
        }
        return licensors;
    }

    private static void setLicensors() throws WTException {
        VendorContactsUtil.licensors = new GroupModel().findGroup("PTC FlexPLM External License");
    }

    public static WTGroup getVENDORS() throws WTException {
        if (Objects.isNull(VENDORS)) {
            setVENDORS();
        }
        return VENDORS;
    }

    private static void setVENDORS() throws WTException {
        VendorContactsUtil.VENDORS = new GroupModel().findGroup("VENDORS");
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
            VendorContactsUtil.lowesOrg = WTContainerHelper.service.getOrgContainer(targetOrg);
        }
    }

}
