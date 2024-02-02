package com.burberry.wc.integration.util;

import java.util.*;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.productapi.constant.BurProductConstant;
import com.burberry.wc.integration.productbomapi.constant.BurProductBOMConstant;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierMaster;
import com.lcs.wc.moa.LCSMOAObject;

/**
 * Utility class for all API DB.
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */

public final class BurberryAPIDBUtil {

	/**
	 * Default Constructor.
	 */
	private BurberryAPIDBUtil() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryAPIDBUtil.class);

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
	 * STR_VAL_ZERO.
	 */
	private static final String STR_VAL_ZERO = "0";

	/**
	 * Method to append request criteria.
	 * 
	 * @param statement
	 *            Prepared Statement
	 * @param colCriteria
	 *            Collection Criteria
	 */
	public static void appendRequestCriteriaToStatement(
			PreparedQueryStatement statement, Collection<Map> colCriteria) {

		String methodName = "appendRequestCriteriaToStatement() ";
		int iCounter = 0;

		statement.appendAndIfNeeded();
		statement.appendOpenParen();

		// Loop through the complete collection of map criteria
		for (Map<String, Object> mapCriteria : colCriteria) {
			logger.debug(methodName + " mapCriteria: " + mapCriteria);
			if (iCounter > 0) {
				statement.appendOrIfNeeded();
			}
			// Loop through each map and get key and value
			for (Map.Entry<String, Object> mapEntry : mapCriteria.entrySet()) {
				logger.debug(methodName + "mapEntry: " + mapEntry);
				String mapKey = mapEntry.getKey();
				logger.debug(methodName + " mapKey: " + mapKey);
				String strTableName = BurConstant.STRING_EMPTY;
				String strColumnName = BurConstant.STRING_EMPTY;
				String strOperator = BurConstant.STRING_EMPTY;
				String strValue = BurConstant.STRING_EMPTY;
				// Split the key
				String[] strTemp = mapKey.split("\\.");
				logger.debug(methodName + "Temp: " + Arrays.toString(strTemp));
				strTableName = strTemp[0];
				logger.debug(methodName + "Table_Name: " + strTableName);
				strColumnName = strTemp[1];
				logger.debug(methodName + "Column_Name: " + strColumnName);
				strOperator = strTemp[2];
				logger.debug(methodName + "String Operator: " + strOperator);

				logger.debug(methodName + "Value: " + strValue);

				// Append criteria based on the object Date / String /
				// Boolean / Number
				appendObjectCriteriaStatement(statement, strTableName,
						strColumnName, strOperator, mapEntry);

			}
			iCounter++;
		}

		statement.appendClosedParen();
	}

	/**
	 * Method to append Object criteria statement (date/string).
	 * 
	 * @param statement
	 *            Prepared Query Statement
	 * @param strTableName
	 *            Table Name
	 * @param strColumnName
	 *            Column Name
	 * @param strOperator
	 *            Opertor
	 * @param mapEntry
	 *            map criteria
	 */
	public static void appendObjectCriteriaStatement(
			PreparedQueryStatement statement, String strTableName,
			String strColumnName, String strOperator,
			Entry<String, Object> mapEntry) {

		String methodName = "appendObjectCriteriaStatement() ";
		logger.debug(methodName + "TableName: " + strTableName);
		logger.debug(methodName + "ColumnName: " + strColumnName);
		logger.debug(methodName + "Operator: " + strOperator);
		// Check if the operator is TO_DATE
		if ("TO_DATE".equalsIgnoreCase(strOperator)) {
			Date dateValue = (Date) mapEntry.getValue();
			logger.debug(methodName + "Date Value: " + dateValue);
			statement.appendCriteria(new Criteria(new QueryColumn(strTableName,
					strColumnName), "?", "="), dateValue);
		}
		// Defect Fix: Start
		else if ("IS_NULL".equalsIgnoreCase(strOperator)) {
			logger.debug(methodName + "IS NULL Value: ");
			statement.appendCriteria(new Criteria(new QueryColumn(strTableName,
					strColumnName), "?", "="), "0");
			statement.appendOr();
			statement.appendCriteria(new Criteria(new QueryColumn(strTableName,
					strColumnName), "?", Criteria.IS_NULL));
		}
		// Defect Fix: End
		// Else data is string, number or boolean
		else {
			String strValue = (String) mapEntry.getValue();
			logger.debug(methodName + "String Value: " + strValue);
			statement.appendCriteria(new Criteria(new QueryColumn(strTableName,
					strColumnName), "?", strOperator), strValue);

		}
	}

	/**
	 * checkIfObjectExists.
	 * 
	 * @param strBranchId
	 *            Branch ID
	 * @param objectIdsList
	 *            Object
	 * @return {@link Boolean}
	 */
	public static boolean checkIfObjectExists(String strBranchId,
			Collection<String> objectIdsList) {

		String methodName = "checkIfObjectExists() ";
		boolean idExists = false;
		// object needs to be added if list is empty
		if (objectIdsList == null) {
			idExists = true;
		} else if (objectIdsList.contains(strBranchId)) {
			idExists = true;
		}
		logger.debug(methodName + " object id " + strBranchId
				+ " exists in list " + idExists);
		return idExists;
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
	public static PreparedQueryStatement getCreateStampCriteriaStatement(
			PreparedQueryStatement statement, Class table, Date startdate,
			Date enddate) throws WTException {
		String methodName = "getCreateStampCriteriaStatement() ";
		logger.debug(methodName + "AppendCreateQuery for "
				+ table.getSimpleName());
		statement.appendCriteria(new Criteria(new QueryColumn(table,
				BurConstant.CREATE_STAMP), startdate,
				Criteria.GREATER_THAN_EQUAL));
		statement.appendAnd();
		statement.appendCriteria(new Criteria(new QueryColumn(table,
				BurConstant.CREATE_STAMP), enddate, Criteria.LESS_THAN_EQUAL));
		// Return Statement
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
	public static PreparedQueryStatement getModifyStampCriteriaStatement(
			PreparedQueryStatement pqs, Class tableName, Date startdate,
			Date enddate) throws WTException {
		String methodName = "getModifyStampCriteriaStatement() ";
		logger.debug(methodName + "AppendModifyCriteria for "
				+ tableName.getName());
		pqs.appendCriteria(new Criteria(new QueryColumn(tableName,
				BurConstant.MODIFY_STAMP), startdate,
				Criteria.GREATER_THAN_EQUAL));
		pqs.appendAnd();
		pqs.appendCriteria(new Criteria(new QueryColumn(tableName,
				BurConstant.MODIFY_STAMP), enddate, Criteria.LESS_THAN_EQUAL));
		// Return Statement
		return pqs;
	}

	// CR R26: Handle Removed Object Delta Updates DB Query:
	// Start

	/**
	 * Method to Query MOA Table Delta Updates.
	 * 
	 * @param tableName
	 *            Owner Reference Table
	 * @param moaModifyTimestamp
	 * @param moaFlexType
	 *            moa flex type
	 * @param startdate
	 *            start date
	 * @param enddate
	 *            end date
	 * @param updateMode
	 *            boolean
	 * @return Collection <Map>
	 * @throws WTException
	 *             Exception
	 */
	public static Collection<Map> getRemovedMOADeltaFromDateRange(
			Class tableName, String moaTableName, String moaColumnName,
			Date startdate, Date enddate) throws WTException {

		// Method Name
		String methodName = "getRemovedMOADeltaFromDateRange() ";

		// Track Start Time
		long moaDeltaStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Removed MOA Delta Query Start Time: ");

		String flexTypeIdPath = BurberryAPIUtil.getFlexTypeIdPath(moaTableName);
		logger.debug(methodName + "Flex Type Path: " + flexTypeIdPath);

		logger.debug(methodName + "MOA Table Name: " + moaTableName);
		logger.debug(methodName + "MOA Column Name: " + moaColumnName);

		String strAttColumnName = BurberryAPIUtil.getObjectColumnName(
				moaTableName, moaColumnName);
		logger.debug(methodName + "MOA Att Column Name: " + strAttColumnName);

		// Initialisation query statement
		PreparedQueryStatement moaDeltaQueryStatment = new PreparedQueryStatement();

		// Set Distinct
		moaDeltaQueryStatment.setDistinct(true);

		// Set Table Names
		moaDeltaQueryStatment.appendFromTable(tableName);
		moaDeltaQueryStatment.appendFromTable(LCSMOAObject.class);

		// Select Column Names
		moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(tableName
				.getSimpleName(), BurConstant.BRANCHID));
		moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
				LCSMOAObject.class, BurConstant.OBJECTIDENTIFIERID));
		moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
				BurConstant.LCSMOAOBJECT, "flexTypeIdPath"));

		// Based on MOA Table Append Additional Select Columns
		moaDeltaQueryStatment = appendAdditionalSelectColumns(moaTableName,
				moaDeltaQueryStatment);

		// Check for Material Price Management Table
		if (!BurPaletteMaterialConstant.BO_TRACK_MATERIAL_PRICE_MANAGEMENT_NAME
				.equalsIgnoreCase(moaTableName)) {
			// Append Join
			moaDeltaQueryStatment.appendJoin(
					new QueryColumn(tableName.getSimpleName(),
							BurConstant.BRANCHID), new QueryColumn(
									BurConstant.LCSMOAOBJECT, strAttColumnName));
		} else {
			moaDeltaQueryStatment = appendMaterialSupplierJoinStatement(
					moaDeltaQueryStatment, strAttColumnName);
		}

		moaDeltaQueryStatment = appendBaseCriteriaForMOATable(tableName,
				startdate, enddate, flexTypeIdPath, moaDeltaQueryStatment);

		// Log Query Statement
		logger.info(methodName + "MOA Delta Prepared Query Statement: "
				+ moaDeltaQueryStatment.toString());
		Collection<?> resultsMOAData = LCSQuery.runDirectQuery(
				moaDeltaQueryStatment).getResults();

		// Track End Time
		long moaDeltaEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"MOA Delta Query End Time: ");
		// Track total execution time
		logger.info(methodName + "MOA Delta Total Execution Time (ms): "
				+ (moaDeltaEndTime - moaDeltaStartTime));
		// Results
		logger.debug(methodName + " resultsMOAData " + resultsMOAData);
		// Return Statement
		return (Collection<Map>) resultsMOAData;
	}

	/**
	 * @param tableName
	 * @param startdate
	 * @param enddate
	 * @param flexTypeIdPath
	 * @param moaDeltaQueryStatment
	 * @return
	 * @throws WTException
	 */
	public static PreparedQueryStatement appendBaseCriteriaForMOATable(
			Class tableName, Date startdate, Date enddate,
			String flexTypeIdPath, PreparedQueryStatement moaDeltaQueryStatment)
					throws WTException {
		// Append Search Criteria Flex type id path
		moaDeltaQueryStatment.appendAndIfNeeded();
		moaDeltaQueryStatment.appendCriteria(
				new Criteria(new QueryColumn(BurConstant.LCSMOAOBJECT,
						"flexTypeIdPath"), "?", Criteria.LIKE), flexTypeIdPath
				+ "%");
		// Append Date Criteria
		moaDeltaQueryStatment.appendAndIfNeeded();
		moaDeltaQueryStatment.appendOpenParen();
		// Append Modify Time Stamp as Search Criteria
		getModifyStampCriteriaStatement(moaDeltaQueryStatment,
				LCSMOAObject.class, startdate, enddate);
		moaDeltaQueryStatment.appendClosedParen();

		// Append latest iteration info
		moaDeltaQueryStatment.appendAndIfNeeded();
		moaDeltaQueryStatment.appendCriteria(new Criteria(new QueryColumn(
				tableName, BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));

		// Append criteria for version info
		moaDeltaQueryStatment.appendAnd();
		moaDeltaQueryStatment.appendCriteria(new Criteria(new QueryColumn(
				tableName, BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));

		// Append criteria for check out state
		moaDeltaQueryStatment.appendAndIfNeeded();
		moaDeltaQueryStatment.appendCriteria(new Criteria(new QueryColumn(
				tableName, BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));

		// Append criteria for effected-out-date
		moaDeltaQueryStatment.appendAndIfNeeded();
		moaDeltaQueryStatment.appendCriteria(new Criteria(new QueryColumn(
				LCSMOAObject.class, "effectOutDate"), "", Criteria.IS_NULL));

		// Append criteria for dropped
		moaDeltaQueryStatment.appendAndIfNeeded();
		moaDeltaQueryStatment.appendCriteria(new Criteria(new QueryColumn(
				LCSMOAObject.class, "dropped"), "1", Criteria.NOT_EQUAL_TO));
		return moaDeltaQueryStatment;
	}

	/**
	 * Method to Append Additional Columns
	 * 
	 * @param moaTable
	 *            String
	 * @param moaDeltaQueryStatment
	 *            Query Statement
	 * @return Query Statement
	 * @throws WTException
	 *             Exception
	 */
	private static PreparedQueryStatement appendAdditionalSelectColumns(
			String moaTable, PreparedQueryStatement moaDeltaQueryStatment)
					throws WTException {
		String methodName = "appendAdditionalSelectColumns() ";
		logger.info("MOA Table Name Passed as Argument  >>>>>>>>>>>>>>  "+moaTable);
		// Track Palette Deletions
		if (BurPaletteMaterialConstant.BO_TRACK_PALATTE_MATERIAL_COLOR_NAME.equalsIgnoreCase(moaTable)) {
			// Get Material Colour Column
			String strMaterialColourColumn = BurberryAPIUtil
					.getObjectColumnName(
							moaTable,
							BurPaletteMaterialConstant.MOA_TRACK_PALATTE_MATERIAL_COLOR_ATT_MATERIAL_COLOR_ID);
			logger.debug(methodName
					+ "Track Palette Deletion >> Material Colour Column: "
					+ strMaterialColourColumn);
			// Get Palette Name Column
			String strPaletteNameColumn = BurberryAPIUtil
					.getObjectColumnName(
							moaTable,
							BurPaletteMaterialConstant.MOA_TRACK_PALATTE_MATERIAL_COLOR_ATT_PALETTE_NAME);
			logger.debug(methodName
					+ "Track Palette Deletion >> Palette Name Column: "
					+ strPaletteNameColumn);

			//Get Supplier ID Column
			String strSupplierIDColumn = BurberryAPIUtil
					.getObjectColumnName(
							moaTable,
							BurPaletteMaterialConstant.MOA_TRACK_SUPPLIER_NAME);
			logger.debug(methodName
					+ "Track Palette Deletion >> Supplier NAME Column: "
					+ strSupplierIDColumn);
			// Append Select Material Colour Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strMaterialColourColumn));
			// Append Select Palette Name Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strPaletteNameColumn));
			// Append Select Supplier ID Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strSupplierIDColumn));
		}// Track Image Page
		else if (moaTable.equalsIgnoreCase(BurProductConstant.BO_TRACK_SPECIFICATION_IMAGE_PAGE_NAME)) {
			// Get Sourcing Column
			String strSourcingColumnName = BurberryAPIUtil.getObjectColumnName(
					moaTable, BurProductConstant.MOA_TRACK_SOURCING_ID);
			logger.debug(methodName
					+ "Track Track Image Page >> Sourcing Column: "
					+ strSourcingColumnName);
			// Get Specification Column
			String strSpecificationColumnName = BurberryAPIUtil
					.getObjectColumnName(moaTable,
							BurProductConstant.MOA_TRACK_SPECIFICATION_ID);
			logger.debug(methodName
					+ "Track Track Image Page >> Specification Column: "
					+ strSpecificationColumnName);
			// Get Image Page Name Database Column Name
			String strImagePageNameColumnName = BurberryAPIUtil
					.getObjectColumnName(
							BurProductConstant.BO_TRACK_SPECIFICATION_IMAGE_PAGE_NAME,
							BurProductConstant.MOA_TRACK_IMAGE_PAGE_NAME);
			logger.debug(methodName
					+ "Track Track Image Page >> Image Page Name Column: "
					+ strImagePageNameColumnName);
			// Append Select Material Colour Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strSourcingColumnName));
			// Append Select Material Colour Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strSpecificationColumnName));
			// Append Select Material Colour Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strImagePageNameColumnName));
		}// Track Image From Image Page
		else if (moaTable.equalsIgnoreCase(BurProductConstant.BO_TRACK_IMAGE_FROM_IMAGE_PAGE_NAME)) {
			// Get Sourcing Column
			String strSourcingColumnName = BurberryAPIUtil.getObjectColumnName(
					moaTable, BurProductConstant.MOA_TRACK_SOURCING_ID);
			logger.debug(methodName
					+ "Track Track Image Page >> Sourcing Column: "
					+ strSourcingColumnName);
			// Get Specification Column
			String strSpecificationColumnName = BurberryAPIUtil
					.getObjectColumnName(moaTable,
							BurProductConstant.MOA_TRACK_SPECIFICATION_ID);
			logger.debug(methodName
					+ "Track Track Image Page >> Specification Column: "
					+ strSpecificationColumnName);
			// Get Image Page Id Database Column Name
			String strImagePageIdColumnName = BurberryAPIUtil
					.getObjectColumnName(
							BurProductConstant.BO_TRACK_IMAGE_FROM_IMAGE_PAGE_NAME,
							BurProductConstant.MOA_TRACK_IMAGE_PAGE_ID);
			logger.debug(methodName
					+ "Track Track Image Page >> Image Page Id Column: "
					+ strImagePageIdColumnName);
			// Get Image File Name Database Column Name
			String strFileUniqueIdColumnName = BurberryAPIUtil
					.getObjectColumnName(
							BurProductConstant.BO_TRACK_IMAGE_FROM_IMAGE_PAGE_NAME,
							BurProductConstant.MOA_TRACK_IMAGE_FILE_UNIQUE_ID);
			logger.debug(methodName
					+ "Track Track Image Page >> Image File Name Column: "
					+ strFileUniqueIdColumnName);
			// Append Select Sourcing Id Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strSourcingColumnName));
			// Append Select Specification Id Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strSpecificationColumnName));
			// Append Select Image Page Id Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strImagePageIdColumnName));
			// Append Select Image File Name Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strFileUniqueIdColumnName));
		}// Track Material Price Entry
		else if (moaTable.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_MATERIAL_PRICING_ENTRY_NAME)) {
			// Get Material Supplier Column
			String strMaterialSupplierColumn = BurberryAPIUtil
					.getObjectColumnName(
							moaTable,
							BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_SUPPLIER_ID);
			logger.debug(methodName
					+ "Track Material Price Entry >> Material Supplier Column: "
					+ strMaterialSupplierColumn);
			// Get the Owner Id Column Name
			String strObjectIdColumnName = BurberryAPIUtil.getObjectColumnName(
					moaTable, BurPaletteMaterialConstant.MOA_TRACK_OBJECT_ID);
			logger.debug(methodName + "MOA Object Id Column Name: "
					+ strObjectIdColumnName);
			// Append Select Material Colour Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strMaterialSupplierColumn));
			// Append Select Material Colour Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strObjectIdColumnName));
		}// Track Material Document
		else if (moaTable.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_MATERIAL_DOCUMENT_NAME)) {
			// Get the Owner Id Column Name
			String strMaterialIdColumnName = BurberryAPIUtil
					.getObjectColumnName(moaTable,
							BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_ID);
			logger.debug(methodName + "MOA Material Id Column Name: "
					+ strMaterialIdColumnName);
			// Get the Document Id Column Name
			String strDocumentIdColumnName = BurberryAPIUtil
					.getObjectColumnName(moaTable,
							BurPaletteMaterialConstant.MOA_TRACK_DOCUMENT_ID);
			logger.debug(methodName + "MOA Document Id Column Name: "
					+ strMaterialIdColumnName);
			// Append Select Material Colour Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strMaterialIdColumnName));
			// Append Select Material Colour Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strDocumentIdColumnName));
		} else {
			logger.info("Adding Other Columns for Other  deletions  !!!!!!!!");
			appendAdditionalSelectColumnsForOthers(moaTable, moaDeltaQueryStatment);
		}
		// Return
		return moaDeltaQueryStatment;
	}

	/**
	 * @param moaTable
	 * @param moaDeltaQueryStatment
	 * @throws WTException
	 */
	private static void appendAdditionalSelectColumnsForOthers(String moaTable,
			PreparedQueryStatement moaDeltaQueryStatment) throws WTException {

		String methodName = "appendAdditionalSelectColumnsForMOA() ";

		// Track BOM Deletions
		if (BurProductBOMConstant.BO_TRACK_BOM_NAME.equalsIgnoreCase(moaTable)) {
			// Get Sourcing Column
			String strSourcingColumnName = BurberryAPIUtil.getObjectColumnName(
					moaTable, BurProductConstant.MOA_TRACK_SOURCING_ID);
			logger.debug(methodName + "Track BOM >> Sourcing Column: "
					+ strSourcingColumnName);
			// Get Specification Column
			String strSpecificationColumnName = BurberryAPIUtil
					.getObjectColumnName(moaTable,
							BurProductConstant.MOA_TRACK_SPECIFICATION_ID);
			logger.debug(methodName + "Track BOM >> Specification Column: "
					+ strSpecificationColumnName);
			// BURBERRY-1420: BOM header Unique ID in CRUD Flag for BOM API Output - Start
			// Get BOM Part Id Column
			String strBOMPartIdColumnName = BurberryAPIUtil
					.getObjectColumnName(moaTable,
							BurProductBOMConstant.MOA_TRACK_BOM_PART_ID);
			logger.debug(methodName + "Track BOM >> BOM Part Id Column: "
					+ strBOMPartIdColumnName);
			// BURBERRY-1420: BOM header Unique ID in CRUD Flag for BOM API Output - End
			// Get Image Page Name Database Column Name
			String strBOMNameColumnName = BurberryAPIUtil.getObjectColumnName(
					moaTable, BurProductBOMConstant.MOA_TRACK_BOM_NAME);
			logger.debug(methodName + "Track BOM >> BOM Name Column: "
					+ strSpecificationColumnName);
			// Append Select Soucing Id Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strSourcingColumnName));
			// Append Select Specification Id Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strSpecificationColumnName));
			// BURBERRY-1420: BOM header Unique ID in CRUD Flag for BOM API Output - Start
			// Append Select BOM Part Id Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strBOMPartIdColumnName));
			// BURBERRY-1420: BOM header Unique ID in CRUD Flag for BOM API Output - End
			// Append Select Palette Name Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strBOMNameColumnName));
		} else if (BurProductConstant.BO_TRACK_SPECIFICATION_NAME
				.equalsIgnoreCase(moaTable)) {
			// Track Specification Deletions.
			// Get Sourcing ID Column
			String strSourcingColumnName = BurberryAPIUtil.getObjectColumnName(
					moaTable, BurProductConstant.MOA_TRACK_SOURCING_ID);
			logger.debug(methodName
					+ "Track Product Specification >> Sourcing ID Column: "
					+ strSourcingColumnName);
			// Get Season Column
			String strSeasonColumnName = BurberryAPIUtil.getObjectColumnName(
					moaTable, BurProductConstant.MOA_TRACK_SEASON_ID);
			logger.debug(methodName
					+ "Track Product Specification >> Season ID Column: "
					+ strSourcingColumnName);
			// Get Specification Name Column
			String strSpecNameColumnName = BurberryAPIUtil.getObjectColumnName(
					moaTable,
					BurProductConstant.MOA_TRACK_SPECIFICATION_SPEC_NAME);
			logger.debug(methodName
					+ "Track Product Specification >> Specification Name Column: "
					+ strSpecNameColumnName);
			///////////////////////  L2 Change  /////////////////////////////////////
			logger.info("#########  !!!!!!!!!!!!!!!!!!!!   ****************************");
			String strSpecificationIDColumnName = BurberryAPIUtil.getObjectColumnName(
					moaTable, BurProductConstant.MOA_TRACK_SPECIFICATION_ID);
			logger.debug(methodName
					+ "Track Product Specification >> Specification ID Column: "
					+ strSpecificationIDColumnName);

			//////////////////////   L2 Change  ///////////////////////////////////

			// Append Select Specification Name Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strSpecNameColumnName));
			// Append Select Soucing Id Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strSourcingColumnName));
			
			//////////////////  L2 Change ///////////////////////////////////////
			// Append Select Specification ID Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strSpecificationIDColumnName));
			///////////////////// L2 Change /////////////////////////////////////
			
			// Append Select Season Id Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strSeasonColumnName));
		}
		// BURBERRY-1485 RD 74: Material Supplier Documents - Start
		// Track Material Supplier Document
		else if (moaTable
				.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_MATERIAL_SUPPLIER_DOCUMENT_NAME)) {
			// Get Material Supplier Column
			String strMaterialSupplierColumn = BurberryAPIUtil
					.getObjectColumnName(
							moaTable,
							BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_SUPPLIER_ID);
			logger.debug(methodName
					+ "Track Material Supplier Documents >> Material Supplier Column: "
					+ strMaterialSupplierColumn);
			// Get the Document Id Column Name
			String strDocumentIdColumnName = BurberryAPIUtil
					.getObjectColumnName(moaTable,
							BurPaletteMaterialConstant.MOA_TRACK_DOCUMENT_ID);
			logger.debug(methodName + "MOA Document Id Column Name: "
					+ strDocumentIdColumnName);
			// Append Select Material Colour Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strMaterialSupplierColumn));
			// Append Select Material Colour Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strDocumentIdColumnName));
		}
		// BURBERRY-1485 RD 74: Material Supplier Documents - End
		else if (moaTable
				.equalsIgnoreCase(BurProductConstant.BO_TRACK_SPECIFICATION_DOCUMENT_NAME)) {
			// Get Spec Column
			String strSpecificationColumn = BurberryAPIUtil
					.getObjectColumnName(
							moaTable,
							BurProductConstant.MOA_TRACK_SPECIFICATION_ID);
			logger.debug(methodName
					+ "Track Specification Documents >> Specification Column: "
					+ strSpecificationColumn);
			// Get the Document Id Column Name
			String strDocumentIdColumnName = BurberryAPIUtil
					.getObjectColumnName(moaTable,
							BurPaletteMaterialConstant.MOA_TRACK_DOCUMENT_ID);
			logger.debug(methodName + "MOA Document Id Column Name: "
					+ strDocumentIdColumnName);
			// Append Select Material Colour Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strSpecificationColumn));
			// Append Select Material Colour Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strDocumentIdColumnName));
		}
		else {

			// Get the Owner Id Column Name
			String strOwnerIdColumnName = BurberryAPIUtil
					.getObjectColumnName(
							moaTable,
							BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_PRICE_MANAGEMENT_OWNER_ID);
			logger.debug(methodName + "MOA Owner Column Name: "
					+ strOwnerIdColumnName);
			// Get the Owner Id Column Name
			String strObjectIdColumnName = BurberryAPIUtil
					.getObjectColumnName(
							moaTable,
							BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_PRICE_MANAGEMENT_MOA_OBJECT_ID);
			logger.debug(methodName + "MOA Object Id Column Name: "
					+ strObjectIdColumnName);

			// Append Select Material Colour Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strOwnerIdColumnName));
			// Append Select Material Colour Column
			moaDeltaQueryStatment.appendSelectColumn(new QueryColumn(
					BurConstant.LCSMOAOBJECT, strObjectIdColumnName));
		}
	}

	/**
	 * Method to append Material-Supplier Table to Query Statement
	 * 
	 * @param matPriceManagementQueryStatement
	 *            PreparedQueryStatement
	 * @return PreparedQueryStatement
	 * @throws WTException
	 *             Exception
	 */
	private static PreparedQueryStatement appendMaterialSupplierJoinStatement(
			PreparedQueryStatement matPriceManagementQueryStatement,
			String strAttColumnName) throws WTException {

		// Append Additional Tables
		matPriceManagementQueryStatement
		.appendFromTable(LCSMaterialSupplier.class);
		matPriceManagementQueryStatement
		.appendFromTable(LCSMaterialSupplierMaster.class);

		// Append Join LCSMaterialSupplier and LCSMOAObject
		matPriceManagementQueryStatement.appendJoin(
				new QueryColumn(LCSMaterialSupplier.class.getSimpleName(),
						BurConstant.BRANCHID), new QueryColumn(
								BurConstant.LCSMOAOBJECT, strAttColumnName));

		// Append Join LCSMaterialSupplier and LCSMaterialSupplierMaster
		matPriceManagementQueryStatement.appendJoin(new QueryColumn(
				LCSMaterialSupplier.class, BurConstant.MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSMaterialSupplierMaster.class,
						BurConstant.OBJECTIDENTIFIERID));

		// Append Join LCSMaterialSupplierMaster and LCSMaterial
		matPriceManagementQueryStatement.appendJoin(new QueryColumn(
				LCSMaterialSupplierMaster.class,
				BurPaletteMaterialConstant.MATERIAL_MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSMaterial.class,
						BurConstant.MASTERREFERENCE_KEY_ID));

		// Append Material-Supplier Basic Criteria
		matPriceManagementQueryStatement.appendAndIfNeeded();
		matPriceManagementQueryStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSMaterialSupplier.class,
						BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));

		// Append criteria for latest iteration info
		matPriceManagementQueryStatement.appendAndIfNeeded();
		matPriceManagementQueryStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSMaterialSupplier.class,
						BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));

		// Append criteria for active
		matPriceManagementQueryStatement.appendAndIfNeeded();
		matPriceManagementQueryStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSMaterialSupplier.class, BurConstant.ACTIVE),
				STR_VAL_ONE, Criteria.EQUALS));

		// Append criteria for check placeholder
		matPriceManagementQueryStatement.appendAndIfNeeded();
		matPriceManagementQueryStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSMaterialSupplierMaster.class,
						BurPaletteMaterialConstant.PLACEHOLDER), STR_VAL_ZERO,
				Criteria.EQUALS));

		// Return Statement
		return matPriceManagementQueryStatement;
	}

	// CR R26: Handle Removed Object Delta Updates DB Query:
	// End
}