/*
 * HBISizePDFContent.java
 *
 * Created on November 13, 2006, 12:58 PM
 */

package com.hbi.wc.flexbom.gen;

import java.util.*;
import java.util.List;

import com.lcs.wc.flexbom.*;
import com.lcs.wc.util.*;
import com.lcs.wc.product.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import wt.part.WTPartMaster;
import com.lcs.wc.part.LCSPart;

import wt.util.*;
import com.lcs.wc.flexbom.gen.*;
/**
 *
 * @author  Chuck
 */
public class HBISizePDFContent extends BOMPDFContentGenerator {
    
    private static final boolean DEBUG = LCSProperties.getBoolean("com.lcs.wc.flexbom.gen.SizePDFContent.verbose");
    private static final int DEBUG_LEVEL = Integer.parseInt(LCSProperties.get("com.lcs.wc.flexbom.gen.SizePDFContent.verboseLevel", "1"));
    public float tableWidthPercent = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.SingleBOMPDFContent.tableWidthPercent", "95.0"))).floatValue();
	private static final String BASIC_CUT_AND_SEW_SELLING = "Product\\BASIC CUT & SEW - SELLING";
	private static final String SELLING_BOM_TYPE_PATH = "BOM\\Materials\\HBI\\Selling\\Routing,BOM\\Materials\\HBI\\Selling\\Packaging,BOM\\Materials\\HBI\\Selling\\Casing,BOM\\Materials\\HBI\\Selling\\Pack Case BOM";
	
    /** Creates a new instance of SizePDFContent */
    public HBISizePDFContent() {
    }
    
