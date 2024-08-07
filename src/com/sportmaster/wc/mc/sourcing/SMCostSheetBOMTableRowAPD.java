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
import java.util.List;
import java.util.Map;

public class SMCostSheetBOMTableRowAPD extends SMCostSheetBOMTableRow {
    private static final Logger LOGGER = Logger.getLogger(SMCostSheetBOMTableRowAPD.class);

    private LCSProductCostSheet lcsProductCostSheet = null;
    private FlexType supplierType = null;
    private FlexType materialSupplierType = null;
    private FlexType materialColorType = null;
    private Date dateForMaterialPrice = null;

    private SMCostSheetBOMTableRowAPD() {}

    public static SMCostSheetBOMTableRow newSMCostSheetBOMTableRowAPD(LCSProductCostSheet lcsProductCostSheet, Date dateForMaterialPrice) throws WTException {
        SMCostSheetBOMTableRowAPD obj = new SMCostSheetBOMTableRowAPD();
        obj.lcsProductCostSheet = lcsProductCostSheet;
        obj.supplierType = FlexTypeCache.getFlexTypeFromPath(SMCostSheetConfig.SUPPLIER_TYPE);
        obj.materialSupplierType = FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.material.LCSMaterialSupplier");
        obj.materialColorType = FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.material.LCSMaterialColor");
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
        // supplierInfo[0] - supplier name, supplierInfo[1] - branch id
        Object[] supplierInfo = SMCostSheetReader.getSupplierNameAndBranchId(bomObj);

        //Ignore empty rows.
        if ( SMFormatHelper.isEmpty( (String) materialInfo[0] ) )
            return null;
        //Ignore marked for deletion.
        if (ptcbomPartMarkUp.equalsIgnoreCase("delete"))
            return null;

        // BOM Link
        String section = FormatHelper.format(bomObj.getData(bomType.getAttribute("section").getSearchResultIndex())); // <-- BOM Link, Section (section)
        String placement = FormatHelper.format(bomObj.getData(bomType.getAttribute("partName").getSearchResultIndex())); // <-- BOM Link, Placement (partName)
        double consumption = FormatHelper.parseDouble(bomObj.getData(bomType.getAttribute("quantity").getSearchResultIndex())); // <-- BOM Link, Consumption (quantity)
        double loss = FormatHelper.parseDouble(bomObj.getData(bomType.getAttribute("lossAdjustment").getSearchResultIndex())); // <-- BOM Link, Loss % (lossAdjustment)
        String smComponentName = FormatHelper.format(bomObj.getData(bomType.getAttribute("smComponentName").getSearchResultIndex())); // <-- BOM Link / Materials, Component Name (smComponentName)
        String smCommentsSM = FormatHelper.format(bomObj.getData(bomType.getAttribute("vrdLinkComments").getSearchResultIndex())); //  <-- BOM Link / Materials, Comments-PM (vrdLinkComments)
        String smSize = FormatHelper.format(bomObj.getData(bomType.getAttribute("vrdSize").getSearchResultIndex())); // <-- BOM Link, Size (vrdSize)

        String unitOfMeasure = "";
        String nominated = "";
        String vrdSupplierMaterialRefNumber = "";

        if (!SMCostSheetReader.isTextMaterial(bomObj, bomType)) {
            // Material
            unitOfMeasure = FormatHelper.format(bomObj.getData(materialType.getAttribute("unitOfMeasure").getSearchResultIndex())); // <-- Material, Article UOM (unitOfMeasure)
            // Material Supplier
            nominated = FormatHelper.format(bomObj.getData(materialSupplierType.getAttribute("smMsNominated").getSearchResultIndex())); // <-- Material Supplier, Nominated? (smMsNominated)
            vrdSupplierMaterialRefNumber = FormatHelper.format(bomObj.getData(materialSupplierType.getAttribute("vrdSupplierMaterialRefNumber").getSearchResultIndex())); // <-- Material Supplier, Supplier Material Ref. No. (vrdSupplierMaterialRefNumber)
        }

        // Material Supplier
        double smMsCuttableWidth = FormatHelper.parseDouble(bomObj.getData(materialSupplierType.getAttribute("smMsCuttableWidth").getSearchResultIndex())); // <-- Material Supplier, Cuttable Width (smMsCuttableWidth)

        // Material Color
        double smCuttableWidthMC = FormatHelper.parseDouble(bomObj.getData(materialColorType.getAttribute("smCuttableWidthMC").getSearchResultIndex())); // <-- Material Color, Cuttable Width-Color (smCuttableWidthMC)

        //BOM Link.Branch ID
        long bomLinkBranchId = bomObj.getLong("FLEXBOMLINK.BRANCHID");
        //Row ID
        String dimensionId = FormatHelper.format(bomObj.getData( "FLEXBOMLINK.DIMENSIONID" ));

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

        double smPriceCC = SMCostSheetCalculate.getSMPriceCC(lcsProductCostSheet, smMSPrice, smMSCurrency);

        /*
        1 	Section 	| smSectionAPD | Single List | <-- BOM Link, Section (section)
        *   Component Name | smComponentName | Single List | <-- BOM Link / Materials, Component Name (smComponentName)
        2 	Placement 	| smPlacement | Text | <-- BOM Link, Placement (partName)
        3 	Material 	| smMaterial | Text | <-- BOM Link, Material (materialDescription)
        4 	Supplier Material Ref No. |	smSupplierMaterialRefNo | Text | <-- Material Supplier, Supplier Material Ref. No. (vrdSupplierMaterialRefNumber)
        5 	Supplier | smSupplier | Text | <-- BOM Link, Supplier (supplierDescription)
        6 	Nom? | smNominated | Single List | <-- Material Supplier, Nominated? (smMsNominated)
        7 	Color | smColor | Text | <-- BOM Link, Color (colorDescription)
        8 	Cuttable Width 	| smCuttableWidth |	Float | <-- Material Supplier, Cuttable Width (smMsCuttableWidth)
        9 	Cuttable Width-Color | smCuttableWidthColor | Float | <-- Material Color, Cuttable Width-Color (smCuttableWidthMC)
        10 	Consumption | smConsumption | Float | <-- BOM Link, Consumption (quantity)
        *   Consumption from SM | smConsumptionOVR | Float | <-- Manual
        11 	Article UOM | smArticleUOM | Single List | <-- Material, Article UOM (unitOfMeasure)
        12 	Loss, % (Supplier) | smLoss | Float | <-- BOM Link, Loss % (lossAdjustment)
        *   Loss, % from SM | smLossOVR | Float | <-- Manual
        13 	MS price | smMSPrice | Float | <-- Material Supplier, MS Price (smMaterialSupplierPrice)
        14 	MS Currency | smMSCurrency | Single List | <-- Material Supplier, MS Currency (smMsCurrencyUnit)
        15 	Price (CC) | smPriceCC | Float | <-- Calc
        16 	Price (OVR) | smPriceOVR | Float | <-- Manual
        *   Price from SM | smPriceFromSM | Float | <-- Manual
        17 	CIF % (Supplier) | smCIF | Float | <-- Manual
        *   CIF % from SM | CIFFromSM | Float | <-- Calc
        18 	CIF Price | smCIFPrice | Float | <-- Calc
        19 	Total | smTotal | Float | <-- Calc (formula in configuration)
        *   Total USD | smTotalUSD | Float | <-- Calc
        *   Total SM | smTotalSM | Float | <-- Calc
        *   Total USD (SM) | smTotalUSDSM | Float | <-- Calc
        20 	Comments-Supplier | smCommentsSupplier | Text Area | <-- Manual
        *   Comments-PM | smCommentsSM | Text Area | <-- BOM Link / Materials, Comments-PM (vrdLinkComments)
        *   Include in Purchase Price | smIncludeInPurchasePrice | Boolean | <-- Manual
         */

        newBOMObj.put("smSectionAPD", section);  // <-- BOM Link, Section (section)
        newBOMObj.put("smComponentName", smComponentName); // <-- BOM Link / Materials, Component Name (smComponentName)
        newBOMObj.put("smPlacement", placement); // <-- BOM Link, Placement (partName)
        newBOMObj.put("smMaterial", materialInfo[0]); // <-- BOM Link, Material (materialDescription)
        newBOMObj.put("smSupplierMaterialRefNo", vrdSupplierMaterialRefNumber); // <-- Material Supplier, Supplier Material Ref. No. (vrdSupplierMaterialRefNumber)
        newBOMObj.put("smSupplier",  supplierInfo[0]); // <-- BOM Link, Supplier (supplierDescription)
        newBOMObj.put("smNominated", nominated); //  <-- Material Supplier, Nominated? (smMsNominated)
        newBOMObj.put("smColor", color); //  <-- BOM Link, Color (colorDescription)
        newBOMObj.put("smCuttableWidth", smMsCuttableWidth); // <-- Material Supplier, Cuttable Width (smMsCuttableWidth)
        newBOMObj.put("smCuttableWidthColor", smCuttableWidthMC); // <-- Material Color, Cuttable Width-Color (smCuttableWidthMC)
        newBOMObj.put("smConsumption", consumption); // <-- BOM Link, Consumption (quantity)
        newBOMObj.put("smArticleUOM", unitOfMeasure); // <-- Material, Article UOM (unitOfMeasure)
        newBOMObj.put("smLoss", loss); // <-- BOM Link, Loss % (lossAdjustment)
        newBOMObj.put(SMCostSheetMOATableConfig.MOA_TABLE_SM_MATERIAL_PRICE_LC, smMSPrice); // <-- Material Supplier, MS Price (smMaterialSupplierPrice)
        newBOMObj.put(SMCostSheetMOATableConfig.MOA_TABLE_SM_CURRENCY_UNIT, smMSCurrency); //  <-- Material Supplier, MS Currency (smMsCurrencyUnit)
        newBOMObj.put("smPriceCC", smPriceCC); // <-- Calc
        //newBOMObj.put("smCIFPrice", smPriceCC); // <-- Calc
        newBOMObj.put("smCommentsSM", smCommentsSM); // <-- BOM Link / Materials, Comments-PM (vrdLinkComments)
        newBOMObj.put("smSize", smSize); // <-- BOM Link, Size (vrdSize)
        newBOMObj.put("smIncludeInPurchasePrice", true);

        if (materialInfo[1] != null) newBOMObj.put("smMaterialID", materialInfo[1]);
        if (colorBranchId != null) newBOMObj.put("smColorID", colorBranchId);
        if (supplierInfo[1] != null) newBOMObj.put("smSupplierID",  supplierInfo[1]);

        newBOMObj.put("smBOMLinkID", bomLinkBranchId);
        newBOMObj.put("smRowID", dimensionId);
        newBOMObj.put("sortingNumber", "" + sortingNumberRow);

        return newBOMObj;
    }

    @Override
    public Map getAdditionalRows(int nextID, int nextSortingNumber) throws WTException {

        List<String> rows = SMCostSheetConfig.getValues( SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_APD );
        if ( FormatHelper.format((String) lcsProductCostSheet.getValue("smCsIncoterms")).equals("smDDP") ) {
            //TODO: может достаточно одного значения и список не нужен?
            rows.addAll( SMCostSheetConfig.getValues( SMCostSheetMOATableConfig.SM_COST_SHEET_BOM_TABLE_ADDITIONAL_ROWS_SECTION_KEYS_CS_INCOTERMS_DDP_APD ) );
        }

        return toRows(nextID, nextSortingNumber, "smSectionAPD", rows);
    }
}
