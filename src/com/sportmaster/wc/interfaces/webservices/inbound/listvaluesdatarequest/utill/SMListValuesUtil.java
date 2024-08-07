package com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.utill;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.type.TypeDefinitionReference;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLifecycleManagedHelper;
import com.lcs.wc.foundation.LCSLifecycleManagedLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.helper.SMInboundIntegrationHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.helper.SMLVIntegrationHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.listvaluesdatarequest.processor.SMLVLogEntryDataProcessor;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;

/**
 * SMListValuesUtil.java
 * This class is using to call the methods defined in process class.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMListValuesUtil {

	/**
	 * ERROR_OCCURED_LITERAL
	 */
	private static final String ERROR_OCCURED_LITERAL = "ERROR OCCURED : ";
	
	/** The att key column map. */
	private static Map<String, String> attKeyColumnMap=new HashMap<String, String>();
	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMListValuesUtil.class);

	/**
	 * constructor.
	 */
	protected SMListValuesUtil() {
		
	}

	/**
	 * return all Business object data from db according to type.
	 * @param businessObjectType - String
	 * @return - collection
	 * @throws WTException - WTException
	 */
	public static Map<String,Multimap<String, FlexObject>> getAllListValues(String businessObjectType) throws WTException{

		Map<String,Multimap<String, FlexObject>> dbData= new HashMap<String,Multimap<String, FlexObject>>();
		
		Multimap<String, FlexObject> classDbData=ArrayListMultimap.create();
		Multimap<String, FlexObject> subClassDbData= ArrayListMultimap.create();
		Multimap<String, FlexObject> categoryDbData=ArrayListMultimap.create();
		Multimap<String, FlexObject> subCategoryDbData=ArrayListMultimap.create();
		Multimap<String, FlexObject> gomDbData=ArrayListMultimap.create();

		SearchResults results = null;
		PreparedQueryStatement statement = new PreparedQueryStatement();//Creating Statement.
		statement.appendFromTable(SMInboundWebserviceConstants.BUSINESS_OBJECT_CLASS_NAME);
		statement.appendSelectColumn(new QueryColumn(SMInboundWebserviceConstants.BUSINESS_OBJECT_CLASS_NAME, "idA2A2"));
		statement.appendSelectColumn(new QueryColumn(SMInboundWebserviceConstants.BUSINESS_OBJECT_CLASS_NAME, "flexTypeIdPath"));

		//add tables
		statement.appendFromTable(SMInboundWebserviceConstants.BUSINESS_OBJECT_CLASS_NAME);

		addCriteria(statement, businessObjectType, SMInboundWebserviceConstants.BUSINESS_OBJECT_CLASS_NAME);

		results=LCSQuery.runDirectQuery(statement);
		Collection<?> data=results.getResults();
		Iterator<?> itr= data.iterator();
		while(itr.hasNext()){
			FlexObject fo= (FlexObject) itr.next();
			
			LCSLifecycleManaged bo= getBoFromFlexObject(fo);
			String type=bo.getFlexType().getTypeName();
			if(SMInboundWebserviceConstants.BO_CLASS.equalsIgnoreCase(type)){
				
				classDbData.put(fo.getString(SMInboundWebserviceConstants.LCSLIFECYCLEMANAGED_MDMID_COLUMN_NAME),fo);

			}else if(SMInboundWebserviceConstants.SUBCLASS_TYPE.equalsIgnoreCase(type)){
				subClassDbData.put(fo.getString(SMInboundWebserviceConstants.LCSLIFECYCLEMANAGED_MDMID_COLUMN_NAME),fo);

			}else if(SMInboundWebserviceConstants.BO_CATEGORY.equalsIgnoreCase(type)){
				categoryDbData.put(fo.getString(SMInboundWebserviceConstants.LCSLIFECYCLEMANAGED_MDMID_COLUMN_NAME),fo);

			}else if(SMInboundWebserviceConstants.SUBCATEGORY_TYPE.equalsIgnoreCase(type)){
				subCategoryDbData.put(fo.getString(SMInboundWebserviceConstants.LCSLIFECYCLEMANAGED_MDMID_COLUMN_NAME),fo);

			}else if(SMInboundWebserviceConstants.GOM_TYPE.equalsIgnoreCase(type)){
				gomDbData.put(fo.getString(SMInboundWebserviceConstants.LCSLIFECYCLEMANAGED_MDMID_COLUMN_NAME),fo);

			}
		}
		dbData.put(SMInboundWebserviceConstants.BO_CLASS.toLowerCase(), classDbData);
		dbData.put(SMInboundWebserviceConstants.BO_SUBCLASS.toLowerCase(), subClassDbData);
		dbData.put(SMInboundWebserviceConstants.BO_CATEGORY.toLowerCase(), categoryDbData);
		dbData.put(SMInboundWebserviceConstants.BO_SUBCATEGORY.toLowerCase(), subCategoryDbData);
		dbData.put(SMInboundWebserviceConstants.BO_GOM.toLowerCase(), gomDbData);
		return dbData;
	}

	/**
	 *  adds required columns to query statement.
	 * @param queryStatement - queryStatement
	 * @param attr - String
	 * @param flexType - flexType
	 * @param queryTableName - String
	 * @return - PreparedQueryStatement
	 * @throws WTException - WTException
	 */
	private static PreparedQueryStatement addSelectColumns(PreparedQueryStatement queryStatement,
			String attr, FlexType flexType, String queryTableName) throws WTException {
		FlexTypeAttribute att = null;
		Collection<?> prdColl = FormatHelper.commaSeparatedListToCollection(attr);
		Iterator<?> itr = prdColl.iterator();
		String prdData = null;
		while(itr.hasNext()){
			prdData = (String) itr.next();
			att = flexType.getAttribute(prdData);
			attKeyColumnMap.put(prdData,queryTableName+"."+att.getColumnName());
			queryStatement.appendSelectColumn(queryTableName, att.getColumnName());
		}
		return queryStatement;
	}
	/**
	 * adding required column to query statement.
	 * @param queryStatement - queryStatement
	 * @param attr - attr
	 * @param queryTableName - queryTableName
	 * @return statement
	 * @throws WTException exception
	 */
	private static PreparedQueryStatement addCriteria(PreparedQueryStatement queryStatement,
			String attr,  String queryTableName) throws WTException {
		Collection<?> prdColl = FormatHelper.commaSeparatedListToCollection(attr);
		Iterator<?> itr = prdColl.iterator();
		String prdData = null;
		while(itr.hasNext()){
			prdData = (String) itr.next();
			FlexType type = FlexTypeCache.getFlexTypeFromPath(prdData);

			addSelectColumns(queryStatement, SMInboundWebserviceConstants.LIST_VALUES_INTEGRATION_FILEDS, type, queryTableName);
			queryStatement.appendOrIfNeeded();
			queryStatement.appendCriteria(new Criteria(queryTableName, "flexTypeIdPath", type.getIdPath(),Criteria.EQUALS));

		}
		return queryStatement;
	}
	/**
	 * return key columns map.
	 * @return  Map
	 */
	public static Map<String, String> getKeyColumnMap(){
		return attKeyColumnMap;
	}

	/**
	 * creates Business Object in Flex System.
	 * @param dataValues - Map
	 * @param type - String
	 * @param attKeyColumnMap - Map
	 * @param dbLogEntryData - Map
	 * @param dbBOData - Multimap
	 * @param lvBean - SMLVIntegrationBean
	 * @param requesType - String
	 */
	public static void createBusinessObject(Map<String, String> dataValues,String type,Map<String, String> attKeyColumnMap,Map<String, FlexObject> dbLogEntryData,Multimap<String, FlexObject> dbBOData,SMLVIntegrationBean lvBean)  {
		String createIDA2A2="";
		String mdmid=null;
		boolean persist=false;
		LCSLifecycleManaged lifecycleManaged = null;
		Map<String, String> logentryDataValue=null;
		FlexType flexType;
		try {
			lifecycleManaged= LCSLifecycleManaged.newLCSLifecycleManaged();
			flexType = FlexTypeCache.getFlexTypeFromPath(type);
			//setting type

			TypeDefinitionReference typeDefinitionRef = flexType.getTypeDefinitionReference(LCSLifecycleManaged.class);

			lifecycleManaged.setTypeDefinitionReference(typeDefinitionRef);
			lifecycleManaged.setFlexType(flexType);
			lifecycleManaged.setFlexTypeIdPath(flexType.getIdPath());
			persist=SMInboundIntegrationHelper.setAttributeValues(dataValues,attKeyColumnMap, null,lifecycleManaged,true,SMInboundWebserviceConstants.BUSINESS_OBJECT_CLASS_NAME);
			if(persist){
				mdmid=(String) lifecycleManaged.getValue(SMInboundWebserviceConstants.BO_MDMID);
				lifecycleManaged=LCSLifecycleManagedHelper.service.saveLifecycleManaged(lifecycleManaged);
				LOGGER.debug("Successfully Created business Object :"+lifecycleManaged.getName());
				lvBean.setLvCreateObjCount(1);
				createIDA2A2=FormatHelper.getNumericObjectIdFromObject(lifecycleManaged);
				SMLVIntegrationHelper.reConfigureColection(dbBOData,null, createIDA2A2, dataValues, attKeyColumnMap,SMInboundWebserviceConstants.BO_MDMID, SMInboundWebserviceConstants.BUSINESS_OBJECT_IDA2A2);
				logentryDataValue=SMLVIntegrationHelper.getLogEntryDataValues(lifecycleManaged, createIDA2A2, SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_INTEGRATED, SMInboundWebserviceConstants.LOGENTRY_STATUS_VALUE_NOTSENT, "",lvBean);
				
			}else{
				logentryDataValue=SMLVIntegrationHelper.getLogEntryDataValues(lifecycleManaged, createIDA2A2, SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_NOT_INTEGRATED, SMInboundWebserviceConstants.LOGENTRY_STATUS_VALUE_NOTSENT, "UNABLE TO SET ATTRIBUTE VALUES",lvBean);
				lvBean.setLvFailCount(1);
			}
		} catch (WTPropertyVetoException wtPropertyExp) {
			wtPropertyExp.printStackTrace();
			LOGGER.error(ERROR_OCCURED_LITERAL+wtPropertyExp.getLocalizedMessage());
			logentryDataValue=SMLVIntegrationHelper.getLogEntryDataValues(lifecycleManaged, createIDA2A2, SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_NOT_INTEGRATED, SMInboundWebserviceConstants.LOGENTRY_STATUS_VALUE_NOTSENT, wtPropertyExp.getLocalizedMessage(),lvBean);
			lvBean.setLvFailCount(1);
		} catch (WTException wtExp) {
			wtExp.printStackTrace();
			LOGGER.error(ERROR_OCCURED_LITERAL+wtExp.getLocalizedMessage());
			logentryDataValue=SMLVIntegrationHelper.getLogEntryDataValues(lifecycleManaged, createIDA2A2, SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_NOT_INTEGRATED, SMInboundWebserviceConstants.LOGENTRY_STATUS_VALUE_NOTSENT, wtExp.getLocalizedMessage(),lvBean);
			lvBean.setLvFailCount(1);
		} 
		SMLVLogEntryDataProcessor.processLogEntry(dbLogEntryData, mdmid, logentryDataValue);
	}

	/**
	 *  Updates Business Object in Flex system.
	 * @param dataValues - Map
	 * @param flexObject - FlexObject
	 * @param lifecycleManaged - LCSLifecycleManaged
	 * @param attKeyColumnMap - Map 
	 * @param dbLogEntryData - Map
	 * @param lvBean - SMLVIntegrationBean
	 * @param requesType - String
	 */
	public static void updateBusinessObject(Map<String, String> dataValues,FlexObject flexObject, LCSLifecycleManaged lifecycleManaged,Map<String, String> attKeyColumnMap,Map<String, FlexObject> dbLogEntryData,SMLVIntegrationBean lvBean) {
		boolean persist=false;
		String ida2a2=flexObject.getString(SMInboundWebserviceConstants.BUSINESS_OBJECT_IDA2A2);
		Map<String, String> logentryDataValue=null;
		String mdmid="";
		try {
			persist = SMInboundIntegrationHelper.setAttributeValues(dataValues,attKeyColumnMap, flexObject,lifecycleManaged,false,SMInboundWebserviceConstants.BUSINESS_OBJECT_CLASS_NAME);
			if(persist){
				mdmid=(String) lifecycleManaged.getValue(SMInboundWebserviceConstants.BO_MDMID);
				LCSLifecycleManagedLogic.deriveFlexTypeValues(lifecycleManaged);
				LCSLifecycleManagedLogic.persist(lifecycleManaged);
				LOGGER.debug("Successfully updated business Object :"+lifecycleManaged.getName());
				logentryDataValue=SMLVIntegrationHelper.getLogEntryDataValues(lifecycleManaged, ida2a2, SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_INTEGRATED, SMInboundWebserviceConstants.LOGENTRY_STATUS_VALUE_NOTSENT, "",lvBean);
				lvBean.setLvUpdateCount(1);
				
			}else{
				logentryDataValue=SMLVIntegrationHelper.getLogEntryDataValues(lifecycleManaged, ida2a2, SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_NOT_INTEGRATED, SMInboundWebserviceConstants.LOGENTRY_STATUS_VALUE_NOTSENT, "UNABLE TO SET ATTRIBUTE VALUES",lvBean);
				lvBean.setLvFailCount(1);
				
			}
		} catch (WTPropertyVetoException wtPropertyExp) {
			wtPropertyExp.printStackTrace();
			LOGGER.error(ERROR_OCCURED_LITERAL+wtPropertyExp.getLocalizedMessage());
			logentryDataValue=SMLVIntegrationHelper.getLogEntryDataValues(lifecycleManaged, ida2a2, SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_NOT_INTEGRATED, SMInboundWebserviceConstants.LOGENTRY_STATUS_VALUE_NOTSENT, wtPropertyExp.getLocalizedMessage(),lvBean);
			lvBean.setLvFailCount(1);
		} catch (WTException wtExp) {
			wtExp.printStackTrace();
			LOGGER.error(ERROR_OCCURED_LITERAL+wtExp.getLocalizedMessage());
			logentryDataValue=SMLVIntegrationHelper.getLogEntryDataValues(lifecycleManaged, ida2a2, SMInboundWebserviceConstants.LOGENTRY_LIST_VALUE_NOT_INTEGRATED,SMInboundWebserviceConstants.LOGENTRY_STATUS_VALUE_NOTSENT, wtExp.getLocalizedMessage(),lvBean);
			lvBean.setLvFailCount(1);
		} 
		
		SMLVLogEntryDataProcessor.processLogEntry(dbLogEntryData, mdmid, logentryDataValue);
	}

	
	
	
	/**
	 *  returns business object from flex object.
	 * @param fo - FlexObject
	 * @return -LCSLifecycleManaged
	 */
	public static LCSLifecycleManaged getBoFromFlexObject(FlexObject fo){
		try {
			return (LCSLifecycleManaged) LCSQuery.findObjectById("com.lcs.wc.foundation.LCSLifecycleManaged:"+fo.getString(SMInboundWebserviceConstants.BUSINESS_OBJECT_IDA2A2));
		} catch (WTException wtExp) {
			wtExp.printStackTrace();
			LOGGER.error(ERROR_OCCURED_LITERAL+wtExp.getLocalizedMessage());
		}
		return null;
	}

}
