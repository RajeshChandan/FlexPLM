package com.sportmaster.wc.sample;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleQuery;
import com.lcs.wc.sample.LCSSampleRequest;
import com.lcs.wc.sample.SampleOwner;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.UserGroupHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.utils.SMEmailHelper;

import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTPropertyVetoException;

/**
 * SMMaterialSampleWorkflowPlugin.
 * 
 * @author 'true'
 * @version 'true' 1.0
 * 
 */
public class SMMaterialSampleWorkflowPlugin {

	/**
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMMaterialSampleWorkflowPlugin.class);

	public static final String SOLE_SAMPLE_STATUS = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sample.smSoleSampleStatus");

	public static final String SOLE_SAMPLE_STATUS_APPROVED = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sample.smSoleSampleStatus.vrdApproved");

	public static final String SOLE_SAMPLE_STATUS_TOBEREVISED = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sample.smSoleSampleStatus.smToBeRevised");

	public static final String SOLE_SAMPLE_STATUS_CANCELLED = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sample.smSoleSampleStatus.smCanceled");

	public static final String SOLE_SAMPLE_REQUESTTYPE = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sampleRequest.vrdRequestType");

	public static final String SOLE_SAMPLE_REQUESTTYPE_VALUES = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sampleRequest.vrdRequestType.values");

	public static final String SOLE_SAMPLE_MATERIAL_TYPE = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sampleRequest.materialType");

	public static final String SOLE_SAMPLE_SAMPLE_TYPE = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sampleRequest.sampleType");

	public static final String SOLE_SAMPLE_DEVELOPMENTSTATUS = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sample.smStatus");

	public static final String SOLE_SAMPLE_DEVSTATUS_RECEIVED = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sample.smStatus.smReceived");

	public static final String SOLE_SAMPLE_DEVSTATUS_READY = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sample.smStatus.smReady");

	public static final String SOLE_SAMPLE_TASKRECEIVED_BY_SUPPLIER = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sample.smSampleTaskReceivedBySupplier");

	public static final String SOLE_SAMPLE_READY_DATE = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sample.smReadyDate");

	public static final String SOLE_SAMPLE_EVALUATED_DATE = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sample.smSampleEvaluatedDate");

	public static final String TASKACCEPTED = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.lcsTaskAccepted");

	public static final String COMPLETE = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.lcsComplete");

	public static final String SAMPLEPREPARED = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.lcsSamplePrepared");

	public static final String SR_REQUESTSTATUS = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sampleRequest.sampleRequestStatus");

	public static final String SR_REQUESTSTATUS_CANCELLED = LCSProperties.get(
			"com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sampleRequest.sampleRequestStatus.smCancelled");

	public static final String SR_REQUESTSTATUS_COMPLETE = LCSProperties.get(
			"com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sampleRequest.sampleRequestStatus.smComplete");

	public static final String SR_QUANTITY = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sampleRequest.quantity");

	public static final String SR_REQUESTCLOSEDATE = LCSProperties
			.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.sampleRequest.smRequestCloseDate");

	public static final String LCS_REQUESTED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.lcsRequested");

	public static final String SAMPLE_STATUS_REQUESTED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.sampleStatus.vrdRequested");

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
	 * setSampleLCS.
	 * 
	 * @param sampleObj for sampleObj.
	 * @param lcsState  for lcsState.
	 * @return void.
	 */
	public static void setSampleLCS(LCSSample sampleObj, String lcStateName) {

		LOGGER.debug("In SMMaterialSampleWorkflowPlugin: setSampleLCS Method - Start");
		// Bypass the security to change state as the users usually don't have access to
		// Change State from UI
		final boolean old_enforced = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			// Setting last param as true to auto complete the WF task
			try {
				LifeCycleHelper.service.setLifeCycleState(sampleObj, State.toState(lcStateName), true);
			} catch (WTInvalidParameterException | WTException e) {
				LOGGER.error("WTInvalidParameterException / WTException in setSampleLCS method: " + e.getMessage());
				e.printStackTrace();
			}
		} finally {
			SessionServerHelper.manager.setAccessEnforced(old_enforced);
		}
		LOGGER.debug("In SMMaterialSampleWorkflowPlugin: setSampleLCS Method - End");
	}

	/**
	 * checkValidSampleType.
	 * 
	 * @param sampleOwner for sampleOwner.
	 * @param sample      for sample.
	 * @return Boolean.
	 */
	public static Boolean checkValidSampleType(SampleOwner sampleOwner, LCSSample sample) {

		LOGGER.debug("In SMMaterialSampleWorkflowPlugin: checkValidSampleType Method - Start");
		try {
			if (sampleOwner instanceof LCSMaterialMaster) {
				// Getting Material object.
				LCSMaterial matObj = (LCSMaterial) VersionHelper.latestIterationOf(sampleOwner);

				String sampleType = sample.getFlexType().getFullNameDisplay(true);
				String materialType = matObj.getFlexType().getFullNameDisplay(true);
				LOGGER.debug("In checkValidSampleType Method - Material Type" + materialType);
				LOGGER.debug("In checkValidSampleType Method - Sample Type" + sampleType);
				LCSSampleRequest sr = sample.getSampleRequest();

				String currentTemplateName = sample.getLifeCycleName();
				String newTemplate = LCSProperties
						.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.LifecycleName");

				if (FormatHelper.hasContent(currentTemplateName) && currentTemplateName.equals(newTemplate)) {
					// Loop to check if sample type & Material Type are valid.
					if (sr != null) {
						String requestType = (String) sr.getValue(SOLE_SAMPLE_REQUESTTYPE);
						Collection cReqTypes = MOAHelper.getMOACollection(SOLE_SAMPLE_REQUESTTYPE_VALUES);
						if (materialType.equals(SOLE_SAMPLE_MATERIAL_TYPE)
								&& sampleType.startsWith(SOLE_SAMPLE_SAMPLE_TYPE) && cReqTypes.contains(requestType)) {
							LOGGER.debug("In checkValidSampleType Method - Valid Sample");
							return true;
						}
					}
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in checkValidSampleType method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.debug("In SMMaterialSampleWorkflowPlugin: checkValidSampleType Method - End");
		return false;
	}

	/**
	 * setSampleAttrBasedOnStatus.
	 * 
	 * @param wtObject for wtObject.
	 * @return void.
	 */
	public static void setSampleAttrBasedOnStatus(WTObject wtObject) {
		LOGGER.debug("In SMMaterialSampleWorkflowPlugin: setSampleAttrBasedOnStatus Method - Start");

		if (wtObject instanceof LCSSample) {
			// Getting Sample object.
			LCSSample sample = (LCSSample) wtObject;
			try {
				// Calling Valid Sample type method.
				boolean bValid = checkValidSampleType((SampleOwner) sample.getOwnerMaster(), sample);
				if (bValid) {

					String sampleStatus = (String) sample.getValue(SOLE_SAMPLE_DEVELOPMENTSTATUS);
					String soleSampleStatus = (String) sample.getValue(SOLE_SAMPLE_STATUS);

					LOGGER.debug("In setSampleAttrBasedOnStatus - Development Status::" + sampleStatus);
					LOGGER.debug("In setSampleAttrBasedOnStatus - Sole Sample Status::" + soleSampleStatus);

					// If Development Status is Received, set Sample Task Received By Supplier to
					// current date
					if (FormatHelper.hasContent(sampleStatus) && sampleStatus.equals(SOLE_SAMPLE_DEVSTATUS_RECEIVED)
							&& sample.getValue(SOLE_SAMPLE_TASKRECEIVED_BY_SUPPLIER) == null) {
						sample.setValue(SOLE_SAMPLE_TASKRECEIVED_BY_SUPPLIER, setCurrentDate());
					}

					// If Development Status is Ready, set Ready Date to current date
					if (FormatHelper.hasContent((sampleStatus)) && sampleStatus.equals(SOLE_SAMPLE_DEVSTATUS_READY)
							&& sample.getValue(SOLE_SAMPLE_READY_DATE) == null) {
						sample.setValue(SOLE_SAMPLE_READY_DATE, setCurrentDate());
					}

					// IF Sole Sample Status value is Approved/ Cancelled/ To Be Revised, set the
					// Sample Evaluated Date to Current Date
					if (FormatHelper.hasContent(soleSampleStatus)
							&& (soleSampleStatus.equals(SOLE_SAMPLE_STATUS_APPROVED)
									|| soleSampleStatus.equals(SOLE_SAMPLE_STATUS_CANCELLED)
									|| soleSampleStatus.equals(SOLE_SAMPLE_STATUS_TOBEREVISED))
							&& sample.getValue(SOLE_SAMPLE_EVALUATED_DATE) == null) {

						sample.setValue(SOLE_SAMPLE_EVALUATED_DATE, setCurrentDate());
					}

					/*
					 * Initially tried with Pre-Persist on Sample Request: when user updated sample
					 * from Sample search results page without editing any of SR attributes, the
					 * Pre-Persist on SR didn't trigger. So, we moved this logic from Pre-Persist on
					 * SR to Per-Persist on Sample. Also, Please note in the below methods we are
					 * setting attributes based on conditions of sample under this SR. Automatically
					 * OOTB saves the SR without any explicit save API.
					 */
					LOGGER.debug(
							"In setSampleAttrBasedOnStatus Method -- calling setSampleReqStatusBasedOnSampleStatus Method- sample Name = "
									+ sample.getName());
					// Method to set Footwear Sole Material Sample - Request Status attribute based
					// on Sample
					// Status.
					setSampleReqStatusBasedOnSampleStatus(sample);
				}
			} catch (WTPropertyVetoException e) {
				LOGGER.error("WTPropertyVetoException in setSampleAttrBasedOnStatus method: " + e.getMessage());
				e.printStackTrace();
			} catch (WTException e) {
				LOGGER.error("WTException in setSampleAttrBasedOnStatus method: " + e.getMessage());
				e.printStackTrace();
			}
		}
		LOGGER.debug("In SMMaterialSampleWorkflowPlugin: setSampleAttrBasedOnStatus Method - End");
	}

	/**
	 * setSampleLCBasedOnStatus.
	 * 
	 * Post Persist on Sample to set LC and to terminate the tasks automatically.
	 * 
	 * @param wtObject for wtObject.
	 * @return void.
	 */
	public static void setSampleLCBasedOnStatus(WTObject wtObject) {
		LOGGER.debug("In SMMaterialSampleWorkflowPlugin: setSampleLCBasedOnStatus Method - Start");
		// Getting Sample object.
		LCSSample sampleObj = (LCSSample) wtObject;
		String sampleStatus;
		try {
			sampleStatus = (String) sampleObj.getValue(SOLE_SAMPLE_DEVELOPMENTSTATUS);
			String soleSampleStatus = (String) sampleObj.getValue(SOLE_SAMPLE_STATUS);

			// Calling Valid Sample type method.
			boolean bValid = checkValidSampleType((SampleOwner) sampleObj.getOwnerMaster(), sampleObj);
			if (bValid) {
				LOGGER.debug("in setSampleLCBasedOnStatus method: sampleStatus = " + sampleStatus);
				LOGGER.debug("In setSampleLCBasedOnStatus method: soleSampleStatus = " + soleSampleStatus);

				String currentCWLCS = sampleObj.getState().getState().toString();
				LOGGER.debug("In setSampleLCBasedOnStatus method: currentCWLCS = " + currentCWLCS);

				// Enters this loop when Sole Sample status value is Requested.
				if (!FormatHelper.hasContent(sampleStatus) && !currentCWLCS.equals(LCS_REQUESTED)
						&& FormatHelper.hasContent(soleSampleStatus)
						&& soleSampleStatus.equalsIgnoreCase(SAMPLE_STATUS_REQUESTED)) {
					// Setting Material Sample Lifecycle state as Requested.
					setSampleLCS(sampleObj, LCS_REQUESTED);
				}
				// If Development Status attribute is set to "Received" then set material sample
				// LC State to "Task Accepted".
				if (!currentCWLCS.equals(TASKACCEPTED) && FormatHelper.hasContent(sampleStatus)
						&& sampleStatus.equalsIgnoreCase(SOLE_SAMPLE_DEVSTATUS_RECEIVED)) {
					LOGGER.debug("In setSampleAttrBasedOnStatus method:Setting state to = " + TASKACCEPTED);
					// Setting Material Sample Lifecycle state as Task Accepted.
					setSampleLCS(sampleObj, TASKACCEPTED);
				}
				// If current state is not Complete/ Sample Prepared, or Development Status
				// attribute is set to "Ready" then set State to Sample Prepared.
				if (!currentCWLCS.equals(COMPLETE) && !currentCWLCS.equals(SAMPLEPREPARED)
						&& FormatHelper.hasContent(sampleStatus)
						&& sampleStatus.equalsIgnoreCase(SOLE_SAMPLE_DEVSTATUS_READY)) {
					LOGGER.debug("In setSampleAttrBasedOnStatus method:Setting state to = " + SAMPLEPREPARED);
					// Setting Material Sample Lifecycle state as Sample Prepared.
					setSampleLCS(sampleObj, SAMPLEPREPARED);
				}
				// If Sole Sample Status attribute is set to "Approved" or "Cancelled" then set
				// material sample LC State to "Complete".
				if (!currentCWLCS.equals(COMPLETE) && FormatHelper.hasContent(soleSampleStatus)
						&& (soleSampleStatus.equals(SOLE_SAMPLE_STATUS_APPROVED)
								|| soleSampleStatus.equals(SOLE_SAMPLE_STATUS_CANCELLED)
								|| soleSampleStatus.equals(SOLE_SAMPLE_STATUS_TOBEREVISED))) {
					LOGGER.debug("In setSampleAttrBasedOnStatus method:Setting state to = " + COMPLETE);
					setSampleLCS(sampleObj, COMPLETE);
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in setSampleLCBasedOnStatus method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.debug("In SMMaterialSampleWorkflowPlugin: setSampleLCBasedOnStatus Method - End");

	}

	/**
	 * setSampleStatesBasedOnSampleReqStatus.
	 * 
	 * Post Persist on SR to set the LC for Samples to Complete in case user
	 * manually changes the Request Status attribute on SR
	 *
	 * @param wtObject for wtObject.
	 * @return void.
	 */
	public static void setSampleStatesBasedOnSampleReqStatus(WTObject wtObject) {
		LOGGER.debug("In SMMaterialSampleWorkflowPlugin: setSampleStatesBasedOnSampleReqStatus Method - Start");

		if (wtObject instanceof LCSSampleRequest) {
			// Getting Sample request object.
			LCSSampleRequest sampleRequest = (LCSSampleRequest) wtObject;

			Collection<FlexObject> cSamples = new ArrayList<FlexObject>();
			LOGGER.debug("in setSampleStatesBasedOnSampleReqStatus method: sampleRequest = " + sampleRequest);
			LCSSampleQuery sq = new LCSSampleQuery();

			String currentTemplateName = sampleRequest.getLifeCycleName();
			String newTemplate = LCSProperties
					.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.LifecycleName");

			// SR Customizaiton:
			try {
				String strSampleRequestStatus = (String) sampleRequest.getValue(SR_REQUESTSTATUS);
				LOGGER.debug(
						"In setSampleStatesBasedOnSampleReqStatus Method - Sample Request= " + sampleRequest.getName());
				LOGGER.debug("In setSampleStatesBasedOnSampleReqStatus Method - strSampleRequestStatus= "
						+ strSampleRequestStatus);

				if (FormatHelper.hasContent(currentTemplateName) && currentTemplateName.equals(newTemplate)) {
					// When request status is cancel or complete set all the associated samples LC
					// to Complete
					if (FormatHelper.hasContent(strSampleRequestStatus)
							&& strSampleRequestStatus.equals(SR_REQUESTSTATUS_CANCELLED)
							|| strSampleRequestStatus.equals(SR_REQUESTSTATUS_COMPLETE)) {
						// Get all associated Samples, set the value for LC State to complete
						cSamples = sq.findSamplesIdForSampleRequest(sampleRequest, false);
						if (cSamples != null && !cSamples.isEmpty()) {
							LOGGER.debug("In setSampleStatesBasedOnSampleReqStatus Method - cSamples = " + cSamples);
							Iterator itSamples = cSamples.iterator();
							String strSampleID = "";
							FlexObject fo;
							LCSSample lcsSample;
							while (itSamples.hasNext()) {
								fo = (FlexObject) itSamples.next();
								strSampleID = (String) fo.get("LCSSAMPLE.IDA2A2");
								lcsSample = (LCSSample) LCSQuery
										.findObjectById("com.lcs.wc.sample.LCSSample:" + strSampleID);
								// Calling Valid Sample type method.
								boolean bValid = checkValidSampleType((SampleOwner) lcsSample.getOwnerMaster(),
										lcsSample);
								if (bValid) {
									String currentCWLCS = lcsSample.getState().getState().toString();
									if (!currentCWLCS.equals(COMPLETE)) {
										// Terminate All Tasks for all Samples
										// Set State to Complete
										setSampleLCS(lcsSample, COMPLETE);
									}
								}
							}
						}

						// Send email for cancelled status:
						if (strSampleRequestStatus.equals(SR_REQUESTSTATUS_CANCELLED)) {
							// Send automatic email notification to Mold Developer user group about
							// cancelled request;
							SMEmailHelper statusMail = new SMEmailHelper();
							Vector to = new Vector();
							@SuppressWarnings("deprecation")
							WTGroup group = (WTGroup) OrganizationServicesHelper.manager.getPrincipal(LCSProperties.get(
									"com.sportmaster.wc.utils.sendEmail.MailToGroup.SMMaterialSampleWorkflowPlugin"));
							if (group != null) {
								to.addElement(group);
							}

							ClientContext lcsContext = ClientContext.getContext();
							wt.org.WTUser from = UserGroupHelper.getWTUser(lcsContext.getUserName());
							LCSMaterial matObj = (LCSMaterial) VersionHelper
									.latestIterationOf(sampleRequest.getOwnerMaster());
							String mailHeader = LCSProperties.get("com.sportmaster.wc.utils.sendEmail.MailHeader");
							String body = ("Please, pay attention! \"" + sampleRequest.getName()
									+ "\" sample development request is cancelled for \"" + matObj.getName() + "\"");
							String strSubject = LCSProperties.get(
									"com.sportmaster.wc.utils.sendEmail.MailSubject.SMMaterialSampleWorkflowPlugin");
							statusMail.sendEmail(from, to, body, strSubject, mailHeader);
						}
					}
				}
			} catch (WTException e) {
				LOGGER.error("WTException in setSampleStatesBasedOnSampleReqStatus method: " + e.getMessage());
				e.printStackTrace();
			}
		}
		LOGGER.debug("In SMMaterialSampleWorkflowPlugin: setSampleStatesBasedOnSampleReqStatus Method - End");
	}

	/**
	 * setSampleReqStatusBasedOnSampleStatus.
	 *
	 * @param wtObject for wtObject.
	 * @return void.
	 */
	public static void setSampleReqStatusBasedOnSampleStatus(LCSSample sample) {

		LOGGER.debug("In SMMaterialSampleWorkflowPlugin: setSampleReqStatusBasedOnSampleStatus Method - Start");
		if (sample.getSampleRequest() instanceof LCSSampleRequest) {
			// Getting Sample Request object.
			LCSSampleRequest sampleRequest = sample.getSampleRequest();
			Collection<FlexObject> cSamples = new ArrayList<FlexObject>();

			String currentTemplateName = sampleRequest.getLifeCycleName();
			String newTemplate = LCSProperties
					.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.LifecycleName");

			try {
				LCSSampleQuery sq = new LCSSampleQuery();
				if (FormatHelper.hasContent(currentTemplateName) && currentTemplateName.equals(newTemplate)) {
					if (sampleRequest != null
							&& sampleRequest.toString().contains("com.lcs.wc.sample.LCSSampleRequest:")) {
						LOGGER.debug("In setSampleReqStatusBasedOnSampleStatus Method - sampleRequest = "
								+ sampleRequest.getName());
						// Get the current Quantity value
						int i = (int) ((long) sampleRequest.getValue(SR_QUANTITY));
						LOGGER.debug("In setSampleReqStatusBasedOnSampleStatus Method - Quantity value = " + i);
						cSamples = sq.findSamplesIdForSampleRequest(sampleRequest, false);
						if (cSamples != null && !cSamples.isEmpty()) {
							LOGGER.debug("In setSampleReqStatusBasedOnSampleStatus Method - cSamples = " + cSamples);

							Iterator itSamples = cSamples.iterator();
							String strSampleID = "";
							FlexObject fo;
							LCSSample lcsSample;
							boolean bPresent = false;
							String strSoleSampleStatus;
							// Iterate each sample
							while (itSamples.hasNext()) {
								bPresent = false;
								strSoleSampleStatus = "";
								fo = (FlexObject) itSamples.next();
								strSampleID = (String) fo.get("LCSSAMPLE.IDA2A2");
								lcsSample = (LCSSample) LCSQuery
										.findObjectById("com.lcs.wc.sample.LCSSample:" + strSampleID);

								// Calling Valid Sample type method.
								boolean bValid = checkValidSampleType((SampleOwner) lcsSample.getOwnerMaster(),
										lcsSample);
								if (bValid) {

									/*
									 * If the currently updated Sample (sample) is same as current Sample
									 * (lcsSample) from DB, get the sample status value from currently updated
									 * sample as this will hold the latest Sample Status values selected by user in
									 * UI.
									 */
									if (!sample.toString().equals(lcsSample.toString())) {
										lcsSample = (LCSSample) PersistenceHelper.manager.refresh(lcsSample);
										strSoleSampleStatus = (String) lcsSample.getValue(SOLE_SAMPLE_STATUS);
									} else {
										strSoleSampleStatus = (String) sample.getValue(SOLE_SAMPLE_STATUS);
									}

									LOGGER.debug("In setSampleReqStatusBasedOnSampleStatus Method - lcsSample = "
											+ lcsSample.getName());
									LOGGER.debug(
											"In setSampleReqStatusBasedOnSampleStatus Method - strSoleSampleStatus = "
													+ strSoleSampleStatus);
									// If Sole Sample Status is Approved/ Cancelled, set the boolean to true
									if (FormatHelper.hasContent(strSoleSampleStatus)
											&& (strSoleSampleStatus.equals(SOLE_SAMPLE_STATUS_APPROVED)
													|| strSoleSampleStatus.equals(SOLE_SAMPLE_STATUS_CANCELLED))) {
										bPresent = true;
									}
									// If at least one sample is not in Approved or Cancelled status, set boolean as
									// False and break the loop
									if (!bPresent) {
										break;
									}
								}
							}

							LOGGER.debug("In setSampleReqStatusBasedOnSampleStatus Method - bPresent = " + bPresent);
							// This check i == cSamples.size() is added as when the quantity is increased
							// and request status is changed as "Open" by the user,
							// the new (incremented) sample object will not be persisted until SR is saved.
							// So, once Sample Request is persisted, it creates the new iterated sample, and
							// that sample will be in Requested state
							// In this case, the Sample Request's Request Status attribute has to be set as
							// Open only.
							if (bPresent && i == cSamples.size()) {
								LOGGER.debug("In setSampleReqStatusBasedOnSampleStatus Method - cSamples.size() = "
										+ cSamples.size());
								// When all samples are approved or cancelled, set the Request Status to
								// Complete, Request Close date to current date
								sampleRequest.setValue(SR_REQUESTSTATUS, SR_REQUESTSTATUS_COMPLETE);
								if (sampleRequest.getValue(SR_REQUESTCLOSEDATE) == null) {
									sampleRequest.setValue(SR_REQUESTCLOSEDATE, setCurrentDate());
								}
							}
						}
					}
				}
			} catch (WTException e) {
				LOGGER.error("WTException in setSampleReqStatusBasedOnSampleStatus Method -" + e.getMessage());
				e.printStackTrace();
			} catch (WTPropertyVetoException e) {
				LOGGER.error(
						"WTPropertyVetoException in setSampleReqStatusBasedOnSampleStatus Method -" + e.getMessage());
				e.printStackTrace();
			}
		}
		LOGGER.debug("In SMMaterialSampleWorkflowPlugin: setSampleReqStatusBasedOnSampleStatus Method - End");
	}

	/**
	 * setSampleReqAttBasedOnReqStatus.
	 * 
	 * PrePersist SR Object.
	 *
	 * @param wtObject for wtObject.
	 * @return void.
	 */
	public static void setSampleReqAttBasedOnReqStatus(WTObject wtObject) {

		LOGGER.debug("In SMMaterialSampleWorkflowPlugin: setSampleReqAttBasedOnReqStatus Method - Start");
		if (wtObject instanceof LCSSampleRequest) {
			// Getting Sample Request object.
			LCSSampleRequest sampleRequest = (LCSSampleRequest) wtObject;
			
			String currentTemplateName = sampleRequest.getLifeCycleName();
			String newTemplate = LCSProperties
					.get("com.sportmaster.wc.sample.SMMaterialSampleWorkflowPlugin.LifecycleName");

			LOGGER.debug("In setSampleReqAttBasedOnReqStatus Method - sampleRequest= " + sampleRequest.getName());
			LOGGER.debug("In setSampleReqAttBasedOnReqStatus Method - currentTemplateName= " + currentTemplateName);
			LOGGER.debug("In setSampleReqAttBasedOnReqStatus Method - newTemplate= " + newTemplate);

			try {
				LCSSampleQuery sq = new LCSSampleQuery();
				if (FormatHelper.hasContent(currentTemplateName) && currentTemplateName.equals(newTemplate)) {
					if (sampleRequest != null) {

						String strSampleRequestStatus = (String) sampleRequest.getValue(SR_REQUESTSTATUS);
						LOGGER.debug("In setSampleReqAttBasedOnReqStatus Method - strSampleRequestStatus= "
								+ strSampleRequestStatus);

						// SR Customizaiton:user manually changes request status as cancelled/ complete
						if (FormatHelper.hasContent(strSampleRequestStatus)
								&& (strSampleRequestStatus.equals(SR_REQUESTSTATUS_CANCELLED)
										|| strSampleRequestStatus.equals(SR_REQUESTSTATUS_COMPLETE))) {
							if (sampleRequest.getValue(SR_REQUESTCLOSEDATE) == null) {
								sampleRequest.setValue(SR_REQUESTCLOSEDATE, setCurrentDate());
							}
						}
					}
				}
			} catch (WTException e) {
				LOGGER.error("WTException in setSampleReqAttBasedOnReqStatus Method -" + e.getMessage());
				e.printStackTrace();
			} catch (WTPropertyVetoException e) {
				LOGGER.error("WTPropertyVetoException in setSampleReqAttBasedOnReqStatus Method -" + e.getMessage());
				e.printStackTrace();
			}
		}
		LOGGER.debug("In SMMaterialSampleWorkflowPlugin: setSampleReqAttBasedOnReqStatus Method - End");
	}

}
