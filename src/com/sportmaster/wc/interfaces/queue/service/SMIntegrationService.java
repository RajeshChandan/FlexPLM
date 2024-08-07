package com.sportmaster.wc.interfaces.queue.service;

import java.sql.Timestamp;



/**
 * SMIntegrationService
 * This class is the interface for the scheduler methods.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 *
 */
public interface SMIntegrationService
{
	/**
	 * This method is using to schedule a Task for the Product Queue.
	 * @param smQueueName - smQueueName
	 * @param deleteOld - deleteOld
	 * @param methodNmae - methodNmae
	 * @param classNmae - classNmae
	 * @throws wt.util.WTException - WTException
	 */
	public void scheduleJobForIntegration( String smQueueName,boolean deleteOld,String methodNmae,String classNmae,Timestamp timeStamp) throws wt.util.WTException;

	
}

