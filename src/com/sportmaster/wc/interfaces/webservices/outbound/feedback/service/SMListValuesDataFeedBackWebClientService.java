package com.sportmaster.wc.interfaces.webservices.outbound.feedback.service;

import java.util.List;
import java.util.Map;
import javax.xml.rpc.soap.SOAPFaultException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import com.sportmaster.wc.interfaces.webservices.bean.SQLException_Exception;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.webservices.bean.BOEnumeration;
import com.sportmaster.wc.interfaces.webservices.bean.GetStatusDivisionRequest;
import com.sportmaster.wc.interfaces.webservices.bean.GetStatusDivisionRequestResponse;
import com.sportmaster.wc.interfaces.webservices.bean.GetStatusSimpleBOInformationRequest;
import com.sportmaster.wc.interfaces.webservices.bean.GetStatusSimpleBOInformationRequestResponse;
import com.sportmaster.wc.interfaces.webservices.bean.ObjectFactory;
import com.sportmaster.wc.interfaces.webservices.bean.PlmEndpointService;
import com.sportmaster.wc.interfaces.webservices.bean.PlmWS;
import com.sportmaster.wc.interfaces.webservices.outbound.feedback.helper.SMLVFIntegrationHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.feedback.processor.SMListVaulesFeedbackProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.feedback.util.SMFBIntegrationUtilBean;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.helper.SMLVIntegrationHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMIntegrationUtill;
import com.sun.xml.ws.client.BindingProviderProperties;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

