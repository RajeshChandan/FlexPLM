package com.sportmaster.wc.interfaces.webservices.outbound.product.util;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.sportmaster.wc.interfaces.webservices.outbound.product.helper.SMProductOutboundHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.processor.SMProductOutboundDataProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.product.processor.SMProductOutboundLogEntryProcessor;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductWS;
import com.sun.xml.ws.client.BindingProviderProperties;

public class SMProductOutboundIntegrationBean {


	public SMProductOutboundIntegrationBean() {
		super();
	}

	private boolean updateRequest;
	private boolean isCancelRequest;
	private boolean isCreate;
	private boolean lifecycleUpdate;
	private boolean productCancelledFlag;
	private boolean updateProductSeasonLink;
	private ProductWS prodOutboundWS;
	private LCSSeason associatedSeason;
	private LCSProductSeasonLink productSeasonLink;
	private SMLogEntryUtill logentryUtil;
	private SMProductOutboundUtil prodUtill;
	private SMProductOutboundHelper prodHelper;
	private SMProductOutboundDataProcessor prodProcessor;
	private SMProductOutboundRequestXMLGenerator xmlUtill;
	private SMProductOutboundLogEntryProcessor logEntryProcessor;
	private int productOutboundRequestID;
	private int colorwayOutboundRequestID;
	private int productSeasonOutboundRequestID;
	private int colorwaySeasonOutboundRequestID;
	private String lifecycleState;
	private String responseErrorReason;
	private String productSeasonLinkMDMID;
	private String productSeasonLinkPLMID;
	private String productValidationErrorMessage;
	private String productSeasonValidationErrorMessage;
	private String colorwayWSDLValidationErrorMessage;
	private String colorwaySeasonWSDLValidationErrorMessage;
	private String costSheetWSDLValidationErrorMessage;
	private String sourcingConfigWSDLValidationErrorMessage;
	private String productSeasonLifeCycleState;
	private Map<String, LCSLogEntry> productSeasonOutboundLogEntry;
	private Map<String, LCSLogEntry> productSeasonOutboundMDMIDLogEntry;
	private Map<String, LCSLogEntry> colorwaySeasonOutboundLogEntry;
	private Map<String, LCSLogEntry> productOutboundLogEntry;
	private Map<String, LCSLogEntry> colorwayOutboundLogEntry;

	/**
	 * @return the updateRequest
	 */
	public boolean isUpdateRequest() {
		return updateRequest;
	}

	/**
	 * @param updateRequest the updateRequest to set
	 */
	public void setUpdateRequest(boolean updateRequest) {
		this.updateRequest = updateRequest;
	}

	/**
	 * @return the isCancelRequest
	 */
	public boolean isCancelRequest() {
		return isCancelRequest;
	}

	/**
	 * @param isCancelRequest the isCancelRequest to set
	 */
	public void setCancelRequest(boolean isCancelRequest) {
		this.isCancelRequest = isCancelRequest;
	}

	/**
	 * @return the isCreate
	 */
	public boolean isCreate() {
		return isCreate;
	}

	/**
	 * @param isCreate the isCreate to set
	 */
	public void setCreate(boolean isCreate) {
		this.isCreate = isCreate;
	}

	/**
	 * @return the updateProductSeasonLink
	 */
	public boolean isUpdateProductSeasonLink() {
		return updateProductSeasonLink;
	}

	/**
	 * @param updateProductSeasonLink the updateProductSeasonLink to set
	 */
	public void setUpdateProductSeasonLink(boolean updateProductSeasonLink) {
		this.updateProductSeasonLink = updateProductSeasonLink;
	}

	/**
	 * @return the associatedSeason
	 */
	public LCSSeason getAssociatedSeason() {
		return associatedSeason;
	}

	/**
	 * @param associatedSeason the associatedSeason to set
	 */
	public void setAssociatedSeason(LCSSeason associatedSeason) {
		this.associatedSeason = associatedSeason;
	}

	/**
	 * @return the logEntryProcessor
	 */
	public SMProductOutboundLogEntryProcessor getLogEntryProcessor() {
		return logEntryProcessor;
	}

	/**
	 * @param logEntryProcessor the logEntryProcessor to set
	 */
	public void setLogEntryProcessor(SMProductOutboundLogEntryProcessor logEntryProcessor) {
		this.logEntryProcessor = logEntryProcessor;
	}

