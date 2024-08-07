package com.sportmaster.wc.emailutility.fpd.constants;

import com.lcs.wc.util.LCSProperties;

public class SMFPDColorwayCreateMOAConstants {

	/**
	 * SEASON_NAME.
	 */
	public static final String SEASON_NAME = "seasonName";
	/**
	 * SKUNAME.
	 */
	public static final String SKU_NAME = "skuName";
	/**
	 * BRAND.
	 */
	public static final String BRAND = "vrdBrand";
	/**
	 * BO_FPD_CREATE_COLOURWAY_FLEX_TYPE.
	 */
	public static final String BO_FPD_CREATE_COLOURWAY_FLEX_TYPE = LCSProperties.get(
			"com.sportmaster.wc.product.fpd.businessobject.flextypeid",
			"Business Object\\smEmailUtilBO\\smFPDColorwayCreateBO");
	/**
	 * BO_FPD_CREATE_COLOURWAY_NAME.
	 */
	public static final String BO_FPD_CREATE_COLOURWAY_NAME = LCSProperties
			.get("com.sportmaster.wc.product.fpd.businessobject.name", "Email Notification for FPD Colorway Create");

	/**
	 * BO_TRACK_MATERIAL_DOCUMENT_MOA_ATTRIBUTE.
	 */
	public static final String BO_FPD_CREATE_COLOURWAY_MOA_ATTRIBUTE = LCSProperties
			.get("com.sportmaster.wc.product.fpd.businessobject.moaAttribute", "smFPDColorwayCreate");

	/**
	 * MOA_FPD_CREATE_COLOURWAY_FLEX_TYPE.
	 */
	public static final String MOA_FPD_CREATE_COLOURWAY_FLEX_TYPE = LCSProperties
			.get("com.sportmaster.wc.product.fpd.multiobject.flextypeid", "Multi-Object\\smFPDColorwayCreateMOA");
	/**
	 * MOA_FPD_SEASON_ID.
	 */
	public static final String MOA_FPD_SEASON_ID = LCSProperties
			.get("com.sportmaster.wc.product.fpd.multiobject.seasonid", "smFPDSeasonId");
	/**
	 * MOA_FPD_SEASON_NAME.
	 */
	public static final String MOA_FPD_SEASON_NAME = LCSProperties
			.get("com.sportmaster.wc.product.fpd.multiobject.seasonname", "smFPDSeason");
	/**
	 * MOA_FPD_BRAND_NAME.
	 */
	public static final String MOA_FPD_BRAND_NAME = LCSProperties
			.get("com.sportmaster.wc.product.fpd.multiobject.brand", "smFPDBrand");
	/**
	 * MOA_FPD_PRODUCT_ID.
	 */
	public static final String MOA_FPD_PRODUCT_ID = LCSProperties
			.get("com.sportmaster.wc.product.fpd.multiobject.productid", "smFPDProductId");
	/**
	 * MOA_FPD_PRODUCT_NAME.
	 */
	public static final String MOA_FPD_PRODUCT_NAME = LCSProperties
			.get("com.sportmaster.wc.product.fpd.multiobject.productname", "smFPDProduct");

	/**
	 * MOA_FPD_CW_ID.
	 */
	public static final String MOA_FPD_CW_ID = LCSProperties.get("com.sportmaster.wc.product.fpd.multiobject.newcwid",
			"smFPDNewCWId");
	/**
	 * MOA_FPD_CW_NAME.
	 */
	public static final String MOA_FPD_CW_NAME = LCSProperties
			.get("com.sportmaster.wc.product.fpd.multiobject.newcwname", "smFPDNewCW");

	/**
	 * MOA_FPD_CREATED_AT.
	 */
	public static final String MOA_FPD_CREATED_AT = LCSProperties
			.get("com.sportmaster.wc.product.fpd.multiobject.createdAt", "smFPDCreatedAt");

	/**
	 * Declaration constants for time zone.
	 */
	public static final String TIME_ZONE = LCSProperties.get("com.sportmaster.wc.emailutility.timeZone");
	
	/**
	 * Production Manager.
	 */
	public static final String PRODUCTION_MANAGER = LCSProperties
			.get("com.sportmaster.wc.productSeasonLink.productionManager", "smProductionManager");

	/**
	 * LCS_CANCELLED_STATE.
	 */
	public static final String LCS_CANCELLED_STATE = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingPlugin.lcsCancelledState");
}
