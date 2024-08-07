/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.helper;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSCostSheet;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;

/**
 * @author BSC
 * Validates Cost Sheet WSDL.
 */
public class SMCostSheetWSDLValidator {
	/**
	 * APPAREL_ACCESSORIES Type
	 */
	private static final String APPAREL_ACCESSORIES = "Apparel\\Accessories";
	/**
	 * APPAREL_MCACCESSORIES Type
	 */
	private static final String APPAREL_MCACCESSORIES = "Apparel\\1. Multicurrency Accessories SEPD";

	/**
	 * COSTING_APPAREL_MULTICURRENCY_APP Type
	 */
	private static final String COSTING_APPAREL_MULTICURRENCY_APP = "Apparel\\0. Multicurrency Apparel";
	
	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMCostSheetWSDLValidator.class);

	/**
	 * WSDL Validation failure.
	 */
	private static final String COST_SHEET_WSDL_VALIDATION_FAILURE = "Failure in validation of Product Cost Sheet WSDL  :  ";
	/**
	 * Attribute key on which validation failed.
	 */
	private static final String COST_SHEET_ATTRIBUTE_KEY_FAILED = "Failed on Product Cost Sheet attribute  >>>   ";

	/**
	 * protected constructor.
	 */
	public SMCostSheetWSDLValidator(){
		//public constructor.
	}

	/**
	 * Validates Cost Sheet Attributes.
	 * @param costSheet - LCSProductCostSheet.
	 * @return - boolean.
	 */
	public boolean validateProductCostSheet(LCSProductCostSheet costSheet, SMProductOutboundIntegrationBean bean){
		try{
			bean.setCostSheetWSDLValidationErrorMessage("");
			bean.setProductSeasonValidationErrorMessage("");
			LOGGER.info("Cost Sheet Season master  >>>>>>>>>    "+costSheet.getSeasonMaster());
			if(null != costSheet.getSeasonMaster()){
				LCSSeason season = VersionHelper.latestIterationOf(costSheet.getSeasonMaster());

				//getting season mdm id.
				if(!FormatHelper.hasContent((String) season.getValue(SMProductOutboundWebServiceConstants.SEASON_MDM_ID))){
					bean.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE+COST_SHEET_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.SEASON_MDM_ID);
					LOGGER.error(bean.getCostSheetWSDLValidationErrorMessage());
					bean.setProductSeasonValidationErrorMessage(bean.getCostSheetWSDLValidationErrorMessage());
					return false;
				}

				//getting cost sheet currency.
				if(!FormatHelper.hasContent((String) costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_CONTRACT_CURRENCY))){
					bean.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE+COST_SHEET_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.COST_SHEET_CONTRACT_CURRENCY);
					LOGGER.error(bean.getCostSheetWSDLValidationErrorMessage());
					bean.setProductSeasonValidationErrorMessage(bean.getCostSheetWSDLValidationErrorMessage());
					return false;
				}

				//checking incoterms.
				if(!FormatHelper.hasContent((String) costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_INCOTERMS))){
					bean.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE+COST_SHEET_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.COST_SHEET_INCOTERMS);
					LOGGER.error(bean.getCostSheetWSDLValidationErrorMessage());
					bean.setProductSeasonValidationErrorMessage(bean.getCostSheetWSDLValidationErrorMessage());
					return false;
				}
				String costingStageKey = costingStageValidation(costSheet);
				
				//checking costing stage.
				if(!FormatHelper.hasContent((String) costSheet.getValue(costingStageKey))){
					bean.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE+COST_SHEET_ATTRIBUTE_KEY_FAILED+costingStageKey);
					LOGGER.error(bean.getCostSheetWSDLValidationErrorMessage());
					bean.setProductSeasonValidationErrorMessage(bean.getCostSheetWSDLValidationErrorMessage());
					return false;
				}
				//updated for PHASE -12 FPD/Multicurrency CHANGE - End
				

				return true;
			}else{
				return false;
			}
		}catch(WTException e){
			LOGGER.error(e.getLocalizedMessage(),e);
			return false;
		}
	}

	/**
	 * @param costSheet
	 * @return
	 */
	public String costingStageValidation(LCSProductCostSheet costSheet) {
		// updated for Phase - 13 - CS/Apparel/MC Changes
		//updated for PHASE -12 FPD/Multicurrency CHANGES - Start
		//updated for PHASE -8 SEPD CHANGES
		//Initializing for Costing Stage attribute key
		String costingStageKey = SMProductOutboundWebServiceConstants.COST_SHEET_COSTING_STAGE;
		
		//checking for footwear multi currency costsheet type
		if(costSheet.getFlexType().getFullNameDisplay().startsWith("Footwear\\Multicurrency")) {
			
			costingStageKey = SMProductOutboundWebServiceConstants.MULTI_COSTING_STAGE;
			
		}else if (costSheet.getFlexType().getFullNameDisplay().startsWith("Sports Equipment")
				|| APPAREL_ACCESSORIES.equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())
				|| APPAREL_MCACCESSORIES.equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			
			costingStageKey = SMProductOutboundWebServiceConstants.SEPD_COSTING_STAGE;
		//-- //Phase 14 - APD MultiCurrency: START //
		}else if(COSTING_APPAREL_MULTICURRENCY_APP.equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())){
			costingStageKey = SMProductOutboundWebServiceConstants.CS_MCAPP_COSTING_STAGE_KEY;
		}
		//-- //Phase 14 - APD MultiCurrency: END //
				
		return costingStageKey;
	}

	/**
	 * Validates Cost Sheet Attributes.
	 * @param costSheetRFQ - LCSProductCostSheet.
	 * @return - boolean.
	 */
	public boolean validateRFQCostSheet(LCSProductCostSheet costSheetRFQ,FlexObject rfqCSFO, SMProductOutboundIntegrationBean beanObj){
		boolean valid = false;
		try{
			beanObj.setCostSheetWSDLValidationErrorMessage("");
			beanObj.setProductSeasonValidationErrorMessage("");
			LOGGER.info("RFQ Cost Sheet Season master  >>>>>>>>>    "+costSheetRFQ.getSeasonMaster());
			if(null != costSheetRFQ.getSeasonMaster()){
				LCSSeason season = VersionHelper.latestIterationOf(costSheetRFQ.getSeasonMaster());

				//getting season mdm id.
				if(!FormatHelper.hasContent((String) season.getValue(SMProductOutboundWebServiceConstants.SEASON_MDM_ID))){
					beanObj.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE+COST_SHEET_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.SEASON_MDM_ID);
					LOGGER.error(beanObj.getCostSheetWSDLValidationErrorMessage());
					beanObj.setProductSeasonValidationErrorMessage(beanObj.getCostSheetWSDLValidationErrorMessage());
					return false;
				}

				//getting cost sheet currency.
				if(!FormatHelper.hasContent((String) costSheetRFQ.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_CONTRACT_CURRENCY))){
					beanObj.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE+COST_SHEET_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.COST_SHEET_CONTRACT_CURRENCY);
					LOGGER.error(beanObj.getCostSheetWSDLValidationErrorMessage());
					beanObj.setProductSeasonValidationErrorMessage(beanObj.getCostSheetWSDLValidationErrorMessage());
					return false;
				}

				//checking incoterms.
				if(!FormatHelper.hasContent((String) costSheetRFQ.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_INCOTERMS))){
					beanObj.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE+COST_SHEET_ATTRIBUTE_KEY_FAILED+SMProductOutboundWebServiceConstants.COST_SHEET_INCOTERMS);
					LOGGER.error(beanObj.getCostSheetWSDLValidationErrorMessage());
					beanObj.setProductSeasonValidationErrorMessage(beanObj.getCostSheetWSDLValidationErrorMessage());
					return false;
				}

				// Phase -13 | splitted for soanr fix | added
				// validateAdditionalRFQFields function to validate rfq cs
				// field.
				valid = validateAdditionalRFQFields(beanObj, costSheetRFQ, rfqCSFO);


			}else{
				valid = false;
			}
		}catch(WTException e){
			LOGGER.error(e.getLocalizedMessage(),e);
			valid = false;
		}
		LOGGER.debug("Validation status >>>>" + valid);
		return valid;
	}

	/**
	 * Phase -13 | added validateAdditionalRFQFields function to validate rfq cs
	 * field.
	 * 
	 * @param bean
	 * @param costSheet
	 * @return
	 * @throws WTException
	 */
	private boolean validateAdditionalRFQFields(SMProductOutboundIntegrationBean bean, LCSCostSheet costSheet, FlexObject rfqCSFO)
			throws WTException {
		// checking vrdCSStatus.
		// updated for 3.9.1 build
		String status = String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_STATUS));
		if (APPAREL_ACCESSORIES.equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			status = String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.ACC_COST_SHEET_STATUS));
		}//-- //Phase 14 - APD MultiCurrency: START //
		else if(COSTING_APPAREL_MULTICURRENCY_APP.equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			status = String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.CS_MCAPP_COSTING_STATUS_KEY));
			if (!FormatHelper.hasContent(status)) {
				bean.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE + COST_SHEET_ATTRIBUTE_KEY_FAILED
						+ SMProductOutboundWebServiceConstants.CS_MCAPP_COSTING_STATUS_KEY);
				LOGGER.error(bean.getCostSheetWSDLValidationErrorMessage());
				bean.setProductSeasonValidationErrorMessage(bean.getCostSheetWSDLValidationErrorMessage());
				return false;
			}
		}//-- //Phase 14 - APD MultiCurrency: END //
		
		if (!FormatHelper.hasContent(status)) {
			bean.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE + COST_SHEET_ATTRIBUTE_KEY_FAILED
					+ SMProductOutboundWebServiceConstants.COST_SHEET_STATUS);
			LOGGER.error(bean.getCostSheetWSDLValidationErrorMessage());
			bean.setProductSeasonValidationErrorMessage(bean.getCostSheetWSDLValidationErrorMessage());
			return false;
		}

		// updated for PHASE -8 SEPD CHNaGES
		if (costSheet.getFlexType().getFullNameDisplay().startsWith("Sports Equipment")
				|| APPAREL_ACCESSORIES.equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {

			// checking costing stage.
			if (!FormatHelper.hasContent((String) costSheet.getValue(SMProductOutboundWebServiceConstants.SEPD_COSTING_STAGE))) {
				bean.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE + COST_SHEET_ATTRIBUTE_KEY_FAILED
						+ SMProductOutboundWebServiceConstants.SEPD_COSTING_STAGE);
				LOGGER.error(bean.getCostSheetWSDLValidationErrorMessage());
				bean.setProductSeasonValidationErrorMessage(bean.getCostSheetWSDLValidationErrorMessage());
				return false;
			}
		//Phase 14 - APD MultiCurrency: START //
		} else if(COSTING_APPAREL_MULTICURRENCY_APP.equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			// checking costing stage.
			if (!FormatHelper.hasContent((String)costSheet.getValue(SMProductOutboundWebServiceConstants.CS_MCAPP_COSTING_STAGE_KEY))) {
				bean.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE + COST_SHEET_ATTRIBUTE_KEY_FAILED
						+ SMProductOutboundWebServiceConstants.CS_MCAPP_COSTING_STAGE_KEY);
				LOGGER.error(bean.getCostSheetWSDLValidationErrorMessage());
				bean.setProductSeasonValidationErrorMessage(bean.getCostSheetWSDLValidationErrorMessage());
				return false;
			}
		//Phase 14 - APD MultiCurrency: END //
		}else {

			// checking costing stage.
			if (!FormatHelper.hasContent((String) costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_COSTING_STAGE))) {
				bean.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE + COST_SHEET_ATTRIBUTE_KEY_FAILED
						+ SMProductOutboundWebServiceConstants.COST_SHEET_COSTING_STAGE);
				LOGGER.error(bean.getCostSheetWSDLValidationErrorMessage());
				bean.setProductSeasonValidationErrorMessage(bean.getCostSheetWSDLValidationErrorMessage());
				return false;
			}
		}
		// Phase -13 | splitted for soanr fix | added
		// validateAdditionalRFQFields function to validate rfq cs
		// field.
		return validateMoreAdditionalRFQField(rfqCSFO, bean, costSheet);
	}

	/**
	 * Phase -13 | added validateAdditionalRFQFields function to validate rfq cs
	 * field.
	 * 
	 * @param rfqCSFO
	 * @param bean
	 * @param costSheet
	 * @return
	 * @throws WTException
	 */
	private boolean validateMoreAdditionalRFQField(FlexObject rfqCSFO, SMProductOutboundIntegrationBean bean, LCSCostSheet costSheet)
			throws WTException {

		// validate RFQ
		if (!FormatHelper.hasContent(rfqCSFO.getString("RFQREQUEST.BRANCHIDITERATIONINFO"))
				|| !FormatHelper.hasContent(rfqCSFO.getString("RFQREQUEST." + FlexTypeCache
						.getFlexTypeRootByClass("com.lcs.wc.sourcing.RFQRequest").getAttribute("ptcrfqName").getColumnName()))) {

			bean.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE + COST_SHEET_ATTRIBUTE_KEY_FAILED
					+ FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.sourcing.RFQRequest").getAttribute("ptcrfqName").getAttributeName());
			LOGGER.error(bean.getCostSheetWSDLValidationErrorMessage());
			bean.setProductSeasonValidationErrorMessage(bean.getCostSheetWSDLValidationErrorMessage());
			return false;
		}

		// validate quote
		if (!FormatHelper.hasContent(rfqCSFO.getString("RFQRESPONSE.BRANCHIDITERATIONINFO"))
				|| !FormatHelper.hasContent(rfqCSFO.getString("RFQRESPONSE." + FlexTypeCache
						.getFlexTypeRootByClass("com.lcs.wc.sourcing.RFQResponse").getAttribute("responseName").getColumnName()))) {

			bean.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE + COST_SHEET_ATTRIBUTE_KEY_FAILED
					+ FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.sourcing.RFQResponse").getAttribute("responseName").getAttDisplay());
			LOGGER.error(bean.getCostSheetWSDLValidationErrorMessage());
			bean.setProductSeasonValidationErrorMessage(bean.getCostSheetWSDLValidationErrorMessage());
			return false;
		}

		// validate quote vendor
		if (FormatHelper.hasContent(rfqCSFO.getString("RFQVENDOR.BRANCHIDITERATIONINFO"))) {
			LCSSupplier splr = (LCSSupplier) LCSMaterialQuery
					.findObjectById("VR:com.lcs.wc.supplier.LCSSupplier:" + rfqCSFO.getString("RFQVENDOR.BRANCHIDITERATIONINFO"));
			if (!FormatHelper.hasContent(String.valueOf(splr.getValue(SMProductOutboundWebServiceConstants.RFQ_VENDOR_MDM_ID)))) {

				bean.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE + COST_SHEET_ATTRIBUTE_KEY_FAILED
						+ SMProductOutboundWebServiceConstants.RFQ_VENDOR_MDM_ID);
				LOGGER.error(bean.getCostSheetWSDLValidationErrorMessage());
				bean.setProductSeasonValidationErrorMessage(bean.getCostSheetWSDLValidationErrorMessage());
				return false;
			}
		}

		// checking pricing country.
		if (!FormatHelper.hasContent(String.valueOf((costSheet.getValue(SMProductOutboundWebServiceConstants.PRICING_COUNTRY))))) {

			bean.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE + COST_SHEET_ATTRIBUTE_KEY_FAILED
					+ SMProductOutboundWebServiceConstants.PRICING_COUNTRY);
			LOGGER.error(bean.getCostSheetWSDLValidationErrorMessage());
			bean.setProductSeasonValidationErrorMessage(bean.getCostSheetWSDLValidationErrorMessage());
			return false;
		} else if (FormatHelper.hasContent(String.valueOf((costSheet.getValue(SMProductOutboundWebServiceConstants.PRICING_COUNTRY))))) {

			LCSCountry country = (LCSCountry) costSheet.getValue(SMProductOutboundWebServiceConstants.PRICING_COUNTRY);
			if (!FormatHelper.hasContent(String.valueOf(country.getValue(SMProductOutboundWebServiceConstants.PRICING_COUNTRY_MDMID)))) {

				bean.setCostSheetWSDLValidationErrorMessage(COST_SHEET_WSDL_VALIDATION_FAILURE + COST_SHEET_ATTRIBUTE_KEY_FAILED
						+ SMProductOutboundWebServiceConstants.PRICING_COUNTRY);
				LOGGER.error(bean.getCostSheetWSDLValidationErrorMessage());
				bean.setProductSeasonValidationErrorMessage(bean.getCostSheetWSDLValidationErrorMessage());
				return false;
			}

		}

		return true;
	}
}
