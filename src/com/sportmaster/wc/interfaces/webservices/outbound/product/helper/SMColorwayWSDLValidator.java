/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.helper;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;

/**
 * @author ITC_Infotech.
 *
 */
public class SMColorwayWSDLValidator {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMColorwayWSDLValidator.class);
	/**
	 * WSDL Validation failure.
	 */
	private static final String COLORWAY_WSDL_VALIDATION_FAILURE = "Failure in validation of Colorway WSDL :  ";
	/**
	 * Attribute key on which validation failed.
	 */
	private static final String COLORWAY_ATTRIBUTE_KEY_FAILED = "Failed on Colorway attribute  >>>   ";
	/**
	 * protected constructor.
	 */
	public SMColorwayWSDLValidator(){
		//public constructor.
	}

	/**
	 * Validate Colorway WSDL.
	 * @param sku - LCSSKU
	 * @return - boolean
	 */
	public boolean validateColorwayWSDL(LCSSKU sku, SMProductOutboundIntegrationBean bean){
		try{
			bean.setColorwayWSDLValidationErrorMessage("");
			
			LCSColor colorwayColor = (LCSColor) sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_COLOR);
			
			if(!FormatHelper.hasContent((String) colorwayColor.getValue(SMProductOutboundWebServiceConstants.COLORWAY_COLOR_MDM_ID))){
				bean.setColorwayWSDLValidationErrorMessage(COLORWAY_WSDL_VALIDATION_FAILURE+COLORWAY_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.COLORWAY_COLOR_MDM_ID);
				LOGGER.error(bean.getColorwayWSDLValidationErrorMessage());
				return false;
			}
			
			if(!FormatHelper.hasContent(sku.getName())){
				bean.setColorwayWSDLValidationErrorMessage(COLORWAY_WSDL_VALIDATION_FAILURE+COLORWAY_ATTRIBUTE_KEY_FAILED+"skuName");
				LOGGER.error(bean.getColorwayWSDLValidationErrorMessage());
				return false;
			}
			
			return true;
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage(), we);
			return false;
			
		}
	}
}
