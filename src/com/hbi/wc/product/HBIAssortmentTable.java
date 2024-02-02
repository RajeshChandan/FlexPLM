package com.hbi.wc.product;

import java.util.Collection;
import java.util.Map;
import java.util.Vector;
import java.util.Date;

import com.lcs.wc.client.web.PDFGeneratorHelper;
import com.lcs.wc.client.web.pdf.PDFContent;
import com.lcs.wc.client.web.pdf.PDFTableGenerator;

import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import wt.fc.WTObject;
//import wt.part.WTPartMaster;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.season.SeasonProductLocator;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import wt.util.WTException;


public class HBIAssortmentTable implements PDFContent
{
  public static String PRODUCT_ID = "PRODUCT_ID";
  public static String SPEC_ID = "SPEC_ID";
  public static String SEASONMASTER_ID = "SEASONMASTER_ID";
  public static final String BASIC_CUT_AND_SEW_PATTERN = "BASIC CUT & SEW - PATTERN";
  public static final String BASIC_CUT_AND_SEW_GARMENT = "BASIC CUT & SEW - GARMENT";
  public static final String BASIC_CUT_AND_SEW_SELLING = "BASIC CUT & SEW - SELLING";
  public static PDFGeneratorHelper pgh = new PDFGeneratorHelper();
  private static String fontSize = "8";

  public Element getPDFContent(Map paramMap, Document paramDocument) throws WTException
  {
    PDFTableGenerator tg = null; 
    FlexType assortmentMoaType = null;
	String delivery = "";
	String revisionCode = "";
	String sellingColor = "";
	String packagingStyle = "";
	String packagingColor = "";
	String packOrder = "";
	String manufStyle = "";
	String garmentProduct = "";
	String colorway = "";
	String freeGarment = "";
	String planningPerc = "";
	String comments = "";
	String routing = "";
	
	Collection<LCSMOAObject> assortmentTableCollectionObj = new Vector() ;
	
	LCSSeasonMaster seasonMaster = null;
	LCSSeason season = null;
	LCSSeasonProductLink spLink=null;
	
    try
    {
      WTObject obj = (WTObject)LCSProductQuery.findObjectById((String)paramMap.get(PRODUCT_ID));
      if (!(obj instanceof LCSProduct)) 
	  {
        throw new WTException("Can not use PDFProductSpecification on a non-LCSProduct - " + obj);
      }

      LCSProduct product = (LCSProduct)obj;
	  float[] columnWidths = {9f,7f,15f,15f,15f,7f,12f,25f,15f,15f,10f,20f,10f};
      PdfPTable assortmentDataTable;
      
      if (FormatHelper.hasContent((String)paramMap.get(SEASONMASTER_ID))) 
	  {
        seasonMaster = (LCSSeasonMaster)LCSQuery.findObjectById((String)paramMap.get(SEASONMASTER_ID));
        season = (LCSSeason)VersionHelper.latestIterationOf(seasonMaster);
		String seasonName = (String)season.getValue("seasonName");
		spLink = LCSSeasonQuery.findSeasonProductLink(product, season);
		//System.out.println(" <<< Season Producy Link >>> " +spLink.getIdentity());	
	 }
	  
	  if (spLink != null)
	  {
			Double prodSeasonRevId=spLink.getProductSeasonRevId();
			int prodSeasonRevID=prodSeasonRevId.intValue();
			LCSProduct prod=(LCSProduct)LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+prodSeasonRevID);
			FlexType flextype = spLink.getFlexType();
			FlexTypeAttribute colorwayPositionAtt = flextype.getAttribute("hbiAssortmentTable");
			assortmentTableCollectionObj = LCSMOAObjectQuery.findMOACollection(prod,colorwayPositionAtt);
	  }
	  
     // System.out.println(" <<< assortmentTableCollectionObj >>> " +assortmentTableCollectionObj);	  

	  tg = new PDFTableGenerator(paramDocument);
      tg.cellClassLight = "RPT_TBL";
      tg.cellClassDark = "RPT_TBD";
      tg.tableSubHeaderClass = "RPT_HEADER";
      tg.tableHeaderClass = "TABLE-HEADERTEXT";

      PdfPCell cell = new PdfPCell(pgh.multiFontPara("Manufacturing Routing", pgh.getCellFont("FORMLABEL", null, null)));
      cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor("BORDERED_BLOCK", null));
      cell.setBorderColor(PDFGeneratorHelper.getCellBGColor("BORDERED_BLOCK", null));
      cell.setBorderWidth(0.0F);
      cell.setHorizontalAlignment(10);
      cell.setVerticalAlignment(16);
      tg.setTitleCell(cell);

