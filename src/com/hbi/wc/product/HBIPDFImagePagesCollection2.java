/*
 * PDFImagePagesCollection.java
 *
 * Created on August 23, 2005, 10:24 AM
 */

package com.hbi.wc.product;

import com.lcs.wc.util.*;
import com.lcs.wc.document.*;
import com.lcs.wc.season.*;
import com.lcs.wc.flextype.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.db.*;
import com.lcs.wc.client.web.*;
import com.lcs.wc.client.web.pdf.*;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lcs.wc.product.*;

import java.util.*;

import wt.util.*;
import wt.fc.WTObject;
import com.lcs.wc.specification.FlexSpecification;
//import wt.part.WTPartMaster;

/**
 *
 * @author  Chuck
 */
public class HBIPDFImagePagesCollection2 implements PDFContentCollection, SpecPageSet {
    public static final boolean INCLUDE_COMMENTS = LCSProperties.getBoolean("com.lcs.wc.product.PDFImagePagesCollection2.includeComments");
	private static final String BASIC_CUT_AND_SEW_SELLING = "Product\\BASIC CUT & SEW - SELLING";
	private static final String SELLING_IMAGE_TYPE = "UPC Details,Product Safety";
    
    /** Creates a new instance of PDFImagePagesCollection */
    public HBIPDFImagePagesCollection2() {
    }
    
    public static final String PRODUCT_ID = "PRODUCT_ID";
    public static final String SPEC_ID = "SPEC_ID";
    public static final String PAGE_TYPE = "pageType";
    public static final String IMAGE_PAGES_HEADER_CLASS = "IMAGE_PAGES_HEADER_CLASS";
    public static final String IMAGE_PAGES_FOOTER_CLASS = "IMAGE_PAGES_FOOTER_CLASS";
    
    Collection pageTitles = new ArrayList();
    
    private PdfWriter writer = null;
    
    /** Gets a Collection of PdfPTables representing Image Pages.
     * Use com.lcs.wc.document.ImagesPagePDFGenerator to generatate each page
     *
     * @param params
     * @param document
     * @throws WTException
     * @return
     */    
    public Collection getPDFContentCollection(Map params, Document document) throws WTException {
        ArrayList imageDocs = new ArrayList();

		boolean ImageTechpackStatus = generateSellingImageTechpackStatus(params);
		debug("<<<<<<<< I am inside  IMAGE" +ImageTechpackStatus);
		if(ImageTechpackStatus)
		{
        try{
            debug("Here I am in getPDFContentCollection params: " + params);
            if(!FormatHelper.hasContent((String)params.get(PRODUCT_ID))){
                return imageDocs;
            }
		   this.pageTitles = new ArrayList();
//            WTObject obj = (WTObject)LCSProductQuery.findObjectById((String)params.get(PRODUCT_ID));
//            if(!(obj instanceof LCSProduct)){
//                throw new WTException("Can not use PDFImagePagesCollection on a non-LCSProduct - " + obj);
//            }
//            
//            WTObject obj2 = (WTObject)LCSProductQuery.findObjectById((String)params.get(SPEC_ID));
//            if(obj2 == null || !(obj2 instanceof FlexSpecification)){
//                throw new WTException("Can not use PDFProductSpecificationMeasurements on without a FlexSpecification - " + obj);
//            }
            //FormatHelper.getVersionId(productSeasonRev)
//            LCSProduct product = (LCSProduct)obj;
//            FlexSpecification spec = (FlexSpecification)obj2;

//            Collection ipgs = getImagePages(product, spec, (String)params.get(PAGE_TYPE));
//            Iterator ips = ipgs.iterator();
//            while(ips.hasNext()){
//                doc = (FlexObject)ips.next();
                //contentMap.put(ImagePagePDFGenerator.DOCUMENT_ID, "VR:com.lcs.wc.document.LCSDocument:" + doc.get("LCSDOCUMENT.BRANCHIDITERATIONINFO"));
                //contentMap.put("HEADER_HEIGHT", params.get("HEADER_HEIGHT"));
                params.put(ImagePagePDFGenerator.DOCUMENT_ID, params.get(PDFProductSpecificationGenerator2.COMPONENT_ID));
//                params.put(ImagePagePDFGenerator.DOCUMENT_ID, "VR:com.lcs.wc.document.LCSDocument:" + doc.get("LCSDOCUMENT.BRANCHIDITERATIONINFO"));
                ImagePagePDFGenerator ippg = new ImagePagePDFGenerator(writer);
                ippg.setIncludeComment(INCLUDE_COMMENTS);
                //ippg.setIncludeComment(false);
                //Element content = ippg.getPDFContent(contentMap, document);
                Element content = ippg.getPDFContent(params, document);
                imageDocs.add(content);
                String pageTitle = ippg.getPageTitle();
                pageTitles.add(pageTitle);
//            }
        }
        catch(Exception e){
            throw new WTException(e);
        }
	}	
        return imageDocs;
    }
    
    
//    private Collection getImagePages(LCSProduct product,FlexSpecification spec, String pageType) throws WTException{
//        try{
//            String productId = FormatHelper.getObjectId((WTPartMaster)product.getMaster());
//            String specId = FormatHelper.getObjectId((WTPartMaster)spec.getMaster());
//
//            FlexTypeQueryStatement statement = (new LCSProductQuery()).getProductImagesQuery(productId, specId);
//            if(FormatHelper.hasContent(pageType)){
//                statement.appendAndIfNeeded();
//                statement.appendFlexCriteria("pageType", pageType, Criteria.EQUALS);          
//            }
//            statement.appendSortBy("LCSDOCUMENT.ATT1");
//            SearchResults results = LCSQuery.runDirectQuery(statement);
//            return results.getResults();
//        }catch(Exception e){
//            e.printStackTrace();
//            throw new WTException(e);
//        }
//    }
    
    /** returns the titles for each page of the Image Page Collection
     * @return
     */    
    public Collection getPageHeaderCollection() {
        return this.pageTitles;
    }
    
    public static void debug(String msg){
        if(false){
            System.out.println(msg);
        }
    }
    
    public void setPdfWriter(PdfWriter pw) {
        writer = pw;
    }
	
	/**
     * This function is using to print only Routing,Packing and Casing Image type for Selling Product.
     * @param params - Map
     * @return ImageTechpackStatus - boolean
     * @throws WTException
     */
   public boolean generateSellingImageTechpackStatus(Map params) throws WTException
   {
		boolean ImageTechpackStatus = true;
		LCSDocument documentObj = null;
		
		WTObject obj = (WTObject)LCSProductQuery.findObjectById((String)params.get(PRODUCT_ID));
		LCSProduct prodObj = (LCSProduct)obj;
		String productFlexTypePath = prodObj.getFlexType().getFullName(true);
		
		if(FormatHelper.hasContent((String)params.get("COMPONENT_ID")))
		{	
			documentObj = (LCSDocument)LCSDocumentQuery.findObjectById((String)params.get("COMPONENT_ID"));
			documentObj = (LCSDocument)VersionHelper.latestIterationOf(documentObj);
			
			String pageTypeValue = (String) documentObj.getValue("pageType");
			String pageType = documentObj.getFlexType().getAttribute("pageType").getAttValueList().getValue(pageTypeValue,null);
			debug(" <<<< PAGE TYPE >>>>" +pageType);
			
			if(BASIC_CUT_AND_SEW_SELLING.equalsIgnoreCase(productFlexTypePath) && !SELLING_IMAGE_TYPE.contains(pageType))
			{
				ImageTechpackStatus = false;
			}
		}
		return ImageTechpackStatus;
    }
}
