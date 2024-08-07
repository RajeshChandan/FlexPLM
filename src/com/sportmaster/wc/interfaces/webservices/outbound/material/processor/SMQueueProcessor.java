package com.sportmaster.wc.interfaces.webservices.outbound.material.processor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSLogEntry;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMIntegrationUtill;

/**
 * SMQueueProcessor.java
 * This class is using to processing queue.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMQueueProcessor {

	
	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMQueueProcessor.class);
	
	/**
	 * Formating log-entry data.
	 * @param dbData --Map
	 * @return --map
	 * @throws WTException --WTException
	 */
	public Map<String, List<LCSLogEntry>> formatLogEntryData(Map<String, FlexObject> dbData) throws WTException{
		
         LOGGER.debug("Formating logentry data");
		LCSLogEntry logEntry;
		Map<String, List<LCSLogEntry>> logData= new HashMap<>();
		List<LCSLogEntry> trimList= new ArrayList<>();
		List<LCSLogEntry> frabicList= new ArrayList<>();
		List<LCSLogEntry> decorationList= new ArrayList<>();
		List<LCSLogEntry> productPakgList= new ArrayList<>();
		List<LCSLogEntry> shipingPakingList= new ArrayList<>();
		List<LCSLogEntry> allTypeList= new ArrayList<>();
		for(Map.Entry<String, FlexObject> entry:dbData.entrySet()){
			logEntry=SMIntegrationUtill.getLogEntryFromFlexObject(entry.getValue());
			if(logEntry.getFlexType().getFullName().contains("Trims")){
			trimList.add(logEntry);	
			}else if(logEntry.getFlexType().getFullName().contains("Fabric")){
				frabicList.add(logEntry);	
			}else if(logEntry.getFlexType().getFullName().contains("Decoration")){
				decorationList.add(logEntry);	
			}else if(logEntry.getFlexType().getFullName().contains("ProductPackaging")){
				productPakgList.add(logEntry);	
			}else if(logEntry.getFlexType().getFullName().contains("ShippingPackaging")){
				shipingPakingList.add(logEntry);	
			}else {
				allTypeList.add(logEntry);
			}
		}
		//putting trims material data
		logData.put("TRIMS", trimList);
		//putting fabric material data
		logData.put("FABRIC", frabicList);
		//putting decoration material data
		logData.put("DECORATION", decorationList);
		//putting product packaging material data
		logData.put("PRODUCTPAKAGING", productPakgList);
		//putting shipping packing material data
		logData.put("SHIPPINGPACKING", shipingPakingList);
		logData.put("ALLTYPE", allTypeList);
		return logData;
		
	}
}
