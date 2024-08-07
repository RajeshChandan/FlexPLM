package com.sportmaster.wc.mc.moa;

import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.mc.object.SMProductCostSheet;
import com.sportmaster.wc.mc.sourcing.*;
import com.sportmaster.wc.mc.tools.SMFormatHelper;
import com.sportmaster.wc.mc.tools.SMPrintHelper;
import org.apache.log4j.Logger;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.method.MethodContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.WorkInProgressHelper;

import java.util.Collection;

public class SMMOAObjectPlugin {

    private static final Logger LOGGER = Logger.getLogger(SMMOAObjectPlugin.class);

    /**
     * Plugin start for LCSMOAObject (PRE_DERIVE event)
     */
    public static WTObject preDeriveCalculateForCurrentRowBOMTable(WTObject wtobject) throws WTException, WTPropertyVetoException {

        LOGGER.info("CUSTOM>>>>>> SMMOAObjectPlugin.preDeriveCalculateForCurrentRowBOMTable: *** PLUGIN START *** (" + wtobject + ")");

        LCSMOAObject lcsmoaObject = SMCostSheetTools.toLCSMOAObject(wtobject);
        LCSProductCostSheet lcsProductCostSheet = (LCSProductCostSheet) VersionHelper.latestIterationOf(lcsmoaObject.getOwner());

        boolean isAPD = SMCostSheetTypeSelector.isAPD(lcsProductCostSheet);
        if (isAPD) {
            // Clean manual values for NMC rows (which should not be filled)
            SMMOAObjectTools.cleanValuesForAdditionalRows_APD(lcsmoaObject);
        }

        LOGGER.debug("CUSTOM>>>>>> SMMOAObjectPlugin.preDeriveCalculateForCurrentRowBOMTable: *** PLUGIN FINISH ***");
        return lcsmoaObject;
    }

    /**
     * Plugin start for LCSMOAObject (PRE_PERSIST event)
     */
    public static WTObject calculateForCurrentRowBOMTable(WTObject wtobject) throws WTException {

        LOGGER.info("CUSTOM>>>>>> SMMOAObjectPlugin.calculateForCurrentRowBOMTable: *** PLUGIN START *** (" + wtobject + ")");

        LCSMOAObject lcsmoaObject = SMCostSheetTools.toLCSMOAObject(wtobject);
        LCSProductCostSheet lcsProductCostSheet = (LCSProductCostSheet) VersionHelper.latestIterationOf(lcsmoaObject.getOwner());

        boolean isAPD = SMCostSheetTypeSelector.isAPD(lcsProductCostSheet);
        if (isAPD)
        {
            String smCsContractCurrencyContext = (String) MethodContext.getContext().get("SM_MC_COST_SHEET_CONTRACT_CURRENCY");
            String smCsContractCurrencyLatestIteration = FormatHelper.format((String) lcsProductCostSheet.getValue("smCsContractCurrency"));

            float smCurrencyRate = SMCostSheetCalculate.getSMCurrencyRate(lcsProductCostSheet,
                    smCsContractCurrencyContext != null ? smCsContractCurrencyContext : smCsContractCurrencyLatestIteration);

            SMCostSheetCalculate.calculateSMTotalUSD_SMTotalUSDSM_APD(lcsmoaObject, smCurrencyRate);

            //Set smTotalFinal
            SMCostSheetCalculateTotalSet.setSMTotalFinal_APD(lcsmoaObject, lcsProductCostSheet);

            //Debug info
            SMPrintHelper.printInfoSMTotalUSD("calculateForCurrentRowBOMTable: ContractCurrency: (" +
                    smCsContractCurrencyContext + "/" + smCsContractCurrencyLatestIteration + ")", lcsmoaObject);
        }

        LOGGER.debug("CUSTOM>>>>>> SMMOAObjectPlugin.calculateForCurrentRowBOMTable: *** PLUGIN FINISH ***");
        return lcsmoaObject;
    }

    /**
     * Plugin start for LCSMOAObject (PRE_PERSIST event)
     */
    public static WTObject calculateForCurrentRowBOMAdditionalTable(WTObject wtobject) throws WTException {

        //LOGGER.info("CUSTOM>>>>>> SMMOAObjectPlugin.calculateForCurrentRowBOMAdditionalTable: *** PLUGIN START *** (" + wtobject + ")");
        LCSMOAObject moaobject = SMCostSheetTools.toLCSMOAObject(wtobject);

        //LOGGER.info("CUSTOM>>>>>> SMMOAObjectPlugin.calculateForCurrentRowBOMAdditionalTable: *** PLUGIN FINISH ***");
        return moaobject;
    }

