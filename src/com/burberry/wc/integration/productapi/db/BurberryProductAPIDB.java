package com.burberry.wc.integration.productapi.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.productbomapi.db.BurberryProductBOMAPIDBHelper;
import com.burberry.wc.integration.util.BurConstant;
import com.burberry.wc.integration.util.BurberryAPIDBUtil;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.placeholder.Placeholder;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;

/**
 * A DB class to handle Database activity. Class contain several method to
 * handle DB activity i.e. creating DB query, statement, and result set
 * allocation. Collection of Criteria Map, Start Date, End Date and Modify are
 * Primary input to fetch details from Flex system.
 *
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryProductAPIDB {

	/**
	 * Default Constructor.
	 */
	private BurberryProductAPIDB() {

	}

	/**
	 * STR_VAL_A.
	 */
	private static final String STR_VAL_A = "A";

	/**
	 * STR_QUESTION.
	 */
	private static final String STR_QUESTION = "?";

	/**
	 * STR_VAL_ONE.
	 */
	private static final String STR_VAL_ONE = "1";

	/**
	 * STR_VAL_ONE.
	 */
	private static final String STR_VAL_ZERO = "0";

	/**
	 * STR_PRODUCT.
	 */
	private static final String STR_PRODUCT = "PRODUCT";

	/**
	 * STR_IDA2A2.
	 */
	private static final String STR_IDA2A2 = "IDA2A2";

	/**
	 * STR_WRK.
	 */
	private static final String STR_WRK = "wrk";

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductAPIDB.class);

	/**
	 * Method to get the results from Product table based on criteria.
	 * 
	 * @param colProductCriteria
	 *            Collection of Product Criteria
	 * @param bomApi 
	 * @return results
	 * @throws WTException
	 *             throw exception
	 */
	public static Collection<Map> getAssociatedProducts(
			Collection<Map> colProductCriteria, boolean bomApi) throws WTException {

		String methodName = "getAssociatedProducts() ";

		// Method Start Time
		long lProdStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product Query Start Time: ");

		Collection<Map> resultProducts = new ArrayList<Map>();
		logger.debug(methodName + "colProductCriteria: " + colProductCriteria);
		if (colProductCriteria.isEmpty()) {
			return resultProducts;
		}

		// Create a new prepared query statement
		PreparedQueryStatement productStatement = new PreparedQueryStatement();
		// Append distinct
		productStatement.setDistinct(true);
		// Append Table
		productStatement.appendFromTable(LCSProduct.class);
		// Append Select columns
		productStatement.appendSelectColumn(new QueryColumn(LCSProduct.class,
				BurConstant.LCS_PRODUCT_OBJECTIDENTIFIERID));
		productStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		// Append Criteria
		productStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		productStatement.appendAnd();
		productStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(productStatement,
				colProductCriteria);
		
		//Appen BOM criteria   
		if(bomApi){
			productStatement.appendFromTable(FlexBOMPart.class);
			BurberryProductBOMAPIDBHelper.appendAdditionalBOMCriteriaByTableName(
					productStatement, FlexBOMPart.class,false);
		}

		logger.info(methodName + "Product Prepared Query Statement: "
				+ productStatement.toString());

		// Execute the query
		resultProducts = LCSQuery.runDirectQuery(productStatement).getResults();

		// Method End Time
		long lProdEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product Query End Time: ");

		logger.info(methodName + "Product Query Total Execution Time (ms): "
				+ (lProdEndTime - lProdStartTime));
		logger.debug(methodName + "Product Query Results: " + resultProducts);
		return (Collection<Map>) resultProducts;
	}

	/**
	 * Method to get Product and Product Season Links id's using criteria map.
	 * 
	 * @param colProductCriteria
	 *            Collection of Product Criteria
	 * @param b 
	 * @return results
	 * @throws WTException
	 *             throw exception
	 */
	public static Collection<Map> getAssociatedProductFromSeasonLink(
			Collection<Map> colProductCriteria, boolean bomApi) throws WTException {

		String methodName = "getAssociatedProductFromSeasonLink() ";
		long lPSStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product Season Link Query Start Time: ");

		Collection<Map> resultProdSeasonLink = new ArrayList<Map>();
		logger.debug(methodName + "colProductCriteria1: " + colProductCriteria);
		if (colProductCriteria.isEmpty()) {
			return resultProdSeasonLink;
		}

		// Create a new prepared query statement
		PreparedQueryStatement prodSeasonLinkStatement = new PreparedQueryStatement();
		// Append distinct
		prodSeasonLinkStatement.setDistinct(true);
		// Append table
		prodSeasonLinkStatement.appendFromTable(LCSProduct.class);
		// Append select column
		prodSeasonLinkStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		prodSeasonLinkStatement.appendSelectColumn(new QueryColumn(
				LCSProductSeasonLink.class, BurConstant.OBJECTIDENTIFIERID));
		// Append Criteria
		prodSeasonLinkStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		prodSeasonLinkStatement.appendAnd();
		prodSeasonLinkStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		prodSeasonLinkStatement.appendFromTable(LCSProductSeasonLink.class);
		prodSeasonLinkStatement.appendJoin(BurConstant.LCSPRODUCT,
				BurConstant.IDA3MASTERREFEREN_CE,
				BurConstant.LCSPRODUCTSEASONLINK, BurConstant.PRODUCTMASTERID);
		prodSeasonLinkStatement.appendAnd();
		prodSeasonLinkStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProductSeasonLink.class, BurConstant.EFFECTLATEST),
				STR_VAL_ONE, Criteria.EQUALS));

		prodSeasonLinkStatement.appendAnd();
		// Defect Fix: Start
		prodSeasonLinkStatement.appendCriteria(new Criteria(new QueryColumn(
				BurConstant.LCSPRODUCTSEASONLINK, BurConstant.SEASONREMOVED),
				STR_VAL_ZERO, Criteria.EQUALS));
		// Defect Fix: End

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				prodSeasonLinkStatement, colProductCriteria);
		
		//Appen BOM criteria     
		if(bomApi){
			prodSeasonLinkStatement.appendFromTable(FlexBOMPart.class);
			BurberryProductBOMAPIDBHelper.appendAdditionalBOMCriteriaByTableName(
					prodSeasonLinkStatement, FlexBOMPart.class,false);
		}

		logger.info(methodName
				+ "Product Season Link Prepared Query Statement: "
				+ prodSeasonLinkStatement.toString());
		// Exceute the query
		resultProdSeasonLink = LCSQuery.runDirectQuery(prodSeasonLinkStatement)
				.getResults();

		long lPSEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product Season Link Query End Time: ");

		logger.info(methodName
				+ "Product Season Link Total Execution Time (ms): "
				+ (lPSEndTime - lPSStartTime));
		logger.debug(methodName + "resultProdSeasonLink: "
				+ resultProdSeasonLink);
		return (Collection<Map>) resultProdSeasonLink;
	}

	/**
	 * Method to get Product and Colourway id's using criteria map.
	 * 
	 * @param colProdColorwayCriteria
	 *            Collection of Product Criteria
	 * @param b 
	 * @return results
	 * @throws WTException
	 *             throw exception
	 */
	public static Collection<Map> getAssociatedProductFromColorway(
			Collection<Map> colProdColorwayCriteria, boolean bomApi) throws WTException {

		String methodName = "getAssociatedProductFromColorway() ";
		long lPCStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product Colourway Query Start Time: ");

		Collection<Map> resultProductColorway = new ArrayList<Map>();
		logger.debug(methodName + "colProdColorwayCriteria: "
				+ colProdColorwayCriteria);
		if (colProdColorwayCriteria.isEmpty()) {
			return resultProductColorway;
		}
		// Create a new prepared query statement
		PreparedQueryStatement prodSKUStatement = new PreparedQueryStatement();
		// Append distinct
		prodSKUStatement.setDistinct(true);
		// Append table
		prodSKUStatement.appendFromTable(LCSProduct.class);
		// Append select column
		prodSKUStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		prodSKUStatement.appendSelectColumn(new QueryColumn(BurConstant.LCSSKU,
				BurConstant.BRANCHID));

		// Append Criteria
		prodSKUStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		prodSKUStatement.appendAnd();
		prodSKUStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		// Append table
		prodSKUStatement.appendFromTable(LCSSKU.class);
		// statement.appendJoin(BurConstant.LCSSKU,BurConstant.IDA3MASTERREFEREN_CE,
		// BurConstant.LCSSKUSEASONLINK,BurConstant.SKUMASTERID);
		// Append Joins
		prodSKUStatement.appendJoin(BurConstant.LCSPRODUCT,
				BurConstant.BRANCHID, BurConstant.LCSSKU,
				BurConstant.PRODUCTAREVID);
		prodSKUStatement.appendAnd();
		// Append criteria
		prodSKUStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSKU.class, BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));
		prodSKUStatement.appendAnd();
		prodSKUStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSKU.class, BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));

		// Defect Fix: Start
		prodSKUStatement.appendAndIfNeeded();
		prodSKUStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSKU.class, BurPaletteMaterialConstant.PLACEHOLDER),
				STR_VAL_ZERO, Criteria.EQUALS));
		// Defect Fix: End

		// Append additional criteria
		// ProductAPIDBHelper.appendRequestCriteriaToStatement(prodSKUStatement,
		// colProdColorwayCriteria);

		BurberryProductAPIDBHelper.appendProductSKUCriteriaToStatement(
				prodSKUStatement, colProdColorwayCriteria);

		
		//Appen BOM criteria    
		if(bomApi){
			prodSKUStatement.appendFromTable(FlexBOMPart.class);
			BurberryProductBOMAPIDBHelper.appendAdditionalBOMCriteriaByTableName(
					prodSKUStatement, FlexBOMPart.class,false);
		}
		logger.info(methodName + "Product Colourway Prepared Query Statement: "
				+ prodSKUStatement.toString());
		// Exceute the results
		resultProductColorway = LCSQuery.runDirectQuery(prodSKUStatement)
				.getResults();

		long lPCEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product Colourway Query End Time: ");

		logger.info(methodName
				+ "Product Colourway Query Total Execution Time (ms): "
				+ (lPCEndTime - lPCStartTime));

		logger.debug(methodName + "resultProductColorway:"
				+ resultProductColorway);

		return (Collection<Map>) resultProductColorway;
	}

	/**
	 * Method to get Product and Colourway Season Link id's using criteria map.
	 * 
	 * @param colProdColorwaySeason
	 *            Collection of Product Criteria
	 * @param b 
	 * @return results
	 * @throws WTException
	 *             throw exception
	 */
	public static Collection<Map> getAssociatedProductFromColorwaySeasonLink(
			Collection<Map> colProdColorwaySeason, boolean bomApi) throws WTException {

		String methodName = "getAssociatedProductFromColorwaySeasonLink() ";
		long lProdSKUSeasonStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Colourway Season Query Start Time: ");

		Collection<Map> resultProductSKUSeasonLink = new ArrayList<Map>();
		logger.debug(methodName + "colProdColorwaySeason: "
				+ colProdColorwaySeason);
		if (colProdColorwaySeason.isEmpty()) {
			return resultProductSKUSeasonLink;
		}

		// Create a new prepared query statement
		PreparedQueryStatement prodSKUSeasonLinkStatement = new PreparedQueryStatement();
		// Append distinct
		prodSKUSeasonLinkStatement.setDistinct(true);
		// Append table
		prodSKUSeasonLinkStatement.appendFromTable(LCSProduct.class);
		// Append Select Column
		prodSKUSeasonLinkStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		prodSKUSeasonLinkStatement.appendSelectColumn(new QueryColumn(
				LCSSKUSeasonLink.class, BurConstant.OBJECTIDENTIFIERID));
		// Append Criteria
		prodSKUSeasonLinkStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		prodSKUSeasonLinkStatement.appendAnd();
		prodSKUSeasonLinkStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));
		// Append additional tables
		prodSKUSeasonLinkStatement.appendFromTable(LCSSKU.class);
		prodSKUSeasonLinkStatement.appendFromTable(LCSSKUSeasonLink.class);
		// Append Joins
		prodSKUSeasonLinkStatement.appendJoin(BurConstant.LCSSKU,
				BurConstant.IDA3MASTERREFEREN_CE, BurConstant.LCSSKUSEASONLINK,
				BurConstant.SKUMASTERID);
		prodSKUSeasonLinkStatement.appendJoin(BurConstant.LCSPRODUCT,
				BurConstant.IDA3MASTERREFEREN_CE, BurConstant.LCSSKUSEASONLINK,
				BurConstant.PRODUCTMASTERID);
		// Append Criteria
		prodSKUSeasonLinkStatement.appendAnd();
		prodSKUSeasonLinkStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSKU.class, BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));
		prodSKUSeasonLinkStatement.appendAnd();
		prodSKUSeasonLinkStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSKU.class, BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));
		prodSKUSeasonLinkStatement.appendAnd();
		prodSKUSeasonLinkStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSKUSeasonLink.class, BurConstant.EFFECTLATEST), STR_VAL_ONE,
				Criteria.EQUALS));

		prodSKUSeasonLinkStatement.appendAnd();
		// Defect Fix: Start
		prodSKUSeasonLinkStatement.appendCriteria(new Criteria(new QueryColumn(
				BurConstant.LCSSKUSEASONLINK, BurConstant.SEASONREMOVED),
				STR_VAL_ZERO, Criteria.EQUALS));
		// Defect Fix: End

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				prodSKUSeasonLinkStatement, colProdColorwaySeason);

		
		
		//Appen BOM criteria    
		if(bomApi){
			prodSKUSeasonLinkStatement.appendFromTable(FlexBOMPart.class);
			BurberryProductBOMAPIDBHelper.appendAdditionalBOMCriteriaByTableName(
					prodSKUSeasonLinkStatement, FlexBOMPart.class,false);
		}
		logger.info(methodName
				+ "Product Colourway Season Prepared Query Statement: "
				+ prodSKUSeasonLinkStatement.toString());
		// Execute the query
		resultProductSKUSeasonLink = LCSQuery.runDirectQuery(
				prodSKUSeasonLinkStatement).getResults();

		long lProdSKUSeasonEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Colourway Season Query End Time: ");

		logger.info(methodName
				+ "Product Colourway Season Total Execution Time (ms): "
				+ (lProdSKUSeasonEndTime - lProdSKUSeasonStartTime));
		logger.debug(methodName + "resultProductSKUSeasonLink:"
				+ resultProductSKUSeasonLink);
		return (Collection<Map>) resultProductSKUSeasonLink;
	}

	/**
	 * Method to get Product and Sourcing Config id's using criteria map.
	 * 
	 * @param colCriteria
	 *            Collection of Product Criteria
	 * @return results
	 * @throws WTException
	 *             throw exception
	 */
	public static Collection<Map> getAssociatedProductFromSource(
			Collection<Map> colCriteria) throws WTException {

		String methodName = "getAssociatedProductFromSource() ";
		long lProdSourcStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product From Source Query Start Time: ");

		Collection<Map> resultProductSource = new ArrayList<Map>();
		logger.debug(methodName + "colCriteria: " + colCriteria);
		if (colCriteria.isEmpty()) {
			return resultProductSource;
		}
		// Create a new prepared query statement
		PreparedQueryStatement prodSourceStatement = new PreparedQueryStatement();
		// Append distinct
		prodSourceStatement.setDistinct(true);
		// Append table
		prodSourceStatement.appendFromTable(LCSProduct.class);
		// Append select column
		prodSourceStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		prodSourceStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.BRANCHID));
		prodSourceStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		// Append Criteria
		prodSourceStatement.appendAnd();
		prodSourceStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));
		// Append table
		prodSourceStatement.appendFromTable(LCSSourcingConfig.class);
		prodSourceStatement.appendJoin(new QueryColumn(BurConstant.LCSPRODUCT,
				BurConstant.BRANCHID), new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.PRODUCTAREVID));
		// Append Criteria
		prodSourceStatement.appendAndIfNeeded();
		prodSourceStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSourcingConfig.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		prodSourceStatement.appendAnd();
		prodSourceStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSourcingConfig.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(prodSourceStatement,
				colCriteria);

		logger.info(methodName
				+ "Product From Source Prepared Query Statement: "
				+ prodSourceStatement.toString());
		// Exceute the query
		resultProductSource = LCSQuery.runDirectQuery(prodSourceStatement)
				.getResults();

		long lProdSourcEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product From Source Query End Time: ");

		logger.info(methodName
				+ "Product From Source Total Execution Time (ms): "
				+ (lProdSourcEndTime - lProdSourcStartTime));
		logger.debug(methodName + "resultProductSource:" + resultProductSource);
		return (Collection<Map>) resultProductSource;
	}

	/**
	 * Method to get Product and Placeholder id's using criteria map.
	 * 
	 * @param colProductPlaceholder
	 *            Collection of Product Criteria
	 * @return results
	 * @throws WTException
	 *             throw exception
	 */
	public static Collection<Map> getAssociatedProductFromPlaceholder(
			Collection<Map> colProductPlaceholder) throws WTException {

		String methodName = "getAssociatedProductFromPlaceholder() ";
		long lProdPlaceholderStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product From Placeholder Query Start Time: ");

		Collection<Map> resultProductPlaceholder = new ArrayList<Map>();
		logger.debug(methodName + "colProductPlaceholder: "
				+ colProductPlaceholder);
		if (colProductPlaceholder.isEmpty()) {
			return resultProductPlaceholder;
		}
		// Create a new prepared query statement
		PreparedQueryStatement productPlaceholderStatement = new PreparedQueryStatement();
		// Append Distinct
		productPlaceholderStatement.setDistinct(true);
		// Append Table
		productPlaceholderStatement.appendFromTable(LCSProduct.class);
		// Append Select columns
		productPlaceholderStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		// Defect fix to get product season belonging to placeholder criteria
		productPlaceholderStatement.appendSelectColumn(new QueryColumn(
				LCSProductSeasonLink.class, BurConstant.OBJECTIDENTIFIERID));
		// Append Criteria
		productPlaceholderStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProduct.class,
						BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));
		productPlaceholderStatement.appendAnd();
		productPlaceholderStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSProduct.class,
						BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));
		// Append tables
		productPlaceholderStatement.appendFromTable(Placeholder.class);
		productPlaceholderStatement.appendFromTable(LCSSeason.class);
		productPlaceholderStatement.appendFromTable(LCSProductSeasonLink.class);
		// Append Joins
		productPlaceholderStatement.appendJoin(new QueryColumn(
				Placeholder.class, BurConstant.SEASONMASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSSeason.class,
						BurConstant.MASTERREFERENCE_KEY_ID));

		productPlaceholderStatement.appendJoin(BurConstant.LCSPRODUCT,
				BurConstant.IDA3MASTERREFEREN_CE,
				BurConstant.LCSPRODUCTSEASONLINK, BurConstant.PRODUCTMASTERID);

		productPlaceholderStatement.appendJoin(new QueryColumn(
				BurConstant.LCSPRODUCTSEASONLINK, LCSProductSeasonLink.class,
				BurConstant.ROLEBOBJECTREF_KEY_ID), new QueryColumn(
				LCSSeason.class, BurConstant.MASTERREFERENCE_KEY_ID));

		// Defect:Start
		productPlaceholderStatement.appendJoin(new QueryColumn(
				Placeholder.class, BurConstant.OBJECTIDENTIFIERID),
				new QueryColumn(LCSProductSeasonLink.class,
						BurConstant.PLACEHOLDER_REF));

		// Defect:End

		productPlaceholderStatement.appendAndIfNeeded();
		// Append Criteria
		productPlaceholderStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSSeason.class,
						BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		productPlaceholderStatement.appendAndIfNeeded();
		productPlaceholderStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSSeason.class,
						BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));
		productPlaceholderStatement.appendAndIfNeeded();
		productPlaceholderStatement
				.appendCriteria(new Criteria(new QueryColumn(
						LCSProductSeasonLink.class, BurConstant.EFFECTLATEST),
						STR_VAL_ONE, Criteria.EQUALS));

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				productPlaceholderStatement, colProductPlaceholder);

		logger.info(methodName
				+ "Product From Placeholder Prepared Query Statement: "
				+ productPlaceholderStatement.toString());
		// Exceute the query
		resultProductPlaceholder = LCSQuery.runDirectQuery(
				productPlaceholderStatement).getResults();

		long lProdPlaceholderEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product From Placeholder Query End Time: ");

		logger.info(methodName
				+ "Product From Placeholder Total Execution Time (ms): "
				+ (lProdPlaceholderEndTime - lProdPlaceholderStartTime));
		logger.debug(methodName + "resultProductPlaceholder:"
				+ resultProductPlaceholder);
		return (Collection<Map>) resultProductPlaceholder;

	}

	/**
	 * Method to get Product and Season id's using criteria map.
	 * 
	 * @param colProdSeason
	 *            Product Criteria
	 * @param bomApi 
	 * @return results
	 * @throws WTException
	 *             throw exception
	 */
	public static Collection<Map> getAssociatedProductFromSeason(
			Collection<Map> colProdSeason, boolean bomApi) throws WTException {

		String methodName = "getAssociatedProductFromSeason() ";
		long lProdSeasonStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product From Season Query Start Time: ");

		Collection<Map> resultProdSeason = new ArrayList<Map>();
		logger.debug(methodName + "colProdSeason: " + colProdSeason);
		if (colProdSeason.isEmpty()) {
			return resultProdSeason;
		}

		// Create a new prepared query statement
		PreparedQueryStatement prodSeasonStatement = new PreparedQueryStatement();
		// Append Distinct
		prodSeasonStatement.setDistinct(true);
		// Append Table
		prodSeasonStatement.appendFromTable(LCSProduct.class);
		// Append Select Column
		prodSeasonStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSSEASON, BurConstant.BRANCHID));
		prodSeasonStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		// Append Criteria
		prodSeasonStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		prodSeasonStatement.appendAnd();
		prodSeasonStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));
		// Append table
		prodSeasonStatement.appendFromTable(LCSSeason.class);
		prodSeasonStatement.appendFromTable(LCSProductSeasonLink.class);
		// Append Joins
		prodSeasonStatement.appendJoin(BurConstant.LCSPRODUCT,
				BurConstant.IDA3MASTERREFEREN_CE,
				BurConstant.LCSPRODUCTSEASONLINK, BurConstant.PRODUCTMASTERID);
		prodSeasonStatement.appendJoin(new QueryColumn(
				BurConstant.LCSPRODUCTSEASONLINK, LCSProductSeasonLink.class,
				"roleBObjectRef.key.id"), new QueryColumn(LCSSeason.class,
				"masterReference.key.id"));
		prodSeasonStatement.appendAnd();
		// Append Criteria
		prodSeasonStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProductSeasonLink.class, BurConstant.EFFECTLATEST),
				STR_VAL_ONE, Criteria.EQUALS));

		prodSeasonStatement.appendAnd();
		prodSeasonStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProductSeasonLink.class, BurConstant.SEASONLINKTYPE),
				STR_PRODUCT, Criteria.EQUALS));

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(prodSeasonStatement,
				colProdSeason);
		
		//Appen BOM criteria   
		if(bomApi){
			prodSeasonStatement.appendFromTable(FlexBOMPart.class);
			BurberryProductBOMAPIDBHelper.appendAdditionalBOMCriteriaByTableName(
					prodSeasonStatement, FlexBOMPart.class,false);
		}

		logger.info(methodName
				+ "Product From Season Prepared Query Statement: "
				+ prodSeasonStatement.toString());
		// Exceute the query
		resultProdSeason = LCSQuery.runDirectQuery(prodSeasonStatement)
				.getResults();

		long lProdSeasonEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product From Season Query End Time: ");

		logger.info(methodName
				+ "Product From Season Total Execution Time (ms): "
				+ (lProdSeasonEndTime - lProdSeasonStartTime));
		logger.debug(methodName + "resultProdSeason:" + resultProdSeason);
		return (Collection<Map>) resultProdSeason;
	}

	/**
	 * Method to get Product and Main RM Material using criteria map.
	 * 
	 * @param colProductMaterial
	 *            Product Criteria
	 * @param bomApi 
	 * @return results
	 * @throws WTException
	 *             throw exception
	 */
	public static Collection<Map> getAssociatedProductFromMainRMMaterial(
			Collection<Map> colProductMaterial, boolean bomApi) throws WTException {

		String methodName = "getAssociatedProductFromMainRMMaterial() ";

		long lProdMaterialStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product From Material Query Start Time: ");

		Collection<Map> resultProductMaterial = new ArrayList<Map>();
		logger.debug(methodName + "colProductMaterial: " + colProductMaterial);
		if (colProductMaterial.isEmpty()) {
			return resultProductMaterial;
		}

		FlexType productType = FlexTypeCache
				.getFlexTypeRootByClass((LCSProduct.class).getName());
		FlexTypeAttribute fta = productType
				.getAttribute(BurConstant.BUR_RM_MAIN);

		// Create a new prepared query statement
		PreparedQueryStatement prodMaterialStatement = new PreparedQueryStatement();
		// Append distinct
		prodMaterialStatement.setDistinct(true);
		// Append table
		prodMaterialStatement.appendFromTable(LCSProduct.class);

		// statement.appendSelectColumn(new
		// QueryColumn(LCSProduct.class,BurConstant.LCS_PRODUCT_OBJECTIDENTIFIERID));
		// Append select column
		prodMaterialStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		prodMaterialStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		prodMaterialStatement.appendAnd();
		prodMaterialStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		// Append table
		prodMaterialStatement.appendFromTable(LCSMaterial.class);

		// Join Between Product and Material
		prodMaterialStatement.appendJoin(BurConstant.LCSPRODUCT,
				fta.getColumnName(), BurConstant.LCSMATERIAL,
				BurConstant.BRANCHID);

		prodMaterialStatement.appendAnd();
		prodMaterialStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		prodMaterialStatement.appendAnd();
		prodMaterialStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));
		prodMaterialStatement.appendAndIfNeeded();
		prodMaterialStatement.appendCriteria(new Criteria(
				new QueryColumn(BurConstant.LCSMATERIAL,
						BurConstant.MATERIAL_STATECHECKOUTINFO), "wrk",
				Criteria.NOT_EQUAL_TO));

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				prodMaterialStatement, colProductMaterial);

		//Appen BOM criteria    
		if(bomApi){
			prodMaterialStatement.appendFromTable(FlexBOMPart.class);
			BurberryProductBOMAPIDBHelper.appendAdditionalBOMCriteriaByTableName(
					prodMaterialStatement, FlexBOMPart.class,false);
		}
		logger.info(methodName
				+ "Product From Material Prepared Query Statement: "
				+ prodMaterialStatement.toString());
		// Exceute the query
		resultProductMaterial = LCSQuery.runDirectQuery(prodMaterialStatement)
				.getResults();

		long lProdMaterialEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product From Material Query End Time: ");

		logger.info(methodName
				+ "Product From Material Total Execution Time (ms): "
				+ (lProdMaterialEndTime - lProdMaterialStartTime));
		logger.debug(methodName + "resultProductMaterial:"
				+ resultProductMaterial);
		return (Collection<Map>) resultProductMaterial;

	}

	/**
	 * Method to get Product and Commodity Code using criteria map.
	 * 
	 * @param colProdCC
	 *            Product Criteria
	 * @return results
	 * @throws WTException
	 *             throw exception
	 */
	public static Collection<Map> getAssociatedProductFromCommodityCode(
			Collection<Map> colProdCC) throws WTException {

		String methodName = "getAssociatedProductFromCommodityCode() ";
		long lProdCCStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product From Commodity Query Start Time: ");

		Collection<Map> resultProductCC = new ArrayList<Map>();
		logger.debug(methodName + "colProdCC: " + colProdCC);
		if (colProdCC.isEmpty()) {
			return resultProductCC;
		}

		FlexType productType = FlexTypeCache
				.getFlexTypeRootByClass((LCSProduct.class).getName());
		FlexTypeAttribute flexTypeAttribute = productType
				.getAttribute(BurConstant.BUR_CC);

		FlexType commodityCodeFlexType = FlexTypeCache
				.getFlexTypeFromPath("Business Object\\burCommodityCode");

		// Create a new prepared query statement
		PreparedQueryStatement prodCCStatement = new PreparedQueryStatement();
		// Append Distinct
		prodCCStatement.setDistinct(true);
		// Append Table
		prodCCStatement.appendFromTable(LCSProduct.class);
		// Append Select COlumns
		prodCCStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		prodCCStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		prodCCStatement.appendAnd();
		prodCCStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));
		// Append table
		prodCCStatement.appendFromTable(LCSLifecycleManaged.class);

		// Join Between Product and Commodity Code
		prodCCStatement.appendJoin(BurConstant.LCSPRODUCT,
				flexTypeAttribute.getColumnName(),
				BurConstant.LCSLIFECYCLEMANAGED, STR_IDA2A2);

		prodCCStatement.appendAnd();
		prodCCStatement.appendCriteria(new Criteria(
				BurConstant.LCSLIFECYCLEMANAGED, "flexTypeIdPath",
				STR_QUESTION, Criteria.LIKE),
				"%\\" + commodityCodeFlexType.getIdNumber() + "%");

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(prodCCStatement,
				colProdCC);

		logger.info(methodName
				+ "Product From Commodity Prepared Query Statement: "
				+ prodCCStatement.toString());
		// Exceute the query
		resultProductCC = LCSQuery.runDirectQuery(prodCCStatement).getResults();

		long lProdCCEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product From Commodity Query End Time: ");

		logger.info(methodName
				+ "Product From Commodity Total Execution Time (ms): "
				+ (lProdCCEndTime - lProdCCStartTime));
		logger.debug(methodName + "resultProductCC:" + resultProductCC);
		return (Collection<Map>) resultProductCC;

	}

}
