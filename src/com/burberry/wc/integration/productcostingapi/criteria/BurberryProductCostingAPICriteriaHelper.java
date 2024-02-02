package com.burberry.wc.integration.productcostingapi.criteria;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.productcostingapi.constant.BurProductCostingConstant;
import com.burberry.wc.integration.util.BurberryAPICriteriaUtil;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.sourcing.CostSheetFlexTypeScopeDefinition;

public final class BurberryProductCostingAPICriteriaHelper {

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductCostingAPICriteriaHelper.class);

	/**
	 * BurberryProductCostingAPICriteriaHelper.
	 */
	private BurberryProductCostingAPICriteriaHelper() {

	}

	/**
	 * @param strAttDisplayName
	 * @param strAttValue
	 * @return
	 * @throws WTException
	 * @throws BurException
	 * @throws ParseException
	 */
	public static Collection<Map> getCostingQueryCriteria(
			String strAttDisplayName, String strAttValue) throws WTException,
			BurException, ParseException {
		String methodName = "getCostingQueryCriteria() ";

		// Initialisation
		HashSet<Map> searchCriteriaCollection = new HashSet<Map>();

		// Check if attribute display name is valid
		Collection<FlexTypeAttribute> colCostSheetAtt = BurberryProductCostingAPICriteria
				.getCostingFlexTypeAttribute(
						CostSheetFlexTypeScopeDefinition.COST_SHEET_SCOPE,
						null, strAttDisplayName);
		logger.debug(methodName + "Cost Sheet Att: " + colCostSheetAtt);
		logger.debug(methodName + "Cost Sheet Attribute Value: " + strAttValue);

		// Loop through each flex type attribute
		for (FlexTypeAttribute costSheetAtt : colCostSheetAtt) {
			logger.debug(methodName + "costSheetAtt: " + costSheetAtt);

			// Check for attribute data type
			String strColumnName = costSheetAtt.getColumnName();
			logger.debug(methodName + "Cost Sheet Table Column Name: "
					+ strColumnName);

			// Append criteria based on data type
			searchCriteriaCollection.addAll(BurberryAPICriteriaUtil
					.appendCriteriaBasedOnDataType(
							BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
							strColumnName, costSheetAtt, strAttValue));
		}
		logger.debug(methodName + "searchCriteriaCollection: "
				+ searchCriteriaCollection);

		// Add the hash set values to collection
		Collection<Map> costSheetQueryCriteria = new ArrayList<Map>(
				searchCriteriaCollection);
		logger.debug(methodName + "costSheetQueryCriteria: "
				+ costSheetQueryCriteria);

		// Return Criteria Collection
		return costSheetQueryCriteria;
	}

}
