package com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.helper;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLifecycleManagedLogic;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSLogEntryLogic;
import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.webservices.bean.Division;
import com.sportmaster.wc.interfaces.webservices.bean.GetDivisionUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.client.SMSubdivisionDataRequestWebClient;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.processor.SMSubDivisionTreeDataProcessor;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.util.SMSubDivisionTreeUtil;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;

/**
 * SMSubDivisionHelper.java
 * Helper class for Sub Division Inbound Integration.
 * @author 'true' ITC_Infotech
 * @version 1.0
 */
public class SMSubDivisionHelper {
	
	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSubDivisionHelper.class);
	
	/**
	 * Constructor.
	 */
	protected SMSubDivisionHelper(){
		//protected Constructor
	}
	
	/**
	 * Persists log entry object.
	 * @param logEntryObj - LCSLogEntry
	 */
	public static void persistLogEntry(LCSLogEntry logEntryObj){
		LCSLogEntryLogic logEntryLogic = new LCSLogEntryLogic();
		try {
			//Save Log Entry object
			logEntryLogic.saveLog(logEntryObj, true);
		} catch (WTException e) {
			LOGGER.error("ERROR in persisting Log Entry Object !!!!!!  "+e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Persists the Business Object\Sub Class Division Tree object.
	 * @param lcmObj - LCSLifeCycleManaged
	 * @return boolean
	 * @throws WTException - WTException
	 */
	public static boolean peristSubDivTree(LCSLifecycleManaged lcmObj){
		try{	
			//persist the Business Object
			LCSLifecycleManagedLogic lifeCyclemgdLogic = new LCSLifecycleManagedLogic();
			lifeCyclemgdLogic.saveLifecycleManaged(lcmObj, true);
			//return true once persist is successful
			return true;
		} catch (WTException exception) {
			//increment failed count if exception occurs
			SMSubDivisionTreeDataProcessor.setFailedCount(SMSubDivisionTreeDataProcessor.getFailedCount() + 1);
			LOGGER.error("ERROR in persisiting Sub Division Tree Object"+exception.getLocalizedMessage());
			exception.printStackTrace();
			return false;
		}
	}
	
	/**
	* Validates all fields of response.
	* @param div - Division
	* @return boolean
	* @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	*/	
	public static boolean validateAllFields(Division div) throws WTException, WTPropertyVetoException{
		LOGGER.debug("Inside validateAllFields Method !!!!!!");
		//Checks if Class is Present in the response
		boolean classFound = SMSubDivisionTreeUtil.checkBOexist(SMSubDivisionTreeUtil.getHmClass(), div.getMdmClass(), SMInboundWebserviceConstants.strClassName, div);
		//Checks if Category is Present in the response
		boolean categoryFound = SMSubDivisionTreeUtil.checkBOexist(SMSubDivisionTreeUtil.getHmCategory(), div.getMdmCategory(), SMInboundWebserviceConstants.strCategoryName,div);
		//Checks if GoM is Present in the response
		boolean gomFound = SMSubDivisionTreeUtil.checkBOexist(SMSubDivisionTreeUtil.getHmGoM(), div.getMdmGom(), SMInboundWebserviceConstants.strGomName,div);
		//Checks if Sub_Class is Present in the response
		boolean subClassFound = SMSubDivisionTreeUtil.checkBOexist(SMSubDivisionTreeUtil.getHmSubClass(), div.getMdmSubClass(), SMInboundWebserviceConstants.strCategoryName,div);
		//Checks if Sub_Category is Present in the response
		boolean subCategoryFound = SMSubDivisionTreeUtil.checkBOexist(SMSubDivisionTreeUtil.getHmSubCategory(), div.getMdmSubCategory(), SMInboundWebserviceConstants.strSubCategoryName,div);
		if(classFound && categoryFound){
			if(gomFound && subClassFound && subCategoryFound){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Generates XML for the Division Response.
	 * @param response - GetDivisionUpdatesRequestResponse
	 * @param type - String
	 * @throws smsfJaxnExp - JAXBException
	 */
	public static void generateXMLFileForSubDivisionTree(GetDivisionUpdatesRequestResponse response,String type) throws WTException, IOException{
		javax.xml.bind.JAXBContext jaxbContext;
		try {
			jaxbContext = javax.xml.bind.JAXBContext
					.newInstance(GetDivisionUpdatesRequestResponse.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			QName qName = new QName(GetDivisionUpdatesRequestResponse.class.getSimpleName());
			// Marshaller marshaller = jc.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			JAXBElement<GetDivisionUpdatesRequestResponse> jaxbEle = new JAXBElement<GetDivisionUpdatesRequestResponse>(qName, GetDivisionUpdatesRequestResponse.class,response);
			//Parameters to be changed 
			LOGGER.debug("Type >>>>>>>>>>>>>>>>> "+type);
			//call processXMLFiles method 
			File xmlFile = SMUtill.processXMLFiles(type,Integer.valueOf(SMSubdivisionDataRequestWebClient.getRequestID()),SMInboundWebserviceConstants.SUB_DIVISION_FILE_LOCATION);
			jaxbMarshaller.marshal(jaxbEle, xmlFile );
			// Writing to XML file
			jaxbMarshaller.marshal(response, xmlFile);
			// Writing to console
			jaxbMarshaller.marshal(response, xmlFile);
		} catch (JAXBException smsfJaxnExp) {
			smsfJaxnExp.printStackTrace();
		}
	}
}
