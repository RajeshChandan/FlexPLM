/**
 * 
 */
package com.sportmaster.wc.interfaces.queue.productoutboundqueue;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.queue.service.SMIntegrationServiceManager;
import com.sportmaster.wc.interfaces.queue.service.SMQueueRescheduleService;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.product.feedback.processor.SMProductOutboundFeedbackProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;

/**
 * @author Carrier
 *
 */
public class SMProductOutboundDataRequestQueueInitializer {
	/**
	 * 
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductOutboundDataRequestQueueInitializer.class);
	/**
	 * Declaring Error Message.
	 */
	private static String errorMsg;
	/**
	 * Constructor.
	 */
	protected SMProductOutboundDataRequestQueueInitializer(){
		//protected Constructor
	}

	/**
	 * Initializes the queue.
	 * @param args - String[]
	 * @throws WTException - WTException
	 */
	public static void main(String args[]) throws WTException{
		
		//Initializing timestamp.
		Timestamp prodTimeStamp;
		//Action.
		String prodAction;
		if (args.length <1) {
			LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.interfaces.queue.productoutboundqueue.SMProductOutboundDataRequestQueueInitializer.SMProductOutboundDataRequestQueueInitializer() [options]\n");
			LOGGER.debug(
					"SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			LOGGER.debug(
					"EXECUTE : Synchronous Execution (Initialize execution now and process the Product Outbound data )\n");
			System.exit(0);
		}
		prodAction=args[0];
		try
		{      
			//Execute Action
			if (SMInboundWebserviceConstants.EXECUTE.equalsIgnoreCase(prodAction)) {
				// Get the current timestamp - can we not just use
				// currentTime, maybe add 500 msec to account for CPU lag?
				prodTimeStamp = new Timestamp(System.currentTimeMillis());
			}
			// If running in queue the timestamp is scheduled time
			//SCHEDULE action.
			else if (SMInboundWebserviceConstants.SCHEDULE.equals(prodAction)) {
				prodTimeStamp=SMUtill.getTimestamp(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_OUTBOUND_QUEUE_START_TIME, SMProductOutboundWebServiceConstants.PRODUCT_SEASON_OUTBOUND_QUEUE_TIME_ZONE, SMProductOutboundWebServiceConstants.PRODUCT_SEASON_OUTBOUND_QUEUE_START_TIME);
			} // If neither then should not run
			else {
				return;
			}
			SMIntegrationServiceManager smIntegrationServiceManager = new SMIntegrationServiceManager();
			//Setting schedule time
			//Timestamp timeStamp=new Timestamp(System.currentTimeMillis());
			//Timestamp timeStamp=SMUtill.getTimestamp(SMOutboundWebServiceConstants.SUPPLIER_SCHEDULE_QUEUE__START_AM, SMInboundWebserviceConstants.TIME_ZONE, SMOutboundWebServiceConstants.SUPPLIER_SCHEDULE_QUEUE__START_AM);
			//Initializing the queue and initiating the task
			smIntegrationServiceManager.scheduleJobForIntegration(SMProductOutboundWebServiceConstants.PRODUCT_OUTBOUND_INTEGRATION_SCHEDULE_QUEUE, true,"executeProductQueueEntry",SMProductOutboundDataRequestQueueInitializer.class.getName(),prodTimeStamp);
		} catch (WTException wtExp) {
			errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
			LOGGER.error(SMInboundWebserviceConstants.QUEUE_START_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_START_ERROR_MESSAGE+SMProductOutboundWebServiceConstants.PRODUCT_OUTBOUND_INTEGRATION_SCHEDULE_QUEUE);
			LOGGER.error(errorMsg);
		}
	}

	/**
	 * Execute Queue Entry.
	 * @return statusInfo - StatusInfo
	 */
	public static synchronized StatusInfo executeProductQueueEntry(){
		long productScheduleQueueTimeInMilliSeconds = 0;
		//Initialize StatusInfo variable
		StatusInfo statusInfo=null;
		try{
			//Setting interval time for queue rescheduling
			productScheduleQueueTimeInMilliSeconds = System.currentTimeMillis()+SMProductOutboundWebServiceConstants.PRODUCT_SEASON_OUTBOUND_QUEUE_INTERVAL_IN_MINUTES*60*1000;
			
			//Calling Sub Division tree processor
			new SMProductOutboundFeedbackProcessor().processProductSeasonOutboundIntegrationScheduleQueue();
			
		}finally{
			//rescheduling the queue
			try {
				LOGGER.debug("Rescheduling queue entry for next run");
				//re-calculating time again if, previous time is already passed current time.
				if(System.currentTimeMillis() > productScheduleQueueTimeInMilliSeconds) {
					productScheduleQueueTimeInMilliSeconds = System.currentTimeMillis()+SMProductOutboundWebServiceConstants.PRODUCT_SEASON_OUTBOUND_QUEUE_INTERVAL_IN_MINUTES*60*1000;
				}
				
				//Setting time for queue rerun
				statusInfo=SMQueueRescheduleService.rescheduleEntry(new Timestamp(productScheduleQueueTimeInMilliSeconds));
				//SMSupplierUtil.printSupplierUpdateSummaryAfterScheduleQueueRun();
				//call product outbound summary here.
				LOGGER.debug("#######################   PRODUCT OUTBOUND SCHEDULE QUEUE INTEGRATION ENDS  ######################");
			} catch (WTPropertyVetoException wtPerptyExp) {
				//Error while rescheduling queue
				errorMsg = wtPerptyExp.getLocalizedMessage()==null? wtPerptyExp.getMessage():wtPerptyExp.getLocalizedMessage();
				LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMProductOutboundWebServiceConstants.PRODUCT_OUTBOUND_INTEGRATION_SCHEDULE_QUEUE);
				LOGGER.error(errorMsg);
			}catch (WTException wtExp) {
				errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
				LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMProductOutboundWebServiceConstants.PRODUCT_OUTBOUND_INTEGRATION_SCHEDULE_QUEUE);
				LOGGER.error(errorMsg);
			}
		}
		return statusInfo;
	}
}
