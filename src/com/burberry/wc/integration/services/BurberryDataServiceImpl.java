package com.burberry.wc.integration.services;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import wt.httpgw.GatewayAuthenticator;
import wt.method.*;
import wt.manager.RemoteServerManager;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.*;

import com.burberry.wc.integration.bean.ErrorMessage;
import com.burberry.wc.integration.exception.*;
import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.palettematerialapi.extraction.BurberryPaletteMaterialAPIDataExtractionHelper;
import com.burberry.wc.integration.productapi.extraction.BurberryProductAPIDataExtractionHelper;
import com.burberry.wc.integration.productbomapi.constant.BurProductBOMConstant;
import com.burberry.wc.integration.productbomapi.extraction.BurberryProductBOMAPIDataExtractionHelper;
import com.burberry.wc.integration.productcostingapi.extraction.BurberryProductCostingAPIDataExtractionHelper;
import com.burberry.wc.integration.productcostingapi.constant.BurProductCostingConstant;
import com.burberry.wc.integration.sampleapi.constant.BurSampleConstant;
import com.burberry.wc.integration.sampleapi.extraction.BurberrySampleAPIDataExtractionHelper;
import com.burberry.wc.integration.planningapi.constant.BurPlanningAPIConstant;
import com.burberry.wc.integration.planningapi.extraction.BurberryPlanningAPIDataExtractionHelper;
import com.burberry.wc.integration.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcs.wc.specification.FlexSpecUtil;
import com.lcs.wc.util.*;

/**
 * Provide implementation for defined service. Extract Product data and
 * generate. XML as output as per the T2/Pricing Schema.
 *
 * Extract Product data and generate XML as output as per the T1/Product Schema.
 *
 * 
 *
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */

