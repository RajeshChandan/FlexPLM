package com.sportmaster.wc.interfaces.webservices.outbound.feedback.service;

import java.sql.Timestamp;
import java.util.Date;
import org.apache.log4j.Logger;
import wt.queue.StatusInfo;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.sportmaster.wc.helper.SMUtill;
import com.sportmaster.wc.interfaces.queue.service.SMIntegrationServiceManager;
import com.sportmaster.wc.interfaces.queue.service.SMQueueRescheduleService;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.feedback.util.SMFBIntegrationUtilBean;

/**
 * SMListValuesDataFeedBackQueueInitializer.java
 * This class has methods for create of schedule queue for feedback response.
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */

public class SMListValuesDataFeedBackQueueInitializer {


	/**
	 * Constructor.
	 */
	protected SMListValuesDataFeedBackQueueInitializer() {


	}

	/**
	 * Declaration for private bean.
	 */
	private static SMFBIntegrationUtilBean sfbBean;
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMListValuesDataFeedBackQueueInitializer.class);
	/**
	 * QUEUE_NAME.
	 */
	private static final String QUEUE_NAME;
	static {
		QUEUE_NAME=new SMInboundWebserviceConstants().getListValuesDataFeedbackQueueName();
	}
	public static void main(String[] args) {
		Timestamp feedbackTimeStamp;
		String feedBackAction;
		//checking arguement length
		if (args.length <1) {
			//adding debug message
			LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.interfaces.webservices.outbound.feedback.service.SMListValuesDataFeedBackQueueInitializer [options]\n");
			LOGGER.debug(
					"SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			LOGGER.debug(
					"EXECUTE : Synchronous Execution (Initialize execution now and process the FeedBack data )\n");
			//doing system exit
			System.exit(0);
		}
		//getting first agrument
		feedBackAction=args[0];
		try
		{	
			if (SMInboundWebserviceConstants.EXECUTE.equalsIgnoreCase(feedBackAction)) {
				// Get the current timestamp - can we not just use
				// currentTime, maybe add 500 msec to account for CPU lag?
				feedbackTimeStamp = new Timestamp(System.currentTimeMillis());
			}
			// If running in queue the timestamp is scheduled time
			else if (SMInboundWebserviceConstants.SCHEDULE.equals(feedBackAction)) {
				feedbackTimeStamp=SMUtill.getTimestamp(SMInboundWebserviceConstants.FEEDBACK_SCHEDULE_TIME, SMInboundWebserviceConstants.TIME_ZONE, SMInboundWebserviceConstants.FEEDBACK_SCHEDULE_TIME_AM_PM);
			} // If neither then should not run
			else {
				return;
			}
			SMIntegrationServiceManager service = new SMIntegrationServiceManager();
			service.scheduleJobForIntegration(QUEUE_NAME,true,"executeListValuesDataFeedbackQueueEntry",SMListValuesDataFeedBackQueueInitializer.class.getName(),feedbackTimeStamp);
			System.exit(0);
		}
		catch (WTException wtExp)
		{	
			wtExp.printStackTrace();
		}
	}

	/**
	 * Set Logger Summary.
	 * @return String values of log
	 */
	public static String setLogger(){
		StringBuffer summary=new StringBuffer();//creating String Buffer for multi line String
		summary.append("STATUS FEEDBACK INBOUND  INTEGRATION  SUMMARY:\n");
		summary.append("**********************************************************************************\n");
		summary.append("**TOTAL NUMBER STATUS SUCESSFULY SENT FOR FEEDBACK INTEGRATION -"+sfbBean.getFbObjCount()+"\n");
		summary.append("**TOTAL NUMBER STATUS FAILED FOR FEEDBACK INTEGRATION -"+sfbBean.getFbFailCount()+"\n");
		summary.append("**STATUS FEEDBACK INTEGRATION START TIME -"+sfbBean.getFbStartTimeInGMT()+"\n");
		summary.append("**STATUS FEEDBACK INTEGRATION END TIME -"+sfbBean.getFbEndTimeinGMT()+"\n");
		summary.append("*******************************************************************************");
		return summary.toString();//Returning log value.
	}

	/**
	 * Method to execute the feedback queue. 
	 * @return the StatusInfo
	 */
	public static synchronized StatusInfo executeListValuesDataFeedbackQueueEntry ()    {
		StatusInfo statusInfo=null;
		long fbIntervalTimeinMilliSeconds=0;
		try {
			LOGGER.debug("STATUS FEEDBACK INTEGRATION STARTS");
			LOGGER.debug("Integration Queue Name: " +QUEUE_NAME);
			//CREATING BEAN OBEJCT
			sfbBean= new SMFBIntegrationUtilBean();
			long curentTime=System.currentTimeMillis();
			//scheduling interval time
			fbIntervalTimeinMilliSeconds=(curentTime+SMInboundWebserviceConstants.FEEDBACK_QUEUE_INTERVAL * 60* 1000);
			sfbBean.setFbStartTimeInGMT(new Date(curentTime).toString());
			//calling client to invoke webservices
			new SMListValuesDataFeedBackWebClientService().sminvokeWebRequest(sfbBean);
			sfbBean.setFbEndTimeinGMT(new Date(System.currentTimeMillis()).toString());


		} 
		finally{
			try {
				LOGGER.debug("Rescheduling queue entry for next run");
				// rescheduling the queue
				statusInfo=SMQueueRescheduleService.rescheduleEntry(new Timestamp(fbIntervalTimeinMilliSeconds));
				LOGGER.debug(setLogger());
				sfbBean=null;
				LOGGER.debug("STATUS FEEDBACK INTEGRATION ends");
			} catch (WTPropertyVetoException wtVeExp) {
				wtVeExp.printStackTrace();
			} catch (WTException etExp) {
				etExp.printStackTrace();
			}
		}
		return statusInfo;
	}
}
