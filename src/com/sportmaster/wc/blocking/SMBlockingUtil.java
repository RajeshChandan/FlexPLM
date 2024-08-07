package com.sportmaster.wc.blocking;

import java.util.List;

import org.apache.log4j.Logger;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * SMBlockingUtil.java
 * This class contains all utill methods for product,product-season,
 * colorway, clorway-season, sourcing configuration-season object which validates
 *  current value with its previous version and set accordingly to respective object.
 *
 * @author 'true' Zahiruddin Ansari
 * @author 'true' Rajesh Chandan - modified code foe system testing issues.
 * 
 * @version 'true' 1.1 version number
 */
public class SMBlockingUtil {


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
	private static final Logger LOGGER = Logger.getLogger(SMBlockingUtil.class);


	/**
	 * Product Style Number.
	 */
	public static final String STYLE_NUM = LCSProperties.get("com.sportmaster.wc.blocking.product.style");


	/**
	 * protected constructor.
	 */
	protected SMBlockingUtil(){
		//constructor.
	}


	/**
	 * Validating lifecycle states.
	 * @param lifecycleStates - list of lifecycle state
	 * @param currentLifeCycleState - current value of lifecycle state
	 * @return the boolean value
	 */
	public static boolean validate(List<String> lifecycleStates, String currentLifeCycleState) {
		for (String states : lifecycleStates) {

			//checking states one by one from properties lifecycle states to current lifecycle state
			if (states.equalsIgnoreCase(currentLifeCycleState)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Set Previous Brand to product Blocked attribute.
	 * @param currentBrand - current product brand value.
	 * @param previousBrand - previous product brand value.
	 * @param product - product object.
	 * @param attribute - attribute internal value.
	 */
	public static void setPreviousBrand(String currentBrand, String previousBrand, LCSProduct product,
			String attribute) {

		if (!currentBrand.equals(previousBrand)) {
			LOGGER.info("chnages found on 'brand' value, curent brand value:-"+currentBrand+" previous version brand value:-"+previousBrand);
			// set previous value
			product.setValue(attribute, previousBrand);
			LOGGER.info("reverted back to previous 'brand' value:-"+previousBrand+" for product:-"+product.getName());
			// Adding for getting the pop-up box at front end
			try {
				SMBlockingAttributes.multiMap.put(product.getFlexType().getAttribute(attribute).getAttDisplay(),
						String.valueOf(product.getValue(STYLE_NUM)) + " ");
			} catch (WTException e) {
				LOGGER.error(ERROR_FOUND_LITERAL,e);
			}
		}
	}

	/**
	 * Set Commercial Sizes CN to product season Blocked attribute.
	 * @param productSeasonLink - product Season Link object.
	 * @param previousprodSeasonLink - product Season Link object.
	 * @param productSeasonAttributes - list of product season attribute.
	 * @param smProduct - product object.
	 */
	public static void setCommercialSizesCN(LCSProductSeasonLink productSeasonLink,
			LCSProductSeasonLink previousprodSeasonLink, List<String> productSeasonAttributes, LCSProduct smProduct) {
		LCSLifecycleManaged currentCommercialSizesCN;
		LCSLifecycleManaged previousCommercialSizesCN;
		String currentCommercialCN = "";
		String previousCommercialCN = "";

		if(previousprodSeasonLink==null) {
			return;
		}
		try {
			if(productSeasonLink.getValue(productSeasonAttributes.get(0))!=null) {

				currentCommercialSizesCN = (LCSLifecycleManaged) productSeasonLink.getValue(productSeasonAttributes.get(0));
				currentCommercialCN = currentCommercialSizesCN.getName();
				if(previousprodSeasonLink.getValue(productSeasonAttributes.get(0))!=null) {

					previousCommercialSizesCN = (LCSLifecycleManaged) previousprodSeasonLink.getValue(productSeasonAttributes.get(0));
					previousCommercialCN = previousCommercialSizesCN.getName();
					if(!currentCommercialCN.equals(previousCommercialCN)) {
						LOGGER.info(CHNAGES_FOUND_LITERAL + productSeasonAttributes.get(0)
						+ VALUE_CURENT_LITERAL + currentCommercialCN + PREVIOUS_VERSION_LITERAL
						+ previousCommercialCN);
						productSeasonLink.setValue(productSeasonAttributes.get(0), previousCommercialSizesCN);
						LOGGER.info(REVERTED_LITERAL+productSeasonAttributes.get(0)+VALUE_LITERAL+previousCommercialCN);
						// Adding for getting the pop-up box at front end
						SMBlockingAttributes.multiMap.put(
								productSeasonLink.getFlexType().getAttribute(productSeasonAttributes.get(0)).getAttDisplay(),
								String.valueOf(smProduct.getValue(STYLE_NUM)) + " ");
					}
				}else {
					LOGGER.info(CHNAGES_FOUND_LITERAL + productSeasonAttributes.get(0)
					+ VALUE_CURENT_LITERAL + currentCommercialCN + PREVIOUS_VERSION_LITERAL
					+ previousCommercialCN);
					productSeasonLink.setValue(productSeasonAttributes.get(0), null);
					LOGGER.info(REVERTED_LITERAL+productSeasonAttributes.get(0)+VALUE_LITERAL+null);
					// Adding for getting the pop-up box at front end
					SMBlockingAttributes.multiMap.put(
							productSeasonLink.getFlexType().getAttribute(productSeasonAttributes.get(0)).getAttDisplay(),
							String.valueOf(smProduct.getValue(STYLE_NUM)) + " ");
				}

			}else {
				if(previousprodSeasonLink.getValue(productSeasonAttributes.get(0))!=null) {

					previousCommercialSizesCN = (LCSLifecycleManaged) previousprodSeasonLink.getValue(productSeasonAttributes.get(0));
					LOGGER.info(CHNAGES_FOUND_LITERAL + productSeasonAttributes.get(0)
					+ VALUE_CURENT_LITERAL + currentCommercialCN + PREVIOUS_VERSION_LITERAL
					+ previousCommercialSizesCN.getName());
					productSeasonLink.setValue(productSeasonAttributes.get(0), previousCommercialSizesCN);
					LOGGER.info(REVERTED_LITERAL+productSeasonAttributes.get(0)+VALUE_LITERAL+previousCommercialSizesCN.getName());
					// Adding for getting the pop-up box at front end
					SMBlockingAttributes.multiMap.put(
							productSeasonLink.getFlexType().getAttribute(productSeasonAttributes.get(0)).getAttDisplay(),
							String.valueOf(smProduct.getValue(STYLE_NUM)) + " ");
				}
			}
		} catch (WTPropertyVetoException e) {
			LOGGER.error(ERROR_FOUND_LITERAL,e);
		} catch (WTException e) {
			LOGGER.error(ERROR_FOUND_LITERAL,e);
		}
	}

	/**
	 * Set Commercial Sizes RU to product season Blocked attribute.
	 * @param productSeasonLink - product Season Link object.
	 * @param previousprodSeasonLink - previous version of product Season Link object.
	 * @param productSeasonAttributes - list of product season attribute.
	 * @param smProduct - product object.
	 */
	public static void setCommercialSizesRU(LCSProductSeasonLink productSeasonLink,
			LCSProductSeasonLink previousprodSeasonLink, List<String> productSeasonAttributes, LCSProduct smProduct) {

		LCSLifecycleManaged currentCommerciaSizesRU;
		LCSLifecycleManaged previousCommerciaSizesRU;
		String currentCommercialRU = "";
		String previousCommercialRU = "";

		if(previousprodSeasonLink==null) {
			return;
		}
		try {
			if(productSeasonLink.getValue(productSeasonAttributes.get(1))!=null) {

				currentCommerciaSizesRU = (LCSLifecycleManaged) productSeasonLink.getValue(productSeasonAttributes.get(1));
				currentCommercialRU = currentCommerciaSizesRU.getName();

				if(previousprodSeasonLink.getValue(productSeasonAttributes.get(1))!=null) {

					previousCommerciaSizesRU = (LCSLifecycleManaged) previousprodSeasonLink.getValue(productSeasonAttributes.get(1));
					previousCommercialRU = previousCommerciaSizesRU.getName();

					if(!currentCommercialRU.equals(previousCommercialRU)) {
						LOGGER.info(CHNAGES_FOUND_LITERAL + productSeasonAttributes.get(1)
						+ VALUE_CURENT_LITERAL + currentCommercialRU + PREVIOUS_VERSION_LITERAL
						+ previousCommercialRU);
						productSeasonLink.setValue(productSeasonAttributes.get(1), previousCommerciaSizesRU);
						LOGGER.info(REVERTED_LITERAL+productSeasonAttributes.get(1)+VALUE_LITERAL+previousCommercialRU);
						// Adding for getting the pop-up box at front end
						SMBlockingAttributes.multiMap.put(
								productSeasonLink.getFlexType().getAttribute(productSeasonAttributes.get(1)).getAttDisplay(),
								String.valueOf(smProduct.getValue(STYLE_NUM)) + " ");
					}
				}else {
					LOGGER.info(CHNAGES_FOUND_LITERAL + productSeasonAttributes.get(1)
					+ VALUE_CURENT_LITERAL + currentCommercialRU + PREVIOUS_VERSION_LITERAL
					+ previousCommercialRU);
					productSeasonLink.setValue(productSeasonAttributes.get(1), null);
					LOGGER.info(REVERTED_LITERAL+productSeasonAttributes.get(1)+VALUE_LITERAL+null);
					// Adding for getting the pop-up box at front end
					SMBlockingAttributes.multiMap.put(
							productSeasonLink.getFlexType().getAttribute(productSeasonAttributes.get(1)).getAttDisplay(),
							String.valueOf(smProduct.getValue(STYLE_NUM)) + " ");
				}

			}else {
				if(previousprodSeasonLink.getValue(productSeasonAttributes.get(1))!=null) {

					previousCommerciaSizesRU = (LCSLifecycleManaged) previousprodSeasonLink.getValue(productSeasonAttributes.get(1));
					LOGGER.info(CHNAGES_FOUND_LITERAL + productSeasonAttributes.get(1)
					+ VALUE_CURENT_LITERAL + currentCommercialRU + PREVIOUS_VERSION_LITERAL
					+ previousCommerciaSizesRU.getName());
					productSeasonLink.setValue(productSeasonAttributes.get(1), previousCommerciaSizesRU);
					LOGGER.info(REVERTED_LITERAL+productSeasonAttributes.get(1)+VALUE_LITERAL+previousCommerciaSizesRU.getName());
					// Adding for getting the pop-up box at front end
					SMBlockingAttributes.multiMap.put(
							productSeasonLink.getFlexType().getAttribute(productSeasonAttributes.get(1)).getAttDisplay(),
							String.valueOf(smProduct.getValue(STYLE_NUM)) + " ");
				}
			}
		} catch (WTPropertyVetoException e) {
			LOGGER.error(ERROR_FOUND_LITERAL,e);
		} catch (WTException e) {
			LOGGER.error(ERROR_FOUND_LITERAL,e);
		}
	}

	/**
	 * Set Previous Color to colorway Blocked attribute.
	 * @param attribute - attribute internal value. 
	 * @param errorValue - value for pop message.
	 * @param skuObj - sku object.
	 * @param previousColorway - previous colorway color value.
	 */
	public static void setPreviousColor(String attribute, String errorValue, LCSSKU skuObj, LCSSKU previousColorway) {

		String currentColorName = "";
		String previousColorName = "";
		LCSColor currentColor = null;
		LCSColor previousColor=null;

		if(previousColorway==null) {
			return;
		}
		try {
			if(skuObj.getValue(attribute)!=null) {

				// Getting current value of color attribute
				currentColor = (LCSColor) skuObj.getValue(attribute);
				currentColorName = currentColor.getName();

				if(previousColorway.getValue(attribute)!=null) {

					previousColor = (LCSColor) previousColorway.getValue(attribute);
					previousColorName = previousColor.getName();
					
					if(!currentColorName.equals(previousColorName)) {
						LOGGER.info(CHNAGES_FOUND_LITERAL + attribute
								+ VALUE_CURENT_LITERAL + currentColorName + PREVIOUS_VERSION_LITERAL
								+ previousColorName);
						skuObj.setValue(attribute, previousColor);
						LCSLogic.deriveFlexTypeValues(skuObj, true);
						LOGGER.info(REVERTED_LITERAL+attribute+VALUE_LITERAL+previousColorName);
						// Adding for getting the pop-up box at front end
						SMBlockingAttributes.multiMap.put(skuObj.getFlexType().getAttribute(attribute).getAttDisplay(), errorValue);
					}
				}else {
					LOGGER.info(CHNAGES_FOUND_LITERAL + attribute
							+ VALUE_CURENT_LITERAL + currentColorName + PREVIOUS_VERSION_LITERAL
							+ previousColorName);
					skuObj.setValue(attribute, null);
					LOGGER.info(REVERTED_LITERAL+attribute+VALUE_LITERAL+null);
					// Adding for getting the pop-up box at front end
					SMBlockingAttributes.multiMap.put(skuObj.getFlexType().getAttribute(attribute).getAttDisplay(), errorValue);
				}

			}else {
				if(previousColorway.getValue(attribute)!=null) {

					previousColor = (LCSColor) previousColorway.getValue(attribute);
					LOGGER.info(CHNAGES_FOUND_LITERAL + attribute
							+ VALUE_CURENT_LITERAL + currentColorName + PREVIOUS_VERSION_LITERAL
							+ previousColor.getName());
					skuObj.setValue(attribute, previousColor);
					LOGGER.info(REVERTED_LITERAL+attribute+VALUE_LITERAL+previousColor.getName());
					// Adding for getting the pop-up box at front end
					SMBlockingAttributes.multiMap.put(skuObj.getFlexType().getAttribute(attribute).getAttDisplay(), errorValue);
				}
			}
		} catch (WTException e) {
			LOGGER.error(ERROR_FOUND_LITERAL,e);
		} catch (WTPropertyVetoException e) {
			LOGGER.error(ERROR_FOUND_LITERAL,e);
		}
	}

	/**
	 *  Set colorway season status to colorway season blocked attribute.
	 * @param errorValue - value for pop message.
	 * @param attributeLimit - colorway season attribute limit internal value.
	 * @param attributeLimitValues - colorway season attribute limit value.
	 * @param skuSeasonObj - sku season object.
	 * @param preSkuSeasonObj - previous version of sku season object.
	 */
	public static void setColorwaySeasonStatus(String errorValue, List<String> attributeLimit,
			List<String> attributeLimitValues, LCSSKUSeasonLink skuSeasonObj, LCSSKUSeasonLink preSkuSeasonObj)  {
		String previousColorwaySeasonStatus;
		String currentColorwaySeasonStatus;
		if(preSkuSeasonObj==null) {
			return;
		}
		try {
			if(skuSeasonObj.getValue(attributeLimit.get(0))!=null) {

				currentColorwaySeasonStatus=(String) skuSeasonObj.getValue(attributeLimit.get(0));

				if(preSkuSeasonObj.getValue(attributeLimit.get(0))!=null) {

					previousColorwaySeasonStatus=(String) preSkuSeasonObj.getValue(attributeLimit.get(0));

					if (FormatHelper.hasContent(previousColorwaySeasonStatus) && !currentColorwaySeasonStatus.equals(previousColorwaySeasonStatus)
							&& currentColorwaySeasonStatus.equalsIgnoreCase(attributeLimitValues.get(0))) {
						LOGGER.info(CHNAGES_FOUND_LITERAL + attributeLimitValues.get(0) + VALUE_CURENT_LITERAL
								+ currentColorwaySeasonStatus + PREVIOUS_VERSION_LITERAL
								+ previousColorwaySeasonStatus);
						// set previous value
						skuSeasonObj.setValue(attributeLimit.get(0), previousColorwaySeasonStatus);
						LOGGER.info(REVERTED_LITERAL + attributeLimitValues.get(0) + VALUE_LITERAL
								+ previousColorwaySeasonStatus);
						// Adding for getting the pop-up box at front end
						SMBlockingAttributes.multiMap.put(
								skuSeasonObj.getFlexType().getAttribute(attributeLimit.get(0)).getAttDisplay(),
								errorValue);
					}
				}
			}
		} catch (WTException e) {
			LOGGER.error(ERROR_FOUND_LITERAL,e);
		}
	}

	/**
	 * Set previous LLT to colorway season blocked attribute.
	* @param errorValue - value for pop message.
	 * @param colorwaySeasonAttributes - colorway season aatributes list
	 * @param previousSkuSeason - previous sku season object.
	 * @param skuSeasonObj - sku season object.
	 */
	public static void setPreviousLLT(String errorValue, List<String> colorwaySeasonAttributes,
			LCSSKUSeasonLink previousSkuSeason, LCSSKUSeasonLink skuSeasonObj) {
		boolean curentLLTValue;
		boolean previousLLTValue;
		if(previousSkuSeason ==null) {
			return;
		}

		try {
			if (skuSeasonObj.getValue(colorwaySeasonAttributes.get(0)) != null) {

				curentLLTValue=(Boolean) skuSeasonObj.getValue(colorwaySeasonAttributes.get(0));

				if(previousSkuSeason.getValue(colorwaySeasonAttributes.get(0))!=null) {

					previousLLTValue=(Boolean) previousSkuSeason.getValue(colorwaySeasonAttributes.get(0));

					if (curentLLTValue!=previousLLTValue) {
						LOGGER.info(CHNAGES_FOUND_LITERAL + colorwaySeasonAttributes.get(0)
						+ VALUE_CURENT_LITERAL + curentLLTValue + PREVIOUS_VERSION_LITERAL
						+ previousLLTValue);
						skuSeasonObj.setValue(colorwaySeasonAttributes.get(0), previousLLTValue);
						LOGGER.info(REVERTED_LITERAL+colorwaySeasonAttributes.get(0)+VALUE_LITERAL+previousLLTValue);
						// Adding for getting the pop-up box at front end
						SMBlockingAttributes.multiMap.put(skuSeasonObj.getFlexType()
								.getAttribute(colorwaySeasonAttributes.get(0)).getAttDisplay(), errorValue);
					}
				}else {
					LOGGER.info(CHNAGES_FOUND_LITERAL + colorwaySeasonAttributes.get(0)
					+ VALUE_CURENT_LITERAL + curentLLTValue + PREVIOUS_VERSION_LITERAL
					+ null);
					skuSeasonObj.setValue(colorwaySeasonAttributes.get(0), null);
					LOGGER.info(REVERTED_LITERAL+colorwaySeasonAttributes.get(0)+VALUE_LITERAL+null);
					// Adding for getting the pop-up box at front end
					SMBlockingAttributes.multiMap.put(skuSeasonObj.getFlexType()
							.getAttribute(colorwaySeasonAttributes.get(0)).getAttDisplay(), errorValue);
				}


			}
		} catch (WTPropertyVetoException e) {
			LOGGER.error(ERROR_FOUND_LITERAL,e);
		} catch (WTException e) {
			LOGGER.error(ERROR_FOUND_LITERAL,e);
		}

	}

	/**
	 *  Set previous MHP value to colorway season blocked attribute.
	 * @param errorValue - value for pop message.
	 * @param colorwaySeasonAttributes - colorway season attribute list.
	 * @param previousSkuSeason - previous sku season object.
	 * @param skuSeasonObj - sku season object.
	 */
	public static void setPreviousMHP(String errorValue, List<String> colorwaySeasonAttributes,
			LCSSKUSeasonLink previousSkuSeason, LCSSKUSeasonLink skuSeasonObj) {
		boolean curentValue;
		boolean previousValue;
		if(previousSkuSeason ==null) {
			return;
		}

		try {
			if (skuSeasonObj.getValue(colorwaySeasonAttributes.get(1)) != null) {

				curentValue=(Boolean) skuSeasonObj.getValue(colorwaySeasonAttributes.get(1));

				if(previousSkuSeason.getValue(colorwaySeasonAttributes.get(1))!=null) {

					previousValue=(Boolean) previousSkuSeason.getValue(colorwaySeasonAttributes.get(1));

					if (curentValue!=previousValue) {
						LOGGER.info(CHNAGES_FOUND_LITERAL + colorwaySeasonAttributes.get(1)
						+ VALUE_CURENT_LITERAL + curentValue + PREVIOUS_VERSION_LITERAL
						+ previousValue);
						skuSeasonObj.setValue(colorwaySeasonAttributes.get(1), previousValue);
						LOGGER.info(REVERTED_LITERAL+colorwaySeasonAttributes.get(1)+VALUE_LITERAL+previousValue);
						// Adding for getting the pop-up box at front end
						SMBlockingAttributes.multiMap.put(skuSeasonObj.getFlexType()
								.getAttribute(colorwaySeasonAttributes.get(1)).getAttDisplay(), errorValue);
					}
				}else {
					LOGGER.info(CHNAGES_FOUND_LITERAL + colorwaySeasonAttributes.get(1)
					+ VALUE_CURENT_LITERAL + curentValue + PREVIOUS_VERSION_LITERAL
					+ null);
					skuSeasonObj.setValue(colorwaySeasonAttributes.get(1), null);
					LOGGER.info(REVERTED_LITERAL+colorwaySeasonAttributes.get(1)+VALUE_LITERAL+null);
					// Adding for getting the pop-up box at front end
					SMBlockingAttributes.multiMap.put(skuSeasonObj.getFlexType()
							.getAttribute(colorwaySeasonAttributes.get(1)).getAttDisplay(), errorValue);
				}


			}
		} catch (WTPropertyVetoException e) {
			LOGGER.error(ERROR_FOUND_LITERAL,e);
		} catch (WTException e) {
			LOGGER.error(ERROR_FOUND_LITERAL,e);
		}

	}

	/**
	 * Set previous SSL factory value to source to season blocked attribute.
	 * @param previousSourceToSeason - previous source to season object.
	 * @param sourceSeasonAttributes - source to season attribute list.
	 * @param ssl - source to season link object.
	 * @param product - product object.
	 * @param sConfig - sourcing config object.
	 * @throws WTException throw WTException.
	 * @throws WTPropertyVetoException - throw WTPropertyVetoException.
	 */
	public static void setPreviousSSLFactory(LCSSourceToSeasonLink previousSourceToSeason,
			List<String> sourceSeasonAttributes, LCSSourceToSeasonLink ssl, LCSProduct product,
			LCSSourcingConfig sConfig) throws WTException, WTPropertyVetoException {

		String currentSslFactoryName = "";
		String previousSslFactoryName = "";
		LCSSupplier previousSslFactory = null;
		LCSSupplier currentSslFactory = null;

		// Getting previous value of sslFactory attribute
		previousSslFactory = (LCSSupplier) previousSourceToSeason.getValue(sourceSeasonAttributes.get(1));

		// Getting current value of sslFactory attribute
		currentSslFactory = (LCSSupplier) ssl.getValue(sourceSeasonAttributes.get(1));

		if (currentSslFactory != null) {
			// getting current name of sslFactory Attribute
			currentSslFactoryName = currentSslFactory.getName();
			LOGGER.debug("Current SslFactory Name:-" + currentSslFactoryName);
		}

		if (previousSslFactory != null) {
			// getting previous name of sslFactory Attribute
			previousSslFactoryName = previousSslFactory.getName();
			LOGGER.debug("Previous SslFactory Name:-" + previousSslFactoryName);
		}

		// Comparing the name of current sslFactory aatribute to prevoius sslFactory
		// attribute
		if (!currentSslFactoryName.equals(previousSslFactoryName)) {
			LOGGER.info(CHNAGES_FOUND_LITERAL + sourceSeasonAttributes.get(1)
			+ VALUE_CURENT_LITERAL + currentSslFactoryName + PREVIOUS_VERSION_LITERAL
			+ previousSslFactoryName);
			// set previous value
			ssl.setValue(sourceSeasonAttributes.get(1), previousSslFactory);
			LOGGER.info(REVERTED_LITERAL+sourceSeasonAttributes.get(1)+VALUE_LITERAL+previousSslFactoryName);
			// Adding for getting the pop-up box at front end
			SMBlockingAttributes.multiMap.put(
					ssl.getFlexType().getAttribute(sourceSeasonAttributes.get(1)).getAttDisplay(),
					String.valueOf(product.getValue(STYLE_NUM)) + "-" + sConfig.getName());
		}
	}

	/**
	 * Validate SourceSeason Lifecycle State.
	 * @param ssl - source to season link object.
	 * @param sourceSeasonAttributes - source to season attributes list. 
	 * @param product - product object.
	 * @param sConfig - sourcing config object.
	 */
	public static void validateSourceSeasonLifecycleSate(LCSSourceToSeasonLink ssl, List<String> sourceSeasonAttributes,
			LCSProduct product, LCSSourcingConfig sConfig){

		LCSSourceToSeasonLink previousSourceToSeason = null;

		// set previous SSL factory value
		try {

			// Getting previous version of source to season Link
			previousSourceToSeason = (LCSSourceToSeasonLink) VersionHelper.predecessorOf(ssl);
			setPreviousSSLFactory(previousSourceToSeason, sourceSeasonAttributes, ssl, product, sConfig);
		} catch (WTException e) {
			LOGGER.error(ERROR_FOUND_LITERAL,e);
		} catch (WTPropertyVetoException e) {
			LOGGER.error(ERROR_FOUND_LITERAL,e);
		}
	}
	
	/**
	 * Get Colorway Color and set to error pop-up message
	 * @param color - colorway color objet.
	 * @param prodSeasonRev - product season object. 
	 */
	public static String getColorwayColor(LCSColor color, LCSProduct prodSeasonRev) {
		String errorValue = "";
		try {
			if (color != null) {
				errorValue = String.valueOf(prodSeasonRev.getValue(STYLE_NUM)) + "-" + color.getName();
			}
		} catch (WTException e) {
			LOGGER.error(ERROR_FOUND_LITERAL,e);
		}
		return errorValue;

	}
}
