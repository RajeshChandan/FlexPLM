package com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import org.apache.log4j.Logger;

import com.sportmaster.wc.interfaces.webservices.bean.Division;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.util.SMSubDivisionTreeUtil;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.client.SMSubdivisionDataRequestWebClient;
import com.sportmaster.wc.interfaces.webservices.inbound.subdivisiontree.helper.SMSubDivisionHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.utill.SMInboundWebserviceConstants;


/**
 * SMSubDivisionTreeDataProcessor.java
 * This class is used to process data for Business Object.
 * @author 'true' ITC_Infotech
 * Processing of Sub Division tree
 * @version 'true' 1.0
 */
public class  SMSubDivisionTreeDataProcessor {

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSubDivisionTreeDataProcessor.class);
	/**
	 * Initializing Failed Count.
	 */
	private static int failedCount;
	/**
	 * Initializing Updates Count.
	 */
	private static int updateCount;
	/**
	 * Initializing Creates Count.
	 */
	private static int createCount;
	/**
	 * Initializing Deletes Count.
	 */
	private static int deleteCount;
	/**
	 * Initializing Flag.
	 */
	private static boolean persistSuccessful;
	/**
	 * ERROR STRING DECLARATION.
	 */
	private static String strErrorString;
	/**
	 * INTEGRATION STATUS DECLARATION.
	 */
	private static String integrationStatus;
	
	/**
	 * declaring Constructor.
	 */
	protected SMSubDivisionTreeDataProcessor(){
		
	}
	
	/**
	 * Populates HashMaps.
	 */
	public static void subDivisionTreeProcessor()
	{
			LOGGER.debug("Inside subDivisionTreeProcessor !!!!!!");
			try{
				//Initializing businessObject Type
				//FlexType businessObjType = FlexTypeCache.getFlexTypeFromPath(SMInboundWebserviceConstants.strSubDivTreePath);
				//Getting the flex attribute
				//Getting all Business Objects List Values objects from BO
				Map<String, Map<String, LCSLifecycleManaged>> businessObjectMap = SMSubDivisionTreeUtil.getAllListValues(SMInboundWebserviceConstants.LIST_VALUES_INTEGRATION_TYPES,SMInboundWebserviceConstants.strMDMID);
				
				//Querying subdivision tree objects from BO
				SMSubDivisionTreeUtil.setHmSubDivTree(querySubDivisionTreeObject(SMInboundWebserviceConstants.strSubDivTreePath));
				//Getting all Business Object/Class objects from BO
				SMSubDivisionTreeUtil.setHmClass(businessObjectMap.get(SMInboundWebserviceConstants.BO_CLASS.toLowerCase()));
				//Getting all Business Object/Sub Class objects from BO
				SMSubDivisionTreeUtil.setHmSubClass(businessObjectMap.get(SMInboundWebserviceConstants.BO_SUBCLASS.toLowerCase()));
				//Getting all Business Object/Category objects from BO
				SMSubDivisionTreeUtil.setHmCategory(businessObjectMap.get(SMInboundWebserviceConstants.BO_CATEGORY.toLowerCase()));
				//Getting all Business Object/Sub Category objects from BO
				SMSubDivisionTreeUtil.setHmSubCategory(businessObjectMap.get(SMInboundWebserviceConstants.BO_SUBCATEGORY.toLowerCase()));
				//Getting all Business Object/GOM objects from BO
				SMSubDivisionTreeUtil.setHmGoM(businessObjectMap.get(SMInboundWebserviceConstants.BO_GOM.toLowerCase()));
				//Getting all LOG Entry/Inbound/Sub Class Division Tree objects from BO
				SMSubDivisionTreeUtil.setHmLogEntryMDMID(SMSubDivisionLogEntryProcessor.queryLogEntry(SMInboundWebserviceConstants.strLogEntryPath, SMInboundWebserviceConstants.LOG_ENTRY_MDMID_KEY));
				
				//Calling Web Client
				SMSubdivisionDataRequestWebClient.subDivClientRequest(SMSubDivisionTreeUtil.getHmSubDivTree());
			}catch(WTException ex){
				LOGGER.error("ERROR in SUB DIVISON !!" + ex.getLocalizedMessage());
				ex.printStackTrace();
			} 
	}
		
	/**
	 * Queries the Business Object/Sub Class Division Tree.
	 * @param flexPath - String
	 * @throws WTException - WTException
	 * @returns HashMap<String, LCSLifeCycleManaged>
	 */
	public static Map<String, LCSLifecycleManaged> querySubDivisionTreeObject(String flexPath) throws WTException{
		//Hash Map to store Sub Division Tree objects with key as MDMID
		Map<String, LCSLifecycleManaged> hashMapSubDivTreeObj=new HashMap<String, LCSLifecycleManaged>();
		LOGGER.debug("Inside querySubDivisionTreeObject Method !!");
		com.lcs.wc.flextype.FlexType boType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(flexPath);
		com.lcs.wc.db.PreparedQueryStatement statement = new com.lcs.wc.db.PreparedQueryStatement();//Creating Statement.
		statement.appendFromTable(LCSLifecycleManaged.class);
		statement.appendSelectColumn("LCSLifecycleManaged", "IDA2A2");//appending columns
		//adding criteria
		statement.appendCriteria(new Criteria(SMInboundWebserviceConstants.LCSLIFECYCLE_CLASS_NAME, "flexTypeIdPath", boType.getIdPath(),Criteria.EQUALS));
		com.lcs.wc.db.SearchResults results = null;
		//executing  statement
		results =LCSQuery.runDirectQuery(statement);
		List<?> dataCollection= results.getResults();
		LOGGER.debug("Data collection Size >>>>\t"+dataCollection.size());
		FlexObject fo=null;
		if (dataCollection.size() > 0) {
			//iterating collection
			for(Object obj:dataCollection){
				fo= (FlexObject) obj;
				//LOGGER.debug("flexObject >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>  "+fo);
				LCSLifecycleManaged lcm=(LCSLifecycleManaged) LCSQuery.findObjectById("com.lcs.wc.foundation.LCSLifecycleManaged:"+fo.getString("LCSLIFECYCLEMANAGED.IDA2A2"));
				//Adding object to Hashmap
				hashMapSubDivTreeObj.put((String)lcm.getValue(SMInboundWebserviceConstants.strMDMID), lcm);
			}	
		}
		return (HashMap<String, LCSLifecycleManaged>) hashMapSubDivTreeObj;
	}
		
	/**
	* Updates the Business Object\Sub Class Division Tree.
	* @param subDivTreeObj - LCSLifecycleManaged
	* @param div - Division
	* @throws WTPropertyVetoException - WTPropertyVetoException
	* @throws WTException - WTException
	*/	
	public static void updateSubDivisionTreeObject(LCSLifecycleManaged subDivTreeObj, Division div) throws WTPropertyVetoException, WTException{
		LOGGER.debug("REQUEST TYPE >>> \tUPDATE");
		LOGGER.debug("MDM ID      >>>>  \t"+div.getMdmBO());
		//validating data
		if(SMSubDivisionHelper.validateAllFields(div)){
			//if validation successful
			//set data for the object
			subDivTreeObj.setValue(SMInboundWebserviceConstants.strCategoryName, SMSubDivisionTreeUtil.getHmCategory().get(div.getMdmCategory()));
			subDivTreeObj.setValue(SMInboundWebserviceConstants.strClassName, SMSubDivisionTreeUtil.getHmClass().get(div.getMdmClass()));
			subDivTreeObj.setValue(SMInboundWebserviceConstants.strGomName, SMSubDivisionTreeUtil.getHmGoM().get(div.getMdmGom()));
			subDivTreeObj.setValue(SMInboundWebserviceConstants.strSubClassName, SMSubDivisionTreeUtil.getHmSubClass().get(div.getMdmSubClass()));
			subDivTreeObj.setValue(SMInboundWebserviceConstants.strSubCategoryName, SMSubDivisionTreeUtil.getHmSubCategory().get(div.getMdmSubCategory()));
			//subDivTreeObj.setValue(SMInboundWebserviceConstants.strGuiID, div.getMdmGuid());
			subDivTreeObj.setValue(SMInboundWebserviceConstants.strStatus, SMInboundWebserviceConstants.STATUS_ACTIVE);
			if(SMSubDivisionHelper.peristSubDivTree(subDivTreeObj)){
				//increment the number of update count
				updateCount+=1;
			}
			strErrorString = SMInboundWebserviceConstants.NO_ERROR;
			integrationStatus = SMInboundWebserviceConstants.STATUS_INTEGRATED;
			//setting log entry data
			SMSubDivisionLogEntryProcessor.setLogEntry(subDivTreeObj, div);
			LOGGER.debug("Sub Division Tree Object Updated !!!!!!!!!! \nSUB DIVISION TREE OBJECT >>>>>>>>>>>>\t"+subDivTreeObj.getName());
		}else{
			//increment failed count if validation fails
			setFailedCount(getFailedCount() + 1);
		}
	}
		
	/**
	 * Creates new objects for Business Object\Sub Class Division Tree.
	 * @param div - Division
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void createSubDivisionTreeObject(Division div) throws WTPropertyVetoException, WTException{
		LOGGER.debug("REQUEST TYPE >>> \tUPDATE");
		LOGGER.debug("MDM ID      >>>>  \t"+div.getMdmBO());
		//validating data
		if(SMSubDivisionHelper.validateAllFields(div)){
			//if validation successful
			//create new object
			//set data for the object
			com.lcs.wc.flextype.FlexType boType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMInboundWebserviceConstants.strSubDivTreePath);
			LCSLifecycleManaged subDivTreeObj = LCSLifecycleManaged.newLCSLifecycleManaged();
			subDivTreeObj.setFlexType(boType);
			subDivTreeObj.setValue(SMInboundWebserviceConstants.strMDMID, div.getMdmBO());
			subDivTreeObj.setValue(SMInboundWebserviceConstants.strCategoryName, SMSubDivisionTreeUtil.getHmCategory().get(div.getMdmCategory()));
			subDivTreeObj.setValue(SMInboundWebserviceConstants.strClassName, SMSubDivisionTreeUtil.getHmClass().get(div.getMdmClass()));
			subDivTreeObj.setValue(SMInboundWebserviceConstants.strGomName, SMSubDivisionTreeUtil.getHmGoM().get(div.getMdmGom()));
			subDivTreeObj.setValue(SMInboundWebserviceConstants.strSubClassName, SMSubDivisionTreeUtil.getHmSubClass().get(div.getMdmSubClass()));
			subDivTreeObj.setValue(SMInboundWebserviceConstants.strSubCategoryName, SMSubDivisionTreeUtil.getHmSubCategory().get(div.getMdmSubCategory()));
			//subDivTreeObj.setValue(SMInboundWebserviceConstants.strGuiID, div.getMdmGuid());
			subDivTreeObj.setValue(SMInboundWebserviceConstants.strStatus, SMInboundWebserviceConstants.STATUS_ACTIVE);
			if(SMSubDivisionHelper.peristSubDivTree(subDivTreeObj)){
				//increment create count
				createCount+=1;
			}
			//Add the MDMID to HashMap of Sub Class division Tree
			SMSubDivisionTreeUtil.getHmSubDivTree().put(div.getMdmBO(), subDivTreeObj);
			LOGGER.debug("Sub Division Tree Object Created. OBJECT NAME >>>>\t"+subDivTreeObj.getName());
			strErrorString = SMInboundWebserviceConstants.NO_ERROR;
			integrationStatus = SMInboundWebserviceConstants.STATUS_INTEGRATED;
			//setting log entry object
			SMSubDivisionLogEntryProcessor.setLogEntry(subDivTreeObj,div);
		}else{
			//if validation fails
			//increment failed count
			setFailedCount(getFailedCount() + 1);
		}
	}
		
	/**
	 * Updates the status attribute for Business Object\Sub Class Division Tree.
	 * @param subDivTreeObj - LCSLifeCycleManaged
	 * @throws WTException - WTException
	 */
	public static void deleteSubDivisionTree(LCSLifecycleManaged subDivTreeObj, Division div) throws WTException, WTPropertyVetoException{
		//validate object
		if(SMSubDivisionHelper.validateAllFields(div)){
			LOGGER.debug("INSIDE DELETE SUB DIVISION TREE METHOD !!!!!!");
			LOGGER.debug("REQUEST TYPE >>> \tDELETE");
			LOGGER.debug("MDM ID      >>>>  \t"+div.getMdmBO());
			//change status to inactive
			subDivTreeObj.setValue(SMInboundWebserviceConstants.strStatus, SMInboundWebserviceConstants.STATUS_INACTIVE);
			if(SMSubDivisionHelper.peristSubDivTree(subDivTreeObj)){
				//increment delete count
				deleteCount+=1;
			}
			strErrorString = SMInboundWebserviceConstants.NO_ERROR;
			integrationStatus = SMInboundWebserviceConstants.STATUS_INTEGRATED;
			//set log entry
			SMSubDivisionLogEntryProcessor.setLogEntry(subDivTreeObj,div);
		}else{
			//if validation fails
			//increment failed count
			setFailedCount(getFailedCount() + 1);
		}
	}

	/**
	 * Returns the total failed counts.
	 * @return failedCount - Integer
	 */
	public static int getFailedCount() {
		return failedCount;
	}

	/**
	 * Sets the failed count for a run.
	 * @param failedCount - Integer
	 */
	public static void setFailedCount(int failedCount) {
		SMSubDivisionTreeDataProcessor.failedCount = failedCount;
	}

	/**
	 * Returns the total number of successful updates.
	 * @return updateCount - Integer
	 */
	public static int getUpdateCount() {
		return updateCount;
	}

	/**
	 * Sets the total number of successful updates.
	 * @param updateCount - Integer
	 */
	public static void setUpdateCount(int updateCount) {
		SMSubDivisionTreeDataProcessor.updateCount = updateCount;
	}

	/**
	 * Returns the total successful creates.
	 * @return createCount - Integer
	 */
	public static int getCreateCount() {
		return createCount;
	}

	/**
	 * Sets the total number of successful creates.
	 * @param createCount - Integer
	 */
	public static void setCreateCount(int createCount) {
		SMSubDivisionTreeDataProcessor.createCount = createCount;
	}

	/**
	 * Returns the total successful deletes.
	 * @return deleteCount - Integer
	 */
	public static int getDeleteCount() {
		return deleteCount;
	}

	/**
	 * Sets the total successful deletes.
	 * @param deleteCount - Integer
	 */
	public static void setDeleteCount(int deleteCount) {
		SMSubDivisionTreeDataProcessor.deleteCount = deleteCount;
	}

	/**
	 * Returns status of object save.
	 * @return persistSuccessful - boolean
	 */
	public static boolean isPersistSuccessful() {
		return persistSuccessful;
	}

	/**
	 * Sets status of Object save.
	 * @param persistSuccessful - boolean
	 */
	public static void setPersistSuccessful(boolean persistSuccessful) {
		SMSubDivisionTreeDataProcessor.persistSuccessful = persistSuccessful;
	}

	/**
	 * Returns the error String.
	 * @return strErrorString - String
	 */
	public static String getStrErrorString() {
		return strErrorString;
	}

	/**
	 * Sets the error String.
	 * @param strErrorString - String
	 */
	public static void setStrErrorString(String strErrorString) {
		SMSubDivisionTreeDataProcessor.strErrorString = strErrorString;
	}

	/**
	 * Returns the Integration status.
	 * @return integrationStatus - String
	 */
	public static String getINTEGRATIONSTATUS() {
		return integrationStatus;
	}

	/**
	 * Sets the Integration status.
	 * @param integrationStatus - String
	 */
	public static void setINTEGRATIONSTATUS(String iNTEGRATIONSTATUS) {
		integrationStatus = iNTEGRATIONSTATUS;
	}	
		
}
