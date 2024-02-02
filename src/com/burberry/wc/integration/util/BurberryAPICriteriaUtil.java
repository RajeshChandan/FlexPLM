package com.burberry.wc.integration.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;

import wt.content.*;
import wt.fc.ObjectReference;
import wt.fv.StoredItem;
import wt.org.WTGroup;
import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.palettematerialapi.db.BurberryPaletteMaterialAPIDBHelper;
import com.burberry.wc.integration.planningapi.db.BurberryPlanningAPIDBHelper;
import com.burberry.wc.integration.productapi.constant.BurProductConstant;
import com.burberry.wc.integration.productapi.db.BurberryProductAPIDBHelper;
import com.burberry.wc.integration.sampleapi.db.BurberrySampleAPIDBHelper;
import com.google.common.collect.Lists;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.*;
import com.lcs.wc.foundation.FindByCriteriaQuery;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.*;

/**
 * Utility class for all APIs.
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */

public final class BurberryAPICriteriaUtil {

	/**
	 * Default Constructor.
	 */
	private BurberryAPICriteriaUtil() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryAPICriteriaUtil.class);
	
	/**
	 * WTGROUP_PREFIX.
	 */
	private static final String WTGROUP_PREFIX = "OR:wt.org.WTGroup:";
	
	/**
	 * STR_EQUAL.
	 */
	private static final String STR_EQUAL = "=";

	/**
	 * STR_LIKE.
	 */
	private static final String STR_LIKE = "LIKE";
	/**
	 * STR_TO_DATE.
	 */
	private static final String STR_TO_DATE = "TO_DATE";

	/**
	 * Method to append criteria based on data type.
	 * 
	 * @param strTableName
	 *            name
	 * @param strColumnName
	 *            column
	 * @param flexTypeAtt
	 *            att
	 * @param strAttValue
	 *            value
	 * @return collection
	 * @throws BurException
	 *             exception
	 * @throws ParseException
	 *             exception
	 * @throws WTException
	 *             Exception
	 */
	public static Collection<Map> appendCriteriaBasedOnDataType(
			String strTableName, String strColumnName,
			FlexTypeAttribute flexTypeAtt, String strAttValue)
			throws BurException, ParseException, WTException {

		String methodName = "appendCriteriaBasedOnDataType() ";

		Collection<Map> colCriteriaMap = new ArrayList<Map>();

		String strAttKey = flexTypeAtt.getAttKey();
		logger.debug(methodName + "AttKey: " + strAttKey);
		String strAttVariableType = flexTypeAtt.getAttVariableType();
		logger.debug(methodName + "AttVariableType: " + strAttVariableType);
		logger.debug(methodName + "Attribute Value: " + strAttValue);

		// Text Type attribute
		if (BurConstant.DERIVED_STRING.equalsIgnoreCase(strAttVariableType)
				|| BurConstant.TEXT.equalsIgnoreCase(strAttVariableType)
				|| BurConstant.TEXT_AREA.equalsIgnoreCase(strAttVariableType)) {
			Map<String, String> criteriaMap = new HashMap<String, String>();
			criteriaMap.put(strTableName + "." + strColumnName + "."
					+ STR_EQUAL, strAttValue);
			colCriteriaMap.add(criteriaMap);
		}

		// Currency, Integer, Float and Sequence Type Attribute
		else if (BurConstant.CURRENCY.equalsIgnoreCase(strAttVariableType)
				|| BurConstant.INTEGER.equalsIgnoreCase(strAttVariableType)
				|| BurConstant.FLOAT.equalsIgnoreCase(strAttVariableType)
				|| BurConstant.SEQUENCE.equalsIgnoreCase(strAttVariableType)) {
			Map<String, String> criteriaMap = new HashMap<String, String>();
			criteriaMap = getCriteriaMapForNumber(strTableName, strColumnName,
					flexTypeAtt, strAttValue);
			colCriteriaMap.add(criteriaMap);
		}

		// Boolean Type Attribute
		else if (BurConstant.BOOLEAN.equalsIgnoreCase(strAttVariableType)) {
			// Map<String, String> criteriaMap = new HashMap<String, String>();
			colCriteriaMap = getCriteriaMapForBoolean(strTableName,
					strColumnName, flexTypeAtt, strAttValue);
		}
		// Date Type Attribute
		else if (BurConstant.DATE.equalsIgnoreCase(strAttVariableType)) {
			Map<String, Object> criteriaMap = new HashMap<String, Object>();
			criteriaMap = getCriteriaMapForDate(strTableName, strColumnName,
					flexTypeAtt, strAttValue);
			colCriteriaMap.add(criteriaMap);
		}

		else {
			colCriteriaMap = getCriteriaForSpecialAttributeTypes(strTableName,
					strColumnName, flexTypeAtt, strAttValue);
		}

		logger.debug(methodName + "colCriteriaMap: " + colCriteriaMap);
		// Return Statement
		return colCriteriaMap;
	}

	/**
	 * @param strTableName
	 * @param strColumnName
	 * @param flexTypeAtt
	 * @param strAttValue
	 * @return
	 * @throws BurException
	 * @throws WTException
	 */
	private static Collection<Map> getCriteriaForSpecialAttributeTypes(
			String strTableName, String strColumnName,
			FlexTypeAttribute flexTypeAtt, String strAttValue)
			throws BurException, WTException {

		String methodName = "getCriteriaForSpecialAttributeTypes() ";
		String strAttVariableType = flexTypeAtt.getAttVariableType();
		logger.debug(methodName + "AttVariableType1: " + strAttVariableType);

		// Object Reference Attribute
		if (BurConstant.OBJECT_REF.equalsIgnoreCase(strAttVariableType)) {
			Collection<Map> colCriteriaMap = getCriteriaMapForObjectReference(
					strTableName, strColumnName, flexTypeAtt, strAttValue);
			logger.debug(methodName + "colCriteriaMap object reference: "
					+ colCriteriaMap);
			return colCriteriaMap;
		}

		else if (BurConstant.COMPOSITE.equalsIgnoreCase(strAttVariableType)) {
			Collection<Map> colCriteriaMap = getCriteriaMapForComposite(
					strTableName, strColumnName, flexTypeAtt, strAttValue);
			logger.debug(methodName + "colCriteriaMap object reference: "
					+ colCriteriaMap);
			return colCriteriaMap;
		}
		// Commented this method as part of defect fix: End
		// Single List and Multi List
		else {
			Collection<Map> colCriteriaMap = getCriteriaForListAttributeType(
					strTableName, strColumnName, flexTypeAtt, strAttValue);
			logger.debug(methodName + "colCriteriaMap list: " + colCriteriaMap);
			return colCriteriaMap;
		}
	}

	/**
	 * Method to create criteria map for Object reference
	 * 
	 * @param strTableName
	 *            table name
	 * @param strColumnName
	 *            column name
	 * @param flexTypeAtt
	 *            flex type att
	 * @param strAttValue
	 *            att value
	 * @return map
	 * @throws BurException
	 *             exception
	 * @throws WTException
	 */
	private static Collection<Map> getCriteriaMapForObjectReference(
			String strTableName, String strColumnName,
			FlexTypeAttribute flexTypeAtt, String strAttValue)
			throws BurException, WTException {

		String methodName = "getCriteriaMapForObjectReference() ";
		// Initialisation
		Collection<Map> criteriaMapList = new ArrayList<Map>();

		// Create flex type based on the ref type
		FlexType refFlexType = FlexTypeCache.getFlexTypeRootByClass(flexTypeAtt
				.getRefType().getTypeClass());
		logger.debug(methodName + "Reference Flex Type: " + refFlexType);

		// Get type name of ref type
		String refTypeName = refFlexType.getTypeName().toUpperCase()
				.replaceAll("\\s+", BurConstant.STRING_EMPTY);
		logger.debug(methodName + "Reference Type Name: " + refTypeName);

		// Get table name of ref type
		String refTableName = refFlexType.getTypeTableName().toUpperCase();
		logger.debug(methodName + "Reference Table Name: " + refTableName);

		// Get attribute name of ref type
		String refAttributeName = flexTypeAtt.getRefDefinition()
				.getAttInternalName();
		logger.debug(methodName + "Reference Attribute Name: "
				+ refAttributeName);

		// Get column name of ref type
		String refColumnName = refFlexType.getAttribute(refAttributeName)
				.getSearchCriteriaIndex();
		logger.debug(methodName + "Reference Column Name: " + refColumnName);

		// Create criteria map
		Map<String, String> mapSearchObject = new HashMap<String, String>();
		mapSearchObject.put(refColumnName, strAttValue);
		logger.debug(methodName + "mapSearchObject: " + mapSearchObject);

		// Defect Fix Start: LCSSKU
		if ("skuName".equalsIgnoreCase(refAttributeName)) {
			refTypeName = "SKU";
		}
		// Defect Fix End: LCSSKU

		// Search for object using criteria map
		List<FlexObject> colFlexObject = new FindByCriteriaQuery()
				.findByCriteria(refTypeName, mapSearchObject, refFlexType,
						null, null, null).getResults();

		logger.debug(methodName + "Results: " + colFlexObject);
		logger.debug(methodName + "Result Size: " + colFlexObject.size());

		// Check if collection object is empty
		if (!colFlexObject.isEmpty()) {
			// Loop through each flex object
			for (FlexObject fob : colFlexObject) {
				// String strObjectId = BurConstant.STRING_EMPTY;
				// Based on Object/Version Reference get ID
				String strObjectId = BurConstant.STRING_EMPTY;
				// Defect Fix Start: LCSSKU
				if (!"skuName".equalsIgnoreCase(refAttributeName)) {
					strObjectId = fob.getString(refTableName + "."
							+ flexTypeAtt.getRefDefinition().getRefColumn());
				}
				// Defect Fix End: LCSSKU
				else {
					strObjectId = fob.getString("LCSSKU.BRANCHIDITERATIONINFO");
				}
				logger.debug(methodName + "Reference Object/Version ID: "
						+ strObjectId);
				HashMap<String, String> criteriaMap = new HashMap<String, String>();
				criteriaMap.put(strTableName + "." + strColumnName + "."
						+ STR_EQUAL, strObjectId);
				logger.debug(methodName + "Criteria Map: " + criteriaMap);
				criteriaMapList.add(criteriaMap);
			}
		} else {
			BurberryAPIUtil
					.throwBurException(
							flexTypeAtt.getAttDisplay() + "=" + strAttValue,
							BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTEVALUE);
		}
		return criteriaMapList;
	}

	/**
	 * Method to Get Criteria For List AttributeType.
	 * 
	 * @param strTableName
	 *            Table Name
	 * @param strColumnName
	 *            Column Name
	 * @param flexTypeAtt
	 *            FLex Type Attr
	 * @param strAttValue
	 *            Value
	 * @return Collection<Map> Collection Map
	 * @throws BurException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 */
	private static Collection<Map> getCriteriaForListAttributeType(
			String strTableName, String strColumnName,
			FlexTypeAttribute flexTypeAtt, String strAttValue)
			throws BurException, WTException {

		Collection<Map> criteriaMapList = new ArrayList<Map>();
		String methodName = "getCriteriaForListAttributeType() ";

		// Single List Attribute
		if (BurConstant.CHOICE.equalsIgnoreCase(flexTypeAtt
				.getAttVariableType())
				|| BurConstant.DRIVEN.equalsIgnoreCase(flexTypeAtt
						.getAttVariableType())) {
			logger.debug(methodName + "CHOICE OR DRIVE: " + strAttValue);
			criteriaMapList = getCriteriaMapForList(strTableName,
					strColumnName, flexTypeAtt, strAttValue, true);
		}
		// Multi List Attribute
		else if (BurConstant.MOA_LIST.equalsIgnoreCase(flexTypeAtt
				.getAttVariableType())
				|| BurConstant.MOA_ENTRY.equalsIgnoreCase(flexTypeAtt
						.getAttVariableType())) {
			logger.debug(methodName + "MOA_LIST: " + strAttValue);
			criteriaMapList = getCriteriaMapForList(strTableName,
					strColumnName, flexTypeAtt, strAttValue, false);
		}
		// User List Attribute
		else if (BurConstant.USER_LIST.equalsIgnoreCase(flexTypeAtt
				.getAttVariableType())) {
			logger.debug(methodName + "USER_LIST: " + strAttValue);
			criteriaMapList = getCriteriaMapForUserList(strTableName,
					strColumnName, flexTypeAtt, strAttValue);

		}
		logger.debug(methodName + "criteriaMapList: " + criteriaMapList);
		// Return Statement
		return criteriaMapList;
	}

	/**
	 * @param strTableName
	 * @param strColumnName
	 * @param flexTypeAtt
	 * @param strAttValue
	 * @return
	 * @throws BurException
	 * @throws WTException
	 */
	private static Collection<Map> getCriteriaMapForUserList(
			String strTableName, String strColumnName,
			FlexTypeAttribute flexTypeAtt, String strAttValue)
			throws BurException, WTException {

		Collection<Map> criteriaMapList = new ArrayList<Map>();
		String groupNames = flexTypeAtt.getAttUserListGroups();
		List<FlexObject> colFlexObject = new ArrayList<FlexObject>();
		if (FormatHelper.hasContent(groupNames)) {
			Collection<String> flexUserListGroups = MOAHelper
					.getMOACollection(groupNames);
			Collection<WTGroup> wtGroups = LCSQuery.getObjectsFromCollection(
					flexUserListGroups, WTGROUP_PREFIX);
			for (WTGroup wtGroup : wtGroups) {
				colFlexObject
						.addAll(UserCache.getGroupUsers(wtGroup.getName()));
			}
		} else {
			colFlexObject.addAll(UserCache.getUsers());
		}
		if (!colFlexObject.isEmpty()) {
			// Loop through each flex object
			for (FlexObject fob : colFlexObject) {
				if (fob.getString(BurConstant.NAM)
						.equalsIgnoreCase(strAttValue)) {
					String strObjectId = fob.getString(BurConstant.OID);
					HashMap<String, String> criteriaMap = new HashMap<String, String>();
					criteriaMap.put(strTableName + "." + strColumnName + "."
							+ STR_EQUAL, strObjectId);
					criteriaMapList.add(criteriaMap);
				}
			}
		} else {
			BurberryAPIUtil
					.throwBurException(
							flexTypeAtt.getAttDisplay() + "=" + strAttValue,
							BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTEVALUE);
		}
		return criteriaMapList;
	}

	/**
	 * Method to get criteria map for single list attribute type. /**
	 * 
	 * @param strTableName
	 *            Table Name
	 * @param strColumnName
	 *            Column Name
	 * @param flexTypeAtt
	 *            Flex Type Attribute
	 * @param strAttValue
	 *            Att Value
	 * @param singleList
	 *            boolean
	 * @return collection
	 * @throws BurException
	 *             exception
	 * @throws WTException
	 *             exception
	 */
	private static Collection<Map> getCriteriaMapForList(String strTableName,
			String strColumnName, FlexTypeAttribute flexTypeAtt,
			String strAttValue, Boolean singleList) throws BurException,
			WTException {

		String methodName = "getCriteriaMapForList() ";
		// Initialisation
		String listkey = BurConstant.STRING_EMPTY;
		Collection<Map> colCriteriaMap = new ArrayList<Map>();
		final String[] choiceValues = strAttValue.split("\\|");

		logger.debug(methodName + "Att Display Name: "
				+ flexTypeAtt.getAttDisplay());

		// Loop through the input values provided
		for (int i = 0; i < choiceValues.length; i++) {
			logger.debug(methodName + "choiceValues: [" + i + "] ="
					+ choiceValues[i]);
			// Pass the display value and get the list key
			listkey = getKeyFromList(flexTypeAtt, choiceValues[i]);
			logger.debug(methodName + "ListKey: " + listkey);
			// If empty then throw not found exception
			if (!FormatHelper.hasContent(listkey)) {
				BurberryAPIUtil
						.throwBurException(
								flexTypeAtt.getAttDisplay() + "="
										+ choiceValues[i],
								BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTEVALUE);
			}
			HashMap<String, String> criteriaMap = new HashMap<String, String>();
			// Check if single list or multi-list
			if (singleList) {
				criteriaMap.put(strTableName + "." + strColumnName + "."
						+ STR_EQUAL, listkey);
				colCriteriaMap.add(criteriaMap);
			} else {
				criteriaMap.put(strTableName + "." + strColumnName + "."
						+ STR_LIKE, "%" + listkey + "%");
				colCriteriaMap.add(criteriaMap);
			}
		}
		logger.debug(methodName + "colCriteriaMap:" + colCriteriaMap);
		// Return Statement
		return colCriteriaMap;
	}

	/**
	 * Method to get key from the list.
	 * 
	 * @param flexTypeAtt
	 *            Flex Type Att
	 * @param displayValue
	 *            Key Display Value
	 * @return String
	 * @throws WTException
	 */
	public static String getKeyFromList(FlexTypeAttribute flexTypeAtt,
			String displayValue) throws WTException {
		String methodName = "getKeyFromList() ";
		final AttributeValueList attValueList = flexTypeAtt.getAttValueList();
		FlexType parenttype = flexTypeAtt.getFlexTypeViaCache();
		String attkey = flexTypeAtt.getAttKey();
		Collection<FlexType> childTypes = parenttype.getAllCreatableChildren();
		// Defect Fix: Start: Get only selectable list keys: Reverted Back -
		// Config Issue
		// final Collection<String> attValues = flexTypeAtt.getAttValueList()
		// .getSelectableKeys(null, false);
		// Defect Fix: End: Get only selectable list keys: Reverted Back -
		// Config Issue
		final Collection<String> attValues = flexTypeAtt.getAttValueList()
				.getKeys();

		logger.debug(methodName + "attValues: " + attValues);
		// Loop through actual attribute list values
		for (final String key : attValues) {
			logger.debug(methodName + "key: " + key);
			// Get the attribute key display name
			final String value = attValueList.getValue(key, null);
			logger.debug(methodName + "value: " + value);
			// Compare the attribute list key display name with the
			// input values provide
			if (value.equalsIgnoreCase(displayValue)) {
				// Return Statement
				return key;
			}
		}
		for (FlexType childType : childTypes) {
			FlexTypeAttribute childAtt = childType.getAttribute(attkey);
			final AttributeValueList childattValueList = childAtt
					.getAttValueList();
			final Collection<String> childattValues = childAtt
					.getAttValueList().getKeys();
			for (final String key : childattValues) {
				logger.debug(methodName + childType.getFullName() + "key: "
						+ key);
				// Get the attribute key display name
				final String value = childattValueList.getValue(key, null);
				logger.debug(methodName + "value: " + value);
				// Compare the attribute list key display name with the
				// input values provide
				if (value.equalsIgnoreCase(displayValue)) {
					// Return Statement
					return key;
				}
			}
		}

		// Return Statement
		return BurConstant.STRING_EMPTY;
	}

	/**
	 * Method to get criteria map for date.
	 * 
	 * @param strTableName
	 *            table name
	 * @param strColumnName
	 *            column name
	 * @param flexTypeAtt
	 *            flextype
	 * @param strAttValue
	 *            value
	 * @return map
	 * @throws ParseException
	 *             exception
	 * @throws BurException
	 *             exception
	 */
	private static Map<String, Object> getCriteriaMapForDate(
			String strTableName, String strColumnName,
			FlexTypeAttribute flexTypeAtt, String strAttValue)
			throws ParseException, BurException {

		String methodName = "getCriteriaMapForDate() ";
		logger.debug(methodName + "String Date Value: " + strAttValue);
		Map<String, Object> criteriaMap = new HashMap<String, Object>();

		logger.debug(methodName + "Date String Value: " + strAttValue);
		// Check date format
		if (strAttValue.length() != 19) {
			BurberryAPIUtil.throwBurException(flexTypeAtt.getAttDisplay() + "="
					+ strAttValue,
					BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_DATE);
		}

		final SimpleDateFormat sf = new SimpleDateFormat(BurConstant.dateFormat);
		sf.setLenient(false);
		Date date = sf.parse(strAttValue);
		logger.debug(methodName + "Date Date Value: " + date);
		criteriaMap.put(strTableName + "." + strColumnName + "." + STR_TO_DATE,
				date);

		logger.debug(methodName + "criteriaMap1: " + criteriaMap);
		// Return Statement
		return criteriaMap;
	}

	/**
	 * Method to get criteria for boolean attribute.
	 * 
	 * @param strTableName
	 *            name
	 * @param strColumnName
	 *            column
	 * @param flexTypeAtt
	 *            flex type
	 * @param strAttValue
	 *            value
	 * @return map
	 * @throws BurException
	 *             exception
	 */
	private static Collection<Map> getCriteriaMapForBoolean(
			String strTableName, String strColumnName,
			FlexTypeAttribute flexTypeAtt, String strAttValue)
			throws BurException {
		String methodName = "getCriteriaMapForBoolean() ";
		logger.debug(methodName + "String Boolean Value: " + strAttValue);

		Collection<Map> colCriteriaBooleanMap = new ArrayList<Map>();
		Map<String, String> criteriaMap = new HashMap<String, String>();
		if ("Yes".equalsIgnoreCase(strAttValue)) {
			criteriaMap.put(strTableName + "." + strColumnName + "."
					+ STR_EQUAL, "1");
		}
		// Defect Fix: Start
		else if ("No".equalsIgnoreCase(strAttValue)) {
			criteriaMap.put(strTableName + "." + strColumnName + "."
					+ "IS_NULL", "0");
		}
		// Defect Fix: End
		else {
			BurberryAPIUtil.throwBurException(flexTypeAtt.getAttDisplay() + "="
					+ strAttValue,
					BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_BOOLEAN);
		}
		colCriteriaBooleanMap.add(criteriaMap);
		logger.debug(methodName + "Boolean Attribute Criteria Map: "
				+ criteriaMap);
		// Return Statement
		return colCriteriaBooleanMap;
	}

	/**
	 * Method to get criteria for number.
	 * 
	 * @param strTableName
	 *            name
	 * @param strColumnName
	 *            column
	 * @param flexTypeAtt
	 *            flextype
	 * @param strAttValue
	 *            value
	 * @return map
	 * @throws BurException
	 *             exception
	 */
	private static Map<String, String> getCriteriaMapForNumber(
			String strTableName, String strColumnName,
			FlexTypeAttribute flexTypeAtt, String strAttValue)
			throws BurException {

		String methodName = "getCriteriaMapForNumber() ";
		logger.debug(methodName + "String Number Value: " + strAttValue);

		Map<String, String> criteriaMap = new HashMap<String, String>();

		logger.debug(methodName + "String Number: " + strAttValue);

		if (FormatHelper.isNumber(strAttValue)) {
				criteriaMap.put("Round(" + strTableName + "." + strColumnName
						+ "," + flexTypeAtt.getAttDecimalFigures() + ")" + "."
						+ STR_EQUAL, strAttValue);
		} else {
			BurberryAPIUtil
					.throwBurException(
							flexTypeAtt.getAttDisplay() + "=" + strAttValue,
							BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTEVALUE);
		}
		logger.debug(methodName + "criteriaMap3: " + criteriaMap);
		// Return Statement
		return criteriaMap;
	}

	/**
	 * Method to create criteria for composite attribute
	 * 
	 * @param strTableName
	 *            name
	 * @param strColumnName
	 *            column name
	 * @param flexTypeAtt
	 *            flex type
	 * @param strAttValue
	 *            value
	 * @return collection
	 * @throws BurException
	 *             exception
	 * @throws LCSException
	 *             exception
	 */
	private static Collection<Map> getCriteriaMapForComposite(
			String strTableName, String strColumnName,
			FlexTypeAttribute flexTypeAtt, String strAttValue)
			throws LCSException {

		String methodName = "getCriteriaMapForComposite() ";

		final AttributeValueList attValueList = flexTypeAtt.getAttValueList();
		logger.debug(methodName + "attValueList: " + attValueList);
		final Collection<String> attValues = flexTypeAtt.getAttValueList()
				.getKeys();
		logger.debug(methodName + "attValues: " + attValues);
		logger.debug(methodName + "compositeAttValues: " + attValues);

		final String[] choiceValues = strAttValue.split(",");
		Collection<Map> colCriteriaMap = new ArrayList<Map>();
		StringBuilder sbBuilder = new StringBuilder();

		// Loop through the input values provided
		for (int i = 0; i < choiceValues.length; i++) {
			logger.debug("choiceValues[" + i + "] " + choiceValues[i].trim());
			// Loop through actual attribute list values
			for (final String key : attValues) {
				// Get the attribute key display name
				final String value = attValueList.getValue(key, null);
				logger.debug(methodName + "key: " + key + " = value: " + value);
				// Compare the attribute list key display name with the
				// input values provide
				String[] tempValue = choiceValues[i].trim().split(
						BurConstant.STR_PERCENTAGE);
				if (value.equalsIgnoreCase(tempValue[1].trim())) {
					logger.debug(methodName + "compositeName: "
							+ tempValue[1].trim());
					sbBuilder.append((tempValue[0])
							+ BurConstant.STR_PERCENTAGE + " " + key);
					sbBuilder.append(BurConstant.STRING_REG_PATTERN);
					break;
				}
			}
		}

		logger.debug("sbBuilder: " + sbBuilder);
		if (sbBuilder.length() > 0) {
			HashMap<String, String> criteriaMap = new HashMap<String, String>();
			criteriaMap.put(strTableName + "." + strColumnName + "."
					+ STR_EQUAL, sbBuilder.toString());
			colCriteriaMap.add(criteriaMap);
		}
		logger.debug(methodName + "colCriteriaMap2: " + colCriteriaMap);
		return colCriteriaMap;
	}
	
	/**
	 * @param primaryAppData
	 * @return
	 */
	public static String getFileUniqueId(ApplicationData primaryAppData) {
		// CR R26: Start

		String strFileUniqId = "";
		String methodName = "getFileUniqueId() ";
		// Code to access unique file id from vault
		ObjectReference streamedRef = primaryAppData.getStreamData();

		if (streamedRef != null) {
			Streamed streamed = (Streamed) streamedRef.getObject();
			logger.debug(methodName + "streamed: " + streamed);
			if (streamed instanceof StoredItem) {
				logger.debug(methodName + "StoredItem: " + streamed);
				StoredItem sItem = (StoredItem) streamed;
				logger.debug("sItem: " + sItem.getName());
				strFileUniqId = (String) sItem.getName();
			}else{
	            StreamData streamData = (StreamData) streamedRef.getObject();
	            strFileUniqId = StoredItem.buildFileName(streamData.getUniqueSequenceNumber());
			}
		}
		return strFileUniqId;
		// CR R26: End
	}
	
}
