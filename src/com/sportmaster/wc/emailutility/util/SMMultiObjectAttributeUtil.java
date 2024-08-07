package com.sportmaster.wc.emailutility.util;

import java.io.IOException;
import java.util.*;
import org.apache.log4j.Logger;

import com.lcs.wc.db.*;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLifecycleManagedQuery;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;

import wt.fc.WTObject;
import wt.org.WTUser;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

public class SMMultiObjectAttributeUtil {

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMMultiObjectAttributeUtil.class);

	/**
	 * CLASS_NAME.
	 */
	private static final String CLASS_NAME = "SMMultiObjectAttributeUtil";
	
	/**
	 * METHOD_START.
	 */
	public static final String METHOD_START = "--Start";
	/**
	 * METHOD_END.
	 */
	public static final String METHOD_END = "--End";


	/**
	 * To get the flexplm url
	 */
	private static String FLEXPLM_URL = "";

	/**
	 * MOAOBJECTLOCATION.
	 */
	public static final String MOAOBJECTLOCATION = LCSProperties.get("com.lcs.wc.moa.LCSMOAObject.rootFolder",
			"/MOAObject");

	/**
	 * Method Name: insertMOARow();
	 * 
	 * @param businessObject
	 * @param strBOMOAAttributeKey
	 * @param strMOAFlexTypePath
	 * @param attributeDataMap
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void insertMOARow(LCSLifecycleManaged businessObject, String strBOMOAAttributeKey,
			String strMOAFlexTypePath, Map<String, String> attributeDataMap)
			throws WTException, WTPropertyVetoException {
		String methodName = "insertMOARow()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Create new MOA Row
		LCSMOAObject moaRow = LCSMOAObject.newLCSMOAObject();
		// Get MOA Flex Type
		FlexType moaFlexTyped = FlexTypeCache.getFlexTypeFromPath(strMOAFlexTypePath);
		// Set MOA Flex Type
		moaRow.setFlexType(moaFlexTyped);
		// Set BO Owner
		moaRow.setOwner(businessObject);
		// Set Role
		moaRow.setRole(strBOMOAAttributeKey);
		// Setting attributes values
		// Loop through each map and get key and value
		for (Map.Entry<String, String> mapEntry : attributeDataMap.entrySet()) {
			// Get attribute key
			String key = mapEntry.getKey();
			// Get attribute value
			String value = mapEntry.getValue();
			if (FormatHelper.hasContent(key) && FormatHelper.hasContent(value)) {
				LOGGER.debug(CLASS_NAME + "--" + methodName + "--MOA Attribute Key: " + key
						+ " || MOA Attribute Value: " + value);
				moaRow.setValue(key, value);
			}
		}
		// Persist the MOA Object
		LCSMOAObjectLogic.assignFolder(MOAOBJECTLOCATION, moaRow);
		// Persist MOA
		LCSMOAObjectLogic.persist(moaRow);
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * Method: getMOAPreparedQueryStatement().
	 * 
	 * @param String
	 * @param String
	 * @param Map<String, String>
	 * @return
	 * @throws WTException
	 */
	public static PreparedQueryStatement getMOAPreparedQueryStatement(String strBOMOAAttributeKey,
			String strMOAFlexType, Map<String, String> criteriaMap) throws WTException {
		String methodName = "getMOAPreparedQueryStatement()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Get MOA Table Flex Type
		FlexType moaFlexType = FlexTypeCache.getFlexTypeFromPath(strMOAFlexType);
		// Get MOA Table Flex Type Id Path
		String moaFlexTypeIdPath = moaFlexType.getTypeIdPath();
		// Initialization of prepared query statement
		PreparedQueryStatement statement = new PreparedQueryStatement();
		// Append Table
		statement.appendFromTable(LCSMOAObject.class);
		// Append Select Column
		statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "role"));
		statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "ownerReference.key.classname"));
		statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "ownerReference.key.id"));
		statement.appendSelectColumn(new QueryColumn(LCSMOAObject.class, "ownerVersion"));
		// Append Default Search Criteria
		statement.appendAndIfNeeded();
		statement.appendCriteria(
				new Criteria(new QueryColumn(LCSMOAObject.class, "effectOutDate"), "", Criteria.IS_NULL));
		// Append Default Search Criteria
		statement.appendAndIfNeeded();
		statement.appendCriteria(
				new Criteria(new QueryColumn(LCSMOAObject.class, "dropped"), "1", Criteria.NOT_EQUAL_TO));
		// Append Role Search Criteria
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria("LCSMOAOBJECT", "ROLE", strBOMOAAttributeKey, Criteria.EQUALS));
		// Append FlexTypeIdPath Search Criteria
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSMOAObject.class, "flexTypeIdPath"), moaFlexTypeIdPath,
				Criteria.EQUALS));

		// Loop through each map and get key and value
		for (Map.Entry<String, String> mapEntry : criteriaMap.entrySet()) {
			// Get attribute key
			String key = mapEntry.getKey();
			// Get attribute value
			String value = mapEntry.getValue();
			if (FormatHelper.hasContent(key) && FormatHelper.hasContent(value)) {
				statement.appendAndIfNeeded();
				statement.appendCriteria(new Criteria(
						new QueryColumn(LCSMOAObject.class, moaFlexType.getAttribute(key).getColumnDescriptorName()),
						"?", Criteria.EQUALS), value);
			}
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
		// Return
		return statement;
	}

	/**
	 * Method Name: deleteMOACollection.
	 * 
	 * @param seasonObject  LCSSeason
	 * @param productObject LCSProduct
	 * @param skuObject     LCSSKU
	 * @throws WTException
	 */
	public static void deleteMOACollection(Collection<WTObject> colMOARowData) throws WTException {
		String methodName = "deleteMOACollection()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Loop through existing MOA row
		for (WTObject moaRow : colMOARowData) {
			try {
				LOGGER.debug("Delete MOA row = " + moaRow);
				// Delete MOA row
				LCSMOAObjectLogic.deleteObject(moaRow);
			} catch (WTException ex) {
				ex.printStackTrace();
			}
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * Method Name: findBusinessObjectByName.
	 * 
	 * @param boFlexType
	 * @param boName
	 * @return
	 * @throws WTException
	 */
	public static LCSLifecycleManaged findBusinessObjectByName(String boFlexType, String boName) throws WTException {
		String methodName = "findBusinessObjectByName()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Initialization
		LCSLifecycleManaged boFPDColorwayCreate;
		// Get Flex Type for Business Object
		FlexType flexType = FlexTypeCache.getFlexTypeFromPath(boFlexType);
		String columnName = flexType.getAttribute("name").getSearchCriteriaIndex();
		// Initialization
		Map<String, String> mapSearchObject = new HashMap<>();
		// Criteria Map
		mapSearchObject.put(columnName, boName);
		// Initialization Iterator
		Iterator iterator = new LCSLifecycleManagedQuery()
				.findLifecycleManagedsByCriteria(mapSearchObject, flexType, null, null, null).getResults().iterator();
		// Loop through iterator
		if (iterator.hasNext()) {
			// Get Flex Object
			FlexObject fob = (FlexObject) iterator.next();
			// Get Business Object
			boFPDColorwayCreate = (LCSLifecycleManaged) LCSQuery.findObjectById(
					"OR:com.lcs.wc.foundation.LCSLifecycleManaged:" + fob.getString("LCSLIFECYCLEMANAGED.IDA2A2"));
		} else {
			LOGGER.info(CLASS_NAME + "--" + methodName + "--BUSINESS OBJECT NOT FOUND!!");
			throw new WTException("Business object " + boName + " dont exsist");
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
		// Return Business Object
		return boFPDColorwayCreate;
	}

	/**
	 * Method to construct the flexplm url to provide link for Product Name in email
	 * table
	 * 
	 * @return
	 */
	public static String viewFlexPLMURL(String strLevel) {
		String methodName = "viewFlexPLMURL()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		try {
			if (FormatHelper.hasContent(strLevel) && "PRODUCT".equalsIgnoreCase(strLevel)) {
				FLEXPLM_URL = WTProperties.getServerProperties().getProperty("wt.server.codebase")
						+ "/rfa/jsp/main/Main.jsp?newWindowActivity=VIEW_PRODUCT&newWindowOid=VR%3Acom.lcs.wc.product.LCSProduct%3A";
			} else {
				//Defect Fix: SMPLM-1344 - Start
				FLEXPLM_URL = WTProperties.getServerProperties().getProperty("wt.server.codebase")
						+ "/rfa/jsp/main/Main.jsp?newWindowActivity=VIEW_SEASON_PRODUCT_LINK&tabPage=PRODUCT&newWindowOid=VR%3Acom.lcs.wc.product.LCSSKU%3A";
				//Defect Fix: SMPLM-1344 - End
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("IOException in constructFlexPLMURL -" + e.getLocalizedMessage());
			// e.printStackTrace();
		}
		LOGGER.debug("FLEXPLM_URL==" + FLEXPLM_URL);
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
		return FLEXPLM_URL;
	}
	
	/**
	 * Method to get the user list attribute value and flexobject
	 * 
	 * @param wtObj
	 * @param att
	 * @param keyTaskNo
	 * @param strUserToExclude
	 * @return
	 */
	public static HashMap getUserAssignedMap(WTObject wtObj, String att) {
		LOGGER.debug("## SMMultiObjectAttributeUtil.getUserAssignedMap method - START ####");
		//List<Object> userList = new ArrayList<Object>();
		HashMap hmUserList = new HashMap();
		FlexObject fo = null;
		String userAttName = "";
		WTUser user = null;

		try {
			if (wtObj instanceof LCSSeasonProductLink) {
				fo = (FlexObject) ((LCSSeasonProductLink) wtObj).getValue(att);
			}
			if (fo != null && fo.containsKey(SMEmailUtilConstants.FO_NAME)
					&& FormatHelper.hasContent((String) fo.getData(SMEmailUtilConstants.FO_NAME))) {
				userAttName = (String) fo.getData(SMEmailUtilConstants.FO_NAME);
				LOGGER.debug("## userAttName=="+userAttName);
				if (FormatHelper.hasContent(userAttName)) {
					try {
						user = wt.org.OrganizationServicesHelper.manager.getUser(userAttName);
						LOGGER.debug("## user=="+user);
						//Check disabled user
						if(user != null && !user.isDisabled()) {
							hmUserList.put(userAttName, user);
						}
					} catch (WTException e) {
						LOGGER.error("## WTException in SMMultiObjectAttributeUtil.getUserAssignedMap method - " + e.getMessage());
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (WTException e1) {
			LOGGER.error("## WTException in SMMultiObjectAttributeUtil.getUserAssignedMap method - " + e1.getMessage());
			e1.printStackTrace();
		}

		LOGGER.debug("## SMMultiObjectAttributeUtil.getUserAssignedMap method - END ####");
		return hmUserList;
	}
}
