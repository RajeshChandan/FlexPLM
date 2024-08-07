package com.sportmaster.wc.interfaces.webservices.outbound.material.helper;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.webservices.bean.DecorationInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.FabricMaterialInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.MaterialInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.PackagingInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.ShippingInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.bean.TrimsInformationUpdatesRequest;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

import wt.util.WTException;

public class SMMaterialXMLHelper {
	
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMMaterialXMLHelper.class);
	
	
	/**
	 * Generating the Request to XML file.
	 * @param request  the request
	 * @param type  the type
	 * @throws WTException the WTException
	 * @throws IOException theIOException
	 */
	public void generateXMLForMaterial(FabricMaterialInformationUpdatesRequest request,String type) {
		
		JAXBContext jaxbContext;
		if(!SMOutboundWebServiceConstants.MATERIAL_XML_GENERATION_FLAG){
			//returning not need to generate XML
			return;
		}
		
		LOGGER.debug("Generating XML Request file for Fabric Material");
		try {
			jaxbContext = JAXBContext.newInstance(FabricMaterialInformationUpdatesRequest.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			QName qName = new QName(FabricMaterialInformationUpdatesRequest.class.getSimpleName());
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			JAXBElement<FabricMaterialInformationUpdatesRequest> jaxbEle = new JAXBElement<>(
					qName, FabricMaterialInformationUpdatesRequest.class, request);
			// creating xml file.
			File materialXmlFile = SMUtill.processXMLFiles(type, request.getRequestId(),
					SMOutboundWebServiceConstants.MATERIAL_XML_FILE_LOCATION);
			
			jaxbMarshaller.marshal(jaxbEle, materialXmlFile );
			// Writing to XML file
			jaxbMarshaller.marshal(request, materialXmlFile);
			
		} catch (JAXBException jbExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, jbExp);
		} catch (WTException wtExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtExp);
		} catch (IOException ioeDivExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, ioeDivExp);
		} 

	}
	
	/**
	 * Generating the Request to XML file.
	 * @param request  the request
	 * @param type  the type
	 * @throws WTException the WTException
	 * @throws IOException theIOException
	 */
	public void generateXMLForMaterial(TrimsInformationUpdatesRequest trimRequest,String type) {
		
		JAXBContext jaxbContextTrim;
		if(!SMOutboundWebServiceConstants.MATERIAL_XML_GENERATION_FLAG){
			//returning not need to generate XML
			return;
		}

		LOGGER.debug("Generating XML Request file for Trims Material");
		try {
			jaxbContextTrim = JAXBContext.newInstance(TrimsInformationUpdatesRequest.class);
			Marshaller jaxbMarshaller = jaxbContextTrim.createMarshaller();
			QName qNameTrim = new QName(TrimsInformationUpdatesRequest.class.getSimpleName());
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			JAXBElement<TrimsInformationUpdatesRequest> jaxbEleTrim = new JAXBElement<>(qNameTrim,
					TrimsInformationUpdatesRequest.class, trimRequest);
			// creating xml file.
			File xmlFileTrim = SMUtill.processXMLFiles(type, trimRequest.getRequestId(),
					SMOutboundWebServiceConstants.MATERIAL_XML_FILE_LOCATION);
			
			jaxbMarshaller.marshal(jaxbEleTrim, xmlFileTrim );
			// Writing to XML file
			jaxbMarshaller.marshal(trimRequest, xmlFileTrim);
			
		} catch (JAXBException jbExpTrim) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, jbExpTrim);
		} catch (WTException wtExpTrim) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtExpTrim);
		} catch (IOException ioeDivExpTrim) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, ioeDivExpTrim);
		} 

	}
	
	/**
	 * Generating the Request to XML file.
	 * @param request  the request
	 * @param type  the type
	 * @throws WTException the WTException
	 * @throws IOException theIOException
	 */
	public void generateXMLForMaterial(DecorationInformationUpdatesRequest decorationRequest,String type) {
		
		JAXBContext jaxbContextDec;
		if(!SMOutboundWebServiceConstants.MATERIAL_XML_GENERATION_FLAG){
			//returning not need to generate XML
			return;
		}
		
		LOGGER.debug("Generating XML Request file for Decoration Material");
		try {
			
			jaxbContextDec = JAXBContext.newInstance(DecorationInformationUpdatesRequest.class);
			Marshaller jaxbMarshallerDec = jaxbContextDec.createMarshaller();
			QName qNameDec = new QName(DecorationInformationUpdatesRequest.class.getSimpleName());
			jaxbMarshallerDec.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			JAXBElement<DecorationInformationUpdatesRequest> jaxbEleDec = new JAXBElement<>(qNameDec,
					DecorationInformationUpdatesRequest.class, decorationRequest);
			// creating xml file.
			File materialXmlFileDec = SMUtill.processXMLFiles(type, decorationRequest.getRequestId(),
					SMOutboundWebServiceConstants.MATERIAL_XML_FILE_LOCATION);
			
			jaxbMarshallerDec.marshal(jaxbEleDec, materialXmlFileDec );
			// Writing to XML file
			jaxbMarshallerDec.marshal(decorationRequest, materialXmlFileDec);
			
		} catch (JAXBException jbExpDec) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, jbExpDec);
		} catch (WTException wtExpDec) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtExpDec);
		} catch (IOException ioeDivExpDec) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, ioeDivExpDec);
		} 

	}
	
	
	/**
	 * Generating the Request to XML file.
	 * @param request  the request
	 * @param type  the type
	 */
	public void generateXMLForMaterial(PackagingInformationUpdatesRequest packagingRequest,String type) {
		
		JAXBContext jaxbContextPac;
		if(!SMOutboundWebServiceConstants.MATERIAL_XML_GENERATION_FLAG){
			//returning not need to generate XML
			return;
		}
		
		LOGGER.debug("Generating XML Request file for Packaging Material");
		try {
			
			jaxbContextPac = JAXBContext.newInstance(PackagingInformationUpdatesRequest.class);
			Marshaller jaxbMarshaller = jaxbContextPac.createMarshaller();
			QName qNamePac = new QName(PackagingInformationUpdatesRequest.class.getSimpleName());
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			JAXBElement<PackagingInformationUpdatesRequest> jaxbElePac = new JAXBElement<>(qNamePac,
					PackagingInformationUpdatesRequest.class, packagingRequest);
			// creating xml file.
			File materialXmlFilePac = SMUtill.processXMLFiles(type, packagingRequest.getRequestId(),
					SMOutboundWebServiceConstants.MATERIAL_XML_FILE_LOCATION);
			
			jaxbMarshaller.marshal(jaxbElePac, materialXmlFilePac );
			// Writing to XML file
			jaxbMarshaller.marshal(packagingRequest, materialXmlFilePac);
			
		} catch (JAXBException jbExpPac) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, jbExpPac);
		} catch (WTException wtExpPac) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtExpPac);
		} catch (IOException ioeDivExpPac) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, ioeDivExpPac);
		} 

	}
	
	
	/**
	 * Generating the Request to XML file.
	 * @param request  the request
	 * @param type  the type
	 */
	public void generateXMLForMaterial(ShippingInformationUpdatesRequest shippingRequest,String type) {
		
		JAXBContext jaxbContextShip;
		if(!SMOutboundWebServiceConstants.MATERIAL_XML_GENERATION_FLAG){
			//returning not need to generate XML
			return;
		}
		
		LOGGER.debug("Generating XML Request file for Shipping Material");
		try {
			
			jaxbContextShip = JAXBContext.newInstance(ShippingInformationUpdatesRequest.class);
			Marshaller jaxbMarshaller = jaxbContextShip.createMarshaller();
			QName qNameShip = new QName(ShippingInformationUpdatesRequest.class.getSimpleName());
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			JAXBElement<ShippingInformationUpdatesRequest> jaxbEleShip = new JAXBElement<>(qNameShip,
					ShippingInformationUpdatesRequest.class, shippingRequest);
			// creating xml file.
			File materialXmlFileShip = SMUtill.processXMLFiles(type, shippingRequest.getRequestId(),
					SMOutboundWebServiceConstants.MATERIAL_XML_FILE_LOCATION);
			jaxbMarshaller.marshal(jaxbEleShip, materialXmlFileShip );
			// Writing to XML file
			jaxbMarshaller.marshal(shippingRequest, materialXmlFileShip);
			
		} catch (JAXBException jbExpShip) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, jbExpShip);
		} catch (WTException wtExpShip) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtExpShip);
		} catch (IOException ioeDivExpShip) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, ioeDivExpShip);
		} 

	}
	
	/**
	 * Generating the Request to XML file.
	 * @param request  the request
	 * @param type  the type
	 */
	public void generateXMLForMaterial(MaterialInformationUpdatesRequest request,String type) {
		
		JAXBContext jaxbContextTrim;
		if(!SMOutboundWebServiceConstants.MATERIAL_XML_GENERATION_FLAG){
			//returning not need to generate XML
			return;
		}

		LOGGER.debug("Generating XML Request file for Material" + request.getMatTypeWithinHierarchy());
		try {
			
			jaxbContextTrim = JAXBContext.newInstance(MaterialInformationUpdatesRequest.class);
			Marshaller jaxbMarshaller = jaxbContextTrim.createMarshaller();
			QName qNameTrim = new QName(MaterialInformationUpdatesRequest.class.getSimpleName());
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			JAXBElement<MaterialInformationUpdatesRequest> jaxbEleTrim = new JAXBElement<>(qNameTrim,
					MaterialInformationUpdatesRequest.class, request);
			//creating xml file.
			File xmlFileTrim = SMUtill.processXMLFiles(type, request.getRequestId(),
					SMOutboundWebServiceConstants.MATERIAL_XML_FILE_LOCATION);
			
			jaxbMarshaller.marshal(jaxbEleTrim, xmlFileTrim );
			// Writing to XML file
			jaxbMarshaller.marshal(request, xmlFileTrim);
			
		} catch (JAXBException jbExpTrim) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, jbExpTrim);
		} catch (WTException wtExpTrim) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtExpTrim);
		} catch (IOException ioeDivExpTrim) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, ioeDivExpTrim);
		} 

	}
}
