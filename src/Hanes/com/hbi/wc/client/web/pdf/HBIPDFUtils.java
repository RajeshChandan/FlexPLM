package com.hbi.wc.client.web.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;

import wt.httpgw.GatewayServletHelper;
import wt.httpgw.URLFactory;
import wt.util.WTException;
import wt.util.WTProperties;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import com.lcs.wc.document.FileRenamer;
import com.lcs.wc.util.FileLocation;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;


/*
 * This is a custom Utility class used to Rename the Generated PDF Report Name as per Hanes Brands Request Done on 02 August 2015 by BSC
 */


public class HBIPDFUtils {
    static String baseUrl = "";
    static String authgwUrl = "";    
	private static final boolean USE_RELATIVE_URL = LCSProperties.getBoolean("com.lcs.wc.client.web.pdf.PDFUtils.useRelativeURL");
	static{
        try{
            baseUrl = LCSProperties.get("com.lcs.wc.client.web.PDFGenerator.webFolder");

            java.net.URL authgwURL = GatewayServletHelper.buildAuthenticatedURL(new URLFactory());
            authgwUrl =(USE_RELATIVE_URL)?authgwURL.getPath():authgwURL.toString();

        }
        catch(Exception e){}
    }
    public static final String defaultCharsetEncoding = LCSProperties.get("com.lcs.wc.util.CharsetFilter.Charset","UTF-8");

	//Paper size options    
    public static int A0 = 0;
    public static int A1 = 1;
    public static int A2 = 2;
    public static int A3 = 3;
    public static int A4 = 4;
    public static int A5 = 5;
    public static int A6 = 6;
    public static int A7 = 7;
    public static int A8 = 8;
    public static int A9 = 9;
    public static int A10 = 10;
    public static int ARCH_A = 11;
    public static int ARCH_B = 12;
    public static int ARCH_C = 13;
    public static int ARCH_D = 14;
    public static int ARCH_E = 15;
    public static int B0 = 16;
    public static int B1 = 17;
    public static int B2 = 18;
    public static int B3 = 19;
    public static int B4 = 20;
    public static int B5 = 21;
    public static int FLSA = 22;
    public static int FLSE = 23;
    public static int HALFLETTER = 24;
    public static int LEDGER = 25;
    public static int LEGAL = 26;
    public static int LETTER = 27;
    public static int NOTE = 28;
    public static int _11X17 = 29;
    
    public static int paperSize = LETTER;	
	
    /** Sets a documents page size.
     * 
     * @param doc
     * @param paperSize
     * @param landscape
     */
    public static void setPrintOptions(Document doc, int paperSize, boolean landscape){
        Rectangle ps = PageSize.LETTER;
        if(paperSize == A0){
            ps = PageSize.A0;
        }
        if(paperSize == A1){
            ps = PageSize.A1;
        }
        if(paperSize == A2){
            ps = PageSize.A2;
        }
        if(paperSize == A3){
            ps = PageSize.A3;
        }
        if(paperSize == A4){
            ps = PageSize.A4;
        }
        if(paperSize == A5){
            ps = PageSize.A5;
        }
        if(paperSize == A6){
            ps = PageSize.A6;
        }
        if(paperSize == A7){
            ps = PageSize.A7;
        }
        if(paperSize == A8){
            ps = PageSize.A8;
        }
        if(paperSize == A9){
            ps = PageSize.A9;
        }
        if(paperSize == A10){
            ps = PageSize.A10;
        }
        if(paperSize == ARCH_A){
            ps = PageSize.ARCH_A;
        }
        if(paperSize == ARCH_B){
            ps = PageSize.ARCH_B;
        }
        if(paperSize == ARCH_C){
            ps = PageSize.ARCH_C;
        }
        if(paperSize == ARCH_D){
            ps = PageSize.ARCH_D;
        }
        if(paperSize == ARCH_E){
            ps = PageSize.ARCH_E;
        }
        if(paperSize == B0){
            ps = PageSize.B0;
        }
        if(paperSize == B1){
            ps = PageSize.B1;
        }
        if(paperSize == B2){
            ps = PageSize.B2;
        }
        if(paperSize == B3){
            ps = PageSize.B3;
        }
        if(paperSize == B4){
            ps = PageSize.B4;
        }
        if(paperSize == B5){
            ps = PageSize.B5;
        }
        if(paperSize == FLSA){
            ps = PageSize.FLSA;
        }
        if(paperSize == FLSE){
            ps = PageSize.FLSE;
        }
        if(paperSize == HALFLETTER){
            ps = PageSize.HALFLETTER;
        }
        if(paperSize == LEDGER){
            ps = PageSize.LEDGER;
        }
        if(paperSize == LEGAL){
            ps = PageSize.LEGAL;
        }
        if(paperSize == LETTER){
            ps = PageSize.LETTER;
        }
        if(paperSize == NOTE){
            ps = PageSize.NOTE;
        }
        if(paperSize == _11X17){
            ps = PageSize._11X17;
        }
        
        if(landscape){
            doc.setPageSize(ps.rotate());
        }
        else{
            doc.setPageSize(ps);
        }
        
    }
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Generates a somewhat unique file 'stub' name for a report using a time stamp
     * a user name, and the report name.
     * @return
     */
    public static String generateStubName(String reportName, String userName){
        SimpleDateFormat format = new SimpleDateFormat("MMddyyyy_hhmm");
        String time = format.format(new java.util.Date());
        
		/*
		 * Removed the User Name for Report Name as per Hanes Brands Request Done on 02 August 2015
		 */
        String fname = FormatHelper.formatRemoveProblemFileNameChars(reportName + "_" + time);
        return fname;
    }
    ///////////////////////////////////////////////////////////////////////////
    /** Determines a files full directory/file name location given a base filename.
     * Handles duplicate filename by appending a number to the end of the file if
     * needed.
     */
    public static String generateFileName(String filename){
        int count = 1;
        
        String temp = filename;
        File file = new File(FileLocation.PDFDownloadLocationFiles + temp + ".pdf");
        while(file.exists()){
            temp = filename + "_" + count;
            count++;
            
            file = new File(FileLocation.PDFDownloadLocationFiles + temp + ".pdf");
        }
        filename = temp;
        return temp + ".pdf";
    }	
    
    public static String getDownloadURL(String filename){
        String url;
        try {
            String fileOutName = java.net.URLEncoder.encode(filename, defaultCharsetEncoding) + ".pdf";
            
            // has trailing file.separator
            String filepath = FormatHelper.formatOSFolderLocation(FileLocation.PDFDownloadLocationFiles);
            
            // Set the URL for this generated temp file
            url = filepath + fileOutName;
            
        } catch(java.io.UnsupportedEncodingException ex){
            ex.printStackTrace();
            url = "ErrorDuringPDFGeneration.pdf";
        }
        return url;
    }    
    ///////////////////////////////////////////////////////////////////////////    
	
    public static PdfPTable prepareElement(Element e){
        if(e instanceof PdfPTable){
            return ((PdfPTable)e);
        }
        
        PdfPCell cell = new PdfPCell();
        cell.addElement(e);
        
        PdfPTable table = new PdfPTable(1);
        table.addCell(cell);
        
        return table;
    }
}
