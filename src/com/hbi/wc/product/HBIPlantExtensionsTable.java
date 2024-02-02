package com.hbi.wc.product;
import com.lcs.wc.client.web.PDFGeneratorHelper;
import com.lcs.wc.client.web.pdf.PDFContent;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.util.LCSProperties;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPTable;
import java.util.Map;
import wt.fc.WTObject;
import wt.util.WTException;

/**
 * HBIPlantExtensionsTable.java
 *
 * This class contains a function which is using to print Plant Extensions Table
 * from the Selling Product on Selling Product Spec (PDF).
 * 
 * If any changes in column order/type of moa table attributes , can be control it using class variable 'TABLE_COLUMNS'
 * If any changes in column width of moa table attributes , can be control it using class variable 'COLUMNS_WIDTH'
 * If any changes in text font size of moa table data , can be control it using class variable 'fontSize'
 
 * @author Manoj Konakalla
 * @ modified April-05-2019
 */
public class HBIPlantExtensionsTable implements PDFContent {
	private static final String DELIM_COMMA = ",";
	private static final String PROD_MOA_ATTR_KEY = "hbiErpPlantExtensions";
	public static String PRODUCT_ID = "PRODUCT_ID";
	public static final String BASIC_CUT_AND_SEW_SELLING = "BASIC CUT & SEW - SELLING";
	public static PDFGeneratorHelper pgh = new PDFGeneratorHelper();
	private static String fontSize = "8";
	private static final String PLANTEXT_TABLE_COLUMN_ATTR="col1:hbiPlantName1,col2:hbiPrimaryDeliverPlant,col3:hbiPlantType,col4:hbiMaxLotSize,col5:hbiPlannedDelTime,col6:hbiTotalRepLeadTme";
	private static final String MOA_TABLE_COLUMNS = LCSProperties.get("com.hbi.product.sp.moaTable.plantExt.columns", PLANTEXT_TABLE_COLUMN_ATTR);
	private static final String[] TABLE_COLUMNS = MOA_TABLE_COLUMNS.split(DELIM_COMMA);
	/*
	private static final String[] TABLE_COLUMNS = { 
			"col1=hbiPlantName1", 
			"col2=hbiPrimaryDeliverPlant",
			"col3=hbiPlantType",
			"col4=hbiMaxLotSize", 
			"col5=hbiPlannedDelTime",
			"col6=hbiTotalRepLeadTme" };*/
	private static final String PLANEXTN_MOA_TYPE = "Multi-Object\\Plant Extensions";
	float[] COLUMNS_WIDTH = { 13f, 13f, 13f, 13f, 13f, 13f };

	/**
	 * 
	 * @param paramMap
	 *            - Map
	 * @param paramDocument
	 *            - Document
	 * @return mainTable - Element
	 * @throws WTException
	 */
	@SuppressWarnings("rawtypes")
	public Element getPDFContent(Map paramMap, Document paramDocument) throws WTException {
		// Below logic is using to draw Plant Extensions MOA Table on a Product Spec.
		// Selling Product Spec.
		PdfPTable plant_ext_moaTable = null;
		WTObject obj = (WTObject) LCSProductQuery.findObjectById((String) paramMap.get(PRODUCT_ID));
		if (obj instanceof LCSProduct) {
			LCSProduct product = (LCSProduct) obj;
			//Start Generate Plant Extensions Moa table
			HBIMoaTableGenerator moaGen = new HBIMoaTableGenerator();
			plant_ext_moaTable = moaGen.getMoaPDFContent(product, PROD_MOA_ATTR_KEY, PLANEXTN_MOA_TYPE,
					TABLE_COLUMNS, COLUMNS_WIDTH,fontSize);
			//End Generate Plant Extensions Moa table
		} else {
			throw new WTException("Can not use PDFProductSpecification on a non-LCSProduct - " + obj);
		}
		return plant_ext_moaTable;

	}

}