package com.sportmaster.wc.emailutility.sepd.constants;

import com.lcs.wc.util.LCSProperties;

public class SMSEPDPSCSCancelledConstants {

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
	 * BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_FLEX_TYPE.
	 */
	public static final String BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_FLEX_TYPE = LCSProperties.get(
			"com.sportmaster.wc.product.sepd.businessobject.flextypeid",
			"Business Object\\smEmailUtilBO\\smSEPDProductSKUSeasonCancelledBO");

	/**
	 * BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_NAME.
	 */
	public static final String BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_NAME = LCSProperties.get(
			"com.sportmaster.wc.product.sepd.businessobject.name",
			"Email Notification for SEPD Product-Season or SKU-Season Cancelled");

	/**
	 * BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_MOA_ATTRIBUTE.
	 */
	public static final String BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_MOA_ATTRIBUTE = LCSProperties
			.get("com.sportmaster.wc.product.sepd.businessobject.moaAttribute", "smSEPDProductSKUSeasonCancelled");

	/**
	 * MOA_SEPD_PRODUCT_SKU_SEASON_CANCELLED_FLEX_TYPE.
	 */
	public static final String MOA_SEPD_PRODUCT_SKU_SEASON_CANCELLED_FLEX_TYPE = LCSProperties.get(
			"com.sportmaster.wc.product.sepd.multiobject.flextypeid",
			"Multi-Object\\smSEPDProductSKUSeasonCancelledMOA");

	/**
	 * MOA_SEPD_SEASON_ID.
	 */
	public static final String MOA_SEPD_SEASON_ID = LCSProperties
			.get("com.sportmaster.wc.product.sepd.multiobject.seasonid", "smSEPDSeasonId");
	/**
	 * MOA_SEPD_SEASON_NAME.
	 */
	public static final String MOA_SEPD_SEASON_NAME = LCSProperties
			.get("com.sportmaster.wc.product.sepd.multiobject.seasoname", "smSEPDSeason");
	/**
	 * MOA_SEPD_BRAND_NAME.
	 */
	public static final String MOA_SEPD_BRAND_NAME = LCSProperties
			.get("com.sportmaster.wc.product.sepd.multiobject.brand", "smSEPDBrand");

	/**
	 * MOA_SEPD_PRODUCT_ID.
	 */
	public static final String MOA_SEPD_PRODUCT_ID = LCSProperties
			.get("com.sportmaster.wc.product.sepd.multiobject.productid", "smSEPDProductId");

	/**
	 * MOA_SEPD_PRODUCT_NAME.
	 */
	public static final String MOA_SEPD_PRODUCT_NAME = LCSProperties
			.get("com.sportmaster.wc.product.sepd.multiobject.productname", "smSEPDProduct");

	/**
	 * MOA_SEPD_CW_ID.
	 */
	public static final String MOA_SEPD_CW_ID = LCSProperties.get("com.sportmaster.wc.product.sepd.multiobject.cwid",
			"smSEPDColorwayId");

	/**
	 * MOA_SEPD_CW_NAME.
	 */
	public static final String MOA_SEPD_CW_NAME = LCSProperties
			.get("com.sportmaster.wc.product.sepd.multiobject.cwname", "smSEPDColorway");

	/**
	 * MOA_SEPD_CREATED_AT.
	 */
	public static final String MOA_SEPD_CREATED_AT = LCSProperties
			.get("com.sportmaster.wc.product.sepd.multiobject.createdAt", "smSEPDCreatedAt");

	/**
	 * MOA_SEPD_LEVEL.
	 */
	public static final String MOA_SEPD_LEVEL = LCSProperties.get("com.sportmaster.wc.product.sepd.multiobject.level",
			"smSEPDLevel");

	/**
	 * LCS_CANCELLED_STATE.
	 */
	public static final String LCS_CANCELLED_STATE = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingPlugin.lcsCancelledState");

	/**
	 * PRODUCT_LEVEL.
	 */
	public static final String PRODUCT_LEVEL = "PRODUCT";

	/**
	 * SKU_LEVEL.
	 */
	public static final String SKU_LEVEL = "SKU";

	/**
	 * Declaration constants for time zone.
	 */
	public static final String TIME_ZONE = LCSProperties.get("com.sportmaster.wc.emailutility.timeZone");

}
