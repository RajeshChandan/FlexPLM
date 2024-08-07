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

public class SMCostSheetBOMTableRowFPD extends SMCostSheetBOMTableRow {

    private static final Logger LOGGER = Logger.getLogger(SMCostSheetBOMTableRowFPD.class);

    private LCSProductCostSheet lcsProductCostSheet = null;
    private FlexType supplierType = null;
    private FlexType materialSupplierType = null;
    private Date dateForMaterialPrice = null;

    private SMCostSheetBOMTableRowFPD() {}

    public static SMCostSheetBOMTableRowFPD newSMCostSheetBOMTableRowFPD(LCSProductCostSheet lcsProductCostSheet, Date dateForMaterialPrice) throws WTException {
        SMCostSheetBOMTableRowFPD obj = new SMCostSheetBOMTableRowFPD();
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
        String color = FormatHelper.format(bomObj.getData(bomType.getAttribute("colorDescription").getSearchResultIndex()));
        Long colorBranchId = SMCostSheetReader.getColorBranchId(bomObj);
        String ptcbomPartMarkUp = FormatHelper.format(bomObj.getData(bomType.getAttribute("ptcbomPartMarkUp").getSearchResultIndex()));
        // Supplier
        //String supplier = FormatHelper.format(bomObj.getData(supplierType.getAttribute("name").getSearchResultIndex())); //smSupplierShortName
        // supplierInfo[0] - supplier name, supplierInfo[1] - branch id
        Object[] supplierInfo = SMCostSheetReader.getSupplierNameAndBranchId(bomObj);

        //Ignore empty rows.
        if (SMFormatHelper.isEmpty((String)materialInfo[0]) && SMFormatHelper.isEmpty(color) && SMFormatHelper.isEmpty((String) supplierInfo[0]))
            return null;
        //Ignore marked for deletion.
        if (ptcbomPartMarkUp.equalsIgnoreCase("delete"))
            return null;

        // BOM Link
        String section = FormatHelper.format(bomObj.getData(bomType.getAttribute("section").getSearchResultIndex()));
        String placement = FormatHelper.format(bomObj.getData(bomType.getAttribute("partName").getSearchResultIndex()));
        double consumption = FormatHelper.parseDouble(bomObj.getData(bomType.getAttribute("quantity").getSearchResultIndex()));
        double loss = FormatHelper.parseDouble(bomObj.getData(bomType.getAttribute("lossAdjustment").getSearchResultIndex()));
        String smMaterialSupplierOVR = FormatHelper.format(bomObj.getData(bomType.getAttribute("smMaterialSupplierOVR").getSearchResultIndex()));
        String smSuppliersUOMOVR = FormatHelper.format(bomObj.getData(bomType.getAttribute("smSuppliersUOMOVR").getSearchResultIndex()));
        String smBOMLinkSupplierComments = FormatHelper.format(bomObj.getData(bomType.getAttribute("smBOMLinkSupplierComments").getSearchResultIndex()));

        String unitOfMeasure = "";
        String nominated = "";
        String vrdSupplierMaterialRefNumber = "";

        if (!SMCostSheetReader.isTextMaterial(bomObj, bomType)) {
            // Material
            unitOfMeasure = FormatHelper.format(bomObj.getData(materialType.getAttribute("unitOfMeasure").getSearchResultIndex()));
            // Material Supplier
            nominated = FormatHelper.format(bomObj.getData(materialSupplierType.getAttribute("smMsNominated").getSearchResultIndex()));
            vrdSupplierMaterialRefNumber = FormatHelper.format(bomObj.getData(materialSupplierType.getAttribute("vrdSupplierMaterialRefNumber").getSearchResultIndex()));
        }

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
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetBuildTable.buildBOMTable: smMSPrice = " + smMSPrice + " " + smMSCurrency);
        //Calculate Contract currency price
        double smPriceCC = SMCostSheetCalculate.getSMPriceCC(lcsProductCostSheet, smMSPrice, smMSCurrency);

        newBOMObj.put("smPriceCC", smPriceCC); //smPriceCC
        newBOMObj.put(SMCostSheetMOATableConfig.MOA_TABLE_SM_MATERIAL_PRICE_LC, smMSPrice);
        newBOMObj.put(SMCostSheetMOATableConfig.MOA_TABLE_SM_CURRENCY_UNIT, smMSCurrency);
        newBOMObj.put("smSection", SMCostSheetReader.getDisplayValue(bomType, "section", section)); //smSection
        newBOMObj.put("smPlacement", placement); //smPlacement
        newBOMObj.put("smNominated", nominated); //smNominated
        newBOMObj.put("smMaterial", materialInfo[0]); //smMaterial
        newBOMObj.put("smColor", color); //smColor
        newBOMObj.put("smSupplier",  supplierInfo[0]); //smSupplier
        newBOMObj.put("smConsumption", consumption); //smConsumption
        newBOMObj.put("smLoss", loss); //smLoss
        newBOMObj.put("smArticleUOM", unitOfMeasure); //smArticleUOM
        newBOMObj.put("smSupplierMaterialRefNo", vrdSupplierMaterialRefNumber); //smSupplierMaterialRefNo
        newBOMObj.put("smMaterialSupplierOVR", smMaterialSupplierOVR);
        newBOMObj.put("smSuppliersUOMOVR", smSuppliersUOMOVR);
        newBOMObj.put("smCommentsSupFromBOM", smBOMLinkSupplierComments);

        if (materialInfo[1] != null) newBOMObj.put("smMaterialID", materialInfo[1]); //smMaterialID
        if (colorBranchId != null) newBOMObj.put("smColorID", colorBranchId); //smColorID
        if (supplierInfo[1] != null) newBOMObj.put("smSupplierID",  supplierInfo[1]); //smSupplierID

        newBOMObj.put("smBOMLinkID", bomLinkBranchId);
        newBOMObj.put("smRowID", dimensionId); //smRowID
        newBOMObj.put("sortingNumber", "" + sortingNumberRow);

        return newBOMObj;
    }
}