    /**
     * Plugin start for LCSMOAObject (POST_UPDATE_PERSIST event)
     */
    public static WTObject calculateBOMTotals(WTObject wtobject) throws WTException, WTPropertyVetoException {

        LOGGER.info("CUSTOM>>>>>> SMMOAObjectPlugin.calculateBOMTotals: *** PLUGIN START *** (" + wtobject + ")");

        LCSMOAObject lcsmoaObject = SMCostSheetTools.toLCSMOAObject(wtobject);
        LCSProductCostSheet lcsProductCostSheet = (LCSProductCostSheet) VersionHelper.latestIterationOf(lcsmoaObject.getOwner());

        //Debug info
        LOGGER.debug("CUSTOM>>>>>> MMOAObjectPlugin.calculateBOMTotals: *** " +
                " Current type: " + lcsProductCostSheet.getFlexType().getTypeName());

        boolean isAPD = SMCostSheetTypeSelector.isAPD(lcsProductCostSheet);
        boolean isFPD = SMCostSheetTypeSelector.isFPD(lcsProductCostSheet);
        boolean isSEPD = SMCostSheetTypeSelector.isSEPD(lcsProductCostSheet);
        if ( isAPD || isFPD || isSEPD )
        {
            if (WorkInProgressHelper.isCheckedOut( lcsProductCostSheet )) {
                LOGGER.debug("CUSTOM>>>>>> SMMOAObjectPlugin.calculateBOMTotals: Cost Sheet is checked OUT");
            } else {
                LOGGER.debug("CUSTOM>>>>>> SMMOAObjectPlugin.calculateBOMTotals: Cost Sheet is checked IN");
                lcsProductCostSheet = (LCSProductCostSheet) WorkInProgressHelper.service.checkout(
                        lcsProductCostSheet, WorkInProgressHelper.service.getCheckoutFolder(), null).getWorkingCopy() ;

                SMCostSheetStatus.updateStatusTotalVariations( lcsProductCostSheet );

                if (isAPD) {
                    SMCostSheetCalculateTotalSet.calculateCostSheetSectionsTotal_APD( lcsProductCostSheet );
                    SMCostSheetCalculateTotalSet.calculateCostSheetSMBOMTotalGSCurr_APD( lcsProductCostSheet );
                    SMCostSheetCalculateTotalSet.calculateCostSheetSMBOMQuotedPriceGSCurr_APD( lcsProductCostSheet );
                    SMCostSheetCalculate.calculateCostSheetAttributesInUSD_APD( lcsProductCostSheet );
                    SMCostSheetCalculate.calculateSMProfitSupplier_APD( lcsProductCostSheet );
                }
                else if (isFPD) {
                    SMCostSheetCalculateTotalSet.calculateCostSheetSectionsTotal_FPD( lcsProductCostSheet );
                    SMCostSheetCalculateTotalSet.calculateCostSheetNmcSectionsTotal_FPD( lcsProductCostSheet );
                    // Calculate formulas
                    SMCostSheetCalculate.calculateFormulasForFPD( lcsProductCostSheet );
                } else if (isSEPD) {
                    // Calculate all formulas
                    SMCostSheetCalculate.calculateFormulasForSEPD( lcsProductCostSheet );
                    // Update BOM section total table
                    SMCostSheetBuildTable.updateBOMSectionTotalsTableMOAObjectCollection( lcsProductCostSheet );
                }

                lcsProductCostSheet = (LCSProductCostSheet) PersistenceHelper.manager.save( lcsProductCostSheet );
                WorkInProgressHelper.service.checkin(lcsProductCostSheet, null);
            }
        }
        LOGGER.debug("CUSTOM>>>>>> SMMOAObjectPlugin.calculateBOMTotals: *** PLUGIN FINISH ***");
        return lcsmoaObject;
    }

