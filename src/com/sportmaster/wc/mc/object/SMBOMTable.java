package com.sportmaster.wc.mc.object;

import com.lcs.wc.flexbom.FlexBOMPart;

import java.util.*;

public class SMBOMTable {

    private Map bomTableMap = null;
    private Set<FlexBOMPart> flexBOMPartList = null;

    private SMBOMTable() {}

    public static SMBOMTable newSMBOMTable(Map bomTableMap, Set<FlexBOMPart> flexBOMPartList) {
        SMBOMTable result = new SMBOMTable();
        result.bomTableMap = (bomTableMap != null) ? bomTableMap : new HashMap();
        result.flexBOMPartList = (flexBOMPartList != null) ? flexBOMPartList : new HashSet<>();
        return result;
    }

    public Map getBOMTableMap() {
        return bomTableMap;
    }

    public FlexBOMPart getFlexBOMPart() {
        if (flexBOMPartList != null && flexBOMPartList.size() > 0)
            return flexBOMPartList.iterator().next();
        return null;
    }

    public long getFlexBOMPartBranchIdentifier() {
        if (flexBOMPartList != null && flexBOMPartList.size() > 0) {
            FlexBOMPart flexBOMPart = flexBOMPartList.iterator().next();
            if (flexBOMPart != null)
                return flexBOMPart.getBranchIdentifier();
        }
        return 0;
    }

    public long getFlexBOMPartMasterObjectIdentifier() {
        if (flexBOMPartList != null && flexBOMPartList.size() > 0) {
            FlexBOMPart flexBOMPart = flexBOMPartList.iterator().next();
            if (flexBOMPart != null)
                return flexBOMPart.getMaster().getPersistInfo().getObjectIdentifier().getId();
        }
        return 0;
    }
}
