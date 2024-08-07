package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.util.FormatHelper;
import com.ptc.core.meta.common.FloatingPoint;
import com.sportmaster.wc.mc.config.SMCostSheetConfig;
import com.sportmaster.wc.mc.object.SMProductCostSheet;
import com.sportmaster.wc.mc.tools.SMFormatHelper;
import org.apache.log4j.Logger;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.*;

public class SMCostSheetCalculateTotalSet {

    private static final Logger LOGGER = Logger.getLogger(SMCostSheetCalculateTotalSet.class);

    /**
     * Calculation of smTotal/smTotalSM attribute sums by sections (for APD)
     */
    public static void calculateCostSheetSectionsTotal_APD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {

        Collection bomTableRowsOnCostSheet = SMFormatHelper.getLCSMOATableRows( SMCostSheetTypeSelector.getSMCostSheetBOMTable( lcsProductCostSheet ) );
        calculateCostSheetSectionsTotal_APD(lcsProductCostSheet, bomTableRowsOnCostSheet);
    }

    /**
     * Calculation of smTotal/smTotalSM attribute sums by sections (for APD)
     */
    public static void calculateCostSheetSectionsTotal_APD(SMProductCostSheet productCostSheet) throws WTException, WTPropertyVetoException {

        Collection bomTableRowsOnCostSheet = SMFormatHelper.getLCSMOATableRows( SMCostSheetTypeSelector.getSMCostSheetBOMTable( productCostSheet ) );
        calculateCostSheetSectionsTotal_APD(productCostSheet.getLCSProductCostSheet(), bomTableRowsOnCostSheet);
    }

    /**
     * Calculation of smTotal/smTotalSM attribute sums by sections (for APD)
     */
    public static void calculateCostSheetSectionsTotal_APD(LCSProductCostSheet lcsProductCostSheet, Collection bomTableRowsOnCostSheet) throws WTException, WTPropertyVetoException {

        String smCostingStage = FormatHelper.format( (String) lcsProductCostSheet.getValue( com.sportmaster.wc.mc.config.SMCostSheetConfig.SM_COSTING_STAGE_ATTRIBUTE_APD ) );
        String attrNameTotal = SMCostSheetConfig.SM_COST_SHEET_COSTING_STAGE_VALUES_CALCULATE_TOTAL_FOREACH_SECTION_APD.contains(smCostingStage) ? "smTotal" : "smTotalSM";

        List<FlexObject> filteredRowsOnCostSheet = SMCostSheetFilter.filter( bomTableRowsOnCostSheet, "smIncludeInPurchasePrice", true );

        Map<String, Double> sectionsTotalMap = SMCostSheetCalculateTotalGet.getCostSheetSectionsTotalCalculated_APD(filteredRowsOnCostSheet, attrNameTotal, false);

        Map<String, String> mapped = SMFormatHelper.toMap( SMCostSheetConfig.SM_COST_SHEET_ATTRS_FOR_SM_TOTAL_BOM_TABLE_APD );
        for (Map.Entry<String,String> entry : mapped.entrySet()) {
            String attrNameInBOMTable = entry.getKey();
            String attrNameInCostSheet = entry.getValue();
            double attrValueInCostSheet =  SMFormatHelper.getDouble( sectionsTotalMap.get(attrNameInBOMTable) );
            lcsProductCostSheet.setValue( attrNameInCostSheet, new FloatingPoint( attrValueInCostSheet, 14) );
        }
    }

