package com.sportmaster.wc.emailutility.queue.service;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.lcs.wc.util.LCSProperties;
import com.ptc.windchill.keystore.WTKeyStoreUtil;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;

import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.Manager;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;
import wt.util.WTProperties;

/**
 * SMNotificationServiceManager.java This class contains the functions used to
 * manage available services in the existing server context and calling queue
 * execution functions.
 *
 * @author 'true' Priya
 * @version 'true' 1.0 version number
 */
public class SMNotificationServiceManager implements RemoteAccess, SMNotificationService, Serializable {
	/**
	 * Declaration for private attributes.
	 */
	private static final boolean SERVERFLAG = RemoteMethodServer.ServerFlag;
	/**
	 * Declaration for private attributes.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Declaration for private attributes.
	 */
	private static final String RESOURCE_CLASS = "wt.fc.fcResource";

	/** The Constant logger. */
	public static final Logger logger = Logger.getLogger(SMNotificationServiceManager.class);
	/**
	 * the flexUserName.
	 */
	private static final String FLEXUSERNAME = LCSProperties
			.get("com.sportmaster.wc.intefaces.queue.service.windchillUserName");
	/**
	 * the flexpasswrod.
	 */
	private static final String FLEXPASSWORD = LCSProperties
			.get("com.sportmaster.wc.intefaces.queue.service.windchillPassword");

	/**
	 * This method is using to get the Manager (get the default Manager from the
	 * ManagerServiceFactory) using to call the scheduled tasks.
	 * 
	 * @throws wt.util.WTException exceptions
	 * @return manager Manager
	 */
	private static Manager getManager() throws wt.util.WTException {
		logger.debug("### START SMNotificationServiceManager.getManager() ###");
		Manager manager = ManagerServiceFactory.getDefault()
				.getManager(com.sportmaster.wc.emailutility.queue.service.SMNotificationStandardService.class);

		if (manager == null) {
			Object[] param = { "com.sportmaster.wc.emailutility.queue.service.SMNotificationStandardService" };
			throw new WTException(RESOURCE_CLASS, wt.fc.fcResource.UNREGISTERED_SERVICE, param);
		}

		logger.debug("### END SMNotificationServiceManager.getManager() ###");
		return manager;
	}

	/**
	 * This method is to call the schedule job for integration in Standard service
	 * class.
	 * 
	 * @param smQueueName NotificationQueue Name
	 * @param smDeleteOld DeleteOld Queue
	 * @param methodName  - methodName
	 * @param className   - classNmae
	 * @throws wt.util.WTException exceptions
	 */
	public void scheduleJobForNotification(String smQueueName, boolean smDeleteOld, String methodName, String className,
			Timestamp timeStamp) throws wt.util.WTException {
		logger.debug("### START SMNotificationServiceManager.scheduleJobForNotification() ###");
		if (SERVERFLAG) {

			((SMNotificationStandardService) getManager()).scheduleJobForNotification(smQueueName, smDeleteOld,
					methodName, className, timeStamp);
			return;
		} else {
			try {
				RemoteMethodServer rms = RemoteMethodServer.getDefault();
				rms.setUserName(FLEXUSERNAME);
				rms.setPassword(
						WTKeyStoreUtil.decryptProperty("com.sportmaster.wc.intefaces.queue.service.windchillPassword",
								FLEXPASSWORD, WTProperties.getLocalProperties().getProperty("wt.home")));
				Class<?>[] argTypes = { String.class, Boolean.TYPE, String.class, String.class, Timestamp.class };
				Object[] args = { smQueueName, Boolean.valueOf(smDeleteOld), methodName, className, timeStamp };
				rms.invoke(SMEmailUtilConstants.SCHEDULEJOBFORNOTIFICATION,
						"com.sportmaster.wc.emailutility.queue.service.SMNotificationStandardService", this, argTypes,
						args);
				logger.debug("### END SMNotificationServiceManager.scheduleJobForNotification() ###");
				return;
			} catch (RemoteException remoteExp) {
				Object[] param = { SMEmailUtilConstants.SCHEDULEJOBFORNOTIFICATION };
				logger.error("RemoteException in SMNotificationServiceManager.scheduleJobForNotification()-"+remoteExp.getMessage());
				//remoteExp.printStackTrace();
				throw new WTException(remoteExp, RESOURCE_CLASS, wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (InvocationTargetException invokeExp) {
				//invokeExp.printStackTrace();
				Object[] param = { SMEmailUtilConstants.SCHEDULEJOBFORNOTIFICATION };
				logger.error("InvocationTargetException in SMNotificationServiceManager.scheduleJobForNotification()-"+invokeExp.getMessage());

				throw new WTException(invokeExp, RESOURCE_CLASS, wt.fc.fcResource.OPERATION_FAILURE, param);
			} catch (IOException ioeExp) {
				//ioeExp.printStackTrace();
				Object[] param = { SMEmailUtilConstants.SCHEDULEJOBFORNOTIFICATION };
				logger.error("IOException in SMNotificationServiceManager.scheduleJobForNotification()-"+ioeExp.getMessage());

				throw new WTException(ioeExp, RESOURCE_CLASS, wt.fc.fcResource.OPERATION_FAILURE, param);
			}

		}

	}

}
