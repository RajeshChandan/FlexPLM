package com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.service;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.utill.SMLVIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.queue.service.SMIntegrationServiceManager;
import com.sportmaster.wc.interfaces.queue.service.SMQueueRescheduleService;

/**
 * SMListValuesDataRequestQueueInitializer.java
 * This class is using to call the methods defined in process class.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMListValuesDataRequestQueueInitializer {


	/**
	 * SMLVIntegrationBean bean class.
	 */
	private static SMLVIntegrationBean lvBean;
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMListValuesDataRequestQueueInitializer.class);
	private static String errorMsg;

	/**
	 * constructor.
	 */
	protected SMListValuesDataRequestQueueInitializer() {

	}

	/**
	 *  main method : will add an entry in queue manager.
	 * @param args
	 */
	public static void main(String[] args) {
		Timestamp listVlauesTimeStamp;
		String listValuesAction;
		//checking arguement length
		if (args.length <1) {
			//adding debug message
			LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.service.SMListValuesDataRequestQueueInitializer [options]\n");
			LOGGER.debug(
					"SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			LOGGER.debug(
					"EXECUTE : Synchronous Execution (Initialize execution now and process the List Values data )\n");
			//doing system exit
			System.exit(0);
		}
		//getting first agrument
		listValuesAction=args[0];
		try
		{	
			if (SMInboundWebserviceConstants.EXECUTE.equalsIgnoreCase(listValuesAction)) {
				// Get the current timestamp - can we not just use
				// currentTime, maybe add 500 msec to account for CPU lag?
				listVlauesTimeStamp = new Timestamp(System.currentTimeMillis());
			}
			// If running in queue the timestamp is scheduled time
			else if (SMInboundWebserviceConstants.SCHEDULE.equals(listValuesAction)) {
				listVlauesTimeStamp=SMUtill.getTimestamp(SMInboundWebserviceConstants.LISTVALUES_SCHEDULE_TIME, SMInboundWebserviceConstants.TIME_ZONE, SMInboundWebserviceConstants.LISTVALUES_SCHEDULE_TIME_AM_PM);
			} // If neither then should not run
			else {
				return;
			}
			SMIntegrationServiceManager service = new SMIntegrationServiceManager();
			service.scheduleJobForIntegration(SMInboundWebserviceConstants.LIST_VALUES_DATA_REQUEST_QUEUE_NAME,true,"executeListValuesDataRequestQueueEntry",SMListValuesDataRequestQueueInitializer.class.getName(),listVlauesTimeStamp);

			System.exit(0);
		}catch (WTException smWTExp){
			errorMsg = smWTExp.getLocalizedMessage()==null? smWTExp.getMessage():smWTExp.getLocalizedMessage();
			LOGGER.error(SMInboundWebserviceConstants.QUEUE_START_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_START_ERROR_MESSAGE+SMInboundWebserviceConstants.LIST_VALUES_DATA_REQUEST_QUEUE_NAME);
			LOGGER.error(errorMsg);
			smWTExp.printStackTrace();
		}
	}


	/**
	 * Set Logger Summary.
	 * @return String values of log
	 */
	public static String setLogger(){
		StringBuffer summary=new StringBuffer();
		//creating String Buffer for multi line String
		summary.append("LIST VALUES INBOUND INTEGRATION SUMMARY:\n");
		summary.append("*******************************************************************************\n");
		summary.append("** TOTAL NUMBER OF List Values UPDATED SUCCESSFULLY-"+lvBean.getLvUpdateCount()+"\n");
		summary.append("** TOTAL NUMBER OF NEW List Values CREATED -"+lvBean.getLvCreateObjCount()+"\n");
		summary.append("** TOTAL NUMBER OF List Values FAILED-"+lvBean.getLvFailCount()+"\n");
		summary.append("** INTEGRATION START TIME -"+lvBean.getStartTimeInGMT()+"\n");
		summary.append("** INTEGRATION END TIME -"+lvBean.getEndTimeinGMT()+"\n");
		summary.append("*******************************************************************************");
		return summary.toString();
		//Returning log value.
	}

	/**
	 * This method is using to execute the registered task from queue manager based on the given time interval and updating the queue object for rescheduling.
	 * @return StatusInfo
	 */
	public static synchronized StatusInfo executeListValuesDataRequestQueueEntry ()    {
		StatusInfo statusInfo=null;
		long lvIntervalTimeinMilliSeconds=0;
		try {
			LOGGER.debug("LIST VALUES INTEGRATION STARTS");
			LOGGER.debug("Integration Queue Name: " +SMInboundWebserviceConstants.LIST_VALUES_DATA_REQUEST_QUEUE_NAME);
			lvBean= new SMLVIntegrationBean();
			long fbCurentTime=System.currentTimeMillis();
			//setting queue start time
			lvBean.setStartTimeInGMT(new Date(fbCurentTime).toString());
			lvIntervalTimeinMilliSeconds=(fbCurentTime+SMInboundWebserviceConstants.LIST_VALUES_QUEUE_INTERVAL * 60* 1000);
			//processing queue entry.
			new SMListValuesDataRequestWebClientService().invokeRequest(lvBean);
			//setting queue end time
			lvBean.setEndTimeinGMT(new Date(System.currentTimeMillis()).toString());

		} 
		finally{
			try {
				LOGGER.debug("Rescheduling queue entry for next run");
				statusInfo=SMQueueRescheduleService.rescheduleEntry(new Timestamp(lvIntervalTimeinMilliSeconds));
				LOGGER.debug("LIST VALUES INTEGRATION ENDS");
				LOGGER.debug(setLogger());
				lvBean=null;
			} catch (WTPropertyVetoException wtPerptyExp) {
				errorMsg = wtPerptyExp.getLocalizedMessage()==null? wtPerptyExp.getMessage():wtPerptyExp.getLocalizedMessage();
				LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMInboundWebserviceConstants.LIST_VALUES_DATA_REQUEST_QUEUE_NAME);
				LOGGER.error(errorMsg);
				wtPerptyExp.printStackTrace();
			} catch (WTException wtExp) {
				errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
				LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMInboundWebserviceConstants.LIST_VALUES_DATA_REQUEST_QUEUE_NAME);
				LOGGER.error(errorMsg);
				wtExp.printStackTrace();
			}
		}
		return statusInfo;
	}
}