    /**
     * Calculation smBOMTotalGSCurr for APD
     * smBOMTotalGSCurr = smBOMFabricsGSCurr + smBOMTrimsGSCurr + smBOMDecorationGSCurr +
     *             smBOMProductPackagingGSCurr + smBOMShippingAndPackingGSCurr +  smBOMGarmentFinishGSCurr + smBOMOtherGSCurr
     */
    public static void calculateCostSheetSMBOMTotalGSCurr_APD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {

        //List attributes on CostSheet for calculating smBOMTotalGSCurr
        List<String> listAttrsForSMBOMTotalGSCurr = SMCostSheetConfig.getValues( SMCostSheetConfig.SM_COST_SHEET_ATTR_SM_TOTAL_GC_CURR_SUM_ATTRS_APD );
        double smBOMTotalGSCurr = 0;
        for (String attr : listAttrsForSMBOMTotalGSCurr) {
            smBOMTotalGSCurr += SMFormatHelper.getDouble( lcsProductCostSheet.getValue(attr) );
        }
        lcsProductCostSheet.setValue( "smBOMTotalGSCurr", new FloatingPoint( smBOMTotalGSCurr, 14) );
    }

    /**
     * Calculation smQuotedPriceGSCurr for APD
     * smQuotedPriceGSCurr = if smOfflineManualPrice = 0,
     *             smBOMTotalGSCurr + smCMTSupplierGSCurr + smOverheadSupplierGsCurr + smProfitSupplierGSCurr +
     *             smTransportationSupplierGSCurr + smMiscellaneousSupplierGSCurr,
     *                 smOfflineManualPrice
     */
    public static void calculateCostSheetSMBOMQuotedPriceGSCurr_APD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {

        double smOfflineManualPrice = SMFormatHelper.getDouble( lcsProductCostSheet.getValue("smOfflineManualPrice") );
        if (smOfflineManualPrice != 0) {
            lcsProductCostSheet.setValue( "smQuotedPriceGSCurr", new FloatingPoint( smOfflineManualPrice, 14) );
            return;
        }

        //List attributes on CostSheet for calculating smQuotedPriceGSCurr
         List<String> listAttrsForSMBOMQuotedPriceGSCurr = SMCostSheetConfig.getValues( SMCostSheetConfig.SM_COST_SHEET_ATTR_SM_QUOTED_PRICE_GC_CURR_SUM_ATTRS_APD );
        double smQuotedPriceGSCurr = 0;
        for (String attr : listAttrsForSMBOMQuotedPriceGSCurr) {
            smQuotedPriceGSCurr += SMFormatHelper.getDouble( lcsProductCostSheet.getValue(attr) );
        }
        lcsProductCostSheet.setValue( "smQuotedPriceGSCurr", new FloatingPoint( smQuotedPriceGSCurr, 14) );
    }

    /**
     * Calculation of smTotal attribute sums by sections (for FPD)
     */
    public static double calculateCostSheetSectionsTotal_FPD(LCSProductCostSheet lcsProductCostSheet) {
        try {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.calculateCostSheetSectionsTotal_FPD: Start.");
            LCSMOATable lcsmoaTable = (LCSMOATable) lcsProductCostSheet.getValue(
                    SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcsProductCostSheet));

            Map<String, Double> sectionsTotalMap =
                    SMCostSheetCalculateTotalGet.getCostSheetSectionsTotalCalculated_FPD( SMFormatHelper.getLCSMOATableRows(lcsmoaTable), false );

            Double smUpperBOMTable = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_BOM_SECTION_UPPER);
            if (smUpperBOMTable != null) lcsProductCostSheet.setValue("smUpperBOMTable", new FloatingPoint(smUpperBOMTable, 14));
            else lcsProductCostSheet.setValue("smUpperBOMTable", new FloatingPoint(0, 14));

