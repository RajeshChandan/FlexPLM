
package com.hbi.wc.material;

import com.lcs.wc.util.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import wt.util.WTMessage;
import java.util.*;
import wt.util.WTProperties;
import java.io.File;

import java.util.Calendar;
import java.text.SimpleDateFormat;

import com.lcs.wc.client.web.PDFGeneratorHelper;

public class HBIPDFMaterialSpecPageHeaderGenerator extends PdfPageEventHelper
{
    public String headerText;
	public Font headerFont;
	public Font footerFont;
	public Font pageOfFont;
    public float cellHeight;
	public float headerHeight;
	public float rowMargin;
	public String headerFontClass = "TABLESECTIONHEADER";
	public String pageNumFontClass = "PAGE_NUMBERS";

	public String footFontClass = "FORMLABEL";
	public ArrayList footerList;
	private static final String FOOTER_TEXT = LCSProperties.get("com.hbi.wc.material.specReport.FooterText");
    public static final String webHomeLocation = LCSProperties.get("flexPLM.webHome.location");
    static String wthome;
    static String imageFile;
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public PDFGeneratorHelper pgh = new PDFGeneratorHelper();

    public PdfTemplate tpl;
    public BaseFont bfont ;

	public String tdocs;

	 static 
    {
        wthome = "";
        imageFile = "";
        try
        {
            imageFile = LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationHeader.headerImage", (new StringBuilder()).append(FormatHelper.formatOSFolderLocation(webHomeLocation)).append("/images/flexplm_black.gif").toString());
            wthome = WTProperties.getServerProperties().getProperty("wt.home");
            imageFile = (new StringBuilder()).append(wthome).append(File.separator).append(imageFile).toString();
        }
        catch(Exception e)
        {
            System.out.println("Error initializing cache for ExcelGeneratorHelper");
            e.printStackTrace();
        }
    }

    public HBIPDFMaterialSpecPageHeaderGenerator()
    {      
        headerText = "";
		headerFont = pgh.getCellFont(headerFontClass, null, null);
		pageOfFont = pgh.getCellFont(pageNumFontClass, null, null);
		footerFont = pgh.getCellFont(footFontClass, null, null);
        cellHeight = 15F;
		headerHeight = 35F;
		rowMargin = 3F;
      try{

		 bfont = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
	  }catch(Exception e){
	     throw new ExceptionConverter(e);
	  }
    }



