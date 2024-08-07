/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.processor;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.ProductDestination;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSCostSheetMaster;
import com.lcs.wc.sourcing.LCSCostSheetQuery;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.helper.SMCostSheetWSDLValidator;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductCostSheet;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductCostSheetRFQ;

/**
 * @author BSC
 *
 */
public class SMCostSheetBeanDataProcessor {

	private static final String APPAREL_ACCESSORIES = "Apparel\\Accessories";
	/**
	 * APPAREL_MCACCESSORIES Type
	 */
	private static final String APPAREL_MCACCESSORIES = "Apparel\\1. Multicurrency Accessories SEPD";

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMCostSheetBeanDataProcessor.class);

	/**
	 * protected constructor.
	 */
	public SMCostSheetBeanDataProcessor(){
		//constructor.
	}

	/**
	 * set data on cost sheet bean.
	 * @param costSheet
	 * @param productCostSheetBean
	 * @throws WTException
	 * @throws DatatypeConfigurationException
	 */
	public ProductCostSheet setDataOnCostsheetBean(LCSProductCostSheet costSheet, ProductCostSheet productCostSheetBean,
			SMProductOutboundIntegrationBean bean) throws WTException, DatatypeConfigurationException {

		if(new SMCostSheetWSDLValidator().validateProductCostSheet(costSheet, bean)){
			LOGGER.info("Set Data on Cost Sheet Bean !!!");
			//set plm id.
			productCostSheetBean.setPlmId(String.valueOf(costSheet.getBranchIdentifier()));

			setDataOnCostSheetColorLink(costSheet, productCostSheetBean);

			//set Season MDM ID.
			productCostSheetBean.setSmMDMSeason(bean.getAssociatedSeason().getValue(SMProductOutboundWebServiceConstants.SEASON_MDM_ID).toString());

			// set quoted price.
			// updated for PHASE -8 SEPD changes
			// updated for Phase 13 - APPAREL_MCACCESSORIES changes
			if (costSheet.getFlexType().getFullNameDisplay().startsWith("Sports Equipment")
					|| APPAREL_ACCESSORIES.equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())
					|| APPAREL_MCACCESSORIES.equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
				if (FormatHelper.hasContent(
						String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.PURCHASE_PRICE_CALC_CONTRACT_CURENCY)))) {
					double quotedPriceGSSupplier = (Double) costSheet
							.getValue(SMProductOutboundWebServiceConstants.PURCHASE_PRICE_CALC_CONTRACT_CURENCY);
					productCostSheetBean.setSmQuotedPriceGSCurr(BigDecimal.valueOf(quotedPriceGSSupplier));
				}
			} else {
				if (FormatHelper.hasContent(String
						.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_QUOTED_PRICE_GS_SUPPLIER_CURRENCY)))) {
					double quotedPriceGSSupplier = (Double) costSheet
							.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_QUOTED_PRICE_GS_SUPPLIER_CURRENCY);
					productCostSheetBean.setSmQuotedPriceGSCurr(BigDecimal.valueOf(quotedPriceGSSupplier));
				}
			}
			
			// Phase 13 | Splitted for Sonar fix | added
			// setAdditionalDataOnCostsheetBean function to set data on cost
			// sheet bean.
			setAdditionalDataOnCostsheetBean(costSheet, productCostSheetBean);

			//is primary.
			productCostSheetBean.setPrimaryCostSheet(String.valueOf(costSheet.isPrimaryCostSheet()));

			//set created on.
			productCostSheetBean.setCreatedOn(bean.getProdUtill().getXMLGregorianCalendarFormat(costSheet.getCreateTimestamp()));

			//set created by.
			productCostSheetBean.setCreatedBy(bean.getProdProcessor().getCostSheetCreator(costSheet));

			//set last updated on
			productCostSheetBean.setLastUpdated(bean.getProdUtill().getXMLGregorianCalendarFormat(costSheet.getModifyTimestamp()));

			//set last updated by.
			productCostSheetBean.setLastUpdatedBy(bean.getProdProcessor().getCostSheetModifier(costSheet));

			return productCostSheetBean;

		}else{
			LCSProduct prod = VersionHelper.latestIterationOf(costSheet.getProductMaster());
			prod = (LCSProduct) VersionHelper.getFirstVersion(prod);
			prod = (LCSProduct) VersionHelper.latestIterationOf(prod);
			LOGGER.info("Cost Sheet Season  -------------->   "+costSheet.getSeasonMaster());
			if(null != costSheet.getSeasonMaster()){
				LCSSeason season = VersionHelper.latestIterationOf(costSheet.getSeasonMaster());

				LCSProductSeasonLink psl = (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(prod, season);

				LCSProduct productSeasonRev = SeasonProductLocator.getProductSeasonRev(psl);
				//Get latest iteration.
				productSeasonRev = (LCSProduct) VersionHelper.latestIterationOf(productSeasonRev);
				//get Season-product Link.
				LCSProductSeasonLink latestSPL = (LCSProductSeasonLink) SeasonProductLocator.getSeasonProductLink(productSeasonRev);
				String plmID = bean.getProdHelper().getProductMasterReferencefromLink(latestSPL);
				bean.getLogEntryProcessor().setLogEntryForProductSeasonOutboundIntegration(plmID,"WSDL_VALIDATION_FAILED", latestSPL, bean);
				return null;
			}else {
				return null;
			}
		}
	}

	/**
	 * Phase 13 | added setAdditionalDataOnCostsheetBean function to set data on
	 * cost sheet bean.
	 * 
	 * @param costSheet
	 * @param productCostSheetBean
	 * @param productType
	 * @throws WTException
	 */
	private void setAdditionalDataOnCostsheetBean(LCSProductCostSheet costSheet, ProductCostSheet productCostSheetBean) throws WTException {
		
		LCSProduct prdt=(LCSProduct)VersionHelper.latestIterationOf(costSheet.getProductMaster());
		//validates FPD Type costsheet for Phase4 fpd cr
		String productType=prdt.getFlexType().getFullName();
		LOGGER.info("productType >>>>>>>>>>>>." +productType);
		//set manual GS currency.
		if ((productType.startsWith("APD") || productType.startsWith("smAccessories")) && FormatHelper.hasContent(String.valueOf(costSheet
				.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_FOB_TOTAL_INT_MANUAL_GS_CURRENCY)))) {
			double fobTotalIntManualGSCurrency = (Double) costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_FOB_TOTAL_INT_MANUAL_GS_CURRENCY);
			productCostSheetBean.setSmFOBTotalIntGSCurr(BigDecimal.valueOf(fobTotalIntManualGSCurrency));
		}

		// Phase -8 sepd chnages
		productCostSheetBean.setName(costSheet.getName());
		if ((productType.startsWith("SEPD") || APPAREL_ACCESSORIES.equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay()))
				&& FormatHelper.hasContent(String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.TYPE_OF_ASSEMBLING)))) {

			productCostSheetBean.setSmCostSheetTypeOfAssembling(
					String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.TYPE_OF_ASSEMBLING)));
		}

		// set Contract currency.
		productCostSheetBean
				.setSmCsContractCurrency(costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_CONTRACT_CURRENCY).toString());

		// set incoterms.
		productCostSheetBean.setSmCsIncoterms(costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_INCOTERMS).toString());

		setCostSheetDestinationDataOnBean(costSheet, productCostSheetBean);

		// set costing stage.
		// updated for phase 13 - App/MC costsheet
		// updated for PHASE -8 SEPD CHNaGES
		/*
		if (costSheet.getFlexType().getFullNameDisplay().startsWith("Sports Equipment")
				|| APPAREL_ACCESSORIES.equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			productCostSheetBean.setSmCostingStage(costSheet.getValue(SMProductOutboundWebServiceConstants.SEPD_COSTING_STAGE).toString());

			// updated for Phase 12 - 3.12.0(FPD/ multi currency change) build -
			// start
		} else if ("Footwear\\Multicurrency".equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			productCostSheetBean.setSmCostingStage(costSheet.getValue(SMProductOutboundWebServiceConstants.MULTI_COSTING_STAGE).toString());

		}
		// updated for Phase 12 - 3.12.0(FPD/ multi currency change) build - end
		else {
			productCostSheetBean
					.setSmCostingStage(costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_COSTING_STAGE).toString());
		}
		*/
		
		String costingStageKey = (new SMCostSheetWSDLValidator()).costingStageValidation(costSheet);
		productCostSheetBean
					.setSmCostingStage(costSheet.getValue(costingStageKey).toString());
		// updated for phase 13 - end
		
		// updated for 3.9.1, 3.12.0(multi currency change) build
		String status = String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_STATUS));
		if (APPAREL_ACCESSORIES.equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			status = String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.ACC_COST_SHEET_STATUS));
		}
		// updated for Phase 12 - 3.12.0(FPD/ multi currency change) build -
		// start
		if ("Footwear\\Multicurrency".equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			status = String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.MULTI_COST_SHEET_STATUS));
		}
		//-- //Phase 14 - APD MultiCurrency: START //
		if ("Apparel\\0. Multicurrency Apparel".equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			status = String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.CS_MCAPP_COSTING_STATUS_KEY));
		}//-- //Phase 14 - APD MultiCurrency: END //
		
		// updated for Phase 12 - 3.12.0(FPD/ multi currency change) build - end
		if (FormatHelper.hasContent(status)) {
			productCostSheetBean.setVrdCSStatus(status);
		}
	}

	/**
	 * @param costSheet
	 * @param productCostSheetBean
	 * @throws WTException
	 */
	public void setCostSheetDestinationDataOnBean(
			LCSProductCostSheet costSheet, ProductCostSheet productCostSheetBean)
					throws WTException {
		//cost sheet destination link.
		Collection<?> destinationLink = LCSCostSheetQuery.getDestinationLinks((LCSCostSheetMaster) costSheet.getMaster());
		FlexObject fo = null;
		ProductDestination costSheetDestination;

		if(null != destinationLink && !destinationLink.isEmpty()){
			for(Object obj : destinationLink){
				fo = (FlexObject) obj;
				costSheetDestination = (ProductDestination) LCSQuery.findObjectById("com.lcs.wc.product.ProductDestination:"+FormatHelper.parseInt(fo.getString("PRODUCTDESTINATION.IDA2A2")));
				productCostSheetBean.getDestinations().add(costSheetDestination.getName());
			}
		}else{
			productCostSheetBean.getDestinations().add("empty");
		}
	}
	
	/**
	 * @param costSheet
	 * @param rfqCostSheetBean
	 * @throws WTException
	 */
	public void setRFQCostSheetDestinationDataOnBean(
			LCSProductCostSheet costSheet, ProductCostSheetRFQ rfqCostSheetBean)
					throws WTException {
		//cost sheet destination link.
		Collection<?> destinationLink = LCSCostSheetQuery.getDestinationLinks((LCSCostSheetMaster) costSheet.getMaster());
		FlexObject fo = null;
		ProductDestination costSheetDestination;

		if(null != destinationLink && !destinationLink.isEmpty()){
			for(Object obj : destinationLink){
				fo = (FlexObject) obj;
				costSheetDestination = (ProductDestination) LCSQuery.findObjectById("com.lcs.wc.product.ProductDestination:"+FormatHelper.parseInt(fo.getString("PRODUCTDESTINATION.IDA2A2")));
				rfqCostSheetBean.getDestinations().add(costSheetDestination.getName());
			}
		}else{
			rfqCostSheetBean.getDestinations().add("empty");
		}
	}

	/**
	 * @param costSheet
	 * @param productCostSheetBean
	 * @throws WTException
	 */
	public void setDataOnCostSheetColorLink(
			LCSProductCostSheet costSheet, ProductCostSheet productCostSheetBean)
					throws WTException {
		List<?> costSheetToColorLinkList = (List<?>) LCSCostSheetQuery.getColorLinks((LCSCostSheetMaster) costSheet.getMaster());
		LCSSKU skuObjInCostSheet;
		String colorwayMDMID;
		//get color links in Cost sheet.
		if(null != costSheetToColorLinkList && !costSheetToColorLinkList.isEmpty()){
			for(Object colorLink : costSheetToColorLinkList){
				FlexObject fo = (FlexObject) colorLink;
				skuObjInCostSheet = (LCSSKU) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:"+fo.getString("LCSSKU.BRANCHIDITERATIONINFO"));
				skuObjInCostSheet = (LCSSKU) VersionHelper.getFirstVersion(skuObjInCostSheet);
				skuObjInCostSheet = (LCSSKU) VersionHelper.latestIterationOf(skuObjInCostSheet);
				colorwayMDMID = (String) skuObjInCostSheet.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID);
				if(FormatHelper.hasContent(colorwayMDMID)){
					productCostSheetBean.getSmMDMColorway().add(colorwayMDMID);
				}else{
					productCostSheetBean.getSmMDMColorway().add(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
				}
			}
		}
	}
	
	/**
	 * @param costSheet
	 * @param rfqCostSheetBean
	 * @throws WTException
	 */
	public void setDataOnRFQCostSheetColorLink(
			LCSProductCostSheet costSheet, ProductCostSheetRFQ rfqCostSheetBean)
					throws WTException {
		List<?> rfqCostSheetToColorLinkList = (List<?>) LCSCostSheetQuery.getColorLinks((LCSCostSheetMaster) costSheet.getMaster());
		LCSSKU skuObjInRFQCostSheet;
		String colorwayMDMID;
		//get color links in Cost sheet.
		if(null != rfqCostSheetToColorLinkList && !rfqCostSheetToColorLinkList.isEmpty()){
			for(Object colorLink : rfqCostSheetToColorLinkList){
				FlexObject fo = (FlexObject) colorLink;
				skuObjInRFQCostSheet = (LCSSKU) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:"+fo.getString("LCSSKU.BRANCHIDITERATIONINFO"));
				skuObjInRFQCostSheet = (LCSSKU) VersionHelper.getFirstVersion(skuObjInRFQCostSheet);
				skuObjInRFQCostSheet = (LCSSKU) VersionHelper.latestIterationOf(skuObjInRFQCostSheet);
				colorwayMDMID = (String) skuObjInRFQCostSheet.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID);
				if(FormatHelper.hasContent(colorwayMDMID)){
					rfqCostSheetBean.getSmMDMColorway().add(colorwayMDMID);
				}else{
					rfqCostSheetBean.getSmMDMColorway().add(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
				}
			}
		}
	}
}
