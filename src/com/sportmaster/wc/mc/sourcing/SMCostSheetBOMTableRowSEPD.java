package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.mc.SMMaterialPrice;
import com.sportmaster.wc.mc.config.SMCostSheetMOATableConfig;
import com.sportmaster.wc.mc.tools.SMFormatHelper;
import org.apache.log4j.Logger;
import wt.util.WTException;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SMCostSheetBOMTableRowSEPD extends SMCostSheetBOMTableRow {
    private static final Logger LOGGER = Logger.getLogger(SMCostSheetBOMTableRowSEPD.class);

    private LCSProductCostSheet lcsProductCostSheet = null;
    private FlexType supplierType = null;
    private FlexType materialSupplierType = null;
    private Date dateForMaterialPrice = null;

    private SMCostSheetBOMTableRowSEPD() {}

    public static SMCostSheetBOMTableRow newSMCostSheetBOMTableRowSEPD(LCSProductCostSheet lcsProductCostSheet, Date dateForMaterialPrice) throws WTException {
        SMCostSheetBOMTableRowSEPD obj = new SMCostSheetBOMTableRowSEPD();
        obj.lcsProductCostSheet = lcsProductCostSheet;
        obj.supplierType = FlexTypeCache.getFlexTypeFromPath(SMCostSheetConfig.SUPPLIER_TYPE);
        obj.materialSupplierType = FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.material.LCSMaterialSupplier");
        obj.dateForMaterialPrice = dateForMaterialPrice;
        return obj;
    }

    public FlexObject getRow(FlexObject bomObj, FlexType bomType, FlexType materialType, int sortingNumberRow) throws WTException {

        FlexObject newBOMObj = SMCostSheetBuildTable.getFlexObject(bomObj, SMCostSheetConfig.BOM_ATTS, bomType, materialType, supplierType, materialSupplierType);

        // BOM Link
        // materialInfo[0] - material name, materialInfo[1] - branch id
        Object[] materialInfo = SMCostSheetReader.getMaterialNameAndBranchId(bomObj, bomType);
        String color = FormatHelper.format( bomObj.getData(bomType.getAttribute("colorDescription").getSearchResultIndex()) ); //Color - как в FPD
        Long colorBranchId = SMCostSheetReader.getColorBranchId(bomObj);
        String ptcbomPartMarkUp = FormatHelper.format(bomObj.getData(bomType.getAttribute("ptcbomPartMarkUp").getSearchResultIndex()));
        // Supplier
        //String supplier = FormatHelper.format(bomObj.getData(supplierType.getAttribute("name").getSearchResultIndex())); //smSupplierShortName
        // supplierInfo[0] - supplier name, supplierInfo[1] - branch id
        Object[] supplierInfo = SMCostSheetReader.getSupplierNameAndBranchId(bomObj);

        //String sectionSEPD = FormatHelper.format(bomObj.getData(bomType.getAttribute("section").getSearchResultIndex()));
        //LOGGER.info("CUSTOM>>>>>> SMCostSheetBOMTableRowSEPD.getRow: 1: " +
        //        "section='" + sectionSEPD + "', materialName='" + materialName + "', color='" + color + "', supplier='" + supplier + "'");

        //Ignore empty rows.
        if ( SMFormatHelper.isEmpty( (String) materialInfo[0] ) )
            return null;
        //Ignore marked for deletion.
        if (ptcbomPartMarkUp.equalsIgnoreCase("delete"))
            return null;

        // BOM Link
        /*
        Consumption OVR - вносится поставщиком
        Nominated price (Contract Currency) - расчет по формуле = Nominated Price* Contract Currency Rate
        Price OVR (Contract Currency) - вносится поставщиком как в FPD
        Total (Contract Currency) - расчет по формуле: = Price OVR  (Contract Currency) * Consumption (1+Loss/100), а если Price OVR  (Contract Currency) пустая - то = (Nominated Price*Contract currency) * Consumption (1+Loss/100)
        Include in BOM total - boolean, вносится ПМом, по умолчанию в первой таблице все галочки = Yes
        Percentage from Total Price - расчет по формуле: = Total (Contract currency) / Purchase Price (Calc.) - Формула в java. Проверка Purchase Price (Calc.) не 0.
        Description => Description на Material
        Size - BOM link attr
        Comments - Supplier - вносится поставщиком

        GoM - GoM на Material
        Managing Department - с материал-поставщика
        */
        String section = FormatHelper.format(bomObj.getData(bomType.getAttribute("section").getSearchResultIndex())); //Section => как в FPD => BOM link attr
        String smBOMLinkBOMSectionL2 = FormatHelper.format(bomObj.getData(bomType.getAttribute("smBOMLinkBOMSectionL2").getSearchResultIndex())); //BOM Section - L2 => smBOMLinkBOMSectionL2 => BOM link attr
        String smBOMLinkBOMSectionL3 = FormatHelper.format(bomObj.getData(bomType.getAttribute("smBOMLinkBOMSectionL3").getSearchResultIndex())); //BOM Section - L3 => smBOMLinkBOMSectionL3 = > BOM link attr
        String placement = FormatHelper.format(bomObj.getData(bomType.getAttribute("partName").getSearchResultIndex())); //Placement => как в FPD => BOM link attr
        double consumption = FormatHelper.parseDouble(bomObj.getData(bomType.getAttribute("quantity").getSearchResultIndex())); //Consumption - как в FPD
        double loss = FormatHelper.parseDouble(bomObj.getData(bomType.getAttribute("lossAdjustment").getSearchResultIndex())); //Loss % - как в FPD
        //String smMaterialSupplierOVR = FormatHelper.format(bomObj.getData(bomType.getAttribute("smMaterialSupplierOVR").getSearchResultIndex())); //Material Supplier - как в FPD
        //String smSuppliersUOMOVR = FormatHelper.format(bomObj.getData(bomType.getAttribute("smSuppliersUOMOVR").getSearchResultIndex())); //UOM OVR - вносится поставщиком
        //String smBOMLinkSupplierComments = FormatHelper.format(bomObj.getData(bomType.getAttribute("smBOMLinkSupplierComments").getSearchResultIndex()));
        String vrdSize = FormatHelper.format(bomObj.getData(bomType.getAttribute("vrdSize").getSearchResultIndex()));
        String vrdLinkComments = FormatHelper.format(bomObj.getData(bomType.getAttribute("vrdLinkComments").getSearchResultIndex()));

        String unitOfMeasure = "";
        String nominated = "";
        String vrdSupplierMaterialRefNumber = "";

        if (!SMCostSheetReader.isTextMaterial(bomObj, bomType)) {
            // Material
            unitOfMeasure = FormatHelper.format(bomObj.getData(materialType.getAttribute("unitOfMeasure").getSearchResultIndex())); //UOM - как в FPD
            // Material Supplier
            nominated = FormatHelper.format(bomObj.getData(materialSupplierType.getAttribute("smMsNominated").getSearchResultIndex())); //Nominated - как в FPD
            vrdSupplierMaterialRefNumber = FormatHelper.format(bomObj.getData(materialSupplierType.getAttribute("vrdSupplierMaterialRefNumber").getSearchResultIndex())); //Supplier Material Ref. No. => - => с материал-поставщика
        }
        //Material
        //String vrdDescription = FormatHelper.format(bomObj.getData(materialType.getAttribute("vrdDescription").getSearchResultIndex()));
        String vrdComments = FormatHelper.format(bomObj.getData(materialType.getAttribute("vrdComments").getSearchResultIndex()));
        String smMaterialGroupOfMerchandise = FormatHelper.format(bomObj.getData(materialType.getAttribute("smMaterialGroupOfMerchandise").getSearchResultIndex()));
        //Material Supplier
        String smManagingDepartment = FormatHelper.format(bomObj.getData(materialSupplierType.getAttribute("smManagingDepartment").getSearchResultIndex()));

        //BOM Link.Branch ID
        long bomLinkBranchId = bomObj.getLong("FLEXBOMLINK.BRANCHID");
        //Row ID
        String dimensionId = FormatHelper.format(bomObj.getData( "FLEXBOMLINK.DIMENSIONID" ));
        //String smRowID = dimensionId + "|~*~|" + materialName + "|~*~|" + color + "|~*~|" + supplier;
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetBOMTableRowSEPD.getRow: smRowID = " + dimensionId);

        //Material Price
        SMMaterialPrice smMaterialPrice = getRowPrice(bomObj, bomType, dateForMaterialPrice);
        double smMSPrice = 0D;
        String smMSCurrency = "";
        if (smMaterialPrice.getPriceLC() > 0D) {
            smMSPrice = smMaterialPrice.getPriceLC();
            smMSCurrency = smMaterialPrice.getLocalCurrency();
        }
        else if (smMaterialPrice.getPrice() > 0D) {
            smMSPrice = smMaterialPrice.getPrice();
            smMSCurrency = "vrdUsd";
        }
        //LOGGER.debug("CUSTOM>>>>>> SMCostSheetBOMTableRowSEPD.getRow: smMSPrice = " + smMSPrice + " " + smMSCurrency);

        double nominatedPriceCC = SMCostSheetCalculate.getSMPriceCC(lcsProductCostSheet, smMSPrice, smMSCurrency);

        //double smTotal = SMCostSheetCalculate.getSMTotalBOMTableSEPD(smPriceCC, smPriceOVR, consumption, loss);

        /*
        Type - Multi-Object\\Cost Sheet BOM\\Sports Equipment
        *-* Section SEPD ------------------------ smSectionSEPD (String - Global Enumeration Used: GE SEPD BOMLink Section)
        *+* Section Level 2 --------------------- smSectionLevel2 (String)
        *+* Section Level 3 --------------------- smSectionLevel3 (String)
        *-* Placement --------------------------- smPlacement (String)
        *-* Material ---------------------------- smMaterial (String)
        *-* Supplier Material Ref No. ----------- smSupplierMaterialRefNo (String)
        *+* Description ------------------------- smMaterialDescription (String)
        *-* Color ------------------------------- smColor (String)
        *-* Nominated? -------------------------- smNominated (String - Enumerated Value List: Yes/smYes, No/smNo)
        *-* Material Supplier ------------------- smSupplier (String)
        *-* UOM --------------------------------- smArticleUOM (String - Global Enumeration Used: UOM)
        *+* UOM OVR ----------------------------- smArticleUOMOVR (String - Global Enumeration Used: UOM) --- *Manual input*
        *-* Consumption ------------------------- smConsumption (Float)
        *?* Consumption OVR --------------------- smConsumptionOVR (Float) ---------------------------------- *Manual input*
        *-* Loss % ------------------------------ smLoss (Float)
        *+* Nominated price (Contract Currency) - smPriceCC (Float)
        *-* Price OVR (Contract Currency) ------- smPriceOVR (Float) ---------------------------------------- *Manual input*
        *?* Total (Contract Currency) ----------- smTotal (Float)
        *?* Include in PP ----------------------- smIncludeInPurchasePrice  (Include in BOM Total (smIncludeInBomTotal)) (Boolean)
        *+* % of PP ----------------------------- smPercentageOfPurchasePrice (Persentage From Total Price (smPercentageFromTotalPrice)) (Float)
        *+* Size -------------------------------- smSize (String)
        *+* Comments - Supplier ----------------- smCommentsSupplier (String) ------------------------------- *Manual input*
        *+* GoM --------------------------------- smMaterialGoM (String)
        *+* Managing Department ----------------- smManagingDepartment (String)

        Comments - SM ----------------------- smCommentsSM ---------------------------------------------- *Manual input*
         */

        newBOMObj.put("smSectionSEPD", section); //smSectionSEPD -  Global Enumeration Used - Single List
        //newBOMObj.put("smSectionLevel2", SMCostSheetReader.getDisplayValue(bomType,"smBOMLinkBOMSectionL2", smBOMLinkBOMSectionL2));
        //newBOMObj.put("smSectionLevel3", SMCostSheetReader.getDisplayValue(bomType,"smBOMLinkBOMSectionL3", smBOMLinkBOMSectionL3));
        newBOMObj.put("smSectionLevel2", smBOMLinkBOMSectionL2);
        newBOMObj.put("smSectionLevel3", smBOMLinkBOMSectionL3);
        newBOMObj.put("smPlacement", placement); //smPlacement
        newBOMObj.put("smMaterial", materialInfo[0]); //smMaterial
        newBOMObj.put("smSupplierMaterialRefNo", vrdSupplierMaterialRefNumber); //smSupplierMaterialRefNo
        //newBOMObj.put("smMaterialDescription", vrdDescription); //smMaterialDescription
        newBOMObj.put("smCommentsMaterial", vrdComments);
        newBOMObj.put("smColor", color); //smColor
        newBOMObj.put("smNominated", nominated); //smNominated - Enumerated Value List
        newBOMObj.put("smSupplier",  supplierInfo[0]); //smSupplier
        newBOMObj.put("smArticleUOM", unitOfMeasure); //smArticleUOM
        newBOMObj.put("smConsumption", consumption); //smConsumption
        newBOMObj.put("smLoss", loss); //smLoss
        newBOMObj.put("smNomPriceCCHidden", nominatedPriceCC); // smPriceCC = Nom. price (CC) (smPriceCC) = Nominated price (Contract Currency) * Contract Currency Rate
        //newBOMObj.put("smTotal", smTotal); //smTotal
        //newBOMObj.put("smPercentageOfPurchasePrice", SMCostSheetCalculate.getSMPurchasePriceCalcSEPD(lcsProductCostSheet, smTotal));
        newBOMObj.put("smSize", vrdSize);
        newBOMObj.put("smMaterialGoM", SMCostSheetReader.getDisplayValueForMultiEntry(materialType, "smMaterialGroupOfMerchandise", smMaterialGroupOfMerchandise));
        newBOMObj.put("smManagingDepartment", SMCostSheetReader.getDisplayValue(materialSupplierType,"smManagingDepartment", smManagingDepartment));
        newBOMObj.put("smCommentsSM", vrdLinkComments);
        newBOMObj.put("smIncludeInPurchasePrice", true);

        newBOMObj.put("smNomPriceHidden", smMSPrice);
        newBOMObj.put("smNomPriceCurrHidden", smMSCurrency);

        if (materialInfo[1] != null) newBOMObj.put("smMaterialID", materialInfo[1]); //smMaterialID
        if (colorBranchId != null) newBOMObj.put("smColorID", colorBranchId); //smColorID
        if (supplierInfo[1] != null) newBOMObj.put("smSupplierID",  supplierInfo[1]); //smSupplierID

        newBOMObj.put("smBOMLinkID", bomLinkBranchId);
        newBOMObj.put("smRowID", dimensionId); //smRowID
        newBOMObj.put("sortingNumber", "" + sortingNumberRow);

        return newBOMObj;
    }

    @Override
    public Map getAdditionalRows(int nextID, int nextSortingNumber) throws WTException {

        List<String> rows = SMCostSheetConfig.getValues( SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_SEPD );
        if ( FormatHelper.format((String) lcsProductCostSheet.getValue("smCsIncoterms")).equals("smDDP") ) {
            //TODO: может достаточно одного значения и список не нужен?
            rows.addAll( SMCostSheetConfig.getValues( SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_CS_INCOTERMS_DDP_SEPD ) );
        }

        return toRows(nextID, nextSortingNumber, "smSectionSEPD", rows);
    }
}
