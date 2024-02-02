package com.burberry.wc.integration.productbomapi.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.productbomapi.constant.BurProductBOMConstant;
import com.burberry.wc.integration.util.BurConstant;
import com.burberry.wc.integration.util.BurberryAPIDBUtil;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sourcing.LCSSourceToSeasonLinkMaster;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
/**
 * A DB class to handle Database activity. Class contain several method to
 * handle DB activity i.e. creating DB query, statement, and result set
 * allocation. Collection of Criteria Map, Start Date, End Date and Modify are
 * Primary input to fetch details from Flex system.
 *
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryProductBOMAPIDB {

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductBOMAPIDB.class);

	/**
	 * STR_VAL_A.
	 */
	private static final String STR_VAL_A = "A";

	/**
	 * STR_VAL_ONE.
	 */
	private static final String STR_VAL_ONE = "1";

	/**
	 * Private Constructor. BurberryProductBomApiDB()
	 */
	private BurberryProductBOMAPIDB() {

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
		long lStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"source startTime: ");
		Collection<Map> resultProductSource = new ArrayList<Map>();
		logger.debug(methodName + "colSourceConfigCriteria: " + colCriteria);
		if (colCriteria.isEmpty()) {
			return resultProductSource;
		}
		// Create a new prepared query statement
		PreparedQueryStatement prodPrimSourceStatement = new PreparedQueryStatement();
		// Append distinct
		prodPrimSourceStatement.setDistinct(true);
		// Append table
		prodPrimSourceStatement.appendFromTable(LCSProduct.class);
		// Append select column
		prodPrimSourceStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		prodPrimSourceStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.BRANCHID));
		prodPrimSourceStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		// Append Criteria
		prodPrimSourceStatement.appendAnd();
		prodPrimSourceStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));
		// Append table
		prodPrimSourceStatement.appendFromTable(LCSSourcingConfig.class);
		prodPrimSourceStatement.appendJoin(new QueryColumn(BurConstant.LCSPRODUCT,
				BurConstant.BRANCHID), new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.PRODUCTAREVID));
		// Append Criteria
		prodPrimSourceStatement.appendAndIfNeeded();
		prodPrimSourceStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSourcingConfig.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		prodPrimSourceStatement.appendAnd();
		prodPrimSourceStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSourcingConfig.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		// Append Table
		prodPrimSourceStatement.appendFromTable(LCSSourcingConfigMaster.class);
		// Append Join
		prodPrimSourceStatement.appendJoin(new QueryColumn(LCSSourcingConfig.class,
				BurConstant.MASTERREFERENCE_KEY_ID), new QueryColumn(
				LCSSourcingConfigMaster.class, BurConstant.OBJECTIDENTIFIERID));

		// Append Table
		prodPrimSourceStatement.appendFromTable(LCSSourceToSeasonLinkMaster.class);
		// Append Join
		prodPrimSourceStatement.appendJoin(new QueryColumn(
				LCSSourceToSeasonLinkMaster.class.getSimpleName(),
				BurProductBOMConstant.SOURCINGCONFIGMASTER_KEY_ID),
				new QueryColumn(LCSSourcingConfigMaster.class,
						BurConstant.OBJECTIDENTIFIERID));
		// Append Criteria
		prodPrimSourceStatement.appendAnd();
		prodPrimSourceStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSourceToSeasonLinkMaster.class,
				BurProductBOMConstant.PRIMARY_STSL), STR_VAL_ONE,
				Criteria.EQUALS));
		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(prodPrimSourceStatement,
				colCriteria);

		prodPrimSourceStatement.appendFromTable(FlexBOMPart.class);
		BurberryProductBOMAPIDBHelper.appendAdditionalBOMCriteriaByTableName(
				prodPrimSourceStatement, FlexBOMPart.class,false);
		logger.info(methodName + "Prepared Query Statement "
				+ prodPrimSourceStatement.toString());
		// Exceute the query
		resultProductSource = LCSQuery.runDirectQuery(prodPrimSourceStatement)
				.getResults();

		long lEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				" source endTime: ");
		logger.info(methodName + "Soruce Total Execution Time (ms): "
				+ (lEndTime - lStartTime));
		logger.debug(methodName + "resultProductSource:" + resultProductSource);
		return (Collection<Map>) resultProductSource;
	}

	/**
	 * Method to get Product and BOM using criteria map.
	 * 
	 * @param colProductBomCriteria
	 *            Product BOM Criteria
	 * @return results
	 * @throws WTException
	 *             throw exception
	 */
	public static Collection<Map> getAssociatedProductFromBom(
			Collection<Map> colProductBomCriteria) throws WTException {

		String methodName = "getAssociatedProductFromBom() ";
		long lStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"BOM startTime: ");

		Collection<Map> resultProductBom = new ArrayList<Map>();
		logger.debug(methodName + "colProductBOMCriteria: "
				+ colProductBomCriteria);
		if (colProductBomCriteria.isEmpty()) {
			return resultProductBom;
		}

		// Create a new prepared query statement
		PreparedQueryStatement bomStatement = new PreparedQueryStatement();
		// Append select column
		bomStatement.appendSelectColumn(new QueryColumn(FlexBOMPart.class,
				BurConstant.OBJECTIDENTIFIERID));
		BurberryProductBOMAPIDBHelper.appendAdditionalBOMCriteriaByTableName(
				bomStatement, FlexBOMPart.class,false);
		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(bomStatement,
				colProductBomCriteria);

		logger.info(methodName + "Prepared Query Statement : "
				+ bomStatement.toString());
		// Exceute the query
		resultProductBom = LCSQuery.runDirectQuery(bomStatement).getResults();

		long lEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"BOM end time ");
		logger.info(methodName + "BOM Total Execution Time (ms): "
				+ (lEndTime - lStartTime));
		logger.debug(methodName + "resultProductBOM:" + resultProductBom);
		return (Collection<Map>) resultProductBom;
	}

	/**
	 * Method to get Product and BOM by BOMLink using criteria map.
	 * 
	 * @param colProductBomLinkCriteria
	 *            Product BOM Link Criteria
	 * @return results
	 * @throws WTException
	 *             throw exception
	 */
	public static Collection<Map> getAssociatedProductFromBomLink(
			Collection<Map> colProductBomLinkCriteria) throws WTException {

		String methodName = "getAssociatedProductFromBom(Link) ";
		long lStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"BOM Link Start time ");

		Collection<Map> resultProductBomLink = new ArrayList<Map>();
		logger.debug(methodName + "colProductBOMLinkCriteria: "
				+ colProductBomLinkCriteria);
		if (colProductBomLinkCriteria.isEmpty()) {
			return resultProductBomLink;
		}

		// Create a new prepared query statement
		PreparedQueryStatement bomLinkStatement = new PreparedQueryStatement();

		// Append select column
		bomLinkStatement.appendSelectColumn(new QueryColumn(FlexBOMLink.class,
				BurConstant.OBJECTIDENTIFIERID));

		// Append select column
		bomLinkStatement.appendSelectColumn(new QueryColumn(FlexBOMPart.class,
				BurConstant.OBJECTIDENTIFIERID));
		BurberryProductBOMAPIDBHelper.appendAdditionalBOMCriteriaByTableName(
				bomLinkStatement, FlexBOMLink.class,false);

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(bomLinkStatement,
				colProductBomLinkCriteria);
		
		// Append Criteria
		bomLinkStatement.appendAnd();
		bomLinkStatement.appendCriteria(new Criteria(new QueryColumn(
							FlexBOMLink.class, BurProductBOMConstant.BOMLINK_OUTDATE),
							"", Criteria.IS_NULL));

		logger.info(methodName + "Prepared Query Statement : "
				+ bomLinkStatement.toString());
		// Exceute the query
		resultProductBomLink = LCSQuery.runDirectQuery(bomLinkStatement)
				.getResults();

		long lEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Bom link end time : ");
		logger.info(methodName + "Bom Link Total Execution Time (ms): "
				+ (lEndTime - lStartTime));
		logger.debug(methodName + "resultProductBOMLink:" + resultProductBomLink);
		return (Collection<Map>) resultProductBomLink;
	}

	/**
	 * Method to get Product and BOM by BOM MaterialMaster using criteria map.
	 * 
	 * @param colProductBomMaterialCriteria
	 *            Product BOM Criteria
	 * @return results
	 * @throws WTException
	 *             throw exception
	 */
	public static Collection<Map> getAssociatedProductFromBomMaterial(
			Collection<Map> colProductBomMaterialCriteria) throws WTException {

		String methodName = "getAssociatedProductFromBomMaterial() ";
		long lStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Bom Material start time : ");

		Collection<Map> resultProductBomMaterial = new ArrayList<Map>();
		logger.debug(methodName + "colProductBOMMaterialCriteria: "
				+ colProductBomMaterialCriteria);
		if (colProductBomMaterialCriteria.isEmpty()) {
			return resultProductBomMaterial;
		}

		// Create a new prepared query statement
		PreparedQueryStatement bomMaterialStatement = new PreparedQueryStatement();
		// Append select column
		bomMaterialStatement.appendSelectColumn(new QueryColumn(
				FlexBOMLink.class, BurConstant.OBJECTIDENTIFIERID));

		// Append select column
		bomMaterialStatement.appendSelectColumn(new QueryColumn(
				FlexBOMPart.class, BurConstant.OBJECTIDENTIFIERID));

		BurberryProductBOMAPIDBHelper.appendAdditionalBOMCriteriaByTableName(
				bomMaterialStatement, LCSMaterial.class,false);

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				bomMaterialStatement, colProductBomMaterialCriteria);

		logger.info(methodName + "Prepared Query Statement : "
				+ bomMaterialStatement.toString());
		// Exceute the query
		resultProductBomMaterial = LCSQuery
				.runDirectQuery(bomMaterialStatement).getResults();

		long lEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Bom Material end time : ");
		logger.info(methodName + "Bom Material Total Execution Time (ms): "
				+ (lEndTime - lStartTime));
		logger.debug(methodName + "resultProductBOMMaterial:"
				+ resultProductBomMaterial);
		return (Collection<Map>) resultProductBomMaterial;
	}

}
