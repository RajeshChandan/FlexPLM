package com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.processor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


import com.google.common.collect.Multimap;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.bean.GetSimpleBOInformationUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.bean.SimpleBO;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.helper.SMLVIntegrationHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.utill.SMLVIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.utill.SMListValuesUtil;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;

/**
 * SMLVIntegrationProcessor.java
 * This class is using to call the methods defined in process class.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMLVIntegrationProcessor {
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMLVIntegrationProcessor.class);
	
	/**
	 * constructor.
	 */
	protected SMLVIntegrationProcessor() {

	}

	/**
	 * process response data according to response type
	 * @param dbBOData - Multimap
	 * @param dbLogEntryData - Map
	 * @param response - GetSimpleBOInformationUpdatesRequestResponse
	 * @param lvBean - SMLVIntegrationBean
	 */
	public static void processDataToDB(Multimap<String, FlexObject> dbBOData,Map<String, FlexObject> dbLogEntryData,GetSimpleBOInformationUpdatesRequestResponse response,SMLVIntegrationBean lvBean) {
		Map<String, String> attKeyColumnMap=SMListValuesUtil.getKeyColumnMap();
		//Map<String, String> logentryDataValue=null;
		String 	boType=response.getName().value();
		String requestId=String.valueOf(response.getRequestId());
		lvBean.setRequestId(requestId);
		lvBean.setBusinessObjectType(boType);
		LCSLifecycleManaged bo;
		Map<String, String> dataValues;
		String requestType;
		Collection<FlexObject> foList;
		List<SimpleBO> responseBOData=response.getSimpleBO();
		LOGGER.debug("Total Count of List Values Received : "+responseBOData.size());
		
		for(SimpleBO simpleBO:responseBOData){
			if(SMLVIntegrationHelper.validateSimpleBO(simpleBO)){
				LOGGER.debug("Successfully Validated Received SimpleBo Object");
				requestType=simpleBO.getRequestType().value();
				lvBean.setRequestType(requestType);
				if(SMInboundWebserviceConstants.BO_UPDATE.equals(requestType)){
					if(dbBOData.containsKey(simpleBO.getMdmBO())){
						//update
						dataValues=SMLVIntegrationHelper.setDataValues(simpleBO,SMInboundWebserviceConstants.STATUS_ACTIVE_KEY);
						foList=dbBOData.get(simpleBO.getMdmBO());
						for(FlexObject fo: foList){
							bo= SMListValuesUtil.getBoFromFlexObject(fo);
							LOGGER.debug("Updating Business Object for MDM ID :	"+simpleBO.getMdmBO());
							SMListValuesUtil.updateBusinessObject(dataValues, fo, bo, attKeyColumnMap,dbLogEntryData,lvBean);
						}
					}else{	
						//create
						dataValues=SMLVIntegrationHelper.setDataValues(simpleBO,SMInboundWebserviceConstants.STATUS_ACTIVE_KEY);
						LOGGER.debug("Creating Business Object for MDM ID :	"+simpleBO.getMdmBO());
						SMListValuesUtil.createBusinessObject(dataValues,SMLVIntegrationHelper.getboType(boType),attKeyColumnMap, dbLogEntryData,dbBOData,lvBean);
					}
				}else if(SMInboundWebserviceConstants.BO_DELETE.equals(requestType)){
					if(dbBOData.containsKey(simpleBO.getMdmBO())){
						//update status as inactive 
						dataValues=SMLVIntegrationHelper.setDataValues(simpleBO,SMInboundWebserviceConstants.STATUS_INACTIVE_KEY);
						foList=dbBOData.get(simpleBO.getMdmBO());
						for(FlexObject fo: foList){
							bo= SMListValuesUtil.getBoFromFlexObject(fo);
							LOGGER.debug("Deleting Business Object for MDM ID :	"+simpleBO.getMdmBO());
							
							SMListValuesUtil.updateBusinessObject(dataValues, fo, bo, attKeyColumnMap,dbLogEntryData,lvBean);
						}
					}else{
						//set logger data data is missing in flex db	
						lvBean.setLvFailCount(1);
						LOGGER.debug("No Valid Business Object Found In Flex PLM DB For MDM ID :	"+simpleBO.getMdmBO());
						processInvalidDataToLogentry(simpleBO, SMInboundWebserviceConstants.NOT_VAILD_MDMBO, dbLogEntryData,boType,requestId);
					}
				}
			}else{
				//for fail count//set log entry for fail record
				lvBean.setLvFailCount(1);
				LOGGER.debug("Validation Failed for Received SimpleBo Object");
				processInvalidDataToLogentry(simpleBO, SMInboundWebserviceConstants.BO_NOTFOUND, dbLogEntryData,boType,requestId);
			}
		}
	}
	
	/**
	 * process Invalid Data To create a log entry.
	 * @param bo - SimpleBO
	 * @param errorReason - String
	 * @param dbLogEntryData - Map
	 * @param boType - String
	 */
	private static void processInvalidDataToLogentry(SimpleBO bo,String errorReason,Map<String, FlexObject> dbLogEntryData,String boType,String requestId){
		Map<String, String> logentryDataValue;
		if(FormatHelper.hasContent(bo.getMdmBO())){
			logentryDataValue=SMLVIntegrationHelper.getLogEntryDataValues(bo, "", SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_NOT_INTEGRATED, SMInboundWebserviceConstants.LOGENTRY_STATUS_VALUE_NOTSENT, errorReason,boType,requestId);
			SMLVLogEntryDataProcessor.processLogEntry(dbLogEntryData, bo.getMdmBO(), logentryDataValue);
		}
	}
}
