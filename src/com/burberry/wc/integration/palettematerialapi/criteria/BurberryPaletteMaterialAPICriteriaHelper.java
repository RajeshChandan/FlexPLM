package com.burberry.wc.integration.palettematerialapi.criteria;

import java.text.ParseException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.util.BurConstant;
import com.burberry.wc.integration.util.BurberryAPICriteriaUtil;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.flextype.*;
import com.lcs.wc.util.FormatHelper;

/**
 * A Criteria class to handle append Criteria activity. Class contain several
 * method to handle appending criteria collection.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryPaletteMaterialAPICriteriaHelper {

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPaletteMaterialAPICriteriaHelper.class);

	/**
	 * BurberryPaletteMaterialAPICriteriaHelper.
	 */
	private BurberryPaletteMaterialAPICriteriaHelper() {

	}

	/**
	 * Method to get Query Criteria Based on Object.
	 * 
	 * @param objectClass
	 *            objectClass
	 * @param strScope
	 *            strScope
	 * @param strLevel
	 *            strLevel
	 * @param strAttDisplayName
	 *            strAttDisplayName
	 * @param strAttValue
	 *            strAttValue
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 * @throws BurException
	 *             Exception
	 * @throws ParseException
	 *             Exception
	 */
	public static Collection<Map> getQueryCriteria(Class objectClass,
			String strScope, String strLevel, String strAttDisplayName,
			String strAttValue) throws BurException, ParseException,
			WTException {

		String methodName = "getQueryCriteria() ";
		// Initialisation of Flex Type
		FlexType objectFlexType = FlexTypeCache
				.getFlexTypeRootByClass(objectClass.getName());
		logger.debug(methodName + "objectFlexType: " + objectFlexType);

		// Initialisation of collection
		Collection<Map> queryCriteria = new ArrayList<Map>();

		// Step 1: Check if attribute display name is valid
		Collection<FlexTypeAttribute> colAttributes = BurberryAPIUtil
				.getFlexTypeAttribute(objectFlexType, strScope, strLevel,
						strAttDisplayName);
		logger.debug(methodName + "colAttributes: " + colAttributes);

		// Loop through the attribute collection
		for (FlexTypeAttribute flexTypeAttribute : colAttributes) {
			// Step 2: Check for attribute data type
			String dbColumnName = flexTypeAttribute.getColumnName();
			logger.debug(methodName + "Attribute Name: " + strAttDisplayName);
			logger.debug(methodName + "Column Name: " + dbColumnName);
			logger.debug(methodName + "Attribute Value: " + strAttValue);

			// Check if the attribute is material sub type
			if ((BurPaletteMaterialConstant.STR_MATERIAL_SUB_TYPE
					.equalsIgnoreCase(flexTypeAttribute.getAttDisplay()))
					|| (BurPaletteMaterialConstant.STR_MATERIAL_HYPEN_SUB_TYPE
							.equalsIgnoreCase(flexTypeAttribute.getAttDisplay()))) {
				queryCriteria.addAll(appendCriteriaforMaterialSubType(
						objectClass.getSimpleName(), dbColumnName,
						flexTypeAttribute, strAttValue, true));
			} else if ((BurPaletteMaterialConstant.STR_SUPPLIER_SUB_TYPE
					.equalsIgnoreCase(flexTypeAttribute.getAttDisplay()))) {
				queryCriteria.addAll(appendCriteriaforMaterialSubType(
						objectClass.getSimpleName(), dbColumnName,
						flexTypeAttribute, strAttValue, false));
			} else {
				// Step 3: Append criteria based on data type
				queryCriteria.addAll(BurberryAPICriteriaUtil
						.appendCriteriaBasedOnDataType(
								objectClass.getSimpleName(), dbColumnName,
								flexTypeAttribute, strAttValue));
			}
		}

		// if the collection is empty
		if (queryCriteria.isEmpty()) {
			BurberryAPIUtil
					.throwBurException(
							strAttDisplayName,
							BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTEVALUE);
		}
		logger.debug(methodName + "QueryCriteria: " + queryCriteria);
		// Step 4: Return Criteria Collection
		// Return Statement
		return queryCriteria;
	}

	/**
	 * Method for get material sub type attribute key.
	 * 
	 * @param strTableName
	 *            Table Name
	 * @param strColumnName
	 *            Column Name
	 * @param matFlexTypeAttribute
	 *            FlexType
	 * @param strAttValue
	 *            Att Value
	 * @param singleList
	 *            singleList
	 * @return Collection
	 * @throws WTException
	 *             WTException
	 */
	private static Collection<Map> appendCriteriaforMaterialSubType(
			String strTableName, String strColumnName,
			FlexTypeAttribute matFlexTypeAttribute, String strAttValue,
			Boolean singleList) throws WTException {
		String methodName = "appendCriteriaforMaterialSubType() ";
		// Initialisation
		String listkey = BurConstant.STRING_EMPTY;
		Collection<Map> colCriteriaMap = new ArrayList<Map>();
		// Split the values
		final String[] choiceValues = strAttValue.split("\\|");
		logger.debug(methodName + "Att Display Name: "
				+ matFlexTypeAttribute.getAttDisplay());

		// Loop through the input values provided
		for (int i = 0; i < choiceValues.length; i++) {
			logger.debug(methodName + "choiceValues: [" + i + "] ="
					+ choiceValues[i]);
			// Pass the display value and get the list key
			listkey = BurberryAPICriteriaUtil.getKeyFromList(
					matFlexTypeAttribute, choiceValues[i]);
			logger.debug(methodName + "ListKey: " + listkey);
			// If empty then throw not found exception
			HashMap<String, String> criteriaMap = new HashMap<String, String>();
			if (FormatHelper.hasContent(listkey)) {
				// Check if single list or multi-list
				if (singleList) {
					criteriaMap.put(strTableName + "." + strColumnName + "."
							+ "=", listkey);
					colCriteriaMap.add(criteriaMap);
				} else {
					criteriaMap.put(strTableName + "." + strColumnName + "."
							+ "LIKE", "%" + listkey + "%");
					colCriteriaMap.add(criteriaMap);
				}
			}
		}
		logger.debug(methodName + "colCriteriaMap:" + colCriteriaMap);
		// Return Statement
		return colCriteriaMap;
	}
}
