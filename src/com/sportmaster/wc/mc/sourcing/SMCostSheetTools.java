package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.ProductDestination;
import com.lcs.wc.product.ReferencedTypeKeys;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.mc.config.SMCostSheetMOATableConfig;
import com.sportmaster.wc.mc.moa.SMMOAObjectReader;
import com.sportmaster.wc.mc.object.SMBOMTable;
import com.sportmaster.wc.mc.object.SMProductCostSheet;
import com.sportmaster.wc.mc.tools.SMFormatHelper;
import org.apache.log4j.Logger;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.ixb.publicforhandlers.IxbHndHelper;
import wt.method.MethodContext;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.*;

public class SMCostSheetTools {

    private static final Logger LOGGER = Logger.getLogger(SMCostSheetTools.class);

    public static void copyMOATableAttrValues(LCSProductCostSheet lcsCostSheet, String moaTableName, Map<String,String> attrs, boolean persist, boolean skipPlugins) throws WTException, WTPropertyVetoException {

        Collection rowsMOATable = LCSMOAObjectQuery.findMOACollection(lcsCostSheet,
                lcsCostSheet.getFlexType().getAttribute(moaTableName));
        copyMOATableAttrValues(rowsMOATable, attrs, persist, skipPlugins);
    }

    public static void copyMOATableAttrValues(Collection rowsMOATable, Map<String,String> attrs, boolean persist, boolean skipPlugins) throws WTException, WTPropertyVetoException {

        for(Object obj : rowsMOATable) {
            LCSMOAObject lcsmoaObject = (LCSMOAObject) obj;
            for (Map.Entry<String,String> entry : attrs.entrySet()) {
                String attrNameSource = entry.getKey();
                Object attrValueSource = lcsmoaObject.getValue(attrNameSource);
                String attrNameTarget = entry.getValue();
                lcsmoaObject.setValue(attrNameTarget, attrValueSource);
            }

            if (persist) {
                LCSMOAObjectLogic.persist(lcsmoaObject, skipPlugins);
            }
        }
    }

    public static void deleteAllRowsMOATable(LCSProductCostSheet lcsCostSheet, String moaTableName) throws WTException {

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.deleteAllRowsMOATable: - Start - delete all rows for " + lcsCostSheet + ", moaTableName: " + moaTableName);
        Collection rows = LCSMOAObjectQuery.findMOACollection(lcsCostSheet, lcsCostSheet.getFlexType().getAttribute(moaTableName));
        if (rows != null) {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.deleteAllRowsMOATable: - Rows count " + rows.size());
            for (Object obj : rows) {
                LCSMOAObject lcsObj = (LCSMOAObject) obj;
                PersistenceHelper.manager.delete(lcsObj);
            }
        }
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.deleteAllRowsMOATable: - Finish - delete all rows for " + lcsCostSheet + ", moaTableName: " + moaTableName);
    }

    public static void clearAllRowsMOATable(LCSProductCostSheet lcsCostSheet, String moaTableAtt) throws WTException {

        LCSMOATable moaTable = (LCSMOATable) lcsCostSheet.getValue(moaTableAtt);
        if (moaTable != null) {
            Collection rows = moaTable.getRows();
            if ( rows.size() > 0 ) {
                Iterator rowsIter = rows.iterator();
                Hashtable rowData = new Hashtable();
                while (rowsIter.hasNext()) {
                    FlexObject row = (FlexObject) rowsIter.next();
                    int id = FormatHelper.parseInt((String) row.get("ID"));
                    row.put("DROPPED", "true");
                    rowData.put("" + id, row);
                }
                LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
                moaLogic.updateMOAObjectCollection(lcsCostSheet, lcsCostSheet.getFlexType().getAttribute(moaTableAtt), rowData);
            }
        }
    }

    public static void refreshAllRowsMOATable(LCSProductCostSheet lcsProductCostSheet, String moaTableAtt) throws WTException {

        LCSMOATable moaTable = (LCSMOATable) lcsProductCostSheet.getValue(moaTableAtt);
        if (moaTable != null) {
            Collection rows = moaTable.getRows();
            if ( rows.size() > 0 ) {
                Hashtable rowData = new Hashtable();
                for (Object obj : rows) {
                    FlexObject row = (FlexObject) obj;
                    int id = FormatHelper.parseInt((String) row.get("ID"));
                    rowData.put("" + id, row);
                }

                SMCostSheetUpdate.newSMCostSheetUpdateForBOMTable(lcsProductCostSheet).copyIDsAndHiddenAttrsForBOMTable(
                        "BRANCHID", "ID", rowData.values());

                clearAllRowsMOATable(lcsProductCostSheet, moaTableAtt);

                LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
                moaLogic.updateMOAObjectCollection(lcsProductCostSheet, lcsProductCostSheet.getFlexType().getAttribute(moaTableAtt), rowData);
            }
        }
    }

