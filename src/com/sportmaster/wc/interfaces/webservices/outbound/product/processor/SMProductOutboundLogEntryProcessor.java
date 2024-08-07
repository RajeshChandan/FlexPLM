/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.processor;

import java.util.Map;

import org.apache.log4j.Logger;

import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author ITC_Infotech
 * 
 */
public class SMProductOutboundLogEntryProcessor {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductOutboundLogEntryProcessor.class);
	/**
	 * Validation failure request Type.
	 */
	private String validateFailureRequestType = "WSDL_VALIDATION_FAILED";
	/**
	 * product class
	 */
	private String lcsproductClass = "VR:com.lcs.wc.product.LCSProduct:";
	/**
	 * constructor.
	 */
	public SMProductOutboundLogEntryProcessor(){
		//protected constructor.
	}

	/**
	 * Set log entry for product season.
	 * @param plmID -String.
	 * @param requestType - String
	 * @param psl - LCSProductSeasonLink.
	 * @throws WTException 
	 */
	public void setLogEntryForProductSeasonOutboundIntegration(String plmID, String requestType,
			LCSProductSeasonLink psl, SMProductOutboundIntegrationBean bean) throws WTException {
		int flag = 0;
		LCSLogEntry logEntry=bean.getLogentryUtil().getLogentry(plmID, psl);
		if(logEntry != null) {
			bean.getProductSeasonOutboundLogEntry().put(plmID,logEntry);
		}
		//iterating map.
		for(Map.Entry<String, LCSLogEntry> entry : bean.getProductSeasonOutboundLogEntry().entrySet()){
			if(FormatHelper.hasContent(entry.getKey()) && entry.getKey().equals(plmID)){
				//check for update.
				flag++;
				//update log entry.
				updateLogEntryForProductSeason(plmID, requestType, entry, psl, bean);
				break;
			}
		}
		if(flag == 0){
			//check for create.
			createLogEntryForProductSeason(plmID, requestType, psl, bean);
		}
	}

	/**
	 * Set log entry for product season.
	 * @param plmID -String.
	 * @param requestType - String
	 * @param psl - LCSProductSeasonLink.
	 * @throws WTException - WTException.
	 * @throws WTPropertyVetoException - WTPropertyVetoException.
	 */
	public void updateLogEntryForProductSeasonOutboundIntegrationMDMID(String mdmID, String requestType,
			LCSProductSeasonLink psl, SMProductOutboundIntegrationBean bean) throws WTException, WTPropertyVetoException {	
		
		LOGGER.info("req type >>>>   "+requestType);
		LOGGER.info("MDM ID  >>>>  "+mdmID);
		LOGGER.info("Request id ########   "+bean.getProductSeasonOutboundRequestID());
		LCSProduct product;
		LCSProductSeasonLink latestPSL;
		String tempMDMID=null;
		for(Map.Entry<String, LCSLogEntry> entry : bean.getProductSeasonOutboundLogEntry().entrySet()){
			tempMDMID=String.valueOf(entry.getValue().getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID)); 

			if(tempMDMID.equals(mdmID)){
				LOGGER.info("MDM ID MATCHED......");
				product = (LCSProduct) LCSQuery.findObjectById(lcsproductClass+(int)psl.getProductSeasonRevId());
				product = (LCSProduct) VersionHelper.latestIterationOf(product);
				latestPSL = (LCSProductSeasonLink) SeasonProductLocator.getSeasonProductLink(product);

				LOGGER.info("psl season removed....."+latestPSL.isSeasonRemoved());
				
				if(!latestPSL.isSeasonRemoved()){
					LOGGER.info("season is not removed.....");
					setLogEntryDataOnProductSeasonLinkExists(requestType,
							psl, product, entry, bean);
				}else{
					entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS,
							SMProductOutboundWebServiceConstants.LOG_ENTRY_OBJECT_MISSING);
				}
			}
		}
	}

	/**
	 * @param requestType
	 * @param psl
	 * @param product
	 * @param entry
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void setLogEntryDataOnProductSeasonLinkExists(String requestType, LCSProductSeasonLink psl,
			LCSProduct product, Map.Entry<String, LCSLogEntry> entry, SMProductOutboundIntegrationBean bean)
			throws WTException, WTPropertyVetoException {
		
		LOGGER.info("constructing log entry data for request Type:-"+requestType);
		
		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_REQUEST_ID, String.valueOf(bean.getProductSeasonOutboundRequestID()));

		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID));
		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, product.getLifeCycleState().toString());
		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
		if(null != bean.getProdHelper().getProductSeasonLinkDetails(psl)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_OBJECT_DETAILS, bean.getProdHelper().getProductSeasonLinkDetails(psl));
		}
		//update failed
		if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.UPDATE_FAILED)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_UPDATE_PENDING);
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
			//update processed.
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.UPDATE_SUCCESS)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_UPDATE_PROCESSED);
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
		}
		bean.getProdHelper().persistLogEntryObject(entry.getValue());
	}

	/**
	 * Set LogEntry for Colorway Season Outbound Integration.
	 * @param plmID
	 */
	public void setLogEntryForColorwaySeasonOutboundIntegration(String plmID, String requestType, LCSSKUSeasonLink ssl,
			SMProductOutboundIntegrationBean bean) {
		
		int flag = 0;
		LCSLogEntry logEntry=bean.getLogentryUtil().getLogentry(plmID, ssl);
		if(logEntry != null) {
			bean.getColorwaySeasonOutboundLogEntry().put(plmID,logEntry);
		}
		//iterating map.
		for(Map.Entry<String, LCSLogEntry> entry : bean.getColorwaySeasonOutboundLogEntry().entrySet()){
			if(entry.getKey().equals(plmID)){
				flag++;

				//update log entry.
				updateLogEntry( requestType, entry, ssl, bean);
				break;
			}
		}
		if(flag == 0){

			//create log entry.
			createLogEntry(requestType, ssl, bean);
		}
	}


	/**
	 * Set LogEntry for Product Outbound Integration.
	 * @param plmID - String
	 */
	public void setLogEntryForProductOutboundIntegration(String plmID, String requestType, LCSProduct prod,
			SMProductOutboundIntegrationBean bean) {
		
		int flag = 0;
		LCSLogEntry logEntry=bean.getLogentryUtil().getLogentry(plmID, prod);
		if(logEntry != null) {
			bean.getProductOutboundLogEntry().put(plmID,logEntry);
		}

		for(Map.Entry<String, LCSLogEntry> entry : bean.getProductOutboundLogEntry().entrySet()){

			if(entry.getKey().equals(plmID)){
				flag++;
				//update log entry.
				updateLogEntry(requestType, entry, prod, bean);
				break;
			}
		}
		if(flag == 0){
			//create log entry.
			createLogEntry(requestType, prod, bean);
		}
	}
	/**
	 * Set LogEntry for Colorway Outbound Integration.
	 * @param plmID
	 */
	public void setLogEntryForColorwayOutboundIntegration(String plmID, String requestType, LCSSKU sku,
			SMProductOutboundIntegrationBean bean) {
		int flag = 0;
		LCSLogEntry logEntry=bean.getLogentryUtil().getLogentry(plmID.trim(), sku);
		if(logEntry != null) {
			bean.getColorwayOutboundLogEntry().put(plmID.trim(),logEntry);
		}

		//iterating map.
		for(Map.Entry<String, LCSLogEntry> entry : bean.getColorwayOutboundLogEntry().entrySet()){
			if(entry.getKey().equals(plmID)){
				flag++;
				//update log entry.
				updateLogEntry(requestType, entry, sku, bean);
				break;
			}
		}
		if(flag == 0){
			//create log entry.
			createLogEntry(requestType, sku, bean);
		}
	}


	/**
	 * create log entry object for product, colorway, colorway Season.
	 * @param plmID 
	 * @param requestType
	 * @param obj
	 */
	public void createLogEntry(String requestType, Object obj, SMProductOutboundIntegrationBean bean){
		try{
			//create log entry for SKU Season.
			if(obj instanceof LCSSKUSeasonLink){
				createLogEntryForSKUSeasonLink(requestType, obj, bean);

				//create request for LCSSKU
			}else if(obj instanceof LCSSKU){
				createLogEntryForColorway(requestType, obj, bean);

				//create request for LCSProduct.
			}else if(obj instanceof LCSProduct){
				createLogEntryForProduct(requestType, obj, bean);
			}
		}catch(WTException wexption){
			LOGGER.error(wexption.getLocalizedMessage(),wexption);
		}catch(WTPropertyVetoException pvExp){
			LOGGER.error(pvExp.getLocalizedMessage(),pvExp);
		}
	}

	/**
	 * @param requestType
	 * @param obj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void createLogEntryForProduct(String requestType, Object obj, SMProductOutboundIntegrationBean bean)
			throws WTException, WTPropertyVetoException {
		LOGGER.info("Request Type  *******  "+requestType);
		LCSProduct product = (LCSProduct) obj;
		LCSProduct prodFirstVersion = (LCSProduct) VersionHelper.getFirstVersion(product);
		prodFirstVersion = (LCSProduct) VersionHelper.latestIterationOf(prodFirstVersion);
		product = (LCSProduct) VersionHelper.latestIterationOf(product);
		LOGGER.info("Product Branch ID in Log entry while CREATE  >>>>>>>>>>     "+prodFirstVersion.getBranchIdentifier());

		LOGGER.info("Creating Log Entry for product  >>>>  "+product.getName());
		com.lcs.wc.flextype.FlexType productOutboundLogEntryType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMProductOutboundWebServiceConstants.LOG_ENTRY_PRODUCT_OUT_BOUND_PATH);
		LCSLogEntry productLogEntryObj = LCSLogEntry.newLCSLogEntry();
		productLogEntryObj.setFlexType(productOutboundLogEntryType);

		productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_REQUEST_ID, String.valueOf(bean.getProductOutboundRequestID()));
		productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID, String.valueOf(prodFirstVersion.getBranchIdentifier()));
		productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, product.getLifeCycleState().toString());
		productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
		productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_OBJECT_DETAILS, product.getName());

		if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_SUCCESS_REQUEST)){
			productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PROCESSED);
			LOGGER.info("Product MDM ID  >>>   "+product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY));
			productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY));
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_FAILED_REQUEST)){
			productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PENDING);
			productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
			productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, "");
		}else if(requestType.equalsIgnoreCase(validateFailureRequestType)){
			productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PENDING);
			productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getProductValidationErrorMessage());
			productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, "");
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CANCELLED_REQUEST)){
			LOGGER.info("Cancelled Pending   !!!!!!!!!!!!");
			productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PENDING);
			productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
			productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY));

		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CANCELLED_SUCCESS)){
			LOGGER.info("Cancelled Success !!!!!!!!!!");
			productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY));
			productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PROCESSED);
			productLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
		}

		bean.getProdHelper().persistLogEntryObject(productLogEntryObj);

		bean.getProductOutboundLogEntry().put(productLogEntryObj.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID).toString(), productLogEntryObj);
		bean.setProductOutboundLogEntry(bean.getProductOutboundLogEntry());
	}

	/**
	 * @param requestType
	 * @param obj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void createLogEntryForColorway(String requestType, Object obj, SMProductOutboundIntegrationBean bean)
			throws WTException, WTPropertyVetoException {
		LCSSKU sku = (LCSSKU) obj;
		sku = (LCSSKU) VersionHelper.latestIterationOf(sku);
		LCSSKU skuFirstVersion = (LCSSKU) VersionHelper.getFirstVersion(sku);
		skuFirstVersion = (LCSSKU) VersionHelper.latestIterationOf(skuFirstVersion);
		LCSProduct product = (LCSProduct) LCSQuery.findObjectById(lcsproductClass+(int)sku.getProductARevId());
		product = (LCSProduct) VersionHelper.latestIterationOf(product);

		String skuDetails = sku.getName()+" , "+product.getName();

		LOGGER.info("SKU Branch ID in log Entry CREATE  >>>>>>>>>>     "+skuFirstVersion.getBranchIdentifier());

		com.lcs.wc.flextype.FlexType colorwayOutboundLogEntryType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMProductOutboundWebServiceConstants.LOG_ENTRY_COLORWAY_OUTBOUND_PATH);
		LCSLogEntry colorwayLogEntryObj = LCSLogEntry.newLCSLogEntry();
		colorwayLogEntryObj.setFlexType(colorwayOutboundLogEntryType);

		colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_REQUEST_ID, String.valueOf(bean.getColorwayOutboundRequestID()));
		colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID, String.valueOf(skuFirstVersion.getBranchIdentifier()));
		colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, sku.getLifeCycleState().toString());
		colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID,"");
		colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
		colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_OBJECT_DETAILS, skuDetails);

		if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_SUCCESS_REQUEST)){
			colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PROCESSED);
			colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID));
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_FAILED_REQUEST)){
			colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PENDING);
			colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
			colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, "");
		}else if(requestType.equalsIgnoreCase(validateFailureRequestType)){
			colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PENDING);
			colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getColorwayWSDLValidationErrorMessage());
			colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, "");
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CANCELLED_REQUEST)){
			colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PENDING);
			colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
			colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID));
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CANCELLED_SUCCESS)){
			colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PROCESSED);
			colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
			colorwayLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID));
		}
		//persist log entry.
		bean.getProdHelper().persistLogEntryObject(colorwayLogEntryObj);

		bean.getColorwayOutboundLogEntry().put(colorwayLogEntryObj.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID).toString(), colorwayLogEntryObj);
		bean.setColorwayOutboundLogEntry(bean.getColorwayOutboundLogEntry());
	}

	/**
	 * @param requestType
	 * @param obj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void createLogEntryForSKUSeasonLink(String requestType, Object obj, SMProductOutboundIntegrationBean bean)
			throws WTException, WTPropertyVetoException {
		LCSSKUSeasonLink ssl = (LCSSKUSeasonLink) obj;

		com.lcs.wc.flextype.FlexType skuSeasonOutboundLogEntryType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMProductOutboundWebServiceConstants.LOG_ENTRY_COLORWAY_SEASON_OUTBOUND_PATH);
		LCSLogEntry skuSeasonLogEntryObj = LCSLogEntry.newLCSLogEntry();
		skuSeasonLogEntryObj.setFlexType(skuSeasonOutboundLogEntryType);

		LCSSKU sku = (LCSSKU) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:"+(int)ssl.getSkuSeasonRevId());
		sku = (LCSSKU) VersionHelper.latestIterationOf(sku);

		skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_REQUEST_ID, String.valueOf(bean.getColorwaySeasonOutboundRequestID()));
		skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID, bean.getProdHelper().getColorwayMasterReferenceFromLink(ssl));
		skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, sku.getLifeCycleState().toString());
		skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
		if(null != bean.getProdHelper().getColorwaySeasonLinkDetails(ssl)){
			skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_OBJECT_DETAILS, bean.getProdHelper().getColorwaySeasonLinkDetails(ssl));
		}

		if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_SUCCESS_REQUEST)){
			skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PROCESSED);
			skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_MDM_ID));
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_FAILED_REQUEST)){
			skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PENDING);
			skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
			skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, "");
		}else if(requestType.equalsIgnoreCase(validateFailureRequestType)){
			skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PENDING);
			skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getColorwaySeasonWSDLValidationErrorMessage());
			skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, "");
		}
		//checking request type cancelled success or not 
		else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CANCELLED_SUCCESS)){
			skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PROCESSED);
			skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID,ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_MDM_ID));
			skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CANCELLED_REQUEST)){
			skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PENDING);
			skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
			skuSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_MDM_ID));

		}

		bean.getProdHelper().persistLogEntryObject(skuSeasonLogEntryObj);
		bean.getColorwaySeasonOutboundLogEntry().put(skuSeasonLogEntryObj.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID).toString(), skuSeasonLogEntryObj);
		bean.setColorwaySeasonOutboundLogEntry(bean.getColorwaySeasonOutboundLogEntry());
	}


	/**
	 * Update existing log entry for product, colorway, product season.
	 * @param plmID
	 * @param requestType
	 * @param obj
	 */
	public void updateLogEntry(String requestType, Map.Entry<String, LCSLogEntry> entry, Object obj,
			SMProductOutboundIntegrationBean bean) {
		try{
			if(obj instanceof LCSSKUSeasonLink){
				updateLogEntryForColorwaySeasonLink(requestType, entry, obj, bean);

			}else if(obj instanceof LCSSKU){
				updateLogEntryForColorway(requestType, entry, obj, bean);

			}else if(obj instanceof LCSProduct){
				updateLogEntryForProduct(requestType, entry, obj, bean);
			}
			bean.getProdHelper().persistLogEntryObject(entry.getValue());
		}catch(WTException w){
			LOGGER.error(w.getLocalizedMessage(),w);
		}catch(WTPropertyVetoException pv){
			LOGGER.error(pv.getLocalizedMessage(), pv);
		}
	}

	/**
	 * @param requestType
	 * @param entry
	 * @param obj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void updateLogEntryForProduct(String requestType, Map.Entry<String, LCSLogEntry> entry, Object obj,
			SMProductOutboundIntegrationBean bean) throws WTException, WTPropertyVetoException {
		LCSProduct product = (LCSProduct) obj;
		product = (LCSProduct) VersionHelper.latestIterationOf(product);
		LOGGER.info("Request Type  ***   >>>> "+requestType);
		LOGGER.info("Updating Log entry for product  >>>    "+product.getName());


		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_REQUEST_ID, String.valueOf(bean.getProductOutboundRequestID()));
		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, product.getLifeCycleState().toString());
		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, product.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY));
		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_OBJECT_DETAILS, product.getName());
		if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CANCELLED_REQUEST)){
			LOGGER.info("Cancelled Pending   !!!!!!!!!!!!");
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PENDING);
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CANCELLED_SUCCESS)){
			LOGGER.info("Cancelled Success !!!!!!!!!!");
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PROCESSED);
		}else if(requestType.equalsIgnoreCase(validateFailureRequestType)){
			LOGGER.info("Validation failure reported !!!!");
			if(FormatHelper.hasContent((String) entry.getValue().getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID))){
				LOGGER.info("setting log entry status ----------");
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PENDING);
			}else{
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PENDING);
			}
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getProductValidationErrorMessage());
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_SUCCESS_REQUEST)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PROCESSED);
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_FAILED_REQUEST)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PENDING);
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
		}
	}

	/**
	 * @param requestType
	 * @param entry
	 * @param obj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void updateLogEntryForColorway(String requestType, Map.Entry<String, LCSLogEntry> entry, Object obj,
			SMProductOutboundIntegrationBean bean) throws WTException, WTPropertyVetoException {
		LOGGER.info("Request Type ############  "+requestType);
		LCSSKU sku = (LCSSKU) obj;
		sku = (LCSSKU) VersionHelper.latestIterationOf(sku);
		LCSProduct product = (LCSProduct) LCSQuery.findObjectById(lcsproductClass+(int)sku.getProductARevId());
		product = (LCSProduct) VersionHelper.latestIterationOf(product);

		String skuDetails = sku.getName()+" , "+product.getName();

		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_REQUEST_ID, String.valueOf(bean.getColorwayOutboundRequestID()));
		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, sku.getLifeCycleState().toString());
		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID));
		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_OBJECT_DETAILS,skuDetails);
		if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CANCELLED_REQUEST)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PENDING);
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CANCELLED_SUCCESS)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PROCESSED);
		}else if(requestType.equalsIgnoreCase(validateFailureRequestType)){
			if(FormatHelper.hasContent((String) entry.getValue().getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID))){
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PENDING);
			}else{
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PENDING);
			}
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getColorwayWSDLValidationErrorMessage());
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_SUCCESS_REQUEST)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PROCESSED);
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_FAILED_REQUEST)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PENDING);
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
		}
	}

	/**
	 * @param requestType
	 * @param entry
	 * @param obj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void updateLogEntryForColorwaySeasonLink(String requestType, Map.Entry<String, LCSLogEntry> entry,
			Object obj, SMProductOutboundIntegrationBean bean) throws WTException, WTPropertyVetoException {


		LCSSKUSeasonLink ssl = (LCSSKUSeasonLink) obj;
		LCSSKU sku = (LCSSKU) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:"+(int)ssl.getSkuSeasonRevId());
		sku = (LCSSKU) VersionHelper.latestIterationOf(sku);

		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_REQUEST_ID, String.valueOf(bean.getColorwaySeasonOutboundRequestID()));
		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, sku.getLifeCycleState().toString());
		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
		entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_MDM_ID));

		if(FormatHelper.hasContent(String.valueOf(ssl.getValue(SMProductOutboundWebServiceConstants.AD_HOC_PLM_ID_COLORWAY_SEASON_LINK)))){
			LOGGER.info("Setting AD HOC PLM ID SSL on SKU Season Link Log Entry !!!!!!!!!!!");
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID, String.valueOf(ssl.getValue(SMProductOutboundWebServiceConstants.AD_HOC_PLM_ID_COLORWAY_SEASON_LINK)));
		}else{
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID, bean.getProdHelper().getColorwayMasterReferenceFromLink(ssl));
		}

		if(null != bean.getProdHelper().getColorwaySeasonLinkDetails(ssl)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_OBJECT_DETAILS, bean.getProdHelper().getColorwaySeasonLinkDetails(ssl));
		}
		if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CANCELLED_REQUEST)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PENDING);
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CANCELLED_SUCCESS)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PROCESSED);
		}else if(requestType.equalsIgnoreCase(validateFailureRequestType)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PENDING);
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getColorwaySeasonWSDLValidationErrorMessage());
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_SUCCESS_REQUEST)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PROCESSED);
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
		}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_FAILED_REQUEST)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PENDING);
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
		}
	}


	/**
	 * Create new log entry for product season.
	 * @param plmID - String
	 * @param requestType - String
	 */
	public void createLogEntryForProductSeason(String plmID, String requestType, LCSProductSeasonLink psl,
			SMProductOutboundIntegrationBean bean) {
		LOGGER.info("Creating log entry for product season link");
		LOGGER.info("Request Type  >>>>>>>>>    "+requestType);
		try{
			com.lcs.wc.flextype.FlexType prodSeasonOutboundLogEntryType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMProductOutboundWebServiceConstants.LOG_ENTRY_PRODUCT_SEASON_OUTBOUND_PATH);
			LCSLogEntry prodSeasonLogEntryObj = LCSLogEntry.newLCSLogEntry();
			prodSeasonLogEntryObj.setFlexType(prodSeasonOutboundLogEntryType);

			LCSProduct prodSeasonRev = (LCSProduct) LCSQuery.findObjectById(lcsproductClass+(int)psl.getProductSeasonRevId());
			prodSeasonRev = (LCSProduct) VersionHelper.latestIterationOf(prodSeasonRev);

			prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_REQUEST_ID, String.valueOf(bean.getProductSeasonOutboundRequestID()));
			prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID, plmID);
			if(null != bean.getProdHelper().getProductSeasonLinkDetails(psl)){
				prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_OBJECT_DETAILS, bean.getProdHelper().getProductSeasonLinkDetails(psl));
			}
			prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, prodSeasonRev.getLifeCycleState().toString());
			prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");

			if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_FAILED_REQUEST)){
				prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, "");
				prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
				prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PENDING);
			}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_SUCCESS_REQUEST)){
				prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID));
				prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
				prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PROCESSED);
			}else if(requestType.equalsIgnoreCase(validateFailureRequestType)){
				prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, "");
				prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getProductSeasonValidationErrorMessage());
				prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PENDING);
			}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.LIFECYCLE_UPDATE_REQUEST)){

				prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID));
				prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_UPDATE_PENDING);
				String currentState = prodSeasonLogEntryObj.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES).toString();

				prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, currentState);
				prodSeasonLogEntryObj.setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");

			}
			bean.getProdHelper().persistLogEntryObject(prodSeasonLogEntryObj);
			LOGGER.debug("Log Entry PLM ID >>>" +prodSeasonLogEntryObj.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID));
			LOGGER.debug("Product season log entry object >>>" +prodSeasonLogEntryObj);
			bean.getProductSeasonOutboundLogEntry().put(String.valueOf(prodSeasonLogEntryObj.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID)), prodSeasonLogEntryObj);
			bean.setProductSeasonOutboundLogEntry(bean.getProductSeasonOutboundLogEntry());
		}catch(WTException weExp){
			LOGGER.error(weExp.getLocalizedMessage(),weExp);
		}catch(WTPropertyVetoException propExp){
			LOGGER.error(propExp.getLocalizedMessage(),propExp);
		}
	}

	/**
	 * Update Existing log entry for product season.
	 * @param plmID - String
	 * @param requestType - String
	 * @throws WTException 
	 */
	public void updateLogEntryForProductSeason(String plmID, String requestType, Map.Entry<String, LCSLogEntry> entry,
			LCSProductSeasonLink psl, SMProductOutboundIntegrationBean bean) throws WTException {
		LOGGER.info("Updating existing log Entry for product Season with plm ID :::::::::::::::::");
		LOGGER.info("Request Type >>> "+requestType);
		String logEntryStatus = (String) entry.getValue().getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS);
		try{
			LCSProduct product = (LCSProduct) LCSQuery.findObjectById(lcsproductClass+(int)psl.getProductSeasonRevId());
			product = (LCSProduct) VersionHelper.latestIterationOf(product);

			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID));
			setLogEntryAttributesForPSLUpdate(plmID, entry, psl, product, bean);
			if(null != bean.getProdHelper().getProductSeasonLinkDetails(psl)){
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_OBJECT_DETAILS, bean.getProdHelper().getProductSeasonLinkDetails(psl));
			}
			if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.UPDATE_REQUEST)){
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_UPDATE_PENDING);
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, product.getLifeCycleState().toString());
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
			}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.UPDATE_FAILED)){
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_UPDATE_PENDING);
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, product.getLifeCycleState().toString());
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
			}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.UPDATE_SUCCESS)){
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_UPDATE_PROCESSED);
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, product.getLifeCycleState().toString());
			}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.LIFECYCLE_UPDATE_REQUEST)){
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_UPDATE_PENDING);
				String currentState = entry.getValue().getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES).toString();
				currentState = updateProductSeasonLogEntryOnLifeCycleUpdate(
						logEntryStatus, product, currentState);
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, currentState);
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
			}else if(requestType.equalsIgnoreCase(validateFailureRequestType)){
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, logEntryStatus);
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getProductSeasonValidationErrorMessage());
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, product.getLifeCycleState().toString());
			}else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_SUCCESS_REQUEST)){
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PROCESSED);
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, product.getLifeCycleState().toString());
			}
			//Checking request type is create failed or not
			else if(requestType.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CREATE_FAILED_REQUEST)){
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS, SMProductOutboundWebServiceConstants.LOG_ENTRY_CREATE_PENDING);
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, product.getLifeCycleState().toString());
			}

			bean.getProdHelper().persistLogEntryObject(entry.getValue());

		}catch(WTException wexp){
			LOGGER.error(wexp.getLocalizedMessage(),wexp);
		}catch(WTPropertyVetoException wpexp){
			LOGGER.error(wpexp.getLocalizedMessage(),wpexp);
		}

	}

	/**
	 * @param plmID
	 * @param entry
	 * @param psl
	 * @param product
	 * @throws WTException
	 */
	public void setLogEntryAttributesForPSLUpdate(String plmID, Map.Entry<String, LCSLogEntry> entry,
			LCSProductSeasonLink psl, LCSProduct product, SMProductOutboundIntegrationBean bean) throws WTException {
		if(FormatHelper.hasContent(plmID)){
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_REQUEST_ID, String.valueOf(bean.getProductSeasonOutboundRequestID()));
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID, plmID);
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES, product.getLifeCycleState().toString());
			entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_ERROR_REASON, "");
		}else{
			LOGGER.info("Setting PLM ID  ...........");
			if(FormatHelper.hasContent(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.AD_HOC_PLM_ID_PRODUCT_SEASON_LINK)))){
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID, String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.AD_HOC_PLM_ID_PRODUCT_SEASON_LINK)));
			}else{
				LOGGER.info("Setting Master Reference ......................");
				entry.getValue().setValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID, bean.getProdHelper().getProductMasterReferencefromLink(psl));
			}
		}
	}

	/**
	 * @param logEntryStatus
	 * @param product
	 * @param currentState
	 * @return
	 */
	public String updateProductSeasonLogEntryOnLifeCycleUpdate(
			String logEntryStatus, LCSProduct product, String currentState) {
		LOGGER.debug("current log entry lifecycle state ------  "+currentState);
		String currState;
		if(!SMProductOutboundWebServiceConstants.LOG_ENTRY_UPDATE_PENDING.equalsIgnoreCase(logEntryStatus)){
			currState = product.getLifeCycleState().toString();
		}else{
			currState = currentState+","+product.getLifeCycleState().toString();

		}
		return currState;
	}

}
