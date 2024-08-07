package com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.helper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.google.common.collect.Multimap;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.webservices.bean.BOEnumeration;
import com.sportmaster.wc.interfaces.webservices.bean.GetSimpleBOInformationUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.bean.RequestType;
import com.sportmaster.wc.interfaces.webservices.bean.SimpleBO;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.utill.SMLVIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;

/**
 * SMLVIntegrationHelper.java
 * This class is using to call the methods defined in process class.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMLVIntegrationHelper {
	/**
	 * ERROR_OCCURED_LITERAL.
	 */
	private static final String ERROR_OCCURED_LITERAL = "ERROR OCCURED -";
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMLVIntegrationHelper.class);
	/**
	 * constructor.
	 */
	protected SMLVIntegrationHelper() {
	}

	/**
	 * creates data values map for list values.
	 * @param simpleBO - SimpleBO
	 * @param status - String
	 * @return - Map
	 */
	public static Map<String, String> setDataValues(SimpleBO simpleBO,String status){
		LOGGER.debug("Creating data values for Business Object : "+simpleBO.getMdmBO());
		Map<String, String> dataValues= new HashMap<String, String>();
		dataValues.put(SMInboundWebserviceConstants.BO_NAME, simpleBO.getName());
		dataValues.put(SMInboundWebserviceConstants.BO_MDMID, simpleBO.getMdmBO());
		dataValues.put(SMInboundWebserviceConstants.BO_STATUS_ENUM , status);
		return dataValues;

	}
	/**
	 * returns Business Object Type.
	 * @param type - String
	 * @return - String
	 */
	public static String getboType(String type){
		if(SMInboundWebserviceConstants.BO_CLASS.equals(type)){

			return   SMInboundWebserviceConstants.LIST_VALUE_BO_CLASS;
		}else if(SMInboundWebserviceConstants.BO_SUBCLASS.equals(type)){

			return SMInboundWebserviceConstants.LIST_VALUE_BO_SUBCLASS;

		}else if(SMInboundWebserviceConstants.BO_CATEGORY.equals(type)){

			return SMInboundWebserviceConstants.LIST_VALUE_BO_CATEGORY;
		}else if(SMInboundWebserviceConstants.BO_SUBCATEGORY.equals(type)){

			return SMInboundWebserviceConstants.LIST_VALUE_BO_SUBCATEGORY;
		}else if(SMInboundWebserviceConstants.BO_GOM.equals(type)){

			return SMInboundWebserviceConstants.LIST_VALUE_BO_GOM;
		}
		return "";

	}

	/**
	 * creates data values map for list value Log entry.
	 * @param bo - LCSLifecycleManaged
	 * @param ida2a2 - String
	 * @param integrationStatus - String
	 * @param status - String
	 * @param errorReason - String
	 * @param requesType - String
	 * @param responseType - String
	 * @return - Map
	 */
	public static Map<String, String> getLogEntryDataValues(LCSLifecycleManaged bo,String ida2a2,String integrationStatus,String status,String errorReason,SMLVIntegrationBean lvBean) {
		Map<String, String> dataValues= new HashMap<String, String>();
		try {
			LOGGER.debug("Creating data values for List values Log Entry Object : "+ bo.getValue(SMInboundWebserviceConstants.BO_NAME));
			dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_NMAE_KEY, (String) bo.getValue(SMInboundWebserviceConstants.BO_NAME));
			dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_MDMID_KEY, (String) bo.getValue(SMInboundWebserviceConstants.BO_MDMID));
			dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_OBJECTID_KEY, ida2a2);
			dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_BUSSINESS_OBJECT_TYPE, lvBean.getBusinessObjectType());
			dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_REQUEST_TYPE, lvBean.getRequestType());
			dataValues.put(SMInboundWebserviceConstants.REQUEST_ID, lvBean.getRequestId());
			dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_INT_STATUS_KEY, integrationStatus);
			dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_STATUS_KEY,status);
			dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_ERROR_KEY,errorReason);
		} catch (WTException e) {
			e.printStackTrace();
			LOGGER.debug("Error Occured during geting values from business object ");
		}
		return dataValues;
	}
	/**
	 * creates data values map for list value Log entry from simpleBO Object.
	 * @param bo - SimpleBO
	 * @param ida2a2 - String
	 * @param integrationStatus - String
	 * @param status - String
	 * @param errorReason - String
	 * @param boType - String
	 * @return - Map
	 */
	public static Map<String, String> getLogEntryDataValues(SimpleBO bo,String ida2a2,String integrationStatus,String status,String errorReason,String 	boType,String requestId ) {
		Map<String, String> dataValues= new HashMap<String, String>();
		LOGGER.debug("Creating data values for List values Log Entry Object : "+ bo.getMdmBO());
		dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_NMAE_KEY, (String) bo.getName());
		dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_MDMID_KEY, (String) bo.getMdmBO());
		dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_OBJECTID_KEY, ida2a2);
		dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_BUSSINESS_OBJECT_TYPE, boType);
		dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_REQUEST_TYPE, bo.getRequestType().toString());
		dataValues.put(SMInboundWebserviceConstants.REQUEST_ID, requestId);
		dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_INT_STATUS_KEY, integrationStatus);
		dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_STATUS_KEY,status);
		dataValues.put(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_ERROR_KEY,errorReason);
		return dataValues;
	}
	/**
	 * Adding newly created data to collection.
	 * @param flexData - Multimap
	 * @param flexLogEntryData - Map
	 * @param ida2a2 - String
	 * @param dataValues -Map
	 * @param attKeyColumnMap - Map
	 * @param primaryKey - String
	 * @param ida1a2Key - String
	 * @throws WTException - WTException
	 */
	public static void reConfigureColection(Multimap<String, FlexObject> flexData, Map<String, FlexObject> flexLogEntryData,String ida2a2,Map<String, String> dataValues, Map<String, String> attKeyColumnMap,String primaryKey,String ida1a2Key)
			throws WTException {
		if(FormatHelper.hasContent(ida2a2)){
			LOGGER.debug("Adding newly created object into collection ");
			FlexObject fo= new FlexObject();
			for(Map.Entry<String, String> entryDataValues : dataValues.entrySet()){
				String key=entryDataValues.getKey().trim();
				String dbKey=attKeyColumnMap.get(key);
				fo.put(dbKey.toUpperCase(), dataValues.get(key));
			}
			fo.put(ida1a2Key, ida2a2);
			if(flexData!=null){
				flexData.put(dataValues.get(primaryKey), fo);
			}else{
				flexLogEntryData.put(dataValues.get(primaryKey), fo);
			}
		}
	}


	/**
	 * validate response object.
	 * @param response - GetSimpleBOInformationUpdatesRequestResponse
	 * @return - Boolean
	 */
	public static Boolean validateResponseObject(GetSimpleBOInformationUpdatesRequestResponse response){
		Boolean status=false;
		LOGGER.debug("Validating Response Object");
		if(response!=null){
			BOEnumeration name=response.getName();
			if(name==null){
				LOGGER.debug("response.getName() is null");
				return false;
			}
			if(checkValues(name.value())){
				if(checkValues(String.valueOf(response.getRequestId()))){
					List<SimpleBO> boLst =  response.getSimpleBO();
					if(boLst.isEmpty()){
						//log for data emplty
						LOGGER.debug("Validation failed : SimpleBo object List is emplty");
						return false;
					}
					status=true;
				}else{
					LOGGER.debug("response.getRequestId() is null");
				}
			}else{
				LOGGER.debug("response.getName().value() is null");
			}

		}else{
			//logger for response null
			LOGGER.debug("Validation failed : Response object is null");
			status=false;
		}
		return status;
	}

	/** 
	 * validates SimpleBO object.
	 * @param simpleBO - SimpleBO
	 * @return - boolean
	 */
	public static boolean validateSimpleBO(SimpleBO simpleBO){
		LOGGER.debug("Validating SimpleBO Object");
		boolean status=false;
		RequestType requestType = simpleBO.getRequestType();
		if(requestType==null){
			LOGGER.debug("simpleBO.getRequestType() is null");
			return status;
		}
		if(checkValues(requestType.value())){
			if(checkValues(simpleBO.getMdmBO())){
				if(checkValues(simpleBO.getName())){
					status=true;
				}else{
					LOGGER.debug("simpleBO.getName() is null");
				}
			}else{
				LOGGER.debug("simpleBO.getMdmBO() is null");
			}
		}else{
			LOGGER.debug("requestType.value() is null");
		}
		return status;

	}

	/**
	 *  checks parameter has content or not.
	 * @param value - String
	 * @return - boolean
	 */
	public static boolean checkValues(String value){
		if(FormatHelper.hasContent(value)){
			return true;
		}
		return false;
	}

	/**
	 *  generates xml file from response object for debg properses.
	 * @param response - GetSimpleBOInformationUpdatesRequestResponse
	 * @param type - String
	 * @throws IOException 
	 */
	public static void generateXMLFileForListValue(GetSimpleBOInformationUpdatesRequestResponse response,String type) {
		javax.xml.bind.JAXBContext jaxbContext;
		try {
			jaxbContext = javax.xml.bind.JAXBContext
					.newInstance(GetSimpleBOInformationUpdatesRequestResponse.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			QName qName = new QName(GetSimpleBOInformationUpdatesRequestResponse.class.getSimpleName());
			// Marshaller marshaller = jc.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<GetSimpleBOInformationUpdatesRequestResponse> jaxbEle = new JAXBElement<GetSimpleBOInformationUpdatesRequestResponse>(qName, GetSimpleBOInformationUpdatesRequestResponse.class,response);

			File xmlFile = SMUtill.processXMLFiles(type,response.getRequestId(),SMInboundWebserviceConstants.LISTVALUES_FILE_LOCATION);
			jaxbMarshaller.marshal(jaxbEle, xmlFile );
			// Writing to XML file
			jaxbMarshaller.marshal(response, xmlFile);
			// Writing to console
			jaxbMarshaller.marshal(response, xmlFile);
		} catch (JAXBException smsfJaxnExp) {
			LOGGER.error(ERROR_OCCURED_LITERAL+smsfJaxnExp.getLocalizedMessage());
			smsfJaxnExp.printStackTrace();
		} catch (WTException wtExp) {
			LOGGER.error(ERROR_OCCURED_LITERAL+wtExp.getLocalizedMessage());
			wtExp.printStackTrace();
		} catch (IOException ioeExp) {
			LOGGER.error(ERROR_OCCURED_LITERAL+ioeExp.getLocalizedMessage());
			ioeExp.printStackTrace();
		}
	}
	public static int getTimeoutbyType(List<String> listType,String type){
		int timeout;
		Field field;
		SMInboundWebserviceConstants constants=new SMInboundWebserviceConstants();
		for(String typee:listType){
			if(type.equalsIgnoreCase(typee)){
				try {
					field = Class.forName("com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants").getField(type.toUpperCase()+"_TIMEOUT");
					timeout=(Integer) field.get(constants);
					return timeout;
				} catch (NoSuchFieldException noMethodExp) {
					LOGGER.error(ERROR_OCCURED_LITERAL+noMethodExp.getLocalizedMessage());
					noMethodExp.printStackTrace();
				} catch (SecurityException securityExp) {
					LOGGER.error(ERROR_OCCURED_LITERAL+securityExp.getLocalizedMessage());
					securityExp.printStackTrace();
				} catch (ClassNotFoundException classNotFoundExp) {
					LOGGER.error(ERROR_OCCURED_LITERAL+classNotFoundExp.getLocalizedMessage());
					classNotFoundExp.printStackTrace();
				} catch (IllegalArgumentException illegalArgExp) {
					LOGGER.error(ERROR_OCCURED_LITERAL+illegalArgExp.getLocalizedMessage());
					illegalArgExp.printStackTrace();
				} catch (IllegalAccessException illegalAccessExp) {
					LOGGER.error(ERROR_OCCURED_LITERAL+illegalAccessExp.getLocalizedMessage());
					illegalAccessExp.printStackTrace();
				}

			}
		}
		return 0;
	}
}
