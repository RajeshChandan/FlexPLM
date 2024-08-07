package com.sportmaster.wc.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.VersionHelper;
/**
 * The Class SMCareLabelReportQuery.
 *
 * @version 'true' 1.0 version number.
 * @author 'true' ITC.
 */
public final class SMCareLabelReportQuery {
	/** The Constant FLEXBOMLINK_IDA2A2. */
	private static final String FLEXBOMLINK_IDA2A2 = "FLEXBOMLINK.IDA2A2";
	/** The Constant SEASON_MDM_ID. */
	private static final String SEASON_MDM_ID = LCSProperties
			.get("season.SEASONMDM");
	/** The Constant SUPPLIER_MDM_ID. */
	private static final String SUPPLIER_MDM_ID = LCSProperties
			.get("materialSupplier.VENDORMDM");
	/** The Constant PROJECT. */
	private static final String PROJECT = LCSProperties
			.get("product.PROJECT");
	/** The Constant WASH_CARE. */
	private static final String WASH_CARE = LCSProperties
			.get("material.WASHCARE");
	/** The Constant COMPOSITION. */
	private static final String COMPOSITION = LCSProperties
			.get("materialFabric.COMPOSITION");
	/** The Constant COMPOSITION_RU. */
	private static final String COMPOSITION_RU = LCSProperties
			.get("materialFabric.COMPOSITIONRU");
	/** The Constant ADDITIONALCARE_MC. */
	private static final String ADDITIONALCARE_MC = LCSProperties
			.get("materialColor.ADDITIONALCAREMC");
	/** The Constant MATERIALMDM. */
	private static final String MATERIALMDM = LCSProperties
			.get("material.MATERIALMDM");
	/** The Constant PRODUCT_MASTERREFERENCE. */
	private static final String PRODUCT_MASTERREFERENCE =
			"LCSPRODUCT.IDA3MASTERREFERENCE";
	/** The Constant OVERRIDENROW_IDA2A2. */
	private static final String OVERRIDENROW_IDA2A2 = 
			"OVERRIDENROW.IDA2A2";
	/** The Constant FLEXTYPE_MATERIAL. */
	private static final String FLEXTYPE_MATERIAL = "Material";
	/** The Constant PRODUCTDESTINATION_DESTINATIONNAME. */
	private static final String PRODUCTDESTINATION_DESTINATIONNAME = 
			"PRODUCTDESTINATION.DESTINATIONNAME";
	/** The Constant LCSCOLOR_COLORNAME. */
	private static final String LCSCOLOR_COLORNAME = 
			"LCSCOLOR.COLORNAME";
	/** The Constant LCSMATERIALCOLOR_IDA2A2. */
	private static final String LCSMATERIALCOLOR_IDA2A2 = 
			"LCSMATERIALCOLOR.IDA2A2";
	/** The Constant LCSMATERIAL_BRANCHIDITERATIONINFO. */
	private static final String LCSMATERIAL_BRANCHIDITERATIONINFO = 
			"LCSMATERIAL.BRANCHIDITERATIONINFO";
	/** The Constant OVERRIDENROW_IDA3D5. */
	private static final String OVERRIDENROW_IDA3D5 = 
			"OVERRIDENROW.IDA3D5";
	/** The Constant materialFabricFlexType. */
	private static final String materialFabricFlexType = 
			"Material\\Fabric";
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger
			.getLogger("CARELABELREPORTLOG");
	/** The Constant OVERRIDENROW. */
	private static final String OVERRIDENROW = "OVERRIDENROW";
	/** The Constant COLOR_DESCRIPTION. */
	private static final String COLOR_DESCRIPTION = "colorDescription";
	/**
	 * Constant Managing Department.
	 */
	private static final String MANAGING_DEPART = LCSProperties.get("com.sportmaster.reports.careLabel.materialSupplierAttribute");
	
	/**
	 * Constant Pre-Retail.
	 */
	private static final String PRE_RETAIL = LCSProperties.get("com.sportmaster.reports.careLabel.smManagingDepartment");
	/** Private Constructor */
	private SMCareLabelReportQuery() {

	}

