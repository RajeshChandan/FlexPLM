package com.hbi.wc.flexbom.gen;

import com.lcs.wc.client.web.PDFGeneratorHelper;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.PDFProductSpecificationGenerator2;
import com.lcs.wc.product.ReferencedTypeKeys;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import wt.log4j.LogR;
import wt.util.WTException;

import java.util.*;

/**
 * @author UST date 14 Nov 2023
 * This class has implementation for Below.
 * - adding Master Material BOM table to TechPack Report.
 */
public class HBIMasterLabelBOMGenerator {

    public static final String TABLE_HEADER_TEXT = "TABLE-HEADERTEXT";
    public static final String RPT_HEADER = "RPT_HEADER";
    public static final String COUNTRY = "Country";
    public static final String FIBER_CONTENT = "Fiber Content";
    public static final String DISPLAY_TEXT = "DISPLAYTEXT";
    public static final String HBI_GARMENT_SIZE_MOA_TABLE = LCSProperties.get("com.hbi.wc.flexbom.gen.HBIMasterLabelBOMGenerator.GarmentSizeTableAttKey", "hbiGarmentSizeTable");
    public static final String HBI_GARMENT_SIZE_ATTRKEY = LCSProperties.get("com.hbi.wc.flexbom.gen.HBIMasterLabelBOMGenerator.GarmentSizeAttKey", "hbiGarmentSize");
    public static final String HBI_ACTIVE_SIZE_MOA_TABLE_ATTRKEY = LCSProperties.get("com.hbi.wc.flexbom.gen.HBIMasterLabelBOMGenerator.ActiveSizeMOATableAttKey", "hbiActiveSizeMOATable");
    public static final String PTCMATERIAL_NAME_ATTRKEY = "ptcmaterialName";
    public static final String HBI_MASTER_MATERIAL_ATTRKEY = LCSProperties.get("com.hbi.wc.flexbom.gen.HBIMasterLabelBOMGenerator.MasterMaterialAttKey", "hbiMasterMaterial");
    public static final String HBI_LABEL_COUNTRY_ATTRKEY = LCSProperties.get("com.hbi.wc.flexbom.gen.HBIMasterLabelBOMGenerator.CountryAttKay", "hbiLabelCountry");
    public static final String HBI_FIBER_CODE_NEW_ATTRKEY = LCSProperties.get("com.hbi.wc.flexbom.gen.HBIMasterLabelBOMGenerator.fiberCodeAttKey", "hbiFiberCodeNew");
    public static final String MATERIAL_BOM_TABLE_HEADER_FIELDS = LCSProperties.get("com.hbi.wc.flexbom.gen.HBIMasterLabelBOMGenerator.HeaderFields", "hbiItemDescription,hbiApplication,hbiMatLabelType,hbiUsageUOM");
    Logger logger = LogR.getLogger(HBIMasterLabelBOMGenerator.class.getName());
    private final PDFGeneratorHelper pgh = new PDFGeneratorHelper();


