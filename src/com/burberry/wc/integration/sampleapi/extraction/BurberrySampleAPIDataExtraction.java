package com.burberry.wc.integration.sampleapi.extraction;

import java.text.ParseException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.palettematerialapi.criteria.BurberryPaletteMaterialAPICriteriaHelper;
import com.burberry.wc.integration.productapi.criteria.BurberryProductAPICriteriaHelper;
import com.burberry.wc.integration.sampleapi.constant.BurSampleConstant;
import com.burberry.wc.integration.sampleapi.criteria.BurberrySampleAPICriteriaHelper;
import com.burberry.wc.integration.sampleapi.db.*;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.material.*;
import com.lcs.wc.sample.*;

/**
 * A class to handle Extraction activity. Class contain several method to handle
 * Extraction activity i.e. Extracting Data from different objects and putting
 * it to the bean.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberrySampleAPIDataExtraction {

	/**
	 * BurberrySampleAPIDataExtraction.
	 */
	private BurberrySampleAPIDataExtraction() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberrySampleAPIDataExtraction.class);

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
			Map<String, String> mapValidObjects) throws BurException,
			ParseException, WTException {

		// Method Name
		String methodName = "getObjectBasedQueryCriteria() ";

		// Method Start Time
		long obStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Start Time: ");

		// Initialisation of criteria map
		Collection<Map> colCriteriaMap = new ArrayList<Map>();

		// Get the Object mapped unique integer number
		int objectMappedNumber = Integer.valueOf(mapValidObjects
				.get(strObjectName));
		logger.debug(methodName + "Object Mapped Number: " + objectMappedNumber);

		// Switch to check the mapped object number
		switch (objectMappedNumber) {

		// Case for Product Sample
		case 1:
			Collection<Map> productSampleQuery = BurberrySampleAPIProductDB
					.getProductSample(BurberrySampleAPICriteriaHelper
							.getQueryCriteria(
									LCSSample.class,
									SampleRequestFlexTypeScopeDefinition.SAMPLE_SCOPE,
									"PRODUCT_SAMPLE", strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "productSampleQuery: "
					+ productSampleQuery);
			colCriteriaMap.addAll(productSampleQuery);
			break;

		// Case for Product Sample Request
		case 2:
			Collection<Map> productSampleRequestQuery = BurberrySampleAPIProductDB
					.getProductSampleRequest(BurberrySampleAPICriteriaHelper
							.getQueryCriteria(
									LCSSampleRequest.class,
									SampleRequestFlexTypeScopeDefinition.SAMPLEREQUEST_SCOPE,
									"PRODUCT_SAMPLE", strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "productSampleRequestQuery: "
					+ productSampleRequestQuery);
			colCriteriaMap.addAll(productSampleRequestQuery);
			break;

		// Case for Product Object
		case 3:
			Collection<Map> productQueryCriteria = BurberrySampleAPIProductDB
					.getAssociatedSampleRequestFromProduct(BurberryProductAPICriteriaHelper
							.getProductQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "productQueryCriteria: "
					+ productQueryCriteria);
			colCriteriaMap.addAll(productQueryCriteria);
			break;

		// Case for Product-Season Object
		case 4:
			Collection<Map> productSeasonLinkQueryCriteria = BurberrySampleAPIProductDB
					.getAssociatedSampleRequestFromProductSeason(BurberryProductAPICriteriaHelper
							.getProductSeasonLinkQueryCriteria(
									strAttDisplayName, strAttValue));
			logger.debug(methodName + "productSeasonLinkQueryCriteria: "
					+ productSeasonLinkQueryCriteria);
			colCriteriaMap.addAll(productSeasonLinkQueryCriteria);
			break;

		// Case for Colourway Object
		case 5:
			Collection<Map> colourwayQueryCriteria = BurberrySampleAPIProductDB
					.getAssociatedSampleRequestFromColorway(BurberryProductAPICriteriaHelper
							.getColourwayQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "colourwayQueryCriteria: "
					+ colourwayQueryCriteria);
			colCriteriaMap.addAll(colourwayQueryCriteria);
			break;

		// Case for Colourway-Season Object
		case 6:
			Collection<Map> colourwaySeasonQueryCriteria = BurberrySampleAPIProductDB
					.getAssociatedSampleRequestFromColorwaySeason(BurberryProductAPICriteriaHelper
							.getColourwaySeasonQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "colourwaySeasonQueryCriteria: "
					+ colourwaySeasonQueryCriteria);
			colCriteriaMap.addAll(colourwaySeasonQueryCriteria);
			break;

		// Default case
		default:
			colCriteriaMap.addAll(getObjectBasedQueryCriteriaForSampleObjects(
					strAttDisplayName, strAttValue, objectMappedNumber));

		}

		logger.debug("Switch Case Collection Criteria: " + colCriteriaMap);

		long obEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"End Time: ");
		logger.debug(methodName + "Total Execution Time (ms): "
				+ (obEndTime - obStartTime));

		// Return Statement
		return colCriteriaMap;
	}

	/**
	 * This method is used to get Object based criteria.
	 * 
	 * @param strAttDisplayName
	 *            Att Display Name
	 * @param strAttValue
	 *            Att Value
	 * @param objectMappedNumber
	 *            Integer
	 * @return Collection<Map>
	 * @throws WTException
	 *             Exception
	 * @throws BurException
	 *             Exception
	 * @throws ParseException
	 *             Exception
	 */
	private static Collection<Map> getObjectBasedQueryCriteriaForSampleObjects(
			String strAttDisplayName, String strAttValue, int objectMappedNumber)
			throws WTException, BurException, ParseException {

		// Method Name
		String methodName = "getObjectBasedQueryCriteriaForBOMObjects()";

		// Initialisation of criteria map
		Collection<Map> colCriteriaMap = new ArrayList<Map>();

		// Switch to check the mapped object number
		switch (objectMappedNumber) {

		// Case for Souring Configuration Object
		case 7:
			Collection<Map> sourcingConfigQueryCriteria = BurberrySampleAPIMaterialDB
					.getAssociatedSampleRequestFromSourcingConfig(BurberryProductAPICriteriaHelper
							.getSourcingConfigQueryCriteria(strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "sourcingConfigQueryCriteria: "
					+ sourcingConfigQueryCriteria);
			colCriteriaMap.addAll(sourcingConfigQueryCriteria);
			break;

		// Case for Material Sample
		case 8:
			Collection<Map> materialSampleQuery = BurberrySampleAPIMaterialDB
					.getMaterialSample(BurberrySampleAPICriteriaHelper
							.getQueryCriteria(
									LCSSample.class,
									SampleRequestFlexTypeScopeDefinition.SAMPLE_SCOPE,
									"MATERIAL_SAMPLE", strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "materialSampleQuery: "
					+ materialSampleQuery);
			colCriteriaMap.addAll(materialSampleQuery);
			break;

		// Case for Material Sample Request
		case 9:
			Collection<Map> materialSampleRequestQuery = BurberrySampleAPIMaterialDB
					.getMaterialSampleRequest(BurberrySampleAPICriteriaHelper
							.getQueryCriteria(
									LCSSampleRequest.class,
									SampleRequestFlexTypeScopeDefinition.SAMPLEREQUEST_SCOPE,
									"MATERIAL_SAMPLE", strAttDisplayName,
									strAttValue));
			logger.debug(methodName + "materialSampleRequestQuery: "
					+ materialSampleRequestQuery);
			colCriteriaMap.addAll(materialSampleRequestQuery);
			break;

		// Case for Material Object
		case 10:
			Collection<Map> materialQueryCriteria = BurberrySampleAPIMaterialDB
					.getAssociatedSampleRequestFromMaterial(BurberryPaletteMaterialAPICriteriaHelper
							.getQueryCriteria(
									LCSMaterial.class,
									MaterialSupplierFlexTypeScopeDefinition.MATERIAL_SCOPE,
									null, strAttDisplayName, strAttValue));
			logger.debug(methodName + "materialQueryCriteria: "
					+ materialQueryCriteria);
			colCriteriaMap.addAll(materialQueryCriteria);
			break;

		// Case for Material Supplier
		case 11:
			Collection<Map> materialSupplierQueryCriteria = BurberrySampleAPIMaterialDB
					.getAssociatedSampleRequestFromMaterialSupplier(BurberryPaletteMaterialAPICriteriaHelper
							.getQueryCriteria(
									LCSMaterialSupplier.class,
									MaterialSupplierFlexTypeScopeDefinition.MATERIALSUPPLIER_SCOPE,
									null, strAttDisplayName, strAttValue));
			logger.debug(methodName + "materialSupplierQueryCriteria: "
					+ materialSupplierQueryCriteria);
			colCriteriaMap.addAll(materialSupplierQueryCriteria);
			break;

		// Case for Material Colour
		case 12:
			Collection<Map> materialColourQueryCriteria = BurberrySampleAPIMaterialDB
					.getAssociatedSampleRequestFromMaterialColour(BurberryPaletteMaterialAPICriteriaHelper
							.getQueryCriteria(LCSMaterialColor.class, null,
									null, strAttDisplayName, strAttValue));
			logger.debug(methodName + "materialColourQueryCriteria: "
					+ materialColourQueryCriteria);
			colCriteriaMap.addAll(materialColourQueryCriteria);
			break;

		default:
			break;
		}
		return colCriteriaMap;
	}

	/**
	 * This method is used to get created / updated sample requests. Associated
	 * to Product and Material
	 * 
	 * @param mapDeltaDateTime
	 *            Map<String, String>
	 * @param bProductDelta
	 *            boolean
	 * @param bMaterialDelta
	 *            boolean
	 * @return Collection<Map>
	 * @throws WTException
	 *             Exception
	 * @throws ParseException
	 *             Exception
	 * @throws BurException
	 *             Exception
	 */
	public static Collection<Map> getModifiedSampleRequestIds(
			Map<String, String> mapDeltaDateTime, boolean bProductDelta,
			boolean bMaterialDelta) throws WTException, ParseException,
			BurException {
		String methodName = "getModifiedSampleRequestIds() ";
		Map deltaDateMap = BurberryAPIUtil.getDeltaDateMap(mapDeltaDateTime);
		logger.debug(methodName + "Delta Date Map: " + deltaDateMap);
		//Check if both Product Delta and Material Delta is passed as parameters
		if (bProductDelta && bMaterialDelta) {
			BurberryAPIUtil.throwBurException("Product Delta: " + bProductDelta
					+ " and Material Delta: " + bMaterialDelta+". ",
					BurSampleConstant.STR_ERROR_MSG_MULTIPLE_DELTA_PARAMETERS);
		}
		Collection<Map> colCriteria = BurberrySampleAPIDBHelper
				.getSampleDeltaFromDateRange(
						(Date) deltaDateMap.get("startdate"),
						(Date) deltaDateMap.get("enddate"),
						(Boolean) deltaDateMap.get("modify"), bProductDelta,
						bMaterialDelta);
		logger.debug(methodName + "Delta Collection Criteria: " + colCriteria);
		// Return Statement
		return colCriteria;
	}

}
