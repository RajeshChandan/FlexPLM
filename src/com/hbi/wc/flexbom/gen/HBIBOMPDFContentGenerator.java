/*
 * BOMPDFContentGenerator.java
 *
 * Created on November 13, 2006, 12:55 PM
 */

package com.hbi.wc.flexbom.gen;

import com.hbi.wc.flexbom.util.HBILabelBOMTechPackUtil;
import com.lcs.wc.client.ClientContext;
import com.lcs.wc.client.web.pdf.*;
import com.lcs.wc.client.web.PDFGeneratorHelper;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.gen.BomDataGenerator;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.PDFProductSpecificationGenerator2;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.util.*;

import wt.util.*;

/**
 *
 * @author  Chuck
 */
public abstract class HBIBOMPDFContentGenerator implements PDFContentCollection {

   private static final String CLASSNAME = HBIBOMPDFContentGenerator.class.getName();
   
   private static final boolean DEBUG = LCSProperties.getBoolean("com.lcs.wc.flexbom.gen.BOMPDFContentGenerator.verbose");
   private static final int DEBUG_LEVEL = Integer.parseInt(LCSProperties.get("com.lcs.wc.product.BOMPDFContentGenerator.verboseLevel", "1"));
   protected static final boolean BOM_ON_SINGLE_PAGE = LCSProperties.getBoolean("com.lcs.wc.product.PDFProductSpecificationGenerator.BOMonSinglePage");
   public static String NUM_SIZE_COLS = "NUM_SIZE_COLS";
   public static String NUM_SKU_COLS = "NUM_SKU_COLS";
   public static String TITLE_CELL = "TITLE_CELL";
   public static String BOM_PART = "BOM_PART";
   public static String BOM_SECTION_VIEWS = "BOM_SECTION_VIEWS";
   public Collection pageTitles = new ArrayList();
   public static PDFGeneratorHelper pgh = new PDFGeneratorHelper();
   public static String HIGH_LIGHT = "highLight";
   public static String PART_NAME = "partName";
   
   HashMap bomOptionsViews;// = new HashMap();
   public String reportKey;
   public String bomTypeId;
   
   /** Creates a new instance of BOMPDFContentGenerator */
   public HBIBOMPDFContentGenerator() {
   }
   /** gets an Element for insertion into a PDF Document
	* @param params A Map of parameters to pass to the Object.  This provides the means for the
	* calling class to have some "fore" knowledge of what implementations are being used
	* and pass appropriate parameters.
	* @param document The PDF Document which the content is going to be added to.  The document is
	* passed in order to provide additional information related to the Document itself
	* incase it is not provided in the params
	* @throws WTException For any error
	* @return an Element for insertion into a Document
	*/
   public abstract Collection getPDFContentCollection(Map params, Document document) throws WTException;

	  /** Generates a collection of PDF Elements containing the BOM.
	* @param bom  <code>Collection</code> the BOM data
	* @param columns <code>Collection</code> the table columns for the BOM
	* @param document <code>Document</code> the PDF Document the table will be added to.
	* @param params a <code>Map</code> of parameters for the report
	*
	* @return <code>Collection</code> of PDF Elements
	*/
   public Collection generatePDFPage(Collection bom, Collection columns, Document document, Map params)throws WTException {
       debug(1, CLASSNAME + "generatePDFPage()" + "\n BOM.size():  " + bom.size() + "\ncolumns.size():  " + columns.size());
	  Collection pdfData = new ArrayList();
	  PDFTableGenerator tg = null;
	  
	  tg = new PDFTableGenerator(document);
	  tg.cellClassLight = "RPT_TBL";
	  tg.cellClassDark = "RPT_TBD";
	  tg.tableSubHeaderClass = "RPT_HEADER";
	  tg.tableHeaderClass = "TABLE-HEADERTEXT";
	  tg.tableSectionHeaderClass = "TABLESECTIONHEADER";
	  tg.setTitleCell(getTitleCell(params));
	  
	  pdfData = tg.drawTables(bom, columns);
	  
	  return pdfData;
   }
   public void init() throws WTException{
       try{
           pageTitles = new ArrayList();
           bomOptionsViews = null;
           reportKey = "";
           bomTypeId = "";
          
           
       }catch(Exception e) {
           throw new WTException(e);
       }
       
   }
   /** Returns a collection of page title Strings.
	*  These page titles go in the upper left hand corner of the report page.
	*
	*@return <code>Collection</code> of page Titles (Strings)
	*/
   public Collection getPageTitles() {
	  return this.pageTitles;
   }
   /** Returns the text to use as the page title.
	*@param params a<code>Map</code> of parameters needed to generate the page title.
	*@return <code>String</code> the page title
	*/
   public String getPageTitleText(Map params) {
	  String title = "";
	  FlexBOMPart bomPart = (FlexBOMPart)params.get(HBIBOMPDFContentGenerator.BOM_PART);
	  
	  title = bomPart.getName() + " -- " + params.get(PDFProductSpecificationGenerator2.REPORT_NAME);
	  return title;
   }
   private static float titleBorderWidth = 0.1f;

