package com.sportmaster.wc.emailutility.queue.service;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * SMNotificationQueueRescheduleService.java This class contains method to
 * reschedule the queue entry.
 *
 * @author 'true' Priya
 * @version 'true' 1.0 version number
 */
public class SMNotificationQueueRescheduleService {

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMNotificationQueueRescheduleService.class);

	protected SMNotificationQueueRescheduleService() {
	}

	/**
	 * Rescheduling queue entry.
	 * 
	 * @param interval - queue reschedule time interval
	 * @return - StatusInfo
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws WTException             - WTException
	 */
	public static StatusInfo rescheduleEntry(Timestamp rescheduletimeStamp)
			throws WTPropertyVetoException, WTException {
		LOGGER.debug("#### SMNotificationQueueRescheduleService.rescheduleEntry - START #####");
		StatusInfo statusInfo = null;
		LOGGER.debug("rescheduletimeStamp=="+rescheduletimeStamp);
		statusInfo = StatusInfo.newStatusInfo("RESCHEDULE");
		statusInfo.setRescheduleTime(rescheduletimeStamp);
		LOGGER.debug("#### SMNotificationQueueRescheduleService.rescheduleEntry - END #####");

		return statusInfo;
	}

}
