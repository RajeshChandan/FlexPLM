package com.burberry.wc.integration.productapi.db;

import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.productapi.constant.BurProductConstant;
import com.burberry.wc.integration.productbomapi.db.BurberryProductBOMAPIDBHelper;
import com.burberry.wc.integration.util.BurConstant;
import com.burberry.wc.integration.util.BurberryAPIDBUtil;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
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
public final class BurberryProductAPIDBHelper {

	/**
	 * Default Constructor.
	 */
	private BurberryProductAPIDBHelper() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductAPIDBHelper.class);

	/**
	 * STR_VAL_A.
	 */
	private static final String STR_VAL_A = "A";

	/**
	 * STR_EQUAL.
	 */
	private static final String STR_EQUAL = "=";

	/**
	 * STR_QUESTION.
	 */
	private static final String STR_QUESTION = "?";

	/**
	 * STR_VAL_ONE.
	 */
	private static final String STR_VAL_ONE = "1";

	/**
	 * STR_VAL_ZERO.
	 */
	private static final String STR_VAL_ZERO = "0";

	/**
	 * Method for Product API.
	 * 
	 * @param colCriteria
	 *            Collection of Criteria Map
	 * @return results Collection of Products
	 * @throws WTException
	 *             throw WTException
	 */
	// JIRA - BURBERRY-1363: START
	public static Collection<FlexObject> getProductAPIDataFromDB(
			List<String> colCriteria) throws WTException {

		String methodName = "getProductAPIDataFromDB() ";
		// Method Start Time
		long lProdAPIStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product API Query Start Time: ");

		// Initialisation of collection
		Collection<FlexObject> productObjResults = new ArrayList<FlexObject>();
		logger.debug(methodName + "colCriteria: " + colCriteria);

		// Check if the criteria collection is not empty
		if (colCriteria.isEmpty()) {
			return productObjResults;
		}

		// Create new prepared query statement
		PreparedQueryStatement pqStatement = new PreparedQueryStatement();
		// Append Distinct
		pqStatement.setDistinct(true);

		// Append table column
		pqStatement.appendFromTable(LCSProduct.class);
		// Append select column
		pqStatement.appendSelectColumn(new QueryColumn(LCSProduct.class,
				BurConstant.LCS_PRODUCT_OBJECTIDENTIFIERID));
		pqStatement.appendSelectColumn(new QueryColumn(BurConstant.LCSPRODUCT,
				BurConstant.BRANCHID));

		// Append search criteria
		pqStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		pqStatement.appendAnd();
		pqStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		pqStatement.appendAnd();
		pqStatement.appendOpenParen();

		// Append additional search criteria
		appendCriteriaToStatement(pqStatement, colCriteria);

		pqStatement.appendClosedParen();

		logger.info(methodName + "Product API Query Prepared Query Statement: "
				+ pqStatement.toString());
		productObjResults = LCSQuery.runDirectQuery(pqStatement).getResults();

		long lProdAPIEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product API Query End Time: ");
		logger.info(methodName
				+ "Product API Query Total Execution Time (ms): "
				+ (lProdAPIEndTime - lProdAPIStartTime));
		logger.debug(methodName + " result1 " + productObjResults);

		return productObjResults;
	}
	//JIRA - BURBERRY-1363: END

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
	//JIRA - BURBERRY-1363: START
	private static void appendCriteriaToStatement(
			PreparedQueryStatement statement, List<String> colCriteria)
			throws WTException {

		String methodName = "appendCriteriaToStatement() ";
		int iCounter = 0;
		// Loop through the complete collection of map criteria
		for (String strBranchId : colCriteria) {
			logger.debug(methodName + " mapCriteria: " + strBranchId);
			String strTableName = "LCSPRODUCT";
			String strColumnName = "BRANCHIDITERATIONINFO";
			logger.debug(methodName + "Counter: " + iCounter);
			if (iCounter > 0) {
				statement.appendOrIfNeeded();
			}
			statement.appendCriteria(new Criteria(new QueryColumn(strTableName,
					strColumnName), STR_QUESTION, STR_EQUAL), strBranchId);
			iCounter++;
		}
	}
	//JIRA - BURBERRY-1363: END
	
	/**
	 * Method to get Product Ids by checking the creation /updates on Product,
	 * Colourway, Product-Season. Colourway Season, Material, Sourcing Config
	 * Objects/tables.
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
	public static Collection<Map> getProductDeltaFromDateRange(Date startdate,
			Date enddate, boolean modifyDate) throws WTException {

		// long lStartTime = System.currentTimeMillis();
		String methodName = "getProductDeltaFromDateRange() ";
		logger.debug(methodName + "startDate5: " + startdate);
		logger.debug(methodName + "enddate5: " + enddate);
		logger.debug(methodName + "modifyDate5: " + modifyDate);

		HashSet<Map> hsProductIds = new HashSet<Map>();

		// Product Delta
		Collection<Map> colProductDelta = getProductIdsFromDateRange(
				LCSProduct.class, startdate, enddate, modifyDate, false);
		logger.debug(methodName + "Collection of Product Delta: "
				+ colProductDelta);
		hsProductIds.addAll(colProductDelta);

		// Colourway Delta
		Collection<Map> colColourwayDelta = getProductIdsFromDateRange(
				LCSSKU.class, startdate, enddate, modifyDate, false);
		logger.debug(methodName + "Collection of Colourway Delta: "
				+ colColourwayDelta);
		hsProductIds.addAll(colColourwayDelta);

		// Product-Season Delta
		Collection<Map> colProductSeasonDelta = getProductIdsFromDateRange(
				LCSProductSeasonLink.class, startdate, enddate, modifyDate,
				false);
		logger.debug(methodName + "Collection of Product Season Delta: "
				+ colProductSeasonDelta);
		hsProductIds.addAll(colProductSeasonDelta);

		// Colourway-Season Delta
		Collection<Map> colColourwaySeasonDelta = getProductIdsFromDateRange(
				LCSSKUSeasonLink.class, startdate, enddate, modifyDate, false);
		logger.debug(methodName + "Collection of Colourway Season Delta: "
				+ colColourwaySeasonDelta);
		hsProductIds.addAll(colColourwaySeasonDelta);

		// Material Delta
		// Collection<Map> colMaterialDelta = new ArrayList<Map>();
		Collection<Map> colMaterialDelta = getMaterialDeltaFromDateRange(
				startdate, enddate, modifyDate);
		logger.debug(methodName + "Collection Material Delta: "
				+ colMaterialDelta);
		hsProductIds.addAll(colMaterialDelta);

		// Souring Config Delta
		Collection<Map> colSourcingConfigDelta = getSourcingConfigDeltaFromDateRange(
				startdate, enddate, modifyDate);
		logger.debug(methodName + "Collection Sourcing Config Delta: "
				+ colSourcingConfigDelta);
		hsProductIds.addAll(colSourcingConfigDelta);

		// CR R26: Handle Remove Image Page from Specification Customisation :
		// Start
		// Removed Image Page from Specification Customisation
		Collection<Map> colRemovedImagePageDelta = BurberryAPIDBUtil
				.getRemovedMOADeltaFromDateRange(
						LCSProduct.class,
						BurProductConstant.BO_TRACK_SPECIFICATION_IMAGE_PAGE_NAME,
						BurProductConstant.MOA_TRACK_PRODUCT_ID, startdate,
						enddate);
		logger.debug(methodName + "Collection Removed Image Page Delta: "
				+ colRemovedImagePageDelta);
		hsProductIds.addAll(colRemovedImagePageDelta);

		// Removed Images from Image Page Customisation
		Collection<Map> colRemovedImagesFromImagePageDelta = BurberryAPIDBUtil
				.getRemovedMOADeltaFromDateRange(LCSProduct.class,
						BurProductConstant.BO_TRACK_IMAGE_FROM_IMAGE_PAGE_NAME,
						BurProductConstant.MOA_TRACK_PRODUCT_ID, startdate,
						enddate);
		logger.debug(methodName
				+ "Collection Removed Image From Image Page Delta: "
				+ colRemovedImagesFromImagePageDelta);
		hsProductIds.addAll(colRemovedImagesFromImagePageDelta);

		// CR R26: Handle Remove Image Page from Specification Customisation :
		// End

		// CR R26: Handle Remove Risk Management MOA Customisation :
		// Start
		// Removed Risk Management MOA Delta Query
		Collection<Map> colRemovedRiskManagementDeltaData = BurberryAPIDBUtil
				.getRemovedMOADeltaFromDateRange(
						LCSProduct.class,
						BurPaletteMaterialConstant.BO_TRACK_RISK_MANAGEMENT_NAME,
						BurPaletteMaterialConstant.MOA_TRACK_RISK_MANAGEMENT_OWNER_ID,
						startdate, enddate);
		logger.info(methodName + "Collection Removed Risk Management Delta: "
				+ colRemovedRiskManagementDeltaData);
		hsProductIds.addAll(colRemovedRiskManagementDeltaData);
		// CR R26: Handle Remove Risk Management MOA Customisation :
		// End

		// CR R26 - Handle Removed Specification From Product Customisation -
		// Start
		Collection<Map> colRemovedSpecDeltaData = BurberryAPIDBUtil
				.getRemovedMOADeltaFromDateRange(LCSProduct.class,
						BurProductConstant.BO_TRACK_SPECIFICATION_NAME,
						BurProductConstant.MOA_TRACK_PRODUCT_ID, startdate,
						enddate);
		
		logger.info(methodName + "Collection Removed Specification Delta: "
				+ colRemovedSpecDeltaData);
		hsProductIds.addAll(colRemovedSpecDeltaData);
		// CR R26 -Handle Removed Specification From Product Customisation - End

		
		Collection<Map> colRemovedSpecDocDeltaData = BurberryAPIDBUtil
				.getRemovedMOADeltaFromDateRange(LCSProduct.class,
						BurProductConstant.BO_TRACK_SPECIFICATION_DOCUMENT_NAME,
						BurProductConstant.MOA_TRACK_PRODUCT_ID, startdate,
						enddate);
		logger.info(methodName + "Collection Removed Specification Document Delta: "
				+ colRemovedSpecDocDeltaData);
		hsProductIds.addAll(colRemovedSpecDocDeltaData);
		logger.debug(methodName + "hsProductIds: " + hsProductIds);
		Collection<Map> criteriaMap = new ArrayList<Map>(hsProductIds);
		logger.debug(methodName + "Delta Criteria Map: " + criteriaMap);
		return criteriaMap;
	}

	/**
	 * Method to get Product Ids based on time range.
	 * 
	 * @param tableName
	 *            table name
	 * @param startdate
	 *            start date
	 * @param enddate
	 *            end date
	 * @param modifyDate
	 *            modify
	 * @param bomApi
	 * @return collection
	 * @throws WTException
	 *             exception
	 */
	public static Collection<Map> getProductIdsFromDateRange(Class tableName,
			Date startdate, Date enddate, boolean modifyDate, boolean bomApi)
			throws WTException {

		String methodName = "getProductIdsFromDateRange() ";

		long lProdDeltaStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product Delta Query Start Time: ");

		// Create a prepared query statement
		PreparedQueryStatement prodStatement = new PreparedQueryStatement();
		// Append distinct
		prodStatement.setDistinct(true);
		// Append tables
		prodStatement.appendFromTable(LCSProduct.class);

		// Append Select column
		prodStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));

		prodStatement.appendOpenParen();

		// Check if create / modify
		if (!modifyDate) {
			// get create statement
			prodStatement = getCreateStampCriteriaStatement(prodStatement,
					tableName, startdate, enddate);
		} else {
			// get modified statement
			prodStatement = getModifyStampCriteriaStatement(prodStatement,
					tableName, startdate, enddate);
		}
		prodStatement.appendClosedParen();

		// Append additional criteria for latest iteration info,
		// effect latest, version info based on table name
		appendAdditionalCriteriaBasedOnTableName(prodStatement, tableName,
				modifyDate);

		if (bomApi) {
			prodStatement.appendFromTable(FlexBOMPart.class);
			BurberryProductBOMAPIDBHelper
					.appendAdditionalBOMCriteriaByTableName(prodStatement,
							FlexBOMPart.class,false);
		}

		logger.info(methodName + "Product Delta Prepared Query Statement: "
				+ prodStatement.toString());
		// Exceute query
		Collection<?> result = LCSQuery.runDirectQuery(prodStatement)
				.getResults();

		long lProdDeltaEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Product Delta Query End Time: ");

		logger.info(methodName + "Product Delta Total Execution Time (ms): "
				+ (lProdDeltaEndTime - lProdDeltaStartTime));
		logger.debug(methodName + " Product Delta Results: " + result);
		return (Collection<Map>) result;
	}

	/**
	 * Method to append basic additional criteria based on table.
	 * 
	 * @param prodStatement
	 *            statement
	 * @param modifyDate
	 *            modify Date
	 * @param tableName
	 *            .getName() table name
	 * @throws WTException
	 *             exception
	 */
	private static void appendAdditionalCriteriaBasedOnTableName(
			PreparedQueryStatement prodStatement, Class tableName,
			boolean modifyDate) throws WTException {

		String methodName = "appendAdditionalCriteriaBasedOnTableName() ";

		logger.debug(methodName + " Append Table: " + tableName.getSimpleName());

		// Commented to resolve Too Many Prepared Query Statement Placeholders
		// (?) in Oracle
		// Append criteria for Product
		// if ("LCSPRODUCT".equalsIgnoreCase(tableName.getSimpleName())) {

		// Append latest iteration info
		prodStatement.appendAnd();
		prodStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		// Append criteria for version info
		prodStatement.appendAnd();
		prodStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));
		// }
		// Append criteria for SKU
		if ("LCSSKU".equalsIgnoreCase(tableName.getSimpleName())) {
			// Append SKU Table
			prodStatement.appendFromTable(tableName);

			// Append Join between Product and SKU
			prodStatement.appendJoin(BurConstant.LCSPRODUCT,
					BurConstant.BRANCHID, BurConstant.LCSSKU,
					BurConstant.PRODUCTAREVID);

			// Append Criteria for latest iteration info
			prodStatement.appendAnd();
			prodStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSSKU.class, BurConstant.LATESTITERATIONINFO),
					STR_VAL_ONE, Criteria.EQUALS));

			// Append criteria for version info
			prodStatement.appendAnd();
			prodStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSSKU.class, BurConstant.VERSIONIDA2VERSIONINFO),
					STR_VAL_A, Criteria.EQUALS));

		}
		// Append criteria for Product-Season
		else if ("LCSPRODUCTSEASONLINK".equalsIgnoreCase(tableName
				.getSimpleName())) {
			// Append LCSProductSeasonLink table
			prodStatement.appendFromTable(LCSProductSeasonLink.class);

			// Append Join between Product and ProductSeasonlink
			prodStatement.appendJoin(BurConstant.LCSPRODUCT,
					BurConstant.IDA3MASTERREFEREN_CE,
					BurConstant.LCSPRODUCTSEASONLINK,
					BurConstant.PRODUCTMASTERID);

			prodStatement.appendAnd();

			// Append Effect Seq or Effect Latest based on the Create/Modify
			appendEffectSeqEffectLatest(prodStatement,
					LCSProductSeasonLink.class, modifyDate);

		}
		// Append criteria for SKU-Season
		else if ("LCSSKUSEASONLINK".equalsIgnoreCase(tableName.getSimpleName())) {
			// Append SKU and LCSSKUSeasonLink Table
			prodStatement.appendFromTable(LCSSKU.class);
			prodStatement.appendFromTable(LCSSKUSeasonLink.class);

			// Append join between LCSSKU and LCSSKUSEASONLINK tables
			prodStatement.appendJoin(BurConstant.LCSSKU,
					BurConstant.IDA3MASTERREFEREN_CE,
					BurConstant.LCSSKUSEASONLINK, BurConstant.SKUMASTERID);
			// Append join between LCSPRODUCT and LCSSKUSEASONLINK tables
			prodStatement.appendJoin(BurConstant.LCSPRODUCT,
					BurConstant.IDA3MASTERREFEREN_CE,
					BurConstant.LCSSKUSEASONLINK, BurConstant.PRODUCTMASTERID);

			prodStatement.appendAnd();

			// Append Effect Seq or Effect Latest based on the Create/Modify
			appendEffectSeqEffectLatest(prodStatement, LCSSKUSeasonLink.class,
					modifyDate);

			// Append Criteria for latest iteration info
			prodStatement.appendAnd();
			prodStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSSKU.class, BurConstant.LATESTITERATIONINFO),
					STR_VAL_ONE, Criteria.EQUALS));

			// Append criteria for version info
			prodStatement.appendAnd();
			prodStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSSKU.class, BurConstant.VERSIONIDA2VERSIONINFO),
					STR_VAL_A, Criteria.EQUALS));

		}
	}

	/**
	 * Method to append effect seq or effect latest based on create/modify.
	 * 
	 * @param prodStatement
	 *            Statement
	 * @param tableName
	 *            table name
	 * @param modifyDate
	 *            boolean
	 * @throws WTException
	 *             exception
	 */
	private static void appendEffectSeqEffectLatest(
			PreparedQueryStatement prodStatement, Class tableName,
			boolean modifyDate) throws WTException {

		String methodName = "appendEffectSeqEffectLatest() ";

		logger.debug(methodName + "Table: " + tableName.getSimpleName());
		logger.debug(methodName + "Modify: " + modifyDate);

		if (modifyDate) {
			// Append Criteria for effect latest
			prodStatement.appendCriteria(new Criteria(new QueryColumn(
					tableName, BurConstant.EFFECTLATEST), STR_VAL_ONE,
					Criteria.EQUALS));
		} else {
			// Append Criteria for effect latest
			prodStatement.appendCriteria(new Criteria(new QueryColumn(
					tableName, BurConstant.EFFECTSEQ), STR_VAL_ZERO,
					Criteria.EQUALS));
		}
	}

	/**
	 * Method to append create stamp criteria.
	 * 
	 * @param statement
	 *            statement
	 * @param table
	 *            table name
	 * @param startdate
	 *            start date
	 * @param enddate
	 *            end date
	 * @return pqs
	 * @throws WTException
	 *             exception
	 */
	private static PreparedQueryStatement getCreateStampCriteriaStatement(
			PreparedQueryStatement statement, Class table, Date startdate,
			Date enddate) throws WTException {

		String methodName = "getCreateStampCriteriaStatement() ";

		logger.debug(methodName + "Start Date8: " + startdate);
		logger.debug(methodName + "End Date8: " + enddate);

		statement.appendOpenParen();
		statement.appendCriteria(new Criteria(new QueryColumn(table,
				BurConstant.CREATE_STAMP), startdate,
				Criteria.GREATER_THAN_EQUAL));
		statement.appendAnd();
		statement.appendCriteria(new Criteria(new QueryColumn(table,
				BurConstant.CREATE_STAMP), enddate, Criteria.LESS_THAN_EQUAL));
		statement.appendClosedParen();

		return statement;

	}

	/**
	 * Method to append modify stamp criteria.
	 * 
	 * @param pqs
	 *            pqs
	 * @param tableName
	 *            table name
	 * @param startdate
	 *            start date
	 * @param enddate
	 *            end date
	 * @return pqs
	 * @throws WTException
	 *             exception
	 */
	private static PreparedQueryStatement getModifyStampCriteriaStatement(
			PreparedQueryStatement pqs, Class tableName, Date startdate,
			Date enddate) throws WTException {

		String methodName = "getModifyStampCriteriaStatement() ";

		logger.debug(methodName + "Start Date13: " + startdate);
		logger.debug(methodName + "End Date13: " + enddate);

		pqs.appendOpenParen();
		pqs.appendCriteria(new Criteria(new QueryColumn(tableName,
				BurConstant.MODIFY_STAMP), startdate,
				Criteria.GREATER_THAN_EQUAL));
		pqs.appendAnd();
		pqs.appendCriteria(new Criteria(new QueryColumn(tableName,
				BurConstant.MODIFY_STAMP), enddate, Criteria.LESS_THAN_EQUAL));
		pqs.appendClosedParen();

		return pqs;

	}

	/**
	 * Method to get Product Ids for updates on Material using start create. and
	 * end create or start modify and end modify dates.
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
	private static Collection<Map> getMaterialDeltaFromDateRange(
			Date startdate, Date enddate, boolean modifyDate)
			throws WTException {

		String methodName = "getMaterialDeltaFromDateRange() ";
		long lMaterialDeltaStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Material Delta Query Start Time: ");

		logger.debug(methodName + "Start Date3: " + startdate);
		logger.debug(methodName + "End Date3: " + enddate);
		logger.debug(methodName + "Modify3: " + modifyDate);

		final PreparedQueryStatement materialDeltaStatement = new PreparedQueryStatement();
		FlexType productType = FlexTypeCache
				.getFlexTypeRootByClass((LCSProduct.class).getName());
		FlexTypeAttribute fta = productType
				.getAttribute(BurConstant.BUR_RM_MAIN);

		materialDeltaStatement.setDistinct(true);
		materialDeltaStatement.appendFromTable(LCSProduct.class);

		// statement.appendSelectColumn(new
		// QueryColumn(LCSProduct.class,BurConstant.LCS_PRODUCT_OBJECTIDENTIFIERID));
		materialDeltaStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		materialDeltaStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		materialDeltaStatement.appendAnd();
		materialDeltaStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		materialDeltaStatement.appendFromTable(LCSMaterial.class);

		// Join Between Product and Material
		materialDeltaStatement.appendJoin(BurConstant.LCSPRODUCT,
				fta.getColumnName(), BurConstant.LCSMATERIAL,
				BurConstant.BRANCHID);

		materialDeltaStatement.appendAnd();
		materialDeltaStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		materialDeltaStatement.appendAnd();
		materialDeltaStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));
		materialDeltaStatement.appendAndIfNeeded();
		materialDeltaStatement.appendCriteria(new Criteria(
				new QueryColumn(BurConstant.LCSMATERIAL,
						BurConstant.MATERIAL_STATECHECKOUTINFO), "wrk",
				Criteria.NOT_EQUAL_TO));

		materialDeltaStatement.appendAnd();

		if (!modifyDate) {
			/*
			 * checking if material was created between given dates
			 */
			materialDeltaStatement.appendOpenParen();
			materialDeltaStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterial.class, BurConstant.CREATE_STAMP), startdate,
					Criteria.GREATER_THAN_EQUAL));
			materialDeltaStatement.appendAnd();
			materialDeltaStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterial.class, BurConstant.CREATE_STAMP), enddate,
					Criteria.LESS_THAN_EQUAL));
			materialDeltaStatement.appendClosedParen();
		} else {
			/*
			 * checking if material was updated between given dates
			 */
			materialDeltaStatement.appendOpenParen();
			materialDeltaStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterial.class, BurConstant.MODIFY_STAMP), startdate,
					Criteria.GREATER_THAN_EQUAL));
			materialDeltaStatement.appendAnd();
			materialDeltaStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterial.class, BurConstant.MODIFY_STAMP), enddate,
					Criteria.LESS_THAN_EQUAL));
			materialDeltaStatement.appendClosedParen();
		}

		logger.info(methodName + "Material Delta Prepared Query Statement: "
				+ materialDeltaStatement.toString());
		Collection<?> resultMaterialDelta = LCSQuery.runDirectQuery(
				materialDeltaStatement).getResults();

		long lMaterialDeltaEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Material Delta Query End Time: ");

		logger.info(methodName + "Material Delta  Total Execution Time (ms): "
				+ (lMaterialDeltaEndTime - lMaterialDeltaStartTime));
		logger.debug(methodName + " resultMaterialDelta " + resultMaterialDelta);
		return (Collection<Map>) resultMaterialDelta;

	}

	/**
	 * Method to get Product Ids for updates on Sourcing Config using start
	 * create and end create or start modify and end modify dates.
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
	private static Collection<Map> getSourcingConfigDeltaFromDateRange(
			Date startdate, Date enddate, boolean modifyDate)
			throws WTException {

		String methodName = "getSourcingConfigDeltaFromDateRange() ";
		long lSourcingConfigDeltaStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Sourcing Config Delta Query Start Time: ");

		logger.debug(methodName + "Start Date4: " + startdate);
		logger.debug(methodName + "End Date4: " + enddate);
		logger.debug(methodName + "Modify4: " + modifyDate);

		final PreparedQueryStatement sourcingConfigStatement = new PreparedQueryStatement();

		sourcingConfigStatement.setDistinct(true);
		sourcingConfigStatement.appendFromTable(LCSProduct.class);

		// statement.appendSelectColumn(new
		// QueryColumn(LCSProduct.class,BurConstant.LCS_PRODUCT_OBJECTIDENTIFIERID));
		sourcingConfigStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID));
		sourcingConfigStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		sourcingConfigStatement.appendAnd();
		sourcingConfigStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		sourcingConfigStatement.appendFromTable(LCSSourcingConfig.class);
		sourcingConfigStatement.appendJoin(new QueryColumn(
				BurConstant.LCSPRODUCT, BurConstant.BRANCHID), new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.PRODUCTAREVID));
		sourcingConfigStatement.appendAndIfNeeded();
		sourcingConfigStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSourcingConfig.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		sourcingConfigStatement.appendAnd();
		sourcingConfigStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSourcingConfig.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		sourcingConfigStatement.appendAnd();

		if (!modifyDate) {
			/*
			 * checking if source was created between given dates
			 */
			sourcingConfigStatement.appendOpenParen();
			sourcingConfigStatement.appendCriteria(new Criteria(
					new QueryColumn(LCSSourcingConfig.class,
							BurConstant.CREATE_STAMP), startdate,
					Criteria.GREATER_THAN_EQUAL));
			sourcingConfigStatement.appendAnd();
			sourcingConfigStatement.appendCriteria(new Criteria(
					new QueryColumn(LCSSourcingConfig.class,
							BurConstant.CREATE_STAMP), enddate,
					Criteria.LESS_THAN_EQUAL));
			sourcingConfigStatement.appendClosedParen();
		} else {
			/*
			 * checking if source was updated between given dates
			 */
			sourcingConfigStatement.appendOpenParen();
			sourcingConfigStatement.appendCriteria(new Criteria(
					new QueryColumn(LCSSourcingConfig.class,
							BurConstant.MODIFY_STAMP), startdate,
					Criteria.GREATER_THAN_EQUAL));
			sourcingConfigStatement.appendAnd();
			sourcingConfigStatement.appendCriteria(new Criteria(
					new QueryColumn(LCSSourcingConfig.class,
							BurConstant.MODIFY_STAMP), enddate,
					Criteria.LESS_THAN_EQUAL));
			sourcingConfigStatement.appendClosedParen();
		}
		logger.info(methodName
				+ "Sourcing Config Delta Prepared Query Statement: "
				+ sourcingConfigStatement.toString());

		Collection<?> resultSourcingConfigDelta = LCSQuery.runDirectQuery(
				sourcingConfigStatement).getResults();

		long lSourcingConfigDeltaEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Sourcing Config Delta Query End Time: ");

		logger.info(methodName
				+ "Sourcing Config Delta Total Execution Time (ms): "
				+ (lSourcingConfigDeltaEndTime - lSourcingConfigDeltaStartTime));
		logger.debug(methodName + " resultSourcingConfigDelta "
				+ resultSourcingConfigDelta);
		return (Collection<Map>) resultSourcingConfigDelta;
	}

	/**
	 * Method to append Colourway criteria.
	 * 
	 * @param statement
	 *            statement
	 * @param criteriaCol
	 *            collection
	 */
	public static void appendProductSKUCriteriaToStatement(
			PreparedQueryStatement statement, Collection<Map> criteriaCol) {

		String methodName = "appendProductSKUCriteriaToStatement() ";
		int counter = 0;

		statement.appendAnd();
		statement.appendOpenParen();

		// Loop through the complete collection of map criteria
		for (Map<String, Object> mapProductSKUCriteria : criteriaCol) {
			logger.debug(methodName + "mapProductSKUCriteria: "
					+ mapProductSKUCriteria);

			if (counter > 0) {
				statement.appendOrIfNeeded();
			}
			// Loop through each map and get key and value
			for (Map.Entry<String, Object> mapProductSKUEntry : mapProductSKUCriteria
					.entrySet()) {
				logger.debug(methodName + "mapEntry: " + mapProductSKUEntry);
				String mapKey = mapProductSKUEntry.getKey();
				logger.debug(methodName + "mapKey: " + mapKey);
				String[] strTemp = mapKey.split("\\.");
				String strTableName = strTemp[0];
				logger.debug(methodName + "DB Table Name: " + strTableName);
				String strColumnName = strTemp[1];
				logger.debug(methodName + "DB Column Name: " + strColumnName);
				String strOperator = strTemp[2];
				logger.debug(methodName + "DB Operator: " + strOperator);

				// Additional Check if table name is LCSPRODUCT
				if (BurConstant.LCSPRODUCT.equalsIgnoreCase(strTableName)) {
					statement.appendOpenParen();
					String[] strColumnTemp = strColumnName.split(":");
					logger.debug(methodName + "Temp object: "
							+ Arrays.toString(strColumnTemp));
					String strProductColumnName = strColumnTemp[0];
					logger.debug(methodName + "ProductColumnName: "
							+ strProductColumnName);
					String strSKUColumnName = strColumnTemp[1];
					logger.debug(methodName + "SKUColumnName: "
							+ strSKUColumnName);

					// Append criteria
					BurberryAPIDBUtil.appendObjectCriteriaStatement(statement,
							strTableName, strProductColumnName, strOperator,
							mapProductSKUEntry);
					statement.appendAnd();
					statement.appendCriteria(new Criteria(new QueryColumn(
							BurConstant.LCSSKU, strSKUColumnName),
							STR_QUESTION, Criteria.IS_NULL));
					statement.appendClosedParen();
				} else {
					// Append criteria based on the object Date / String /
					// Boolean / Number
					BurberryAPIDBUtil.appendObjectCriteriaStatement(statement,
							strTableName, strColumnName, strOperator,
							mapProductSKUEntry);
				}

			}
			counter++;
		}
		statement.appendClosedParen();
		// TODO Auto-generated method stub
	}

}
