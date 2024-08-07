package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.sportmaster.wc.mc.object.SMProductCostSheet;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SMCostSheetHideValue {

    public static void hideOrShowBOMTableAttrValues(SMProductCostSheet productCostSheet, boolean show) throws WTException, WTPropertyVetoException {

        Map<String,String> attrs = new HashMap<>();
        attrs.put("smNomPriceHidden", "smMSPrice");
        attrs.put("smNomPriceCurrHidden", "smMSCurrency");
        attrs.put("smNomPriceCCHidden", "smPriceCC");

        String bomTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(productCostSheet);
        hideOrShowMOATableAttrValues(productCostSheet.getLCSProductCostSheet(), bomTableName, attrs, show);
    }

    public static void hideOrShowMOATableAttrValues(LCSProductCostSheet lcsCostSheet, String moaTableName, Map<String,String> attrs, boolean show) throws WTException, WTPropertyVetoException {

        Collection rowsMOATable = LCSMOAObjectQuery.findMOACollection(lcsCostSheet,
                lcsCostSheet.getFlexType().getAttribute(moaTableName));

        if( show ) copyMOATableAttrValues(rowsMOATable, attrs);
        else clearMOATableAttrValues(rowsMOATable, attrs);
    }

    public static void clearMOATableAttrValues(Collection rowsMOATable, Map<String,String> attrs) throws WTException {

        for(Object obj : rowsMOATable) {
            LCSMOAObject lcsmoaObject = (LCSMOAObject) obj;
            for (Map.Entry<String,String> entry : attrs.entrySet()) {
                String attrNameTarget = entry.getValue();
                lcsmoaObject.setValue(attrNameTarget, null);
            }
            LCSMOAObjectLogic.deriveFlexTypeValues(lcsmoaObject, false);
            LCSMOAObjectLogic.persist(lcsmoaObject, true);
        }
    }

    public static void copyMOATableAttrValues(Collection rowsMOATable, Map<String,String> attrs) throws WTException, WTPropertyVetoException {

        for(Object obj : rowsMOATable) {
            LCSMOAObject lcsmoaObject = (LCSMOAObject) obj;
            for (Map.Entry<String,String> entry : attrs.entrySet()) {
                String attrNameSource = entry.getKey();
                Object attrValueSource = lcsmoaObject.getValue(attrNameSource);
                String attrNameTarget = entry.getValue();
                lcsmoaObject.setValue(attrNameTarget, attrValueSource);
            }
            LCSMOAObjectLogic.deriveFlexTypeValues(lcsmoaObject, false);
            LCSMOAObjectLogic.persist(lcsmoaObject, true);
        }
    }
}
