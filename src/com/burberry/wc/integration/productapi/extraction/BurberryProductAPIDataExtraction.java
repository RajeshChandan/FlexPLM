package com.burberry.wc.integration.productapi.extraction;

import java.text.ParseException;
import java.util.*;
import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.exception.InvalidInputException;
import com.burberry.wc.integration.productapi.criteria.BurberryProductAPICriteriaHelper;
import com.burberry.wc.integration.productapi.db.BurberryProductAPIDB;
import com.burberry.wc.integration.productapi.db.BurberryProductAPIDBHelper;
import com.burberry.wc.integration.util.BurberryAPIUtil;

/**
 * A Helper class to handle Extraction activity. Class contain several method to
 * handle Extraction activity i.e. Extracting Data from different objects and
 * putting it to the bean.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryProductAPIDataExtraction {

	/**
	 * BurberryProductAPIDataExtractionHelper.
	 */
	private BurberryProductAPIDataExtraction() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductAPIDataExtraction.class);

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
			InvalidInputException, BurException, ParseException {

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
			Collection<Map> productQueryCriteria = BurberryProductAPIDB
					.getAssociatedProducts(BurberryProductAPICriteriaHelper
							.getProductQueryCriteria(strAttDisplayName,
									strAttValue),false);
			logger.debug(methodName + "productQueryCriteria: "
					+ productQueryCriteria);
			colCriteriaMap.addAll(productQueryCriteria);
			break;

		// Case for Product-Season Object
		case 2:
			Collection<Map> productSeasonLinkQueryCriteria = BurberryProductAPIDB
					.getAssociatedProductFromSeasonLink(BurberryProductAPICriteriaHelper
							.getProductSeasonLinkQueryCriteria(
									strAttDisplayName, strAttValue),false);
			logger.debug(methodName + "productSeasonLinkQueryCriteria: "
					+ productSeasonLinkQueryCriteria);
			colCriteriaMap.addAll(productSeasonLinkQueryCriteria);
			break;

		// Case for Colourway Object
		case 3:
			Collection<Map> colourwayQueryCriteria = BurberryProductAPIDB
					.getAssociatedProductFromColorway(BurberryProductAPICriteriaHelper
							.getColourwayQueryCriteria(strAttDisplayName,
									strAttValue),false);
			logger.debug(methodName + "colourwayQueryCriteria: "
					+ colourwayQueryCriteria);
			colCriteriaMap.addAll(colourwayQueryCriteria);
			break;

		// This case is for Colourway-Season Object
		case 4:
			Collection<Map> colourwaySeasonQueryCriteria = BurberryProductAPIDB
					.getAssociatedProductFromColorwaySeasonLink(BurberryProductAPICriteriaHelper
							.getColourwaySeasonQueryCriteria(strAttDisplayName,
									strAttValue),false);
			logger.debug(methodName + "colourwaySeasonQueryCriteria: "
					+ colourwaySeasonQueryCriteria);
			colCriteriaMap.addAll(colourwaySeasonQueryCriteria);
			break;

		// Case for Season Object
		case 5:
			Collection<Map> seasonQueryCriteria = BurberryProductAPIDB
					.getAssociatedProductFromSeason(BurberryProductAPICriteriaHelper
							.getSeasonQueryCriteria(strAttDisplayName,
									strAttValue),false);
			logger.debug(methodName + "seasonQueryCriteria: "
					+ seasonQueryCriteria);
			colCriteriaMap.addAll(seasonQueryCriteria);
			break;

		// Case for Souring Configuration Object
		case 6:
			Collection<Map> sourcingConfigQueryCriteria = BurberryProductAPIDB
					.getAssociatedProductFromSource(BurberryProductAPICriteriaHelper
							.getSourcingConfigQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "sourcingConfigQueryCriteria: "
					+ sourcingConfigQueryCriteria);
			colCriteriaMap.addAll(sourcingConfigQueryCriteria);
			break;

		// Case for Placeholder Object
		case 7:
			Collection<Map> placeholderQueryCriteria = BurberryProductAPIDB
					.getAssociatedProductFromPlaceholder(BurberryProductAPICriteriaHelper
							.getPlaceholderQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "placeholderQueryCriteria: "
					+ placeholderQueryCriteria);
			colCriteriaMap.addAll(placeholderQueryCriteria);
			break;

		// Case for Material Object
		case 8:
			Collection<Map> materialQueryCriteria = BurberryProductAPIDB
					.getAssociatedProductFromMainRMMaterial(BurberryProductAPICriteriaHelper
							.getMaterialQueryCriteria(strAttDisplayName,
									strAttValue),false);
			logger.debug(methodName + "materialQueryCriteria: "
					+ materialQueryCriteria);
			colCriteriaMap.addAll(materialQueryCriteria);
			break;

		// Case for Commodity Code Object
		case 9:
			Collection<Map> commodityCodeQueryCriteria = BurberryProductAPIDB
					.getAssociatedProductFromCommodityCode(BurberryProductAPICriteriaHelper
							.getCommodityCodeQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "commodityCodeQueryCriteria: "
					+ commodityCodeQueryCriteria);
			colCriteriaMap.addAll(commodityCodeQueryCriteria);
			break;

		// Default case
		default:
			break;
		}

		logger.debug("Switch Case Collection Criteria: " + colCriteriaMap);
		return colCriteriaMap;

	}

	/**
	 * Method to get Start Date and End Date criteria map for Delta.
	 * 
	 * @param mapDeltaDateTime
	 *            Map
	 * @return Collection<Map> Collection
	 * @throws BurException
	 *             Exception
	 * @throws ParseException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 */
	public static Collection<Map> getModifiedProductIds(
			Map<String, String> mapDeltaDateTime) throws BurException,
			ParseException, WTException {

		String methodName = "getModifiedProductIds() ";

		Map deltaDateMap = BurberryAPIUtil.getDeltaDateMap(mapDeltaDateTime);

		Collection<Map> colCriteria = BurberryProductAPIDBHelper
				.getProductDeltaFromDateRange(
						(Date) deltaDateMap.get("startdate"),
						(Date) deltaDateMap.get("enddate"),
						(Boolean) deltaDateMap.get("modify"));
		logger.debug(methodName + "Delta Collection Criteria: " + colCriteria);
		return colCriteria;
	}

}
