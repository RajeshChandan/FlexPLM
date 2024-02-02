package com.burberry.wc.integration.sampleapi.db;

import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.sampleapi.constant.BurSampleConstant;
import com.burberry.wc.integration.util.BurConstant;
import com.burberry.wc.integration.util.BurberryAPIDBUtil;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.db.*;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleRequest;

/**
 * A DB helper class to handle Database activity. Class contain several method
 * to handle DB activity i.e. creating DB query, statement, and result set
 * allocation. Collection of Criteria Map, Start Date, End Date and Modify are
 * Primary input to fetch details from Flex system.
 *
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberrySampleAPIDBHelper {

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberrySampleAPIDBHelper.class);

	/**
	 * Default Constructor.
	 */
	private BurberrySampleAPIDBHelper() {

	}
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
	 * Method for Final Sample API query.
	 * 
	 * @param colCriteria
	 *            Collection Map
	 * @return Collection FlexObject
	 * @throws WTException
	 *             Exception
	 */
	// JIRA - BURBERRY-1363: START
	public static Collection<FlexObject> getSampleRequestAPIDataFromDB(
			List<String> colCriteria) throws WTException {

		// Method Name
		String methodName = "getSampleRequestAPIDataFromDB() ";

		// Track execution time
		long sampleReqAPIStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampleRequestAPIStartTime: ");

		// Initialisation of collection
		Collection<FlexObject> sampleRequestObjResults = new ArrayList<FlexObject>();
		logger.debug(methodName + "colCriteria: " + colCriteria);

		// Check if the criteria collection is not empty
		if (colCriteria.isEmpty()) {
			return sampleRequestObjResults;
		}

		// Create new prepared query statement
		PreparedQueryStatement sampleRequestStatement = new PreparedQueryStatement();
		// Append Distinct
		sampleRequestStatement.setDistinct(true);

		// Append from Table Column
		sampleRequestStatement.appendFromTable(LCSSampleRequest.class);

		// Append Select Column
		sampleRequestStatement.appendSelectColumn(new QueryColumn(
				LCSSampleRequest.class, BurConstant.OBJECTIDENTIFIERID));

		// Append Search Criteria
		appendCriteriaToStatement(sampleRequestStatement, colCriteria);

		logger.info(methodName + " Sample Request API Data Extraction Query: "
				+ sampleRequestStatement.toString());

		// Execute the query
		sampleRequestObjResults = LCSQuery.runDirectQuery(
				sampleRequestStatement).getResults();
		logger.debug(methodName + " sampleRequestObjResults: "
				+ sampleRequestObjResults);

		// Track execution time
		long sampleReqAPIEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"sampleReqAPIEndTime: ");
		logger.info(methodName
				+ "Sample Request API Data Extraction Query Total Execution Time (ms): "
				+ (sampleReqAPIEndTime - sampleReqAPIStartTime));

		// Return Statement
		return sampleRequestObjResults;
	}

	/**
	 * Method to dynamically generate criteria statement.
	 * 
	 * @param statement
	 *            Prepared query statement
	 * @param colCriteria
	 *            collection of criteria map
	 * @throws WTException
	 *             throw wt exception
	 */
	// JIRA - BURBERRY-1363: START
	private static void appendCriteriaToStatement(
			PreparedQueryStatement preparedQueryStatement,
			List<String> colCriteria) {

		// Method Name
		String methodName = "appendCriteriaToStatement() ";
		// Initialise counter
		int criteriaCounter = 0;
		// Loop through the complete collection of map criteria
		for (String strBranchId : colCriteria) {
			logger.debug(methodName + " mapCriteria: " + strBranchId);
			String strTableName = "LCSSAMPLEREQUEST";
			String strColumnName = "IDA2A2";
			logger.debug(methodName + "Counter: " + criteriaCounter);
			if (criteriaCounter > 0) {
				preparedQueryStatement.appendOrIfNeeded();
			}
			preparedQueryStatement.appendCriteria(new Criteria(new QueryColumn(
					strTableName, strColumnName), STR_QUESTION, STR_EQUAL),
					strBranchId);
			criteriaCounter++;
		}

	}
	// JIRA - BURBERRY-1363: END

	/**
	 * Method to Generate Delta query.
	 * 
	 * @param startdate
	 *            Date
	 * @param enddate
	 *            Date
	 * @param updateMode
	 *            Boolean
	 * @param bMaterialDelta
	 * @param bProductDelta
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */

	public static Collection<Map> getSampleDeltaFromDateRange(Date startdate,
			Date enddate, boolean updateMode, boolean bProductDelta,
			boolean bMaterialDelta) throws WTException {

		// Method Name
		String methodName = "getSampleDeltaFromDateRange() ";

		// Track execution time
		long sampleDeltaFromDateRangeStartTime = BurberryAPIUtil
				.printCurrentTime(methodName,
						"sampleDeltaFromDateRangeStartTime: ");

		logger.debug(methodName + "StartDate=" + startdate);
		logger.debug(methodName + "EndDate=" + enddate);
		logger.debug(methodName + "ModifyDate=" + updateMode);
		logger.debug(methodName + "Product Delta=" + bProductDelta);
		logger.debug(methodName + "Material Delta=" + bMaterialDelta);

		// Initialisation
		HashSet<Map> hashSetSampleRequest = new HashSet<Map>();

		// Collection of Sample Delta
		Collection<Map> colSampleDelta = getSampleFromDateRange(
				LCSSample.class, startdate, enddate, updateMode, bProductDelta,
				bMaterialDelta);
		logger.debug(methodName + "Collection of Sample Delta: "
				+ colSampleDelta);
		hashSetSampleRequest.addAll(colSampleDelta);

		Collection<Map> criteriaMap = new ArrayList<Map>(hashSetSampleRequest);
		logger.debug(methodName + "Delta Criteria Map: " + criteriaMap);

		// Track execution time
		long sampleDeltaFromDateRangeEndTime = BurberryAPIUtil
				.printCurrentTime(methodName,
						"sampleDeltaFromDateRangeEndTime: ");
		logger.info(methodName
				+ "Sample Request API Delta for Date Range Query Total Execution Time (ms): "
				+ (sampleDeltaFromDateRangeEndTime - sampleDeltaFromDateRangeStartTime));

		// Return Statement
		return criteriaMap;

	}

	/**
	 * Method to get Sample Delta Query.
	 * 
	 * @param tableName
	 *            Table Name
	 * @param startdate
	 *            Date
	 * @param enddate
	 *            Date
	 * @param updateMode
	 *            Boolean
	 * @param bMaterialDelta
	 * @param bProductDelta
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */
	private static Collection<Map> getSampleFromDateRange(Class tableName,
			Date startdate, Date enddate, boolean updateMode,
			boolean bProductDelta, boolean bMaterialDelta) throws WTException {

		// Method Name
		String methodName = "getSampleFromDateRange() ";
		// Track execution time
		long sampleDeltaStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampleDeltaStartTime: ");

		// Create a new prepared query statement
		PreparedQueryStatement sampleDeltaStatement = new PreparedQueryStatement();

		// Append Distinct
		sampleDeltaStatement.setDistinct(true);

		// Append Table Name
		sampleDeltaStatement.appendFromTable(LCSSample.class);
		sampleDeltaStatement.appendFromTable(LCSSampleRequest.class);

		// Append Select Columns
		sampleDeltaStatement.appendSelectColumn(new QueryColumn(
				LCSSampleRequest.class, BurConstant.OBJECTIDENTIFIERID));

		// Append Join between Sample and Sample Request
		sampleDeltaStatement.appendJoin(new QueryColumn(LCSSample.class,
				BurSampleConstant.SAMPLE_REQUESTREFERENCE_KEY_ID),
				new QueryColumn(LCSSampleRequest.class,
						BurConstant.OBJECTIDENTIFIERID));

		sampleDeltaStatement.appendAndIfNeeded();
		sampleDeltaStatement.appendOpenParen();
		// Check if create / modify
		if (!updateMode) {
			// get create statement
			sampleDeltaStatement = BurberryAPIDBUtil
					.getCreateStampCriteriaStatement(sampleDeltaStatement,
							tableName, startdate, enddate);
		} else {
			// get modified statement
			sampleDeltaStatement = BurberryAPIDBUtil
					.getModifyStampCriteriaStatement(sampleDeltaStatement,
							tableName, startdate, enddate);
		}
		// Check for Product and Material Delta
		if (bProductDelta || bMaterialDelta) {
			// get create statement
			sampleDeltaStatement = appendFlexTypeIdPathCriteriaStatement(
					sampleDeltaStatement, LCSSampleRequest.class,
					bProductDelta, bMaterialDelta);
		}
		sampleDeltaStatement.appendClosedParen();

		logger.info(methodName + tableName.getSimpleName().toUpperCase()
				+ " Sample Delta Query Statement: "
				+ sampleDeltaStatement.toString());
		// Execute query
		Collection<?> sampleDeltaResult = LCSQuery.runDirectQuery(
				sampleDeltaStatement).getResults();
		logger.debug(methodName + " sampleDeltaResult: " + sampleDeltaResult);

		// Track execution time
		long sampleDeltaEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"sampleDeltaEndTime: ");
		logger.info(methodName + "Sample Delta Total Execution Time (ms): "
				+ (sampleDeltaEndTime - sampleDeltaStartTime));

		// Return Statement
		return (Collection<Map>) sampleDeltaResult;
	}

	/**
	 * Method to append Product/Material Sample Request.
	 * 
	 * @param statement
	 *            statement
	 * @param strTableName
	 *            table name
	 * @param startdate
	 *            start date
	 * @param enddate
	 *            end date
	 * @return PreparedQueryStatement
	 * @throws WTException
	 *             exception
	 */
	public static PreparedQueryStatement appendFlexTypeIdPathCriteriaStatement(
			PreparedQueryStatement statement, Class table,
			boolean bProductDelta, boolean bMaterialDelta) throws WTException {

		// Method Name
		String methodName = "appendFlexTypeIdPathCriteriaStatement() ";
		logger.debug(methodName + "AppendQuery for " + table.getSimpleName());

		// Initialisation
		FlexType flexType = null;
		// Product Delta
		if (bProductDelta) {
			flexType = FlexTypeCache
					.getFlexTypeFromPath(BurSampleConstant.PRODUCT_SAMPLE_FLEXTYPE);
		}
		// Material Delta
		if (bMaterialDelta) {
			flexType = FlexTypeCache
					.getFlexTypeFromPath(BurSampleConstant.MATERIAL_SAMPLE_FLEXTYPE);
		}
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(table,
				"flexTypeIdPath"), "?", Criteria.LIKE),
				flexType.getTypeIdPath() + "%");
		// Return Statement
		return statement;
	}

}
