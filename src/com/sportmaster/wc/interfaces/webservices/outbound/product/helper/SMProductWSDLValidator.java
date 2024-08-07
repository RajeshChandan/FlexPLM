/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.helper;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;

/**
 * @author ITC_Infotech.
 *
 */
public class SMProductWSDLValidator {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductWSDLValidator.class);
	/**
	 * WSDL Validation failure.
	 */
	private static final String PRODUCT_WSDL_VALIDATION_FAILURE = "Failure in validation of Product WSDL :  ";
	/**
	 * Attribute key on which validation failed.
	 */
	private static final String PRODUCT_ATTRIBUTE_KEY_FAILED = "Failed on product attribute  >>>  ";
	/**
	 * Protected constructor.
	 */
	public SMProductWSDLValidator(){
		//public constructor.
	}

	/**
	 * Validates product season link with WSDL.
	 * @param product - LCSProduct.
	 * @param psl - LCSProductSeasonLink.
	 * @return - boolean.
	 */
	public boolean validateProductWSDL(LCSProduct product, LCSProductSeasonLink psl, SMProductOutboundIntegrationBean bean){
		try{
			bean.setProductValidationErrorMessage("");
			//checking for brand.
			if(!FormatHelper.hasContent((String) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_BRAND))){
				bean.setProductValidationErrorMessage(PRODUCT_WSDL_VALIDATION_FAILURE+PRODUCT_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.PRODUCT_BRAND);
				LOGGER.error(bean.getProductValidationErrorMessage());
				return false;
			}

			/*
			 * commented for 3.9.0.0 Build, AS "vrdDescription" attribute is non mandatory for product
			 * //checking for description. if(!FormatHelper.hasContent((String)
			 * product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_DESCRIPTION))){
			 * bean.setProductValidationErrorMessage(PRODUCT_WSDL_VALIDATION_FAILURE+
			 * PRODUCT_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.
			 * PRODUCT_DESCRIPTION); LOGGER.error(bean.getProductValidationErrorMessage());
			 * return false; }
			 */

			//checking for age.
			if(!FormatHelper.hasContent((String) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_AGE))){
				bean.setProductValidationErrorMessage(PRODUCT_WSDL_VALIDATION_FAILURE+PRODUCT_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.PRODUCT_AGE);
				LOGGER.error(bean.getProductValidationErrorMessage());
				return false;
			}

			//checking for style number
			if(!FormatHelper.hasContent(String.valueOf(product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_NUMBER)))){
				bean.setProductValidationErrorMessage(PRODUCT_WSDL_VALIDATION_FAILURE+PRODUCT_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.PRODUCT_NUMBER);
				LOGGER.error(bean.getProductValidationErrorMessage());
				return false;
			}

			//checking for gender.
			if(!FormatHelper.hasContent((String) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_GENDER))){
				bean.setProductValidationErrorMessage(PRODUCT_WSDL_VALIDATION_FAILURE+PRODUCT_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.PRODUCT_GENDER);
				LOGGER.error(bean.getProductValidationErrorMessage());
				return false;
			}
			
			//chnaged for Phase-4 FPD chnages
			/*//checking for fabric group.
			if(!FormatHelper.hasContent((String) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_FABRIC_GROUP))){
				setProductValidationErrorMessage(PRODUCT_WSDL_VALIDATION_FAILURE+PRODUCT_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.PRODUCT_FABRIC_GROUP);
				LOGGER.error(getProductValidationErrorMessage());
				return false;
			}*/
			
			//chnaged for Phase-4 FPD chnages
			/*//checking for product fit.
			if(!FormatHelper.hasContent((String) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_FIT))){
				setProductValidationErrorMessage(PRODUCT_WSDL_VALIDATION_FAILURE+PRODUCT_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.PRODUCT_FIT);
				LOGGER.error(getProductValidationErrorMessage());
				return false;
			}*/


			//checking for sub class division reference.
			LCSLifecycleManaged subDivisionref = (LCSLifecycleManaged) product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SUB_DIVISION);


			if(subDivisionref == null){
				bean.setProductValidationErrorMessage(PRODUCT_WSDL_VALIDATION_FAILURE+PRODUCT_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.PRODUCT_SUB_DIVISION);
				LOGGER.error(bean.getProductValidationErrorMessage());
				return false;
			}else{ 
				if(!FormatHelper.hasContent((String) subDivisionref.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SUB_DIVISION_MDMID))){
					bean.setProductValidationErrorMessage(PRODUCT_WSDL_VALIDATION_FAILURE+PRODUCT_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.PRODUCT_SUB_DIVISION_MDMID);
					LOGGER.error(bean.getProductValidationErrorMessage());
					return false;
				}
			}
			return true;
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage(), we);
			bean.setProductValidationErrorMessage(we.getLocalizedMessage());
			return false;
		}
	}

}
