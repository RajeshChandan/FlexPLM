package com.burberry.wc.integration.planningapi.criteria;

import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.util.BurConstant;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.flextype.*;
import com.lcs.wc.planning.FlexPlan;

/**
 * A Criteria Helper class to get criteria for prepared query.
 *
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryPlanningAPICriteria {

	/**
	 * BurberryProductCostingAPICriteria.
	 */
	private BurberryPlanningAPICriteria() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPlanningAPICriteria.class);

	/**
	 * @param flexType
	 * @param planScope
	 * @param strAttDisplayName
	 * @return
	 * @throws WTException
	 * @throws BurException
	 */
	public static Collection<FlexTypeAttribute> getPlanningFlexTypeAttribute(
			FlexType flexType, String planScope, String strAttDisplayName)
			throws WTException, BurException {
		// Method Name
		String methodName = "getPlanningFlexTypeAttribute() ";

		// Initialisation
		Collection<FlexTypeAttribute> colFlexTypeAttribute = new ArrayList<FlexTypeAttribute>();

		logger.debug(methodName + "plan Scope: " + planScope);
		logger.debug(methodName + "Display Name: " + strAttDisplayName);

		// Initialisation
		final FlexType planFlexType = FlexTypeCache
				.getFlexTypeRootByClass((FlexPlan.class).getName());

		logger.debug(methodName + "planFlexType: " + planFlexType);
		logger.debug(methodName + "Plan Attribute Name: " + strAttDisplayName);

		// Get all the child flex type nodes
		Collection<FlexType> colChildFlexTypes = planFlexType
				.getAllCreatableChildren();

		// Loop through each child flex type
		for (FlexType childFlexType : colChildFlexTypes) {
			Map<String, String> attMap = childFlexType
					.getAttributeKeyDisplayMap(childFlexType.getAllAttributes(planScope,null));
			// Initialisation
			Collection<FlexTypeAttribute> colPlanningFlexTypeAttribute = new ArrayList<FlexTypeAttribute>();
			// Loop through each attribute
			for (Map.Entry<String, String> entry : attMap.entrySet()) {
				logger.debug(methodName + "entry: " + entry);
				if (entry.getValue().equalsIgnoreCase(strAttDisplayName)) {
					logger.debug(methodName + "attKey " + entry.getKey()
							+ " type " + childFlexType.getFullName());
					logger.debug(methodName + "Flex Type Attribute: "
							+ childFlexType.getAttribute(entry.getKey()));
					// Add to collection
					colPlanningFlexTypeAttribute.add(childFlexType
							.getAttribute(entry.getKey()));
				}
			}
			// Add all to collection
			colFlexTypeAttribute.addAll(colPlanningFlexTypeAttribute);
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
