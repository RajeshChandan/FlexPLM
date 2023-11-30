package com.hbi.wc.product;

import com.hbi.wc.flexbom.util.HBISpecificationPDFGenUtil;
import com.lcs.wc.client.web.PDFGeneratorHelper;
import com.lcs.wc.client.web.pdf.PDFContent;
import com.lcs.wc.client.web.pdf.PDFTableGenerator;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.SortHelper;
import com.lcs.wc.util.VersionHelper;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.Date;
import java.util.HashMap;
import java.lang.Object;
import wt.fc.WTObject;
//import wt.part.WTPartMaster;
import wt.util.WTException;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.SearchResults;

import wt.org.WTUser;

public class HBIRevisionTable implements PDFContent
{
  public static String PRODUCT_ID = "PRODUCT_ID";
  public static String SPEC_ID = "SPEC_ID";
  public static String SEASONMASTER_ID = "SEASONMASTER_ID";
  public static final String BASIC_CUT_AND_SEW_PATTERN = "BASIC CUT & SEW - PATTERN";
  public static final String BASIC_CUT_AND_SEW_GARMENT = "BASIC CUT & SEW - GARMENT";
  public static final String BASIC_CUT_AND_SEW_SELLING = "BASIC CUT & SEW - SELLING";
	public static boolean DEBUG = LCSProperties
			.getBoolean("com.lcs.wc.product.PDFProductSpecificationGenerator2.verbose");

