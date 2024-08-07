package com.sportmaster.wc.interfaces.webservices.outbound.product.processor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.log4j.Logger;

import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sizing.ProdSizeCategoryToSeason;
import com.lcs.wc.sizing.ProductSizeCategory;
import com.lcs.wc.sizing.SizingQuery;
import com.lcs.wc.sourcing.LCSCostSheetQuery;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.UserGroupHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.product.helper.SMCostSheetWSDLValidator;
import com.sportmaster.wc.interfaces.webservices.outbound.product.helper.SMProductSeasonWSDLValidator;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductCostSheetRFQ;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLink;
import com.sportmaster.wc.interfaces.webservices.productbean.SourcingConfig;

import wt.org.WTUser;
import wt.util.WTException;

/**
 * @author BSC
 *
 */
public class SMProductSeasonLinkBeanDataProcessor {

	private static final String SM_ACCESSORIES = "smAccessories";
	private static final String AUTHENTICATIONNAME = "AUTHENTICATIONNAME";
	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductSeasonLinkBeanDataProcessor.class);
	/**
	 * Name.
	 */
	private static final String NAME = "name";
	/**
	 * product season lifecycle state.
	 */
	/**
	 * protected constructor.
	 */
	protected SMProductSeasonLinkBeanDataProcessor(){
		//protected construtor.
	}

	/**
	 * Set data on product season link.
	 * @param psl
	 * @param prodSeasonLinkInfoRequest
	 * @throws WTException
	 * @throws DatatypeConfigurationException
	 */
	public ProductSeasonLink setDataOnProductSeasonBean(LCSProductSeasonLink prodseasonlink,
			ProductSeasonLink prodSeasonLinkInfoRequest, SMProductOutboundIntegrationBean bean)
					throws WTException, DatatypeConfigurationException {

		if(!new SMProductSeasonWSDLValidator().validateProductSeasonWSDL(prodseasonlink, bean)) {
			String plmID = bean.getProdHelper().getProductMasterReferencefromLink(prodseasonlink);
			bean.getLogEntryProcessor().setLogEntryForProductSeasonOutboundIntegration(plmID, "WSDL_VALIDATION_FAILED", prodseasonlink, bean);
			return null;
		}
		LOGGER.info("Setting data on Product Season Bean --------");

		//set mdm id.
		if(FormatHelper.hasContent((String) prodseasonlink.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID))){
			prodSeasonLinkInfoRequest.setMdmId(prodseasonlink.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID).toString());
		}

		LCSProduct prodSeasonRev = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+(int)prodseasonlink.getProductSeasonRevId());

		LCSProduct prodSeasonLatest = (LCSProduct) VersionHelper.latestIterationOf(prodSeasonRev);

		LCSProductSeasonLink psl = (LCSProductSeasonLink) SeasonProductLocator.getSeasonProductLink(prodSeasonLatest);


		LCSProduct prodARev = (LCSProduct) VersionHelper.getFirstVersion(prodSeasonRev);
		prodARev = (LCSProduct) VersionHelper.latestIterationOf(prodARev);

		//set object type.
		prodSeasonLinkInfoRequest.setObjectType(bean.getProdHelper().modifyObjectType(prodSeasonRev.getFlexType().getFullNameDisplay()));
		//added for phase 8 SEPD changes
		prodSeasonLinkInfoRequest.setType1Level(bean.getProdHelper().modifyObjectType(prodSeasonRev.getFlexType().getFullNameDisplay().split("\\\\")[0]));
		prodSeasonLinkInfoRequest.setType2Level(bean.getProdHelper().modifyObjectType(prodSeasonRev.getFlexType().getFullNameDisplay().split("\\\\")[1]));

		//set plm id.
		setDataOnPSL(prodSeasonLinkInfoRequest, prodSeasonRev, psl, bean);

		settingDataOnProductSeaon(psl, prodSeasonLinkInfoRequest);

		//added for phase - 8  SEPD chnages
		setProductseasonBeanData(psl, prodSeasonLinkInfoRequest);

		// phase - 13, add composition to integration flow.
		setPSComposiitonData(psl, prodSeasonLinkInfoRequest);
		
		// Phase 14 - EMP-481 - Start
		setLastTriggeredDetails(bean, psl, prodSeasonLinkInfoRequest);
		// Phase 14 - EMP-481 - End
		
		// Phase 14 - EMP-489 - Start
		setDesignBrief(psl, prodSeasonLinkInfoRequest);
		// Phase 14 - EMP-489 - End

		//set category manager.
		LCSLifecycleManaged categoryManager = (LCSLifecycleManaged) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_CATEGORY_MANAGER);
		if(null != categoryManager){
			prodSeasonLinkInfoRequest.setSmCategoryManagement(categoryManager.getValue(NAME).toString());
		}

		LCSSeason season = VersionHelper.latestIterationOf(psl.getSeasonMaster());

		//setting season to class member
		bean.setAssociatedSeason(season);

		//set season MDM ID.
		prodSeasonLinkInfoRequest.setSmMDMSeason((String) season.getValue(SMProductOutboundWebServiceConstants.SEASON_MDM_ID));

		//set Product Technologist.
		FlexObject productTechnologist = (FlexObject) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_TECHNOLOGIST);
		String productTechnologistLogin="";
		String productTechnologistEmail = "";
		if(null != productTechnologist){
			productTechnologistLogin = productTechnologist.getString(AUTHENTICATIONNAME);
			//set Technologist Email.
			WTUser productTechnologistUser = UserGroupHelper.getWTUser(productTechnologistLogin);
			if(null != productTechnologistUser){
				productTechnologistEmail = productTechnologistUser.getEMail();
			}

			//set Technologist User Details.
			if(FormatHelper.hasContent(productTechnologistEmail)){
				prodSeasonLinkInfoRequest.setSmProductTechnologist(productTechnologistEmail);
			}else if(FormatHelper.hasContent(productTechnologistLogin)){
				prodSeasonLinkInfoRequest.setSmProductTechnologist(productTechnologistLogin);
			}else {
				String productTechnologistUserName = productTechnologist.getString("NAME");
				LOGGER.info("PRODUCT TECHNOLOGIST USER NAME **************   "+productTechnologistUserName);
				prodSeasonLinkInfoRequest.setSmProductTechnologist(productTechnologistUserName);
			}
		}

		// Phase -13 : splitted for sonar fix , created setadditionalDataPSL
		// fucntion to set data on prodSeasonLinkInfoRequest bean.
		setadditionalDataPSL(psl, prodSeasonLinkInfoRequest, bean, prodARev, prodSeasonRev, season);

		return prodSeasonLinkInfoRequest;
	}

	// Phase 14 - EMP-489 - Start
	/**
	 * @param bean
	 * @param psl
	 * @param prodSeasonLinkInfoRequest
	 * @throws WTException 
	 */
	private void setDesignBrief(LCSProductSeasonLink psl, ProductSeasonLink prodSeasonLinkInfoRequest) throws WTException {
		// TODO Auto-generated method stub
		String productType = psl.getFlexType().getFullName();
		Boolean designBriefBol= (Boolean)psl.getValue(SMProductOutboundWebServiceConstants.PRODSEASON_DESIGN_BRIEF);
		if(productType.startsWith("SEPD")){
			if(designBriefBol!=null && designBriefBol.booleanValue()){
				prodSeasonLinkInfoRequest.setSmDesignBrief(true);
			}else{
				prodSeasonLinkInfoRequest.setSmDesignBrief(false);	
			}
		}
	}
	// Phase 14 - EMP-489 - End

	// Phase 14 - EMP-481 - Start
	/**
	 * @param bean
	 * @param psl
	 * @param prodSeasonLinkInfoRequest
	 * @throws WTException 
	 * @throws DatatypeConfigurationException 
	 */
	private void setLastTriggeredDetails(SMProductOutboundIntegrationBean bean, LCSProductSeasonLink psl,
			ProductSeasonLink prodSeasonLinkInfoRequest) throws WTException, DatatypeConfigurationException {
		// TODO Auto-generated method stub
		//Date lastTriggeredOnPS = (Date)psl.getValue(SMProductOutboundWebServiceConstants.PRODSEASON_TRIGGERED_ON);
		Date lastTriggeredOnPS = new Date();
		//if(lastTriggeredOnPS!=null){
			prodSeasonLinkInfoRequest.setSmLastIntTriggered(bean.getProdUtill().getXMLGregorianCalendarFormat(lastTriggeredOnPS));
			String userEmail = psl.getCreator().getEMail();
			if(!FormatHelper.hasContent(userEmail)){
				userEmail = psl.getCreator().getFullName();
			}
			prodSeasonLinkInfoRequest.setSmUserIntTriggered(userEmail);
		//}
	}
	// Phase 14 - EMP-481 - End
	/**
	 * Phase -13 : splitted for sonar fix , created setadditionalDataPSL
	 * fucntion to set data on prodSeasonLinkInfoRequest bean.
	 * 
	 * @param psl
	 * @param prodSeasonLinkInfoRequest
	 * @param bean
	 * @param prodARev
	 * @param prodSeasonRev
	 * @param season
	 * @throws WTException
	 * @throws DatatypeConfigurationException
	 */
	private void setadditionalDataPSL(LCSProductSeasonLink psl, ProductSeasonLink prodSeasonLinkInfoRequest,
			SMProductOutboundIntegrationBean bean, LCSProduct prodARev, LCSProduct prodSeasonRev, LCSSeason season)
			throws WTException, DatatypeConfigurationException {

		String userLogin;
		// set Production Manager.
		FlexObject user = (FlexObject) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_PRODUCTION_MANAGER);
		if (null != user) {
			String userEmail = "";
			userLogin = user.getString(AUTHENTICATIONNAME);

			// set Production manager.
			WTUser wtuser = UserGroupHelper.getWTUser(userLogin);
			if (null != wtuser) {
				userEmail = wtuser.getEMail();
			}

			// set production manager.
			if (FormatHelper.hasContent(userEmail)) {
				prodSeasonLinkInfoRequest.setSmProductionManager(userEmail);
			} else if (FormatHelper.hasContent(userLogin)) {
				prodSeasonLinkInfoRequest.setSmProductionManager(userLogin);
			} else {
				String userName = user.getString("NAME");
				LOGGER.info("USER NAME **************   " + userName);
				prodSeasonLinkInfoRequest.setSmProductionManager(userName);
			}
		}

		// set product season status.
		prodSeasonLinkInfoRequest
				.setSmProductSeasonStatus(psl.getValue(SMProductOutboundWebServiceConstants.STATUS_STYLE_SEASONAL).toString());

		// added for phasge -8 3.8.1.0 build starts
		if (FormatHelper.hasContent(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_SALES_NEWNESS)))) {
			// set sales Newsness.
			prodSeasonLinkInfoRequest
					.setSmSalesNewness(psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_SALES_NEWNESS).toString());
		}
		// added for phasge -8 3.8.1.0 build ends
		// Phase 14 - EMP-490 - Start
		setOtherAttributesOnProductSeason(psl, prodSeasonLinkInfoRequest, prodARev, bean);
		// Phase 14 - EMP-490 - End

		// set RRP china.
		setAdditionalAttributesOnProductSeason(psl, prodSeasonLinkInfoRequest);

		if (FormatHelper.hasContent((String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID))) {
			@SuppressWarnings("unchecked")
			List<LCSSourcingConfig> srcList = (List<LCSSourcingConfig>) LCSSourcingConfigQuery
					.getSourcingConfigForProductSeason(prodSeasonRev, season);

			getSourcingConfigurationsForProductSeason(psl, prodSeasonLinkInfoRequest, srcList, bean);

			// added for phase-8 SEPD changes
			setRFQCostsheetData(prodSeasonLinkInfoRequest, prodSeasonRev, season, bean);
		}

		setCreatorAndModifier(prodSeasonLinkInfoRequest, prodSeasonRev, psl, bean);
	}

	/**
	 * @param prodSeasonLinkInfoRequest
	 * @param prodSeasonRev
	 * @param psl
	 * @throws WTException
	 */
	public void setDataOnPSL(ProductSeasonLink prodSeasonLinkInfoRequest, LCSProduct prodSeasonRev,
			LCSProductSeasonLink psl, SMProductOutboundIntegrationBean bean) throws WTException {
		if(FormatHelper.hasContent(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.AD_HOC_PLM_ID_PRODUCT_SEASON_LINK)))){
			prodSeasonLinkInfoRequest.setPlmId(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.AD_HOC_PLM_ID_PRODUCT_SEASON_LINK)));
		}else{
			prodSeasonLinkInfoRequest.setPlmId(bean.getProdHelper().getProductMasterReferencefromLink(psl));
		}
		if(bean.isUpdateProductSeasonLink()){
			prodSeasonLinkInfoRequest.setLifeCycleState(bean.getLifecycleState());
			bean.setProductSeasonLifeCycleState(bean.getLifecycleState());
		}else{
			//set lifecycle state.
			prodSeasonLinkInfoRequest.setLifeCycleState(prodSeasonRev.getLifeCycleState().toString());
			bean.setProductSeasonLifeCycleState(prodSeasonRev.getLifeCycleState().toString());
		}
	}

	/**
	 * @param psl
	 * @param prodSeasonLinkInfoRequest
	 * @param srcList
	 * @param srcConfigBean
	 * @throws WTException
	 */
	public void getSourcingConfigurationsForProductSeason(LCSProductSeasonLink psl,
			ProductSeasonLink prodSeasonLinkInfoRequest, List<LCSSourcingConfig> srcList,
			SMProductOutboundIntegrationBean bean) throws WTException {
		if(null != srcList && !srcList.isEmpty()){
			LOGGER.info("Product Season Sourcing config List Size    >>>>>>>>>>    "+srcList.size());
			SourcingConfig srcConfigBeanData;
			for(LCSSourcingConfig sourcingConfig : srcList){
				LOGGER.info("Sourcing config in List  >>>>   "+sourcingConfig.getName());
				//validate if sourcing Config has valid Business Supplier with valid MDM ID.
				if(validateBusinessSupplierOnSrcConfig(sourcingConfig)){
					SourcingConfig sourcingConfigBean = new SourcingConfig();
					srcConfigBeanData = bean.getProdProcessor().setDataOnSourcingConfig(sourcingConfig,
							sourcingConfigBean, psl, bean);
					//set data on sourcing configuration.
					if(null != srcConfigBeanData){
						prodSeasonLinkInfoRequest.getSourcingConfig().add(srcConfigBeanData);
					}
				}
			}
		}
	}

	/**
	 * added for PHASE-8 SEPD Chnages.
	 * @param psl
	 * @param prodSeasonLinkInfoRequest
	 * @throws WTException
	 */
	public void setRFQCostsheetData(ProductSeasonLink prodSeasonLinkInfoRequest, LCSProduct prodSeasonRev,
			LCSSeason season, SMProductOutboundIntegrationBean bean) throws WTException {

		Collection<?> costSheetColl;
		costSheetColl = LCSCostSheetQuery.getCostSheetsForProduct(null, prodSeasonRev,
				null, season, new ArrayList<Object>(), false, true);

		for(Object rfqCS:costSheetColl) {
			FlexObject rfqCSFO=(FlexObject) rfqCS;
			if("RFQ".equalsIgnoreCase(rfqCSFO.getString("LCSCOSTSHEET.COSTSHEETTYPE"))) {

				try {

					LCSProductCostSheet costSheet = (LCSProductCostSheet) LCSQuery
							.findObjectById("VR:com.lcs.wc.sourcing.LCSProductCostSheet:"
									+ rfqCSFO.getData("LCSCOSTSHEET.BRANCHIDITERATIONINFO"));

					if(bean.getProdProcessor().isCostSheetValidForIntegration(costSheet)){
						ProductCostSheetRFQ rfqCSBean= setDataOnRFQCostsheetBean(costSheet, rfqCSFO, bean);
						if(rfqCSBean != null) {
							prodSeasonLinkInfoRequest.getProductCostSheetRFQ().add(rfqCSBean);
						}
					}
				}catch(WTException | DatatypeConfigurationException e) {
					LOGGER.error(e);
				}
			}}
	}

	public ProductCostSheetRFQ setRFQCostsheetBeanData(ProductCostSheetRFQ rfqCSBean,FlexObject rfqCSFO) throws WTException {

		//setting RFQ
		if(FormatHelper.hasContent(rfqCSFO.getString("RFQREQUEST.BRANCHIDITERATIONINFO"))) {
			rfqCSBean.setRfq(rfqCSFO.getString("RFQREQUEST."
					+ FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.sourcing.RFQRequest")
					.getAttribute("ptcrfqName").getColumnName()));
		}

		//setting quote
		if(FormatHelper.hasContent(rfqCSFO.getString("RFQRESPONSE.BRANCHIDITERATIONINFO"))) {
			rfqCSBean.setQuote(rfqCSFO.getString("RFQRESPONSE."
					+ FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.sourcing.RFQResponse")
					.getAttribute("responseName").getColumnName()));
		}

		//setting quote vendor
		if(FormatHelper.hasContent(rfqCSFO.getString("RFQVENDOR.BRANCHIDITERATIONINFO"))) {
			LCSSupplier splr=(LCSSupplier) LCSQuery.findObjectById("VR:com.lcs.wc.supplier.LCSSupplier:"+rfqCSFO.getString("RFQVENDOR.BRANCHIDITERATIONINFO"));
			if(FormatHelper.hasContent(String.valueOf(splr.getValue(SMProductOutboundWebServiceConstants.RFQ_VENDOR_MDM_ID)))) {
				rfqCSBean.setQuoteVendor(String.valueOf(splr.getValue(SMProductOutboundWebServiceConstants.RFQ_VENDOR_MDM_ID)));
			}
		}
		return rfqCSBean;
	}

	public ProductCostSheetRFQ setDataOnRFQCostsheetBean(LCSProductCostSheet costSheet, FlexObject rfqCSFO,
			SMProductOutboundIntegrationBean bean) throws WTException, DatatypeConfigurationException {

		ProductCostSheetRFQ  rfqCSBean;
		SMCostSheetBeanDataProcessor csBeanProcessor = new SMCostSheetBeanDataProcessor();
		if(!new SMCostSheetWSDLValidator().validateRFQCostSheet(costSheet,rfqCSFO, bean)) {
			LCSProduct prodObj = VersionHelper.latestIterationOf(costSheet.getProductMaster());
			prodObj = (LCSProduct) VersionHelper.getFirstVersion(prodObj);
			prodObj = (LCSProduct) VersionHelper.latestIterationOf(prodObj);
			LOGGER.info("Cost Sheet Season  -------------->   "+costSheet.getSeasonMaster());
			if(null != costSheet.getSeasonMaster()){
				LCSSeason seasonObj = VersionHelper.latestIterationOf(costSheet.getSeasonMaster());

				LCSProductSeasonLink pslObj = (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(prodObj, seasonObj);

				LCSProduct productSeasonRevObj = SeasonProductLocator.getProductSeasonRev(pslObj);
				//Get latest iteration.
				productSeasonRevObj = (LCSProduct) VersionHelper.latestIterationOf(productSeasonRevObj);
				//get Season-product Link.
				LCSProductSeasonLink latestSPLObj = (LCSProductSeasonLink) SeasonProductLocator.getSeasonProductLink(productSeasonRevObj);
				String plmIDVal = bean.getProdHelper().getProductMasterReferencefromLink(latestSPLObj);
				bean.getLogEntryProcessor().setLogEntryForProductSeasonOutboundIntegration(plmIDVal,"WSDL_VALIDATION_FAILED", latestSPLObj, bean);
				return null;
			}
			return null;
		}
		rfqCSBean = new ProductCostSheetRFQ();

		LOGGER.info("Set Data on RFQ Cost Sheet Bean !!!");

		rfqCSBean = setRFQCostsheetBeanData(rfqCSBean, rfqCSFO);

		rfqCSBean.setWhatIf(costSheet.isWhatIf());
		//setting name
		rfqCSBean.setName(costSheet.getName());

		//set plm id.
		rfqCSBean.setPlmId(String.valueOf(costSheet.getBranchIdentifier()));

		csBeanProcessor.setDataOnRFQCostSheetColorLink(costSheet, rfqCSBean);

		//set Season MDM ID.
		rfqCSBean.setSmMDMSeason(bean.getAssociatedSeason().getValue(SMProductOutboundWebServiceConstants.SEASON_MDM_ID).toString());

		//setting pricing country mdm id value
		if(FormatHelper.hasContent(String.valueOf((costSheet.getValue(SMProductOutboundWebServiceConstants.PRICING_COUNTRY))))) {
			LCSCountry country= (LCSCountry) costSheet.getValue(SMProductOutboundWebServiceConstants.PRICING_COUNTRY);
			if(FormatHelper.hasContent(String.valueOf(country.getValue(SMProductOutboundWebServiceConstants.PRICING_COUNTRY_MDMID)))) {

				rfqCSBean.setPricingCountry(String.valueOf(country.getValue(SMProductOutboundWebServiceConstants.PRICING_COUNTRY_MDMID)));
			}
		}

		//set quoted price.
		//updated for PHASE -8 SEPD CHNaGES
		if(costSheet.getFlexType().getFullNameDisplay().startsWith("Sports Equipment")) {
			if(FormatHelper.hasContent(String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.PURCHASE_PRICE_CALC_CONTRACT_CURENCY)))){
				double quotedPriceGSSupplier = (Double) costSheet.getValue(SMProductOutboundWebServiceConstants.PURCHASE_PRICE_CALC_CONTRACT_CURENCY);
				rfqCSBean.setSmQuotedPriceGSCurr(BigDecimal.valueOf(quotedPriceGSSupplier));
			}
		}else {
			if(FormatHelper.hasContent(String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_QUOTED_PRICE_GS_SUPPLIER_CURRENCY)))){
				double quotedPriceGSSupplier = (Double) costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_QUOTED_PRICE_GS_SUPPLIER_CURRENCY);
				rfqCSBean.setSmQuotedPriceGSCurr(BigDecimal.valueOf(quotedPriceGSSupplier));
			}
		}

		// Phase -13 | Splited for Sonar fix , created
		// setAdditionalDataOnRFQCostsheetBean function to set data on
		// ProductCostSheetRFQ bean.
		setAdditionalDataOnRFQCostsheetBean(costSheet, rfqCSBean, csBeanProcessor);

		//is primary.
		rfqCSBean.setPrimaryCostSheet(String.valueOf(costSheet.isPrimaryCostSheet()));

		//set created on.
		rfqCSBean.setCreatedOn(bean.getProdUtill().getXMLGregorianCalendarFormat(costSheet.getCreateTimestamp()));

		//set created by.
		rfqCSBean.setCreatedBy(bean.getProdProcessor().getCostSheetCreator(costSheet));

		//set last updated on
		rfqCSBean.setLastUpdated(bean.getProdUtill().getXMLGregorianCalendarFormat(costSheet.getModifyTimestamp()));

		//set last updated by.
		rfqCSBean.setLastUpdatedBy(bean.getProdProcessor().getCostSheetModifier(costSheet));

		return rfqCSBean;

	}

	/**
	 * Phase -13 | created setAdditionalDataOnRFQCostsheetBean function to set
	 * data on ProductCostSheetRFQ bean.
	 * 
	 * @param costSheet
	 * @param rfqCSBean
	 * @param csBeanProcessor
	 * @throws WTException
	 */
	private void setAdditionalDataOnRFQCostsheetBean(LCSProductCostSheet costSheet, ProductCostSheetRFQ rfqCSBean,
			SMCostSheetBeanDataProcessor csBeanProcessor)
			throws WTException {

		LCSProduct prdt = VersionHelper.latestIterationOf(costSheet.getProductMaster());

		// validates FPD Type costsheet for Phase4 fpd cr
		String productType = prdt.getFlexType().getFullName();

		// set manual GS currency.
		if ((productType.startsWith("APD") || productType.startsWith(SM_ACCESSORIES)) && FormatHelper.hasContent(
				String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_FOB_TOTAL_INT_MANUAL_GS_CURRENCY)))) {
			double fobTotalIntManualGSCurrency = (Double) costSheet
					.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_FOB_TOTAL_INT_MANUAL_GS_CURRENCY);
			rfqCSBean.setSmFOBTotalIntGSCurr(BigDecimal.valueOf(fobTotalIntManualGSCurrency));
		}

		// set Contract currency.
		rfqCSBean.setSmCsContractCurrency(costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_CONTRACT_CURRENCY).toString());

		// set incoterms.
		rfqCSBean.setSmCsIncoterms(costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_INCOTERMS).toString());

		csBeanProcessor.setRFQCostSheetDestinationDataOnBean(costSheet, rfqCSBean);

		// set costing stage.
		// updated for PHASE -8 SEPD CHNaGES
		if (costSheet.getFlexType().getFullNameDisplay().startsWith("Sports Equipment")
				|| "Apparel\\Accessories".equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			rfqCSBean.setSmCostingStage(costSheet.getValue(SMProductOutboundWebServiceConstants.SEPD_COSTING_STAGE).toString());
		//Phase 14 - APD MultiCurrency: START //
		} else if("Apparel\\0. Multicurrency Apparel".equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			rfqCSBean.setSmCostingStage(costSheet.getValue(SMProductOutboundWebServiceConstants.CS_MCAPP_COSTING_STAGE_KEY).toString());
		}//Phase 14 - APD MultiCurrency: END //
		else {
			rfqCSBean.setSmCostingStage(costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_COSTING_STAGE).toString());
		}

		// updated for 3.9.1 build
		String status = String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_STATUS));
		if ("Apparel\\Accessories".equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			status = String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.ACC_COST_SHEET_STATUS));
		//Phase 14 - APD MultiCurrency: START //
		}else if("Apparel\\0. Multicurrency Apparel".equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			status = String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.CS_MCAPP_COSTING_STATUS_KEY));
		}//Phase 14 - APD MultiCurrency: END //
		if (FormatHelper.hasContent(status)) {
			rfqCSBean.setVrdCSStatus(status);
		}
	}

	/**
	 * @param sourcingConfig
	 * @throws WTException
	 */
	public boolean validateBusinessSupplierOnSrcConfig(
			LCSSourcingConfig sourcingConfig) throws WTException {
		LCSSupplier businessSupplierOnSrcConfig = (LCSSupplier) sourcingConfig.getValue(SMProductOutboundWebServiceConstants.SOURCING_CONFIGURATION_BUSINESS_SUPPLIER);
		if(businessSupplierOnSrcConfig != null){
			if(FormatHelper.hasContent((String) businessSupplierOnSrcConfig.getValue(SMProductOutboundWebServiceConstants.SOURCING_CONFIGURATION_BUSINESS_SUPPLIER_MDM_ID))){
				return true;
			}
		}else{
			LOGGER.debug("WON'T PROCESS SOURCING CONFIGURATION  >>>  "+sourcingConfig.getName()+"  FOR REQUEST AS THERE IS NO VALID BUSINESS SUPPLIER OR BUSINESS SUPPLIER HAS NO MDM ID");
		}
		return false;
	}

	/**
	 * @param psl
	 * @param prodSeasonLinkInfoRequest
	 * @throws WTException
	 */
	public void setAdditionalAttributesOnProductSeason(
			LCSProductSeasonLink psl,
			ProductSeasonLink prodSeasonLinkInfoRequest) throws WTException {
		//set RRP China.
		if(FormatHelper.hasContent(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_RRP_CHINA)))){
			double priceRRPChina = (Double)(psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_RRP_CHINA));
			prodSeasonLinkInfoRequest.setSmRecommendedRetailPriceStyleRMB((BigDecimal.valueOf(priceRRPChina)));
		}

		//set RRP Russia.
		if(FormatHelper.hasContent(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_RRP_RUSSIA)))){
			double priceRRPRussia = (Double)(psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_RRP_RUSSIA));
			prodSeasonLinkInfoRequest.setSmRecommendedRetailPriceStyle(BigDecimal.valueOf(priceRRPRussia));
		}

		//set initial FC style.
		if(FormatHelper.hasContent(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_INITIAL_FC_STYLE)))){
			prodSeasonLinkInfoRequest.setInitialFCStyle((BigInteger.valueOf((Long) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_INITIAL_FC_STYLE))));
		}


	}

	/**
	 * @param psl
	 * @param prodSeasonLinkInfoRequest
	 * @param bean 
	 * @param prodSeasonRev
	 * @throws WTException
	 */
	public void setOtherAttributesOnProductSeason(
			LCSProductSeasonLink psl,
			ProductSeasonLink prodSeasonLinkInfoRequest,
			LCSProduct prodArev, SMProductOutboundIntegrationBean bean) throws WTException {
		//set PSL MDM ID.
		if(FormatHelper.hasContent((String) prodArev.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY))){
			prodSeasonLinkInfoRequest.setSmMDMProduct(prodArev.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY).toString());
		}else{
			prodSeasonLinkInfoRequest.setSmMDMProduct(SMProductOutboundWebServiceConstants.EMPTY_MDM_ID);
		}
		//set plm id.
		LCSProduct prodFirstVersion = (LCSProduct) VersionHelper.getFirstVersion(prodArev);
		prodFirstVersion = (LCSProduct) VersionHelper.latestIterationOf(prodFirstVersion);
		LOGGER.debug("Product PLM ID on Product Season Link  >>>>>>>>    "+prodFirstVersion.getBranchIdentifier());
		prodSeasonLinkInfoRequest.setPlmIdProduct(String.valueOf(prodFirstVersion.getBranchIdentifier()));

		//updated logic for FPD Changes
		String productType=psl.getFlexType().getFullName();

		//updating for EHR 331 Phase-7
		//set Development category.
		if ((productType.startsWith("APD") || productType.startsWith(SM_ACCESSORIES)) && FormatHelper.hasContent(
				(String) psl.getValue(SMProductOutboundWebServiceConstants.DEVELOPMENT_CATEGORY))) {
			// added for Phase-7 CR 331
			prodSeasonLinkInfoRequest.setSmDevelopmentCategory(
					String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.DEVELOPMENT_CATEGORY)));
		}
		//updating for 3.9.2.0 Build changes, stops sending dev newness data for AAC\SEPD node
		//set development newness.

		if ((productType.startsWith("APD") || (productType.startsWith(SM_ACCESSORIES) && !productType.startsWith(SM_ACCESSORIES +"\\smSEPDAccessories"))) && FormatHelper.hasContent(
				(String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_DEVELOPMENT_NEWNESS))) {
			prodSeasonLinkInfoRequest.setSmDevelopmentNewness(psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_DEVELOPMENT_NEWNESS).toString());

		}

		//set production group.
		prodSeasonLinkInfoRequest.setSmProductionGroup(psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_PRODUCTION_GROUP).toString());

		//set commercial size CN.
		LCSLifecycleManaged commercialSizeCN = (LCSLifecycleManaged) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_COMMERCIAL_SIZES_CHINA);
		LOGGER.info("commercial size CN   *********   "+commercialSizeCN);
		if(null != commercialSizeCN){
			LOGGER.debug("Commercial Size China Value  >>>>   "+commercialSizeCN.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_COMMERCIAL_SIZES_SIZE_VALUE));
			prodSeasonLinkInfoRequest.setSmCommercialSizesChina(commercialSizeCN.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_COMMERCIAL_SIZES_SIZE_VALUE).toString());

			LOGGER.debug("Commercial Size China Master Scale Value >>>>   "+commercialSizeCN.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_COMMERCIAL_SIZE_MASTER_SCALE));
			prodSeasonLinkInfoRequest.setSmMasterScaleChina(commercialSizeCN.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_COMMERCIAL_SIZE_MASTER_SCALE).toString().trim());
		}

		//set commercial size RU.
		LCSLifecycleManaged commercialSizeRU = (LCSLifecycleManaged) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_COMMERCIAL_SIZES_RUSSIA);
		LOGGER.info("commercial size RU   *********   "+commercialSizeRU);
		if(null != commercialSizeRU){
			LOGGER.debug("Commercial Size Russia Value  >>>>   "+commercialSizeRU.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_COMMERCIAL_SIZES_SIZE_VALUE));
			prodSeasonLinkInfoRequest.setSmCommercialSizesRussia(commercialSizeRU.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_COMMERCIAL_SIZES_SIZE_VALUE).toString());

			LOGGER.debug("Commercial Size Russia Master Scale Value >>>>   "+commercialSizeRU.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_COMMERCIAL_SIZE_MASTER_SCALE));
			prodSeasonLinkInfoRequest.setSmMasterScaleRussia(commercialSizeRU.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_COMMERCIAL_SIZE_MASTER_SCALE).toString().trim());
		// Phase 14 - EMP-490 - Start
		}else{
			//-- Fetch the PSD for Season
			fetchPSDDetails(psl, prodArev, prodSeasonLinkInfoRequest, bean);
		}// Phase 14 - EMP-490 - End
	}

	/**
	 * @param psl
	 * @param prodArev 
	 * @param prodSeasonLinkInfoRequest
	 * @param bean
	 */
	private void fetchPSDDetails(LCSProductSeasonLink psl,
			LCSProduct prodArev, ProductSeasonLink prodSeasonLinkInfoRequest,
			SMProductOutboundIntegrationBean bean) throws WTException{
		// TODO Auto-generated method stub
		LCSSeason season = (LCSSeason)VersionHelper.latestIterationOf(psl.getSeasonMaster()); 
		PreparedQueryStatement pqs = SizingQuery.getPSDDataQueryForProductSeason(prodArev, season);
		LOGGER.debug("-- Sizing Query: "+pqs);
		SearchResults src = LCSQuery.runDirectQuery(pqs);
		Collection sizingCol = src.getResults();
		LOGGER.debug("-- Sizing COLL : "+sizingCol);
		if(sizingCol!=null && sizingCol.size() > 0){
			if(sizingCol.size() == 1){
				LOGGER.debug("-- Lets process --");
				FlexObject fob = (FlexObject)sizingCol.iterator().next();
				ProductSizeCategory psd = (ProductSizeCategory)LCSQuery
						.findObjectById("VR:com.lcs.wc.sizing.ProductSizeCategory:"+fob.getString("PRODUCTSIZECATEGORY.BRANCHIDITERATIONINFO"));
				ProdSizeCategoryToSeason psdSeasonObj = (ProdSizeCategoryToSeason)LCSQuery
						.findObjectById("VR:com.lcs.wc.sizing.ProdSizeCategoryToSeason:"+fob.getString("PRODSIZECATEGORYTOSEASON.BRANCHIDITERATIONINFO"));
				String psdMDMID = (String)psd.getValue(SMProductOutboundWebServiceConstants.PSD_MDM_ID_KEY);
				String psdPLMID = getPSDPLMID(fob, season);
				String seasonalSizes = MOAHelper.parseOutDelims(psdSeasonObj.getSizeValues(), ";");
				//-- Setting the Payload Bean.
				LOGGER.debug("-- psdMDMID : "+psdMDMID);
				LOGGER.debug("-- psdPLMID : "+psdPLMID);
				LOGGER.debug("-- seasonalSizes : "+seasonalSizes);
				if(FormatHelper.hasContent(psdMDMID)){
					prodSeasonLinkInfoRequest.setSmMDMSIZ(psdMDMID);
					prodSeasonLinkInfoRequest.setSmPLMIDSIZ(psdPLMID);
					prodSeasonLinkInfoRequest.setSmCommercialSizesRussia(seasonalSizes);
				}
			}else{
				LOGGER.debug("-- Throw ERROR - more than 2 PSDs --");
				String plmIDVal = bean.getProdHelper().getProductMasterReferencefromLink(psl);
				bean.setResponseErrorReason("There are 2 or more seasonal Product Size Definition in "+season.getName());
				bean.getLogEntryProcessor().setLogEntryForProductSeasonOutboundIntegration(plmIDVal,"UPDATE_FAILED", psl, bean);
				prodSeasonLinkInfoRequest.setSmPLMIDSIZ("MULTIPLE_PSDs_FOUND_FOR_PRODUCT");
				LOGGER.debug("-- ERROR: Multiple PSDs found for a product. check logs.");
			}
		}else{
			//Do nothing.
			LOGGER.debug("-- DO NOTHING --");
		}
	}

	/**
	 * @param fob
	 * @param season
	 * @return
	 */
	private String getPSDPLMID(FlexObject fob, LCSSeason season) {
		// TODO Auto-generated method stub
		String psdPLMID = null;
		String psdMaster = fob.getString("PRODUCTSIZECATEGORYMASTER.IDA2A2");
		String seasonMasterID = FormatHelper.getNumericObjectIdFromObject(season.getMaster());
		psdPLMID = psdMaster + "-" + seasonMasterID;
		return psdPLMID;
	}

	/**
	 * added for pahse - 8 SEPD chnages
	 * @param psl
	 * @param prodSeasonLinkInfoRequest
	 * @throws WTException
	 */
	public void setProductseasonBeanData(LCSProductSeasonLink psl,
			ProductSeasonLink prodSeasonLinkInfoRequest)throws WTException {

		String productType=psl.getFlexType().getFullName();

		// set development group
		if (!productType.startsWith("FPD")
				&& FormatHelper.hasContent(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.DEVELOPMENT_GROUP)))) {
				prodSeasonLinkInfoRequest.setSmDevelopmentGroup(String.valueOf( psl.getValue(SMProductOutboundWebServiceConstants.DEVELOPMENT_GROUP)));
			}

		if(productType.startsWith("SEPD")) {
			//set PROTOTYPE value
			if(FormatHelper.hasContent(String.valueOf( psl.getValue(SMProductOutboundWebServiceConstants.PROTOTYPE)))) {
				prodSeasonLinkInfoRequest.setSmPrototype(FormatHelper.parseBoolean(String.valueOf( psl.getValue(SMProductOutboundWebServiceConstants.PROTOTYPE))));
			}

			//updated for 3.8.2.0 Build
			// set PLANNED_ASSESMBLY_TYPE value
			if (FormatHelper.hasContent(
					String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.PLANNED_ASSESMBLY_TYPE)))) {

				String plannedAssembly = psl.getValue(SMProductOutboundWebServiceConstants.PLANNED_ASSESMBLY_TYPE)
						.toString();
				List<String> plannedAssemblyList = FormatHelper
						.commaSeparatedListToList(plannedAssembly.replaceAll("[|~*~|]", ","));
				for (String assembly : plannedAssemblyList) {
					prodSeasonLinkInfoRequest.getSmProductPlannedAssemblyType().add(assembly);
				}
			}

		}

		// Phase -13 | Splited for Sonar fix , created
		// setAdditionalProductseasonBeanData function to set data on
		// ProductSeasonLink bean.
		setAdditionalProductseasonBeanData(psl, prodSeasonLinkInfoRequest);

	}

	/**
	 * created setAdditionalProductseasonBeanData function to set data on
	 * ProductSeasonLink bean.
	 * 
	 * @param psl
	 * @param prodSeasonLinkInfoRequest
	 * @throws WTException
	 */
	private void setAdditionalProductseasonBeanData(LCSProductSeasonLink psl, ProductSeasonLink prodSeasonLinkInfoRequest)
			throws WTException {

		String productType = psl.getFlexType().getFullName();

		if(productType.startsWith("FPD")) {
			//set FASTTRACK value
			if(FormatHelper.hasContent(String.valueOf( psl.getValue(SMProductOutboundWebServiceConstants.FASTTRACK)))) {
				prodSeasonLinkInfoRequest.setFastTrack(String.valueOf( psl.getValue(SMProductOutboundWebServiceConstants.FASTTRACK)));
			}
		}else{
			if(FormatHelper.hasContent(String.valueOf( psl.getValue(SMProductOutboundWebServiceConstants.FASTTRACK_OTHERS)))) {
				prodSeasonLinkInfoRequest.setFastTrack(String.valueOf( psl.getValue(SMProductOutboundWebServiceConstants.FASTTRACK_OTHERS)));
			}
		}

		//setting default value fasle for 3.8.0.0 build
		//Updated NOS value for 3.8.1.0 build
		if(productType.startsWith("APD") || productType.startsWith(SM_ACCESSORIES)) {

			prodSeasonLinkInfoRequest.setNos(FormatHelper.parseBoolean(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.NOS))));

		} else if(productType.startsWith("FPD") || productType.startsWith("SEPD")) {

			prodSeasonLinkInfoRequest.setNos(FormatHelper.parseBoolean("false"));
		}

		// phase 13 - EMP-415 - Integration of "Sourcing" attribute
		if (productType.startsWith("FPD")
				&& FormatHelper.hasContent(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.SM_SOURCING)))) {
			// set SM_SOURCING value
			prodSeasonLinkInfoRequest
			.setSmSourcing(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.SM_SOURCING)));
		}

	}

	/**
	 * phase 13 - add composition to product integration flow.
	 *
	 * @param psl                       - LCSProductSeasonLink
	 * @param prodSeasonLinkInfoRequest - ProductSeasonLink
	 * @throws WTException
	 */
	private void setPSComposiitonData(LCSProductSeasonLink psl, ProductSeasonLink prodSeasonLinkInfoRequest)
			throws WTException {

		// getting product flex type
		String productType = psl.getFlexType().getFullName();
		boolean blocked = FormatHelper
				.parseBoolean(String.valueOf(psl.getValue(SMProductInboundWebServiceConstants.PSL_BLOCKED)));
		// validate product type
		if (productType.startsWith("FPD") && blocked) {

			// validate upper has value
			if (FormatHelper.hasContent(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.PSL_UPPER)))) {
				// set upper value
				prodSeasonLinkInfoRequest
				.setSmUpper(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.PSL_UPPER)));
			}

			// validate linning has value
			if (FormatHelper
					.hasContent(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.PSL_LINNING)))) {
				// set linning value
				prodSeasonLinkInfoRequest
				.setSmLining(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.PSL_LINNING)));
			}

			// validate sole has value
			if (FormatHelper.hasContent(
					String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.PSL_SOLE)))) {

				String sole = psl.getValue(SMProductOutboundWebServiceConstants.PSL_SOLE)
						.toString();
				// set sole value
				MOAHelper.getMOACollection(sole).stream().forEach(p -> prodSeasonLinkInfoRequest.getSmSole().add(p));
			}
		}
	}
	/**
	 * @param psl
	 * @param prodSeasonLinkInfoRequest
	 * @throws WTException
	 */
	public void settingDataOnProductSeaon(LCSProductSeasonLink psl,
			ProductSeasonLink prodSeasonLinkInfoRequest) throws WTException {
		FlexObject user;
		String userLogin;
		String userEmail = "";
		//set product season retail destination.
		if(FormatHelper.hasContent((String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_RETAIL_DESTINATION))){
			String retailDest = psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_RETAIL_DESTINATION).toString();
			List<String> retailDestination = FormatHelper.commaSeparatedListToList(retailDest.replaceAll("[|~*~|]", ","));

			for(String destination : retailDestination){
				prodSeasonLinkInfoRequest.getSmRetailDestinationSync().add(destination);
			}

		}

		//set target purchase price.
		if(FormatHelper.hasContent((String.valueOf( psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_TARGET_PURCHASE_PRICE))))){
			double targetPurchasePrice = (Double) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_TARGET_PURCHASE_PRICE);
			prodSeasonLinkInfoRequest.setSmTargetPurchasePrice(BigDecimal.valueOf(targetPurchasePrice));
		}
		//updated logic for FPD Changes
		//updated logic for pahse - 8 SEPD Changes
		String productType=psl.getFlexType().getFullName();
		//set Planned colorways.
		if (!productType.startsWith("FPD")  && FormatHelper.hasContent((String
				.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_PLANNED_COLORWAYS))))) {
			prodSeasonLinkInfoRequest.setSmPlannedColorways(BigInteger.valueOf((Long) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_PLANNED_COLORWAYS)));
		}


		//brand manager
		user = (FlexObject) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_BRAND_MANAGER);

		if(null != user){
			userLogin = user.getString(AUTHENTICATIONNAME);
			WTUser wtuser = UserGroupHelper.getWTUser(userLogin);
			if(null != wtuser){
				userEmail = wtuser.getEMail();
			}
			//set brand manager.
			if(FormatHelper.hasContent(userEmail)){
				prodSeasonLinkInfoRequest.setSmBrandManager(userEmail);
			}else if(FormatHelper.hasContent(userLogin)){
				prodSeasonLinkInfoRequest.setSmBrandManager(userLogin);
			}else {
				String userName = user.getString("NAME");
				LOGGER.info("USER NAME **************   "+userName);
				prodSeasonLinkInfoRequest.setSmBrandManager(userName);
			}
		}

		// Phase-13 : splitting for sonar fix, created new function to set
		// capsule value.
		setPSLCapsuleVal(productType, psl, prodSeasonLinkInfoRequest);
	}
	/**
	 * Phase -13 :- set capsule value on product season bean.
	 * 
	 * @param productType
	 * @param psl
	 * @param prodSeasonLinkInfoRequest
	 * @throws WTException
	 */
	private void setPSLCapsuleVal(String productType, LCSProductSeasonLink psl, ProductSeasonLink prodSeasonLinkInfoRequest)
			throws WTException {

		if (!productType.startsWith("SEPD")){

			//capsule
			LCSLifecycleManaged capsule = (LCSLifecycleManaged) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_CAPSULE);
			if(null != capsule){
				//send MDM ID if required.
				prodSeasonLinkInfoRequest.setSmCapsule(capsule.getValue(NAME).toString());
			}
		}
	}

	/**
	 * @param prodSeasonLinkInfoRequest
	 * @param prodSeasonRev
	 * @throws DatatypeConfigurationException
	 */
	public void setCreatorAndModifier(ProductSeasonLink prodSeasonLinkInfoRequest, LCSProduct prodSeasonRev,
			SMProductOutboundIntegrationBean bean) throws DatatypeConfigurationException {
		//set created on.
		prodSeasonLinkInfoRequest.setCreatedOn(bean.getProdUtill().getXMLGregorianCalendarFormat(prodSeasonRev.getCreateTimestamp()));

		//set created by
		prodSeasonLinkInfoRequest.setCreatedBy(bean.getProdProcessor().getProductCreator(prodSeasonRev));

		//set last updated on.
		prodSeasonLinkInfoRequest.setLastUpdated(bean.getProdUtill().getXMLGregorianCalendarFormat(prodSeasonRev.getModifyTimestamp()));

		//set last updated by.
		prodSeasonLinkInfoRequest.setLastUpdatedBy(bean.getProdProcessor().getProductModifier(prodSeasonRev));
	}

	/**
	 * set modifier date according to product season link.
	 * fox for jira ticket SMPLM-836
	 * @param prodSeasonLinkInfoRequest
	 * @param prodSeasonRev
	 * @param productSeason
	 * @throws DatatypeConfigurationException
	 */
	public void setCreatorAndModifier(ProductSeasonLink prodSeasonLinkInfoRequest, LCSProduct prodSeasonRev,
			LCSProductSeasonLink productSeason, SMProductOutboundIntegrationBean bean)
					throws DatatypeConfigurationException {

		//set created on.
		prodSeasonLinkInfoRequest.setCreatedOn(bean.getProdUtill().getXMLGregorianCalendarFormat(productSeason.getCreateTimestamp()));

		//set created by
		prodSeasonLinkInfoRequest.setCreatedBy(bean.getProdProcessor().getProductCreator(prodSeasonRev));

		//set last updated on.
		prodSeasonLinkInfoRequest.setLastUpdated(bean.getProdUtill().getXMLGregorianCalendarFormat(productSeason.getModifyTimestamp()));

		//set last updated by.
		prodSeasonLinkInfoRequest.setLastUpdatedBy(bean.getProdProcessor().getProductModifier(prodSeasonRev));
	}
}
