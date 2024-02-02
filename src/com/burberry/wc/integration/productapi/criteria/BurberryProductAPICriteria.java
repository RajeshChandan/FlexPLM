package com.burberry.wc.integration.productapi.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.util.BurConstant;
import com.burberry.wc.integration.util.BurberryAPICriteriaUtil;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FootwearApparelFlexTypeScopeDefinition;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;

/**
 * A Criteria Helper class to get criteria for prepared query.
 *
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryProductAPICriteria {

	/**
	 * STR_EQUAL.
	 */
	private static final String STR_EQUAL = "=";

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductAPICriteria.class);

	/**
	 * ProductAPICriteria.
	 */
	private BurberryProductAPICriteria() {

	}

	/**
	 * Method to get Product flex type attribute.
	 * 
	 * @param flexType
	 *            FlexType
	 * @param attributeScope
	 *            name
	 * @param attributeLevel
	 *            attributeLevel
	 * @param strAttDisplayName
	 *            strAttDisplayName
	 * @return flex type attribute
	 * @throws WTException
	 *             exception
	 * @throws BurException
	 *             exception
	 */
	public static Collection<FlexTypeAttribute> getProductFlexTypeAttribute(
			FlexType flexType, String attributeScope, String attributeLevel,
			String strAttDisplayName) throws WTException, BurException {
		String methodName = "getProductFlexTypeAttribute() ";
		Collection<FlexTypeAttribute> colProductFlexTypeAttribute = new ArrayList<FlexTypeAttribute>();
		HashMap<String, String> attMap = FlexType
				.getAttributeKeyDisplayMap(flexType.getAllAttributes(attributeScope, null));

		logger.debug(methodName + "Product Att Map: " + attMap);
		for (Map.Entry<String, String> entry : attMap.entrySet()) {
			logger.debug(methodName + "entry: " + entry);
			// Check Attribute Scope and Attribute Display Name
			if ((flexType.getAttribute(entry.getKey())
							.getAttObjectLevel()
							.equalsIgnoreCase(attributeLevel) || flexType
							.getAttribute(entry.getKey())
							.getAttObjectLevel()
							.equalsIgnoreCase(
									FootwearApparelFlexTypeScopeDefinition.PRODUCT_SKU_LEVEL))
					&& entry.getValue().equalsIgnoreCase(strAttDisplayName)) {
				logger.debug(methodName + "attKey: " + entry.getKey()
						+ " type: " + flexType.getFullName() + " attDisplay: "
						+ entry.getValue());
				colProductFlexTypeAttribute.add(flexType.getAttribute(entry
						.getKey()));
				
			}
		}
		// Check for additional product apparel attributes from property file
		logger.debug(methodName
				+ "Check for additional attributes from property file..");
		colProductFlexTypeAttribute
				.addAll(getProductApparelAttributes(strAttDisplayName));

		logger.debug(methodName + "colProductFlexTypeAttribute: "
				+ colProductFlexTypeAttribute);

		if (colProductFlexTypeAttribute.isEmpty()) {
			BurberryAPIUtil.throwBurException(strAttDisplayName,
					BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTE);
		}
		return colProductFlexTypeAttribute;
	}

	/**
	 * Method getProductApparelAttributes.
	 * 
	 * @param strAttDisplayName
	 *            name
	 * @return collection
	 * @throws WTException
	 *             exception
	 */
	private static Collection<FlexTypeAttribute> getProductApparelAttributes(
			String strAttDisplayName) throws WTException {
		String methodName = "getProductApparelAttributes() ";
		Collection<FlexTypeAttribute> colProductFlexTypeAttribute = new ArrayList<FlexTypeAttribute>();

		Map<String, String> mapDuplicateProductAtt = new HashMap<String, String>();
		String prodAtts[] = BurConstant.STR_PRODUCT_DUPLICATE_APPAREL_ATT
				.split("\\|");
		for (String value : prodAtts) {
			logger.debug(methodName + "value: " + value);
			String temp[] = value.split("~");
			logger.debug(methodName + "temp[0]: " + temp[0] + " = temp[1]:"
					+ temp[1]);
			mapDuplicateProductAtt.put(temp[0], temp[1]);
		}
		logger.debug(methodName + "mapDuplicateProductAtt: "
				+ mapDuplicateProductAtt);

		logger.debug(methodName + "strAttDisplayName: " + strAttDisplayName);

		if (mapDuplicateProductAtt.containsKey(strAttDisplayName)) {
			String strStringKey = mapDuplicateProductAtt.get(strAttDisplayName);
			logger.debug(methodName + "strStringKey: " + strStringKey);

			// if (strStringKey.contains(",")) {
			String temp[] = strStringKey.split(",");
			for (String value : temp) {
				logger.debug(methodName + " value:" + value);
				String strPath = LCSProperties
						.get(BurConstant.STR_PRODUCT_ALIAS_APPAREL_ATT + value);
				logger.debug(methodName + "strPath: " + strPath);
				FlexType productFlexType = FlexTypeCache
						.getFlexTypeFromPath(strPath);
				FlexTypeAttribute fta = productFlexType.getAttribute(value);
				colProductFlexTypeAttribute.add(fta);
			}
			// } else {
			// FlexType apparelProductFlexType = FlexTypeCache
			// .getFlexTypeFromPath("Product\\Apparel");
			// FlexTypeAttribute fta = apparelProductFlexType
			// .getAttribute(strStringKey);
			// colProductFlexTypeAttribute.add(fta);
			// }

		}

		logger.debug(methodName + "colProductFlexTypeAttribute: "
				+ colProductFlexTypeAttribute);

		// TODO Auto-generated method stub
		return colProductFlexTypeAttribute;
	}

	/**
	 * Method getMaterialFlexTypeAttribute.
	 * 
	 * @param materialFlexType
	 *            flex type
	 * @param strAttDisplayName
	 *            name
	 * @return collection
	 * @throws WTException
	 *             exception
	 * @throws BurException
	 *             exception
	 */
	public static Collection<FlexTypeAttribute> getMaterialFlexTypeAttribute(
			FlexType materialFlexType, String strAttDisplayName)
			throws WTException, BurException {
		String methodName = "getMaterialFlexTypeAttribute() ";
		Collection<FlexTypeAttribute> colMaterialFlexTypeAttribute = new ArrayList<FlexTypeAttribute>();
		HashMap<String, String> attMap = FlexType
				.getAttributeKeyDisplayMap(materialFlexType.getAllAttributes());
		logger.debug(methodName + "Material Att Map: " + attMap);

		logger.debug(methodName + "strAttDisplayName: " + strAttDisplayName);

		for (Map.Entry<String, String> entry : attMap.entrySet()) {
			logger.debug(methodName + "entry: " + entry);
			if (entry.getValue().equalsIgnoreCase(strAttDisplayName)) {
				logger.debug(methodName + "attKey " + entry.getKey() + " type "
						+ materialFlexType.getFullName());
				colMaterialFlexTypeAttribute.add(materialFlexType
						.getAttribute(entry.getKey()));
			}
		}
		// Check for additional material attributes from property file
		logger.debug(methodName
				+ "Check for additional attributes from property file..");
		colMaterialFlexTypeAttribute
				.addAll(getAdditionalMaterialAttributes(strAttDisplayName));

		logger.debug(methodName + "colMaterialFlexTypeAttribute: "
				+ colMaterialFlexTypeAttribute);

		if (colMaterialFlexTypeAttribute.isEmpty()) {
			BurberryAPIUtil.throwBurException(strAttDisplayName,
					BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTE);
		}

		return colMaterialFlexTypeAttribute;
	}

	/**
	 * @param strAttDisplayName
	 *            name
	 * @return collection
	 * @throws WTException
	 *             exception
	 */
	private static Collection<FlexTypeAttribute> getAdditionalMaterialAttributes(
			String strAttDisplayName) throws WTException {
		String methodName = "getAdditionalMaterialAttributes() ";
		Collection<FlexTypeAttribute> colMaterialFlexTypeAttribute = new ArrayList<FlexTypeAttribute>();

		Map<String, String> mapDuplicateMaterialAtt = new HashMap<String, String>();
		String matAtts[] = BurConstant.STR_MATERIAL_DUPLICATE_ATT.split("\\|");
		for (String value : matAtts) {
			logger.debug(methodName + "value2: " + value);
			String temp[] = value.split("~");
			logger.debug(methodName + "temp2[0]: " + temp[0] + " = temp2[1]:"
					+ temp[1]);
			mapDuplicateMaterialAtt.put(temp[0], temp[1]);
		}
		logger.debug(methodName + "mapDuplicateMaterialAtt: "
				+ mapDuplicateMaterialAtt);

		logger.debug(methodName + "strAttDisplayName2: " + strAttDisplayName);

		if (mapDuplicateMaterialAtt.containsKey(strAttDisplayName)) {
			String strStringKey = mapDuplicateMaterialAtt
					.get(strAttDisplayName);
			logger.debug(methodName + "strStringKey: " + strStringKey);

			String temp[] = strStringKey.split(",");
			for (String value : temp) {
				logger.debug(methodName + " Value:" + value);
				String strFlexTypePath = LCSProperties
						.get(BurConstant.STR_MATERIAL_ALIAS_ATT + value);
				String slFlexTypePath[] = strFlexTypePath.split(",");
				for (String strPath : slFlexTypePath) {
					logger.debug(methodName + "strPath: " + strPath);
					FlexType materialFlexType = FlexTypeCache
							.getFlexTypeFromPath(strPath);
					FlexTypeAttribute fta = materialFlexType
							.getAttribute(value);
					colMaterialFlexTypeAttribute.add(fta);
				}
			}
		}
		logger.debug(methodName + "colMaterialFlexTypeAttribute: "
				+ colMaterialFlexTypeAttribute);
		return colMaterialFlexTypeAttribute;
	}

	/**
	 * Method appendCriteriaforOperationalCategory.
	 * 
	 * @param strTableName
	 *            name
	 * @param strColumnName
	 *            column
	 * @param flexTypeAtt
	 *            flextype
	 * @param strAttValue
	 *            attvalue
	 * @return collection
	 * @throws WTException
	 *             exception
	 * @throws BurException
	 *             exception
	 */
	public static Collection<Map> appendCriteriaforOperationalCategory(
			String strTableName, String strColumnName,
			FlexTypeAttribute flexTypeAtt, String strAttValue)
			throws WTException, BurException {

		String methodName = "appendCriteriaforOperationalCategory() ";
		Collection<Map> colcriteria = new ArrayList<Map>();

		// Operational Category - Product
		FlexType productFlexType = FlexTypeCache.getFlexTypeFromPath("Product");
		// Get Flex Type Attribute
		FlexTypeAttribute ftaProduct = productFlexType
				.getAttribute(BurConstant.BUR_OPERATIONAL_CATEGORY);
		// Split the values
		String[] choiceValues = strAttValue.split("\\|");
		// Looping through choice values
		for (int i = 0; i < choiceValues.length; i++) {
			logger.debug(methodName + "productValues: [" + i + "] ="
					+ choiceValues[i]);
			String listkey = BurConstant.STRING_EMPTY;
			listkey = BurberryAPICriteriaUtil.getKeyFromList(ftaProduct,
					choiceValues[i]);
			logger.debug(methodName + " list key:" + listkey);
			if (FormatHelper.hasContent(listkey)) {
				HashMap<String, String> criteriaMap = new HashMap<String, String>();
				criteriaMap.put(strTableName + "." + ftaProduct.getColumnName()
						+ "." + STR_EQUAL, listkey);
				colcriteria.add(criteriaMap);
			}
		}

		// Operational Category - Apparel
		FlexType apparelProductFlexType = FlexTypeCache
				.getFlexTypeFromPath("Product\\Apparel");
		FlexTypeAttribute ftaApparel = apparelProductFlexType
				.getAttribute(BurConstant.BUR_OPERATIONAL_CATEGORY_APP);
		// Looping through choice values
		for (int i = 0; i < choiceValues.length; i++) {
			logger.debug(methodName + "apparelValues: [" + i + "] ="
					+ choiceValues[i]);
			String listkey = BurConstant.STRING_EMPTY;
			listkey = BurberryAPICriteriaUtil.getKeyFromList(ftaApparel,
					choiceValues[i]);
			logger.debug(methodName + " list key:" + listkey);
			if (FormatHelper.hasContent(listkey)) {
				HashMap<String, String> criteriaMap = new HashMap<String, String>();
				criteriaMap.put(strTableName + "." + ftaApparel.getColumnName()
						+ "." + STR_EQUAL, listkey);
				colcriteria.add(criteriaMap);
			}
		}
		// Check if collection is empty
		if (colcriteria.isEmpty()) {
			BurberryAPIUtil
					.throwBurException(
							flexTypeAtt.getAttDisplay() + " has",
							BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTEVALUE);
		}
		return colcriteria;

	}
}
