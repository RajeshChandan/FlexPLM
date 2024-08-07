package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.sourcing.LCSCostSheetLogic;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.mc.SMNmcRows;
import com.sportmaster.wc.mc.moa.SMMOAObjectTools;
import com.sportmaster.wc.mc.object.SMProductCostSheet;
import com.sportmaster.wc.mc.tools.SMFormatHelper;
import org.apache.log4j.Logger;
import wt.fc.WTObject;
import wt.ixb.publicforhandlers.IxbHndHelper;
import wt.method.MethodContext;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.wip.WorkInProgressHelper;

public class SMCostSheetPlugin {

    private static final Logger LOGGER = Logger.getLogger(SMCostSheetPlugin.class);

    /**
     * Plugin start for LCSProductCostSheet (PRE_PERSIST event)
     */
    public static WTObject updateCostsheetTables(WTObject wtobject)
            throws WTException, WTPropertyVetoException {

        LOGGER.info("CUSTOM>>>>>> SMCostSheetPlugin.updateCostsheetTables: *** PLUGIN START *** (" + wtobject + ")," +
                " DisplayIdentity (" + IxbHndHelper.getDisplayIdentityForIxb(wtobject) + ")");

        //Cast WTObject to LCSProductCostSheet. If WTObject is of a different type, method throws an exception.
        SMProductCostSheet productCostSheet = SMCostSheetTools.toSMProductCostSheet(wtobject);
        //Debug info
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetPlugin.updateCostsheetTables: *** " +
                " Current type: " + productCostSheet.getFlexType().getTypeName());

