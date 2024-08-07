package com.sportmaster.wc.product;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.LCSSKUQuery;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import wt.fc.ObjectNoLongerExistsException;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
* SMCascadingAttributes.
* 
* @author 'true' ITC_Infotech
* @version 'true' 1.0
* 
*/
public class SMCascadingAttributes {

/**
 * LOGGER.
 */
public static final Logger LOGGER = Logger.getLogger(SMCascadingAttributes.class);
/**
 * Active Departments.
 */
public static final String ACTIVE_DEPARTMENTS = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.activeDepartments");
/**
 * Product Link Type.
 */
public static final String LINK_TYPE_PRODUCT = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.productSeasonLink");
/**
 * Product Season Life Cycle State CANCELLED.
 */
public static final String PRODUCT_LIFECYCLE_STATE_CANCELLED = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.productLifeCycleStateCancelled");
/**
 * SKU Season Status Key.
 */
public static final String SKU_SEASON_STATUS_KEY = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.skuSeasonStatusKey");
/**
 * Cancelled Status Key.
 */
public static final String CANCELLED_STATUS_KEY = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.cancelledStatusKey");
/**
 * Assortment Plan confirmation.
 */
public static final String ASSORTMENT_PLAN_CONFIRMATION = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.assortmentPlanConfirmation");
/**
 * Initial Allocation approval.
 */
public static final String INITIAL_ALLOCATION_PREPARED_DATE = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.initialAllocationApproval");
/**
 * Product is Prepared for FC1.
 */
public static final String PRODUCT_IS_PREPARED_FOR_FC1 = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.productIsPreparedForFC1");
/**
 * Ready for 1st Forecast.
 */
//public static final String READY_FOR_1ST_FORECAST = LCSProperties
		//.get("com.sportmaster.wc.product.SMCascadingAttributes.readyFor1stForecast");
/**
 * Ready for order.
 */
public static final String READY_FOR_ORDER = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.readyForOrder");
/**
 * Product is Prepared for FC2.
 */
public static final String PRODUCT_IS_PREPARED_FOR_FC2 = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.productIsPreparedForFC2");
/**
 * Ready for 2nd Forecast.
 */
//public static final String READY_FOR_2ND_FORECAST = LCSProperties
		//.get("com.sportmaster.wc.product.SMCascadingAttributes.readyFor2ndForecast");
/**
 * Product Season Status.
 */
public static final String PRODUCT_SEASON_STATUS = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.productSeasonStatus");
/**
 * Approved Status Key.
 */
public static final String APPROVED_STATUS_KEY = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.approvedStatusKey");
/**
 * CW is Prepared for FC1.
 */
public static final String CW_IS_PREPARED_FOR_FC1 = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.cWIsPreparedForFC1");
/**
 * LCS - CW cancelled after.
 */
public static final String CW_READY_FOR_1ST_FORECAST = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.cWReadyFor1stForecast");
/**
 * CW Ready for order.
 */
public static final String CW_READY_FOR_ORDER = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.cWReadyForOrder");
/**
 * CW is Prepared for FC2.
 */
public static final String CW_IS_PREPARED_FOR_FC2 = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.cWIsPreparedForFC2");
/**
 * CW Ready for 2nd Forecast.
 */
public static final String CW_READY_FOR_2ND_FORECAST = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.cWReadyFor2ndForecast");
/**
 * Life Cycle State AP APPROVAL.
 */
public static final String LIFECYCLE_STATE_AP_APPROVAL = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.lcsAPApproval");	
/**
 * Life Cycle State ALLOCATION.
 */
public static final String LIFECYCLE_STATE_ALLOCATION = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.lcsAllocation");
/**
 * Life Cycle State TP DEVELOPMENT.
 */
public static final String LIFECYCLE_STATE_TP_DEVELOPMENT = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.lcsTPDevelopment");
/**
 * Life Cycle State LINE REVIEW.
 */
public static final String LIFECYCLE_STATE_LINE_REVIEW = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.lcsLineReview");		
/**
 * Life Cycle State 1ST FORECAST.
 */
public static final String LIFECYCLE_STATE_1ST_FORECAST = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.lcs1stForecast");		
/**
 * Life Cycle State LINE CLOSE.
 */
public static final String LIFECYCLE_STATE_LINE_CLOSE = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.lcsLineClose");		
/**
 * Life Cycle State 2ND FORECAST.
 */
public static final String LIFECYCLE_STATE_2ND_FORECAST = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.lcs2ndForecast");		
/**
 * Life Cycle State ORDER.
 */
public static final String LIFECYCLE_STATE_ORDER = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.lcsOrder");		
/**
 * InWork Status Key.
 */
public static final String INWORK_STATUS_KEY = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.inWorkStatusKey");	
/**
 * To Integrate.
 */
public static final String TO_INTEGRATE = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.psToIntegrate");
/**
 * Design Brief Accepted.
 */
public static final String DESIGN_BRIEF_ACCEPTED = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.psDesignBriefAccepted");
/**
* LCS cancelled state.
*/
public static final String LCS_CANCELLED_STATE = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingPlugin.lcsCancelledState");

//Phase 14 - SEPD Workflow customization - Enh - Start
/**
 * Product Season Status.
 */
public static final String DESIGN_BRIEF = LCSProperties
		.get("com.sportmaster.wc.product.SMCascadingAttributes.smDesignBrief");
		
/**
 * Constructor. 
 */
protected SMCascadingAttributes() {
	// protected constructor
}

/**
 * Method to set SKU Season status and SKU Season lifecycle state as Cancelled
 * when Product Season lifecycle is Cancelled.
 * 
 * @param WTObject for WTObject.
 * @throws WTException for WTExcpetion.
 */
public static void setSKUStatus(WTObject obj) {
	try {
		if (obj instanceof LCSProduct) {
			LOGGER.debug("#####################SMCascadingAttributes class -   CASCADING ATTRIBUTES PLUGIN START    ###############");

			// Getting Product Object.
			LCSProduct product = (LCSProduct) obj;
			LOGGER.debug("METHOD--setSKUStatus - product="+product.getName());
			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				LCSSeasonProductLink smSeasonProdLink = SeasonProductLocator.getSeasonProductLink(product);
				boolean isDeptvalid = validateDepartment(product);
				LOGGER.debug("METHOD--setSKUStatus - isDeptvalid="+isDeptvalid);
				// creating state object with Cancelled state.
				State cancelledState = State.toState(PRODUCT_LIFECYCLE_STATE_CANCELLED);

				// Checking that link is not null
				processProductLifeCycle(product, smSeasonProdLink, isDeptvalid, cancelledState);
			}
			LOGGER.debug("#####################   CASCADING ATTRIBUTES PLUGIN END    ###############");
		}
	} catch (WTException wtExp) {
		LOGGER.error(wtExp.getLocalizedMessage());
		wtExp.printStackTrace();
	}
}

