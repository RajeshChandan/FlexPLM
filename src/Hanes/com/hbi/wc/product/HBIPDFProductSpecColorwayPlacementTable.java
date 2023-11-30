package com.hbi.wc.product;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import com.hbi.wc.util.HBIUtil;
import com.lcs.wc.client.web.PDFGeneratorHelper;
import com.lcs.wc.client.web.pdf.PDFContent;
import com.lcs.wc.client.web.pdf.PDFTableGenerator;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype. AttributeValueList;
import com.lcs.wc.flextype. FlexType;
import com.lcs.wc.flextype. FlexTypeAttribute;
import com.lcs.wc.flextype. FlexTypeCache;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import wt.fc.WTObject;
import wt.org.WTUser;
//import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.vc.Mastered;



// This is for colorwayplacementtable on tech pack

public class HBIPDFProductSpecColorwayPlacementTable implements PDFContent
{
	public static String PRODUCT_ID = "PRODUCT_ID";
  	public static String SEASONMASTER_ID = "SEASONMASTER_ID";
	public static final String BASIC_CUT_AND_SEW_PATTERN = "BASIC CUT & SEW - PATTERN";
	public static final String BASIC_CUT_AND_SEW_GARMENT = "BASIC CUT & SEW - GARMENT";
	public static final String BASIC_CUT_AND_SEW_SELLING = "BASIC CUT & SEW - SELLING";

    public static String SPEC_ID = "SPEC_ID";
    public static PDFGeneratorHelper pgh = new PDFGeneratorHelper();
    private static String fontSize = "8";

	LCSSeasonMaster seasonMaster = null;
	LCSSeason season = null;
	LCSSourceToSeasonLink sslink = null;	
	
