/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.processor;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.helper.SMColorwayWSDLValidator;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.Colorway;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLink;
import com.sportmaster.wc.interfaces.webservices.productbean.Thumbnail;

import wt.util.WTException;
import wt.util.WTProperties;

/**
 * @author BSC
 *
 */
public class SMColorwayBeanDataProcessor {

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMColorwayBeanDataProcessor.class);	
	/**
	 * protected constructor.
	 */
	public SMColorwayBeanDataProcessor(){
		//public construtor.
	}

	/**
	 * set data on colorway bean.
	 * @param sku
	 * @param skuInfoRequest
	 * @throws WTException
	 * @throws DatatypeConfigurationException
	 * @throws IOException 
	 */
	public Colorway setDataOnColorwayBean(LCSSKU col, Colorway skuInfoRequest, SMProductOutboundIntegrationBean bean)
			throws WTException, DatatypeConfigurationException, IOException {

		if(new SMColorwayWSDLValidator().validateColorwayWSDL(col, bean)){
			//server hostname.
			String serverHostName = WTProperties.getLocalProperties().getProperty("wt.rmi.server.hostname");

			LOGGER.info("Setting data on Colorway Bean !!!!");
			LCSSKU sku = col;
			
			//commted below line to fix ptx case 14756226
			LOGGER.debug("SKU ID   >>>>>>>>>>>>>>>>>>>>>>    "+FormatHelper.getNumericObjectIdFromObject(sku));
			//set mdm ID.
			if(FormatHelper.hasContent((String) sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID))){
				skuInfoRequest.setMdmId(sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID).toString());
			}

			LOGGER.info("Setting colorway MDM ID on Schedule queue  *****************"+bean.isUpdateProductSeasonLink());
			if(!FormatHelper.hasContent((String) sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID)) && bean.isUpdateProductSeasonLink()){
				skuInfoRequest.setMdmId(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
			}

			//set object type.
			skuInfoRequest.setObjectType(bean.getProdHelper().modifyObjectType(sku.getFlexType().getFullNameDisplay(true)));

			//phase-8 SEPD changes
			skuInfoRequest.setType1Level(bean.getProdHelper().modifyObjectType(sku.getFlexType().getFullNameDisplay().split("\\\\")[0]));
			skuInfoRequest.setType2Level(bean.getProdHelper().modifyObjectType(sku.getFlexType().getFullNameDisplay().split("\\\\")[1]));

			//set plm ID.
			LCSSKU colorway = (LCSSKU) VersionHelper.getFirstVersion(sku);
			colorway = (LCSSKU) VersionHelper.latestIterationOf(colorway);

			LOGGER.info("Colorway Branch ID value  --------------------->>>     "+colorway.getBranchIdentifier());
			skuInfoRequest.setPlmId(String.valueOf(colorway.getBranchIdentifier()));

			//set lifecycle state.
			skuInfoRequest.setLifeCycleState(sku.getLifeCycleState().toString());
			
			// Phase - 13 | Splitted for Sonar issue | added new
			// setDataOnColorwayBeanObject function to add data to colorway bean
			// .
			setDataOnColorwayBeanObject(sku, skuInfoRequest, bean, serverHostName);

			//get skus.
			LOGGER.info("IS UPDATE PSL (Setting Colorway Season Attributes on Colorway Bean)   >>>>>>>    "+bean.isUpdateProductSeasonLink());
			validateSKUSeaonLink(skuInfoRequest, sku, bean);

			//set created on.
			skuInfoRequest.setCreatedOn(bean.getProdUtill().getXMLGregorianCalendarFormat(sku.getCreateTimestamp()));
			//set created by.
			skuInfoRequest.setCreatedBy(bean.getProdProcessor().getColorwayCreator(sku));
			//set last updated on.
			skuInfoRequest.setLastUpdated(bean.getProdUtill().getXMLGregorianCalendarFormat(sku.getModifyTimestamp()));

			//set last updated by.
			skuInfoRequest.setLastUpdatedBy(bean.getProdProcessor().getColorwayModifier(sku));
			
			// Phase 14 - EMP-481 - Start
			setLastTriggeredDetails(bean, sku, skuInfoRequest);
			// Phase 14 - EMP-481 - End

			return skuInfoRequest;

		}else{
			LCSSKU sku = col;
			sku = (LCSSKU) VersionHelper.getFirstVersion(sku);
			sku = (LCSSKU) VersionHelper.latestIterationOf(sku);
			LCSSKU colorway = (LCSSKU) VersionHelper.getFirstVersion(sku);
			colorway = (LCSSKU) VersionHelper.latestIterationOf(colorway);
			String plmID = String.valueOf(colorway.getBranchIdentifier());
			bean.getLogEntryProcessor().setLogEntryForColorwayOutboundIntegration(plmID, "WSDL_VALIDATION_FAILED", colorway, bean);
			return null;
		}
	}

	// Phase 14 - EMP-481 - Start
	/**
	 * @param bean
	 * @param sku
	 * @param skuInfoRequest
	 * @throws WTException
	 * @throws DatatypeConfigurationException
	 */
	private void setLastTriggeredDetails(SMProductOutboundIntegrationBean bean, LCSSKU sku,
			Colorway skuInfoRequest) throws WTException, DatatypeConfigurationException {
		// TODO Auto-generated method stub
		//Date lastTriggeredOnSKU = (Date)sku.getValue(SMProductOutboundWebServiceConstants.SKU_TRIGGERED_ON);
		Date lastTriggeredOnSKU = new Date();
		//if(lastTriggeredOnSKU!=null){
			skuInfoRequest.setSmLastIntTriggered(bean.getProdUtill().getXMLGregorianCalendarFormat(lastTriggeredOnSKU));
			skuInfoRequest.setSmUserIntTriggered(bean.getProdProcessor().getColorwayModifier(sku));
		//}
	}
	// Phase 14 - EMP-481 - End

	/**
	 * Phase - 13 | added new setDataOnColorwayBeanObject function to add data
	 * to colorway bean .
	 * 
	 * @param sku
	 * @param skuInfoRequest
	 * @param bean
	 * @param serverHostName
	 * @throws WTException
	 */
	private void setDataOnColorwayBeanObject(LCSSKU sku, Colorway skuInfoRequest, SMProductOutboundIntegrationBean bean,
			String serverHostName) throws WTException {

		// added for phasge -8 3.8.1.0 build starts
		if (bean.isCancelRequest()) {
			skuInfoRequest.setLifeCycleState(SMProductOutboundWebServiceConstants.CANCELLED);
		}
		//added for phasge -8 3.8.1.0 build ends
		//updated logic for FPD Changes
		String skuType=sku.getFlexType().getFullName();
		if(skuType.startsWith("APD") || skuType.startsWith("smAccessories")) {
			//set safety standard.
			LCSLifecycleManaged safetyStandard=(LCSLifecycleManaged) sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SAFETY_STANDARD);
			if(null != safetyStandard && FormatHelper.hasContent((String) safetyStandard.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SAFETY_STANDARD_MDM_ID))){
				skuInfoRequest.setSmSafetyStandard(safetyStandard.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SAFETY_STANDARD_MDM_ID).toString());
			}
			//set safety code.
			LCSLifecycleManaged safetyCode = (LCSLifecycleManaged) sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SAFETY_CODE);
			if(null != safetyCode){
				skuInfoRequest.setSmSafetyCode(safetyCode.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SAFETY_CODE_NAME).toString());
			}
		}

		LCSProduct prod = sku.getProduct();
		prod = (LCSProduct) VersionHelper.getFirstVersion(prod);
		prod = (LCSProduct) VersionHelper.latestIterationOf(prod);

		//set product mdm id.
		if(null != prod.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY)){
			skuInfoRequest.setSmMDMProduct(prod.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY).toString());
		}
		else{
			skuInfoRequest.setSmMDMProduct(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
		}

		//set plm id.
		LOGGER.info("Product PLM ID on Colorway Bean >>>>>>>>>>   "+prod.getBranchIdentifier());
		skuInfoRequest.setPlmIdProduct(String.valueOf(prod.getBranchIdentifier()));

		//set sku name. sepd
		skuInfoRequest.setSkuName(sku.getName());

		//set colorway mdm id.
		setColorDataOnSKU(skuInfoRequest, sku);

		//set colorway thumbnail.
		if(FormatHelper.hasContent(sku.getPartPrimaryImageURL())){
			Thumbnail skuThumbnail = new Thumbnail();
			skuThumbnail.setThumbnailURL("https://"+serverHostName+FormatHelper.formatImageUrl(sku.getPartPrimaryImageURL()));
			//set MD5 hash.
			skuThumbnail.setThumbnailHash(DigestUtils.md5Hex(FormatHelper.formatImageUrl(sku.getPartPrimaryImageURL())));

			skuInfoRequest.setThumbnail(skuThumbnail);
		}
	}

	/**
	 * @param skuInfoRequest
	 * @param sku
	 * @throws WTException
	 */
	public void setColorDataOnSKU(Colorway skuInfoRequest, LCSSKU sku)
			throws WTException {
		LCSColor colorwayColor = (LCSColor) sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_COLOR);

		if(null != colorwayColor.getValue(SMProductOutboundWebServiceConstants.COLORWAY_COLOR_MDM_ID)){
			skuInfoRequest.setSmMDMCOL(colorwayColor.getValue(SMProductOutboundWebServiceConstants.COLORWAY_COLOR_MDM_ID).toString());
		}else{
			skuInfoRequest.setSmMDMCOL(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
		}
	}

	/**
	 * @param skuInfoRequest
	 * @param sku
	 * @throws WTException
	 */
	public void validateSKUSeaonLink(Colorway skuInfoRequest, LCSSKU sku, SMProductOutboundIntegrationBean bean)
			throws WTException {
		
		if(FormatHelper.hasContent((String) sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID)) || bean.isUpdateProductSeasonLink()){
			if(bean.isUpdateProductSeasonLink()){
				getColorwaySeasonLinkFromColorway(sku, skuInfoRequest, bean);
			}
		}
	}

	/**
	 * @param sku
	 * @param skuInfoRequest
	 * @throws WTException
	 */
	public void getColorwaySeasonLinkFromColorway(LCSSKU sku, Colorway skuInfoRequest,
			SMProductOutboundIntegrationBean bean) throws WTException {
		
		if(null != bean.getProdProcessor().findSKUSeasonLink(sku) && !bean.getProdProcessor().findSKUSeasonLink(sku).isEmpty()){
			
			List<LCSSKUSeasonLink> skuSeasonLinkList = bean.getProdProcessor().findSKUSeasonLink(sku);
			LCSSKU colorwaySeasonRev;
			LCSSeason season;
			LCSSeason pslSeason;
			ColorwaySeasonLink colorwaySeasonLinkDataBean;

			for(LCSSKUSeasonLink skuSeasonLink : skuSeasonLinkList){
				season = SeasonProductLocator.getSeasonRev(skuSeasonLink);
				String seasonMasterID1 = FormatHelper.getNumericObjectIdFromObject(season.getMaster());
				pslSeason = SeasonProductLocator.getSeasonRev(bean.getProductSeasonLink());
				LOGGER.info("Season in Context >>>>>>>>>  "+bean.getAssociatedSeason()); 	
				String seasonMasterID2 = FormatHelper.getNumericObjectIdFromObject(pslSeason.getMaster());
				//Set data on Colorway Season Link.
				if(seasonMasterID1.equalsIgnoreCase(seasonMasterID2)){
					colorwaySeasonRev = SeasonProductLocator.getSKUSeasonRev(skuSeasonLink);
					colorwaySeasonRev = (LCSSKU) VersionHelper.latestIterationOf(colorwaySeasonRev);
					skuSeasonLink = (LCSSKUSeasonLink) SeasonProductLocator.getSeasonProductLink(colorwaySeasonRev);
					colorwaySeasonLinkDataBean = bean.getProdProcessor()
							.setDataForColorwaySeasonLinkOutboundRequest(skuSeasonLink, new ColorwaySeasonLink(), bean);
					if(null != colorwaySeasonLinkDataBean){
						skuInfoRequest.getColorwaySeasonLink().add(colorwaySeasonLinkDataBean);
					}
				}
			}
		}
	}

}