/**
 * @param product
 * @param smSeasonProdLink
 * @param isDeptvalid
 * @param cancelledState
 * @throws WTException
 * @throws LifeCycleException
 * @throws ObjectNoLongerExistsException
 */
public static void processProductLifeCycle(LCSProduct product, LCSSeasonProductLink smSeasonProdLink,
		boolean isDeptvalid, State cancelledState)
		throws WTException, LifeCycleException, ObjectNoLongerExistsException {
	if (smSeasonProdLink != null && LINK_TYPE_PRODUCT.equalsIgnoreCase(smSeasonProdLink.getType()) && isDeptvalid) {

		LOGGER.debug("Season Product Link >>>>>>>>>   " + smSeasonProdLink);
		// LOGGER.debug("Effective sequence >>>>>>>>>
		// "+smSeasonProdLink.getEffectSequence());

		// Getting season.
		LCSSeason smSeason = SeasonProductLocator.getSeasonRev(smSeasonProdLink);
		// Season name.
		LOGGER.debug("SEASON NAME  >>>>>>>>>  " + smSeason.getName());

		// Getting product lifecycle state.
		String prodLifeCycleState = product.getLifeCycleState().toString();

		LOGGER.debug("PRODUCT SEASON LIFECYCLE STATE  >>>>>>>>>>>   " + prodLifeCycleState);

		processColorway(product, cancelledState, prodLifeCycleState);
	}
}

/**
 * gets valid SKUs and sets value/state.
 * 
 * @param product            - LCSProduct.
 * @param cancelledState     - State.
 * @param prodLifeCycleState - String.
 * @throws WTException                   - WTException.
 * @throws LifeCycleException            - LifeCycleException.
 * @throws ObjectNoLongerExistsException - ObjectNoLongerExistsException.
 */
public static void processColorway(LCSProduct product, State cancelledState, String prodLifeCycleState)
		throws WTException, LifeCycleException, ObjectNoLongerExistsException {
	// Getting Colorways for Product
	List<?> smSKUList = (List<?>) LCSSKUQuery.findSKUs(product);
	LOGGER.debug("METHOD--processColorway - smSKUList="+smSKUList);
	LCSSKU smSKU;
	LCSSKUSeasonLink skuSeasonLink;
	if (smSKUList != null && smSKUList.size() > 0) {
		// Number of Colorways associated to product
		LOGGER.debug("SKU List Size >>>>>>>   " + smSKUList.size());
		for (Object skuObj : smSKUList) {
			LOGGER.debug("***************************");
			smSKU = (LCSSKU) skuObj;
			LOGGER.debug("METHOD--processColorway - smSKU name="+smSKU.getName());
			// Getting SKU Season Link
			skuSeasonLink = (LCSSKUSeasonLink) LCSSeasonQuery.findSeasonProductLink(smSKU.getMaster(),
					smSKU.getSeasonMaster());

			// Setting SKU Season Status as Cancelled
			// if product lifecycle State is cancelled
			// Also checking if SKU is not removed from season.
			if (PRODUCT_LIFECYCLE_STATE_CANCELLED.equalsIgnoreCase(prodLifeCycleState)
					&& !skuSeasonLink.isSeasonRemoved()) {
				LOGGER.debug("\n#################################################\n");
				LOGGER.debug("METHOD--processColorway - setting SKU season status as="+CANCELLED_STATUS_KEY);
				// Setting SKU Season Status as cancelled
				skuSeasonLink.setValue(SKU_SEASON_STATUS_KEY, CANCELLED_STATUS_KEY);
				LOGGER.debug("METHOD--processColorway - setting SKU LC State as="+cancelledState);
				// Setting SKU Season Lifecycle state as cancelled.
				LifeCycleHelper.service.setLifeCycleState(smSKU, cancelledState);
				try {
					LOGGER.debug("METHOD--processColorway - PRODUCT_LIFECYCLE_STATE_CANCELLED="+PRODUCT_LIFECYCLE_STATE_CANCELLED);
					skuSeasonLink.setSkuState(PRODUCT_LIFECYCLE_STATE_CANCELLED);
				} catch (WTPropertyVetoException wtPve) {
					LOGGER.error(wtPve.getLocalizedMessage());
					wtPve.printStackTrace();
				}

				// PersistenceHelper.manager.refresh(smSKU);

				// using persist API to prevent plugin from re-triggering.
				LCSLogic.persist(skuSeasonLink, true);

				LOGGER.debug("Successfully set Cancelled status for Status CW Seasonal for  :  " + smSKU.getName());
				LOGGER.debug("\n#################################################\n");
			}
		}
	}
}

