package com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.webservices.bean.Division;
import com.sportmaster.wc.interfaces.webservices.bean.GetDivisionUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.GetDivisionUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.bean.PlmEndpointService;
import com.sportmaster.wc.interfaces.webservices.bean.PlmWS;
import com.sportmaster.wc.interfaces.webservices.bean.SQLException_Exception;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.helper.SMSubDivisionHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.processor.SMSubDivisionLogEntryProcessor;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.processor.SMSubDivisionTreeDataProcessor;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.util.SMSubDivisionTreeUtil;
import com.sun.xml.ws.client.BindingProviderProperties;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

/**
 * 
 * @author 'true' ITC_Infotech.
 *
 */
public class SMSubdivisionDataRequestWebClient {
	
	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSubdivisionDataRequestWebClient.class);
	/**
	 * Initializing total count.
	 */
	private static int totalCount;
	/**
	 * Declaring Error Message.
	 */
	private static String errorMsg;
	/**
	 * Declaring Request ID.
	 */
	private static String requestID;
	
	/**
	 * Constructor.
	 */
	protected SMSubdivisionDataRequestWebClient(){
		//Constructor
	}
	
	/**
	 * Sends request to PLM Gate.
	 * Also receives the response.
	 * @param map - Map<String, LCSLifeCycleManaged>
	 */
	public static void subDivClientRequest(Map<String, LCSLifecycleManaged> map) {
		try{
			LOGGER.debug("###################  STARTING WEB CLIENT   ######################");
			
			//Calling PLM Gate Web Service
			PlmEndpointService plmEndPointService = new PlmEndpointService();
			//Getting End Point URL
			PlmWS wS = plmEndPointService.getPlmEndpointPort();
			

			//Initializing request object
			GetDivisionUpdatesRequest divUpdateReq = new GetDivisionUpdatesRequest();
			//setting request ID
			divUpdateReq.setRequestId(new SMUtill().getRequestID());
			
			//Set timeout until the response is received
			((BindingProvider)wS).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, SMInboundWebserviceConstants.SUB_DIVISION_TIMEOUT_IN_MINUTES*60*1000);
			
			//Response
			GetDivisionUpdatesRequestResponse divUpdateRes = wS.getDivisionUpdatesRequest(divUpdateReq);
			
			//Setting Request ID obtained from the response
			setRequestID(String.valueOf(divUpdateRes.getRequestId()));
			
			//generates XML in the server
			SMSubDivisionHelper.generateXMLFileForSubDivisionTree(divUpdateRes,SMInboundWebserviceConstants.SUB_DIVISION_TYPE);
			
			//////////////////////////////////////////////////////
			
			//Getting response in a collection
			iterateThroughResponse(map, divUpdateRes);
			//print Summary of Integration run
			SMSubDivisionTreeUtil.printSummary();
			LOGGER.debug("###################  END OF WEB CLIENT   ######################");
		}catch(ClientTransportException clientExc){
			errorMsg = clientExc.getLocalizedMessage();
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_CONNECTIVITY_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE+SMInboundWebserviceConstants.SUB_DIVISION_INTERFACE_NAME+SMInboundWebserviceConstants.CLIENT_TRANSPORT_ERROR_MESSAGE+errorMsg);
			clientExc.printStackTrace();
		}catch (ServerSOAPFaultException serverSoapEXP) {
			errorMsg = serverSoapEXP.getLocalizedMessage();
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_SCHEMA_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_SCHEMA_ERROR_MESSAGE+SMInboundWebserviceConstants.SUB_DIVISION_INTERFACE_NAME+SMInboundWebserviceConstants.SERVER_SOAP_FAULT_ERROR_MESSAGE+errorMsg);
			serverSoapEXP.printStackTrace();
		}catch(SOAPFaultException soapExp){
			errorMsg = soapExp.getLocalizedMessage();
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_CONNECTIVITY_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE+SMInboundWebserviceConstants.SUB_DIVISION_INTERFACE_NAME+SMInboundWebserviceConstants.SOAP_FAULT_ERROR_MESSAGE+errorMsg);
			soapExp.printStackTrace();
		}catch(WebServiceException webSrcvExcp){
			errorMsg = webSrcvExcp.getLocalizedMessage();
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_TIMEOUT_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE+SMInboundWebserviceConstants.SUB_DIVISION_INTERFACE_NAME+SMInboundWebserviceConstants.WEB_SERVICE_TIMEOUT_ERROR_MESSAGE+errorMsg);
			webSrcvExcp.printStackTrace();
		}catch (WTPropertyVetoException pvExc) {
			LOGGER.error(pvExc.getLocalizedMessage());
			pvExc.printStackTrace();
		} catch (WTException wtExc) {
			LOGGER.error(wtExc.getLocalizedMessage());
			wtExc.printStackTrace();
		} catch (IOException ioExc) {
			LOGGER.error(ioExc.getLocalizedMessage());
			ioExc.printStackTrace();
		} catch (SQLException_Exception sqlExcpt) {
			LOGGER.error(sqlExcpt.getLocalizedMessage());
			sqlExcpt.printStackTrace();
		}
	}

	/**
	 * Iterates through response.
	 * @param map - Map<String, LCSLifeCycleManaged>
	 * @param divUpdateRes - GetDivisionUpdatesRequestResponse
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws WTException - WTException
	 */
	public static void iterateThroughResponse(
			Map<String, LCSLifecycleManaged> map,
			GetDivisionUpdatesRequestResponse divUpdateRes)
			throws WTPropertyVetoException, WTException {
		List<Division> divisionList = divUpdateRes.getDivision();
		//Setting total count of objects
		setTotalCount(divisionList.size());
		//Iterating through responses
		for(Division division : divisionList){
			//Validates the response for missing fields
			processingResponse(map, division);
		}
	}

	
		
	/**
	 * Processes the response.
	 * @param map - Map<String, LCSLifeCycleManaged>
	 * @param division - Division
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws WTException - WTException
	 */
	private static void processingResponse(Map<String, LCSLifecycleManaged> map,
			Division division) throws WTPropertyVetoException, WTException {
		//printing request Type
		LOGGER.debug("REQUEST TYPE #################################   "+division.getRequestType().value());
		if(SMSubDivisionTreeUtil.validateResponse(division)){
			if(map.containsKey(division.getMdmBO()) && SMInboundWebserviceConstants.UPDATE_REQUEST.equals(division.getRequestType().value())){
				//call update
				LOGGER.info("Division Request Type >>>\tUPDATE");
				SMSubDivisionTreeDataProcessor.updateSubDivisionTreeObject(map.get(division.getMdmBO()),division);
			}else if(!(map.containsKey(division.getMdmBO())) && SMInboundWebserviceConstants.UPDATE_REQUEST.equals(division.getRequestType().value())){
				//call create
				LOGGER.info("Division Request Type >>>\tUPDATE");
				SMSubDivisionTreeDataProcessor.createSubDivisionTreeObject(division);
			}else if(map.containsKey(division.getMdmBO()) && SMInboundWebserviceConstants.DELETE_REQUEST.equals(division.getRequestType().value())){
				//call delete
				LOGGER.info("Division Request Type >>>\tDELETE");
				SMSubDivisionTreeDataProcessor.deleteSubDivisionTree(map.get(division.getMdmBO()), division);
			}else{
				//MDM ID is invalid
				SMSubDivisionTreeDataProcessor.setFailedCount(SMSubDivisionTreeDataProcessor.getFailedCount() + 1);//Increment failed count
				SMSubDivisionTreeDataProcessor.setStrErrorString("INVALID MDM ID !!  >>>>\t"  +division.getMdmBO());//Set Error Reason
				//Setting Failed Status
				SMSubDivisionTreeDataProcessor.setINTEGRATIONSTATUS(SMInboundWebserviceConstants.STATUS_NOT_INTEGRATED);
				LOGGER.error(SMSubDivisionTreeDataProcessor.getStrErrorString());//Logging error
				//Set Log Entry
				SMSubDivisionLogEntryProcessor.setLogEntry(null, division);
			}
		}
	}

	/**
	 * Gets the total objects in response.
	 * @return totalCount - Integer
	 */
	public static int getTotalCount() {
		return totalCount;
	}

	/**
	 * Set the total count of objects in response.
	 * @param totalCount - Integer
	 */
	public static void setTotalCount(int totalCount) {
		SMSubdivisionDataRequestWebClient.totalCount = totalCount;
	}

	/**
	 * Returns the Request ID.
	 * @return requestID - String
	 */
	public static String getRequestID() {
		return requestID;
	}

	/**
	 * Set value of Request ID.
	 * @param requestID - String
	 */
	public static void setRequestID(String requestID) {
		SMSubdivisionDataRequestWebClient.requestID = requestID;
	}
	
}

