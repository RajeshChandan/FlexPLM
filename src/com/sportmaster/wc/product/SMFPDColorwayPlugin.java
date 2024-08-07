package com.sportmaster.wc.product;

import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.log4j.Logger;

import com.lcs.wc.db.*;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.*;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.emailutility.fpd.constants.SMFPDColorwayCreateMOAConstants;
import com.sportmaster.wc.emailutility.util.SMEmailNotificationUtil;
import com.sportmaster.wc.emailutility.util.SMMultiObjectAttributeUtil;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class SMFPDColorwayPlugin {

	/**
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMFPDColorwayPlugin.class);
	/**
	 * CLASS_NAME.
	 */
	public static final String CLASS_NAME = "SMFPDColorwayPlugin";
	/**
	 * METHOD_START.
	 */
	public static final String METHOD_START = "--Start";
	/**
	 * METHOD_END.
	 */
	public static final String METHOD_END = "--End";

	/**
	 * Constructor.
	 */
	protected SMFPDColorwayPlugin() {
		// protected constructor
	}

	/**
	 * Method Name: trackColorwayCreateOnMOA.
	 * 
	 * @param wtObj for wtObj.
	 * @return void.
	 * @throws WTPropertyVetoException
	 */
	public static void trackColorwayCreateOnMOA(WTObject wtObj) {
		String methodName = "trackColorwayCreateOnMOA()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		try {
			if (wtObj instanceof LCSSKUSeasonLink) {
				// Getting season product link.
				LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) wtObj;
				LOGGER.debug(CLASS_NAME + "--" + methodName + "--skuSeasonLink =" + skuSeasonLink);
				// Getting Season Object.
				LCSSeason seasonObject = (LCSSeason) VersionHelper.latestIterationOf(skuSeasonLink.getSeasonMaster());
				LOGGER.debug(CLASS_NAME + "--" + methodName + "--Season Name =" + seasonObject.getName());
				// Getting sku Object.
				LCSSKU skuObject = SeasonProductLocator.getSKUSeasonRev(skuSeasonLink);
				LOGGER.debug(CLASS_NAME + "--" + methodName + "--SKU Name ="
						+ skuObject.getValue(SMFPDColorwayCreateMOAConstants.SKU_NAME));
				// Getting Product Object .
				LCSProduct productObject = SeasonProductLocator.getProductSeasonRev(skuObject);
				LOGGER.debug(CLASS_NAME + "--" + methodName + "--Product Name =" + productObject.getName());
				LOGGER.debug(CLASS_NAME + "--" + methodName + "--skuSeasonLink.isSeasonRemoved() ="
						+ skuSeasonLink.isSeasonRemoved());
				// Check is sku is not removed from the season
				if (!skuSeasonLink.isSeasonRemoved()) {
					// Check for business condition for valid sku-season
					if (validateSKUSeasonLink(skuSeasonLink, productObject)) {
						LOGGER.debug(CLASS_NAME + "--" + methodName + "--Valid SKU Season Link");
						// Get Business Object
						LCSLifecycleManaged businessObject = SMMultiObjectAttributeUtil.findBusinessObjectByName(
								SMFPDColorwayCreateMOAConstants.BO_FPD_CREATE_COLOURWAY_FLEX_TYPE,
								SMFPDColorwayCreateMOAConstants.BO_FPD_CREATE_COLOURWAY_NAME);
						LOGGER.debug(CLASS_NAME + "--" + methodName + "--BusinessObject =" + businessObject.getName());
						// Add a new MOA row
						addNewMOARow(seasonObject, productObject, skuObject, businessObject,
								SMFPDColorwayCreateMOAConstants.MOA_FPD_CREATE_COLOURWAY_FLEX_TYPE);
					}
				} // SKU is removed from the season
				else {
					LOGGER.debug(CLASS_NAME + "--" + methodName + "--Remove Existing MOA Row");
					// Remove existing moa row
					removeExistingMOARow(seasonObject, productObject, skuObject);
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in trackColorwayCreateOnMOA Method -" + e.getMessage());
			e.printStackTrace();
		} catch (WTPropertyVetoException pve) {
			LOGGER.error("WTPropertyVetoException in trackColorwayCreateOnMOA Method -" + pve.getMessage());
			pve.printStackTrace();
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	// Defect Fix: SMPLM-1348 - Start
	/**
	 * Method Name: checkSKUSeasonCancelledOnMOA. Description: Check SKU Season Life
	 * cycle state Cancelled on MOA Table.
	 * 
	 * @param wtObj
	 */
	public static void checkSKUSeasonCancelledOnMOA(WTObject wtObj) {
		String methodName = "checkSKUSeasonCancelledOnMOA()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		try {
			// Check for SKU Object
			if (wtObj instanceof LCSSKU) {
				// Get SKU Object
				LCSSKU skuSeasonRev = (LCSSKU) wtObj;
				// Check for A version of SKU object on cancelled state.
				if ("A".equalsIgnoreCase(skuSeasonRev.getVersionDisplayIdentifier().toString())) {
					return;
				}
				// Get latest iteration.
				skuSeasonRev = (LCSSKU) VersionHelper.latestIterationOf(skuSeasonRev);
				LOGGER.debug(CLASS_NAME + "--" + methodName + "--SKU Name="
						+ skuSeasonRev.getValue(SMFPDColorwayCreateMOAConstants.SKU_NAME));
				// Get LCSSKU Season Link
				LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) SeasonProductLocator
						.getSeasonProductLink(skuSeasonRev);
				// Check for null and Season is NOT removed
				if (skuSeasonLink != null && !skuSeasonLink.isSeasonRemoved()) {
					// Getting Season Object.
					LCSSeason seasonObject = (LCSSeason) VersionHelper
							.latestIterationOf(skuSeasonLink.getSeasonMaster());
					LOGGER.debug(CLASS_NAME + "--" + methodName + "-- Season Name=" + seasonObject.getName());
					// Getting Product Object .
					LCSProduct productSeasonRev = SeasonProductLocator.getProductSeasonRev(skuSeasonRev);
					// Get latest iteration.
					productSeasonRev = (LCSProduct) VersionHelper.latestIterationOf(productSeasonRev);
					LOGGER.debug(CLASS_NAME + "--" + methodName + "-- Product Name=" + productSeasonRev.getName());
					// Getting Current SKU Season Life cycle state.
					String skuLifecycleState = skuSeasonRev.getLifeCycleState().getFullDisplay();
					LOGGER.debug(CLASS_NAME + "--" + methodName + "-- SKU LifecycleState=" + skuLifecycleState);
					// Check for business condition for Cancelled
					if (SMFPDColorwayCreateMOAConstants.LCS_CANCELLED_STATE.equalsIgnoreCase(skuLifecycleState)) {
						LOGGER.debug(CLASS_NAME + "--" + methodName + "--LCS IS CANCELLED");
						LOGGER.debug(CLASS_NAME + "--" + methodName + "--Remove Existing Row");
						// Remove existing moa row
						removeExistingMOARow(seasonObject, productSeasonRev, skuSeasonRev);
					}
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in checkSKUSeasonCancelledOnMOA Method -" + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}
	// Defect Fix: SMPLM-1348 - End

	/**
	 * Method Name: validateSKUSeasonLink.
	 * 
	 * @param skuSeasonLink LCSSKUSeasonLink.
	 * @param productObject LCSProduct.
	 * @return boolean.
	 * @throws WTException.
	 */
	private static boolean validateSKUSeasonLink(LCSSKUSeasonLink skuSeasonLink, LCSProduct productObject)
			throws WTException {
		String methodName = "validateSKUSeasonLink()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Check for new SKU Season Link
		boolean bNewSKUseasonLink = false;
		// Check effect sequence is 0 then its a new link
		if (skuSeasonLink.getEffectSequence() == 0) {
			bNewSKUseasonLink = true;
		}
		// Check for previous skuSeasonLink
		boolean bSKURemovedAndAdded = false;
		// SKU Season Link is old link and was removed and added back
		if (skuSeasonLink.getEffectOutDate() == null && skuSeasonLink.getEffectSequence() > 0) {
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--SKU Season Link was removed and added back");
			// Get previous skuseason link
			LCSSKUSeasonLink priorLink = (LCSSKUSeasonLink) LCSSeasonQuery.getPriorSeasonProductLink(skuSeasonLink);
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--priorLink = " + priorLink);
			// Check previous skuseason link was removed
			if (priorLink.isSeasonRemoved()) {
				bSKURemovedAndAdded = true;
			}
		}
		// Getting Current Product LifeCycle state.
		String prodLifecycleState = productObject.getLifeCycleState().getFullDisplay();
		LOGGER.debug(CLASS_NAME + "--" + methodName + "--Product Lifecycle State =" + prodLifecycleState);
		// Check for life cycle state is not "In Work" and not "AP Approval"
		if (FormatHelper.hasContent(prodLifecycleState) && !"In Work".equalsIgnoreCase(prodLifecycleState)
				&& !"AP Approval".equalsIgnoreCase(prodLifecycleState)
				&& !SMFPDColorwayCreateMOAConstants.LCS_CANCELLED_STATE.equalsIgnoreCase(prodLifecycleState)
				&& (bNewSKUseasonLink || bSKURemovedAndAdded)) {
			LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_END);
			// Sku season link is valid
			return true;
		}
		LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_END);
		// Sku season link is not valid
		return false;
	}

	/**
	 * Method Name: addNewMOARow.
	 * 
	 * @param seasonObject       LCSSeason.
	 * @param productObject      LCSProduct.
	 * @param skuObject          LCSSKU.
	 * @param businessObject
	 * @param strMOAFlexTypePath
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void addNewMOARow(LCSSeason seasonObject, LCSProduct productObject, LCSSKU skuObject,
			LCSLifecycleManaged businessObject, String strMOAFlexTypePath) throws WTException, WTPropertyVetoException {
		String methodName = "addNewMOARow()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		if (!checkMOARowExist(seasonObject, productObject, skuObject)) {
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--MOA row does not exists, hence create a new MOA row.");
			// Create a new MOA row
			createNewMOARow(seasonObject, productObject, skuObject, businessObject, strMOAFlexTypePath);
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * @param seasonObject
	 * @param productObject
	 * @param skuObject
	 * @return
	 * @throws WTException
	 */
	private static boolean checkMOARowExist(LCSSeason seasonObject, LCSProduct productObject, LCSSKU skuObject)
			throws WTException {
		String methodName = "checkMOARowExist()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Initialization
		boolean rowExists = true;
		// Initialization
		Map<String, String> criteriaMap = new HashMap<String, String>();
		// Add Search Criteria
		criteriaMap.put(SMFPDColorwayCreateMOAConstants.MOA_FPD_SEASON_ID,
				String.valueOf(seasonObject.getBranchIdentifier()));
		criteriaMap.put(SMFPDColorwayCreateMOAConstants.MOA_FPD_PRODUCT_ID,
				String.valueOf(productObject.getBranchIdentifier()));
		criteriaMap.put(SMFPDColorwayCreateMOAConstants.MOA_FPD_CW_ID, String.valueOf(skuObject.getBranchIdentifier()));
		// Prepared Query Statement
		PreparedQueryStatement statement = SMMultiObjectAttributeUtil.getMOAPreparedQueryStatement(
				SMFPDColorwayCreateMOAConstants.BO_FPD_CREATE_COLOURWAY_MOA_ATTRIBUTE,
				SMFPDColorwayCreateMOAConstants.MOA_FPD_CREATE_COLOURWAY_FLEX_TYPE, criteriaMap);
		LOGGER.debug(CLASS_NAME + "--" + methodName + "--Prepared Statement: " + statement);
		@SuppressWarnings("unchecked")
		Collection<WTObject> moaRows = LCSQuery.getObjectsFromResults(statement, "OR:com.lcs.wc.moa.LCSMOAObject:",
				"LCSMOAOBJECT.IDA2A2");
		LOGGER.debug(CLASS_NAME + "--" + methodName + "--Collection MOA Rows=" + moaRows.size());
		// Check there is no existing moa row
		if (moaRows.isEmpty() || moaRows.size() == 0) {
			LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
			return false;
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
		return rowExists;
	}

	/**
	 * Method Name: createNewMOARow.
	 * 
	 * @param seasonObject   LCSSeason
	 * @param productObject  LCSProduct
	 * @param skuObject      LCSSKU
	 * @param businessObject
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public static void createNewMOARow(LCSSeason seasonObject, LCSProduct productObject, LCSSKU skuObject,
			LCSLifecycleManaged businessObject, String strMOAFlexTypePath) throws WTPropertyVetoException, WTException {
		String methodName = "createNewMOARow()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Initialization
		Map<String, String> attributeDataMap = new HashMap<String, String>();
		// Setting attributes values
		// Add Season Id
		attributeDataMap.put(SMFPDColorwayCreateMOAConstants.MOA_FPD_SEASON_ID,
				String.valueOf(seasonObject.getBranchIdentifier()));
		// Add Season Name.
		attributeDataMap.put(SMFPDColorwayCreateMOAConstants.MOA_FPD_SEASON_NAME,
				String.valueOf(seasonObject.getValue(SMFPDColorwayCreateMOAConstants.SEASON_NAME)));
		// Get Brand
		String smBrand = SMEmailNotificationUtil.getObjectValue((LCSProduct) productObject,
				LCSProperties.get("com.sportmaster.wc.emailutility.processor.product.lcsproduct.brand"),
				((LCSProduct) productObject).getFlexType());
		// Add Brand
		attributeDataMap.put(SMFPDColorwayCreateMOAConstants.MOA_FPD_BRAND_NAME, smBrand);
		// Check for SKU Object
		if (skuObject != null) {
			// Add SKU Id.
			attributeDataMap.put(SMFPDColorwayCreateMOAConstants.MOA_FPD_CW_ID,
					String.valueOf(skuObject.getBranchIdentifier()));
			// Add SKU Name.
			attributeDataMap.put(SMFPDColorwayCreateMOAConstants.MOA_FPD_CW_NAME,
					String.valueOf(skuObject.getValue(SMFPDColorwayCreateMOAConstants.SKU_NAME)));
		}
		// Add Product Name.
		attributeDataMap.put(SMFPDColorwayCreateMOAConstants.MOA_FPD_PRODUCT_ID,
				String.valueOf(productObject.getBranchIdentifier()));
		// Add Product Id.
		attributeDataMap.put(SMFPDColorwayCreateMOAConstants.MOA_FPD_PRODUCT_NAME,
				String.valueOf(productObject.getName()));
		// Simple Date Formatter
		SimpleDateFormat moscowTime = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
		moscowTime.setTimeZone(TimeZone.getTimeZone(SMFPDColorwayCreateMOAConstants.TIME_ZONE));
		// Add current date and time stamp
		attributeDataMap.put(SMFPDColorwayCreateMOAConstants.MOA_FPD_CREATED_AT,
				moscowTime.format(new java.util.Date()));
		// Calling method to create a MOA row
		SMMultiObjectAttributeUtil.insertMOARow(businessObject,
				SMFPDColorwayCreateMOAConstants.BO_FPD_CREATE_COLOURWAY_MOA_ATTRIBUTE, strMOAFlexTypePath,
				attributeDataMap);
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * Method Name: removeExistingMOARow.
	 * 
	 * @param seasonObject  LCSSeason
	 * @param productObject LCSProduct
	 * @param skuObject     LCSSKU
	 * @throws WTException
	 */
	public static void removeExistingMOARow(LCSSeason seasonObject, LCSProduct productObject, LCSSKU skuObject)
			throws WTException {
		String methodName = "removeExistingMOARow()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Initialization
		Map<String, String> searchMap = new HashMap<String, String>();
		// Add Search Criteria
		searchMap.put(SMFPDColorwayCreateMOAConstants.MOA_FPD_SEASON_ID,
				String.valueOf(seasonObject.getBranchIdentifier()));
		searchMap.put(SMFPDColorwayCreateMOAConstants.MOA_FPD_PRODUCT_ID,
				String.valueOf(productObject.getBranchIdentifier()));
		searchMap.put(SMFPDColorwayCreateMOAConstants.MOA_FPD_CW_ID, String.valueOf(skuObject.getBranchIdentifier()));
		LOGGER.debug(CLASS_NAME + "--" + methodName + "--Search Map=" + searchMap);
		// Prepared Query Statement
		PreparedQueryStatement statement = SMMultiObjectAttributeUtil.getMOAPreparedQueryStatement(
				SMFPDColorwayCreateMOAConstants.BO_FPD_CREATE_COLOURWAY_MOA_ATTRIBUTE,
				SMFPDColorwayCreateMOAConstants.MOA_FPD_CREATE_COLOURWAY_FLEX_TYPE, searchMap);
		LOGGER.debug(CLASS_NAME + "--" + methodName + "--Search Statement: " + statement);
		@SuppressWarnings("unchecked")
		Collection<WTObject> moaRows = LCSQuery.getObjectsFromResults(statement, "OR:com.lcs.wc.moa.LCSMOAObject:",
				"LCSMOAOBJECT.IDA2A2");
		LOGGER.debug(CLASS_NAME + "--" + methodName + "--Remove MOA Rows =" + moaRows.size());
		// Remove all matching MOA Rows
		SMMultiObjectAttributeUtil.deleteMOACollection(moaRows);
		LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_END);
	}

}