	/**
	 * @param context - context
	 * @param inputSelectedMap - inputSelectedMap
	 * @return reportBean - reportBean
	 */
	public static SMCareLabelReportBean runReportQuery(ClientContext context,
			Map<String, Object> inputSelectedMap) {
		SMCareLabelReportBean reportBean = new SMCareLabelReportBean();
		SMCareLabelReportModel reportModel = new SMCareLabelReportModel();
		reportBean = reportModel.getReportColumnsData(
				context,inputSelectedMap, reportBean);
		// Get the criteria selected in the JSP
		reportBean = SMCareLabelReportHelper.
				getSelectedCriteria(inputSelectedMap, reportBean);
		String strSelectedSeasons = "";
		Collection<FlexObject> linesheetData;
		LCSSeason season;
		FlexType productType;
		com.lcs.wc.season.LineSheetQuery lsq = 
				new com.lcs.wc.season.LineSheetQuery();
		// Initializations
		Map cMap;
		Collection<FlexObject> lsDataCol;
		Collection<FlexObject> reportData = new ArrayList<FlexObject>();
		Map lsDataByProduct;
		Collection<String> prdMasterOids;
		Iterator lsRowsItr;
		Map.Entry me;
		Collection totalBOMData = null;
		strSelectedSeasons = reportBean.getSelectedSeasonOid();
		linesheetData = new ArrayList<FlexObject>();
		try {
			FlexType seasonFlexType = FlexTypeCache
					.getFlexTypeFromPath("Season");
			Collection<FlexObject> overridenRows = null;
			// get the season object
			season = (LCSSeason) LCSQuery
					.findObjectById("VR:com.lcs.wc.season.LCSSeason:"
							+ strSelectedSeasons);
			// If season object is not null
			if (null != season) {
				productType = season.getProductType();
				// Get the criteria selected in UI
				cMap = getCriteriaMap(reportBean, 
						productType, inputSelectedMap);
				lsDataCol = lsq.runSeasonProductReport(null, // LCSProduct
						season, // LCSSeason season
						null, // LCSSourcingConfig config
						true, // boolean skus
						cMap, // Map criteria
						true, // boolean postProcessing
						null, // String materialGroupId
						true, // boolean sourcing
						true, // boolean secondarySourcing
						false, // boolean primaryCostOnly
						false, // boolean whatifCosts
						reportBean.getAttKeyList(), // Collection
						false, // boolean includeRemoved
						false, // boolean includePlaceHolders
						null, // Collection seasonGroupIds
						true, // Include cost spec (only applied if sourcing
						// is true)
						null, // cost Spec which is not filtered out here
						null, // PreparedStatement
						true, // execute this at sku level to enable rollup
						// of color information
						false, // exclude Inactive costsheets
						-1);
				LOGGER.debug("linesheet data for season :"+ 
						season.getName() + " >>>>>>" + lsDataCol.size());
				//LOGGER.debug("linesheet data for season :"+ season.getName() + " >>>>>>" + lsDataCol);
				// group based on PRODUCT_MASTERREFERENCE
				lsDataByProduct = com.lcs.wc.util.FlexObjectUtil
						.groupIntoCollections(lsDataCol,PRODUCT_MASTERREFERENCE);
				prdMasterOids = new ArrayList<String>();
				lsRowsItr = lsDataByProduct.entrySet().iterator();
				// Iterate lsRowsItr and get the product master id and add to prdMasterOids
				while (lsRowsItr.hasNext()) {
					me = (Map.Entry) lsRowsItr.next();
					prdMasterOids.add((String) me.getKey());
				}	
				// If there are no product objects, return reportBean
				if(prdMasterOids.isEmpty()){
					return reportBean;
				}
				Collection<String> seasonOids = new ArrayList<String>();
				//updated for 3.9.0.0 Build, fixed JIRA-1261 
				seasonOids.add(LCSQuery.getNumericFromOid(FormatHelper.getVersionId(season)));
				
				// Main query to get the bomlink data by products and season
				SMCareLabelReportHelper smReportHelper = new SMCareLabelReportHelper();
				// Top Rows will be fetched, if it does not have any library Color associated
				SearchResults bomTopRows = smReportHelper.getMaterialSuppFromBOMLinkData(reportBean,
						seasonOids, prdMasterOids, false, false, false, false, false);
				LOGGER.debug("\n bomTopRows>>>>>"+ bomTopRows.getResultsFound());
				// Overridden rows (sku, dest, sku-dest rows will be
				// fetched, if the overridden rows does not have any library Color associated)
				SearchResults ovrBomData = smReportHelper.getMaterialSuppFromBOMLinkData(reportBean,
						seasonOids, prdMasterOids, false, true,	false, false, false);
				LOGGER.debug("\n ovrBomData>>>>>"+ ovrBomData.getResultsFound());
				// Top rows will be fetched, if the rows has library color associated
				SearchResults bomTopMatColorRows = smReportHelper.getMaterialSuppFromBOMLinkData(reportBean,
						seasonOids, prdMasterOids, true, false,	false, false, false);
				LOGGER.debug("\n bomTopMatColorRows>>>>>"
						+ bomTopMatColorRows.getResultsFound());
				// Overridden material color rows will be fetched, if the
				// overridden row has library color associated (if material,
				// supplier and color are all overridden, will not be fetched here)
				SearchResults ovrBomMatColorData = smReportHelper.getMaterialSuppFromBOMLinkData(reportBean,
						seasonOids, prdMasterOids, true, true, false, false, false);
				LOGGER.debug("\n ovrBomMatColorData>>>>>"+ ovrBomMatColorData.getResultsFound());

				// All Overridden rows will be fetched if base row has a library color associated
				SearchResults matColorOvrdnData = smReportHelper.getMaterialSuppFromBOMLinkData(reportBean,
						seasonOids, prdMasterOids, true, true, true, false, false);
				LOGGER.debug("\n matColorOvrdnData>>>>>"+ matColorOvrdnData.getResultsFound());
				// Overridden material color rows will be fetched, if the
				// overridden row has library color associated (if material,
				// supplier and color are all overridden, will not be fetched here)
				SearchResults matColorOvrdnData2 = smReportHelper.getMaterialSuppFromBOMLinkData(reportBean,
						seasonOids, prdMasterOids, true, true, true, false, true);
				LOGGER.debug("\n matColorOvrdnData2>>>>>"+ matColorOvrdnData2.getResultsFound());
				// Overridden material rows will be fetched (if color is null, but material is overridden, will be fetched here)
				SearchResults ovrMatOvrBomData = smReportHelper.getMaterialSuppFromBOMLinkData(reportBean,
						seasonOids, prdMasterOids, false, true,	false, true, false);
				LOGGER.debug("\n ovrMatOvrBomData>>>>>"+ ovrMatOvrBomData.getResultsFound());
				// Overridden material color rows will be fetched, if the
				// overridden row has library color associated (if material,
				// supplier and color are all overridden, will be fetched here)
				SearchResults ovrMatColOvrBomData = smReportHelper
						.getMaterialSuppFromBOMLinkData(reportBean,
								seasonOids, prdMasterOids, true, true, true, true, false);
				LOGGER.debug("\n ovrMatColOvrBomData>>>>>"+ ovrMatColOvrBomData.getResultsFound());
				// Merging all top rows in totalBOMData collection - (all base rows without color and with color)
				totalBOMData = bomTopRows.getResults();
				totalBOMData.addAll(bomTopMatColorRows.getResults());
				// Merging all Material overrides (including Material with color and Material without color)
				overridenRows = ovrMatOvrBomData.getResults();
				overridenRows.addAll(ovrMatColOvrBomData.getResults());
				// Method to organize FlexObjects
				getReportData(overridenRows, ovrBomData,
						matColorOvrdnData2, matColorOvrdnData,ovrBomMatColorData);

				reportData.addAll(frameDataForReport(seasonFlexType,
						linesheetData, season, lsDataCol, totalBOMData,	overridenRows));
				reportBean.setReportData(reportData);
				LOGGER.debug("\n Total rows in generated report="+ reportData.size());
			}
		} catch (WTException e) {
			LOGGER.error("WTException in SMCareLabelReportQuery runReportQuery"
					+ e.getMessage());
			e.printStackTrace();
		}
		// return reportbean object
		return reportBean;
	}

	/**
	 * @param overridenRows - overridenRows
	 * @param ovrBomData - ovrBomData
	 * @param matColorOvrdnData2 - matColorOvrdnData2
	 * @param matColorOvrdnData - matColorOvrdnData
	 * @param ovrBomMatColorData - ovrBomMatColorData
	 */
	private static void getReportData(Collection<FlexObject> overridenRows,
			SearchResults ovrBomData, SearchResults matColorOvrdnData2,
			SearchResults matColorOvrdnData, 
			SearchResults ovrBomMatColorData) {
		Map ovrBomLinkMap = 
				com.lcs.wc.util.FlexObjectUtil
				.groupIntoCollections(overridenRows, 
						OVERRIDENROW_IDA2A2);
		// Adding all the above results to overridenRows (if not already present
		// in overridenRows)
		if (ovrBomData.getResultsFound() > 0) {
			for (FlexObject fo : (Collection<FlexObject>) ovrBomData
					.getResults()) {
				String bomlinkOid = fo.getData(
						OVERRIDENROW_IDA2A2);
				if (!ovrBomLinkMap.containsKey(bomlinkOid)) {
					overridenRows.add(fo);
				}
			}
		}
		// Adding all the above results to overridenRows (if not already present
		// in overridenRows)
		if (matColorOvrdnData2.getResultsFound() > 0) {
			for (FlexObject fo : (Collection<FlexObject>) matColorOvrdnData2
					.getResults()) {
				String bomlinkOid = fo.getData(
						OVERRIDENROW_IDA2A2);
				if (!ovrBomLinkMap.containsKey(bomlinkOid)) {
					overridenRows.add(fo);
				}
			}
		}
		// Adding all the above results to overridenRows (if not already present
		// in overridenRows)
		if (matColorOvrdnData.getResultsFound() > 0) {
			for (FlexObject fo : (Collection<FlexObject>) matColorOvrdnData
					.getResults()) {
				String bomlinkOid = fo.getData(
						OVERRIDENROW_IDA2A2);
				if (!ovrBomLinkMap.containsKey(bomlinkOid)) {
					overridenRows.add(fo);
				}
			}
		}
		// calling this method to reduce Cyclomatic Complexity
		getReportData(ovrBomMatColorData, ovrBomLinkMap, overridenRows);
	}

