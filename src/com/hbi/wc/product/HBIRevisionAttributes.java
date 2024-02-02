
package com.hbi.wc.product;

import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.flextype. FlexType;
import com.lcs.wc.flextype. FlexTypeAttribute;
import com.lcs.wc.flextype. FlexTypeCache;
import com.lcs.wc.flextype. AttributeValueList;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.client.web.PDFGeneratorHelper;
import com.lcs.wc.client.web.pdf.PDFContent;
import com.lcs.wc.client.web.pdf.PDFTableGenerator;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import wt.util.WTException;
import wt.fc.WTObject;
import com.lcs.wc.specification.FlexSpecification;
import wt.vc.Mastered;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOAObject;

//import wt.part.WTPartMaster;
import com.lcs.wc.season.*;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;


// This is for Revision table on tech pack

public class HBIRevisionAttributes implements PDFContent {


	  public static String PRODUCT_ID = "PRODUCT_ID";
      public static String SPEC_ID = "SPEC_ID";
	  public static String SEASONMASTER_ID = "SEASONMASTER_ID";
      public static PDFGeneratorHelper pgh = new PDFGeneratorHelper();
      private static String fontSize = "8";

	 /** returns the Collection of PdfPTables containing the Sourcing Config Routing MOA table for the specification
     * @param params
     * @param document
     * @throws WTException
     * @return  */ 
	 public Element getPDFContent(Map params, Document document) throws WTException {
        
		PDFTableGenerator tg = null;
		String revisionMOATypeName = "Multi-Object\\Revision Attributes";
		FlexType revisionMoaType = null;
		String hbiCasingChanges = " ";
		String hbiColorwayChanges = " ";
		String hbiConstructionChanges = " ";
		String hbiCutPartChanges = " ";
		String hbiGarmentChanges = " ";
		String hbiLabelChanges = " ";
		String hbiMeasurementChanges = " ";
		String hbiPackagingChanges = " ";
		String hbiProductChanges = " ";
		String hbiRoutingChanges = " ";
		
		Collection revisionColl = new Vector() ;
		//SPL
		LCSSeasonMaster seasonMaster = null;
		LCSSeason season = null;
		LCSSourceToSeasonLink sslink = null;
		LCSSeasonProductLink spLink=null;
		
		try{

			WTObject obj = (WTObject)LCSProductQuery.findObjectById((String)params.get(PRODUCT_ID));
            if(!(obj instanceof LCSProduct)){
                throw new WTException("Can not use PDFProductSpecification on a non-LCSProduct - " + obj);
            }

	    
			LCSProduct product = (LCSProduct)obj;
			
			if(FormatHelper.hasContent((String)params.get(SEASONMASTER_ID))){		
	    		seasonMaster = (LCSSeasonMaster)LCSQuery.findObjectById((String)params.get(SEASONMASTER_ID));
				season = (LCSSeason)VersionHelper.latestIterationOf(seasonMaster);
				//seasonNameAtt = season.getFlexType().getAttribute("seasonName").getAttDisplay() + ":";
				String seasonName = (String)season.getValue("seasonName");
				spLink = LCSSeasonQuery.findSeasonProductLink(product, season);

				
				if (spLink != null) {
					Double prodSeasonRevId=spLink.getProductSeasonRevId();
					int prodSeasonRevID=prodSeasonRevId.intValue();
					LCSProduct prod=(LCSProduct)LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+prodSeasonRevID);
					FlexType flextype = spLink.getFlexType();
					FlexTypeAttribute revatt = flextype.getAttribute("hbiRevisionAttributes");
					revisionColl = LCSMOAObjectQuery.findMOACollection(prod,revatt);
					
			}


			}

			
			
			
			Iterator revisionIter = revisionColl.iterator();
			//table
			tg = new PDFTableGenerator(document);
            tg.cellClassLight = "RPT_TBL";
            tg.cellClassDark = "RPT_TBD";
            tg.tableSubHeaderClass = "RPT_HEADER";
            tg.tableHeaderClass = "TABLE-HEADERTEXT";

			//START - Commented by UST to remove hbiRevisionType attribute for Tech Pack
			/*PdfPCell cell = new PdfPCell(pgh.multiFontPara("Manufacturing Routing", pgh.getCellFont("FORMLABEL", null, null)));
			//cell.setBackgroundColor(pgh.getCellBGColor("BORDERED_BLOCK", null));
           // cell.setBorderColor(pgh.getCellBGColor("BORDERED_BLOCK", null));
           cell.setBorderWidth(5);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tg.setTitleCell(cell);*/
			//**********

			PdfPTable mainTable = new PdfPTable(1);
			//PdfPTable revisionHeaderAttTable = new PdfPTable(10);
						
			//START - Commented by UST to remove hbiRevisionType attribute for Tech Pack
			/*String revisionType="";
			//Added by Anjana -- if condition
			if (spLink != null) {
			revisionType=(String)spLink.getValue("hbiRevisionType");
			}
			
			if (FormatHelper.hasContent(revisionType))
			{
				AttributeValueList  revisionTypeAttList = spLink.getFlexType().getAttribute("hbiRevisionType").getAttValueList();
				revisionType = revisionTypeAttList.getValue((String)spLink.getValue("hbiRevisionType"), null);
				
			}
			else{
				revisionType="";
			
			}
			
			PdfPTable revisionDataTable = new PdfPTable(2);
			
			cell = createDataCell(product.getFlexType().getAttribute("hbiRevisionType").getAttDisplay(true) +":     "+revisionType);
			revisionDataTable.addCell(cell);


			String inventorySegregationRequired="";
			//Added by Anjana -- if condition
			if (spLink != null) {
			inventorySegregationRequired=(String)spLink.getValue("hbiInventorySegregationRequire");
			}
			if (FormatHelper.hasContent(revisionType))
			{
				AttributeValueList  inventorySegregationRequiredAttList = spLink.getFlexType().getAttribute("hbiInventorySegregationRequire").getAttValueList();
				inventorySegregationRequired = inventorySegregationRequiredAttList.getValue((String)spLink.getValue("hbiInventorySegregationRequire"), null);
				
			}
			else{
				inventorySegregationRequired="";
			
			}
			
			cell = createDataCell(product.getFlexType().getAttribute("hbiInventorySegregationRequire").getAttDisplay(true) +":     "+inventorySegregationRequired);
			revisionDataTable.addCell(cell);

			mainTable.addCell(new PdfPCell(revisionDataTable));


			PdfPTable revisionDataTable1 = new PdfPTable(2);
			String miscUpdate="";
			//Added by Anjana -- if condition
			if (spLink != null) {
			miscUpdate=(String)spLink.getValue("hbiMiscUpdate");
			}
			if (!FormatHelper.hasContent(miscUpdate))
			{
				miscUpdate="";
			}
			cell = createDataCell(product.getFlexType().getAttribute("hbiMiscUpdate").getAttDisplay(true) +":     "+miscUpdate);
			revisionDataTable1.addCell(cell);

			String revComments="";
			//Added by Anjana if condition
			if (spLink != null) {
			revComments=(String)spLink.getValue("hbiRevComments");
			}
			if (!FormatHelper.hasContent(revComments))
			{
				revComments="";				
			}
			
			
			cell = createDataCell(product.getFlexType().getAttribute("hbiRevComments").getAttDisplay(true) +":     "+revComments);
			revisionDataTable1.addCell(cell);
			mainTable.addCell(new PdfPCell(revisionDataTable1));*/
			//END - Comment


			return mainTable;

			}
			 catch(Exception e){
			 throw new WTException(e);
			}
		
		  }
	


	/* *returns the PdfPCell containing data for  MOA table for the specification
     * @param label
     * @return 
	 */ 
	private  PdfPCell createDataCell(String data){

	    Font font = pgh.getCellFont("RPT_TBD","Left", fontSize);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(data, font));
        //pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_TBD", null));
	    return pdfpcell;
    }



}// end classz