   /** Returns the PdfPCell to use as a title for the BOM.
	*  This is a single cell directly above the BOM, and below the Header.
	*  This method calls getTitleCellLeftText(params), getTitleCellCenterText(params)
	*  and getTitleCellRightText(params) to get the text for the three sections of the
	*  cell.
	*
	*@param params <code>Map</code> the parameters need to create the cell
	*@return PdfPCell the titleCell
	*/
   public PdfPCell getTitleCell(Map params) throws WTException{
	  
	  String leftText = getTitleCellLeftText(params);
	  String centerText = getTitleCellCenterText(params);
	  String rightText = getTitleCellRightText(params);
	  String careCd = "";
	  String careCodeDescription = "";
		if(FormatHelper.hasContent(centerText) && centerText.equals("GPLabelBOM")) { 
			FlexSpecification specObj = (FlexSpecification) LCSQuery.findObjectById((String) params.get(PDFProductSpecificationGenerator2.SPEC_ID));
			Map<String, Object>  careCodeDataMap = new HBILabelBOMTechPackUtil().getCareCodeFromSourceToSea(params,specObj);
			careCd = (String) careCodeDataMap.get("name");
			careCodeDescription = (String) careCodeDataMap.get("hbiObjectDescription");
		}

	  try {
		 String bgCStyle = "HEADING3";
		 
		 Font font = pgh.getCellFont(bgCStyle, null, null);
		 PdfPCell leftCell = new PdfPCell(pgh.multiFontPara(leftText, font));
		 leftCell.setBackgroundColor(pgh.getCellBGColor(bgCStyle, null));
		 leftCell.setBorder(0);
         leftCell.setBorderWidthTop(titleBorderWidth);
         leftCell.setBorderWidthLeft(titleBorderWidth);
         leftCell.setBorderWidthBottom(titleBorderWidth);
		 
		 font = pgh.getCellFont(bgCStyle, "center", null);
		 PdfPCell centerCell = new PdfPCell(pgh.multiFontPara(centerText, font));
		 centerCell.setBackgroundColor(pgh.getCellBGColor(bgCStyle, null));
		 centerCell.setBorder(0);
         centerCell.setBorderWidthTop(titleBorderWidth);
         centerCell.setBorderWidthBottom(titleBorderWidth);
		 centerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		 
		 font = pgh.getCellFont(bgCStyle, "right", null);
		 PdfPCell rightCell = new PdfPCell(pgh.multiFontPara(rightText, font));
		 rightCell.setBackgroundColor(pgh.getCellBGColor(bgCStyle, null));
		 rightCell.setBorder(0);
         rightCell.setBorderWidthTop(titleBorderWidth);
         rightCell.setBorderWidthRight(titleBorderWidth);
         rightCell.setBorderWidthBottom(titleBorderWidth);
		 rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		 
		 //Resize the area for the three sections of the cell, based on the amount of text in each box
		 float outsidePer = 33.3f;
		 float centerPer = 33.3f;
		 float centerLen = 1;
		 if(FormatHelper.hasContent(centerText)){
			centerLen= centerText.length();
		 }
		 float  outsideLen = 1;
		 if(FormatHelper.hasContent(leftText)) {
			outsideLen = leftText.length();
		 }
		 if(FormatHelper.hasContent(rightText) && rightText.length() > outsideLen) {
			outsideLen = rightText.length();
		 }

		 centerPer = (centerLen/(centerLen + (outsideLen *2)))*100;
		 outsidePer = (100 - centerPer)/2;

		 PdfPTable titleTable = new PdfPTable(3);
		 float[] widths = {outsidePer, centerPer, outsidePer};
		 titleTable.setTotalWidth(widths);
		 titleTable.setWidthPercentage(100);	
		 // OOTB Tech pack requirement 12/07/18
		
		 if(centerText.equals("GPLabelBOM") && rightText.contains("Labelling")) {
			font = pgh.getCellFont("DISPLAYTEXT", "Left", null);
			PdfPCell careCode = new PdfPCell(pgh.multiFontPara("Care Code:  " + careCd, font));
			careCode.setColspan(1);
			careCode.setBorder(Rectangle.NO_BORDER);
			titleTable.addCell(careCode);
			
			PdfPCell careCodeDesc = new PdfPCell(pgh.multiFontPara("Care Code Description:  " + careCodeDescription, font));
			careCodeDesc.setColspan(2);
			careCodeDesc.setBorder(Rectangle.NO_BORDER);
			titleTable.addCell(careCodeDesc);
			
            PdfPCell blank = new PdfPCell();
            blank.setColspan(3);
            titleTable.addCell(blank);
		 }
		 //Ends here
		 titleTable.addCell(leftCell);
		 titleTable.addCell(centerCell);
		 titleTable.addCell(rightCell);
		 
		 PdfPCell titleCell = new PdfPCell(titleTable);
		 titleCell.setBorder(0);		 
		 return titleCell;
	  }
	  catch(Exception e){
		 throw new WTException(e);
	  }
   }
   /** Returns the text for the left side of the title cell
	* @param params <code>Map</code> the parameters for determining the cell text
	* @return String the text of the left side of the title cell.
	*/
   protected String getTitleCellLeftText(Map params) throws WTException{
	  FlexBOMPart bomPart = (FlexBOMPart)params.get(HBIBOMPDFContentGenerator.BOM_PART);
	  String title=bomPart.getName();
	 
	  if(params.get("multi_source")!=null) {
		  LCSSourcingConfig sourceM =(LCSSourcingConfig)LCSQuery.findObjectById((String) params.get("multi_source")) ;
		  String patternSpecInSource="";
			if(sourceM.getValue("hbiSpecificationPattern")!=null){
				patternSpecInSource = (String)sourceM.getValue("hbiSpecificationPattern");
			}
			String seasonInSourceStr = "";
			if(sourceM.getValue("hbiDvlpSeason")!=null){
				LCSLifecycleManaged seasonInSource = (LCSLifecycleManaged)sourceM.getValue("hbiDvlpSeason");
				
				seasonInSourceStr=seasonInSource.getName();
			}
			String colorwayInSource="";
			if(sourceM.getValue("hbiColorwayGroupingName")!=null){
				colorwayInSource = (String)sourceM.getValue("hbiColorwayGroupingName");
			}
			String colorwayGroupingInSource="";
			if(sourceM.getValue("hbiColorwayGrouping")!=null){
				LCSLifecycleManaged colorwayGroupingBO = (LCSLifecycleManaged)sourceM.getValue("hbiColorwayGrouping");
				
				colorwayGroupingInSource = (String)colorwayGroupingBO.getName();
			}
			
			String sourceFullName = patternSpecInSource+"-"+seasonInSourceStr+"-"+colorwayGroupingInSource+"-"+colorwayInSource;
			
		  title = bomPart.getName() +" | "+ sourceFullName;
	  }
   	  LCSLog.debug("");
	  return title;
   }
   
