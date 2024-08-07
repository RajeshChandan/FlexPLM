package com.sportmaster.wc.document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentLogic;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.UserGroupHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.utils.SMEmailHelper;

import wt.fc.WTObject;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.lifecycle.State;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

public class SMSEPDImagePageWFPlugin {

	/**
	 * pageType key
	 */
	public static final String PAGETYPE = LCSProperties.get("com.sportmaster.wc.document.SMImagePageWFPlugin.pageType");
	/**
	 * Detail Sketch key
	 */
	public static final String DETAILSKETCH = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.detailSketch");
	/**
	 * ownerReference key
	 */
	public static final String OWNERREFERENCE = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.ownerReference");

	/**
	 * Image Page Name key
	 */
	public static final String IMGPAGENAME = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imgPageName");
	/**
	 * Image Page Status key
	 */
	public static final String IMAGEPAGESTATUS = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imagePageStatus");
	/**
	 * Image Life Cycle State In Work.
	 */
	public static final String IMAGE_LIFECYCLE_STATE_INWORK = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imageLifeCycleStateInWork");

	/**
	 * Image Page Status Cancelled key
	 */
	public static final String IMGCANCELLED = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imgCancelled");
	/**
	 * Image Life Cycle State Cancelled.
	 */
	public static final String IMAGE_LIFECYCLE_STATE_CANCELLED = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imageLifeCycleStateCancelled");

	/**
	 * SEPD Image Page Life Cycle template
	 */
	public static final String SEPDIMGLCTEMPLATE = LCSProperties
			.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.imgLCTemplate");
	/**
	 * Designer approval key
	 */
	public static final String DESIGNERAPPROVAL = LCSProperties
			.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.smDesignerApprovalSEPD");

	/**
	 * Designer approval - for update from BM key
	 */
	public static final String DESIGNERAPPROVALFORUPDATEFROMBM = LCSProperties
			.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.smDesignerApprovalSEPD.smForUpdateFromBM");

	/**
	 * Designer approval - Ready For Review key
	 */
	public static final String DESIGNERAPPROVALREADYFORREVIEW = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.smDesignerApprovalSEPD.readyForReview");

	/**
	 * Technologist Approval key
	 */
	public static final String TECHNOLOGISTAPPROVAL = LCSProperties
			.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.smTechnologistApprovalSEPD");

	/**
	 * Technologist Approval - Checked by BM key
	 */
	public static final String TECHAPPROVALCHECKEDBYBM = LCSProperties
			.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.smTechnologistApprovalSEPD.smForUpdateFromBM");

	/**
	 * Technologist Approval - Checked no Problem key
	 */
	public static final String TECHAPPROVALCHECKNOPROBLEM = LCSProperties.get(
			"com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.smTechnologistApprovalSEPD.smApprovedByTechnologist");

	/**
	 * Technologist Approval Checked with Problem key
	 */
	public static final String TECHAPPROVALCHECKWITHPROBLEM = LCSProperties.get(
			"com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.smTechnologistApprovalSEPD.smForUpdateFromTechnologist");

	/**
	 * Image Life Cycle State - Technologist Review
	 */
	public static final String IMAGE_LIFECYCLE_STATE_TECHNOLOGISTREVIEW = LCSProperties
			.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.imageLifeCycleStateTechReview");

	/**
	 * Image Life Cycle State - Design for Review.
	 */
	public static final String IMAGE_LIFECYCLE_STATE_DESIGNFORREVIEW = LCSProperties
			.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.imageLifeCycleStateDesignForReview");

	/**
	 * Image Life Cycle State - BM Tech Review.
	 */
	public static final String IMAGE_LIFECYCLE_STATE_BMTECHREVIEW = LCSProperties
			.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.imageLifeCycleStateBMTechReview");
	/**
	 * designer key
	 */
	public static final String DESIGNERKEY = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.designerKey");

	/**
	 * Brand Manager Approval key
	 */
	public static final String BRANDMANAGERAPPROVAL = LCSProperties
			.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.smBrandManagerApproval");

	/**
	 * Brand Manager Approval - Approved by BM key
	 */
	public static final String BRANDMANAGERAPPROVAL_APPROVEDBYBM = LCSProperties
			.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.smBrandManagerApproval.smApprovedByBM");

	/**
	 * BM Comments key
	 */
	public static final String BMCOMMENTS = LCSProperties
			.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.smBMCommentSEPDTA");
	/**
	 * Technologist Comments key
	 */
	public static final String TECHCOMMENTS = LCSProperties
			.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.smTechnologistCommentSEPDTA");

