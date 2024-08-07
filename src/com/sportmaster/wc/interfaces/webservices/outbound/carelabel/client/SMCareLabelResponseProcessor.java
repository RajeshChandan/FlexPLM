/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.carelabel.client;

import java.util.Map;


import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.foundation.LCSLogEntry;
import com.sportmaster.wc.interfaces.webservices.carelabelbean.CareLabelReportRequestResponse;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.helper.SMCareLabelIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.processor.SMCareLabelLogEntryProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.carelabel.util.SMCareLabelConstants;

/**
 * SMCareLabelResponseProcessor.
 * 
 * @author 'true' ITC.
 * @version 'true' 1.0 version number
 * @since Feb 23, 2018
 */
public class SMCareLabelResponseProcessor {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMCareLabelResponseProcessor.class);
	/**
	 * Constructor.
	 */
	protected SMCareLabelResponseProcessor(){
		//constructor.
	}

	/**
	 * Process Response from PLM Gate.
	 * @param careLabelResponseItem - CareLabelReportRequestResponse.
	 */
	public static void processResponse(CareLabelReportRequestResponse careLabelResponseItem, SMCareLabelIntegrationBean integrationBean){
		try{
			LOGGER.debug("*********************    Processing Response received from PLM Gate for CareLabelRequest  *************************");
			//get collection of all care label log entry records in map.
			Map<String, LCSLogEntry> careLabelLogEntryMap = SMCareLabelLogEntryProcessor.queryCareLabelLogEntry(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_REQUEST_ID);
			//check if Map is not null and has elements.
			if(null != careLabelLogEntryMap && careLabelLogEntryMap.size() > 0){
				for(Map.Entry<String, LCSLogEntry> careLabelEntry : careLabelLogEntryMap.entrySet()){
					//check if Request ID is present in log entry and update that log entry.
					checkLogEntyReqID(careLabelResponseItem,
							careLabelLogEntryMap, careLabelEntry, integrationBean);
				}
			}
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage());
			we.printStackTrace();
		}
	}

	
	
	/**
	 * This method update the log entry for that request ID.
	 * @param careLabelResponseItem the CareLabelReportRequestResponse.
	 * @param careLabelLogEntryMap the map.
	 * @param careLabelEntry the Map.
	 * @throws WTException the exception.
	 */
	private static void checkLogEntyReqID(
			CareLabelReportRequestResponse careLabelResponseItem,
			Map<String, LCSLogEntry> careLabelLogEntryMap,
			Map.Entry<String, LCSLogEntry> careLabelEntry, SMCareLabelIntegrationBean integrationBean) throws WTException {
		if(careLabelLogEntryMap.containsKey(careLabelEntry.getKey())){
			//check if Request ID matches with response. where in response is not null.
			if(careLabelResponseItem != null && String.valueOf(careLabelResponseItem.getRequestId()).equalsIgnoreCase(careLabelEntry.getValue().getValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_REQUEST_ID).toString())){
				//Update log entry as per response.
				SMCareLabelLogEntryProcessor.updateLogEntryOnResponse(careLabelResponseItem, careLabelEntry);
			}
			//if response is null because of some reason - connectivity/schema.
			else if(String.valueOf(integrationBean.getCareLabelRequestID()).equalsIgnoreCase(careLabelEntry.getValue().getValue(SMCareLabelConstants.CARE_LABEL_LOG_ENTRY_REQUEST_ID).toString())){
				
				SMCareLabelLogEntryProcessor.updateLogEntryForError(integrationBean, careLabelEntry);
			}
		}
	}
}
