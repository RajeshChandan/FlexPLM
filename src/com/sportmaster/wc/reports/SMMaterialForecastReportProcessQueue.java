package com.sportmaster.wc.reports;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.org.WTPrincipal;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.util.LCSProperties;


/**
 * 
 * @author 'true' BSC -PTC.
 * @version 'true' 1.0 version number.
 */
public final class SMMaterialForecastReportProcessQueue
{
	  
	/**
	 * Declaration for private ERROR_LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger("MFDRLOG");

	//default private constructor
	private SMMaterialForecastReportProcessQueue(){
		
	}
 

	/**
	 * This method actually invokes the Product task.
	 * which internally calls the relevant functions for Product Integration.
	 *
	 * @throws NoSuchMethodException the no such method exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws WTException the wT exception
	 */
	public static void execute(ClientContext context, Map<String, Object> inputSelectedMap) throws java.lang.NoSuchMethodException, java.lang.reflect.InvocationTargetException, java.lang.IllegalAccessException, wt.util.WTException
	{

		//Get the Processing queue name from the property entry
		String strQueueName=LCSProperties.get("com.sportmaster.reports.materialforecast.processingQueue","SM_MATERIAL_FC_REPORT");	
 
 		ProcessingQueue queue = null;
		try {
			
			//Set parameter for queue class
			Class<?>[] argTypesx = { Map.class,Boolean.class};
			Object[] argValuesx = { inputSelectedMap,true};
			
			//Disable the access to extract data for report
			SessionServerHelper.manager.setAccessEnforced(false);
			final WTPrincipal wtprincipal = SessionHelper.manager
					.getAdministrator();

			//set context to administrator
			wt.session.SessionContext.setEffectivePrincipal(wtprincipal);
			
			//get existing Processing qeueue
			queue = QueueHelper.manager.getQueue(strQueueName);
			LOGGER.debug(" exectuing addQueueEntry on WINCHILL METHOD SERVER  "
					+ queue);

			if (null != queue) {
				//Adding queue entry if the Processing queue is in start state
				if ("STARTED".equals(queue.getQueueState())) {
					LOGGER.debug("****Processed till addEntry*****");
					queue.addEntry(
							wtprincipal,
							"runReportQuery",
							"com.sportmaster.wc.reports.SMMaterialForecastReportQuery",
							argTypesx, argValuesx);
					LOGGER.debug("****Processed after addEntry********");
					SessionServerHelper.manager.setAccessEnforced(true);
				} else {
					//Creating Processing Queue and then adding queue entry if the Processing queue is in start state
					queue = createProcessingQueue(strQueueName);
					
					//adding queue entry to generate report
					queue.addEntry(
							wtprincipal,
							"runReportQuery",
							"com.sportmaster.wc.reports.SMMaterialForecastReportQuery",
							argTypesx, argValuesx);
					LOGGER.debug("****Processed after addEntry********");
					SessionServerHelper.manager.setAccessEnforced(true);
				}
			} else {
				//Creating Processing Queue and then adding queue entry if the Processing queue is in start state

				queue = createProcessingQueue(strQueueName);
				
				//adding queue entry to generate report
				queue.addEntry(
						wtprincipal,
						"runReportQuery",
						"com.sportmaster.wc.reports.SMMaterialForecastReportQuery",
						argTypesx, argValuesx);
				LOGGER.debug("****Processed after addEntry********");
				SessionServerHelper.manager.setAccessEnforced(true);
			}

		} catch (final WTException e) {
			LOGGER.error("Error while creating Processing queue for data extract for Material forecast report");
			throw new WTException(e);
		}
	}
  

	public static ProcessingQueue createProcessingQueue(final String strQueueName)
			throws WTException {
		LOGGER.debug(" exectuing on WINCHILL METHOD SERVER");
		ProcessingQueue pQueue = null;
		try {
			//Creating Processing Queue
			pQueue = QueueHelper.manager.createQueue(strQueueName, true);
		} catch (final WTException e) {
			LOGGER.error("Error while creating Processing queue for data extract for Report");
		}
		LOGGER.debug(" exectuing on WINCHILL METHOD SERVER DONE");
		//Returning the new created Processing Queue
		return pQueue;
	}

}