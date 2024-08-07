/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.inbound.product.helper;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.webservices.inbound.product.client.SMProductInboundDataRequestWebClient;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationItem;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationStatusItem;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationItem;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationStatusItem;

/**
 * @author Carrier
 *
 */
public class SMProductInboundHelper {

	/**
	 * ERROR_FOUND_CNST.
	 */
	private static final String ERROR_FOUND_CNST = "ERROR FOUND :-";
	/**
	 * The LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMProductInboundHelper.class);
	/**
	 * initializing empty string.
	 */
	public static final String EMPTY_STRING = "";

	/**
	 * protected constructor.
	 */
	protected SMProductInboundHelper(){
		//protected constructor.
	}

	/**
	 * Saving the file to system location.
	 * 
	 * @param requestID
	 *            the requestID
	 * @return fileLocation
	 * @throws WTException
	 *             the WTException
	 * @throws IOException
	 *             the IOException
	 */
	public static File saveXMLFiles(int requestID, String fileLocation) throws WTException, IOException {
		File xmlFile = null;

		SMUtill smUtill = new SMUtill();
		/** Get Calendar Instance */
		final Calendar cal = Calendar.getInstance();
		/** Get Current Date */
		final String todaysDate = smUtill.getDate(cal);
		/** Get current Time */
		final String timeNow = smUtill.getTimeForXML(cal);

		final String completePath = fileLocation+File.separator+ todaysDate+"_"+timeNow+"_"+"_"+requestID+"_Processing.xml";
		final String totalPath = completePath.trim();
		xmlFile = new File(totalPath);

		return xmlFile;
	}
	
	/**
	 * Generates XML for the Product Inbound Response.
	 * @param response - ProductSeasonLinkInformation
	 * @param type - String
	 * @throws smsfJaxnExp - JAXBException
	 */
	public static void generateXMLForProductInboundDataRequest(ProductSeasonLinkInformationItem prodInboundResponseInfoItem){
		javax.xml.bind.JAXBContext jaxbContext;
		try {
			jaxbContext = javax.xml.bind.JAXBContext
					.newInstance(ProductSeasonLinkInformationItem.class);
			Marshaller jaxbProductInboundMarshaller = jaxbContext.createMarshaller();
			QName qNameProductInbound = new QName(ProductSeasonLinkInformationItem.class.getSimpleName());
			// Marshaller marshaller = jc.createMarshaller();
			jaxbProductInboundMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<ProductSeasonLinkInformationItem> jaxbProductInboundEle = new JAXBElement<>(
					qNameProductInbound, ProductSeasonLinkInformationItem.class, prodInboundResponseInfoItem);
			//call saveXMLFiles method 
			File xmlProductInboundFile = SMProductInboundHelper.saveXMLFiles(SMProductInboundDataRequestWebClient.getRequestID(), SMProductInboundWebServiceConstants.PRODUCT_INBOUND_RESPONSE_XML_FILE_LOCATION);
			jaxbProductInboundMarshaller.marshal(jaxbProductInboundEle, xmlProductInboundFile );
			// Writing to XML file
			jaxbProductInboundMarshaller.marshal(prodInboundResponseInfoItem, xmlProductInboundFile);
			// Writing to console
			jaxbProductInboundMarshaller.marshal(prodInboundResponseInfoItem, xmlProductInboundFile);
		} catch (JAXBException | WTException | IOException e) {
			LOGGER.error(ERROR_FOUND_CNST, e);
		}
	}
	
	/**
	 * Generates XML for the Product Inbound Response.
	 * @param response - ProductSeasonLinkInformation
	 * @param type - String
	 * @throws smsfJaxnExp - JAXBException
	 */
	public static void generateXMLForColorwayInboundDataRequest(ColorwaySeasonLinkInformationItem colorwayInboundResponseInfoItem){
		javax.xml.bind.JAXBContext jaxbContext;
		try {
			jaxbContext = javax.xml.bind.JAXBContext
					.newInstance(ColorwaySeasonLinkInformationItem.class);
			Marshaller jaxbSkuInboundMarshaller = jaxbContext.createMarshaller();
			QName qName = new QName(ColorwaySeasonLinkInformationItem.class.getSimpleName());
			// Marshaller marshaller = jc.createMarshaller();
			jaxbSkuInboundMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<ColorwaySeasonLinkInformationItem> jaxbSkuInboundEle = new JAXBElement<>(qName,
					ColorwaySeasonLinkInformationItem.class, colorwayInboundResponseInfoItem);
			//call saveXMLFiles method 
			File xmlSkuInboundFile = SMProductInboundHelper.saveXMLFiles((SMProductInboundDataRequestWebClient.getRequestID()), SMProductInboundWebServiceConstants.COLORWAY_INBOUND_RESPONSE_XML_FILE_LOCATION);
			jaxbSkuInboundMarshaller.marshal(jaxbSkuInboundEle, xmlSkuInboundFile );
			// Writing to XML file
			jaxbSkuInboundMarshaller.marshal(colorwayInboundResponseInfoItem, xmlSkuInboundFile);
			// Writing to console
			jaxbSkuInboundMarshaller.marshal(colorwayInboundResponseInfoItem, xmlSkuInboundFile);
		} catch (JAXBException | WTException | IOException e) {
			LOGGER.error(ERROR_FOUND_CNST, e);
		}
	}
	
