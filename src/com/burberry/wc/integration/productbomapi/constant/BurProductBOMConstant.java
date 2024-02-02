package com.burberry.wc.integration.productbomapi.constant;

import com.lcs.wc.util.LCSProperties;

/**
 * Class hold reference of common constant used. All the constant will be used
 * in different classes. This class will be the central location to declare any
 * string/ char value as constant for BOM API. Most of constant value will be
 * read from property file to populate. ID used as constant should have
 * universal reference id from FLEX system. Most of the constant will start with
 * BUR_ which are specific to Flex field. These fields must have value which are
 * universal ID for the fields. So in future if DB changes it will impact the
 * running Java code.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurProductBOMConstant {

	/**
	 * BurProductBOMConstant.
	 */
	private BurProductBOMConstant() {

	}

	/**
	 * STR_PRODUCTAPI_VALID_OBJECTS.
	 */
	public static final String STR_PRODUCT_BOM_API_VALID_OBJECTS = LCSProperties
			.get("com.burberry.integration.productbomapi.validObjects");
	/**
	 * STYLE_ATT.
	 */
	public static final String STYLE_ATT = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.product");
	/**
	 * STYLE_IGNORE.
	 */
	public static final String STYLE_IGNORE = LCSProperties
			.get("com.burberry.integration.productbomapi.ignoreattributes.product");
	/**
	 * STYLE_REQ.
	 */
	public static final String STYLE_REQ = LCSProperties
			.get("com.burberry.integration.productbomapi.requiredattributes.product");
	/**
	 * JSON_PRODUCTKEY.
	 */
	public static final String JSON_PRODUCTKEY = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.mapping.product");
	/**
	 * JSON_COLOURWAYKEY.
	 */
	public static final String JSON_COLOURWAYKEY = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.mapping.colourway");
	/**
	 * SKU_ATT.
	 */
	public static final String SKU_ATT = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.colourway");
	/**
	 * SKU_REQ.
	 */
	public static final String SKU_REQ = LCSProperties
			.get("com.burberry.integration.productbomapi.requiredattributes.colourway");
	/**
	 * SKU_IGNORE.
	 */
	public static final String SKU_IGNORE = LCSProperties
			.get("com.burberry.integration.productbomapi.ignoreattributes.colourway");
	/**
	 * JSON_SEASONKEY.
	 */
	public static final String JSON_SEASONKEY = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.mapping.season");
	/**
	 * SEASON_ATT.
	 */
	public static final String SEASON_ATT = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.season");
	/**
	 * SEASON_REQ.
	 */
	public static final String SEASON_REQ = LCSProperties
			.get("com.burberry.integration.productbomapi.requiredattributes.season");
	/**
	 * SEASON_IGNORE.
	 */
	public static final String SEASON_IGNORE = LCSProperties
			.get("com.burberry.integration.productbomapi.ignoreattributes.season");
	/**
	 * JSON_PRODCUTSEASONKEY.
	 */
	public static final String JSON_PRODCUTSEASONKEY = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.mapping.productseason");
	/**
	 * PRODUCT_SEASON_IGNORE.
	 */
	public static final String PRODUCT_SEASON_IGNORE = LCSProperties
			.get("com.burberry.integration.productbomapi.ignoreattributes.productseason");
	/**
	 * PRODUCT_SEASON_ATT.
	 */
	public static final String PRODUCT_SEASON_ATT = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.productseason");
	/**
	 * PRODUCT_SEASON_REQ.
	 */
	public static final String PRODUCT_SEASON_REQ = LCSProperties
			.get("com.burberry.integration.productbomapi.requiredattributes.productseason");
	/**
	 * JSON_COLOURWAYSEASONKEY.
	 */
	public static final String JSON_COLOURWAYSEASONKEY = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.mapping.colourwayseason");
	/**
	 * SKU_SEASON_IGNORE.
	 */
	public static final String SKU_SEASON_IGNORE = LCSProperties
			.get("com.burberry.integration.productbomapi.ignoreattributes.colourwayseason");
	/**
	 * SKU_SEASON_ATT.
	 */
	public static final String SKU_SEASON_ATT = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.colourwayseason");
	/**
	 * SKU_SEASON_REQ.
	 */
	public static final String SKU_SEASON_REQ = LCSProperties
			.get("com.burberry.integration.productbomapi.requiredattributes.colourwayseason");
	/**
	 * JSON_SOURCE.
	 */
	public static final String JSON_SOURCE = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.mapping.source");
	/**
	 * SOURCE_IGNORE.
	 */
	public static final String SOURCE_IGNORE = LCSProperties
			.get("com.burberry.integration.productbomapi.ignoreattributes.source");
	/**
	 * SOURCE_ATT.
	 */
	public static final String SOURCE_ATT = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.source");
	/**
	 * SOURCE_REQ.
	 */
	public static final String SOURCE_REQ = LCSProperties
			.get("com.burberry.integration.productbomapi.requiredattributes.source");
	/**
	 * JSON_SPEC.
	 */
	public static final String JSON_SPEC = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.mapping.specification");
	/**
	 * SPEC_IGNORE.
	 */
	public static final String SPEC_IGNORE = LCSProperties
			.get("com.burberry.integration.productbomapi.ignoreattributes.specification");
	/**
	 * SPEC_ATT.
	 */
	public static final String SPEC_ATT = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.specification");
	/**
	 * SPEC_REQ.
	 */
	public static final String SPEC_REQ = LCSProperties
			.get("com.burberry.integration.productbomapi.requiredattributes.specification");
	/**
	 * JSON_BOM.
	 */
	public static final String JSON_BOM = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.mapping.bom");
	/**
	 * BOM_IGNORE.
	 */
	public static final String BOM_IGNORE = LCSProperties
			.get("com.burberry.integration.productbomapi.ignoreattributes.bom");
	/**
	 * BOM_ATT.
	 */
	public static final String BOM_ATT = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.bom");
	/**
	 * BOM_REQ.
	 */
	public static final String BOM_REQ = LCSProperties
			.get("com.burberry.integration.productbomapi.requiredattributes.bom");
	/**
	 * JSON_BOMLINK.
	 */
	public static final String JSON_BOMLINK = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.mapping.bomlink");
	/**
	 * BOMLINK_IGNORE.
	 */
	public static final String BOMLINK_IGNORE = LCSProperties
			.get("com.burberry.integration.productbomapi.ignoreattributes.bomlink");
	/**
	 * BOMLINK_ATT.
	 */
	public static final String BOMLINK_ATT = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.bomlink");
	/**
	 * BOMLINK_REQ.
	 */
	public static final String BOMLINK_REQ = LCSProperties
			.get("com.burberry.integration.productbomapi.requiredattributes.bomlink");

	/**
	 * JSON_MATSUP.
	 */
	public static final String JSON_MATSUP = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.mapping.materialsupplier");
	/**
	 * MATSUP_IGNORE.
	 */
	public static final String MATSUP_IGNORE = LCSProperties
			.get("com.burberry.integration.productbomapi.ignoreattributes.materialsupplier");
	/**
	 * MATSUP_ATT.
	 */
	public static final String MATSUP_ATT = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.materialsupplier");
	/**
	 * MATSUP_REQ.
	 */
	public static final String MATSUP_REQ = LCSProperties
			.get("com.burberry.integration.productbomapi.requiredattributes.materialsupplier");
	/**
	 * JSON_MAT.
	 */
	public static final String JSON_MAT = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.mapping.materialmaster");
	/**
	 * MAT_IGNORE.
	 */
	public static final String MAT_IGNORE = LCSProperties
			.get("com.burberry.integration.productbomapi.ignoreattributes.materialmaster");
	/**
	 * MAT_ATT.
	 */
	public static final String MAT_ATT = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.materialmaster");
	/**
	 * MAT_REQ.
	 */
	public static final String MAT_REQ = LCSProperties
			.get("com.burberry.integration.productbomapi.requiredattributes.materialmaster");

	/**
	 * JSON_MATCOLOR.
	 */
	public static final String JSON_MATCOLOR = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.mapping.materialcolour");
	/**
	 * MATCOLOR_IGNORE.
	 */
	public static final String MATCOLOR_IGNORE = LCSProperties
			.get("com.burberry.integration.productbomapi.ignoreattributes.materialcolour");
	/**
	 * MATCOLOR_ATT.
	 */
	public static final String MATCOLOR_ATT = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.materialcolour");
	/**
	 * MATCOLOR_REQ.
	 */
	public static final String MATCOLOR_REQ = LCSProperties
			.get("com.burberry.integration.productbomapi.requiredattributes.materialcolour");
	/**
	 * STATE.
	 */
	public static final String STATE = "state";
	/**
	 * SPEC_NAME.
	 */
	public static final String SPEC_NAME = "specName";

	/**
	 * BOM.
	 */
	public static final String BOM = "FlexBomPart";

	/**
	 * MATERIAL_BRANCHID.
	 */
	public static final String MATERIAL_BRANCHID = "LCSMATERIAL.BRANCHIDITERATIONINFO";

	/**
	 * SEASON_BRANCHID.
	 */
	public static final String SEASON_BRANCHID = "LCSSEASON.BRANCHIDITERATIONINFO";

	/**
	 * PRODUCT_SEASON_LINK_ID.
	 */
	public static final String PRODUCT_SEASON_LINK_ID = "LCSPRODUCTSEASONLINK.IDA2A2";

	/**
	 * COLOURWAY_BRANCHID.
	 */
	public static final String COLOURWAY_BRANCHID = "LCSSKU.BRANCHIDITERATIONINFO";

	/**
	 * COLOURWAY_SEASON_ID.
	 */
	public static final String COLOURWAY_SEASON_ID = "LCSSKUSEASONLINK.IDA2A2";

	/**
	 * SOURCINGCONFIG_BRANCHID.
	 */
	public static final String SOURCINGCONFIG_BRANCHID = "LCSSOURCINGCONFIG.BRANCHIDITERATIONINFO";

	/**
	 * BOM_ID.
	 */
	public static final String BOM_ID = "FLEXBOMPART.IDA2A2";
	/**
	 * PRODUCT_MASTERREFERENCE_KEY_ID.
	 */
	public static final String PRODUCT_MASTERREFERENCE_KEY_ID = "productMasterReference.key.id";

	/**
	 * BOMLINK_BOM_MASTERREFERENCE_KEY_ID.
	 */
	public static final String BOMLINK_BOM_MASTERREFERENCE_KEY_ID = "parentReference.key.id";

	/**
	 * BOMLINK_MATERIAL_MASTERREFERENCE_KEY_ID.
	 */
	public static final String BOMLINK_MATERIAL_MASTERREFERENCE_KEY_ID = "childReference.key.id";

	/**
	 * BOM_PRODUCTMATERIAL_MASTERREFERENCE_KEY_ID.
	 */
	public static final String BOM_PRODUCTMATERIAL_MASTERREFERENCE_KEY_ID = "ownerMasterReference.key.id";
	/**
	 * THUMBNAIL.
	 */
	public static final String THUMBNAIL = "thumbnail";

	/**
	 * PRIMARYSOURCE.
	 */
	public static final String PRIMARYSOURCE = "primarySource";
	/**
	 * DROPPED.
	 */
	public static final String DROPPED = "dropped";

	/**
	 * OUTDATE.
	 */
	public static final String BOMLINK_OUTDATE = "outDate";
	/**
	 * FLEXBOMID.
	 */
	public static final String FLEXBOMID = "FLEXBOMPART.IDA2A2";
	/**
	 * PRIMARY_STSL.
	 */
	public static final String PRIMARY_STSL = "primarySTSL";

	/**
	 * SOURCINGCONFIGMASTER_KEY_ID.
	 */
	public static final String SOURCINGCONFIGMASTER_KEY_ID = "IDA3A6";

	/**
	 * BOM_NAME.
	 */
	public static final String BOM_NAME = "ptcbomPartName";
	/**
	 * BOMTYPE.
	 */
	public static final String BOMTYPE = "bomtype";

	/**
	 * SECTION.
	 */
	public static final String SECTION = "section";
	/**
	 * DIM_NAME.
	 */
	public static final String DIM_NAME = "dimensionName";

	/**
	 * LOG ENTRY TYPE.
	 */
	public static final String PRODUCTBOMLOGENTRY = LCSProperties
			.get("com.burberry.integration.productbomapi.logentry");
	/**
	 * STR_ERROR_MSG_PRODUCT_BOML_API.
	 */
	public static final String STR_ERROR_MSG_PRODUCT_BOML_API = LCSProperties
			.get("com.burberry.integration.productbomapi.errormessage");
	/**
	 * SYSTEM_JSON_SPEC.
	 */
	public static final String SYSTEM_JSON_SPEC = LCSProperties
			.get("com.burberry.integration.productbomapi.system.jsonattributes.mapping.specification");
	/**
	 * SYSTEM_JSON_BOM.
	 */
	public static final String SYSTEM_JSON_BOM = LCSProperties
			.get("com.burberry.integration.productbomapi.system.jsonattributes.mapping.bom");
	/**
	 * SYSTEM_JSON_BOMLINK.
	 */
	public static final String SYSTEM_JSON_BOMLINK = LCSProperties
			.get("com.burberry.integration.productbomapi.system.jsonattributes.mapping.bomlink");
	/**
	 * SYSTEM_JSON_PRODUCTKEY.
	 */
	public static final String SYSTEM_JSON_PRODUCTKEY = LCSProperties
			.get("com.burberry.integration.productbomapi.system.jsonattributes.mapping.product");
	/**
	 * SYSTEM_JSON_COLOURWAY.
	 */
	public static final String SYSTEM_JSON_COLOURWAY = LCSProperties
			.get("com.burberry.integration.productbomapi.system.jsonattributes.mapping.colourway");
	/**
	 * SYSTEM_JSON_SEASONKEY.
	 */
	public static final String SYSTEM_JSON_SEASONKEY = LCSProperties
			.get("com.burberry.integration.productbomapi.system.jsonattributes.mapping.season");
	/**
	 * SYSTEM_JSON_SOURCE.
	 */
	public static final String SYSTEM_JSON_SOURCE = LCSProperties
			.get("com.burberry.integration.productbomapi.system.jsonattributes.mapping.source");
	/**
	 * SYSTEM_JSON_COLOURWAYSEASONKEY.
	 */
	public static final String SYSTEM_JSON_COLOURWAYSEASONKEY = LCSProperties
			.get("com.burberry.integration.productbomapi.system.jsonattributes.mapping.colourwayseason");
	/**
	 * SYSTEM_JSON_PRODUCTSEASONKEY.
	 */
	public static final String SYSTEM_JSON_PRODUCTSEASONKEY = LCSProperties
			.get("com.burberry.integration.productbomapi.system.jsonattributes.mapping.productseason");
	/**
	 * SYSTEM_JSON_MATCOLOR.
	 */
	public static final String SYSTEM_JSON_MATCOLOR = LCSProperties
			.get("com.burberry.integration.productbomapi.system.jsonattributes.mapping.materialcolour");
	/**
	 * SYSTEM_JSON_MAT.
	 */
	public static final String SYSTEM_JSON_MAT = LCSProperties
			.get("com.burberry.integration.productbomapi.system.jsonattributes.mapping.materialmaster");
	/**
	 * SYSTEM_JSON_MATSUP.
	 */
	public static final String SYSTEM_JSON_MATSUP = LCSProperties
			.get("com.burberry.integration.productbomapi.system.jsonattributes.mapping.materialsupplier");

	/**
	 * PRODUCTAREVID.
	 */
	public static final String PRODUCTAREVID = "productARevId";

	/**
	 * SPEC_COMPONENT_LINK_TYPE.
	 */
	public static final String SPEC_COMPONENT_LINK_TYPE = "componentType";

	/**
	 * COMPONENT_REFERENCE_KEY_ID.
	 */
	public static final String COMPONENT_REFERENCE_KEY_ID = "componentReference.key.id";

	/**
	 * SEASON_MASTER_REFERENCE.
	 */
	public static final String SEASON_MASTER_REFERENCE = "seasonMasterReference.key.id";

	/**
	 * SPECIFICATION_MASTER_REFERENCE.
	 */
	public static final String SPECIFICATION_MASTER_REFERENCE = "specificationMasterReference.key.id";

	/**
	 * PRIMARY SPEC, Specification to Season Link.
	 */
	public static final String PRIMARY_SPEC = "primarySpec";

	/**
	 * SEASON_MASTER_REFERENCE_ID.
	 */
	public static final String SEASON_MASTER_REFERENCE_ID = "ida3b5";

	/**
	 * SPEC_MASTER_REFERENCE_ID.
	 */
	public static final String SPEC_MASTER_REFERENCE_ID = "ida3a5";

	/**
	 * SOURCEMASTER_REFERNECE_ID.
	 */
	public static final String SOURCEMASTER_REFERNECE_ID = "ida3a6";

	/**
	 * SPEC_MASTER_REFERNECE_ID.
	 */
	public static final String SPEC_MASTER_REFERENCE_KEY_ID = "masterReference.key.id";

	/**
	 * SPEC_SOURCEMASTER_REFERNECE_KEY_ID.
	 */
	public static final String SPEC_SOURCE_REFERENCE_KEY_ID = "specSourceReference.key.id";

	/**
	 * SORTING_NUMBER_KEY
	 */
	public static final String SORTING_NUMBER_KEY = "sortingNumber";

	// ////////////////////////////////////////////////////////////////////////
	// /////// ADD for CR R26 - Deletion Customisation.
	// ////////////////////////////////////////////////////////////////////////
	/**
	 * BO_TRACK_BOM_NAME.
	 */
	public static final String BO_TRACK_BOM_NAME = LCSProperties
			.get("com.burberry.wc.flexbom.burrberryflexbom.businessobject.name");

	/**
	 * MOA_TRACK_MATERIAL_ID.
	 */
	public static final String MOA_TRACK_PRODUCT_ID = LCSProperties
			.get("com.burberry.wc.costsheet.burberrycostsheet.multiobject.productid");

	/**
	 * MOA_TRACK_BOM_FLEX_TYPE.
	 */
	public static final String MOA_TRACK_BOM_FLEX_TYPE = LCSProperties
			.get("com.burberry.wc.flexbom.burrberryflexbom.multiobject.flextypeid");
	/**
	 * MOA_TRACK_BOM_NAME.
	 */
	public static final String MOA_TRACK_BOM_NAME = LCSProperties
			.get("com.burberry.wc.flexbom.burrberryflexbom.multiobject.bomname");

	/**
	 * SPEC_OWNER_REF.
	 */
	public static final String SPEC_OWNER_REF = "specOwnerReference.key.id";

	// ////////////////////////////////////////////////////////////////////////
	// /////// ADD for CR # JIRA 1384# - Material Colour Size Variation.
	// ////////////////////////////////////////////////////////////////////////

	/**
	 * JSON_COLOUR_VARIATION.
	 */
	public static final String JSON_COLOUR_VARIATION = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.mapping.colourvariation");

	/**
	 * COLOUR_VARIATION_IGNORE.
	 */
	public static final String COLOUR_VARIATION_IGNORE = LCSProperties
			.get("com.burberry.integration.productbomapi.ignoreattributes.colourvariation");
	/**
	 * COLOUR_VARIATION_ATT.
	 */
	public static final String COLOUR_VARIATION_ATT = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.colourvariation");
	/**
	 * COLOUR_VARIATION_REQ.
	 */
	public static final String COLOUR_VARIATION_REQ = LCSProperties
			.get("com.burberry.integration.productbomapi.requiredattributes.colourvariation");
	/**
	 * SYSTEM_JSON_COLOUR_VARIATION.
	 */
	public static final String SYSTEM_JSON_COLOUR_VARIATION = LCSProperties
			.get("com.burberry.integration.productbomapi.system.jsonattributes.mapping.colourvariation");
	
	// BURBERRY-1389: Fix for Material Supplier/Supplier Name
	/**
	 * JSON_KEY_SUPPLIER_DESCRIPTION.
	 */
	public static final String JSON_KEY_SUPPLIER_DESCRIPTION= "supplierDescription";
	
	// BURBERRY-1389: Fix for Material Supplier/Supplier Name
	/**
	 * STR_PLACEHOLDER.
	 */
	public static final String STR_PLACEHOLDER= "placeholder";
	
	// BURBERRY-1399: RD-39 Need to add BOMLink unique id in BOM API for uniqueness
	/**
	 * STR_BOM_BRANCH_ID.
	 */
	public static final String STR_BOM_BRANCH_ID= "branchId";
	
	/**
	 * STR_SAP_MAT_NUM.
	 */
	public static final String STR_SAP_MAT_NUM = "burSAPMaterialNumber";
	
	// BURBERRY-1420: BOM header Unique ID in CRUD Flag for BOM API Output - Start
	/**
	 * MOA_TRACK_BOM_PART_ID.
	 */
	public static final String MOA_TRACK_BOM_PART_ID = LCSProperties
			.get("com.burberry.wc.flexbom.burrberryflexbom.multiobject.bompartid");
	/**
	 * STR_BOM_NAME.
	 */
	public static final String STR_BOM_NAME= "bomName";
	
	/**
	 * STR_BOM_HEADER_UNIQID.
	 */
	public static final String STR_BOM_HEADER_UNIQID = "bomHeaderUniqId";
	// BURBERRY-1420: BOM header Unique ID in CRUD Flag for BOM API Output - End
	
	// BURBERRY-1436: RD 58 - Get Latest Material & Material Supplier information for BOM Link: Start
	/**
	 * JSON_KEY_MATERIAL_DESCRIPTION.
	 */
	public static final String JSON_KEY_MATERIAL_DESCRIPTION= "materialDescription";
		
	/**
	 * JSON_KEY_PM_DESCRIPTION.
	 */
	public static final String JSON_KEY_PM_DESCRIPTION="pmDescription";
	/**
	 * STR_PRIMARY_MATERIAL.
	 */
	public static final String STR_PRIMARY_MATERIAL= "primaryMaterial";
	
	// RD 58 - Get Latest Material & Material Supplier information for BOM Link: End
	
	// BURBERRY-1485 New Attributes Additions post Sprint 8: Start
	/**
	 * JSON_SUPPLIER_MASTER.
	 */
	public static final String JSON_SUPPLIER_MASTER = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.mapping.suppliermaster");
	/**
	 * SUPPLIER_MASTER_IGNORE.
	 */
	public static final String SUPPLIER_MASTER_IGNORE = LCSProperties
			.get("com.burberry.integration.productbomapi.ignoreattributes.suppliermaster");
	/**
	 * SUPPLIER_MASTER_ATT.
	 */
	public static final String SUPPLIER_MASTER_ATT = LCSProperties
			.get("com.burberry.integration.productbomapi.jsonattributes.suppliermaster");
	/**
	 * SUPPLIER_MASTER_REQ.
	 */
	public static final String SUPPLIER_MASTER_REQ = LCSProperties
			.get("com.burberry.integration.productbomapi.requiredattributes.suppliermaster");
	
	/**
	 * SYSTEM_JSON_SUPPLIER_MASTER.
	 */
	public static final String SYSTEM_JSON_SUPPLIER_MASTER = LCSProperties
			.get("com.burberry.integration.productbomapi.system.jsonattributes.mapping.suppliermaster");
	
	// BURBERRY-1485 New Attributes Additions post Sprint 8: End
	
	//BURBERRY-1495: RD-76 Free-typed colour data in BOM not appearing in Rating report: Start
	/**
	 * JSON_CV_COLOR_DESC.
	 */
	public static final String JSON_CV_COLOR_DESC= "colorDescription";
	//BURBERRY-1495: RD-76 Free-typed colour data in BOM not appearing in Rating report: End
	

}