    public static void refreshExchangeRates(LCSProductCostSheet lcsCostSheet) throws WTException, WTPropertyVetoException {

        clearCurrencyRatesTable(lcsCostSheet);

        LCSSeason season = (LCSSeason) VersionHelper.latestIterationOf(lcsCostSheet.getSeasonMaster());
        Long seasonBranchId = season.getBranchIdentifier();

        FlexObject row;  int index = 0;
        Hashtable rowData = new Hashtable();

        Set<String> currencies = SMMOAObjectReader.getCurrencyList(lcsCostSheet);

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.refreshExchangeRates: Set is  -> " + currencies);
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.refreshExchangeRates: Set size  is  -> " + currencies.size());

        for (String nextCurrency : currencies)
        {
            if (nextCurrency != null ) {
                index++;

                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.refreshExchangeRates: Next Currency to update -> " + nextCurrency);
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.refreshExchangeRates: Current Exchange Rate for " + nextCurrency + " is " + getCurrencyRate(seasonBranchId, nextCurrency));
                row = new FlexObject();

                Float cr = getCurrencyRate(seasonBranchId, nextCurrency);
                String comments = "";
                if (cr == -1) {
                    if (nextCurrency.startsWith("sm")) comments = "No rate for currency " + nextCurrency.substring(2);
                    else if (nextCurrency.startsWith("vrd")) comments = "No rate for currency " + nextCurrency.substring(3);
                    else
                        comments = "No rate for currency " + nextCurrency;
                }

                row.put("SMLASTUPDATED", new Date());
                row.put("SMCURRENCYRATE", "" + getCurrencyRate(seasonBranchId, nextCurrency));
                row.put("SMCURRENCYVALUE", nextCurrency);
                row.put("SMCOMMENTS", comments);
                row.put("ID", "" + index);
                rowData.put("" + index, row);
            }
        }
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.refreshExchangeRates: rowData size  is  -> " + rowData.size());
        LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
        moaLogic.updateMOAObjectCollection((WTObject) lcsCostSheet, lcsCostSheet.getFlexType().getAttribute(SMCostSheetConfig.CURRENCY_RATES_TABLE), rowData);

        lcsCostSheet.setValue(SMCostSheetConfig.REFRESH_EXCHANGERATE, false);
    }

    public static void clearCurrencyRatesTable(LCSProductCostSheet lcsCostSheet) throws WTException {

        LCSMOATable ratesTable = (LCSMOATable) lcsCostSheet.getValue(SMCostSheetConfig.CURRENCY_RATES_TABLE);
        if (ratesTable != null) {
            Collection rows = ratesTable.getRows();
            //  int size = rows.size();
            Iterator rowsIter = rows.iterator();// Existing rows from currency rates table
            FlexObject row;
            //int index = 0;
            Hashtable rowData = new Hashtable();
            while (rowsIter.hasNext()) {
                row = (FlexObject) rowsIter.next();
                int id = FormatHelper.parseInt((String) row.get("ID"));
                row.put("DROPPED", "true");
                rowData.put("" + id, row);
            }
            LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
            moaLogic.updateMOAObjectCollection((WTObject) lcsCostSheet, lcsCostSheet.getFlexType().getAttribute(SMCostSheetConfig.CURRENCY_RATES_TABLE), rowData);
        }
    }

    public static float getCurrencyRate(Long seasonBranchId, String currency) throws WTException {
       if (currency.equals("vrdUsd")) return 1;
        final FlexType boType = lookupCurrencyConvTable();
        SearchResults sr = processExchangeRate(boType, seasonBranchId, currency);
        String currencyRate = processExchangeRateResults(sr, boType);
        if (currencyRate != null) return Float.parseFloat(processExchangeRateResults(sr, boType));
        else return -1;
    }

    public static FlexType lookupCurrencyConvTable() throws WTException {
        return FlexTypeCache.getFlexTypeFromPath(SMCostSheetConfig.FLEX_PATH);
    }

    public static SearchResults processExchangeRate(FlexType boType, Long seasonBranchId, String currency)
            throws WTException {
        String classNAME = "LCSLIFECYCLEMANAGED";
        final PreparedQueryStatement statement = new PreparedQueryStatement();
        statement.appendFromTable(LCSLifecycleManaged.class);
        statement.appendSelectColumn(SMCostSheetConfig.LCS_LIFECYCLE_MANAGED,
                boType.getAttribute(SMCostSheetConfig.BO_EXCHANGE_RATE).getColumnName());
        statement.appendCriteria(new Criteria(classNAME, boType.getAttribute(
                SMCostSheetConfig.BO_CURRENCY).getColumnName(), currency, Criteria.EQUALS));
        statement.appendAndIfNeeded();
        statement.appendCriteria(new Criteria(classNAME, boType.getAttribute(
                SMCostSheetConfig.BO_SEASON).getColumnName(), seasonBranchId.toString(),
                Criteria.EQUALS));
        return LCSQuery.runDirectQuery(statement);

    }

