package com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.processor;

import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSLogEntry;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.helper.SMLVIntegrationHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMIntegrationUtill;

/**
 * SMLVLogEntryDataProcessor.java
 * This class is using to call the methods defined in process class.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMLVLogEntryDataProcessor {


	/**
	 * constructor.
	 */
	protected SMLVLogEntryDataProcessor() {
	}

	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMLVLogEntryDataProcessor.class);
	
	/**
	 *  process log entry data to DB.
	 * @param dbLogEntryData - Map
	 * @param mdmId - String
	 * @param dataValues - Map
	 */
	public static void processLogEntry(Map<String, FlexObject> dbLogEntryData,String mdmId,Map<String, String> dataValues){
		FlexObject fo;
		String ida2a2;
		Map<String, String> attKeyColumnMap=SMIntegrationUtill.getAttKeyColumnMap();
		LOGGER.debug("Processing log entry for MDM ID : "+mdmId);
		try {
			if(dbLogEntryData.containsKey(mdmId)){
				//update
				fo= dbLogEntryData.get(mdmId);
					LCSLogEntry logEntry=SMIntegrationUtill.getLogEntryFromFlexObject(fo);
					LOGGER.debug("Updating log entry for MDM ID  : "+dataValues.get(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_MDMID_KEY));
					SMIntegrationUtill.updateLogEntry(logEntry, fo, dataValues, attKeyColumnMap);
			}else{
				//create
				LOGGER.debug("Creating log entry for MDM ID  : "+dataValues.get(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_MDMID_KEY));
				ida2a2=SMIntegrationUtill.createLogEntry(dataValues, SMInboundWebserviceConstants.LOGENTRY_LISTVALUE_TYPE, attKeyColumnMap);
				SMLVIntegrationHelper.reConfigureColection(null,dbLogEntryData, ida2a2, dataValues, attKeyColumnMap, SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_MDMID_KEY, "LCSLOGENTRY.IDA2A2");
			}
		} catch (WTException wtExp) {
			wtExp.printStackTrace();
			LOGGER.debug("ERROR OCCURED : "+wtExp.getLocalizedMessage());
		}
	}
}
