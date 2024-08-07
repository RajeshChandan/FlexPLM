/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.LCSSKUQuery;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.sourcing.LCSCostSheetQuery;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.sourcing.LCSSKUSourcingLink;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.helper.SMSourcingConfigWSDLValidator;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductCostSheet;
import com.sportmaster.wc.interfaces.webservices.productbean.SourcingConfig;

/**
 * @author BSC
 *
 */
public class SMSourcingConfigurationBeanDataProcessor {

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSourcingConfigurationBeanDataProcessor.class);

	/**
	 * protected constructor.
	 */
	public SMSourcingConfigurationBeanDataProcessor(){
		//constructor.
	}

	/**
	 * Set data on sourcing config bean.
	 * @param src
	 * @param sourcingConfigBean
	 * @param psl
	 * @throws WTException
	 * @throws DatatypeConfigurationException
	 */
	public SourcingConfig setDataOnSourceConfigurationBean(LCSSourcingConfig src,
			SourcingConfig sourcingConfigBean, LCSProductSeasonLink psl,
			SMProductOutboundIntegrationBean bean) throws WTException, DatatypeConfigurationException {

		if(new SMSourcingConfigWSDLValidator().validateSourcingConfigWSDL(src, psl, bean)){
			LOGGER.info("Set Data on Sourcing Configuration Bean !!!!");
			LOGGER.info("Src Config name in Src config bean  >>>>>>>   "+src.getName());

			//get source to season link.
			LCSSourceToSeasonLink sourceToSeasonLink = new LCSSourcingConfigQuery().getSourceToSeasonLink(src, bean.getAssociatedSeason());

			//set plm id.
			sourcingConfigBean.setPlmId(bean.getProdHelper().getSourcePLMID(src, bean.getAssociatedSeason()));

			//set order destination.
			if(FormatHelper.hasContent((String) sourceToSeasonLink.getValue(SMProductOutboundWebServiceConstants.SOURCING_CONFIGURATION_DESTINATION))){
				String srcOrderDestination = sourceToSeasonLink.getValue(SMProductOutboundWebServiceConstants.SOURCING_CONFIGURATION_DESTINATION).toString();
				List<String> orderDestinationSourcing = FormatHelper.commaSeparatedListToList(srcOrderDestination.replaceAll("[|~*~|]", ","));

				//set order destination.
				for(String destination : orderDestinationSourcing){
					LOGGER.info("Order destination for Sourcing  :::::  "+src.getName()+"   is  ********  "+destination);
					sourcingConfigBean.getSmSCDestination().add(destination);
				}
			}
			// phase 13 - sonar fix : splitted for complexity
			setBeanDataforSourcing(psl, sourcingConfigBean, bean, src, sourceToSeasonLink);
			return sourcingConfigBean;

		}else{
			String plmID = FormatHelper.getNumericObjectIdFromObject(psl);
			bean.getLogEntryProcessor().setLogEntryForProductSeasonOutboundIntegration(plmID, "WSDL_VALIDATION_FAILED", psl, bean);
			return null;
		}
	}

	/**
	 * // phase 13 - function to set datat on sourcing config bean.
	 * 
	 * @param psl
	 * @param sourcingConfigBean
	 * @param bean
	 * @param src
	 * @param sourceToSeasonLink
	 * @throws WTException
	 * @throws DatatypeConfigurationException
	 */
	private void setBeanDataforSourcing(LCSProductSeasonLink psl, SourcingConfig sourcingConfigBean, SMProductOutboundIntegrationBean bean,
			LCSSourcingConfig src, LCSSourceToSeasonLink sourceToSeasonLink) throws WTException, DatatypeConfigurationException {

		// set MDM ID.
		if (FormatHelper.hasContent((String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID))) {
			sourcingConfigBean
					.setSmMDMProductSeasonLink(psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID).toString());
		} else {
			sourcingConfigBean.setSmMDMProductSeasonLink(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
		}

		// set MDM Season.
		if (FormatHelper
				.hasContent(String.valueOf(bean.getAssociatedSeason().getValue(SMProductOutboundWebServiceConstants.SEASON_MDM_ID)))) {
			sourcingConfigBean.setSmMDMSeason(
					String.valueOf(bean.getAssociatedSeason().getValue(SMProductOutboundWebServiceConstants.SEASON_MDM_ID)));
		} else {
			sourcingConfigBean.setSmMDMSeason(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
		}

		// get business supplier.
		LCSSupplier businessSupplier = (LCSSupplier) src
				.getValue(SMProductOutboundWebServiceConstants.SOURCING_CONFIGURATION_BUSINESS_SUPPLIER);

		setVendorMDMIDOnSourcingObject(sourcingConfigBean, businessSupplier);

		// set sourcing status.
		sourcingConfigBean.setVrdSourcingStatus(
				sourceToSeasonLink.getValue(SMProductOutboundWebServiceConstants.SOURCING_CONFIGURATION_STATUS).toString());

		// set primary season.
		sourcingConfigBean.setSmPrimarySourceForSeason(sourceToSeasonLink.isPrimarySTSL());

		// get sku sourcing links.
		setDataOnSKUSourcingObject(src, sourcingConfigBean, bean);

		LCSSupplier factory = (LCSSupplier) sourceToSeasonLink
				.getValue(SMProductOutboundWebServiceConstants.SOURCING_CONFIGURATION_FACTORY);

		// set MDM factory.
		if (null != factory) {
			String factoryMDMID = String
					.valueOf(factory.getValue(SMProductOutboundWebServiceConstants.SOURCING_CONFIGURATION_FACTORY_MDM_ID));
			LOGGER.info("Factory MDM ID >>>  " + factoryMDMID);
			if (FormatHelper.hasContent(factoryMDMID)) {
				sourcingConfigBean.setSmMDMFactory(factoryMDMID);
			} else {
				sourcingConfigBean.setSmMDMFactory(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
			}
		}

		setDataOnAssociatedSourcingObjects(src, sourcingConfigBean, psl, bean);

		// set created on.
		sourcingConfigBean.setCreatedOn(bean.getProdUtill().getXMLGregorianCalendarFormat(src.getCreateTimestamp()));

		// set created by.
		sourcingConfigBean.setCreatedBy(bean.getProdProcessor().getSourcingConfigCreator(src));

		// set last updated
		sourcingConfigBean.setLastUpdated(bean.getProdUtill().getXMLGregorianCalendarFormat(src.getModifyTimestamp()));

		// set last updated by.
		sourcingConfigBean.setLastUpdatedBy(bean.getProdProcessor().getSourcingConfigModifier(src));

		List<?> srcOrderDest = sourcingConfigBean.getSmSCDestination();
		for (Object orderDest : srcOrderDest) {
			LOGGER.info(orderDest);
		}
	}

	/**
	 * @param sourcingConfigBean
	 * @param businessSupplier
	 * @throws WTException
	 */
	public void setVendorMDMIDOnSourcingObject(
			SourcingConfig sourcingConfigBean, LCSSupplier businessSupplier)
					throws WTException {
		//set business supplier mdm id.
		if(null != businessSupplier){
			if(FormatHelper.hasContent((String) businessSupplier.getValue(SMProductOutboundWebServiceConstants.SOURCING_CONFIGURATION_BUSINESS_SUPPLIER_MDM_ID))){
				sourcingConfigBean.setSmMDMVendor(businessSupplier.getValue(SMProductOutboundWebServiceConstants.SOURCING_CONFIGURATION_BUSINESS_SUPPLIER_MDM_ID).toString());
			}else{
				sourcingConfigBean.setSmMDMVendor(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
			}
		}else{
			sourcingConfigBean.setSmMDMVendor(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
		}
	}

	/**
	 * @param src
	 * @param sourcingConfigBean
	 * @param psl
	 * @throws WTException
	 */
	public void setDataOnAssociatedSourcingObjects(LCSSourcingConfig src, SourcingConfig sourcingConfigBean,
			LCSProductSeasonLink psl, SMProductOutboundIntegrationBean bean) throws WTException {
		
		LCSProduct productSeason = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+(int)psl.getProductSeasonRevId());

		List<?> productSeasonSKUList = (List<?>) LCSSKUQuery.findSKUs(productSeason);

		//set all SKUs MDM ID.
		setActiveColorwaysOnSourcingConfiguration(src, sourcingConfigBean,
				productSeasonSKUList);

		LOGGER.info("Season *********************    "+bean.getAssociatedSeason());
		Collection<?> costSheetList = LCSCostSheetQuery.getCostSheetsForProduct(new HashMap<Object, Object>(), productSeason, src,
				bean.getAssociatedSeason(), new ArrayList<Object>(), false, false);

		LCSProductCostSheet productCostSheet;

		if(null != costSheetList && !costSheetList.isEmpty()){
			LOGGER.info("Cost Sheet List Size   >>>>>>>>>>     "+costSheetList.size());
			ProductCostSheet productCostSheetBeanData;
			for(Object cs : costSheetList){
				if(!"class com.lcs.wc.sourcing.LCSSKUCostSheet".equalsIgnoreCase(cs.getClass().toString())){
					FlexObject fo = (FlexObject) cs;
					productCostSheet = (LCSProductCostSheet) LCSQuery.findObjectById("VR:com.lcs.wc.sourcing.LCSProductCostSheet:"+fo.getString("LCSCOSTSHEET.BRANCHIDITERATIONINFO"));
					LOGGER.info("Cost Sheet ----------------->>   "+productCostSheet.getName());
					productCostSheetBeanData = bean.getProdProcessor().setDataOnProductCostSheet(productCostSheet,
							new ProductCostSheet(), bean);
					//set product cost sheet.
					if(null != productCostSheetBeanData){
						sourcingConfigBean.getProductCostSheet().add(productCostSheetBeanData);
					}
				}
			}
		}
	}

	/**
	 * @param src
	 * @param sourcingConfigBean
	 * @param productSeasonSKUList
	 * @throws WTException
	 */
	public void setActiveColorwaysOnSourcingConfiguration(
			LCSSourcingConfig src, SourcingConfig sourcingConfigBean,
			List<?> productSeasonSKUList) throws WTException {
		LCSSKU colorway;
		String colorwayMDMID;
		if(null != productSeasonSKUList && !productSeasonSKUList.isEmpty()){
			LOGGER.info("Src Config name  >>>>   "+src.getName());
			for(Object obj : productSeasonSKUList){
				colorway = (LCSSKU) obj;
				colorway = (LCSSKU) VersionHelper.getFirstVersion(colorway);
				colorway = (LCSSKU) VersionHelper.latestIterationOf(colorway);
				LOGGER.info("SKU ID ^^^^^^^**************    "+FormatHelper.getNumericObjectIdFromObject(colorway));
				LOGGER.info("Colorway Sourcing Object >>>>>   "+colorway+"   :::::   "+colorway.getName());
				colorwayMDMID = (String) colorway.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID);
				if(FormatHelper.hasContent(colorwayMDMID) && !sourcingConfigBean.getSmMDMColorway().contains(colorwayMDMID)){
					sourcingConfigBean.getSmMDMColorway().add(colorwayMDMID);
				}else{
					sourcingConfigBean.getSmMDMColorway().add(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
				}
			}
		}
	}

	/**
	 * @param src
	 * @param sourcingConfigBean
	 * @throws WTException
	 */
	public void setDataOnSKUSourcingObject(LCSSourcingConfig src,
			SourcingConfig sourcingConfigBean, SMProductOutboundIntegrationBean bean) throws WTException {
		SearchResults res = new LCSSourcingConfigQuery().getSKUSourcingLinkDataForConfig(src, bean.getAssociatedSeason(), true);

		Collection<?> skuSourcingLinks = res.getResults();

		LCSSKU sourcingColorway = null;
		String sourcingColorwayMDMID;

		//set colorway sourcing boolean.
		if(null != skuSourcingLinks && !skuSourcingLinks.isEmpty()){
			LOGGER.info("SKU Sourcing Link collection Size   >>>>   "+skuSourcingLinks.size());
			for(Object obj: skuSourcingLinks){
				FlexObject fObj = (FlexObject) obj;
				LCSSKUSourcingLink skuSourcingLink =(LCSSKUSourcingLink)LCSQuery.findObjectById("com.lcs.wc.sourcing.LCSSKUSourcingLink:"+fObj.getString("LCSSKUSOURCINGLINK.IDA2A2"));
				sourcingColorway = VersionHelper.latestIterationOf(skuSourcingLink.getSkuMaster());
				sourcingColorway = (LCSSKU) VersionHelper.getFirstVersion(sourcingColorway);
				sourcingColorway = (LCSSKU) VersionHelper.latestIterationOf(sourcingColorway);
				LOGGER.info("SKU Sourcing Object >>>  "+sourcingColorway+"   :::::::::    "+sourcingColorway.getName());
				sourcingColorwayMDMID = (String) sourcingColorway.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID);
				if(FormatHelper.hasContent(sourcingColorwayMDMID) && !sourcingConfigBean.getColorwaySourcingBoolean().contains(sourcingColorwayMDMID)){
					sourcingConfigBean.getColorwaySourcingBoolean().add(sourcingColorwayMDMID);
				}else{
					sourcingConfigBean.getColorwaySourcingBoolean().add(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
				}
			}
		}
	}

}
