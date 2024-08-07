package com.sportmaster.wc.interfaces.webservices.outbound.bom.util;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.sportmaster.wc.interfaces.webservices.bombean.BOMRequestResponse;
import com.sportmaster.wc.interfaces.webservices.bombean.BomWS;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.helper.SMBOMOutboundHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.processor.SMBOMOutboundDataProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.processor.SMBOMOutboundLogEntryProcessor;
import com.sun.xml.ws.client.BindingProviderProperties;

public class SMBOMOutboundIntegrationBean {

	public SMBOMOutboundIntegrationBean() {
		super();
	}

	private boolean updateRequest;
	private boolean isCreate;
	private FlexBOMPart flexbomPart;
	private BomWS bomOutboundWS;
	private LCSSeason associatedSeason;
	private LCSProductSeasonLink productSeasonLink;
	private SMBOMLogEntryUtill logentryUtil;
	private SMBOMOutboundUtil bomUtill;
	private SMBOMOutboundHelper bomHelper;
	private SMBOMOutboundDataProcessor bomProcessor;
	private SMBOMOutboundRequestXMLGenerator xmlUtill;
	private SMBOMOutboundLogEntryProcessor logEntryProcessor;
	private BOMRequestResponse bomOutboundResponse;
	private int bomOutboundRequestID;
	private String responseErrorReason;
	private String logentryStatus;
	private String bomPartPLMID;
	private String bomPartValidationErrorMessage;
	private Map<String, LCSLogEntry> bomPartOutboundLogEntry;
	private String colorwayPLMId;
	private String colorwayMDMId;
	private String colorwaySeasonPLMID;
	private String colorwaySeasonMDMID;
    private String sizeName;
    private String sizeDefinitionPLMID;
    
    
	public String getColorwayPLMId() {
		return colorwayPLMId;
	}

	public void setColorwayPLMId(String colorwayPLMId) {
		this.colorwayPLMId = colorwayPLMId;
	}

	public String getColorwayMDMId() {
		return colorwayMDMId;
	}

	public void setColorwayMDMId(String colorwayMDMId) {
		this.colorwayMDMId = colorwayMDMId;
	}

	public String getColorwaySeasonPLMID() {
		return colorwaySeasonPLMID;
	}

	public void setColorwaySeasonPLMID(String colorwaySeasonPLMID) {
		this.colorwaySeasonPLMID = colorwaySeasonPLMID;
	}

	public String getColorwaySeasonMDMID() {
		return colorwaySeasonMDMID;
	}

	public void setColorwaySeasonMDMID(String colorwaySeasonMDMID) {
		this.colorwaySeasonMDMID = colorwaySeasonMDMID;
	}

	public String getSizeName() {
		return sizeName;
	}

