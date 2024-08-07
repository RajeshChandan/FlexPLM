package com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.processor;

import java.util.Map;

import org.apache.log4j.Logger;


import com.google.common.collect.Multimap;
import com.lcs.wc.db.FlexObject;
import com.sportmaster.wc.interfaces.webservices.bean.GetSimpleBOInformationUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.helper.SMLVIntegrationHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.processor.SMLVIntegrationProcessor;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.utill.SMLVIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;

/**
 * SMListValuesResponseDataProcessor.java
 * This class is using to call the methods defined in process class.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMListValuesResponseDataProcessor {
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMListValuesResponseDataProcessor.class);


	/**
	 * constructor.
	 */
	protected SMListValuesResponseDataProcessor() {
	}

	/**
	 * Process data from response object.
	 * @param response - GetSimpleBOInformationUpdatesRequestResponse
	 * @param lvBean - SMLVIntegrationBean
	 * @param dbBOData - Map
	 * @param dbLogEntryData - Map
	 */
	public static void processResponseData(GetSimpleBOInformationUpdatesRequestResponse response,SMLVIntegrationBean lvBean,Map<String,Multimap<String, FlexObject>> dbBOData,Map<String, FlexObject> dbLogEntryData){
		String responseType;
		//validating response object.
		if(SMLVIntegrationHelper.validateResponseObject(response)){
			LOGGER.debug("Response object validated successfully");
			responseType=response.getName().value();
			lvBean.setBusinessObjectType(responseType);

			LOGGER.debug("Processing response object for list values of type : "+responseType);

			//Processing data according to response type.
			if(SMInboundWebserviceConstants.BO_CLASS.equals(responseType)){

				SMLVIntegrationProcessor.processDataToDB(dbBOData.get(SMInboundWebserviceConstants.BO_CLASS.toLowerCase()), dbLogEntryData, response,lvBean);

			}else if(SMInboundWebserviceConstants.BO_SUBCLASS.equals(responseType)){

				SMLVIntegrationProcessor.processDataToDB(dbBOData.get(SMInboundWebserviceConstants.BO_SUBCLASS.toLowerCase()), dbLogEntryData, response,lvBean);

			}else if(SMInboundWebserviceConstants.BO_CATEGORY.equals(responseType)){

				SMLVIntegrationProcessor.processDataToDB(dbBOData.get(SMInboundWebserviceConstants.BO_CATEGORY.toLowerCase()), dbLogEntryData, response,lvBean);

			}else if(SMInboundWebserviceConstants.BO_SUBCATEGORY.equals(responseType)){

				SMLVIntegrationProcessor.processDataToDB(dbBOData.get(SMInboundWebserviceConstants.BO_SUBCATEGORY.toLowerCase()), dbLogEntryData, response,lvBean);

			}else if(SMInboundWebserviceConstants.BO_GOM.equals(responseType)){

				SMLVIntegrationProcessor.processDataToDB(dbBOData.get(SMInboundWebserviceConstants.BO_GOM.toLowerCase()), dbLogEntryData, response,lvBean);

			}
		}else{
			//set logger for response validation fails
			LOGGER.debug("Validation of Response Object Fails");
		}
	}

}
