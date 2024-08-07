package com.lowes.web.util;

import com.lowes.model.group.GroupModel;
import wt.org.WTGroup;
import wt.util.WTException;

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
    private static WTGroup vendors;

    public static WTGroup getLicensors() throws WTException {
        if (Objects.isNull(licensors)) {
            setLicensors();
        }
        return licensors;
    }

    private static void setLicensors() throws WTException {
        VendorContactsUtil.licensors = new GroupModel().findGroup("PTC FlexPLM External License");
    }

    public static WTGroup getVendors() throws WTException {
        if (Objects.isNull(vendors)) {
            setVENDORS();
        }
        return vendors;
    }

    private static void setVENDORS() throws WTException {
        VendorContactsUtil.vendors = new GroupModel().findGroup("VENDORS");
    }

}