	 /** returns the Collection of PdfPTables containing the "Colorway Placement Table" MOA table for the specification
       * @param params
       * @param document
       * @throws WTException
       * @return  
	   */ 
	 @SuppressWarnings("rawtypes")
	public Element getPDFContent(Map params, Document document) throws WTException 
	 {
		try
		{
			WTObject obj = (WTObject)LCSProductQuery.findObjectById((String)params.get(PRODUCT_ID));
			if(!(obj instanceof LCSProduct))
			{
				throw new WTException("Can not use PDFProductSpecification on a non-LCSProduct - " + obj);
			}
			
			LCSProduct product = (LCSProduct)obj;
			PdfPTable mainPTable  = new PdfPTable(1);
			PdfPTable prodAttTable = null;

			if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_SELLING) != -1)
			{
				prodAttTable = getSellingProductInfo(params, document,product);
			}
			if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_SELLING) == -1)
			{
				prodAttTable = getProductInfo(params, document);
			}	
			if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_GARMENT) != -1)
			{
				prodAttTable = getGarmentProductInfo(params, document);
			}
			
			PdfPCell cell  = new PdfPCell(prodAttTable);
			
			//START - Comment by UST for techpack Changes - remove Colorway Placement Table) 01/01/2013
			/*PdfPTable colorwayPlacementTable = getColorwayPlacementTable(params, document);
			PdfPCell cell1  = new PdfPCell(colorwayPlacementTable);*/
			//END - Comment
			
			PdfPCell spacecell = new PdfPCell();
			spacecell.setBorder(0);
			mainPTable.addCell(spacecell);
			mainPTable.addCell(cell);
			
			//START - Comment by UST for techpack Changes - remove Colorway Placement Table) 01/01/2013				
			/*PdfPCell spacerCell = new PdfPCell(pgh.multiFontPara("Colorway Placement Table", pgh.getCellFont("FORMLABEL", null, null)));
            spacerCell.setBorder(0);
			mainPTable.addCell(spacerCell);
			mainPTable.addCell(cell1);*/
			//END - Comment
			
			return mainPTable;
		}
		catch(Exception ex)
		{
			throw new WTException(ex);
		}
	}


     /**
     * @param params
     * @param document
     * @return
     * @throws WTException
     */
    @SuppressWarnings({ "unused", "rawtypes" })
	private PdfPTable getProductInfo(Map params, Document document) throws WTException   {
       
			String garmentApsColDiv = "";
			String packSize = "";
			String delivery = "";
			String planning = "";
			String pckApsColDiv = "";
			String sConfigMfgStyleDisVal = " ";
			String sConstructionMethodCdVal= "";
			//Commented by UST -- removed attribute Revision Code from Sourcing Config
			String sConfigPckStyleDisVal = " ";
		//	String sConfihRevisionCodeDisVal = " ";
		    LCSSourcingConfig sourceConfig = null;
			LCSLifecycleManaged constructionMethod = null;

	 
		 try{

				WTObject obj = (WTObject)LCSProductQuery.findObjectById((String)params.get(PRODUCT_ID));
				if(!(obj instanceof LCSProduct)){
					throw new WTException("Can not use PDFProductSpecification on a non-LCSProduct - " + obj);
				}
				LCSProduct prod = (LCSProduct)obj;
				FlexType flextype = prod.getFlexType();
				String specId = (String)params.get(SPEC_ID);
				if(FormatHelper.hasContent((String)params.get(SEASONMASTER_ID))){		
					seasonMaster = (LCSSeasonMaster)LCSQuery.findObjectById((String)params.get(SEASONMASTER_ID));
					season = (LCSSeason)VersionHelper.latestIterationOf(seasonMaster);
				}

				if(FormatHelper.hasContent(specId)){
            		FlexSpecification spec = (FlexSpecification)LCSProductQuery.findObjectById(specId);
					sourceConfig = (LCSSourcingConfig)VersionHelper.latestIterationOf((Mastered)spec.getSpecSource());
	    			//Commented by UST for techpack Changes 12/31/2012
					//sConfigMfgStyleDisVal = (String)sourceConfig.getValue("hbiGarmentStyle");
					//Commented by UST -- removed attributes Revision Code from Sourcing Config
	    			//Commented by UST for techpack Changes 12/31/2012
					//sConfigPckStyleDisVal = (String)sourceConfig.getValue("hbiPackagingStyle");
	    			//sConfihRevisionCodeDisVal = (String)sourceConfig.getValue("hbiRevisionCode");
					if(season != null){
					sslink  = (new LCSSourcingConfigQuery()).getSourceToSeasonLink(sourceConfig, season);
					constructionMethod = (LCSLifecycleManaged)sslink.getValue("hbiConstructionMethodCode");
					if(constructionMethod!=null){
						sConstructionMethodCdVal = constructionMethod.getName();}
					}
				}
				//Commented by UST for techpack Changes 12/31/2012
				/*packSize = FormatHelper.format((Double)prod.getValue("hbiPackSize")); 
				delivery = getAttListValue(prod.getFlexType().getAttribute("hbiDelivery"), prod);*/
			//Commented by UST -- Removed Planning % from tech pack
				//planning = FormatHelper.format((Double)prod.getValue("hbiPlanning")); 
				//Commented by UST for techpack Changes 12/31/2012
				/*garmentApsColDiv = getAttListValue(prod.getFlexType().getAttribute("hbiApsColorDiv"), prod);
				pckApsColDiv = getAttListValue(prod.getFlexType().getAttribute("hbiApsColorDivPack"), prod);*/

					PdfPTable mTable = new PdfPTable(1);
					float [] colWidths = {40.0F,60.0F};
					PdfPTable pTable = new PdfPTable(colWidths);
					PdfPCell cell = new PdfPCell();

				if(prod.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_PATTERN) == -1)
				{
				//	System.out.println("Display : " + sourceConfig.getFlexType().getAttribute("hbiConstructionMethodCode").getAttDisplay());
					cell = new PdfPCell(pgh.multiFontPara(sourceConfig.getFlexType().getAttribute("hbiConstructionMethodCode").getAttDisplay() + ":", pgh.getCellFont("FORMLABEL", null, null)));
					cell.setBorder(0);
					pTable.addCell(cell);
					cell = new PdfPCell(pgh.multiFontPara(sConstructionMethodCdVal, pgh.getCellFont("DISPLAYTEXT", null, null)));
					cell.setBorder(0);
					pTable.addCell(cell);				
					cell = new PdfPCell(pTable);
					cell.setBorder(0);
					mTable.addCell(cell);				
				}
				return  mTable;
		 }catch(Exception e){
			throw new WTException(e);
		 }
		
    }


     /**
     * @param params
     * @param document
     * @return
     * @throws WTException
     */
    @SuppressWarnings("rawtypes")
	private PdfPTable getGarmentProductInfo(Map params, Document document) throws WTException   {
    	float [] colWidths = {30.0F,60.0F};
    	PdfPTable table = new PdfPTable(1);
		PdfPTable tableRow= new PdfPTable(2);
		PdfPTable tempTable = new PdfPTable(colWidths);
		PdfPCell cell = new PdfPCell();
		PdfPCell column1 = new PdfPCell();
		String column1_Label = "";
		String column1_Value ="";
		PdfPCell column2 = new PdfPCell();
		String column2_Label = "";
		String column2_Value ="";
	

		    LCSSourcingConfig sourceConfig = null;
		    LCSSourceToSeasonLink sslink = null;	
		    LCSSeasonMaster seasonMaster = null;
		    LCSSeason season = null;
			try{
				WTObject obj = (WTObject)LCSProductQuery.findObjectById((String)params.get(PRODUCT_ID));
				if(!(obj instanceof LCSProduct)){
					throw new WTException("Can not use PDFProductSpecification on a non-LCSProduct - " + obj);
				}
				LCSProduct prod = (LCSProduct)obj;
				FlexType flextype = prod.getFlexType();
				String specId = (String)params.get(SPEC_ID);
						
				if(FormatHelper.hasContent((String)params.get(SEASONMASTER_ID))){
					seasonMaster = (LCSSeasonMaster)LCSQuery.findObjectById((String)params.get(SEASONMASTER_ID));
					season = (LCSSeason)VersionHelper.latestIterationOf(seasonMaster);
				}	
				
				if(FormatHelper.hasContent(specId)){
            		FlexSpecification spec = (FlexSpecification)LCSProductQuery.findObjectById(specId);
					sourceConfig = (LCSSourcingConfig)VersionHelper.latestIterationOf((Mastered)spec.getSpecSource());
					if(season != null)
					{
						sslink  = (new LCSSourcingConfigQuery()).getSourceToSeasonLink(sourceConfig, season);
					}
					
				}
				
					if(prod.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_GARMENT) != -1)
				{

						
						//GP Tech Pack Changes Start 12/14/2018
						// Start Row 1
						//cel11
						column1 = createHeaderCell("General Attributes");
						column1.setColspan(2);
						column1.setBorder(0);
						tableRow.addCell(column1);
						
						AttributeValueList avlistHBIDivision = flextype.getAttribute("hbiDivision").getAttValueList();						
						column1_Label = flextype.getAttribute("hbiDivision").getAttDisplay() + ":     ";
						column1_Value =  avlistHBIDivision.getValue((String)prod.getValue("hbiDivision"), Locale.getDefault());
						if(!FormatHelper.hasContent(column1_Value)) column1_Value = "";
						column1 = this.createDataCell(column1_Label,"FORMLABEL");
						column1.setBorder(0);
						//Add cells to temptable to merge cells
						tempTable.addCell(column1);
						column1 = this.createDataCell(column1_Value,"DISPLAYTEXT");
						column1.setBorder(0);
						tempTable.addCell(column1);
						
						column1 = new PdfPCell(tempTable);
						tableRow.addCell(column1);
						//cel12
						AttributeValueList avlistHBIBrandCategory = flextype.getAttribute("hbiBrandCategory").getAttValueList();
						column2_Label = flextype.getAttribute("hbiBrandCategory").getAttDisplay() + ":     " ;
						column2_Value = avlistHBIBrandCategory.getValue((String)prod.getValue("hbiBrandCategory"), Locale.getDefault());
						if(!FormatHelper.hasContent(column2_Value)) column2_Value = "";
						
						tempTable = new PdfPTable(colWidths);
						column2 = this.createDataCell(column2_Label,"FORMLABEL");
						column2.setBorder(0);
						tempTable.addCell(column2);
						column2 = this.createDataCell(column2_Value,"DISPLAYTEXT");
						column2.setBorder(0);
						tempTable.addCell(column2);
						
						column2 = new PdfPCell(tempTable);
						tableRow.addCell(column2);

						// End Row 1
						
						// Start Row 2
						//cel21
						AttributeValueList avlistHBIAPSColorDivision = flextype.getAttribute("hbiApsColorDiv").getAttValueList();						
						column1_Label = flextype.getAttribute("hbiApsColorDiv").getAttDisplay() + ":     ";
						column1_Value = avlistHBIAPSColorDivision.getValue((String)prod.getValue("hbiApsColorDiv"), Locale.getDefault());
						if(!FormatHelper.hasContent(column1_Value)) column1_Value = "";
						
						tempTable = new PdfPTable(colWidths);
						column1 = this.createDataCell(column1_Label,"FORMLABEL");
						column1.setBorder(0);
						tempTable.addCell(column1);
						column1 = this.createDataCell(column1_Value,"DISPLAYTEXT");
						column1.setBorder(0);
						tempTable.addCell(column1);
						
						column1 = new PdfPCell(tempTable);
						tableRow.addCell(column1);

						//cel22
						column2_Label = sourceConfig.getFlexType().getAttribute("hbiConstructionMethodCode1").getAttDisplay() + ":     " ;						
						LCSLifecycleManaged boConstructionMC = (LCSLifecycleManaged)sourceConfig.getValue("hbiConstructionMethodCode1");
						if(boConstructionMC != null){
							column2_Value = boConstructionMC.getName();
							if(!FormatHelper.hasContent(column2_Value)) column2_Value = "";
						}else{
							column2_Value = "";
						}
						
						tempTable = new PdfPTable(colWidths);
						column2 = this.createDataCell(column2_Label,"FORMLABEL");
						column2.setBorder(0);
						tempTable.addCell(column2);
						column2 = this.createDataCell(column2_Value,"DISPLAYTEXT");
						column2.setBorder(0);
						tempTable.addCell(column2);
						
						column2 = new PdfPCell(tempTable);
						tableRow.addCell(column2);
						// End Row 2
						
						
						// Start Row 3
						//cel31
						AttributeValueList avlistHBIDesigner = flextype.getAttribute("hbiDesigner").getAttValueList();
						column1_Label = flextype.getAttribute("hbiDesigner").getAttDisplay() + ":     ";
						column1_Value = avlistHBIDesigner.getValue((String)prod.getValue("hbiDesigner"), Locale.getDefault());
						if(!FormatHelper.hasContent(column1_Value)) column1_Value = "";
						
						tempTable = new PdfPTable(colWidths);
						column1 = this.createDataCell(column1_Label,"FORMLABEL");
						column1.setBorder(0);
						tempTable.addCell(column1);
						column1 = this.createDataCell(column1_Value,"DISPLAYTEXT");
						column1.setBorder(0);
						tempTable.addCell(column1);
						
						column1 = new PdfPCell(tempTable);
						tableRow.addCell(column1);
						//cel32
						AttributeValueList avlistHBITechnicalDesigner = flextype.getAttribute("hbiTechnicalDesigner").getAttValueList();
						column2_Label = flextype.getAttribute("hbiTechnicalDesigner").getAttDisplay() + ":     ";
						column2_Value = avlistHBITechnicalDesigner.getValue((String)prod.getValue("hbiTechnicalDesigner"), Locale.getDefault());
						if(!FormatHelper.hasContent(column2_Value)) column2_Value = "";
						
						tempTable = new PdfPTable(colWidths);
						column2 = this.createDataCell(column2_Label,"FORMLABEL");
						column2.setBorder(0);
						tempTable.addCell(column2);
						column2 = this.createDataCell(column2_Value,"DISPLAYTEXT");
						column2.setBorder(0);
						tempTable.addCell(column2);
						
						column2 = new PdfPCell(tempTable);
						tableRow.addCell(column2);
						// End Row 3
						
						// Start Row 4
						//cel41
						AttributeValueList avlistHBIProductManager = flextype.getAttribute("hbiProductManager").getAttValueList();
						column1_Label = flextype.getAttribute("hbiProductManager").getAttDisplay() + ":     ";
						column1_Value = avlistHBIProductManager.getValue((String)prod.getValue("hbiProductManager"), Locale.getDefault());
						if(!FormatHelper.hasContent(column1_Value)) column1_Value = "";
						
						tempTable = new PdfPTable(colWidths);
						column1 = this.createDataCell(column1_Label,"FORMLABEL");
						column1.setBorder(0);
						tempTable.addCell(column1);
						column1 = this.createDataCell(column1_Value,"DISPLAYTEXT");
						column1.setBorder(0);
						tempTable.addCell(column1);
						
						column1 = new PdfPCell(tempTable);
						tableRow.addCell(column1);
						//cel42
						//Added Irregular Style for GP General Attributes -- START 
						column2_Label = flextype.getAttribute("hbiIrregularStyle").getAttDisplay() + ":     ";
						column2_Value = (String)prod.getValue("hbiIrregularStyle");
						if(!FormatHelper.hasContent(column2_Value)) column2_Value = "";
						
						tempTable = new PdfPTable(colWidths);
						column2 = this.createDataCell(column2_Label,"FORMLABEL");
						column2.setBorder(0);
						tempTable.addCell(column2);
						column2 = this.createDataCell(column2_Value,"DISPLAYTEXT");
						column2.setBorder(0);
						tempTable.addCell(column2);
						
						column2 = new PdfPCell(tempTable);
						tableRow.addCell(column2);
						//Added Irregular Style for GP General Attributes -- END 
						// End Row 4
						
						// Start Row 5
						//cel51
						AttributeValueList avlistHBIProjectAdmin = flextype.getAttribute("hbiProjectAdministrator").getAttValueList();
						column1_Label = flextype.getAttribute("hbiProjectAdministrator").getAttDisplay() + ":     ";
						column1_Value = avlistHBIProjectAdmin.getValue((String)prod.getValue("hbiProjectAdministrator"), Locale.getDefault());
						if(!FormatHelper.hasContent(column1_Value)) column1_Value = "";
						
						tempTable = new PdfPTable(colWidths);
						column1 = this.createDataCell(column1_Label,"FORMLABEL");
						column1.setBorder(0);
						tempTable.addCell(column1);
						column1 = this.createDataCell(column1_Value,"DISPLAYTEXT");
						column1.setBorder(0);
						tempTable.addCell(column1);
						
						column1 = new PdfPCell(tempTable);
						tableRow.addCell(column1);
						
						//cel52
						//Added Irregular Style for GP General Attributes -- START 
						column2_Label = flextype.getAttribute("hbiImperfectStyle").getAttDisplay() + ":     ";
						column2_Value = (String)prod.getValue("hbiImperfectStyle");
						if(!FormatHelper.hasContent(column2_Value)) column2_Value = "";
						
						tempTable = new PdfPTable(colWidths);
						column2 = this.createDataCell(column2_Label,"FORMLABEL");
						column2.setBorder(0);
						tempTable.addCell(column2);
						column2 = this.createDataCell(column2_Value,"DISPLAYTEXT");
						column2.setBorder(0);
						tempTable.addCell(column2);
						
						column2 = new PdfPCell(tempTable);
						tableRow.addCell(column2);
						//Added Imperfect Style for GP General Attributes -- END 
						// End Row 5
						
						// start Row 6
						
						//cel61
						if(sslink!=null) {
							column1_Label = sslink.getFlexType().getAttribute("hbiActiveSpec").getAttDisplay() + ":     " ;
							AttributeValueList avlistHBIActiveSpec = sslink.getFlexType().getAttribute("hbiActiveSpec").getAttValueList();
							column1_Value = avlistHBIActiveSpec.getValue((String)sslink.getValue("hbiActiveSpec"),Locale.getDefault());
						} else {
							column1_Label = "Active Specification" + ":     " ;
							column1_Value = "";
						}
						if(!FormatHelper.hasContent(column1_Value)) column1_Value = "";		
						
						tempTable = new PdfPTable(colWidths);
						column1 = this.createDataCell(column1_Label,"FORMLABEL");
						column1.setBorder(0);
						tempTable.addCell(column1);
						column1 = this.createDataCell(column1_Value,"DISPLAYTEXT");
						column1.setBorder(0);
						tempTable.addCell(column1);
						
						column1 = new PdfPCell(tempTable);
						tableRow.addCell(column1);
						
						//cel62 - Dummmy
						
						column1_Label = "" + "     ";
						column1_Value = "";
					
						
						tempTable = new PdfPTable(colWidths);
						column1 = this.createDataCell(column1_Label,"FORMLABEL");
						column1.setBorder(0);
						tempTable.addCell(column1);
						column1 = this.createDataCell(column1_Value,"DISPLAYTEXT");
						column1.setBorder(0);
						tempTable.addCell(column1);
						
						column1 = new PdfPCell(tempTable);
						tableRow.addCell(column1);
						
						//End Row 6
						cell = new PdfPCell(tableRow);
						cell.setBorder(0);
						table.addCell(cell);
						//GP Tech Pack Changes End 12/14/2018
				}
				return  table;

			}catch(Exception e){
			throw new WTException(e);
			}
		
    }

	 /** creates  the columns of PdfPTables containing the "Colorway Placement Table" MOA table rows for the specification
       * @param params
       * @param document
       * @throws WTException
       * @return PdfPTable
	   */
	 @SuppressWarnings({ "unused", "static-access", "rawtypes" })
	private PdfPTable getColorwayPlacementTable(Map params, Document document) throws WTException{

			PDFTableGenerator ptg = null;
			String positionInPack = " ";
			LCSSKU garmentColor = null;
			String garmentColorName = " ";
			String colorType=" ";
			String printCode=" ";
			String promoProductName=" ";
			String sizesOffered=" ";
			LCSProduct promoProduct=null;
			/*Added by UST*/
			String assortment = " ";
			String asstColorCode = " ";
			String delivery = " ";
			String planPct = " ";
			String colorwayPrintCode = " ";

			LCSSeasonMaster seasonMaster = null;
			LCSSeason season = null;
			LCSSourceToSeasonLink sslink = null;
			LCSSeasonProductLink spLink=null;
			Collection colorwayPositionColl = new Vector();

			String colorwayPlsTblTypeName = "Multi-Object\\Colorway Placement Table";
			FlexType colorwayPlsTblType = null;
			try{


				//System.out.println("#################################################################");
				//System.out.println("params"+params);
				//System.out.println("#################################################################");


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
						FlexTypeAttribute colorwayPositionAtt = flextype.getAttribute("hbiPackPositionNew");
						colorwayPositionColl = LCSMOAObjectQuery.findMOACollection(prod,colorwayPositionAtt);
					}
				//System.out.println("#################################################################");
				//System.out.println("spLink "+spLink);
				//System.out.println("colorwayPositionAtt "+colorwayPositionAtt);
				//System.out.println("colorwayPositionColl "+colorwayPositionColl);
				//System.out.println("#################################################################");

				}

				Iterator colorwayPositionIter = colorwayPositionColl.iterator();
			
				//table
				ptg = new PDFTableGenerator(document);
				ptg.cellClassLight = "RPT_TBL";
				ptg.cellClassDark = "RPT_TBD";
				ptg.tableSubHeaderClass = "RPT_HEADER";
				ptg.tableHeaderClass = "TABLE-HEADERTEXT";
				

				PdfPCell cell = new PdfPCell(pgh.multiFontPara("Colorway Placement Table", pgh.getCellFont("FORMLABEL", null, null)));
				cell.setBackgroundColor(pgh.getCellBGColor("BORDERED_BLOCK", null));
				cell.setBorderColor(pgh.getCellBGColor("BORDERED_BLOCK", null));
				cell.setBorderWidth(0.0f);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				ptg.setTitleCell(cell);
				

				PdfPTable mainTable = new PdfPTable(1);
				PdfPTable colorwayPlsHeaderTable = new PdfPTable(11);
				colorwayPlsTblType = FlexTypeCache.getFlexTypeFromPath(colorwayPlsTblTypeName);
				