public class BurberryDataServiceImpl implements BurberryDataService,
		RemoteAccess {

	/**
	 * sessionTrack.
	 */
	private BurberryAPISessionTrack sessionTrack = BurberryAPISessionTrack
			.getInstance();

	/**
	 * logger.
	 */
	private static final Logger logger = Logger.getLogger(BurberryDataServiceImpl.class);

	/**
	 * constructor to intiate logger
	 */
	public BurberryDataServiceImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.burberry.wc.integration.services.IBurberryDataService#getPricingData
	 * (java.lang.String)
	 */
	@Override
	public Response getPricingData(final String sDate, final String eDate,
			final String seasons) throws NoSuchFieldException,
			SecurityException, NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, IOException {

		String methodName = "getPricingData() ";
		BurberryLogFileGenerator.configureLog();
		logger.info(methodName + "Parameter passed : sDate =" + sDate
				+ "  eDate = " + eDate + "seasons = " + seasons);
		try {

			// Get BMS
			RemoteMethodServer backgroundMethodServer = getBackgroundMS(methodName);

			Class[] argObjects = { String.class, String.class, String.class }; // Mention
																				// Map.class
			// here
			final Object[] argValues = { sDate, eDate, seasons };

			Status pricingStatus = null;
			Object pricingObject = null;
			Map<Status, Object> serverResponseMap = (Map<Status, Object>) backgroundMethodServer
					.invoke("getPricing",
							BurberryPricingDataExtractionHelper.class.getName(),
							null, argObjects, argValues);
			for (Map.Entry<Status, Object> mapEntry : serverResponseMap
					.entrySet()) {
				pricingStatus = mapEntry.getKey();
				pricingObject = mapEntry.getValue();
				logger.info(methodName + " Pricing status " + pricingStatus
						+ "  Pricing object " + pricingObject);
			}
			return Response.status(pricingStatus).entity(pricingObject).build();
			/*
			 * catching all the exceptions such as authorization,nodata,invalid
			 * input and sending response as per error logging
			 */

		} catch (InvocationTargetException e) {
			return getErrorResponseBean(BurConstant.STR_ERROR_MSG_PRODUCT,
					e.getMessage(), Status.INTERNAL_SERVER_ERROR);
		} catch (RemoteException e) {
			return getErrorResponseBean(BurConstant.STR_ERROR_MSG_PRODUCT,
					e.getMessage(), Status.INTERNAL_SERVER_ERROR);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.burberry.wc.integration.services.IBurberryDataService#getProductData
	 * (java.lang.String)
	 */
	@Override
	public Response getProductData(final String sDate, final String eDate,
			final String seasons) throws IOException {

		String methodName = "getProductData() ";
		BurberryLogFileGenerator.configureLog();
		logger.info(methodName + "Parameter passed : sDate =" + sDate
				+ "  eDate = " + eDate + "seasons = " + seasons);
		// Method used to get Product Data from given inputs
		try {

			// Get BMS
			RemoteMethodServer backgroundMethodServer = getBackgroundMS(methodName);

			Class[] argObjects = { String.class, String.class, String.class }; // Mention
																				// Map.class
			// here
			final Object[] argValues = { sDate, eDate, seasons };

			Status productStatus = null;
			Object productObject = null;
			Map<Status, Object> serverResponseMap = (Map<Status, Object>) backgroundMethodServer
					.invoke("getProducts",
							BurberryProductDataExtractionHelper.class.getName(),
							null, argObjects, argValues);
			for (Map.Entry<Status, Object> mapEntry : serverResponseMap
					.entrySet()) {
				productStatus = mapEntry.getKey();
				productObject = mapEntry.getValue();
				logger.info(methodName + " Product status "
						+ productStatus + "  Product object "
						+ productObject);
			}
			return Response.status(productStatus).entity(productObject).build();
			/*
			 * catching all the exceptions such as authorization,nodata,invalid
			 * input and sending response as per error logging
			 */

		} catch (InvocationTargetException e) {
			return getErrorResponseBean(BurConstant.STR_ERROR_MSG_PRODUCT,
					e.getMessage(), Status.INTERNAL_SERVER_ERROR);
		} catch (RemoteException e) {
			return getErrorResponseBean(BurConstant.STR_ERROR_MSG_PRODUCT,
					e.getMessage(), Status.INTERNAL_SERVER_ERROR);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.burberry.wc.integration.services.IBurberryDataService#getProductAPIData
	 * (java.lang.String)
	 */

	@Override
	public Response getProductAPIData(UriInfo ui, HttpServletRequest request)
			throws NoSuchMethodException, NoSuchFieldException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, WTException,
			BurException, ParseException, IOException {
		// Method used to get Product Data from given inputs

		BurberryLogFileGenerator.configureProductAPILog();
		// Get Seesion ID
		String sessionID = request.getSession(true).getId();
		// API Type
		String apiTypePath = request.getPathInfo().replace("/", "")
				.toLowerCase();
		// Get Current request Thread.
		Thread t = Thread.currentThread();

		// BURBERRY-1372: Start
		WTUser productAPIWTUser = (WTUser) SessionHelper.getPrincipal();
		String productAPIUserId = productAPIWTUser.getAuthenticationName();
		// BURBERRY-1372: End

		try {
			// Method Name
			String methodName = "getProductAPIData() ";
			// Get BMS
			RemoteMethodServer backgroundMethodServer = getBackgroundMS(methodName);

			MultivaluedMap<String, String> queryParams = ui
					.getQueryParameters();
			logger.info(methodName + "Product API Parameter passed : "
					+ queryParams);

			Class[] argObjects = { MultivaluedMap.class }; // Mention Map.class
															// here
			final Object[] argValues = { queryParams };

			logger.info(methodName + "Product API argValues passed "
					+ Arrays.toString(argValues));
			/** Invoking rmsObj **/
			Status productStatus = null;
			Object productObject = null;
			// BURBERRY-1372: Start
			sessionTrack.addAPISessionThread(apiTypePath, sessionID, t);
			sessionTrack.setUserSessionMap(productAPIUserId, sessionID);
			// BURBERRY-1372: End
			Map<Status, Object> serverResponseMap = (Map<Status, Object>) backgroundMethodServer
					.invoke("getProductAPIData",
							BurberryProductAPIDataExtractionHelper.class
									.getName(), null, argObjects, argValues);

			logger.info(methodName
					+ " Product API server response map returned "
					+ serverResponseMap);

			ObjectMapper mapperObj = new ObjectMapper();
			String jsonStr = BurConstant.STRING_EMPTY;

			for (Map.Entry<Status, Object> mapEntry : serverResponseMap
					.entrySet()) {
				productStatus = mapEntry.getKey();
				productObject = mapEntry.getValue();
				logger.info(methodName + " Product API status "
						+ productStatus + "  Product API object "
						+ productObject);
				// jsonStr =
				// mapperObj.writerWithDefaultPrettyPrinter().writeValueAsString(object);
				jsonStr = mapperObj.writeValueAsString(productObject);

			}

			return Response.status(productStatus).entity(jsonStr).build();
			// return Response.status(status).entity(object).build();
			/*
			 * catching Invocation exception
			 */

		} catch (final InvocationTargetException e) {
			LCSLog.error(BurConstant.STR_ERROR_MSG_PRODUCT_API + e.toString());
			return getErrorResponseBean(BurConstant.STR_ERROR_MSG_PRODUCT_API,
					e.getMessage(), Status.UNAUTHORIZED);
		} catch (final WTRemoteException e) {
			// LCSLog.error(BurConstant.STR_ERROR_MSG_PRODUCT_API +
			// e.toString());
			return getErrorResponseBean(BurConstant.STR_ERROR_MSG_PRODUCT_API,
					String.valueOf(e.getNestedThrowable().getCause()),
					Status.GONE);
		} finally {
			sessionTrack.removedThreadForSessionAndAPI(sessionID, apiTypePath,
					t);
		}

	}

	@Override
	public Response getPaletteMaterialAPIData(UriInfo ui,
			HttpServletRequest request) throws NoSuchMethodException,
			NoSuchFieldException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, WTException,
			BurException, ParseException, IOException {
		// Method used to get Palette Material Data from given inputs

		BurberryLogFileGenerator.configurePaletteMaterialAPILog();
		// Get Seesion ID
		String sessionID = request.getSession(true).getId();
		// API Type
		String apiTypePath = request.getPathInfo().replace("/", "")
				.toLowerCase();
		// Get Current request Thread.
		Thread t = Thread.currentThread();
		// BURBERRY-1372: Start
		WTUser paletteMaterialAPIWTUser = (WTUser) SessionHelper.getPrincipal();
		String paletteMaterialAPIUserId = paletteMaterialAPIWTUser
				.getAuthenticationName();
		// BURBERRY-1372: End
		try {
			// Method Name
			String methodName = "getPaletteMaterialAPIData() ";
			MultivaluedMap<String, String> queryParams = ui
					.getQueryParameters();
			logger.info(methodName
					+ "Palette Material API Parameter passed : " + queryParams);

			// Get BMS
			RemoteMethodServer backgroundMethodServer = getBackgroundMS(methodName);

			Class[] argObjects = { MultivaluedMap.class }; // Mention
															// Map.class
															// here
			final Object[] argValues = { queryParams };

			logger.info(methodName + "Palette Material API argValues passed "
					+ Arrays.toString(argValues));
			Status materialStatus = null;
			Object materialObject = null;
			// BURBERRY-1372: Start
			sessionTrack.addAPISessionThread(apiTypePath, sessionID, t);
			sessionTrack.setUserSessionMap(paletteMaterialAPIUserId, sessionID);
			// BURBERRY-1372: End
			Map<Status, Object> serverResponseMap = (Map<Status, Object>) backgroundMethodServer
					.invoke("getPaletteMaterialAPIData",
							BurberryPaletteMaterialAPIDataExtractionHelper.class
									.getName(), null, argObjects, argValues);
			logger.info(methodName
					+ "Palette Material API server response map returned "
					+ serverResponseMap);

			ObjectMapper mapperObj = new ObjectMapper();
			String jsonStr = BurConstant.STRING_EMPTY;

			for (Map.Entry<Status, Object> mapEntry : serverResponseMap
					.entrySet()) {
				materialStatus = mapEntry.getKey();
				materialObject = mapEntry.getValue();
				logger.info(methodName + " Palette Material API status "
						+ materialStatus + " Palette Material API object "
						+ materialObject);
				// jsonStr =
				// mapperObj.writerWithDefaultPrettyPrinter().writeValueAsString(object);
				jsonStr = mapperObj.writeValueAsString(materialObject);

			}
			return Response.status(materialStatus).entity(jsonStr).build();
			// return Response.status(status).entity(object).build();

		} catch (final InvocationTargetException e) {
			LCSLog.error(BurPaletteMaterialConstant.STR_ERROR_MSG_PALATTE_MATERIAL_API
					+ e.toString());
			return getErrorResponseBean(
					BurPaletteMaterialConstant.STR_ERROR_MSG_PALATTE_MATERIAL_API,
					e.getMessage(), Status.UNAUTHORIZED);
		} catch (final WTRemoteException e) {
			// LCSLog.error(BurConstant.STR_ERROR_MSG_PRODUCT_API +
			// e.toString());
			return getErrorResponseBean(BurConstant.STR_ERROR_MSG_PRODUCT_API,
					String.valueOf(e.getNestedThrowable().getCause()),
					Status.GONE);
		} finally {
			sessionTrack.removedThreadForSessionAndAPI(sessionID, apiTypePath,
					t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.burberry.wc.integration.services.IBurberryDataService#getProductAPIData
	 * (java.lang.String)
	 */

	@Override
	public Response getProductBOMAPIData(UriInfo ui, HttpServletRequest request)
			throws NoSuchMethodException, NoSuchFieldException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, WTException,
			BurException, ParseException, IOException {
		// Method used to get Product BOM Data from given inputs
		BurberryLogFileGenerator.configureProductBOMAPILog();
		// Get Seesion ID
		String sessionID = request.getSession(true).getId();
		// API Type
		String apiTypePath = request.getPathInfo().replace("/", "")
				.toLowerCase();
		// Get Current request Thread.
		Thread t = Thread.currentThread();
		// BURBERRY-1372: Start
		WTUser productBOMWTUser = (WTUser) SessionHelper.getPrincipal();
		String productBOMUserId = productBOMWTUser.getAuthenticationName();
		// BURBERRY-1372: End

		try {
			// Method Name
			String methodName = "getProductBOMAPIData() ";
			// Get BMS
			RemoteMethodServer productBOMBackGroundMS = getBackgroundMS(methodName);
			MultivaluedMap<String, String> queryParams = ui
					.getQueryParameters();
			logger.info(methodName + "Product BOM Parameter passed : "
					+ queryParams);

			Class[] argObjects = { MultivaluedMap.class }; // Mention Map.class
															// here
			final Object[] argValues = { queryParams };

			logger.info(methodName + "Product BOM argValues passed "
					+ Arrays.toString(argValues));
			/** Invoking rmsObj **/
			Status productBOMStatus = null;
			Object productBOMObject = null;
			// BURBERRY-1372: Start
			sessionTrack.addAPISessionThread(apiTypePath, sessionID, t);
			sessionTrack.setUserSessionMap(productBOMUserId, sessionID);
			// BURBERRY-1372: End
			Map<Status, Object> serverResponseMap = (Map<Status, Object>) productBOMBackGroundMS
					.invoke("getProductBOMAPIData",
							BurberryProductBOMAPIDataExtractionHelper.class
									.getName(), null, argObjects, argValues);

			ObjectMapper mapperObj = new ObjectMapper();
			String jsonStr = BurConstant.STRING_EMPTY;
			logger.info(methodName
					+ "Product BOM server response map returned "
					+ serverResponseMap);
			for (Map.Entry<Status, Object> mapEntry : serverResponseMap
					.entrySet()) {
				productBOMStatus = mapEntry.getKey();
				productBOMObject = mapEntry.getValue();
				jsonStr = mapperObj.writeValueAsString(productBOMObject);
				logger.info(methodName + "Product BOM status "
						+ productBOMStatus + "Product BOM object "
						+ productBOMObject);
			}
			return Response.status(productBOMStatus).entity(jsonStr).build();
			// return Response.status(status).entity(object).build();
			/*
			 * catching Invocation exception
			 */
		} catch (final InvocationTargetException e) {
			return getErrorResponseBean(
					BurProductBOMConstant.STR_ERROR_MSG_PRODUCT_BOML_API,
					e.getMessage(), Status.UNAUTHORIZED);
		} catch (final WTRemoteException e) {
			// LCSLog.error(BurConstant.STR_ERROR_MSG_PRODUCT_API +
			// e.toString());
			return getErrorResponseBean(BurConstant.STR_ERROR_MSG_PRODUCT_API,
					String.valueOf(e.getNestedThrowable().getCause()),
					Status.GONE);
		} finally {
			sessionTrack.removedThreadForSessionAndAPI(sessionID, apiTypePath,
					t);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.burberry.wc.integration.services.IBurberryDataService#getSampleAPIData
	 * (java.lang.String)
	 */

	@Override
	public Response getSampleAPIData(UriInfo ui, HttpServletRequest request)
			throws NoSuchMethodException, NoSuchFieldException,
			SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, WTException,
			BurException, ParseException, IOException {
		// Method used to get Sample Request Data from given inputs
		BurberryLogFileGenerator.configureSampleAPILog();
		// Get Seesion ID
		String sessionID = request.getSession(true).getId();
		// API Type
		String apiTypePath = request.getPathInfo().replace("/", "")
				.toLowerCase();
		// Get Current request Thread.
		Thread t = Thread.currentThread();
		// BURBERRY-1372: Start
		WTUser sampleAPIWTUser = (WTUser) SessionHelper.getPrincipal();
		String sampleAPIUserId = sampleAPIWTUser.getAuthenticationName();
		// BURBERRY-1372: End

		try {
			// Method Name
			String methodName = "getSampleAPIData() ";
			MultivaluedMap<String, String> sampleQueryParams = ui
					.getQueryParameters();
			logger.info(methodName + "Sample API Parameter passed : "
					+ sampleQueryParams);

			// Get BMS
			RemoteMethodServer backgroundMethodServer = getBackgroundMS(methodName);

			Class[] argObjects = { MultivaluedMap.class }; // Mention
															// Map.class
															// here
			final Object[] argValues = { sampleQueryParams };

			logger.info(methodName + "Sample API argValues passed "
					+ Arrays.toString(argValues));
			Status sampleStatus = null;
			Object sampleObject = null;
			// BURBERRY-1372: Start
			sessionTrack.addAPISessionThread(apiTypePath, sessionID, t);
			sessionTrack.setUserSessionMap(sampleAPIUserId, sessionID);
			// BURBERRY-1372: End
			Map<Status, Object> serverResponseMap = (Map<Status, Object>) backgroundMethodServer
					.invoke("getSampleAPIData",
							BurberrySampleAPIDataExtractionHelper.class
									.getName(), null, argObjects, argValues);
			logger.info(methodName
					+ "Sample API server response map returned Sample API "
					+ serverResponseMap);

			ObjectMapper mapperObj = new ObjectMapper();
			String jsonStr = BurConstant.STRING_EMPTY;

			for (Map.Entry<Status, Object> mapEntry : serverResponseMap
					.entrySet()) {
				sampleStatus = mapEntry.getKey();
				sampleObject = mapEntry.getValue();
				logger.info(methodName + " Sample API status " + sampleStatus
						+ "Sample API object " + sampleObject);
				// jsonStr =
				// mapperObj.writerWithDefaultPrettyPrinter().writeValueAsString(sampleObject);
				jsonStr = mapperObj.writeValueAsString(sampleObject);

			}
			return Response.status(sampleStatus).entity(jsonStr).build();

		} catch (final InvocationTargetException e) {
			LCSLog.error(BurSampleConstant.STR_SAMPLE_API_ERROR_MSG
					+ e.toString());
			return getErrorResponseBean(
					BurSampleConstant.STR_SAMPLE_API_ERROR_MSG, e.getMessage(),
					Status.UNAUTHORIZED);
		} catch (final WTRemoteException e) {
			// LCSLog.error(BurConstant.STR_ERROR_MSG_PRODUCT_API +
			// e.toString());
			return getErrorResponseBean(BurConstant.STR_ERROR_MSG_PRODUCT_API,
					String.valueOf(e.getNestedThrowable().getCause()),
					Status.GONE);
		} finally {
			sessionTrack.removedThreadForSessionAndAPI(sessionID, apiTypePath,
					t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.burberry.wc.integration.services.IBurberryDataService#getSampleAPIData
	 * (java.lang.String)
	 */

	@Override
	public Response getProductCostingAPIData(UriInfo ui,
			HttpServletRequest request) throws NoSuchFieldException,
			SecurityException, NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, WTException,
			BurException, ParseException, IOException {
		// Method used to get Product Costing Data from given inputs
		BurberryLogFileGenerator.configureProductCostingAPILog();
		// Get Seesion ID
		String sessionID = request.getSession(true).getId();
		// API Type
		String apiTypePath = request.getPathInfo().replace("/", "")
				.toLowerCase();
		// Get Current request Thread.
		Thread t = Thread.currentThread();
		// BURBERRY-1372: Start
		WTUser productCostingWTUser = (WTUser) SessionHelper.getPrincipal();
		String productCostingUser = productCostingWTUser
				.getAuthenticationName();
		// BURBERRY-1372: End

		try {
			// Method Name
			String methodName = "getProductCostingAPIData() ";

			MultivaluedMap<String, String> queryParameters = ui
					.getQueryParameters();
			logger.info(methodName + "Product Costing API Parameter passed : "
					+ queryParameters);

			// Get Background Method Server
			RemoteMethodServer backgroundMethodServerObject = getBackgroundMS(methodName);

			Class[] argumentObjects = { MultivaluedMap.class }; // Mention
			// Map.class
			// here
			final Object[] argumentValues = { queryParameters };

			logger.info(methodName + "Product Costing API argValues passed "
					+ Arrays.toString(argumentValues));
			Status costingStatus = null;
			Object costingObject = null;
			// BURBERRY-1372: Start
			sessionTrack.addAPISessionThread(apiTypePath, sessionID, t);
			sessionTrack.setUserSessionMap(productCostingUser, sessionID);
			// BURBERRY-1372: End
			Map<Status, Object> serverResponseMap = (Map<Status, Object>) backgroundMethodServerObject
					.invoke("getProductCostingAPIData",
							BurberryProductCostingAPIDataExtractionHelper.class
									.getName(), null, argumentObjects,
							argumentValues);
			logger.info(methodName
					+ " Product Costing API server response map returned "
					+ serverResponseMap);

			ObjectMapper mapperObj = new ObjectMapper();
			String jsonStr = BurConstant.STRING_EMPTY;

			for (Map.Entry<Status, Object> mapEntry : serverResponseMap
					.entrySet()) {
				costingStatus = mapEntry.getKey();
				costingObject = mapEntry.getValue();
				logger.info(methodName + " Product Costing API status "
						+ costingStatus + " Product Costing API object "
						+ costingObject);
				// jsonStr = mapperObj.writerWithDefaultPrettyPrinter()
				// .writeValueAsString(costingObject);
				jsonStr = mapperObj.writeValueAsString(costingObject);

			}
			return Response.status(costingStatus).entity(jsonStr).build();
			// return Response.status(status).entity(object).build();

		} catch (final InvocationTargetException e) {
			LCSLog.error(BurProductCostingConstant.STR_PRODUCT_COSTING_API_ERROR_MSG
					+ e.toString());
			return getErrorResponseBean(
					BurProductCostingConstant.STR_PRODUCT_COSTING_API_ERROR_MSG,
					e.getMessage(), Status.UNAUTHORIZED);
		} catch (final WTRemoteException e) {
			// LCSLog.error(BurConstant.STR_ERROR_MSG_PRODUCT_API +
			// e.toString());
			return getErrorResponseBean(BurConstant.STR_ERROR_MSG_PRODUCT_API,
					String.valueOf(e.getNestedThrowable().getCause()),
					Status.GONE);
		} finally {
			sessionTrack.removedThreadForSessionAndAPI(sessionID, apiTypePath,
					t);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.burberry.wc.integration.services.IBurberryDataService#getPlanningAPIData
	 * (java.lang.String)
	 */
	@Override
	public Response getPlanningAPIData(UriInfo ui, HttpServletRequest request)
			throws NoSuchFieldException, SecurityException,
			NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, WTException,
			BurException, ParseException, IOException {
		// Method used to get planning Data from given inputs
		BurberryLogFileGenerator.configurePlanningAPILog();
		// Get Seesion ID
		String sessionID = request.getSession(true).getId();
		// API Type
		String apiTypePath = request.getPathInfo().replace("/", "")
				.toLowerCase();
		// Get Current request Thread.
		Thread t = Thread.currentThread();
		// BURBERRY-1372: Start
		WTUser planningAPIWTUser = (WTUser) SessionHelper.getPrincipal();
		String planningAPIUserId = planningAPIWTUser.getAuthenticationName();
		// BURBERRY-1372: End
		try {
			// Method Name
			String methodName = "getPlanningAPIData() ";
			MultivaluedMap<String, String> planQueryParams = ui
					.getQueryParameters();
			logger.info(methodName + "Planning API Parameter passed : "
					+ planQueryParams);

			// Get BMS
			RemoteMethodServer backgroundMethodServer = getBackgroundMS(methodName);
			Class[] argObjects = { MultivaluedMap.class }; // Mention
															// Map.class
															// here
			final Object[] planArgValues = { planQueryParams };
			// Print Arg Values
			logger.info(methodName + "Planning API API argValues passed "
					+ Arrays.toString(planArgValues));
			Status planStatus = null;
			Object planObject = null;
			// BURBERRY-1372: Start
			sessionTrack.addAPISessionThread(apiTypePath, sessionID, t);
			sessionTrack.setUserSessionMap(planningAPIUserId, sessionID);
			// BURBERRY-1372: End
			// Get Response Map from Extraction Helper
			Map<Status, Object> planningServerResponseMap = (Map<Status, Object>) backgroundMethodServer
					.invoke("getPlanningAPIData",
							BurberryPlanningAPIDataExtractionHelper.class
									.getName(), null, argObjects, planArgValues);
			logger.info(methodName
					+ "Planning API server response map returned "
					+ planningServerResponseMap);
			ObjectMapper planMapperObj = new ObjectMapper();
			String planJsonStr = BurConstant.STRING_EMPTY;
			// Build JSON Response from Response Map
			for (Map.Entry<Status, Object> mapEntry : planningServerResponseMap
					.entrySet()) {
				planStatus = mapEntry.getKey();
				planObject = mapEntry.getValue();
				logger.info(methodName + " Planning API status " + planStatus
						+ " Planning API object " + planObject);
				planJsonStr = planMapperObj.writeValueAsString(planObject);

			}
			return Response.status(planStatus).entity(planJsonStr).build();

		} catch (final InvocationTargetException e) {
			LCSLog.error(BurPlanningAPIConstant.STR_ERROR_MSG_PLANNING_API
					+ e.toString());
			return getErrorResponseBean(
					BurPlanningAPIConstant.STR_ERROR_MSG_PLANNING_API,
					e.getMessage(), Status.UNAUTHORIZED);
		} catch (final WTRemoteException e) {
			// LCSLog.error(BurConstant.STR_ERROR_MSG_PRODUCT_API +
			// e.toString());
			return getErrorResponseBean(BurConstant.STR_ERROR_MSG_PRODUCT_API,
					String.valueOf(e.getNestedThrowable().getCause()),
					Status.GONE);
		} finally {
			sessionTrack.removedThreadForSessionAndAPI(sessionID, apiTypePath,
					t);
		}
	}

	/**
	 * Return response in case of exception. This method should be replaced with
	 * ExceptionMapper. ExceptionMapper help to standardised.
	 * 
	 * Use ErrorMessage bean to create and return Response. Error message will
	 * be passed from actual exception.
	 * 
	 * @param errorMsg
	 * @return Response
	 */
	public Response getErrorResponseBean(final String reportingAPI,
			final String errorMsg, final Status unauthorized) {

		final ErrorMessage errorMessage = new ErrorMessage(reportingAPI,
				unauthorized.getStatusCode(), errorMsg);
		return Response.status(unauthorized).entity(errorMessage).build();
	}

	/**
	 * @param methodName
	 *            methodName
	 * @return RemoteMethodServer
	 * @throws IOException
	 *             IOException
	 */
	private RemoteMethodServer getBackgroundMS(String methodName)
			throws IOException {

		RemoteMethodServer backgroundMethodServer = null;
		String codebase = WTProperties.getServerProperties().getProperty(
				"wt.server.codebase");
		String backgroundMethodServerName = LCSProperties
				.get("com.burberry.wc.integration.productapi.backgroundmsname");
		WTProperties wtProperties = WTProperties.getLocalProperties();
		String backgroundServerPh = wtProperties
				.getProperty("wt.rmi.server.hostname");
		String services = wtProperties
				.getProperty("wt.manager.monitor.services");

		Boolean bmsConfigured = false;

		if (FormatHelper.hasContent(backgroundServerPh)
				&& FormatHelper.hasContent(backgroundMethodServerName)) {
			String numServices = wtProperties
					.getProperty("wt.manager.monitor.start."
							+ backgroundMethodServerName);
			if (services.contains(backgroundMethodServerName)
					&& FormatHelper.hasContent(numServices)) {
				bmsConfigured = true;
			}

		}
		// using background method server if available
		if (bmsConfigured) {
			URL url = new URL(codebase + "/");
			logger.info(methodName + " url " + url);
			RemoteServerManager serverManager = RemoteServerManager
					.getDefault();
			if (!serverManager.getAllServers(backgroundMethodServerName)
					.isEmpty()) {
				backgroundMethodServer = RemoteMethodServer.getInstance(url,
						backgroundMethodServerName);
				logger.info(methodName + " background method server "
						+ backgroundMethodServer);
			}
		}
		if (backgroundMethodServer == null) {
			backgroundMethodServer = RemoteMethodServer.getDefault();
			logger.info(methodName + " default method server selected "
					+ backgroundMethodServer);
		}
		
		//Burberry 1273 - Analyze & solve weird MethodResultException exception Fix
		GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser(LCSProperties.get("com.burberry.integration.backgroundmethodserver.username"));
        backgroundMethodServer.setAuthenticator(auth);
		return backgroundMethodServer;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.burberry.wc.integration.services.BurberryDataService
	 * #getCancelReqest(javax.ws.rs.core.UriInfo,
	 * javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public Response getCancelReqest(UriInfo ui, HttpServletRequest request) {
		if (ui.getQueryParameters().get("api") == null) {
			return getErrorResponseBean("",
					" Please provide a URI with 'api'query String. "
							+ "eg: /cancelRequest?api=productAPI ",
					Status.BAD_REQUEST);
		}

		String apiType = ui.getQueryParameters().get("api").get(0);
		String sessionID = request.getSession(true).getId();
		// BURBERRY-1372: Start
		String userID = null;
		if (ui.getQueryParameters().get("user") != null) {
			userID = ui.getQueryParameters().get("user").get(0);
		}
		logger.info(sessionID + " API Name: " + apiType);
		if (!FormatHelper.hasContent(userID)
				&& !sessionTrack.interruptAPIExecution(sessionID, apiType)) {
			return getErrorResponseBean(apiType,
					" There was No running API Request to terminate",
					Status.GONE);
		} else if (FormatHelper.hasContent(userID)
				&& !sessionTrack.interruptAPIExecutionByUser(userID, apiType)) {
			logger.info(sessionID + " User Id: " + userID);
			return getErrorResponseBean(apiType,
					" There was No running API Request to terminate for this user, "
							+ userID, Status.GONE);
		}
		// BURBERRY-1372: End
		return Response
				.status(Status.OK)
				.entity(apiType
						+ " - API Request was terminated/interrupted successfully ")
				.build();
	}

}
