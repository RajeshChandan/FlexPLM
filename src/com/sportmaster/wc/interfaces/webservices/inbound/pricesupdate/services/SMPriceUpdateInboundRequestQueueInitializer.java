
package com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.services;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.queue.service.SMIntegrationServiceManager;
import com.sportmaster.wc.interfaces.queue.service.SMQueueRescheduleService;
import com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.processor.SMPricesUpdateProcessor;
import com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.util.SMPricesInboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * SMPriceUpdateInboundRequestQueueInitializer.java
 * This class used to initialize price update Request queue.
 * it has two option:
 * 		EXECUTE		- execute the queue entry immediately & reschedule for next run.
 * 		SCHEDULE	- recreate queue entry to next run.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMPriceUpdateInboundRequestQueueInitializer {

	/*
	 * REQ_LOGGER.
	 */
	public static final Logger REQ_LOGGER = Logger.getLogger("priceUpdateRequest");
	/**
	 * Declaring Error Message.
	 */
	private static String errorMsg;
	/**
	 * Constructor.
	 */
	protected SMPriceUpdateInboundRequestQueueInitializer(){
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
			REQ_LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.interfaces.webservices.inbound.pricesUpdate.services.SMPriceUpdateInboundRequestQueueInitializer [options]\n");
			REQ_LOGGER.debug(
					"SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			REQ_LOGGER.debug(
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
			}
			
			// If running in queue the timestamp is scheduled time
			//SCHEDULE action.
			else if (SMInboundWebserviceConstants.SCHEDULE.equals(productInboundFeedbackAction)) {
				
				priceInboundRequestQueueTime = SMUtill.getTimestamp(
						SMPricesInboundWebServiceConstants.REQUEST_QUEUE_START_TIME,
						SMInboundWebserviceConstants.TIME_ZONE,
						SMPricesInboundWebServiceConstants.REQUEST_QUEUE_START_AM);

			// If neither then should not run
			}else {
				return;
			}
			
			SMIntegrationServiceManager smIntegrationServiceManager = new SMIntegrationServiceManager();
			
			//Setting schedule time
			//Initializing the queue and initiating the task
			smIntegrationServiceManager.scheduleJobForIntegration(SMPricesInboundWebServiceConstants.REQUEST_QUEUE_NAME,
					true, "executePriceUpdateInboundRequestQueueEntry",
					SMPriceUpdateInboundRequestQueueInitializer.class.getName(), priceInboundRequestQueueTime);

		} catch (WTException wtExp) {
			errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
			REQ_LOGGER.error(SMInboundWebserviceConstants.QUEUE_START_ERROR_CODE
					+ SMInboundWebserviceConstants.QUEUE_START_ERROR_MESSAGE
					+ SMPricesInboundWebServiceConstants.REQUEST_QUEUE_NAME);
			REQ_LOGGER.error(errorMsg,wtExp);
		}
	}

	/**
	 * Execute Queue Entry.
	 * @return statusInfo - StatusInfo
	 */
	public static synchronized StatusInfo executePriceUpdateInboundRequestQueueEntry(){
		
		REQ_LOGGER.debug("#######################   PRICE UPDATE INBOUND INTEGRATION REQUEST INTEGRATION STARTS  ######################");
		long priceInboundRequestQueueTimeInMilliSeconds = 0;
		SMPricesUpdateProcessor processor = new SMPricesUpdateProcessor();
		//Initialize StatusInfo variable
		StatusInfo statusInfo=null;
		try{
			//Setting interval time for queue rescheduling
			priceInboundRequestQueueTimeInMilliSeconds = System.currentTimeMillis()
					+ SMPricesInboundWebServiceConstants.REQUEST_QUEUE_INTERVAL_IN_MINUTES * 60 * 1000;

			
			try {
				
				//Call method to start feedback processor.
				processor.processPricesUpdateRequest();
				
			} catch (WTException e) {
				REQ_LOGGER.error("ERROR FOUND:-",e);
			}
		}finally{
			//rescheduling the queue
			try {
				REQ_LOGGER.debug("Rescheduling queue entry for next run");
				
				//Setting time for queue rerun
				statusInfo=SMQueueRescheduleService.rescheduleEntry(new Timestamp(priceInboundRequestQueueTimeInMilliSeconds));
				
			} catch (WTPropertyVetoException wtPerptyExp) {
				//Error while rescheduling queue
				errorMsg = wtPerptyExp.getLocalizedMessage()==null? wtPerptyExp.getMessage():wtPerptyExp.getLocalizedMessage();
				REQ_LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMPricesInboundWebServiceConstants.REQUEST_QUEUE_NAME);
				REQ_LOGGER.error(errorMsg);
			}catch (WTException wtExp) {
				errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
				REQ_LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMPricesInboundWebServiceConstants.REQUEST_QUEUE_NAME);
				REQ_LOGGER.error(errorMsg,wtExp);
			}
		}
		
		processor.printSummary();
		REQ_LOGGER.debug("#######################   PRICE UPDATE INBOUND INTEGRATION REQUEST INTEGRATION ENDS  ######################");
		return statusInfo;
	}	
}
