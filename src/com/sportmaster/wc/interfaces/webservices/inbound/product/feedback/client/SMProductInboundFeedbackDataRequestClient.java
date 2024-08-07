/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.inbound.product.feedback.client;

import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.sportmaster.wc.interfaces.webservices.inbound.product.feedback.processor.SMProductInboundFeedbackLogEntryProcessor;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundUtil;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationStatus;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationStatusItem;
import com.sportmaster.wc.interfaces.webservices.productbean.GetStatusProductRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.GetStatusProductRequestResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductEndpointService;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationStatus;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationStatusItem;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductWS;
import com.sun.xml.ws.client.BindingProviderProperties;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

/**
 * @author Carrier
 *
 */
public class SMProductInboundFeedbackDataRequestClient {

	private static final String ERROR_OCCURED_CONST = "ERROR OCCURED :--";
	/**
	 * THE LOGGER.
	 */
	private static final Logger LOGGER=Logger.getLogger(SMProductInboundFeedbackDataRequestClient.class);
	/**
	 * Request ID.
	 */
	private static int feedbackRequestID;
	/**
	 * Error Message
	 */
	private static String feedbackErrorMessage;
	/**
	 * Feedback Failed Count.
	 */
	private static int feedbackFailedCount;
	/**
	 * Constructor.
	 */
	protected SMProductInboundFeedbackDataRequestClient(){
		//protected constructor
	}