	/**
	 * @param ovrBomMatColorData - ovrBomMatColorData
	 * @param ovrBomLinkMap - ovrBomLinkMap
	 * @param overridenRows - overridenRows
	 */
	private static void getReportData(SearchResults ovrBomMatColorData,
			Map ovrBomLinkMap, Collection<FlexObject> overridenRows) {
		if (ovrBomMatColorData.getResultsFound() > 0) {
			for (FlexObject fo : (Collection<FlexObject>) 
					ovrBomMatColorData.getResults()) {
				// get OVERRIDENROW_IDA2A2 from fo
				String bomlinkOid = fo.getData(
						OVERRIDENROW_IDA2A2);
				// add fo to overridenRows 
				if (!ovrBomLinkMap.containsKey(bomlinkOid)) {
					overridenRows.add(fo);
				}
			}
		}
	}

	/**
	 * @param reportBean - reportBean
	 * @param productType - productType
	 * @param inputSelectedMap - inputSelectedMap
	 * @return cMap - cMap
	 */
	private static Map getCriteriaMap(SMCareLabelReportBean reportBean,
			FlexType productType, Map<String, Object> inputSelectedMap) {
		Map<String, Object> cMap = new HashMap<String, Object>();

		String strProductSeasonStatus = LCSProperties
				.get("com.sportmaster.reports.careLabel.smProductSeasonStatus");
		try {
			// Add the criteria for Product Season Status fetched 
			// from property file
			cMap.put(
					productType.getAttribute(
							LCSProperties
							.get("productSeason.PRODUCTSEASONSTATUS"))
					.getSearchCriteriaIndex(), strProductSeasonStatus);
			// Add the criteria for user selected Technologist
			if (reportBean.getSelectedProducctTechnologist() != null) {
				cMap.put(
						productType.getAttribute(
								LCSProperties
								.get("productSeason.TECHNOLOGIST"))
						.getSearchCriteriaIndex(), reportBean
						.getSelectedProducctTechnologist());
			}
			// Add the criteria for user selected Brand
			if (reportBean.getSelectedBrands() != null) {
				cMap.put(
						productType.getAttribute(
								LCSProperties.get("product.BRAND"))
						.getSearchCriteriaIndex(), inputSelectedMap
						.get("brandId"));
			}
			// Add the criteria for user selected Gender
			if (reportBean.getSelectedGenders() != null) {
				cMap.put(
						productType.getAttribute(
								LCSProperties.get("product.GENDER"))
						.getSearchCriteriaIndex(), inputSelectedMap
						.get("genderId"));
			}
			// Add the criteria for user selected Age
			if (reportBean.getSelectedAges() != null) {
				cMap.put(
						productType.getAttribute(
								LCSProperties.get("product.AGE"))
						.getSearchCriteriaIndex(), inputSelectedMap
						.get("ageId"));
			}
			// Add the criteria for user selected Production Group
			if (reportBean.getSelectedProductionGroupOid() != null
					&& !reportBean.getSelectedProductionGroupOid().isEmpty()) {
				cMap.put(
						productType.getAttribute(
								LCSProperties
								.get("productSeason.PRODUCTIONGROUP"))
						.getSearchCriteriaIndex(), inputSelectedMap
						.get("productionGroupId"));
			}
			// Add the criteria for user selected Project
			if (reportBean.getSelectedProject() != null
					&& !reportBean.getSelectedProject().isEmpty()) {
				cMap.put(productType.getAttribute(PROJECT)
						.getSearchCriteriaIndex(), inputSelectedMap
						.get("projectId"));
			}
			// method to get the selected product names
			getSelectedProductName(reportBean, productType, cMap);
		} catch (WTException e) {
			LOGGER.error("WTException in SMCareLabelReportQuery - getCriteriaMap:"
					+ e.getMessage());
			e.printStackTrace();
		}
		LOGGER.debug("cMap=" + cMap);
		return cMap;
	}

	/**
	 * @param reportBean - reportBean
	 * @param productType - productType
	 * @param cMap - cMap
	 * @throws WTException - WTException
	 */
	private static void getSelectedProductName(
			SMCareLabelReportBean reportBean, 
			FlexType productType,
			Map<String, Object> cMap) throws WTException {
		Map productNameMap;
		StringBuilder sb = new StringBuilder();
		Collection cProducts;
		Iterator itProducts;
		String selectedProducts = "";
		String selectedProductIds = "";
		// Add the criteria for user selected Product Name
		if (null != reportBean.getSelectedProductName()
				&& !reportBean.getSelectedProductName().isEmpty()
				&& null != reportBean.getIntProductMap()) {
			// get the ProductMap from reportBean
			productNameMap = reportBean.getIntProductMap();
			cProducts = reportBean.getSelectedProductName();
			// Iterate cProducts
			itProducts = cProducts.iterator();
			while (itProducts.hasNext()) {
				selectedProductIds = (String) itProducts.next();
				// Create a delimiter string with the selected product names and
				// add it to criteria map
				if (FormatHelper.hasContent(selectedProducts)) {
					sb.append(selectedProducts)
					.append(MOAHelper.DELIM)
					.append((String) productNameMap
							.get(selectedProductIds)).append(MOAHelper.DELIM);
				} else {
					selectedProducts = (String) productNameMap
							.get(selectedProductIds);
				}
			}
			if (FormatHelper.hasContent(sb.toString())) {
				selectedProducts = sb.toString();
			}

			if (FormatHelper.hasContent(selectedProducts)) {
				cMap.put(
						productType.getAttribute(
								LCSProperties.get("product.PRODUCTNAME"))
						.getSearchCriteriaIndex(), selectedProducts);
			}
		}
	}

