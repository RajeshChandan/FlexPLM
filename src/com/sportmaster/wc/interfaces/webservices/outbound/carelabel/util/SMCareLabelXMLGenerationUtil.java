/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.CareLabelReportRequest;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.client.SMCareLabelDataClient;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.helper.SMCareLabelIntegrationBean;

/**
 * SMCareLabelXMLGenerationUtil - Functionality .
 * 
 * @author 'true' ITC
 * @version 'true' 1.0 version number
 * @since March 15, 2018
 */
public class SMCareLabelXMLGenerationUtil {
	
	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMCareLabelXMLGenerationUtil.class);
	/**
	 * Constructor.
	 */
	protected SMCareLabelXMLGenerationUtil(){
		//Constructor
	}
	
	/**
	 * Generates Product Outbound XML File.
	 * @param prodRequest - ProductInformationUpdatesRequest.
	 * @param type - String.
	 */
	public static void generateXMLFileForCareLabelIntegrationRequest(CareLabelReportRequest careLabelRequest,String type, SMCareLabelIntegrationBean intgBean){
		javax.xml.bind.JAXBContext careLabelJaxbContext;
		try {
			LOGGER.debug(">>>>> Genearte XML files START :::" );
			//XML for Business Supplier Request
			careLabelJaxbContext = javax.xml.bind.JAXBContext
					.newInstance(CareLabelReportRequest.class);
			Marshaller careLabelJaxbMarshaller = careLabelJaxbContext.createMarshaller();
			QName careLabelQName = new QName(CareLabelReportRequest.class.getSimpleName());
			// Marshaller marshaller = jc.createMarshaller();
			careLabelJaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<CareLabelReportRequest> careLabelJaxbEle = new JAXBElement<CareLabelReportRequest>(careLabelQName, CareLabelReportRequest.class,careLabelRequest);
			//call processXMLFiles method 
			
			File careLabelIntegrationXMLFile = SMUtill.processXMLFiles(type,intgBean.getCareLabelRequestID(),SMCareLabelConstants.CARE_LABEL_INTEGRATION_XML_GENERATION_LOCATION);
			careLabelJaxbMarshaller.marshal(careLabelJaxbEle, careLabelIntegrationXMLFile);
			// Writing to XML file
			careLabelJaxbMarshaller.marshal(careLabelRequest, careLabelIntegrationXMLFile);
			// Writing to console
			//bsJaxbMarshaller.marshal(request, xmlFile);
		} catch (JAXBException smsfJaxnExp) {
			LOGGER.error(smsfJaxnExp.getLocalizedMessage());
			smsfJaxnExp.printStackTrace();
		} catch (WTException excpt) {
			LOGGER.error(excpt.getLocalizedMessage());
			excpt.printStackTrace();
		} catch (IOException ioe) {
			LOGGER.error(ioe.getLocalizedMessage());
			ioe.printStackTrace();
		}
		
		LOGGER.debug(">>>>> Genearte XML files END :::" );
	}
	
	
}
