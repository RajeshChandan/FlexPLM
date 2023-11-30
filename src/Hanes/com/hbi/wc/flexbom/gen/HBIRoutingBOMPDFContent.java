/*
 * HBIRoutingBOMPDFContent.java
 *
 * Created on April 10, 2019, 12:58 PM
 */

package com.hbi.wc.flexbom.gen;

import java.util.*;
import com.lcs.wc.flexbom.*;
import com.lcs.wc.product.*;
import com.lcs.wc.util.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lcs.wc.part.LCSPart;
import wt.util.*;


import com.lcs.wc.client.web.TableColumn;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.gen.*;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTyped;

import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.infoengine.client.web.ElementTableData;
import com.lcs.wc.report.ColumnList;

/**
 *
 * @author Manoj From UST
 * @Date Oct 4, 2018, 12:58 PM
 * 
 *
 *       This class is implemented by taking the code from
 *       HBISingleBOMPDFContent and modified as per requirement
 * 
 */
public class HBIRoutingBOMPDFContent extends BOMPDFContentGenerator {
	public float tableWidthPercent = (new Float(
			LCSProperties.get("com.lcs.wc.flexbom.gen.SingleBOMPDFContent.tableWidthPercent", "95.0"))).floatValue();

	// GP and SP teck pack customizations - 04/10/2019
	public static final String VALID_BOM_TYPE_PATHS = LCSProperties.get("bomreport.routingbom.product.bomtypes");
	public static final String BASIC_CUT_AND_SEW_GARMENT = LCSProperties.get("hbi.gp.product.type","Product\\BASIC CUT & SEW - GARMENT");
	public static final String BASIC_CUT_AND_SEW_SELLING = LCSProperties.get("hbi.sp.product.type","Product\\BASIC CUT & SEW - SELLING");
	private static final boolean MATT_VARATIONS_ENABLED = false;
	private static final boolean BOM_ON_SINGLE_PAGE = false;
   public static final String LABEL_TYPE = "Product\\HBI-SUPPORTING\\LABEL";