	/**
	 * feedback processor.
	 */
	public static void processProductInboundFeedbackData(){
		try{
			LOGGER.debug("#################       START OF PRODUCT INBOUND INTEGRATION  FEEDABCK CLIENT    ###############");

			ProductEndpointService prodFeedbackEndPointService = new ProductEndpointService();

			ProductWS productFeedbckWS = prodFeedbackEndPointService.getProductEndpointPort();

			GetStatusProductRequest prodStatusRequest = new GetStatusProductRequest();

			//Setting Request ID in request.
			prodStatusRequest.setRequestId(new SMProductInboundUtil().getProductInboundRequestID());

			setFeedbackRequestID(prodStatusRequest.getRequestId());

			//Set timeout until the response is received
			((BindingProvider)productFeedbckWS).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, SMProductInboundWebServiceConstants.PRODUCT_INBOUND_TIMEOUT_IN_MINUTES*60*1000);

			//setting feedback request for Product-season
			ProductSeasonLinkInformationStatus prodSeasonStatusResponse = SMProductInboundFeedbackLogEntryProcessor.processLogEntryForProductFeedbackStatus();

			//setting feedback request for SKU-Season
			ColorwaySeasonLinkInformationStatus colorwaySeasonStatusResponse = SMProductInboundFeedbackLogEntryProcessor.processLogEntryForColorwayFeedbackStatus();

			//check for null
			if(null != prodSeasonStatusResponse){
				LOGGER.info("product season inbound feedback request details :::::::::::: ");
				//display response items.
				logProductSeasonFeedbackResponse(prodSeasonStatusResponse);
				//set product season link status
				prodStatusRequest.setProductSeasonLinkInformationStatus(prodSeasonStatusResponse);
			}
			//check for null
			if(null != colorwaySeasonStatusResponse){
				LOGGER.info("colorway season inbound feedback request details :::::::::::: ");
				//display response items.
				logColorwaytSeasonFeedbackResponse(colorwaySeasonStatusResponse);

				//set colorway season link status
				prodStatusRequest.setColorwaySeasonLinkInformationStatus(colorwaySeasonStatusResponse);
			}

			LOGGER.debug("Sending Product Inbound Integration Feedback Data to PLM GATE");


			//sending request.
			// if(null != prodStatusRequest){
			// get response object.
			GetStatusProductRequestResponse prodFeedbackResponse = productFeedbckWS.getStatusProductRequest(prodStatusRequest);

			// Getting Request ID from Response.
			setFeedbackRequestID(prodFeedbackResponse.getRequestId());

			// Process feedback response.
			SMProductInboundFeedbackLogEntryProcessor.processFeedbackResponse(prodFeedbackResponse);
			// }

			LOGGER.debug("###############   PRODUCT INBOUND INTEGARTION STATUS FEEDBACK ENDS   ##############");

		}catch(ClientTransportException clientExc){
			setFeedbackErrorMessage(clientExc.getLocalizedMessage());
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_CONNECTIVITY_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_FEEDBACK_INTEGRATION+SMInboundWebserviceConstants.CLIENT_TRANSPORT_ERROR_MESSAGE+getFeedbackErrorMessage());
			LOGGER.error(ERROR_OCCURED_CONST, clientExc);
		}catch (ServerSOAPFaultException serverSoapEXP) {
			setFeedbackErrorMessage(serverSoapEXP.getLocalizedMessage());
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_SCHEMA_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_SCHEMA_ERROR_MESSAGE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_FEEDBACK_INTEGRATION+SMInboundWebserviceConstants.SERVER_SOAP_FAULT_ERROR_MESSAGE+getFeedbackErrorMessage());
			LOGGER.error(ERROR_OCCURED_CONST, serverSoapEXP);
		}catch(SOAPFaultException soapExp){
			setFeedbackErrorMessage(soapExp.getLocalizedMessage());
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_CONNECTIVITY_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_FEEDBACK_INTEGRATION+SMInboundWebserviceConstants.SOAP_FAULT_ERROR_MESSAGE+getFeedbackErrorMessage());
			LOGGER.error(ERROR_OCCURED_CONST, soapExp);
		}catch(WebServiceException webSrcvExcp){
			setFeedbackErrorMessage(webSrcvExcp.getLocalizedMessage());
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_TIMEOUT_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_FEEDBACK_INTEGRATION+SMInboundWebserviceConstants.WEB_SERVICE_TIMEOUT_ERROR_MESSAGE+getFeedbackErrorMessage());
			LOGGER.error(ERROR_OCCURED_CONST, webSrcvExcp);
		} catch (WTException wtExcpt) {
			setFeedbackErrorMessage(wtExcpt.getLocalizedMessage());
			LOGGER.error(ERROR_OCCURED_CONST, wtExcpt);
		}
	}

	/**
	 * @param colorwaySeasonStatusResponse
	 */
	public static void logColorwaytSeasonFeedbackResponse(
			ColorwaySeasonLinkInformationStatus colorwaySeasonStatusResponse) {
		List<ColorwaySeasonLinkInformationStatusItem> skuFeedbackList = colorwaySeasonStatusResponse.getColorwaySeasonLinkInformationStatusItem();
		if (null != skuFeedbackList && !skuFeedbackList.isEmpty()) {
			//iterating through colorway season status item list.
			for(ColorwaySeasonLinkInformationStatusItem skuFeedbackItem : skuFeedbackList){
				LOGGER.info("SKU Feedback Item MDM ID >>> "+skuFeedbackItem.getMdmId());
				LOGGER.info("SKU Feedback Item PLM ID >>> "+skuFeedbackItem.getPlmId());
				LOGGER.info("SKU Feedback Item Request Status >>> "+skuFeedbackItem.getRequestStatus());
			}
		}
	}

	/**
	 * @param prodSeasonStatusResponse
	 */
	public static void logProductSeasonFeedbackResponse(
			ProductSeasonLinkInformationStatus prodSeasonStatusResponse) {
		List<ProductSeasonLinkInformationStatusItem> prodFeedbackList = prodSeasonStatusResponse.getProductSeasonLinkInformationStatusItem();
		if (null != prodFeedbackList && !prodFeedbackList.isEmpty()) {
			//iterating through prod season status list item.
			for(ProductSeasonLinkInformationStatusItem prodFeedbackItem : prodFeedbackList){
				LOGGER.info("Prod Feedback Item  MDM ID >>>>  "+prodFeedbackItem.getMdmId());
				LOGGER.info("Prod Feedback Item  PLM ID >>>>  "+prodFeedbackItem.getPlmId());
				LOGGER.info("Prod Feedback Item  Request Status >>>>  "+prodFeedbackItem.getRequestStatus());
			}
		}
	}

	/**
	 * @return the feedbackRequestID
	 */
	public static int getFeedbackRequestID() {
		return feedbackRequestID;
	}

	/**
	 * @param feedbackRequestID the feedbackRequestID to set
	 */
	public static void setFeedbackRequestID(int feedbackID) {
		feedbackRequestID = feedbackID;
	}

	/**
	 * @return the feedbackErrorMessage
	 */
	public static String getFeedbackErrorMessage() {
		return feedbackErrorMessage;
	}

	/**
	 * @param feedbackErrorMessage the feedbackErrorMessage to set
	 */
	public static void setFeedbackErrorMessage(String errorMsg) {
		feedbackErrorMessage = errorMsg;
	}

	/**
	 * @return the feedbackFailedCount
	 */
	public static int getFeedbackFailedCount() {
		return feedbackFailedCount;
	}

	/**
	 * @param feedbackFailedCount the feedbackFailedCount to set
	 */
	public static void setFeedbackFailedCount(int feedbackFailedCount) {
		SMProductInboundFeedbackDataRequestClient.feedbackFailedCount = feedbackFailedCount;
	}
}
