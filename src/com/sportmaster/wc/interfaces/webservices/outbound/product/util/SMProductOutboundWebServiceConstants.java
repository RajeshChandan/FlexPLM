package com.sportmaster.wc.interfaces.webservices.outbound.product.util;

import com.lcs.wc.util.LCSProperties;

/**
 * @author BSC
 *
 */
public class SMProductOutboundWebServiceConstants {

	/**
	 * Protected Constructor.
	 */
	protected SMProductOutboundWebServiceConstants(){
		//constructor.
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////  PRODUCT   OUTBOUND   INTEGRATION  CONSTANTS ////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Product Object MDM ID.
	 */
	public static final String PRODUCT_MDM_ID_KEY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.MDMID");
	/**
	 * Dummy MDM ID for First Request.
	 */
	public static final String FAKE_MDM_ID = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.request.dummyMDMID");
	/**
	 * Empty MDM ID.
	 */
	public static final String EMPTY_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.request.emptyMDMID");
	/**
	 * Season MDM ID.
	 */
	public static final String SEASON_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.seasonMDMID");
	/**
	 * active departments for integration.
	 */
	public static final String ACTIVE_DEPARTMENTS_FOR_INTEGRATION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.activeDepartmentsForIntegration");
	/**
	 * Product queue start time.
	 */
	public static final String PRODUCT_SEASON_OUTBOUND_QUEUE_START_TIME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.productOutboundScheduleQueue.startTime");
	/**
	 * Product queue start AM.
	 */
	public static final String PRODUCT_SEASON_OUTBOUND_QUEUE_START_AM=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.productOutboundScheduleQueue.AMorPM");
	/**
	 * Product queue time zone.
	 */
	public static final String PRODUCT_SEASON_OUTBOUND_QUEUE_TIME_ZONE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.productOutboundScheduleQueue.timeZone");
	/**
	 * Product queue interval in minutes.
	 */
	public static final long PRODUCT_SEASON_OUTBOUND_QUEUE_INTERVAL_IN_MINUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.productOutboundScheduleQueue.scheduleInMinutes",60);
	/**
	 * product outbound schedule name.
	 */
	public static final String PRODUCT_OUTBOUND_INTEGRATION_SCHEDULE_QUEUE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.productOutboundQueueScheduleQueue");
	/**
	 * product outbound processing name.
	 */
	public static final String PRODUCT_OUTBOUND_INTEGRATION_PROCESSING_QUEUE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.productOutboundQueueProcessingQueue");
	/**
	 * Enable/Disable XML generation for outgoing Request.
	 */
	public static final boolean GENERATE_XML_FOR_RESPONSE = LCSProperties.getBoolean("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.request.generateXMLFileForRequest");
	/**
	 * Colorway MDM ID.
	 */
	public static final String COLORWAY_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.colorway.MDMID");
	/**
	 * Product Season Link MDM ID.
	 */
	public static final String PRODUCT_SEASON_LINK_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.productSeasonLink.MDMID");
	/**
	 * Colorway Season Link MDM ID.
	 */
	public static final String COLORWAY_SEASON_LINK_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.colorwaySeasonLink.MDMID");
	/**
	 * Product Outbound XML Location.
	 */
	public static final String PRODUCT_OUTBOUND_XML_GENERATION_LOCATION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productRequestXMLFileLocation");
	/**
	 * Colorway Outbound XML Location.
	 */
	public static final String COLORWAY_OUTBOUND_XML_GENERATION_LOCATION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.colorwayRequestXMLFileLocation");
	/**
	 * Product Season Outbound Link XML Location.
	 */
	public static final String PRODUCT_SEASON_LINK_OUTBOUND_XML_GENERATION_LOCATION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLinkRequestXMLFileLocation");
	/**
	 * Colorway Season Outbound XML Location.
	 */
	public static final String COLORWAY_SEASON_LINK_OUTBOUND_XML_GENERATION_LOCATION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.colorwaySeasonLinkRequestXMLFileLocation");
	/**
	 * Product XML type.
	 */
	public static final String PRODUCT_XML_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productXMLType");
	/**
	 * Colorway XML Type.
	 */
	public static final String COLORWAY_XML_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.colorwayXMLType");
	/**
	 * Product Season Link XML Type.
	 */
	public static final String PRODUCT_SEASON_LINK_XML_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonXMLType");
	/**
	 * Colorway Season Link XML Type.
	 */
	public static final String COLORWAY_SEASON_LINK_XML_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.colorwaySeasonXMLType");
	/**
	 * Time out in minutes.
	 */
	public static final int PRODUCT_OUTBOUND_INTEGRATION_TIMEOUT_IN_MINS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.webserviceTimeOutInMinutes", 3);
	/**
	 * Product Macrobrand.
	 */
	public static final String PRODUCT_MACROBRAND = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.macroBrand");
	/**
	 * Product Macrobrand MDM ID.
	 */
	public static final String PRODUCT_MACROBRAND_MDMID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.macroBrandMDMID");
	/**
	 * Product Brand.
	 */
	public static final String PRODUCT_BRAND = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.brand");
	/**
	 * Product Style Name RU.
	 */
	public static final String PRODUCT_STYLE_NAME_RU = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.styleNameRU");
	/**
	 * Product Description.
	 */
	public static final String PRODUCT_DESCRIPTION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productDescription");
	/**
	 * Product Style Code.
	 */
	public static final String PRODUCT_STYLE_CODE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.styleCode");
	/**
	 * Product Age.
	 */
	public static final String PRODUCT_AGE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.age");
	/**
	 * Product Status.
	 */
	public static final String STATUS_STYLE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.statusStyle");
	/**
	 * Colorway Status.
	 */
	public static final String COLORWAY_STATUS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.colorway.colorwayStatus");
	/**
	 * Product Number
	 */
	public static final String PRODUCT_NUMBER = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productNumber");
	/**
	 * Product Style Analogue.
	 */
	public static final String PRODUCT_STYLE_ANALOGUE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.styleAnalogue");
	/**
	 * Product Gender.
	 */
	public static final String PRODUCT_GENDER = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.gender");
	/**
	 * Product Fabric Group.
	 */
	public static final String PRODUCT_FABRIC_GROUP = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.fabricGroup");
	/**
	 * Product Sub Division.
	 */
	public static final String PRODUCT_SUB_DIVISION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.subDivision");
	/**
	 * Product Sub division MDM ID.
	 */
	public static final String PRODUCT_SUB_DIVISION_MDMID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.subDivisionMDMID");
	/**
	 * Product Fit.
	 */
	public static final String PRODUCT_FIT = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.fit");
	/**
	 * Product Thumbnail URL.
	 */
	public static final String PRODUCT_THUMBNAIL_URL = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productThumbnailURL");
	/**
	 * Colorway Safety Standard.
	 */
	public static final String COLORWAY_SAFETY_STANDARD = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.colorway.safetyStandard");
	/**
	 * Colorway Safety Code.
	 */
	public static final String COLORWAY_SAFETY_CODE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.colorway.safetyCode");
	/**
	 * Colorway Safety Code Object Name.
	 */
	public static final String COLORWAY_SAFETY_CODE_NAME = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.colorway.safetyCodeName");
	/**
	 * Colorway Color MDM ID.
	 */
	public static final String COLORWAY_COLOR_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.colorway.colorMDMID");
	/**
	 * Colorway Color object refernce.
	 */
	public static final String COLORWAY_COLOR = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.colorway.colorObjectReference");
	/**
	 * Colorway Safety Standard MDM ID.
	 */
	public static final String COLORWAY_SAFETY_STANDARD_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.colorway.safetyStandardMDMID");
	/**
	 * Product Season Link Retail Destination.
	 */
	public static final String PRODUCT_SEASON_LINK_RETAIL_DESTINATION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.retailDestinationSync");
	/**
	 * Product Season Link Target Purchase price.
	 */
	public static final String PRODUCT_SEASON_LINK_TARGET_PURCHASE_PRICE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.targetPurchaseprice");
	/**
	 * Product Season Link Planned Colorways.
	 */
	public static final String PRODUCT_SEASON_LINK_PLANNED_COLORWAYS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.plannedColorways");
	/**
	 * Product Season Link Brand Manager.
	 */
	public static final String PRODUCT_SEASON_LINK_BRAND_MANAGER = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.brandManager");
	/**
	 * Product Season Link Capsule.
	 */
	public static final String PRODUCT_SEASON_LINK_CAPSULE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.capsule");
	/**
	 * Product Season Link Category manager.
	 */
	public static final String PRODUCT_SEASON_LINK_CATEGORY_MANAGER = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.categoryManager");
	/**
	 * Product Season Link Production Manager.
	 */
	public static final String PRODUCT_SEASON_LINK_PRODUCTION_MANAGER = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.productionManager");
	/**
	 * Product Season Satus.
	 */
	public static final String STATUS_STYLE_SEASONAL = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.statusStyleSeasonal");
	/**
	 * Product Season Link Sales/Newness.
	 */
	public static final String PRODUCT_SEASON_LINK_SALES_NEWNESS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.salesNewness");
	/**
	 * Product Season Link Development Newness.
	 */
	public static final String PRODUCT_SEASON_LINK_DEVELOPMENT_NEWNESS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.developmentNewness");
	/**
	 * Product Season Link Retail Destination.
	 */
	public static final String PRODUCT_SEASON_LINK_PRODUCTION_GROUP = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.productionGroup");
	/**
	 * Product Season Link Commercial Size China.
	 */
	public static final String PRODUCT_SEASON_LINK_COMMERCIAL_SIZES_CHINA = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.commercialSizesChina");
	/**
	 * Product Season Link Commercial Size Russia.
	 */
	public static final String PRODUCT_SEASON_LINK_COMMERCIAL_SIZES_RUSSIA = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.commercialSizesRussia");
	/**
	 * commercial size SIZE value.
	 */
	public static final String PRODUCT_SEASON_LINK_COMMERCIAL_SIZES_SIZE_VALUE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.commercialSizes.sizes");
	/**
	 * Product Season Link Commercial Sizes Master Scale.
	 */
	public static final String PRODUCT_SEASON_LINK_COMMERCIAL_SIZE_MASTER_SCALE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.commercialSizesMasterScale");
	/**
	 * Product Season Link RRP China
	 */
	public static final String PRODUCT_SEASON_LINK_RRP_CHINA = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.recommendedRetailPriceStyleRMB");
	/**
	 * Product Season Link RRP Russia.
	 */
	public static final String PRODUCT_SEASON_LINK_RRP_RUSSIA = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.recommendedRetailPriceStyle");
	/**
	 * Product Season Link Initial FC Style.
	 */
	public static final String PRODUCT_SEASON_LINK_INITIAL_FC_STYLE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.initialFCStyle");
	/**
	 * Colorway Season Link MDM ID.
	 */
	public static final String COLORWAY_SEASON_LINK_MDMID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.colorwaySeasonLink.MDMID");
	/**
	 * Colorway Season Link Retail Destination.
	 */
	public static final String COLORWAY_SEASON_LINK_RETAIL_DESTINATION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.colorwaySeasonLink.retailDestinationSync");
	/**
	 * Colorway Season Link Comments-CW.
	 */
	public static final String COLORWAY_SEASON_LINK_COMMENTS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.colorwaySeasonLink.smCommentsColorway");
	/**
	 * Colorway Season Link Flow.
	 */
	public static final String COLORWAY_SEASON_LINK_FLOW = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.colorwaySeasonLink.flow");
	/**
	 * Colorway Season Link manual high priority.
	 */
	public static final String COLORWAY_SEASON_LINK_MANUAL_HIGH_PRIORITY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.colorwaySeasonLink.manualHighPriority");
	/**
	 * Colorway Season Link Colorway Season Status.
	 */
	public static final String COLORWAY_SEASON_LINK_COLORWAY_SEASON_STATUS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.colorwaySeasonLink.colorwaySeasonStatus");
	/**
	 * Colorway Season Link LLT.
	 */
	public static final String COLORWAY_SEASON_LINK_LONG_LEAD_TIME = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.colorwaySeasonLink.longLeadTime");
	/**
	 * Sourcing Configuration Destination.
	 */
	public static final String SOURCING_CONFIGURATION_DESTINATION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.sourcingConfiguration.orderDestination");
	/**
	 * Sourcing Configuration Business Supplier.
	 */
	public static final String SOURCING_CONFIGURATION_BUSINESS_SUPPLIER = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.sourcingConfiguration.businessSupplier");
	/**
	 * Sourcing Configuration Business Supplier MDM ID.
	 */
	public static final String SOURCING_CONFIGURATION_BUSINESS_SUPPLIER_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.sourcingConfiguration.businessSupplierMDMID");
	/**
	 * Sourcing Configuration Status.
	 */
	public static final String SOURCING_CONFIGURATION_STATUS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.sourcingConfiguration.sourceToSeason.sourcingStatus");
	/**
	 * Sourcing Configuration Factory.
	 */
	public static final String SOURCING_CONFIGURATION_FACTORY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.sourcingConfiguration.factory");
	/**
	 * Sourcing Configuration Factory MDM ID.
	 */
	public static final String SOURCING_CONFIGURATION_FACTORY_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.sourcingConfiguration.associatedFactoryMDMID");
	/**
	 * Cost Sheet Quoted Price GS Supplier Currency.
	 */
	public static final String COST_SHEET_QUOTED_PRICE_GS_SUPPLIER_CURRENCY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.quotedPriceGSSupplierCurrency");
	/**
	 * Cost Sheet FOB Total Int MAnual GS Currency.
	 */
	public static final String COST_SHEET_FOB_TOTAL_INT_MANUAL_GS_CURRENCY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.fobTotalIntManualGSCurrency");
	/**
	 * Cost Sheet Contract Currency.
	 */
	public static final String COST_SHEET_CONTRACT_CURRENCY= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.contractCurrency");
	/**
	 * Cost Sheet Incoterms.
	 */
	public static final String COST_SHEET_INCOTERMS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.incoterms");
	/**
	 * Cost Sheet Costing Stage.
	 */
	public static final String COST_SHEET_COSTING_STAGE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.costingStage");
	/**
	 * Cost Sheet Status.
	 */
	public static final String COST_SHEET_STATUS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.costSheetStatus");
	/**
	 * Update Pending.
	 */
	public static final String LOG_ENTRY_UPDATE_PENDING = "UPDATE_PENDING";
	/**
	 * CREATE Pending.
	 */
	public static final String LOG_ENTRY_CREATE_PENDING = "CREATE_PENDING";
	/**
	 * Update Processed.
	 */
	public static final String LOG_ENTRY_UPDATE_PROCESSED = "UPDATE_PROCESSED";
	/**
	 * Create processed.
	 */
	public static final String LOG_ENTRY_CREATE_PROCESSED = "CREATE_PROCESSED";
	/**
	 * Cancelled Pending.
	 */
	public static final String LOG_ENTRY_CANCELLED_PENDING = "CANCELLED_PENDING";
	/**
	 * Cancelled Processed.
	 */
	public static final String LOG_ENTRY_CANCELLED_PROCESSED = "CANCELLED_PROCESSED";
	/**
	 * Object Missing.
	 */
	public static final String LOG_ENTRY_OBJECT_MISSING = "OBJECT_MISSING";
	/**
	 * Log Entry Request ID.
	 */
	public static final String LOG_ENTRY_REQUEST_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.integration.logEntry.requestID");
	/**
	 * Log Entry Request ID.
	 */
	public static final String LOG_ENTRY_MDM_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.integration.logEntry.logEntryMDMDID");

	/**
	 * Log Entry Request ID.
	 */
	public static final String LOG_ENTRY_OBJECT_DETAILS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.integration.logEntry.objectDetails");
	/**
	 * Log Entry Request ID.
	 */
	public static final String LOG_ENTRY_PLM_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.integration.logEntry.plmID");
	/**
	 * Log Entry Request ID.
	 */
	public static final String LOG_ENTRY_INTEGRATION_STATUS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.integration.logEntry.integrationStatus");
	/**
	 * Log Entry Request ID.
	 */
	public static final String LOG_ENTRY_ERROR_REASON=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.integration.logEntry.errorReason");
	/**
	 * Log Entry Request ID.
	 */
	public static final String LOG_ENTRY_LIFECYCLE_STATES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.integration.logEntry.lifeCycleStatus");
	/**
	 * Log Entry Request ID.
	 */
	public static final String LOG_ENTRY_PRODUCT_OUT_BOUND_PATH=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.integration.logEntry.productOutboundLogEntryPath");
	/**
	 * Log Entry Request ID.
	 */
	public static final String LOG_ENTRY_COLORWAY_OUTBOUND_PATH=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.integration.logEntry.colorwayOutboundLogEntryPath");
	/**
	 * Log Entry Request ID.
	 */
	public static final String LOG_ENTRY_PRODUCT_SEASON_OUTBOUND_PATH=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.integration.logEntry.productSeasonOutboundLogEntryPath");
	/**
	 * Log Entry Request ID.
	 */
	public static final String LOG_ENTRY_COLORWAY_SEASON_OUTBOUND_PATH=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.integration.logEntry.colorwaySeasonOutboundLogEntryPath");
	/**
	 * Log Entry Request ID.
	 */
	public static final String LOG_ENTRY_PRODUCT_OUTBOUND_INTEGRATION_INITIAL_REQUEST_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.integration.logEntry.initialRequestID");
	/**
	 * Create failed request.
	 */
	public static final String CREATE_FAILED_REQUEST = "CREATE_FAILED";
	/**
	 * Create success request.
	 */
	public static final String CREATE_SUCCESS_REQUEST = "CREATE_SUCCESS";
	/**
	 * Update request.
	 */
	public static final String UPDATE_REQUEST = "UPDATE";
	/**
	 * Update Failed request.
	 */
	public static final String UPDATE_FAILED = "UPDATE_FAILED";
	/**
	 * Update Successful request.
	 */
	public static final String UPDATE_SUCCESS = "UPDATE_SUCCESS";
	/**
	 * Lifecycle update request.
	 */
	public static final String LIFECYCLE_UPDATE_REQUEST = "LIFECYCLE_UPDATE";
	/**
	 * Cancelled request.
	 */
	public static final String CANCELLED_REQUEST = "CANCELLED_REQUEST";
	/**
	 * Cancelled success.
	 */
	public static final String CANCELLED_SUCCESS = "CANCELLED_SUCCESS";
	/**
	 * 1st Forecast RU.
	 */
	public static final String FIRST_FORECAST_RU = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.lifecyclestate.firstForecastRU");
	/**
	 * 1st Forecast CN.
	 */
	public static final String FIRST_FORECAST_CN = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.lifecyclestate.firstForecastCN");
	/**
	 * 2nd Forecast.
	 */
	public static final String SECOND_FORECAST = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.lifecyclestate.secondForecast");
	/**
	 * Allocation.
	 */
	public static final String ALLOCATION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.lifecyclestate.allocation");
	/**
	 * AP - Approval.
	 */
	public static final String AP_APPROVAL = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.lifecyclestate.apApproval");
	/**
	 * Order.
	 */
	public static final String ORDER = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.lifecyclestate.order");
	/**
	 * Cancelled.
	 */
	public static final String CANCELLED = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.lifecyclestate.cancelled");
	/**
	 * In work.
	 */
	public static final String IN_WORK = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.lifecyclestate.inWork");
	/**
	 * Approved cost Sheet.
	 */
	public static final String COSTSHEET_APPROVED = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.approvedStatus");
	/**
	 * LR costing stage.
	 */
	public static final String LR_COSTING_STAGE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.lrCostingStage");
	/**
	 * LC Costing stage.
	 */
	public static final String LC_COSTING_STAGE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.lcCostingStage");
	/**
	 * Final costing stage.
	 */
	public static final String FINAL_COSTING_STAGE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.finalCostingStage");
	/**
	 * Ad Hoc PLM ID Product Season Link.
	 *
	 */
	public static final String AD_HOC_PLM_ID_PRODUCT_SEASON_LINK = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.sdHocPLMIDPSL");
	/**
	 * Ad Hoc PLM ID Colorway Season Link.
	 */
	public static final String AD_HOC_PLM_ID_COLORWAY_SEASON_LINK = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.colorwaySeasonLink.adHocPLMIDSSL");
	/**
	 * Project (Only APD).
	 */
	public static final String PRODUCT_PROJECT = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.project");
	/**
	 * Product Technologist.
	 */
	public static final String PRODUCT_SEASON_LINK_TECHNOLOGIST = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.technologist");
	/**
	 * ***************************************************************************************************************************************
	 *                                             New Attributes for phase 4 changes
	 * ***************************************************************************************************************************************
	 */

	/**
	 * TP Development.
	 */
	public static final String TP_DEVELOPMENT = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.lifecyclestate.tpDevelopment");
	/**
	 * Line Close.
	 */
	public static final String LINE_CLOSE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.lifecyclestate.lineClose");

	public static final String IS_COPY_PRODUCT=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.smisCopyProduct");

	/**
	 * ********************************
	 * Entires for Phase-4 FPD changes
	 * ********************************
	 */
	public static final String FIRST_SAMPLE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.firstSample");
	public static final String SECOND_SAMPLE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.secondSample");
	public static final String CFMS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.CFMS");
	public static final String INTIAL_FORECAST_CW=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.colorwaySeasonLink.InitialForecastCW");

	/**
	 * ********************************
	 * Entires for Phase-7 changes
	 * ********************************
	 */
	//added for cr 331
	public static final String APD_ACC_COSTING_STAGES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.APDACCCostingStages");
	public static final String EARLY_BUY=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.colorwaySeasonLink.earlyBuy");
	public static final String DEVELOPMENT_CATEGORY=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.productSeasonLink.DevelopmentCategoryNew");
	public static final String FPD_COSTING_STAGES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.FPDCostingStages");

	/**
	 * ********************************
	 * Entires for Phase-8(3.8.0.0) changes
	 * ********************************
	 */

	public static final String SEPD_DEVELOPMENT_NEWNESS =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.colorwayseasonlink.smTechnicalNewness");
	public static final String PLANNED_ASSESMBLY_TYPE= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.colorwayseasonlink.smPlannedAssemblyType");
	public static final String SPECIFIC_PRODUCT= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.smSpecificOfProduct");
	public static final String TYPE_OF_ASSEMBLING= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.costsheet.smTypeOfAssembling");
	public static final String DEVELOPMENT_GROUP=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productseasonlink.smDevelopmentGroup");
	public static final String PROTOTYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productseasonlink.smPrototype");
	public static final String FASTTRACK=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productseasonlink.fastTrack");
	public static final String NOS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productseasonlink.nos");
	public static final String PRODUCT_PLANNED_ASSEMBLY_TYPE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productseasonlink.smProductPlannedAssemblyType");
	/**
	 * Cost Sheet Costing Stage SEPD.
	 */
	public static final String SEPD_COSTING_STAGE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.costsheet.costingStage");
	/**
	 * Cost Sheet PURCHASE_PRICE_CALC_CONTRACT_CURENCY.
	 */
	public static final String PURCHASE_PRICE_CALC_CONTRACT_CURENCY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.costsheet.PurchasePriceCalcContractCurency");
	/**
	 * Cost Sheet PRICING_COUNTRY.
	 */
	public static final String PRICING_COUNTRY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.costsheet.pricingCountry");
	/**
	 * Cost Sheet PRICING_COUNTRY.
	 */
	public static final String PRICING_COUNTRY_MDMID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.costsheet.pricingCountry.MDMID");
	/**
	 * Sourcing Configuration Business Supplier MDM ID.
	 */
	public static final String RFQ_VENDOR_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.costsheet.RFQVenodr.MDMID");


	/**
	 * cancel request queue name.
	 */
	public static final String CANCEL_OUTBOUND_QUEUE_NAME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.cancelRequest.scheduleQueueName");
	/**
	 * Product queue start time.
	 */
	public static final String CANCEL_OUTBOUND_QUEUE_START_TIME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.cancelRequest.startTime");
	/**
	 * Product queue start AM.
	 */
	public static final String CANCEL_OUTBOUND_QUEUE_START_AM=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.cancelRequest.AMorPM");
	/**
	 * Product queue interval in minutes.
	 */
	public static final long CANCEL_OUTBOUND_QUEUE_INTERVAL_IN_MINUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productIntegration.cancelRequest.intervalInMinutes",60);
	/**
	 * Cost Sheet PRICING_COUNTRY.
	 */
	public static final String TO_INTEGRATE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productseasonlink.toIntegate");

	/**
	 * ********************************
	 * Entires for Phase-8(3.8.2.0) HOT-FIX changes
	 * ********************************
	 */
	/**
	 * Colorway season link MKTG_IMAGE_STYLE.
	 */
	public static final String MKTG_IMAGE_STYLE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.colorwaySeasonLink.MktgImageStyle");

	/**
	 * ********************************
	 * Entires for Phase-9(3.9.0.0) BUILD changes
	 * ********************************
	 */
	public static final String SPECIFIC_PRODUCT_NEW= LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.smSpecificOfProductNew");


	/**
	 * ********************************
	 * Entires for Phase-9(3.9.1.0) BUILD changes
	 * ********************************
	 */
	public static final String FASTTRACK_OTHERS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.productseasonlink.fastTrackOthers");
	/**
	 * Cost Sheet Status.
	 */
	public static final String ACC_COST_SHEET_STATUS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.product.costsheet.smCSStatusSEPD");

	/**
	 * ********************************
	 * Entires for Phase-9(3.9.2.0) BUILD changes
	 * ********************************
	 */

	//FPD_DEVELOPMENT_NEWNESS
	public static final String FPD_DEVELOPMENT_NEWNESS =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.colorwayseasonlink.smFPDDevelopmentNewness");

	//ACC_DEVELOPMENT_NEWNESS
	public static final String ACC_DEVELOPMENT_NEWNESS =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.colorwayseasonlink.smAccDevelopmentNewness");

	/**
	 * ********************************
	 * Entries for Phase-12(3.12.0.0) BUILD changes
	 * ********************************
	 */
	//MULTI_COST_SHEET_STATUS
	public static final String MULTI_COST_SHEET_STATUS =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.costsheet.smCostSheetStatusFPD");
	//MULTI_COSTING_STAGE
	public static final String MULTI_COSTING_STAGE =LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.costsheet.smCostingStageFPD");

	/**
	 * *************************************
	 * Entries for Phase-13 BUILD changes
	 * *************************************
	 */
	// SM_SOURCING
	public static final String SM_SOURCING = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.productseasonlink.smSourcing");

	// SM_HIGH_PRIORITY
	public static final String SM_HIGH_PRIORITY = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.colorwaySeasonLink.smHighPriority");

	// PSL_BLOCKED
	public static final String PSL_BLOCKED = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.productseasonlink.smPSBlocked");
	// PSL_UPPER
	public static final String PSL_UPPER = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.productseasonlink.smPSUpper");
	// PSL_LINNING
	public static final String PSL_LINNING = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.productseasonlink.smPSLining");
	// PSL_SOLE
	public static final String PSL_SOLE = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.productseasonlink.smPSSole");
	
	//-- PRODUCT_TRIGGERED_BY
	public static final String PRODUCT_TRIGGERED_BY = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.product.smPTriggeredBy");
	//-- PRODUCT_TRIGGERED_ON
	public static final String PRODUCT_TRIGGERED_ON = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.product.smPTriggeredOn");
	//-- PRODSEASON_TRIGGERED_BY
	public static final String PRODSEASON_TRIGGERED_BY = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.prodSeason.smPSTriggeredBy");
	//-- PRODSEASON_TRIGGERED_ON
	public static final String PRODSEASON_TRIGGERED_ON = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.prodSeason.smPSTriggeredOn");
	//-- SKU_TRIGGERED_BY
	public static final String SKU_TRIGGERED_BY = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.sku.smCWTriggeredBy");
	//-- SKU_TRIGGERED_ON
	public static final String SKU_TRIGGERED_ON = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.sku.smCWTriggeredOn");
	//-- SKUSEASON_TRIGGERED_BY
	public static final String SKUSEASON_TRIGGERED_BY = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.sku.smCWSTriggeredBy");
	//-- SKUSEASON_TRIGGERED_ON
	public static final String SKUSEASON_TRIGGERED_ON = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.sku.smCWSTriggeredOn");
	//-- PRODSEASON_DESIGN_BRIEF
	public static final String PRODSEASON_DESIGN_BRIEF = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.prodSeason.smPSDesignBrief");
	//-- PSD_MDM_ID_KEY
	public static final String PSD_MDM_ID_KEY = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.productSizeDefinition.smMDMSIZ");
	//-- CS_MCAPP_COSTING_STAGE_KEY
	public static final String CS_MCAPP_COSTING_STAGE_KEY = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.costsheet.MCAPP.costingStage");
	//-- CS_MCAPP_COSTING_STATUS_KEY
	public static final String CS_MCAPP_COSTING_STATUS_KEY = LCSProperties
			.get("com.sportmaster.wc.interfaces.webservices.outbound.costsheet.MCAPP.costingStatus");
}
