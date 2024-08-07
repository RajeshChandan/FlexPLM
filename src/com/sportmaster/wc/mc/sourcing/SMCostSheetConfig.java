package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.util.LCSProperties;
import com.sportmaster.wc.mc.tools.SMFormatHelper;
import java.util.List;

public class SMCostSheetConfig {

    //Current config property prefix
    private static final String PREFIX = "com.sportmaster.wc.mc.sourcing.SMCostSheetConfig";

    public static final String CURRENCY_RATES_TABLE = LCSProperties.get(PREFIX + ".smCurrencyRatesTable", "smCurrencyRatesTable");
    public static final String FLEX_PATH = LCSProperties.get(PREFIX + ".flexPath", "Business Object\\Lookup Tables\\smSeasonalExchangeRates");
    public static final String LCS_LIFECYCLE_MANAGED = "LCSLifecycleManaged";
    public static final String BO_EXCHANGE_RATE = LCSProperties.get(PREFIX + ".smFxExchangeRate", "smFxExchangeRate");
    public static final String BO_CURRENCY = LCSProperties.get(PREFIX + ".smFxCurrency", "smFxCurrency");
    public static final String BO_SEASON = LCSProperties.get(PREFIX + ".smFxSeason", "smFxSeason");

    //CostSheet
    public static final String REFRESH_EXCHANGERATE = LCSProperties.get(PREFIX + ".refreshExchangeRates", "smRefreshExchangeRate");
    public static final String DO_BOM_UPDATE = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.doBOMUpdate", "smDoBOMUpdate");
    public static final String BOM_ROLL_UP_DATE = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.BOMRollUpDate", "vrdDateBOMRollup");
    public static final String COST_SHEET_ATT_DATE_OF_BOM_UPDATE = LCSProperties.get(PREFIX + ".smDateofBOMUpdate", "smDateofBOMUpdate");
    public static final String VRD_EFFECTIVE_DATE_ATT = LCSProperties.get("com.vrd.costsheet.SMCostSheetConfig.EffectiveDate","vrdEffectiveDate");

    public static final String BOM_ATTS = LCSProperties.get("com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.BOMAtts",
            "");

    public static final String SUPPLIER_TYPE = LCSProperties.get("com.lcs.wc.supplier.SupplierRootType","Supplier");
    public static final String MATERIAL_SUPPLIER_TYPE = LCSProperties.get("com.lcs.wc.supplier.MaterialSupplierRootType","com.lcs.wc.material.LCSMaterialSupplier"); //Material Supplier
    public static final String DEFAULT_EFFECTIVE_DATE_ATT = LCSProperties.get("com.vrd.reports.PublishBOMPlugin.DefaultEffectiveDate","Season.vrdEffectiveDate");
    public static final String BOM_REF_NAME_ATT = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.BOMReference", "vrdBOMReference");
    public static final String FPD_COSTING_STAGE_VALUE = LCSProperties.get(PREFIX + ".smFPDCostingStageValue", "smPullover");

    public static final String SM_CS_EXCHANGE_RATE = LCSProperties.get(PREFIX + ".smCsExchangeRate", "smCsExchangeRate");

    public static final String SM_BASIC_MATERIAL_PRICE = LCSProperties.get(PREFIX + ".MaterialPricingEntry.smBasicMaterialPrice","smBasicMaterialPrice");

    //MaterialPricingEntry
    public static final String MATERIAL_PRICE = LCSProperties.get(PREFIX + ".MaterialPricingEntry.materialPrice","materialPrice");
    public static final String MATERIAL_PRICING_ENTRY_SM_MATERIAL_PRICE_LC = LCSProperties.get(PREFIX + ".MaterialPricingEntry.smMaterialPriceLC","smMsPrice");
    public static final String MATERIAL_PRICING_ENTRY_SM_CURRENCY_UNIT = LCSProperties.get(PREFIX + ".MaterialPricingEntry.smCurrencyUnit","smMPECurrencyUnit");//smCurrencyUnit
    public static final String VRD_COLOR_SPECIFIC_PRICE = LCSProperties.get(PREFIX + ".MaterialPricingEntry.vrdColorSpecificPrice","vrdColorSpecificPrice");
    public static final String SM_COLOR_SPECIFIC_PRICE_LC = LCSProperties.get(PREFIX + ".MaterialPricingEntry.smColorSpecificPriceLC","smMsColorSpecificPrice");

    public static final String COLOR_SPECIFIC_CURRENCY_UNIT = LCSProperties.get(PREFIX + ".MaterialColor.smCurrencyUnit","smMsColorCurrency");//smCurrencyUnit

