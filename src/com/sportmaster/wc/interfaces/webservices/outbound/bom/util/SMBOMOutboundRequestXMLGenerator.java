/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.bom.util;

import java.io.File;

import java.io.IOException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.webservices.bombean.BOMRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwayInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationUpdatesRequest;

/**
 * @author BSC
 *
 */
public class SMBOMOutboundRequestXMLGenerator {

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMBOMOutboundRequestXMLGenerator.class);
	/**
	 * Constructor.
	 */
	public SMBOMOutboundRequestXMLGenerator(){
		//Constructor
	}
	
	/**
	 * Generates BOM Outbound XML File.
	 * @param bomRequest - BOMRequest.
	 * @param type - String.
	 */
	public void generateXMLFileForBOMOutboundRequest(BOMRequest bomRequest,String type){
		javax.xml.bind.JAXBContext bomJaxbContext;
		try {
			//XML for BOM Request
			bomJaxbContext = javax.xml.bind.JAXBContext
					.newInstance(BOMRequest.class);
			Marshaller bomJaxbMarshaller = bomJaxbContext.createMarshaller();
			QName bomQName = new QName(BOMRequest.class.getSimpleName());
			
			bomJaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<BOMRequest> bomJaxbEle = new JAXBElement<>(bomQName, BOMRequest.class,
					bomRequest);
			//call processXMLFiles method 
			//updated requestID and mdmId for generating XML Phase-4
			File bomXMLFile = SMUtill.processXMLFiles(type, bomRequest.getRequestId(),
					SMBOMOutboundWebServiceConstants.BOM_OUTBOUND_XML_GENERATION_LOCATION);
			bomJaxbMarshaller.marshal(bomJaxbEle, bomXMLFile);
			// Writing to XML file
			bomJaxbMarshaller.marshal(bomRequest, bomXMLFile);
			// Writing to console
			//bsJaxbMarshaller.marshal(request, xmlFile);
		} catch (JAXBException | WTException | IOException smsfJaxnExp) {
			LOGGER.error(smsfJaxnExp.getLocalizedMessage(), smsfJaxnExp);
		}
	}
	

}
