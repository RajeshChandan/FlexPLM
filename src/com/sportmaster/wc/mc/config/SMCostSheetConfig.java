package com.sportmaster.wc.mc.config;

import com.lcs.wc.util.LCSProperties;
import com.sportmaster.wc.mc.tools.SMFormatHelper;
import java.util.List;

public class SMCostSheetConfig {
    //Current config property prefix
    protected static final String PREFIX = "com.sportmaster.wc.mc.sourcing.SMCostSheetConfig";

    //Cost Sheet/Apparel/Multicurrency Apparel (smApparelMulticurrency$com_lcs_wc_sourcing_LCSProductCostSheet)
    public static final String SM_COST_SHEET_MC_TYPE_APD = LCSProperties.get(PREFIX + ".LCSProductCostSheet.typeAPD",
            "Cost Sheet\\Apparel\\smApparelMulticurrency");

    public static final String SM_COST_SHEET_MC_TYPE_FPD = LCSProperties.get(PREFIX + ".LCSProductCostSheet.typeFPD",
            "Cost Sheet\\Footwear\\smFootwearMulticurrency");

    public static final String SM_COST_SHEET_MC_TYPE_SEPD = LCSProperties.get(PREFIX + ".LCSProductCostSheet.typeSEPD",
            "Cost Sheet\\smSportsEquipment\\smSportsEquipmentMulticurrency"); // Cost Sheet/Sports Equipment/Multicurrency

    public static final String SM_COST_SHEET_MC_TYPE_SEPD_ACCESSORIES = LCSProperties.get(PREFIX + ".LCSProductCostSheet.typeSEPDApparelAccessories",
            "Cost Sheet\\Apparel\\smSportsEquipmentAccMulticurrency"); // Cost Sheet/Apparel/Multicurrency Accessories

    public static final String FLEX_BOM_PART_TYPE_BIKES_SEPD = LCSProperties.get(PREFIX + ".FlexBOMPart.typeBikesSEPD",
            "BOM\\Materials\\Product\\smSportsEquipment_Product_Materials_BOM\\smBikes");

    public static final String PRODUCT_TYPE_BIKES_SEPD = LCSProperties.get(PREFIX + ".LCSProduct.typeBikesSEPD",
            "Product\\SEPD\\smBikes");

    public static final String SM_REFERENCED_BOM_NAME_ATTRIBUTE = LCSProperties.get(PREFIX + ".smBOMReferenceAttributeAPD", "vrdBOMReference");
    public static final String SM_DO_BOM_ROLLUP_ATTRIBUTE = LCSProperties.get(PREFIX + ".smDoBOMRollUpAttribute", "vrdDoBOMRollup");
    public static final String SM_DO_BOM_UPDATE_ATTRIBUTE = LCSProperties.get(PREFIX + ".smDoBOMUpdateAttribute", "smDoBOMUpdate");
    public static final String SM_COSTSHEET_BOM_TABLE_APD_ATTRIBUTE = LCSProperties.get(PREFIX + ".smCostSheetBOMTableAPD", "smCostSheetBOMTableAPD");

    public static final String SM_COSTING_STAGE_ATTRIBUTE_APD = LCSProperties.get(PREFIX + ".smCostingStageAttributeAPD", "smCostingStageAPD");

    public static final String SM_COSTING_STAGE_ATTRIBUTE_FPD = LCSProperties.get(PREFIX + ".smCostingStageAttributeFPD", "smCostingStageFPD");

    public static final String SM_COSTING_STAGE_ATTRIBUTE_SEPD = LCSProperties.get(PREFIX + ".smCostingStageAttributeSEPD", "smSportsEquipmentCostingStage");

    public static final String SM_COST_SHEET_REFRESH_EXCHANGE_RATES_EXCLUDE_STATES = LCSProperties.get(
            PREFIX + ".refreshExchangeRates.excludeForValuesAtt.smCostSheetStatusFPD", "smApproved,smCancelled");
    public static final String SM_COST_SHEET_STATUS_APD = LCSProperties.get(
            PREFIX + ".smCostSheetStatusAPD", "smCSStatusAPD");
    public static final String SM_COST_SHEET_STATUS_FPD = LCSProperties.get(
            PREFIX + ".smCostSheetStatusFPD", "smCostSheetStatusFPD");
    public static final String SM_COST_SHEET_STATUS_SEPD = LCSProperties.get(
            PREFIX + ".smCostSheetStatusSEPD", "vrdCSStatus");
    public static final String SM_COST_SHEET_STATUS_SEPD_ACCESSORIES = LCSProperties.get(
            PREFIX + ".smCostSheetStatusSEPDApparelAccessories", "smCSStatusSEPD");

