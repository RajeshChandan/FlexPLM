package com.burberry.wc.integration.productbomapi.db;

import java.text.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.productapi.constant.BurProductConstant;
import com.burberry.wc.integration.productapi.db.BurberryProductAPIDBHelper;
import com.burberry.wc.integration.productbomapi.constant.BurProductBOMConstant;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.sourcing.LCSSourceToSeasonLinkMaster;
import com.lcs.wc.specification.*;

/**
 * A DB helper class to handle Database activity. Class contain several method
 * to handle DB activity i.e. creating DB query, statement, and result set
 * allocation. Collection of Criteria Map, Start Date, End Date and Modify are
 * Primary input to fetch details from Flex system.
 *
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryProductBOMAPIDBHelper {

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductBOMAPIDBHelper.class);

	/**
	 * STR_VAL_A.
	 */
	private static final String STR_VAL_A = "A";

	/**
	 * STR_VAL_ONE.
	 */
	private static final String STR_VAL_ONE = "1";


	/**
	/**
	 * STR_COMPONENT_TYPE.
	 */
	private static final String STR_COMPONENT_BOM_TYPE = "BOM";

	/**
	 * Private Constructor. BurberryProductBomApiDBHelper
	 */
	private BurberryProductBOMAPIDBHelper() {

	}

	/**
	 * Method to get Product Ids by checking the creation /updates on Product,
	 * Colourway, Product-Season. Colourway Season, Material, Sourcing Config,
	 * BOM, BOM Link, BOM Material. Objects/tables.
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

		boolean bomApi = true;

		// Product Delta
		Collection<Map> colProductDelta = BurberryProductAPIDBHelper
				.getProductIdsFromDateRange(LCSProduct.class, startdate,
						enddate, modifyDate, bomApi);
		logger.debug(methodName + "Collection of Product Delta: "
				+ colProductDelta);
		hsProductIds.addAll(colProductDelta);

		// Product-Season Delta
		Collection<Map> colProductSeasonDelta = BurberryProductAPIDBHelper
				.getProductIdsFromDateRange(LCSProductSeasonLink.class,
						startdate, enddate, modifyDate, bomApi);
		logger.debug(methodName + "Collection of Product Season Delta: "
				+ colProductSeasonDelta);
		hsProductIds.addAll(colProductSeasonDelta);

		// Colourway Delta
		Collection<Map> colColourwayDelta = BurberryProductAPIDBHelper
				.getProductIdsFromDateRange(LCSSKU.class, startdate, enddate,
						modifyDate, bomApi);
		logger.debug(methodName + "Collection of Colourway Delta: "
				+ colColourwayDelta);
		hsProductIds.addAll(colColourwayDelta);

		// Colourway-Season Delta
		Collection<Map> colColourwaySeasonDelta = BurberryProductAPIDBHelper
				.getProductIdsFromDateRange(LCSSKUSeasonLink.class, startdate,
						enddate, modifyDate, bomApi);
		logger.debug(methodName + "Collection of Colourway Season Delta: "
				+ colColourwaySeasonDelta);
		hsProductIds.addAll(colColourwaySeasonDelta);

		// BOM Delta
		Collection<Map> colBOMDelta = getProductBOMDeltaFromDateRange(
				FlexBOMPart.class, startdate, enddate, modifyDate);
		logger.debug(methodName + "Collection BOM Delta: " + colBOMDelta);
		hsProductIds.addAll(colBOMDelta);

		// BOM Link Delta
		Collection<Map> colBOMLinkDelta = getProductBOMDeltaFromDateRange(
				FlexBOMLink.class, startdate, enddate, modifyDate);
		logger.debug(methodName + "Collection BOM Link Delta: "
				+ colBOMLinkDelta);
		hsProductIds.addAll(colBOMLinkDelta);

		// CR R26 - Handle Removed BOM from Specification Customisation - STart
		// Removed BOM Delta Query
		Collection<Map> colRemovedBOMDeltaData = BurberryAPIDBUtil.getRemovedMOADeltaFromDateRange(
				LCSProduct.class, BurProductBOMConstant.BO_TRACK_BOM_NAME,
				BurProductBOMConstant.MOA_TRACK_PRODUCT_ID, startdate, enddate);
		logger.debug(methodName + "Collection Removed BOM Document Delta: "
				+ colRemovedBOMDeltaData);
		hsProductIds.addAll(colRemovedBOMDeltaData);
		// CR R26 - Handle Removed BOM from Specification Customisation - End

		// CR R26 - Handle Removed Specification From Product Customisation -
/*		// Start
		Collection<Map> colRemovedSpecDeltaData = BurberryAPIDBUtil.getRemovedMOADeltaFromDateRange(
				LCSProduct.class,
				BurProductConstant.BO_TRACK_SPECIFICATION_NAME,
				BurProductConstant.MOA_TRACK_PRODUCT_ID, startdate, enddate);
		logger.debug(methodName + "Collection Remove Specification Delta: "
				+ colRemovedSpecDeltaData);
		hsProductIds.addAll(colRemovedSpecDeltaData);
		// CR R26 -Handle Removed Specification From Product Customisation - End
*/
		logger.debug(methodName + "hsProductIds: " + hsProductIds);
		Collection<Map> criteriaMap = new ArrayList<Map>(hsProductIds);
		logger.debug(methodName + "Delta Criteria Map: " + criteriaMap);
		return criteriaMap;
	}

	/**
	 * Method to get Product Ids for updates on BOM using start create. and end
	 * create or start modify and end modify dates.
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
	private static Collection<Map> getProductBOMDeltaFromDateRange(
			Class<? extends FlexTyped> flexTypeClass, Date startdate,
			Date enddate, boolean modifyDate) throws WTException {

		String tableName = flexTypeClass.getSimpleName();
		String methodName = "getProductBOMDeltaFromDateRange() of " + tableName;
		long lStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"BOM Delta StartTime: ");

		logger.debug(methodName + "Start Date " + startdate);
		logger.debug(methodName + "End Date " + enddate);
		logger.debug(methodName + "Modify " + modifyDate);

		// Create a prepared query statement
		PreparedQueryStatement bomDeltaStatement = new PreparedQueryStatement();

		// Check if create / modify
		if (!modifyDate) {
			// get create statement
			bomDeltaStatement = getCreateStampCriteriaStatement(
					bomDeltaStatement, flexTypeClass, startdate, enddate);
		} else {
			// get modified statement
			bomDeltaStatement = getModifyStampCriteriaStatement(
					bomDeltaStatement, flexTypeClass, startdate, enddate);
		}

		appendAdditionalBOMCriteriaByTableName(bomDeltaStatement, flexTypeClass,true);

		logger.info(methodName + "Prepared Query Statement : "
				+ bomDeltaStatement.toString());
		// Exceute query
		Collection<?> result = LCSQuery.runDirectQuery(bomDeltaStatement)
				.getResults();

		long lEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"BOM Delta endTime: ");
		logger.info(methodName + "Total Execution Time (ms): "
				+ (lEndTime - lStartTime));
		logger.debug(methodName + " result " + result);
		return (Collection<Map>) result;
	}

	/**
	 * Method to get Additional Criteria for BOM, BOM Link and BOM Material
	 * Objects.
	 * 
	 * @param preparedStmt
	 * @param tableClass
	 * @param deltaCriteria 
	 * @throws WTException
	 */
	public static void appendAdditionalBOMCriteriaByTableName(
			PreparedQueryStatement preparedStmt,
			Class<? extends FlexTyped> tableClass, boolean deltaCriteria) throws WTException {

		// Append distinct
		preparedStmt.setDistinct(true);
		// Append table
		preparedStmt.appendFromTable(LCSProduct.class);
		// Append select column
		preparedStmt.appendSelectColumn(new QueryColumn(BurConstant.LCSPRODUCT,
				BurConstant.BRANCHID));
		// Append select column
		if(!deltaCriteria){
			preparedStmt.appendSelectColumn(new QueryColumn("FLEXBOMPART",
						BurConstant.BRANCHID));
		}
		// Append Criteria
		preparedStmt.appendAndIfNeeded();
		preparedStmt.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		// Append Criteria
		preparedStmt.appendAnd();
		preparedStmt.appendCriteria(new Criteria(new QueryColumn(
				LCSProduct.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		
		appendSourceSpecCriteria(preparedStmt);

		// [NEW JOIN TO GET PRIMARY SOURCE TO SEASON - END]
		if ("LCSMaterial".equalsIgnoreCase(tableClass.getSimpleName())) {
			// Append Table
			preparedStmt.appendFromTable(LCSMaterial.class);
			// Append Join
			preparedStmt
					.appendJoin(
							new QueryColumn(LCSMaterial.class,
									BurConstant.MASTERREFERENCE_KEY_ID),
							new QueryColumn(
									FlexBOMLink.class,
									BurProductBOMConstant.BOMLINK_MATERIAL_MASTERREFERENCE_KEY_ID));
			// Append Criteria
			preparedStmt.appendAnd();
			preparedStmt.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterial.class, BurConstant.LATESTITERATIONINFO),
					STR_VAL_ONE, Criteria.EQUALS));
			// Append Criteria
			preparedStmt.appendAnd();
			preparedStmt.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterial.class, BurConstant.VERSIONIDA2VERSIONINFO),
					STR_VAL_A, Criteria.EQUALS));
			// Append Criteria
			preparedStmt.appendAndIfNeeded();
			preparedStmt.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterial.class, BurConstant.CHECK_OUT_INFO_STATE),
					"wrk", Criteria.NOT_EQUAL_TO));
		}

		if ("FlexBOMLink".equalsIgnoreCase(tableClass.getSimpleName())
				|| "LCSMaterial".equalsIgnoreCase(tableClass.getSimpleName())) {
			// Append Table
			preparedStmt.appendFromTable(FlexBOMLink.class);
			preparedStmt.appendJoin(new QueryColumn(FlexBOMLink.class,
					BurProductBOMConstant.BOMLINK_BOM_MASTERREFERENCE_KEY_ID),
					new QueryColumn(FlexBOMPart.class,
							BurConstant.MASTERREFERENCE_KEY_ID));

			// Append Criteria
			preparedStmt.appendAnd();
			preparedStmt.appendCriteria(new Criteria(new QueryColumn(
					FlexBOMLink.class, BurProductBOMConstant.DIM_NAME), "",
					Criteria.IS_NULL));
			// Append Criteria
			preparedStmt.appendAnd();
			preparedStmt.appendCriteria(new Criteria(new QueryColumn(
					FlexBOMLink.class, BurProductBOMConstant.BOMLINK_OUTDATE),
					"", Criteria.IS_NULL));

		}

		preparedStmt.appendFromTable(FlexBOMPart.class);
		// Append Join
		/*preparedStmt
				.appendJoin(
						new QueryColumn(LCSProduct.class,
								BurConstant.MASTERREFERENCE_KEY_ID),
						new QueryColumn(
								FlexBOMPart.class,
								BurProductBOMConstant.BOM_PRODUCTMATERIAL_MASTERREFERENCE_KEY_ID));*/
		// Append Criteria
		preparedStmt.appendAndIfNeeded();
		preparedStmt.appendCriteria(new Criteria(new QueryColumn(
				FlexBOMPart.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		// Append Criteria
		preparedStmt.appendAnd();
		preparedStmt.appendCriteria(new Criteria(new QueryColumn(
				FlexBOMPart.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));
		
		// Append Criteria
		preparedStmt.appendAnd();
		preparedStmt.appendCriteria(new Criteria(new QueryColumn(
				FlexBOMPart.class, BurConstant.CHECK_OUT_INFO_STATE),
				"wrk", Criteria.NOT_EQUAL_TO));

	}

	/**
	 * Method to append query to get BOM detail by Primary Source and Primary
	 * Specification.
	 * 
	 * @param preparedStmt
	 * @throws WTException
	 */
	public static void appendSourceSpecCriteria(
			PreparedQueryStatement preparedStmt) throws WTException {

		// Append Table
		preparedStmt.appendFromTable(LCSSourceToSeasonLinkMaster.class);
	
		// Append table
		preparedStmt.appendFromTable(FlexSpecToComponentLink.class);
		// Append Criteria
		preparedStmt.appendAndIfNeeded();
		preparedStmt.appendCriteria(new Criteria(new QueryColumn(
				FlexSpecToComponentLink.class,
				BurProductBOMConstant.SPEC_COMPONENT_LINK_TYPE),
				STR_COMPONENT_BOM_TYPE, Criteria.EQUALS));
		// Append Join
		preparedStmt.appendJoin(new QueryColumn(FlexSpecToComponentLink.class,
				BurProductBOMConstant.COMPONENT_REFERENCE_KEY_ID),
				new QueryColumn(FlexBOMPart.class,
						BurConstant.MASTERREFERENCE_KEY_ID));

		// Append table
		preparedStmt.appendFromTable(FlexSpecToSeasonLink.class);
		
		// Append Join
		preparedStmt.appendJoin(
				new QueryColumn(FlexSpecToSeasonLink.class.getSimpleName(),
						BurProductBOMConstant.SPEC_MASTER_REFERENCE_ID),
				new QueryColumn(FlexSpecToComponentLink.class,
						BurProductBOMConstant.SPECIFICATION_MASTER_REFERENCE));

		// Append table
		preparedStmt.appendFromTable(FlexSpecification.class);
		// Append Criteria
		// Append Join
		preparedStmt.appendAndIfNeeded();
		preparedStmt
				.appendJoin(
						new QueryColumn(LCSProduct.class,
								BurConstant.MASTERREFERENCE_KEY_ID),
						new QueryColumn(
								FlexSpecification.class,
								BurProductBOMConstant.SPEC_OWNER_REF));
		
		preparedStmt.appendCriteria(new Criteria(new QueryColumn(
				FlexSpecification.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		// Append Join
		preparedStmt.appendJoin(
				new QueryColumn(FlexSpecification.class,
						BurProductBOMConstant.SPEC_SOURCE_REFERENCE_KEY_ID),
				new QueryColumn(LCSSourceToSeasonLinkMaster.class
						.getSimpleName(),
						BurProductBOMConstant.SOURCEMASTER_REFERNECE_ID));
		preparedStmt.appendJoin(
				new QueryColumn(FlexSpecToSeasonLink.class.getSimpleName(),
						BurProductBOMConstant.SPEC_MASTER_REFERENCE_ID),
				new QueryColumn(FlexSpecification.class,
						BurProductBOMConstant.SPEC_MASTER_REFERENCE_KEY_ID));
	}

	/**
	 * Method to append create stamp criteria.
	 * 
	 * @param statement
	 *            statement
	 * @param strTableName
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
			PreparedQueryStatement statement, Class<? extends FlexTyped> table,
			Date startdate, Date enddate) throws WTException {
		String methodName = "getCreateStampCriteriaStatement() ";
		logger.debug(methodName + "AppendCreateQuery for "
				+ table.getSimpleName());
		statement.appendAndIfNeeded();
		statement.appendOpenParen();
		statement.appendCriteria(new Criteria(new QueryColumn(table,
				BurConstant.CREATE_STAMP), startdate,
				Criteria.GREATER_THAN_EQUAL));
		statement.appendAnd();
		statement.appendCriteria(new Criteria(new QueryColumn(table,
				BurConstant.CREATE_STAMP), enddate, Criteria.LESS_THAN_EQUAL));
		statement.appendClosedParen();
		// Return Statement
		return statement;
	}

	/**
	 * Method to append modify stamp criteria.
	 * 
	 * @param pqs
	 *            pqs
	 * @param strTableName
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
			PreparedQueryStatement pqs, Class<? extends FlexTyped> tableName,
			Date startdate, Date enddate) throws WTException {
		String methodName = "getModifyStampCriteriaStatement() ";
		logger.debug(methodName + "AppendModifyCriteria for "
				+ tableName.getName());
		pqs.appendAndIfNeeded();
		pqs.appendOpenParen();
		pqs.appendCriteria(new Criteria(new QueryColumn(tableName,
				BurConstant.MODIFY_STAMP), startdate,
				Criteria.GREATER_THAN_EQUAL));
		pqs.appendAnd();
		pqs.appendCriteria(new Criteria(new QueryColumn(tableName,
				BurConstant.MODIFY_STAMP), enddate, Criteria.LESS_THAN_EQUAL));
		pqs.appendClosedParen();
		// Return Statement
		return pqs;
	}

}
