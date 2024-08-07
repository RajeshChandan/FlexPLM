package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.product.ReferencedTypeKeys;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.mc.config.SMCostSheetMOATableConfig;
import com.sportmaster.wc.mc.tools.SMFormatHelper;
import org.apache.log4j.Logger;
import wt.ixb.publicforhandlers.IxbHndHelper;
import wt.util.WTException;

import java.util.*;

public class SMCostSheetBuildTable {

    private static final Logger LOGGER = Logger.getLogger(SMCostSheetBuildTable.class);

    public static void updateBOMSectionTotalsTableMOAObjectCollection(LCSProductCostSheet lcsProductCostSheet) throws WTException {

        Collection bomTableRows = SMFormatHelper.getLCSMOATableRows( SMCostSheetTypeSelector.getSMCostSheetBOMTable( lcsProductCostSheet ) );
        Collection bomAdditionalTableRows = SMFormatHelper.getLCSMOATableRows( SMCostSheetTypeSelector.getSMCostSheetBOMAdditionalTable( lcsProductCostSheet ) );
        updateBOMSectionTotalsTableMOAObjectCollection(lcsProductCostSheet, bomTableRows, bomAdditionalTableRows);
    }

    public static void updateBOMSectionTotalsTableMOAObjectCollection(LCSProductCostSheet lcsProductCostSheet, Collection bomTableRows, Collection bomAdditionalTableRows) throws WTException {

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetBuildTable.updateBOMSectionTotalsTableMOAObjectCollection: Start.");
        String bomSectionsTotalTableName = SMCostSheetTypeSelector.getSMCostSheetBOMSectionsTotalTableName( lcsProductCostSheet );
        SMCostSheetTools.clearAllRowsMOATable( lcsProductCostSheet, bomSectionsTotalTableName );

        Hashtable sectionTotalsTableHash = buildBOMSectionTotalsTable( lcsProductCostSheet, bomTableRows, bomAdditionalTableRows, true );

        LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
        moaLogic.updateMOAObjectCollection( lcsProductCostSheet, lcsProductCostSheet.getFlexType().getAttribute( bomSectionsTotalTableName ), sectionTotalsTableHash );

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetBuildTable.updateBOMSectionTotalsTableMOAObjectCollection: Start.");
    }

