package com.burberry.wc.integration.productapi.constant;

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

public final class BurProductConstant {

	/**
	 * constructor.
	 */
	private BurProductConstant() {

	}

	// CR R26: Handle Remove Image Page from Specification Customisation : Start

	/**
	 * BO_TRACK_SPECIFICATION_IMAGE_PAGE_FLEX_TYPE.
	 */
	public static final String BO_TRACK_SPECIFICATION_IMAGE_PAGE_FLEXTYPE = LCSProperties
			.get("com.burberry.wc.component.burberryimagepage.businessobject.flextypeid");

	/**
	 * BO_TRACK_SPECIFICATION_IMAGE_PAGE_MOA_ATTRIBUTE.
	 */
	public static final String BO_TRACK_SPECIFICATION_IMAGE_PAGE_MOA_ATTRIBUTE = LCSProperties
			.get("com.burberry.wc.component.burberryimagepage.businessbject.moaAttribute");

	/**
	 * BO_TRACK_SPECIFICATION_IMAGE_PAGE_NAME.
	 */
	public static final String BO_TRACK_SPECIFICATION_IMAGE_PAGE_NAME = LCSProperties
			.get("com.burberry.wc.component.burberryimagepage.businessobject.name");

	/**
	 * BO_TRACK_IMAGE_FROM_IMAGE_PAGE_NAME.
	 */
	public static final String BO_TRACK_IMAGE_FROM_IMAGE_PAGE_NAME = LCSProperties
			.get("com.burberry.wc.document.imageunderimagepage.businessobject.name");

	/**
	 * MOA_TRACK_SPECIFICATION_IMAGE_PAGE_FLEXTYPE.
	 */
	public static final String MOA_TRACK_SPECIFICATION_IMAGE_PAGE_FLEXTYPE = LCSProperties
			.get("com.burberry.wc.component.burberryimagepage.multiobject.flextypeid");

	/**
	 * MOA_TRACK_IMAGE_FROM_IMAGE_PAGE_FLEXTYPE.
	 */
	public static final String MOA_TRACK_IMAGE_FROM_IMAGE_PAGE_FLEXTYPE = LCSProperties
			.get("com.burberry.wc.document.imageunderimagepage.multiobject.flextypeid");

	/**
	 * MOA_TRACK_IMAGE_PAGE_NAME.
	 */
	public static final String MOA_TRACK_IMAGE_PAGE_NAME = LCSProperties
			.get("com.burberry.wc.component.burberryimagepage.multiobject.imagepagename");

	/**
	 * MOA_TRACK_IMAGE_PAGE_ID.
	 */
	public static final String MOA_TRACK_IMAGE_PAGE_ID = LCSProperties
			.get("com.burberry.wc.document.imageunderimagepage.multiobject.imagepageid");

	/**
	 * MOA_TRACK_IMAGE_PAGE_ID.
	 */
	public static final String MOA_TRACK_IMAGE_FILE_UNIQUE_ID = LCSProperties
			.get("com.burberry.wc.document.imageunderimagepage.multiobject.filedocimgid");

	/**
	 * MOA_TRACK_SOURCING_ID.
	 */
	public static final String MOA_TRACK_SOURCING_ID = LCSProperties
			.get("com.burberry.wc.costsheet.burberrycostsheet.multiobject.sourcingid");

	/**
	 * MOA_TRACK_PRODUCT_ID.
	 */
	public static final String MOA_TRACK_PRODUCT_ID = LCSProperties
			.get("com.burberry.wc.costsheet.burberrycostsheet.multiobject.productid");

	/**
	 * MOA_TRACK_SPECIFICATION_ID.
	 */
	public static final String MOA_TRACK_SPECIFICATION_ID = LCSProperties
			.get("com.burberry.wc.specification.burrberryspecification.multiobject.specificationid");

	/**
	 * FLEXSPECIFICATION.
	 */
	public static final String FLEXSPECIFICATION = "FLEXSPECIFICATION";

	/**
	 * OWNERREFERENCE_KEY_ID.
	 */
	public static final String OWNERREFERENCE_KEY_ID = "ownerReference.key.id";

	// CR R26: Handle Remove Image Page from Specification Customisation : End

