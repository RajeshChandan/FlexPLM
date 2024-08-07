package com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.services;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.queue.service.SMIntegrationServiceManager;
import com.sportmaster.wc.interfaces.queue.service.SMQueueRescheduleService;
import com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.processor.SMPricesUpdateFeedbackProcessor;
import com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.util.SMPricesInboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * SMPriceUpdateFeedbackQueueInitializer.java
 * This class used initialize price update feedback queue.
 * it has two option:
 * 		EXECUTE		- execute the queue entry immediately & reschedule for next run.
 * 		SCHEDULE	- recreate queue entry to next run.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMPriceUpdateFeedbackQueueInitializer {
	
	/*
	 * FEEDBACK_LOGGER.
	 */
	public static final Logger FEEDBACK_LOGGER = Logger.getLogger("priceUpdateFeedback");
	/**
	 * Declaring Error Message.
	 */
	private static String errorMsg;
	/**
	 * Constructor.
	 */
	protected SMPriceUpdateFeedbackQueueInitializer(){
		//protected Constructor.
	}

	/**
	 * Initializes the queue.
	 * @param args - String[]
	 * @throws WTException - WTException
	 */
	public static void main(String[] args) throws WTException{
		//Initializing timestamp.
		Timestamp priceInboundRequestQueueTime;
		//Action.
		String productInboundFeedbackAction;
		if (args.length <1) {
			FEEDBACK_LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.services.SMPriceUpdateFeedbackQueueInitializer [options]\n");
			FEEDBACK_LOGGER.debug(
					"SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			FEEDBACK_LOGGER.debug(
					"EXECUTE : Synchronous Execution (Initialize execution now and process the Product Inbound Integartion Feedback data )\n");
			System.exit(0);
		}
		productInboundFeedbackAction=args[0];
		try
		{      
			//Execute Action
			if (SMInboundWebserviceConstants.EXECUTE.equalsIgnoreCase(productInboundFeedbackAction)) {
				
				// Get the current timestamp - can we not just use
				// currentTime, maybe add 500 msec to account for CPU lag?
				priceInboundRequestQueueTime = new Timestamp(System.currentTimeMillis());
			
			// If running in queue the timestamp is scheduled time
			//SCHEDULE action.
			}else if (SMInboundWebserviceConstants.SCHEDULE.equals(productInboundFeedbackAction)) {
				
				priceInboundRequestQueueTime = SMUtill.getTimestamp(
						SMPricesInboundWebServiceConstants.FEEDBACK_QUEUE_START_TIME,
						SMInboundWebserviceConstants.TIME_ZONE,
						SMPricesInboundWebServiceConstants.FEEDBACK_QUEUE_START_AM);

			// If neither then should not run
			}else {
				return;
			}
			
			SMIntegrationServiceManager smIntegrationServiceManager = new SMIntegrationServiceManager();
			
			//Setting schedule time
			//Initializing the queue and initiating the task
			smIntegrationServiceManager.scheduleJobForIntegration(
					SMPricesInboundWebServiceConstants.FEEDBACK_QUEUE_NAME, true,
					"executePriceUpdateInboundFeedbackQueueEntry",
					SMPriceUpdateFeedbackQueueInitializer.class.getName(), priceInboundRequestQueueTime);

		} catch (WTException wtExp) {
			errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
			FEEDBACK_LOGGER.error(SMInboundWebserviceConstants.QUEUE_START_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_START_ERROR_MESSAGE+SMPricesInboundWebServiceConstants.FEEDBACK_QUEUE_NAME);
			FEEDBACK_LOGGER.error(errorMsg);
		}
	}

	/**
	 * Execute Queue Entry.
	 * @return statusInfo - StatusInfo
	 */
	public static synchronized StatusInfo executePriceUpdateInboundFeedbackQueueEntry(){
		
		FEEDBACK_LOGGER.debug("#######################   PRICE UPDATE INBOUND FEEDBACK INTEGRATION STARTS  ######################");
		long priceInboundRequestQueueTimeInMilliSeconds = 0;
		SMPricesUpdateFeedbackProcessor feedbackProcessor = new SMPricesUpdateFeedbackProcessor();
		//Initialize StatusInfo variable
		StatusInfo statusInfo=null;
		try{
			//Setting interval time for queue rescheduling
			priceInboundRequestQueueTimeInMilliSeconds = System.currentTimeMillis()
					+ SMPricesInboundWebServiceConstants.FEEDBACK_QUEUE_INTERVAL_IN_MINUTES * 60 * 1000;

			//Call method to start feedback processor.
			feedbackProcessor.processFeedback();
		}finally{
			//rescheduling the queue
			try {
				FEEDBACK_LOGGER.debug("Rescheduling queue entry for next run");
				//Setting time for queue rerun
				statusInfo=SMQueueRescheduleService.rescheduleEntry(new Timestamp(priceInboundRequestQueueTimeInMilliSeconds));
				
			} catch (WTPropertyVetoException wtPerptyExp) {
				//Error while rescheduling queue
				errorMsg = wtPerptyExp.getLocalizedMessage()==null? wtPerptyExp.getMessage():wtPerptyExp.getLocalizedMessage();
				FEEDBACK_LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMPricesInboundWebServiceConstants.REQUEST_QUEUE_NAME);
				FEEDBACK_LOGGER.error(errorMsg);
			}catch (WTException wtExp) {
				errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
				FEEDBACK_LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMPricesInboundWebServiceConstants.REQUEST_QUEUE_NAME);
				FEEDBACK_LOGGER.error(errorMsg);
			}
		}
		
		feedbackProcessor.printFeedbackSummary();
		FEEDBACK_LOGGER.debug("#######################   PRICE UPDATE INBOUND FEEDBACK INTEGRATION ENDS  ######################");
		return statusInfo;
	}	

}
