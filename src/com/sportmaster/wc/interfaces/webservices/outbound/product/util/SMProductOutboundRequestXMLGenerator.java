/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.util;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwayInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationUpdatesRequest;

/**
 * @author BSC
 *
 */
public class SMProductOutboundRequestXMLGenerator {

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductOutboundRequestXMLGenerator.class);
	/**
	 * Constructor.
	 */
	public SMProductOutboundRequestXMLGenerator(){
		//Constructor
	}
	
	/**
	 * Generates Product Outbound XML File.
	 * @param prodRequest - ProductInformationUpdatesRequest.
	 * @param type - String.
	 */
	public void generateXMLFileForProductOutboundRequest(ProductInformationUpdatesRequest prodRequest,String type){
		javax.xml.bind.JAXBContext prodJaxbContext;
		try {
			//XML for Business Supplier Request
			prodJaxbContext = javax.xml.bind.JAXBContext
					.newInstance(ProductInformationUpdatesRequest.class);
			Marshaller prodJaxbMarshaller = prodJaxbContext.createMarshaller();
			QName prodQName = new QName(ProductInformationUpdatesRequest.class.getSimpleName());
			// Marshaller marshaller = jc.createMarshaller();
			prodJaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<ProductInformationUpdatesRequest> prodJaxbEle = new JAXBElement<>(prodQName, ProductInformationUpdatesRequest.class,
					prodRequest);
			//call processXMLFiles method 
			//updated requestID and mdmId for generating XML Phase-4
			File prodXMLFile = SMUtill.processXMLFiles(type, prodRequest.getRequestId(),
					SMProductOutboundWebServiceConstants.PRODUCT_OUTBOUND_XML_GENERATION_LOCATION,
					prodRequest.getProduct().getMdmId());
			prodJaxbMarshaller.marshal(prodJaxbEle, prodXMLFile);
			// Writing to XML file
			prodJaxbMarshaller.marshal(prodRequest, prodXMLFile);
			// Writing to console
			//bsJaxbMarshaller.marshal(request, xmlFile);
		} catch (JAXBException | WTException | IOException smsfJaxnExp) {
			LOGGER.error(smsfJaxnExp.getLocalizedMessage(), smsfJaxnExp);
		}
	}
	
	/**
	 * Generate Colorway XML Generation.
	 * @param skuRequest - ColorwayInformationUpdatesRequest.
	 * @param type - String.
	 */
	public void generateXMLFileForColorwayOutboundRequest(ColorwayInformationUpdatesRequest skuRequest,String type){
		javax.xml.bind.JAXBContext skuJaxbContext;
		try {
			//XML for Business Supplier Request
			skuJaxbContext = javax.xml.bind.JAXBContext
					.newInstance(ColorwayInformationUpdatesRequest.class);
			Marshaller skuJaxbMarshaller = skuJaxbContext.createMarshaller();
			QName skuQName = new QName(ColorwayInformationUpdatesRequest.class.getSimpleName());
			// Marshaller marshaller = jc.createMarshaller();
			skuJaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<ColorwayInformationUpdatesRequest> skuJaxbEle = new JAXBElement<>(skuQName, ColorwayInformationUpdatesRequest.class,
					skuRequest);
			//call processXMLFiles method 
			//updated requestID and mdmId for generating XML Phase-4
			File skuXMLFile = SMUtill.processXMLFiles(type, skuRequest.getRequestId(),
					SMProductOutboundWebServiceConstants.COLORWAY_OUTBOUND_XML_GENERATION_LOCATION,
					skuRequest.getColorway().getMdmId());
			skuJaxbMarshaller.marshal(skuJaxbEle, skuXMLFile);
			// Writing to XML file
			skuJaxbMarshaller.marshal(skuRequest, skuXMLFile);
			// Writing to console
			//bsJaxbMarshaller.marshal(request, xmlFile);
		} catch (JAXBException | WTException | IOException smsfJaxnExp) {
			LOGGER.error(smsfJaxnExp.getLocalizedMessage(), smsfJaxnExp);
		}
	}
	
	/**
	 * Generate Product Season Link XML File.
	 * @param prodSeasonLinkRequest - ProductSeasonLinkInformationUpdatesRequest.
	 * @param type - String.
	 */
	public void generateXMLFileForProductSeasonLinkOutboundRequest(ProductSeasonLinkInformationUpdatesRequest prodSeasonLinkRequest,String type){
		javax.xml.bind.JAXBContext prodSeasonLinkJaxbContext;
		try {
			//XML for Business Supplier Request
			prodSeasonLinkJaxbContext = javax.xml.bind.JAXBContext
					.newInstance(ProductSeasonLinkInformationUpdatesRequest.class);
			Marshaller prodSeasonLinkJaxbMarshaller = prodSeasonLinkJaxbContext.createMarshaller();
			QName prodSeasonLinkQName = new QName(ProductSeasonLinkInformationUpdatesRequest.class.getSimpleName());
			// Marshaller marshaller = jc.createMarshaller();
			prodSeasonLinkJaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<ProductSeasonLinkInformationUpdatesRequest> prodSeasonLinkJaxbEle = new JAXBElement<>(prodSeasonLinkQName,
					ProductSeasonLinkInformationUpdatesRequest.class, prodSeasonLinkRequest);
			//call processXMLFiles method 
			//updated requestID and mdmId for generating XML Phase-4
			File prodSeasonLinkXMLFile = SMUtill.processXMLFiles(type, prodSeasonLinkRequest.getRequestId(),
					SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_OUTBOUND_XML_GENERATION_LOCATION,
					prodSeasonLinkRequest.getProductSeasonLink().getMdmId());
			prodSeasonLinkJaxbMarshaller.marshal(prodSeasonLinkJaxbEle, prodSeasonLinkXMLFile);
			// Writing to XML file
			prodSeasonLinkJaxbMarshaller.marshal(prodSeasonLinkRequest, prodSeasonLinkXMLFile);
			// Writing to console
			//bsJaxbMarshaller.marshal(request, xmlFile);
		} catch (JAXBException | WTException | IOException smsfJaxnExp) {
			LOGGER.error(smsfJaxnExp.getLocalizedMessage(), smsfJaxnExp);
		}
	}
	
	/**
	 * Generate Colorway Season XML File.
	 * @param skuSeasonLinkRequest - ColorwaySeasonLinkInformationUpdatesRequest.
	 * @param type - String.
	 */
	public void generateXMLFileForColorwaySeasonLinkOutboundRequest(ColorwaySeasonLinkInformationUpdatesRequest skuSeasonLinkRequest,String type){
		javax.xml.bind.JAXBContext skuSeasonLinkJaxbContext;
		try {
			//XML for Business Supplier Request
			skuSeasonLinkJaxbContext = javax.xml.bind.JAXBContext
					.newInstance(ColorwaySeasonLinkInformationUpdatesRequest.class);
			Marshaller skuSeasonLinkJaxbMarshaller = skuSeasonLinkJaxbContext.createMarshaller();
			QName skuSeasonLinkQName = new QName(ColorwaySeasonLinkInformationUpdatesRequest.class.getSimpleName());
			// Marshaller marshaller = jc.createMarshaller();
			skuSeasonLinkJaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<ColorwaySeasonLinkInformationUpdatesRequest> skuSeasonLinkJaxbEle = new JAXBElement<>(skuSeasonLinkQName,
					ColorwaySeasonLinkInformationUpdatesRequest.class, skuSeasonLinkRequest);
			//call processXMLFiles method 
			//updated requestID and mdmId for generating XML Phase-4
			File skuSeasonLinkXMLFile = SMUtill.processXMLFiles(type, skuSeasonLinkRequest.getRequestId(),
					SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_OUTBOUND_XML_GENERATION_LOCATION,
					skuSeasonLinkRequest.getColorwaySeasonLink().getMdmId());
			skuSeasonLinkJaxbMarshaller.marshal(skuSeasonLinkJaxbEle, skuSeasonLinkXMLFile);
			// Writing to XML file
			skuSeasonLinkJaxbMarshaller.marshal(skuSeasonLinkRequest, skuSeasonLinkXMLFile);
			// Writing to console
			//bsJaxbMarshaller.marshal(request, xmlFile);
		} catch (JAXBException | WTException | IOException smsfJaxnExp) {
			LOGGER.error(smsfJaxnExp.getLocalizedMessage(), smsfJaxnExp);
		}
	}
}