	/**
	 * Generates XML for the Product Inbound Feedback Response.
	 * @param response - ProductSeasonLinkInformation
	 * @param type - String
	 * @throws smsfJaxnExp - JAXBException
	 */
	public static void generateXMLForProductInboundFeedbackResponse(ProductSeasonLinkInformationStatusItem prodSeasonLinkInfoStatusItem){
		javax.xml.bind.JAXBContext jaxbContext;
		try {
			jaxbContext = javax.xml.bind.JAXBContext
					.newInstance(ProductSeasonLinkInformationStatusItem.class);
			Marshaller jaxbProductInboundFeedbackMarshaller = jaxbContext.createMarshaller();
			QName qNameProductInboundFeedback = new QName(ProductSeasonLinkInformationStatusItem.class.getSimpleName());
			// Marshaller marshaller = jc.createMarshaller();
			jaxbProductInboundFeedbackMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<ProductSeasonLinkInformationStatusItem> jaxbProductInboundFeedbackEle = new JAXBElement<>(
					qNameProductInboundFeedback, ProductSeasonLinkInformationStatusItem.class, prodSeasonLinkInfoStatusItem);
			//call saveXMLFiles method 
			File xmlProductInboundFeedbackFile = SMProductInboundHelper.saveXMLFiles(SMProductInboundDataRequestWebClient.getRequestID(), SMProductInboundWebServiceConstants.PRODUCT_INBOUND_FEEDBACK_REQUEST_XML_FILE_LOCATION);
			jaxbProductInboundFeedbackMarshaller.marshal(jaxbProductInboundFeedbackEle, xmlProductInboundFeedbackFile );
			// Writing to XML file
			jaxbProductInboundFeedbackMarshaller.marshal(prodSeasonLinkInfoStatusItem, xmlProductInboundFeedbackFile);
			// Writing to console
			jaxbProductInboundFeedbackMarshaller.marshal(prodSeasonLinkInfoStatusItem, xmlProductInboundFeedbackFile);
		} catch (JAXBException | WTException | IOException e) {
			LOGGER.error(ERROR_FOUND_CNST, e);
		}
	}
	
	/**
	 * Generates XML for the Colorway Inbound Feedback Response.
	 * @param response - ColorwaySeasonLinkInformationStatusItem
	 * @param type - String
	 * @throws smsfJaxnExp - JAXBException
	 */
	public static void generateXMLForColorwayInboundFeedbackResponse(ColorwaySeasonLinkInformationStatusItem skuSeasonLinkInfoStatusItem){
		javax.xml.bind.JAXBContext jaxbContext;
		try {
			jaxbContext = javax.xml.bind.JAXBContext
					.newInstance(ColorwaySeasonLinkInformationStatusItem.class);
			Marshaller jaxbSKUInboundFeedbackMarshaller = jaxbContext.createMarshaller();
			QName qNameSKUInboundFeedback = new QName(ColorwaySeasonLinkInformationStatusItem.class.getSimpleName());
			// Marshaller marshaller = jc.createMarshaller();
			jaxbSKUInboundFeedbackMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<ColorwaySeasonLinkInformationStatusItem> jaxbColorwayInboundEle = new JAXBElement<>(qNameSKUInboundFeedback,
					ColorwaySeasonLinkInformationStatusItem.class, skuSeasonLinkInfoStatusItem);
			//call saveXMLFiles method 
			File xmlColorwayInboundFeedbackFile = SMProductInboundHelper.saveXMLFiles(SMProductInboundDataRequestWebClient.getRequestID(), SMProductInboundWebServiceConstants.COLORWAY_INBOUND_FEEDBACK_REQUEST_XML_FILE_LOCATION);
			jaxbSKUInboundFeedbackMarshaller.marshal(jaxbColorwayInboundEle, xmlColorwayInboundFeedbackFile );
			// Writing to XML file
			jaxbSKUInboundFeedbackMarshaller.marshal(skuSeasonLinkInfoStatusItem, xmlColorwayInboundFeedbackFile);
			// Writing to console
			jaxbSKUInboundFeedbackMarshaller.marshal(skuSeasonLinkInfoStatusItem, xmlColorwayInboundFeedbackFile);
		} catch (JAXBException | WTException | IOException e) {
			LOGGER.error(ERROR_FOUND_CNST, e);
		}
	}
	
	/**.
	 * The method getDate returns date in the format yyyy-mm- dd.
	 * @param cal Calendar
	 * @return cal.year month date String
	 */
	public  String getDate(Calendar cal){
		// Integration CR
		return EMPTY_STRING + cal.get(Calendar.YEAR) + (cal.get(Calendar.MONTH)+1) + cal.get(Calendar.DATE);
	}

	/**.
	 * The method getTimeForXML returns getTimeForXML in the format hh:mm:ss.
	 * @param cal Calendar
	 * @return HOUR_OF_DAY MINUTE SECOND MILLISECOND String
	 */
	public  String getTimeForXML(Calendar cal){
		return EMPTY_STRING + cal.get(Calendar.HOUR_OF_DAY) +
				(cal.get(Calendar.MINUTE)) + cal.get(Calendar.SECOND)+cal.get(Calendar.MILLISECOND);
	}
}