    /** gets an Element for insertion into a PDF Document
     * @param params A Map of parameters to pass to the Object.  This provides the means for the
     * calling class to have some "fore" knowledge of what implementations are being used
     * and pass appropriate parameters.
     * @param document The PDF Document which the content is going to be added to.  The document is
     * passed in order to provide additional information related to the Document itself
     * incase it is not provided in the params
     * @throws WTException For any error
     * @return an Element for insertion into a Document
     */
    public Collection getPDFContentCollection(Map params, Document document) throws WTException 
	{
        debug(1, "SizePDFContent.getPDFContentCollection()");
        Collection content = new ArrayList();
        Collection spcontent = new ArrayList();
        Collection sectionPageTitles = new ArrayList();
		debug("Size report Params:"+params);
        HBISizeGenerator bomDG = new HBISizeGenerator();
        FlexBOMPart bomPart = (FlexBOMPart)params.get(BOMPDFContentGenerator.BOM_PART);
		boolean BOMTechpackStatus = generateBOMTechpackStatus(bomPart);
		if(BOMTechpackStatus)
		{
		
        Map tparams = new HashMap(params.size() + 3);
        tparams.putAll(params);
        boolean usingSize1 = true;
        debug("USE_SIZE1_SIZE2::"+(String)params.get(BomDataGenerator.USE_SIZE1_SIZE2));
        debug("SIZES1:"+(Collection)params.get(BomDataGenerator.SIZES1));
        debug("SIZES2:"+(Collection)params.get(BomDataGenerator.SIZES2));
        if("size2".equalsIgnoreCase((String)params.get(BomDataGenerator.USE_SIZE1_SIZE2)) ) {
            usingSize1 = false;
        }
        Collection allSizes = new ArrayList();
        if(usingSize1) {
            allSizes = (Collection)params.get(BomDataGenerator.SIZES1);
        } else {
            allSizes = (Collection)params.get(BomDataGenerator.SIZES2);
        }
        int maxPerPage = ((Integer)params.get(PDFProductSpecificationGenerator2.SIZES_PER_PAGE)).intValue();
        
        Collection sections = bomPart.getFlexType().getAttribute("section").getAttValueList().getSelectableKeys(com.lcs.wc.client.ClientContext.getContext().getLocale(), true);
        String section = "";
        Iterator sectionIter = sections.iterator();
        Iterator sizeIt = null;
        
        //Create collection of arrayLists of skus
        Collection sizesArray = splitItems(allSizes, maxPerPage);
        debug("Splited sizes array::"+sizesArray);
        while(sectionIter.hasNext()) {
            section = (String)sectionIter.next();
            if(DEBUG){ debug("-adding section :  " + section);}
            tparams.put(BomDataGenerator.SECTION, section);
            setSectionViewId(tparams);
            sizeIt= sizesArray.iterator();
            
            Collection sizesThisRun = new ArrayList();
            
            while(sizeIt.hasNext()) {
                sizesThisRun = (Collection)sizeIt.next();
                if(usingSize1) {
                    tparams.put(BomDataGenerator.SIZES1, sizesThisRun);
                } else {
                    tparams.put(BomDataGenerator.SIZES2, sizesThisRun);
                }
                bomDG.init(tparams);
                Collection data = bomDG.getBOMData();
                Collection columns = bomDG.getTableColumns();
                
                spcontent.addAll(generatePDFPage(data, columns, document, tparams));
                if(!BOM_ON_SINGLE_PAGE) {
                    sectionPageTitles.add(getPageTitleText(tparams));
                }
            }
        }
        
        PdfPTable fullBOMTable = new PdfPTable(1);
        PdfPTable e = null;
        PdfPCell cell = null;
        fullBOMTable.setWidthPercentage(tableWidthPercent);
        //Add the BOM Header Attributes
        if(FormatHelper.parseBoolean((String)params.get(PDFProductSpecificationBOM2.PRINT_BOM_HEADER) )){
            debug(2, "FormatHelper.parseBoolean((String)params.get(PDFProductSpecificationBOM2.PRINT_BOM_HEADER) )");
            Collection BomHeaderAtts = (Collection)params.get(PDFProductSpecificationBOM2.BOM_HEADER_ATTS);
            if(BomHeaderAtts != null && !BomHeaderAtts.isEmpty()){
                debug(2, "BomHeaderAtts != null && !BomHeaderAtts.isEmpty()");
                if(PDFProductSpecificationBOM2.BOM_HEADER_SAME_PAGE  && BOM_ON_SINGLE_PAGE){
                    debug(2, "PDFProductSpecificationBOM2.BOM_HEADER_SAME_PAGE  && BOM_ON_SINGLE_PAGE");
                    for(Iterator HeaderI = BomHeaderAtts.iterator(); HeaderI.hasNext();){
                        e = (PdfPTable)HeaderI.next();
                        cell = new PdfPCell(e);
                        fullBOMTable.addCell(cell);
                    }
                }else{
                    debug(2, "NOT--PDFProductSpecificationBOM2.BOM_HEADER_SAME_PAGE  && BOM_ON_SINGLE_PAGE");
                    content.add(BomHeaderAtts);
                    this.pageTitles.addAll((Collection)params.get(PDFProductSpecificationBOM2.BOM_HEADER_PAGE_TITLES));
                }
            }
        }
        
        //Add BOM Sections
        Collection BOMFooter = (Collection)params.get(PDFProductSpecificationBOM2.BOM_FOOTER_ATTS);
        boolean usingFooter = (BOMFooter != null && !BOMFooter.isEmpty() &&FormatHelper.parseBoolean((String)params.get(PDFProductSpecificationBOM2.PRINT_BOM_FOOTER) ));
        if(BOM_ON_SINGLE_PAGE){
            debug(2, "BOM_ON_SINGLE_PAGE");
            Iterator sci = spcontent.iterator();
            while(sci.hasNext()){
                e = (PdfPTable )sci.next();
                cell = new PdfPCell(e);
                fullBOMTable.addCell(cell);
            }
            
            //Add Footer
            if (usingFooter){
                debug(2, "usingFooter");
                if(PDFProductSpecificationBOM2.BOM_FOOTER_SAME_PAGE){
                    for(Iterator footI = BOMFooter.iterator();footI.hasNext();) {
                        e = (PdfPTable)footI.next();
                        cell =new PdfPCell(e);
                        fullBOMTable.addCell(cell);
                    }
                    //Add BOM to content
                    content.add(fullBOMTable);
                    this.pageTitles.add(getPageTitleText(tparams));
                }else{
                    //Add BOM to content
                    content.add(fullBOMTable);
                    this.pageTitles.add(getPageTitleText(tparams));
                    //Add Footer to content
                    content.addAll(BOMFooter);
                    this.pageTitles.addAll((Collection)params.get(PDFProductSpecificationBOM2.BOM_FOOTER_PAGE_TITLES) );
                }
            } else {
                content.add(fullBOMTable);
                this.pageTitles.add(getPageTitleText(tparams));
            }
        }else { //BOM sections different pages
            this.pageTitles.addAll(sectionPageTitles);
            Iterator sci = spcontent.iterator();
            //Add the first section to the fullBOMTable in case we have a Header
            e = (PdfPTable)sci.next();
            cell = new PdfPCell(e);
            fullBOMTable.addCell(cell);
            content.add(fullBOMTable);
            while(sci.hasNext()){
                e = (PdfPTable)sci.next();
                if(!sci.hasNext() && usingFooter){
                    //Last element && using a footer
                    if(PDFProductSpecificationBOM2.BOM_FOOTER_SAME_PAGE){
                        fullBOMTable = new PdfPTable(1);
                        cell = new PdfPCell(e);
                        fullBOMTable.addCell(cell);
                        for(Iterator footI = BOMFooter.iterator();footI.hasNext();) {
                            e = (PdfPTable)footI.next();
                            cell =new PdfPCell(e);
                            fullBOMTable.addCell(cell);
                        }
                        content.add(fullBOMTable);
                    } else {
                        //Add last element
                        content.add(e);
                        //Add Footer
                        content.addAll(BOMFooter);
                        this.pageTitles.addAll((Collection)params.get(PDFProductSpecificationBOM2.BOM_FOOTER_PAGE_TITLES) );
                    }
                }else{
                    //Not the last element
                    content.add(e);
                }
            } //while
        }
	}
        
        return content;
		
    }
   /////////////////////////////////////////////////////////////////////////////
   public static void debug(String msg){debug(msg, 1); }
   public static void debug(int i, String msg){debug(msg, i); }
   public static void debug(String msg, int i){
	  if(DEBUG && i <= DEBUG_LEVEL) debug(msg);
   }
   
