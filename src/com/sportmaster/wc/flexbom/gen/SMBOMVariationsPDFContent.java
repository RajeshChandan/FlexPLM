package com.sportmaster.wc.flexbom.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.gen.BOMPDFContentGenerator;
import com.lcs.wc.flexbom.gen.BomDataGenerator;
import com.lcs.wc.product.PDFProductSpecificationBOM2;
import com.lcs.wc.product.PDFProductSpecificationGenerator2;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.vrd.wc.flexbom.gen.VRDBOMPDFContentGenerator;

import wt.util.WTException;

/**
 * This class will be the core class to generate the report 
 * based on the user selection on filter page and will call methods
 * to fetch all required BOM data, Product and Season information and prepare the pdf object. 
 * This class will be called when the user selects "BOM: Variations" from the list of available components.
 *
 */
public class SMBOMVariationsPDFContent extends VRDBOMPDFContentGenerator {

	private float tableWidthPercent = (new Float(
			LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorPDFContent.tableWidthPercent", "95.0"))).floatValue();
	private static final boolean BOM_PDF_NO_EXTEND_LAST_ROW = LCSProperties
			.getBoolean("com.lcs.wc.product.PDFProductSpecificationGenerator.BOMnoExtendLastRow");

	private static final Logger LOGGER = Logger.getLogger(SMBOMVariationsPDFContent.class);

	/** Creates a new instance of MatColorPDFContent */
	public SMBOMVariationsPDFContent() {

	}

	/**
	 * This is the main method to generate the report 
	 * based on the user selection on filter page to fetch all required 
	 * BOM data, Product and Season information and prepare the pdf object. 
	 * This method will be called when the user selects "BOM: Variations" from the list of available components.
	 *
	 */
	@Override
	public Collection getPDFContentCollection(Map params, Document document) throws WTException {
		LOGGER.debug("In getPDFContentCollection method - Start ");
		Collection content = new ArrayList();
		Collection spcontent = new ArrayList();
		Collection sectionPageTitles = new ArrayList();
		FlexBOMPart bomPart = (FlexBOMPart) params.get(BOMPDFContentGenerator.BOM_PART);
		Map tparams = new HashMap(params.size() + 2);
		tparams.putAll(params);

		/// VRD EXTENSION START: Removing default columns
		SMBOMVariationsGenerator bomDG; // = new SMBOMVariationsGenerator();
		/// VRD EXTENSION END
		Collection allSkus = (Collection) params.get(BomDataGenerator.COLORWAYS);
		int maxPerPage = ((Integer) params.get(PDFProductSpecificationGenerator2.COLORWAYS_PER_PAGE)).intValue();

		// Create collection of arrayLists of skus
		Collection skusArray = splitItems(allSkus, maxPerPage);

		Iterator skusIt = null;
		Collection skusThisRun; // = new ArrayList();
		// Get section details
		Collection sections = bomPart.getFlexType().getAttribute("section").getAttValueList()
				.getSelectableKeys(com.lcs.wc.client.ClientContext.getContext().getLocale(), true);
		String section = "";
		Iterator sectionIter = sections.iterator();
		// Iterate each Section
		while (sectionIter.hasNext()) {
			section = (String) sectionIter.next();
			LOGGER.debug("-adding section : " + section);
			LOGGER.info("In getPDFContentCollection method - Current Section== " + section);
			// Add current section to tParams
			tparams.put(BomDataGenerator.SECTION, section);
			// set view id for each section 
			setSectionViewId(tparams);
			skusIt = skusArray.iterator();
			// iterate skus
			while (skusIt.hasNext()) {
				skusThisRun = (Collection) skusIt.next();
				LOGGER.debug("In getPDFContentCollection - skusThisRun== " + skusThisRun);
				tparams.put(BomDataGenerator.COLORWAYS, skusThisRun);
				/// VRD EXTENSION START: Removing default columns
				bomDG = new SMBOMVariationsGenerator();
				/// VRD EXTENSION END
				// Call init method
				bomDG.init(tparams);
				// Get BOM Data
				Collection data = bomDG.getBOMData();
				LOGGER.info("In getPDFContentCollection method - Current Section - skusThisRun's bom data size== "
						+ data.size());
				LOGGER.debug("In getPDFContentCollection method - Current Section - skusThisRun's bom data== " + data);
				// Get table columns to be displayed on the report
				Collection columns = bomDG.getTableColumns();
				// call generatePDFPage method to generate the PDF content
				spcontent.addAll(generatePDFPage(data, columns, document, tparams));
				if (!BOM_ON_SINGLE_PAGE) {
					sectionPageTitles.add(getPageTitleText(tparams));
				}
			}
		}

		generatePDFTableData(params, content, spcontent, sectionPageTitles, tparams);
		LOGGER.debug("In getPDFContentCollection method - End ");
		return content;
	}

	/**
	 * Method to generate PDF Table 
	 * @param params
	 * @param content
	 * @param spcontent
	 * @param sectionPageTitles
	 * @param tparams
	 */
	private void generatePDFTableData(Map params, Collection content, Collection spcontent, Collection sectionPageTitles,
			Map tparams) {
		PdfPTable fullBOMTable = new PdfPTable(1);
		PdfPTable e = null;
		PdfPCell cell = null;
		fullBOMTable.setWidthPercentage(tableWidthPercent);
		/// VRD EXTENSION START: Fixing issue with how table splits between pages
		fullBOMTable.setSplitLate(false);
		/// VRD EXTENSION END
		generateBOMHeaderPDFTable(params, content, fullBOMTable);

		// Add BOM Sections
		Collection bomFooter = (Collection) params.get(PDFProductSpecificationBOM2.BOM_FOOTER_ATTS);
		boolean usingFooter = (bomFooter != null && !bomFooter.isEmpty()
				&& FormatHelper.parseBoolean((String) params.get(PDFProductSpecificationBOM2.PRINT_BOM_FOOTER)));
		if (BOM_ON_SINGLE_PAGE) {
			generateOnSinglePage(params, content, spcontent, tparams, fullBOMTable, bomFooter, usingFooter);
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
						for (Iterator footI = bomFooter.iterator(); footI.hasNext();) {
							e = (PdfPTable) footI.next();
							cell = new PdfPCell(e);
							fullBOMTable.addCell(cell);
						}
						content.add(fullBOMTable);
					} else {
						// Add last element
						content.add(e);
						// Add Footer
						content.addAll(bomFooter);
						this.pageTitles
								.addAll((Collection) params.get(PDFProductSpecificationBOM2.BOM_FOOTER_PAGE_TITLES));
					}
				} else {
					// Not the last element
					content.add(e);
				}
			} // while
		}
	}

	/**
	 * Method to generate BOM on single page
	 * @param params
	 * @param content
	 * @param spcontent
	 * @param tparams
	 * @param fullBOMTable
	 * @param bomFooter
	 * @param usingFooter
	 */
	private void generateOnSinglePage(Map params, Collection content, Collection spcontent, Map tparams,
			PdfPTable fullBOMTable, Collection bomFooter, boolean usingFooter) {
		PdfPTable e;
		PdfPCell cell;
		Iterator sci = spcontent.iterator();
		while (sci.hasNext()) {
			e = (PdfPTable) sci.next();
			cell = new PdfPCell(e);
			fullBOMTable.addCell(cell);
		}
		// set splitlate
		if (BOM_PDF_NO_EXTEND_LAST_ROW) {
			fullBOMTable.setSplitLate(false);
		}
		// Add Footer
		if (usingFooter) {
			if (PDFProductSpecificationBOM2.BOM_FOOTER_SAME_PAGE) {
				for (Iterator footI = bomFooter.iterator(); footI.hasNext();) {
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
				content.addAll(bomFooter);
				this.pageTitles.addAll((Collection) params.get(PDFProductSpecificationBOM2.BOM_FOOTER_PAGE_TITLES));
			}
		} else {
			content.add(fullBOMTable);
			this.pageTitles.add(getPageTitleText(tparams));
		}
	}

	/**
	 * Method to generate BOM Header section
	 * @param params
	 * @param content
	 * @param fullBOMTable
	 */
	private void generateBOMHeaderPDFTable(Map params, Collection content, PdfPTable fullBOMTable) {
		PdfPTable e;
		PdfPCell cell;
		// Add the BOM Header Attributes
		if (FormatHelper.parseBoolean((String) params.get(PDFProductSpecificationBOM2.PRINT_BOM_HEADER))) {
			Collection bomHeaderAtts = (Collection) params.get(PDFProductSpecificationBOM2.BOM_HEADER_ATTS);
			if (bomHeaderAtts != null && !bomHeaderAtts.isEmpty()) {
				if (PDFProductSpecificationBOM2.BOM_HEADER_SAME_PAGE && BOM_ON_SINGLE_PAGE) {
					for (Iterator headerI = bomHeaderAtts.iterator(); headerI.hasNext();) {
						e = (PdfPTable) headerI.next();
						cell = new PdfPCell(e);
						fullBOMTable.addCell(cell);
					}
				} else {
					content.add(bomHeaderAtts);
					this.pageTitles.addAll((Collection) params.get(PDFProductSpecificationBOM2.BOM_HEADER_PAGE_TITLES));
				}
			}
		}
	}
}
