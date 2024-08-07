package com.sportmaster.wc.interfaces.webservices.outbound.feedback.helper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSLogEntry;
import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.webservices.bean.GetStatusDivisionRequest;
import com.sportmaster.wc.interfaces.webservices.bean.GetStatusSimpleBOInformationRequest;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMIntegrationUtill;


/**
 * SMLVFIntegrationHelper.java
 * This class has methods for getting Log entry for the Business Object.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMLVFIntegrationHelper {
	private static final String ERROR_OCCOURED_MESSAGE = "ERROR OCCOURED : - ";
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMLVFIntegrationHelper.class);

	/**
	 *  constructor
	 */
	protected SMLVFIntegrationHelper() {
	}

	/**
	 *  get the Log entry data by type of business Object.
	 * @param dbData - dbData
	 * @return -Map
	 * @throws WTException the  WTException
	 */
	public static Map<String,Map<String, LCSLogEntry>> getLogEntrydataByType(Map<String, FlexObject> dbData) throws WTException{
		String type;

		Map<String,Map<String, LCSLogEntry>> logentryDbData= new HashMap<String,Map<String, LCSLogEntry>>();

		Map<String, LCSLogEntry> classDbData=new HashMap<String, LCSLogEntry>();
		Map<String, LCSLogEntry> subClassDbData= new HashMap<String, LCSLogEntry>();
		Map<String, LCSLogEntry> categoryDbData=new HashMap<String, LCSLogEntry>();
		Map<String, LCSLogEntry> subCategoryDbData=new HashMap<String, LCSLogEntry>();
		Map<String, LCSLogEntry> gomDbData=new HashMap<String, LCSLogEntry>();
		//processing according to logentry type.
		for(Map.Entry<String, FlexObject> data: dbData.entrySet()){
			LCSLogEntry logEntry=SMIntegrationUtill.getLogEntryFromFlexObject(data.getValue());
			type= (String) logEntry.getValue(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_BUSSINESS_OBJECT_TYPE);
			if(SMInboundWebserviceConstants.BO_CLASS.equalsIgnoreCase(type)){
				classDbData.put(data.getKey(), logEntry);
			}else if(SMInboundWebserviceConstants.BO_SUBCLASS.equalsIgnoreCase(type)){
				subClassDbData.put(data.getKey(), logEntry);
			}else if (SMInboundWebserviceConstants.BO_CATEGORY.equalsIgnoreCase(type)){
				categoryDbData.put(data.getKey(), logEntry);
			}else if(SMInboundWebserviceConstants.BO_SUBCATEGORY.equalsIgnoreCase(type)){
				subCategoryDbData.put(data.getKey(), logEntry);
			}else if(SMInboundWebserviceConstants.BO_GOM.equalsIgnoreCase(type)){
				gomDbData.put(data.getKey(), logEntry);
			}
		}
		//creting map according to type
		logentryDbData.put(SMInboundWebserviceConstants.BO_CLASS.toLowerCase(), classDbData);
		logentryDbData.put(SMInboundWebserviceConstants.BO_SUBCLASS.toLowerCase(), subClassDbData);
		logentryDbData.put(SMInboundWebserviceConstants.BO_CATEGORY.toLowerCase(), categoryDbData);
		logentryDbData.put(SMInboundWebserviceConstants.BO_SUBCATEGORY.toLowerCase(), subCategoryDbData);
		logentryDbData.put(SMInboundWebserviceConstants.BO_GOM.toLowerCase(), gomDbData);
		return logentryDbData;

	}

	/**
	 * Generating the Request to XML file.
	 * @param request  the request
	 * @param type  the type
	 * @throws WTException the WTException
	 * @throws IOException theIOException
	 */
	public static void generateXML(GetStatusSimpleBOInformationRequest request,String type) {
		javax.xml.bind.JAXBContext jaxbContext;
		
		LOGGER.debug("Generating XML file for GetStatusSimpleBOInformationRequest");
		try {
			jaxbContext = javax.xml.bind.JAXBContext
					.newInstance(GetStatusSimpleBOInformationRequest.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			QName qName = new QName(GetStatusSimpleBOInformationRequest.class.getSimpleName());
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<GetStatusSimpleBOInformationRequest> jaxbEle = new JAXBElement<GetStatusSimpleBOInformationRequest>(qName, GetStatusSimpleBOInformationRequest.class,request);
			//creting xml file.
			File xmlFile = SMUtill.processXMLFiles(type,request.getStatus().getRequestId(),SMInboundWebserviceConstants.FEEDBACK_FILE_LOCATION);
			jaxbMarshaller.marshal(jaxbEle, xmlFile );
			// Writing to XML file
			jaxbMarshaller.marshal(request, xmlFile);
			// Writing to console
			jaxbMarshaller.marshal(request, xmlFile);
		} catch (JAXBException jAXExp) {
			LOGGER.error(ERROR_OCCOURED_MESSAGE+jAXExp.getLocalizedMessage());
			jAXExp.printStackTrace();
		} catch (WTException wtExpctn) {
			LOGGER.error(ERROR_OCCOURED_MESSAGE+wtExpctn.getLocalizedMessage());
			wtExpctn.printStackTrace();
		} catch (IOException ioeExp) {
			LOGGER.error(ERROR_OCCOURED_MESSAGE+ioeExp.getLocalizedMessage());
			ioeExp.printStackTrace();
		}

	}
	/**
	 * Generating the Request to XML file.
	 * @param request  the request
	 * @param type  the type
	 * @throws WTException the WTException
	 * @throws IOException theIOException
	 */
	public static void generateXMLForDivision(GetStatusDivisionRequest request,String type) {
		javax.xml.bind.JAXBContext jaxbContext;
		
		LOGGER.debug("Generating XML file for GetStatusDivisionRequest");
		try {
			jaxbContext = javax.xml.bind.JAXBContext
					.newInstance(GetStatusDivisionRequest.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			QName qName = new QName(GetStatusDivisionRequest.class.getSimpleName());
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<GetStatusDivisionRequest> jaxbEle = new JAXBElement<GetStatusDivisionRequest>(qName, GetStatusDivisionRequest.class,request);
			//creating xml file.
			File xmlFile = SMUtill.processXMLFiles(type,request.getDivisionStatus().getRequestId(),SMInboundWebserviceConstants.FEEDBACK_FILE_LOCATION);
			jaxbMarshaller.marshal(jaxbEle, xmlFile );
			// Writing to XML file
			jaxbMarshaller.marshal(request, xmlFile);
			// Writing to console
			jaxbMarshaller.marshal(request, xmlFile);
		} catch (JAXBException jbExp) {
			LOGGER.error(ERROR_OCCOURED_MESSAGE+jbExp.getLocalizedMessage());
			jbExp.printStackTrace();
		} catch (WTException wtExp) {
			LOGGER.error(ERROR_OCCOURED_MESSAGE+wtExp.getLocalizedMessage());
			wtExp.printStackTrace();
		} catch (IOException ioeDivExp) {
			LOGGER.error(ERROR_OCCOURED_MESSAGE+ioeDivExp.getLocalizedMessage());
			ioeDivExp.printStackTrace();
		} 

	}
}
