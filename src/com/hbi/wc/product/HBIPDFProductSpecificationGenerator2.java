/*
 * PDFProductSpecificationGenerator2.java
 *
 * Created on August 23, 2005, 4:06 PM
 */

package com.hbi.wc.product;

import com.hbi.wc.flexbom.gen.HBIMasterLabelBOMGenerator;
import com.hbi.wc.flexbom.gen.HBISewUsageGenerator;
import com.hbi.wc.flexbom.gen.HBISizeGenerator;
import com.hbi.wc.flexbom.util.HBISpecificationPDFGenUtil;
import com.lcs.wc.client.ClientContext;
import com.lcs.wc.client.web.PDFGeneratorHelper;
import com.lcs.wc.client.web.pdf.*;
import com.lcs.wc.construction.LCSConstructionInfo;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.document.FileRenamer;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentQuery;
import com.lcs.wc.epmstruct.PDFCadDocVariationsGenerator;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.gen.BOMPDFContentGenerator;
import com.lcs.wc.flexbom.gen.BomDataGenerator;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.measurements.LCSMeasurements;
import com.lcs.wc.part.LCSPart;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.PDFMultiSpecGenerator;
import com.lcs.wc.product.PDFProductSpecificationGenerator2;
import com.lcs.wc.product.*;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.sizing.SizingQuery;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecToSeasonLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.log4j.Logger;
import wt.fc.WTObject;
import wt.httpgw.GatewayServletHelper;
import wt.httpgw.URLFactory;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

/**
 * This class generates a Product Specification document in PDF format.
 * <p>
 * It requires a SourcingConfig in order to generate the spec.
 *
 * @author Chuck
 */

// ///////////////////////////////////////////////////////////////////////////////////////////////////////

public class HBIPDFProductSpecificationGenerator2 extends PDFProductSpecificationGenerator2 {

    private static final String CLASSNAME = HBIPDFProductSpecificationGenerator2.class.getName();
    public static final String defaultCharsetEncoding = LCSProperties.get("com.lcs.wc.util.CharsetFilter.Charset",
            "UTF-8");

    @SuppressWarnings("unused")
    private static String authgwUrl = "";

    public static String PRODUCT_ID = "PRODUCT_ID";
    public static String PRODUCT_MASTER_ID = "PRODUCT_MASTER_ID";
    public static String SPEC_ID = "SPEC_ID";
    public static String SEASONMASTER_ID = "SEASONMASTER_ID";
    public static String COMPONENT_ID = "COMPONENT_ID";
    public static String TYPE_COMP_DELIM = "-:-";

    public static String HEADER_HEIGHT = "HEADER_HEIGHT";

    public static String PAGE_SIZE = "PAGE_SIZE";
    public static String LANDSCAPE = "LANDSCAPE";
    public static String PORTRAIT = "PORTRAIT";
    public static String PAGE_OPTIONS = "PAGE_OPTIONS";
    public static String SPEC_PAGE_OPTIONS = "SPEC_PAGE_OPTIONS";
    public static String COMPONENT_PAGE_OPTIONS = "COMPONENT_PAGE_OPTIONS";
    public static String BOM_SECTION_VIEWS = "BOM_SECTION_VIEWS";
    public static String COLORWAYS = "COLORWAYS";
    public static String SOURCES = "SOURCES";
    public static String DESTINATIONS = "DESTINATIONS";
    public static String COLORWAYS_PER_PAGE = "COLORWAYS_PER_PAGE";
    public static String SIZES_PER_PAGE = "SIZES_PER_PAGE";
    public static String SHOW_CHANGE_SINCE = "SHOW_CHANGE_SINCE";
    public static String SIZES1 = "SIZES1";
    public static String SIZES2 = "SIZES2";
    public static String PRODUCT_SIZE_CAT_ID = "PRODUCT_SIZE_CAT_ID";
    public static String REPORT_NAME = "REPORT_NAME";
    public static String REPORT_KEY = "REPORT_KEY";
    public static String SPEC_CAD_FILTER = "SPEC_CAD_FILTER";
    public static String SPEC_CAD_DOCS = "SPEC_CAD_DOCS";
    public static String NON_COMPONENT_REPORTS = "NON_COMPONENT_REPORTS";
    public static String SPEC_PART_FILTER = "SPEC_PART_FILTER";
    public static String SPEC_PARTS = "SPEC_PARTS";
    public static String SHOW_INDENTED_BOM = "SHOW_INDENTED_BOM";
    public static String INCLUDE_BOM_OWNER_VARIATIONS = "INCLUDE_BOM_OWNER_VARIATIONS";
    public static String INCLUDE_MEASUREMENTS_OWNER_SIZES = "INCLUDE_MEASUREMENTS_OWNER_SIZES";
    // Component Type
    public static final String BOM = "BOM";
    public static final String BOL = "BOL";
    public static final String CONSTRUCTION = "Construction";
    public static final String IMAGE_PAGES = "Images Page";
    public static final String MEASUREMENTS = "Measurements";
    // Added HBI
    public static final String BASIC_CUT_AND_SEW_COLORWAY = "BASIC CUT & SEW - COLORWAY";
    public static final String BASIC_CUT_AND_SEW_PATTERN = "BASIC CUT & SEW - PATTERN";
    public static final String BASIC_CUT_AND_SEW_GARMENT = "BASIC CUT & SEW - GARMENT";
    public static final String HBI_SUPPORTING_IMAGE = "HBI-SUPPORTING\\IMAGE";
    public static final String BASIC_CUT_AND_SEW_SELLING = "BASIC CUT & SEW - SELLING";
    private static final String BOM_COLORWAY = LCSProperties.get("hbi.gp.colorwaybom.type", "BOM\\Materials\\HBI\\Colorway");
    public static final String GARMENT_BOM_TYPE_PATH = LCSProperties.get("bomreport.sewusagebom.garmentproduct.bomtypes");
    private static final String PATTERN_SPECID = "PATTERN_SPECID";
    private static final String SECTION_GARMENT = "garment";
    private static final String PATTERN_SEW_BOM = LCSProperties.get("hbi.pp.sewbom.type", "BOM\\Materials\\HBI\\Pattern Product Sew BOM");
    public Collection pageTitles = new ArrayList();
    protected static final boolean BOM_ON_SINGLE_PAGE = LCSProperties
            .getBoolean("com.lcs.wc.product.PDFProductSpecificationGenerator.BOMonSinglePage");
    // ** Added for Pulling of Pattern Product Components automatically into
    // output */
    public static final Boolean VIEW_PATTERN_SPEC_IN_CHOOSER = LCSProperties
            .getBoolean("com.hbi.wc.flexbom.util.HBIPatternSpec.ViewPatternSpecInChooser");

    private LCSProduct product = null;
    private FlexSpecification spec = null;
    private LCSSeasonMaster seasonMaster = null;
    private String productName = "";
    private String specName = "";
    private String fileOutName = null;
    private String outputFile = null;
    private String zipFileName = null;
    private Map<String, Object> params = new HashMap<String, Object>();
    public String returnURL = "";
    String techPackStatus = "";
    public Rectangle ps = PageSize.LETTER;
    public boolean landscape = false;
    public boolean isGPProduct = false;
    private PDFHeader pdfHeader = null;
    public static ClassLoadUtil clu = null;
    public float cellHeight = 15.0f;
    float hhFudge = 20.0f + this.cellHeight;
    public String fontClass = "TABLESECTIONHEADER";
    public String outputLocation = "";
    public String outputURL = "";
    public Collection<String> pages;
    private HashMap<String, Object> compHeaderFooterClasses = new HashMap<String, Object>(5);

    // PDFProductSpecPageHeaderGenerator ppsphg = new
    // PDFProductSpecPageHeaderGenerator();
    // Added for HBI
    HBIPDFProductSpecPageHeaderGenerator ppsphg = new HBIPDFProductSpecPageHeaderGenerator();
    HBIPDFGPProductSpecPageHeaderGenerator ppsgpphg = new HBIPDFGPProductSpecPageHeaderGenerator();
    // end
    PDFGeneratorHelper pgh = new PDFGeneratorHelper();

    private PdfWriter writer = null;

    String propertyFile = null;

    protected static final String PDF_OVERRIDE_CLASS = LCSProperties
            .get("com.lcs.wc.product.ProductPDFSpecificationGenerationClass");

    public static boolean DEBUG = LCSProperties
            .getBoolean("com.lcs.wc.product.PDFProductSpecificationGenerator2.verbose");

