package com.burberry.wc.integration.planningapi.extraction;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.exception.InvalidInputException;
import com.burberry.wc.integration.planningapi.criteria.BurberryPlanningAPICriteriaHelper;
import com.burberry.wc.integration.planningapi.db.BurberryPlanningAPIDB;
import com.burberry.wc.integration.planningapi.db.BurberryPlanningAPIDBHelper;
import com.burberry.wc.integration.productapi.criteria.BurberryProductAPICriteriaHelper;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.db.FlexObject;

/**
 * A Helper class to handle Extraction activity. Class contain several method to
 * handle Extraction activity i.e. Extracting Data from different objects and
 * putting it to the bean.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryPlanningAPIDataExtraction {

	/**
	 * BurberryPlanningAPIDataExtraction.
	 */
	private BurberryPlanningAPIDataExtraction() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPlanningAPIDataExtraction.class);

	/**
	 * fetchCriteriaMap.
	 * 
	 * @param strObjectName
	 *            Object Name
	 * @param strAttDisplayName
	 *            Display Name
	 * @param strAttValue
	 *            Value
	 * @param mapValidObjects
	 *            mapValidObjects
	 * @return Collection<Map> Collection
	 * @throws WTException
	 *             Exception
	 * @throws InvalidInputException
	 *             Exception
	 * @throws BurException
	 *             Exception
	 * @throws ParseException
	 *             Exception
	 */
	public static Collection<Map> getObjectBasedQueryCriteria(
			String strObjectName, String strAttDisplayName, String strAttValue,
			Map<String, String> mapValidObjects) throws WTException,
			BurException, ParseException {
		String methodName = "getObjectBasedQueryCriteria() ";
		// Initialisation of criteria map
		Collection<Map> colCriteriaMap = new ArrayList<Map>();

		// Get the Object mapped unique integer number
		int objectMappedNumber = Integer.valueOf(mapValidObjects
				.get(strObjectName));

		// Switch to check the mapped object number
		switch (objectMappedNumber) {

		// Case for Plan Object
		case 1:
			Collection<FlexObject> planQueryCriteria = BurberryPlanningAPIDB
					.getAssociatedPlans(BurberryPlanningAPICriteriaHelper
							.getPlanQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "planQueryCriteria: " + planQueryCriteria);
			colCriteriaMap.addAll(planQueryCriteria);
			break;

		// Case for Plan Detail Object
		case 2:
			Collection<FlexObject> planDetailQueryCriteria = BurberryPlanningAPIDB
					.getAssociatedPlansFromDetail(BurberryPlanningAPICriteriaHelper
							.getPlanDetailQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "planDetailQueryCriteria: "
					+ planDetailQueryCriteria);
			colCriteriaMap.addAll(planDetailQueryCriteria);
			break;

		// Case for Season Object
		case 3:
			Collection<FlexObject> seasonQueryCriteria = BurberryPlanningAPIDB
					.getAssociatedPlanFromSeason(BurberryProductAPICriteriaHelper
							.getSeasonQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "seasonQueryCriteria: "
					+ seasonQueryCriteria);
			colCriteriaMap.addAll(seasonQueryCriteria);
			break;

		default:
			break;
		}

		return colCriteriaMap;
	}

	public static Collection<Map> getModifiedPlanIds(
			Map<String, String> mapDeltaDate) throws ParseException, BurException, WTException {

		String methodName = "getModifiedPlanIds() ";
		Map deltaDateMap = BurberryAPIUtil.getDeltaDateMap(mapDeltaDate);

		logger.debug(methodName + "Delta Date Map: " + deltaDateMap);
		Collection<Map> colCriteria = BurberryPlanningAPIDBHelper
				.getPlanningDeltaFromDateRange(
						(Date) deltaDateMap.get("startdate"),
						(Date) deltaDateMap.get("enddate"),
						(Boolean) deltaDateMap.get("modify"));
		logger.debug(methodName + "Delta Collection Criteria: " + colCriteria);
		// Return Statement
		return colCriteria;
	}

}
