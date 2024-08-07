package com.sportmaster.wc.interfaces.webservices.outbound.material.plugin;

import java.util.Map;

import org.apache.log4j.Logger;

import wt.fc.WTObject;
import wt.util.WTException;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialHleper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialPluginHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.helper.SMMaterialXMLHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.material.processor.SMLogEntryProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.material.util.SMMaterialBean;
import com.sportmaster.wc.interfaces.webservices.outbound.material.util.SMMaterialUtill;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

/**
 * SMMaterialDeletePlugin.java
 * This class is using to call plugins for material.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMMaterialDeletePlugin {
	
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMMaterialDeletePlugin.class);
	
	/**
	 * constructor.
	 */
	protected SMMaterialDeletePlugin() {
		
	}


	/**
	 * this is the plug-in method called on DELETE event.
	 * ON delete OF MATERIAL, it changed the status value to "OBJECT_MISSING" on respective log-entry. 
	 * @param obj - WTObject
	 */
	public static void onDeleteMaterial(WTObject obj){
		LOGGER.debug("################# MATERIAL DELETE PLUGIN STARTS #################");
		LCSMaterial material;
		//checking material instance
		if(obj instanceof LCSMaterial){
			material=(LCSMaterial) obj;
			//getting material type
			/*
			 * //checking eligibility if(!SMMaterialPlugins.checkEligibility(type)){ return
			 * ; }
			 */
			try {
				//process for create task
				LOGGER.debug("executing delete operation");
				SMMaterialBean bean = new SMMaterialBean();
				
				String mdmId = String.valueOf( material.getValue("smMDMMAT"));
				
				bean.setUtill(new SMMaterialUtill());
				bean.setHelper(new SMMaterialHleper());
				bean.setXmlHelper(new SMMaterialXMLHelper());
				bean.setPluginHelper(new SMMaterialPluginHelper());
				bean.setLogEntryProcessor(new SMLogEntryProcessor());
				
				// setting bean data
				bean.setSmMDMDIV(mdmId);
				bean.setMaterial(material);
				bean.setForScheduleQueue(false);
				bean.setSmObjectName(bean.getMaterial().getName());
				bean.setConstant(new SMOutboundWebServiceConstants());
				bean.setObjectTye(material.getFlexType().getFullNameDisplay());
				bean.setSmIntegrationStatus(SMOutboundWebServiceConstants.OBJECT_MISSING);
				bean.setSmObjectID(FormatHelper.getNumericFromReference(material.getMasterReference()));
				
				// getting log entry data
				Map<String, FlexObject> flexLogEntryData = bean.getUtill().getMaterialLogENtryData(
						bean.getPluginHelper().getLogEntryType(bean.getObjectTye(), true, bean.getConstant()),
						SMOutboundWebServiceConstants.MATERIAL_LOGENTRY_ATTRIBUTES, bean.getSmObjectID(),
						SMOutboundWebServiceConstants.MATERIAL_LOGENTRY_OBJECTID_ATTR);
				bean.setAttKeyColumnMap(bean.getUtill().getKeyColumnMap());
				LOGGER.debug("Setting status value " + SMOutboundWebServiceConstants.OBJECT_MISSING
						+ " in log entry for delete");
				bean.getLogEntryProcessor().processMaterialLogentry(flexLogEntryData, bean);
				LOGGER.debug("################# MATERIAL DELETE PLUGIN ENDS #################");
				return;
			} catch (WTException wtExp) {
				LOGGER.error(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtExp);
			}
		}
		LOGGER.debug("################# MATERIAL DELETE PLUGIN ENDS #################");
	}
}
