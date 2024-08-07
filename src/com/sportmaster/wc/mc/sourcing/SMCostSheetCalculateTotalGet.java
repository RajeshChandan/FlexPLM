package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.mc.tools.SMFormatHelper;
import org.apache.log4j.Logger;
import wt.util.WTException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SMCostSheetCalculateTotalGet {

    private static final Logger LOGGER = Logger.getLogger(SMCostSheetCalculateTotalGet.class);

    private interface SMTotal {
        double getSMTotal(FlexObject flexObject);
    }

    /**
     * Calculate "BOM Total Table" (total for all rows) for APD
     * @param flexObjects - attributes map for row
     * @param recalculatedTotal - recalculate or read current value
     * @return total of sections
     */
    public static double getCostSheetBOMTableTotalCalculated_APD(Collection flexObjects, boolean recalculatedTotal) {

        Map<String, Double> sectionsTotalMap = getCostSheetSectionsTotalCalculated_APD(flexObjects, "smTotal", recalculatedTotal);
        return getCostSheetBOMTableTotalCalculated(sectionsTotalMap);
    }

    /**
     * Calculate "BOM Total Table" (total for all rows) for FPD
     * @param flexObjects - attributes map for row
     * @param recalculatedTotal - recalculate or read current value
     * @return total of sections
     */
    public static double getCostSheetBOMTableTotalCalculated_FPD(Collection flexObjects, boolean recalculatedTotal) {

        Map<String, Double> sectionsTotalMap = getCostSheetSectionsTotalCalculated_FPD(flexObjects, recalculatedTotal);
        return getCostSheetBOMTableTotalCalculated(sectionsTotalMap);
    }

    /**
     * Calculate "BOM Total Table" (total for all rows) for SEPD
     * @param flexObjects - attributes map for row
     * @param recalculatedTotal - recalculate or read current value
     * @return total of sections
     */
    public static double getCostSheetBOMTableTotalCalculated_SEPD(Collection flexObjects, boolean recalculatedTotal) {

        Map<String, Double> sectionsTotalMap = getCostSheetSectionsTotalCalculated_SEPD(flexObjects, recalculatedTotal);
        return getCostSheetBOMTableTotalCalculated(sectionsTotalMap);
    }

    /**
     * Calculate "BOM Total Table" (smBOMTotalTable)
     */
    public static double getCostSheetBOMTableTotalCalculated(Map<String, Double> sectionsTotalMap) {

        double smBOMTotalTable = 0;
        for (Double value : sectionsTotalMap.values()) {
            if (value != null) {
                smBOMTotalTable += value;
            }
        }
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculateTotalGet.getCostSheetBOMTableTotalCalculated: result: " + smBOMTotalTable);
        return smBOMTotalTable;
    }

    /**
     * Count the total for each section (for FPD)
     * @return Map where:
     *  key - the name of the section
     *  value - the total for this section
     */
    public static Map<String, Double> getCostSheetSectionsTotalCalculated_FPD(Collection flexObjects, boolean recalculatedTotal) {
        try {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculateTotalGet.getCostSheetSectionsTotalCalculated_FPD: Start.");

            Map<String, Double> sectionsTotalMap = new HashMap<>();

            for (Object object : flexObjects) {
                FlexObject flexObject = (FlexObject) object;
                String section = FormatHelper.format( flexObject.getData("smSection") );
                if (!section.isEmpty() && !section.equals("NMC")) {
                    Double total;
                    if (!recalculatedTotal) total = flexObject.getDouble("smTotal");
                    else total = getSMTotal(flexObject);
                    LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculateTotalGet.getCostSheetSectionsTotalCalculated_FPD: smSection = " + section + ", total: " + total);
                    sectionsTotalMap.merge(section, total, Double::sum);
                }
            }

            LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculateTotalGet.getCostSheetSectionsTotalCalculated_FPD: Finish.");
            return sectionsTotalMap;
        } catch ( Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculateTotalGet.getCostSheetSectionsTotalCalculated_FPD: Exception ("+ e.getMessage() + ")");
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    /**
     * Count the total for each section (for APD)
     * @return Map where:
     *  key - the name of the section
     *  value - the total for this section
     */
    public static Map<String, Double> getCostSheetSectionsTotalCalculated_APD(
            Collection<FlexObject> flexObjects, String totalAttInternalName, boolean recalculatedTotal) {

        SMTotal smTotal;
        if (recalculatedTotal) {
            if ("smTotalSM".equals(totalAttInternalName))
                smTotal = flexObject -> getSMTotalSM_APD(flexObject);
            else smTotal = flexObject -> getSMTotal_APD(flexObject);
        }
        else smTotal = flexObject -> flexObject.getDouble(totalAttInternalName);
        return getCostSheetSectionsTotalCalculated(flexObjects, "smSectionAPD", smTotal);
    }

    /**
     * Count the total for each section (for SEPD)
     * @return Map where:
     *  key - the name of the section
     *  value - the total for this section
     */
    public static Map<String, Double> getCostSheetSectionsTotalCalculated_SEPD(
            Collection<FlexObject> flexObjects, boolean recalculatedTotal) {

        SMTotal smTotal;
        if (recalculatedTotal) smTotal = flexObject -> getSMTotal_SEPD(flexObject);
        else smTotal = flexObject -> flexObject.getDouble("smTotal");
        return getCostSheetSectionsTotalCalculated(flexObjects, "smSectionSEPD", smTotal);
    }

    /**
     * Count the total for each section (for APD/SEPD)
     * @return Map where:
     *  key - the name of the section
     *  value - the total for this section
     */
    public static Map<String, Double> getCostSheetSectionsTotalCalculated(
            Collection<FlexObject> flexObjects, String sectionAttInternalName, SMTotal smTotal) {

        try {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculateTotalGet.getCostSheetSectionsTotalCalculated: Start.");

            Map<String, Double> sectionsTotalMap = new HashMap<>();
            for (FlexObject flexObject : flexObjects) {
                String section = FormatHelper.format( flexObject.getData(sectionAttInternalName) );
                if ( !section.isEmpty() ) {
                    Double total = smTotal.getSMTotal(flexObject);
                    LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculateTotalGet.getCostSheetSectionsTotalCalculated: " + sectionAttInternalName +
                            " = " + section + ", total: " + total);
                    sectionsTotalMap.merge(section, total, Double::sum);
                }
            }

            LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculateTotalGet.getCostSheetSectionsTotalCalculated: Finish.");
            return sectionsTotalMap;
        } catch ( Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculateTotalGet.getCostSheetSectionsTotalCalculated: Exception ("+ e.getMessage() + ")");
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    /**
     * Calculate Total (CC) Supplier - Internal name: smTotal
     * @return smTotal = smCIFPrice * smConsumption * (1 + smLoss/100)
     */
    public static double getSMTotal_APD(FlexObject flexObject) {

        double smCIFPrice = getSMCIFPrice_APD(flexObject);
        double smConsumption = flexObject.getDouble("smConsumption");
        double smLoss = flexObject.getDouble("smLoss");

        return smCIFPrice * smConsumption * (1 + smLoss/100);
    }

    /**
     * Calculate Total estimated CE/BE, predictive, precosting - Internal name: smTotalSM
     * @return smTotalSM = (if(smCIFPriceFromSM != 0,smCIFPriceFromSM,smCIFPrice)) *
     *      (if(smConsumptionOVR != 0,smConsumptionOVR,smConsumption)) *
     *      (1+if(smLossOVR !=0,smLossOVR,smLoss)/100)
     */
    public static double getSMTotalSM_APD(FlexObject flexObject) {

        double smCIFPriceFromSM = getSMCIFPriceFromSM_APD(flexObject);
        double smCIFPrice = getSMCIFPrice_APD(flexObject);
        double smConsumptionOVR = flexObject.getDouble("smConsumptionOVR");
        double smConsumption = flexObject.getDouble("smConsumption");
        double smLossOVR = flexObject.getDouble("smLossOVR");
        double smLoss = flexObject.getDouble("smLoss");

        return (smCIFPriceFromSM != 0 ? smCIFPriceFromSM : smCIFPrice) *
                (smConsumptionOVR != 0 ? smConsumptionOVR : smConsumption) *
                (1 + (smLossOVR != 0 ? smLossOVR : smLoss) / 100);
    }

    /**
     * Calculate CIF Price - Internal name: smCIFPrice
     * @return smCIFPrice = if(smPriceOVR != 0, smPriceOVR,smPriceCC) * (1+smCIF/100)
     */
    public static double getSMCIFPrice_APD(FlexObject flexObject) {

        double smPriceOVR = flexObject.getDouble("smPriceOVR");
        double smPriceCC = flexObject.getDouble("smPriceCC") ;
        double smCIF = flexObject.getDouble("smCIF");

        return (smPriceOVR != 0 ? smPriceOVR : smPriceCC) * (1 + smCIF/100);
    }

    /**
     * Calculate  CIF Price from SM - Internal name: smCIFPriceFromSM
     * @return smCIFPriceFromSM = smPriceFromSM*(1+smCIFFromSM/100)
     */
    public static double getSMCIFPriceFromSM_APD(FlexObject flexObject) {

        double smPriceFromSM = flexObject.getDouble("smPriceFromSM");
        double smCIFFromSM = flexObject.getDouble("smCIFFromSM");

        return smPriceFromSM * (1 + smCIFFromSM / 100);
    }

    /**
     * Calculate of smTotal
     * @return smTotal = if(smPriceOVR != 0.0000, smPriceOVR*( smConsumption + smLoss/100), smPriceCC * ( smConsumption + smLoss/100))
     */
    public static double getSMTotal(FlexObject flexObject) {

        double result = 0;
        if (flexObject != null) {
            double smPriceOVR = flexObject.getDouble("smPriceOVR");
            double smConsumption = flexObject.getDouble("smConsumption");
            double smPriceCC = flexObject.getDouble("smPriceCC");
            double smLoss = flexObject.getDouble("smLoss");
            if (smPriceOVR != 0) {
                result = smPriceOVR * smConsumption*( 1 + smLoss / 100);
            } else {
                result = smPriceCC *  smConsumption * (1 + smLoss / 100);
            }
        }
        return result;
    }

    /**
     * if(smConsumptionOVR != 0.0000,
     *  if(smPriceOVR != 0.0000,
     *      smPriceOVR*smConsumptionOVR*(1 + smLoss/100),
     *      smPriceCC * smConsumptionOVR * (1 + smLoss/100)),
     *  if(smPriceOVR != 0.0000,
     *      smPriceOVR*smConsumption*(1 + smLoss/100),
     *      smPriceCC * smConsumption * (1 + smLoss/100)))
     * @param flexObject
     * @return
     */
    public static double getSMTotal_SEPD(FlexObject flexObject) {

        if (flexObject != null) {
            double smConsumptionOVR = flexObject.getDouble("smConsumptionOVR");
            double smPriceOVR = flexObject.getDouble("smPriceOVR");
            double smLoss = flexObject.getDouble("smLoss");
            double smPriceCC = flexObject.getDouble("smPriceCC");
            double smConsumption = flexObject.getDouble("smConsumption");

            if (smConsumptionOVR != 0) {
                if (smPriceOVR != 0) {
                    return smPriceOVR * smConsumptionOVR * (1 + smLoss / 100);
                } else {
                    return smPriceCC * smConsumptionOVR * (1 + smLoss / 100);
                }
            } else {
                if (smPriceOVR != 0) {
                    return smPriceOVR * smConsumption * (1 + smLoss / 100);
                } else {
                    return smPriceCC * smConsumption * (1 + smLoss / 100);
                }
            }
        }
        return 0;
    }

    public static double getBOMTotal_SEPD(LCSProductCostSheet lcsProductCostSheet) throws WTException {

        Collection bomTableRows = SMFormatHelper.getLCSMOATableRows( SMCostSheetTypeSelector.getSMCostSheetBOMTable( lcsProductCostSheet ) );
        Collection bomAdditionalTableRows = SMFormatHelper.getLCSMOATableRows( SMCostSheetTypeSelector.getSMCostSheetBOMAdditionalTable( lcsProductCostSheet ) );

        Map bomSectionTotalsTable = SMCostSheetBuildTable.buildBOMSectionTotalsTable(lcsProductCostSheet, bomTableRows, bomAdditionalTableRows, true);
        double smBOMTotal = getBOMTotal_SEPD(bomSectionTotalsTable);

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculateTotalGet.getBOMTotal_SEPD: smBOMTotal=" + smBOMTotal);

        return smBOMTotal;
    }

    /**
     * Calculate BOM Total (key = smBOMSectionTotal)
     */
    public static double getBOMTotal_SEPD(Map rows) {

        double smBOMTotal = 0D;
        for (Object object : rows.values()) {
            FlexObject row = (FlexObject) object;
            smBOMTotal += row.getDouble("smBOMSectionTotal");
        }

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculateTotalGet.getBOMTotal_SEPD: smBOMTotal=" + smBOMTotal);

        return smBOMTotal;
    }
}
