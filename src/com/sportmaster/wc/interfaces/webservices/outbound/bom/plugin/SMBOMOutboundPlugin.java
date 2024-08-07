/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.bom.plugin;

import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.queue.service.SMProcessingQueueService;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.util.SMBOMOutboundWebServiceConstants;

import wt.fc.WTObject;
import wt.util.WTException;

/**
 * @author ITC_Infotech.
 *
 */
public class SMBOMOutboundPlugin {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMBOMOutboundPlugin.class);


	/**
	 * protected constructor.
	 */
	protected SMBOMOutboundPlugin(){
		//constructor.
	}

	public static void processBOMOutboundData(WTObject obj){
		LOGGER.debug(" ###############   PLUGIN TRIGGERRED FOR  BOM  OUTBOUND  INTEGRATION  STARTS###############");
		if(obj instanceof FlexBOMPart){
			FlexBOMPart flexbomPart = (FlexBOMPart)obj;

			String bomType = flexbomPart.getFlexType().getFullName();
			LOGGER.debug("bomType = "+bomType);

			//check for the bom type
			if (bomType.contains("smSportsEquipment")) {

				Object[] arrayOfObject = {FormatHelper.getNumericObjectIdFromObject(flexbomPart)};
				Class<?>[] ARG_TYPES = {String.class};
				// adding to processing Queue
				try {
					SMProcessingQueueService.addQueueEntry(SMBOMOutboundWebServiceConstants.BOM_OUTBOUND_INTEGRATION_PROCESSING_QUEUE,
							ARG_TYPES, arrayOfObject, "executeQueue",
							"com.sportmaster.wc.interfaces.webservices.outbound.bom.processor.SMBOMQueueProcessor");
				} catch (WTException | SQLException e) {
					LOGGER.error("", e);
				}

			}
			LOGGER.debug(" ###############   PLUGIN TRIGGERRED FOR  BOM  OUTBOUND  INTEGRATION  ENDS###############");
		}
	}
}
