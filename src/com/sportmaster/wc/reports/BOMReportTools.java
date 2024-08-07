package com.sportmaster.wc.reports;


import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import wt.util.WTException;

import java.util.Iterator;

public class BOMReportTools {

    public static String getBOMMaterialName(FlexBOMLink bomLink) throws WTException {

        String initMatDesc = (String) bomLink.getValue("materialDescription");
        if (initMatDesc != null && !initMatDesc.trim().isEmpty()) {
            return initMatDesc;
        }

        FlexBOMLink aLink = LCSFlexBOMQuery.findTopLevelBranch(bomLink);
        if (aLink != null) {
            String matDesc = (String) aLink.getValue("materialDescription");
            if (matDesc != null && !matDesc.trim().isEmpty()) {
                return matDesc;
            }
        }

        LCSMaterial material = getMaterialForLink(bomLink); // Get Material object for BOMLink
        if (material != null) {
            return material.getName();
        }

        return "";
    }

    public static String getBOMMaterialNumber(FlexBOMLink bomLink) throws WTException {

        String materialNumberForLink = getLCSMaterialNumber(bomLink);
        if (materialNumberForLink != null) {
            return materialNumberForLink;
        }

        FlexBOMLink topLink = LCSFlexBOMQuery.findTopLevelBranch(bomLink); // Get top link of the branch
        if (topLink != null) {
            String topName = (String) topLink.getValue("materialDescription");
            String childName = getBOMMaterialName(bomLink);

            if (topName != null && childName != null && topName.trim().equals(childName.trim())) {
                String topMaterialNumberForLink = getLCSMaterialNumber(topLink);
                if (topMaterialNumberForLink != null) {
                    return topMaterialNumberForLink;
                }
            }
        }

        return "";
    }

    private static String getLCSMaterialNumber(FlexBOMLink bomLink) throws WTException {

        LCSMaterial material = getMaterialForLink(bomLink); // Get Material object for BOMLink
        if (material != null) {
            Long matNumberObj = (Long) material.getValue("vrdMaterialNum"); // Get att Material# value from LCSMaterial
            if (matNumberObj != null) {
                return matNumberObj.toString();
            }
        }

        return null;
    }

    private static LCSMaterial getMaterialForLink(FlexBOMLink bomLink) throws WTException {

        String aLinkId = "" + bomLink.getPersistInfo().getObjectIdentifier().getId();

        PreparedQueryStatement statement = new PreparedQueryStatement();
        statement.appendFromTable("FLEXBOMLINK");
        statement.appendSelectColumn("FLEXBOMLINK", "IDA3B5");
        statement.appendCriteria(new Criteria("FLEXBOMLINK", "IDA2A2", "?", Criteria.EQUALS), aLinkId);

        Iterator mat = (LCSQuery.runDirectQuery(statement).getResults()).iterator();
        FlexObject matObj = (FlexObject) mat.next();
        String matId = FormatHelper.format(matObj.getData("FLEXBOMLINK.IDA3B5"));

        LCSMaterialMaster materialMaster = (LCSMaterialMaster) LCSQuery.findObjectById("com.lcs.wc.material.LCSMaterialMaster" + ":" + matId);
        return VersionHelper.latestIterationOf(materialMaster);
    }
}