    public static String processExchangeRateResults(SearchResults results,
                                                    FlexType boType) throws WTException {
        final List<?> dataCollection = results.getResults();
        if (dataCollection.size() > 0) {
            for (Object obj1 : dataCollection) {
                final FlexObject fo = (FlexObject) obj1;
                return fo
                        .getString("LCSLIFECYCLEMANAGED."
                                + boType.getAttribute(SMCostSheetConfig.BO_EXCHANGE_RATE)
                                .getColumnName());
            }
        }
        return null;
    }

    public static double getTotalForBOMTable(LCSProductCostSheet lcsProductCostSheet, String skuMasterId, String productDestinationID, Date dateForMaterialPrice) throws WTException {
        SMBOMTable bomTable = getBOMTableCalculated(lcsProductCostSheet, skuMasterId, productDestinationID, dateForMaterialPrice);
        if ( SMCostSheetTypeSelector.isAPD(lcsProductCostSheet) )
            return SMCostSheetCalculateTotalGet.getCostSheetBOMTableTotalCalculated_APD(bomTable.getBOMTableMap().values(), true);
        if ( SMCostSheetTypeSelector.isFPD(lcsProductCostSheet) )
            return SMCostSheetCalculateTotalGet.getCostSheetBOMTableTotalCalculated_FPD(bomTable.getBOMTableMap().values(), true);
        if ( SMCostSheetTypeSelector.isSEPD(lcsProductCostSheet) )
            return SMCostSheetCalculateTotalGet.getCostSheetBOMTableTotalCalculated_SEPD(bomTable.getBOMTableMap().values(), true);
        throw new WTException("Type '" + FlexTypeCache.getFlexType(lcsProductCostSheet).getTypeName() + "' is NOT supported.");
    }

    public static Map updateCostSheetBOMTable(LCSProductCostSheet lcsProductCostSheet) {
        try {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.calculateCostSheet: Start.");

            //Сбрасываем атрибут DO_BOM_UPDATE в значение false
            lcsProductCostSheet = SMCostSheetWriter.resetBooleanValue(lcsProductCostSheet, SMCostSheetConfig.DO_BOM_UPDATE, false);
            //Set current date for smDateofBOMUpdate
            lcsProductCostSheet.setValue(SMCostSheetConfig.COST_SHEET_ATT_DATE_OF_BOM_UPDATE, new Date());

            String skuMasterId = "";
            String productDestinationID = "";
            Map csDimLinks = (Map) MethodContext.getContext().get("COSTSHEET_DIM_LINKS");
            if(csDimLinks != null)	{
                String repColorId = (String) csDimLinks.get("REPCOLOR"); // Colorway
                if(FormatHelper.hasContent(repColorId)) {
                    LCSSKU sku = (LCSSKU) LCSQuery.findObjectById(repColorId);
                    if (sku != null) {
                        skuMasterId = FormatHelper.getObjectId((WTPartMaster) sku.getMaster());
                    }
                }
                productDestinationID = (String) csDimLinks.get("REPDESTINATION"); // Destination
            } else {
                skuMasterId = SMCostSheetReader.getLCSSKUMasterID(lcsProductCostSheet);
                productDestinationID = SMCostSheetReader.getProductDestinationID(lcsProductCostSheet);
            }
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.calculateCostSheet: skuMasterId (" + skuMasterId + ")");
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.calculateCostSheet: productDestinationID (" + productDestinationID + ")");

            Date dateForPrice = SMCostSheetTypeSelector.getDateForPrice(lcsProductCostSheet);
            SMBOMTable bomTable = getBOMTableCalculated(lcsProductCostSheet, skuMasterId, productDestinationID, dateForPrice);

            // Set BOM ID (smBOMID) **********************************
            Long smBOMID = bomTable.getFlexBOMPartMasterObjectIdentifier();
            lcsProductCostSheet.setValue("smBOMID", smBOMID);
            // *******************************************************

            //Copying the values of the attributes filled in by the user

            String attributesNotUpdate = SMCostSheetTypeSelector.getSMCostSheetBOMTableAttsNotUpdate(lcsProductCostSheet);
            SMCostSheetUpdate.newSMCostSheetUpdateForBOMTable(lcsProductCostSheet).copyCurrentValuesForBOMTable(
                    attributesNotUpdate, bomTable.getBOMTableMap().values());

            SMCostSheetBuildTable.updateBOMTableMOAObjectCollection(lcsProductCostSheet, bomTable.getBOMTableMap());

            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.calculateCostSheet: Finish.");
            return bomTable.getBOMTableMap();

        } catch(Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetTools.calculateCostSheet: buildBOMTable - " + e.getMessage(), e);
            e.printStackTrace();
        }
        return new HashMap();
    }

