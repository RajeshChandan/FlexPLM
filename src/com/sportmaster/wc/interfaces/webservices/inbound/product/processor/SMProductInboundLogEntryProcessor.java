/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.inbound.product.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.product.client.SMProductInboundDataRequestWebClient;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundUtil;
import com.sportmaster.wc.interfaces.webservices.inbound.product.util.SMProductInboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonLinkInformationItem;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonLinkInformationItem;

/**
 * @author Carrier
 *
 */
public class SMProductInboundLogEntryProcessor {

	/**
	 * The LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMProductInboundLogEntryProcessor.class);
	/**
	 * Collection of Product Season Link Log Entry Objects.
	 */
	private static Map<String,LCSLogEntry> productSeasonLinkLogEntryCollection;
	/**
	 * Collection of SKU Season Link Log Entry Objects.
	 */
	private static Map<String, LCSLogEntry> skuSeasonLinkLogEntryCollection;

	/**
	 * Protected constructor.
	 */
	protected SMProductInboundLogEntryProcessor(){
		//protected constructor
	}

	/**
	 * Setting Log Entry for Product inbound.
	 * @param prodSeasonLink - LCSProductSeasonLink
	 * @param prodSeasonLinkInfoItem - ProductSeasonLinkInformationItem
	 */
	public static void setProductSeasonLinkInboundLogEntry(LCSProductSeasonLink prodSeasonLink, ProductSeasonLinkInformationItem prodSeasonLinkInfoItem){
		if(getProductSeasonLinkLogEntryCollection().containsKey(prodSeasonLinkInfoItem.getPlmId())){
			updateLogEntryProductSeasonLinkInbound(getProductSeasonLinkLogEntryCollection().get(prodSeasonLinkInfoItem.getPlmId()), prodSeasonLink, prodSeasonLinkInfoItem);
		}
		else{
			createLogEntryProductSeasonLinkInbound(prodSeasonLink, prodSeasonLinkInfoItem);
		}
	}

	/**
	 * Setting Log Entry for Colorway Inbound.
	 * @param skuSeasonLink - LCSSKUSeasonLink
	 * @param skuSeasonLinkInfoItem - ColorwaySeasonLinkInformationItem
	 */
	public static void setColorwaySeasonLinkInboundLogEntry(LCSSKUSeasonLink skuSeasonLink, ColorwaySeasonLinkInformationItem skuSeasonLinkInfoItem){
		if(getSkuSeasonLinkLogEntryCollection().containsKey(skuSeasonLinkInfoItem.getPlmId())){
			updateLogEntryColorwaySeasonLinkInbound(getSkuSeasonLinkLogEntryCollection().get(skuSeasonLinkInfoItem.getPlmId()), skuSeasonLink, skuSeasonLinkInfoItem);
		}
		else{
			createLogEntryColorwaySeasonLinkInbound(skuSeasonLink, skuSeasonLinkInfoItem);
		}
	}