	/**
	 * Method frameDataForReport - frameDataForReport.
	 * 
	 * @param seasonFlexType - seasonFlexType
	 * @param linesheetData - linesheetData
	 * @param season - season
	 * @param lsDataCol - lsDataCol
	 * @param topBomRows - topBomRows
	 * @param overriddnBOMRows - overriddnBOMRows
	 * @return linesheetData - Collection
	 * @throws WTException - WTException
	 */
	private static Collection<FlexObject> frameDataForReport(
			FlexType seasonFlexType, Collection<FlexObject> linesheetData,
			LCSSeason season, Collection<FlexObject> lsDataCol,
			Collection<FlexObject> topBomRows,
			Collection<FlexObject> overriddnBOMRows) throws WTException {
		// If FO is not empty
		if (!lsDataCol.isEmpty()) {
			// Group the top rows based on product id
			Map bomRowMap = com.lcs.wc.util.FlexObjectUtil
					.groupIntoCollections(topBomRows, PRODUCT_MASTERREFERENCE);
			Map ovrdBomRowMap = null;
			if (overriddnBOMRows != null && !overriddnBOMRows.isEmpty()) {
				// Group the overridden rows based on product id
				ovrdBomRowMap = com.lcs.wc.util.FlexObjectUtil
						.groupIntoCollections(overriddnBOMRows,
								PRODUCT_MASTERREFERENCE);
			}
			// Initializations
			Map bomRowsByBOMPart = null;
			Collection<FlexObject> bomRows;
			Collection<FlexObject> ovrdBomRows = null;
			String prdMasterOid;
			String prdSrcMasterOid;
			Map ovrBomLinkMap = null;
			// Iterate through the product result
			for (FlexObject lsData : lsDataCol) {
				prdMasterOid = (String) lsData.getData(PRODUCT_MASTERREFERENCE);
				prdSrcMasterOid = (String) lsData
						.getData("LCSSOURCINGCONFIG.IDA3MASTERREFERENCE");
				if (bomRowMap.get(prdMasterOid) == null) {
					continue;
				}
				// Get the Bom data for the product
				bomRows = (Collection<FlexObject>) bomRowMap.get(prdMasterOid);
				// Group the BOMs (top rows) based on BOMPart
				bomRowsByBOMPart = com.lcs.wc.util.FlexObjectUtil
						.groupIntoCollections(bomRows, "FLEXBOMPART.IDA2A2");
				if (null != ovrdBomRowMap && !ovrdBomRowMap.isEmpty()) {
					// Get the overridden rows for the product
					ovrdBomRows = (Collection<FlexObject>) ovrdBomRowMap
							.get(prdMasterOid);
					if (ovrdBomRows == null) {
						ovrdBomRows = new ArrayList();
					}
				}

				if (null != ovrdBomRows) {
					// Group the overridden rows based on bomlink
					ovrBomLinkMap = com.lcs.wc.util.FlexObjectUtil
							.groupIntoCollections(ovrdBomRows,
									FLEXBOMLINK_IDA2A2);
				}
				// Method to group FO based on bom part 
				// and to form report data flexobjects
				groupDataOnBOMPart(bomRowsByBOMPart, ovrBomLinkMap,
						prdSrcMasterOid, seasonFlexType, 
						linesheetData, season, lsData);
			}
		}
		return linesheetData;
	}

	/**
	 * @param bomRowsByBOMPart - bomRowsByBOMPart
	 * @param ovrBomLinkMap - ovrBomLinkMap
	 * @param prdSrcMasterOid - prdSrcMasterOid
	 * @param seasonFlexType - seasonFlexType
	 * @param linesheetData - linesheetData
	 * @param season - season
	 * @param lsData - lsData
	 * @throws WTException - WTException
	 */
	private static void groupDataOnBOMPart(Map bomRowsByBOMPart,
			Map ovrBomLinkMap, String prdSrcMasterOid, FlexType seasonFlexType,
			Collection<FlexObject> linesheetData, LCSSeason season,
			FlexObject lsData) throws WTException {
		Iterator bomMSRowsItr;
		// Iterate bomRowsByBOMPart
		bomMSRowsItr = bomRowsByBOMPart
				.entrySet().iterator();
		Map.Entry me;
		Map bomRowsByBranchId;
		Iterator bomRowsItr;
		Map.Entry me1;
		FlexObject mainRow;
		while (bomMSRowsItr.hasNext()) {
			me = (Map.Entry) bomMSRowsItr.next();
			bomRowsByBranchId = com.lcs.wc.util.FlexObjectUtil
					.groupIntoCollections(((ArrayList) me.getValue()),
							"FLEXBOMLINK.BRANCHID");
			bomRowsItr = bomRowsByBranchId.entrySet().iterator();
			while (bomRowsItr.hasNext()) {
				me1 = (Map.Entry) bomRowsItr.next();
				mainRow = (FlexObject) ((ArrayList) me1.getValue()).get(0);
				// Call this method to create flexobject to populate in report
				// data
				(new SMCareLabelReportQuery()).setReportDataRows(
						seasonFlexType, linesheetData, season, ovrBomLinkMap,
						lsData, prdSrcMasterOid, mainRow);
			}
		}
	}

	/**
	 * @param seasonFlexType - seasonFlexType
	 * @param linesheetData - linesheetData
	 * @param season - season
	 * @param ovrBomLinkMap - ovrBomLinkMap
	 * @param lsData - lsData
	 * @param prdSrcMasterOid - prdSrcMasterOid
	 * @param mainRow - mainRow
	 * @throws WTException - WTException
	 */
	private static void setReportDataRows(FlexType seasonFlexType,
			Collection<FlexObject> linesheetData, LCSSeason season,
			Map ovrBomLinkMap, FlexObject lsData, String prdSrcMasterOid,
			FlexObject mainRow) throws WTException {
		String mainRowOid = mainRow.getData(FLEXBOMLINK_IDA2A2);
		String bomSrcMasterid = mainRow
				.getString("LATESTITERFLEXSPECIFICATION.IDA3B12");
		// checking BOM is belongs to Product
		if (!prdSrcMasterOid.equals(bomSrcMasterid)) {
			return;
		}
		// Get BOM ID and Src Config ID
		String bomOwnerOid = mainRow.getData("FLEXBOMPART.IDA3A12");
		String scOid = (String) lsData
				.getData("LCSSOURCINGCONFIG.BRANCHIDITERATIONINFO");
		// checking BOM should belongs product owner.
		if (!FormatHelper.hasContent(bomOwnerOid)) {
			return;
		}
		// get Supplier FlexType
		FlexType supplierFlexType = FlexTypeCache
				.getFlexTypeFromPath("Supplier");
		Collection<FlexObject> ovrRows = null;
		if (null != ovrBomLinkMap && ovrBomLinkMap.containsKey(mainRowOid)) {
			ovrRows = (Collection<FlexObject>) ovrBomLinkMap.get(mainRowOid);
		}

		if (ovrRows == null) {
			ovrRows = new ArrayList();
		}
		String skuMasterid = (String) lsData
				.getData("LCSSKU.IDA3MASTERREFERENCE");

		Map ovrRowsByColor = com.lcs.wc.util.FlexObjectUtil
				.groupIntoCollections(ovrRows, "OVERRIDENROW.idA3E5");
		Collection<FlexObject> ovrFoByColor = (Collection<FlexObject>) ovrRowsByColor
				.get(skuMasterid);

		Map ovrRowsTemp = new HashMap();
		ovrRowsTemp.put("", mainRow);

		// Getting Destination row
		Map destVariationMapTemp = new HashMap();
		Map destRows = com.lcs.wc.util.FlexObjectUtil.groupIntoCollections(
				ovrRows, "OVERRIDENROW.DIMENSIONNAME");
		if (destRows.containsKey(":DESTINATION")) {
			Collection<FlexObject> destOrRows = (Collection<FlexObject>) destRows
					.get(":DESTINATION");
			for (FlexObject fo : destOrRows) {
				FlexObject destFo = new FlexObject();
				destFo.putAll(fo);
				destVariationMapTemp.put(
						destFo.get(PRODUCTDESTINATION_DESTINATIONNAME), destFo);
			}
		}
		// method to get Colorway Variation Rows
		getColorwayVariationRows(ovrFoByColor, mainRow, ovrRowsTemp,
				destVariationMapTemp);
		// method to set Report Data Values
		setReportDataValues(destVariationMapTemp, ovrRowsTemp, mainRow, lsData,
				season, seasonFlexType, scOid, supplierFlexType, linesheetData);
	}

