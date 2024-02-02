package com.burberry.wc.integration.productcostingapi.db;

import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.productcostingapi.constant.BurProductCostingConstant;
import com.burberry.wc.integration.util.BurConstant;
import com.burberry.wc.integration.util.BurberryAPIDBUtil;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.db.*;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sourcing.LCSProductCostSheet;

public final class BurberryProductCostingAPIDBHelper {

	/**
	 * Default Constructor.
	 */
	private BurberryProductCostingAPIDBHelper() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductCostingAPIDBHelper.class);

	/**
	 * STR_VAL_A.
	 */
	private static final String STR_VAL_A = "A";

	/**
	 * STR_WRK.
	 */
	private static final String STR_WRK = "wrk";

	/**
	 * STR_VAL_ONE.
	 */
	private static final String STR_VAL_ONE = "1";

	/**
	 * Method to get Product Ids by checking the creation /updates on Cost Sheet
	 * 
	 * @param startdate
	 *            Start Date
	 * @param enddate
	 *            End Date
	 * @param modifyDate
	 *            Create/Modify
	 * @return results
	 * @throws WTException
	 *             throw exception
	 */

	public static Collection<Map> getProductCostingDeltaFromDateRange(
			Date startdate, Date enddate, boolean modifyDate)
			throws WTException {

		// long lStartTime = System.currentTimeMillis();
		String methodName = "getProductCostingDeltaFromDateRange() ";
		logger.debug(methodName + "Cost Sheet Start Date: " + startdate);
		logger.debug(methodName + "Cost Sheet End Date: " + enddate);
		logger.debug(methodName + "Cost Sheet Modify: " + modifyDate);

		// Initialisation
		HashSet<Map> hsProductCostingIds = new HashSet<Map>();

		// Cost Sheet Delta
		Collection<Map> colProductCostingDelta = getProductAssociatedCostSheetFromDateRange(
				startdate, enddate, modifyDate);
		logger.debug(methodName + "Collection of Cost Sheet Delta: "
				+ colProductCostingDelta);
		// Add the collection into hashset
		hsProductCostingIds.addAll(colProductCostingDelta);
		logger.debug(methodName + "hsProductIds: " + hsProductCostingIds);

		// CR R26: Handle Removed Cost Sheet Customization :
		// Start
		// Removed Cost Sheet Delta Query Data only for Updates
		Collection<Map> colRemovedCostSheetsFromDeltaDateRange = getRemoveCostSheetDeltaFromDateRange(
				startdate, enddate);
		logger.debug(methodName + "Collection Removed Cost Sheet Delta: "
				+ colRemovedCostSheetsFromDeltaDateRange);
		hsProductCostingIds.addAll(colRemovedCostSheetsFromDeltaDateRange);
		// CR R26: Handle Removed Cost Sheet Customization : End

		// Add to collection
		Collection<Map> criteriaMap = new ArrayList<Map>(hsProductCostingIds);
		logger.debug(methodName + "Delta Criteria Map: " + criteriaMap);

		// Return
		return criteriaMap;
	}

	private static Collection<Map> getRemoveCostSheetDeltaFromDateRange(
			Date startdate, Date enddate) throws WTException {
		String methodName = "getRemoveCostSheetDeltaFromDateRange() ";

		// Track execution time
		long remProdCSColStart = BurberryAPIUtil.printCurrentTime(methodName,
				"remProdCSColStart: ");

		// Get MOA Track Palette Material Color Flex Type
		FlexType moaFlexType = FlexTypeCache
				.getFlexTypeFromPath(BurProductCostingConstant.MOA_TRACK_COST_SHEET_FLEX_TYPE);
		// Get FlexType Id Path
		String flexTypeIdPath = moaFlexType.getTypeIdPath();
		logger.debug("FlexTypeIdPath: " + flexTypeIdPath);

		// Get Product Id Database Name
		String productIdColName = moaFlexType.getAttribute(
				BurProductCostingConstant.MOA_TRACK_COST_SHEET_PRODUCT_ID)
				.getColumnName();
		logger.debug("Product Database Column Name: " + productIdColName);

		// Get CS Id Database Name
		String costSheetId = moaFlexType.getAttribute(
				BurProductCostingConstant.MOA_TRACK_COST_SHEET_ID)
				.getColumnName();
		logger.debug("CS ID Database Column Name: " + costSheetId);

		// Get Source ID Database Column Name
		String sourceIDAttName = moaFlexType.getAttribute(
				BurProductCostingConstant.MOA_TRACK_COST_SHEET_SOURCING_ID)
				.getColumnName();
		logger.debug("Source Id Database Column Name: " + sourceIDAttName);

		// Create a prepared query statement
		PreparedQueryStatement remProdCostSheetColStatement = new PreparedQueryStatement();
		// Append distinct
		remProdCostSheetColStatement.setDistinct(true);
		// Append tables
		remProdCostSheetColStatement.appendFromTable(LCSProduct.class);
		remProdCostSheetColStatement.appendFromTable(LCSMOAObject.class);

		// Append Select column
		remProdCostSheetColStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		
		// Append Select column
		remProdCostSheetColStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSMOAOBJECT, "flexTypeIdPath"));
		// Append Select column
		remProdCostSheetColStatement.appendSelectColumn(new QueryColumn(
				LCSMOAObject.class, BurConstant.OBJECTIDENTIFIERID));

		// Append Select column
		remProdCostSheetColStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSMOAOBJECT, costSheetId));

		// Append Select column
		remProdCostSheetColStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSMOAOBJECT, sourceIDAttName));

		// Append Join between MOA And Material ID
		remProdCostSheetColStatement.appendJoin(new QueryColumn(
				BurConstant.LCSMOAOBJECT, productIdColName), new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));

		// Append Flex Type ID as Search Criteria
		remProdCostSheetColStatement.appendAndIfNeeded();
		remProdCostSheetColStatement.appendCriteria(new Criteria(
				new QueryColumn(BurConstant.LCSMOAOBJECT, "flexTypeIdPath"),
				"?", Criteria.LIKE), flexTypeIdPath + "%");

		remProdCostSheetColStatement.appendAndIfNeeded();

		remProdCostSheetColStatement = BurberryAPIDBUtil
				.getModifyStampCriteriaStatement(remProdCostSheetColStatement,
						LCSMOAObject.class, startdate, enddate);

		remProdCostSheetColStatement.appendAndIfNeeded();

		remProdCostSheetColStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProduct.class,
						BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		remProdCostSheetColStatement.appendAndIfNeeded();
		remProdCostSheetColStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProduct.class,
						BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));
		remProdCostSheetColStatement.appendAndIfNeeded();
		remProdCostSheetColStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProduct.class,
						BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));

		logger.info(methodName
				+ "Removed Product Cost Sheet Delta Query Statement: "
				+ remProdCostSheetColStatement.toString());
		// Exceute query
		Collection<?> remProdCostSheetColDeltaResult = LCSQuery.runDirectQuery(
				remProdCostSheetColStatement).getResults();
		logger.debug(methodName + " remProdCostSheetColDeltaResult: "
				+ remProdCostSheetColDeltaResult);

		// Track execution time
		long remProdCSColEnd = BurberryAPIUtil.printCurrentTime(methodName,
				"remProdCSColEnd: ");
		logger.info(methodName
				+ "Removed Palette Material Colour Delta Total Execution Time (ms): "
				+ (remProdCSColEnd - remProdCSColStart));

		// Return Statement
		return (Collection<Map>) remProdCostSheetColDeltaResult;
	}

	/**
	 * This Method is used to get Cost Sheet based on Date Range.
	 * 
	 * @param startdate
	 *            Start Date
	 * @param enddate
	 *            End Date
	 * @param modifyDate
	 *            Boolean
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */
	private static Collection<Map> getProductAssociatedCostSheetFromDateRange(
			Date startdate, Date enddate, boolean modifyDate)
			throws WTException {

		// Method Name
		String methodName = "getProductAssociatedCostSheetFromDateRange() ";
		// Track start time
		long lProductCostingDeltaStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Costing Delta Query Start Time: ");

		// Initialisation
		final PreparedQueryStatement productCostingDeltaStatement = new PreparedQueryStatement();

		// Append Distinct
		productCostingDeltaStatement.setDistinct(true);

		// Append Table
		productCostingDeltaStatement.appendFromTable(LCSProductCostSheet.class);
		productCostingDeltaStatement.appendFromTable(LCSProduct.class);

		// Append Select Columns
		productCostingDeltaStatement.appendSelectColumn(new QueryColumn(
				BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
				BurConstant.BRANCHID));
		productCostingDeltaStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));

		// Append Join
		productCostingDeltaStatement.appendJoin(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID), new QueryColumn(
				BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
				BurConstant.PRODUCTAREVID));

		// Append Cost Sheet Criteria
		productCostingDeltaStatement.appendAndIfNeeded();
		productCostingDeltaStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProductCostSheet.class,
						BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));

		// Append Product Criteria
		productCostingDeltaStatement.appendAndIfNeeded();
		productCostingDeltaStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProduct.class,
						BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		productCostingDeltaStatement.appendAndIfNeeded();
		productCostingDeltaStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProduct.class,
						BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));
		productCostingDeltaStatement.appendAndIfNeeded();
		productCostingDeltaStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProduct.class,
						BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));

		productCostingDeltaStatement.appendAnd();

		// Check for Create/Modify
		if (!modifyDate) {
			/*
			 * checking if source was created between given dates
			 */
			productCostingDeltaStatement.appendOpenParen();
			productCostingDeltaStatement.appendCriteria(new Criteria(
					new QueryColumn(LCSProductCostSheet.class,
							BurConstant.CREATE_STAMP), startdate,
					Criteria.GREATER_THAN_EQUAL));
			productCostingDeltaStatement.appendAnd();
			productCostingDeltaStatement.appendCriteria(new Criteria(
					new QueryColumn(LCSProductCostSheet.class,
							BurConstant.CREATE_STAMP), enddate,
					Criteria.LESS_THAN_EQUAL));
			productCostingDeltaStatement.appendClosedParen();
		} else {
			/*
			 * checking if source was updated between given dates
			 */
			productCostingDeltaStatement.appendOpenParen();
			productCostingDeltaStatement.appendCriteria(new Criteria(
					new QueryColumn(LCSProductCostSheet.class,
							BurConstant.MODIFY_STAMP), startdate,
					Criteria.GREATER_THAN_EQUAL));
			productCostingDeltaStatement.appendAnd();
			productCostingDeltaStatement.appendCriteria(new Criteria(
					new QueryColumn(LCSProductCostSheet.class,
							BurConstant.MODIFY_STAMP), enddate,
					Criteria.LESS_THAN_EQUAL));
			productCostingDeltaStatement.appendClosedParen();
		}
		logger.info(methodName
				+ "Product Costing Delta Prepared Query Statement: "
				+ productCostingDeltaStatement.toString());

		// Execute the query
		Collection<?> resultProductCostingDelta = LCSQuery.runDirectQuery(
				productCostingDeltaStatement).getResults();

		// Track end time
		long lProductCostingDeltaEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Costing Delta Query End Time: ");

		logger.info(methodName
				+ "Product Costing Delta Total Execution Time (ms): "
				+ (lProductCostingDeltaEndTime - lProductCostingDeltaStartTime));
		logger.debug(methodName + " resultProductCostingDelta "
				+ resultProductCostingDelta);

		// Return
		return (Collection<Map>) resultProductCostingDelta;

	}

	

}