    private static final int DEBUG_LEVEL = Integer
            .parseInt(LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationGenerator2.verboseLevel", "1"));
    // added for Hbi
    private static final boolean USE_RELATIVE_URL = LCSProperties
            .getBoolean("com.lcs.wc.product.PDFProductSpecificationGenerator2.useRelativeURL");

    public float sideMargins = (new Float(
            LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationGenerator2.pageMargin", "1.0"))).floatValue();
    // public float bottomMargin = (new
    // Float(LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationGenerator2.pageMargin",
    // "5.0"))).floatValue();
    // added for Hbi
    public float bottomMargin = (new Float(
            LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationGenerator2.pageMargin", "20.0"))).floatValue();

    // Start HBI SP tech pack customization name change 02/04/2019

    public static final String PROD_SEQ_NUM = LCSProperties.get("com.hbi.wc.product.hbiProductSequenceNo",
            "hbiProductSequenceNo");
    public static final String SELLING_STYLE_NUM = LCSProperties.get("com.hbi.wc.product.hbiSellingStyleNumber",
            "hbiSellingStyleNumber");
    public static final String APS_PACK_QTY = LCSProperties.get("com.hbi.wc.product.hbiAPSPackQuantity",
            "hbiAPSPackQuantity");

    public static final String ERP_ATT_CODE = LCSProperties.get("com.hbi.wc.product.hbiErpAttributionCode",
            "hbiErpAttributionCode");

    public static final String LEGAL_GPREP[] = LCSProperties.get("com.hbi.legal.GPReport").split(",");
    //Used to get request types selected by user in select page. This is set and used only for Garment Product now. Nov 2019
    public static String reqTypeStr = "";
    // end
    // added for Hbi

    static {
        try {
            java.net.URL authgwURL = GatewayServletHelper.buildAuthenticatedURL(new URLFactory());
            authgwUrl = (USE_RELATIVE_URL) ? authgwURL.getPath() : authgwURL.toString();

        } catch (Exception e) {
            debug("Error initializing cache for ExcelGeneratorHelper");
            e.printStackTrace();
        }
    }

    /**
     * Creates a new instance of PDFProductSpecificationGenerator
     *
     * @param config The specificaiton for which to generate a PDF Specification
     * @throws WTException
     */
    public HBIPDFProductSpecificationGenerator2(LCSProduct product) throws WTException {
        this.product = product;
        init();
    }

    public HBIPDFProductSpecificationGenerator2(FlexSpecification spec) throws WTException {
        this.spec = spec;
        this.specName = (String) this.spec.getValue("specName");
        this.specName = FormatHelper.replaceCharacter(this.specName, ":", "_");
        init();
    }

    public HBIPDFProductSpecificationGenerator2(FlexSpecification spec, String propertyFile) throws WTException {
        this.spec = spec;
        this.specName = (String) this.spec.getValue("specName");
        this.specName = FormatHelper.replaceCharacter(this.specName, ":", "_");
        this.propertyFile = propertyFile;
        init();
    }

    public HBIPDFProductSpecificationGenerator2(FlexSpecToSeasonLink fstsl) throws WTException {
        this((FlexSpecification) VersionHelper.latestIterationOf(fstsl.getSpecificationMaster()),
                fstsl.getSeasonMaster());
    }

    public HBIPDFProductSpecificationGenerator2(FlexSpecification spec, LCSSeasonMaster seasonMaster) throws WTException {
        this.spec = spec;
        this.specName = (String) this.spec.getValue("specName");
        this.specName = FormatHelper.replaceCharacter(this.specName, ":", "_");
        this.seasonMaster = seasonMaster;
        init();
    }

    protected HBIPDFProductSpecificationGenerator2() {

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static PDFProductSpecificationGenerator2 getOverrideInstance(Object[] arguments) throws WTException {
        PDFProductSpecificationGenerator2 ppsg = null;

        try {
            Class pdfGenClass = PDFProductSpecificationGenerator2.class;
            if (FormatHelper.hasContent(PDF_OVERRIDE_CLASS)
                    && !"com.lcs.wc.product.PDFProductSpecificationGenerator2".equals(PDF_OVERRIDE_CLASS)) {
                pdfGenClass = Class.forName(PDF_OVERRIDE_CLASS);
            }

            Class[] argTypes = new Class[arguments.length];

            for (int i = 0; i < arguments.length; i++) {
                argTypes[i] = arguments[i].getClass();
            }

            Constructor pdfGenConstructor = pdfGenClass.getConstructor(argTypes);

            ppsg = (PDFProductSpecificationGenerator2) pdfGenConstructor.newInstance(arguments);

        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof WTException) {
                throw (WTException) e;
            } else {
                throw new WTException(e);
            }
        }

        return ppsg;

    }

    @Override
    public void init() throws WTException {
        debug(1, CLASSNAME + ".init()");
        try {
            if (FormatHelper.hasContent(this.propertyFile)) {
                clu = new ClassLoadUtil(this.propertyFile);
            } else {
                clu = new ClassLoadUtil(FileLocation.productSpecProperties2);
            }

            if (this.product == null && this.spec != null) {
                this.product = (LCSProduct) VersionHelper.getVersion(this.spec.getSpecOwner(), "A");
            }
            String prodId = FormatHelper.getVersionId(this.product);
            String prodMasterId = FormatHelper.getObjectId((LCSPartMaster) this.product.getMaster());

            this.productName = (String) this.product.getValue("productName");

            String userName = ClientContext.getContext().getUser().getName();

            // Start HBI GP tech pack customization - 09/07/2018
            if (this.product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_GARMENT)) {
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-dd-yyyy");
                // For GP the filename should be "productName_Date" example:
                // Productname_Sep-03-2018
                this.fileOutName = this.productName + "_" + simpleDateFormat.format(date);
                this.isGPProduct = true;
            }
            // End HBI GP tech pack customization - 09/07/2018

            // Start HBI SP tech pack customization name change - 02/04/2019, By
            // Chethan

            else if (this.product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_SELLING)) {
                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM-dd-yyyy");
                Long hbiProductSequenceNo = (Long) this.product.getValue(PROD_SEQ_NUM);
                Integer i = hbiProductSequenceNo.intValue();

                String sequence = String.valueOf(i);
                String hbiSellingStyleNumber = (String) this.product.getValue(SELLING_STYLE_NUM);
                String hbiAPSPackQuantity = null;
                AttributeValueList valueList = this.product.getFlexType().getAttribute(APS_PACK_QTY).getAttValueList();
                if (valueList != null) {
                    hbiAPSPackQuantity = valueList.getValue((String) this.product.getValue(APS_PACK_QTY), null);
                }

                LCSLifecycleManaged bo = (LCSLifecycleManaged) this.product.getValue(ERP_ATT_CODE);
                String hbiErpAttributionCode = null;
                if (bo != null) {
                    hbiErpAttributionCode = (String) bo.getValue("name");
                }

                this.productName = "SP-" + sequence + "_" + hbiSellingStyleNumber + "_P" + hbiAPSPackQuantity + "_"
                        + hbiErpAttributionCode;
                this.fileOutName = this.productName + "_" + simpleDateFormat.format(date);
            }
            // End HBI SP tech pack customization name change - 02/04/2019, By
            // Chethan

            // added for Hbi
            else {
                this.fileOutName = "ProductSpec_" + this.productName + "_" + this.specName + "_" + userName;
            }
            // End HBI GP tech pack customization - 09/07/2018

            this.fileOutName = FormatHelper.formatRemoveProblemFileNameChars(this.fileOutName);
            // fileOutName = java.net.URLEncoder.encode(fileOutName,
            // defaultCharsetEncoding);
            this.zipFileName = this.fileOutName + ".zip";
            this.fileOutName = this.fileOutName + ".pdf";
            this.params.put(PRODUCT_ID, prodId);
            this.params.put(PRODUCT_MASTER_ID, prodMasterId);
            this.params.put(SPEC_ID, FormatHelper.getVersionId(this.spec));
            this.params.put(PAGE_SIZE, this.ps);

            if (this.seasonMaster != null) {
                this.params.put(SEASONMASTER_ID, FormatHelper.getObjectId(this.seasonMaster));
            }

            this.techPackStatus = getTechPackStatus(this.spec);
            // Added for GP product HBI Start 12/18/2018
            if (this.isGPProduct) {
                //Removed the INwork text from the page - Jey 6th May 2019
                this.ppsgpphg.headerTextCenter = "";
            } else {
                this.ppsphg.headerTextCenter = "";
            }
            // Added for GP product HBI End 12/18/2018
            boolean useHeader = false;
            if ((this.pages != null && this.pages.contains("HEADER")) || clu.getClass("HEADER") != null) {
                useHeader = true;
            }
            Object header = clu.getClass("HEADER");

            if (useHeader && header != null) {
                this.pdfHeader = (PDFHeader) ((PDFHeader) header).getPDFHeader(this.params);
            }

            if (useHeader && this.pdfHeader != null) {
                // float h = pdfHeader.getHeight();

                float h = this.pdfHeader.getHeight() + this.hhFudge;
                Float hheight = new Float(h);
                this.params.put(HEADER_HEIGHT, hheight);
            } else {
                this.params.put(HEADER_HEIGHT, new Float(0));
            }
            setCompHeaderFooterClasses(this.pages, clu);
        } catch (Exception e) {
            e.printStackTrace();
            throw new WTException(e);
        }

    }

    /**
     * Generates the PDF document for the Spec
     *
     * @return the url to the generated document
     * @throws WTException
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public String generateSpec() throws WTException {
        try {

            // added for hbi
            HBIPDFProductSpecificationCoverPageHeader hbiProdCoverHeader = new HBIPDFProductSpecificationCoverPageHeader();
            // Commented for removing Colorway Placement Table from Tech Pack by
            // UST
            HBIPDFProductSpecColorwayPlacementTable hbiProdSpecColorwayPlsTbl = new HBIPDFProductSpecColorwayPlacementTable();
            // Reeno Commented Start: 2/7/2019
            // HBIApprovedSuppliersTable hbiApprovedSuppliersTbl = new
            // HBIApprovedSuppliersTable();
            // Reeno Commented End: 2/7/2019
            HBISizetable sizeTable = new HBISizetable();
            HBISourceRoutingTable hbiSourceRoutingTbl = new HBISourceRoutingTable();
            HBIRevisionTable hbiRevisionTbl = new HBIRevisionTable();
            HBIPutUpCodeTable hbiPutUpCodeTableObj = new HBIPutUpCodeTable();
            HBIPlantExtensionsTable hbiPlantExtnTableObj = new HBIPlantExtensionsTable();
            HBIAssortmentTable hbiassortmentTableObj = new HBIAssortmentTable();
            // Commented for removing Product Revision Attributes Table from
            // Tech Pack by UST
            // HBIRevisionAttributes hbiRevisionAttrs= new
            // HBIRevisionAttributes();
            // end
            Collection<LCSDocument> imgComp_ids = new ArrayList();
            Locale clientLocale = (Locale) this.params.get("clientLocale");
            // Added for GP product HBI Start 12/18/2018
            if (this.isGPProduct) {
                this.ppsgpphg.fontClass = this.fontClass;
            } else {
                this.ppsphg.fontClass = this.fontClass;
            }
            // Added for GP product HBI End 12/18/2018
            Document doc = null;

            Collection<String> keys = this.pages;

            if (this.isGPProduct) {
                debug("Before keys :: " + keys);
                Collection<String> comps = this.pages;

                imgComp_ids = getFrontAndBackImages(comps);

                // All Front and back images are removed from keys/this.pages
                // collection
                keys = comps;

                debug("imgComp_ids :: " + imgComp_ids);
            }

            /*
             * Changed by UST on 2/20/2019 - START For Pulling of Pattern
             * Product Components automatically into output
             */
            if (!VIEW_PATTERN_SPEC_IN_CHOOSER) {
                keys.addAll(HBISpecificationPDFGenUtil.getPatternSpecs(this.spec));
            }

            /*
             * For Pulling of Pattern Product Components automatically into
             * output - END
             */

            if (keys == null) {
                // keys = clu.getKeyList();
                keys = new ArrayList<String>();
            }

            if (keys.contains("HEADER")) {
                keys.remove("HEADER");
            }

            Map<String, Object> tParams = null;
            Iterator<String> i = keys.iterator();
            Iterator<String> keysItr = keys.iterator();
            int x = 0;

            boolean first = true;
            String item = null;
            String key = null;
            String componentId = null;
            HashMap pageOptions = (HashMap) this.params.get(PAGE_OPTIONS);
            // debug("pageOptions::" + pageOptions);

			/*ArrayList bomoptions = (ArrayList) (pageOptions.get("BOM"));
			debug("bomoptions::" + bomoptions);*/

            ArrayList reqType = (ArrayList) (pageOptions.get("RequestType"));

            // ///////////////////////////////////////HBI//////////////////////////////////////////////////////////////
            // Cover Page Spot
            //
            tParams = new HashMap<String, Object>(this.params.size() + 6);
            if (clientLocale != null)
                tParams.put("clientLocale", clientLocale);
            tParams.putAll(this.params);
            if (clu.getParams(BOM) != null) {
                tParams.putAll(clu.getParams(BOM));
            }

            if (first && this.spec != null && !this.product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_COLORWAY)) {

                doc = prepareDocument((String) tParams.get("orientation"));
                doc.open();

                //Removed Cover Page -Jey 6th May 2019
                //this.ppsphg.headerTextLeft = "Cover Page";
                this.ppsphg.headerTextLeft = "";
                Element e = null;
                PdfPTable elementTable = new PdfPTable(1);
                PdfPCell contentCell = null;
                if ((!this.product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_PATTERN))
                        && (!this.product.getFlexType().getFullName().equals(HBI_SUPPORTING_IMAGE))) {
                    e = hbiProdCoverHeader.getPDFHeader(tParams);
                    elementTable.setWidthPercentage(95);
                    contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                    contentCell.setBorder(0);
                    elementTable.addCell(contentCell);
                }

                // Condition added by UST for Colorway Tech pack generation
                if (this.spec != null) {
                    if ((!this.product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_PATTERN))
                            && (!this.product.getFlexType().getFullName().equals(HBI_SUPPORTING_IMAGE))) {
                        e = hbiProdSpecColorwayPlsTbl.getPDFContent(tParams, doc);
                        contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                        contentCell.setBorder(0);
                        elementTable.addCell(contentCell);
                        elementTable.setSplitLate(false);
                    }

                    PdfPCell spacerCell = null;
                    if (!this.product.getFlexType().getFullName().equals(HBI_SUPPORTING_IMAGE))
                        spacerCell = new PdfPCell(this.pgh.multiFontPara("Sizing Table", this.pgh.getCellFont("FORMLABEL", null, null)));
                    if ((!this.product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_PATTERN))
                            && (!this.product.getFlexType().getFullName().equals(HBI_SUPPORTING_IMAGE))
                            && (!this.product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_SELLING))) {
                        spacerCell.setBorder(0);
                        elementTable.addCell(spacerCell);
                        e = sizeTable.getPDFContent(tParams, doc);
                        contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                        contentCell.setBorder(0);
                        elementTable.addCell(contentCell);
                        // elementTable.setSplitLate(false);
                    }

                    doc.add(elementTable);