	private static final int DEBUG_LEVEL = Integer
			.parseInt(LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationGenerator2.verboseLevel", "1"));  
	private static final int REVISION_CNT = Integer
			.parseInt(LCSProperties.get("com.lcs.wc.product.HBIRevisionTable.rowcount", "15"));  	
  public static PDFGeneratorHelper pgh = new PDFGeneratorHelper();
  private static String fontSize = "8";

  public Element getPDFContent(Map paramMap, Document paramDocument)
    throws WTException
  {
    PDFTableGenerator tg = null; 
    String revisionMOATypeName = "Multi-Object\\Revision Attributes"; 
	FlexType revisionMoaType = null;
	String hbiComments = " ";
	String hbiDateModified = " ";
	String hbiLastUsed = " ";
	String hbiRevision1 = "";
	String hbiRevision2 = "";
	String hbiRevision3 = "";
	String hbiSpecObjStr = "";
	String specMOAValue = "";

	Collection revisionColl = new Vector() ;
	//SPL
	LCSSeasonMaster seasonMaster = null;
	LCSSeason season = null;
	LCSSourceToSeasonLink sslink = null;
	LCSSourcingConfig sConfig = null;
	LCSSeasonProductLink spLink=null;
	LCSLifecycleManaged revision1 = null;
	LCSLifecycleManaged revision2 = null;
	LCSLifecycleManaged revision3 = null;
	Object hbiSpecObj = null;

    try
    {
      WTObject obj = (WTObject)LCSProductQuery.findObjectById((String)paramMap.get(PRODUCT_ID));
      if (!(obj instanceof LCSProduct)) {
        throw new WTException("Can not use PDFProductSpecification on a non-LCSProduct - " + obj);
      }

      LCSProduct product = (LCSProduct)obj;
	  float[] columnWidths = {3f, 13f, 13f, 13f, 32f, 13f, 13f};
      PdfPTable revisionDataTable;
      LCSMOAObject moaObject;
      if (FormatHelper.hasContent((String)paramMap.get(SEASONMASTER_ID))) {
        seasonMaster = (LCSSeasonMaster)LCSQuery.findObjectById((String)paramMap.get(SEASONMASTER_ID));
        season = (LCSSeason)VersionHelper.latestIterationOf(seasonMaster);

        String seasonName = (String)season.getValue("seasonName");

        spLink = LCSSeasonQuery.findSeasonProductLink(product, season);
	 }
	 
	  FlexType flextype = product.getFlexType();
	  FlexTypeAttribute revatt = ((FlexType)flextype).getAttribute("hbiRevisionAttributes");
	  revisionColl = LCSMOAObjectQuery.findMOACollection(product, revatt);		

      Iterator revisionIter  = ((Collection)revisionColl).iterator();

      tg = new PDFTableGenerator(paramDocument);
      tg.cellClassLight = "RPT_TBL";
      tg.cellClassDark = "RPT_TBD";
      tg.tableSubHeaderClass = "RPT_HEADER";
      tg.tableHeaderClass = "TABLE-HEADERTEXT";

      PdfPCell cell = new PdfPCell(pgh.multiFontPara("Manufacturing Routing", pgh.getCellFont("FORMLABEL", null, null)));
      cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor("BORDERED_BLOCK", null));
      cell.setBorderColor(PDFGeneratorHelper.getCellBGColor("BORDERED_BLOCK", null));
      cell.setBorderWidth(0.0F);
      cell.setHorizontalAlignment(0);
      cell.setVerticalAlignment(6);
      tg.setTitleCell(cell);

      PdfPTable mainTable = new PdfPTable(1);
      PdfPTable revisionHeaderAttTable = new PdfPTable(7);
	  revisionHeaderAttTable.setWidths(columnWidths);

      revisionMoaType = FlexTypeCache.getFlexTypeFromPath("Multi-Object\\Revision Attributes");
  	  if((product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_PATTERN) != -1) || 
			(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_GARMENT) != -1) || 
			(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_SELLING) != -1))
	  {
			cell = createHeaderCell(revisionMoaType.getAttribute("hbiSpec").getAttDisplay(true));
			revisionHeaderAttTable.addCell(cell);
	  }
      cell = createHeaderCell(revisionMoaType.getAttribute("hbiRevisionBusinessObject").getAttDisplay(true));
      revisionHeaderAttTable.addCell(cell);
      cell = createHeaderCell(revisionMoaType.getAttribute("hbiRevision2").getAttDisplay(true));
      revisionHeaderAttTable.addCell(cell);

      cell = createHeaderCell(revisionMoaType.getAttribute("hbiRevision3").getAttDisplay(true));
      revisionHeaderAttTable.addCell(cell);
      cell = createHeaderCell(revisionMoaType.getAttribute("hbiComments").getAttDisplay(true));
      revisionHeaderAttTable.addCell(cell);
      cell = createHeaderCell(revisionMoaType.getAttribute("hbiDateModified").getAttDisplay(true));
      revisionHeaderAttTable.addCell(cell);
      cell = createHeaderCell(revisionMoaType.getAttribute("hbiLastUsed").getAttDisplay(true));
      revisionHeaderAttTable.addCell(cell);

      mainTable.addCell(new PdfPCell(revisionHeaderAttTable));
      while (((Iterator)revisionIter).hasNext())
      {
		hbiDateModified = "";
		hbiRevision1 = "";
		hbiRevision2 = "";
		hbiRevision3 = "";
		specMOAValue = "";
		
        revisionDataTable = new PdfPTable(7);
	    revisionDataTable.setWidths(columnWidths);
        moaObject = (LCSMOAObject)((Iterator)revisionIter).next(); 
	  //Added BASIC_CUT_AND_SEW_SELLING type to print Revision Table on Selling Product Spec as  well.
 	  if((product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_PATTERN) != -1) || 
			(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_GARMENT) != -1) || 
			(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_SELLING) != -1))
		{
			Collection specKeyValue =null;
			Collection specKey =null;    	
			hbiSpecObj = moaObject.getValue("hbiSpec");
			if(hbiSpecObj != null){
				hbiSpecObjStr = hbiSpecObj.toString();
				if(hbiSpecObjStr!=null)
				{
					specKeyValue =moaObject.getFlexType().getAttribute("hbiSpec").getAttValueList().getSelectableValues(null,true);
					specKey =moaObject.getFlexType().getAttribute("hbiSpec").getAttValueList().getSelectableKeys(null,true);
				}
				Iterator specKeyIter = specKey.iterator();
				Iterator specKeyValueIter = specKeyValue.iterator();
				HashMap<String,String> specHashKeys = new HashMap<String,String>();
				while(specKeyIter.hasNext() && specKeyValueIter.hasNext())
				{    		
					String keyVal1 =specKeyIter.next().toString().trim();
					String tmpVal1= specKeyValueIter.next().toString().trim();
					specHashKeys.put(keyVal1, tmpVal1);    		
				}
				specMOAValue = specHashKeys.get(hbiSpecObjStr);
			}
			cell = createDataCell(specMOAValue);
			revisionDataTable.addCell(cell);
		}
		revision1 = (LCSLifecycleManaged)moaObject.getValue("hbiRevisionBusinessObject");
		if(revision1 != null){
			hbiRevision1 = (String)revision1.getName();
			}
        cell = createDataCell(hbiRevision1);
        revisionDataTable.addCell(cell);
		
		revision2 = (LCSLifecycleManaged)moaObject.getValue("hbiRevision2");
		if(revision2 != null){
			hbiRevision2 = (String)revision2.getName();
		}
        cell = createDataCell(hbiRevision2);
        revisionDataTable.addCell(cell);
	
		revision3 = (LCSLifecycleManaged)moaObject.getValue("hbiRevision3");
		if(revision3 != null){
			hbiRevision3 = (String)revision3.getName();
			}
        cell = createDataCell(hbiRevision3);
        revisionDataTable.addCell(cell);
		
		Object comments	= moaObject.getValue("hbiComments");
		hbiComments="";//For CA#35727-14-for the fixing the issues of duplication of comments in pdf
		if(comments != null){
			hbiComments	= comments.toString();
		}
        cell = createDataCell(hbiComments);
        revisionDataTable.addCell(cell);
		
		Date pModifyDate = (Date)moaObject.getValue("hbiDateModified");
		if(pModifyDate != null){
			hbiDateModified = FormatHelper.applyFormat(pModifyDate, "MM/dd/yyyy");
		}
        cell = createDataCell(hbiDateModified);
        revisionDataTable.addCell(cell);
		
		FlexObject flexobj  = (FlexObject)moaObject.getValue("hbiLastUsed");
		hbiLastUsed = "";
		if(flexobj != null)
		{
			//String userId = (String)flexobj.get("OID");
			//WTUser user = (WTUser) LCSQuery.findObjectById("wt.org.WTUser:" + userId);
			hbiLastUsed = (String) flexobj.get("FULLNAME");
			
		}
        cell = createDataCell(hbiLastUsed);
        revisionDataTable.addCell(cell);		
		

        /*AttributeValueList productChangeAttList = moaObject.getFlexType().getAttribute("hbiProductChanges").getAttValueList();
        String productchanges = productChangeAttList.getValue((String)moaObject.getValue("hbiProductChanges"), null);
        cell = createDataCell(productchanges);
        revisionDataTable.addCell(cell);

        AttributeValueList routingChangeAttList = moaObject.getFlexType().getAttribute("hbiRoutingChanges").getAttValueList();
        String c = routingChangeAttList.getValue((String)moaObject.getValue("hbiRoutingChanges"), null);
        cell = createDataCell(productchanges);
        revisionDataTable.addCell(cell);

        AttributeValueList measurementChangeAttList = moaObject.getFlexType().getAttribute("hbiMeasurementChanges").getAttValueList();
        String measurementchanges = measurementChangeAttList.getValue((String)moaObject.getValue("hbiMeasurementChanges"), null);
        cell = createDataCell(measurementchanges);
        revisionDataTable.addCell(cell);

        AttributeValueList constructionChangeAttList = moaObject.getFlexType().getAttribute("hbiConstructionChanges").getAttValueList();
        String constructionchanges = constructionChangeAttList.getValue((String)moaObject.getValue("hbiConstructionChanges"), null);
        cell = createDataCell(constructionchanges);
        revisionDataTable.addCell(cell);

        AttributeValueList colorwayChangeAttList = moaObject.getFlexType().getAttribute("hbiColorwayChanges").getAttValueList();
        String colorwaychanges = colorwayChangeAttList.getValue((String)moaObject.getValue("hbiColorwayChanges"), null);
        cell = createDataCell(colorwaychanges);
        revisionDataTable.addCell(cell);

        AttributeValueList cutPartChangeAttList = moaObject.getFlexType().getAttribute("hbiCutPartChanges").getAttValueList();
        String cutPartChanges = cutPartChangeAttList.getValue((String)moaObject.getValue("hbiCutPartChanges"), null);
        cell = createDataCell(cutPartChanges);
        revisionDataTable.addCell(cell);

        AttributeValueList garmentChangeAttList = moaObject.getFlexType().getAttribute("hbiGarmentChanges").getAttValueList();
        String garmentChanges = garmentChangeAttList.getValue((String)moaObject.getValue("hbiGarmentChanges"), null);
        cell = createDataCell(garmentChanges);
        revisionDataTable.addCell(cell);

        AttributeValueList labelChangeAttList = moaObject.getFlexType().getAttribute("hbiLabelChanges").getAttValueList();
        String labelChanges = labelChangeAttList.getValue((String)moaObject.getValue("hbiLabelChanges"), null);
        cell = createDataCell(labelChanges);
        revisionDataTable.addCell(cell);

        AttributeValueList packingChangeAttList = moaObject.getFlexType().getAttribute("hbiPackagingChanges").getAttValueList();
        String packingChanges = packingChangeAttList.getValue((String)moaObject.getValue("hbiPackagingChanges"), null);
        cell = createDataCell(packingChanges);
        revisionDataTable.addCell(cell);

        AttributeValueList casingChangeAttList = moaObject.getFlexType().getAttribute("hbiCasingChanges").getAttValueList();
        String casingChanges = casingChangeAttList.getValue((String)moaObject.getValue("hbiCasingChanges"), null);
        cell = createDataCell(casingChanges);
        revisionDataTable.addCell(cell);*/

        mainTable.addCell(new PdfPCell(revisionDataTable));
      }

      return mainTable;
    }
    catch (Exception e) {
    throw new WTException(e);}
  }

  /**
 * @param paramMap
 * @param paramDocument
 * @return Element
 * @throws WTException
 * @author UST 
 */
  @SuppressWarnings("unchecked")