	public void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}

	public String getSizeDefinitionPLMID() {
		return sizeDefinitionPLMID;
	}

	public void setSizeDefinitionPLMID(String sizeDefinitionPLMID) {
		this.sizeDefinitionPLMID = sizeDefinitionPLMID;
	}


	/**
	 * @return the updateRequest
	 */
	public boolean isUpdateRequest() {
		return updateRequest;
	}

	/**
	 * @param updateRequest
	 *            the updateRequest to set
	 */
	public void setUpdateRequest(boolean updateRequest) {
		this.updateRequest = updateRequest;
	}

	/**
	 * @return the isCreate
	 */
	public boolean isCreate() {
		return isCreate;
	}

	/**
	 * @param isCreate
	 *            the isCreate to set
	 */
	public void setCreate(boolean isCreate) {
		this.isCreate = isCreate;
	}

	/**
	 * @return the associatedSeason
	 */
	public LCSSeason getAssociatedSeason() {
		return associatedSeason;
	}

	/**
	 * @return the flexbomPart
	 */
	public FlexBOMPart getFlexbomPart() {
		return flexbomPart;
	}

	/**
	 * @param flexbomPart
	 *            the flexbomPart to set
	 */
	public void setFlexbomPart(FlexBOMPart flexbomPart) {
		this.flexbomPart = flexbomPart;
	}

	/**
	 * @param associatedSeason
	 *            the associatedSeason to set
	 */
	public void setAssociatedSeason(LCSSeason associatedSeason) {
		this.associatedSeason = associatedSeason;
	}

	/**
	 * @return the logEntryProcessor
	 */
	public SMBOMOutboundLogEntryProcessor getBomLogEntryProcessor() {
		return logEntryProcessor;
	}

	/**
	 * @return the logentryStatus
	 */
	public String getLogentryStatus() {
		return logentryStatus;
	}

	/**
	 * @param logentryStatus
	 *            the logentryStatus to set
	 */
	public void setLogentryStatus(String logentryStatus) {
		this.logentryStatus = logentryStatus;
	}

	/**
	 * @param logEntryProcessor
	 *            the logEntryProcessor to set
	 */
	public void setBomLogEntryProcessor(SMBOMOutboundLogEntryProcessor logEntryProcessor) {
		this.logEntryProcessor = logEntryProcessor;
	}

	/**
	 * @return the bomOutboundWS
	 */
	public BomWS getBOMOutboundWS() {

		((BindingProvider) bomOutboundWS).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT,
				SMBOMOutboundWebServiceConstants.BOM_OUTBOUND_INTEGRATION_TIMEOUT_IN_MINS * 60 * 1000);

		return bomOutboundWS;
	}

	/**
	 * @param bomOutboundWS
	 *            the bomOutboundWS to set
	 */
	public void setBOMOutboundWS(BomWS bomOutboundWS) {
		this.bomOutboundWS = bomOutboundWS;
	}

	/**
	 * @return the productSeasonLink
	 */
	public LCSProductSeasonLink getProductSeasonLink() {
		return productSeasonLink;
	}

	/**
	 * @param productSeasonLink
	 *            the productSeasonLink to set
	 */
	public void setProductSeasonLink(LCSProductSeasonLink productSeasonLink) {
		this.productSeasonLink = productSeasonLink;
	}

	/**
	 * @return the logentryUtil
	 */
	public SMBOMLogEntryUtill getBOMLogEntryUtill() {
		return logentryUtil;
	}

	/**
	 * @param logentryUtil
	 *            the logentryUtil to set
	 */
	public void setBOMLogEntryUtill(SMBOMLogEntryUtill logentryUtil) {
		this.logentryUtil = logentryUtil;
	}

	/**
	 * @return the bomUtill
	 */
	public SMBOMOutboundUtil getBomUtill() {
		return bomUtill;
	}

	/**
	 * @param bomUtill
	 *            the bomUtill to set
	 */
	public void setBomUtill(SMBOMOutboundUtil bomUtill) {
		this.bomUtill = bomUtill;
	}

	/**
	 * @return the bomProcessor
	 */
	public SMBOMOutboundDataProcessor getBomProcessor() {
		return bomProcessor;
	}

	/**
	 * @param bomProcessor
	 *            the bomProcessor to set
	 */
	public void setBomProcessor(SMBOMOutboundDataProcessor bomProcessor) {
		this.bomProcessor = bomProcessor;
	}

	/**
	 * @return the xmlUtill
	 */
	public SMBOMOutboundRequestXMLGenerator getXmlUtill() {
		return xmlUtill;
	}

	/**
	 * @param xmlUtill
	 *            the xmlUtill to set
	 */
	public void setXmlUtill(SMBOMOutboundRequestXMLGenerator xmlUtill) {
		this.xmlUtill = xmlUtill;
	}

	/**
	 * @return the bomOutboundRequestID
	 */
	public int getBOMOutboundRequestID() {
		return bomOutboundRequestID;
	}

	/**
	 * @param bomOutboundRequestID
	 *            the bomOutboundRequestID to set
	 */
	public void setBOMOutboundRequestID(int bomOutboundRequestID) {
		this.bomOutboundRequestID = bomOutboundRequestID;
	}

	/**
	 * @return the responseErrorReason
	 */
	public String getResponseErrorReason() {
		return responseErrorReason;
	}

	/**
	 * @param responseErrorReason
	 *            the responseErrorReason to set
	 */
	public void setResponseErrorReason(String responseErrorReason) {
		this.responseErrorReason = responseErrorReason;
	}

	/**
	 * @return the bomPartPLMID
	 */
	public String getBOMPartPLMID() {
		return bomPartPLMID;
	}

	/**
	 * @param bomPartPLMID
	 *            the bomPartPLMID to set
	 */
	public void setBOMPartPLMID(String bomPartPLMID) {
		this.bomPartPLMID = bomPartPLMID;
	}

	/**
	 * @return the bomPartOutboundLogEntry
	 */
	public Map<String, LCSLogEntry> getBomPartOutboundLogEntry() {
		return bomPartOutboundLogEntry;
	}

	/**
	 * @param bomPartOutboundLogEntry
	 *            the bomPartOutboundLogEntry to set
	 */
	public void setBomPartOutboundLogEntry(Map<String, LCSLogEntry> bomPartOutboundLogEntry) {
		this.bomPartOutboundLogEntry = bomPartOutboundLogEntry;
	}

	/**
	 * @return the bomHelper
	 */
	public SMBOMOutboundHelper getBomHelper() {
		return bomHelper;
	}

	/**
	 * @param bomHelper
	 *            the bomHelper to set
	 */
	public void setBomHelper(SMBOMOutboundHelper bomHelper) {
		this.bomHelper = bomHelper;
	}

	/**
	 * @return the bomOutboundResponse
	 */
	public BOMRequestResponse getBomOutboundResponse() {
		return bomOutboundResponse;
	}

	/**
	 * @param bomOutboundResponse
	 *            the bomOutboundResponse to set
	 */
	public void setBomOutboundResponse(BOMRequestResponse bomOutboundResponse) {
		this.bomOutboundResponse = bomOutboundResponse;
	}

	/**
	 * @return the bomPartValidationErrorMessage
	 */
	public String getBOMPartValidationErrorMessage() {
		return bomPartValidationErrorMessage;
	}

	/**
	 * @param productValidationErrorMessage
	 *            the productValidationErrorMessage to set
	 */
	public void setBOMPartValidationErrorMessage(String bomPartValidationErrorMessage) {
		this.bomPartValidationErrorMessage = bomPartValidationErrorMessage;
	}

}
