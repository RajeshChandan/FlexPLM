package com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.processor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.client.SMPriceUpdateRequestWebClient;
import com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.util.SMPriceInboundUtil;
import com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.util.SMPriceUpdateXMLUtil;
import com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.util.SMPricesInboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ColorwaySeasonPricesInformation;
import com.sportmaster.wc.interfaces.webservices.productbean.GetPricesUpdatesResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonPricesInformation;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;
/**
 * SMPricesUpdateProcessor.java
 * This class processes complete price update request integration process..
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMPricesUpdateProcessor {

	/*
	 * REQ_LOGGER.
	 */
	public static final Logger REQ_LOGGER = Logger.getLogger("priceUpdateRequest");

	/**
	 * log entry collection.
	 */
	private Map<String, LCSLogEntry> logEntryColl = new HashMap<>();
	/**
	 * error message.
	 */
	private static String errorMsg;
	/**
	 * request id.
	 */
	private static String requestId;
	/**
	 * Total number of records received.
	 */
	private int totalCount;
	/**
	 * Total number of records received.
	 */
	private int totalCountProcessed;
	/**
	 * Total number of records received.
	 */
	private int totalCountFailed;
	/**
	 * Failed flag for Product Season Link.
	 */
	private static boolean integartionFailurePSL=true;

	/**
	 * this method process complete price update request integration business flow.
	 * @throws WTException exception
	 */
	public void processPricesUpdateRequest() throws WTException {

		LCSProductSeasonLink productSeasonLink;
		GetPricesUpdatesResponse response= SMPriceUpdateRequestWebClient.getPricesUpdateRequestResponse();

		if(validateResponse(response)) {

			REQ_LOGGER.debug("response.getRequestId()========>"+response.getRequestId());
			setRequestId(response.getRequestId());
			REQ_LOGGER.debug("response.ErrorMessage--->"+response.getErrorMessage());
			setErrorMsg(response.getErrorMessage());

			//generate XML FILE FROM REPONSE DATA.
			SMPriceUpdateXMLUtil.generateXMLFileForPriceUpdateIntegrationRequest(response, "PricesUpdateRequest");

			logEntryColl=SMPriceInboundUtil.queryPricesUpdateInboundLogEntry();
			REQ_LOGGER.debug("total logentry found in FLEX PLM:---->"+logEntryColl.size());

			//process data
			for(ProductSeasonPricesInformation pricesData:response.getProductSeasonPricesInformation()) {

				REQ_LOGGER.debug("processing prices update data for PLMID:-"+pricesData.getPlmId());

				productSeasonLink= SMPriceInboundUtil.getProductSeasonLinkFromPLMID(pricesData.getPlmId());
				if(productSeasonLink != null) {
					// Phase - 13 Changes, process prices data into FlexPLM
					processPrices(productSeasonLink, pricesData);
					//processing log entry.
					processPriceUpdateLogEntry(productSeasonLink, pricesData);
				}else {
					REQ_LOGGER.debug("PRODUCT SEAON LINK OBJECT not found for recved PLM ID:-"+pricesData.getPlmId());
					setTotalCountFailed(1);
					setErrorMsg("PRODUCT SEAON LINK OBJECT not found for recved PLM ID:-"+pricesData.getPlmId());
					processPriceUpdateLogEntry(productSeasonLink, pricesData);
				}
			}
		}

	}

	/**
	 * Phase 13 Changes. process prices data into FlexPLM.
	 *
	 * @param productSeasonLink
	 * @param pricesData
	 */
	private void processPrices(LCSProductSeasonLink productSeasonLink, ProductSeasonPricesInformation pricesData) {

		String pslMDMId;
		String pslType;
		try {

			pslMDMId = String.valueOf(productSeasonLink.getValue(SMPricesInboundWebServiceConstants.PSL_MDM_ID));
			pslType = productSeasonLink.getFlexType().getFullName();
			if (pslMDMId.equals(pricesData.getMdmId())) {

				// filtering target mup attribute based on product season type.
				if (pslType.startsWith("APD") || pslType.startsWith("smAccessories")) {
					setValue(productSeasonLink, SMPricesInboundWebServiceConstants.TARGET_M_UP_KEY_APD_ACCESSORIES,
							pricesData.getTargetMUp());
				} else {
					setValue(productSeasonLink, SMPricesInboundWebServiceConstants.TARGET_M_UP_KEY_SEPD_FPD, pricesData.getTargetMUp());
				}

				setValue(productSeasonLink, SMPricesInboundWebServiceConstants.TARGET_PP_KEY, pricesData.getTargetPP());
				setValue(productSeasonLink, SMPricesInboundWebServiceConstants.RR_PRU_KEY, pricesData.getRRPru());

				// phase 13 -EMP-284 - EHR 642|Integration of price
				// attributes from TOP to FlexPLM - start
				setValue(productSeasonLink, SMPricesInboundWebServiceConstants.TARGET_PURCHASE_PRICE, pricesData.getTargetPurchasePrice());
				setValue(productSeasonLink, SMPricesInboundWebServiceConstants.TARGET_PP_CURRENCY, pricesData.getTargetPPCurrency());
				setValue(productSeasonLink, SMPricesInboundWebServiceConstants.PRODUCTION_REGION, pricesData.getProductionRegion());
				setValue(productSeasonLink, SMPricesInboundWebServiceConstants.AP_RRP_GROSS_RUB, pricesData.getAPRRPGrossRUB());


				processSkuData(pricesData.getColorwaySeasonPricesInformation());
				// phase 13 -EMP-284 - EHR 642|Integration of price
				// attributes from TOP to FlexPLM - start

				// persisting product season object.
				SMPriceInboundUtil.persistProductSeasonLink(productSeasonLink);
				setIntegartionFailurePSL(false);
				setTotalCountProcessed(1);
				REQ_LOGGER.debug("Updated prices update data on prodcut season link object for PLMID:-" + pricesData.getPlmId());
			} else {
				REQ_LOGGER
				.debug("Recvied MDM ID :-" + pricesData.getMdmId() + ", not matched with PRODUCT SEASON LINK MDM ID :-" + pslMDMId);
				setIntegartionFailurePSL(true);
				setErrorMsg("Recvied MDM ID :-" + pricesData.getMdmId() + ", not matched with PRODUCT SEASON LINK MDM ID :-" + pslMDMId);
				setTotalCountFailed(1);
			}
		} catch (WTException | WTPropertyVetoException e) {
			REQ_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL, e);
			setIntegartionFailurePSL(true);
			setErrorMsg(e.getLocalizedMessage());
			setTotalCountFailed(1);
		}
	}
	private void setValue(LCSProductSeasonLink productSeasonLink, String key, BigDecimal value)
			throws WTPropertyVetoException, WTException {

		if (Objects.nonNull(value) && FormatHelper.hasContent(value.toString())) {
			productSeasonLink.setValue(key, value);
		}

	}
	private void setValue(LCSSKUSeasonLink skuSeasonLink, String key, BigDecimal value) throws WTPropertyVetoException, WTException {

		if (Objects.nonNull(value) && FormatHelper.hasContent(value.toString())) {
			skuSeasonLink.setValue(key, value);
		}
	}
	private void setValue(LCSProductSeasonLink productSeasonLink, String key, String value) throws WTException {
		if (Objects.nonNull(value) && FormatHelper.hasContent(value)) {
			productSeasonLink.setValue(key, value);
		}
	}
	/**
	 * handles log entry create \ update process.
	 * @param productSeasonLink -  product season object
	 * @param pricesData - ProductSeasonPricesInformation
	 */
	public void processPriceUpdateLogEntry(LCSProductSeasonLink productSeasonLink,ProductSeasonPricesInformation pricesData)
	{
		if(logEntryColl.containsKey(pricesData.getPlmId())) {
			LCSLogEntry pricesEntry=logEntryColl.get(pricesData.getPlmId());
			//update log entry
			SMPriceInboundUtil.updateLogEntryProductSeasonLinkInbound(pricesEntry, productSeasonLink, pricesData);
		}else {
			//create log entry and add to master coll
			LCSLogEntry pricesEntry=SMPriceInboundUtil.createLogEntryPriceUpdateInbound(productSeasonLink, pricesData);
			try {
				logEntryColl.put((String)pricesEntry.getValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_PLM_ID), pricesEntry);
			} catch (WTException e) {
				REQ_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,e);
			}
		}

	}
	/**
	 * phase 13 -EMP-284 - EHR 642|Integration of price attributes from TOP to
	 * FlexPLM.
	 *
	 * @param skuPriceData
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private void processSkuData(List<ColorwaySeasonPricesInformation> skuPriceData) throws WTException, WTPropertyVetoException {

		LCSSKUSeasonLink skuSeasonLink;
		for (ColorwaySeasonPricesInformation skuPrice : skuPriceData) {
			skuSeasonLink = SMPriceInboundUtil.getSKUSeasonLinkFromPLMID(skuPrice.getPlmId());
			if (Objects.nonNull(skuSeasonLink)) {

				setValue(skuSeasonLink, SMPricesInboundWebServiceConstants.PLANNED_MUP, skuPrice.getPlannedMUP());
				setValue(skuSeasonLink, SMPricesInboundWebServiceConstants.RRP_GROSS_RUB, skuPrice.getRRPGrossRUB());

				SMPriceInboundUtil.persistColorwaySeasonLink(skuSeasonLink);
			}

		}
	}
	/**
	 * validates price update response data.
	 * @param response - GetPricesUpdatesResponse
	 * @return boolean value
	 */
	private boolean validateResponse(GetPricesUpdatesResponse response) {
		if(response == null){
			REQ_LOGGER.debug("Recevied a NULL RESPONSE");
			return false;
		}
		if( null != response.getProductSeasonPricesInformation()) {

			//set looger for toal data recevied
			REQ_LOGGER.debug("total prices object data recevied:-"+response.getProductSeasonPricesInformation().size());
			setTotalCount(response.getProductSeasonPricesInformation().size());

		}else {
			REQ_LOGGER.debug("total prices object data recevied:-"+response.getProductSeasonPricesInformation().size());
			setTotalCount(response.getProductSeasonPricesInformation().size());
			return false;
		}

		REQ_LOGGER.debug("response is valid");
		return true;
	}

	/**
	 * @return the errorMsg
	 */
	public static String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg the errorMsg to set
	 */
	public static void setErrorMsg(String errorMsg) {
		SMPricesUpdateProcessor.errorMsg = errorMsg;
	}

	/**
	 * @return the logEntryColl
	 */
	public Map<String, LCSLogEntry> getLogEntryColl() {
		return logEntryColl;
	}

	/**
	 * @return the requestId
	 */
	public static String getRequestId() {
		return requestId;
	}

	/**
	 * @param requestId the requestId to set
	 */
	public static void setRequestId(String requestId) {
		SMPricesUpdateProcessor.requestId = requestId;
	}

	/**
	 * @param integartionFailurePSL the integartionFailurePSL to set
	 */
	public static void setIntegartionFailurePSL(boolean integartionFailurePSL) {
		SMPricesUpdateProcessor.integartionFailurePSL = integartionFailurePSL;
	}

	/**
	 * @return the integartionFailurePSL
	 */
	public static boolean isIntegartionFailurePSL() {
		return integartionFailurePSL;
	}

	/**
	 * @return the totalCount
	 */
	public int getTotalCount() {
		return totalCount;
	}

	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * @return the totalCountProcessed
	 */
	public int getTotalCountProcessed() {
		return totalCountProcessed;
	}

	/**
	 * @param totalCountProcessed the totalCountProcessed to set
	 */
	public void setTotalCountProcessed(int totalCountProcessed) {
		this.totalCountProcessed = this.totalCountProcessed + totalCountProcessed;
	}

	/**
	 * @return the totalCountFailed
	 */
	public int getTotalCountFailed() {
		return totalCountFailed;
	}

	/**
	 * @param totalCountFailed the totalCountFailed to set
	 */
	public void setTotalCountFailed(int totalCountFailed) {
		this.totalCountFailed = this.totalCountFailed + totalCountFailed;
	}

	/**
	 * prints price update request integration summary in logs file.
	 */
	public void printSummary() {
		StringBuilder summary=new StringBuilder();

		summary.append("\n\n###############    SUMMARY OF PRICE UPDATE REQUEST INTEGRATION RUN    ###############");
		summary.append("\n#########   TOTAL NUMBER OF RECORDS RECEIVED\t\t---------->   ").append(getTotalCount());
		summary.append("\n#########   TOTAL NUMBER OF SUCCESSFUL RECORDS\t\t---------->   ").append(getTotalCountProcessed());
		summary.append("\n#########   TOTAL NUMBER OF FAILED RECORDS\t\t---------->   ").append(getTotalCountFailed());
		summary.append("\n\n#####################################################################################");

		REQ_LOGGER.debug(summary);
	}

}
