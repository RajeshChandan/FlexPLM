package com.burberry.wc.integration.productcostingapi.constant;

import com.lcs.wc.util.LCSProperties;

public final class BurProductCostingConstant {

	/**
	 * BurProductCostingConstant.
	 */
	private BurProductCostingConstant() {

	}

	/**
	 * STR_PRODUCT_COSTING_API_ERROR_MSG.
	 */
	public static final String STR_PRODUCT_COSTING_API_ERROR_MSG = LCSProperties
			.get("com.burberry.integration.productcostingapi.errormessage");

	/**
	 * STR_PRODUCT_COSTING_API_VALID_OBJECTS.
	 */
	public static final String STR_PRODUCT_COSTING_API_VALID_OBJECTS = LCSProperties
			.get("com.burberry.integration.productcostingapi.validObjects");

	/**
	 * PRODUCT_COSTING_API_LOG_ENTRY_FLEXTYPE.
	 */
	public static final String PRODUCT_COSTING_API_LOG_ENTRY_FLEXTYPE = LCSProperties
			.get("com.burberry.integration.productcostingapi.logentry");

	/**
	 * LCSCOSTSHEET.
	 */
	public static final String LCSPRODUCTCOSTSHEET = "LCSPRODUCTCOSTSHEET";

	/**
	 * SOURCINGCONFIGREVID.
	 */
	public static final String SOURCINGCONFIGREVID = "SOURCINGCONFIGREVID";

	/**
	 * PLACEHOLDER.
	 */
	public static final String PLACEHOLDER = "placeholder";

	/**
	 * ROLEBOBJECTREF.
	 */
	public static final String ROLEBOBJECTREF = "roleBObjectRef.key.id";

	/**
	 * COSTSHEET_TO_COLOR_LINK.
	 */
	public static final String COSTSHEET_TO_COLOR_LINK = "CostSheetToColorLink";

	/**
	 * JSON_KEY.
	 */
	public static final String JSON_KEY = "com.burberry.integration.productcostingapi.jsonattributes.mapping";

	/**
	 * JSON_SYSTEM_KEY.
	 */
	public static final String JSON_SYSTEM_KEY = "com.burberry.integration.productcostingapi.system.jsonattributes.mapping";

	/**
	 * PRODUCT_REQ.
	 */
	public static final String PRODUCT_REQ = LCSProperties
			.get("com.burberry.integration.productcostingapi.requiredattributes.product");

	/**
	 * PRODUCT_IGNORE.
	 */
	public static final String PRODUCT_IGNORE = LCSProperties
			.get("com.burberry.integration.productcostingapi.ignoreattributes.product");

	/**
	 * PRODUCT_ATT.
	 */
	public static final String PRODUCT_ATT = LCSProperties
			.get("com.burberry.integration.productcostingapi.jsonattributes.product");

	/**
	 * JSON_PRODUCT_KEY.
	 */
	public static final String JSON_PRODUCT_KEY = LCSProperties.get(JSON_KEY
			+ ".product");

