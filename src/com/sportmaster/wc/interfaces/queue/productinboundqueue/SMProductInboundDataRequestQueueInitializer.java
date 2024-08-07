/**
 * 
 */
package com.sportmaster.wc.interfaces.queue.productinboundqueue;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.queue.service.SMIntegrationServiceManager;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;

/**
 * @author Carrier
 *
 */
public class SMProductInboundDataRequestQueueInitializer {

	/**
	 * 
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductInboundDataRequestQueueInitializer.class);
	/**
	 * Declaring Error Message.
	 */
	private static String errorMsg;
	/**
	 * Constructor.
	 */
	protected SMProductInboundDataRequestQueueInitializer(){
		//protected constructor.
	}
	/**
	 * Initializes the queue.
	 * @param args - String[]
	 * @throws WTException - WTException
	 */
	public static void main(String args[]) throws WTException{
		
		//initializing timestamp.
		Timestamp productInboundTimeStamp;
		//Action
		String prodAction;
		if (args.length <1) {
			LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.interfaces.queue.productinboundqueue.SMProductInboundDataRequestQueueInitializer [options]\n");
			LOGGER.debug(
					"SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			LOGGER.debug(
					"EXECUTE : Synchronous Execution (Initialize execution now and process the Product Inbound Integration data )\n");
			System.exit(0);
		}
		prodAction=args[0];
		try
		{      
			//If action is EXECUTE.
			if (SMInboundWebserviceConstants.EXECUTE.equalsIgnoreCase(prodAction)) {
				// Get the current timestamp - can we not just use
				// currentTime, maybe add 500 msec to account for CPU lag?
				productInboundTimeStamp = new Timestamp(System.currentTimeMillis());
			}
			// If running in queue the timestamp is scheduled time
			//Action is Schedule
			else if (SMInboundWebserviceConstants.SCHEDULE.equals(prodAction)) {
				productInboundTimeStamp=SMUtill.getTimestamp(SMProductInboundWebServiceConstants.PRODUCT_INBOUND_QUEUE_START_TIME, SMInboundWebserviceConstants.TIME_ZONE, SMProductInboundWebServiceConstants.PRODUCT_INBOUND_QUEUE_START_AM);
			} // If neither then should not run
			else {
				return;
			}

			SMIntegrationServiceManager smIntegrationServiceManager = new SMIntegrationServiceManager();
			//Setting schedule time
			//Initializing the queue and initiating the task
			smIntegrationServiceManager.scheduleJobForIntegration(SMProductInboundWebServiceConstants.PRODUCT_INBOUND_QUEUE_NAME, true,"executeProductInboundDataRequestQueueEntry","com.sportmaster.wc.interfaces.queue.productinboundqueue.SMProductInboundDataRequestTask",productInboundTimeStamp);
		} catch (WTException wtExp) {
			errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
			LOGGER.error(SMInboundWebserviceConstants.QUEUE_START_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_START_ERROR_MESSAGE+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_QUEUE_NAME);
			LOGGER.error(errorMsg);
			wtExp.printStackTrace();
		}
	}
}
