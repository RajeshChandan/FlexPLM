package com.sportmaster.wc.interfaces.webservices.outbound.bom.processor;

import java.util.HashMap;
import java.util.Objects;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.lcs.wc.flexbom.FlexBOMPart;
import com.sportmaster.wc.interfaces.webservices.bombean.BOMPart;
import com.sportmaster.wc.interfaces.webservices.bombean.BOMRequest;
import com.sportmaster.wc.interfaces.webservices.bombean.BOMRequestResponse;
import com.sportmaster.wc.interfaces.webservices.bombean.BomEndpointService;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.helper.SMBOMOutboundHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.plugin.SMBOMOutboundPlugin;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.util.SMBOMLogEntryUtill;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.util.SMBOMOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.util.SMBOMOutboundRequestXMLGenerator;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.util.SMBOMOutboundUtil;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.util.SMBOMOutboundWebServiceConstants;
import com.sun.xml.ws.client.ClientTransportException;
import com.sun.xml.ws.fault.ServerSOAPFaultException;

import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class SMBOMClient {
	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = LogR.getLogger(SMBOMOutboundPlugin.class.getName());

	public void processBOMRequest(FlexBOMPart flexbomPart) throws WTPropertyVetoException, WTException {

		SMBOMOutboundIntegrationBean bomBean =  constructBOMBean();
		LOGGER.debug("FlexBOMPart is CREATED/UPDATED");
		bomBean.setFlexbomPart(flexbomPart);
		//Setting BOM Beans
		BOMRequest bomRequest = new BOMRequest();
		BOMPart bomPartObj = new BOMPart();

		//Setting request id for BomPart
		bomBean.setBOMOutboundRequestID(bomBean.getBomUtill().generateBOMPartOutboundIntegrationRequestID());
		LOGGER.debug("REQUEST ID FOR BOMPart  >>>  "+bomBean.getBOMOutboundRequestID());

		//calling BOM outbound processor for setting the BOMPart bean
		BOMPart bomPartBeanReturned = bomBean.getBomProcessor().setDataForBOMOutboundRequest(flexbomPart, bomPartObj, bomBean);
		bomRequest.setBOMPart(bomPartBeanReturned);

		//set request ID.
		bomRequest.setRequestId(bomBean.getBOMOutboundRequestID());

		//Generate XML 
		if(SMBOMOutboundWebServiceConstants.GENERATE_XML_FOR_RESPONSE && null != bomPartBeanReturned){
			LOGGER.debug("calling " + bomBean.getXmlUtill() + " to geneate xml file");
			//generate XML.
			bomBean.getXmlUtill().generateXMLFileForBOMOutboundRequest(bomRequest, SMBOMOutboundWebServiceConstants.BOM_XML_TYPE);
		}

		//Sending Request TO PLMGATE
		if (Objects.nonNull(bomPartBeanReturned)) {
			String errorMsg= "";

			try {
				LOGGER.debug("BOM Part PLMGate before response ::::::::::::::");
				BOMRequestResponse bomOutboundResponse = bomBean.getBOMOutboundWS().bomRequest(bomRequest);
				LOGGER.debug("BOM Part PLMGate response ::::::::::::::");
				LOGGER.debug("Received Integration Status  >>>  " + bomOutboundResponse.isIntegrationStatus());
				LOGGER.debug("Received Error Message in Response  >>>   " + bomOutboundResponse.getErrorMessage());

				bomBean.setBomOutboundResponse(bomOutboundResponse);


			} catch (ClientTransportException clientExc) {
				errorMsg = clientExc.getLocalizedMessage();
				bomBean.setResponseErrorReason(errorMsg);
				LOGGER.error("ClientTransportException in SMBOMClient class ===",clientExc);
			} catch (ServerSOAPFaultException serverSoapEXP) {
				errorMsg = serverSoapEXP.getLocalizedMessage();
				bomBean.setResponseErrorReason(errorMsg);
				LOGGER.error("ServerSOAPFaultException in SMBOMClient class ===",serverSoapEXP);
			} catch (SOAPFaultException soapExp) {
				errorMsg = soapExp.getLocalizedMessage();
				bomBean.setResponseErrorReason(errorMsg);
				LOGGER.error("SOAPFaultException in SMBOMClient class ===",soapExp);
			} catch (WebServiceException webSrcvExcp) {
				errorMsg = webSrcvExcp.getLocalizedMessage();
				bomBean.setResponseErrorReason(errorMsg);
				LOGGER.error("WebServiceException in SMBOMClient class ===",webSrcvExcp);
			}

			// process bom outbound response.
			processBOMOutboundIntegrationResponse(bomBean);
		}


	}
	public static void processBOMOutboundIntegrationResponse(SMBOMOutboundIntegrationBean bean)
			throws WTException, WTPropertyVetoException {

		LOGGER.info("Processing bom response  -----");
		// bom response.
		BOMRequestResponse bomResponse = bean.getBomOutboundResponse();

		bean.setLogentryStatus(SMBOMOutboundWebServiceConstants.LOG_ENTRY_PENDING);
		if (Objects.nonNull(bomResponse)) {
			// setting PLMGate error reason.
			bean.setResponseErrorReason(bomResponse.getErrorMessage());
			if (bomResponse.isIntegrationStatus()) {
				bean.setLogentryStatus(SMBOMOutboundWebServiceConstants.LOG_ENTRY_PROCESSED);
			}

		}
		bean.getBomLogEntryProcessor().setLogEntryForBOMOutboundIntegration(bean);
	}
	private static SMBOMOutboundIntegrationBean constructBOMBean() {

		SMBOMOutboundIntegrationBean bomBean = new SMBOMOutboundIntegrationBean();

		bomBean.setBomUtill(new SMBOMOutboundUtil());
		bomBean.setBOMLogEntryUtill(new SMBOMLogEntryUtill());
		bomBean.setBomLogEntryProcessor(new SMBOMOutboundLogEntryProcessor());
		bomBean.setBomProcessor(new SMBOMOutboundDataProcessor());
		bomBean.setBomHelper(new SMBOMOutboundHelper());
		bomBean.setXmlUtill(new SMBOMOutboundRequestXMLGenerator());
		bomBean.setBOMOutboundWS(new BomEndpointService().getBomEndpointPort());
		bomBean.setBomPartOutboundLogEntry(new HashMap<>());

		return bomBean;
	}
}
