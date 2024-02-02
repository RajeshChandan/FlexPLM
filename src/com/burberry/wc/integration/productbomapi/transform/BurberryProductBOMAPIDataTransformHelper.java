package com.burberry.wc.integration.productbomapi.transform;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.productbomapi.bean.*;
import com.burberry.wc.integration.productbomapi.bean.Source;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.LCSSKUQuery;
import com.lcs.wc.product.ProductHeaderQuery;
import com.lcs.wc.season.*;
import com.lcs.wc.sourcing.*;
import com.lcs.wc.specification.FlexSpecToSeasonLink;
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
public final class BurberryProductBOMAPIDataTransformHelper {

	/**
	 * BurberryProductAPIDataExtraction.
	 */
	private BurberryProductBOMAPIDataTransformHelper() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductBOMAPIDataTransformHelper.class);

	/**
	 * @param productObj
	 * @param colProdToSeasonIds
	 * @param colProdToSeasonLinkIds
	 * @param colProdToSKUIds
	 * @param colProdToSKUSeasonLinkIds
	 * @param colProdToBOMIds
	 * @param colProdToBOMLinkIds
	 * @param colProdToSourceIds
	 * @param deltaCriteria
	 * @param bomDeltaDateMap 
	 * @return
	 * @throws IOException
	 * @throws WTException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws PropertyVetoException
	 */
	public static Style getStyleBean(LCSProduct productObj,
			Collection<String> colProdToSeasonIds,
			Collection<String> colProdToSeasonLinkIds,
			Collection<String> colProdToSKUIds,
			Collection<String> colProdToSKUSeasonLinkIds,
			Collection<String> colProdToBOMIds,
			Collection<String> colProdToBOMLinkIds,
			Collection<String> colProdToSourceIds,
			Map<String, Collection<HashMap>> mapTrackedBOM,
			boolean deltaCriteria, Map bomDeltaDateMap) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException, PropertyVetoException {
		String methodName = "getStyleBean()";
		long styleStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"styleStartTime: ");

		logger.debug(methodName + " Product : " + productObj.getName());
		Style styleBean = BurberryProductBOMAPIStyleUtil
				.getProductBean(productObj);

		// Extracting colourways list data
		List<Colourway> lstColourway = getListColourwayBean(productObj,
				colProdToSKUIds, colProdToSKUSeasonLinkIds, colProdToSeasonIds,
				deltaCriteria);
		logger.debug(methodName + " list of colourway beans " + lstColourway);
		styleBean.setColourways(lstColourway);

		// Extracting product season list data
		List<ProductSeason> lstProductSeasons = getListProductSeasonBean(
				productObj, colProdToSeasonIds, colProdToSeasonLinkIds,
				deltaCriteria, colProdToBOMIds, colProdToBOMLinkIds,
				colProdToSourceIds, mapTrackedBOM,bomDeltaDateMap);
		logger.debug(methodName + " list of Product Season beans "
				+ lstProductSeasons);
		styleBean.setProductSeason(lstProductSeasons);

		// Track execution time
		long styleEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Style End Time: ");
		logger.debug(methodName + "Style Transform  Total Execution Time (ms): "
				+ (styleEndTime - styleStartTime));
		// Return style bean
		return styleBean;

	}

	/**
	 * @param productObj
	 * @param bomColProdToSeasonIds
	 * @param colProdToSeasonLinkIds
	 * @param deltaCriteria
	 * @param colProdToBOMIds
	 * @param colProdToBOMLinkIds
	 * @param colProdToSourceIds
	 * @param bomDeltaDateMap 
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 * @throws PropertyVetoException
	 */
	private static List<ProductSeason> getListProductSeasonBean(
			LCSProduct productObj, Collection<String> bomColProdToSeasonIds,
			Collection<String> bomColProdToSeasonLinkIds,
			boolean deltaCriteria, Collection<String> colProdToBOMIds,
			Collection<String> colProdToBOMLinkIds,
			Collection<String> colProdToSourceIds,
			Map<String, Collection<HashMap>> mapTrackedBOM, Map bomDeltaDateMap) throws WTException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, IOException, PropertyVetoException {
		String methodName = "getListProductSeasonBean()";

		long productSeasonStart = BurberryAPIUtil.printCurrentTime(methodName,
				"BOM productSeasonStart: ");
		Collection<LCSProductSeasonLink> bomProductSeasonLinks = new LCSSeasonQuery()
				.findSeasonProductLinks(productObj);
		logger.debug(methodName + " BOM productseasonlinks "
				+ bomProductSeasonLinks);
		ArrayList<ProductSeason> listProductSeason = new ArrayList<ProductSeason>();
		// checking through each product season object under product
		for (LCSProductSeasonLink bomSpl : bomProductSeasonLinks) {
			if (bomSpl.isEffectLatest()
					&& ((!bomSpl.isSeasonRemoved()) || deltaCriteria)) {
				// checking if product season criteria is given in URL and
				// validating if product season object satisfies the URL
				// criteria
				boolean bomIdExists = BurberryAPIDBUtil.checkIfObjectExists(
						FormatHelper.getNumericObjectIdFromObject(bomSpl),
						bomColProdToSeasonLinkIds);
				// checking if season criteria is given in URL and validating if
				// season object satisfies the URL criteria
				boolean bomSeasonIdExists = BurberryAPIDBUtil
						.checkIfObjectExists(String.valueOf(((Double) bomSpl
								.getSeasonRevId()).intValue()),
								bomColProdToSeasonIds);
				if (bomIdExists && bomSeasonIdExists) {
					logger.debug(methodName
							+ "Extracting data from BOM productseasonlink "
							+ bomSpl);
					// Extraction of Season Object data

					// Extraction of product season object
					ProductSeason productSeasonBean = BurberryProductBOMAPIStyleUtil
							.getProductSeasonBean(bomSpl);
					logger.debug(methodName + " BOM product season bean "
							+ productSeasonBean);

					List<Source> lstSourceBeans = getListSourceBean(
							bomSpl.getSeasonMaster(), productObj,
							colProdToBOMIds, colProdToBOMLinkIds,
							colProdToSourceIds, mapTrackedBOM, deltaCriteria,bomDeltaDateMap);
					validateProductSeason(bomSpl, productSeasonBean,
							lstSourceBeans, listProductSeason);
					logger.debug(methodName + " product season beans "
							+ productSeasonBean);
				}
			}

		}

		logger.debug(methodName + " list of BOM product season beans "
				+ listProductSeason);
		long productSeasonEnd = BurberryAPIUtil.printCurrentTime(methodName,
				"BOM productSeasonEnd: ");
		// Total execution time for product season
		logger.debug(methodName
				+ "BOM Product Season Transform  Total Execution Time (ms): "
				+ (productSeasonEnd - productSeasonStart));
		// Returning list of product season objects
		return listProductSeason;
	}

	/**
	 * @param bomSpl
	 * @param productSeasonBean
	 * @param lstSourceBeans
	 * @param listProductSeason
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	private static void validateProductSeason(LCSProductSeasonLink bomSpl,
			ProductSeason productSeasonBean, List<Source> lstSourceBeans,
			List<ProductSeason> listProductSeason)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		
		String methodName = "validateProductSeason() ";
		if (!lstSourceBeans.isEmpty()) {
			Season seasonBean = BurberryProductBOMAPIStyleUtil
					.getSeasonBean(bomSpl);
			logger.debug(methodName + " BOM season bean " + seasonBean);
			// Setting season bean on product season object
			productSeasonBean.setSeason(seasonBean);
			productSeasonBean.setSource(lstSourceBeans);
			//Add product season to list
			listProductSeason.add(productSeasonBean);
		}

	}

	/**
	 * @param productObj
	 * @param colProdToSKUIds
	 * @param colProdToSKUSeasonLinkIds
	 * @param colProdToSeasonIds
	 * @param deltaCriteria
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 */
	private static List<Colourway> getListColourwayBean(LCSProduct productObj,
			Collection<String> colProdToSKUIds,
			Collection<String> colProdToSKUSeasonLinkIds,
			Collection<String> colProdToSeasonIds, boolean deltaCriteria)
			throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException {
		String methodName = "getListColourwayBean()";
		long skuStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"BOM skuStartTime: ");
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
				logger.debug(methodName
						+ " BOM Extracting data from colourway "
						+ sku.getName());
				// Extraction of Colourway Object data
				Colourway colourwayBean = BurberryProductBOMAPIStyleUtil
						.getColourwayBean(sku);
				// getting all the colourway season links associated to
				// colourway
				List<ColourwaySeason> lstcolourwayseason = getListColourwayseasonBeanBOM(
						sku, colProdToSKUSeasonLinkIds, colProdToSeasonIds,
						deltaCriteria);
				logger.debug(methodName
						+ " BOM list of colourway season beans "
						+ lstcolourwayseason);
				// setting colourway season beans list on colourway bean
				if (!lstcolourwayseason.isEmpty()) {
					colourwayBean.setColourwaySeason(lstcolourwayseason);
					logger.debug(methodName + "  BOM colourway bean "
							+ sku.getName());
				}
				listcolourways.add(colourwayBean);

			}

		}
		logger.debug(methodName + " BOM list of colourway beans "
				+ listcolourways);

		long skuEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"BOM skuEndTime: ");
		// Execution time to extract colourways
		logger.debug(methodName
				+ "BOM Colourways Transform  Total Execution Time (ms): "
				+ (skuEndTime - skuStartTime));

		// Return list of colourway objects
		return listcolourways;

	}

	/**
	 * @param sku
	 * @param bomColProdToSKUSeasonLinkIds
	 * @param bomColProdToSeasonIds
	 * @param deltaCriteria
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 */
	private static List<ColourwaySeason> getListColourwayseasonBeanBOM(
			LCSSKU sku, Collection<String> bomColProdToSKUSeasonLinkIds,
			Collection<String> bomColProdToSeasonIds, boolean deltaCriteria)
			throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException {
		String methodName = "getListColourwayseasonBean()";
		long skuSeasonStart = BurberryAPIUtil.printCurrentTime(methodName,
				"BOM skuSeasonStart: ");
		Collection<LCSSKUSeasonLink> bomSkuseasonLinks = new LCSSeasonQuery()
				.findSeasonProductLinks((LCSPartMaster) sku.getMaster());
		logger.debug(methodName + "BOM skuseasonlinks " + bomSkuseasonLinks);
		ArrayList<ColourwaySeason> listcolourwayseason = new ArrayList<ColourwaySeason>();
		// checking through each colourway season link for a colourway object
		for (LCSSKUSeasonLink bomSkuseasonLink : bomSkuseasonLinks) {
			if (bomSkuseasonLink.isEffectLatest()
					&& (!bomSkuseasonLink.isSeasonRemoved() || deltaCriteria)) {
				// checking if colourway season criteria is given in URL and
				// validating if colourway season object satisfies the URL
				// criteria
				boolean bomIdExists = BurberryAPIDBUtil
						.checkIfObjectExists(
								FormatHelper
										.getNumericObjectIdFromObject(bomSkuseasonLink),
								bomColProdToSKUSeasonLinkIds);
				// checking if season criteria is given in URL and validating if
				// season object satisfies the URL criteria
				boolean bomSeasonIdExists = BurberryAPIDBUtil
						.checkIfObjectExists(String
								.valueOf(((Double) bomSkuseasonLink
										.getSeasonRevId()).intValue()),
								bomColProdToSeasonIds);
				if (bomIdExists && bomSeasonIdExists) {
					logger.debug(methodName
							+ "Extracting data from BOM colourway season "
							+ bomSkuseasonLink);
					// Season Object
					LCSSeason colourSeason = (LCSSeason) VersionHelper
							.latestIterationOf(bomSkuseasonLink
									.getSeasonMaster());
					String skuSeasonName = (String) colourSeason
							.getValue(BurConstant.SEASON_NAM);
					// Extraction of Colourway season Object data
					ColourwaySeason colourwayseasonBean = BurberryProductBOMAPIStyleUtil
							.getColourwaySeasonBean(bomSkuseasonLink,
									skuSeasonName);
					logger.debug(methodName + " BOM colourway season bean "
							+ colourwayseasonBean);
					listcolourwayseason.add(colourwayseasonBean);
				}
			}

		}
		logger.debug(methodName + " list of BOM colourway season bean "
				+ listcolourwayseason);
		long skuSeasonEnd = BurberryAPIUtil.printCurrentTime(methodName,
				"BOM skuSeasonEnd: ");
		logger.debug(methodName
				+ "BOM Colourway Season Transform  Total Execution Time (ms): "
				+ (skuSeasonEnd - skuSeasonStart));
		// Return list of colouyrway source beans
		return listcolourwayseason;
	}

	/**
	 * @param seasonMaster
	 * @param productObj
	 * @param colProdToBOMIds
	 * @param colProdToBOMLinkIds
	 * @param colProdToSourceIds
	 * @param mapTrackedProdSpec
	 * @param mapTrackedBOM
	 * @param deltaCriteria
	 * @param bomDeltaDateMap 
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 * @throws PropertyVetoException
	 */
	public static List<Source> getListSourceBean(LCSSeasonMaster seasonMaster,
			LCSProduct productObj, Collection<String> colProdToBOMIds,
			Collection<String> colProdToBOMLinkIds,
			Collection<String> colProdToSourceIds,
			Map<String, Collection<HashMap>> mapTrackedBOM,
			boolean deltaCriteria, Map bomDeltaDateMap) throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException,
			PropertyVetoException {
		String methodName = "getListSourceBean()";
		/*
		 * get source of product
		 */
		Collection<LCSSourcingConfig> sourcesCol = LCSSourcingConfigQuery
				.getSourcingConfigForProductSeason(
						(LCSPartMaster) productObj.getMaster(), seasonMaster);
		logger.debug(methodName + "sources " + sourcesCol);
		ArrayList<Source> sourceBeanList = new ArrayList<Source>();
		// Checking through each sourcing configuration under project
		for (LCSSourcingConfig sourcingConfig : sourcesCol) {
			// checking if source criteria is given in URL and validating if
			// source object satisfies the URL criteria
			boolean idExists = BurberryAPIDBUtil.checkIfObjectExists(
					String.valueOf(sourcingConfig.getBranchIdentifier()),
					colProdToSourceIds);
			if (idExists) {
				LCSSourcingConfig source = (LCSSourcingConfig) VersionHelper
						.latestIterationOf(sourcingConfig);
				LCSSeason season = (LCSSeason) VersionHelper
						.latestIterationOf(seasonMaster);
				ProductHeaderQuery phq = new ProductHeaderQuery();
				Collection<LCSSKU> skus = phq.findSKUs(productObj, source,
						season, true);
				// Get Source Bean
				Source sourceBean = BurberryProductBOMAPIStyleUtil
						.getSourceBean(source, seasonMaster, productObj,
								colProdToBOMIds, colProdToBOMLinkIds,
								mapTrackedBOM);
				Collection<FlexSpecToSeasonLink> colSpecToSeasonLinks = BurberryAPIUtil
						.getAllSpecToSeasonLinks(source, seasonMaster,
								productObj);
				List<Specification> lstSpecification = new ArrayList<Specification>();
				for (FlexSpecToSeasonLink specToSeasonLink : colSpecToSeasonLinks) {
					Specification spec = getSpecificationBean(productObj,
							source, specToSeasonLink, skus, colProdToBOMIds,
							colProdToBOMLinkIds, mapTrackedBOM, deltaCriteria,bomDeltaDateMap);
					if (spec.getBOM() != null && !spec.getBOM().isEmpty()) {
						lstSpecification.add(spec);
					}
				}
				if (!lstSpecification.isEmpty()) {
					sourceBean.setSpecification(lstSpecification);
					sourceBeanList.add(sourceBean);
				}
			}
		}
		logger.debug(methodName + " Product Source Bean List " + sourceBeanList);
		return sourceBeanList;
	}

	/**
	 * Method to get primary spec.
	 * 
	 * @param productObj
	 *            LCSProduct
	 * @param source
	 *            LCSSourcingConfig
	 * @param specToSeasonLink
	 *            FlexSpecification
	 * @param deltaCriteria
	 * @param bomDeltaDateMap 
	 * @param mapTrackedImagePage
	 * @param mapTrackedImageFromImagePage
	 * @param startDate
	 *            Date
	 * @param endDate
	 *            date
	 * @return PrimarySpec
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws PropertyVetoException
	 */
	private static Specification getSpecificationBean(LCSProduct productObj,
			LCSSourcingConfig source, FlexSpecToSeasonLink specToSeasonLink,
			Collection<LCSSKU> skus, Collection<String> colProdToBOMIds,
			Collection<String> colProdToBOMLinkIds,
			Map<String, Collection<HashMap>> mapTrackedBOM,
			boolean deltaCriteria, Map bomDeltaDateMap) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException, PropertyVetoException {
		String methodName = "getSpecificationBean() ";
		// Initialisation
		Specification primarySpecBean = new Specification();
		// Check if Spec is not null
		if (specToSeasonLink != null) {
			// Get Spec
			FlexSpecification specObject = (FlexSpecification) VersionHelper
					.latestIterationOf(specToSeasonLink
							.getSpecificationMaster());

			// Get Primary Spec Bean.
			primarySpecBean = BurberryProductBOMAPIJsonDataUtil
					.getSpecificationBean(specObject, specToSeasonLink);
			logger.debug(methodName + "Primary Specification Bean: "
					+ primarySpecBean);
			// CR R26: Handle Remove Object Customisation : Start
			// Get the list of BOM bean and set on spec bean.
			List<BOM> bomList = BurberryProductBOMAPIDataTransform
					.getListBOMBean(productObj, source, specObject,
							specToSeasonLink.getSeasonMaster(), skus,
							colProdToBOMIds, colProdToBOMLinkIds,
							mapTrackedBOM, deltaCriteria,bomDeltaDateMap);

			logger.debug(methodName + "List of Associated Image Pages: "
					+ bomList);
			primarySpecBean.setBOM(bomList);
			// CR R26: Handle Remove Object Customisation : End
		}
		logger.debug(methodName + "primarySpecBean: " + primarySpecBean);
		return primarySpecBean;
	}

}