    public static final String MOA_TABLE_EXCHANGE_RATES_CURRENCY_VALUE = LCSProperties.get(PREFIX + ".MOATable.exchangeRatesCurrencyValue","smCurrencyValue");//smCurrencyValue attribute for Exchange Rates Table
    public static final String MOA_TABLE_EXCHANGE_RATES_CURRENCY_RATE = LCSProperties.get(PREFIX + ".MOATable.exchangeRatesCurrencyRate","smCurrencyRate");//smCurrencyRate attribute for Exchange Rates Table

    // MOA Table - String values for NMC rows
    public static final String MOA_TABLE_NMC_UPPER = LCSProperties.get(PREFIX + ".MOATable.nmcUpperStringValue","UPPER for Pullover");
    public static final String MOA_TABLE_NMC_SOLE  = LCSProperties.get(PREFIX + ".MOATable.nmcSoleStringValue", "SOLE for Pullover");
    public static final String MOA_TABLE_NMC_LABOR = LCSProperties.get(PREFIX + ".MOATable.nmcLaborStringValue","Labor");
    public static final String MOA_TABLE_NMC_OVERHEAD = LCSProperties.get(PREFIX + ".MOATable.nmcOverheadStringValue","Overhead");
    public static final String MOA_TABLE_NMC_NEW_LAST_COST = LCSProperties.get(PREFIX + ".MOATable.nmcNewLastCostStringValue","New last cost");
    public static final String MOA_TABLE_NMC_DIE_CUT_COST = LCSProperties.get(PREFIX + ".MOATable.nmcDieCutCostStringValue","Die cut cost");
    public static final String MOA_TABLE_NMC_PROFIT = LCSProperties.get(PREFIX + ".MOATable.nmcProfitStringValue","Profit");
    public static final String MOA_TABLE_NMC_SOURCING = LCSProperties.get(PREFIX + ".MOATable.nmcSourcingStringValue","Sourcing commission (Trading company)");
    public static final String MOA_TABLE_NMC_TRANSPORTATION = LCSProperties.get(PREFIX + ".MOATable.nmcTransportationStringValue","Transportation (Local Logistics)");


    // MOA Table - String display values for section attribute
    public static final String MOA_TABLE_BOM_SECTION_UPPER = LCSProperties.get(PREFIX + ".MOATable.sectionDisplayValue.upper","01 - Upper");
    public static final String MOA_TABLE_BOM_SECTION_LINING = LCSProperties.get(PREFIX + ".MOATable.sectionDisplayValue.lining","02 - Lining");
    public static final String MOA_TABLE_BOM_SECTION_INTERLINING = LCSProperties.get(PREFIX + ".MOATable.sectionDisplayValue.interlining","03 - Interlining");
    public static final String MOA_TABLE_BOM_SECTION_TRIM = LCSProperties.get(PREFIX + ".MOATable.sectionDisplayValue.trim","04 - Trim");
    public static final String MOA_TABLE_BOM_SECTION_INSOLE = LCSProperties.get(PREFIX + ".MOATable.sectionDisplayValue.insole","05 - Insole");
    public static final String MOA_TABLE_BOM_SECTION_OUTSOLE = LCSProperties.get(PREFIX + ".MOATable.sectionDisplayValue.outsole","06 - Outsole");
    public static final String MOA_TABLE_BOM_SECTION_LOGO_DECORATION = LCSProperties.get(PREFIX + ".MOATable.sectionDisplayValue.logoDecoration","07 - Logo/Decorations");
    public static final String MOA_TABLE_BOM_SECTION_PACKING = LCSProperties.get(PREFIX + ".MOATable.sectionDisplayValue.packing","08 - Packing");

    //Material
    public static final String MATERIAL_MATERIAL_PRICE = LCSProperties.get(PREFIX + ".Material.materialPrice","materialPrice");
    public static final String MATERIAL_SM_MATERIAL_PRICE_LC = LCSProperties.get(PREFIX + ".Material.smMaterialPriceLC","smMaterialPriceLC");
    public static final String MATERIAL_SM_CURRENCY_UNIT = LCSProperties.get(PREFIX + ".Material.smCurrencyUnit","smCurrencyUnit");
    public static final String SUPPLIER_CURRENCY_ATT = LCSProperties.get(PREFIX + ".Supplier.smSuppliersContractCurrency","smSuppliersContractCurrency");
    public static final String MATERIAL_COLOR_CURRENCY_ATT = LCSProperties.get(PREFIX + ".MaterialColor.smMsColorCurrency","smMsColorCurrency");
    public static final String MATERIAL_SUPPLIER_CURRENCY_ATT = LCSProperties.get(PREFIX + ".MaterialColor.smMsCurrencyUnit","smMsCurrencyUnit");

    public static List<String> getValues(String values) {
        return SMFormatHelper.toList(values);
    }
}
