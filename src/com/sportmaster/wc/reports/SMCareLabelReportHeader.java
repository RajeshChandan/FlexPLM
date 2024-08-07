package com.sportmaster.wc.reports;

import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import wt.util.WTException;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.client.web.ExcelGeneratorHelper;
import com.lcs.wc.client.web.ExcelTableHeaderGenerator;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;

/**
 * The Class SMCareLabelReportHeader.
 *
 * @version 'true' 1.0 version number.
 * @author 'true' ITC.
 */
public class SMCareLabelReportHeader extends ExcelTableHeaderGenerator {

	/** The Constant LEFT. */
	private static final String LEFT = "left";

	/** The Constant RPT_HEADER. */
	private static final String RPT_HEADER = "RPT_HEADER";
	/** The request. */
	private SMCareLabelReportBean reportBean;
	/** The Constant LOGGER. */
	private static final Logger LOGGER = 
			Logger.getLogger("CARELABELREPORTLOG");
	/** The Constant PRODUCT. */
	private static final String PRODUCT = "Product";
	/**
	 * Instantiates a new hb report excel header generator.
	 *
	 * @param reportBean - reportBean
	 * @throws WTException - WTException
	 */
	public SMCareLabelReportHeader(SMCareLabelReportBean reportBean) 
			throws WTException{
		super();		 
		this.reportBean=reportBean; 
	}
	
	/**
	 * 
	 * @param wb - wb.
	 * @param ws - ws.
	 * @param columnCount - columnCount.
	 * @return rowId - rowId
	 */
	//method to create header
	public int createHeader(HSSFWorkbook wb,
			HSSFSheet ws, int columnCount){
		// call createReportHeader method
		if(this.reportBean != null){
			int rowId = 0;		 
			rowId=createReportHeader(wb, ws, columnCount);
			return ++rowId;
		}
		return 0;
	}