   /**
     * This function is using to print only Routing,Packing and Casing BOM for Selling Product.
     * @param bomPart - FlexBOMPart
     * @return bomTachPackStatus - boolean
     * @throws WTException
     */
   public boolean generateBOMTechpackStatus(FlexBOMPart bomPart) throws WTException
   {
		boolean bomTachPackStatus = true;
		LCSProduct prodObj = null;
		
		String bomPartFlexTypePath = bomPart.getFlexType().getFullName(true);
//		WTPartMaster wtPartMaster = bomPart.getOwnerMaster();
//		LCSPart lcsPart = (LCSPart)VersionHelper.latestIterationOf(wtPartMaster);
		LCSPart lcsPart = (LCSPart) VersionHelper.latestIterationOf(bomPart.getOwnerMaster());
	
		if(lcsPart instanceof LCSProduct)
		{
			prodObj = (LCSProduct)lcsPart;
			String productFlexTypePath = prodObj.getFlexType().getFullName(true);
			
			if(BASIC_CUT_AND_SEW_SELLING.equalsIgnoreCase(productFlexTypePath) && !SELLING_BOM_TYPE_PATH.contains(bomPartFlexTypePath))
			{
				debug(" <<<<<< SIZE bomPart >>>>>>>" +bomPart.getName());
				bomTachPackStatus = false;
			}
		}
		return bomTachPackStatus;
    }
}
