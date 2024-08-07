package com.sportmaster.wc.interfaces.webservices.outbound.supplier.helper;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.log4j.Logger;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.supplier.LCSSupplierLogic;
import com.lcs.wc.supplier.LCSSupplierMaster;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.SortHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

import wt.util.WTException;

/**
 *
 *  @author 'true' ITC_Infotech.
 *
 */
public class SMSupplierHelper {

	private static final String ERROR_OCCURED = "ERROR OCCURED :-";
	private static final String FLEX_TYPE_ID_PATH = "flexTypeIdPath";
	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSupplierHelper.class);
	/**
	 * Request ID.
	 */
	public static final int  SUPPLIER_REQUEST_ID=Integer.parseInt(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_REQUEST_ID_CONSTANT);

	/**
	 * Return unique request id.
	 * @return int - supplierRequestID
	 * @throws WTException - WTException
	 */
	@SuppressWarnings("unchecked")
	public int  generateOutboundSupplierRequestID(){
		int supplierRequestID=SUPPLIER_REQUEST_ID;
		try{
			LOGGER.debug("Inside generateOutboundSupplierRequestID  !!!!!!!!!!");
			String supplierLogEntry="LCSLogEntry";
			FlexType supplierLogType = FlexTypeCache.getFlexTypeFromPath(SMOutboundWebServiceConstants.LOG_ENRTY_BUSINESS_SUPPLIER_OUTBOUND_PATH);
			FlexTypeAttribute suppAttr = supplierLogType.getAttribute(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENRTY_REQUEST_ID);
			SearchResults result = null;
			PreparedQueryStatement stmt = new PreparedQueryStatement();//Creating Statement.

			//stmt.appendFromTable(supplierLogEntry);
			stmt.appendSelectColumn(new QueryColumn(supplierLogEntry, "idA2A2"));
			stmt.appendSelectColumn(supplierLogEntry, suppAttr.getColumnName());
			//add tables
			stmt.appendFromTable(supplierLogEntry);

			stmt.appendCriteria(new Criteria(supplierLogEntry, FLEX_TYPE_ID_PATH, supplierLogType.getIdPath(),Criteria.EQUALS));
			stmt.appendOrIfNeeded();
			supplierLogType = FlexTypeCache.getFlexTypeFromPath(SMOutboundWebServiceConstants.LOG_ENRTY_MATERIAL_SUPPLIER_OUTBOUND_PATH);
			stmt.appendCriteria(new Criteria(supplierLogEntry, FLEX_TYPE_ID_PATH, supplierLogType.getIdPath(),Criteria.EQUALS));

			stmt.appendOrIfNeeded();
			supplierLogType = FlexTypeCache.getFlexTypeFromPath(SMOutboundWebServiceConstants.LOG_ENRTY_FACTORY_OUTBOUND_PATH);
			stmt.appendCriteria(new Criteria(supplierLogEntry, FLEX_TYPE_ID_PATH, supplierLogType.getIdPath(),Criteria.EQUALS));


			result=LCSQuery.runDirectQuery(stmt);
			List<FlexObject> data=result.getResults();
			int reqId;
			data=(List<FlexObject>) SortHelper.sortFlexObjects(data, supplierLogEntry+"."+suppAttr.getColumnName());

			if(!data.isEmpty()){
				reqId=data.get(data.size()-1).getInt( supplierLogEntry+"."+suppAttr.getColumnName());
				if(reqId==0){
					supplierRequestID=supplierRequestID+1;
				}else{
					supplierRequestID =reqId+1;
				}
				//supplierRequestID =reqId+1;
				LOGGER.debug("REQUEST ID >>>>>>>>>>>>>    "+supplierRequestID);
				return supplierRequestID;
			}
		}catch(WTException excpt){
			LOGGER.error(ERROR_OCCURED, excpt);
		}
		LOGGER.debug("REQ ID >>>> ####  "+supplierRequestID);
		return supplierRequestID+1;
	}

	/**
	 * Set Value to Supplier bean.
	 * @param obj - Object
	 * @param supplierBeanAttr - String
	 * @param suppPLMValue - String
	 */
	public static void setSupplierBean(Object obj, String supplierBeanAttr, String suppPLMValue){
		LOGGER.debug("Enter set method !!!!!!!!!!");
		//Setting attributes for all Supplier Types
		Class<?> supplierBeanClass = obj.getClass();
		if(supplierBeanClass != null && FormatHelper.hasContent(suppPLMValue)) {
			try {
				Field supplierBeanField = supplierBeanClass.getDeclaredField(supplierBeanAttr);
				supplierBeanField.setAccessible(true);
				supplierBeanField.set(obj, suppPLMValue);

			} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException nsfe) {
				LOGGER.error(ERROR_OCCURED, nsfe);
			}
		}
	}

	/**
	 * Persists supplier object.
	 * @param supplierObj - LCSSupplier
	 */
	@SuppressWarnings("static-access")
	public static void persistSupplier(LCSSupplier supplierObj){
		LOGGER.debug("Persisting Supplier Object !!!!!!!!!!!");
		LCSSupplierLogic supplierLogic = new LCSSupplierLogic();
		try {
			//Save Supplier object
			supplierLogic.persist(supplierObj, true);
			LOGGER.debug("Successfully persisted Supplier Object >>>>>>>>>>>>   "+supplierObj.getName());
		} catch (WTException e) {
			LOGGER.error("ERROR in persisting Supplier Object !!!!!!  ", e);
		}
	}

	/**
	 * Convert Date Object to XMLGregorianCalendar.
	 * @param date - Date
	 * @return xmlGregorianCalendar - XMLGregorianCalendar
	 * @throws DatatypeConfigurationException - DatatypeConfigurationException
	 */
	public static XMLGregorianCalendar getXMLGregorianCalendarFormat(Date date) throws DatatypeConfigurationException{
		LOGGER.debug("Inside getXMLGregorianCalendarFormat !!!!!!!!!!");
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(date);
		//Converting Date to XMLGregorianCalendar
		//XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
	}

	/**
	 * Obtain Supplier Object From Master.
	 * @param suppDataFromProcessQueue - Map<String, String>
	 * @return suppObj - LCSSupplier
	 */
	public static LCSSupplier obtainSupplierObjectFromMaster(Map<String, String> suppDataFromProcessQueue){
		LOGGER.debug("Inside obtainSupplierObjectFromMaster !!!!!!!!");
		LOGGER.debug("SIZEEEEEEEEEEE    >>>>>>>>   "+suppDataFromProcessQueue.size());
		LCSSupplier suppObject = new LCSSupplier();
		try{
			//iterating through map
			for(Map.Entry<String, String> suppEntry : suppDataFromProcessQueue.entrySet()){
				LCSSupplierMaster suppMaster=(LCSSupplierMaster) LCSQuery.findObjectById("OR:com.lcs.wc.supplier.LCSSupplierMaster:"+suppEntry.getKey());
				LOGGER.debug("LCSSUPPLIER MASTER >>>>>>>>>>>>>>>>>>>>>>>>>>>>  "+suppMaster);
				//getting latest iteration
				suppObject = (LCSSupplier) VersionHelper.latestIterationOf(suppMaster);
				LOGGER.debug("SUPPLIER KEY >>>>>>>>>>>>>>>>  "+suppEntry.getKey());
				//Supplier Name
				LOGGER.debug("SUPPLIER NAME >>>>>>>>>>>>>>>  "+suppObject.getName());
			}
			//return supplier object
			return suppObject;
		}catch(WTException exp){
			LOGGER.error(ERROR_OCCURED, exp);
		}
		return null; //check

	}

	/**
	 * Get Supplier Object from master.
	 * @param supplierMasterReference - String
	 * @return supplierObject - LCSSupplier
	 */
	public static LCSSupplier getSupplierObjectFromMaster(String supplierMasterReference){
		LOGGER.debug("Inside getSupplierObjectFromMaster !!!!!!!!");
		LCSSupplier supplierObject = null;
		try{
			LCSSupplierMaster supplMaster=(LCSSupplierMaster) LCSQuery.findObjectById("OR:com.lcs.wc.supplier.LCSSupplierMaster:"+supplierMasterReference);
			LOGGER.debug("LCSSUPPLIER MASTER >>>>>>>>>>>>>>>>>>>>>>>>>>>>  "+supplMaster);
			supplierObject = (LCSSupplier) VersionHelper.latestIterationOf(supplMaster);
			LOGGER.debug("SUPPLIER MASTER REF >>>>>>>>>>>>>>>>  "+supplierMasterReference);
			LOGGER.debug("SUPPLIER NAME >>>>>>>>>>>>>>>  "+supplierObject.getName());
			return supplierObject;
		}catch(WTException exp){
			LOGGER.error(ERROR_OCCURED, exp);
		}
		return null;

	}

	/**
	 * Obtain Supplier ID from Master.
	 * @param suppDataFromProcessQueue - Map<String, String>
	 * @return masterReference - String
	 */
	public static String obtainSupplierObjectIDFromMaster(Map<String, String> suppDataFromProcessQueue){
		LOGGER.debug("Inside obtainSupplierObjectIDFromMaster !!!!!!!!!");
		//Supplier Object From Master
		LCSSupplier supplier = obtainSupplierObjectFromMaster(suppDataFromProcessQueue);
		//return supplier object
		return FormatHelper.getNumericObjectIdFromObject(supplier);
	}

	/**
	 * Get Master Reference from Map.
	 * @param suppDataFromProcessQueue - Map<String, String>
	 * @return masterReference - String
	 */
	public static String getMasterRefFromMap(Map<String, String> suppDataFromProcessQueue){
		LOGGER.debug("Inside getMasterRefFromMap !!!!!!");
		String masterReference = "";
		//Iterating through Map
		for(Map.Entry<String, String> suppEntry : suppDataFromProcessQueue.entrySet()){
			//Master Reference From Map
			masterReference = suppEntry.getKey();
		}
		return masterReference;
	}

}