	/**
	 * name key
	 */
	public static final String FO_NAME = LCSProperties.get("com.sportmaster.wc.document.SMImagePageWFPlugin.FOName");

	public static final String CURRENTSTATESTRING = "current state--->>>";

	/**
	 * LOGGER.
	 */
	public static final Logger logger = Logger.getLogger(SMSEPDImagePageWFPlugin.class);

	/**
	 * Method to set image page LC States PRE_PERSIST SSP
	 * 
	 * @param wtobject
	 * @return
	 * @throws WTException
	 * 
	 */
	public static void setSEPDImagePageData(WTObject obj) throws WTException {
		logger.debug("start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--setSEPDImagePageData");
		try {
			// Check if obj instanceof LCSDocument
			if (obj instanceof LCSDocument) {
				LCSDocument imgObj = (LCSDocument) obj;
				logger.debug("In setSEPDImagePageData method: PrePersist isHasContents==" + imgObj.isHasContents());
				logger.debug("imgObj.getCheckoutInfo()====" + imgObj.getCheckoutInfo());

				// Check if image page name start with Document/image pages and if the object is
				// the working copy
				if (imgObj.getCheckoutInfo() != null
						&& imgObj.getCheckoutInfo().getState().toString().equalsIgnoreCase("wrk")
						&& (imgObj.getFlexType().getFullName(true)).startsWith(IMGPAGENAME)) {

					logger.debug("setSEPDImagePageData obj=" + obj);

					String currentTemplateName = imgObj.getLifeCycleName();
					logger.debug("In setSEPDImagePageData method: currentTemplateName==" + currentTemplateName);

					logger.debug("setSEPDImagePageData method: imgObj name=" + imgObj.getName());

					// Get PS Link object
					LCSProductSeasonLink lcsSeasonalProd = (LCSProductSeasonLink) SMImagePagePlugins
							.getSeasonalProductsForImagePages(imgObj);
					if (lcsSeasonalProd != null) {
						logger.debug("lcsSeasonalProd=" + lcsSeasonalProd);
						logger.debug("currentTemplateName=" + currentTemplateName);

						String strProductType = lcsSeasonalProd.getFlexType().getFullName();
						// Check if Image Page belongs to a Seasonal Product and the lifecycle tempalte
						// is "Sportmaster SEPD Image Page Lifecycle"
						if (strProductType.startsWith("SEPD") && currentTemplateName.equals(SEPDIMGLCTEMPLATE)) {
							logger.debug("IMGLCTEMPLATE=" + SEPDIMGLCTEMPLATE);
							setImagePageDataMethod(imgObj);
						}
					}
				}
			}
		} catch (WTException e) {
			logger.error("WTException in SMSEPDImagePageWFPlugin - setSEPDImagePageData method: " + e.getMessage());
			e.printStackTrace();

		} catch (WTPropertyVetoException e) {
			logger.error("WTPropertyVetoException in SMSEPDImagePageWFPlugin and METHOD--setSEPDImagePageData -->> "
					+ e.getMessage());
			e.printStackTrace();
		}
		logger.debug("end - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--setSEPDImagePageData");
	}

