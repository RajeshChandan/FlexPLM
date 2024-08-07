package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.mc.config.SMCostSheetConfig;
import com.sportmaster.wc.mc.object.SMProductCostSheet;
import com.sportmaster.wc.mc.tools.SMFormatHelper;
import org.apache.log4j.Logger;
import wt.util.WTException;
import wt.vc.wip.WorkInProgressHelper;

public class SMCostSheetLogic {

    private static final Logger LOGGER = Logger.getLogger(SMCostSheetLogic.class);

    private SMProductCostSheet productCostSheet = null;
    // private Boolean updateNMC = null;
    private Boolean cleanManualValues = null;
    private Boolean updateSMTotalFinal = null;

    private SMCostSheetLogic() { }

    public static SMCostSheetLogic newSMCostSheetLogic(SMProductCostSheet productCostSheet) {
        SMCostSheetLogic result = new SMCostSheetLogic();
        result.productCostSheet = productCostSheet;
        return result;
    }

    /*
    public boolean isUpdateNMC() throws WTException {
        if (updateNMC == null) initLogic();
        return updateNMC;
    }
     */

    public boolean isCleanManualValues() throws WTException {
        if (cleanManualValues == null) initLogic();
        return cleanManualValues;
    }

    public boolean isUpdateSMTotalFinal_APD() throws WTException {
        if (updateSMTotalFinal == null) initLogic();
        return updateSMTotalFinal;
    }

    private void initLogic() throws WTException {
        //updateNMC = false;
        cleanManualValues = false;
        updateSMTotalFinal = true;
        if (WorkInProgressHelper.isWorkingCopy(productCostSheet.getLCSProductCostSheet())) {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetLogic.updateCostsheetTables: isWorkingCopy = true");
            LCSProductCostSheet originalCostSheet = (LCSProductCostSheet) WorkInProgressHelper.service.originalCopyOf(productCostSheet.getLCSProductCostSheet());

            // Debug info ------------------------------------
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetLogic.updateCostsheetTables: *** " +
                    " Working Copy type: " + productCostSheet.getFlexType().getTypeName());
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetLogic.updateCostsheetTables: *** " +
                    " Original Copy type: " + FlexTypeCache.getFlexType(originalCostSheet).getTypeName());

            boolean smCleanBOMTable = SMFormatHelper.getBoolean( productCostSheet.getValue("smCleanBOMTable") );

            /*
            if (!productCostSheet.isFirstIteration()) {
                String wrkStatus = (String) productCostSheet.getValue( SMCostSheetTypeSelector.getSMCostingStage(productCostSheet.getLCSProductCostSheet()) );
                String origStatus = (String) originalCostSheet.getValue( SMCostSheetTypeSelector.getSMCostingStage(originalCostSheet) );
                updateNMC = !wrkStatus.equals(origStatus);
            }
             */

            if (!productCostSheet.isFirstIteration() || productCostSheet.isCopied()) {
                String wrkCurrency = (String) productCostSheet.getValue("smCsContractCurrency");
                String origCurrency = (String) originalCostSheet.getValue("smCsContractCurrency");
                cleanManualValues = (!wrkCurrency.equals(origCurrency) || smCleanBOMTable);
            }

            if (productCostSheet.isAPD()) { //Initializing the value of updateSMTotalFinal
                String smCostingStageWorkingCopy = FormatHelper.format((String) productCostSheet.getValue(SMCostSheetConfig.SM_COSTING_STAGE_ATTRIBUTE_APD));
                boolean isSMTotalWorkingCopy = SMCostSheetConfig.SM_COST_SHEET_COSTING_STAGE_VALUES_CALCULATE_TOTAL_FOREACH_SECTION_APD.contains(smCostingStageWorkingCopy);
                String smCostingStageOriginalCopy = FormatHelper.format((String) originalCostSheet.getValue(SMCostSheetConfig.SM_COSTING_STAGE_ATTRIBUTE_APD));
                boolean isSMTotalOriginalCopy = SMCostSheetConfig.SM_COST_SHEET_COSTING_STAGE_VALUES_CALCULATE_TOTAL_FOREACH_SECTION_APD.contains(smCostingStageOriginalCopy);
                updateSMTotalFinal = isSMTotalWorkingCopy != isSMTotalOriginalCopy;
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetLogic.updateCostsheetTables: updateSMTotalFinal = " + updateSMTotalFinal);
            }

            LOGGER.debug("CUSTOM>>>>>> SMCostSheetLogic.updateCostsheetTables: Clean = " + smCleanBOMTable);
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetLogic.updateCostsheetTables: cleanManualValues = " + cleanManualValues);
            //LOGGER.debug("CUSTOM>>>>>> SMCostSheetLogic.updateCostsheetTables: Update NMC = " + updateNMC);
        }
    }
}
