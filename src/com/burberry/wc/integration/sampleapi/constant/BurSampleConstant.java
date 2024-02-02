package com.burberry.wc.integration.sampleapi.constant;

import com.lcs.wc.util.LCSProperties;

/**
 * Class hold reference of common constant used. All the constant will be used
 * in different classes. This class will be the central location to declare any
 * string/ char value as constant for Sample Request API. Most of constant value
 * will be read from property file to populate. ID used as constant should have
 * universal reference id from FLEX system. Most of the constant will start with
 * BUR_ which are specific to Flex field. These fields must have value which are
 * universal ID for the fields. So in future if DB changes it will impact the
 * running Java code.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurSampleConstant {

	/**
	 * BurSampleRequestConstant.
	 */
	private BurSampleConstant() {

	}

	/**
	 * STR_SAMPLE_API_ERROR_MSG.
	 */
	public static final String STR_SAMPLE_API_ERROR_MSG = LCSProperties
			.get("com.burberry.integration.sampleapi.errormessage");

	/**
	 * STR_SAMPLE_API_VALID_OBJECTS.
	 */
	public static final String STR_SAMPLE_API_VALID_OBJECTS = LCSProperties
			.get("com.burberry.integration.sampleapi.validObjects");

	/**
	 * SAMPLE_API_LOG_ENTRY_FLEXTYPE.
	 */
	public static final String SAMPLE_API_LOG_ENTRY_FLEXTYPE = LCSProperties
			.get("com.burberry.integration.sampleapi.logentry");

	/**
	 * LCSSAMPLEREQUEST.
	 */
	public static final String LCSSAMPLEREQUEST = "LCSSAMPLEREQUEST";

	/**
	 * LCSSAMPLEREQUEST_IDA2A2.
	 */
	public static final String LCSSAMPLEREQUEST_IDA2A2 = "LCSSAMPLEREQUEST.IDA2A2";

	/**
	 * LCSSAMPLEREQUEST_ROOT_ID.
	 */
	public static final String LCSSAMPLEREQUEST_ROOT_ID = "OR:com.lcs.wc.sample.LCSSampleRequest:";

	/**
	 * LCSSAMPLEREQUEST_REFERENCE.
	 */
	public static final String LCSSAMPLEREQUEST_REFERENCE = "sampleRequestReference";

	/**
	 * SAMPLE_REQUESTREFERENCE_KEY_ID.
	 */
	public static final String SAMPLE_REQUESTREFERENCE_KEY_ID = "sampleRequestReference.key.id";

	/**
	 * OWNER_MASTERREFERENCE_KEY_ID.
	 */
	public static final String OWNER_MASTERREFERENCE_KEY_ID = "ownerMasterReference.key.id";

	/**
	 * SOURCING_MASTERREFERENCE_KEY_ID.
	 */
	public static final String SOURCING_MASTERREFERENCE_KEY_ID = "sourcingMasterReference.key.id";

	/**
	 * COLOR_REFERENCE_KEY_ID.
	 */
	public static final String COLOR_REFERENCE_KEY_ID = "colorReference.key.id";

	/**
	 * PRODUCT_SAMPLE_FLEXTYPE.
	 */
	public static final String PRODUCT_SAMPLE_FLEXTYPE = LCSProperties
			.get("com.burberry.integration.alias.sample.product");

	/**
	 * MATERIAL_SAMPLE_FLEXTYPE.
	 */
	public static final String MATERIAL_SAMPLE_FLEXTYPE = LCSProperties
			.get("com.burberry.integration.alias.sample.material");

	/**
	 * JSON_KEY.
	 */
	public static final String JSON_KEY = "com.burberry.integration.sampleapi.jsonattributes.mapping";

	/**
	 * JSON_SYSTEM_KEY.
	 */
	public static final String JSON_SYSTEM_KEY = "com.burberry.integration.sampleapi.system.jsonattributes.mapping";

	/**
	 * PRODUCT_REQ.
	 */
	public static final String PRODUCT_REQ = LCSProperties
			.get("com.burberry.integration.sampleapi.requiredattributes.product");

	/**
	 * PRODUCT_IGNORE.
	 */
	public static final String PRODUCT_IGNORE = LCSProperties
			.get("com.burberry.integration.sampleapi.ignoreattributes.product");

	/**
	 * PRODUCT_ATT.
	 */
	public static final String PRODUCT_ATT = LCSProperties
			.get("com.burberry.integration.sampleapi.jsonattributes.product");

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
	 * PRODUCT_SEASON_REQ.
	 */
	public static final String PRODUCT_SEASON_REQ = LCSProperties
			.get("com.burberry.integration.sampleapi.requiredattributes.productseason");

	/**
	 * PRODUCT_SEASON_IGNORE.
	 */
	public static final String PRODUCT_SEASON_IGNORE = LCSProperties
			.get("com.burberry.integration.sampleapi.ignoreattributes.productseason");

	/**
	 * PRODUCT_SEASON_ATT.
	 */
	public static final String PRODUCT_SEASON_ATT = LCSProperties
			.get("com.burberry.integration.sampleapi.jsonattributes.productseason");

	/**
	 * JSON_PRODUCT_SEASON_KEY.
	 */
	public static final String JSON_PRODUCT_SEASON_KEY = LCSProperties
			.get(JSON_KEY + ".productseason");

	/**
	 * JSON_SYSTEM_PRODUCT_SEASON_KEY.
	 */
	public static final String JSON_SYSTEM_PRODUCT_SEASON_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".productseason");

	/**
	 * SOURCE_SUPPLIER_REQ.
	 */
	public static final String SOURCE_SUPPLIER_REQ = LCSProperties
			.get("com.burberry.integration.sampleapi.requiredattributes.sourcesupplier");

	/**
	 * SOURCE_SUPPLIER_IGNORE.
	 */
	public static final String SOURCE_SUPPLIER_IGNORE = LCSProperties
			.get("com.burberry.integration.sampleapi.ignoreattributes.sourcesupplier");

	/**
	 * SOURCE_SUPPLIER_ATT.
	 */
	public static final String SOURCE_SUPPLIER_ATT = LCSProperties
			.get("com.burberry.integration.sampleapi.jsonattributes.sourcesupplier");

	/**
	 * JSON_SOURCE_SUPPLIER.
	 */
	public static final String JSON_SOURCE_SUPPLIER = LCSProperties
			.get(JSON_KEY + ".sourcesupplier");

	/**
	 * JSON_SYSTEM_SOURCE_SUPPLIER_KEY.
	 */
	public static final String JSON_SYSTEM_SOURCE_SUPPLIER_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".sourcesupplier");

	/**
	 * PRODUCT_SAMPLE_REQUEST_REQ.
	 */
	public static final String PRODUCT_SAMPLE_REQUEST_REQ = LCSProperties
			.get("com.burberry.integration.sampleapi.requiredattributes.productsamplerequest");

	/**
	 * PRODUCT_SAMPLE_REQUEST_IGNORE.
	 */
	public static final String PRODUCT_SAMPLE_REQUEST_IGNORE = LCSProperties
			.get("com.burberry.integration.sampleapi.ignoreattributes.productsamplerequest");

	/**
	 * PRODUCT_SAMPLE_REQUEST_ATT.
	 */
	public static final String PRODUCT_SAMPLE_REQUEST_ATT = LCSProperties
			.get("com.burberry.integration.sampleapi.jsonattributes.productsamplerequest");

	/**
	 * JSON_PRODUCT_SAMPLE_REQUEST.
	 */
	public static final String JSON_PRODUCT_SAMPLE_REQUEST = LCSProperties
			.get(JSON_KEY + ".productsamplerequest");

	/**
	 * JSON_SYSTEM_PRODUCT_SAMPLE_REQUEST_KEY.
	 */
	public static final String JSON_SYSTEM_PRODUCT_SAMPLE_REQUEST_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".productsamplerequest");

	/**
	 * MATERIAL_SAMPLE_REQUEST_REQ.
	 */
	public static final String MATERIAL_SAMPLE_REQUEST_REQ = LCSProperties
			.get("com.burberry.integration.sampleapi.requiredattributes.materialsamplerequest");

	/**
	 * MATERIAL_SAMPLE_REQUEST_IGNORE.
	 */
	public static final String MATERIAL_SAMPLE_REQUEST_IGNORE = LCSProperties
			.get("com.burberry.integration.sampleapi.ignoreattributes.materialsamplerequest");

	/**
	 * MATERIAL_SAMPLE_REQUEST_ATT.
	 */
	public static final String MATERIAL_SAMPLE_REQUEST_ATT = LCSProperties
			.get("com.burberry.integration.sampleapi.jsonattributes.materialsamplerequest");

	/**
	 * JSON_MATERIAL_SAMPLE_REQUEST.
	 */
	public static final String JSON_MATERIAL_SAMPLE_REQUEST = LCSProperties
			.get(JSON_KEY + ".materialsamplerequest");

	/**
	 * JSON_SYSTEM_MATERIAL_SAMPLE_REQUEST_KEY.
	 */
	public static final String JSON_SYSTEM_MATERIAL_SAMPLE_REQUEST_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".materialsamplerequest");

	/**
	 * PRODUCT_SAMPLE_REQ.
	 */
	public static final String PRODUCT_SAMPLE_REQ = LCSProperties
			.get("com.burberry.integration.sampleapi.requiredattributes.productsample");

	/**
	 * PRODUCT_SAMPLE_IGNORE.
	 */
	public static final String PRODUCT_SAMPLE_IGNORE = LCSProperties
			.get("com.burberry.integration.sampleapi.ignoreattributes.productsample");

	/**
	 * PRODUCT_SAMPLE_ATT.
	 */
	public static final String PRODUCT_SAMPLE_ATT = LCSProperties
			.get("com.burberry.integration.sampleapi.jsonattributes.productsample");

	/**
	 * JSON_PRODUCT_SAMPLE.
	 */
	public static final String JSON_PRODUCT_SAMPLE = LCSProperties.get(JSON_KEY
			+ ".productsample");

	/**
	 * JSON_SYSTEM_PRODUCT_SAMPLE_KEY.
	 */
	public static final String JSON_SYSTEM_PRODUCT_SAMPLE_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".productsample");

	/**
	 * MATERIAL_SAMPLE_REQ.
	 */
	public static final String MATERIAL_SAMPLE_REQ = LCSProperties
			.get("com.burberry.integration.sampleapi.requiredattributes.materialsample");

	/**
	 * MATERIAL_SAMPLE_IGNORE.
	 */
	public static final String MATERIAL_SAMPLE_IGNORE = LCSProperties
			.get("com.burberry.integration.sampleapi.ignoreattributes.materialsample");

	/**
	 * MATERIAL_SAMPLE_ATT.
	 */
	public static final String MATERIAL_SAMPLE_ATT = LCSProperties
			.get("com.burberry.integration.sampleapi.jsonattributes.materialsample");

	/**
	 * JSON_MATERIAL_SAMPLE.
	 */
	public static final String JSON_MATERIAL_SAMPLE = LCSProperties
			.get(JSON_KEY + ".materialsample");

	/**
	 * JSON_SYSTEM_MATERIAL_SAMPLE_KEY.
	 */
	public static final String JSON_SYSTEM_MATERIAL_SAMPLE_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".materialsample");

	/**
	 * MATERIAL_REQ.
	 */
	public static final String MATERIAL_REQ = LCSProperties
			.get("com.burberry.integration.sampleapi.requiredattributes.material");

	/**
	 * MATERIAL_IGNORE.
	 */
	public static final String MATERIAL_IGNORE = LCSProperties
			.get("com.burberry.integration.sampleapi.ignoreattributes.material");

	/**
	 * MATERIAL_ATT.
	 */
	public static final String MATERIAL_ATT = LCSProperties
			.get("com.burberry.integration.sampleapi.jsonattributes.material");

	/**
	 * JSON_MATERIAL_ATT.
	 */
	public static final String JSON_MATERIAL_ATT = LCSProperties.get(JSON_KEY
			+ ".material");

	/**
	 * JSON_SYSTEM_MATERIAL_KEY.
	 */
	public static final String JSON_SYSTEM_MATERIAL_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".material");

	/**
	 * MATERIAL_COLOUR_REQ.
	 */
	public static final String MATERIAL_COLOUR_REQ = LCSProperties
			.get("com.burberry.integration.sampleapi.requiredattributes.materialcolour");

	/**
	 * MATERIAL_COLOUR_IGNORE.
	 */
	public static final String MATERIAL_COLOUR_IGNORE = LCSProperties
			.get("com.burberry.integration.sampleapi.ignoreattributes.materialcolour");

	/**
	 * MATERIAL_COLOUR_ATT.
	 */
	public static final String MATERIAL_COLOUR_ATT = LCSProperties
			.get("com.burberry.integration.sampleapi.jsonattributes.materialcolour");

	/**
	 * JSON_MATERIAL_COLOUR_ATT.
	 */
	public static final String JSON_MATERIAL_COLOUR_ATT = LCSProperties
			.get(JSON_KEY + ".materialcolour");

	/**
	 * JSON_SYSTEM_MATERIAL_COLOUR_KEY.
	 */
	public static final String JSON_SYSTEM_MATERIAL_COLOUR_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".materialcolour");

	/**
	 * MATERIAL_SUPPLIER_REQ.
	 */
	public static final String MATERIAL_SUPPLIER_REQ = LCSProperties
			.get("com.burberry.integration.sampleapi.requiredattributes.materialsupplier");

	/**
	 * MATERIAL_SUPPLIER_IGNORE.
	 */
	public static final String MATERIAL_SUPPLIER_IGNORE = LCSProperties
			.get("com.burberry.integration.sampleapi.ignoreattributes.materialsupplier");

	/**
	 * MATERIAL_SUPPLIER_ATT.
	 */
	public static final String MATERIAL_SUPPLIER_ATT = LCSProperties
			.get("com.burberry.integration.sampleapi.jsonattributes.materialsupplier");

	/**
	 * JSON_MATERIAL_SUPPLIER_KEY.
	 */
	public static final String JSON_MATERIAL_SUPPLIER_ATT = LCSProperties
			.get(JSON_KEY + ".materialsupplier");

	/**
	 * JSON_SYSTEM_MATERIAL_SUPPLIER_KEY.
	 */
	public static final String JSON_SYSTEM_MATERIAL_SUPPLIER_KEY = LCSProperties
			.get(JSON_SYSTEM_KEY + ".materialsupplier");

	/**
	 * MATERIAL_SUPPLIER_NAME.
	 */
	public static final String MATERIAL_SUPPLIER_NAME = "materialSupplierName";

	/**
	 * MATERIAL_COLOUR_NAME.
	 */
	public static final String MATERIAL_COLOUR_NAME = "materialColourName";

	/**
	 * LCSSAMPLE_PREFIX.
	 */
	public static final String LCSSAMPLE_PREFIX = "OR:com.lcs.wc.sample.LCSSample:";

	/**
	 * LCSSAMPLE_ID.
	 */
	public static final String LCSSAMPLE_ID = "LCSSAMPLE.IDA2A2";

	/**
	 * SAMPLE_NAME.
	 */
	public static final String SAMPLE_NAME = "sampleName";

	/**
	 * SAMPLE_REQUEST_REF.
	 */
	public static final String SAMPLE_REQUEST_REF = "sampleRequestReference";

	/**
	 * SAMPLE_STATE.
	 */
	public static final String SAMPLE_STATE = "state";

	/**
	 * SAMPLE_COLOURWAY_REF.
	 */
	public static final String SAMPLE_COLOURWAY_REF = "vrdColorwayRef";

	/**
	 * DOCUMENT_IGNORE.
	 */
	public static final String DOCUMENT_IGNORE = LCSProperties
			.get("com.burberry.integration.sampleapi.ignoreattributes.document");

	/**
	 * DOCUMENT_ATT.
	 */
	public static final String DOCUMENT_ATT = LCSProperties
			.get("com.burberry.integration.sampleapi.jsonattributes.document");

	/**
	 * DOCUMENT_REQ.
	 */
	public static final String DOCUMENT_REQ = LCSProperties
			.get("com.burberry.integration.sampleapi.requiredattributes.document");

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
	 * SAMPLE_COLOURWAY_REF_ATT.
	 */
	public static final String SAMPLE_COLOURWAY_REF_ATT = "vrdColorwayRef";

	/**
	 * LCSSAMPLE.
	 */
	public static final String LCSSAMPLE = "LCSSAMPLE";

	/**
	 * PRODUCT_SEASON_NAME.
	 */
	public static final String PRODUCT_SEASON_NAME = "seasonName";

	/**
	 * PLACEHOLDER.
	 */
	public static final String PLACEHOLDER = "placeholder";

	/**
	 * COLOUR_REFERENCE_KEY_ID.
	 */
	public static final String COLOUR_REFERENCE_KEY_ID = "colorReference.key.id";

	/**
	 * SEASONREVID.
	 */
	public static final String SEASONREVID = "SEASONREVID";

	/**
	 * STR_ERROR_MSG_MULTIPLE_DELTA_PARAMETERS.
	 */
	public static final String STR_ERROR_MSG_MULTIPLE_DELTA_PARAMETERS = LCSProperties
			.get("com.burberry.integration.sampleapi.errormessage.multipledeltaparameters");

	/**
	 * PRIMARY_SOURCE
	 */
	public static final Object PRIMARY_SOURCE = "primarySource";
	
	/**
	 * SOURCE_IGNORE.
	 */
	public static final String SOURCE_IGNORE = LCSProperties
			.get("com.burberry.integration.sampleapi.ignoreattributes.source");

	/**
	 * SOURCE_ATT.
	 */
	public static final String SOURCE_ATT = LCSProperties
			.get("com.burberry.integration.sampleapi.jsonattributes.source");

	/**
	 * SOURCE_REQ.
	 */
	public static final String SOURCE_REQ = LCSProperties
			.get("com.burberry.integration.sampleapi.requiredattributes.source");
	
	/**
	 * JSON_SOURCE_ATT.
	 */
	public static final String JSON_SOURCE_ATT = LCSProperties
			.get(JSON_KEY + ".source");
	
	
	/**
	 * JSON_DOCUMENT_SYSTEM_ATT.
	 */
	public static final String JSON_SOURCE_SYSTEM_ATT = LCSProperties
			.get(JSON_SYSTEM_KEY + ".source");


}
