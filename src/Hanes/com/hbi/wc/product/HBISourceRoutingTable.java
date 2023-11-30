
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
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;


// This is for Sourceroutingtable on tech pack

public class HBISourceRoutingTable implements PDFContent {


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
		String routingMOATypeName = "Multi-Object\\Routing MOA";
		FlexType routingMoaType = null;
		String yarnSupplierName = " ";
		String knitSupplierName = " ";
		String cutPlantFactoryName = " ";
		String sewFactoryName = " ";
		String primaryDistributionName = " ";
		String flowSupplierName = " ";
		String distributionFactorynNme = " ";
		String attributionFactoryName = " ";
		String printFactoryName = " ";
		String bdfFactoryName = " ";
		//String primaryDistribution = " ";
		Collection routingColl = new Vector() ;
		//SPL
		LCSSeasonMaster seasonMaster = null;
		LCSSeason season = null;
		LCSSourceToSeasonLink sslink = null;
		
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
				//String seasonName = (String)season.getValue("seasonName");
				//spLink = LCSSeasonQuery.findSeasonProductLink(product, season);
			}

			WTObject obj2 =  (WTObject)LCSProductQuery.findObjectById((String)params.get(SPEC_ID));
            if(obj2 == null || !(obj2 instanceof FlexSpecification)){
                throw new WTException("Can not use PDFProductSpecification on without a FlexSpecification - " + obj2);
            }

			
            FlexSpecification spec = (FlexSpecification)obj2;
            LCSSourcingConfig sConfig = (LCSSourcingConfig)VersionHelper.latestIterationOf((Mastered)spec.getSpecSource());
			//LCSSourceToSeasonLink sslink  = (new LCSSourcingConfigQuery()).getSourceToSeasonLink(sConfig, season);
			if(season != null){
				sslink  = (new LCSSourcingConfigQuery()).getSourceToSeasonLink(sConfig, season);
			}

            FlexType flextype = sConfig.getFlexType();
			FlexTypeAttribute routingatt = flextype.getAttribute("hbiRouting");
			if (sslink != null) {
				routingColl = LCSMOAObjectQuery.findMOACollection(sslink,routingatt);
			}

			Iterator routingIter = routingColl.iterator();
			//table
			tg = new PDFTableGenerator(document);
            tg.cellClassLight = "RPT_TBL";
            tg.cellClassDark = "RPT_TBD";
            tg.tableSubHeaderClass = "RPT_HEADER";
            tg.tableHeaderClass = "TABLE-HEADERTEXT";

			PdfPCell cell = new PdfPCell(pgh.multiFontPara("Manufacturing Routing", pgh.getCellFont("FORMLABEL", null, null)));
			cell.setBackgroundColor(pgh.getCellBGColor("BORDERED_BLOCK", null));
            cell.setBorderColor(pgh.getCellBGColor("BORDERED_BLOCK", null));
            cell.setBorderWidth(0.0f);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tg.setTitleCell(cell);

			PdfPTable mainTable = new PdfPTable(1);
			PdfPTable routingHeaderTable = new PdfPTable(9);
						
			routingMoaType = FlexTypeCache.getFlexTypeFromPath(routingMOATypeName);

		    cell = createHeaderCell(routingMoaType.getAttribute("hbiYarnFactory").getAttDisplay(true));
			routingHeaderTable.addCell(cell);

			cell = createHeaderCell(routingMoaType.getAttribute("hbiKnitFactory").getAttDisplay(true));
			routingHeaderTable.addCell(cell);

			cell = createHeaderCell(routingMoaType.getAttribute("hbiDBF").getAttDisplay(true));
			routingHeaderTable.addCell(cell);

			cell = createHeaderCell(routingMoaType.getAttribute("hbiPrintFactory").getAttDisplay(true));   
			routingHeaderTable.addCell(cell);

		    cell = createHeaderCell(routingMoaType.getAttribute("hbiCutPlantFactory").getAttDisplay(true));
			routingHeaderTable.addCell(cell);

			cell = createHeaderCell(routingMoaType.getAttribute("hbiSewFactory").getAttDisplay(true));
			routingHeaderTable.addCell(cell);

			cell = createHeaderCell(routingMoaType.getAttribute("hbiAttributionFactory").getAttDisplay(true)); ///
			routingHeaderTable.addCell(cell);

			cell = createHeaderCell(routingMoaType.getAttribute("hbiDistributionFactory").getAttDisplay(true));
			routingHeaderTable.addCell(cell);

			cell = createHeaderCell(routingMoaType.getAttribute("hbiPrimaryDistribution").getAttDisplay(true));
			routingHeaderTable.addCell(cell);

			mainTable.addCell(new PdfPCell(routingHeaderTable));
			while(routingIter.hasNext()){
			
				PdfPTable routingDataTable = new PdfPTable(9);
				LCSMOAObject moaObject = (LCSMOAObject)routingIter.next();

				/*LCSSupplier yarnSupplier = (LCSSupplier)moaObject.getValue("hbiYarnFactory");
				if(yarnSupplier!=null){
					yarnSupplierName = yarnSupplier.getName();
				}*/

				AttributeValueList yarnFactoryAttList = moaObject.getFlexType().getAttribute("hbiYarnFactory").getAttValueList();
				yarnSupplierName = yarnFactoryAttList.getValue((String)moaObject.getValue("hbiYarnFactory"), null);

				cell = createDataCell(yarnSupplierName);
				routingDataTable.addCell(cell);

				LCSSupplier knitSupplier = (LCSSupplier)moaObject.getValue("hbiKnitFactory");
				if(knitSupplier!=null){
					knitSupplierName = knitSupplier.getName();
				}

				cell = createDataCell(knitSupplierName);
				routingDataTable.addCell(cell);


				LCSSupplier bdfhFactory = (LCSSupplier)moaObject.getValue("hbiDBF");
				if (bdfhFactory != null) {
					bdfFactoryName = bdfhFactory.getName();
				}
				
				cell = createDataCell(bdfFactoryName);
				routingDataTable.addCell(cell);

				LCSSupplier printFactory = (LCSSupplier)moaObject.getValue("hbiPrintFactory");
				if (printFactory != null) {
					printFactoryName = printFactory.getName();
				}

				cell = createDataCell(printFactoryName);
				routingDataTable.addCell(cell);

				LCSSupplier cutPlantFactory = (LCSSupplier)moaObject.getValue("hbiCutPlantFactory");
				if (cutPlantFactory != null) {
					cutPlantFactoryName = cutPlantFactory.getName();
				}

				cell = createDataCell(cutPlantFactoryName);
				routingDataTable.addCell(cell);

				LCSSupplier sewFactory = (LCSSupplier)moaObject.getValue("hbiSewFactory");
				if (sewFactory != null) {
					sewFactoryName = sewFactory.getName();
				}
				cell = createDataCell(sewFactoryName);
				routingDataTable.addCell(cell);

				LCSSupplier attributionFactory = (LCSSupplier)moaObject.getValue("hbiAttributionFactory");
				if (attributionFactory != null) {
					attributionFactoryName = attributionFactory.getName();
				}

				cell = createDataCell(attributionFactoryName);
				routingDataTable.addCell(cell);

				LCSSupplier distributionFactory = (LCSSupplier)moaObject.getValue("hbiDistributionFactory");
				if (distributionFactory != null) {
					distributionFactorynNme = distributionFactory.getName();
				}

				cell = createDataCell(distributionFactorynNme);
				routingDataTable.addCell(cell);

				primaryDistributionName = (String)moaObject.getValue("hbiPrimaryDistribution");
				primaryDistributionName = FormatHelper.formatBoolean(primaryDistributionName) ;
				cell = createDataCell(primaryDistributionName);
				routingDataTable.addCell(cell);

				mainTable.addCell(new PdfPCell(routingDataTable));
				
				yarnSupplierName= "";
				knitSupplierName= " ";
				bdfFactoryName= " ";
				cutPlantFactoryName= " ";
				sewFactoryName= " ";
				primaryDistributionName=" ";
				distributionFactorynNme= " ";
				attributionFactoryName= " ";
				printFactoryName= " ";

			}

			return mainTable;

			}
			 catch(Exception e){
			 throw new WTException(e);
			}
		
		  }
	
	/* *returns the PdfPCell containing header label for  MOA table for the specification
     * @param label
     * @return  
	 */ 
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



}// end class