package com.sportmaster.wc.sample;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleQuery;
import com.lcs.wc.sample.LCSSampleRequest;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.UserGroupHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.utils.SMEmailHelper;

import wt.fc.ObjectNoLongerExistsException;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTPropertyVetoException;
import wt.workflow.engine.StandardWfEngineService;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.engine.WfTransition;

/**
 * SMProductSampleWorkflowPlugin.
 * 
 * @author 'true'
 * @version 'true' 1.0
 * 
 */
public class SMProductSampleWorkflowPlugin {

	/**
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMProductSampleWorkflowPlugin.class);
	/**
	 * Sample Status.
	 */
	public static final String SAMPLE_STATUS = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleStatus");
	/**
	 * Sample Status - Requested.
	 */
	public static final String SAMPLE_STATUS_REQUESTED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleStatus.vrdRequested");
	/**
	 * LCS - In Work.
	 */
	public static final String LCS_INWORK = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.lcsInWork");
	/**
	 * Supplier Status.
	 */
	public static final String SUPPLIER_STATUS = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.supplierStatus");
	/**
	 * Sample Task Received By Supplier.
	 */
	public static final String SAMPLE_TASK_RECEIVED_BY_SUPPLIER = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleTaskReceivedBySupplier");
	/**
	 * Supplier Status - Received.
	 */
	public static final String SUPPLIER_STATUS_RECEIVED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.smSampleSupplierStatus.smReceived");
	/**
	 * LCS - Accept Sample.
	 */
	public static final String LCS_ACCEPTSAMPLE = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.lcsAcceptSample");
	/**
	 * Ship Date.
	 */
	public static final String SHIP_DATE = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.shipDate");
	/**
	 * Supplier Status - Shipped.
	 */
	public static final String SUPPLIER_STATUS_SHIPPED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.smSampleSupplierStatus.smShipped");
	/**
	 * LCS - Ship Sample.
	 */
	public static final String LCS_SHIP_SAMPLE = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.lcsShipSample");
	/**
	 * Sample Submitted For Review.
	 */
	public static final String SAMPLE_SUBMITTED_FOR_REVIEW = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleSubmittedForReview");
	/**
	 * Sample Status - Submitted For Review.
	 */
	public static final String SAMPLE_STATUS_SUBMITTEDFORREVIEW = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleStatus.submittedForReview");
	/**
	 * LCS - Submit For Review.
	 */
	public static final String LCS_SUBMIT_FOR_REVIEW = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.lcsSubmitForReview");
	/**
	 * Sample Evaluated Date.
	 */
	public static final String SAMPLE_EVALUATED_DATE = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleEvaluatedDate");
	/**
	 * Sample Status - Approved.
	 */
	public static final String SAMPLE_STATUS_APPROVED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleStatus.vrdApproved");
	/**
	 * Sample Status - Conditionally Approved.
	 */
	public static final String SAMPLE_STATUS_CONDITIONALLY_APPROVED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleStatus.vrdConditionallyApproved");
	/**
	 * LCS - Closed.
	 */
	public static final String LCS_CLOSED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.lcsClosed");
	/**
	 * Sample Status - Rejected -> Dropped.
	 */
	public static final String SAMPLE_STATUS_REJECTED_DROPPED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleStatus.vrdRejectedDropped");
	/**
	 * Sample Status - Canceled.
	 */
	public static final String SAMPLE_STATUS_CANCELED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleStatus.smCanceled");
	/**
	 * sample Request Status.
	 */
	public static final String SAMPLE_REQUEST_STATUS = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleRequestStatus");
	/**
	 * Request Close Date.
	 */
	public static final String REQUEST_CLOSE_DATE = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.requestCloseDate");
	/**
	 * Request Status - Closed.
	 */
	public static final String REQUEST_STATUS_CLOSED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleRequestStatus.requestStatusClosed");
	/**
	 * Sample Status - Rejected -> Resubmit.
	 */
	public static final String SAMPLE_STATUS_REJECTED_RESUBMIT = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleStatus.vrdRejectedResubmit");
	/**
	 * LCS - Requested.
	 */
	public static final String LCS_REQUESTED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.lcsRequested");
	/**
	 * lcsTaskAccepted.
	 */
	public static final String LCS_TASK_ACCEPTED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.lcsTaskAccepted");
	/**
	 * lcsSampleFinished.
	 */
	public static final String LCS_SAMPLE_FINISHED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.lcsSampleFinished");
	/**
	 * lcsSubmittedForReview.
	 */
	public static final String LCS_SUBMITTED_FOR_REVIEW = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.lcsSubmittedForReview");
	/**
	 * Sample Status - To Be Revised.
	 */
	public static final String SAMPLE_STATUS_TO_BE_REVISED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleStatus.toBeRevised");
	/**
	 * Request Open Date.
	 */
	public static final String REQUEST_OPEN_DATE = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.requestOpenDate");
	/**
	 * Request Status - Open.
	 */
	public static final String REQUEST_STATUS_OPEN = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleRequestStatus.requestStatusOpen");
	/**
	 * Request Status - Cancelled.
	 */
	public static final String REQUEST_STATUS_CANCELLED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleRequestStatus.requestStatusCancelled");
	/**
	 * Supplier status Footwear - Sample Finished.
	 */
	public static final String SUPPLIER_STATUS_SAMPLE_FINISHED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.smSampleSupplierStatus.sampleFinished");
	/**
	 * Apparel Product Sample Type.
	 */
	public static final String APPAREL_PRODUCT_SAMPLE_TYPE = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.apparelProductSampleType");
	/**
	 * Footwear Product Sample Type.
	 */
	public static final String FOOTWEAR_PRODUCT_SAMPLE_TYPE = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.footwearProductSampleType");
	/**
	 * Supplier status Apparel - Sample Finished.
	 */
	public static final String SUPPLIER_STATUS_APPAREL_SAMPLE_FINISHED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.smSampleSupplierStatus.smSampleFinished");
	/**
	 * Sourcing Config - Business Supplier.
	 */
	public static final String BUSINESS_SUPPLIER = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.businessSupplier");
	/**
	 * Supplier Vendor Group.
	 */
	public static final String SUPPLIER_VENDOR_GROUP = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.supplierVendorGroup");
	/**
	 * Apparel Product Sample LC Template Name.
	 */
	public static final String APPAREL_PRODUCT_SAMPLE_LC_TEMPLATE_NAME = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.apparelProductSampleLCTemplateName");
	/**
	 * Footwear Product Sample LC Template Name.
	 */
	public static final String FOOTWEAR_PRODUCT_SAMPLE_LC_TEMPLATE_NAME = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.footwearProductSampleLCTemplateName");
	/**
	 * PSL - Developer OSO.
	 */
	public static final String PSL_DEVELOPER_OSO = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.pSLDeveloperOSO");
	/**
	 * Sample Finished Date.
	 */
	public static final String SAMPLE_FINISHED_DATE = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleFinishedDate");
	/**
	 * Flex Object Name.
	 */
	public static final String FO_NAME = LCSProperties
			.get("com.sportmaster.wc.product.SMProductSampleWorkflowPlugin.fOName", "NAME");
	// Phase 13 - SEPD Product Sample - Start
	/**
	 * SEPD Product Sample Type.
	 */
	public static final String SEPD_PRODUCT_SAMPLE_TYPE = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sepdProductSampleType");
	/**
	 * SEPD Product Sample Type.
	 */
	public static final String SEPD_PRODUCT_SAMPLE_LC_TEMPLATE_NAME = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sepdProductSampleLCTemplateName");
	/**
	 * SEPD LCS - Requested.
	 */
	public static final String LCS_REQUESTED_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.lcsRequested");
	/**
	 * SEPD LCS - Task Accepted.
	 */
	public static final String LCS_TASK_ACCEPTED_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.lcsTaskAccepted");
	/**
	 * SEPD LCS - Sample Produced.
	 */
	public static final String LCS_SAMPLE_PRODUCED_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.lcsSampleProduced");
	/**
	 * SEPD LCS - Sample Checked.
	 */
	public static final String LCS_SAMPLE_CHECKED_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.lcsSampleChecked");
	/**
	 * SEPD LCS - Closed.
	 */
	public static final String LCS_CLOSED_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.lcsClosed");
	/**
	 * SEPD Sample Status - Requested.
	 */
	public static final String SAMPLE_STATUS_REQUESTED_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.sampleStatus.vrdRequested");
	/**
	 * SEPD Supplier Sample Status - Received.
	 */
	public static final String SUPPLIER_STATUS_RECEIVED_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.smSampleSupplierStatus.smReceived");
	/**
	 * SEPD Supplier Sample Status - Sample Finished.
	 */
	public static final String SUPPLIER_STATUS_SAMPLE_FINISHED_SEPD = LCSProperties.get(
			"com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.smSampleSupplierStatus.smSampleFinished");
	/**
	 * SEPD Sample Status - Approved.
	 */
	public static final String SAMPLE_STATUS_APPROVED_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.sampleStatus.vrdApproved");
	/**
	 * SEPD Sample Status - Canceled.
	 */
	public static final String SAMPLE_STATUS_CANCELED_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.sampleStatus.smCanceled");
	/**
	 * SEPD Sample Status - To Be Revised.
	 */
	public static final String SAMPLE_STATUS_TO_BE_REVISED_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.sampleStatus.toBeRevised");
	/**
	 * SEPD - Sample Confirmation.
	 */
	public static final String SAMPLE_CONFIRMATION_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.smSampleConfirmationList");
	/**
	 * SEPD Sample Confirmation - Confirmed.
	 */
	public static final String SAMPLE_CONFIRMATION_LIST_CONFIRMED_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.smSampleConfirmationList.smConfirmed");
	/**
	 * SEPD Sample Confirmation - Pending.
	 */
	public static final String SAMPLE_CONFIRMATION_LIST_PENDING_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.smSampleConfirmationList.smPending");
	/**
	 * SEPD Sample Confirmation - Rejected.
	 */
	public static final String SAMPLE_CONFIRMATION_LIST_REJECTED_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.smSampleConfirmationList.smRejected");
	/**
	 * SEPD Sample Request Type.
	 */
	public static final String SAMPLE_REQUEST_TYPE_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.sampleRequest.vrdRequestType");
	/**
	 * SEPD Sample Request Type - PPS.
	 */
	public static final String SAMPLE_REQUEST_TYPE_PPS_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.sampleRequest.vrdRequestType.smPPS");
	/**
	 * SEPD Sample Task Received by Supplier.
	 */
	public static final String SAMPLE_TASK_RECEIVED_BY_SUPPLIER_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.smSampleTaskReceivedBySupplier");
	/**
	 * SEPD Sample Finished FACT.
	 */
	public static final String SAMPLE_FINISHED_FACT_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.smSampleFinishedFACT");
	/**
	 * SEPD Sample Evaluated Date.
	 */
	public static final String SAMPLE_EVALUATED_DATE_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.smSampleEvaluatedDate");
	/**
	 * SEPD Sample Request Status.
	 */
	public static final String REQUEST_STATUS_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.sampleRequest.sampleRequestStatus");
	/**
	 * SEPD Sample Request Close Date.
	 */
	public static final String REQUEST_CLOSE_DATE_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.sampleRequest.smRequestCloseDate");
	/**
	 * SEPD Request Status - Cancelled.
	 */
	public static final String REQUEST_STATUS_CANCELLED_SEPD = LCSProperties.get(
			"com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.sampleRequestStatus.requestStatusCancelled");
	/**
	 * SEPD Request Status - Closed.
	 */
	public static final String REQUEST_STATUS_CLOSED_SEPD = LCSProperties.get(
			"com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.sampleRequestStatus.requestStatusClosed");
	/**
	 * SEPD Sample Status - Submitted For Review.
	 */
	public static final String SAMPLE_STATUS_SUBMITTED_FOR_REVIEW_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.sampleStatus.vrdSubmittedForReview");
	/**
	 * SEPD Supplier Sample Status - Shipped.
	 */
	public static final String SUPPLIER_STATUS_SHIPPED_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.smSampleSupplierStatus.smShipped");
	/**
	 * SEPD Sample Sent to HQ FACT.
	 */
	public static final String SAMPLE_SENT_TO_HQ_FACT_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.vrdShipDate");
	/**
	 * SEPD Sample Received HQ FACT.
	 */
	public static final String SAMPLE_RECEIVED_HQ_FACT_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.smReceivedInHQ");
	/**
	 * SEPD Sample Checked FACT.
	 */
	public static final String SAMPLE_CHECKED_FACT = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.smSampleConfirmedFact");
	/**
	 * Product Type - ACC\SEPD.
	 */
	public static final String PRODUCT_TYPE_ACC_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.accSEPDProductType");
	/**
	 * Product Type - SEPD.
	 */
	public static final String PRODUCT_TYPE_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.sepdProductType");
	/**
	 * Sourcing Config - OSO Developer Apparel.
	 */
	public static final String OSO_DEVELOPER_APPAREL = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.smOSOAppDeveloper", "smOSOAppDeveloper");
	/**
	 * Sourcing Config - OSO Developer SEPD.
	 */
	public static final String OSO_DEVELOPER_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.smOSODeveloper", "smOSODeveloper");
	/**
	 * Sourcing Config Type - Apparel.
	 */
	public static final String APPAREL_SOURCE_TYPE = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.apparelSourceType", "Apparel");
	/**
	 * Sourcing Config - SEPD.
	 */
	public static final String SEPD_SOURCE_TYPE = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sepdSourceType", "smSportsEquipement");
	/**
	 * LCSSAMPLE.IDA2A2 - SEPD.
	 */
	public static final String LCSSAMPLE_IDA2A2 = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.sampleIDA2A2");
	/**
	 * Sample Object ID.
	 */
	public static final String SEPD_SAMPLE_OBJECT_ID = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.samplObjectID");
	/**
	 * Season Requested - SEPD.
	 */
	public static final String SEPD_SEASON_REQUESTED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.vrdSeasonRequested");
	/**
	 * Colorway - SEPD.
	 */
	public static final String SEPD_COLORWAY = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.SEPD.vrdColorwayRef");
	/**
	 * Email Body 1.
	 */
	public static final String EMAIL_BODY_1 = LCSProperties
			.get("com.sportmaster.wc.product.SMProductSampleWorkflowPlugin.emailBody1");
	/**
	 * Email Body 2.
	 */
	public static final String EMAIL_BODY_2 = LCSProperties
			.get("com.sportmaster.wc.product.SMProductSampleWorkflowPlugin.emailBody2");
	// Phase 13 - SEPD Product Sample - End