    /**
     * Plugin start for LCSMOAObject (POST_PERSIST and POST_DELETE events)
     */
    public static WTObject calculateBOMAdditionalTotals(WTObject wtobject) throws WTException, WTPropertyVetoException {

        LOGGER.info("CUSTOM>>>>>> SMMOAObjectPlugin.calculateBOMAdditionalTotals: *** PLUGIN START *** (" + wtobject + ")");

        LCSMOAObject moaobject = SMCostSheetTools.toLCSMOAObject(wtobject);
        LCSProductCostSheet lcsProductCostSheet = (LCSProductCostSheet) VersionHelper.latestIterationOf(moaobject.getOwner());

        //Debug info
        FlexType currentType = FlexTypeCache.getFlexType(lcsProductCostSheet);
        LOGGER.debug("CUSTOM>>>>>> SMMOAObjectPlugin.calculateBOMAdditionalTotals: *** " +
                " Current type: " + currentType.getTypeName());

        if (SMCostSheetTypeSelector.isSEPD(lcsProductCostSheet)) //SEPD Only
        {
            if (wt.vc.wip.WorkInProgressHelper.isCheckedOut(lcsProductCostSheet)) {
                LOGGER.debug("CUSTOM>>>>>> SMMOAObjectPlugin.calculateBOMAdditionalTotals: Cost Sheet is checked OUT");
            } else {
                LOGGER.debug("CUSTOM>>>>>> SMMOAObjectPlugin.calculateBOMAdditionalTotals: Cost Sheet is checked IN");
                lcsProductCostSheet = (LCSProductCostSheet) wt.vc.wip.WorkInProgressHelper.service.checkout(
                        lcsProductCostSheet, wt.vc.wip.WorkInProgressHelper.service.getCheckoutFolder(), null).getWorkingCopy();

                /* *** Set SM_MC_COST_SHEET_COPY_AND_FIRST_ITERATION value *** */
                if (SMProductCostSheet.isCopied(lcsProductCostSheet) && SMProductCostSheet.isFirstIteration(lcsProductCostSheet)) {
                    String smMcCostSheetCopy = FormatHelper.format((String) MethodContext.getContext().get("SM_MC_COST_SHEET_COPY"));
                    if (!"DONE".equalsIgnoreCase(smMcCostSheetCopy)) {
                        LOGGER.info("CUSTOM>>>>>> SMMOAObjectPlugin.calculateBOMAdditionalTotals: set SM_MC_COST_SHEET_COPY = YES");
                        MethodContext.getContext().put("SM_MC_COST_SHEET_COPY", "YES");
                    } else {
                        LOGGER.info("CUSTOM>>>>>> SMMOAObjectPlugin.calculateBOMAdditionalTotals: SM_MC_COST_SHEET_COPY is DONE");
                    }
                }
                /* *** *************************************************** *** */

                /* ******************************* */
                // Calculate all formulas
                SMCostSheetCalculate.calculateFormulasForSEPD(lcsProductCostSheet);
                /* Update BOM section totals table */
                Collection bomTableRows = SMFormatHelper.getLCSMOATableRows(SMCostSheetTypeSelector.getSMCostSheetBOMTable(lcsProductCostSheet));
                Collection bomAdditionalTableRows = SMFormatHelper.getLCSMOATableRows(SMCostSheetTypeSelector.getSMCostSheetBOMAdditionalTable(lcsProductCostSheet));
                SMCostSheetBuildTable.updateBOMSectionTotalsTableMOAObjectCollection(lcsProductCostSheet, bomTableRows, bomAdditionalTableRows);
                /* ******************************* */

                lcsProductCostSheet = (LCSProductCostSheet) PersistenceHelper.manager.save(lcsProductCostSheet);
                WorkInProgressHelper.service.checkin(lcsProductCostSheet, null);
            }
        }
        LOGGER.debug("CUSTOM>>>>>> SMMOAObjectPlugin.calculateBOMAdditionalTotals: *** PLUGIN FINISH ***");
        return moaobject;
    }

    public static WTObject copyBOMAdditionalTable(WTObject wtobject) throws WTException, WTPropertyVetoException {

        LOGGER.info("CUSTOM>>>>>> SMMOAObjectPlugin.copyBOMAdditionalTable: *** PLUGIN START *** (" + wtobject + ")");

        LCSMOAObject moaobject = SMCostSheetTools.toLCSMOAObject(wtobject);
        LCSProductCostSheet lcsProductCostSheet = (LCSProductCostSheet) VersionHelper.latestIterationOf(moaobject.getOwner());
        boolean isFirstIteration = "A.1".equals(lcsProductCostSheet.getIterationDisplayIdentifier().toString());
        LOGGER.debug("CUSTOM>>>>>> SMMOAObjectPlugin.copyBOMAdditionalTable: isFirstIteration =" + isFirstIteration);

        boolean isSEPD = SMCostSheetTypeSelector.isSEPD(lcsProductCostSheet);
        if (isSEPD && isFirstIteration) {
            if (wt.vc.wip.WorkInProgressHelper.isCheckedOut(lcsProductCostSheet)) {
                LOGGER.debug("CUSTOM>>>>>> SMMOAObjectPlugin.copyBOMAdditionalTable: Cost Sheet is checked OUT");
            } else {
                LOGGER.debug("CUSTOM>>>>>> SMMOAObjectPlugin.copyBOMAdditionalTable: Cost Sheet is checked IN");
                lcsProductCostSheet = (LCSProductCostSheet) wt.vc.wip.WorkInProgressHelper.service.checkout(
                        lcsProductCostSheet, wt.vc.wip.WorkInProgressHelper.service.getCheckoutFolder(), null).getWorkingCopy();
                LOGGER.debug("CUSTOM>>>>>> SMMOAObjectPlugin.copyBOMAdditionalTable: Point 01");

                /* *** Copy BOM Additional Table *** */

                LCSProductCostSheet srcCS = null;
                if (lcsProductCostSheet.getCopiedFrom() != null)
                    srcCS = (LCSProductCostSheet) lcsProductCostSheet.getCopiedFrom();
                if (lcsProductCostSheet.getCarriedOverFrom() != null)
                    srcCS = (LCSProductCostSheet) lcsProductCostSheet.getCarriedOverFrom();

                if (srcCS != null) {
                    LOGGER.info("CUSTOM>>>>>> SMCostSheetPlugin.copyBOMAdditionalTable: run copyAlternateBOMTable() ...");
                    SMCostSheetTools.copyAlternateBOMTable(srcCS, lcsProductCostSheet);
                }
                lcsProductCostSheet = (LCSProductCostSheet) PersistenceHelper.manager.save(lcsProductCostSheet);
                WorkInProgressHelper.service.checkin(lcsProductCostSheet, null);
            }
        }
        LOGGER.debug("CUSTOM>>>>>> SMMOAObjectPlugin.copyBOMAdditionalTable: *** PLUGIN FINISH ***");
        return moaobject;
    }
}
