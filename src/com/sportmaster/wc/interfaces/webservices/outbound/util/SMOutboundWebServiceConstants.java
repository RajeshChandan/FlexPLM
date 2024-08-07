package com.sportmaster.wc.interfaces.webservices.outbound.util;

import com.lcs.wc.util.LCSProperties;

/**
 * SMOutboundWebServiceConstants.java
 * This class has used to read constants from the property file.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMOutboundWebServiceConstants {

	/**
	 * Outbound Connectivity Error Code.
	 */
	public static final String OUTBOUND_CONNECTIVITY_ERROR_CODE= LCSProperties.get("com.wc.sm.intefaces.errorcode.webservice.outbound.conectivity");
	/**
	 * Outbound Schema Error Code.
	 */
	public static final String OUTBOUND_SCHEMA_ERROR_CODE = LCSProperties.get("com.wc.sm.intefaces.errorcode.webservice.outbound.schema");
	/**
	 * Outbound TimeOut Error Code.
	 */
	public static final String OUTBOUND_RESPONSE_TIMEOUT_ERROR_CODE= LCSProperties.get("com.wc.sm.intefaces.errorcode.webservice.outbound.timeout");


	/**
	 * use to reduce sonar majaor issue.
	 */
	private String sonarResolved;

	/**
	 * @return the sonarResolved
	 */
	public String getSonarResolved() {
		return sonarResolved;
	}
	/**
	 * @param sonarResolved the sonarResolved to set
	 */
	public void setSonarResolved(String sonarResolved) {
		this.sonarResolved = sonarResolved;
	}

	/**
	 * MATERIAL_XML_GENERATION_FLAG.
	 */
	public static final Boolean MATERIAL_XML_GENERATION_FLAG=LCSProperties.getBoolean("com.sportmaster.wc.interfaces.webservices.outbound.material.XML_GENERATION_FLAG");

	/**
	 * Material queue start time.
	 */
	public static final String MATERIAL_SCHEDULE_QUEUE__START_TIME= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.service.starttime");
	/**
	 * Material time  Queue start time am.
	 */
	public static final String MATERIAL_SCHEDULE_QUEUE__START_AM = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.service.AM_PM");
	/**
	 * Material Queue time inetrval.
	 */
	public static final long MATERIAL_SCHEDULE_QUEUE__INTERVAL_IN_MINS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.material.queueinterval",240);

	/**
	 * ERROR_OCCURED_LITERAL.
	 */
	public static final String ERROR_OCCURED_LITERAL = "ERROR OCCURED -";
	/**
	 * Declaration constants for common package.
	 */
	public static final String COMMON_BEAN_PACKAGE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.bean.COMMON_BEAN_PACKAGE");
	/**
	 * Declaration constants Trims class name.
	 */

	public static final String TRIMS_CLASSNAME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_CLASSNAME");
	/**
	 * Declaration constants for Fabric class name.
	 */
	public static final String FABRIC_CLASSNAME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_CLASSNAME");

	//public static final String DECORATION_CLASSNAME="DecorationInformationUpdatesRequest";
	/**
	 * Declaration constants for Material Queue Orders.
	 */
	public static final String MATERIAL_QUEUE_ORDER=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_QUEUE_ORDER");
	/**
	 * Declaration constants for Materilas type.
	 */
	public static final String MATERIAL_TYPES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.integrationtypes");
	/**
	 * Declaration constants forMaterial request type.
	 */
	public static final String MATERIAL_REQUEST_TYPES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_REQUEST_TYPES");
	/**
	 * Declaration constants for material processing queue name.
	 */
	public static final String MATERIAL_PROCESSING_QUEUE_NAME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.processingqueuename");

	/**
	 * Declaration constants for logentry object id.
	 */
	public static final String LOGENTRY_OBJECTID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.LOGENTRY_OBJECTID");
	/**
	 * Declaration constants for Material logentry types.
	 */
	public static final String MATERIAL_LOGENTRY_TYPEES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_TYPES");
	/**
	 * Declaration constants for Material log entry trims type.
	 */
	public static final String MATERIAL_LOGENTRY_TRIMS_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_TRIMS_TYPE");
	/**
	 * Declaration constants for Queue start ERROR CODE.
	 */
	public static final String MATERIAL_LOGENTRY_FABRIC_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_FABRIC_TYPE");

	public static final String MATERIAL_LOGENTRY_DECORATION_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_DECORATION_TYPE");
	/**
	 * Declaration constants for material log entry attributes.
	 */
	public static final String MATERIAL_LOGENTRY_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_ATTRIBUTES");
	/**
	 * Declaration constants for material log entry status attribute.
	 */
	public static final String MATERIAL_LOGENTRY_STATUS_ATTR=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_STATUS_ATTR");
	/**
	 * Declaration constants for material log entry object id attribute.
	 */

	public static final String MATERIAL_LOGENTRY_OBJECTID_ATTR=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_OBJECTID_ATTR");

	/**
	 * Declaration constants for create pending.
	 */
	public static final String CREATE_PENDING=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.CREATE_PENDING");
	/**
	 * Declaration constants for create processed.
	 */
	public static final String CREATE_PROCESSED=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.CREATE_PROCESSED");
	/**
	 * Declaration constants for update pending.
	 */
	public static final String UPDATE_PENDING=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.UPDATE_PENDING");
	/**
	 * Declaration constants for update processed.
	 */
	public static final String UPDATE_PROCESSED=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.UPDATE_PROCESSED");
	/**
	 * Declaration constants for OBJECT_MISSING.
	 */
	public static final String OBJECT_MISSING=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.OBJECT_MISSING","OBJECT_MISSING");

	/**
	 * Declaration constants for logentry required id.
	 */

	public static final String LOGENTRY_TYPE_REQID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.LOGENTRY_TYPE_REQID");
	/**
	 * Declaration constants for logentry request id.
	 */
	public static final String LOGENTRY_REQUEST_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.LOGENTRY_REQUEST_ID");

	/**
	 * Declaration constants for Trims common attributes.
	 */
	public static final String TRIMS_COMMON_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_COMMON_ATTRIBUTES");
	/**
	 * Declaration constants for Trims common mapped attributes.
	 */
	public static final String TRIMS_COMMON_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_COMMON_MAPPED_ELEMENTS");
	/**
	 * Declaration constants for Trims others attributes.
	 */
	public static final String TRIMS_OTHER_FIELDS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_OTHER_FIELDS");
	/**
	 * Declaration constants for Trims zipper mapped attributes.
	 */

	public static final String TRIMS_ZIPPER_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_ZIPPER_MAPPED_ELEMENTS");
	/**
	 * Declaration constants for Trims/zipper attributes.
	 */
	public static final String TRIMS_ZIPPER_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_ZIPPER_ATTRIBUTES");
	/**
	 * Declaration constants for Trims supplier attributes.
	 */
	public static final String TRIMS_MATERIAL_SUPPLIER_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_MATERIAL_SUPPLIER_ATTRIBUTES");
	/**
	 * Declaration constants for Trims material supplier mapped attribute.
	 */
	public static final String TRIMS_MATERIAL_SUPPLER_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_MATERIAL_SUPPLER_MAPPED_ELEMENTS");
	/**
	 * Declaration constants for Trims material supplier others attribute.
	 */
	public static final String TRIMS_MATERIAL_SUPPLER_OTHER_FIELDS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_MATERIAL_SUPPLER_OTHER_FIELDS");
	/**
	 * Declaration constants for Trims material suppliers attributes.
	 */
	public static final String TRIMS_SUPPLIER_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_SUPPLIER_ATTRIBUTES");
	/**
	 * Declaration constants for trims suppliers mapped elements.
	 */
	public static final String TRIMS_SUPPLER_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_SUPPLER_MAPPED_ELEMENTS");
	/**
	 * Declaration constants for Trims pricing attributes.
	 */
	public static final String TRIMS_PRICING_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_PRICING_ATTRIBUTES");
	/**
	 * Declaration constants for Trims pricing mapped attributes.
	 */
	public static final String TRIMS_PRICING_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_PRICING_MAPPED_ELEMENTS");
	/**
	 * Declaration constants for Trims pricing Moa attributes.
	 */
	public static final String TRIMS_MAT_MOA_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_MAT_MOA_TYPE");
	/**
	 * Declaration constants for Trims material supplier beans.
	 */
	public static final String TRIMS_MAT_SUP_BEAN_CLASS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_MAT_SUP_BEAN_CLASS");
	/**
	 * Declaration constants for Trims material supplier eleents.
	 */
	public static final String TRIMS_MAT_SUP_BEAN_ELEMENT=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_MAT_SUP_BEAN_ELEMENT");
	/**
	 * Declaration constants for Trims pricing beans.
	 */
	public static final String TRIMS_PRICING_BEAN_CLASS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_PRICING_BEAN_CLASS");
	/**
	 * Declaration constants for Trims pricing elements.
	 */
	public static final String TRIMS_PRICING_BEAN_ELEMENT=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_PRICING_BEAN_ELEMENT");
	/**
	 * Declaration constants for Trims pricing elements.
	 */
	public static final String TRIMS_REQUIRED_MATERIAL_SUPPLIER_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_REQUIRED_MATERIAL_SUPPLIER_ELEMENTS");
	/**
	 * Declaration constants for Trims pricing elements.
	 */
	public static final String TRIMS_REQUIRED_PRICING_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_REQUIRED_PRICING_ELEMENTS");


	/**
	 * Declaration constants for fabric common attributes.
	 */
	public static final String FABRIC_COMMON_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_COMMON_ATTRIBUTES");
	/**
	 * Declaration constants for fabric common mapped attributes.
	 */
	public static final String FABRIC_COMMON_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_COMMON_MAPPED_ELEMENTS");
	/**
	 * Declaration constants for fabric others attributes.
	 */
	public static final String FABRIC_OTHER_FIELDS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_OTHER_FIELDS");
	/**
	 * Declaration constants for fabric/knit mapped elements.
	 */

	public static final String FABRIC_KNIT_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_KNIT_MAPPED_ELEMENTS");
	/**
	 * Declaration constants for fabric/knit mapped attributes.
	 */
	public static final String FABRIC_KNIT_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_KNIT_ATTRIBUTES");
	/**
	 * Declaration constants for Fabric material supplier attributes.
	 */

	public static final String FABRIC_MATERIAL_SUPPLIER_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_MATERIAL_SUPPLIER_ATTRIBUTES");
	/**
	 * Declaration constants for Fabric material supplier mapped elements.
	 */
	public static final String FABRIC_MATERIAL_SUPPLER_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_MATERIAL_SUPPLER_MAPPED_ELEMENTS");
	/**
	 * Declaration constants for Fabric material supplier others fields.
	 */
	public static final String FABRIC_MATERIAL_SUPPLER_OTHER_FIELDS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_MATERIAL_SUPPLER_OTHER_FIELDS");

	/**
	 * Declaration constants for Fabric material supplier attributes.
	 */
	public static final String FABRIC_SUPPLIER_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_SUPPLIER_ATTRIBUTES");
	/**
	 * Declaration constants for Fabric material supplier mapped elements.
	 */
	public static final String FABRIC_SUPPLER_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_SUPPLER_MAPPED_ELEMENTS");
	/**
	 * Declaration constants for Fabric material pricing attributes.
	 */

	public static final String FABRIC_PRICING_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_PRICING_ATTRIBUTES");
	/**
	 * Declaration constants for Fabric material pricing mapped elements.
	 */
	public static final String FABRIC_PRICING_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_PRICING_MAPPED_ELEMENTS");
	/**
	 * Declaration constants for fabric material MOA types.
	 */

	public static final String FABRIC_MAT_MOA_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_MAT_MOA_TYPE");
	/**
	 * Declaration constants for Fabric material supplier bean.
	 */
	public static final String FABRIC_MAT_SUP_BEAN_CLASS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_MAT_SUP_BEAN_CLASS");
	/**
	 * Declaration constants for Fabric material supplier bean elements.
	 */
	public static final String FABRIC_MAT_SUP_BEAN_ELEMENT=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_MAT_SUP_BEAN_ELEMENT");
	/**
	 * Declaration constants for Fabric pricing bean class.
	 */
	public static final String FABRIC_PRICING_BEAN_CLASS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_PRICING_BEAN_CLASS");
	/**
	 * Declaration constants for Fabric pricing bean elements.
	 */
	public static final String FABRIC_PRICING_BEAN_ELEMENT=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_PRICING_BEAN_ELEMENT");
	/**
	 * Declaration constants for Fabric pricing bean elements.
	 */
	public static final String FABRIC_REQUIRED_MATERIAL_SUPPLIER_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_REQUIRED_MATERIAL_SUPPLIER_ELEMENTS");
	/**
	 * Declaration constants for Fabric pricing bean elements.
	 */
	public static final String FABRIC_REQUIRED_PRICING_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_REQUIRED_PRICING_ELEMENTS");




	public static final String TRIMS_REQUIRED_MATERIAL_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_REQUIRED_MATERIAL_ELEMENTS");

	public static final String FABRIC_REQUIRED_MATERIAL_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FABRIC_REQUIRED_MATERIAL_ELEMENTS");

	/**
	 * common MOA ATTRIBUTE INTERNAL NAME.
	 */
	public static final String MATERIAL_MOA_KEY=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.COMMON_MOA_ATTRIBUTE");



	public static final String MATERIAL_INTEGRATION_QUEUE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.service.queuename");


	public static final String MATERIAL_XML_FILE_LOCATION=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.helper.materialXmlFileLoaction");

	public static final String FAKE_MDM_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.FAKE_MDM_ID","FAKEID-000");

	/**
	 * Declaration constants for material status.
	 */
	public static final String MATERIAL_STATUS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.plugin.MATERIAL_STATUS");
	/**
	 * Declaration constants for material status.
	 */
	public static final String MATERIAL_INDEVELOPMENT=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.plugin.MATERIAL_INDEVELOPMENT");
	/**
	 * Declaration constants for material status.
	 */
	public static final String MATERIAL_ACTIVE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.plugin.MATERIAL_ACTIVE");
	/**
	 * Declaration constants for material status.
	 */
	public static final String MATERIAL_DROPPED=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.plugin.MATERIAL_DROPPED");

	public static final int MATERIAL_TIMEOUT_IN_MINUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_TIMEOUT_IN_MINUTES", 3);



	//*****************************DECORATION MATERIAL OUTBOUND CONSTATS...**************************

	/**
	 * decoration class attribute.
	 *
	 */

	public static final String DECORATION_CLASSNAME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_CLASSNAME");

	/**
	 * decoration commons attribute.
	 *
	 */
	public static final String DECORATION_COMMON_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_COMMON_ATTRIBUTES");
	/**
	 * decoration commons mapped elements.
	 *
	 */
	public static final String DECORATION_COMMON_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_COMMON_MAPPED_ELEMENTS");
	/**
	 * decoration material supplier attribute.
	 *
	 */
	public static final String DECORATION_MATERIAL_SUPPLIER_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_MATERIAL_SUPPLIER_ATTRIBUTES");
	/**
	 * decoration material supplier mapped attribute.
	 *
	 */
	public static final String DECORATION_MATERIAL_SUPPLER_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_MATERIAL_SUPPLER_MAPPED_ELEMENTS");
	/**
	 * decoration material supplier other fields.
	 *
	 */
	public static final String DECORATION_MATERIAL_SUPPLER_OTHER_FIELDS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_MATERIAL_SUPPLER_OTHER_FIELDS");
	/**
	 * decoration supplier attribute.
	 *
	 */
	public static final String DECORATION_SUPPLIER_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_SUPPLIER_ATTRIBUTES");
	/**
	 * decoration suplier mapped attribute.
	 *
	 */
	public static final String DECORATION_SUPPLER_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_SUPPLER_MAPPED_ELEMENTS");
	/**
	 * decoration pricing attribute.
	 *
	 */
	public static final String DECORATION_PRICING_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_PRICING_ATTRIBUTES");
	/**
	 * decoration pricing mapped attribute.
	 *
	 */
	public static final String DECORATION_PRICING_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_PRICING_MAPPED_ELEMENTS");
	/**
	 * decoration MOA  type.
	 *
	 */
	public static final String DECORATION_MAT_MOA_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_MAT_MOA_TYPE");
	/**
	 * decoration bean attribute.
	 *
	 */
	public static final String DECORATION_MAT_SUP_BEAN_CLASS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_MAT_SUP_BEAN_CLASS");
	/**
	 * decoration class attribute.
	 *
	 */
	public static final String DECORATION_MAT_SUP_BEAN_ELEMENT=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_MAT_SUP_BEAN_ELEMENT");
	/**
	 * decoration bean  elements.
	 *
	 */
	public static final String DECORATION_PRICING_BEAN_CLASS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_PRICING_BEAN_CLASS");
	/**
	 * decoration pricing attribute.
	 *
	 */
	public static final String DECORATION_PRICING_BEAN_ELEMENT=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_PRICING_BEAN_ELEMENT");
	/**
	 * decoration pricing other fields.
	 *
	 */
	public static final String DECORATION_OTHER_FIELDS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_OTHER_FIELDS");
	/**
	 * decoration supplier required elemnts.
	 *
	 */
	public static final String DECORATION_REQUIRED_MATERIAL_SUPPLIER_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_REQUIRED_MATERIAL_SUPPLIER_ELEMENTS");
	/**
	 * decoration required pricing elements.
	 *
	 */
	public static final String DECORATION_REQUIRED_MATERIAL_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.DECORATION_REQUIRED_MATERIAL_ELEMENTS");


	//*****************************PRODUCT PACKAGING MATERIAL OUTBOUND CONSTATS...**************************

	/**
	 * product packaging class-name.
	 *
	 */
	public static final String PRODUCT_PACKAGING_CLASSNAME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_CLASSNAME");
	/**
	 * product packaging other fields.
	 *
	 */
	public static final String PRODUCT_PACKAGING_OTHER_FIELDS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_OTHER_FIELDS");
	/**
	 * product packaging common attribute.
	 *
	 */
	public static final String PRODUCT_PACKAGING_COMMON_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_COMMON_ATTRIBUTES");

	/**
	 * product packaging common mapped elemnts.
	 *
	 */
	public static final String PRODUCT_PACKAGING_COMMON_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_COMMON_MAPPED_ELEMENTS");
	/**
	 * product packaging supplier attributes.
	 *
	 */
	public static final String PRODUCT_PACKAGING_MATERIAL_SUPPLIER_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_MATERIAL_SUPPLIER_ATTRIBUTES");
	/**
	 * product packaging supplier mapped.
	 *
	 */
	public static final String PRODUCT_PACKAGING_MATERIAL_SUPPLER_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_MATERIAL_SUPPLER_MAPPED_ELEMENTS");
	/**
	 * product packaging other fields.
	 *
	 */
	public static final String PRODUCT_PACKAGING_MATERIAL_SUPPLER_OTHER_FIELDS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_MATERIAL_SUPPLER_OTHER_FIELDS");
	/**
	 * product packaging supplier attributes.
	 *
	 */
	public static final String PRODUCT_PACKAGING_SUPPLIER_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_SUPPLIER_ATTRIBUTES");
	/**
	 * product packaging mapped elements.
	 *
	 */
	public static final String PRODUCT_PACKAGING_SUPPLER_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_SUPPLER_MAPPED_ELEMENTS");
	/**
	 * product packaging pricing attributes.
	 *
	 */
	public static final String PRODUCT_PACKAGING_PRICING_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_PRICING_ATTRIBUTES");
	/**
	 * product packaging pricing mapped elements.
	 *
	 */
	public static final String PRODUCT_PACKAGING_PRICING_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_PRICING_MAPPED_ELEMENTS");
	/**
	 * product packaging MOA type.
	 *
	 */
	public static final String PRODUCT_PACKAGING_MAT_MOA_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_MAT_MOA_TYPE");
	/**
	 * product packaging supplier bean.
	 *
	 */
	public static final String PRODUCT_PACKAGING_MAT_SUP_BEAN_CLASS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_MAT_SUP_BEAN_CLASS");
	/**
	 * product packaging bean elements.
	 *
	 */
	public static final String PRODUCT_PACKAGING_MAT_SUP_BEAN_ELEMENT=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_MAT_SUP_BEAN_ELEMENT");
	/**
	 * product packaging pricing bean.
	 *
	 */
	public static final String PRODUCT_PACKAGING_PRICING_BEAN_CLASS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_PRICING_BEAN_CLASS");
	/**
	 * product packaging pricing bean element.
	 *
	 */
	public static final String PRODUCT_PACKAGING_PRICING_BEAN_ELEMENT=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_PRICING_BEAN_ELEMENT");
	/**
	 * product packaging log entry type.
	 *
	 */
	public static final String MATERIAL_LOGENTRY_PRODUCTPACKAGING_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_PRODUCTPACKAGING_TYPE");
	/**
	 * product packaging material supplier element.
	 *
	 */

	public static final String PRODUCT_PACKAGING_REQUIRED_MATERIAL_SUPPLIER_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_REQUIRED_MATERIAL_SUPPLIER_ELEMENTS");
	/**
	 * product packaging pricing element.
	 *
	 */
	public static final String PRODUCT_PACKAGING_REQUIRED_MATERIAL_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.PRODUCT_PACKAGING_REQUIRED_MATERIAL_ELEMENTS");

	/**
	 * Shipping packing class name.
	 *
	 */

	public static final String SHIPPING_AND_PACKING_CLASSNAME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_CLASSNAME");
	/**
	 *shipping packing other fields.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_OTHER_FIELDS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_OTHER_FIELDS");
	/**
	 * shipping packing common element.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_COMMON_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_COMMON_ATTRIBUTES");
	/**
	 * shipping packing common mapped element.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_COMMON_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_COMMON_MAPPED_ELEMENTS");
	/**
	 * shipping packing material supplier attribute.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_MATERIAL_SUPPLIER_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_MATERIAL_SUPPLIER_ATTRIBUTES");
	/**
	 * shipping packing supplier mapped attribute.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_MATERIAL_SUPPLER_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_MATERIAL_SUPPLER_MAPPED_ELEMENTS");
	/**
	 * shipping packing material supplier other fields.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_MATERIAL_SUPPLER_OTHER_FIELDS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_MATERIAL_SUPPLER_OTHER_FIELDS");
	/**
	 * shipping packing supplier attribute.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_SUPPLIER_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_SUPPLIER_ATTRIBUTES");
	/**
	 * shipping packing supplier mapped attribute.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_SUPPLER_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_SUPPLER_MAPPED_ELEMENTS");
	/**
	 * shipping packing pricing attribute.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_PRICING_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_PRICING_ATTRIBUTES");
	/**
	 * shipping packing pricing mapped element.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_PRICING_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_PRICING_MAPPED_ELEMENTS");
	/**
	 * shipping packing MOA type.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_MAT_MOA_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_MAT_MOA_TYPE");
	/**
	 * shipping packing material supplier bean.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_MAT_SUP_BEAN_CLASS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_MAT_SUP_BEAN_CLASS");
	/**
	 * shipping packing material supplier bean element.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_MAT_SUP_BEAN_ELEMENT=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_MAT_SUP_BEAN_ELEMENT");
	/**
	 * shipping packing pricing bean.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_PRICING_BEAN_CLASS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_PRICING_BEAN_CLASS");
	/**
	 * shipping packing pricing bean element.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_PRICING_BEAN_ELEMENT=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_PRICING_BEAN_ELEMENT");
	/**
	 * shipping packing logentry type.
	 *
	 */
	public static final String MATERIAL_LOGENTRY_SHIPPINGANDPACKING_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_SHIPPINGPACKING_TYPE");
	/**
	 * shipping packing supplier required element.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_REQUIRED_MATERIAL_SUPPLIER_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_REQUIRED_MATERIAL_SUPPLIER_ELEMENTS");

	/**
	 * shipping packing pricing required element.
	 *
	 */
	public static final String SHIPPING_AND_PACKING_REQUIRED_MATERIAL_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SHIPPING_PACKING_REQUIRED_MATERIAL_ELEMENTS");



	//public static final String TRIMS_FLAT_KNIT_RIB_LOGENTRY_MATERIAL_ATTR=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_FLAT_KNIT_RIB_LOGENTRY_MATERIAL_ATTR");


	public static final String TRIMS_FLATKNITRIB_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_FLAT_KNIT_RIB_MAPPED_ELEMENTS");
	public static final String TRIMS_FLATKNITRIB_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_FLAT_KNIT_RIB_ATTRIBUTES");

	public static final String TRIMS_HOOK_LOOP_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_HOOK_LOOP_MAPPED_ELEMENTS");
	public static final String TRIMS_HOOK_LOOP_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.TRIMS_HOOK_LOOP_ATTRIBUTES");


	/**
	 * Shipping packing class name.
	 *
	 */

	public static final String ALL_TYPE_CLASSNAME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_CLASSNAME");
	/**
	 *material ALL_TYPE other fields.
	 *
	 */
	public static final String ALL_TYPE_OTHER_FIELDS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_OTHER_FIELDS");
	/**
	 * material ALL_TYPE common element.
	 *
	 */
	public static final String ALL_TYPE_COMMON_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_COMMON_ATTRIBUTES");
	/**
	 * material ALL_TYPE common mapped element.
	 *
	 */
	public static final String ALL_TYPE_COMMON_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_COMMON_MAPPED_ELEMENTS");
	/**
	 * material ALL_TYPE material supplier attribute.
	 *
	 */
	public static final String ALL_TYPE_MATERIAL_SUPPLIER_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_MATERIAL_SUPPLIER_ATTRIBUTES");
	/**
	 * material ALL_TYPE supplier mapped attribute.
	 *
	 */
	public static final String ALL_TYPE_MATERIAL_SUPPLER_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_MATERIAL_SUPPLER_MAPPED_ELEMENTS");
	/**
	 * material ALL_TYPE material supplier other fields.
	 *
	 */
	public static final String ALL_TYPE_MATERIAL_SUPPLER_OTHER_FIELDS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_MATERIAL_SUPPLER_OTHER_FIELDS");
	/**
	 * material ALL_TYPE supplier attribute.
	 *
	 */
	public static final String ALL_TYPE_SUPPLIER_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_SUPPLIER_ATTRIBUTES");
	/**
	 * material ALL_TYPE supplier mapped attribute.
	 *
	 */
	public static final String ALL_TYPE_SUPPLER_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_SUPPLER_MAPPED_ELEMENTS");
	/**
	 * material ALL_TYPE pricing attribute.
	 *
	 */
	public static final String ALL_TYPE_PRICING_ATTRIBUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_PRICING_ATTRIBUTES");
	/**
	 * material ALL_TYPE pricing mapped element.
	 *
	 */
	public static final String ALL_TYPE_PRICING_MAPPED_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_PRICING_MAPPED_ELEMENTS");
	/**
	 * material ALL_TYPE MOA type.
	 *
	 */
	public static final String ALL_TYPE_MAT_MOA_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_MAT_MOA_TYPE");
	/**
	 * material ALL_TYPE material supplier bean.
	 *
	 */
	public static final String ALL_TYPE_MAT_SUP_BEAN_CLASS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_MAT_SUP_BEAN_CLASS");
	/**
	 * material ALL_TYPE material supplier bean element.
	 *
	 */
	public static final String ALL_TYPE_MAT_SUP_BEAN_ELEMENT=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_MAT_SUP_BEAN_ELEMENT");
	/**
	 * material ALL_TYPE pricing bean.
	 *
	 */
	public static final String ALL_TYPE_PRICING_BEAN_CLASS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_PRICING_BEAN_CLASS");
	/**
	 * material ALL_TYPE pricing bean element.
	 *
	 */
	public static final String ALL_TYPE_PRICING_BEAN_ELEMENT=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_PRICING_BEAN_ELEMENT");

	/**
	 * material ALL_TYPE supplier required element.
	 *
	 */
	public static final String ALL_TYPE_REQUIRED_MATERIAL_SUPPLIER_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_REQUIRED_MATERIAL_SUPPLIER_ELEMENTS");

	/**
	 * material ALL_TYPE pricing required element.
	 *
	 */
	public static final String ALL_TYPE_REQUIRED_MATERIAL_ELEMENTS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ALL_TYPE_REQUIRED_MATERIAL_ELEMENTS");

	/**
	 * material ALL_TYPE logentry type.
	 *
	 */
	public static final String MATERIAL_LOGENTRY_APPAREL_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_APPAREL_TYPE");
	/**
	 * MATERIAL_LOGENTRY_ARTIFICIALFUR_TYPE.
	 */
	public static final String MATERIAL_LOGENTRY_ARTIFICIALFUR_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_ARTIFICIALFUR_TYPE");
	/**
	 * MATERIAL_LOGENTRY_ARTIFICIALLEATHER_TYPE.
	 */
	public static final String MATERIAL_LOGENTRY_ARTIFICIALLEATHER_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_ARTIFICIALLEATHER_TYPE");
	/**
	 * MATERIAL_LOGENTRY_BONDEDMATERIAL_TYPE.
	 */
	public static final String MATERIAL_LOGENTRY_BONDEDMATERIAL_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_BONDEDMATERIAL_TYPE");
	/**
	 * MATERIAL_LOGENTRY_BONDEDMATERIAL_TYPE.
	 */
	public static final String MATERIAL_LOGENTRY_FOAM_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_FOAM_TYPE");
	/**
	 * MATERIAL_LOGENTRY_FOOTWEAR_TYPE.
	 */
	public static final String MATERIAL_LOGENTRY_FOOTWEAR_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_FOOTWEAR_TYPE");
	/**
	 * MATERIAL_LOGENTRY_LABOR_TYPE.
	 */
	public static final String MATERIAL_LOGENTRY_LABOR_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_LABOR_TYPE");
	/**
	 * MATERIAL_LOGENTRY_LEATHER_TYPE.
	 */
	public static final String MATERIAL_LOGENTRY_LEATHER_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_LEATHER_TYPE");
	/**
	 * MATERIAL_LOGENTRY_SPORTSEQUIPMENT_TYPE.
	 */
	public static final String MATERIAL_LOGENTRY_SPORTSEQUIPMENT_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.MATERIAL_LOGENTRY_SPORTSEQUIPMENT_TYPE");

	public static final String APPAREL_COMPOSITION = null;
	public static final String ARTIFICIAL_FUR_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ArtificialFur.composition");
	public static final String ARTIFICIAL_LEATHER_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.ArtificialLeather.composition");
	public static final String BONDED_MATERIAL_COMPOSITION = null;
	public static final String FOAM_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.Foam.composition");
	public static final String FOOTWEAR_COMPOSITION = null;
	public static final String LABOR_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.Labor.composition");
	public static final String SPORTS_EQUIPMENT_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.SportsEquipment.composition");

	public static final String FOOTWEAR_TEXTILE_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.composition.FootwearTextile");
	public static final String FOOTWEAR_UPPER_ENGINEERED_UPPER_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.composition.FootwearUpperEngineeredUpper");
	public static final String FOOTWEAR_YARN_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.composition.FootwearYarn");
	public static final String FOOTWEAR_FILM_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.composition.FootwearFilm");
	public static final String FOOTWEAR_FUR_ARTIFICIAL_FUR_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.composition.FootwearFurArtificialFur");
	public static final String FOOTWEAR_FUR_NATURAL_FUR_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.composition.FootwearFurNaturalFur");
	public static final String FOOTWEAR_ARTIFICIAL_LEATHER_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.composition.FootwearArtificialLeather");
	public static final String FOOTWEAR_INSULATION_AND_WATER_PROTECTION_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.composition.FootwearInsulationandWaterProtection");
	public static final String LEATHER_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.composition.Leather");
	public static final String LEATHER_NATURAL_LEATHER_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.composition.LeatherNaturalLeather");

	public static final String SPORTS_EQUIPMENT_BOOT_PARTS_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.composition.SportsEquipmentBootparts");

	public static final String FABRIC_NONWOVEN_COMPOSITION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.composition.fabricnonwoven");

	public static final String TRIMS_ZIPPER_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.type.TrimsZipper");
	public static final String TRIMS_FLAT_KNIT_RIB_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.type.TrimsFlatKnitRib");
	public static final String TRIMS_HOOK_LOOP_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.type.TrimsHookLoop");
	public static final String FABRIC_KNIT_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.material.type.FabricKnit");

	//*****************************SUPPLIER OUTBOUND CONSTATS...**************************

	/**
	 * Declaration internal key for Supplier MDM ID.
	 */
	public static final String SUPPLIER_MDMID= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierMDMDID");
	/**
	 * Business Supplier constant.
	 */
	public static final String BUSINESS_SUPPLIER= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.businessSupplier");
	/**
	 * Material Supplier constant.
	 */
	public static final String MATERIAL_SUPPLIER= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.materialSupplier");
	/**
	 * Factory Supplier constant.
	 */
	public static final String FACTORY= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.factory");
	/**
	 * Outbound Attributes for Business Supplier.
	 */
	public static final String OUTBOUND_BUSINESS_SUPPLIER_ATTRIBUTES = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.businessSupplierPLMAttributes");
	/**
	 * Bean Entries for Business Supplier.
	 */
	public static final String BUSINESS_SUPPLIER_BEAN_ATTRIBUTES = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.businessSupplierBeanAttributes");
	/**
	 * Outbound Attributes for Material Supplier.
	 */
	public static final String OUTBOUND_MATERIAL_SUPPLIER_ATTRIBUTES = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.materialSupplierPLMAttributes");
	/**
	 * Bean Entries for Material Supplier.
	 */
	public static final String MATERIAL_SUPPLIER_BEAN_ATTRIBUTES = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.materialSupplierBeanAttributes");
	/**
	 * Outbound Attributes for Factory.
	 */
	public static final String OUTBOUND_FACTORY_ATTRIBUTES = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.factoryPLMAttributes");
	/**
	 * Bean Entries for Factory.
	 */
	public static final String FACTORY_BEAN_ATTRIBUTES = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.factoryBeanAttributes");
	/**
	 * Outbound Schedule Queue Name for Supplier.
	 */
	public static final String SUPPLIER_OUTBOUND_SCHEDULE_QUEUE_NAME = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierOutBoundScheduleQueueName");
	/**
	 * Outbound Schedule Queue Name for Supplier.
	 */
	public static final String SUPPLIER_PROCESSING_QUEUE_NAME = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierProcessingQueueName");
	/**
	 * Log Entry Name key for Outbound\Supplier.
	 */
	public static final String SUPPLIER_LOG_ENRTY_OBJECT_NAME = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryObjectName");
	/**
	 * Log Entry Request ID key for Outbound\Supplier.
	 */
	public static final String SUPPLIER_LOG_ENRTY_REQUEST_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryRequestID");
	/**
	 * Log Entry MDMID key for Outbound\Supplier.
	 */
	public static final String SUPPLIER_LOG_ENRTY_MDMID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryMDMID");
	/**
	 * Log Entry Object ID key for Outbound\Supplier.
	 */
	public static final String SUPPLIER_LOG_ENTRY_OBJECTID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryObjectID");
	/**
	 * Log Entry Integration Status key for Outbound\Supplier.
	 */
	public static final String SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryIntegrationStatus");
	/**
	 * Log Entry Error Reason key for Outbound\Supplier.
	 */
	public static final String SUPPLIER_LOG_ENTRY_ERROR_REASON = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryErrorReason");
	/**
	 * Log Entry CREATE PENDING status.
	 */
	public static final String SUPPLIER_LOG_ENTRY_CREATE_PENDING = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryCreatePendingStatus");
	/**
	 * Log Entry UPDATE PENDING status.
	 */
	public static final String SUPPLIER_LOG_ENTRY_UPDATE_PENDING = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryUpdatePendingStatus");
	/**
	 * Log Entry CREATE PROCESSED status.
	 */
	public static final String SUPPLIER_LOG_ENTRY_CREATE_PROCESSED = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryCreateProcessedStatus");

	/**
	 * Log Entry UPDATE PROCESSED status.
	 */
	public static final String SUPPLIER_LOG_ENTRY_UPDATE_PROCESSED = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryUpdateProcessedStatus");
	/**
	 * Log Entry Outbound Path.
	 */
	public static final String LOG_ENRTY_SUPPLIER_OUTBOUND_PATH = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryOutboundSupplierPath");
	/**
	 * Log Entry Outbound\Business Supplier Path.
	 */
	public static final String LOG_ENRTY_BUSINESS_SUPPLIER_OUTBOUND_PATH = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryOutboundBusinessSupplierPath");
	/**
	 * Log Entry Outbound\Material Supplier Path.
	 */
	public static final String LOG_ENRTY_MATERIAL_SUPPLIER_OUTBOUND_PATH = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryOutboundMaterialSupplierPath");
	/**
	 * Log Entry Outbound\Factory Path.
	 */
	public static final String LOG_ENRTY_FACTORY_OUTBOUND_PATH = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryOutboundFactorySupplierPath");
	/**
	 * Log Entry Request ID Constant.
	 */
	public static final String SUPPLIER_LOG_ENTRY_REQUEST_ID_CONSTANT = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntrySupplierRequestIDConstant");
	/**
	 * Supplier Outbound Log Entry Path.
	 */
	public static final String ALL_LOG_ENTRY_OUTBOUND_SUPPLIER_PATH = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryOutboundSupplierPathCombined");
	/**
	 * Create Request.
	 */
	public static final String SUPPLIER_CREATE_REQUEST_FAILED = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierCreateRequest");
	/**
	 * Update Request.
	 */
	public static final String SUPPLIER_UPDATE_REQUEST = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierUpdateRequest");
	/**
	 * Supplier Dummy MDM ID.
	 */
	public static final String SUPPLIER_DUMMY_MDM_ID_ON_CREATE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.dummyMDMID");
	/**
	 * Supplier MDM ID Key.
	 */
	public static final String SUPPLIER_MDM_ID_KEY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierMDMIDKey");
	/**
	 * Supplier Agent Key.
	 */
	public static final String SUPPLIER_AGENT_KEY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.agentAttributeKey");
	/**
	 * Supplier Request XML FILE LOCATION.
	 */
	public static final String SUPPLIER_REQUEST_XML_FILE_LOCATION = LCSProperties.get("com.sm.wc.interface.xmlGeneration.supplierRequestXMLLocation");
	/**
	 * Supplier Request for QUEUE_UPDATE.
	 */
	public static final String SUPPLIER_QUEUE_REQUEST = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierQueueUpdateRequest");
	/**
	 * Supplier Country Obj Reference Key.
	 */
	public static final String SUPPLIER_COUNTRY_OBJ_REF_KEY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.countryAttributeKey");
	/**
	 * Country MDMID key.
	 */
	public static final String COUNTRY_MDMID_KEY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.countryMDMIDKey");
	/**
	 * Supplier Contact MOA Table Key.
	 */
	public static final String SUPPLIER_CONTACT_MOA_KEY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierContactMOATableKey");
	/**
	 * Supplier Contact MOA Table Name Key.
	 */
	public static final String SUPPLIER_CONTACT_MOA_NAME_KEY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierContactMOATableNameKey");
	/**
	 * Supplier Contact MOA Table Title Key.
	 */
	public static final String SUPPLIETR_CONTACT_MOA_TITLE_KEY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierContactMOATableTitleKey");
	/**
	 * Supplier Contact MOA Table Title Key.
	 */
	public static final String SUPPLIETR_CONTACT_MOA_END_DATE_KEY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierContactMOATableEndDate");
	/**
	 * Supplier Contact MOA Table Area Of Responsibility Key.
	 */
	public static final String SUPPLIER_CONTACT_MOA_AREA_OF_RESPONSIBILITY_KEY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierContactMOATableKeyAreaOfResponsibilityKey");
	/**
	 * Supplier Contact MOA Table Email Key.
	 */
	public static final String SUPPLIER_CONTACT_MOA_EMAIL_KEY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierContactMOATableEmailKey");
	/**
	 * Supplier Contact MOA Table Phone Key.
	 */
	public static final String SUPPLIER_CONTACT_MOA_PHONE_KEY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierContactMOATablePhoneKey");
	/**
	 * Supplier Contact MOA Table MOA Attributes Key.
	 */
	public static final String SUPPLIER_CONTACT_MOA_ATTRIBUTES= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierContactMOATableAttributesKey");
	/**
	 * Supplier Object Deleted Log Entry Status.
	 */
	public static final String LOG_ENTRY_OBJECT_MISSING= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.logEntryObjectDeletedStatus");
	/**
	 * Supplier Create Response is Success.
	 */
	public static final String SUPPLIER_CREATE_REQUEST_SUCCESS= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierCreateSuccessful");
	/**
	 * Supplier Create Response is Success.
	 */
	public static final String SUPPLIER_UPDATE_REQUEST_SUCCESS= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierUpdateRequestSucess");
	/**
	 * Supplier Create Response is Success.
	 */
	public static final String SUPPLIER_UPDATE_REQUEST_FAILED= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierUpdateRequestFailed");
	/**
	 * Supplier Request Time Out Setting.
	 */
	public static final int SUPPLIER_WEB_SERVICE_REQUEST_TIMEOUT_IN_MINUTES= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.supplierRequestTimeOutInMinutes",3);
	/**
	 * Interval in minutes for Supplier Queue.
	 */
	public static final long SUPPLIER_SCHEDULE_QUEUE_INTERVAL_IN_MINS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.intervalInMinutes",240);
	/**
	 * Sub Division Queue start time.
	 */
	public static final String SUPLIER_SCHEDULE_QUEUE_START_TIME = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.scheduleQueueStartTime");
	/**
	 * AM or PM for SubDivision Queue.
	 */
	public static final String SUPPLIER_SCHEDULE_QUEUE_START_AM = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.AMorPM");
	/**
	 * LCSLogEntry.
	 */
	public static final String LCSLOGENTRY = "LCSLogEntry";
	/**
	 * flexTypeIdPath.
	 */
	public static final String FLEXTYPEIDPATH = "flexTypeIdPath";
	/**
	 * Under Review state.
	 */
	public static final String SUPPLIER_LIFECYCLE_STATE_UNDER_REVIEW = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.underReviewState");
	/**
	 * Released state.
	 */
	public static final String SUPPLIER_LIFECYCLE_STATE_RELEASED = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.releasedState");
	/**
	 * Canceled state.
	 */
	public static final String SUPPLIER_LIFECYCLE_STATE_CANCELED = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.canceledState");
	/**
	 * Previous Lifecycle state key.
	 */
	public static final String SUPPLIER_PREVIOUS_LIFECYCLE_ATTRIBUTE_KEY = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.previousLifeCycleStateKey");
	/**
	 * Verbose for XML Generation for Supplier.
	 */
	public static final boolean SUPPLIER_REQUEST_XML_GENERATION_VERBOSE = LCSProperties
			.getBoolean("com.sportmaster.wc.interfaces.webservices.outbound.supplier.enableRequestXMLGenerationForSupplierIntegration");
	/**
	 * Business Supplier Path.
	 */
	public static final String BUSINESS_SUPPLIER_PATH = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.businessSupplierPath");

	/**
	 * ******************************** Entries for Phase-13 BUILD changes
	 * ********************************
	 */
	/**
	 * To integrate key
	 */
	public static final String TO_INTEGRATE = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.toIntegrate");
			
	/**
	* // Phase 14 - EMP-449 - SUPPLIER_PREFIX_KEY
	**/
	public static final String SUPPLIER_PREFIX_KEY = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.supplier.smSupplierPrefix");

}
