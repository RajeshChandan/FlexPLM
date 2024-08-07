package com.sportmaster.wc.mc.moa;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.ptc.core.meta.common.FloatingPoint;
import com.sportmaster.wc.mc.SMNmcRows;
import com.sportmaster.wc.mc.config.SMCostSheetMOATableConfig;
import com.sportmaster.wc.mc.object.SMProductCostSheet;
import com.sportmaster.wc.mc.sourcing.SMCostSheetConfig;
import com.sportmaster.wc.mc.sourcing.SMCostSheetTools;
import com.sportmaster.wc.mc.sourcing.SMCostSheetTypeSelector;
import com.sportmaster.wc.mc.sourcing.SMCostSheetUpdate;
import com.sportmaster.wc.mc.tools.SMFormatHelper;
import org.apache.log4j.Logger;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.*;

public class SMMOAObjectTools {

    private static final Logger LOGGER = Logger.getLogger(SMMOAObjectTools.class);

    /**
     * Clean values for additional/NMC row
     * @param lcsmoaObject - MOATable row
     */
    public static void cleanValuesForAdditionalRows_APD(LCSMOAObject lcsmoaObject) throws WTException, WTPropertyVetoException {

        String section = FormatHelper.format( (String) lcsmoaObject.getValue("smSectionAPD") );
        if ( !section.trim().isEmpty() && SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_ALL_KEYS_APD.contains(section) ) {
            cleanValues(lcsmoaObject, SMFormatHelper.toMap(SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_ATTRS_IGNORE_USER_VALUES_APD));
        }
    }

    /**
     * Clean values for MOATable row
     * @param lcsmoaObject - MOATable row
     * @param attrs Map:
     *              key - attribute name
     *              value - type (double/float or string)
     */
    public static void cleanValues(LCSMOAObject lcsmoaObject, Map<String, String> attrs) throws WTException, WTPropertyVetoException {

        for (Map.Entry<String,String> entry : attrs.entrySet()) {
            if ("double".equalsIgnoreCase(entry.getValue()) || "float".equalsIgnoreCase(entry.getValue()))
                lcsmoaObject.setValue(entry.getKey(), new FloatingPoint( 0, 14 ));
            else if ("string".equalsIgnoreCase(entry.getValue()))
                lcsmoaObject.setValue(entry.getKey(), "");
        }
    }

    public static void refreshBOMTableRows(SMProductCostSheet productCostSheet) throws WTException {

        String bomTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(productCostSheet);
        SMCostSheetTools.refreshAllRowsMOATable(productCostSheet.getLCSProductCostSheet(), bomTableName);
    }

    public static String updateBOMTableRows_SMTransportationSupplier_APD(SMProductCostSheet productCostSheet) throws WTException {

        List<String> values = SMCostSheetConfig.getValues(SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_CS_INCOTERMS_DDP_APD);
        return updateBOMTableRows(productCostSheet, values, "smSectionAPD");
    }

    public static String updateBOMTableRows_SMTransportationSupplier_SEPD(SMProductCostSheet productCostSheet) throws WTException {

        List<String> values = SMCostSheetConfig.getValues(SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_CS_INCOTERMS_DDP_SEPD);
        return updateBOMTableRows(productCostSheet, values, "smSectionSEPD");
    }

    /**
     * Update table rows
     * @return Status: ADDED, DROPPED, DONE
     */
    public static String updateBOMTableRows(SMProductCostSheet productCostSheet, List<String> values, String smSectionInternalName) throws WTException {

        boolean smCsIncotermsIsDDP = FormatHelper.format((String) productCostSheet.getValue("smCsIncoterms")).equals("smDDP");
        String bomTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(productCostSheet);

        for (String value : values) {
            if (smCsIncotermsIsDDP) {
                if (!isBOMTableOneOrMoreRow(productCostSheet, smSectionInternalName, value)) {
                    List<FlexObject> rows = new ArrayList<>();
                    FlexObject flexObject = new FlexObject();
                    flexObject.put(smSectionInternalName, value);
                    flexObject.put("smIncludeInPurchasePrice", true);
                    flexObject.put("smConsumption", 1);
                    rows.add(flexObject);
                    addBOMTableLastRows(productCostSheet.getLCSProductCostSheet(), smSectionInternalName, rows);
                    return "ADDED";
                }
            } else {
                FlexTypeAttribute flexTypeAttribute = productCostSheet.getLCSProductCostSheet().getFlexType().getAttribute(bomTableName);
                for (Object obj : LCSMOAObjectQuery.findMOACollection(productCostSheet.getLCSProductCostSheet(), flexTypeAttribute)) {
                    LCSMOAObject lcsObj = (LCSMOAObject) obj;
                    String section = (String) lcsObj.getValue(smSectionInternalName);
                    if (section.equals(value)) {
                        PersistenceHelper.manager.delete(lcsObj);
                        LOGGER.info("CUSTOM>>>>>> SMMOAObjectTools.updateBOMTableRows: deleted: " + section);
                        return "DROPPED";
                    }
                }
            }
        }
        return "DONE";
    }

