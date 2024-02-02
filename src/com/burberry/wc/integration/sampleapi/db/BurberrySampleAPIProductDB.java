package com.burberry.wc.integration.sampleapi.db;

import java.util.*;
import org.apache.log4j.Logger;
import wt.util.WTException;

import com.burberry.wc.integration.sampleapi.constant.BurSampleConstant;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleRequest;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.db.Criteria;

/**
 * A DB class to handle Database activity. Class contain several method to
 * handle DB activity i.e. creating DB query, statement, and result set
 * allocation. Collection of Criteria Map, Start Date, End Date and Modify are
 * Primary input to fetch details from Flex system.
 *
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberrySampleAPIProductDB {

	/**
	 * Default Constructor.
	 */
	private BurberrySampleAPIProductDB() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberrySampleAPIProductDB.class);

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
	 * STR_VAL_ONE.
	 */
	private static final String STR_VAL_ZERO = "0";

	/**
	 * Method to generate prepared query statement for Product Sample Request.
	 * 
	 * @param colProdSampReqCriteria
	 *            Criteria collection
	 * @return Collection Map
	 * @throws WTException
	 *             Exception
	 */

	public static Collection<Map> getProductSampleRequest(
			Collection<Map> colProdSampReqCriteria) throws WTException {

		// Set Method Name
		String methodName = "getProductSampleRequest() ";

		// Track Start Time for Query Execution
		long prodSampleReqStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "prodSampleReqStartTime: ");

		// Initialisation
		Collection<Map> resultsProdSampReq = new ArrayList<Map>();

		logger.debug(methodName + "colProdSampReqCriteria: "
				+ colProdSampReqCriteria);
		if (colProdSampReqCriteria.isEmpty()) {
			return resultsProdSampReq;
		}

		// Create a new prepared query statement
		PreparedQueryStatement prodSampleReqStatement = new PreparedQueryStatement();
		// Append Distinct
		prodSampleReqStatement.setDistinct(true);

		// Append Table Name
		prodSampleReqStatement.appendFromTable(LCSSampleRequest.class);

		// Append Select Columns
		prodSampleReqStatement.appendSelectColumn(new QueryColumn(
				LCSSampleRequest.class, BurConstant.OBJECTIDENTIFIERID));

		// Append Search Criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				prodSampleReqStatement, colProdSampReqCriteria);

		// Append Product flex type criteria
		BurberrySampleAPIDBHelper.appendFlexTypeIdPathCriteriaStatement(
				prodSampleReqStatement, LCSSampleRequest.class, true, false);

		logger.info(methodName
				+ "Product Sample Request Prepared Query Statement: "
				+ prodSampleReqStatement.toString());

		// Execute the query
		resultsProdSampReq = LCSQuery.runDirectQuery(prodSampleReqStatement)
				.getResults();
		logger.debug(methodName + "resultsProdSampReq: " + resultsProdSampReq);

		// Track End Time for Query Execution
		long prodSampleReqEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "matEndTime: ");
		logger.info(methodName
				+ "Product Sample Request Query Total Execution Time (ms): "
				+ (prodSampleReqEndTime - prodSampleReqStartTime));

		// Return Statement
		return (Collection<Map>) resultsProdSampReq;
	}

	/**
	 * Method to generate prepared query statement for product sample.
	 * 
	 * @param colProdSampCriteria
	 *            Collection
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */

	public static Collection<Map> getProductSample(
			Collection<Map> colProdSampCriteria) throws WTException {

		// Set Method Name
		String methodName = "getProductSample() ";

		// Track Start Time for Query Execution
		long prodSampleStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"prodSampleStartTime: ");
		// Initialisation
		Collection<Map> resultsProdSamp = new ArrayList<Map>();
		logger.debug(methodName + "colProdSampCriteria: " + colProdSampCriteria);
		if (colProdSampCriteria.isEmpty()) {
			return resultsProdSamp;
		}

		// Create a new prepared query statement
		PreparedQueryStatement prodSampleStatement = new PreparedQueryStatement();
		// Append Distinct
		prodSampleStatement.setDistinct(true);

		// Append Table Name
		prodSampleStatement.appendFromTable(LCSSample.class);
		prodSampleStatement.appendFromTable(LCSSampleRequest.class);

		// Append Select Columns
		prodSampleStatement.appendSelectColumn(new QueryColumn(
				LCSSampleRequest.class, BurConstant.OBJECTIDENTIFIERID));
		prodSampleStatement.appendSelectColumn(new QueryColumn(LCSSample.class,
				BurConstant.OBJECTIDENTIFIERID));

		// Append Join between Sample and Sample Request
		prodSampleStatement.appendJoin(new QueryColumn(LCSSample.class,
				BurSampleConstant.SAMPLE_REQUESTREFERENCE_KEY_ID),
				new QueryColumn(LCSSampleRequest.class,
						BurConstant.OBJECTIDENTIFIERID));

		// Append Search Criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(prodSampleStatement,
				colProdSampCriteria);

		// Append product flex type criteria
		BurberrySampleAPIDBHelper.appendFlexTypeIdPathCriteriaStatement(
				prodSampleStatement, LCSSampleRequest.class, true, false);

		logger.info(methodName + "Product Sample Prepared Query Statement: "
				+ prodSampleStatement.toString());

		// Execute the query
		resultsProdSamp = LCSQuery.runDirectQuery(prodSampleStatement)
				.getResults();
		logger.debug(methodName + "resultsProdSamp: " + resultsProdSamp);

		// Track End Time for Query Execution
		long prodSampleEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"prodSampleEndTime: ");
		logger.info(methodName
				+ "Product Sample Query Total Execution Time (ms): "
				+ (prodSampleEndTime - prodSampleStartTime));

		// Return Statement
		return (Collection<Map>) resultsProdSamp;
	}

	/**
	 * Method to generate prepared query statement to get sample request from
	 * Product.
	 * 
	 * @param productQueryCriteria
	 *            Collection
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */

	public static Collection<Map> getAssociatedSampleRequestFromProduct(
			Collection<Map> productQueryCriteria) throws WTException {

		// Set Method Name
		String methodName = "getAssociatedSampleRequestFromProduct() ";

		// Track Start Time for Query Execution
		long sampReqForProdStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampReqForProdStartTime: ");

		// Initialisation
		Collection<Map> resultsSampleReqforProduct = new ArrayList<Map>();
		logger.debug(methodName + "productQueryCriteria: "
				+ productQueryCriteria);
		if (productQueryCriteria.isEmpty()) {
			return resultsSampleReqforProduct;
		}

		// Create a new prepared query statement
		PreparedQueryStatement sampleReqFromProdStatement = new PreparedQueryStatement();
		// Append Distinct
		sampleReqFromProdStatement.setDistinct(true);

		// Append Table Name
		sampleReqFromProdStatement.appendFromTable(LCSSampleRequest.class);
		sampleReqFromProdStatement.appendFromTable(LCSProduct.class);
		sampleReqFromProdStatement.appendFromTable(LCSSample.class);

		// Append Select Column
		sampleReqFromProdStatement.appendSelectColumn(new QueryColumn(
				LCSSampleRequest.class, BurConstant.OBJECTIDENTIFIERID));
		sampleReqFromProdStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				sampleReqFromProdStatement, productQueryCriteria);

		// Append Join between Sample and Sample Request
		sampleReqFromProdStatement.appendJoin(new QueryColumn(LCSSample.class,
				BurSampleConstant.SAMPLE_REQUESTREFERENCE_KEY_ID),
				new QueryColumn(LCSSampleRequest.class,
						BurConstant.OBJECTIDENTIFIERID));

		// Append Join between Sample Request and Product
		sampleReqFromProdStatement.appendJoin(new QueryColumn(LCSProduct.class,
				BurConstant.MASTERREFERENCE_KEY_ID), new QueryColumn(
				LCSSampleRequest.class,
				BurSampleConstant.OWNER_MASTERREFERENCE_KEY_ID));

		// Append Criteria
		sampleReqFromProdStatement.appendAndIfNeeded();
		sampleReqFromProdStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		sampleReqFromProdStatement.appendAndIfNeeded();
		sampleReqFromProdStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		sampleReqFromProdStatement.appendAndIfNeeded();
		sampleReqFromProdStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		logger.info(methodName
				+ "Sample Request Prepared Query Statement for Associated Products: "
				+ sampleReqFromProdStatement.toString());

		// Execute the query
		resultsSampleReqforProduct = LCSQuery.runDirectQuery(
				sampleReqFromProdStatement).getResults();
		logger.debug(methodName + "resultsSampleReqforProduct: "
				+ resultsSampleReqforProduct);

		// Track End Time for Query Execution
		long sampleReqForProdEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampleReqForProdEndTime: ");
		logger.info(methodName
				+ "Sample Request for Associated Products Query Total Execution Time (ms): "
				+ (sampleReqForProdEndTime - sampReqForProdStartTime));

		// Return Statement
		return (Collection<Map>) resultsSampleReqforProduct;
	}

	/**
	 * Method to generate prepared query statement to get sample request from
	 * Product Season.
	 * 
	 * @param productSeasonLinkQueryCriteria
	 *            Collection
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */

	public static Collection<Map> getAssociatedSampleRequestFromProductSeason(
			Collection<Map> productSeasonLinkQueryCriteria) throws WTException {

		// Set Method Name
		String methodName = "getAssociatedSampleRequestFromProductSeason() ";

		// Track Start Time for Query Execution
		long sampReqForProdSeasonStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampReqForProdSeasonStartTime: ");

		// Initialisation
		Collection<Map> resultsSampleReqforProductSeason = new ArrayList<Map>();
		logger.debug(methodName + "productSeasonLinkQueryCriteria: "
				+ productSeasonLinkQueryCriteria);
		if (productSeasonLinkQueryCriteria.isEmpty()) {
			return resultsSampleReqforProductSeason;
		}

		// Create a new prepared query statement
		PreparedQueryStatement sampleReqFromProdSeasonStatement = new PreparedQueryStatement();
		// Append distinct
		sampleReqFromProdSeasonStatement.setDistinct(true);

		// Append Table Name
		sampleReqFromProdSeasonStatement
				.appendFromTable(LCSSampleRequest.class);
		sampleReqFromProdSeasonStatement.appendFromTable(LCSProduct.class);
		sampleReqFromProdSeasonStatement
				.appendFromTable(LCSProductSeasonLink.class);
		sampleReqFromProdSeasonStatement.appendFromTable(LCSSample.class);

		// Append Select Column
		sampleReqFromProdSeasonStatement.appendSelectColumn(new QueryColumn(
				LCSSampleRequest.class, BurConstant.OBJECTIDENTIFIERID));
		sampleReqFromProdSeasonStatement.appendSelectColumn(new QueryColumn(
				LCSProductSeasonLink.class, BurConstant.OBJECTIDENTIFIERID));

		// Append Search Criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				sampleReqFromProdSeasonStatement,
				productSeasonLinkQueryCriteria);

		// Append Join between Sample and Sample Request
		sampleReqFromProdSeasonStatement.appendJoin(new QueryColumn(
				LCSSample.class,
				BurSampleConstant.SAMPLE_REQUESTREFERENCE_KEY_ID),
				new QueryColumn(LCSSampleRequest.class,
						BurConstant.OBJECTIDENTIFIERID));

		// Append Join between Sample Request and Product
		sampleReqFromProdSeasonStatement.appendJoin(new QueryColumn(
				LCSProduct.class, BurConstant.MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSSampleRequest.class,
						BurSampleConstant.OWNER_MASTERREFERENCE_KEY_ID));

		// Append Join between Product and ProductSeasonLink
		sampleReqFromProdSeasonStatement.appendJoin(BurConstant.LCSPRODUCT,
				BurConstant.IDA3MASTERREFEREN_CE,
				BurConstant.LCSPRODUCTSEASONLINK, BurConstant.PRODUCTMASTERID);

		// Append Basic Criteria
		sampleReqFromProdSeasonStatement.appendAndIfNeeded();
		sampleReqFromProdSeasonStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProduct.class,
						BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		sampleReqFromProdSeasonStatement.appendAndIfNeeded();
		sampleReqFromProdSeasonStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProduct.class,
						BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));
		sampleReqFromProdSeasonStatement.appendAndIfNeeded();
		sampleReqFromProdSeasonStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProduct.class,
						BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));

		sampleReqFromProdSeasonStatement.appendAndIfNeeded();
		sampleReqFromProdSeasonStatement
				.appendCriteria(new Criteria(new QueryColumn(
						LCSProductSeasonLink.class, BurConstant.EFFECTLATEST),
						STR_VAL_ONE, Criteria.EQUALS));
		sampleReqFromProdSeasonStatement.appendAndIfNeeded();
		sampleReqFromProdSeasonStatement.appendCriteria(new Criteria(
				new QueryColumn(BurConstant.LCSPRODUCTSEASONLINK,
						BurConstant.SEASONREMOVED), STR_VAL_ZERO,
				Criteria.EQUALS));

		logger.info(methodName
				+ "Sample Request Prepared Query Statement for Associated Product Season: "
				+ sampleReqFromProdSeasonStatement.toString());

		// Execute the query
		resultsSampleReqforProductSeason = LCSQuery.runDirectQuery(
				sampleReqFromProdSeasonStatement).getResults();
		logger.debug(methodName + "resultsSampleReqforProductSeason: "
				+ resultsSampleReqforProductSeason);

		// Track End Time for Query Execution
		long sampleReqForProdSeasonEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampReqForProdSeasonEndTime: ");
		logger.info(methodName
				+ "Sample Request for Associated Products Season Query Total Execution Time (ms): "
				+ (sampleReqForProdSeasonEndTime - sampReqForProdSeasonStartTime));

		// Return Statement
		return (Collection<Map>) resultsSampleReqforProductSeason;
	}

	/**
	 * Method to generate prepared query statement to get sample request from
	 * Colourway.
	 * 
	 * @param colourwayQueryCriteria
	 *            Collection
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */

	public static Collection<Map> getAssociatedSampleRequestFromColorway(
			Collection<Map> colourwayQueryCriteria) throws WTException {
		// Set Method Name
		String methodName = "getAssociatedSampleRequestforColorway() ";

		// Track Start Time for Query Execution
		long sampReqForColourwayStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampReqForColourwayStartTime: ");

		// Initialisation
		Collection<Map> resultsSampleReqforColourway = new ArrayList<Map>();
		logger.debug(methodName + "colourwayQueryCriteria: "
				+ colourwayQueryCriteria);
		if (colourwayQueryCriteria.isEmpty()) {
			return resultsSampleReqforColourway;
		}

		// Create a new prepared query statement
		PreparedQueryStatement sampleReqFromColourwayStatement = new PreparedQueryStatement();
		// Append distinct
		sampleReqFromColourwayStatement.setDistinct(true);

		// Append Table Name
		sampleReqFromColourwayStatement.appendFromTable(LCSSample.class);
		sampleReqFromColourwayStatement.appendFromTable(LCSSampleRequest.class);
		sampleReqFromColourwayStatement.appendFromTable(LCSSKU.class);

		// Append Select Columns
		sampleReqFromColourwayStatement.appendSelectColumn(new QueryColumn(
				LCSSampleRequest.class, BurConstant.OBJECTIDENTIFIERID));
		sampleReqFromColourwayStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSSKU, BurConstant.BRANCHID));
		sampleReqFromColourwayStatement.appendSelectColumn(new QueryColumn(
				LCSSample.class, BurConstant.OBJECTIDENTIFIERID));

		// Append Search Criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				sampleReqFromColourwayStatement, colourwayQueryCriteria);

		// Append Join between Sample and Sample Request
		sampleReqFromColourwayStatement.appendJoin(new QueryColumn(
				LCSSample.class,
				BurSampleConstant.SAMPLE_REQUESTREFERENCE_KEY_ID),
				new QueryColumn(LCSSampleRequest.class,
						BurConstant.OBJECTIDENTIFIERID));

		// Append Color Reference to SKU
		sampleReqFromColourwayStatement.appendJoin(new QueryColumn(
				LCSSample.class, BurSampleConstant.COLOUR_REFERENCE_KEY_ID),
				new QueryColumn(LCSSKU.class,
						BurConstant.MASTERREFERENCE_KEY_ID));

		// Append Basic criteria
		sampleReqFromColourwayStatement.appendAndIfNeeded();
		sampleReqFromColourwayStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSSKU.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		sampleReqFromColourwayStatement.appendAndIfNeeded();
		sampleReqFromColourwayStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSSKU.class,
						BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));
		sampleReqFromColourwayStatement.appendAndIfNeeded();
		sampleReqFromColourwayStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSSKU.class, BurSampleConstant.PLACEHOLDER),
				STR_VAL_ZERO, Criteria.EQUALS));

		logger.info(methodName
				+ "Sample Request Prepared Query Statement for Associated Colourway: "
				+ sampleReqFromColourwayStatement.toString());

		// Execute the query
		resultsSampleReqforColourway = LCSQuery.runDirectQuery(
				sampleReqFromColourwayStatement).getResults();
		logger.debug(methodName + "resultsSampleReqforProduct: "
				+ resultsSampleReqforColourway);

		// Track End Time for Query Execution
		long sampReqForColourwayEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampReqForColourwayEndTime: ");
		logger.info(methodName
				+ "Sample Request for Associated Colourway Query Total Execution Time (ms): "
				+ (sampReqForColourwayEndTime - sampReqForColourwayStartTime));

		// Return Statement
		return (Collection<Map>) resultsSampleReqforColourway;
	}

	/**
	 * Method to generate prepared query statement to get sample request from
	 * Colourway Season.
	 * 
	 * @param colourwaySeasonQueryCriteria
	 *            Collection
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */

	public static Collection<Map> getAssociatedSampleRequestFromColorwaySeason(
			Collection<Map> colourwaySeasonQueryCriteria) throws WTException {

		// Set Method Name
		String methodName = "getAssociatedSampleRequestforColorwaySeason() ";

		// Track Start Time for Query Execution
		long sampReqForColourSeasonStartTime = BurberryAPIUtil
				.printCurrentTime(methodName, "sampReqForColourwayStartTime: ");

		// Initialisation
		Collection<Map> resultsSampleReqforColourwaySeason = new ArrayList<Map>();
		logger.debug(methodName + "colourwayQueryCriteria: "
				+ colourwaySeasonQueryCriteria);
		if (colourwaySeasonQueryCriteria.isEmpty()) {
			return resultsSampleReqforColourwaySeason;
		}

		// Create a new prepared query statement
		PreparedQueryStatement sampleReqFromColourwaySeasonStatement = new PreparedQueryStatement();
		// Append Distinct
		sampleReqFromColourwaySeasonStatement.setDistinct(true);

		// Append Table Name
		sampleReqFromColourwaySeasonStatement.appendFromTable(LCSSample.class);
		sampleReqFromColourwaySeasonStatement
				.appendFromTable(LCSSampleRequest.class);
		sampleReqFromColourwaySeasonStatement
				.appendFromTable(LCSProductSeasonLink.class);
		sampleReqFromColourwaySeasonStatement.appendFromTable(LCSSKU.class);
		sampleReqFromColourwaySeasonStatement
				.appendFromTable(LCSSKUSeasonLink.class);

		// Append Select Columns
		sampleReqFromColourwaySeasonStatement
				.appendSelectColumn(new QueryColumn(LCSSampleRequest.class,
						BurConstant.OBJECTIDENTIFIERID));
		sampleReqFromColourwaySeasonStatement
				.appendSelectColumn(new QueryColumn(LCSSKUSeasonLink.class,
						BurConstant.OBJECTIDENTIFIERID));
		sampleReqFromColourwaySeasonStatement
				.appendSelectColumn(new QueryColumn(LCSProductSeasonLink.class,
						BurConstant.OBJECTIDENTIFIERID));
		sampleReqFromColourwaySeasonStatement
				.appendSelectColumn(new QueryColumn(LCSSample.class,
						BurConstant.OBJECTIDENTIFIERID));

		// Append Search Criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				sampleReqFromColourwaySeasonStatement,
				colourwaySeasonQueryCriteria);

		// Append Join between PRODUCTSEASONLINK and SKUSEASONLINK
		sampleReqFromColourwaySeasonStatement.appendJoin(
				BurConstant.LCSPRODUCTSEASONLINK, BurConstant.PRODUCTMASTERID,
				BurConstant.LCSSKUSEASONLINK, BurConstant.PRODUCTMASTERID);

		// Append Join between PRODUCTSEASONLINK and SKUSEASONLINK
		sampleReqFromColourwaySeasonStatement.appendJoin(
				BurConstant.LCSPRODUCTSEASONLINK,
				BurSampleConstant.SEASONREVID, BurConstant.LCSSKUSEASONLINK,
				BurSampleConstant.SEASONREVID);

		// Append Join between SKU and SKUSEASONLINK
		sampleReqFromColourwaySeasonStatement.appendJoin(BurConstant.LCSSKU,
				BurConstant.IDA3MASTERREFEREN_CE, BurConstant.LCSSKUSEASONLINK,
				BurConstant.SKUMASTERID);

		// Append Join between Sample and Sample Request
		sampleReqFromColourwaySeasonStatement.appendJoin(new QueryColumn(
				LCSSample.class,
				BurSampleConstant.SAMPLE_REQUESTREFERENCE_KEY_ID),
				new QueryColumn(LCSSampleRequest.class,
						BurConstant.OBJECTIDENTIFIERID));

		// Append Color Reference to SKU
		sampleReqFromColourwaySeasonStatement.appendJoin(new QueryColumn(
				LCSSample.class, BurSampleConstant.COLOUR_REFERENCE_KEY_ID),
				new QueryColumn(LCSSKU.class,
						BurConstant.MASTERREFERENCE_KEY_ID));

		// Append Basic Criteria
		sampleReqFromColourwaySeasonStatement.appendAnd();
		sampleReqFromColourwaySeasonStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSSKU.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		sampleReqFromColourwaySeasonStatement.appendAnd();
		sampleReqFromColourwaySeasonStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSSKU.class,
						BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));
		sampleReqFromColourwaySeasonStatement.appendAnd();
		sampleReqFromColourwaySeasonStatement
				.appendCriteria(new Criteria(new QueryColumn(
						LCSSKUSeasonLink.class, BurConstant.EFFECTLATEST),
						STR_VAL_ONE, Criteria.EQUALS));

		sampleReqFromColourwaySeasonStatement.appendAnd();
		sampleReqFromColourwaySeasonStatement.appendCriteria(new Criteria(
				new QueryColumn(BurConstant.LCSSKUSEASONLINK,
						BurConstant.SEASONREMOVED), STR_VAL_ZERO,
				Criteria.EQUALS));

		// Append Basic Criteria
		sampleReqFromColourwaySeasonStatement.appendAndIfNeeded();
		sampleReqFromColourwaySeasonStatement
				.appendCriteria(new Criteria(new QueryColumn(
						LCSProductSeasonLink.class, BurConstant.EFFECTLATEST),
						STR_VAL_ONE, Criteria.EQUALS));
		sampleReqFromColourwaySeasonStatement.appendAndIfNeeded();
		sampleReqFromColourwaySeasonStatement.appendCriteria(new Criteria(
				new QueryColumn(BurConstant.LCSPRODUCTSEASONLINK,
						BurConstant.SEASONREMOVED), STR_VAL_ZERO,
				Criteria.EQUALS));

		logger.info(methodName
				+ "Sample Request Prepared Query Statement for Associated Colourway Season: "
				+ sampleReqFromColourwaySeasonStatement.toString());

		// Execute the query
		resultsSampleReqforColourwaySeason = LCSQuery.runDirectQuery(
				sampleReqFromColourwaySeasonStatement).getResults();
		logger.debug(methodName + "resultsSampleReqforColourwaySeason: "
				+ resultsSampleReqforColourwaySeason);

		// Track End Time for Query Execution
		long sampReqForColourSeasonEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampReqForColourSeasonEndTime: ");
		logger.info(methodName
				+ "Sample Request for Associated Colourway Season Query Total Execution Time (ms): "
				+ (sampReqForColourSeasonEndTime - sampReqForColourSeasonStartTime));

		// Return Statement
		return (Collection<Map>) resultsSampleReqforColourwaySeason;
	}

}
