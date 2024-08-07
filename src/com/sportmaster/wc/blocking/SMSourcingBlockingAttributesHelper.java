/**
 * 
 */
package com.sportmaster.wc.blocking;

import java.util.List;

import org.apache.log4j.Logger;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.fc.PersistenceServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * SMSourcingBlockingAttributesHelper.java
 * This class contains all plugin validation methods for Sourcing configuration-season,
 * object.
 *
 * @author 'true' Zahiruddin Ansari
 * @author 'true' Rajesh Chandan - modified code foe system testing issues.
 * 
 * @version 'true' 1.1 version number
 */
public class SMSourcingBlockingAttributesHelper {

	/**
	 * VENDOR_ATTR.
	 */
	private static final String VENDOR_ATTR = LCSProperties.get("com.sportmaster.wc.blocking.sourcing.sourcingConfigAttributes","vendor");

	/**
	 * ERROR_FOUND_LITERAL.
	 */
	private static final String ERROR_FOUND_LITERAL = "ERROR FOUND:-";

	/**
	 * CHNAGES_FOUND_LITERAL.
	 */
	private static final String CHNAGES_FOUND_LITERAL = "chnages found on '";


	/**
	 * VALUE_CURENT_LITERAL.
	 */
	private static final String VALUE_CURENT_LITERAL = "' value, curent version value:-";


	/**
	 * PREVIOUS_VERSION_LITERAL.
	 */
	private static final String PREVIOUS_VERSION_LITERAL = " previous version value:-";


	/**
	 * REVERTED_LITERAL.
	 */
	private static final String REVERTED_LITERAL = "reverted back to previous '";