/**
 * Validates active department.
 * 
 * @param product - LCSProduct.
 */
public static boolean validateDepartment(LCSProduct product) {
	String productDept = product.getFlexType().getFullName();
	List<String> activeDeptList = FormatHelper.commaSeparatedListToList(ACTIVE_DEPARTMENTS);
	for (String currentDept : activeDeptList) {
		if (productDept.startsWith(currentDept)) {
			return true;
		}
	}
	return false;
}

/**
 * setCurrentDate.
 * 
 * @return Date
 */
public static Date setCurrentDate() {
	// getting MSK timezone
	TimeZone tz = TimeZone.getTimeZone(LCSProperties.get("com.lcs.wc.util.FormatHelper.STANDARD_TIMEZONE"));
	// Fix for SMPLM-1316 (to set right date) - Start
	/*
	SimpleDateFormat converter = new SimpleDateFormat("dd/MM/yyyy");
	converter.setTimeZone(tz);
	converter.format(currentDate);
	System.out.println("\n\ncurrentDate="+currentDate);
	*/
	Calendar cal = Calendar.getInstance(tz);
	LOGGER.debug("Calendar time before offset="+cal.getTime());
	long date = cal.getTimeInMillis();
	int offset = cal.getTimeZone().getOffset(date);
	cal.setTimeInMillis(date + offset);
	LOGGER.debug("Calendar time after offset="+cal.getTime());
	return cal.getTime();	
	//return currentDate;
	// Fix for SMPLM-1316 (to set right date) - End
}

/**
 * setCurrentDateToProductSeasonAttributes.
 * 
 * @param seasonProductLink for seasonProductLink.
 * @param psVariable        for psVariable.
 * @param statusKey         for statusKey.
 * @param statusValue       for statusValue.
 * @return void.
 */
public static void setCurrentDateToProductSeasonAttributes(LCSSeasonProductLink seasonProductLink,
		String psVariable, String statusKey, String statusValue) {
	try {
		if (seasonProductLink != null) {
			if (FormatHelper.hasContent(statusKey)) {
				// Setting attribute Status key to approved.
				seasonProductLink.setValue(statusKey, statusValue);
			}
			// Setting attribute to First task completion date.
			if (seasonProductLink.getValue(psVariable) == null) {
				seasonProductLink.setValue(psVariable, setCurrentDate());
			}
			LCSLogic.persist(seasonProductLink, false);
		}
	} catch (WTPropertyVetoException e) {
		LOGGER.error(
				"WTPropertyVetoException in setCurrentDateToProductSeasonAttributes method: " + e.getMessage());
		e.printStackTrace();
	} catch (WTException e) {
		LOGGER.error("WTException in setCurrentDateToProductSeasonAttributes method: " + e.getMessage());
		e.printStackTrace();
	}
}

/**
 * setCurrentDateToColorwaySeasonAttributes.
 * 
 * @param skuSeasonLink for skuSeasonLink.
 * @param cwVariable    for cwVariable.
 * @param cwStatusKey   for cwStatusKey.
 * @param cwStatusValue for cwStatusValue.
 * @return void.
 */
public static void setCurrentDateToColorwaySeasonAttributes(LCSSKUSeasonLink skuSeasonLink, String cwVariable,
		String cwStatusKey, String cwStatusValue) {
	try {
		if (skuSeasonLink != null) {
			if (FormatHelper.hasContent(cwStatusKey)) {
				// Setting attribute Status key to approved.
				skuSeasonLink.setValue(cwStatusKey, cwStatusValue);
			}
			// Setting attribute to First task completion date.
			if (FormatHelper.hasContent(cwVariable) && skuSeasonLink.getValue(cwVariable) == null) {
				skuSeasonLink.setValue(cwVariable, setCurrentDate());
			}
			LCSLogic.persist(skuSeasonLink, false);
		}
	} catch (WTPropertyVetoException e) {
		LOGGER.error(
				"WTPropertyVetoException in setCurrentDateToColorwaySeasonAttributes method: " + e.getMessage());
		e.printStackTrace();
	} catch (WTException e) {
		LOGGER.error("WTException in setCurrentDateToColorwaySeasonAttributes method: " + e.getMessage());
		e.printStackTrace();
	}
}

/**
 * approveAssortment.
 * 
 * @param object for object.
 * @return void.
 */
public static void approveAssortment(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--approveAssortment");
	try {
		if (object instanceof LCSProduct) {

			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				LCSSeasonProductLink seasonProductLink = SeasonProductLocator.getSeasonProductLink(product);
				// Setting values to Product Season attributes.
				setCurrentDateToProductSeasonAttributes(seasonProductLink, ASSORTMENT_PLAN_CONFIRMATION,
						PRODUCT_SEASON_STATUS, APPROVED_STATUS_KEY);
			}
		}
		if (object instanceof LCSSKU) {
			// Getting SKU Object.
			LCSSKU skuObj = (LCSSKU) object;

			// Getting SKU season link.
			LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) SeasonProductLocator.getSeasonProductLink(skuObj);
			// Setting values to Colorway Season attributes.
			setCurrentDateToColorwaySeasonAttributes(skuSeasonLink, "", SKU_SEASON_STATUS_KEY, APPROVED_STATUS_KEY);
		}
	} catch (WTException e) {
		LOGGER.error("WTException in approveAssortment method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--approveAssortment");
}

