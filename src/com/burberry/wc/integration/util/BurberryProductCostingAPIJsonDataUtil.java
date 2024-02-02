package com.burberry.wc.integration.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.productcostingapi.bean.*;
import com.burberry.wc.integration.productcostingapi.constant.BurProductCostingConstant;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sizing.ProductSizeCategory;
import com.lcs.wc.sourcing.LCSCostSheetMaster;
import com.lcs.wc.sourcing.LCSCostSheetQuery;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.VersionHelper;

public final class BurberryProductCostingAPIJsonDataUtil {

	/**
	 * logger.
	 */
	public static final Logger logger = Logger
			.getLogger(BurberryProductCostingAPIJsonDataUtil.class);

	/**
	 * Private constructor.
	 */
	private BurberryProductCostingAPIJsonDataUtil() {

	}

	/**
	 * Method to Fetch Style Bean Data.
	 * 
	 * @param LCSProduct
	 * @return Product bean
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 */

	public static Style getStyleBean(LCSProduct productObj)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		// Method Name
		String methodName = "getStyleBean() ";
		logger.debug(methodName + "Extracting Data from Product: "
				+ productObj.getName());

		// Initialisation
		Style styleBean = new Style();

		// Get JSOON mapping from keys
		Map<String, String> jsonMappingProduct = BurberryAPIUtil
				.getJsonMapping(BurProductCostingConstant.JSON_PRODUCT_KEY);
		logger.debug(methodName + "jsonMappingProduct: " + jsonMappingProduct);

		// Get JSON mapping from System keys
		Map<String, String> systemAttjsonMappingProduct = BurberryAPIUtil
				.getJsonMapping(BurProductCostingConstant.JSON_SYSTEM_PRODUCT_KEY);
		logger.debug(methodName + "systemAttjsonMappingProduct: "
				+ systemAttjsonMappingProduct);

		// Using JSON mapping attribute key and JSON key get data and set on
		// bean
		BurberryAPIBeanUtil.getObjectData(
				BurProductCostingConstant.PRODUCT_IGNORE, styleBean,
				productObj, BurProductCostingConstant.PRODUCT_ATT,
				jsonMappingProduct, systemAttjsonMappingProduct);

		// Get Product Name
		if (jsonMappingProduct.containsKey(BurConstant.NAME)) {
			BeanUtils.setProperty(styleBean,
					jsonMappingProduct.get(BurConstant.NAME),
					productObj.getName());
		}

		logger.debug(methodName + "Style Bean: " + styleBean);

		// Validate Required Attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(styleBean,
		// BurProductCostingConstant.PRODUCT_REQ);

