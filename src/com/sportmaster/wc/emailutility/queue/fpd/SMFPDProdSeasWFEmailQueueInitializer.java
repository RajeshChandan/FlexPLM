package com.sportmaster.wc.emailutility.queue.fpd;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.fpd.processor.SMFPDProdSeasEmailProcessor;
import com.sportmaster.wc.emailutility.queue.service.SMNotificationQueueRescheduleService;
import com.sportmaster.wc.emailutility.queue.service.SMNotificationServiceManager;
import com.sportmaster.wc.helper.SMUtill;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author 'true' Rajesh Chandan. Entry point for Queue Execution Class
 *
 */
public class SMFPDProdSeasWFEmailQueueInitializer {
	/**
	 *
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER_FPD = Logger.getLogger(SMFPDProdSeasWFEmailQueueInitializer.class);
	/**
	 * Declaring Error Message.
	 */
	private static String errorMsg;

	/**
	 * Constructor.
	 */
	protected SMFPDProdSeasWFEmailQueueInitializer() {
		// protected Constructor
	}

	public static void main(String[] args) {

		LOGGER_FPD.debug("### SMFPDProdSeasWFEmailQueueInitializer.main() - Start ###");
		// Initializing time stamp.
		Timestamp scheduleTimeStampFPD;
		// Action.
		String fpdQueueAction;
		if (args.length < 1) {
			LOGGER_FPD.debug(
					"Usage: windchill com.sportmaster.wc.emailutility.queue.sepd.SMFPDProdSeasWFEmailQueueInitializer [options]\n");
			LOGGER_FPD.debug("SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			LOGGER_FPD.debug("EXECUTE : Synchronous Execution (Initialize execution now and process the notifications)\n");
			System.exit(0);
		}
		fpdQueueAction = args[0];
		try {
			// Execute Action
			if (SMEmailUtilConstants.EXECUTE.equalsIgnoreCase(fpdQueueAction)) {
				// If option is Execute, Get the current timestamp and run the
				// queue immediately
				scheduleTimeStampFPD = new Timestamp(System.currentTimeMillis());
			}
			// FPD SCHEDULE action.
			// If option is Schedule, get the scheduled time stamp and execute
			// the queue
			// during scheduled run time
			else if (SMEmailUtilConstants.SCHEDULE.equals(fpdQueueAction)) {
				// call getTimeStamp method to get the queue run time for
				// reschedule.
				scheduleTimeStampFPD = SMUtill.getTimestamp(SMEmailUtilConstants.FPD_PS_WF_EMAIL_SCHEDULE_QUEUE_START_TIME,
						SMEmailUtilConstants.TIME_ZONE, SMEmailUtilConstants.FPD_PS_WF_EMAIL_SCHEDULE_QUEUE_START_AM);
			} // If neither then should not run
			else {
				return;
			}
			LOGGER_FPD.info("Product Season Workflow notification scheduled timestamp==" + scheduleTimeStampFPD);
			SMNotificationServiceManager smNotificationServiceManager = new SMNotificationServiceManager();
			// Initializing the queue and initiating the task for notification
			smNotificationServiceManager.scheduleJobForNotification(SMEmailUtilConstants.FPD_PS_WF_EMAIL_SCHEDULE_QUEUE_NAME, true,
					"executeFPDProdSeasNotificationQueueEntry", SMFPDProdSeasWFEmailQueueInitializer.class.getName(),
					scheduleTimeStampFPD);

		} catch (WTException wtExp) {
			errorMsg = wtExp.getLocalizedMessage() == null ? wtExp.getMessage() : wtExp.getLocalizedMessage();
			LOGGER_FPD.error(SMEmailUtilConstants.QUEUE_START_ERROR_CODE + SMEmailUtilConstants.QUEUE_START_ERROR_MESSAGE
					+ SMEmailUtilConstants.FPD_PS_WF_EMAIL_SCHEDULE_QUEUE_NAME);
			LOGGER_FPD.error(errorMsg);
			wtExp.printStackTrace();
		}
		LOGGER_FPD.debug("### SMFPDProdSeasWFEmailQueueInitializer.main() - End ###");

	}

	/**
	 * Execute Queue Entry. This method will call processFPDProdSeasScheduleQueue to identify the open tasks in last 24
	 * hours for sending notification
	 *
	 * @return statusInfo - StatusInfo
	 */
	public static synchronized StatusInfo executeFPDProdSeasNotificationQueueEntry() {
		LOGGER_FPD.debug("### SMFPDProdSeasWFEmailQueueInitializer.executeFPDProdSeasNotificationQueueEntry() - Start ###");
		Timestamp scheduleTimeStamp = null;
		// Initialize StatusInfo variable
		StatusInfo statusInfo = null;
		try {

			// Calling processFPDProdSeasScheduleQueue to send email
			// notifications for open tasks in last 24 hours.
			new SMFPDProdSeasEmailProcessor().processFPDProdSeasScheduleQueue();

			// Setting interval time for queue rescheduling
			scheduleTimeStamp = SMUtill.getTimestamp(SMEmailUtilConstants.FPD_PS_WF_EMAIL_SCHEDULE_QUEUE_START_TIME,
					SMEmailUtilConstants.TIME_ZONE, SMEmailUtilConstants.FPD_PS_WF_EMAIL_SCHEDULE_QUEUE_START_AM);
			LOGGER_FPD.debug("Next scheduleTimeStamp =" + scheduleTimeStamp);

		} finally {
			// rescheduling the queue
			try {
				LOGGER_FPD.debug("Rescheduling queue entry for next run");

				// Once process is complete, setting time for queue rerun
				statusInfo = SMNotificationQueueRescheduleService.rescheduleEntry(scheduleTimeStamp);
				LOGGER_FPD.debug(
						"#######################   FPD PRODUCT SEASON WF NOTIFICATION SCHEDULE QUEUE ENDS  ######################");
			} catch (WTPropertyVetoException wtPerptyExp) {
				// Error while rescheduling queue
				errorMsg = wtPerptyExp.getLocalizedMessage() == null ? wtPerptyExp.getMessage() : wtPerptyExp.getLocalizedMessage();
				LOGGER_FPD.error(SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_CODE + SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE
						+ SMEmailUtilConstants.FPD_PS_WF_EMAIL_SCHEDULE_QUEUE_NAME);
				LOGGER_FPD.error(errorMsg);
			} catch (WTException wtExp) {
				errorMsg = wtExp.getLocalizedMessage() == null ? wtExp.getMessage() : wtExp.getLocalizedMessage();
				LOGGER_FPD.error(SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_CODE + SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE
						+ SMEmailUtilConstants.FPD_PS_WF_EMAIL_SCHEDULE_QUEUE_NAME);
				LOGGER_FPD.error(errorMsg);
			}
		}
		LOGGER_FPD.debug("### SMFPDProdSeasWFEmailQueueInitializer.executeFPDProdSeasNotificationQueueEntry() - End ###");
		return statusInfo;
	}

}
