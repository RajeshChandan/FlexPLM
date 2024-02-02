package com.burberry.wc.integration.productcostingapi.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.util.BurConstant;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.sourcing.LCSProductCostSheet;

public final class BurberryProductCostingAPICriteria {

	/**
	 * BurberryProductCostingAPICriteria.
	 */
	private BurberryProductCostingAPICriteria() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductCostingAPICriteria.class);

	/**
	 * Method to get Costing Flex type attribute.
	 * 
	 * @param flexType
	 *            FlexType
	 * @param strAttDisplayName
	 *            name
	 * @return flex type attribute
	 * @throws WTException
	 *             exception
	 * @throws BurException
	 *             exception
	 */
	public static Collection<FlexTypeAttribute> getCostingFlexTypeAttribute(
			String scope, String level, String strAttDisplayName)
			throws WTException, BurException {

		// Method Name
		String methodName = "getCostingFlexTypeAttribute() ";

		// Initialisation
		Collection<FlexTypeAttribute> colFlexTypeAttribute = new ArrayList<FlexTypeAttribute>();

		logger.debug(methodName + "Cost Scope: " + scope);
		logger.debug(methodName + "Cost Level: " + level);
		logger.debug(methodName + "Display Name: " + strAttDisplayName);

		// Initialisation
		final FlexType costSheetFlexType = FlexTypeCache
				.getFlexTypeRootByClass((LCSProductCostSheet.class).getName());

		logger.debug(methodName + "costSheetFlexType: " + costSheetFlexType);
		logger.debug(methodName + "Cost Sheet Attribute Name: "
				+ strAttDisplayName);

		// Get all the child flex type nodes
		Collection<FlexType> colChildFlexTypes = costSheetFlexType
				.getAllCreatableChildren();

		// Loop through each child flex type
		for (FlexType childFlexType : colChildFlexTypes) {

			Map<String, String> attMap = childFlexType
					.getAttributeKeyDisplayMap(childFlexType.getAllAttributes());

			// Initialisation
			Collection<FlexTypeAttribute> colCostingFlexTypeAttribute = new ArrayList<FlexTypeAttribute>();

			// Loop through each attribute
			for (Map.Entry<String, String> entry : attMap.entrySet()) {
				logger.debug(methodName + "entry: " + entry);
				if (entry.getValue().equalsIgnoreCase(strAttDisplayName)) {
					logger.debug(methodName + "attKey " + entry.getKey()
							+ " type " + childFlexType.getFullName());
					logger.debug(methodName + "Flex Type Attribute: "
							+ childFlexType.getAttribute(entry.getKey()));
					// Add to collection
					colCostingFlexTypeAttribute.add(childFlexType
							.getAttribute(entry.getKey()));
				}
			}
			// Add all to collection
			colFlexTypeAttribute.addAll(colCostingFlexTypeAttribute);
		}
		
		if (colFlexTypeAttribute.isEmpty()) {
			BurberryAPIUtil.throwBurException(strAttDisplayName,
					BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTE);
		}
		logger.debug(methodName + "Collection FlexTypeAttribute: "
				+ colFlexTypeAttribute);
		// Return Statement
		return colFlexTypeAttribute;
	}

}
