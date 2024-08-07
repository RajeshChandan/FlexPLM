package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.sportmaster.wc.mc.config.SMCostSheetConfig;
import com.sportmaster.wc.mc.config.SMCostSheetMOATableConfig;
import com.sportmaster.wc.mc.object.SMProductCostSheet;
import org.apache.log4j.Logger;
import wt.util.WTException;

import java.util.Date;

public class SMCostSheetTypeSelector {

    private LCSProductCostSheet lcsProductCostSheet = null;
    private FlexType costSheetBOMTableType = null;
    private String costSheetBOMTableName = null;
    private String costSheetBOMTableAttsNotUpdate = null;
    private static final Logger LOGGER = Logger.getLogger(SMCostSheetPlugin.class);

    public SMCostSheetTypeSelector(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        this.lcsProductCostSheet = lcsProductCostSheet;
        this.costSheetBOMTableType = getSMCostSheetBOMTableType(lcsProductCostSheet);
        this.costSheetBOMTableName = getSMCostSheetBOMTableName(lcsProductCostSheet);
        this.costSheetBOMTableAttsNotUpdate = getSMCostSheetBOMTableAttsNotUpdate(lcsProductCostSheet);
    }

    public String getSMCostSheetBOMTableName() {
        return costSheetBOMTableName;
    }

    public FlexType getSMCostSheetBOMTableType() {
        return costSheetBOMTableType;
    }

    public String getSMCostSheetBOMTableAttsNotUpdate() {
        return costSheetBOMTableAttsNotUpdate;
    }

    public static boolean isMulticurrency(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        if (SMCostSheetTypeSelector.isAPD(lcsProductCostSheet))
            return true;
        else if (SMCostSheetTypeSelector.isFPD(lcsProductCostSheet))
            return true;
        else if (SMCostSheetTypeSelector.isSEPD(lcsProductCostSheet))
            return true;
        return false;
    }

    public static boolean isAPD(LCSProductCostSheet lcsProductCostSheet) throws WTException {

        FlexType currentType = FlexTypeCache.getFlexType(lcsProductCostSheet);
        FlexType type = FlexTypeCache.getFlexTypeFromPath(SMCostSheetConfig.SM_COST_SHEET_MC_TYPE_APD);
        if (type.equals(currentType))
            return true;
        return false;
    }

    public static boolean isFPD(LCSProductCostSheet lcsProductCostSheet) throws WTException {

        FlexType currentType = FlexTypeCache.getFlexType(lcsProductCostSheet);
        FlexType type = FlexTypeCache.getFlexTypeFromPath(SMCostSheetConfig.SM_COST_SHEET_MC_TYPE_FPD);
        if (type.equals(currentType))
            return true;
        return false;
    }

    public static boolean isSEPD(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        FlexType currentType = FlexTypeCache.getFlexType(lcsProductCostSheet);
        FlexType type1 = FlexTypeCache.getFlexTypeFromPath(SMCostSheetConfig.SM_COST_SHEET_MC_TYPE_SEPD);
        FlexType type2 = FlexTypeCache.getFlexTypeFromPath(SMCostSheetConfig.SM_COST_SHEET_MC_TYPE_SEPD_ACCESSORIES);

        if (type1.equals(currentType) || type2.equals(currentType))
            return true;
        return false;
    }

    public static boolean isSEPD_only(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        FlexType currentType = FlexTypeCache.getFlexType(lcsProductCostSheet);
        FlexType type = FlexTypeCache.getFlexTypeFromPath(SMCostSheetConfig.SM_COST_SHEET_MC_TYPE_SEPD);
        if (type.equals(currentType))
            return true;
        return false;
    }

    public static boolean isSEPD_Accessories(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        FlexType currentType = FlexTypeCache.getFlexType(lcsProductCostSheet);
        FlexType type = FlexTypeCache.getFlexTypeFromPath(SMCostSheetConfig.SM_COST_SHEET_MC_TYPE_SEPD_ACCESSORIES);
        if (type.equals(currentType))
            return true;
        return false;
    }

    public static LCSMOATable getSMCostSheetBOMTable(SMProductCostSheet productCostSheet) throws WTException {
        String costSheetBOMTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName( productCostSheet );
        return  (LCSMOATable) productCostSheet.getValue( costSheetBOMTableName );
    }