public Element getGarmentProductPDFContent(Map paramMap, Document paramDocument)
		    throws WTException
		  {
	    PDFTableGenerator tg = null; 
	    String revisionMOATypeName = "Multi-Object\\Revision Attributes"; 
		FlexType revisionMoaType = null;
		String hbiComments = " ";
		String hbiDateModified = " ";
		String hbiLastUsed = " ";
		String hbiRevision1 = "";
		String hbiRevision2 = "";
		String hbiRevision3 = "";
		String hbiSpecObjStr = "";
		String hbiProdType = "";
		String specMOAValue = "";

		Collection revisionColl = new Vector() ;
		Collection previsionColl = new Vector() ;
		//SPL
		LCSSeasonMaster seasonMaster = null;
		LCSSeason season = null;
		LCSSourceToSeasonLink sslink = null;
		LCSSourcingConfig sConfig = null;
		LCSSeasonProductLink spLink=null;
		LCSLifecycleManaged revision1 = null;
		LCSLifecycleManaged revision2 = null;
		LCSLifecycleManaged revision3 = null;
		Object hbiSpecObj = null;
		int rowcnt = 0;

		try
		    {
		        WTObject obj = (WTObject)LCSProductQuery.findObjectById((String)paramMap.get(PRODUCT_ID));
		        if (!(obj instanceof LCSProduct)) {
		          throw new WTException("Can not use PDFProductSpecification on a non-LCSProduct - " + obj);
		        }
	
		        LCSProduct product = (LCSProduct)obj;		    	
		        float[] columnWidths = {6f, 4f, 12f, 12f, 12f, 30f, 12f, 12f};
		        PdfPTable revisionDataTable;
		        LCSMOAObject moaObject;
		        
		        FlexSpecification spec = (FlexSpecification) LCSQuery.findObjectById(paramMap.get(SPEC_ID).toString());
		        int specNum = FormatHelper.hasContent(spec.getValue("number").toString())?Integer.parseInt(spec.getValue("number").toString()):0;
		        int pspecNum = 0;
		        
		        LCSProduct linkedPatternProduct = findPatternProdLinkedToGP(product);
		    		  
		        FlexType flextype = product.getFlexType();
		        FlexTypeAttribute revatt = ((FlexType)flextype).getAttribute("hbiRevisionAttributes");
		        //revisionColl = LCSMOAObjectQuery.findMOACollection(product, revatt);		
		        SearchResults results = LCSMOAObjectQuery.findMOACollectionData(product, revatt);
		        revisionColl = results.getResults();
		        debug("Garment revision Collection " + revisionColl.size());
		        String pspecId = HBISpecificationPDFGenUtil.getParentSpecId(spec);
		        if(revisionColl.size() > 0) {
			        if(FormatHelper.hasContent(pspecId)) {
				        FlexSpecification pspec = (FlexSpecification) LCSQuery.findObjectById(pspecId);
				        
						  if(linkedPatternProduct!=null && pspec!=null) {
							  FlexType pflextype = linkedPatternProduct.getFlexType();
							  FlexTypeAttribute prevatt = ((FlexType)pflextype).getAttribute("hbiRevisionAttributes");
							  //previsionColl = LCSMOAObjectQuery.findMOACollection(linkedPatternProduct, prevatt);
							  SearchResults presults = LCSMOAObjectQuery.findMOACollectionData(linkedPatternProduct, prevatt);
							  previsionColl = presults.getResults();
							  debug("Pattern revision Collection " + previsionColl.size());
							  if(previsionColl.size() > 0) {
								  revisionColl.addAll(previsionColl);
							  }
							  
							  pspecNum = FormatHelper.hasContent(pspec.getValue("number").toString())? Integer.parseInt(pspec.getValue("number").toString()) : 0;
						  }
			        }
			        
				  debug("Garment revision Collection  - AFTER" + revisionColl.size());
				  //Code changes by Wipro Upgrade Team
				  //revisionColl = SortHelper.sortFlexObjects(revisionColl, "LCSMOAOBJECT.DATE1:DESC");
				  revisionColl = SortHelper.sortFlexObjects(revisionColl, "LCSMOAOBJECT.ptc_tms_1typeInfoLCSMOAObjec:DESC");
				  revisionColl = LCSQuery.getObjectsFromResults(revisionColl, "OR:com.lcs.wc.moa.LCSMOAObject:", "LCSMOAOBJECT.IDA2A2");
		        } //revisionColl has content
			  
		      Iterator revisionIter  = ((Collection)revisionColl).iterator();

		      tg = new PDFTableGenerator(paramDocument);
		      tg.cellClassLight = "RPT_TBL";
		      tg.cellClassDark = "RPT_TBD";
		      tg.tableSubHeaderClass = "RPT_HEADER";
		      tg.tableHeaderClass = "TABLE-HEADERTEXT";

		      PdfPCell cell = new PdfPCell(pgh.multiFontPara("Manufacturing Routing", pgh.getCellFont("FORMLABEL", null, null)));
		      cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor("BORDERED_BLOCK", null));
		      cell.setBorderColor(PDFGeneratorHelper.getCellBGColor("BORDERED_BLOCK", null));
		      cell.setBorderWidth(0.0F);
		      cell.setHorizontalAlignment(0);
		      cell.setVerticalAlignment(6);
		      tg.setTitleCell(cell);

		      PdfPTable mainTable = new PdfPTable(1);
		      PdfPTable revisionHeaderAttTable = new PdfPTable(8);
			  revisionHeaderAttTable.setWidths(columnWidths);

		      revisionMoaType = FlexTypeCache.getFlexTypeFromPath("Multi-Object\\Revision Attributes");
		      
		      cell = createHeaderCell("Product Type");
		      revisionHeaderAttTable.addCell(cell);	
		      
        	  cell = createHeaderCell(revisionMoaType.getAttribute("hbiSpec").getAttDisplay(true));
			  revisionHeaderAttTable.addCell(cell);
		
		      cell = createHeaderCell(revisionMoaType.getAttribute("hbiRevisionBusinessObject").getAttDisplay(true));
		      revisionHeaderAttTable.addCell(cell);
		      cell = createHeaderCell(revisionMoaType.getAttribute("hbiRevision2").getAttDisplay(true));
		      revisionHeaderAttTable.addCell(cell);

		      cell = createHeaderCell(revisionMoaType.getAttribute("hbiRevision3").getAttDisplay(true));
		      revisionHeaderAttTable.addCell(cell);
		      cell = createHeaderCell(revisionMoaType.getAttribute("hbiComments").getAttDisplay(true));
		      revisionHeaderAttTable.addCell(cell);
		      cell = createHeaderCell(revisionMoaType.getAttribute("hbiDateModified").getAttDisplay(true));
		      revisionHeaderAttTable.addCell(cell);
		      cell = createHeaderCell(revisionMoaType.getAttribute("hbiLastUsed").getAttDisplay(true));
		      revisionHeaderAttTable.addCell(cell);

		      mainTable.addCell(new PdfPCell(revisionHeaderAttTable));
		      while (((Iterator)revisionIter).hasNext() && rowcnt < REVISION_CNT)
		      {
				hbiDateModified = "";
				hbiRevision1 = "";
				hbiRevision2 = "";
				hbiRevision3 = "";
				hbiProdType = "";
				specMOAValue = "";
				
		        revisionDataTable = new PdfPTable(8);
			    revisionDataTable.setWidths(columnWidths);
		        moaObject = (LCSMOAObject)((Iterator)revisionIter).next();
		        LCSPartMaster wtPartMaster = (LCSPartMaster) moaObject.getOwner();
		        
				if (VersionHelper.latestIterationOf(wtPartMaster) instanceof LCSProduct) {
					LCSProduct owner = (LCSProduct) VersionHelper
							.latestIterationOf(wtPartMaster);
					hbiProdType = owner.getFlexType().getFullName();
				}
		       debug("Object Id " + FormatHelper.getObjectId(wtPartMaster));
		       debug("Prod Type " + hbiProdType);
		        
				Collection specKeyValue =null;
				Collection specKey =null;    	
				hbiSpecObj = moaObject.getValue("hbiSpec");

				if(hbiSpecObj != null){
					hbiSpecObjStr = hbiSpecObj.toString();
					if(hbiSpecObjStr!=null)
					{
						specKeyValue =moaObject.getFlexType().getAttribute("hbiSpec").getAttValueList().getSelectableValues(null,true);
						specKey =moaObject.getFlexType().getAttribute("hbiSpec").getAttValueList().getSelectableKeys(null,true);
					}
					Iterator specKeyIter = specKey.iterator();
					Iterator specKeyValueIter = specKeyValue.iterator();
					HashMap<String,String> specHashKeys = new HashMap<String,String>();
					while(specKeyIter.hasNext() && specKeyValueIter.hasNext())
					{    		
						String keyVal1 =specKeyIter.next().toString().trim();
						String tmpVal1= specKeyValueIter.next().toString().trim();
						specHashKeys.put(keyVal1, tmpVal1);    		
					}
					specMOAValue = specHashKeys.get(hbiSpecObjStr);
				}
				
				int specMOAInt = FormatHelper.hasContent(specMOAValue)?Integer.parseInt(specMOAValue):0;
		        if((hbiProdType.equals(BASIC_CUT_AND_SEW_GARMENT) && specMOAInt<=specNum)
		        		|| (hbiProdType.equals(BASIC_CUT_AND_SEW_PATTERN) && specMOAInt<=pspecNum)) {
			        cell = createDataCell(hbiProdType.substring(hbiProdType.lastIndexOf('-')+1).trim());
			        revisionDataTable.addCell(cell);
	
					cell = createDataCell(specMOAValue);
					revisionDataTable.addCell(cell);
	
					revision1 = (LCSLifecycleManaged)moaObject.getValue("hbiRevisionBusinessObject");
					if(revision1 != null){
						hbiRevision1 = (String)revision1.getName();
						}
			        cell = createDataCell(hbiRevision1);
			        revisionDataTable.addCell(cell);
					
					revision2 = (LCSLifecycleManaged)moaObject.getValue("hbiRevision2");
					if(revision2 != null){
						hbiRevision2 = (String)revision2.getName();
					}
			        cell = createDataCell(hbiRevision2);
			        revisionDataTable.addCell(cell);
				
					revision3 = (LCSLifecycleManaged)moaObject.getValue("hbiRevision3");
					if(revision3 != null){
						hbiRevision3 = (String)revision3.getName();
						}
			        cell = createDataCell(hbiRevision3);
			        revisionDataTable.addCell(cell);
					
					Object comments	= moaObject.getValue("hbiComments");
					hbiComments="";//For CA#35727-14-for the fixing the issues of duplication of comments in pdf
					if(comments != null){
						hbiComments	= comments.toString();
					}
			        cell = createDataCell(hbiComments);
			        revisionDataTable.addCell(cell);
					
					Date pModifyDate = (Date)moaObject.getValue("hbiDateModified");
					if(pModifyDate != null){
						hbiDateModified = FormatHelper.applyFormat(pModifyDate, "MM/dd/yyyy");
					}
			        cell = createDataCell(hbiDateModified);
			        revisionDataTable.addCell(cell);
					
					FlexObject flexobj  = (FlexObject)moaObject.getValue("hbiLastUsed");
					String userId = (String)flexobj.get("OID");
					if (!userId.equals("")) {
						WTUser user = (WTUser) LCSQuery.findObjectById("wt.org.WTUser:" + userId);
						hbiLastUsed = "";
						if(user!=null && !user.isRepairNeeded()){
							if(user.getFullName()!=null) {
								hbiLastUsed = user.getFullName();
							}
						} 
					}
			        cell = createDataCell(hbiLastUsed);
			        revisionDataTable.addCell(cell);		
			        mainTable.addCell(new PdfPCell(revisionDataTable));
			        rowcnt++;			        
		        }
		      }

		      return mainTable;
		    }
		    catch (Exception e) {
		    throw new WTException(e);}
		  }
  
	/* *returns the PdfPCell containing header label for  MOA table for the specification
     * @param label
     * @return  */ 
	private PdfPCell createHeaderCell(String label){
	    
		Font font = pgh.getCellFont("RPT_HEADER","Left", fontSize);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(label, font));
	    pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_HEADER", null));           
	    return pdfpcell;
    }

	/* *returns the PdfPCell containing data for  MOA table for the specification
     * @param label
     * @return 
	 */ 
	private  PdfPCell createDataCell(String data){

	    Font font = pgh.getCellFont("RPT_TBD","Left", fontSize);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(data, font));
        pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_TBD", null));
	    return pdfpcell;
    }
	
	/**
	 * @param product
	 * @return
	 * @throws WTException
	 * @Date 09/07/18 This function return "pattern product" linked to Garment
	 *       Product, if only a single "pattern product" linked to "garment
	 *       product", else return null.
	 */
	@SuppressWarnings({ "static-access" })
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
	 *       productIda3MasterRef passed .
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
}