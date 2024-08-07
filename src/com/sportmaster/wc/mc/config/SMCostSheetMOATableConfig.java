package com.sportmaster.wc.mc.config;

import com.lcs.wc.util.LCSProperties;

public class SMCostSheetMOATableConfig {

    //Current config property prefix
    protected static final String PREFIX = SMCostSheetConfig.PREFIX;

    /* Common properties */
    public static final String MOA_TABLE_ATTRIBUTE_ROW_ID = LCSProperties.get(PREFIX + ".BOMTable.attributeRowId",
            "smRowID");
    public static final String MOA_TABLE_HIDE_ATTRIBUTES_COPY = LCSProperties.get(PREFIX + ".BOMTable.hideAttributesCopy",
            "smMaterialID,smColorID,smSupplierID,smBOMLinkID");
    public static final String MOA_TABLE_ATTRIBUTE_ROW_ID_DELIMITER = LCSProperties.get(PREFIX + ".BOMTable.attributeRowIdDelimiter",
            "|~*~|");

    /* Additional for APD */
    public static final String MOA_TABLE_HIDE_ATTRIBUTES_COPY_ADDITIONAL_APD = LCSProperties.get(PREFIX + ".BOMTable.hideAttributesCopyAdditionalAPD",
            "smTotalFinal");

    /* Additional for SEPD */
    public static final String MOA_TABLE_HIDE_ATTRIBUTES_COPY_ADDITIONAL_SEPD = LCSProperties.get(PREFIX + ".BOMTable.hideAttributesCopyAdditionalSEPD",
            "smNomPriceHidden,smNomPriceCurrHidden,smNomPriceCCHidden");

    //MOATable
    public static final String MOA_TABLE_SM_MATERIAL_PRICE_LC = LCSProperties.get(PREFIX + ".MOATable.smMaterialPriceLC", "smMSPrice");//smMaterialPriceLC
    public static final String MOA_TABLE_SM_CURRENCY_UNIT = LCSProperties.get(PREFIX + ".MOATable.smCurrencyUnit", "smMSCurrency");//smCurrencyUnit

    /* APD */
    public static final String SM_COST_SHEET_BOM_TABLE_ATT_APD = LCSProperties.get(PREFIX + ".BOMTableAPD.Att",
            "smCostSheetBOMTableAPD");
    public static final String SM_COST_SHEET_BOM_TABLE_TYPE_PATH_APD = LCSProperties.get(PREFIX + ".BOMTableAPD.flexTypePath",
            "Multi-Object\\smCostSheetBOM\\smCostSheetBOMApparel");
    public static final String SM_COST_SHEET_BOM_TABLE_ATTRIBUTES_NOT_UPDATE_APD = LCSProperties.get(PREFIX + ".BOMTableAPD.attributesNotUpdate",
            "smConsumptionOVR,smLossOVR,smPriceOVR,smPriceFromSM,smCIF,smCIFFromSM,smCommentsSupplier,smCommentsPM,smIncludeInPurchasePrice");
    // *** NMC ROWS -->
    public static final String SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_APD = LCSProperties.get(PREFIX + ".BOMTableAPD.additionalRowsSectionKeys",
            "smCMTSupplier;smOverheadSupplier;smProfitSupplier;smMiscellaneousSupplier");
    public static final String SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_CS_INCOTERMS_DDP_APD = LCSProperties.get(PREFIX + ".BOMTableAPD.additionalRowsSectionKeys",
            "smTransportationSupplier");
    public static final String SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_ALL_KEYS_APD = SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_APD
            + ";" + SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_CS_INCOTERMS_DDP_APD;
    public static final String SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_ATTRS_IGNORE_USER_VALUES_APD = LCSProperties.get(PREFIX + ".BOMTableAPD.additionalRowsSectionAttrsIgnoreUserValues",
            "smConsumptionOVR:double,smLossOVR:double,smCIF:double,smCIFFromSM:double");
    // <-- NMC ROWS ***