	/**
	 * REVERTED_LITERAL.
	 */
	private static final String VALUE_LITERAL = "' value:-";
	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSourcingBlockingAttributesHelper.class);

	/**
	 * Product Style Number.
	 */
	public static final String STYLE_NUM = LCSProperties.get("com.sportmaster.wc.blocking.product.style");

	/**
	 * protected constructor.
	 */
	protected SMSourcingBlockingAttributesHelper() {
		// constructor.
	}

	/**
	 * @param sConfig
	 */
	public static void smSourcingConfigAttributes(LCSSourcingConfig sConfig) {

		LCSSupplier vendor;
		LCSSupplier preVendor;
		String vendorName;
		String preVendorNmae;
		try {

			// Product object from sourcing configuration
			LCSProduct product = (LCSProduct) VersionHelper.latestIterationOf(sConfig.getProductMaster());
			
			vendor = (LCSSupplier) sConfig.getValue(VENDOR_ATTR);
			vendorName=vendor.getName();
			LCSSourcingConfig prevSrcConfig= (LCSSourcingConfig) VersionHelper.predecessorOf(sConfig);
			preVendor=(LCSSupplier) prevSrcConfig.getValue(VENDOR_ATTR);
			if(preVendor==null) {
				return;
			}
			preVendorNmae=preVendor.getName();
			if (!vendorName.equals(preVendorNmae)) {
				LOGGER.info(CHNAGES_FOUND_LITERAL + VENDOR_ATTR
						+ VALUE_CURENT_LITERAL + vendorName + PREVIOUS_VERSION_LITERAL
						+ preVendorNmae);
				// set previous value
				sConfig.setValue(VENDOR_ATTR, preVendor);
				
				// Fixed production business supplier issue - Phase-7 - Start
				String sName=String.valueOf(prevSrcConfig.getValue("name"));
				
				LCSSourcingConfigMaster sMaster = sConfig.getMaster();
				sConfig.setSourcingConfigName(sName);
				sConfig.setValue("name", sName);
				sMaster.setSourcingConfigName(sName);
				
				PersistenceServerHelper.manager.update(sConfig, false);
				PersistenceServerHelper.manager.update(sMaster, false);
				
				//LCSLogic.deriveFlexTypeValues(sConfig, true);
				// Fixed production business supplier issue - Phase-7 - End
				
				LOGGER.info(REVERTED_LITERAL+VENDOR_ATTR+VALUE_LITERAL+preVendorNmae);
				// Adding for getting the pop-up box at front end
				SMBlockingAttributes.multiMap.put(
						sConfig.getFlexType().getAttribute(VENDOR_ATTR).getAttDisplay(),
						String.valueOf(product.getValue(STYLE_NUM)) + "-" + preVendor.getName());
			}

		} catch (WTException e) {
			LOGGER.error(ERROR_FOUND_LITERAL, e);
		} catch (WTPropertyVetoException e) {
			LOGGER.error(ERROR_FOUND_LITERAL, e);
		}


	}

	/**
	 * Getting previous source to season Blocked attribute Value and setting to the
	 * source to season object.
	 * 
	 * @param ssl
	 *            - source to season link.
	 */
	public static void smSourcingSeasonAttributes(LCSSourceToSeasonLink ssl) {
		int flag = 0;
		String currentLifeCycleState = "";
		LCSSeason season = null;
		LCSProduct product = null;
		LCSSourcingConfig sourcingConfig = null;

		// List of Sourcing configuration to season
		List<String> sourceSeasonAttributes = FormatHelper.commaSeparatedListToList(
				LCSProperties.get("com.sportmaster.wc.blocking.sourcing.sourcingConfigToSeasonAttributes"));
		// List of Source season lifecycle states
		List<String> smSourceSeasonlifecycleStates = FormatHelper.commaSeparatedListToList(LCSProperties
				.get("com.sportmaster.wc.blocking.sourcing.sourcingConfigToSeasonAttributes.lifecycleStates"));

		try {
			// Sourcing Configuration from Source to Season link
			sourcingConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf(ssl.getSourcingConfigMaster());
			// Season object from Source to Season link
			season = (LCSSeason) VersionHelper.latestIterationOf(ssl.getSeasonMaster());

			// Product object from sourcing configuration
			product = (LCSProduct) VersionHelper.latestIterationOf(sourcingConfig.getProductMaster());

			LCSSourcingConfigQuery srcQuery = new LCSSourcingConfigQuery();
			List<?> srcList = (List<?>) LCSSourcingConfigQuery.getSourcingConfigForProduct(product.getMaster());
			for (Object srcObj : srcList) {

				if (flag > 0) {
					break;
				}

				LCSSourcingConfig sConfig = (LCSSourcingConfig) srcObj;
				SearchResults result;
				try {
					result = srcQuery.getSKUSourcingLinkDataForConfig(sConfig, season, true);
					List<?> skuListForSrcConfig = result.getResults();

					for (Object objct : skuListForSrcConfig) {
						FlexObject fObj = (FlexObject) objct;

						LCSSKU skuObj;
						try {
							// Getting colorway object from flexObject
							skuObj = (LCSSKU) LCSQuery.findObjectById(
									"VR:com.lcs.wc.product.LCSSKU:" + fObj.getString("LCSSKU.BRANCHIDITERATIONINFO"));

							// Getting colorway-season information
							LCSSKU skuSeasonObj = LCSSeasonQuery.getSKUForSeason(skuObj, season.getMaster());

							// Current lifecycle state
							currentLifeCycleState = skuSeasonObj.getLifeCycleState().toString();

							if (SMBlockingUtil.validate(smSourceSeasonlifecycleStates, currentLifeCycleState)) {
								LOGGER.info("lifecycle validation sucessfull for source:-" + sConfig.getName()
								+ ",colorway name:-" + skuObj.getName() + ",product name:-"
								+ product.getName());
								flag++;
								break;
							}
						} catch (WTException e) {
							LOGGER.error(ERROR_FOUND_LITERAL, e);
						}
					}
				} catch (WTException e1) {
					LOGGER.error(ERROR_FOUND_LITERAL, e1);
				}

			}
		} catch (WTException e) {
			LOGGER.error(ERROR_FOUND_LITERAL, e);
		}
		if (flag > 0) {
			SMBlockingUtil.validateSourceSeasonLifecycleSate(ssl, sourceSeasonAttributes, product, sourcingConfig);
		}
	}
}