            Double smLiningBOMTable = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_BOM_SECTION_LINING);
            if (smLiningBOMTable != null) lcsProductCostSheet.setValue("smLiningBOMTable", new FloatingPoint(smLiningBOMTable, 14));
            else lcsProductCostSheet.setValue("smLiningBOMTable", new FloatingPoint(0, 14));

            Double smInterliningBOMTable = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_BOM_SECTION_INTERLINING);
            if (smInterliningBOMTable != null) lcsProductCostSheet.setValue("smInterliningBOMTable", new FloatingPoint(smInterliningBOMTable, 14));
            else lcsProductCostSheet.setValue("smInterliningBOMTable", new FloatingPoint(0, 14));

            Double smTrimBOMTable = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_BOM_SECTION_TRIM);
            if (smTrimBOMTable != null) lcsProductCostSheet.setValue("smTrimBOMTable", new FloatingPoint(smTrimBOMTable, 14));
            else lcsProductCostSheet.setValue("smTrimBOMTable", new FloatingPoint(0, 14));

            Double smInsoleBOMTable = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_BOM_SECTION_INSOLE);
            if (smInsoleBOMTable != null) lcsProductCostSheet.setValue("smInsoleBOMTable", new FloatingPoint(smInsoleBOMTable, 14));
            else lcsProductCostSheet.setValue("smInsoleBOMTable", new FloatingPoint(0, 14));

            Double smOutsoleBOMTable = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_BOM_SECTION_OUTSOLE);
            if (smOutsoleBOMTable != null) lcsProductCostSheet.setValue("smOutsoleBOMTable", new FloatingPoint(smOutsoleBOMTable, 14));
            else lcsProductCostSheet.setValue("smOutsoleBOMTable", new FloatingPoint(0, 14));

            Double smLogoDecorationBOMTable = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_BOM_SECTION_LOGO_DECORATION);
            if (smLogoDecorationBOMTable != null) lcsProductCostSheet.setValue("smLogoDecorationBOMTable", new FloatingPoint(smLogoDecorationBOMTable, 14));
            else lcsProductCostSheet.setValue("smLogoDecorationBOMTable", new FloatingPoint(0, 14));

            Double smPackingBOMTable = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_BOM_SECTION_PACKING);
            if (smPackingBOMTable != null) lcsProductCostSheet.setValue("smPackingBOMTable", new FloatingPoint(smPackingBOMTable, 14));
            else lcsProductCostSheet.setValue("smPackingBOMTable", new FloatingPoint(0, 14));

            //Calculate "BOM Total Table" (smBOMTotalTable)
            double smBOMTotalTable = SMCostSheetCalculateTotalGet.getCostSheetBOMTableTotalCalculated(sectionsTotalMap);
            lcsProductCostSheet.setValue("smBOMTotalTable", new FloatingPoint(smBOMTotalTable, 14));

            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.calculateCostSheetSectionsTotal_FPD: Finish.");

            return smBOMTotalTable;

        } catch (WTException | WTPropertyVetoException exception) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetTools.calculateCostSheetSectionsTotal_FPD: Exception ("+ exception.getMessage() + ")", exception);
            exception.printStackTrace();
        }

        return 0;
    }

    /**
     * Calculation of smTotal attribute sums by NMC sections (for FPD)
     */
    public static void calculateCostSheetNmcSectionsTotal_FPD(LCSProductCostSheet lcsProductCostSheet) {
        try {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.calculateCostSheetNmcTotal_FPD: Start.");
            LCSMOATable lcsmoaTable = (LCSMOATable) lcsProductCostSheet.getValue(
                    SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcsProductCostSheet));

            Map<String, Double> sectionsTotalMap = new HashMap<>();

            if (lcsmoaTable != null) {
                for (Object object : lcsmoaTable.getRows(true)) {
                    FlexObject flexObject = (FlexObject) object;
                    String section = FormatHelper.format( flexObject.getData("smSection") );
                    if (section.equals("NMC")) {
                        String placement = flexObject.getData("smPlacement");
                        if (placement != null && !placement.trim().isEmpty()) {
                            Double total = flexObject.getDouble("smTotal");
                            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.calculateCostSheetNmcTotal_FPD: smPlacement = " + placement + ", total: " + total);
                            sectionsTotalMap.merge(placement, total, Double::sum);
                        }
                    }
                }
            }

            Double vrdUppersFOB = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_NMC_UPPER); //Upper (Supplier) - vrdUppersFOB
            if (vrdUppersFOB != null) lcsProductCostSheet.setValue("vrdUppersFOB", new FloatingPoint(vrdUppersFOB, 14));
            else lcsProductCostSheet.setValue("vrdUppersFOB", new FloatingPoint(0, 14));

            Double smSoleSupplier = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_NMC_SOLE); //Sole for Pullover (Supplier) - smSoleSupplier
            if (smSoleSupplier != null) lcsProductCostSheet.setValue("smSoleSupplier", new FloatingPoint(smSoleSupplier, 14));
            else lcsProductCostSheet.setValue("smSoleSupplier", new FloatingPoint(0, 14));

            Double vrdCMTFOB = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_NMC_LABOR); //Labor (supplier) - vrdCMTFOB
            if (vrdCMTFOB != null) lcsProductCostSheet.setValue("vrdCMTFOB", new FloatingPoint(vrdCMTFOB, 14));
            else lcsProductCostSheet.setValue("vrdCMTFOB", new FloatingPoint(0, 14));

            Double smOverheadSupplier = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_NMC_OVERHEAD); //Overhead (Supplier ) - smOverheadSupplier
            if (smOverheadSupplier != null) lcsProductCostSheet.setValue("smOverheadSupplier", new FloatingPoint(smOverheadSupplier, 14));
            else lcsProductCostSheet.setValue("smOverheadSupplier", new FloatingPoint(0, 14));

            Double smNewLastCost = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_NMC_NEW_LAST_COST); //New last cost - smNewLastCost
            if (smNewLastCost != null) lcsProductCostSheet.setValue("smNewLastCost", new FloatingPoint(smNewLastCost, 14));
            else lcsProductCostSheet.setValue("smNewLastCost", new FloatingPoint(0, 14));

            Double smDieCutCost = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_NMC_DIE_CUT_COST); //Die cut cost - smDieCutCost
            if (smDieCutCost != null) lcsProductCostSheet.setValue("smDieCutCost", new FloatingPoint(smDieCutCost, 14));
            else lcsProductCostSheet.setValue("smDieCutCost", new FloatingPoint(0, 14));

            Double smProfitSupplier = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_NMC_PROFIT); //Profit (Supplier) - smProfitSupplier
            if (smProfitSupplier != null) lcsProductCostSheet.setValue("smProfitSupplier", new FloatingPoint(smProfitSupplier, 14));
            else lcsProductCostSheet.setValue("smProfitSupplier", new FloatingPoint(0, 14));

            Double smSourcingCommissionSupplier = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_NMC_SOURCING); //Sourcing Commission (Trading company) - smSourcingCommissionSupplier
            if (smSourcingCommissionSupplier != null) lcsProductCostSheet.setValue("smSourcingCommissionSupplier", new FloatingPoint(smSourcingCommissionSupplier, 14));
            else lcsProductCostSheet.setValue("smSourcingCommissionSupplier", new FloatingPoint(0, 14));

            Double smTransportationSupplier = sectionsTotalMap.get(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.MOA_TABLE_NMC_TRANSPORTATION); //Transportation (Local Logistics) - smTransportationSupplier
            if (smTransportationSupplier != null) lcsProductCostSheet.setValue("smTransportationSupplier", new FloatingPoint(smTransportationSupplier, 14));
            else lcsProductCostSheet.setValue("smTransportationSupplier", new FloatingPoint(0, 14));

            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.calculateCostSheetNmcTotal_FPD: Finish.");
        } catch (WTException | WTPropertyVetoException exception) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetTools.calculateCostSheetNmcTotal_FPD: Exception ("+ exception.getMessage() + ")", exception);
            exception.printStackTrace();
        }
    }

    /**
     * Calculation of smNMCTotal
     * Formula:
     * Labor + Overhead+New last cost+Die cut cost+Profit + Sourcing comission (Trading company)+Transportation (Local Logistics)
     * vrdCMTFOB + smOverheadSupplier + smProfitSupplier + smNewLastCost + smDieCutCost + smSourcingCommissionSupplier + smTransportationSupplier
     */
    public static void calculateSMNMCTotal(LCSProductCostSheet lcsProductCostSheet) {

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMNMCTotal: Start.");
        try {
            double vrdCMTFOB = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("vrdCMTFOB"));
            double smOverheadSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smOverheadSupplier"));
            double smProfitSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smProfitSupplier"));
            double smNewLastCost = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smNewLastCost"));
            double smDieCutCost = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smDieCutCost"));
            double smSourcingCommissionSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smSourcingCommissionSupplier"));
            double smTransportationSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smTransportationSupplier"));

            double result = vrdCMTFOB + smOverheadSupplier + smProfitSupplier + smNewLastCost +
                    smDieCutCost + smSourcingCommissionSupplier + smTransportationSupplier;

            lcsProductCostSheet.setValue("smNMCTotal", new FloatingPoint(result, 14));

            LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculateTotalSet.calculateSMNMCTotal: smNMCTotal=" + result + ". Finish.");
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculateTotalSet.calculateSMNMCTotal: Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
    }

    /**
     * Calculation of smTotal attribute sums by NMC sections (for SEPD)
     */
    public static void calculateCostSheetSectionsTotal_SEPD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {

        Collection bomTableRows = SMFormatHelper.getLCSMOATableRows( SMCostSheetTypeSelector.getSMCostSheetBOMTable( lcsProductCostSheet ) );
        Collection bomAdditionalTableRows = SMFormatHelper.getLCSMOATableRows( SMCostSheetTypeSelector.getSMCostSheetBOMAdditionalTable( lcsProductCostSheet ) );

        List<FlexObject> rowsInBOMTable = SMCostSheetFilter.filter( bomTableRows,
                "smIncludeInPurchasePrice", true );
        List<FlexObject> rowsInBOMAdditionalTable = SMCostSheetFilter.filter( bomAdditionalTableRows,
                "smIncludeInPurchasePrice", true );

        List<FlexObject> rows = new ArrayList<>();
        rows.addAll(rowsInBOMTable);
        rows.addAll(rowsInBOMAdditionalTable);

        Double smLaborSupplier = 0D;
        Double smOverheadSupplier = 0D;
        Double smProfitSupplierAmount = 0D;
        Double smTransportationSupplier = 0D;

        for (Object object : rows) {
            FlexObject currentRow = (FlexObject) object;
            // smLaborSupplier - LABOR ==>> smLaborSupplier
            // smOverheadSupplier - OVERHEAD ==>> smOverheadSupplie
            // smProfitSupplier - PROFIT ==>> smProfitSupplierAmount
            // smTransportationSupplier - Transportation, Local Logistics - ONLY FOR DDP ==>> smTransportationSupplier
            String smSectionSEPD = FormatHelper.format( currentRow.getData("smSectionSEPD") );
            if (smSectionSEPD.equals("smLaborSupplier")) { //LABOR
                smLaborSupplier += FormatHelper.parseDouble( currentRow.getData("smTotal"));
            }
            if (smSectionSEPD.equals("smOverheadSupplier")) { //OVERHEAD
                smOverheadSupplier += FormatHelper.parseDouble( currentRow.getData("smTotal"));
            }
            if (smSectionSEPD.equals("smProfitSupplier")) { //PROFIT
                smProfitSupplierAmount += FormatHelper.parseDouble( currentRow.getData("smTotal"));
            }
            if (smSectionSEPD.equals("smTransportationSupplier")) { //Transportation, Local Logistics
                smTransportationSupplier += FormatHelper.parseDouble( currentRow.getData("smTotal"));
            }
        }

        // Labor (Supplier)
        lcsProductCostSheet.setValue( "smLaborSupplier", new FloatingPoint( smLaborSupplier, 14) );
        // Overhead (Supplier)
        lcsProductCostSheet.setValue( "smOverheadSupplier", new FloatingPoint( smOverheadSupplier, 14) );
        // Profit (Supplier)
        lcsProductCostSheet.setValue( "smProfitSupplierAmount", new FloatingPoint( smProfitSupplierAmount, 14) );
        // Transportation, Local Logistics - ONLY FOR DDP Incoterms
        lcsProductCostSheet.setValue( "smTransportationSupplier", new FloatingPoint( smTransportationSupplier, 14) );
    }

    /**
     * BOM Total = SUM(BOM Section Total from BOM Sections Totals Table)
     * @return smBOMTotal = SUM(smBOMSectionTotal from smBOMSectionsTotals )
     */
    public static double calculateBOMTotal_SEPD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {

        double smBOMTotal = SMCostSheetCalculateTotalGet.getBOMTotal_SEPD(lcsProductCostSheet);

        lcsProductCostSheet.setValue( "smBOMTotal", new FloatingPoint( smBOMTotal, 14) );

        return smBOMTotal;
    }

    /**
     * Calculate NMC Total = Labor (Supplier) + Overhead (Supplier) + Profit (Supplier) + Transportation, Local Logistics - ONLY FOR DDP Incoterms
     * @return smNMCTotal =
     *         smLaborSupplier + smOverheadSupplier + smProfitSupplierAmount + smTransportationSupplier
     */
    public static double calculateNMCTotal_SEPD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {

        double smLaborSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smLaborSupplier"));
        double smOverheadSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smOverheadSupplier"));
        double smProfitSupplierAmount = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smProfitSupplierAmount"));
        double smTransportationSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smTransportationSupplier"));

        double smNMCTotal = smLaborSupplier + smOverheadSupplier + smProfitSupplierAmount + smTransportationSupplier;
        lcsProductCostSheet.setValue( "smNMCTotal", new FloatingPoint( smNMCTotal, 14) );

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculateTotalSet.calculateNMCTotal_SEPD: smNMCTotal=" + smNMCTotal);

        return smNMCTotal;
    }

    /**
     * Copies value of smTotal or smTotalSM to smTotalFinal depending on the value of the smCostingStage attribute.
     * @param lcsmoaObject - row in BOMTable
     * @param lcsProductCostSheet - current CostSheet
     */
    public static void setSMTotalFinal_APD(LCSMOAObject lcsmoaObject, LCSProductCostSheet lcsProductCostSheet) {

        try {
            String smCostingStage = FormatHelper.format( (String) lcsProductCostSheet.getValue( SMCostSheetConfig.SM_COSTING_STAGE_ATTRIBUTE_APD ) );
            String sourceAttrNameForSMTotalFinal = SMCostSheetConfig.SM_COST_SHEET_COSTING_STAGE_VALUES_CALCULATE_TOTAL_FOREACH_SECTION_APD.contains(smCostingStage) ? "smTotal" : "smTotalSM";
            lcsmoaObject.setValue("smTotalFinal", lcsmoaObject.getValue( sourceAttrNameForSMTotalFinal ));
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculateTotalSet.setSMTotalFinal_APD: " +
                    "Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
    }

    /**
     * Copies value of smTotal or smTotalSM to smTotalFinal depending on the value of the smCostingStage attribute.
     * For all BOM Table rows on CostSheet.
     * With saving objects to the database.
     */
    public static void updateSMTotalFinalForBOMTableRows_APD(SMProductCostSheet productCostSheet) {

        try {
            String smCostingStage = FormatHelper.format( (String) productCostSheet.getValue( SMCostSheetConfig.SM_COSTING_STAGE_ATTRIBUTE_APD ) );
            String sourceAttrNameForSMTotalFinal = SMCostSheetConfig.SM_COST_SHEET_COSTING_STAGE_VALUES_CALCULATE_TOTAL_FOREACH_SECTION_APD.contains(smCostingStage) ? "smTotal" : "smTotalSM";

            LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculateTotalSet.saveSMTotalFinalForBOMTableRows_APD: attribute of " + sourceAttrNameForSMTotalFinal + " copy to smTotalFinal");

            Map<String,String> attrs = new HashMap<>();
            attrs.put(sourceAttrNameForSMTotalFinal, "smTotalFinal");

            String bomTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(productCostSheet);
            SMCostSheetTools.copyMOATableAttrValues(productCostSheet.getLCSProductCostSheet(), bomTableName, attrs, true, true);
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculateTotalSet.saveSMTotalFinalForBOMTableRows_APD: " +
                    "Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
    }
}
