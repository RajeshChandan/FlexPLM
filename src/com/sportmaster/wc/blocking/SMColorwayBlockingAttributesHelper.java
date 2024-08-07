/**
 * 
 */
package com.sportmaster.wc.blocking;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.util.WTException;

/**
 * SMColorwayBlockingAttributesHelper.java This class contains all plugin
 * validation methods for Colorway,Colorway-season, object.
 *
 * @author 'true' Zahiruddin Ansari
 * @author 'true' Rajesh Chandan - modified code foe system testing issues.
 * 
 * @version 'true' 1.1 version number
 */
public class SMColorwayBlockingAttributesHelper {

	/**
	 * ERROR_FOUND_LITERAL.
	 */
	private static final String ERROR_FOUND_LITERAL = "ERROR FOUND:-";

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMColorwayBlockingAttributesHelper.class);

	/**
	 * Product Style Number.
	 */
	public static final String STYLE_NUM = LCSProperties.get("com.sportmaster.wc.blocking.product.style");

	/**
	 * Colorway color.
	 */
	public static final String COLOR = LCSProperties.get("com.sportmaster.wc.blocking.colorway.color");

	/**
	 * protected constructor.
	 */
	protected SMColorwayBlockingAttributesHelper() {
		// constructor.
	}

	/**
	 * Getting previous Colorway Blocked attribute Value and setting to the colorway
	 * object.
	 * 
	 * @param skuObj
	 *            - object of type colorway
	 */
	public static void smColorwayAttributes(LCSSKU skuObj) {
		LCSSeasonMaster colorwaySeasonMaster = null;
		LCSProductSeasonLink productSeasonLink = null;
		boolean lifecycleStatus = false;
		LCSSeason season = null;
		String currentLifeCycleState = "";
		String errorValue = "";
		Collection<?> seasonCollection = null;
		LCSProduct product = null;
		
		LCSSKUSeasonLink skuSeasonLink = null;
		
		try { 
			product = skuObj.getProduct();
			// getting season collection
			// 3.9.1.0 - EHR 546 - Start
			// Instead of getting seasons for the product, get the seasons for the colorway for the blocking logic.
			//seasonCollection = new LCSSeasonQuery().findSeasons(product);
			seasonCollection = new LCSSeasonQuery().findSeasons(skuObj.getMaster());
			// 3.9.1.0 - EHR 546 - End
			for (Object seasonObj : seasonCollection) {

				try {

					colorwaySeasonMaster = (LCSSeasonMaster) seasonObj;
					// season object from colorway season master
					season = (LCSSeason) VersionHelper.latestIterationOf(colorwaySeasonMaster);

					// product season link obj
					productSeasonLink = (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(product, season);
					LCSProduct prodSeasonRev = null;
					LCSSKU previousColorway = null;
					
					// Check if SKU Season link
					skuSeasonLink = (LCSSKUSeasonLink) LCSSeasonQuery.findSeasonProductLink(skuObj, season);
					
					// 3.9.1.0 - EHR 546 - Start
					// Instead of checking product season, check if cw season is not removed from season for the blocking logic.
					//if (null != productSeasonLink && !productSeasonLink.isSeasonRemoved()) {
					if (null != productSeasonLink && null != skuSeasonLink && !skuSeasonLink.isSeasonRemoved()) {
						// 3.9.1.0 - EHR 546 - End
						// prod season rev
						prodSeasonRev = SeasonProductLocator.getProductSeasonRev(productSeasonLink);

						// Current lifecycle state
						currentLifeCycleState = prodSeasonRev.getLifeCycleState().toString();

						// List of colorway attributes
						List<String> colorwayAttributes = FormatHelper.commaSeparatedListToList(
								LCSProperties.get("com.sportmaster.wc.blocking.product.colorwayAttributes"));

						// List of colorway lifecycle states
						List<String> smColorwaylifecycleStates = FormatHelper.commaSeparatedListToList(LCSProperties
								.get("com.sportmaster.wc.blocking.product.colorwayAttributes.lifecycleStates"));

						// Checking lifecycle status whether it true or false
						lifecycleStatus = SMBlockingUtil.validate(smColorwaylifecycleStates, currentLifeCycleState);
						if (lifecycleStatus) {
							LOGGER.info("lifecycle validation sucessfull for attached season :-" + season.getName());
							// Getting previous version of colorway
							previousColorway = (LCSSKU) VersionHelper.predecessorOf(skuObj);

							// added for eror poup msg
							LCSColor color = (LCSColor) previousColorway.getValue(COLOR);
							
							errorValue = SMBlockingUtil.getColorwayColor(color, prodSeasonRev);
							for (String attribute : colorwayAttributes) {

								// set previous color value
								SMBlockingUtil.setPreviousColor(attribute, errorValue, skuObj, previousColorway);
							}
							break;
						}
					}
				} catch (WTException e) {
					LOGGER.error(ERROR_FOUND_LITERAL, e);
				}
			}
		} catch (WTException e1) {
			LOGGER.error(ERROR_FOUND_LITERAL, e1);
		}
	}

	/**
	 * Getting previous colorway-season Blocked attribute Value and setting to the
	 * colorway-season object.
	 * 
	 * @param skuSeasonObj
	 *            - object of type colorway-season
	 */
	public static void smColorwaySeasonAttributes(LCSSKUSeasonLink skuSeasonObj) {

		LCSSKU skuARev = null;
		LCSProduct smProduct = null;
		String currentLifeCycleState = "";
		LCSSeason season = null;
		LCSProductSeasonLink productSeasonLink = null;
		boolean lifecycleStatus = false;
		LCSSKUSeasonLink previousSkuSeason = null;
		String errorValue = "";

		// List of colorway season attributes
		List<String> colorwaySeasonAttributes = FormatHelper.commaSeparatedListToList(
				LCSProperties.get("com.sportmaster.wc.blocking.product.colorwaySeasonAttributes"));
		// List of colorway season lifecycle states
		List<String> smColorwaySeasonlifecycleStates = FormatHelper.commaSeparatedListToList(
				LCSProperties.get("com.sportmaster.wc.blocking.product.colorwaySeasonAttributes.lifecycleStates"));
		// List of attribute limit on colorway season
		List<String> attributeLimit = FormatHelper.commaSeparatedListToList(
				LCSProperties.get("com.sportmaster.wc.blocking.product.colorwaySeason.limitAttr"));
		// List of attribute limit value on colorway season
		List<String> attributeLimitValues = FormatHelper.commaSeparatedListToList(
				LCSProperties.get("com.sportmaster.wc.blocking.product.colorwaySeason.limitAttrValue"));

		// sku object from skuSeason
		try {
			
			skuARev = SeasonProductLocator.getSKUARev(skuSeasonObj);
			skuARev = (LCSSKU) VersionHelper.latestIterationOf(skuARev);
			
			LOGGER.info("executing blocking customisation for colorway season link:-" + skuARev.getName());
			// product object from sku
			smProduct = skuARev.getProduct();

			// season object from sku-Season
			season = (LCSSeason) LCSQuery
					.findObjectById("VR:com.lcs.wc.season.LCSSeason:" + (int) skuSeasonObj.getSeasonRevId());
			season = (LCSSeason) VersionHelper.latestIterationOf(season.getMaster());

			// product season link
			productSeasonLink = (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(smProduct, season);

			// Product season rev
			smProduct = SeasonProductLocator.getProductSeasonRev(productSeasonLink);

			// Current lifecycle state
			currentLifeCycleState = smProduct.getLifeCycleState().toString();

			// Checking lifecycle status
			lifecycleStatus = SMBlockingUtil.validate(smColorwaySeasonlifecycleStates, currentLifeCycleState);
			LOGGER.info("curent lifecycle state:-" + currentLifeCycleState);
			if (lifecycleStatus) {
				LOGGER.info("lifecycle state validation sucessfull");
				// get previous version of sku season object
				previousSkuSeason = (LCSSKUSeasonLink) LCSSeasonQuery.getPriorSeasonProductLink(skuSeasonObj);
				
				// Added for error pop message
				LCSColor color = (LCSColor) skuARev.getValue(COLOR);
				if(color == null) {
				     return;
				}
				errorValue = String.valueOf(smProduct.getValue(STYLE_NUM)) + "-" + color.getName();
				
				// set previous colorwaySeasonStatus
				SMBlockingUtil.setColorwaySeasonStatus(errorValue, attributeLimit, attributeLimitValues, skuSeasonObj,
						previousSkuSeason);

				// set previous LLT value
				SMBlockingUtil.setPreviousLLT(errorValue, colorwaySeasonAttributes, previousSkuSeason, skuSeasonObj);

				// set previous MHP value
				SMBlockingUtil.setPreviousMHP(errorValue, colorwaySeasonAttributes, previousSkuSeason, skuSeasonObj);
			}
		} catch (WTException e) {
			LOGGER.error(ERROR_FOUND_LITERAL, e);
		}

	}
}
