/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.helper;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;

/**
 * @author ITC_Infotech.
 *
 */
public class SMProductSeasonWSDLValidator {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductSeasonWSDLValidator.class);
	/**
	 * WSDL Validation failure.
	 */
	private static final String PRODUCT_SEASON_WSDL_VALIDATION_FAILURE = "Failure in validation of Product Season WSDL :  ";
	/**
	 * Attribute key on which validation failed.
	 */
	private static final String PRODUCT_SEASON_ATTRIBUTE_KEY_FAILED = "Failed on product season link attribute  >>>  ";
	/**
	 * protected constructor.
	 */
	public SMProductSeasonWSDLValidator(){
		//public constructor.
	}

	/**
	 * validates product season link with WSDL.
	 * @param psl - LCSProductSeasonLink.
	 * @return - Boolean.
	 */
	public boolean validateProductSeasonWSDL(LCSProductSeasonLink psl, SMProductOutboundIntegrationBean bean){

		try{
			LCSSeason season = VersionHelper.latestIterationOf(psl.getSeasonMaster());

			//checking retail destination.
			if(!FormatHelper.hasContent((String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_RETAIL_DESTINATION))){
				bean.setProductSeasonValidationErrorMessage(PRODUCT_SEASON_WSDL_VALIDATION_FAILURE+PRODUCT_SEASON_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_RETAIL_DESTINATION);
				LOGGER.error(bean.getProductSeasonValidationErrorMessage());
				return false;
			}

			//checking brand manager.
			FlexObject user = (FlexObject) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_BRAND_MANAGER);
			if(user == null){
				bean.setProductSeasonValidationErrorMessage(PRODUCT_SEASON_WSDL_VALIDATION_FAILURE+PRODUCT_SEASON_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_BRAND_MANAGER);
				LOGGER.error(bean.getProductSeasonValidationErrorMessage());
				return false;
			}

			//checking season MDM id.
			if(!FormatHelper.hasContent((String) season.getValue(SMProductOutboundWebServiceConstants.SEASON_MDM_ID))){
				bean.setProductSeasonValidationErrorMessage(PRODUCT_SEASON_WSDL_VALIDATION_FAILURE+PRODUCT_SEASON_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.SEASON_MDM_ID);
				LOGGER.error(bean.getProductSeasonValidationErrorMessage());
				return false;
			}

			//checking status style seasonal.
			if(!FormatHelper.hasContent((String) psl.getValue(SMProductOutboundWebServiceConstants.STATUS_STYLE_SEASONAL))){
				bean.setProductSeasonValidationErrorMessage(PRODUCT_SEASON_WSDL_VALIDATION_FAILURE+PRODUCT_SEASON_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.STATUS_STYLE_SEASONAL);
				LOGGER.error(bean.getProductSeasonValidationErrorMessage());
				return false;
			}

			/*
			 * Updated code for PHASE -8  SEPD Changes.
			 * REMOVED SALES NEWNESS Attribute validation check, its no longer mandatory on WSDL.
			 * 
			 * checking sales newness. if(!FormatHelper.hasContent((String)
			 * psl.getValue(SMProductOutboundWebServiceConstants.
			 * PRODUCT_SEASON_LINK_SALES_NEWNESS))){
			 * setProductSeasonValidationErrorMessage(PRODUCT_SEASON_WSDL_VALIDATION_FAILURE
			 * +PRODUCT_SEASON_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.
			 * PRODUCT_SEASON_LINK_SALES_NEWNESS);
			 * LOGGER.error(getProductSeasonValidationErrorMessage()); return false; }
			 */

			//checking production group.
			if(!FormatHelper.hasContent((String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_PRODUCTION_GROUP))){
				bean.setProductSeasonValidationErrorMessage(PRODUCT_SEASON_WSDL_VALIDATION_FAILURE+PRODUCT_SEASON_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_PRODUCTION_GROUP);
				LOGGER.error(bean.getProductSeasonValidationErrorMessage());
				return false;
			}

			return true;
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage(), we);
			bean.setProductSeasonValidationErrorMessage(we.getLocalizedMessage());
			return false;
		}

	}

}
