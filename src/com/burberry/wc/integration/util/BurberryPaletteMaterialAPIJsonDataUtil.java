package com.burberry.wc.integration.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTProperties;

import com.burberry.wc.integration.palettematerialapi.bean.*;
import com.burberry.wc.integration.palettematerialapi.constant.*;
import com.lcs.wc.color.*;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;

/**
 * A Helper class to handle JSON data transform activity. Class contain several
 * method to handle transform of object data putting it to the beans.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryPaletteMaterialAPIJsonDataUtil {

	/**
	 * logger.
	 */
	public static final Logger logger = Logger
			.getLogger(BurberryPaletteMaterialAPIJsonDataUtil.class);

	/**
	 * Private constructor.
	 */
	private BurberryPaletteMaterialAPIJsonDataUtil() {

	}

	/**
	 * Method to get Material Bean.
	 * 
	 * @param materialObject
	 *            LCSMaterial
	 * @return Material Bean
	 * @throws WTException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 */
	public static Material getMaterialBean(LCSMaterial materialObject)
			throws WTException, IllegalAccessException,
			InvocationTargetException, IOException, NoSuchMethodException {

		String methodName = "getMaterialBean() ";
		// Initialisation
		final Material materialBean = new Material();
		logger.debug(methodName + "Extracting data from Material: "
				+ materialObject.getName());
		// Setting codebase path
		String codebase = WTProperties.getServerProperties().getProperty(
				"wt.server.codebase");
		codebase = codebase.substring(0, codebase.lastIndexOf('/'));

		// Generate json mapping from keys
		Map<String, String> jsonMappingMaterial = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_MATERIAL_ATT);
		logger.debug(methodName + "jsonMappingMaterial: " + jsonMappingMaterial);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingMaterial = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_MATERIAL_SYSTEM_ATT);
		logger.debug(methodName + "systemAttjsonMappingMaterial: "
				+ systemAttjsonMappingMaterial);

		// Get all the object data
		BurberryAPIBeanUtil.getObjectData(
				BurPaletteMaterialConstant.MATERIAL_IGNORE, materialBean,
				materialObject, BurPaletteMaterialConstant.MATERIAL_ATT,
				jsonMappingMaterial, systemAttjsonMappingMaterial);

		//CR RD-022 JIRA 1368: Start
		// Check for thumb nail url
		if (systemAttjsonMappingMaterial
				.containsKey(BurConstant.PRIMARYIMAGEURL)) {
			if (FormatHelper.hasContent(materialObject.getPrimaryImageURL())) {
				//Fix BURBERRY-1417: RD 57 - Image URL encoding to be handled in API Extraction-Start
				BeanUtils.setProperty(materialBean,
						systemAttjsonMappingMaterial
								.get(BurConstant.PRIMARYIMAGEURL),
						(codebase + FormatHelper.formatImageUrl(materialObject.getPrimaryImageURL())));
				//Fix BURBERRY-1417:RD 57 - Image URL encoding to be handled in API Extraction- End
			}
		}
		// Get Material Flex Type name
		if (systemAttjsonMappingMaterial.containsKey(BurConstant.MATERIALTYPE)) {
			BeanUtils.setProperty(materialBean,
					systemAttjsonMappingMaterial.get(BurConstant.MATERIALTYPE),
					(materialObject.getFlexType().getFullNameDisplay()));
		}
		//CR RD-022 JIRA 1368: End
		
		// Validate required attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(materialBean,
		// BurPaletteMaterialConstant.MATERIAL_REQ);
		// Return Statement
		return materialBean;
	}

	/**
	 * Method to get Material Supplier Bean.
	 * 
	 * @param matSupplier
	 *            LCSMaterialSupplier
	 * @return MaterialSupplier Bean
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
	 */
	public static MaterialSupplier getMaterialSupplierBean(
			LCSMaterialSupplier matSupplier) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException {

		String methodName = "getMaterialSupplierBean() ";
		// Initialisation
		final MaterialSupplier materialSupplierBean = new MaterialSupplier();
		logger.debug(methodName + "Extracting data from Material Supplier: "
				+ matSupplier.getName());

		// Generate json mapping from keys
		Map<String, String> jsonMappingMatSup = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_MATERIAL_SUPPLIER_ATT);
		logger.debug(methodName + "jsonMappingMatSup: " + jsonMappingMatSup);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingMatSup = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_MATERIAL_SUPPLIER_SYSTEM_ATT);
		logger.debug(methodName + "systemAttjsonMappingMatSup: "
				+ systemAttjsonMappingMatSup);

		// Get all the object data
		BurberryAPIBeanUtil.getObjectData(
				BurPaletteMaterialConstant.MATERIAL_SUPPLIER_IGNORE,
				materialSupplierBean, matSupplier,
				BurPaletteMaterialConstant.MATERIAL_SUPPLIER_ATT,
				jsonMappingMatSup, systemAttjsonMappingMatSup);

		if (jsonMappingMatSup
				.containsKey(BurPaletteMaterialConstant.MATERIAL_SUPPLIER_NAME)
				&& (matSupplier.getSupplierMaster() != null)) {
			BeanUtils.setProperty(materialSupplierBean, jsonMappingMatSup
					.get(BurPaletteMaterialConstant.MATERIAL_SUPPLIER_NAME),
					matSupplier.getSupplierMaster().getSupplierName());
		}
		// Validate required attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(materialSupplierBean,
		// BurPaletteMaterialConstant.MATERIAL_SUPPLIER_REQ);
		// Return Statement
		return materialSupplierBean;
	}

	/**
	 * Method to get Material Colour Bean.
	 * 
	 * @param matColour
	 *            LCSMaterialColor
	 * @return Material Colour
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
	 */
	public static MaterialColour getMaterialColourBean(
			LCSMaterialColor matColour) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException {
		String methodName = "getMaterialColourBean() ";
		// Initialisation
		final MaterialColour materialColourBean = new MaterialColour();
		logger.debug(methodName + "Extracting data from Material Color: "
				+ matColour.getName());

		// Generate json mapping from keys
		Map<String, String> jsonMappingMatColour = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_MATERIAL_COLOUR_ATT);
		logger.debug(methodName + "jsonMappingMatColour: "
				+ jsonMappingMatColour);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingMatColour = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_MATERIAL_COLOUR_SYSTEM_ATT);
		logger.debug(methodName + "systemAttjsonMappingMatColour: "
				+ systemAttjsonMappingMatColour);

		// Get all the object data
		BurberryAPIBeanUtil.getObjectData(
				BurPaletteMaterialConstant.MATERIAL_COLOUR_IGNORE,
				materialColourBean, matColour,
				BurPaletteMaterialConstant.MATERIAL_COLOUR_ATT,
				jsonMappingMatColour, systemAttjsonMappingMatColour);

		if (jsonMappingMatColour
				.containsKey(BurPaletteMaterialConstant.MATERIAL_COLOUR_NAME)
				&& FormatHelper.hasContent(matColour.getColor().getColorName())) {
			BeanUtils.setProperty(materialColourBean, jsonMappingMatColour
					.get(BurPaletteMaterialConstant.MATERIAL_COLOUR_NAME),
					matColour.getColor().getColorName());
		}
		// Validate required attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(materialColourBean,
		// BurPaletteMaterialConstant.MATERIAL_COLOUR_REQ);
		// Return Statement
		return materialColourBean;
	}

	/**
	 * Method to get Supplier Bean.
	 * 
	 * @param supplierObject
	 *            LCSSupplier
	 * @return Supplier Bean
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
	 */
	public static Supplier getSupplierBean(LCSSupplier supplierObject)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {

		String methodName = "getSupplierBean() ";
		// Initialisation
		final Supplier supplierBean = new Supplier();
		logger.debug(methodName + "Extracting data from Supplier: "
				+ supplierObject.getName());

		// Generate json mapping from keys
		Map<String, String> jsonMappingSupplier = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_SUPPLIER_ATT);
		logger.debug(methodName + "jsonMappingSupplier: " + jsonMappingSupplier);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingSupplier = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_SUPPLIER_SYSTEM_ATT);
		logger.debug(methodName + "systemAttjsonMappingSupplier: "
				+ systemAttjsonMappingSupplier);

		// Get all the object data
		BurberryAPIBeanUtil.getObjectData(
				BurPaletteMaterialConstant.SUPPLIER_IGNORE, supplierBean,
				supplierObject, BurPaletteMaterialConstant.SUPPLIER_ATT,
				jsonMappingSupplier, systemAttjsonMappingSupplier);

		// Validate required attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(supplierBean,
		// BurPaletteMaterialConstant.SUPPLIER_REQ);
		// Return Statement
		return supplierBean;
	}

	/**
	 * Method to get Palette Bean.
	 * 
	 * @param palette
	 *            LCSPalette
	 * @return Palette Bean
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
	 */
	public static Palette getPaletteBean(LCSPalette palette)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		String methodName = "getPaletteBean() ";
		// Initialisation
		final Palette paletteBean = new Palette();
		logger.debug(methodName + "Extracting data from Palette: "
				+ palette.getName());

		// Generate json mapping from keys
		Map<String, String> jsonMappingPalette = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_PALETTE_ATT);
		logger.debug(methodName + "jsonMappingPalette: " + jsonMappingPalette);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingPalette = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_PALETTE_SYSTEM_ATT);
		logger.debug(methodName + "systemAttjsonMappingPalette: "
				+ systemAttjsonMappingPalette);

		// Get all the object data
		BurberryAPIBeanUtil.getObjectData(
				BurPaletteMaterialConstant.PALETTE_IGNORE, paletteBean,
				palette, BurPaletteMaterialConstant.PALETTE_ATT,
				jsonMappingPalette, systemAttjsonMappingPalette);

		// Validate required attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(paletteBean,
		// BurPaletteMaterialConstant.PALETTE_REQ);
		// Return Statement
		return paletteBean;
	}

	/**
	 * Method to get Risk Management Bean.
	 * 
	 * @param riskMgtObject
	 *            LCSMOAObject
	 * @return Risk Management
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
	 */
	public static RiskManagement getRiskManagementBean(
			LCSMOAObject riskMgtObject) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException {

		String methodName = "getRiskManagementBean() ";
		// Initialisation
		final RiskManagement riskManagementBean = new RiskManagement();
		logger.debug(methodName + "Extracting data from Risk Management: "
				+ riskMgtObject.getName());

		// Generate json mapping from keys
		Map<String, String> jsonMappingRiskMgt = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_RISK_MANAGEMENT_ATT);
		logger.debug(methodName + "jsonMappingRiskMgt: " + jsonMappingRiskMgt);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingRiskMgt = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_RISK_MANAGEMENT_SYSTEM_ATT);
		logger.debug(methodName + "systemAttjsonMappingRiskMgt: "
				+ systemAttjsonMappingRiskMgt);

		// Get all the object data
		BurberryAPIBeanUtil.getObjectData(
				BurPaletteMaterialConstant.RISK_MANAGEMENT_IGNORE,
				riskManagementBean, riskMgtObject,
				BurPaletteMaterialConstant.RISK_MANAGEMENT_ATT,
				jsonMappingRiskMgt, systemAttjsonMappingRiskMgt);

		// Loop through each key and json key
		if (jsonMappingRiskMgt.containsKey(BurConstant.STR_IDA2A2)) {
			BeanUtils.setProperty(riskManagementBean,
					jsonMappingRiskMgt.get(BurConstant.STR_IDA2A2),
					FormatHelper.getNumericObjectIdFromObject((riskMgtObject)));
		}
		// Validate required attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(riskManagementBean,
		// BurPaletteMaterialConstant.RISKMANAGEMENT_REQ);
		// Return Statement
		return riskManagementBean;
	}

	/**
	 * Method to get Colour Bean.
	 * 
	 * @param color
	 *            LCSColour
	 * @return Colour Bean
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
	 */
	public static Colour getColourBean(LCSColor color)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		String methodName = "getColourBean() ";
		// Initialisation
		final Colour colourBean = new Colour();

		String codebase = WTProperties.getServerProperties().getProperty(
				"wt.server.codebase");
		codebase = codebase.substring(0, codebase.lastIndexOf('/'));
		logger.debug(methodName + "Extracting data from Colour: "
				+ color.getName());
		// Generate json mapping from keys
		Map<String, String> jsonMappingColour = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_COLOUR_ATT);
		logger.debug(methodName + "jsonMappingColour: " + jsonMappingColour);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingColour = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_COLOUR_SYSTEM_ATT);
		logger.debug(methodName + "systemAttjsonMappingColour: "
				+ systemAttjsonMappingColour);

		// Get all the object data
		BurberryAPIBeanUtil.getObjectData(
				BurPaletteMaterialConstant.COLOUR_IGNORE, colourBean, color,
				BurPaletteMaterialConstant.COLOUR_ATT, jsonMappingColour,
				systemAttjsonMappingColour);

		if (jsonMappingColour
				.containsKey(BurPaletteMaterialConstant.COLOR_HEXIDECIMAL)
				&& (color.getColorHexidecimalValue() != null)) {
			BeanUtils.setProperty(colourBean, jsonMappingColour
					.get(BurPaletteMaterialConstant.COLOR_HEXIDECIMAL), color
					.getColorHexidecimalValue());
		}
		// Setting Colour Thumbnail - CR R11 Changes
		if (jsonMappingColour
				.containsKey(BurPaletteMaterialConstant.COLOR_THUMBNAIL)
				&& FormatHelper.hasContent(color.getThumbnail())) {
			BeanUtils.setProperty(colourBean, jsonMappingColour
					.get(BurPaletteMaterialConstant.COLOR_THUMBNAIL), codebase
					+ color.getThumbnail());
		}
		// Validate required attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(colourBean,
		// BurPaletteMaterialConstant.COLOUR_REQ);
		// Return Statement
		return colourBean;
	}

	/**
	 * Method to get Palette Material Colour Bean.
	 * 
	 * @param palMatColourLink
	 *            LCSPaletteMaterialColorLink
	 * @return PaletteMaterialColour Bean
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
	 */
	public static PaletteInfo getPaletteInfoBean(
			LCSPaletteMaterialColorLink palMatColourLink)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		String methodName = "getPaletteInfoBean() ";

		// Initialisation
		final PaletteInfo paletteInfoBean = new PaletteInfo();
		logger.debug(methodName + "Extracting data from Palette Info: "
				+ palMatColourLink);

		// Generate json mapping from keys
		Map<String, String> jsonMappingPatMatColour = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_PAL_MAT_COLOUR_ATT);
		logger.debug(methodName + "jsonMappingPatMatColour: "
				+ jsonMappingPatMatColour);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingPatMatColour = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_PAL_MAT_COLOUR_SYSTEM_ATT);
		logger.debug(methodName + "systemAttjsonMappingPatMatColour: "
				+ systemAttjsonMappingPatMatColour);

		// Get all the object data
		BurberryAPIBeanUtil.getObjectData(
				BurPaletteMaterialConstant.PAL_MAT_COLOUR_IGNORE,
				paletteInfoBean, palMatColourLink,
				BurPaletteMaterialConstant.PAL_MAT_COLOUR_ATT,
				jsonMappingPatMatColour, systemAttjsonMappingPatMatColour);

		if (jsonMappingPatMatColour
				.containsKey(BurPaletteMaterialConstant.PALETTE_NAME)
				&& (palMatColourLink.getPalette().getName() != null)) {
			BeanUtils.setProperty(paletteInfoBean, jsonMappingPatMatColour
					.get(BurPaletteMaterialConstant.PALETTE_NAME),
					palMatColourLink.getPalette().getName());
		}
		// Validate required attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(paletteInfoBean,
		// BurPaletteMaterialConstant.PAL_MAT_COLOUR_REQ);

		// Return Statement
		return paletteInfoBean;
	}

	/**
	 * Method to get Yarn Details.
	 * 
	 * @param yarnDtl
	 *            LCSMOAObject
	 * @return YarnDetail
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
	 */
	public static YarnDetail getYarnDetailBean(LCSMOAObject yarnDtl)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		String methodName = "getYarnDetailBean() ";
		// Initialisation
		final YarnDetail yarnDetailBean = new YarnDetail();
		logger.debug(methodName + "Extracting data from Yarn Details: "
				+ yarnDtl.getName());

		// Generate json mapping from keys
		Map<String, String> jsonMappingYarnDtl = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_YARN_DETAIL_ATT);
		logger.debug(methodName + "jsonMappingYarnDtl: " + jsonMappingYarnDtl);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingYarnDtl = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_YARN_DETAIL_SYSTEM_ATT);
		logger.debug(methodName + "systemAttjsonMappingYarnDtl: "
				+ systemAttjsonMappingYarnDtl);

		// Get all the object data
		BurberryAPIBeanUtil.getObjectData(
				BurPaletteMaterialConstant.YARN_DETAIL_IGNORE, yarnDetailBean,
				yarnDtl, BurPaletteMaterialConstant.YARN_DETAIL_ATT,
				jsonMappingYarnDtl, systemAttjsonMappingYarnDtl);

		// Get the unique id and set on bean
		if (jsonMappingYarnDtl.containsKey(BurConstant.STR_IDA2A2)) {
			BeanUtils.setProperty(yarnDetailBean,
					jsonMappingYarnDtl.get(BurConstant.STR_IDA2A2),
					FormatHelper.getNumericObjectIdFromObject((yarnDtl)));
		}

		// Validate required attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(yarnDetailBean,
		// BurPaletteMaterialConstant.YARN_DETAIL_REQ);
		// Return Statement
		return yarnDetailBean;
	}

	/**
	 * Method to get Material Pricing Entry Bean Data.
	 * 
	 * @param matPricingEntry
	 *            MaterialPricingEntry
	 * @return MaterialPricingEntry
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
	 */
	public static MaterialPricingEntry getMaterialPricingEntryBean(
			com.lcs.wc.material.MaterialPricingEntry matPricingEntry)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		String methodName = "getPricingEntryBean() ";
		// Initialisation
		final MaterialPricingEntry matPricingBean = new MaterialPricingEntry();
		logger.debug(methodName + "Extracting data from MaterialPricingEntry: "
				+ matPricingEntry);

		/*
		 * // Get all the attributes keys final StringTokenizer attKeys = new
		 * StringTokenizer(
		 * BurPaletteMaterialConstant.JSON_MATERIAL_PRICING_ENTRY_ATT,
		 * BurConstant.STRING_COMMA);
		 * 
		 * // Generate json mapping from keys Map<String, String>
		 * jsonMappingMatPricingEntry = BurberryAPIUtil
		 * .getJsonMapping(attKeys); logger.debug(methodName +
		 * "jsonMappingMatPricingEntry: " + jsonMappingMatPricingEntry);
		 */

		// Generate json mapping from keys
		Map<String, String> jsonMappingMatPricingEntry = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_MATERIAL_PRICING_ENTRY_ATT);
		logger.debug(methodName + "jsonMappingMatPricingEntry: "
				+ jsonMappingMatPricingEntry);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingMatPricingEntry = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_MATERIAL_PRICING_ENTRY_SYSTEM_ATT);
		logger.debug(methodName + "systemAttjsonMappingMatPricingEntry: "
				+ systemAttjsonMappingMatPricingEntry);

		// Get all the object data
		BurberryAPIBeanUtil
				.getObjectData(
						BurPaletteMaterialConstant.MAT_PRICING_ENTRY_IGNORE,
						matPricingBean, matPricingEntry,
						BurPaletteMaterialConstant.MAT_PRICING_ENTRY_ATT,
						jsonMappingMatPricingEntry,
						systemAttjsonMappingMatPricingEntry);

		// Loop through each key and json key
		for (Map.Entry<String, String> mapEntry : jsonMappingMatPricingEntry
				.entrySet()) {
			String strAttKey = mapEntry.getKey();
			String strJsonKey = mapEntry.getValue();
			logger.debug(methodName + "Material-Pricing-Entry AttKey="
					+ strAttKey);
			logger.debug(methodName + "Material-Pricing-Entry JsonKey="
					+ strJsonKey);
			if (BurPaletteMaterialConstant.MAT_PRICING_IN_DATE
					.equalsIgnoreCase(strAttKey)
					&& (matPricingEntry.getInDate() != null)) {
				// Defect Fix: Start
				BeanUtils.setProperty(matPricingBean, strJsonKey,
						(BurberryDataUtil.getValueForDate(matPricingEntry
								.getInDate())));
			} else if (BurPaletteMaterialConstant.MAT_PRICING_OUT_DATE
					.equalsIgnoreCase(strAttKey)
					&& (matPricingEntry.getOutDate() != null)) {
				BeanUtils.setProperty(matPricingBean, strJsonKey,
						(BurberryDataUtil.getValueForDate(matPricingEntry
								.getOutDate())));
				// Defect Fix: End
			}// Get Colour Name is exists
			else if (BurPaletteMaterialConstant.MAT_COLOUR_REFERENCE
					.equalsIgnoreCase(strAttKey)
					&& (matPricingEntry.getMaterialColor() != null)) {
				BeanUtils.setProperty(matPricingBean, strJsonKey,
						(matPricingEntry.getMaterialColor().getColor()
								.getName()));
			}
			// Get the unique id and set on bean
			else if (BurConstant.STR_IDA2A2.equalsIgnoreCase(strAttKey)) {
				BeanUtils.setProperty(matPricingBean, strJsonKey, FormatHelper
						.getNumericObjectIdFromObject((matPricingEntry)));
			}
		}

		// Validate required attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(matPricingBean,
		// BurPaletteMaterialConstant.MAT_PRICING_ENTRY_REQ);
		// Return Statement
		return matPricingBean;
	}

	/**
	 * Method to check Material type.
	 * 
	 * @param materialObj
	 *            materialObj
	 * @param strAttKey
	 *            strAttKey
	 * @return Object
	 * @throws WTException
	 *             Exception
	 */
	public static Object getMaterialTypeValue(LCSMaterial materialObj,
			String strAttKey) throws WTException {
		String value = null;
		String methodName = "getTypeValue() ";
		logger.debug(methodName + "FlexType: "
				+ materialObj.getFlexType().getFullName(true));
		if (!materialObj.getFlexType().getFullName(true)
				.contains(BurConstant.SOLE_TYPE)) {
			value = BurberryDataUtil.getData(materialObj, strAttKey, null);
		} else {
			value = BurberryDataUtil.getData(materialObj,
					BurConstant.BUR_MAT_TYPE, null);
		}
		logger.debug(methodName + " Material Type value returned: " + value);
		// Return statement
		return value;
	}

	/**
	 * Method to Material Price Management Bean Data.
	 * 
	 * @param matPriceMgmt
	 *            material price Management
	 * @return MatPriceManagement
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
	 */

	public static MatPriceManagement getMaterialPriceManagementBean(
			LCSMOAObject matPriceMgmt) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException {

		String methodName = "getMaterialPriceManagementBean() ";
		// Initialisation
		final MatPriceManagement matPriceMgmtBean = new MatPriceManagement();
		logger.debug(methodName
				+ "Extracting data from Material Price Management Details: "
				+ matPriceMgmt.getName());

		// Generate json mapping from keys
		Map<String, String> jsonMappingMatPriceMgmt = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_MATERIAL_PRICE_MGMT_ATT);

		logger.debug(methodName + "jsonMappingMatPriceMgmt: "
				+ jsonMappingMatPriceMgmt);

		// Generate json mapping from System keys
		Map<String, String> sysAttjsonMappingMatPriceMgmt = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_MATERIAL_PRICE_MGMT_SYSTEM_ATT);
		logger.debug(methodName + "sysAttjsonMappingMatPriceMgmt: "
				+ sysAttjsonMappingMatPriceMgmt);

		// Get all the object data
		BurberryAPIBeanUtil.getObjectData(
				BurPaletteMaterialConstant.MATERIAL_PRICE_MGMT_IGNORE,
				matPriceMgmtBean, matPriceMgmt,
				BurPaletteMaterialConstant.MATERIAL_PRICE_MGMT_ATT,
				jsonMappingMatPriceMgmt, sysAttjsonMappingMatPriceMgmt);

		// Get the unique id and set on bean
		if (jsonMappingMatPriceMgmt.containsKey(BurConstant.STR_IDA2A2)) {
			BeanUtils.setProperty(matPriceMgmtBean,
					jsonMappingMatPriceMgmt.get(BurConstant.STR_IDA2A2),
					FormatHelper.getNumericObjectIdFromObject((matPriceMgmt)));
		}

		// Validate required attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(matPriceMgmtBean,
		// BurPaletteMaterialConstant.MATERIAL_PRICE_MGMT_REQ);
		// Return Statement
		return matPriceMgmtBean;
	}

	/**
	 * Method to Set Removed Palette Info Bean
	 * 
	 * @param strRemovedPaletteName
	 *            Palette Name
	 * @param removedPaletteBean
	 * @return Palette Info Bean
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 */
	// CR R26: Handle Remove Palette Material Colour Customisation : Start
	public static void getRemovedPaletteBean(String strRemovedPaletteName,
			Object removedPaletteBean) throws IllegalAccessException,
			InvocationTargetException {
		// Set on the Bean Object
		BeanUtils.setProperty(removedPaletteBean,
				BurPaletteMaterialConstant.PALETTE_NAME, strRemovedPaletteName);
		BeanUtils.setProperty(removedPaletteBean, BurConstant.JSON_CRUD_KEY,
				"DELETE");
	}

	// CR R26: Handle Remove Palette Material Colour Customisation : End

	/**
	 * Method to Set Removed Risk Management Bean
	 * 
	 */
	// CR R26: Handle Remove MOA Object Customisation : Start
	public static void getRemovedMOABean(Object removedObjectBean,
			String jsonKey, String strMOAObjectId)
			throws IllegalAccessException, InvocationTargetException {
		// Set on the Bean Objects
		BeanUtils.setProperty(removedObjectBean, jsonKey, strMOAObjectId);
		BeanUtils.setProperty(removedObjectBean, BurConstant.JSON_CRUD_KEY,
				"DELETE");
	}
	// CR R26: Handle Remove MOA Object Customisation : End

	/**
	 * @param supplierObj
	 * @return
	 * @throws IOException 
	 * @throws WTException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public static PaletteSupplier getPaletteSupplierBean(LCSSupplier supplierObj) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, WTException, IOException {
		
		String methodName = "getPaletteSupplierBean() ";
		// Initialisation
		final PaletteSupplier supplierBean = new PaletteSupplier();
		logger.debug(methodName + "Extracting data from Supplier: "
				+ supplierObj.getName());

		// Generate json mapping from keys
		Map<String, String> jsonMappingSupplier = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_PAL_SUPPLIER_ATT);
		logger.debug(methodName + "jsonMappingSupplier: " + jsonMappingSupplier);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingSupplier = BurberryAPIUtil
				.getJsonMapping(BurPaletteMaterialConstant.JSON_PAL_SUPPLIER_SYSTEM_ATT);
		logger.debug(methodName + "systemAttjsonMappingSupplier: "
				+ systemAttjsonMappingSupplier);
		
		// Get all the object data
		BurberryAPIBeanUtil.getObjectData(
				BurPaletteMaterialConstant.PAL_SUPPLIER_IGNORE, supplierBean,
				supplierObj, BurPaletteMaterialConstant.PAL_SUPPLIER_ATT,
				jsonMappingSupplier, systemAttjsonMappingSupplier);

		// Validate required attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(supplierBean,
		// BurPaletteMaterialConstant.SUPPLIER_REQ);
		// Return Statement
		return supplierBean;
	}

}