   /** Returns the text for the center part of the title cell
	* @param params <code>Map</code> the parameters for determining the cell text
	* @return String the text of the center part of the title cell.
	*/
   public String getTitleCellCenterText(Map params) throws WTException {
	  return (String)params.get(PDFProductSpecificationGenerator2.REPORT_NAME);
   }
   
   /** Returns the text for the right side of the title cell
	* @param params <code>Map</code> the parameters for determining the cell text
	* @return String the text of the right side of the title cell.
	*/
   protected String getTitleCellRightText(Map params) throws WTException {
	  FlexBOMPart bomPart = (FlexBOMPart)params.get(HBIBOMPDFContentGenerator.BOM_PART);
	  FlexTypeAttribute att = bomPart.getFlexType().getAttribute("section");
	  StringBuffer buffer = new StringBuffer();
	  buffer.append(att.getAttDisplay() + ":  ");
	  Locale  locale = ClientContext.getContext().getResolvedLocale();//.getContextLocale();

	  buffer.append(att.getAttValueList().getValue((String)params.get(BomDataGenerator.SECTION), locale));
	   return buffer.toString();
   }
   
   /**This method takes a collection of objects, and creates a collection of collections(which are no larger than maxPerPage).
	*
	* @param items a Collection of objects
	* @param maxPerPage	int The max number of objects per collection
	*
	* @return Collection   a Collection of Collection(no larger than maxPerPage)
	*/
   public static Collection splitItems(Collection items, int maxPerPage) {
	  debug("BOMPDFContentGenerator.splitItems:  items-" + items + "  maxPerPage:  " + maxPerPage);
	  Collection coll = new ArrayList();
	  if((maxPerPage == 0 ) || (maxPerPage >= items.size())) {
		 coll.add(items);
	  } else {
		 int count = 0;
		 ArrayList tempColl = new ArrayList();
		 Object next;
		 Iterator it = items.iterator();
		 while(it.hasNext()) {
			next = it.next();
			count++;
			if(count > maxPerPage) {
			   //add array to collection of arrays, and create new temp array
			   coll.add(tempColl);
			   tempColl = new ArrayList();
			   count = 1;
			}
			tempColl.add(next);
		 }
		 //Add the last array list to the collection
		 coll.add(tempColl);
	  }
	  debug("BOMPDFContentGenerator.splitItems--returning " + coll);
	  return coll;
	  
   }
   
