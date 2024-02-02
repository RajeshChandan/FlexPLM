/*
 * HBISingleBOMPDFContent.java
 *
 * Created on November 17, 2006, 2:40 PM
 */

package com.hbi.wc.flexbom.gen;

import java.util.*;

import com.lcs.wc.flexbom.*;
import com.lcs.wc.foundation.*;
import com.lcs.wc.sizing.*;
import com.lcs.wc.product.*;
import com.lcs.wc.util.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lcs.wc.part.LCSPart;
import com.lcs.wc.part.LCSPartMaster;

import wt.util.*;
//import wt.part.WTPartMaster;
import com.lcs.wc.util.LCSProperties;

import com.lcs.wc.flexbom.gen.*;

/**
 *
 * @author  Chuck
 */
public class HBISingleBOMPDFContent extends BOMPDFContentGenerator
    {
    private static final boolean DEBUG = LCSProperties.getBoolean("com.lcs.wc.flexbom.gen.SingleBOMPDFContent.verbose");
    public float tableWidthPercent = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.SingleBOMPDFContent.tableWidthPercent", "95.0"))).floatValue();
	private static final String BASIC_CUT_AND_SEW_SELLING = "Product\\BASIC CUT & SEW - SELLING";
	private static final String SELLING_BOM_TYPE_PATH = "BOM\\Materials\\HBI\\Selling\\Routing,BOM\\Materials\\HBI\\Selling\\Packaging,BOM\\Materials\\HBI\\Selling\\Casing,BOM\\Materials\\HBI\\Selling\\Pack Case BOM";
	@SuppressWarnings("rawtypes")
	private Map titleCache = new HashMap();
    private static final String SIZE1LABEL = "SIZE1LABEL";
    private static final String SIZE2LABEL = "SIZE2LABEL";
    
    /** Creates a new instance of SingleBOMPDFContent */
    public HBISingleBOMPDFContent() {
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
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection getPDFContentCollection(Map params, Document document) throws WTException {
        ArrayList content = new ArrayList();
        ArrayList spcontent = new ArrayList();
		
		FlexBOMPart bomPart1 = (FlexBOMPart)params.get(BOMPDFContentGenerator.BOM_PART);
		boolean BOMTechpackStatus = generateBOMTechpackStatus(bomPart1);
		System.out.println("I am inside SINGLE BOM" +BOMTechpackStatus);
		if(BOMTechpackStatus)
		{
        //Add the BOM Header Attributes
        if(FormatHelper.parseBoolean((String)params.get(PDFProductSpecificationBOM2.PRINT_BOM_HEADER) )){
            Collection BomHeaderAtts = (Collection)params.get(PDFProductSpecificationBOM2.BOM_HEADER_ATTS);
            if(BomHeaderAtts != null && !BomHeaderAtts.isEmpty()){
                if(PDFProductSpecificationBOM2.BOM_HEADER_SAME_PAGE){
                    if(BOM_ON_SINGLE_PAGE){
                        spcontent.addAll(BomHeaderAtts);
                    }else{
                        content.addAll(BomHeaderAtts);
                        this.pageTitles.addAll((Collection)params.get(PDFProductSpecificationBOM2.BOM_HEADER_PAGE_TITLES));
                    }
                }else{
                    content.add(BomHeaderAtts);
                    this.pageTitles.addAll((Collection)params.get(PDFProductSpecificationBOM2.BOM_HEADER_PAGE_TITLES));
                }
            }
        }
        
        Collection sources = (Collection)params.get(BomDataGenerator.SOURCES);
        Collection dests = (Collection)params.get(BomDataGenerator.DESTINATIONS);
        Collection  colorways = (Collection)params.get(BomDataGenerator.COLORWAYS);
        Collection sizes1 = (Collection)params.get(BomDataGenerator.SIZES1);
        Collection sizes2 = (Collection)params.get(BomDataGenerator.SIZES2);
        FlexBOMPart bomPart = (FlexBOMPart)params.get(BOMPDFContentGenerator.BOM_PART);
        Collection sections = bomPart.getFlexType().getAttribute("section").getAttValueList().getSelectableKeys(com.lcs.wc.client.ClientContext.getContext().getLocale(), true);
        
        HashMap tparams = new HashMap(params.size() +6);
        tparams.putAll(params);
        
        //If there is no collection add a null value so the iterator works
        if(sources == null ||sources.size() <1 ) {
            sources = new ArrayList();
            sources.add(null);
        }
        
        if(dests == null || dests.size() < 1) {
            dests = new ArrayList();
            dests.add(null);
        }
        if(colorways == null || colorways.size() < 1) {
            colorways = new ArrayList();
            colorways.add(null);
        }
        if(sizes1 == null || sizes1.size() < 1) {
            sizes1 = new ArrayList();
            sizes1.add(null);
        }
        if(sizes2 == null || sizes2.size() < 1) {
            sizes2 = new ArrayList();
            sizes2.add(null);
        }
        Iterator sourcesIt = sources.iterator();
        Iterator destsIt;
        Iterator colorwayIt;
        Iterator sizes1It;
        Iterator sizes2It;
        Iterator sectionIter;
        
        String source = "";
        String dest = "";
        String colorway = "";
        String size1 = "";
        String size2 = "";
        String section = "";
        
        while (sourcesIt.hasNext()) {
            source = (String)sourcesIt.next();
            tparams.put(HBISingleBOMGenerator.SOURCE_ID, source);
            destsIt = dests.iterator();
            
            while(destsIt.hasNext()) {
                dest = (String)destsIt.next();
                tparams.put(HBISingleBOMGenerator.DESTINATION_ID, dest);
                colorwayIt = colorways.iterator();
                
                while(colorwayIt.hasNext()) {
                    colorway = (String)colorwayIt.next();
                    tparams.put(HBISingleBOMGenerator.SKU_ID, colorway);
                    sizes1It = sizes1.iterator();
                    
                    while(sizes1It.hasNext()) {
                        size1 = (String)sizes1It.next();
                        //System.out.println("size1: " + size1);
                        tparams.put(HBISingleBOMGenerator.SIZE1_VAL, size1);
                        sizes2It = sizes2.iterator();
                        
                        while(sizes2It.hasNext()){
                            size2 = (String)sizes2It.next();
                            //System.out.println("size2: " + size2);
                            tparams.put(HBISingleBOMGenerator.SIZE2_VAL, size2);
                            sectionIter = sections.iterator();
                            
                            while(sectionIter.hasNext()){
                                section = (String)sectionIter.next();
                                tparams.put(BomDataGenerator.SECTION, section);
                                setSectionViewId(tparams);
                                if(BOM_ON_SINGLE_PAGE) {
                                    spcontent.addAll(generateSingleBOMPage(tparams, document));
                                } else {
                                    Collection sbp = generateSingleBOMPage(tparams, document);
                                    Iterator sbpi = sbp.iterator();
                                    PdfPTable stable = null;
                                    while(sbpi.hasNext()){
                                        stable = (PdfPTable)sbpi.next();
                                        stable.setWidthPercentage(tableWidthPercent);
                                    }
                                    content.addAll(sbp);
                                    this.pageTitles.add(getPageTitleText(tparams));
                                }
                            }//End section iterator
                            //Footer
                            if (FormatHelper.parseBoolean((String)params.get(PDFProductSpecificationBOM2.PRINT_BOM_FOOTER) )){
                                Collection BOMFooter = (Collection)params.get(PDFProductSpecificationBOM2.BOM_FOOTER_ATTS);
                                if(BOMFooter != null && !BOMFooter.isEmpty()){
                                    if(PDFProductSpecificationBOM2.BOM_FOOTER_SAME_PAGE){
                                        if(BOM_ON_SINGLE_PAGE){
                                            spcontent.addAll(BOMFooter);
                                        }else{
                                            content.addAll(BOMFooter);
                                            this.pageTitles.addAll((Collection)params.get(PDFProductSpecificationBOM2.BOM_FOOTER_PAGE_TITLES) );
                                        }
                                    }else{
                                        content.add(BOMFooter);
                                        this.pageTitles.addAll((Collection)params.get(PDFProductSpecificationBOM2.BOM_FOOTER_PAGE_TITLES) );
                                    }
                                    
                                }
                            }
                            
                            if(BOM_ON_SINGLE_PAGE) {
                                PdfPTable fullBOMTable = new PdfPTable(1);
                                fullBOMTable.setWidthPercentage(tableWidthPercent);
                                
                                Iterator sci = spcontent.iterator();
                                PdfPTable e = null;
                                PdfPCell cell = null;
                                while(sci.hasNext()){
                                    e = (PdfPTable )sci.next();
                                    cell = new PdfPCell(e);
                                    
                                    fullBOMTable.addCell(cell);
                                }
                                
                                content.add(fullBOMTable);
                                
                                this.pageTitles.add(getPageTitleText(tparams));
                                spcontent = new ArrayList();
                            }
                        }//End sizes2 iterator
                    }//End Sizes1 iterator
                } //End Colorway iterator
            }//End Dests iterator
        }//Emd Source iterator
	}	
        return content;
    }
    
    @SuppressWarnings("rawtypes")
	public Collection generateSingleBOMPage(Map params, Document document) throws WTException {
        if(DEBUG){
            //params.remove("RAW_DATA");
            //System.out.println("generateSingleBOMPage():  params- "  + params);
        }
        HBISingleBOMGenerator bomDG = new HBISingleBOMGenerator();
        //System.out.println("gsbp - size1: " + params.get(HBISingleBOMGenerator.SIZE1_VAL));
        //System.out.println("gsbp - size2: " + params.get(HBISingleBOMGenerator.SIZE2_VAL));
        bomDG.init(params);
        //Collection data = new ArrayList();
        Collection data = bomDG.getBOMData();
        Collection columns = bomDG.getTableColumns();
        
        return generatePDFPage(data, columns, document, params);
    }
    /** Returns the text to use as the page title
     */
    @SuppressWarnings("rawtypes")
	public String getPageTitleText(Map params) {
        String title = "";
        FlexBOMPart bomPart = (FlexBOMPart)params.get(BOMPDFContentGenerator.BOM_PART);
        
        title = bomPart.getName() + " -- " + params.get(PDFProductSpecificationGenerator2.REPORT_NAME);
        
        return title;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public String getTitleCellCenterText(Map params) throws WTException{
        String centerText= "";
        
		System.out.println("I am inside SINGLE BOM  getTitleCellCenterText");
        //Add destination label
        String destinationLabel = WTMessage.getLocalizedMessage( RB.FLEXBOM, "destination_LBL", RB.objA ) ;
        String destinationName = (String)this.titleCache.get(params.get(HBISingleBOMGenerator.DESTINATION_ID));
        String destId = (String)params.get(HBISingleBOMGenerator.DESTINATION_ID);
        if(!FormatHelper.hasContent(destinationName) && FormatHelper.hasContent(destId) ) {
            if(DEBUG) {System.out.println("--Looking up destinationName:  " + destId);}
            if(destId.indexOf("ProductDestination") < 0){
                destId = "OR:com.lcs.wc.product.ProductDestination:" + destId;
            }
            ProductDestination dest = (ProductDestination) LCSQuery.findObjectById(destId);
            destinationName = dest.getDestinationName();
            titleCache.put(params.get(HBISingleBOMGenerator.DESTINATION_ID), destinationName);
        }
        if(FormatHelper.hasContent(destinationName)) {
            centerText = centerText + destinationLabel + destinationName ;
        }
        
        //Add colorway Label
        String colorwayLabel = WTMessage.getLocalizedMessage( RB.FLEXBOM, "colorwayColon_LBL", RB.objA ) ;
        String colorwayName = (String)titleCache.get(params.get(HBISingleBOMGenerator.SKU_ID));
        String skuId = (String)params.get(HBISingleBOMGenerator.SKU_ID);
        if(!FormatHelper.hasContent(colorwayName) && FormatHelper.hasContent(skuId)) {
            String skuMasterId = "";
            if(skuId.indexOf("LCSSKU") > -1){
                LCSSKU sku = (LCSSKU)LCSQuery.findObjectById(skuId);
                skuMasterId = FormatHelper.getNumericObjectIdFromObject((LCSPartMaster)sku.getMaster());
            }
            else if(skuId.indexOf("LCSPartMaster") > -1){
                skuMasterId = FormatHelper.getNumericFromOid(skuId);
            }
            else{
                skuMasterId = skuId;
            }
            LCSSKU skuArev = LCSSKUQuery.getSKURevA(skuMasterId);
            colorwayName = skuArev.getName();
            
            titleCache.put(params.get(HBISingleBOMGenerator.SKU_ID), colorwayName);
        }
        if(FormatHelper.hasContent(colorwayName)) {
            if(FormatHelper.hasContent(centerText)){
                centerText = centerText + ", ";
            }
            centerText = centerText + colorwayLabel + colorwayName;
        }
        
        ProductSizeCategory productSizeCategory;
        //Add Size1 label
        String size1Value = (String)params.get(HBISingleBOMGenerator.SIZE1_VAL);
        if(FormatHelper.hasContent(size1Value)) {
            String size1Label = (String)titleCache.get(SIZE1LABEL);
            if(!FormatHelper.hasContent(size1Label)){
                if(FormatHelper.hasContent((String)params.get(PDFProductSpecificationGenerator2.PRODUCT_SIZE_CAT_ID))){
                    productSizeCategory = (ProductSizeCategory)LCSQuery.findObjectById((String)params.get(PDFProductSpecificationGenerator2.PRODUCT_SIZE_CAT_ID));
                    size1Label = productSizeCategory.getSizeRange().getFullSizeRange().getSize1Label() + ":  ";
                } else {
                    size1Label = WTMessage.getLocalizedMessage( RB.QUERYDEFINITION, "size1", RB.objA ) ;
                }
                titleCache.put(SIZE1LABEL, size1Label);
            }
            if(FormatHelper.hasContent(centerText)){
                centerText = centerText + ", ";
            }
            centerText = centerText + size1Label + size1Value;
        }
        
        //Add size 2 label
        String size2Value = (String)params.get(HBISingleBOMGenerator.SIZE2_VAL);
        if(FormatHelper.hasContent(size2Value)){
            String size2Label = (String)titleCache.get(SIZE2LABEL);
            if(!FormatHelper.hasContent(size2Label)){
                if(FormatHelper.hasContent((String)params.get(PDFProductSpecificationGenerator2.PRODUCT_SIZE_CAT_ID))){
                    productSizeCategory = (ProductSizeCategory)LCSQuery.findObjectById((String)params.get(PDFProductSpecificationGenerator2.PRODUCT_SIZE_CAT_ID));
                    size2Label = productSizeCategory.getSizeRange().getFullSizeRange().getSize2Label() + ":  ";
                } else {
                    size2Label = WTMessage.getLocalizedMessage( RB.QUERYDEFINITION, "size1", RB.objA ) ;
                }
                titleCache.put(SIZE2LABEL, size2Label);
            }
            if(FormatHelper.hasContent(centerText)){
                centerText = centerText + ", ";
            }
            centerText = centerText + size2Label + size2Value;
        }
        
        if(!FormatHelper.hasContent(centerText)) {
            centerText = WTMessage.getLocalizedMessage( RB.FLEXBOM, "topLevelBOM_LBL", RB.objA ) ;
        }
        
        return centerText;
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
			System.out.println(" <<<<<< BEFORE ENTRING  SINGLE bomPart >>>>>>>" +!SELLING_BOM_TYPE_PATH.contains(bomPartFlexTypePath));
			if(BASIC_CUT_AND_SEW_SELLING.equalsIgnoreCase(productFlexTypePath) && !SELLING_BOM_TYPE_PATH.contains(bomPartFlexTypePath))
			{
				System.out.println(" <<<<<< SINGLE bomPart >>>>>>>" +bomPart.getName());
				bomTachPackStatus = false;
			}
			System.out.println(" <<<<<< OUTSIDE  SINGLE bomPart >>>>>>>" +bomPart.getName());
		}
		return bomTachPackStatus;
    }
    
}
