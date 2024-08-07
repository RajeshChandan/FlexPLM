package com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.service;


import java.util.List;
import java.util.Map;

import javax.xml.rpc.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import wt.util.WTException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import com.google.common.collect.Multimap;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.webservices.bean.BOEnumeration;
import com.sportmaster.wc.interfaces.webservices.bean.GetSimpleBOInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.GetSimpleBOInformationUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.bean.ObjectFactory;
import com.sportmaster.wc.interfaces.webservices.bean.PlmEndpointService;
import com.sportmaster.wc.interfaces.webservices.bean.PlmWS;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.helper.SMLVIntegrationHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.processor.SMListValuesResponseDataProcessor;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.utill.SMLVIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.utill.SMListValuesUtil;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMIntegrationUtill;
import com.sun.xml.ws.client.BindingProviderProperties;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.fault.ServerSOAPFaultException;
import com.sportmaster.wc.interfaces.webservices.bean.SQLException_Exception;

/**
 * SMListValuesDataRequestWebClientService.java
 * This class is using to call the methods defined in process class.
 * for Integration.
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMListValuesDataRequestWebClientService {
	/**
	 * INTERFACE_NAME.
	 */
	private static final String INTERFACE_NAME = " LIST VALUES INBOUND DATA REQUEST ";
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMListValuesDataRequestWebClientService.class);

	/**
	 * constructor.
	 */
	public SMListValuesDataRequestWebClientService() {
	}


	/**
	 *  This method send WS request and process response object.
	 * @param lvBean SMLVIntegrationBean
	 */
	public void invokeRequest(SMLVIntegrationBean lvBean) {
		String errorMsg;
		int requestID;
		List<String> listType= FormatHelper.commaSeparatedListToList(SMInboundWebserviceConstants.LIST_VALUES_ENUM_VALUES);
		try{
			Map<String,Multimap<String, FlexObject>> dbBOData= SMListValuesUtil.getAllListValues(SMInboundWebserviceConstants.LIST_VALUES_INTEGRATION_TYPES);
			LOGGER.debug("Fetched List values DB Data Size :"+dbBOData.size());
			//log entry db data
			Map<String, FlexObject> dbLogEntryData=new SMIntegrationUtill().getLogEntryListValues(SMInboundWebserviceConstants.LOGENTRY_LISTVALUE_TYPE,SMInboundWebserviceConstants.LOG_ENTRY_LIST_VALUES_INTEGRATION_FIELDS,null);
			LOGGER.debug("Fetched List values Log Entry DB Data Size :"+dbLogEntryData.size());
			requestID=new SMUtill().getRequestID();
			for(String type:listType){
				
				try{
					GetSimpleBOInformationUpdatesRequestResponse response;
					//creating object of ObjectFactory
					ObjectFactory factory= new ObjectFactory();
					//creating object of PlmEndpointService
					PlmEndpointService service = new PlmEndpointService();
					PlmWS ws = service.getPlmEndpointPort();
					//Declaring variable of GetSimpleBOInformationUpdatesRequest
					GetSimpleBOInformationUpdatesRequest request;
					//creating object of GetSimpleBOInformationUpdatesRequest
					request = factory.createGetSimpleBOInformationUpdatesRequest();
					BOEnumeration boEnum=SMIntegrationUtill.getBOEnumType(type);
					//setting name
					request.setName(boEnum);
					LOGGER.debug("Request For List value"+request.getName());
					//setting request id
					request.setRequestId(requestID);
					requestID++;
					//adding service timeout time.
					((BindingProvider)ws).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, SMLVIntegrationHelper.getTimeoutbyType(listType, type));
					//invoking request
					response = ws.getSimpleBOInformationUpdatesRequest(request);

					//generating xml file from response object
					SMLVIntegrationHelper.generateXMLFileForListValue(response, type);
					//processing response data
					SMListValuesResponseDataProcessor.processResponseData(response,lvBean,dbBOData,dbLogEntryData);
			
				}catch(ClientTransportException clientExc){
					errorMsg = clientExc.getLocalizedMessage()==null? clientExc.getMessage():clientExc.getLocalizedMessage();
					LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_CONNECTIVITY_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE+INTERFACE_NAME+SMInboundWebserviceConstants.CLIENT_TRANSPORT_ERROR_MESSAGE+errorMsg);
					clientExc.printStackTrace();
				}catch(SOAPFaultException soapExp){
					errorMsg = soapExp.getLocalizedMessage()==null? soapExp.getMessage():soapExp.getLocalizedMessage();
					LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_CONNECTIVITY_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE+INTERFACE_NAME+SMInboundWebserviceConstants.SOAP_FAULT_ERROR_MESSAGE+errorMsg);
					soapExp.printStackTrace();
				}catch (ServerSOAPFaultException serverSoapEXP) {
					errorMsg = serverSoapEXP.getLocalizedMessage();
					LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_SCHEMA_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_SCHEMA_ERROR_MESSAGE+INTERFACE_NAME+SMInboundWebserviceConstants.SERVER_SOAP_FAULT_ERROR_MESSAGE+errorMsg);
					serverSoapEXP.printStackTrace();
				} catch (WebServiceException webservcExp) {
					errorMsg = webservcExp.getLocalizedMessage();
					LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_TIMEOUT_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE+INTERFACE_NAME+SMInboundWebserviceConstants.WEB_SERVICE_TIMEOUT_ERROR_MESSAGE+errorMsg);
					webservcExp.printStackTrace();
				} catch (SQLException_Exception sqlExcpt) {
					LOGGER.error(sqlExcpt.getLocalizedMessage());
					sqlExcpt.printStackTrace();
				}
			}
			//catching wt exception
		} catch (WTException wtExp) {
			wtExp.printStackTrace();
			LOGGER.error(wtExp.getLocalizedMessage());
		}
	}
}