   /** Puts the viewId of that section of the bom into the params map.
	* @params params <code>Map</code> the parameters for generating the report.
	*/
   public void setSectionViewId(Map params) {
	  if (bomOptionsViews == null) {
		 bomOptionsViews =  (HashMap)params.get(BOM_SECTION_VIEWS);
		 if(bomOptionsViews == null){
			bomOptionsViews = new HashMap();
		 }
	  }
	  debug("bomOptionsViews:  " + bomOptionsViews.keySet());
	  if(!FormatHelper.hasContent(this.reportKey)){
		 this.reportKey = (String)params.get(PDFProductSpecificationGenerator2.REPORT_KEY) ;
		 debug("setting reportKey:  " + reportKey);
	  }
	  if(!FormatHelper.hasContent(this.bomTypeId)){
		 com.lcs.wc.flextype.FlexType bomType = ((FlexBOMPart)params.get(HBIBOMPDFContentGenerator.BOM_PART)).getFlexType();
		 this.bomTypeId = FormatHelper.getObjectId(bomType) ;
		 debug("setting bomTypeId:  " + bomTypeId);
	  }

	  String viewId = (String)bomOptionsViews.get(this.reportKey+ this.bomTypeId + (String)params.get(BomDataGenerator.SECTION));
	  debug("section:  " + params.get(BomDataGenerator.SECTION)+  "--viewId:  " + viewId);
	  params.put(BomDataGenerator.VIEW_ID, viewId);
	  
   }
    /////////////////////////////////////////////////////////////////////////////
   public static void debug(String msg){debug(msg, 1); }
   public static void debug(int i, String msg){debug(msg, i); }
   public static void debug(String msg, int i){
	  if(DEBUG && i <= DEBUG_LEVEL) System.out.println(msg);
   }
   
}
