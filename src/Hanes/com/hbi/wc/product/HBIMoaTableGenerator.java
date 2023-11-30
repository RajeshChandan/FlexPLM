package com.hbi.wc.product;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.hbi.wc.util.HBIUtil;
import com.lcs.wc.client.web.PDFGeneratorHelper;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import wt.fc.WTObject;
import wt.util.WTException;

/**
 * @author Manoj Konakala
 * @Date April-05-2019
 *
 */
public class HBIMoaTableGenerator {
	
	private static final String DELIM_EQUAL = ":";
	private static final String COL = "col";
	public static PDFGeneratorHelper pgh = new PDFGeneratorHelper();
	private static String fontSize = "8";
	private  String[] TABLE_COLUMNS = null;
	private int  MOA_TABLE_COLUMNS_COUNT = 0;
	private static String MOA_OBJECT_TYPE = "";
	private static  Map<String, String> TABLE_COLUMNS_MAP = null;
	float[] COLUMNS_WIDTH = null;

	/**
	 * @param owner
	 * @param flexObj_MoaAttrKey
	 * @param moa_type_path
	 * @param table_columns
	 * @param columns_Width
	 * @param text_fontSize
	 * @return
	 * @throws WTException
	 */
	public  PdfPTable getMoaPDFContent(WTObject owner, String flexObj_MoaAttrKey,String moa_type_path,String[] table_columns,float[] columns_Width,String text_fontSize) throws WTException {
		TABLE_COLUMNS = table_columns;
		COLUMNS_WIDTH = columns_Width;
		MOA_OBJECT_TYPE = moa_type_path;
		MOA_TABLE_COLUMNS_COUNT = TABLE_COLUMNS.length;
		TABLE_COLUMNS_MAP = getTableColumns(TABLE_COLUMNS);
		fontSize =  text_fontSize;
		return getMoaPDFGenerator(owner, flexObj_MoaAttrKey);
		
	}

	/**
	 * @param owner
	 * @param moaAttrKey
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private PdfPTable getMoaPDFGenerator(WTObject owner, String moaAttrKey) throws WTException {
		Collection<LCSMOAObject> moaCollectionObj = new Vector();
		PdfPTable moaTable = new PdfPTable(1);	
		try {
			FlexType flextype =HBIUtil.getFlexObjectType(owner);
			
			// Getting collection of  MOA object.
			
			FlexTypeAttribute moaFlexTypeAttObj = ((FlexType) flextype).getAttribute(moaAttrKey);
			moaCollectionObj = LCSMOAObjectQuery.findMOACollection(owner, moaFlexTypeAttObj);

			// Start Header columns of the moa table
			PdfPTable moaHeaderRowTable = createHeaderRow(MOA_OBJECT_TYPE,COLUMNS_WIDTH,TABLE_COLUMNS_MAP);

			moaTable.addCell(new PdfPCell(moaHeaderRowTable));
			// End Header columns of the moa table

			//Start Data rows of the Moa table
			for (LCSMOAObject moaObject : moaCollectionObj) {
				PdfPTable moaDataRow = new PdfPTable(MOA_TABLE_COLUMNS_COUNT);
				moaDataRow.setWidths(COLUMNS_WIDTH);
			
				
				for(int i=1;i<=MOA_TABLE_COLUMNS_COUNT;i++){
					//Data rows
					moaDataRow.addCell(createDataCell(moaObject, TABLE_COLUMNS_MAP.get(COL+i)));

				}
				moaTable.addCell(new PdfPCell(moaDataRow));
			}
			//End Data rows of the Moa table
			
			return moaTable;
		} catch (Exception e) {
			throw new WTException(e);
		}
	}


	/**
	 * @param columns
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String, String> getTableColumns(String[] columns) {
		 Map<String, String> columnMap = new HashMap();
		 
		for(String col : columns){
			//System.out.println("col:: "+col);
			String[] colkey = col.split(DELIM_EQUAL);
			//System.out.println("colkey[0] :: "+colkey[0]);
			//System.out.println("colkey[1] :: "+colkey[1]);
			columnMap.put(colkey[0], colkey[1]);
		}
		
		return columnMap;
	}

	/**
	 * @param label
	 * @return
	 */
	@SuppressWarnings("static-access")
	private PdfPCell createHeaderCell(String label) {
		Font font = pgh.getCellFont("RPT_HEADER", "Left", fontSize);
		PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(label, font));
		pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_HEADER", null));
		return pdfpcell;
	}


	/**
	 * @param data
	 * @return
	 */
	@SuppressWarnings("static-access")
	private PdfPCell createDataCell(String data) {
		Font font = pgh.getCellFont("RPT_TBD", "Left", fontSize);
		PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(data, font));
		pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_TBD", null));
		return pdfpcell;
	}

	/**
	 * @param moa_path
	 * @param columnWidths
	 * @param col
	 * @param tableColumns
	 * @return
	 * @throws DocumentException
	 * @throws WTException
	 */
	private PdfPTable createHeaderRow(String moa_path, float[] columnWidths,Map<String,String> tableColumns) throws DocumentException, WTException {

		PdfPTable putUpCodeHeaderAttTable = new PdfPTable(MOA_TABLE_COLUMNS_COUNT);
		putUpCodeHeaderAttTable.setWidths(columnWidths);
		PdfPCell cell = null;
		for(int i=1;i<=MOA_TABLE_COLUMNS_COUNT;i++){
			//Header columns
			cell = createHeaderCell(HBIUtil.getAttDisplayValue(moa_path,tableColumns.get(COL+i)));
			putUpCodeHeaderAttTable.addCell(cell);

		}
		return putUpCodeHeaderAttTable;
	}

	/**
	 * @param moaObject
	 * @param attrKey
	 * @return
	 * @throws WTException
	 * @author Manoj Konakalla
	 * @Date April 3,2019
	 */
	private PdfPCell createDataCell(LCSMOAObject moaObject, String attrKey) throws WTException {
		String attrValue = HBIUtil.getAttributeTypeValue(moaObject, attrKey);
		return createDataCell(attrValue);

	}
}
