package com.sportmaster.wc.flexbom.gen;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;
import org.apache.log4j.Logger;

import com.lcs.wc.client.web.*;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.*;
import com.lcs.wc.flexbom.gen.BOMPDFContentGenerator;
import com.lcs.wc.flexbom.gen.BomDataGenerator;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.*;
import com.lcs.wc.report.ColumnList;
import com.lcs.wc.sizing.ProductSizeCategory;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.util.*;
import wt.fc.ReferenceFactory;
import wt.util.WTException;
import wt.util.WTMessage;

/**
 * This class will be the core class to generate the report based on the user
 * selection on filter page and will call methods to fetch all required BOM
 * data, Product and Season information and prepare the pdf object. This class
 * will be called when the user selects "BOM: Variations" from the list of
 * available components
 *
 */
public class SMBOMVariationsGenerator extends BomDataGenerator {

	private static final Logger LOGGER = Logger.getLogger(SMBOMVariationsGenerator.class);

	public static final String MATERIAL_TYPE_PATH = "MATERIAL_TYPE_PATH";

	private static final String COLORS = "COLORS";
	private static final String SIZE1 = "SIZE1";
	// private static final String SIZE2 = "SIZE2";
	private static final String DESTINATION = "DESTINATION";
	private static final String SKU = "SKU";

	private static final String COLOR_NAME = "COLORNAME";
	private static final String COLOR_DESC = "COLORDESC";
	private static final String COLOR_ID = "COLORID";
	private static final String COLOR_HEX = "COLORHEX";
	private static final String COLOR_THUMB = "COLORTHUMB";
	private static final String MATERIALCOLOR_ID = "MATERIALCOLORID";
	private static final String FLEXBOMLINK_SIZE1 = "FLEXBOMLINK.SIZE1";
	private static final String LCSCOLOR_COLORNAME = "LCSCOLOR.COLORNAME";
	private static final String COLORWAY_COLUMN = "COLORWAYCOLUMN";
	private static final String FLEXBOMLINK = "FLEXBOMLINK.";
	private static final String Colorways = "Colorways";
	private static final String FLEXBOMLINK_IDA3D5 = "FLEXBOMLINK.IDA3D5";

	/// VRD EXTENSION START: Removing automatic adding of columns
	private static final String DEFAULT_COLUMNS = LCSProperties.get(
			"com.lcs.wc.flexbom.gen.BOMVariationGenerator.defaultColumns",
			"SOURCES,partName,materialDescription,supplierName,Material.materialPrice,Material.unitOfMeasure,BOM.quantity,Colorways");
	/// VRD EXTENSION END

	// Initializations
	private String materialNameDbColName;
	private String highlightAtt = "ATT1";
	private String materialDescDbColName;
	private String componentNameDbColName;
	private String colorNameDbColName;
	private String priceDbColName;
	private String componentNameDisplay;

	private String priceOverrideDbColName;
	private String quantityDbColName;
	private String lossAdjustmentDbColName;
	private String rowTotalDbColName;

	private String wcPartNameDisplay = "";

	private String priceKey;
	private String overrideKey;
	private String quantityKey;
	private String lossAdjustmentKey;
	private String rowTotalKey;

	private static String materialDescription = "materialDescription";
	private static String colorDescription = "colorDescription";
	private String partName = "partName";
	private String colorwaysLabel = WTMessage.getLocalizedMessage(RB.FLEXBOM, "colorwaysLabel", RB.objA);
	private String colorLabel = WTMessage.getLocalizedMessage(RB.COLOR, "color_LBL", RB.objA);
	private String size1Label = WTMessage.getLocalizedMessage(RB.QUERYDEFINITION, "size1", RB.objA);
	// private String size2Label = WTMessage.getLocalizedMessage(RB.QUERYDEFINITION,
	// "size2", RB.objA);

