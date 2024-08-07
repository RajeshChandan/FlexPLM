package com.sportmaster.wc.interfaces.queue.service;


import java.io.Serializable;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import wt.org.WTPrincipal;
import wt.queue.QueueHelper;
import wt.queue.ScheduleQueue;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;

import com.lcs.wc.util.FormatHelper;


/**
 * SMIntegrationStandardService.java
 * This class contains the generic functions using to perform the create/delete operation of queues based on the inputs in the existing application context.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMIntegrationStandardService extends StandardManager implements SMIntegrationService, Serializable
{	
	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMIntegrationStandardService.class);
	/**
	 * Declaration for private attribute.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Declaration for private attribute.
	 */
	private static SMIntegrationStandardService instance;


	/**
	 * This constructor is using automatically in the Manager class(SMIntegrationStandardService class) while initializing the Manager instance for Queue.
	 * @return instance - SMIntegrationStandardService
	 * @throws wt.util.WTException - WTException
	 */
	public static SMIntegrationStandardService newSMIntegrationStandardService() throws wt.util.WTException
	{	
		synchronized (SMIntegrationStandardService.class) {
			if(instance == null)
			{
				instance = new SMIntegrationStandardService();
				instance.initialize();
			}
			return instance;
		}


	}



	/**
	 * This method is using to create the Product Queue.
	 * @param smQueueName - String
	 * @param deleteOld - boolean
	 * @param methodNmae
	 * @param classNmae
	 * @throws wt.util.WTException exceptions
	 */	
	public void scheduleJobForIntegration( String smQueueName, boolean deleteOld,String methodNmae,String classNmae,Timestamp timeStamp) throws wt.util.WTException
	{
		//validating the incoming TaskObject and queueName, based on the validation proceeding the process of registering the queue in the Queue Manager
		if ( !FormatHelper.hasContent(smQueueName))
		{
			LOGGER.debug("scheduleJob - obj: "  + ", smQueueName: " + smQueueName + ". Do not schedule the entry");
			LOGGER.error("scheduleJob - obj: "  + ", smQueueName: " + smQueueName + ". Do not schedule the entry");
			return;
		}
		LOGGER.debug("ScheduleQueue Name is : " + smQueueName);
		try{
			//before creating a new queue validating the existence of the queue with the input details, if exists based on certain criteria DELETING the queue
			ScheduleQueue ipQueue = (ScheduleQueue) QueueHelper.manager.getQueue(smQueueName, wt.queue.ScheduleQueue.class);
			if (ipQueue != null && deleteOld)
			{
				LOGGER.debug("ScheduleQueue is : " + ipQueue);
				QueueHelper.manager.deleteQueue(ipQueue);
				ipQueue = null;
			}

			//if the queue is null then creating and registering a new queue in the queue manager needed for 'Product Integration'
			if(ipQueue == null)
			{
				ipQueue = QueueHelper.manager.createScheduleQueue(smQueueName);
				Class<?> aCrlass[] = { };
				Object arObj[] = { };
				WTPrincipal wtprincipal = SessionHelper.manager.getAdministrator();
				
				ipQueue.addEntry(wtprincipal, methodNmae, classNmae, aCrlass, arObj, timeStamp);
				QueueHelper.manager.startQueue(ipQueue);
			}
		}catch(WTException wtExp){
			LOGGER.debug(wtExp.getLocalizedMessage());
		}
	}

}