/**
 * approveAllocation.
 * 
 * @param object for object.
 * @return void.
 */
public static void approveAllocation(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--approveAllocation");
	try {
		if (object instanceof LCSProduct) {

			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				LCSSeasonProductLink seasonProductLink = SeasonProductLocator.getSeasonProductLink(product);
				// Phase 11 - Renamed INITIAL_ALLOCATION_APPROVAL to INITIAL_ALLOCATION_PREPARED_DATE
				// Setting values to Product Season attributes.
				setCurrentDateToProductSeasonAttributes(seasonProductLink, INITIAL_ALLOCATION_PREPARED_DATE, "", "");
			}
		}
	} catch (WTException e) {
		LOGGER.error("WTException in approveAllocation method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--approveAllocation");
}

/**
 * approveAPAfterLR.
 * Phase 11 - If existing Product/Colorway is in Task# 8 which holds old Workflow template requires this method. On approval of this task, it calls this 
method and then set its LF state to 1st Forecast (where it takes new Workflow template). For Phase 11, Task# 8 is removed, and this function is not called 
from any of the Workflow Tasks.
 *
 * @param object for object.
 * @return void.
 */
public static void approveAPAfterLR(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--approveAPAfterLR");
	LOGGER.debug("Inside CLASS--SMCascadingAttributes and METHOD--approveAPAfterLR object="+object);
	/*try {
		if (object instanceof LCSProduct) {

			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				LCSSeasonProductLink seasonProductLink = SeasonProductLocator.getSeasonProductLink(product);
				// Setting values to Product Season attributes.
				//setCurrentDateToProductSeasonAttributes(seasonProductLink, READY_FOR_1ST_FORECAST,
					//  PRODUCT_SEASON_STATUS, APPROVED_STATUS_KEY);
			}
		}
		if (object instanceof LCSSKU) {
			// Getting SKU Object.
			LCSSKU skuObj = (LCSSKU) object;

			// Getting SKU season link.
			LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) SeasonProductLocator.getSeasonProductLink(skuObj);
			// Setting values to Colorway Season attributes.
			//setCurrentDateToColorwaySeasonAttributes(skuSeasonLink, CW_READY_FOR_1ST_FORECAST,
					//SKU_SEASON_STATUS_KEY, APPROVED_STATUS_KEY);
		}
	} catch (WTException e) {
		LOGGER.error("WTException in approveAPAfterLR method: " + e.getMessage());
		e.printStackTrace();
	}*/
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--approveAPAfterLR");
}


/**
 * approveAPAfterLC.
 * Phase 11 - If existing Product/Colorway is in Task# 12 which holds old Workflow template requires this method. On approval of this task, it calls this 
method and then set its LF state to 2nd Forecast (where it takes new Workflow template). For Phase 11, Task 12 is removed, and this function is not called 
from any of the new Workflow Tasks.
 *
 * @param object for object.
 * @return void.
 */
public static void approveAPAfterLC(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--approveAPAfterLC");
	LOGGER.debug("Inside CLASS--SMCascadingAttributes and METHOD--approveAPAfterLC object="+object);
	/*try {
		if (object instanceof LCSProduct) {

			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				LCSSeasonProductLink seasonProductLink = SeasonProductLocator.getSeasonProductLink(product);
				// Setting values to Product Season attributes.
				//setCurrentDateToProductSeasonAttributes(seasonProductLink, READY_FOR_2ND_FORECAST,
						//PRODUCT_SEASON_STATUS, APPROVED_STATUS_KEY);
			}
		}
		if (object instanceof LCSSKU) {
			// Getting SKU Object.
			LCSSKU skuObj = (LCSSKU) object;

			// Getting SKU season link.
			LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) SeasonProductLocator.getSeasonProductLink(skuObj);
			// Setting values to Colorway Season attributes.
			//setCurrentDateToColorwaySeasonAttributes(skuSeasonLink, CW_READY_FOR_2ND_FORECAST,
					//SKU_SEASON_STATUS_KEY, APPROVED_STATUS_KEY);
		}
	} catch (WTException e) {
		LOGGER.error("WTException in approveAPAfterLC method: " + e.getMessage());
		e.printStackTrace();
	}*/
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--approveAPAfterLC");
}

/**
 * checkBuyReadyForSupplier.
 * 
 * @param object for object.
 * @return void.
 */
public static void checkBuyReadyForSupplier(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--checkBuyReadyForSupplier");
	try {
		if (object instanceof LCSProduct) {

			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				LCSSeasonProductLink seasonProductLink = SeasonProductLocator.getSeasonProductLink(product);
				// Setting values to Product Season attributes.
				setCurrentDateToProductSeasonAttributes(seasonProductLink, READY_FOR_ORDER, "", "");
			}
		}
		if (object instanceof LCSSKU) {
			// Getting SKU Object.
			LCSSKU skuObj = (LCSSKU) object;

			// Getting SKU season link.
			LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) SeasonProductLocator.getSeasonProductLink(skuObj);
			// Setting values to Colorway Season attributes.
			setCurrentDateToColorwaySeasonAttributes(skuSeasonLink, CW_READY_FOR_ORDER, "", "");
		}
	} catch (WTException e) {
		LOGGER.error("WTException in checkBuyReadyForSupplier method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--checkBuyReadyForSupplier");
}

