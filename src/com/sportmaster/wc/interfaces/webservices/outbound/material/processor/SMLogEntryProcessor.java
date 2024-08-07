package com.sportmaster.wc.interfaces.webservices.outbound.material.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMIntegrationUtill;
import com.sportmaster.wc.interfaces.webservices.outbound.material.util.SMMaterialBean;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

import wt.util.WTException;
/**
 * SMLogEntryProcessor.java
 * This class is using to call process class method.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMLogEntryProcessor {

	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMLogEntryProcessor.class);
	
	
	/**
	 * processing material log-entry data.
	 * @param logentryData - map
	 * @param materialBean - SMMaterialBean
	 */
	public void processMaterialLogentry(Map<String, FlexObject> logentryData,SMMaterialBean materialBean){
		
		Map<String, String> dataValues = getLogEntryDataValues(materialBean);
		String objectId = FormatHelper.getNumericFromReference(materialBean.getMaterial().getMasterReference());
		if (logentryData.containsKey(objectId)) {
			// update log entry
			FlexObject fo = logentryData.get(objectId);
			LCSLogEntry logEntry;
			try {
				logEntry = SMIntegrationUtill.getLogEntryFromFlexObject(fo);
				LOGGER.debug("Updating Log Entry for Material : " + materialBean.getMaterial().getName());
				SMIntegrationUtill.updateLogEntry(logEntry, fo, dataValues, materialBean.getAttKeyColumnMap());
			} catch (WTException wtLogEntryExp) {
				LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtLogEntryExp);
			}
		} else {
			LOGGER.debug("Cretaing Log Entry for Material : " + materialBean.getMaterial().getName());
			// create log-entry
			SMIntegrationUtill.createLogEntry(
					getLogEntryDataValues(materialBean), materialBean.getPluginHelper()
							.getLogEntryType(materialBean.getObjectTye(), true, materialBean.getConstant()),
					materialBean.getAttKeyColumnMap());
		}
	}
	
	/**
	 * Getting log entry data values.
	 * @param materialBean - SMMaterialBean
	 * @return  map
	 */
	public Map<String, String>  getLogEntryDataValues(SMMaterialBean materialBean){
		
		Map<String, String> dataValues = new HashMap<>();
		for (Map.Entry<String, String> keyEntry : materialBean.getAttKeyColumnMap().entrySet()) {
			dataValues.put(keyEntry.getKey(), materialBean.getPluginHelper().getField(materialBean, keyEntry.getKey()));
		}
		return dataValues;
	}
}