	/**
	 * @param ovrFoByColor - ovrFoByColor
	 * @param mainRow - mainRow
	 * @param ovrRowsTemp - ovrRowsTemp
	 * @param destVariationMapTemp - destVariationMapTemp
	 */
	private static void getColorwayVariationRows(
			Collection<FlexObject> ovrFoByColor, FlexObject mainRow,
			Map ovrRowsTemp, Map destVariationMapTemp) {
		// colorway variation, if not main row
		if (ovrFoByColor != null && ovrFoByColor.size() > 0) {
			Map colorRows = com.lcs.wc.util.FlexObjectUtil
					.groupIntoCollections(ovrFoByColor,
							"OVERRIDENROW.DIMENSIONNAME");
			if (colorRows.containsKey(":SKU")) {
				FlexObject fo = (FlexObject) ((ArrayList) colorRows.get(":SKU"))
						.get(0);
				FlexObject colorFo = new FlexObject();
				colorFo.putAll(fo);
				colorFo.put(PRODUCTDESTINATION_DESTINATIONNAME, "");
				setOverridenRowValues(colorFo, null, null, mainRow);
				ovrRowsTemp.put("", colorFo);
			}
			// SKU destination variation, if get destination row, if not get
			// color
			if (colorRows.containsKey(":SKU:DESTINATION")) {
				Collection<FlexObject> destOrRows = (Collection<FlexObject>) colorRows
						.get(":SKU:DESTINATION");
				for (FlexObject fo : destOrRows) {
					FlexObject colrDestFo = new FlexObject();
					colrDestFo.putAll(fo);
					FlexObject colorFo = (FlexObject) ovrRowsTemp.get("");
					FlexObject destFo = (FlexObject) destVariationMapTemp
							.get(colrDestFo
									.get(PRODUCTDESTINATION_DESTINATIONNAME));
					if (destFo == null) {
						destFo = new FlexObject();
					}
					/*LOGGER.debug("clolor name in Color-dest.Before."
							+ colrDestFo.get(OVERRIDENROW_IDA3D5) + "."
							+ colrDestFo.getData(LCSCOLOR_COLORNAME));*/
					setOverridenRowValues(colrDestFo, destFo, colorFo, mainRow);
					/*LOGGER.debug("clolor name in Color-dest.after.."
							+ colrDestFo.getData(LCSCOLOR_COLORNAME));*/
					// add destination to fo
					ovrRowsTemp.put(
							colrDestFo.get(PRODUCTDESTINATION_DESTINATIONNAME),
							colrDestFo);
				}
			}
		}

	}

	/**
	 * @param destVariationMapTemp - destVariationMapTemp
	 * @param ovrRowsTemp - ovrRowsTemp
	 * @param mainRow - mainRow
	 * @param lsData - lsData
	 * @param season - season
	 * @param seasonFlexType - seasonFlexType
	 * @param scOid - scOid
	 * @param supplierFlexType - supplierFlexType
	 * @param linesheetData - linesheetData
	 * @throws WTException - WTException
	 */
	private static void setReportDataValues(Map destVariationMapTemp,
			Map ovrRowsTemp, FlexObject mainRow, FlexObject lsData,
			LCSSeason season, FlexType seasonFlexType, String scOid,
			FlexType supplierFlexType, Collection<FlexObject> linesheetData) throws WTException {
		Map.Entry me;
		FlexObject fo1;
		FlexObject destFo;
		FlexObject colorFo;
		String materialColorId;
		LCSMaterialColor materialColor;
		FlexType bomLinkType = FlexTypeCache
				.getFlexTypeFromPath("BOM\\Materials\\Product");
		// If destination link itertaion, add color or main row
		Iterator bomDestRowsItr = destVariationMapTemp.entrySet().iterator();
		// Iterate bomDestRowsItr
		while (bomDestRowsItr.hasNext()) {
			me = (Map.Entry) bomDestRowsItr.next();
			if (!ovrRowsTemp.containsKey(me.getKey())) {
				fo1 = (FlexObject) me.getValue();
				destFo = new FlexObject();
				destFo.putAll(fo1);
				colorFo = (FlexObject) ovrRowsTemp.get("");
				setOverridenRowValues(destFo, null, colorFo, mainRow);
				ovrRowsTemp.put(me.getKey(), destFo);
			}
		}
		// Iterate ovrRowsTemp
		Iterator bomMSRowsItr = ovrRowsTemp.entrySet().iterator();
		Map.Entry me1;
		FlexObject oFo;
		FlexObject fo;
		LCSMaterialSupplier materialSupplier;
		// get Product\\APD flextype
		FlexType productAPDFlexType = FlexTypeCache
				.getFlexTypeFromPath("Product\\APD");
		LCSProduct lcsProduct;
		// Iterate bomMSRowsItr
		while (bomMSRowsItr.hasNext()) {
			materialColor = null;
			me1 = (Map.Entry) bomMSRowsItr.next();
			oFo = (FlexObject) me1.getValue();
			fo = new FlexObject();
			fo.putAll(mainRow);
			fo.putAll(lsData);
			fo.put("WTTYPEDEFINITION.BRANCHIDITERATIONINFO",
					mainRow.get("WTTYPEDEFINITION.BRANCHIDITERATIONINFO"));
			if (null != oFo) {
				fo.putAll(oFo);
				setOverridenRowValues(fo, oFo);
			}
			// Add product - project attribute to fo
			lcsProduct = (LCSProduct) LCSQuery
					.findObjectById("com.lcs.wc.product.LCSProduct:"
							+ fo.getString("LCSPRODUCT.IDA2A2"));
			if (null != lcsProduct) {
				fo.put(productAPDFlexType.getAttribute(PROJECT)
						.getSearchResultIndex(), productAPDFlexType
						.getAttribute(PROJECT).getDisplayValue(lcsProduct));
			}
			// Add Season attribute to fo
			if (null != season) {
				fo.put(seasonFlexType.getAttribute(
						LCSProperties.get("season.SEASONNAME"))
						.getSearchResultIndex(), season.getName());
				fo.put(seasonFlexType.getAttribute(SEASON_MDM_ID)
						.getSearchResultIndex(),
						seasonFlexType.getAttribute(SEASON_MDM_ID)
						.getDisplayValue(season));
			}
			// Get the sourcing config object
			LCSSourcingConfig lcsSrcConf = (LCSSourcingConfig) LCSQuery
					.findObjectById("VR:com.lcs.wc.sourcing.LCSSourcingConfig:"
							+ scOid);
			if (null != lcsSrcConf) {
				getSourcingConfigAttributes(season, lcsSrcConf, fo,
						supplierFlexType);
			}
			// Add Mat Supp attribute to fo
			materialSupplier = (LCSMaterialSupplier) LCSQuery
					.findObjectById("VR:com.lcs.wc.material.LCSMaterialSupplier:"
							+ fo.getString("LCSMATERIALSUPPLIER.BRANCHIDITERATIONINFO"));
			if (null != materialSupplier) {
				if (FormatHelper.hasContent(
						fo.getString(LCSMATERIALCOLOR_IDA2A2))) {
					materialColorId = "OR:com.lcs.wc.material.LCSMaterialColor:"
							+ fo.getString(LCSMATERIALCOLOR_IDA2A2);
					materialColor = (LCSMaterialColor) LCSQuery
							.findObjectById(materialColorId);
				}
				getMatSuppAndColorAttrs(materialSupplier, supplierFlexType, fo, materialColor);
			}

			// Fix to include only FOs with Primary/ Alt Primary values true - Start, Updated for - 3.8.2.0 build 
			String managingDeptColunName = FlexTypeCache
					.getFlexTypeRootByClass("com.lcs.wc.material.LCSMaterialSupplier").getAttribute(MANAGING_DEPART)
					.getSearchResultIndex();
			if (("1").equals(
					fo.getData(bomLinkType.getAttribute(LCSProperties.get("bomLink.PRIMARY")).getSearchResultIndex()))
					|| ("1").equals(fo.getData(
							bomLinkType.getAttribute(LCSProperties.get("bomLink.ALTPRIMARY")).getSearchResultIndex()))
					|| (PRE_RETAIL).equals(fo.getData(managingDeptColunName))) {
				// Add all fo to linesheetData
				linesheetData.add(fo);
			}
			// Fix to include only FOs with Primary/ Alt Primary values true - End

		}
	}