    /*  *** APD ***
        ___________________________________________________________________
        Значение атрибута smSectionAPD |	атрибут на КостШите
        -------------------------------------------------------------------
        vrdBOMFabrics                  |	smBOMFabricsGSCurr
        vrdBOMTrims                    |	smBOMTrimsGSCurr
        smBOMDecoration                |	smBOMDecorationGSCurr
        smBOMProductPackaging          |	smBOMProductPackagingGSCurr
        smBOMShippingPacking           |	smBOMShippingAndPackingGSCurr
        smBOMGarmentFinish             |	smBOMGarmentFinishGSCurr
        vrdBOMOther                    |	smBOMOtherGSCurr
        smCMTSupplier                  |	smCMTSupplierGSCurr
        smOverheadSupplier             |	smOverheadSupplierGsCurr
        smProfitSupplier               |	smProfitSupplierGSCurr
        smTransportationSupplier       |	smTransportationSupplierGSCurr
        smMiscellaneousSupplier        |	smMiscellaneousSupplierGSCurr
        ____________________________________________________________________
    */
    public static final String SM_COST_SHEET_ATTRS_FOR_SM_TOTAL_BOM_TABLE_APD = LCSProperties.get(PREFIX + ".attrsForSMTotalBOMTableAPD",
            "vrdBOMFabrics:smBOMFabricsGSCurr,vrdBOMTrims:smBOMTrimsGSCurr,smBOMDecoration:smBOMDecorationGSCurr,smBOMProductPackaging:smBOMProductPackagingGSCurr," +
                    "smBOMShippingPacking:smBOMShippingAndPackingGSCurr,smBOMGarmentFinish:smBOMGarmentFinishGSCurr,vrdBOMOther:smBOMOtherGSCurr," +
                    "smCMTSupplier:smCMTSupplierGSCurr,smOverheadSupplier:smOverheadSupplierGsCurr,smProfitSupplier:smProfitSupplierGSCurr," +
                    "smTransportationSupplier:smTransportationSupplierGSCurr,smMiscellaneousSupplier:smMiscellaneousSupplierGSCurr");

    public static final String SM_COST_SHEET_COSTING_STAGE_VALUES_CALCULATE_TOTAL_FOREACH_SECTION_APD = LCSProperties.get(PREFIX + ".calculateTotalForEachSectionAPD.smCostingStageValues",
            "smLr,smLc,smFinal");

    public static final String SM_COST_SHEET_ATTR_SM_TOTAL_GC_CURR_SUM_ATTRS_APD = LCSProperties.get(PREFIX + ".attrSMTotalGSCurr.sumAttrsAPD",
            "smBOMFabricsGSCurr,smBOMTrimsGSCurr,smBOMDecorationGSCurr,smBOMProductPackagingGSCurr,smBOMShippingAndPackingGSCurr,smBOMGarmentFinishGSCurr,smBOMOtherGSCurr");

    public static final String SM_COST_SHEET_ATTR_SM_QUOTED_PRICE_GC_CURR_SUM_ATTRS_APD = LCSProperties.get(PREFIX + ".attrSMQuotedPriceGSCurr.sumAttrsAPD",
            "smBOMTotalGSCurr,smCMTSupplierGSCurr,smOverheadSupplierGsCurr,smProfitSupplierGSCurr,smTransportationSupplierGSCurr,smMiscellaneousSupplierGSCurr");

    public static final String SM_COST_SHEET_ATTRS_CONVERT_IN_USD_APD = LCSProperties.get(PREFIX + ".attrsConvertInUSDForCostSheetAPD",
            "smQuotedPriceConverted:smQuotedPriceGSCurr,smOfflineManualPriceConverted:smOfflineManualPrice," +
                    "smBOMFabricsConverted:smBOMFabricsGSCurr,smBOMTrimsConverted:smBOMTrimsGSCurr,smBOMDecorationConverted:smBOMDecorationGSCurr," +
                    "smBOMProductPackagingConverted:smBOMProductPackagingGSCurr,smBOMShippingPackingConverted:smBOMShippingAndPackingGSCurr," +
                    "smBOMGarmentFinishConverted:smBOMGarmentFinishGSCurr,smBOMOtherConverted:smBOMOtherGSCurr,smBOMTotalConverted:smBOMTotalGSCurr," +
                    "vrdCMTFOB:smCMTSupplierGSCurr,smOverheadSupplier:smOverheadSupplierGsCurr,smProfitSupplierAmount:smProfitSupplierGSCurr," +
                    "smTransportationSupplier:smTransportationSupplierGSCurr,smMiscellaneousSupplier:smMiscellaneousSupplierGSCurr");

    public static List<String> getValues(String values) {
        return SMFormatHelper.toList(values);
    }
}