    /* FPD */
    public static final String SM_COST_SHEET_BOM_TABLE_ATT_FPD = LCSProperties.get(PREFIX + ".BOMTableFPD.Att",
            "smCostSheetBOMTableFPD");
    public static final String SM_COST_SHEET_BOM_TABLE_TYPE_PATH_FPD = LCSProperties.get(PREFIX + ".BOMTableFPD.flexTypePath",
            "Multi-Object\\smCostSheetBOM\\smCostSheetBOMFootwear");
    public static final String SM_COST_SHEET_BOM_TABLE_ATTRIBUTES_NOT_UPDATE_FPD = LCSProperties.get(PREFIX + ".BOMTableFPD.attributesNotUpdate",
            "smPriceOVR,smCommentsSupplier");

    /* SEPD */
    public static final String SM_COST_SHEET_BOM_TABLE_ATT_SEPD = LCSProperties.get(PREFIX + ".BOMTableSEPD.Att",
            "smCostSheetBOMTableSEPD");
    public static final String SM_COST_SHEET_BOM_TABLE_TYPE_PATH_SEPD = LCSProperties.get(PREFIX + ".BOMTableSEPD.flexTypePath",
            "Multi-Object\\smCostSheetBOM\\smCostSheetBOMSportsEquipment");
    public static final String SM_COST_SHEET_BOM_TABLE_ATTRIBUTES_NOT_UPDATE_SEPD = LCSProperties.get(PREFIX + ".BOMTableSEPD.attributesNotUpdate",
            "smArticleUOMOVR,smConsumptionOVR,smPriceOVR,smCommentsSupplier,smIncludeInPurchasePrice");

    public static final String SM_COST_SHEET_BOM_SECTIONS_TOTALS_TABLE_ATT_SEPD = LCSProperties.get(PREFIX + ".BOMTableSectionsTotalsSEPD.Att",
            "smBOMSectionsTotals");
    public static final String SM_COST_SHEET_BOM_ADDITIONAL_TABLE_ATT_SEPD = LCSProperties.get(PREFIX + ".BOMTableAdditionalSEPD.Att",
            "smCostSheetBOMSportsEquipmentAdditional");
    public static final String SM_COST_SHEET_BOM_ADDITIONAL_TABLE_NMC_SECTIONS = LCSProperties.get(PREFIX + ".BOMTableAdditionalSEPD.NMC",
            "smLaborSupplier|smOverheadSupplier|smProfitSupplier");
    public static final String SM_COST_SHEET_BOM_SECTIONS_TOTALS_TABLE_TYPE_PATH_SEPD = LCSProperties.get(PREFIX + ".BOMTableSectionsTotalsSEPD.flexTypePath",
            "Multi-Object\\smBOMSectionsTotals");

    /* smLaborSupplier - LABOR, smOverheadSupplier - OVERHEAD, smProfitSupplier - PROFIT */
    public static final String SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_SEPD = LCSProperties.get(PREFIX + ".BOMTableSEPD.additionalRowsSectionKeys",
            "smLaborSupplier;smOverheadSupplier;smProfitSupplier");
    /* smTransportationSupplier - TRANSPORTATION, LOCAL LOGISTICS */
    public static final String SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_CS_INCOTERMS_DDP_SEPD = LCSProperties.get(PREFIX + ".BOMTableSEPD.additionalRowsSectionKeys",
            "smTransportationSupplier");

    /* SEPD / ACC */
    public static final String SM_COST_SHEET_BOM_TABLE_TYPE_PATH_SEPD_ACCESSORIES = LCSProperties.get(PREFIX + ".BOMTableSEPDAccessories.flexTypePath",
            "Multi-Object\\smCostSheetBOM\\smCostSheetBOMSportsEquipment\\smCostSheetBOMAccessories");
    public static final String SM_COST_SHEET_BOM_SECTIONS_TOTALS_TABLE_TYPE_PATH_SEPD_ACCESSORIES = LCSProperties.get(PREFIX + ".BOMTableSectionsTotalsSEPDAccessories.flexTypePath",
            "Multi-Object\\smBOMSectionsTotals\\smBOMSectionsTotals_Accessories");

}
