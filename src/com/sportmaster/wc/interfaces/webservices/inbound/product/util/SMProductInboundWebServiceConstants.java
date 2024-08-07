/**
 *
 */
package com.sportmaster.wc.interfaces.webservices.inbound.product.util;

import com.lcs.wc.util.LCSProperties;

/**
 * @author Carrier
 *
 */
public class SMProductInboundWebServiceConstants {

	/**
	 * protected constructor.
	 */
	protected SMProductInboundWebServiceConstants(){
		//protected constructor
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////  PRODUCT  INDBOUND  INTRGRATION  CONSTANTS ////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Product Season link MDMID.
	 */
	public static final String PRODUCT_SEASON_LINK_MDMID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.MDMID");
	/**
	 * Product Season Inbound Timeout in Minutes.
	 */
	public static final int PRODUCT_INBOUND_TIMEOUT_IN_MINUTES = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productInboundTimeOutInMinutes",180000);
	/**
	 * Product Season link Intake date Russia.
	 */
	public static final String PRODUCT_SEASON_LINK_INTAKE_DATE_RUSSIA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.smInTakeDateStyleRussia");
	/**
	 * Product Season link Intake date China.
	 */
	public static final String PRODUCT_SEASON_LINK_INTAKE_DATE_CHINA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.smIntakeDateStyleChina");
	/**
	 * Product Season link 1st Forecast On Hold Russia.
	 */
	public static final String PRODUCT_SEASON_LINK_FIRST_FORECAST_ON_HOLD_RUSSIA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.smForecastUnitsStyle1stRussiaOnHold");
	/**
	 * Product Season link 1st Forecast On Hold Ukraine.
	 */
	public static final String PRODUCT_SEASON_LINK_FIRST_FORECAST_ON_HOLD_UKRAINE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.smForecastUnitsStyle1stUkraineOnHold");
	/**
	 * Product Season link 1st Forecast On Hold China.
	 */
	public static final String PRODUCT_SEASON_LINK_FIRST_FORECAST_ON_HOLD_CHINA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.smForecastUnitsStyle1stChinaOnHold");
	/**
	 * Product Season link 1st Forecast Russia.
	 */
	public static final String PRODUCT_SEASON_LINK_FIRST_FORECAST_RUSSIA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.smForecastUnitsStyle1stRussia");
	/**
	 * Product Season link 1st Forecast Ukraine.
	 */
	public static final String PRODUCT_SEASON_LINK_FIRST_FORECAST_UKRAINE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.smForecastUnitsStyle1stUkraine");
	/**
	 * Product Season link 1st Forecast China.
	 */
	public static final String PRODUCT_SEASON_LINK_FIRST_FORECAST_CHINA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.smForecastUnitsStyle1stChina");
	/**
	 * Product Season link 2nd Forecast Russia.
	 */
	public static final String PRODUCT_SEASON_LINK_SECOND_FORECAST_RUSSIA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.smForecastUnitsStyle2ndRussia");
	/**
	 * Product Season link 2nd Forecast Ukraine.
	 */
	public static final String PRODUCT_SEASON_LINK_SECOND_FORECAST_UKRAINE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.smForecastUnitsStyle2ndUkraine");
	/**
	 * Product Season link 2nd Forecast China.
	 */
	public static final String PRODUCT_SEASON_LINK_SECOND_FORECAST_CHINA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.smForecastUnitsStyle2ndChina");
	/**
	 * Product Season link Bulk Order Russia.
	 */
	public static final String PRODUCT_SEASON_LINK_BULK_ORDER_RUSSIA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.smBulkOrderUnitsStyleRussia");
	/**
	 * Product Season link Bulk Order Ukraine.
	 */
	public static final String PRODUCT_SEASON_LINK_BULK_ORDER_UKRAINE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.smBulkOrderUnitsStyleUkraine");
	/**
	 * Product Season link Bulk Order China.
	 */
	public static final String PRODUCT_SEASON_LINK_BULK_ORDER_CHINA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.smBulkOrderUnitsStyleChina");
	/**
	 * Colorway Season link MDM ID.
	 */
	public static final String COLORWAY_SEASON_LINK_MDMID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.MDMID");
	/**
	 * Colorway Season link Intake date Russia.
	 */
	public static final String COLORWAY_SEASON_LINK_INTAKE_DATE_RUSSIA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.smIntakeDateColorwayRussia");
	/**
	 * Colorway Season link Intake date China.
	 */
	public static final String COLORWAY_SEASON_LINK_INTAKE_DATE_CHINA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.smIntakeDateColorwayChina");
	/**
	 * Colorway Season link First Forecast on Hold Russia.
	 */
	public static final String COLORWAY_SEASON_LINK_FIRST_FORECAST_ON_HOLD_RUSSIA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.smForecastUnitsColorway1stRussiaOnHold");
	/**
	 * Colorway Season link First Forecast on Hold Ukraine.
	 */
	public static final String COLORWAY_SEASON_LINK_FIRST_FORECAST_ON_HOLD_UKRAINE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.smForecastUnitsColorway1stUkraineOnHold");
	/**
	 * Colorway Season link First Forecast on Hold China.
	 */
	public static final String COLORWAY_SEASON_LINK_FIRST_FORECAST_ON_HOLD_CHINA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.smForecastUnitsColorway1stChinaOnHold");
	/**
	 * Colorway Season link First Forecast Russia.
	 */
	public static final String COLORWAY_SEASON_LINK_FIRST_FORECAST_RUSSIA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.smForecastUnitsColorway1stRussia");
	/**
	 * Colorway Season link First Forecast Ukraine.
	 */
	public static final String COLORWAY_SEASON_LINK_FIRST_FORECAST_UKRAINE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.smForecastUnitsColorway1stUkraine");
	/**
	 * Colorway Season link First Forecast China.
	 */
	public static final String COLORWAY_SEASON_LINK_FIRST_FORECAST_CHINA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.smForecastUnitsColorway1stChina");
	/**
	 * Colorway Season link 2nd Forecast Russia.
	 */
	public static final String COLORWAY_SEASON_LINK_SECOND_FORECAST_RUSSIA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.smForecastUnitsColorway2ndRussia");
	/**
	 * Colorway Season link 2nd Forecast Ukraine.
	 */
	public static final String COLORWAY_SEASON_LINK_SECOND_FORECAST_UKRAINE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.smForecastUnitsColorway2ndUkraine");
	/**
	 * Colorway Season link 2nd Forecast China.
	 */
	public static final String COLORWAY_SEASON_LINK_SECOND_FORECAST_CHINA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.smForecastUnitsColorway2ndChina");
	/**
	 * Colorway Season link 2nd Forecast China.
	 */
	public static final String COLORWAY_SEASON_LINK_BULK_ORDER_RUSSIA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.smBulkOrderUnitsColorwayRussia");
	/**
	 * Colorway Season link 2nd Forecast China.
	 */
	public static final String COLORWAY_SEASON_LINK_BULK_ORDER_UKRAINE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.smBulkOrderUnitsColorwayUkraine");
	/**
	 * Colorway Season link 2nd Forecast China.
	 */
	public static final String COLORWAY_SEASON_LINK_BULK_ORDER_CHINA=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.smBulkOrderUnitsColorwayChina");
	/**
	 * Product Season Link Log Entry Path.
	 */
	public static final String LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PATH=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.logEntry.productSeasonLinkLogEntryPath");
	/**
	 * Product Season Link Log Entry Request ID.
	 */
	public static final String LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_REQUEST_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.logEntry.requestID");
	/**
	 * Product Season Link Log Entry Link Type.
	 */
	public static final String LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_OBJECT_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.logEntry.objectType");
	/**
	 * Product Season Link Log Entry MDM ID.
	 */
	public static final String LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_MDM_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.logEntry.mdmID");
	/**
	 * Product Season Link Log Entry Link Details.
	 */
	public static final String LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_DETAILS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.logEntry.linkDetails");
	/**
	 * Product Season Link Log Entry PLM ID.
	 */
	public static final String LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PLM_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.logEntry.plmID");
	/**
	 * Product Season Link Log Entry Feedback Status.
	 */
	public static final String LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_FEEDBACK_STATUS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.logEntry.feedbackStatus");
	/**
	 * Product Season Link Log Entry Error Reason.
	 */
	public static final String LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_ERROR_REASON=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.logEntry.errorReason");
	/**
	 * Product Season Link Log Entry Integration Status.
	 */
	public static final String LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_INTEGRATION_STATUS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonLink.logEntry.integartionStatus");
	/**
	 * Colorway Season Link Log Entry Integration Path.
	 */
	public static final String LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PATH=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.logEntry.colorwaySeasonLinkLogEntryPath");
	/**
	 * Colorway Season Link Log Entry Integration Path.
	 */
	public static final String LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_REQUEST_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.logEntry.requestID");
	/**
	 * Colorway Season Link Log Entry Integration Object Type.
	 */
	public static final String LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_OBJECT_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.logEntry.objectType");
	/**
	 * Colorway Season Link Log Entry Integration MDM ID.
	 */
	public static final String LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_MDMID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.logEntry.mdmID");
	/**
	 * Colorway Season Link Log Entry Integration link Details.
	 */
	public static final String LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_LINK_DETAILS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.logEntry.linkDetails");
	/**
	 * Colorway Season Link Log Entry Integration PLM ID.
	 */
	public static final String LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.logEntry.plmID");
	/**
	 * Colorway Season Link Log Entry Integration Feedback Status.
	 */
	public static final String LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_FEEDBACK_STATUS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.logEntry.feedbackStatus");
	/**
	 * Colorway Season Link Log Entry Integration Error Reason.
	 */
	public static final String LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_ERROR_REASON=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.logEntry.errorReason");
	/**
	 * Colorway Season Link Log Entry Integration Status.
	 */
	public static final String LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_INTEGARTION_STATUS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.colorwaySeasonLink.logEntry.integartionStatus");
	/**
	 * Request ID Initial Value.
	 */
	public static final String PRODUCT_INBOUND_INTEGRATION_REQUEST_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.logEntry.requestIDInitialValue");
	/**
	 * Product Inbound Integration webservice name.
	 */
	public static final String PRODUCT_INBOUND_INTEGRATION=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.webserviceName");
	/**
	 * XML File location for Product Inbound Integration.
	 */
	public static final String PRODUCT_INBOUND_RESPONSE_XML_FILE_LOCATION=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productSeasonInboundDataResponseXMLFileLocation");
	/**
	 * XML File location for Colorway Inbound Integration.
	 */
	public static final String COLORWAY_INBOUND_RESPONSE_XML_FILE_LOCATION=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.skuSeasonInboundDataResponseXMLFileLocation");
	/**
	 * Product Inbound Integration Queue Name.
	 */
	public static final String PRODUCT_INBOUND_QUEUE_NAME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.productInboundQueueName");
	/**
	 * Product Inbound Queue Start Time.
	 */
	public static final String PRODUCT_INBOUND_QUEUE_START_TIME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.productInboundIntegartionQueue.scheduleQueueStartTime");
	/**
	 * Product Inbound Queue Interval in Minutes.
	 */
	public static final long PRODUCT_INBOUND_QUEUE_INTERVAL_IN_MINUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.productInboundIntegartionQueue.intervalInMinutes",30);
	/**
	 * Product Inbound Queue AM or PM.
	 */
	public static final String PRODUCT_INBOUND_QUEUE_START_AM=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.productInboundIntegartionQueue.AMorPM");
	/**
	 * LCSLogEntry.
	 */
	final public static String LCSLOGENTRY= "LCSLogEntry";
	/**
	 * Status Integrated.
	 */
	final public static String INTEGRATED= "INTEGRATED";
	/**
	 * Status Not Integrated.
	 */
	final public static String NOT_INTEGRATED= "NO_INTEGRATED";
	/**
	 * Status Pending.
	 */
	final public static String PENDING= "PENDING";
	/**
	 * Product Inbound Feedback Queue Start Time.
	 */
	public final static String PRODUCT_INBOUND_FEEDBACK_QUEUE_START_TIME = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.productInboundFeedbackIntegartion.scheduleQueueStartTime");
	/**
	 * Product Inbound Feedback Interval in Minutes.
	 */
	public static final long PRODUCT_INBOUND_FEEDBACK_QUEUE_INTERVAL_IN_MINUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.productInboundFeedbackIntegartion.intervalInMinutes",60);
	/**
	 * Product Inbound Feedback Queue AM or PM.
	 */
	public static final String PRODUCT_INBOUND_FEEDBACK_QUEUE_START_AM = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.productInboundFeedbackIntegartion.AMorPM");
	/**
	 * Product Inbound Feedback Integration webservice name.
	 */
	public static final String PRODUCT_INBOUND_FEEDBACK_INTEGRATION=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.feedback.productInboundFeedback");
	/**
	 * XML File location for Product Inbound Feedback Integration.
	 */
	public static final String PRODUCT_INBOUND_FEEDBACK_REQUEST_XML_FILE_LOCATION=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.feedback.productInboundFeedbackRequestXMLFileLocation");
	/**
	 * Product Inbound Feedback Integration Queue Name.
	 */
	public static final String PRODUCT_INBOUND_FEEDBACK_QUEUE_NAME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.feedback.productInboundFeedbackQueueName");
	/**
	 * XML File location for Colorway Inbound Feedback Integration.
	 */
	public static final String COLORWAY_INBOUND_FEEDBACK_REQUEST_XML_FILE_LOCATION=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.feedback.colorwayInboundFeedbackRequestXMLFileLocation");
	/**
	 * Enable XML Generation for Product-Season/SKU-Season Inbound data.
	 */
	public static final boolean GENERATE_RESPONSE_FOR_INBOUND_DATA = LCSProperties.getBoolean("com.sportmaster.wc.interfaces.webservices.inbound.product.processor.generateInboundXMLFiles");
	/**
	 * Enable XML Generation for Product-Season/SKU-Season Feedback data.
	 */
	public static final boolean GENERATE_RESPONSE_FOR_FEEDBACK_DATA = LCSProperties.getBoolean("com.sportmaster.wc.interfaces.webservices.inbound.product.feedback.generateInboundFeedbackXMLFiles");
	/**
	 * Received Valid status in Feedback.
	 */
	public static final String RECEIVED_VALID_FEEDBACK = "RECEIVED_VALID";
	/**
	 * Received Invalid status in Feedback.
	 */
	public static final String RECEIVED_INVALID_FEEDBACK = "RECEIVED_INVALID";
	/**
	 * Processed status.
	 */
	public static final String PROCESSED = "PROCESSED";

	/**
	 * PHASE 8 SEPD CHNAGES.
	 * added new filed iteration id on log entry.
	 *
	 */

	/**
	 * ITERATION_ID.
	 */
	public static final String ITERATION_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.logEntry.iterationId","smIterationId");
	/**
	 * ITERATION_ID_DEFAULT_VAL.
	 */
	public static final String ITERATION_ID_DEFAULT_VAL=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.product.logEntry.iterationId.defaultVal","EmptyID");

	/**
	 * *************************************
	 * Entries for Phase-13 BUILD changes
	 * *************************************
	 */
	// Bulk Order-Style-Total
	public static final String BULK_ORDER_STYLE_TOTAL = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.productseasonlink.smBulkOrderUnitsStyleTotal");
	// PSL_BLOCKED
	public static final String PSL_BLOCKED = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.productseasonlink.smPSBlocked");
	// PSL_UPPER
	public static final String PSL_UPPER = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.productseasonlink.smPSUpper");
	// PSL_LINNING
	public static final String PSL_LINNING = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.productseasonlink.smPSLining");
	// PSL_SOLE
	public static final String PSL_SOLE = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.productseasonlink.smPSSole");
	// PROD_UPPER
	public static final String PROD_UPPER = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.product.smProdUpper");
	// PROD_LINNING
	public static final String PROD_LINNING = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.product.smProdLining");
	// PROD_SOLE
	public static final String PROD_SOLE = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.product.smProdSole");
}

