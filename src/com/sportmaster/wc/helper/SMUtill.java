package com.sportmaster.wc.helper;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import java.util.List;


import wt.util.WTException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;

import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.SortHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;

/**
 * SMListValuesDataRequestQueueInitializer.java
 * This class is using to call the methods defined in process class.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMUtill {

	public static final String EMPTY_STRING = "";
	
	public static final Integer  REQUEST_IDD=Integer.valueOf(SMInboundWebserviceConstants.REQUEST_ID_CONST_VALUE);

 
	/**.
	 * The method getDate returns date in the format yyyy-mm- dd.
	 * @param cal Calendar
	 * @return cal.year month date String
	 */
	public  String getDate(Calendar cal){
		// Integration CR
		return EMPTY_STRING + cal.get(Calendar.YEAR) + (cal.get(Calendar.MONTH)+1) + cal.get(Calendar.DATE);
	}

	/**.
	 * The method getTimeForXML returns getTimeForXML in the format hh:mm:ss.
	 * @param cal Calendar
	 * @return HOUR_OF_DAY MINUTE SECOND MILLISECOND String
	 */
	public  String getTimeForXML(Calendar cal){
		return EMPTY_STRING + cal.get(Calendar.HOUR_OF_DAY) +
				(cal.get(Calendar.MINUTE)) + cal.get(Calendar.SECOND)+cal.get(Calendar.MILLISECOND);
	}

	/**
	 * Saving the file to system location.
	 * @param type the type
	 * @param requestID the requestID
	 * @return  file 
	 * @throws WTException the WTException
	 * @throws IOException the IOException
	 */
	public static File processXMLFiles(String type,int requestID, String fileLocation)
			throws WTException, IOException {
		File xmlFile = null;

		SMUtill smUtill = new SMUtill();
		/** Get Calendar Instance */
		final Calendar cal = Calendar.getInstance();
		/** Get Current Date */
		final String todaysDate = smUtill.getDate(cal);
		/** Get current Time */
		final String timeNow = smUtill.getTimeForXML(cal);

		final String completePath = fileLocation+File.separator+ todaysDate+"_"+timeNow+"_"+type+"_"+requestID+"_Processing.xml";
		final String totalPath = completePath.trim();
		xmlFile = new File(totalPath);

		return xmlFile;
	}
	
	/**
	 * Added method for getting mdmID generation phase-4
	 * Saving the file to system location.
	 * @param type the type
	 * @param requestID the requestID
	 * @return  file 
	 * @throws WTException the WTException
	 * @throws IOException the IOException
	 */
	public static File processXMLFiles(String type,int requestID, String fileLocation,String mdmID)
			throws WTException, IOException {
		File xmlFile = null;

		SMUtill smUtill = new SMUtill();
		/** Get Calendar Instance */
		final Calendar cal = Calendar.getInstance();
		/** Get Current Date */
		final String todaysDate = smUtill.getDate(cal);
		/** Get current Time */
		final String timeNow = smUtill.getTimeForXML(cal);
		//Phase -4
        if("FAKEID-000".equalsIgnoreCase(mdmID) || "EmptyMDMID".equalsIgnoreCase(mdmID)) {
		final String completePath = fileLocation+File.separator+ todaysDate+"_"+timeNow+"_"+type+"_"+requestID+"_Processing.xml";
		final String totalPath = completePath.trim();
		xmlFile = new File(totalPath);
		return xmlFile;
        }
        final String completePath = fileLocation+File.separator+ todaysDate+"_"+timeNow+"_"+type+"_"+requestID+"_"+mdmID+"_Processing.xml";
		final String totalPath = completePath.trim();
		xmlFile = new File(totalPath);
		return xmlFile;
	}

	/**
	 * this method return unique request id.
	 * @return int
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public int  getRequestID() throws WTException{
		int requestId=REQUEST_IDD;
		String lcsLogEntry="LCSLogEntry";
		FlexType logType = FlexTypeCache.getFlexTypeFromPath(SMInboundWebserviceConstants.LOGENTRY_LISTVALUE_TYPE);
		FlexTypeAttribute att = logType.getAttribute(SMInboundWebserviceConstants.REQUEST_ID);
		SearchResults results = null;
		PreparedQueryStatement statement = new PreparedQueryStatement();//Creating Statement.
		
		statement.appendFromTable(lcsLogEntry);
		statement.appendSelectColumn(new QueryColumn(lcsLogEntry, "idA2A2"));
		statement.appendSelectColumn(lcsLogEntry, att.getColumnName());
		//add tables
		statement.appendFromTable(lcsLogEntry);

		statement.appendCriteria(new Criteria(lcsLogEntry, "flexTypeIdPath", logType.getIdPath(),Criteria.EQUALS));
		statement.appendOrIfNeeded();
		logType = FlexTypeCache.getFlexTypeFromPath(SMInboundWebserviceConstants.LOGENTRY_SUBDIVISIONTREE_NMAE);
		statement.appendCriteria(new Criteria(lcsLogEntry, "flexTypeIdPath", logType.getIdPath(),Criteria.EQUALS));

		results=LCSQuery.runDirectQuery(statement);
		List<FlexObject> data=results.getResults();
		int reqId;
		data=(List<FlexObject>) SortHelper.sortFlexObjects(data, lcsLogEntry+"."+att.getColumnName());
		
		if(!data.isEmpty()){
			reqId=data.get(data.size()-1).getInt( lcsLogEntry+"."+att.getColumnName());
			requestId =reqId+1;
			return requestId;
		}
	return requestId+1;
	}

	
	/**
	 * this method is for schedule the timing of Queue
	 * @param scheduleRunTime the scheduleRunTime
	 * @param timezone   the time zone
	 * @param scheduleTimeAMPM the scheduleTimeAMPM
	 * @return
	 */
	public static Timestamp getTimestamp(String scheduleRunTime,String timezone,String scheduleTimeAMPM){
		// Parse hours and minutes
		final String time = scheduleRunTime;
		final String propEntryArray[] = time.split("\\.");
		final String hours = propEntryArray[0];
		Integer h = Integer.parseInt(hours);
		final String minutes = propEntryArray[1];
		Integer m = Integer.parseInt(minutes);

		// Get the current calendar date/time
		Calendar cal12AM = Calendar.getInstance(TimeZone.getTimeZone(timezone));
		
	
		// Set the timezone and scheduled time for today
		//cal12AM.setTimeZone(TimeZone.getTimeZone(timezone));
		cal12AM.set(Calendar.HOUR, h);
		cal12AM.set(Calendar.MINUTE, m);
		cal12AM.set(Calendar.SECOND, 00);

		// If h is 12, set to 0 in order to create 24-hour value next
		
		if ((h == 12) && "AM".equalsIgnoreCase(scheduleTimeAMPM)) {
			h = 0;
		}
      
		// Set for AM or PM
		if ("AM".equalsIgnoreCase(scheduleTimeAMPM)) {
			//cal12AM.set(Calendar.AM_PM, Calendar.AM);
			cal12AM.set(Calendar.HOUR_OF_DAY, h);
		} else {
			//cal12AM.set(Calendar.AM_PM, Calendar.PM);
			cal12AM.set(Calendar.HOUR_OF_DAY, h + 12);
		}

		// Get the current calendar date/time
		Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone(timezone));

		// Set the timezone for the current time
		//currentTime.setTimeZone(TimeZone.getTimeZone(timezone));

		// Declare a null timestamp
		Timestamp timeStamp = null;
	
	
			// If current time is later than scheduled time, schedule
			// for tomorrow. Else schedule for today.
			if (currentTime.after(cal12AM)) {
				
				timeStamp = new Timestamp(cal12AM.getTimeInMillis() + 24L * 60 * 60 * 1000);
			} else {
				
				timeStamp = new Timestamp(cal12AM.getTimeInMillis());
			}
			return timeStamp;
	}





}
