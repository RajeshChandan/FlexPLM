package com.burberry.wc.integration.productbomapi.extraction;

import java.text.ParseException;
import java.util.*;
import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.exception.InvalidInputException;
import com.burberry.wc.integration.productapi.criteria.BurberryProductAPICriteriaHelper;
import com.burberry.wc.integration.productapi.db.BurberryProductAPIDB;
import com.burberry.wc.integration.productbomapi.criteria.BurberryProductBOMAPICriteriaHelper;
import com.burberry.wc.integration.productbomapi.db.BurberryProductBOMAPIDB;
import com.burberry.wc.integration.productbomapi.db.BurberryProductBOMAPIDBHelper;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;

/**
 * A class to handle Extraction activity. Class contain several method to handle
 * Extraction activity i.e. Extracting Data from different objects and putting
 * it to the bean.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryProductBOMAPIDataExtraction {

	/**
	 * BurberryPaletteMaterialAPIDataExtraction.
	 */
	private BurberryProductBOMAPIDataExtraction() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductBOMAPIDataExtraction.class);

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
	 * @return Collection
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
		long obStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Start Time: ");

		// Initialisation of criteria map
		Collection<Map> colCriteriaMap = new ArrayList<Map>();

		// Get the Object mapped unique integer number
		int objectMapNumber = Integer.valueOf(mapValidObjects
				.get(strObjectName));
		// Switch to check the mapped object number
		switch (objectMapNumber) {
		// Case for Product Object
		case 1:
			Collection<Map> productCriteria = BurberryProductAPIDB
					.getAssociatedProducts(BurberryProductAPICriteriaHelper
							.getProductQueryCriteria(strAttDisplayName,
									strAttValue),true);
			logger.debug(methodName + "productQueryCriteria: "
					+ productCriteria);
			colCriteriaMap.addAll(productCriteria);
			break;

		// Case for Product-Season Object
		case 2:
			Collection<Map> productSeasonLinkCriteria = BurberryProductAPIDB
					.getAssociatedProductFromSeasonLink(BurberryProductAPICriteriaHelper
							.getProductSeasonLinkQueryCriteria(
									strAttDisplayName, strAttValue),true);
			logger.debug(methodName + "productSeasonLinkQueryCriteria: "
					+ productSeasonLinkCriteria);
			colCriteriaMap.addAll(productSeasonLinkCriteria);
			break;

		// Case for Colourway Object
		case 3:
			Collection<Map> colourwayCriteria = BurberryProductAPIDB
					.getAssociatedProductFromColorway(BurberryProductAPICriteriaHelper
							.getColourwayQueryCriteria(strAttDisplayName,
									strAttValue),true);
			logger.debug(methodName + "colourwayQueryCriteria: "
					+ colourwayCriteria);
			colCriteriaMap.addAll(colourwayCriteria);
			break;

		// This case is for Colourway-Season Object
		case 4:
			Collection<Map> colourwaySeasonCriteria = BurberryProductAPIDB
					.getAssociatedProductFromColorwaySeasonLink(BurberryProductAPICriteriaHelper
							.getColourwaySeasonQueryCriteria(strAttDisplayName,
									strAttValue),true);
			logger.debug(methodName + "colourwaySeasonQueryCriteria: "
					+ colourwaySeasonCriteria);
			colCriteriaMap.addAll(colourwaySeasonCriteria);
			break;

		case 5:
			Collection<Map> sourcingConfigCriteria = BurberryProductBOMAPIDB
					.getAssociatedProductFromSource(BurberryProductAPICriteriaHelper
							.getSourcingConfigQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "sourcingConfigQueryCriteria: "
					+ sourcingConfigCriteria);
			colCriteriaMap.addAll(sourcingConfigCriteria);
			break;

		// Default case
		default:
			colCriteriaMap.addAll(getObjectBasedQueryCriteriaForBOMObjects(
					strAttDisplayName, strAttValue, objectMapNumber));

		}

		logger.debug(methodName + "Switch Case Collection Criteria: "
				+ colCriteriaMap);

		long obEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"End Time: ");
		logger.debug(methodName + "Total Execution Time (ms): "
				+ (obEndTime - obStartTime));

		// Return Statement
		return colCriteriaMap;

	}

	/**
	 * @param strAttDisplayName
	 *            strAttDisplayName
	 * @param strAttValue
	 *            strAttValue
	 * @param objectMappedNumber
	 *            objectMappedNumber
	 * @return Collection
	 * @throws WTException
	 *             WTException
	 * @throws BurException
	 *             BurException
	 * @throws ParseException
	 *             ParseException
	 */
	private static Collection<Map> getObjectBasedQueryCriteriaForBOMObjects(
			String strAttDisplayName, String strAttValue, int objectMappedNumber)
			throws WTException, BurException, ParseException {

		String methodName = "getObjectBasedQueryCriteriaForBOMObjects()";

		// Initialisation of criteria map
		Collection<Map> colCriteriaMap = new ArrayList<Map>();

		// Switch to check the mapped object number
		switch (objectMappedNumber) {
		// Case for Material Object
		case 6:
			Collection<Map> materialQueryCriteria = BurberryProductAPIDB
					.getAssociatedProductFromMainRMMaterial(BurberryProductAPICriteriaHelper
							.getMaterialQueryCriteria(strAttDisplayName,
									strAttValue),true);
			logger.debug(methodName + "materialQueryCriteria: "
					+ materialQueryCriteria);
			colCriteriaMap.addAll(materialQueryCriteria);
			break;

		// Case for BOM Object
		case 7:
			Collection<Map> bomQueryCriteria = BurberryProductBOMAPIDB
					.getAssociatedProductFromBom(BurberryProductBOMAPICriteriaHelper
							.getQueryCriteria(strAttDisplayName, strAttValue,
									FlexBOMPart.class));
			logger.debug(methodName + "bomQueryCriteria: " + bomQueryCriteria);
			colCriteriaMap.addAll(bomQueryCriteria);
			break;

		// Case for BOM Link Object
		case 8:
			Collection<Map> bomLinkQueryCriteria = BurberryProductBOMAPIDB
					.getAssociatedProductFromBomLink(BurberryProductBOMAPICriteriaHelper
							.getQueryCriteria(strAttDisplayName, strAttValue,
									FlexBOMLink.class));
			logger.debug(methodName + "bomLinkQueryCriteria: "
					+ bomLinkQueryCriteria);
			colCriteriaMap.addAll(bomLinkQueryCriteria);
			break;

		// Case for BOM Material Object
		case 9:
			Collection<Map> bomMaterialQueryCriteria = BurberryProductBOMAPIDB
					.getAssociatedProductFromBomMaterial(BurberryProductBOMAPICriteriaHelper
							.getBomMaterialQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "bomMaterialQueryCriteria: "
					+ bomMaterialQueryCriteria);
			colCriteriaMap.addAll(bomMaterialQueryCriteria);
			break;

		// Case for Season Object
		case 10:
			Collection<Map> seasonQueryCriteria = BurberryProductAPIDB
					.getAssociatedProductFromSeason(BurberryProductAPICriteriaHelper
							.getSeasonQueryCriteria(strAttDisplayName,
									strAttValue),true);
			logger.debug(methodName + "seasonQueryCriteria: "
					+ seasonQueryCriteria);
			colCriteriaMap.addAll(seasonQueryCriteria);
			break;
		default:
			break;
		}
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
		logger.debug(methodName + "Delta Date Map: " + deltaDateMap);
		Collection<Map> colCriteria = BurberryProductBOMAPIDBHelper
				.getProductDeltaFromDateRange(
						(Date) deltaDateMap.get("startdate"),
						(Date) deltaDateMap.get("enddate"),
						(Boolean) deltaDateMap.get("modify"));
		logger.debug(methodName + "Delta Collection Criteria: " + colCriteria);
		// Return Statement
		return colCriteria;
	}
}
