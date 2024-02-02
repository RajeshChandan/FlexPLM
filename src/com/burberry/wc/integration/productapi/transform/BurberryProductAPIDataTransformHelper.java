package com.burberry.wc.integration.productapi.transform;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.productapi.bean.*;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.*;
import com.lcs.wc.season.*;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
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
public final class BurberryProductAPIDataTransformHelper {

	/**
	 * BurberryProductAPIDataExtraction.
	 */
	private BurberryProductAPIDataTransformHelper() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductAPIDataTransformHelper.class);

	/**
	 * getStyleBean.
	 * 
	 * @param prod
	 *            LCSProduct
	 * @param colProdToSourceIds
	 *            Collection
	 * @param colProdToSKUSeasonLinkIds
	 *            Collection
	 * @param colProdToSKUIds
	 *            Collection
	 * @param colProdToSeasonLinkIds
	 *            Collection
	 * @param colProdToSeasonIds
	 *            Collection
	 * @param deltaCriteria
	 * @param mapTrackedImagePage
	 * @param mapTrackedRiskManagement
	 * @param mapTrackedImagePage2
	 * @param mapDeltaDateTime
	 * @return {@link Style}
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws NoSuchMethodException
	 * @throws PropertyVetoException
	 */
	public static Style getStyleBean(LCSProduct productObj,
			Collection<String> colProdToSeasonIds,
			Collection<String> colProdToSeasonLinkIds,
			Collection<String> colProdToSKUIds,
			Collection<String> colProdToSKUSeasonLinkIds,
			Collection<String> colProdToSourceIds, boolean deltaCriteria,
			Map<String, Collection<HashMap>> mapTrackedRiskManagement,
			Map<String, Collection<HashMap>> mapTrackedImagePage,
			Map<String, Collection<HashMap>> mapTrackedImageFromImagePage,
			Map<String, Collection<HashMap>> mapTrackedProdSpec,Map<String, Collection<HashMap>> mapTrackedDocuments)
			throws WTException, IllegalAccessException,
			InvocationTargetException, IOException, NoSuchMethodException,
			PropertyVetoException {

		String methodName = "getStyleBean()";
		long styleStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"styleStartTime: ");

		logger.debug(methodName + " Product : " + productObj.getName());
		Style styleBean = ObjectFactory.createProductAPIStyle();
		styleBean = BurberryProductAPIJsonDataUtil.getProductBean(productObj);

		// CR: Document: Initialisation
		List<Document> lstDocumentBeans = new ArrayList<Document>();
		// CR: Document Extracting list of document: Start
		lstDocumentBeans.addAll(BurberryProductAPIDataTransform
				.getListDocumentsBean(
						SeasonProductLocator.getProductARev(productObj), null));
		logger.debug(methodName + " list of Document " + lstDocumentBeans);

		// Extracting Main RM material Data
		LCSMaterial materialObj = (LCSMaterial) productObj
				.getValue(BurConstant.BUR_RM_MAIN);
		logger.debug(methodName + " material object being extracted "
				+ materialObj);
		// CR RD-022 JIRA 1368: Start
		if (materialObj != null) {
			styleBean.setMaterial(BurberryProductAPIJsonDataUtil
					.getMaterialBean(materialObj));
		}
		// CR RD-022 JIRA 1368: End
		
		// Extracting commodity code data
		LCSLifecycleManaged boCommodityCode = (LCSLifecycleManaged) productObj
				.getValue(BurConstant.BUR_CC);
		logger.debug(methodName + " Commodity code object being extracted "
				+ boCommodityCode);
		styleBean.setCommodityCode(BurberryProductAPIJsonDataUtil
				.getcommodityCodeBean(boCommodityCode));

		// Extracting colourways list data
		List<Colourway> lstColourway = getListColourwayBean(productObj,
				colProdToSKUIds, colProdToSKUSeasonLinkIds, colProdToSeasonIds,
				deltaCriteria);
		logger.debug(methodName + " list of colourway beans " + lstColourway);
		styleBean.setColourways(lstColourway);

		// Extracting product season list data
		List<ProductSeason> lstProductSeasons = getListProductSeasonBean(
				productObj, colProdToSeasonIds, colProdToSeasonLinkIds,
				deltaCriteria, lstDocumentBeans);
		logger.debug(methodName + " list of Product Season beans "
				+ lstProductSeasons);
		styleBean.setProductSeason(lstProductSeasons);

		// CR R26: Handle Remove Object Customisation: Start
		// Extracting source list data
		List<Source> lstSources = getListSourceBean(productObj,
				colProdToSourceIds, colProdToSeasonIds, colProdToSeasonLinkIds,
				deltaCriteria, mapTrackedImagePage,
				mapTrackedImageFromImagePage, mapTrackedProdSpec, mapTrackedDocuments);
		logger.debug(methodName + " list of source beans " + lstSources);
		styleBean.setSource(lstSources);

		// Extracting RiskManagement list data
		List<RiskManagement> lstRiskMgt = getListRiskMgmtBean(productObj,
				mapTrackedRiskManagement);
		logger.debug(methodName + " list of Risk Management beans " + lstRiskMgt);
		styleBean.setRiskManagement(lstRiskMgt);
		// CR R26: Handle Remove Object Customisation: End

		// Set Documents Bean to Style
		styleBean.setDocuments(lstDocumentBeans);
		// CR: Document Extracting list of document: End

		logger.debug(methodName + " style bean " + styleBean);

		long styleEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"styleEndTime: ");
		logger.debug(methodName + "Style Transform  Total Execution Time (ms): "
				+ (styleEndTime - styleStartTime));
		return styleBean;
	}

	/**
	 * getListRiskMgmtBean.
	 * 
	 * @param productObj
	 *            LCSProduct
	 * @param mapTrackedRiskManagement
	 * @param endModifyDate
	 * @param startModifyDate
	 * @return List RiskManagement
	 * @throws WTException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	private static List<RiskManagement> getListRiskMgmtBean(
			LCSProduct productObj,
			Map<String, Collection<HashMap>> mapTrackedRiskManagement)
			throws WTException, IllegalAccessException,
			InvocationTargetException, IOException, NoSuchMethodException {

		Collection<LCSMOAObject> riskManagements;
		String methodName = "getListRiskMgmtBean() ";
		long rmStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"rmStartTime: ");

		logger.debug(methodName + "Risk Management: Flex Type = "
				+ productObj.getFlexType().getFullName(true));

		// Check if product object belongs to Apparel/Accessories childrens type
		if (productObj.getFlexType().getFullName(true)
				.equalsIgnoreCase(BurConstant.APPAREL_CHILDREN_TYPE)
				|| productObj
						.getFlexType()
						.getFullName(true)
						.equalsIgnoreCase(BurConstant.ACCESSORIES_CHILDREN_TYPE)) {
			// getting risk managemenet MOA objects from product
			riskManagements = LCSMOAObjectQuery.findMOACollection(
					productObj,
					FlexTypeCache.getFlexType(productObj).getAttribute(
							BurConstant.RM_CHILDREN_KEY));
			// Defect Fix:Start
			// getting risk managemenet MOA objects from product
			riskManagements.addAll(LCSMOAObjectQuery.findMOACollection(
					productObj, FlexTypeCache.getFlexType(productObj)
							.getAttribute(BurConstant.RM_ALL_KEY)));
			// Defect Fix: End
		}
		// check if product object belongs to footwear Flextype
		else if (productObj.getFlexType().getFullName(true)
				.contains(BurConstant.FOOTWEAR_TYPE)) {
			// getting risk managemenet MOA objects from product
			riskManagements = LCSMOAObjectQuery.findMOACollection(
					productObj,
					FlexTypeCache.getFlexType(productObj).getAttribute(
							BurConstant.RM_FOOTWEAR_KEY));
		}
		// if product object belongs to any other Flextype
		else {
			// getting risk managemenet MOA objects from product
			riskManagements = LCSMOAObjectQuery.findMOACollection(
					productObj,
					FlexTypeCache.getFlexType(productObj).getAttribute(
							BurConstant.RM_ALL_KEY));
		}
		logger.debug(methodName + "riskManagement " + riskManagements);
		ArrayList<RiskManagement> listRiskManagement = new ArrayList<RiskManagement>();
		for (LCSMOAObject riskmgt : riskManagements) {
			logger.debug(methodName + " risk management object " + riskmgt);
			RiskManagement riskBean = ObjectFactory
					.createProductAPIStyleRiskManagement();
			// Extract details from each Risk Management Object
			riskBean = BurberryProductAPIJsonDataUtil
					.getRiskManagementBean(riskmgt);
			logger.debug(methodName + " Risk Management Bean " + riskBean);
			listRiskManagement.add(riskBean);

		}
		logger.debug(methodName + " list of risk management beans "
				+ listRiskManagement);

		// CR R26: Handle Remove Object Customisation: Start
		List<RiskManagement> lstRemovedRiskManagement = getRemovedListRiskManagement(
				productObj, mapTrackedRiskManagement);
		logger.debug(methodName + "List of Removed Risk Management: "
				+ lstRemovedRiskManagement);
		listRiskManagement.addAll(lstRemovedRiskManagement);
		// CR R26: Handle Remove Object Customisation: End

		long rmEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"rmEndTime: ");
		logger.debug(methodName
				+ "Risk Management Transform  Total Execution Time (ms): "
				+ (rmEndTime - rmStartTime));
		return listRiskManagement;

	}

	/**
	 * getListSourceBean.
	 * 
	 * @param productObj
	 *            LCSProduct
	 * @param colProdToSourceIds
	 *            Collection
	 * @param deltaCriteria
	 * @param mapTrackedImagePage
	 * @param mapTrackedImageFromImagePage
	 * @param endDate
	 * @param startDate
	 * @param mapDeltaDateTime
	 * @return {@link List} List
	 * @throws WTException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 * @throws PropertyVetoException
	 */
	private static List<Source> getListSourceBean(LCSProduct productObj,
			Collection<String> colProdToSourceIds,
			Collection<String> colProdToSeasonIds,
			Collection<String> colProdToSeasonLinkIds, boolean deltaCriteria,
			Map<String, Collection<HashMap>> mapTrackedImagePage,
			Map<String, Collection<HashMap>> mapTrackedImageFromImagePage,
			Map<String, Collection<HashMap>> mapTrackedProdSpec,Map<String, Collection<HashMap>> mapTrackedDocuments)
			throws WTException, IllegalAccessException,
			InvocationTargetException, IOException, NoSuchMethodException,
			PropertyVetoException {

		String methodName = "getListSourceBean()";
		long sourceStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"sourceStartTime: ");

		Collection<LCSSourcingConfig> sources = LCSSourcingConfigQuery
				.getSourcingConfigsForProduct(productObj);
		logger.debug(methodName + "sources " + sources);
		ArrayList<Source> listSource = new ArrayList<Source>();
		// Checking through each sourcing configuration under project
		for (LCSSourcingConfig source : sources) {
			// checking if source criteria is given in URL and validating if
			// source object satisfies the URL criteria
			boolean idExists = BurberryAPIDBUtil.checkIfObjectExists(
					String.valueOf(source.getBranchIdentifier()),
					colProdToSourceIds);
			if (idExists) {
				source = (LCSSourcingConfig) VersionHelper
						.latestIterationOf(source);
				logger.debug(methodName + "Extracting data from source "
						+ source.getName());
				Source sourceBean = ObjectFactory.createProductAPIStyleSource();
				// Extraction of Source Object data
				sourceBean = BurberryProductAPIJsonDataUtil.getSourceBean(
						source, productObj);

				sourceBean.setSourceSeason(BurberryProductAPIDataTransform
						.getListSourceSeason(productObj, source,
								colProdToSeasonIds, colProdToSeasonLinkIds,
								deltaCriteria, mapTrackedImagePage,
								mapTrackedImageFromImagePage,
								mapTrackedProdSpec,mapTrackedDocuments));

				logger.debug(methodName + " source bean " + sourceBean);
				listSource.add(sourceBean);
			}
		}
		logger.debug(methodName + " list of source beans " + listSource);
		long sourceEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"sourceEndTime: ");
		logger.debug(methodName
				+ "Source Transform  Total Execution Time (ms): "
				+ (sourceEndTime - sourceStartTime));
		return listSource;
	}

	/**
	 * getListColourwayBean.
	 * 
	 * @param productObj
	 *            LCSProduct
	 * @param colProdToSKUSeasonLinkIds
	 *            Collection
	 * @param colProdToSKUIds
	 *            Collection
	 * @param colProdToSeasonIds
	 *            Collection
	 * @param deltaCriteria
	 * @return {@link List} List
	 * @throws WTException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 */
	private static List<Colourway> getListColourwayBean(LCSProduct productObj,
			Collection<String> colProdToSKUIds,
			Collection<String> colProdToSKUSeasonLinkIds,
			Collection<String> colProdToSeasonIds, boolean deltaCriteria)
			throws WTException, IllegalAccessException,
			InvocationTargetException, IOException, NoSuchMethodException {

		String methodName = "getListColourwayBean()";
		long skuStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"skuStartTime: ");
		Collection<LCSSKU> skus = LCSSKUQuery.findSKUs(productObj);
		ArrayList<Colourway> listcolourways = new ArrayList<Colourway>();
		// checking through each colourway under product
		for (LCSSKU sku : skus) {
			// checking if colourway criteria is given in URL and validating if
			// colourway object satisfies the URL criteria
			boolean idExists = BurberryAPIDBUtil.checkIfObjectExists(
					String.valueOf(sku.getBranchIdentifier()), colProdToSKUIds);
			if (idExists) {
				sku = (LCSSKU) VersionHelper.latestIterationOf(sku);
				logger.debug(methodName + " Extracting data from colourway "
						+ sku.getName());
				Colourway colourwayBean = ObjectFactory
						.createProductAPIStyleColourway();
				// Extraction of Colourway Object data
				colourwayBean = BurberryProductAPIJsonDataUtil
						.getColourwayBean(sku);
				// getting all the colourway season links associated to
				// colourway
				List<ColourwaySeason> lstcolourwayseason = getListColourwayseasonBean(
						sku, colProdToSKUSeasonLinkIds, colProdToSeasonIds,
						deltaCriteria);
				logger.debug(methodName + " list of colourway season beans "
						+ lstcolourwayseason);
				colourwayBean.setColourwaySeason(lstcolourwayseason);
				logger.debug(methodName + " colourway bean " + sku.getName());
				listcolourways.add(colourwayBean);

			}

		}
		logger.debug(methodName + " list of colourway beans " + listcolourways);

		long skuEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"skuEndTime: ");
		logger.debug(methodName
				+ "Colourways Transform  Total Execution Time (ms): "
				+ (skuEndTime - skuStartTime));
		return listcolourways;

	}

	/**
	 * getListColourwayseasonBean
	 * 
	 * @param sku
	 *            LCSSKU
	 * @param colProdToSKUSeasonLinkIds
	 *            Collection
	 * @param deltaCriteria
	 * @param colProdToSKUIds
	 *            Collection
	 * @return {@link List} List
	 * @throws WTException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 */
	private static List<ColourwaySeason> getListColourwayseasonBean(LCSSKU sku,
			Collection<String> colProdToSKUSeasonLinkIds,
			Collection<String> colProdToSeasonIds, boolean deltaCriteria)
			throws WTException, IllegalAccessException,
			InvocationTargetException, IOException, NoSuchMethodException {

		String methodName = "getListColourwayseasonBean()";
		long skuSeasonStart = BurberryAPIUtil.printCurrentTime(methodName,
				"skuSeasonStart: ");
		Collection<LCSSKUSeasonLink> skuseasonLinks = new LCSSeasonQuery()
				.findSeasonProductLinks((LCSPartMaster) sku.getMaster());
		logger.debug(methodName + "skuseasonlinks " + skuseasonLinks);
		ArrayList<ColourwaySeason> listcolourwayseason = new ArrayList<ColourwaySeason>();

		// checking through each colourway season link for a colourway object
		for (LCSSKUSeasonLink skuseasonLink : skuseasonLinks) {
			if (skuseasonLink.isEffectLatest()
					&& (!skuseasonLink.isSeasonRemoved() || deltaCriteria)) {
				// checking if colourway season criteria is given in URL and
				// validating if colourway season object satisfies the URL
				// criteria
				boolean idExists = BurberryAPIDBUtil.checkIfObjectExists(
						FormatHelper
								.getNumericObjectIdFromObject(skuseasonLink),
						colProdToSKUSeasonLinkIds);
				// checking if season criteria is given in URL and validating if
				// season object satisfies the URL criteria
				boolean checkIfSeasonIdExist = BurberryAPIDBUtil
						.checkIfObjectExists(String
								.valueOf(((Double) skuseasonLink
										.getSeasonRevId()).intValue()),
								colProdToSeasonIds);
				if (idExists && checkIfSeasonIdExist) {
					logger.debug(methodName
							+ "Extracting data from colourway season "
							+ skuseasonLink);
					ColourwaySeason colourwayseasonBean = ObjectFactory
							.createProductAPIStyleColourwayColourwaySeason();
					// Season Object
					LCSSeason colourSeason = (LCSSeason) VersionHelper
							.latestIterationOf(skuseasonLink.getSeasonMaster());
					String skuSeasonName = (String) colourSeason
							.getValue(BurConstant.SEASON_NAM);

					// Extraction of Colourway season Object data
					colourwayseasonBean = BurberryProductAPIJsonDataUtil
							.getColourwaySeasonBean(skuseasonLink,
									skuSeasonName);
					logger.debug(methodName + " colourway season bean "
							+ colourwayseasonBean);
					listcolourwayseason.add(colourwayseasonBean);
				}
			}

		}
		logger.debug(methodName + " list of colourway season bean "
				+ listcolourwayseason);
		long skuSeasonEnd = BurberryAPIUtil.printCurrentTime(methodName,
				"skuSeasonEnd: ");
		logger.debug(methodName
				+ "Colourway Season Transform  Total Execution Time (ms): "
				+ (skuSeasonEnd - skuSeasonStart));
		return listcolourwayseason;
	}

	/**
	 * getListProductSeasonBean.
	 * 
	 * @param productObj
	 *            LCSProduct
	 * @param colProdToSeasonLinkIds
	 *            Collection
	 * @param colProdToSeasonIds
	 *            Collection
	 * @param deltaCriteria
	 * @param lstDocumentBeans
	 * @return {@link List} LIST
	 * @throws WTException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 * @throws PropertyVetoException 
	 */
	private static List<ProductSeason> getListProductSeasonBean(
			LCSProduct productObj, Collection<String> colProdToSeasonIds,
			Collection<String> colProdToSeasonLinkIds, boolean deltaCriteria,
			List<Document> lstDocumentBeans) throws WTException,
			IllegalAccessException, InvocationTargetException, IOException,
			NoSuchMethodException, PropertyVetoException {

		String methodName = "getListProductSeasonBean()";

		long productSeasonStart = BurberryAPIUtil.printCurrentTime(methodName,
				"productSeasonStart: ");
		Collection<LCSProductSeasonLink> productSeasonLinks = new LCSSeasonQuery()
				.findSeasonProductLinks(productObj);
		logger.debug(methodName + " productseasonlinks " + productSeasonLinks);
		ArrayList<ProductSeason> listProductSeason = new ArrayList<ProductSeason>();
		// checking through each product season object under product
		for (LCSProductSeasonLink spl : productSeasonLinks) {
			if (spl.isEffectLatest()
					&& ((!spl.isSeasonRemoved()) || deltaCriteria)) {
				// checking if product season criteria is given in URL and
				// validating if product season object satisfies the URL
				// criteria
				boolean idExists = BurberryAPIDBUtil.checkIfObjectExists(
						FormatHelper.getNumericObjectIdFromObject(spl),
						colProdToSeasonLinkIds);
				// checking if season criteria is given in URL and validating if
				// season object satisfies the URL criteria
				boolean checkIfSeasonIdExist = BurberryAPIDBUtil
						.checkIfObjectExists(String.valueOf(((Double) spl
								.getSeasonRevId()).intValue()),
								colProdToSeasonIds);
				if (idExists && checkIfSeasonIdExist) {
					logger.debug(methodName
							+ "Extracting data from productseasonlink " + spl);
					ProductSeason productSeasonBean = ObjectFactory
							.createProductAPIStyleProductSeason();
					Season seasonBean = ObjectFactory
							.createProductAPIStyleProductSeasonSeason();
					// Extraction of Season Object data
					seasonBean = BurberryProductAPIJsonDataUtil
							.getSeasonBean(spl);
					logger.debug(methodName + " season bean " + seasonBean);
					// Extraction of product season object
					productSeasonBean = BurberryProductAPIJsonDataUtil
							.getProductSeasonBean(spl);
					logger.debug(methodName + " product season bean "
							+ productSeasonBean);
					// Setting season bean on product season object
					productSeasonBean.setSeason(seasonBean);
					listProductSeason.add(productSeasonBean);

					// CR: Document: Start
					LCSProduct productFromSeason = (LCSProduct) LCSProductQuery
							.findObjectById("VR:com.lcs.wc.product.LCSProduct:"
									+ Double.valueOf(
											spl.getProductSeasonRevId())
											.longValue());
					// Season Object
					LCSSeason docSeason = (LCSSeason) VersionHelper
							.latestIterationOf(spl.getSeasonMaster());
					String docSeasonName = (String) docSeason
							.getValue(BurConstant.SEASON_NAM);
					logger.debug(methodName + "Document Season Name: "
							+ docSeasonName);
					lstDocumentBeans.addAll(BurberryProductAPIDataTransform
							.getListDocumentsBean(productFromSeason,
									docSeasonName));
					logger.debug(methodName + "Product Season Documents Bean: "
							+ lstDocumentBeans);
					// CR: Document: End
				}
			}
		}
		logger.debug(methodName + " list of product season beans "
				+ listProductSeason);
		long productSeasonEnd = BurberryAPIUtil.printCurrentTime(methodName,
				"productSeasonEnd: ");
		logger.debug(methodName
				+ "Product Season Transform  Total Execution Time (ms): "
				+ (productSeasonEnd - productSeasonStart));
		return listProductSeason;
	}

	/**
	 * Method to get removed risk management objects
	 * 
	 * @param productObject
	 *            LCSProduct
	 * @param mapTrackedRiskManagement
	 * @param startModifyDate
	 *            Date
	 * @param endModifyDate
	 *            Date
	 * @return List<RiskManagement>
	 * @throws WTException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 */
	// CR R26: Handle Remove Object Customisation: Start
	private static List<RiskManagement> getRemovedListRiskManagement(
			LCSProduct productObject,
			Map<String, Collection<HashMap>> mapTrackedRiskManagement)
			throws WTException, IllegalAccessException,
			InvocationTargetException {

		// Method Name
		String methodName = "getRemovedListRiskManagement() ";
		// Track execution time
		long remRiskMgmtStart = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove Risk Management Transform Start Time: ");

		// Initialisation
		List<RiskManagement> lstRemovedRiskMgmtBean = new ArrayList<RiskManagement>();

		// Check tracked map contains product id
		if (mapTrackedRiskManagement.containsKey(String.valueOf(productObject
				.getBranchIdentifier()))) {
			// Get the collection for this material
			Collection<HashMap> colMap = mapTrackedRiskManagement.get(String
					.valueOf(productObject.getBranchIdentifier()));
			// Loop through the collection
			for (HashMap hm : colMap) {
				// Get the removed palette name
				String removedMOARow = (String) hm.get("MOA_OBJECT_ID");
				RiskManagement removedRiskManagementBean = new RiskManagement();
				BurberryPaletteMaterialAPIJsonDataUtil
						.getRemovedMOABean(
								removedRiskManagementBean,
								BurPaletteMaterialConstant.RISK_MANAGEMENT_JSON_UNIQUE_ID,
								removedMOARow);
				logger.debug(methodName + "Removed MOA Bean: "
						+ removedRiskManagementBean);
				// Add to list
				lstRemovedRiskMgmtBean.add(removedRiskManagementBean);
			}
		}

		logger.debug(methodName + "Removed Risk Management Bean Size: "
				+ lstRemovedRiskMgmtBean.size());

		// Track execution time
		long remRiskMgmtEnd = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove Risk Management Transform End Time: ");
		logger.debug(methodName
				+ "Remove Risk Management Transform Total Execution Time (ms): "
				+ (remRiskMgmtEnd - remRiskMgmtStart));

		return lstRemovedRiskMgmtBean;
	}
	// CR R26: Handle Remove Object Customisation: End

}
