/*
 * PDFProductSpecificationGenerator2.java
 *
 * Created on August 23, 2005, 4:06 PM
 */

package com.hbi.wc.product;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import wt.fc.WTObject;
import wt.httpgw.GatewayServletHelper;
import wt.httpgw.URLFactory;
//import wt.part.WTPartMaster;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTMessage;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.client.web.PDFGeneratorHelper;
import com.lcs.wc.client.web.pdf.PDFContent;
import com.lcs.wc.client.web.pdf.PDFContentCollection;
import com.lcs.wc.client.web.pdf.PDFHeader;
import com.lcs.wc.client.web.pdf.PDFUtils;
import com.lcs.wc.construction.LCSConstructionInfo;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.document.FileRenamer;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.epmstruct.PDFCadDocVariationsGenerator;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.gen.BomDataGenerator;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.measurements.LCSMeasurements;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.ChangeTrackingPDFGenerator;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.product.PDFImagePagesCollection;
import com.lcs.wc.product.PDFImagePagesCollection2;
import com.lcs.wc.product.PDFMultiSpecGenerator;
import com.lcs.wc.product.PDFProductSpecificationBOM2;
import com.lcs.wc.product.PDFProductSpecificationConstruction2;
import com.lcs.wc.product.PDFProductSpecificationMeasurements2;
import com.lcs.wc.product.SpecPage;
import com.lcs.wc.product.SpecPageNonComponentReport;
import com.lcs.wc.product.SpecPageSet;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.sizing.SizingQuery;
import com.lcs.wc.specification.FlexSpecToSeasonLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.ClassLoadUtil;
import com.lcs.wc.util.FileLocation;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.MultiCharDelimStringTokenizer;
import com.lcs.wc.util.RB;
import com.lcs.wc.util.VersionHelper;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/** This class generates a Product Specification document in PDF format.
 *
 * It requires a SourcingConfig in order to generate the spec.
 * @author Chuck
 */

// IMP NOTE: 
/////////////////////////////////////////////////////////////////////////////////////////////////////

// This file using as HBI customization file because if we over overide this file we need do lot of maintaince work.
// so we decided to use as HBI customization file.
// for merging take ootb source file and integrate HBI related code.

/////////////////////////////////////////////////////////////////////////////////////////////////////////



public class PDFProductSpecificationGenerator2 {

	private static final String CLASSNAME = PDFProductSpecificationGenerator2.class.getName();
	public static final String defaultCharsetEncoding = LCSProperties.get("com.lcs.wc.util.CharsetFilter.Charset","UTF-8");
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
	public static String SHOW_CHANGE_SINCE="SHOW_CHANGE_SINCE";
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
	//Component Type
	public static final String BOM = "BOM";
	public static final String BOL = "BOL";
	public static final String CONSTRUCTION = "Construction";
	public static final String IMAGE_PAGES = "Images Page";
	public static final String MEASUREMENTS = "Measurements";
	//Added HBI
	public static final String BASIC_CUT_AND_SEW_COLORWAY = "BASIC CUT & SEW - COLORWAY";
	public static final String BASIC_CUT_AND_SEW_PATTERN = "BASIC CUT & SEW - PATTERN";
	public static final String BASIC_CUT_AND_SEW_GARMENT = "BASIC CUT & SEW - GARMENT";
	public static final String HBI_SUPPORTING_IMAGE = "HBI-SUPPORTING\\IMAGE";


	//private static Map classMap = null;

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

	private PDFHeader pdfHeader = null;

	public static ClassLoadUtil clu = null;

	public float cellHeight = 15.0f;

	float hhFudge = 20.0f + cellHeight;

	public String fontClass = "TABLESECTIONHEADER";

	public String outputLocation = "";
	public String outputURL = "";

	public Collection<String> pages;

	private HashMap<String,Object> compHeaderFooterClasses = new HashMap<String,Object>(5);

	//PDFProductSpecPageHeaderGenerator ppsphg = new PDFProductSpecPageHeaderGenerator();
	// Added for HBI
	HBIPDFProductSpecPageHeaderGenerator ppsphg = new HBIPDFProductSpecPageHeaderGenerator();
	//end
	PDFGeneratorHelper pgh = new PDFGeneratorHelper();

	private PdfWriter writer = null;

	String propertyFile = null;

	protected static final String PDF_OVERRIDE_CLASS = LCSProperties.get("com.lcs.wc.product.ProductPDFSpecificationGenerationClass");

	public static boolean DEBUG = LCSProperties.getBoolean("com.lcs.wc.product.PDFProductSpecificationGenerator2.verbose");

