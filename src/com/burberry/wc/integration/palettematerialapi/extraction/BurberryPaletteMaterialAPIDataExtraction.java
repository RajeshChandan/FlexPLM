package com.burberry.wc.integration.palettematerialapi.extraction;

import java.text.ParseException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.exception.InvalidInputException;
import com.burberry.wc.integration.palettematerialapi.criteria.BurberryPaletteMaterialAPICriteriaHelper;
import com.burberry.wc.integration.palettematerialapi.db.BurberryPaletteMaterialAPIDB;
import com.burberry.wc.integration.palettematerialapi.db.BurberryPaletteMaterialAPIDBHelper;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.material.*;
import com.lcs.wc.color.*;
import com.lcs.wc.supplier.LCSSupplier;

/**
 * A class to handle Extraction activity. Class contain several method to handle
 * Extraction activity i.e. Extracting Data from different objects and putting
 * it to the bean.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryPaletteMaterialAPIDataExtraction {

	/**
	 * BurberryPaletteMaterialAPIDataExtraction.
	 */
	private BurberryPaletteMaterialAPIDataExtraction() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryPaletteMaterialAPIDataExtraction.class);

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
		int objectMappedNumber = Integer.valueOf(mapValidObjects
				.get(strObjectName));
		logger.debug(methodName + "Object Mapped Number: " + objectMappedNumber);
		// Switch to check the mapped object number
		switch (objectMappedNumber) {

		// Case for Material Object
		case 1:
			Collection<Map> materialQueryCriteria = BurberryPaletteMaterialAPIDB
					.getAssociatedMaterials(BurberryPaletteMaterialAPICriteriaHelper
							.getQueryCriteria(
									LCSMaterial.class,
									MaterialSupplierFlexTypeScopeDefinition.MATERIAL_SCOPE,
									null, strAttDisplayName, strAttValue));
			logger.debug(methodName + "materialQueryCriteria: "
					+ materialQueryCriteria);
			colCriteriaMap.addAll(materialQueryCriteria);
			break;

		// Case for Material Supplier
		case 2:
			Collection<Map> materialSupplierQueryCriteria = BurberryPaletteMaterialAPIDB
					.getAssociatedMaterialsFromMaterialSupplier(BurberryPaletteMaterialAPICriteriaHelper
							.getQueryCriteria(
									LCSMaterialSupplier.class,
									MaterialSupplierFlexTypeScopeDefinition.MATERIALSUPPLIER_SCOPE,
									null, strAttDisplayName, strAttValue));
			logger.debug(methodName + "materialSupplierQueryCriteria: "
					+ materialSupplierQueryCriteria);
			colCriteriaMap.addAll(materialSupplierQueryCriteria);
			break;

		// Case for Supplier
		case 3:
			Collection<Map> supplierQueryCriteria = BurberryPaletteMaterialAPIDB
					.getAssociatedMaterialsFromSupplier(BurberryPaletteMaterialAPICriteriaHelper
							.getQueryCriteria(LCSSupplier.class, null, null,
									strAttDisplayName, strAttValue));
			logger.debug(methodName + "supplierQueryCriteria: "
					+ supplierQueryCriteria);
			colCriteriaMap.addAll(supplierQueryCriteria);
			break;

		// Case for Material Colour
		case 4:
			Collection<Map> materialColourQueryCriteria = BurberryPaletteMaterialAPIDB
					.getAssociatedMaterialsFromMaterialColour(BurberryPaletteMaterialAPICriteriaHelper
							.getQueryCriteria(LCSMaterialColor.class, null,
									null, strAttDisplayName, strAttValue));
			logger.debug(methodName + "materialColourQueryCriteria: "
					+ materialColourQueryCriteria);
			colCriteriaMap.addAll(materialColourQueryCriteria);
			break;

		// Case for Colour
		case 5:
			Collection<Map> colourQueryCriteria = BurberryPaletteMaterialAPIDB
					.getAssociatedMaterialsFromColour(BurberryPaletteMaterialAPICriteriaHelper
							.getQueryCriteria(LCSColor.class, null, null,
									strAttDisplayName, strAttValue));
			logger.debug(methodName + "colourQueryCriteria: "
					+ colourQueryCriteria);
			colCriteriaMap.addAll(colourQueryCriteria);
			break;

		// Case for Palette
		case 6:
			Collection<Map> paletteQueryCriteria = BurberryPaletteMaterialAPIDB
					.getAssociatedMaterialsFromPalette(BurberryPaletteMaterialAPICriteriaHelper
							.getQueryCriteria(
									LCSPalette.class,
									PaletteColorFlexTypeScopeDefinition.PALETTE_SCOPE,
									null, strAttDisplayName, strAttValue));
			logger.debug(methodName + "paletteQueryCriteria: "
					+ paletteQueryCriteria);
			colCriteriaMap.addAll(paletteQueryCriteria);
			break;

		// Default case
		default:
			break;
		}

		logger.debug("Switch Case Collection Criteria: " + colCriteriaMap);

		long obEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"End Time: ");
		logger.info(methodName + "Total Execution Time (ms): "
				+ (obEndTime - obStartTime));

		// Return Statement
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
	public static Collection<Map> getModifiedMaterialIds(
			Map<String, String> mapDeltaDateTime) throws BurException,
			ParseException, WTException {

		String methodName = "getModifiedMaterialIds() ";
		Map deltaDateMap = BurberryAPIUtil.getDeltaDateMap(mapDeltaDateTime);
		logger.debug(methodName + "Delta Date Map: " + deltaDateMap);
		Collection<Map> colCriteria = BurberryPaletteMaterialAPIDBHelper
				.getMaterialDeltaFromDateRange(
						(Date) deltaDateMap.get("startdate"),
						(Date) deltaDateMap.get("enddate"),
						(Boolean) deltaDateMap.get("modify"));
		logger.debug(methodName + "Delta Collection Criteria: " + colCriteria);
		// Return Statement
		return colCriteria;
	}

}