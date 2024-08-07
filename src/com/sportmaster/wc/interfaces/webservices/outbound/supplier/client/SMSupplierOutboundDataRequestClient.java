package com.sportmaster.wc.interfaces.webservices.outbound.supplier.client;

import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.bean.BusinessSupplierInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.BusinessSupplierInformationUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.bean.FactoryInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.FactoryInformationUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.bean.MaterialSupplierInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.MaterialSupplierInformationUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.bean.PlmEndpointService;
import com.sportmaster.wc.interfaces.webservices.bean.PlmWS;
import com.sportmaster.wc.interfaces.webservices.bean.SQLException_Exception;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.helper.SMSupplierHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor.SMSupplierDataProcessing;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor.SMSupplierLogEntryProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.util.SMSupplierRequestXMLGeneration;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.util.SMSupplierUtil;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;
import com.sun.xml.ws.client.BindingProviderProperties;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * 
 * @author 'true' ITC_Infotech.
 *
 */

public class SMSupplierOutboundDataRequestClient {

	private static final String ERROR_OCCURED = "ERROR OCCURED :-";
	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSupplierOutboundDataRequestClient.class);
	/**
	 * Supplier Request ID.
	 */
	private static int supplierRequestID;
	/**
	 * Storing MDM ID in response.
	 */
	private static String responseMDMID;
	/**
	 * Storing Response Integration Status.
	 */
	private static boolean responseIntegrationStatus;
	/**
	 * Flag to check if Factory is associated with Business Supplier.
	 */
	private static boolean factoryAssociatedToBusinessSupplier;
	/**
	 * Declaring Error Message received in response.
	 */
	private static String responseErrorReason;
	/**
	 * Storing total number of failed update count.
	 */
	private static int failedUpdateCount;
	/**
	 * Declaring flag for failed count during exception.
	 */
	private static boolean failedCountDueToException;
	/**
	 * Declaring Flag for Create.
	 */
	private static boolean isCreate;
	/**
	 * Constructor
	 */
	protected SMSupplierOutboundDataRequestClient(){
		//protected Constructor
	}

	/**
	 * Trigger Web Service Request.
	 * @param supplierObj - LCSSupplier
	 * @param supplierObjectIDMap - Map<String, String>
	 * @throws SQLException_Exception 
	 */
	public static void supplierDataRequest(LCSSupplier supplierObj, Map<String, String> supplierObjectIDMap) {
		Object obj = null;
		initializeValues();
		LOGGER.debug("FAILED COUNT @@  >>>>>>>>   "+getFailedUpdateCount() + "failed count due to exception FLAG "+isFailedCountDueToException());
		try{
			LOGGER.debug("###################  STARTING SUPPLIER OUTBOUND WEB CLIENT   ######################");
			//Starting web service
			PlmEndpointService plmEndPointServiceObj = new PlmEndpointService();
			PlmWS wS = plmEndPointServiceObj.getPlmEndpointPort();

			obj = findSupplierType(supplierObj, obj, wS);
		}catch(ClientTransportException clientExc){
			setResponseErrorReason(clientExc.getLocalizedMessage());
			updateFailedCount();
			LOGGER.debug("Failed count incremented value>>>>>>>   " + getFailedUpdateCount());
			setFailedCountDueToException(true);
			LOGGER.error(SMOutboundWebServiceConstants.OUTBOUND_CONNECTIVITY_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE
					+ SMOutboundWebServiceConstants.SUPPLIER_OUTBOUND_SCHEDULE_QUEUE_NAME
					+ SMInboundWebserviceConstants.CLIENT_TRANSPORT_ERROR_MESSAGE + clientExc.getLocalizedMessage());
			LOGGER.error(ERROR_OCCURED, clientExc);
		}catch (ServerSOAPFaultException serverSoapEXP) {
			updateFailedCount();
			LOGGER.debug("Failed count incremented  @@@@ >>>>>>>   " + getFailedUpdateCount());
			setFailedCountDueToException(true);
			setResponseErrorReason(serverSoapEXP.getLocalizedMessage());
			LOGGER.error(SMOutboundWebServiceConstants.OUTBOUND_SCHEMA_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_SCHEMA_ERROR_MESSAGE
					+ SMOutboundWebServiceConstants.SUPPLIER_OUTBOUND_SCHEDULE_QUEUE_NAME
					+ SMInboundWebserviceConstants.SERVER_SOAP_FAULT_ERROR_MESSAGE + serverSoapEXP.getLocalizedMessage());
			LOGGER.error(ERROR_OCCURED, serverSoapEXP);
		}catch(SOAPFaultException soapExp){
			setResponseErrorReason(soapExp.getLocalizedMessage());
			updateFailedCount();
			LOGGER.debug("Failed count incremented @@@@@ >>>>>>>   " + getFailedUpdateCount());
			setFailedCountDueToException(true);
			LOGGER.error(SMOutboundWebServiceConstants.OUTBOUND_CONNECTIVITY_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE
					+ SMOutboundWebServiceConstants.SUPPLIER_OUTBOUND_SCHEDULE_QUEUE_NAME
					+ SMInboundWebserviceConstants.SOAP_FAULT_ERROR_MESSAGE + soapExp.getLocalizedMessage());
			LOGGER.error(ERROR_OCCURED, soapExp);
		}catch(WebServiceException webSrcvExcp){
			setResponseErrorReason(webSrcvExcp.getLocalizedMessage());
			updateFailedCount();
			LOGGER.debug("Failed count incremented  @@@@@ >>>>>>>   " + getFailedUpdateCount());
			setFailedCountDueToException(true);
			LOGGER.error(SMOutboundWebServiceConstants.OUTBOUND_RESPONSE_TIMEOUT_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE
					+ SMOutboundWebServiceConstants.SUPPLIER_OUTBOUND_SCHEDULE_QUEUE_NAME
					+ SMInboundWebserviceConstants.WEB_SERVICE_TIMEOUT_ERROR_MESSAGE + webSrcvExcp.getLocalizedMessage());
			LOGGER.error(ERROR_OCCURED, webSrcvExcp);
		}catch(SQLException_Exception sqlExcp){
			updateFailedCount();
			LOGGER.debug("Failed count incremented  @@@@@ >>>>>>>   " + getFailedUpdateCount());
			setFailedCountDueToException(true);
			LOGGER.error(ERROR_OCCURED, sqlExcp);
		}catch(WTPropertyVetoException exp){
			LOGGER.error(ERROR_OCCURED, exp);
		}catch(WTException expt){
			LOGGER.error(ERROR_OCCURED, expt);
		}
		
		processResponse(obj, isCreate(), supplierObj, supplierObjectIDMap);
		
		
	}

	/**
	 * Sets initial values for attributes.
	 */
	public static void initializeValues() {
		setResponseErrorReason("");
		setResponseMDMID("");
		setResponseIntegrationStatus(false);
		setCreate(false);
		setFailedCountDueToException(false);
	}

	/**
	 * Find Supplier Type before request.
	 * @param supplierObj - LCSSupplier
	 * @param wS - PlmWS
	 * @param obj - Object
	 * @return obj - Object
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws SQLException_Exception - SQLException_Exception
	 */
	public static Object findSupplierType(LCSSupplier supplierObj,
			Object object, PlmWS wS)
			throws WTException, WTPropertyVetoException, SQLException_Exception {
		Object obj=object;
		if(SMOutboundWebServiceConstants.LOG_ENRTY_BUSINESS_SUPPLIER_OUTBOUND_PATH.equals(SMSupplierUtil.determineSupplierType(supplierObj))){
			obj=triggerBusinessSupplierWebServiceRequest(supplierObj,
					wS);

		}else if(SMOutboundWebServiceConstants.LOG_ENRTY_MATERIAL_SUPPLIER_OUTBOUND_PATH.equals(SMSupplierUtil.determineSupplierType(supplierObj))){
			obj=triggerMaterialSupplierWebServiceRequest(supplierObj,
					wS);

		}else if(SMOutboundWebServiceConstants.LOG_ENRTY_FACTORY_OUTBOUND_PATH.equals(SMSupplierUtil.determineSupplierType(supplierObj))){
			obj=triggerFactoryWebServiceRequest(supplierObj,
					wS);

		}
		return obj;
	}

	/**
	 * Trigger Request for Factory Object.
	 * @param supplierObj - LCSSupplier
	 * @param wS - PlmWs
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws SQLException_Exception 
	 */
	public static Object triggerFactoryWebServiceRequest(LCSSupplier supplierObj,
			PlmWS wS)
					throws WTException, WTPropertyVetoException, SQLException_Exception {
			setFactoryAssociatedToBusinessSupplier(SMSupplierUtil.getSupplierMDMIDFromFactoryMOA(SMSupplierUtil.queryBusinessSupplier(), supplierObj));
			
			if(isFactoryAssociatedToBusinessSupplier()){
				setSupplierRequestID(new SMSupplierHelper().generateOutboundSupplierRequestID());
				//ObjectFactory factory= new ObjectFactory();
	
				LOGGER.debug("Request ID generated FOR supplier integration >>>>  " + getSupplierRequestID());
				
				FactoryInformationUpdatesRequest factoryRequest =new FactoryInformationUpdatesRequest();
	
				SMSupplierDataProcessing.setDataToFactoryRequestBean(supplierObj, factoryRequest);
	
				if(!FormatHelper.hasContent((String)supplierObj.getValue(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY))){
					factoryRequest.setMdmId(SMOutboundWebServiceConstants.SUPPLIER_DUMMY_MDM_ID_ON_CREATE);
					setCreate(true);
				}
	
				if(SMOutboundWebServiceConstants.SUPPLIER_REQUEST_XML_GENERATION_VERBOSE){
					SMSupplierRequestXMLGeneration.generateXMLFileForFactoryRequest(factoryRequest, SMOutboundWebServiceConstants.FACTORY);
				}
	
				//LOGGER.debug("Create Log Entry !!!!!!!!!");
				//SMSupplierLogEntryProcessor.setLogEntryForSupplier(supplierObjectIDMap, SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_FAILED);
	
				((BindingProvider) wS).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT,
						SMOutboundWebServiceConstants.SUPPLIER_WEB_SERVICE_REQUEST_TIMEOUT_IN_MINUTES * 60 * 1000);
	
				//Process response
				FactoryInformationUpdatesRequestResponse factoryResponse = wS.factoryInformationUpdatesRequest(factoryRequest);
	
				setResponseErrorReason(factoryResponse.getErrorMessage());
	
				//processResponse(factoryResponse, isCreate(), supplierObj, supplierObjectIDMap);
				return factoryResponse;
		}else{
			//if(!isFailedCountDueToException()){
				//updateFailedCount();
				////LOGGER.info("Failed count incremented >>>>>>>   "+getFailedUpdateCount());
			//}
			LOGGER.debug("Factory Object is not referenced in Business Supplier MOA, hence not sending data to PLM GATE !!!!!!!!!!");
		}
		return new FactoryInformationUpdatesRequestResponse();
	}

	/**
	 * Trigger Request for Material Supplier Object.
	 * @param supplierObj - LCSSupplier
	 * @param wS - PlmWs
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws SQLException_Exception 
	 */
	public static Object triggerMaterialSupplierWebServiceRequest(
			LCSSupplier supplierObj, PlmWS wS) throws WTException, WTPropertyVetoException, SQLException_Exception {
		setSupplierRequestID(new SMSupplierHelper().generateOutboundSupplierRequestID());

		LOGGER.debug("Request ID generated >>>>  "+getSupplierRequestID());
		
		MaterialSupplierInformationUpdatesRequest materialSupplierRequest = new MaterialSupplierInformationUpdatesRequest();

		SMSupplierDataProcessing.setDataToMaterialSupplierRequestBean(supplierObj, materialSupplierRequest);

		if(!FormatHelper.hasContent((String)supplierObj.getValue(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY))){
			materialSupplierRequest.setMdmId(SMOutboundWebServiceConstants.SUPPLIER_DUMMY_MDM_ID_ON_CREATE);
			setCreate(true);
		}

		if(SMOutboundWebServiceConstants.SUPPLIER_REQUEST_XML_GENERATION_VERBOSE){
			SMSupplierRequestXMLGeneration.generateXMLFileForMaterialSupplierRequest(materialSupplierRequest, SMOutboundWebServiceConstants.MATERIAL_SUPPLIER);
		}
		//LOGGER.debug("Create Log Entry !!!!!!!!!");
		//SMSupplierLogEntryProcessor.setLogEntryForSupplier(supplierObjectIDMap, SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_FAILED);

		((BindingProvider)wS).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, SMOutboundWebServiceConstants.SUPPLIER_WEB_SERVICE_REQUEST_TIMEOUT_IN_MINUTES*60*1000);

		//Process Response
		MaterialSupplierInformationUpdatesRequestResponse materialSupplierResponse = wS.materialSupplierInformationUpdatesRequest(materialSupplierRequest);
		setResponseErrorReason(materialSupplierResponse.getErrorMessage());

		//processResponse(materialSupplierResponse, isCreate(), supplierObj, supplierObjectIDMap);
		return materialSupplierResponse;
	}

	/**
	 * Trigger Request for Business Supplier Object.
	 * @param supplierObj - LCSSupplier
	 * @param wS - PlmWs
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws SQLException_Exception 
	 */
	public static Object triggerBusinessSupplierWebServiceRequest(
			LCSSupplier supplierObj, PlmWS wS) throws WTException, WTPropertyVetoException, SQLException_Exception {
		setSupplierRequestID(new SMSupplierHelper().generateOutboundSupplierRequestID());

		LOGGER.debug("Request ID generated >>>>  "+getSupplierRequestID());
		
		BusinessSupplierInformationUpdatesRequest businessSupplierRequest = new BusinessSupplierInformationUpdatesRequest();

		SMSupplierDataProcessing.setDataToBusinessSupplierRequestBean(supplierObj, businessSupplierRequest);

		if(!FormatHelper.hasContent((String)supplierObj.getValue(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY))){
			businessSupplierRequest.setMdmId(SMOutboundWebServiceConstants.SUPPLIER_DUMMY_MDM_ID_ON_CREATE);
			setCreate(true);
		}

		if(SMOutboundWebServiceConstants.SUPPLIER_REQUEST_XML_GENERATION_VERBOSE){
			SMSupplierRequestXMLGeneration.generateXMLFileForBusinessSupplierRequest(businessSupplierRequest, SMOutboundWebServiceConstants.BUSINESS_SUPPLIER);
		}
		//LOGGER.debug("Create Log Entry !!!!!!!!!");
		//SMSupplierLogEntryProcessor.setLogEntryForSupplier(supplierObjectIDMap, SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_FAILED);

		((BindingProvider) wS).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT,
				SMOutboundWebServiceConstants.SUPPLIER_WEB_SERVICE_REQUEST_TIMEOUT_IN_MINUTES * 60 * 1000);

		//Process Response
		BusinessSupplierInformationUpdatesRequestResponse businessSupplierResponse = wS.businessSupplierInformationUpdatesRequest(businessSupplierRequest);
		setResponseErrorReason(businessSupplierResponse.getErrorMessage());
		//processResponse(businessSupplierResponse, isCreate(), supplierObj, supplierObjectIDMap);
		return businessSupplierResponse;
	}


	/**
	 * Process Response.
	 * @param obj - Object
	 * @param flag - boolean
	 * @param supplierObject - LCSSupplier
	 * @param supplierObjectIDMap - Map<string, String>
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws WTException - WTException
	 */
	public static void processResponse(Object obj, boolean flag, LCSSupplier supplierObject, Map<String, String> supplierObjectIDMap) {
		LOGGER.debug("Inside Process Response !!!!!!!!!!!!!");
		Map<String, String> supplierIDMapForLogEntry = new HashMap<>();
		try {
			checkSupplierResponseType(obj, flag, supplierObject,
					supplierObjectIDMap, supplierIDMapForLogEntry);
		} catch (WTPropertyVetoException wtPvEx) {
			LOGGER.error(ERROR_OCCURED, wtPvEx);
		} catch (WTException wtEx) {
			LOGGER.error(ERROR_OCCURED, wtEx);
		}

	}

	/**
	 * @param obj
	 * @param flag
	 * @param supplierObject
	 * @param supplierObjectIDMap
	 * @param supplierIDMapForLogEntry
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void checkSupplierResponseType(Object obj, boolean flag,
			LCSSupplier supplierObject,
			Map<String, String> supplierObjectIDMap,
			Map<String, String> supplierIDMapForLogEntry) throws WTException,
			WTPropertyVetoException {
		if(obj instanceof BusinessSupplierInformationUpdatesRequestResponse){
			processBusinessSupplierResponse(obj, flag, supplierObject,
					supplierObjectIDMap, supplierIDMapForLogEntry);
		}else if(obj instanceof MaterialSupplierInformationUpdatesRequestResponse){
			processMaterialSupplierResponse(obj, flag, supplierObject,
					supplierObjectIDMap, supplierIDMapForLogEntry);
		}else if(obj instanceof FactoryInformationUpdatesRequestResponse && isFactoryAssociatedToBusinessSupplier()){
			processFactoryResponse(obj, flag, supplierObject,
					supplierObjectIDMap, supplierIDMapForLogEntry);
		}else if(obj == null && flag){
			SMSupplierLogEntryProcessor.setLogEntryForSupplier(supplierObjectIDMap, SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_FAILED,supplierObject);
		}
		else if (obj == null && !Boolean.valueOf(flag)) {
			if(!isFailedCountDueToException()){
				updateFailedCount();
				LOGGER.debug("Failed count incremented for resposne>>>>>>>   " + getFailedUpdateCount());
			}
			updateLogEntryOnResponse(supplierObject, supplierIDMapForLogEntry, SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST_FAILED);
		}
	}

	/**
	 * Process Factory Response.
	 * @param obj - Object
	 * @param flag - boolean
	 * @param supplierObject - LCSSupplier
	 * @param supplierObjectIDMap - Map<Sring, String>
	 * @param supplierIDMapForLogEntry - Map<String, String>
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void processFactoryResponse(Object obj, boolean flag,
			LCSSupplier supplierObject,
			Map<String, String> supplierObjectIDMap,
			Map<String, String> supplierIDMapForLogEntry) throws WTException,
			WTPropertyVetoException {
		LOGGER.debug("Inside processFactoryResponse !!!!!!!!!!   FLAG  >>>>>>>>>>    "+flag);
		FactoryInformationUpdatesRequestResponse factoryResp = (FactoryInformationUpdatesRequestResponse)obj;
		//Storing MDMID in variable
		setResponseMDMID(factoryResp.getMdmId());
		setResponseIntegrationStatus(factoryResp.isIntegrationStatus());
		//LOGGER.debug("MDM ID >>>>>>>>>>    "+factoryResp.getMdmId());
		if(flag ){
			creatingFactoryLogEntryOnCreateRequest(supplierObject,
					supplierObjectIDMap, supplierIDMapForLogEntry, factoryResp);
		}else {
			creatingFactoryLogEntryObjectOnUpdate(supplierObject,
					supplierIDMapForLogEntry, factoryResp);
		}
	}

	/**
	 * @param supplierObject
	 * @param supplierIDMapForLogEntry
	 * @param factoryResp
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void creatingFactoryLogEntryObjectOnUpdate(
			LCSSupplier supplierObject,
			Map<String, String> supplierIDMapForLogEntry,
			FactoryInformationUpdatesRequestResponse factoryResp)
			throws WTException, WTPropertyVetoException {
		if(factoryResp.isIntegrationStatus()){
			factoryBusinessSupplierAssociation(supplierObject,
					supplierIDMapForLogEntry);
		}else if(!factoryResp.isIntegrationStatus() || !isFactoryAssociatedToBusinessSupplier()){
			//updateFailedCount();
			////LOGGER.info("Failed count incremented >>>>>>>   "+getFailedUpdateCount());
			if(!isFailedCountDueToException()){
				updateFailedCount();
				LOGGER.debug("Failed count incremented ********  >>>>>>>   " + getFailedUpdateCount());
			}
			updateLogEntryOnResponse(supplierObject, supplierIDMapForLogEntry, SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST_FAILED);
		}
	}

	/**
	 * @param supplierObject
	 * @param supplierObjectIDMap
	 * @param supplierIDMapForLogEntry
	 * @param factoryResp
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void creatingFactoryLogEntryOnCreateRequest(
			LCSSupplier supplierObject,
			Map<String, String> supplierObjectIDMap,
			Map<String, String> supplierIDMapForLogEntry,
			FactoryInformationUpdatesRequestResponse factoryResp)
			throws WTException, WTPropertyVetoException {
		if(factoryResp.isIntegrationStatus() && !SMOutboundWebServiceConstants.SUPPLIER_DUMMY_MDM_ID_ON_CREATE.equals(factoryResp.getMdmId())){
			supplierObject.setValue(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY, factoryResp.getMdmId());
			//SMSupplierHelper.persistSupplier(supplierObject);
			updateLogEntryOnResponse(supplierObject,supplierIDMapForLogEntry, SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_SUCCESS);
			supplierIDMapForLogEntry.clear();
		}else if(!factoryResp.isIntegrationStatus() || SMOutboundWebServiceConstants.SUPPLIER_DUMMY_MDM_ID_ON_CREATE.equals(factoryResp.getMdmId())){
			SMSupplierLogEntryProcessor.setLogEntryForSupplier(supplierObjectIDMap, SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_FAILED,supplierObject);
		}
	}

	/**
	 * @param supplierObject
	 * @param supplierIDMapForLogEntry
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void factoryBusinessSupplierAssociation(
			LCSSupplier supplierObject,
			Map<String, String> supplierIDMapForLogEntry) throws WTException,
			WTPropertyVetoException {
		if(isFactoryAssociatedToBusinessSupplier()){
			updateLogEntryOnResponse(supplierObject, supplierIDMapForLogEntry, SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST_SUCCESS);
		}else{
			if(!isFailedCountDueToException()){
				updateFailedCount();
				LOGGER.debug("Failed count incremented >>>>>>>   " + getFailedUpdateCount());
			}
		}
	}

	/**
	 * Process Material Supplier Response.
	 * @param obj - Object
	 * @param flag - boolean
	 * @param supplierObject - LCSSupplier
	 * @param supplierObjectIDMap - Map<Sring, String>
	 * @param supplierIDMapForLogEntry - Map<String, String>
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void processMaterialSupplierResponse(Object obj,
			boolean flag, LCSSupplier supplierObject,
			Map<String, String> supplierObjectIDMap,
			Map<String, String> supplierIDMapForLogEntry) throws WTException,
			WTPropertyVetoException {
		MaterialSupplierInformationUpdatesRequestResponse materialSuppResponse = (MaterialSupplierInformationUpdatesRequestResponse)obj;
		setResponseMDMID(materialSuppResponse.getMdmId());
		setResponseIntegrationStatus(materialSuppResponse.isIntegrationStatus());
		LOGGER.debug("MDM ID >>>>>>>>>>    "+materialSuppResponse.getMdmId());
		if(flag){
			if(materialSuppResponse.isIntegrationStatus() && !SMOutboundWebServiceConstants.SUPPLIER_DUMMY_MDM_ID_ON_CREATE.equals(materialSuppResponse.getMdmId())){
				supplierObject.setValue(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY, materialSuppResponse.getMdmId());
				//SMSupplierHelper.persistSupplier(supplierObject);
				updateLogEntryOnResponse(supplierObject,supplierIDMapForLogEntry, SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_SUCCESS);
				supplierIDMapForLogEntry.clear();
			}else if(!materialSuppResponse.isIntegrationStatus() || SMOutboundWebServiceConstants.SUPPLIER_DUMMY_MDM_ID_ON_CREATE.equals(materialSuppResponse.getMdmId())){
				SMSupplierLogEntryProcessor.setLogEntryForSupplier(supplierObjectIDMap, SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_FAILED,supplierObject);
			}
		}else {
			if(materialSuppResponse.isIntegrationStatus()){
				updateLogEntryOnResponse(supplierObject, supplierIDMapForLogEntry, SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST_SUCCESS);
			}else{
				if(!isFailedCountDueToException()){
					updateFailedCount();
					//LOGGER.info("Failed count incremented #####  >>>>>>>   "+getFailedUpdateCount());
				}
				updateLogEntryOnResponse(supplierObject, supplierIDMapForLogEntry, SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST_FAILED);
			}
		}
	}

	/**
	 * Process Business Supplier Response.
	 * @param obj - Object
	 * @param flag - boolean
	 * @param supplierObject - LCSSupplier
	 * @param supplierObjectIDMap - Map<Sring, String>
	 * @param supplierIDMapForLogEntry - Map<String, String>
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void processBusinessSupplierResponse(Object obj,
			boolean flag, LCSSupplier supplierObject,
			Map<String, String> supplierObjectIDMap,
			Map<String, String> supplierIDMapForLogEntry) throws WTException,
			WTPropertyVetoException {
		LOGGER.debug("Inside Process Business Supplier Response !!!!!!!!!!!");
		BusinessSupplierInformationUpdatesRequestResponse businessSuppResponse = (BusinessSupplierInformationUpdatesRequestResponse)obj;
		setResponseMDMID(businessSuppResponse.getMdmId());
		setResponseIntegrationStatus(businessSuppResponse.isIntegrationStatus());
		LOGGER.debug("MDM ID in response  >>>>>>>>>>>>    "+businessSuppResponse.getMdmId());
		LOGGER.debug("FLAG >>>>>>>>>>>>>>>>   "+flag);
		if(flag){
			//Create Request Successful
			if(businessSuppResponse.isIntegrationStatus() && !SMOutboundWebServiceConstants.SUPPLIER_DUMMY_MDM_ID_ON_CREATE.equals(businessSuppResponse.getMdmId())){
				supplierObject.setValue(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY, businessSuppResponse.getMdmId());
				//SMSupplierHelper.persistSupplier(supplierObject);
				updateLogEntryOnResponse(supplierObject,supplierIDMapForLogEntry, SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_SUCCESS);
				supplierIDMapForLogEntry.clear();
			}else if(!businessSuppResponse.isIntegrationStatus()|| SMOutboundWebServiceConstants.SUPPLIER_DUMMY_MDM_ID_ON_CREATE.equals(businessSuppResponse.getMdmId())){
				//Update Request Failed
				SMSupplierLogEntryProcessor.setLogEntryForSupplier(supplierObjectIDMap, SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_FAILED,supplierObject);
			}
		}else {
			//SMSupplierHelper.persistSupplier(supplierObject);
			//Update Request Success
			if(businessSuppResponse.isIntegrationStatus()){
				updateLogEntryOnResponse(supplierObject, supplierIDMapForLogEntry, SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST_SUCCESS);
			}else{
				if(!isFailedCountDueToException()){
					updateFailedCount();
					LOGGER.debug("Failed count incremented >>>>>>>   " + getFailedUpdateCount());
				}
				//Update request failed
				updateLogEntryOnResponse(supplierObject, supplierIDMapForLogEntry, SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST_FAILED);
			}
		}
	}

	/**
	 * Update Log Entry as per Response.
	 * @param supplierObject - LCSSupplier
	 * @param supplierIDMapForLogEntry - Map<String, String>
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void updateLogEntryOnResponse(LCSSupplier supplierObject,
			Map<String, String> supplierIDMapForLogEntry, String requestType) throws WTException,
			WTPropertyVetoException {
		LOGGER.debug("Inside Update Log Entry on response !!!!!!!!!!!!");
		LOGGER.debug("req type >>>>>>>  "+requestType);
		supplierIDMapForLogEntry.put(SMSupplierUtil.getSupplierMasterReferenceFromSupplier(supplierObject), supplierObject.getName());
		SMSupplierLogEntryProcessor.setLogEntryForSupplier(supplierIDMapForLogEntry, requestType,supplierObject);
	}

	/**
	 * Gets Request ID.
	 * @return the supplierRequestID
	 */
	public static int getSupplierRequestID() {
		return supplierRequestID;
	}

	/**
	 * Sets Request ID to class variable.
	 * @param supplierRequestID the supplierRequestID to set
	 */
	public static void setSupplierRequestID(int supplierRequestID) {
		SMSupplierOutboundDataRequestClient.supplierRequestID = supplierRequestID;
	}

	/**
	 * Gets value if request is Create.
	 * @return the isCreate
	 */
	public static boolean isCreate() {
		return isCreate;
	}

	/**
	 * Sets flag if request is Create.
	 * @param isCreate the isCreate to set
	 */
	public static void setCreate(boolean bool) {
		isCreate = bool;
	}

	/**
	 * @return the responseErrorReason
	 */
	public static String getResponseErrorReason() {
		return responseErrorReason;
	}

	/**
	 * @param responseErrorReason the responseErrorReason to set
	 */
	public static void setResponseErrorReason(String responseErrorReason) {
		SMSupplierOutboundDataRequestClient.responseErrorReason = responseErrorReason;
	}

	/**
	 * Get MDMID from Response.
	 * @return the responseMDMID
	 */
	public static String getResponseMDMID() {
		return responseMDMID;
	}

	/**
	 * Set MDMID from Response.
	 * @param responseMDMID the responseMDMID to set
	 */
	public static void setResponseMDMID(String responseMDMID) {
		SMSupplierOutboundDataRequestClient.responseMDMID = responseMDMID;
	}

	/**
	 * @return the responseIntegrationStatus
	 */
	public static boolean isResponseIntegrationStatus() {
		return responseIntegrationStatus;
	}

	/**
	 * @param responseIntegrationStatus the responseIntegrationStatus to set
	 */
	public static void setResponseIntegrationStatus(
			boolean responseIntegrationStatus) {
		SMSupplierOutboundDataRequestClient.responseIntegrationStatus = responseIntegrationStatus;
	}

	/**
	 * Get value of flag.
	 * @return the factoryAssociatedToBusinessSupplier
	 */
	public static boolean isFactoryAssociatedToBusinessSupplier() {
		return factoryAssociatedToBusinessSupplier;
	}

	/**
	 * Set flag if factory is associated to business Supplier.
	 * @param factoryAssociatedToBusinessSupplier the factoryAssociatedToBusinessSupplier to set
	 */
	public static void setFactoryAssociatedToBusinessSupplier(
			boolean factoryAssociatedToBusinessSupplier) {
		SMSupplierOutboundDataRequestClient.factoryAssociatedToBusinessSupplier = factoryAssociatedToBusinessSupplier;
	}

	/**
	 * @return the failedUpdateCount
	 */
	public static int getFailedUpdateCount() {
		return failedUpdateCount;
	}

	/**
	 * @param failedUpdateCount the failedUpdateCount to set
	 */
	public static void setFailedUpdateCount(int failedUpdateCount) {
		SMSupplierOutboundDataRequestClient.failedUpdateCount = failedUpdateCount;
	}
	/**
	 * @param failedUpdateCount the failedUpdateCount to set
	 */
	public static void updateFailedCount() {
		failedUpdateCount ++;
	}

	/**
	 * @return the failedCountDueToException
	 */
	public static boolean isFailedCountDueToException() {
		return failedCountDueToException;
	}

	/**
	 * @param failedCountDueToException the failedCountDueToException to set
	 */
	public static void setFailedCountDueToException(
			boolean failedCountDueToException) {
		SMSupplierOutboundDataRequestClient.failedCountDueToException = failedCountDueToException;
	}
}