/**
 * SMListValuesDataFeedBackWebClientService.java
 * This class has methods to invoke the service for List value feedback.
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMListValuesDataFeedBackWebClientService {
	/**
	 * SUB_DIVISION_FEEDBACK_INTERFACE_NAME.
	 */
	private static final String SUB_DIVISION_FEEDBACK_INTERFACE_NAME = " STATUS INFORMATION REQUEST FOR SUB DIVISION TREE ";
	/**
	 * LISTVALUES_FEEDBACK_INTERFACE_NAME.
	 */
	private static final String LISTVALUES_FEEDBACK_INTERFACE_NAME = " STATUS INFROMATION REQUEST FOR LISTVALUES ";
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMListValuesDataFeedBackWebClientService.class);
	
	
	/**
	 *  to invoke the web service request for Feedback.
	 *  @param smfbBean the smfbbean
	 */
	
	
	public void sminvokeWebRequest(SMFBIntegrationUtilBean smfbBean ){
		String fbErrorMsg;
		List<String> listType= FormatHelper.commaSeparatedListToList(SMInboundWebserviceConstants.LIST_VALUES_ENUM_VALUES);
		int requestID;

		Map<String, FlexObject> dbData;
		try {
			BOEnumeration boEnum;
			SMIntegrationUtill utill= new SMIntegrationUtill();
			//Getting Data from log entry  list Value
			dbData = utill.getLogEntryListValues(SMInboundWebserviceConstants.LOGENTRY_LISTVALUE_TYPE, SMInboundWebserviceConstants.LOG_ENTRY_LIST_VALUES_INTEGRATION_FIELDS, SMInboundWebserviceConstants.LOGENTRY_STATUS_VALUE_NOTSENT);
			Map<String,Map<String, LCSLogEntry>> logentryDbData=SMLVFIntegrationHelper.getLogEntrydataByType(dbData);
			//Getting Data from log entry  subdivision Tree
			dbData=utill.getLogEntryListValues(SMInboundWebserviceConstants.LOGENTRY_SUBDIVISIONTREE_NMAE, SMInboundWebserviceConstants.LOGENTRY_SUBDIVISIONTREE_ATTRIBUTE, SMInboundWebserviceConstants.LOGENTRY_STATUS_VALUE_NOTSENT);
			requestID=new SMUtill().getRequestID();
			//Creating object factory
			ObjectFactory factory= new ObjectFactory();
			for(String type:listType){
				try{
					smfbBean.setFbrequestId(requestID);
					// Creating StatusSimpleBOInformationRequest object
					GetStatusSimpleBOInformationRequest lvFeedbackRequest=factory.createGetStatusSimpleBOInformationRequest();
					//getting PLM service
					PlmEndpointService service = new PlmEndpointService();
					//getting End point web service
					PlmWS ws = service.getPlmEndpointPort();
					LOGGER.debug("Processing data for type : "+type);
					//processing data for the Business Object type.
					boEnum=SMIntegrationUtill.getBOEnumType(type);
					//Setting List value Status
					lvFeedbackRequest.setStatus(SMListVaulesFeedbackProcessor.processFeedbackQueue(logentryDbData.get(type.toLowerCase()), factory,boEnum,smfbBean));
					requestID++;
					//adding service timeout time.
					((BindingProvider)ws).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, SMLVIntegrationHelper.getTimeoutbyType(listType, type));
					
					//generating XML for List value
					LOGGER.debug("Generating XML files");
					SMLVFIntegrationHelper.generateXML(lvFeedbackRequest,type);
					LOGGER.debug("Invoking client service to send data for type : "+type);
					//getting list value Response
					GetStatusSimpleBOInformationRequestResponse lvResponse=ws.getStatusSimpleBOInformationRequest(lvFeedbackRequest);
					LOGGER.debug("setting log entry status to PROCESSED ");
					//processing list value response
					SMListVaulesFeedbackProcessor.processListValuesFeedbackResponse(logentryDbData.get(type.toLowerCase()),lvResponse, smfbBean);
				//catching service related exception
				}catch(ClientTransportException listclientExc){
					errorBeancount(smfbBean);
					fbErrorMsg = listclientExc.getLocalizedMessage()==null? listclientExc.getMessage():listclientExc.getLocalizedMessage();
					LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_OUTBOUND_CONNECTIVITY_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE+LISTVALUES_FEEDBACK_INTERFACE_NAME+SMInboundWebserviceConstants.CLIENT_TRANSPORT_ERROR_MESSAGE+fbErrorMsg);
					listclientExc.printStackTrace();
				}catch(SOAPFaultException listsoapExp){
					errorBeancount(smfbBean);
					fbErrorMsg = listsoapExp.getLocalizedMessage()==null? listsoapExp.getMessage():listsoapExp.getLocalizedMessage();
					LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_OUTBOUND_CONNECTIVITY_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE+LISTVALUES_FEEDBACK_INTERFACE_NAME+SMInboundWebserviceConstants.SOAP_FAULT_ERROR_MESSAGE+fbErrorMsg);
					listsoapExp.printStackTrace();
				}catch (ServerSOAPFaultException listserverSoapEXP) {
					errorBeancount(smfbBean);
					fbErrorMsg = listserverSoapEXP.getLocalizedMessage();
					LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_OUTBOUND_SCHEMA_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_SCHEMA_ERROR_MESSAGE+LISTVALUES_FEEDBACK_INTERFACE_NAME+SMInboundWebserviceConstants.SERVER_SOAP_FAULT_ERROR_MESSAGE+fbErrorMsg);
					listserverSoapEXP.printStackTrace();
				} catch (WebServiceException webservcExp) {
					errorBeancount(smfbBean);
					fbErrorMsg = webservcExp.getLocalizedMessage();
					LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_OUTBOUND_TIMEOUT_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE+LISTVALUES_FEEDBACK_INTERFACE_NAME+SMInboundWebserviceConstants.WEB_SERVICE_TIMEOUT_ERROR_MESSAGE+fbErrorMsg);
					webservcExp.printStackTrace();
				} catch (SQLException_Exception sqlExp) {
					LOGGER.error("ERROR OCCURED : - "+sqlExp.getLocalizedMessage());
					sqlExp.printStackTrace();
				}
					
			}
			smfbBean.setFbrequestId(requestID);
			sendRequestForDivisionFeedback(factory, dbData, smfbBean);
		} catch (WTException wtExp) {
			wtExp.printStackTrace();
			LOGGER.error("WTException : "+wtExp.getLocalizedMessage());
		} 

	}
	/**
	 * this method will send the subdivision feed back.
	 * @param factory the factory
	 * @param dbData the dbdata
	 * @param smfbBean the smfbbean
	 */
	private static void sendRequestForDivisionFeedback(ObjectFactory factory,Map<String, FlexObject> dbData,SMFBIntegrationUtilBean smfbBean){
		String fbErrorMsg;
		try{
			LOGGER.debug("Processing Log entry data for Divison tree");
			GetStatusDivisionRequest divisionFeedbackRequest=factory.createGetStatusDivisionRequest();
			//Setting Sub division Status
			divisionFeedbackRequest.setDivisionStatus(SMListVaulesFeedbackProcessor.processDivisionFeedBackQueue(dbData, factory,smfbBean));
			LOGGER.debug("Generating XML files");
			SMLVFIntegrationHelper.generateXMLForDivision(divisionFeedbackRequest,"DIVISION");
			LOGGER.debug("Invoking client service to send data for type : Divison Tree");
			// getting service
			PlmEndpointService service = new PlmEndpointService();
			//getting End point web service
			PlmWS ws = service.getPlmEndpointPort();
			//adding service timeout time.
			((BindingProvider)ws).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, SMInboundWebserviceConstants.SUB_DIVISION_TIMEOUT_IN_MINUTES*60*1000);
			//getting division Response
			GetStatusDivisionRequestResponse divResponse=ws.getStatusDivisionRequest(divisionFeedbackRequest);
			//processing List feedback data
			SMListVaulesFeedbackProcessor.processDivsionFeedbackResponse(dbData,divResponse, smfbBean);
			LOGGER.debug("Setting Divsion tree log entry status to PROCESSED ");
		}catch(ClientTransportException clientExc){
			errorBeancount(smfbBean);
			fbErrorMsg = clientExc.getLocalizedMessage()==null? clientExc.getMessage():clientExc.getLocalizedMessage();
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_OUTBOUND_CONNECTIVITY_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE+SUB_DIVISION_FEEDBACK_INTERFACE_NAME+SMInboundWebserviceConstants.CLIENT_TRANSPORT_ERROR_MESSAGE+fbErrorMsg);
			clientExc.printStackTrace();
		}catch(SOAPFaultException soapExp){
			errorBeancount(smfbBean);
			fbErrorMsg = soapExp.getLocalizedMessage()==null? soapExp.getMessage():soapExp.getLocalizedMessage();
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_OUTBOUND_CONNECTIVITY_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE+SUB_DIVISION_FEEDBACK_INTERFACE_NAME+SMInboundWebserviceConstants.SOAP_FAULT_ERROR_MESSAGE+fbErrorMsg);
			soapExp.printStackTrace();
		}catch (ServerSOAPFaultException serverSoapEXP) {
			errorBeancount(smfbBean);
			fbErrorMsg = serverSoapEXP.getLocalizedMessage()==null? serverSoapEXP.getMessage():serverSoapEXP.getLocalizedMessage();
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_OUTBOUND_SCHEMA_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_SCHEMA_ERROR_MESSAGE+SUB_DIVISION_FEEDBACK_INTERFACE_NAME+SMInboundWebserviceConstants.SERVER_SOAP_FAULT_ERROR_MESSAGE+fbErrorMsg);
			serverSoapEXP.printStackTrace();
		} catch (WebServiceException webservcdivExp) {
			errorBeancount(smfbBean);
			fbErrorMsg = webservcdivExp.getLocalizedMessage();
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_OUTBOUND_TIMEOUT_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE+LISTVALUES_FEEDBACK_INTERFACE_NAME+SMInboundWebserviceConstants.WEB_SERVICE_TIMEOUT_ERROR_MESSAGE+fbErrorMsg);
			webservcdivExp.printStackTrace();
		//catching SQL EXCEPTON
		} catch (SQLException_Exception sqlExpDiv) {
			LOGGER.error("ERROR OCCURED :  "+sqlExpDiv.getLocalizedMessage());
			sqlExpDiv.printStackTrace();
		}
	}
	/**
	 * set bean cunt while error occured.
	 * @param smfbBean - SMFBIntegrationUtilBean
	 */
	private static void errorBeancount(SMFBIntegrationUtilBean smfbBean){
	   //setting count for failure
		smfbBean.setFbFailCount(-smfbBean.getFbFailCount());
		//setting sucess count 
		smfbBean.setFbObjCount(-smfbBean.getFbObjCount());
	}
}