                    if (!this.product.getFlexType().getFullName().equals(HBI_SUPPORTING_IMAGE)) {
                        setOrientation((String) tParams.get("orientation"), doc);
                        doc.newPage();
                        //Removed Cover Page text at header -Jey 6th May 2019
                        //this.ppsphg.headerTextLeft = "Cover Page";
                        this.ppsphg.headerTextLeft = "";
                        e = hbiProdCoverHeader.getPDFHeader(tParams);
                        elementTable = new PdfPTable(1);
                        elementTable.setWidthPercentage(95);

                        contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                        contentCell.setBorder(0);
                        elementTable.addCell(contentCell);

                        /*
                         * Start HBI GP Tech Pack customizations -
                         * 09/07/2018 1. if the product type is GP and 2. if
                         * any pattern product linked to GP ?then prints GP
                         * revision table first and then Pattern Product
                         * table. 3.If No pattern pattern product linked to
                         * GP,the only print revision table for GP Note:a)
                         * Revision table with heading
                         * "Garment Product Revision" for GP b) Revision
                         * table with heading "Pattern Product Revision" for
                         * PP
                         */
                        if (this.product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_GARMENT)) {
                            //Condition to hide consolidated revisions table for GPDevelReport & FitSampleRequestReport request types

                            if (reqType != null && !reqType.isEmpty()) {
                                reqTypeStr = (String) reqType.get(0);
                                //No revision table for following request types
                                if (!"GPDevelReport".equalsIgnoreCase(reqTypeStr) && !"FitSampleRequestReport".equalsIgnoreCase(reqTypeStr)) {
                                    e = hbiRevisionTbl.getGarmentProductPDFContent(tParams, doc);
                                    elementTable = getRevisionElementTable("Revision Attributes", e, spacerCell, contentCell, elementTable);
                                    doc.add(elementTable);
                                }
                            }


                            if (true) {
                                Iterator itr = imgComp_ids.iterator();
                                while (itr.hasNext()) {
                                    LCSDocument imgComp = (LCSDocument) itr.next();
                                    Map<String, Object> iParams = new HashMap<String, Object>(this.params.size() + 6);
                                    if (clientLocale != null)
                                        tParams.put("clientLocale", clientLocale);
                                    iParams.putAll(this.params);
                                    // put the component id into the tparams
                                    // map
                                    iParams.put(COMPONENT_ID, FormatHelper.getObjectId(imgComp));
                                    // iParams.put(COMPONENT_PAGE_OPTIONS,
                                    // pageOptions.get(key));

                                    PDFImagePagesCollection2 img = new PDFImagePagesCollection2();
                                    img.setPdfWriter(this.writer);
                                    Collection contents = img.getPDFContentCollection(iParams, doc);
                                    if (contents != null && !contents.isEmpty()) {
                                        doc.newPage();

                                        this.ppsphg.headerTextLeft = "";
                                        e = hbiProdCoverHeader.getPDFHeader(tParams);
                                        elementTable = new PdfPTable(1);
                                        elementTable.setWidthPercentage(95);

                                        contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                                        contentCell.setBorder(0);
                                        elementTable.addCell(contentCell);
                                        spacerCell = new PdfPCell(this.pgh.multiFontPara(imgComp.getName(), this.pgh.getCellFont("FORMLABEL", null, null)));
                                        spacerCell.setBorder(0);
                                        elementTable.addCell(spacerCell);

                                        e = (Element) contents.iterator().next();
                                        elementTable.setWidthPercentage(95);
                                        contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                                        contentCell.setBorder(0);
                                        elementTable.addCell(contentCell);
                                        elementTable.setSplitLate(false);

                                        doc.add(elementTable);


                                    }
                                }
                            }

                            //Commented Sep 2019- Jey, for GPReport, Revision Attributes should come after Sizing Table and before Images Front/Back


                        }
                        // End HBI GP Tech Pack customizations - 09/07/2018

                        else {

                            if (this.product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_SELLING)) {
                                spacerCell = new PdfPCell(this.pgh.multiFontPara("Specification Changes",
                                        this.pgh.getCellFont("FORMLABEL", null, null)));
                                spacerCell.setBorder(0);
                                elementTable.addCell(spacerCell);
                            } else {
                                spacerCell = new PdfPCell(this.pgh.multiFontPara("Product Revision",
                                        this.pgh.getCellFont("FORMLABEL", null, null)));
                                spacerCell.setBorder(0);
                                elementTable.addCell(spacerCell);
                            }