    protected static SMBOMTable getBOMTableCalculated(LCSProductCostSheet lcsProductCostSheet, String skuMasterId, String productDestinationID, Date dateForMaterialPrice) {
        try {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.getCalculateCostSheet: Start.");

            FlexSpecification spec = null;
            if(lcsProductCostSheet.getSpecificationMaster() != null) {
                spec = (FlexSpecification)VersionHelper.latestIterationOf(lcsProductCostSheet.getSpecificationMaster());
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.getCalculateCostSheet: FlexSpecification (" + IxbHndHelper.getDisplayIdentityForIxb(spec) + ")");
            }

            String sourcingConfigMasterId = "";
            if(lcsProductCostSheet.getSourcingConfigMaster() != null) {
                sourcingConfigMasterId = FormatHelper.getObjectId(lcsProductCostSheet.getSourcingConfigMaster());
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.getCalculateCostSheet: SourcingConfigMaster (" + IxbHndHelper.getDisplayIdentityForIxb(lcsProductCostSheet.getSourcingConfigMaster()) + ")" +
                        ", sourcingConfigMasterId (" + sourcingConfigMasterId + ")");
            }

            // Sizing
            String size1 = FormatHelper.format(lcsProductCostSheet.getRepresentativeSize());
            String size2 = FormatHelper.format(lcsProductCostSheet.getRepresentativeSize2());

            Date timestamp = null;
            Map<FlexBOMPart, Collection> allBOMData = new HashMap<>();
            if(spec != null)
            {
                Collection specBOMs = FlexSpecQuery.getSpecComponents(spec,"BOM");
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.getCalculateCostSheet: - Collection SpecBOM size = " + specBOMs.size());
                String refBOMName = FormatHelper.format((String) lcsProductCostSheet.getValue(SMCostSheetConfig.BOM_REF_NAME_ATT));

                for (Object object : specBOMs)
                {
                    FlexBOMPart flexbompart = (FlexBOMPart) object;
                    FlexType bomType = flexbompart.getFlexType();
                    FlexType materialType = flexbompart.getFlexType().getReferencedFlexType(ReferencedTypeKeys.MATERIAL_TYPE);
                    LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.getCalculateCostSheet: 4digitsFix -" + refBOMName.split(":")[0] +"-" );
                    if(!"LABOR".equals(flexbompart.getBomType()) && refBOMName.split(":")[0].equals(flexbompart.getName().split(":")[0]))
                    {
                        Collection data = LCSFlexBOMQuery.findFlexBOMData(flexbompart, sourcingConfigMasterId, skuMasterId, size1, size2, productDestinationID,
                                LCSFlexBOMQuery.WIP_ONLY, timestamp, false, false, LCSFlexBOMQuery.ALL_APPLICABLE_TO_DIMENSION,
                                "", "", "", materialType).getResults();
                        Collection bomData = SMCostSheetSorter.groupDataToBranchId( LCSFlexBOMQuery.mergeDimensionBOM(data), bomType );
                        allBOMData.put(flexbompart, bomData);
                    }
                }
            }

            SMCostSheetBOMTableRow costSheetBOMTableRow = SMCostSheetTypeSelector.getBOMTableRowForType(lcsProductCostSheet, dateForMaterialPrice);
            Map result = SMCostSheetBuildTable.buildBOMTable(costSheetBOMTableRow, allBOMData);

            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.getCalculateCostSheet: Finish.");

            return SMBOMTable.newSMBOMTable(result, allBOMData.keySet());

        } catch(Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetTools.getCalculateCostSheet: Exception (" + e.getMessage() + ")", e);
            e.printStackTrace();
        }

