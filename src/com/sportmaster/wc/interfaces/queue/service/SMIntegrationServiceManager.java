package com.sportmaster.wc.interfaces.queue.service;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.lcs.wc.util.LCSProperties;
import com.ptc.windchill.keystore.WTKeyStoreUtil;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;
import wt.util.WTProperties;


/**
 * SMIntegrationServiceManager.java
 * This class contains the functions using to manage the available services in the existing server context and calling the queue execution functions.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMIntegrationServiceManager implements RemoteAccess, SMIntegrationService, Serializable
{
	private static final String ERROR_FOUND = "ERROR FOUND :-";
	private static final String SCHEDULE_JOB_METHOD_NAME = "scheduleJobForIntegration";
	/**
	 * Declaration for private attributes.
	 */
	private static final boolean SERVER_FLAG = RemoteMethodServer.ServerFlag;
	/**
	 * Declaration for private attributes.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Declaration for private attributes.
	 */
	private static final String RESOURCE_CLASS = "wt.fc.fcResource";

	/** The Constant logger. */
	public static final Logger logger= Logger.getLogger(SMIntegrationServiceManager.class);
	/**
	 * the flexUserName.
	 */
	private static final String FLEX_USER_NAME=LCSProperties.get("com.sportmaster.wc.intefaces.queue.service.windchillUserName");
	/**
	 * the flexpasswrod.
	 */
	private static final String FLEX_PASSWORD=LCSProperties.get("com.sportmaster.wc.intefaces.queue.service.windchillPassword");

	/**
	 * This method is using to get the Manager (get the default Manager from the ManagerServiceFactory) using to call the scheduled tasks.
	 * @throws wt.util.WTException exceptions
	 * @return manager Manager
	 */
	private static Manager getManager() throws wt.util.WTException
	{
		logger.debug("### START SMIntegrationServiceManager.getManager() ###");
		Manager manager = ManagerServiceFactory.getDefault().getManager(com.sportmaster.wc.interfaces.queue.service.SMIntegrationStandardService.class);

		if (manager == null)
		{
			Object[] param = { "com.sportmaster.wc.interfaces.queue.service.SMIntegrationStandardService" };
			throw new WTException(RESOURCE_CLASS, wt.fc.fcResource.UNREGISTERED_SERVICE, param);
		}

		logger.debug("### END SMIntegrationServiceManager.getManager() ###");
		return manager;
	}


	/**
	 * This method is to call the schedule job for integration in Standard service class.
	 * @param smQueueName ProductQueue Name
	 * @param smDeleteOld DeleteOld Queue
	 * @param  methodName - methodName
	 * @param classNmae - classNmae
	 * @throws wt.util.WTException exceptions
	 */
	@Override
	public void scheduleJobForIntegration( String smQueueName, boolean smDeleteOld,String methodName,String classNmae,Timestamp timeStamp) throws wt.util.WTException
	{
		if(SERVER_FLAG)
		{

			((SMIntegrationStandardService) getManager()).scheduleJobForIntegration( smQueueName, smDeleteOld,methodName,classNmae,timeStamp);
			return;
		}
		else
		{
			try
			{
				RemoteMethodServer rms=RemoteMethodServer.getDefault();
				rms.setUserName(FLEX_USER_NAME);

				rms.setPassword(WTKeyStoreUtil.decryptProperty("com.sportmaster.wc.intefaces.queue.service.windchillPassword",FLEX_PASSWORD, WTProperties.getLocalProperties().getProperty("wt.home")));
				Class<?>[] argTypes = { String.class,Boolean.TYPE,String.class,String.class,Timestamp.class };
				Object[] args = { smQueueName, Boolean.valueOf(smDeleteOld),methodName,classNmae ,timeStamp};
				rms.invoke(SCHEDULE_JOB_METHOD_NAME, "com.sportmaster.wc.interfaces.queue.service.SMIntegrationStandardService", this,
						argTypes, args);
				return;
			}
			catch (RemoteException remoteExp)
			{
				Object[] param = {SCHEDULE_JOB_METHOD_NAME};
				logger.error(ERROR_FOUND, remoteExp);
				throw new WTException(remoteExp, RESOURCE_CLASS, wt.fc.fcResource.OPERATION_FAILURE, param);
			}
			catch (InvocationTargetException invokeExp)
			{
				logger.error(ERROR_FOUND, invokeExp);
				Object[] param = {SCHEDULE_JOB_METHOD_NAME};
				throw new WTException(invokeExp, RESOURCE_CLASS, wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (IOException ioeExp) {
				logger.error(ERROR_FOUND, ioeExp);
				Object[] param = {SCHEDULE_JOB_METHOD_NAME};
				throw new WTException(ioeExp, RESOURCE_CLASS, wt.fc.fcResource.OPERATION_FAILURE, param);
			}

		}



	}


}