	/**
	 * @param materialSupplier - materialSupplier
	 * @param supplierFlexType - supplierFlexType
	 * @param fo - fo
	 * @param materialColor - materialColor
	 * @throws WTException - WTException
	 */
	private static void getMatSuppAndColorAttrs(
			LCSMaterialSupplier materialSupplier, 
			FlexType supplierFlexType,
			FlexObject fo, LCSMaterialColor materialColor) throws WTException {
		// Initializations
		LCSSupplier lcsSupp;
		LCSMaterial lcsMat;
		String strWashCare;
		// Get BOM flextype
		FlexType bomType = FlexTypeCache.getFlexTypeFromPath("BOM");
		FlexType materialFlexType = FlexTypeCache
				.getFlexTypeFromPath(FLEXTYPE_MATERIAL);
		final FlexType materialFabric = FlexTypeCache
				.getFlexTypeFromPath(materialFabricFlexType);


		// Get Supplier object
		lcsSupp = (LCSSupplier) VersionHelper
				.latestIterationOf(
						materialSupplier.getSupplierMaster());
		if (null != lcsSupp) {
			// Add supplier MDM to fo
			fo.put(supplierFlexType.getAttribute(SUPPLIER_MDM_ID)
					.getSearchResultIndex() + "_3", supplierFlexType
					.getAttribute(SUPPLIER_MDM_ID).getDisplayValue(lcsSupp));
		}
		// Get Material Object
		lcsMat = (LCSMaterial) VersionHelper.latestIterationOf(
				materialSupplier.getMaterialMaster());
		// If Material Type is Fabric, add wash care, finish, composition,
		// composition RU, Lamination Coating values to FO
		if (null != lcsMat
				&& lcsMat.getFlexType().getFullName(true).contains("Material\\Fabric")) {
			// Get washcare attribute
			strWashCare = materialFlexType.getAttribute(WASH_CARE)
					.getDisplayValue(lcsMat);
			// split and display wash care information in 5 columns in the
			// report
			if (FormatHelper.hasContent(strWashCare)) {
				getWashCareAttributes(materialFlexType, strWashCare, fo);
			}
			// Add fabric attr Finish to fo
			fo.put("FABRIC.vrdFinish",
					materialFabric.getAttribute(
							LCSProperties.get("materialFabric.FINISHING"))
					.getDisplayValue(lcsMat));
			// Add fabric attr COMPOSITION to fo
			fo.put("FABRIC.vrdFiberContent",
					materialFabric.getAttribute(COMPOSITION).getDisplayValue(
							lcsMat));
			// Add fabric attr COMPOSITION_RU to fo
			fo.put("FABRIC.smCompositionRU",
					materialFabric.getAttribute(COMPOSITION_RU)
					.getDisplayValue(lcsMat));
			// Add fabric attr LAMINATIONCOATING to fo
			fo.put("FABRIC.smLaminationCoating",
					materialFabric.getAttribute(
							LCSProperties
							.get("materialFabric.LAMINATIONCOATING"))
					.getDisplayValue(lcsMat));
			// If maerial type is Fabric\Other, add other level attributes to fo
			if (lcsMat.getFlexType().getFullName().contains("Other")) {
				getFabricOtherAttributes(lcsMat, fo);
			}
		}
		// colorDescription
		if (null != materialColor) {
			fo.put(LCSCOLOR_COLORNAME, materialColor.getColor().getColorName());
			
			// Set additional care value as blank
			fo.put(FlexTypeCache.getFlexTypeFromPath("Material Color")
							.getAttribute(ADDITIONALCARE_MC).getSearchResultIndex(),"");
			// Add Additional Care information only for Fabric Type of materials
			if (null != lcsMat
					&& lcsMat.getFlexType().getFullName(true).contains("Material\\Fabric")) {
				fo.put(FlexTypeCache.getFlexTypeFromPath("Material Color")
						.getAttribute(ADDITIONALCARE_MC).getSearchResultIndex(),
						materialColor.getValue(ADDITIONALCARE_MC));
				
				// When overridden row material supplier is changed without changing the color, the material color attribute (additional care) value has to be blanked out in overridden row in report
				LCSMaterialSupplier materialSupplierForMatCol = null;
				materialSupplierForMatCol = VersionHelper.latestIterationOf(materialColor.getMaterialSupplierMaster());
				if(null != materialSupplierForMatCol && !materialSupplierForMatCol.toString().equals(materialSupplier.toString())) {
					fo.put(FlexTypeCache.getFlexTypeFromPath("Material Color")
							.getAttribute(ADDITIONALCARE_MC).getSearchResultIndex(),"");
				}
			}
						
			// If Material Color is not null, also Free Text Color is present, add free text color
			if(FormatHelper.hasContent(fo.getData(bomType.getAttribute(
					COLOR_DESCRIPTION).getSearchResultIndex()))){
				fo.put(LCSCOLOR_COLORNAME, fo.getData(bomType.getAttribute(
						COLOR_DESCRIPTION).getSearchResultIndex()));
				// If material color name is not same as BOM Link color description, blank value for Additional Care
				if(!materialColor.getColor().getColorName().equals(fo.getData(bomType.getAttribute(
						COLOR_DESCRIPTION).getSearchResultIndex()))){
					fo.put(FlexTypeCache.getFlexTypeFromPath("Material Color")
							.getAttribute(ADDITIONALCARE_MC).getSearchResultIndex(),"");
				}
			}
		}
		// If Library color is blank, pull the user entered color from BOM Link
		if (null == materialColor) {
			fo.put(LCSCOLOR_COLORNAME, fo.getData(bomType.getAttribute(
					COLOR_DESCRIPTION).getSearchResultIndex()));
		}
	}

