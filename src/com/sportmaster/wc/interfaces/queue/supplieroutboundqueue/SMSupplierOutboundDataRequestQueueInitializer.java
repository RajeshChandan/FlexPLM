/**
 * 
 */
package com.sportmaster.wc.interfaces.queue.supplieroutboundqueue;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.queue.service.SMIntegrationServiceManager;
import com.sportmaster.wc.interfaces.queue.service.SMQueueRescheduleService;
import com.sportmaster.wc.interfaces.webservices.bean.SQLException_Exception;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor.SMSupplierFeedbackProcessing;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.util.SMSupplierUtil;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;


/**
 * @author 'true' ITC_Infotech.
 *
 */
public class SMSupplierOutboundDataRequestQueueInitializer {
	/**
	 * 
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSupplierOutboundDataRequestQueueInitializer.class);
	/**
	 * Declaring Error Message.
	 */
	private static String errorMsg;
	/**
	 * Constructor.
	 */
	protected SMSupplierOutboundDataRequestQueueInitializer(){
		//protected Constructor
	}

	/**
	 * Initializes the queue.
	 * @param args - String[]
	 * @throws WTException - WTException
	 */
	public static void main(String args[]) throws WTException{
		
		//Initializing timestamp.
		Timestamp supplierTimeStamp;
		//Action.
		String supplierAction;
		if (args.length <1) {
			LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.interfaces.queue.supplieroutboundqueue.SMSupplierOutboundDataRequestQueueInitializer.SMSupplierOutboundDataRequestQueueInitializer() [options]\n");
			LOGGER.debug(
					"SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			LOGGER.debug(
					"EXECUTE : Synchronous Execution (Initialize execution now and process the Supplier data )\n");
			System.exit(0);
		}
		supplierAction=args[0];
		try
		{      
			//Execute Action
			if (SMInboundWebserviceConstants.EXECUTE.equalsIgnoreCase(supplierAction)) {
				// Get the current timestamp - can we not just use
				// currentTime, maybe add 500 msec to account for CPU lag?
				supplierTimeStamp = new Timestamp(System.currentTimeMillis());
			}
			// If running in queue the timestamp is scheduled time
			//SCHEDULE action.
			else if (SMInboundWebserviceConstants.SCHEDULE.equals(supplierAction)) {
				supplierTimeStamp=SMUtill.getTimestamp(SMOutboundWebServiceConstants.SUPLIER_SCHEDULE_QUEUE_START_TIME, SMInboundWebserviceConstants.TIME_ZONE, SMOutboundWebServiceConstants.SUPPLIER_SCHEDULE_QUEUE_START_AM);
			} // If neither then should not run
			else {
				return;
			}
			SMIntegrationServiceManager smIntegrationServiceManager = new SMIntegrationServiceManager();
			//Setting schedule time
			//Timestamp timeStamp=new Timestamp(System.currentTimeMillis());
			//Timestamp timeStamp=SMUtill.getTimestamp(SMOutboundWebServiceConstants.SUPPLIER_SCHEDULE_QUEUE__START_AM, SMInboundWebserviceConstants.TIME_ZONE, SMOutboundWebServiceConstants.SUPPLIER_SCHEDULE_QUEUE__START_AM);
			//Initializing the queue and initiating the task
			smIntegrationServiceManager.scheduleJobForIntegration(SMOutboundWebServiceConstants.SUPPLIER_OUTBOUND_SCHEDULE_QUEUE_NAME, true,"executeSupplierQueueEntry",SMSupplierOutboundDataRequestQueueInitializer.class.getName(),supplierTimeStamp);
		} catch (WTException wtExp) {
			errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
			LOGGER.error(SMInboundWebserviceConstants.QUEUE_START_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_START_ERROR_MESSAGE+SMOutboundWebServiceConstants.SUPPLIER_OUTBOUND_SCHEDULE_QUEUE_NAME);
			LOGGER.error(errorMsg);
		}
	}

	/**
	 * Execute Queue Entry.
	 * @return statusInfo - StatusInfo
	 */
	public static synchronized StatusInfo executeSupplierQueueEntry(){
		long supplierScheduleQueueTimeInMilliSeconds = 0;
		//Initialize StatusInfo variable
		StatusInfo statusInfo=null;
		try{
			//Setting interval time for queue rescheduling
			supplierScheduleQueueTimeInMilliSeconds = System.currentTimeMillis()+SMOutboundWebServiceConstants.SUPPLIER_SCHEDULE_QUEUE_INTERVAL_IN_MINS*60*1000;
			//Calling Sub Division tree processor
			SMSupplierFeedbackProcessing.processSupplierScheduleQueue();
		} catch (SQLException_Exception sqlExcpt) {
			LOGGER.error(sqlExcpt.getLocalizedMessage());
			sqlExcpt.printStackTrace();
		}finally{
			//rescheduling the queue
			try {
				LOGGER.debug("Rescheduling queue entry for next run");
				//Setting time for queue rerun
				statusInfo=SMQueueRescheduleService.rescheduleEntry(new Timestamp(supplierScheduleQueueTimeInMilliSeconds));
				SMSupplierUtil.printSupplierUpdateSummaryAfterScheduleQueueRun();
				LOGGER.debug("#######################   SUPPLIER SCHEDULE QUEUE INTEGRATION ENDS  ######################");
			} catch (WTPropertyVetoException wtPerptyExp) {
				//Error while rescheduling queue
				errorMsg = wtPerptyExp.getLocalizedMessage()==null? wtPerptyExp.getMessage():wtPerptyExp.getLocalizedMessage();
				LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMOutboundWebServiceConstants.SUPPLIER_OUTBOUND_SCHEDULE_QUEUE_NAME);
				LOGGER.error(errorMsg);
			}catch (WTException wtExp) {
				errorMsg = wtExp.getLocalizedMessage()==null? wtExp.getMessage():wtExp.getLocalizedMessage();
				LOGGER.error(SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_CODE+SMInboundWebserviceConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE+SMOutboundWebServiceConstants.SUPPLIER_OUTBOUND_SCHEDULE_QUEUE_NAME);
				LOGGER.error(errorMsg);
			}
		}
		return statusInfo;
	}

}