/**
 * confirmPrepareAssortmentForFC1.
 * 
 * @param object for object.
 * @return void.
 */
public static void confirmPrepareAssortmentForFC1(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--confirmPrepareAssortmentForFC1");
	try {
		if (object instanceof LCSProduct) {

			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				LCSSeasonProductLink seasonProductLink = SeasonProductLocator.getSeasonProductLink(product);
				// Phase 11 - Setting "Status-Style Seasonal" attribute Value to "Approved"
				// Setting values to Product Season attributes.
				setCurrentDateToProductSeasonAttributes(seasonProductLink, PRODUCT_IS_PREPARED_FOR_FC1, PRODUCT_SEASON_STATUS, APPROVED_STATUS_KEY);
			}
		}
		if (object instanceof LCSSKU) {
			// Getting SKU Object.
			LCSSKU skuObj = (LCSSKU) object;

			// Getting SKU season link.
			LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) SeasonProductLocator.getSeasonProductLink(skuObj);
			// Phase 11 - Set "Status-CW Seasonal" attribute Value to "Approved"
			// Setting values to Colorway Season attributes.
			setCurrentDateToColorwaySeasonAttributes(skuSeasonLink, CW_IS_PREPARED_FOR_FC1, SKU_SEASON_STATUS_KEY, APPROVED_STATUS_KEY);
		}
	} catch (WTException e) {
		LOGGER.error("WTException in confirmPrepareAssortmentForFC1 method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--confirmPrepareAssortmentForFC1");
}

/**
 * confirmPrepareAssortmentForFC2.
 * 
 * @param object for object.
 * @return void.
 */
public static void confirmPrepareAssortmentForFC2(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--confirmPrepareAssortmentForFC2");
	try {
		if (object instanceof LCSProduct) {

			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				LCSSeasonProductLink seasonProductLink = SeasonProductLocator.getSeasonProductLink(product);
				// Phase 11 - Setting "Status-Style Seasonal" attribute Value to "Approved"
				// Setting values to Product Season attributes.
				setCurrentDateToProductSeasonAttributes(seasonProductLink, PRODUCT_IS_PREPARED_FOR_FC2, PRODUCT_SEASON_STATUS, APPROVED_STATUS_KEY);
			}
		}
		if (object instanceof LCSSKU) {
			// Getting SKU Object.
			LCSSKU skuObj = (LCSSKU) object;

			// Getting SKU season link.
			LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) SeasonProductLocator.getSeasonProductLink(skuObj);
			// Phase 11 - Setting "Status-CW Seasonal" attribute Value to "Approved"
			// Setting values to Colorway Season attributes.
			setCurrentDateToColorwaySeasonAttributes(skuSeasonLink, CW_IS_PREPARED_FOR_FC2, SKU_SEASON_STATUS_KEY, APPROVED_STATUS_KEY);
		}
	} catch (WTException e) {
		LOGGER.error("WTException in confirmPrepareAssortmentForFC2 method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--confirmPrepareAssortmentForFC2");
}

/**
 * cancelStatusAttribute.
 * 
 * @param object for object.
 * @return void.
 */
public static void cancelStatusAttribute(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--cancelStatusAttribute");
	try {
		if (object instanceof LCSProduct) {
			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				LCSSeasonProductLink seasonProductLink = SeasonProductLocator.getSeasonProductLink(product);
				// Setting Cancel state to Product Season LCS.
				setCancelledStatus(seasonProductLink);
			}
		}
		if (object instanceof LCSSKU) {
			// Getting SKU Object.
			LCSSKU skuObj = (LCSSKU) object;

			// Getting SKU season link.
			LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) SeasonProductLocator.getSeasonProductLink(skuObj);

			if (skuSeasonLink != null && !SKU_SEASON_STATUS_KEY.equals(CANCELLED_STATUS_KEY)) {
				// Setting attribute Status-CW Seasonal to cancelled.
				skuSeasonLink.setValue(SKU_SEASON_STATUS_KEY, CANCELLED_STATUS_KEY);
				LCSLogic.persist(skuSeasonLink, false);
			}
		}
	} catch (WTException e) {
		LOGGER.error("WTException in cancelStatusAttribute method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--cancelStatusAttribute");
}

/**
 * setCancelledStatus.
 * 
 * @param seasonProductLink for seasonProductLink.
 * @return void.
 */
public static void setCancelledStatus(LCSSeasonProductLink seasonProductLink) {
	try {
		if (seasonProductLink != null && !PRODUCT_SEASON_STATUS.equals(CANCELLED_STATUS_KEY)) {
			// Setting attribute Status-Style Seasonal to cancelled.
			seasonProductLink.setValue(PRODUCT_SEASON_STATUS, CANCELLED_STATUS_KEY);
			LCSLogic.persist(seasonProductLink, false);
		}
	} catch (WTException e) {
		LOGGER.error("WTException in setCancelledStatus method: " + e.getMessage());
		e.printStackTrace();
	}
}
	
/**
 * checkColorwayLCS.
 * 
 * @param object for object.
 * @return void.
 */
