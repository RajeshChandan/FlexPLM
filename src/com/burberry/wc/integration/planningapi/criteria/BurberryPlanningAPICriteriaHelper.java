package com.burberry.wc.integration.planningapi.criteria;

import java.text.ParseException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.planningapi.constant.BurPlanningAPIConstant;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.flextype.*;
import com.lcs.wc.planning.*;

/**
 * A Criteria class to generate criteria for prepared query.
 *
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryPlanningAPICriteriaHelper {

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPlanningAPICriteriaHelper.class);

	/**
	 * BurberryPlanningAPICriteriaHelper.
	 */
	private BurberryPlanningAPICriteriaHelper() {

	}

	/**
	 * @param strAttDisplayName
	 * @param strAttValue
	 * @return
	 * @throws WTException
	 * @throws BurException
	 * @throws ParseException
	 */
	public static Collection<Map> getPlanQueryCriteria(
			String strAttDisplayName, String strAttValue) throws WTException,
			ParseException, BurException {

		String methodName = "getPlanQueryCriteria() ";

		// Initialisation
		HashSet<Map> searchCriteriaCollection = new HashSet<Map>();

		HashSet<Map> listAttCriteriaCollection = new HashSet<Map>();
		FlexType flexType = FlexTypeCache
				.getFlexTypeRootByClass((FlexPlan.class).getName());

		// Check if attribute display name is valid
		Collection<FlexTypeAttribute> colPlanAtts = BurberryPlanningAPICriteria
				.getPlanningFlexTypeAttribute(flexType,
						PlanningFlexTypeScopeDefinition.PLAN_SCOPE,
						strAttDisplayName);
		logger.debug(methodName + "Plan Att: " + colPlanAtts);
		logger.debug(methodName + "Plan Attribute Value: " + strAttValue);
		boolean listAtt=false;
		// Loop through each flex type attribute
		for (FlexTypeAttribute planAtt : colPlanAtts) {
			logger.debug(methodName + "Plan: "
					+ planAtt.getFlexTypeViaCache().getFullName(true));
			// Check for attribute data type
			String strColumnName = planAtt.getColumnName();
			logger.debug(methodName + "Plan Table Column Name: "
					+ strColumnName);
			//check if attribute is of List Type
			if (BurConstant.CHOICE.equalsIgnoreCase(planAtt
					.getAttVariableType())
					|| BurConstant.DRIVEN.equalsIgnoreCase(planAtt
							.getAttVariableType())) {
				logger.debug(methodName + "List Attribute: " + planAtt);
				listAtt=true;
				try {
					// Append criteria based on data type
					listAttCriteriaCollection.addAll(BurberryAPICriteriaUtil
							.appendCriteriaBasedOnDataType(
									BurPlanningAPIConstant.FLEXPLAN,
									strColumnName, planAtt, strAttValue));
				} catch (BurException e) {
					logger.debug(methodName + " strAttValue " + strAttValue
							+ " not available on "
							+ planAtt.getFlexTypeViaCache().getFullName());
				}
			} else {
				searchCriteriaCollection.addAll(BurberryAPICriteriaUtil
						.appendCriteriaBasedOnDataType(
								BurPlanningAPIConstant.FLEXPLAN, strColumnName,
								planAtt, strAttValue));
			}
		}
		//If no criteria found for list throw error
		if (listAtt && listAttCriteriaCollection.isEmpty()) {
			BurberryAPIUtil
					.throwBurException(
							strAttDisplayName + "=" + strAttValue,
							BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTEVALUE);
		} else {
			searchCriteriaCollection.addAll(listAttCriteriaCollection);
		}

		logger.debug(methodName + "searchCriteriaCollection: "
				+ searchCriteriaCollection);

		// Add the hash set values to collection
		Collection<Map> planQueryCriteria = new ArrayList<Map>(
				searchCriteriaCollection);
		logger.debug(methodName + "planQueryCriteria: " + planQueryCriteria);

		// Step 4: Return Criteria Collection
		return planQueryCriteria;
	}

	/**
	 * @param strAttDisplayName
	 * @param strAttValue
	 * @return
	 * @throws BurException
	 * @throws ParseException
	 * @throws WTException
	 */
	public static Collection<Map> getPlanDetailQueryCriteria(
			String strAttDisplayName, String strAttValue) throws BurException,
			ParseException, WTException {
		String methodName = "getPlanDetailQueryCriteria() ";

		// Initialisation
		HashSet<Map> searchCriteriaCollection = new HashSet<Map>();

		HashSet<Map> listAttCriteriaCollection = new HashSet<Map>();
		FlexType flexType = FlexTypeCache
				.getFlexTypeRootByClass((PlanLineItem.class).getName());

		boolean listAtt=false;
		// Check if attribute display name is valid
		Collection<FlexTypeAttribute> colPlanDetailAtts = BurberryPlanningAPICriteria
				.getPlanningFlexTypeAttribute(flexType,
						PlanningFlexTypeScopeDefinition.LINEITEM_SCOPE,
						strAttDisplayName);
		logger.debug(methodName + "Plan Detail Att: " + colPlanDetailAtts);
		logger.debug(methodName + "Plan Detail Attribute Value: " + strAttValue);

		// Loop through each flex type attribute
		for (FlexTypeAttribute planDetailAtt : colPlanDetailAtts) {
			logger.debug(methodName + "Plan Detail: "
					+ planDetailAtt.getFlexTypeViaCache().getFullName(true));
			// Check for attribute data type
			String strColumnName = planDetailAtt.getColumnName();
			logger.debug(methodName + "Plan Detail Table Column Name: "
					+ strColumnName);
			//check if attribute is of List Type
			if (BurConstant.CHOICE.equalsIgnoreCase(planDetailAtt
					.getAttVariableType())
					|| BurConstant.DRIVEN.equalsIgnoreCase(planDetailAtt
							.getAttVariableType())) {
				listAtt=true;
				logger.debug(methodName + "List Attribute: " + planDetailAtt);
				try {
					// Append criteria based on data type
					listAttCriteriaCollection.addAll(BurberryAPICriteriaUtil
							.appendCriteriaBasedOnDataType(
									BurPlanningAPIConstant.PLANLINEITEM,
									strColumnName, planDetailAtt, strAttValue));
				} catch (BurException e) {
					logger.debug(methodName + " strAttValue " + strAttValue
							+ " not available on "
							+ planDetailAtt.getFlexTypeViaCache().getFullName());
				}
			} else {
				searchCriteriaCollection.addAll(BurberryAPICriteriaUtil
						.appendCriteriaBasedOnDataType(
								BurPlanningAPIConstant.PLANLINEITEM,
								strColumnName, planDetailAtt, strAttValue));
			}
		}
		//If no criteria found for list throw error

		if (listAtt && listAttCriteriaCollection.isEmpty()) {
			BurberryAPIUtil
					.throwBurException(
							strAttDisplayName + "=" + strAttValue,
							BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTEVALUE);
		} else {
			searchCriteriaCollection.addAll(listAttCriteriaCollection);
		}
		logger.debug(methodName + "searchCriteriaCollection: "
				+ searchCriteriaCollection);

		// Add the hash set values to collection
		Collection<Map> planDetailQueryCriteria = new ArrayList<Map>(
				searchCriteriaCollection);
		logger.debug(methodName + "planDetailQueryCriteria: "
				+ planDetailQueryCriteria);

		// Step 4: Return Criteria Collection
		return planDetailQueryCriteria;
	}

}
