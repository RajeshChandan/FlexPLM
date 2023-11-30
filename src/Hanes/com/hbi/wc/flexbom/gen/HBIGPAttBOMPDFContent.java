package com.hbi.wc.flexbom.gen;

import java.util.*;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.*;
import com.lcs.wc.product.*;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.util.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
//import wt.part.WTPartMaster;
import com.lcs.wc.part.LCSPart;
import wt.util.*;
import com.lcs.wc.flexbom.gen.*;
import com.lcs.wc.foundation.LCSQuery;

/**
 *
 * @author UST
 * @Date Mar 29, 2019, 10:00 AM
 * 
 *
 *       This class is implemented by taking the code from HBIMatColorPDFContent
 *       and modified as per requirement for attribution BOM
 * 
 */

public class HBIGPAttBOMPDFContent extends HBIBOMPDFContentGenerator {

	private static final boolean DEBUG = LCSProperties
			.getBoolean("com.lcs.wc.flexbom.gen.HBIMattGPMatColorPDFContent.verbose");
	private static final int DEBUG_LEVEL = Integer
			.parseInt(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorPDFContent.verboseLevel", "1"));
	public float tableWidthPercent = (new Float(
			LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorPDFContent.tableWidthPercent", "95.0"))).floatValue();
	// GP teck pack customizations - 09/20/2018
	public static final String BASIC_CUT_AND_SEW_GARMENT = "Product\\BASIC CUT & SEW - GARMENT";
	public static final String LABEL_TYPE = "Product\\HBI-SUPPORTING\\LABEL";

	public static final String GARMENT_BOM_TYPE_PATH = LCSProperties.get("bomreport.attbom.garmentproduct.bomtypes");
	// Multi Spec BOM
	public static final String MULTI_SPEC_BOM = LCSProperties.get("com.hbi.multispecBOM");

	/** Creates a new instance of MatColorPDFContent */
	public HBIGPAttBOMPDFContent() {
	}

	/**
	 * gets an Element for insertion into a PDF Document
	 * 
	 * @param params
	 *            A Map of parameters to pass to the Object. This provides the
	 *            means for the calling class to have some "fore" knowledge of
	 *            what implementations are being used and pass appropriate
	 *            parameters.
	 * @param document
	 *            The PDF Document which the content is going to be added to.
	 *            The document is passed in order to provide additional
	 *            information related to the Document itself incase it is not
	 *            provided in the params
	 * @throws WTException
	 *             For any error
	 * @return an Element for insertion into a Document
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public Collection getPDFContentCollection(Map params, Document document) throws WTException {
		if (DEBUG) {
			debug("MatColorPDFContent.getPDFContentCollection");
		}
		Collection content = new ArrayList();
		Collection spcontent = new ArrayList();
		Collection sectionPageTitles = new ArrayList();
		FlexBOMPart bomPart = (FlexBOMPart) params.get(BOMPDFContentGenerator.BOM_PART);
		// GP teck pack customizations - 09/20/2018
        

		boolean gpBOMTechpackStatus = isValidateGPBOMTypeForBOMReport(bomPart, params);

		if (gpBOMTechpackStatus) {
			Map tparams = new HashMap(params.size() + 2);
			tparams.putAll(params);

			HBIGPAttBOMGenerator bomDG = new HBIGPAttBOMGenerator();
			Collection allSkus = (Collection) params.get(BomDataGenerator.COLORWAYS);
			int maxPerPage = ((Integer) params.get(PDFProductSpecificationGenerator2.COLORWAYS_PER_PAGE)).intValue();

			// Create collection of arrayLists of skus
			Collection skusArray = splitItems(allSkus, maxPerPage);
			// Multiple Colorways for Multi Spec -Jey
			Boolean multiSpecBOM = false;
			LCSSourcingConfig sourceM = null;

			if (params.get("multi_source") != null) {
				sourceM = (LCSSourcingConfig) LCSQuery.findObjectById((String) params.get("multi_source"));
				multiSpecBOM = true;
				allSkus = getMultiSpecColorways(params, sourceM);
				skusArray = splitItems(allSkus, maxPerPage);
			}
			// Multiple Colorways for Multi Spec -Jey
			Iterator skusIt = null;

			Collection skusThisRun = new ArrayList();

			Collection sections = bomPart.getFlexType().getAttribute("section").getAttValueList()
					.getSelectableKeys(com.lcs.wc.client.ClientContext.getContext().getResolvedLocale(), true);
					//.getSelectableKeys(com.lcs.wc.client.ClientContext.getContext().getLocale(), true);
			String section = "";
			Iterator sectionIter = sections.iterator();
			while (sectionIter.hasNext()) {
				section = (String) sectionIter.next();
				skusIt = skusArray.iterator();
				// skusArray contains the colorways which is displayed in the
				// selection page for that spec
				// multiSpecColorwaysArray contains colorways that belong to
				// that BOM from that source

				tparams.put(BomDataGenerator.SECTION, section);
				setSectionViewId(tparams);

				if (allSkus.isEmpty()) {

					SingleBOMGenerator singleBom = new SingleBOMGenerator();
					singleBom.init(tparams);

					Collection data = singleBom.getBOMData();
					// To get the all columns as in view selected on select
					// techpack page
					Collection columns = singleBom.getTableColumns();
					spcontent.addAll(generatePDFPage(data, columns, document, tparams));
					if (!BOM_ON_SINGLE_PAGE) {
						sectionPageTitles.add(getPageTitleText(tparams));
					}
				} else {

					while (skusIt.hasNext()) {

						skusThisRun = (Collection) skusIt.next();

						tparams.put(BomDataGenerator.COLORWAYS, skusThisRun);
						bomDG = new HBIGPAttBOMGenerator();
						bomDG.init(tparams);
						Collection data = bomDG.getBOMData();
						Collection columns = bomDG.getTableColumns();
						spcontent.addAll(generatePDFPage(data, columns, document, tparams));

						if (!BOM_ON_SINGLE_PAGE) {
							sectionPageTitles.add(getPageTitleText(tparams));

						}
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
				// Add the first section to the fullBOMTable in case we have a
				// Header
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
	public static void debug(String msg) {
		debug(msg, 1);
	}

	public static void debug(int i, String msg) {
		debug(msg, i);
	}

	public static void debug(String msg, int i) {
		if (DEBUG && i <= DEBUG_LEVEL)
			System.out.println(msg);
	}

	/**
	 * 
	 * @param bomPart
	 * @param params
	 * @return
	 * @throws WTException
	 *             GP teck pack customizations
	 *             Select_teck_pack_page_requirements - 09/20/2018
	 */
	@SuppressWarnings("rawtypes")
	public boolean isValidateGPBOMTypeForBOMReport(FlexBOMPart bomPart, Map params) throws WTException {
		boolean bomGPTachPackStatus = true;
		LCSProduct prodObj = null;
		String bomPartFlexTypePath = bomPart.getFlexType().getFullName(true);
		//WTPartMaster wtPartMaster = bomPart.getOwnerMaster();
		BOMOwner wtPartMaster = bomPart.getOwnerMaster();
		LCSPart lcsPart = (LCSPart) VersionHelper.latestIterationOf(wtPartMaster);
		debug("SEASONMASTER_ID :: " + (String) params.get("SEASONMASTER_ID"));
		if (lcsPart instanceof LCSProduct) {
			prodObj = (LCSProduct) lcsPart;
			String productFlexTypePath = prodObj.getFlexType().getFullName(true);
			
			if ((BASIC_CUT_AND_SEW_GARMENT.equalsIgnoreCase(productFlexTypePath) || LABEL_TYPE.equalsIgnoreCase(productFlexTypePath))
					&& !Arrays.asList(GARMENT_BOM_TYPE_PATH.split(",")).contains(bomPartFlexTypePath)) {
				debug(" <<<<<<bomPart >>>>>>>" + bomPart.getName());
				
				bomGPTachPackStatus = false;
			}
		}
		// Check if user selected season before generating report
		if (!FormatHelper.hasContent((String) params.get("SEASONMASTER_ID"))) {
			bomGPTachPackStatus = false;
			System.out.println(
					"!!!! GPAttributionBOM Report Printing will be skipped as no season selected while generating Report for bom ["
							+ bomPart.getName() + "]");
		}
		return bomGPTachPackStatus;
	}

	/**
	 * @param params
	 * @param source
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection getMultiSpecColorways(Map params, LCSSourcingConfig source) {

		Collection specificColl = new ArrayList();

		HashMap specificSKUMap = new HashMap();

		try {
			String seasonMaster_Oid = (String) params.get("SEASONMASTER_ID");

			LCSSeasonMaster seasonMaster = (LCSSeasonMaster) LCSQuery.findObjectById(seasonMaster_Oid);
			LCSSeason season = (LCSSeason) VersionHelper.latestIterationOf(seasonMaster);
			LCSSourcingConfigQuery sQuery = new LCSSourcingConfigQuery();

			SearchResults skuSourceSR = sQuery.getSKUSourcingLinkDataForConfig(source, season, true);
			if (skuSourceSR != null && skuSourceSR.getResultsFound() > 0) {
				Collection<FlexObject> skuSourceFOColl = skuSourceSR.getResults();

				for (FlexObject skuSourceFo : skuSourceFOColl) {

					String skumasterId = skuSourceFo.getData("SKUMASTER.IDA2A2");
					String skuMasterName = skuSourceFo.getData("SKUMASTER.NAME");
					specificSKUMap.put(skumasterId, skuMasterName);

				}
			}

			if (!specificSKUMap.isEmpty()) {
				specificColl = specificSKUMap.keySet();
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return specificColl;
	}
}
