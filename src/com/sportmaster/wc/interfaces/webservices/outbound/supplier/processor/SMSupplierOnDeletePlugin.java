/**
 *
 */
package com.sportmaster.wc.interfaces.webservices.outbound.supplier.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.fc.WTObject;
import wt.util.WTException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.helper.SMSubDivisionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.supplier.util.SMSupplierUtil;

/**
 * @author 'true' ITC_Infotech.
 *
 */
public class SMSupplierOnDeletePlugin {

	private static final String FLEX_TYPE_ID_PATH = "flexTypeIdPath";
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSupplierOnDeletePlugin.class);
	/**
	 * Constructor.
	 */
	protected SMSupplierOnDeletePlugin(){
		//protected Constructor
	}

	/**
	 * Triggers on deletion of Supplier Object.
	 * @param obj - WTObject
	 */
	public static void processSupplierOnDelete(WTObject obj){
		try{
			LOGGER.debug("Triggering processSupplierOnDelete !!!!!!!!!!!!!!!");
			LCSSupplier supplierObj = (LCSSupplier)obj;
			Map<String, LCSLogEntry> supplierLogEntryMap= queryOutBoundSupplierLogEntryForDelete(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_OBJECTID);
			String supplierMasterReference = SMSupplierUtil.getSupplierMasterReferenceFromSupplier(supplierObj);

			for(Map.Entry<String, LCSLogEntry> supplierEntry : supplierLogEntryMap.entrySet()){
				if(supplierMasterReference.equals(supplierEntry.getKey())){
					supplierEntry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_INTEGRATION_STATUS, SMOutboundWebServiceConstants.LOG_ENTRY_OBJECT_MISSING);
					supplierEntry.getValue().setValue(SMOutboundWebServiceConstants.SUPPLIER_LOG_ENTRY_ERROR_REASON, "");
					SMSubDivisionHelper.persistLogEntry(supplierEntry.getValue());
				}
			}
		}catch(WTException wtExcp){
			LOGGER.error(wtExcp.getLocalizedMessage());
			wtExcp.printStackTrace();
		}
	}


	/**
	 * Queries the Log Entry.
	 * @param String flexPath
	 * @throws wt.util.WTException exceptions
	 * @returns HashMap<String, LCSLogEntry>
	 */
	public static Map<String, LCSLogEntry> queryOutBoundSupplierLogEntryForDelete(String attribute){
		Map<String, LCSLogEntry> logEntrySupplierForDeleteMap = new HashMap<>();
		try{
			LOGGER.debug("Inside queryOutBoundSupplierLogEntryForDelete method !!!!!!!!!!");
			//Hashmap to store Log entries

			//String supplierLogEntry="LCSLogEntry";
			com.lcs.wc.flextype.FlexType supplierLogEntryType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMOutboundWebServiceConstants.LOG_ENRTY_BUSINESS_SUPPLIER_OUTBOUND_PATH);
			//LOGGER.debug("ID PATH ##############################   "+supplierLogEntryType.getIdPath());
			com.lcs.wc.db.PreparedQueryStatement statement = new com.lcs.wc.db.PreparedQueryStatement();//Creating Statement.
			statement.appendFromTable(LCSLogEntry.class);
			statement.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, supplierLogEntryType.getAttribute(attribute).getColumnName());//append column
			statement.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, "IDA2A2");//append column
			statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY, FLEX_TYPE_ID_PATH,
					supplierLogEntryType.getIdPath(), Criteria.EQUALS));// adding
			// criteria

			statement.appendOrIfNeeded();
			supplierLogEntryType = FlexTypeCache.getFlexTypeFromPath(SMOutboundWebServiceConstants.LOG_ENRTY_MATERIAL_SUPPLIER_OUTBOUND_PATH);
			statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY, FLEX_TYPE_ID_PATH,
					supplierLogEntryType.getIdPath(), Criteria.EQUALS));

			statement.appendOrIfNeeded();
			supplierLogEntryType = FlexTypeCache.getFlexTypeFromPath(SMOutboundWebServiceConstants.LOG_ENRTY_FACTORY_OUTBOUND_PATH);
			statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY, FLEX_TYPE_ID_PATH,
					supplierLogEntryType.getIdPath(), Criteria.EQUALS));

			com.lcs.wc.db.SearchResults supplierLogEntryResults = null;
			//executing  statement
			supplierLogEntryResults =LCSQuery.runDirectQuery(statement);
			List<?> outboundLogEntryDataCollection= supplierLogEntryResults.getResults();
			FlexObject fo=null;
			LOGGER.debug("Log Entry Outbound data collection Size (for delete)    >>>>>>>\t"+outboundLogEntryDataCollection.size());
			if (!outboundLogEntryDataCollection.isEmpty()) {
				for(Object obj:outboundLogEntryDataCollection){
					fo= (FlexObject) obj;
					//LOGGER.debug("Supplier LogEntry Object >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  "+fo);
					LCSLogEntry logEntry=(LCSLogEntry) LCSQuery.findObjectById("com.lcs.wc.foundation.LCSLogEntry:"+fo.getString("LCSLOGENTRY.IDA2A2"));
					//storing in hashmap
					logEntrySupplierForDeleteMap.put((String)logEntry.getValue(attribute), logEntry);
				}
			}
		}catch(WTException exp){
			LOGGER.error("ERROR OCCURED:-", exp);
		}
		return logEntrySupplierForDeleteMap;
	}
}
