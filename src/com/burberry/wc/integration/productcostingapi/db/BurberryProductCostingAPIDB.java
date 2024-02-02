package com.burberry.wc.integration.productcostingapi.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.foundation.LCSQuery;
import com.burberry.wc.integration.productapi.db.BurberryProductAPIDBHelper;
import com.burberry.wc.integration.productbomapi.constant.BurProductBOMConstant;
import com.burberry.wc.integration.productcostingapi.constant.BurProductCostingConstant;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.*;
import com.lcs.wc.sourcing.*;

/**
 * A DB class to handle Database activity. Class contain several method to
 * handle DB activity i.e. creating DB query, statement, and result set
 * allocation. Collection of Criteria Map, Start Date, End Date and Modify are
 * Primary input to fetch details from Flex system.
 *
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryProductCostingAPIDB {

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductCostingAPIDB.class);

	/**
	 * Private Constructor.
	 */
	private BurberryProductCostingAPIDB() {

	}

	/**
	 * STR_VAL_A.
	 */
	private static final String STR_VAL_A = "A";

	/**
	 * STR_VAL_ONE.
	 */
	private static final String STR_VAL_ONE = "1";

	/**
	 * STR_VAL_ONE.
	 */
	private static final String STR_VAL_ZERO = "0";

	/**
	 * STR_WRK.
	 */
	private static final String STR_WRK = "wrk";

	/**
	 * Method to generate prepared query statement to get Product Associated to
	 * Cost Sheet
	 * 
	 * @param productCostingQueryCriteria
	 *            Criteria collection
	 * @return Collection Map
	 * @throws WTException
	 *             Exception
	 */

	public static Collection<Map> getProductsAssociatedToCostSheet(
			Collection<Map> productCostingQueryCriteria) throws WTException {

		// Method Name
		String methodName = "getProductsAssociatedToCostSheet() ";
		// Track Start Time
		long prodCostingStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Costing StartTime: ");

		// Initialisation
		Collection<Map> resultProductCosting = new ArrayList<Map>();
		logger.debug(methodName + "productCostingQueryCriteria: "
				+ productCostingQueryCriteria);
		if (productCostingQueryCriteria.isEmpty()) {
			return resultProductCosting;
		}
		// Create a new prepared query statement
		PreparedQueryStatement prodCostingStatement = new PreparedQueryStatement();

		// Append Distinct
		prodCostingStatement.setDistinct(true);

		// Append Table
		prodCostingStatement.appendFromTable(LCSProduct.class);
		prodCostingStatement.appendFromTable(LCSProductCostSheet.class);
		prodCostingStatement.appendFromTable(LCSSourcingConfig.class);

		// Append Select Columns
		prodCostingStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		prodCostingStatement.appendSelectColumn(new QueryColumn(
				BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
				BurConstant.BRANCHID));
		prodCostingStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.BRANCHID));

		// Append Join Between Product and cost sheet
		prodCostingStatement.appendJoin(new QueryColumn(BurConstant.LCSPRODUCT,
				BurConstant.BRANCHID), new QueryColumn(
				BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
				BurConstant.PRODUCTAREVID));
		
		// Append Join Between Product and Source
		prodCostingStatement.appendJoin(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID), new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.PRODUCTAREVID));
		
		// Append Join Between Cost Sheet and Source
		prodCostingStatement.appendJoin(new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.BRANCHID),
				new QueryColumn(BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
						BurProductCostingConstant.SOURCINGCONFIGREVID));

		// Append Criteria
		prodCostingStatement.appendAndIfNeeded();
		prodCostingStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProductCostSheet.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));

		// Append Criteria
		prodCostingStatement.appendAndIfNeeded();
		prodCostingStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		prodCostingStatement.appendAndIfNeeded();
		prodCostingStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		prodCostingStatement.appendAndIfNeeded();
		prodCostingStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));
		
		// Append Criteria for Sourcing Config
		prodCostingStatement.appendAndIfNeeded();
		prodCostingStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSSourcingConfig.class,
						BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));
		prodCostingStatement.appendAndIfNeeded();
		prodCostingStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSSourcingConfig.class,
						BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));

		// Append Search Criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				prodCostingStatement, productCostingQueryCriteria);

		logger.info(methodName + "Product Costing Prepared Query Statement: "
				+ prodCostingStatement.toString());

		// Execute the query
		resultProductCosting = LCSQuery.runDirectQuery(prodCostingStatement)
				.getResults();
		logger.debug(methodName + "resultProductCosting: "
				+ resultProductCosting);

		// Track End Time
		long prodCostingEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product Costing End Time: ");
		logger.info(methodName
				+ "Product Costing Query Total Execution Time (ms): "
				+ (prodCostingEndTime - prodCostingStartTime));

		// Return Statement
		return (Collection<Map>) resultProductCosting;

	}

	/**
	 * Method to generate prepared query statement to get Product Associated to
	 * Product Season Cost Sheet
	 * 
	 * @param productSeasonLinkQueryCriteria
	 *            Collection
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */
	public static Collection<Map> getProductsAssociatedToProductSeasonCostSheet(
			Collection<Map> productSeasonLinkQueryCriteria) throws WTException {

		// Method Name
		String methodName = "getProductsAssociatedToProductSeasonCostSheet() ";
		// Track Start Time
		long prodSeasonCostingStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Season Cost Sheet StartTime: ");

		// Initialisation
		Collection<Map> resultProductSeasonCosting = new ArrayList<Map>();
		logger.debug(methodName + "productCostingQueryCriteria: "
				+ productSeasonLinkQueryCriteria);
		if (productSeasonLinkQueryCriteria.isEmpty()) {
			return resultProductSeasonCosting;
		}
		// Create a new prepared query statement
		PreparedQueryStatement prodSeasonCostingStatement = new PreparedQueryStatement();
		// Append Distinct
		prodSeasonCostingStatement.setDistinct(true);
		// Append Table
		prodSeasonCostingStatement.appendFromTable(LCSProduct.class);
		prodSeasonCostingStatement.appendFromTable(LCSProductSeasonLink.class);
		prodSeasonCostingStatement.appendFromTable(LCSProductCostSheet.class);
		prodSeasonCostingStatement.appendFromTable(LCSCostSheetMaster.class);
		prodSeasonCostingStatement.appendFromTable(LCSSourcingConfig.class);
		prodSeasonCostingStatement.appendFromTable(LCSSourceToSeasonLinkMaster.class);
		// Append Select Columns
		prodSeasonCostingStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		prodSeasonCostingStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.BRANCHID));
		prodSeasonCostingStatement.appendSelectColumn(new QueryColumn(
				BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
				BurConstant.BRANCHID));
		// Append Join Statements Product and Cost Sheet
		prodSeasonCostingStatement.appendJoin(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID), new QueryColumn(
				BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
				BurConstant.PRODUCTAREVID));
		// Append Join Statements Product and Product Season
		prodSeasonCostingStatement.appendJoin(BurConstant.LCSPRODUCT,
				BurConstant.IDA3MASTERREFEREN_CE,
				BurConstant.LCSPRODUCTSEASONLINK, BurConstant.PRODUCTMASTERID);
		// Append Join Between Cost Sheet and Source
		prodSeasonCostingStatement.appendJoin(new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.BRANCHID),
				new QueryColumn(BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
						BurProductCostingConstant.SOURCINGCONFIGREVID));
		// Append Join Between Product and Source
		prodSeasonCostingStatement.appendJoin(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID), new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.PRODUCTAREVID));
		// Append Join Statements Cost Sheet Master and Season Master
		prodSeasonCostingStatement.appendJoin(new QueryColumn(
				LCSCostSheetMaster.class,
				BurConstant.SEASONMASTERREFERENCE_KEY_ID), new QueryColumn(
				LCSSourceToSeasonLinkMaster.class,
				BurConstant.SEASONMASTERREFERENCE_KEY_ID));
		// Append Join Statements Cost Sheet and Cost Sheet Master
		prodSeasonCostingStatement.appendJoin(new QueryColumn(
				LCSProductCostSheet.class, BurConstant.MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSCostSheetMaster.class,
						BurConstant.OBJECTIDENTIFIERID));
		// Append Join Statements Product-Season Link and Season Master
		prodSeasonCostingStatement.appendJoin(new QueryColumn(
				LCSProductSeasonLink.class, BurConstant.ROLEBOBJECTREF_KEY_ID),
				new QueryColumn(LCSSourceToSeasonLinkMaster.class,
						BurConstant.SEASONMASTERREFERENCE_KEY_ID));
		prodSeasonCostingStatement.appendJoin(new QueryColumn(
				LCSSourceToSeasonLinkMaster.class.getSimpleName(),
				BurProductBOMConstant.SOURCINGCONFIGMASTER_KEY_ID),
				new QueryColumn(BurConstant.LCSSOURCINGCONFIG,
						BurConstant.IDA3MASTERREFEREN_CE));
		// Append Product Cost Sheet Criteria
		prodSeasonCostingStatement.appendAndIfNeeded();
		prodSeasonCostingStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProductCostSheet.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		// Append Product Criteria
		prodSeasonCostingStatement.appendAndIfNeeded();
		prodSeasonCostingStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		prodSeasonCostingStatement.appendAndIfNeeded();
		prodSeasonCostingStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));

		prodSeasonCostingStatement.appendAndIfNeeded();
		prodSeasonCostingStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));
		// Append Product Season Criteria
		prodSeasonCostingStatement.appendAndIfNeeded();
		prodSeasonCostingStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProductSeasonLink.class, BurConstant.EFFECTLATEST),
				STR_VAL_ONE, Criteria.EQUALS));
		prodSeasonCostingStatement.appendAndIfNeeded();
		prodSeasonCostingStatement.appendCriteria(new Criteria(new QueryColumn(
				BurConstant.LCSPRODUCTSEASONLINK, BurConstant.SEASONREMOVED),
				STR_VAL_ZERO, Criteria.EQUALS));
		// Append Criteria for Sourcing Config
		prodSeasonCostingStatement.appendAndIfNeeded();
		prodSeasonCostingStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSourcingConfig.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		prodSeasonCostingStatement.appendAndIfNeeded();
		prodSeasonCostingStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSourcingConfig.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));
		// Append Search Criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				prodSeasonCostingStatement, productSeasonLinkQueryCriteria);
		logger.info(methodName
				+ "Product Season Costing Prepared Query Statement: "
				+ prodSeasonCostingStatement.toString());
		// Execute the query
		resultProductSeasonCosting = LCSQuery.runDirectQuery(
				prodSeasonCostingStatement).getResults();
		logger.debug(methodName + "resultProductSeasonCosting: "
				+ resultProductSeasonCosting);
		// Track End Time
		long prodSeasonCostingEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Season Cost Sheet EndTime: ");
		logger.info(methodName
				+ "Product Season Costing Query Total Execution Time (ms): "
				+ (prodSeasonCostingEndTime - prodSeasonCostingStartTime));
		// Return Statement
		return (Collection<Map>) resultProductSeasonCosting;
	}

	/**
	 * Method to generate prepared query statement to get Product Associated to
	 * Colourway Cost Sheet
	 * 
	 * @param colourwayQueryCriteria
	 *            Collection
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */
	public static Collection<Map> getProductsAssociatedToColorwayCostSheet(
			Collection<Map> colourwayQueryCriteria) throws WTException {

		// Method Name
		String methodName = "getProductsAssociatedToColorwayCostSheet() ";

		// Track Start Time
		long prodColourwayCSStart = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Colourway Cost Sheet Query Start Time: ");

		// Initialisation
		Collection<Map> resultProductColorwayCostSheet = new ArrayList<Map>();
		logger.debug(methodName + "colourwayQueryCriteria: "
				+ colourwayQueryCriteria);
		if (colourwayQueryCriteria.isEmpty()) {
			return resultProductColorwayCostSheet;
		}

		// Create a new prepared query statement
		PreparedQueryStatement prodSKUCostSheetStatement = new PreparedQueryStatement();

		// Append Distinct
		prodSKUCostSheetStatement.setDistinct(true);

		// Append Table
		prodSKUCostSheetStatement.appendFromTable(LCSProduct.class);
		prodSKUCostSheetStatement.appendFromTable(LCSSKU.class);
		prodSKUCostSheetStatement.appendFromTable(LCSSourcingConfig.class);
		prodSKUCostSheetStatement.appendFromTable(CostSheetToColorLink.class);
		prodSKUCostSheetStatement.appendFromTable(LCSProductCostSheet.class);

		// Append Select column
		prodSKUCostSheetStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		prodSKUCostSheetStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSSKU, BurConstant.BRANCHID));
		prodSKUCostSheetStatement.appendSelectColumn(new QueryColumn(
				BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
				BurConstant.BRANCHID));
		prodSKUCostSheetStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.BRANCHID));

		// Append Join Statements Product and SKU
		prodSKUCostSheetStatement.appendJoin(BurConstant.LCSPRODUCT,
				BurConstant.BRANCHID, BurConstant.LCSSKU,
				BurConstant.PRODUCTAREVID);

		// Append Join Statements Product and Cost Sheet
		prodSKUCostSheetStatement.appendJoin(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID), new QueryColumn(
				BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
				BurConstant.PRODUCTAREVID));

		// Append Join Between Cost Sheet and Source
		prodSKUCostSheetStatement.appendJoin(new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.BRANCHID),
				new QueryColumn(BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
						BurProductCostingConstant.SOURCINGCONFIGREVID));

		// Append Join Between Product and Source
		prodSKUCostSheetStatement.appendJoin(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID), new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.PRODUCTAREVID));

		// Append Join Statements Cost Sheet and Cost Sheet Link
		prodSKUCostSheetStatement.appendJoin(new QueryColumn(
				CostSheetToColorLink.class, BurConstant.ROLEBOBJECTREF_KEY_ID),
				new QueryColumn(BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
						BurConstant.IDA3MASTERREFEREN_CE));

		// Append Join Statements SKU and Cost Sheet Link
		prodSKUCostSheetStatement.appendJoin(new QueryColumn(
				CostSheetToColorLink.class, BurConstant.ROLEAOBJECTREF_KEY_ID),
				new QueryColumn(BurConstant.LCSSKU,
						BurConstant.IDA3MASTERREFEREN_CE));

		// Append Product Cost Sheet Criteria
		prodSKUCostSheetStatement.appendAndIfNeeded();
		prodSKUCostSheetStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProductCostSheet.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));

		// Append Criteria for Product
		prodSKUCostSheetStatement.appendAndIfNeeded();
		prodSKUCostSheetStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));

		prodSKUCostSheetStatement.appendAndIfNeeded();
		prodSKUCostSheetStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));

		prodSKUCostSheetStatement.appendAndIfNeeded();
		prodSKUCostSheetStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		// Append Criteria for SKU
		prodSKUCostSheetStatement.appendAndIfNeeded();
		prodSKUCostSheetStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSKU.class, BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));

		prodSKUCostSheetStatement.appendAndIfNeeded();
		prodSKUCostSheetStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSKU.class, BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));

		prodSKUCostSheetStatement.appendAndIfNeeded();
		prodSKUCostSheetStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSKU.class, BurProductCostingConstant.PLACEHOLDER),
				STR_VAL_ZERO, Criteria.EQUALS));

		// Append Criteria for Sourcing Config
		prodSKUCostSheetStatement.appendAndIfNeeded();
		prodSKUCostSheetStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSourcingConfig.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		prodSKUCostSheetStatement.appendAndIfNeeded();
		prodSKUCostSheetStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSourcingConfig.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		// Append Search Criteria
		BurberryProductAPIDBHelper.appendProductSKUCriteriaToStatement(
				prodSKUCostSheetStatement, colourwayQueryCriteria);

		logger.info(methodName
				+ "Product Colourway Cost Sheet Prepared Query Statement: "
				+ prodSKUCostSheetStatement.toString());

		// Execute the query
		resultProductColorwayCostSheet = LCSQuery.runDirectQuery(
				prodSKUCostSheetStatement).getResults();

		// Track End Time
		long prodColourwayCSEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Colourway Cost Sheet Query End Time: ");

		logger.info(methodName
				+ "Product Colourway Cost Sheet Query Total Execution Time (ms): "
				+ (prodColourwayCSEndTime - prodColourwayCSStart));

		logger.debug(methodName + "resultProductColorwayCostSheet:"
				+ resultProductColorwayCostSheet);

		// Return Statement
		return (Collection<Map>) resultProductColorwayCostSheet;

	}

	/**
	 * Method to generate prepared query statement to get Product Associated to
	 * Colourway Season Cost Sheet
	 * 
	 * @param colourwaySeasonQueryCriteria
	 *            Collection
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */
	public static Collection<Map> getProductAssociatedToColorwaySeasonCostSheet(
			Collection<Map> colourwaySeasonQueryCriteria) throws WTException {
		// Method Name
		String methodName = "getProductAssociatedToColorwaySeasonCostSheet() ";
		// Track Start Time
		long prodSKUSeasonCSStart = BurberryAPIUtil.printCurrentTime(
				methodName, "Colourway Season Cost Sheet Query Start Time: ");
		// Initialisation
		Collection<Map> resultProductSKUSeasonCostSheet = new ArrayList<Map>();
		logger.debug(methodName + "colourwaySeasonQueryCriteria: "
				+ colourwaySeasonQueryCriteria);
		if (colourwaySeasonQueryCriteria.isEmpty()) {
			return resultProductSKUSeasonCostSheet;
		}
		// Create a new prepared query statement
		PreparedQueryStatement prodSKUSeasonCostsheetStatement = new PreparedQueryStatement();
		// Append Distinct
		prodSKUSeasonCostsheetStatement.setDistinct(true);
		// Append Table
		prodSKUSeasonCostsheetStatement.appendFromTable(LCSSKU.class);
		prodSKUSeasonCostsheetStatement.appendFromTable(LCSProduct.class);
		prodSKUSeasonCostsheetStatement.appendFromTable(LCSSKUSeasonLink.class);
		prodSKUSeasonCostsheetStatement.appendFromTable(LCSSourcingConfig.class);
		prodSKUSeasonCostsheetStatement.appendFromTable(LCSSourceToSeasonLinkMaster.class);
		prodSKUSeasonCostsheetStatement.appendFromTable(LCSCostSheetMaster.class);
		prodSKUSeasonCostsheetStatement.appendFromTable(CostSheetToColorLink.class);
		prodSKUSeasonCostsheetStatement.appendFromTable(LCSProductCostSheet.class);
		// Append Select Column
		prodSKUSeasonCostsheetStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		prodSKUSeasonCostsheetStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.BRANCHID));
		prodSKUSeasonCostsheetStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSSKU, BurConstant.BRANCHID));
		prodSKUSeasonCostsheetStatement.appendSelectColumn(new QueryColumn(
				BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
				BurConstant.BRANCHID));
		// Append Join SKU And SKU-Season Link
		prodSKUSeasonCostsheetStatement.appendJoin(BurConstant.LCSSKU,
				BurConstant.IDA3MASTERREFEREN_CE, BurConstant.LCSSKUSEASONLINK,
				BurConstant.SKUMASTERID);
		// Append Join Between Cost Sheet and Source
		prodSKUSeasonCostsheetStatement.appendJoin(new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.BRANCHID),
				new QueryColumn(BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
						BurProductCostingConstant.SOURCINGCONFIGREVID));
		// Append Join Between Product and Source
		prodSKUSeasonCostsheetStatement.appendJoin(new QueryColumn(BurConstant.LCSPRODUCT, BurConstant.BRANCHID),
				new QueryColumn(BurConstant.LCSSOURCINGCONFIG, BurConstant.PRODUCTAREVID));
		// Append Join Product And SKU-Season Link
		prodSKUSeasonCostsheetStatement.appendJoin(BurConstant.LCSPRODUCT,BurConstant.IDA3MASTERREFEREN_CE,
				BurConstant.LCSSKUSEASONLINK,BurConstant.PRODUCTMASTERID);
		// Append Join Statements SKU and Cost Sheet Link
		prodSKUSeasonCostsheetStatement.appendJoin(new QueryColumn(
				CostSheetToColorLink.class, BurConstant.ROLEAOBJECTREF_KEY_ID),
				new QueryColumn(BurConstant.LCSSKU,BurConstant.IDA3MASTERREFEREN_CE));
		// Append Join Statements Cost Sheet and Cost Sheet Link
		prodSKUSeasonCostsheetStatement.appendJoin(new QueryColumn(CostSheetToColorLink.class, 
				BurConstant.ROLEBOBJECTREF_KEY_ID),new QueryColumn(BurProductCostingConstant.LCSPRODUCTCOSTSHEET,
				BurConstant.IDA3MASTERREFEREN_CE));
		// Append Join Statements Cost Sheet Master and Season Master
		prodSKUSeasonCostsheetStatement.appendJoin(new QueryColumn(LCSCostSheetMaster.class,
				BurConstant.SEASONMASTERREFERENCE_KEY_ID), new QueryColumn(
				LCSSourceToSeasonLinkMaster.class,BurConstant.SEASONMASTERREFERENCE_KEY_ID));
		// Append Join Statements Cost Sheet and Cost Sheet Master
		prodSKUSeasonCostsheetStatement.appendJoin(new QueryColumn(LCSProductCostSheet.class,
				BurConstant.MASTERREFERENCE_KEY_ID),new QueryColumn(
				LCSCostSheetMaster.class,BurConstant.OBJECTIDENTIFIERID));
		// Append Join Statements SKU-Season Link and Season Master
		prodSKUSeasonCostsheetStatement.appendJoin(new QueryColumn(
				LCSSKUSeasonLink.class, BurConstant.ROLEBOBJECTREF_KEY_ID),
				new QueryColumn(LCSSourceToSeasonLinkMaster.class,
				BurConstant.SEASONMASTERREFERENCE_KEY_ID));
		// Append Product Cost Sheet Criteria
		prodSKUSeasonCostsheetStatement.appendAndIfNeeded();
		prodSKUSeasonCostsheetStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProductCostSheet.class,BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,Criteria.EQUALS));
		// Append Criteria for Product
		prodSKUSeasonCostsheetStatement.appendAndIfNeeded();
		prodSKUSeasonCostsheetStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProduct.class,
						BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		prodSKUSeasonCostsheetStatement.appendAndIfNeeded();
		prodSKUSeasonCostsheetStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProduct.class,
						BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));
		prodSKUSeasonCostsheetStatement.appendAndIfNeeded();
		prodSKUSeasonCostsheetStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProduct.class,BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));
		// Append Criteria for SKU
		prodSKUSeasonCostsheetStatement.appendAndIfNeeded();
		prodSKUSeasonCostsheetStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSSKU.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		prodSKUSeasonCostsheetStatement.appendAndIfNeeded();
		prodSKUSeasonCostsheetStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSSKU.class,BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A,Criteria.EQUALS));
		prodSKUSeasonCostsheetStatement.appendAndIfNeeded();
		prodSKUSeasonCostsheetStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSSKU.class,BurProductCostingConstant.PLACEHOLDER),
				STR_VAL_ZERO,Criteria.EQUALS));
		// Append Criteria for SKU-Season Link
		prodSKUSeasonCostsheetStatement.appendAndIfNeeded();
		prodSKUSeasonCostsheetStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSKUSeasonLink.class, BurConstant.EFFECTLATEST),
				STR_VAL_ONE, Criteria.EQUALS));
		prodSKUSeasonCostsheetStatement.appendAndIfNeeded();
		prodSKUSeasonCostsheetStatement.appendCriteria(new Criteria(new QueryColumn(
				BurConstant.LCSSKUSEASONLINK,BurConstant.SEASONREMOVED),
				STR_VAL_ZERO,Criteria.EQUALS));
		prodSKUSeasonCostsheetStatement.appendJoin(new QueryColumn(
				LCSSourceToSeasonLinkMaster.class.getSimpleName(),
				BurProductBOMConstant.SOURCINGCONFIGMASTER_KEY_ID),
				new QueryColumn(BurConstant.LCSSOURCINGCONFIG,
						BurConstant.IDA3MASTERREFEREN_CE));
		// Append Criteria for Sourcing Config
		prodSKUSeasonCostsheetStatement.appendAndIfNeeded();
		prodSKUSeasonCostsheetStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSSourcingConfig.class,
						BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));
		prodSKUSeasonCostsheetStatement.appendAndIfNeeded();
		prodSKUSeasonCostsheetStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSourcingConfig.class,BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A,Criteria.EQUALS));
		// Append Search Criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				prodSKUSeasonCostsheetStatement, colourwaySeasonQueryCriteria);
		logger.info(methodName
				+ "Colourway Season Cost Sheet Prepared Query Statement: "
				+ prodSKUSeasonCostsheetStatement.toString());
		// Execute the query
		resultProductSKUSeasonCostSheet = LCSQuery.runDirectQuery(
				prodSKUSeasonCostsheetStatement).getResults();
		// Track End Time
		long prodSKUSeasonCSEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Colourway Season Cost Sheet Query End Time: ");
		logger.info(methodName
				+ "Colourway Season Cost Sheet Total Execution Time (ms): "
				+ (prodSKUSeasonCSEndTime - prodSKUSeasonCSStart));
		logger.debug(methodName + "resultProductSKUSeasonCostSheet:"
				+ resultProductSKUSeasonCostSheet);
		// Return
		return (Collection<Map>) resultProductSKUSeasonCostSheet;
	}

}