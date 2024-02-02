package com.hbi.wc.product;

import com.hbi.wc.flexbom.gen.HBIBOMPDFContentGenerator;
import com.lcs.wc.client.web.pdf.PDFContentCollection;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flexbom.gen.BOMPDFContentGenerator;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.MaterialPriceList;
import com.lcs.wc.product.PDFProductSpecificationGenerator2;
import com.lcs.wc.product.SpecPageSet;
import com.lcs.wc.util.ClassLoadUtil;
import com.lcs.wc.util.FileLocation;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.RB;
import com.lowagie.text.Document;

import java.util.ArrayList;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import wt.util.WTException;
import wt.util.WTMessage;
/**
*
* @author Manoj From UST
* @Date Oct 4, 2018, 12:58 PM
* 
*
*       This class is implemented by taking the code from PDFProductSpecificationBOM2 and
*       modified as per requirement of HBI
* 
*/
public class HBIPDFProductSpecificationBOM2 implements PDFContentCollection, SpecPageSet {
	String operationLabel;
	String materialLabel;
	String colorLabel;
	String supplierLabel;
	String materialStatusLabel;
	private static final boolean DEBUG = LCSProperties
			.getBoolean("com.lcs.wc.product.HBIPDFProductSpecificationBOM2.verbose");
	private static final int DEBUG_LEVEL = Integer
			.parseInt(LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationBOM2.verboseLevel", "1"));
	public static final boolean BOM_HEADER_SAME_PAGE = LCSProperties
			.getBoolean("com.lcs.wc.product.PDFProductSpecificationGenerator.BOMHeaderSamePage");
	public static final boolean BOM_HEADER_EVERY_REPORT = LCSProperties
			.getBoolean("com.lcs.wc.product.PDFProductSpecificationGenerator.BOMHeaderEveryReport");
	public static final boolean BOM_FOOTER_SAME_PAGE = LCSProperties
			.getBoolean("com.lcs.wc.product.PDFProductSpecificationGenerator.BOMFooterSamePage");
	public static final boolean BOM_FOOTER_EVERY_REPORT = LCSProperties
			.getBoolean("com.lcs.wc.product.PDFProductSpecificationGenerator.BOMFooterEveryReport");
	//Modified by reeno - Start 2-6-2019
	public static final String HBI_BOM_REPORTS = LCSProperties
			.get("com.lcs.wc.product.PDFProductSpecificationGenerator.hbiBOMReports");
	//Modified by reeno - End 2-6-2019
	protected ClassLoadUtil clu;
	public static String PRODUCT_ID = "PRODUCT_ID";
	public static String SPEC_ID = "SPEC_ID";
	public static String HEADER_HEIGHT = "HEADER_HEIGHT";
	public static final String BOM_HEADER_CLASS = "BOM_HEADER_CLASS";
	public static final String BOM_FOOTER_CLASS = "BOM_FOOTER_CLASS";
	public static final String BOL_HEADER_CLASS = "BOL_HEADER_CLASS";
	public static final String BOL_FOOTER_CLASS = "BOL_FOOTER_CLASS";
	public static String COLORWAYS = "COLORWAYS";
	public static String SOURCES = "SOURCES";
	public static String DESTINATIONS = "DESTINATIONS";
	public static final boolean COSTING = LCSProperties.getBoolean("jsp.flexbom.costedBOM");
	public static final String viewBOMMode = "VIEW";
	public static final String skuMode = "SINGLE";
	public static final String BOM_HEADER_ATTS = "BOM_HEADER_ATTS";
	public static final String BOM_HEADER_PAGE_TITLES = "BOM_HEADER_PAGE_TITLES";
	public static final String PRINT_BOM_HEADER = "PRINT_BOM_HEADER";
	public static final String PRINT_BOM_HEADER_SAME_PAGE = "PRINT_BOM_HEADER_SAME_PAGE";
	public static final String BOM_FOOTER_ATTS = "BOM_FOOTER_ATTS";
	public static final String BOM_FOOTER_PAGE_TITLES = "BOM_FOOTER_PAGE_TITLES";
	public static final String PRINT_BOM_FOOTER = "PRINT_BOM_FOOTER";
	public static final String PRINT_BOM_FOOTER_SAME_PAGE = "PRINT_BOM_FOOTER_SAME_PAGE";
	
	private static String PRICE_COLUMN = "";
	public float pageHeight;
	@SuppressWarnings("rawtypes")
	Collection pageTitles;
	String propertyFile;

	@SuppressWarnings("rawtypes")
	public HBIPDFProductSpecificationBOM2() {
		this.operationLabel = WTMessage.getLocalizedMessage("com.lcs.wc.resource.FlexBOMRB", "operation_LBL", RB.objA);
		this.materialLabel = WTMessage.getLocalizedMessage("com.lcs.wc.resource.MaterialRB", "material_LBL", RB.objA);
		this.colorLabel = WTMessage.getLocalizedMessage("com.lcs.wc.resource.ColorRB", "color_LBL", RB.objA);
		this.supplierLabel = WTMessage.getLocalizedMessage("com.lcs.wc.resource.FlexBOMRB", "supplier_LBL", RB.objA);
		this.materialStatusLabel = WTMessage.getLocalizedMessage("com.lcs.wc.resource.FlexBOMRB", "materialStatus_LBL",
				RB.objA);
		this.clu = null;
		this.pageHeight = 0.0F;
		this.pageTitles = new ArrayList();
		this.propertyFile = null;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection getPDFContentCollection(Map params, Document document) throws WTException {
		debug(1, "PDFProductSpecificationBOM2.getPDFContentCollection()");
		debug(2, "params.keySet():  " + params.keySet());

		try {
			//Modified by reeno - Start 2-6-2019
			ArrayList<String> mattBomReport = new ArrayList<String>();
			StringTokenizer strToken = new StringTokenizer(HBI_BOM_REPORTS,",");
			while(strToken.hasMoreTokens()){
				mattBomReport.add(strToken.nextToken());
			}
			//Modified by reeno - End 2-6-2019
			String Id = (String) params.get(PDFProductSpecificationGenerator2.COMPONENT_ID);
			this.pageTitles = new ArrayList();
			FlexBOMPart bomPart = (FlexBOMPart) LCSQuery.findObjectById(Id);
			params.put(BOMPDFContentGenerator.BOM_PART, bomPart);
			params.put("FLEXTYPED", bomPart);
			params.put("PRINT_BOM_HEADER_SAME_PAGE", String.valueOf(BOM_HEADER_SAME_PAGE));
			params.put("PRINT_BOM_FOOTER_SAME_PAGE", String.valueOf(BOM_FOOTER_SAME_PAGE));
			this.getBOMHeaderAndFooter(params, document);
			boolean printFooter = FormatHelper.parseBoolean((String) params.get("PRINT_BOM_FOOTER"));
			if (printFooter && !BOM_FOOTER_EVERY_REPORT) {
				params.remove("PRINT_BOM_FOOTER");
			}

			if (FormatHelper.hasContent(this.propertyFile)) {
				this.clu = new ClassLoadUtil(this.propertyFile);
			} else {
				this.clu = new ClassLoadUtil(FileLocation.productSpecBOMProperties2);
			}

			Collection bomOptions = (Collection) params.get(PDFProductSpecificationGenerator2.COMPONENT_PAGE_OPTIONS);
			this.pageHeight = this.calcPageHeight(params, document);
			Collection tables = new ArrayList();
			String option = "";
			if (bomOptions != null && bomOptions.size() > 0) {
				Iterator bomOptionsIter = bomOptions.iterator();
				params.put("RAW_DATA", this.getBOMData(bomPart));

				while (bomOptionsIter.hasNext()) {
					option = (String) bomOptionsIter.next();
					HBIBOMPDFContentGenerator bomHbiCG =null;
					BOMPDFContentGenerator bomCG = null; 
					debug("Creating PDF BOM Option :  " + option);

					if (!bomOptionsIter.hasNext() && printFooter && !BOM_FOOTER_EVERY_REPORT) {
						params.put("PRINT_BOM_FOOTER", "true");
					}
					if("GPLabelBOM".equals(option) || "GPcolorwayReport".equals(option)||"GPAttributionBOM".equals(option)) {
						 bomHbiCG = (HBIBOMPDFContentGenerator) this.clu.getClass(option);
						if (bomHbiCG == null) {
							debug("#Could not find a class for option : " + option + ".  Skipping BOM Option");
						}
					}else {
						 bomCG = (BOMPDFContentGenerator) this.clu.getClass(option);
						if (bomCG == null) {
							debug("#Could not find a class for option : " + option + ".  Skipping BOM Option");
						}
					} 
					
					if (this.clu.getParams(option) != null) {
						params.putAll(this.clu.getParams(option));
					}
					if(mattBomReport.contains(option)) {
						params.put(PDFProductSpecificationGenerator2.REPORT_NAME, option);
					} else {  
						String display = WTMessage.getLocalizedMessage("com.lcs.wc.resource.FlexBOMRB", option, RB.objA);
						params.put(PDFProductSpecificationGenerator2.REPORT_NAME, display);
					} 
					params.put(PDFProductSpecificationGenerator2.REPORT_KEY, option);
					
					if ("GPLabelBOM".equals(option)|| "GPcolorwayReport".equals(option)||"GPAttributionBOM".equals(option)) {
						tables.addAll(bomHbiCG.getPDFContentCollection(params, document));
						this.pageTitles.addAll(bomHbiCG.getPageTitles());
					} else {
						tables.addAll(bomCG.getPDFContentCollection(params, document));
						this.pageTitles.addAll(bomCG.getPageTitles());
					}
				
					if (!BOM_HEADER_EVERY_REPORT && params.containsKey("PRINT_BOM_HEADER")) {
						params.remove("PRINT_BOM_HEADER");
					}
				}
			}

			return tables;
		} catch (Exception var12) {
			throw new WTException(var12);
		}
	}

	@SuppressWarnings("rawtypes")
	private float calcPageHeight(Map params, Document doc) throws WTException {
		float height = doc.top() - doc.bottom();
		if (params.get(HEADER_HEIGHT) != null) {
			Object hh = params.get("HEADER_HEIGHT");
			if (hh instanceof Float) {
				height -= (Float) hh;
			}

			if (hh instanceof String) {
				height -= new Float((String) hh);
			}
		}

		return height;
	}

	@SuppressWarnings("rawtypes")
	public Collection getPageHeaderCollection() {
		return this.pageTitles;
	}

	@SuppressWarnings("rawtypes")
	public Collection getBOMData(FlexBOMPart bomPart) throws WTException {
		new ArrayList();
		FlexType bomType = bomPart.getFlexType();
		FlexType materialType = bomType.getReferencedFlexType("MATERIAL_TYPE_ID");
		Collection qresults = LCSFlexBOMQuery.findFlexBOMData(bomPart, (String) null, (String) null, (String) null,
				(String) null, (String) null, "EFFECTIVE_ONLY", (Date) null, false, false, "ALL_DIMENSIONS",
				(String) null, (String) null, (String) null, materialType).getResults();
		 qresults = LCSFlexBOMQuery.bomSort(qresults, bomPart.getFlexType());
		qresults = LCSFlexBOMQuery.joinInLinkedBOMs(qresults);
		this.mergeMaterialColorPricing(qresults);
		return qresults;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void mergeMaterialColorPricing(Collection data) throws WTException {
		Iterator i = data.iterator();
		FlexObject record = null;
		Collection matSups = new ArrayList();
		Collection matSupColors = new ArrayList();
		Map matSup = null;
		Map matSupColor = null;
		String materialColorId = "";
		String materialSupplierMasterId = "";

		while (i.hasNext()) {
			record = (FlexObject) i.next();
			matSupColor = new HashMap();
			matSup = new HashMap();
			materialColorId = FormatHelper.format(record.getData("LCSMATERIALCOLOR.IDA2A2"));
			materialSupplierMasterId = FormatHelper.format(record.getData("LCSMATERIALSUPPLIERMASTER.IDA2A2"));
			matSup.put("materialSupplierMasterId", materialSupplierMasterId);
			matSupColor.put("materialSupplierMasterId", materialSupplierMasterId);
			matSupColor.put("materialColorId", materialColorId);
			matSups.add(matSup);
			matSupColors.add(matSupColor);
		}

		if (matSupColors.size() > 0) {
			Date reqDate = new Date();
			MaterialPriceList mpl = new MaterialPriceList(matSups, matSupColors, reqDate);
			i = data.iterator();

			while (i.hasNext()) {
				record = (FlexObject) i.next();
				materialColorId = FormatHelper.format(record.getData("LCSMATERIALCOLOR.IDA2A2"));
				materialSupplierMasterId = FormatHelper.format(record.getData("LCSMATERIALSUPPLIERMASTER.IDA2A2"));
				double materialPrice = mpl.getPrice(materialSupplierMasterId, materialColorId);
				record.put(PRICE_COLUMN, materialPrice);
			}
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void getBOMHeaderAndFooter(Map params, Document document) throws WTException {
		try {
			String getAtts = (String) params.get("PRINT_BOM_HEADER");
			BOMPDFContentGenerator bomAttCG = (BOMPDFContentGenerator) params.get("BOM_HEADER_CLASS");
			if (FormatHelper.parseBoolean(getAtts) && bomAttCG != null) {
				debug("getBOMHeaderAndFooter():--Generating header attributes");
				bomAttCG.init();
				params.put("BOM_HEADER_ATTS", bomAttCG.getPDFContentCollection(params, document));
				params.put("BOM_HEADER_PAGE_TITLES", bomAttCG.getPageTitles());
			} else {
				debug("getBOMHeaderAndFooter():Not generating header attributes");
			}

			String useFooter = (String) params.get("PRINT_BOM_FOOTER");
			bomAttCG = (BOMPDFContentGenerator) params.get("BOM_FOOTER_CLASS");
			if (FormatHelper.parseBoolean(useFooter) && bomAttCG != null) {
				debug(1, "getBOMHeaderAndFooter():--Generating footer");
				bomAttCG.init();
				params.put("BOM_FOOTER_ATTS", bomAttCG.getPDFContentCollection(params, document));
				params.put("BOM_FOOTER_PAGE_TITLES", bomAttCG.getPageTitles());
			} else {
				debug(1, "getBOMHeaderAndFooter():Not generating footer");
			}

		} catch (Exception var6) {
			throw new WTException(var6);
		}
	}

	public static void debug(String msg) {
		debug(msg, 1);
	}

	public static void debug(int i, String msg) {
		debug(msg, i);
	}

	public static void debug(String msg, int i) {
		if (DEBUG && i <= DEBUG_LEVEL) {
			System.out.println(msg);
		}

	}

	static {
		try {
			PRICE_COLUMN = "LCSMATERIALSUPPLIER."
					+ FlexTypeCache.getFlexTypeRoot("Material").getAttribute("materialPrice").getVariableName();
		} catch (Exception var1) {
			System.out.println("ERROR: Can not determing the matrialPrice attribute for Material type");
			var1.printStackTrace();
		}

	}
}