                            e = hbiRevisionTbl.getPDFContent(tParams, doc);
                            contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                            contentCell.setBorder(0);
                            elementTable.addCell(contentCell);
                            elementTable.setSplitLate(false);
                        }
                    }

                    if ((!this.product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_PATTERN))
                            && (!this.product.getFlexType().getFullName().equals(HBI_SUPPORTING_IMAGE))
                            && (!this.product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_SELLING))
                            && !this.product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_GARMENT)) {
                        doc.add(elementTable);
                        setOrientation((String) tParams.get("orientation"), doc);
                        doc.newPage();
                        //Removed Cover Page -Jey 6th May 2019
                        //this.ppsphg.headerTextLeft = "Cover Page";
                        this.ppsphg.headerTextLeft = "";
                        e = hbiProdCoverHeader.getPDFHeader(tParams);

                        elementTable = new PdfPTable(1);
                        elementTable.setWidthPercentage(95);
                        contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                        contentCell.setBorder(0);
                        elementTable.addCell(contentCell);

                        spacerCell = new PdfPCell(this.pgh.multiFontPara("Manufacturing Routing",
                                this.pgh.getCellFont("FORMLABEL", null, null)));
                        spacerCell.setBorder(0);
                        elementTable.addCell(spacerCell);

                        e = hbiSourceRoutingTbl.getPDFContent(tParams, doc);
                        contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                        contentCell.setBorder(0);
                        elementTable.addCell(contentCell);
                        elementTable.setSplitLate(false);
                    }
                    // Start Selling Product cover page3
                    if (this.product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_SELLING)) {
                        doc.add(elementTable);
                        setOrientation((String) tParams.get("orientation"), doc);
                        doc.newPage();
                        //Removed Cover Page -Jey 6th May 2019
                        //this.ppsphg.headerTextLeft = "Cover Page";
                        this.ppsphg.headerTextLeft = "";
                        e = hbiProdCoverHeader.getPDFHeader(tParams);

                        elementTable = new PdfPTable(1);
                        elementTable.setWidthPercentage(95);
                        contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                        contentCell.setBorder(0);
                        elementTable.addCell(contentCell);

                        // START : This logic is using to print Put Up Code
                        // table on Selling Product Spec.

                        spacerCell = new PdfPCell(
                                this.pgh.multiFontPara("Put Up Code", this.pgh.getCellFont("FORMLABEL", null, null)));
                        spacerCell.setBorder(0);
                        elementTable.addCell(spacerCell);

                        e = hbiPutUpCodeTableObj.getPDFContent(tParams, doc);
                        contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                        contentCell.setBorder(0);
                        elementTable.addCell(contentCell);
                        elementTable.setSplitLate(false);
                        // END : This logic is using to print Put Up Code
                        // table

                        // START : This logic is using to print Plant
                        // Extensions
                        // table on Selling Product Spec.
                        spacerCell = new PdfPCell(
                                this.pgh.multiFontPara("Plant Extensions", this.pgh.getCellFont("FORMLABEL", null, null)));
                        spacerCell.setBorder(0);
                        elementTable.addCell(spacerCell);

                        e = hbiPlantExtnTableObj.getPDFContent(tParams, doc);
                        contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                        contentCell.setBorder(0);
                        elementTable.addCell(contentCell);
                        elementTable.setSplitLate(false);
                        // END : This logic is using to print Sizing Table
                        //
                        // table on Selling Product Spec.

                        spacerCell = new PdfPCell(
                                this.pgh.multiFontPara("Sizing Table", this.pgh.getCellFont("FORMLABEL", null, null)));
                        spacerCell.setBorder(0);
                        elementTable.addCell(spacerCell);
                        e = sizeTable.getPDFContent(tParams, doc);
                        contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                        contentCell.setBorder(0);
                        elementTable.addCell(contentCell);

                        doc.add(elementTable);

                        // End Selling Product cover page3

                        // START : This logic is using to print Assortment
                        // table
                        // on Selling Product Spec.
                        setOrientation((String) tParams.get("orientation"), doc);
                        doc.newPage();
                        //Removed Cover Page -Jey 6th May 2019
                        //this.ppsphg.headerTextLeft = "Cover Page";
                        this.ppsphg.headerTextLeft = "";
                        e = hbiProdCoverHeader.getPDFHeader(tParams);

                        elementTable = new PdfPTable(1);
                        elementTable.setWidthPercentage(95);
                        contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                        contentCell.setBorder(0);
                        elementTable.addCell(contentCell);

                        spacerCell = new PdfPCell(
                                this.pgh.multiFontPara("Assortment Table", this.pgh.getCellFont("FORMLABEL", null, null)));
                        spacerCell.setBorder(0);
                        elementTable.addCell(spacerCell);

                        e = hbiassortmentTableObj.getPDFContent(tParams, doc);
                        contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                        contentCell.setBorder(0);
                        elementTable.addCell(contentCell);
                        elementTable.setSplitLate(false);
                    }
                    // END : This logic is using to print Assortment table
                    // on Selling Product Spec.

                }
                if (!this.product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_GARMENT)) {
                    doc.add(elementTable);
                }
                first = false;

            }

            // /////////////////////////////////////////////HBI
            // END/////////////////////////////////////////////////////////////////////
            //HBI DEC 2019- Sew Usage to combine colorway BOMs into a single page
            //Prepare a map to store required BOM data, later print in a single page after all native colorway BOMs
            LinkedHashMap colorwayBOMs = new LinkedHashMap();
            Collection colorwayBOMsColl = new ArrayList();
            String selectedComps = "";
            while (keysItr.hasNext()) {
                selectedComps = (String) keysItr.next();

                if (FormatHelper.hasContent(selectedComps)) {

                    if (selectedComps.indexOf(TYPE_COMP_DELIM) == -1) {

                        continue;
                    }

                } else {
                    continue;
                }
                String selectedCompKey = selectedComps.substring(0, selectedComps.indexOf(TYPE_COMP_DELIM));
                LCSLog.debug("selectedComps " + selectedComps);
                LCSLog.debug("selectedCompKey " + selectedCompKey);
				/*if (selectedCompKey.contains("LCSSourcingConfig")) {

				} else {*/
                if (selectedCompKey.contains("BOM")) {
                    String selectedComponentId = selectedComps.substring(selectedComps.indexOf(TYPE_COMP_DELIM) + TYPE_COMP_DELIM.length());

                    FlexBOMPart bomPart = (FlexBOMPart) LCSQuery.findObjectById(selectedComponentId);
                    if (bomPart != null) {

                        if (bomPart.getFlexType().getFullName(true).equals(BOM_COLORWAY)) {

                            colorwayBOMs.put(selectedComponentId, bomPart);
                            colorwayBOMsColl.add(bomPart);
                        }

                    }

                }

            }
            //Sew Usage BOM count
            int cBOMCount = 0;
            //HBI DEC 2019- Sew Usage to combine colorway BOMs into a single page
            while (i.hasNext()) {
                item = (String) i.next();

                if (FormatHelper.hasContent(item)) {

                    if (item.indexOf(TYPE_COMP_DELIM) == -1) {

                        continue;
                    }

                } else {
                    continue;
                }
                // Cut the key into type, and component id
                key = item.substring(0, item.indexOf(TYPE_COMP_DELIM));
                debug("key::" + key);
                //If other source components came in - This requirement was for Multi SPEC i.e. to add components from other sources as well.
                if (key.contains("LCSSourcingConfig")) {
                    String source = key.substring(key.indexOf("|") + "|".length());
                    debug("source::" + source);

                    this.params.put("multi_source", source);
                } else {
                    this.params.put("multi_source", null);
                }
                if (key.equals(BOL) || key.contains("BOM")) {
                    key = BOM;
                }

                componentId = item.substring(item.indexOf(TYPE_COMP_DELIM) + TYPE_COMP_DELIM.length());

                if (this.isGPProduct && colorwayBOMs.containsKey(componentId)) {
                    cBOMCount++;

                }
                Object obj = clu.getClass(key);
                debug("obj::" + obj);
                //
                tParams = new HashMap<String, Object>(this.params.size() + 6);
                if (clientLocale != null)
                    tParams.put("clientLocale", clientLocale);
                tParams.putAll(this.params);
                // put the component id into the tparams map
                tParams.put(COMPONENT_ID, componentId);
                tParams.put(COMPONENT_PAGE_OPTIONS, pageOptions.get(key));
                componentOwnerCheck(componentId, tParams);

                if (clu.getParams(key) != null) {
                    tParams.putAll(clu.getParams(key));
                }

                if (obj instanceof PDFContent) {
                    debug("PDFContent>>>>>>>>>>>>.");
                    PDFContent content = (PDFContent) obj;
                    String cHeader = ((SpecPage) obj).getPageHeaderString();
                    // Added for GP product HBI Start 12/18/2018
                    if (this.isGPProduct) {
                        this.ppsgpphg.headerTextLeft = cHeader;
                    } else {
                        this.ppsphg.headerTextLeft = cHeader;
                    }
                    // Added for GP product HBI End 12/18/2018
                    if (first) {
                        doc = prepareDocument((String) tParams.get("orientation"));
                        doc.open();
                        first = false;
                    } else {
                        setOrientation((String) tParams.get("orientation"), doc);
                        doc.newPage();
                    }

                    Element e = content.getPDFContent(tParams, doc);

                    x++;

                    PdfPTable elementTable = new PdfPTable(1);

                    if (this.pdfHeader != null) {
                        // doc.add(pdfHeader);
                        // doc.add(pgh.multiFontPara(" "));
                        PdfPCell titleCell = new PdfPCell(PDFUtils.prepareElement(this.pdfHeader));
                        titleCell.setBorder(0);
                        elementTable.addCell(titleCell);

                        PdfPCell spacerCell = new PdfPCell(this.pgh.multiFontPara(" "));
                        spacerCell.setBorder(0);
                        elementTable.addCell(spacerCell);

                    }
                    // doc.add(e);
                    PdfPCell contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                    contentCell.setBorder(0);
                    elementTable.addCell(contentCell);

                    doc.add(elementTable);

                } else if (obj instanceof PDFContentCollection) { // To Do


                    PDFContentCollection content = (PDFContentCollection) obj;
                    Collection contents = new ArrayList();
                    boolean cFirst = true;
                    if (first) {
                        doc = prepareDocument((String) tParams.get("orientation"));
                        doc.open();
                        first = false;
                    } else {
                        setOrientation((String) tParams.get("orientation"), doc);
                        doc.newPage();
                    }
                    try {

                        if (content instanceof PDFImagePagesCollection) {
                            ((PDFImagePagesCollection) content).setPdfWriter(this.writer);
                        } else if (content instanceof PDFImagePagesCollection2) {
                            ((PDFImagePagesCollection2) content).setPdfWriter(this.writer);
                        }
                        // if(!product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_COLORWAY))
                        contents = content.getPDFContentCollection(tParams, doc);

                    } catch (Exception e) {
                        // if(!product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_COLORWAY))
                        e.printStackTrace();
                    }

                    Vector contentHeaders = new Vector(((SpecPageSet) obj).getPageHeaderCollection());

                    Iterator ci = contents.iterator();
                    int pCount = 0;
                    while (ci.hasNext()) {
                        String cHeader = "";
                        if (contentHeaders.size() > 0) {
                            cHeader = (String) contentHeaders.elementAt(pCount);
                        }
                        if (!cFirst) {
                            doc.newPage();
                        } else {
                            cFirst = false;
                        }
                        // This needs to be after the doc.newPage(), because the
                        // header is written in onEndPage()
                        debug("pCount::" + pCount);

                        // Added for GP product HBI Start 12/18/2018
                        if (this.isGPProduct) {
                            this.ppsgpphg.headerTextLeft = cHeader;
                        } else {
                            this.ppsphg.headerTextLeft = cHeader;
                        }
                        // Added for GP product HBI End 12/18/2018
                        pCount++;
                        // --
                        Element e = (Element) ci.next();
                        PdfPTable elementTable = new PdfPTable(1);
                        elementTable.setWidthPercentage(95);


                        //Change the Page Size to legal (Bigger Size) for Label BOM, Routing BOM
                        //Legal standard size is 8.5 inch width(612.0F) and 14 inch height(1008.0F)

                        // Dec 2019 To set all BOMs page size to be Legal i.e. 8.5 x 14 inches and rotate so that width is 14 inch
                        //This is updated now hence commenting this part, all pages should be legal - Dec 2019
						/*if(FormatHelper.hasContent(reqTypeStr) && "GPReport".equalsIgnoreCase(reqTypeStr)){
							if(LEGAL_GPREP.length > 0) {
								for(String legalrep : LEGAL_GPREP) {
									if(cHeader.indexOf(legalrep)!=-1) {
										doc.setPageSize(PageSize.LEGAL.rotate());
										doc.newPage();
										break;
									}
								}
							}
						}*/
						/*if(cHeader.indexOf("GPLabelBOM")!=-1) {
							doc.setPageSize(PageSize.LEGAL.rotate());
							doc.newPage();
						} */
                        if (this.pdfHeader != null) {
                            PdfPCell titleCell = new PdfPCell(PDFUtils.prepareElement(this.pdfHeader));
                            titleCell.setBorder(0);
                            elementTable.addCell(titleCell);

                            PdfPCell spacerCell = new PdfPCell(this.pgh.multiFontPara(" "));
                            spacerCell.setBorder(0);
                            elementTable.addCell(spacerCell);

                        }
                        PdfPCell contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                        contentCell.setBorder(0);
                        contentCell.setVerticalAlignment(Element.ALIGN_CENTER);
                        elementTable.addCell(contentCell);
                        elementTable.setExtendLastRow(false);
                        elementTable.setSplitLate(false);
                        doc.add(elementTable);

                        if (this.isGPProduct && colorwayBOMs.containsKey(componentId)) {
                            if (cBOMCount == colorwayBOMsColl.size()) {
                                //UST-HANES code
                                /**
                                 * Calling below function to add Master Material BOMs All Colorway BOMs are printed
                                 */
                                printMaterLabelBOM(colorwayBOMs, doc, this.params);
                                //After all colorway BOMs are printed, then print combine sew usage for colorway BOMs
                                printSewUsageReport(colorwayBOMs, doc, this.params);

                            }
                        }
                        x++;
                    }
                }
            }
            Collection<String> nonComponentReports = (Collection<String>) this.params.get(NON_COMPONENT_REPORTS);// clu.getAssignableFromKeyList(SpecPageNonComponentReport.class);
            if (!nonComponentReports.isEmpty()) {
                for (String reportKey : nonComponentReports) {
                    doc = generateReport(doc, reportKey);
                    x++;
                }
            }

            // for FlexEPMDocToSpecLink Variations
            if (pageOptions.containsKey("CAD Document Variations")) {
                tParams = new HashMap<String, Object>(this.params);
                key = "CADDOC_VARIATIONS";
                Collection contents = new ArrayList();
                if (doc == null) {
                    doc = prepareDocument((String) tParams.get("orientation"));
                    doc.open();

                }

                Object obj = clu.getClass(key);
                PDFContentCollection content = (PDFContentCollection) obj;
                contents.addAll(content.getPDFContentCollection(tParams, doc));

                if (contents != null && !contents.isEmpty() && contents.size() > 0) {
                    Vector contentHeaders = new Vector(((SpecPageSet) obj).getPageHeaderCollection());

                    Iterator<?> ci = contents.iterator();
                    int pCount = 0;
                    while (ci.hasNext()) {
                        doc.newPage();

                        String cHeader = (String) contentHeaders.elementAt(pCount);
                        debug("cHeader:" + cHeader);
                        // Added for GP product HBI Start 12/18/2018
                        if (this.isGPProduct) {
                            this.ppsgpphg.headerTextLeft = cHeader;
                        } else {
                            this.ppsphg.headerTextLeft = cHeader;
                        }
                        // Added for GP product HBI End 12/18/2018
                        PdfPTable elementTable = new PdfPTable(1);
                        Element e = (Element) ci.next();
                        elementTable.setWidthPercentage(95);

                        if (this.pdfHeader != null) {

                            PdfPCell titleCell = new PdfPCell(PDFUtils.prepareElement(this.pdfHeader));
                            titleCell.setBorder(0);
                            elementTable.addCell(titleCell);

                            PdfPCell spacerCell = new PdfPCell(this.pgh.multiFontPara(" "));
                            spacerCell.setBorder(0);
                            elementTable.addCell(spacerCell);
                        }

                        PdfPCell contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                        contentCell.setBorder(0);
                        elementTable.addCell(contentCell);
                        elementTable.setSplitLate(false);
                        doc.add(elementTable);
                        x++;
                        pCount++;
                    }
                }
            }

            // For Change Tracking report
            if (pageOptions.containsKey("Tracked Changes") && null != tParams) {
                key = "CHANGE_TRACKING";
                Collection contents = new ArrayList();
                List trackedChangesList = new ArrayList();
                trackedChangesList = (ArrayList) pageOptions.get("Tracked Changes");
                Object obj = clu.getClass(key);
                String condensed = "CONDENSED";

                WTObject specObj = (WTObject) LCSProductQuery.findObjectById((String) this.params.get(SPEC_ID));
                if (specObj == null || !(specObj instanceof FlexSpecification)) {
                    throw new WTException(
                            "Can not use PDFProductSpecificationFitSpec on without a FlexSpecification - " + specObj);
                }

                FlexSpecification spec = (FlexSpecification) specObj;
                ((Collection) this.params.get(SPEC_PAGE_OPTIONS)).add(FormatHelper.getVersionId(spec));

                if (trackedChangesList.indexOf(ChangeTrackingPDFGenerator.EXPANDED_REPORT) > -1) {
                    tParams.put(condensed, new Boolean(false));
                    PDFContentCollection content = (PDFContentCollection) obj;
                    contents.addAll(content.getPDFContentCollection(tParams, doc));
                }

                if (trackedChangesList.indexOf(ChangeTrackingPDFGenerator.CONDENSED_REPORT) > -1) {
                    tParams.put(condensed, new Boolean(true));
                    PDFContentCollection content = (PDFContentCollection) obj;
                    contents.addAll(content.getPDFContentCollection(tParams, doc));
                }

                if (contents != null && !contents.isEmpty() && contents.size() > 0) {
                    Vector contentHeaders = new Vector(((SpecPageSet) obj).getPageHeaderCollection());

                    Iterator<?> ci = contents.iterator();
                    int pCount = 0;
                    while (ci.hasNext()) {
                        doc.newPage();

                        String cHeader = (String) contentHeaders.elementAt(pCount);
                        debug("cHeader:" + cHeader);
                        // Added for GP product HBI Start 12/18/2018
                        if (this.isGPProduct) {
                            this.ppsgpphg.headerTextLeft = cHeader;
                        } else {
                            this.ppsphg.headerTextLeft = cHeader;
                        }
                        // Added for GP product HBI End 12/18/2018
                        PdfPTable elementTable = new PdfPTable(1);
                        Element e = (Element) ci.next();
                        elementTable.setWidthPercentage(95);

                        if (this.pdfHeader != null) {

                            PdfPCell titleCell = new PdfPCell(PDFUtils.prepareElement(this.pdfHeader));
                            titleCell.setBorder(0);
                            elementTable.addCell(titleCell);

                            PdfPCell spacerCell = new PdfPCell(this.pgh.multiFontPara(" "));
                            spacerCell.setBorder(0);
                            elementTable.addCell(spacerCell);
                        }

                        PdfPCell contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                        contentCell.setBorder(0);
                        elementTable.addCell(contentCell);
                        elementTable.setSplitLate(false);
                        doc.add(elementTable);
                        x++;
                        pCount++;
                    }
                }
            }

            if (x < 1 && !keys.isEmpty()) {
                if (doc == null || !(doc.isOpen())) {
                    doc = prepareDocument((String) this.params.get("orientation"));
                    doc.open();
                }

                Paragraph p = this.pgh.multiFontPara(
                        WTMessage.getLocalizedMessage(RB.PRODUCT, "noPagesGeneratedForSpec_MSG", RB.objA));
                if (p.size() < 1) {
                    String noPagesGeneratedForSpec_MSG_EN = WTMessage.getLocalizedMessage(RB.PRODUCT,
                            "noPagesGeneratedForSpec_MSG", RB.objA, Locale.ENGLISH);
                    Object[] objB = {SessionHelper.getLocale(), noPagesGeneratedForSpec_MSG_EN};
                    String emptyMsg = wt.util.WTMessage.getLocalizedMessage(RB.PRODUCT, "couldNotFindFont_MSG", objB,
                            Locale.ENGLISH);
                    p = this.pgh.multiFontPara(emptyMsg);
                }
                doc.add(p);
            }

            doc.close();

            return this.outputFile;
        } catch (Exception e) {
            e.printStackTrace();
            throw new WTException(e);
        }
    }


    /**
     * @param keys
     * @return
     * @throws WTException
     * @author Manoj Konakalla
     * @Date May 09,2019
     * This method filters all the images page of type 'frontSketch'
     * and remove those frontSketch image components from keys collection
     */
    private Collection<LCSDocument> getFrontAndBackImages(Collection<String> keys) throws WTException {
        Collection<LCSDocument> imgs = new ArrayList();
        ArrayList comps = new ArrayList(keys);
        Iterator itr = comps.iterator();
        while (itr.hasNext()) {
            String item = (String) itr.next();
            //Code Upgrade by Wipro Team
            String key = "";
            if (item.indexOf(TYPE_COMP_DELIM) != -1) {
                key = item.substring(0, item.indexOf(TYPE_COMP_DELIM));
            }
            //Code Upgrade by Wipro Team
            if ("Images Page".equals(key)) {
                String componentId = item.substring(item.indexOf(TYPE_COMP_DELIM) + TYPE_COMP_DELIM.length());
                LCSDocument imageDoc = (LCSDocument) LCSDocumentQuery.findObjectById(componentId);
                String pageType = (String) imageDoc.getValue("pageType");
                if ("frontSketch".equals(pageType)) {
                    imgs.add(imageDoc);
                    keys.remove(item);
                }
            }
        }
        return imgs;
    }

    /**
     * @param revisionTableName
     * @param e
     * @param spacerCell
     * @param contentCell
     * @param elementTable
     * @return PdfPTable
     * @Date 09/07/18 This method returns the revision table to be printed in
     * techpack
     */
    private PdfPTable getRevisionElementTable(String revisionTableName, Element e, PdfPCell spacerCell,
                                              PdfPCell contentCell, PdfPTable elementTable) {

        spacerCell = new PdfPCell(this.pgh.multiFontPara(revisionTableName, this.pgh.getCellFont("FORMLABEL", null, null)));
        spacerCell.setBorder(0);
        elementTable.addCell(spacerCell);
        contentCell = new PdfPCell(PDFUtils.prepareElement(e));
        contentCell.setBorder(0);
        elementTable.addCell(contentCell);
        elementTable.setSplitLate(false);

        return elementTable;
    }

    /**
     * @param product
     * @return
     * @throws WTException
     * @Date 09/07/18 This function return "pattern product" linked to Garment
     * Product, if only a single "pattern product" linked to "garment
     * product", else return null.
     */
    @SuppressWarnings({"static-access"})
    public static LCSProduct findPatternProdLinkedToGP(LCSProduct garmentProduct) throws WTException {
        LCSProductQuery prodquery = new LCSProductQuery();
        LCSProduct linkedPatternProduct = null;
        String linktype = "Pattern-Garment";

        Vector<FlexObject> linkedProducts = (Vector<FlexObject>) prodquery.getLinkedProducts(
                FormatHelper.getObjectId((LCSPartMaster) garmentProduct.getMaster()), false, true, linktype);

        debug("Number of Linked pattern Products to Garment Product: " + linkedProducts.size());
        // Check if only one Pattern product linked
        if (linkedProducts != null && linkedProducts.size() == 1 && linkedProducts.get(0) != null) {
            FlexObject linkproduct = linkedProducts.get(0);
            debug("linkproduct: " + linkproduct);

            linkedPatternProduct = findProduct(linkproduct.getString("PARENTPRODUCT.IDA3MASTERREFERENCE"));
            return linkedPatternProduct;
        }
        return linkedPatternProduct;

    }

    /**
     * @param ProductIda3MasterRef
     * @return lcsProduct
     * @throws WTException
     * @Date 09/07/18 This method returns a latest revison product for the
     * productIda3MasterRef passed .
     */
    @SuppressWarnings("rawtypes")
    private static LCSProduct findProduct(String productIda3MasterRef) throws WTException {
        LCSProduct product = null;
        PreparedQueryStatement stmt = new PreparedQueryStatement();
        stmt.appendFromTable("LCSProduct", "product");
        stmt.appendSelectColumn("product", "ida2a2");
        stmt.appendOpenParen();
        stmt.appendCriteria(new Criteria("product", "ida3Masterreference", productIda3MasterRef, Criteria.EQUALS));
        stmt.appendAnd();
        stmt.appendCriteria(new Criteria("product", "latestIterationInfo", "1", Criteria.EQUALS));
        stmt.appendAnd();
        stmt.appendCriteria(new Criteria("product", "versionida2versioninfo", "A", Criteria.EQUALS));
        stmt.appendClosedParen();

        debug("stmt........" + stmt.toString());
        Vector output = LCSQuery.runDirectQuery(stmt).getResults();
        debug("size: " + output.size());
        if (output.size() == 1) {
            FlexObject obj = (FlexObject) output.get(0);
            product = (LCSProduct) LCSQuery
                    .findObjectById("OR:com.lcs.wc.product.LCSProduct:" + obj.getData("PRODUCT.IDA2A2"));
            return product;
        }
        return product;

    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected Document generateReport(Document doc, String key) throws WTException {
        try {
            Object obj = clu.getClass(key);
            Map<String, Object> tParams = new HashMap<String, Object>(this.params);
            Collection contents = new ArrayList();
            if (doc == null) {
                doc = prepareDocument((String) tParams.get("orientation"));
                doc.open();
            } else {
                setOrientation((String) tParams.get("orientation"), doc);
            }

            if (obj instanceof PDFContent) {
                PDFContent content = (PDFContent) obj;
                String cHeader = ((SpecPage) obj).getPageHeaderString();
                // Added for GP product HBI Start 12/18/2018
                if (this.isGPProduct) {
                    this.ppsgpphg.headerTextLeft = cHeader;
                } else {
                    this.ppsphg.headerTextLeft = cHeader;
                }
                // Added for GP product HBI End 12/18/2018
                Element e = content.getPDFContent(tParams, doc);
                PdfPTable elementTable = new PdfPTable(1);

                if (this.pdfHeader != null) {
                    // doc.add(pdfHeader);
                    // doc.add(pgh.multiFontPara(" "));
                    PdfPCell titleCell = new PdfPCell(PDFUtils.prepareElement(this.pdfHeader));
                    titleCell.setBorder(0);
                    elementTable.addCell(titleCell);

                    PdfPCell spacerCell = new PdfPCell(this.pgh.multiFontPara(" "));
                    spacerCell.setBorder(0);
                    elementTable.addCell(spacerCell);

                }
                // doc.add(e);
                PdfPCell contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                contentCell.setBorder(0);
                elementTable.addCell(contentCell);

                doc.add(elementTable);

            } else if (obj instanceof PDFContentCollection) {
                PDFContentCollection content = (PDFContentCollection) obj;
                contents.addAll(content.getPDFContentCollection(tParams, doc));

                if (contents != null && !contents.isEmpty() && contents.size() > 0) {
                    Vector contentHeaders = new Vector(((SpecPageSet) obj).getPageHeaderCollection());

                    Iterator<?> ci = contents.iterator();
                    int pCount = 0;
                    while (ci.hasNext()) {
                        doc.newPage();

                        String cHeader = (String) contentHeaders.elementAt(pCount);
                        debug("cHeader:" + cHeader);
                        // Added for GP product HBI Start 12/18/2018
                        if (this.isGPProduct) {
                            this.ppsgpphg.headerTextLeft = cHeader;
                        } else {
                            this.ppsphg.headerTextLeft = cHeader;
                        }
                        // Added for GP product HBI End 12/18/2018
                        PdfPTable elementTable = new PdfPTable(1);
                        Element e = (Element) ci.next();
                        elementTable.setWidthPercentage(95);

                        if (this.pdfHeader != null) {

                            PdfPCell titleCell = new PdfPCell(PDFUtils.prepareElement(this.pdfHeader));
                            titleCell.setBorder(0);
                            elementTable.addCell(titleCell);

                            PdfPCell spacerCell = new PdfPCell(this.pgh.multiFontPara(" "));
                            spacerCell.setBorder(0);
                            elementTable.addCell(spacerCell);
                        }

                        PdfPCell contentCell = new PdfPCell(PDFUtils.prepareElement(e));
                        contentCell.setBorder(0);
                        elementTable.addCell(contentCell);
                        elementTable.setSplitLate(false);
                        doc.add(elementTable);
                        pCount++;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new WTException(e);
        }
        return doc;

    }

    // added for Hbi
    public String getZipName() {
        return this.zipFileName;
    }

    private Document prepareDocument(String orientation) throws WTException {
        try {
            String outDirStr = null;

            if (FormatHelper.hasContent(this.outputLocation)) {
                outDirStr = this.outputLocation;
            } else {
                outDirStr = FileLocation.PDFDownloadLocationFiles;
            }

            File outFile = new File(outDirStr, this.fileOutName);

            outFile = FileRenamer.rename(outFile);
            this.fileOutName = outFile.getName();
            this.outputFile = outFile.getAbsolutePath();

            FileOutputStream outStream = new FileOutputStream(outFile);

            Document pdfDoc = new Document();
            pdfDoc.setMargins(this.sideMargins, this.sideMargins, this.cellHeight, this.bottomMargin);

            this.writer = PdfWriter.getInstance(pdfDoc, outStream);
            // Added for GP product HBI Start 12/18/2018
            if (this.isGPProduct) {
                this.writer.setPageEvent(this.ppsgpphg);
            } else {
                this.writer.setPageEvent(this.ppsphg);
            }
            // Added for GP product HBI End 12/18/2018
            setOrientation(orientation, pdfDoc);
            // added for Hbi
            this.fileOutName = java.net.URLEncoder.encode(this.fileOutName, defaultCharsetEncoding);

            if (FormatHelper.hasContent(this.outputURL)) {
                this.returnURL = this.outputURL + this.fileOutName;
            } else {
                // has trailing file.separator
                String filepath = FormatHelper.formatOSFolderLocation(FileLocation.PDFDownloadLocationFiles);

                // Set the URL for this generated temp file
                this.returnURL = filepath + this.fileOutName;

            }

            return pdfDoc;
        } catch (Exception e) {
            e.printStackTrace();
            throw new WTException(e);
        }
    }

    @Override
    public String getURL() {
        return this.returnURL;
    }

    private void setOrientation(String orientation, Document doc) {
        //LCSLog.debug("orientation: " + orientation);
        //HBI DEC 2019 - All pages to be of legal size
        //Added this instead of using this.ps
        Rectangle ps = PageSize.LEGAL;
        //LCSLog.debug("ps: " + this.ps);
        if (FormatHelper.hasContent(orientation)) {
            if (LANDSCAPE.equalsIgnoreCase(orientation)) {
                doc.setPageSize(ps.rotate());
            } else {
                doc.setPageSize(ps);
            }
        } else {
            if (this.landscape) {
                doc.setPageSize(ps.rotate());
            } else {
                doc.setPageSize(ps);
            }
        }

        debug("page size for render: " + doc.getPageSize());
    }

    /**
     * sets the page size for teh generated document
     * <p>
     * NOTE: Use com.lcs.wc.client.web.pdf.PDFPageSize to get the correct page
     * sizes
     *
     * @param pageSize
     */
    @Override
    public void setPageSize(Rectangle pageSize) {
        this.ps = pageSize;
    }

    /**
     * sets whether or not the generated document should use landscape
     * orientation
     *
     * @param landscape
     */
    @Override
    public void setLandscape(boolean landscape) {
        this.landscape = landscape;
    }

    /**
     * Gets the status of the given SourcingConfig. This is used for generating
     * the page title
     * <p>
     * NOTE: Assumes Sourcing Config type has techPackStatus as an attribute If
     * it is not there then Developement is returned
     *
     * @param sourcingConfig
     * @return
     * @throws WTException
     */
    @Override
    public String getTechPackStatus(FlexSpecification spec) throws WTException {
        String tps = "";
        if (spec != null) {
            tps = spec.getLifeCycleState().getDisplay(ClientContext.getContextLocale());
        }
        return tps;
    }

    /**
     * Gets the path to the PDF file once it is generated
     *
     * @return the path to the PDF file
     */
    @Override
    public String getFilePath() {
        return this.outputFile;
    }

    /**
     * Sets which pages to should be included in the Spec The list should be a
     * subset of the pages listed in the ProductSpecification.properties under
     * codebase.
     * <p>
     * If no pages are specified, then all pages will be included
     *
     * @param pages
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setPages(Collection pages) {
        this.pages = pages;
    }

    /**
     * Returns what pages are included in the spec generation
     *
     * @return
     */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Collection getPages() {
        if (this.pages != null) {
            return this.pages;
        }

        return clu.getKeyList();
    }

    /**
     * Set Which type of Pages for BOM or other components to print
     *
     * @param pageOptions
     */
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setPageOptions(String pageOptions) {
        debug("setPageOptions(String pageOptions)" + pageOptions);
        HashMap<String, Object> pageOptionsMap = new HashMap<String, Object>();
        Collection<String> componentColl = new ArrayList<String>();
        Collection<String> nonComponentReports = new Vector<String>();

        if (FormatHelper.hasContent(pageOptions)) {
            StringTokenizer token = new MultiCharDelimStringTokenizer(pageOptions, MOAHelper.DELIM);
            String item = null;
            String component = null;
            String pageOption = null;
            Object cObj = null;
            while (token.hasMoreTokens()) {
                item = token.nextToken();
                debug(2, "item :  " + item);
                if (!FormatHelper.hasContent(item)) {
                    continue;
                }
                component = item.substring(0, item.indexOf(":"));
                pageOption = item.substring(item.indexOf(":") + 1).trim();

                //component BOM
                //pageOption GPsewUsageBOM
                if (pageOptionsMap.containsKey(component)) {
                    componentColl = (Collection) pageOptionsMap.get(component);
                    componentColl.add(pageOption);
                } else {
                    componentColl = new ArrayList<String>();
                    componentColl.add(pageOption);
                    pageOptionsMap.put(component, componentColl);
                }
                cObj = clu.getClass(pageOption);
                if (cObj instanceof SpecPageNonComponentReport) {
                    nonComponentReports.add(pageOption);
                }
            }
        }

        this.params.put(PAGE_OPTIONS, pageOptionsMap);
        this.params.put(NON_COMPONENT_REPORTS, nonComponentReports);

    }

    /**
     * Set Which components of Specification to print
     *
     * @param specPageOptions
     */
    @Override
    public void setSpecPageOptions(String specPageOptions) {
        Collection<String> pageOptionsMap = new ArrayList<String>();
        if (FormatHelper.hasContent(specPageOptions)) {
            StringTokenizer token = new MultiCharDelimStringTokenizer(specPageOptions, MOAHelper.DELIM);
            String item = null;
            String component = null;
            while (token.hasMoreTokens()) {
                item = token.nextToken();
                if (!FormatHelper.hasContent(item)) {
                    continue;
                }
                component = item.substring(item.indexOf("-:-") + 3).trim();
                pageOptionsMap.add(component);
            }
        }
        this.params.put(SPEC_PAGE_OPTIONS, pageOptionsMap);
    }

    /**
     * Set Which components of Specification to print
     *
     * @param specPageOptions
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void setSpecPageOptions(Collection specPageOptions) {
        Collection<String> pageOptionsMap = new ArrayList<String>();
        String item = null;
        String component = null;
        Iterator specPageOptionsIterator = specPageOptions.iterator();
        while (specPageOptionsIterator.hasNext()) {
            item = (String) specPageOptionsIterator.next();
            if (!FormatHelper.hasContent(item)) {
                continue;
            }
            component = item.substring(item.indexOf("-:-") + 3).trim();
            pageOptionsMap.add(component);
        }
        this.params.put(SPEC_PAGE_OPTIONS, pageOptionsMap);
    }

    /**
     * Set Hash map of which view for which BOM section
     *
     * @param BOMSectionViews
     */
    @Override
    public void setBOMSectionViews(String BOMSectionViews) {
        HashMap<String, String> bomSectionViewsMap = new HashMap<String, String>(8);
        if (FormatHelper.hasContent(BOMSectionViews)) {
            String key = null;
            String bomOption = null;
            String bomFlexType = null;
            String section = null;
            String viewId = null;
            String row = null;
            StringTokenizer token = new MultiCharDelimStringTokenizer(BOMSectionViews, MOAHelper.DELIM);

            while (token.hasMoreTokens()) {
                row = token.nextToken();
                if (!FormatHelper.hasContent(row)) {
                    continue;
                }
                debug(2, "row :  " + row);
                String[] rowArray = row.split(TYPE_COMP_DELIM);
                bomOption = rowArray[0];
                bomFlexType = rowArray[1];
                section = rowArray[2];
                viewId = rowArray[3];
                if (!viewId.startsWith("OR:com.lcs.wc.report")) {
                    viewId = "OR:com.lcs.wc.report." + viewId;
                }
                key = bomOption + bomFlexType + section;
                bomSectionViewsMap.put(key, viewId);
            }
        }
        this.params.put(BOM_SECTION_VIEWS, bomSectionViewsMap);
    }

    void setParams(String moa, String key) {
        Collection<String> vec = null;
        if (FormatHelper.hasContent(moa)) {
            vec = MOAHelper.getMOACollection(moa);
        } else {
            vec = new ArrayList<String>();
        }

        this.params.put(key, vec);
    }

    /**
     * Set which EPMDocuments to package
     *
     * @param availEPMDOCDocs
     */
    @Override
    public void setAvailEPMDOCDocs(String availEPMDOCDocs) {
        setParams(availEPMDOCDocs, SPEC_CAD_DOCS);
    }

    /**
     * Set which Windchill filter to use
     *
     * @param cadDocFilter
     */
    @Override
    public void setCadDocFilter(String cadDocFilter) {
        setParams(cadDocFilter, SPEC_CAD_FILTER);
    }

    /**
     * Set which Parts to package
     *
     * @param availParts
     */
    @Override
    public void setAvailParts(String availParts) {
        setParams(availParts, SPEC_PARTS);
    }

    /**
     * Set which Part Windchill filter to use
     *
     * @param partFilter
     */
    @Override
    public void setPartFilter(String partFilter) {
        this.params.put(SPEC_PART_FILTER, partFilter);
    }

    /**
     * Set which Part Windchill filter to use
     *
     * @param showIndentedBOM
     */
    @Override
    public void setShowIndentedBOM(String showIndentedBOM) {
        this.params.put(SHOW_INDENTED_BOM, showIndentedBOM);
    }

    /**
     * Set which colorways to display in the bom
     *
     * @param colorways
     */
    @Override
    public void setColorways(String colorways) {
        setParams(colorways, BomDataGenerator.COLORWAYS);
    }

    /**
     * Set the sources
     *
     * @param sources
     */
    @Override
    public void setSources(String sources) {
        setParams(sources, BomDataGenerator.SOURCES);
    }

    /**
     * Set the destinations
     *
     * @param destinations
     */
    @Override
    public void setDestinations(String destinations) {
        setParams(destinations, BomDataGenerator.DESTINATIONS);
    }

    /**
     * Set the number of colorways to display on the page
     *
     * @param colorwaysPerPage
     */
    @Override
    public void setColorwaysPerPage(String colorwaysPerPage) {
        Integer intI = Integer.valueOf("0");

        if (FormatHelper.hasContent(colorwaysPerPage)) {
            try {
                intI = Integer.valueOf(colorwaysPerPage);
            } catch (NumberFormatException nfe) {
                // If we get an exception just use 0, aka all sizes
                intI = Integer.valueOf("0");
            }
        }
        this.params.put(COLORWAYS_PER_PAGE, intI);
    }

    /**
     * Set the number of colorways to display on the page
     *
     * @param colorwaysPerPage
     */
    @Override
    public void setSizesPerPage(String sizesPerPage) {
        Integer intI = null;

        if (FormatHelper.hasContent(sizesPerPage)) {
            try {
                intI = Integer.valueOf(sizesPerPage);
            } catch (NumberFormatException nfe) {
                // If we get an exception just use 0, aka all sizes
                intI = Integer.valueOf("0");
            }
        } else {
            intI = Integer.valueOf("0");
        }
        this.params.put(SIZES_PER_PAGE, intI);
    }

    /**
     * Set the size 1 values
     *
     * @param Size1Sizes
     */
    @Override
    public void setSize1Sizes(String size1Sizes) {
        setParams(size1Sizes, SIZES1);
    }

    /**
     * Set the size 2 values
     *
     * @param Size2Sizes New value of property Size2Sizes.
     */
    @Override
    public void setSize2Sizes(String size2Sizes) {
        setParams(size2Sizes, SIZES2);
    }

    /**
     * Set the Product Size Category Id
     */
    @Override
    public void setProductSizeCatId(String sizeCatId) {
        this.params.put(PRODUCT_SIZE_CAT_ID, sizeCatId);
    }

    /**
     * Set the show color swatch boolean
     */
    @Override
    public void setShowColorSwatch(String showColorSwatch) {
        this.params.put(BomDataGenerator.USE_COLOR_SWATCH, showColorSwatch);
    }

    /**
     * Set show Material Thumbnail boolean
     */
    @Override
    public void setShowMatThumbnail(String showMatThumbnail) {
        this.params.put(BomDataGenerator.USE_MAT_THUMBNAIL, showMatThumbnail);
    }

    @Override
    public void setShowChangeSince(String showChangeSince) {
        this.params.put(SHOW_CHANGE_SINCE, showChangeSince);
    }

    /**
     * Set the use size1/size2 value
     *
     * @param useSize1Size2 a String with a value of (size1|size2)
     */
    @Override
    public void setUseSize1Size2(String useSize1Size2) {
        this.params.put(BomDataGenerator.USE_SIZE1_SIZE2, useSize1Size2);
        this.params.put(BomDataGenerator.SIZE_ATT, useSize1Size2);
    }

    /**
     * This method is to add key/value pairs, which are passed on to the actual
     * generators.
     */

    @Override
    public void setAddlParams(Map addParams) {
        this.params.putAll(addParams);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setCompHeaderFooterClasses(Collection pages, ClassLoadUtil clu) {
        debug(1, "setCompHeaderFooterClasses(Collection pages, ClassLoadUtil clu)");
        // debug(2, "pages: " + pages + "\n, clu: " + clu.getKeyList());

        HashMap compMap = new HashMap(2);
        // BOM
        if ((pages != null && pages.contains("BOMHeader")) || clu.getClass("BOMHeader") != null) {
            compMap.put(PDFProductSpecificationBOM2.BOM_HEADER_CLASS, clu.getClass("BOMHeader"));
            if (pages != null && pages.contains("BOMHeader")) {
                pages.remove("BOMHeader");
            }
        }
        if ((pages != null && pages.contains("BOMFooter")) || clu.getClass("BOMFooter") != null) {
            compMap.put(PDFProductSpecificationBOM2.BOM_FOOTER_CLASS, clu.getClass("BOMFooter"));
            if (pages != null && pages.contains("BOMFooter")) {
                pages.remove("BOMFooter");
            }
        }
        this.compHeaderFooterClasses.put(BOM, compMap);
        compMap = new HashMap(2);
        // ///////////////////
        // BOL
        // ///////////////////
        if ((pages != null && pages.contains("BOLHeader")) || clu.getClass("BOLHeader") != null) {
            compMap.put(PDFProductSpecificationBOM2.BOL_HEADER_CLASS, clu.getClass("BOLHeader"));
            if (pages != null && pages.contains("BOLHeader")) {
                pages.remove("BOLHeader");
            }
        }
        if ((pages != null && pages.contains("BOLFooter")) || clu.getClass("BOLFooter") != null) {
            compMap.put(PDFProductSpecificationBOM2.BOL_FOOTER_CLASS, clu.getClass("BOLFooter"));
            if (pages != null && pages.contains("BOLFooter")) {
                pages.remove("BOLFooter");
            }
        }
        this.compHeaderFooterClasses.put(BOL, compMap);
        compMap = new HashMap(2);
        // ///////////////////
        // Construction
        // ///////////////////
        if ((pages != null && pages.contains("ConstructionHeader")) || clu.getClass("ConstructionHeader") != null) {
            compMap.put(PDFProductSpecificationConstruction2.CONSTRUCTION_HEADER_CLASS,
                    clu.getClass("ConstructionHeader"));
            if (pages != null && pages.contains("ConstructionHeader")) {
                pages.remove("ConstructionHeader");
            }
        }
        if ((pages != null && pages.contains("ConstructionFooter")) || clu.getClass("ConstructionFooter") != null) {
            compMap.put(PDFProductSpecificationConstruction2.CONSTRUCTION_FOOTER_CLASS,
                    clu.getClass("ConstructionFooter"));
            if (pages != null && pages.contains("ConstructionFooter")) {
                pages.remove("ConstructionFooter");
            }
        }
        this.compHeaderFooterClasses.put(CONSTRUCTION, compMap);
        compMap = new HashMap(2);
        // ///////////////////
        // ImagePages
        // ///////////////////

        if ((pages != null && pages.contains("ImagePagesHeader")) || clu.getClass("ImagePagesHeader") != null) {
            compMap.put(PDFImagePagesCollection2.IMAGE_PAGES_HEADER_CLASS, clu.getClass("ImagePagesHeader"));
            if (pages != null && pages.contains("ImagePagesHeader")) {
                pages.remove("ImagePagesHeader");
            }
        }
        if ((pages != null && pages.contains("ImagePagesFooter")) || clu.getClass("ImagePagesFooter") != null) {
            compMap.put(PDFImagePagesCollection2.IMAGE_PAGES_FOOTER_CLASS, clu.getClass("ImagePagesFooter"));
            if (pages != null && pages.contains("ImagePagesFooter")) {
                pages.remove("ImagePagesFooter");
            }
        }
        this.compHeaderFooterClasses.put(IMAGE_PAGES, compMap);
        compMap = new HashMap(2);
        // ///////////////////
        // Measurements
        // ///////////////////
        if ((pages != null && pages.contains("MeasurementsHeader")) || clu.getClass("MeasurementsHeader") != null) {
            compMap.put(PDFProductSpecificationMeasurements2.MEASUREMENTS_HEADER_CLASS,
                    clu.getClass("MeasurementsHeader"));
            if (pages != null && pages.contains("MeasurementsHeader")) {
                pages.remove("MeasurementsHeader");
            }
        }
        if ((pages != null && pages.contains("MeasurementsFooter")) || clu.getClass("MeasurementsFooter") != null) {
            compMap.put(PDFProductSpecificationMeasurements2.MEASUREMENTS_FOOTER_CLASS,
                    clu.getClass("MeasurementsFooter"));
            if (pages != null && pages.contains("MeasurementsFooter")) {
                pages.remove("MeasurementsFooter");
            }
        }
        this.compHeaderFooterClasses.put(MEASUREMENTS, compMap);

        // ///////////////////
        // SpecCadDoc Variations
        // ///////////////////
        compMap = new HashMap(2);
        if ((pages != null && pages.contains("SpecCadDocVariationHeader"))
                || clu.getClass("SpecCadDocVariationHeader") != null) {
            compMap.put(PDFCadDocVariationsGenerator.SPECCADDOC_VARIATION_HEADER_CLASS,
                    clu.getClass("SpecCadDocVariationHeader"));
            if (pages != null && pages.contains("SpecCadDocVariationHeader")) {
                pages.remove("SpecPartVariationHeader");
            }
        }
        if ((pages != null && pages.contains("SpecCadDocVariationFooter"))
                || clu.getClass("SpecCadDocVariationFooter") != null) {
            compMap.put(PDFCadDocVariationsGenerator.SPECCADDOC_VARIATION_FOOTER_CLASS,
                    clu.getClass("SpecCadDocVariationFooter"));
            if (pages != null && pages.contains("SpecCadDocVariationFooter")) {
                pages.remove("SpecPartVariationFooter");
            }
        }

        // ///////////////////
        // SpecPart Variations
        // ///////////////////
        compMap = new HashMap(2);

        if ((pages != null && pages.contains("SpecPartVariationHeader"))
                || clu.getClass("SpecPartVariationHeader") != null) {
            compMap.put(PDFCadDocVariationsGenerator.SPECCADDOC_VARIATION_HEADER_CLASS,
                    clu.getClass("SpecPartVariationHeader"));
            if (pages != null && pages.contains("SpecPartVariationHeader")) {
                pages.remove("SpecPartVariationHeader");
            }
        }
        if ((pages != null && pages.contains("SpecPartVariationFooter"))
                || clu.getClass("SpecPartVariationFooter") != null) {
            compMap.put(PDFCadDocVariationsGenerator.SPECCADDOC_VARIATION_FOOTER_CLASS,
                    clu.getClass("SpecPartVariationFooter"));
            if (pages != null && pages.contains("SpecPartVariationFooter")) {
                pages.remove("SpecPartVariationFooter");
            }
        }
        this.compHeaderFooterClasses.put(MEASUREMENTS, compMap);
        debug(1, "--end of setCompHeaderFooterClasses()- compHeaderFooterClasses.keySet():  "
                + this.compHeaderFooterClasses.keySet());
    }

    /**
     * DO A CHECK TO SEE IF THE BOM IS OWNED BY THE SPEC'S PRODUCT IF IT IS NOT
     * THEN NEED TO OVERWRITE THE DIMENSIONS WITH THE DIMENSIONS FROM THE OWNER
     * BOM
     * <p>
     * THIS WILL ALWAYS BE A MAPPING OF ALL DIMENSIONS, IT DOES NOT TRY TO DRAW
     * ANY CORRELATION BETWEEN THE SPEC'S PRODUCT'S DIMENSIONS AND THE ACTUAL
     * OWNER DIMENSIONS
     **/
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void componentOwnerCheck(String compId, Map tparams) throws WTException {
        WTObject component = (WTObject) LCSQuery.findObjectById(compId);
        LCSPartMaster compOwnerMaster = null;

        if (component instanceof FlexBOMPart) {
            compOwnerMaster = (LCSPartMaster) ((FlexBOMPart) component).getOwnerMaster();
        } else if (component instanceof LCSDocument) {
            String ownerId = (String) ((LCSDocument) component).getValue("ownerReference");
            compOwnerMaster = (LCSPartMaster) LCSQuery.findObjectById(ownerId);
        } else if (component instanceof LCSMeasurements) {
            compOwnerMaster = ((LCSMeasurements) component).getProductMaster();
        } else if (component instanceof LCSConstructionInfo) {
            compOwnerMaster = ((LCSConstructionInfo) component).getProductMaster();
        }

        String specOwnerMasterId = (String) tparams.get(PDFProductSpecificationGenerator2.PRODUCT_MASTER_ID);
        String compOwnerMasterId = FormatHelper.getObjectId(compOwnerMaster);
        /*
         * boolean includeBOMVariation = FormatHelper .parseBoolean((String)
         * tparams
         * .get(PDFProductSpecificationGenerator2.INCLUDE_BOM_OWNER_VARIATIONS))
         * ; boolean includeMeasurementsOwnerSizes = FormatHelper
         * .parseBoolean((String) tparams
         * .get(PDFProductSpecificationGenerator2.
         * INCLUDE_MEASUREMENTS_OWNER_SIZES));
         */

        if (!(specOwnerMasterId.equals(compOwnerMasterId))) {
            // Different products
            LCSProduct product = com.lcs.wc.season.SeasonProductLocator.getProductARev(compOwnerMaster);

            PDFMultiSpecGenerator pmsg = new PDFMultiSpecGenerator();

			/*String tsizes1 = pmsg.getSizes1(null, product);
			tparams.put(PDFProductSpecificationGenerator2.SIZES1, MOAHelper.getMOACollection(tsizes1));*/

            String tsizes2 = pmsg.getSizes2(null, product);
            tparams.put(PDFProductSpecificationGenerator2.SIZES2, MOAHelper.getMOACollection(tsizes2));

            String tsources = pmsg.getSources(null, product);
            tparams.put(BomDataGenerator.SOURCES, MOAHelper.getMOACollection(tsources));

            String tdest = pmsg.getDestinations(null, product);
            tparams.put(BomDataGenerator.DESTINATIONS, MOAHelper.getMOACollection(tdest));

            String tcolor = pmsg.getColorways(null, product);
            tparams.put(BomDataGenerator.COLORWAYS, MOAHelper.getMOACollection(tcolor));

            Collection resultVector = SizingQuery.findProductSizeCategoriesForProduct(product).getResults();
            if (resultVector.size() > 0) {
                FlexObject obj = (FlexObject) resultVector.iterator().next();
                tparams.put(PDFProductSpecificationGenerator2.PRODUCT_SIZE_CAT_ID,
                        obj.getString("PRODUCTSIZECATEGORY.IDA2A2"));
            } else {
                tparams.put(PDFProductSpecificationGenerator2.PRODUCT_SIZE_CAT_ID, "");
            }

        }

    }

    // ///////////////////////////////////////////////////////////////////////////
    public static void debug(String msg) {
        debug(msg, 1);
    }

    public static void debug(int i, String msg) {
        debug(msg, i);
    }

    public static void debug(String msg, int i) {
        if (DEBUG && i <= DEBUG_LEVEL)
            System.out.println(msg);
    }

    /**
     * Returns an unmodifiable copy of the params Map on this
     * PDFProductSpecificationGenerator2 instance This allows for testing and
     * viewing the params while preventing their modification.
     *
     * @return
     */
    @Override
    public Map getUnmodifiableParams() {
        return Collections.unmodifiableMap(this.params);
    }

    //Print after all native colorway BOMs are printed
    //This should be called only once.
    private void printSewUsageReport(LinkedHashMap<String, FlexBOMPart> colorwayBOMs, Document doc, Map<String, Object> params2) {

        try {
            System.out.println("printSewUsageReport> called >>>>>>>>>>>>>>>>>");
            //Title page for SEW USAGE Report
            PdfPTable headerTable = new PdfPTable(1);
            headerTable.setWidthPercentage(95);
            HBIPDFProductSpecificationBOM2 specBom = new HBIPDFProductSpecificationBOM2();
            if (this.pdfHeader != null) {
                Boolean sewUsage = true;


                HBISewUsageGenerator bomDG = new HBISewUsageGenerator();
                for (FlexBOMPart gpbomPart : colorwayBOMs.values()) {
                    Collection spcontent = new ArrayList();

                    FlexType bomType = gpbomPart.getFlexType();
                    this.params.put("RAW_DATA", specBom.getBOMData(gpbomPart));

                    FlexType patternBomType = FlexTypeCache.getFlexTypeFromPath(PATTERN_SEW_BOM);

                    FlexSpecification spec = (FlexSpecification) LCSQuery
                            .findObjectById((String) this.params.get(PDFProductSpecificationGenerator2.SPEC_ID));
                    this.params.put(BOMPDFContentGenerator.BOM_PART, gpbomPart);
                    //SEW Usage Report page is generated only for Colorway and SEW BOMs as of DEC 2019
                    //Now all native colorway BOMs sew usage report is combined and generated at a single page.
                    //No change to SEW BOMs, it will have its separate sew usage report. which is selected from the chooser page options
                    boolean gpBOMTechpackStatus = isValidateGPBOMTypeForSewUsageBOMReport(gpbomPart, spec);

                    boolean isValidSpec = isSpecLinkedToPatternProdSpec(spec);
                    System.out.println("gpBOMTechpackStatus>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + gpBOMTechpackStatus);
                    System.out.println("isValidSpec>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + isValidSpec);
                    /*
                     * If the Product type is GP and Spec is linked to a pattern product spec
                     * and pattern product spec is having atleast one pattern sew usage bom.
                     */

                    if (gpBOMTechpackStatus && isValidSpec) {

                        Map tparams = new HashMap(this.params.size() + 3);
                        tparams.putAll(this.params);
                        boolean usingSize1 = true;
                        if ("size2".equalsIgnoreCase((String) this.params.get(BomDataGenerator.USE_SIZE1_SIZE2))) {
                            usingSize1 = false;
                        }
                        Collection allSizes = new ArrayList();
                        if (usingSize1) {
                            allSizes = (Collection) this.params.get(BomDataGenerator.SIZES1);
                        } else {
                            allSizes = (Collection) this.params.get(BomDataGenerator.SIZES2);
                        }
                        int maxPerPage = ((Integer) this.params.get(PDFProductSpecificationGenerator2.SIZES_PER_PAGE)).intValue();

                        // Start Manoj
                        // Get Pattern product sew BOM data
                        Collection patterBomData = getPatternSewBomData(this.params);

                        Collection sections = getSections(gpbomPart);
                        // End Manoj

                        String section = "";
                        Iterator sectionIter = sections.iterator();
                        Iterator sizeIt = null;

                        // Create collection of arrayLists of sizes
                        Collection sizesArray = splitItems(allSizes, maxPerPage);

                        while (sectionIter.hasNext()) {
                            section = (String) sectionIter.next();
                            tparams.put(BomDataGenerator.SECTION, section);
                            //setSectionViewId(tparams);
                            sizeIt = sizesArray.iterator();

                            Collection sizesThisRun = new ArrayList();

                            while (sizeIt.hasNext()) {
                                sizesThisRun = (Collection) sizeIt.next();

                                if (usingSize1) {
                                    tparams.put(BomDataGenerator.SIZES1, sizesThisRun);
                                } else {
                                    tparams.put(BomDataGenerator.SIZES2, sizesThisRun);
                                }
                                bomDG.init(tparams);

                                Collection data = bomDG.getBOMData();

                                Collection columns = bomDG.getTableColumns();

                                // Start Manoj 09/20/2018
                                if (patterBomData != null) {

                                    Collection gpSewUsageBomData = getSewUsageFromPatternBomData1(patterBomData, patternBomType,
                                            data, bomType, sizesThisRun);

                                    spcontent.addAll(generatePDFPage(gpSewUsageBomData, columns, doc, tparams, gpbomPart));
                                }
                            }//Size
                        }//Section


                        PdfPTable e = null;
                        PdfPCell cell = null;
                        PdfPTable fullBOMTable = new PdfPTable(1);
                        fullBOMTable.setWidthPercentage(95);
                        // Add the BOM Header Attributes

                        Iterator sci = spcontent.iterator();
                        while (sci.hasNext()) {
                            e = (PdfPTable) sci.next();
                            cell = new PdfPCell(e);
                            fullBOMTable.addCell(cell);
                            if (sewUsage) {
                                //*************** Adding Header to SEW USAGE REPORT ***********************
                                doc.newPage();
                                this.ppsgpphg.headerTextLeft = "GP COLORWAY BOM - SEW USAGE REPORT";
                                PdfPCell headerCell = new PdfPCell(PDFUtils.prepareElement(this.pdfHeader));
                                headerCell.setBorder(0);
                                headerTable.addCell(headerCell);

                                PdfPCell spacerCell = new PdfPCell(this.pgh.multiFontPara(" "));
                                spacerCell.setBorder(0);
                                headerTable.addCell(spacerCell);
                                doc.add(headerTable);
                                //Add header only once and generate this page if sew usage data there else no need to create new page.
                                sewUsage = false;
                                //*************** Adding Header to SEW USAGE REPORT ***********************
                            }


                            //adding the sew usage table to doc for each colorway bom
                            doc.add(fullBOMTable);
                        }
                    }//Valid Spec
                }//Colorway BOMs iteration
            }//PDF header not null
        } catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @param bomPart
     * @return
     * @throws WTException
     * @author Manoj
     * GP teck pack customizations  Select_teck_pack_page_requirements - 09/20/2018
     */
    private boolean isValidateGPBOMTypeForSewUsageBOMReport(FlexBOMPart bomPart, FlexSpecification spec) throws WTException {


        boolean bomGPTachPackStatus = true;
        LCSProduct prodObj = null;
        FlexType bomType = bomPart.getFlexType();

        String bomPartFlexTypePath = bomType.getFullName(true);
        //Logic for printing only Native Colorway BOM for Sew Usage
 		/*if(spec!=null) {
 			Collection<?> components = FlexSpecQuery.getSpecToComponentObjectsData(spec);
 			FlexObject compFO = null;
			Iterator<?> compIterator = components.iterator();
			boolean isBOMSpec = false;
			  while(compIterator.hasNext()){
				  compFO = (FlexObject) compIterator.next();
				  if(compFO.getData("COMPONENT_TYPE").equals("BOM") && bomPart.getName().equals(compFO.getData("NAME"))) {

					  isBOMSpec = true;
					  break;
				  }
			  }
			  if(!isBOMSpec) {
				  return false;
			  }
 		}*/
// 		WTPartMaster wtPartMaster = bomPart.getOwnerMaster();
        LCSPart lcsPart = (LCSPart) VersionHelper.latestIterationOf(bomPart.getOwnerMaster());
        if (lcsPart instanceof LCSProduct) {
            prodObj = (LCSProduct) lcsPart;
            String productFlexTypePath = prodObj.getFlexType().getFullName(true);


            if (BASIC_CUT_AND_SEW_GARMENT.equalsIgnoreCase(productFlexTypePath) && !bomPartFlexTypePath.equals(BOM_COLORWAY)) {
                bomGPTachPackStatus = false;
            }
        }

        return bomGPTachPackStatus;
    }

    @SuppressWarnings("rawtypes")
    private boolean isSpecLinkedToPatternProdSpec(FlexSpecification spec2) throws WTException {

        if (spec2 != null) {
            LCSProduct product = (LCSProduct) LCSQuery.findObjectById((String) this.params.get(PDFProductSpecificationGenerator2.PRODUCT_ID));
            //Get the linked parent spec of a garment product spec's.
            ArrayList specToSpecLinks = (ArrayList) FlexSpecQuery.findSpecToSpecLinks(product, null, null, spec2, false, true);

            //Get the linked pattern product of Garment product .
            LCSProduct patternProd = HBIPDFProductSpecificationGenerator2.findPatternProdLinkedToGP(product);
            System.out.println("patternProd>>>>>>>>>>" + patternProd);
            //Check if the spec having any linked parent spec.
            if (!specToSpecLinks.isEmpty() && specToSpecLinks.get(0) != null && patternProd != null) {
                FlexObject gpObj = (FlexObject) specToSpecLinks.get(0);
                String linkedSpecida2 = gpObj.getData("LINKEDSPECID");

                //Get all the Spec's associated to linked pattern product.
                SearchResults pSpecToSpecLink = FlexSpecQuery.findSpecsByOwner(patternProd.getMaster(), null, null, null);
                Iterator results = pSpecToSpecLink.getResults().iterator();

                //Verify if the GP's parent spec is a linked pattern product's spec
                while (results.hasNext()) {
                    FlexObject obj = (FlexObject) results.next();
                    if (obj.get("FLEXSPECIFICATION.BRANCHIDITERATIONINFO").equals(linkedSpecida2)) {
                        this.params.put(PATTERN_SPECID, linkedSpecida2);
                        return true;
                    }
                }

            }
        }

        return false;

    }

    private Collection<?> getPatternSewBomData(Map<String, Object> patternParams) throws WTException {

        HBISizeGenerator bomDG = new HBISizeGenerator();
        Collection<?> allSizes = (Collection<?>) patternParams.get(BomDataGenerator.SIZES1);
        boolean usingSize1 = true;
        if ("size2".equalsIgnoreCase((String) patternParams.get(BomDataGenerator.USE_SIZE1_SIZE2))) {
            usingSize1 = false;
        }
        if (usingSize1) {
            patternParams.put(BomDataGenerator.SIZES1, allSizes);
        } else {
            patternParams.put(BomDataGenerator.SIZES2, allSizes);
        }
        HBIPDFProductSpecificationBOM2 specBom = new HBIPDFProductSpecificationBOM2();
        FlexSpecification pSpec = (FlexSpecification) LCSQuery.findObjectById(
                "VR:com.lcs.wc.specification.FlexSpecification:" + (String) patternParams.get(PATTERN_SPECID));

        // Get Pattern sew Bom Data from pattern spec
        FlexBOMPart bomPart = getPatternBomPart(pSpec);

        if (bomPart != null) {
            // FlexBOMPart gpBOM = (FlexBOMPart)params.get(BOMPDFContentGenerator.BOM_PART);
            Collection sections = getSections(bomPart);
            String section = "";
            Iterator sectionIter = sections.iterator();
            while (sectionIter.hasNext()) {

                section = (String) sectionIter.next();

                patternParams.put(BomDataGenerator.SECTION, section);

                patternParams.remove("RAW_DATA");
                patternParams.put("RAW_DATA", specBom.getBOMData(bomPart));
                bomDG.init(patternParams);
                Collection data = bomDG.getBOMData();

                return data;
            }
        }

        return null;
    }

    /**
     * @param pSpec
     * @return
     * @throws WTException
     * @author Manoj
     */
    private FlexBOMPart getPatternBomPart(FlexSpecification pSpec) throws WTException {

        Collection<FlexBOMPart> boms = FlexSpecQuery.getSpecComponents(pSpec, BOM);

        if (boms != null) {

            for (FlexBOMPart bom : boms) {

                FlexType patternBomType = bom.getFlexType();
                if (patternBomType.getFullName(true).equals(PATTERN_SEW_BOM)) {
                    return bom;
                }
            }
        }

        return null;
    }

    private Collection getSections(FlexBOMPart bomPart) throws LCSException, WTException {

        Collection section = new Vector();
        if (bomPart.getFlexType().getFullName(true).equals(BOM_COLORWAY)) {
            section.add(SECTION_GARMENT);
            return section;
        }
        section = bomPart.getFlexType().getAttribute("section").getAttValueList().getSelectableKeys(com.lcs.wc.client.ClientContext.getContext().getLocale(), true);

        return section;
    }

    public static Collection splitItems(Collection items, int maxPerPage) {
        debug("BOMPDFContentGenerator.splitItems:  items-" + items + "  maxPerPage:  " + maxPerPage);
        Collection coll = new ArrayList();
        if (maxPerPage != 0 && maxPerPage < items.size()) {
            int count = 0;
            ArrayList tempColl = new ArrayList();

            Object next;
            for (Iterator it = items.iterator(); it.hasNext(); tempColl.add(next)) {
                next = it.next();
                ++count;
                if (count > maxPerPage) {
                    coll.add(tempColl);
                    tempColl = new ArrayList();
                    count = 1;
                }
            }

            coll.add(tempColl);
        } else {
            coll.add(items);
        }

        debug("BOMPDFContentGenerator.splitItems--returning " + coll);
        return coll;
    }

    private Hashtable getSewUsageFromPatternBomData(Collection patterBomData, FlexType patternType) throws WTException {

        Collection sewUsage = new Vector();
        Hashtable<String, FlexObject> ppMap = new Hashtable<String, FlexObject>();
        Iterator itr = patterBomData.iterator(); // Pattern product BOM Data
        while (itr.hasNext()) {
            Object o = itr.next();
            if (o instanceof FlexObject) {
                FlexObject flex = (FlexObject) o;
                String key1 = getDBColumnName(patternType, "hbiGarmentUse");
                String key2 = getDBColumnName(patternType, "hbiUOM");
                String ppKey = flex.get("FLEXBOMLINK." + key1) + ":" + flex.get("FLEXBOMLINK." + key2);
                ppMap.put(ppKey, (FlexObject) o);
            }
        }
        return ppMap;
    }

    //Pattern P BOM should contain same value of GarmentUSE and UsageUOM as in GP BOM
    private Collection getSewUsageFromPatternBomData1(Collection patterBomData, FlexType patternType, Collection gpBomData, FlexType bomType, Collection sizesThisRun) throws WTException {

        Hashtable<String, FlexObject> ppMap = getSewUsageFromPatternBomData(patterBomData, patternType);

        Collection sewUsageData = new Vector();
        String key1;
        String key2;
        Iterator itr = gpBomData.iterator();

        while (itr.hasNext()) {
            Object gpBOMFo = itr.next();
            if (gpBOMFo instanceof FlexObject) {
                FlexObject gpBOMFO = (FlexObject) gpBOMFo;
                key1 = "FLEXBOMLINK." + getDBColumnName(bomType, "hbiGarmentUse");

                FlexType mat = FlexTypeCache.getFlexTypeFromPath("Material");
                key2 = "LCSMATERIAL." + getDBColumnName(mat, "hbiUsageUOM");

                String gpKey = gpBOMFO.get(key1) + ":" + gpBOMFO.get(key2);    // To get garment product key

                if (ppMap.containsKey(gpKey)) {
                    gpBOMFO = getSewUsageFromPatternBomData2(ppMap.get(gpKey), gpBOMFO, sizesThisRun);
                    sewUsageData.add(gpBOMFO);
                }
				/*else {
					sewUsageData.add(flex2);
				}*/
            }
        }

        return sewUsageData;
    }

    private FlexObject getSewUsageFromPatternBomData2(FlexObject flex1, FlexObject flex2, Collection sizesThisRun) {
        debug("############Started getSewUsageFromPatternBomData2 Method #######");
        FlexObject obj = flex2;
        Iterator itr = sizesThisRun.iterator();
        while (itr.hasNext()) {
            String size = (String) itr.next();
            String sizekey = size.trim() + ".DISPLAY_VAL";
            String usageperdozen = flex1.getData(sizekey);

            debug("sizekey:" + sizekey + "-" + "usageperdozen:" + usageperdozen);
            obj.put(sizekey, usageperdozen);

        }
        debug("############Completed getSewUsageFromPatternBomData2 Method #######");
        return obj;
    }

    public static String getDBColumnName(FlexType flextype, String key) throws WTException {

        FlexTypeAttribute typeAttr = flextype.getAttribute(key);
        //Changed by Wipro Upgrade Team
        //String dbColumnName = typeAttr.getColumnPrefix().concat(typeAttr.getAttColumn());
        String dbColumnName = typeAttr.getColumnName();

        return dbColumnName;
    }

    public Collection generatePDFPage(Collection bom, Collection columns, Document document, Map params, FlexBOMPart gpbomPart)
            throws WTException {

        new ArrayList();
        PDFTableGenerator tg = null;
        tg = new PDFTableGenerator(document);
        tg.cellClassLight = "RPT_TBL";
        tg.cellClassDark = "RPT_TBD";
        tg.tableSubHeaderClass = "RPT_HEADER";
        tg.tableHeaderClass = "TABLE-HEADERTEXT";
        tg.tableSectionHeaderClass = "TABLESECTIONHEADER";
        PdfPCell titleCell = new PdfPCell(this.pgh.multiFontPara(gpbomPart.getName(), this.pgh.getCellFont("FORMLABEL", null, null)));

        tg.setTitleCell(titleCell);
        Collection pdfData = tg.drawTables(bom, columns);
        return pdfData;
    }

    public String getPageTitleText(Map params) {
        String title = "";
        FlexBOMPart bomPart = (FlexBOMPart) params.get("BOM_PART");
        title = bomPart.getName() + " -- " + params.get(PDFProductSpecificationGenerator2.REPORT_NAME);
        return title;
    }

    //UST-HANES code
    /**
     * This function will add Garment Label BOM data to Tech pack report.
     *  - BOM table visible after Colorway BOM.
     *  - table data will appear only if BOM has Relevant dataset.
     * @param colorwayBOMs -  Colorway BOM input
     * @param pdfDoc - PDF Docuemnt object
     * @param params - Tech Ppck apram
     * @throws Exception
     */
    public void printMaterLabelBOM(LinkedHashMap<String, FlexBOMPart> colorwayBOMs, Document pdfDoc, Map<String, Object> params) throws Exception {

        //*************** Adding Header to master MAT BOM REPORT ***********************
        // calling function to fetch and draw  master material bom data.
        List<PdfPTable> masterBOMPDFTableList = new HBIMasterLabelBOMGenerator().printMaterLabelBOM(colorwayBOMs, params);

        //validation :  add data to report only if valid data is present
        if (!masterBOMPDFTableList.isEmpty()) {
            PdfPTable pdfTable = new PdfPTable(1); //main PDF Table object
            pdfTable.setWidthPercentage(95); //setting table width

            pdfDoc.newPage(); // creating new page on document

            //setting PDF Page Header
            this.ppsgpphg.headerTextLeft = "GP COLORWAY BOM - Material BOM REPORT";
            PdfPCell cell = new PdfPCell(PDFUtils.prepareElement(this.pdfHeader)); //adding header
            cell.setBorder(0);
            pdfTable.addCell(cell);

             cell = new PdfPCell(this.pgh.multiFontPara(" ")); //adding spacer cell
            cell.setBorder(0);
            pdfTable.addCell(cell);
            pdfDoc.add(pdfTable);

            //
            /**
             * adding Master Material BOM to Tech pack.
             *  - a new table to be added for each valid Master Material.
             */
            for (PdfPTable MasterBOMPDFTable : masterBOMPDFTableList) {
                if (MasterBOMPDFTable != null) {
                    pdfDoc.add(MasterBOMPDFTable);
                }
            }
        }

        //*************** Adding Header to master MAT BOM REPORT ***********************
    }
}
