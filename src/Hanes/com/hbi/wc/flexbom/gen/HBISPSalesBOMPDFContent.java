/*
 * HBISPPackCasePDFContent.java
 *
 * Created on Dec 14, 2018, 12:58 PM
 */

package com.hbi.wc.flexbom.gen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import wt.part.WTPartMaster;
import wt.util.WTException;

import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.gen.BOMPDFContentGenerator;
import com.lcs.wc.flexbom.gen.BomDataGenerator;
import com.lcs.wc.part.LCSPart;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.PDFProductSpecificationBOM2;
import com.lcs.wc.product.PDFProductSpecificationGenerator2;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
/**
*
* @author John S. Reeno From UST
* @Date Dec 8, 2018, 12:58 PM
* 
*
*       This class is implemented by taking the code from HBISizePDFContent and
*       modified as per requirement
* 
*/
public class HBISPSalesBOMPDFContent extends BOMPDFContentGenerator {
    
    private static final boolean DEBUG = LCSProperties.getBoolean("com.lcs.wc.flexbom.gen.HBISPPackCaseBOMPDFContent.verbose");
    private static final int DEBUG_LEVEL = Integer.parseInt(LCSProperties.get("com.lcs.wc.flexbom.gen.SizePDFContent.verboseLevel", "1"));
    public float tableWidthPercent = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.SingleBOMPDFContent.tableWidthPercent", "95.0"))).floatValue();
	//GP teck pack customizations - 09/20/2018
	public static final String BASIC_CUT_AND_SELLING = "Product\\BASIC CUT & SEW - SELLING";
    public static final String  SP_BOM_TYPE_PATH = LCSProperties.get("bomreport.sales.salesproduct.bomtypes");
	
    /** Creates a new instance of SizePDFContent */
    public HBISPSalesBOMPDFContent() {
    }
    
