package com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.bean.Division;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.client.SMSubdivisionDataRequestWebClient;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.helper.SMSubDivisionHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.util.SMSubDivisionTreeUtil;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;

/**
 * SMSubDivisionLogEntryProcessor.java
 * Processing of Log Entry Object.
 * @author 'true' ITC_Infotech
 * @version 1.0
 */
public class SMSubDivisionLogEntryProcessor {
	
	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSubDivisionLogEntryProcessor.class);
	
	/**
	 * Constructor.
	 */
	protected SMSubDivisionLogEntryProcessor(){
		//protected constructor
	}
	
	/**
	 * Setting log entry data.
	 * @param lcsObj - LCSLifeCycleManaged
	 * @param div - Division
	 * @throws WTException - WTExceptions
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void setLogEntry(LCSLifecycleManaged lcmObj, Division div) throws WTPropertyVetoException, WTException{
		LOGGER.debug("Inside setLogEntry method !!");
		if(SMSubDivisionTreeUtil.getHmLogEntryMDMID().containsKey(div.getMdmBO())){
			//update existing log entry
			updateLogEntry(lcmObj, div, SMSubDivisionTreeUtil.getHmLogEntryMDMID().get(div.getMdmBO()));
		}else{
			//create new log entry
			createLogEntry(lcmObj, div);
		}
	}

	/**
	 * Creating log entry object.
	 * @param lcmObj - LCSLifeCycleManaged
	 * @param div - Division
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException 
	 */
	public static void createLogEntry(LCSLifecycleManaged lcmObj, Division div) throws WTException, WTPropertyVetoException{
		LOGGER.debug("Inside createLogEntry method !!!!");
		com.lcs.wc.flextype.FlexType logType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMInboundWebserviceConstants.strLogEntryPath);
		LCSLogEntry logEntryObj = LCSLogEntry.newLCSLogEntry();
		//Setting attribute values
		logEntryObj.setFlexType(logType);
		logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_REQUEST_ID,SMSubdivisionDataRequestWebClient.getRequestID());
		logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENTRY_MDMID_KEY, div.getMdmBO());
		//logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_OBJECT_NAME, div.getName());
		logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_REQUEST_TYPE, div.getRequestType().value());
		logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_BO_TYPE, SMInboundWebserviceConstants.logEntrySubDivisionPath);
		logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_ERROR_REASON, SMSubDivisionTreeDataProcessor.getStrErrorString());
		logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_INTEGRATION_STATUS_KEY, SMSubDivisionTreeDataProcessor.getINTEGRATIONSTATUS());
		logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENTRY_STATUS,SMInboundWebserviceConstants.LOG_ENTRY_STATUS_PENDING);
		if(null != lcmObj){
			logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_NAME, lcmObj.getValue(SMInboundWebserviceConstants.SUB_DIVISION_NAME));
			logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_OBJECTID_KEY, FormatHelper.getNumericObjectIdFromObject(lcmObj));
			SMSubDivisionTreeUtil.getHmLogEntryMDMID().put(div.getMdmBO(), logEntryObj);
		}
		else{
			SMSubDivisionTreeUtil.getHmLogEntryMDMID().put(div.getMdmBO(), logEntryObj);
			logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_OBJECTID_KEY, SMSubDivisionTreeUtil.checkLogEntryObjectID(div));
			logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_NAME, SMSubDivisionTreeUtil.checkLogEntryName(div));
		}
		SMSubDivisionHelper.persistLogEntry(logEntryObj);
		
	}
	
	/**
	 * Updates existing log entry.
	 * @param lcmObj - LCSLifecycleManaged
	 * @param div - Division
	 * @param logEntryObj - LCSLogEntry
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void updateLogEntry(LCSLifecycleManaged lcmObj, Division div, LCSLogEntry logEntryObj) throws WTException, WTPropertyVetoException{
		LOGGER.debug("Inside updatelogEntry method !!!!!!!!!!!!!!!!");
		logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_REQUEST_ID, SMSubdivisionDataRequestWebClient.getRequestID());
		logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_REQUEST_TYPE, div.getRequestType().value());
		logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_BO_TYPE, SMInboundWebserviceConstants.logEntrySubDivisionPath);
		logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_ERROR_REASON, SMSubDivisionTreeDataProcessor.getStrErrorString());
		logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_INTEGRATION_STATUS_KEY, SMSubDivisionTreeDataProcessor.getINTEGRATIONSTATUS());
		logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENTRY_STATUS,SMInboundWebserviceConstants.LOG_ENTRY_STATUS_PENDING);
		if(null != lcmObj){
			logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_NAME, lcmObj.getValue(SMInboundWebserviceConstants.SUB_DIVISION_NAME));
			logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_OBJECTID_KEY, FormatHelper.getNumericObjectIdFromObject(lcmObj));
		}
		else{
			logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_OBJECTID_KEY, SMSubDivisionTreeUtil.checkLogEntryObjectID(div));
			logEntryObj.setValue(SMInboundWebserviceConstants.LOG_ENRTY_NAME, SMSubDivisionTreeUtil.checkLogEntryName(div));
		}
		SMSubDivisionHelper.persistLogEntry(logEntryObj);
	}
	
	
	
	/**
	 * Queries the Log Entry.
	 * @param flexPath - String
	 * @throws WTException - WTException
	 * @returns hashMapLogEntry - HashMap<String, LCSLogEntry>
	 */
	public static Map<String, LCSLogEntry> queryLogEntry(String flexPath, String attribute) throws WTException{
		LOGGER.debug("Inside queryLogEntry method !!!!!!!!!!");
		//Hashmap to store Log entries
		Map<String, LCSLogEntry> hashMapLogEntry=new HashMap<String, LCSLogEntry>();
		com.lcs.wc.flextype.FlexType logType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMInboundWebserviceConstants.strLogEntryPath);
		com.lcs.wc.db.PreparedQueryStatement statement = new com.lcs.wc.db.PreparedQueryStatement();//Creating Statement.
		statement.appendFromTable(LCSLogEntry.class);
		statement.appendSelectColumn("LCSLogEntry", logType.getAttribute(attribute).getColumnName());//append column
		statement.appendSelectColumn("LCSLogEntry", "IDA2A2");//append column
		statement.appendCriteria(new Criteria("LCSLogEntry", "flexTypeIdPath", logType.getIdPath(),Criteria.EQUALS));//adding criteria
		com.lcs.wc.db.SearchResults results = null;
		//executing  statement
		results =LCSQuery.runDirectQuery(statement);
		List<?> dataCollection= results.getResults();
		FlexObject fo=null;
		LOGGER.debug("Log Entry data collection Size >>>>>>>\t"+dataCollection.size());
		if (dataCollection.size() > 0) {
			for(Object obj:dataCollection){
				fo= (FlexObject) obj;
				//LOGGER.debug("LogEntry Object >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  "+fo);
				LCSLogEntry logEntry=(LCSLogEntry) LCSQuery.findObjectById("com.lcs.wc.foundation.LCSLogEntry:"+fo.getString("LCSLOGENTRY.IDA2A2"));
				//storing in hashmap
				hashMapLogEntry.put((String)logEntry.getValue(attribute), logEntry);
			}	
		}
		//returning log entry
		return (HashMap<String, LCSLogEntry>) hashMapLogEntry;
	}
	
	

}