	// CR R26: Handle Delete Specification Customisation : Start
	/**
	 * MOA_TRACK_SPECIFICATION_FLEX_TYPE.
	 */
	public static final String MOA_TRACK_SPECIFICATION_FLEX_TYPE = LCSProperties
			.get("com.burberry.wc.specification.burrberryspecification.multiobject.flextypeid");

	/**
	 * BO_TRACK_SPECIFICATION_NAME.
	 */
	public static final String BO_TRACK_SPECIFICATION_NAME = LCSProperties
			.get("com.burberry.wc.specification.burrberryspecification.businessobject.name");

	/**
	 * MOA_TRACK_SEASON_ID.
	 */
	public static final String MOA_TRACK_SEASON_ID = LCSProperties
			.get("com.burberry.wc.specification.burrberryspecification.multiobject.seasonid");

	/**
	 * MOA_TRACK_SPECIFICATION_SPEC_NAME.
	 */

	public static final String MOA_TRACK_SPECIFICATION_SPEC_NAME = LCSProperties
			.get("com.burberry.wc.specification.burrberryspecification.multiobject.specificationname");

	/**
	 * LCSPRODUCT_BRANCHIDITERATIONINFO.
	 */
	public static final String LCSPRODUCT_BRANCHIDITERATIONINFO = "LCSPRODUCT.BRANCHIDITERATIONINFO";

	// CR R26: Handle Delete Specification Customisation : END

	// JIRA - BURBERRY-1363: START
	/**
	 * BURBERRY_INTEGRATION_DB_MAX_LIMIT.
	 */
	public static final String BURBERRY_INTEGRATION_DB_MAX_LIMIT = LCSProperties
			.get("com.burberry.integration.db.query.placeholder.maximum.limit");
	// JIRA - BURBERRY-1363: END

	/**
	 * LCSPRODUCT_ROOT_ID.
	 */
	public static final String LCSPRODUCT_ROOT_ID = "VR:com.lcs.wc.product.LCSProduct:";

	// BURBERRY-1485: Append Price Library Retail : Start
	/**
	 * INITIAL_PRICE_BAND.
	 */
	public static final String INITIAL_PRICE_BAND = LCSProperties
			.get("com.burberry.integration.productseason.initialpriceband");

	/**
	 * JSON_PRICELIBRARY_ATT.
	 */
	public static final String JSON_PRICELIBRARY_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.mapping.pricelibrary");

	/**
	 * JSON_PRICELIBRARY_SYSTEM_ATT.
	 */
	public static final String JSON_PRICELIBRARY_SYSTEM_ATT = LCSProperties
			.get("com.burberry.integration.productapi.system.jsonattributes.mapping.pricelibrary");

	/**
	 * PRICELIBRARY_IGNORE.
	 */
	public static final String PRICELIBRARY_IGNORE = LCSProperties
			.get("com.burberry.integration.productapi.ignoreattributes.pricelibrary");

	/**
	 * PRICELIBRARY_ATT.
	 */
	public static final String PRICELIBRARY_ATT = LCSProperties
			.get("com.burberry.integration.productapi.jsonattributes.pricelibrary");

	/**
	 * PRICELIBRARY_REQ.
	 */
	public static final String PRICELIBRARY_REQ = LCSProperties
			.get("com.burberry.integration.productapi.requiredattributes.pricelibrary");

	// BURBERRY-1485: Append Price Library Retail : End

	// BURBERRY-1543 : Start

	/**
	 * TYPE
	 */
	public static final Object TYPE = "type";

	// BURBERRY-1543 : End

	/**
	 * BO_TRACK_SPECIFICATION_DOCUMENT_NAME.
	 */
	public static final String BO_TRACK_SPECIFICATION_DOCUMENT_NAME = LCSProperties
			.get("com.burberry.wc.document.burberryspecificationdocument.businessobject.name");

	/**
	 * MOA_TRACK_SPECIFICATION_DOC_FLEX_TYPE.
	 */
	public static final String MOA_TRACK_SPECIFICATION_DOC_FLEX_TYPE = LCSProperties
			.get("com.burberry.wc.document.burberryspecificationdocument.multiobject.flextypeid");

}
