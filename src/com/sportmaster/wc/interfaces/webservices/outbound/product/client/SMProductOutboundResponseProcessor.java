/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.client;

import java.util.Date;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwayInformationUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductInformationUpdatesRequestResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationUpdatesRequestResponse;

/**
 * @author BSC.
 *
 */
public class SMProductOutboundResponseProcessor {

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductOutboundResponseProcessor.class);
	/**
	 * protected constructor.
	 */
	public SMProductOutboundResponseProcessor(){
		//constructor.
	}
	

	/**
	 * Process Colorway Season response.
	 * @param responseObj - Object.
	 * @param dataObject - Object.
	 * @throws WTException - WTException.
	 * @throws WTPropertyVetoException 
	 */
	public void processColorwaySeasonLinkOutboundResponse(Object responseObj, Object dataObject,
			boolean createFlag, SMProductOutboundIntegrationBean bean) throws WTException, WTPropertyVetoException {
		LOGGER.info("Processing colorway Season response  -----");
		ColorwaySeasonLinkInformationUpdatesRequestResponse skuSeasonResponse = (ColorwaySeasonLinkInformationUpdatesRequestResponse) responseObj;
		LCSSKUSeasonLink ssl = (LCSSKUSeasonLink) dataObject;

		//get SKU Season Link.
		LCSSKU skuSeasonRev = SeasonProductLocator.getSKUSeasonRev(ssl);
		skuSeasonRev = (LCSSKU) VersionHelper.latestIterationOf(skuSeasonRev);
		ssl = (LCSSKUSeasonLink) SeasonProductLocator.getSeasonProductLink(skuSeasonRev);

		LOGGER.info("CREATE FLAG <COLORWAY SEASON LINK>   *******************    "+createFlag);
		//on create.
		if(createFlag){
			if(skuSeasonResponse.isIntegrationStatus() && FormatHelper.hasContent(skuSeasonResponse.getMdmId()) && !skuSeasonResponse.getMdmId().equalsIgnoreCase(SMProductOutboundWebServiceConstants.FAKE_MDM_ID)){
				ssl.setValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_MDM_ID, skuSeasonResponse.getMdmId());
				// Phase 14 - EMP-481 - Start
				ssl.setValue(SMProductOutboundWebServiceConstants.SKUSEASON_TRIGGERED_BY, ssl.getCreator().getFullName());
				ssl.setValue(SMProductOutboundWebServiceConstants.SKUSEASON_TRIGGERED_ON, new Date());
				// Phase 14 - EMP-481 - End
				bean.getProdUtill().persistColorwaySeasonLinkObject(ssl);
				//set log entry.
				bean.getLogEntryProcessor().setLogEntryForColorwaySeasonOutboundIntegration(skuSeasonResponse.getPlmId(), SMProductOutboundWebServiceConstants.CREATE_SUCCESS_REQUEST, ssl, bean);
			}else if(!skuSeasonResponse.isIntegrationStatus() || !FormatHelper.hasContent(skuSeasonResponse.getMdmId()) ||skuSeasonResponse.getMdmId().equalsIgnoreCase(SMProductOutboundWebServiceConstants.FAKE_MDM_ID)){
				//also add check for Exception failure.
				//set log entry.
				// Phase 14 - EMP-481 - Start
				ssl.setValue(SMProductOutboundWebServiceConstants.SKUSEASON_TRIGGERED_BY, ssl.getCreator().getFullName());
				ssl.setValue(SMProductOutboundWebServiceConstants.SKUSEASON_TRIGGERED_ON, new Date());
				bean.getProdUtill().persistColorwaySeasonLinkObject(ssl);
				// Phase 14 - EMP-481 - End
				bean.getLogEntryProcessor().setLogEntryForColorwaySeasonOutboundIntegration(skuSeasonResponse.getPlmId(), SMProductOutboundWebServiceConstants.CREATE_FAILED_REQUEST, ssl, bean);
			}
			//on lifecycle update.
		}else{
			//validate response.
			if(skuSeasonResponse.isIntegrationStatus()){
				//set log entry.
				bean.getLogEntryProcessor().setLogEntryForColorwaySeasonOutboundIntegration(skuSeasonResponse.getPlmId(), SMProductOutboundWebServiceConstants.CANCELLED_SUCCESS, ssl, bean);
			}else if(!skuSeasonResponse.isIntegrationStatus()  ){
				//also add check for Exception failure
				//set log entry.
				bean.getLogEntryProcessor().setLogEntryForColorwaySeasonOutboundIntegration(skuSeasonResponse.getPlmId(), SMProductOutboundWebServiceConstants.CANCELLED_REQUEST, ssl, bean);
			}
			// Phase 14 - EMP-481 - Start
			ssl.setValue(SMProductOutboundWebServiceConstants.SKUSEASON_TRIGGERED_BY, ssl.getCreator().getFullName());
			ssl.setValue(SMProductOutboundWebServiceConstants.SKUSEASON_TRIGGERED_ON, new Date());
			bean.getProdUtill().persistColorwaySeasonLinkObject(ssl);
			// Phase 14 - EMP-481 - End
		}
	}

	/**
	 * Process Product Season response.
	 * @param responseObj - Object.
	 * @param dataObject - Object.
	 * @throws WTException - WTException.
	 * @throws WTPropertyVetoException 
	 */
	public void processProductSeasonLinkOutboundResponse(Object responseObj, Object dataObject,
			SMProductOutboundIntegrationBean bean) throws WTException, WTPropertyVetoException {
		LOGGER.info("Processing product Season response  -----");
		ProductSeasonLinkInformationUpdatesRequestResponse prodSeasonResponse = (ProductSeasonLinkInformationUpdatesRequestResponse) responseObj;

		LCSProductSeasonLink psl = (LCSProductSeasonLink) dataObject;
		LCSProduct prodSeasonRev = SeasonProductLocator.getProductSeasonRev(psl);
		prodSeasonRev = (LCSProduct) VersionHelper.latestIterationOf(prodSeasonRev);
		psl = (LCSProductSeasonLink) SeasonProductLocator.getSeasonProductLink(prodSeasonRev);

		//on create.
		if(bean.isCreate()){
			//validate response.
			if(prodSeasonResponse.isIntegrationStatus() && FormatHelper.hasContent(prodSeasonResponse.getMdmId()) && !prodSeasonResponse.getMdmId().equalsIgnoreCase(SMProductOutboundWebServiceConstants.FAKE_MDM_ID)){
				psl.setValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID, prodSeasonResponse.getMdmId());
				// Phase 14 - EMP-481 - Start
				psl.setValue(SMProductOutboundWebServiceConstants.PRODSEASON_TRIGGERED_BY, psl.getCreator().getFullName());
				psl.setValue(SMProductOutboundWebServiceConstants.PRODSEASON_TRIGGERED_ON, new Date());
				// Phase 14 - EMP-481 - End
				bean.getProdUtill().persistProductSeasonLinkObject(psl);
				//set log entry.
				bean.getLogEntryProcessor().setLogEntryForProductSeasonOutboundIntegration(
						prodSeasonResponse.getPlmId(), SMProductOutboundWebServiceConstants.CREATE_SUCCESS_REQUEST, psl,
						bean);
			}else if(!prodSeasonResponse.isIntegrationStatus() || !FormatHelper.hasContent(prodSeasonResponse.getMdmId()) || prodSeasonResponse.getMdmId().equalsIgnoreCase(SMProductOutboundWebServiceConstants.FAKE_MDM_ID)  ){
				//also add check for Exception failure.
				// Phase 14 - EMP-481 - Start
				psl.setValue(SMProductOutboundWebServiceConstants.PRODSEASON_TRIGGERED_BY, psl.getCreator().getFullName());
				psl.setValue(SMProductOutboundWebServiceConstants.PRODSEASON_TRIGGERED_ON, new Date());
				bean.getProdUtill().persistProductSeasonLinkObject(psl);
				// Phase 14 - EMP-481 - End

				//set log entry.
				bean.getLogEntryProcessor().setLogEntryForProductSeasonOutboundIntegration(
						prodSeasonResponse.getPlmId(), SMProductOutboundWebServiceConstants.CREATE_FAILED_REQUEST, psl,
						bean);
			}
			//on LC state change.
		}	
	}

	/**
	 * Process Colorway response.
	 * @param responseObj - Object.
	 * @param dataObject - Object.
	 * @throws WTException - WTException.
	 * @throws WTPropertyVetoException 
	 */
	public void processColorwayOutboundResponse(Object responseObj, Object dataObject, boolean createFlag,
			SMProductOutboundIntegrationBean bean) throws WTException, WTPropertyVetoException {
		LOGGER.info("Processing colorway response  -----");
		ColorwayInformationUpdatesRequestResponse skuResponse = (ColorwayInformationUpdatesRequestResponse) responseObj;
		LCSSKU sku = (LCSSKU) dataObject;
		sku = (LCSSKU) VersionHelper.getVersion(sku, "A");

		LOGGER.info("CREATE FLAG <SKU>    *******************    "+createFlag);
		//on create of Object.
		if(createFlag){
			//validate response.
			if(skuResponse.isIntegrationStatus() && FormatHelper.hasContent(skuResponse.getMdmId()) && !skuResponse.getMdmId().equalsIgnoreCase(SMProductOutboundWebServiceConstants.FAKE_MDM_ID)){
				sku.setValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID, skuResponse.getMdmId());
				// Phase 14 - EMP-481 - Start
				//sku.setValue(SMProductOutboundWebServiceConstants.SKU_TRIGGERED_BY, bean.getProdProcessor().getColorwayModifier(sku));
				sku.setValue(SMProductOutboundWebServiceConstants.SKU_TRIGGERED_BY, sku.getModifierFullName());
				sku.setValue(SMProductOutboundWebServiceConstants.SKU_TRIGGERED_ON, new Date());
				// Phase 14 - EMP-481 - End
				bean.getProdUtill().persistColorwayObject(sku);
				//set log entry.
				bean.getLogEntryProcessor().setLogEntryForColorwayOutboundIntegration(skuResponse.getPlmId(), SMProductOutboundWebServiceConstants.CREATE_SUCCESS_REQUEST, sku, bean);
			}else if(!skuResponse.isIntegrationStatus() || skuResponse.getMdmId().equalsIgnoreCase(SMProductOutboundWebServiceConstants.FAKE_MDM_ID) || !FormatHelper.hasContent(skuResponse.getMdmId())){
				//also add check for Exception failure.
				// Phase 14 - EMP-481 - Start
				//sku.setValue(SMProductOutboundWebServiceConstants.SKU_TRIGGERED_BY, bean.getProdProcessor().getColorwayModifier(sku));
				sku.setValue(SMProductOutboundWebServiceConstants.SKU_TRIGGERED_BY, sku.getModifierFullName());
				sku.setValue(SMProductOutboundWebServiceConstants.SKU_TRIGGERED_ON, new Date());
				bean.getProdUtill().persistColorwayObject(sku);
				// Phase 14 - EMP-481 - End
				bean.getLogEntryProcessor().setLogEntryForColorwayOutboundIntegration(skuResponse.getPlmId(), SMProductOutboundWebServiceConstants.CREATE_FAILED_REQUEST, sku, bean);
			}
		}else{
			//validate object.
			if(skuResponse.isIntegrationStatus()){
				bean.getLogEntryProcessor().setLogEntryForColorwayOutboundIntegration(skuResponse.getPlmId(), SMProductOutboundWebServiceConstants.CANCELLED_SUCCESS, sku, bean);
			}else if(!skuResponse.isIntegrationStatus()  ){
				//also add check for Exception failure
				//set log entry.
				bean.getLogEntryProcessor().setLogEntryForColorwayOutboundIntegration(skuResponse.getPlmId(), SMProductOutboundWebServiceConstants.CANCELLED_REQUEST, sku, bean);
			}
			// Phase 14 - EMP-481 - Start
			//sku.setValue(SMProductOutboundWebServiceConstants.SKU_TRIGGERED_BY, bean.getProdProcessor().getColorwayModifier(sku));
			sku.setValue(SMProductOutboundWebServiceConstants.SKU_TRIGGERED_BY, sku.getModifierFullName());
			sku.setValue(SMProductOutboundWebServiceConstants.SKU_TRIGGERED_ON, new Date());
			bean.getProdUtill().persistColorwayObject(sku);
			// Phase 14 - EMP-481 - End
		}
	}

	/**
	 * Process Product response.
	 * @param responseObj - Object.
	 * @param dataObject - Object.
	 * @throws WTException - WTException.
	 * @throws WTPropertyVetoException - WTPropertyVetoException.
	 */
	public void processProductOutboundResponse(Object responseObj, Object dataObject, boolean updatePSL,
			boolean createFlag, SMProductOutboundIntegrationBean bean)
			throws WTException, WTPropertyVetoException {
		LOGGER.info("Processing product response  -----");
		//product response.
		ProductInformationUpdatesRequestResponse prodResponse = (ProductInformationUpdatesRequestResponse) responseObj; 
		LCSProduct prod = (LCSProduct) dataObject;
		prod = (LCSProduct) VersionHelper.getVersion(prod, "A");
		LOGGER.info("PRODUCT ID ^^^^^^^^^^^^^^^^^^^^^^^     "+FormatHelper.getNumericObjectIdFromObject(prod));

		LOGGER.info("CREATE FLAG <PRODUCT>     ***************************    "+createFlag);
		//during 1st time create.
		if(createFlag){
			//validate response.
			validateProductResponseOnCreate(prodResponse, prod,bean);
			//during LC state change.	
		}else{
			if(!updatePSL){
				validateProductResponseOnUpdate(prodResponse, prod,bean);
				// Phase 14 - EMP-481 - Start
				prod.setValue(SMProductOutboundWebServiceConstants.PRODUCT_TRIGGERED_BY, prod.getModifierFullName());
				prod.setValue(SMProductOutboundWebServiceConstants.PRODUCT_TRIGGERED_ON, new Date());
				bean.getProdUtill().persistProductObject(prod);
				// Phase 14 - EMP-481 - End
			}else{
				//process product outbound response.
				processProductOutboundResponseFromProductResponse(prodResponse,bean);
			}
		}
	}

	/**
	 * @param prodResponse
	 * @param prod
	 * @throws WTException
	 */
	public void validateProductResponseOnUpdate(ProductInformationUpdatesRequestResponse prodResponse,
			LCSProduct prd, SMProductOutboundIntegrationBean bean) throws WTException {
		LCSProduct prod = prd;
		prod = (LCSProduct) VersionHelper.latestIterationOf(prod);
		//validate response.
		if(prodResponse.isIntegrationStatus()){
			//set log entry for update.
			bean.getLogEntryProcessor().setLogEntryForProductOutboundIntegration(prodResponse.getPlmId(),
					SMProductOutboundWebServiceConstants.CANCELLED_SUCCESS, prod, bean);
		}else if(!prodResponse.isIntegrationStatus()  ){
			//also add check for Exception failure.
			bean.getLogEntryProcessor().setLogEntryForProductOutboundIntegration(prodResponse.getPlmId(),
					SMProductOutboundWebServiceConstants.CANCELLED_REQUEST, prod, bean);
		}
	}

	/**
	 * @param prodResponse
	 * @param prod
	 * @throws WTPropertyVetoException 
	 * @throws WTException 
	 */
	public void validateProductResponseOnCreate(ProductInformationUpdatesRequestResponse prodResponse,
			LCSProduct prod, SMProductOutboundIntegrationBean bean) throws WTPropertyVetoException, WTException {
		
		if (FormatHelper.hasContent(prodResponse.getMdmId()) && prodResponse.isIntegrationStatus()
				&& !prodResponse.getMdmId().equalsIgnoreCase(SMProductOutboundWebServiceConstants.FAKE_MDM_ID)) {
			
			prod.setValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY, prodResponse.getMdmId());
			//save product.
			// Phase 14 - EMP-481 - Start
			//prod.setValue(SMProductOutboundWebServiceConstants.PRODUCT_TRIGGERED_BY, bean.getProdProcessor().getProductModifier(prod));
			prod.setValue(SMProductOutboundWebServiceConstants.PRODUCT_TRIGGERED_BY, prod.getModifierFullName());
			prod.setValue(SMProductOutboundWebServiceConstants.PRODUCT_TRIGGERED_ON, new Date());
			// Phase 14 - EMP-481 - End
			bean.getProdUtill().persistProductObject(prod);
			//set log entry for create for product.
			bean.getLogEntryProcessor().setLogEntryForProductOutboundIntegration(prodResponse.getPlmId(),
					SMProductOutboundWebServiceConstants.CREATE_SUCCESS_REQUEST, prod, bean);

		} else if (!prodResponse.isIntegrationStatus() || !FormatHelper.hasContent(prodResponse.getMdmId())
				|| prodResponse.getMdmId().equalsIgnoreCase(SMProductOutboundWebServiceConstants.FAKE_MDM_ID)) {
			
			//also add check for Exception failure.
			// Phase 14 - EMP-481 - Start
			//prod.setValue(SMProductOutboundWebServiceConstants.PRODUCT_TRIGGERED_BY, bean.getProdProcessor().getProductModifier(prod));
			prod.setValue(SMProductOutboundWebServiceConstants.PRODUCT_TRIGGERED_BY, prod.getModifierFullName());
			prod.setValue(SMProductOutboundWebServiceConstants.PRODUCT_TRIGGERED_ON, new Date());
			bean.getProdUtill().persistProductObject(prod);
			// Phase 14 - EMP-481 - End
			//set log entry for create for product.
			bean.getLogEntryProcessor().setLogEntryForProductOutboundIntegration(prodResponse.getPlmId(),
					SMProductOutboundWebServiceConstants.CREATE_FAILED_REQUEST, prod, bean);
		}
	}

	/**
	 * @param prodResponse
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void processProductOutboundResponseFromProductResponse(
			ProductInformationUpdatesRequestResponse prodResponse, SMProductOutboundIntegrationBean bean)
			throws WTException, WTPropertyVetoException {
		//setting request ID.
		bean.setProductSeasonOutboundRequestID(
				bean.getProdUtill().generateProductSeasonOutboundIntegrationRequestID());
		if(prodResponse.isIntegrationStatus()){
			//set log entry for update.
			bean.getLogEntryProcessor().updateLogEntryForProductSeasonOutboundIntegrationMDMID(
					bean.getProductSeasonLinkMDMID(),
					SMProductOutboundWebServiceConstants.UPDATE_SUCCESS,
					bean.getProductSeasonLink(), bean);
		}else if(!prodResponse.isIntegrationStatus()  ){
			//also add check for Exception failure.
			bean.getLogEntryProcessor().updateLogEntryForProductSeasonOutboundIntegrationMDMID(
					bean.getProductSeasonLinkMDMID(),
					SMProductOutboundWebServiceConstants.UPDATE_FAILED,
					bean.getProductSeasonLink(), bean);
		}
	}

}