	/**
	 * @param materialFlexType - materialFlexType
	 * @param strWashCare1 - strWashCare1
	 * @param fo - fo
	 * @throws WTException - WTException
	 */
	private static void getWashCareAttributes(FlexType materialFlexType,
			String strWashCare1, FlexObject fo) throws WTException {
		String wash = "";
		String strWashCareArray[];
		// Split strWashCare1 by <br> if <br> is present in the string, else split by line separator,
		// as in some cases, strWashCare1 is split by <br> and in some case, its split by line separator.
		if(strWashCare1.contains("<br>")) {
			strWashCareArray=strWashCare1.split("<br>");
		}
		else {
			strWashCareArray=strWashCare1.split(System.lineSeparator());
		}
		//Build 3.15.0.0 Changes | moved special chars to sportmaster.lcs.properties file - STARTS
		String specialChar1 = LCSProperties.get("com.sportmaster.wc.reports.SMCareLabelReportQuery.specialChar.1");
		for(int i=1;i<=strWashCareArray.length;i++) {
			wash=strWashCareArray[0];
			if (wash.contains(specialChar1)) {
				wash = FormatHelper.removeCharacter(wash, specialChar1);
				wash = FormatHelper.removeCharacter(wash,
						LCSProperties.get("com.sportmaster.wc.reports.SMCareLabelReportQuery.specialChar.2"));
				wash = FormatHelper.removeCharacter(wash,
						LCSProperties.get("com.sportmaster.wc.reports.SMCareLabelReportQuery.specialChar.3"));
			}
			if (i==1) {
				fo.put(materialFlexType.getAttribute(WASH_CARE)
						.getSearchResultIndex() + "_1", wash);
			}else {
				
				fo.put(materialFlexType.getAttribute(WASH_CARE)
						.getSearchResultIndex() + "_"+i,strWashCareArray[i-1]);
			}
		}
		//Build 3.15.0.0 Changes | moved special chars to sportmaster.lcs.properties file - ENDS
	}

	/**
	 * @param lcsMat lcsMat
	 * @param fo - fo
	 * @throws WTException - WTException
	 */
	private static void getFabricOtherAttributes(LCSMaterial lcsMat,
			FlexObject fo) throws WTException {
		// If Material type is Fabric Other,
		// get Layer 1, Layer 2 attributes from Fabric Other 
		final FlexType materialFabric = FlexTypeCache
				.getFlexTypeFromPath(materialFabricFlexType);
		LCSMaterial layer1Material;
		LCSMaterial layer2Material;
		layer1Material = (LCSMaterial) lcsMat.getValue(LCSProperties
				.get("materialFabric.LAYER1"));
		layer2Material = (LCSMaterial) lcsMat.getValue(LCSProperties
				.get("materialFabric.LAYER2"));
		// Get the COMPOSITION, COMPOSITION_RU from layer 1 and
		// add to fo
		if (null != layer1Material) {
			fo.put("FABRICOTHER.smLayer1_1",
					materialFabric.getAttribute(COMPOSITION).getDisplayValue(
							layer1Material));
			fo.put("FABRICOTHER.smLayer1_2",
					materialFabric.getAttribute(COMPOSITION_RU)
					.getDisplayValue(layer1Material));
		}
		// Get the COMPOSITION, COMPOSITION_RU from layer 2 and
		// add to fo
		if (null != layer2Material) {
			fo.put("FABRICOTHER.smLayer2_1",
					materialFabric.getAttribute(COMPOSITION).getDisplayValue(
							layer2Material));
			fo.put("FABRICOTHER.smLayer2_2",
					materialFabric.getAttribute(COMPOSITION_RU)
					.getDisplayValue(layer2Material));
		}
	}

	/**
	 * @param season - season
	 * @param lcsSrcConf - lcsSrcConf
	 * @param fo - fo
	 * @param supplierFlexType - supplierFlexType
	 * @throws WTException - WTException
	 */
	private static void getSourcingConfigAttributes(LCSSeason season,
			LCSSourcingConfig lcsSrcConf, FlexObject fo,
			FlexType supplierFlexType) throws WTException {
		LCSSupplier lcsSuppVendor;
		LCSSourceToSeasonLink stsl;
		LCSSupplier lcsSuppFactory;
		// get Vendor from sourcing config
		lcsSuppVendor = (LCSSupplier) lcsSrcConf.getValue(LCSProperties
				.get("sourcingConfig.VENDOR"));
		if (null != lcsSuppVendor) {
			// add vendor's supplier mdm to fo
			fo.put(supplierFlexType.getAttribute(SUPPLIER_MDM_ID)
					.getSearchResultIndex() + "_1",
					supplierFlexType.getAttribute(SUPPLIER_MDM_ID)
					.getDisplayValue(lcsSuppVendor));
		}

		stsl = null;
		if (null != season) {
			// get source to season link
			stsl = (new LCSSourcingConfigQuery()).getSourceToSeasonLink(
					lcsSrcConf, season);
			if (null != stsl) {
				// get Factory from sourcing config
				lcsSuppFactory = (LCSSupplier) stsl.getValue(LCSProperties
						.get("sourceToSeasLink.FACTORY"));
				if (null != lcsSuppFactory) {
					// add Factory's supplier mdm to fo
					fo.put(supplierFlexType.getAttribute(SUPPLIER_MDM_ID)
							.getSearchResultIndex() + "_2",
							supplierFlexType.getAttribute(SUPPLIER_MDM_ID)
							.getDisplayValue(lcsSuppFactory));
				}
			}
		}
	}

	/**
	 * @param fo - fo
	 * @param overriddnRow - overriddnRow
	 * @return fo - fo
	 * @throws WTException - WTException 
	 */
	private static FlexObject setOverridenRowValues(FlexObject fo,
			FlexObject overriddnRow) throws WTException {
		Iterator bomOverridenRowsItr = overriddnRow.entrySet().iterator();
		Map.Entry me2;
		String key = "";
		FlexType bomLinkType = FlexTypeCache
				.getFlexTypeFromPath("BOM\\Materials\\Product");
		while (bomOverridenRowsItr.hasNext()) {
			me2 = (Map.Entry) bomOverridenRowsItr.next();
			key = (String) me2.getKey();
			// For Boolean attributes, if value is false, its setting as 0 in DB, so adding a condition to include 0 values for boolean attributes
			if(key.contains(OVERRIDENROW) && (key.equals(OVERRIDENROW + bomLinkType.getAttribute(LCSProperties.get("bomLink.PRIMARY")).getSearchResultIndex().replaceAll("FlexBOMLink", "").toUpperCase())
					|| key.equals(OVERRIDENROW + bomLinkType.getAttribute(LCSProperties.get("bomLink.ALTPRIMARY")).getSearchResultIndex().replaceAll("FlexBOMLink", "").toUpperCase())
					|| key.equals(OVERRIDENROW + bomLinkType.getAttribute(LCSProperties.get("bomLink.CCC")).getSearchResultIndex().replaceAll("FlexBOMLink", "").toUpperCase())))
			{
				if(FormatHelper.hasContentAllowZero((String) me2.getValue())) {
					fo.put(key.replace(OVERRIDENROW, "FLEXBOMLINK"),
							me2.getValue());
				}
			}
			else if (key.contains(OVERRIDENROW) && !("0").equals(me2.getValue())
					&& FormatHelper.hasContent((String) me2.getValue())) {
				fo.put(key.replace(OVERRIDENROW, "FLEXBOMLINK"),
						me2.getValue());
			}
		}
		return fo;
	}

