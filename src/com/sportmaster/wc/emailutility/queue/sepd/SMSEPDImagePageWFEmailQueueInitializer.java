package com.sportmaster.wc.emailutility.queue.sepd;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.queue.service.SMNotificationQueueRescheduleService;
import com.sportmaster.wc.emailutility.queue.service.SMNotificationServiceManager;
import com.sportmaster.wc.emailutility.sepd.processor.SMSEPDImagePageEmailProcessor;
import com.sportmaster.wc.helper.SMUtill;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author 'true' Narasimha Bandla.
 *
 */
public class SMSEPDImagePageWFEmailQueueInitializer {
	/**
	 * 
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSEPDImagePageWFEmailQueueInitializer.class);
	/**
	 * Declaring Error Message.
	 */
	private static String errorMsg;

	/**
	 * Constructor.
	 */
	protected SMSEPDImagePageWFEmailQueueInitializer() {
		// protected Constructor
	}

	/**
	 * Initializes the queue. queue will call this Main method
	 * 
	 * @param args - String[]
	 * @throws WTException - WTException
	 */
	public static void main(String args[]) throws WTException {
		LOGGER.debug("### SMSEPDImagePageWFEmailQueueInitializer.main() - Start ###");
		// Action.
		String queueAction;
		// Initializing time stamp.
		Timestamp scheduleTimeStamp;
		
		if (args.length < 1) {
			LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.emailutility.queue.sepd.SMSEPDImagePageWFEmailQueueInitializer [options]\n");
			LOGGER.debug(
					"SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			LOGGER.debug("EXECUTE : Synchronous Execution (Initialize execution now and process the notifications)\n");
			System.exit(0);
		}
		queueAction = args[0];
		try {
			// SCHEDULE action.
			// If option is Schedule, get the scheduled time stamp and execute the queue
			// during scheduled run time
			if (SMEmailUtilConstants.SCHEDULE.equals(queueAction)) {
				// call getTimeStamp method to get the queue run time for reschedule.
				scheduleTimeStamp = SMUtill.getTimestamp(
						SMEmailUtilConstants.SEPD_IMAGEPAGE_WF_EMAIL_SCHEDULE_QUEUE_START_TIME, SMEmailUtilConstants.TIME_ZONE,
						SMEmailUtilConstants.SEPD_IMAGEPAGE_WF_EMAIL_SCHEDULE_QUEUE_START_AM_OR_PM);
			}
			// Execute Action
			else if (SMEmailUtilConstants.EXECUTE.equalsIgnoreCase(queueAction)) {
				// If option is Execute, Get the current timestamp and run the queue immediately
				scheduleTimeStamp = new Timestamp(System.currentTimeMillis());
			} // If neither then should not run
			else {
				return;
			}
			LOGGER.info("Image Page Workflow notification scheduled timestamp==" + scheduleTimeStamp);
			SMNotificationServiceManager smNotificationServiceManager = new SMNotificationServiceManager();
			// Initializing the queue and initiating the task for notification
			smNotificationServiceManager.scheduleJobForNotification(
					SMEmailUtilConstants.SEPD_IMAGEPAGE_WF_EMAIL_SCHEDULE_QUEUE_NAME, true,
					"executeImagePageNotificationQueueEntry", SMSEPDImagePageWFEmailQueueInitializer.class.getName(),
					scheduleTimeStamp);
		} catch (WTException wtExp) {
			errorMsg = wtExp.getLocalizedMessage() == null ? wtExp.getMessage() : wtExp.getLocalizedMessage();
			LOGGER.error(SMEmailUtilConstants.QUEUE_START_ERROR_CODE + SMEmailUtilConstants.QUEUE_START_ERROR_MESSAGE
					+ SMEmailUtilConstants.SEPD_IMAGEPAGE_WF_EMAIL_SCHEDULE_QUEUE_NAME);
			LOGGER.error(errorMsg);
			wtExp.printStackTrace();
		}
		LOGGER.debug("### SMSEPDImagePageWFEmailQueueInitializer.main() - End ###");
	}

	/**
	 * Execute Queue Entry. This method will call processImagePageScheduleQueue
	 * to identify the open tasks in last 24 hours for sending notification
	 * 
	 * @return statusInfo - StatusInfo
	 */
	public static synchronized StatusInfo executeImagePageNotificationQueueEntry() {
		LOGGER.debug(
				"### SMSEPDImagePageWFEmailQueueInitializer.executeImagePageNotificationQueueEntry() - Start ###");
		Timestamp scheduleTimeStamp = null;
		// Initialize StatusInfo variable
		StatusInfo statusInfo = null;
		try {
			
			// Calling processSEPDProdSeasScheduleQueue to send email notifications for opentasks in last 24 hours.
			SMSEPDImagePageEmailProcessor.processSEPDImagePagecheduleQueue();
			
			// Setting interval time for queue rescheduling
			scheduleTimeStamp = SMUtill.getTimestamp(SMEmailUtilConstants.SEPD_IMAGEPAGE_WF_EMAIL_SCHEDULE_QUEUE_START_TIME,
					SMEmailUtilConstants.TIME_ZONE, SMEmailUtilConstants.SEPD_IMAGEPAGE_WF_EMAIL_SCHEDULE_QUEUE_START_AM_OR_PM);
			LOGGER.debug("Next scheduleTimeStamp =" + scheduleTimeStamp);
			
		} finally {
			// rescheduling the queue
			try {
				LOGGER.debug("Rescheduling queue entry for next run");


				// Once process is complete, setting time for queue rerun
				statusInfo = SMNotificationQueueRescheduleService.rescheduleEntry(scheduleTimeStamp);
				LOGGER.debug(
						"#######################   IMAGE PAGE WF NOTIFICATION SCHEDULE QUEUE ENDS  ######################");
			} catch (WTPropertyVetoException wtPerptyExp) {
				// Error while rescheduling queue
				errorMsg = wtPerptyExp.getLocalizedMessage() == null ? wtPerptyExp.getMessage()
						: wtPerptyExp.getLocalizedMessage();
				LOGGER.error(SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_CODE
						+ SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE
						+ SMEmailUtilConstants.SEPD_IMAGEPAGE_WF_EMAIL_SCHEDULE_QUEUE_NAME);
				LOGGER.error(errorMsg);
			} catch (WTException wtExp) {
				errorMsg = wtExp.getLocalizedMessage() == null ? wtExp.getMessage() : wtExp.getLocalizedMessage();
				LOGGER.error(SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_CODE
						+ SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE
						+ SMEmailUtilConstants.SEPD_IMAGEPAGE_WF_EMAIL_SCHEDULE_QUEUE_NAME);
				LOGGER.error(errorMsg);
			}
		}
		LOGGER.debug("### SMSEPDImagePageWFEmailQueueInitializer.executeImagePageNotificationQueueEntry() - End ###");
		return statusInfo;
	}
}