	// Get property values
	private float partNameWidth = (new Float(
			LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.partNameWidth", "1.5"))).floatValue();
	private float materialNameWidth = (new Float(
			LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.materialNameWidth", "1.25"))).floatValue();
	private float supplierNameWidth = (new Float(
			LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.supplierNameWidth", "1.25"))).floatValue();
	private float colorwayWidth = (new Float(
			LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.colorwayWidth", "0.75"))).floatValue();

	private int imageWidth = (Integer
			.valueOf(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.matThumbWidth", "75"))).intValue();
	private int imageHeight = (Integer
			.valueOf(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.matThumbHeight", "0"))).intValue();

	/// VRD EXTENSION START: Configuration for pricing calculations
	private static final String COLOR_SPECIFIC_PRICE = LCSProperties
			.get("com.vrd.costsheet.CostSheetBOMPlugin.ColorSpecificPrice");
	private static final String PRICE_OVERRIDE = LCSProperties.get("com.vrd.costsheet.CostSheetBOMPlugin.PriceOverride",
			"priceOverride");
	private static final String MATERIAL_QUANTITY = LCSProperties
			.get("com.vrd.costsheet.CostSheetBOMPlugin.MaterialQuantity", "quantity");
	private static final String LOSS_ADJUSTMENT = LCSProperties
			.get("com.vrd.costsheet.CostSheetBOMPlugin.LossAdjustment", "lossAdjustment");
	/// VRD EXTENSION END
	// SM EXTENSION START
	private static final String CIF_PERCENT = LCSProperties.get("com.sportmaster.BOMLink.CIFPercent", "smCIFPercent");
	private static final String CIF_PRICE = LCSProperties.get("com.sportmaster.BOMLink.CIFPrice", "smCIFPrice");
	// SM EXTENSION STOP

	private static final String DISPLAY_VAL = "DISPLAY_VAL";
	private Map<String, Collection<String>> dimensionMap;

	private FlexType materialType;
	private FlexType supplierType;
	private FlexType bomType;

	/** for other attributes */
	private String cifPercent = "";
	private String cifPrice = "";
	private String sectioncolum = "";

	private Date reqDate;

	private Collection bomDataMergedFinal = new ArrayList();
	private boolean bVarMerge;
	
	private FlexBOMPart bomPart;

	/**
	 * This method is returns the final dataSet format.
	 * 
	 * @param params
	 */
	public Collection getBOMData() throws WTException {
		LOGGER.debug("\n this.dataSet SIZE==" + this.dataSet.size());
		return this.dataSet;
	}

	/**
	 * This method is the entry point to initialize and get the bom data in required
	 * format.
	 * 
	 * @param params
	 */
	public void init(Map<String, Object> params) throws WTException {
		LOGGER.debug("In init method - Start ");
		super.init(params);
		SMBOMVariationsUtil smBOMVarUtil = new SMBOMVariationsUtil();
		LOGGER.debug("In init method - params== " + params);
		if (params != null) {
			// Initializations
			initReportableFields(params);

			Collection bomData;
			Collection bomDataMerged;

			// Get BOM Part object
			bomPart = (FlexBOMPart) params.get(BOMPDFContentGenerator.BOM_PART);
			bomData = findMergedBOMData((String) params.get(BomDataGenerator.SECTION), bomPart);

			LOGGER.debug("In init method - bomData== " + bomData);
			LOGGER.info("In init method - bomData== " + bomData.size());

			if (bomData != null) {
				this.dataSet = groupDataToBranchId(bomData, "FLEXBOMLINK.BRANCHID", "FLEXBOMLINK.MASTERBRANCHID",
						"FLEXBOMLINK.SORTINGNUMBER");
				this.dataSet = SortHelper.sortFlexObjectsByNumber(bomData, "FLEXBOMLINK.SORTINGNUMBER");

				// Final dataset (for all variation combination) to be merged
				bomDataMerged = this.dataSet;
				// Assign bomDataMerged to bomDataMergedFinal, in case no size/ dest/ cw variations chosen by users,
				// this will be considered as final list for merging
				bomDataMergedFinal = bomDataMerged;

				// Merge Sizes - If user selects Size1, then first, Compare all rows having same data (except
				// Size column) - in case match is found, club the sizes
				getSizeMergedData(bomDataMergedFinal);

				// Merge Destinations - If user selects Dest, then, Compare all rows having same data (except Dest
				// column) - in case match is found, club the Dest
				getDestMergedData(bomDataMergedFinal);

				// Merge Colorways - If user selects SKU then, Compare all rows having same data (except CW
				// columns) - in case match is found, place the Colorways in appropriate column
				// in merged row
				getColorwayMergedData(bomDataMergedFinal);

				// If user does not select Size/ Dest/ Colorways while generating TP (Rare
				// scenario)
				mergeFlexObjectsNoVars(bomDataMerged);
				//FLEXBOMLINK.BRANCHID
				//bomDataMergedFinal = SortHelper.sortFlexObjectsByNumber(bomDataMergedFinal, "FLEXBOMLINK.SORTINGNUMBER");
				bomDataMergedFinal = new FlexObjectSorter().sortFlexObjects(bomDataMergedFinal,
						(List<String>) smBOMVarUtil.getSortCriteria(), new HashMap());
				/*// Sort flexobjet based on Material, Supplier fllowed by consumption in desc
				// order
				bomDataMergedFinal = new FlexObjectSorter().sortFlexObjects(bomDataMergedFinal,
						(List<String>) smBOMVarUtil.getSortCriteria(), new HashMap());*/
				// Set this.dataSet
				this.dataSet = bomDataMergedFinal;
			}
			LOGGER.debug("In init method - this.dataSet FINAL== " + this.dataSet);
			LOGGER.info("In init method - this.dataSet FINAL== " + this.dataSet.size());

			getSizeCategoryLabel(params); 

		}
		LOGGER.debug("In init method - End ");
	}

	/**
	 * Merge rows when no variations are chosen
	 * 
	 * @param bomDataMerged
	 * @throws WTException
	 */
	private void mergeFlexObjectsNoVars(Collection bomDataMerged) throws WTException {
		// If user does not select Size/ Dest/ Colorways while generating TP (Rare
		// scenario)
		if (!bVarMerge && bomDataMerged.size() > 0) {
			bomDataMergedFinal = mergeFlexObjectsPerSection(bomDataMerged, "");
		}
	}

	/**
	 * Method to call merging logic to merge cw columns including dest and size for comparison
	 * 
	 * @param bomDataMerged
	 * @return
	 * @throws WTException
	 */
	private void getColorwayMergedData(Collection bomDataMerged) throws WTException {
		// If user selects SKU then, Compare all rows having same data (except CW
		// columns) - in case match is found, place the Colorways in appropriate column
		// in merged row
		if (dimensionMap.get(SKU).size() > 0) {
			// if bomDataMerged2 (merge has happened for both Size and Dest), then use this
			// merged data to compare for SKU level merging
			// if merge has happened for Size (in getSizeMergedData method), then cw merge will happen on top of size merged data
			// if merge has happened for dest (in getDestMergedData method), then cw merge will happen on top of dest merged data
			// else merge will happen on original data
			// Assign mergeFlexObjectsPerSection cw merged data to bomDataMergedFinal
			// (in case no size/ dest is chosen) this will be considered as final set
			bomDataMergedFinal = mergeFlexObjectsPerSection(bomDataMerged, SKU);
			bVarMerge = true;
		}
	}

	/**
	 * Method to call merging logic to merge dest columns including size and cw for comparison
	 * 
	 * @param bomDataMerged
	 * @return
	 * @throws WTException
	 */
	private void getDestMergedData(Collection bomDataMerged) throws WTException {
		// If user selects Dest, then, Compare all rows having same data (except Dest
		// column) - in case match is found, club the Dest
		if (dimensionMap.get(DESTINATION).size() > 0) {
			
			// if merge has happened for Size (in bomDataMergedFinal method), then merge will happen on top of size merged data
			// else merge will happen on original data 
			// Assign mergeFlexObjectsPerSection dest merged data to bomDataMergedFinal
			// (in case no size/ cw is chosen) this will be considered as final set
			bomDataMergedFinal = mergeFlexObjectsPerSection(bomDataMerged, DESTINATION);
			bVarMerge = true;
		}
	}

	/**
	 * Method to call merging logic to merge size columns including dest and cw for comparison
	 * 
	 * @param bomDataMerged
	 * @return
	 * @throws WTException
	 */
	private void getSizeMergedData(Collection bomDataMerged) throws WTException {
		// If user selects Size1, then first, Compare all rows having same data (except
		// Size column) - in case match is found, club the sizes
		if (dimensionMap.get(SIZE1).size() > 0) {
			// Call merge method to merge the size columns			
			// Assign mergeFlexObjectsPerSection size merged data to bomDataMergedFinal
			// (in case no dest/ cw is chosen) this will be considered as final set
			bomDataMergedFinal = mergeFlexObjectsPerSection(bomDataMerged, SIZE1);
			bVarMerge = true;
		}
	}

	/**
	 * Method to get size label
	 * 
	 * @param params
	 * @throws WTException
	 */
	private void getSizeCategoryLabel(Map<String, Object> params) throws WTException {
		// Get product size category to display size1 label
		if (FormatHelper.hasContent((String) params.get(PDFProductSpecificationGenerator2.PRODUCT_SIZE_CAT_ID))) {
			ProductSizeCategory productSizeCategory = (ProductSizeCategory) LCSQuery
					.findObjectById((String) params.get(PDFProductSpecificationGenerator2.PRODUCT_SIZE_CAT_ID));
			size1Label = productSizeCategory.getSizeRange().getFullSizeRange().getSize1Label();
		}
	}

	/**
	 * Method to get the bom data based on user selected criteria
	 * 
	 * @param currentSection
	 * @param bomPart
	 * @return
	 * @throws WTException
	 */
	private Collection findMergedBOMData(String currentSection, FlexBOMPart bomPart) throws WTException {
		SMBOMVariationsUtil smBOMVarUtil = new SMBOMVariationsUtil();
		// Get the view attributes and add to usedAttKeys collection
		Collection usedAttKeys = getUsedAttKeysForQuery();

		FlexType materialFlexType = bomPart.getFlexType().getReferencedFlexType(ReferencedTypeKeys.MATERIAL_TYPE);
		// Get the pricing date - last checked in bom date
		String pricingDateDateString = FormatHelper.format(wt.fc.PersistenceHelper.getModifyStamp(bomPart));
		if (!FormatHelper.hasContent(pricingDateDateString)) {
			pricingDateDateString = FormatHelper.format(new Date());
		}

		try {
			reqDate = new Timestamp(FormatHelper.parseDate(pricingDateDateString).getTime());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		// If the dimension map hasn't been initialized, build the map
		// ex. dimensionMap --- {SIZE1=[ M, L], SIZE2=[XL, XXL, XXXL],
		// DESTINATION=[OR:com.lcs.wc.product.ProductDestination:1714092],
		// SOURCE=[1452716], SKU=[1453042, 1453152]}
		if (dimensionMap == null) {
			initDimensionMap(bomPart);
		}

		// Call this method to get all possible combination of variations selected by
		// user while generating TP
		// ex format:SKUID-SIZENAME-DESTOID (if any dimension is not selected by user,
		// will be replaced by '-'
		// user choose - Red CW, Blue CW : M Size : RU Destination
		// The total possible combinations is 2 (Red:M:RU, Blue:M:RU)
		// varList Format:
		// [1453397|~*~|M|~*~|OR:com.lcs.wc.product.ProductDestination:1475739|~*~|,
		// 1453841|~*~|M|~*~|OR:com.lcs.wc.product.ProductDestination:1475739|~*~|
		Collection varList = smBOMVarUtil.createVariationCombination(dimensionMap);
		LOGGER.info("In findMergedBOMData method - varList== " + varList);

		Collection bomData = new ArrayList();
		Collection allApplicableLinks = new ArrayList();
		Collection expandedBOM = new ArrayList();
		Iterator variationCombinations = varList.iterator();
		String currVarCombination = "";
		String currSKU = "";
		String currSize = "";
		String currDest = "";
		List<String> alVariations;
		while (variationCombinations.hasNext()) {
			currVarCombination = "";
			currVarCombination = (String) variationCombinations.next();
			alVariations = (List) MOAHelper.getMOACollection(currVarCombination);
			currSKU = "";
			currSize = "";
			currDest = "";
			currSKU = (String) alVariations.get(0);
			currSize = (String) alVariations.get(1);
			currDest = (String) alVariations.get(2);
			// Replace "-" with empty string when there is no variation selected, to pass
			// empty string in the query
			if (("-").equals(currSKU)) {
				currSKU = "";
			}
			if (("-").equals(currSize)) {
				currSize = "";
			}
			if (("-").equals(currDest)) {
				currDest = "";
			}

			// Query to get the collection for each variation combination
			allApplicableLinks = LCSFlexBOMQuery
					.findFlexBOMData(bomPart, "", currSKU, currSize, "", currDest, "WIP_ONLY", null, false, false,
							"ALL_APPLICABLE_TO_DIMENSION", "SINGLE", null, null, materialFlexType, usedAttKeys)
					.getResults();

			// Call this to merge the Dimensions and get the correct data for variation
			// based on the order of priority
			allApplicableLinks = LCSFlexBOMQuery.mergeDimensionBOM(allApplicableLinks, null, materialFlexType);

			// This is required to add free text materials to the collection
			try {
				expandedBOM = LCSFindFlexBOMHelper.joinInLinkedBOMs(allApplicableLinks, usedAttKeys,
						bomPart.getBomType());
			} catch (Exception e) {
				LOGGER.error("In init method == " + e.getMessage());
				e.printStackTrace();
			}

			// Filter based on section
			allApplicableLinks = TableDataUtil.filterBasedOnValue(expandedBOM, sectioncolum, (String) currentSection);

			LOGGER.debug("In init method - allApplicableLinks== " + allApplicableLinks);

			if (allApplicableLinks != null) {
				// Call addVariationColumns method to add variation details for each flexobject
				bomData.addAll(addVariationColumns(allApplicableLinks, currSKU, currSize, currDest, bomPart));
			}
		}
		return bomData;
	}

	/**
	 * Method to include the attributes for query
	 * 
	 * @return
	 */
	private Collection getUsedAttKeysForQuery() {
		Collection usedAttKeys = new HashSet();
		if (this.view != null) {
			usedAttKeys = getUsedAttList(this.view.getAttributes());
		}
		if (this.view == null) {
			usedAttKeys.add("section");
			usedAttKeys.add(materialDescription);
			usedAttKeys.add(colorDescription);
			usedAttKeys.add("highLight");
		}
		usedAttKeys.add(materialDescription);
		usedAttKeys.add(colorDescription);

		// ================================================================
		// IMPORTANT- THESE ATTRIBUTES MUST BE INCLUDED IN THE QUERY RESULT
		// OR THE TOTAL CALCULATED ON THE PAGE WILL BE INCONSISTENT
		// ================================================================
		usedAttKeys.add(partName);
		usedAttKeys.add("materialPrice");
		/// VRD EXTENSION START: Configuration for pricing calculations
		usedAttKeys.add(COLOR_SPECIFIC_PRICE);
		usedAttKeys.add(MATERIAL_QUANTITY);
		usedAttKeys.add(LOSS_ADJUSTMENT);
		// SM EXTENSION START
		usedAttKeys.add(CIF_PERCENT);
		usedAttKeys.add(CIF_PRICE);
		// SM EXTENSION STOP
		usedAttKeys.add(PRICE_OVERRIDE);
		/// VRD EXTENSION END

		return usedAttKeys;
	}

	/**
	 * Method to merge the flexobject as per logic
	 * 
	 * @param dataSet
	 * @param variation
	 * @return
	 * @throws WTException
	 */
	private Collection mergeFlexObjectsPerSection(Collection<TableData> dataSet, String variation) throws WTException {
		LOGGER.debug("In mergeFlexObjectsPerSection method - Start ");		
		// Call this method to identify the keys that will be used for comparing
		// flexobjects
		Set keys = findKeysToCompare(variation);

		Collection<TableData> bomDataMerged;
		// row iteration
		FlexObject currRow;
		FlexObject compareRow;
		SMBOMVariationsUtil smBOMVarUtil = new SMBOMVariationsUtil();

		for (int i = 0; i < dataSet.size(); i++) {
			currRow = (FlexObject) ((List) dataSet).get(i);
			// Iterate recursive to merge the data.
			for (int k = i + 1; k < dataSet.size(); k++) {
				compareRow = (FlexObject) ((List) dataSet).get(k);

				// Is match for all flexBOM link attributes for that view
				if (FlexObjectUtil.isMatch(currRow, compareRow, keys)) {
					k = compareAndMergeVariations(dataSet, variation, currRow, compareRow, smBOMVarUtil, k);
				}
			}
		}

		bomDataMerged = dataSet;
		LOGGER.debug("In mergeFlexObjectsPerSection method - bomDataMerged= " + bomDataMerged);
		LOGGER.debug("In mergeFlexObjectsPerSection method - End ");
		return bomDataMerged;
	}

	private int compareAndMergeVariations(Collection<TableData> dataSet, String variation, FlexObject currRow,
			FlexObject compareRow, SMBOMVariationsUtil smBOMVarUtil, int k) throws WTException {
		int i;
		i = k;
		String sizesString;
		String destinationstring;
		String colorstring;
		String colorwaysString;
		// Merge the size, Destination, Colorways columns if data match
		if (SIZE1.equalsIgnoreCase(variation)) {
			// Merging Sizes - check for null and check if size is already not there in the
			// collection then only add
			sizesString = smBOMVarUtil.addMergString(currRow.getString(FLEXBOMLINK_SIZE1),
					compareRow.getString(FLEXBOMLINK_SIZE1));
			 LOGGER.debug("\n sizesString==" + sizesString);
			currRow.put(FLEXBOMLINK_SIZE1, sizesString);
		}

		else if (DESTINATION.equalsIgnoreCase(variation)) {
			// Merging Destinations
			destinationstring = smBOMVarUtil.addMergString(currRow.getString(DESTINATION),
					compareRow.getString(DESTINATION));
			LOGGER.debug("\n destinationstring==" + destinationstring);
			currRow.put(DESTINATION, destinationstring);
		}

		else if (SKU.equalsIgnoreCase(variation)) {
			// Merging colorway-color column
			if (compareRow.containsKey("CURRENT_SKU")) {
				colorwaysString = smBOMVarUtil.addMergString(currRow.getString(COLORWAY_COLUMN),
						compareRow.getString(COLORWAY_COLUMN));
				currRow.put(COLORWAY_COLUMN, colorwaysString);

				String skuId1 = (String) compareRow.get("CURRENT_SKU");
				MaterialColorInfo rowColorMci = MCIDimensionHelper.getMaterialColorInfo(compareRow);
				// Merge the color data
				smBOMVarUtil.addColorData(currRow, skuId1, rowColorMci);
			} else { // Else block will be executed when colorways is not selected by user
				colorstring = smBOMVarUtil.addMergString(currRow.getString(LCSCOLOR_COLORNAME),
						compareRow.getString(LCSCOLOR_COLORNAME));
				LOGGER.debug("\n colorstring IN ELSE==" + colorstring);
				currRow.put(LCSCOLOR_COLORNAME, colorstring);
			}
		}

		dataSet.remove(compareRow);
		i--;
		return i;
	}

	/**
	 * Method to find the keys that will be used for comparing flexobjects
	 * 
	 * @param variation
	 * @return
	 * @throws WTException
	 */
	private Set findKeysToCompare(String variation) throws WTException {
		Set keys = new HashSet();
		Collection<String> viewAtts = new ArrayList<String>();

		if (this.view != null) {
			// Get only view BOM level attribute keys to compare.
			viewAtts = getViewAttributes(this.view);
		}
		String attcol = "";
		FlexType bomType1 = null;
		String attKey = "";
		FlexType matColType = null;
		String matColAttcol = "";
		String matColAttKey = "";
		
		
		for (String att : viewAtts) {
			// Include BOM Level attributes that are selected in view for comparison
			if (att.startsWith("BOM.")) {
				attKey = att.substring(att.indexOf('.') + 1);
				bomType1 = bomPart.getFlexType();
				attcol = bomType1.getAttribute(attKey).getColumnName();
				keys.add(FLEXBOMLINK + attcol);

			}
			// Include Material Color Level attributes for comparison if its selected in
			// view
			if (att.startsWith("Material Color.")) {
				matColAttKey = att.substring(att.indexOf('.') + 1);
				matColType = FlexTypeCache.getFlexTypeFromPath("Material Color");
				matColAttcol = matColType.getAttribute(matColAttKey).getColumnName();
				keys.add("LCSMATERIALCOLOR." + matColAttcol);
			}

			// Add Material name, supplier name, price for comparison
			if (materialDescription.equalsIgnoreCase(att)) {
				keys.add(FLEXBOMLINK + materialDescDbColName);
			}
			if ("supplierName".equalsIgnoreCase(att)) {
				keys.add("LCSSUPPLIERMASTER.SUPPLIERNAME");
			}
			if ("Material.materialPrice".equalsIgnoreCase(att)) {
				keys.add(priceKey.toUpperCase());
			}

			if (FormatHelper.hasContent(variation)) {
				// If variation is size, include destination and colorways column for comparison
				keys.addAll(includeDestColorwaysToCompareKeysForSizeMerge(variation, att));

				// If variation is destination, include size and colorways column for comparison
				keys.addAll(includeSizeColorwaysToCompareKeysForDestMerge(variation, att));

				// If variation is SKU, include destination and size for comparison
				keys.addAll(includeSizeDestToCompareKeysForColorwaysMerge(variation, att));

			}

		}
		LOGGER.debug("In findKeysToCompare method - keys= " + keys);
		return keys;
	}

	/**
	 * Method to include size and dest keys to compare
	 * 
	 * @param variation
	 * @param att
	 * @return
	 */
	private Collection includeSizeDestToCompareKeysForColorwaysMerge(String variation, String att) {
		Set keys = new HashSet();
		if (variation.equalsIgnoreCase(SKU)) {
			if (FLEXBOMLINK_SIZE1.equalsIgnoreCase(att)) {
				keys.add(FLEXBOMLINK_SIZE1);
			}
			if (DESTINATION.equalsIgnoreCase(att)) {
				keys.add(DESTINATION);
			}
		}
		return keys;
	}

	/**
	 * 
	 * Method to include size and colorways keys to compare
	 * 
	 * @param variation
	 * @param att
	 * @return
	 */
	private Collection includeSizeColorwaysToCompareKeysForDestMerge(String variation, String att) {
		Set keys = new HashSet();
		if (variation.equalsIgnoreCase(DESTINATION)) {
			if (FLEXBOMLINK_SIZE1.equalsIgnoreCase(att)) {
				keys.add(FLEXBOMLINK_SIZE1);
			}
			if (Colorways.equals(att)) {
				if (this.getColorways() != null && this.getColorways().size() > 0) {
					// Add colorway column also in the comparison
					keys.add(COLORWAY_COLUMN);
					for (String cwId : this.getColorways()) {
						//keys.add(cwId + "." + DISPLAY_VAL);
						keys.add(cwId + "." + COLOR_NAME); 
					}
				} else {
					//keys.add(DISPLAY_VAL);
					keys.add(COLOR_NAME);
				}
			}
		}
		return keys;
	}

	/**
	 * Method to include colorways and dest keys to compare
	 * 
	 * @param variation
	 * @param att
	 * @return
	 */
	private Collection includeDestColorwaysToCompareKeysForSizeMerge(String variation, String att) {
		Set keys = new HashSet();
		if (variation.equalsIgnoreCase(SIZE1)) {

			if (DESTINATION.equalsIgnoreCase(att)) {
				keys.add(DESTINATION);
			}
			if (Colorways.equals(att)) {

				if (this.getColorways() != null && this.getColorways().size() > 0) {
					// Add colorway column also in the comparison
					keys.add(COLORWAY_COLUMN);
					for (String cwId : this.getColorways()) {
						//keys.add(cwId + "." + DISPLAY_VAL);
						keys.add(cwId + "." + COLOR_NAME); 
					}
				} else {
					//keys.add(DISPLAY_VAL);
					keys.add(COLOR_NAME);
				}
			}
		}
		return keys;
	}

	/**
	 * Initialize the dimension map used to initialize the MCIDimensionHelper Class
	 * 
	 * @param bomPart
	 * @throws WTException
	 */
	private void initDimensionMap(FlexBOMPart bomPart) throws WTException {
		LOGGER.debug("In initDimensionMap method - Start ");
		dimensionMap = new HashMap<String, Collection<String>>();
		dimensionMap.put(MCIDimensionHelper.SKU, this.getColorways());
		dimensionMap.put(MCIDimensionHelper.SIZE1, this.getSizes1());
		// dimensionMap.put (MCIDimensionHelper.SIZE2, this.getSizes2());

		// If no sources have been selected on the User Interface, get all the sources
		// for the product to display in the report
		if (this.getSources().size() == 0) {
			LCSPartMaster productMaster = (LCSPartMaster) bomPart.getOwnerMaster();
			Collection<LCSSourcingConfig> allSources = LCSSourcingConfigQuery
					.getSourcingConfigForProduct(productMaster);
			Collection<String> allSourcesId = new ArrayList<String>();

			for (LCSSourcingConfig source : allSources) {
				String sourceId = FormatHelper
						.getNumericObjectIdFromObject((LCSSourcingConfigMaster) source.getMaster());
				allSourcesId.add(sourceId);
			}
			dimensionMap.put(MCIDimensionHelper.SOURCE, allSourcesId);
		} else {
			dimensionMap.put(MCIDimensionHelper.SOURCE, this.getSources());
		}

		// We need to check if the format of the destination Id is correct before
		// initializing the MCIdimenstionHelper.
		// This is needed because when we run the report from the linesheet, it seems
		// like the destination Ids are passed in differently
		Collection<String> destinationList = new ArrayList<String>();

		for (String destinationId : this.getDestinations()) {
			if (!destinationId.startsWith("OR:")) {
				destinationId = "OR:com.lcs.wc.product.ProductDestination:" + destinationId;
			}

			destinationList.add(destinationId);
		}
		dimensionMap.put(MCIDimensionHelper.DESTINATION, destinationList);
		LOGGER.info("-adding dimensionMap :== " + dimensionMap);
		LOGGER.debug("In initDimensionMap method - End ");
	}

	/**
	 * Method to add variation columns and calculate price for each row If material
	 * is empty, then dont add that link (assuming its a blank row)
	 * 
	 * @param bomData
	 * @param skuId
	 * @param size
	 * @param dest
	 * @param bomPart
	 * @return
	 * @throws WTException
	 */
	private Collection addVariationColumns(Collection bomData, String skuId, String size, String dest,
			FlexBOMPart bomPart) throws WTException {
		LOGGER.debug("In addVariationColumns method - Start ");
		SMBOMVariationsUtil smBOMVarUtil = new SMBOMVariationsUtil();
		Collection bomDataNew = new ArrayList();
		Iterator it = bomData.iterator();
		FlexObject fo;

		Map<String, LCSSKU> cwIdSKUMap = new HashMap<String, LCSSKU>();
		cwIdSKUMap.putAll((Map<String, LCSSKU>) getCwIdSKUMap());

		while (it.hasNext()) {
			fo = (FlexObject) it.next();
			// If material is empty, then don't add that link (assuming its a blank row)
			if (FormatHelper.hasContent((String) fo.get(FLEXBOMLINK + materialDescDbColName))) {

				fo.put(FLEXBOMLINK_SIZE1, size);

				String destinationName = "";
				if (FormatHelper.hasContent(dest)) {
					ProductDestination destination = (ProductDestination) LCSQuery.findObjectById(dest);
					destinationName = (String) destination.getDestinationName();
				}
				fo.put(DESTINATION, destinationName);

				if (FormatHelper.hasContent(skuId)) {
					frameFlexObject(skuId, fo, cwIdSKUMap);
				}

				// Method to calculate the price
				smBOMVarUtil.calculatePrice(fo, reqDate, priceKey, overrideKey, quantityKey, lossAdjustmentKey,
						rowTotalKey, cifPercent, cifPrice, bomPart);

				bomDataNew.add(fo);
			}
		}
		LOGGER.debug("In addVariationColumns method - End ");
		return bomDataNew;

	}

	private void frameFlexObject(String skuId, FlexObject fo, Map<String, LCSSKU> cwIdSKUMap) throws WTException {
		fo.put("CURRENT_SKU", skuId);
		fo.put(COLORWAY_COLUMN, (String) cwIdSKUMap.get(skuId).getValue("skuName"));

		fo.put(skuId + "." + COLOR_NAME, fo.get(LCSCOLOR_COLORNAME));
		fo.put(skuId + "." + COLOR_DESC,
				fo.get(FLEXBOMLINK + bomType.getAttribute(colorDescription).getColumnName()));
		fo.put(skuId + "." + COLOR_ID, fo.get(FLEXBOMLINK_IDA3D5));
		fo.put(skuId + "." + MATERIALCOLOR_ID, fo.get("FLEXBOMLINK.IDA3G5"));
		if (fo.get(FLEXBOMLINK_IDA3D5) != null
				&& FormatHelper.hasContent((String) fo.get(FLEXBOMLINK_IDA3D5))) {
			LCSColor color = (LCSColor) LCSQuery
					.findObjectById("OR:com.lcs.wc.color.LCSColor:" + fo.get(FLEXBOMLINK_IDA3D5));

			if (color.getColorHexidecimalValue() != null) {
				fo.put(skuId + "." + COLOR_HEX, color.getColorHexidecimalValue());
			}

			if (color.getThumbnail() != null) {
				fo.put(skuId + "." + COLOR_THUMB, color.getThumbnail());
			}
		}
	}

	/**
	 * 
	 * Add colorway level information per colorway
	 * 
	 * @return
	 * @throws WTException
	 */
	private Object getCwIdSKUMap() throws WTException {
		ReferenceFactory rf = new ReferenceFactory();
		Map<String, LCSSKU> cwIdSKUMap = new HashMap<String, LCSSKU>();
		if (!this.getColorways().isEmpty()) {
			Collection<LCSSKU> skus = LCSSKUQuery.getSKURevA(this.getColorways());
			for (LCSSKU sku : skus) {
				String refString = rf.getReferenceString(sku.getMasterReference());
				String idString = refString.substring(refString.lastIndexOf(':') + 1);
				cwIdSKUMap.put(idString, sku);
			}
		}
		return cwIdSKUMap;
	}

	/**
	 * Method to get the att list to include in query
	 * 
	 * @param clist
	 * @return
	 */
	public static Collection getUsedAttList(Collection clist) {
		Iterator i = clist.iterator();
		Set attList = new HashSet();
		String key = "";

		while (i.hasNext()) {
			key = (String) i.next();
			if (key.indexOf('.') > -1) {
				key = key.substring(key.indexOf('.') + 1);
			}

			attList.add(key);
		}

		attList.add("section");
		attList.add(materialDescription);
		attList.add(colorDescription);
		attList.add("highLight");

		return attList;
	}

	/**
	 * Initializations
	 * 
	 * @param params
	 * @throws WTException
	 */
	private void initReportableFields(Map<String, Object> params) throws WTException {
		// Initializations
		if (params.get(MATERIAL_TYPE_PATH) != null) {
			materialType = FlexTypeCache.getFlexTypeFromPath((String) params.get(MATERIAL_TYPE_PATH));
		} else {
			materialType = FlexTypeCache.getFlexTypeRoot("Material");
		}
		supplierType = FlexTypeCache.getFlexTypeRoot("Supplier");
		bomType = FlexTypeCache.getFlexTypeRoot("BOM");

		materialNameDbColName = materialType.getAttribute("name").getColumnName();
		componentNameDbColName = bomType.getAttribute(partName).getColumnName();
		colorNameDbColName = bomType.getAttribute(colorDescription).getColumnName();
		priceDbColName = materialType.getAttribute("materialPrice").getColumnName();
		materialDescDbColName = bomType.getAttribute(materialDescription).getColumnName();
		componentNameDisplay = bomType.getAttribute(partName).getAttDisplay();

		priceOverrideDbColName = bomType.getAttribute("priceOverride").getColumnName();
		quantityDbColName = bomType.getAttribute("quantity").getColumnName();
		lossAdjustmentDbColName = bomType.getAttribute("lossAdjustment").getColumnName();
		rowTotalDbColName = bomType.getAttribute("rowTotal").getColumnName();
		highlightAtt = bomType.getAttribute("highLight").getColumnName();

		if (WCPART_ENABLED) {
			wcPartNameDisplay = bomType.getAttribute("wcPartName").getAttDisplay();
		}
		priceKey = "LCSMATERIALSUPPLIER." + priceDbColName;
		overrideKey = FLEXBOMLINK + priceOverrideDbColName;
		quantityKey = FLEXBOMLINK + quantityDbColName;
		lossAdjustmentKey = FLEXBOMLINK + lossAdjustmentDbColName;
		rowTotalKey = FLEXBOMLINK + rowTotalDbColName;

		// Added thes attributes to handle boolean value in the report.
		FlexType bomLinkType = FlexTypeCache.getFlexTypeFromPath("BOM\\Materials\\Product");
		cifPercent = bomLinkType.getAttribute("smCIFPercent").getColumnName();
		cifPrice = bomLinkType.getAttribute("smCIFPrice").getColumnName();
		sectioncolum = FLEXBOMLINK + bomLinkType.getAttribute("section").getColumnName();
	}

	/**
	 * Method to get the view attributes
	 * 
	 * @param view
	 * @return
	 */
	public Collection<String> getViewAttributes(ColumnList view) {
		ArrayList<String> viewAtts = new ArrayList<String>();

		viewAtts.addAll(addViewAttributes(view));
		

		if (WCPART_ENABLED && view == null) {
			viewAtts.add(0, "BOM.wcPartName");
		}
		/// VRD EXTENSION START: Removing automatic adding of columns
		// viewAtts.add(0, "supplierName");
		// viewAtts.add(0, materialDescription);
		/// VRD EXTENSION END
		if (this.getDestinations() != null && this.getDestinations().size() > 0) {
			viewAtts.add(0, DESTINATION);
			viewAtts.add(1, partName);
		}
		else {
			viewAtts.add(0, partName);
		}

		// Always dispaly Source Column. If it is null, display all sources
		/// VRD EXTENSION START: Removing automatic adding of columns
		// viewAtts.add(0, SOURCES);

		if (viewAtts.contains("size1Dim")) {
			viewAtts.add(viewAtts.indexOf("size1Dim"), FLEXBOMLINK_SIZE1);
			viewAtts.remove("size1Dim");
		}
		/*
		 * if (viewAtts.contains("size2Dim")) {
		 * viewAtts.add(viewAtts.indexOf("size2Dim"), SIZE2);
		 * viewAtts.remove("size2Dim"); }
		 */

		// if(this.getSizes2() != null && this.getSizes2().size() > 0){
		// viewAtts.add(0, SIZE2);
		// }
		// if(this.getSizes1() != null && this.getSizes1().size() > 0){
		// viewAtts.add(0, SIZE1);
		// }

		// if(this.getColorways() != null && this.getColorways().size() > 0){
		// viewAtts.add(0, COLORS);
		
		//viewAtts.add(0, partName);
		
		if (view == null) {
			String[] defaultViewAtts = DEFAULT_COLUMNS.split(",");
			/*
			 * for (int i = 0; i < defaultViewAtts.length; i++) {
			 * viewAtts.add(defaultViewAtts[i]); }
			 */
			viewAtts.addAll(Arrays.asList(defaultViewAtts));
			if (this.getSizes1() != null && this.getSizes1().size() > 0) {
				viewAtts.add(SIZE1);
			}
			/*
			 * if(this.getSizes2() != null && this.getSizes2().size() > 0){
			 * viewAtts.add(SIZE2); }
			 */
		}
		if (this.useMatThumbnail) {
			viewAtts.add(0, "MATERIAL.thumbnail");
		}

		/// VRD EXTENSION END
		return viewAtts;
	}

	private Collection<String> addViewAttributes(ColumnList view) {
		ArrayList<String> viewAtts = new ArrayList<String>();
		if (view != null) {
			// debug("view attributes: " + view.getAttributes());
			if (view.getAttributes() != null) {
				viewAtts.addAll(view.getAttributes());
			}
		}
		return viewAtts;
	}

	/**
	 * Method to get the view columns to be printed in TP
	 * 
	 * @param view
	 * @return
	 * @throws WTException
	 */
	public Map<String, TableColumn> getViewColumns(ColumnList view) throws WTException {
		Map<String, TableColumn> viewColumns = new HashMap<String, TableColumn>();
		if (view != null) {
			viewColumns.putAll(getViewColumns());
			// debug("viewColumn keys: " + viewColumns.keySet());
		}
		/// VRD EXTENSION START: Removing automatic adding of columns
		if (view == null) {
			viewColumns.putAll(getColumnsWhenViewNotSelected());
		}
		/// VRD EXTENSION END

		TableColumn column = new TableColumn();

		if(!viewColumns.containsKey("BOM.partName")) {
			column = new BOMPartNameTableColumn();
			column.setHeaderLabel(componentNameDisplay);
			column.setTableIndex(FLEXBOMLINK + componentNameDbColName);
			column.setDisplayed(true);
			((BOMPartNameTableColumn) column).setSubComponetIndex("FLEXBOMLINK.MASTERBRANCHID");
			((BOMPartNameTableColumn) column).setComplexMaterialIndex("FLEXBOMLINK.MASTERBRANCH");
			((BOMPartNameTableColumn) column).setLinkedBOMIndex("FLEXBOMLINK.LINKEDBOM");
			column.setSpecialClassIndex("CLASS_OVERRIDE");
			column.setPdfColumnWidthRatio(partNameWidth);
			viewColumns.put(partName, column);
		}

		if (WCPART_ENABLED) {
			column = new TableColumn();
			column.setHeaderLabel(wcPartNameDisplay);
			column.setTableIndex(WCPARTNAME);
			column.setDisplayed(true);
			column.setFormat(FormatHelper.MOA_FORMAT);
			viewColumns.put("BOM.wcPartName", column);
		}

		column = new TableColumn();
		column.setHeaderLabel(WTMessage.getLocalizedMessage(RB.SOURCING, "sourceColumn_LBL", RB.objA));
		column.setTableIndex(SOURCES);
		column.setDisplayed(true);
		column.setFormat(FormatHelper.MOA_FORMAT);
		viewColumns.put(SOURCES, column);

		column = new TableColumn();
		column.setHeaderLabel(colorwaysLabel);
		column.setTableIndex(COLORS);
		column.setDisplayed(true);
		column.setFormat(FormatHelper.MOA_FORMAT);
		viewColumns.put(COLORS, column);

		column = new TableColumn();
		column.setHeaderLabel(size1Label);
		column.setTableIndex(FLEXBOMLINK_SIZE1);
		column.setDisplayed(true);
		column.setFormat(FormatHelper.MOA_FORMAT);
		viewColumns.put(FLEXBOMLINK_SIZE1, column);

		column = new TableColumn();
		column.setHeaderLabel(WTMessage.getLocalizedMessage(RB.FLEXBOM, "destination_noColon_LBL", RB.objA));
		column.setTableIndex(DESTINATION);
		column.setDisplayed(true);
		column.setFormat(FormatHelper.MOA_FORMAT);
		viewColumns.put(DESTINATION, column);

		column = new BOMMaterialTableColumn();
		column.setHeaderLabel(this.materialLabel);
		column.setTableIndex("LCSMATERIAL." + materialNameDbColName);
		column.setDisplayed(true);
		column.setPdfColumnWidthRatio(materialNameWidth);
		column.setLinkMethod("viewMaterial");
		column.setLinkTableIndex("childId");
		column.setLinkMethodPrefix("OR:com.lcs.wc.material.LCSMaterialMaster:");
		((BOMMaterialTableColumn) column).setDescriptionIndex(FLEXBOMLINK + materialDescDbColName);
		viewColumns.put(materialDescription, column);

		column = new TableColumn();
		column.setHeaderLabel(this.supplierLabel);
		column.setTableIndex("LCSSUPPLIERMASTER.SUPPLIERNAME");
		column.setDisplayed(true);
		column.setPdfColumnWidthRatio(supplierNameWidth);
		column.setFormat(FormatHelper.STRING_FORMAT);
		viewColumns.put("supplierName", column);

		column = new TableColumn();
		column.setDisplayed(true);
		column.setHeaderLabel("");
		column.setHeaderAlign("left");
		column.setLinkMethod("launchImageViewer");
		column.setLinkTableIndex("LCSMATERIAL.PRIMARYIMAGEURL");
		column.setTableIndex("LCSMATERIAL.PRIMARYIMAGEURL");
		column.setColumnWidth("1%");
		column.setLinkMethodPrefix("");
		column.setImage(true);
		column.setShowFullImage(this.useMatThumbnail);

		if (imageWidth > 0) {
			column.setImageWidth(imageWidth);
		}
		if (imageHeight > 0) {
			column.setImageHeight(imageHeight);
		}
		viewColumns.put("MATERIAL.thumbnail", column);

		viewColumns.putAll(getColorwaysColumns());

		return viewColumns;
	}

	private Map<String, TableColumn> getColorwaysColumns() throws WTException {
		TableColumn column;// = new TableColumn();
		Map<String, TableColumn> viewColumns = new HashMap<String, TableColumn>();
		// debug("Getting columns...colorways: " + getColorways());
		if (this.getColorways() != null && this.getColorways().size() > 0) {
			ReferenceFactory rf = new ReferenceFactory();
			Map<String, LCSSKU> cwIdSKUMap = new HashMap<String, LCSSKU>();
			if (!this.getColorways().isEmpty()) {
				Collection<LCSSKU> skus = LCSSKUQuery.getSKURevA(this.getColorways());
				for (LCSSKU sku : skus) {
					String refString = rf.getReferenceString(sku.getMasterReference());
					String idString = refString.substring(refString.lastIndexOf(':') + 1);
					cwIdSKUMap.put(idString, sku);
				}
			}

			column = new TableColumn();
			column.setDisplayed(true);
			column.setTableIndex(COLORWAY_COLUMN);
			// column.setDescriptionIndex(FLEXBOMLINK + colorNameDbColName);
			column.setHeaderLabel(Colorways);
			column.setLinkMethod("viewColorways");
			column.setLinkTableIndex(COLORWAY_COLUMN);
			column.setFormat(FormatHelper.MOA_FORMAT);
			// column.setLinkMethodPrefix("OR:com.lcs.wc.color.LCSColor:");
			column.setColumnWidth("1%");
			column.setWrapping(false);
			// column.setBgColorIndex("LCSCOLOR.COLORHEXIDECIMALVALUE");
			// column.setUseColorCell(true);
			column.setAlign("center");
			// column.setImageIndex("LCSCOLOR.THUMBNAIL");
			// column.setUseColorCell(this.useColorSwatch);
			column.setFormatHTML(false);
			viewColumns.put(COLORWAY_COLUMN, column);

			for (String cwId : this.getColorways()) {
				BOMColorTableColumn colorColumn = new BOMColorTableColumn();
				colorColumn.setDisplayed(true);
				colorColumn.setTableIndex(cwId + "." + COLOR_NAME);
				colorColumn.setDescriptionIndex(cwId + "." + COLOR_DESC);
				colorColumn.setHeaderLabel((String) cwIdSKUMap.get(cwId).getValue("skuName"));
				colorColumn.setLinkMethod("viewColor");
				colorColumn.setLinkTableIndex(cwId + "." + COLOR_ID);
				colorColumn.setLinkMethodPrefix("OR:com.lcs.wc.color.LCSColor:");
				colorColumn.setColumnWidth("1%");
				colorColumn.setWrapping(false);
				colorColumn.setBgColorIndex(cwId + "." + COLOR_HEX);
				colorColumn.setUseColorCell(true);
				colorColumn.setAlign("center");
				colorColumn.setImageIndex(cwId + "." + COLOR_THUMB);
				colorColumn.setSpecialClassIndex(cwId + "_CLASS_OVERRIDE");
				colorColumn.setPdfColumnWidthRatio(colorwayWidth);
				colorColumn.setUseColorCell(this.useColorSwatch);
				colorColumn.setFormatHTML(false);
				viewColumns.put(cwId + "." + DISPLAY_VAL, colorColumn);
			}
		} else {
			BOMColorTableColumn colorColumn = new BOMColorTableColumn();
			colorColumn.setDisplayed(true);
			colorColumn.setTableIndex(LCSCOLOR_COLORNAME);
			colorColumn.setDescriptionIndex(FLEXBOMLINK + colorNameDbColName);
			colorColumn.setHeaderLabel(colorLabel);
			colorColumn.setLinkMethod("viewColor");
			colorColumn.setLinkTableIndex("LCSCOLOR.IDA2A2");
			colorColumn.setLinkMethodPrefix("OR:com.lcs.wc.color.LCSColor:");
			colorColumn.setColumnWidth("1%");
			colorColumn.setWrapping(false);
			colorColumn.setBgColorIndex("LCSCOLOR.COLORHEXIDECIMALVALUE");
			colorColumn.setUseColorCell(true);
			colorColumn.setAlign("center");
			colorColumn.setImageIndex("LCSCOLOR.THUMBNAIL");
			colorColumn.setUseColorCell(this.useColorSwatch);
			colorColumn.setFormatHTML(false);
			viewColumns.put(DISPLAY_VAL, colorColumn);
		}
		return viewColumns;
	}

	private Map<String, TableColumn> getColumnsWhenViewNotSelected() throws WTException {
		Map<String, TableColumn> viewColumns = new HashMap<String, TableColumn>();
		TableColumn column = null;
		FlexTypeGenerator flexg = new FlexTypeGenerator();
		FlexTypeAttribute att = null;
		String attKey;

		String[] defaultViewAtts = DEFAULT_COLUMNS.split(",");
		for (int i = 0; i < defaultViewAtts.length; i++) {
			if (defaultViewAtts[i].startsWith("Material.")) {

				attKey = defaultViewAtts[i].substring(defaultViewAtts[i].indexOf('.') + 1);
				att = materialType.getAttribute(attKey);
				column = flexg.createTableColumn(null, att, materialType, false,
						att.getSearchResultsTableName().toUpperCase());
				viewColumns.put(defaultViewAtts[i], column);

			} else if (defaultViewAtts[i].startsWith("Supplier.")) {

				attKey = defaultViewAtts[i].substring(defaultViewAtts[i].indexOf('.') + 1);
				att = supplierType.getAttribute(attKey);
				column = flexg.createTableColumn(null, att, supplierType, false, null);
				viewColumns.put(defaultViewAtts[i], column);

			} else if (defaultViewAtts[i].startsWith("BOM.")) {

				attKey = defaultViewAtts[i].substring(defaultViewAtts[i].indexOf('.') + 1);
				att = bomType.getAttribute(attKey);
				column = flexg.createTableColumn(null, att, bomType, false, "FLEXBOMLINK");
				viewColumns.put(defaultViewAtts[i], column);

			}
		}
		return viewColumns;
	}

	/**
	 * Method to get table columns
	 * 
	 * @return
	 */
	public Collection<TableColumn> getTableColumns() throws WTException {
		Collection<String> viewAtts = getViewAttributes(this.view);
		Map<String, TableColumn> viewColumns = getViewColumns(this.view);
		Collection<TableColumn> columns = new ArrayList<TableColumn>();
		for (String att : viewAtts) {
			if (Colorways.equals(att)) {
				columns.addAll(getColorwayTableColumns(viewColumns));
			} else {
				if (viewColumns.get(att) != null) {
					columns.add(viewColumns.get(att));
				}
			}
		}

		Iterator columnGroups = columns.iterator();
		TableColumn singleColumn;
		Collection<TableColumn> columnResults = new ArrayList<TableColumn>(columns.size());
		while (columnGroups.hasNext()) {
			singleColumn = (TableColumn) columnGroups.next();
			if (singleColumn != null && !(singleColumn instanceof BOMColorTableColumn)) {
				singleColumn.setColumnClassIndex(FLEXBOMLINK + highlightAtt);
				columnResults.add(singleColumn);
			} else {
				columnResults.add(singleColumn);
			}
		}
		LOGGER.debug("\nIn getTableColumns method - columnResults==" + columnResults);
		return columnResults;
	}

	private Collection<TableColumn> getColorwayTableColumns(Map<String, TableColumn> viewColumns) {
		Collection<TableColumn> columns = new ArrayList<TableColumn>();
		if (this.getColorways() != null && this.getColorways().size() > 0) {
			// Add colorway column
			columns.add(viewColumns.get(COLORWAY_COLUMN));

			for (String cwId : this.getColorways()) {
				if (viewColumns.get(cwId + "." + DISPLAY_VAL) != null) {
					columns.add(viewColumns.get(cwId + "." + DISPLAY_VAL));
				}
			}
		} else {
			columns.add(viewColumns.get(DISPLAY_VAL));
		}
		return columns;
	}

}