	/** Creates a new instance of SingleBOMPDFContent */
	public HBIRoutingBOMPDFContent() {
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection getPDFContentCollection(Map params, Document document) throws WTException {
		ArrayList content = new ArrayList();
		ArrayList spcontent = new ArrayList();

		FlexBOMPart bomPart1 = (FlexBOMPart) params.get(BOMPDFContentGenerator.BOM_PART);
		// GP teck pack customizations - 09/20/2018
		boolean gpBOMTechpackStatus = isValidateGPBOMTypeForBOMReport(bomPart1);
		if (gpBOMTechpackStatus) {
			// Add the BOM Header Attributes
			if (FormatHelper.parseBoolean((String) params.get(PDFProductSpecificationBOM2.PRINT_BOM_HEADER))) {
				Collection BomHeaderAtts = (Collection) params.get(PDFProductSpecificationBOM2.BOM_HEADER_ATTS);
				if (BomHeaderAtts != null && !BomHeaderAtts.isEmpty()) {
					if (PDFProductSpecificationBOM2.BOM_HEADER_SAME_PAGE) {
						if (BOM_ON_SINGLE_PAGE) {
							spcontent.addAll(BomHeaderAtts);
						} else {
							content.addAll(BomHeaderAtts);
							this.pageTitles.addAll(
									(Collection) params.get(PDFProductSpecificationBOM2.BOM_HEADER_PAGE_TITLES));
						}
					} else {
						content.add(BomHeaderAtts);
						this.pageTitles
								.addAll((Collection) params.get(PDFProductSpecificationBOM2.BOM_HEADER_PAGE_TITLES));
					}
				}
			}
			// Start Manoj -9/20/2018
			Collection dests = null;
			Collection colorways = null;
			Collection sizes1 = null;
			Collection sizes2 = null;

			// If MATT_VARATIONS_ENABLED is false, then Single Bom Report Will
			// print only Top Level BOM Data
			if (MATT_VARATIONS_ENABLED) {

				dests = (Collection) params.get(BomDataGenerator.DESTINATIONS);
				colorways = (Collection) params.get(BomDataGenerator.COLORWAYS);
				sizes1 = (Collection) params.get(BomDataGenerator.SIZES1);
				sizes2 = (Collection) params.get(BomDataGenerator.SIZES2);
			}
			Collection sources = (Collection) params.get(BomDataGenerator.SOURCES);
			FlexBOMPart bomPart = (FlexBOMPart) params.get(BOMPDFContentGenerator.BOM_PART);
			Collection sections = bomPart.getFlexType().getAttribute("section").getAttValueList()
					.getSelectableKeys(com.lcs.wc.client.ClientContext.getContext().getLocale(), true);
			// End Manoj -9/20/2018

			HashMap tparams = new HashMap(params.size() + 6);
			tparams.putAll(params);

			// If there is no collection add a null value so the iterator works
			if (sources == null || sources.size() < 1) {
				sources = new ArrayList();
				sources.add(null);
			}

			if (dests == null || dests.size() < 1) {
				dests = new ArrayList();
				dests.add(null);
			}
			if (colorways == null || colorways.size() < 1) {
				colorways = new ArrayList();
				colorways.add(null);
			}
			if (sizes1 == null || sizes1.size() < 1) {
				sizes1 = new ArrayList();
				sizes1.add(null);
			}
			if (sizes2 == null || sizes2.size() < 1) {
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
				source = (String) sourcesIt.next();
				tparams.put("SOURCE_ID", source);
				destsIt = dests.iterator();

				while (destsIt.hasNext()) {
					dest = (String) destsIt.next();
					tparams.put("DESTINATION_ID", dest);
					colorwayIt = colorways.iterator();

					while (colorwayIt.hasNext()) {
						colorway = (String) colorwayIt.next();
						tparams.put("SKU_ID", colorway);
						sizes1It = sizes1.iterator();

						while (sizes1It.hasNext()) {
							size1 = (String) sizes1It.next();
							tparams.put("SIZE1_VAL", size1);
							sizes2It = sizes2.iterator();

							while (sizes2It.hasNext()) {
								size2 = (String) sizes2It.next();
								tparams.put("SIZE2_VAL", size2);
								sectionIter = sections.iterator();

								while (sectionIter.hasNext()) {
									section = (String) sectionIter.next();
									tparams.put(BomDataGenerator.SECTION, section);
									setSectionViewId(tparams);
									if (BOM_ON_SINGLE_PAGE) {
										spcontent.addAll(generateSingleBOMPage(tparams, document));
									} else {
										Collection sbp = generateSingleBOMPage(tparams, document);
										Iterator sbpi = sbp.iterator();
										PdfPTable stable = null;
										while (sbpi.hasNext()) {
											stable = (PdfPTable) sbpi.next();
											stable.setWidthPercentage(tableWidthPercent);
											this.pageTitles.add(getPageTitleText(tparams));											
										}
										content.addAll(sbp);
									}
								} // End section iterator
									// Footer
								if (FormatHelper.parseBoolean(
										(String) params.get(PDFProductSpecificationBOM2.PRINT_BOM_FOOTER))) {
									Collection BOMFooter = (Collection) params
											.get(PDFProductSpecificationBOM2.BOM_FOOTER_ATTS);
									if (BOMFooter != null && !BOMFooter.isEmpty()) {
										if (PDFProductSpecificationBOM2.BOM_FOOTER_SAME_PAGE) {
											if (BOM_ON_SINGLE_PAGE) {
												spcontent.addAll(BOMFooter);
											} else {
												content.addAll(BOMFooter);
												this.pageTitles.addAll((Collection) params
														.get(PDFProductSpecificationBOM2.BOM_FOOTER_PAGE_TITLES));
											}
										} else {
											content.add(BOMFooter);
											this.pageTitles.addAll((Collection) params
													.get(PDFProductSpecificationBOM2.BOM_FOOTER_PAGE_TITLES));
										}

									}
								}

								if (BOM_ON_SINGLE_PAGE) {
									PdfPTable fullBOMTable = new PdfPTable(1);
									fullBOMTable.setWidthPercentage(tableWidthPercent);

									Iterator sci = spcontent.iterator();
									PdfPTable e = null;
									PdfPCell cell = null;
									while (sci.hasNext()) {
										e = (PdfPTable) sci.next();
										cell = new PdfPCell(e);

										fullBOMTable.addCell(cell);
									}

									content.add(fullBOMTable);

									this.pageTitles.add(getPageTitleText(tparams));
									spcontent = new ArrayList();
								}
							} // End sizes2 iterator
						} // End Sizes1 iterator
					} // End Colorway iterator
				} // End Dests iterator
			} // Emd Source iterator
		}
		return content;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection generateSingleBOMPage(Map params, Document document) throws WTException {
		HBISingleBOMGenerator bomDG = new HBISingleBOMGenerator();
		Collection columns;
		bomDG.init(params);
		//added for routing view
		FlexBOMPart bomPart1 = (FlexBOMPart) params.get(BOMPDFContentGenerator.BOM_PART);
		String val=getAttListValue(bomPart1.getFlexType().getAttribute("hbiRoutingView"),bomPart1);
		// To get the all columns as in view selected on select techpack page
		if(!FormatHelper.hasContent(val)) {
		val="UW Routing View (Sys)";	
		}
		
		columns = bomDG.getTableColumns(bomPart1.getFlexType(),getView(val));


		// CC Task - Remove ‘Extra’ blank column in Routing Table output - Added
		// by Anjana 11-Apr-'19 -- START
		Iterator itr = columns.iterator();
		while (itr.hasNext()) {
			TableColumn col = (TableColumn) itr.next();

			if (!FormatHelper.hasContent(col.getHeaderLabel()) || col.getHeaderLabel().equals("")) {
				itr.remove();
			}
		}
		// CC Task - Remove ‘Extra’ blank column in Routing Table output - Added
		// by Anjana 11-Apr-'19 --END
		
		// Collection data = new ArrayList();
		Collection data = bomDG.getBOMData();
		Collection<ElementTableData> bomdata = new ArrayList<ElementTableData>();
		Collection content = new ArrayList();
		
		Iterator itrdata = data.iterator();
		int count = 1;
		while(itrdata.hasNext()) {
			ElementTableData tdata = (ElementTableData) itrdata.next();
			if(!tdata.getData("highLight").equals("HIGHLIGHT_PURPLE")) {
				bomdata.add(tdata);
			} else {
				content.addAll(generatePDFPage(bomdata, columns, document, params));

				bomdata.clear();

				count++;
			}
		}
		content.addAll(generatePDFPage(bomdata, columns, document, params));

		bomdata.clear();		

		return content;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lcs.wc.flexbom.gen.BOMPDFContentGenerator#getPageTitleText(java.util.
	 * Map)
	 */
	@SuppressWarnings("rawtypes")
	public String getPageTitleText(Map params) {
		String title = "";
		FlexBOMPart bomPart = (FlexBOMPart) params.get(BOMPDFContentGenerator.BOM_PART);

		title = bomPart.getName() + " -- " + params.get(PDFProductSpecificationGenerator2.REPORT_NAME);

		return title;
	}

	/**
	 * 
	 * @param bomPart
	 * @return
	 * @throws WTException
	 *             GP teck pack customizations
	 *             Select_teck_pack_page_requirements - 09/20/2018
	 */
	public boolean isValidateGPBOMTypeForBOMReport(FlexBOMPart bomPart) throws WTException {
		boolean bomGPTachPackStatus = true;
		LCSProduct prodObj = null;

		String bomPartFlexTypePath = bomPart.getFlexType().getFullName(true);
	//	WTPartMaster wtPartMaster = bomPart.getOwnerMaster();
	//	LCSPart lcsPart = (LCSPart) VersionHelper.latestIterationOf(wtPartMaster);
		LCSPart lcsPart = (LCSPart) VersionHelper.latestIterationOf(bomPart.getOwnerMaster());
		if (lcsPart instanceof LCSProduct) {
			prodObj = (LCSProduct) lcsPart;
			String productFlexTypePath = prodObj.getFlexType().getFullName(true);

			if ((BASIC_CUT_AND_SEW_GARMENT.equalsIgnoreCase(productFlexTypePath)||LABEL_TYPE.equalsIgnoreCase(productFlexTypePath))
					&& !Arrays.asList(VALID_BOM_TYPE_PATHS.split(",")).contains(bomPartFlexTypePath)) {
				bomGPTachPackStatus = false;
			} else if (BASIC_CUT_AND_SEW_SELLING.equalsIgnoreCase(productFlexTypePath)
					&& !Arrays.asList(VALID_BOM_TYPE_PATHS.split(",")).contains(bomPartFlexTypePath)) {
				bomGPTachPackStatus = false;
			}
			
		}
		return bomGPTachPackStatus;
	}
	private static String getAttListValue(FlexTypeAttribute att, FlexTyped typed) {
		String key = "";
		String value = "";
		try {
			key = att.getAttKey();
			value = (String) typed.getValue(key);
			AttributeValueList valueList = att.getAttValueList();
			if (valueList != null) {
				value = valueList.getValue(value, null);
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return value;
	}
	private static ColumnList getView(String viewName) {
		PreparedQueryStatement statement = new PreparedQueryStatement();
		ColumnList view=null;;

		try {
			statement.appendSelectColumn(
					new QueryColumn(ColumnList.class, "thePersistInfo.theObjectIdentifier.id"));
			statement.appendFromTable(ColumnList.class);
			statement.appendAndIfNeeded();
			statement.appendCriteria(new Criteria("ColumnList", "DISPLAYNAME", viewName, Criteria.EQUALS));

			SearchResults results = LCSQuery.runDirectQuery(statement);
			//
			if(results != null && results.getResultsFound() > 0)
			{
				FlexObject flexObj = (FlexObject) results.getResults().firstElement();
				view = (ColumnList) LCSQuery.findObjectById("OR:com.lcs.wc.report.ColumnList:"+flexObj.getString("ColumnList.IDA2A2"));
			}			
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return view;
			}
}
