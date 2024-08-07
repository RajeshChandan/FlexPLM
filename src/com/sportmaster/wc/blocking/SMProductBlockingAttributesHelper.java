/**
 * 
 */
package com.sportmaster.wc.blocking;

import java.util.Collection;
import java.util.List;
import org.apache.log4j.Logger;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import wt.util.WTException;

/**
 * SMProductBlockingAttributesHelper.java This class contains all plugin
 * validation methods for product,product-season, object.
 *
 * @author 'true' Zahiruddin Ansari
 * @author 'true' Rajesh Chandan - modified code foe system testing issues.
 * 
 * @version 'true' 1.1 version number
 */
public class SMProductBlockingAttributesHelper {

	/**
	 * VALIDATION_SUCESS_LITERAL.
	 */
	private static final String VALIDATION_SUCESS_LITERAL = "lifecycle state validation sucessfull for atrribute:-";

	/**
	 * ERROR_FOUND_LITERAL.
	 */
	private static final String ERROR_FOUND_LITERAL = "ERROR FOUND:-";

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductBlockingAttributesHelper.class);

	/**
	 * Product Style Number.
	 */
	public static final String STYLE_NUM = LCSProperties.get("com.sportmaster.wc.blocking.product.style");

	/**
	 * protected constructor.
	 */
	protected SMProductBlockingAttributesHelper() {
		// constructor.
	}

	/**
	 * Getting previous Product Blocked attribute Value and setting to the product
	 * object.
	 * 
	 * @param product
	 *            - product object.
	 */
	public static void smProductAttributes(LCSProduct product) {

		LCSSeasonMaster seasonMaster = null;
		LCSProductSeasonLink productSeasonLink = null;
		String currentBrand = null;
		String previousBrand = null;
		LCSSeason season = null;
		String currentLifeCycleState = "";

		// List of product attributes
		List<String> productAttributes = FormatHelper
				.commaSeparatedListToList(LCSProperties.get("com.sportmaster.wc.blocking.product.productAttributes"));
		// List of product lifecycle states
		List<String> smProductlifecycleStates = FormatHelper.commaSeparatedListToList(
				LCSProperties.get("com.sportmaster.wc.blocking.product.productAttributes.lifecycleStates"));

		Collection<?> seasonCollection = null;
		try {
			seasonCollection = new LCSSeasonQuery().findSeasons(product);

			for (Object seasonObj : seasonCollection) {
				seasonMaster = (LCSSeasonMaster) seasonObj;

				try {
					season = (LCSSeason) VersionHelper.latestIterationOf(seasonMaster);
					productSeasonLink = (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(product, season);
					LCSProduct prodSeasonRev = null;
					LCSProduct previousProduct = null;

					if (null != productSeasonLink && !productSeasonLink.isSeasonRemoved()) {
						// product season rev
						prodSeasonRev = SeasonProductLocator.getProductSeasonRev(productSeasonLink);

						// current lifecycle state
						currentLifeCycleState = prodSeasonRev.getLifeCycleState().toString();

						// Checking lifecycle status whether it true or false
						if (SMBlockingUtil.validate(smProductlifecycleStates, currentLifeCycleState)) {
							LOGGER.info("lifecycle validation sucessfull for attached season :-" + season.getName());
							// getting previous version of product
							previousProduct = (LCSProduct) VersionHelper.predecessorOf(product);

							for (String attribute : productAttributes) {

								// previous brand value
								try {
									previousBrand = (String) previousProduct.getValue(attribute);
									// current brand value
									currentBrand = (String) product.getValue(attribute);

									// set previous brand value
									SMBlockingUtil.setPreviousBrand(currentBrand, previousBrand, product, attribute);
								} catch (WTException e) {
									LOGGER.error(ERROR_FOUND_LITERAL, e);
								}

							}
							break;
						}
					}
				} catch (WTException e) {
					LOGGER.error(ERROR_FOUND_LITERAL, e);
				}
			}
		} catch (WTException e) {
			LOGGER.error(ERROR_FOUND_LITERAL, e);
		}
	}

	/**
	 * Getting previous Product Season Blocked attribute Value and setting to the
	 * product-season object.
	 * 
	 * @param productSeasonLink
	 *            - product season link object.
	 */
	public static void smProductSeasonAttributes(LCSProductSeasonLink productSeasonLink) {

		LCSProductSeasonLink previousprodSeasonLink = null;
		LCSProduct smProduct = null;
		String productSeasonStatus = null;
		String currentSeasonStatus = null;
		String currentLifeCycleState = "";

		// List of product season attributes
		List<String> productSeasonAttributes = FormatHelper.commaSeparatedListToList(
				LCSProperties.get("com.sportmaster.wc.blocking.product.productSeason.attributes"));

		// List of Product season CN lifecycle states
		List<String> smproductSeasonCNLifeCycleState = FormatHelper.commaSeparatedListToList(
				LCSProperties.get("com.sportmaster.wc.blocking.product.productSeasonAttributeCN.Lifecyclestate"));

		// List of Product season RU lifecycle states
		List<String> smproductSeasonRULifeCycleState = FormatHelper.commaSeparatedListToList(
				LCSProperties.get("com.sportmaster.wc.blocking.product.productSeasonAttributeRU.Lifecyclestate"));

		// List of product season attribute limit
		List<String> productSeasonAttributeLimit = FormatHelper.commaSeparatedListToList(
				LCSProperties.get("com.sportmaster.wc.blocking.product.productSeason.limitAttr"));

		// List of product season attribute limit values
		List<String> productSeasonAttributeLimitValues = FormatHelper.commaSeparatedListToList(
				LCSProperties.get("com.sportmaster.wc.blocking.product.productSeason.limitAttrValue"));

		// List of product season attribute limit lifecycle states
		List<String> smproductSeasonLmtAttrLifeCycleState = FormatHelper.commaSeparatedListToList(
				LCSProperties.get("com.sportmaster.wc.blocking.product.productSeason.limitAttr.lifecycleStates"));

		// getting product season rev
		try {
			// Getting previous version of product season Link
			previousprodSeasonLink = (LCSProductSeasonLink) LCSSeasonQuery.getPriorSeasonProductLink(productSeasonLink);

			smProduct = SeasonProductLocator.getProductSeasonRev(productSeasonLink);
			LOGGER.info("executing blocking customisation for product season link:-" + smProduct.getName());

			// Current lifecycle state
			currentLifeCycleState = smProduct.getLifeCycleState().toString();

			LOGGER.info("curent lifecycle state:-" + currentLifeCycleState);
			// Checking lifecycle status
			if (SMBlockingUtil.validate(smproductSeasonLmtAttrLifeCycleState, currentLifeCycleState)) {
				LOGGER.info(VALIDATION_SUCESS_LITERAL + productSeasonAttributeLimit.get(0));
				productSeasonStatus = (String) previousprodSeasonLink.getValue(productSeasonAttributeLimit.get(0));
				currentSeasonStatus = (String) productSeasonLink.getValue(productSeasonAttributeLimit.get(0));

				if (FormatHelper.hasContent(productSeasonStatus) && !productSeasonStatus.equals(currentSeasonStatus)
						&& productSeasonAttributeLimitValues.get(0).equalsIgnoreCase(currentSeasonStatus)) {

					LOGGER.info("chnages found on '" + productSeasonAttributeLimit.get(0)
							+ "' value, curent version value:-" + currentSeasonStatus + " previous version value:-"
							+ productSeasonStatus);

					// set previous value
					productSeasonLink.setValue(productSeasonAttributeLimit.get(0), productSeasonStatus);

					LOGGER.info("reverted back to previous '" + productSeasonAttributeLimit.get(0) + "' value:-"
							+ productSeasonStatus);
					// Adding for getting the pop-up box at front end
					SMBlockingAttributes.multiMap.put(productSeasonLink.getFlexType()
							.getAttribute(productSeasonAttributeLimit.get(0)).getAttDisplay(),
							String.valueOf(smProduct.getValue(STYLE_NUM)) + " ");
				}
			}

			// Checking lifecycle status
			if (SMBlockingUtil.validate(smproductSeasonCNLifeCycleState, currentLifeCycleState)) {
				LOGGER.info(VALIDATION_SUCESS_LITERAL + productSeasonAttributes.get(0));

				// set previous CommercialSizesCN value
				SMBlockingUtil.setCommercialSizesCN(productSeasonLink, previousprodSeasonLink, productSeasonAttributes,
						smProduct);

			}

			if (SMBlockingUtil.validate(smproductSeasonRULifeCycleState, currentLifeCycleState)) {
				LOGGER.info(VALIDATION_SUCESS_LITERAL + productSeasonAttributes.get(1));
				// set previous CommercialSizesRU value
				SMBlockingUtil.setCommercialSizesRU(productSeasonLink, previousprodSeasonLink, productSeasonAttributes,
						smProduct);

			}
		} catch (WTException e) {
			LOGGER.error(ERROR_FOUND_LITERAL, e);
		}
	}
}