	/**
	 * Constructor.
	 */
	protected SMProductSampleWorkflowPlugin() {
		// protected constructor
	}

	/**
	 * setCurrentDate.
	 * 
	 * @return Date.
	 */
	public static Date setCurrentDate() {
		Date currentDate = new Date();
		// getting MSK timezone
		TimeZone tz = TimeZone.getTimeZone(LCSProperties.get("com.lcs.wc.util.FormatHelper.STANDARD_TIMEZONE"));
		SimpleDateFormat converter = new SimpleDateFormat("dd/MM/yyyy");
		converter.setTimeZone(tz);
		converter.format(currentDate);
		return currentDate;
	}

	/**
	 * getProduct.
	 * 
	 * @param sampleObj for sampleObj.
	 * 
	 * @return LCSProduct.
	 * @throws WTException.
	 */
	public static LCSProduct getProduct(LCSSampleRequest sampleReqObj) throws WTException {

		// Getting Season.
		LCSSeason seasonObj = (LCSSeason) sampleReqObj.getValue("vrdSeasonRequested");

		// Getting part master.
		LCSPartMaster master = (LCSPartMaster) sampleReqObj.getOwnerMaster();

		// Getting ProductARev Object.
		LCSProduct prodAObj = SeasonProductLocator.getProductARev(master);

		// Get Season Product Link
		LCSSeasonProductLink spLink = LCSSeasonQuery.findSeasonProductLink(prodAObj, seasonObj);

		LCSProduct prodRev = null;

		if (spLink != null && !(spLink.isSeasonRemoved())) {
			// Getting Product Object.
			prodRev = SeasonProductLocator.getProductSeasonRev(spLink);
			return prodRev;
		}
		return null;
	}

	/**
	 * setSampleLCS.
	 * 
	 * @param sampleObj for sampleObj.
	 * @param lcsState  for lcsState.
	 * @return void.
	 */
	public static void setSampleLCS(LCSSample sampleObj, String lcStateName) {
		LOGGER.debug("start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setSampleLCS");
		try {
			// Bypass the security to change state as the users usually don't have access to
			// Change State from UI
			final boolean old_enforced = SessionServerHelper.manager.setAccessEnforced(false);
			try {
				// Setting last param as true to auto complete the WF task
				LifeCycleHelper.service.setLifeCycleState(sampleObj, State.toState(lcStateName), true);
			} finally {
				SessionServerHelper.manager.setAccessEnforced(old_enforced);
			}
		} catch (LifeCycleException e) {
			LOGGER.error("LifeCycleException in setSampleLCS method: " + e.getMessage());
			e.printStackTrace();
		} catch (WTInvalidParameterException e) {
			LOGGER.error("WTInvalidParameterException in setSampleLCS method: " + e.getMessage());
			e.printStackTrace();
		} catch (WTException e) {
			LOGGER.error("WTException in setSampleLCS method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.debug("end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setSampleLCS");
	}

	/**
	 * setSampleLCSShipSampleAPD.
	 * 
	 * @param sampleObj for sampleObj.
	 * @param lcsState  for lcsState.
	 * @return void.
	 */
	public static void setSampleLCSShipSampleAPD(LCSSample sampleObj, String lcStateName) {
		LOGGER.debug("start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setSampleLCSShipSampleAPD");
		try {
			// Bypass the security to change state as the users usually don't have access to
			// Change State from UI
			final boolean old_enforced = SessionServerHelper.manager.setAccessEnforced(false);
			try {
				LifeCycleHelper.service.setLifeCycleState(sampleObj, State.toState(lcStateName));

				StandardWfEngineService service = new StandardWfEngineService();
				Enumeration processes = service.getAssociatedProcesses(sampleObj, WfState.OPEN_RUNNING);

				// Auto complete the current WF task
				while (processes.hasMoreElements()) {
					WfProcess process = (WfProcess) processes.nextElement();
					service.changeState(process, WfTransition.TERMINATE);
				}
			} finally {
				SessionServerHelper.manager.setAccessEnforced(old_enforced);
			}
		} catch (WTInvalidParameterException e) {
			LOGGER.error("WTInvalidParameterException in setSampleLCSShipSampleAPD method: " + e.getMessage());
			e.printStackTrace();
		} catch (LifeCycleException e) {
			LOGGER.error("LifeCycleException in setSampleLCSShipSampleAPD method: " + e.getMessage());
			e.printStackTrace();
		} catch (WTException e) {
			LOGGER.error("WTException in setSampleLCSShipSampleAPD method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.debug("end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setSampleLCSShipSampleAPD");
	}

	/**
	 * setProductSampleLCS.
	 * 
	 * @param object for object.
	 * @return void.
	 */
	public static void setProductSampleLCS(WTObject object) {
		LOGGER.info("start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleLCS");

		try {
			// Getting Sample object.
			LCSSample sampleObj = (LCSSample) object;

			String sampleStatus = (String) sampleObj.getValue(SAMPLE_STATUS);
			String supplierStatus = (String) sampleObj.getValue(SUPPLIER_STATUS);

			// Phase 13 Start - SEPD Product Sample Workflow - Getting Product object.
			// Getting Product Object.
			LCSProduct prodObj = getProduct(sampleObj.getSampleRequest());
			// Phase 13 End - SEPD Product Sample Workflow - Getting Product object.

			// Getting current LC Template name.
			String currentTemplateName = sampleObj.getLifeCycleName();

			LOGGER.debug("In setProductSampleLCS Method - sampleObj = " + sampleObj);
			LOGGER.debug("In setProductSampleLCS Method - sampleStatus = " + sampleStatus);
			LOGGER.debug("In setProductSampleLCS Method - supplierStatus = " + supplierStatus);
			LOGGER.debug("In setProductSampleLCS Method - currentTemplateName = " + currentTemplateName);

			// Loop to set product sample LC state based on Product Sample Type.
			if (isValidAPDSampleType(sampleObj.getFlexType().getFullName(), currentTemplateName, prodObj)) {

				// Method to set Apparel Product Sample LCS.
				setProductSampleApparelLCS(sampleObj, sampleStatus, supplierStatus);

			} else if (isValidFPDSampleType(sampleObj.getFlexType().getFullName(), currentTemplateName, prodObj)) {

				// Method to set Footwear Product Sample LCS.
				setProductSampleFootwearLCS(sampleObj, sampleStatus, supplierStatus);

			}
			// Phase 13 Start - SEPD Product Sample Workflow - setProductSampleLCS.
			else if (prodObj != null
					&& (prodObj.getFlexType().getFullName().startsWith(PRODUCT_TYPE_ACC_SEPD)
							|| prodObj.getFlexType().getFullName().startsWith(PRODUCT_TYPE_SEPD))
					&& sampleObj.getFlexType().getFullName().startsWith(SEPD_PRODUCT_SAMPLE_TYPE)
					&& FormatHelper.hasContent(currentTemplateName)
					&& currentTemplateName.equals(SEPD_PRODUCT_SAMPLE_LC_TEMPLATE_NAME)) {
				LOGGER.debug("In setProductSampleLCS Method - Product Type = " + prodObj.getFlexType().getFullName());
				// Method to set SEPD Product Sample LCS.
				setProductSampleSEPDLCS(sampleObj, sampleStatus, supplierStatus);
			}
			// Phase 13 End - SEPD Product Sample Workflow - setProductSampleLCS.
		} catch (WTException e) {
			LOGGER.error("WTException in setProductSampleLCS method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleLCS");
	}

	/**
	 * setProductSampleApparelLCS.
	 * 
	 * @param sampleObj      for sampleObj.
	 * @param sampleStatus   for sampleStatus.
	 * @param supplierStatus for supplierStatus.
	 * @return void.
	 */
	public static void setProductSampleApparelLCS(LCSSample sampleObj, String sampleStatus, String supplierStatus) {
		LOGGER.debug("start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleApparelLCS");

		// Getting current LCS of Apparel Product Sample.
		String currentLCS = sampleObj.getState().getState().toString();

		LOGGER.debug("In setProductSampleApparelLCS Method - currentLCS = " + currentLCS);

		// Enters this loop when Sample status value is Requested.
		if (!FormatHelper.hasContent(supplierStatus) && !currentLCS.equals(LCS_INWORK)
				&& FormatHelper.hasContent(sampleStatus) && sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_REQUESTED)) {
			// Setting Product Sample Lifecycle state as In Work.
			setSampleLCS(sampleObj, LCS_INWORK);
		}

		// Enters this loop when Sample status is Requested and Supplier status is
		// Received.
		if (sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_REQUESTED) && !currentLCS.equals(LCS_ACCEPTSAMPLE)
				&& FormatHelper.hasContent(supplierStatus)
				&& supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_RECEIVED)) {
			// Setting Product Sample Lifecycle state as Accept Sample.
			setSampleLCS(sampleObj, LCS_ACCEPTSAMPLE);
		}

		// Enters this loop when Sample status is Requested and Supplier status is
		// Shipped.
		if (sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_REQUESTED) && !currentLCS.equals(LCS_SHIP_SAMPLE)
				&& FormatHelper.hasContent(supplierStatus)
				&& supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_SHIPPED)) {
			// Setting Product Sample Lifecycle state as Ship Sample.
			// Calling below method, that will terminate the running WF task first, and then
			// change the LC State to Ship Sample,
			// without terminate the task was visible for the user even after the LC State
			// is changed.
			setSampleLCSShipSampleAPD(sampleObj, LCS_SHIP_SAMPLE);
		}

		// Enters this loop when Sample status is Submitted for review.
		if (!currentLCS.equals(LCS_SUBMIT_FOR_REVIEW) && FormatHelper.hasContent(sampleStatus)
				&& sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_SUBMITTEDFORREVIEW)) {
			// Setting Product Sample Lifecycle state as Submit For Review.
			setSampleLCS(sampleObj, LCS_SUBMIT_FOR_REVIEW);
		}

