package com.hbi.wc.product;
import java.util.Map;
import com.lcs.wc.client.web.pdf.PDFContent;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.util.LCSProperties;

import wt.fc.WTObject;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPTable;

import wt.util.WTException;

/**
 * HBIPutUpCodeTable.java
 *
 * This class contains a function which is using to print Put Up Code Table
 * from the Selling Product on Selling Product Spec (PDF).
 * If any changes in column order/type of moa table attributes , can be control it using class variable 'TABLE_COLUMNS'
 * If any changes in column width of moa table attributes , can be control it using class variable 'COLUMNS_WIDTH'
 * If any changes in text font size of moa table data , can be control it using class variable 'fontSize'
 * 
 * @author Manoj Konakalla
 * @ modified April-05-2019
 */
public class HBIPutUpCodeTable implements PDFContent {
	private static final String DELIM_COMMA = ",";
	private static final String PROD_MOA_ATTR_KEY = "hbiPutUpCode";
	public static String PRODUCT_ID = "PRODUCT_ID";
	public static final String BASIC_CUT_AND_SEW_SELLING = "BASIC CUT & SEW - SELLING";
	private static String fontSize = "8";
	private static final String PUTUP_TABLE_COLUMN_ATTR="col1:hbiPutUpCode,col2:hbiPrimaryPutup,col3:hbiEPC,col4:hbiReferenceSpecification,col5:hbiStartDate,col6:hbiPutUpCodeReason,col7:hbiEndDate";
	private static final String MOA_TABLE_COLUMNS = LCSProperties.get("com.hbi.product.sp.moaTable.putupcode.columns", PUTUP_TABLE_COLUMN_ATTR);
	private static final String[] TABLE_COLUMNS = MOA_TABLE_COLUMNS.split(DELIM_COMMA);
	/*private static final String[] TABLE_COLUMNS = {
			"col1=hbiPutUpCode", 
			"col2=hbiPrimaryPutup",
			"col3=hbiEPC", 
			"col4=hbiReferenceSpecification",
			"col5=hbiStartDate",
			"col6=hbiPutUpCodeReason",
			"col7=hbiEndDate" };
	*/	
	private static final String PUTUP_MOA_PATH = "Multi-Object\\Put Up Code";
	float[] COLUMNS_WIDTH =  { 35f,10f,5f,20f,10f,25f,10f };


	/* (non-Javadoc)
	 * @see com.lcs.wc.client.web.pdf.PDFContent#getPDFContent(java.util.Map, com.lowagie.text.Document)
	 */
	
	public Element getPDFContent(Map paramMap, Document paramDocument) throws WTException {
		// Below logic is using to draw PUTUP CODE MOA Table on a Product Spec.
		PdfPTable putup_moaTable = null;
		WTObject obj = (WTObject) LCSProductQuery.findObjectById((String) paramMap.get(PRODUCT_ID));
		if (obj instanceof LCSProduct) {
			LCSProduct product = (LCSProduct) obj;
			HBIMoaTableGenerator moaGen = new HBIMoaTableGenerator();
			putup_moaTable = moaGen.getMoaPDFContent(product, PROD_MOA_ATTR_KEY, PUTUP_MOA_PATH,
					TABLE_COLUMNS, COLUMNS_WIDTH,fontSize);
		} else {
			throw new WTException("Can not use PDFProductSpecification on a non-LCSProduct - " + obj);
		}
		return putup_moaTable;
	}

}