      PdfPTable mainTable = new PdfPTable(1);
      PdfPTable assortmentHeaderAttTable = new PdfPTable(13);
	  assortmentHeaderAttTable.setWidths(columnWidths);

      assortmentMoaType = FlexTypeCache.getFlexTypeFromPath("Multi-Object\\Assortment Table");
	  
  	  cell = createHeaderCell(assortmentMoaType.getAttribute("hbiSeasonDelivery").getAttDisplay(true));
	  assortmentHeaderAttTable.addCell(cell);

      cell = createHeaderCell(assortmentMoaType.getAttribute("hbiRevisionCode").getAttDisplay(true));
      assortmentHeaderAttTable.addCell(cell);
	  
      cell = createHeaderCell(assortmentMoaType.getAttribute("hbiSellingColor").getAttDisplay(true));
      assortmentHeaderAttTable.addCell(cell);

      cell = createHeaderCell(assortmentMoaType.getAttribute("hbiPackagingStyleNew").getAttDisplay(true));
      assortmentHeaderAttTable.addCell(cell);
	  
      cell = createHeaderCell(assortmentMoaType.getAttribute("hbiPackagingColor").getAttDisplay(true));
      assortmentHeaderAttTable.addCell(cell);
	  
      cell = createHeaderCell(assortmentMoaType.getAttribute("hbiOrder").getAttDisplay(true));
      assortmentHeaderAttTable.addCell(cell);
	  
      cell = createHeaderCell(assortmentMoaType.getAttribute("hbiManufStyle").getAttDisplay(true));
      assortmentHeaderAttTable.addCell(cell);
	  
	  cell = createHeaderCell(assortmentMoaType.getAttribute("hbiGarmentProduct").getAttDisplay(true));
      assortmentHeaderAttTable.addCell(cell);
	  
	  cell = createHeaderCell(assortmentMoaType.getAttribute("hbiColorway").getAttDisplay(true));
      assortmentHeaderAttTable.addCell(cell);
	  
	  cell = createHeaderCell(assortmentMoaType.getAttribute("hbiFreeGarment").getAttDisplay(true));
      assortmentHeaderAttTable.addCell(cell);
	  
	  cell = createHeaderCell(assortmentMoaType.getAttribute("hbiPlanningPercentage").getAttDisplay(true));
      assortmentHeaderAttTable.addCell(cell);
	  
	  cell = createHeaderCell(assortmentMoaType.getAttribute("hbiCommentsAssortment").getAttDisplay(true));
      assortmentHeaderAttTable.addCell(cell);
	  
	  cell = createHeaderCell(assortmentMoaType.getAttribute("hbiRouting").getAttDisplay(true));
      assortmentHeaderAttTable.addCell(cell);

