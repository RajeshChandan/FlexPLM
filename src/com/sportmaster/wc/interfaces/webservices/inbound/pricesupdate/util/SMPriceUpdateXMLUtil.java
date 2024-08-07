/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.util;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.webservices.productbean.GetPricesUpdatesResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.GetStatusPricesRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.GetStatusPricesResponse;

import wt.util.WTException;

/**
 * SMPriceUpdateXMLUtil.java
 * This class handles XML file generation activities.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMPriceUpdateXMLUtil {

	/**
	 * constructor.
	 */
	protected SMPriceUpdateXMLUtil() {
	}
	/*
	 * REQ_LOGGER.
	 */
	private static final Logger REQ_LOGGER = Logger.getLogger("priceUpdateRequest");
	/*
	 * FEEDBACK_LOGGER.
	 */
	private static final Logger FEEDBACK_LOGGER = Logger.getLogger("priceUpdateFeedback");
	
	/**
	 * @param priceUpdateResponse
	 * @param type
	 * @param intgBean
	 */
	public static void generateXMLFileForPriceUpdateIntegrationRequest(GetPricesUpdatesResponse priceUpdateResponse,String type){
		javax.xml.bind.JAXBContext priceUpdateJaxbContext;
		try {
			//XML for Business Supplier Request
			priceUpdateJaxbContext = javax.xml.bind.JAXBContext
					.newInstance(GetPricesUpdatesResponse.class);
			Marshaller priceUpdateJaxbMarshaller = priceUpdateJaxbContext.createMarshaller();
			QName priceUpdateQName = new QName(GetPricesUpdatesResponse.class.getSimpleName());
			priceUpdateJaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<GetPricesUpdatesResponse> priceUpdateJaxbEle = new JAXBElement<GetPricesUpdatesResponse>(priceUpdateQName, GetPricesUpdatesResponse.class,priceUpdateResponse);
			//call processXMLFiles method 
			int requestId =Integer.parseInt(priceUpdateResponse.getRequestId());
			File priceUpdateIntegrationXMLFile = SMUtill.processXMLFiles(type, requestId,
					SMPricesInboundWebServiceConstants.PRICE_UPDATE_INTEGRATION_XML_GENERATION_LOCATION);
			priceUpdateJaxbMarshaller.marshal(priceUpdateJaxbEle, priceUpdateIntegrationXMLFile);
			// Writing to XML file
			priceUpdateJaxbMarshaller.marshal(priceUpdateResponse, priceUpdateIntegrationXMLFile);
			// Writing to console
		} catch (JAXBException smsfJaxnExp) {
			REQ_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,smsfJaxnExp);
		} catch (WTException excpt) {
			REQ_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,excpt);
		} catch (IOException ioe) {
			REQ_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,ioe);
		}
	}
	
	/**
	 * @param feedbackRequest
	 * @param type
	 * @param intgBean
	 */
	public static void generateXMLFileForFeedbackRequest(GetStatusPricesRequest feedbackRequest,String type){
		javax.xml.bind.JAXBContext priceUpdateJaxbContext;
		try {
			//XML for Business Supplier Request
			priceUpdateJaxbContext = javax.xml.bind.JAXBContext
					.newInstance(GetStatusPricesRequest.class);
			Marshaller priceUpdateJaxbMarshaller = priceUpdateJaxbContext.createMarshaller();
			QName priceUpdateQName = new QName(GetStatusPricesRequest.class.getSimpleName());
			priceUpdateJaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<GetStatusPricesRequest> priceUpdateJaxbEle = new JAXBElement<GetStatusPricesRequest>(priceUpdateQName,
					GetStatusPricesRequest.class, feedbackRequest);
			//call processXMLFiles method 
			int requestId =Integer.parseInt(feedbackRequest.getRequestId());
			File priceUpdateIntegrationXMLFile = SMUtill.processXMLFiles(type, requestId,
					SMPricesInboundWebServiceConstants.PRICE_UPDATE_FEEDBACK_INTEGRATION_XML_GENERATION_LOCATION);
			priceUpdateJaxbMarshaller.marshal(priceUpdateJaxbEle, priceUpdateIntegrationXMLFile);
			// Writing to XML file
			priceUpdateJaxbMarshaller.marshal(feedbackRequest, priceUpdateIntegrationXMLFile);
			// Writing to console
		} catch (JAXBException smsfJaxnExp) {
			FEEDBACK_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,smsfJaxnExp);
		} catch (WTException excpt) {
			FEEDBACK_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,excpt);
		} catch (IOException ioe) {
			FEEDBACK_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,ioe);
		}

	}
	/**
	 * @param feedbackResponse
	 * @param type
	 * @param intgBean
	 */
	public static void generateXMLFileForFeedbackResponse(GetStatusPricesResponse feedbackResponse,String type){
		javax.xml.bind.JAXBContext priceUpdateJaxbContext;
		try {
			//XML for Business Supplier Request
			priceUpdateJaxbContext = javax.xml.bind.JAXBContext
					.newInstance(GetStatusPricesResponse.class);
			Marshaller priceUpdateJaxbMarshaller = priceUpdateJaxbContext.createMarshaller();
			QName priceUpdateQName = new QName(GetStatusPricesResponse.class.getSimpleName());
			priceUpdateJaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<GetStatusPricesResponse> priceUpdateJaxbEle = new JAXBElement<GetStatusPricesResponse>(
					priceUpdateQName, GetStatusPricesResponse.class, feedbackResponse);
			//call processXMLFiles method 
			int requestId =Integer.parseInt(feedbackResponse.getRequestId());
			File priceUpdateIntegrationXMLFile = SMUtill.processXMLFiles(type, requestId,
					SMPricesInboundWebServiceConstants.PRICE_UPDATE_FEEDBACK_INTEGRATION_XML_GENERATION_LOCATION);
			priceUpdateJaxbMarshaller.marshal(priceUpdateJaxbEle, priceUpdateIntegrationXMLFile);
			// Writing to XML file
			priceUpdateJaxbMarshaller.marshal(feedbackResponse, priceUpdateIntegrationXMLFile);
			// Writing to console
		} catch (JAXBException smsfJaxnExp) {
			FEEDBACK_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,smsfJaxnExp);
		} catch (WTException excpt) {
			FEEDBACK_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,excpt);
		} catch (IOException ioe) {
			FEEDBACK_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,ioe);
		}

	}
}
