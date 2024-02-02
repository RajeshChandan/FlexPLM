package com.burberry.wc.integration.util;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTProperties;

import com.burberry.wc.integration.productapi.bean.*;
import com.burberry.wc.integration.productapi.constant.BurProductConstant;
import com.burberry.wc.integration.sampleapi.constant.BurSampleConstant;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.*;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.util.*;
import com.lcs.wc.color.LCSColor;

/**
 * A Helper class to handle Extraction activity. Class contain several method to
 * handle Extraction activity i.e. Extracting Data from different objects and
 * putting it to the bean
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryProductAPIJsonDataUtil {

	/**
	 * logger.
	 */
	public static final Logger logger = Logger
			.getLogger(BurberryProductAPIJsonDataUtil.class);

	/**
	 * Private constructor.
	 */
	private BurberryProductAPIJsonDataUtil() {

	}

	/**
	 * STR_WT_SERVER.
	 */
	private static final String STR_WT_SERVER = "wt.server.codebase";

	/**
	 * @param source
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	public static Source getSourceBean(LCSSourcingConfig source,
			LCSProduct productObj) throws WTException, IllegalAccessException,
			InvocationTargetException, IOException, NoSuchMethodException {

		String methodName = "getSourceBean()";
		logger.debug(methodName + "Extracting data from source "
				+ source.getName());
		Source sourceBean = ObjectFactory.createProductAPIStyleSource();

		// Generate json mapping from keys
		Map<String, String> jsonMappingSource = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_SOURCE);
		logger.debug(methodName + "jsonMappingSource: " + jsonMappingSource);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingSource = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_SYSTEM_SOURCE_KEY);
		logger.debug(methodName + "systemAttjsonMappingSource: "
				+ systemAttjsonMappingSource);

		BurberryAPIBeanUtil.getObjectData(BurConstant.SOURCE_IGNORE,
				sourceBean, source, BurConstant.SOURCE_ATT, jsonMappingSource,
				systemAttjsonMappingSource);
		getSupplierData(source, sourceBean);
		for (Map.Entry<String, String> mapEntry : jsonMappingSource.entrySet()) {
			String strAttKey = mapEntry.getKey();
			String strJsonKey = mapEntry.getValue();
			logger.debug(methodName + " source strAttKey " + strAttKey
					+ " source strJsonKey " + strJsonKey);
			if (BurConstant.PRIMARY.equalsIgnoreCase(strAttKey)) {
				BeanUtils.setProperty(sourceBean, strJsonKey,
						source.isPrimarySource() ? "Yes" : "No");
			}
		}
		// BurberryAPIBeanUtil.validateRequiredAttributes(sourceBean,
		// BurConstant.SOURCE_REQ);
		
		// BURBERRY-1485 New Attributes Additions post Sprint 8: Start
		if (systemAttjsonMappingSource.containsKey(BurConstant.BRANCHID)) {
			BeanUtils.setProperty(sourceBean,
					systemAttjsonMappingSource.get(BurConstant.BRANCHID),
					source.getBranchIdentifier());
		}
		// BURBERRY-1485 New Attributes Additions post Sprint 8: End
		
		return sourceBean;
	}

	/**
	 * @param source
	 * @param sourcePropertiesBean
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	public static void getSupplierData(LCSSourcingConfig source,
			Object sourcePropertiesBean) throws WTException,
			IllegalAccessException, InvocationTargetException, IOException,
			NoSuchMethodException {

		WTObject supplierObj = (WTObject) source.getValue(BurConstant.VENDOR);

		String methodName = "getSupplierData()";
		if (source.getValue(BurConstant.VENDOR) != null) {
			logger.debug(methodName + "Extracting Supplier data " + supplierObj
					+ " " + BurConstant.JSON_SOURCE_SUP);

			// Generate json mapping from keys
			Map<String, String> jsonMappingSupplier = BurberryAPIUtil
					.getJsonMapping(BurConstant.JSON_SOURCE_SUP);
			logger.debug(methodName + "jsonMappingSupplier: "
					+ jsonMappingSupplier);

			// Generate json mapping from System keys
			Map<String, String> systemAttjsonMappingSupplier = BurberryAPIUtil
					.getJsonMapping(BurConstant.JSON_SOURCE_SUP_SYSTEM_KEY);
			logger.debug(methodName + "systemAttjsonMappingSupplier: "
					+ systemAttjsonMappingSupplier);

			BurberryAPIBeanUtil.getObjectData(BurConstant.SUP_IGNORE,
					sourcePropertiesBean, supplierObj, BurConstant.SUP_ATT,
					jsonMappingSupplier, systemAttjsonMappingSupplier);

		}

	}

	/**
	 * @param boCommodityCode
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	public static CommodityCode getcommodityCodeBean(WTObject boCommodityCode)
			throws WTException, IllegalAccessException,
			InvocationTargetException, IOException, NoSuchMethodException {

		String methodName = "getcommodityCodeBean()";
		CommodityCode ccBean = ObjectFactory
				.createProductAPIStyleCommoditCode();

		if (boCommodityCode != null) {
			logger.debug(methodName + "Extracting data from commodityCode "
					+ boCommodityCode);

			// Generate json mapping from keys
			Map<String, String> jsonMappingCommodityCode = BurberryAPIUtil
					.getJsonMapping(BurConstant.JSON_COMMODIT_CODE);
			logger.debug(methodName + "jsonMappingCommodityCode: "
					+ jsonMappingCommodityCode);

			// Generate json mapping from System keys
			Map<String, String> systemAttjsonMappingCommodityCode = BurberryAPIUtil
					.getJsonMapping(BurConstant.JSON_COMMODIT_CODE_SYSTEM_KEY);
			logger.debug(methodName + "systemAttjsonMappingCommodityCode: "
					+ systemAttjsonMappingCommodityCode);

			BurberryAPIBeanUtil
					.getObjectData(BurConstant.CC_IGNORE, ccBean,
							boCommodityCode, BurConstant.CC_ATT,
							jsonMappingCommodityCode,
							systemAttjsonMappingCommodityCode);
			
			if (systemAttjsonMappingCommodityCode.containsKey(BurConstant.STR_IDA2A2)) {
				BeanUtils.setProperty(ccBean,
						systemAttjsonMappingCommodityCode.get(BurConstant.STR_IDA2A2),
						FormatHelper.getNumericObjectIdFromObject(boCommodityCode));
			}
			if (systemAttjsonMappingCommodityCode.containsKey(BurProductConstant.TYPE)) {
				BeanUtils.setProperty(ccBean,
						systemAttjsonMappingCommodityCode.get(BurProductConstant.TYPE),
						((LCSLifecycleManaged)boCommodityCode).getFlexType().getFullNameDisplay(true));
			}
			
		}
		// BurberryAPIBeanUtil.validateRequiredAttributes(ccBean,
		// BurConstant.CC_REQ);
		return ccBean;
	}

	/**
	 * @param placeholderObj
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws WTException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	public static ProdPlaceholder getPlaceholderBean(WTObject placeholderObj)
			throws IllegalAccessException, InvocationTargetException,
			WTException, IOException, NoSuchMethodException {

		String methodName = "getPlaceholderBean()";
		ProdPlaceholder placeholderBean = ObjectFactory
				.createProductAPIStylePlaceholder();

		if (placeholderObj != null) {
			logger.debug(methodName + "Extracting data from placeholder "
					+ placeholderObj);

			// Generate json mapping from keys
			Map<String, String> jsonMappingPlaceholder = BurberryAPIUtil
					.getJsonMapping(BurConstant.JSON_PLACEHOLDERKEY);
			logger.debug(methodName + "jsonMappingPlaceholder: "
					+ jsonMappingPlaceholder);

			// Generate json mapping from System keys
			Map<String, String> systemAttjsonMappingPlaceholder = BurberryAPIUtil
					.getJsonMapping(BurConstant.JSON_PLACEHOLDER_SYSTEM_KEY);
			logger.debug(methodName + "systemAttjsonMappingPlaceholder: "
					+ systemAttjsonMappingPlaceholder);

			BurberryAPIBeanUtil.getObjectData(BurConstant.PH_IGNORE,
					placeholderBean, placeholderObj, BurConstant.PH_ATT,
					jsonMappingPlaceholder, systemAttjsonMappingPlaceholder);
		}
		//BurberryAPIBeanUtil.validateRequiredAttributes(placeholderBean,
		//		BurConstant.PH_REQ);
		return placeholderBean;

	}

	/**
	 * @param spl
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws WTException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	public static Season getSeasonBean(LCSProductSeasonLink spl)
			throws IllegalAccessException, InvocationTargetException,
			WTException, IOException, NoSuchMethodException {

		LCSSeason season = (LCSSeason) VersionHelper.latestIterationOf(spl
				.getSeasonMaster());
		String methodName = "getSeasonBean()";
		logger.debug(methodName + "Extracting data from season "
				+ season.getName());

		Season seasonBean = ObjectFactory
				.createProductAPIStyleProductSeasonSeason();

		// Generate json mapping from keys
		Map<String, String> jsonMappingSeason = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_SEASONKEY);
		logger.debug(methodName + "jsonMappingSeason: " + jsonMappingSeason);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingSeason = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_SEASON_SYSTEM_KEY);
		logger.debug(methodName + "systemAttjsonMappingSeason: "
				+ systemAttjsonMappingSeason);

		BurberryAPIBeanUtil.getObjectData(BurConstant.SEASON_IGNORE,
				seasonBean, season, BurConstant.SEASON_ATT, jsonMappingSeason,
				systemAttjsonMappingSeason);
		for (Map.Entry<String, String> mapEntry : systemAttjsonMappingSeason.entrySet()) {
			String strAttKey = mapEntry.getKey();
			String strJsonKey = mapEntry.getValue();
			logger.debug(methodName + " season strAttKey " + strAttKey
					+ "  season strJsonKey " + strJsonKey);
			if (BurConstant.ACTIVE.equalsIgnoreCase(strAttKey)) {
				BeanUtils.setProperty(seasonBean, strJsonKey,
						season.isActive() ? "Yes" : "No");
			}
		}
		//BurberryAPIBeanUtil.validateRequiredAttributes(seasonBean,
		//		BurConstant.SEASON_REQ);
		return seasonBean;
	}

	/**
	 * @param sku
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws WTException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	public static Colourway getColourwayBean(LCSSKU sku)
			throws IllegalAccessException, InvocationTargetException,
			WTException, IOException, NoSuchMethodException {

		String codebase = WTProperties.getServerProperties().getProperty(
				STR_WT_SERVER);
		codebase = codebase.substring(0, codebase.lastIndexOf('/'));
		String methodName = "getColourwayBean()";
		logger.debug(methodName + "Extracting data from colourway "
				+ sku.getName());
		Colourway colourwayBean = ObjectFactory
				.createProductAPIStyleColourway();

		// Generate json mapping from keys
		Map<String, String> jsonMappingColourway = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_COLOURWAYKEY);
		logger.debug(methodName + "jsonMappingColourway: "
				+ jsonMappingColourway);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingColourway = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_COLOURWAY_SYSTEM_KEY);
		logger.debug(methodName + "systemAttjsonMappingColourway: "
				+ systemAttjsonMappingColourway);

		BurberryAPIBeanUtil.getObjectData(BurConstant.SKU_IGNORE,
				colourwayBean, sku, BurConstant.SKU_ATT, jsonMappingColourway,
				systemAttjsonMappingColourway);
		for (Map.Entry<String, String> mapEntry : jsonMappingColourway
				.entrySet()) {
			String strAttKey = mapEntry.getKey();
			String strJsonKey = mapEntry.getValue();
			logger.debug(methodName + " colourway strAttKey " + strAttKey
					+ " colourway strJsonKey " + strJsonKey);
			// BURBERRY-1401 RD-52 - Product Colour Image URL: Start
			if (BurConstant.PARTPRIMARYIMAGEURL.equalsIgnoreCase(strAttKey)) {
				String skuColourwayThumbnailImage = getskuColourThumbnail(sku);
				if (FormatHelper.hasContent(skuColourwayThumbnailImage)) {
					BeanUtils.setProperty(colourwayBean, strJsonKey, codebase
							+ skuColourwayThumbnailImage);
				}
			}
			// BURBERRY-1401 RD-52 - Product Colour Image URL: End
		}
		//BurberryAPIBeanUtil.validateRequiredAttributes(colourwayBean,
		//		BurConstant.SKU_REQ);
		return colourwayBean;

	}
	
	// BURBERRY-1401 RD-52 - Product Colour Image URL: Start
	/**
	 * Method to get SKU/Colour Thumbnail
	 * 
	 * @param sku
	 *            LCSSKU
	 * @return String thumbnail image
	 * @throws WTException
	 */
	private static String getskuColourThumbnail(LCSSKU sku) throws WTException {
		String methodName = "getskuColourwayThumbnailImage() ";
		String skuColourThumbnail = BurConstant.STRING_EMPTY;
		if (FormatHelper.hasContent(sku.getPartPrimaryImageURL())) {
			// Fix BURBERRY-1417: RD 57 - Image URL encoding to be handled in
			// API Extraction- Start
			skuColourThumbnail = FormatHelper.formatImageUrl(sku
					.getPartPrimaryImageURL());
			// Fix BURBERRY-1417: RD 57 - Image URL encoding to be handled in
			// API Extraction- End
			logger.debug(methodName + "SKU Thumbnail: " + skuColourThumbnail);
			return skuColourThumbnail;
		} else {
			// Get colour object from sku
			LCSColor skuColorObject = (LCSColor) sku.getValue("color");
			if (skuColorObject != null
					&& FormatHelper.hasContent(skuColorObject.getThumbnail())) {
				// Get the colour thumbnail
				// Fix BURBERRY-1417: RD 57 - Image URL encoding to be handled
				// in API Extraction- Start
				skuColourThumbnail = FormatHelper.formatImageUrl(skuColorObject
						.getThumbnail());
				// Fix BURBERRY-1417: RD 57 - Image URL encoding to be handled
				// in API Extraction- End
				logger.debug(methodName + "Colour Thumbnail: "
						+ skuColourThumbnail);
				return skuColourThumbnail;
			}
		}
		return skuColourThumbnail;
	}

	// BURBERRY-1401 RD-52 - Product Colour Image URL: End

	/**
	 * @param spl
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws WTException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	public static ProductSeason getProductSeasonBean(LCSProductSeasonLink spl)
			throws IllegalAccessException, InvocationTargetException,
			WTException, IOException, NoSuchMethodException {

		String methodName = "getProductSeasonBean()";
		logger.debug(methodName + "Extracting data from seasonProduct Link "
				+ spl);
		ProductSeason productSeasonBean = ObjectFactory
				.createProductAPIStyleProductSeason();

		// Generate json mapping from keys
		Map<String, String> jsonMappingProductSeason = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_PRODCUTSEASONKEY);
		logger.debug(methodName + "jsonMappingProductSeason: "
				+ jsonMappingProductSeason);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingProductSeason = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_PRODCUTSEASON_SYSTEM_KEY);
		logger.debug(methodName + "systemAttjsonMappingProductSeason: "
				+ systemAttjsonMappingProductSeason);

		BurberryAPIBeanUtil.getObjectData(BurConstant.PRODUCT_SEASON_IGNORE,
				productSeasonBean, spl, BurConstant.PRODUCT_SEASON_ATT,
				jsonMappingProductSeason, systemAttjsonMappingProductSeason);
		productSeasonBean
				.setProdPlaceholder(getPlaceholderBean(((LCSProductSeasonLink) spl)
						.getPlaceholder()));
		//BURBERRY-1485: Append Price Library Retail : Start
		BurberryProductAPIUtil.appendProposedRetailPrice(spl,productSeasonBean);
		//BURBERRY-1485: Append Price Library Retail : End
		
		if(spl.isSeasonRemoved()){
			BeanUtils.setProperty(productSeasonBean,BurConstant.JSON_CRUD_KEY,
					"DELETE");
		}
		//BurberryAPIBeanUtil.validateRequiredAttributes(productSeasonBean,
		//		BurConstant.PRODUCT_SEASON_REQ);
		return productSeasonBean;
	}
		
	/**
	 * @param BurberryAPIUtilmaterialObj
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	public static Material getMaterialBean(LCSMaterial materialObj)
			throws WTException, IllegalAccessException,
			InvocationTargetException, IOException, NoSuchMethodException {

		String methodName = "getMaterialBean()";
		Material materialBean = ObjectFactory.createProductAPIStyleMaterial();

		logger.debug(methodName + "Extracting data from Material "
				+ materialObj.getName());
		String codebase = WTProperties.getServerProperties().getProperty(
				STR_WT_SERVER);
		codebase = codebase.substring(0, codebase.lastIndexOf('/'));

		// Generate json mapping from keys
		Map<String, String> jsonMappingMaterial = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_MATERIALKEY);
		logger.debug(methodName + "jsonMappingMaterial: "
				+ jsonMappingMaterial);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingMaterial = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_MATERIAL_SYSTEM_KEY);
		logger.debug(methodName + "systemAttjsonMappingMaterial: "
				+ systemAttjsonMappingMaterial);

		BurberryAPIBeanUtil.getObjectData(BurConstant.MAT_IGNORE,
				materialBean, materialObj, BurConstant.MAT_ATT,
				jsonMappingMaterial, systemAttjsonMappingMaterial);
		
		//CR RD-022 JIRA 1368: Start
		// Check for thumb nail url
		if (systemAttjsonMappingMaterial
				.containsKey(BurConstant.PRIMARYIMAGEURL)) {
			if (FormatHelper.hasContent(materialObj.getPrimaryImageURL())) {
				//Fix BURBERRY-1417: RD 57 - Image URL encoding to be handled in API Extraction- Start
				BeanUtils.setProperty(materialBean,
						systemAttjsonMappingMaterial
								.get(BurConstant.PRIMARYIMAGEURL),
						(codebase + FormatHelper.formatImageUrl(materialObj.getPrimaryImageURL())));
				//Fix BURBERRY-1417: RD 57 - Image URL encoding to be handled in API Extraction- End
			}
		}
		// Get Material Flex Type name
		if (systemAttjsonMappingMaterial.containsKey(BurConstant.MATERIALTYPE)) {
			BeanUtils.setProperty(materialBean,
					systemAttjsonMappingMaterial.get(BurConstant.MATERIALTYPE),
					(materialObj.getFlexType().getFullNameDisplay()));
		}
		//CR RD-022 JIRA 1368: End
			
		//BurberryAPIBeanUtil.validateRequiredAttributes(materialBean,
		//		BurConstant.MAT_REQ);
		return materialBean;
	}

	/**
	 * @param productObj
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	public static Style getProductBean(LCSProduct productObj)
			throws WTException, IllegalAccessException,
			InvocationTargetException, IOException, NoSuchMethodException {

		final Style styleBean = ObjectFactory.createProductAPIStyle();
		String methodName = "getProductBean()";
		logger.debug(methodName + "Extracting data from product "
				+ productObj.getName());

		// Generate json mapping from keys
		Map<String, String> jsonMappingProduct = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_PRODUCTKEY);
		logger.debug(methodName + "jsonMappingProduct: " + jsonMappingProduct);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingProduct = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_PRODUCT_SYSTEM_KEY);
		logger.debug(methodName + "systemAttjsonMappingProduct: "
				+ systemAttjsonMappingProduct);

		BurberryAPIBeanUtil.getObjectData(BurConstant.STYLE_IGNORE, styleBean,
				productObj, BurConstant.STYLE_ATT, jsonMappingProduct,
				systemAttjsonMappingProduct);
		String codebase = WTProperties.getServerProperties().getProperty(
				STR_WT_SERVER);
		codebase = codebase.substring(0, codebase.lastIndexOf('/'));
		for (Map.Entry<String, String> mapEntry : jsonMappingProduct.entrySet()) {
			String strAttKey = mapEntry.getKey();
			String strJsonKey = mapEntry.getValue();
			logger.debug(methodName + " product strAttKey " + strAttKey
					+ " product strJsonKey " + strJsonKey);
			if (BurConstant.NAME.equalsIgnoreCase(strAttKey)) {
				BeanUtils.setProperty(styleBean, strJsonKey,
						productObj.getName());
			//Defect Fix: JIRA BURBERRY-1289: Start
			} else if (BurConstant.BUR_OPERATIONAL_CATEGORY
					.equalsIgnoreCase(strAttKey)) {
			//Defect Fix: JIRA BURBERRY-1289: End
				BeanUtils.setProperty(styleBean, strJsonKey,
						BurberryProductAPIUtil.getOperationalValue(
								(LCSProduct) productObj, strAttKey));
			} else if (BurConstant.BUR_BRAND.equalsIgnoreCase(strAttKey)) {
				BeanUtils.setProperty(styleBean, strJsonKey,
						BurberryProductAPIUtil.getBrandValue(productObj,
								strAttKey));
			} else if (BurConstant.PARTPRIMARYIMAGEURL
					.equalsIgnoreCase(strAttKey)) {
				if (FormatHelper
						.hasContent(productObj.getPartPrimaryImageURL())) {
					//Fix BURBERRY-1417: RD 57 - Image URL encoding to be handled in API Extraction- Start
					String imageUrl = FormatHelper.formatImageUrl(productObj.getPartPrimaryImageURL());
					logger.debug(methodName +"Formatted Image URL: <<"+imageUrl+">>");
					BeanUtils.setProperty(styleBean, strJsonKey, codebase + imageUrl);
					//Fix BURBERRY-1417: RD 57 - Image URL encoding to be handled in API Extraction- End
				}
			} else if (BurConstant.PART_TYPE_NAME.equalsIgnoreCase(strAttKey)) {
				BeanUtils.setProperty(styleBean, strJsonKey, productObj
						.getFlexType().getFullNameDisplay(false));
			}
		}
		//BurberryAPIBeanUtil.validateRequiredAttributes(styleBean,
		//		BurConstant.STYLE_REQ);
		return styleBean;
	}

	/**
	 * @param riskmgt
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	public static RiskManagement getRiskManagementBean(WTObject riskmgt)
			throws WTException, IllegalAccessException,
			InvocationTargetException, IOException, NoSuchMethodException {

		String methodName = "getRiskManagementBean()";
		logger.debug(methodName + "Extracting data from risk management "
				+ riskmgt);

		RiskManagement riskManagementBean = ObjectFactory
				.createProductAPIStyleRiskManagement();

		// Generate json mapping from keys
		Map<String, String> jsonMappingRMGT = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_RMGT);
		logger.debug(methodName + "jsonMappingRMGT: " + jsonMappingRMGT);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingRMGT = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_RMGT_SYSTEM_KEY);
		logger.debug(methodName + "systemAttjsonMappingRMGT: "
				+ systemAttjsonMappingRMGT);

		BurberryAPIBeanUtil.getObjectData(BurConstant.RM_IGNORE,
				riskManagementBean, riskmgt, BurConstant.RM_ATT,
				jsonMappingRMGT, systemAttjsonMappingRMGT);
		if (jsonMappingRMGT.containsKey(BurConstant.STR_IDA2A2)) {
			BeanUtils.setProperty(riskManagementBean,
					jsonMappingRMGT.get(BurConstant.STR_IDA2A2),
					FormatHelper.getNumericObjectIdFromObject(riskmgt));
		}
		//BurberryAPIBeanUtil.validateRequiredAttributes(riskManagementBean,
		//		BurConstant.RM_REQ);
		return riskManagementBean;
	}

	/**
	 * @param skuseasonLink
	 * @param skuSeasonName
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws WTException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	public static ColourwaySeason getColourwaySeasonBean(
			LCSSKUSeasonLink skuseasonLink, String skuSeasonName)
			throws IllegalAccessException, InvocationTargetException,
			WTException, IOException, NoSuchMethodException {

		String methodName = "getColourwaySeasonBean()";
		logger.debug(methodName + "Extracting data from colourway season "
				+ skuseasonLink);
		ColourwaySeason colourwaySeasonBean = ObjectFactory
				.createProductAPIStyleColourwayColourwaySeason();

		// Generate json mapping from keys
		Map<String, String> jsonMappingColourwaySeason = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_COLOURWAYSEASONKEY);
		logger.debug(methodName + "jsonMappingColourwaySeason: "
				+ jsonMappingColourwaySeason);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingColourwaySeason = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_COLOURWAYSEASON_SYSTEM_KEY);
		logger.debug(methodName + "systemAttjsonMappingColourwaySeason: "
				+ systemAttjsonMappingColourwaySeason);

		BurberryAPIBeanUtil
				.getObjectData(BurConstant.SKU_SEASON_IGNORE,
						colourwaySeasonBean, skuseasonLink,
						BurConstant.SKU_SEASON_ATT, jsonMappingColourwaySeason,
						systemAttjsonMappingColourwaySeason);
		// Loop through each key and json key
		if (jsonMappingColourwaySeason
				.containsKey(BurConstant.COLOURWAY_SEASON_ATT_BURSEASON)) {
			BeanUtils.setProperty(colourwaySeasonBean,
					jsonMappingColourwaySeason
							.get(BurConstant.COLOURWAY_SEASON_ATT_BURSEASON),
					skuSeasonName);
		}

		if(skuseasonLink.isSeasonRemoved()){
			BeanUtils.setProperty(colourwaySeasonBean,BurConstant.JSON_CRUD_KEY,
					"DELETE");
		}
		//BurberryAPIBeanUtil.validateRequiredAttributes(colourwaySeasonBean,
		//		BurConstant.SKU_SEASON_REQ);
		return colourwaySeasonBean;
	}

	/**
	 * Method to get Document Bean.
	 * 
	 * @param document
	 *            LCSDocument
	 * @param documentBean
	 * @param docSeasonName
	 * @return Document Bean
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
	public static void getDocumentBean(LCSDocument document,
			Object documentBean, String docSeasonName)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException, PropertyVetoException {
		String methodName = "getDocumentBean() ";
		logger.debug(methodName + "Extracting data from Document: "
				+ document.getName());

		// Setting codebase path
		String codebase = WTProperties.getServerProperties().getProperty(
				STR_WT_SERVER);
		codebase = codebase.substring(0, codebase.lastIndexOf('/'));
		// Generate json mapping from keys
		Map<String, String> jsonMappingDocument = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_DOCUMENT_ATT);
		logger.debug(methodName + "jsonMappingDocument: " + jsonMappingDocument);
		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingDocument = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_DOCUMENT_SYSTEM_ATT);
		logger.debug(methodName + "systemAttjsonMappingDocument: "
				+ systemAttjsonMappingDocument);
		// Get all the object data
		BurberryAPIBeanUtil.getObjectData(BurConstant.DOCUMENT_IGNORE,
				documentBean, document, BurConstant.DOCUMENT_ATT,
				jsonMappingDocument, systemAttjsonMappingDocument);
		// Get Thumbnail location data
		if (jsonMappingDocument.containsKey(BurConstant.THUMBNAIL_LOCATION)) {
			if (FormatHelper.hasContent(document.getThumbnailLocation())) {
				BeanUtils.setProperty(documentBean, jsonMappingDocument.get(BurConstant.THUMBNAIL_LOCATION),
								(codebase + FormatHelper
										.formatImageUrl(document
												.getThumbnailLocation())));
			}
		}
		// Get Document Type name
		if (jsonMappingDocument.containsKey(BurConstant.DOCUMENT_TYPE)) {
			BeanUtils.setProperty(documentBean,
					jsonMappingDocument.get(BurConstant.DOCUMENT_TYPE),
					(document.getFlexType().getFullNameDisplay()));
		}
		// Get Primary content URL location
		if (jsonMappingDocument.containsKey(BurConstant.PRIMARY_CONTENT_URL)) {
			LCSDocument refdocument = (LCSDocument) ContentHelper.service
					.getContents(document);
			// Get application data from document
			ApplicationData primaryAppData = (ApplicationData) ContentHelper
					.getPrimary(refdocument);
			BeanUtils.setProperty(documentBean, jsonMappingDocument
					.get(BurConstant.PRIMARY_CONTENT_URL),
					BurberryProductAPIUtil.getStaticURL(primaryAppData,
							refdocument));
		}
		// Get checkin comment from application data
		if (jsonMappingDocument.containsKey(BurConstant.CHECK_IN_COMMENT)) {
			BeanUtils.setProperty(documentBean,
					jsonMappingDocument.get(BurConstant.CHECK_IN_COMMENT),
					BurberryAPIBeanUtil.getDocumentCheckinComment(document));
		}
		// Get Season Name for Document
		if (jsonMappingDocument.containsKey(BurConstant.DOCUMENT_SEASON)) {
			BeanUtils.setProperty(documentBean,
					jsonMappingDocument.get(BurConstant.DOCUMENT_SEASON),
					docSeasonName);
		}
		// CR-R32: Start
		// Get Document Branch Iteration Id
		if (jsonMappingDocument.containsKey(BurConstant.BRANCHID)) {
			BeanUtils.setProperty(documentBean,
					jsonMappingDocument.get(BurConstant.BRANCHID),
					document.getBranchIdentifier());
		}
		// CR-R32: End

	}

	/**
	 * Method to Set Removed Image Page Bean
	 * 
	 * @param strRemovedImagePageName
	 *            String
	 * @param removedImagePageBean
	 *            Bean
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	// CR R26: Handle Remove Object Customisation : Start

	public static void getRemovedImagePageBean(Object removedImagePageBean,
			String jsonKey, String strRemovedImagePageName)
			throws IllegalAccessException, InvocationTargetException {
		// Set on the Bean Object
		BeanUtils.setProperty(removedImagePageBean, jsonKey,
				strRemovedImagePageName);
		BeanUtils.setProperty(removedImagePageBean, BurConstant.JSON_CRUD_KEY,
				"DELETE");

	}
	// CR R26: Handle Remove Object Customisation : End
}
