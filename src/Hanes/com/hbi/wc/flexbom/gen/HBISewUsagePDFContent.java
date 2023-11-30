package com.hbi.wc.flexbom.gen;

import java.util.*;

import com.hbi.wc.product.HBIPDFProductSpecificationBOM2;
import com.hbi.wc.product.HBIPDFProductSpecificationGenerator2;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.*;
import com.lcs.wc.util.*;
import com.lcs.wc.product.*;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.specification.SpecOwner;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import wt.part.WTPartMaster;
import wt.util.*;
import com.lcs.wc.flexbom.gen.*;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPart;
/**
*
* @author Manoj From UST
* @Date Oct 4, 2018, 12:58 PM
* 
*
*       This class is implemented by taking the code from HBISizePDFContent and
*       modified as per requirement
* 
*/
public class HBISewUsagePDFContent extends BOMPDFContentGenerator {
    
    private static final boolean DEBUG = LCSProperties.getBoolean("com.lcs.wc.flexbom.gen.HBISewUsagePDFContent.verbose");
    private static final int DEBUG_LEVEL = Integer.parseInt(LCSProperties.get("com.lcs.wc.flexbom.gen.HBISewUsagePDFContent.verboseLevel", "1"));
    public float tableWidthPercent = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.HBISewUsagePDFContent.tableWidthPercent", "95.0"))).floatValue();
    private static final String BASIC_CUT_AND_SEW_GARMENT = LCSProperties.get("hbi.gp.product.type");
   // private static final String MATERIAL_HBI_ATTRIBUTION_BOM = LCSProperties.get("hbi.gp.attributionbom.type");
   //private static final String MATERIAL_HBI_LABEL_BOM = LCSProperties.get("hbi.gp.labelbom.type");
   // private static final String MATERIAL_HBI_ROUTING_BOM = LCSProperties.get("hbi.gp.routingbom.type");
   // private static final String MATERIAL_HBI_GARMENT_SOURCED_BOM =LCSProperties.get("hbi.gp.sourcedbom.type");
    private static final String BOM_COLORWAY = LCSProperties.get("hbi.gp.colorwaybom.type","BOM\\Materials\\HBI\\Colorway");
	//private static final String BOM_GARMENT_SEW = LCSProperties.get("hbi.gp.sewbom.type","BOM\\Materials\\HBI\\Garment Sew");
	public static final String  GARMENT_BOM_TYPE_PATH = LCSProperties.get("bomreport.sewusagebom.garmentproduct.bomtypes");
  	private static final String PATTERN_SPECID = "PATTERN_SPECID";
	private static final String PATTERN_SEW_BOM = LCSProperties.get("hbi.pp.sewbom.type","BOM\\Materials\\HBI\\Pattern Product Sew BOM");
	private static final String BOM = "BOM";
	private static final String SECTION_GARMENT = "garment";
    private Map<String, Object> params = new HashMap<String, Object>();
	private FlexType patternBomType;
	private FlexType bomType;
    /** Creates a new instance of HBISewUsagePDFContent */
    public HBISewUsagePDFContent() {
    	
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
	 * 
	 *         The below method returns the sew usage BOM Data 
	 * 
	 *         If the Product type is GP and the Spec is linked to a pattern product
	 *         spec ,and  pattern product spec is having atleast one pattern sew
	 *         usage bom component.
	 * 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection getPDFContentCollection(Map sewParams, Document document) throws WTException {
		debug("############Started getPDFContentCollection Method #######");
		debug(1, "SizePDFContent.getPDFContentCollection()");
		params = sewParams;
		Collection content = new ArrayList();
		Collection spcontent = new ArrayList();
		Collection sectionPageTitles = new ArrayList();

		HBISewUsageGenerator bomDG = new HBISewUsageGenerator();
		FlexBOMPart bomPart = (FlexBOMPart) params.get(BOMPDFContentGenerator.BOM_PART);
		FlexSpecification spec = (FlexSpecification) LCSQuery
				.findObjectById((String) params.get(PDFProductSpecificationGenerator2.SPEC_ID));		
		boolean gpBOMTechpackStatus = isValidateGPBOMTypeForSewUsageBOMReport(bomPart, spec);

		boolean isValidSpec = isSpecLinkedToPatternProdSpec(spec);
		/*
		 * If the Product type is GP and Spec is linked to a pattern product spec
		 * and pattern product spec is having atleast one pattern sew usage bom.
		 */
		if (gpBOMTechpackStatus && isValidSpec) {
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
			int maxPerPage = ((Integer) params.get(PDFProductSpecificationGenerator2.SIZES_PER_PAGE)).intValue();

			// Start Manoj
			// Get Pattern product sew BOM data
			Collection patterBomData = getPatternSewBomData(params);
			Collection sections = getSections(bomPart);
			// End Manoj

			String section = "";
			Iterator sectionIter = sections.iterator();
			Iterator sizeIt = null;

			// Create collection of arrayLists of sizes
			Collection sizesArray = splitItems(allSizes, maxPerPage);
			debug("sizesArray::"+sizesArray);
			while (sectionIter.hasNext()) {
				section = (String) sectionIter.next();
				tparams.put(BomDataGenerator.SECTION, section);
				setSectionViewId(tparams);
				sizeIt = sizesArray.iterator();
				
				Collection sizesThisRun = new ArrayList();

				while (sizeIt.hasNext()) {
					sizesThisRun = (Collection) sizeIt.next();
					debug("sizeIt::"+sizesThisRun);
					if (usingSize1) {
						tparams.put(BomDataGenerator.SIZES1, sizesThisRun);
					} else {
						tparams.put(BomDataGenerator.SIZES2, sizesThisRun);
					}
					bomDG.init(tparams);
					Collection data = bomDG.getBOMData();
					debug("SEW USAGE VIEW_ID.."+(String) params.get("VIEW_ID"));
					Collection columns = bomDG.getTableColumns();

					// Start Manoj 09/20/2018
					if (patterBomData != null) {
						Collection gpSewUsageBomData = getSewUsageFromPatternBomData1(patterBomData, patternBomType,
								data, bomType, sizesThisRun);
						spcontent.addAll(generatePDFPage(gpSewUsageBomData, columns, document, tparams));
					}
					// End Manoj 09/20/2018
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
		debug("############Completed getPDFContentCollection Method #######");
		return content;

	}

	/**
	 * @param bomPart
	 * @return
	 * @throws LCSException
	 * @throws WTException
	 * @author Manoj
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection getSections(FlexBOMPart bomPart) throws LCSException, WTException {
		debug("############Started getSections Method #######");
		
		Collection section = new Vector();
		if(bomPart.getFlexType().getFullName(true).equals(BOM_COLORWAY)){
			section.add(SECTION_GARMENT);
			return section;
		}
		section= bomPart.getFlexType().getAttribute("section").getAttValueList().getSelectableKeys(com.lcs.wc.client.ClientContext.getContext().getLocale(), true);
		debug("############Completed getSections Method #######");
		return section;
	}

	@SuppressWarnings("rawtypes")
	private boolean isSpecLinkedToPatternProdSpec(FlexSpecification spec2) throws WTException {
		debug("############Started isSpecLinkedToPatternProdSpec Method #######");
		if(spec2!=null){
		LCSProduct product = (LCSProduct)LCSQuery.findObjectById((String) params.get(PDFProductSpecificationGenerator2.PRODUCT_ID));
		//Get the linked parent spec of a garment product spec's.
		ArrayList specToSpecLinks = (ArrayList) FlexSpecQuery.findSpecToSpecLinks(product, null, null, spec2, false, true);
		
		//Get the linked pattern product of Garment product .
		LCSProduct patternProd= HBIPDFProductSpecificationGenerator2.findPatternProdLinkedToGP(product);
		
		//Check if the spec having any linked parent spec.
		if(!specToSpecLinks.isEmpty() && specToSpecLinks.get(0) !=null && patternProd!=null){
			FlexObject gpObj=(FlexObject) specToSpecLinks.get(0);
			String linkedSpecida2 =gpObj.getData("LINKEDSPECID");
			
			//Get all the Spec's associated to linked pattern product.
			//SearchResults pSpecToSpecLink =FlexSpecQuery.findSpecsByOwner((WTPartMaster) patternProd.getMaster(), null, null, null);
			SearchResults pSpecToSpecLink =FlexSpecQuery.findSpecsByOwner((SpecOwner) patternProd.getMaster(), null, null, null);
			Iterator results = pSpecToSpecLink.getResults().iterator();
			
			//Verify if the GP's parent spec is a linked pattern product's spec
			while (results.hasNext()) {
				FlexObject obj = (FlexObject) results.next();
				if(obj.get("FLEXSPECIFICATION.BRANCHIDITERATIONINFO").equals(linkedSpecida2))
				{
					params.put(PATTERN_SPECID,linkedSpecida2);
					return true;
				}
			}
			
		}
	}
		debug("############Completed isSpecLinkedToPatternProdSpec Method #######");
		return false;
	
	}

	/**
	 * @param bomPart
	 * @return
	 * @throws WTException
	 * @author Manoj
	 * GP teck pack customizations  Select_teck_pack_page_requirements - 09/20/2018
	 */
	private boolean isValidateGPBOMTypeForSewUsageBOMReport(FlexBOMPart bomPart, FlexSpecification spec) throws WTException {

		debug("############Started isValidateGPBOMTypeForSewUsageBOMReport Method#######");
 		boolean bomGPTachPackStatus = true;
 		LCSProduct prodObj = null;
 		bomType =bomPart.getFlexType();
 		
 		String bomPartFlexTypePath = bomType.getFullName(true);
 		//Logic for printing only Native Colorway BOM for Sew Usage
 		if(spec!=null) {
 			Collection<?> components = FlexSpecQuery.getSpecToComponentObjectsData(spec);
 			FlexObject compFO = null;
			Iterator<?> compIterator = components.iterator();
			boolean isBOMSpec = false;
			  while(compIterator.hasNext()){
				  compFO = (FlexObject) compIterator.next();
				 // System.out.println("Sew Usage BOM logic "+compFO);
				  if(compFO.getData("COMPONENT_TYPE").equals("BOM") && bomPart.getName().equals(compFO.getData("NAME"))) {
					//  System.out.println("bomPart Idendity " + bomPart.getName());
					 // System.out.println("EQUALS");
					  isBOMSpec = true;
					  break;
				  }
				  //System.out.println("inside loop");
			  }
			  //System.out.println("outside loop");
			  if(!isBOMSpec) {
				  return false;
			  }
 		}
 		//WTPartMaster wtPartMaster = bomPart.getOwnerMaster();
 		BOMOwner wtPartMaster = bomPart.getOwnerMaster();
 		LCSPart lcsPart = (LCSPart)VersionHelper.latestIterationOf(wtPartMaster);	
 		if(lcsPart instanceof LCSProduct)
 		{
 			prodObj = (LCSProduct)lcsPart;
 			String productFlexTypePath = prodObj.getFlexType().getFullName(true);
 			
 			if(Arrays.asList(GARMENT_BOM_TYPE_PATH.split(",")).contains(bomPartFlexTypePath)) {
 				}
 			
 			if(BASIC_CUT_AND_SEW_GARMENT.equalsIgnoreCase(productFlexTypePath) && !Arrays.asList(GARMENT_BOM_TYPE_PATH.split(",")).contains(bomPartFlexTypePath))
 			{
 				bomGPTachPackStatus = false;
 			}
 		}
 		debug("############Completed isValidateGPBOMTypeForSewUsageBOMReport Method #######");
		return bomGPTachPackStatus;
     }
    
	@SuppressWarnings({ "rawtypes", "unused" })
	private Hashtable getSewUsageFromPatternBomData(Collection patterBomData, FlexType patternType) throws WTException {
		debug("############Started getSewUsageFromPatternBomData Method #######");
		Collection sewUsage = new Vector();
		Hashtable<String, FlexObject> ppMap = new Hashtable<String, FlexObject>();
		Iterator itr = patterBomData.iterator(); // Pattern product BOM Data
		while (itr.hasNext()) {
			Object o = itr.next();
			if (o instanceof FlexObject) {
				FlexObject flex = (FlexObject) o;
				String key1 = getDBColumnName(patternType, "hbiGarmentUse");
				String key2 = getDBColumnName(patternType, "hbiUOM");
				String ppKey = flex.get("FLEXBOMLINK." + key1) + ":" + flex.get("FLEXBOMLINK." + key2);
				ppMap.put(ppKey, (FlexObject) o);
			}
		}
		debug("############Completed getSewUsageFromPatternBomData Method #######");
		return ppMap;
	}

/**
 * @param compare1
 * @param flex1
 * @param gpBomData
 * @param sizesThisRun
 * @return
 * @throws WTException 
 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Collection getSewUsageFromPatternBomData1(Collection patterBomData,FlexType patternType, Collection gpBomData, FlexType bomType,Collection sizesThisRun) throws WTException {
		debug("############Started getSewUsageFromPatternBomData1 Method #######");
		Hashtable<String, FlexObject> ppMap = getSewUsageFromPatternBomData(patterBomData, patternType);
		
		Collection sewUsageData = new Vector();
		String key1;
		String key2;
		Iterator itr = gpBomData.iterator(); 
		while (itr.hasNext()) {
			Object o = itr.next();
			if (o instanceof FlexObject) {
				FlexObject flex2 = (FlexObject) o;
				key1 = "FLEXBOMLINK." + getDBColumnName(bomType, "hbiGarmentUse");

				FlexType mat= FlexTypeCache.getFlexTypeFromPath("Material");
				key2 = "LCSMATERIAL." + getDBColumnName(mat, "hbiUsageUOM");

				String gpKey = flex2.get(key1) + ":" + flex2.get(key2);    // To get garment product key
				if(ppMap.containsKey(gpKey)) {
					flex2 = getSewUsageFromPatternBomData2(ppMap.get(gpKey), flex2, sizesThisRun);
					sewUsageData.add(flex2);
				}
				/*else {
					sewUsageData.add(flex2);
				}*/
			}
		}
		debug("############Completed getSewUsageFromPatternBomData1 Method #######");
		return sewUsageData;
	}

/**
 * @param flex1
 * @param flex2
 * @param sizesThisRun
 * @return
 * @author Manoj
 */
	@SuppressWarnings("rawtypes")
	private FlexObject getSewUsageFromPatternBomData2(FlexObject flex1, FlexObject flex2, Collection sizesThisRun) {
		debug("############Started getSewUsageFromPatternBomData2 Method #######");
		FlexObject obj = flex2;
		Iterator itr = sizesThisRun.iterator();
		while (itr.hasNext()) {
			String size = (String) itr.next();
			String sizekey = size.trim() + ".DISPLAY_VAL";
			String usageperdozen = flex1.getData(sizekey);

			debug("sizekey:" + sizekey + "-" + "usageperdozen:" + usageperdozen);
			obj.put(sizekey, usageperdozen);

		}
		debug("############Completed getSewUsageFromPatternBomData2 Method #######");
		return obj;
	}

	/**
	 * @param patternParams
	 * @return
	 * @throws WTException
	 * @author Manoj
	 */
	@SuppressWarnings("rawtypes")
	private Collection<?> getPatternSewBomData(Map<String, Object> patternParams) throws WTException {
		debug("############Started getPatternSewBomData Method #######");
		HBISizeGenerator bomDG = new HBISizeGenerator();
		Collection<?> allSizes = (Collection<?>) patternParams.get(BomDataGenerator.SIZES1);
		boolean usingSize1 = true;
		if ("size2".equalsIgnoreCase((String) patternParams.get(BomDataGenerator.USE_SIZE1_SIZE2))) {
			usingSize1 = false;
		}
		if (usingSize1) {
			patternParams.put(BomDataGenerator.SIZES1, allSizes);
		} else {
			patternParams.put(BomDataGenerator.SIZES2, allSizes);
		}
		HBIPDFProductSpecificationBOM2 specBom = new HBIPDFProductSpecificationBOM2();
		FlexSpecification pSpec = (FlexSpecification) LCSQuery.findObjectById(
				"VR:com.lcs.wc.specification.FlexSpecification:" + (String) patternParams.get(PATTERN_SPECID));

		// Get Pattern sew Bom Data from pattern spec
		FlexBOMPart bomPart = getPatternBomPart(pSpec);

		if (bomPart != null) {
			// FlexBOMPart gpBOM = (FlexBOMPart)params.get(BOMPDFContentGenerator.BOM_PART);
			Collection sections = getSections(bomPart);
			String section = "";
			Iterator sectionIter = sections.iterator();
			while (sectionIter.hasNext()) {

				section = (String) sectionIter.next();
				debug("-adding section :  " + section);
				patternParams.put(BomDataGenerator.SECTION, section);
				setSectionViewId(patternParams);
				patternParams.remove("RAW_DATA");
				patternParams.put("RAW_DATA", specBom.getBOMData(bomPart));
				bomDG.init(patternParams);
				Collection data = bomDG.getBOMData();
				debug("############Completed getPatternSewBomData Method #######");
				return data;
			}
		}
		debug("############Completed getPatternSewBomData Method #######");
		return null;
	}

	/**
	 * @param pSpec
	 * @return
	 * @throws WTException
	 * @author Manoj
	 */
	private FlexBOMPart getPatternBomPart(FlexSpecification pSpec) throws WTException {
		debug("############Started getPatternBomPart Method #######");

		Collection<FlexBOMPart> boms = FlexSpecQuery.getSpecComponents(pSpec, BOM);
		if (boms != null) {
			debug("Pattern spec bom componenets count::" + boms.size());
			debug("Valid Pattern Bom Type::" + PATTERN_SEW_BOM);
			for (FlexBOMPart bom : boms) {
				debug("Pattern Bom Name::" + bom.getName() + "\n Bom type:" + bom.getFlexType().getFullName(true));
				patternBomType = bom.getFlexType();
				if (patternBomType.getFullName(true).equals(PATTERN_SEW_BOM)) {
					return bom;
				}
			}
		}
		debug("############Completed getPatternBomPart Method #######");
		return null;
	}

	/**
	 * @param flextype
	 * @param key
	 * @return
	 * @throws WTException
	 * @author Manoj
	 *  The below method return DB column name
	 *  for the flex object type and flex attribute key name passed 
	 */
	public static String getDBColumnName(FlexType flextype, String key) throws WTException {
		debug("############Started getDBColumnName Method #######");
		FlexTypeAttribute typeAttr = flextype.getAttribute(key);
		//Changes by Wipro Upgrade Team
		//String dbColumnName = typeAttr.getColumnPrefix().concat(typeAttr.getAttColumn());
		String dbColumnName = typeAttr.getColumnName();
		debug("############Completed getDBColumnName Method #######");
		return dbColumnName;
	}

	/////////////////////////////////////////////////////////////////////////////
   public static void debug(String msg){debug(msg, 1); }
   public static void debug(int i, String msg){debug(msg, i); }
   public static void debug(String msg, int i){
	  if(DEBUG && i <= DEBUG_LEVEL) System.out.println(msg);
   }

}
