package com.sportmaster.wc.interfaces.webservices.outbound.material.plugin;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.queue.service.SMProcessingQueueService;
import com.sportmaster.wc.interfaces.webservices.outbound.helper.SMOutboundIntegrationHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialHleper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialPluginHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialXMLHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.processor.SMLogEntryProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.material.processor.SMMaterialPluginProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.material.util.SMMaterialBean;
import com.sportmaster.wc.interfaces.webservices.outbound.material.util.SMMaterialUtill;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

/**
 * SMMaterialPlugins.java
 * This class is using to call plugins for material.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMMaterialPlugins {

	/**
	 * COMMON_END_LOGGER_LITERAL.
	 */
	private static final String COMMON_END_LOGGER_LITERAL = "################# MATERIAL PLUGIN ENDS #################";
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMMaterialPlugins.class);



	/**
	 * constructor.
	 */
	protected SMMaterialPlugins() {

	}



	/**
	 * Call material Plug-ins.
	 * @param obj --WTObject
	 */
	public static void onPersistMaterial(WTObject obj){

		LOGGER.debug("################# MATERIAL PLUGIN STARTS #################");
		LCSMaterial material;
		LCSMaterial materialB;
		String statusVlaue;
		String stausValueB;
		String mdmid;
		String type;
		Map<String, String> queueData= new HashMap<>();
		try{
			if(obj instanceof LCSMaterial){
				material=(LCSMaterial) obj;
				//Getting types
				type=material.getFlexType().getFullNameDisplay();

				LOGGER.debug("Material Type: "+type);
				
				/*
				 * commented for 3.9.0. build, as material integration is enabled for all
				 * material types. //checking the eligilibity. if(!checkEligibility(type)){
				 * return ; }
				 */

				mdmid = (String) material.getValue("smMDMMAT");
				if (!FormatHelper.hasContent(mdmid)) {
					// process for create task
					LOGGER.debug("Calling Material Plugins for the Creation of material");

					SMMaterialBean bean = new SMMaterialBean();
					
					bean.setUtill(new SMMaterialUtill());
					bean.setHelper(new SMMaterialHleper());
					bean.setXmlHelper(new SMMaterialXMLHelper());
					bean.setPluginHelper(new SMMaterialPluginHelper());
					bean.setLogEntryProcessor(new SMLogEntryProcessor());
					
					bean.setForScheduleQueue(false);
					bean.setObjectTye(material.getFlexType().getFullNameDisplay());
					// setting material
					bean.setMaterial(material);
					// setting status
					bean.setSmIntegrationStatus(SMOutboundWebServiceConstants.CREATE_PENDING);
					// creating request
					new SMMaterialPluginProcessor().creteRequest(bean);
					LOGGER.debug(COMMON_END_LOGGER_LITERAL);
					return;

				}
				materialB = (LCSMaterial) VersionHelper.predecessorOf(material);
				// checking PREDECESSOR is available or not
				if (materialB == null) {
					LOGGER.debug("NO PREDECESSOR FOUND FOR MATERIAL :" + material.getName());
					LOGGER.debug(COMMON_END_LOGGER_LITERAL);
					// exit if null
					return;
				}
				// setting material status
				statusVlaue = (String) material.getValue(SMOutboundWebServiceConstants.MATERIAL_STATUS);
				stausValueB = (String) materialB.getValue(SMOutboundWebServiceConstants.MATERIAL_STATUS);
				statusVlaue = String.valueOf(SMOutboundIntegrationHelper.getAttributeValues(material, null,
						SMOutboundWebServiceConstants.MATERIAL_STATUS, statusVlaue));
				stausValueB = String.valueOf(SMOutboundIntegrationHelper.getAttributeValues(materialB, null,
						SMOutboundWebServiceConstants.MATERIAL_STATUS, stausValueB));
				
				// checking the update.
				if (checkForUpdate(statusVlaue, stausValueB)) {
					// call for update processing
					LOGGER.debug("Calling Material Plugins for the Update of material Status:");
					queueData.put("MATERIAL", FormatHelper.getNumericFromReference(material.getMasterReference()));
					final Class<?>[] argTypesx = { Map.class };
					final Object[] argValuesx = { queueData };
					// adding to processing Queue
					SMProcessingQueueService.addQueueEntry(SMOutboundWebServiceConstants.MATERIAL_PROCESSING_QUEUE_NAME,
							argTypesx, argValuesx, "processUpdateProcessingQueue",
							"com.sportmaster.wc.interfaces.webservices.outbound.material.processor.SMUpdatePuginProcessor");
					LOGGER.debug(COMMON_END_LOGGER_LITERAL);
					return;
				} else {
					LOGGER.debug("NOT READY TO TRIGGER UPDATE QUEUE");
				}

			}
		} catch (LCSException lcsExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, lcsExp);
		} catch (WTException wtExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtExp);
		} catch (WTPropertyVetoException wtProExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtProExp);
		} catch (SQLException esqlExp) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, esqlExp);
		}
		LOGGER.debug(COMMON_END_LOGGER_LITERAL);
	}
	/**
	 * Checking Update crieteria for material.
	 * @param statusVlaue
	 * @param statusValueB
	 * @return
	 */
	private static boolean checkForUpdate(String statusVlaue, String statusValueB){
		
		// checking the material status
		if (SMOutboundWebServiceConstants.MATERIAL_INDEVELOPMENT.equalsIgnoreCase(statusValueB)
				&& SMOutboundWebServiceConstants.MATERIAL_ACTIVE.equalsIgnoreCase(statusVlaue)) {
			return true;
		} else if (SMOutboundWebServiceConstants.MATERIAL_ACTIVE.equalsIgnoreCase(statusValueB)
				&& SMOutboundWebServiceConstants.MATERIAL_DROPPED.equalsIgnoreCase(statusVlaue)) {
			return true;

		} else if (SMOutboundWebServiceConstants.MATERIAL_INDEVELOPMENT.equalsIgnoreCase(statusValueB)
				&& SMOutboundWebServiceConstants.MATERIAL_DROPPED.equalsIgnoreCase(statusVlaue)) {
			return true;

		}
		return false;
	}
	/**
	 * Checking eligibility Material for integration.
	 * @param type - String
	 * @return
	 */
	public static boolean checkEligibility(String type){
		List<String> materialRequestType= FormatHelper.commaSeparatedListToList(SMOutboundWebServiceConstants.MATERIAL_REQUEST_TYPES);
		for(String reqType:materialRequestType){
			//comparing the material types
			if(new SMMaterialPluginHelper().compareType(type, reqType)){
				return true;
			}
		}
		return false;
	}
}
