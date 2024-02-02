package com.burberry.wc.integration.sampleapi.criteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTypeAttribute;

import com.burberry.wc.integration.sampleapi.constant.BurSampleConstant;

public final class BurberrySampleAPICriteria {

	
	/**
	 * BurberrySampleAPICriteria.
	 */
	private BurberrySampleAPICriteria() {

	}
	
	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberrySampleAPICriteria.class);

	
	/**
	 * Method to get Sample Flex type attribute.
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
	public static Collection<FlexTypeAttribute> getSampleFlexTypeAttribute(
			String scope, String level, String strAttDisplayName)
			throws WTException, BurException {

		//Method Name
		String methodName = "getSampleFlexTypeAttribute() ";
		
		//Initialisation
		Collection<FlexTypeAttribute> colFlexTypeAttribute = new ArrayList<FlexTypeAttribute>();
		FlexType flexType = null;
		Map<String, String> attMap = new HashMap<String, String>();
		
		logger.debug(methodName + "Scope: " + scope);
		logger.debug(methodName + "Level: " + level);
		logger.debug(methodName + "AttDisplayName: " + strAttDisplayName);
		
		// Check if attribute level is Product Sample
		if ("PRODUCT_SAMPLE".equalsIgnoreCase(level)) {
			//Set Product Sample Flex Type
			flexType = FlexTypeCache
					.getFlexTypeFromPath(BurSampleConstant.PRODUCT_SAMPLE_FLEXTYPE);
			logger.debug(methodName + "Product Sample Flex Type: " + flexType);
			// Get all the attributes associated to flex type in a map
			attMap = FlexType.getAttributeKeyDisplayMap(flexType
					.getAllAttributes(scope, null));
		}
		// Check if attribute level is Material Sample
		else if ("MATERIAL_SAMPLE".equalsIgnoreCase(level)) {
			//Set Material Sample Flex Type
			flexType = FlexTypeCache
					.getFlexTypeFromPath(BurSampleConstant.MATERIAL_SAMPLE_FLEXTYPE);
			logger.debug(methodName + "Material Sample Flex Type: " + flexType);
			// Get all the attributes associated to flex type in a map
			attMap = FlexType.getAttributeKeyDisplayMap(flexType
					.getAllAttributes(scope, null));
		}
		logger.debug(methodName + "Sample Flex Att Map: " + attMap);

		// Loop through each attribute
		for (Map.Entry<String, String> entry : attMap.entrySet()) {
			logger.debug(methodName + "entry: " + entry);
			if (entry.getValue().equalsIgnoreCase(strAttDisplayName)) {
				logger.debug(methodName + "attKey " + entry.getKey() + " type "
						+ flexType.getFullName());
				logger.debug(methodName + "Flex Type Attribute: "
						+ flexType.getAttribute(entry.getKey()));
				// Add to collection
				colFlexTypeAttribute.add(flexType.getAttribute(entry.getKey()));
			}
		}
		logger.debug(methodName + "Collection FlexTypeAttribute: " + colFlexTypeAttribute);
		
		// Return Statement
		return colFlexTypeAttribute;
	}

}
