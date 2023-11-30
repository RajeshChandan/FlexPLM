package com.hbi.wc.product;

import com.lcs.wc.client.web.PDFGeneratorHelper;
import com.lcs.wc.client.web.pdf.PDFContent;
import com.lcs.wc.client.web.pdf.PDFTableGenerator;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.season.LCSSeasonQuery;
import wt.vc.Mastered;
import com.lcs.wc.sourcing.*;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.supplier.LCSSupplier;
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
import com.lcs.wc.db.FlexObject;
import wt.org.WTUser;

public class HBIApprovedSuppliersTable
{
  public static String PRODUCT_ID = "PRODUCT_ID";
  public static String SPEC_ID = "SPEC_ID";
  public static String SEASONMASTER_ID = "SEASONMASTER_ID";
  public static final String BASIC_CUT_AND_SEW_GARMENT = "BASIC CUT & SEW - GARMENT";
  public static PDFGeneratorHelper pgh = new PDFGeneratorHelper();
  private static String fontSize = "8";

  public Element getPatternPDFContent(Map paramMap, Document paramDocument)
    throws WTException
  {System.out.println("inside getPatternPDFContent");
    PDFTableGenerator tg = null; 
    String revisionMOATypeName = "Multi-Object\\Approved Suppliers"; 
	FlexType revisionMoaType = null;
	String hbiGreenSeal = "";
	String hbiRedSeal = "";
	String hbiSupplier = "";
	String hbiMfgFlowObjStr = "";
	String mfgFlowMOAValue = "";
	String strComments = ""; //CA 52979-14 added Comments attribute

	Collection revisionColl = new Vector() ;
	//SPL
	LCSSeasonMaster seasonMaster = null;
	LCSSeason season = null;
	LCSSeasonProductLink spLink = null;
	LCSSupplier hbiLCSSupplier = null;
	Object hbiMfgFlowObj = null;
	LCSSourcingConfig sConfig = null;

    try
    {
      WTObject obj = (WTObject)LCSProductQuery.findObjectById((String)paramMap.get(PRODUCT_ID));
      if (!(obj instanceof LCSProduct)) {
        throw new WTException("Can not use PDFProductSpecification on a non-LCSProduct - " + obj);
      }

      LCSProduct product = (LCSProduct)obj;
      PdfPTable revisionDataTable;
      LCSMOAObject moaObject;
      if (FormatHelper.hasContent((String)paramMap.get(SEASONMASTER_ID))) {
        seasonMaster = (LCSSeasonMaster)LCSQuery.findObjectById((String)paramMap.get(SEASONMASTER_ID));
        season = (LCSSeason)VersionHelper.latestIterationOf(seasonMaster);

        String seasonName = (String)season.getValue("seasonName");

        spLink = LCSSeasonQuery.findSeasonProductLink(product, season);
	 }
	 String specId = (String)paramMap.get(SPEC_ID);
	 if(FormatHelper.hasContent(specId)){
       	FlexSpecification spec = (FlexSpecification)LCSProductQuery.findObjectById(specId);
   		sConfig = (LCSSourcingConfig)VersionHelper.latestIterationOf((Mastered)spec.getSpecSource());
	 }
	PdfPTable mainTable = new PdfPTable(1);
	PdfPTable revisionHeaderAttTable = new PdfPTable(5);
	if(sConfig != null)
	{
		  FlexType flextype = sConfig.getFlexType();
		  FlexTypeAttribute revatt = ((FlexType)flextype).getAttribute("hbiApprovedSuppliersGarment");
		  revisionColl = LCSMOAObjectQuery.findMOACollection(sConfig, revatt);		

		  Iterator revisionIter  = ((Collection)revisionColl).iterator();

		  //tg = new PDFTableGenerator(paramDocument);

		  PdfPCell cell = new PdfPCell(pgh.multiFontPara("Approved Suppliers", pgh.getCellFont("FORMLABEL", null, null)));
		  /*cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor("BORDERED_BLOCK", null));
		  cell.setBorderColor(PDFGeneratorHelper.getCellBGColor("BORDERED_BLOCK", null));
		  cell.setBorderWidth(0.0F);
		  cell.setHorizontalAlignment(0);
		  cell.setVerticalAlignment(3);
		  tg.setTitleCell(cell);*/

		  revisionMoaType = FlexTypeCache.getFlexTypeFromPath("Multi-Object\\Approved Suppliers");
		  if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_GARMENT) != -1)
		  
				cell = createHeaderCell(revisionMoaType.getAttribute("hbiSource").getAttDisplay(true));
				revisionHeaderAttTable.addCell(cell);
				cell = createHeaderCell(revisionMoaType.getAttribute("hbiFlow").getAttDisplay(true));
				revisionHeaderAttTable.addCell(cell);
				cell = createHeaderCell(revisionMoaType.getAttribute("hbiGreenSeal").getAttDisplay(true));
				revisionHeaderAttTable.addCell(cell);
				cell = createHeaderCell(revisionMoaType.getAttribute("hbiRedSeal").getAttDisplay(true));
				revisionHeaderAttTable.addCell(cell);
				//CA 52979-14 added Comments attribute
				System.out.println("tEsting ..... " + revisionMoaType.getAttribute("hbiComments").getAttDisplay(true));
				cell = createHeaderCell(revisionMoaType.getAttribute("hbiComments").getAttDisplay(true));
				revisionHeaderAttTable.addCell(cell);	
				
		  
		  mainTable.addCell(new PdfPCell(revisionHeaderAttTable));
		  while (((Iterator)revisionIter).hasNext())
		  {
			hbiGreenSeal = "";
			hbiRedSeal = "";
			hbiSupplier = "";
			mfgFlowMOAValue = "";
			strComments = "";
			
			revisionDataTable = new PdfPTable(5);
			moaObject = (LCSMOAObject)((Iterator)revisionIter).next();

			if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_GARMENT) != -1)
			{
				hbiLCSSupplier = (LCSSupplier)moaObject.getValue("hbiSource");
				if(hbiLCSSupplier != null){
					hbiSupplier = (String)hbiLCSSupplier.getName();
				}
				cell = createDataCell(hbiSupplier);
				revisionDataTable.addCell(cell);

				Collection mfgFlowKeyValue =null;
				Collection mfgFlowKey =null;    	
				hbiMfgFlowObj = moaObject.getValue("hbiFlow");
				if(hbiMfgFlowObj != null){
					hbiMfgFlowObjStr = hbiMfgFlowObj.toString();
					if(hbiMfgFlowObjStr!=null)
					{
						mfgFlowKeyValue =moaObject.getFlexType().getAttribute("hbiFlow").getAttValueList().getSelectableValues(null,true);
						mfgFlowKey =moaObject.getFlexType().getAttribute("hbiFlow").getAttValueList().getSelectableKeys(null,true);
					}
					Iterator mfgFlowKeyIter = mfgFlowKey.iterator();
					Iterator mfgFlowKeyValueIter = mfgFlowKeyValue.iterator();
					HashMap<String,String> mfgFlowHashKeys = new HashMap<String,String>();
					while(mfgFlowKeyIter.hasNext() && mfgFlowKeyValueIter.hasNext())
					{    		
						String keyVal1 =mfgFlowKeyIter.next().toString().trim();
						String tmpVal1= mfgFlowKeyValueIter.next().toString().trim();
						mfgFlowHashKeys.put(keyVal1, tmpVal1);    		
					}
					mfgFlowMOAValue = mfgFlowHashKeys.get(hbiMfgFlowObjStr);
				}
				cell = createDataCell(mfgFlowMOAValue);
				revisionDataTable.addCell(cell);
				Date greenSealDate = (Date)moaObject.getValue("hbiGreenSeal");
				if(greenSealDate != null){
					hbiGreenSeal = FormatHelper.applyFormat(greenSealDate, "MM/dd/yyyy");
				}
				cell = createDataCell(hbiGreenSeal);
				revisionDataTable.addCell(cell);
				Date redSealDate = (Date)moaObject.getValue("hbiRedSeal");
				if(redSealDate != null){
					hbiRedSeal = FormatHelper.applyFormat(redSealDate, "MM/dd/yyyy");
				}
				cell = createDataCell(hbiRedSeal);
				revisionDataTable.addCell(cell);
				//CA 52979-14 added Comments attribute

				System.out.println("before gettinng comments ****************");
				strComments=(String)moaObject.getValue("hbiComments");
				cell = createDataCell(strComments);
				revisionDataTable.addCell(cell);
				strComments = "";
			}		

			mainTable.addCell(new PdfPCell(revisionDataTable));
		  }
	}
      return mainTable;
    }
    catch (Exception e) {
    	e.printStackTrace();
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
}