    public static boolean isBOMTableOneOrMoreRow(SMProductCostSheet productCostSheet, String attKey, String attValue) throws WTException {

        String costSheetBOMTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName( productCostSheet );
        LCSMOATable moaTable = (LCSMOATable) productCostSheet.getValue( costSheetBOMTableName );
        if (moaTable != null) {
            for (Object object : moaTable.getRows()) {
                FlexObject currentRow = (FlexObject) object;
                String valueCurrentRow = FormatHelper.format( currentRow.getString(attKey) );
                if (valueCurrentRow.equals(attValue))
                    return true;
            }
        }
        return false;
    }

    public static void addBOMTableLastRows(LCSProductCostSheet lcsProductCostSheet, String attNameRowID, List<FlexObject> lastRows) throws WTException {

        if (lastRows != null && lastRows.size() > 0) {
            String costSheetBOMTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcsProductCostSheet);
            LCSMOATable moaTable = (LCSMOATable) lcsProductCostSheet.getValue(costSheetBOMTableName);
            if (moaTable != null) {
                Hashtable resultRows = new Hashtable();
                int nextSortingNumber = 0;
                int nextID = 0;
                for (Object object : moaTable.getRows()) {
                    FlexObject currentRow = (FlexObject) object;
                    String id = FormatHelper.format(currentRow.getString("ID"));
                    resultRows.put(id, currentRow);

                    int currentID = FormatHelper.parseInt(id);
                    if (currentID > nextID) nextID = currentID;
                    int currentSortingNumber = FormatHelper.parseInt(currentRow.getString("SORTINGNUMBER"));
                    if (currentSortingNumber > nextSortingNumber) nextSortingNumber = currentSortingNumber;
                }

                for (FlexObject lastRow : lastRows) {
                    String smRowID = FormatHelper.format( lastRow.getString( attNameRowID ) );
                    lastRow.put("smRowID", smRowID);
                    lastRow.put("sortingNumber", Integer.toString( ++nextSortingNumber) );
                    resultRows.put( Integer.toString( ++nextID ), lastRow);
                }

                SMCostSheetUpdate.newSMCostSheetUpdateForBOMTable(lcsProductCostSheet).copyIDsAndHiddenAttrsForBOMTable(
                        "BRANCHID", "ID", resultRows.values());

                // clear values
                SMCostSheetTools.clearAllRowsMOATable(lcsProductCostSheet, costSheetBOMTableName);

                LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
                moaLogic.updateMOAObjectCollection(lcsProductCostSheet,
                        lcsProductCostSheet.getFlexType().getAttribute(costSheetBOMTableName), resultRows);
            }
        }
    }

    public static void deleteBOMTableRows(LCSProductCostSheet lcsProductCostSheet, String attKey, List<String> attValues) throws WTException {

        String costSheetBOMTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName( lcsProductCostSheet );
        LCSMOATable moaTable = (LCSMOATable) lcsProductCostSheet.getValue( costSheetBOMTableName );
        if (moaTable != null) {
            Hashtable rows = new Hashtable();
            boolean updateMOATable = false;
            for (Object object : moaTable.getRows()) {
                FlexObject row = (FlexObject) object;
                String id = FormatHelper.format( row.getString("ID") );

                for (String value : attValues) {
                    String currentValue = FormatHelper.format( row.getString(attKey) );
                    if (currentValue.equals(value)) {
                        row.put("DROPPED", "true"); // Mark row for deletion
                        updateMOATable = true;
                        break;
                    }
                }

                rows.put(id, row);
            }

            if (updateMOATable) {
                SMCostSheetUpdate.newSMCostSheetUpdateForBOMTable(lcsProductCostSheet).copyIDsAndHiddenAttrsForBOMTable(
                        "BRANCHID", "ID", rows.values());
                LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
                moaLogic.updateMOAObjectCollection(lcsProductCostSheet,
                        lcsProductCostSheet.getFlexType().getAttribute(costSheetBOMTableName), rows);
            }
        }
    }

    public static SMNmcRows deleteNmcRows(LCSProductCostSheet lcsCostSheet) {

        SMNmcRows result = new SMNmcRows();

        try
        {
            Hashtable rowData = new Hashtable();
            String costSheetBOMTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcsCostSheet);
            LCSMOATable bomTable = (LCSMOATable) lcsCostSheet.getValue(costSheetBOMTableName);

            if (bomTable != null) {

                Collection rows = bomTable.getRows();
                if (rows.size() > 0) {

                    for (Object o : rows) {
                        FlexObject row = (FlexObject) o;
                        int id = FormatHelper.parseInt((String) row.get("ID"));
                        String section = (String) row.get("smSection");

                        if (section.equals("NMC")) {
                            float price = FormatHelper.parseFloat((String) row.get("smPriceOVR"));
                            String placement = (String) row.get("smPlacement");

                            if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_UPPER))
                                result.setUpperForPulloverPrice(price);
                            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_SOLE))
                                result.setSoleForPulloverPrice(price);
                            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_LABOR))
                                result.setLaborPrice(price);
                            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_OVERHEAD))
                                result.setOverheadPrice(price);
                            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_NEW_LAST_COST))
                                result.setNewLastCostPrice(price);
                            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_DIE_CUT_COST))
                                result.setDieCutCostPrice(price);
                            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_PROFIT))
                                result.setProfitPrice(price);
                            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_SOURCING))
                                result.setSourcingComissionPrice(price);
                            else if (placement.equals(SMCostSheetConfig.MOA_TABLE_NMC_TRANSPORTATION))
                                result.setTransportationPrice(price);
                            //row.put("DROPPED", "true"); // Mark row for deletion
                        } else rowData.put("" + id, row);
                    }
                    LOGGER.trace(rowData.toString());

                    SMCostSheetUpdate.newSMCostSheetUpdateForBOMTable(lcsCostSheet).copyIDsAndHiddenAttrsForBOMTable(
                            "BRANCHID", "ID", rowData.values());

                    SMCostSheetTools.clearAllRowsMOATable(lcsCostSheet, costSheetBOMTableName);

                    LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
                    moaLogic.updateMOAObjectCollection(lcsCostSheet, lcsCostSheet.getFlexType().getAttribute(costSheetBOMTableName), rowData);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return result;
    }

    public static void createNmcRows(LCSProductCostSheet lcsCostSheet, SMNmcRows rows) throws WTException {

        FlexObject row; //float price=0; String placement="";
        Hashtable rowData = new Hashtable();
        String costSheetBOMTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcsCostSheet);
        LCSMOATable bomTable = (LCSMOATable) lcsCostSheet.getValue(costSheetBOMTableName);

        String costingStage = (String) lcsCostSheet.getValue(SMCostSheetTypeSelector.getSMCostingStage(lcsCostSheet));
        Boolean isPulloverStage = costingStage.equals(SMCostSheetConfig.FPD_COSTING_STAGE_VALUE);
        int index = 0;
        int sorting = 0;
        if (bomTable != null) {
            for (Object o : bomTable.getRows()) {
                row = (FlexObject) o;
                int id = FormatHelper.parseInt((String) row.get("ID"));
                int sn = FormatHelper.parseInt((String) row.get("SORTINGNUMBER"));
                if (id > index) index = id;
                if (sn > sorting) sorting = sn;
                rowData.put("" + id, row);
            }
        }
        if (isPulloverStage) {
            row = new FlexObject();
            index++;
            sorting++;
            row.put("smSection", "NMC");
            row.put("smPlacement", SMCostSheetConfig.MOA_TABLE_NMC_UPPER);
            row.put("smRowID", "smUpper");
            row.put("smPriceOVR", rows.getUpperForPulloverPrice());
            row.put("smConsumption", 1);
            row.put("SORTINGNUMBER", sorting);
            rowData.put("" + index, row);


            row = new FlexObject();
            index++;
            sorting++;
            row.put("smSection", "NMC");
            row.put("smPlacement", SMCostSheetConfig.MOA_TABLE_NMC_SOLE);
            row.put("smRowID", "smSole");
            row.put("smPriceOVR", rows.getSoleForPulloverPrice());
            row.put("smConsumption", 1);
            row.put("SORTINGNUMBER", sorting);
            rowData.put("" + index, row);
        }
        row = new FlexObject();
        index++;
        sorting++;
        row.put("smSection", "NMC");
        row.put("smPlacement", SMCostSheetConfig.MOA_TABLE_NMC_LABOR);
        row.put("smRowID", "smLabor");
        row.put("smPriceOVR", rows.getLaborPrice());
        row.put("smConsumption", 1);
        row.put("SORTINGNUMBER", sorting);
        rowData.put("" + index, row);

        row = new FlexObject();
        index++;
        sorting++;
        row.put("smSection", "NMC");
        row.put("smPlacement", SMCostSheetConfig.MOA_TABLE_NMC_OVERHEAD);
        row.put("smRowID", "smOverhead");
        row.put("smPriceOVR", rows.getOverheadPrice());
        row.put("smConsumption", 1);
        row.put("SORTINGNUMBER", sorting);
        rowData.put("" + index, row);

        row = new FlexObject();
        index++;
        sorting++;
        row.put("smSection", "NMC");
        row.put("smPlacement", SMCostSheetConfig.MOA_TABLE_NMC_NEW_LAST_COST);
        row.put("smRowID", "smNewLastCost");
        row.put("smPriceOVR", rows.getNewLastCostPrice());
        row.put("smConsumption", 1);
        row.put("SORTINGNUMBER", sorting);
        rowData.put("" + index, row);

        row = new FlexObject();
        index++;
        sorting++;
        row.put("smSection", "NMC");
        row.put("smPlacement", SMCostSheetConfig.MOA_TABLE_NMC_DIE_CUT_COST);
        row.put("smRowID", "smDieCutCost");
        row.put("smPriceOVR", rows.getDieCutCostPrice());
        row.put("smConsumption", 1);
        row.put("SORTINGNUMBER", sorting);
        rowData.put("" + index, row);

        row = new FlexObject();
        index++;
        sorting++;
        row.put("smSection", "NMC");
        row.put("smPlacement", SMCostSheetConfig.MOA_TABLE_NMC_PROFIT);
        row.put("smRowID", "smProfit");
        row.put("smPriceOVR", rows.getProfitPrice());
        row.put("smConsumption", 1);
        row.put("SORTINGNUMBER", sorting);
        rowData.put("" + index, row);

        row = new FlexObject();
        index++;
        sorting++;
        row.put("smSection", "NMC");
        row.put("smPlacement", SMCostSheetConfig.MOA_TABLE_NMC_SOURCING);
        row.put("smRowID", "smSourcing");
        row.put("smPriceOVR", rows.getSourcingComissionPrice());
        row.put("smConsumption", 1);
        row.put("SORTINGNUMBER", sorting);
        rowData.put("" + index, row);

        row = new FlexObject();
        index++;
        sorting++;
        row.put("smSection", "NMC");
        row.put("smPlacement", SMCostSheetConfig.MOA_TABLE_NMC_TRANSPORTATION);
        row.put("smRowID", "smTransportation");
        row.put("smPriceOVR", rows.getTransportationPrice());
        row.put("smConsumption", 1);
        row.put("SORTINGNUMBER", sorting);
        rowData.put("" + index, row);

        LOGGER.trace(rowData.toString());

        // copy RowID for all rows
        SMCostSheetUpdate.newSMCostSheetUpdateForBOMTable(lcsCostSheet).copyIDsAndHiddenAttrsForBOMTable(
                "BRANCHID", "ID", rowData.values());
        // delete all rows
        SMCostSheetTools.clearAllRowsMOATable(lcsCostSheet, costSheetBOMTableName);

        if (bomTable != null) {
            LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
            moaLogic.updateMOAObjectCollection(lcsCostSheet, lcsCostSheet.getFlexType().getAttribute(costSheetBOMTableName), rowData);
        }
    }

    public static Float addCurrencyRate(LCSProductCostSheet lcsCostSheet, String currency) throws WTException {
        LCSSeason season = (LCSSeason) VersionHelper.latestIterationOf(lcsCostSheet.getSeasonMaster());
        Long seasonBranchId = season.getBranchIdentifier();
        Float rate = SMCostSheetTools.getCurrencyRate(seasonBranchId, currency);
        FlexObject row;
        int index = 0;
        Hashtable rowData = new Hashtable();
        LCSMOATable ratesTable = (LCSMOATable) lcsCostSheet.getValue(SMCostSheetConfig.CURRENCY_RATES_TABLE);

        if (ratesTable != null) {
            for (Object o : ratesTable.getRows()) {
                row = (FlexObject) o;
                int id = FormatHelper.parseInt((String) row.get("ID"));
                if (id > index) index = id;
                rowData.put("" + id, row);
            }
        }

        String comments = "";
        row = new FlexObject();
        index++;
        if (rate == -1) {
            if (currency.startsWith("sm")) comments = "No rate for currency " + currency.substring(2);
            else if (currency.startsWith("vrd")) comments = "No rate for currency " + currency.substring(3);
            else
                comments = "No rate for currency " + currency;
        }

        row.put("SMLASTUPDATED", new Date());
        row.put("SMCURRENCYRATE", "" + rate);
        row.put("SMCURRENCYVALUE", currency);
        row.put("SMCOMMENTS", comments);
        row.put("ID", "" + index);
        rowData.put("" + index, row);

        LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
        moaLogic.updateMOAObjectCollection((WTObject) lcsCostSheet, lcsCostSheet.getFlexType().getAttribute(SMCostSheetConfig.CURRENCY_RATES_TABLE), rowData);
        return rate;
    }

}
