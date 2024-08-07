package com.sportmaster.wc.interfaces.queue.subdivisionqueue;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.queue.service.SMIntegrationServiceManager;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;

/**
 * SMSubDivisionDataRequestQueueIntializer.java
 * This class initializes the schdule queue.
 * And calls the request task.
 * @author 'true' ITC_Infotech
 * @version 1.0
 */
public class SMSubDivisionDataRequestQueueIntializer {

	/**
	 * 
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSubDivisionDataRequestQueueIntializer.class);
	/**
	 * Declaring Error Message.
	 */
	private static String errorMsg;
	/**
	 * Constructor.
	 */
	protected SMSubDivisionDataRequestQueueIntializer(){
		//protected Constructor
	}

	/**
	 * Initializes the queue.
	 * @param args - String[]
	 * @throws WTException - WTException
	 */
	public static void main(String args[]) throws WTException{
		
		//initializing timestamp.
		Timestamp subDivTimeStamp;
		//Action
		String subDivAction;
		if (args.length <1) {
			LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.interfaces.queue.subdivisionqueue.SMSubDivisionDataRequestQueueIntializer [options]\n");
			LOGGER.debug(
					"SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			LOGGER.debug(
					"EXECUTE : Synchronous Execution (Initialize execution now and process the Sub class Division Tree data )\n");
			System.exit(0);
		}
		subDivAction=args[0];
		try
		{      
			//If action is EXECUTE.
			if (SMInboundWebserviceConstants.EXECUTE.equalsIgnoreCase(subDivAction)) {
				// Get the current timestamp - can we not just use
				// currentTime, maybe add 500 msec to account for CPU lag?
				subDivTimeStamp = new Timestamp(System.currentTimeMillis());
			}
			// If running in queue the timestamp is scheduled time
			//Action is Schedule
			else if (SMInboundWebserviceConstants.SCHEDULE.equals(subDivAction)) {
				subDivTimeStamp=SMUtill.getTimestamp(SMInboundWebserviceConstants.SUB_DIVISION_QUEUE_START_TIME, SMInboundWebserviceConstants.TIME_ZONE, SMInboundWebserviceConstants.SUB_DIVISION_QUEUE_AM);
			} // If neither then should not run
			else {
				return;
			}

			SMIntegrationServiceManager smIntegrationServiceManager = new SMIntegrationServiceManager();
			//Setting schedule time
			//Initializing the queue and initiating the task
			smIntegrationServiceManager.scheduleJobForIntegration(SMInboundWebserviceConstants.SUB_DIVISION_QUEUE_NAME, true,"executeSubDivisionDataRequestQueueEntry","com.sportmaster.wc.interfaces.queue.subdivisionqueue.SMSubdivisionDataRequestTask",subDivTimeStamp);
		} catch (WTException wtExp) {
			errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
			LOGGER.error(SMInboundWebserviceConstants.QUEUE_START_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_START_ERROR_MESSAGE+SMInboundWebserviceConstants.SUB_DIVISION_QUEUE_NAME);
			LOGGER.error(errorMsg);
			wtExp.printStackTrace();
		}
	}
}
