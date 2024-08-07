package com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.util;
import com.lcs.wc.util.LCSProperties;
/**
 * SMPricesInboundWebServiceConstants.java
 * This class is using for constants from the property file.
 * @author 'true' Abhay Singh
 * @version 'true' 1.0 version number
 */
public class SMPricesInboundWebServiceConstants {


	/**
	 * Constructor.
	 */
	protected SMPricesInboundWebServiceConstants() {
	}
	/**
	 * ERROR_LITERAL.
	 */
	public static final String ERROR_LITERAL = "ERROR FOUND:-";
	/**
	 * Declaration constants for web service schema ERROR CODE.
	 */
	public static final String REQUEST_QUEUE_NAME = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.pricesUpdate.QueueName", "PRICES_INBOUND_REQUEST_QUEUE");
	/**
	 * price update Inbound Feedback Interval in Minutes.
	 */
	public static final long REQUEST_QUEUE_INTERVAL_IN_MINUTES = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.pricesUpdate.request.queue.intervalInMinutes", 10);
	/**
	 * price update Inbound Queue Start Time.
	 */
	public static final String REQUEST_QUEUE_START_TIME = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.pricesUpdate.request.queue.startTime", "7");
	/**
	 * price update Inbound Queue AM or PM.
	 */
	public static final String REQUEST_QUEUE_START_AM = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.pricesUpdate.request.queue.AMorPM", "AM");

	/**
	 * Declaration constants for web service schema ERROR CODE.
	 */
	public static final String FEEDBACK_QUEUE_NAME = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.pricesUpdate.feedback.QueueName", "PRICES_INBOUND_FEEDBACK_QUEUE");
	/**
	 * price update Inbound Feedback Interval in Minutes.
	 */
	public static final long FEEDBACK_QUEUE_INTERVAL_IN_MINUTES = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.pricesUpdate.feedback.queue.intervalInMinutes", 10);
	/**
	 * price update Inbound Queue Start Time.
	 */
	public static final String FEEDBACK_QUEUE_START_TIME = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.pricesUpdate.feedback.queue.startTime", "7");
	/**
	 * price update Inbound Queue AM or PM.
	 */
	public static final String FEEDBACK_QUEUE_START_AM = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.pricesUpdate.feedback.queue.AMorPM", "AM");

	/**
	 * Request ID Initial Value.
	 */
	public static final int INTEGRATION_REQUEST_ID = Integer
			.parseInt(LCSProperties.get("com.sportmaster.wc.interfaces.webservices.inbound.pricesUpdate.requestIDInitialValue"));
	/**
	 * LCSLogEntry.
	 */
	public static final String LCSLOGENTRY = "LCSLogEntry";
	/**
	 * Price Update Log Entry Path.
	 */
	public static final String LOG_ENTRY_INBOUND_PRICE_UPDATE_PATH = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.pricesUpdate.logEntry.priceUpdateLogEntryPath");
	/**
	 *prices update Log Entry Request ID.
	 */
	public static final String LOG_ENTRY_INBOUND_PRICE_UPDATE_REQUEST_ID = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.pricesUpdate.logEntry.requestID");
	/**
	 * Price update Inbound Timeout in Minutes.
	 */
	public static final int PRICE_UPDATE_INBOUND_TIMEOUT_IN_MINUTES = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.pricesUpdate.timeOutInMinutes", 180000);
	/**
	 * price update Inbound Integration webservice name.
	 */
	public static final String PRICES_UPDATE_INBOUND_INTEGRATION = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.pricesUpdate.webserviceName");
	/**
	 * Price update Outbound Integration XML Location.
	 */
	public static final String PRICE_UPDATE_INTEGRATION_XML_GENERATION_LOCATION = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.priceUpdateIntegrationXMLFileLocation");