	/**
	 * @return the prodOutboundWS
	 */
	public ProductWS getProdOutboundWS() {
		
		((BindingProvider) prodOutboundWS).getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT,
				SMProductOutboundWebServiceConstants.PRODUCT_OUTBOUND_INTEGRATION_TIMEOUT_IN_MINS * 60 * 1000);

		return prodOutboundWS;
	}

	/**
	 * @param prodOutboundWS the prodOutboundWS to set
	 */
	public void setProdOutboundWS(ProductWS prodOutboundWS) {
		this.prodOutboundWS = prodOutboundWS;
	}

	/**
	 * @return the productSeasonLink
	 */
	public LCSProductSeasonLink getProductSeasonLink() {
		return productSeasonLink;
	}

	/**
	 * @param productSeasonLink the productSeasonLink to set
	 */
	public void setProductSeasonLink(LCSProductSeasonLink productSeasonLink) {
		this.productSeasonLink = productSeasonLink;
	}

	/**
	 * @return the logentryUtil
	 */
	public SMLogEntryUtill getLogentryUtil() {
		return logentryUtil;
	}

	/**
	 * @param logentryUtil the logentryUtil to set
	 */
	public void setLogentryUtil(SMLogEntryUtill logentryUtil) {
		this.logentryUtil = logentryUtil;
	}

	/**
	 * @return the prodUtill
	 */
	public SMProductOutboundUtil getProdUtill() {
		return prodUtill;
	}

	/**
	 * @param prodUtill the prodUtill to set
	 */
	public void setProdUtill(SMProductOutboundUtil prodUtill) {
		this.prodUtill = prodUtill;
	}

	/**
	 * @return the prodProcessor
	 */
	public SMProductOutboundDataProcessor getProdProcessor() {
		return prodProcessor;
	}

	/**
	 * @param prodProcessor the prodProcessor to set
	 */
	public void setProdProcessor(SMProductOutboundDataProcessor prodProcessor) {
		this.prodProcessor = prodProcessor;
	}

	/**
	 * @return the xmlUtill
	 */
	public SMProductOutboundRequestXMLGenerator getXmlUtill() {
		return xmlUtill;
	}

	/**
	 * @return the productSeasonLifeCycleState
	 */
	public String getProductSeasonLifeCycleState() {
		return productSeasonLifeCycleState;
	}

	/**
	 * @param productSeasonLifeCycleState the productSeasonLifeCycleState to set
	 */
	public void setProductSeasonLifeCycleState(String productSeasonLifeCycleState) {
		this.productSeasonLifeCycleState = productSeasonLifeCycleState;
	}

	/**
	 * @param xmlUtill the xmlUtill to set
	 */
	public void setXmlUtill(SMProductOutboundRequestXMLGenerator xmlUtill) {
		this.xmlUtill = xmlUtill;
	}

	/**
	 * @return the productOutboundRequestID
	 */
	public int getProductOutboundRequestID() {
		return productOutboundRequestID;
	}

	/**
	 * @param productOutboundRequestID the productOutboundRequestID to set
	 */
	public void setProductOutboundRequestID(int productOutboundRequestID) {
		this.productOutboundRequestID = productOutboundRequestID;
	}

	/**
	 * @return the colorwayOutboundRequestID
	 */
	public int getColorwayOutboundRequestID() {
		return colorwayOutboundRequestID;
	}

	/**
	 * @param colorwayOutboundRequestID the colorwayOutboundRequestID to set
	 */
	public void setColorwayOutboundRequestID(int colorwayOutboundRequestID) {
		this.colorwayOutboundRequestID = colorwayOutboundRequestID;
	}

	/**
	 * @return the productSeasonOutboundRequestID
	 */
	public int getProductSeasonOutboundRequestID() {
		return productSeasonOutboundRequestID;
	}

	/**
	 * @param productSeasonOutboundRequestID the productSeasonOutboundRequestID to
	 *                                       set
	 */
	public void setProductSeasonOutboundRequestID(int productSeasonOutboundRequestID) {
		this.productSeasonOutboundRequestID = productSeasonOutboundRequestID;
	}

	/**
	 * @return the colorwaySeasonOutboundRequestID
	 */
	public int getColorwaySeasonOutboundRequestID() {
		return colorwaySeasonOutboundRequestID;
	}

	/**
	 * @param colorwaySeasonOutboundRequestID the colorwaySeasonOutboundRequestID to
	 *                                        set
	 */
	public void setColorwaySeasonOutboundRequestID(int colorwaySeasonOutboundRequestID) {
		this.colorwaySeasonOutboundRequestID = colorwaySeasonOutboundRequestID;
	}

	/**
	 * @return the responseErrorReason
	 */
	public String getResponseErrorReason() {
		return responseErrorReason;
	}

	/**
	 * @param responseErrorReason the responseErrorReason to set
	 */
	public void setResponseErrorReason(String responseErrorReason) {
		this.responseErrorReason = responseErrorReason;
	}

	/**
	 * @return the productSeasonLinkMDMID
	 */
	public String getProductSeasonLinkMDMID() {
		return productSeasonLinkMDMID;
	}

	/**
	 * @param productSeasonLinkMDMID the productSeasonLinkMDMID to set
	 */
	public void setProductSeasonLinkMDMID(String productSeasonLinkMDMID) {
		this.productSeasonLinkMDMID = productSeasonLinkMDMID;
	}

	/**
	 * @return the productSeasonLinkPLMID
	 */
	public String getProductSeasonLinkPLMID() {
		return productSeasonLinkPLMID;
	}

	/**
	 * @param productSeasonLinkPLMID the productSeasonLinkPLMID to set
	 */
	public void setProductSeasonLinkPLMID(String productSeasonLinkPLMID) {
		this.productSeasonLinkPLMID = productSeasonLinkPLMID;
	}

	/**
	 * @return the colorwaySeasonWSDLValidationErrorMessage
	 */
	public String getColorwaySeasonWSDLValidationErrorMessage() {
		return colorwaySeasonWSDLValidationErrorMessage;
	}

	/**
	 * @param colorwaySeasonWSDLValidationErrorMessage the
	 *                                                 colorwaySeasonWSDLValidationErrorMessage
	 *                                                 to set
	 */
	public void setColorwaySeasonWSDLValidationErrorMessage(String colorwaySeasonWSDLValidationErrorMessage) {
		this.colorwaySeasonWSDLValidationErrorMessage = colorwaySeasonWSDLValidationErrorMessage;
	}

	/**
	 * @return the productSeasonOutboundLogEntry
	 */
	public Map<String, LCSLogEntry> getProductSeasonOutboundLogEntry() {
		return productSeasonOutboundLogEntry;
	}

	/**
	 * @param productSeasonOutboundLogEntry the productSeasonOutboundLogEntry to set
	 */
	public void setProductSeasonOutboundLogEntry(Map<String, LCSLogEntry> productSeasonOutboundLogEntry) {
		this.productSeasonOutboundLogEntry = productSeasonOutboundLogEntry;
	}

	/**
	 * @return the productSeasonOutboundMDMIDLogEntry
	 */
	public Map<String, LCSLogEntry> getProductSeasonOutboundMDMIDLogEntry() {
		return productSeasonOutboundMDMIDLogEntry;
	}

	/**
	 * @param productSeasonOutboundMDMIDLogEntry the
	 *                                           productSeasonOutboundMDMIDLogEntry
	 *                                           to set
	 */
	public void setProductSeasonOutboundMDMIDLogEntry(Map<String, LCSLogEntry> productSeasonOutboundMDMIDLogEntry) {
		this.productSeasonOutboundMDMIDLogEntry = productSeasonOutboundMDMIDLogEntry;
	}

	/**
	 * @return the colorwaySeasonOutboundLogEntry
	 */
	public Map<String, LCSLogEntry> getColorwaySeasonOutboundLogEntry() {
		return colorwaySeasonOutboundLogEntry;
	}

	/**
	 * @param colorwaySeasonOutboundLogEntry the colorwaySeasonOutboundLogEntry to
	 *                                       set
	 */
	public void setColorwaySeasonOutboundLogEntry(Map<String, LCSLogEntry> colorwaySeasonOutboundLogEntry) {
		this.colorwaySeasonOutboundLogEntry = colorwaySeasonOutboundLogEntry;
	}

	/**
	 * @return the productOutboundLogEntry
	 */
	public Map<String, LCSLogEntry> getProductOutboundLogEntry() {
		return productOutboundLogEntry;
	}

	/**
	 * @param productOutboundLogEntry the productOutboundLogEntry to set
	 */
	public void setProductOutboundLogEntry(Map<String, LCSLogEntry> productOutboundLogEntry) {
		this.productOutboundLogEntry = productOutboundLogEntry;
	}

	/**
	 * @return the colorwayOutboundLogEntry
	 */
	public Map<String, LCSLogEntry> getColorwayOutboundLogEntry() {
		return colorwayOutboundLogEntry;
	}

	/**
	 * @param colorwayOutboundLogEntry the colorwayOutboundLogEntry to set
	 */
	public void setColorwayOutboundLogEntry(Map<String, LCSLogEntry> colorwayOutboundLogEntry) {
		this.colorwayOutboundLogEntry = colorwayOutboundLogEntry;
	}

	/**
	 * @return the prodHelper
	 */
	public SMProductOutboundHelper getProdHelper() {
		return prodHelper;
	}

	/**
	 * @return the lifecycleUpdate
	 */
	public boolean isLifecycleUpdate() {
		return lifecycleUpdate;
	}

	/**
	 * @param lifecycleUpdate the lifecycleUpdate to set
	 */
	public void setLifecycleUpdate(boolean lifecycleUpdate) {
		this.lifecycleUpdate = lifecycleUpdate;
	}

	/**
	 * @return the lifecycleState
	 */
	public String getLifecycleState() {
		return lifecycleState;
	}

	/**
	 * @param lifecycleState the lifecycleState to set
	 */
	public void setLifecycleState(String lifecycleState) {
		this.lifecycleState = lifecycleState;
	}

	/**
	 * @param prodHelper the prodHelper to set
	 */
	public void setProdHelper(SMProductOutboundHelper prodHelper) {
		this.prodHelper = prodHelper;
	}

	/**
	 * @return the productCancelledFlag
	 */
	public boolean isProductCancelledFlag() {
		return productCancelledFlag;
	}

	/**
	 * @param productCancelledFlag the productCancelledFlag to set
	 */
	public void setProductCancelledFlag(boolean productCancelledFlag) {
		this.productCancelledFlag = productCancelledFlag;
	}

	/**
	 * @return the colorwayWSDLValidationErrorMessage
	 */
	public String getColorwayWSDLValidationErrorMessage() {
		return colorwayWSDLValidationErrorMessage;
	}

	/**
	 * @param colorwayWSDLValidationErrorMessage the
	 *                                           colorwayWSDLValidationErrorMessage
	 *                                           to set
	 */
	public void setColorwayWSDLValidationErrorMessage(String colorwayWSDLValidationErrorMessage) {
		this.colorwayWSDLValidationErrorMessage = colorwayWSDLValidationErrorMessage;
	}

	/**
	 * @return the costSheetWSDLValidationErrorMessage
	 */
	public String getCostSheetWSDLValidationErrorMessage() {
		return costSheetWSDLValidationErrorMessage;
	}

	/**
	 * @param costSheetWSDLValidationErrorMessage the
	 *                                            costSheetWSDLValidationErrorMessage
	 *                                            to set
	 */
	public void setCostSheetWSDLValidationErrorMessage(String costSheetWSDLValidationErrorMessage) {
		this.costSheetWSDLValidationErrorMessage = costSheetWSDLValidationErrorMessage;
	}

	/**
	 * @return the sourcingConfigWSDLValidationErrorMessage
	 */
	public String getSourcingConfigWSDLValidationErrorMessage() {
		return sourcingConfigWSDLValidationErrorMessage;
	}

	/**
	 * @param sourcingConfigWSDLValidationErrorMessage the
	 *                                                 sourcingConfigWSDLValidationErrorMessage
	 *                                                 to set
	 */
	public void setSourcingConfigWSDLValidationErrorMessage(String sourcingConfigWSDLValidationErrorMessage) {
		this.sourcingConfigWSDLValidationErrorMessage = sourcingConfigWSDLValidationErrorMessage;
	}

	/**
	 * @return the productValidationErrorMessage
	 */
	public String getProductValidationErrorMessage() {
		return productValidationErrorMessage;
	}

	/**
	 * @param productValidationErrorMessage the productValidationErrorMessage to set
	 */
	public void setProductValidationErrorMessage(String productValidationErrorMessage) {
		this.productValidationErrorMessage = productValidationErrorMessage;
	}

	/**
	 * @return the productSeasonValidationErrorMessage
	 */
	public String getProductSeasonValidationErrorMessage() {
		return productSeasonValidationErrorMessage;
	}

	/**
	 * @param productSeasonValidationErrorMessage the
	 *                                            productSeasonValidationErrorMessage
	 *                                            to set
	 */
	public void setProductSeasonValidationErrorMessage(String productSeasonValidationErrorMessage) {
		this.productSeasonValidationErrorMessage = productSeasonValidationErrorMessage;
	}

}
