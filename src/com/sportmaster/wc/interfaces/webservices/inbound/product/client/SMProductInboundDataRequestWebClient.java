/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.inbound.product.client;

import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.sportmaster.wc.interfaces.webservices.inbound.product.helper.SMProductInboundHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.product.processor.SMProductInboundDataProcessor;
import com.sportmaster.wc.interfaces.webservices.inbound.product.processor.SMProductInboundLogEntryProcessor;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundUtil;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformation;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationItem;
import com.sportmaster.wc.interfaces.webservices.productbean.GetProductUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.GetProductUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductEndpointService;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformation;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationItem;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductWS;
import com.sun.xml.ws.client.BindingProviderProperties;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

import wt.util.WTException;


/**
 * @author ITC_Infotech.
 *
 */
public class SMProductInboundDataRequestWebClient {

	/**
	 * ERROR_OCCURED.
	 */
	private static final String ERROR_OCCURED = "Error Occured :-";

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductInboundDataRequestWebClient.class);

	/**
	 * Total number of records received.
	 */
	private static int totalCountProductInbound;

	/**
	 * Total number of records received.
	 */
	private static int totalCountColorwayInbound;
	/**
	 * Getting Product Request ID from Response.
	 */
	private static int productRequestID;
	/**
	 * Failed flag for Product Season Link.
	 */
	private static boolean integartionFailurePSL;
	/**
	 * Failed flag for SKU Season Link.
	 */
	private static boolean integrationFailureSSL;
	/**
	 * Error message.
	 */
	private static String errorMsg;

	/**
	 * Protected constructor.
	 */
	protected SMProductInboundDataRequestWebClient(){
		//protected constructor.
	}

	/**
	 * Send Request for Product Inbound Integration.
	 */
	public static void productInboundRequest(){
		try{
			LOGGER.debug("##############   STARTING PRODUCT INBOUND INTEGRATION CLIENT  ##############");
			setIntegartionFailurePSL(false);
			setIntegrationFailureSSL(false);

			ProductEndpointService productEndpointService = new ProductEndpointService();

			ProductWS productWS = productEndpointService.getProductEndpointPort();

			GetProductUpdatesRequest productUpdatesRequest = new GetProductUpdatesRequest();

			int requestIDInRequest = new SMProductInboundUtil().getProductInboundRequestID();
			LOGGER.info("Request ID in request for Inbound Integartion  >>>>>>>>>>>>>  "+requestIDInRequest);
			productUpdatesRequest.setRequestId(requestIDInRequest);

			//Setting timeout
			((BindingProvider)productWS).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, SMProductInboundWebServiceConstants.PRODUCT_INBOUND_TIMEOUT_IN_MINUTES*60*1000);

			GetProductUpdatesRequestResponse productUpdatesRequestResponse = productWS.getProductUpdatesRequest(productUpdatesRequest);

			//Setting Request ID.
			setRequestID(requestIDInRequest);
			LOGGER.info("REQUEST ID  ********************   "+getRequestID());

			//Set Product Log Entry to Map. 
			SMProductInboundLogEntryProcessor.setProductSeasonLinkLogEntryCollection(SMProductInboundLogEntryProcessor.queryProductSeasonLinkInboundLogEntry());

			//Set Colorway Log Entry to Map.
			SMProductInboundLogEntryProcessor.setSkuSeasonLinkLogEntryCollection(SMProductInboundLogEntryProcessor.queryColorwaySeasonLinkInboundLogEntry());

			if(null != productUpdatesRequestResponse.getProductSeasonLinkInformation() || null != productUpdatesRequestResponse.getColorwaySeasonLinkInformation()){
				
				//validate product response object.
				List<ProductSeasonLinkInformationItem> productSeasonLinkInformationItem = validateProductInboundResponse(productUpdatesRequestResponse);
				
				//validate colorway response object.
				List<ColorwaySeasonLinkInformationItem> colorwaySeasonLinkInformationItem = validateColorwayInboundResponse(productUpdatesRequestResponse);
				
				//Process Response for Product Inbound Integration.
				processResponse(productSeasonLinkInformationItem, colorwaySeasonLinkInformationItem);
			}

			LOGGER.debug("##############    PRODUCT SEASON INBOUND INTEGARTION ENDS     ##############");

		}catch(ClientTransportException clientExc){
			setErrorMsg(clientExc.getLocalizedMessage());
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_CONNECTIVITY_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_INTEGRATION+SMInboundWebserviceConstants.CLIENT_TRANSPORT_ERROR_MESSAGE+getErrorMsg());
			LOGGER.error(ERROR_OCCURED, clientExc);
		}catch (ServerSOAPFaultException serverSoapEXP) {
			setErrorMsg(serverSoapEXP.getLocalizedMessage());
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_SCHEMA_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_SCHEMA_ERROR_MESSAGE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_INTEGRATION+SMInboundWebserviceConstants.SERVER_SOAP_FAULT_ERROR_MESSAGE+getErrorMsg());
			LOGGER.error(ERROR_OCCURED, serverSoapEXP);
		}catch(SOAPFaultException soapExp){
			setErrorMsg(soapExp.getLocalizedMessage());
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_CONNECTIVITY_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_INTEGRATION+SMInboundWebserviceConstants.SOAP_FAULT_ERROR_MESSAGE+getErrorMsg());
			LOGGER.error(ERROR_OCCURED, soapExp);
		}catch(WebServiceException webSrcvExcp){
			setErrorMsg(webSrcvExcp.getLocalizedMessage());
			LOGGER.error(SMInboundWebserviceConstants.WEBSERVICE_INBOUND_TIMEOUT_ERROR_CODE+SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_INTEGRATION+SMInboundWebserviceConstants.WEB_SERVICE_TIMEOUT_ERROR_MESSAGE+getErrorMsg());
			LOGGER.error(ERROR_OCCURED, webSrcvExcp);
		} catch (WTException wtExcpt) {
			setErrorMsg(wtExcpt.getLocalizedMessage());
			LOGGER.error(ERROR_OCCURED, wtExcpt);
		}
	}

	/**
	 * Validate colorway response.
	 * @param productUpdatesRequestResponse - GetProductUpdatesRequestResponse
	 * @return List<ColorwaySeasonLinkInformationItem>
	 */
	public static List<ColorwaySeasonLinkInformationItem> validateColorwayInboundResponse(
			GetProductUpdatesRequestResponse productUpdatesRequestResponse) {
		ColorwaySeasonLinkInformation colorwaySeasonLinkInformation = productUpdatesRequestResponse.getColorwaySeasonLinkInformation();
		List<ColorwaySeasonLinkInformationItem> colorwaySeasonLinkInformationItem;

		if(null != colorwaySeasonLinkInformation && null != colorwaySeasonLinkInformation.getColorwaySeasonLinkInformationItem()){
			colorwaySeasonLinkInformationItem = colorwaySeasonLinkInformation.getColorwaySeasonLinkInformationItem();
			//Setting Total Colorway Inbound Count.
			setTotalCountColorwayInbound(colorwaySeasonLinkInformationItem.size());
		}else{
			colorwaySeasonLinkInformationItem = null;
		}
		return colorwaySeasonLinkInformationItem;
	}

	/**
	 * validate product response.
	 * @param productUpdatesRequestResponse - GetProductUpdatesRequestResponse
	 * @return - List<ProductSeasonLinkInformationItem>
	 */
	public static List<ProductSeasonLinkInformationItem> validateProductInboundResponse(
			GetProductUpdatesRequestResponse productUpdatesRequestResponse) {
		ProductSeasonLinkInformation productSeasonLinkInformation = productUpdatesRequestResponse.getProductSeasonLinkInformation();
		List<ProductSeasonLinkInformationItem> productSeasonLinkInformationItem;
		if(null != productSeasonLinkInformation && null != productSeasonLinkInformation.getProductSeasonLinkInformationItem()){
			productSeasonLinkInformationItem = productSeasonLinkInformation.getProductSeasonLinkInformationItem();
			//Setting Total Product Inbound Count.

			setTotalCountProductInbound(productSeasonLinkInformationItem.size());
		}else{
			productSeasonLinkInformationItem = null;
		}
		return productSeasonLinkInformationItem;
	}

	/**
	 * Process Response for Inbound Integration.
	 * @param productSeasonLinkInformationItem - ProductSeasonLinkInformationItem
	 * @param colorwaySeasonLinkInformationItem - ColorwaySeasonLinkInformationItem
	 */
	public static void processResponse(
			List<ProductSeasonLinkInformationItem> productSeasonLinkInformationItem,
			List<ColorwaySeasonLinkInformationItem> colorwaySeasonLinkInformationItem) {
		if(null != productSeasonLinkInformationItem){
			for(ProductSeasonLinkInformationItem productInboundList : productSeasonLinkInformationItem){
				//reset error message.
				setErrorMsg("");
				setIntegartionFailurePSL(false);
				//check if XML generation is SET.
				if(SMProductInboundWebServiceConstants.GENERATE_RESPONSE_FOR_INBOUND_DATA){
					//Generate XML.
					SMProductInboundHelper.generateXMLForProductInboundDataRequest(productInboundList);
				}
				//start product inbound data processing
				SMProductInboundDataProcessor.processProductInboundResponse(productInboundList);
			}
		}
		if(null != colorwaySeasonLinkInformationItem){
			for(ColorwaySeasonLinkInformationItem colorwayInboundList : colorwaySeasonLinkInformationItem){
				//reset error message.
				setErrorMsg("");
				setIntegrationFailureSSL(false);
				//check if XML Generation is set.
				if(SMProductInboundWebServiceConstants.GENERATE_RESPONSE_FOR_INBOUND_DATA){
					//Generate XML.
					SMProductInboundHelper.generateXMLForColorwayInboundDataRequest(colorwayInboundList);
				}
				//start colorway inbound data processing
				SMProductInboundDataProcessor.processColorwayInboundResponse(colorwayInboundList);
			}
		}

	}

	/**
	 * @return the tOTAL_COUNT_PRODUCT_INBOUND
	 */
	public static int getTotalCountProductInbound() {
		return totalCountProductInbound;
	}

	/**
	 * @param tOTAL_COUNT_PRODUCT_INBOUND the tOTAL_COUNT_PRODUCT_INBOUND to set
	 */
	public static void setTotalCountProductInbound(
			int prodInboundTotal) {
		totalCountProductInbound = prodInboundTotal;
	}

	/**
	 * @return the tOTAL_COUNT_COLORWAY_INBOUND
	 */
	public static int getTotalCountColorwayInbound() {
		return totalCountColorwayInbound;
	}

	/**
	 * @param tOTAL_COUNT_COLORWAY_INBOUND the tOTAL_COUNT_COLORWAY_INBOUND to set
	 */
	public static void setTotalCountColorwayInbound(
			int colorwayInboundTotal) {
		totalCountColorwayInbound = colorwayInboundTotal;
	}

	/**
	 * @return the rEQUEST_ID
	 */
	public static int getRequestID() {
		return productRequestID;
	}

	/**
	 * @param rEQUEST_ID the rEQUEST_ID to set
	 */
	public static void setRequestID(int reqID) {
		productRequestID = reqID;
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
		SMProductInboundDataRequestWebClient.errorMsg = errorMsg;
	}

	/**
	 * @return the integartionFailurePSL
	 */
	public static boolean isIntegartionFailurePSL() {
		return integartionFailurePSL;
	}

	/**
	 * @param integartionFailurePSL the integartionFailurePSL to set
	 */
	public static void setIntegartionFailurePSL(boolean integartionFailurePSL) {
		SMProductInboundDataRequestWebClient.integartionFailurePSL = integartionFailurePSL;
	}

	/**
	 * @return the integrationFailureSSL
	 */
	public static boolean isIntegrationFailureSSL() {
		return integrationFailureSSL;
	}

	/**
	 * @param integrationFailureSSL the integrationFailureSSL to set
	 */
	public static void setIntegrationFailureSSL(boolean integrationFailureSSL) {
		SMProductInboundDataRequestWebClient.integrationFailureSSL = integrationFailureSSL;
	}
}
