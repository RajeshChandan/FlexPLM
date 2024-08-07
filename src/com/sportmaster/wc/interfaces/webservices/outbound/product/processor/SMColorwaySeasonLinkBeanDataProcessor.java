/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.processor;

import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.log4j.Logger;

import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.helper.SMColorwaySeasonValidator;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLink;

import wt.util.WTException;

/**
 * @author BSC
 *
 */
public class SMColorwaySeasonLinkBeanDataProcessor {

	/**
	 * MOA_DELIM_REGEX.
	 */
	private static final String MOA_DELIM_REGEX = "[|~*~|]";
	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMColorwaySeasonLinkBeanDataProcessor.class);
	/**
	 * protected constructor.
	 */
	protected SMColorwaySeasonLinkBeanDataProcessor(){
		//protected construtor.
	}

	/**
	 * Set data on SKU Season Bean.
	 * @param ssl
	 * @param skuSeasonInfoRequest
	 * @throws WTException
	 * @throws DatatypeConfigurationException
	 */
	public ColorwaySeasonLink setDataOnColorwaySeasonBean(LCSSKUSeasonLink ssl,
			ColorwaySeasonLink skuSeasonInfoRequest, SMProductOutboundIntegrationBean bean)
					throws WTException, DatatypeConfigurationException {

		if( !new SMColorwaySeasonValidator().validateColorwaySeasonWSDL(ssl, bean)) {
			String plmID = bean.getProdHelper().getColorwayMasterReferenceFromLink(ssl);
			bean.getLogEntryProcessor().setLogEntryForColorwaySeasonOutboundIntegration(plmID, "WSDL_VALIDATION_FAILED", ssl, bean);
			return null;
		}
		LOGGER.info("Set data on sku season bean  !!!!");

		LCSSKU skuARev = SeasonProductLocator.getSKUARev(ssl);
		skuARev = (LCSSKU) VersionHelper.latestIterationOf(skuARev);


		if(FormatHelper.hasContent((String) ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_MDMID))){
			skuSeasonInfoRequest.setMdmId(ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_MDMID).toString());
		}

		//new
		LOGGER.info("Setting colorway Season MDM ID on Schedule queue  *****************"
				+ bean.isUpdateProductSeasonLink());
		if (!FormatHelper
				.hasContent((String) ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_MDM_ID))
				&& bean.isUpdateProductSeasonLink()) {
			skuSeasonInfoRequest.setMdmId(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
		}


		LCSSKU skuSeasonRev = (LCSSKU) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:"+(int)ssl.getSkuSeasonRevId());

		//set object type.
		skuSeasonInfoRequest.setObjectType(bean.getProdHelper().modifyObjectType(skuSeasonRev.getFlexType().getFullNameDisplay()));
		//phase - SEPD changes
		skuSeasonInfoRequest.setType1Level(bean.getProdHelper().modifyObjectType(skuSeasonRev.getFlexType().getFullNameDisplay().split("\\\\")[0]));
		skuSeasonInfoRequest.setType2Level(bean.getProdHelper().modifyObjectType(skuSeasonRev.getFlexType().getFullNameDisplay().split("\\\\")[1]));

		//set plm id.
		if(FormatHelper.hasContent(String.valueOf(ssl.getValue(SMProductOutboundWebServiceConstants.AD_HOC_PLM_ID_COLORWAY_SEASON_LINK)))){
			LOGGER.debug("Setting SKU Season Link PLM ID from AD HOC PLM ID field *********************");
			skuSeasonInfoRequest.setPlmId(String.valueOf(ssl.getValue(SMProductOutboundWebServiceConstants.AD_HOC_PLM_ID_COLORWAY_SEASON_LINK)));
		}else{
			LOGGER.debug("Setting SKU Season Link from SKU Season Link  ******************");
			skuSeasonInfoRequest.setPlmId(bean.getProdHelper().getColorwayMasterReferenceFromLink(ssl));
		}	

		//set lifecycle state.
		skuSeasonInfoRequest.setLifeCycleState(skuSeasonRev.getLifeCycleState().toString());

		//added for phasge -8 3.8.1.0 build starts
		if(bean.isCancelRequest()) {
			skuSeasonInfoRequest.setLifeCycleState(SMProductOutboundWebServiceConstants.CANCELLED);
		}
		//added for phasge -8 3.8.1.0 build ends
		//setting retail destination SKU Season.
		settingRetailDestinationOnSKUSeasonLink(ssl, skuSeasonInfoRequest,
				skuSeasonRev);

		//set colorway mdm id.
		if(null != skuARev.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID)){
			skuSeasonInfoRequest.setSmMDMColorway(skuARev.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID).toString());
		}else{
			skuSeasonInfoRequest.setSmMDMColorway(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
		}

		//set plm id.
		LCSSKU skuFirstVersion = (LCSSKU) VersionHelper.latestIterationOf(skuARev);
		skuFirstVersion = (LCSSKU) VersionHelper.latestIterationOf(skuFirstVersion);
		LOGGER.debug("Colorway PLM ID on Colorway Season Bean   >>>>>>>>>>   "+skuFirstVersion.getBranchIdentifier());
		skuSeasonInfoRequest.setPlmIdColorway(String.valueOf(skuFirstVersion.getBranchIdentifier()));

		LCSSeason seasonSku = VersionHelper.latestIterationOf(ssl.getSeasonMaster());

		//set season MDM ID.
		skuSeasonInfoRequest.setSmMDMSeason(seasonSku.getValue(SMProductOutboundWebServiceConstants.SEASON_MDM_ID).toString());

		//set colorway season link comments.
		if(FormatHelper.hasContent((String) ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_COMMENTS))){
			skuSeasonInfoRequest.setSmCommentsColorway(ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_COMMENTS).toString());
		}

		//set Flow.
		if(FormatHelper.hasContent((String) ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_FLOW))){
			skuSeasonInfoRequest.setSmFlow(ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_FLOW).toString());
		}

		//set SKU Season Status.
		skuSeasonInfoRequest.setSmColorwaySeasonStatus(ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_COLORWAY_SEASON_STATUS).toString());


		//set Manual HP
		skuSeasonInfoRequest.setSmManualHighPriority(FormatHelper.parseBoolean(String.valueOf(ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_MANUAL_HIGH_PRIORITY))));

		//set LLT.
		skuSeasonInfoRequest.setSmLongLeadTimeCW(FormatHelper.parseBoolean(String.valueOf(ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_LONG_LEAD_TIME))));

		//updated logic for Phase-4 Phase-8 FPD SEPD changes
		String skuSeasonType=ssl.getFlexType().getFullName();
		if(skuSeasonType.startsWith("FPD") || skuSeasonType.startsWith("SEPD")) {
			if(FormatHelper.hasContent(String.valueOf(ssl.getValue(SMProductOutboundWebServiceConstants.INTIAL_FORECAST_CW)))){
				skuSeasonInfoRequest.setSmInitialForecastCW(FormatHelper.parseInt(String.valueOf(ssl.getValue(SMProductOutboundWebServiceConstants.INTIAL_FORECAST_CW))));
			}
		}

		// Phase -13 | splitted for sonar issue | added
		// setAdditionalDataOnColorwaySeasonBean function to set data on
		// ColorwaySeasonLink ean.
		setAdditionalDataOnColorwaySeasonBean(ssl, skuSeasonInfoRequest, bean, skuSeasonType, skuSeasonRev);
		
		// Phase 14 - EMP-481 - Start
		setLastTriggeredDetails(bean, ssl, skuSeasonInfoRequest);
		// Phase 14 - EMP-481 - End
		
		return skuSeasonInfoRequest;
	}

	// Phase 14 - EMP-481 - Start
	/**
	 * @param bean 
	 * @param psl
	 * @param prodSeasonLinkInfoRequest
	 * @throws WTException 
	 * @throws DatatypeConfigurationException 
	 */
	private void setLastTriggeredDetails(SMProductOutboundIntegrationBean bean, LCSSKUSeasonLink ssl,
			ColorwaySeasonLink skuSeasonInfoRequest) throws WTException, DatatypeConfigurationException {
		// TODO Auto-generated method stub
		//Date lastTriggeredOnSKUS = (Date)ssl.getValue(SMProductOutboundWebServiceConstants.SKUSEASON_TRIGGERED_ON);
		Date lastTriggeredOnSKUS = new Date();
		//if(lastTriggeredOnSKUS!=null){
			skuSeasonInfoRequest.setSmLastIntTriggered(bean.getProdUtill().getXMLGregorianCalendarFormat(lastTriggeredOnSKUS));
			String userEmail = ssl.getCreator().getEMail();
			if(!FormatHelper.hasContent(userEmail)){
				userEmail = ssl.getCreator().getFullName();
			}
			skuSeasonInfoRequest.setSmUserIntTriggered(userEmail);
		//}
	}
	// Phase 14 - EMP-481 - End

	/**
	 * Phase -13 | added setAdditionalDataOnColorwaySeasonBean function to set
	 * data on ColorwaySeasonLink ean.
	 * 
	 * @param ssl
	 * @param skuSeasonInfoRequest
	 * @param bean
	 * @param skuSeasonType
	 * @param skuSeasonRev
	 * @throws WTException
	 * @throws DatatypeConfigurationException
	 */
	private void setAdditionalDataOnColorwaySeasonBean(LCSSKUSeasonLink ssl, ColorwaySeasonLink skuSeasonInfoRequest,
			SMProductOutboundIntegrationBean bean, String skuSeasonType, LCSSKU skuSeasonRev)
			throws WTException, DatatypeConfigurationException {

		// updated logic for Phase-8, 3.9.2.0 build SEPD changes
		LOGGER.debug("skuSeasonType>>>>>>>>>>>>>" + skuSeasonType);
		// Added Development newness attr to FPD, SPED, ACC\SEPD NODE
		// smFPDDevelopmentNewness
		String devNewness = null;
		if (skuSeasonType.startsWith("FPD")) {
			devNewness = String.valueOf(ssl.getValue(SMProductOutboundWebServiceConstants.FPD_DEVELOPMENT_NEWNESS));
		} else if (skuSeasonType.startsWith("smAccessories\\smSEPDAccessories")) {
			devNewness = String.valueOf(ssl.getValue(SMProductOutboundWebServiceConstants.ACC_DEVELOPMENT_NEWNESS));
		} else if (skuSeasonType.startsWith("SEPD")) {
			devNewness = String.valueOf(ssl.getValue(SMProductOutboundWebServiceConstants.SEPD_DEVELOPMENT_NEWNESS));
		}
		if (FormatHelper.hasContent(devNewness)) {
			// skuSeasonInfoRequest.setSmSEPDDevelopmentNewness(String.valueOf(ssl.getValue(SMProductOutboundWebServiceConstants.SEPD_DEVELOPMENT_NEWNESS)));
			skuSeasonInfoRequest.setSmSEPDDevelopmentNewness(devNewness);
		}

		// updated for 3.8.2.0 Build
		// setting planned assembly
		if (skuSeasonType.startsWith("SEPD") && FormatHelper
				.hasContent(ssl.getFlexType().getAttribute(SMProductOutboundWebServiceConstants.PLANNED_ASSESMBLY_TYPE).getAttGroup())) {

			settingProductPlannedAssemblyTypeOnSKUSeasonLink(ssl, skuSeasonInfoRequest, skuSeasonRev);
		}

		// updated logic for Phase-8 (3.8.2.0 build) hot fix, 3.9.2.0 build
		// changes

		if (skuSeasonType.startsWith("APD")) {
			skuSeasonInfoRequest.setMktgImageStyle(
					FormatHelper.parseBoolean(String.valueOf(ssl.getValue(SMProductOutboundWebServiceConstants.MKTG_IMAGE_STYLE))));
		} else if (skuSeasonType.startsWith("FPD") || skuSeasonType.startsWith("SEPD") || skuSeasonType.startsWith("smAccessories")) {
			skuSeasonInfoRequest.setMktgImageStyle(FormatHelper.parseBoolean("false"));
		}

		// set created on.
		skuSeasonInfoRequest.setCreatedOn(bean.getProdUtill().getXMLGregorianCalendarFormat(skuSeasonRev.getCreateTimestamp()));

		// set created by.
		skuSeasonInfoRequest.setCreatedBy(bean.getProdProcessor().getColorwayCreator(skuSeasonRev));

		// set updated on.
		skuSeasonInfoRequest.setLastUpdated(bean.getProdUtill().getXMLGregorianCalendarFormat(skuSeasonRev.getModifyTimestamp()));

		// set updated by.
		skuSeasonInfoRequest.setLastUpdatedBy(bean.getProdProcessor().getColorwayModifier(skuSeasonRev));
		// updating for EHR 331 Phase-7
		// set Early buy
		if (!skuSeasonType.startsWith("FPD")) {
			skuSeasonInfoRequest
					.setSmEarlyBuy(FormatHelper.parseBoolean(String.valueOf(ssl.getValue(SMProductOutboundWebServiceConstants.EARLY_BUY))));
		}

		// phase 13 - EMP-183 - 423|Add a High Priority checkbox to the
		// colorway-season
		if (skuSeasonType.startsWith("APD") || skuSeasonType.startsWith("smAccessories")) {
			// setting High Priority checkbox to the colorway-season
			skuSeasonInfoRequest.setSmHighPriority(
					FormatHelper.parseBoolean(String.valueOf(ssl.getValue(SMProductOutboundWebServiceConstants.SM_HIGH_PRIORITY))));
		}

	}

	/**
	 * below method set value for planned assembly type on color-way season object,
	 * if color-way season doesn't have the value, then copies the value from product
	 * season link object.
	 * 
	 * @param ssl
	 * @param skuSeasonInfoRequest
	 * @param skuSeasonRev
	 * @throws WTException
	 */
	public void settingProductPlannedAssemblyTypeOnSKUSeasonLink(LCSSKUSeasonLink ssl,
			ColorwaySeasonLink skuSeasonInfoRequest, LCSSKU skuSeasonRev) throws WTException {
		//updated for 3.8.2.0 Build
		LOGGER.debug(">>>>>>>>"+ssl.getFlexType().getAttribute(SMProductOutboundWebServiceConstants.PLANNED_ASSESMBLY_TYPE).getAttGroup());
		if(!FormatHelper.hasContent((String) ssl.getValue(SMProductOutboundWebServiceConstants.PLANNED_ASSESMBLY_TYPE))){

			LCSProduct prodSeasonRev = SeasonProductLocator.getProductSeasonRev(skuSeasonRev);
			prodSeasonRev = (LCSProduct) VersionHelper.latestIterationOf(prodSeasonRev);
			LCSProductSeasonLink prodSeasonLink = (LCSProductSeasonLink) SeasonProductLocator.getSeasonProductLink(prodSeasonRev);
			LOGGER.debug("prodSeasonLink>>>>>>>>"+prodSeasonLink);
			LOGGER.debug("PLANNED_ASSESMBLY_TYPE >>>>>    "+prodSeasonLink.getValue(SMProductOutboundWebServiceConstants.PLANNED_ASSESMBLY_TYPE));

			if(FormatHelper.hasContent((String) prodSeasonLink.getValue(SMProductOutboundWebServiceConstants.PLANNED_ASSESMBLY_TYPE))) {

				//set retail destination.
				String plannedAssembly = prodSeasonLink.getValue(SMProductOutboundWebServiceConstants.PLANNED_ASSESMBLY_TYPE).toString();
				List<String> plannedAssemblyList = FormatHelper.commaSeparatedListToList(plannedAssembly.replaceAll(MOA_DELIM_REGEX, ","));
				for(String assembly : plannedAssemblyList){
					skuSeasonInfoRequest.getSmProductPlannedAssemblyType().add(assembly);
				}
			}


		}else{

			LOGGER.debug("PLANNED_ASSESMBLY_TYPE ######    "+ssl.getValue(SMProductOutboundWebServiceConstants.PLANNED_ASSESMBLY_TYPE));
			String plannedAssembly = ssl.getValue(SMProductOutboundWebServiceConstants.PLANNED_ASSESMBLY_TYPE)
					.toString();
			List<String> plannedAssemblyList = FormatHelper
					.commaSeparatedListToList(plannedAssembly.replaceAll(MOA_DELIM_REGEX, ","));
			for (String assembly : plannedAssemblyList) {
				skuSeasonInfoRequest.getSmProductPlannedAssemblyType().add(assembly);
			}
		}
	}

	/**
	 * @param ssl
	 * @param skuSeasonInfoRequest
	 * @param skuSeasonRev
	 * @throws WTException
	 */
	public void settingRetailDestinationOnSKUSeasonLink(
			LCSSKUSeasonLink ssl, ColorwaySeasonLink skuSeasonInfoRequest,
			LCSSKU skuSeasonRev) throws WTException {

		if(!FormatHelper.hasContent((String) ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_RETAIL_DESTINATION))){
			LCSProduct prodSeasonRev = SeasonProductLocator.getProductSeasonRev(skuSeasonRev);
			prodSeasonRev = (LCSProduct) VersionHelper.latestIterationOf(prodSeasonRev);
			LCSProductSeasonLink prodSeasonLink = (LCSProductSeasonLink) SeasonProductLocator.getSeasonProductLink(prodSeasonRev);
			LOGGER.info("retail destination >>>>>    "+prodSeasonLink.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_RETAIL_DESTINATION));
			//set retail destination.
			String retailDestSku = prodSeasonLink.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_RETAIL_DESTINATION).toString();
			List<String> retailDestinationSku = FormatHelper.commaSeparatedListToList(retailDestSku.replaceAll(MOA_DELIM_REGEX, ","));
			for(String destination : retailDestinationSku){
				skuSeasonInfoRequest.getSmRetailDestinationSync().add(destination);
			}

		}else{
			LOGGER.info("retail destination ######    "+ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_RETAIL_DESTINATION));
			//set retail destination.
			String retailDestSku = ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_RETAIL_DESTINATION).toString();
			List<String> retailDestinationSku = FormatHelper.commaSeparatedListToList(retailDestSku.replaceAll(MOA_DELIM_REGEX, ","));
			for(String destination : retailDestinationSku){
				skuSeasonInfoRequest.getSmRetailDestinationSync().add(destination);
			}
		}
	}

}
