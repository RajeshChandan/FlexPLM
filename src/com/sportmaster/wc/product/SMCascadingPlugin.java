package com.sportmaster.wc.product;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.LCSSKUQuery;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * SMCascadingPlugin.
 * 
 * @author 'true'
 * @version 'true' 1.0
 * 
 */
public class SMCascadingPlugin {

	/**
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMCascadingPlugin.class);
	/**
	 * Product Season Status.
	 */
	public static final String PRODUCT_SEASON_STATUS = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingAttributes.productSeasonStatus");
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
	 * Product Season Life Cycle State CANCELLED.
	 */
	public static final String PRODUCT_LIFECYCLE_STATE_CANCELLED = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingAttributes.productLifeCycleStateCancelled");
	/**
	 * SKU Season Status 'Cancelled' key.
	 */
	public static final String SKU_STATUS_CANCELLED = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingPlugin.skuStatusCancelled");
	/**
	 * Product cancelled on.
	 */
	public static final String PRODUCT_CANCELLED_ON = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingPlugin.productCancelledOn");
	/**
	 * LCS cancelled after.
	 */
	public static final String LCS_CANCELLED_AFTER = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingPlugin.lcsCancelledAfter");
	/**
	 * CW cancelled on.
	 */
	public static final String CW_CANCELLED_ON = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingPlugin.cWCancelledOn");
	/**
	 * LCS - CW cancelled after.
	 */
	public static final String LCS_CW_CANCELLED_AFTER = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingPlugin.lcsCWCancelledAfter");
	/**
	 * LCS - CW started from.
	 */
	public static final String LCS_CW_STARTED_FROM = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingPlugin.lcsCWStartedFrom");
	/**
	 * LCS cancelled state.
	 */
	public static final String LCS_CANCELLED_STATE = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingPlugin.lcsCancelledState");

	/**
	 * Constructor.
	 */
	protected SMCascadingPlugin() {
		// protected constructor
	}

	/**
	 * setCurrentDate.
	 * 
	 * @return Date.
	 */
	public static Date setCurrentDate() {
		// getting MSK timezone
		TimeZone tz = TimeZone.getTimeZone(LCSProperties.get("com.lcs.wc.util.FormatHelper.STANDARD_TIMEZONE"));
		LOGGER.debug("tz="+tz);
		// Fix for SMPLM-1316 (to set right date) - Start
		/*
		SimpleDateFormat converter = new SimpleDateFormat("dd/MM/yyyy");
		converter.setTimeZone(tz);
		converter.format(currentDate);
		*/		
		Calendar cal = Calendar.getInstance(tz);
		long date = cal.getTimeInMillis();
		LOGGER.debug("Calendar time before offset="+cal.getTime());
		LOGGER.debug("date before="+date);
		int offset = cal.getTimeZone().getOffset(date);
		cal.setTimeInMillis(date + offset);
		LOGGER.debug("Calendar time after offset="+cal.getTime());
		return cal.getTime();		
		//return currentDate;
		// Fix for SMPLM-1316 (to set right date) - End
		
	}

