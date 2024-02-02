package com.burberry.wc.integration.productcostingapi.extraction;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.productapi.criteria.BurberryProductAPICriteriaHelper;
import com.burberry.wc.integration.productcostingapi.criteria.BurberryProductCostingAPICriteriaHelper;
import com.burberry.wc.integration.productcostingapi.db.BurberryProductCostingAPIDB;
import com.burberry.wc.integration.productcostingapi.db.BurberryProductCostingAPIDBHelper;
import com.burberry.wc.integration.util.BurberryAPIUtil;

/**
 * A class to handle Extraction activity. Class contain several method to handle
 * Extraction activity i.e. Extracting Data from different objects and putting
 * it to the bean.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryProductCostingAPIDataExtraction {

	/**
	 * BurberryProductCostingAPIDataExtraction.
	 */
	private BurberryProductCostingAPIDataExtraction() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductCostingAPIDataExtraction.class);

	/**
	 * This method is used to get Object based criteria.
	 * 
	 * @param strObjectName
	 *            Object Name
	 * @param strAttDisplayName
	 *            Att Display Name
	 * @param strAttValue
	 *            Att Value
	 * @param mapValidObjects
	 *            Map Valid Objects
	 * @return Collection<Map>
	 * @throws BurException
	 *             Exception
	 * @throws ParseException
	 *             Exception
	 * @throws WTException
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

		// Case for Product Object
		case 1:
			Collection<Map> productQueryCriteria = BurberryProductCostingAPIDB
					.getProductsAssociatedToCostSheet(BurberryProductAPICriteriaHelper
							.getProductQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "productQueryCriteria: "
					+ productQueryCriteria);
			colCriteriaMap.addAll(productQueryCriteria);
			break;

		// Case for Product-Season Object
		case 2:
			Collection<Map> productSeasonLinkQueryCriteria = BurberryProductCostingAPIDB
					.getProductsAssociatedToProductSeasonCostSheet(BurberryProductAPICriteriaHelper
							.getProductSeasonLinkQueryCriteria(
									strAttDisplayName, strAttValue));
			logger.debug(methodName + "productSeasonLinkQueryCriteria: "
					+ productSeasonLinkQueryCriteria);
			colCriteriaMap.addAll(productSeasonLinkQueryCriteria);
			break;

		// Case for Colourway Object
		case 3:
			Collection<Map> colourwayQueryCriteria = BurberryProductCostingAPIDB
					.getProductsAssociatedToColorwayCostSheet(BurberryProductAPICriteriaHelper
							.getColourwayQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "colourwayQueryCriteria: "
					+ colourwayQueryCriteria);
			colCriteriaMap.addAll(colourwayQueryCriteria);
			break;

		// This case is for Colourway-Season Object
		case 4:
			Collection<Map> colourwaySeasonQueryCriteria = BurberryProductCostingAPIDB
					.getProductAssociatedToColorwaySeasonCostSheet(BurberryProductAPICriteriaHelper
							.getColourwaySeasonQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "colourwaySeasonQueryCriteria: "
					+ colourwaySeasonQueryCriteria);
			colCriteriaMap.addAll(colourwaySeasonQueryCriteria);
			break;

		// Case for Souring Configuration Object
		case 5:
			Collection<Map> sourcingConfigQueryCriteria = BurberryProductCostingAPIDB
					.getProductsAssociatedToCostSheet(BurberryProductAPICriteriaHelper
							.getSourcingConfigQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "sourcingConfigQueryCriteria: "
					+ sourcingConfigQueryCriteria);
			colCriteriaMap.addAll(sourcingConfigQueryCriteria);
			break;

		// Case for Cost Sheet Object
		case 6:
			Collection<Map> costSheetQueryCriteria = BurberryProductCostingAPIDB
					.getProductsAssociatedToCostSheet(BurberryProductCostingAPICriteriaHelper
							.getCostingQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "costSheetQueryCriteria: "
					+ costSheetQueryCriteria);
			colCriteriaMap.addAll(costSheetQueryCriteria);
			break;

		// Default case
		default:
			break;
		}

		logger.debug("Switch Case Collection Criteria: " + colCriteriaMap);
		return colCriteriaMap;
	}

	/**
	 * Method to get Product for Delta Map.
	 * 
	 * @param mapDeltaDate
	 *            Map
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 * @throws ParseException
	 *             Exception
	 * @throws BurException
	 *             Exception
	 */
	public static Collection<Map> getModifiedProductIds(
			Map<String, String> mapDeltaDate) throws WTException,
			ParseException, BurException {

		String methodName = "getModifiedProductIds() ";
		Map deltaDateMap = BurberryAPIUtil.getDeltaDateMap(mapDeltaDate);

		logger.debug(methodName + "Delta Date Map: " + deltaDateMap);
		Collection<Map> colCriteria = BurberryProductCostingAPIDBHelper
				.getProductCostingDeltaFromDateRange(
						(Date) deltaDateMap.get("startdate"),
						(Date) deltaDateMap.get("enddate"),
						(Boolean) deltaDateMap.get("modify"));
		logger.debug(methodName + "Delta Collection Criteria: " + colCriteria);
		// Return Statement
		return colCriteria;
	}

}
