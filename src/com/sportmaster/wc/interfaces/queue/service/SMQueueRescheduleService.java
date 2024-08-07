package com.sportmaster.wc.interfaces.queue.service;

import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.sql.Timestamp;
/**
 * SMQueueRescheduleService.java
 * This class contains method to reschedule the queue entry.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMQueueRescheduleService {

	protected SMQueueRescheduleService() {
	}
	
	/**
	 * Rescheduling queue entry.
	 * @param interval - queue reschedule time interval
	 * @return - StatusInfo
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws WTException - WTException
	 */
	public static StatusInfo rescheduleEntry(Timestamp rescheduletimeStamp) throws WTPropertyVetoException, WTException{
		StatusInfo statusInfo = null;

			statusInfo = StatusInfo.newStatusInfo("RESCHEDULE");
			statusInfo.setRescheduleTime(rescheduletimeStamp);
			return statusInfo;
	}
	
}
