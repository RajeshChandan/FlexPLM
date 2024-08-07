/**
 * 
 */
package com.sportmaster.wc.interfaces.queue.productinboundqueue;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.queue.service.SMIntegrationServiceManager;
import com.sportmaster.wc.interfaces.queue.service.SMQueueRescheduleService;
import com.sportmaster.wc.interfaces.webservices.inbound.product.feedback.client.SMProductInboundFeedbackDataRequestClient;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;

/**
 * @author Carrier
 *
 */
public class SMProductInboundFeedbackQueueInitializer {

	/**
	 * 
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductInboundFeedbackQueueInitializer.class);
	/**
	 * Declaring Error Message.
	 */
	private static String errorMsg;
	/**
	 * Constructor.
	 */
	protected SMProductInboundFeedbackQueueInitializer(){
		//protected Constructor.
	}

	/**
	 * Initializes the queue.
	 * @param args - String[]
	 * @throws WTException - WTException
	 */
	public static void main(String args[]) throws WTException{
		
		//Initializing timestamp.
		Timestamp productInboundFeedbackQueueTime;
		//Action.
		String productInboundFeedbackAction;
		if (args.length <1) {
			LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.interfaces.queue.productinboundqueue.SMProductInboundFeedbackQueueInitializer [options]\n");
			LOGGER.debug(
					"SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			LOGGER.debug(
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
				productInboundFeedbackQueueTime = new Timestamp(System.currentTimeMillis());
			}
			// If running in queue the timestamp is scheduled time
			//SCHEDULE action.
			else if (SMInboundWebserviceConstants.SCHEDULE.equals(productInboundFeedbackAction)) {
				productInboundFeedbackQueueTime=SMUtill.getTimestamp(SMProductInboundWebServiceConstants.PRODUCT_INBOUND_FEEDBACK_QUEUE_NAME, SMInboundWebserviceConstants.TIME_ZONE, SMProductInboundWebServiceConstants.PRODUCT_INBOUND_FEEDBACK_QUEUE_START_AM);
			} // If neither then should not run
			else {
				return;
			}
			SMIntegrationServiceManager smIntegrationServiceManager = new SMIntegrationServiceManager();
			//Setting schedule time
			//Timestamp timeStamp=new Timestamp(System.currentTimeMillis());
			//Timestamp timeStamp=SMUtill.getTimestamp(SMOutboundWebServiceConstants.SUPPLIER_SCHEDULE_QUEUE__START_AM, SMInboundWebserviceConstants.TIME_ZONE, SMOutboundWebServiceConstants.SUPPLIER_SCHEDULE_QUEUE__START_AM);
			//Initializing the queue and initiating the task
			smIntegrationServiceManager.scheduleJobForIntegration(SMProductInboundWebServiceConstants.PRODUCT_INBOUND_FEEDBACK_QUEUE_NAME, true,"executeProductInboundFeedbackQueueEntry",SMProductInboundFeedbackQueueInitializer.class.getName(),productInboundFeedbackQueueTime);
		} catch (WTException wtExp) {
			errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
			LOGGER.error(SMInboundWebserviceConstants.QUEUE_START_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_START_ERROR_MESSAGE+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_FEEDBACK_QUEUE_NAME);
			LOGGER.error(errorMsg);
		}
	}

	/**
	 * Execute Queue Entry.
	 * @return statusInfo - StatusInfo
	 */
	public static synchronized StatusInfo executeProductInboundFeedbackQueueEntry(){
		long productInboundFeedbackQueueTimeInMilliSeconds = 0;
		//Initialize StatusInfo variable
		StatusInfo statusInfo=null;
		try{
			//Setting interval time for queue rescheduling
			productInboundFeedbackQueueTimeInMilliSeconds = System.currentTimeMillis()+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_FEEDBACK_QUEUE_INTERVAL_IN_MINUTES*60*1000;
			//Calling Sub Division tree processor
			//SMSupplierFeedbackProcessing.processSupplierScheduleQueue();
			
			//Call method to start feedback processor.
			SMProductInboundFeedbackDataRequestClient.processProductInboundFeedbackData();
			
		}finally{
			//rescheduling the queue
			try {
				LOGGER.debug("Rescheduling queue entry for next run");
				//Setting time for queue rerun
				statusInfo=SMQueueRescheduleService.rescheduleEntry(new Timestamp(productInboundFeedbackQueueTimeInMilliSeconds));
				//SMSupplierUtil.printSupplierUpdateSummaryAfterScheduleQueueRun();
				
				//call method to print summary after Product Inbound Feedback queue is run.
				
				LOGGER.debug("#######################   PRODUCT INBOUND INTEGRATION FEEDBACK INTEGRATION ENDS  ######################");
			} catch (WTPropertyVetoException wtPerptyExp) {
				//Error while rescheduling queue
				errorMsg = wtPerptyExp.getLocalizedMessage()==null? wtPerptyExp.getMessage():wtPerptyExp.getLocalizedMessage();
				LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_FEEDBACK_QUEUE_NAME);
				LOGGER.error(errorMsg);
			}catch (WTException wtExp) {
				errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
				LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_FEEDBACK_QUEUE_NAME);
				LOGGER.error(errorMsg);
			}
		}
		return statusInfo;
	}	
}
