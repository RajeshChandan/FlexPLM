package com.burberry.wc.integration.planningapi.extraction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.*;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import wt.method.RemoteAccess;
import wt.org.WTPrincipal;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.exception.NoRecordFoundException;
import com.burberry.wc.integration.planningapi.bean.*;
import com.burberry.wc.integration.planningapi.constant.BurPlanningAPIConstant;
import com.burberry.wc.integration.planningapi.transform.BurberryPlanningAPIDataTransformHelper;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.planning.FlexPlan;
import com.lcs.wc.util.VersionHelper;

/**
 * A Helper class to handle Extraction activity. Class contain several method to
 * handle Extraction activity i.e. Extracting Data from different objects and
 * putting it to the bean.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryPlanningAPIDataExtractionHelper implements
		RemoteAccess {

	/**
	 * BurberryPlanningAPIDataExtractionHelper.
	 */
	private BurberryPlanningAPIDataExtractionHelper() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPlanningAPIDataExtractionHelper.class);

	/**
	 * apiType.
	 */
	private static final String apiType = "Planning";

	/**
	 * @param queryParams
	 * @return
	 * @throws BurException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws ParseException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public static Object getPlanningAPIData(
			MultivaluedMap<String, String> queryParams) throws BurException,
			IllegalAccessException, InvocationTargetException, ParseException,
			IOException, NoSuchMethodException, WTPropertyVetoException,
			WTException {

		// Configure Logger Properties
		BurberryLogFileGenerator.configurePlanningAPILog();
		boolean previousEnforcement = true;
		String methodName = "getPlanningAPIData() ";

		// Method Start Time
		long palMatStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Planning API Data Extraction Start Time: ");

		// Initialisation of response map to be be sent back for request
		Map<Status, Object> responseMap = new HashMap<Status, Object>();

		try {
			// Set Authenticated Principal
			WTPrincipal currentUsr = SessionHelper.manager.getPrincipal();
			SessionHelper.manager.setAuthenticatedPrincipal(currentUsr
					.getName());
			previousEnforcement = SessionServerHelper.manager
					.setAccessEnforced(false);

			// Initialisation of a new Plan Bean Object
			PlanningAPI planningAPIBean = new PlanningAPI();

			// Step 1: Get Valid Objects Map
			Map<String, String> mapValidObjects = BurberryAPIUtil
					.initializeValidObjects(BurPlanningAPIConstant.STR_PLANNING_VALID_OBJECTS);
			logger.debug(methodName + "mapValidObjects: " + mapValidObjects);

			// Step 2: Check Passed Parameters
			BurberryAPIUtil
					.verifyPassedParameters(queryParams, mapValidObjects);
			logger.debug(methodName + "queryParams: " + queryParams);
			
			// JIRA - BURBERRY-1363: START
			// Step 3: Based on the valid objects get query criteria
			List<Map> criteria = (List<Map>) BurberryAPIUtil.getCriteriaCollection(
					apiType, queryParams, mapValidObjects);
			logger.debug(methodName + "Planning Criteria: " + criteria);
			

			
			List<String> listofObjects = BurberryAPIUtil.getUniqueObjectIds(criteria,"FLEXPLAN.BRANCHIDITERATIONINFO");
			// JIRA - BURBERRY-1363: END
			// data
			List<Planning> lstPlanning =new ArrayList<Planning>();
			// Step 5: Pass the Plans collections and get transformed bean
			if (listofObjects != null && !listofObjects.isEmpty()) {
				lstPlanning= getTransformedPlanningData(listofObjects,
					criteria);
			}

			logger.debug(methodName + "Bean Plan List: " + lstPlanning);
			logger.info(methodName + "Number of Plans: " + lstPlanning.size());

			// Step 6: Set the list of all the bean data
			planningAPIBean.setPlanning(lstPlanning);

			// Step 7: Throw exception if matches no record fetched.
			BurberryAPIBeanUtil.sendNoRecordFoundException(planningAPIBean
					.getPlanning());

			// Step 8: Setting Response Map
			responseMap.put(Status.OK, planningAPIBean);

			// Method End Time
			long palMatEndTime = BurberryAPIUtil.printCurrentTime(methodName,
					"Planning API Data Extraction End Time: ");
			logger.info(methodName
					+ "Planning API  Total Execution Time (ms): "
					+ (palMatEndTime - palMatStartTime));

		} catch (final WTException e) {
			responseMap
					.put(Status.INTERNAL_SERVER_ERROR,
							BurberryAPIBeanUtil.getErrorResponseBean(
									e.getMessage(),
									Status.INTERNAL_SERVER_ERROR,
									queryParams,
									palMatStartTime,
									BurPlanningAPIConstant.PLANNING_API_LOG_ENTRY_FLEXTYPE,
									apiType));
			logger.error(BurPlanningAPIConstant.STR_ERROR_MSG_PLANNING_API, e);
		} catch (final ParseException e) {
			responseMap
					.put(Status.INTERNAL_SERVER_ERROR,
							BurberryAPIBeanUtil
									.getErrorResponseBean(
											BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_DATE,
											Status.INTERNAL_SERVER_ERROR,
											queryParams,
											palMatStartTime,
											BurPlanningAPIConstant.PLANNING_API_LOG_ENTRY_FLEXTYPE,
											apiType));
			logger.error(BurPlanningAPIConstant.STR_ERROR_MSG_PLANNING_API, e);
		} catch (final NoRecordFoundException e) {
			responseMap
					.put(Status.OK,
							BurberryAPIBeanUtil.getErrorResponseBean(
									e.getMessage(),
									Status.OK,
									queryParams,
									palMatStartTime,
									BurPlanningAPIConstant.PLANNING_API_LOG_ENTRY_FLEXTYPE,
									apiType));
			logger.error(BurPlanningAPIConstant.STR_ERROR_MSG_PLANNING_API, e);
		} catch (final BurException e) {
			responseMap
					.put(Status.BAD_REQUEST,
							BurberryAPIBeanUtil.getErrorResponseBean(
									e.getMessage(),
									Status.BAD_REQUEST,
									queryParams,
									palMatStartTime,
									BurPlanningAPIConstant.PLANNING_API_LOG_ENTRY_FLEXTYPE,
									apiType));
			logger.error(BurPlanningAPIConstant.STR_ERROR_MSG_PLANNING_API, e);
		} finally {
			// Restore access control enforcement.
			SessionServerHelper.manager.setAccessEnforced(previousEnforcement);

		}
		return responseMap;
	}

	/**
	 * @param listofObjects
	 * @param criteria
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws IOException
	 */
	private static List<Planning> getTransformedPlanningData(
			List<String> listofObjects, Collection<Map> criteria) throws WTException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
		ArrayList<Planning> planList = new ArrayList<Planning>();
		String methodName = "getTransformedPlanningData() ";

		// Track Start time
		long transProdCostingStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Planning - Transformation Start Time: ");
		
		logger.debug(methodName + "Plan Count: " + listofObjects.size());

		Map<String, List> mapFilterObject = BurberryAPIUtil.mapFilter(criteria);

		// Filter the map collection criteria and separate the below flex
		// object
		Collection<String> collPlanDetails = mapFilterObject
				.get("PLANLINEITEM.IDA2A2");
		logger.debug(methodName + "Collection of Plan to Plan Detail: "
				+ collPlanDetails);

		Collection<String> collPlanToSeasonIds = mapFilterObject
				.get("LCSSEASON.BRANCHIDITERATIONINFO");
		logger.debug(methodName + "Collection of Plan to Season: "
				+ collPlanToSeasonIds);

		// Loop through Plan Collection
		for (String plan : listofObjects) {
			FlexPlan planObj = null;
			planObj = getPlanObject(plan);
			logger.info(methodName + "Plan Object: " + planObj.getName());
			Planning planningBean = BurberryPlanningAPIDataTransformHelper
					.getPlanningBean(planObj, collPlanDetails,
							collPlanToSeasonIds);
			planList.add(planningBean);
			logger.debug(methodName + "Plan List Object : " + planList);
		}
		
		// Track execution time
		long transProdCostingEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Planning API - Transformation End Time: ");
		logger.info(methodName
				+ "Planning API - Transformation Total Execution Time (ms): "
				+ (transProdCostingEndTime - transProdCostingStartTime));
		return planList;
	}

	/**
	 * @param plan
	 * @return
	 * @throws WTException 
	 */
	private static FlexPlan getPlanObject(String plan) throws WTException {
		
		String methodName = "getPlanObject() ";
		// Initialisation of Plan object
		FlexPlan planObj = null;
		planObj = (FlexPlan) LCSQuery
				.findObjectById(BurPlanningAPIConstant.PLAN_ID+plan);
		planObj = ((FlexPlan) VersionHelper.latestIterationOf(planObj));
		logger.debug(methodName + "Plan Object: " + planObj);
		return planObj;
	}

}
