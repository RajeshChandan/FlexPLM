
/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.carelabel.processor;

import java.sql.SQLException;

import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.sportmaster.wc.interfaces.queue.service.SMProcessingQueueService;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.helper.SMCareLabelHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.helper.SMCareLabelIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util.SMCareLabelConstants;

/**
 * SMCareLabelProcessor.
 * 
 * @author 'true' ITC.
 * @version 'true' 1.0 version number
 * @since March 13, 2018
 */
public class SMCareLabelProcessor {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMCareLabelProcessor.class);


	//constructor.
		public SMCareLabelProcessor(){
			//constructor.
		}


	/**
	 * This method is called from the jsp on click of integration trigger.
	 * @param context
	 * @param inputSelectedMap
	 * @return
	 * @throws SQLException 
	 * @throws WTException 
	 */
	public static void executeIntegration(com.lcs.wc.client.ClientContext context, Map<String, Object> inputSelectedMap) throws WTException, SQLException {


		LOGGER.debug(" ############### START :TRIGGERRED FOR  CARE LABEL  OUTBOUND  INTEGRATION  ###############");


		//Map<String, String> careLabelInputCriteriaMap= new HashMap<String, String>();
		final Class<?>[] careLabelArgTypesx = { Map.class };
		final Object[] careLabelArgValuex = { inputSelectedMap };
		
		//Setting selected values to the bean.
		SMCareLabelIntegrationBean integrationBean=new SMCareLabelIntegrationBean();
		SMCareLabelHelper.setSelectedCriteria(inputSelectedMap, integrationBean);

		//Adding to the queue entry.
		addCareLabelQueue(careLabelArgTypesx, careLabelArgValuex);

		LOGGER.debug(" ############### END : CARE LABEL  OUTBOUND  INTEGRATION  ###############");
	}


	/**
	 * This method starting processing queue for care label.
	 * 
	 * @param careLabelArgTypesx the class.
	 * @param careLabelArgValuex the object.
	 * @throws WTException the exception.
	 * @throws SQLException the exception.
	 */
	public static void addCareLabelQueue(Class<?>[] careLabelArgTypesx, Object[] careLabelArgValuex) throws WTException, SQLException {
		//adding entry for process queue.
		SMProcessingQueueService.addQueueEntry(SMCareLabelConstants.CARELABEL_QUEUE_NAME, careLabelArgTypesx, careLabelArgValuex, "processCareLabelDataFromQueue", "com.sportmaster.wc.interfaces.webservices.outbound.carelabel.processor.SMCareLabelDataProcessor");
	}

}
