package com.sportmaster.wc.emailutility.queue.service;

import java.sql.Timestamp;

/**
 * SMNotificationService
 * This class is the interface for the scheduler methods.
 *
 * @author 'true' Priya
 * @version 'true' 1.0 version number
 *
 */
public interface SMNotificationService
{
	/**
	 * This method is using to schedule a Task for the Product Queue.
	 * @param smQueueName - smQueueName
	 * @param deleteOld - deleteOld
	 * @param methodName - methodNmae
	 * @param className - classNmae
	 * @throws wt.util.WTException - WTException
	 */
	public void scheduleJobForNotification( String smQueueName,boolean deleteOld,String methodName,String className,Timestamp timeStamp) throws wt.util.WTException;

	
}