    public static LCSMOATable getSMCostSheetBOMTable(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        String costSheetBOMTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName( lcsProductCostSheet );
        return  (LCSMOATable) lcsProductCostSheet.getValue( costSheetBOMTableName );
    }

    public static String getSMCostSheetBOMTableName(SMProductCostSheet productCostSheet) throws WTException {
        if(productCostSheet.isAPD())
            return SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ATT_APD;
        if(productCostSheet.isFPD())
            return SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ATT_FPD;
        if(productCostSheet.isSEPD())
            return SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ATT_SEPD;
        throw new WTException("Type '" + productCostSheet.getFlexType().getTypeName() + "' is NOT supported.");
    }

    public static String getSMCostSheetBOMTableName(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        if(isAPD(lcsProductCostSheet))
            return SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ATT_APD;
        if(isFPD(lcsProductCostSheet))
            return SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ATT_FPD;
        if(isSEPD(lcsProductCostSheet))
            return SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ATT_SEPD;
        throw new WTException("Type '" + FlexTypeCache.getFlexType(lcsProductCostSheet).getTypeName() + "' is NOT supported.");
    }

    public static FlexType getSMCostSheetBOMTableType(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        if(isAPD(lcsProductCostSheet))
            return FlexTypeCache.getFlexTypeFromPath(SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_TYPE_PATH_APD);
        if(isFPD(lcsProductCostSheet))
            return FlexTypeCache.getFlexTypeFromPath(SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_TYPE_PATH_FPD);
        if(isSEPD_only(lcsProductCostSheet))
            return FlexTypeCache.getFlexTypeFromPath(SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_TYPE_PATH_SEPD);
        if(isSEPD_Accessories(lcsProductCostSheet))
            return FlexTypeCache.getFlexTypeFromPath(SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_TYPE_PATH_SEPD_ACCESSORIES);
        throw new WTException("Type '" + FlexTypeCache.getFlexType(lcsProductCostSheet).getTypeName() + "' is NOT supported.");
    }

    public static LCSMOATable getSMCostSheetBOMSectionsTotalTable(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        String costSheetBOMSectionsTotalTableName = SMCostSheetTypeSelector.getSMCostSheetBOMSectionsTotalTableName( lcsProductCostSheet );
        return  (LCSMOATable) lcsProductCostSheet.getValue( costSheetBOMSectionsTotalTableName );
    }

    public static String getSMCostSheetBOMSectionsTotalTableName(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        if(isSEPD(lcsProductCostSheet))
            return SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_SECTIONS_TOTALS_TABLE_ATT_SEPD;
        throw new WTException("Type '" + FlexTypeCache.getFlexType(lcsProductCostSheet).getTypeName() + "' is NOT supported.");
    }

    public static FlexType getSMCostSheetBOMSectionsTotalTableType(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        if(isSEPD_only(lcsProductCostSheet))
            return FlexTypeCache.getFlexTypeFromPath(SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_SECTIONS_TOTALS_TABLE_TYPE_PATH_SEPD);
        if(isSEPD_Accessories(lcsProductCostSheet))
            return FlexTypeCache.getFlexTypeFromPath(SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_SECTIONS_TOTALS_TABLE_TYPE_PATH_SEPD_ACCESSORIES);
        throw new WTException("Type '" + FlexTypeCache.getFlexType(lcsProductCostSheet).getTypeName() + "' is NOT supported.");
    }

    public static LCSMOATable getSMCostSheetBOMAdditionalTable(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        String costSheetBOMAdditionalTableName = getSMCostSheetBOMAdditionalTableName( lcsProductCostSheet );
        return (LCSMOATable) lcsProductCostSheet.getValue( costSheetBOMAdditionalTableName );
    }

    public static String getSMCostSheetBOMAdditionalTableName(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        if(isSEPD(lcsProductCostSheet))
            return SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_ADDITIONAL_TABLE_ATT_SEPD;
        throw new WTException("Type '" + FlexTypeCache.getFlexType(lcsProductCostSheet).getTypeName() + "' is NOT supported.");
    }

    public static String getSMCostSheetBOMTableAttsNotUpdate(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        if(isAPD(lcsProductCostSheet))
            return SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ATTRIBUTES_NOT_UPDATE_APD;
        if(isFPD(lcsProductCostSheet))
            return SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ATTRIBUTES_NOT_UPDATE_FPD;
        if(isSEPD(lcsProductCostSheet))
            return SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ATTRIBUTES_NOT_UPDATE_SEPD;
        throw new WTException("Type '" + FlexTypeCache.getFlexType(lcsProductCostSheet).getTypeName() + "' is NOT supported.");
    }

