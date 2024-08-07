package com.sportmaster.wc.interfaces.webservices.outbound.material.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialHleper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialPluginHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialXMLHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.processor.SMLogEntryProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.material.processor.SMMaterialPluginProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.material.processor.SMQueueProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.material.util.SMMaterialBean;
import com.sportmaster.wc.interfaces.webservices.outbound.material.util.SMMaterialQueueBean;
import com.sportmaster.wc.interfaces.webservices.outbound.material.util.SMMaterialUtill;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;


/**
 * SMMaterialWebClientService.java
 * This class has methods to invoke the service for Material.
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMMaterialWebClientService {


	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMMaterialWebClientService.class);


	/**
	 *  to invoke the web service request for Feedback.
	 *  @param bean the bean
	 */
	public void sminvokeWebRequest(SMMaterialQueueBean bean ){

		LOGGER.debug("invoking the request");
		//getting the order
		List<String> materialOrder= FormatHelper.commaSeparatedListToList(SMOutboundWebServiceConstants.MATERIAL_QUEUE_ORDER);

		try {
			Map<String, List<LCSLogEntry>> logData = new SMQueueProcessor().formatLogEntryData(new SMMaterialUtill()
					.getMaterialLogENtryData(SMOutboundWebServiceConstants.MATERIAL_LOGENTRY_TYPEES,
							SMOutboundWebServiceConstants.MATERIAL_LOGENTRY_ATTRIBUTES,
							SMOutboundWebServiceConstants.UPDATE_PENDING,
							SMOutboundWebServiceConstants.MATERIAL_LOGENTRY_STATUS_ATTR));
			for(String type:materialOrder){
				bean.setTotalObjCount(logData.get(type).size());
				for(LCSLogEntry logEntry:logData.get(type)){
					// invoking the request
					invokeQueueRequest(logEntry,bean);
				}
			}
		} catch (WTException wtExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtExp);
		}
	}
	/**
	 * invoking the Queue.
	 * @param logEntry --LCSLogEntry
	 */
	private void invokeQueueRequest(LCSLogEntry logEntry,SMMaterialQueueBean queueBean){
		LCSMaterial material;
		String type;
		SMMaterialBean bean;
		try {
			//getting the materil form the log entry
			LCSMaterialMaster matrlmstr=(LCSMaterialMaster) LCSMaterialQuery.findObjectById("com.lcs.wc.material.LCSMaterialMaster:"+logEntry.getValue("smObjectID"));
			//getting material latest iteration
			material= (LCSMaterial) VersionHelper.latestIterationOf(matrlmstr);
			
			queueBean.setMaterialBean(new SMMaterialBean());
			
			//creating MaterialBean object
			bean=queueBean.getMaterialBean();
			
			bean.setUtill(new SMMaterialUtill());
			bean.setHelper(new SMMaterialHleper());
			bean.setXmlHelper(new SMMaterialXMLHelper());
			bean.setPluginHelper(new SMMaterialPluginHelper());
			bean.setLogEntryProcessor(new SMLogEntryProcessor());
			
			// getting the material types
			type=material.getFlexType().getFullNameDisplay();
			LOGGER.debug("Material Type:"+type);
			//setting values on MaterialBean object
			bean.setForScheduleQueue(true);
			bean.setObjectTye(material.getFlexType().getFullNameDisplay());
			bean.setSmIntegrationStatus(SMOutboundWebServiceConstants.UPDATE_PENDING);
			bean.setMaterial(material);
			// creating the request
			new SMMaterialPluginProcessor().creteRequest(bean);
			//setting count on queue bean
			queueBean.setTotalFailCount(bean.getTotalFailCount());
			queueBean.setTotalProcessedCount(bean.getTotalProcessedCount());
			//setting MaterialBean to null
			queueBean.setMaterialBean(null);
		} catch (WTException wtExp) {
			queueBean.setTotalFailCount(1);
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL,wtExp);
		}

	}



}
