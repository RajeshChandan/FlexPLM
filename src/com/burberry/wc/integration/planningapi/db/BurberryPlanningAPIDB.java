package com.burberry.wc.integration.planningapi.db;

import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.planningapi.constant.BurPlanningAPIConstant;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.db.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.planning.*;
import com.lcs.wc.season.LCSSeason;

public final class BurberryPlanningAPIDB {

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPlanningAPIDB.class);

	/**
	 * STR_VAL_A.
	 */
	private static final String STR_VAL_A = "A";

	/**
	 * Private Constructor.
	 */
	private BurberryPlanningAPIDB() {

	}

	/**
	 * @param planQueryCriteria
	 * @return
	 * @throws WTException
	 */
	public static Collection<FlexObject> getAssociatedPlans(
			Collection<Map> planQueryCriteria) throws WTException {
		// Method Name
		String methodName = "getAssociatedPlans() ";
		// Track Start Time
		long planningStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Planning StartTime: ");

		// Initialisation
		Collection<FlexObject> resultPlans = new ArrayList<FlexObject>();
		logger.info(methodName + "planCriteria: " + planQueryCriteria);
		if (planQueryCriteria.isEmpty()) {
			return resultPlans;
		}
		// Create a new prepared query statement
		PreparedQueryStatement planningStatement = new PreparedQueryStatement();

		// Append Distinct
		planningStatement.setDistinct(true);

		planningStatement.appendFromTable(FlexPlan.class);

		// Append Select columns
		planningStatement.appendSelectColumn(new QueryColumn(
				BurPlanningAPIConstant.FLEXPLAN, BurConstant.BRANCHID));

		planningStatement.addLatestIterationClause(FlexPlan.class);

		planningStatement.appendAnd();
		planningStatement.appendCriteria(new Criteria(new QueryColumn(
				FlexPlan.class, BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(planningStatement,
				planQueryCriteria);

		logger.info(methodName + "Planning Prepared Query Statement: "
				+ planningStatement.toString());

		// Execute the query
		resultPlans = LCSQuery.runDirectQuery(planningStatement).getResults();
		logger.debug(methodName + "resultPlans: " + resultPlans);

		// Track End Time
		long planningEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Planning End Time: ");
		logger.info(methodName + "Planning Query Total Execution Time (ms): "
				+ (planningEndTime - planningStartTime));

		// Return Statement
		return resultPlans;
	}

	/**
	 * @param planDetailQueryCriteria
	 * @return
	 * @throws WTException
	 */
	public static Collection<FlexObject> getAssociatedPlansFromDetail(
			Collection<Map> planDetailQueryCriteria) throws WTException {
		// Method Name
		String methodName = "getAssociatedPlansFromDetail() ";
		// Track Start Time
		long planningStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Planning StartTime: ");

		// Initialisation
		Collection<FlexObject> resultPlans = new ArrayList<FlexObject>();
		logger.debug(methodName + "planCriteria: " + planDetailQueryCriteria);
		if (planDetailQueryCriteria.isEmpty()) {
			return resultPlans;
		}
		// Create a new prepared query statement
		PreparedQueryStatement planningStatement = new PreparedQueryStatement();

		// Append Distinct
		planningStatement.setDistinct(true);

		planningStatement.appendFromTable(FlexPlan.class);
		planningStatement.appendFromTable(PlanLineItem.class);
		// Append Select columns
		planningStatement.appendSelectColumn(new QueryColumn(
				BurPlanningAPIConstant.FLEXPLAN, BurConstant.BRANCHID));
		planningStatement.appendSelectColumn(new QueryColumn(
				PlanLineItem.class, BurConstant.OBJECTIDENTIFIERID));

		planningStatement.addLatestIterationClause(FlexPlan.class);

		planningStatement.appendAnd();
		planningStatement.appendCriteria(new Criteria(new QueryColumn(
				FlexPlan.class, BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));

		planningStatement.appendAnd();
		planningStatement.appendJoin(new QueryColumn(PlanLineItem.class,
				BurPlanningAPIConstant.PLANMASTERREF), new QueryColumn(
				FlexPlan.class, BurConstant.MASTERREFERENCE_KEY_ID));

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(planningStatement,
				planDetailQueryCriteria);

		logger.info(methodName + "Planning Prepared Query Statement: "
				+ planningStatement.toString());

		// Execute the query
		resultPlans = LCSQuery.runDirectQuery(planningStatement).getResults();
		logger.debug(methodName + "resultPlans: " + resultPlans);

		// Track End Time
		long planningEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Planning End Time: ");
		logger.info(methodName + "Planning Query Total Execution Time (ms): "
				+ (planningEndTime - planningStartTime));

		// Return Statement
		return resultPlans;
	}

	/**
	 * @param seasonQueryCriteria
	 * @return
	 * @throws WTException
	 */
	public static Collection<FlexObject> getAssociatedPlanFromSeason(
			Collection<Map> seasonQueryCriteria) throws WTException {
		// Method Name
		String methodName = "getAssociatedPlanFromSeason() ";
		// Track Start Time
		long planningStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Planning StartTime: ");

		// Initialisation
		Collection<FlexObject> resultPlans = new ArrayList<FlexObject>();
		logger.debug(methodName + "planCriteria: " + seasonQueryCriteria);
		if (seasonQueryCriteria.isEmpty()) {
			return resultPlans;
		}
		// Create a new prepared query statement
		PreparedQueryStatement planningStatement = new PreparedQueryStatement();

		// Append Distinct
		planningStatement.setDistinct(true);

		planningStatement.appendFromTable(FlexPlan.class);
		planningStatement.appendFromTable(PlanMaster.class);
		planningStatement.appendFromTable(LCSSeason.class);
		planningStatement.appendFromTable(PlanToOwnerLink.class);
		// Append Select columns
		planningStatement.appendSelectColumn(new QueryColumn(
				BurPlanningAPIConstant.FLEXPLAN, BurConstant.BRANCHID));
		planningStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSSEASON, BurConstant.BRANCHID));

		planningStatement.addLatestIterationClause(FlexPlan.class);
		planningStatement.addLatestIterationClause(LCSSeason.class);
		planningStatement.appendAnd();
		planningStatement.appendCriteria(new Criteria(new QueryColumn(
				FlexPlan.class, BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));

		planningStatement.appendAnd();
		planningStatement.appendJoin(new QueryColumn(PlanMaster.class,
				BurConstant.OBJECTIDENTIFIERID), new QueryColumn(
				FlexPlan.class, BurConstant.MASTERREFERENCE_KEY_ID));
		
	
		
		planningStatement.appendAndIfNeeded();
		planningStatement.appendJoin(new QueryColumn(PlanToOwnerLink.class,
				BurConstant.ROLEBOBJECTREF_KEY_ID), new QueryColumn(FlexPlan.class,
						BurConstant.MASTERREFERENCE_KEY_ID));
		planningStatement.appendAndIfNeeded();
		planningStatement.appendJoin(new QueryColumn(PlanToOwnerLink.class,
				BurConstant.ROLEAOBJECTREF_KEY_ID), new QueryColumn(LCSSeason.class,
						BurConstant.MASTERREFERENCE_KEY_ID));

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(planningStatement,
				seasonQueryCriteria);

		logger.debug(methodName + "Planning Prepared Query Statement: "
				+ planningStatement.toString());

		// Execute the query
		resultPlans = LCSQuery.runDirectQuery(planningStatement).getResults();
		logger.debug(methodName + "resultPlans: " + resultPlans);

		// Track End Time
		long planningEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Planning End Time: ");
		logger.debug(methodName + "Planning Query Total Execution Time (ms): "
				+ (planningEndTime - planningStartTime));

		// Return Statement
		return resultPlans;
	}

}