        if ( productCostSheet.isMulticurrency() ) {
            boolean accessEnforced = SessionServerHelper.manager.isAccessEnforced();
            try {
                SessionServerHelper.manager.setAccessEnforced(false);

                if (WorkInProgressHelper.isModifiable(productCostSheet.getLCSProductCostSheet())) {

                    String smMcCostSheetCopy = FormatHelper.format((String) MethodContext.getContext().get("SM_MC_COST_SHEET_COPY"));

                    LOGGER.debug("CUSTOM>>>>>> SMCostSheetPlugin.updateCostsheetTables: fix - isCopied - " + productCostSheet.isCopied());
                    LOGGER.debug("CUSTOM>>>>>> SMCostSheetPlugin.updateCostsheetTables: fix - isFirstIteration - " + productCostSheet.isFirstIteration());
                    LOGGER.debug("CUSTOM>>>>>> SMCostSheetPlugin.updateCostsheetTables: fix - smMcCostSheetCopy - " + smMcCostSheetCopy);

                    if ((productCostSheet.isCopied() && productCostSheet.isFirstIteration()) || "YES".equalsIgnoreCase(smMcCostSheetCopy)) {

                        // Copy Referenced BOM Name (PLM-716) Start
                        SMCostSheetTools.copyBOMTableAndReferencedBOMName(productCostSheet.getSourceCostSheet(), productCostSheet.getLCSProductCostSheet());
                        // Copy Referenced BOM Name (PLM-716) End

                        // Copying and setting attributes when copying CostSheet (for APD)
                        if (productCostSheet.isAPD()) {
                            // Set smCSStatusAPD
                            SMCostSheetTools.setSMCSStatus_APD(productCostSheet.getLCSProductCostSheet());
                            // Copy smPMsCommentAPD
                            SMCostSheetTools.copySMPMsComment_APD(productCostSheet.getSourceCostSheet(), productCostSheet.getLCSProductCostSheet());
                        }
                        LOGGER.info("CUSTOM>>>>>> SMCostSheetPlugin.updateCostsheetTables: set SM_MC_COST_SHEET_COPY = DONE");
                        MethodContext.getContext().put("SM_MC_COST_SHEET_COPY", "DONE");
                    }

                    //Set current value smCsContractCurrency to context
                    MethodContext.getContext().put("SM_MC_COST_SHEET_CONTRACT_CURRENCY", productCostSheet.getValue("smCsContractCurrency"));

                    // Check if :
                    // 1. NMC rows are needed to be updated
                    // 2. Manual Values are needed to be cleaned in case of changing Contract Currency or setting attribute
                    SMCostSheetLogic costSheetLogic = SMCostSheetLogic.newSMCostSheetLogic(productCostSheet);
                    //boolean updateNMC = costSheetLogic.isUpdateNMC();
                    boolean cleanManualValues = costSheetLogic.isCleanManualValues();

                    // Check if Currency Rates table is needed to be updated (and do refresh)
                    boolean updateCurrencyRates = SMFormatHelper.getBoolean( productCostSheet.getValue(SMCostSheetConfig.REFRESH_EXCHANGERATE) );
                    boolean excludeUpdateCurrencyRates = SMCostSheetReader.isExcludeRefreshExchangeRatesByStates(productCostSheet.getLCSProductCostSheet());
                    if (updateCurrencyRates && !excludeUpdateCurrencyRates)
                        SMCostSheetTools.refreshExchangeRates(productCostSheet.getLCSProductCostSheet());
                    else productCostSheet.setValue(SMCostSheetConfig.REFRESH_EXCHANGERATE, false);

                    // Clean manual values
                    if (cleanManualValues) {
                        if (productCostSheet.isAPD()) {
                            SMCostSheetTools.clearManualValuesAPD(productCostSheet.getLCSProductCostSheet());
                        }
                        else if (productCostSheet.isFPD())
                            SMCostSheetTools.clearManualValuesFPD(productCostSheet.getLCSProductCostSheet());
                        else if (productCostSheet.isSEPD()) {
                            SMCostSheetTools.clearManualValuesSEPDAlternate(productCostSheet.getLCSProductCostSheet());
                            SMCostSheetTools.clearManualValuesSEPDMain(productCostSheet.getLCSProductCostSheet());
                        }
                    }

                    SMNmcRows nmcRows = null;
                    if (productCostSheet.isFPD()) {
                        nmcRows = SMMOAObjectTools.deleteNmcRows(productCostSheet.getLCSProductCostSheet());
                    }

                    // Check if CS BOM table is needed to be updated (and do update)
                    boolean doBOMUpdate = SMFormatHelper.getBoolean( productCostSheet.getValue(SMCostSheetConfig.DO_BOM_UPDATE) );
                    LOGGER.info("CUSTOM>>>>>> SMCostSheetPlugin.updateCostsheetTables: copyRefBOMName... doBOMUpdate = " + doBOMUpdate);
                    if (doBOMUpdate) SMCostSheetTools.updateCostSheetBOMTable(productCostSheet.getLCSProductCostSheet());
                    else {
                        if (productCostSheet.isAPD()) {
                            if (updateCurrencyRates || cleanManualValues)
                                SMCostSheetCalculate.calculateSMPriceCC_APD(productCostSheet.getLCSProductCostSheet());
                            SMMOAObjectTools.updateBOMTableRows_SMTransportationSupplier_APD(productCostSheet);
                        }
                        else if (productCostSheet.isFPD()) {
                            if (updateCurrencyRates || cleanManualValues)
                                SMCostSheetCalculate.calculateSMPriceCC_FPD(productCostSheet.getLCSProductCostSheet());
                        }
                        else if (productCostSheet.isSEPD()) {
                            SMMOAObjectTools.updateBOMTableRows_SMTransportationSupplier_SEPD(productCostSheet);
                        }
                    }

                    SMCostSheetStatus.updateStatusTotalVariations(productCostSheet.getLCSProductCostSheet());

                    if (productCostSheet.isAPD()) {
                        SMCostSheetCalculateTotalSet.calculateCostSheetSectionsTotal_APD(productCostSheet);
                        SMCostSheetCalculateTotalSet.calculateCostSheetSMBOMTotalGSCurr_APD(productCostSheet.getLCSProductCostSheet());
                        SMCostSheetCalculateTotalSet.calculateCostSheetSMBOMQuotedPriceGSCurr_APD(productCostSheet.getLCSProductCostSheet());
                        SMCostSheetCalculate.calculateCostSheetAttributesInUSD_APD(productCostSheet.getLCSProductCostSheet());
                        SMCostSheetCalculate.calculateSMProfitSupplier_APD(productCostSheet.getLCSProductCostSheet());
                        //Update smTotalFinal for all BOM Table rows on CostSheet
                        if(costSheetLogic.isUpdateSMTotalFinal_APD()) {
                            SMCostSheetCalculateTotalSet.updateSMTotalFinalForBOMTableRows_APD(productCostSheet);
                        }
                    }
                    else if (productCostSheet.isFPD()) {
                        SMCostSheetCalculateTotalSet.calculateCostSheetSectionsTotal_FPD(productCostSheet.getLCSProductCostSheet());
                        SMMOAObjectTools.createNmcRows(productCostSheet.getLCSProductCostSheet(), nmcRows);
                        SMCostSheetCalculateTotalSet.calculateCostSheetNmcSectionsTotal_FPD(productCostSheet.getLCSProductCostSheet());
                        // Calculate formulas
                        SMCostSheetCalculate.calculateFormulasForFPD(productCostSheet.getLCSProductCostSheet());
                    } else if (productCostSheet.isSEPD()) {
                        // Hide or show attribute values
                        boolean smShowNomPricesToSupplier = SMFormatHelper.getBoolean( productCostSheet.getValue("smShowNomPricesToSupplier") );
                        SMCostSheetHideValue.hideOrShowBOMTableAttrValues(productCostSheet, smShowNomPricesToSupplier);
                        // Calculate all formulas
                        SMCostSheetCalculate.calculateFormulasForSEPD(productCostSheet.getLCSProductCostSheet());
                        // Update BOM section total table
                        SMCostSheetBuildTable.updateBOMSectionTotalsTableMOAObjectCollection(productCostSheet.getLCSProductCostSheet());
                    }
                }

            } finally {
                SessionServerHelper.manager.setAccessEnforced(accessEnforced);
            }
        }

