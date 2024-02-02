package com.burberry.wc.integration.productbomapi.transform;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.fc.Persistable;
import wt.util.WTException;

import com.burberry.wc.integration.productbomapi.bean.*;
import com.burberry.wc.integration.productbomapi.constant.BurProductBOMConstant;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.*;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecToComponentLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;

/**
 * A Helper class to handle Transformation activity. Class contain several
 * method to handle Extraction activity i.e. Extracting Data from different
 * objects and putting it to the bean
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryProductBOMAPIDataTransform {

	/**
	 * logger.
	 */
	public static final Logger logger = Logger
			.getLogger(BurberryProductBOMAPIDataTransform.class);

	/**
	 * DIMID_COL.
	 */
	public static final String DIMID_COL = "FLEXBOMLINK.DIMENSIONID";

	/**
	 * DIM_COL.
	 */
	public static final String DIM_COL = "FLEXBOMLINK.DIMENSIONNAME";

	/**
	 * Private constructor.
	 */
	private BurberryProductBOMAPIDataTransform() {

	}

	/**
	 * @param primSpec
	 * @param seasonMaster
	 * @param skus
	 * @param colProdToBOMIds
	 * @param colProdToBOMLinkIds
	 * @param deltaCriteria 
	 * @param bomDeltaDateMap 
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 */
	public static List<BOM> getListBOMBean(LCSProduct product,
			LCSSourcingConfig source, FlexSpecification primSpec,
			LCSSeasonMaster seasonMaster, Collection<LCSSKU> skus,
			Collection<String> colProdToBOMIds,
			Collection<String> colProdToBOMLinkIds,
			Map<String, Collection<HashMap>> mapTrackedBOM, boolean deltaCriteria, Map bomDeltaDateMap) throws WTException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, IOException {

		String methodName = "getListBOMBean()";
		// Track execution time
		long lstBOMStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"BOM List Start Time: ");
		List<BOM> lstBOM = new ArrayList<BOM>();
		Map<String,String> associatedBOMMap = new HashMap<String,String>();
		// Get all BOM available for primary spec
		Collection<FlexBOMPart> specComLinks = FlexSpecQuery.getSpecComponents(
				primSpec, "BOM");
		for (FlexBOMPart flexBOMPart : specComLinks) {
			flexBOMPart = (FlexBOMPart) VersionHelper
					.latestIterationOf(flexBOMPart);
			if(VersionHelper.isCheckedOut(flexBOMPart)) {
				flexBOMPart=(FlexBOMPart) VersionHelper.predecessorOf(flexBOMPart);
			}
			boolean objectExist = BurberryAPIDBUtil.checkIfObjectExists(
					String.valueOf(flexBOMPart.getBranchIdentifier()),
					colProdToBOMIds);
			// Check if object exists in BOM criteria filter
			if (objectExist || deltaCriteria) {
				// Add BOM to list of bom beans
				logger.debug(methodName + " Add BOM " + flexBOMPart.getName()
						+ " to list of boms");
				//BURBERRY-1484: BI-010 - Primary BOM? Attribute needs adding to API as priority: START
				FlexSpecToComponentLink flexSpecToBOMLink = FlexSpecQuery.getSpecToComponentLink(primSpec, flexBOMPart);
				lstBOM.add(BurberryProductBOMAPIJsonDataUtil.getBomBean(
						flexBOMPart,flexSpecToBOMLink, seasonMaster, skus, colProdToBOMLinkIds,deltaCriteria,bomDeltaDateMap));
				//BURBERRY-1484: BI-010 - Primary BOM? Attribute needs adding to API as priority: END
				
				// BURBERRY-1420: BOM header Unique ID in CRUD Flag for BOM API Output - Start
				//associatedBOM.add(flexBOMPart.getName());
				associatedBOMMap.put(String.valueOf(flexBOMPart.getBranchIdentifier()),flexBOMPart.getName());
				// BURBERRY-1420: BOM header Unique ID in CRUD Flag for BOM API Output - End
			}
		}

		// CR R26: Handle Remove BOM Customisation : Start
		List<BOM> lstRemovedBOM = getRemovedBOMList(product, source, primSpec,
				associatedBOMMap, mapTrackedBOM);
		logger.debug(methodName + "List of Removed BOM: " + lstRemovedBOM);
		lstBOM.addAll(lstRemovedBOM);
		// CR R26: Handle Remove BOM Customisation : End

		logger.debug(methodName + "List of BOM: " + lstBOM);

		logger.debug(methodName + "list of bom beans " + lstBOM);

		// Track execution time
		long lstBOMEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"List BOM End Time: ");
		logger.debug(methodName + "List BOM  Total Execution Time (ms): "
				+ (lstBOMEndTime - lstBOMStartTime));
		// Return list of BOM objects
		return lstBOM;
	}

	// CR R26: Handle Remove BOM Customisation : Start
	/**
	 * Method to get Removed BOM Bean.
	 * 
	 * @param product
	 * @param source
	 * @param primSpec
	 * @param associatedBOM
	 * @param mapTrackedBOM
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws WTException 
	 */
	private static List<BOM> getRemovedBOMList(LCSProduct product,
			LCSSourcingConfig source, FlexSpecification primSpec,
			Map<String,String> associatedBOMMap,
			Map<String, Collection<HashMap>> mapTrackedBOM)
			throws IllegalAccessException, InvocationTargetException, WTException {

		String methodName = "getRemovedBOMList() ";
		// Track execution time
		long remBOMStart = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove BOM Transform Start Time: ");

		// Initialisation
		List<BOM> lstRemovedBOMBean = new ArrayList<BOM>();
		Map<String,String> removedBOMMap = new HashMap<String,String>();

		// Check Product Id
		if (mapTrackedBOM.containsKey(String.valueOf(product
				.getBranchIdentifier()))) {

			// Get the Collection
			Collection<HashMap> colMap = mapTrackedBOM.get(String
					.valueOf(product.getBranchIdentifier()));

			// Loop through the collection
			for (HashMap hm : colMap) {
				// Get Source Id
				String strSourceId = String.valueOf(hm.get("SOURCE_ID"));
				// Get Specification Id
				String strSpecificationId = String.valueOf(hm
						.get("SPECIFICATION_ID"));

				// Check Source Id and Specification Id
				if (FormatHelper.hasContent(strSourceId)
						&& strSourceId.equalsIgnoreCase(String.valueOf(source
								.getBranchIdentifier()))
						&& FormatHelper.hasContent(strSpecificationId)
						&& strSpecificationId.equalsIgnoreCase(String
								.valueOf(primSpec.getBranchIdentifier()))) {
					// Get Image Page Name
					// BURBERRY-1420:BOM header Unique ID in CRUD Flag for BOM API Output - Start
					String strRemovedBOMName = (String) hm.get("BOM_NAME");
					String strRemovedBOMPartId = (String) hm.get("BOM_PART_ID");
					// Add to list
					removedBOMMap.put(strRemovedBOMPartId, strRemovedBOMName);
					// BURBERRY-1420:BOM header Unique ID in CRUD Flag for BOM API Output - End
				}
			}
		}

		// Remove Duplicate Palette Names
		//Set<String> hashSetRemoveDuplicate = new HashSet<String>();
		//hashSetRemoveDuplicate.addAll(removedBOMMap);
		//removedBOMMap.clear();
		//removedBOMMap.addAll(hashSetRemoveDuplicate);

		// Compare and Removed / Added Objects
		List<String> lstComparedAndRemovedObjects = BurberryAPIUtil
				.compareAndRemoveSameObjects(
						new ArrayList<String>(associatedBOMMap.keySet()), 
						new ArrayList<String>(removedBOMMap.keySet()));
		logger.debug(methodName + "List Removed Palette Names: "
				+ lstComparedAndRemovedObjects);

		// BURBERRY-1420: BOM header Unique ID in CRUD Flag for BOM API Output - Start
		// Loop through the complete collection of map criteria
		for (String strRemovedBOMPartId : lstComparedAndRemovedObjects) {
			logger.debug(methodName + "strRemovedBOMPartId: " + strRemovedBOMPartId);
			// Initialisation
			BOM removedBOMBean = new BOM();
			if(FormatHelper.hasContent(strRemovedBOMPartId)){
				String strRemovedBOMName = removedBOMMap.get(strRemovedBOMPartId);			
				BurberryProductAPIJsonDataUtil.getRemovedImagePageBean(
						removedBOMBean, BurProductBOMConstant.STR_BOM_NAME,
						strRemovedBOMName);
				BurberryProductAPIJsonDataUtil.getRemovedImagePageBean(
						removedBOMBean,
						BurProductBOMConstant.STR_BOM_HEADER_UNIQID,
						strRemovedBOMPartId);
				logger.debug(methodName + "RemovedBOMBean: " + removedBOMBean);
				// Add to the list
				lstRemovedBOMBean.add(removedBOMBean);
			}
		}
		//BURBERRY-1420:BOM header Unique ID in CRUD Flag for BOM API Output - End
		
		// Track execution time
		long remBOMEnd = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove BOM Transform End Time: ");
		logger.debug(methodName
				+ "Remove BOM Transform  Total Execution Time (ms): "
				+ (remBOMEnd - remBOMStart));
		return lstRemovedBOMBean;
	}

	// CR R26: Handle Remove BOM Customisation : END


	/**
	 * @param flexBOMPart
	 * @param seasonMaster
	 * @param skus
	 * @param colProdToBOMLinkIds
	 * @param deltaCriteria 
	 * @param bomDeltaDateMap 
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 */
	public static List<BOMLink> getListBomLinks(FlexBOMPart flexBOMPart,
			LCSSeasonMaster seasonMaster, Collection<LCSSKU> skus,
			Collection<String> colProdToBOMLinkIds, boolean deltaCriteria, Map bomDeltaDateMap) throws WTException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, IOException {

		String methodName = "getListBomLinks()";
		// Track execution time
		long lstBOMLinkStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"List BOM Link Start Time: ");
		List<BOMLink> lstBomLinks = new ArrayList<BOMLink>();
		logger.debug(methodName + " extracting links from BOM "
				+ flexBOMPart.getName());
		// get all available sections on BOM
		Collection<String> sections = flexBOMPart
				.getFlexType()
				.getAttribute(BurProductBOMConstant.SECTION)
				.getAttValueList()
				.getSelectableValues(
						com.lcs.wc.client.ClientContext.getContext()
								.getLocale(), true);
		for (String section : sections) {
			logger.debug(methodName + " Extracting BOMLinks for section "
					+ section);
			BOMLink bomLinkBean = BurberryProductBOMAPIJsonDataUtil
					.getBOMLinkBean(flexBOMPart, section, seasonMaster, skus,
							colProdToBOMLinkIds,deltaCriteria,bomDeltaDateMap);
			if (bomLinkBean != null) {
				// Add bom link bean to the list
				lstBomLinks.add(bomLinkBean);
			}
		}
		logger.debug(methodName + " list of bom link beans " + lstBomLinks);
		// Track execution time
		long lstBOMLinkEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"List BOM Link End Time: ");
		logger.debug(methodName + "List BOM Link Total Execution Time (ms): "
				+ (lstBOMLinkEndTime - lstBOMLinkStartTime));
		// Return list of BOM Link objects
		return lstBomLinks;
	}

	/**
	 * @param link
	 * @param childLinks
	 * @param seasonMaster
	 * @param skus
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 */
	public static List<ColourVariation> getListColourVariation(
			FlexBOMLink link, List<FlexObject> childLinks,
			LCSSeasonMaster seasonMaster, Collection<LCSSKU> skus)
			throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException {

		String methodName = "getListColourVariation()";
		// Track execution time
		long lstCVStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"List Colourway Variation Start Time: ");
		logger.debug(methodName+" skus associated to source "+skus);
		List<ColourVariation> lstClryVariation = new ArrayList();
		Collection<LCSSKU> skusAdded = new ArrayList();
		// Parse through ALL overrided child Links and update JSON
		for (FlexObject childBranch : childLinks) {
			FlexBOMLink childLink = (FlexBOMLink) LCSQuery
					.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:"
							+ childBranch.getData("FLEXBOMLINK.IDA2A2"));
			logger.debug(methodName + " child link from flex object "
					+ childLink);
			String dimId = childLink.getDimensionId();
			String sourceId = getIdfromDimensionId(dimId, "SC:");
			// CR # JIRA # - Material Colour Size Variation: Start
			String sizeId = getIdfromDimensionId(dimId, "SIZE1:");
			//Check if Source and Size Variation
			if (!FormatHelper.hasContent(sourceId) 
					&& !FormatHelper.hasContent(sizeId)){
				//Get the SKU
				LCSSKU sku = getSkuforChildLink(childLink, seasonMaster);
				// Check if the link is latest and not associated to source
				// variation
				if (isLatestBOMLink(childLink) && sku != null
						&& skus.contains(sku)) {
					logger.debug(methodName + " colourway variation for  "
							+ sku.getName());
					lstClryVariation.add(BurberryProductBOMAPIUtil
							.getColourVariation(link,childLink, sku));
					skusAdded.add(sku);
				}
			}
			// CR # JIRA # - Material Colour Size Variation: End			
		}
		// ADD parent link data to colour variations which are not over ridden
		for (LCSSKU sku : skus) {
			if (!skusAdded.contains(sku) && link.getColor() != null) {
				logger.debug(methodName + " colourway not added " + sku);
				lstClryVariation.add(BurberryProductBOMAPIUtil
						.getColourVariation(link,link, sku));
			}
		}

		logger.debug(methodName + " list of colourway variation beans "
				+ lstClryVariation);
		// Track execution time
		long lstCVLinkEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"List Colourway Variation End Time: ");
		logger.debug(methodName
				+ "List Colourway Variation Total Execution Time (ms): "
				+ (lstCVLinkEndTime - lstCVStartTime));

		// Return list of colourway variation beans
		return lstClryVariation;
	}

	/**
	 * @param childLink
	 * @return
	 */
	private static boolean isLatestBOMLink(FlexBOMLink childLink) {
		// Check on outdate and dropped for latest BOM Link
		return ( childLink.getOutDate() == null && !childLink
				.isDropped());
	}

	/**
	 * @param childLink
	 * @param seasonMaster
	 * @return
	 * @throws WTException
	 */
	private static LCSSKU getSkuforChildLink(FlexBOMLink childLink,
			LCSSeasonMaster seasonMaster) throws WTException {

		String methodName = "getSkuforChildLink()";
		String dimensionId = childLink.getDimensionId();
		String partMasterId = getIdfromDimensionId(dimensionId, "SKU:");
		if (FormatHelper.hasContent(partMasterId)) {
			LCSSKU sku = (LCSSKU) VersionHelper
					.getVersion((Persistable) LCSQuery
							.findObjectById(partMasterId),"A");
			LCSSKUSeasonLink ssl = (LCSSKUSeasonLink) LCSSeasonQuery
					.findSeasonProductLink((LCSPartMaster) sku.getMaster(),
							seasonMaster);
			if (ssl != null && ssl.isEffectLatest() && !ssl.isSeasonRemoved()) {
				// if link has colourway sourcing
				logger.debug(methodName + " colourway for variation "
						+ sku.getName());
				// returning Colourway associated
				return sku;
			}
		}
		return null;
	}

	/**
	 * @param dimId
	 * @param object
	 * @return
	 */
	private static String getIdfromDimensionId(String dimId, String object) {

		String methodName = "getIdfromDimensionId()";
		// If object is not available on dimension Id
		if (dimId.indexOf(object) < 0) {
			return "";
		}
		String stub = dimId.substring(dimId.indexOf(object) + object.length());
		if (stub.indexOf('-') > -1) {
			stub = stub.substring(0, stub.indexOf('-'));
		}

		logger.debug(methodName + " Id returned  " + stub);

		// Return dimension ID associated to object
		return stub.trim();
	}

}
