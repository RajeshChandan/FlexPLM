package com.sportmaster.wc.interfaces.webservices.outbound.material.service;

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
import com.sportmaster.wc.interfaces.webservices.outbound.material.util.SMMaterialQueueBean;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

/**
 * SMMaterialQueueInitializer.java
 * This class has methods for create of schedule queue Material .
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */

public class SMMaterialQueueInitializer {


	/**
	 * Constructor.
	 */
	protected SMMaterialQueueInitializer() {


	}

	/**
	 * Declaration for private bean.
	 */
	private static SMMaterialQueueBean queueBean;
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMMaterialQueueInitializer.class);

	public static void main(String[] args) {
		Timestamp materialTimeStamp;
		String materialAction;
		//checking arguement length
		if (args.length <1) {
			//adding debug message
			LOGGER.debug(
					"Usage: windchill com.sportmaster.wc.interfaces.webservices.outbound.material.service.SMMaterialQueueInitializer [options]\n");
			LOGGER.debug(
					"SCHEDULE : Asynchronous Execution (Add entry to Windchill Queue and set the queue run on scheduled time)\n");
			LOGGER.debug(
					"EXECUTE : Synchronous Execution (Initialize execution now and process the Material data )\n");
			//doing system exit
			System.exit(0);
		}
		//getting first argument
		materialAction=args[0];
		try
		{	
			if (SMInboundWebserviceConstants.EXECUTE.equalsIgnoreCase(materialAction)) {
				// Get the current timestamp - can we not just use
				// currentTime, maybe add 500 msec to account for CPU lag?
				materialTimeStamp = new Timestamp(System.currentTimeMillis());
			}
			// If running in queue the timestamp is scheduled time
			else if (SMInboundWebserviceConstants.SCHEDULE.equals(materialAction)) {
				materialTimeStamp=SMUtill.getTimestamp(SMOutboundWebServiceConstants.MATERIAL_SCHEDULE_QUEUE__START_TIME, SMInboundWebserviceConstants.TIME_ZONE, SMOutboundWebServiceConstants.MATERIAL_SCHEDULE_QUEUE__START_AM);
			} // If neither then should not run
			else {
				return;
			}
			SMIntegrationServiceManager service = new SMIntegrationServiceManager();
			service.scheduleJobForIntegration(SMOutboundWebServiceConstants.MATERIAL_INTEGRATION_QUEUE,true,"executeMaterialOutboundQueueEntry",SMMaterialQueueInitializer.class.getName(),materialTimeStamp);

			System.exit(0);
		}
		catch (WTException wtExcep)
		{	
			wtExcep.printStackTrace();
		}
	}

	/**
	 * Set Logger Summary.
	 * @return String values of log
	 */
	public static String setLoggerData(){
		StringBuffer summaryData=new StringBuffer();//creating String Buffer for multi line String
		summaryData.append("MATERIAL OUTBOUND INTEGARTION STATUS SUMMARY:\n");
		summaryData.append("**********************************************************************************\n");
		summaryData.append("**TOTAL NUMBER OF MATERIAL Processed to PLM GATE - "+queueBean.getTotalProcessedCount()+"\n");
		summaryData.append("**TOTAL NUMBER MATERIAL GET FAILED - "+queueBean.getTotalFailCount()+"\n");
		summaryData.append("**TOTAL NUMBER MATERIAL With Status  UPDATE_PENDING Found - "+queueBean.getTotalObjCount()+"\n");
		summaryData.append("**MATERIAL OUTBOUND INTEGRATION START TIME -"+queueBean.getMatStartTimeInGMT()+"\n");
		summaryData.append("**MATERIAL OUTBOUND INTEGRATION END TIME -"+queueBean.getMatEndTimeinGMT()+"\n");
		summaryData.append("*******************************************************************************");
		return summaryData.toString();//Returning log value.
	}

	/**
	 * Method to execute the feedback queue. 
	 * @return the StatusInfo
	 */
	public static synchronized StatusInfo executeMaterialOutboundQueueEntry ()    {
		StatusInfo statusInfo=null;
		long fbIntervalTimeinMilliSeconds=0;
		try {
			LOGGER.debug("#################### MATERIAL OUTBOUND INTEGRATION QUEUE STARTS #################### ");
			LOGGER.debug("Integration Queue Name: " +SMOutboundWebServiceConstants.MATERIAL_INTEGRATION_QUEUE);
			//CREATING BEAN OBEJCT
			queueBean= new SMMaterialQueueBean();
			long curentTime=System.currentTimeMillis();
			//scheduling interval time
			fbIntervalTimeinMilliSeconds=(curentTime+ SMOutboundWebServiceConstants.MATERIAL_SCHEDULE_QUEUE__INTERVAL_IN_MINS* 60* 1000);
			queueBean.setMatStartTimeInGMT(new Date(curentTime).toString());
			//calling client to invoke webservices
			new SMMaterialWebClientService().sminvokeWebRequest(queueBean);
			queueBean.setMatEndTimeinGMT(new Date(System.currentTimeMillis()).toString());


		} 
		finally{
			try {
				LOGGER.debug("Rescheduling queue entry for next run");
				// rescheduling the queue
				statusInfo=SMQueueRescheduleService.rescheduleEntry(new Timestamp(fbIntervalTimeinMilliSeconds));
				LOGGER.debug(setLoggerData());
				queueBean=null;
				LOGGER.debug("#################### MATERIAL OUTBOUND INTEGRATION QUEUE ENDS #################### ");
			} catch (WTPropertyVetoException wtVetoExp) {
				wtVetoExp.printStackTrace();
			} catch (WTException etExcep) {
				etExcep.printStackTrace();
			}
		}
		return statusInfo;
	}
}
