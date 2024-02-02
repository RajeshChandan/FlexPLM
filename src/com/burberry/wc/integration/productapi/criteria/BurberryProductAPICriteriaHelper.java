package com.burberry.wc.integration.productapi.criteria;

import java.text.ParseException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.util.BurConstant;
import com.burberry.wc.integration.util.BurberryAPICriteriaUtil;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.flextype.*;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.placeholder.Placeholder;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.SourcingConfigFlexTypeScopeDefinition;

/**
 * A Criteria class to generate criteria for prepared query.
 *
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryProductAPICriteriaHelper {

	/**
	 * STR_EQUAL.
	 */
	private static final String STR_EQUAL = "=";

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductAPICriteriaHelper.class);

	/**
	 * ProductAPICriteriaHelper.
	 */
	private BurberryProductAPICriteriaHelper() {

	}

	/**
	 * Method to get Product Criteria.
	 * 
	 * @param strAttDisplayName
	 *            Display Name
	 * @param strAttValue
	 *            Att Value
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 * @throws BurException
	 *             Exception
	 * @throws ParseException
	 *             Exception
	 */
	public static Collection<Map> getProductQueryCriteria(
			String strAttDisplayName, String strAttValue) throws WTException,
			BurException, ParseException {

		String methodName = "getProductQueryCriteria() ";

		FlexType productType = FlexTypeCache
				.getFlexTypeRootByClass((LCSProduct.class).getName());
		logger.debug(methodName + "productType: " + productType);

		Collection<Map> productQueryCriteria = new ArrayList<Map>();

		// Step 1: Check if attribute display name is valid
		// FlexTypeAttribute productAtt =
		// getFlexTypeAttribute(productType,strAttDisplayName);
		Collection<FlexTypeAttribute> colProductAtt = BurberryProductAPICriteria
				.getProductFlexTypeAttribute(productType,
						FootwearApparelFlexTypeScopeDefinition.PRODUCT_SCOPE,
						FootwearApparelFlexTypeScopeDefinition.PRODUCT_LEVEL,
						strAttDisplayName);
		logger.debug(methodName + "colProductAtt: " + colProductAtt);

		for (FlexTypeAttribute productAtt : colProductAtt) {
			// Step 2: Check for attribute data type
			String strColumnName = productAtt.getColumnName();
			logger.debug(methodName + "Product Attribute Name: "
					+ strAttDisplayName);
			logger.debug(methodName + "Product Table Column Name: "
					+ strColumnName);
			logger.debug(methodName + "Product Attribute Value: " + strAttValue);

			// Check if the attribute is operational category
			if (BurConstant.STR_OPERATIONAL_CATEGORY
					.equalsIgnoreCase(productAtt.getAttDisplay())) {
				productQueryCriteria.addAll(BurberryProductAPICriteria
						.appendCriteriaforOperationalCategory(
								BurConstant.LCSPRODUCT, strColumnName,
								productAtt, strAttValue));
			} else {
				// Step 3: Append criteria based on data type
				productQueryCriteria.addAll(BurberryAPICriteriaUtil
						.appendCriteriaBasedOnDataType(BurConstant.LCSPRODUCT,
								strColumnName, productAtt, strAttValue));
			}
		}

		logger.debug(methodName + "productQueryCriteria: "
				+ productQueryCriteria);
		// Step 4: Return Criteria Collection
		return productQueryCriteria;
	}

	/**
	 * Method to get Product Season Link Criteria.
	 * 
	 * @param strAttDisplayName
	 *            name
	 * @param strAttValue
	 *            value
	 * @return collection
	 * @throws WTException
	 *             exception
	 * @throws BurException
	 *             exception
	 * @throws ParseException
	 *             exception
	 */
	public static Collection<Map> getProductSeasonLinkQueryCriteria(
			String strAttDisplayName, String strAttValue) throws WTException,
			BurException, ParseException {

		String methodName = "getProductSeasonLinkQueryCriteria() ";

		final FlexType productSeasonLinkType = FlexTypeCache
				.getFlexTypeRootByClass((LCSProduct.class).getName());

		logger.debug(methodName + "productSeasonLinkType: "
				+ productSeasonLinkType);

		// String strScope =
		// FootwearApparelFlexTypeScopeDefinition.PRODUCTSEASON_SCOPE;
		String strLevel = FootwearApparelFlexTypeScopeDefinition.PRODUCT_LEVEL;
		logger.debug(methodName + "Level: " + strLevel);

		logger.debug(methodName + "Product Season Attribute Name: "
				+ strAttDisplayName);

		Collection<Map> productSeasonLinkQueryCriteria = new ArrayList<Map>();

		// Step 1: Check if attribute display name is valid
		Collection<FlexTypeAttribute> colProductSeasonAtt = BurberryProductAPICriteria
				.getProductFlexTypeAttribute(
						productSeasonLinkType,
						FootwearApparelFlexTypeScopeDefinition.PRODUCTSEASON_SCOPE,
						FootwearApparelFlexTypeScopeDefinition.PRODUCT_LEVEL,
						strAttDisplayName);

		for (FlexTypeAttribute productSeasonAtt : colProductSeasonAtt) {
			logger.debug(methodName + "productSeasonAtt: " + productSeasonAtt);

			// Step 2: Check for attribute data type
			String strColumnName = productSeasonAtt.getColumnName(strLevel);

			logger.debug(methodName + "Product Season Table Column Name: "
					+ strColumnName);

			logger.debug(methodName + "Product Season Attribute Value: "
					+ strAttValue);

			// Step 3: Append criteria based on data type
			productSeasonLinkQueryCriteria.addAll(BurberryAPICriteriaUtil
					.appendCriteriaBasedOnDataType(
							BurConstant.LCSPRODUCTSEASONLINK, strColumnName,
							productSeasonAtt, strAttValue));
		}

		logger.debug(methodName + "productSeasonLinkQueryCriteria: "
				+ productSeasonLinkQueryCriteria);

		// Step 4: Return Criteria Collection
		return productSeasonLinkQueryCriteria;
	}

	/**
	 * @param strAttDisplayName
	 *            name
	 * @param strAttValue
	 *            value
	 * @return collection
	 * @throws WTException
	 *             exception
	 * @throws BurException
	 *             exception
	 * @throws ParseException
	 *             exception
	 */
	public static Collection<Map> getColourwayQueryCriteria(
			String strAttDisplayName, String strAttValue) throws WTException,
			BurException, ParseException {

		String methodName = "getColourwayQueryCriteria() ";
		Collection<Map> colourwayQueryCriteria = new ArrayList<Map>();
		final FlexType skuFlexType = FlexTypeCache
				.getFlexTypeRootByClass((LCSSKU.class).getName());
		logger.debug(methodName + "skuFlexType: " + skuFlexType);

		logger.debug(methodName + "Colourway Attribute Name: "
				+ strAttDisplayName);

		// Step 1: Check if attribute display name is valid
		/*
		 * FlexTypeAttribute colourwayAtt = getFlexTypeAttribute(skuFlexType,
		 * strAttDisplayName); logger.debug(methodName +
		 * "Colourway Table Column Name: " + colourwayAtt.getColumnName());
		 */

		Collection<FlexTypeAttribute> colColourwayAtt = BurberryProductAPICriteria
				.getProductFlexTypeAttribute(skuFlexType,
						FootwearApparelFlexTypeScopeDefinition.PRODUCT_SCOPE,
						FootwearApparelFlexTypeScopeDefinition.SKU_LEVEL,
						strAttDisplayName);
		logger.debug(methodName + "colColourwayAtt: " + colColourwayAtt);

		for (FlexTypeAttribute colourwayAtt : colColourwayAtt) {
			logger.debug(methodName + "colourwayAtt: " + colourwayAtt);

			if (FootwearApparelFlexTypeScopeDefinition.PRODUCT_SKU_LEVEL
					.equalsIgnoreCase(colourwayAtt.getAttObjectLevel())) {
				// Step 2: Check for attribute data type
				String strProductColumnName = colourwayAtt
						.getColumnName(FootwearApparelFlexTypeScopeDefinition.PRODUCT_SKU_LEVEL);
				logger.debug(methodName + "Product Table Column Name: "
						+ strProductColumnName);
				logger.debug(methodName + "Product Attribute Value: "
						+ strAttValue);

				// Step 3: Append criteria based on data type
				Collection<Map> colQueryCriteria = BurberryAPICriteriaUtil
						.appendCriteriaBasedOnDataType(BurConstant.LCSPRODUCT,
								strProductColumnName, colourwayAtt, strAttValue);
				colourwayQueryCriteria.addAll(appendSKUColumn(colourwayAtt,
						colQueryCriteria));
			}

			// Step 2: Check for attribute data type
			String strColumnName = colourwayAtt
					.getColumnName(FootwearApparelFlexTypeScopeDefinition.SKU_LEVEL);

			logger.debug(methodName + "Colouway Table Column Name: "
					+ strColumnName);

			logger.debug(methodName + "Colourway Attribute Value: "
					+ strAttValue);

			// Step 3: Append criteria based on data type
			colourwayQueryCriteria.addAll(BurberryAPICriteriaUtil
					.appendCriteriaBasedOnDataType(BurConstant.LCSSKU,
							strColumnName, colourwayAtt, strAttValue));
		}
		logger.debug(methodName + "colourwayQueryCriteria: "
				+ colourwayQueryCriteria);

		// Step 4: Return Criteria Collection
		return colourwayQueryCriteria;
	}

	/**
	 * Method to append sku column.
	 * 
	 * @param colourwayAtt
	 *            Colourway Attribute
	 * @param colQueryCriteria
	 *            Collection
	 * @return Collection
	 */
	private static Collection<Map> appendSKUColumn(
			FlexTypeAttribute colourwayAtt, Collection<Map> colQueryCriteria) {
		String methodName = "appendSKUColumn() ";
		Collection<Map> collectionMap = new ArrayList<Map>();
		for (Map<String, Object> mapCriteria : colQueryCriteria) {
			logger.debug(methodName + "mapCriteria: " + mapCriteria);
			// Loop through each map and get key and value
			for (Map.Entry<String, Object> mapEntry : mapCriteria.entrySet()) {
				String key = mapEntry.getKey();
				String[] strTemp = key.split("\\.");
				String strTableName = strTemp[0];
				logger.debug(methodName + "strTableName: " + strTableName);

				String strColumnName = strTemp[1];
				logger.debug(methodName + "strColumnName: " + strColumnName);

				String strSKUColumnName = colourwayAtt
						.getColumnName(FootwearApparelFlexTypeScopeDefinition.SKU_LEVEL);
				logger.debug(methodName + "strSKUColumnName: "
						+ strSKUColumnName);

				Map<String, String> criteriaMap = new HashMap<String, String>();
				criteriaMap.put(strTableName + "." + strColumnName + ":"
						+ strSKUColumnName + "." + STR_EQUAL,
						(String) mapEntry.getValue());
				logger.debug(methodName + "criteriaMap: " + criteriaMap);
				collectionMap.add(criteriaMap);
			}
		}
		logger.debug(methodName + "collectionMap: " + collectionMap);
		return collectionMap;
	}

	/**
	 * @param strAttDisplayName
	 *            name
	 * @param strAttValue
	 *            value
	 * @return collection
	 * @throws WTException
	 *             exception
	 * @throws BurException
	 *             exception
	 * @throws ParseException
	 *             exception
	 */
	public static Collection<Map> getColourwaySeasonQueryCriteria(
			String strAttDisplayName, String strAttValue) throws WTException,
			BurException, ParseException {

		String methodName = "getColourwaySeasonQueryCriteria() ";

		final FlexType skuSeasonFlexType = FlexTypeCache
				.getFlexTypeRootByClass((LCSSKU.class).getName());

		logger.debug(methodName + "skuSeasonFlexType: " + skuSeasonFlexType);

		// String strScope =
		// FootwearApparelFlexTypeScopeDefinition.PRODUCTSEASON_SCOPE;
		String strLevel = FootwearApparelFlexTypeScopeDefinition.SKU_LEVEL;
		logger.debug(methodName + "Level: " + strLevel);

		logger.debug(methodName + "Colourway Season Attribute Name: "
				+ strAttDisplayName);

		// Step 1: Check if attribute display name is valid
		/*
		 * FlexTypeAttribute skuSeasonAtt = getFlexTypeAttribute(
		 * skuSeasonFlexType, strAttDisplayName); logger.debug(methodName +
		 * "skuSeasonAtt: " + skuSeasonAtt);
		 */

		Collection<Map> skuSeasonQueryCriteria = new ArrayList<Map>();

		// Step 1: Check if attribute display name is valid
		Collection<FlexTypeAttribute> colSkuSeasonAttAtt = BurberryProductAPICriteria
				.getProductFlexTypeAttribute(
						skuSeasonFlexType,
						FootwearApparelFlexTypeScopeDefinition.PRODUCTSEASON_SCOPE,
						FootwearApparelFlexTypeScopeDefinition.SKU_LEVEL,
						strAttDisplayName);
		logger.debug(methodName + "colSkuSeasonAttAtt: " + colSkuSeasonAttAtt);

		for (FlexTypeAttribute skuSeasonAtt : colSkuSeasonAttAtt) {
			logger.debug(methodName + "skuSeasonAtt: " + skuSeasonAtt);

			// Step 2: Check for attribute data type
			String strColumnName = skuSeasonAtt.getColumnName(strLevel);
			logger.debug(methodName + "Colourway Season Table Column Name: "
					+ strColumnName);

			logger.debug(methodName + "Colourway Season Attribute Value: "
					+ strAttValue);

			// Step 3: Append criteria based on data type
			skuSeasonQueryCriteria = BurberryAPICriteriaUtil
					.appendCriteriaBasedOnDataType(
							BurConstant.LCSSKUSEASONLINK, strColumnName,
							skuSeasonAtt, strAttValue);
		}

		logger.debug(methodName + "skuSeasonQueryCriteria: "
				+ skuSeasonQueryCriteria);
		// Step 4: Return Criteria Collection
		return skuSeasonQueryCriteria;
	}

	/**
	 * @param strAttDisplayName
	 *            name
	 * @param strAttValue
	 *            value
	 * @return collection
	 * @throws WTException
	 *             exception
	 * @throws BurException
	 *             exception
	 * @throws ParseException
	 *             exception
	 */
	public static Collection<Map> getSeasonQueryCriteria(
			String strAttDisplayName, String strAttValue) throws WTException,
			BurException, ParseException {

		String methodName = "getSeasonQueryCriteria() ";

		final FlexType seasonFlexType = FlexTypeCache
				.getFlexTypeRootByClass((LCSSeason.class).getName());

		logger.debug(methodName + "seasonFlexType: " + seasonFlexType);

		logger.debug(methodName + "Season Attribute Name: " + strAttDisplayName);

		// Step 1: Check if attribute display name is valid
		FlexTypeAttribute seasonAtt = getFlexTypeAttribute(seasonFlexType,
				strAttDisplayName);

		logger.debug(methodName + "seasonAtt: " + seasonAtt);

		// Step 2: Check for attribute data type
		String strColumnName = seasonAtt.getColumnName();
		logger.debug(methodName + "Season Table Column Name: " + strColumnName);

		logger.debug(methodName + "Season Attribute Value: " + strAttValue);

		// Step 3: Append criteria based on data type
		Collection<Map> seasonQueryCriteria = BurberryAPICriteriaUtil
				.appendCriteriaBasedOnDataType(BurConstant.LCSSEASON,
						strColumnName, seasonAtt, strAttValue);

		logger.debug(methodName + "seasonQueryCriteria: " + seasonQueryCriteria);

		// Step 4: Return Criteria Collection
		return seasonQueryCriteria;

	}

	/**
	 * @param strAttDisplayName
	 *            name
	 * @param strAttValue
	 *            value
	 * @return collection
	 * @throws WTException
	 *             exception
	 * @throws BurException
	 *             exception
	 * @throws ParseException
	 *             exception
	 */
	public static Collection<Map> getSourcingConfigQueryCriteria(
			String strAttDisplayName, String strAttValue) throws WTException,
			BurException, ParseException {

		String methodName = "getSourcingConfigQueryCriteria() ";

		final FlexType sourcingConfigFlexType = FlexTypeCache
				.getFlexTypeRootByClass((LCSSourcingConfig.class).getName());
		logger.debug(methodName + "sourcingConfigFlexType: "
				+ sourcingConfigFlexType);

		logger.debug(methodName + "Source Attribute Name: " + strAttDisplayName);

		Collection<Map> sourcingConfigQueryCriteria = new ArrayList<Map>();

		// Step 1: Check if attribute display name is valid
		Collection<FlexTypeAttribute> colSourcingConfigAtt = BurberryProductAPICriteria
				.getProductFlexTypeAttribute(
						sourcingConfigFlexType,
						SourcingConfigFlexTypeScopeDefinition.SOURCING_CONFIG_SCOPE,
						SourcingConfigFlexTypeScopeDefinition.PRODUCT_LEVEL,
						strAttDisplayName);
		logger.debug(methodName + "colSourcingConfigAtt: "
				+ colSourcingConfigAtt);

		for (FlexTypeAttribute sourcingConfigAtt : colSourcingConfigAtt) {
			logger.debug(methodName + "sourcingConfigAtt: " + sourcingConfigAtt);

			// Step 2: Check for attribute data type
			String strColumnName = sourcingConfigAtt.getColumnName();
			logger.debug(methodName + "Source Table Column Name: "
					+ strColumnName);

			logger.debug(methodName + "Source Attribute Value: " + strAttValue);

			// Step 3: Append criteria based on data type
			sourcingConfigQueryCriteria.addAll(BurberryAPICriteriaUtil
					.appendCriteriaBasedOnDataType(
							BurConstant.LCSSOURCINGCONFIG, strColumnName,
							sourcingConfigAtt, strAttValue));
		}
		logger.debug(methodName + "sourcingConfigQueryCriteria: "
				+ sourcingConfigQueryCriteria);

		// Step 4: Return Criteria Collection
		return sourcingConfigQueryCriteria;

	}

	/**
	 * @param strAttDisplayName
	 *            name
	 * @param strAttValue
	 *            value
	 * @return collection
	 * @throws WTException
	 *             exception
	 * @throws BurException
	 *             exception
	 * @throws ParseException
	 *             exception
	 */
	public static Collection<Map> getPlaceholderQueryCriteria(
			String strAttDisplayName, String strAttValue) throws WTException,
			BurException, ParseException {

		String methodName = "getPlaceholderQueryCriteria() ";

		final FlexType placeHolderFlexType = FlexTypeCache
				.getFlexTypeRootByClass((Placeholder.class).getName());

		logger.debug(methodName + "placeHolderFlexType: " + placeHolderFlexType);

		logger.debug(methodName + "Placeholder Attribute Name: "
				+ strAttDisplayName);

		// Step 1: Check if attribute display name is valid
		FlexTypeAttribute placeholderAtt = getFlexTypeAttribute(
				placeHolderFlexType, strAttDisplayName);

		// Step 2: Check for attribute data type
		String strColumnName = placeholderAtt.getColumnName();
		logger.debug(methodName + "Placeholder Table Column Name: "
				+ strColumnName);

		logger.debug(methodName + "Placeholder Attribute Value: " + strAttValue);

		// Step 3: Append criteria based on data type
		Collection<Map> placeholderQueryCriteria = BurberryAPICriteriaUtil
				.appendCriteriaBasedOnDataType(BurConstant.PLACEHOLDER,
						strColumnName, placeholderAtt, strAttValue);

		logger.debug(methodName + "placeholderQueryCriteria: "
				+ placeholderQueryCriteria);

		// Step 4: Return Criteria Collection
		return placeholderQueryCriteria;
	}

	/**
	 * @param strAttDisplayName
	 *            name
	 * @param strAttValue
	 *            value
	 * @return collection
	 * @throws WTException
	 *             exception
	 * @throws BurException
	 *             exception
	 * @throws ParseException
	 *             exception
	 */
	public static Collection<Map> getMaterialQueryCriteria(
			String strAttDisplayName, String strAttValue) throws WTException,
			BurException, ParseException {

		String methodName = "getMaterialQueryCriteria() ";
		Collection<Map> materialQueryCriteria = new ArrayList<Map>();
		final FlexType materialFlexType = FlexTypeCache
				.getFlexTypeRootByClass((LCSMaterial.class).getName());
		logger.debug(methodName + "materialFlexType: " + materialFlexType);

		// Step 1: Check if attribute display name is valid
		Collection<FlexTypeAttribute> colMaterialAtt = BurberryProductAPICriteria
				.getMaterialFlexTypeAttribute(materialFlexType,
						strAttDisplayName);

		logger.debug(methodName + "Material Attribute Name: "
				+ strAttDisplayName);

		for (FlexTypeAttribute materialAtt : colMaterialAtt) {
			// Step 2: Check for attribute data type
			String strColumnName = materialAtt.getColumnName();

			logger.debug(methodName + "Material Table Column Name: "
					+ strColumnName);

			logger.debug(methodName + "Material Attribute Value: "
					+ strAttValue);

			// Step 3: Append criteria based on data type
			materialQueryCriteria.addAll(BurberryAPICriteriaUtil
					.appendCriteriaBasedOnDataType(BurConstant.LCSMATERIAL,
							strColumnName, materialAtt, strAttValue));
		}

		logger.debug(methodName + "materialQueryCriteria: "
				+ materialQueryCriteria);

		// Step 4: Return Criteria Collection
		return materialQueryCriteria;
	}

	/**
	 * Method to get commodity code criteria.
	 * 
	 * @param strAttDisplayName
	 *            name
	 * @param strAttValue
	 *            value
	 * @return collection
	 * @throws WTException
	 *             Exception
	 * @throws BurException
	 *             Exception
	 * @throws ParseException
	 *             Exception
	 */
	public static Collection<Map> getCommodityCodeQueryCriteria(
			String strAttDisplayName, String strAttValue) throws WTException,
			BurException, ParseException {

		String methodName = "getCommodityCodeQueryCriteria() ";

		final FlexType commodityCodeFlexType = FlexTypeCache
				.getFlexTypeFromPath("Business Object\\burCommodityCode");

		logger.debug(methodName + "commodityCodeFlexType: "
				+ commodityCodeFlexType);

		// Step 1: Check if attribute display name is valid
		FlexTypeAttribute commodityCodeAtt = getFlexTypeAttribute(
				commodityCodeFlexType, strAttDisplayName);

		logger.debug(methodName + "Commodity Code Attribute Name: "
				+ strAttDisplayName);

		// Step 2: Check for attribute data type
		String strColumnName = commodityCodeAtt.getColumnName();

		logger.debug(methodName + "Commodity Code Table Column Name: "
				+ strColumnName);

		logger.debug(methodName + "Commodity Code Attribute Value: "
				+ strAttValue);

		// Step 3: Append criteria based on data type
		Collection<Map> ccQueryCriteria = BurberryAPICriteriaUtil
				.appendCriteriaBasedOnDataType(BurConstant.LCSLIFECYCLEMANAGED,
						strColumnName, commodityCodeAtt, strAttValue);

		logger.debug(methodName + "ccQueryCriteria: " + ccQueryCriteria);

		// Step 4: Return Criteria Collection
		return ccQueryCriteria;
	}

	/**
	 * Method to get flex type attribute.
	 * 
	 * @param flexType
	 *            FlexType
	 * @param strAttDisplayName
	 *            name
	 * @return flex type attribute
	 * @throws WTException
	 *             exception
	 * @throws BurException
	 *             exception
	 */
	private static FlexTypeAttribute getFlexTypeAttribute(FlexType flexType,
			String strAttDisplayName) throws WTException, BurException {

		String methodName = "getFlexTypeAttribute() ";
		logger.debug(methodName + "AttDisplayName: " + strAttDisplayName);
		HashMap<String, String> attMap = FlexType
				.getAttributeKeyDisplayMap(flexType.getAllAttributes());

		logger.debug(methodName + "attMap: " + attMap);

		for (Map.Entry<String, String> entry : attMap.entrySet()) {
			logger.debug(methodName + "entry: " + entry);
			if (entry.getValue().equalsIgnoreCase(strAttDisplayName)) {
				logger.debug(methodName + "attKey " + entry.getKey() + " type "
						+ flexType.getFullName());
				return flexType.getAttribute(entry.getKey());
			}
		}

		BurberryAPIUtil.throwBurException(strAttDisplayName,
				BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTE);
		return null;
	}

}
