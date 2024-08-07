package com.sportmaster.wc.emailutility.sepd.queue;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.lcs.wc.util.LCSProperties;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.queue.service.SMNotificationQueueRescheduleService;
import com.sportmaster.wc.emailutility.queue.service.SMNotificationServiceManager;
import com.sportmaster.wc.emailutility.sepd.processor.SMSEPDPSCSCancelledProcessor;
import com.sportmaster.wc.helper.SMUtill;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class SMSEPDPSCSCancelledQueueInitializer {

	/**
	 * 
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSEPDPSCSCancelledQueueInitializer.class);

	/**
	 * 
	 * CLASS_NAME.
	 */
	private static final String CLASS_NAME = "SMSEPDPSCSCancelledQueueInitializer";

	/**
	 * METHOD_START.
	 */
	public static final String METHOD_START = "--Start";
	/**
	 * METHOD_END.
	 */
	public static final String METHOD_END = "--End";

	/**
	 * EXCEPTION_OCCURED.
	 */
	public static final String EXCEPTION_OCCURED = "--Exception Ocuured: ";

	/**
	 * SEPD_PS_CS_CANCELLED_EMAIL_SCHEDULE_QUEUE_START_TIME
	 */
	public final static String SEPD_PS_CS_CANCELLED_EMAIL_SCHEDULE_QUEUE_START_TIME = LCSProperties
			.get("com.sportmaster.wc.emailutility.sepd.queue.SMSEPDPSCSCancelled.scheduleQueueStartTime", "5.00");

	/**
	 * SEPD_PS_CS_CANCELLED_EMAIL_SCHEDULE_QUEUE_START_AM_PM
	 */
	public final static String SEPD_PS_CS_CANCELLED_EMAIL_SCHEDULE_QUEUE_START_AM_PM = LCSProperties
			.get("com.sportmaster.wc.emailutility.sepd.queue.SMSEPDPSCSCancelled.AMorPM", "AM");

	/**
	 * SEPD_PS_CS_CANCELLED_EMAIL_SCHEDULE_QUEUE_NAME.
	 */
	public static final String SEPD_PS_CS_CANCELLED_EMAIL_SCHEDULE_QUEUE_NAME = LCSProperties.get(
			"com.sportmaster.wc.emailutility.sepd.queue.SMSEPDPSCSCancelled.scheduleQueueName",
			"SEPD_PS_CS_CancelledEmailQueue");

	/**
	 * Declaring Error Message.
	 */
	private static String ERROR_MSG;

	/**
	 * Constructor.
	 */
	protected SMSEPDPSCSCancelledQueueInitializer() {
		// protected Constructor
	}

	/**
	 * Initializes the queue. queue will call this Main method
	 * 
	 * @param arguments - String[]
	 * @throws WTException - WTException
	 */
	public static void main(String arguments[]) throws WTException {
		String methodName = "main()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Initializing time stamp.
		Timestamp scheduledTimeStamp;
		// Action.
		String queueActions;
		if (arguments.length < 1) {
			LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.emailutility.sepd.queue.SMSEPDPSCSCancelledQueueInitializer [options]\n");
			LOGGER.debug(
					"SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			LOGGER.debug("EXECUTE : Synchronous Execution (Initialize execution now and process the notifications)\n");
			System.exit(0);
		}
		queueActions = arguments[0];
		try {
			// Execute Action
			if (SMEmailUtilConstants.EXECUTE.equalsIgnoreCase(queueActions)) {
				// If option is Execute, Get the current time stamp and run the queue
				// immediately
				scheduledTimeStamp = new Timestamp(System.currentTimeMillis());
			}
			// SCHEDULE action.
			// If option is Schedule, get the scheduled time stamp and execute the queue
			// during scheduled run time
			else if (SMEmailUtilConstants.SCHEDULE.equals(queueActions)) {
				// call getTimeStamp method to get the queue run time for reschedule.
				scheduledTimeStamp = SMUtill.getTimestamp(SEPD_PS_CS_CANCELLED_EMAIL_SCHEDULE_QUEUE_START_TIME,
						SMEmailUtilConstants.TIME_ZONE, SEPD_PS_CS_CANCELLED_EMAIL_SCHEDULE_QUEUE_START_AM_PM);
			} // If neither then should not run
			else {
				return;
			}
			LOGGER.info(CLASS_NAME + "--" + methodName
					+ "--Product-Season or Colorway-Season Cancelled notification scheduled timestamp--"
					+ scheduledTimeStamp);
			SMNotificationServiceManager smNotificationServiceManager = new SMNotificationServiceManager();

			// Initializing the queue and initiating the task for notification
			smNotificationServiceManager.scheduleJobForNotification(SEPD_PS_CS_CANCELLED_EMAIL_SCHEDULE_QUEUE_NAME,
					true, "executeSEPDPSCSCancelledEmailNotificationQueueEntry",
					SMSEPDPSCSCancelledQueueInitializer.class.getName(), scheduledTimeStamp);

		} catch (WTException ex) {
			ERROR_MSG = ex.getLocalizedMessage() == null ? ex.getMessage() : ex.getLocalizedMessage();
			LOGGER.error(CLASS_NAME + "--" + methodName + "--" + SMEmailUtilConstants.QUEUE_START_ERROR_CODE
					+ SMEmailUtilConstants.QUEUE_START_ERROR_MESSAGE + SEPD_PS_CS_CANCELLED_EMAIL_SCHEDULE_QUEUE_NAME);
			LOGGER.error(ERROR_MSG);
			ex.printStackTrace();
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * Execute Queue Entry. This method will call processSEPDProdSeasScheduleQueue
	 * to identify the open tasks in last 24 hours for sending notification
	 * 
	 * @return statusInfo - StatusInfo
	 */
	public static synchronized StatusInfo executeSEPDPSCSCancelledEmailNotificationQueueEntry() {
		String methodName = "executeSEPDPSCSCancelledEmailNotificationQueueEntry()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		Timestamp scheduledTimeStamp = null;
		// Initialize StatusInfo variable
		StatusInfo statusInformation = null;
		try {

			// Calling processSEPDProductSKUSeasonCancelledScheduleQueue to send email
			// notifications
			SMSEPDPSCSCancelledProcessor.processSEPDPSCSCancelledScheduleQueue();

			// Setting interval time for queue rescheduling
			scheduledTimeStamp = SMUtill.getTimestamp(SEPD_PS_CS_CANCELLED_EMAIL_SCHEDULE_QUEUE_START_TIME,
					SMEmailUtilConstants.TIME_ZONE, SEPD_PS_CS_CANCELLED_EMAIL_SCHEDULE_QUEUE_START_AM_PM);
			LOGGER.info(CLASS_NAME + "--" + methodName + "--Next scheduleTimeStamp =" + scheduledTimeStamp);

		} finally {
			// rescheduling the queue
			try {
				LOGGER.info(CLASS_NAME + "--" + methodName + "--Rescheduling queue entry for next run--");

				// Once process is complete, setting time for queue rerun
				statusInformation = SMNotificationQueueRescheduleService.rescheduleEntry(scheduledTimeStamp);
				LOGGER.info(CLASS_NAME + "--" + methodName
						+ "---- SEPD PRODUCT-SEASON OR SKU-SEASON CANCELLED EMAIL NOTIFICATION SCHEDULE QUEUE ENDS  -----");
			} catch (WTPropertyVetoException pve) {
				// Error while rescheduling queue
				ERROR_MSG = pve.getLocalizedMessage() == null ? pve.getMessage() : pve.getLocalizedMessage();
				LOGGER.error(CLASS_NAME + "--" + methodName + "--" + SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_CODE
						+ SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE
						+ SEPD_PS_CS_CANCELLED_EMAIL_SCHEDULE_QUEUE_NAME);
				LOGGER.error(ERROR_MSG);
			} catch (WTException ex) {
				ERROR_MSG = ex.getLocalizedMessage() == null ? ex.getMessage() : ex.getLocalizedMessage();
				LOGGER.error(CLASS_NAME + "--" + methodName + "--" + SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_CODE
						+ SMEmailUtilConstants.QUEUE_RESCHEDULE_ERROR_MESSAGE
						+ SEPD_PS_CS_CANCELLED_EMAIL_SCHEDULE_QUEUE_NAME);
				LOGGER.error(ERROR_MSG);
			}
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
		return statusInformation;
	}

}
