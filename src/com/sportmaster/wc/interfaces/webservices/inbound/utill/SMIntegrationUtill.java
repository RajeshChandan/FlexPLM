package com.sportmaster.wc.interfaces.webservices.inbound.utill;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSLogEntryLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.bean.BOEnumeration;
import com.sportmaster.wc.interfaces.webservices.inbound.helper.SMInboundIntegrationHelper;

/**
 * SMIntegrationUtill.java
 * This class is using to call the methods defined in process class.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMIntegrationUtill {

	/**
	 * LCS_LOG_ENTRY.
	 */
	private static final String LCS_LOG_ENTRY = "LCSLogEntry";
	/**
	 * LCSLOGENTRY_IDA2A2.
	 */
	private static final String LCSLOGENTRY_IDA2A2 = "LCSLOGENTRY.IDA2A2";
	/** The att key column map. */
	private static Map<String, String> attKeyColumnMap = new HashMap<>();

	/**
	 * constructor.
	 */
	public SMIntegrationUtill() {
		super();
	}

	/**
	 * this method fetch all log entry from according to type and status value.
	 * @param type - String
	 * @param attributes - String
	 * @param stausValue - String
	 * @return - Map
	 * @throws WTException - WTException
	 */
	public Map<String, FlexObject> getLogEntryListValues(String type,String attributes,String stausValue) throws WTException{
		Map<String, FlexObject> dbData = new HashMap<>();

		FlexType logType = FlexTypeCache.getFlexTypeFromPath(type);
		SearchResults results = null;
		PreparedQueryStatement statement = new PreparedQueryStatement();//Creating Statement.
		statement.appendFromTable(LCS_LOG_ENTRY);
		statement.appendSelectColumn(new QueryColumn(LCS_LOG_ENTRY, "idA2A2"));
		//statement.appendSelectColumn(new QueryColumn(integrationClass, "typeDisplay"));


		addSelectColumns(statement, attributes, logType, LCS_LOG_ENTRY);

		//add tables
		statement.appendFromTable(LCS_LOG_ENTRY);
		if(FormatHelper.hasContent(stausValue)){
			statement.appendCriteria(new Criteria(LCS_LOG_ENTRY, logType.getAttribute(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_STATUS_KEY).getColumnName(), stausValue, Criteria.EQUALS));
		}

		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(LCS_LOG_ENTRY, "flexTypeIdPath", logType.getIdPath(),Criteria.EQUALS));

		results=LCSQuery.runDirectQuery(statement);
		Collection<?> data=results.getResults();
		Iterator<?> itr= data.iterator();
		while(itr.hasNext()){
			FlexObject fo= (FlexObject) itr.next();
			LCSLogEntry entry=getLogEntryFromFlexObject(fo);
			String mdmid=(String) entry.getValue(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_MDMID_KEY);
			dbData.put(mdmid,fo);
		}
		return dbData;
	}

	/** returns key value map.
	 * @return Map
	 */
	public static Map<String, String> getAttKeyColumnMap() {
		return attKeyColumnMap;
	}

	/**
	 * returns LCSLogEntry from flex object.
	 * @param fo - FlexObject
	 * @return - LCSLogEntry
	 * @throws WTException - WTException
	 */
	public static LCSLogEntry getLogEntryFromFlexObject(FlexObject fo) throws WTException{
		return (LCSLogEntry) LCSQuery.findObjectById("com.lcs.wc.foundation.LCSLogEntry:"+fo.getString(LCSLOGENTRY_IDA2A2));
	}

	/**
	 * adding required columsn to query statement.
	 * @param queryStatement - queryStatement
	 * @param attr - attr
	 * @param flexType - flexType
	 * @param queryTableName - queryTableName
	 * @return statement
	 * @throws WTException exception
	 */
	private static PreparedQueryStatement addSelectColumns(PreparedQueryStatement queryStatement,
			String attr, FlexType flexType, String queryTableName) throws WTException {
		FlexTypeAttribute att = null;
		if(FormatHelper.hasContent(attr)){
			Collection<?> prdColl = FormatHelper.commaSeparatedListToCollection(attr);
			Iterator<?> itr = prdColl.iterator();
			String prdData = null;
			while(itr.hasNext()){
				prdData = (String) itr.next();
				att = flexType.getAttribute(prdData);
				attKeyColumnMap.put(prdData,queryTableName+"."+att.getColumnName());
				queryStatement.appendSelectColumn(queryTableName, att.getColumnName());
			}
		}
		return queryStatement;
	}

	/**
	 *  this method creates log entry in flex DB.
	 * @param dataValues - Map
	 * @param flexType - String
	 * @param attKeyColumnMap - Map
	 * @return - String
	 */
	public static String createLogEntry(Map<String, String> dataValues,String flexType,Map<String, String> attKeyColumnMap) {

		LCSLogEntry logEntry;
		try {
			logEntry = new LCSLogEntry();

			FlexType logType = FlexTypeCache.getFlexTypeFromPath(flexType);
			TypeDefinitionReference typeDefinitionRef = logType.getTypeDefinitionReference(LCSLogEntry.class);
			logEntry.setTypeDefinitionReference(typeDefinitionRef);
			logEntry.setFlexType(logType);
			logEntry.setFlexTypeIdPath(logType.getTypeIdPath());

			SMInboundIntegrationHelper.setAttributeValues(dataValues, attKeyColumnMap, null, logEntry, true, LCS_LOG_ENTRY);
			logEntry.setValue(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_ERROR_KEY, dataValues.get(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_ERROR_KEY));
			LCSLogEntryLogic logic= new LCSLogEntryLogic();
			logEntry=logic.saveLog(logEntry, true);
			return FormatHelper.getNumericObjectIdFromObject(logEntry);
		} catch (WTPropertyVetoException | WTException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * this method updates log entry in flex DB.
	 * @param lcsLogEntry - LCSLogEntry
	 * @param fo - FlexObject
	 * @param dataValues - Map
	 * @param attKeyColumnMap - Map
	 */
	public static void updateLogEntry(LCSLogEntry lcsLogEntry,FlexObject fo,Map<String, String> dataValues,Map<String, String> attKeyColumnMap) {
		//phase 13 - added for soanr fix, unused method parameter
		Objects.nonNull(fo);
		LCSLogEntry logEntry;
		try {
			logEntry =lcsLogEntry;

			SMInboundIntegrationHelper.setAttributeValues(dataValues, attKeyColumnMap, fo, logEntry, false, LCS_LOG_ENTRY);
			logEntry.setValue(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_ERROR_KEY, dataValues.get(SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_ERROR_KEY));
			LCSLogEntryLogic logic= new LCSLogEntryLogic();
			logic.saveLog(logEntry, true);
		} catch (WTPropertyVetoException | WTException e) {
			e.printStackTrace();
		}

	}

	/**
	 * this method returns BOEnumeration according to type.
	 * @param type - String
	 * @return - BOEnumeration
	 */
	public static BOEnumeration getBOEnumType(String type){
		if(SMInboundWebserviceConstants.BO_CLASS.equals(type)){

			return BOEnumeration.CLASS;
		}else if(SMInboundWebserviceConstants.BO_SUBCLASS.equals(type)){
			return BOEnumeration.SUB_CLASS;

		}else if(SMInboundWebserviceConstants.BO_CATEGORY.equals(type)){

			return BOEnumeration.CATEGORY;
		}else if(SMInboundWebserviceConstants.BO_SUBCATEGORY.equals(type)){

			return BOEnumeration.SUB_CATEGORY;
		}else if(SMInboundWebserviceConstants.BO_GOM.equals(type)){
			return BOEnumeration.GO_M;

		}
		return null;
	}


}