    /**
     * This method is to generate Master material data and return relevant data to List of PDFTable.
     *
     * @param colorwayBOMs - Colorway BOM Data
     * @param params       - TechPack Param
     * @return - List of PDFTable
     */
    public List<PdfPTable> printMaterLabelBOM(LinkedHashMap<String, FlexBOMPart> colorwayBOMs, Map<String, Object> params) {

        List<PdfPTable> masterBOMPDFTableList = new ArrayList<>();

        //Getting Product Sizes
        Map<Integer, String> productSizes = getProductSizes(params);

        if (productSizes.isEmpty()) {
            return masterBOMPDFTableList;
        }
        try {
            //Looping through Colorway BOM object
            for (FlexBOMPart bomPart : colorwayBOMs.values()) {

                masterBOMPDFTableList.addAll(materialBom(bomPart, productSizes));

            }
        } catch (Exception e) {
            this.logger.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
        return masterBOMPDFTableList;
    }

    /**
     * This method is to generate Master material data and return relevant data to List of PDFTable.
     *
     * @param bomPart      - BOM Object
     * @param productSizes - product sizes
     * @return - returns list of bom table
     * @throws Exception - exception
     */
    private List<PdfPTable> materialBom(FlexBOMPart bomPart, Map<Integer, String> productSizes) throws Exception {

        List<PdfPTable> masterBOMPDFTableList = new ArrayList<>();

        //Fetching Flex BOM Link rows from Colorway bom
        Collection<?> productBomData = LCSFlexBOMQuery.findFlexBOMData(bomPart, null, null, null, null, null, "", null, false, false, "ALL_DIMENSIONS", "ALL_SKUS", "ALL_SOURCES", "").getResults();
        //skip to next material if no bom data found
        if (Objects.isNull(productBomData)) {
            return masterBOMPDFTableList;
        }

        for (Object obj : productBomData) {
            FlexObject var11 = (FlexObject) obj;

            //filtering BOM link rows having Material info
            if (var11.getString("FLEXBOMLINK.IDA2A2") == null || var11.getString("LCSMATERIAL.BRANCHIDITERATIONINFO") == null) {
                continue;
            }

            LCSMaterial material = (LCSMaterial) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:" + var11.getString("LCSMATERIAL.BRANCHIDITERATIONINFO"));//fetching material object

            //continue to next row data if material is null
            if (Objects.isNull(material)) {
                continue;
            }

            //fetching material bom data
            PreparedQueryStatement matBOMQry = LCSFlexBOMQuery.findBOMPartsForOwnerQuery(material.getMaster(), "A", "MAIN", null, null, false, null);
            SearchResults matBOMQryResult = LCSQuery.runDirectQuery(matBOMQry);
            int resultCount = 0;

            if (Objects.nonNull(matBOMQryResult)) {
                resultCount = matBOMQryResult.getResultsFound();
            }
            this.logger.debug("material BOM found>>>>>" + resultCount);
            //Filter based on Material type, master material value and master material bom values.
            if (validateMaterial(material) && resultCount > 0) {
                //Calling method to fetch master material BOM.
                PdfPTable masterBOMTable = getGarmentLabelBOM(material, matBOMQryResult, productSizes);
                //adding data to list
                if (Objects.nonNull(masterBOMTable)) {
                    masterBOMPDFTableList.add(masterBOMTable);
                }
            }
        }
        return masterBOMPDFTableList;
    }

    /**
     * this method validates a material is master material or not.
     * criteria:
     * master material attribute should eb true
     * material type should be Garment Label
     *
     * @param material - material object
     * @return - boolean
     */
    private boolean validateMaterial(LCSMaterial material) {
        boolean valid = false;
        try {
            material = (LCSMaterial) VersionHelper.latestIterationOf(material);
            this.logger.debug("latestIterationOf Material Obj getName>>>>>" + material.getValue(PTCMATERIAL_NAME_ATTRKEY));

            String materialType = material.getFlexType().getFullNameDisplay(false);//getting material type

            //Fetching master material attribute value
            boolean isMasterMaterial = false;
            if (FormatHelper.hasContent(String.valueOf(material.getValue(HBI_MASTER_MATERIAL_ATTRKEY)))) {

                this.logger.debug("materialObj hbiMasterMaterial getValue >>" + material.getValue(HBI_MASTER_MATERIAL_ATTRKEY));
                isMasterMaterial = (boolean) material.getValue(HBI_MASTER_MATERIAL_ATTRKEY);
            }
            this.logger.debug("materialObj hbiMasterMaterial >>" + isMasterMaterial);


            //Filter based on Material type, master material value.
            if ("Garment Label".equalsIgnoreCase(materialType) && isMasterMaterial) {
                valid = true;

            }
        } catch (WTException e) {
            this.logger.error(e.getLocalizedMessage());
        }
        return valid;

    }

    /**
     * This method is to fetch and draw table for master material bom.
     *
     * @param material        - material Object
     * @param matBOMQryResult - search result
     * @param productSizes    - active product sizes
     * @return - pdf table
     */
    private PdfPTable getGarmentLabelBOM(LCSMaterial material, SearchResults matBOMQryResult, Map<Integer, String> productSizes) {

        List<JSONObject> masterBOMSizeData = new ArrayList<>();
        Map<String, String> bomTableHeaderData = new HashMap<>();
        try {

            for (String attrKey : FormatHelper.commaSeparatedListToList(MATERIAL_BOM_TABLE_HEADER_FIELDS)) {
                bomTableHeaderData.put(material.getFlexType().getAttribute(attrKey).getAttDisplay(), getAttributeValue(material, attrKey));
            }
            Collection<?> results = LCSQuery.getObjectsFromResults(matBOMQryResult, "VR:com.lcs.wc.flexbom.FlexBOMPart:", "FLEXBOMPART.BRANCHIDITERATIONINFO");

            if (Objects.nonNull(results) && !results.isEmpty()) {

                FlexBOMPart bomPart = (FlexBOMPart) results.iterator().next();
                bomPart = (FlexBOMPart) VersionHelper.latestIterationOf(bomPart);
                FlexType bomType = bomPart.getFlexType().getReferencedFlexType(ReferencedTypeKeys.MATERIAL_TYPE);
                //fetching material bom data
                Collection<?> masterBOMData = LCSFlexBOMQuery.findFlexBOMData(bomPart, null, null, null, null, null, LCSFlexBOMQuery.EFFECTIVE_ONLY, null, false, false, LCSFlexBOMQuery.ALL_DIMENSIONS, null, null, null, bomType).getResults();
                if (Objects.nonNull(masterBOMData)) {
                    for (Object var9 : masterBOMData) {

                        FlexObject flexObject = (FlexObject) var9;

                        if (flexObject.getString("FLEXBOMLINK.IDA2A2") != null && flexObject.getString("LCSMATERIAL.BRANCHIDITERATIONINFO") != null) {

                            JSONObject jsonObject = new JSONObject();
                            LCSMaterial masterBOMMaterial = (LCSMaterial) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterial:" + flexObject.getString("LCSMATERIAL.BRANCHIDITERATIONINFO"));
                            masterBOMMaterial = (LCSMaterial) VersionHelper.latestIterationOf(masterBOMMaterial);
                            this.logger.debug("latestIterationOf Material Obj getName>>>>>" + masterBOMMaterial.getValue(PTCMATERIAL_NAME_ATTRKEY));

                            jsonObject.put(COUNTRY, getAttributeValue(masterBOMMaterial, HBI_LABEL_COUNTRY_ATTRKEY));
                            jsonObject.put(FIBER_CONTENT, getAttributeValue(masterBOMMaterial, HBI_FIBER_CODE_NEW_ATTRKEY));
                            String sizeValue = getAttributeValue(masterBOMMaterial, HBI_GARMENT_SIZE_ATTRKEY);
                            jsonObject.put(sizeValue, getAttributeValue(masterBOMMaterial, PTCMATERIAL_NAME_ATTRKEY));

                            //filtering bases on product sizes
                            if (productSizes.containsValue(sizeValue)) {
                                masterBOMSizeData.add(jsonObject);
                            }
                        }
                    }
                }
            }
        } catch (WTException | JSONException e) {
            this.logger.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
        PdfPTable masterBOMTable = null;
        this.logger.debug("active bom dta found>>>" + masterBOMSizeData.size());
        if (!masterBOMSizeData.isEmpty()) {
            masterBOMTable = getGarmentLabelBOMTable(bomTableHeaderData, masterBOMSizeData, productSizes, getAttributeValue(material, PTCMATERIAL_NAME_ATTRKEY));
        }
        return masterBOMTable;
    }

    /**
     * @param bomTableHeaderData
     * @param masterBOMSizeData
     * @param productSizes
     * @param masterMaterialName
     * @return
     */
    private PdfPTable getGarmentLabelBOMTable(Map<String, String> bomTableHeaderData, List<JSONObject> masterBOMSizeData, Map<Integer, String> productSizes, String masterMaterialName) {

        PdfPTable mainTable;
        try {
            mainTable = new PdfPTable(1);

            mainTable.setWidthPercentage(95);
            mainTable.addCell(genTableHeader(masterMaterialName));

            PdfPTable headerTable = new PdfPTable(bomTableHeaderData.size() * 2);
            for (Map.Entry<String, String> mapEntry : bomTableHeaderData.entrySet()) {
                headerTable.addCell(genTableHeaderCell(mapEntry.getKey()));
                headerTable.addCell(genTableCell(mapEntry.getValue()));
            }
            mainTable.addCell(new PdfPCell(headerTable));

            PdfPCell spaceCell = getSpaceCell();
            spaceCell.setBorder(0);
            mainTable.addCell(spaceCell);

            PdfPTable dataTable = new PdfPTable(productSizes.size() + 2);
            dataTable.setWidths(getTableWidth(productSizes));
            dataTable.addCell(genTableHeaderCell(COUNTRY));
            dataTable.addCell(genTableHeaderCell(FIBER_CONTENT));

            for (Map.Entry<Integer, String> sizeEntry : productSizes.entrySet()) {

                dataTable.addCell(genTableHeaderCell(sizeEntry.getValue()));
            }

            for (JSONObject bomDataJson : masterBOMSizeData) {
                dataTable.addCell(genTableCell(bomDataJson.getString(COUNTRY)));
                dataTable.addCell(genTableCell(bomDataJson.getString(FIBER_CONTENT)));

                for (Map.Entry<Integer, String> sizeEntry : productSizes.entrySet()) {

                    if (bomDataJson.has(sizeEntry.getValue()) && FormatHelper.hasContent(bomDataJson.getString(sizeEntry.getValue()))) {
                        dataTable.addCell(genTableCell(bomDataJson.getString(sizeEntry.getValue())));
                    } else {
                        dataTable.addCell(genTableCell(""));
                    }
                }

            }

            mainTable.addCell(new PdfPCell(dataTable));

            spaceCell = getSpaceCell();
            spaceCell.setBorder(0);
            mainTable.addCell(spaceCell);
        } catch (JSONException | DocumentException e) {
            mainTable = null;
            this.logger.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
        return mainTable;
    }

    private PdfPCell genTableHeaderCell(String text) {

        String value = "";
        if (FormatHelper.hasContent(text)) {
            value = text;
        }
        PdfPCell cell = new PdfPCell(multiFontPara(value, this.pgh.getCellFont(HBIMasterLabelBOMGenerator.TABLE_HEADER_TEXT, null, "8")));
        cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(RPT_HEADER, null));

        return cell;
    }

    private PdfPCell genTableCell(String text) {
        String value = "";
        if (FormatHelper.hasContent(text)) {
            value = text;
        }
        return new PdfPCell(multiFontPara(value, this.pgh.getCellFont(HBIMasterLabelBOMGenerator.RPT_HEADER, null, "8")));
    }

    private PdfPCell genTableHeader(String text) {

        String value = "";
        if (FormatHelper.hasContent(text)) {
            value = text;
        }

        PdfPTable table = new PdfPTable(1);
        PdfPCell cell = new PdfPCell(multiFontPara("Material BOM for " + value, this.pgh.getCellFont("FORMLABEL", null, "8")));
        cell.setBorder(0);
        table.addCell(cell);
        return new PdfPCell(table);

    }

    private PdfPCell getSpaceCell() {
        PdfPTable table = new PdfPTable(1);

        PdfPCell cell = new PdfPCell(multiFontPara("", this.pgh.getCellFont(DISPLAY_TEXT, null, null)));
        cell.setBorder(0);
        cell.setFixedHeight(10f);
        table.addCell(cell);
        return new PdfPCell(table);
    }

    private Phrase multiFontPara(String text, Font font) {

        String value = "";
        if (FormatHelper.hasContent(text)) {
            value = text;
        }

        return new Phrase(new Chunk(value, font));

    }

    /**
     * this method fetch active sizes from Product Garment Size Table.
     *
     * @param params - TechPack Params //TO-DO ADDING PARAGRAPH DESCRIBING VAR1 DATA SET
     * @return - return List of active sizes
     */
    private Map<Integer, String> getProductSizes(Map<String, Object> params) {
        Map<Integer, String> sizes = new TreeMap<>();

        //getting product object bases on ID.
        LCSProduct product;
        try {
            product = (LCSProduct) LCSQuery.findObjectById((String) params.get(PDFProductSpecificationGenerator2.PRODUCT_ID));

            if (Objects.isNull(product)) {
                return sizes;
            }

            //getting MOA Table object from product object
            LCSMOATable sizeTable = (LCSMOATable) product.getValue(HBI_GARMENT_SIZE_MOA_TABLE);
            Collection<?> tableRows = sizeTable.getRows(); //getting table rows
            //looping through MOA Rows
            for (Object obj : tableRows) {

                FlexObject flexObject = (FlexObject) obj;
                //fetching MOATable row object
                LCSMOAObject tableRow = (LCSMOAObject) LCSQuery
                        .findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:" + flexObject.getData("OID"));

                String sizeValue = (String) tableRow.getValue(HBI_GARMENT_SIZE_ATTRKEY);//Getting size value

                boolean active = false;
                //fetching Active size value
                if (FormatHelper.hasContent(String.valueOf(tableRow.getValue(HBI_ACTIVE_SIZE_MOA_TABLE_ATTRKEY)))) {
                    active = (boolean) tableRow.getValue(HBI_ACTIVE_SIZE_MOA_TABLE_ATTRKEY);
                }
                //filtering size bases on active size value
                if (!tableRow.isDropped() && active) {
                    sizes.put(tableRow.getSortingNumber(), sizeValue);
                }
            }
        } catch (WTException e) {
            this.logger.error(e.getLocalizedMessage());
        }


        this.logger.debug("active Product Sizes :" + sizes.values());
        return sizes;
    }

    private String getAttributeValue(FlexTyped flexTyped, String attKey) {
        String value = "";

        try {
            value = String.valueOf(flexTyped.getValue(attKey));
            FlexType flexType = flexTyped.getFlexType();
            FlexTypeAttribute attribute = flexType.getAttribute(attKey);
            String attType = attribute.getAttVariableType();

            if ("driven".equals(attType) || "choice".equals(attType) || "colorSelect".equals(attType)) {
                value = getListAttributeValue(attribute, value);
            } else if (attType.equalsIgnoreCase("object_ref_list") || attType.equalsIgnoreCase("object_ref")) {
                value = getObjectRefValue(attribute, value);
            }

        } catch (WTException var0) {
            this.logger.debug(var0.getLocalizedMessage());
        }
        return value;
    }

    public String getObjectRefValue(FlexTypeAttribute attribute, String value) throws WTException {

        FlexType flexType = attribute.getRefType();
        FlexTyped flexTyped;
        String newValue = value;
        if (value.contains(flexType.getTypeClass())) {
            flexTyped = (FlexTyped) LCSQuery.findObjectById(value);
        } else {
            flexTyped = (FlexTyped) LCSQuery.findObjectById(flexType.getTypeClass() + ":" + value);
        }
        if (flexTyped != null) {
            newValue = String.valueOf(flexTyped.getValue(getTypeMap().get(flexType.getTypeClass())));
        }

        return newValue;
    }

    /**
     * This method return name attribute for each object, it is used to retrieve reference type attribute value.
     *
     * @return - return Map
     */
    public Map<String, String> getTypeMap() {

        Map<String, String> valueMap = new HashMap<>();
        valueMap.put("com.lcs.wc.foundation.LCSLifecycleManaged", "name");
        valueMap.put("com.lcs.wc.material.LCSMaterial", PTCMATERIAL_NAME_ATTRKEY);

        return valueMap;
    }

    /**
     * This method return display value for list type attribute.
     *
     * @param attribute - FlexTypeAttribute
     * @param value     - String
     * @return - string attribute display value
     */
    public String getListAttributeValue(FlexTypeAttribute attribute, String value) {

        if (!FormatHelper.hasContent(value)) {
            return "";
        }
        try {
            AttributeValueList valueList = attribute.getAttValueList();
            Locale locale = new Locale("en", "US");
            Collection<?> keys = valueList.getSelectableKeys(locale, true);
            for (Object obj : keys) {

                String internalName = (String) obj;
                if (internalName.equals(value)) {
                    value = valueList.getValue(internalName, locale);
                    break;
                }
            }
        } catch (Exception e) {
            this.logger.debug(e.getLocalizedMessage());
        }
        return value;
    }

    /**
     * this method return table with for master bom Table.
     *
     * @param sizesMap - Map ,product sizes
     * @return - float array
     */
    private float[] getTableWidth(Map<Integer, String> sizesMap) {
        List<Float> widths = new ArrayList<>();
        widths.add(25f);
        widths.add(40f);
        for (int i = 0; i < sizesMap.size(); i++) {
            widths.add(15f);
        }

        float[] floatArray = new float[widths.size()];
        int i = 0;

        for (Float f : widths) {
            floatArray[i++] = (f != null ? f : 10f); // Or whatever default you want.
        }

        return floatArray;
    }
}