	/**
	 * JSON_SYSTEM_PRODUCT_KEY.
	 */
	public static final String JSON_SYSTEM_PRODUCT_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".product");

	/**
	 * SOURCE_REQ.
	 */
	public static final String SOURCE_REQ = LCSProperties
			.get("com.burberry.integration.productcostingapi.requiredattributes.source");

	/**
	 * SOURCE_IGNORE.
	 */
	public static final String SOURCE_IGNORE = LCSProperties
			.get("com.burberry.integration.productcostingapi.ignoreattributes.source");

	/**
	 * SOURCE_ATT.
	 */
	public static final String SOURCE_ATT = LCSProperties
			.get("com.burberry.integration.productcostingapi.jsonattributes.source");

	/**
	 * JSON_SOURCE_KEY.
	 */
	public static final String JSON_SOURCE_KEY = LCSProperties.get(JSON_KEY
			+ ".source");

	/**
	 * JSON_SYSTEM_SOURCE_KEY.
	 */
	public static final String JSON_SYSTEM_SOURCE_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".source");

	/**
	 * COSTSHEET_REQ.
	 */
	public static final String COSTSHEET_REQ = LCSProperties
			.get("com.burberry.integration.productcostingapi.requiredattributes.costsheet");

	/**
	 * COSTSHEET_IGNORE.
	 */
	public static final String COSTSHEET_IGNORE = LCSProperties
			.get("com.burberry.integration.productcostingapi.ignoreattributes.costsheet");

	/**
	 * COSTSHEET_ATT.
	 */
	public static final String COSTSHEET_ATT = LCSProperties
			.get("com.burberry.integration.productcostingapi.jsonattributes.costsheet");

	/**
	 * JSON_COSTSHEET_KEY.
	 */
	public static final String JSON_COSTSHEET_KEY = LCSProperties.get(JSON_KEY
			+ ".costsheet");

	/**
	 * JSON_SYSTEM_COSTSHEET_KEY.
	 */
	public static final String JSON_SYSTEM_COSTSHEET_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".costsheet");

	/**
	 * COLOURWAY_REQ.
	 */
	public static final String COLOURWAY_REQ = LCSProperties
			.get("com.burberry.integration.productcostingapi.requiredattributes.colourway");

	/**
	 * COLOURWAY_IGNORE.
	 */
	public static final String COLOURWAY_IGNORE = LCSProperties
			.get("com.burberry.integration.productcostingapi.ignoreattributes.colourway");

	/**
	 * COLOURWAY_ATT.
	 */
	public static final String COLOURWAY_ATT = LCSProperties
			.get("com.burberry.integration.productcostingapi.jsonattributes.colourway");

	/**
	 * JSON_COLOURWAY_KEY.
	 */
	public static final String JSON_COLOURWAY_KEY = LCSProperties.get(JSON_KEY
			+ ".colourway");

	/**
	 * JSON_SYSTEM_COLOURWAY_KEY.
	 */
	public static final String JSON_SYSTEM_COLOURWAY_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".colourway");

	/**
	 * PRIMARY_SOURCE.
	 */
	public static final String PRIMARY_SOURCE = "primarySource";

	/**
	 * PRIMARY_COST_SHEET.
	 */
	public static final String PRIMARY_COST_SHEET = "primaryCostSheet";

	/**
	 * SEASONNAME.
	 */
	public static final String SEASONNAME = "seasonName";

	/**
	 * REP_COLOURWAY.
	 */
	public static final String REP_COLOURWAY = "repColourway";

	/**
	 * PRODUCT_SIZE.
	 */
	public static final String PRODUCT_SIZE = "sizeDefinition";

	/**
	 * SPEC.
	 */
	public static final String SPEC = "specification";
	
	// CR R26: Handle Remove Cost Sheet Customisation : Start

    /**
     * MOA_TRACK_COST_SHEET_FLEX_TYPE.
     */
    public static final String MOA_TRACK_COST_SHEET_FLEX_TYPE = LCSProperties
            .get("com.burberry.wc.costsheet.burberrycostsheet.multiobject.flextypeid");

    /**
     * BO_TRACK_COST_SHEET_MOA_ATTRIBUTE.
     */
    public static final String BO_TRACK_COST_SHEET_MOA_ATTRIBUTE = LCSProperties
            .get("com.burberry.wc.costsheet.burberrycostsheet.businessbject.moaAttribute");

    /**
     * MOA_TRACK_COST_SHEET_ID.
     */
    public static final String MOA_TRACK_COST_SHEET_ID = LCSProperties
            .get("com.burberry.wc.costsheet.burberrycostsheet.multiobject.costsheetid");

    /**
     * MOA_TRACK_SOURCING_ID.
     */
    public static final String MOA_TRACK_COST_SHEET_SOURCING_ID = LCSProperties
            .get("com.burberry.wc.costsheet.burberrycostsheet.multiobject.sourcingid");

    /**
     * MOA_TRACK_PRODUCT_ID.
     */
    public static final String MOA_TRACK_COST_SHEET_PRODUCT_ID = LCSProperties
            .get("com.burberry.wc.costsheet.burberrycostsheet.multiobject.productid");

	/**
	 * CSUNIQID.
	 */
	public static final String CSUNIQID = LCSProperties
            .get("com.burberry.integration.productcostingapi.json.uniqueid.costsheet");

	/**
     * BO_TRACK_COST_SHEET_NAME.
     */
    public static final String BO_TRACK_COST_SHEET_NAME = LCSProperties
            .get("com.burberry.wc.costsheet.burberrycostsheet.businessobject.name");
	
	// CR R26: Handle Remove Cost Sheet Customisation : End

}