	/**
	 * @param fo - fo
	 * @param destinationRow - destinationRow
	 * @param colorwayRow - colorwayRow
	 * @param mainRow - mainRow
	 * @return fo - fo
	 */
	private static FlexObject setOverridenRowValues(FlexObject fo,
			FlexObject destinationRow, FlexObject colorwayRow,
			FlexObject mainRow) {
		if (null != destinationRow) {
			for (String key : destinationRow.getIndexes()) {
				try {
					if (key.equalsIgnoreCase(FlexTypeCache
							.getFlexTypeFromPath(FLEXTYPE_MATERIAL)
							.getAttribute(MATERIALMDM).getSearchResultIndex())
							&& !destinationRow.getData(LCSMATERIAL_BRANCHIDITERATIONINFO)
							.equals(fo.getData(LCSMATERIAL_BRANCHIDITERATIONINFO))) {
						continue;
					}
				} catch (WTException e) {
					e.printStackTrace();
				}
				setMaterialColorOverriddenRows(fo, key, destinationRow);				
			}
		}
		if (null != colorwayRow) {
			setOverridenColorwayRowValues(colorwayRow, fo, destinationRow);
		}
		setMainRowValuesForOverriddenRows(mainRow, fo);

		return fo;
	}

	/**
	 * @param fo - fo
	 * @param key - key
	 * @param destinationRow - destinationRow
	 */
	private static void setMaterialColorOverriddenRows(FlexObject fo, 
			String key, FlexObject destinationRow) {
		// To handle overidden mat-colo is empty on main coll, then get
		// from colorway var. to handle colo added at parent level
		if (OVERRIDENROW_IDA3D5.equals(key)
				&& "0".equals(fo.getData(OVERRIDENROW_IDA3D5))) {
			fo.setData(LCSMATERIALCOLOR_IDA2A2,
					destinationRow.getString(LCSMATERIALCOLOR_IDA2A2));
		} else if (FormatHelper.hasContent(destinationRow.getData(key))
				&& !"0".equals(destinationRow.getData(key))
				&& (!FormatHelper.hasContent(fo.getData(key)) 
						|| "0".equals(fo.getData(key)))) {
			fo.setData(key, destinationRow.getData(key));
		}		
	}

	/**
	 * @param colorwayRow - colorwayRow
	 * @param fo - fo
	 * @param destinationRow - destinationRow
	 */
	private static void setOverridenColorwayRowValues(FlexObject colorwayRow,
			FlexObject fo, FlexObject destinationRow) {
		// Iterate colorwayRow
		for (String key : colorwayRow.getIndexes()) {
			try {
				if (key.equalsIgnoreCase(FlexTypeCache
						.getFlexTypeFromPath(FLEXTYPE_MATERIAL)
						.getAttribute(MATERIALMDM).getSearchResultIndex())
						&& !colorwayRow.getData(
								LCSMATERIAL_BRANCHIDITERATIONINFO).equals(
										fo.getData(LCSMATERIAL_BRANCHIDITERATIONINFO))) {
					continue;
				}
			} catch (WTException e) {
				LOGGER.error("WTException in SMCareLabelReportQuery -" +
						" setOverridenRowValues:"+ e.getMessage());
				e.printStackTrace();
			}			
			setMaterialColorFO(colorwayRow, fo, destinationRow, key);	
			// To handle destina level color change/ colorway level color change
			if (null != destinationRow && OVERRIDENROW_IDA3D5.equals(key)
					&& "0".equals(fo.getData(OVERRIDENROW_IDA3D5))) {
				fo.setData(LCSMATERIALCOLOR_IDA2A2,
						destinationRow.getString(LCSMATERIALCOLOR_IDA2A2));
			}
		}
	}

	/**
	 * @param colorwayRow - colorwayRow
	 * @param fo - fo
	 * @param destinationRow - destinationRow
	 * @param key - key
	 */
	private static void setMaterialColorFO(FlexObject colorwayRow,
			FlexObject fo, FlexObject destinationRow, String key) {
		boolean isSet = false;
		// To handle overidden mat-color is empty on main coll, then get from
		// colorway var. to handle color added at parent level
		if (OVERRIDENROW_IDA3D5.equals(key)
				&& "0".equals(fo.getData(OVERRIDENROW_IDA3D5))
				&& !":DESTINATION".equalsIgnoreCase(fo
						.getData("OVERRIDENROW.DIMENSIONNAME"))) {
			fo.setData(LCSMATERIALCOLOR_IDA2A2, colorwayRow.getString(LCSMATERIALCOLOR_IDA2A2));
			isSet = true;
		} else if (FormatHelper.hasContent(colorwayRow.getData(key))
				&& !"0".equals(colorwayRow.getData(key))
				&& (!FormatHelper.hasContent(fo.getData(key)) 
						|| "0".equals(fo.getData(key)))) {

			fo.setData(key, colorwayRow.getData(key));
		} else if (LCSMATERIALCOLOR_IDA2A2.equals(key)) {
			// Adding new method to reduce cyclometric complexity
			setMaterialColorFlexObject(isSet, colorwayRow, fo, key, destinationRow);
		}		
	}

	/**
	 * @param isSet - isSet
	 * @param colorwayRow - colorwayRow
	 * @param fo - fo
	 * @param key - key
	 * @param destinationRow - destinationRow
	 */
	private static void setMaterialColorFlexObject(boolean isSet,
			FlexObject colorwayRow, FlexObject fo, String key, FlexObject destinationRow) {
		if(destinationRow == null && FormatHelper.hasContent(colorwayRow
				.getString(LCSMATERIALCOLOR_IDA2A2)) 
				&& !colorwayRow.getString(LCSMATERIALCOLOR_IDA2A2).equals(
						fo.getString(LCSMATERIALCOLOR_IDA2A2)) && isSet){
			//	 Add colorway row values to fo
			fo.setData(key, colorwayRow.getData(key));
		}
	}

	/**
	 * @param mainRow - mainRow
	 * @param fo - fo
	 */
	private static void setMainRowValuesForOverriddenRows(FlexObject mainRow,
			FlexObject fo) {
		if (null != mainRow) {
			for (String key : mainRow.getIndexes()) {
				try {
					if (key.equalsIgnoreCase(FlexTypeCache
							.getFlexTypeFromPath(FLEXTYPE_MATERIAL)
							.getAttribute(MATERIALMDM).getSearchResultIndex())
							&& !mainRow
							.getData(LCSMATERIAL_BRANCHIDITERATIONINFO)
							.equals(fo
									.getData(LCSMATERIAL_BRANCHIDITERATIONINFO))) {
						continue;
					}
				} catch (WTException e) {
					LOGGER.error("WTException in SMCareLabelReportQuery " +
							"- setOverridenRowValues:"+ e.getMessage());
					e.printStackTrace();
				}
				if (FormatHelper.hasContent(mainRow.getData(key))
						&& !"0".equals(mainRow.getData(key))
						&& (!FormatHelper.hasContent(fo.getData(key)) 
								|| "0".equals(fo.getData(key)))) {
					fo.setData(key, mainRow.getData(key));
				}
			}
		}
	}
}
