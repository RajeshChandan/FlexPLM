/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor;

import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.helper.SMSubDivisionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.client.SMSupplierOutboundDataRequestClient;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.helper.SMSupplierHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.util.SMSupplierUtil;

/**
 * @author 'true' ITC_Infotech.
 *
 */
public class SMSupplierLogEntryProcessor {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSupplierLogEntryProcessor.class);
	/**
	 * HashMap of LogEntry.
	 */
	private static Map<String, LCSLogEntry> supplierLogEntryCollection;
	/**
	 * HashMap of LogEntry.
	 */
	private static Map<String, String> supplierDataFromProcessingQueue;
	/**
	 * Boolean to flag when log entry is updated.
	 */
	private static boolean logEntryUpdated;
	/**
	 * Constructor.
	 */
	protected SMSupplierLogEntryProcessor(){
		//protected constructor
	}

	/**
	 * Set Log Entry.
	 * @param supplierObjectIdMap - Map<String, String>
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void setLogEntryForSupplier(Map<String, String> supplierObjectIdMap, String requestType,LCSSupplier supplier) throws WTException, WTPropertyVetoException{
		LOGGER.debug("Inside setLogEntryForSupplier");
		LOGGER.debug("REQUEST TYPE >>>>>>>>>>>>>>>>    "+requestType);
		setLogEntryUpdated(false);
		setSupplierDataFromProcessingQueue(supplierObjectIdMap);
		LOGGER.info("Data Collection Size by Processing Queue >>>>>>>>>>>>>>>>>>>>>>>  "+getSupplierDataFromProcessingQueue().size());
		setSupplierLogEntryCollection(SMSupplierUtil.queryOutboundSuplierLogEntry(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_OBJECTID));
		//LCSSupplier supplierObj = obtainSupplierObjectFromMaster(getSupplierDataFromProcessingQueue());
		//String supplierIdA2A2 = obtainSupplierObjectIDFromMaster(getSupplierDataFromProcessingQueue());
		createLogEntryForFirstRun(requestType);
		checkForUpdateLogEntry(getSupplierDataFromProcessingQueue(), requestType,supplier);
		checkForCreateLogEntry(requestType, supplier);
	}

	/**
	 * Creates Log Entry for first run.
	 * @param supplierObjectIdMap - Map<String, String>
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void createLogEntryForFirstRun(String requestType) throws WTException,
	WTPropertyVetoException {
		LOGGER.debug("Inside createLogEntryForFirstRun !!!!!!!!!");
		if(getSupplierLogEntryCollection().size() == 0){
			LOGGER.debug("Log Entry Supplier Collection Size is ZERO !!!!!!!!!!!!!!!!!");
			createLogEntryForSupplier(SMSupplierHelper.obtainSupplierObjectIDFromMaster(getSupplierDataFromProcessingQueue()), SMSupplierUtil.determineSupplierType(SMSupplierHelper.obtainSupplierObjectFromMaster(getSupplierDataFromProcessingQueue())), requestType);
			setLogEntryUpdated(true);
			LOGGER.debug("isLogEntryUpdated  >>>  "+isLogEntryUpdated());
		}
	}

	/**
	 * Check if entry is to be updated.
	 * @param supplierObjectIdMap - Map<String, String>
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void checkForUpdateLogEntry(
			Map<String, String> suppDataFromProcessQueue, String requestType,LCSSupplier supplier) throws WTException, WTPropertyVetoException {
		LOGGER.debug("Inside checkForUpdateLogEntry !!!!");
		for (Map.Entry<String, LCSLogEntry> entry : getSupplierLogEntryCollection().entrySet()) {
			//LOGGER.debug("key=" + entry.getKey() + ", value=" + entry.getValue());
			if(suppDataFromProcessQueue.containsKey(entry.getKey())){
				//check status and update
				updateSupplierLogEntry(entry, requestType, supplier);
				setLogEntryUpdated(true);
				LOGGER.info("logEntryUpdated >>>>>>>>>>>>>>>>>>>>  "+isLogEntryUpdated());
			}
		}
	}

	/**
	 * Check if Log entry record is present or not.
	 * @param supplierObjectIdMap - Map<String, String>
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void checkForCreateLogEntry(
			String requestType, LCSSupplier supplier) throws WTException,
			WTPropertyVetoException {
		LOGGER.debug("Inside checkForCreateLogEntry  !!!!!");
		LOGGER.debug("isLogEntryUpdated  ######  "+isLogEntryUpdated());
		if(!isLogEntryUpdated()){
			createLogEntryForSupplier(SMSupplierHelper.obtainSupplierObjectIDFromMaster(getSupplierDataFromProcessingQueue()), SMSupplierUtil.determineSupplierType(supplier), requestType);				
			setLogEntryUpdated(false);
			LOGGER.debug("isLogEntryUpdated ******  "+isLogEntryUpdated());
		}
	}

	/**
	 * Update Supplier Log Entry.
	 * @param entry - Map<String, LCSLogEntry>
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void updateSupplierLogEntry(
			Map.Entry<String, LCSLogEntry> entry, String requestType, LCSSupplier lcsSupplierObj){
		LOGGER.debug("Inside updateSupplierLogEntry !!!!!!!!");
		String supplType = lcsSupplierObj.getFlexType().getFullNameDisplay();
		LOGGER.debug("Supplier Type >>>>>>>>   *********   >>> "+supplType);
		LOGGER.info("Request TYPE   ###################    >>   "+requestType);
		LOGGER.debug("Request ID *********  >>>>>>>>>    " + SMSupplierOutboundDataRequestClient.getSupplierRequestID());
		try{
			if(SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_FAILED.equals(requestType)){
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_OBJECT_NAME, lcsSupplierObj.getName());
				//if(!SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_CREATE_PENDING.equals(entry.getValue().getValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS))){
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS, SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_CREATE_PENDING);
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_ERROR_REASON, SMSupplierOutboundDataRequestClient.getResponseErrorReason());
				//}
			}else if(SMOutboundWebServiceConstants.SUPPLIER_QUEUE_REQUEST.equals(requestType)){
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_MDMID, lcsSupplierObj.getValue(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY));
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_REQUEST_ID, String.valueOf(SMSupplierOutboundDataRequestClient.getSupplierRequestID()));

			}else if(SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST.equals(requestType)){
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_OBJECT_NAME, lcsSupplierObj.getName());
				//if(!SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_UPDATE_PENDING.equals(entry.getValue().getValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS))){
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS, SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_UPDATE_PENDING);
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_ERROR_REASON, "");
			}else if(SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_SUCCESS.equals(requestType)){
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_MDMID, lcsSupplierObj.getValue(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY));
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_ERROR_REASON, SMSupplierOutboundDataRequestClient.getResponseErrorReason());
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS, SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_CREATE_PROCESSED);
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_REQUEST_ID, String.valueOf(SMSupplierOutboundDataRequestClient.getSupplierRequestID()));

			}else if(SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST_SUCCESS.equals(requestType)){
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_OBJECT_NAME, lcsSupplierObj.getName());
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_ERROR_REASON, SMSupplierOutboundDataRequestClient.getResponseErrorReason());
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS, SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_UPDATE_PROCESSED);
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_MDMID, lcsSupplierObj.getValue(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY));
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_REQUEST_ID, String.valueOf(SMSupplierOutboundDataRequestClient.getSupplierRequestID()));

			}else if(SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST_FAILED.equals(requestType)){
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_OBJECT_NAME, lcsSupplierObj.getName());
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_ERROR_REASON, SMSupplierOutboundDataRequestClient.getResponseErrorReason());
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS, SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_UPDATE_PENDING);
				entry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_REQUEST_ID, String.valueOf(SMSupplierOutboundDataRequestClient.getSupplierRequestID()));
			}
			SMSubDivisionHelper.persistLogEntry(entry.getValue());
		}catch(WTPropertyVetoException wtPVExcp){
			LOGGER.error(wtPVExcp.getLocalizedMessage());
			wtPVExcp.printStackTrace();
		} catch (WTException e1) {
			LOGGER.error(e1.getLocalizedMessage());
			e1.printStackTrace();
		}
	}

	/**
	 * Process data from processing queue.
	 * @param supplierObjectIdMap - Map<String, String>
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws WTException - WTException
	 */
	public static void processDataFromProcessQueue(Map<String, String> supplierObjectIdMap) throws WTPropertyVetoException, WTException{
		//SMSupplierOutboundDataRequestClient.setSupplierRequestID(new SMSupplierHelper().generateOutboundSupplierRequestID());
		LOGGER.debug("Map size ###################   "+supplierObjectIdMap.size());
		String refference="";
		String requestType = "";
		for(Map.Entry<String, String> entry : supplierObjectIdMap.entrySet()){
			if(SMOutboundWebServiceConstants.SUPPLIER_QUEUE_REQUEST.equals(entry.getValue())){
				refference=entry.getKey();
				//setLogEntryForSupplier(supplierObjectIdMap, SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST);
				requestType = SMOutboundWebServiceConstants.SUPPLIER_QUEUE_REQUEST;
			}else if(SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST.equals(entry.getKey())){
				refference=entry.getValue();
				requestType = SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST;
				supplierObjectIdMap.put(entry.getValue(),entry.getKey());
				supplierObjectIdMap.remove(entry.getKey());
			}
		}
		setLogEntryForSupplier(supplierObjectIdMap, requestType,SMSupplierHelper.getSupplierObjectFromMaster(refference));
	}

	/**
	 * Create Log Entry for Supplier.
	 * @param ObjectID - String
	 * @param logEntryOutboundPath - String
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void createLogEntryForSupplier(String objectID, String logEntryOutboundPath, String requestType) throws WTException, WTPropertyVetoException{
		LOGGER.debug("Inside createLogEntryForSupplier !!!!!!!!!");
		com.lcs.wc.flextype.FlexType outboundLogEntryType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(logEntryOutboundPath);
		LCSLogEntry logEntrySupplierOutboundObj = LCSLogEntry.newLCSLogEntry();
		//Setting attribute values
		logEntrySupplierOutboundObj.setFlexType(outboundLogEntryType);
		LCSSupplier supplObj = (LCSSupplier) LCSQuery.findObjectById("com.lcs.wc.supplier.LCSSupplier:"+objectID);
		String sType = supplObj.getFlexType().getFullNameDisplay();
		LOGGER.debug("OBJECT ID PASSED >>>>>>>>>>>>>>>>>>>>   "+objectID);
		LOGGER.debug("Object ID of Supplier >>>>>>>>>>>>>>>   "+FormatHelper.getNumericObjectIdFromObject(supplObj));
		LOGGER.debug("REQUEST TYPE  >>>>>>>>>>>>    "+requestType);
		//LOGGER.debug("SUPPLIER NAME  >>>>>>>>>>>>>>>>>>>>>>>   "+supplObj.getName());

		if(SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST.equals(requestType) && !sType.equalsIgnoreCase(SMOutboundWebServiceConstants.FACTORY)){
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_OBJECT_NAME, supplObj.getName());
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_OBJECTID, SMSupplierHelper.getMasterRefFromMap(getSupplierDataFromProcessingQueue()));
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_MDMID, supplObj.getValue(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY));
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS, SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_UPDATE_PENDING);
			//logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_REQUEST_ID, String.valueOf(SMSupplierOutboundDataRequestClient.getSupplierRequestID()));
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_ERROR_REASON, "");
		}
		else{
			if(!sType.equalsIgnoreCase(SMOutboundWebServiceConstants.FACTORY)){
				logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_REQUEST_ID, String.valueOf(SMSupplierOutboundDataRequestClient.getSupplierRequestID()));
				logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_OBJECT_NAME, supplObj.getName());
				logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_OBJECTID, SMSupplierHelper.getMasterRefFromMap(getSupplierDataFromProcessingQueue()));
				//logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_ERROR_REASON, "");
				setLogEntryObjectOnCreate(requestType,
						logEntrySupplierOutboundObj);
			}  
			else if(sType.equalsIgnoreCase(SMOutboundWebServiceConstants.FACTORY)){
				setLogEntryObjectOnCreateForFactory(requestType,
						logEntrySupplierOutboundObj, supplObj);
			}
			//SMSubDivisionHelper.persistLogEntry(logEntrySupplierOutboundObj);
		}
		// Moved the above persist logic here, to fix save on Update_Pending (SUPPLIER_UPDATE_REQUEST) scenario
		SMSubDivisionHelper.persistLogEntry(logEntrySupplierOutboundObj);
	}

	/**
	 * @param requestType
	 * @param logEntrySupplierOutboundObj
	 */
	public static void setLogEntryObjectOnCreate(String requestType,
			LCSLogEntry logEntrySupplierOutboundObj) {
		if(SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_FAILED.equals(requestType)){
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS, SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_CREATE_PENDING);
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_ERROR_REASON, SMSupplierOutboundDataRequestClient.getResponseErrorReason());
		}else if(SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_SUCCESS.equals(requestType)){
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS, SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_CREATE_PROCESSED);
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_ERROR_REASON, SMSupplierOutboundDataRequestClient.getResponseErrorReason());
			if(!SMSupplierOutboundDataRequestClient.getResponseMDMID().equals(SMOutboundWebServiceConstants.SUPPLIER_DUMMY_MDM_ID_ON_CREATE) && SMSupplierOutboundDataRequestClient.isResponseIntegrationStatus()){
				logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_MDMID, SMSupplierOutboundDataRequestClient.getResponseMDMID());
			}
		}
	}
	
	/**
	 * @param requestType
	 * @param logEntrySupplierOutboundObj
	 * @throws WTException 
	 * @throws WTPropertyVetoException 
	 */
	public static void setLogEntryObjectOnCreateForFactory(String requestType,
			LCSLogEntry logEntrySupplierOutboundObj, LCSSupplier suppObj) throws WTPropertyVetoException, WTException {
		LOGGER.debug("REQ TYPE   *********   "+requestType);
		if(SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_FAILED.equals(requestType)){
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_REQUEST_ID, String.valueOf(SMSupplierOutboundDataRequestClient.getSupplierRequestID()));
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_OBJECT_NAME, suppObj.getName());
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_OBJECTID, SMSupplierHelper.getMasterRefFromMap(getSupplierDataFromProcessingQueue()));
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS, SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_CREATE_PENDING);
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_ERROR_REASON, SMSupplierOutboundDataRequestClient.getResponseErrorReason());
		}else if(SMOutboundWebServiceConstants.SUPPLIER_CREATE_REQUEST_SUCCESS.equals(requestType)){
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_REQUEST_ID, String.valueOf(SMSupplierOutboundDataRequestClient.getSupplierRequestID()));
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_OBJECT_NAME, suppObj.getName());
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_OBJECTID, SMSupplierHelper.getMasterRefFromMap(getSupplierDataFromProcessingQueue()));
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS, SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_CREATE_PROCESSED);
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_ERROR_REASON, SMSupplierOutboundDataRequestClient.getResponseErrorReason());
			if(!SMSupplierOutboundDataRequestClient.getResponseMDMID().equals(SMOutboundWebServiceConstants.SUPPLIER_DUMMY_MDM_ID_ON_CREATE) && SMSupplierOutboundDataRequestClient.isResponseIntegrationStatus()){
				logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_MDMID, SMSupplierOutboundDataRequestClient.getResponseMDMID());
			}
		}if(SMOutboundWebServiceConstants.SUPPLIER_UPDATE_REQUEST.equals(requestType)){
			LOGGER.debug("here.....................");
			//logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_REQUEST_ID, String.valueOf(SMSupplierOutboundDataRequestClient.getSupplierRequestID()));
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_OBJECT_NAME, suppObj.getName());
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_MDMID, suppObj.getValue(SMOutboundWebServiceConstants.SUPPLIER_MDM_ID_KEY));
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_OBJECTID, SMSupplierHelper.getMasterRefFromMap(getSupplierDataFromProcessingQueue()));
			logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS, SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_UPDATE_PENDING);
			//logEntrySupplierOutboundObj.setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_ERROR_REASON, SMSupplierOutboundDataRequestClient.getResponseErrorReason());
		}
	}

	/**
	 * Gets the supplier Log entry collection.
	 * @return the supplierLogEntryCollection
	 */
	public static Map<String, LCSLogEntry> getSupplierLogEntryCollection() {
		return supplierLogEntryCollection;
	}

	/**
	 * Sets the Supplier Log Entry Collection.
	 * @param map the supplierLogEntryCollection to set
	 */
	public static void setSupplierLogEntryCollection(
			Map<String, LCSLogEntry> map) {
		supplierLogEntryCollection = map;
	}

	/**
	 * Get the flag.
	 * @return the logEntryUpdated
	 */
	public static boolean isLogEntryUpdated() {
		return logEntryUpdated;
	}

	/**
	 * Sets the flag.
	 * @param logEntryUpdated the logEntryUpdated to set
	 */
	public static void setLogEntryUpdated(boolean logEntryUpdated) {
		SMSupplierLogEntryProcessor.logEntryUpdated = logEntryUpdated;
	}

	/**
	 * Get Supplier data from Process Queue.
	 * @return the supplierDataFromProcessingQueue
	 */
	public static Map<String, String> getSupplierDataFromProcessingQueue() {
		return supplierDataFromProcessingQueue;
	}

	/**
	 * Set supplier data from Process Queue to Map.
	 * @param supplierDataFromProcessingQueue the supplierDataFromProcessingQueue to set
	 */
	public static void setSupplierDataFromProcessingQueue(
			Map<String, String> supplierDataFromProcessingQueue) {
		SMSupplierLogEntryProcessor.supplierDataFromProcessingQueue = supplierDataFromProcessingQueue;
	}


}