		// Enters this loop when Sample status is one of the following - Approved /
		// Conditionally Approved / Rejected -> Dropped / Canceled / Rejected ->
		// Re-submit.
		if (!currentLCS.equals(LCS_CLOSED) && FormatHelper.hasContent(sampleStatus)
				&& (sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_APPROVED)
						|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_CONDITIONALLY_APPROVED)
						|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_REJECTED_DROPPED)
						|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_CANCELED)
						|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_REJECTED_RESUBMIT))) {
			// Setting Product Sample Lifecycle state as Closed.
			setSampleLCS(sampleObj, LCS_CLOSED);
		}
		LOGGER.debug("end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleApparelLCS");
	}

	/**
	 * setProductSampleFootwearLCS.
	 * 
	 * @param sampleObj      for sampleObj.
	 * @param sampleStatus   for sampleStatus.
	 * @param supplierStatus for supplierStatus.
	 * @return void.
	 */
	public static void setProductSampleFootwearLCS(LCSSample sampleObj, String sampleStatus, String supplierStatus) {
		LOGGER.debug("start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleFootwearLCS");

		// Getting current LCS of Footwear Product Sample.
		String currentLCS = sampleObj.getState().getState().toString();

		LOGGER.debug("In setProductSampleFootwearLCS Method - currentLCS = " + currentLCS);

		// Enters this loop when Sample status value is Requested.
		if (!FormatHelper.hasContent(supplierStatus) && !currentLCS.equals(LCS_REQUESTED)
				&& FormatHelper.hasContent(sampleStatus) && sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_REQUESTED)) {
			// Setting Product Sample Lifecycle state as Requested.
			setSampleLCS(sampleObj, LCS_REQUESTED);
		}

		// Enters this loop when Sample status is Requested and Supplier status is
		// Received.
		if (sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_REQUESTED) && !currentLCS.equals(LCS_TASK_ACCEPTED)
				&& FormatHelper.hasContent(supplierStatus)
				&& supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_RECEIVED)) {
			// Setting Product Sample Lifecycle state as Task Accepted.
			setSampleLCS(sampleObj, LCS_TASK_ACCEPTED);
		}

		// Enters this loop when Sample status is Requested and Supplier status is
		// Sample Finished and Shipped or Sample Finished.
		if (sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_REQUESTED) && !currentLCS.equals(LCS_SAMPLE_FINISHED)
				&& FormatHelper.hasContent(supplierStatus) && (supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_SHIPPED)
						|| supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_SAMPLE_FINISHED))) {
			// Setting Product Sample Lifecycle state as Sample Finished.
			setSampleLCS(sampleObj, LCS_SAMPLE_FINISHED);
		}

		// Enters this loop when Sample status is Submitted for review.
		if (!currentLCS.equals(LCS_SUBMITTED_FOR_REVIEW) && FormatHelper.hasContent(sampleStatus)
				&& sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_SUBMITTEDFORREVIEW)) {
			// Setting Product Sample Lifecycle state as Submitted For Review.
			setSampleLCS(sampleObj, LCS_SUBMITTED_FOR_REVIEW);
		}

		// Enters this loop when Sample status is Approved / Cancelled / To be Revised.
		if (!currentLCS.equals(LCS_CLOSED) && FormatHelper.hasContent(sampleStatus)
				&& (sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_APPROVED)
						|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_TO_BE_REVISED)
						|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_CANCELED))) {
			// Setting Product Sample Lifecycle state as Closed.
			setSampleLCS(sampleObj, LCS_CLOSED);
		}
		LOGGER.debug("end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleFootwearLCS");
	}

	// Phase 13 Start - SEPD Product Sample Workflow
	/**
	 * setProductSampleSEPDLCS.
	 * 
	 * @param sampleObj      for sampleObj.
	 * @param sampleStatus   for sampleStatus.
	 * @param supplierStatus for supplierStatus.
	 * @return void.
	 * @throws WTException
	 */
	public static void setProductSampleSEPDLCS(LCSSample sampleObj, String sampleStatus, String supplierStatus)
			throws WTException {
		LOGGER.debug("start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleSEPDLCS");

		// Getting current LCS of SEPD Product Sample.
		String currentLCS = sampleObj.getState().getState().toString();

		String lcs = "";

		LOGGER.debug("In setProductSampleSEPDLCS Method - currentLCS = " + currentLCS);

		// Getting Sample Request Object.
		LCSSampleRequest sampleReqObj = sampleObj.getSampleRequest();

		// Getting Sample Confirmation attribute value.
		String sampleConfirmationList = (String) sampleObj.getValue(SAMPLE_CONFIRMATION_SEPD);

		// Getting Sample Request Type attribute value.
		String requestType = (String) sampleReqObj.getValue(SAMPLE_REQUEST_TYPE_SEPD);

		// Enters this loop when Sample status is Approved / Cancelled / To be Revised.
		if (FormatHelper.hasContent(sampleStatus) && (sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_APPROVED_SEPD)
				|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_TO_BE_REVISED_SEPD)
				|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_CANCELED_SEPD))) {
			// Setting Product Sample Lifecycle state as Closed.
			lcs = LCS_CLOSED_SEPD;
			setSEPDLCSBasedOnCurrentLCS(sampleObj, currentLCS, lcs);
		}

		// This loop will be executed when Task 1 is retriggered from
		// LCS_SAMPLE_PRODUCED_SEPD by setting SAMPLE_CONFIRMATION as REJECTED, and then
		// supplier user wants to trigger task 2, task 3
		else if (isRetriggerFromRejectedTask(currentLCS, sampleConfirmationList, requestType)) {
			retriggerTask2And3FromRejectedTask(sampleObj, supplierStatus, currentLCS);
		}

		// Enter this loop when "Sample Confirmation" = "Rejected" AND "Request Type" IS
		// NOT "PPS".
		else if ((FormatHelper.hasContent(sampleConfirmationList)
				&& sampleConfirmationList.equalsIgnoreCase(SAMPLE_CONFIRMATION_LIST_REJECTED_SEPD))
				&& !requestType.equalsIgnoreCase(SAMPLE_REQUEST_TYPE_PPS_SEPD)) {
			// Setting Product Sample Lifecycle state as Requested.
			lcs = LCS_REQUESTED_SEPD;
			setSEPDLCSBasedOnCurrentLCS(sampleObj, currentLCS, lcs);
		}

		// Enters this loop when Sample Confirmation is Confirmed and Request Type is
		// not PPS.
		else if (!requestType.equalsIgnoreCase(SAMPLE_REQUEST_TYPE_PPS_SEPD)
				&& FormatHelper.hasContent(sampleConfirmationList)
				&& sampleConfirmationList.equalsIgnoreCase(SAMPLE_CONFIRMATION_LIST_CONFIRMED_SEPD)) {
			// Setting Product Sample Lifecycle state as Sample Checked.
			lcs = LCS_SAMPLE_CHECKED_SEPD;
			setSEPDLCSBasedOnCurrentLCS(sampleObj, currentLCS, lcs);
		}

		// Enter this loop when "Request Type" IS "PPS" and Sample Confirmation is
		// "Confirmed"/"Rejected".
		else if (isSampleConfirmationIsRejectedOrConfirmedForPPSSample(sampleConfirmationList, requestType)) {
			setSEPDLCSBasedOnCurrentLCS(sampleObj, currentLCS, LCS_CLOSED_SEPD);
		}

		// Enters this loop when Supplier status is Sample Finished.
		else if (FormatHelper.hasContent(supplierStatus)
				&& supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_SAMPLE_FINISHED_SEPD)) {
			// Setting Product Sample Lifecycle state as Sample Produced.
			lcs = LCS_SAMPLE_PRODUCED_SEPD;
			setSEPDLCSBasedOnCurrentLCS(sampleObj, currentLCS, lcs);
		}

		// Enters this loop when Supplier status is Received.
		else if (FormatHelper.hasContent(supplierStatus)
				&& supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_RECEIVED_SEPD)) {
			// Setting Product Sample Lifecycle state as Task Accepted.
			lcs = LCS_TASK_ACCEPTED_SEPD;
			setSEPDLCSBasedOnCurrentLCS(sampleObj, currentLCS, lcs);
		}

		// Enters this loop when Sample status value is Requested.
		else if (isSEPDSampleStatusRequested(sampleStatus)) {
			// Setting Product Sample Lifecycle state as Requested.
			setSEPDLCSBasedOnCurrentLCS(sampleObj, currentLCS, LCS_REQUESTED_SEPD);
		}
		LOGGER.debug("end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleSEPDLCS");
	}

	/**
	 * @param sampleConfirmationList
	 * @param requestType
	 * @return
	 */
	private static boolean isSampleConfirmationIsRejectedOrConfirmedForPPSSample(String sampleConfirmationList,
			String requestType) {
		return requestType.equalsIgnoreCase(SAMPLE_REQUEST_TYPE_PPS_SEPD)
				&& (FormatHelper.hasContent(sampleConfirmationList)
						&& (sampleConfirmationList.equalsIgnoreCase(SAMPLE_CONFIRMATION_LIST_CONFIRMED_SEPD)
								|| sampleConfirmationList.equalsIgnoreCase(SAMPLE_CONFIRMATION_LIST_REJECTED_SEPD)));
	}

	/**
	 * @param sampleObj
	 * @param supplierStatus
	 * @param currentLCS
	 */
	private static void retriggerTask2And3FromRejectedTask(LCSSample sampleObj, String supplierStatus,
			String currentLCS) {
		String lcs;
		// Enters this loop when Supplier status is Sample Finished.
		if (FormatHelper.hasContent(supplierStatus)
				&& supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_SAMPLE_FINISHED_SEPD)) {
			// Setting Product Sample Lifecycle state as Sample Produced.
			lcs = LCS_SAMPLE_PRODUCED_SEPD;
			setSEPDLCSBasedOnCurrentLCS(sampleObj, currentLCS, lcs);
		}
		// Enters this loop when Supplier status is Received.
		else if (FormatHelper.hasContent(supplierStatus)
				&& supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_RECEIVED_SEPD)) {
			// Setting Product Sample Lifecycle state as Task Accepted.
			lcs = LCS_TASK_ACCEPTED_SEPD;
			setSEPDLCSBasedOnCurrentLCS(sampleObj, currentLCS, lcs);
		}
	}

	/**
	 * @param currentLCS
	 * @param sampleConfirmationList
	 * @param requestType
	 * @return
	 */
	private static boolean isRetriggerFromRejectedTask(String currentLCS, String sampleConfirmationList,
			String requestType) {
		return (currentLCS.equals(LCS_REQUESTED_SEPD) || currentLCS.equals(LCS_TASK_ACCEPTED_SEPD))
				&& (FormatHelper.hasContent(sampleConfirmationList)
						&& sampleConfirmationList.equalsIgnoreCase(SAMPLE_CONFIRMATION_LIST_REJECTED_SEPD))
				&& !requestType.equalsIgnoreCase(SAMPLE_REQUEST_TYPE_PPS_SEPD);
	}

	/**
	 * @param sampleStatus
	 * @return
	 */
	private static boolean isSEPDSampleStatusRequested(String sampleStatus) {
		return FormatHelper.hasContent(sampleStatus) && sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_REQUESTED_SEPD);
	}

	/**
	 * setSEPDLCSBasedOnCurrentLCS.
	 * 
	 * @param sampleObj  for sampleObj.
	 * @param currentLCS for currentLCS.
	 * @param lcs        for lcs.
	 * @return void.
	 */
	private static void setSEPDLCSBasedOnCurrentLCS(LCSSample sampleObj, String currentLCS, String lcs) {
		LOGGER.info("start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setSEPDLCSBasedOnCurrentLCS");
		LOGGER.debug("In setSEPDLCSBasedOnCurrentLCS method - currentLCS==" + currentLCS);
		LOGGER.debug("In setSEPDLCSBasedOnCurrentLCS method - lcs==" + lcs);
		if (!currentLCS.equals(lcs)) {
			// Calling this method to set SEPD Product Sample Lifecycle state as lcs.
			setSampleLCS(sampleObj, lcs);
		}
		LOGGER.info("End - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setSEPDLCSBasedOnCurrentLCS");
	}

	// Phase 13 End - SEPD Product Sample Workflow
	/**
	 * setProductSampleAttributes.
	 * 
	 * @param object for object.
	 * @return void.
	 */
	public static void setProductSampleAttributes(WTObject object) {
		LOGGER.info("start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleAttributes");

		try {
			// Getting Sample object.
			LCSSample sampleObj = (LCSSample) object;
			// Getting current LC Template name.
			String currentTemplateName = sampleObj.getLifeCycleName();

			// Phase 13 Start
			// Getting Product Object.
			LCSProduct prodObj = getProduct(sampleObj.getSampleRequest());

			String supplierStatus = (String) sampleObj.getValue(SUPPLIER_STATUS);
			String sampleStatus = (String) sampleObj.getValue(SAMPLE_STATUS);
			// Phase 13 End

			LOGGER.debug("In setProductSampleAttributes Method - supplierStatus = " + supplierStatus);
			LOGGER.debug("In setProductSampleAttributes Method - currentTemplateName = " + currentTemplateName);

			LOGGER.debug("In setProductSampleAttributes Method - sampleObj = " + sampleObj);
			LOGGER.debug("In setProductSampleAttributes Method - sampleStatus = " + sampleStatus);

			// Loop to set product sample attribute values based on Product Sample Type.
			if (isValidAPDSampleType(sampleObj.getFlexType().getFullName(), currentTemplateName, prodObj)) {

				// Method to set Apparel Product Sample attributes.
				setProductSampleApparelAttributes(sampleObj, sampleStatus, supplierStatus);

				/*
				 * Initially tried with Pre-Persist on Sample Request: when user updated sample
				 * from Sample search results page without editing any of SR attributes, the
				 * Pre-Persist on SR didn't trigger. So, we moved this logic from Pre-Persist on
				 * SR to Per-Persist on Sample. Also, Please note in the below methods we are
				 * setting attributes based on conditions of sample under this SR. Automatically
				 * OOTB saves the SR without any explicit save API.
				 */
				LOGGER.debug(
						"In setProductSampleAttributes Method -- calling setSampleReqStatusBasedOnSampleStatus Method- sampleObj Name = "
								+ sampleObj.getName());
				// Method to set Apparel Product Sample Request Status attribute based on Sample
				// Status.
				setSampleReqStatusBasedOnSampleStatus(sampleObj);

			} else if (isValidFPDSampleType(sampleObj.getFlexType().getFullName(), currentTemplateName, prodObj)) {

				// Method to set Footwear Product Sample attributes.
				setProductSampleFootwearAttributes(sampleObj, sampleStatus, supplierStatus);

				/*
				 * Initially tried with Pre-Persist on Sample Request: when user updated sample
				 * from Sample search results page without editing any of SR attributes, the
				 * Pre-Persist on SR didn't trigger. So, we moved this logic from Pre-Persist on
				 * SR to Per-Persist on Sample. Also, Please note in the below methods we are
				 * setting attributes based on conditions of sample under this SR. Automatically
				 * OOTB saves the SR without any explicit save API.
				 */
				LOGGER.debug(
						"In setProductSampleAttributes Method -- calling setSampleReqStatusBasedOnSampleStatus Method- sampleObj Name = "
								+ sampleObj.getName());
				// Method to set Footwear Product Sample Request Status attribute based on
				// Sample Status.
				setSampleReqStatusBasedOnSampleStatus(sampleObj);

			}
			// Phase 13 Start - SEPD Product Sample Workflow - setProductSampleAttributes.
			else if (prodObj != null
					&& (prodObj.getFlexType().getFullName().startsWith(PRODUCT_TYPE_ACC_SEPD)
							|| prodObj.getFlexType().getFullName().startsWith(PRODUCT_TYPE_SEPD))
					&& sampleObj.getFlexType().getFullName().startsWith(SEPD_PRODUCT_SAMPLE_TYPE)
					&& FormatHelper.hasContent(currentTemplateName)
					&& currentTemplateName.equals(SEPD_PRODUCT_SAMPLE_LC_TEMPLATE_NAME)) {

				// Method to set SEPD Product Sample attributes.
				setProductSampleSEPDAttributes(sampleObj, sampleStatus, supplierStatus);

				/*
				 * Initially tried with Pre-Persist on Sample Request: when user updated sample
				 * from Sample search results page without editing any of SR attributes, the
				 * Pre-Persist on SR didn't trigger. So, we moved this logic from Pre-Persist on
				 * SR to Per-Persist on Sample. Also, Please note in the below methods we are
				 * setting attributes based on conditions of sample under this SR. Automatically
				 * OOTB saves the SR without any explicit save API.
				 */
				LOGGER.debug(
						"In setProductSampleAttributes Method for SEPD Type -- calling setSampleReqStatusBasedOnSampleStatus Method- sampleObj Name = "
								+ sampleObj.getName());
				// Method to set SEPD Product Sample Request Status attribute based on
				// Sample Status.
				setSampleReqStatusBasedOnSampleStatus(sampleObj);
			}
			// Phase 13 End - SEPD Product Sample Workflow - setProductSampleAttributes.
		} catch (WTException e) {
			LOGGER.error("WTException in setProductSampleAttributes method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleAttributes");
	}

	/**
	 * setProductSampleApparelAttributes.
	 * 
	 * @param sampleObj      for sampleObj.
	 * @param sampleStatus   for sampleStatus.
	 * @param supplierStatus for supplierStatus.
	 * @return void.
	 */
	public static void setProductSampleApparelAttributes(LCSSample sampleObj, String sampleStatus,
			String supplierStatus) {
		LOGGER.debug(
				"start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleApparelAttributes");
		try {
			// Enters this loop when Supplier status is Received.
			if (FormatHelper.hasContent(supplierStatus) && supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_RECEIVED)
					&& sampleObj.getValue(SAMPLE_TASK_RECEIVED_BY_SUPPLIER) == null) {
				// Setting the "Sample Task Received by Supplier" Attribute to "Current Date"
				sampleObj.setValue(SAMPLE_TASK_RECEIVED_BY_SUPPLIER, setCurrentDate());
			}

			// Enters this loop when Supplier status is Shipped.
			if (FormatHelper.hasContent(supplierStatus) && supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_SHIPPED)
					&& sampleObj.getValue(SHIP_DATE) == null) {
				// Setting "Ship Date" attribute to Current Date.
				sampleObj.setValue(SHIP_DATE, setCurrentDate());
			}

			// Enters this loop when Sample status is Submitted for review.
			if (FormatHelper.hasContent(sampleStatus) && sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_SUBMITTEDFORREVIEW)
					&& sampleObj.getValue(SAMPLE_SUBMITTED_FOR_REVIEW) == null) {
				// Setting "Sample Submitted for Review" attribute to "Current Date".
				sampleObj.setValue(SAMPLE_SUBMITTED_FOR_REVIEW, setCurrentDate());
			}

			// Enters this loop when Sample status is one of the following - Approved /
			// Conditionally Approved / Rejected -> Dropped / Canceled / Rejected ->
			// Re-submit.
			if (FormatHelper.hasContent(sampleStatus)
					&& (sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_APPROVED)
							|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_CONDITIONALLY_APPROVED)
							|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_REJECTED_DROPPED)
							|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_CANCELED)
							|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_REJECTED_RESUBMIT))
					&& sampleObj.getValue(SAMPLE_EVALUATED_DATE) == null) {
				// Setting "sample Evaluated Date" attribute to Current Date.
				sampleObj.setValue(SAMPLE_EVALUATED_DATE, setCurrentDate());
			}
		} catch (WTPropertyVetoException | WTException e) {
			LOGGER.error("WTPropertyVetoException / WTException in setProductSampleApparelAttributes method: "
					+ e.getMessage());
			e.printStackTrace();
		}
		LOGGER.debug("end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleApparelAttributes");
	}

	/**
	 * setProductSampleFootwearAttributes.
	 * 
	 * @param sampleObj      for sampleObj.
	 * @param sampleStatus   for sampleStatus.
	 * @param supplierStatus for supplierStatus.
	 * @return void.
	 */
	public static void setProductSampleFootwearAttributes(LCSSample sampleObj, String sampleStatus,
			String supplierStatus) {
		LOGGER.debug(
				"start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleFootwearAttributes");
		try {
			// Enters this loop when Supplier status is Received.
			if (FormatHelper.hasContent(supplierStatus) && supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_RECEIVED)
					&& sampleObj.getValue(SAMPLE_TASK_RECEIVED_BY_SUPPLIER) == null) {
				// Setting the "Sample Task Received by Supplier" Attribute to "Current Date".
				sampleObj.setValue(SAMPLE_TASK_RECEIVED_BY_SUPPLIER, setCurrentDate());
			}

			// Enters this loop when Supplier status is Sample Finished and Shipped or
			// Sample Finished.
			if (FormatHelper.hasContent(supplierStatus) && (supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_SHIPPED)
					|| supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_SAMPLE_FINISHED))) {
				setAttributesBasedOnSupplierStatus(sampleObj, supplierStatus);
			}

			// Enters this loop when Sample status is Submitted for review.
			if (FormatHelper.hasContent(sampleStatus) && sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_SUBMITTEDFORREVIEW)
					&& sampleObj.getValue(SAMPLE_SUBMITTED_FOR_REVIEW) == null) {
				// Setting "Sample Submitted for Review" attribute to "Current Date".
				sampleObj.setValue(SAMPLE_SUBMITTED_FOR_REVIEW, setCurrentDate());
			}

			// Enters this loop when Sample status is Approved / Cancelled / To be Revised.
			if (FormatHelper.hasContent(sampleStatus)
					&& (sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_APPROVED)
							|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_TO_BE_REVISED)
							|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_CANCELED))
					&& sampleObj.getValue(SAMPLE_EVALUATED_DATE) == null) {
				// Setting "sample Evaluated Date" attribute to Current Date.
				sampleObj.setValue(SAMPLE_EVALUATED_DATE, setCurrentDate());
			}
		} catch (WTPropertyVetoException | WTException e) {
			LOGGER.error("WTPropertyVetoException / WTException in setProductSampleFootwearAttributes method: "
					+ e.getMessage());
			e.printStackTrace();
		}
		LOGGER.debug(
				"end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleFootwearAttributes");
	}

	/**
	 * @param sampleObj
	 * @param supplierStatus
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void setAttributesBasedOnSupplierStatus(LCSSample sampleObj, String supplierStatus)
			throws WTException, WTPropertyVetoException {
		// When Supplier status is Sample Finished and Shipped.
		if (supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_SHIPPED) && sampleObj.getValue(SHIP_DATE) == null) {
			// Setting "Ship Date" attribute to Current Date.
			sampleObj.setValue(SHIP_DATE, setCurrentDate());
		}
		// When Supplier status is Sample Finished.
		else if (supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_SAMPLE_FINISHED)) {
			// Setting "Ship Date" attribute to blank value.
			sampleObj.setValue(SHIP_DATE, "");
			if (sampleObj.getValue(SAMPLE_FINISHED_DATE) == null) {
				// Setting "Sample Finished Date" attribute to Current Date.
				sampleObj.setValue(SAMPLE_FINISHED_DATE, setCurrentDate());
			}
		}
	}

	// Phase 13 Start - SEPD Product Sample Workflow.
	/**
	 * setProductSampleSEPDAttributes.
	 * 
	 * @param sampleObj      for sampleObj.
	 * @param sampleStatus   for sampleStatus.
	 * @param supplierStatus for supplierStatus.
	 * @return void.
	 */
	public static void setProductSampleSEPDAttributes(LCSSample sampleObj, String sampleStatus, String supplierStatus) {
		LOGGER.debug("start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleSEPDAttributes");
		try {
			// Enters this loop when Supplier status is Received.
			if (FormatHelper.hasContent(supplierStatus)
					&& supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_RECEIVED_SEPD)
					&& sampleObj.getValue(SAMPLE_TASK_RECEIVED_BY_SUPPLIER_SEPD) == null) {
				// Setting the "Sample Task Received by Supplier" Attribute to "Current Date".
				sampleObj.setValue(SAMPLE_TASK_RECEIVED_BY_SUPPLIER_SEPD, setCurrentDate());
			}

			// Enters this loop when Supplier status is Sample Finished
			if (FormatHelper.hasContent(supplierStatus)
					&& (supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_SAMPLE_FINISHED_SEPD)
							&& sampleObj.getValue(SAMPLE_FINISHED_FACT_SEPD) == null)) {
				// Setting "Sample Finished FACT" attribute to Current Date.
				sampleObj.setValue(SAMPLE_FINISHED_FACT_SEPD, setCurrentDate());
			}

			// Getting Sample Request Object.
			LCSSampleRequest sampleReqObj = sampleObj.getSampleRequest();

			// Getting Sample Confirmation attribute value.
			String sampleConfirmationList = (String) sampleObj.getValue(SAMPLE_CONFIRMATION_SEPD);

			// Getting Sample Request Type attribute value.
			String requestType = (String) sampleReqObj.getValue(SAMPLE_REQUEST_TYPE_SEPD);

			// Enter this loop when "Sample Confirmation" is set to "Rejected", "Pending" or
			// "Confirmed" then set the "Sample Checked FACT" attribute to current date.
			if (FormatHelper.hasContent(sampleConfirmationList)
					&& (sampleConfirmationList.equalsIgnoreCase(SAMPLE_CONFIRMATION_LIST_CONFIRMED_SEPD)
							|| sampleConfirmationList.equalsIgnoreCase(SAMPLE_CONFIRMATION_LIST_PENDING_SEPD)
							|| sampleConfirmationList.equalsIgnoreCase(SAMPLE_CONFIRMATION_LIST_REJECTED_SEPD))
					&& sampleObj.getValue(SAMPLE_CHECKED_FACT) == null) {
				// Setting "Sample Checked FACT" attribute to Current Date.
				sampleObj.setValue(SAMPLE_CHECKED_FACT, setCurrentDate());
			}

			// Enter this loop when "Sample Confirmation" = "Rejected" AND "Request Type" IS
			// NOT "PPS".
			if (FormatHelper.hasContent(sampleConfirmationList)
					&& sampleConfirmationList.equalsIgnoreCase(SAMPLE_CONFIRMATION_LIST_REJECTED_SEPD)
					&& !requestType.equalsIgnoreCase(SAMPLE_REQUEST_TYPE_PPS_SEPD)
					&& !sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_REQUESTED_SEPD)) {
				// Setting Sample status as Requested.
				sampleObj.setValue(SAMPLE_STATUS, SAMPLE_STATUS_REQUESTED_SEPD);
			}

			// Enters this loop when Sample status is Approved / Cancelled / To be Revised.
			if (FormatHelper.hasContent(sampleStatus)
					&& (sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_APPROVED_SEPD)
							|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_TO_BE_REVISED_SEPD)
							|| sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_CANCELED_SEPD))
					&& sampleObj.getValue(SAMPLE_EVALUATED_DATE_SEPD) == null) {
				// Setting "sample Evaluated Date" attribute to Current Date.
				sampleObj.setValue(SAMPLE_EVALUATED_DATE_SEPD, setCurrentDate());
			}

			// Method to set Date attributes for SEPD Product Sample attributes.
			setDateAttributesForSEPDSample(sampleObj, sampleStatus, supplierStatus);

		} catch (WTPropertyVetoException | WTException e) {
			LOGGER.error("WTPropertyVetoException / WTException in setProductSampleSEPDAttributes method: "
					+ e.getMessage());
			e.printStackTrace();
		}
		LOGGER.debug("end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setProductSampleSEPDAttributes");
	}

	/**
	 * setDateAttributesForSEPDSample.
	 * 
	 * @param sampleObj      for sampleObj.
	 * @param sampleStatus   for sampleStatus.
	 * @param supplierStatus for supplierStatus.
	 * @return void.
	 */
	private static void setDateAttributesForSEPDSample(LCSSample sampleObj, String sampleStatus,
			String supplierStatus) {
		try {
			LOGGER.info(
					"start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setDateAttributesForSEPDSample");
			// When Sample Status is set to Submitted for Review then set the Sample
			// Received HQ FACT attribute to current date if it is empty.
			if (FormatHelper.hasContent(sampleStatus)
					&& sampleStatus.equalsIgnoreCase(SAMPLE_STATUS_SUBMITTED_FOR_REVIEW_SEPD)
					&& sampleObj.getValue(SAMPLE_RECEIVED_HQ_FACT_SEPD) == null) {
				// Setting current date to Sample Received HQ Fact SEPD attribute.
				sampleObj.setValue(SAMPLE_RECEIVED_HQ_FACT_SEPD, setCurrentDate());
			}

			// When Supplier Sample Status is set to Sample Shipped then set the Sample Sent
			// to HQ FACT attribute to current date if it is empty.
			if (FormatHelper.hasContent(supplierStatus) && supplierStatus.equalsIgnoreCase(SUPPLIER_STATUS_SHIPPED_SEPD)
					&& sampleObj.getValue(SAMPLE_SENT_TO_HQ_FACT_SEPD) == null) {
				// Setting current date to Sample Sent to HQ Fact SEPD attribute.
				sampleObj.setValue(SAMPLE_SENT_TO_HQ_FACT_SEPD, setCurrentDate());
			}
		} catch (WTPropertyVetoException | WTException e) {
			LOGGER.error("WTPropertyVetoException / WTException in setDateAttributesForSEPDSample method: "
					+ e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setDateAttributesForSEPDSample");
	}
	// Phase 13 End - SEPD Product Sample Workflow.

	/**
	 * setSampleReqStatusBasedOnSampleStatus.
	 * 
	 * @param sample for sample.
	 * @return void.
	 */
	/**
	 * @param sample
	 */
	public static void setSampleReqStatusBasedOnSampleStatus(LCSSample sample) {
		LOGGER.info(
				"start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setSampleReqStatusBasedOnSampleStatus");

		try {
			if (sample.getSampleRequest() instanceof LCSSampleRequest) {

				// Getting Sample Request object.
				LCSSampleRequest sampleReqObj = sample.getSampleRequest();

				Collection<FlexObject> colSamples;
				LCSSampleQuery sq = new LCSSampleQuery();
				if (sampleReqObj.toString().contains("com.lcs.wc.sample.LCSSampleRequest:")) {

					LOGGER.debug("In setSampleReqStatusBasedOnSampleStatus Method - sampleReqObj = "
							+ sampleReqObj.getName());

					colSamples = sq.findSamplesIdForSampleRequest(sampleReqObj, true);
					LOGGER.debug("In setSampleReqStatusBasedOnSampleStatus Method - colSamples = " + colSamples);

					if (colSamples != null && !colSamples.isEmpty()) {

						setSRStatusBasedOnSampleStatus(sample, sampleReqObj, colSamples);
					}
				}
			}
		} catch (WTPropertyVetoException | WTException e) {
			LOGGER.error("WTPropertyVetoException / WTException in setSampleReqStatusBasedOnSampleStatus method: "
					+ e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info(
				"end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setSampleReqStatusBasedOnSampleStatus");
	}

	/**
	 * @param sample
	 * @param sampleReqObj
	 * @param colSamples
	 * @throws WTException
	 * @throws ObjectNoLongerExistsException
	 * @throws WTPropertyVetoException
	 */
	private static void setSRStatusBasedOnSampleStatus(LCSSample sample, LCSSampleRequest sampleReqObj,
			Collection<FlexObject> colSamples)
			throws WTException, ObjectNoLongerExistsException, WTPropertyVetoException {
		// Getting Request type of Sample Request object.
		String requestType = sampleReqObj.getFlexType().getFullName();

		// Getting current LC Template name.
		String currentTemplateName = sampleReqObj.getLifeCycleName();

		// Phase 13 Start - SEPD Product Sample Workflow - Getting Product object.
		// Getting Sample Request Type attribute value.
		String sampleRequestType = (String) sampleReqObj.getValue(SAMPLE_REQUEST_TYPE_SEPD);

		// Getting Product Object.
		LCSProduct prodObj = getProduct(sampleReqObj);
		// Phase 13 End - SEPD Product Sample Workflow - Getting Product object.

		LOGGER.debug("In setSampleReqStatusBasedOnSampleStatus Method - requestType = " + requestType);
		LOGGER.debug("In setSampleReqStatusBasedOnSampleStatus Method - currentTemplateName = " + currentTemplateName);

		Iterator itSamples = colSamples.iterator();
		boolean bPresent = false;
		String lastUpdatedSampleStatus = (String) sample.getValue(SAMPLE_STATUS);

		while (itSamples.hasNext()) {
			bPresent = validateSamples(sample, requestType, currentTemplateName, sampleRequestType, prodObj, itSamples,
					lastUpdatedSampleStatus);
			if (!bPresent) {
				break;
			}
		}
		setRequestStatusClosed(sampleReqObj, bPresent);

		// Phase 13 - FPD Sample WF Enh - set sample status as Cancelled when request
		// status = Cancelled
		setFPDSampleStatusCancelledFromSamplePerPersist(sample, sampleReqObj, currentTemplateName, prodObj);

	}

	/**
	 * @param sample
	 * @param sampleReqObj
	 * @param currentTemplateName
	 * @param prodObj
	 * @throws WTException
	 */
	private static void setFPDSampleStatusCancelledFromSamplePerPersist(LCSSample sample, LCSSampleRequest sampleReqObj,
			String currentTemplateName, LCSProduct prodObj) throws WTException {
		LOGGER.debug("In setFPDSampleStatusCancelledFromPerPersist Method - start ");

		Iterator itSamples;
		FlexObject fo;
		LCSSample lcsSample;
		if (prodObj != null && sampleReqObj.getFlexType().getFullName().startsWith(FOOTWEAR_PRODUCT_SAMPLE_TYPE)
				&& FormatHelper.hasContent(currentTemplateName)
				&& currentTemplateName.equals(FOOTWEAR_PRODUCT_SAMPLE_LC_TEMPLATE_NAME)) {
			String strSampleID = "";
			LCSSampleQuery sq = new LCSSampleQuery();
			Collection colSamples = sq.findSamplesIdForSampleRequest(sampleReqObj, true);
			LOGGER.debug("In setFPDSampleStatusCancelledFromPerPersist Method - sampleReqObj== " + sampleReqObj);
			LOGGER.debug("In setFPDSampleStatusCancelledFromPerPersist Method - colSamples== " + colSamples);
			// Getting Request status value.
			String requestStatus = (String) sampleReqObj.getValue(SAMPLE_REQUEST_STATUS);
			LOGGER.debug("In setFPDSampleStatusCancelledFromPerPersist Method  - requestStatus== " + requestStatus);
			if (FormatHelper.hasContent(requestStatus) && requestStatus.equalsIgnoreCase(REQUEST_STATUS_CANCELLED)) {
				// Iterator itSamples = colSamples.iterator();
				itSamples = colSamples.iterator();
				while (itSamples.hasNext()) {
					fo = (FlexObject) itSamples.next();
					strSampleID = (String) fo.get(LCSSAMPLE_IDA2A2);
					LOGGER.debug("In setFPDSampleStatusCancelledFromPerPersist Method - strSampleID = " + strSampleID);
					lcsSample = (LCSSample) LCSSampleQuery.findObjectById(SEPD_SAMPLE_OBJECT_ID + strSampleID);
					LOGGER.debug("In setFPDSampleStatusCancelledFromPerPersist Method - Sample Status="
							+ lcsSample.getValue(SAMPLE_STATUS));

					/*
					 * If the currently updated Sample (sample) is same as current Sample
					 * (lcsSample) from DB, get the sample status value from currently updated
					 * sample as this will hold the latest Sample Status values selected by user in
					 * UI.
					 */
					// Only for the currently updated sample from UI, set the value of Sample Statu
					// as cancelled in PrePersist. For other Samples set the value in Post Persist
					if (sample.toString().equals(lcsSample.toString())) {
						sample.setValue(SAMPLE_STATUS, SAMPLE_STATUS_CANCELED);
						LOGGER.debug(
								"In setFPDSampleStatusCancelledFromPerPersist Method - After setting to Cancelled - Sample Status="
										+ sample.getValue(SAMPLE_STATUS));
						LOGGER.debug(
								"In setFPDSampleStatusCancelledFromPerPersist Method - After setting to Cancelled - lcsSample Status="
										+ lcsSample.getValue(SAMPLE_STATUS));
					}
				}
			}
		}
		LOGGER.debug("In setFPDSampleStatusCancelledFromPerPersist Method - end ");
	}

	private static boolean validateSamples(LCSSample sample, String requestType, String currentTemplateName,
			String sampleRequestType, LCSProduct prodObj, Iterator itSamples, String lastUpdatedSampleStatus)
			throws WTException, ObjectNoLongerExistsException {
		String strSampleID;
		FlexObject fo;
		LCSSample lcsSample;
		boolean bPresent;
		String sampleStatusFo;
		String sampleConfirmationListFo;
		bPresent = false;
		fo = (FlexObject) itSamples.next();
		strSampleID = (String) fo.get(LCSSAMPLE_IDA2A2);
		LOGGER.debug("In setSampleReqStatusBasedOnSampleStatus Method - strSampleID = " + strSampleID);
		lcsSample = (LCSSample) LCSSampleQuery.findObjectById(SEPD_SAMPLE_OBJECT_ID + strSampleID);

		sampleStatusFo = getSampleAttrValue(sample, lcsSample, SAMPLE_STATUS);
		sampleConfirmationListFo = getSampleAttrValue(sample, lcsSample, SAMPLE_CONFIRMATION_SEPD);

		// When Request type is Apparel Product Sample.
		if (isValidAPDSampleType(requestType, currentTemplateName, prodObj)) {
			LOGGER.debug("In setSampleReqStatusBasedOnSampleStatus Method - lastUpdatedSampleStatus = "
					+ lastUpdatedSampleStatus);
			// Get the last updated sample, and check if the Sample Status is not Reject
			// Resubmit / Requested / Submitted For Review.
			if (isValidLastUpdatedSampleStatus(lastUpdatedSampleStatus)) {
				bPresent = checkAPDSampleAttributes(bPresent, sampleStatusFo);
				if (!bPresent) {
					return false;
				}
			} else {
				return false;
			}
			// When Request type is Footwear Product Sample.
		} else if (isValidFPDSampleType(requestType, currentTemplateName, prodObj)) {
			bPresent = checkFPDSampleAttributes(bPresent, sampleStatusFo);
			if (!bPresent) {
				return false;
			}
		}
		// Phase 13 Start - SEPD Product Sample Workflow
		else if (isValidSEPDSampleType(requestType, currentTemplateName, prodObj)) {
			bPresent = checkSEPDSampleAttributes(sampleRequestType, bPresent, sampleStatusFo, sampleConfirmationListFo);
			if (!bPresent) {
				return false;
			}
		}
		// Phase 13 End - SEPD Product Sample Workflow
		return bPresent;
	}

	/**
	 * @param sample
	 * @param lcsSample
	 * @param strAttKey
	 * @return
	 * @throws ObjectNoLongerExistsException
	 * @throws WTException
	 */
	private static String getSampleAttrValue(LCSSample sample, LCSSample lcsSample, String strAttKey)
			throws ObjectNoLongerExistsException, WTException {
		String strAttValue = "";
		/*
		 * If the currently updated Sample (sample) is same as current Sample
		 * (lcsSample) from DB, get the sample status value from currently updated
		 * sample as this will hold the latest Sample Status values selected by user in
		 * UI.
		 */
		if (!sample.toString().equals(lcsSample.toString())) {
			lcsSample = (LCSSample) PersistenceHelper.manager.refresh(lcsSample);
			strAttValue = (String) lcsSample.getValue(strAttKey);

		} else {
			strAttValue = (String) sample.getValue(strAttKey);

		}

		return strAttValue;
	}

	private static boolean isValidLastUpdatedSampleStatus(String lastUpdatedSampleStatus) {
		return !lastUpdatedSampleStatus.equals(SAMPLE_STATUS_REJECTED_RESUBMIT)
				&& !lastUpdatedSampleStatus.equals(SAMPLE_STATUS_REQUESTED)
				&& !lastUpdatedSampleStatus.equals(SAMPLE_STATUS_SUBMITTEDFORREVIEW);
	}

	/**
	 * @param sampleReqObj
	 * @param bPresent
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void setRequestStatusClosed(LCSSampleRequest sampleReqObj, boolean bPresent)
			throws WTException, WTPropertyVetoException {
		if (bPresent) {
			// Setting Request status to closed.
			sampleReqObj.setValue(SAMPLE_REQUEST_STATUS, REQUEST_STATUS_CLOSED);
			LOGGER.debug("In setSampleReqStatusBasedOnSampleStatus method - REQUEST STATUS "
					+ sampleReqObj.getValue(SAMPLE_REQUEST_STATUS));
			if (sampleReqObj.getValue(REQUEST_CLOSE_DATE) == null) {
				// Setting Request close date to current date.
				sampleReqObj.setValue(REQUEST_CLOSE_DATE, setCurrentDate());
			}
		}

	}

	/**
	 * @param requestType
	 * @param currentTemplateName
	 * @param prodObj
	 * @return
	 */
	private static boolean isValidSEPDSampleType(String requestType, String currentTemplateName, LCSProduct prodObj) {
		return prodObj != null
				&& (prodObj.getFlexType().getFullName().startsWith(PRODUCT_TYPE_ACC_SEPD)
						|| prodObj.getFlexType().getFullName().startsWith(PRODUCT_TYPE_SEPD))
				&& requestType.startsWith(SEPD_PRODUCT_SAMPLE_TYPE) && FormatHelper.hasContent(currentTemplateName)
				&& currentTemplateName.equals(SEPD_PRODUCT_SAMPLE_LC_TEMPLATE_NAME);
	}

	/**
	 * @param requestType
	 * @param currentTemplateName
	 * @param prodObj
	 * @return
	 */
	private static boolean isValidFPDSampleType(String requestType, String currentTemplateName, LCSProduct prodObj) {
		return prodObj != null && requestType.startsWith(FOOTWEAR_PRODUCT_SAMPLE_TYPE)
				&& FormatHelper.hasContent(currentTemplateName)
				&& currentTemplateName.equals(FOOTWEAR_PRODUCT_SAMPLE_LC_TEMPLATE_NAME);
	}

	/**
	 * @param requestType
	 * @param currentTemplateName
	 * @param prodObj
	 * @return
	 */
	private static boolean isValidAPDSampleType(String requestType, String currentTemplateName, LCSProduct prodObj) {
		return prodObj != null && requestType.startsWith(APPAREL_PRODUCT_SAMPLE_TYPE)
				&& FormatHelper.hasContent(currentTemplateName)
				&& currentTemplateName.equals(APPAREL_PRODUCT_SAMPLE_LC_TEMPLATE_NAME);
	}

	// Phase 13 Start - Extracted this APD method for SONAR fix.
	/**
	 * checkAPDSampleAttributes.
	 * 
	 * @param bPresent       for bPresent.
	 * @param sampleStatusFo for sampleStatusFo.
	 * @return boolean.
	 */
	private static boolean checkAPDSampleAttributes(boolean bPresent, String sampleStatusFo) {
		LOGGER.info("Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--checkAPDSampleAttributes");
		// When Sample status is one of the following Approved / Conditionally Approved
		// / Rejected -> Dropped / Canceled / Rejected -> Re-Submit for all samples
		// associated then set attributes of Sample Request.
		if (FormatHelper.hasContent(sampleStatusFo) && (sampleStatusFo.equalsIgnoreCase(SAMPLE_STATUS_APPROVED)
				|| sampleStatusFo.equalsIgnoreCase(SAMPLE_STATUS_CONDITIONALLY_APPROVED)
				|| sampleStatusFo.equalsIgnoreCase(SAMPLE_STATUS_REJECTED_DROPPED)
				|| sampleStatusFo.equalsIgnoreCase(SAMPLE_STATUS_CANCELED)
				|| sampleStatusFo.equalsIgnoreCase(SAMPLE_STATUS_REJECTED_RESUBMIT))) {
			bPresent = true;
		}
		return bPresent;
	}

	// Extracted this FPD method for SONAR fix.
	/**
	 * checkFPDSampleAttributes.
	 * 
	 * @param bPresent for bPresent.
	 * @param bPresent for bPresent.
	 * @return boolean.
	 */
	private static boolean checkFPDSampleAttributes(boolean bPresent, String sampleStatusFo) {
		LOGGER.info("Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--checkFPDSampleAttributes");
		// Sample status is one of the following Approved / Cancelled / To be Revised
		// for all samples associated then set attributes of Sample Request.
		if (FormatHelper.hasContent(sampleStatusFo) && (sampleStatusFo.equalsIgnoreCase(SAMPLE_STATUS_APPROVED)
				|| sampleStatusFo.equalsIgnoreCase(SAMPLE_STATUS_TO_BE_REVISED)
				|| sampleStatusFo.equalsIgnoreCase(SAMPLE_STATUS_CANCELED))) {
			bPresent = true;
		}
		return bPresent;
	}

	/**
	 * checkSEPDSampleAttributes.
	 * 
	 * @param sampleRequestType        for sampleRequestType.
	 * @param bPresent                 for bPresent
	 * @param sampleStatusFo           for sampleStatusFo.
	 * @param sampleConfirmationListFo for sampleConfirmationListFo.
	 * @return boolean.
	 */
	private static boolean checkSEPDSampleAttributes(String sampleRequestType, boolean bPresent, String sampleStatusFo,
			String sampleConfirmationListFo) {
		LOGGER.info("Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--checkSEPDSampleAttributes");
		// Enters this loop when Request Type is NOT PPS and all samples in this request
		// has "Sample Status" set to - "Approved", "Cancelled", "To be Revised" or
		// when Request Type is PPS - all samples under this request has
		// all samples in this request has ["Sample Status" set to - "Approved",
		// "Cancelled", "To be Revised"] OR ["Sample Confirmation" as "Confirmed"].
		if ((!sampleRequestType.equalsIgnoreCase(SAMPLE_REQUEST_TYPE_PPS_SEPD)
				&& (FormatHelper.hasContent(sampleStatusFo)
						&& (sampleStatusFo.equalsIgnoreCase(SAMPLE_STATUS_APPROVED_SEPD)
								|| sampleStatusFo.equalsIgnoreCase(SAMPLE_STATUS_TO_BE_REVISED_SEPD)
								|| sampleStatusFo.equalsIgnoreCase(SAMPLE_STATUS_CANCELED_SEPD))))
				|| (sampleRequestType.equalsIgnoreCase(SAMPLE_REQUEST_TYPE_PPS_SEPD)
						&& ((FormatHelper.hasContent(sampleStatusFo)
								&& sampleStatusFo.equalsIgnoreCase(SAMPLE_STATUS_APPROVED_SEPD)
								|| sampleStatusFo.equalsIgnoreCase(SAMPLE_STATUS_TO_BE_REVISED_SEPD)
								|| sampleStatusFo.equalsIgnoreCase(SAMPLE_STATUS_CANCELED_SEPD))
								|| (FormatHelper.hasContent(sampleConfirmationListFo) && sampleConfirmationListFo
										.equalsIgnoreCase(SAMPLE_CONFIRMATION_LIST_CONFIRMED_SEPD))))) {
			bPresent = true;
		}
		return bPresent;
	}

	/**
	 * getSampleColorway.
	 * 
	 * @param sampleReqObj for sampleReqObj.
	 * @param sq           for sq.
	 * @return String.
	 * @throws WTException
	 */
	public static String getSampleColorway(LCSSampleRequest sampleReqObj, LCSSampleQuery sq) throws WTException {
		LOGGER.info("Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--getSampleColorway");
		Collection<FlexObject> colSamples = sq.findSamplesIdForSampleRequest(sampleReqObj, true);
		LOGGER.debug("In getSampleColorway method - colSamples " + colSamples);

		if (colSamples != null && !colSamples.isEmpty()) {
			Iterator itSamples = colSamples.iterator();
			String strSampleID = "";
			FlexObject fo;
			LCSSKU skuObj;
			String colorway = "";
			LCSSample lcsSample;
			while (itSamples.hasNext()) {
				fo = (FlexObject) itSamples.next();
				strSampleID = (String) fo.get(LCSSAMPLE_IDA2A2);
				lcsSample = (LCSSample) LCSQuery.findObjectById(SEPD_SAMPLE_OBJECT_ID + strSampleID);
				skuObj = (LCSSKU) lcsSample.getValue(SEPD_COLORWAY);
				if (skuObj != null) {
					colorway = (String) skuObj.getName();
				}
				LOGGER.debug("In getSampleColorway method - COLORWAY " + colorway);
				if (FormatHelper.hasContent(colorway)) {
					return colorway;
				}
			}
		}
		return "";
	}
	// Phase 13 End - SEPD Product Sample Workflow

	/**
	 * setSampleStateBasedOnRequestStatusAttribute.
	 * 
	 * Post Persist on SR to set the LC for Samples to Complete in case user
	 * manually changes the Request Status attribute on SR
	 * 
	 * @param object for object.
	 * @return void.
	 */
	public static void setSampleStateBasedOnRequestStatusAttribute(WTObject object) {
		LOGGER.info(
				"start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setSampleStateBasedOnRequestStatusAttribute");

		try {
			// Getting Sample Request object.
			LCSSampleRequest sampleReqObj = (LCSSampleRequest) object;

			// Getting current LC Template name.
			String currentTemplateName = sampleReqObj.getLifeCycleName();

			// Getting Request status value.
			String requestStatus = (String) sampleReqObj.getValue(SAMPLE_REQUEST_STATUS);

			// Getting Request type of Sample Request object.
			String requestType = sampleReqObj.getFlexType().getFullName();

			// Phase 13 Start - SEPD Product Sample Workflow
			// Getting Product Object.
			LCSProduct prodObj = getProduct(sampleReqObj);
			// Phase 13 Start - SEPD Product Sample Workflow

			LOGGER.debug(
					"In setSampleStateBasedOnRequestStatusAttribute Method - sampleReqObj = " + sampleReqObj.getName());
			LOGGER.debug("In setSampleStateBasedOnRequestStatusAttribute Method - requestStatus = " + requestStatus);
			LOGGER.debug("In setSampleStateBasedOnRequestStatusAttribute Method - currentTemplateName = "
					+ currentTemplateName);

			Collection<FlexObject> colSamples;
			LCSSampleQuery sq = new LCSSampleQuery();

			if (isValidFPDSampleType(requestType, currentTemplateName, prodObj)) {
				// Enters this loop when Request status is cancelled or closed.
				if (FormatHelper.hasContent(requestStatus) && (requestStatus.equalsIgnoreCase(REQUEST_STATUS_CANCELLED)
						|| requestStatus.equalsIgnoreCase(REQUEST_STATUS_CLOSED))) {
					setFPDStateAndSendEmail(sampleReqObj, requestStatus, prodObj, sq);
				}
			}
			// Phase 13 Start - SEPD Product Sample Workflow
			else if (isValidSEPDSampleType(requestType, currentTemplateName, prodObj)) {
				// Calling this method when Request status is cancelled or closed.
				setLCSStateClosedOrCancelled(sampleReqObj, requestStatus, prodObj, sq);
			}
			// Phase 13 End - SEPD Product Sample Workflow
		} catch (WTException e) {
			LOGGER.error("WTPropertyVetoException / WTException in setSampleStateBasedOnRequestStatusAttribute method: "
					+ e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info(
				"end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setSampleStateBasedOnRequestStatusAttribute");
	}

	/**
	 * @param sampleReqObj
	 * @param requestStatus
	 * @param prodObj
	 * @param sq
	 * @throws WTException
	 */
	private static void setFPDStateAndSendEmail(LCSSampleRequest sampleReqObj, String requestStatus, LCSProduct prodObj,
			LCSSampleQuery sq) throws WTException {
		Collection<FlexObject> colSamples;
		colSamples = sq.findSamplesIdForSampleRequest(sampleReqObj, true);

		if (colSamples != null && !colSamples.isEmpty()) {
			LOGGER.debug("In setSampleStateBasedOnRequestStatusAttribute Method - colSamples = " + colSamples);
			String strSampleID = "";
			FlexObject fo;
			LCSSample lcsSample;
			Iterator itSamples = colSamples.iterator();

			while (itSamples.hasNext()) {
				fo = (FlexObject) itSamples.next();
				strSampleID = (String) fo.get(LCSSAMPLE_IDA2A2);
				lcsSample = (LCSSample) LCSQuery.findObjectById(SEPD_SAMPLE_OBJECT_ID + strSampleID);
				LOGGER.debug("In setSampleStateBasedOnRequestStatusAttribute Method - lcsSample Name = "
						+ lcsSample.getName());
				String currentLCS = lcsSample.getState().getState().toString();
				LOGGER.debug("In setSampleStateBasedOnRequestStatusAttribute Method - currentLCS = " + currentLCS);
				if (!currentLCS.equals(LCS_CLOSED)) {
					LOGGER.debug("In setSampleStateBasedOnRequestStatusAttribute Method - currentLCS is Closed= "
							+ currentLCS);
					// Terminate All Tasks for all SRs
					// Setting Product Sample Lifecycle state as Closed.
					setSampleLCS(lcsSample, LCS_CLOSED);
				}
			}
		}
		// Enters this loop when Request status is cancelled to send mail notification.
		if (FormatHelper.hasContent(requestStatus) && requestStatus.equals(REQUEST_STATUS_CANCELLED)) {
			sendEmailForFPDCancelledStatus(sampleReqObj, prodObj);
		}
	}

	/**
	 * @param sampleReqObj
	 * @param prodObj
	 * @throws WTException
	 */
	private static void sendEmailForFPDCancelledStatus(LCSSampleRequest sampleReqObj, LCSProduct prodObj)
			throws WTException {
		// Getting Sourcing Config Master.
		LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) sampleReqObj.getSourcingMaster();

		// Getting Sourcing Config Object.
		LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);

		// Getting Supplier object.
		LCSSupplier vendorObj = (LCSSupplier) sourceObj.getValue(BUSINESS_SUPPLIER);

		// Getting vendor Group.
		String vendorGroup = (String) vendorObj.getValue(SUPPLIER_VENDOR_GROUP);

		// Phase 13 Start - commenting below lines as these lines were moved before
		// loop.
		// Getting Part Master Object.
		// LCSPartMaster prodMaster = sourceObj.getProductMaster();

		// Getting Product Object.
		// LCSProduct prodObj = VersionHelper.latestIterationOf(prodMaster);
		// Phase 13 End.
		String requestName = sampleReqObj.getName(); // Getting Request name of Sample Request.
		String prodName = prodObj.getName(); // Getting product name.

		// Getting Product Season Link.
		LCSSeasonProductLink spLink = SeasonProductLocator.getSeasonProductLink(prodObj);

		FlexObject fo = (FlexObject) spLink.getValue(PSL_DEVELOPER_OSO); // Storing flex object.

		String developerOSO = null;

		if (fo != null && fo.containsKey(FO_NAME) && FormatHelper.hasContent((String) fo.getData(FO_NAME))) {
			developerOSO = (String) fo.getData(FO_NAME);
		}

		SMEmailHelper statusMail = new SMEmailHelper();
		ArrayList<WTPrincipal> to = new ArrayList<>();
		if (FormatHelper.hasContent(vendorGroup)) {
			// Adding vendorGroup group to ArrayList<WTPrincipal>.
			@SuppressWarnings("deprecation")
			WTGroup group = (WTGroup) OrganizationServicesHelper.manager.getPrincipal(vendorGroup);
			if (group != null) {
				to.add(group);
			}
		}
		if (FormatHelper.hasContent(developerOSO)) {
			// Adding developerOSO user to ArrayList<WTPrincipal>.
			@SuppressWarnings("deprecation")
			WTUser user = wt.org.OrganizationServicesHelper.manager.getUser(developerOSO);
			if (user != null) {
				to.add(user);
			}
		}
		ClientContext lcsContext = ClientContext.getContext();

		// Getting From user details.
		wt.org.WTUser from = UserGroupHelper.getWTUser(lcsContext.getUserName());
		String mailHeader = LCSProperties.get("com.sportmaster.wc.utils.sendEmail.MailHeader");
		// Email body content.
		String body = (EMAIL_BODY_1 + requestName + EMAIL_BODY_2 + prodName + "\"");

		// Email Subject content.
		String strSubject = LCSProperties.get("com.sportmaster.wc.utils.sendEmail.MailSubject.FPDProductSample");
		// Calling sendEmail function of Class SMEmailHelper.
		statusMail.sendEmail(from, to, body, strSubject, mailHeader);
	}

	// Phase 13 Start- SEPD Product Sample Workflow
	/**
	 * setLCSStateClosedOrCancelled.
	 * 
	 * @param sampleReqObj  for sampleReqObj.
	 * @param requestStatus for requestStatus.
	 * @param prodObj       for prodObj.
	 * @param sq            for sq.
	 * @return void.
	 * @throws WTException.
	 */
	private static void setLCSStateClosedOrCancelled(LCSSampleRequest sampleReqObj, String requestStatus,
			LCSProduct prodObj, LCSSampleQuery sq) throws WTException {
		LOGGER.info("Start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setLCSStateClosedOrCancelled");
		Collection<FlexObject> colSamples;
		LOGGER.debug("In setLCSStateClosedOrCancelled Method -  REQUEST STATUS " + requestStatus);
		// Enters this loop when Request status is cancelled or closed.
		if (FormatHelper.hasContent(requestStatus) && (requestStatus.equalsIgnoreCase(REQUEST_STATUS_CANCELLED_SEPD)
				|| requestStatus.equalsIgnoreCase(REQUEST_STATUS_CLOSED_SEPD))) {
			colSamples = sq.findSamplesIdForSampleRequest(sampleReqObj, true);
			LOGGER.debug("In setLCSStateClosedOrCancelled Method - colSamples = " + colSamples);

			if (colSamples != null && !colSamples.isEmpty()) {
				Iterator itSamples = colSamples.iterator();
				String strSampleID = "";
				FlexObject fo;
				LCSSample lcsSample;
				while (itSamples.hasNext()) {
					fo = (FlexObject) itSamples.next();
					strSampleID = (String) fo.get(LCSSAMPLE_IDA2A2);
					lcsSample = (LCSSample) LCSQuery.findObjectById(SEPD_SAMPLE_OBJECT_ID + strSampleID);
					String currentLCS = lcsSample.getState().getState().toString();
					LOGGER.debug("In setLCSStateClosedOrCancelled Method - lcsSample Name = " + lcsSample.getName());
					LOGGER.debug("In setLCSStateClosedOrCancelled Method - currentLCS = " + currentLCS);
					if (!currentLCS.equals(LCS_CLOSED)) {
						// Terminate All Tasks for all SRs
						// Setting Product Sample Lifecycle state as Closed.
						setSampleLCS(lcsSample, LCS_CLOSED);
					}
				}
			}
			// Calling this method when Request status is cancelled to send mail
			// notification.
			getDetailsToSendMailNotificationOnCancelRequest(sampleReqObj, requestStatus, prodObj, sq);
		}
		LOGGER.info("end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setLCSStateClosedOrCancelled");
	}

	/**
	 * getDetailsToSendMailNotificationOnCancelRequest.
	 * 
	 * @param sampleReqObj  for sampleReqObj.
	 * @param requestStatus for requestStatus.
	 * @param prodObj       for prodObj.
	 * @param sq            for sq.
	 * @return void.
	 */
	private static void getDetailsToSendMailNotificationOnCancelRequest(LCSSampleRequest sampleReqObj,
			String requestStatus, LCSProduct prodObj, LCSSampleQuery sq) throws WTException {
		LOGGER.info(
				"Start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--getDetailsToSendMailNotificationOnCancelRequest");
		// Enters this loop when Request status is cancelled to send mail notification.
		if (FormatHelper.hasContent(requestStatus) && requestStatus.equals(REQUEST_STATUS_CANCELLED)) {
			// Getting Sourcing Config Master.
			LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) sampleReqObj.getSourcingMaster();

			// Getting Sourcing Config Object.
			LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);

			// Getting Supplier object.
			LCSSupplier vendorObj = (LCSSupplier) sourceObj.getValue(BUSINESS_SUPPLIER);

			String vendorGroup = "";
			if (vendorObj != null) {
				// Getting vendor Group.
				vendorGroup = (String) vendorObj.getValue(SUPPLIER_VENDOR_GROUP);
			}

			// Getting Season object.
			LCSSeason season = (LCSSeason) sampleReqObj.getValue(SEPD_SEASON_REQUESTED);

			// Getting Source to season link.
			LCSSourceToSeasonLink sourceToSeasonLink = new LCSSourcingConfigQuery().getSourceToSeasonLink(sourceObj,
					season);

			String requestName = sampleReqObj.getName(); // Getting Request name of Sample Request.
			String prodName = prodObj.getName(); // Getting product name.
			String colorway = getSampleColorway(sampleReqObj, sq);
			LOGGER.debug("In getDetailsToSendMailNotificationOnCancelRequestMethod - CANCEL Colorway" + colorway);

			String developerOSO = getDeveloperOSOUser(sourceObj, sourceToSeasonLink);

			SMEmailHelper statusMail = new SMEmailHelper();
			ArrayList<WTPrincipal> to = getToList(vendorGroup, developerOSO);
			ClientContext lcsContext = ClientContext.getContext();

			// Getting From user details.
			wt.org.WTUser from = UserGroupHelper.getWTUser(lcsContext.getUserName());
			String mailHeader = LCSProperties.get("com.sportmaster.wc.utils.sendEmail.MailHeader");

			String body = "";
			// Email body content.
			if (FormatHelper.hasContent(colorway)) {
				body = (EMAIL_BODY_1 + requestName + EMAIL_BODY_2 + prodName + "\" and \"" + colorway + "\"");
			} else {
				body = (EMAIL_BODY_1 + requestName + EMAIL_BODY_2 + prodName + "\"");
			}

			// Email Subject content.
			String strSubject = LCSProperties.get("com.sportmaster.wc.utils.sendEmail.MailSubject.SEPDProductSample");
			// Calling sendEmail function of Class SMEmailHelper.
			statusMail.sendEmail(from, to, body, strSubject, mailHeader);
		}
		LOGGER.info(
				"end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--getDetailsToSendMailNotificationOnCancelRequest");
	}

	/**
	 * getDeveloperOSOUser.
	 * 
	 * @param sourceObj          for sourceObj.
	 * @param sourceToSeasonLink for sourceToSeasonLink.
	 * @return String.
	 */
	private static String getDeveloperOSOUser(LCSSourcingConfig sourceObj, LCSSourceToSeasonLink sourceToSeasonLink) {
		LOGGER.info("Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--getDeveloperOSOUser");
		String developerOSO = "";

		if (sourceObj.getFlexType().getFullName().startsWith(APPAREL_SOURCE_TYPE)) {
			// Getting OSO Developer value.
			developerOSO = teamAssignmentOSODeveloperAttributeForSampleWF(OSO_DEVELOPER_APPAREL, sourceToSeasonLink);
			LOGGER.debug("In getDeveloperOSOUser Method - OSO DEVELOPER Apparel " + developerOSO);
		} else if (sourceObj.getFlexType().getFullName().startsWith(SEPD_SOURCE_TYPE)) {
			// Getting OSO Developer value.
			developerOSO = teamAssignmentOSODeveloperAttributeForSampleWF(OSO_DEVELOPER_SEPD, sourceToSeasonLink);
			LOGGER.debug("In getDeveloperOSOUser Method - OSO DEVELOPER SEPD " + developerOSO);
		}
		return developerOSO;
	}

	/**
	 * getToList.
	 * 
	 * @param vendorGroup  for vendorGroup.
	 * @param developerOSO for developerOSO.
	 * @return ArrayList<WTPrincipal>.
	 * @throws WTException.
	 */
	private static ArrayList<WTPrincipal> getToList(String vendorGroup, String developerOSO) throws WTException {
		LOGGER.info("Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--getToList");
		ArrayList<WTPrincipal> to = new ArrayList<>();

		if (FormatHelper.hasContent(developerOSO)) {
			// Adding developerOSO user to ArrayList<WTPrincipal>.
			@SuppressWarnings("deprecation")
			WTUser user = wt.org.OrganizationServicesHelper.manager.getUser(developerOSO);
			if (user != null) {
				to.add(user);
			}
			LOGGER.debug("In getToList method -- OSO DEVELOPER User " + to);
		}
		if (FormatHelper.hasContent(vendorGroup)) {
			// Adding vendorGroup group to ArrayList<WTPrincipal>.
			@SuppressWarnings("deprecation")
			WTGroup group = (WTGroup) OrganizationServicesHelper.manager.getPrincipal(vendorGroup);
			if (group != null) {
				to.add(group);
			}
			LOGGER.debug("In getToList method -- Vendor group " + to);
		}
		return to;
	}

	/**
	 * teamAssignmentOSODeveloperAttributeForSampleWF.
	 * 
	 * @param attName            for attName.
	 * @param sourceToSeasonLink for sourceToSeasonLink.
	 * @return String.
	 */
	public static String teamAssignmentOSODeveloperAttributeForSampleWF(String attName,
			LCSSourceToSeasonLink sourceToSeasonLink) {

		String result = null;
		try {

			if (sourceToSeasonLink != null) {
				FlexObject fo = (FlexObject) sourceToSeasonLink.getValue(attName);
				if (fo != null && fo.containsKey(FO_NAME) && FormatHelper.hasContent((String) fo.getData(FO_NAME))) {
					LOGGER.debug("\n Attribute Value====" + fo.getData(FO_NAME));
					LOGGER.debug("In teamAssignmentOSODeveloperAttributeForSampleWF method - OSO DEVELOPER "
							+ fo.getData(FO_NAME));
					result = (String) fo.getData(FO_NAME);
				}
			} else {
				result = "";
			}
		} catch (WTException e) {
			LOGGER.error("WTException in teamAssignmentOSODeveloperAttributeForSampleWF method: " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	// Phase 13 End - SEPD Product Sample Workflow

	/**
	 * setSampleReqAttBasedOnReqStatus.
	 * 
	 * @param object for object.
	 * @return void.
	 */
	public static void setSampleReqAttBasedOnReqStatus(WTObject object) {
		LOGGER.info("start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setSampleReqAttBasedOnReqStatus");
		try {
			if (object instanceof LCSSampleRequest) {

				// Getting Sample Request object.
				LCSSampleRequest sampleReqObj = (LCSSampleRequest) object;

				// Getting current LC Template name.
				String currentTemplateName = sampleReqObj.getLifeCycleName();

				// Phase 13 Start - SEPD Product Sample Workflow
				// Getting Product Object.
				LCSProduct prodObj = getProduct(sampleReqObj);
				// Phase 13 End - SEPD Product Sample Workflow

				LOGGER.debug(
						"In setSampleReqAttBasedOnReqStatus Method - sampleReqObj Name = " + sampleReqObj.getName());
				LOGGER.debug(
						"In setSampleReqAttBasedOnReqStatus Method - currentTemplateName = " + currentTemplateName);
				// Getting Request type of Sample Request object.
				String requestType = sampleReqObj.getFlexType().getFullName();

				if (isValidFPDSampleType(requestType, currentTemplateName, prodObj)) {
					String strSampleRequestStatus = (String) sampleReqObj.getValue(SAMPLE_REQUEST_STATUS);
					LOGGER.debug("In setSampleReqAttBasedOnReqStatus Method - strSampleRequestStatus= "
							+ strSampleRequestStatus);

					// SR Customizaiton:user manually changes request status as cancelled/ complete
					if (FormatHelper.hasContent(strSampleRequestStatus)
							&& (strSampleRequestStatus.equalsIgnoreCase(REQUEST_STATUS_CANCELLED)
									|| strSampleRequestStatus.equalsIgnoreCase(REQUEST_STATUS_CLOSED))
							&& sampleReqObj.getValue(REQUEST_CLOSE_DATE) == null) {
						sampleReqObj.setValue(REQUEST_CLOSE_DATE, setCurrentDate());

						// Added for Phase 13 - FPD Product Sample to set Sample status cancelled
						// For currently updated sample from UI, set the value from PRE PERSIST, for
						// other samples set it from POST PERSIST here.
						setFPDSampleStatusCancelledFromSampleRequestPerPersist(sampleReqObj, strSampleRequestStatus);
					}
				}
				// Phase 13 Start - SEPD Product Sample Workflow
				else if (prodObj != null
						&& (prodObj.getFlexType().getFullName().startsWith(PRODUCT_TYPE_ACC_SEPD)
								|| prodObj.getFlexType().getFullName().startsWith(PRODUCT_TYPE_SEPD))
						&& FormatHelper.hasContent(currentTemplateName)
						&& currentTemplateName.equals(SEPD_PRODUCT_SAMPLE_LC_TEMPLATE_NAME)) {
					String strSEPDSampleRequestStatus = (String) sampleReqObj.getValue(REQUEST_STATUS_SEPD);
					LOGGER.debug("In setSampleReqAttBasedOnReqStatus Method - strSEPDSampleRequestStatus= "
							+ strSEPDSampleRequestStatus);
					LOGGER.debug("In setSampleReqAttBasedOnReqStatus Method - SEPD SAMPLE REQUEST STATUS"
							+ strSEPDSampleRequestStatus);

					// Calling Method to set attributes value based on SEPD Sample Request
					// attributes.
					setSampleReqAttBasedOnSEPDReqStatus(sampleReqObj, strSEPDSampleRequestStatus);
				}
				// Phase 13 End - SEPD Product Sample Workflow
			}
		} catch (WTPropertyVetoException | WTException e) {
			LOGGER.error("WTPropertyVetoException / WTException in setSampleReqAttBasedOnReqStatus method: "
					+ e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setSampleReqAttBasedOnReqStatus");
	}

	/**
	 * @param sampleReqObj
	 * @param strSampleRequestStatus
	 * @throws WTException
	 */
	private static void setFPDSampleStatusCancelledFromSampleRequestPerPersist(LCSSampleRequest sampleReqObj,
			String strSampleRequestStatus) throws WTException {
		LOGGER.debug(
				"start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setFPDSampleStatusCancelledFromSampleRequestPerPersist");
		// Phase 13 - Start
		// When the "Request Status" is changed to "Cancelled". System should
		// automatically change Sample Status to "Cancelled" for all samples in this
		// Request
		// this check is required when LC template is default and we try to copy the product, system throws red line error upon copy
		if (sampleReqObj.getPersistInfo().getObjectIdentifier() != null) {
			LCSSampleQuery sQuery = new LCSSampleQuery();
			if (strSampleRequestStatus.equalsIgnoreCase(REQUEST_STATUS_CANCELLED)) {
				Collection<FlexObject> colSamples = sQuery.findSamplesIdForSampleRequest(sampleReqObj, true);
				LOGGER.debug("In setFPDSampleStatusCancelledPostPersist Method - colSamples = " + colSamples);
				if (colSamples != null && !colSamples.isEmpty()) {
					Iterator itSample = colSamples.iterator();
					String sampleID = "";
					LCSSample lcsSample;
					FlexObject fo;
					while (itSample.hasNext()) {
						fo = (FlexObject) itSample.next();
						sampleID = (String) fo.get(LCSSAMPLE_IDA2A2);
						lcsSample = (LCSSample) LCSQuery.findObjectById(SEPD_SAMPLE_OBJECT_ID + sampleID);
						LOGGER.debug("In setFPDSampleStatusCancelledPostPersist Method - lcsSample'" + lcsSample);
						LOGGER.debug("In setFPDSampleStatusCancelledPostPersist Method - lcsSample'"
								+ lcsSample.getValue(SAMPLE_STATUS));

						lcsSample.setValue(SAMPLE_STATUS, SAMPLE_STATUS_CANCELED);

						LOGGER.debug(
								"In setFPDSampleStatusCancelledFromSampleRequestPerPersist Method - sample status value after setting as Cancelled="
										+ lcsSample.getValue(SAMPLE_STATUS));
					}
				}
			}
		}
		LOGGER.debug(
				"end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setFPDSampleStatusCancelledFromSampleRequestPerPersist");
		// Phase 13 - End
	}

	// Phase 13 Start - SEPD Product Sample Workflow
	/**
	 * setSampleReqAttBasedOnSEPDReqStatus.
	 * 
	 * @param sampleReqObj               for sampleReqObj.
	 * @param strSEPDSampleRequestStatus for strSEPDSampleRequestStatus.
	 * @return void.
	 */
	private static void setSampleReqAttBasedOnSEPDReqStatus(LCSSampleRequest sampleReqObj,
			String strSEPDSampleRequestStatus) {
		LOGGER.info(
				"Start - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setSampleReqAttBasedOnSEPDReqStatus");
		try {
			// SR SEPD Customizaiton:user manually changes request status as cancelled/
			// complete
			if (FormatHelper.hasContent(strSEPDSampleRequestStatus)
					&& (strSEPDSampleRequestStatus.equalsIgnoreCase(REQUEST_STATUS_CANCELLED_SEPD)
							|| strSEPDSampleRequestStatus.equalsIgnoreCase(REQUEST_STATUS_CLOSED_SEPD))
					&& sampleReqObj.getValue(REQUEST_CLOSE_DATE_SEPD) == null) {
				// Setting Request close date attribute with current date.
				sampleReqObj.setValue(REQUEST_CLOSE_DATE_SEPD, setCurrentDate());
			}
		} catch (WTPropertyVetoException | WTException e) {
			LOGGER.error("WTPropertyVetoException / WTException in setSampleReqAttBasedOnSEPDReqStatus method: "
					+ e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info(
				"end - Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--setSampleReqAttBasedOnSEPDReqStatus");
	}
	// Phase 13 End - SEPD Product Sample Workflow.

}