	/**
	 * Called from PRE_PERSIST SSP
	 * 
	 * @param imgObj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void setImagePageDataMethod(LCSDocument imgObj) throws WTException, WTPropertyVetoException {
		logger.debug("start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--setImagePageDataMethod");
		// Page Type (pageType) = Detail sketch (detailSketch)
		// Designer Approval (smDesignerApprovalSEPD) = Ready for Review
		// (smReadyForReview)
		// Created -> In Work
		String imagePgType = (String) imgObj.getValue(PAGETYPE);
		String designerApproval = (String) imgObj.getValue(DESIGNERAPPROVAL);
		logger.debug("imagePgType==" + imagePgType);
		logger.debug("Designer Approval==" + designerApproval);
		LCSDocumentLogic dLogic = new LCSDocumentLogic();
		String strTechApproval = (String) imgObj.getValue(TECHNOLOGISTAPPROVAL);

		logger.debug("imgObj.isHasContents()==" + imgObj.isHasContents());
		logger.debug("imgObj.getLifeCycleState().toString()==" + imgObj.getLifeCycleState().toString());
		logger.debug("FormatHelper.hasContent(strTechApproval)==" + FormatHelper.hasContent(strTechApproval));
		logger.debug("In setImagePageData method: PrePersist isAImanaged==" + dLogic.isAImanaged(imgObj));

		// For task 1, manually set state to in work and start the workflow task
		// Check if Image Page type is detail sketch and designer approval is ready for
		// review
		// and if not AI managerd (for image page created from FlexPLM UI)or
		// if AI Managed and Has Content is true (in case of Design
		// Suite, when only thumbnail is uploaded the object gets saved, which inturn
		// calls plugins. To avoid this and to execute the below logic only when user
		// publishes from AI, the hasContent additional check is added. When thumbnail
		// is uploaded this is set to false, only during publish this gets set to )
		if (imagePgType.equalsIgnoreCase(DETAILSKETCH) && FormatHelper.hasContent(designerApproval)
				&& designerApproval.equalsIgnoreCase(DESIGNERAPPROVALREADYFORREVIEW)
				&& !imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_INWORK)
				&& !FormatHelper.hasContent(strTechApproval) // this check is required to trigger BM Tech Review state
																// when value for tech review is "Checked no Problem" or
																// "checked with Problem"
				&& (!dLogic.isAImanaged(imgObj) || (dLogic.isAImanaged(imgObj) && imgObj.isHasContents()))) {
			logger.debug("imgObj before setStateInWork method=" + imgObj);
			setStateInWork(imgObj);
		}
		// Set LC state based on Image Page Attribute Value change for other states
		else if (imagePgType.equalsIgnoreCase(DETAILSKETCH)) {
			logger.debug("imgObj before setIPStateBasedOnIPAttributes method=" + imgObj);
			setIPStateBasedOnIPAttributes(imgObj);

		}
		logger.debug("end - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--setImagePageDataMethod");
	}

	/**
	 * Called from PRE_PERSIST SSP to set In Work LC State
	 * 
	 * @param imgObj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void setStateInWork(LCSDocument imgObj) throws WTException, WTPropertyVetoException {
		logger.debug("start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--setStateInWork");
		// This logic is added to terminate the currently open WF Task when the previous
		// Image Page is
		// not null and user wants to re-initiate the Image
		// Page WF for In Work task.
		LCSDocument predDoc = (LCSDocument) VersionHelper.predecessorOf(imgObj);
		// Check if previous version of Image Page state is not DESIGN FOR REVIEW
		if (predDoc != null) {
			// Terminate the WF task once LC State is SD Approved
			logger.debug("setStateInWork method PrePersist :Terminate the Workflow task once LCState is--->>>"
					+ predDoc.getLifeCycleState().toString());
			SMImagePagePlugins.terminateWorkflowTask(predDoc);
		}

		logger.debug("^current state=" + imgObj.getLifeCycleState().toString());
		LifeCycleTemplateReference ref = imgObj.getLifeCycleTemplate();
		LifeCycleState st = new LifeCycleState();
		State inWorkState = State.toState(IMAGE_LIFECYCLE_STATE_INWORK);
		logger.debug("Setting LC to ===" + IMAGE_LIFECYCLE_STATE_INWORK);
		st.setLifeCycleId(ref);
		st.setState(inWorkState);
		imgObj.setState(st);
		logger.debug("after change to inwork state==" + imgObj.getLifeCycleState().toString());
		logger.debug("end - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--setStateInWork");
	}

	/**
	 * Called from PRE_PERSIST SSP to set other states and its attribute values
	 * except In Work LC State
	 * 
	 * @param imgObj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void setIPStateBasedOnIPAttributes(LCSDocument imgObj) throws WTException, WTPropertyVetoException {
		logger.debug("start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--setIPStateBasedOnIPAttributes");
		String strTechApproval = (String) imgObj.getValue(TECHNOLOGISTAPPROVAL);
		String strDesignerApproval = (String) imgObj.getValue(DESIGNERAPPROVAL);
		String strBMApproval = (String) imgObj.getValue(BRANDMANAGERAPPROVAL);
		LifeCycleTemplateReference ref = imgObj.getLifeCycleTemplate();
		LifeCycleState st = new LifeCycleState();
		logger.debug(CURRENTSTATESTRING + imgObj.getLifeCycleState().toString());
		LCSDocument predDoc = (LCSDocument) VersionHelper.predecessorOf(imgObj);
		logger.debug("predDoc lc state===" + predDoc.getLifeCycleState().toString());
		String imagePageStatus = (String) imgObj.getValue(IMAGEPAGESTATUS);
		logger.debug("imagePageStatus->>>" + imagePageStatus);
		// terminate all tasks if image page status is cancelled and set state as
		// Cancelled
		if (imagePageStatus.equalsIgnoreCase(IMGCANCELLED)
				&& !imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_CANCELLED)) {
			checkImagePageStatusCancelled(imgObj, ref, st);
		}
		// Technologist Review (smTechnologistApprovalSEPD) - Checked by BM
		// In Work -> Technologist Review
		else if (FormatHelper.hasContent(strTechApproval) && strTechApproval.equalsIgnoreCase(TECHAPPROVALCHECKEDBYBM)
				&& !imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_TECHNOLOGISTREVIEW)) {
			setStateTechnologistReview(imgObj, ref, st, predDoc);
		}
		// Check if Designer Approval is For Update from BM, set state to
		// DesignforReview
		else if (FormatHelper.hasContent(strDesignerApproval) && strDesignerApproval.equalsIgnoreCase(DESIGNERAPPROVALFORUPDATEFROMBM)
				&& !imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_DESIGNFORREVIEW)) {
			setStateDesignForReview(imgObj, ref, st, predDoc);
		}
		// Technologist changes "Checked No Problems" in Technologist Review", change
		// state Technologist Review -> BM Tech Review
		else if (FormatHelper.hasContent(strTechApproval) && strTechApproval.equalsIgnoreCase(TECHAPPROVALCHECKNOPROBLEM)
				&& !imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_BMTECHREVIEW)) {
			setStateBMTechReviewCheckedNoProb(imgObj, ref, st, predDoc);
		}
		// Technologist changes "Checked with Problems" in Technologist Review", change
		// state Technologist Review -> BM Tech Review
		else if (FormatHelper.hasContent(strTechApproval) && strTechApproval.equalsIgnoreCase(TECHAPPROVALCHECKWITHPROBLEM)
				&& !imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_BMTECHREVIEW)) {
			setStateBMTechReviewCheckedWithProb(imgObj, ref, st, predDoc);
		}
		// if state = BM Tech Review
		else if (imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_BMTECHREVIEW)) {
			checkBMTechReviewState(imgObj, strDesignerApproval, strBMApproval, predDoc);
		}

		logger.debug("end - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--setIPStateBasedOnIPAttributes");
	}

	/**
	 * @param imgObj
	 * @param predDoc
	 * @param imagePgType
	 * @param designerApproval
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void checkImagePageStatusCancelled(LCSDocument imgObj, LifeCycleTemplateReference ref,
			LifeCycleState st) throws WTException, WTPropertyVetoException {
		logger.debug("start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--checkImagePageStatusCancelled");
		LCSDocument predDoc = (LCSDocument) VersionHelper.predecessorOf(imgObj);
		logger.debug(CURRENTSTATESTRING + imgObj.getLifeCycleState().toString());
		String setState = IMAGE_LIFECYCLE_STATE_CANCELLED;
		/*
		 * At any time, User can open Image Page in Update Mode, set "Cancelled" value
		 * in the "Image Page status" attribute, Save data => Workflow should be
		 * terminated (all open tasks should be automatically closed) and IP Workflow
		 * closed with the Canceled stage
		 */
		terminateCurrentWFAndSetNewState(imgObj, ref, st, predDoc, setState);
	}

	/**
	 * @param imgObj
	 * @param ref
	 * @param st
	 * @param predDoc
	 * @throws WTPropertyVetoException
	 */
	private static void setStateTechnologistReview(LCSDocument imgObj, LifeCycleTemplateReference ref,
			LifeCycleState st, LCSDocument predDoc) throws WTPropertyVetoException {
		logger.debug("start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--setStateTechnologistReview");
		String setState = IMAGE_LIFECYCLE_STATE_TECHNOLOGISTREVIEW;
		logger.debug(CURRENTSTATESTRING + imgObj.getLifeCycleState().toString());

		terminateCurrentWFAndSetNewState(imgObj, ref, st, predDoc, setState);
	}

	/**
	 * @param imgObj
	 * @param ref
	 * @param st
	 * @param predDoc
	 * @param setState
	 * @throws WTPropertyVetoException
	 */
	private static void terminateCurrentWFAndSetNewState(LCSDocument imgObj, LifeCycleTemplateReference ref,
			LifeCycleState st, LCSDocument predDoc, String setState) throws WTPropertyVetoException {
		logger.debug("start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--terminateCurrentWFAndSetNewState");
		if (predDoc != null) {
			// Terminate the WF task once LC State is SD Approved
			logger.debug(
					"terminateCurrentWFAndSetNewState method - PrePersistSSP: Terminate WF task when LC State is --->>>"
							+ predDoc.getLifeCycleState().toString());
			SMImagePagePlugins.terminateWorkflowTask(predDoc);
		}
		logger.debug("Set state to--->>>" + setState);
		State stateChange = State.toState(setState);
		st.setLifeCycleId(ref);
		st.setState(stateChange);
		imgObj.setState(st);
		logger.debug("after change to state==" + imgObj.getLifeCycleState().toString());
		logger.debug("end - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--terminateCurrentWFAndSetNewState");
	}

	/**
	 * @param imgObj
	 * @param ref
	 * @param st
	 * @param predDoc
	 * @throws WTPropertyVetoException
	 */
	private static void setStateDesignForReview(LCSDocument imgObj, LifeCycleTemplateReference ref, LifeCycleState st,
			LCSDocument predDoc) throws WTPropertyVetoException {
		logger.debug("start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--setStateDesignForReview");
		String setState = IMAGE_LIFECYCLE_STATE_DESIGNFORREVIEW;
		logger.debug(CURRENTSTATESTRING + imgObj.getLifeCycleState().toString());

		imgObj.setValue(BRANDMANAGERAPPROVAL, "");
		imgObj.setValue(TECHNOLOGISTAPPROVAL, "");

		terminateCurrentWFAndSetNewState(imgObj, ref, st, predDoc, setState);
	}

	/**
	 * @param imgObj
	 * @param ref
	 * @param st
	 * @param predDoc
	 * @throws WTPropertyVetoException
	 */
	private static void setStateBMTechReviewCheckedNoProb(LCSDocument imgObj, LifeCycleTemplateReference ref,
			LifeCycleState st, LCSDocument predDoc) throws WTPropertyVetoException {
		logger.debug("start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--setStateBMTechReviewCheckedNoProb");
		logger.debug(CURRENTSTATESTRING + imgObj.getLifeCycleState().toString());
		String setState = IMAGE_LIFECYCLE_STATE_BMTECHREVIEW;
		terminateCurrentWFAndSetNewState(imgObj, ref, st, predDoc, setState);
	}

	/**
	 * @param imgObj
	 * @param ref
	 * @param st
	 * @param predDoc
	 * @throws WTPropertyVetoException
	 */
	private static void setStateBMTechReviewCheckedWithProb(LCSDocument imgObj, LifeCycleTemplateReference ref,
			LifeCycleState st, LCSDocument predDoc) throws WTPropertyVetoException {
		logger.debug("start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--setStateBMTechReviewCheckedWithProb");
		logger.debug(CURRENTSTATESTRING + imgObj.getLifeCycleState().toString());
		String setState = IMAGE_LIFECYCLE_STATE_BMTECHREVIEW;
		// blank out brand manager value
		imgObj.setValue(BRANDMANAGERAPPROVAL, "");
		terminateCurrentWFAndSetNewState(imgObj, ref, st, predDoc, setState);
	}

	/**
	 * @param imgObj
	 * @param strDesignerApproval
	 * @param strBMApproval
	 * @param ref
	 * @param st
	 * @param predDoc
	 * @throws WTPropertyVetoException
	 */
	private static void checkBMTechReviewState(LCSDocument imgObj, String strDesignerApproval, String strBMApproval,
			LCSDocument predDoc) throws WTPropertyVetoException {
		logger.debug("start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--checkBMTechReviewState");
		logger.debug("strDesignerApproval=" + strDesignerApproval);
		logger.debug("strBMApproval=" + strBMApproval);
		logger.debug(CURRENTSTATESTRING + imgObj.getLifeCycleState().toString());
		//String setState = IMAGE_LIFECYCLE_STATE_DESIGNFORREVIEW;
		if (strBMApproval.equalsIgnoreCase(BRANDMANAGERAPPROVAL_APPROVEDBYBM)) {
			logger.debug(CURRENTSTATESTRING + imgObj.getLifeCycleState().toString());
			// Check if previous version of LC State = IMAGE_LIFECYCLE_STATE_BMTECHREVIEW
			if (predDoc != null && predDoc.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_BMTECHREVIEW)) {
				// Terminate the WF task and end the workflow
				logger.debug(
						"method checkBMTechReviewState - Pre Persist SSP: Terminate WF task once LC State is --->>>"
								+ predDoc.getLifeCycleState().toString());
				SMImagePagePlugins.terminateWorkflowTask(predDoc);
			}
		} 
		/*
		else if (strDesignerApproval.equalsIgnoreCase(DESIGNERAPPROVALFORUPDATEFROMBM)) {
			logger.debug(CURRENTSTATESTRING + imgObj.getLifeCycleState().toString());
			imgObj.setValue(BRANDMANAGERAPPROVAL, "");
			imgObj.setValue(TECHNOLOGISTAPPROVAL, "");

			terminateCurrentWFAndSetNewState(imgObj, ref, st, predDoc, setState);
		} */

		logger.debug("end - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--checkBMTechReviewState");
	}

	/**
	 * Post Update Persist SSP - to trigger wf task and send email notification
	 * Method to start the workflow task for all LC States
	 * 
	 * @param obj
	 * @throws WTException
	 */
	public static void setSEPDImagePageStatePostUpdatePersist(WTObject obj) throws WTException {
		logger.debug(
				"start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--setSEPDImagePageStatePostUpdatePersist");
		try {
			// Check if obj instanceof LCSDocument
			if (obj instanceof LCSDocument) {
				LCSDocument imgObj = (LCSDocument) obj;
				logger.debug("In setSEPDImagePageStatePostUpdatePersist checkedout state=="
						+ imgObj.getCheckoutInfo().getState().toString());
				logger.debug("In setSEPDImagePageStatePostUpdatePersist isHasContents==" + imgObj.isHasContents());
				logger.debug("Image Location==" + imgObj.getLocation());
				LCSProductSeasonLink spLink = (LCSProductSeasonLink) SMImagePagePlugins
						.getSeasonalProductsForImagePages(imgObj);
				String currentTemplateName = imgObj.getLifeCycleName();
				if (spLink != null) {
					String strProductType = spLink.getFlexType().getFullName();
					logger.debug("currentTemplateName==" + currentTemplateName);
					// If product type starts with sepd and template name = sportmaster sepd image
					// page lifecycle Template
					if (strProductType.startsWith("SEPD") && currentTemplateName.equals(SEPDIMGLCTEMPLATE)) {
						FlexObject fo = (FlexObject) spLink.getValue(DESIGNERKEY);
						logger.debug("Image Page Name--->>>" + imgObj.getName());
						logger.debug("isCheckedOut" + VersionHelper.isCheckedOut(imgObj));
						logger.debug("imgObj.getThumbnailLocation()--" + imgObj.getThumbnailLocation());
						logger.debug("VersionHelper.isCheckedOut(imgObj)=" + VersionHelper.isCheckedOut(imgObj));
						logger.debug("co info==" + imgObj.getCheckoutInfo().getState().toString());

						// Check if image page name start with Document/image pages
						if (imgObj.isLatestIteration()
								&& (imgObj.getFlexType().getFullName(true)).startsWith(IMGPAGENAME) && fo != null
								&& fo.containsKey(FO_NAME) && FormatHelper.hasContent((String) fo.getData(FO_NAME))
								&& imgObj.getCheckoutInfo().getState().toString().equalsIgnoreCase("wrk")) {
							logger.debug("before calling startSEPDImagePageWorkflowTaskforEachState...");
							startSEPDImagePageWorkflowTaskforEachState(imgObj);
						}
					}
				}
			}
		} catch (WTException e) {
			logger.error("WTException in SMSEPDImagePageWFPlugin - setSEPDImagePageStatePostUpdatePersist method: "
					+ e.getMessage());
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			logger.error(
					"WTPropertyVetoException in SMSEPDImagePageWFPlugin - setSEPDImagePageStatePostUpdatePersist method: "
							+ e.getMessage());
			e.printStackTrace();
		}
		logger.debug("end - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--setSEPDImagePageStatePostUpdatePersist");

	}

	/**
	 * @param imgObj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void startSEPDImagePageWorkflowTaskforEachState(LCSDocument imgObj)
			throws WTException, WTPropertyVetoException {
		logger.debug(
				"start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--startSEPDImagePageWorkflowTaskforEachState");
		LCSDocument predDoc;
		String imagePgType;
		imagePgType = (String) imgObj.getValue(PAGETYPE);
		predDoc = (LCSDocument) VersionHelper.predecessorOf(imgObj);
		String designerApproval;
		designerApproval = (String) imgObj.getValue(DESIGNERAPPROVAL);
		String strTechApproval = (String) imgObj.getValue(TECHNOLOGISTAPPROVAL);
		logger.debug("imagePgType==" + imagePgType);
		logger.debug("designerApproval==" + designerApproval);
		logger.debug("imgObj.getLifeCycleState()=" + imgObj.getLifeCycleState().toString());

		// Check if Image Page type is detail sketch and designer approval is uploaded
		// by designer and current image page object is In Work State
		if (imagePgType.equalsIgnoreCase(DETAILSKETCH) && predDoc != null) {
			startWFTaskForEachState(imgObj, predDoc, designerApproval, strTechApproval);
		}
		logger.debug(
				"end - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--startSEPDImagePageWorkflowTaskforEachState");
	}

	/**
	 * @param imgObj
	 * @param predDoc
	 * @param designerApproval
	 * @param strTechApproval
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void startWFTaskForEachState(LCSDocument imgObj, LCSDocument predDoc, String designerApproval,
			String strTechApproval) throws WTException, WTPropertyVetoException {
		logger.debug("start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--startWFTaskForEachState");
		logger.debug("in method startWFTaskForEachState imgObj==" + imgObj.getLifeCycleState().toString());
		logger.debug("in method startWFTaskForEachState predDoc==" + predDoc.getLifeCycleState().toString());

		// Check if previous Image Page object's state not INWORK, so the task is not
		// assigned multiple times
		if (FormatHelper.hasContent(designerApproval)
				&& designerApproval.equalsIgnoreCase(DESIGNERAPPROVALREADYFORREVIEW)
				&& imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_INWORK)
				&& !predDoc.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_INWORK)) {
			// Call method to Start Task
			SMImagePagePlugins.startWorkflow(imgObj,
					LCSProperties.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.inWorkTask"));
		} else if (FormatHelper.hasContent(strTechApproval) && strTechApproval.equalsIgnoreCase(TECHAPPROVALCHECKEDBYBM)
				&& imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_TECHNOLOGISTREVIEW)
				&& !predDoc.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_TECHNOLOGISTREVIEW)) {
			// Call method to Start Task
			SMImagePagePlugins.startWorkflow(imgObj,
					LCSProperties.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.techReviewTask"));
		} else if (FormatHelper.hasContent(designerApproval)
				&& designerApproval.equalsIgnoreCase(DESIGNERAPPROVALFORUPDATEFROMBM)
				&& imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_DESIGNFORREVIEW)
				&& !predDoc.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_DESIGNFORREVIEW)) {
			// Call method to Start Task
			SMImagePagePlugins.startWorkflow(imgObj,
					LCSProperties.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.designerReviewTask"));
			String designer = getEmailUser(imgObj, DESIGNERKEY);
			logger.debug("designer===" + designer);
			// Send email to Designer from Team Assignment
			sendEmailToUser(imgObj, designer, "Update Design");
		} else if (FormatHelper.hasContent(strTechApproval)
				&& strTechApproval.equalsIgnoreCase(TECHAPPROVALCHECKNOPROBLEM)
				&& imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_BMTECHREVIEW)
				&& !predDoc.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_BMTECHREVIEW)) {
			// Call method to Start Task
			SMImagePagePlugins.startWorkflow(imgObj,
					LCSProperties.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.bmTechReviewTask"));
		} else if (FormatHelper.hasContent(strTechApproval)
				&& strTechApproval.equalsIgnoreCase(TECHAPPROVALCHECKWITHPROBLEM)
				&& imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_BMTECHREVIEW)
				&& !predDoc.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_BMTECHREVIEW)) {
			// Call method to Start Task
			SMImagePagePlugins.startWorkflow(imgObj,
					LCSProperties.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.bmTechReviewTask"));
			String brandManager = getEmailUser(imgObj, LCSProperties
					.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.productSeason.smBrandManager"));
			logger.debug("mailto brandManager===" + brandManager);
			// Send email to Brand Manager from Team Assignment
			sendEmailToUser(imgObj, brandManager, "BM Design Approval");
		}
		logger.debug("end - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--startWFTaskForEachState");
	}

	/**
	 * Send email
	 * 
	 * @param imgObj
	 * @param designer
	 * @param statusMail
	 * @param from
	 * @throws WTException
	 */
	private static void sendEmailToUser(LCSDocument imgObj, String toUser, String strTaskName) throws WTException {
		logger.debug("start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--sendEmailToUser");
		String SM_IMAGEPAGE_SEASON = LCSProperties.get("com.sportmaster.wc.document.SMImagePageWFPlugin.smSeason",
				"smSeason");
		LCSSeason season = (LCSSeason) imgObj.getValue(SM_IMAGEPAGE_SEASON);
		String strSeasName = "";
		if (season != null) {
			strSeasName = season.getName();
		}
		ClientContext lcsContext = ClientContext.getContext();
		// Get current user to set as email from
		wt.org.WTUser from = UserGroupHelper.getWTUser(lcsContext.getUserName());
		logger.debug("from user== " + from);
		SMEmailHelper statusMail = new SMEmailHelper();
		String strBMComments = (String) imgObj.getValue(BMCOMMENTS);
		logger.debug("strBMComments== " + strBMComments);
		String strTechComments = (String) imgObj.getValue(TECHCOMMENTS);
		logger.debug("strTechComments== " + strTechComments);
		String strUrl = constructFlexPLMURL();
		String strOID = "";
		strOID = FormatHelper.getNumericObjectIdFromObject(imgObj);

		logger.debug("document oid=" + strOID);
		logger.debug("toUser===" + toUser);
		// get user to list
		//Vector srDesto = new java.util.Vector();
		ArrayList<WTPrincipal> srDesToList = new ArrayList<>(); 
		WTUser srUser = wt.org.OrganizationServicesHelper.manager.getUser(toUser);
		if (srUser != null) {
			logger.debug("toUser==" + srUser.getName());
			//srDesto.addElement(srUser);
			srDesToList.add(srUser);
		}

		String srMailHeader = LCSProperties.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.MailHeader");
		String strSubject = String.format(
				LCSProperties.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.MailSubject"), strTaskName);
		String strMailContent = String.format(
				LCSProperties.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.MailContent"), strTaskName);
		StringBuilder sb = new StringBuilder();
		sb.append(strMailContent).append("<br>").append("<br>");
		sb.append("<html>");
		sb.append("<head>");
		sb.append("</head>");
		sb.append("<table border='1'>");

		sb.append(SMEmailUtilConstants.HTML_TH + "Season" + SMEmailUtilConstants.HTML_SLASH_TH);
		sb.append(SMEmailUtilConstants.HTML_TH + "Image Page Name" + SMEmailUtilConstants.HTML_SLASH_TH);
		sb.append(SMEmailUtilConstants.HTML_TH + "BM Comment" + SMEmailUtilConstants.HTML_SLASH_TH);
		sb.append(SMEmailUtilConstants.HTML_TH + "Technologist Comment" + SMEmailUtilConstants.HTML_SLASH_TH);

		sb.append("<tr>");
		sb.append(SMEmailUtilConstants.HTML_TD + strSeasName + SMEmailUtilConstants.HTML_SLASH_TD);
		sb.append(SMEmailUtilConstants.HTML_TD + "<a href=\"" + strUrl + strOID + "\">" + imgObj.getName() + " </a> "
				+ SMEmailUtilConstants.HTML_SLASH_TD);
		sb.append(SMEmailUtilConstants.HTML_TD + strBMComments + SMEmailUtilConstants.HTML_SLASH_TD);
		sb.append(SMEmailUtilConstants.HTML_TD + strTechComments + SMEmailUtilConstants.HTML_SLASH_TD);

		sb.append("</tr>");

		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");
		logger.debug("===Final Email Content====" + sb.toString());
		statusMail.sendEmail(from, srDesToList, sb.toString(), strSubject, srMailHeader);
		logger.debug("end - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--sendEmailToUser");
	}

	/**
	 * getEmailUser.
	 * 
	 * @param strUserKey
	 * 
	 * @param object     for object.
	 * @return String.
	 * 
	 */
	public static String getEmailUser(LCSDocument imgObj, String strUserKey) throws WTException {
		logger.debug("start - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--getEmailUser");
		String userKey = "";
		try {
			LCSProductSeasonLink spLink = (LCSProductSeasonLink) SMImagePagePlugins
					.getSeasonalProductsForImagePages(imgObj);
			if (spLink != null) {
				FlexObject fo = (FlexObject) spLink.getValue(strUserKey);
				// Check if fo name
				if (fo != null && fo.containsKey(FO_NAME) && FormatHelper.hasContent((String) fo.getData(FO_NAME))) {
					userKey = (String) fo.getData(FO_NAME);
				}
			}
		} catch (WTException e) {
			logger.error(" WTException in getEmailUser method: " + e.getMessage());
			e.printStackTrace();
		}
		logger.debug("end - Inside CLASS--SMSEPDImagePageWFPlugin and METHOD--getEmailUser");
		return userKey;
	}

	/**
	 * Method to construct the flexplm url to provide link for Product Name in email
	 * table
	 * 
	 * @return
	 */
	public static String constructFlexPLMURL() {
		logger.debug("### SMSEPDImagePageWFPlugin.constructFlexPLMURL - START ###");
		String FLEXPLM_URL = "";
		try {
			FLEXPLM_URL = WTProperties.getServerProperties().getProperty("wt.server.codebase")
					+ "/rfa/jsp/main/Main.jsp?newWindowActivity=VIEW_DOCUMENT&newWindowOid=OR%3Acom.lcs.wc.document.LCSDocument%3A";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("IOException in constructFlexPLMURL -" + e.getLocalizedMessage());
			// e.printStackTrace();
		}
		logger.debug("FLEXPLM_URL==" + FLEXPLM_URL);
		logger.debug("### SMSEPDImagePageWFPlugin.constructFlexPLMURL - END ###");
		return FLEXPLM_URL;
	}
}
