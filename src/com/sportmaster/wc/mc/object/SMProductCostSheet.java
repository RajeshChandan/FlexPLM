package com.sportmaster.wc.mc.object;

import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.sportmaster.wc.mc.sourcing.SMCostSheetTypeSelector;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class SMProductCostSheet {

    private LCSProductCostSheet productCostSheet = null;
    private LCSProductCostSheet sourceCostSheet = null;
    private FlexType flexType = null;
    private boolean isAPD = false;
    private boolean isFPD = false;
    private boolean isSEPD = false;
    private Boolean isFirstIteration = null;

    private SMProductCostSheet() {}

    public static SMProductCostSheet newSMProductCostSheet(LCSProductCostSheet productCostSheet) throws WTException {
        SMProductCostSheet result = new SMProductCostSheet();
        result.productCostSheet = productCostSheet;
        result.sourceCostSheet = getSrcCostSheet(productCostSheet);
        result.flexType = FlexTypeCache.getFlexType(productCostSheet);
        result.isFirstIteration = isFirstIteration(productCostSheet);
        if (SMCostSheetTypeSelector.isAPD(productCostSheet))
            result.isAPD = true;
        else if (SMCostSheetTypeSelector.isFPD(productCostSheet))
            result.isFPD = true;
        else if (SMCostSheetTypeSelector.isSEPD(productCostSheet))
            result.isSEPD = true;
        return result;
    }

    public LCSProductCostSheet getLCSProductCostSheet() { return productCostSheet; }
    public LCSProductCostSheet getSourceCostSheet() { return sourceCostSheet; }
    public FlexType getFlexType() { return flexType; }
    public Object getValue(String key) throws WTException { return productCostSheet.getValue(key); }

    public boolean isAPD() { return isAPD; }
    public boolean isFPD() { return isFPD; }
    public boolean isSEPD() { return isSEPD; }
    public boolean isMulticurrency() { return isAPD || isFPD || isSEPD; }
    public boolean isFirstIteration() { return isFirstIteration; }
    public boolean isCopied() { return (sourceCostSheet != null); }

    public void setValue(String key, Object value) throws WTException, WTPropertyVetoException { productCostSheet.setValue(key, value); }
    public void setValue(String key, String value) { productCostSheet.setValue(key, value); }

    private static LCSProductCostSheet getSrcCostSheet(LCSProductCostSheet productCostSheet) {
        LCSProductCostSheet srcCS = null;
        if (productCostSheet.getCopiedFrom() != null)
            srcCS = (LCSProductCostSheet) productCostSheet.getCopiedFrom();
        if (productCostSheet.getCarriedOverFrom() != null)
            srcCS = (LCSProductCostSheet) productCostSheet.getCarriedOverFrom();
        return srcCS;
    }

    public static boolean isCopied(LCSProductCostSheet productCostSheet) {
        return getSrcCostSheet(productCostSheet) != null;
    }

    public static boolean isFirstIteration(LCSProductCostSheet productCostSheet) {
        return "A.1".equals(productCostSheet.getIterationDisplayIdentifier().toString());
    }
}
