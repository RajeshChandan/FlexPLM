package com.sportmaster.wc.emailutility.fpd.queue;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.lcs.wc.util.LCSProperties;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.queue.service.SMNotificationQueueRescheduleService;
import com.sportmaster.wc.emailutility.queue.service.SMNotificationServiceManager;
import com.sportmaster.wc.emailutility.fpd.processor.SMFPDColorwayCreateMOAProcessor;
import com.sportmaster.wc.helper.SMUtill;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class SMFPDColorwayCreateMOAQueueInitializer {

	/**
	 * 
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMFPDColorwayCreateMOAQueueInitializer.class);

	/**
	 * 
	 * CLASS_NAME.
	 */
	private static final String CLASS_NAME = "SMFPDColorwayCreateMOAQueueInitializer";
	/**
	 * METHOD_START.
	 */
	public static final String METHOD_START = "--Start";
	/**
	 * METHOD_END.
	 */
	public static final String METHOD_END = "--End";

	/**
	 * FPD_CW_CREATE_MOA_EMAIL_SCHEDULE_QUEUE_START_TIME
	 */
	public final static String FPD_CW_CREATE_MOA_EMAIL_SCHEDULE_QUEUE_START_TIME = LCSProperties
			.get("com.sportmaster.wc.emailutility.fpd.queue.SMFPDColorwayCreate.scheduleQueueStartTime", "4.00");

	/**
	 * FPD_CW_CREATE_MOA_EMAIL_SCHEDULE_QUEUE_START_AM
	 */
	public final static String FPD_CW_CREATE_MOA_EMAIL_SCHEDULE_QUEUE_START_AM_PM = LCSProperties
			.get("com.sportmaster.wc.emailutility.fpd.queue.SMFPDColorwayCreate.AMorPM", "PM");

	/**
	 * FPD_CW_CREATE_MOA_EMAIL_SCHEDULE_QUEUE_NAME.
	 */
	public static final String FPD_CW_CREATE_MOA_EMAIL_SCHEDULE_QUEUE_NAME = LCSProperties.get(
			"com.sportmaster.wc.emailutility.fpd.queue.SMFPDColorwayCreate.scheduleQueueName",
			"FPDColorwayCreateMOAEmailQueue");

	/**
	 * Declaring Error Message.
	 */
	private static String ERROR_MSG;

	/**
	 * Constructor.
	 */
	protected SMFPDColorwayCreateMOAQueueInitializer() {
		// protected Constructor
	}

	/**
	 * Initializes the queue. queue will call this Main method
	 * 
	 * @param args - String[]
	 * @throws WTException - WTException
	 */
	public static void main(String args[]) throws WTException {
		String methodName = "main()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Initializing time stamp.
		Timestamp scheduleTimeStamp;
		// Action.
		String queueAction;
		if (args.length < 1) {
			LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.emailutility.fpd.queue.SMFPDColorwayCreateMOAQueueInitializer [options]\n");
			LOGGER.debug(
					"SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			LOGGER.debug("EXECUTE : Synchronous Execution (Initialize execution now and process the notifications)\n");
			System.exit(0);
		}
		queueAction = args[0];
		try {
			// Execute Action
			if (SMEmailUtilConstants.EXECUTE.equalsIgnoreCase(queueAction)) {
				// If option is Execute, Get the current time stamp and run the queue
				// immediately
				scheduleTimeStamp = new Timestamp(System.currentTimeMillis());
			}
			// SCHEDULE action.
			// If option is Schedule, get the scheduled time stamp and execute the queue
			// during scheduled run time
			else if (SMEmailUtilConstants.SCHEDULE.equals(queueAction)) {
				// call getTimeStamp method to get the queue run time for reschedule.
				scheduleTimeStamp = SMUtill.getTimestamp(FPD_CW_CREATE_MOA_EMAIL_SCHEDULE_QUEUE_START_TIME,
						SMEmailUtilConstants.TIME_ZONE, FPD_CW_CREATE_MOA_EMAIL_SCHEDULE_QUEUE_START_AM_PM);
			} // If neither then should not run
			else {
				return;
			}
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--New Colorway Create notification scheduled timestamp="
					+ scheduleTimeStamp);
			SMNotificationServiceManager smNotificationServiceManager = new SMNotificationServiceManager();

			// Initializing the queue and initiating the task for notification
			smNotificationServiceManager.scheduleJobForNotification(FPD_CW_CREATE_MOA_EMAIL_SCHEDULE_QUEUE_NAME, true,
					"executeFPDColorwayCreateMOAEmailNotificationQueueEntry",
					SMFPDColorwayCreateMOAQueueInitializer.class.getName(), scheduleTimeStamp);

		} catch (WTException wtExp) {
			ERROR_MSG = wtExp.getLocalizedMessage() == null ? wtExp.getMessage() : wtExp.getLocalizedMessage();
			LOGGER.error(CLASS_NAME + "--" + methodName + "--" + SMEmailUtilConstants.QUEUE_START_ERROR_CODE
					+ SMEmailUtilConstants.QUEUE_START_ERROR_MESSAGE + FPD_CW_CREATE_MOA_EMAIL_SCHEDULE_QUEUE_NAME);
			LOGGER.error(ERROR_MSG);
			wtExp.printStackTrace();
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * Execute Queue Entry. This method will call processSEPDProdSeasScheduleQueue
	 * to identify the open tasks in last 24 hours for sending notification
	 * 
	 * @return statusInfo - StatusInfo
	 */
	public static synchronized StatusInfo executeFPDColorwayCreateMOAEmailNotificationQueueEntry() {
		String methodName = "executeFPDColorwayCreateMOAEmailNotificationQueueEntry()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		Timestamp scheduleTimeStamp = null;
		// Initialize StatusInfo variable
		StatusInfo statusInfo = null;
		try {
			// Calling processFPDColorwayCreateScheduleQueue to send email notifications
			SMFPDColorwayCreateMOAProcessor.processFPDColorwayCreateScheduleQueue();
			// Setting interval time for queue rescheduling
			scheduleTimeStamp = SMUtill.getTimestamp(FPD_CW_CREATE_MOA_EMAIL_SCHEDULE_QUEUE_START_TIME,
					SMEmailUtilConstants.TIME_ZONE, FPD_CW_CREATE_MOA_EMAIL_SCHEDULE_QUEUE_START_AM_PM);
			LOGGER.debug(CLASS_NAME + "--" + methodName + "Next scheduleTimeStamp =" + scheduleTimeStamp);

		} finally {
			// rescheduling the queue
			try {
				LOGGER.debug(CLASS_NAME + "--" + methodName + "--Rescheduling queue entry for next run--");
				// Once process is complete, setting time for queue rerun
				statusInfo = SMNotificationQueueRescheduleService.rescheduleEntry(scheduleTimeStamp);
				LOGGER.debug(CLASS_NAME + "--" + methodName
						+ "---- FPD COLORWAY CREATE MOA EMAIL NOTIFICATION SCHEDULE QUEUE ENDS -----");
			} catch (WTPropertyVetoException wtPerptyExp) {
				// Error while rescheduling queue
				ERROR_MSG = wtPerptyExp.getLocalizedMessage() == null ? wtPerptyExp.getMessage()
						: wtPerptyExp.getLocalizedMessage();
				LOGGER.error(CLASS_NAME + "--" + methodName + "--" + SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_CODE
						+ SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE
						+ FPD_CW_CREATE_MOA_EMAIL_SCHEDULE_QUEUE_NAME);
				LOGGER.error(ERROR_MSG);
			} catch (WTException wtExp) {
				ERROR_MSG = wtExp.getLocalizedMessage() == null ? wtExp.getMessage() : wtExp.getLocalizedMessage();
				LOGGER.error(CLASS_NAME + "--" + methodName + "--" + SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_CODE
						+ SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE
						+ FPD_CW_CREATE_MOA_EMAIL_SCHEDULE_QUEUE_NAME);
				LOGGER.error(ERROR_MSG);
			}
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
		return statusInfo;
	}

}
