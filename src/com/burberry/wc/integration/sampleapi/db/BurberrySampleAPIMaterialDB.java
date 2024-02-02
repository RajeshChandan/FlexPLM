package com.burberry.wc.integration.sampleapi.db;

import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.sampleapi.constant.BurSampleConstant;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.*;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleRequest;
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

public final class BurberrySampleAPIMaterialDB {

	/**
	 * Default Constructor.
	 */
	private BurberrySampleAPIMaterialDB() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberrySampleAPIMaterialDB.class);

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
	 * Method to generate prepared query statement for Material Sample Request.
	 * 
	 * @param colMatSampReqCriteria
	 *            Criteria collection
	 * @return Collection Map
	 * @throws WTException
	 *             Exception
	 */

	public static Collection<Map> getMaterialSampleRequest(
			Collection<Map> colMatSampReqCriteria) throws WTException {

		// Set Method Name
		String methodName = "getMaterialSampleRequest() ";

		// Track Start Time for Query Execution
		long matSampleReqStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "matSampleReqStartTime: ");

		// Initialisation
		Collection<Map> resultsMatSampleReq = new ArrayList<Map>();
		logger.debug(methodName + "colMatSampReqCriteria: "
				+ colMatSampReqCriteria);
		if (colMatSampReqCriteria.isEmpty()) {
			return resultsMatSampleReq;
		}

		// Create a new prepared query statement
		PreparedQueryStatement materialSampleReqStatement = new PreparedQueryStatement();

		// Append Distinct
		materialSampleReqStatement.setDistinct(true);

		// Append Table Name
		materialSampleReqStatement.appendFromTable(LCSSampleRequest.class);

		// Append Select Columns
		materialSampleReqStatement.appendSelectColumn(new QueryColumn(
				LCSSampleRequest.class, BurConstant.OBJECTIDENTIFIERID));

		// Append Search Criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				materialSampleReqStatement, colMatSampReqCriteria);

		// Append Material flex type criteria
		BurberrySampleAPIDBHelper
				.appendFlexTypeIdPathCriteriaStatement(
						materialSampleReqStatement, LCSSampleRequest.class,
						false, true);

		logger.info(methodName
				+ "Material Sample Request Prepared Query Statement: "
				+ materialSampleReqStatement.toString());

		// Execute the query
		resultsMatSampleReq = LCSQuery.runDirectQuery(
				materialSampleReqStatement).getResults();
		logger.debug(methodName + "resultsMatSampleReq: " + resultsMatSampleReq);

		// Track End Time for Query Execution
		long matSampleReqEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"matSampleEndTime: ");
		logger.info(methodName
				+ "Material Sample Request Query Total Execution Time (ms): "
				+ (matSampleReqEndTime - matSampleReqStartTime));

		// Return Statement
		return (Collection<Map>) resultsMatSampleReq;
	}

	/**
	 * Method to generate prepared query statement for material sample.
	 * 
	 * @param colMatSampCriteria
	 *            Collection
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */

	public static Collection<Map> getMaterialSample(
			Collection<Map> colMatSampCriteria) throws WTException {

		// Set Method Name
		String methodName = "getMaterialSample() ";

		// Track Start Time for Query Execution
		long matSampleStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"matSampleStartTime: ");
		// Initialisation
		Collection<Map> resultsMatSample = new ArrayList<Map>();
		logger.debug(methodName + "colMatSampCriteria: " + colMatSampCriteria);
		if (colMatSampCriteria.isEmpty()) {
			return resultsMatSample;
		}

		// Create a new prepared query statement
		PreparedQueryStatement materialSampleStatement = new PreparedQueryStatement();

		// Append Distinct
		materialSampleStatement.setDistinct(true);

		// Append Table
		materialSampleStatement.appendFromTable(LCSSample.class);
		materialSampleStatement.appendFromTable(LCSSampleRequest.class);

		// Append Select Columns
		materialSampleStatement.appendSelectColumn(new QueryColumn(
				LCSSampleRequest.class, BurConstant.OBJECTIDENTIFIERID));
		materialSampleStatement.appendSelectColumn(new QueryColumn(
				LCSSample.class, BurConstant.OBJECTIDENTIFIERID));

		// Append Search criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				materialSampleStatement, colMatSampCriteria);

		// Append material flex type criteria
		BurberrySampleAPIDBHelper.appendFlexTypeIdPathCriteriaStatement(
				materialSampleStatement, LCSSampleRequest.class, false, true);

		// Append Join between Sample and Sample Request
		materialSampleStatement.appendJoin(new QueryColumn(LCSSample.class,
				BurSampleConstant.SAMPLE_REQUESTREFERENCE_KEY_ID),
				new QueryColumn(LCSSampleRequest.class,
						BurConstant.OBJECTIDENTIFIERID));

		logger.info(methodName + "Material Sample Prepared Query Statement: "
				+ materialSampleStatement.toString());

		// Execute the query
		resultsMatSample = LCSQuery.runDirectQuery(materialSampleStatement)
				.getResults();
		logger.debug(methodName + "resultsMatSample: " + resultsMatSample);

		// Track End Time for Query Execution
		long matSampleEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"matSampleEndTime: ");
		logger.info(methodName
				+ "Material Sample Query Total Execution Time (ms): "
				+ (matSampleEndTime - matSampleStartTime));

		// Return Statement
		return (Collection<Map>) resultsMatSample;

	}

	/**
	 * Method to generate prepared query statement to get sample request from
	 * Source.
	 * 
	 * @param sourcingConfigQueryCriteria
	 *            Collection
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */

	public static Collection<Map> getAssociatedSampleRequestFromSourcingConfig(
			Collection<Map> sourcingConfigQueryCriteria) throws WTException {
		// Set Method Name
		String methodName = "getAssociatedSampleRequestforSourcingConfig() ";

		// Track Start Time for Query Execution
		long sampleSourConfigStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampleSourConfigStartTime: ");
		// Initialisation
		Collection<Map> resultsSampleSourceConfig = new ArrayList<Map>();
		logger.debug(methodName + "sourcingConfigQueryCriteria: "
				+ sourcingConfigQueryCriteria);
		if (sourcingConfigQueryCriteria.isEmpty()) {
			return resultsSampleSourceConfig;
		}

		// Create a new prepared query statement
		PreparedQueryStatement sampleReqFromSourceStatment = new PreparedQueryStatement();
		// Append Distinct
		sampleReqFromSourceStatment.setDistinct(true);

		// Append Table Name
		sampleReqFromSourceStatment.appendFromTable(LCSSample.class);
		sampleReqFromSourceStatment.appendFromTable(LCSSampleRequest.class);
		sampleReqFromSourceStatment.appendFromTable(LCSSourcingConfig.class);
		

		// Append Select Columns
		sampleReqFromSourceStatment.appendSelectColumn(new QueryColumn(
				LCSSampleRequest.class, BurConstant.OBJECTIDENTIFIERID));
		sampleReqFromSourceStatment.appendSelectColumn(new QueryColumn(
				BurConstant.LCSSOURCINGCONFIG, BurConstant.BRANCHID));

		// Append Join between Sample and Sample Request
		sampleReqFromSourceStatment.appendJoin(new QueryColumn(LCSSample.class,
				BurSampleConstant.SAMPLE_REQUESTREFERENCE_KEY_ID),
				new QueryColumn(LCSSampleRequest.class,
						BurConstant.OBJECTIDENTIFIERID));

		// Append Join between Sample Request and Sourcing Config
		sampleReqFromSourceStatment.appendJoin(new QueryColumn(
				LCSSampleRequest.class,
				BurSampleConstant.SOURCING_MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSSourcingConfig.class,
						BurConstant.MASTERREFERENCE_KEY_ID));

		// Append Basic Criteria
		sampleReqFromSourceStatment.appendAndIfNeeded();
		sampleReqFromSourceStatment.appendCriteria(new Criteria(
				new QueryColumn(LCSSourcingConfig.class,
						BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));
		sampleReqFromSourceStatment.appendAnd();
		sampleReqFromSourceStatment.appendCriteria(new Criteria(
				new QueryColumn(LCSSourcingConfig.class,
						BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));

		// Append Search Criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				sampleReqFromSourceStatment, sourcingConfigQueryCriteria);

		logger.info(methodName
				+ "Sample Request Prepared Query Statement for Associated Sourcing Config: "
				+ sampleReqFromSourceStatment.toString());

		// Execute the query
		resultsSampleSourceConfig = LCSQuery.runDirectQuery(
				sampleReqFromSourceStatment).getResults();
		logger.debug(methodName + "resultsSampleSourceConfig: "
				+ resultsSampleSourceConfig);

		// Track End Time for Query Execution
		long sampleSourceConfigEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampleSourceConfigEndTime: ");
		logger.info(methodName
				+ "Sample Request for Associated Sourcing Config Query Total Execution Time (ms): "
				+ (sampleSourceConfigEndTime - sampleSourConfigStartTime));

		// Return Statement
		return (Collection<Map>) resultsSampleSourceConfig;
	}

	/**
	 * Method to generate prepared query statement to get sample request from
	 * Material.
	 * 
	 * @param materialQueryCriteria
	 *            Collection
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */

	public static Collection<Map> getAssociatedSampleRequestFromMaterial(
			Collection<Map> materialQueryCriteria) throws WTException {

		// Set Method Name
		String methodName = "getAssociatedSampleRequestforMaterials() ";

		// Track Start Time for Query Execution
		long sampleForMatStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampleForMatStartTime: ");

		// Initialisation
		Collection<Map> resultsSampforMaterials = new ArrayList<Map>();
		logger.debug(methodName + "materialQueryCriteria: "
				+ materialQueryCriteria);
		if (materialQueryCriteria.isEmpty()) {
			return resultsSampforMaterials;
		}

		// Create a new prepared query statement
		PreparedQueryStatement sampleReqFromMaterialStatement = new PreparedQueryStatement();
		// Append Distinct
		sampleReqFromMaterialStatement.setDistinct(true);

		// Append Table Name
		sampleReqFromMaterialStatement.appendFromTable(LCSSample.class);
		sampleReqFromMaterialStatement.appendFromTable(LCSSampleRequest.class);
		sampleReqFromMaterialStatement.appendFromTable(LCSMaterial.class);
		

		// Append Select Columns
		sampleReqFromMaterialStatement.appendSelectColumn(new QueryColumn(
				LCSSampleRequest.class, BurConstant.OBJECTIDENTIFIERID));

		// Append Join between Sample and Sample Request
		sampleReqFromMaterialStatement.appendJoin(new QueryColumn(
				LCSSample.class,
				BurSampleConstant.SAMPLE_REQUESTREFERENCE_KEY_ID),
				new QueryColumn(LCSSampleRequest.class,
						BurConstant.OBJECTIDENTIFIERID));

		// Append Join Sample Request and Material
		sampleReqFromMaterialStatement.appendJoin(new QueryColumn(
				LCSMaterial.class, BurConstant.MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSSampleRequest.class,
						BurSampleConstant.OWNER_MASTERREFERENCE_KEY_ID));

		// Append Basic Criteria
		sampleReqFromMaterialStatement.appendAndIfNeeded();
		sampleReqFromMaterialStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSMaterial.class,
						BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		sampleReqFromMaterialStatement.appendAnd();
		sampleReqFromMaterialStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSMaterial.class,
						BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));
		sampleReqFromMaterialStatement.appendAnd();
		sampleReqFromMaterialStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSMaterial.class,
						BurConstant.VERSIONIDA2VERSIONINFO), STR_VAL_A,
				Criteria.EQUALS));

		// Append Search criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				sampleReqFromMaterialStatement, materialQueryCriteria);

		logger.info(methodName
				+ "Sample Request Prepared Query Statement for Material: "
				+ sampleReqFromMaterialStatement.toString());

		// Execute the query
		resultsSampforMaterials = LCSQuery.runDirectQuery(
				sampleReqFromMaterialStatement).getResults();
		logger.debug(methodName + "resultsProdSamp: " + resultsSampforMaterials);

		// Track End Time for Query Execution
		long sampleForMatEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"sampleForMatEndTime: ");
		logger.info(methodName
				+ "Sample Request Query for Material Total Execution Time (ms): "
				+ (sampleForMatEndTime - sampleForMatStartTime));

		// Return Statement
		return (Collection<Map>) resultsSampforMaterials;
	}

	/**
	 * Method to generate prepared query statement to get sample request from
	 * material supplier.
	 * 
	 * @param materialSupplierQueryCriteria
	 *            Collection
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */

	public static Collection<Map> getAssociatedSampleRequestFromMaterialSupplier(
			Collection<Map> materialSupplierQueryCriteria) throws WTException {
		// Set Method Name
		String methodName = "getAssociatedSampleRequestFromMaterialSupplier() ";

		// Track Start Time for Query Execution
		long sampleForMatSupplStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampleForMatSupplStartTime: ");

		// Initialisation
		Collection<Map> resultsSampReqforMatSupp = new ArrayList<Map>();
		logger.debug(methodName + "materialSupplierQueryCriteria: "
				+ materialSupplierQueryCriteria);
		if (materialSupplierQueryCriteria.isEmpty()) {
			return resultsSampReqforMatSupp;
		}

		// Create a new prepared query statement
		PreparedQueryStatement sampleReqFromMatSuppStatement = new PreparedQueryStatement();
		// Append distinct
		sampleReqFromMatSuppStatement.setDistinct(true);

		// Append Table
		sampleReqFromMatSuppStatement.appendFromTable(LCSSample.class);
		sampleReqFromMatSuppStatement.appendFromTable(LCSSampleRequest.class);
		sampleReqFromMatSuppStatement
				.appendFromTable(LCSMaterialSupplier.class);

		// Append Select columns
		sampleReqFromMatSuppStatement.appendSelectColumn(new QueryColumn(
				LCSSampleRequest.class, BurConstant.OBJECTIDENTIFIERID));
		sampleReqFromMatSuppStatement
				.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class
						.getSimpleName(), BurConstant.BRANCHID));

		// Append Join between Sample and Sample Request
		sampleReqFromMatSuppStatement.appendJoin(new QueryColumn(
				LCSSample.class,
				BurSampleConstant.SAMPLE_REQUESTREFERENCE_KEY_ID),
				new QueryColumn(LCSSampleRequest.class,
						BurConstant.OBJECTIDENTIFIERID));

		// Append Join between Sample and Material Supplier
		sampleReqFromMatSuppStatement.appendJoin(new QueryColumn(
				LCSSample.class,
				BurSampleConstant.SOURCING_MASTERREFERENCE_KEY_ID),
				new QueryColumn(LCSMaterialSupplier.class,
						BurConstant.MASTERREFERENCE_KEY_ID));

		// Append Material-Supplier Basic Criteria
		sampleReqFromMatSuppStatement.appendAndIfNeeded();
		sampleReqFromMatSuppStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSMaterialSupplier.class,
						BurConstant.CHECK_OUT_INFO_STATE), STR_WRK,
				Criteria.NOT_EQUAL_TO));
		sampleReqFromMatSuppStatement.appendAndIfNeeded();
		sampleReqFromMatSuppStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSMaterialSupplier.class,
						BurConstant.LATESTITERATIONINFO), STR_VAL_ONE,
				Criteria.EQUALS));
		sampleReqFromMatSuppStatement.appendAndIfNeeded();
		sampleReqFromMatSuppStatement.appendCriteria(new Criteria(
				new QueryColumn(LCSMaterialSupplier.class, BurConstant.ACTIVE),
				STR_VAL_ONE, Criteria.EQUALS));

		// Append Search criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				sampleReqFromMatSuppStatement, materialSupplierQueryCriteria);

		logger.info(methodName
				+ "Sample Request Prepared Query Statement for Material Supplier: "
				+ sampleReqFromMatSuppStatement.toString());

		// Execute the query
		resultsSampReqforMatSupp = LCSQuery.runDirectQuery(
				sampleReqFromMatSuppStatement).getResults();
		logger.debug(methodName + "resultsSampReqforMatSupp: "
				+ resultsSampReqforMatSupp);

		// Track End Time for Query Execution
		long sampReqMatSuppEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampReqMatSuppEndTime: ");
		logger.info(methodName
				+ "Sample Request Query for Material Supplier Total Execution Time (ms): "
				+ (sampReqMatSuppEndTime - sampleForMatSupplStartTime));

		// Return Statement
		return (Collection<Map>) resultsSampReqforMatSupp;
	}

	/**
	 * Method to generate prepared query statement to get sample request from
	 * material colour.
	 * 
	 * @param materialColourQueryCriteria
	 *            Collection
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */

	public static Collection<Map> getAssociatedSampleRequestFromMaterialColour(
			Collection<Map> materialColourQueryCriteria) throws WTException {

		// Set Method Name
		String methodName = "getAssociatedSampleRequestFromMaterialColour() ";

		// Track Start Time for Query Execution
		long sampleForMatColourStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampleForMatColourStartTime: ");

		// Initialisation
		Collection<Map> resultsSampReqforMatColour = new ArrayList<Map>();
		logger.debug(methodName + "materialSupplierQueryCriteria: "
				+ materialColourQueryCriteria);
		if (materialColourQueryCriteria.isEmpty()) {
			return resultsSampReqforMatColour;
		}

		// Create a new prepared query statement
		PreparedQueryStatement sampleReqFromMatColorStatement = new PreparedQueryStatement();
		// Append Distinct
		sampleReqFromMatColorStatement.setDistinct(true);

		// Append Table Name
		sampleReqFromMatColorStatement.appendFromTable(LCSSample.class);
		sampleReqFromMatColorStatement.appendFromTable(LCSSampleRequest.class);
		sampleReqFromMatColorStatement.appendFromTable(LCSMaterialColor.class);

		// Append Select Columns
		sampleReqFromMatColorStatement.appendSelectColumn(new QueryColumn(
				LCSSampleRequest.class, BurConstant.OBJECTIDENTIFIERID));
		sampleReqFromMatColorStatement.appendSelectColumn(new QueryColumn(
				LCSMaterialColor.class, BurConstant.OBJECTIDENTIFIERID));

		// Append Join between Sample and Sample Request
		sampleReqFromMatColorStatement.appendJoin(new QueryColumn(
				LCSSample.class,
				BurSampleConstant.SAMPLE_REQUESTREFERENCE_KEY_ID),
				new QueryColumn(LCSSampleRequest.class,
						BurConstant.OBJECTIDENTIFIERID));

		// Append Join between Sample and Material Colour
		sampleReqFromMatColorStatement.appendJoin(new QueryColumn(
				LCSSample.class, BurSampleConstant.COLOR_REFERENCE_KEY_ID),
				new QueryColumn(LCSMaterialColor.class,
						BurConstant.OBJECTIDENTIFIERID));

		// Append Search criteria
		BurberryAPIDBUtil.appendRequestCriteriaToStatement(
				sampleReqFromMatColorStatement, materialColourQueryCriteria);

		logger.info(methodName
				+ "Sample Request Prepared Query Statement for Material Colour: "
				+ sampleReqFromMatColorStatement.toString());

		// Execute the query
		resultsSampReqforMatColour = LCSQuery.runDirectQuery(
				sampleReqFromMatColorStatement).getResults();
		logger.debug(methodName + "resultsSampReqforMatColour: "
				+ resultsSampReqforMatColour);

		// Track End Time for Query Execution
		long sampReqMatColourEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "sampReqMatColourEndTime: ");
		logger.info(methodName
				+ "Sample Request Query for Material Colour Total Execution Time (ms): "
				+ (sampReqMatColourEndTime - sampleForMatColourStartTime));

		// Return Statement
		return (Collection<Map>) resultsSampReqforMatColour;
	}

}