public static void checkColorwayLCS(WTObject object) {
	try {
		if (object instanceof LCSSKU) {
			// Getting SKU Object.
			LCSSKU skuObj = (LCSSKU) object;

			// Getting product Object.
			LCSProduct product = SeasonProductLocator.getProductSeasonRev(skuObj);

			// Getting current Product Lifecycle state.
			String currentProductLCS = product.getState().getState().toString();

			// creating state object with Product Season Lifecycle state.
			State productState = State.toState(currentProductLCS);

			// Bypass the security to change state as the users usually don't have access to
			// Change State from UI
			final boolean old_enforced = SessionServerHelper.manager.setAccessEnforced(false);
			try {
				// Setting Product Season Lifecycle state to SKU Season Lifecycle state.
				LifeCycleHelper.service.setLifeCycleState(skuObj, productState);
			} finally {
				SessionServerHelper.manager.setAccessEnforced(old_enforced);
			}
		}
	} catch (WTException e) {
		LOGGER.error("WTException in checkColorwayLCS method: " + e.getMessage());
		e.printStackTrace();
	}
}

// Phase 11 - SEPD Workflow customization - Start

/**
 * setSEPDProductSeasonAttributes.
 * 
 * @param seasonProductLink for seasonProductLink.
 * @param statusKey         for statusKey.
 * @param statusValue       for statusValue.
 * @return void.
 */