      mainTable.addCell(new PdfPCell(assortmentHeaderAttTable));
      for(LCSMOAObject moaObject : assortmentTableCollectionObj)
      {
		assortmentDataTable = new PdfPTable(13);
	    assortmentDataTable.setWidths(columnWidths);
		
        Object deliveryObj = moaObject.getValue("hbiSeasonDelivery");
		delivery = "";
		if(deliveryObj != null)
		delivery	= deliveryObj.toString();
		cell = createDataCell(delivery);
        assortmentDataTable.addCell(cell);
		
		double hbiRevisionCodeValue = (Double)moaObject.getValue("hbiRevisionCode");
		int intRevisionCodeValue = (int) hbiRevisionCodeValue;
		revisionCode = Integer.toString(intRevisionCodeValue);
		cell = createDataCell(revisionCode);
		assortmentDataTable.addCell(cell);
		
		Object sellingColorObj = moaObject.getValue("hbiSellingColor");
		sellingColor = "";
		if(sellingColorObj != null)
		sellingColor = sellingColorObj.toString();
		cell = createDataCell(sellingColor);
        assortmentDataTable.addCell(cell);
			
		LCSLifecycleManaged packagingStyleObj = (LCSLifecycleManaged)moaObject.getValue("hbiPackagingStyleNew");
		packagingStyle = "";
		if(packagingStyleObj != null)
		packagingStyle = (String)packagingStyleObj.getName();
		cell = createDataCell(packagingStyle);
        assortmentDataTable.addCell(cell);
		
		Object packagingColorObj = moaObject.getValue("hbiPackagingColor");
		packagingColor = "";
		if(packagingColorObj != null)
		packagingColor = packagingColorObj.toString();
		cell = createDataCell(packagingColor);
        assortmentDataTable.addCell(cell);
		
		String packOrderValue = (String) moaObject.getValue("hbiOrder");
		packOrder = moaObject.getFlexType().getAttribute("hbiOrder").getAttValueList().getValue(packOrderValue,null);
		cell = createDataCell(packOrder);
		assortmentDataTable.addCell(cell);
		
		LCSLifecycleManaged manufStyleObj = (LCSLifecycleManaged)moaObject.getValue("hbiManufStyle");
		manufStyle = "";
		if(manufStyleObj != null)
		manufStyle = (String)manufStyleObj.getName();
		cell = createDataCell(manufStyle);
        assortmentDataTable.addCell(cell);
		
		LCSProduct garmentProductObj = (LCSProduct)moaObject.getValue("hbiGarmentProduct");
		garmentProduct = "";
		if(garmentProductObj != null)
		garmentProduct = (String)garmentProductObj.getName();
		cell = createDataCell(garmentProduct);
        assortmentDataTable.addCell(cell);
		
		LCSSKU colorwayObj = (LCSSKU)moaObject.getValue("hbiColorway");
		colorway = "";
		if(colorwayObj != null)
		colorway = (String)colorwayObj.getName();
		cell = createDataCell(colorway);
        assortmentDataTable.addCell(cell);
		
		String freeGarmentValue = (String) moaObject.getValue("hbiFreeGarment");
		freeGarment = moaObject.getFlexType().getAttribute("hbiFreeGarment").getAttValueList().getValue(freeGarmentValue,null);
		cell = createDataCell(freeGarment);
		assortmentDataTable.addCell(cell);
		
		double planningPercValue = (Double)moaObject.getValue("hbiPlanningPercentage");
		planningPerc = Double.toString(planningPercValue);
		cell = createDataCell(planningPerc);
		assortmentDataTable.addCell(cell);
		
		Object commentsObj = moaObject.getValue("hbiCommentsAssortment");
		comments = "";
		if(commentsObj != null)
		comments = commentsObj.toString();
		cell = createDataCell(comments);
        assortmentDataTable.addCell(cell);
		
		String routingValue = (String) moaObject.getValue("hbiRouting");
		routing = moaObject.getFlexType().getAttribute("hbiRouting").getAttValueList().getValue(routingValue,null);
		cell = createDataCell(routing);
		assortmentDataTable.addCell(cell);

        mainTable.addCell(new PdfPCell(assortmentDataTable));
      }

      return mainTable;
    }
    catch (Exception e) 
	{
		throw new WTException(e);
	}
  }

	/* *returns the PdfPCell containing header label for  MOA table for the specification
     * @param label
     * @return  */ 
	private PdfPCell createHeaderCell(String label)
	{
	    
		Font font = pgh.getCellFont("RPT_HEADER","Left", fontSize);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(label, font));
	    pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_HEADER", null));           
	    return pdfpcell;
    }

	/* *returns the PdfPCell containing data for  MOA table for the specification
     * @param label
     * @return 
	 */ 
	private  PdfPCell createDataCell(String data)
	{

	    Font font = pgh.getCellFont("RPT_TBD","Left", fontSize);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(data, font));
        pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_TBD", null));
	    return pdfpcell;
    }
}