package com.burberry.wc.integration.planningapi.transform;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import wt.fc.WTObject;
import wt.util.WTException;

import com.burberry.wc.integration.planningapi.bean.AssociatedSeason;
import com.burberry.wc.integration.planningapi.bean.PlanDetail;
import com.burberry.wc.integration.planningapi.constant.BurPlanningAPIConstant;
import com.burberry.wc.integration.util.BurberryAPIDBUtil;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.burberry.wc.integration.util.BurberryPlanningAPIJsonDataUtil;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.planning.FlexPlan;
import com.lcs.wc.planning.PlanLineItem;
import com.lcs.wc.planning.PlanMaster;
import com.lcs.wc.planning.PlanQuery;
import com.lcs.wc.planning.PlanToOwnerLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;

public final class BurberryPlanningAPIDataTransform {

	/**
	 * BurberryPlanningAPIDataTransform.
	 */
	private BurberryPlanningAPIDataTransform() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPlanningAPIDataTransform.class);

	/**
	 * @param planObj
	 * @param collPlanDetails
	 * @return
	 * @throws WTException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static List<PlanDetail> getListPlanDetailBean(FlexPlan planObj,
			Collection<String> collPlanDetails) throws WTException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, IOException {
		String methodName = "getListPlanDetailBean() ";
		long planDetailStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"planDetailStartTime: ");

		Collection<PlanLineItem> listPlanLineItems = PlanQuery
				.findPlanLineItems(planObj,  BurPlanningAPIConstant.SORTINGNUMBER);
		logger.debug(methodName + "Collection Of Plan LineItems: "
				+ listPlanLineItems);
		ArrayList<PlanDetail> listPlanDetail = new ArrayList<PlanDetail>();

		// Checking through each plan detail under plan
		for (PlanLineItem planLineItem : listPlanLineItems) {
			// checking if plan detail criteria is given in URL and validating
			// if
			// plan detail object satisfies the URL criteria
			boolean idExists = BurberryAPIDBUtil.checkIfObjectExists(String
					.valueOf(FormatHelper
							.getNumericObjectIdFromObject(planLineItem)),
					collPlanDetails);
			// Check if exists
			if (idExists) {

				logger.debug(methodName + "Extracting data from PlanLineItem: "
						+ planLineItem);

				// Extraction of Plan Detail Object data
				PlanDetail planDetailBean = BurberryPlanningAPIJsonDataUtil
						.getPlanDetailBean(planLineItem);
				logger.debug(methodName + "Plan Detail Bean: " + planDetailBean);
				listPlanDetail.add(planDetailBean);

			}
		}
		logger.debug(methodName + "List of plan detail beans " + listPlanDetail);
		long planDetailEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"planDetailEndTime: ");
		logger.debug(methodName
				+ "Plan Detail Transform  Total Execution Time (ms): "
				+ (planDetailEndTime - planDetailStartTime));
		//return list of plandetail
		return listPlanDetail;
	}

	/**
	 * @param planObj
	 * @param collPlanToSeasonIds
	 * @return
	 * @throws WTException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static List<AssociatedSeason> getListAssociatedSeasonBean(
			FlexPlan planObj, Collection<String> collPlanToSeasonIds)
			throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException {
		String methodName = "getListAsscoaitedSeasonBean() ";
		long associatedSeasonStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "associatedSeasonStartTime: ");

		Collection<PlanToOwnerLink> listOwnerLinks = LCSQuery
				.getObjectsFromResults(PlanQuery.findPlanToOwnerLinksQuery(
						(PlanMaster) planObj.getMaster(), null),
						"com.lcs.wc.planning.PlanToOwnerLink:", "PLANTOOWNERLINK.IDA2A2");
		logger.debug(methodName + "Collection Of Seasons: " + listOwnerLinks);
		ArrayList<AssociatedSeason> listAssociatedSeason = new ArrayList<AssociatedSeason>();

		// Checking through each season under plan
		for (PlanToOwnerLink ownerLink : listOwnerLinks) {
			
		WTObject owner=ownerLink.getOwner();
		if(owner instanceof LCSSeasonMaster){
			LCSSeason season=(LCSSeason) VersionHelper.latestIterationOf(owner);
			// checking if Season criteria is given in URL and validating if
			// season object satisfies the URL criteria
			boolean idExists = BurberryAPIDBUtil.checkIfObjectExists(
					String.valueOf(season.getBranchIdentifier()),
					collPlanToSeasonIds);
			// Check if exists
			if (idExists) {
				logger.debug(methodName + "Extracting data from Season: "
						+ season.getName());
				// Extraction of Plan Detail Object data
				AssociatedSeason associatedSeason = BurberryPlanningAPIJsonDataUtil
						.getAssociatedSeasonBean(season);
				listAssociatedSeason.add(associatedSeason);

			}
		}
		}
		logger.debug(methodName + "List of Associated Season beans "
				+ listAssociatedSeason);
		long associatedSeasonEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "associatedSeasonEndTime: ");
		logger.debug(methodName
				+ "Associated Season Transform  Total Execution Time (ms): "
				+ (associatedSeasonEndTime - associatedSeasonStartTime));
				
		//Return list of associatedSeason
		return listAssociatedSeason;
	}
}
