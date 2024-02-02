/**
 * 
 */
package com.burberry.wc.integration.util;

import com.lcs.wc.util.LCSProperties;

/**
 * Class hold reference of common constant used. All the constant will be used
 * in different classes. This class will be the central location to declare any
 * string/ char value as constant. Most of constant value will be read from
 * property file to populate. ID used as constant should have universal
 * reference id from FLEX system. Most of the constant will start with BUR_
 * which are specific to Flex field. These fields must have value which are
 * universal ID for the fields. So in future if DB changes it will impact the
 * running Java code.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurConstant {

	/**
	 * constructor.
	 */
	private BurConstant() {

	}

	/**
	 * PRODUCTSEASON.
	 */
	public static final String PRODUCTSEASON = "productseason";

	/**
	 * COSTSHEET.
	 */
	public static final String COSTSHEET = "costsheet";

	/**
	 * PRODUCT.
	 */
	public static final String PRODUCT = "product";
	/**
	 * BUR_LENGTH.
	 */
	public static final String BUR_LENGTH = LCSProperties
			.get("com.burberry.integration.length");
	/**
	 * BUR_WIDTH.
	 */
	public static final String BUR_WIDTH = LCSProperties
			.get("com.burberry.integration.width");
	/**
	 * BUR_HEIGHT.
	 */
	public static final String BUR_HEIGHT = LCSProperties
			.get("com.burberry.integration.length");
	/**
	 * BUR_SLEEVE.
	 */
	public static final String BUR_SLEEVE = LCSProperties
			.get("com.burberry.integration.sleeve");
	/**
	 * BUR_PLAN_GROUP.
	 */
	public static final String BUR_PLAN_GROUP = LCSProperties
			.get("com.burberry.integration.plangroup");
	/**
	 * BUR_LENGTH_APP.
	 */
	public static final String BUR_LENGTH_APP = LCSProperties
			.get("com.burberry.integration.lengthApp");
	/**
	 * PM_QUANTITY.
	 */
	public static final String PM_QUANTITY = LCSProperties
			.get("com.burberry.integration.pmq");
	/**
	 * BUR_CONTENT.
	 */
	public static final String BUR_CONTENT = LCSProperties
			.get("com.burberry.integration.content");
	/**
	 * BUR_SWING_TICKET_ID.
	 */
	public static final String BUR_SWING_TICKET_ID = LCSProperties
			.get("com.burberry.integration.swing");
	/**
	 * VRD_COUNTRY_OF_ORIGIN.
	 */
	public static final String VRD_COUNTRY_OF_ORIGIN = LCSProperties
			.get("com.burberry.integration.coo");
	/**
	 * BUR_STILL_VALID_REASON.
	 */
	public static final String BUR_STILL_VALID_REASON = LCSProperties
			.get("com.burberry.integration.stillvalid");
	/**
	 * BUR_COLOURWAY_STATUS.
	 */
	public static final String BUR_COLOURWAY_STATUS = LCSProperties
			.get("com.burberry.wc.product.BurberryColourwayStatus");
	/**
	 * BUR_OVER_MAKES.
	 */
	public static final String BUR_OVER_MAKES = LCSProperties
			.get("com.burberry.integration.overmake");
	/**
	 * BUR_LIMITED_AVAILABILITY.
	 */
	public static final String BUR_LIMITED_AVAILABILITY = LCSProperties
			.get("com.burberry.integration.avail");
	/**
	 * BUR_KEY_DRIVER.
	 */
	public static final String BUR_KEY_DRIVER = LCSProperties
			.get("com.burberry.integration.keydriver");
	/**
	 * BUR_GSR.
	 */
	public static final String BUR_GSR = LCSProperties
			.get("com.burberry.integration.gsr");
	/**
	 * BUR_FIXTURE.
	 */
	public static final String BUR_FIXTURE = LCSProperties
			.get("com.burberry.integration.fixture");
	/**
	 * BUR_CLIMATE.
	 */
	public static final String BUR_CLIMATE = LCSProperties
			.get("com.burberry.integration.climate");
	/**
	 * BUR_CARRY_OUT_CODING.
	 */
	public static final String BUR_CARRY_OUT_CODING = LCSProperties
			.get("com.burberry.integration.carryoutcode");
	/**
	 * BUR_CARRYFORWARD_COLOURWAY.
	 */
	public static final String BUR_CARRYFORWARD_COLOURWAY = LCSProperties
			.get("com.burberry.integration.carryforward");
	/**
	 * BUR_CAPSULE.
	 */
	public static final String BUR_CAPSULE = LCSProperties
			.get("com.burberry.integration.capsule");
	/**
	 * BUR_BRAND_BUY.
	 */
	public static final String BUR_BRAND_BUY = LCSProperties
			.get("com.burberry.integration.brandbuy");
	/**
	 * BUR_AD_CAMPAIGN.
	 */
	public static final String BUR_AD_CAMPAIGN = LCSProperties
			.get("com.burberry.integration.adcampaign");
	/**
	 * SUPPLIER_NAME.
	 */
	public static final String SUPPLIER_NAME = LCSProperties
			.get("com.burberry.integration.supplier");
	/**
	 * BUR_SAP_VENDOR_ID.
	 */
	public static final String BUR_SAP_VENDOR_ID = LCSProperties
			.get("com.burberry.integration.vendorid");
	/**
	 * BUR_FSR_STATUS.
	 */
	public static final String BUR_FSR_STATUS = LCSProperties
			.get("com.burberry.integration.fsr");
	/**
	 * BUR_THEME_FLOORSET.
	 */

	public static final String BUR_THEME_FLOORSET = LCSProperties
			.get("com.burberry.integration.theme");
	/**
	 * BUR_COLLECTION_MARKET.
	 */
	public static final String BUR_COLLECTION_MARKET = LCSProperties
			.get("com.burberry.integration.collection");
	/**
	 * BUR_TREATMENT.
	 */
	public static final String BUR_TREATMENT = LCSProperties
			.get("com.burberry.integration.treatment");
	/**
	 * BUR_RETAIL_END_DATE.
	 */
	public static final String BUR_RETAIL_END_DATE = LCSProperties
			.get("com.burberry.integration.retailend");
	/**
	 * BUR_RETAIL_START_DATE.
	 */
	public static final String BUR_RETAIL_START_DATE = LCSProperties
			.get("com.burberry.integration.retailstart");
	/**
	 * BUR_PRINT_PATTERN.
	 */
	public static final String BUR_PRINT_PATTERN = LCSProperties
			.get("com.burberry.integration.printpattern");
	/**
	 * BUR_PERSONALISATION.
	 */
	public static final String BUR_PERSONALISATION = LCSProperties
			.get("com.burberry.integration.personalisation");
	/**
	 * BUR_MARKETING_COLOUR.
	 */
	public static final String BUR_MARKETING_COLOUR = LCSProperties
			.get("com.burberry.integration.marketingcolour");
	/**
	 * BUR_DESIGN_INTENT_CHECK.
	 */
	public static final String BUR_DESIGN_INTENT_CHECK = LCSProperties
			.get("com.burberry.integration.designcheck");
	/**
	 * BUR_ICONIC.
	 */
	public static final String BUR_ICONIC = LCSProperties
			.get("com.burberry.integration.iconic");
	/**
	 * BUR_HERITAGE.
	 */
	public static final String BUR_HERITAGE = LCSProperties
			.get("com.burberry.integration.heritage");
	/**
	 * BUR_GENDER.
	 */
	public static final String BUR_GENDER = LCSProperties
			.get("com.burberry.integration.gender");
	/**
	 * BUR_EXIT_ROUTE.
	 */
	public static final String BUR_EXIT_ROUTE = LCSProperties
			.get("com.burberry.integration.exitroute");
	/**
	 * BUR_COLOUR_GROUP.
	 */
	public static final String BUR_COLOUR_GROUP = LCSProperties
			.get("com.burberry.integration.colourgroup");
	/**
	 * BUR_COLOUR_CODE.
	 */
	public static final String BUR_COLOUR_CODE = LCSProperties
			.get("com.burberry.integration.colourcode");
	/**
	 * BUR_CHECK_DESCRIPTION.
	 */
	public static final String BUR_CHECK_DESCRIPTION = LCSProperties
			.get("com.burberry.integration.checkdsc");
	/**
	 * BUR_LEGACY_PLMID.
	 */
	public static final String BUR_LEGACY_PLMID = LCSProperties
			.get("com.burberry.integration.legacyplmid");
	/**
	 * BUR_LEGACY_PLM_STYLE_ID.
	 */
	public static final String BUR_LEGACY_PLM_STYLE_ID = LCSProperties
			.get("com.burberry.integration.legacystyleid");
	/**
	 * BUR_WORLD.
	 */
	public static final String BUR_WORLD = LCSProperties
			.get("com.burberry.integration.world");
	/**
	 * BUR_WEIGHT_GROUP.
	 */
	public static final String BUR_WEIGHT_GROUP = LCSProperties
			.get("com.burberry.integration.weightgroup");
	/**
	 * BUR_WASH_INSTRUCTIONS.
	 */
	public static final String BUR_WASH_INSTRUCTIONS = LCSProperties
			.get("com.burberry.integration.instructions");
	/**
	 * BUR_TRIM.
	 */
	public static final String BUR_TRIM = LCSProperties
			.get("com.burberry.integration.trim");
	/**
	 * BUR_SUB_WORLD.
	 */
	public static final String BUR_SUB_WORLD = LCSProperties
			.get("com.burberry.integration.subworld");
	/**
	 * BUR_SIZE_GROUPING.
	 */
	public static final String BUR_STYLE_DETAILING = LCSProperties
			.get("com.burberry.integration.styledetail");
	/**
	 * 
	 */
	public static final String BUR_SIZE_GROUPING = LCSProperties
			.get("com.burberry.integration.sizegroup");
	/**
	 * BUR_SIL_HOUETTE.
	 */
	public static final String BUR_SIL_HOUETTE = LCSProperties
			.get("com.burberry.integration.silhouette");
	/**
	 * BUR_SHAPE.
	 */
	public static final String BUR_SHAPE = LCSProperties
			.get("com.burberry.integration.shape");
	/**
	 * BUR_SIZE_RANGE.
	 */
	public static final String BUR_SIZE_RANGE = LCSProperties
			.get("com.burberry.integration.sizerange");
	/**
	 * BUR_PRODUCTION_TYPE.
	 */
	public static final String BUR_PRODUCTION_TYPE = LCSProperties
			.get("com.burberry.integration.productiontype");
	/**
	 * BUR_SHIP_BOXED_HANGING.
	 */
	public static final String BUR_SHIP_BOXED_HANGING = LCSProperties
			.get("com.burberry.integration.hanging");
	/**
	 * BUR_MARKETING_DESCRIPTION.
	 */
	public static final String BUR_MARKETING_DESCRIPTION = LCSProperties
			.get("com.burberry.integration.marketdsc");
	/**
	 * BUR_MAIN_RM.
	 */
	public static final String BUR_MAIN_RM = LCSProperties
			.get("com.burberry.wc.flexbom.mainRM");
	/**
	 * BUR_KAFF_FLAG.
	 */
	public static final String BUR_KAFF_FLAG = LCSProperties
			.get("com.burberry.integration.kaff");
	/**
	 * BUR_HEEL_HEIGHT.
	 */
	public static final String BUR_HEEL_HEIGHT = LCSProperties
			.get("com.burberry.integration.heelheight");
	/**
	 * BUR_FIT.
	 */
	public static final String BUR_FIT = LCSProperties
			.get("com.burberry.integration.fit");
	/**
	 * BUR_EXOTIC.
	 */
	public static final String BUR_EXOTIC = LCSProperties
			.get("com.burberry.integration.exotic");
	/**
	 * BUR_DOWN_WEIGHTS_WEIGHT.
	 */
	public static final String BUR_DOWN_WEIGHTS_WEIGHT = LCSProperties
			.get("com.burberry.integration.downweight");
	/**
	 * BUR_COMMODITY_CODE.
	 */
	public static final String BUR_COMMODITY_CODE = LCSProperties
			.get("com.burberry.integration.product.xmlattributes.commoditycode");
	/**
	 * BUR_CITES.
	 */
	public static final String BUR_CITES = LCSProperties
			.get("com.burberry.integration.cites");
	/**
	 * BUR_CHECK_BRANDING.
	 */
	public static final String BUR_CHECK_BRANDING = LCSProperties
			.get("com.burberry.integration.checkbranding");
	/**
	 * BUR_BRANDING_TYPE.
	 */
	public static final String BUR_BRANDING_TYPE = LCSProperties
			.get("com.burberry.integration.brandingType");
	/**
	 * VRD_STYLE_NUM.
	 */
	public static final String VRD_STYLE_NUM = LCSProperties
			.get("com.burberry.integration.stylenum");
	/**
	 * BUR_MAIN_RM_CODE.
	 */
	public static final String BUR_MAIN_RM_CODE = LCSProperties
			.get("com.burberry.wc.flexbom.productMainRMCodeKey");
	/**
	 * NAME.
	 */
	public static final String NAME = "name";
	/**
	 * BUR_AGE_GROUP.
	 */
	public static final String BUR_AGE_GROUP = LCSProperties
			.get("com.burberry.integration.agegroup");
	/**
	 * BUR_SUB_GROUP.
	 */
	public static final String BUR_SUB_GROUP = LCSProperties
			.get("com.burberry.integration.subgroup");
	/**
	 * BUR_DEPT_GROUP.
	 */
	public static final String BUR_DEPT_GROUP = LCSProperties
			.get("com.burberry.integration.deptgroup");
	/**
	 * BUR_PRODUCT_TYPE.
	 */
	public static final String BUR_PRODUCT_TYPE = LCSProperties
			.get("com.burberry.integration.prodType");
	/**
	 * BUR_ACCESSORY_GROUP.
	 */
	public static final String BUR_ACCESSORY_GROUP = LCSProperties
			.get("com.burberry.integration.accessorygroup");
	/**
	 * BUR_OPERATIONAL_CATEGORY.
	 */
	public static final String BUR_OPERATIONAL_CATEGORY = LCSProperties
			.get("com.burberry.integration.operationalcategory");
	/**
	 * BUR_BRAND.
	 */
	public static final String BUR_BRAND = LCSProperties
			.get("com.burberry.integration.brand");
	/**
	 * BUR_DIVISION.
	 */
	public static final String BUR_DIVISION = LCSProperties
			.get("com.burberry.integration.division");
	/**
	 * MODIFY.
	 */
	public static final String MODIFY = "modify";

	/**
	 * SEASON_NAME.
	 */
	public static final String SEASON_NAME = LCSProperties
			.get("com.burberry.integration.season");
	/**
	 * VRD_TARGET_RETAIL_PRICE.
	 */
	public static final String VRD_TARGET_RETAIL_PRICE = LCSProperties
			.get("com.burberry.integration.target");
	/**
	 * BUR_ADJUSTED_USD_RETAIL.
	 */
	public static final String BUR_ADJUSTED_USD_RETAIL = LCSProperties
			.get("com.burberry.integration.pricing.xmlattributes.usd");
	/**
	 * BUR_ADJUSTED_HKD_RETAIL.
	 */
	public static final String BUR_ADJUSTED_HKD_RETAIL = LCSProperties
			.get("com.burberry.integration.pricing.xmlattributes.hkd");
	/**
	 * BUR_ADJUSTED_EUR_RETAIL.
	 */
	public static final String BUR_ADJUSTED_EUR_RETAIL = LCSProperties
			.get("com.burberry.integration.pricing.xmlattributes.eur");
	/**
	 * VRD_ELC.
	 */
	public static final String VRD_ELC = LCSProperties
			.get("com.burberry.integration.landedcost");
	/**
	 * BUR_PARTNER_TRADING_CURRENCY.
	 */
	public static final String BUR_PARTNER_TRADING_CURRENCY = LCSProperties
			.get("com.burberry.integration.currency");
	/**
	 * BUR_FG_COST_LOCAL.
	 */
	// public static final String BUR_FG_COST_LOCAL = LCSProperties
	// .get("com.burberry.integration.fgcost");
	/**
	 * VENDOR.
	 */
	public static final String VENDOR = LCSProperties
			.get("com.burberry.integration.vendor");

	/**
	 * COLOUR_PRINT.
	 */
	public static final String COLOUR_PRINT = "colour/print";

	/**
	 * PRINT.
	 */
	public static final String PRINT = "Print";

	/**
	 * VRD_FIBER_CONTENT.
	 */
	public static final String VRD_FIBER_CONTENT = LCSProperties
			.get("com.burberry.integration.product.xmlattributes.fibercontent");

	/**
	 * BUR_LATIN_NAME.
	 */
	public static final String BUR_LATIN_NAME = LCSProperties
			.get("com.burberry.integration.product.xmlattributes.latinname");

	/**
	 * BUR_COMMON_NAME.
	 */
	public static final String BUR_COMMON_NAME = LCSProperties
			.get("com.burberry.integration.product.xmlattributes.commonname");

	/**
	 * SHEARLING.
	 */
	public static final String SHEARLING = "Shealing";

	/**
	 * FEATHER.
	 */
	public static final String FEATHER = "Feather";

	/**
	 * LEATHER.
	 */
	public static final String LEATHER = "Leather";

	/**
	 * FUR.
	 */
	public static final String FUR = "Fur";

	/**
	 * COLOUR_CHECK.
	 */
	public static final String COLOUR_CHECK = "colour/check";

	/**
	 * CHECK.
	 */
	public static final String CHECK = "check";

	/**
	 * COLOR.
	 */
	public static final String COLOR = "color";

	/**
	 * COLOUR.
	 */
	public static final String COLOUR = "colour";

	/**
	 * COLOURWAY.
	 */
	public static final String COLOURWAY = "colourway";

	/**
	 * BUR_PACKAGING_LABELING.
	 */
	public static final String BUR_PACKAGING_LABELING = LCSProperties
			.get("com.burberry.integration.product.xmlattributes.pkgSection");

	/**
	 * SECTION2.
	 */
	public static final String SECTION = "section";

	/**
	 * LCSPRODUCT_ROOT_ID.
	 */
	public static final String LCSPRODUCT_ROOT_ID = "OR:com.lcs.wc.product.LCSProduct:";
	/**
	 * LCSPRODUCT_IDA2A2.
	 */
	public static final String LCSPRODUCT_IDA2A2 = "LCSPRODUCT.IDA2A2";

	/**
	 * ALL_DIMENSIONS.
	 */
	public static final String ALL_DIMENSIONS = "ALL_DIMENSIONS";

	/**
	 * BOM2.
	 */
	public static final String BOM = "BOM";

	/**
	 * MATERIAL.
	 */
	public static final String MATERIAL = "material";

	/**
	 * FLEXBOM.
	 */
	public static final String FLEXBOM = "flexbom";

	/**
	 * SOURCE2.
	 */
	public static final String SOURCE = "source";

	/**
	 * SKUSEASON2.
	 */
	public static final String SKUSEASON = "skuseason";

	/**
	 * SEASON.
	 */
	public static final String SEASON = "season";

	/**
	 * BUR_COLOURWAY_LAUNCH_DATE.
	 */
	public static final String BUR_COLOURWAY_LAUNCH_DATE = LCSProperties
			.get("com.burberry.integration.seasonstartdate");
	/**
	 * BUR_SEASON.
	 */
	public static final String BUR_SEASON = LCSProperties
			.get("com.burberry.integration.colorwayseason");
	/**
	 * BUR_RETAIL_SEASON_END_DATE.
	 */
	public static final String BUR_RETAIL_SEASON_END_DATE = LCSProperties
			.get("com.burberry.integration.seasonenddate");
	/**
	 * BUR_IP.
	 */
	public static final String BUR_IP = LCSProperties
			.get("com.burberry.integration.ip");
	/**
	 * BUR_SAP_MATERIAL_NUMBER.
	 */
	public static final String BUR_SAP_MATERIAL_NUMBER = LCSProperties
			.get("com.burberry.wc.product.sapMatNum");
	/**
	 * BUR_FABRIC_TYPE.
	 */
	public static final String BUR_FABRIC_TYPE = LCSProperties
			.get("com.burberry.integration.fabrictype");
	/**
	 * VRD_DESCRIPTION.
	 */
	public static final String VRD_DESCRIPTION = LCSProperties
			.get("com.burberry.integration.description");

	/**
	 * PRODUCT_PART_KEY.
	 */
	public static final String PRODUCT_PART_KEY = "com.burberry.integration.product.xmlattributes.";
	/**
	 * PRODCUT_PRODUCTKEY.
	 */
	public static final String PRODCUT_PRODUCTKEY = LCSProperties
			.get(PRODUCT_PART_KEY + PRODUCT);
	/**
	 * 
	 */
	public static final String PRODCUT_SKU_KEY = LCSProperties
			.get(PRODUCT_PART_KEY + COLOURWAY);
	/**
	 * PRODUCT_SKUSEASON_KEY.
	 */
	public static final String PRODUCT_SKUSEASON_KEY = LCSProperties
			.get(PRODUCT_PART_KEY + SKUSEASON);

	/**
	 * LCS_FLEXSPECIFICATION_ROOT_ID.
	 */
	public static final String LCS_FLEXSPECIFICATION_ROOT_ID = "OR:com.lcs.wc.specification.FlexSpecification:";
	/**
	 * LCS_FLEXSPECIFICATION_IDA2A2.
	 */
	public static final String LCS_FLEXSPECIFICATION_IDA2A2 = "FlexSpecification.IDA2A2";

	/**
	 * LCS_LCSMATERIALMASTER_ROOT_ID.
	 */
	public static final String LCS_FLEXBOMLINK_IDA3B5 = "FlexBOMLink.IDA3B5";
	/**
	 * 
	 */
	public static final String LCS_LCSMATERIALMASTER_ROOT_ID = "OR:com.lcs.wc.material.LCSMaterialMaster:";

	/**
	 * LCS_FLEXBOMLINK_IDA2A2.
	 */
	public static final String LCS_FLEXBOMLINK_IDA2A2 = "FlexBOMLink.IDA2A2";
	/**
	 * LCS_FLEXBOMLINK_ROOT_ID.
	 */
	public static final String LCS_FLEXBOMLINK_ROOT_ID = "OR:com.lcs.wc.flexbom.FlexBOMLink:";

	/**
	 * BUR_APPROVED_COLOURWAY_COST.
	 */
	public static final String BUR_APPROVED_COLOURWAY_COST = LCSProperties
			.get("com.burberry.integration.pricing.xmlattributes.colourwaycost");

	/**
	 * PRICING_PART_KEY.
	 */
	public static final String PRICING_PART_KEY = "com.burberry.integration.pricing.xmlattributes.";
	/**
	 * PRICING_PRODUCT_KEY.
	 */
	public static final String PRICING_PRODUCT_KEY = LCSProperties
			.get(PRICING_PART_KEY + PRODUCT);
	/**
	 * PRICING_SKU_KEY.
	 */
	public static final String PRICING_SKU_KEY = LCSProperties
			.get(PRICING_PART_KEY + COLOURWAY);
	/**
	 * PRICING_SKUSEASON_KEY.
	 */
	public static final String PRICING_SKUSEASON_KEY = LCSProperties
			.get(PRICING_PART_KEY + SKUSEASON);
	/**
	 * PRICING_SOURCE_KEY.
	 */
	public static final String PRICING_SOURCE_KEY = LCSProperties
			.get(PRICING_PART_KEY + SOURCE);
	/**
	 * PRICING_COSTSHEET_KEY.
	 */
	public static final String PRICING_COSTSHEET_KEY = LCSProperties
			.get(PRICING_PART_KEY + COSTSHEET);
	/**
	 * PRICING_PRODUCTSEASON_KEY.
	 */
	public static final String PRICING_PRODUCTSEASON_KEY = LCSProperties
			.get(PRICING_PART_KEY + PRODUCTSEASON);
	/**
	 * STR_VALIDATION_STATUS.
	 */
	public static final String PRICING_SEASON_KEY = LCSProperties
			.get(PRICING_PART_KEY + SEASON);
	/**
	 * 
	 */
	public static final String STR_VALIDATION_STATUS = LCSProperties
			.get("com.burberry.wc.validation");
	/**
	 * STR_PASSED_KEY.
	 */
	public static final String STR_PASSED_KEY = LCSProperties
			.get("com.burberry.wc.passedKey");

	/**
	 * STRING_COLON.
	 */
	public static final String STRING_COLON = ":";

	/**
	 * STRING_CAMMA.
	 */
	public static final String STRING_COMMA = ",";

	/**
	 * STRING_PATTERN.
	 */
	public static final String STRING_REG_PATTERN = "|~*~|";
	/**
	 * STRING_EMPTY.
	 */
	public static final String STRING_EMPTY = "";
	/**
	 * STR_NO_MATCHING_RECORD_FOUND.
	 */
	public static final String STR_NO_MATCHING_RECORD_FOUND = "No matching record found for the given parameter. Please try with  other combination.";

	/**
	 * COMPOSITE.
	 */
	public static final String COMPOSITE = "composite";

	/**
	 * MOA_ENTRY.
	 */
	public static final String MOA_ENTRY = "moaEntry";

	/**
	 * MOA_LIST.
	 */
	public static final String MOA_LIST = "moaList";

	/**
	 * GMT.
	 */
	public static final String GMT = "GMT";
	/**
	 * COUNTRY.
	 */
	public static final String COUNTRY = "country";
	/**
	 * DATE.
	 */
	public static final String DATE = "date";

	/**
	 * INTEGER.
	 */
	public static final String INTEGER = "integer";
	/**
	 * FLOAT.
	 */
	public static final String FLOAT = "float";

	/**
	 * TEXT_AREA.
	 */
	public static final String TEXT_AREA = "textArea";

	/**
	 * TEXT_AREA.
	 */
	public static final String CONSTANT = "constant";

	/**
	 * BOOLEAN.
	 */
	public static final String BOOLEAN = "boolean";

	/**
	 * DERIVED_STRING.
	 */
	public static final String DERIVED_STRING = "derivedString";

	/**
	 * TEXT.
	 */
	public static final String TEXT = "text";

	/**
	 * DRIVEN.
	 */
	public static final String DRIVEN = "driven";

	/**
	 * CHOICE.
	 */
	public static final String CHOICE = "choice";

	/**
	 * MM_DD_YYYY.
	 */
	public static final String MM_DD_YYYY = "MM-dd-yyyy";

	/**
	 * SEQUENCE.
	 */
	public static final String SEQUENCE = "sequence";

	/**
	 * FULLNAME.
	 */
	public static final String FULLNAME = "FULLNAME";

	/**
	 * USER_LIST.
	 */
	public static final String USER_LIST = "userList";

	/**
	 * OBJECT_REF_LIST.
	 */
	public static final String OBJECT_REF_LIST = "object_ref_list";

	/**
	 * OBJECT_REF.
	 */
	public static final String OBJECT_REF = "object_ref";
	/**
	 * REST_APP_GROUP.
	 */
	public static final String REST_APP_GROUP = LCSProperties
			.get("com.burberry.integration.group");

	/**
	 * STR_ERROR_MSG_PRODUCT.
	 */
	public static final String STR_ERROR_MSG_PRODUCT = "Error occured while extracting Product info";
	/**
	 * STR_ERROR_MSG_PRICING.
	 */
	public static final String STR_ERROR_MSG_PRICING = "Error occured while extracting Pricing info";
	/**
	 * STR_ERROR_MSG.
	 */
	public static final String STR_ERROR_MSG = " Please check the input parameter. Parameter should be passed  & seperated i.e.  product?start=2017-01-01 00:00:00&end=2017-01-21 00:00:00&season=2018 Spring";
	/**
	 * STR_INVALID_SEASON_TYPE.
	 */
	public static final String STR_INVALID_SEASON_TYPE = "Please provide valid input for season field.It can not be null or empty. \n Please check the URL for season input parameter if  passed  as per  the design.\n Windchill/servlet/rest/BDEService/product?start=2017-01-01 00:00:00&end=2017-01-21 00:00:00&season=2018 Spring";
	/**
	 * STR_INVALID_START_DATE.
	 */
	public static final String STR_INVALID_START_DATE = "Please provide valid input for start date field.It can not be null or empty. \n Please check the URL for start dateinput parameter if  passed  as per  the design.\n Windchill/servlet/rest/BDEService/product?start=2017-01-01 00:00:00&end=2017-01-21 00:00:00&season=2018 Spring";
	/**
	 * STR_NO_SEASON_TYPE_FOUND.
	 */
	public static final String STR_NO_SEASON_TYPE_FOUND = "Could not find any season  with  given season name.Please provide valid input for season field. \n Please check the URL for season input parameter if  passed  as per  the design.\n Windchill/servlet/rest/BDEService/product?start=2017-01-01 00:00:00&end=2017-01-21 00:00:00&season=2018 Spring";

	/**
	 * LCS_PRODUCT_OBJECTIDENTIFIERID.
	 */
	public static final String LCS_PRODUCT_OBJECTIDENTIFIERID = "thePersistInfo.theObjectIdentifier.id";

	/**
	 * LATESTITERATIONINFO.
	 */
	public static final String LATESTITERATIONINFO = "iterationInfo.latest";
	/**
	 * IDA3MASTERREFEREN_CE.
	 */
	public static final String IDA3MASTERREFEREN_CE = "IDA3MASTERREFERENCE";
	/**
	 * PRODUCTMASTERID.
	 */
	public static final String PRODUCTMASTERID = "productMasterId";
	/**
	 * VERSIONIDA2VERSIONINFO.
	 */
	public static final String VERSIONIDA2VERSIONINFO = "versionInfo.identifier.versionId";
	/**
	 * EFFECTLATEST.
	 */
	public static final String EFFECTLATEST = "effectLatest";

	/**
	 * LCSSKUSEASONLINK.
	 */
	public static final String LCSSKUSEASONLINK = "LCSSKUSEASONLINK";
	/**
	 * LCSPRODUCT.
	 */
	public static final String LCSPRODUCT = "LCSPRODUCT";
	/**
	 * currency.
	 */
	public static final String CURRENCY = "currency";
	/**
	 * MODIFY_STAMPA2.
	 */
	public static final String MODIFY_STAMP = "thePersistInfo.modifyStamp";

	/**
	 * dateFormat.
	 */
	public static final String dateFormat = LCSProperties
			.get("com.burberry.integration.dateformat");
	/**
	 * Apparel.
	 */
	public static final CharSequence Apparel = "Apparel";
	/**
	 * BUR_OPERATIONAL_CATEGORY_APP.
	 */
	public static final String BUR_OPERATIONAL_CATEGORY_APP = LCSProperties
			.get("com.burberry.integration.operationalcategoryApp");
	/**
	 * LCSSKU.
	 */
	public static final String LCSSKU = "LCSSKU";
	/**
	 * PRODUCTAREVID.
	 */
	public static final String PRODUCTAREVID = "PRODUCTAREVID";
	/**
	 * BRANCHID.
	 */
	public static final String BRANCHID = "BranchIDIterationInfo";
	/**
	 * LCSPRODUCTSEASONLINK.
	 */
	public static final String LCSPRODUCTSEASONLINK = "LCSProductSeasonLink";
	/**
	 * SKUMASTERID.
	 */
	public static final String SKUMASTERID = "SKUMASTERID";
	/**
	 * MATERIALSTOCHECK property entry for Fiber Content.
	 */
	public static final String MATERIALSTOCHECK = LCSProperties
			.get("com.burberry.integration.materialtypes");
	/**
	 * COLOURSTOCHECK property entry for IP.
	 */
	public static final String COLOURSTOCHECK = LCSProperties
			.get("com.burberry.integration.colortypesforIP");
	/**
	 * BUR_ISO.
	 */
	public static final String BUR_ISO = LCSProperties
			.get("com.burberry.integration.isocode");

	/**
	 * BUR_STYLENAME.
	 */
	public static final String BUR_STYLENAME = LCSProperties
			.get("com.burberry.integration.styleName");
	/**
	 * BUR_LEGACYCOLOURCODE.
	 */
	public static final String BUR_LEGACYCOLOURCODE = LCSProperties
			.get("com.burberry.integration.legacyColourCode");
	/**
	 * BUR_PRICEBANDACTUALEURRETAIL.
	 */
	public static final String BUR_PRICEBANDACTUALEURRETAIL = LCSProperties
			.get("com.burberry.integration.priceBandActualEURRetail");
	/**
	 * BUR_PRICEBANDACTUALHKDRETAIL.
	 */
	public static final String BUR_PRICEBANDACTUALHKDRETAIL = LCSProperties
			.get("com.burberry.integration.priceBandActualHKDRetail");
	/**
	 * BUR_PRICEBANDACTUALUSDRETAIL.
	 */
	public static final String BUR_PRICEBANDACTUALUSDRETAIL = LCSProperties
			.get("com.burberry.integration.priceBandActualUSDRetail");
	/**
	 * BUR_PRODUCTSEASONACTUALRETAILGBP.
	 */
	public static final String BUR_PRODUCTSEASONACTUALRETAILGBP = LCSProperties
			.get("com.burberry.integration.productSeasonActualRetailGBP");
	/**
	 * BUR_PRODUCTSEASONINITIALPRICEBAND.
	 */
	public static final String BUR_PRODUCTSEASONINITIALPRICEBAND = LCSProperties
			.get("com.burberry.integration.productSeasonInitialPriceBand");
	/**
	 * BUR_VALIDPRICEBANDEUR.
	 */
	public static final String BUR_VALIDPRICEBANDEUR = LCSProperties
			.get("com.burberry.integration.validPriceBandEUR");
	/**
	 * BUR_VALIDPRICEBANDHKD.
	 */
	public static final String BUR_VALIDPRICEBANDHKD = LCSProperties
			.get("com.burberry.integration.validPriceBandHKD");
	/**
	 * BUR_VALIDPRICEBANDUSD.
	 */
	public static final String BUR_VALIDPRICEBANDUSD = LCSProperties
			.get("com.burberry.integration.validPriceBandUSD");
	/**
	 * ApparelMens.
	 */
	public static final CharSequence ApparelMens = "Apparel\\Mens";
	/**
	 * ApparelWomens.
	 */
	public static final CharSequence ApparelWomens = "Apparel\\Womens";
	/**
	 * BUR_VRDBRAND.
	 */
	public static final String BUR_VRDBRAND = LCSProperties
			.get("com.burberry.integration.vrdBrand");

	// Phase 3 Sprint 1 August
	/**
	 * BUR_FGCOSTGBP.
	 */
	public static final String BUR_FGCOSTGBP = LCSProperties
			.get("com.burberry.integration.fgCostGBP");

	// Phase 3 Sprint 2 September
	/**
	 * BUR_FREIGHTPERCENTOFFGCOSTGBP.
	 */
	public static final String BUR_FREIGHTPERCENTOFFGCOSTGBP = LCSProperties
			.get("com.burberry.integration.freightPercentofFGCostGBP");
	/**
	 * BUR_PRODUCTDUTYRATE.
	 */
	public static final String BUR_PRODUCTDUTYRATE = LCSProperties
			.get("com.burberry.integration.productDutyRate");
	/**
	 * BUR_COSTSCENARIOCOO.
	 */
	public static final String BUR_COSTSCENARIOCOO = LCSProperties
			.get("com.burberry.integration.costScenarioCOO");
	/**
	 * BUR_VENDORFGCOST.
	 */
	public static final String BUR_VENDORFGCOST = LCSProperties
			.get("com.burberry.integration.vendorFGCost");
	/**
	 * BUR_LOOKBOOK.
	 */
	public static final String BUR_LOOKBOOK = LCSProperties
			.get("com.burberry.integration.lookbook");
	/**
	 * BUR_COLOURTYPE.
	 */
	public static final String BUR_COLOURTYPE = LCSProperties
			.get("com.burberry.integration.colourType");

	/**
	 * ADDDED FOR PRODUCT API .
	 */
	/**
	 * JSON_KEY.
	 */
	public static final String JSON_KEY = "com.burberry.integration.productapi.jsonattributes.mapping";

	/**
	 * JSON_KEY.
	 */
	public static final String JSON_SYSTEM_KEY = "com.burberry.integration.productapi.system.jsonattributes.mapping";

	/**
	 * JSON_PRODUCTKEY.
	 */
	public static final String JSON_PRODUCTKEY = LCSProperties.get(JSON_KEY
			+ ".product");

	/**
	 * JSON_PRODUCT_SYSTEM_KEY.
	 */
	public static final String JSON_PRODUCT_SYSTEM_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".product");

	/**
	 * JSON_MATERIALKEY.
	 */
	public static final String JSON_MATERIALKEY = LCSProperties.get(JSON_KEY
			+ ".material");

	/**
	 * JSON_MATERIAL_SYSTEM_KEY.
	 */
	public static final String JSON_MATERIAL_SYSTEM_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".material");

	/**
	 * JSON_COLOURWAYKEY.
	 */
	public static final String JSON_COLOURWAYKEY = LCSProperties.get(JSON_KEY
			+ ".colourway");

	/**
	 * JSON_COLOURWAY_SYSTEM_KEY.
	 */
	public static final String JSON_COLOURWAY_SYSTEM_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".colourway");

	/**
	 * JSON_PLACEHOLDERKEY.
	 */
	public static final String JSON_PLACEHOLDERKEY = LCSProperties.get(JSON_KEY
			+ ".placeholder");

	/**
	 * JSON_PLACEHOLDER_SYSTEM_KEY.
	 */
	public static final String JSON_PLACEHOLDER_SYSTEM_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".placeholder");

	/**
	 * JSON_PRODCUTSEASONKEY.
	 */
	public static final String JSON_PRODCUTSEASONKEY = LCSProperties
			.get(JSON_KEY + ".productseason");

	/**
	 * JSON_PRODCUTSEASON_SYSTEM_KEY.
	 */
	public static final String JSON_PRODCUTSEASON_SYSTEM_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".productseason");

	/**
	 * JSON_COMMODIT_CODE.
	 */
	public static final String JSON_COMMODIT_CODE = LCSProperties.get(JSON_KEY
			+ ".commoditycode");

	/**
	 * JSON_COMMODIT_CODE_SYSTEM_KEY.
	 */
	public static final String JSON_COMMODIT_CODE_SYSTEM_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".commoditycode");

	/**
	 * JSON_SOURCE.
	 */
	public static final String JSON_SOURCE = LCSProperties.get(JSON_KEY
			+ ".source");

	/**
	 * JSON_SYSTEM_SOURCE_KEY.
	 */
	public static final String JSON_SYSTEM_SOURCE_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".source");

	/**
	 * JSON_RMGT.
	 */
	public static final String JSON_RMGT = LCSProperties.get(JSON_KEY
			+ ".riskmanagement");

	/**
	 * JSON_RMGT_SYSTEM_KEY.
	 */
	public static final String JSON_RMGT_SYSTEM_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".riskmanagement");

	/**
	 * JSON_COLOURWAYSEASONKEY.
	 */
	public static final String JSON_COLOURWAYSEASONKEY = LCSProperties
			.get(JSON_KEY + ".colourwayseason");

	/**
	 * JSON_COLOURWAYSEASON_SYSTEM_KEY.
	 */
	public static final String JSON_COLOURWAYSEASON_SYSTEM_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".colourwayseason");

	/**
	 * JSON_MATERIAL_SUP_KEY.
	 */
	public static final String JSON_MATERIAL_SUP_KEY = LCSProperties
			.get(JSON_KEY + ".materialsupplier");

	/**
	 * CHECKOUT_INFO.
	 */
	public static final String CHECKOUT_INFO = "checkoutstate";

	/**
	 * JSON_SEASONKEY.
	 */
	public static final String JSON_SEASONKEY = LCSProperties.get(JSON_KEY
			+ ".season");

	/**
	 * JSON_SEASON_SYSTEM_KEY.
	 */
	public static final String JSON_SEASON_SYSTEM_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".season");

	/**
	 * ACTIVE.
	 */
	public static final String ACTIVE = "active";

	/**
	 * SEASON_NAM.
	 */
	public static final String SEASON_NAM = "seasonName";

	/**
	 * PARTPRIMARYIMAGEURL.
	 */
	public static final String PARTPRIMARYIMAGEURL = "partPrimaryImageURL";

	/**
	 * PRIMARYIMAGEURL.
	 */
	public static final String PRIMARYIMAGEURL = "primaryImageURL";

	/**
	 * OBJECTIDENTIFIERID.
	 */
	public static final String OBJECTIDENTIFIERID = "thePersistInfo.theObjectIdentifier.id";

	/**
	 * CHECK_OUT_INFO_STATE.
	 */
	public static final String CHECK_OUT_INFO_STATE = "checkoutInfo.state";

	/**
	 * BUR_RM_MAIN.
	 */
	public static final String BUR_RM_MAIN = "burMainRM";

	/**
	 * MAT_STATE.
	 */
	public static final String MAT_STATE = "checkoutInfo.state";

	/**
	 * BUR_CC.
	 */
	public static final String BUR_CC = "burCommodityCode";

	/**
	 * MODIFYSTAMP.
	 */
	public static final String MODIFYSTAMP = "modifyStamp";

	/**
	 * PRODUCT_STATECHECKOUTINFO.
	 */
	public static final String PRODUCT_STATECHECKOUTINFO = "STATECHECKOUTINFO";
	/**
	 * LCSSEASON.
	 */
	public static final String LCSSEASON = "LCSSEASON";

	/**
	 * LCSSOURCINGCONFIG.
	 */
	public static final String LCSSOURCINGCONFIG = "LCSSOURCINGCONFIG";

	/**
	 * PLACEHOLDER.
	 */
	public static final String PLACEHOLDER = "PLACEHOLDER";

	/**
	 * STRING_COLON.
	 */
	public static final String STRING_DOT = ".";

	/**
	 * SEASONLINKTYPE.
	 */
	public static final String SEASONLINKTYPE = "seasonLinkType";

	/**
	 * LCSMATERIAL.
	 */
	public static final String LCSMATERIAL = "LCSMATERIAL";

	/**
	 * MATERIAL_STATECHECKOUTINFO.
	 */
	public static final String MATERIAL_STATECHECKOUTINFO = "STATECHECKOUTINFO";

	/**
	 * LCSLIFECYCLEMANAGED.
	 */
	public static final String LCSLIFECYCLEMANAGED = "LCSLIFECYCLEMANAGED";

	/**
	 * FOOTWEAR_TYPE.
	 */
	public static final String FOOTWEAR_TYPE = LCSProperties
			.get("com.burberry.integration.footwear");
	/**
	 * APPAREL_CHILDREN_TYPE.
	 */
	public static final String APPAREL_CHILDREN_TYPE = LCSProperties
			.get("com.burberry.integration.apparel.chidren");
	/**
	 * ACCESSORIES_CHILDREN_TYPE.
	 */
	public static final String ACCESSORIES_CHILDREN_TYPE = LCSProperties
			.get("com.burberry.integration.accessories.chidren");
	/**
	 * RM_FOOTWEAR_KEY.
	 */
	public static final String RM_FOOTWEAR_KEY = LCSProperties
			.get("com.burberry.integration.riskmanagement.footwear");
	/**
	 * RM_CHILDREN_KEY.
	 */
	public static final String RM_CHILDREN_KEY = LCSProperties
			.get("com.burberry.integration.riskmanagement.apparel.chidren");
	/**
	 * RM_ALL_KEY.
	 */
	public static final String RM_ALL_KEY = LCSProperties
			.get("com.burberry.integration.riskmanagement.all");
	/**
	 * SKU_BRANCHID.
	 */
	public static final String SKU_BRANCHID = "LCSSKU.BRANCHIDITERATIONINFO";
	/**
	 * SKUSEASONLINK_ID.
	 */
	public static final String SKUSEASONLINK_ID = "LCSSKUSEASONLINK.IDA2A2";
	/**
	 * PRODUCTSEASONLINK_ID.
	 */
	public static final String PRODUCTSEASONLINK_ID = "LCSPRODUCTSEASONLINK.IDA2A2";
	/**
	 * SOURCE_BRANCHID.
	 */
	public static final String SOURCE_BRANCHID = "LCSSOURCINGCONFIG.BRANCHIDITERATIONINFO";
	/**
	 * JSON_SOURCE_SUP.
	 */
	public static final String JSON_SOURCE_SUP = LCSProperties.get(JSON_KEY
			+ ".sourcesupplier");

	/**
	 * JSON_SOURCE_SUP_SYSTEM_KEY.
	 */
	public static final String JSON_SOURCE_SUP_SYSTEM_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".sourcesupplier");

	/**
	 * SEASONMASTERREFERENCE_KEY_ID.
	 */
	public static final String SEASONMASTERREFERENCE_KEY_ID = "seasonMasterReference.key.id";
	/**
	 * MASTERREFERENCE_KEY_ID.
	 */
	public static final String MASTERREFERENCE_KEY_ID = "masterReference.key.id";

	/**
	 * ROLEAOBJECTREF_KEY_ID.
	 */
	public static final String ROLEAOBJECTREF_KEY_ID = "roleAObjectRef.key.id";

	/**
	 * ROLEBOBJECTREF_KEY_ID.
	 */
	public static final String ROLEBOBJECTREF_KEY_ID = "roleBObjectRef.key.id";

	/**
	 * CREATE_STAMP.
	 */
	public static final String CREATE_STAMP = "thePersistInfo.createStamp";

	/**
	 * STR_ERROR_MSG_PRODUCT_API.
	 */
	public static final String STR_ERROR_MSG_PRODUCT_API = LCSProperties
			.get("com.burberry.integration.productapi.errormessage");

	/**
	 * STR_ERROR_MSG_PRODUCT_API_INVALID_BOOLEAN.
	 */
	public static final String STR_ERROR_MSG_PRODUCT_API_INVALID_BOOLEAN = LCSProperties
			.get("com.burberry.integration.productapi.errormessage.invalidboolean");

	/**
	 * STR_ERROR_MSG_PRODUCT_API_INVALID_DATE.
	 */
	public static final String STR_ERROR_MSG_PRODUCT_API_INVALID_DATE = LCSProperties
			.get("com.burberry.integration.productapi.errormessage.invaliddate");

	/**
	 * STR_ERROR_MSG_PRODUCT_API_MAXIMUM_PARAMTERS.
	 */
	public static final String STR_ERROR_MSG_PRODUCT_API_MAXIMUM_PARAMTERS = LCSProperties
			.get("com.burberry.integration.productapi.errormessage.maximumparameters");

	/**
	 * STR_ERROR_MSG_PRODUCT_API_NO_PARAMETERS.
	 */
	public static final String STR_ERROR_MSG_PRODUCT_API_NO_PARAMETERS = LCSProperties
			.get("com.burberry.integration.productapi.errormessage.noparameters");

	/**
	 * STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTE.
	 */
	public static final String STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTE = LCSProperties
			.get("com.burberry.integration.productapi.errormessage.invalidattribute");

	/**
	 * STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTEVALUE.
	 */
	public static final String STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTEVALUE = LCSProperties
			.get("com.burberry.integration.productapi.errormessage.invalidattributevalue");

	/**
	 * STR_ERROR_MSG_PRODUCT_API_INVALID_OBJECT.
	 */
	public static final String STR_ERROR_MSG_PRODUCT_API_INVALID_OBJECT = LCSProperties
			.get("com.burberry.integration.productapi.errormessage.invalidobject");

	/**
	 * STR_ERROR_MSG_PRODUCT_API_MISSING_START_DATE.
	 */
	public static final String STR_ERROR_MSG_PRODUCT_API_MISSING_START_DATE = LCSProperties
			.get("com.burberry.integration.productapi.errormessage.nostartdate");

	/**
	 * PRIMARY.
	 */
	public static final String PRIMARY = "primary";

	/**
	 * VRD_BRAND.
	 */
	public static final String VRD_BRAND = "vrdBrand";

	/**
	 * SOURCE_REQ.
	 */
	public static final String SOURCE_REQ = LCSProperties
			.get("com.burberry.integration.productapi.requiredattributes.source");
	/**
	 * SOURCE_IGNORE.
	 */
	public static final String SOURCE_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.source");
	/**
	 * SOURCE_ATT.
	 */
	public static final String SOURCE_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.source");
	/**
	 * SUP_ATT.
	 */
	public static final String SUP_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.sourcesupplier");
	/**
	 * SUP_IGNORE.
	 */
	public static final String SUP_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.sourcesupplier");
	/**
	 * CC_REQ.
	 */
	public static final String CC_REQ = LCSProperties
			.get("com.burberry.integration.productapi.requiredattributes.commoditycode");
	/**
	 * CC_IGNORE.
	 */
	public static final String CC_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.commoditycode");
	/**
	 * CC_ATT.
	 */
	public static final String CC_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.commoditycode");
	/**
	 * PH_REQ.
	 */
	public static final String PH_REQ = LCSProperties
			.get("com.burberry.integration.productapi.requiredattributes.placeholder");
	/**
	 * PH_IGNORE.
	 */
	public static final String PH_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.placeholder");
	/**
	 * PH_ATT.
	 */
	public static final String PH_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.placeholder");
	/**
	 * SEASON_REQ.
	 */
	public static final String SEASON_REQ = LCSProperties
			.get("com.burberry.integration.productapi.requiredattributes.season");
	/**
	 * SEASON_IGNORE.
	 */
	public static final String SEASON_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.season");
	/**
	 * SEASON_ATT.
	 */
	public static final String SEASON_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.season");
	/**
	 * SKU_REQ.
	 */
	public static final String SKU_REQ = LCSProperties
			.get("com.burberry.integration.productapi.requiredattributes.colourway");
	/**
	 * SKU_IGNORE.
	 */
	public static final String SKU_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.colourway");
	/**
	 * SKU_ATT.
	 */
	public static final String SKU_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.colourway");
	/**
	 * PRODUCT_SEASON_REQ.
	 */
	public static final String PRODUCT_SEASON_REQ = LCSProperties
			.get("com.burberry.integration.productapi.requiredattributes.productseason");
	/**
	 * PRODUCT_SEASON_IGNORE.
	 */
	public static final String PRODUCT_SEASON_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.productseason");
	/**
	 * PRODUCT_SEASON_ATT.
	 */
	public static final String PRODUCT_SEASON_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.productseason");
	/**
	 * MAT_REQ.
	 */
	public static final String MAT_REQ = LCSProperties
			.get("com.burberry.integration.productapi.requiredattributes.material");
	/**
	 * MAT_IGNORE.
	 */
	public static final String MAT_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.material");
	/**
	 * MAT_ATT.
	 */
	public static final String MAT_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.material");

	/**
	 * STYLE_REQ.
	 */
	public static final String STYLE_REQ = LCSProperties
			.get("com.burberry.integration.productapi.requiredattributes.product");
	/**
	 * STYLE_IGNORE.
	 */
	public static final String STYLE_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.product");
	/**
	 * STYLE_ATT.
	 */
	public static final String STYLE_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.product");
	/**
	 * RM_REQ.
	 */
	public static final String RM_REQ = LCSProperties
			.get("com.burberry.integration.productapi.requiredattributes.riskmanagement");
	/**
	 * RM_IGNORE.
	 */
	public static final String RM_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.riskmanagement");
	/**
	 * RM_ATT.
	 */
	public static final String RM_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.riskmanagement");
	/**
	 * SKU_SEASON_REQ.
	 */
	public static final String SKU_SEASON_REQ = LCSProperties
			.get("com.burberry.integration.productapi.requiredattributes.colourwayseason");
	/**
	 * SKU_SEASON_IGNORE.
	 */
	public static final String SKU_SEASON_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.colourwayseason");

	/**
	 * SKU_SEASON_ATT.
	 */
	public static final String SKU_SEASON_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.colourwayseason");

	/**
	 * STR_PRODUCT_DUPLICATE_APPAREL_ATT.
	 */
	public static final String STR_PRODUCT_DUPLICATE_APPAREL_ATT = LCSProperties
			.get("com.burberry.integration.alias.product");

	/**
	 * STR_MATERIAL_DUPLICATE_ATT.
	 */
	public static final String STR_MATERIAL_DUPLICATE_ATT = LCSProperties
			.get("com.burberry.integration.alias.material");
	/**
	 * STR_PRODUCT_DUPLICATE_APPAREL_ATT.
	 */
	public static final String STR_PRODUCT_ALIAS_APPAREL_ATT = "com.burberry.integration.alias.product.";

	/**
	 * STR_MATERIAL_DUPLICATE_ATT.
	 */
	public static final String STR_MATERIAL_ALIAS_ATT = "com.burberry.integration.alias.material.";

	/**
	 * STR_SPACE.
	 */
	public static final String STR_SPACE = " ";

	/**
	 * STR_OPERATIONAL_CATEGORY.
	 */
	public static final String STR_OPERATIONAL_CATEGORY = "Operational Category";

	/**
	 * STR_PRODUCTAPI_VALID_OBJECTS.
	 */
	public static final String STR_PRODUCTAPI_VALID_OBJECTS = LCSProperties
			.get("com.burberry.integration.productapi.validObjects");

	/**
	 * PLACEHOLDER_REF.
	 */
	public static final String PLACEHOLDER_REF = "placeholderReference.key.id";

	/**
	 * STR_PERCENTAGE.
	 */
	public static final String STR_PERCENTAGE = "%";

	/**
	 * EFFECTSEQ.
	 */
	public static final String EFFECTSEQ = "effectSequence";

	/**
	 * MATERIALTYPE.
	 */
	public static final String MATERIALTYPE = "vrdType";

	/**
	 * SOLE_TYPE.
	 */
	public static final String SOLE_TYPE = LCSProperties
			.get("com.burberry.integration.material.footwear.sole");

	/**
	 * BUR_MAT_TYPE.
	 */
	public static final String BUR_MAT_TYPE = LCSProperties
			.get("com.burberry.integration.material.footwear.sole.materialType");

	/**
	 * STR_MATERIAL_API_VALID_OBJECTS.
	 */
	public static final String STR_MATERIAL_API_VALID_OBJECTS = LCSProperties
			.get("com.burberry.integration.materialapi.validObjects");
	/**
	 * SEASONREMOVED.
	 */
	public static final String SEASONREMOVED = "seasonremoved";
	/**
	 * LOG_ENTRY_FAILED_ENABLED.
	 */
	public static final String LOG_ENTRY_FAILED_ENABLED = LCSProperties
			.get("com.burberry.integration.logentry.failed.enabled");

	/**
	 * LOG_ENTRY_SUCCESS_ENABLED.
	 */
	public static final String LOG_ENTRY_SUCCESS_ENABLED = LCSProperties
			.get("com.burberry.integration.logentry.success.enabled");

	/**
	 * JSON_IMAGE_PAGE.
	 */
	public static final String JSON_IMAGE_PAGE = LCSProperties.get(JSON_KEY
			+ ".imagespage");

	/**
	 * JSON_IMAGE_PAGE_SYSTEM_KEY.
	 */
	public static final String JSON_IMAGE_PAGE_SYSTEM_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".imagespage");

	/**
	 * SOURCE_SEASON_ATT.
	 */
	public static final String IMAGE_PAGE_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.imagespage");

	/**
	 * SOURCE_SEASON_IGNORE.
	 */
	public static final String IMAGE_PAGE_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.imagespage");

	/**
	 * SOURCE_SEASON_REQ.
	 */
	public static final String IMAGE_PAGE_REQ = LCSProperties
			.get("com.burberry.integration.productapi.requiredattributes.imagespage");

	/**
	 * JSON_SOURCE_SEASON.
	 */
	public static final String JSON_SOURCE_SEASON = LCSProperties.get(JSON_KEY
			+ ".sourceseason");

	/**
	 * JSON_SOURCE_SEASON_SYSTEM_KEY.
	 */
	public static final String JSON_SOURCE_SEASON_SYSTEM_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".sourceseason");

	/**
	 * SOURCE_SEASON_ATT.
	 */
	public static final String SOURCE_SEASON_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.sourceseason");

	/**
	 * SOURCE_SEASON_IGNORE.
	 */
	public static final String SOURCE_SEASON_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.sourceseason");

	/**
	 * SOURCE_SEASON_REQ.
	 */
	public static final String SOURCE_SEASON_REQ = LCSProperties
			.get("com.burberry.integration.productapi.requiredattributes.sourceseason");

	/**
	 * JSON_SPECIFICATION.
	 */
	public static final String JSON_SPECIFICATION = LCSProperties.get(JSON_KEY
			+ ".specification");

	/**
	 * JSON_SPECIFICATION_SYSTEM_KEY.
	 */
	public static final String JSON_SPECIFICATION_SYSTEM_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".specification");

	/**
	 * SPECIFICATION_ATT.
	 */
	public static final String SPECIFICATION_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.specification");

	/**
	 * SPECIFICATION_IGNORE.
	 */
	public static final String SPECIFICATION_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.specification");

	/**
	 * SPECIFICATION_REQ.
	 */
	public static final String SPECIFICATION_REQ = LCSProperties
			.get("com.burberry.integration.productapi.requiredattributes.specification");

	/**
	 * NAME_ATTRIBUTES.
	 */
	public static final String NAME_ATTRIBUTES = LCSProperties
			.get("com.burberry.integration.reportingapi.name");

	/**
	 * JSON_DOCUMENT_ATT.
	 */
	public static final String JSON_DOCUMENT_ATT = LCSProperties.get(JSON_KEY
			+ ".document");

	/**
	 * JSON_DOCUMENT_SYSTEM_ATT.
	 */
	public static final String JSON_DOCUMENT_SYSTEM_ATT = LCSProperties
			.get(JSON_SYSTEM_KEY + ".document");

	/**
	 * DOCUMENT_IGNORE.
	 */
	public static final String DOCUMENT_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.document");

	/**
	 * DOCUMENT_ATT.
	 */
	public static final String DOCUMENT_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.document");

	/**
	 * DOCUMENT_REQ.
	 */
	public static final String DOCUMENT_REQ = LCSProperties
			.get("com.burberry.integration.productapi.requiredattributes.document");

	/**
	 * LOG_ENTRY_BEGIN_DATE.
	 */
	public static final String LOG_ENTRY_BEGIN_DATE = LCSProperties
			.get("com.burberry.integration.reportingapi.logentry.beginDate");

	/**
	 * LOG_ENTRY_END_DATE.
	 */
	public static final String LOG_ENTRY_END_DATE = LCSProperties
			.get("com.burberry.integration.reportingapi.logentry.endDate");

	/**
	 * LOG_ENTRY_PARAMETERS_PASSED.
	 */
	public static final String LOG_ENTRY_PARAMETERS_PASSED = LCSProperties
			.get("com.burberry.integration.reportingapi.logentry.parametersPassed");

	/**
	 * LOG_ENTRY_STATUS.
	 */
	public static final String LOG_ENTRY_STATUS = LCSProperties
			.get("com.burberry.integration.reportingapi.logentry.status");

	/**
	 * LOG_ENTRY_STATUS_MESSAGE.
	 */
	public static final String LOG_ENTRY_STATUS_MESSAGE = LCSProperties
			.get("com.burberry.integration.reportingapi.logentry.statusMessage");

	/**
	 * LOG_ENTRY_TOTAL_TIME.
	 */
	public static final String LOG_ENTRY_TOTAL_TIME = LCSProperties
			.get("com.burberry.integration.reportingapi.logentry.totalTime");

	/**
	 * THUMBNAIL_LOCATION.
	 */
	public static final String THUMBNAIL_LOCATION = "thumbnailLocation";

	/**
	 * CHECK_IN_COMMENT.
	 */
	public static final String DOCUMENT_TYPE = "docType";

	/**
	 * PRIMARY_CONTENT_URL.
	 */
	public static final String PRIMARY_CONTENT_URL = "primaryContentURL";

	/**
	 * CHECK_IN_COMMENT.
	 */
	public static final String CHECK_IN_COMMENT = "checkinComment";

	/**
	 * DOCUMENT_SEASON.
	 */
	public static final String DOCUMENT_SEASON = "docSeason";

	/**
	 * SEASON_BRANCHID.
	 */
	public static final String SEASON_BRANCHID = "LCSSEASON.BRANCHIDITERATIONINFO";
	/**
	 * STR_IDA2A2.
	 */
	public static final String STR_IDA2A2 = "ida2a2";

	/**
	 * COLOURWAY_SEASON_ATT.
	 */
	public static final String COLOURWAY_SEASON_ATT_BURSEASON = "burSeason";

	/**
	 * SKUNAME.
	 */
	public static final String SKUNAME = "skuName";
	/**
	 * BUR_RUNWAY.
	 */
	public static final Object BUR_RUNWAY = LCSProperties
			.get("com.burberry.integration.runway");

	/**
	 * PART_TYPE_NAME.
	 */
	public static final String PART_TYPE_NAME = "partTypeName";

	/**
	 * OID.
	 */
	public static final String OID = "OID";

	/**
	 * NAM.
	 */
	public static final String NAM = "NAME";

	/**
	 * VRD_CS_NUM.
	 */
	public static final Object VRD_CS_NUM = LCSProperties
			.get("com.burberry.integration.costsheetnum");

	/**
	 * HYPERLINK_URL.
	 */
	public static final String HYPERLINK_URL = "url";
	/**
	 * LCSMOAOBJECT.
	 */
	public static final String LCSMOAOBJECT = "LCSMOAOBJECT";
	/**
	 * JSON_CRUD_KEY.
	 */
	public static final String JSON_CRUD_KEY = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.common");
	/**
	 * STR_LCSMOAOBJ_IDA2A2.
	 */
	public static final String STR_LCSMOAOBJ_IDA2A2 = "LCSMOAOBJECT.IDA2A2";
	/**
	 * LCSMOAOBJ_FLEXTYPEID_PATH.
	 */
	public static final String LCSMOAOBJ_FLEXTYPEID_PATH = "LCSMOAOBJECT.FLEXTYPEIDPATH";
	/**
	 * BUR_CORE.
	 */
	public static final Object BUR_CORE =LCSProperties.get("com.burberry.integration.core");

}