        LOGGER.info("CUSTOM>>>>>> SMCostSheetPlugin.updateCostsheetTables: *** PLUGIN FINISH *** ");
        return productCostSheet.getLCSProductCostSheet();
    }

    /**
     * Plugin start for LCSProductCostSheet (POST_CREATE_PERSIST event)
     */
    public static WTObject createTablesForNewCostSheet(WTObject wtobject)
            throws WTException, WTPropertyVetoException {

        LOGGER.info("CUSTOM>>>>>> SMCostSheetPlugin.createTablesForNewCostSheet: *** PLUGIN START *** (" + wtobject + ")," +
                " DisplayIdentity (" + IxbHndHelper.getDisplayIdentityForIxb(wtobject) + ")");

        //Cast WTObject to LCSProductCostSheet. If WTObject is of a different type, method throws an exception.
        LCSProductCostSheet lcscostsheet = SMCostSheetTools.toLCSProductCostSheet(wtobject);

        // Get the latest iteration to avoid check-out error
        lcscostsheet = VersionHelper.latestIterationOf(lcscostsheet.getMaster());

        boolean accessEnforced = SessionServerHelper.manager.isAccessEnforced();
        try {
            SessionServerHelper.manager.setAccessEnforced(false);

            // check CS out if not yet
            if (wt.vc.wip.WorkInProgressHelper.isCheckedOut(lcscostsheet)) {
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetPlugin.createTablesForNewCostSheet: Cost Sheet is checked OUT");
            } else {
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetPlugin.createTablesForNewCostSheet: Cost Sheet is checked IN");
                lcscostsheet = (LCSProductCostSheet) wt.vc.wip.WorkInProgressHelper.service.checkout(
                        lcscostsheet, wt.vc.wip.WorkInProgressHelper.service.getCheckoutFolder(), null).getWorkingCopy();

                if (SMCostSheetTypeSelector.isSEPD(lcscostsheet)) {
                    boolean isBikes = SMCostSheetReader.isBikesProduct_SEPD(lcscostsheet);
                    lcscostsheet.setValue("smShowNomPricesToSupplier", !isBikes);
                }

                LCSCostSheetLogic.persist(lcscostsheet, false, false);
                lcscostsheet = (LCSProductCostSheet) wt.vc.wip.WorkInProgressHelper.service.checkin(lcscostsheet, null);
            }
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessEnforced);
        }
        LOGGER.info("CUSTOM>>>>>> SMCostSheetPlugin.createTablesForNewCostSheet: *** PLUGIN FINISH ***");
        return lcscostsheet;
    }

    /**
     * Plugin start for LCSProductCostSheet (PRE_CREATE_PERSIST event)
     */
    public static WTObject preCreateCostSheet(WTObject wtobject) throws WTException {
        LOGGER.info("CUSTOM>>>>>> SMCostSheetPlugin.preCreateCostSheet: *** PLUGIN START *** (" + wtobject + ")," +
                " DisplayIdentity (" + IxbHndHelper.getDisplayIdentityForIxb(wtobject) + ")");

        //Cast WTObject to LCSProductCostSheet. If WTObject is of a different type, method throws an exception.
        LCSProductCostSheet lcsProductCostSheet = SMCostSheetTools.toLCSProductCostSheet(wtobject);

        boolean accessEnforced = SessionServerHelper.manager.isAccessEnforced();
        try {
            SessionServerHelper.manager.setAccessEnforced(false);

        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessEnforced);
        }

        LOGGER.info("CUSTOM>>>>>> SMCostSheetPlugin.preCreateCostSheet: *** PLUGIN FINISH ***");
        return lcsProductCostSheet;
    }

    public static WTObject deleteBOMTable(WTObject wtobject) throws WTException {
        LOGGER.info("CUSTOM>>>>>> SMCostSheetPlugin.deleteBOMTable: *** PLUGIN START *** (" + wtobject + ")," +
                " DisplayIdentity (" + IxbHndHelper.getDisplayIdentityForIxb(wtobject) + ")");

        //Cast WTObject to LCSProductCostSheet. If WTObject is of a different type, method throws an exception.
        LCSProductCostSheet lcsProductCostSheet = SMCostSheetTools.toLCSProductCostSheet(wtobject);
        String bomTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcsProductCostSheet);

        boolean accessEnforced = SessionServerHelper.manager.isAccessEnforced();
        try {
            SessionServerHelper.manager.setAccessEnforced(false);
            SMCostSheetTools.clearAllRowsMOATable(lcsProductCostSheet, bomTableName);
        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessEnforced);
        }

        LOGGER.info("CUSTOM>>>>>> SMCostSheetPlugin.deleteBOMTable: *** PLUGIN FINISH *** ");
        return lcsProductCostSheet;
    }

    public static WTObject deleteAllMOATables(WTObject wtobject) throws WTException {
        LOGGER.info("CUSTOM>>>>>> SMCostSheetPlugin.deleteAllMOATables: *** PLUGIN START *** (" + wtobject + ")," +
                " DisplayIdentity (" + IxbHndHelper.getDisplayIdentityForIxb(wtobject) + ")");

        //Cast WTObject to LCSProductCostSheet. If WTObject is of a different type, method throws an exception.
        LCSProductCostSheet lcsProductCostSheet = SMCostSheetTools.toLCSProductCostSheet(wtobject);

        String bomTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcsProductCostSheet);
        String bomAdditionalTableName = SMCostSheetTypeSelector.getSMCostSheetBOMAdditionalTableName(lcsProductCostSheet);
        String bomSectionsTotalTableName = SMCostSheetTypeSelector.getSMCostSheetBOMSectionsTotalTableName(lcsProductCostSheet);

        boolean accessEnforced = SessionServerHelper.manager.isAccessEnforced();
        try {
            SessionServerHelper.manager.setAccessEnforced(false);

            SMCostSheetTools.deleteAllRowsMOATable(lcsProductCostSheet, bomTableName);
            SMCostSheetTools.deleteAllRowsMOATable(lcsProductCostSheet, bomAdditionalTableName);
            SMCostSheetTools.deleteAllRowsMOATable(lcsProductCostSheet, bomSectionsTotalTableName);

        } finally {
            SessionServerHelper.manager.setAccessEnforced(accessEnforced);
        }
        LOGGER.info("CUSTOM>>>>>> SMCostSheetPlugin.deleteAllMOATables: *** PLUGIN FINISH *** ");
        return lcsProductCostSheet;
    }

    public static LCSProductCostSheet doCICO(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {
        lcsProductCostSheet = (LCSProductCostSheet) WorkInProgressHelper.service.checkin(lcsProductCostSheet, null);
        lcsProductCostSheet = (LCSProductCostSheet) wt.vc.wip.WorkInProgressHelper.service.checkout(
                lcsProductCostSheet, wt.vc.wip.WorkInProgressHelper.service.getCheckoutFolder(), null).getWorkingCopy();
        return lcsProductCostSheet;
    }

    /////////////
    public static WTObject preDerive(WTObject wtobject)
            throws WTException {

        LOGGER.info("CUSTOM>>>>>> SMCostSheetPlugin.preDerive: *** PLUGIN START *** (" + wtobject + ")," +
                " DisplayIdentity (" + IxbHndHelper.getDisplayIdentityForIxb(wtobject) + ")");

        //Cast WTObject to LCSProductCostSheet. If WTObject is of a different type, method throws an exception.
        LCSProductCostSheet lcscostsheet = SMCostSheetTools.toLCSProductCostSheet(wtobject);
        //Debug info

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.preDerive: fix - isModifiable - " + WorkInProgressHelper.isModifiable(lcscostsheet));

        boolean isFirstIteration = "A.1".equals(lcscostsheet.getIterationDisplayIdentifier().toString());

        LCSProductCostSheet srcCS = null;
        if (lcscostsheet.getCopiedFrom() != null)
            srcCS = (LCSProductCostSheet) lcscostsheet.getCopiedFrom();
        if (lcscostsheet.getCarriedOverFrom() != null)
            srcCS = (LCSProductCostSheet) lcscostsheet.getCarriedOverFrom();

        boolean isCopied = (srcCS != null);

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.preDerive: fix - isCopied - " + isCopied);
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.preDerive: fix - isFirstIteration - " + isFirstIteration);
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.preDerive: fix - Iteration - " + lcscostsheet.getIterationDisplayIdentifier().toString());
        return lcscostsheet;
    }
    //////////
}


