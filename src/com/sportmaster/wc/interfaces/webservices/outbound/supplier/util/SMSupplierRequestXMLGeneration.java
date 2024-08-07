
package com.sportmaster.wc.interfaces.webservices.outbound.supplier.util;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.webservices.bean.BusinessSupplierInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.FactoryInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.MaterialSupplierInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.client.SMSupplierOutboundDataRequestClient;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;


/**
 * @author 'true' ITC_Infotech.
 *
 */
public class SMSupplierRequestXMLGeneration {

	private static final String ERROR_OCCURED = "ERROR OCCURED :-";
	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSupplierRequestXMLGeneration.class);
	/**
	 * Constructor.
	 */
	protected SMSupplierRequestXMLGeneration(){
		//Constructor
	}

	/**
	 * Generates XML for the Business Supplier Response.
	 * @param response - GetDivisionUpdatesRequestResponse
	 * @param type - String
	 * @throws smsfJaxnExp - JAXBException
	 */
	public static void generateXMLFileForBusinessSupplierRequest(BusinessSupplierInformationUpdatesRequest request,String type){
		javax.xml.bind.JAXBContext bsJaxbContext;
		try {
			//XML for Business Supplier Request
			bsJaxbContext = javax.xml.bind.JAXBContext
					.newInstance(BusinessSupplierInformationUpdatesRequest.class);
			Marshaller bsJaxbMarshaller = bsJaxbContext.createMarshaller();
			QName bsQName = new QName(BusinessSupplierInformationUpdatesRequest.class.getSimpleName());
			// Marshaller marshaller = jc.createMarshaller();
			bsJaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<BusinessSupplierInformationUpdatesRequest> bsJaxbEle = new JAXBElement<>(bsQName,
					BusinessSupplierInformationUpdatesRequest.class, request);
			//Parameters to be changed
			LOGGER.debug("bean Type >>>>>>>>>>>>>>>>> " + type);
			//call processXMLFiles method
			File xmlFile = SMUtill.processXMLFiles(type,SMSupplierOutboundDataRequestClient.getSupplierRequestID(),SMOutboundWebServiceConstants.SUPPLIER_REQUEST_XML_FILE_LOCATION);
			bsJaxbMarshaller.marshal(bsJaxbEle, xmlFile );
			// Writing to XML file
			bsJaxbMarshaller.marshal(request, xmlFile);
			// Writing to console
			//bsJaxbMarshaller.marshal(request, xmlFile);
		} catch (JAXBException | WTException | IOException exp) {
			LOGGER.error(ERROR_OCCURED, exp);
		}
	}

	/**
	 * Generates XML for the Material Response.
	 * @param response - GetDivisionUpdatesRequestResponse
	 * @param type - String
	 * @throws smsfJaxnExp - JAXBException
	 */
	public static void generateXMLFileForMaterialSupplierRequest(MaterialSupplierInformationUpdatesRequest request,String type){
		javax.xml.bind.JAXBContext msJaxbContext;
		try {
			//XML for Material Supplier Request
			msJaxbContext = javax.xml.bind.JAXBContext
					.newInstance(MaterialSupplierInformationUpdatesRequest.class);
			Marshaller msJaxbMarshaller = msJaxbContext.createMarshaller();
			QName qName = new QName(MaterialSupplierInformationUpdatesRequest.class.getSimpleName());
			// Marshaller marshaller = jc.createMarshaller();
			msJaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<MaterialSupplierInformationUpdatesRequest> msJaxbEle = new JAXBElement<>(qName,
					MaterialSupplierInformationUpdatesRequest.class, request);
			//Parameters to be changed
			LOGGER.debug("Type >>>>>>>>>>>>>>>>> "+type);
			//call processXMLFiles method
			File xmlFile = SMUtill.processXMLFiles(type,SMSupplierOutboundDataRequestClient.getSupplierRequestID(),SMOutboundWebServiceConstants.SUPPLIER_REQUEST_XML_FILE_LOCATION);
			msJaxbMarshaller.marshal(msJaxbEle, xmlFile );
			// Writing to XML file
			msJaxbMarshaller.marshal(request, xmlFile);
			// Writing to console
			//msJaxbMarshaller.marshal(request, xmlFile);
		} catch (JAXBException | WTException | IOException ex) {
			LOGGER.error(ERROR_OCCURED, ex);
		}
	}

	/**
	 * Generates XML for the Factory Response.
	 * @param response - GetDivisionUpdatesRequestResponse
	 * @param type - String
	 * @throws smsfJaxnExp - JAXBException
	 */
	public static void generateXMLFileForFactoryRequest(FactoryInformationUpdatesRequest request,String type){
		javax.xml.bind.JAXBContext factJaxbContext;
		try {
			//XML for Factory Request
			factJaxbContext = javax.xml.bind.JAXBContext
					.newInstance(FactoryInformationUpdatesRequest.class);
			Marshaller factJaxbMarshaller = factJaxbContext.createMarshaller();
			QName factQName = new QName(FactoryInformationUpdatesRequest.class.getSimpleName());
			// Marshaller marshaller = jc.createMarshaller();
			factJaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<FactoryInformationUpdatesRequest> factJaxbEle = new JAXBElement<>(factQName, FactoryInformationUpdatesRequest.class,
					request);
			//Parameters to be changed
			LOGGER.debug("Type >>>>>>>>>>>>>>>>> "+type);
			//call processXMLFiles method
			File xmlFile = SMUtill.processXMLFiles(type,SMSupplierOutboundDataRequestClient.getSupplierRequestID(),SMOutboundWebServiceConstants.SUPPLIER_REQUEST_XML_FILE_LOCATION);
			factJaxbMarshaller.marshal(factJaxbEle, xmlFile );
			// Writing to XML file
			factJaxbMarshaller.marshal(request, xmlFile);
			// Writing to console
			//factJaxbMarshaller.marshal(request, xmlFile);
		} catch (JAXBException | WTException | IOException exc) {
			LOGGER.error(ERROR_OCCURED, exc);
		}
	}
}
