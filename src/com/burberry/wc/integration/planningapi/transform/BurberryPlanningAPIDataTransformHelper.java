package com.burberry.wc.integration.planningapi.transform;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.planningapi.bean.AssociatedSeason;
import com.burberry.wc.integration.planningapi.bean.PlanDetail;
import com.burberry.wc.integration.planningapi.bean.Planning;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.burberry.wc.integration.util.BurberryPlanningAPIJsonDataUtil;
import com.lcs.wc.planning.FlexPlan;

public final class BurberryPlanningAPIDataTransformHelper {

	/**
	 * BurberryPlanningAPIDataTransformHelper.
	 */
	private BurberryPlanningAPIDataTransformHelper() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPlanningAPIDataTransformHelper.class);
	/**
	 * @param planObj
	 * @param collPlanDetails
	 * @param collPlanToSeasonIds
	 * @return
	 * @throws IOException 
	 * @throws WTException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public static Planning getPlanningBean(FlexPlan planObj,
			Collection<String> collPlanDetails,
			Collection<String> collPlanToSeasonIds) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, WTException, IOException {
		String methodName = "getPlanningBean() ";
		long planningstartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"planningstartTime: ");

		logger.debug(methodName + " planObj : " + planObj.getName());

		// Extracting Plan Data
		Planning planningBean = BurberryPlanningAPIJsonDataUtil
				.getPlanningBean(planObj);
		// Extracting Plan Detail List data
		List<PlanDetail> lstPlanDetails = BurberryPlanningAPIDataTransform
				.getListPlanDetailBean(planObj, collPlanDetails);
		
		logger.debug(methodName + "List of Plan Detail Beans: " + lstPlanDetails);
		// Extracting Source List data
		List<AssociatedSeason> lstSeason = BurberryPlanningAPIDataTransform
						.getListAssociatedSeasonBean(planObj, collPlanToSeasonIds);
		
		logger.debug(methodName + "List of Season Beans: " + lstSeason);
		planningBean.setPlanDetails(lstPlanDetails);
		planningBean.setAssociatedSeasons(lstSeason);
		logger.debug(methodName + " Planning Bean " + planningBean);

		long planningEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"lstPlanDetails: ");
		logger.debug(methodName + "Plan Transform  Total Execution Time (ms): "
				+ (planningEndTime - planningstartTime));
		return planningBean;
	}

}
