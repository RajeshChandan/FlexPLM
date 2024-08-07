package com.sportmaster.wc.document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentLogic;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.UserGroupHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.utils.SMEmailHelper;

import wt.fc.WTObject;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.lifecycle.State;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class SMImagePageWFPlugin {

	static final String TASK_NAME = "taskName";
	static final String TASK_DURATION = "taskDuration";
	static final String TASK_ROLE = "taskRole";

	/**
	 * pageType key
	 */
	public static final String PAGETYPE = LCSProperties.get("com.sportmaster.wc.document.SMImagePageWFPlugin.pageType");
	/**
	 * Designer approval key
	 */
	public static final String DESIGNERAPPROVAL = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.designerAppraval");
	/**
	 * Detail Sketch key
	 */
	public static final String DETAILSKETCH = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.detailSketch");
	/**
	 * Uploded By Designer key
	 */
	public static final String UPLOADEDBYDESIGNER = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.uploadedByDesigner");

	/**
	 * Image Page Status key
	 */
	public static final String IMAGEPAGESTATUS_INWORK = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.smInWork");
	/**
	 * Image Page Status key
	 */
	public static final String IMAGEPAGESTATUS = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imagePageStatus");
	/**
	 * Design Upload Date key
	 */
	public static final String DESIGNUPLOADDATE = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.designUploadDate");
	/**
	 * PM Approval Date key
	 */
	public static final String PMAPPROVALDATE = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.pmApprovalDate");

	public static final String SDAPPROVALDATE = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.sdApprovalDate");
	/**
	 * Designer Approval key
	 */
	public static final String DESIGNERAPPROVALDATE = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.designerApprovalDate");
	/**
	 * Image Page Status Approved key
	 */
	public static final String IMGAPPROVED = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imgApproved");
	/**
	 * Image Page Status For Update key
	 */
	public static final String IMGFORUPDATE = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imgForUpdate");
	/**
	 * Image Page Status Cancelled key
	 */
	public static final String IMGCANCELLED = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imgCancelled");
	/**
	 * Image Page Life Cycle key
	 */
	public static final String IMGLCTEMPLATE = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imgLCTemplate");
	/**
	 * Image Page Name key
	 */
	public static final String IMGPAGENAME = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imgPageName");
	/**
	 * designer key
	 */
	public static final String DESIGNERKEY = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.designerKey");
	/**
	 * designer key
	 */
	public static final String FO_NAME = LCSProperties.get("com.sportmaster.wc.document.SMImagePageWFPlugin.FOName");
	/**
	 * ownerReference key
	 */
	public static final String OWNERREFERENCE = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.ownerReference");
	/**
	 * Image Life Cycle State In Work.
	 */
	public static final String IMAGE_LIFECYCLE_STATE_INWORK = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imageLifeCycleStateInWork");
	/**
	 * Image Life Cycle State PM Approved.
	 */
	public static final String IMAGE_LIFECYCLE_STATE_PMAPPROVED = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imageLifeCycleStatePMApproved");
	/**
	 * Image Life Cycle State PM For Update.
	 */
	public static final String IMAGE_LIFECYCLE_STATE_PMFORUPDATE = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imageLifeCycleStatePMForUpdate");
	/**
	 * Image Life Cycle State Cancelled.
	 */
	public static final String IMAGE_LIFECYCLE_STATE_CANCELLED = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.imageLifeCycleStateCancelled");

	/**
	 * Sketch string
	 */
	public static final String SKETCH = "The sketch \"";
	/**
	 * LOGGER.
	 */
	public static final Logger logger = Logger.getLogger(SMImagePageWFPlugin.class);

	// Phase 13 - Start
	/**
	 * senior designer key
	 */
	public static final String SENIORDESIGNERKEY = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.seniorDesignerKey");

	/**
	 * PM Comments key
	 */
	public static final String PMCOMMENTS = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.pmComments");

	/**
	 * THROWERROR when PM Comments is empty
	 */
	private static boolean bThrowError = false;
	// Phase 13 - End

	/**
	 * Method to set image page status and design uploaded by data on image page
	 * 
	 * @param wtobject
	 * @return
	 * @throws WTException
	 * 
	 */
	public static void setImagePageData(WTObject obj) throws WTException {

		logger.debug("start - Inside CLASS--SMImagePageWFPlugin and METHOD--setImagePageData");

		try {
			// Check if obj instanceof LCSDocument
			if (obj instanceof LCSDocument) {
				LCSDocument imgObj = (LCSDocument) obj;
				logger.debug(" \nIn setImagePageData method: PrePersist isHasContents==" + imgObj.isHasContents());

				String currentTemplateName = imgObj.getLifeCycleName();
				logger.debug("In setImagePageData method: currentTemplateName==" + currentTemplateName);
				// Check if image page name start with Document/image pages and if the object is
				// the working copy
				if ((imgObj.getFlexType().getFullName(true)).startsWith(IMGPAGENAME) && imgObj.getCheckoutInfo() != null
						&& imgObj.getCheckoutInfo().getState().toString().equalsIgnoreCase("wrk")) {
					logger.debug("setImagePageData method: imgObj name=" + imgObj.getName());
					LCSProductSeasonLink lcsSeasonalProd = (LCSProductSeasonLink) SMImagePagePlugins
							.getSeasonalProductsForImagePages(imgObj);

					// Check if Image Page belongs to a Seasonal Product and the lifecycle tempalte
					// is "Sportmaster Image Page Lifecycle"
					if (lcsSeasonalProd != null && currentTemplateName.equals(IMGLCTEMPLATE)) {

						setImagePageDataMethod(imgObj, lcsSeasonalProd);

					}
				}
			}
		} catch (WTException e) {
			// Phase 13 - Start
			logger.error("WTException in setImagePageData method: " + e.getMessage());
			e.printStackTrace();
			if (bThrowError) {
				logger.debug("Throwing error as PM Comments is empty, THROWERROR=" + bThrowError);
				throw new WTException("PM Comment is mandatory if Image Page Status is For Update.");
			}

		} catch (WTPropertyVetoException e) {
			logger.error("WTPropertyVetoException in SMImagePageWFPlugin and METHOD--setImagePageData -->> "
					+ e.getMessage());
			e.printStackTrace();
		}
		// Phase 13 - End
		logger.debug("end - Inside CLASS--SMImagePageWFPlugin and METHOD--setImagePageData");
	}

	/**
	 * @param imgObj
	 * @param lcsSeasonalProd
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void setImagePageDataMethod(LCSDocument imgObj, LCSProductSeasonLink lcsSeasonalProd)
			throws WTException, WTPropertyVetoException {
		String strProductType = lcsSeasonalProd.getFlexType().getFullName();
		if (strProductType.startsWith("FPD")) {
			String imagePgType;
			String designerApproval;
			String imagePageStatus;
			// WF Task 0
			imagePgType = (String) imgObj.getValue(PAGETYPE);
			designerApproval = (String) imgObj.getValue(DESIGNERAPPROVAL);
			imagePageStatus = (String) imgObj.getValue(IMAGEPAGESTATUS);
			logger.debug("imagePgType==" + imagePgType);
			logger.debug("designerApproval==" + designerApproval);
			logger.debug("imagePageStatus==" + imagePageStatus);
			LCSDocumentLogic dLogic = new LCSDocumentLogic();
			logger.debug("In setImagePageData method: PrePersist isAImanaged==" + dLogic.isAImanaged(imgObj));
			// Check if Image Page type is detail sketch and designer approval is uploaded
			// by designer and if not AI managerd (for image page created from FlexPLM UI)or
			// if AI Managed and Has Content is true (in case of Design
			// Suite, when only thumbnail is uploaded the object gets saved, which inturn
			// calls plugins. To avoid this and to execute the below logic only when user
			// publishes from AI, the hasContent additional check is added. When thumbnail
			// is uploaded this is set to false, only during publish this gets set to true)
			if (imagePgType.equalsIgnoreCase(DETAILSKETCH) && FormatHelper.hasContent(designerApproval)
					&& designerApproval.equalsIgnoreCase(UPLOADEDBYDESIGNER)
					&& imagePageStatus.equals(IMAGEPAGESTATUS_INWORK)
					&& !imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_INWORK)
					&& (!dLogic.isAImanaged(imgObj) || (dLogic.isAImanaged(imgObj) && imgObj.isHasContents()))) {

				setStateInWork(imgObj);
			}

			// Image Page Status Value
			else if (imagePgType.equalsIgnoreCase(DETAILSKETCH)) {
				setIPStateBasedOnIPStatus(imgObj);
			}
		}
	}

	/**
	 * @param imgObj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void setIPStateBasedOnIPStatus(LCSDocument imgObj) throws WTException, WTPropertyVetoException {
		String imagePageStatus;
		imagePageStatus = (String) imgObj.getValue(IMAGEPAGESTATUS);
		LifeCycleTemplateReference ref = imgObj.getLifeCycleTemplate();
		LifeCycleState st = new LifeCycleState();
		String setState = "";
		logger.debug("current state--->>>" + imgObj.getLifeCycleState().toString());
		logger.debug("imagePageStatus--->>>" + imagePageStatus);
		// Check if Image Page Status Approved or Cancelled or For Update
		if (imagePageStatus.equalsIgnoreCase(IMGAPPROVED)
				&& !imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_PMAPPROVED)) {
			imgObj.setValue(PMAPPROVALDATE, new java.util.Date());
			setState = IMAGE_LIFECYCLE_STATE_PMAPPROVED;
			logger.debug("Set state to--->>>" + setState);
			State stateChange = State.toState(setState);
			st.setLifeCycleId(ref);
			st.setState(stateChange);
			imgObj.setState(st);
		} else if (imagePageStatus.equalsIgnoreCase(IMGCANCELLED)
				&& !imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_CANCELLED)) {
			imgObj.setValue(PMAPPROVALDATE, new java.util.Date());
			setState = IMAGE_LIFECYCLE_STATE_CANCELLED;
			logger.debug("Set state to--->>>" + setState);
			State stateChange = State.toState(setState);
			st.setLifeCycleId(ref);
			st.setState(stateChange);
			imgObj.setState(st);
		} else if (imagePageStatus.equalsIgnoreCase(IMGFORUPDATE)
				&& !imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_PMFORUPDATE)) {

			// For Phase 13 - FPD IP WF Enh, added key as param to use same method to get
			// senior designer - Start
			String strPMComments = (String) imgObj.getValue(PMCOMMENTS);
			logger.debug("strPMComments=" + strPMComments);
			if (!FormatHelper.hasContent(strPMComments)) {
				bThrowError = true;
				logger.debug("THROWERROR as PM Comments is empty=" + bThrowError);
				throw new WTException("PM Comment is mandatory if Image Page Status is For Update.");

			}
			// For Phase 13 - FPD IP WF Enh, added key as param to use same method to get
			// senior designer - End

			State stateChange = State.toState(IMAGE_LIFECYCLE_STATE_PMFORUPDATE);
			logger.debug("Set state to--->>>>" + IMAGE_LIFECYCLE_STATE_PMFORUPDATE);
			st.setLifeCycleId(ref);
			st.setState(stateChange);
			imgObj.setState(st);
		}
	}

	private static void setStateInWork(LCSDocument imgObj) throws WTException, WTPropertyVetoException {
		// This logic is added to terminate the WF Task when the previous Image Page
		// Lifecycle State is in "SD Approved" and user wants to reinitiate the Image
		// Page WF for In Work task.
		// In this case,the WF task for "SD Approoved" will be present for the users, so
		// need to terminate it through code, before initiating WF Task for In Work
		// State
		LCSDocument predDoc = (LCSDocument) VersionHelper.predecessorOf(imgObj);
		// Check if previous version of Image Page state is not PM Approved
		if (predDoc != null && predDoc.getLifeCycleState().toString().equals("SDAPPROVED")) {
			// Terminate the WF task once LC State is SD Approved
			logger.debug("Pre Persist: Terminate the WF task once LC State is --->>>"
					+ predDoc.getLifeCycleState().toString());
			SMImagePagePlugins.terminateWorkflowTask(predDoc);
		}

		// Set image page status to inwork, and design upload date to current date.
		// imgObj.setValue(IMAGEPAGESTATUS, "smInWork");
		imgObj.setValue(DESIGNUPLOADDATE, new Date());

		// Clearing out the dates value to recapture again for approval process, when
		// user wants to retrigger the WF from the beginning
		imgObj.setValue(PMAPPROVALDATE, "");
		imgObj.setValue(SDAPPROVALDATE, "");

		LifeCycleTemplateReference ref = imgObj.getLifeCycleTemplate();
		LifeCycleState st = new LifeCycleState();
		State inWorkState = State.toState(IMAGE_LIFECYCLE_STATE_INWORK);
		logger.debug("Setting LC to ===" + IMAGE_LIFECYCLE_STATE_INWORK);
		st.setLifeCycleId(ref);
		st.setState(inWorkState);
		imgObj.setState(st);
	}

	/**
	 * getSeasonalProductsForImagePages.
	 * 
	 * @param object for object.
	 * @return String.
	 * 
	 *
	 */
	/*
	 * public static WTObject getSeasonalProductsForImagePages(LCSDocument imgObj) {
	 * logger.
	 * debug("start - Inside CLASS--SMImagePageWFPlugin and METHOD--getSeasonalProductsForImagePages"
	 * ); LCSProductSeasonLink psLink = null; String SM_IMAGEPAGE_SEASON =
	 * LCSProperties.get("com.sportmaster.wc.document.SMImagePageWFPlugin.smSeason",
	 * "smSeason"); try { String partMaster = (String)
	 * imgObj.getValue(OWNERREFERENCE); LCSPartMaster partMasterObj; LCSProduct
	 * product;
	 * 
	 * // Check if partMaster contains value if (FormatHelper.hasContent(partMaster)
	 * && partMaster.contains("com.lcs.wc.part.LCSPartMaster")) { partMasterObj =
	 * (LCSPartMaster) LCSQuery.findObjectById(partMaster); // Check if partMaster
	 * is not null if (partMasterObj != null) { LCSSeason season = (LCSSeason)
	 * imgObj.getValue(SM_IMAGEPAGE_SEASON); if (season != null) { product =
	 * VersionHelper.latestIterationOf(partMasterObj); logger.debug("Product Type=="
	 * + product.getFlexType().getFullName()); // Only for FPD products, the Image
	 * Page WF has to trigger. if
	 * (product.getFlexType().getFullName().startsWith("FPD")) { psLink =
	 * (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(product, season);
	 * } } } } } catch (WTException e) {
	 * logger.error("WTException in getSeasonalProductsForImagePages method: " +
	 * e.getMessage()); e.printStackTrace(); } logger.
	 * debug("end - Inside CLASS--SMImagePageWFPlugin and METHOD--getSeasonalProductsForImagePages"
	 * ); // Check if Product is seasonal or not if (psLink != null) { return
	 * psLink; } else { return null; } }
	 */

	/**
	 * getDesigner.
	 * 
	 * @param strUserKey
	 * 
	 * @param object     for object.
	 * @return String.
	 * 
	 *         For Phase 13 - FPD IP WF Enh, added key as param to use same method
	 *         to get senior designer
	 */
	public static String getDesigner(LCSDocument imgObj, String strUserKey) throws WTException {
		logger.debug("start - Inside CLASS--SMImagePageWFPlugin and METHOD--getDesigner");
		String designer = "";
		try {
			LCSProductSeasonLink spLink = (LCSProductSeasonLink) SMImagePagePlugins
					.getSeasonalProductsForImagePages(imgObj);
			if (spLink != null) {
				FlexObject fo = (FlexObject) spLink.getValue(strUserKey);
				// Check if fo name
				if (fo != null && fo.containsKey(FO_NAME) && FormatHelper.hasContent((String) fo.getData(FO_NAME))) {
					designer = (String) fo.getData(FO_NAME);
				}
			}
		} catch (WTException e) {
			logger.error(" WTException in getDesigner method: " + e.getMessage());
			e.printStackTrace();
		}
		logger.debug("end - Inside CLASS--SMImagePageWFPlugin and METHOD--getDesigner");
		return designer;
	}

	/**
	 * Method to start the Workflow task
	 * 
	 * @param obj
	 */
	/**
	 * @param obj
	 */
	public static void setImagePageStatePostUpdatePersist(WTObject obj) {
		logger.debug("start - Inside CLASS--SMImagePageWFPlugin and METHOD--setImagePageStatePostUpdatePersist");
		try {
			String imagePgType = "";
			String designerApproval = "";
			String imagePageStatus = "";
			LCSDocument predDoc;

			// Check if obj instanceof LCSDocument
			if (obj instanceof LCSDocument) {
				LCSDocument imgObj = (LCSDocument) obj;
				logger.debug(
						"In PostUpdatePersist checkedout state==" + imgObj.getCheckoutInfo().getState().toString());
				logger.debug("In PostUpdatePersist isHasContents==" + imgObj.isHasContents());
				logger.debug("==Image Location==" + imgObj.getLocation());
				LCSProductSeasonLink spLink = (LCSProductSeasonLink) SMImagePagePlugins
						.getSeasonalProductsForImagePages(imgObj);
				String currentTemplateName = imgObj.getLifeCycleName();

				if (spLink != null && currentTemplateName.equals(IMGLCTEMPLATE)) {
					String strProductType = spLink.getFlexType().getFullName();
					FlexObject fo = (FlexObject) spLink.getValue(DESIGNERKEY);
					logger.debug("Image Page Name--->>>" + imgObj.getName());
					logger.debug("isCheckedOut" + VersionHelper.isCheckedOut(imgObj));
					logger.debug("imgObj.getThumbnailLocation()--" + imgObj.getThumbnailLocation());
					// Check if image page name start with Document/image pages
					if (strProductType.startsWith("FPD") && imgObj.isLatestIteration()
							&& (imgObj.getFlexType().getFullName(true)).startsWith(IMGPAGENAME) && fo != null
							&& fo.containsKey(FO_NAME) && FormatHelper.hasContent((String) fo.getData(FO_NAME))
							&& imgObj.getCheckoutInfo().getState().toString().equalsIgnoreCase("wrk")) {
						setImagePageState(imgObj);
					}
				}
			}
		} catch (WTException e) {
			logger.error("WTException in setImagePageStatePostUpdatePersist method: " + e.getMessage());
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			logger.error("WTPropertyVetoException in setImagePageStatePostUpdatePersist method: " + e.getMessage());
			e.printStackTrace();
		}
		logger.debug("end - Inside CLASS--SMImagePageWFPlugin and METHOD--setImagePageStatePostUpdatePersist");
	}

	/**
	 * @param imgObj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void setImagePageState(LCSDocument imgObj) throws WTException, WTPropertyVetoException {
		LCSDocument predDoc;
		String imagePgType;
		imagePgType = (String) imgObj.getValue(PAGETYPE);
		predDoc = (LCSDocument) VersionHelper.predecessorOf(imgObj);
		String designerApproval;
		designerApproval = (String) imgObj.getValue(DESIGNERAPPROVAL);

		// Check if Image Page type is detail sketch and designer approval is uploaded
		// by designer and current image page object is In Work State
		if (imagePgType.equalsIgnoreCase(DETAILSKETCH) && designerApproval.equalsIgnoreCase(UPLOADEDBYDESIGNER)
				&& imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_INWORK)) {
			// Check if previous Image Page object's state not INWORK, so the task is not
			// assgined multiple times
			if (predDoc != null && !predDoc.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_INWORK)) {
				// Call method to Start Task - Moved the below method to SMImagesPagePlugins for
				// generic method
				imgObj = SMImagePagePlugins.startWorkflow(imgObj, "Sportmaster Image Page Workflow");
			}
		}
		// Check if Image Page type is detail sketch
		if (imagePgType.equalsIgnoreCase(DETAILSKETCH)) {
			checkImagePageStatus(imgObj, predDoc, imagePgType, designerApproval);
		}
	}

	/**
	 * @param imgObj
	 * @param predDoc
	 * @param imagePgType
	 * @param designerApproval
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void checkImagePageStatus(LCSDocument imgObj, LCSDocument predDoc, String imagePgType,
			String designerApproval) throws WTException, WTPropertyVetoException {
		String imagePageStatus;

		imagePageStatus = (String) imgObj.getValue(IMAGEPAGESTATUS);

		logger.debug("Post Update Persist: imagePageStatus--->>>" + imagePageStatus);
		logger.debug("Post Update Persist: imagePgType--->>>" + imagePgType);
		logger.debug("Post Update Persist: designerApproval--->>>" + designerApproval);
		logger.debug("Post Update Persist: cyrrent lifecycle state--->>>" + imgObj.getLifeCycleState().toString());
		// Phase 13 - FPD IP WF Enh, added key as param to use same method to get senior
		// designer - Start
		// Get Designer user to send email
		String designer = getDesigner(imgObj, DESIGNERKEY);
		logger.debug("designer--" + designer);
		// Phase 13 - FPD IP WF Enh, added key as param to use same method to get senior
		// designer - End
		SMEmailHelper statusMail = new SMEmailHelper();
		//Vector to = getDeseignerUser(designer);
		ArrayList<WTPrincipal> to = getDeseignerUser(designer);
		
		ClientContext lcsContext = ClientContext.getContext();
		// Get current user to set as email from
		wt.org.WTUser from = UserGroupHelper.getWTUser(lcsContext.getUserName());
		// Check if image page status is approved
		if (imagePageStatus.equalsIgnoreCase(IMGAPPROVED)
				&& imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_PMAPPROVED)) {
			// Check if previous version of Image Page state is not PM Approved
			if (predDoc != null && !predDoc.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_PMAPPROVED)) {
				// Terminate the WF task once LC State is PM Approved
				logger.debug("Post Update Persist: Terminate the WF task once LC State is PM Approved--->>>"
						+ imgObj.getLifeCycleState().toString());
				SMImagePagePlugins.terminateWorkflowTask(imgObj);
			}
		}
		// Check if Image Page type is detail sketch and image page LC is PM For Update
		else if (imagePageStatus.equalsIgnoreCase(IMGFORUPDATE)
				&& imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_PMFORUPDATE)) {
			// Check if previous version of Image Page state is not PM for update
			if (predDoc != null && !predDoc.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_PMFORUPDATE)) {
				String mailHeader = LCSProperties
						.get("com.sportmaster.wc.utils.sendEmail.MailHeader.SMImagePageWFPlugin");
				String body = (SKETCH + imgObj.getName() + "\"  was declined by PM. Please update your sketch.");
				String strSubject = LCSProperties
						.get("com.sportmaster.wc.utils.sendEmail.MailSubject.FPDImagePagePMDecline");
				statusMail.sendEmail(from, to, body, strSubject, mailHeader);

				// Phase 13 - FPD IP WF Enh, send additional notification to Senior Designer -
				// Start
				sendEmailToSeniorDesigner(imgObj, designer, statusMail, from);
				// Phase 13 - FPD IP WF Enh, send additional notification to Senior Designer -
				// End

				// Terminate the WF task once LC State is PM For Update
				logger.debug("Post Update Persist: Terminate the WF task once LC State is PM For Update--->>>"
						+ imgObj.getLifeCycleState().toString());
				SMImagePagePlugins.terminateWorkflowTask(imgObj);
			}
		}
		// Check if Image Page type is detail sketch and image page status is Cancelled
		else if (imagePageStatus.equalsIgnoreCase(IMGCANCELLED)
				&& imgObj.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_CANCELLED)) {

			// Check if previous version of Image Page state is not Cancelled
			if (predDoc != null && !predDoc.getLifeCycleState().toString().equals(IMAGE_LIFECYCLE_STATE_CANCELLED)) {
				String mailHeader = LCSProperties
						.get("com.sportmaster.wc.utils.sendEmail.MailHeader.SMImagePageWFPlugin");
				String body = (SKETCH + imgObj.getName() + "\" was Cancelled by PM.");
				String strSubject = LCSProperties
						.get("com.sportmaster.wc.utils.sendEmail.MailSubject.FPDImagePagePMCancel");
				statusMail.sendEmail(from, to, body, strSubject, mailHeader);
				wt.fc.PersistenceServerHelper.manager.update(imgObj, false);
				// Terminate the WF task once LC State is Cancelled
				logger.debug("Post Update Persist: Terminate the WF task once LC State is Cancelled--->>>"
						+ imgObj.getLifeCycleState().toString());
				SMImagePagePlugins.terminateWorkflowTask(imgObj);
			}
		}
	}

	/**
	 * @param designer
	 * @return
	 * @throws WTException
	 */
	private static ArrayList<WTPrincipal> getDeseignerUser(String designer) throws WTException {
		//Vector to = new java.util.Vector();
		ArrayList<WTPrincipal> to = new ArrayList<>();
		WTUser user = wt.org.OrganizationServicesHelper.manager.getUser(designer);
		if (FormatHelper.hasContent(designer) && user != null) {
			to.add(user);
		}
		return to;
	}

	/**
	 * Phase 13 - FPD IP WF Enh, send additional notification to Senior Designer -
	 * Start If Designer and Senior Designer are not same, then send notification to
	 * senior designer.
	 * 
	 * @param imgObj
	 * @param designer
	 * @param statusMail
	 * @param from
	 * @throws WTException
	 */
	private static void sendEmailToSeniorDesigner(LCSDocument imgObj, String designer, SMEmailHelper statusMail,
			wt.org.WTUser from) throws WTException {
		logger.debug("start - Inside CLASS--SMImagePageWFPlugin and METHOD--sendEmailToSeniorDesigner");

		String seniorDesigner = getDesigner(imgObj, SENIORDESIGNERKEY);
		logger.debug("seniorDesigner--" + seniorDesigner);
		// get senior designer to list
		//Vector srDesto = new java.util.Vector();
		List<WTPrincipal> srDesto = new ArrayList<>();
		// If Designer and Senior Designer are same, then do not send notification to
		// senior designer.
		if (FormatHelper.hasContent(designer) && FormatHelper.hasContent(seniorDesigner)
				&& !designer.equalsIgnoreCase(seniorDesigner)) {
			logger.debug("----Designer and Senior Designer are not same----");
			WTUser srDesUser = wt.org.OrganizationServicesHelper.manager.getUser(seniorDesigner);
			if (FormatHelper.hasContent(seniorDesigner) && srDesUser != null) {
				srDesto.add(srDesUser);
			}
			String srDesMailHeader = LCSProperties
					.get("com.sportmaster.wc.utils.sendEmail.MailHeader.seniorDesigner.SMImagePageWFPlugin");
			String srDesBody = (SKETCH + imgObj.getName() + "\"  was declined by PM.");
			String strSrDesSubject = LCSProperties
					.get("com.sportmaster.wc.utils.sendEmail.MailSubject.FPDImagePagePMDecline");
			statusMail.sendEmail(from, srDesto, srDesBody, strSrDesSubject, srDesMailHeader);
		}
		logger.debug("end - Inside CLASS--SMImagePageWFPlugin and METHOD--sendEmailToSeniorDesigner");
	}
	// Phase 13 - FPD IP WF Enh, send additional notification to Senior Designer -
	// End

}