	/**
	 * Creating Log Entry for Product Inbound.
	 * @param prodSeasonLink - LCSProductSeasonLink
	 * @param prodSeasonLinkInfoItem - ProductSeasonLinkInformationItem
	 */
	public static void createLogEntryProductSeasonLinkInbound(LCSProductSeasonLink prodSeasonLink, ProductSeasonLinkInformationItem prodSeasonLinkInfoItem){
		LOGGER.debug("Inside create Log Entry for Product Season Link");
		try{
			com.lcs.wc.flextype.FlexType productInboundCreateLogType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PATH);
			LCSLogEntry prodSeasonLinkLogEntry = LCSLogEntry.newLCSLogEntry();
			LOGGER.info("request id ------------->>  "+SMProductInboundDataRequestWebClient.getRequestID());
			//Setting attribute values
			prodSeasonLinkLogEntry.setFlexType(productInboundCreateLogType);
			if(null != prodSeasonLink){
				prodSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_REQUEST_ID, String.valueOf(SMProductInboundDataRequestWebClient.getRequestID()));
				prodSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_OBJECT_TYPE, "PRODUCT SEASON LINK");
				prodSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_MDM_ID, prodSeasonLink.getValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_MDMID));
				prodSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_DETAILS, SMProductInboundUtil.productSeasonLinkDetails(prodSeasonLink));
				prodSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PLM_ID, prodSeasonLinkInfoItem.getPlmId());
				prodSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_FEEDBACK_STATUS, SMProductInboundWebServiceConstants.PENDING);
				prodSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_ERROR_REASON, SMProductInboundDataRequestWebClient.getErrorMsg());
				//added for phase-8 SEPD chnages
				prodSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.ITERATION_ID, prodSeasonLinkInfoItem.getIterationId());
				if(!FormatHelper.hasContent(SMProductInboundDataRequestWebClient.getErrorMsg()) && !SMProductInboundDataRequestWebClient.isIntegartionFailurePSL()){
					LOGGER.info("INTEGARTION SUCCESS on PSL");
					prodSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_INTEGRATION_STATUS, SMProductInboundWebServiceConstants.INTEGRATED);
				}else if(SMProductInboundDataRequestWebClient.isIntegartionFailurePSL()){
					LOGGER.info("INTEGRATION FAILED on PSL");
					prodSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_INTEGRATION_STATUS, SMProductInboundWebServiceConstants.NOT_INTEGRATED);
				}
				//persist log Entry.
				SMProductInboundUtil.persistInboundLogEntry(prodSeasonLinkLogEntry);
				//Adding newly created log entry to existing collection.
				getProductSeasonLinkLogEntryCollection().put((String) prodSeasonLinkLogEntry.getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PLM_ID), prodSeasonLinkLogEntry);
			}else{
				LCSLogEntry prodSeasonLogEntryInvalidPLMID = LCSLogEntry.newLCSLogEntry();
				prodSeasonLogEntryInvalidPLMID.setFlexType(productInboundCreateLogType);
				prodSeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_REQUEST_ID, String.valueOf(SMProductInboundDataRequestWebClient.getRequestID()));
				prodSeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_OBJECT_TYPE, "PRODUCT SEASON LINK");
				prodSeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_MDM_ID, prodSeasonLinkInfoItem.getMdmId());
				prodSeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_DETAILS, "");
				prodSeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PLM_ID, prodSeasonLinkInfoItem.getPlmId());
				prodSeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_FEEDBACK_STATUS, SMProductInboundWebServiceConstants.PENDING);
				prodSeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_ERROR_REASON, "Product Season Link DOES NOT EXIST !!! INVALID PLM ID RECEIVED !!!");
				prodSeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_INTEGRATION_STATUS, SMProductInboundWebServiceConstants.NOT_INTEGRATED);
				//added for phase-8 SEPD chnages
				prodSeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.ITERATION_ID, prodSeasonLinkInfoItem.getIterationId());
				//persist log Entry.
				SMProductInboundUtil.persistInboundLogEntry(prodSeasonLogEntryInvalidPLMID);
				//Adding newly created log entry to existing collection.
				getProductSeasonLinkLogEntryCollection().put((String) prodSeasonLogEntryInvalidPLMID.getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PLM_ID), prodSeasonLogEntryInvalidPLMID);
			}
			SMProductInboundLogEntryProcessor.setProductSeasonLinkLogEntryCollection(getProductSeasonLinkLogEntryCollection());
		}catch(WTException wtExc){
			wtExc.printStackTrace();
		} catch (WTPropertyVetoException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Updates Existing Log Entry.
	 * @param productSeasonLinkLogEntry - LCSLogEntry
	 * @param prodSeasonLink - LCSProductSeasonLink
	 * @param prodSeasonLinkInfoItem - ProductSeasonLinkInformationItem
	 */
	public static void updateLogEntryProductSeasonLinkInbound(LCSLogEntry productSeasonLinkLogEntry, LCSProductSeasonLink prodSeasonLink, ProductSeasonLinkInformationItem prodSeasonLinkInfoItem){
		LOGGER.debug("Inside UPDATE Log Entry for Product Season Link");
		try{
			LOGGER.info("request id ------------->>>  "+SMProductInboundDataRequestWebClient.getRequestID());
			//Setting attribute Values.
			if(null != prodSeasonLink){
				productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_REQUEST_ID, String.valueOf(SMProductInboundDataRequestWebClient.getRequestID()));
				productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_MDM_ID, prodSeasonLink.getValue(SMProductInboundWebServiceConstants.PRODUCT_SEASON_LINK_MDMID));
				productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_DETAILS, SMProductInboundUtil.productSeasonLinkDetails(prodSeasonLink));
				productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PLM_ID, prodSeasonLinkInfoItem.getPlmId());
				productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_FEEDBACK_STATUS, SMProductInboundWebServiceConstants.PENDING);
				productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_ERROR_REASON, SMProductInboundDataRequestWebClient.getErrorMsg());
				//added for phase-8 SEPD chnages
				productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.ITERATION_ID, prodSeasonLinkInfoItem.getIterationId());
				if(!FormatHelper.hasContent(SMProductInboundDataRequestWebClient.getErrorMsg()) && !SMProductInboundDataRequestWebClient.isIntegartionFailurePSL()){
					LOGGER.info("INTEGARTION SUCCESS on PSL");
					productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_INTEGRATION_STATUS, SMProductInboundWebServiceConstants.INTEGRATED);
				}else if(SMProductInboundDataRequestWebClient.isIntegartionFailurePSL()){
					LOGGER.info("INTEGARTION FAILED on PSL");
					productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_INTEGRATION_STATUS, SMProductInboundWebServiceConstants.NOT_INTEGRATED);
				}
			}else{
				productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_REQUEST_ID, String.valueOf(SMProductInboundDataRequestWebClient.getRequestID()));
				productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_MDM_ID, prodSeasonLinkInfoItem.getMdmId());
				productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_DETAILS, "");
				productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PLM_ID, prodSeasonLinkInfoItem.getPlmId());
				productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_FEEDBACK_STATUS, SMProductInboundWebServiceConstants.PENDING);
				productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_ERROR_REASON, "Product Season Link DOES NOT EXIST !!! INVALID PLM ID RECEIVED !!!");
				productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_INTEGRATION_STATUS, SMProductInboundWebServiceConstants.NOT_INTEGRATED);
				//added for phase-8 SEPD chnages
				productSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.ITERATION_ID, prodSeasonLinkInfoItem.getIterationId());
			}
			//Persisting updated log entry.
			SMProductInboundUtil.persistInboundLogEntry(productSeasonLinkLogEntry);
		}catch(WTException wtEx){
			wtEx.printStackTrace();
		} catch (WTPropertyVetoException wtPv) {
			wtPv.printStackTrace();
		}
	}

	/**
	 * Create Log Entry for Colorway Season.
	 * @param skuSeasonLink - LCSSKUSeasonLink
	 * @param skuSeasonLinkInfoItem - ColorwaySeasonLinkInformationItem
	 */
	public static void createLogEntryColorwaySeasonLinkInbound(LCSSKUSeasonLink skuSeasonLink, ColorwaySeasonLinkInformationItem skuSeasonLinkInfoItem){
		LOGGER.debug("Inside create Log Entry for Colorway Season Link");
		try{
			com.lcs.wc.flextype.FlexType colorwayInboundCreateLogType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PATH);
			LCSLogEntry colorwaySeasonLinkLogEntry = LCSLogEntry.newLCSLogEntry();
			LOGGER.info("request id ------------->>>>>  "+SMProductInboundDataRequestWebClient.getRequestID());
			if(null != skuSeasonLink){
				//Setting attribute values
				colorwaySeasonLinkLogEntry.setFlexType(colorwayInboundCreateLogType);
				colorwaySeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_REQUEST_ID, String.valueOf(SMProductInboundDataRequestWebClient.getRequestID()));
				colorwaySeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_OBJECT_TYPE, "COLORWAY SEASON LINK");
				colorwaySeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_MDMID, skuSeasonLink.getValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_MDMID));
				colorwaySeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_LINK_DETAILS, SMProductInboundUtil.skuSeasonLinkDetails(skuSeasonLink));
				colorwaySeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID, skuSeasonLinkInfoItem.getPlmId());
				colorwaySeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_FEEDBACK_STATUS, SMProductInboundWebServiceConstants.PENDING);
				colorwaySeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_ERROR_REASON, SMProductInboundDataRequestWebClient.getErrorMsg());
				//added for phase-8 SEPD chnages
				colorwaySeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.ITERATION_ID, skuSeasonLinkInfoItem.getIterationId());
				if(!FormatHelper.hasContent(SMProductInboundDataRequestWebClient.getErrorMsg()) && !SMProductInboundDataRequestWebClient.isIntegrationFailureSSL()){
					LOGGER.info("INTEGARTION SUCCESS on SSL");
					colorwaySeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_INTEGARTION_STATUS, SMProductInboundWebServiceConstants.INTEGRATED);
				}else if(SMProductInboundDataRequestWebClient.isIntegrationFailureSSL()){
					LOGGER.info("INTEGARTION FAILED on SSL");
					colorwaySeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_INTEGARTION_STATUS, SMProductInboundWebServiceConstants.NOT_INTEGRATED);
				}
				SMProductInboundUtil.persistInboundLogEntry(colorwaySeasonLinkLogEntry);
				getSkuSeasonLinkLogEntryCollection().put((String)colorwaySeasonLinkLogEntry.getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID), colorwaySeasonLinkLogEntry);
			}else{
				LCSLogEntry colorwaySeasonLogEntryInvalidPLMID = LCSLogEntry.newLCSLogEntry();
				colorwaySeasonLogEntryInvalidPLMID.setFlexType(colorwayInboundCreateLogType);
				colorwaySeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_REQUEST_ID, String.valueOf(SMProductInboundDataRequestWebClient.getRequestID()));
				colorwaySeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_OBJECT_TYPE, "COLORWAY SEASON LINK");
				colorwaySeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_MDMID, skuSeasonLinkInfoItem.getMdmId());
				colorwaySeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_LINK_DETAILS, "");
				colorwaySeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID, skuSeasonLinkInfoItem.getPlmId());
				colorwaySeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_FEEDBACK_STATUS, SMProductInboundWebServiceConstants.PENDING);
				colorwaySeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_ERROR_REASON, "Colorway Season Link DOES NOT EXIST !!! INVALID PLM ID RECEIVED !!!");
				colorwaySeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_INTEGARTION_STATUS, SMProductInboundWebServiceConstants.NOT_INTEGRATED);
				//added for phase-8 SEPD chnages
				colorwaySeasonLogEntryInvalidPLMID.setValue(SMProductInboundWebServiceConstants.ITERATION_ID, skuSeasonLinkInfoItem.getIterationId());
				SMProductInboundUtil.persistInboundLogEntry(colorwaySeasonLogEntryInvalidPLMID);
				getSkuSeasonLinkLogEntryCollection().put((String)colorwaySeasonLogEntryInvalidPLMID.getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID), colorwaySeasonLogEntryInvalidPLMID);
			}
			//Persisting Log Entry.
			

			//Adding newly created ntry to existing collection.
			
			setSkuSeasonLinkLogEntryCollection(getSkuSeasonLinkLogEntryCollection());
		}catch(WTException wtexcption){
			wtexcption.printStackTrace();
		} catch (WTPropertyVetoException propVetoExcp){
			propVetoExcp.printStackTrace();
		}
	}

	/**
	 * Update SKU Season log entry.
	 * @param colorwaySeasonLinkLogEntry - LCSLogEntry
	 * @param skuSeasonLink - LCSSKUSeasonLink
	 * @param skuSeasonLinkInfoItem - ColorwaySeasonLinkInformationItem
	 */
	public static void updateLogEntryColorwaySeasonLinkInbound(LCSLogEntry skuSeasonLinkLogEntry, LCSSKUSeasonLink skuSeasonLink, ColorwaySeasonLinkInformationItem skuSeasonLinkInfoItem){
		LOGGER.debug("Inside update Log Entry for Colorway Season Link");
		try{
			LOGGER.info("request id ------------->>>>>>  "+SMProductInboundDataRequestWebClient.getRequestID());
			//Setting attribute.
			if(null != skuSeasonLink){
				skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_REQUEST_ID, String.valueOf(SMProductInboundDataRequestWebClient.getRequestID()));
				skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_MDMID, skuSeasonLink.getValue(SMProductInboundWebServiceConstants.COLORWAY_SEASON_LINK_MDMID));
				skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_LINK_DETAILS, SMProductInboundUtil.skuSeasonLinkDetails(skuSeasonLink));
				skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID, skuSeasonLinkInfoItem.getPlmId());
				skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_FEEDBACK_STATUS, SMProductInboundWebServiceConstants.PENDING);
				skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_ERROR_REASON, SMProductInboundDataRequestWebClient.getErrorMsg());
				//added for phase-8 SEPD chnages
				skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.ITERATION_ID, skuSeasonLinkInfoItem.getIterationId());
				if(!FormatHelper.hasContent(SMProductInboundDataRequestWebClient.getErrorMsg()) && !SMProductInboundDataRequestWebClient.isIntegrationFailureSSL()){
					LOGGER.info("INTEGARTION SUCCESS on SSL");
					skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_INTEGARTION_STATUS, SMProductInboundWebServiceConstants.INTEGRATED);
				}else if(SMProductInboundDataRequestWebClient.isIntegrationFailureSSL()){
					LOGGER.info("INTEGARTION FAILED on SSL");
					skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_INTEGARTION_STATUS, SMProductInboundWebServiceConstants.NOT_INTEGRATED);
				}
			}else{
				skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_REQUEST_ID, String.valueOf(SMProductInboundDataRequestWebClient.getRequestID()));
				skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_MDMID, skuSeasonLinkInfoItem.getMdmId());
				skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_LINK_DETAILS, "");
				skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID, skuSeasonLinkInfoItem.getPlmId());
				skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_FEEDBACK_STATUS, SMProductInboundWebServiceConstants.PENDING);
				skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_ERROR_REASON, "Colorway Season Link DOES NOT EXIST !!! INVALID PLM ID RECEIVED !!!");
				skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_INTEGARTION_STATUS, SMProductInboundWebServiceConstants.NOT_INTEGRATED);
				//added for phase-8 SEPD chnages
				skuSeasonLinkLogEntry.setValue(SMProductInboundWebServiceConstants.ITERATION_ID, skuSeasonLinkInfoItem.getIterationId());
				
			}
			//persisting updated log entry.
			SMProductInboundUtil.persistInboundLogEntry(skuSeasonLinkLogEntry);
		}catch(WTException wtExc){
			wtExc.printStackTrace();
		} catch (WTPropertyVetoException wtPvE) {
			wtPvE.printStackTrace();
		}
	}

	/**
	 * Queries the Product Season Log Entry.
	 * @param flexPath - String
	 * @throws WTException - WTException
	 * @returns hashMapLogEntry - HashMap<String, LCSLogEntry>
	 */
	public static Map<String, LCSLogEntry> queryProductSeasonLinkInboundLogEntry() throws WTException{
		LOGGER.debug("Inside Query Product Season Link Inbound Log Entry method !!!!!!!!!!");
		//Hashmap to store Log entries
		Map<String, LCSLogEntry> hashMapProductSeasonLinkLogEntry=new HashMap<String, LCSLogEntry>();
		com.lcs.wc.flextype.FlexType logType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PATH);
		com.lcs.wc.db.PreparedQueryStatement prodSeasonStatement = new com.lcs.wc.db.PreparedQueryStatement();//Creating Statement.
		prodSeasonStatement.appendFromTable(LCSLogEntry.class);
		prodSeasonStatement.appendSelectColumn(SMProductInboundWebServiceConstants.LCSLOGENTRY, logType.getAttribute(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PLM_ID).getColumnName());//append column
		prodSeasonStatement.appendSelectColumn(SMProductInboundWebServiceConstants.LCSLOGENTRY, "IDA2A2");//append column
		prodSeasonStatement.appendCriteria(new Criteria(SMProductInboundWebServiceConstants.LCSLOGENTRY, "flexTypeIdPath", logType.getIdPath(),Criteria.EQUALS));//adding criteria
		com.lcs.wc.db.SearchResults prodResults = null;
		//executing  statement
		prodResults =LCSQuery.runDirectQuery(prodSeasonStatement);
		List<?> proddataCollection= prodResults.getResults();
		FlexObject prodfo=null;
		LOGGER.debug("Log Entry data collection Size for Prod Inbound >>>>>>>\t"+proddataCollection.size());
		if (proddataCollection.size() > 0) {
			for(Object obj:proddataCollection){
				prodfo= (FlexObject) obj;
				LCSLogEntry prodlogEntry=(LCSLogEntry) LCSQuery.findObjectById("com.lcs.wc.foundation.LCSLogEntry:"+prodfo.getString("LCSLOGENTRY.IDA2A2"));
				//storing in hashmap
				hashMapProductSeasonLinkLogEntry.put((String)prodlogEntry.getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PLM_ID), prodlogEntry);
			}	
		}
		//returning log entry
		return (HashMap<String, LCSLogEntry>) hashMapProductSeasonLinkLogEntry;
	}

	/**
	 * Queries the Colorway Season Link Log Entry.
	 * @param flexPath - String
	 * @throws WTException - WTException
	 * @returns hashMapLogEntry - HashMap<String, LCSLogEntry>
	 */
	public static Map<String, LCSLogEntry> queryColorwaySeasonLinkInboundLogEntry() throws WTException{
		LOGGER.debug("Inside query Colorway Season Link Inbound method !!!!!!!!!!");
		//Hashmap to store Log entries
		Map<String, LCSLogEntry> hashColorwaySeasonLogEntryMapEntry=new HashMap<String, LCSLogEntry>();
		com.lcs.wc.flextype.FlexType logType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PATH);
		com.lcs.wc.db.PreparedQueryStatement skuSeasonStatement = new com.lcs.wc.db.PreparedQueryStatement();//Creating Statement.
		skuSeasonStatement.appendFromTable(LCSLogEntry.class);
		skuSeasonStatement.appendSelectColumn(SMProductInboundWebServiceConstants.LCSLOGENTRY, logType.getAttribute(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID).getColumnName());//append column
		skuSeasonStatement.appendSelectColumn(SMProductInboundWebServiceConstants.LCSLOGENTRY, "IDA2A2");//append column
		skuSeasonStatement.appendCriteria(new Criteria(SMProductInboundWebServiceConstants.LCSLOGENTRY, "flexTypeIdPath", logType.getIdPath(),Criteria.EQUALS));//adding criteria
		com.lcs.wc.db.SearchResults skuresults = null;
		//executing  statement
		skuresults =LCSQuery.runDirectQuery(skuSeasonStatement);
		List<?> skudataCollection= skuresults.getResults();
		FlexObject skufo=null;
		LOGGER.debug("Log Entry data collection Size for SKU Season >>>>>>>\t"+skudataCollection.size());
		if (skudataCollection.size() > 0) {
			for(Object obj:skudataCollection){
				skufo= (FlexObject) obj;
				LCSLogEntry skulogEntry=(LCSLogEntry) LCSQuery.findObjectById("com.lcs.wc.foundation.LCSLogEntry:"+skufo.getString("LCSLOGENTRY.IDA2A2"));
				//storing in hashmap
				hashColorwaySeasonLogEntryMapEntry.put((String)skulogEntry.getValue(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PLM_ID), skulogEntry);
			}	
		}
		//returning log entry
		return (HashMap<String, LCSLogEntry>) hashColorwaySeasonLogEntryMapEntry;
	}

	/**
	 * @return the productSeasonLinkLogEntryCollection
	 */
	public static Map<String, LCSLogEntry> getProductSeasonLinkLogEntryCollection() {
		return productSeasonLinkLogEntryCollection;
	}

	/**
	 * @param productSeasonLinkLogEntryCollection the productSeasonLinkLogEntryCollection to set
	 */
	public static void setProductSeasonLinkLogEntryCollection(
			Map<String, LCSLogEntry> productSeasonLinkLogEntryCollection) {
		SMProductInboundLogEntryProcessor.productSeasonLinkLogEntryCollection = productSeasonLinkLogEntryCollection;
	}

	/**
	 * @return the skuSeasonLinkLogEntryCollection
	 */
	public static Map<String, LCSLogEntry> getSkuSeasonLinkLogEntryCollection() {
		return skuSeasonLinkLogEntryCollection;
	}

	/**
	 * @param skuSeasonLinkLogEntryCollection the skuSeasonLinkLogEntryCollection to set
	 */
	public static void setSkuSeasonLinkLogEntryCollection(
			Map<String, LCSLogEntry> skuSeasonLinkLogEntryCollection) {
		SMProductInboundLogEntryProcessor.skuSeasonLinkLogEntryCollection = skuSeasonLinkLogEntryCollection;
	}
}
