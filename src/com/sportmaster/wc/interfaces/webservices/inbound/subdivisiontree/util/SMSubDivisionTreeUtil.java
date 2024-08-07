package com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

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
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.bean.Division;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.client.SMSubdivisionDataRequestWebClient;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.processor.SMSubDivisionLogEntryProcessor;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.processor.SMSubDivisionTreeDataProcessor;

import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;

/**
 * @author ITC_Infotech
 * Utility class for SubDivision tree integration.
 * @author 'true' ITC_Infotech
 * @version 1.0
 */
public class SMSubDivisionTreeUtil {

	/** The att key column map. */
	private static Map<String, String> attKeyColumnMap=new HashMap<String, String>();
	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSubDivisionTreeUtil.class);
	/**
	 * Initializing Empty String.
	 */
	public static final String EMPTY_STRING = "";
	
	/**
	 * Initializing zero value.
	 */
	public static final int ZERO = 0;
	
	//Declaring HashMap for Business Object/class
	private static Map<String, LCSLifecycleManaged> hmClass;
	//Declaring HashMap for Business Object/Sub class
	private static Map<String, LCSLifecycleManaged> hmSubClass;
	//Declaring HashMap for Business Object/Category
	private static Map<String, LCSLifecycleManaged> hmCategory;
	//Declaring HashMap for Business Object/Sub Category
	private static Map<String, LCSLifecycleManaged> hmSubCategory;
	//Declaring HashMap for Business Object/GOM
	private static Map<String, LCSLifecycleManaged> hmGoM;
	//Declaring HashMap for Business Object/SUB DIVISION TREE
	private static Map<String, LCSLifecycleManaged> hmSubDivTree;
	//Declaring HashMap for Log Entry/Inbound/Sub Division Tree
	private static Map<String, LCSLogEntry> hmLogEntryMDMID;
	
	/**
	 * Constructor.
	 */
	protected SMSubDivisionTreeUtil(){
		//protected constructor
	}

	/**
	 * Getting all List Values.
	 * @param businessObjectType - String
	 * @param columnName- String
	 * @return boDBData - Map<String, Map<String, LCSLifecycleManaged>>
	 * @throws WTException - WTException
	 */
	public static Map<String,Map<String, LCSLifecycleManaged>> getAllListValues(String businessObjectType,String columnName) throws WTException{

		Map<String,Map<String, LCSLifecycleManaged>> boDBData= new HashMap<String,Map<String, LCSLifecycleManaged>>();
		
		Map<String, LCSLifecycleManaged> classDbData=new HashMap<String, LCSLifecycleManaged>();
		Map<String, LCSLifecycleManaged> subClassDbData=new HashMap<String, LCSLifecycleManaged>();
		Map<String, LCSLifecycleManaged> categoryDbData=new HashMap<String, LCSLifecycleManaged>();
		Map<String, LCSLifecycleManaged> subCategoryDbData=new HashMap<String, LCSLifecycleManaged>();
		Map<String, LCSLifecycleManaged> gomDbData=new HashMap<String, LCSLifecycleManaged>();

		SearchResults results = null;
		PreparedQueryStatement prepQueryStatement = new PreparedQueryStatement();//Creating Statement.
		prepQueryStatement.appendFromTable(SMInboundWebserviceConstants.BUSINESS_OBJECT_CLASS_NAME);
		prepQueryStatement.appendSelectColumn(new QueryColumn(SMInboundWebserviceConstants.BUSINESS_OBJECT_CLASS_NAME, "idA2A2"));
		prepQueryStatement.appendSelectColumn(new QueryColumn(SMInboundWebserviceConstants.BUSINESS_OBJECT_CLASS_NAME, "flexTypeIdPath"));

		//add tables
		prepQueryStatement.appendFromTable(SMInboundWebserviceConstants.BUSINESS_OBJECT_CLASS_NAME);

		addCriteria(prepQueryStatement, businessObjectType, SMInboundWebserviceConstants.BUSINESS_OBJECT_CLASS_NAME);

		results=LCSQuery.runDirectQuery(prepQueryStatement);
		Collection<?> data=results.getResults();
		Iterator<?> itr= data.iterator();
		while(itr.hasNext()){
			FlexObject fo= (FlexObject) itr.next();
			
			LCSLifecycleManaged bo= getBoFromFlexObject(fo);
			String type=bo.getFlexType().getTypeName();
			if(SMInboundWebserviceConstants.BO_CLASS.equalsIgnoreCase(type)){
				
				//LOGGER.debug("Inside Class list value");
				classDbData.put(String.valueOf(bo.getValue(columnName)),bo);

			}else if(SMInboundWebserviceConstants.SUBCLASS_TYPE.equalsIgnoreCase(type)){
				//LOGGER.debug("Inside Sub Class list value");
				subClassDbData.put(String.valueOf(bo.getValue(columnName)),bo);

			}else if(SMInboundWebserviceConstants.BO_CATEGORY.equalsIgnoreCase(type)){
				//LOGGER.debug("Inside Category list value");
				categoryDbData.put(String.valueOf(bo.getValue(columnName)),bo);

			}else if(SMInboundWebserviceConstants.SUBCATEGORY_TYPE.equalsIgnoreCase(type)){
				//LOGGER.debug("Inside Sub Category list value");
				subCategoryDbData.put(String.valueOf(bo.getValue(columnName)),bo);

			}else if(SMInboundWebserviceConstants.GOM_TYPE.equalsIgnoreCase(type)){
				//LOGGER.debug("Inside Group of merchandiseory list value");
				gomDbData.put(String.valueOf(bo.getValue(columnName)),bo);

			}
		}
		//adding data to Class
		boDBData.put(SMInboundWebserviceConstants.BO_CLASS.toLowerCase(), classDbData);
		//adding data to Sub Class
		boDBData.put(SMInboundWebserviceConstants.BO_SUBCLASS.toLowerCase(), subClassDbData);
		//Adding data to Category
		boDBData.put(SMInboundWebserviceConstants.BO_CATEGORY.toLowerCase(), categoryDbData);
		//Adding data to Sub Category
		boDBData.put(SMInboundWebserviceConstants.BO_SUBCATEGORY.toLowerCase(), subCategoryDbData);
		//Adding data to GoM
		boDBData.put(SMInboundWebserviceConstants.BO_GOM.toLowerCase(), gomDbData);
		return boDBData;
	}

	/**
	 * getting Attribute Column Map.
	 * @param queryStatement - PreparedQueryStatement
	 * @param attr - String
	 * @param flexType - FlexType
	 * @param queryTableName - String
	 * @return queryStatement - PreparedQueryStatement
	 * @throws WTException - WTException
	 */
	private static PreparedQueryStatement addSelectColumnsToQuery(PreparedQueryStatement queryStatement,
			String attr, FlexType flexType, String queryTableName) throws WTException {
		FlexTypeAttribute flexAttr = null;
		Collection<?> prdColl = FormatHelper.commaSeparatedListToCollection(attr);
		Iterator<?> itr = prdColl.iterator();
		String prdData = null;
		while(itr.hasNext()){
			prdData = (String) itr.next();
			flexAttr = flexType.getAttribute(prdData);
			attKeyColumnMap.put(prdData,queryTableName+"."+flexAttr.getColumnName());
			queryStatement.appendSelectColumn(queryTableName, flexAttr.getColumnName());
		}
		return queryStatement;
	}
	/**
	 * Adding required column to query statement.
	 * @param queryStatement - PreparedQueryStatement
	 * @param attr - String
	 * @param queryTableName - String
	 * @return queryStatement - PreparedQueryStatement
	 * @throws WTException - WTException
	 */
	private static PreparedQueryStatement addCriteria(PreparedQueryStatement queryStatement,
			String attr,  String queryTableName) throws WTException {
		Collection<?> prdColl = FormatHelper.commaSeparatedListToCollection(attr);
		Iterator<?> itr = prdColl.iterator();
		String prdData = null;
		while(itr.hasNext()){
			prdData = (String) itr.next();
			FlexType type = FlexTypeCache.getFlexTypeFromPath(prdData);

			addSelectColumnsToQuery(queryStatement, SMInboundWebserviceConstants.LIST_VALUES_INTEGRATION_FILEDS, type, queryTableName);
			queryStatement.appendOrIfNeeded();
			queryStatement.appendCriteria(new Criteria(queryTableName, "flexTypeIdPath", type.getIdPath(),Criteria.EQUALS));

		}
		return queryStatement;
	}
	
	
	/**
	 * Get Attribute Column Map.
	 * @return attKeyColumnMap - Map<String, String>
	 */
	public static Map<String, String> getKeyColumnMap(){
		return attKeyColumnMap;
	}
	
	
	/**
	 * Gets Business Object from FlexObject.
	 * @param fo - FlexObject
	 * @return LCSLifecycleManaged
	 */
	public static LCSLifecycleManaged getBoFromFlexObject(FlexObject fo){
		try {
			return (LCSLifecycleManaged) LCSQuery.findObjectById("com.lcs.wc.foundation.LCSLifecycleManaged:"+fo.getString(SMInboundWebserviceConstants.BUSINESS_OBJECT_IDA2A2));
		} catch (WTException wtExp) {
			LOGGER.error("ERROR OCCURED : "+wtExp.getLocalizedMessage());
			wtExp.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Checks if the response is valid and not null.
	 * @param hmCategory2 - Map<String, LCSLifeCycleManaged>
	 * @param name - String
	 * @param boName - String
	 * @param div - Division
	 * @return boolean
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException 
	 */
	public static boolean checkBOexist(Map<String, LCSLifecycleManaged> hmCategory2, String name, String boName, Division div) throws WTException, WTPropertyVetoException{
		LOGGER.debug("Inside checkBOexist method !!!!");
		
		//Checks if required objects are present
		if(FormatHelper.hasContent(name) && hmCategory2.containsKey(name)){
			return true;
		}else if (!FormatHelper.hasContent(name)){
			//Logging Error
			SMSubDivisionTreeDataProcessor.setStrErrorString(SMInboundWebserviceConstants.EMPTY_VALUE_ERROR+"  "+SMSubDivisionTreeUtil.getDisplayName(boName));
			SMSubDivisionTreeDataProcessor.setINTEGRATIONSTATUS(SMInboundWebserviceConstants.STATUS_NOT_INTEGRATED);
			LOGGER.error(SMSubDivisionTreeDataProcessor.getStrErrorString());
			//checkLogEntryObjectID(div);
			//Setting Log Entry
			SMSubDivisionLogEntryProcessor.setLogEntry(null, div);
			return false;
		}
		else{
			//Logging Error
			SMSubDivisionTreeDataProcessor.setStrErrorString("Object with MDMID   "+name+" "+SMInboundWebserviceConstants.OBJ_REFERENCE_ABSENT+" "+SMSubDivisionTreeUtil.getDisplayName(boName));
			SMSubDivisionTreeDataProcessor.setINTEGRATIONSTATUS(SMInboundWebserviceConstants.STATUS_NOT_INTEGRATED);
			//Get display name for attribute key
			SMSubDivisionTreeUtil.getDisplayName(boName);
			LOGGER.error(SMSubDivisionTreeDataProcessor.getStrErrorString());
			//checkLogEntryObjectID(div);
			//Setting Log Entry
			SMSubDivisionLogEntryProcessor.setLogEntry(null, div);
			return false;
		}
	
	}
	
	/**
	 * Check for Object ID in Log Entry.
	 * @param div - Division
	 * @return String
	 */
	public static String checkLogEntryObjectID(Division div){
		LOGGER.debug("Inside checkLogEntryMDMID method !!!!!");
		if(hmLogEntryMDMID.containsKey(div.getMdmBO())){
			try {
				String logEntryObjectID = (String) hmLogEntryMDMID.get(div.getMdmBO()).getValue(SMInboundWebserviceConstants.LOG_ENRTY_OBJECTID_KEY);
				LOGGER.debug("logEntryObjectID >>>>>>>>>>>>>>>>    "+logEntryObjectID);
				return logEntryObjectID;
			} catch (WTException e) {
				LOGGER.error(e.getLocalizedMessage());
				e.printStackTrace();
			}	
		}
		return SMInboundWebserviceConstants.NO_ERROR;
	}
	
	/**
	 * 
	 * @param div - Division
	 * @return String
	 */
	public static String checkLogEntryName(Division div){
		LOGGER.debug("Inside checkLogEntryName method !!!!!");
		if(hmLogEntryMDMID.containsKey(div.getMdmBO())){
			LOGGER.info("MDMID exists in Log entry records !!");
			try {
				String logEntryName = (String) hmLogEntryMDMID.get(div.getMdmBO()).getValue(SMInboundWebserviceConstants.LOG_ENRTY_NAME);
				LOGGER.debug("logEntryName >>>>>>>>>>>>>>>>>  "+logEntryName);
				return logEntryName;
			} catch (WTException e) {
				LOGGER.error(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
		return SMInboundWebserviceConstants.NO_ERROR;
	}
	
	/**
	 * gets Display Name of attribute.
	 * @param strInternalName - String 
	 * @return String
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static String getDisplayName(String strInternalName) throws WTException, WTPropertyVetoException{
		com.lcs.wc.flextype.FlexType boType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMInboundWebserviceConstants.strSubDivTreePath);
		LCSLifecycleManaged lcmObj = LCSLifecycleManaged.newLCSLifecycleManaged();
		lcmObj.setFlexType(boType);
		//Returns the display name of attribute
		return lcmObj.getFlexType().getAttribute(strInternalName).getAttDisplay();
	}
	
	/**
	 * Prints summary for one integration run.
	 */
	public static void printSummary(){
		StringBuffer summary=new StringBuffer();
		summary.append("\n\n###############    SUMMARY OF SUB DIVISION TREE INTEGRATION RUN    ###############");
		summary.append("\n#########   TOTAL NUMBER OF RECORDS RECEIVED\t\t---------->   ").append(SMSubdivisionDataRequestWebClient.getTotalCount());
		summary.append("\n#########   TOTAL NUMBER OF SUCCESSFUL UPDATES\t\t---------->   ").append(SMSubDivisionTreeDataProcessor.getUpdateCount());
		summary.append("\n#########   TOTAL NUMBER OF SUCCESSFUL CREATES\t\t---------->   ").append(SMSubDivisionTreeDataProcessor.getCreateCount());
		summary.append("\n#########   TOTAL NUMBER OF SUCCESSFUL DELETES\t\t---------->   ").append(SMSubDivisionTreeDataProcessor.getDeleteCount());
		summary.append("\n#########   TOTAL NUMBER OF FAILED RECORDS\t\t---------->   ").append(SMSubDivisionTreeDataProcessor.getFailedCount());
		summary.append("\n#####################################################################################");
		LOGGER.debug(summary);
		//Reset all counts
		SMSubDivisionTreeDataProcessor.setUpdateCount(ZERO);
		SMSubDivisionTreeDataProcessor.setFailedCount(ZERO);
		SMSubDivisionTreeDataProcessor.setDeleteCount(ZERO);
		SMSubDivisionTreeDataProcessor.setCreateCount(ZERO);
	}
	
	
	/**
	 * Flags if response has missing data.
	 * @param div - Division
	 * @return boolean
	 */
	public static boolean validateResponse(Division div){
		LOGGER.debug("Inside validateResponse method !!!!!!!!");
		if(responseHasAllFields(div)){
			return true;
		}
		else{
			List<String> errorMsg = new ArrayList<String>();
			errorMsg = checkDivisionElements(div, errorMsg);
			SMSubDivisionTreeDataProcessor.setFailedCount(SMSubDivisionTreeDataProcessor.getFailedCount() + 1);
			for(int i=0; i<errorMsg.size();i++){
				if(FormatHelper.hasContent(errorMsg.get(i))){
					LOGGER.error("INVALID RESPONSE !!!!!!!  "+errorMsg.get(i));
					if(!errorMsg.get(i).equals(SMInboundWebserviceConstants.MDM_ID_NOT_PRESENT)){
						SMSubDivisionTreeDataProcessor.setStrErrorString(errorMsg.get(i));
						SMSubDivisionTreeDataProcessor.setINTEGRATIONSTATUS(SMInboundWebserviceConstants.STATUS_NOT_INTEGRATED);
						try {
							SMSubDivisionLogEntryProcessor.setLogEntry(null, div);
						} catch (WTPropertyVetoException e) {
							LOGGER.error(e.getLocalizedMessage());
							e.printStackTrace();
						} catch (WTException wtExp) {
							LOGGER.error(wtExp.getLocalizedMessage());
							wtExp.printStackTrace();
						}
					}
					break;
				}
			}
			return false;
		}
	}
	
	/**
	 * Validates if response has all data.
	 * @param div - Division
	 * @return boolean - Boolean
	 */
	public static boolean responseHasAllFields(Division div){
		LOGGER.debug("Inside responseHasAllFields method !!!!!!!!");
		boolean checkOne = FormatHelper.hasContent(div.getMdmBO()) && FormatHelper.hasContent(div.getMdmCategory());
		boolean checkTwo = FormatHelper.hasContent(div.getMdmClass())&& FormatHelper.hasContent(div.getMdmGom());
		boolean checkThree = FormatHelper.hasContent(div.getMdmSubCategory())&& FormatHelper.hasContent(div.getMdmSubClass())&& FormatHelper.hasContent(div.getRequestType().value());
		if(checkOne && checkTwo && checkThree){
			return true;
		}
		return false;
	}
	
	/**
	 * Flags which element is missing in response
	 * @param div - Division
	 * @param errorMsg - List<String>
	 * @return errorMsg - List<String>
	 */
	public static List<String> checkDivisionElements(Division div, List<String> errorMsg){
			String mdmIDPresent = !FormatHelper.hasContent(div.getMdmBO()) ? SMInboundWebserviceConstants.MDM_ID_NOT_PRESENT : "";
			errorMsg.add(mdmIDPresent);
			String categoryPresent = !FormatHelper.hasContent(div.getMdmCategory()) ? SMInboundWebserviceConstants.CATEGORY_NOT_PRESENT : "";
			errorMsg.add(categoryPresent);
			String classPresent = !FormatHelper.hasContent(div.getMdmClass()) ? SMInboundWebserviceConstants.CLASS_NOT_PRESENT : "";
			errorMsg.add(classPresent);
			String gomPresent = !FormatHelper.hasContent(div.getMdmGom()) ? SMInboundWebserviceConstants.GOM_NOT_PRESENT : "";
			errorMsg.add(gomPresent);
			//String guIDPresent = !FormatHelper.hasContent(div.getMdmGuid()) ? SMInboundWebserviceConstants.GUI_ID_NOT_PRESENT : "";
			//errorMsg.add(guIDPresent);
			String subCategoryPresent = !FormatHelper.hasContent(div.getMdmSubCategory()) ? SMInboundWebserviceConstants.SUB_CATEGORY_NOT_PRESENT : "";
			errorMsg.add(subCategoryPresent);
			String subClassPresent = !FormatHelper.hasContent(div.getMdmSubClass()) ? SMInboundWebserviceConstants.SUB_CLASS_NOT_PRESENT : "";
			errorMsg.add(subClassPresent);
			//String namePresent = !FormatHelper.hasContent(div.getName()) ? SMInboundWebserviceConstants.NAME_NOT_PRESENT : "";
			//errorMsg.add(namePresent);
			String requestTypePresent = !FormatHelper.hasContent(div.getRequestType().value()) ? SMInboundWebserviceConstants.REQUEST_TYPE_NOT_PRESENT : "";
			errorMsg.add(requestTypePresent);
			return errorMsg;
	}

	/**
	 * Returns the map of Business Object\Class.
	 * @return hmClass - Map<String, LCSLifeCycleManaged>
	 */
	public static Map<String, LCSLifecycleManaged> getHmClass() {
		return hmClass;
	}

	/**
	 * Sets value for Collection for Business Object\Class.
	 * @param hmClass - Map<String, LCSLifecycleManaged>
	 */
	public static void setHmClass(Map<String, LCSLifecycleManaged> hmClass) {
		SMSubDivisionTreeUtil.hmClass = hmClass;
	}

	/**
	 * Returns the map of Business Object\Sub_Class.
	 * @return hmClass - Map<String, LCSLifeCycleManaged>
	 */
	public static Map<String, LCSLifecycleManaged> getHmSubClass() {
		return hmSubClass;
	}

	/**
	 * Sets value for Collection for Business Object\Sub_Class.
	 * @param hmSubClass - Map<String, LCSLifecycleManaged>
	 */
	public static void setHmSubClass(Map<String, LCSLifecycleManaged> hmSubClass) {
		SMSubDivisionTreeUtil.hmSubClass = hmSubClass;
	}

	/**
	 * Returns the map of Business Object\Category.
	 * @return hmClass - Map<String, LCSLifeCycleManaged>
	 */
	public static Map<String, LCSLifecycleManaged> getHmCategory() {
		return hmCategory;
	}

	/**
	 * Sets value for Collection for Business Object\Category.
	 * @param hmCategory - Map<String, LCSLifecycleManaged>
	 */
	public static void setHmCategory(Map<String, LCSLifecycleManaged> hmCategory) {
		SMSubDivisionTreeUtil.hmCategory = hmCategory;
	}

	/**
	 * Returns the map of Business Object\Sub Category.
	 * @return hmClass - Map<String, LCSLifeCycleManaged>
	 */
	public static Map<String, LCSLifecycleManaged> getHmSubCategory() {
		return hmSubCategory;
	}

	/**
	 * Sets value for Collection for Business Object\Sub Category.
	 * @param hmSubCategory - Map<String, LCSLifecycleManaged>
	 */
	public static void setHmSubCategory(
			Map<String, LCSLifecycleManaged> hmSubCategory) {
		SMSubDivisionTreeUtil.hmSubCategory = hmSubCategory;
	}

	/**
	 * Returns the map of Business Object\GoM.
	 * @return hmClass - Map<String, LCSLifeCycleManaged>
	 */
	public static Map<String, LCSLifecycleManaged> getHmGoM() {
		return hmGoM;
	}

	/**
	 * Sets value for Collection for Business Object\GoM.
	 * @param hmGoM - Map<String, LCSLifecycleManaged>
	 */
	public static void setHmGoM(Map<String, LCSLifecycleManaged> hmGoM) {
		SMSubDivisionTreeUtil.hmGoM = hmGoM;
	}

	/**
	 * Returns the map of Business Object\Sub Class Division Tree.
	 * @return hmClass - Map<String, LCSLifeCycleManaged>
	 */
	public static Map<String, LCSLifecycleManaged> getHmSubDivTree() {
		return hmSubDivTree;
	}

	/**
	 * Sets value for Collection for Business Object\Sub Class Division Tree.
	 * @param hmSubDivTree - Map<String, LCSLifecycleManaged>
	 */
	public static void setHmSubDivTree(Map<String, LCSLifecycleManaged> map) {
		SMSubDivisionTreeUtil.hmSubDivTree = map;
	}

	/**
	 * Returns the map of Log Entry.
	 * @return hmClass - Map<String, LCSLogEntry>
	 */
	public static Map<String, LCSLogEntry> getHmLogEntryMDMID() {
		return hmLogEntryMDMID;
	}

	/**
	 * Sets value for Collection for Log Entry.
	 * @param hmLogEntryMDMID - Map<String, LCSLifecycleManaged>
	 */
	public static void setHmLogEntryMDMID(Map<String, LCSLogEntry> map) {
		SMSubDivisionTreeUtil.hmLogEntryMDMID = map;
	}

}
