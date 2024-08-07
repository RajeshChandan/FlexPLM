package com.sportmaster.wc.interfaces.webservices.outbound.product.helper;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;



public class SMSourcingConfigWSDLValidator {

	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSourcingConfigWSDLValidator.class);
	/**
	 * WSDL Validation failure.
	 */
	private static final String SOURCING_CONFIG_WSDL_VALIDATION_FAILURE = "Failure in validation of Sourcing Configuration Link WSDL  :  ";
	/**
	 * Attribute key on which validation failed.
	 */
	private static final String SOURCING_CONFIGURATION_ATTRIBUTE_KEY_FAILED = "Failed on Sourcing Config Link attribute  >>>   ";

	/**
	 * protected constructor.
	 */
	public SMSourcingConfigWSDLValidator(){
		//public constructor.
	}

	/**
	 * Validates Sourcing Config WSDL.
	 * @param srcConfig - LCSSourcingConfig.
	 * @return - boolean.
	 */
	public boolean validateSourcingConfigWSDL(LCSSourcingConfig srcConfig, LCSProductSeasonLink psl, SMProductOutboundIntegrationBean bean){
		try{
			bean.setSourcingConfigWSDLValidationErrorMessage("");
			bean.setProductSeasonValidationErrorMessage("");
			//get season.
			LCSSeason season = VersionHelper.latestIterationOf(psl.getSeasonMaster());
			LOGGER.info("Season *******  "+season.getName());
			
			//getting business supplier object.
			LCSSupplier businessSupplier = (LCSSupplier) srcConfig.getValue(SMProductOutboundWebServiceConstants.SOURCING_CONFIGURATION_BUSINESS_SUPPLIER);
			
			//checking business supplier reference.
			if(null == businessSupplier){
				bean.setSourcingConfigWSDLValidationErrorMessage(SOURCING_CONFIG_WSDL_VALIDATION_FAILURE+SOURCING_CONFIGURATION_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.SOURCING_CONFIGURATION_BUSINESS_SUPPLIER);
				LOGGER.error(bean.getSourcingConfigWSDLValidationErrorMessage());
				bean.setProductSeasonValidationErrorMessage(bean.getSourcingConfigWSDLValidationErrorMessage());
				return false;
			} 
			
			
			//get source to season link.
			LCSSourceToSeasonLink sourceToSeasonLink = new LCSSourcingConfigQuery().getSourceToSeasonLink(srcConfig,
					bean.getAssociatedSeason());
			//checking source to season link.
			if(!FormatHelper.hasContent((String) sourceToSeasonLink.getValue(SMProductOutboundWebServiceConstants.SOURCING_CONFIGURATION_STATUS))){
				bean.setSourcingConfigWSDLValidationErrorMessage(SOURCING_CONFIG_WSDL_VALIDATION_FAILURE+SOURCING_CONFIGURATION_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.SOURCING_CONFIGURATION_STATUS);
				LOGGER.error(bean.getSourcingConfigWSDLValidationErrorMessage());
				bean.setProductSeasonValidationErrorMessage(bean.getSourcingConfigWSDLValidationErrorMessage());
				return false;
			}
			
			//checking season mdm id.
			if(!FormatHelper.hasContent((String) season.getValue(SMProductOutboundWebServiceConstants.SEASON_MDM_ID))){
				bean.setSourcingConfigWSDLValidationErrorMessage(SOURCING_CONFIG_WSDL_VALIDATION_FAILURE+SOURCING_CONFIGURATION_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.SEASON_MDM_ID);
				LOGGER.error(bean.getSourcingConfigWSDLValidationErrorMessage());
				bean.setProductSeasonValidationErrorMessage(bean.getSourcingConfigWSDLValidationErrorMessage());
				return false;
			}
			
			return true;
		
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage(), we);
			return false;
		}



	}

}
