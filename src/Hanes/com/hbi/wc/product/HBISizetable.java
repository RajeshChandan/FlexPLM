package com.hbi.wc.product;

import com.lcs.wc.flextype. FlexType;
import com.lcs.wc.flextype. FlexTypeAttribute;
import com.lcs.wc.flextype. FlexTypeCache;
import com.lcs.wc.product.LCSProduct;
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
import wt.util.WTException;
import wt.fc.WTObject;

// This is for sizetable on tech pack

public class HBISizetable implements PDFContent{

	  public static String PRODUCT_ID = "PRODUCT_ID";
      public static String SPEC_ID = "SPEC_ID";
      public static PDFGeneratorHelper pgh = new PDFGeneratorHelper();
      private static String fontSize = "8";

	 /** returns the Collection of PdfPTables containing the "Size Table" MOA table for the specification
       * @param params
       * @param document
       * @throws WTException
       * @return  Element
	   */ 
	 public Element getPDFContent(Map params, Document document) throws WTException {

			try{
				 PdfPTable sizeTable = getSizeTable(params, document);
				 return sizeTable;

			}catch(Exception ex){
				throw new WTException(ex);
			}
   
	 }
	
	 /** creates  the columns of PdfPTables containing the "APS Size Table" MOA table rows for the specification
       * @param params
       * @param document
       * @throws WTException
       * @return PdfPTable
	   */ 
	 private PdfPTable getSizeTable(Map params, Document document) throws WTException{

			PDFTableGenerator ptg = null;
			String sizeCode = " ";
			//Commented by UST for tech pack correction - 07/18/2012
			/*String labelDesc = " ";
			String packDesc = " ";*/
			String garmentSize = " "; //Code Added by UST for tech pack correction - 07/18/2012
			String plmPatternSize = " ";
			/*String xSize = " "; */
			String ropsCode = " ";
			//s3 Code
			String s3Code = " ";
			String apsSizeTblTypeName = "Multi-Object\\Sizing Table";
			FlexType apsSizeTblType = null;
			try{

				WTObject obj = (WTObject)LCSProductQuery.findObjectById((String)params.get(PRODUCT_ID));
				if(!(obj instanceof LCSProduct)){
					throw new WTException("Can not use PDFProductSpecification on a non-LCSProduct - " + obj);
				}

				LCSProduct product = (LCSProduct)obj;
				FlexType flextype = product.getFlexType();
				FlexTypeAttribute apsSizeTableAtt = flextype.getAttribute("hbiGarmentSizeTable");
				Collection sizeTableAttColl = LCSMOAObjectQuery.findMOACollection(product,apsSizeTableAtt);		
				Iterator sizeTableAttIter = sizeTableAttColl.iterator();

				//table
				ptg = new PDFTableGenerator(document);
				ptg.cellClassLight = "RPT_TBL";
				ptg.cellClassDark = "RPT_TBD";
				ptg.tableSubHeaderClass = "RPT_HEADER";
				ptg.tableHeaderClass = "TABLE-HEADERTEXT";
				

				PdfPCell cell = new PdfPCell(pgh.multiFontPara("Sizing Table", pgh.getCellFont("FORMLABEL", null, null)));
				cell.setBackgroundColor(pgh.getCellBGColor("BORDERED_BLOCK", null));
				cell.setBorderColor(pgh.getCellBGColor("BORDERED_BLOCK", null));
				cell.setBorderWidth(0.0f);
				cell.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				ptg.setTitleCell(cell);
				

				PdfPTable HbiSizeTable = new PdfPTable(1);
				//float widths[] = {25F,25F,25F,25F};
				PdfPTable sizeHeaderTable = new PdfPTable(4);
				//routingheaderTable.setWidths(widths);


				apsSizeTblType = FlexTypeCache.getFlexTypeFromPath(apsSizeTblTypeName);
				
				//Added by UST tfor tech pack correction - 07/18/2012
				cell = createHeaderCell(apsSizeTblType.getAttribute("hbiGarmentSize").getAttDisplay(true));
				sizeHeaderTable.addCell(cell);		
				//-- END --
				//Added by ust for 411541-16
				cell = createHeaderCell(apsSizeTblType.getAttribute("hbiplmPatternSize").getAttDisplay(true));
				sizeHeaderTable.addCell(cell);	
				//Ended for 411541-16
				cell = createHeaderCell(apsSizeTblType.getAttribute("hbiSizeCodeText").getAttDisplay(true));
				sizeHeaderTable.addCell(cell);

				//Commented by UST tfor tech pack correction - 07/18/2012
				/*cell = createHeaderCell(apsSizeTblType.getAttribute("hbiLabelDesc").getAttDisplay(true));
				sizeHeaderTable.addCell(cell);

				cell = createHeaderCell(apsSizeTblType.getAttribute("hbiPackDesc").getAttDisplay(true));
				sizeHeaderTable.addCell(cell);*/
				
				/*cell = createHeaderCell(apsSizeTblType.getAttribute("hbiXSize").getAttDisplay(true));
				sizeHeaderTable.addCell(cell);*/

				//S3 Code
				cell = createHeaderCell(apsSizeTblType.getAttribute("hbiS3CodeText").getAttDisplay(true));
				sizeHeaderTable.addCell(cell);
				
				/*cell = createHeaderCell(apsSizeTblType.getAttribute("hbiRopsCode").getAttDisplay(true));
				sizeHeaderTable.addCell(cell);

				*/
				HbiSizeTable.addCell(new PdfPCell(sizeHeaderTable));

				while(sizeTableAttIter.hasNext()){
					//String sizeCodeInt=""; - commented for sizecode change CA # 38986-14
					PdfPTable sizeDataTable = new PdfPTable(4);
					LCSMOAObject moaObject = (LCSMOAObject)sizeTableAttIter.next();
					
					//Added additional Tech pack requirements - Start 1/9/2019.
					String aps = (String)moaObject.getValue("hbiSizeCodeText");
					String isActiveSize = String.valueOf(moaObject.getValue("hbiActiveSizeMOATable"));
					if(isActiveSize !=null && isActiveSize.equalsIgnoreCase("true")){
					//Added additional Tech pack requirements - End 1/9/2019.

					//Added by UST tfor tech pack correction - 07/18/2012
					garmentSize = (String)moaObject.getValue("hbiGarmentSize");
					cell = createDataCell(garmentSize);
					sizeDataTable.addCell(cell);
					//-- END --
					
					//Added by UST for tech pack correction 411541-16
					plmPatternSize = (String)moaObject.getValue("hbiplmPatternSize");
					cell = createDataCell(plmPatternSize);
					sizeDataTable.addCell(cell);
					//-- END --
					
					//Changed for sizecode change CA # 38986-14
					//sizeCode= moaObject.getValue("hbiSizeCodeText").toString();
					sizeCode= (String)moaObject.getValue("hbiSizeCodeText");
					//sizeCode=sizeCodeInt.substring(0,sizeCodeInt.lastIndexOf("."));
					cell = createDataCell(sizeCode);
					sizeDataTable.addCell(cell);

					//Commented by UST tfor tech pack correction - 07/18/2012
					/*labelDesc = (String)moaObject.getValue("hbiLabelDesc");
					cell = createDataCell(labelDesc);
					sizeDataTable.addCell(cell);

					packDesc =(String)moaObject.getValue("hbiPackDesc");
					cell = createDataCell(packDesc);
					sizeDataTable.addCell(cell);*/
					
                    /*String xSizeInt="";
                    xSizeInt = moaObject.getValue("hbiXSize").toString();
					xSize=xSizeInt.substring(0,xSizeInt.lastIndexOf("."));
					cell = createDataCell(xSize);*/
					
					//S3 Code
					s3Code =(String)moaObject.getValue("hbiS3CodeText");
					cell = createDataCell(s3Code);				
					sizeDataTable.addCell(cell);
					
					/*AttributeValueList ropsCodeAttList = moaObject.getFlexType().getAttribute("hbiRopsCode").getAttValueList();
					ropsCode = ropsCodeAttList.getValue((String)moaObject.getValue("hbiRopsCode"), null);
					cell = createDataCell(ropsCode);
					sizeDataTable.addCell(cell);
					*/

					HbiSizeTable.addCell(new PdfPCell(sizeDataTable));
					garmentSize = " " ; //Added by UST tfor tech pack correction - 07/18/2012
					plmPatternSize = " ";//Added by ust for 411541-16
					sizeCode = " ";
					//Commented by UST tfor tech pack correction - 07/18/2012
					/*labelDesc = " ";
					packDesc = " ";*/
					/*xSize = " ";*/
					ropsCode = " ";
					//s3 Code
					s3Code = " ";
					}
					// Bracket below Added for Tech pack additional requirements 1/9/2019
				}

				return HbiSizeTable;
			
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