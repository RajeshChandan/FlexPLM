package com.sportmaster.wc.interfaces.queue.subdivisionqueue;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.sportmaster.wc.interfaces.queue.service.SMQueueRescheduleService;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.processor.SMSubDivisionTreeDataProcessor;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;

/**
 * SMSubdivisionDataRequestTask.java
 * Executes the request task once queue is initialized.
 * @author 'true' ITC_Infotech
 * @version 1.0
 */
public  class SMSubdivisionDataRequestTask {
	
	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSubDivisionDataRequestQueueIntializer.class);
	/**
	 * Declaration for Error Message.
	 */
	private static String errorMsg;
	/**
	 * Constructor.
	 */
	protected SMSubdivisionDataRequestTask() {
		//protected Constructor
	}
	
	/**
	 * Executes the request.
	 * @return statusInfo - StatusInfo
	 */
	public static synchronized StatusInfo executeSubDivisionDataRequestQueueEntry() {
		long subDivisionIntervalTimeinMilliSeconds = 0;
		//Initialize StatusInfo variable
		StatusInfo statusInfo=null;
		try{
			//Setting interval time for queue rescheduling
			subDivisionIntervalTimeinMilliSeconds = System.currentTimeMillis()+SMInboundWebserviceConstants.SUB_DIVISION_QUEUE_INTERVAL_IN_MINS*60*1000;
			//Calling Sub Division tree processor
			SMSubDivisionTreeDataProcessor.subDivisionTreeProcessor();
		}finally{
			//rescheduling the queue
			try {
				LOGGER.debug("Rescheduling queue entry for next run");
				//Setting time for queue rerun
				statusInfo=SMQueueRescheduleService.rescheduleEntry(new Timestamp(subDivisionIntervalTimeinMilliSeconds));
				LOGGER.debug("#######################   SUB DIVISION TREEE INBOUND INTEGRATION ENDS  ######################");
			} catch (WTPropertyVetoException wtPerptyExp) {
				//Error while rescheduling queue
				errorMsg = wtPerptyExp.getLocalizedMessage()==null? wtPerptyExp.getMessage():wtPerptyExp.getLocalizedMessage();
				LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMInboundWebserviceConstants.SUB_DIVISION_QUEUE_NAME);
				LOGGER.error(errorMsg);
			}catch (WTException wtExp) {
				errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
				LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMInboundWebserviceConstants.SUB_DIVISION_QUEUE_NAME);
				LOGGER.error(errorMsg);
			}
		}
		return statusInfo;
	}

}
