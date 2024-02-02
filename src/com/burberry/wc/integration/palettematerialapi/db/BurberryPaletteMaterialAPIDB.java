package com.burberry.wc.integration.palettematerialapi.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.palettematerialapi.constant.*;
import com.burberry.wc.integration.util.BurConstant;
import com.burberry.wc.integration.util.BurberryAPIDBUtil;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.color.LCSPalette;
import com.lcs.wc.color.LCSPaletteMaterialLink;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierMaster;
import com.lcs.wc.supplier.LCSSupplier;

/**
 * A DB class to handle Database activity. Class contain several method to
 * handle DB activity i.e. creating DB query, statement, and result set
 * allocation. Collection of Criteria Map, Start Date, End Date and Modify are
 * Primary input to fetch details from Flex system.
 *
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryPaletteMaterialAPIDB {

	/**
	 * Default Constructor.
	 */
	private BurberryPaletteMaterialAPIDB() {

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
	 * STR_VAL_ZERO.
	 */
	private static final String STR_VAL_ZERO = "0";

	/**
	 * STR_WRK.
	 */
	private static final String STR_WRK = "wrk";

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPaletteMaterialAPIDB.class);

	/**
	 * Method to generate prepared query statement for Material.
	 * 
	 * @param colMaterialCriteria
	 *            Criteria collection
	 * @return Collection Map
	 * @throws WTException
	 *             Exception
	 */
	public static Collection<Map> getAssociatedMaterials(
			Collection<Map> colMaterialCriteria) throws WTException {
		// Set Method Name
		String methodName = "getAssociatedMaterials() ";
		// Track Start Time for Query Execution
		long matStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"matStartTime: ");
		// Initialisation
		Collection<Map> resultMaterials = new ArrayList<Map>();
		logger.debug(methodName + "colMaterialCriteria: " + colMaterialCriteria);
		if (colMaterialCriteria.isEmpty()) {
			return resultMaterials;
		}

		// Create a new prepared query statement
		PreparedQueryStatement materialStatement = new PreparedQueryStatement();
		// Append distinct
		materialStatement.setDistinct(true);
		// Append Table
		materialStatement.appendFromTable(LCSMaterial.class);
		// Append Select columns
		materialStatement.appendSelectColumn(new QueryColumn(LCSMaterial.class,
				BurConstant.OBJECTIDENTIFIERID));
		materialStatement.appendSelectColumn(new QueryColumn(LCSMaterial.class
				.getSimpleName(), BurConstant.BRANCHID));

		// Append Criteria
		materialStatement.appendAndIfNeeded();
		materialStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		materialStatement.appendAnd();
		materialStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		materialStatement.appendAnd();
		materialStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(materialStatement,
				colMaterialCriteria);

		logger.info(methodName + "Material Prepared Query Statement: "
				+ materialStatement.toString());

		// Execute the query
		resultMaterials = LCSQuery.runDirectQuery(materialStatement)
				.getResults();
		logger.debug(methodName + "resultMaterials: " + resultMaterials);

		// Track End Time for Query Execution
		long matEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"matEndTime: ");
		logger.info(methodName + "Material Query Total Execution Time (ms): "
				+ (matEndTime - matStartTime));

		// Return Statement
		return (Collection<Map>) resultMaterials;
	}

	/**
	 * Method to generate prepared query statement for Material-Supplier.
	 * 
	 * @param colMaterialSupplierCriteria
	 *            Collection
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */
	public static Collection<Map> getAssociatedMaterialsFromMaterialSupplier(
			Collection<Map> colMaterialSupplierCriteria) throws WTException {
		// Set Method Name
		String methodName = "getAssociatedMaterialsFromMaterialSupplier() ";
		// Track Start Time for Query Execution
		long matMatSupStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"matMatSupStartTime: ");

		// Initialisation
		Collection<Map> resultMaterialSupplier = new ArrayList<Map>();
		logger.debug(methodName + "colMaterialSupplierCriteria: "
				+ colMaterialSupplierCriteria);
		// Check if query criteria collection is empty
		if (colMaterialSupplierCriteria.isEmpty()) {
			return resultMaterialSupplier;
		}

		// Create a new prepared query statement
		PreparedQueryStatement matSupStatement = new PreparedQueryStatement();
		// Append distinct
		matSupStatement.setDistinct(true);
		// Append table
		matSupStatement.appendFromTable(LCSMaterial.class);
		matSupStatement.appendFromTable(LCSMaterialSupplier.class);
		matSupStatement.appendFromTable(LCSMaterialSupplierMaster.class);

		// Append select column
		matSupStatement.appendSelectColumn(new QueryColumn(LCSMaterial.class
				.getSimpleName(), BurConstant.BRANCHID));
		matSupStatement
				.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class
						.getSimpleName(), BurConstant.BRANCHID));

		// Append Joins
		matSupStatement.appendJoin(new QueryColumn(LCSMaterialSupplier.class,
				BurConstant.MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSMaterialSupplierMaster.class,
						BurConstant.OBJECTIDENTIFIERID));
		matSupStatement.appendJoin(new QueryColumn(
				LCSMaterialSupplierMaster.class,
				BurPaletteMaterialConstant.MATERIAL_MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSMaterial.class,
						BurConstant.MASTERREFERENCE_KEY_ID));

		// Append Material Basic Criteria
		matSupStatement.appendAndIfNeeded();
		matSupStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		matSupStatement.appendAndIfNeeded();
		matSupStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		matSupStatement.appendAnd();
		matSupStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		// Append Material-Supplier Basic Criteria
		matSupStatement.appendAndIfNeeded();
		matSupStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterialSupplier.class, BurConstant.CHECK_OUT_INFO_STATE),
				STR_WRK, Criteria.NOT_EQUAL_TO));
		matSupStatement.appendAndIfNeeded();
		matSupStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterialSupplier.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		matSupStatement.appendAndIfNeeded();
		matSupStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterialSupplier.class, BurConstant.ACTIVE), STR_VAL_ONE,
				Criteria.EQUALS));
		matSupStatement.appendAndIfNeeded();
		matSupStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterialSupplierMaster.class,
				BurPaletteMaterialConstant.PLACEHOLDER), STR_VAL_ZERO,
				Criteria.EQUALS));

		// Append Request Criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(matSupStatement,
				colMaterialSupplierCriteria);

		logger.info(methodName + "Material-Supplier Prepared Query Statement: "
				+ matSupStatement.toString());

		// Execute the query
		resultMaterialSupplier = LCSQuery.runDirectQuery(matSupStatement)
				.getResults();
		logger.debug(methodName + "resultMaterialSupplier: "
				+ resultMaterialSupplier);

		// Track Time for Query Execution
		long matMatSupEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"matMatSupEndTime: ");
		logger.info(methodName + "Material Sup Total Execution Time (ms): "
				+ (matMatSupEndTime - matMatSupStartTime));

		// Return Statement
		return (Collection<Map>) resultMaterialSupplier;
	}

	/**
	 * Method to generate prepared query statement for Supplier.
	 * 
	 * @param supplierQueryCriteria
	 * @return
	 * @throws WTException
	 */
	public static Collection<Map> getAssociatedMaterialsFromSupplier(
			Collection<Map> supplierQueryCriteria) throws WTException {
		// Set Method Name
		String methodName = "getAssociatedMaterialsFromSupplier() ";
		// Track Start Time for Query Execution
		long msStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"msStartTime: ");

		// Initialisation
		Collection<Map> resultSupplier = new ArrayList<Map>();
		logger.debug(methodName + "supplierQueryCriteria: "
				+ supplierQueryCriteria);
		if (supplierQueryCriteria.isEmpty()) {
			return resultSupplier;
		}

		// Create a new prepared query statement
		PreparedQueryStatement supStatement = new PreparedQueryStatement();
		// Append distinct
		supStatement.setDistinct(true);
		// Append table
		supStatement.appendFromTable(LCSMaterial.class);
		supStatement.appendFromTable(LCSSupplier.class);
		supStatement.appendFromTable(LCSMaterialSupplier.class);
		supStatement.appendFromTable(LCSMaterialSupplierMaster.class);

		// Append select column
		supStatement.appendSelectColumn(new QueryColumn(LCSMaterial.class
				.getSimpleName(), BurConstant.BRANCHID));
		supStatement
				.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class
						.getSimpleName(), BurConstant.BRANCHID));

		// Append JOINS
		supStatement.appendJoin(new QueryColumn(LCSMaterialSupplier.class,
				BurConstant.MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSMaterialSupplierMaster.class,
						BurConstant.OBJECTIDENTIFIERID));
		supStatement.appendJoin(new QueryColumn(
				LCSMaterialSupplierMaster.class,
				BurPaletteMaterialConstant.SUPPLIER_MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSSupplier.class,
						BurConstant.MASTERREFERENCE_KEY_ID));
		supStatement.appendJoin(new QueryColumn(
				LCSMaterialSupplierMaster.class,
				BurPaletteMaterialConstant.MATERIAL_MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSMaterial.class,
						BurConstant.MASTERREFERENCE_KEY_ID));

		// Append Material Basic criteria
		supStatement.appendAndIfNeeded();
		supStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		supStatement.appendAndIfNeeded();
		supStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		supStatement.appendAnd();
		supStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		// Append Supplier Basic criteria
		supStatement.appendAndIfNeeded();
		supStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSupplier.class, BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		supStatement.appendAndIfNeeded();
		supStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSSupplier.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));

		// Append Material Supplier Basic criteria
		supStatement.appendAndIfNeeded();
		supStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterialSupplier.class, BurConstant.CHECK_OUT_INFO_STATE),
				STR_WRK, Criteria.NOT_EQUAL_TO));
		supStatement.appendAndIfNeeded();
		supStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterialSupplier.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		supStatement.appendAndIfNeeded();
		supStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterialSupplier.class, BurConstant.ACTIVE), STR_VAL_ONE,
				Criteria.EQUALS));
		supStatement.appendAndIfNeeded();
		supStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterialSupplierMaster.class,
				BurPaletteMaterialConstant.PLACEHOLDER), STR_VAL_ZERO,
				Criteria.EQUALS));

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(supStatement,
				supplierQueryCriteria);

		logger.info(methodName + "Supplier Prepared Query Statement: "
				+ supStatement.toString());

		// Execute the query
		resultSupplier = LCSQuery.runDirectQuery(supStatement).getResults();
		logger.debug(methodName + "resultSupplier: " + resultSupplier);

		// Track End Time for Query Execution
		long msEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"msEndTime: ");
		logger.info(methodName + "Mat Suppl Total Execution Time (ms): "
				+ (msEndTime - msStartTime));
		// Return Statement
		return (Collection<Map>) resultSupplier;
	}

	/**
	 * Method to generate prepared query statement for Material-Colour.
	 * 
	 * @param materialColourQueryCriteria
	 * @return
	 * @throws WTException
	 */
	public static Collection<Map> getAssociatedMaterialsFromMaterialColour(
			Collection<Map> materialColourQueryCriteria) throws WTException {
		// Set Method Name
		String methodName = "getAssociatedMaterialsFromMaterialColour() ";
		// Track Start Time for Query Execution
		long matColStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"matColStartTime: ");

		// Initialisation
		Collection<Map> resultMaterialColours = new ArrayList<Map>();
		logger.debug(methodName + "materialColourQueryCriteria: "
				+ materialColourQueryCriteria);
		if (materialColourQueryCriteria.isEmpty()) {
			return resultMaterialColours;
		}

		// Create a new prepared query statement
		PreparedQueryStatement matColourStatement = new PreparedQueryStatement();
		// Append distinct
		matColourStatement.setDistinct(true);

		// Append table
		matColourStatement.appendFromTable(LCSMaterial.class);
		matColourStatement.appendFromTable(LCSMaterialColor.class);

		// Append select column
		matColourStatement.appendSelectColumn(new QueryColumn(LCSMaterial.class
				.getSimpleName(), BurConstant.BRANCHID));
		matColourStatement.appendSelectColumn(new QueryColumn(
				LCSMaterialColor.class, BurConstant.OBJECTIDENTIFIERID));

		// Append Join
		matColourStatement.appendJoin(new QueryColumn(LCSMaterialColor.class,
				BurPaletteMaterialConstant.MATERIAL_MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSMaterial.class,
						BurConstant.MASTERREFERENCE_KEY_ID));

		// Append Basic Criteria
		matColourStatement.appendAndIfNeeded();
		matColourStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		matColourStatement.appendAndIfNeeded();
		matColourStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		matColourStatement.appendAnd();
		matColourStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(matColourStatement,
				materialColourQueryCriteria);

		logger.info(methodName + "Material Colour Prepared Query Statement: "
				+ matColourStatement.toString());

		// Execute the query
		resultMaterialColours = LCSQuery.runDirectQuery(matColourStatement)
				.getResults();
		logger.debug(methodName + "resultOfMaterialColours: "
				+ resultMaterialColours);

		// Track End Time for Query Execution
		long matColEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"matColEndTime: ");
		logger.info(methodName + "Material Colour Total Execution Time (ms): "
				+ (matColEndTime - matColStartTime));

		// Return Statement
		return (Collection<Map>) resultMaterialColours;
	}

	/**
	 * Method to generate prepared query statement for Colour.
	 * 
	 * @param colourQueryCriteria
	 * @return
	 * @throws WTException
	 */
	public static Collection<Map> getAssociatedMaterialsFromColour(
			Collection<Map> colourQueryCriteria) throws WTException {
		// Set Method Name
		String methodName = "getAssociatedMaterialsFromColour() ";

		// Track Start Time for Query Execution
		long colStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"colStartTime: ");

		// Initialisation
		Collection<Map> resultColours = new ArrayList<Map>();
		logger.debug(methodName + "colourQueryCriteria: " + colourQueryCriteria);
		if (colourQueryCriteria.isEmpty()) {
			return resultColours;
		}
		// Create a new prepared query statement
		PreparedQueryStatement colourStatement = new PreparedQueryStatement();
		// Append distinct
		colourStatement.setDistinct(true);

		// Append table
		colourStatement.appendFromTable(LCSMaterial.class);
		colourStatement.appendFromTable(LCSColor.class);
		colourStatement.appendFromTable(LCSMaterialColor.class);

		// Append select column
		colourStatement.appendSelectColumn(new QueryColumn(LCSMaterial.class
				.getSimpleName(), BurConstant.BRANCHID));
		colourStatement.appendSelectColumn(new QueryColumn(
				LCSMaterialColor.class, BurConstant.OBJECTIDENTIFIERID));

		// Append Join
		colourStatement.appendJoin(new QueryColumn(LCSMaterialColor.class,
				BurPaletteMaterialConstant.MATERIAL_MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSMaterial.class,
						BurConstant.MASTERREFERENCE_KEY_ID));
		colourStatement
				.appendJoin(new QueryColumn(LCSMaterialColor.class,
						BurPaletteMaterialConstant.COLOUR_REFERENCE_KEY_ID),
						new QueryColumn(LCSColor.class,
								BurConstant.OBJECTIDENTIFIERID));

		// Append Basic Criteria
		colourStatement.appendAndIfNeeded();
		colourStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		colourStatement.appendAndIfNeeded();
		colourStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		colourStatement.appendAnd();
		colourStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(colourStatement,
				colourQueryCriteria);

		logger.info(methodName + "Colour Prepared Query Statement: "
				+ colourStatement.toString());

		// Execute the query
		resultColours = LCSQuery.runDirectQuery(colourStatement).getResults();
		logger.debug(methodName + "resultColours: " + resultColours);

		// Track End Time for Query Execution
		long colEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"colEndTime: ");
		logger.info(methodName + "Colour Total Execution Time (ms): "
				+ (colEndTime - colStartTime));

		// Return Statement
		return (Collection<Map>) resultColours;
	}

	/**
	 * Method to generate prepared query statement for Palette.
	 * 
	 * @param paletteQueryCriteria
	 * @return
	 * @throws WTException
	 */
	public static Collection<Map> getAssociatedMaterialsFromPalette(
			Collection<Map> paletteQueryCriteria) throws WTException {
		// Set Method Name
		String methodName = "getAssociatedMaterialsFromPalette() ";
		// Track Start Time and End Time for Query Execution
		long palMatStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"palMatStartTime: ");

		// Initialisation
		Collection<Map> resultPalette = new ArrayList<Map>();
		logger.debug(methodName + "paletteQueryCriteria: "
				+ paletteQueryCriteria);
		if (paletteQueryCriteria.isEmpty()) {
			return resultPalette;
		}

		// Create a new prepared query statement
		PreparedQueryStatement palStatement = new PreparedQueryStatement();
		// Append distinct
		palStatement.setDistinct(true);

		// Append table
		palStatement.appendFromTable(LCSMaterial.class);
		palStatement.appendFromTable(LCSPalette.class);
		palStatement.appendFromTable(LCSPaletteMaterialLink.class);

		// Append select column
		palStatement.appendSelectColumn(new QueryColumn(LCSMaterial.class
				.getSimpleName(), BurConstant.BRANCHID));
		// palStatement.appendSelectColumn(new QueryColumn(
		// LCSPaletteMaterialLink.class, BurConstant.OBJECTIDENTIFIERID));
		palStatement.appendSelectColumn(new QueryColumn(LCSPalette.class,
				BurConstant.OBJECTIDENTIFIERID));

		// Append JOINS
		palStatement.appendJoin(new QueryColumn(LCSMaterial.class,
				BurConstant.MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSPaletteMaterialLink.class,
						BurConstant.ROLEAOBJECTREF_KEY_ID));
		palStatement.appendJoin(new QueryColumn(LCSPalette.class,
				BurConstant.OBJECTIDENTIFIERID),
				new QueryColumn(LCSPaletteMaterialLink.class,
						BurConstant.ROLEBOBJECTREF_KEY_ID));

		// Append Basic Criteria
		palStatement.appendAndIfNeeded();
		palStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		palStatement.appendAndIfNeeded();
		palStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.LATESTITERATIONINFO),
				STR_VAL_ONE, Criteria.EQUALS));
		palStatement.appendAnd();
		palStatement.appendCriteria(new Criteria(new QueryColumn(
				LCSMaterial.class, BurConstant.VERSIONIDA2VERSIONINFO),
				STR_VAL_A, Criteria.EQUALS));

		// Append additional criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(palStatement,
				paletteQueryCriteria);

		logger.info(methodName + "Palette Prepared Query Statement: "
				+ palStatement.toString());

		// Execute the query
		resultPalette = LCSQuery.runDirectQuery(palStatement).getResults();
		logger.debug(methodName + "resultPalette: " + resultPalette);

		// Track End Time for Query Execution
		long patMatEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"patMatEndTime: ");
		logger.info(methodName + "Palette Material Total Execution Time (ms): "
				+ (patMatEndTime - palMatStartTime));

		// Return Statement
		return (Collection<Map>) resultPalette;
	}

}
