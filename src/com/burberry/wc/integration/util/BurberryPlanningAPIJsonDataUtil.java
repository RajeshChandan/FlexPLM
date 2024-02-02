package com.burberry.wc.integration.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.planningapi.bean.AssociatedSeason;
import com.burberry.wc.integration.planningapi.bean.PlanDetail;
import com.burberry.wc.integration.planningapi.bean.Planning;
import com.burberry.wc.integration.planningapi.constant.BurPlanningAPIConstant;
import com.lcs.wc.planning.FlexPlan;
import com.lcs.wc.planning.PlanLineItem;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.util.FormatHelper;

public final class BurberryPlanningAPIJsonDataUtil {

	/**
	 * BurberryPlanningAPIJsonDataUtil.
	 */
	private BurberryPlanningAPIJsonDataUtil() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPlanningAPIJsonDataUtil.class);

	/**
	 * @param planObj
	 * @return
	 * @throws IOException
	 * @throws WTException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static Planning getPlanningBean(FlexPlan planObj)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {

		String methodName = "getPlanningBean()";
		logger.debug(methodName + "Extracting data from Plan "
				+ planObj.getName());
		Planning planningBean = new Planning();

		// Generate json mapping from keys
		Map<String, String> jsonMappingPlan = BurberryAPIUtil
				.getJsonMapping(BurPlanningAPIConstant.JSON_PLAN_ATT);
		logger.debug(methodName + "jsonMappingPlan: " + jsonMappingPlan);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingPlan = BurberryAPIUtil
				.getJsonMapping(BurPlanningAPIConstant.SYSTEM_JSON_PLAN_ATT);
		logger.debug(methodName + "systemAttjsonMappingPlan: "
				+ systemAttjsonMappingPlan);

		BurberryAPIBeanUtil.getObjectData(
				BurPlanningAPIConstant.PLAN_ATT_IGNORE, planningBean, planObj,
				BurPlanningAPIConstant.PLAN_ATT, jsonMappingPlan,
				systemAttjsonMappingPlan);

		// set ida2a2 of plan
		if (systemAttjsonMappingPlan.containsKey(BurConstant.STR_IDA2A2)) {
			BeanUtils.setProperty(planningBean,
					systemAttjsonMappingPlan.get(BurConstant.STR_IDA2A2),
					planObj.getBranchIdentifier());

		}

		// get previous plan id
		if (systemAttjsonMappingPlan
				.containsKey(BurPlanningAPIConstant.PREV_IDA2A2)) {
			FlexPlan prevPlan = null;
			// check if vrd prev plan attributes exists on plan type
			if (planObj.getFlexType().attributeExist(
					BurPlanningAPIConstant.VRD_PREV_PLAN)) {
				prevPlan = (FlexPlan) planObj
						.getValue(BurPlanningAPIConstant.VRD_PREV_PLAN);

			} else {
				prevPlan = (FlexPlan) planObj
						.getValue(BurPlanningAPIConstant.BUR_PREV_PLAN);

			}
			// get prev plan id from prev plan object
			if (prevPlan != null) {
				BeanUtils.setProperty(planningBean, systemAttjsonMappingPlan
						.get(BurPlanningAPIConstant.PREV_IDA2A2), prevPlan
						.getBranchIdentifier());
			}

		}

		// Get previous plan value based on plan type
		if (jsonMappingPlan.containsKey(BurPlanningAPIConstant.VRD_PREV_PLAN)) {
			BeanUtils.setProperty(planningBean,
					jsonMappingPlan.get(BurPlanningAPIConstant.VRD_PREV_PLAN),
					getPrevPlanValue(planObj));

		}

		// Get previous plan value based on plan type
		if (jsonMappingPlan.containsKey(BurPlanningAPIConstant.BUR_PLAN_STATUS)) {
			BeanUtils
					.setProperty(planningBean, jsonMappingPlan
							.get(BurPlanningAPIConstant.BUR_PLAN_STATUS),
							getPlanStatusValue(planObj));

		}

		logger.debug(methodName + "planningBean: " + planningBean);
		// Return Statement
		return planningBean;
	}

	/**
	 * @param planObj
	 * @return
	 * @throws WTException 
	 */
	private static String getPlanStatusValue(FlexPlan planObj) throws WTException {
		String value = null;
		// Check if Plan is accessories
		if (planObj.getFlexType().getFullName(true)
				.contains(BurPlanningAPIConstant.ACCESSORIES_TYPE)) {
			value = BurberryDataUtil.getData(planObj,
					BurPlanningAPIConstant.BUR_PLAN_STATUS, null);
		} else {
			value = BurberryDataUtil.getData(planObj,
					BurPlanningAPIConstant.VRD_PLAN_STATUS, null);
		}
		// Return Statement
		return value;
	}

	/**
	 * @param planObj
	 * @return
	 * @throws WTException
	 */
	private static String getPrevPlanValue(FlexPlan planObj) throws WTException {
		String value = null;
		// Check if Plan is accessories
		if (planObj.getFlexType().getFullName(true)
				.contains(BurPlanningAPIConstant.ACCESSORIES_TYPE)) {
			value = BurberryDataUtil.getData(planObj,
					BurPlanningAPIConstant.BUR_PREV_PLAN, null);
		} else {
			value = BurberryDataUtil.getData(planObj,
					BurPlanningAPIConstant.VRD_PREV_PLAN, null);
		}
		// Return Statement
		return value;
	}

	/**
	 * @param planLineItem
	 * @return
	 * @throws IOException
	 * @throws WTException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static PlanDetail getPlanDetailBean(PlanLineItem planLineItem)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {

		String methodName = "getPlanDetailBean()";
		logger.debug(methodName + "Extracting data from Plan Line Item "
				+ planLineItem);
		PlanDetail planDetailBean = new PlanDetail();

		// Generate json mapping from keys
		Map<String, String> jsonMappingPlanDetail = BurberryAPIUtil
				.getJsonMapping(BurPlanningAPIConstant.JSON_PLAN_DETAIL_ATT);
		logger.debug(methodName + "jsonMappingPlanDetail: "
				+ jsonMappingPlanDetail);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingPlanDetail = BurberryAPIUtil
				.getJsonMapping(BurPlanningAPIConstant.SYSTEM_JSON_PLAN_DETAIL_ATT);
		logger.debug(methodName + "systemAttjsonMappingPlanDetail: "
				+ systemAttjsonMappingPlanDetail);

		BurberryAPIBeanUtil.getObjectData(
				BurPlanningAPIConstant.PLAN_DETAIL_ATT_IGNORE, planDetailBean,
				planLineItem, BurPlanningAPIConstant.PLAN_DETAIL_ATT,
				jsonMappingPlanDetail, systemAttjsonMappingPlanDetail);

		// Set branch id value
		if (systemAttjsonMappingPlanDetail
				.containsKey(BurPlanningAPIConstant.BRANCHID)) {
			BeanUtils.setProperty(planDetailBean,
					systemAttjsonMappingPlanDetail
							.get(BurPlanningAPIConstant.BRANCHID), planLineItem
							.getBranchId());

		}
		// Set sorting number value
		if (systemAttjsonMappingPlanDetail
				.containsKey(BurPlanningAPIConstant.SORTINGNUMBER)) {
			BeanUtils.setProperty(planDetailBean,
					systemAttjsonMappingPlanDetail
							.get(BurPlanningAPIConstant.SORTINGNUMBER),
					planLineItem.getSortingNumber());

		}
		// set parent id value
		if (systemAttjsonMappingPlanDetail
				.containsKey(BurPlanningAPIConstant.PARENTID)) {
			BeanUtils.setProperty(planDetailBean,
					systemAttjsonMappingPlanDetail
							.get(BurPlanningAPIConstant.PARENTID), planLineItem
							.getParentId());

		}
		// set ida2a2 value
		if (systemAttjsonMappingPlanDetail.containsKey(BurConstant.STR_IDA2A2)) {
			BeanUtils.setProperty(planDetailBean,
					systemAttjsonMappingPlanDetail.get(BurConstant.STR_IDA2A2),
					FormatHelper.getNumericObjectIdFromObject(planLineItem));

		}
		logger.debug(methodName + "planDetailBean: " + planDetailBean);
		// return plan detail bean
		return planDetailBean;
	}

	/**
	 * @param season
	 * @return
	 * @throws IOException
	 * @throws WTException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static AssociatedSeason getAssociatedSeasonBean(LCSSeason season)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		String methodName = "getAssociatedSeasonBean()";
		logger.debug(methodName + "Extracting data from Season "
				+ season.getName());
		AssociatedSeason asscoiatedSeason = new AssociatedSeason();

		// Generate json mapping from keys
		Map<String, String> jsonMappingPlanSeason = BurberryAPIUtil
				.getJsonMapping(BurPlanningAPIConstant.JSON_SEASON_ATT);
		logger.debug(methodName + "jsonMappingPlanSeason: "
				+ jsonMappingPlanSeason);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingPlanSeason = BurberryAPIUtil
				.getJsonMapping(BurPlanningAPIConstant.SYSTEM_JSON_SEASON_ATT);
		logger.debug(methodName + "systemAttjsonMappingPlanSeason: "
				+ systemAttjsonMappingPlanSeason);

		BurberryAPIBeanUtil.getObjectData(
				BurPlanningAPIConstant.SEASON_ATT_IGNORE, asscoiatedSeason,
				season, BurPlanningAPIConstant.SEASON_ATT,
				jsonMappingPlanSeason, systemAttjsonMappingPlanSeason);

		logger.debug(methodName + "asscoiatedSeason: " + asscoiatedSeason);
		// Return statement
		return asscoiatedSeason;
	}

}
