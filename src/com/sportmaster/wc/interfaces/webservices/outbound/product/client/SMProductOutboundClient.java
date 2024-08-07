package com.sportmaster.wc.interfaces.webservices.outbound.product.client;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.Colorway;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwayInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwayInformationUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLink;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.Product;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductInformationUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLink;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationUpdatesRequestResponse;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

import wt.util.WTException;

/**
 * @author BSC.
 *
 */
public class SMProductOutboundClient {

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductOutboundClient.class);

	public void productOutboundRequest(Object triggerObj, boolean flag, SMProductOutboundIntegrationBean bean){
		String errorMsg;
		try{
			bean.setUpdateRequest(flag);
			LOGGER.debug("####################    STARTING  PRODUCT  OUTBOUND  CLIENT    #################");

			//check for LCSProduct.
			if(triggerObj instanceof LCSProduct){
				//trigger request for product.
				triggerOutboundRequestForProduct(triggerObj, flag, bean);

				//check for Colorway.
			}else if(triggerObj instanceof LCSSKU){
				//trigger request for Colorway.
				triggerOutboundRequestForColorway(triggerObj, bean);
				
			}else if(triggerObj instanceof LCSProductSeasonLink){
				//trigger request for Product Season link.
				triggerOutboundRequestForProductSeasonLink(triggerObj, flag, bean);

			}else if(triggerObj instanceof LCSSKUSeasonLink){

				//trigger for Colorway Season Link.
				triggerOutboundRequestForColorwaySeason(triggerObj, bean);
			}

		}catch(ClientTransportException clientExc){
			errorMsg = clientExc.getLocalizedMessage();
			bean.setResponseErrorReason(errorMsg);
			LOGGER.error(SMOutboundWebServiceConstants.OUTBOUND_CONNECTIVITY_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE
					+ SMProductOutboundWebServiceConstants.PRODUCT_OUTBOUND_INTEGRATION_SCHEDULE_QUEUE
					+ SMInboundWebserviceConstants.CLIENT_TRANSPORT_ERROR_MESSAGE + errorMsg, clientExc);
		}catch (ServerSOAPFaultException serverSoapEXP) {
			errorMsg = serverSoapEXP.getLocalizedMessage();
			bean.setResponseErrorReason(errorMsg);
			LOGGER.error(SMOutboundWebServiceConstants.OUTBOUND_SCHEMA_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_SCHEMA_ERROR_MESSAGE
					+ SMProductOutboundWebServiceConstants.PRODUCT_OUTBOUND_INTEGRATION_SCHEDULE_QUEUE
					+ SMInboundWebserviceConstants.SERVER_SOAP_FAULT_ERROR_MESSAGE + errorMsg, serverSoapEXP);
		}catch(SOAPFaultException soapExp){
			errorMsg = soapExp.getLocalizedMessage();
			bean.setResponseErrorReason(errorMsg);
			LOGGER.error(SMOutboundWebServiceConstants.OUTBOUND_CONNECTIVITY_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_CONNECTIVITY_ERROR_MESSAGE
					+ SMProductOutboundWebServiceConstants.PRODUCT_OUTBOUND_INTEGRATION_SCHEDULE_QUEUE
					+ SMInboundWebserviceConstants.SOAP_FAULT_ERROR_MESSAGE + errorMsg, soapExp);
		}catch(WebServiceException webSrcvExcp){
			errorMsg = webSrcvExcp.getLocalizedMessage();
			bean.setResponseErrorReason(errorMsg);
			LOGGER.error(SMOutboundWebServiceConstants.OUTBOUND_RESPONSE_TIMEOUT_ERROR_CODE
					+ SMInboundWebserviceConstants.WEBSERVICE_COMMON_TIMEOUT_ERROR_MESSAGE
					+ SMProductOutboundWebServiceConstants.PRODUCT_OUTBOUND_INTEGRATION_SCHEDULE_QUEUE
					+ SMInboundWebserviceConstants.WEB_SERVICE_TIMEOUT_ERROR_MESSAGE + errorMsg, webSrcvExcp);

		}catch(WTException expt){ 

			LOGGER.error(expt.getLocalizedMessage(), expt);

		}
	}

	/**
	 * Trigger request for Colorway Season.
	 * @param triggerObj - Object.
	 * @param prodOutboundWS - ProductWS.
	 * @throws WTException - WTException.
	 */
	public void triggerOutboundRequestForColorwaySeason(Object triggerObj, SMProductOutboundIntegrationBean bean)
			throws WTException {
		//check sku season link.
		LCSSKUSeasonLink ssl = (LCSSKUSeasonLink) triggerObj;
		LOGGER.debug("Processing colorway season bean ........");
		ColorwaySeasonLink colorwaySeasonBeanReturned;
		//check colorway season request object.
		ColorwaySeasonLinkInformationUpdatesRequest skuSeasonLinkOutBoundRequest = new ColorwaySeasonLinkInformationUpdatesRequest();

		skuSeasonLinkOutBoundRequest.setColorwaySeasonLink(new ColorwaySeasonLink());
		//check colorway season link object.
		ColorwaySeasonLink skuSeasonLinkRequestObj = skuSeasonLinkOutBoundRequest.getColorwaySeasonLink();

		boolean anotherFlag = false;

		//set request ID for skuSeasonLinkOutBoundRequest.
		bean.setColorwaySeasonOutboundRequestID(bean.getProdUtill().generateColorwaySeasonOutboundIntegrationRequestID());

		LOGGER.debug("SKU Season Request ID  >>>>>  "+bean.getColorwaySeasonOutboundRequestID());
		//set data on bean.SMProductOutboundDataProcessor
		colorwaySeasonBeanReturned = bean.getProdProcessor().setDataForColorwaySeasonLinkOutboundRequest(ssl,
				skuSeasonLinkRequestObj, bean);

		skuSeasonLinkOutBoundRequest.setRequestId(bean.getColorwaySeasonOutboundRequestID());

		//set fake mdm id.
		if(!FormatHelper.hasContent((String) ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_MDM_ID))){
			LOGGER.info("Setting FAKE MDM for Colorway Season Link #############");
			bean.setCreate(true);
			anotherFlag = true;
			skuSeasonLinkRequestObj.setMdmId(SMProductOutboundWebServiceConstants.FAKE_MDM_ID);
		}
		//check if xml generation is enabled.
		if(SMProductOutboundWebServiceConstants.GENERATE_XML_FOR_RESPONSE && null != colorwaySeasonBeanReturned){
			//generate xml.
			bean.getXmlUtill().generateXMLFileForColorwaySeasonLinkOutboundRequest(skuSeasonLinkOutBoundRequest,
					SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_XML_TYPE);
		}


		LOGGER.info("CREATE FLAG <SKU Season>   #####################  "+bean.isCreate());
		LOGGER.info("ANOTHER FLAG <SKU Season>  #####################  "+anotherFlag);

		//Sending Request.
		if(null != colorwaySeasonBeanReturned){
			ColorwaySeasonLinkInformationUpdatesRequestResponse skuSeasonLinkOutboundResponse = bean.getProdOutboundWS()
					.colorwaySeasonLinkInformationUpdatesRequest(skuSeasonLinkOutBoundRequest);

			LOGGER.debug("Colorway Season response ::::::::::::::");
			LOGGER.debug("Received MDM ID  >>>>>  "+skuSeasonLinkOutboundResponse.getMdmId());
			LOGGER.debug("Received Integration Status  >>>>>  "+skuSeasonLinkOutboundResponse.isIntegrationStatus());
			LOGGER.debug("Received Error Message in Response  >>>>>   "+skuSeasonLinkOutboundResponse.getErrorMessage());

			bean.setResponseErrorReason(skuSeasonLinkOutboundResponse.getErrorMessage());

			LOGGER.info("ANOTHER FLAG <SKU Season>  #####################  "+anotherFlag);
			//process response
			processProductOutboundIntegrationResponse(skuSeasonLinkOutboundResponse, anotherFlag, ssl, false, bean);

		}else{
			LOGGER.error("NOT SENDING COLORWAY SEASON LINK REQUEST DUE TO VALIDATION FAILURE. PLEASE CHECK LOG ENTRY OBJECT FOR DETAILS");
		}
	}

	/**
	 * Trigger request for Product Season.
	 * @param triggerObj - Object.
	 * @param prodOutboundWS - ProductWS.
	 * @throws WTException - WTException.
	 */
	public void triggerOutboundRequestForProductSeasonLink(Object triggerObj, boolean flag,
			SMProductOutboundIntegrationBean bean) throws WTException {
		//check for LCSProduct season link object.
		LCSProductSeasonLink psl = (LCSProductSeasonLink) triggerObj;
		LOGGER.debug("Processing product season bean ........");
		ProductSeasonLink prodSeasonLinkBeanReturned;
		//product season link request object.
		ProductSeasonLinkInformationUpdatesRequest prodSeasonLinkOutBoundRequest = new ProductSeasonLinkInformationUpdatesRequest();

		prodSeasonLinkOutBoundRequest.setProductSeasonLink(new ProductSeasonLink());
		//product seaon link object.
		ProductSeasonLink prodSeasonLinkRequestObj = prodSeasonLinkOutBoundRequest.getProductSeasonLink();

		//set request ID for prodSeasonLinkOutBoundRequest.
		bean.setProductSeasonOutboundRequestID(bean.getProdUtill().generateProductSeasonOutboundIntegrationRequestID());

		LOGGER.debug("Request ID for Product Season >>>>>  "+bean.getProductSeasonOutboundRequestID());
		//set data on product season bean.
		prodSeasonLinkBeanReturned = bean.getProdProcessor().setDataForProductSeasonLinkOutboundRequest(psl,
				prodSeasonLinkRequestObj, bean);

		prodSeasonLinkOutBoundRequest.setRequestId(bean.getProductSeasonOutboundRequestID());

		//set fake mdm id.
		if(!FormatHelper.hasContent((String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID))){
			bean.setCreate(true);
			prodSeasonLinkRequestObj.setMdmId(SMProductOutboundWebServiceConstants.FAKE_MDM_ID);
		}
		if(flag && FormatHelper.hasContent((String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID))){
			prodSeasonLinkRequestObj.setLifeCycleState(bean.getLifecycleState());
		}
		//check if XML generation is set.
		if(SMProductOutboundWebServiceConstants.GENERATE_XML_FOR_RESPONSE && null != prodSeasonLinkBeanReturned){
			//generate XML file.
			bean.getXmlUtill().generateXMLFileForProductSeasonLinkOutboundRequest(prodSeasonLinkOutBoundRequest,
					SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_XML_TYPE);
		}

		//Sending Request.
		if(null != prodSeasonLinkBeanReturned){
			// Phase 14 - EMP-490 - Start
			// If more than one PSD exist on Product Season, do not integrate.
			boolean sendData = true; 
			String psdPLMID = prodSeasonLinkBeanReturned.getSmPLMIDSIZ();
			LOGGER.debug("-- psdPLMID to check Multiple PSD --"+psdPLMID);
			LOGGER.debug("-- psdPLMID to check Multiple PSD prodSeasonLinkOutBoundRequest.getProductSeasonLink().getSmPLMIDSIZ()----"+prodSeasonLinkOutBoundRequest.getProductSeasonLink().getSmPLMIDSIZ());

			LOGGER.debug("--prodSeasonLinkRequestObj.getSmPLMIDSIZ()---"+prodSeasonLinkRequestObj.getSmPLMIDSIZ());
			
			if(FormatHelper.hasContent(psdPLMID) && "MULTIPLE_PSDs_FOUND_FOR_PRODUCT".equalsIgnoreCase(psdPLMID)){
				sendData = false;
				LOGGER.debug("-- DATA NOT SENT TO DOWNSTREAM --");
				
			}
			if(sendData){
			// Phase 14 - EMP-490 - End
				ProductSeasonLinkInformationUpdatesRequestResponse prodSeasonLinkOutboundResponse = bean.getProdOutboundWS()
						.productSeasonLinkInformationUpdatesRequest(prodSeasonLinkOutBoundRequest);
				LOGGER.debug("Product Season response ::::::::::::::");
				LOGGER.debug("Received MDM ID  --->   "+prodSeasonLinkOutboundResponse.getMdmId());
				LOGGER.debug("Received Integration Status  ---->>>  "+prodSeasonLinkOutboundResponse.isIntegrationStatus());
				LOGGER.debug("Received Error Message in Response ---->>>   "+prodSeasonLinkOutboundResponse.getErrorMessage());
	
				bean.setResponseErrorReason(prodSeasonLinkOutboundResponse.getErrorMessage());
	
				LOGGER.info("CREATE <PRODUCT SEASON LINK>    ###################  "+bean.isCreate());
				//process response.
				processProductOutboundIntegrationResponse(prodSeasonLinkOutboundResponse, bean.isCreate(), psl, false, bean);
				
			}
		}else{
			LOGGER.error("NOT SENDING PRODUCT SEASON LINK REQUEST DUE TO VALIDATION FAILURE. PLEASE CHECK LOG ENTRY OBJECT FOR DETAILS");
		}
	}

	/**
	 * Trigger request for Colorway.
	 * @param triggerObj - Object.
	 * @param prodOutboundWS - ProductWS.
	 * @throws WTException - WTException.
	 */
	public void triggerOutboundRequestForColorway(Object triggerObj, SMProductOutboundIntegrationBean bean)
			throws WTException {

		//check if object is LCSSKU.
		LCSSKU sku = (LCSSKU) triggerObj;
		LOGGER.debug("Processing Colorway bean ........");
		Colorway colorwayBeanObjectReturned;
		//colorway request.
		ColorwayInformationUpdatesRequest skuOutBoundRequest = new ColorwayInformationUpdatesRequest();

		skuOutBoundRequest.setColorway(new Colorway());
		//colorway object.
		Colorway skuRequestObj = skuOutBoundRequest.getColorway();

		boolean anotherFlag = false;

		//set request id.
		bean.setColorwayOutboundRequestID(bean.getProdUtill().generateColorwayOutboundIntegrationRequestID());

		LOGGER.debug("Request ID for Colorway  >>>> "+bean.getColorwayOutboundRequestID());
		//set data on colorway bean.
		colorwayBeanObjectReturned = bean.getProdProcessor().setDataForColorwayOutboundRequest(sku,skuRequestObj, bean);

		skuOutBoundRequest.setRequestId(bean.getColorwayOutboundRequestID());

		LOGGER.info("CREATE FLAG <COLORWAY>   =============>   "+bean.isCreate());

		//set fake mdm id.
		if(!FormatHelper.hasContent((String) sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID))){
			LOGGER.info("Setting FAKE MDM for Coloway ##################");
			bean.setCreate(true);
			anotherFlag = true;
			skuRequestObj.setMdmId(SMProductOutboundWebServiceConstants.FAKE_MDM_ID);
		}
		//check if XML generation is turned on.
		if(SMProductOutboundWebServiceConstants.GENERATE_XML_FOR_RESPONSE && null != colorwayBeanObjectReturned){
			//generate XML file.
			bean.getXmlUtill().generateXMLFileForColorwayOutboundRequest(skuOutBoundRequest, SMProductOutboundWebServiceConstants.COLORWAY_XML_TYPE);
		}

		LOGGER.info("CREATE FLAG  <COLORWAY>  ###############  "+bean.isCreate());
		LOGGER.info("ANOTHER FLAG  <COLORWAY>  ###############  "+anotherFlag);


		//Sending Request.
		if(null != colorwayBeanObjectReturned){
			ColorwayInformationUpdatesRequestResponse skuOutboundResponse = bean.getProdOutboundWS()
					.colorwayInformationUpdatesRequest(skuOutBoundRequest);
			LOGGER.debug("Colorway response ::::::::::::::");
			LOGGER.debug("Received MDM ID  :::::>>>  "+skuOutboundResponse.getMdmId());
			LOGGER.debug("Received Integration Status  ::::>>>  "+skuOutboundResponse.isIntegrationStatus());
			LOGGER.debug("Received Error Message in Response  :::::>>>   "+skuOutboundResponse.getErrorMessage());

			bean.setResponseErrorReason(skuOutboundResponse.getErrorMessage());

			LOGGER.info("ANOTHER FLAG  <COLORWAY>  ###############  "+anotherFlag);
			//process response.
			processProductOutboundIntegrationResponse(skuOutboundResponse, anotherFlag, sku, false, bean);
		}else{
			LOGGER.error("NOT SENDING COLORWAY REQUEST DUE TO VALIDATION FAILURE. PLEASE CHECK LOG ENTRY OBJECT FOR DETAILS");
		}
	}

	/**
	 * Trigger request for Product.
	 * @param triggerObj - Object.
	 * @param prodOutboundWS - ProductWS.
	 * @throws WTException - WTException.
	 */
	public void triggerOutboundRequestForProduct(Object triggerObj, boolean flag, SMProductOutboundIntegrationBean bean)
			throws WTException {
		LCSProduct product = (LCSProduct) triggerObj;
		LOGGER.debug("Processing product bean ........");
		//product request.
		ProductInformationUpdatesRequest prodOutBoundRequest = new ProductInformationUpdatesRequest();
		Product prodRequestObj = new Product();
		prodOutBoundRequest.setProduct(prodRequestObj);
		Product prodBeanReturned;

		boolean isUpdatePSL = false;
		bean.setUpdateProductSeasonLink(false);

		bean.setProductCancelledFlag(true);

		LOGGER.info("FLAG      >>>>>>>>>>>>    "+flag);

		boolean anotherFlag = false;

		//set request ID for prodRequestObj.
		bean.setProductOutboundRequestID(bean.getProdUtill().generateProductOutboundIntegrationRequestID());
		LOGGER.debug("REQUEST ID FOR PRODUCT  >>>  "+bean.getProductOutboundRequestID());
		if(flag){
			isUpdatePSL = true;
			bean.setUpdateProductSeasonLink(true);
			prodBeanReturned = bean.getProdProcessor().setDataForProductOutboundRequest(product, prodRequestObj,
					bean.getProductSeasonLink(), bean);
			if(!FormatHelper.hasContent((String) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY))){
				prodOutBoundRequest.getProduct().setMdmId(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
			}
		}else{
			bean.setUpdateProductSeasonLink(false);
			prodBeanReturned = bean.getProdProcessor().setDataForProductOutboundRequest(product,prodRequestObj, null, bean);
		}

		//set request ID.
		prodOutBoundRequest.setRequestId(bean.getProductOutboundRequestID());

		//set fake MDM id during create.
		if(!FormatHelper.hasContent((String) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY)) && !flag){
			bean.setCreate(true);
			anotherFlag = true;
			LOGGER.info("Setting FAKE MDM ID for Product Bean......Create Flag  --------->   "+bean.isCreate());
			bean.setProductCancelledFlag(false);
			LOGGER.info("Setting FAKE MDM ID  ......... Cancelled Flag Value  ------>     "+bean.isProductCancelledFlag());
			prodRequestObj.setMdmId(SMProductOutboundWebServiceConstants.FAKE_MDM_ID);
		}
		LOGGER.debug("before generatinf xml : " + SMProductOutboundWebServiceConstants.GENERATE_XML_FOR_RESPONSE);
		LOGGER.debug("before generatinf xml prodBeanReturned: " + prodBeanReturned);
		//check if XML generation is enabled.
		if(SMProductOutboundWebServiceConstants.GENERATE_XML_FOR_RESPONSE && null != prodBeanReturned){
			LOGGER.debug("calling " + bean.getXmlUtill() + " to geneate xml file");
			//generate XML.
			bean.getXmlUtill().generateXMLFileForProductOutboundRequest(prodOutBoundRequest, SMProductOutboundWebServiceConstants.PRODUCT_XML_TYPE);
		}

		LOGGER.info("CREATE FLAG <PROD>   #####################  "+bean.isCreate());
		LOGGER.info("ANOTHER FLAG <PROD>  #####################  "+anotherFlag);
		//Sending Request.
		if(null != prodBeanReturned){
			// Phase 14 - added for multiple size logic check and not to send xml to downstream - start
			boolean sendData = true;
			sendData = isValidDataForIntegration(prodBeanReturned, sendData);
			if(sendData) {
				// Phase 14 - added for multiple size logic check and not to send xml to downstream - end
				ProductInformationUpdatesRequestResponse prodOutboundResponse = bean.getProdOutboundWS()
						.productInformationUpdatesRequest(prodOutBoundRequest);
				LOGGER.debug("Product response ::::::::::::::");
				LOGGER.debug("Received MDM ID  >>>  "+prodOutboundResponse.getMdmId());
				LOGGER.debug("Received Integration Status  >>>  "+prodOutboundResponse.isIntegrationStatus());
				LOGGER.debug("Received Error Message in Response  >>>   "+prodOutboundResponse.getErrorMessage());
	
	
				//setting error reason.
				bean.setResponseErrorReason(prodOutboundResponse.getErrorMessage());
	
				LOGGER.info("ANOTHER FLAG <PROD>  #####################  "+anotherFlag);
				//process product outbound response.
				processProductOutboundIntegrationResponse(prodOutboundResponse, anotherFlag, product, isUpdatePSL, bean);
			}
		}else{
			LOGGER.error("NOT SENDING PRODUCT REQUEST DUE TO VALIDATION FAILURE. PLEASE CHECK LOG ENTRY OBJECT FOR DETAILS");
		}
	}

	/**
	 * @param prodBeanReturned
	 * @param sendData
	 * @return
	 */
	private boolean isValidDataForIntegration(Product prodBeanReturned, boolean sendData) {
		if(prodBeanReturned.getProductSeasonLink() != null && !prodBeanReturned.getProductSeasonLink().isEmpty()) {
			ProductSeasonLink psl = prodBeanReturned.getProductSeasonLink().get(0);
			if(psl != null && ("MULTIPLE_PSDs_FOUND_FOR_PRODUCT").equals(psl.getSmPLMIDSIZ())) {
				sendData = false;
			}
		}
		return sendData;
	}
	/**
	 * Start processing repsonse from Webservice.
	 * @param responseObj - Object.
	 * @param isCreate - boolean.
	 * @param dataObject - Object.
	 * @param updatePSL - boolean.
	 */
	public void processProductOutboundIntegrationResponse(Object responseObj, boolean isCreate, Object dataObject,
			boolean updatePSL, SMProductOutboundIntegrationBean bean) {

		SMProductOutboundResponseProcessor responseProcessor = new SMProductOutboundResponseProcessor();
		try{
			//check if response is of type product.
			if(responseObj instanceof ProductInformationUpdatesRequestResponse){
				//process product outbound response.
				responseProcessor.processProductOutboundResponse(responseObj, dataObject, updatePSL, isCreate, bean);

				//response is Colorway Response.
			}else if(responseObj instanceof ColorwayInformationUpdatesRequestResponse){
				//process colorway outbound response
				responseProcessor.processColorwayOutboundResponse(responseObj, dataObject, isCreate, bean);

				//check if response is for Product Season link.
			}else if(responseObj instanceof ProductSeasonLinkInformationUpdatesRequestResponse){
				//process product season response.
				responseProcessor.processProductSeasonLinkOutboundResponse(responseObj, dataObject, bean);

				//check for Colorway Season link.
			}else if(responseObj instanceof ColorwaySeasonLinkInformationUpdatesRequestResponse){
				//process colorway season response.
				responseProcessor.processColorwaySeasonLinkOutboundResponse(responseObj, dataObject, isCreate, bean);
			}
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage(), we);

		} catch (wt.util.WTPropertyVetoException e) {
			LOGGER.error(e.getLocalizedMessage(), e);

		}
	}

}