    public static Hashtable buildBOMSectionTotalsTable(LCSProductCostSheet lcsProductCostSheet, Collection bomTableRows, Collection bomAdditionalTableRows, boolean recalculatedTotal) throws WTException {

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetBuildTable.buildBOMSectionTotalsTable: Start.");

        Hashtable result = new Hashtable();

        List<String> sectionsIgnore = new ArrayList<>();
        List<String> sectionsIgnore_LaborOverheadProfit = new ArrayList<>();
        sectionsIgnore_LaborOverheadProfit.addAll( SMCostSheetConfig.getValues( SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_SEPD ) );
        sectionsIgnore.addAll(sectionsIgnore_LaborOverheadProfit);
        //TODO: может достаточно одного значения и список не нужен?
        sectionsIgnore.addAll( SMCostSheetConfig.getValues( SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_CS_INCOTERMS_DDP_SEPD ) );

        List<FlexObject> rowsInBOMTable = SMCostSheetFilter.filter( bomTableRows,
                "smIncludeInPurchasePrice", true );
        List<FlexObject> rowsInBOMAdditionalTable = SMCostSheetFilter.filter( bomAdditionalTableRows,
                "smIncludeInPurchasePrice", true );

        rowsInBOMTable = SMCostSheetFilter.filterExclude( rowsInBOMTable,
                "smSectionSEPD", sectionsIgnore );

        rowsInBOMAdditionalTable = SMCostSheetFilter.filterExclude( rowsInBOMAdditionalTable,
                "smSectionSEPD", sectionsIgnore_LaborOverheadProfit );

        List<FlexObject> rowsInBOMTableResult = new ArrayList<>();
        List<FlexObject> rowsInBOMAdditionalTableResult = new ArrayList<>();

        for (FlexObject flexObject_BOMAdditionalTable : rowsInBOMAdditionalTable) {
            String smSectionSEPD_BOMAdditionalTable = FormatHelper.format( flexObject_BOMAdditionalTable.getData("smSectionSEPD") );
            boolean found = false;
            for (Object row : bomTableRows) {
                FlexObject flexObject_BOMTable = (FlexObject) row;
                String smSectionSEPD_BOMTable = FormatHelper.format( flexObject_BOMTable.getData("smSectionSEPD") );
                if ( smSectionSEPD_BOMAdditionalTable.equals( smSectionSEPD_BOMTable ) ) {
                    rowsInBOMTableResult.add( flexObject_BOMAdditionalTable );
                    found = true;
                    break;
                }
            }
            if ( !found ) {
                rowsInBOMAdditionalTableResult.add( flexObject_BOMAdditionalTable );
            }
        }

        rowsInBOMTableResult.addAll(rowsInBOMTable);

        Map<String, Double> listTotalCalculatedBOMSections = SMCostSheetCalculateTotalGet.getCostSheetSectionsTotalCalculated_SEPD(
                rowsInBOMTableResult, recalculatedTotal );
        Map<String, Double> listTotalCalculatedAdditionalTableSections = SMCostSheetCalculateTotalGet.getCostSheetSectionsTotalCalculated_SEPD(
                rowsInBOMAdditionalTableResult, recalculatedTotal );

        FlexType flexType = SMCostSheetTypeSelector.getSMCostSheetBOMSectionsTotalTableType(lcsProductCostSheet);
        /*  smBOMSectionName - single list, Global Enumeration Used: GE SEPD BOMLink Section
            smBOMSectionTotal - float(4) - сумма по конкретной секции
            smBOMSectionPercentOfPP - float(2) - доля в процентах тотала конкретной секции от значения в атрибуте smPurchasePriceCalcContractCur
         */

        int index = 0;
        for (String internalName : flexType.getAttribute("smBOMSectionName").getAttValueList().getKeys()) {
            Double smBOMSectionTotal = listTotalCalculatedBOMSections.get(internalName);
            if (smBOMSectionTotal != null) {
                FlexObject newFlexObject = new FlexObject();
                String id = Integer.toString( index++ );
                newFlexObject.put("smBOMSectionName", internalName);
                newFlexObject.put("smBOMSectionTotal", smBOMSectionTotal);
                newFlexObject.put("smBOMSectionPercentOfPP", SMCostSheetCalculate.getSMBOMSectionPercentOfPP_SEPD( lcsProductCostSheet, smBOMSectionTotal ));
                newFlexObject.put("sortingNumber", id);
                result.put(id, newFlexObject);
            }
        }

        for (Map.Entry<String, Double>  entry : listTotalCalculatedAdditionalTableSections.entrySet()) {
            FlexObject newFlexObject = new FlexObject();
            String id = Integer.toString( index++ );
            newFlexObject.put("smBOMSectionName", entry.getKey());
            newFlexObject.put("smBOMSectionTotal", entry.getValue());
            newFlexObject.put("smBOMSectionPercentOfPP", SMCostSheetCalculate.getSMBOMSectionPercentOfPP_SEPD( lcsProductCostSheet, entry.getValue() ));
            newFlexObject.put("sortingNumber", id);
            result.put(id, newFlexObject);
        }

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetBuildTable.buildBOMSectionTotalsTable: Finish.");

        return result;
    }

    protected static Map buildBOMTable(SMCostSheetBOMTableRow costSheetBOMTableRow, Map<FlexBOMPart, Collection> bomData)
            throws Exception
    {
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetBuildTable.buildBOMTable: ()");

        Map bomHash = new HashMap();

        int indexSort = 0;
        int nextID = 0;
        for (Map.Entry<FlexBOMPart, Collection> bomDataEntry: bomData.entrySet())
        {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetBuildTable.buildBOMTable: FlexBOMPart (" + IxbHndHelper.getDisplayIdentityForIxb(bomDataEntry.getKey()) + ")");

            FlexType bomType = bomDataEntry.getKey().getFlexType();
            FlexType materialType = bomType.getReferencedFlexType(ReferencedTypeKeys.MATERIAL_TYPE);
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetBuildTable.buildBOMTable: getFlexTypeRootByClass (FlexBOMLink: " + bomType.toString() + ")");

            Collection data = bomDataEntry.getValue();
            if (data != null)
            {
                for (Object obj : data)
                {
                    FlexObject bomObj = (FlexObject) obj;
                    //LOGGER.debug("CUSTOM>>>>>> SMCostSheetBuildTable.buildBOMTable: " + bomObj.toString());

                    FlexObject newBOMObj = costSheetBOMTableRow.getRow(bomObj, bomType, materialType, indexSort);
                    if (newBOMObj == null)
                        continue;

                    /// CODE TO UNIQUELY IDENTIFY ROW
                    Object branchID = bomObj.get("FLEXBOMLINK.BRANCHID");
                    int currentID = FormatHelper.parseInt((String) branchID);
                    if (currentID > nextID) nextID = currentID;
                    bomHash.put(branchID, newBOMObj);

                    indexSort++;
                }
            }
        }

        bomHash.putAll( costSheetBOMTableRow.getAdditionalRows( ++nextID, indexSort ) );

        return bomHash;
    }

