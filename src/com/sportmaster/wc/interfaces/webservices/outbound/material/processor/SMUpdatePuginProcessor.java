package com.sportmaster.wc.interfaces.webservices.outbound.material.processor;

import java.util.Map;

import org.apache.log4j.Logger;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMIntegrationUtill;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialHleper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialPluginHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialXMLHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.util.SMMaterialBean;
import com.sportmaster.wc.interfaces.webservices.outbound.material.util.SMMaterialUtill;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

import wt.util.WTException;

/**
 * SMUpdatePuginProcessor.java
 * This class has methods for update on Material to procees the data.
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMUpdatePuginProcessor {

	/**
	 * MATERIAL_COMMON_KEY.
	 */
	private static final String MATERIAL_COMMON_KEY = "MATERIAL";
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMUpdatePuginProcessor.class);

	/**
	 * Calling process queue to update log entry.
	 * @param materialUpdateData--Map
	 */
	public static void processUpdateProcessingQueue(Map<String, String> materialUpdateData){

		LOGGER.debug("Calling process queue to update logentry");
		LCSMaterial material;
		String mdmId = null;
		String type;
		FlexObject fo;

		LCSLogEntry logEntry;
		Map<String, String> attKeyColumnMap;
		Map<String, String> dataValues = null;
		try {
			LCSMaterialMaster matrlmstr = (LCSMaterialMaster) LCSMaterialQuery.findObjectById(
					"com.lcs.wc.material.LCSMaterialMaster:" + materialUpdateData.get(MATERIAL_COMMON_KEY));
			material = (LCSMaterial) VersionHelper.latestIterationOf(matrlmstr);
			material = (LCSMaterial) VersionHelper.latestIterationOf(material.getMaster());
			type = material.getFlexType().getFullNameDisplay();
			type=type.replace("&", "AND");
			mdmId = (String) material.getValue("smMDMMAT");
			LOGGER.debug("Material MDMID: " + mdmId);

			SMMaterialBean bean = new SMMaterialBean();
			bean.setUtill(new SMMaterialUtill());
			bean.setHelper(new SMMaterialHleper());
			bean.setXmlHelper(new SMMaterialXMLHelper());
			bean.setPluginHelper(new SMMaterialPluginHelper());
			bean.setLogEntryProcessor(new SMLogEntryProcessor());
			
			fo = new SMUpdatePuginProcessor().getLogEntryData(materialUpdateData.get(MATERIAL_COMMON_KEY), type, bean);
			attKeyColumnMap = bean.getUtill().getKeyColumnMap();
			
			if (fo == null) {

				bean.setAttKeyColumnMap(attKeyColumnMap);
				bean.setObjectTye(material.getFlexType().getFullNameDisplay());
				bean.setConstant(new SMOutboundWebServiceConstants());
				bean.setSmIntegrationStatus(SMOutboundWebServiceConstants.UPDATE_PENDING);
				bean.setSmMDMDIV(mdmId);
				bean.setSmObjectID(materialUpdateData.get(MATERIAL_COMMON_KEY));
				bean.setSmObjectName(material.getName());
				
				dataValues = bean.getLogEntryProcessor().getLogEntryDataValues(bean);
				LOGGER.debug("Cretaing Log Entry for Material : " + material.getName());
				// create log entry
				SMIntegrationUtill.createLogEntry(dataValues,
						bean.getPluginHelper().getLogEntryType(bean.getObjectTye(), true, bean.getConstant()),
						bean.getAttKeyColumnMap());
				return;
			}
			
			fo.put(attKeyColumnMap.get("smObjectName"), material.getName());
			fo.put(attKeyColumnMap.get(SMOutboundWebServiceConstants.LOGENTRY_OBJECTID),
					materialUpdateData.get(MATERIAL_COMMON_KEY));
			fo.put(attKeyColumnMap.get("smIntegrationStatus"), SMOutboundWebServiceConstants.UPDATE_PENDING);
			dataValues = bean.getPluginHelper().getLogEntryDataValues(attKeyColumnMap, fo);
			// update logentry
			logEntry = SMIntegrationUtill.getLogEntryFromFlexObject(fo);
			SMIntegrationUtill.updateLogEntry(logEntry, fo, dataValues, attKeyColumnMap);
		
		} catch (WTException e) {
			LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, e);
		}
	}

	/**
	 * Getting log-entry data.
	 * @param objectID --String
	 * @param type --String
	 * @return-- FlexObject
	 * @throws WTException--WTException
	 */
	private FlexObject getLogEntryData(String objectID,String type, SMMaterialBean bean) throws WTException{

		LOGGER.debug("Getting data for the Object type");

		Map<String, FlexObject> dbData = bean.getUtill().getMaterialLogENtryData(
				bean.getPluginHelper().getLogEntryType(type, true, new SMOutboundWebServiceConstants()),
				SMOutboundWebServiceConstants.MATERIAL_LOGENTRY_ATTRIBUTES, objectID,
				SMOutboundWebServiceConstants.MATERIAL_LOGENTRY_OBJECTID_ATTR);
		
		return dbData.get(objectID);

	}
}
