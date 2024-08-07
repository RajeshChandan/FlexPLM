package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.util.FormatHelper;
import com.ptc.core.meta.common.FloatingPoint;
import com.sportmaster.wc.mc.config.SMCostSheetConfig;
import com.sportmaster.wc.mc.config.SMCostSheetMOATableConfig;
import com.sportmaster.wc.mc.moa.SMMOAObjectReader;
import com.sportmaster.wc.mc.tools.SMFormatHelper;
import org.apache.log4j.Logger;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class SMCostSheetCalculate {

    private static final Logger LOGGER = Logger.getLogger(SMCostSheetCalculate.class);

    /**
     * Calculate and set attributes value of smProfitSupplier
     */
    public static void calculateSMProfitSupplier_APD(LCSProductCostSheet lcsProductCostSheet) {

        try {
            double smProfitSupplier = getSMProfitSupplier_APD( lcsProductCostSheet );
            lcsProductCostSheet.setValue("smProfitSupplier", new FloatingPoint( smProfitSupplier, 14 ));
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMProfitSupplier_APD: " +
                    "Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
    }

    /**
     * Profit % = (Profit (Supplier) / (BOM Total + CMT (Supplier) + Overhead (Supplier))) * 100
     * @return smProfitSupplier =
     *      (smProfitSupplierGSCurr / (smBOMTotalGSCurr + smCMTSupplierGSCurr + smOverheadSupplierGsCurr)) * 100
     */
    public static double getSMProfitSupplier_APD(LCSProductCostSheet lcsProductCostSheet) throws WTException {

        double smProfitSupplierGSCurr = SMFormatHelper.getDouble( lcsProductCostSheet.getValue("smProfitSupplierGSCurr") );
        double smBOMTotalGSCurr = SMFormatHelper.getDouble( lcsProductCostSheet.getValue("smBOMTotalGSCurr") );
        double smCMTSupplierGSCurr = SMFormatHelper.getDouble( lcsProductCostSheet.getValue("smCMTSupplierGSCurr") );
        double smOverheadSupplierGsCurr = SMFormatHelper.getDouble( lcsProductCostSheet.getValue("smOverheadSupplierGsCurr") );
        //double smMiscellaneousSupplierGSCurr = SMFormatHelper.getDouble( lcsProductCostSheet.getValue("smMiscellaneousSupplierGSCurr") );

        double value = smBOMTotalGSCurr + smCMTSupplierGSCurr + smOverheadSupplierGsCurr; // + smMiscellaneousSupplierGSCurr;
        if (value != 0)
            return (smProfitSupplierGSCurr / value ) * 100;
        return 0;
    }

    /**
     * Calculation of attributes by formulas (for APD):
     *  smQuotedPriceConverted = if smCsExchangeRate <> 0 , smQuotedPriceGSCurr / smCsExchangeRate, 0
     *  smOfflineManualPriceConverted =  if smCsExchangeRate <> 0 ,  smOfflineManualPrice / smCsExchangeRate, 0
     *  smBOMFabricsConverted = if smCsExchangeRate <> 0 ,  smBOMFabricsGSCurr / smCsExchangeRate, 0
     *  smBOMTrimsConverted = if smCsExchangeRate <> 0 ,  smBOMTrimsGSCurr / smCsExchangeRate, 0
     *  smBOMDecorationConverted = if smCsExchangeRate <> 0 ,  smBOMDecorationGSCurr / smCsExchangeRate, 0
     *  smBOMProductPackagingConverted = if smCsExchangeRate <> 0 ,  smBOMProductPackagingGSCurr / smCsExchangeRate, 0
     *  smBOMShippingPackingConverted = if smCsExchangeRate <> 0 ,  smBOMShippingAndPackingGSCurr / smCsExchangeRate, 0
     *  smBOMGarmentFinishConverted = if smCsExchangeRate <> 0 ,  smBOMGarmentFinishGSCurr / smCsExchangeRate, 0
     *  smBOMOtherConverted = if smCsExchangeRate <> 0 ,  smBOMOtherGSCurr / smCsExchangeRate, 0
     *  smBOMTotalConverted = if smCsExchangeRate <> 0 ,  smBOMTotalGSCurr / smCsExchangeRate, 0
     *  vrdCMTFOB = if smCsExchangeRate <> 0 ,  smCMTSupplierGSCurr / smCsExchangeRate, 0
     *  smOverheadSupplier = if smCsExchangeRate <> 0 ,  smOverheadSupplierGsCurr / smCsExchangeRate, 0
     *  smProfitSupplierAmount = if smCsExchangeRate <> 0 ,  smProfitSupplierGSCurr / smCsExchangeRate, 0
     *  smTransportationSupplier = if smCsExchangeRate <> 0 ,  smTransportationSupplierGSCurr / smCsExchangeRate, 0
     *  smMiscellaneousSupplier = if smCsExchangeRate <> 0 ,  smMiscellaneousSupplierGSCurr / smCsExchangeRate, 0
     */
    public static void calculateCostSheetAttributesInUSD_APD(LCSProductCostSheet lcsProductCostSheet) {

        try
        {
            float smCsExchangeRate = SMCostSheetCalculate.getSMCurrencyRate( lcsProductCostSheet );
            if (smCsExchangeRate != 0)
            {
                Map<String, String> mapped = SMFormatHelper.toMap( SMCostSheetConfig.SM_COST_SHEET_ATTRS_CONVERT_IN_USD_APD );
                for (Map.Entry<String,String> entry : mapped.entrySet()) {
                    String attrNameResult = entry.getKey();
                    String attrNameForConvert = entry.getValue();
                    double value = SMFormatHelper.getDouble( lcsProductCostSheet.getValue( attrNameForConvert ) );
                    lcsProductCostSheet.setValue( attrNameResult, new FloatingPoint( value / smCsExchangeRate, 14) );
                }
            }

        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculate.calculateCostSheetAttributesInUSD_APD: " +
                    "Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
    }

    /**
     * Calculate and persist attributes value of smTotalUSD and smTotalUSDSM
     * @param lcsmoaObject - row in BOM Table on CostSheet
     * @param smCurrencyRate - currency rate on CostSheet
     */
    public static void persistSMTotalUSD_SMTotalUSDSM_APD(LCSMOAObject lcsmoaObject, float smCurrencyRate) {

        try {
            double smTotalUSD = getSMTotalUSD_APD(lcsmoaObject, smCurrencyRate);
            double smTotalUSDSM = getSMTotalUSDSM_APD(lcsmoaObject, smCurrencyRate);
            lcsmoaObject.setValue("smTotalUSD", new FloatingPoint( smTotalUSD, 14 ));
            lcsmoaObject.setValue("smTotalUSDSM", new FloatingPoint( smTotalUSDSM, 14 ));
            LCSMOAObjectLogic.persist(lcsmoaObject, false);
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculate.persistSMTotalUSD_SMTotalUSDSM_APD: " +
                    "Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
    }

    /**
     * Calculate and set attributes value of smTotalUSD and smTotalUSDSM
     * @param lcsmoaObject - row in BOM Table on CostSheet
     * @param smCurrencyRate - currency rate on CostSheet
     */
    public static void calculateSMTotalUSD_SMTotalUSDSM_APD(LCSMOAObject lcsmoaObject, float smCurrencyRate) {

        try {
            double smTotalUSD = getSMTotalUSD_APD(lcsmoaObject, smCurrencyRate);
            double smTotalUSDSM = getSMTotalUSDSM_APD(lcsmoaObject, smCurrencyRate);
            lcsmoaObject.setValue("smTotalUSD", new FloatingPoint( smTotalUSD, 14 ));
            lcsmoaObject.setValue("smTotalUSDSM", new FloatingPoint( smTotalUSDSM, 14 ));
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMTotalUSD_SMTotalUSDSM_APD: " +
                    "Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
    }

    /**
     * Total USD (SM) = if (Currency rate to 1 USD != 0 , Total (SM) / Currency rate to 1 USD), where Currency Value = Contract Currency
     * @return smTotalUSDSM = if (smCurrencyRate != 0.0000 , smTotalSM / smCurrencyRate , 0.0000), where smCurrencyValue == smCsContractCurrency
     */
    public static double getSMTotalUSDSM_APD(LCSMOAObject lcsmoaObject, float smCurrencyRate) throws WTException {

        double smTotalSM = SMFormatHelper.getDouble( lcsmoaObject.getValue("smTotalSM") );
        if (smCurrencyRate != 0)
            return smTotalSM / smCurrencyRate;
        return 0;
    }

    /**
     * Total USD = if (Currency rate to 1 USD != 0 , Total (CC) / Currency rate to 1 USD , 0), where Currency Value = Contract Currency
     * @return smTotalUSD = if (smCurrencyRate != 0.0000 , smTotal / smCurrencyRate , 0.0000), where smCurrencyValue == smCsContractCurrency
     */
    public static double getSMTotalUSD_APD(LCSMOAObject lcsmoaObject, float smCurrencyRate) throws WTException {

        double smTotal = SMFormatHelper.getDouble( lcsmoaObject.getValue("smTotal") );
        if (smCurrencyRate != 0)
            return smTotal / smCurrencyRate;
        return 0;
    }

    public static double calculateSMCIFPriceFromSM_APD(LCSMOAObject lcsmoaObject) {
        try {
            double smCIFPriceFromSM = getSMCIFPriceFromSM_APD(lcsmoaObject);
            lcsmoaObject.setValue("smCIFPriceFromSM", new FloatingPoint( smCIFPriceFromSM, 14 ));
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMCIFPriceFromSM_APD: " +
                    "Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Calculate  CIF Price from SM - Internal name: smCIFPriceFromSM
     * @return smCIFPriceFromSM = smPriceFromSM*(1+smCIFFromSM/100)
     */
    public static double getSMCIFPriceFromSM_APD(LCSMOAObject lcsmoaObject) throws WTException {

        double smPriceFromSM = SMFormatHelper.getDouble( lcsmoaObject.getValue("smPriceFromSM") );
        double smCIFFromSM = SMFormatHelper.getDouble( lcsmoaObject.getValue("smCIFFromSM") );

        return smPriceFromSM * (1+smCIFFromSM/100);
    }

    public static double calculateSMTotal_APD(LCSMOAObject lcsmoaObject) {
        try {
            double smTotal = getSMTotal_APD(lcsmoaObject);
            lcsmoaObject.setValue("smTotal", new FloatingPoint( smTotal, 14 ));
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMTotal_APD: " +
                    "Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Calculate Total (CC) Supplier - Internal name: smTotal
     * @return smTotal = smCIFPrice * smConsumption * (1 + smLoss/100)
     */
    public static double getSMTotal_APD(LCSMOAObject lcsmoaObject) throws WTException {

        double smCIFPrice = SMFormatHelper.getDouble( lcsmoaObject.getValue("smCIFPrice") );
        double smConsumption = SMFormatHelper.getDouble( lcsmoaObject.getValue("smConsumption") );
        double smLoss = SMFormatHelper.getDouble( lcsmoaObject.getValue("smLoss") );

        return smCIFPrice * smConsumption * (1 + smLoss/100);
    }

    public static double calculateSMCIFPrice_APD(LCSMOAObject lcsmoaObject) {
        try {
            double smCIFPrice = getSMCIFPrice_APD(lcsmoaObject);
            lcsmoaObject.setValue("smCIFPrice", new FloatingPoint( smCIFPrice, 14 ));
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMCIFPrice_APD: " +
                    "Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Calculate CIF Price - Internal name: smCIFPrice
     * @return smCIFPrice = if(smPriceOVR != 0, smPriceOVR,smPriceCC) * (1+smCIF/100)
     */
    public static double getSMCIFPrice_APD(LCSMOAObject lcsmoaObject) throws WTException {

        double smPriceOVR = SMFormatHelper.getDouble( lcsmoaObject.getValue("smPriceOVR") );
        double smPriceCC = SMFormatHelper.getDouble( lcsmoaObject.getValue("smPriceCC") );
        double smCIF = SMFormatHelper.getDouble( lcsmoaObject.getValue("smCIF") );

        return (smPriceOVR != 0 ? smPriceOVR : smPriceCC) * (1 + smCIF/100);
    }

    public static double calculateSMPercentageOfPurchasePrice_SEPD(
            LCSProductCostSheet lcsProductCostSheet, LCSMOAObject moaObject, LCSMOATable bomTable, LCSMOATable bomAdditionalTable)
            throws WTException, WTPropertyVetoException {

        Collection bomTableRows = SMFormatHelper.getLCSMOATableRows( bomTable );
        Collection bomAdditionalTableRows = SMFormatHelper.getLCSMOATableRows( bomAdditionalTable );

        Map bomSectionTotalsTable = SMCostSheetBuildTable.buildBOMSectionTotalsTable(lcsProductCostSheet, bomTableRows, bomAdditionalTableRows, true);
        double smTotal = SMCostSheetCalculateTotalGet.getBOMTotal_SEPD(bomSectionTotalsTable);
        double smPercentageOfPurchasePrice = SMCostSheetCalculate.getSMPercentageOfPurchasePrice_SEPD( lcsProductCostSheet, smTotal );
        moaObject.setValue("smPercentageOfPurchasePrice", new FloatingPoint( smPercentageOfPurchasePrice, 14 ));

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMPercentageOfPurchasePrice_SEPD: smPercentageOfPurchasePrice=" +
                smPercentageOfPurchasePrice + ", smTotal=" + smTotal);

        return smPercentageOfPurchasePrice;
    }

    /**
     * % of PP = if (Purchase Price (Calc.) MC != 0 , Total (CC) / Purchase Price (Calc.) MC , 0)
     * @return smPercentageOfPurchasePrice =
     *         if (smPurchasePriceCalcContractCur != 0.0000 , smTotal / smPurchasePriceCalcContractCur , 0.00)
     */
    public static double getSMPercentageOfPurchasePrice_SEPD(LCSProductCostSheet lcsProductCostSheet, double smTotal) throws WTException {

        double smPurchasePriceCalcContractCur = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smPurchasePriceCalcContractCur"));
        if (smPurchasePriceCalcContractCur != 0D)
            return smTotal / smPurchasePriceCalcContractCur;

        return 0D;
    }

    public static void calculateFormulasForFPD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {

        SMCostSheetCalculate.calculateSMQuotedPriceGSCurr(lcsProductCostSheet); // Calculate smQuotedPriceGSCurr
        SMCostSheetCalculate.calculateSMProfitPercentSupplier(lcsProductCostSheet); // Calculate smProfitPercentSupplier
        SMCostSheetCalculate.calculateSMSourcingCommissionTradingCompanyPercent(lcsProductCostSheet); // Calculate smSourcingCommissionTradingcompanyPercent
        SMCostSheetCalculateTotalSet.calculateSMNMCTotal(lcsProductCostSheet); //smNMCTotal
        SMCostSheetCalculate.calculateVRDFOBCalc(lcsProductCostSheet); //vrdFOBCalc
    }

    public static void calculateFormulasForSEPD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {

        SMCostSheetCalculateTotalSet.calculateCostSheetSectionsTotal_SEPD( lcsProductCostSheet );
        SMCostSheetCalculateTotalSet.calculateNMCTotal_SEPD( lcsProductCostSheet );
        SMCostSheetCalculateTotalSet.calculateBOMTotal_SEPD( lcsProductCostSheet );
        SMCostSheetCalculate.calculateSMPurchasePriceCalcContractCur_SEPD( lcsProductCostSheet );
        SMCostSheetCalculate.calculateSMPurchasePriceInclVAT_SEPD( lcsProductCostSheet );
        SMCostSheetCalculate.calculateSMBOMTotalPercentOfPurchasePriceCalc_SEPD( lcsProductCostSheet );
        SMCostSheetCalculate.calculateSMNMCTotalPercentOfPurchasePriceCalc_SEPD( lcsProductCostSheet );
        SMCostSheetCalculate.calculateSMLaborPercent_SEPD( lcsProductCostSheet );
        SMCostSheetCalculate.calculateSMOverheadPercent_SEPD( lcsProductCostSheet );
        SMCostSheetCalculate.calculateSMProfitSupplier_SEPD( lcsProductCostSheet );
        SMCostSheetCalculate.calculateSMTransportationSupplierPercent_SEPD( lcsProductCostSheet );
    }

    /**
     * Purchase Price (Calc.) incl VAT = Purchase Price (Calc.) * (100 + VAT Rate % ) / 100
     * @return smPurchasePriceInclVAT =
     *         smPurchasePriceCalcContractCur * (100 + smVATRatePercent) / 100
     */
    public static double calculateSMPurchasePriceInclVAT_SEPD(LCSProductCostSheet lcsProductCostSheet) {
        try {
            double smPurchasePriceCalcContractCur = SMFormatHelper.getDouble( lcsProductCostSheet.getValue("smPurchasePriceCalcContractCur") );
            long smVATRatePercent = SMFormatHelper.getLong( lcsProductCostSheet.getValue("smVATRatePercent") );
            double smPurchasePriceInclVAT = smPurchasePriceCalcContractCur * (100 + smVATRatePercent) / 100;
            lcsProductCostSheet.setValue("smPurchasePriceInclVAT", new FloatingPoint( smPurchasePriceInclVAT, 14 ));
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMPurchasePriceInclVAT_SEPD: " +
                    "Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Transportation, Local Logistics % = if (Purchase Price (Calc.) = 0 , 0, ( Transportation, Local Logistics - ONLY FOR DDP Incoterms / Purchase Price (Calc.) ) *100 )
     * @return smTransportationSupplierPercent =
     *         if (smPurchasePriceCalcContractCur = 0, 0, ( smTransportationSupplier / smPurchasePriceCalcContractCur ) * 100 )
     */
    public static double calculateSMTransportationSupplierPercent_SEPD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {

        double smTransportationSupplierPercent;

        double smTransportationSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smTransportationSupplier"));
        double smPurchasePriceCalcContractCur = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smPurchasePriceCalcContractCur"));
        if (smPurchasePriceCalcContractCur == 0D)
            smTransportationSupplierPercent = 0D;
        else smTransportationSupplierPercent = ( smTransportationSupplier / smPurchasePriceCalcContractCur ) * 100;

        lcsProductCostSheet.setValue( "smTransportationSupplierPercent", new FloatingPoint( smTransportationSupplierPercent, 14) );

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMTransportationSupplierPercent_SEPD: smTransportationSupplierPercent=" + smTransportationSupplierPercent );

        return smTransportationSupplierPercent;
    }

    /**
     * Profit % = if (Purchase Price (Calc.) = 0 , 0, ( Profit (Supplier) / Purchase Price (Calc.) ) *100 )
     * @return smProfitSupplier =
     *         if (smPurchasePriceCalcContractCur = 0, 0, ( smProfitSupplierAmount / smPurchasePriceCalcContractCur ) * 100 )
     */
    public static double calculateSMProfitSupplier_SEPD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {

        double smProfitSupplier;

        double smProfitSupplierAmount = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smProfitSupplierAmount"));
        double smPurchasePriceCalcContractCur = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smPurchasePriceCalcContractCur"));
        if (smPurchasePriceCalcContractCur == 0D)
            smProfitSupplier = 0D;
        else smProfitSupplier = ( smProfitSupplierAmount / smPurchasePriceCalcContractCur ) * 100;

        lcsProductCostSheet.setValue( "smProfitSupplier", new FloatingPoint( smProfitSupplier, 14) );

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMProfitSupplier_SEPD: smProfitSupplier=" + smProfitSupplier );

        return smProfitSupplier;
    }

    /**
     * Overhead % = if (Purchase Price (Calc.) = 0 , 0, ( Overhead (Supplier) / Purchase Price (Calc.) ) *100 )
     * @return smOverheadPercent =
     *         if (smPurchasePriceCalcContractCur = 0, 0, ( smOverheadSupplier / smPurchasePriceCalcContractCur ) * 100 )
     */
    public static double calculateSMOverheadPercent_SEPD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {

        double smOverheadPercent;

        double smOverheadSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smOverheadSupplier"));
        double smPurchasePriceCalcContractCur = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smPurchasePriceCalcContractCur"));
        if (smPurchasePriceCalcContractCur == 0D)
            smOverheadPercent = 0D;
        else smOverheadPercent = ( smOverheadSupplier / smPurchasePriceCalcContractCur ) * 100;

        lcsProductCostSheet.setValue( "smOverheadPercent", new FloatingPoint( smOverheadPercent, 14) );

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMOverheadPercent_SEPD: smOverheadPercent=" + smOverheadPercent );

        return smOverheadPercent;
    }

    /**
     * Labor % = if (Purchase Price (Calc.) = 0 , 0, ( Labor (Supplier) / Purchase Price (Calc.) ) *100 )
     * @return smLaborPercent =
     *         if (smPurchasePriceCalcContractCur = 0, 0, ( smLaborSupplier / smPurchasePriceCalcContractCur ) * 100 )
     */
    public static double calculateSMLaborPercent_SEPD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {

        double smLaborPercent;

        double smLaborSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smLaborSupplier"));
        double smPurchasePriceCalcContractCur = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smPurchasePriceCalcContractCur"));
        if (smPurchasePriceCalcContractCur == 0D)
            smLaborPercent = 0D;
        else smLaborPercent = ( smLaborSupplier / smPurchasePriceCalcContractCur ) * 100;

        lcsProductCostSheet.setValue( "smLaborPercent", new FloatingPoint( smLaborPercent, 14) );

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMLaborPercent_SEPD: smLaborPercent=" + smLaborPercent);

        return smLaborPercent;
    }

    /**
     * NMC_Total % of Purchase price (Calc.) = if (Purchase Price (Calc.) = 0 , 0, ( NMC Total / Purchase Price (Calc.) ) *100 )
     * @return smNMCTotalPercentOfPurchasePriceCalc =
     *         if (smPurchasePriceCalcContractCur = 0, 0, ( smNMCTotal / smPurchasePriceCalcContractCur ) * 100 )
     */
    public static double calculateSMNMCTotalPercentOfPurchasePriceCalc_SEPD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {

        double smNMCTotalPercentOfPurchasePriceCalc;

        double smNMCTotal = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smNMCTotal"));
        double smPurchasePriceCalcContractCur = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smPurchasePriceCalcContractCur"));
        if (smPurchasePriceCalcContractCur == 0D)
            smNMCTotalPercentOfPurchasePriceCalc = 0D;
        else smNMCTotalPercentOfPurchasePriceCalc = ( smNMCTotal / smPurchasePriceCalcContractCur ) * 100;

        lcsProductCostSheet.setValue( "smNMCTotalPercentOfPurchasePriceCalc", new FloatingPoint( smNMCTotalPercentOfPurchasePriceCalc, 14) );

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMNMCTotalPercentOfPurchasePriceCalc_SEPD: smNMCTotalPercentOfPurchasePriceCalc=" + smNMCTotalPercentOfPurchasePriceCalc);

        return smNMCTotalPercentOfPurchasePriceCalc;
    }

    /**
     * BOM_Total % of Purchase price (Calc.) = if (Purchase Price (Calc.) = 0, 0, ( BOM Total / Purchase Price (Calc.) ) *100 )
     * @return smBOMTotalPercentOfPurchasePriceCalc =
     *         if (smPurchasePriceCalcContractCur = 0, 0, ( smBOMTotal / smPurchasePriceCalcContractCur ) * 100 )
     */
    public static double calculateSMBOMTotalPercentOfPurchasePriceCalc_SEPD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {

        double smBOMTotalPercentOfPurchasePriceCalc;

        double smBOMTotal = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smBOMTotal"));
        double smPurchasePriceCalcContractCur = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smPurchasePriceCalcContractCur"));
        if (smPurchasePriceCalcContractCur == 0D)
            smBOMTotalPercentOfPurchasePriceCalc = 0D;
        else smBOMTotalPercentOfPurchasePriceCalc = ( smBOMTotal / smPurchasePriceCalcContractCur ) * 100;

        lcsProductCostSheet.setValue( "smBOMTotalPercentOfPurchasePriceCalc", new FloatingPoint( smBOMTotalPercentOfPurchasePriceCalc, 14) );

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMBOMTotalPercentOfPurchasePriceCalc_SEPD: smBOMTotalPercentOfPurchasePriceCalc=" + smBOMTotalPercentOfPurchasePriceCalc);

        return smBOMTotalPercentOfPurchasePriceCalc;
    }

    /**
     * Purchase Price (Calc.) = if (BOM Total = 0 AND NMC Total = 0, Manual Purchase Price (Offline), BOM Total + NMC Total)
     * @return smPurchasePriceCalcContractCur =
     *         if (smBOMTotal = 0 AND smNMCTotal = 0, smBOMTotalOffline, smBOMTotal + smNMCTotal)
     */
    public static double calculateSMPurchasePriceCalcContractCur_SEPD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {

        double smBOMTotal = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smBOMTotal"));
        double smNMCTotal = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smNMCTotal"));

        double smPurchasePriceCalcContractCur = getSMPurchasePriceCalcContractCur_SEPD(lcsProductCostSheet, smBOMTotal, smNMCTotal);

        lcsProductCostSheet.setValue( "smPurchasePriceCalcContractCur", new FloatingPoint( smPurchasePriceCalcContractCur, 14) );

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMPurchasePriceCalcContractCur_SEPD: smPurchasePriceCalcContractCur=" + smPurchasePriceCalcContractCur);

        return smPurchasePriceCalcContractCur;
    }

    public static double getSMPurchasePriceCalcContractCur_SEPD(LCSProductCostSheet lcsProductCostSheet, double smBOMTotal, double smNMCTotal) throws WTException {

        double smPurchasePriceCalcContractCur = SMFormatHelper.getDouble( lcsProductCostSheet.getValue("smBOMTotalOffline") );

        if (smPurchasePriceCalcContractCur == 0) {
            smPurchasePriceCalcContractCur = smBOMTotal + smNMCTotal;
        }

        return smPurchasePriceCalcContractCur;
    }

    public static double getSMBOMSectionPercentOfPP_SEPD(LCSProductCostSheet lcsProductCostSheet, double smBOMSectionTotal) throws WTException {

        double smPurchasePriceCalcContractCur = SMFormatHelper.getDouble( lcsProductCostSheet.getValue("smPurchasePriceCalcContractCur") );

        if (smPurchasePriceCalcContractCur != 0) {
            return ( smBOMSectionTotal / smPurchasePriceCalcContractCur ) * 100;
        }
        return 0;
    }

    public static double getSMTotalBOMTable_SEPD(double smPriceCC, double smPriceOVR, double smConsumption, double smLoss) {
        /* =
        Price OVR  (Contract Currency) * Consumption (1+Loss/100),
        а если Price OVR  (Contract Currency) пустая - то = (Nominated Price*Contract currency) * Consumption (1+Loss/100)

        if(smPriceOVR != 0.0000, smPriceOVR*smConsumption*(1 + smLoss/100), smPriceCC * smConsumption * (1 + smLoss/100))
        */
        if(smPriceOVR != 0) return smPriceOVR * smConsumption * (1 + smLoss/100);
        return smPriceCC * smConsumption * (1 + smLoss/100);
    }

    public static double getSMPurchasePriceCalc_SEPD(LCSProductCostSheet lcsProductCostSheet, double totalContractCurrency) throws WTException {
        // = Total (Contract currency) / Purchase Price (Calc.)
        double smPurchasePriceCalc = SMFormatHelper.getDouble( lcsProductCostSheet.getValue("smPurchasePriceCalcContractCur") ); //smPurchasePriceCalcContractCur
        if (smPurchasePriceCalc != 0) {
            return totalContractCurrency / smPurchasePriceCalc;
        }
        return 0;
    }

    public static void calculateVRDFOBCalc(LCSProductCostSheet lcsProductCostSheet) {
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateVRDFOBCalc: Start.");
        /*
         * If ("BOM Total">0) then ("BOM Total" + "Labor (Supplier)" + "Overhead(Supplier") + "Profit (Supplier)+ "New Last cost" + "Die cut cost")"
         * else ("Upper (Supplier)" + "Sole (Supplier)" +"Labor (Supplier)" + "Overhead(Supplier") + "Profit (Supplier)" + "New Last cost" + "Die cut cost")
         *
         * if(smBOMTotalTable> 0.0, smBOMTotalTable + vrdCMTFOB + smOverheadSupplier + smProfitSupplier + smNewLastCost + smDieCutCost,
         * vrdUppersFOB + smSoleSupplier + vrdCMTFOB + smOverheadSupplier + smProfitSupplier + smNewLastCost + smDieCutCost)
         */
        try {
            double smBOMTotalTable = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smBOMTotalTable"));
            double vrdCMTFOB = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("vrdCMTFOB"));
            double smOverheadSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smOverheadSupplier"));
            double smProfitSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smProfitSupplier"));
            double smNewLastCost = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smNewLastCost"));
            double smDieCutCost = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smDieCutCost"));
            double vrdUppersFOB = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("vrdUppersFOB"));
            double smSoleSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smSoleSupplier"));

            double result;
            if (smBOMTotalTable > 0) {
                result = smBOMTotalTable + vrdCMTFOB + smOverheadSupplier + smProfitSupplier + smNewLastCost + smDieCutCost;
            } else {
                result = vrdUppersFOB + smSoleSupplier + vrdCMTFOB + smOverheadSupplier + smProfitSupplier + smNewLastCost + smDieCutCost;
            }

            lcsProductCostSheet.setValue("vrdFOBCalc", new FloatingPoint(result, 14));

            LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateVRDFOBCalc: vrdFOBCalc=" + result + ". Finish.");
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculate.calculateVRDFOBCalc: Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
    }

    public static void calculateSMQuotedPriceGSCurr(LCSProductCostSheet lcsProductCostSheet) {
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMQuotedPriceGSCurr: Start.");

        try {
            double smBOMTotalTable = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smBOMTotalTable"));
            double vrdCMTFOB = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("vrdCMTFOB"));
            double smOverheadSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smOverheadSupplier"));
            double smProfitSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smProfitSupplier"));
            double smVATSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smVATSupplier"));
            double smVATTaxRebateSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smVATTaxRebateSupplier"));
            double smSourcingCommissionSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smSourcingCommissionSupplier"));
            double smTransportationSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smTransportationSupplier"));
            double smNewLastCost = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smNewLastCost"));
            double smDieCutCost = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smDieCutCost"));
            double vrdUppersFOB = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("vrdUppersFOB"));
            double smSoleSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smSoleSupplier"));

            double result;
            if (smBOMTotalTable > 0) {
                result = smBOMTotalTable + vrdCMTFOB + smOverheadSupplier + smProfitSupplier + smVATSupplier - smVATTaxRebateSupplier +
                        smSourcingCommissionSupplier + smTransportationSupplier + smNewLastCost + smDieCutCost;
            } else {
                result = vrdUppersFOB + smSoleSupplier + vrdCMTFOB + smOverheadSupplier + smProfitSupplier +
                        (smVATTaxRebateSupplier != 0 ? smVATSupplier - smVATTaxRebateSupplier : 0) +
                        smSourcingCommissionSupplier + smTransportationSupplier + smNewLastCost + smDieCutCost;
            }

            lcsProductCostSheet.setValue("smQuotedPriceGSCurr", new FloatingPoint(result, 14));

            LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMQuotedPriceGSCurr: smQuotedPriceGSCurr=" + result + ". Finish.");
        }
        catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMQuotedPriceGSCurr: Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
    }

    public static void calculateSMProfitPercentSupplier(LCSProductCostSheet lcsProductCostSheet) {
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMProfitPercentSupplier: Start.");

        try {
            double smBOMTotalTable = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smBOMTotalTable"));
            double vrdCMTFOB = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("vrdCMTFOB"));
            double smOverheadSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smOverheadSupplier"));
            double smProfitSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smProfitSupplier"));
            double smNewLastCost = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smNewLastCost"));
            double smDieCutCost = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smDieCutCost"));
            double vrdUppersFOB = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("vrdUppersFOB"));
            double smSoleSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smSoleSupplier"));

            double result = 0;
            if (smProfitSupplier != 0) {
                double sum = 0;
                if (smBOMTotalTable > 0) {
                    sum = smBOMTotalTable + vrdCMTFOB + smOverheadSupplier + smNewLastCost + smDieCutCost;
                } else if (vrdUppersFOB > 0 || smSoleSupplier > 0 || vrdCMTFOB > 0 || smOverheadSupplier > 0 || smNewLastCost > 0 || smDieCutCost > 0) {
                    sum = vrdUppersFOB + smSoleSupplier + vrdCMTFOB + smOverheadSupplier + smNewLastCost + smDieCutCost;
                }
                if (sum != 0) result = smProfitSupplier * 100 / sum;
            }
            lcsProductCostSheet.setValue("smProfitPercentSupplier", new FloatingPoint(result, 14));

            LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMProfitPercentSupplier: smProfitPercentSupplier=" + result + ". Finish.");
        }
        catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMProfitPercentSupplier: Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
    }

    public static void calculateSMSourcingCommissionTradingCompanyPercent(LCSProductCostSheet lcsProductCostSheet) {
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMSourcingCommissionTradingCompanyPercent: Start.");

        try {
            double smBOMTotalTable = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smBOMTotalTable"));
            double vrdCMTFOB = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("vrdCMTFOB"));
            double smOverheadSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smOverheadSupplier"));
            double smProfitSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smProfitSupplier"));
            double smVATSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smVATSupplier"));
            double smVATTaxRebateSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smVATTaxRebateSupplier"));
            double smSourcingCommissionSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smSourcingCommissionSupplier"));
            double smTransportationSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smTransportationSupplier"));
            double smNewLastCost = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smNewLastCost"));
            double smDieCutCost = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smDieCutCost"));
            double vrdUppersFOB = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("vrdUppersFOB"));
            double smSoleSupplier = SMFormatHelper.getDouble(lcsProductCostSheet.getValue("smSoleSupplier"));

            double result = 0;
            double sum = 0;
            if(smBOMTotalTable > 0) {
                sum = smBOMTotalTable + vrdCMTFOB + smOverheadSupplier + smProfitSupplier + smVATSupplier - smVATTaxRebateSupplier + smNewLastCost + smDieCutCost;
            }
            else if(vrdUppersFOB > 0 || smSoleSupplier > 0 || vrdCMTFOB > 0 || smOverheadSupplier > 0 || smProfitSupplier > 0 || smVATTaxRebateSupplier > 0 ||
                    smSourcingCommissionSupplier > 0 || smTransportationSupplier > 0 || smNewLastCost > 0 || smDieCutCost > 0 ) {
                sum = vrdUppersFOB + smSoleSupplier + vrdCMTFOB + smOverheadSupplier + smProfitSupplier +
                        (smVATTaxRebateSupplier != 0 ? smVATSupplier - smVATTaxRebateSupplier : 0) +
                        smSourcingCommissionSupplier + smTransportationSupplier + smNewLastCost + smDieCutCost;
            }
            if (sum != 0) result = (smSourcingCommissionSupplier * 100) / sum; // added multiply to 100 to correct percentage display


            lcsProductCostSheet.setValue("smSourcingCommissionTradingcompanyPercent", new FloatingPoint(result, 14));

            LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMSourcingCommissionTradingCompanyPercent: smSourcingCommissionTradingcompanyPercent=" + result + ". Finish.");
        }
        catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMSourcingCommissionTradingCompanyPercent: Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
    }

    /**
     * Calculate smPriceCC (for all department)
     */
    public static void calculateSMPriceCC(LCSProductCostSheet lcsProductCostSheet, String costSheetBOMTableName, Collection<FlexObject> bomTableRows) {

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMPriceCC: Start.");
        try {
            Hashtable rowData = new Hashtable();
            for (FlexObject row : bomTableRows) {
                double smMSPrice = row.getDouble(SMCostSheetMOATableConfig.MOA_TABLE_SM_MATERIAL_PRICE_LC);
                String smMSCurrency = FormatHelper.format(row.getData(SMCostSheetMOATableConfig.MOA_TABLE_SM_CURRENCY_UNIT));
                double smPriceCC = getSMPriceCC(lcsProductCostSheet, smMSPrice, smMSCurrency);
                row.put("smPriceCC", smPriceCC);
                String id = FormatHelper.format( row.getData("ID") );
                rowData.put(id, row);
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMPriceCC: smMSPrice: " + smMSPrice + ", smMSCurrency: " + smMSCurrency + ", smPriceCC: " + smPriceCC);
            }

            SMCostSheetUpdate.newSMCostSheetUpdateForBOMTable(lcsProductCostSheet).copyIDsAndHiddenAttrsForBOMTable(
                    "BRANCHID", "ID", rowData.values());

            LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
            moaLogic.updateMOAObjectCollection(lcsProductCostSheet, lcsProductCostSheet.getFlexType().getAttribute(costSheetBOMTableName), rowData);

        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMPriceCC: Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMPriceCC: Finish.");
    }

    /**
     * Calculate smPriceCC (for APD)
     */
    public static void calculateSMPriceCC_APD(LCSProductCostSheet lcsProductCostSheet) {

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMPriceCC: Start.");
        try {
            String costSheetBOMTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcsProductCostSheet);
            LCSMOATable moaTable = (LCSMOATable) lcsProductCostSheet.getValue(costSheetBOMTableName);
            if (moaTable != null)
            {
                //Ignore NMC sections
                List<String> rowsIgnore = SMCostSheetConfig.getValues( SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_APD );
                rowsIgnore.addAll( SMCostSheetConfig.getValues( SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_CS_INCOTERMS_DDP_APD ) );

                Collection<FlexObject> bomTableRows = SMCostSheetFilter.filterExclude( moaTable.getRows(), "smSectionAPD", rowsIgnore );
                //SMPrintHelper.print("calculateSMPriceCC", bomTableRows, "smSectionAPD"); //Print info
                calculateSMPriceCC( lcsProductCostSheet, costSheetBOMTableName, bomTableRows );
            }
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMPriceCC: Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMPriceCC: Finish.");
    }

    public static void calculateSMPriceCC_FPD(LCSProductCostSheet lcsProductCostSheet) {

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMPriceCC_FPD: Start.");
        try {
            String costSheetBOMTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcsProductCostSheet);
            LCSMOATable moaTable = (LCSMOATable) lcsProductCostSheet.getValue(costSheetBOMTableName);
            if (moaTable != null) {
                Hashtable rowData = new Hashtable();
                for (Object object : moaTable.getRows()) {
                    FlexObject row = (FlexObject) object;
                    String section = FormatHelper.format( row.getData("smSection") );
                    if ( !section.equals("NMC") ) {
                        double smMSPrice = row.getDouble(SMCostSheetMOATableConfig.MOA_TABLE_SM_MATERIAL_PRICE_LC);
                        String smMSCurrency = FormatHelper.format(row.getData(SMCostSheetMOATableConfig.MOA_TABLE_SM_CURRENCY_UNIT));
                        double smPriceCC = getSMPriceCC(lcsProductCostSheet, smMSPrice, smMSCurrency);
                        row.put("smPriceCC", smPriceCC);
                    }
                    String id = FormatHelper.format( row.getData("ID") );
                    rowData.put(id, row);
                }

                SMCostSheetUpdate.newSMCostSheetUpdateForBOMTable(lcsProductCostSheet).copyIDsAndHiddenAttrsForBOMTable(
                        "BRANCHID", "ID", rowData.values());

                LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
                moaLogic.updateMOAObjectCollection(lcsProductCostSheet, lcsProductCostSheet.getFlexType().getAttribute(costSheetBOMTableName), rowData);
            }
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMPriceCC_FPD: Exception ("+ e.getMessage() + ")", e);
            e.printStackTrace();
        }
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.calculateSMPriceCC_FPD: Finish.");
    }

    /**
     * Get currency rate for attribute value of smCsContractCurrency on the LCSProductCostSheet
     */
    public static float getSMCurrencyRate(LCSProductCostSheet lcsProductCostSheet) throws WTException {

        String smCsContractCurrency = FormatHelper.format( (String) lcsProductCostSheet.getValue("smCsContractCurrency") );
        return getSMCurrencyRate(lcsProductCostSheet, smCsContractCurrency);
    }

    /**
     *  Get currency rate for attribute value of smCsContractCurrency on the LCSProductCostSheet
     */
    public static float getSMCurrencyRate(LCSProductCostSheet lcsProductCostSheet, String smCsContractCurrency) throws WTException {

        float smCurrencyRate = SMMOAObjectReader.getCurrencyExchangeRate( lcsProductCostSheet, smCsContractCurrency );
        if (smCurrencyRate == -1 && smCsContractCurrency.equals("vrdUsd"))
            smCurrencyRate = 1f;

        return smCurrencyRate;
    }

    public static double getSMPriceCC(LCSProductCostSheet lcsProductCostSheet, double smMSPrice, String smMSCurrency) throws WTException {
        double smPriceCC = 0D;

        if(smMSPrice > 0D)
        {
            String smCsContractCurrency = (String) lcsProductCostSheet.getValue("smCsContractCurrency");
            if(smCsContractCurrency == null) smCsContractCurrency = "";
            if(smMSCurrency == null) smMSCurrency = "";

            LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.getSMPriceCC: smMSCurrency = " + smMSCurrency + ", smCsContractCurrency = " + smCsContractCurrency);
            if(smMSCurrency.equals(smCsContractCurrency)) {
                smPriceCC = smMSPrice;
            }
            else {
                float smCsContractCurrencyRate = SMMOAObjectReader.getCurrencyExchangeRate(lcsProductCostSheet, smCsContractCurrency);
                if(smCsContractCurrencyRate == -1 && smCsContractCurrency.equals("vrdUsd"))
                    smCsContractCurrencyRate = 1f;
                float smMSCurrencyRate = SMMOAObjectReader.getCurrencyExchangeRate(lcsProductCostSheet, smMSCurrency);
                if(smMSCurrencyRate == -1 && smMSCurrency.equals("vrdUsd"))
                    smMSCurrencyRate = 1f;
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.getSMPriceCC: smMSCurrencyRate = " + smMSCurrencyRate + ", smCsContractCurrencyRate = " + smCsContractCurrencyRate);
                if(smMSCurrencyRate > 0 && smCsContractCurrencyRate > 0) {
                    smPriceCC = smMSPrice * (smCsContractCurrencyRate/smMSCurrencyRate);
                    LOGGER.debug("CUSTOM>>>>>> SMCostSheetCalculate.getSMPriceCC: smPriceCC = " + smPriceCC
                            + ", formula: smMSPrice * (smCsContractCurrencyRate/smMSCurrencyRate) : "
                            + smMSPrice + " * (" + smCsContractCurrencyRate + " / " + smMSCurrencyRate + ")");
                }
            }
        }
        return smPriceCC;
    }

    public static double getNominatedPriceCCForSEPD(LCSProductCostSheet lcsProductCostSheet, double smMSPrice) throws WTException {
        double smPriceCC = 0D;

        if(smMSPrice > 0D)
        {
            String smCsContractCurrency = (String) lcsProductCostSheet.getValue("smCsContractCurrency");
            if(smCsContractCurrency == null) smCsContractCurrency = "";

            LOGGER.info("CUSTOM>>>>>> SMCostSheetCalculate.getNominatedPriceCCForSEPD: smCsContractCurrency = " + smCsContractCurrency);
                float smCsContractCurrencyRate = SMMOAObjectReader.getCurrencyExchangeRate(lcsProductCostSheet, smCsContractCurrency);
                if(smCsContractCurrencyRate == -1 && smCsContractCurrency.equals("vrdUsd"))
                    smCsContractCurrencyRate = 1f;

                smPriceCC = smMSPrice * smCsContractCurrencyRate;
                LOGGER.info("CUSTOM>>>>>> SMCostSheetCalculate.getNominatedPriceCCForSEPD: smPriceCC = " + smPriceCC
                        + ", formula: smMSPrice * smCsContractCurrencyRate = "
                        + smMSPrice + " * " + smCsContractCurrencyRate + "");

        }
        return smPriceCC;
    }
}