	/**
	 * @param wb - wb
	 * @param ws - ws
	 * @param columnCount - columnCount
	 * @return rowId - rowId
	 * @throws WTException - WTException
	 */
	public int createReportHeader(HSSFWorkbook wb, 
			HSSFSheet ws, int columnCount)
	{		
		LCSSeason season;
		int rowId = 1;
		HSSFRow row =null;
		int columnIndex=0;
		
		ws.setDisplayGridlines(true);
		this.egh = new ExcelGeneratorHelper(wb);
		
		// First Row
		String firstRowText = "Report Filter";
		
		
		// Get the HSSFRow
		row = getHSSFRow(ws,rowId, columnIndex);

		HSSFCell cell = row.createCell(columnIndex);
		// set cell style to the header
		HSSFCellStyle style = wb.createCellStyle();		 
		HSSFCellStyle cellstyle = egh.
				getCellStyle(RPT_HEADER, LEFT, "8");		
		//row.
		//cell.
		ws.addMergedRegion(new CellRangeAddress(1, 1, 0, 1));
		//ws.
		row.setRowStyle(style);			
		cell.setCellStyle(cellstyle);			
		cell.setCellValue(firstRowText);
		rowId++;
		// Get the HSSFRow
		row = getHSSFRow(ws,rowId, columnIndex);
		
		rowId++;		
		//Getting the display value for 
		//the selected season name criteria
		String seasons = "";	
		String selectedProducts = "";
		
		try {
		// Get the season object
		season=(LCSSeason)LCSQuery.findObjectById(
				"VR:com.lcs.wc.season.LCSSeason:"
						+this.reportBean.getSelectedSeasonOid());
		if(null != season){
			seasons = season.getName();
		}
		//getting season name and adding it to header
		createHeaderCells(row,cellstyle,
				"Season:",seasons,0,columnIndex);
		// Get the HSSFRow
		row = getHSSFRow(ws,rowId, columnIndex);

		rowId++;
		//Getting the display value for 
		//the selected product name criteria
		int i=0;
		
		LCSProduct product;
		if(null != this.reportBean.getSelectedProductName()){				
			for(String productId:this.reportBean.getSelectedProductName()){
				// Get product object
				product =(LCSProduct)LCSQuery.findObjectById(
						"VR:com.lcs.wc.product.LCSProduct:"+productId);
				if(FormatHelper.hasContent(selectedProducts)){
					i++;
					// splitting to display 5 products in a row
					if(i==5){
						selectedProducts=selectedProducts+", "+ "\n" +product.getName();
						i=0;
					}
					else{
						selectedProducts=selectedProducts+", "+product.getName();
					}
				}else{
					selectedProducts=product.getName();
				}
			}			
		}
		// Wrap the entire row for product name row
		cellstyle.setWrapText(true);
		row.setRowStyle(cellstyle);
		
		try {
		// Add selected product names to header cell
		createHeaderCells(row,cellstyle,
				PRODUCT,selectedProducts,0,columnIndex);
		}
		catch(IllegalArgumentException iae) {
			LOGGER.error("IllegalArgumentException in SMCareLabelReportHeader - " 
					+"createReportHeader: "
						+iae.getMessage());
			selectedProducts = "The maximum length of cell contents (text) is 32,767 characters.\nSelected products length is exceeding the limit, and can't be displayed in the report.";
			createHeaderCells(row,cellstyle,
					PRODUCT,selectedProducts,0,columnIndex);
		}
		// Get the HSSFRow
		row = getHSSFRow(ws,rowId, columnIndex);
		rowId++;		
		//Getting the display value for 
		//the selected brand criteria
		String brands=getDisplayValues(
				"brand",this.reportBean.getSelectedBrands(),
				PRODUCT,LCSProperties.get("product.BRAND"));
		// Add selected Brand to header cell
		createHeaderCells(row,cellstyle,
				"Brand",brands,0,columnIndex);
		// Get the HSSFRow
		row = getHSSFRow(ws,rowId, columnIndex);

		rowId++;		
		//Getting the display value for 
		//the selected gender criteria
		String gender=getDisplayValues(
				"gender",this.reportBean.getSelectedGenders(),
				PRODUCT,LCSProperties.get("product.GENDER"));	
		// Add selected Gender to header cell
		createHeaderCells(row,cellstyle,
				"Gender",gender,0,columnIndex);
		// Get the HSSFRow
		row = getHSSFRow(ws,rowId, columnIndex);

		rowId++;		
		//Getting the display value for 
		//the selected age criteria
		String age=getDisplayValues("age",this.reportBean.getSelectedAges(),
				PRODUCT,LCSProperties.get("product.AGE"));		
		// Add selected Age to header cell
		createHeaderCells(row,cellstyle,
				"Age",age,0,columnIndex);
		// Get the HSSFRow
		row = getHSSFRow(ws,rowId, columnIndex);

		rowId++;				
		//Getting the display value for 
		//the selected Project criteria
		String selectedProjects=getDisplayValues(
				"project",this.reportBean.getSelectedProject(),
				PRODUCT,LCSProperties.get("product.PROJECT"));	
		// Add selected Project to header cell
		createHeaderCells(row,cellstyle,
				"Project",selectedProjects,0,columnIndex);
		// Get the HSSFRow
		row = getHSSFRow(ws,rowId, columnIndex);

		rowId++;				
		
		//Updated for - 3.8.2.0 build - Start
		//Getting the display value for 
		//the selected Production Group criteria
		String productionGroup= "";
		
		if(season.getProductType().getFullNameDisplay().startsWith("SEPD")) {
			
			productionGroup=getDisplayValues("productionGroup",
					this.reportBean.getSelectedProductionGroupOid(),
					"Product\\SEPD",LCSProperties.get("productSeason.PRODUCTIONGROUP"));	
			
		}else if(season.getProductType().getFullNameDisplay().startsWith("FPD")) {
			
			productionGroup=getDisplayValues("productionGroup",
					this.reportBean.getSelectedProductionGroupOid(),
					"Product\\FPD",LCSProperties.get("productSeason.PRODUCTIONGROUP"));
			
		}else if(season.getProductType().getFullNameDisplay().startsWith("Accessories")) {
			
			productionGroup=getDisplayValues("productionGroup",
					this.reportBean.getSelectedProductionGroupOid(),
					"Product\\smAccessories",LCSProperties.get("productSeason.PRODUCTIONGROUP"));
			
		}else {
			productionGroup=getDisplayValues("productionGroup",
					this.reportBean.getSelectedProductionGroupOid(),
					"Product\\APD",LCSProperties.get("productSeason.PRODUCTIONGROUP"));	
		}
		//Updated for - 3.8.2.0 build - end
			
		// Add selected Production Group to header cell
		createHeaderCells(row,cellstyle,
				"Production Group",productionGroup,0,columnIndex);
		// Get the HSSFRow
		row = getHSSFRow(ws,rowId, columnIndex);

		rowId++;
		//Getting the display value for 
		//the selected technologist criteria
		String technologist="";			 
		// Get display value for selected Technologist
		if(FormatHelper.hasContent(this.reportBean.getSelectedProducctTechnologist()))
		{
			technologist = (String) this.reportBean.getIntTechnologistMap().
					get(this.reportBean.getSelectedProducctTechnologist());
		}
		// Add selected Technologist to header cell
		createHeaderCells(row,cellstyle,
				"Technologist",technologist,0,columnIndex);
		// Get the HSSFRow
		row = getHSSFRow(ws,rowId, columnIndex);
		LOGGER.debug("SMCareLabelReportHeader - row id: "+rowId);
		}
		catch (WTException e) {
			LOGGER.error("WTException in SMCareLabelReportHeader - " 
						+"createReportHeader: "
							+e.getMessage());
			e.printStackTrace();
		}
		
		return rowId;
	}

