/**
 * 
 */
package com.sportmaster.wc.interfaces.queue.productinboundqueue;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.sportmaster.wc.interfaces.queue.service.SMQueueRescheduleService;
import com.sportmaster.wc.interfaces.webservices.inbound.product.client.SMProductInboundDataRequestWebClient;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;

/**
 * @author Carrier
 *
 */
public class SMProductInboundDataRequestTask {
	
	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductInboundDataRequestTask.class);
	/**
	 * Declaration for Error Message.
	 */
	private static String errorMsg;
	/**
	 * Constructor.
	 */
	protected SMProductInboundDataRequestTask() {
		//protected Constructor
	}
	
	/**
	 * Executes the request.
	 * @return statusInfo - StatusInfo
	 */
	public static synchronized StatusInfo executeProductInboundDataRequestQueueEntry() {
		long productInboundIntegrationIntervalTimeinMilliSeconds = 0;
		//Initialize StatusInfo variable
		StatusInfo statusInfo=null;
		try{
			//Setting interval time for queue rescheduling
			productInboundIntegrationIntervalTimeinMilliSeconds = System.currentTimeMillis()+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_QUEUE_INTERVAL_IN_MINUTES*60*1000;
			
			//Call Product Inbound Integration Client.
			SMProductInboundDataRequestWebClient.productInboundRequest();
		}finally{
			//rescheduling the queue
			try {
				LOGGER.debug("Rescheduling queue entry for next run");
				//Setting time for queue rerun
				statusInfo=SMQueueRescheduleService.rescheduleEntry(new Timestamp(productInboundIntegrationIntervalTimeinMilliSeconds));
				LOGGER.debug("#######################   PRODUCT SEASON LINK INBOUND INTEGRATION ENDS  ######################");
			} catch (WTPropertyVetoException wtPerptyExp) {
				//Error while rescheduling queue
				errorMsg = wtPerptyExp.getLocalizedMessage()==null? wtPerptyExp.getMessage():wtPerptyExp.getLocalizedMessage();
				LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_QUEUE_NAME);
				LOGGER.error(errorMsg);
			}catch (WTException wtExp) {
				errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
				LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMProductInboundWebServiceConstants.PRODUCT_INBOUND_QUEUE_NAME);
				LOGGER.error(errorMsg);
			}
		}
		return statusInfo;
	}
}