	/**
	 * checkProductStatusStyleSeasonal.
	 * 
	 * @param wtObj for wtObj.
	 * @return void.
	 */
	public static void checkProductStatusStyleSeasonal(WTObject wtObj) {
		LOGGER.info("start - Inside CLASS--SMCascadingPlugin and METHOD--checkProductStatusStyleSeasonal");

		try {
			if (wtObj instanceof LCSSeasonProductLink) {
				// Getting season product link
				LCSSeasonProductLink seasonProductLink = (LCSSeasonProductLink) wtObj;

				// Getting Product Object.
				LCSProduct lcsProd = SeasonProductLocator.getProductSeasonRev(seasonProductLink);
				LOGGER.debug("METHOD--checkProductStatusStyleSeasonal - lcsProd name="+lcsProd.getName());
				// Getting Current Product LifeCycle state.
				String currentLCS = lcsProd.getLifeCycleState().getFullDisplay();
				LOGGER.debug("METHOD--checkProductStatusStyleSeasonal - currentLCS="+currentLCS);

				// Getting value of Product Status Style Seasonal attribute.
				String productStatusStyleSeasonal = seasonProductLink.getValue(PRODUCT_SEASON_STATUS).toString();
				LOGGER.debug("METHOD--checkProductStatusStyleSeasonal - productStatusStyleSeasonal="+productStatusStyleSeasonal);
				if (productStatusStyleSeasonal != null && FormatHelper.hasContent(productStatusStyleSeasonal)
						&& productStatusStyleSeasonal.equalsIgnoreCase(CANCELLED_STATUS_KEY)) {
					LOGGER.debug("METHOD--checkProductStatusStyleSeasonal - setting Product Cancelled On and LC Cancelled After attributes...");

					// Setting Product cancelled on attribute to current Date.
					seasonProductLink.setValue(PRODUCT_CANCELLED_ON, setCurrentDate());

					// Setting LCS cancelled after attribute to current LCS.
					seasonProductLink.setValue(LCS_CANCELLED_AFTER, currentLCS);

					/*
					 * // creating state object with Cancelled state. State cancelledState =
					 * State.toState(PRODUCT_LIFECYCLE_STATE_CANCELLED);
					 * 
					 * final boolean old_enforced =
					 * SessionServerHelper.manager.setAccessEnforced(false); try { // Setting
					 * Product Lifecycle state as cancelled.
					 * LifeCycleHelper.service.setLifeCycleState(lcsProd, cancelledState, true); }
					 * finally { SessionServerHelper.manager.setAccessEnforced(old_enforced); }
					 */
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in checkProductStatusStyleSeasonal Method -" + e.getMessage());
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			LOGGER.error("WTPropertyVetoException in checkProductStatusStyleSeasonal Method -" + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SMCascadingPlugin and METHOD--checkProductStatusStyleSeasonal");
	}

	/**
	 * setSKUSeasonAttribute.
	 * 
	 * @param wtObj for wtObj.
	 * @return void.
	 * @throws WTPropertyVetoException 
	 */
	public static void setSKUSeasonAttribute(WTObject wtObj) throws WTPropertyVetoException {
		LOGGER.info("start - Inside CLASS--SMCascadingPlugin and METHOD--setSKUSeasonAttribute");

		try {
			if (wtObj instanceof LCSSeasonProductLink) {
				// Getting season product link
				LCSSeasonProductLink seasonProductLink = (LCSSeasonProductLink) wtObj;

				// Getting Product Object.
				LCSProduct lcsProd = SeasonProductLocator.getProductSeasonRev(seasonProductLink);
				LOGGER.debug("METHOD--setSKUSeasonAttribute - lcsProd name="+lcsProd.getName());

				// Getting value of Product Status Style Seasonal attribute.
				String productStatusStyleSeasonal = seasonProductLink.getValue(PRODUCT_SEASON_STATUS).toString();
				LOGGER.debug("METHOD--setSKUSeasonAttribute - productStatusStyleSeasonal="+productStatusStyleSeasonal);
				if (productStatusStyleSeasonal != null && FormatHelper.hasContent(productStatusStyleSeasonal)
						&& productStatusStyleSeasonal.equalsIgnoreCase(CANCELLED_STATUS_KEY)) {
					
					// creating state object with Cancelled state.
					State cancelledState = State.toState(PRODUCT_LIFECYCLE_STATE_CANCELLED);
					LOGGER.debug("METHOD--setSKUSeasonAttribute - cancelledState="+cancelledState);

					final boolean old_enforced = SessionServerHelper.manager.setAccessEnforced(false);
					try {
						// Setting Product Lifecycle state as cancelled.
						LifeCycleHelper.service.setLifeCycleState(lcsProd, cancelledState, true); 
					} finally {
						SessionServerHelper.manager.setAccessEnforced(old_enforced);
					}					
					
					// Phase 14 - Fix for SMPLM-1355 Cancelled LCS Not shown in Line Sheet - Start
					seasonProductLink.setProdState(PRODUCT_LIFECYCLE_STATE_CANCELLED);
					// using persist API to prevent plugin from re-triggering.
					LCSLogic.persist(seasonProductLink, true);
					// Phase 14 - Fix for SMPLM-1355 Cancelled LCS Not shown in Line Sheet - End
					
					
					// Getting Product Object.
					LCSProduct lcsProd1 = SeasonProductLocator.getProductSeasonRev(seasonProductLink);
					
					final List<LCSSKU> skuCollection = (List<LCSSKU>) LCSSKUQuery.findSKUs(lcsProd1);
					LOGGER.debug("METHOD--setSKUSeasonAttribute - skuCollection="+skuCollection);
					setSKUsCancelled(skuCollection);
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in setSKUSeasonAttribute Method -" + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SMCascadingPlugin and METHOD--setSKUSeasonAttribute");
	}

	/**
	 * setSKUsCancelled.
	 * 
	 * @param skuCollection for skuCollection.
	 * @return void.
	 */
	public static void setSKUsCancelled(final List<LCSSKU> skuCollection) {

		try {
			String currentLCSCW;
			LCSSKU skuObj;
			LCSSKUSeasonLink skuSeasonLink;
			if (skuCollection != null && skuCollection.size() > 0) {

				for (Object obj : skuCollection) {
					// Getting sku Object.
					skuObj = (LCSSKU) obj;
					LOGGER.debug("METHOD--setSKUsCancelled - skuObj name="+skuObj.getName());
					// Getting Current Colorway Season Lifecycle state.
					currentLCSCW = skuObj.getLifeCycleState().getFullDisplay();
					LOGGER.debug("METHOD--setSKUsCancelled - currentLCSCW="+currentLCSCW);
					// Getting sku Season Link
					skuSeasonLink = (LCSSKUSeasonLink) LCSSeasonQuery.findSeasonProductLink(skuObj.getMaster(),
							skuObj.getSeasonMaster());

					if (!currentLCSCW.equals(LCS_CANCELLED_STATE)) {
						LOGGER.debug("METHOD--setSKUsCancelled - setting sku season status, cw cancelled on, cw cancelled after and CW State..");
						// Setting LCS - CW cancelled afterSKU Season status attribute to Cancelled.
						skuSeasonLink.setValue(SKU_SEASON_STATUS_KEY, CANCELLED_STATUS_KEY);

						// Setting CW cancelled on attribute to current Date.
						skuSeasonLink.setValue(CW_CANCELLED_ON, setCurrentDate());

						// Setting LCS - CW cancelled after attribute to current LCS.
						skuSeasonLink.setValue(LCS_CW_CANCELLED_AFTER, currentLCSCW);

						// creating state object with Cancelled state.
						State cancelledState = State.toState(SKU_STATUS_CANCELLED);
						LOGGER.debug("METHOD--setSKUsCancelled - cancelledState="+cancelledState);
						// Fix for C15318744 Product Season/ Colorway Season WF Line sheet are displaying not correct information - Start
						// Moved persist here to set the lifecycle state on latest SKUSeason link object.
						// saving cw-season status
						LCSLogic.persist(skuSeasonLink);
						// Fix for C15318744 Product Season/ Colorway Season WF Line sheet are displaying not correct information - End
						// Bypass the security to change state as the users usually don't have access to
						// Change State from UI
						final boolean old_enforced = SessionServerHelper.manager.setAccessEnforced(false);
						try {
							LOGGER.debug("METHOD--setSKUsCancelled - Setting SKU STATE="+cancelledState);
							// Setting SKU Season Lifecycle state as cancelled.
							LifeCycleHelper.service.setLifeCycleState(skuObj, cancelledState, true);
						} finally {
							SessionServerHelper.manager.setAccessEnforced(old_enforced);
						}
						// Fix for C15318744 Product Season/ Colorway Season WF Line sheet are displaying not correct information - Start
						// Moving above to set the lifecycle state on latest SKUSeason link object.
						// saving cw-season status
						//LCSLogic.persist(skuSeasonLink);
						// Fix for C15318744 Product Season/ Colorway Season WF Line sheet are displaying not correct information - End
					}
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in setSKUsCancelled Method -" + e.getMessage());
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			LOGGER.error("WTPropertyVetoException in setSKUsCancelled Method -" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * statusCWSeasonalCancelled.
	 * 
	 * @param wtObj for wtObj.
	 * @return void.
	 */
	public static void statusCWSeasonalCancelled(WTObject wtObj) {
		LOGGER.info("start - Inside CLASS--SMCascadingPlugin and METHOD--statusCWSeasonalCancelled");

		try {
			if (wtObj instanceof LCSSKUSeasonLink) {
				// Getting season product link.
				LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) wtObj;

				// For Case 15714080 - Getting sku object of current season.
				// Getting sku Master.
				//LCSPartMaster skuMaster = skuSeasonLink.getSkuMaster();
				// Getting sku Object.
				//LCSSKU skuObj = (LCSSKU) VersionHelper.latestIterationOf(skuMaster); 
				LCSSKU skuObj = SeasonProductLocator.getSKUSeasonRev(skuSeasonLink);
				LOGGER.debug("METHOD--statusCWSeasonalCancelled - skuObj="+skuObj.getName());
				// Getting Status CW Seasonal attribute value.
				String skuStatusCWSeasonal = skuSeasonLink.getValue(SKU_SEASON_STATUS_KEY).toString();
				LOGGER.debug("METHOD--statusCWSeasonalCancelled - skuStatusCWSeasonal="+skuStatusCWSeasonal);
				// Getting Current SKU LifeCycle state. 
				String currentLCS = skuObj.getLifeCycleState().getFullDisplay();
				LOGGER.debug("METHOD--statusCWSeasonalCancelled - currentLCS="+currentLCS);
				if (!currentLCS.equals(LCS_CANCELLED_STATE) && FormatHelper.hasContent(skuStatusCWSeasonal)
						&& skuStatusCWSeasonal.equalsIgnoreCase(CANCELLED_STATUS_KEY)) {
					LOGGER.debug("METHOD--statusCWSeasonalCancelled - setting CW Cancelled on, CW Cancelled After");
					// Setting CW cancelled on attribute to current Date.
					skuSeasonLink.setValue(CW_CANCELLED_ON, setCurrentDate());

					// Setting LCS - CW cancelled after attribute to current LCS. 
					skuSeasonLink.setValue(LCS_CW_CANCELLED_AFTER, currentLCS);
					//String currentLCS = skuObj.getLifeCycleState().getFullDisplay();
					// Phase-9 CR - Fix for C15318744 Product Season/ Colorway Season WF Line sheet are displaying not correct information - Start
					// Commented these and moved to Post Persist, as the Cancelled LCS State is getting persisted on old version and not displayed in linesheet
					/*
					// creating state object with Cancelled state.
					State cancelledState = State.toState(SKU_STATUS_CANCELLED);

					// Bypass the security to change state as the users usually don't have access to
					// Change State from UI
					final boolean old_enforced = SessionServerHelper.manager.setAccessEnforced(false);
					try {
						// Setting SKU Season Lifecycle state as cancelled.
						LifeCycleHelper.service.setLifeCycleState(skuObj, cancelledState, true);
					} finally {
						SessionServerHelper.manager.setAccessEnforced(old_enforced);
					}
					*/
					// Phase-9 CR - Fix for C15318744 Product Season/ Colorway Season WF Line sheet are displaying not correct information - End
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in statusCWSeasonalCancelled Method -" + e.getMessage());
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			LOGGER.error("WTPropertyVetoException in statusCWSeasonalCancelled Method -" + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SMCascadingPlugin and METHOD--statusCWSeasonalCancelled");
	}

	/**
	 * setLifeCycleStateForColorway.
	 * 
	 * @param wtObj for wtObj.
	 * @return void.
	 */
	public static void setLifeCycleStateForColorway(WTObject wtObj) {
		LOGGER.info("start - Inside CLASS--SMCascadingPlugin and METHOD--setLifeCycleStateForColorway");

		try {
			if (wtObj instanceof LCSSKUSeasonLink) {

				// Getting season product link.
				LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) wtObj;

				// Getting sku Master.
				LCSPartMaster skuMaster = skuSeasonLink.getSkuMaster();

				// Getting sku Object.
				LCSSKU skuObj = (LCSSKU) VersionHelper.latestIterationOf(skuMaster);
				LOGGER.debug("METHOD--setLifeCycleStateForColorway - skuObj name="+skuObj.getName());
				// Getting product Object .
				LCSProduct product = SeasonProductLocator.getProductSeasonRev(skuObj);
				LOGGER.debug("METHOD--setLifeCycleStateForColorway - product name="+product.getName());
				// Getting display name of current Product Lifecycle state.
				String currentProductLifecycle = product.getState().getState().getDisplay();
				LOGGER.debug("METHOD--setLifeCycleStateForColorway - currentProductLifecycle="+currentProductLifecycle);
				// Setting current Product Lifecyle state to attribute LCS-CW started from.
				if (!FormatHelper.hasContent((String) skuSeasonLink.getValue(LCS_CW_STARTED_FROM))) {
					LOGGER.debug("METHOD--setLifeCycleStateForColorway - Setting CW Started From...");
					skuSeasonLink.setValue(LCS_CW_STARTED_FROM, currentProductLifecycle);
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in setLifeCycleStateForColorway Method -" + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SMCascadingPlugin and METHOD--setLifeCycleStateForColorway");
	}
	
	// Phase-9 CR - Fix for C15318744 Product Season/ Colorway Season WF Line sheet are displaying not correct information - Start
	/**
	 * setCWSeasonalStateCancelled.
	 * 
	 * @param wtObj for wtObj.
	 * @return void.
	 */
	public static void setCWSeasonalStateCancelled(WTObject wtObj) {
		LOGGER.info("start - Inside CLASS--SMCascadingPlugin and METHOD--setCWSeasonalStateCancelled");

		try {
			if (wtObj instanceof LCSSKUSeasonLink) {
				// Getting season product link.
				LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) wtObj;
				LOGGER.debug("METHOD--setCWSeasonalStateCancelled - skuSeasonLink.getCopiedFrom()="+skuSeasonLink.getCopiedFrom());
				LOGGER.debug("METHOD--setCWSeasonalStateCancelled - skuSeasonLink.getEffectSequence()="+skuSeasonLink.getEffectSequence());
				// For Case 15714080 - Getting sku object of current season.
				// Getting sku Master.
				//LCSPartMaster skuMaster = skuSeasonLink.getSkuMaster();
				// Getting sku Object.
				LCSSKU skuObj = SeasonProductLocator.getSKUSeasonRev(skuSeasonLink);
				LOGGER.debug("METHOD--setCWSeasonalStateCancelled - skuObj.getName="+skuObj.getName());
				// Getting Status CW Seasonal attribute value.
				String skuStatusCWSeasonal = skuSeasonLink.getValue(SKU_SEASON_STATUS_KEY).toString();
				// Getting Current SKU LifeCycle state.
				String currentLCS = skuObj.getLifeCycleState().getFullDisplay();
				LOGGER.debug("Inside CLASS--SMCascadingPlugin and METHOD--setCWSeasonalStateCancelled currentLCS="+currentLCS);
				LOGGER.debug("Inside CLASS--SMCascadingPlugin and METHOD--setCWSeasonalStateCancelled skuStatusCWSeasonal="+skuStatusCWSeasonal);
				// check skuSeasonLink.getEffectSequence not 0, as for copied sku, the value was setting LC State as cancelled
				if (isValidSKUSeasLinkToSetCancelState(skuSeasonLink, skuStatusCWSeasonal, currentLCS)) {
					// creating state object with Cancelled state.
					State cancelledState = State.toState(SKU_STATUS_CANCELLED);
					LOGGER.debug("METHOD--setCWSeasonalStateCancelled - cancelledState="+cancelledState);
					// Bypass the security to change state as the users usually don't have access to
					// Change State from UI
					final boolean old_enforced = SessionServerHelper.manager.setAccessEnforced(false);
					try {
						LOGGER.debug("METHOD--setCWSeasonalStateCancelled - setting to="+cancelledState);
						// Setting SKU Season Lifecycle state as cancelled.
						LifeCycleHelper.service.setLifeCycleState(skuObj, cancelledState, true);
					} finally {
						SessionServerHelper.manager.setAccessEnforced(old_enforced);
					}
				}
				
			}
		} catch (WTException e) {
			LOGGER.error("WTException in setCWSeasonalStateCancelled Method -" + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SMCascadingPlugin and METHOD--setCWSeasonalStateCancelled");
		}	
	// Phase-9 CR - Fix for C15318744 Product Season/ Colorway Season WF Line sheet are displaying not correct information - End

	/**
	 * @param skuSeasonLink
	 * @param skuStatusCWSeasonal
	 * @param currentLCS
	 * @return
	 */
	private static boolean isValidSKUSeasLinkToSetCancelState(LCSSKUSeasonLink skuSeasonLink,
			String skuStatusCWSeasonal, String currentLCS) {
		LOGGER.debug("Inside CLASS--SMCascadingPlugin and METHOD--isValidSKUSeasLinkToSetCancelState");
		LOGGER.debug("METHOD--isValidSKUSeasLinkToSetCancelState - skuSeasonLink.getCopiedFrom()="+skuSeasonLink.getCopiedFrom());
		LOGGER.debug("METHOD--isValidSKUSeasLinkToSetCancelState - skuSeasonLink.getEffectSequence()="+skuSeasonLink.getEffectSequence());
		return ((skuSeasonLink.getCopiedFrom()!= null && skuSeasonLink.getEffectSequence() > 0) || skuSeasonLink.getCopiedFrom()== null) && !currentLCS.equals(LCS_CANCELLED_STATE) && FormatHelper.hasContent(skuStatusCWSeasonal)
				&& skuStatusCWSeasonal.equalsIgnoreCase(CANCELLED_STATUS_KEY);
	}
}
