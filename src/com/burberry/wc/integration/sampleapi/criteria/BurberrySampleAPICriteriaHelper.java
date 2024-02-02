package com.burberry.wc.integration.sampleapi.criteria;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.util.BurConstant;
import com.burberry.wc.integration.util.BurberryAPICriteriaUtil;
import com.lcs.wc.flextype.FlexTypeAttribute;

public final class BurberrySampleAPICriteriaHelper {

	/**
	 * BurberrySampleAPICriteriaHelper.
	 */
	private BurberrySampleAPICriteriaHelper() {

	}
	
	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberrySampleAPICriteriaHelper.class);

	/**
	 * Method to get Query Criteria Based on Object.
	 * 
	 * @param strAttDisplayName
	 *            Display Name
	 * @param strAttValue
	 *            Att Value
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

		String methodName = "Sample API: getQueryCriteria() ";
		
		logger.debug(methodName + "strScope: " + strScope);
		logger.debug(methodName + "strLevel: " + strLevel);
		logger.debug(methodName + "strAttDisplayName: " + strAttDisplayName);
		logger.debug(methodName + "strAttValue: " + strAttValue);
		
		// Initialisation of collection
		Collection<Map> sampleAPIQueryCriteria = new ArrayList<Map>();

		// Step 1: Check if attribute display name is valid
		Collection<FlexTypeAttribute> colAttributes = BurberrySampleAPICriteria
				.getSampleFlexTypeAttribute(strScope, strLevel,
						strAttDisplayName);
		logger.debug(methodName + "Sample API: colAttributes: " + colAttributes);

		// Check if collection is empty
		if (colAttributes.isEmpty()) {
			throwBurException(strAttDisplayName,
					BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTE);
		}

		// Loop through the attribute collection
		for (FlexTypeAttribute flexTypeAttribute : colAttributes) {
			// Step 2: Check for attribute data type
			String dbColumnName = flexTypeAttribute.getColumnName();

			logger.debug(methodName + "Sample API: Attribute Name: "
					+ strAttDisplayName);
			logger.debug(methodName + "Sample API: Column Name: " + dbColumnName);
			logger.debug(methodName + "Sample API: Attribute Value: "
					+ strAttValue);

			// Step 3: Append criteria based on data type
			sampleAPIQueryCriteria.addAll(BurberryAPICriteriaUtil
					.appendCriteriaBasedOnDataType(objectClass.getSimpleName(),
							dbColumnName, flexTypeAttribute, strAttValue));
		}
		logger.debug(methodName + "SampleAPI: QueryCriteria: " + sampleAPIQueryCriteria);
		// Step 4: Return Criteria Collection
		// Return Statement
		return sampleAPIQueryCriteria;
	}

	/**
	 * Method to throw exception.
	 * 
	 * @param param
	 *            string and message
	 * @param msg
	 * @throws BurException
	 */
	public static void throwBurException(String param, String msg)
			throws BurException {
		throw new BurException(" " + param + " " + msg);
	}

}
