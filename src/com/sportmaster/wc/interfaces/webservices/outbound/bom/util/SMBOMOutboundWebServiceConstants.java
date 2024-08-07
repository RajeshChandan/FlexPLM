package com.sportmaster.wc.interfaces.webservices.outbound.bom.util;

import com.lcs.wc.util.LCSProperties;

/**
 * @author BSC
 *
 */
public class SMBOMOutboundWebServiceConstants {

	/**
	 * Protected Constructor.
	 */
	protected SMBOMOutboundWebServiceConstants(){
		//constructor.
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////  BOM   OUTBOUND   INTEGRATION  CONSTANTS ////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * BOM queue start time.
	 */
	public static final String BOM_OUTBOUND_QUEUE_START_TIME=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.bomOutboundScheduleQueue.startTime");
	/**
	 * BOM queue start AM.
	 */
	public static final String BOM_OUTBOUND_QUEUE_START_AM=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.bomOutboundScheduleQueue.AMorPM");
	/**
	 * BOM queue time zone.
	 */
	public static final String BOM_OUTBOUND_QUEUE_TIME_ZONE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.bomOutboundScheduleQueue.timeZone");
	/**
	 * BOM queue interval in minutes.
	 */
	public static final long BOM_OUTBOUND_QUEUE_INTERVAL_IN_MINUTES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.bomOutboundScheduleQueue.scheduleInMinutes",60);
	/**
	 * BOM outbound schedule name.
	 */
	public static final String BOM_OUTBOUND_INTEGRATION_SCHEDULE_QUEUE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.bomOutboundScheduleQueue");
	/**
	 * BOM outbound processing name.
	 */
	public static final String BOM_OUTBOUND_INTEGRATION_PROCESSING_QUEUE=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.bomOutboundQueueProcessingQueue");
	/**
	 * Enable/Disable XML generation for outgoing Request.
	 */
	public static final boolean GENERATE_XML_FOR_RESPONSE = LCSProperties.getBoolean("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.request.generateXMLFileForRequest");
	
	/**
	 * BOM Outbound XML Location.
	 */
	public static final String BOM_OUTBOUND_XML_GENERATION_LOCATION = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.product.processor.bom.bomRequestXMLFileLocation");
	
	/**
	 * BOM XML type.
	 */
	public static final String BOM_XML_TYPE = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.processor.bom.bomXMLType");
	
	
	/**
	 * BOM Status
	 */
	public static final String BOM_STATUS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.processor.bom.bomStatus");	
	
	/**
	*businessSupplierMDMID
	*/
	public static final String BUSSINESS_SUPPLIER_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.businessSupplierMDMID");
	/**
	 * Ad Hoc PLM ID Colorway Season Link.
	 */
	public static final String AD_HOC_PLM_ID_SKU_SEASON_LINK = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.processor.bom.colorwaySeasonLink.adHocPLMIDSSL");
	
	/**
	 * Empty MDM ID.
	 */
	public static final String EMPTY_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.request.emptyMDMID");
	
	/**
	 * Spec_Status 
	 */
	public static final String SPEC_STATUS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.spec.SpecStatus");
	
	/**
	*SEASON_MDM_ID
	*/
	public static final String SEASON_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.seasonMDMID");
	
	/**
	*MATERIAL_SUPPLIER_MDM_ID
	*/
	public static final String MATERIAL_SUPPLIER_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.materialSUpplierMDMID");
	
	/**
	*COLORWAY_MDM_ID
	*/
	public static final String COLORWAY_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.colorwayMDMID");
	
	/**
	*COLORWAY_SEASON_MDM_ID
	*/
	public static final String COLORWAY_SEASON_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.colorwaySeasonMDMID");
	
	
	/**
	*Material_MDM_ID
	*/
	public static final String MATERIAL_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.materialMDMID");
	
	/**
	*COLOR_PROVIDER_CATALOG smProviderCatalog
	*/
	public static final String COLOR_PROVIDER_CATALOG = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.colorProviderCatalog");

	/**
	*METALLIC_COLOR_PROVIDER_CATALOG smProviderCatalog
	*/
	public static final String METALLIC_COLOR_PROVIDER_CATALOG = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.metalliccolorProviderCatalog");
	/**
	*COLOR_STANDARD_REF vrdColorStdRefNum
	*/
	public static final String SOLID_COLOR_STANDARD_REF = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.solidColorStandardRef");

	/**
	*COLOR_STANDARD_REF smColorStdRefNum
	*/
	public static final String COLOR_STANDARD_REF = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.colorStandardRef");
	
	/**
	*COLOR_ARTWORK vrdArtworkNum
	*/
	public static final String COLOR_ARTWORK = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.colorArtwork");
	
	

	/**
	 * Time out in minutes.
	 */
	public static final int BOM_OUTBOUND_INTEGRATION_TIMEOUT_IN_MINS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bomIntegration.webserviceTimeOutInMinutes", 3);
	
	/**
	 * Sourcing Configuration Business Supplier.
	 */
	public static final String SOURCING_CONFIGURATION_BUSINESS_SUPPLIER = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.processor.bom.sourcingConfiguration.businessSupplier");
	/**
	 * smPlmSupplierId
	 */
	public static final String BUSINESS_SUPPLIER_PLM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.processor.bom.sourcingConfiguration.businessSupplier.PLMID");
	/**
	 * Sourcing Configuration Business Supplier MDM ID.
	 */
	public static final String SOURCING_CONFIGURATION_BUSINESS_SUPPLIER_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.processor.bom.sourcingConfiguration.businessSupplierMDMID");
	/**
	 * Sourcing Configuration Status.
	 */
	public static final String SOURCING_CONFIGURATION_STATUS = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.processor.bom.sourcingConfiguration.sourceToSeason.sourcingStatus");
	/**
	 * Sourcing Configuration Factory.
	 */
	public static final String SOURCING_CONFIGURATION_FACTORY = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.processor.bom.sourcingConfiguration.factory");
	/**
	 * Sourcing Configuration Factory MDM ID.
	 */
	public static final String SOURCING_CONFIGURATION_FACTORY_MDM_ID = LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.processor.bom.sourcingConfiguration.associatedFactoryMDMID");
	
	/**
	 * Pending.
	 */
	public static final String LOG_ENTRY_PENDING = "PENDING";

	/**
	 * Processed.
	 */
	public static final String LOG_ENTRY_PROCESSED = "PROCESSED";

	/**
	 * Log Entry Request ID.
	 */
	public static final String BOM_LOG_ENTRY_REQUEST_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.integration.logEntry.requestID");
	/**
	 * Log Entry logEntryMDMDID .
	 */
	public static final String BOM_LOG_ENTRY_MDM_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.integration.logEntry.logEntryMDMDID");

	/**
	 * Log Entry objectDetails .
	 */
	public static final String BOM_LOG_ENTRY_OBJECT_DETAILS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.integration.logEntry.objectDetails");
	/**
	 * Log Entry plmID .
	 */
	public static final String BOM_LOG_ENTRY_PLM_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.integration.logEntry.plmID");
	/**
	 * Log Entry integrationStatus
	 */
	public static final String BOM_LOG_ENTRY_INTEGRATION_STATUS=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.integration.logEntry.integrationStatus");
	/**
	 * Log Entry errorReason.
	 */
	public static final String BOM_LOG_ENTRY_ERROR_REASON=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.integration.logEntry.errorReason");
	/**
	 * Log Entry lifeCycleStatus
	 */
	public static final String BOM_LOG_ENTRY_LIFECYCLE_STATES=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.integration.logEntry.lifeCycleStatus");
	/**
	 * Log Entry bomOutboundLogEntryPath
	 */
	public static final String LOG_ENTRY_BOM_OUT_BOUND_PATH=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.integration.logEntry.bomOutboundLogEntryPath");
	
	/**
	 * Log Entry initialRequestID
	 */
	public static final String LOG_ENTRY_BOM_OUTBOUND_INTEGRATION_INITIAL_REQUEST_ID=LCSProperties.get("com.sportmaster.wc.interfaces.webservices.outbound.bom.integration.logEntry.initialRequestID");


}
