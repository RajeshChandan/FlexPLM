package com.sportmaster.wc.product;

import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.log4j.Logger;

import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.*;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.emailutility.sepd.constants.SMSEPDPSCSCancelledConstants;
import com.sportmaster.wc.emailutility.util.SMEmailNotificationUtil;
import com.sportmaster.wc.emailutility.util.SMMultiObjectAttributeUtil;
import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class SMSEPDProductSKUSeasonPlugin {

	/**
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMSEPDProductSKUSeasonPlugin.class);

	/**
	 * CLASS_NAME.
	 */
	public static final String CLASS_NAME = "SMSEPDProductSKUSeasonPlugin";
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
	protected SMSEPDProductSKUSeasonPlugin() {
		// protected constructor
	}

	/**
	 * Method Name: trackSKUSeasonCancelledOnMOA. Description: Track SKU Season Life
	 * cycle state Cancelled on MOA Table.
	 * 
	 * @param wtObj
	 */
	public static void trackSKUSeasonCancelledOnMOA(WTObject wtObj) {
		String methodName = "trackSKUSeasonCancelledOnMOA()";
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
						+ skuSeasonRev.getValue(SMSEPDPSCSCancelledConstants.SKU_NAME));
				// Get LCSSKU Season Link
				LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) SeasonProductLocator
						.getSeasonProductLink(skuSeasonRev);
				// Check for null and Season is NOT removed
				if (skuSeasonLink != null && !skuSeasonLink.isSeasonRemoved()) {
					// Getting Season Object.
					LCSSeason seasonObject = (LCSSeason) VersionHelper
							.latestIterationOf(skuSeasonLink.getSeasonMaster());
					//RCOM105
					//seasonObject = (LCSSeason) VersionHelper.latestIterationOf(seasonObject);
					LOGGER.debug(CLASS_NAME + "--" + methodName + "--Season Name=" + seasonObject.getName());
					// Getting Product Object .
					LCSProduct productSeasonRev = SeasonProductLocator.getProductSeasonRev(skuSeasonRev);
					// Get latest iteration.
					productSeasonRev = (LCSProduct) VersionHelper.latestIterationOf(productSeasonRev);
					LOGGER.debug(CLASS_NAME + "--" + methodName + "--Product Name=" + productSeasonRev.getName());
					// Getting Current SKU Season Life cycle state.
					String skuLifecycleState = skuSeasonRev.getLifeCycleState().getFullDisplay();
					LOGGER.debug(CLASS_NAME + "--" + methodName + "--skuLifecycleState=" + skuLifecycleState);
					// Check for business condition for Cancelled
					if (SMSEPDPSCSCancelledConstants.LCS_CANCELLED_STATE.equalsIgnoreCase(skuLifecycleState)) {
						// Get Business Object
						LCSLifecycleManaged businessObject = SMMultiObjectAttributeUtil.findBusinessObjectByName(
								SMSEPDPSCSCancelledConstants.BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_FLEX_TYPE,
								SMSEPDPSCSCancelledConstants.BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_NAME);
						LOGGER.debug(CLASS_NAME + "--" + methodName + "--BO Name= " + businessObject.getName());
						// Add a new MOA row
						addNewMOARow(seasonObject, productSeasonRev, skuSeasonRev,
								SMSEPDPSCSCancelledConstants.SKU_LEVEL, businessObject,
								SMSEPDPSCSCancelledConstants.MOA_SEPD_PRODUCT_SKU_SEASON_CANCELLED_FLEX_TYPE);

					} else {
						LOGGER.debug(CLASS_NAME + "--" + methodName + "--LCS IS NOT CANCELLED");
						// If LCS is not cancelled then remove any existing moa rows
						removeExistingMOARow(seasonObject, productSeasonRev, skuSeasonRev,
								SMSEPDPSCSCancelledConstants.SKU_LEVEL);
					}
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in trackSKUSeasonCancelledOnMOA Method -" + e.getMessage());
			e.printStackTrace();
		} catch (WTPropertyVetoException pve) {
			LOGGER.error("WTPropertyVetoException in trackSKUSeasonCancelledOnMOA Method -" + pve.getMessage());
			pve.printStackTrace();
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * Method Name: trackProductSeasonCancelledOnMOA. Description: Track Product
	 * Season Life cycle state Cancelled on MOA Table.
	 * 
	 * @param wtObj
	 */
	public static void trackProductSeasonCancelledOnMOA(WTObject wtObj) {
		String methodName = "trackProductSeasonCancelledOnMOA()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		try {
			// Check for LCSProduct
			if (wtObj instanceof LCSProduct) {
				// Get Product Object
				LCSProduct productSeasonRev = (LCSProduct) wtObj;
				// Check for A version Product object on cancelled state.
				if ("A".equalsIgnoreCase(productSeasonRev.getVersionDisplayIdentifier().toString())) {
					return;
				}
				// Get latest iteration.
				productSeasonRev = (LCSProduct) VersionHelper.latestIterationOf(productSeasonRev);
				LOGGER.debug(CLASS_NAME + "--" + methodName + "--Product Name=" + productSeasonRev.getName());
				// Getting season product link.
				LCSProductSeasonLink productSeasonLink = (LCSProductSeasonLink) SeasonProductLocator
						.getSeasonProductLink(productSeasonRev);
				// Check for null
				if (productSeasonLink != null) {
					// Getting Season Object.
					LCSSeason seasonObject = (LCSSeason) VersionHelper
							.latestIterationOf(productSeasonLink.getSeasonMaster());
					LOGGER.debug(CLASS_NAME + "--" + methodName + "--Season Name=" + seasonObject.getName());
					// Getting Current Product Life cycle state.
					String currrentProdSeasonLCS = productSeasonRev.getLifeCycleState().getFullDisplay();
					LOGGER.debug(CLASS_NAME + "--" + methodName + "--Product-Season Lifecycle State="
							+ currrentProdSeasonLCS);
					// Check for Product Season Life cycle state.
					if (SMSEPDPSCSCancelledConstants.LCS_CANCELLED_STATE.equalsIgnoreCase(currrentProdSeasonLCS)) {
						LOGGER.debug(CLASS_NAME + "--" + methodName + "--LCS IS CANCELLED");
						// Get Business Object
						LCSLifecycleManaged businessObject = SMMultiObjectAttributeUtil.findBusinessObjectByName(
								SMSEPDPSCSCancelledConstants.BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_FLEX_TYPE,
								SMSEPDPSCSCancelledConstants.BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_NAME);
						LOGGER.debug("BO Name =" + businessObject.getName());
						// Add a new MOA row
						addNewMOARow(seasonObject, productSeasonRev, null, SMSEPDPSCSCancelledConstants.PRODUCT_LEVEL,
								businessObject,
								SMSEPDPSCSCancelledConstants.MOA_SEPD_PRODUCT_SKU_SEASON_CANCELLED_FLEX_TYPE);
					} else {
						LOGGER.debug(CLASS_NAME + "--" + methodName + "--LCS IS NOT CANCELLED");
						// If LCS is not cancelled then remove any existing moa rows
						removeExistingMOARow(seasonObject, productSeasonRev, null,
								SMSEPDPSCSCancelledConstants.PRODUCT_LEVEL);
					}
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in trackProductSeasonCancelledOnMOA Method -" + e.getMessage());
			e.printStackTrace();
		} catch (WTPropertyVetoException pve) {
			LOGGER.error("WTPropertyVetoException in trackProductSeasonCancelledOnMOA Method -" + pve.getMessage());
			pve.printStackTrace();
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * Method Name: addNewMOARow. Description: Add new MOA Row.
	 * 
	 * @param seasonObject
	 * @param productObject
	 * @param skuObject
	 * @param strLevel
	 * @param businessObject
	 * @param strMOAFlexTypePath
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void addNewMOARow(LCSSeason seasonObject, LCSProduct productObject, LCSSKU skuObject, String strLevel,
			LCSLifecycleManaged businessObject, String strMOAFlexTypePath) throws WTException, WTPropertyVetoException {
		String methodName = "addNewMOARow()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		if (!checkExistingMOARow(seasonObject, productObject, skuObject, strLevel)) {
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--MOA row does not exists, hence create a new MOA row.");
			// Create a new MOA row
			createNewMOARow(seasonObject, productObject, skuObject, strLevel, businessObject, strMOAFlexTypePath);
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * Method Name: checkExistingMOARow(). Description: Check for existing MOA Row.
	 * 
	 * @param seasonObject
	 * @param productObject
	 * @param skuObject
	 * @param strLevel
	 * @return
	 * @throws WTException
	 */
	private static boolean checkExistingMOARow(LCSSeason seasonObject, LCSProduct productObject, LCSSKU skuObject,
			String strLevel) throws WTException {
		String methodName = "checkExistingMOARow()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Initialization
		boolean rowExists = true;
		// Initialization
		Map<String, String> criteriaMap = new HashMap<String, String>();
		// Add Search Criteria
		criteriaMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_SEASON_ID,
				String.valueOf(seasonObject.getBranchIdentifier()));
		criteriaMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_PRODUCT_ID,
				String.valueOf(productObject.getBranchIdentifier()));
		// Check if SKU Object
		if (skuObject != null) {
			criteriaMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_CW_ID,
					String.valueOf(skuObject.getBranchIdentifier()));
		}
		// Add Level
		criteriaMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_LEVEL, strLevel);
		// Prepared Query Statement
		PreparedQueryStatement statement = SMMultiObjectAttributeUtil.getMOAPreparedQueryStatement(
				SMSEPDPSCSCancelledConstants.BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_MOA_ATTRIBUTE,
				SMSEPDPSCSCancelledConstants.MOA_SEPD_PRODUCT_SKU_SEASON_CANCELLED_FLEX_TYPE, criteriaMap);
		LOGGER.debug(CLASS_NAME + "--" + methodName + "--Prepared Statement: " + statement);
		@SuppressWarnings("unchecked")
		Collection<WTObject> moaRows = LCSQuery.getObjectsFromResults(statement, "OR:com.lcs.wc.moa.LCSMOAObject:",
				"LCSMOAOBJECT.IDA2A2");
		LOGGER.debug(CLASS_NAME + "--" + methodName + "--Collection MOA Rows=" + moaRows.size());
		// Check there for any existing MOA row
		if (moaRows.isEmpty() || moaRows.size() == 0) {
			LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
			return false;
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
		return rowExists;
	}

	/**
	 * Method Name: createNewMOARow(). Description: Create a New MOA Row.
	 * 
	 * @param seasonObject
	 * @param productObject
	 * @param skuObject
	 * @param strLevel
	 * @param businessObject
	 * @param strMOAFlexTypePath
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	public static void createNewMOARow(LCSSeason seasonObject, LCSProduct productObject, LCSSKU skuObject,
			String strLevel, LCSLifecycleManaged businessObject, String strMOAFlexTypePath)
			throws WTPropertyVetoException, WTException {
		String methodName = "createNewMOARow()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Initialization
		Map<String, String> attributeDataMap = new HashMap<String, String>();
		// Setting attributes values
		// Add Season Id
		attributeDataMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_SEASON_ID,
				String.valueOf(seasonObject.getBranchIdentifier()));
		// Add Season Name.
		attributeDataMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_SEASON_NAME,
				String.valueOf(seasonObject.getValue(SMSEPDPSCSCancelledConstants.SEASON_NAME)));
		// Get Brand
		String smBrand = SMEmailNotificationUtil.getObjectValue((LCSProduct) productObject,
				LCSProperties.get("com.sportmaster.wc.emailutility.processor.product.lcsproduct.brand"),
				((LCSProduct) productObject).getFlexType());
		// Add Brand
		attributeDataMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_BRAND_NAME, smBrand);
		// Add Product Name.
		attributeDataMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_PRODUCT_ID,
				String.valueOf(productObject.getBranchIdentifier()));
		// Add Product Id.
		attributeDataMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_PRODUCT_NAME,
				String.valueOf(productObject.getName()));
		// Check for SKU Object
		if (skuObject != null) {
			// Add SKU Id.
			attributeDataMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_CW_ID,
					String.valueOf(skuObject.getBranchIdentifier()));
			// Add SKU Name.
			attributeDataMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_CW_NAME,
					String.valueOf(skuObject.getValue(SMSEPDPSCSCancelledConstants.SKU_NAME)));
		}
		// Simple Date Formatter
		SimpleDateFormat moscowTime = new SimpleDateFormat("dd/MM/yyyy hh.mm aa");
		moscowTime.setTimeZone(TimeZone.getTimeZone(SMSEPDPSCSCancelledConstants.TIME_ZONE));
		// Add current date and time stamp
		attributeDataMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_CREATED_AT, moscowTime.format(new java.util.Date()));
		// Add level
		attributeDataMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_LEVEL, strLevel);
		// Calling method to create a MOA row
		SMMultiObjectAttributeUtil.insertMOARow(businessObject,
				SMSEPDPSCSCancelledConstants.BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_MOA_ATTRIBUTE, strMOAFlexTypePath,
				attributeDataMap);
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * Method Name:removeExistingMOARow(). Description: Remove MOA Rows.
	 * 
	 * @param seasonObject
	 * @param productObject
	 * @param skuObject
	 * @param strLevel
	 * @throws WTException
	 */
	public static void removeExistingMOARow(LCSSeason seasonObject, LCSProduct productObject, LCSSKU skuObject,
			String strLevel) throws WTException {
		String methodName = "removeExistingMOARow()";
		LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_START);
		// Initialization
		Map<String, String> searchMap = new HashMap<String, String>();
		// Add Search Criteria
		searchMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_SEASON_ID,
				String.valueOf(seasonObject.getBranchIdentifier()));
		searchMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_PRODUCT_ID,
				String.valueOf(productObject.getBranchIdentifier()));
		// Check if SKU Object is not null
		if (skuObject != null) {
			searchMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_CW_ID, String.valueOf(skuObject.getBranchIdentifier()));
		}
		// Add Level
		searchMap.put(SMSEPDPSCSCancelledConstants.MOA_SEPD_LEVEL, strLevel);
		LOGGER.debug(CLASS_NAME + "--" + methodName + "--Search Map" + searchMap);
		// Prepared Query Statement
		PreparedQueryStatement statement = SMMultiObjectAttributeUtil.getMOAPreparedQueryStatement(
				SMSEPDPSCSCancelledConstants.BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_MOA_ATTRIBUTE,
				SMSEPDPSCSCancelledConstants.MOA_SEPD_PRODUCT_SKU_SEASON_CANCELLED_FLEX_TYPE, searchMap);
		LOGGER.debug(CLASS_NAME + "--" + methodName + "--Search Statement: " + statement);
		@SuppressWarnings("unchecked")
		Collection<WTObject> moaRows = LCSQuery.getObjectsFromResults(statement, "OR:com.lcs.wc.moa.LCSMOAObject:",
				"LCSMOAOBJECT.IDA2A2");
		LOGGER.debug(CLASS_NAME + "--" + methodName + "--Remove MOA Rows Count =" + moaRows.size());
		// Remove all matching MOA Rows
		SMMultiObjectAttributeUtil.deleteMOACollection(moaRows);
		LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_END);
	}

}