	/**
	 * Price update Outbound Integration XML Location.
	 */
	public static final String PRICE_UPDATE_FEEDBACK_INTEGRATION_XML_GENERATION_LOCATION = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.priceUpdatefeedbackIntegrationXMLFileLocation");

	/*
	 * targetMUP
	 */
	public static final String TARGET_M_UP_KEY_SEPD_FPD = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.productSeasonAtts.targetMUp");

	/*
	 * targetMUP
	 */
	public static final String TARGET_M_UP_KEY_APD_ACCESSORIES = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.productSeasonAtts.targetMargin");
	/*
	 * targetPP
	 */
	public static final String TARGET_PP_KEY = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.productSeasonAtts.targetPP");
	/*
	 * Recomded retail price
	 */
	public static final String RR_PRU_KEY = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.productSeasonAtts.RRPru");

	public static final String PSL_MDM_ID = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.productSeasonAtts.MDMID");

	/**
	 *prices update Log Entry Link Type.
	 */
	public static final String LOG_ENTRY_INBOUND_PRICE_UPDATE_OBJECT_TYPE = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.logEntry.objectType");
	/**
	 *prices update Log Entry MDM ID.
	 */
	public static final String LOG_ENTRY_INBOUND_PRICE_UPDATE_MDM_ID = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.logEntry.mdmID");
	/**
	 *prices update Log Entry Link Details.
	 */
	public static final String LOG_ENTRY_INBOUND_OBJECT_NAME = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.logEntry.name");
	/**
	 *prices update Log Entry PLM ID.
	 */
	public static final String LOG_ENTRY_INBOUND_PRICE_UPDATE_PLM_ID = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.logEntry.plmID");
	/**
	 *prices update Log Entry Feedback Status.
	 */
	public static final String LOG_ENTRY_INBOUND_PRICE_UPDATE_FEEDBACK_STATUS = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.logEntry.feedbackStatus");
	/**
	 *prices update Log Entry Error Reason.
	 */
	public static final String LOG_ENTRY_INBOUND_PRICE_UPDATE_ERROR_REASON = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.logEntry.errorReason");
	/**
	 *prices update Log Entry Integration Status.
	 */
	public static final String LOG_ENTRY_INBOUND_PRICE_UPDATE_INTEGRATION_STATUS = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.logEntry.integartionStatus");
	/**
	 * prices update Log Entry Itteration Id.
	 */
	public static final String LOG_ENTRY_INBOUND_PRICE_UPDATE_ITTERATION_ID = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.logEntry.itterationid");

	/**
	 * Status Integrated.
	 */
	public static final String INTEGRATED = "INTEGRATED";
	/**
	 * Status Not Integrated.
	 */
	public static final String NOT_INTEGRATED = "NO_INTEGRATED";
	/**
	 * Status Pending.
	 */
	public static final String PENDING = "PENDING";

	/**
	 * RECEIVED_VALID_FEEDBACK.
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
	 * ************************************* Entries for Phase-13 BUILD changes
	 * *************************************
	 */
	/**
	 * TARGET_PURCHASE_PRICE.
	 */
	public static final String TARGET_PURCHASE_PRICE = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.productSeason.smTargetPP");
	/**
	 * TARGET_PP_CURRENCY.
	 */
	public static final String TARGET_PP_CURRENCY = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.productSeason.smTargetPPCurrency");
	/**
	 * PRODUCTION_REGION.
	 */
	public static final String PRODUCTION_REGION = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.productSeason.smProductionRegion");
	/**
	 * AP_RRP_GROSS_RUB.
	 */
	public static final String AP_RRP_GROSS_RUB = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.productSeason.smAPRRPGrossRUB");
	/**
	 * PLANNED_MUP.
	 */
	public static final String PLANNED_MUP = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.colorwaySeason.smPlannedMUP");
	/**
	 * RRP_GROSS_RUB.
	 */
	public static final String RRP_GROSS_RUB = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.inbound.priceUpdate.integration.colorwaySeason.smRRPGrossRUB");
}