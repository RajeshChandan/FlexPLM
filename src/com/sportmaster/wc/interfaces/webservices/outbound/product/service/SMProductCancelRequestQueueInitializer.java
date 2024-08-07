package com.sportmaster.wc.interfaces.webservices.outbound.product.service;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.queue.service.SMIntegrationServiceManager;
import com.sportmaster.wc.interfaces.queue.service.SMQueueRescheduleService;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.product.processor.SMCancelRequestProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;


/**
 * SMProductCancelRequestQueueInitializer.java
 * This class used to initialize CANCEL Request queue.
 * it has two option:
 * 		EXECUTE		- execute the queue entry immediately & reschedule for next run.
 * 		SCHEDULE	- recreate queue entry to next run.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMProductCancelRequestQueueInitializer {

	public SMProductCancelRequestQueueInitializer() {
		super();
	}

	/*
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMProductCancelRequestQueueInitializer.class);
	/**
	 * Declaring Error Message.
	 */
	private static String errorMsg;

	/**
	 * Initializes the queue.
	 * @param args - String[]
	 * @throws WTException - WTException
	 */
	public static void main(String[] args) throws WTException{
		//Initializing timestamp.
		Timestamp cancelRequestQueueTime;
		//Action.
		String productInboundFeedbackAction;
		if (args.length <1) {
			LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.interfaces.webservices.outbound.product.service.SMProductCancelRequestQueueInitializer [options]\n");
			LOGGER.debug(
					"SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			LOGGER.debug(
					"EXECUTE : Synchronous Execution (Initialize execution now and process the Product outbound Integartion cancel data )\n");
			System.exit(0);
		}
		productInboundFeedbackAction=args[0];
		try
		{      
			//Execute Action
			if (SMInboundWebserviceConstants.EXECUTE.equalsIgnoreCase(productInboundFeedbackAction)) {

				// Get the current timestamp - can we not just use
				// currentTime, maybe add 500 msec to account for CPU lag?
				cancelRequestQueueTime = new Timestamp(System.currentTimeMillis());
			}

			// If running in queue the timestamp is scheduled time
			//SCHEDULE action.
			else if (SMInboundWebserviceConstants.SCHEDULE.equals(productInboundFeedbackAction)) {

				cancelRequestQueueTime = SMUtill.getTimestamp(
						SMProductOutboundWebServiceConstants.CANCEL_OUTBOUND_QUEUE_START_TIME,
						SMProductOutboundWebServiceConstants.PRODUCT_SEASON_OUTBOUND_QUEUE_TIME_ZONE,
						SMProductOutboundWebServiceConstants.CANCEL_OUTBOUND_QUEUE_START_AM);

				// If neither then should not run
			}else {
				return;
			}

			SMIntegrationServiceManager smIntegrationServiceManager = new SMIntegrationServiceManager();

			//Setting schedule time
			//Initializing the queue and initiating the task
			smIntegrationServiceManager.scheduleJobForIntegration(SMProductOutboundWebServiceConstants.CANCEL_OUTBOUND_QUEUE_NAME,
					true, "executeCancelRequestQueueEntry",
					SMProductCancelRequestQueueInitializer.class.getName(), cancelRequestQueueTime);

		} catch (WTException wtExp) {
			errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
			LOGGER.error(SMInboundWebserviceConstants.QUEUE_START_ERROR_CODE
					+ SMInboundWebserviceConstants.QUEUE_START_ERROR_MESSAGE
					+ SMProductOutboundWebServiceConstants.CANCEL_OUTBOUND_QUEUE_NAME);
			LOGGER.error(errorMsg,wtExp);
		}
	}

	/**
	 * Execute Queue Entry.
	 * @return statusInfo - StatusInfo
	 */
	public static synchronized StatusInfo executeCancelRequestQueueEntry(){

		LOGGER.debug("#######################   PRODUCT CANCEL REQUEST QUEUE INTEGRATION STARTS  ######################");
		//Initialize StatusInfo variable
		StatusInfo statusInfo=null;
		long cancelRequestQueueTimeInMilliSeconds = 0;
		try{
			//Setting interval time for queue rescheduling
			cancelRequestQueueTimeInMilliSeconds = System.currentTimeMillis()+SMProductOutboundWebServiceConstants.CANCEL_OUTBOUND_QUEUE_INTERVAL_IN_MINUTES*60*1000;
			
			LOGGER.debug("Executing cancel request - starts");
			new SMCancelRequestProcessor().executeQueueEntry();
			LOGGER.debug("Execution cancel request - ends");
		}finally{
			//rescheduling the queue
			try {
				LOGGER.debug("Rescheduling queue entry for next run");

				//Setting time for queue rerun
				statusInfo=SMQueueRescheduleService.rescheduleEntry(new Timestamp(cancelRequestQueueTimeInMilliSeconds));

			} catch (WTPropertyVetoException wtPerptyExp) {
				//Error while rescheduling queue
				errorMsg = wtPerptyExp.getLocalizedMessage()==null? wtPerptyExp.getMessage():wtPerptyExp.getLocalizedMessage();
				LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMProductOutboundWebServiceConstants.CANCEL_OUTBOUND_QUEUE_NAME);
				LOGGER.error(errorMsg);
			}catch (WTException wtExp) {
				errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
				LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMProductOutboundWebServiceConstants.CANCEL_OUTBOUND_QUEUE_NAME);
				LOGGER.error(errorMsg,wtExp);
			}
		}

		LOGGER.debug("#######################   PRODUCT CANCEL REQUEST QUEUE INTEGRATION ENDS  ######################");
		return statusInfo;
	}	
}

