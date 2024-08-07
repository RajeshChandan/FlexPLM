package com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.client;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.util.SMPriceInboundUtil;
import com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.util.SMPricesInboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.GetPricesUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.GetPricesUpdatesResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.GetStatusPricesRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.GetStatusPricesResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductEndpointService;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductWS;
import com.sun.xml.ws.client.BindingProviderProperties;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

import wt.util.WTException;

/**
 * SMPriceUpdateRequestWebClient.java
 * This class handles SOAP request response.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMPriceUpdateRequestWebClient{
	
	
	/**
	 * Constructor.
	 */
	protected SMPriceUpdateRequestWebClient() {
	}
	/*
	 * REQ_LOGGER.
	 */
	public static final Logger REQ_LOGGER = Logger.getLogger("priceUpdateRequest");
	/*
	 * FEEDBACK_LOGGER.
	 */
	public static final Logger FEEDBACK_LOGGER = Logger.getLogger("priceUpdateFeedback");

	/**
	 * Error message.
	 */
	private static String errorMsg;


	/**
	 * Send Request for Price Update request Integration.
	 */
	public static GetPricesUpdatesResponse getPricesUpdateRequestResponse(){
		GetPricesUpdatesResponse pricesUpdatesRequestResponse = null;
		try{
			//service class object.
			ProductEndpointService productEndpointService = new ProductEndpointService();
			
			//endpoint port class object.
			ProductWS productWS = productEndpointService.getProductEndpointPort();
			
			////bean class object.
			GetPricesUpdatesRequest pricesUpdatesRequest = new GetPricesUpdatesRequest();
			
			//generating request id.
			int requestIDInRequest = new SMPriceInboundUtil().getProductInboundRequestID();
			REQ_LOGGER.debug("Request ID in request for Inbound Integartion  >>>>>>>>>>>>>  "+requestIDInRequest);

			//Setting Request ID.
			pricesUpdatesRequest.setRequestId(String.valueOf(requestIDInRequest));

			//Setting timeout
			((BindingProvider)productWS).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, SMPricesInboundWebServiceConstants.PRICE_UPDATE_INBOUND_TIMEOUT_IN_MINUTES*60*1000);
			
			REQ_LOGGER.debug("Sending price update inbound request to PLM Gate with request id:-"+requestIDInRequest);
			//sending price update request.
			pricesUpdatesRequestResponse = productWS.getPricesUpdates(pricesUpdatesRequest);
			REQ_LOGGER.debug("Transcation sucessful between FLEX PLM <-> PLM GATE");
			return pricesUpdatesRequestResponse;

		}catch(ClientTransportException clientExc){
			setErrorMsg(clientExc.getLocalizedMessage());
			//adding logger to log file.
			REQ_LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_CONNECTIVITY_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE
					+ SMPricesInboundWebServiceConstants.PRICES_UPDATE_INBOUND_INTEGRATION
					+ SMInboundWebserviceConstants.CLIENT_TRANSPORT_ERROR_MESSAGE + getErrorMsg(),clientExc);
		}catch (ServerSOAPFaultException serverSoapEXP) {
			setErrorMsg(serverSoapEXP.getLocalizedMessage());
			//adding logger to log file.
			REQ_LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_SCHEMA_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_SCHEMA_ERROR_MESSAGE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE
					+ SMPricesInboundWebServiceConstants.PRICES_UPDATE_INBOUND_INTEGRATION
					+ SMInboundWebserviceConstants.SERVER_SOAP_FAULT_ERROR_MESSAGE + getErrorMsg(),serverSoapEXP);
		}catch(SOAPFaultException soapExp){
			setErrorMsg(soapExp.getLocalizedMessage());
			//adding logger to log file.
			REQ_LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_CONNECTIVITY_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE
					+ SMPricesInboundWebServiceConstants.PRICES_UPDATE_INBOUND_INTEGRATION
					+ SMInboundWebserviceConstants.SOAP_FAULT_ERROR_MESSAGE + getErrorMsg(),soapExp);
		}catch(WebServiceException webSrcvExcp){
			setErrorMsg(webSrcvExcp.getLocalizedMessage());
			//adding logger to log file.
			REQ_LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_TIMEOUT_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE
					+ SMPricesInboundWebServiceConstants.PRICES_UPDATE_INBOUND_INTEGRATION
					+ SMInboundWebserviceConstants.WEB_SERVICE_TIMEOUT_ERROR_MESSAGE + getErrorMsg(),webSrcvExcp);
		} catch (WTException wtExcpt) {
			setErrorMsg(wtExcpt.getLocalizedMessage());
			//adding logger to log file.
			REQ_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,wtExcpt);
		}catch (NumberFormatException numExp) {
			setErrorMsg(numExp.getLocalizedMessage());
			//adding logger to log file.
			REQ_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,numExp);
			
		}
		return pricesUpdatesRequestResponse;
	}

	/**
	 * Send Request for Price Update feedback Integration.
	 * @param getStatusPrices - GetStatusPricesRequest
	 * @return GetStatusPricesResponse
	 */
	public static GetStatusPricesResponse getPricesUpdateStatusRequest(GetStatusPricesRequest getStatusPrices) {
		GetStatusPricesResponse response = null;
		try{
			//service classs object.
			ProductEndpointService productEndpointService = new ProductEndpointService();
			//endpoint port class object.
			ProductWS productWS = productEndpointService.getProductEndpointPort();
			//Setting timeout
			((BindingProvider) productWS).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT,
					SMPricesInboundWebServiceConstants.PRICE_UPDATE_INBOUND_TIMEOUT_IN_MINUTES * 60 * 1000);
			
			FEEDBACK_LOGGER.debug("sennding feed back request to PLM GATE, request id :-"+getStatusPrices.getRequestId());
			//sending feedback request.
			response=productWS.getStatusPrices(getStatusPrices);
			
			FEEDBACK_LOGGER.debug("Recevied response from PLM GATE, request id :-"+getStatusPrices.getRequestId());

		}catch(ClientTransportException statusClientExc){
			setErrorMsg(statusClientExc.getLocalizedMessage());
			//adding logger to log file.
			FEEDBACK_LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_CONNECTIVITY_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE
					+ SMPricesInboundWebServiceConstants.PRICES_UPDATE_INBOUND_INTEGRATION
					+ SMInboundWebserviceConstants.CLIENT_TRANSPORT_ERROR_MESSAGE + getErrorMsg(),statusClientExc);
		}catch (ServerSOAPFaultException statusServerSoapEXP) {
			setErrorMsg(statusServerSoapEXP.getLocalizedMessage());
			//adding logger to log file.
			FEEDBACK_LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_SCHEMA_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_SCHEMA_ERROR_MESSAGE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE
					+ SMPricesInboundWebServiceConstants.PRICES_UPDATE_INBOUND_INTEGRATION
					+ SMInboundWebserviceConstants.SERVER_SOAP_FAULT_ERROR_MESSAGE + getErrorMsg(),statusServerSoapEXP);
		}catch(SOAPFaultException statusSsoapExp){
			setErrorMsg(statusSsoapExp.getLocalizedMessage());
			//adding logger to log file.
			FEEDBACK_LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_CONNECTIVITY_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE
					+ SMPricesInboundWebServiceConstants.PRICES_UPDATE_INBOUND_INTEGRATION
					+ SMInboundWebserviceConstants.SOAP_FAULT_ERROR_MESSAGE + getErrorMsg(),statusSsoapExp);
		}catch(WebServiceException statuWwebSrcvExcp){
			setErrorMsg(statuWwebSrcvExcp.getLocalizedMessage());
			//adding logger to log file.
			FEEDBACK_LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_TIMEOUT_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE
					+ SMPricesInboundWebServiceConstants.PRICES_UPDATE_INBOUND_INTEGRATION
					+ SMInboundWebserviceConstants.WEB_SERVICE_TIMEOUT_ERROR_MESSAGE + getErrorMsg(),statuWwebSrcvExcp);
		} 
		return response;
	}
	/**
	 * @return the errorMsg
	 */
	public static String getErrorMsg() {
		return errorMsg;
	}
	/**
	 * @param errorMsg the errorMsg to set
	 */
	public static void setErrorMsg(String errorMsg) {
		SMPriceUpdateRequestWebClient.errorMsg = errorMsg;
	}



}