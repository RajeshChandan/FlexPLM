package com.burberry.wc.integration.planningapi.db;

import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.planningapi.constant.BurPlanningAPIConstant;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.db.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.planning.FlexPlan;

/**
 * A DB class to handle Database activity. Class contain several method to
 * handle DB activity i.e. creating DB query, statement, and result set
 * allocation. Collection of Criteria Map, Start Date, End Date and Modify are
 * Primary input to fetch details from Flex system.
 *
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryPlanningAPIDBHelper {
	/**
	 * Default Constructor.
	 */
	private BurberryPlanningAPIDBHelper() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPlanningAPIDBHelper.class);

	/**
	 * STR_VAL_A.
	 */
	private static final String STR_VAL_A = "A";

	// JIRA - BURBERRY-1363: START
	/**
	 * STR_EQUAL.
	 */
	private static final String STR_EQUAL = "=";

	/**
	 * STR_QUESTION.
	 */
	private static final String STR_QUESTION = "?";

	// JIRA - BURBERRY-1363: END

	/**
	 * Method for Planning API.
	 * 
	 * @param colCriteria
	 *            Collection of Criteria Map
	 * @return results Collection of Plans
	 * @throws WTException
	 *             throw WTException
	 */
	// JIRA - BURBERRY-1363: START
	public static Collection<FlexObject> getPlanningAPIfromDB(
			List<String> colCriteria) throws WTException {

		String methodName = "getPlanningAPIfromDB() ";
		// Method Start Time
		long lPlanAPIStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Planning API Query Start Time: ");

		// Initialisation of collection
		Collection<FlexObject> planObjResults = new ArrayList<FlexObject>();
		logger.debug(methodName + "colCriteria: " + colCriteria);

		// Check if the criteria collection is not empty
		if (colCriteria.isEmpty()) {
			return planObjResults;
		}

		// Create new prepared query statement
		PreparedQueryStatement pqStatement = new PreparedQueryStatement();
		// Append Distinct
		pqStatement.setDistinct(true);

		// Append table column
		pqStatement.appendFromTable(FlexPlan.class);
		// Append select column
		pqStatement.appendSelectColumn(new QueryColumn(
				BurPlanningAPIConstant.FLEXPLAN, BurConstant.BRANCHID));

		// Append select column
		pqStatement.appendSelectColumn(new QueryColumn(FlexPlan.class,
				BurConstant.OBJECTIDENTIFIERID));
		// Append search criteria
		pqStatement.addLatestIterationClause(FlexPlan.class);
		pqStatement.appendAnd();
		pqStatement
				.appendCriteria(new Criteria(new QueryColumn(FlexPlan.class,
						BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
						Criteria.EQUALS));

		pqStatement.appendAnd();
		pqStatement.appendOpenParen();

		// Append additional search criteria
		appendCriteriaToStatement(pqStatement, colCriteria);

		pqStatement.appendClosedParen();

		logger.info(methodName
				+ "Planning API Query Prepared Query Statement: "
				+ pqStatement.toString());
		planObjResults = LCSQuery.runDirectQuery(pqStatement).getResults();

		long lPlanAPIEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Planning API Query End Time: ");
		logger.info(methodName
				+ "Planning API Query Total Execution Time (ms): "
				+ (lPlanAPIEndTime - lPlanAPIStartTime));
		logger.debug(methodName + " result1 " + planObjResults);

		return planObjResults;
	}
	// JIRA - BURBERRY-1363: END
	
	/**
	 * Method to dynamically generate criteria statement.
	 * 
	 * @param statement
	 *            Prepared query statement
	 * @param colCriteria
	 *            collection of criteria map
	 * @throws WTException
	 *             throw wtexcetion
	 */
	// JIRA - BURBERRY-1363: START
	private static void appendCriteriaToStatement(
			PreparedQueryStatement statement, List<String> colCriteria)
			throws WTException {

		String methodName = "appendCriteriaToStatement() ";
		int criteriaCounter = 0;
		// Loop through the complete collection of map criteria
		for (String strBranchId : colCriteria) {
			logger.debug(methodName + " mapCriteria: " + strBranchId);
			String strTableName = "FLEXPLAN";
			String strColumnName = "BRANCHIDITERATIONINFO";
			logger.debug(methodName + "Counter: " + criteriaCounter);
			if (criteriaCounter > 0) {
				statement.appendOrIfNeeded();
			}
			statement.appendCriteria(new Criteria(new QueryColumn(strTableName,
					strColumnName), STR_QUESTION, STR_EQUAL), strBranchId);
			criteriaCounter++;
		}
	}
	// JIRA - BURBERRY-1363: END

	/**
	 * @param startdate
	 * @param enddate
	 * @param modifyDate
	 * @return
	 * @throws WTException
	 */
	public static Collection<Map> getPlanningDeltaFromDateRange(Date startdate,
			Date enddate, Boolean modifyDate) throws WTException {
		String methodName = "getPlanningDeltaFromDateRange() ";
		logger.debug(methodName + "startDate5: " + startdate);
		logger.debug(methodName + "enddate5: " + enddate);
		logger.debug(methodName + "modifyDate5: " + modifyDate);

		HashSet<Map> hsPlanIds = new HashSet<Map>();

		// Plan Delta
		Collection<Map> colPlanDelta = getPlanIdsFromDateRange(FlexPlan.class,
				startdate, enddate, modifyDate);
		logger.debug(methodName + "Collection of Plan Delta: " + colPlanDelta);
		hsPlanIds.addAll(colPlanDelta);

		return hsPlanIds;

	}

	/**
	 * @param class1
	 * @param startdate
	 * @param enddate
	 * @param modifyDate
	 * @return
	 * @throws WTException
	 */
	private static Collection<Map> getPlanIdsFromDateRange(Class tableName,
			Date startdate, Date enddate, Boolean modifyDate)
			throws WTException {

		String methodName = "getPlanIdsFromDateRange() ";

		long planDeltaStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Plan Delta Query Start Time: ");

		// Create a prepared query statement
		PreparedQueryStatement planStatement = new PreparedQueryStatement();
		// Append distinct
		planStatement.setDistinct(true);
		// Append tables
		planStatement.appendFromTable(FlexPlan.class);

		// Append Select column
		planStatement.appendSelectColumn(new QueryColumn(
				BurPlanningAPIConstant.FLEXPLAN, BurConstant.BRANCHID));

		planStatement.appendOpenParen();

		// Check if create / modify
		if (!modifyDate) {
			// get create statement
			planStatement = BurberryAPIDBUtil.getCreateStampCriteriaStatement(
					planStatement, tableName, startdate, enddate);
		} else {
			// get modified statement
			planStatement = BurberryAPIDBUtil.getModifyStampCriteriaStatement(
					planStatement, tableName, startdate, enddate);
		}
		planStatement.appendClosedParen();

		// Append search criteria
		planStatement.appendAndIfNeeded();
		planStatement.addLatestIterationClause(FlexPlan.class);
		planStatement.appendAnd();
		planStatement.appendCriteria(new Criteria(new QueryColumn(
				FlexPlan.class, BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));

		logger.info(methodName + "Plan Delta Prepared Query Statement: "
				+ planStatement.toString());
		// Exceute query
		Collection<?> result = LCSQuery.runDirectQuery(planStatement)
				.getResults();

		long planDeltaEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Plan Delta Query End Time: ");

		logger.info(methodName + "Plan Delta Total Execution Time (ms): "
				+ (planDeltaEndTime - planDeltaStartTime));
		logger.debug(methodName + " Plan Delta Results: " + result);
		return (Collection<Map>) result;
	}

}
