package com.burberry.wc.integration.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.productbomapi.bean.*;
import com.burberry.wc.integration.productbomapi.constant.BurProductBOMConstant;
import com.burberry.wc.integration.productbomapi.transform.BurberryProductBOMAPIDataTransform;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.specification.FlexSpecToComponentLink;
import com.lcs.wc.specification.FlexSpecToSeasonLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;

/**
 * A Helper class to handle JSON data transform activity for Product BOM API.
 * Class contain several method to handle transform of object data putting it to
 * the beans.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryProductBOMAPIJsonDataUtil {

	/**
	 * logger.
	 */
	public static final Logger logger = Logger
			.getLogger(BurberryProductBOMAPIJsonDataUtil.class);

	/**
	 * Private constructor.
	 */
	private BurberryProductBOMAPIJsonDataUtil() {

	}

	/**
	 * @param primSpec
	 * @param seasonMaster
	 * @param skus
	 * @param colProdToBOMIds
	 * @param colProdToBOMLinkIds
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	/*public static PrimarySpec getPrimarySpec(FlexSpecification primSpec,LCSProduct product,LCSSourcingConfig source,
			LCSSeasonMaster seasonMaster, Collection<LCSSKU> skus,
			Collection<String> colProdToBOMIds, Collection<String> colProdToBOMLinkIds,
			Map<String, Collection<HashMap>> mapTrackedBOM)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {

		String methodName = "getPrimarySpec()";
		PrimarySpec primarySpec = new PrimarySpec();
		if (primSpec != null) {
			logger.debug(methodName + "Extracting data from Specification "
					+ primSpec.getName());
			Map<String, String> jsonMapping = BurberryAPIUtil
					.getJsonMapping(BurProductBOMConstant.JSON_SPEC);
			Map<String, String> sysJsonMapping = BurberryAPIUtil
					.getJsonMapping(BurProductBOMConstant.SYSTEM_JSON_SPEC);
			// Getting primary spec bean data
			BurberryAPIBeanUtil
					.getObjectData(BurProductBOMConstant.SPEC_IGNORE,
							primarySpec, primSpec,
							BurProductBOMConstant.SPEC_ATT, jsonMapping,
							sysJsonMapping);
			// Validating Required attributes
			//BurberryAPIBeanUtil.validateRequiredAttributes(primarySpec,
			//		BurProductBOMConstant.SPEC_REQ);
			// setting boms on primary spec
			primarySpec.setBOM(BurberryProductBOMAPIDataTransform
					.getListBOMBean(product,source,primSpec, seasonMaster, skus,
							colProdToBOMIds, colProdToBOMLinkIds,mapTrackedBOM));
		}
		logger.debug(methodName + "Returing Specification bean " + primarySpec);
		// returning primary spec bean
		return primarySpec;
	}*/

	/**
	 * @param flexBOMPart
	 * @param flexSpecToBOMLink 
	 * @param seasonMaster
	 * @param skus
	 * @param colProdToBOMLinkIds
	 * @param deltaCriteria 
	 * @param bomDeltaDateMap 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	public static BOM getBomBean(FlexBOMPart flexBOMPart,
			FlexSpecToComponentLink flexSpecToBOMLink, LCSSeasonMaster seasonMaster, Collection<LCSSKU> skus,
			Collection<String> colProdToBOMLinkIds, boolean deltaCriteria, Map bomDeltaDateMap) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException {
		String methodName = "getBomBean()";
		BOM bomBean = new BOM();
		logger.info(methodName + "Extracting data from BOM Part "
				+ flexBOMPart.getName());

		Map<String, String> jsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.JSON_BOM);

		Map<String, String> sysJsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.SYSTEM_JSON_BOM);
		// Getting BOM bean data
		BurberryAPIBeanUtil.getObjectData(BurProductBOMConstant.BOM_IGNORE,
				bomBean, flexBOMPart, BurProductBOMConstant.BOM_ATT,
				jsonMapping, sysJsonMapping);
		bomBean.setBOMLink(BurberryProductBOMAPIDataTransform.getListBomLinks(
				flexBOMPart, seasonMaster, skus, colProdToBOMLinkIds,deltaCriteria,bomDeltaDateMap));
		if(sysJsonMapping.containsKey(BurProductBOMConstant.BOMTYPE)){
			//logger.debug("bomBean "+bomBean);
			BeanUtils.setProperty(bomBean,sysJsonMapping.get(BurProductBOMConstant.BOMTYPE),
					flexBOMPart.getFlexType().getTypeDisplayName());
		}
		// Validate required attributes
		//BurberryAPIBeanUtil.validateRequiredAttributes(bomBean,
			//	BurProductBOMConstant.BOM_REQ);
		
		// BURBERRY-1436: RD 58 - Get Latest Material & Material Supplier information for BOM Link: Start
		// Check Primary Material description json key
		if (jsonMapping
				.containsKey(BurProductBOMConstant.JSON_KEY_PM_DESCRIPTION)) {
			// Get Material Supplier Name
			String strMaterialSupplierName = getLatestMaterialSupplierName(flexBOMPart);
			if (FormatHelper.hasContent(strMaterialSupplierName)) {
				// Set Material Supplier Name
				BeanUtils.setProperty(bomBean, jsonMapping
						.get(BurProductBOMConstant.JSON_KEY_PM_DESCRIPTION),
						strMaterialSupplierName);
			}
		}
		// BURBERRY-1436: RD 58 - Get Latest Material & Material Supplier information for BOM Link: End
		
		// RD-39 - BURBERRY-1399: Start
		// Get BOM Part Branch Iteration Id
		if (sysJsonMapping.containsKey(BurConstant.BRANCHID)) {
			BeanUtils.setProperty(bomBean,
					sysJsonMapping.get(BurConstant.BRANCHID),
					flexBOMPart.getBranchIdentifier());
		}
		// RD-39 - BURBERRY-1399: End
		
		//BURBERRY-1484: BI-010 - Primary BOM? Attribute needs adding to API as priority: START
		if (sysJsonMapping.containsKey(BurConstant.PRIMARY)) {
			BeanUtils.setProperty(bomBean,
					sysJsonMapping.get(BurConstant.PRIMARY),
					flexSpecToBOMLink.isPrimaryComponent());
		}
		//BURBERRY-1484: BI-010 - Primary BOM? Attribute needs adding to API as priority: END
		
		logger.debug(methodName + "Returing BOM bean " + bomBean);
		// returning bom bean
		return bomBean;
	}

	// // BURBERRY-1436: RD 58 - Get Latest Material & Material Supplier information for BOM Link: Start
	/**
	 * Method to get material supplier name from object reference
	 * 
	 * @param flexBOMPart
	 *            FlexBOMPart
	 * @return String
	 * @throws WTException
	 *             Exception
	 */
	private static String getLatestMaterialSupplierName(FlexBOMPart flexBOMPart)
			throws WTException {
		//Method Name
		String methodName = "getLatestMaterialSupplierName() ";
		//Initialisation
		String strMaterialSuppName = BurConstant.STRING_EMPTY;
		// Get Primary Material Supplier Object
		LCSMaterialSupplier matSupplierObject = (LCSMaterialSupplier) flexBOMPart
				.getValue(BurProductBOMConstant.STR_PRIMARY_MATERIAL);
		// Check for null and not placeholder
		if (matSupplierObject != null
				&& !FormatHelper.areWTObjectsEqual(
						matSupplierObject.getMaterialMaster(),
						LCSMaterialQuery.PLACEHOLDER)) {
			logger.debug(methodName + "Material-Supplier Object: "
					+ matSupplierObject.getName());
			strMaterialSuppName = matSupplierObject.getName();
		}
		//Return
		return strMaterialSuppName;
	}
	// BURBERRY-1436: RD 58 - Get Latest Material & Material Supplier information for BOM Link: : End

	/**
	 * @param flexBOMPart
	 * @param section
	 * @param seasonMaster
	 * @param skus
	 * @param colProdToBOMLinkIds
	 * @param deltaCriteria 
	 * @param bomDeltaDateMap 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	public static BOMLink getBOMLinkBean(FlexBOMPart flexBOMPart,
			String section, LCSSeasonMaster seasonMaster,
			Collection<LCSSKU> skus, Collection<String> colProdToBOMLinkIds, boolean deltaCriteria, Map bomDeltaDateMap)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {

		String methodName = "getBOMLinkBean()";
		BOMLink bomLinkBean = null;
		List<SectionVariation> lstSectionVariation = new ArrayList<SectionVariation>();
		logger.debug(methodName + "Extracting data from BOM Link " + flexBOMPart);
		// Get child links map
		Map<FlexObject, List> linksMap = BurberryProductBOMAPIUtil.getChildMap(
				flexBOMPart, FlexTypeCache.getFlexTypeRoot("Material"));
		for (Entry<FlexObject, List> entry : linksMap.entrySet()) {
			FlexObject branch = entry.getKey();
			FlexBOMLink link = (FlexBOMLink) LCSQuery
					.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:"
							+ branch.getData("FLEXBOMLINK.IDA2A2"));
			if (section.equals(BurberryDataUtil.getData(link,
					BurProductBOMConstant.SECTION, null))
					&& link.getOutDate() == null && (!link.isDropped() || deltaCriteria)) {
				// check if BOM Link criteria matches with the URL and Link is
				// not an empty link
				boolean ifIdExists = BurberryAPIDBUtil.checkIfObjectExists(
						branch.getData("FLEXBOMLINK.IDA2A2"), colProdToBOMLinkIds);
				boolean nonEmptyLink = BurberryProductBOMAPIUtil
						.validateValuesonLink(link);
				//CR BURBERRY-1437: Handle Dropped BOM Links for Delta Scenario : Start
				boolean validDeltaDroppedLink = checkDroppedLinkForDelta(link, deltaCriteria, bomDeltaDateMap);
				//CR BURBERRY-1437: Handle Dropped BOM Links for Delta Scenario : End
				if (ifIdExists && nonEmptyLink && validDeltaDroppedLink) {
					lstSectionVariation.add(getSectionVariationBean(link,
							linksMap.get(branch), seasonMaster, skus));
				}
			}
		}
		if (!lstSectionVariation.isEmpty()) {
			bomLinkBean = new BOMLink();
			bomLinkBean.setSection(section);
			bomLinkBean.setSectionVariation(lstSectionVariation);
		}

		logger.debug(methodName + "Returing Bom link bean " + bomLinkBean);
		// Returning bom link
		return bomLinkBean;

	}
	
	// CR BURBERRY-1437: Handle Dropped BOM Links for Delta Scenario : Start
	/**
	 * Check if dropped link is modified between delta date range.
	 * 
	 * @param link
	 *            BOM Link
	 * @param deltaCriteria
	 *            Boolean
	 * @param deltaDateMap
	 *            Delta Criteria Map
	 * @return
	 */
	private static boolean checkDroppedLinkForDelta(FlexBOMLink link,
			boolean deltaCriteria, Map deltaDateMap) {
		// Method Name
		String methodName = "checkDroppedLinkForDelta() ";
		// Check for Delta and Dropped Links
		if (deltaCriteria && link.isDropped()) {
			// Get Start Date
			Date startDate = (Date) deltaDateMap.get("startdate");
			logger.debug(methodName + "Start Date: " + startDate);
			// Get End Date
			Date endDate = (Date) deltaDateMap.get("enddate");
			logger.debug(methodName + "End Date: " + endDate);
			// Get modify time stamp
			Date linkModifiedDate = link.getModifyTimestamp();
			logger.debug(methodName + "Link Modify Timestamp: " + linkModifiedDate);
			logger.debug(methodName + "Dropped BOM Link: "
					+ FormatHelper.getNumericObjectIdFromObject(link));
			// Check date range between start and end dates
			if ((startDate.before(linkModifiedDate) && endDate.after(linkModifiedDate))
					|| startDate.equals(linkModifiedDate)
					|| endDate.equals(linkModifiedDate)) {
				// Return true dropped link was modified for date range
				logger.debug(methodName
						+ "Return true dropped link was modified in date range.");
				return true;
			} else {
				// Return false dropped link was not modified for date range
				logger.debug(methodName
						+ "Return false dropped link was not modified in date range.");
				return false;
			}
		}
		// Return true not a dropped link
		return true;
	}

	// CR BURBERRY-1437: Handle Dropped BOM Links for Delta Scenario : End

	/**
	 * @param link
	 * @param childLinks
	 * @param seasonMaster
	 * @param skus
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	public static SectionVariation getSectionVariationBean(FlexBOMLink link,
			List childLinks, LCSSeasonMaster seasonMaster,
			Collection<LCSSKU> skus) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException {

		String methodName = "getSectionVariationBean()";
		SectionVariation sectionVariationBean = new SectionVariation();

		Map<String, String> jsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.JSON_BOMLINK);

		Map<String, String> sysJsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.SYSTEM_JSON_BOMLINK);
		// getting section variation data
		BurberryAPIBeanUtil.getObjectData(BurProductBOMConstant.BOMLINK_IGNORE,
				sectionVariationBean, link, BurProductBOMConstant.BOMLINK_ATT,
				jsonMapping, sysJsonMapping);
		
		//Get the latest material name
		appendMaterialNameOnBean(link,jsonMapping,sectionVariationBean);
		//Get the latest supplier name
		appendSupplierNameOnBean(link,sysJsonMapping,sectionVariationBean);
		//Get the latest color name
		appendColorNameOnBean(link,jsonMapping,sectionVariationBean);
		
		// RD-39 - BURBERRY-1399: Start
		// Get BOM Link Unique Id
		if (sysJsonMapping.containsKey(BurProductBOMConstant.STR_BOM_BRANCH_ID)) {
			BeanUtils
					.setProperty(sectionVariationBean, sysJsonMapping
							.get(BurProductBOMConstant.STR_BOM_BRANCH_ID), 
							link.getBranchId());
		}
		// RD-39 - BURBERRY-1399: End	
		
		// Checking if material on Link is not placeholder material
		if (link.getChild()!=null && !FormatHelper.areWTObjectsEqual(link.getChild(),
				LCSMaterialQuery.PLACEHOLDER)) {
			sectionVariationBean.setMaterialMaster(BurberryProductBOMAPIUtil
					.getMaterialMasterBean(link));
		}
		// Setting colourway variation
		sectionVariationBean
				.setColourVariation(BurberryProductBOMAPIDataTransform
						.getListColourVariation(link, childLinks, seasonMaster,
								skus));
		//BurberryAPIBeanUtil.validateRequiredAttributes(sectionVariationBean,
		//		BurProductBOMConstant.BOMLINK_REQ);

		// Setting BOM Link Sorting Number - CR R11 Changes
		if(jsonMapping.containsKey(BurProductBOMConstant.SORTING_NUMBER_KEY)){
			BeanUtils.setProperty(sectionVariationBean,
					jsonMapping.get(BurProductBOMConstant.SORTING_NUMBER_KEY),
					link.getSortingNumber());
		}
		if(link.isDropped()){
			BeanUtils.setProperty(sectionVariationBean,BurConstant.JSON_CRUD_KEY,
					"DELETE");
		}
		logger.debug(methodName + "Returing section variation bean "
				+ sectionVariationBean);
		// Returning section variation bean
		return sectionVariationBean;

	}
	
	// BURBERRY-1436: RD 58 - Get Latest Material & Material Supplier
	// information for BOM Link: Start
	/**
	 * Method to get latest material name from link.
	 * 
	 * @param link
	 *            FlexBOMLink
	 * @param jsonMapping
	 *            JSON MAP
	 * @param sectionVariationBean
	 *            Bean
	 * @throws WTException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 */
	private static void appendMaterialNameOnBean(FlexBOMLink link,
			Map<String, String> jsonMapping,
			SectionVariation sectionVariationBean) throws WTException,
			IllegalAccessException, InvocationTargetException {
		// Checking if material on Link is not placeholder material
		if (link.getChild() != null
				&& !FormatHelper.areWTObjectsEqual(link.getChild(),
						LCSMaterialQuery.PLACEHOLDER)) {
			// Check if json map contains key
			if (jsonMapping
					.containsKey(BurProductBOMConstant.JSON_KEY_MATERIAL_DESCRIPTION)) {
				// Get Material Object from BOM Link
				LCSMaterial materialObj = (LCSMaterial) VersionHelper
						.latestIterationOf(link.getChild());
				// Set Material Name
				BeanUtils
						.setProperty(
								sectionVariationBean,
								jsonMapping
										.get(BurProductBOMConstant.JSON_KEY_MATERIAL_DESCRIPTION),
								materialObj.getName());
			}
		}

	}

	// BURBERRY-1436: RD 58 - Get Latest Material & Material Supplier
	// information for BOM Link: End

	// BURBERRY-1389: Fix for Material Supplier/Supplier Name : Start
	/**
	 * Method to get latest supplier name.
	 * 
	 * @param link
	 *            FlexBOMLink
	 * @param sysJsonMapping
	 *            JSON MAP
	 * @param sectionVariationBean
	 *            Bean
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 */
	private static void appendSupplierNameOnBean(FlexBOMLink link,
			Map<String, String> sysJsonMapping,
			SectionVariation sectionVariationBean)
			throws IllegalAccessException, InvocationTargetException {
		// Check supplier description json key
		if (sysJsonMapping
				.containsKey(BurProductBOMConstant.JSON_KEY_SUPPLIER_DESCRIPTION)) {
			// Check link has supplier and not placeholder
			if (FormatHelper.hasContent(link.getSupplier().getSupplierName())
					&& !BurProductBOMConstant.STR_PLACEHOLDER
							.equalsIgnoreCase(link.getSupplier()
									.getSupplierName())) {
				// Set supplier name
				BeanUtils
						.setProperty(
								sectionVariationBean,
								sysJsonMapping
										.get(BurProductBOMConstant.JSON_KEY_SUPPLIER_DESCRIPTION),
								link.getSupplier().getSupplierName());
			}
		}
	}

	// BURBERRY-1389: Fix for Material Supplier/Supplier Name: End

	// BURBERRY-1495: RD-76 Free-typed colour data in BOM not appearing in
	// Rating report: Start
	/**
	 * Method to get latest color name.
	 * 
	 * @param link
	 *            FlexBOMLink
	 * @param jsonMapping
	 *            JSON MAP
	 * @param sectionVariationBean
	 *            Bean
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 */
	private static void appendColorNameOnBean(FlexBOMLink link,
			Map<String, String> jsonMapping,
			SectionVariation sectionVariationBean)
			throws IllegalAccessException, InvocationTargetException {
		// Get Color Object
		LCSColor color = link.getColor();
		// Check if color object exists
		if (color != null
				&& jsonMapping
						.containsKey(BurProductBOMConstant.JSON_CV_COLOR_DESC)) {
			// Set color name
			BeanUtils.setProperty(sectionVariationBean,
					jsonMapping.get(BurProductBOMConstant.JSON_CV_COLOR_DESC),
					color.getName());
		}
	}
	// BURBERRY-1495: RD-76 Free-typed colour data in BOM not appearing in
	// Rating report: End

	/**
	 * This method is to get specification bean.
	 * 
	 * @param spec
	 * @param specToSeasonLink
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	public static Specification getSpecificationBean(FlexSpecification spec,
			FlexSpecToSeasonLink specToSeasonLink)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		String methodName = "getSpecificationBean() ";
		logger.debug(methodName + "Extracting data from specification " + spec);

		Specification primarySpecBean = new Specification();

		// Generate json mapping from keys
		Map<String, String> jsonSpecificationMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.JSON_SPEC);
		logger.debug(methodName + "jsonSpecificationMapping: "
				+ jsonSpecificationMapping);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonSpecificationMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.SYSTEM_JSON_SPEC);
		logger.debug(methodName + "systemAttjsonSpecificationMapping: "
				+ systemAttjsonSpecificationMapping);

		BurberryAPIBeanUtil.getObjectData(BurProductBOMConstant.SPEC_IGNORE,
				primarySpecBean, spec, BurProductBOMConstant.SPEC_ATT,
				jsonSpecificationMapping, systemAttjsonSpecificationMapping);

		// Get Whether Specification is primary
		if (systemAttjsonSpecificationMapping.containsKey(BurConstant.PRIMARY)) {
			BeanUtils.setProperty(primarySpecBean,
					systemAttjsonSpecificationMapping.get(BurConstant.PRIMARY),
					specToSeasonLink.isPrimarySpec());
		}
		
		if (systemAttjsonSpecificationMapping.containsKey(BurConstant.BRANCHID)) {
			BeanUtils.setProperty(primarySpecBean,
					systemAttjsonSpecificationMapping.get(BurConstant.BRANCHID),
					String.valueOf(spec.getBranchIdentifier()));
		}

		// BurberryAPIBeanUtil.validateRequiredAttributes(primarySpecBean,
		// BurConstant.SPECIFICATION_REQ);
		// Return statement
		return primarySpecBean;
	}

}
