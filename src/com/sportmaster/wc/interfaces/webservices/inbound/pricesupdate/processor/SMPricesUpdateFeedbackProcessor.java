package com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.processor;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lcs.wc.foundation.LCSLogEntry;
import com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.client.SMPriceUpdateRequestWebClient;
import com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.util.SMPriceInboundUtil;
import com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.util.SMPriceUpdateXMLUtil;
import com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.util.SMPricesInboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.GetStatusPricesRequest;
import com.sportmaster.wc.interfaces.webservices.productbean.GetStatusPricesResponse;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonPricesInformationStatus;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonPricesInformationStatusResponse;

import wt.util.WTException;

/**
 * SMPricesUpdateFeedbackProcessor.java
 * This class processes complete feedback integration process..
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMPricesUpdateFeedbackProcessor {

	/*
	 * FEEDBACK_LOGGER.
	 */
	public static final Logger FEEDBACK_LOGGER = Logger.getLogger("priceUpdateFeedback");

	/**
	 * pricesUpdateLogEntryColl.
	 */
	private Map<String, LCSLogEntry> pricesUpdateLogEntryColl=new HashMap<String, LCSLogEntry>();
	/**
	 * feedbackRequestId.
	 */
	private int feedbackRequestId;
	/**
	 * Total number of records received.
	 */
	private int totalCountReceived;
	/**
	 * Total number of records received.
	 */
	private int countProcessed;
	/**
	 * Total number of records received.
	 */
	private int countFailed;

	/**
	 *this method handles complete feedback business flow.
	 */
	public void processFeedback() {

		try {
			
			setPricesUpdateLogEntryColl(SMPriceInboundUtil.queryPriceUpdateFeedbackLogEntry());

			GetStatusPricesRequest getStatusPrices = new GetStatusPricesRequest();
			
			//setting request id.
			setRequestId(new SMPriceInboundUtil().getProductInboundRequestID());
			FEEDBACK_LOGGER.debug("Request ID in request for Inbound Integartion  >>>>>>>>>>>>>  "+getRequestId());


			getStatusPrices.setRequestId(String.valueOf(getRequestId()));
			getStatusPrices(getStatusPrices);
			
			//generating XML file for request data.
			SMPriceUpdateXMLUtil.generateXMLFileForFeedbackRequest(getStatusPrices, "StatusPricesRequest");
			
			//Sending price update SOAP request
			GetStatusPricesResponse response = SMPriceUpdateRequestWebClient.getPricesUpdateStatusRequest(getStatusPrices);
			
			if(validateResponse(response)) {
				//generating XML file for response data.
				SMPriceUpdateXMLUtil.generateXMLFileForFeedbackResponse(response, "StatusPricesResponse");
				//processing received data.
				processStatusPricesResponse(response);
			}
			
		} catch (WTException e) {
			FEEDBACK_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,e);
		}

	}

	/**
	 * Creates DATA from price update feddback request.
	 * @param getStatusPrices - bean class
	 */
	private void getStatusPrices(GetStatusPricesRequest getStatusPrices) {
		ProductSeasonPricesInformationStatus pricesStatus ;
		if(getPricesUpdateLogEntryColl() != null && getPricesUpdateLogEntryColl().size() > 0){
			//itterating logentry coll data.
			for(Map.Entry<String, LCSLogEntry> pricesMapEntry : getPricesUpdateLogEntryColl().entrySet()){
				pricesStatus = new ProductSeasonPricesInformationStatus();
				FEEDBACK_LOGGER.info("Adding price entry  from list to feedback ##############");
				try {
					pricesStatus.setMdmId(pricesMapEntry.getValue().getValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_MDM_ID).toString());
					pricesStatus.setPlmId(pricesMapEntry.getValue().getValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_PLM_ID).toString());
					pricesStatus.setStatus(pricesMapEntry.getValue().getValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_INTEGRATION_STATUS).toString());
					pricesStatus.setIterationId(pricesMapEntry.getValue().getValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_ITTERATION_ID).toString());

					getStatusPrices.getProductSeasonPricesInformationStatus().add(pricesStatus);
					FEEDBACK_LOGGER.debug("Added prices update entry for plm id:-"+pricesStatus.getPlmId());
				} catch (WTException e) {
					FEEDBACK_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL, e);
				}
			}
		}
	}

	/**
	 * processes feedback response data.
	 * @param response -  response object
	 */
	private void processStatusPricesResponse(GetStatusPricesResponse response) {

		FEEDBACK_LOGGER.debug("recevied REQUEST ID in response:-"+ response.getRequestId());
		FEEDBACK_LOGGER.debug("recevied ERROR REASON in response:-"+ response.getErrorMessage());
		FEEDBACK_LOGGER.debug("recevied TOTAL STATUS PRICES UPDATE DATA COUNTin response:-"
				+ response.getProductSeasonPricesInformationStatusResponse().size());

		setTotalCount(response.getProductSeasonPricesInformationStatusResponse().size());
		
		//iterating received response data.
		for(ProductSeasonPricesInformationStatusResponse statsuResponse:response.getProductSeasonPricesInformationStatusResponse()) {
			try {
				LCSLogEntry prodSeasonLogEntryObj = getPricesUpdateLogEntryColl().get(statsuResponse.getPlmId());

				if(prodSeasonLogEntryObj != null){
					prodSeasonLogEntryObj.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_PLM_ID, statsuResponse.getPlmId());
					
					if(SMPricesInboundWebServiceConstants.RECEIVED_VALID_FEEDBACK.equalsIgnoreCase(statsuResponse.getStatus())){
						FEEDBACK_LOGGER.info("received valid  ......setting status to Processed !!!!!!!!");
						prodSeasonLogEntryObj.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_FEEDBACK_STATUS, SMPricesInboundWebServiceConstants.PROCESSED);
					}else{
						FEEDBACK_LOGGER.info("received invalid  ......setting status to PENDING !!!!!!!!");
						prodSeasonLogEntryObj.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_FEEDBACK_STATUS, SMPricesInboundWebServiceConstants.PENDING);
					}
					
					prodSeasonLogEntryObj.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_REQUEST_ID, String.valueOf(getRequestId()));

					SMPriceInboundUtil.persistPricesLogEntry(prodSeasonLogEntryObj);
					setTotalCountProcessed(1);
					
				}else{
					FEEDBACK_LOGGER.error("PRODUCT SEASON LINK OBJECT ID in RESPONSE DOES NOT MATCH THAT IN LOG ENTRY");
					setTotalCountFailed(1);
				}
			} catch (WTException e) {
				FEEDBACK_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL, e);
				setTotalCountFailed(1);
			}

		}
	}
	
	/**
	 * validates price update FEEDBACK response data.
	 * @param response - GetStatusPricesResponse
	 * @return boolean value
	 */
	private boolean validateResponse(GetStatusPricesResponse response) {
		if(response == null){
			FEEDBACK_LOGGER.debug("Recevied a NULL RESPONSE");
			return false;
		}
		if( null == response.getProductSeasonPricesInformationStatusResponse()) {

			//set looger for toal data recevied
			FEEDBACK_LOGGER.debug("Recevied BLANK Respsone");
			return false;
		}
		FEEDBACK_LOGGER.debug("response is valid");
		return true;
	}
	
	/**
	 * @return the pricesUpdateLogEntryColl
	 */
	public Map<String, LCSLogEntry> getPricesUpdateLogEntryColl() {
		return pricesUpdateLogEntryColl;
	}

	/**
	 * @param pricesUpdateLogEntryColl the pricesUpdateLogEntryColl to set
	 */
	private void setPricesUpdateLogEntryColl(Map<String, LCSLogEntry> pricesUpdateLogEntryColl) {
		this.pricesUpdateLogEntryColl = pricesUpdateLogEntryColl;
	}

	/**
	 * @return the requestId
	 */
	public int getRequestId() {
		return feedbackRequestId;
	}

	/**
	 * @param requestId the requestId to set
	 */
	public void setRequestId(int requestId) {
		this.feedbackRequestId = requestId;
	}

	/**
	 * @return the totalCount
	 */
	public int getTotalCount() {
		return totalCountReceived;
	}

	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this.totalCountReceived = totalCount;
	}

	/**
	 * @return the totalCountProcessed
	 */
	public int getTotalCountProcessed() {
		return countProcessed;
	}

	/**
	 * @param totalCountProcessed the totalCountProcessed to set
	 */
	public void setTotalCountProcessed(int totalCountProcessed) {
		this.countProcessed = this.countProcessed + totalCountProcessed;
	}

	/**
	 * @return the totalCountFailed
	 */
	public int getTotalCountFailed() {
		return countFailed;
	}

	/**
	 * @param totalCountFailed the totalCountFailed to set
	 */
	public void setTotalCountFailed(int totalCountFailed) {
		this.countFailed = this.countFailed + totalCountFailed;
	}
	
	/**
	 * adds feedback integration summary in log file.
	 */
	public void printFeedbackSummary() {
		StringBuilder summary=new StringBuilder();
		
		summary.append("\n\n###############    SUMMARY OF PRICE UPDATE FEEDBACK INTEGRATION RUN    ###############");
		summary.append("\n#########   TOTAL NUMBER OF RECORDS RECEIVED\t\t---------->   ").append(getTotalCount());
		summary.append("\n#########   TOTAL NUMBER OF SUCCESSFUL RECORDS\t\t---------->   ").append(getTotalCountProcessed());
		summary.append("\n#########   TOTAL NUMBER OF FAILED RECORDS\t\t---------->   ").append(getTotalCountFailed());
		summary.append("\n\n#####################################################################################");
		
		FEEDBACK_LOGGER.debug(summary);
	}

}