		// Return
		return styleBean;
	}

	/**
	 * Method to Fetch Source Bean Data.
	 * 
	 * @param LCSSourcingConfig
	 * @return Source bean
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 */

	public static Source getSourceBean(LCSSourcingConfig sourcingConfig)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {

		// Method Name
		String methodName = "getSourceBean() ";
		logger.debug(methodName + "Extracting Data from Source: "
				+ sourcingConfig.getName());

		// Initialisation
		Source sourceBean = new Source();

		// Get JSOON mapping from keys
		Map<String, String> jsonMappingSource = BurberryAPIUtil
				.getJsonMapping(BurProductCostingConstant.JSON_SOURCE_KEY);
		logger.debug(methodName + "jsonMappingSource: " + jsonMappingSource);

		// Get JSON mapping from System keys
		Map<String, String> systemAttjsonMappingSource = BurberryAPIUtil
				.getJsonMapping(BurProductCostingConstant.JSON_SYSTEM_SOURCE_KEY);
		logger.debug(methodName + "systemAttjsonMappingSource: "
				+ systemAttjsonMappingSource);

		// Using JSON mapping attribute key and JSON key get data and set on
		// bean
		BurberryAPIBeanUtil.getObjectData(
				BurProductCostingConstant.SOURCE_IGNORE, sourceBean,
				sourcingConfig, BurProductCostingConstant.SOURCE_ATT,
				jsonMappingSource, systemAttjsonMappingSource);

		if (systemAttjsonMappingSource
				.containsKey(BurProductCostingConstant.PRIMARY_SOURCE)) {
			BeanUtils.setProperty(sourceBean, systemAttjsonMappingSource
					.get(BurProductCostingConstant.PRIMARY_SOURCE),
					sourcingConfig.isPrimarySource());
		}
		//BURBERRY-1485 New Attributes Additions post Sprint 8: Start
		if (systemAttjsonMappingSource.containsKey(BurConstant.BRANCHID)) {
			BeanUtils.setProperty(sourceBean,
					systemAttjsonMappingSource.get(BurConstant.BRANCHID),
					sourcingConfig.getBranchIdentifier());
		}
		//BURBERRY-1485 New Attributes Additions post Sprint 8: End
		
		// Validate Required Attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(sourceBean,
		// BurProductCostingConstant.SOURCE_REQ);

		// Return
		return sourceBean;
	}

	/**
	 * Method to Fetch Cost Sheet Bean Data.
	 * 
	 * @param collProdToSKUIds
	 * 
	 * @param LCSSourcingConfig
	 * @return Source bean
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 */

	public static CostSheet getCostSheetBean(
			LCSProductCostSheet costSheetObject,
			Collection<String> collProdToSKUIds) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException {

		// Method Name
		String methodName = "getCostSheetBean() ";
		logger.info(methodName + "Extracting Data from Cost Sheet: "
				+ costSheetObject.getName());

		// Initialisation
		CostSheet costSheetBean = new CostSheet();

		// Get JSOON mapping from keys
		Map<String, String> jsonMappingCostSheet = BurberryAPIUtil
				.getJsonMapping(BurProductCostingConstant.JSON_COSTSHEET_KEY);
		logger.debug(methodName + "jsonMappingCostSheet: "
				+ jsonMappingCostSheet);

		// Get JSON mapping from System keys
		Map<String, String> systemAttjsonMappingCostSheet = BurberryAPIUtil
				.getJsonMapping(BurProductCostingConstant.JSON_SYSTEM_COSTSHEET_KEY);
		logger.debug(methodName + "systemAttjsonMappingCostSheet: "
				+ systemAttjsonMappingCostSheet);

		// Using JSON mapping attribute key and JSON key get data and set on
		// bean
		BurberryAPIBeanUtil.getObjectData(
				BurProductCostingConstant.COSTSHEET_IGNORE, costSheetBean,
				costSheetObject, BurProductCostingConstant.COSTSHEET_ATT,
				jsonMappingCostSheet, systemAttjsonMappingCostSheet);

		if (systemAttjsonMappingCostSheet
				.containsKey(BurProductCostingConstant.SEASONNAME)
				&& costSheetObject.getSeasonMaster() != null) {
			LCSSeason season = (LCSSeason) VersionHelper
					.latestIterationOf(costSheetObject.getSeasonMaster());
			// costSheetBean.setCsProdSeasonName(season.getName());
			BeanUtils.setProperty(costSheetBean, systemAttjsonMappingCostSheet
					.get(BurProductCostingConstant.SEASONNAME), season
					.getName());
		}
		if (systemAttjsonMappingCostSheet
				.containsKey(BurProductCostingConstant.PRIMARY_COST_SHEET)) {
			BeanUtils.setProperty(costSheetBean, systemAttjsonMappingCostSheet
					.get(BurProductCostingConstant.PRIMARY_COST_SHEET),
					costSheetObject.isPrimaryCostSheet());
		}
		if (systemAttjsonMappingCostSheet.containsKey(BurConstant.BRANCHID)) {
			BeanUtils.setProperty(costSheetBean,
					systemAttjsonMappingCostSheet.get(BurConstant.BRANCHID),
					costSheetObject.getBranchIdentifier());
		}

		updateCostSheetAttributes(costSheetObject, costSheetBean,
				systemAttjsonMappingCostSheet);

		Collection<FlexObject> colorlist = LCSCostSheetQuery
				.getColorLinks((LCSCostSheetMaster) costSheetObject.getMaster());

		costSheetBean.setColourways(getListColourway(colorlist,
				collProdToSKUIds));
		logger.debug(methodName + "Cost Sheet Bean: " + costSheetBean);

		// Validate Required Attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(costSheetBean,
		// BurProductCostingConstant.COSTSHEET_REQ);

		// Return
		return costSheetBean;
	}

	private static void updateCostSheetAttributes(
			LCSProductCostSheet costSheetObject, CostSheet costSheetBean,
			Map<String, String> systemAttjsonMappingCostSheet)
			throws WTException, IllegalAccessException,
			InvocationTargetException {
		if (systemAttjsonMappingCostSheet
				.containsKey(BurProductCostingConstant.REP_COLOURWAY)) {
			final Collection<FlexObject> colorIds = LCSCostSheetQuery
					.getRepresentativeColor((LCSCostSheetMaster) costSheetObject
							.getMaster());
			for (FlexObject fobj : colorIds) {
				LCSSKU costsheetsku = (LCSSKU) LCSQuery
						.findObjectById("VR:com.lcs.wc.product.LCSSKU:"
								+ fobj.getData("LCSSKU.BRANCHIDITERATIONINFO"));
				// Fix for BUG 25136 as this is giving B version of sku and old
				// sku name value
				/*
				 * costsheetsku = (LCSSKU) VersionHelper
				 * .latestIterationOf(costsheetsku.getMaster());
				 */
				BeanUtils.setProperty(costSheetBean,
						systemAttjsonMappingCostSheet
								.get(BurProductCostingConstant.REP_COLOURWAY),
						costsheetsku.getValue(BurConstant.SKUNAME));
			}
		}
		if (systemAttjsonMappingCostSheet
				.containsKey(BurProductCostingConstant.PRODUCT_SIZE)) {
			ProductSizeCategory size = new LCSCostSheetQuery()
					.getReferencedProductSizeCategory((LCSCostSheetMaster) costSheetObject
							.getMaster());
			if (size != null) {
				BeanUtils.setProperty(costSheetBean,
						systemAttjsonMappingCostSheet
								.get(BurProductCostingConstant.PRODUCT_SIZE),
						size.getName());
			}
		}

		if (systemAttjsonMappingCostSheet
				.containsKey(BurProductCostingConstant.SPEC)) {
			if (costSheetObject.getSpecificationMaster() != null) {
				FlexSpecification spec = (FlexSpecification) VersionHelper
						.latestIterationOf(costSheetObject
								.getSpecificationMaster());
				BeanUtils.setProperty(costSheetBean,
						systemAttjsonMappingCostSheet
								.get(BurProductCostingConstant.SPEC), spec
								.getName());
			}
		}

	}

	public static List<Colourway> getListColourway(
			Collection<FlexObject> colorlist,
			Collection<String> collProdToSKUIds) throws WTException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, IOException {
		List<Colourway> listclwy = new ArrayList<Colourway>();
		for (FlexObject fobj : colorlist) {
			boolean idExists = BurberryAPIDBUtil.checkIfObjectExists(String
					.valueOf(fobj.getData("LCSSKU.BRANCHIDITERATIONINFO")),
					collProdToSKUIds);
			// Check if exists
			if (idExists) {
				LCSSKU costsheetsku = (LCSSKU) LCSQuery
						.findObjectById("VR:com.lcs.wc.product.LCSSKU:"
								+ fobj.getData("LCSSKU.BRANCHIDITERATIONINFO"));
				costsheetsku = (LCSSKU) VersionHelper
						.latestIterationOf(costsheetsku.getMaster());
				listclwy.add(getColourwayBean(costsheetsku));

			}
		}
		return listclwy;
	}

	/**
	 * Method to Fetch Colourway Bean Data.
	 * 
	 * @param LCSSKU
	 * @return Colourway bean
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 */

	public static Colourway getColourwayBean(LCSSKU skuObject)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		// Method Name
		String methodName = "getColourwayBean() ";
		logger.debug(methodName + "Extracting Data from Colourway: "
				+ skuObject.getName());

		// Initialisation
		Colourway colourwayBean = new Colourway();

		// Get JSOON mapping from keys
		Map<String, String> jsonMappingColourway = BurberryAPIUtil
				.getJsonMapping(BurProductCostingConstant.JSON_COLOURWAY_KEY);
		logger.debug(methodName + "jsonMappingColourway: "
				+ jsonMappingColourway);

		// Get JSON mapping from System keys
		Map<String, String> systemAttjsonMappingColourway = BurberryAPIUtil
				.getJsonMapping(BurProductCostingConstant.JSON_SYSTEM_COLOURWAY_KEY);
		logger.debug(methodName + "systemAttjsonMappingColourway: "
				+ systemAttjsonMappingColourway);

		// Using JSON mapping attribute key and JSON key get data and set on
		// bean
		BurberryAPIBeanUtil.getObjectData(
				BurProductCostingConstant.COLOURWAY_IGNORE, colourwayBean,
				skuObject, BurProductCostingConstant.COLOURWAY_ATT,
				jsonMappingColourway, systemAttjsonMappingColourway);

		logger.debug(methodName + "Colourway Bean: " + colourwayBean);

		// Validate Required Attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(colourwayBean,
		// BurProductCostingConstant.COLOURWAY_REQ);

		// Return
		return colourwayBean;
	}

}