    protected static FlexObject getFlexObject(FlexObject bomObj, String filterAttrs, FlexType bomType, FlexType materialType, FlexType supplierType, FlexType materialSupplierType) throws WTException {
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetBuildTable.getFlexObject: ()");

        FlexObject newBOMObj = new FlexObject();

        StringTokenizer st = new StringTokenizer(filterAttrs, ",");
        while (st.hasMoreTokens())
        {
            String attName = st.nextToken();
            String attKey = null;
            if (attName.indexOf(".") > 0) attKey = attName.substring(attName.indexOf(".") + 1, attName.length());
            else attKey = attName;

            //MATERIAL, SUPPLIER, MATERIALSUPPLIER and other prefixes params of property 'com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.BOMAtts'
            FlexTypeAttribute att = null;
            if (attName.startsWith("MATERIAL.")) att = materialType.getAttribute(attKey);
            else if (attName.startsWith("SUPPLIER.")) att = supplierType.getAttribute(attKey);
            else if(attName.startsWith("MATERIALSUPPLIER.")) {
                att = materialSupplierType.getAttribute(attKey);
                LOGGER.debug("CUSTOM>>>>>> SMCostSheetBuildTable.getFlexObject: [attName]: " + attName + ", [Value]: " + (String) bomObj.get(att.getSearchResultIndex()));
            }
            else att = bomType.getAttribute(attKey);

            String attValue = (String) bomObj.get(att.getSearchResultIndex());
            String attDisplay = null;
            if (att.getAttVariableType().equals("integer") || att.getAttVariableType().equals("float") || att.getAttVariableType().equals("currency")) {
                attDisplay = attValue;
            } else {
                attDisplay = att.getDisplayValue(attValue);
                attDisplay = attDisplay.replaceAll("&nbsp;", " ");
            }
            //   LOGGER.debug("CUSTOM>>>>>> CostSheetBOMPlugin.calculateCostSheet: getRowPrice = "+  getRowPrice(bomPart,bomObj,new Date()));
            newBOMObj.put(attKey, attDisplay);
        }

        return newBOMObj;
    }

    public static void updateBOMTableMOAObjectCollection(LCSProductCostSheet lcsProductCostSheet, Map bomHash) throws WTException {
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetBuildTable.updateBOMTableMOAObjectCollection: ()");
        LCSMOATable moa = (LCSMOATable) lcsProductCostSheet.getValue(SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcsProductCostSheet));
        if(moa != null)
        {
            Hashtable rowData = new Hashtable();
            int index = 0;
            for (Object object : moa.getRows())
            {
                FlexObject row = (FlexObject) object;
                int id = FormatHelper.parseInt((String) row.get("ID"));
                boolean override = FormatHelper.parseBoolean(row.getString("OVERRIDE"));
                String branchID = (String) row.get("BRANCHID");

                if (bomHash.get(branchID) == null || !FormatHelper.hasContent(branchID)) {
                    // Remove extra rows
                    if (!override) {
                        row.put("DROPPED", "true");
                    }
                } else {
                    // Update existing row
                    if (!override) {
                        putRow(bomHash, branchID, row);
                    }
                    bomHash.remove(branchID);
                }
                row.put("ID", "" + id);
                rowData.put("" + id, row);
                if (id > index) index = id;
            }

            // Add new section rows from BOM
            for (Object object : bomHash.keySet()) {
                index++;
                String branchID = (String) object;
                FlexObject row = new FlexObject();
                row.put("BRANCHID", branchID);

                putRow(bomHash, branchID, row);

                row.put("OVERRIDE", "false");
                row.put("DROPPED", "false");
                row.put("ID", "" + index);
                rowData.put("" + index, row);
            }

            LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
            String costSheetBOMTableName = SMCostSheetTypeSelector.getSMCostSheetBOMTableName(lcsProductCostSheet);
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetBuildTable.updateBOMTableMOAObjectCollection: call updateMOAObjectCollection ( MOA attr = " + costSheetBOMTableName + ") ...");
            moaLogic.updateMOAObjectCollection( lcsProductCostSheet, lcsProductCostSheet.getFlexType().getAttribute(costSheetBOMTableName), rowData );
        }
    }

    private static void putRow(Map bomHash, String branchID, FlexObject row) {
        FlexObject newBOMObj = (FlexObject) bomHash.get(branchID);

        Collection attKeys = newBOMObj.getIndexes();
        Iterator attKeysIter = attKeys.iterator();
        while(attKeysIter.hasNext()) {
            String attKey = (String) attKeysIter.next();
            String attDisplay = (String) newBOMObj.get(attKey);
            row.put(attKey.toUpperCase(),attDisplay);
        }
    }
}
