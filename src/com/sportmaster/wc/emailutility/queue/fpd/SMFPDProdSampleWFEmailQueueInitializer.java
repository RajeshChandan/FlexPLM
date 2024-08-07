package com.sportmaster.wc.emailutility.queue.fpd;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.fpd.processor.SMFPDProdSampleEmailProcessor;
import com.sportmaster.wc.emailutility.queue.service.SMNotificationQueueRescheduleService;
import com.sportmaster.wc.emailutility.queue.service.SMNotificationServiceManager;
import com.sportmaster.wc.helper.SMUtill;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author 'true' Priya. Entry point for Queue Execution Class
 *
 */
public class SMFPDProdSampleWFEmailQueueInitializer {
	/**
	 * 
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMFPDProdSampleWFEmailQueueInitializer.class);
	/**
	 * Declaring Error Message.
	 */
	private static String errorMsg;

	/**
	 * Constructor.
	 */
	protected SMFPDProdSampleWFEmailQueueInitializer() {
		// protected Constructor SMFPDProdSampleWFEmailQueueInitializer()
	}

	/**
	 * Initializes the queue. queue will call this Main method
	 * 
	 * @param args - String[]
	 * @throws WTException - WTException
	 */
	public static void main(String args[]) throws WTException {
		LOGGER.debug("### SMFPDProdSampleWFEmailQueueInitializer.main() - Start ###");
		// Initializing time stamp.
		Timestamp timeStampSchedule;
		// Queue Action.
		String queueAction;
		if (args.length < 1) {
			LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.emailutility.queue.fpd.SMFPDProdSampleWFEmailQueueInitializer [options]\n");
			LOGGER.debug(
					"SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			LOGGER.debug("EXECUTE : Synchronous Execution (Initialize execution now and process the notifications)\n");
			System.exit(0);
		}
		queueAction = args[0];
		try {
			// Action - Execute.
			if (SMEmailUtilConstants.EXECUTE.equalsIgnoreCase(queueAction)) {
				// If option is Execute, Get the current timestamp and run the queue immediately
				timeStampSchedule = new Timestamp(System.currentTimeMillis());
			}
			// Action - SCHEDULE.
			// If option is Schedule, get the scheduled time stamp and execute the queue
			// during scheduled run time
			else if (SMEmailUtilConstants.SCHEDULE.equals(queueAction)) {
				// call getTimeStamp method to get the queue run time for reschedule.
				timeStampSchedule = SMUtill.getTimestamp(
						SMEmailUtilConstants.FPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_QUEUE_START_TIME,
						SMEmailUtilConstants.TIME_ZONE,
						SMEmailUtilConstants.FPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_QUEUE_START_AM);
			} // If neither then should not run
			else {
				return;
			}
			LOGGER.info("Product Sample Workflow notification scheduled timestamp==" + timeStampSchedule);
			SMNotificationServiceManager smNotificationServiceManager = new SMNotificationServiceManager();
			// Initializing the queue and initiating the task for notification
			smNotificationServiceManager.scheduleJobForNotification(
					SMEmailUtilConstants.FPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_QUEUE_NAME, true,
					"executeFPDProdSampleNotificationQueueEntry",
					SMFPDProdSampleWFEmailQueueInitializer.class.getName(), timeStampSchedule);
		} catch (WTException wtExp) {
			errorMsg = wtExp.getLocalizedMessage() == null ? wtExp.getMessage() : wtExp.getLocalizedMessage();
			LOGGER.error(SMEmailUtilConstants.QUEUE_START_ERROR_CODE + SMEmailUtilConstants.QUEUE_START_ERROR_MESSAGE
					+ SMEmailUtilConstants.FPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_QUEUE_NAME);
			LOGGER.error(errorMsg);
			wtExp.printStackTrace();
		}
		LOGGER.debug("### SMFPDProdSampleWFEmailQueueInitializer.main() - End ###");
	}

	/**
	 * Execute Queue Entry. This method will call processFPDProdSampleScheduleQueue
	 * to identify the open tasks in last 24 hours for sending notification
	 * 
	 * @return statusInfo - StatusInfo
	 */
	public static synchronized StatusInfo executeFPDProdSampleNotificationQueueEntry() {
		LOGGER.debug(
				"### SMFPDProdSampleWFEmailQueueInitializer.executeFPDProdSampleNotificationQueueEntry() - Start ###");
		Timestamp timeStampSchedule = null;
		// Initialize StatusInfo variable
		StatusInfo statusInfo = null;
		try {

			// Calling processFPDProdSampleScheduleQueue to send email notifications for
			// opentasks in last 24 hours.
			SMFPDProdSampleEmailProcessor.processFPDProdSampleScheduleQueue();

			// Setting interval time for queue rescheduling
			timeStampSchedule = SMUtill.getTimestamp(
					SMEmailUtilConstants.FPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_QUEUE_START_TIME,
					SMEmailUtilConstants.TIME_ZONE,
					SMEmailUtilConstants.FPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_QUEUE_START_AM);
			LOGGER.debug("Next timeStampSchedule =" + timeStampSchedule);

		} finally {
			// rescheduling the queue
			try {
				LOGGER.debug("Rescheduling queue entry for next run");

				// Once process is complete, setting time for queue rerun
				statusInfo = SMNotificationQueueRescheduleService.rescheduleEntry(timeStampSchedule);
				LOGGER.debug(
						"#######################   FPD PRODUCT SAMPLE WF NOTIFICATION SCHEDULE QUEUE ENDS  ######################");
			} catch (WTPropertyVetoException wtPerptyExp) {
				// Error while rescheduling queue
				errorMsg = wtPerptyExp.getLocalizedMessage() == null ? wtPerptyExp.getMessage()
						: wtPerptyExp.getLocalizedMessage();
				LOGGER.error(SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_CODE
						+ SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE
						+ SMEmailUtilConstants.FPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_QUEUE_NAME);
				LOGGER.error(errorMsg);
			} catch (WTException wtExp) {
				errorMsg = wtExp.getLocalizedMessage() == null ? wtExp.getMessage() : wtExp.getLocalizedMessage();
				LOGGER.error(SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_CODE
						+ SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE
						+ SMEmailUtilConstants.FPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_QUEUE_NAME);
				LOGGER.error(errorMsg);
			}
		}
		LOGGER.debug(
				"### SMFPDProdSampleWFEmailQueueInitializer.executeFPDProdSampleNotificationQueueEntry() - End ###");
		return statusInfo;
	}

}