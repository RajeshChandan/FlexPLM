package com.sportmaster.wc.interfaces.webservices.outbound.feedback.processor;

import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSLogEntryLogic;
import com.sportmaster.wc.interfaces.webservices.bean.BOEnumeration;
import com.sportmaster.wc.interfaces.webservices.bean.DivisionStatusInformation;
import com.sportmaster.wc.interfaces.webservices.bean.GetStatusDivisionRequestResponse;
import com.sportmaster.wc.interfaces.webservices.bean.GetStatusSimpleBOInformationRequestResponse;
import com.sportmaster.wc.interfaces.webservices.bean.ObjectFactory;
import com.sportmaster.wc.interfaces.webservices.bean.StatusInformation;
import com.sportmaster.wc.interfaces.webservices.bean.StatusRequestType;
import com.sportmaster.wc.interfaces.webservices.bean.UpdateInformation;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMIntegrationUtill;
import com.sportmaster.wc.interfaces.webservices.outbound.feedback.util.SMFBIntegrationUtilBean;

/**
 * SMListVaulesFeedbackProcessor.java
 * This class has methods for process the feedback response Integration.
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */

public class SMListVaulesFeedbackProcessor {
	/**
	 * ERROR_OCCOURED_MESSAGE.
	 */
	private static final String ERROR_OCCOURED_MESSAGE = "Error OCCOURED -  ";
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMListVaulesFeedbackProcessor.class);

	/**
	 * Constructor.
	 */
	protected SMListVaulesFeedbackProcessor() {
	}


	/**
	 *  processing the list business object feedback queue.
	 * @param logentryData - logentryData
	 * @param factory the logentryData
	 * @param name the name
	 * @param smfbBean - smfbBean
	 * @return StatusInformation
	 */
	public static StatusInformation processFeedbackQueue(Map<String, LCSLogEntry> logentryData,ObjectFactory factory,BOEnumeration name,SMFBIntegrationUtilBean smfbBean){
		LOGGER.debug("Setting StatusInfromation");
		StatusInformation  lvFeedbackStatus=factory.createStatusInformation();
		//setting the BO Name
		lvFeedbackStatus.setName(name);
		//setting the request ID
		lvFeedbackStatus.setRequestId(smfbBean.getFbrequestId());
		if(logentryData.isEmpty()){
			LOGGER.debug("NO DATA FOUND TO SEND REQUEST FOR "+name.value());
		}
		UpdateInformation updateInfo;
		for(Map.Entry<String, LCSLogEntry> logentry:logentryData.entrySet()){
			try {
				LOGGER.debug("Setting update infromation for MDM ID : "+logentry.getKey());
				updateInfo= factory.createUpdateInformation();
				updateInfo.setErrMessage((String) logentry.getValue().getValue(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_ERROR_KEY));
				updateInfo.setMdmBO(logentry.getKey());
				updateInfo.setRequestType(getRequestType((String) logentry.getValue().getValue(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_INT_STATUS_KEY)));
				lvFeedbackStatus.getUpdateInformation().add(updateInfo);
				smfbBean.setFbObjCount(1);
			} catch (WTException wtExp) {
				wtExp.printStackTrace();
				smfbBean.setFbFailCount(1);
				LOGGER.debug("WTException Occured : "+wtExp.getLocalizedMessage());
			}
		}
		return lvFeedbackStatus;

	}

	/**
	 * processing sub division tree  feedback response.
	 * @param logentryData the logentryData
	 * @param factory the logentryData
	 * @param smfbBean - smfbBean
	 * @return  DivisionStatusInformation
	 */

	public static DivisionStatusInformation processDivisionFeedBackQueue(Map<String, FlexObject> logentryData,ObjectFactory factory,SMFBIntegrationUtilBean smfbBean){
		LOGGER.debug("Setting status infromation for Sub Class Division Tree");
		UpdateInformation updateInfo;
		LCSLogEntry logEntryObj;
		DivisionStatusInformation divStatus= factory.createDivisionStatusInformation();
		divStatus.setRequestId(smfbBean.getFbrequestId());
		if(logentryData.isEmpty()){
			LOGGER.debug("NO DATA FOUND TO SEND FOR SUB DIVISION TREE FEEDBACK REQUEST");
		}
		for(Map.Entry<String, FlexObject> logentry:logentryData.entrySet()){
			try{
				LOGGER.debug("Setting Update Infromation for MDM ID : "+logentry.getKey());
				logEntryObj=SMIntegrationUtill.getLogEntryFromFlexObject(logentry.getValue());
				updateInfo= factory.createUpdateInformation();
				updateInfo.setErrMessage((String) logEntryObj.getValue(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_ERROR_KEY));
				updateInfo.setMdmBO(logentry.getKey());
				updateInfo.setRequestType(getRequestType((String) logEntryObj.getValue(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_INT_STATUS_KEY)));
				divStatus.getUpdateInformation().add(updateInfo);
				smfbBean.setFbObjCount(1);

			} catch (WTException wtExp) {
				wtExp.printStackTrace();
				smfbBean.setFbFailCount(1);
				LOGGER.debug("WTException Occured : "+wtExp.getLocalizedMessage());
			}
		}
		return divStatus;
	}


	/**
	 * getting the request type of Business Object.
	 * @param requestType - requestType
	 * @return StatusRequestType
	 */
	public static StatusRequestType getRequestType(String requestType){
		StatusRequestType type=null;
		if(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_INTEGRATED.equals(requestType)){
			type=StatusRequestType.INTEGRATED;
		}else if(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_NOT_INTEGRATED.equals(requestType)){
			type=StatusRequestType.NO_INTEGRATED;
		}
		return type;
	}

	/**
	 * processing the response.
	 * @param logentryData the logentryData
	 * @param lvResponse - GetStatusSimpleBOInformationRequestResponse
	 * @param smfbBean - smfbBean
	 * @throws WTException  the WTException
	 * @throws WTPropertyVetoException 
	 */
	public static void processListValuesFeedbackResponse(Map<String, LCSLogEntry> logentryData,GetStatusSimpleBOInformationRequestResponse lvResponse,SMFBIntegrationUtilBean smfbBean) {
		LCSLogEntry logEntry;
		boolean receivedStatus=true;
		LOGGER.debug("processing list values Logentry to set status as PROCESSED");
		
		if(SMInboundWebserviceConstants.RECEIVED_INVALID.equalsIgnoreCase(lvResponse.getStatus().value())){
			receivedStatus=false;
			LOGGER.debug("Response status is : "+lvResponse.getStatus().value());
		}
		for(Map.Entry<String, LCSLogEntry> entry:logentryData.entrySet()){
			try {
				LOGGER.debug("Setting status for MDM ID : "+entry.getKey());
				logEntry=entry.getValue();
				if(receivedStatus){
					logEntry.setValue(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_STATUS_KEY, SMInboundWebserviceConstants.LOGENTRY_STATUS_VALUE_SENT);
				}
				logEntry.setValue(SMInboundWebserviceConstants.REQUEST_ID, String.valueOf(smfbBean.getFbrequestId()));
				new LCSLogEntryLogic().saveLog(logEntry, true);
			} catch (WTException wtExp) {
				LOGGER.debug(ERROR_OCCOURED_MESSAGE+wtExp.getLocalizedMessage());
				wtExp.printStackTrace();
			}
		}
	}

	/**
	 * processing the sub division tree response.
	 * @param logentryData the logentryData
	 * @param divResponse - GetStatusDivisionRequestResponse
	 * @param smfbBean - smfbBean
	 * @throws WTException  the WTException
	 * @throws WTPropertyVetoException 
	 */
	public static void processDivsionFeedbackResponse(Map<String, FlexObject> logentryData,GetStatusDivisionRequestResponse divResponse,SMFBIntegrationUtilBean smfbBean) {
		LCSLogEntry logEntry;
		boolean receivedStatus=true;
		LOGGER.debug("processing sub class division tree Logentry to set status as PROCESSED");
		if(SMInboundWebserviceConstants.RECEIVED_INVALID.equalsIgnoreCase(divResponse.getStatus().value())){
			receivedStatus=false;
			LOGGER.debug("Response status is : "+divResponse.getStatus().value());
		}
		for(Map.Entry<String, FlexObject> entry:logentryData.entrySet()){
			try {
				LOGGER.debug("Setting status for MDM ID : "+entry.getKey());
				logEntry=SMIntegrationUtill.getLogEntryFromFlexObject(entry.getValue());
				if(receivedStatus){
					//seeting log entry data
					logEntry.setValue(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_STATUS_KEY, SMInboundWebserviceConstants.LOGENTRY_STATUS_VALUE_SENT);
				}
				logEntry.setValue(SMInboundWebserviceConstants.REQUEST_ID, String.valueOf(smfbBean.getFbrequestId()));
				new LCSLogEntryLogic().saveLog(logEntry, true);
			} catch (WTException divWtExp) {
				LOGGER.debug(ERROR_OCCOURED_MESSAGE+divWtExp.getLocalizedMessage());
				divWtExp.printStackTrace();
			}
		}
	}
}