public static void setSEPDProductSeasonAttributes(LCSSeasonProductLink seasonProductLink, String key, Object objectValue) {
	LOGGER.debug("start - Inside CLASS--SMCascadingAttributes and METHOD--setSEPDProductSeasonAttributes");
	try {
		if (seasonProductLink != null) {
			if (FormatHelper.hasContent(key)) {
				// Setting attribute Status key to approved.
				LOGGER.debug("key="+key);
				LOGGER.debug("objectValue="+objectValue);
				seasonProductLink.setValue(key, objectValue);
			}
		}
	} catch (WTException e) {
		LOGGER.error("WTException in setSEPDProductSeasonAttributes method: " + e.getMessage());
		e.printStackTrace();
	} catch (WTPropertyVetoException e) {
		LOGGER.error("WTPropertyVetoException in setSEPDProductSeasonAttributes method: " + e.getMessage());
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	LOGGER.debug("end - Inside CLASS--SMCascadingAttributes and METHOD--setSEPDProductSeasonAttributes");
}

/**
	 * setSKUsLCState.
	 * 
	 * @param skuCollection for skuCollection.
	 * @return void.
	 */
	public static void setSKUsLCState(final List<LCSSKU> skuCollection, String currentLCSProduct) {
		LOGGER.debug("start - Inside CLASS--SMCascadingAttributes and METHOD--setSKUsLCState");
		try {
			String currentLCSCW;
			LCSSKU skuObj;
			LCSSKUSeasonLink skuSeasonLink;
			if (skuCollection != null && skuCollection.size() > 0) {

				for (Object obj : skuCollection) {
					// Getting sku Object.
					skuObj = (LCSSKU) obj;

					// Getting Current Colorway Season Lifecycle state.
					currentLCSCW = skuObj.getLifeCycleState().getFullDisplay();
					LOGGER.debug("currentLCSCW="+currentLCSCW);
					// Getting sku Season Link
					skuSeasonLink = (LCSSKUSeasonLink) LCSSeasonQuery.findSeasonProductLink(skuObj.getMaster(),
							skuObj.getSeasonMaster()); 

					if (!currentLCSCW.equalsIgnoreCase(LCS_CANCELLED_STATE)) {
						
						// creating state object with Cancelled state.
						State productLCS = State.toState(currentLCSProduct);
						LOGGER.debug("productLCS="+productLCS);
						// Bypass the security to change state as the users usually don't have access to
						// Change State from UI
						final boolean old_enforced = SessionServerHelper.manager.setAccessEnforced(false);
						try {
							// Setting SKU Season Lifecycle state as cancelled.
							LifeCycleHelper.service.setLifeCycleState(skuObj, productLCS, true);
						} finally {
							SessionServerHelper.manager.setAccessEnforced(old_enforced);
						}
					}
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in setSKUsLCState Method -" + e.getMessage());
			e.printStackTrace();
		} 
		LOGGER.debug("end - Inside CLASS--SMCascadingAttributes and METHOD--setSKUsLCState");
	}

/**
 * confirmPrepareAssortmentForTOPCalculation.
 * 
 * @param object for object.
 * @return void.
 */
public static void confirmPrepareAssortmentForTOPCalculation(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--confirmPrepareAssortmentForTOPCalculation");
	try {
		if (object instanceof LCSProduct) {
			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Setting AP Approval state to Colorways associated to Product.
				final List<LCSSKU> skuCollection = (List<LCSSKU>) LCSSKUQuery.findSKUs(product);
				LOGGER.debug("skuCollection="+skuCollection);
				setSKUsLCState(skuCollection, LIFECYCLE_STATE_AP_APPROVAL);
			}
		}
	} catch (WTException e) {
		LOGGER.error("WTException in confirmPrepareAssortmentForTOPCalculation method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--confirmPrepareAssortmentForTOPCalculation");
}

/**
 * approveHandoverAssortmentToTeam.
 * 
 * @param object for object.
 * @return void.
 */
public static void approveHandoverAssortmentToTeam(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--approveHandoverAssortmentToTeam");
	try {
		if (object instanceof LCSProduct) {
			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				LCSSeasonProductLink seasonProductLink = SeasonProductLocator.getSeasonProductLink(product);
				LOGGER.debug("PRODUCT_SEASON_STATUS="+seasonProductLink.getValue(PRODUCT_SEASON_STATUS));
				// Setting values to Product Season attributes.
				setSEPDProductSeasonAttributes(seasonProductLink, PRODUCT_SEASON_STATUS, APPROVED_STATUS_KEY);
				LCSLogic.persist(seasonProductLink, false);
				LOGGER.debug("PRODUCT_SEASON_STATUS after change="+seasonProductLink.getValue(PRODUCT_SEASON_STATUS));
				final List<LCSSKU> skuCollection = (List<LCSSKU>) LCSSKUQuery.findSKUs(product);
				setSKUsLCState(skuCollection, LIFECYCLE_STATE_ALLOCATION);
			}		
		}  
	} catch (WTException e) {
		LOGGER.error("WTException in approveHandoverAssortmentToTeam method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--approveHandoverAssortmentToTeam");
}

/**
 * updateHandoverAssortmentToTeam.
 * 
 * @param object for object.
 * @return void.
 * @throws WTPropertyVetoException 
 */
public static void updateHandoverAssortmentToTeam(WTObject object) throws WTPropertyVetoException {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--updateHandoverAssortmentToTeam");
	try {
		if (object instanceof LCSProduct) {
			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;
			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				LCSProductSeasonLink productSeasonLink = (LCSProductSeasonLink) SeasonProductLocator.getSeasonProductLink(product);
				// Setting values to Product Season attributes.
				setSEPDProductSeasonAttributes(productSeasonLink, PRODUCT_SEASON_STATUS, INWORK_STATUS_KEY);
				
				setSEPDProductSeasonAttributes(productSeasonLink, TO_INTEGRATE, true);
				LOGGER.debug(" TO INTEGRATE -before object persist--"+productSeasonLink.getValue(TO_INTEGRATE));
				// Setting skip plugin as true and explicitly calling Handle Post Persist API as skip plugin did not trigger the SSP's
				LCSLogic.deriveFlexTypeValues(productSeasonLink, true);
				LCSLogic.persist(productSeasonLink, true);
				LOGGER.debug(" TO INTEGRATE -after object persist--"+productSeasonLink.getValue(TO_INTEGRATE));
				LCSLogic.handlePostPersist(productSeasonLink, false); 
				LOGGER.debug(" TO INTEGRATE -after handle post persist--"+productSeasonLink.getValue(TO_INTEGRATE));
				
			}		
		} 
	} catch (WTException e) { 
		LOGGER.error("WTException in updateHandoverAssortmentToTeam method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--updateHandoverAssortmentToTeam");
}

/**
 * approveReviewDesignBrief. 
 * 
 * @param object for object.
 * @return void.
 */
public static void approveReviewDesignBrief(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--approveReviewDesignBrief");
	try {
		if (object instanceof LCSProduct) {
			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				LCSSeasonProductLink seasonProductLink = SeasonProductLocator.getSeasonProductLink(product);
				setSEPDProductSeasonAttributes(seasonProductLink, DESIGN_BRIEF_ACCEPTED, true);
				LCSLogic.persist(seasonProductLink, false);
			}		
		}
	} catch (WTException e) {
		LOGGER.error("WTException in approveReviewDesignBrief method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--approveReviewDesignBrief");
}

/**
 * completePrepareInitialAllocation.
 * 
 * @param object for object.
 * @return void.
 */
public static void completePrepareInitialAllocation(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--completePrepareInitialAllocation");
	try {
		if (object instanceof LCSProduct) {
			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				
				final List<LCSSKU> skuCollection = (List<LCSSKU>) LCSSKUQuery.findSKUs(product);
				setSKUsLCState(skuCollection, LIFECYCLE_STATE_TP_DEVELOPMENT);
			}		
		}
	} catch (WTException e) {
		LOGGER.error("WTException in completePrepareInitialAllocation method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--completePrepareInitialAllocation");
}

/**
 * completeHandoverApprovedPPToBMForLineReview.
 * 
 * @param object for object.
 * @return void.
 */
public static void completeHandoverApprovedPPToBMForLineReview(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--completeHandoverApprovedPPToBMForLineReview");
	try {
		if (object instanceof LCSProduct) {
			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				
				final List<LCSSKU> skuCollection = (List<LCSSKU>) LCSSKUQuery.findSKUs(product);
				setSKUsLCState(skuCollection, LIFECYCLE_STATE_LINE_REVIEW);
			}		
		}
	} catch (WTException e) {
		LOGGER.error("WTException in completeHandoverApprovedPPToBMForLineReview method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--completeHandoverApprovedPPToBMForLineReview");
}

/**
 * approvePrepareAssortmentForTheNextStage.
 * 
 * @param object for object.
 * @return void.
 */
public static void approvePrepareAssortmentForTheNextStage(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--approvePrepareAssortmentForTheNextStage");
	try {
		if (object instanceof LCSProduct) {
			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				LCSSeasonProductLink seasonProductLink = SeasonProductLocator.getSeasonProductLink(product);
				// Setting values to Product Season attributes.
				setSEPDProductSeasonAttributes(seasonProductLink, PRODUCT_SEASON_STATUS, APPROVED_STATUS_KEY);
				LCSLogic.persist(seasonProductLink, false);
				
				final List<LCSSKU> skuCollection = (List<LCSSKU>) LCSSKUQuery.findSKUs(product);
				setSKUsLCState(skuCollection, LIFECYCLE_STATE_1ST_FORECAST);
			}		
		}
	} catch (WTException e) {
		LOGGER.error("WTException in approvePrepareAssortmentForTheNextStage method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--approvePrepareAssortmentForTheNextStage");
}

/**
 * completeHandoverApprovedPPToBMForLineClose.
 * 
 * @param object for object.
 * @return void.
 */
public static void completeHandoverApprovedPPToBMForLineClose(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--completeHandoverApprovedPPToBMForLineClose");
	try {
		if (object instanceof LCSProduct) {
			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				
				final List<LCSSKU> skuCollection = (List<LCSSKU>) LCSSKUQuery.findSKUs(product);
				setSKUsLCState(skuCollection, LIFECYCLE_STATE_LINE_CLOSE);
			}		
		}
	} catch (WTException e) {
		LOGGER.error("WTException in completeHandoverApprovedPPToBMForLineClose method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--completeHandoverApprovedPPToBMForLineClose");
}


/**
 * approvePrepareAssortmentForTheNextStage2ndForecast.
 * 
 * @param object for object.
 * @return void.
 */
public static void approvePrepareAssortmentForTheNextStage2ndForecast(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--approvePrepareAssortmentForTheNextStage2ndForecast");
	try {
		if (object instanceof LCSProduct) {
			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				LCSSeasonProductLink seasonProductLink = SeasonProductLocator.getSeasonProductLink(product);
				// Setting values to Product Season attributes.
				setSEPDProductSeasonAttributes(seasonProductLink, PRODUCT_SEASON_STATUS, APPROVED_STATUS_KEY);
				LCSLogic.persist(seasonProductLink, false);
				
				final List<LCSSKU> skuCollection = (List<LCSSKU>) LCSSKUQuery.findSKUs(product);
				setSKUsLCState(skuCollection, LIFECYCLE_STATE_2ND_FORECAST);
			}		
		}
	} catch (WTException e) {
		LOGGER.error("WTException in approvePrepareAssortmentForTheNextStage2ndForecast method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--approvePrepareAssortmentForTheNextStage2ndForecast");
}

/**
 * completeConfirmDevelopmentStageIsCompleted.
 * 
 * @param object for object.
 * @return void.
 */
public static void completeConfirmDevelopmentStageIsCompleted(WTObject object) {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--completeConfirmDevelopmentStageIsCompleted");
	try {
		if (object instanceof LCSProduct) {
			// Getting Product Object. 
			LCSProduct product = (LCSProduct) object;

			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {		
				final List<LCSSKU> skuCollection = (List<LCSSKU>) LCSSKUQuery.findSKUs(product);
				setSKUsLCState(skuCollection, LIFECYCLE_STATE_ORDER);
			}		
		}
	} catch (WTException e) {
		LOGGER.error("WTException in completeConfirmDevelopmentStageIsCompleted method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--completeConfirmDevelopmentStageIsCompleted");
}
//Phase 11 - SEPD Workflow customization - Ends

//Phase 14 - SEPD Workflow customization - Enh - Start
/**
 * updateDesignBrief.
 * 
 * @param object for object.
 * @param bHandOverDesignBrief
 * @return void.
 * @throws WTPropertyVetoException 
 */
public static void updateDesignBrief(WTObject object, boolean bHandOverDesignBrief) throws WTPropertyVetoException {
	LOGGER.info("start - Inside CLASS--SMCascadingAttributes and METHOD--updateHandoverAssortmentToTeam");
	try {
		if (object instanceof LCSProduct) {
			// Getting Product Object.
			LCSProduct product = (LCSProduct) object;
			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				LCSProductSeasonLink productSeasonLink = (LCSProductSeasonLink) SeasonProductLocator.getSeasonProductLink(product);
				// Setting values to Product Season attributes.
				// set handover design brief to true/ false
				LOGGER.debug(" bHandOverDesignBrief--"+bHandOverDesignBrief);
				setSEPDProductSeasonAttributes(productSeasonLink, DESIGN_BRIEF, bHandOverDesignBrief);
				LOGGER.debug(" Design Brief -before object persist--"+productSeasonLink.getValue(DESIGN_BRIEF));
				
				setSEPDProductSeasonAttributes(productSeasonLink, TO_INTEGRATE, true);
				LOGGER.debug(" TO INTEGRATE -before object persist--"+productSeasonLink.getValue(TO_INTEGRATE));
				// Setting skip plugin as true and explicitly calling Handle Post Persist API as skip plugin did not trigger the SSP's
				LCSLogic.deriveFlexTypeValues(productSeasonLink, true);
				LCSLogic.persist(productSeasonLink, true);
				LOGGER.debug(" TO INTEGRATE -after object persist--"+productSeasonLink.getValue(TO_INTEGRATE));
				LCSLogic.handlePostPersist(productSeasonLink, false); 
				LOGGER.debug(" TO INTEGRATE -after handle post persist--"+productSeasonLink.getValue(TO_INTEGRATE));
				LOGGER.debug(" Design Brief -after handle post persist--"+productSeasonLink.getValue(DESIGN_BRIEF));
				
			}		
		} 
	} catch (WTException e) { 
		LOGGER.error("WTException in updateHandoverAssortmentToTeam method: " + e.getMessage());
		e.printStackTrace();
	}
	LOGGER.info("end - Inside CLASS--SMCascadingAttributes and METHOD--updateHandoverAssortmentToTeam");
}
//Phase 14 - SEPD Workflow customization - Enh - Start

}