	private static final int DEBUG_LEVEL = Integer.parseInt(LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationGenerator2.verboseLevel", "1"));
	// added for Hbi
	private static final boolean USE_RELATIVE_URL = LCSProperties.getBoolean("com.lcs.wc.product.PDFProductSpecificationGenerator2.useRelativeURL");

	public float sideMargins = (new Float(LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationGenerator2.pageMargin", "1.0"))).floatValue();
	//public float bottomMargin = (new Float(LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationGenerator2.pageMargin", "5.0"))).floatValue();
	// added for Hbi
	public float bottomMargin = (new Float(LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationGenerator2.pageMargin", "20.0"))).floatValue();
	//end
	// added for Hbi
	static{
		try{
			java.net.URL authgwURL = GatewayServletHelper.buildAuthenticatedURL(new URLFactory());
			authgwUrl =(USE_RELATIVE_URL)?authgwURL.getPath():authgwURL.toString();

		}
		catch(Exception e){
			System.out.println("Error initializing cache for ExcelGeneratorHelper");
			e.printStackTrace();
		}
	}

	/** Creates a new instance of PDFProductSpecificationGenerator
	 * @param config The specificaiton for which to generate a PDF Specification
	 * @throws WTException
	 */
	public PDFProductSpecificationGenerator2(LCSProduct product) throws WTException{
		this.product = product;
		init();
	}

	public PDFProductSpecificationGenerator2(FlexSpecification spec) throws WTException{
		this.spec = spec;
		this.specName = (String)this.spec.getValue("specName");
		this.specName = FormatHelper.replaceCharacter(this.specName, ":", "_");
		init();
	}

	public PDFProductSpecificationGenerator2(FlexSpecification spec, String propertyFile) throws WTException{
		this.spec = spec;
		this.specName = (String)this.spec.getValue("specName");
		this.specName = FormatHelper.replaceCharacter(this.specName, ":", "_");
		this.propertyFile = propertyFile;
		init();
	}

	public PDFProductSpecificationGenerator2(FlexSpecToSeasonLink fstsl) throws WTException{
		this((FlexSpecification)VersionHelper.latestIterationOf(fstsl.getSpecificationMaster()), fstsl.getSeasonMaster());
	}

	public PDFProductSpecificationGenerator2(FlexSpecification spec, LCSSeasonMaster seasonMaster) throws WTException{
		this.spec = spec;
		this.specName = (String)this.spec.getValue("specName");
		this.specName = FormatHelper.replaceCharacter(this.specName, ":", "_");
		this.seasonMaster = seasonMaster;
		init();
	}

	protected PDFProductSpecificationGenerator2(){

	}

	public static PDFProductSpecificationGenerator2 getOverrideInstance(Object[] arguments) throws WTException{
		PDFProductSpecificationGenerator2 ppsg = null;

		try{
			Class pdfGenClass = PDFProductSpecificationGenerator2.class;
			if(FormatHelper.hasContent(PDF_OVERRIDE_CLASS) && !"com.lcs.wc.product.PDFProductSpecificationGenerator2".equals(PDF_OVERRIDE_CLASS)){
				pdfGenClass = Class.forName(PDF_OVERRIDE_CLASS);
			}

			Class[] argTypes = new Class[arguments.length];

			for(int i = 0; i < arguments.length;i++){
				argTypes[i] = arguments[i].getClass();
			}

			Constructor pdfGenConstructor = pdfGenClass.getConstructor(argTypes);

			ppsg = (PDFProductSpecificationGenerator2) pdfGenConstructor.newInstance(arguments);

		} catch (Exception e) {
			e.printStackTrace();
			if( e instanceof WTException){
				throw (WTException)e;
			}else{
				throw new WTException(e);
			}
		}

		return ppsg;


	}

	private void init() throws WTException{
		debug(1, CLASSNAME +  ".init()");
		try{
			if(FormatHelper.hasContent(this.propertyFile)){
				clu = new ClassLoadUtil(this.propertyFile);
			}
			else{
				clu = new ClassLoadUtil(FileLocation.productSpecProperties2);
			}

			if(this.product == null && this.spec != null){
				this.product = (LCSProduct)VersionHelper.getVersion(this.spec.getSpecOwner(), "A");
			}
			String prodId = FormatHelper.getVersionId(this.product);
			String prodMasterId = FormatHelper.getObjectId((LCSPartMaster)this.product.getMaster());

			productName = (String)this.product.getValue("productName");

			String userName = ClientContext.getContext().getUser().getName();

			// added for Hbi
			fileOutName = "ProductSpec_" + productName + "_" + specName + "_" + userName;// + ".pdf";
			fileOutName = FormatHelper.formatRemoveProblemFileNameChars(fileOutName);
			fileOutName = java.net.URLEncoder.encode(fileOutName, defaultCharsetEncoding);
			zipFileName = fileOutName + ".zip";
			fileOutName = fileOutName + ".pdf";
			params.put(PRODUCT_ID, prodId);
			params.put(PRODUCT_MASTER_ID, prodMasterId);
			params.put(SPEC_ID, FormatHelper.getVersionId(this.spec));
			params.put(PAGE_SIZE, this.ps);


			if(seasonMaster!=null) {
				params.put(SEASONMASTER_ID, FormatHelper.getObjectId(this.seasonMaster));
			}

			techPackStatus = getTechPackStatus(spec);
			ppsphg.headerTextCenter = techPackStatus;

			boolean useHeader = false;
			if((pages != null && pages.contains("HEADER")) || clu.getClass("HEADER") != null){
				useHeader = true;
			}
			Object header = clu.getClass("HEADER");
			if(useHeader && header != null){
				pdfHeader = (PDFHeader)((PDFHeader)header).getPDFHeader(params);
			}
			if(useHeader && pdfHeader != null){
				//float h = pdfHeader.getHeight();
				float h = pdfHeader.getHeight() + hhFudge;
				Float hheight = new Float(h);
				params.put(HEADER_HEIGHT, hheight);
			}
			else{
				params.put(HEADER_HEIGHT, new Float(0));
			}

			setCompHeaderFooterClasses(pages, clu);
		}
		catch(Exception e){
			e.printStackTrace();
			throw new WTException(e);
		}

	}

	/** Generates the PDF document for the Spec
	 *
	 * @throws WTException
	 * @return the url to the generated document
	 */
	public String generateSpec() throws WTException{
		try{

			// added for hbi 
			HBIPDFProductSpecificationCoverPageHeader hbiProdCoverHeader = new HBIPDFProductSpecificationCoverPageHeader();
			//Commented for removing Colorway Placement Table from Tech Pack by UST
			HBIPDFProductSpecColorwayPlacementTable hbiProdSpecColorwayPlsTbl = new HBIPDFProductSpecColorwayPlacementTable();
			HBIApprovedSuppliersTable hbiApprovedSuppliersTbl = new HBIApprovedSuppliersTable();
			HBISizetable sizeTable = new HBISizetable();
			HBISourceRoutingTable hbiSourceRoutingTbl = new HBISourceRoutingTable();
			HBIRevisionTable hbiRevisionTbl = new HBIRevisionTable();
			//Commented for removing Product Revision Attributes Table from Tech Pack by UST
			//HBIRevisionAttributes  hbiRevisionAttrs= new HBIRevisionAttributes();
			// end


			Locale clientLocale = (Locale)this.params.get("clientLocale");
			ppsphg.fontClass = this.fontClass;
			Document doc = null;
			Collection<String> keys = this.pages;
			if(keys == null){
				//keys = clu.getKeyList();
				keys = new ArrayList<String>();
			}

			if(keys.contains("HEADER")){
				keys.remove("HEADER");
			}

			Map<String,Object> tParams = null;
			Iterator<String> i = keys.iterator();
			int x = 0;

			boolean first = true;
			String item = null;
			String key = null;
			String componentId = null;
			HashMap pageOptions = (HashMap)params.get(PAGE_OPTIONS);
			while(i.hasNext()){
				item = (String)i.next();
				if(!FormatHelper.hasContent(item)){
					continue;
				}
				// Cut the key into type, and component id
				key = item.substring(0, item.indexOf(TYPE_COMP_DELIM));
				if (key.equals(BOL)){
					key = BOM;
				}
				componentId = item.substring(item.indexOf(TYPE_COMP_DELIM) +TYPE_COMP_DELIM.length());

				Object obj = clu.getClass(key);
				tParams = new HashMap<String,Object>(params.size()+ 6);
				if(clientLocale!=null)tParams.put("clientLocale",clientLocale);
				tParams.putAll(params);
				//put the component id into the tparams map
				tParams.put(COMPONENT_ID, componentId);
				tParams.put(COMPONENT_PAGE_OPTIONS, pageOptions.get(key));
				componentOwnerCheck(componentId, tParams);

				if(clu.getParams(key) != null){
					tParams.putAll(clu.getParams(key));
				}
				tParams.putAll((Map)compHeaderFooterClasses.get(key));

				/////////////////////////////////////////HBI//////////////////////////////////////////////////////////////
				// Cover Page Spot

				if (first && spec != null && !product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_COLORWAY)){

					doc = prepareDocument((String)tParams.get("orientation"));
					doc.open();
					this.ppsphg.headerTextLeft = "Cover Page";
					Element e = null;
					PdfPTable elementTable = new PdfPTable(1);
					PdfPCell contentCell = null;
					if((!product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_PATTERN))
							&& (!product.getFlexType().getFullName().equals(HBI_SUPPORTING_IMAGE)))
					{
						e = hbiProdCoverHeader.getPDFHeader(tParams);
						elementTable.setWidthPercentage(95);
						contentCell = new PdfPCell(PDFUtils.prepareElement(e));
						contentCell.setBorder(0);
						elementTable.addCell(contentCell);
					}
					//Commented for removing Colorway Placement Table from Tech Pack by UST
					/*PdfPCell spacerCell = new PdfPCell(pgh.multiFontPara("Colorway Placement Table", pgh.getCellFont("FORMLABEL", null, null)));
                        spacerCell.setBorder(0);
						elementTable.addCell(spacerCell);
					 */

					//Condition added by UST for Colorway Tech pack generation
					if(spec != null){
						if((!product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_PATTERN))
								&& (!product.getFlexType().getFullName().equals(HBI_SUPPORTING_IMAGE)))
						{
							e = hbiProdSpecColorwayPlsTbl.getPDFContent(tParams, doc);
							contentCell = new PdfPCell(PDFUtils.prepareElement(e));
							contentCell.setBorder(0);
							elementTable.addCell(contentCell);	
							elementTable.setSplitLate(false);
						}

						PdfPCell spacerCell = null;
						if(!product.getFlexType().getFullName().equals(HBI_SUPPORTING_IMAGE))
							spacerCell = new PdfPCell(pgh.multiFontPara("Sizing Table", pgh.getCellFont("FORMLABEL", null, null)));
						if((!product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_PATTERN))
								&& (!product.getFlexType().getFullName().equals(HBI_SUPPORTING_IMAGE)))
						{
							spacerCell.setBorder(0);
							elementTable.addCell(spacerCell);
							e = sizeTable.getPDFContent(tParams, doc);
							contentCell = new PdfPCell(PDFUtils.prepareElement(e));
							contentCell.setBorder(0);
							elementTable.addCell(contentCell);	
							//elementTable.setSplitLate(false);	
						}

						//doc.add(elementTable);
						System.out.println("product.getFlexType().getFullName()>>>>>>>>>>>>>>>>>"+product.getFlexType().getFullName());
						if(product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_GARMENT))
						{
							System.out.println("inside Approved SUppliers");
							spacerCell = new PdfPCell(pgh.multiFontPara("Approved Suppliers", pgh.getCellFont("FORMLABEL", null, null)));
							spacerCell.setBorder(0);
							elementTable.addCell(spacerCell);

							e = hbiApprovedSuppliersTbl.getPatternPDFContent(tParams, doc);
							contentCell = new PdfPCell(PDFUtils.prepareElement(e));
							contentCell.setBorder(0);
							elementTable.addCell(contentCell);	
							elementTable.setSplitLate(false);
						}
						doc.add(elementTable);

						if(!product.getFlexType().getFullName().equals(HBI_SUPPORTING_IMAGE))
						{
							setOrientation((String)tParams.get("orientation"), doc);	
							doc.newPage();
							this.ppsphg.headerTextLeft = "Cover Page";
							e = hbiProdCoverHeader.getPDFHeader(tParams);
							elementTable = new PdfPTable(1);
							elementTable.setWidthPercentage(95);

							contentCell = new PdfPCell(PDFUtils.prepareElement(e));
							contentCell.setBorder(0);
							elementTable.addCell(contentCell);

							// START - Comment by UST to remove Product Revision Attributes 12/31/2012
							/*spacerCell = new PdfPCell(pgh.multiFontPara("Product Revision Attributes", pgh.getCellFont("FORMLABEL", null, null)));
                        spacerCell.setBorder(0);
						elementTable.addCell(spacerCell);

						e = hbiRevisionAttrs.getPDFContent(tParams, doc);
						contentCell = new PdfPCell(PDFUtils.prepareElement(e));
						contentCell.setBorder(0);
						elementTable.addCell(contentCell);	
					    elementTable.setSplitLate(false);*/
							//END - Comment

							spacerCell = new PdfPCell(pgh.multiFontPara("Product Revision", pgh.getCellFont("FORMLABEL", null, null)));
							spacerCell.setBorder(0);
							elementTable.addCell(spacerCell);

							e = hbiRevisionTbl.getPDFContent(tParams, doc);
							contentCell = new PdfPCell(PDFUtils.prepareElement(e));
							contentCell.setBorder(0);
							elementTable.addCell(contentCell);	
							elementTable.setSplitLate(false);
						}
						/*						if(product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_GARMENT))
						{
							doc.add(elementTable);
						}*/
						if((!product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_PATTERN))
								&& (!product.getFlexType().getFullName().equals(HBI_SUPPORTING_IMAGE)))
						{
							doc.add(elementTable);

							setOrientation((String)tParams.get("orientation"), doc);	
							doc.newPage();
							this.ppsphg.headerTextLeft = "Cover Page";
							e = hbiProdCoverHeader.getPDFHeader(tParams);
							elementTable = new PdfPTable(1);
							elementTable.setWidthPercentage(95);
							contentCell = new PdfPCell(PDFUtils.prepareElement(e));
							contentCell.setBorder(0);
							elementTable.addCell(contentCell);

							spacerCell = new PdfPCell(pgh.multiFontPara("Manufacturing Routing", pgh.getCellFont("FORMLABEL", null, null)));
							spacerCell.setBorder(0);
							elementTable.addCell(spacerCell);

							e = hbiSourceRoutingTbl.getPDFContent(tParams, doc);
							contentCell = new PdfPCell(PDFUtils.prepareElement(e));
							contentCell.setBorder(0);
							elementTable.addCell(contentCell);	
							elementTable.setSplitLate(false);
						}



						/*PdfPCell spacerCell2 = new PdfPCell(pgh.multiFontPara(" "));
						spacerCell2.setMinimumHeight(3.0f);
                        spacerCell2.setBorder(0);
						elementTable.addCell(spacerCell2);*/
					}

					doc.add(elementTable);
					first=false;
				} 


				///////////////////////////////////////////////HBI END/////////////////////////////////////////////////////////////////////

				if(obj instanceof PDFContent){
					PDFContent content = (PDFContent)obj;
					String cHeader = ((SpecPage)obj).getPageHeaderString();
					ppsphg.headerTextLeft = cHeader;
					if(first){
						doc = prepareDocument((String)tParams.get("orientation"));
						doc.open();
						first = false;
					}
					else{
						setOrientation((String)tParams.get("orientation"), doc);
						doc.newPage();
					}

					Element e = content.getPDFContent(tParams, doc);

					x++;
					//Need to insure a "single" page...going to try adding to a single
					//table and add the single table to the document...not sure about the
					//impact to all content, image pages for example could get distorted
					PdfPTable elementTable = new PdfPTable(1);

					if(pdfHeader != null){
						//doc.add(pdfHeader);
						//doc.add(pgh.multiFontPara(" "));
						PdfPCell titleCell = new PdfPCell(PDFUtils.prepareElement(pdfHeader));
						titleCell.setBorder(0);
						elementTable.addCell(titleCell);


						PdfPCell spacerCell = new PdfPCell(pgh.multiFontPara(" "));
						spacerCell.setBorder(0);
						elementTable.addCell(spacerCell);

					}
					//doc.add(e);
					PdfPCell contentCell = new PdfPCell(PDFUtils.prepareElement(e));
					contentCell.setBorder(0);
					elementTable.addCell(contentCell);

					doc.add(elementTable);

				}
				else if(obj instanceof PDFContentCollection){ // To Do

					PDFContentCollection content = (PDFContentCollection)obj;
					Collection contents = new ArrayList();
					boolean cFirst = true;
					if(first){
						doc = prepareDocument((String)tParams.get("orientation"));
						doc.open();
						first = false;
					}
					else{
						setOrientation((String)tParams.get("orientation"), doc);
						doc.newPage();
					}
					try{

						if (content instanceof PDFImagePagesCollection) {
							((PDFImagePagesCollection)content).setPdfWriter(writer);
						}
						else if (content instanceof PDFImagePagesCollection2) {
							((PDFImagePagesCollection2)content).setPdfWriter(writer);
						}
						//if(!product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_COLORWAY))
							contents = content.getPDFContentCollection(tParams, doc);

					}
					catch(Exception e){
						//if(!product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_COLORWAY))
						e.printStackTrace();
					}

					Vector contentHeaders = new Vector(((SpecPageSet)obj).getPageHeaderCollection());

					Iterator ci = contents.iterator();
					int pCount = 0;
					while(ci.hasNext()){
						if(!cFirst){
							doc.newPage();
						}
						else{
							cFirst = false;
						}
						//This needs to be after the doc.newPage(), because the header is written in onEndPage()

						String cHeader = (String)contentHeaders.elementAt(pCount);

						ppsphg.headerTextLeft = cHeader;
						pCount++;
						//--
						Element e = (Element)ci.next();
						PdfPTable elementTable = new PdfPTable(1);
						elementTable.setWidthPercentage(95);

						if(pdfHeader != null){
							//doc.add(pdfHeader);
							//doc.add(pgh.multiFontPara(" "));
							PdfPCell titleCell = new PdfPCell(PDFUtils.prepareElement(pdfHeader));
							titleCell.setBorder(0);
							elementTable.addCell(titleCell);

							PdfPCell spacerCell = new PdfPCell(pgh.multiFontPara(" "));
							spacerCell.setBorder(0);
							elementTable.addCell(spacerCell);
						}
						PdfPCell contentCell = new PdfPCell(PDFUtils.prepareElement(e));
						contentCell.setBorder(0);
						elementTable.addCell(contentCell);
						elementTable.setSplitLate(false);
						doc.add(elementTable);
						x++;
					}
				}
			}

			Collection<String> nonComponentReports = (Collection<String>)params.get(NON_COMPONENT_REPORTS);//clu.getAssignableFromKeyList(SpecPageNonComponentReport.class);
			if(!nonComponentReports.isEmpty()){
				for(String reportKey: nonComponentReports){
					doc = generateReport(doc, reportKey);
					x++;
				}
			}

			//for FlexEPMDocToSpecLink Variations
			if(pageOptions.containsKey("CAD Document Variations")){
				tParams = new HashMap<String,Object>(params);
				key = "CADDOC_VARIATIONS";
				Collection contents = new ArrayList();
				if(doc == null){
					doc = prepareDocument((String)tParams.get("orientation"));
					doc.open();

				}

				Object obj = clu.getClass(key);
				PDFContentCollection content = (PDFContentCollection)obj;
				contents.addAll(content.getPDFContentCollection(tParams, doc));

				if(contents != null && !contents.isEmpty() && contents.size()>0){
					Vector contentHeaders = new Vector(((SpecPageSet)obj).getPageHeaderCollection());

					Iterator<?> ci = contents.iterator();
					int pCount = 0;
					while(ci.hasNext()){
						doc.newPage();

						String cHeader = (String)contentHeaders.elementAt(pCount);
						debug("cHeader:" + cHeader);
						ppsphg.headerTextLeft = cHeader;


						PdfPTable elementTable = new PdfPTable(1);
						Element e = (Element)ci.next();
						elementTable.setWidthPercentage(95);

						if(pdfHeader != null){

							PdfPCell titleCell = new PdfPCell(PDFUtils.prepareElement(pdfHeader));
							titleCell.setBorder(0);
							elementTable.addCell(titleCell);


							PdfPCell spacerCell = new PdfPCell(pgh.multiFontPara(" "));
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

			//For Change Tracking report
			if(pageOptions.containsKey("Tracked Changes") && null != tParams){
				key = "CHANGE_TRACKING";
				Collection contents = new ArrayList();
				List trackedChangesList = new ArrayList();
				trackedChangesList = (ArrayList) pageOptions.get("Tracked Changes");
				Object obj = clu.getClass(key);
				String condensed = "CONDENSED";

				WTObject specObj =  (WTObject)LCSProductQuery.findObjectById((String)params.get(SPEC_ID));
				if(specObj == null || !(specObj instanceof FlexSpecification)){
					throw new WTException("Can not use PDFProductSpecificationFitSpec on without a FlexSpecification - " + specObj);
				}

				FlexSpecification spec = (FlexSpecification)specObj;
				((Collection)params.get(SPEC_PAGE_OPTIONS)).add(FormatHelper.getVersionId(spec));

				if(trackedChangesList.indexOf(ChangeTrackingPDFGenerator.EXPANDED_REPORT) > -1){
					tParams.put(condensed, new Boolean(false));
					PDFContentCollection content = (PDFContentCollection)obj;
					contents.addAll(content.getPDFContentCollection(tParams, doc));
				}

				if(trackedChangesList.indexOf(ChangeTrackingPDFGenerator.CONDENSED_REPORT) > -1){
					tParams.put(condensed, new Boolean(true));
					PDFContentCollection content = (PDFContentCollection)obj;
					contents.addAll(content.getPDFContentCollection(tParams, doc));
				}

				if(contents != null && !contents.isEmpty() && contents.size()>0){
					Vector contentHeaders = new Vector(((SpecPageSet)obj).getPageHeaderCollection());

					Iterator<?> ci = contents.iterator();
					int pCount = 0;
					while(ci.hasNext()){
						doc.newPage();

						String cHeader = (String)contentHeaders.elementAt(pCount);
						debug("cHeader:" + cHeader);
						ppsphg.headerTextLeft = cHeader;

						PdfPTable elementTable = new PdfPTable(1);
						Element e = (Element)ci.next();
						elementTable.setWidthPercentage(95);

						if(pdfHeader != null){

							PdfPCell titleCell = new PdfPCell(PDFUtils.prepareElement(pdfHeader));
							titleCell.setBorder(0);
							elementTable.addCell(titleCell);


							PdfPCell spacerCell = new PdfPCell(pgh.multiFontPara(" "));
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

			if (x < 1) {
				if(doc == null || !(doc.isOpen())){
					doc = prepareDocument((String)params.get("orientation"));
					doc.open();
				}

				Paragraph p = pgh.multiFontPara(WTMessage.getLocalizedMessage( RB.PRODUCT, "noPagesGeneratedForSpec_MSG", RB.objA ) );
				if (p.size() < 1) {
					String noPagesGeneratedForSpec_MSG_EN = WTMessage.getLocalizedMessage( RB.PRODUCT, "noPagesGeneratedForSpec_MSG", RB.objA, Locale.ENGLISH );
					Object[] objB = {SessionHelper.getLocale(), noPagesGeneratedForSpec_MSG_EN};
					String emptyMsg = wt.util.WTMessage.getLocalizedMessage ( RB.PRODUCT, "couldNotFindFont_MSG", objB, Locale.ENGLISH );
					p = pgh.multiFontPara(emptyMsg );
				}
				doc.add(p);
			}
			doc.close();

			return outputFile;
		}
		catch(Exception e){
			e.printStackTrace();
			throw new WTException(e);
		}
	}

	protected Document generateReport(Document doc, String key) throws  WTException {
		try{
			Object obj = clu.getClass(key);
			Map<String, Object> tParams = new HashMap<String,Object>(params);
			Collection contents = new ArrayList();
			if(doc == null){
				doc = prepareDocument((String)tParams.get("orientation"));
				doc.open();
			}else{
				setOrientation((String)tParams.get("orientation"), doc);
			}

			if(obj instanceof PDFContent){
				PDFContent content = (PDFContent)obj;
				String cHeader = ((SpecPage)obj).getPageHeaderString();
				ppsphg.headerTextLeft = cHeader;
				Element e = content.getPDFContent(tParams, doc);
				PdfPTable elementTable = new PdfPTable(1);

				if(pdfHeader != null){
					//doc.add(pdfHeader);
					//doc.add(pgh.multiFontPara(" "));
					PdfPCell titleCell = new PdfPCell(PDFUtils.prepareElement(pdfHeader));
					titleCell.setBorder(0);
					elementTable.addCell(titleCell);


					PdfPCell spacerCell = new PdfPCell(pgh.multiFontPara(" "));
					spacerCell.setBorder(0);
					elementTable.addCell(spacerCell);

				}
				//doc.add(e);
				PdfPCell contentCell = new PdfPCell(PDFUtils.prepareElement(e));
				contentCell.setBorder(0);
				elementTable.addCell(contentCell);

				doc.add(elementTable);

			}else if(obj instanceof PDFContentCollection){
				PDFContentCollection content = (PDFContentCollection)obj;
				contents.addAll(content.getPDFContentCollection(tParams, doc));

				if(contents != null && !contents.isEmpty() && contents.size()>0){
					Vector contentHeaders = new Vector(((SpecPageSet)obj).getPageHeaderCollection());

					Iterator<?> ci = contents.iterator();
					int pCount = 0;
					while(ci.hasNext()){
						doc.newPage();

						String cHeader = (String)contentHeaders.elementAt(pCount);
						debug("cHeader:" + cHeader);
						ppsphg.headerTextLeft = cHeader;

						PdfPTable elementTable = new PdfPTable(1);
						Element e = (Element)ci.next();
						elementTable.setWidthPercentage(95);

						if(pdfHeader != null){

							PdfPCell titleCell = new PdfPCell(PDFUtils.prepareElement(pdfHeader));
							titleCell.setBorder(0);
							elementTable.addCell(titleCell);

							PdfPCell spacerCell = new PdfPCell(pgh.multiFontPara(" "));
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


		}
		catch(Exception e){
			e.printStackTrace();
			throw new WTException(e);
		}
		return doc;

	}


	// added for Hbi
	public String getZipName()
	{
		return zipFileName;
	}

	private Document prepareDocument(String orientation) throws WTException{
		try{
			String outDirStr = null;

			if(FormatHelper.hasContent(this.outputLocation)){
				outDirStr = this.outputLocation;
			} else {
				outDirStr = FileLocation.PDFDownloadLocationFiles;
			}

			File outFile = new File(outDirStr, fileOutName);

			outFile = FileRenamer.rename(outFile);
			fileOutName = outFile.getName();
			outputFile = outFile.getAbsolutePath();

			FileOutputStream outStream = new FileOutputStream(outFile);

			Document pdfDoc = new Document();
			pdfDoc.setMargins(sideMargins, sideMargins, cellHeight, bottomMargin);

			writer = PdfWriter.getInstance(pdfDoc, outStream);

			writer.setPageEvent(ppsphg);
			setOrientation(orientation, pdfDoc);
			// added for Hbi
			fileOutName = java.net.URLEncoder.encode(fileOutName, defaultCharsetEncoding);

					if(FormatHelper.hasContent(outputURL)){
						returnURL =  outputURL + fileOutName;
					}
					else{
						// has trailing file.separator
						String filepath = FormatHelper.formatOSFolderLocation(FileLocation.PDFDownloadLocationFiles);

						// Set the URL for this generated temp file
						returnURL = filepath + fileOutName;

					}

					return pdfDoc;
		}
		catch(Exception e){
			e.printStackTrace();
			throw new WTException(e);
		}
	}

	public String getURL()
	{
		return returnURL;
	}

	private void setOrientation(String orientation, Document doc){
		debug("orientation: " + orientation);

		debug("ps: " + this.ps);
		if(FormatHelper.hasContent(orientation)){
			if(LANDSCAPE.equalsIgnoreCase(orientation)){
				doc.setPageSize(this.ps.rotate());
			}
			else{
				doc.setPageSize(this.ps);
			}
		}
		else{
			if(this.landscape){
				doc.setPageSize(this.ps.rotate());
			}
			else{
				doc.setPageSize(this.ps);
			}
		}

		debug("page size for render: " + doc.getPageSize());
	}


	/** sets the page size for teh generated document
	 *
	 * NOTE: Use com.lcs.wc.client.web.pdf.PDFPageSize to get the correct
	 * page sizes
	 * @param pageSize
	 */
	public void setPageSize(Rectangle pageSize){
		this.ps = pageSize;
	}

	/** sets whether or not the generated document should use landscape orientation
	 * @param landscape
	 */
	public void setLandscape(boolean landscape){
		this.landscape = landscape;
	}

	/** Gets the status of the given SourcingConfig.  This is used for generating the
	 * page title
	 *
	 * NOTE: Assumes Sourcing Config type has techPackStatus as an attribute
	 * If it is not there then Developement is returned
	 *
	 * @param sourcingConfig
	 * @throws WTException
	 * @return
	 */
	public String getTechPackStatus(FlexSpecification spec) throws WTException{
		String tps = "";
		if(spec != null){
			tps = spec.getLifeCycleState().getDisplay(ClientContext.getContextLocale());
		}
		return tps;
	}

	private PdfPTable createHeaderTable(String pageTitle, String status) throws WTException{
		try{
			Font font = pgh.getCellFont(fontClass, null, "8");

			// write the headertable
			PdfPTable table = new PdfPTable(3);
			table.setWidthPercentage(95.0f);

			PdfPCell left = new PdfPCell(pgh.multiFontPara(pageTitle, font));
			left.setHorizontalAlignment(Element.ALIGN_LEFT);
			left.setFixedHeight(cellHeight);
			left.setBorder(0);
			table.addCell(left);

			PdfPCell center = new PdfPCell(pgh.multiFontPara(status, font));
			center.setHorizontalAlignment(Element.ALIGN_CENTER);
			center.setFixedHeight(cellHeight);
			center.setBorder(0);
			table.addCell(center);

			PdfPCell right = new PdfPCell(pgh.multiFontPara(" ", font));
			right.setHorizontalAlignment(Element.ALIGN_LEFT);
			right.setFixedHeight(cellHeight);
			right.setBorder(0);
			table.addCell(right);


			return table;
		}
		catch(Exception e){
			throw new WTException(e);
		}

	}

	/** Gets the path to the PDF file once it is generated
	 * @return the path to the PDF file
	 */
	public String getFilePath(){
		return this.outputFile;
	}

	/** Sets which pages to should be included in the Spec
	 * The list should be a subset of the pages listed in the ProductSpecification.properties
	 * under codebase.
	 *
	 * If no pages are specified, then all pages will be included
	 * @param pages
	 */
	public void setPages(Collection pages){
		this.pages = pages;
	}

	/** Returns what pages are included in the spec generation
	 * @return
	 */
	public Collection getPages(){
		if(this.pages != null){
			return this.pages;
		}

		return clu.getKeyList();
	}

	/** Set Which type of Pages for BOM or other components to print
	 * @param pageOptions
	 */
	public void setPageOptions(String pageOptions) {
		debug("setPageOptions(String pageOptions)" + pageOptions);
		HashMap<String,Object> pageOptionsMap = new HashMap<String,Object>();
		Collection<String> componentColl = new ArrayList<String>();
		Collection<String> nonComponentReports = new Vector<String>();

		if(FormatHelper.hasContent(pageOptions)) {
			StringTokenizer token = new MultiCharDelimStringTokenizer(pageOptions, MOAHelper.DELIM);
			String item = null;
			String component = null;
			String pageOption = null;
			Object cObj = null;
			while(token.hasMoreTokens()) {
				item = token.nextToken();
				debug(2, "item :  " + item);
				if(!FormatHelper.hasContent(item)) {
					continue;
				}
				component = item.substring(0, item.indexOf(":"));
				pageOption = item.substring(item.indexOf(":") +1).trim();
				if(pageOptionsMap.containsKey(component)) {
					componentColl = (Collection)pageOptionsMap.get(component);
					componentColl.add(pageOption);
				} else {
					componentColl = new ArrayList<String>();
					componentColl.add(pageOption);
					pageOptionsMap.put(component, componentColl);
				}
				cObj = clu.getClass(pageOption);
				if(cObj instanceof SpecPageNonComponentReport){
					nonComponentReports.add(pageOption);
				}
			}
		}

		params.put(PAGE_OPTIONS, pageOptionsMap);
		params.put(NON_COMPONENT_REPORTS, nonComponentReports);

	}

	/** Set Which components of Specification to print
	 * @param specPageOptions
	 */
	public void setSpecPageOptions(String specPageOptions){
		Collection<String> pageOptionsMap = new ArrayList<String>();
		if(FormatHelper.hasContent(specPageOptions)){
			StringTokenizer token = new MultiCharDelimStringTokenizer(specPageOptions, MOAHelper.DELIM);
			String item = null;
			String component=null;
			while(token.hasMoreTokens()){
				item = token.nextToken();
				if(!FormatHelper.hasContent(item)){
					continue;
				}
				component = item.substring(item.indexOf("-:-")+3).trim();
				pageOptionsMap.add(component);
			}
		}
		params.put(SPEC_PAGE_OPTIONS, pageOptionsMap);
	}

	/** Set Which components of Specification to print
	 * @param specPageOptions
	 */
	public void setSpecPageOptions(Collection specPageOptions){
		Collection<String> pageOptionsMap = new ArrayList<String>();
		String item = null;
		String component=null;
		Iterator specPageOptionsIterator = specPageOptions.iterator();
		while(specPageOptionsIterator.hasNext()){
			item = (String)specPageOptionsIterator.next();
			if(!FormatHelper.hasContent(item)){
				continue;
			}
			component = item.substring(item.indexOf("-:-")+3).trim();
			pageOptionsMap.add(component);
		}
		params.put(SPEC_PAGE_OPTIONS, pageOptionsMap);
	}

	/** Set Hash map of which view for which BOM section
	 * @param BOMSectionViews
	 */
	public void setBOMSectionViews(String BOMSectionViews) {
		HashMap<String,String> bomSectionViewsMap = new HashMap<String,String>(8);
		if(FormatHelper.hasContent(BOMSectionViews)){
			String key = null;
			String bomOption = null;
			String bomFlexType = null;
			String section = null;
			String viewId= null;
			String row = null;
			StringTokenizer token = new MultiCharDelimStringTokenizer(BOMSectionViews, 
					MOAHelper.DELIM);

			while(token.hasMoreTokens()) {
				row = token.nextToken();
				if(!FormatHelper.hasContent(row)) {
					continue;
				}
				debug(2, "row :  " + row);
				String[] rowArray = row.split(TYPE_COMP_DELIM);
				bomOption = rowArray[0];
				bomFlexType = rowArray[1];
				section = rowArray[2];
				viewId = rowArray[3];
				if(!viewId.startsWith("OR:com.lcs.wc.report")){
					viewId = "OR:com.lcs.wc.report." + viewId;
				}
				key = bomOption +bomFlexType+section;
				bomSectionViewsMap.put(key, viewId);
			}
		}
		params.put(BOM_SECTION_VIEWS, bomSectionViewsMap);
	}

	void setParams(String moa, String key)
	{
		Collection<String> vec = null;
		if(FormatHelper.hasContent(moa)){
			vec = MOAHelper.getMOACollection(moa);
		} else {
			vec = new ArrayList<String>();
		}

		params.put(key, vec);
	}

	/** Set which EPMDocuments to package
	 * @param availEPMDOCDocs
	 */
	public void setAvailEPMDOCDocs(String availEPMDOCDocs) {
		setParams(availEPMDOCDocs, SPEC_CAD_DOCS);
	}

	/** Set which Windchill filter to use
	 * @param cadDocFilter
	 */
	public void setCadDocFilter(String cadDocFilter) {
		setParams(cadDocFilter, SPEC_CAD_FILTER);
	}

	/** Set which Parts to package
	 * @param availParts
	 */
	public void setAvailParts(String availParts) {
		setParams(availParts, SPEC_PARTS);
	}

	/** Set which Part Windchill filter to use
	 * @param partFilter
	 */
	public void setPartFilter(String partFilter) {
		params.put(SPEC_PART_FILTER, partFilter);
	}

	/** Set which Part Windchill filter to use
	 * @param showIndentedBOM
	 */
	public void setShowIndentedBOM(String showIndentedBOM) {
		params.put(SHOW_INDENTED_BOM, showIndentedBOM);
	}

	/** Set which colorways to display in the bom
	 * @param colorways
	 */
	public void setColorways(String colorways) {
		setParams(colorways, BomDataGenerator.COLORWAYS);
	}

	/** Set the sources
	 * @param sources
	 */
	public void setSources(String sources) {
		setParams(sources, BomDataGenerator.SOURCES);
	}

	/** Set the destinations
	 * @param destinations
	 */
	public void setDestinations(String destinations) {
		setParams(destinations, BomDataGenerator.DESTINATIONS);
	}

	/** Set the number of colorways to display on the page
	 * @param colorwaysPerPage
	 */
	public void setColorwaysPerPage(String colorwaysPerPage) {
		Integer intI = Integer.valueOf("0");

		if(FormatHelper.hasContent(colorwaysPerPage)){
			try {
				intI = Integer.valueOf(colorwaysPerPage);
			} catch(NumberFormatException nfe) {
				//If we get an exception just use 0, aka all sizes
				intI = Integer.valueOf("0");
			}
		}
		params.put(COLORWAYS_PER_PAGE, intI);
	}

	/** Set the number of colorways to display on the page
	 * @param colorwaysPerPage
	 */
	public void setSizesPerPage(String sizesPerPage) {
		Integer intI = null;

		if(FormatHelper.hasContent(sizesPerPage)){
			try {
				intI = Integer.valueOf(sizesPerPage);
			} catch (NumberFormatException nfe) {
				//If we get an exception just use 0, aka all sizes
				intI = Integer.valueOf("0");
			}
		} else {
			intI = Integer.valueOf("0");
		}
		params.put(SIZES_PER_PAGE, intI);
	}

	/** Set the size 1 values
	 * @param Size1Sizes
	 */
	public void setSize1Sizes(String size1Sizes) {
		setParams(size1Sizes, SIZES1);
	}

	/** Set the size 2 values
	 * @param Size2Sizes New value of property Size2Sizes.
	 */
	public void setSize2Sizes(String size2Sizes) {
		setParams(size2Sizes, SIZES2);
	}

	/**Set the Product Size Category Id
	 */
	public void setProductSizeCatId(String sizeCatId) {
		params.put(PRODUCT_SIZE_CAT_ID, sizeCatId);
	}

	/**Set the show color swatch boolean
	 */
	public void setShowColorSwatch(String showColorSwatch) {
		params.put(BomDataGenerator.USE_COLOR_SWATCH, showColorSwatch);
	}

	/**Set show Material Thumbnail boolean
	 */
	public void setShowMatThumbnail(String showMatThumbnail) {
		params.put(BomDataGenerator.USE_MAT_THUMBNAIL, showMatThumbnail);
	}

	public void setShowChangeSince(String showChangeSince){
		params.put(SHOW_CHANGE_SINCE, showChangeSince);
	}

	/**Set the use size1/size2 value
	 * @param useSize1Size2 a String with a value of (size1|size2)
	 */
	public void setUseSize1Size2(String useSize1Size2) {
		params.put(BomDataGenerator.USE_SIZE1_SIZE2, useSize1Size2);
		params.put(BomDataGenerator.SIZE_ATT, useSize1Size2);
	}

	/**This method is to add key/value pairs, which are passed on to the actual generators.
	 */
	public void setAddlParams(Map addParams){
		params.putAll(addParams);
	}

	private void setCompHeaderFooterClasses(Collection pages, ClassLoadUtil clu) {
		debug(1, "setCompHeaderFooterClasses(Collection pages, ClassLoadUtil clu)");
		//debug(2, "pages:  " + pages + "\n, clu:  " + clu.getKeyList());

		HashMap compMap = new HashMap(2);
		//BOM
		if((pages != null && pages.contains("BOMHeader")) || clu.getClass("BOMHeader") != null){
			compMap.put(PDFProductSpecificationBOM2.BOM_HEADER_CLASS,clu.getClass("BOMHeader"));
			if(pages != null && pages.contains("BOMHeader")){
				pages.remove("BOMHeader");
			}
		}
		if((pages != null && pages.contains("BOMFooter")) || clu.getClass("BOMFooter") != null){
			compMap.put(PDFProductSpecificationBOM2.BOM_FOOTER_CLASS,clu.getClass("BOMFooter"));
			if(pages != null && pages.contains("BOMFooter")){
				pages.remove("BOMFooter");
			}
		}
		compHeaderFooterClasses.put(BOM, compMap);
		compMap = new HashMap(2);
		/////////////////////
		//BOL
		/////////////////////
		if((pages != null && pages.contains("BOLHeader")) || clu.getClass("BOLHeader") != null){
			compMap.put(PDFProductSpecificationBOM2.BOL_HEADER_CLASS,clu.getClass("BOLHeader"));
			if(pages != null && pages.contains("BOLHeader")){
				pages.remove("BOLHeader");
			}
		}
		if((pages != null && pages.contains("BOLFooter")) || clu.getClass("BOLFooter") != null){
			compMap.put(PDFProductSpecificationBOM2.BOL_FOOTER_CLASS,clu.getClass("BOLFooter"));
			if(pages != null && pages.contains("BOLFooter")){
				pages.remove("BOLFooter");
			}
		}
		compHeaderFooterClasses.put(BOL, compMap);
		compMap = new HashMap(2);
		/////////////////////
		//Construction
		/////////////////////
		if((pages != null && pages.contains("ConstructionHeader")) || clu.getClass("ConstructionHeader") != null){
			compMap.put(PDFProductSpecificationConstruction2.CONSTRUCTION_HEADER_CLASS,clu.getClass("ConstructionHeader"));
			if(pages != null && pages.contains("ConstructionHeader")){
				pages.remove("ConstructionHeader");
			}
		}
		if((pages != null && pages.contains("ConstructionFooter")) || clu.getClass("ConstructionFooter") != null){
			compMap.put(PDFProductSpecificationConstruction2.CONSTRUCTION_FOOTER_CLASS,clu.getClass("ConstructionFooter"));
			if(pages != null && pages.contains("ConstructionFooter")){
				pages.remove("ConstructionFooter");
			}
		}
		compHeaderFooterClasses.put(CONSTRUCTION, compMap);
		compMap = new HashMap(2);
		/////////////////////
		//ImagePages
		/////////////////////

		if((pages != null && pages.contains("ImagePagesHeader")) || clu.getClass("ImagePagesHeader") != null){
			compMap.put(PDFImagePagesCollection2.IMAGE_PAGES_HEADER_CLASS,clu.getClass("ImagePagesHeader"));
			if(pages != null && pages.contains("ImagePagesHeader")){
				pages.remove("ImagePagesHeader");
			}
		}
		if((pages != null && pages.contains("ImagePagesFooter")) || clu.getClass("ImagePagesFooter") != null){
			compMap.put(PDFImagePagesCollection2.IMAGE_PAGES_FOOTER_CLASS,clu.getClass("ImagePagesFooter"));
			if(pages != null && pages.contains("ImagePagesFooter")){
				pages.remove("ImagePagesFooter");
			}
		}
		compHeaderFooterClasses.put(IMAGE_PAGES, compMap);
		compMap = new HashMap(2);
		/////////////////////
		//Measurements
		/////////////////////
		if((pages != null && pages.contains("MeasurementsHeader")) || clu.getClass("MeasurementsHeader") != null){
			compMap.put(PDFProductSpecificationMeasurements2.MEASUREMENTS_HEADER_CLASS,clu.getClass("MeasurementsHeader"));
			if(pages != null && pages.contains("MeasurementsHeader")){
				pages.remove("MeasurementsHeader");
			}
		}
		if((pages != null && pages.contains("MeasurementsFooter")) || clu.getClass("MeasurementsFooter") != null){
			compMap.put(PDFProductSpecificationMeasurements2.MEASUREMENTS_FOOTER_CLASS,clu.getClass("MeasurementsFooter"));
			if(pages != null && pages.contains("MeasurementsFooter")){
				pages.remove("MeasurementsFooter");
			}
		}
		compHeaderFooterClasses.put(MEASUREMENTS, compMap);

		/////////////////////
		//SpecCadDoc Variations
		/////////////////////
		compMap = new HashMap(2);
		if((pages != null && pages.contains("SpecCadDocVariationHeader")) || clu.getClass("SpecCadDocVariationHeader") != null){
			compMap.put(PDFCadDocVariationsGenerator.SPECCADDOC_VARIATION_HEADER_CLASS,clu.getClass("SpecCadDocVariationHeader"));
			if(pages != null && pages.contains("SpecCadDocVariationHeader")){
				pages.remove("SpecPartVariationHeader");
			}
		}
		if((pages != null && pages.contains("SpecCadDocVariationFooter")) || clu.getClass("SpecCadDocVariationFooter") != null){
			compMap.put(PDFCadDocVariationsGenerator.SPECCADDOC_VARIATION_FOOTER_CLASS,clu.getClass("SpecCadDocVariationFooter"));
			if(pages != null && pages.contains("SpecCadDocVariationFooter")){
				pages.remove("SpecPartVariationFooter");
			}
		}

		/////////////////////
		//SpecPart Variations
		/////////////////////
		compMap = new HashMap(2);

		if((pages != null && pages.contains("SpecPartVariationHeader")) || clu.getClass("SpecPartVariationHeader") != null){
			compMap.put(PDFCadDocVariationsGenerator.SPECCADDOC_VARIATION_HEADER_CLASS,clu.getClass("SpecPartVariationHeader"));
			if(pages != null && pages.contains("SpecPartVariationHeader")){
				pages.remove("SpecPartVariationHeader");
			}
		}
		if((pages != null && pages.contains("SpecPartVariationFooter")) || clu.getClass("SpecPartVariationFooter") != null){
			compMap.put(PDFCadDocVariationsGenerator.SPECCADDOC_VARIATION_FOOTER_CLASS,clu.getClass("SpecPartVariationFooter"));
			if(pages != null && pages.contains("SpecPartVariationFooter")){
				pages.remove("SpecPartVariationFooter");
			}
		}
		compHeaderFooterClasses.put(MEASUREMENTS, compMap);
		debug(1, "--end of setCompHeaderFooterClasses()- compHeaderFooterClasses.keySet():  " + compHeaderFooterClasses.keySet());
	}


	/**
	 * DO A CHECK TO SEE IF THE BOM IS OWNED BY THE SPEC'S PRODUCT
	 * IF IT IS NOT THEN NEED TO OVERWRITE THE DIMENSIONS WITH THE DIMENSIONS FROM
	 * THE OWNER BOM
	 *
	 * THIS WILL ALWAYS BE A MAPPING OF ALL DIMENSIONS, IT DOES NOT TRY TO DRAW 
	 * ANY CORRELATION BETWEEN THE SPEC'S PRODUCT'S DIMENSIONS AND THE ACTUAL OWNER
	 * DIMENSIONS
	 **/
	private static void componentOwnerCheck(String compId, Map tparams) throws WTException{
		WTObject component = (WTObject)LCSQuery.findObjectById(compId);
		LCSPartMaster compOwnerMaster = null;

		if(component instanceof FlexBOMPart){
			compOwnerMaster = (LCSPartMaster)((FlexBOMPart)component).getOwnerMaster();
		}
		else if(component instanceof LCSDocument){
			String ownerId = (String)((LCSDocument)component).getValue("ownerReference");
			compOwnerMaster = (LCSPartMaster)LCSQuery.findObjectById(ownerId);
		}
		else if(component instanceof LCSMeasurements){
			compOwnerMaster = ((LCSMeasurements)component).getProductMaster();
		}
		else if(component instanceof LCSConstructionInfo){
			compOwnerMaster = ((LCSConstructionInfo)component).getProductMaster();
		}

		String specOwnerMasterId = (String)tparams.get(PDFProductSpecificationGenerator2.PRODUCT_MASTER_ID);
		String compOwnerMasterId = FormatHelper.getObjectId(compOwnerMaster);
		boolean includeBOMVariation = FormatHelper.parseBoolean((String)tparams.get(PDFProductSpecificationGenerator2.INCLUDE_BOM_OWNER_VARIATIONS));
		boolean includeMeasurementsOwnerSizes = FormatHelper.parseBoolean((String)tparams.get(PDFProductSpecificationGenerator2.INCLUDE_MEASUREMENTS_OWNER_SIZES));

		if(!(specOwnerMasterId.equals(compOwnerMasterId))){
			//Different products
			LCSProduct product = com.lcs.wc.season.SeasonProductLocator.getProductARev(compOwnerMaster);

			PDFMultiSpecGenerator pmsg = new PDFMultiSpecGenerator();

			String tsizes1 = pmsg.getSizes1(null, product);
			tparams.put(PDFProductSpecificationGenerator2.SIZES1, MOAHelper.getMOACollection(tsizes1));

			String tsizes2 = pmsg.getSizes2(null, product);
			tparams.put(PDFProductSpecificationGenerator2.SIZES2, MOAHelper.getMOACollection(tsizes2));

			String tsources = pmsg.getSources(null, product);
			tparams.put(BomDataGenerator.SOURCES, MOAHelper.getMOACollection(tsources));

			String tdest = pmsg.getDestinations(null, product);
			tparams.put(BomDataGenerator.DESTINATIONS, MOAHelper.getMOACollection(tdest));

			String tcolor = pmsg.getColorways(null, product);
			tparams.put(BomDataGenerator.COLORWAYS, MOAHelper.getMOACollection(tcolor));

			Collection resultVector = SizingQuery.findProductSizeCategoriesForProduct(product).getResults();
			if(resultVector.size() > 0){
				FlexObject obj = (FlexObject)resultVector.iterator().next();
				tparams.put(PDFProductSpecificationGenerator2.PRODUCT_SIZE_CAT_ID, obj.getString("PRODUCTSIZECATEGORY.IDA2A2"));
			}
			else{
				tparams.put(PDFProductSpecificationGenerator2.PRODUCT_SIZE_CAT_ID, "");
			}


		}

	}

	/////////////////////////////////////////////////////////////////////////////
	public static void debug(String msg){debug(msg, 1); }
	public static void debug(int i, String msg){debug(msg, i); }
	public static void debug(String msg, int i){
		if(DEBUG && i <= DEBUG_LEVEL) System.out.println(msg);
	}


	/** Returns an unmodifiable copy of the params Map on this PDFProductSpecificationGenerator2 instance
	 * This allows for testing and viewing the params while preventing their modification.
	 * @return
	 */
	public Map getUnmodifiableParams(){
		return Collections.unmodifiableMap(params);
	}
}