        return SMBOMTable.newSMBOMTable(null, null);
    }

    /**
     * Clear manual values (for all department)
     */
    public static LCSProductCostSheet clearManualValues(LCSProductCostSheet lcsProductCostSheet, String bomTableName, Map<String, Object> attrsForClear) throws WTException {

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.clearManualValues: Start.");
        try
        {
            lcsProductCostSheet = SMCostSheetWriter.resetBooleanValue(lcsProductCostSheet, "smCleanBOMTable", false);

            LCSMOATable lcsmoaTable = (LCSMOATable) lcsProductCostSheet.getValue(bomTableName);
            if (lcsmoaTable != null)
            {
                Collection rows = lcsmoaTable.getRows();
                if (rows.size() > 0) {
                    Hashtable rowData = new Hashtable();
                    for (Object object : rows) {
                        FlexObject row = (FlexObject) object;
                        int id = FormatHelper.parseInt((String) row.get("ID"));
                        row.putAll(attrsForClear);
                        rowData.put("" + id, row);
                    }

                    SMCostSheetUpdate.newSMCostSheetUpdateForBOMTable(lcsProductCostSheet).copyIDsAndHiddenAttrsForBOMTable(
                            "BRANCHID", "ID", rowData.values());

                    // clear values
                    clearAllRowsMOATable(lcsProductCostSheet, bomTableName);

                    LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
                    moaLogic.updateMOAObjectCollection(lcsProductCostSheet, lcsProductCostSheet.getFlexType().getAttribute(
                            bomTableName), rowData);
                }
            }

        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetTools.clearManualValues: Exception (" + e.getMessage() + ")", e);
            e.printStackTrace();
        }

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.clearManualValues: Finish.");

        return lcsProductCostSheet;
    }

    /**
     * Clear manual values (for APD):
     * Display name 				| Internal Name
     * ---------------------------------------------------------------------------
     * Consumption from SM 		    | smConsumptionOVR
     * Loss, % from SM 			    | smLossOVR
     * Price (OVR) from Supplier 	| smPriceOVR
     * Price from SM 				| smPriceFromSM
     * CIF, % (Supplier) 			| smCIF
     * CIF, % from SM 				| smCIFFromSM
     * Comments-Supplier 			| smCommentsSupplier
     * Comments-SM 				    | smCommentsSM
     * Comments-PM 				    | smCommentsPM
     * Include in Purchase Price 	| smIncludeInPurchasePrice | Reset to "Yes"
     */
    public static LCSProductCostSheet clearManualValuesAPD(LCSProductCostSheet lcsProductCostSheet) throws WTException {

        String bomTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName( lcsProductCostSheet );

        Map<String, Object> attrsForClear = new HashMap<>();
        attrsForClear.put("smConsumptionOVR", 0);
        attrsForClear.put("smLossOVR", 0);
        attrsForClear.put("smPriceOVR", 0);
        attrsForClear.put("smPriceFromSM", 0);
        attrsForClear.put("smCIF", 0);
        attrsForClear.put("smCIFFromSM", 0);
        attrsForClear.put("smCommentsSupplier", "");
        attrsForClear.put("smCommentsPM", "");
        attrsForClear.put("smIncludeInPurchasePrice", true);

        return clearManualValues(lcsProductCostSheet, bomTableName, attrsForClear);
    }

    public static LCSProductCostSheet clearManualValuesFPD(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.clearManualValues: Start.");

        lcsProductCostSheet = SMCostSheetWriter.resetBooleanValue(lcsProductCostSheet, "smCleanBOMTable", false);

        Hashtable rowData = new Hashtable();
        String bomTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcsProductCostSheet);
        LCSMOATable bomTable = (LCSMOATable) lcsProductCostSheet.getValue(bomTableName);

        if (bomTable != null) {
            Collection rows = bomTable.getRows();
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.clearManualValues: bomTable.getRows().size(): " + rows.size());
            if (rows.size() > 0) {
                for (Object o : rows) {
                    FlexObject row = (FlexObject) o;
                    int id = FormatHelper.parseInt((String) row.get("ID"));
                    row.put("smPriceOVR", 0);
                    row.put("smCommentsSupplier", "");
                    rowData.put("" + id, row);

                    LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.clearManualValues: bomTable row: " + row);
                }

                SMCostSheetUpdate.newSMCostSheetUpdateForBOMTable(lcsProductCostSheet).copyIDsAndHiddenAttrsForBOMTable(
                        "BRANCHID", "ID", rowData.values());

                // clear values
                clearAllRowsMOATable(lcsProductCostSheet, bomTableName);

                LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
                moaLogic.updateMOAObjectCollection(lcsProductCostSheet,
                        lcsProductCostSheet.getFlexType().getAttribute(bomTableName), rowData);
            }
        }

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.clearManualValues: Finish.");

        return lcsProductCostSheet;
    }

    public static LCSProductCostSheet clearManualValuesSEPDMain(LCSProductCostSheet lcsProductCostSheet) {

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.clearManualValuesSEPDMain: Start.");

        try {
            lcsProductCostSheet = SMCostSheetWriter.resetBooleanValue(lcsProductCostSheet, "smCleanBOMTable", false);

            FlexObject row;
            Hashtable rowData = new Hashtable();
            String bomTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcsProductCostSheet);
            LCSMOATable bomTable = (LCSMOATable) lcsProductCostSheet.getValue(bomTableName);

            if (bomTable != null) {

                Collection bomTableRows = bomTable.getRows();
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.clearManualValuesSEPDMain: bomTable.getRows().size(): " + bomTableRows.size());

                if (bomTableRows.size() > 0) {
                    for (Object o : bomTableRows) {
                        row = (FlexObject) o;
                        int id = FormatHelper.parseInt((String) row.get("ID"));
                        row.put("smArticleUOMOVR", "");
                        row.put("smConsumptionOVR", 0);
                        row.put("smCommentsSupplier", "");
                        row.put("smIncludeInPurchasePrice", true);
                        rowData.put("" + id, row);

                        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.clearManualValuesSEPDMain: bomTable row: " + row);
                    }

                    SMCostSheetUpdate.newSMCostSheetUpdateForBOMTable(lcsProductCostSheet).copyIDsAndHiddenAttrsForBOMTable(
                            "BRANCHID", "ID", rowData.values());

                    // clear values
                    clearAllRowsMOATable(lcsProductCostSheet, bomTableName);

                    LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
                    moaLogic.updateMOAObjectCollection(lcsProductCostSheet, lcsProductCostSheet.getFlexType().getAttribute(
                            bomTableName), rowData);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetTools.clearManualValuesSEPDMain: Exception (" + e.getMessage() + ")", e);
            e.printStackTrace();
        }

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.clearManualValuesSEPDMain: Finish.");

        return lcsProductCostSheet;
    }

    public static LCSProductCostSheet clearManualValuesSEPDAlternate(LCSProductCostSheet lcsProductCostSheet) throws WTException, WTPropertyVetoException {
        LOGGER.info("CUSTOM>>>>>> SMCostSheetTools.clearManualValuesSEPDAlternate: Start.");

        lcsProductCostSheet = SMCostSheetWriter.resetBooleanValue(lcsProductCostSheet, "smCleanBOMTable", false);

        String bomAdditionalTableName = SMCostSheetTypeSelector.getSMCostSheetBOMAdditionalTableName(lcsProductCostSheet);
        for(Object obj : LCSMOAObjectQuery.findMOACollection(lcsProductCostSheet,
                lcsProductCostSheet.getFlexType().getAttribute(bomAdditionalTableName))) {
            LCSMOAObject lcsObj = (LCSMOAObject) obj;
            String section = (String) lcsObj.getValue("smSectionSEPD");
            if ( ! SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_ADDITIONAL_TABLE_NMC_SECTIONS.contains(section)) {
                PersistenceHelper.manager.delete(lcsObj);
                LOGGER.info("CUSTOM>>>>>> SMCostSheetTools.clearManualValuesSEPDAlternate: deleted: " + section);
            }
            else LOGGER.info("CUSTOM>>>>>> SMCostSheetTools.clearManualValuesSEPDAlternate: skipped: " + section);
        }

        LOGGER.info("CUSTOM>>>>>> SMCostSheetTools.clearManualValuesSEPDAlternate: Finish.");
        return lcsProductCostSheet;
    }

    public static String getRepColorway(LCSProductCostSheet lcscostsheet) throws WTException {
        Map csDimLinks = (Map) MethodContext.getContext().get("COSTSHEET_DIM_LINKS");
        String repColorId = (String) csDimLinks.get("REPCOLOR");
        if(FormatHelper.hasContent(repColorId)) {
            LCSSKU sku = (LCSSKU) LCSQuery.findObjectById(repColorId);
            if (sku != null) {
                return FormatHelper.getObjectId((WTPartMaster) sku.getMaster());
                    }
        }
        return "";
    }

    public static String getRepDestination(LCSProductCostSheet lcscostsheet) throws WTException {
        Map csDimLinks = (Map) MethodContext.getContext().get("COSTSHEET_DIM_LINKS");
        String productDestinationID = (String) csDimLinks.get("REPDESTINATION");
        if (FormatHelper.hasContent(productDestinationID)) {
            ProductDestination productDestination = (ProductDestination) LCSQuery.findObjectById(productDestinationID);
            if (productDestination != null) {
                return productDestinationID;
                }
        }
        return "";
    }

    public static void copyBOMTable(LCSProductCostSheet srcCostSheet, LCSProductCostSheet targetCostSheet) throws WTException {

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyBOMTable: Start.");

        try {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyBOMTable: source: " + IxbHndHelper.getDisplayIdentityForIxb(srcCostSheet));
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyBOMTable: target: " + IxbHndHelper.getDisplayIdentityForIxb(targetCostSheet));

            Hashtable rowData = new Hashtable();
            LCSMOATable bomTable = (LCSMOATable) srcCostSheet.getValue(SMCostSheetTypeSelector.getSMCostSheetBOMTableName(srcCostSheet));

            if (bomTable != null) {
                Collection rowsSource = bomTable.getRows();
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyBOMTable: source BOM Table size: " + rowsSource.size());
                if (rowsSource.size() > 0) {

                    for (Object obj : bomTable.getRows()) {
                        FlexObject row = (FlexObject) obj;
                        int id = FormatHelper.parseInt((String) row.get("ID"));
                        rowData.put("" + id, row);
                    }

                    SMCostSheetUpdate.newSMCostSheetUpdateForBOMTable(srcCostSheet).copyIDsAndHiddenAttrsForBOMTable(
                            "BRANCHID", "ID", rowData.values());

                    // clear values
                    String bomTableNameTarget = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(targetCostSheet);
                    clearAllRowsMOATable(targetCostSheet, bomTableNameTarget);

                    LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
                    moaLogic.updateMOAObjectCollection(targetCostSheet,
                            targetCostSheet.getFlexType().getAttribute(bomTableNameTarget), rowData);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetTools.copyBOMTable: Exception (" + e.getMessage() + ")", e);
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyBOMTable: Finish.");
    }

    public static void copyAlternateBOMTable(LCSProductCostSheet srcCostSheet, LCSProductCostSheet targetCostSheet) throws WTException {

        LOGGER.info("CUSTOM>>>>>> SMCostSheetTools.copyAlternateBOMTable: Start.");

        try {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyAlternateBOMTable: source: " + IxbHndHelper.getDisplayIdentityForIxb(srcCostSheet));
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyAlternateBOMTable: target: " + IxbHndHelper.getDisplayIdentityForIxb(targetCostSheet));

            Hashtable rowData = new Hashtable();
            LCSMOATable bomTable = (LCSMOATable) srcCostSheet.getValue(SMCostSheetTypeSelector.getSMCostSheetBOMAdditionalTableName(srcCostSheet));

            if (bomTable != null) {
                Collection rowsSource = bomTable.getRows();
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyAlternateBOMTable: source BOM Table size: " + rowsSource.size());
                if (rowsSource.size() > 0) {

                    for (Object obj : bomTable.getRows()) {
                        FlexObject row = (FlexObject) obj;
                        int id = FormatHelper.parseInt((String) row.get("ID"));
                        String section = (String) row.get("smSectionSEPD");
                        if (SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_ADDITIONAL_TABLE_NMC_SECTIONS.contains(section))
                            rowData.put("" + id, row);
                    }
                    // clear values
                    String bomTableNameTarget = SMCostSheetTypeSelector.getSMCostSheetBOMAdditionalTableName(targetCostSheet);
                    clearAllRowsMOATable(targetCostSheet, bomTableNameTarget);

                    LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
                    moaLogic.updateMOAObjectCollection(targetCostSheet,
                            targetCostSheet.getFlexType().getAttribute(bomTableNameTarget), rowData);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetTools.copyBOMTable: Exception (" + e.getMessage() + ")", e);
            e.printStackTrace();
        }

        LOGGER.info("CUSTOM>>>>>> SMCostSheetTools.copyAlternateBOMTable: Finish.");
    }

    public static SMProductCostSheet toSMProductCostSheet(WTObject wtobject) throws WTException {
        if(wtobject instanceof LCSProductCostSheet) {
            return SMProductCostSheet.newSMProductCostSheet( (LCSProductCostSheet) wtobject );
        } else {
            throw new WTException("CUSTOM>>>>>> SMCostSheetTools.toSMProductCostSheet: Object is not instance of LCSProductCostSheet");
        }
    }

    public static LCSProductCostSheet toLCSProductCostSheet(WTObject wtobject) throws WTException {
        if(wtobject instanceof LCSProductCostSheet) {
            return (LCSProductCostSheet) wtobject;
        } else {
            throw new WTException("CUSTOM>>>>>> SMCostSheetTools.toLCSProductCostSheet: Object is not instance of LCSProductCostSheet");
        }
    }

    public static LCSMOAObject toLCSMOAObject(WTObject wtobject) throws WTException {
        if(wtobject instanceof LCSMOAObject) {
            return  (LCSMOAObject) wtobject;
        } else {
            throw new WTException("CUSTOM>>>>>> SMCostSheetTools.toLCSMOAObject: Object is not instance of LCSMOAObject");
        }
    }

    public static void copyReferencedBOMName(LCSProductCostSheet srcCostSheet, LCSProductCostSheet targetCostSheet) {
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyReferencedBOMName: Start.");

        try {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyReferencedBOMName: source: " + IxbHndHelper.getDisplayIdentityForIxb(srcCostSheet));
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyReferencedBOMName: target: " + IxbHndHelper.getDisplayIdentityForIxb(targetCostSheet));

            String bomName = (String) srcCostSheet.getValue(com.sportmaster.wc.mc.config.SMCostSheetConfig.SM_REFERENCED_BOM_NAME_ATTRIBUTE);
            targetCostSheet.setValue(com.sportmaster.wc.mc.config.SMCostSheetConfig.SM_REFERENCED_BOM_NAME_ATTRIBUTE,bomName);
        } catch (WTException e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetTools.copyReferencedBOMName: Exception (" + e.getMessage() + ")", e);
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyReferencedBOMName: End.");
    }

    /**
     * Copy BOM Table and Referenced BOM Name
     * @param srcCostSheet - copied CostSheet
     * @param targetCostSheet - new CostSheet
     */
    public static void copyBOMTableAndReferencedBOMName(LCSProductCostSheet srcCostSheet, LCSProductCostSheet targetCostSheet) {

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyBOMTableAndReferencedBOMName: Start.");

        try {
            String sourceRefBOMName = SMFormatHelper.format((String) srcCostSheet.getValue(com.sportmaster.wc.mc.config.SMCostSheetConfig.SM_REFERENCED_BOM_NAME_ATTRIBUTE));
            String targetRefBOMName = SMFormatHelper.format((String) targetCostSheet.getValue(com.sportmaster.wc.mc.config.SMCostSheetConfig.SM_REFERENCED_BOM_NAME_ATTRIBUTE));

            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyBOMTableAndReferencedBOMName: copyRefBOMName - String sourceRefBOMName =  " + sourceRefBOMName);
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyBOMTableAndReferencedBOMName: copyRefBOMName - String targetRefBOMName =  " + targetRefBOMName);

            boolean doBOMUpdate = SMFormatHelper.getBoolean(targetCostSheet.getValue(com.sportmaster.wc.mc.config.SMCostSheetConfig.SM_DO_BOM_UPDATE_ATTRIBUTE));
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyBOMTableAndReferencedBOMName: copyRefBOMName - Boolean doBOMUpdate =  " + doBOMUpdate);

            boolean emptyTargetRefBOMName = targetRefBOMName.equals("");
            if (sourceRefBOMName.equals(targetRefBOMName)) {
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyBOMTableAndReferencedBOMName: copyRefBOMName - variant A , do nothing ");
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyBOMTableAndReferencedBOMName: run copyBOMTable() ...");
                copyBOMTable(srcCostSheet, targetCostSheet);
            } else if (emptyTargetRefBOMName) {
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyBOMTableAndReferencedBOMName: copyRefBOMName - variant C , copying refBOMName ");
                copyReferencedBOMName(srcCostSheet, targetCostSheet);
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyBOMTableAndReferencedBOMName: run copyBOMTable() ...");
                copyBOMTable(srcCostSheet, targetCostSheet);
            } else {
                if (!doBOMUpdate) {
                    LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyBOMTableAndReferencedBOMName: copyRefBOMName - variant B , deleting CS BOM Table ");
                    deleteAllRowsMOATable(targetCostSheet, SMCostSheetTypeSelector.getSMCostSheetBOMTableName(targetCostSheet));
                    //com.sportmaster.wc.mc.config.SMCostSheetConfig.SM_COSTSHEET_BOM_TABLE_APD_ATTRIBUTE);
                    //targetCostSheet.setValue(com.sportmaster.wc.mc.config.SMCostSheetConfig.SM_DO_BOM_UPDATE_ATTRIBUTE, false);
                } /* else {
                LOGGER.info("CUSTOM>>>>>> SMCostSheetTools.copyBOMTableAndReferencedBOMName: copyRefBOMName - variant D , copying refBOMName ");
                SMCostSheetTools.copyReferencedBOMName(productCostSheet.getSourceCostSheet(), productCostSheet.getLCSProductCostSheet());
            } */
            }
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetTools.copyBOMTableAndReferencedBOMName: Exception (" + e.getMessage() + ")", e);
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copyBOMTableAndReferencedBOMName: End.");
    }

    /**
     * Set smCSStatusAPD
     * @param targetCostSheet - target CostSheet
     */
    public static void setSMCSStatus_APD(LCSProductCostSheet targetCostSheet) {

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.setSMCSStatus_APD: Start.");

        try {
            String targetSMCSStatusAPD = FormatHelper.format( (String) targetCostSheet.getValue( "smCSStatusAPD" ) );
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.setSMCSStatus_APD: smCSStatusAPD = '" + targetSMCSStatusAPD + "'");
            if ( targetSMCSStatusAPD.isEmpty() ) {
                targetCostSheet.setValue("smCSStatusAPD", "smInWork");
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.setSMCSStatus_APD: smInWork (set)=>> smCSStatusAPD ");
            } else {
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.setSMCSStatus_APD: smCSStatusAPD is not Empty");
            }
        } catch (WTException e) {
            e.printStackTrace();
        }

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.setSMCSStatus_APD: End.");
    }

    /**
     * Copy smPMsCommentAPD
     * @param srcCostSheet - source CostSheet
     * @param targetCostSheet - target CostSheet
     */
    public static void copySMPMsComment_APD(LCSProductCostSheet srcCostSheet, LCSProductCostSheet targetCostSheet) {

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copySMPMsComment_APD: Start.");

        try {
            String srcSMPMsCommentAPD = FormatHelper.format( (String) srcCostSheet.getValue( "smPMsCommentAPD" ) );
            String targetSMPMsCommentAPD = FormatHelper.format( (String) targetCostSheet.getValue( "smPMsCommentAPD" ) );
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copySMPMsComment_APD: smPMsCommentAPD: src:'" + srcSMPMsCommentAPD + "' ==>> target:'" + targetSMPMsCommentAPD + "'");
            if ( targetSMPMsCommentAPD.isEmpty() ) {
                targetCostSheet.setValue("smPMsCommentAPD", srcSMPMsCommentAPD);
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copySMPMsComment_APD: " + srcSMPMsCommentAPD +  " (set)=>> smPMsCommentAPD ");
            } else {
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copySMPMsComment_APD: smCSStatusAPD is not Empty");
            }
        } catch (WTException e) {
            e.printStackTrace();
        }

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetTools.copySMPMsComment_APD: End.");
    }
}