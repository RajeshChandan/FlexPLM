package com.burberry.wc.integration.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.productbomapi.bean.Colourway;
import com.burberry.wc.integration.productbomapi.bean.ColourwaySeason;
import com.burberry.wc.integration.productbomapi.bean.ProductSeason;
import com.burberry.wc.integration.productbomapi.bean.Season;
import com.burberry.wc.integration.productbomapi.bean.Source;
import com.burberry.wc.integration.productbomapi.bean.Style;
import com.burberry.wc.integration.productbomapi.constant.BurProductBOMConstant;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.util.VersionHelper;

/**
 * A Helper class to handle JSON data transform activity for Product BOM API.
 * Class contain several method to handle transform of object data putting it to
 * the beans.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryProductBOMAPIStyleUtil {

	/**
	 * BurberryProductAPIDataExtraction.
	 */
	private BurberryProductBOMAPIStyleUtil() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductBOMAPIStyleUtil.class);

	/**
	 * @param productObj
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	public static Style getProductBean(LCSProduct productObj)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {

		final Style styleBean = new Style();
		String methodName = "getProductBean()";
		logger.debug(methodName + "Extracting data from product "
				+ productObj.getName());
		Map<String, String> jsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.JSON_PRODUCTKEY);

		Map<String, String> sysJsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.SYSTEM_JSON_PRODUCTKEY);
		// Getting Product Bean data
		BurberryAPIBeanUtil.getObjectData(BurProductBOMConstant.STYLE_IGNORE,
				styleBean, productObj, BurProductBOMConstant.STYLE_ATT,
				jsonMapping, sysJsonMapping);
		if(jsonMapping.containsKey(BurConstant.NAME)){
			BeanUtils.setProperty(styleBean, jsonMapping.get(BurConstant.NAME),
					productObj.getName());
		}
		// Validating Required attributes
		//BurberryAPIBeanUtil.validateRequiredAttributes(styleBean,
			//	BurProductBOMConstant.STYLE_REQ);
		logger.debug(methodName + "Returing Style bean " + styleBean);
		// returning Style bean
		return styleBean;
	}

	/**
	 * @param sku
	 * @return
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 */
	public static Colourway getColourwayBean(LCSSKU sku) throws IOException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException {

		String methodName = "getColourwayBean()";
		logger.debug(methodName + "Extracting data from colourway "
				+ sku.getName());
		Colourway colourwayBean = new Colourway();
		Map<String, String> jsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.JSON_COLOURWAYKEY);
		Map<String, String> sysJsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.SYSTEM_JSON_COLOURWAY);
		// Getting colourway data
		BurberryAPIBeanUtil.getObjectData(BurProductBOMConstant.SKU_IGNORE,
				colourwayBean, sku, BurProductBOMConstant.SKU_ATT, jsonMapping,
				sysJsonMapping);
		// Validating Required attributes
		//BurberryAPIBeanUtil.validateRequiredAttributes(colourwayBean,
			//	BurProductBOMConstant.SKU_REQ);
		logger.debug(methodName + "Returing Colourway bean " + colourwayBean);
		// Returning colourway Bean
		return colourwayBean;

	}

	/**
	 * @param spl
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 */
	public static Season getSeasonBean(LCSProductSeasonLink spl)
			throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException {
		LCSSeason season = (LCSSeason) VersionHelper.latestIterationOf(spl
				.getSeasonMaster());
		String methodName = "getSeasonBean()";
		logger.debug(methodName + "Extracting data from season "
				+ season.getName());
		Season seasonBean = new Season();
		Map<String, String> jsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.JSON_SEASONKEY);

		Map<String, String> sysJsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.SYSTEM_JSON_SEASONKEY);
		// Getting season Bean data
		BurberryAPIBeanUtil.getObjectData(BurProductBOMConstant.SEASON_IGNORE,
				seasonBean, season, BurProductBOMConstant.SEASON_ATT,
				jsonMapping, sysJsonMapping);
		// Validating Required attributes
		//BurberryAPIBeanUtil.validateRequiredAttributes(seasonBean,
			//	BurProductBOMConstant.SEASON_REQ);
		logger.debug(methodName + "Returing Season bean " + seasonBean);
		// Returning Source Vean
		return seasonBean;
	}

	/**
	 * @param spl
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	public static ProductSeason getProductSeasonBean(LCSProductSeasonLink spl)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		String methodName = "getProductSeasonBean()";
		logger.debug(methodName + "Extracting data from seasonProduct Link "
				+ spl);
		ProductSeason productSeasonBean = new ProductSeason();
		Map<String, String> jsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.JSON_PRODCUTSEASONKEY);
		Map<String, String> sysJsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.SYSTEM_JSON_PRODUCTSEASONKEY);
		// Getting product season bean data
		BurberryAPIBeanUtil.getObjectData(
				BurProductBOMConstant.PRODUCT_SEASON_IGNORE, productSeasonBean,
				spl, BurProductBOMConstant.PRODUCT_SEASON_ATT, jsonMapping,
				sysJsonMapping);
		
		// Validating Required attributes
		//BurberryAPIBeanUtil.validateRequiredAttributes(productSeasonBean,
		//		BurProductBOMConstant.PRODUCT_SEASON_REQ);
		logger.debug(methodName + "Returing Product Season bean "
				+ productSeasonBean);
		// returning product season object
		return productSeasonBean;
	}

	/**
	 * @param skuseasonLink
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	public static ColourwaySeason getColourwaySeasonBean(
			LCSSKUSeasonLink skuseasonLink, String skuSeasonName)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		String methodName = "getColourwaySeasonBean()";
		logger.debug(methodName + "Extracting data from colourway season "
				+ skuseasonLink);
		ColourwaySeason colourwaySeasonBean = new ColourwaySeason();

		Map<String, String> jsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.JSON_COLOURWAYSEASONKEY);
		Map<String, String> sysJsonMapping = BurberryAPIUtil
				.getJsonMapping(BurProductBOMConstant.SYSTEM_JSON_COLOURWAYSEASONKEY);
		// Getting Colourway season bean data
		BurberryAPIBeanUtil.getObjectData(
				BurProductBOMConstant.SKU_SEASON_IGNORE, colourwaySeasonBean,
				skuseasonLink, BurProductBOMConstant.SKU_SEASON_ATT,
				jsonMapping, sysJsonMapping);
		// Loop through each key and json key
		for (Map.Entry<String, String> mapEntry : jsonMapping.entrySet()) {
			String strAttKey = mapEntry.getKey();
			String strJsonKey = mapEntry.getValue();
			logger.debug(methodName + "Colour Season: AttKey=" + strAttKey);
			logger.debug(methodName + "Colour Season: JsonKey=" + strJsonKey);
			if (BurConstant.COLOURWAY_SEASON_ATT_BURSEASON
					.equalsIgnoreCase(strAttKey)) {
				BeanUtils.setProperty(colourwaySeasonBean, strJsonKey,
						skuSeasonName);
			}
		}
	
		// Validating Required attribute;
		//BurberryAPIBeanUtil.validateRequiredAttributes(colourwaySeasonBean,
		//		BurProductBOMConstant.SKU_SEASON_REQ);
		logger.debug(methodName + "Returing Colourway Season bean "
				+ colourwaySeasonBean);
		// Returning colourway season Bean data
		return colourwaySeasonBean;
	}

	/**
	 * @param primSource
	 * @param seasonMaster
	 * @param productObj
	 * @param colProdToBOMIds
	 * @param colProdToBOMLinkIds
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	public static Source getSourceBean(LCSSourcingConfig primSource,
			LCSSeasonMaster seasonMaster, LCSProduct productObj,
			Collection<String> colProdToBOMIds, Collection<String> colProdToBOMLinkIds,
			Map<String, Collection<HashMap>> mapTrackedBOM)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {

		String methodName = "getSourceBean()";
		Source sourceBean = new Source();
		if (primSource != null) {
			logger.debug(methodName + "Extracting data from source "
					+ primSource.getName());

			Map<String, String> jsonMapping = BurberryAPIUtil
					.getJsonMapping(BurProductBOMConstant.JSON_SOURCE);
			Map<String, String> sysJsonMapping = BurberryAPIUtil
					.getJsonMapping(BurProductBOMConstant.SYSTEM_JSON_SOURCE);
			// Getting Source bean data
			BurberryAPIBeanUtil.getObjectData(
					BurProductBOMConstant.SOURCE_IGNORE, sourceBean,
					primSource, BurProductBOMConstant.SOURCE_ATT, jsonMapping,
					sysJsonMapping);
			//Set if Source is primary or not.
			if (sysJsonMapping.containsKey(BurConstant.PRIMARY)) {
				BeanUtils.setProperty(sourceBean,
						sysJsonMapping.get(BurConstant.PRIMARY),
						primSource.isPrimarySource());
			}
			
			// BURBERRY-1485 New Attributes Additions post Sprint 8: Start
			if (sysJsonMapping.containsKey(BurConstant.BRANCHID)) {
				BeanUtils.setProperty(sourceBean,
						sysJsonMapping.get(BurConstant.BRANCHID),
						primSource.getBranchIdentifier());
			}
			// BURBERRY-1485 New Attributes Additions post Sprint 8: End

		}

		logger.debug(methodName + "Returing Source bean " + sourceBean);

		// Returing source bean
		return sourceBean;

	}
}