   	/* (non-Javadoc)
   	 * @see com.lcs.wc.flexbom.gen.BOMPDFContentGenerator#getPDFContentCollection(java.util.Map, com.lowagie.text.Document)
   	 */
   	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection getPDFContentCollection(Map params, Document document) throws WTException {
		debug(1, "SizePDFContent.getPDFContentCollection()");
		Collection content = new ArrayList();
		Collection spcontent = new ArrayList();
		Collection sectionPageTitles = new ArrayList();

		HBISPSalesBOMGenerator bomDG = new HBISPSalesBOMGenerator();
		FlexBOMPart bomPart = (FlexBOMPart) params.get(BOMPDFContentGenerator.BOM_PART);
		
		boolean spBOMTechpackStatus = isValidateBOMTypeForSPSalesReport(bomPart);
		if (spBOMTechpackStatus) {

			Map tparams = new HashMap(params.size() + 3);
			tparams.putAll(params);
			boolean usingSize1 = true;
			if ("size2".equalsIgnoreCase((String) params.get(BomDataGenerator.USE_SIZE1_SIZE2))) {
				usingSize1 = false;
			}
			Collection allSizes = new ArrayList();
			if (usingSize1) {
				allSizes = (Collection) params.get(BomDataGenerator.SIZES1);
			} else {
				allSizes = (Collection) params.get(BomDataGenerator.SIZES2);
			}

			Collection sections = bomPart.getFlexType().getAttribute("section").getAttValueList()
					.getSelectableKeys(com.lcs.wc.client.ClientContext.getContext().getLocale(), true);
			String section = "";
			Iterator sectionIter = sections.iterator();
			Iterator sizeIt = null;

			// Create collection of arrayLists of skus
			Collection sizesArray = new ArrayList();
			sizesArray.add(allSizes);

			while (sectionIter.hasNext()) {
				section = (String) sectionIter.next();
				if (DEBUG) {
					System.out.println("-adding section :  " + section);
				}
				tparams.put(BomDataGenerator.SECTION, section);
				setSectionViewId(tparams);
				sizeIt = sizesArray.iterator();

				Collection sizesThisRun = new ArrayList();

				while (sizeIt.hasNext()) {
					sizesThisRun = (Collection) sizeIt.next();
					if (usingSize1) {
						tparams.put(BomDataGenerator.SIZES1, sizesThisRun);
					} else {
						tparams.put(BomDataGenerator.SIZES2, sizesThisRun);
					}
					bomDG.init(tparams);
					Collection data = bomDG.getBOMData();
					Collection columns = bomDG.getTableColumns();	
					spcontent.addAll(generatePDFPage(data, columns, document, tparams));
					if (!BOM_ON_SINGLE_PAGE) {
						sectionPageTitles.add(getPageTitleText(tparams));
					}
				}
			}

			PdfPTable fullBOMTable = new PdfPTable(1);
			PdfPTable e = null;
			PdfPCell cell = null;
			fullBOMTable.setWidthPercentage(tableWidthPercent);
			// Add the BOM Header Attributes
			if (FormatHelper.parseBoolean((String) params.get(PDFProductSpecificationBOM2.PRINT_BOM_HEADER))) {
				debug(2, "FormatHelper.parseBoolean((String)params.get(PDFProductSpecificationBOM2.PRINT_BOM_HEADER) )");
				Collection BomHeaderAtts = (Collection) params.get(PDFProductSpecificationBOM2.BOM_HEADER_ATTS);
				if (BomHeaderAtts != null && !BomHeaderAtts.isEmpty()) {
					debug(2, "BomHeaderAtts != null && !BomHeaderAtts.isEmpty()");
					if (PDFProductSpecificationBOM2.BOM_HEADER_SAME_PAGE && BOM_ON_SINGLE_PAGE) {
						debug(2, "PDFProductSpecificationBOM2.BOM_HEADER_SAME_PAGE  && BOM_ON_SINGLE_PAGE");
						for (Iterator HeaderI = BomHeaderAtts.iterator(); HeaderI.hasNext();) {
							e = (PdfPTable) HeaderI.next();
							cell = new PdfPCell(e);
							fullBOMTable.addCell(cell);
						}
					} else {
						debug(2, "NOT--PDFProductSpecificationBOM2.BOM_HEADER_SAME_PAGE  && BOM_ON_SINGLE_PAGE");
						content.add(BomHeaderAtts);
						this.pageTitles
								.addAll((Collection) params.get(PDFProductSpecificationBOM2.BOM_HEADER_PAGE_TITLES));
					}
				}
			}

			// Add BOM Sections
			Collection BOMFooter = (Collection) params.get(PDFProductSpecificationBOM2.BOM_FOOTER_ATTS);
			boolean usingFooter = (BOMFooter != null && !BOMFooter.isEmpty()
					&& FormatHelper.parseBoolean((String) params.get(PDFProductSpecificationBOM2.PRINT_BOM_FOOTER)));
			if (BOM_ON_SINGLE_PAGE) {
				debug(2, "BOM_ON_SINGLE_PAGE");
				Iterator sci = spcontent.iterator();
				while (sci.hasNext()) {
					e = (PdfPTable) sci.next();
					cell = new PdfPCell(e);
					fullBOMTable.addCell(cell);
				}

				// Add Footer
				if (usingFooter) {
					debug(2, "usingFooter");
					if (PDFProductSpecificationBOM2.BOM_FOOTER_SAME_PAGE) {
						for (Iterator footI = BOMFooter.iterator(); footI.hasNext();) {
							e = (PdfPTable) footI.next();
							cell = new PdfPCell(e);
							fullBOMTable.addCell(cell);
						}
						// Add BOM to content
						content.add(fullBOMTable);
						this.pageTitles.add(getPageTitleText(tparams));
					} else {
						// Add BOM to content
						content.add(fullBOMTable);
						this.pageTitles.add(getPageTitleText(tparams));
						// Add Footer to content
						content.addAll(BOMFooter);
						this.pageTitles
								.addAll((Collection) params.get(PDFProductSpecificationBOM2.BOM_FOOTER_PAGE_TITLES));
					}
				} else {
					content.add(fullBOMTable);
					this.pageTitles.add(getPageTitleText(tparams));
				}
			} else { // BOM sections different pages
				this.pageTitles.addAll(sectionPageTitles);
				Iterator sci = spcontent.iterator();
				// Add the first section to the fullBOMTable in case we have a Header
				e = (PdfPTable) sci.next();
				cell = new PdfPCell(e);
				fullBOMTable.addCell(cell);
				content.add(fullBOMTable);
				while (sci.hasNext()) {
					e = (PdfPTable) sci.next();
					if (!sci.hasNext() && usingFooter) {
						// Last element && using a footer
						if (PDFProductSpecificationBOM2.BOM_FOOTER_SAME_PAGE) {
							fullBOMTable = new PdfPTable(1);
							cell = new PdfPCell(e);
							fullBOMTable.addCell(cell);
							for (Iterator footI = BOMFooter.iterator(); footI.hasNext();) {
								e = (PdfPTable) footI.next();
								cell = new PdfPCell(e);
								fullBOMTable.addCell(cell);
							}
							content.add(fullBOMTable);
						} else {
							// Add last element
							content.add(e);
							// Add Footer
							content.addAll(BOMFooter);
							this.pageTitles.addAll(
									(Collection) params.get(PDFProductSpecificationBOM2.BOM_FOOTER_PAGE_TITLES));
						}
					} else {
						// Not the last element
						content.add(e);
					}
				} // while
			}
		}
		return content;
	}

	   /////////////////////////////////////////////////////////////////////////////
   public static void debug(String msg){debug(msg, 1); }
   public static void debug(int i, String msg){debug(msg, i); }
   public static void debug(String msg, int i){
	  if(DEBUG && i <= DEBUG_LEVEL) System.out.println(msg);
   }
      
   /**
    * 
    * @param bomPart
    * @return
    * @throws WTException
    * @author Manoj
    * @Date 12/14/2018 
    */
  public boolean isValidateBOMTypeForSPSalesReport(FlexBOMPart bomPart) throws WTException
   {
		boolean bomSPTachPackStatus = true;
		LCSProduct prodObj = null;
		String bomPartFlexTypePath = bomPart.getFlexType().getFullName(true);
//		WTPartMaster wtPartMaster = bomPart.getOwnerMaster();
//		LCSPart lcsPart = (LCSPart)VersionHelper.latestIterationOf(wtPartMaster);
		LCSPart lcsPart = (LCSPart) VersionHelper.latestIterationOf(bomPart.getOwnerMaster());
		
		if(lcsPart instanceof LCSProduct)
		{
			prodObj = (LCSProduct)lcsPart;
			String productFlexTypePath = prodObj.getFlexType().getFullName(true);
			if(BASIC_CUT_AND_SELLING.equalsIgnoreCase(productFlexTypePath) && !Arrays.asList(SP_BOM_TYPE_PATH.split(",")).contains(bomPartFlexTypePath))
			{
				bomSPTachPackStatus = false;
			}
		}
		return bomSPTachPackStatus;
    }  
}
