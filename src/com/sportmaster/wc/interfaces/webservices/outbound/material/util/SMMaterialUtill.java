package com.sportmaster.wc.interfaces.webservices.outbound.material.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialLogic;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierMaster;
import com.lcs.wc.material.LCSMaterialSupplierQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.SortHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMIntegrationUtill;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;
/**
 * SMMaterialUtill.java
 * This class has utility methods.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMMaterialUtill {

	/**
	 * Declaration for private LOGGER attribute.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMMaterialUtill.class);

	public static final Integer  REQUEST_IDD=Integer.valueOf(SMInboundWebserviceConstants.REQUEST_ID_CONST_VALUE);
	public static final String LCS_LOG_ENTRY="LCSLogEntry";
	/**
	 * LCSLOGENTRY_IDA2A2.
	 */
	/** The att key column map. */
	private static Map<String, String> attKeyColumnMap=new HashMap<>();

	/**
	 * this method return unique request id.
	 * @return int
	 * @throws WTException
	 */
	@SuppressWarnings("unchecked")
	public int  getRequestID(String types) throws WTException{
		
		SearchResults results = null;
		LOGGER.debug("Getting Request ID:");
		int requestId = FormatHelper.parseInt(String.valueOf(REQUEST_IDD));
		FlexType logType = FlexTypeCache.getFlexTypeFromPath(SMOutboundWebServiceConstants.LOGENTRY_TYPE_REQID);
		FlexTypeAttribute att = logType.getAttribute(SMOutboundWebServiceConstants.LOGENTRY_REQUEST_ID);

		PreparedQueryStatement statement = new PreparedQueryStatement();// Creating Statement.

		statement.appendFromTable(LCS_LOG_ENTRY);
		statement.appendSelectColumn(new QueryColumn(LCS_LOG_ENTRY, "idA2A2"));
		statement.appendSelectColumn(LCS_LOG_ENTRY, att.getColumnName());
		// add tables
		statement.appendFromTable(LCS_LOG_ENTRY);

		addCriteria(types, statement);
		results = LCSQuery.runDirectQuery(statement);
		List<FlexObject> data = results.getResults();
		int reqId;
		data = (List<FlexObject>) SortHelper.sortFlexObjects(data, LCS_LOG_ENTRY + "." + att.getColumnName());

		if (!data.isEmpty()) {
			reqId = data.get(data.size() - 1).getInt(LCS_LOG_ENTRY + "." + att.getColumnName());
			requestId = reqId + 1;
			return requestId;
		}
		return requestId + 1;
	}

	/**
	 * adding Criteria for Query.
	 * @param types ---String
	 * @param statement---PreparedQueryStatement
	 * @throws WTException--Exception
	 */
	private void addCriteria(String types,PreparedQueryStatement statement) throws WTException{
		FlexType logType;
		Collection<?> logTypesColl=FormatHelper.commaSeparatedListToCollection(types);
		for(Object typeObj:logTypesColl){

			statement.appendOrIfNeeded();
			logType = FlexTypeCache.getFlexTypeFromPath(String.valueOf(typeObj));
			statement.appendCriteria(new Criteria(LCS_LOG_ENTRY, "flexTypeIdPath", logType.getIdPath(),Criteria.EQUALS));
		}

	}
	/**
	 * adding Criteria for Query.
	 * @param types--String
	 * @param statement--PreparedQueryStatement
	 * @param criteriaValue--String
	 * @param criteriaAttr --String
	 * @param queue --boolean
	 * @throws WTException--Exception
	 */
	private void addCriteria(String types,PreparedQueryStatement statement,String criteriaValue,String criteriaAttr, boolean queue) throws WTException{
		FlexType logType;
		Collection<?> logTypesColl = FormatHelper.commaSeparatedListToCollection(types);
		for (Object typeObj : logTypesColl) {

			statement.appendOrIfNeeded();
			logType = FlexTypeCache.getFlexTypeFromPath(String.valueOf(typeObj));
			if (queue) {
				statement.appendCriteria(new Criteria(LCS_LOG_ENTRY, logType.getAttribute(criteriaAttr).getColumnName(),
						criteriaValue, Criteria.EQUALS));
				statement.appendAndIfNeeded();
			}
			statement.appendCriteria(
					new Criteria(LCS_LOG_ENTRY, "flexTypeIdPath", logType.getIdPath(), Criteria.EQUALS));
		}

	}
	/**
	 * @param type --string
	 * @param attributes --String
	 * @param criteriaValue --String 
	 * @param criteriaAttr -String
	 * @return --Map
	 * @throws WTException --Exception
	 */
	public Map<String, FlexObject> getMaterialLogENtryData(String type,String attributes,String criteriaValue,String criteriaAttr) throws WTException{
		
		Map<String, FlexObject> dbData = new HashMap<>();

		FlexType logType = FlexTypeCache.getFlexTypeFromPath(SMOutboundWebServiceConstants.LOGENTRY_TYPE_REQID);
		SearchResults results = null;
		PreparedQueryStatement statement = new PreparedQueryStatement();// Creating Statement.
		statement.appendFromTable(LCS_LOG_ENTRY);
		statement.appendSelectColumn(new QueryColumn(LCS_LOG_ENTRY, "idA2A2"));

		addSelectColumns(statement, attributes, logType);
		// add tables
		statement.appendFromTable(LCS_LOG_ENTRY);

		if (FormatHelper.hasContent(criteriaValue)) {
			addCriteria(type, statement, criteriaValue, criteriaAttr, true);
		} else {
			addCriteria(type, statement);
		}

		results = LCSQuery.runDirectQuery(statement);
		@SuppressWarnings("unchecked")
		Collection<FlexObject> data = results.getResults();
		Iterator<?> itr = data.iterator();
		while (itr.hasNext()) {
			FlexObject fo = (FlexObject) itr.next();
			LCSLogEntry entry = SMIntegrationUtill.getLogEntryFromFlexObject(fo);
			String mapKey = (String) entry.getValue(SMOutboundWebServiceConstants.MATERIAL_LOGENTRY_OBJECTID_ATTR);
			dbData.put(mapKey, fo);
		}
		return dbData;
	}

	/**
	 * Getting pricing MOA Data.
	 * @param material --LCSMaterial
	 * @return
	 */
	public LCSMOATable getPricingData(LCSMaterial material){
		LCSMOATable moa = null;
		try {
			
			SearchResults result = LCSMaterialSupplierQuery.findMaterialSuppliers(material);
			@SuppressWarnings("unchecked")
			Collection<FlexObject> coll = result.getResults();
			for (FlexObject fo : coll) {
				
				LCSMaterialSupplierMaster matSupplrMstr = (LCSMaterialSupplierMaster) LCSMaterialSupplierQuery
						.findObjectById("com.lcs.wc.material.LCSMaterialSupplierMaster:"
								+ fo.getString("LCSMATERIALSUPPLIERMASTER.IDA2A2"));
				
				LCSMaterialSupplier mtrlSplr = (LCSMaterialSupplier) VersionHelper.latestIterationOf(matSupplrMstr);
				moa = (LCSMOATable) mtrlSplr.getValue("smPricingReferenceInformation");
			}
		} catch (WTException wtExpMat) {
			LOGGER.debug(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtExpMat);
		}
		return moa;


	}
	/**
	 * Adding required columns to query statement.
	 * @param queryStatement - queryStatement
	 * @param attr - attr
	 * @param flexType - flexType
	 * @param queryTableName - queryTableName
	 * @return statement
	 * @throws WTException exception
	 */
	private PreparedQueryStatement addSelectColumns(PreparedQueryStatement queryStatement,
			String attr, FlexType flexType) throws WTException {
		FlexTypeAttribute att = null;
		if(FormatHelper.hasContent(attr)){
			Collection<?> prdColl = FormatHelper.commaSeparatedListToCollection(attr);
			Iterator<?> itr = prdColl.iterator();
			String prdData = null;
			while(itr.hasNext()){
				prdData = (String) itr.next();
				att = flexType.getAttribute(prdData);
				attKeyColumnMap.put(prdData,LCS_LOG_ENTRY+"."+att.getColumnName());
				queryStatement.appendSelectColumn(LCS_LOG_ENTRY, att.getColumnName());
			}
		}
		return queryStatement;
	}

	/**
	 * Updating the material.
	 * @param material --String
	 * @param value --string
	 */
	public void updateMaterial(LCSMaterial material,String value) {
		material.setValue("smMDMMAT", value);
		try {
			LCSMaterialLogic.persist(material,true);
			
		} catch (WTException wtExp) {
			LOGGER.debug(SMOutboundWebServiceConstants.ERROR_OCCURED_LITERAL, wtExp);
		}
	}

	/**
	 * return key columns map.
	 * @return  Map
	 */
	public Map<String, String> getKeyColumnMap(){
		return attKeyColumnMap;
	}
}