// UST - Modified the order and pulled new attribute headers on Colorway table MOA to tech pack.

				cell = createHeaderCell(colorwayPlsTblType.getAttribute("hbiAssortment").getAttDisplay(true));
				colorwayPlsHeaderTable.addCell(cell);
				
				cell = createHeaderCell(colorwayPlsTblType.getAttribute("hbiAsstColorCode").getAttDisplay(true));
				colorwayPlsHeaderTable.addCell(cell);
				
				cell = createHeaderCell(colorwayPlsTblType.getAttribute("hbiDelivery").getAttDisplay(true));
				colorwayPlsHeaderTable.addCell(cell);
				
				cell = createHeaderCell(colorwayPlsTblType.getAttribute("hbiPlanPct").getAttDisplay(true));
				colorwayPlsHeaderTable.addCell(cell);
				
				cell = createHeaderCell(colorwayPlsTblType.getAttribute("hbiPositionInPack").getAttDisplay(true));
				colorwayPlsHeaderTable.addCell(cell);
								
				cell = createHeaderCell(colorwayPlsTblType.getAttribute("hbiGarmentColor").getAttDisplay(true));
				colorwayPlsHeaderTable.addCell(cell);				

				cell = createHeaderCell(colorwayPlsTblType.getAttribute("sizesOffered").getAttDisplay(true));
				colorwayPlsHeaderTable.addCell(cell);

				cell = createHeaderCell(colorwayPlsTblType.getAttribute("hbiPrintCode").getAttDisplay(true));
				colorwayPlsHeaderTable.addCell(cell);

				cell = createHeaderCell(colorwayPlsTblType.getAttribute("hbiColorwayPrintCode").getAttDisplay(true));
				colorwayPlsHeaderTable.addCell(cell);
				
				cell = createHeaderCell(colorwayPlsTblType.getAttribute("hbiColorType").getAttDisplay(true));
				colorwayPlsHeaderTable.addCell(cell);
				
				cell = createHeaderCell(colorwayPlsTblType.getAttribute("hbiPromoProduct").getAttDisplay(true));
				colorwayPlsHeaderTable.addCell(cell);				
				
				

				mainTable.addCell(new PdfPCell(colorwayPlsHeaderTable));

				while(colorwayPositionIter.hasNext()){
				
			
					PdfPTable colorwayPlsDataTable = new PdfPTable(11);
					LCSMOAObject moaObject = (LCSMOAObject)colorwayPositionIter.next();
					
					/*Added by UST to include changes in colorway table------BEGIN---------------------*/
					AttributeValueList assortmentAttList = moaObject.getFlexType().getAttribute("hbiAssortment").getAttValueList();
					assortment = assortmentAttList.getValue((String)moaObject.getValue("hbiAssortment"), null);
					cell = createDataCell(assortment);
					colorwayPlsDataTable.addCell(cell);
					assortment = " ";					
					
					asstColorCode=(String)moaObject.getValue("hbiAsstColorCode");
					cell = createDataCell(asstColorCode);
					colorwayPlsDataTable.addCell(cell);
					asstColorCode=" ";
					
					AttributeValueList deliveryAttList = moaObject.getFlexType().getAttribute("hbiDelivery").getAttValueList();
					delivery = deliveryAttList.getValue((String)moaObject.getValue("hbiDelivery"), null);
					cell = createDataCell(delivery);
					colorwayPlsDataTable.addCell(cell);
					delivery = " ";	

					planPct="" + moaObject.getValue("hbiPlanPct") + "%";
					cell = createDataCell(planPct);
					colorwayPlsDataTable.addCell(cell);
					planPct = " ";					
					
					/*---Code Added by UST -------- END ------- */
						
					AttributeValueList positionInPackAttList = moaObject.getFlexType().getAttribute("hbiPositionInPack").getAttValueList();
					positionInPack = positionInPackAttList.getValue((String)moaObject.getValue("hbiPositionInPack"), null);
					cell = createDataCell(positionInPack);
					colorwayPlsDataTable.addCell(cell);
					positionInPack= " ";					

					garmentColor = (LCSSKU)moaObject.getValue("hbiGarmentColor");
					if(garmentColor != null){
						garmentColorName = ((LCSPartMaster)garmentColor.getMaster()).getName();

					}
					cell = createDataCell(garmentColorName);
					colorwayPlsDataTable.addCell(cell);					
					//mainTable.addCell(new PdfPCell(colorwayPlsDataTable));
					garmentColorName= " ";
					
					sizesOffered=(String)moaObject.getValue("sizesOffered");
					cell = createDataCell(sizesOffered);
					colorwayPlsDataTable.addCell(cell);
					sizesOffered=" ";

					printCode=(String)moaObject.getValue("hbiPrintCode");
					cell = createDataCell(printCode);
					colorwayPlsDataTable.addCell(cell);
					printCode=" ";
					
					//Added by UST to include Colorway Print Code which was missing
					colorwayPrintCode=(String)moaObject.getValue("hbiColorwayPrintCode");
					cell = createDataCell(colorwayPrintCode);
					colorwayPlsDataTable.addCell(cell);
					colorwayPrintCode=" ";					

					AttributeValueList colorTypeAttList = moaObject.getFlexType().getAttribute("hbiColorType").getAttValueList();
					colorType = colorTypeAttList.getValue((String)moaObject.getValue("hbiColorType"), null);
					cell = createDataCell(colorType);
					colorwayPlsDataTable.addCell(cell);
					colorType=" ";
					
					promoProduct = (LCSProduct)moaObject.getValue("hbiPromoProduct");
					if(promoProduct != null){
						promoProductName = ((LCSPartMaster)promoProduct.getMaster()).getName();

					}
					cell = createDataCell(promoProductName);
					colorwayPlsDataTable.addCell(cell);					
					promoProductName= " ";
						
					mainTable.addCell(new PdfPCell(colorwayPlsDataTable));
				
				}

				return mainTable;
			
			}catch(Exception e){
				throw new WTException(e);
			}

	   }

	   
	/* *returns the PdfPCell containing header label for  MOA table for the specification
     * @param label
     * @return  */ 
	@SuppressWarnings("static-access")
	private PdfPCell createHeaderCell(String label){
	    
		com.lowagie.text.Font font = pgh.getCellFont("RPT_HEADER","Left", fontSize);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(label, font));
	    pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_HEADER", null));           
	    return pdfpcell;
    }

	/* *returns the PdfPCell containing data for  MOA table for the specification
     * @param label
     * @return  */ 
	@SuppressWarnings("static-access")
	private  PdfPCell createDataCell(String data){

		com.lowagie.text.Font font = pgh.getCellFont("RPT_TBD","Left", fontSize);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(data, font));
        pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_TBD", null));
	    return pdfpcell;
    }
