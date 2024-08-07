/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;
import com.lcs.wc.supplier.LCSSupplier;
import com.sportmaster.wc.interfaces.webservices.bean.SQLException_Exception;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.client.SMSupplierOutboundDataRequestClient;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.util.SMSupplierUtil;

/**
 * @author 'true' ITC_Infotech.
 *
 */
public class SMSupplierFeedbackProcessing {

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSupplierFeedbackProcessing.class);
	/**
	 * Map for Business Supplier Collection.
	 */
	private static Map<String, LCSSupplier> businessSupplierCollection = new HashMap<>();
	/**
	 * Map for Material Supplier Collection.
	 */
	private static Map<String, LCSSupplier> materialSupplierCollection = new HashMap<>();
	/**
	 * Map for Factory Collection.
	 */
	private static Map<String, LCSSupplier> factoryCollection = new HashMap<>();
	
	/**
	 * Constructor.
	 */
	protected SMSupplierFeedbackProcessing(){
		//constructor
	}
	
	/**
	 * Process process Queue Entry.
	 * @throws SQLException_Exception 
	 */
	public static void processSupplierScheduleQueue() throws SQLException_Exception{
		LOGGER.debug("#################  Executing processSupplierScheduleQueue  #################");
		try {
			//Classify supplier Type
			SMSupplierUtil.classifySupplier(SMSupplierUtil.querySupplierLogEntry(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_OBJECTID));
			//Iterate through Supplier Collection
			iterateThroughSupplierCollection();
		} catch (WTException e) {
			LOGGER.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Iterates through Supplier Collection and Set data to Bean. 
	 * @throws SQLException_Exception 
	 * @throws WTException - WTException 
	 */
	public static void iterateThroughSupplierCollection() throws SQLException_Exception{
		LOGGER.debug("Inside iterateThroughSupplierCollection !!!!!!!!!");
		Map<String, String> supplierObjectIDMap = new HashMap<>();
		try{
			for (Map.Entry<String, LCSSupplier> entry : businessSupplierCollection.entrySet()){
				supplierObjectIDMap.put(SMSupplierUtil.getSupplierMasterReferenceFromSupplier(entry.getValue()), entry.getValue().getName());
				SMSupplierOutboundDataRequestClient.supplierDataRequest(entry.getValue(), supplierObjectIDMap);
				supplierObjectIDMap.clear();
			}
			for (Map.Entry<String, LCSSupplier> entry : materialSupplierCollection.entrySet()){
				supplierObjectIDMap.put(SMSupplierUtil.getSupplierMasterReferenceFromSupplier(entry.getValue()), entry.getValue().getName());
				SMSupplierOutboundDataRequestClient.supplierDataRequest(entry.getValue(), supplierObjectIDMap);
				supplierObjectIDMap.clear();
			}
			for (Map.Entry<String, LCSSupplier> entry : factoryCollection.entrySet()){
				supplierObjectIDMap.put(SMSupplierUtil.getSupplierMasterReferenceFromSupplier(entry.getValue()), entry.getValue().getName());
				SMSupplierOutboundDataRequestClient.supplierDataRequest(entry.getValue(), supplierObjectIDMap);
				supplierObjectIDMap.clear();
			}
		}catch(WTException exp){
			LOGGER.error(exp.getLocalizedMessage(), exp);
		}
	}

	

	/**
	 * Get Business Supplier Collection.
	 * @return the businessSupplierCollection
	 */
	public static Map<String, LCSSupplier> getBusinessSupplierCollection() {
		return businessSupplierCollection;
	}

	/**
	 * Set Business Supplier Collection.
	 * @param businessSupplierCollection the businessSupplierCollection to set
	 */
	public static void setBusinessSupplierCollection(
			Map<String, LCSSupplier> businessSupplierCollection) {
		SMSupplierFeedbackProcessing.businessSupplierCollection = businessSupplierCollection;
	}

	/**
	 * Get Material Supplier Collection.
	 * @return the materialSupplierCollection
	 */
	public static Map<String, LCSSupplier> getMaterialSupplierCollection() {
		return materialSupplierCollection;
	}

	/**
	 * Set Material Supplier Collection.
	 * @param materialSupplierCollection the materialSupplierCollection to set
	 */
	public static void setMaterialSupplierCollection(
			Map<String, LCSSupplier> materialSupplierCollection) {
		SMSupplierFeedbackProcessing.materialSupplierCollection = materialSupplierCollection;
	}

	/**
	 * Get Factory Collection.
	 * @return the factoryCollection
	 */
	public static Map<String, LCSSupplier> getFactoryCollection() {
		return factoryCollection;
	}

	/**
	 * Set Factory Collection.
	 * @param factoryCollection the factoryCollection to set
	 */
	public static void setFactoryCollection(Map<String, LCSSupplier> factoryCollection) {
		SMSupplierFeedbackProcessing.factoryCollection = factoryCollection;
	}
	
}