	public void onOpenDocument(PdfWriter writer, Document document) {
        try {
            		         
            tpl = writer.getDirectContent().createTemplate(100, 100);
            tpl.setBoundingBox(new Rectangle(-20, -20, 100, 100));
            // initialization of the font
            bfont = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);

        }
        catch(Exception e) {
            throw new ExceptionConverter(e);
        }
    }





	 /** Sets up the document for having the header written
     * @see com.lowagie.text.pdf.PdfPageEventHelper#onOpenDocument(com.lowagie.text.pdf.PdfWriter, com.lowagie.text.Document)
     * @param writer
     * @param document
     */

    public void onEndPage(PdfWriter writer, Document document)
    {   
	   try
        {
        
			//PdfContentByte cb = writer.getDirectContent();
			PdfContentByte cb = writer.getDirectContent();
            cb.saveState();
            cb.restoreState();

			PdfPTable headerTable = setHeader(writer, document);		
			float headHeight = headerTable.getTotalHeight();

            PdfPTable footerTable = setFooter(writer, document);
            float footHeight = footerTable.getTotalHeight();

			
			//drawing rectangle
			cb.setLineWidth(.50F);
			cb.rectangle (document.left(), document.bottom() - (footHeight + rowMargin) , document.right()-document.left(), document.top()  - document.bottom() + headHeight + footHeight + 2 * rowMargin);
            cb.stroke();
			

			headerTable.writeSelectedRows(0, -1, document.left(), document.top() + headHeight + rowMargin, cb);

			footerTable.writeSelectedRows(0, -1, document.left(), document.bottom() - rowMargin  , cb);   


            bfont = pageOfFont.getCalculatedBaseFont(false);

			//float textSize = font.getWidthPoint(text, 8);
            float textBase = document.getPageSize().getHeight() - 9;
           // for odd pagenumbers, show the footer at the left
           float adjust = bfont.getWidthPoint("000", 8);
			//cb.addTemplate(tpl, document.right() - adjust, textBase);
			cb.addTemplate(tpl, document.right() - adjust, 23.0F);
			cb.saveState();
            cb.restoreState();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
	
    private PdfPTable setHeader(PdfWriter writer, Document document)
	{
	   
	   PdfPTable mainTable = new PdfPTable(1);
	   mainTable.setTotalWidth(document.right() - document.left());  
	   mainTable.setLockedWidth(true);
	   PdfPTable headerTable = new PdfPTable(2);
	  
	  try
	  {
		
        float widths[] = {
          40F, 60F
        };
        headerTable.setWidths(widths);

		Image img = Image.getInstance(imageFile);
        PdfPCell imageCell = new PdfPCell(img, true);
        imageCell.setUseBorderPadding(true);
        imageCell.setPadding(4F);
		imageCell.setBorderWidth(0.0F);
		imageCell.setHorizontalAlignment (Element.ALIGN_LEFT);
		imageCell.setVerticalAlignment(Element.ALIGN_LEFT);
        imageCell.setFixedHeight(headerHeight);
        headerTable.addCell(imageCell);
      
		PdfPCell dataCell = new PdfPCell(pgh.multiFontPara(headerText, headerFont));

		dataCell.setUseBorderPadding(true);
		dataCell.setPadding(4F);
		dataCell.setBorderWidth(0.0F);
		dataCell.setHorizontalAlignment (Element.ALIGN_LEFT);
		dataCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		dataCell.setFixedHeight(headerHeight);
		
        headerTable.addCell(dataCell);
		
		PdfPCell cell = new PdfPCell(headerTable);
		mainTable.addCell(cell);
	  }
	  catch(Exception e)
	  {
		   e.printStackTrace();  
	  }	

	  return mainTable;
	}

	private PdfPTable setFooter(PdfWriter writer, Document document)
	{
	   
	   PdfPTable mainTable = new PdfPTable(1);
	   mainTable.setTotalWidth(document.right() - document.left());  
	   mainTable.setLockedWidth(true);
	   //float[] widths = {60.0F, 40.0F};
	   float[] widths = {60.0F, 30.0F,8.0F,2.0F};
	   PdfPTable footerTable = new PdfPTable(widths);
	   try
	   {
		     //float widths[] = {60F, 40F};
            // footerTable.setWidths(widths);
			 //footerTable.setWidths();
		
			 PdfPCell confCell = new PdfPCell(pgh.multiFontPara(FOOTER_TEXT, footerFont));
			 confCell.setColspan(4);
			 confCell.setUseBorderPadding(true);
			 //confCell.setPadding(4.0F);
			 confCell.setHorizontalAlignment (Element.ALIGN_CENTER);
			 confCell.setVerticalAlignment(Element.ALIGN_TOP);
			 confCell.setBorder(0);
			 confCell.setFixedHeight(cellHeight);
			 footerTable.addCell(confCell);	

			 Calendar cal = Calendar.getInstance();
			 SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			 PdfPCell dateCell = new PdfPCell(pgh.multiFontPara("Date  : " + sdf.format(cal.getTime()), pageOfFont));
			 dateCell.setUseBorderPadding(true);
			 //dateCell.setPadding(4F);
			 dateCell.setHorizontalAlignment (Element.ALIGN_LEFT);
			 dateCell.setVerticalAlignment(Element.ALIGN_TOP);
			 dateCell.setFixedHeight(cellHeight);
			 dateCell.setBorder(0);
			 footerTable.addCell(dateCell);	

			 Object objB[] = {
					Integer.toString(writer.getPageNumber())
				};
			 String text = WTMessage.getLocalizedMessage("com.lcs.wc.resource.MainRB", "pageOf_LBL", objB);     
			 //PdfPCell pageOf = new PdfPCell(pgh.multiFontPara(text + writer.getPageNumber(), pageOfFont));
			 PdfPCell pageOf = new PdfPCell(pgh.multiFontPara(text, pageOfFont));
 			 pageOf.setColspan(2);
			 pageOf.setUseBorderPadding(true);
			 //pageOf.setPadding(4F);
			 pageOf.setHorizontalAlignment (Element.ALIGN_RIGHT);
			 //pageOf.setHorizontalAlignment (Element.ALIGN_CENTER);
			 pageOf.setVerticalAlignment(Element.ALIGN_TOP);
			 pageOf.setFixedHeight(cellHeight);
			 pageOf.setBorder(0);
			 footerTable.addCell(pageOf);

			 PdfPCell emptycell = new PdfPCell();
			 emptycell.setBorder(0);
 			 footerTable.addCell(emptycell);
			
			 
			 PdfPCell cell = new PdfPCell(footerTable);
			 mainTable.addCell(cell);
	  }
	  catch(Exception e)
	  {
		   e.printStackTrace();  
	  }	
	   return mainTable;	
	}	

	public void onCloseDocument(PdfWriter writer, Document document) {
       
		tpl.setColorFill(pgh.getColor(pageNumFontClass));
        tpl.setColorStroke(pgh.getColor(pageNumFontClass));
        tpl.beginText();
        tpl.setFontAndSize(bfont, 8);
        tpl.setTextMatrix(0, -1);
        tpl.showText("" + (writer.getPageNumber() - 1));
        tpl.endText();
		
    }
    
}