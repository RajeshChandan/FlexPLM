/**
 * 
 */
package com.sportmaster.wc.reports;

//import java.util.Date;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import wt.util.WTException;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.client.web.ExcelGeneratorHelper;
import com.lcs.wc.client.web.ExcelTableHeaderGenerator;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.supplier.LCSSupplierMaster;
import com.lcs.wc.util.FormatHelper;
//import com.ibm.icu.text.SimpleDateFormat;


/**
 * The Class SMMaterialForecastReportHeader.
 *
 * @author 'true' Nawab Khan.
 * @version 'true' 1.0 version number.
 */
public class SMMaterialForecastReportHeader extends ExcelTableHeaderGenerator {


	/** The Constant LEFT. */
	private static final String LEFT = "left";

	/** The Constant RPT_HEADER. */
	private static final String RPT_HEADER = "RPT_HEADER";
	/** The request. */
	private SMMaterialForecastReportBean reportBean;

	/**
	 * Instantiates a new hb report excel header generator.
	 *
	 * @param request the request
	 * @throws WTException the wT exception
	 */
	public SMMaterialForecastReportHeader(SMMaterialForecastReportBean reportBean) throws WTException{
		super();		 
		this.reportBean=reportBean; 
	}
	/**
	 * 
	 * @param bom - bom.
	 * @param wb - wb.
	 * @param ws - ws.
	 * @param columnCount - columnCount.
	 * @param columnIndex - columnIndex.
	 * @return true/false.
	 */
	//method to create header
	public int createHeader(HSSFWorkbook wb, HSSFSheet ws, int columnCount){

		if(this.reportBean != null){
			int rowId = 0;		 
			try {
				rowId=createReportHeader(wb, ws, columnCount);
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ++rowId;
		}
		return 0;
	}

	/**
	 * Creates the bom iteration report header.
	 *
	 * @param wb the wb
	 * @param bom - bom.
	 * @param ws the ws.
	 * @param columnIndex the columnIndex.
	 * @param columnCount the column count.
	 * @return true/false.
	 * @throws WTException the WTException.
	 */
	public int createReportHeader(HSSFWorkbook wb, HSSFSheet ws, int columnCount) throws WTException{

		if(this.reportBean != null){
			ws.setDisplayGridlines(true);	 

			this.egh = new ExcelGeneratorHelper(wb);

			int columnIndex=0;
			// First Row
			String firstRowText = "Report Filter";

			int rowId = 0;
			HSSFRow row =null;
			if(columnIndex>0){
				row =ws.getRow(rowId);
			}else{
				row =ws.createRow(rowId);
			}

			HSSFCell cell = row.createCell(columnIndex);
			// set cell style to the header
 			HSSFCellStyle style = wb.createCellStyle();
			 
			HSSFCellStyle cellstyle = egh.getCellStyle(RPT_HEADER, LEFT, "8");		

			row.setRowStyle(style);			
			cell.setCellStyle(cellstyle);			
			cell.setCellValue(firstRowText);
			rowId++;


			if(columnIndex>0){
				row =ws.getRow(rowId);
			}else{
				row =ws.createRow(rowId);
			}

			rowId++;
			String seasons=getDisplayValues("season");
			//getting season name and adding it to header
			createHeaderCells(row,cellstyle,"Season:",seasons,0,columnIndex);

			if(columnIndex>0){
				row =ws.getRow(rowId);
			}else{
				row =ws.createRow(rowId);
			}

			rowId++;

			//Getting the display value for the selected material type criteria
			String selectedMaterialTypes=getMaterialDisplayValues("materialType");
			 
 			createHeaderCells(row,cellstyle,"Material Group\\Type:",selectedMaterialTypes,0,columnIndex);

			if(columnIndex>0){
				row =ws.getRow(rowId);
			}else{
				row =ws.createRow(rowId);
			}

			rowId++;
			//Getting the display value for the selected brand criteria
			String brands=getDisplayValues("brand");			 
 			createHeaderCells(row,cellstyle,"Brand:",brands,0,columnIndex);

			if(columnIndex>0){
				row =ws.getRow(rowId);
			}else{
				row =ws.createRow(rowId);
			}

			rowId++;

			String selectedMs=getMaterialDisplayValues("materialSupplier");
		 
			//getting season name and adding it to header
			createHeaderCells(row,cellstyle,"Material Supplier:",selectedMs,0,columnIndex);

			if(columnIndex>0){
				row =ws.getRow(rowId);
			}else{
				row =ws.createRow(rowId);
			}

			rowId++;

			String isNominated="";

			if(this.reportBean.isNominatedSupplier()){
				isNominated="Yes";
				// Added to display the filter criteria in document object - Start
				this.reportBean.setSelectedNominatedDisplay(isNominated);
				// Added to display the filter criteria in document object - End
			}

			//getting season name and adding it to header
			createHeaderCells(row,cellstyle,"Nominated Supplier:",isNominated,0,columnIndex);

			if(columnIndex>0){
				row =ws.getRow(rowId);
			}else{
				row =ws.createRow(rowId);
			}


			return rowId;
		}
		return 0;
	}

	/**
	 * @param objectName
	 * @return
	 * @throws WTException
	 */
	public String getDisplayValues(String objectName) throws WTException {
		String displayValue="";
		if("season".equalsIgnoreCase(objectName)){
			if(this.reportBean.getSelectedSeasonOids()!=null){
				for(String seasonId:this.reportBean.getSelectedSeasonOids()){
					LCSSeason season=(LCSSeason)LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:"+seasonId);
					if(FormatHelper.hasContent(displayValue)){
						displayValue=displayValue+", "+season.getName();
					}else{
						displayValue=season.getName();
					}
				}
			}
			// Added to display the filter criteria in document object - Start
			this.reportBean.setSelectedSeasonDisplayName(displayValue);
			// Added to display the filter criteria in document object - End
		}else if("brand".equalsIgnoreCase(objectName)){
			if(this.reportBean.getSelectedBrands()!=null){
				FlexType flexType=FlexTypeCache.getFlexTypeFromPath("Product");
				FlexTypeAttribute brandAttr = flexType.getAttribute("vrdBrand");
				AttributeValueList brandAttList = brandAttr.getAttValueList();
 				for(String brand:this.reportBean.getSelectedBrands()){ 					
 					if(FormatHelper.hasContent(displayValue)){ 						
						displayValue=displayValue+", "+brandAttList.getValue(brand,ClientContext.getContextLocale()); 
					}else{
						displayValue=brandAttList.getValue(brand,ClientContext.getContextLocale());
					}
				}
			}
			// Added to display the filter criteria in document object - Start
			this.reportBean.setSelectedBrandDisplayName(displayValue);
			// Added to display the filter criteria in document object - End
		}
		return displayValue;
	}

	
	/**
	 * Get the display value for material object
	 * @param objectName
	 * @return
	 * @throws WTException
	 */
	public String getMaterialDisplayValues(String objectName) throws WTException {
		String displayValue="";
		if("materialType".equalsIgnoreCase(objectName)){
			if(this.reportBean.getSelectedMaterialTypeOids()!=null){
				for(String typeOid:this.reportBean.getSelectedMaterialTypeOids()){
					FlexType tempType = FlexTypeCache.getFlexType(typeOid); 
					if(FormatHelper.hasContent(displayValue)){
						displayValue=displayValue+", "+tempType.getFullNameDisplay(true);
					}else{
						displayValue=tempType.getFullNameDisplay(true);
					}
				}
			}
			// Added to display the filter criteria in document object - Start
			this.reportBean.setSelectedMaterialTypeDisplayName(displayValue);
			// Added to display the filter criteria in document object - End
		}else if("materialSupplier".equalsIgnoreCase(objectName)){
			if(this.reportBean.getSelectedMatSupplierOids()!=null){
				for(String msOid:this.reportBean.getSelectedMatSupplierOids()){
					LCSSupplierMaster suppMaster=(LCSSupplierMaster)LCSQuery.findObjectById("OR:com.lcs.wc.supplier.LCSSupplierMaster:"+msOid);
					if(FormatHelper.hasContent(displayValue)){
						displayValue=displayValue+", "+suppMaster.getSupplierName();
					}else{
						displayValue=suppMaster.getSupplierName();
					}
				}
			}
			// Added to display the filter criteria in document object - Start
			this.reportBean.setSelectedMatSupDisplayName(displayValue);
			// Added to display the filter criteria in document object - End
		} 
		return displayValue;
	}



	/**
	 * creates header cells
	 * 
	 * @param row - row.
	 * @param cellstyle - cellstyle.
	 * @param label - label.
	 * @param rowText - rowText.
	 * @param cellIndex - cellIndex.
	 * @param columnIndex - columnIndex.
	 */
	private void createHeaderCells(
			HSSFRow row, HSSFCellStyle cellstyle,String label,String rowText,int cellIndex,int columnIndex) {
		HSSFCell cell;
		HSSFRichTextString cellText;

		row.setRowStyle(cellstyle);
		// create cell
		cell = row.createCell(cellIndex+columnIndex);
		cell.setCellStyle(cellstyle);

		cellText = new HSSFRichTextString(label);
		cell.setCellValue(cellText);		
		cell = row.createCell(cellIndex+columnIndex+1); 
		// set cell style
		cell.setCellStyle(cellstyle);
		cellText = new HSSFRichTextString(rowText);
		cell.setCellValue(cellText);	
	}
}
