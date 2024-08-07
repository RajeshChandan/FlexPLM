/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.sourcing.LCSCostSheet;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.Colorway;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLink;
import com.sportmaster.wc.interfaces.webservices.productbean.Product;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductCostSheet;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLink;
import com.sportmaster.wc.interfaces.webservices.productbean.SourcingConfig;

/**
 * @author ITC_Infotech.
 *
 */
public class SMProductOutboundDataProcessor {

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductOutboundDataProcessor.class);
	/**
	 * protected constructor.
	 */
	public SMProductOutboundDataProcessor(){
		//public constructor.
	}

	/**
	 * Process Product module.
	 * @param product - LCSProduct.
	 * @param prodInfoRequest - Product.
	 */
	public Product setDataForProductOutboundRequest(LCSProduct product, Product prodInfoRequest,
			LCSProductSeasonLink psl, SMProductOutboundIntegrationBean bean) {
		try{

			//set data on product bean.
			Product prodDataBean = new SMProductBeanDataProcessor().setDataForProductBean(product, prodInfoRequest, psl, bean);
			if(null != prodDataBean ){
				return prodDataBean;
			}else{
				LOGGER.info("Product Bean Object is NULL ****");
				return null;
			}

		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage(), we);
			return null;
		} catch (javax.xml.datatype.DatatypeConfigurationException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return null;
		} catch (IOException ioe) {
			LOGGER.error(ioe.getLocalizedMessage(), ioe);
			return null;
		}
	}



	/**
	 * Process Colorway module.
	 * @param sku - LCSSKU.
	 * @param skuInfoRequest - Colorway.
	 */
	public Colorway setDataForColorwayOutboundRequest(LCSSKU sku, Colorway skuInfoRequest,
			SMProductOutboundIntegrationBean bean) {
		try{
			//set data on colorway bean.
			Colorway colorwayDataBean = new SMColorwayBeanDataProcessor().setDataOnColorwayBean(sku, skuInfoRequest, bean);
			if(null != colorwayDataBean){
				return colorwayDataBean;
			}else{
				return null;
			}

		}catch(WTException wte){
			LOGGER.error(wte.getLocalizedMessage(), wte);
			return null;
		} catch (javax.xml.datatype.DatatypeConfigurationException dataConfigExp) {
			LOGGER.error(dataConfigExp.getLocalizedMessage(), dataConfigExp);
			return null;
		} catch (IOException ioe) {
			LOGGER.error(ioe.getLocalizedMessage(), ioe);
			return null;
		}
	}



	/**
	 * Process Product Season Link module.
	 * @param psl - LCSProductSeasonLink.
	 * @param prodSeasonLinkInfoRequest - ProductSeasonLink.
	 */
	public ProductSeasonLink setDataForProductSeasonLinkOutboundRequest(LCSProductSeasonLink psl,
			ProductSeasonLink prodSeasonLinkInfoRequest, SMProductOutboundIntegrationBean bean) {
		try{

			//set data on product season link.
			ProductSeasonLink prodSeasonLinkDataBean = new SMProductSeasonLinkBeanDataProcessor()
					.setDataOnProductSeasonBean(psl, prodSeasonLinkInfoRequest, bean);
			if(null != prodSeasonLinkDataBean){
				return prodSeasonLinkDataBean;
			}else{
				return null;
			}

		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage(), we);
			return null;
		} catch (javax.xml.datatype.DatatypeConfigurationException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return null;
		}
	}



	/**
	 * Process Colorway Season Link module.
	 * @param ssl - LCSSKUSeasonLink.
	 * @param skuSeasonInfoRequest - ColorwaySeasonLink.
	 */
	public ColorwaySeasonLink setDataForColorwaySeasonLinkOutboundRequest(LCSSKUSeasonLink ssl,
			ColorwaySeasonLink skuSeasonInfoRequest, SMProductOutboundIntegrationBean bean) {
		try{

			//set data on colorway season bean.
			ColorwaySeasonLink colorwaySeasonLinkDataBean = new SMColorwaySeasonLinkBeanDataProcessor()
					.setDataOnColorwaySeasonBean(ssl, skuSeasonInfoRequest, bean);
			if(null != colorwaySeasonLinkDataBean){
				return colorwaySeasonLinkDataBean;
			}else{
				return null;
			}
		}catch(WTException ex){
			LOGGER.error(ex.getLocalizedMessage(), ex);
			return null;
		} catch (javax.xml.datatype.DatatypeConfigurationException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return null;
		}
	}



	/**
	 * Setting data on Sourcing config bean.
	 * @param src
	 * @param sourcingConfigBean
	 * @param psl
	 */
	public SourcingConfig setDataOnSourcingConfig(LCSSourcingConfig src, SourcingConfig sourcingConfigBean,
			LCSProductSeasonLink psl, SMProductOutboundIntegrationBean bean) {
		try{
			//set data on sourcing config.
			SourcingConfig srcConfigDataBean = new SMSourcingConfigurationBeanDataProcessor()
					.setDataOnSourceConfigurationBean(src, sourcingConfigBean, psl, bean);
			if(null !=srcConfigDataBean ){
				return srcConfigDataBean;
			}else{
				return null;
			}
		}catch(WTException wexp){
			LOGGER.error(wexp.getLocalizedMessage(), wexp);
			return null;
		} catch (javax.xml.datatype.DatatypeConfigurationException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return null;
		}
	}

	/**
	 * Setting data on cost Sheet bean.
	 * @param costSheet
	 * @param productCostSheetBean
	 * @param src
	 */
	public ProductCostSheet setDataOnProductCostSheet(LCSProductCostSheet costSheet,
			ProductCostSheet productCostSheetBean, SMProductOutboundIntegrationBean bean) {
		try{
			//validate cost sheet data.
			if(isCostSheetValidForIntegration(costSheet)){
				//set data on cost sheet bean.
				ProductCostSheet productCostSheetDataBean = new SMCostSheetBeanDataProcessor()
						.setDataOnCostsheetBean(costSheet, productCostSheetBean, bean);
				if(null != productCostSheetDataBean){
					return productCostSheetDataBean;
				}else{
					return null;
				}
			}else{
				return null;
			}
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage(), we);
			return null;
		} catch (javax.xml.datatype.DatatypeConfigurationException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return null;
		}
	}



	/**
	 * Return list of SKUs for a Product.
	 * @param product - LCSProduct.
	 * @return - ArrayList<LCSSKU>
	 */
	public List<LCSSKU> findColorwaysForProduct(LCSProduct product){
		try{
			HashMap<String,String> criteria = new HashMap<>();
			//getting product ID.
			String productRevId = FormatHelper.getNumericFromOid(FormatHelper
					.getVersionId(product));
			criteria.put("LCSPRODUCT.BRANCHIDITERATIONINFO", productRevId);
			//get SKUs.
			com.lcs.wc.db.SearchResults skuResults = new com.lcs.wc.product.LCSSKUQuery().findSKUsByCriteria(criteria,
					product.getFlexType(), null, null);

			LCSSKU colorway;
			List<LCSSKU> colorwayList = new ArrayList<>();
			Iterator<?> skuIter = skuResults.getResults().iterator();
			//iterating colorway list.
			while(skuIter.hasNext()){
				com.lcs.wc.db.FlexObject fo = (com.lcs.wc.db.FlexObject) skuIter.next();
				//getting sku object.
				colorway = (LCSSKU) com.lcs.wc.foundation.LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:" + fo.getData("LCSSKU.BRANCHIDITERATIONINFO"));
				//getting latest colorway iteration.
				colorway = (LCSSKU) com.lcs.wc.util.VersionHelper.latestIterationOf(colorway);
				//adding to list
				colorwayList.add(colorway);
			}
			return colorwayList;
		}catch(WTException exp){
			LOGGER.error(exp.getLocalizedMessage(), exp);
			return null;
		}
	}

	/**
	 * Returns list of all Product Season link of Product.
	 * @param product - LCSProduct.
	 * @return - ArrayList<LCSProductSeasonLink>
	 */
	public List<LCSProductSeasonLink> findProductSeasonLinksForProduct(LCSProduct product){
		try{
			//getting season collection.
			Collection<?> seasonCollection = new LCSSeasonQuery().findSeasons(product);
			LCSSeasonMaster seasonMaster;
			LCSSeason season;
			//get product season link.
			List<LCSProductSeasonLink> pslCollection = new ArrayList<>();

			if(null != seasonCollection && !seasonCollection.isEmpty()){
				for(Object seasonObj : seasonCollection){
					seasonMaster = (LCSSeasonMaster) seasonObj;
					season = com.lcs.wc.util.VersionHelper.latestIterationOf(seasonMaster);
					LOGGER.debug("Season Obj >>>>>>>  "+season.getName());

					LCSProductSeasonLink psl = (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(product, season);


					if (validObject(psl) && !psl.isSeasonRemoved()) {

						LOGGER.debug("IS PRODUCT REMOVED FROM SEASON ??  " + psl.isSeasonRemoved());
						pslCollection.add(psl);
					}
				}
			}
			return pslCollection;
		}catch(WTException wexp){
			LOGGER.error(wexp.getLocalizedMessage(), wexp);
			return null;
		}
	}

	/**
	 * Phase - 13 | added to check if LCSProductSeasonLink is null or not.
	 * 
	 * @param psl
	 * @return
	 */
	private boolean validObject(LCSProductSeasonLink psl) {
		boolean valid = false;

		if (psl != null) {
			valid = true;
		}

		return valid;
	}

	/**
	 * getting SKU Season links.
	 * @param sku
	 * @return
	 */
	public List<LCSSKUSeasonLink> findSKUSeasonLink(LCSSKU sku){
		try{
			//getting sku season collection.
			Collection<?> skuSeasonCollection = new LCSSeasonQuery().findSeasons(sku.getMaster());

			LCSSeasonMaster seasonMaster;
			LCSSeason season;

			List<LCSSKUSeasonLink> skuSeasonList = new ArrayList<>();

			if(null != skuSeasonCollection && !skuSeasonCollection.isEmpty()){
				for(Object seasonObj : skuSeasonCollection){
					seasonMaster = (LCSSeasonMaster) seasonObj;
					season = com.lcs.wc.util.VersionHelper.latestIterationOf(seasonMaster);

					//SKU Season Link.
					LCSSKUSeasonLink ssl = (LCSSKUSeasonLink) LCSSeasonQuery.findSeasonProductLink(sku, season);

					if(null != ssl && !ssl.isSeasonRemoved()){
						skuSeasonList.add(ssl);
					}
				}
			}
			return skuSeasonList;
		}catch(WTException wt){
			LOGGER.error(wt.getLocalizedMessage(), wt);
			return null;
		}
	}

	/**
	 * Checks if costSheet is valid for integration.
	 * @param costSheet - LCSProductCostSheet
	 * @return - boolean
	 * @throws WTException 
	 * removed validateCostSheetFPD() - as costing stage and life cycle validation removed on PHASE - 8 SEPD.
	 * now only validation check is - cost sheet satatus  = approved.
	 */
	public boolean isCostSheetValidForIntegration(LCSProductCostSheet costSheet) throws WTException{

		//updating for EHR 331 Phase-7
		//removed product season lifecycle state check
		//updated for PHASE -8 SEPD CHnges , removed costing stage check.

		//updated for 3.9.1 build
		String status = String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.COST_SHEET_STATUS));
		if("Apparel\\Accessories".equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			status = String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.ACC_COST_SHEET_STATUS));
		}
		
		//updated for Phase - 12, 3.12.0.0 build - start
		if("Footwear\\Multicurrency".equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			status = String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.MULTI_COST_SHEET_STATUS));
		}
		//Phase - 13 | added CS status attr for multi curency CS.
		if ("Apparel\\1. Multicurrency Accessories SEPD".equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			status = String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.ACC_COST_SHEET_STATUS));
		}
		//Phase 14 - APD MultiCurrency: START //
		if ("Apparel\\0. Multicurrency Apparel".equalsIgnoreCase(costSheet.getFlexType().getFullNameDisplay())) {
			status = String.valueOf(costSheet.getValue(SMProductOutboundWebServiceConstants.CS_MCAPP_COSTING_STATUS_KEY));
		}//Phase 14 - APD MultiCurrency: END //
		LOGGER.debug("Cost sheet status for type "+ costSheet.getFlexType().getFullNameDisplay() + " is ->>"+ status);
		//updated for Phase - 12, 3.12.0.0 build - end
		if (status.equalsIgnoreCase(SMProductOutboundWebServiceConstants.COSTSHEET_APPROVED)) {

			LOGGER.info("Cost Sheet Status is Approved");
			return true;

		}

		LOGGER.debug("Validation failed on Cost Sheet  !!!");
		return false;
	}

	/**
	 * Returns modifier email/name.
	 * @param supplier - Product
	 * @return String
	 */
	public String getProductModifier(LCSProduct product){
		if(FormatHelper.hasContent(product.getModifierEMail())){
			return product.getModifierEMail();
		}
		else{
			return product.getModifierFullName();
		}
	}
	/**
	 * Returns modifier email/name.
	 * @param supplier - Colorway
	 * @return String
	 */
	public String getColorwayModifier(LCSSKU sku){
		if(FormatHelper.hasContent(sku.getModifierEMail())){
			return sku.getModifierEMail();
		}
		else{
			return sku.getModifierFullName();
		}
	}

	/**
	 * Returns modifier email/name.
	 * @param supplier - Sourcing Config.
	 * @return String
	 */
	public String getSourcingConfigModifier(LCSSourcingConfig srcConfig){
		if(FormatHelper.hasContent(srcConfig.getModifierEMail())){
			return srcConfig.getModifierEMail();
		}
		else{
			return srcConfig.getModifierFullName();
		}
	}
	/**
	 * Returns modifier email/name.
	 * @param supplier - Product Cost Sheet
	 * @return String
	 */
	public String getCostSheetModifier(LCSCostSheet costSheet){
		if(FormatHelper.hasContent(costSheet.getModifierEMail())){
			return costSheet.getModifierEMail();
		}
		else{
			return costSheet.getModifierFullName();
		}
	}

	/**
	 * Returns creator email/name.
	 * @param supplier - LCSProduct
	 * @return String
	 */
	public String getProductCreator(LCSProduct product){
		if(FormatHelper.hasContent(product.getCreatorEMail())){
			return product.getCreatorEMail();
		}
		else{
			return product.getCreatorFullName();
		}
	}
	/**
	 * Returns creator email/name.
	 * @param supplier - LCSColorway
	 * @return String
	 */
	public String getColorwayCreator(LCSSKU sku){
		if(FormatHelper.hasContent(sku.getCreatorEMail())){
			return sku.getCreatorEMail();
		}
		else{
			return sku.getCreatorFullName();
		}
	}
	/**
	 * Returns creator email/name.
	 * @param supplier - LCSCostSheet
	 * @return String
	 */
	public String getCostSheetCreator(LCSCostSheet costSheet){
		if(FormatHelper.hasContent(costSheet.getCreatorEMail())){
			return costSheet.getCreatorEMail();
		}
		else{
			return costSheet.getCreatorFullName();
		}
	}
	/**
	 * Returns creator email/name.
	 * @param supplier - LCSSouringConfig
	 * @return String
	 */
	public String getSourcingConfigCreator(LCSSourcingConfig srcConfig){
		if(FormatHelper.hasContent(srcConfig.getCreatorEMail())){
			return srcConfig.getCreatorEMail();
		}
		else{
			return srcConfig.getCreatorFullName();
		}
	}

}
