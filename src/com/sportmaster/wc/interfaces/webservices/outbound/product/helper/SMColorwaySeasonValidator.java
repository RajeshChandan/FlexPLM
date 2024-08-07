/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.helper;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;

/**
 * @author ITC_Infotech.
 *
 */
public class SMColorwaySeasonValidator {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMColorwaySeasonValidator.class);
	/**
	 * WSDL Validation failure.
	 */
	private static final String COLORWAY_SEASON_WSDL_VALIDATION_FAILURE = "Failure in validation of Colorway Season Link WSDL  :  ";
	/**
	 * Attribute key on which validation failed.
	 */
	private static final String COLORWAY_SEASON_ATTRIBUTE_KEY_FAILED = "Failed on Colorway Season link attribute  >>>   ";
	/**
	 * protected constructor.
	 */
	public SMColorwaySeasonValidator(){
		//public constructor.
	}

	/**
	 * Validae SKU Season Link WSDL.
	 * @param ssl - LCSSKuSeasonLink
	 * @return - boolean.
	 */
	public boolean validateColorwaySeasonWSDL(LCSSKUSeasonLink ssl, SMProductOutboundIntegrationBean bean){
		try{
			bean.setColorwaySeasonWSDLValidationErrorMessage("");
			LCSSKU skuSeasonRev = (LCSSKU) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:"+(int)ssl.getSkuSeasonRevId());
			LCSSeason season = VersionHelper.latestIterationOf(ssl.getSeasonMaster());

			if(!FormatHelper.hasContent((String) ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_RETAIL_DESTINATION))){
				LCSProduct prodSeasonRev = SeasonProductLocator.getProductSeasonRev(skuSeasonRev);
				prodSeasonRev = (LCSProduct) VersionHelper.latestIterationOf(prodSeasonRev);
				LCSProductSeasonLink prodSeasonLink = (LCSProductSeasonLink) SeasonProductLocator.getSeasonProductLink(prodSeasonRev);
				
				if (!FormatHelper.hasContent((String) prodSeasonLink
						.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_RETAIL_DESTINATION))) {
					bean.setColorwaySeasonWSDLValidationErrorMessage(
							COLORWAY_SEASON_WSDL_VALIDATION_FAILURE + COLORWAY_SEASON_ATTRIBUTE_KEY_FAILED
									+ SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_RETAIL_DESTINATION);
					LOGGER.error(bean.getColorwaySeasonWSDLValidationErrorMessage());
					return false;
				}
			}
			
			if (!FormatHelper.hasContent((String) ssl
					.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_COLORWAY_SEASON_STATUS))) {
				bean.setColorwaySeasonWSDLValidationErrorMessage(
						COLORWAY_SEASON_WSDL_VALIDATION_FAILURE + COLORWAY_SEASON_ATTRIBUTE_KEY_FAILED
								+ SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_COLORWAY_SEASON_STATUS);
				LOGGER.error(bean.getColorwaySeasonWSDLValidationErrorMessage());
				return false;
			}
			
			if (!FormatHelper
					.hasContent((String) season.getValue(SMProductOutboundWebServiceConstants.SEASON_MDM_ID))) {
				bean.setColorwaySeasonWSDLValidationErrorMessage(COLORWAY_SEASON_WSDL_VALIDATION_FAILURE
						+ COLORWAY_SEASON_WSDL_VALIDATION_FAILURE + SMProductOutboundWebServiceConstants.SEASON_MDM_ID);
				LOGGER.error(bean.getColorwaySeasonWSDLValidationErrorMessage());
				return false;
			}
			
			return true;
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage(), we);
			return false;
		}
	}

}
