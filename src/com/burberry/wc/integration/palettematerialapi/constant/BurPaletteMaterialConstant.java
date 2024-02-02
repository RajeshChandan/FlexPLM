package com.burberry.wc.integration.palettematerialapi.constant;

import com.lcs.wc.util.LCSProperties;

/**
 * Class hold reference of common constant used. All the constant will be used
 * in different classes. This class will be the central location to declare any
 * string/ char value as constant for Palette Material API. Most of constant
 * value will be read from property file to populate. ID used as constant should
 * have universal reference id from FLEX system. Most of the constant will start
 * with BUR_ which are specific to Flex field. These fields must have value
 * which are universal ID for the fields. So in future if DB changes it will
 * impact the running Java code.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurPaletteMaterialConstant {

	/**
	 * BurPaletteMaterialConstant.
	 */
	private BurPaletteMaterialConstant() {

	}

	/**
	 * STR_ERROR_MSG_PALATTE_MATERIAL_API.
	 */
	public static final String STR_ERROR_MSG_PALATTE_MATERIAL_API = LCSProperties
			.get("com.burberry.integration.palettematerialapi.errormessage");

	/**
	 * SUPPLIER_MASTERREFERENCE_KEY_ID.
	 */
	public static final String SUPPLIER_MASTERREFERENCE_KEY_ID = "supplierMasterReference.key.id";

	/**
	 * MATERIAL_MASTERREFERENCE_KEY_ID.
	 */
	public static final String MATERIAL_MASTERREFERENCE_KEY_ID = "materialMasterReference.key.id";

	/**
	 * COLOUR_REFERENCE_KEY_ID.
	 */
	public static final String COLOUR_REFERENCE_KEY_ID = "colorReference.key.id";

	/**
	 * PLACEHOLDER.
	 */
	public static final String PLACEHOLDER = "placeholder";

	/**
	 * STR_PALETTE_MATERIAL_VALID_OBJECTS.
	 */
	public static final String STR_PALETTE_MATERIAL_VALID_OBJECTS = LCSProperties
			.get("com.burberry.integration.palettematerialapi.validObjects");

	/**
	 * STR_GENERIC_CHILD_NODE_ATT.
	 */
	public static final String STR_GENERIC_CHILD_NODE_ATT = "com.burberry.integration.alias.";

	/**
	 * STR_MATERIAL_CHILD_NODE_ATT.
	 */
	public static final String STR_MATERIAL_CHILD_NODE_ATT = "com.burberry.integration.alias.material";

	/**
	 * STR_COLOUR_CHILD_NODE_ATT.
	 */
	public static final String STR_COLOUR_CHILD_NODE_ATT = "com.burberry.integration.alias.colour";

	/**
	 * MATERIAL_JSON_KEY.
	 */
	public static final String MATERIAL_JSON_KEY = "com.burberry.integration.palettematerialapi.jsonattributes.mapping";

	/**
	 * MATERIAL_JSON_SYSTEM_KEY.
	 */
	public static final String MATERIAL_JSON_SYSTEM_KEY = "com.burberry.integration.palettematerialapi.system.jsonattributes.mapping";

	/**
	 * JSON_MATERIAL_ATT.
	 */
	public static final String JSON_MATERIAL_ATT = LCSProperties
			.get(MATERIAL_JSON_KEY + ".material");

	/**
	 * JSON_MATERIAL_SYSTEM_ATT.
	 */
	public static final String JSON_MATERIAL_SYSTEM_ATT = LCSProperties
			.get(MATERIAL_JSON_SYSTEM_KEY + ".material");

	/**
	 * JSON_SUPPLIER_ATT.
	 */
	public static final String JSON_SUPPLIER_ATT = LCSProperties
			.get(MATERIAL_JSON_KEY + ".supplier");

	/**
	 * JSON_SUPPLIER_SYSTEM_ATT.
	 */
	public static final String JSON_SUPPLIER_SYSTEM_ATT = LCSProperties
			.get(MATERIAL_JSON_SYSTEM_KEY + ".supplier");

	/**
	 * JSON_PALETTE_ATT.
	 */
	public static final String JSON_PALETTE_ATT = LCSProperties
			.get(MATERIAL_JSON_KEY + ".palette");

	/**
	 * JSON_PALETTE_SYSTEM_ATT.
	 */
	public static final String JSON_PALETTE_SYSTEM_ATT = LCSProperties
			.get(MATERIAL_JSON_SYSTEM_KEY + ".palette");

	/**
	 * JSON_RISK_MANAGEMENT_ATT.
	 */
	public static final String JSON_RISK_MANAGEMENT_ATT = LCSProperties
			.get(MATERIAL_JSON_KEY + ".riskmanagement");

	/**
	 * JSON_RISK_MANAGEMENT_SYSTEM_ATT.
	 */
	public static final String JSON_RISK_MANAGEMENT_SYSTEM_ATT = LCSProperties
			.get(MATERIAL_JSON_SYSTEM_KEY + ".riskmanagement");

	/**
	 * JSON_YARN_DETAIL_ATT.
	 */
	public static final String JSON_YARN_DETAIL_ATT = LCSProperties
			.get(MATERIAL_JSON_KEY + ".yarndetail");

	/**
	 * JSON_YARN_DETAIL_SYSTEM_ATT.
	 */
	public static final String JSON_YARN_DETAIL_SYSTEM_ATT = LCSProperties
			.get(MATERIAL_JSON_SYSTEM_KEY + ".yarndetail");

	/**
	 * JSON_MATERIAL_PRICING_ENTRY_ATT.
	 */
	public static final String JSON_MATERIAL_PRICING_ENTRY_ATT = LCSProperties
			.get(MATERIAL_JSON_KEY + ".materialpricingentry");

	/**
	 * JSON_MATERIAL_PRICING_ENTRY_SYSTEM_ATT.
	 */
	public static final String JSON_MATERIAL_PRICING_ENTRY_SYSTEM_ATT = LCSProperties
			.get(MATERIAL_JSON_SYSTEM_KEY + ".materialpricingentry");

	/**
	 * JSON_COLOUR_ATT.
	 */
	public static final String JSON_COLOUR_ATT = LCSProperties
			.get(MATERIAL_JSON_KEY + ".colour");

	/**
	 * JSON_COLOUR_SYSTEM_ATT.
	 */
	public static final String JSON_COLOUR_SYSTEM_ATT = LCSProperties
			.get(MATERIAL_JSON_SYSTEM_KEY + ".colour");

	/**
	 * JSON_PAL_MAT_COLOUR_ATT.
	 */
	public static final String JSON_PAL_MAT_COLOUR_ATT = LCSProperties
			.get(MATERIAL_JSON_KEY + ".palettematerialcolour");

	/**
	 * JSON_PAL_MAT_COLOUR_SYSTEM_ATT.
	 */
	public static final String JSON_PAL_MAT_COLOUR_SYSTEM_ATT = LCSProperties
			.get(MATERIAL_JSON_SYSTEM_KEY + ".palettematerialcolour");

	/**
	 * JSON_MATERIAL_SUPPLIER_ATT.
	 */
	public static final String JSON_MATERIAL_SUPPLIER_ATT = LCSProperties
			.get(MATERIAL_JSON_KEY + ".materialsupplier");

	/**
	 * JSON_MATERIAL_SUPPLIER_SYSTEM_ATT.
	 */
	public static final String JSON_MATERIAL_SUPPLIER_SYSTEM_ATT = LCSProperties
			.get(MATERIAL_JSON_SYSTEM_KEY + ".materialsupplier");

	/**
	 * JSON_MATERIAL_ATT.
	 */
	public static final String JSON_MATERIAL_COLOUR_ATT = LCSProperties
			.get(MATERIAL_JSON_KEY + ".materialcolour");

	/**
	 * JSON_MATERIAL_COLOUR_SYSTEM_ATT.
	 */
	public static final String JSON_MATERIAL_COLOUR_SYSTEM_ATT = LCSProperties
			.get(MATERIAL_JSON_SYSTEM_KEY + ".materialcolour");

	/**
	 * MATERIAL_IGNORE.
	 */
	public static final String MATERIAL_IGNORE = LCSProperties
			.get("com.burberry.integration.palettematerialapi.ignoreattributes.material");

	/**
	 * PALETTE_IGNORE.
	 */
	public static final String PALETTE_IGNORE = LCSProperties
			.get("com.burberry.integration.palettematerialapi.ignoreattributes.palette");

	/**
	 * RISK_MANAGEMENT_IGNORE.
	 */
	public static final String RISK_MANAGEMENT_IGNORE = LCSProperties
			.get("com.burberry.integration.palettematerialapi.ignoreattributes.riskmanagement");

	/**
	 * YARN_DETAIL_IGNORE.
	 */
	public static final String YARN_DETAIL_IGNORE = LCSProperties
			.get("com.burberry.integration.palettematerialapi.ignoreattributes.yarndetail");

	/**
	 * MAT_PRICING_ENTRY_IGNORE.
	 */
	public static final String MAT_PRICING_ENTRY_IGNORE = LCSProperties
			.get("com.burberry.integration.palettematerialapi.ignoreattributes.materialpricingentry");

	/**
	 * COLOUR_IGNORE.
	 */
	public static final String COLOUR_IGNORE = LCSProperties
			.get("com.burberry.integration.palettematerialapi.ignoreattributes.colour");

	/**
	 * PAL_MAT_COLOUR_IGNORE.
	 */
	public static final String PAL_MAT_COLOUR_IGNORE = LCSProperties
			.get("com.burberry.integration.palettematerialapi.ignoreattributes.palettematerialcolour");

	/**
	 * MATERIAL_SUPPLIER_IGNORE.
	 */
	public static final String MATERIAL_SUPPLIER_IGNORE = LCSProperties
			.get("com.burberry.integration.palettematerialapi.ignoreattributes.materialsupplier");

	/**
	 * MATERIAL_COLOR_IGNORE.
	 */
	public static final String MATERIAL_COLOUR_IGNORE = LCSProperties
			.get("com.burberry.integration.palettematerialapi.ignoreattributes.materialcolour");

	/**
	 * SUPPLIER_IGNORE.
	 */
	public static final String SUPPLIER_IGNORE = LCSProperties
			.get("com.burberry.integration.palettematerialapi.ignoreattributes.supplier");

	/**
	 * SUPPLIER_ATT.
	 */
	public static final String SUPPLIER_ATT = LCSProperties
			.get("com.burberry.integration.palettematerialapi.jsonattributes.supplier");

	/**
	 * SUPPLIER_REQ.
	 */
	public static final String SUPPLIER_REQ = LCSProperties
			.get("com.burberry.integration.palettematerialapi.requiredattributes.supplier");

	/**
	 * MATERIAL_ATT.
	 */
	public static final String MATERIAL_ATT = LCSProperties
			.get("com.burberry.integration.palettematerialapi.jsonattributes.material");

	/**
	 * PALETTE_ATT.
	 */
	public static final String PALETTE_ATT = LCSProperties
			.get("com.burberry.integration.palettematerialapi.jsonattributes.palette");

	/**
	 * RISK_MGMT_ATT.
	 */
	public static final String RISK_MANAGEMENT_ATT = LCSProperties
			.get("com.burberry.integration.palettematerialapi.jsonattributes.riskmanagement");

	/**
	 * YARN_DETAIL_ATT.
	 */
	public static final String YARN_DETAIL_ATT = LCSProperties
			.get("com.burberry.integration.palettematerialapi.jsonattributes.yarndetail");

	/**
	 * MAT_PRICING_ATT.
	 */
	public static final String MAT_PRICING_ENTRY_ATT = LCSProperties
			.get("com.burberry.integration.palettematerialapi.jsonattributes.materialpricingentry");

	/**
	 * MATERIAL_ATT_RISK_MANAGEMENT.
	 */
	public static final String MATERIAL_RISK_MANAGEMENT_FLEXTYPE = LCSProperties
			.get("com.burberry.integration.alias.material.burRiskManagement");

	/**
	 * MATERIAL_YARN_DETAILS.
	 */
	public static final String MATERIAL_YARN_DETAILS = LCSProperties
			.get("com.burberry.integration.alias.material.burYarnDetails");

	/**
	 * COLOUR_ATT.
	 */
	public static final String COLOUR_ATT = LCSProperties
			.get("com.burberry.integration.palettematerialapi.jsonattributes.colour");

	/**
	 * PAL_MAT_COLOUR_ATT.
	 */
	public static final String PAL_MAT_COLOUR_ATT = LCSProperties
			.get("com.burberry.integration.palettematerialapi.jsonattributes.palettematerialcolour");

	/**
	 * PALETTE_REQ.
	 */
	public static final String PALETTE_REQ = LCSProperties
			.get("com.burberry.integration.palettematerialapi.requiredattributes.palette");

	/**
	 * RISKMANAGEMENT_REQ.
	 */
	public static final String RISKMANAGEMENT_REQ = LCSProperties
			.get("com.burberry.integration.palettematerialapi.requiredattributes.riskmanagement");

	/**
	 * YARN_DETAIL_REQ.
	 */
	public static final String YARN_DETAIL_REQ = LCSProperties
			.get("com.burberry.integration.palettematerialapi.requiredattributes.yarndetail");

	/**
	 * YARN_DETAIL_REQ.
	 */
	public static final String MAT_PRICING_ENTRY_REQ = LCSProperties
			.get("com.burberry.integration.palettematerialapi.requiredattributes.materialpricingentry");

	/**
	 * COLOUR_REQ.
	 */
	public static final String COLOUR_REQ = LCSProperties
			.get("com.burberry.integration.palettematerialapi.requiredattributes.colour");

	/**
	 * PAL_MAT_COLOUR_REQ.
	 */
	public static final String PAL_MAT_COLOUR_REQ = LCSProperties
			.get("com.burberry.integration.palettematerialapi.requiredattributes.palettematerialcolour");

	/**
	 * MATERIAL_SUPPLIER_ATT.
	 */
	public static final String MATERIAL_SUPPLIER_ATT = LCSProperties
			.get("com.burberry.integration.palettematerialapi.jsonattributes.materialsupplier");

	/**
	 * MATERIAL_COLOUR_ATT.
	 */
	public static final String MATERIAL_COLOUR_ATT = LCSProperties
			.get("com.burberry.integration.palettematerialapi.jsonattributes.materialcolour");

	/**
	 * MATERIAL_REQ.
	 */
	public static final String MATERIAL_REQ = LCSProperties
			.get("com.burberry.integration.palettematerialapi.requiredattributes.material");

	/**
	 * MATERIAL_SUPPLIER_REQ.
	 */
	public static final String MATERIAL_SUPPLIER_REQ = LCSProperties
			.get("com.burberry.integration.palettematerialapi.requiredattributes.materialsupplier");

	/**
	 * MATERIAL_COLOR_REQ.
	 */
	public static final String MATERIAL_COLOUR_REQ = LCSProperties
			.get("com.burberry.integration.palettematerialapi.requiredattributes.materialcolour");

	/**
	 * CREATESTAMP.
	 */
	public static final String CREATESTAMP = "createStamp";

	/**
	 * CREATOR.
	 */
	public static final String CREATOR = "creator";

	/**
	 * MODIFIER.
	 */
	public static final String MODIFIER = "modifier";

	/**
	 * MATERIAL_SUPPLIER_NAME.
	 */
	public static final String MATERIAL_SUPPLIER_NAME = "materialSupplierName";

	/**
	 * PALETTE_NAME.
	 */
	public static final String PALETTE_NAME = "paletteName";

	/**
	 * MATERIAL_COLOUR_NAME.
	 */
	public static final String MATERIAL_COLOUR_NAME = "materialColourName";

	/**
	 * COLOUR.
	 */
	public static final String COLOUR = "color";

	/**
	 * MAT_PRICING_IN_DATE.
	 */
	public static final String MAT_PRICING_IN_DATE = "inDate";

	/**
	 * MAT_PRICING_OUT_DATE.
	 */
	public static final String MAT_PRICING_OUT_DATE = "outDate";

	/**
	 * MAT_COLOUR_REFERENCE.
	 */
	public static final String MAT_COLOUR_REFERENCE = "materialColorReference";

	/**
	 * LCSMATERIAL_IDA2A2.
	 */
	public static final String LCSMATERIAL_IDA2A2 = "LCSMATERIAL.IDA2A2";

	/**
	 * LCSMATERIAL_ROOT_ID.
	 */
	public static final String LCSMATERIAL_ROOT_ID = "VR:com.lcs.wc.material.LCSMaterial:";

	/**
	 * MATERIAL_SUPPLIER_BRANCHID.
	 */
	public static final String MATERIAL_SUPPLIER_BRANCHID = "LCSMATERIALSUPPLIER.BRANCHIDITERATIONINFO";

	/**
	 * LCSMATERIALSUPPLIER_PREFIX.
	 */
	public static final String LCS_MATERIAL_SUPPLIER_PREFIX = "VR:com.lcs.wc.material.LCSMaterialSupplier:";

	/**
	 * PALETTE_BRANCHID.
	 */
	public static final String PALETTE_BRANCHID = "LCSPALETTE.BRANCHIDITERATIONINFO";

	/**
	 * MATERIAL_PRICING_ENTRY_ID.
	 */
	public static final String MATERIAL_PRICING_ENTRY_ID = "MATERIALPRICINGENTRY.IDA2A2";

	/**
	 * MATERIALPRICINGENTRY_PREFIX.
	 */
	public static final String MATERIAL_PRICING_ENTRY_PREFIX = "OR:com.lcs.wc.material.MaterialPricingEntry:";

	/**
	 * MATERIAL_COLOR_ID.
	 */
	public static final String MATERIAL_COLOR_ID = "LCSMATERIALCOLOR.IDA2A2";

	/**
	 * LCSMATERIALCOLOR_PREFIX.
	 */
	public static final String LCS_MATERIAL_COLOR_PREFIX = "OR:com.lcs.wc.material.LCSMaterialColor:";

	/**
	 * PALETTE_MATERIAL_LINK_ID.
	 */
	public static final String PALETTE_MATERIAL_LINK_ID = "LCSPALETTEMATERIALLINK.IDA2A2";

	/**
	 * PALETTE_MATERIAL_PREFIX.
	 */
	public static final String PALETTE_MATERIAL_PREFIX = "OR:com.lcs.wc.color.LCSPaletteMaterialLink:";

	/**
	 * PALETTE_MATERIAL_COLOUR_LINK_ID.
	 */
	public static final String PALETTE_MATERIAL_COLOUR_LINK_ID = "LCSPALETTEMATERIALCOLORLINK.IDA2A2";

	/**
	 * PALETTE_MATERIAL_COLOUR_PREFIX.
	 */
	public static final String PALETTE_MATERIAL_COLOUR_PREFIX = "OR:com.lcs.wc.color.LCSPaletteMaterialColorLink:";

	/**
	 * PALETTE_ID.
	 */
	public static final String PALETTE_ID = "LCSPALETTE.IDA2A2";

	/**
	 * PALETTE_PREFIX.
	 */
	public static final String PALETTE_PREFIX = "OR:com.lcs.wc.color.LCSPalette:";

	/**
	 * COLOR_ID.
	 */
	public static final String COLOR_ID = "LCSCOLOR.IDA2A2";

	/**
	 * LCSCOLOR_PREFIX.
	 */
	public static final String LCSCOLOR_PREFIX = "OR:com.lcs.wc.color.LCSColor:";

	/**
	 * DOCUEMENT_ID.
	 */
	public static final String DOCUMENT_ID = "LCSDOCUMENT.IDA2A2";

	/**
	 * LCSDOCUMENT_PREFIX.
	 */
	public static final String LCSDOCUMENT_PREFIX = "OR:com.lcs.wc.document.LCSDocument:";

	/**
	 * LCSPALETTEMATERIALCOLORLINK.
	 */
	public static final String LCSPALETTEMATERIALCOLORLINK = "LCSPALETTEMATERIALCOLORLINK";

	/**
	 * MATERIAL_MOA_YARN_DETAILS.
	 */
	public static final String MATERIAL_MOA_YARN_DETAIL_ATT = "burYarnDetails";

	/**
	 * STR_MATERIAL_SUB_TYPE.
	 */
	public static final String STR_MATERIAL_SUB_TYPE = "Material Sub Type";

	/**
	 * STR_MATERIAL_HYPEN_SUB_TYPE.
	 */
	public static final String STR_MATERIAL_HYPEN_SUB_TYPE = "Material Sub-Type";

	/**
	 * STR_SUPPLIER_SUB_TYPE.
	 */
	public static final String STR_SUPPLIER_SUB_TYPE = "Supplier Sub-Type";

	/**
	 * PRODUCT_API_LOG_ENTRY_FLEXTYPE.
	 */
	public static final String PRODUCT_API_LOG_ENTRY_FLEXTYPE = LCSProperties
			.get("com.burberry.integration.productapi.logentry");

	/**
	 * PALETTE_MATERIAL_API_LOG_ENTRY_FLEXTYPE.
	 */
	public static final String PALETTE_MATERIAL_API_LOG_ENTRY_FLEXTYPE = LCSProperties
			.get("com.burberry.integration.palettematerialapi.logentry");

	/**
	 * COLOR_HEXIDECIMAL.
	 */
	public static final String COLOR_HEXIDECIMAL = "colorHexidecimalValue";

	/**
	 * MATERIAL_PRICE_MANAGEMENT_FLEXTYPE.
	 */
	public static final String MATERIAL_PRICE_MANAGEMENT_FLEXTYPE = LCSProperties
			.get("com.burberry.integration.alias.material.vrdMaterialSize");

	/**
	 * MATERIAL_MOA_MATERIAL_PRICE_MGMT_ATT.
	 */
	public static final String MATERIAL_MOA_MATERIAL_PRICE_MGMT_ATT = "vrdMaterialSize";

	/**
	 * JSON_MATERIAL_PRICE_MGMT_ATT.
	 */
	public static final String JSON_MATERIAL_PRICE_MGMT_ATT = LCSProperties
			.get(MATERIAL_JSON_KEY + ".matPriceManagement");

	/**
	 * JSON_MATERIAL_PRICE_MGMT_SYSTEM_ATT.
	 */
	public static final String JSON_MATERIAL_PRICE_MGMT_SYSTEM_ATT = LCSProperties
			.get(MATERIAL_JSON_SYSTEM_KEY + ".matPriceManagement");

	/**
	 * MATERIAL_PRICE_MGMT_IGNORE.
	 */
	public static final String MATERIAL_PRICE_MGMT_IGNORE = LCSProperties
			.get("com.burberry.integration.palettematerialapi.ignoreattributes.matPriceManagement");

	/**
	 * MATERIAL_PRICE_MGMT_ATT.
	 */
	public static final String MATERIAL_PRICE_MGMT_ATT = LCSProperties
			.get("com.burberry.integration.palettematerialapi.jsonattributes.matPriceManagement");

	/**
	 * MATERIAL_PRICE_MGMT_REQ.
	 */
	public static final String MATERIAL_PRICE_MGMT_REQ = LCSProperties
			.get("com.burberry.integration.palettematerialapi.requiredattributes.matPriceManagement");
	
	/**
	 * COLOR_THUMBNAIL
	 */
	public static final Object COLOR_THUMBNAIL = "thumbnail";
	

	// CR R26: Handle Remove Object Customisation : Start

	/**
	 * LCSMATERIALCOLOR.
	 */
	public static final String LCSMATERIALCOLOR = "LCSMATERIALCOLOR";



	/**
	 * BO_TRACK_PALATTE_MATERIAL_COLOR_NAME.
	 */
	public static final String BO_TRACK_PALATTE_MATERIAL_COLOR_NAME = LCSProperties
			.get("com.burberry.wc.palette.burberrypalettematerialcolor.businessobject.name");

	/**
	 * BO_TRACK_PALATTE_MATERIAL_COLOR_MOA_ATTRIBUTE.
	 */
	public static final String BO_TRACK_PALATTE_MATERIAL_COLOR_MOA_ATTRIBUTE = LCSProperties
			.get("com.burberry.wc.palette.burberrypalettematerialcolor.businessbject.moaAttribute");

	/**
	 * MOA_TRACK_PALATTE_MATERIAL_COLOR_FLEX_TYPE.
	 */
	public static final String MOA_TRACK_PALATTE_MATERIAL_COLOR_FLEX_TYPE = LCSProperties
			.get("com.burberry.wc.palette.burberrypalettematerialcolor.multiobject.flextypeid");

	/**
	 * MOA_TRACK_PALATTE_MATERIAL_COLOR_MATERIAL_ID.
	 */
	public static final String MOA_TRACK_PALATTE_MATERIAL_COLOR_ATT_MATERIAL_ID = LCSProperties
			.get("com.burberry.wc.palette.burberrypalettematerialcolor.multiobject.materialid");

	/**
	 * MOA_TRACK_PALATTE_MATERIAL_COLOR_ATT_MATERIAL_COLOR_ID.
	 */
	public static final String MOA_TRACK_PALATTE_MATERIAL_COLOR_ATT_MATERIAL_COLOR_ID = LCSProperties
			.get("com.burberry.wc.palette.burberrypalettematerialcolor.multiobject.materialcolorid");

	/**
	 * MOA_TRACK_PALATTE_MATERIAL_COLOR_ATT_PALETTE_NAME.
	 */
	public static final String MOA_TRACK_PALATTE_MATERIAL_COLOR_ATT_PALETTE_NAME = LCSProperties
			.get("com.burberry.wc.palette.burberrypalettematerialcolor.multiobject.palettename");

	/**
	 * MOA_TRACK_PALATTE_MATERIAL_COLOR_ATT_MODIFY_TIMESTAMP.
	 */
	public static final String MOA_TRACK_PALATTE_MATERIAL_COLOR_ATT_MODIFY_TIMESTAMP = LCSProperties
			.get("com.burberry.wc.palette.burberrypalettematerialcolor.multiobject.modifytimestamp");


    /**
     * BO_TRACK_YARN_DETAILS_NAME.
     */
    public static final String BO_TRACK_YARN_DETAILS_NAME = LCSProperties
            .get("com.burberry.wc.moa.burberryyarndetails.businessobject.name");

    /**
     * BO_TRACK_YARN_DETAILS_MOA_ATTRIBUTE.
     */
    public static final String BO_TRACK_YARN_DETAILS_MOA_ATTRIBUTE = LCSProperties
            .get("com.burberry.wc.moa.burberryyarndetails.businessbject.moaAttribute");

    /**
     * MOA_TRACK_YARN_DETAILS_FLEX_TYPE.
     */
    public static final String MOA_TRACK_YARN_DETAILS_FLEX_TYPE = LCSProperties
            .get("com.burberry.wc.moa.burberryyarndetails.multiobject.flextypeid");

    /**
     * MOA_TRACK_YARN_DETAILS_MOA_OBJECT_ID.
     */
    public static final String MOA_TRACK_YARN_DETAILS_MOA_OBJECT_ID = LCSProperties
            .get("com.burberry.wc.moa.burberryriskmanagement.multiobject.moaobjectid");

    /**
     * MOA_TRACK_YARN_DETAILS_OWNER_ID.
     */
    public static final String MOA_TRACK_YARN_DETAILS_OWNER_ID = LCSProperties
            .get("com.burberry.wc.moa.burberryriskmanagement.multiobject.ownerid");

    /**
     * MOA_TRACK_YARN_DETAILS_OWNER_TYPE.
     */
    public static final String MOA_TRACK_YARN_DETAILS_OWNER_TYPE = LCSProperties
            .get("com.burberry.wc.moa.burberryriskmanagement.multiobject.ownertype");

	/**
	 * BO_TRACK_RISK_MANAGEMENT_NAME.
	 */
	public static final String BO_TRACK_RISK_MANAGEMENT_NAME = LCSProperties
			.get("com.burberry.wc.moa.burberryriskmanagement.businessobject.name");

	/**
	 * MOA_TRACK_RISK_MANAGEMENT_FLEX_TYPE.
	 */
	public static final String MOA_TRACK_RISK_MANAGEMENT_FLEX_TYPE = LCSProperties
			.get("com.burberry.wc.moa.burberryriskmanagement.multiobject.flextypeid");

	/**
	 * BO_TRACK_RISK_MANAGEMENT_MOA_ATTRIBUTE.
	 */
	public static final String BO_TRACK_RISK_MANAGEMENT_MOA_ATTRIBUTE = LCSProperties
			.get("com.burberry.wc.moa.burberryriskmanagement.businessbject.moaAttribute");

	/**
	 * MOA_TRACK_RISK_MANAGEMENT_MOA_OBJECT_ID.
	 */
	public static final String MOA_TRACK_RISK_MANAGEMENT_MOA_OBJECT_ID = LCSProperties
			.get("com.burberry.wc.moa.burberryriskmanagement.multiobject.moaobjectid");

	/**
	 * MOA_TRACK_RISK_MANAGEMENT_OWNER_ID.
	 */
	public static final String MOA_TRACK_RISK_MANAGEMENT_OWNER_ID = LCSProperties
			.get("com.burberry.wc.moa.burberryriskmanagement.multiobject.ownerid");

	/**
	 * MOA_TRACK_RISK_MANAGEMENT_OWNER_TYPE.
	 */
	public static final String MOA_TRACK_RISK_MANAGEMENT_OWNER_TYPE = LCSProperties
			.get("com.burberry.wc.moa.burberryriskmanagement.multiobject.ownertype");
	


    /**
     * BO_TRACK_MATERIAL_PRICE_MANAGEMENT_NAME.
     */
    public static final String BO_TRACK_MATERIAL_PRICE_MANAGEMENT_NAME = LCSProperties
            .get("com.burberry.wc.moa.burberrymaterialpricemanagement.businessobject.name");

    /**
     * BO_TRACK_MATERIAL_PRICE_MANAGEMENT_MOA_ATTRIBUTE.
     */
    public static final String BO_TRACK_MATERIAL_PRICE_MANAGEMENT_MOA_ATTRIBUTE = LCSProperties
            .get("com.burberry.wc.moa.burberrymaterialpricemanagement.businessbject.moaAttribute");

    /**
     * MOA_TRACK_MATERIAL_PRICE_MANAGEMENT_FLEX_TYPE.
     */
    public static final String MOA_TRACK_MATERIAL_PRICE_MANAGEMENT_FLEX_TYPE = LCSProperties
            .get("com.burberry.wc.moa.burberrymaterialpricemanagement.multiobject.flextypeid");

    /**
     * MOA_TRACK_MATERIAL_PRICE_MANAGEMENT_MOA_OBJECT_ID.
     */
    public static final String MOA_TRACK_MATERIAL_PRICE_MANAGEMENT_MOA_OBJECT_ID = LCSProperties
            .get("com.burberry.wc.moa.burberryriskmanagement.multiobject.moaobjectid");

    /**
     * MOA_TRACK_MATERIAL_PRICE_MANAGEMENT_OWNER_ID.
     */
    public static final String MOA_TRACK_MATERIAL_PRICE_MANAGEMENT_OWNER_ID = LCSProperties
            .get("com.burberry.wc.moa.burberryriskmanagement.multiobject.ownerid");

    /**
     * MOA_TRACK_MATERIAL_PRICE_MANAGEMENT_OWNER_TYPE.
     */
    public static final String MOA_TRACK_MATERIAL_PRICE_MANAGEMENT_OWNER_TYPE = LCSProperties
            .get("com.burberry.wc.moa.burberryriskmanagement.multiobject.ownertype");
    

    /**
     * BO_TRACK_MATERIAL_DOCUMENT_MOA_ATTRIBUTE.
     */
    public static final String BO_TRACK_MATERIAL_DOCUMENT_MOA_ATTRIBUTE = LCSProperties
            .get("com.burberry.wc.document.burberrymaterialdocument.businessobject.moaAttribute");

    /**
     * BO_TRACK_MATERIAL_DOCUMENT_NAME.
     */
    public static final String BO_TRACK_MATERIAL_DOCUMENT_NAME = LCSProperties
            .get("com.burberry.wc.document.burberrymaterialdocument.businessobject.name");

    /**
     * MOA_TRACK_MATERIAL_DOCUMENT_FLEX_TYPE.
     */
    public static final String MOA_TRACK_MATERIAL_DOCUMENT_FLEX_TYPE = LCSProperties
            .get("com.burberry.wc.document.burberrymaterialdocument.multiobject.flextypeid");

    /**
     * MOA_TRACK_MATERIAL_ID.
     */
    public static final String MOA_TRACK_MATERIAL_ID = LCSProperties
            .get("com.burberry.wc.palette.burberrypalettematerialcolor.multiobject.materialid");

    /**
     * MOA_TRACK_DOCUMENT_ID.
     */
    public static final String MOA_TRACK_DOCUMENT_ID = LCSProperties
            .get("com.burberry.wc.document.burberrymaterialdocument.multiobject.documenttid");

    /**
     * BO_TRACK_MATERIAL_PRICING_ENTRY_MOA_ATTRIBUTE.
     */
    public static final String BO_TRACK_MATERIAL_PRICING_ENTRY_MOA_ATTRIBUTE = LCSProperties
            .get("com.burberry.wc.material.burrberryprice.businessobject.moaAttribute");

    /**
     * BO_TRACK_MATERIAL_PRICING_ENTRY_NAME.
     */
    public static final String BO_TRACK_MATERIAL_PRICING_ENTRY_NAME = LCSProperties
            .get("com.burberry.wc.material.burrberryprice.businessobject.name");

    /**
     * MOA_TRACK_MATERIAL_PRICING_ENTRY_FLEX_TYPE.
     */
    public static final String MOA_TRACK_MATERIAL_PRICING_ENTRY_FLEX_TYPE = LCSProperties
            .get("com.burberry.wc.material.burrberryprice.multiobject.flextypeid");

    /**
     * MOA_TRACK_OBJECT_ID.
     */
    public static final String MOA_TRACK_OBJECT_ID = LCSProperties
            .get("com.burberry.wc.material.burrberryprice.multiobject.objectid");

    /**
     * MOA_TRACK_MATERIAL_SUPPLIER_ID.
     */
    public static final String MOA_TRACK_MATERIAL_SUPPLIER_ID = LCSProperties
            .get("com.burberry.wc.material.burrberryprice.multiobject.materialsupplierid");

    /**
     * MOA_TRACK_MATERIAL_COLOR_ID.
     */
    public static final String MOA_TRACK_MATERIAL_COLOR_ID = LCSProperties
            .get("com.burberry.wc.palette.burberrypalettematerialcolor.multiobject.materialcolorid");
    
    /**
     * RISK_MANAGEMENT_JSON_UNIQUE_ID.
     */
    public static final String RISK_MANAGEMENT_JSON_UNIQUE_ID = LCSProperties
            .get("com.burberry.integration.palettematerialapi.json.uniqueid.riskmanagement");
    
    /**
     * YARN_DETAIL_JSON_UNIQUE_ID.
     */
    public static final String YARN_DETAIL_JSON_UNIQUE_ID = LCSProperties
            .get("com.burberry.integration.palettematerialapi.json.uniqueid.yarndetail");
    
    /**
     * MATERIAL_PRICE_ENTRY_JSON_UNIQUE_ID.
     */
    public static final String MATERIAL_PRICE_ENTRY_JSON_UNIQUE_ID = LCSProperties
            .get("com.burberry.integration.palettematerialapi.json.uniqueid.materialpricingentry");
    
    /**
     * MATERIAL_PRICE_MANAGEMENT_JSON_UNIQUE_ID.
     */
    public static final String MATERIAL_PRICE_MANAGEMENT_JSON_UNIQUE_ID = LCSProperties
            .get("com.burberry.integration.palettematerialapi.json.uniqueid.matPriceManagement");
        
    
    /**
     * MATERIAL_DOCUMENT_JSON_UNIQUE_ID.
     */
    public static final String MATERIAL_DOCUMENT_JSON_UNIQUE_ID = LCSProperties
            .get("com.burberry.integration.palettematerialapi.json.uniqueid.matDocument");
    
	/**
	 * LCSMATERIAL_BRANCHIDITERATIONINFO.
	 */
	public static final String LCSMATERIAL_BRANCHIDITERATIONINFO = "LCSMATERIAL.BRANCHIDITERATIONINFO";
	
	// CR R26: Handle Remove Object Customisation : End

	// BURBERRY-1485 RD 74: Material Supplier Documents - Start

	/**
	 * BO_TRACK_MATERIAL_SUPPLIER_DOCUMENT_FLEX_TYPE.
	 */
	public static final String BO_TRACK_MATERIAL_SUPPLIER_DOCUMENT_FLEX_TYPE = LCSProperties
			.get("com.burberry.wc.document.burberrymaterialsupplierdocument.businessobject.flextypeid");

	/**
	 * BO_TRACK_MATERIAL_SUPPLIER_DOCUMENT_MOA_ATTRIBUTE.
	 */
	public static final String BO_TRACK_MATERIAL_SUPPLIER_DOCUMENT_MOA_ATTRIBUTE = LCSProperties
			.get("com.burberry.wc.document.burberrymaterialsupplierdocument.businessobject.moaAttribute");

	/**
	 * BO_TRACK_MATERIAL_SUPPLIER_DOCUMENT_NAME.
	 */
	public static final String BO_TRACK_MATERIAL_SUPPLIER_DOCUMENT_NAME = LCSProperties
			.get("com.burberry.wc.document.burberrymaterialsupplierdocument.businessobject.name");

	/**
	 * MOA_TRACK_MATERIAL_SUPPLIER_DOCUMENT_FLEX_TYPE.
	 */
	public static final String MOA_TRACK_MATERIAL_SUPPLIER_DOCUMENT_FLEX_TYPE = LCSProperties
			.get("com.burberry.wc.document.burberrymaterialsupplierdocument.multiobject.flextypeid");

	/**
	 * MOA_ATTR_MATERIAL_SUPPLIER_ID.
	 */
	public static final String MOA_ATTR_MATERIAL_SUPPLIER_ID = LCSProperties
			.get("com.burberry.wc.document.burberrymaterialsupplierdocument.multiobject.materialsupplierid");

	/**
	 * JSON_PAL_SUPPLIER_ATT.
	 */
	public static final String JSON_PAL_SUPPLIER_ATT = LCSProperties
			.get(MATERIAL_JSON_KEY + ".paletteSupplier");

	/**
	 * JSON_PAL_SUPPLIER_SYSTEM_ATT.
	 */
	public static final String JSON_PAL_SUPPLIER_SYSTEM_ATT = LCSProperties
			.get(MATERIAL_JSON_SYSTEM_KEY + ".paletteSupplier");

	/**
	 * PAL_SUPPLIER_IGNORE.
	 */
	public static final String PAL_SUPPLIER_IGNORE = LCSProperties
			.get("com.burberry.integration.palettematerialapi.ignoreattributes.paletteSupplier");
	
	/**
	 * PAL_SUPPLIER_ATT.
	 */
	public static final String PAL_SUPPLIER_ATT = LCSProperties
			.get("com.burberry.integration.palettematerialapi.jsonattributes.paletteSupplier");

	/**
	 * MOA_TRACK_SUPPLIER_ID.
	 */
	public static final String MOA_TRACK_SUPPLIER_NAME = LCSProperties
			.get("com.burberry.wc.palette.burberrypalettematerialcolor.multiobject.supplierName");
	
	// BURBERRY-1485 RD 74: Material Supplier Documents - End


}