    public static SMCostSheetBOMTableRow getBOMTableRowForType(LCSProductCostSheet lcsProductCostSheet, Date dateForMaterialPrice) throws WTException {
        if (SMCostSheetTypeSelector.isAPD(lcsProductCostSheet))
            return SMCostSheetBOMTableRowAPD.newSMCostSheetBOMTableRowAPD(lcsProductCostSheet, dateForMaterialPrice);
        if (SMCostSheetTypeSelector.isFPD(lcsProductCostSheet))
            return SMCostSheetBOMTableRowFPD.newSMCostSheetBOMTableRowFPD(lcsProductCostSheet, dateForMaterialPrice);
        if (SMCostSheetTypeSelector.isSEPD(lcsProductCostSheet))
            return SMCostSheetBOMTableRowSEPD.newSMCostSheetBOMTableRowSEPD(lcsProductCostSheet, dateForMaterialPrice);
        throw new WTException("Type '" + FlexTypeCache.getFlexType(lcsProductCostSheet).getTypeName() + "' is NOT supported.");
    }

    public static String getSMCostingStage(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        if (SMCostSheetTypeSelector.isAPD(lcsProductCostSheet))
            return SMCostSheetConfig.SM_COSTING_STAGE_ATTRIBUTE_APD; //TODO: уточнить имя атрибута
        if (SMCostSheetTypeSelector.isFPD(lcsProductCostSheet))
            return SMCostSheetConfig.SM_COSTING_STAGE_ATTRIBUTE_FPD;
        if (SMCostSheetTypeSelector.isSEPD(lcsProductCostSheet))
            return SMCostSheetConfig.SM_COSTING_STAGE_ATTRIBUTE_SEPD;
        throw new WTException("Type '" + FlexTypeCache.getFlexType(lcsProductCostSheet).getTypeName() + "' is NOT supported.");
    }

    public static String getSMCostSheetStatus(LCSProductCostSheet lcsProductCostSheet) throws WTException {
        if (SMCostSheetTypeSelector.isAPD(lcsProductCostSheet))
            return SMCostSheetConfig.SM_COST_SHEET_STATUS_APD;
        if (SMCostSheetTypeSelector.isFPD(lcsProductCostSheet))
            return SMCostSheetConfig.SM_COST_SHEET_STATUS_FPD;
        if (SMCostSheetTypeSelector.isSEPD_only(lcsProductCostSheet))
            return SMCostSheetConfig.SM_COST_SHEET_STATUS_SEPD;
        if (SMCostSheetTypeSelector.isSEPD_Accessories(lcsProductCostSheet))
            return SMCostSheetConfig.SM_COST_SHEET_STATUS_SEPD_ACCESSORIES;
        throw new WTException("Type '" + FlexTypeCache.getFlexType(lcsProductCostSheet).getTypeName() + "' is NOT supported.");
    }

    public static Date getDateForPrice(LCSProductCostSheet lcsProductCostSheet) throws WTException {

        Date date = null;

        if (isFPD(lcsProductCostSheet)) {
            date = (Date) lcsProductCostSheet.getValue(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.COST_SHEET_ATT_DATE_OF_BOM_UPDATE);
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTypeSelector.getDateForPrice: Date for Material Price (" +
                    com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.COST_SHEET_ATT_DATE_OF_BOM_UPDATE +
                    "): " + date);
        }
        else if (isAPD(lcsProductCostSheet) || isSEPD(lcsProductCostSheet)) {
            date = (Date) lcsProductCostSheet.getValue(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.VRD_EFFECTIVE_DATE_ATT);
            if (date == null) {
                date = (Date) lcsProductCostSheet.getValue(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.COST_SHEET_ATT_DATE_OF_BOM_UPDATE);
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTypeSelector.getDateForPrice: Date for Material Price (" +
                        com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.COST_SHEET_ATT_DATE_OF_BOM_UPDATE +
                        "): " + date);
            } else {
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTypeSelector.getDateForPrice: Date for Material Price (" +
                        com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.VRD_EFFECTIVE_DATE_ATT +
                        "): " + date);
            }
        }

        if (date == null) {
            date = new Date();
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTypeSelector.getDateForPrice: Date for Material Price (current date): " + date);
        }

        return date;
    }
}
