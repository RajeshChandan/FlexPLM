/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.bom.processor;

import java.util.Objects;

import org.apache.log4j.Logger;

import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogEntry;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.util.SMBOMOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.bom.util.SMBOMOutboundWebServiceConstants;

import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author ITC_Infotech
 * 
 */
public class SMBOMOutboundLogEntryProcessor {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogR.getLogger(SMBOMOutboundLogEntryProcessor.class.getName());
	/**
	 * constructor.
	 */
	public SMBOMOutboundLogEntryProcessor(){
		//protected constructor.
	}

	/**
	 * Set log entry for product season.
	 * @param plmID -String.
	 * @param requestType - String
	 * @param flexbomPart - FlexBOMPart.
	 * @throws WTException 
	 */
	public void setLogEntryForBOMOutboundIntegration(SMBOMOutboundIntegrationBean bean) {
		int flag = 0;
		String branchID = String.valueOf(bean.getFlexbomPart().getBranchIdentifier());
		LCSLogEntry logEntry = bean.getBOMLogEntryUtill().getLogentry(branchID);

		try {
			if (logEntry != null) {
				updateLogEntryForBOM(branchID, logEntry, bean);
				flag++;
			}
			if (flag == 0) {
				// check for create.
				createLogEntryForBOM(branchID, bean);
			}
		} catch (WTPropertyVetoException | WTException e) {
			LOGGER.error("", e);
		}
	}

	/**
	 * @param branchID
	 * @param bean
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void createLogEntryForBOM(String branchID, SMBOMOutboundIntegrationBean bean) throws WTException, WTPropertyVetoException {
		LOGGER.info("Creating log entry for BOMPart");
		FlexType bomOutboundLogEntryType = FlexTypeCache.getFlexTypeFromPath(SMBOMOutboundWebServiceConstants.LOG_ENTRY_BOM_OUT_BOUND_PATH);

		LCSLogEntry bomLogEntryObj = LCSLogEntry.newLCSLogEntry();

		bomLogEntryObj.setFlexType(bomOutboundLogEntryType);

		bomLogEntryObj.setValue(SMBOMOutboundWebServiceConstants.BOM_LOG_ENTRY_REQUEST_ID, String.valueOf(bean.getBOMOutboundRequestID()));
		bomLogEntryObj.setValue(SMBOMOutboundWebServiceConstants.BOM_LOG_ENTRY_PLM_ID, branchID);

		bomLogEntryObj.setValue(SMBOMOutboundWebServiceConstants.BOM_LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
		bomLogEntryObj.setValue(SMBOMOutboundWebServiceConstants.BOM_LOG_ENTRY_INTEGRATION_STATUS, bean.getLogentryStatus());

		String bomDetails = bean.getBomHelper().getBOMDetails(bean.getFlexbomPart());
		if (Objects.nonNull(bomDetails)) {
			bomLogEntryObj.setValue(SMBOMOutboundWebServiceConstants.BOM_LOG_ENTRY_OBJECT_DETAILS, bomDetails);
		}

		bean.getBomHelper().persistLogEntryObject(bomLogEntryObj);

		LOGGER.debug("Log Entry PLM ID >>>" + bomLogEntryObj.getValue(SMBOMOutboundWebServiceConstants.BOM_LOG_ENTRY_PLM_ID));
		LOGGER.debug("BOM log entry object >>>" + bomLogEntryObj);
	}

	/**
	 * Update Existing log entry for Flexbompart.
	 * @param branchID - String
	 * @param requestType - String
	 * @param entry - Map
	 * @param flexbomPart - FlexBOMPart
	 * @param bean - SMBOMOutboundIntegrationBean
	 * @throws WTException 
	 */
	public void updateLogEntryForBOM(String branchID, LCSLogEntry entry, SMBOMOutboundIntegrationBean bean)
			throws WTException, WTPropertyVetoException {

		LOGGER.info("Updating existing log Entry for BOM with plm ID :::::::::::::::::");

		entry.setValue(SMBOMOutboundWebServiceConstants.BOM_LOG_ENTRY_PLM_ID, branchID);
		entry.setValue(SMBOMOutboundWebServiceConstants.BOM_LOG_ENTRY_ERROR_REASON, bean.getResponseErrorReason());
		entry.setValue(SMBOMOutboundWebServiceConstants.BOM_LOG_ENTRY_INTEGRATION_STATUS, bean.getLogentryStatus());
		entry.setValue(SMBOMOutboundWebServiceConstants.BOM_LOG_ENTRY_REQUEST_ID, String.valueOf(bean.getBOMOutboundRequestID()));

		String bomDetails = bean.getBomHelper().getBOMDetails(bean.getFlexbomPart());
		if (Objects.nonNull(bomDetails)) {
			entry.setValue(SMBOMOutboundWebServiceConstants.BOM_LOG_ENTRY_OBJECT_DETAILS, bomDetails);
		}

		bean.getBomHelper().persistLogEntryObject(entry);
	}

}