	/**
	 * @param ws - ws
	 * @param rowId - rowId
	 * @param columnIndex - columnIndex
	 * @return HSSFRow - HSSFRow
	 */
	private HSSFRow getHSSFRow(HSSFSheet ws, int rowId, int columnIndex) { 
		// get the row number
		HSSFRow row;
		if(columnIndex>0){
			row =ws.getRow(rowId);
		}else{
			row =ws.createRow(rowId);
		}
		return row;		
	}
	
	/**
	 * @param objectName - objectName
	 * @param multiSelectedCriteria - multiSelectedCriteria
	 * @param flexTypeName - flexTypeName
	 * @param strAttKey - strAttKey
	 * @return displayValue - displayValue
	 * @throws WTException - WTException
	 */
	private String getDisplayValues(String objectName, Collection<String> multiSelectedCriteria, 
			String flexTypeName, String strAttKey) throws WTException 
	{		
		String displayValue="";
		Map inputCriteria;		
		// Get display value for selected Project
		if("project".equalsIgnoreCase(objectName) 
				&& null != multiSelectedCriteria && !multiSelectedCriteria.isEmpty()){
			inputCriteria = this.reportBean.getIntProjectMap();
			for(String selectedAtt:multiSelectedCriteria){ 					
				if(FormatHelper.hasContent(displayValue)){ 						
					displayValue=displayValue+", "+
				(String)inputCriteria.get(selectedAtt); 
				}else{
					displayValue=(String) inputCriteria.get(selectedAtt);
				}
			}
		}		
		// Get display value for other selected criteria
		else if(null != multiSelectedCriteria && !multiSelectedCriteria.isEmpty()){
			FlexType flexType=FlexTypeCache.getFlexTypeFromPath(flexTypeName);
			FlexTypeAttribute fta = flexType.getAttribute(strAttKey);		
			AttributeValueList attList = fta.getAttValueList();
			// iterate multiSelectedCriteria, and form the selected criteria string
			for(String selectedAtt:multiSelectedCriteria){ 					
				if(FormatHelper.hasContent(displayValue)){ 
				// append if multiple values are selected
				displayValue=displayValue+
						", "+attList.getValue(
								selectedAtt,ClientContext.getContextLocale()); 
				}
				else{
				displayValue=attList.getValue(selectedAtt,ClientContext.getContextLocale());
				}
			}
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
			HSSFRow row, HSSFCellStyle cellstyle,String label,
			String rowText,int cellIndex,int columnIndex) {
		HSSFCell cell;
		HSSFRichTextString cellText;
		// set row style
		row.setRowStyle(cellstyle);
		// create cell
		cell = row.createCell(cellIndex+columnIndex);
		cell.setCellStyle(cellstyle);
		// Set cell value
		cellText = new HSSFRichTextString(label);
		cell.setCellValue(cellText);		
		cell = row.createCell(cellIndex+columnIndex+1); 
		// set cell style
		cell.setCellStyle(cellstyle);
		cellText = new HSSFRichTextString(rowText);
		cell.setCellValue(cellText);	
	}
}
