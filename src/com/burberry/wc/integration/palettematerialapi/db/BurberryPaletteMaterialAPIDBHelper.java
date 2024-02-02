package com.burberry.wc.integration.palettematerialapi.db;

import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.util.BurConstant;
import com.burberry.wc.integration.util.BurberryAPIDBUtil;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.color.LCSPalette;
import com.lcs.wc.color.LCSPaletteMaterialLink;
import com.lcs.wc.color.LCSPaletteMaterialColorLink;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.*;
import com.lcs.wc.supplier.LCSSupplier;

/**
 * A DB helper class to handle Database activity. Class contain several method
 * to handle DB activity i.e. creating DB query, statement, and result set
 * allocation. Collection of Criteria Map, Start Date, End Date and Modify are
 * Primary input to fetch details from Flex system.
 *
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryPaletteMaterialAPIDBHelper {

	/**
	 * STR_VAL_A.
	 */
	private static final String STR_VAL_A = "A";

	/**
	 * STR_VAL_ONE.
	 */
	private static final String STR_VAL_ONE = "1";

	/**
	 * STR_VAL_ZERO.
	 */
	private static final String STR_VAL_ZERO = "0";

	/**
	 * STR_WRK.
	 */
	private static final String STR_WRK = "wrk";
	
	/**
	 * STR_EQUAL.
	 */
	private static final String STR_EQUAL = "=";

	/**
	 * STR_QUESTION.
	 */
	private static final String STR_QUESTION = "?";

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPaletteMaterialAPIDBHelper.class);

	/**
	 * Default Constructor.
	 */
	private BurberryPaletteMaterialAPIDBHelper() {

	}

	/**
	 * Method for Final Palette Material API query.
	 * 
	 * @param colCriteria
	 *            Collection Map
	 * @return Collection FlexObject
	 * @throws WTException
	 *             Exception
	 */
	// JIRA - BURBERRY-1363: START
	public static Collection<FlexObject> getPaletteMaterialAPIDataFromDB(
			List<String> colCriteria) throws WTException {

		String methodName = "getPaletteMaterialAPIDataFromDB() ";
		// Track execution time
		long palMatAPIStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"palMatAPIStartTime: ");

		// Initialisation of collection
		Collection<FlexObject> materialObjResults = new ArrayList<FlexObject>();
		logger.debug(methodName + "colCriteria: " + colCriteria);

		// Check if the criteria collection is not empty
		if (colCriteria.isEmpty()) {
			return materialObjResults;
		}

		// Create new prepared query statement
		PreparedQueryStatement pqStatement = new PreparedQueryStatement();
		// Append Distinct
		pqStatement.setDistinct(true);

		// Append table column
		pqStatement.appendFromTable(LCSMaterial.class);
		// Append select column
		pqStatement.appendSelectColumn(new QueryColumn(LCSMaterial.class,
				BurConstant.OBJECTIDENTIFIERID));
		pqStatement.appendSelectColumn(new QueryColumn(LCSMaterial.class
				.getSimpleName(), BurConstant.BRANCHID));

		// Append search criteria
		pqStatement.appendAndIfNeeded();
		pqStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		pqStatement.appendAnd();
		pqStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		pqStatement.appendAnd();
		pqStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		pqStatement.appendAnd();
		pqStatement.appendOpenParen();

		// Append additional search criteria
		appendCriteriaToStatement(pqStatement, colCriteria);

		pqStatement.appendClosedParen();

		logger.info(methodName + "Palette Material API Data Extraction Query: "
				+ pqStatement.toString());
		materialObjResults = LCSQuery.runDirectQuery(pqStatement).getResults();
		logger.debug(methodName + " materialObjResults: " + materialObjResults);

		// Track execution time
		long palMatAPIEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"palMatAPIEndTime: ");
		logger.info(methodName
				+ "Palette Material API Data Extraction Query Total Execution Time (ms): "
				+ (palMatAPIEndTime - palMatAPIStartTime));

		// Return Statement
		return materialObjResults;
	}
	// JIRA - BURBERRY-1363: END
	
	/**
	 * Method to dynamically generate criteria statement.
	 * 
	 * @param pqstatement
	 *            Prepared query statement
	 * @param colCriteria
	 *            collection of criteria map
	 * @throws WTException
	 *             throw wtexcetion
	 */
	// JIRA - BURBERRY-1363: START
	private static void appendCriteriaToStatement(
			PreparedQueryStatement pqstatement, List<String> colCriteria)
			throws WTException {

		String methodName = "appendCriteriaToStatement() ";
		int criteriaCount = 0;
		// Loop through the complete collection of map criteria
		for (String strBranchId : colCriteria) {
			logger.debug(methodName + " mapCriteria: " + strBranchId);
			String strTableName = "LCSMATERIAL";
			String strColumnName = "BRANCHIDITERATIONINFO";
			logger.debug(methodName + "Counter: " + criteriaCount);
			if (criteriaCount > 0) {
				pqstatement.appendOrIfNeeded();
			}
			pqstatement.appendCriteria(new Criteria(new QueryColumn(strTableName,
					strColumnName), STR_QUESTION, STR_EQUAL), strBranchId);
			criteriaCount++;
		}
	}
	// JIRA - BURBERRY-1363: END
	/**
	 * Method to generate delta query.
	 * 
	 * @param startdate
	 *            Date
	 * @param enddate
	 *            Date
	 * @param updateMode
	 *            Boolean
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */
	public static Collection<Map> getMaterialDeltaFromDateRange(Date startdate,
			Date enddate, boolean updateMode) throws WTException {

		String methodName = "getMaterialDeltaFromDateRange() ";
		logger.debug(methodName + "Start_Date: " + startdate);
		logger.debug(methodName + "End_Date: " + enddate);
		logger.debug(methodName + "modifyDate: " + updateMode);

		// Initialisation
		HashSet<Map> hashSetMaterials = new HashSet<Map>();

		// Material Delta Query Data
		Collection<Map> colMaterialDelta = getMaterialIdsFromDateRange(
				LCSMaterial.class, startdate, enddate, updateMode);
		logger.debug(methodName + "Collection of Material Delta: "
				+ colMaterialDelta);
		hashSetMaterials.addAll(colMaterialDelta);

		// Material Supplier Delta Query Data
		Collection<Map> colMaterialSupplierDelta = getMaterialIdsFromDateRange(
				LCSMaterialSupplier.class, startdate, enddate, updateMode);
		logger.debug(methodName + "Collection of Material Supplier Delta: "
				+ colMaterialSupplierDelta);
		hashSetMaterials.addAll(colMaterialSupplierDelta);

		// Supplier Delta Query Data only for Updates
		if (updateMode) {
			Collection<Map> colSupplierDelta = getMaterialIdsFromDateRange(
					LCSSupplier.class, startdate, enddate, updateMode);
			logger.debug(methodName + "Collection of Supplier Delta: "
					+ colSupplierDelta);
			hashSetMaterials.addAll(colSupplierDelta);

		}

		// Material Added to Palette Delta Query Data
		if (!updateMode) {
			// Get collection of materials added to palette
			Collection<Map> colPaletteMaterialDelta = getPaletteMaterialDeltaFromDateRange(
					startdate, enddate);
			logger.debug(methodName
					+ "Collection of Palette to Material Delta: "
					+ colPaletteMaterialDelta);
			hashSetMaterials.addAll(colPaletteMaterialDelta);
		}
		
		//BURBERRY-1505 Palette Material API Delta call using 'MODIFY' parameter not working as expected: Start
		// Palette to Material Colour Delta Query Data
		Collection<Map> colPaletteMaterialColourDelta = getPaletteMaterialColourDeltaFromDateRange(
				startdate, enddate, updateMode);
		logger.debug(methodName
				+ "Collection Palette to Material Colour Delta: "
				+ colMaterialDelta);
		hashSetMaterials.addAll(colPaletteMaterialColourDelta);
		logger.debug(methodName + "hashSetMaterials: " + hashSetMaterials);
		//BURBERRY-1505 Palette Material API Delta call using 'MODIFY' parameter not working as expected: End
		
		// CR R26: Handle Remove Object Customisation :
		// Start
		// Removed Palette Material Colour Delta Query Data only for Updates
		Collection<Map> colRemovePaletteMaterialColourDelta = BurberryAPIDBUtil
				.getRemovedMOADeltaFromDateRange(
						LCSMaterial.class,
						BurPaletteMaterialConstant.BO_TRACK_PALATTE_MATERIAL_COLOR_NAME,
						BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_ID,
						startdate, enddate);
		logger.debug(methodName
				+ "Collection Removed Palette to Material Colour Delta: "
				+ colRemovePaletteMaterialColourDelta);
		hashSetMaterials.addAll(colRemovePaletteMaterialColourDelta);

		// Removed Risk Management MOA Delta Query
		Collection<Map> colRemovedRiskManagementDeltaData = BurberryAPIDBUtil
				.getRemovedMOADeltaFromDateRange(
						LCSMaterial.class,
						BurPaletteMaterialConstant.BO_TRACK_RISK_MANAGEMENT_NAME,
						BurPaletteMaterialConstant.MOA_TRACK_RISK_MANAGEMENT_OWNER_ID,
						startdate, enddate);
		logger.debug(methodName + "Collection Removed Risk Management Delta: "
				+ colRemovedRiskManagementDeltaData);
		hashSetMaterials.addAll(colRemovedRiskManagementDeltaData);

		// Removed Yarn Details MOA Delta Query
		Collection<Map> colRemovedYarnDetailsDelta = BurberryAPIDBUtil
				.getRemovedMOADeltaFromDateRange(
						LCSMaterial.class,
						BurPaletteMaterialConstant.BO_TRACK_YARN_DETAILS_NAME,
						BurPaletteMaterialConstant.MOA_TRACK_YARN_DETAILS_OWNER_ID,
						startdate, enddate);
		logger.debug(methodName
				+ "Collection Removed Material Pricing Entry Delta: "
				+ colRemovedYarnDetailsDelta);
		hashSetMaterials.addAll(colRemovedYarnDetailsDelta);

		// Removed Material Price Management MOA Delta Query
		Collection<Map> colMatPriceManagementDelta = BurberryAPIDBUtil
				.getRemovedMOADeltaFromDateRange(
						LCSMaterial.class,
						BurPaletteMaterialConstant.BO_TRACK_MATERIAL_PRICE_MANAGEMENT_NAME,
						BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_PRICE_MANAGEMENT_OWNER_ID,
						startdate, enddate);
		logger.debug(methodName
				+ "Collection Removed Material Pricing Entry Delta: "
				+ colMatPriceManagementDelta);
		hashSetMaterials.addAll(colMatPriceManagementDelta);

		// Removed Material Pricing Entry Delta Query
		Collection<Map> colRemovedMatPricingEntryDeltaData = BurberryAPIDBUtil
				.getRemovedMOADeltaFromDateRange(
						LCSMaterial.class,
						BurPaletteMaterialConstant.BO_TRACK_MATERIAL_PRICING_ENTRY_NAME,
						BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_ID,
						startdate, enddate);
		logger.debug(methodName
				+ "Collection Removed Material Pricing Entry Delta: "
				+ colRemovedMatPricingEntryDeltaData);
		hashSetMaterials.addAll(colRemovedMatPricingEntryDeltaData);

		// Removed Material Document Delta Query
		Collection<Map> colRemovedMatDocumentDeltaData = BurberryAPIDBUtil
				.getRemovedMOADeltaFromDateRange(
						LCSMaterial.class,
						BurPaletteMaterialConstant.BO_TRACK_MATERIAL_DOCUMENT_NAME,
						BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_ID,
						startdate, enddate);
		logger.debug(methodName
				+ "Collection Removed Material Document Delta: "
				+ colRemovedMatDocumentDeltaData);
		hashSetMaterials.addAll(colRemovedMatDocumentDeltaData);

		// BURBERRY-1485 RD 74: Material Supplier Documents - Start
		// Removed Material Supplier Document Delta Query
		Collection<Map> colRemovedMatSupplierDocumentDeltaData = BurberryAPIDBUtil
				.getRemovedMOADeltaFromDateRange(
						LCSMaterial.class,
						BurPaletteMaterialConstant.BO_TRACK_MATERIAL_SUPPLIER_DOCUMENT_NAME,
						BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_ID,
						startdate, enddate);
		logger.debug(methodName
				+ "Collection Removed Material Supplier Document Delta: "
				+ colRemovedMatSupplierDocumentDeltaData);
		hashSetMaterials.addAll(colRemovedMatSupplierDocumentDeltaData);
		
		// BURBERRY-1485 RD 74: Material Supplier Documents - End
		
		// CR R26: Handle Remove MOA Object Customization : End

		
		
		Collection<Map> criteriaMap = new ArrayList<Map>(hashSetMaterials);
		logger.debug(methodName + "Delta Criteria Map: " + criteriaMap);
		// Return Statement
		return criteriaMap;
	}

	/**
	 * Method to get Material Delta Query.
	 * 
	 * @param tableName
	 *            Table Name
	 * @param startdate
	 *            Date
	 * @param enddate
	 *            Date
	 * @param updateMode
	 *            Boolean
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */
	private static Collection<Map> getMaterialIdsFromDateRange(Class tableName,
			Date startdate, Date enddate, boolean updateMode)
			throws WTException {

		String methodName = "getMaterialIdsFromDateRange() ";
		// Track execution time
		long matDeltaStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"matDeltaStartTime: ");
		// Create a prepared query statement
		PreparedQueryStatement matStatement = new PreparedQueryStatement();
		// Append distinct
		matStatement.setDistinct(true);
		// Append tables
		matStatement.appendFromTable(LCSMaterial.class);

		// Append Select column
		matStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSMATERIAL, BurConstant.BRANCHID));

		// Append additional criteria for latest iteration info,
		// effect latest, version info based on table name
		appendAdditionalCriteriaBasedOnTableName(matStatement, tableName);

		matStatement.appendAnd();
		matStatement.appendOpenParen();

		// Check if create / modify
		if (!updateMode) {
			// get create statement
			matStatement = BurberryAPIDBUtil.getCreateStampCriteriaStatement(
					matStatement, tableName, startdate, enddate);
		} else {
			// get modified statement
			matStatement = BurberryAPIDBUtil.getModifyStampCriteriaStatement(
					matStatement, tableName, startdate, enddate);
		}
		matStatement.appendClosedParen();

		logger.info(methodName + tableName.getSimpleName().toUpperCase()
				+ " Delta Query Statement: " + matStatement.toString());
		// Exceute query
		Collection<?> matDeltaResult = LCSQuery.runDirectQuery(matStatement)
				.getResults();
		logger.debug(methodName + " matDeltaResult: " + matDeltaResult);

		// Track execution time
		long matDeltaEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"matDeltaEndTime: ");
		logger.info(methodName + "Material Delta Total Execution Time (ms): "
				+ (matDeltaEndTime - matDeltaStartTime));

		// Return Statement
		return (Collection<Map>) matDeltaResult;
	}

	/**
	 * Method to get Delta Data query.
	 * 
	 * @param startdate
	 *            Date
	 * @param enddate
	 *            Date
	 * @return Collection Map
	 * @throws WTException
	 *             Exception
	 */
	private static Collection<Map> getPaletteMaterialDeltaFromDateRange(
			Date startdate, Date enddate) throws WTException {

		String methodName = "getPaletteMaterialDeltaFromDateRange() ";
		// Track execution time
		long palMatDeltaStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "palMatDeltaStartTime: ");

		// Create a prepared query statement
		PreparedQueryStatement palMatStatement = new PreparedQueryStatement();
		// Append distinct
		palMatStatement.setDistinct(true);
		// Append tables
		palMatStatement.appendFromTable(LCSMaterial.class);
		palMatStatement.appendFromTable(LCSPalette.class);
		palMatStatement.appendFromTable(LCSPaletteMaterialLink.class);

		// Append Select column
		palMatStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSMATERIAL, BurConstant.BRANCHID));

		// Append Joins
		palMatStatement.appendJoin(new QueryColumn(LCSPalette.class,
				BurConstant.OBJECTIDENTIFIERID),
				new QueryColumn(LCSPaletteMaterialLink.class,
						BurConstant.ROLEBOBJECTREF_KEY_ID));
		palMatStatement.appendJoin(new QueryColumn(LCSMaterial.class,
				BurConstant.MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSPaletteMaterialLink.class,
						BurConstant.ROLEAOBJECTREF_KEY_ID));

		// Append Basic Criteria
		palMatStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		palMatStatement.appendAnd();
		palMatStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));
		palMatStatement.appendAndIfNeeded();
		palMatStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));

		// Append Create statement
		palMatStatement.appendAndIfNeeded();
		palMatStatement.appendOpenParen();
		palMatStatement = BurberryAPIDBUtil.getCreateStampCriteriaStatement(
				palMatStatement, LCSPaletteMaterialLink.class, startdate,
				enddate);
		palMatStatement.appendClosedParen();

		logger.info(methodName
				+ "LCSPaletteMaterialLink Delta Query Statement: "
				+ palMatStatement.toString());
		// Exceute query
		Collection<?> palMatDeltaResult = LCSQuery.runDirectQuery(
				palMatStatement).getResults();
		logger.debug(methodName + " palMatDeltaResult " + palMatDeltaResult);

		// Track execution time
		long palMatDeltaEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"palMatDeltaEndTime: ");
		logger.info(methodName
				+ "Palette Material Delta Total Execution Time (ms): "
				+ (palMatDeltaEndTime - palMatDeltaStartTime));

		// Return Statement
		return (Collection<Map>) palMatDeltaResult;

	}
	
	//BURBERRY-1505 Palette Material API Delta call using 'MODIFY' parameter not working as expected: Start
	/**
	 * Method to get colour delta query.
	 * 
	 * @param startdate
	 *            Date
	 * @param enddate
	 *            Date
	 * @param update
	 *            Boolean
	 * @return collection map
	 * @throws WTException
	 *             exception
	 */
	private static Collection<Map> getPaletteMaterialColourDeltaFromDateRange(
			Date startdate, Date enddate, boolean update) throws WTException {

		String methodName = "getPaletteMaterialColourDeltaFromDateRange() ";
		// Track execution time
		long palMatColourDeltaStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "palMatColourDeltaStartTime: ");

		// Create a prepared query statement
		PreparedQueryStatement palMatColStatement = new PreparedQueryStatement();
		// Append distinct
		palMatColStatement.setDistinct(true);
		// Append tables
		palMatColStatement.appendFromTable(LCSMaterial.class);
		palMatColStatement.appendFromTable(LCSMaterialColor.class);
		palMatColStatement.appendFromTable(LCSPaletteMaterialColorLink.class);

		// Append Select column
		palMatColStatement.appendSelectColumn(new QueryColumn(
				BurConstant.LCSMATERIAL, BurConstant.BRANCHID));

		// Append Joins
		palMatColStatement.appendJoin(new QueryColumn(LCSMaterialColor.class,
				BurPaletteMaterialConstant.MATERIAL_MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSMaterial.class,
						BurConstant.MASTERREFERENCE_KEY_ID));
		palMatColStatement.appendJoin(new QueryColumn(
				LCSPaletteMaterialColorLink.class,
				BurConstant.ROLEAOBJECTREF_KEY_ID), new QueryColumn(
				LCSMaterialColor.class, BurConstant.OBJECTIDENTIFIERID));

		// Append Basic Criteria
		palMatColStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		palMatColStatement.appendAnd();
		palMatColStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));
		palMatColStatement.appendAndIfNeeded();
		palMatColStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));

		palMatColStatement.appendAndIfNeeded();
		palMatColStatement.appendOpenParen();
		// Check if create / modify
		if (!update) {
			// get create statement
			palMatColStatement = BurberryAPIDBUtil.getCreateStampCriteriaStatement(
					palMatColStatement, LCSPaletteMaterialColorLink.class,
					startdate, enddate);
		} else {
			// get modified statement
			palMatColStatement = BurberryAPIDBUtil.getModifyStampCriteriaStatement(
					palMatColStatement, LCSPaletteMaterialColorLink.class,
					startdate, enddate);
		}
		palMatColStatement.appendClosedParen();

		logger.info(methodName
				+ "LCSPaletteMaterialColorLink Delta Query Statement: "
				+ palMatColStatement.toString());
		// Exceute query
		Collection<?> palMatColorDeltaResult = LCSQuery.runDirectQuery(
				palMatColStatement).getResults();
		logger.debug(methodName + " palMatColorDeltaResult: "
				+ palMatColorDeltaResult);

		// Track execution time
		long palMatColorDeltaEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "palMatColorDeltaEndTime: ");
		logger.info(methodName
				+ "Palette Material Colour Delta Total Execution Time (ms): "
				+ (palMatColorDeltaEndTime - palMatColourDeltaStartTime));

		// Return Statement
		return (Collection<Map>) palMatColorDeltaResult;
	}
	//BURBERRY-1505 Palette Material API Delta call using 'MODIFY' parameter not working as expected: End

	/**
	 * Method to append basic additional criteria based on table.
	 * 
	 * @param materialStatement
	 *            statement
	 * @param tableName
	 *            .getName() table name
	 * @throws WTException
	 *             exception
	 */
	private static void appendAdditionalCriteriaBasedOnTableName(
			PreparedQueryStatement materialStatement, Class tableName)
			throws WTException {
		String methodName = "appendAdditionalCriteriaBasedOnTableName() ";
		logger.debug(methodName + " Append Table: " + tableName.getSimpleName());

		// Append Criteria for Material
		if ("LCSMATERIAL".equalsIgnoreCase(tableName.getSimpleName())) {
			// Append Criteria
			materialStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterial.class, BurConstant.LATESTITERATIONINFO),
					STR_VAL_ONE, Criteria.EQUALS));
			materialStatement.appendAnd();
			materialStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterial.class, BurConstant.VERSIONIDA2VERSIONINFO),
					STR_VAL_A, Criteria.EQUALS));
		}
		// Append Criteria for Supplier
		else if ("LCSSUPPLIER".equalsIgnoreCase(tableName.getSimpleName())) {
			// Append Table
			materialStatement.appendFromTable(LCSSupplier.class);
			materialStatement.appendFromTable(LCSMaterialSupplier.class);
			materialStatement.appendFromTable(LCSMaterialSupplierMaster.class);
			// Append Joins
			materialStatement.appendJoin(new QueryColumn(
					LCSMaterialSupplier.class,
					BurConstant.MASTERREFERENCE_KEY_ID), new QueryColumn(
					LCSMaterialSupplierMaster.class,
					BurConstant.OBJECTIDENTIFIERID));
			materialStatement
					.appendJoin(
							new QueryColumn(
									LCSMaterialSupplierMaster.class,
									BurPaletteMaterialConstant.SUPPLIER_MASTERREFERENCE_KEY_ID),
							new QueryColumn(LCSSupplier.class,
									BurConstant.MASTERREFERENCE_KEY_ID));
			materialStatement
					.appendJoin(
							new QueryColumn(
									LCSMaterialSupplierMaster.class,
									BurPaletteMaterialConstant.MATERIAL_MASTERREFERENCE_KEY_ID),
							new QueryColumn(LCSMaterial.class,
									BurConstant.MASTERREFERENCE_KEY_ID));
			// Append Basic Criteria
			materialStatement.appendAndIfNeeded();
			materialStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterial.class, BurConstant.CHECK_OUT_INFO_STATE),
					STR_WRK, Criteria.NOT_EQUAL_TO));
			materialStatement.appendAndIfNeeded();
			materialStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterial.class, BurConstant.LATESTITERATIONINFO),
					STR_VAL_ONE, Criteria.EQUALS));
			materialStatement.appendAndIfNeeded();
			materialStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSSupplier.class, BurConstant.CHECK_OUT_INFO_STATE),
					STR_WRK, Criteria.NOT_EQUAL_TO));
			materialStatement.appendAndIfNeeded();
			materialStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSSupplier.class, BurConstant.LATESTITERATIONINFO),
					STR_VAL_ONE, Criteria.EQUALS));
			materialStatement.appendAndIfNeeded();
			materialStatement.appendCriteria(new Criteria(
					new QueryColumn(LCSMaterialSupplier.class,
							BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
					Criteria.NOT_EQUAL_TO));
			materialStatement.appendAndIfNeeded();
			materialStatement.appendCriteria(new Criteria(
					new QueryColumn(LCSMaterialSupplier.class,
							BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
					Criteria.EQUALS));
			materialStatement.appendAndIfNeeded();
			materialStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterialSupplier.class, BurConstant.ACTIVE),
					STR_VAL_ONE, Criteria.EQUALS));
			materialStatement.appendAndIfNeeded();
			materialStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterialSupplierMaster.class,
					BurPaletteMaterialConstant.PLACEHOLDER), STR_VAL_ZERO,
					Criteria.EQUALS));

		}
		// Append Criteria for Material Supplier
		else if ("LCSMATERIALSUPPLIER".equalsIgnoreCase(tableName
				.getSimpleName())) {
			// Append Tables
			materialStatement.appendFromTable(LCSMaterialSupplier.class);
			materialStatement.appendFromTable(LCSMaterialSupplierMaster.class);
			// Append Joins
			materialStatement.appendJoin(new QueryColumn(
					LCSMaterialSupplier.class,
					BurConstant.MASTERREFERENCE_KEY_ID), new QueryColumn(
					LCSMaterialSupplierMaster.class,
					BurConstant.OBJECTIDENTIFIERID));
			materialStatement
					.appendJoin(
							new QueryColumn(
									LCSMaterialSupplierMaster.class,
									BurPaletteMaterialConstant.MATERIAL_MASTERREFERENCE_KEY_ID),
							new QueryColumn(LCSMaterial.class,
									BurConstant.MASTERREFERENCE_KEY_ID));
			// Append Basic Criteria
			materialStatement.appendAndIfNeeded();
			materialStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterial.class, BurConstant.CHECK_OUT_INFO_STATE),
					STR_WRK, Criteria.NOT_EQUAL_TO));
			materialStatement.appendAndIfNeeded();
			materialStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterial.class, BurConstant.LATESTITERATIONINFO),
					STR_VAL_ONE, Criteria.EQUALS));
			materialStatement.appendAndIfNeeded();
			materialStatement.appendCriteria(new Criteria(
					new QueryColumn(LCSMaterialSupplier.class,
							BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
					Criteria.NOT_EQUAL_TO));
			materialStatement.appendAndIfNeeded();
			materialStatement.appendCriteria(new Criteria(
					new QueryColumn(LCSMaterialSupplier.class,
							BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
					Criteria.EQUALS));
			materialStatement.appendAndIfNeeded();
			materialStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterialSupplier.class, BurConstant.ACTIVE),
					STR_VAL_ONE, Criteria.EQUALS));
			materialStatement.appendAndIfNeeded();
			materialStatement.appendCriteria(new Criteria(new QueryColumn(
					LCSMaterialSupplierMaster.class,
					BurPaletteMaterialConstant.PLACEHOLDER), STR_VAL_ZERO,
					Criteria.EQUALS));

		}
	}
}