package com.sportmaster.wc.emailutility.queue.sepd;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.queue.service.SMNotificationQueueRescheduleService;
import com.sportmaster.wc.emailutility.queue.service.SMNotificationServiceManager;
import com.sportmaster.wc.emailutility.sepd.processor.SMSEPDProdCostsheetEmailProcessor;
import com.sportmaster.wc.helper.SMUtill;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author 'true' Priya. Entry point for Queue Execution Class
 *
 */
public class SMSEPDProdCostsheetWFEmailQueueInitializer {
	/**
	 * 
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSEPDProdCostsheetWFEmailQueueInitializer.class);
	/**
	 * Declaring Error Message.
	 */
	private static String errorMsg;

	/**
	 * Constructor.
	 */
	protected SMSEPDProdCostsheetWFEmailQueueInitializer() {
		// protected Constructor SMSEPDProdCostsheetWFEmailQueueInitializer()
	}

	/**
	 * Initializes the queue and queue will call this Main method
	 * 
	 * @param args - String[]
	 * @throws WTException - WTException
	 */
	public static void main(String args[]) throws WTException {
		LOGGER.debug("### SMSEPDProdCostsheetWFEmailQueueInitializer.main() - Start ###");
		// Initializing time stamp.
		Timestamp timeStamp;
		// Queue Action.
		String qAction;
		if (args.length < 1) {
			LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.emailutility.queue.sepd.SMSEPDProdCostsheetWFEmailQueueInitializer [options]\n");
			LOGGER.debug(
					"SCHEDULE : Asynchronous Execution ( Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			LOGGER.debug("EXECUTE : Synchronous Execution ( Initialize execution now and process the notifications)\n");
			System.exit(0);
		}
		qAction = args[0];
		try {
			// Execute Action
			if (SMEmailUtilConstants.EXECUTE.equalsIgnoreCase(qAction)) {
				// If option is Execute, Get the current time stamp and run the queue
				// immediately
				timeStamp = new Timestamp(System.currentTimeMillis());
			}
			// SCHEDULE action.
			// If option is Schedule, get the scheduled time stamp and execute the queue
			// during scheduled run time
			else if (SMEmailUtilConstants.SCHEDULE.equals(qAction)) {
				// call getTimeStamp method to get the queue run time for reschedule.
				timeStamp = SMUtill.getTimestamp(
						SMEmailUtilConstants.SEPD_PRODUCT_COSTSHEET_WF_EMAIL_SCHEDULE_QUEUE_START_TIME,
						SMEmailUtilConstants.TIME_ZONE,
						SMEmailUtilConstants.SEPD_PRODUCT_COSTSHEET_WF_EMAIL_SCHEDULE_QUEUE_START_AM);
			} // If neither then should not run
			else {
				return;
			}
			LOGGER.debug("Product Costsheet Workflow notification scheduled timestamp==" + timeStamp);
			SMNotificationServiceManager smNotificationServiceManager = new SMNotificationServiceManager();
			// Initializing the queue and initiating the task for notification
			smNotificationServiceManager.scheduleJobForNotification(
					SMEmailUtilConstants.SEPD_PRODUCT_COSTSHEET_WF_EMAIL_SCHEDULE_QUEUE_NAME, true,
					"executeSEPDProdSampleNotificationQueueEntry",
					SMSEPDProdCostsheetWFEmailQueueInitializer.class.getName(), timeStamp);
		} catch (WTException wtExp) {
			errorMsg = wtExp.getLocalizedMessage() == null ? wtExp.getMessage() : wtExp.getLocalizedMessage();
			LOGGER.error(SMEmailUtilConstants.QUEUE_START_ERROR_CODE + SMEmailUtilConstants.QUEUE_START_ERROR_MESSAGE
					+ SMEmailUtilConstants.SEPD_PRODUCT_COSTSHEET_WF_EMAIL_SCHEDULE_QUEUE_NAME);
			LOGGER.error(errorMsg);
			wtExp.printStackTrace();
		}
		LOGGER.debug("### SMSEPDProdCostsheetWFEmailQueueInitializer.main() - End ###");
	}

	/**
	 * Execute Queue Entry. This method will call processSEPDProdSampleScheduleQueue
	 * to identify the open tasks in last 24 hours for sending notification
	 * 
	 * @return statusInfo - StatusInfo
	 * @throws InterruptedException 
	 */
	public static synchronized StatusInfo executeSEPDProdSampleNotificationQueueEntry() throws InterruptedException {
		LOGGER.debug(
				"### SMSEPDProdCostsheetWFEmailQueueInitializer.executeSEPDProdCostsheetNotificationQueueEntry() - Start ###");
		Timestamp scheduleTimeStamp = null;
		// Initialize StatusInfo variable
		StatusInfo statusInfo = null;
		try {

			// Calling processSEPDProdCostsheetScheduleQueue to send email notifications for
			// opentasks in last 24 hours.
			SMSEPDProdCostsheetEmailProcessor.processSEPDProdCostsheetScheduleQueue();
			
			// Bug fix - Getting email twice on same day, as the process gets completed quickly and email queue is triggered again for the same day
			// To avoid this adding sleep time so the email notification does not occur twice for same day
			//Thread.sleep(10000);
			TimeUnit.SECONDS.sleep(10);
			// Setting interval time for queue rescheduling
			scheduleTimeStamp = SMUtill.getTimestamp(
					SMEmailUtilConstants.SEPD_PRODUCT_COSTSHEET_WF_EMAIL_SCHEDULE_QUEUE_START_TIME,
					SMEmailUtilConstants.TIME_ZONE,
					SMEmailUtilConstants.SEPD_PRODUCT_COSTSHEET_WF_EMAIL_SCHEDULE_QUEUE_START_AM);
			LOGGER.debug("Next scheduleTimeStamp =" + scheduleTimeStamp);

		} finally {
			// rescheduling the queue
			try {
				LOGGER.debug("Rescheduling queue entry for next run");

				// SMSupplierUtil.printSupplierUpdateSummaryAfterScheduleQueueRun();

				// Once process is complete, setting time for queue rerun
				statusInfo = SMNotificationQueueRescheduleService.rescheduleEntry(scheduleTimeStamp);
				LOGGER.debug(
						"#######################   SEPD product Costsheet WF NOTIFICATION SCHEDULE QUEUE ENDS  ######################");
			} catch (WTPropertyVetoException wtPerptyExp) {
				// Error while rescheduling queue
				errorMsg = wtPerptyExp.getLocalizedMessage() == null ? wtPerptyExp.getMessage()
						: wtPerptyExp.getLocalizedMessage();
				LOGGER.error(SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_CODE
						+ SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE
						+ SMEmailUtilConstants.SEPD_PRODUCT_COSTSHEET_WF_EMAIL_SCHEDULE_QUEUE_NAME);
				LOGGER.error(errorMsg);
			} catch (WTException wtExp) {
				errorMsg = wtExp.getLocalizedMessage() == null ? wtExp.getMessage() : wtExp.getLocalizedMessage();
				LOGGER.error(SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_CODE
						+ SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE
						+ SMEmailUtilConstants.SEPD_PRODUCT_COSTSHEET_WF_EMAIL_SCHEDULE_QUEUE_NAME);
				LOGGER.error(errorMsg);
			}
		}
		LOGGER.debug(
				"### SMSEPDProdCostsheetWFEmailQueueInitializer.executeSEPDProdCostsheetNotificationQueueEntry() - End ###");
		return statusInfo;
	}

}