//Added for GP Tech Pack Start 12/14/2018
	@SuppressWarnings("static-access")
	private  PdfPCell createDataCell(String data, String type){
		com.lowagie.text.Font font = pgh.getCellFont(type,"Left", fontSize);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(data, font));
        pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_TBD", null));
	    return pdfpcell;
    }
//Added for GP Tech Pack End 12/14/2018
	/** Get attribute(single list or driven) value of objects like product, season, source
          * @param att, typed
          * @return String
        */
        @SuppressWarnings("unused")
		private String getAttListValue(FlexTypeAttribute att, FlexTyped typed)
        {
	       String key = ""; 
	       String value = "";
	       try{
	    	  key = att.getAttKey();
	    	  value = (String)typed.getValue(key);
	    	  AttributeValueList valueList = att.getAttValueList();
	    	  if(valueList !=null){
			     value = valueList.getValue(value,null);
		      }
	       }
	        catch(Exception e){
			//e.printStackTrace();
	       }
	        return value;
          }
		  
		  
	/**
   * This function is using to read,validate and print Selling Product Information on cover page like General Attribute Section, Contacts Section and Marketing (BI) attributes section.
   * @param paramMap - Map
   * @param document - Document
   * @return mTable - PdfPTable
   * @throws WTException
   */	
	@SuppressWarnings({ "rawtypes" })
	private PdfPTable getSellingProductInfo(Map params, Document document,LCSProduct productObj) throws WTException  
	 {
		try
		{
			PdfPTable coverPageTable = new PdfPTable(1);
			
			//Start General Attributes Table
			PdfPTable generalAttributesTable = getSPGeneralAttributeInfo(params, document, productObj);
			//End General Attributes Table
			
			//Start Other Details Table - Added on April 02,2019
			PdfPTable otherDetailsTable = getSPOtherDetailsInfo(params, document, productObj);
			//End Other Details Table
			
			//Start Marketing (BI) Table - Added on April 02,2019
			PdfPTable marketingBI = getSPMarketingBI(params, document, productObj);
			//End  Marketing (BI) Table
		
			//START : Getting Contacts Attributes
			String productAdministrator = "";
			String sourcingManager = "";
			String merchandising = "";
			
			//Getting Product Administrator attribute from the product. 
			FlexObject prodAdminFlexobj  = (FlexObject)productObj.getValue("hbiProdAdmin");
			if(prodAdminFlexobj != null)
			{
				String prodAdminUserId = (String)prodAdminFlexobj.get("OID");
				if (FormatHelper.hasContent(prodAdminUserId))
				{
					WTUser prodAdminUser = (WTUser) LCSQuery.findObjectById("wt.org.WTUser:" + prodAdminUserId);
					productAdministrator = prodAdminUser.getFullName();
				}
			}	
			
			//Getting Sourcing Manager Attribute from the product. 
			FlexObject sourcingManagerflexobj  = (FlexObject)productObj.getValue("hbiSourcingManager");
			if(sourcingManagerflexobj != null)
			{
				String sourcingManagerUserId = (String)sourcingManagerflexobj.get("OID");
				if (FormatHelper.hasContent(sourcingManagerUserId))
				{
					WTUser sourcingManagerUser = (WTUser) LCSQuery.findObjectById("wt.org.WTUser:" + sourcingManagerUserId);
					sourcingManager = sourcingManagerUser.getFullName();
				}
			}	
			
			//Getting Merchandising attribute from the product. 
			FlexObject merchandisingFlexobj  = (FlexObject)productObj.getValue("hbiMerchandising");
			if(merchandisingFlexobj != null)
			{
				String merchandisingUserId = (String)merchandisingFlexobj.get("OID");
				if (FormatHelper.hasContent(merchandisingUserId))
				{
					WTUser merchandisingUser = (WTUser) LCSQuery.findObjectById("wt.org.WTUser:" + merchandisingUserId);
					merchandising = merchandisingUser.getFullName();
				}
			}	
			//END : Getting Contacts Attributes
				
			//START : To print Contacts Group
			PdfPTable pTable2 = new PdfPTable(1);
			//float [] colWidths2 = {50.0F,50.0F,50.0F};
			//PdfPTable pTable2 = new PdfPTable(colWidths2);
			PdfPCell cell2 = new PdfPCell();
		
			cell2 = createHeaderCell("Contacts :");	
			cell2.setColspan(3);
			pTable2.addCell(cell2);
				
			cell2 = new PdfPCell(pgh.multiFontPara(productObj.getFlexType().getAttribute("hbiProdAdmin").getAttDisplay() + ":  "+productAdministrator, pgh.getCellFont("DISPLAYTEXT", null, null)));
			cell2.setBorder(0);
			pTable2.addCell(cell2);
					
			cell2 = new PdfPCell(pgh.multiFontPara(productObj.getFlexType().getAttribute("hbiSourcingManager").getAttDisplay() + ":  "+sourcingManager, pgh.getCellFont("DISPLAYTEXT", null, null)));
			cell2.setBorder(0);
			pTable2.addCell(cell2);
			
			cell2 = new PdfPCell(pgh.multiFontPara(productObj.getFlexType().getAttribute("hbiMerchandising").getAttDisplay() + ":  "+merchandising, pgh.getCellFont("DISPLAYTEXT", null, null)));
			cell2.setBorder(0);
			pTable2.addCell(cell2);
		
			//END : To print Contacts Group.
	
			coverPageTable.addCell(generalAttributesTable);
		
			coverPageTable.addCell(otherDetailsTable);
			
			coverPageTable.addCell(marketingBI);
			
			coverPageTable.addCell(pTable2);
			
			return  coverPageTable;
		}
		catch(Exception e)
		{
			throw new WTException(e);
		}
	}

    /**
     * @param params
     * @param document
     * @param productObj
     * @return
     * @throws WTException
     * @author Manoj Konakalla
     * @Date March 25,2019
     */
    @SuppressWarnings({  "rawtypes" })
	private PdfPTable getSPGeneralAttributeInfo(Map params, Document document,LCSProduct productObj) throws WTException  
	 {
     PdfPTable pTable = new PdfPTable(1);
     //Table with two coloumns
     PdfPTable tableRow= new PdfPTable(2);
     PdfPCell column1 = new PdfPCell();
     PdfPCell column2 = new PdfPCell();
		
	// Start Header Row
	column1 = createHeaderCell("General Attributes");	
	column1.setColspan(2);
	tableRow.addCell(column1);
	
	// Header End Row 
	
	// Start Row 1
	//cel11 :: Selling Style # 
	column1 = getColumnCell(productObj, "hbiSellingStyleNumber");
	tableRow.addCell(column1);//cell11
	//Cell12 :: SAP Product Group 
	column2 = getColumnCell(productObj, "hbiSAPProductGroup");
	tableRow.addCell(column2);//cell12
	// End Row 1
	
	// Start Row 2
	//Cell21 :: ERP Material Type 
	column1 = getColumnCell(productObj, "hbiErpMaterialType");
	tableRow.addCell(column1);//cell21
	
	//Cell22 :: Product Description 
	column2 = getColumnCell(productObj, "hbiDescription");
	tableRow.addCell(column2);//cell22
	// End Row 2
	
	// Start Row 3
	//Cell31 :: Attribution Code 
	column1 = getColumnCell(productObj, "hbiErpAttributionCode");
	tableRow.addCell(column1);//cell31

	//Cell32 :: Ext. Material Group 
	column2 = getColumnCell(productObj, "hbiErpExtMatGroup");
	tableRow.addCell(column2);//cell32
	// End Row 3

	// Start Row 4
	//Cell41 :: Pack Quantity 
	column1 = getColumnCell(productObj, "hbiAPSPackQuantity");
	tableRow.addCell(column1);//Cell41
	
	//Cell42 :: Free Goods Quantity 
	column2 = getColumnCell(productObj, "hbiErpFreeGoods");
	tableRow.addCell(column2);//cell42
	// End Row 4
	
	// Start Row 5
	//Cell51 :: Selling Size Category 
	column1 = getColumnCell(productObj, "hbiSellingSizeCategory");
	tableRow.addCell(column1);//cell51

	//Cell52 :: PLM No 
	column2 =  getColumnCell(productObj, "hbiPLMNo");
	tableRow.addCell(column2);//cell52
	// End Row 5
	
	// Start Row 6
	//Cell61 :: Exclusive (Omni Channel, Adjmi, Children's Place) 
	column1 = getColumnCell(productObj, "hbiOmniSelection");
	tableRow.addCell(column1);//Cell61

	//Cell62 :: Comments 
	column2 = getColumnCell(productObj, "hbiComments");
	tableRow.addCell(column2);//cell62
	// End Row 6
	
	// Start Row 7
	//Cell71 :: Product Features: Single Select 
	column1 = getColumnCell(productObj, "hbiSingleSelectFeature");
	tableRow.addCell(column1);//Cell71
	

	//Cell71 :: ""
	column2 = this.createDataCell("");
	tableRow.addCell(column2);
	
	//cel72
	// End Row 7
	PdfPCell cell = new PdfPCell(tableRow);
	cell.setBorder(0);
	pTable.addCell(cell);
	return pTable;
    	
	 }

    /**
     * @param params
     * @param document
     * @param productObj
     * @return
     * @throws WTException
     * @author Manoj Konakalla
     * @Date April 2,2019
     */
    @SuppressWarnings({  "rawtypes" })
	private PdfPTable getSPOtherDetailsInfo(Map params, Document document,LCSProduct productObj) throws WTException  
	 {
     PdfPTable pTable = new PdfPTable(1);
     //Table with two coloumns
     PdfPTable tableRow= new PdfPTable(2);
     PdfPCell column1 = new PdfPCell();
     PdfPCell column2 = new PdfPCell();
		
	// Start Header Row
	column1 = createHeaderCell("Other Details");	
	column1.setColspan(2);
	tableRow.addCell(column1);
	
	// Header End Row 
	
	// Start Row 1
	//cel11 :: Other Details 
	column1 = getColumnCell(productObj, "hbiErpOmniMerch");
	tableRow.addCell(column1);//cell11
	//Cell12 :: Sales Org Extension(s) 
	column2 = getColumnCell(productObj, "hbiSalesOrg2");
	tableRow.addCell(column2);//cell12
	// End Row 1
	
	// Start Row 2
	//Cell21 :: Irregular Style 
	column1 = getColumnCell(productObj, "hbiIrregularStyle");
	tableRow.addCell(column1);//cell21
	
	//Cell22 :: Corp Division 
	column2 = getColumnCell(productObj, "hbiErpCorpDivision");
	tableRow.addCell(column2);//cell22
	// End Row 2
	
	// Start Row 3
	//Cell31 :: Imperfect Style 
	column1 = getColumnCell(productObj, "hbiImperfectStyle");
	tableRow.addCell(column1);//cell31

	//Cell32 :: Cost Family Code 
	column2 = getColumnCell(productObj, "hbICostFamilyCode");
	tableRow.addCell(column2);//cell32
	// End Row 3

	// Start Row 4
	//Cell41 :: Thirds Style 
	column1 = getColumnCell(productObj, "hbiThirdsStyle");
	tableRow.addCell(column1);//Cell41
	
	//Cell42 :: ""
	column2 = getColumnCell(productObj, "");
	tableRow.addCell(column2);//cell42
	// End Row 4
	
	// Start Row 5
	//Cell51 :: GTIN/UPC Label Code 
	column1 = getColumnCell(productObj, "hbiGTINUPCLabelCode");
	tableRow.addCell(column1);//cell51

	//Cell52 :: Canadian Style Type 
	column2 =  getColumnCell(productObj, "hbiCanadianStyleType");
	tableRow.addCell(column2);//cell52

	
	// End Row 5
	
	// Start Row 6
	//Cell61 :: Usage Designation 
	column1 = getColumnCell(productObj, "hbiUsageDesignation");
	tableRow.addCell(column1);//Cell61

	//Cell62 :: ""
	column2 = getColumnCell(productObj, "");
	tableRow.addCell(column2);//cell62
	// End Row 6
	
	PdfPCell cell = new PdfPCell(tableRow);
	cell.setBorder(0);
	pTable.addCell(cell);
	return pTable;
    	
	 }
    
    /**
     * @param params
     * @param document
     * @param productObj
     * @return
     * @throws WTException
     * @author Manoj Konakalla
     * @Date April 2,2019
     */
    @SuppressWarnings({  "rawtypes" })
	private PdfPTable getSPMarketingBI(Map params, Document document,LCSProduct productObj) throws WTException  
	 {
     PdfPTable pTable = new PdfPTable(1);
     //Table with one column
     PdfPTable tableRow= new PdfPTable(1);
     PdfPCell column1 = new PdfPCell();
 		
	// Start Header Row
	column1 = createHeaderCell("Marketing (BI)");	
	column1.setColspan(2);
	tableRow.addCell(column1);
	
	// Header End Row 
	
	// Start Row 1
	//cel11 :: Brand Owner Group (BI) 
	column1 = getColumnCell(productObj, "hbiErpBrandOwnerGroup");
	tableRow.addCell(column1);//cell11
	// End Row 1
	
	// Start Row 2
	//Cell21 :: Brand Group (BI)
	column1 = getColumnCell(productObj, "hbiErpBrandGroup");
	tableRow.addCell(column1);//cell21
	// End Row 2
	
	// Start Row 3
	//Cell31 :: Brand Name (BI) 
	column1 = getColumnCell(productObj, "hbiErpBrandName");
	tableRow.addCell(column1);//cell31
	// End Row 3

	// Start Row 4
	//Cell41 :: Sub-Brand (BI) 
	column1 = getColumnCell(productObj, "hbiErpSubBrand");
	tableRow.addCell(column1);//Cell41
	// End Row 4
	
	// Start Row 5
	//Cell51 :: Gender (BI) 
	column1 = getColumnCell(productObj, "hbiErpGender");
	tableRow.addCell(column1);//cell51
	// End Row 5
	
	// Start Row 6
	//Cell61 :: Gender Category (BI) 
	column1 = getColumnCell(productObj, "hbiErpGenderCat");
	tableRow.addCell(column1);//Cell61
	// End Row 6
	
	PdfPCell cell = new PdfPCell(tableRow);
	cell.setBorder(0);
	pTable.addCell(cell);
	return pTable;
    	
	 }
 
    /**
     * @param productObj
     * @param attrKey
     * @return
     * @throws WTException
     */
    /**
     * @param productObj
     * @param attrKey
     * @return
     * @throws WTException
     * @author Manoj Konakalla
     */
    private PdfPCell getColumnCell(LCSProduct productObj, String attrKey) throws WTException {
    	PdfPCell column = new PdfPCell();
    	if (FormatHelper.hasContent(attrKey)) {
			FlexType flextype = productObj.getFlexType();
			
			float[] colWidths = { 30.0F, 60.0F };
			PdfPTable tempTable = new PdfPTable(colWidths);
			tempTable = new PdfPTable(colWidths);
			String column_Label = flextype.getAttribute(attrKey).getAttDisplay() + ":  ";
			String column_Value = HBIUtil.getAttributeTypeValue(productObj, attrKey);
			column = this.createDataCell(column_Label, "FORMLABEL");
			column.setBorder(0);
			tempTable.addCell(column);

			column = this.createDataCell(column_Value, "DISPLAYTEXT");
			column.setBorder(0);
			tempTable.addCell(column);

			 column = new PdfPCell(tempTable);
		}else{
			 column = this.createDataCell(attrKey);
    	}
    	return column;
	}


	

} // class end