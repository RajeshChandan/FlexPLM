package com.lowes.util;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.session.SessionHelper;
import wt.util.WTException;

public class LowesQueueService {

	private static final Logger LOGGER =  LogManager.getLogger(LowesQueueService.class);
	
	/**
	 * @param queueMap
	 */
	public static void processQueue(Map<String, Object> queueMap)  {
		String methodName = "processQueue- ";
		
		LOGGER.debug( methodName + "Start");
		LOGGER.debug( methodName + "queueMap "+queueMap);
		
		String className = (String) queueMap.get("className");
		String methodToExecute = (String) queueMap.get("methodToExecute");
		String queueName = (String) queueMap.get("queueName");
		
		Class[] argTypes = (Class[]) queueMap.get("argTypes");
		Object[] args = (Object[]) queueMap.get("args");
		
		ProcessingQueue pQueue = null;
		
		try {
			pQueue = (ProcessingQueue) QueueHelper.manager.getQueue(queueName, ProcessingQueue.class);
			if (pQueue == null) {
				pQueue = QueueHelper.manager.createQueue(queueName);
				pQueue = QueueHelper.manager.startQueue(pQueue);
			}
			if (pQueue != null) {
				pQueue.addEntry(SessionHelper.getPrincipal(), methodToExecute, className, argTypes, args);
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		LOGGER.debug( methodName + "End");
	}
	
}
