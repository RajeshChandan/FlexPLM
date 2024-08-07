/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util;

import com.lcs.wc.util.LCSProperties;


/**
 * SMCareLabelConstants - Functionality .
 * 
 * @author 'true' ITC
 * @version 'true' 1.0 version number
 * @since March 14, 2018
 */

public class SMCareLabelConstants {

	//protected constructor.
	protected SMCareLabelConstants(){
		//constructor.
	}


	/**
	 * Care Label Log Entry Path.
	 */
	public static final String CARE_LABEL_INTEGARTION_LOG_ENTRY_PATH = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.logEntry.logEntryCareLabelPath");
	/**
	 * Log Entry Request ID ATT.
	 */
	public static final String CARE_LABEL_LOG_ENTRY_REQUEST_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.logEntry.logEntryRequestID");


	/**
	 * Log Entry Request ID initia ID.
	 */
	public static final String LOG_ENTRY_CARELABEL_OUTBOUND_INTEGRATION_INITIAL_REQUEST_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.logEntry.initialRequestID");


	/**
	 * Log Entry Error Reason.
	 */
	public static final String CARE_LABEL_LOG_ENTRY_ERROR_REASON = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.logEntry.logEntryErrorReason");
	/**
	 * Log Entry Integration Triggered By.
	 */
	public static final String CARE_LABEL_LOG_ENTRY_INTEGRATION_TRIGGERED_BY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.logEntry.logEntryTriggeredBy");
	/**
	 * Log Entry PLM ID.
	 */
	public static final String CARE_LABEL_LOG_ENTRY_PLM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.logEntry.logEntryPLMID");
	/**
	 * Log Entry MDM ID.
	 */
	public static final String CARE_LABEL_LOG_ENTRY_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.logEntry.logEntryMDMID");
	/**
	 * Log Entry Integration status.
	 */
	public static final String CARE_LABEL_LOG_ENTRY_INTEGRATION_STATUS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.logEntry.logEntryIntegrationStatus");
	/**
	 * Log Entry Filters Applied.
	 */
	public static final String CARE_LABEL_LOG_ENTRY_FILTERS_APPLIED = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.logEntry.logEntryFiltersApplied");
	/**
	 * Log Entry Object Details.
	 */
	public static final String CARE_LABEL_LOG_ENTRY_OBJECT_DETAILS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.logEntry.logEntryObjectDetails");
	/**
	 * Product Season Link MDM ID.
	 */
	public static final String PRODUCT_SEASON_LINK_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.productSeason.prodSeasonMDMID");
	/**
	 * Product Season Link Ad Hoc PLM ID.
	 */
	public static final String PRODUCT_SEASON_LINK_AD_HOC_PLM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.productSeason.adHocPLMIDPSL");
	/**
	 * Colorway Season Link Ad Hoc PLM ID.
	 */
	public static final String COLORWAY_SEASON_LINK_AD_HOC_PLM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.colorwaySeason.adHocPLMIDSSL");
	/**
	 * Log Entry PENDING status.
	 */
	public static final String CARE_LABEL_LOG_ENTRY_PENDING_STATUS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.logEntry.logEntryPendingStatus");
	/**
	 * Log Entry PROCESSED status.
	 */
	public static final String CARE_LABEL_LOG_ENTRY_PROCESSED_STATUS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.logEntry.logEntryProcessedStatus");
	/**
	 * Care Label Outbound Integration XML Location.
	 */
	public static final String CARE_LABEL_INTEGRATION_XML_GENERATION_LOCATION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.careLabelIntegrationXMLFileLocation");
	/**
	 * XML Genration boolean check
	 */
	public static final boolean IS_CARE_LABEL_INTEGRATION_XML_GENERATION = LCSProperties.getBoolean("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.isCareLabelXMLGeneration");
	/**
	 * QUEUE NAME
	 */
	public static final String CARELABEL_QUEUE_NAME = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.queueName");
	/**
	 * STYLE_NUM
	 */
	public static final String STYLE_NUM = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.product.vrdStyleNum");
	/**
	 * BRAND
	 */
	public static final String BRAND = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.product.vrdBrand");
	/**
	 * AGE
	 */
	public static final String AGE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.product.smAge");
	/**
	 * Gender
	 */
	public static final String GENDER = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.gender");
	/**
	 * PROJECT
	 */
	public static final String PROJECT = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.product.smProject");
	/**
	 * PRODUCTION_GROUP
	 */
	public static final String PRODUCTION_GROUP = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.prodseason.smProductionGroup");
	/**
	 * TECNOLOGIST
	 */
	public static final String TECNOLOGIST = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.prodseason.smProducctTechnologist");
	/**
	 * ORDER_DESTINATION
	 */
	public static final String ORDER_DESTINATION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.sourceseason.smSSLDestination");

	/**
	 * FACTROTY
	 */
	public static final String FACTROTY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.sourceseason.smSSLFactory");

	/**
	 * VENDOR
	 */
	public static final String VENDOR = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.source.vendor");

	/**
	 * MATERIAL_TYPE
	 */
	public static final String MATERIAL_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.material.vrdType");

	/**
	 * UNIT_MEASURE
	 */
	public static final String UNIT_MEASURE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.material.unitOfMeasure");

	/**
	 * FINISH
	 */
	public static final String FINISH = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.fabric.vrdFinish");

	/**
	 * LAMINATION_COATING
	 */
	public static final String LAMINATION_COATING = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.fabric.smLaminationCoating");

	/**
	 * ADDITIONALI_CARE
	 */
	public static final String ADDITIONALI_CARE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.matcol.fabric.smAdditionalCareMC");

	/**
	 * QUANTITY
	 */
	public static final String QUANTITY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.bomlink.quantity");
	/**
	 * PRIMARY
	 */
	public static final String PRIMARY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.bomlink.smPrimary");
	/**
	 * ALT_PRIMARY
	 */
	public static final String ALT_PRIMARY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.bomlink.smAltPrimary");
	/**
	 * CCC
	 */
	public static final String CCC = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.bomlink.smBOMContrastColorCombination");
	/**
	 * COMPONENET_NAME
	 */
	public static final String COMPONENET_NAME = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.bom.smComponentName");
	/**
	 * COMPONENET_NAME
	 */
	public static final String PART_NAME = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.bom.partName");
	/**
	 * COMPONENET_NAME
	 */
	public static final String BOM_STATUS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.bom.vrdBOMStatus");

	/**
	 * Outbound Connectivity Error Code.
	 */
	final public static String OUTBOUND_CL_CONNECTIVITY_ERROR_CODE= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.conectivity");
	/**
	 * Outbound Schema Error Code.
	 */
	final public static String OUTBOUND_CL_SCHEMA_ERROR_CODE= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.schema");
	
	/**
	 * Outbound Connectivity Message
	 */
	final public static String OUTBOUND_CL_CONNECTIVITY_ERROR_MESG= LCSProperties.get("com.sportmaster.wc.interfaces.webservice.connectivity.carelabel.commonmassage");
	/**
	 * Outbound Schema Error Message.
	 */
	final public static String OUTBOUND_CL_SCHEMA_ERROR_MESG= LCSProperties.get("com.sportmaster.wc.interfaces.webservice.carelabel.schema.commonmassage");
	
	/**
	 * MANAGING_DEPARTMENT 3.8.1.0 build
	 */
	public static final String MANAGING_DEPARTMENT = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.carelabel.integration.material.managingDepartment");

}
