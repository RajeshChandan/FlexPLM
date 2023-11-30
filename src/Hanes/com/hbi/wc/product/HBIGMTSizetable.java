package com.hbi.wc.product;

import com.lcs.wc.flextype. FlexType;
import com.lcs.wc.flextype. FlexTypeAttribute;
import com.lcs.wc.flextype. FlexTypeCache;
import com.lcs.wc.flextype. AttributeValueList;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.client.web.PDFGeneratorHelper;
import com.lcs.wc.client.web.pdf.PDFContent;
import com.lcs.wc.client.web.pdf.PDFTableGenerator;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOAObject;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.util.Map;
import java.util.Collection;
import java.util.Iterator;

//import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.fc.WTObject;


// This is for Gmtsizetable on tech pack

public class HBIGMTSizetable implements PDFContent{

	  public static String PRODUCT_ID = "PRODUCT_ID";
      public static String SPEC_ID = "SPEC_ID";
      public static PDFGeneratorHelper pgh = new PDFGeneratorHelper();
      private static String fontSize = "8";

	 /** returns the Collection of PdfPTables containing the "GMT Size Table" MOA table for the specification
       * @param params
       * @param document
       * @throws WTException
       * @return  Element
	   */ 
	 public Element getPDFContent(Map params, Document document) throws WTException {

			try{
				PdfPTable gmtSizeTable = getGMTSizeTable(params, document);
				return gmtSizeTable;
			}catch(Exception ex){
				throw new WTException(ex);
			}
   
	 }

	 /** creates  the columns of PdfPTables containing the "GMT Size Table" MOA table rows for the specification
       * @param params
       * @param document
       * @throws WTException
       * @return PdfPTable
	   */ 
	 private PdfPTable getGMTSizeTable(Map params, Document document) throws WTException{

			PDFTableGenerator ptg = null;
			String garmentSize = " ";
			String xSize = " ";
			String sizeCode = " ";
			String gmtSizeTblTypeName = "Multi-Object\\Garment Size Table";
			FlexType gmtSizeTblType = null;

			try{

				WTObject obj = (WTObject)LCSProductQuery.findObjectById((String)params.get(PRODUCT_ID));
				if(!(obj instanceof LCSProduct)){
					throw new WTException("Can not use PDFProductSpecification on a non-LCSProduct - " + obj);
				}

				LCSProduct product = (LCSProduct)obj;
				FlexType flextype = product.getFlexType();
				FlexTypeAttribute gmtSizeTableAtt = flextype.getAttribute("hbiGarmentSizeTable");
				Collection gmtSizeTableAttColl = LCSMOAObjectQuery.findMOACollection(product,gmtSizeTableAtt);
				Iterator gmtSizeTableAttIter = gmtSizeTableAttColl.iterator();

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
				//float widths[] = {25F,25F,25F,25F};
				PdfPTable gmtSizeHeaderTable = new PdfPTable(3);
				//routingheaderTable.setWidths(widths);


				gmtSizeTblType = FlexTypeCache.getFlexTypeFromPath(gmtSizeTblTypeName);

				cell = createHeaderCell(gmtSizeTblType.getAttribute("hbiGarmentSize").getAttDisplay(true));
				gmtSizeHeaderTable.addCell(cell);

				cell = createHeaderCell(gmtSizeTblType.getAttribute("hbiXSize").getAttDisplay(true));
				gmtSizeHeaderTable.addCell(cell);

				cell = createHeaderCell(gmtSizeTblType.getAttribute("hbiSizeCode").getAttDisplay(true));
				gmtSizeHeaderTable.addCell(cell);

				mainTable.addCell(new PdfPCell(gmtSizeHeaderTable));
				
				while(gmtSizeTableAttIter.hasNext()){
			
					PdfPTable gmtSizeDataTable = new PdfPTable(3);
					LCSMOAObject moaObject = (LCSMOAObject)gmtSizeTableAttIter.next();
					garmentSize= (String)moaObject.getValue("hbiGarmentSize");
					cell = createDataCell(garmentSize);
					gmtSizeDataTable.addCell(cell);
					xSize = moaObject.getValue("hbiXSize").toString();
					cell = createDataCell(xSize);
					gmtSizeDataTable.addCell(cell);
					sizeCode = moaObject.getValue("hbiSizeCode").toString();
					cell = createDataCell(sizeCode);
					gmtSizeDataTable.addCell(cell);						
					mainTable.addCell(new PdfPCell(gmtSizeDataTable));
					garmentSize = " ";
					xSize= " ";
					sizeCode= " ";


				}


				return mainTable;
			
			}catch(Exception e){
				throw new WTException(e);
			}

	   }

	   
	 /* * returns the PdfPCell containing header label for  MOA table for the specification
        * @param label
        * @return  
		*/ 
	 private PdfPCell createHeaderCell(String label){
	    
		Font font = pgh.getCellFont("RPT_HEADER","Left", fontSize);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(label, font));
	    pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_HEADER", null));           
	    return pdfpcell;
     }

	 /* * returns the PdfPCell containing data for  MOA table for the specification
        * @param label
        * @return  
		*/ 
	 private  PdfPCell createDataCell(String data){

	    Font font = pgh.getCellFont("RPT_TBD","Left", fontSize);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(data, font));
        pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_TBD", null));
	    return pdfpcell;
    }

} // end class