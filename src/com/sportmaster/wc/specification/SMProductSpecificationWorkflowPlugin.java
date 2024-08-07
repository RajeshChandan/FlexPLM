package com.sportmaster.wc.specification;

import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecToSeasonLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FlexContainerHelper;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.UserGroupHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.utils.SMEmailHelper;
import com.sportmaster.wc.utils.SmWorkflowHelper;

import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.inf.container.ExchangeContainer;
import wt.inf.container.WTContainerHelper;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.lifecycle.State;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTOrganization;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.definer.WfProcessTemplate;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.StandardWfEngineService;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfProcess;
import wt.workflow.engine.WfState;
import wt.workflow.engine.WfTransition;

public class SMProductSpecificationWorkflowPlugin {

	/**
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMProductSpecificationWorkflowPlugin.class);

	/**
	 * 
	 * Flex Object Name.
	 * 
	 */
	public static final String FO_NAME = LCSProperties.get("com.sportmaster.wc.product.SmWorkflowHelper.fOName",
			"NAME");

	/**
	 * 
	 * Sourcing Config - Business Supplier.
	 * 
	 */
	public static final String BUSINESS_SUPPLIER = LCSProperties
			.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.businessSupplier", "vendor");

	/**
	 * 
	 * Supplier Vendor Group.
	 * 
	 */
	public static final String SUPPLIER_VENDOR_GROUP = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.supplierVendorGroup",
			"vrdVendorGroup");

	/**
	 * 
	 * OSO_DEVELOPER_APPAREL
	 * 
	 */
	public static final String OSO_DEVELOPER_APPAREL = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.smOSOAppDeveloper",
			"smOSOAppDeveloper");
	/**
	 * 
	 * OSO_DEVELOPER_SEPD
	 * 
	 */
	public static final String OSO_DEVELOPER_SEPD = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.smOSODeveloper", "smOSODeveloper");
	/**
	 * Sourcing Config Type - Apparel.
	 */
	public static final String APPAREL_SOURCE_TYPE = LCSProperties
			.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.apparelSourceType", "Apparel");
	/**
	 * Sourcing Config - SEPD.
	 */
	public static final String SEPD_SOURCE_TYPE = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.sepdSourceType",
			"smSportsEquipement");
	/**
	 * 
	 * NEEDSMREVISION
	 * 
	 */
	public static final String NEED_SM_REVISION = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.needSMRevision", "smNeedSMRevision");
	/**
	 * 
	 * REVISED_BY_SM
	 * 
	 */
	public static final String REVISED_BY_SM = LCSProperties
			.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.revisedBySM", "smRevisedBySM");

	/**
	 * 
	 * SPEC_CONFIRMED_BY_SUPPLIER
	 * 
	 */
	public static final String SPEC_CONFIRMED_BY_SUPPLIER = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.specConfirmedBySupplier",
			"smSpecConfirmedBySupplier");
	/**
	 * 
	 * ENGINEERING_SAMPLE
	 * 
	 */
	public static final String ENGINEERING_SAMPLE = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.engineeringSample",
			"smEngineeringSample");
	/**
	 * 
	 * APPROVED
	 * 
	 */
	public static final String APPROVED = LCSProperties
			.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.approved", "smApproved");
	/**
	 * 
	 * PRODUCTION_MANAGER
	 * 
	 */
	public static final String PRODUCTION_MANAGER = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.productionManager",
			"smProductionManager");

	/**
	 * 
	 * PRODUCCT_TECHNOLOGIST
	 * 
	 */
	public static final String PRODUCCT_TECHNOLOGIST = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.producctTechnologist",
			"smProducctTechnologist");
	/**
	 * 
	 * NEED_REVISION
	 * 
	 */
	public static final String NEED_REVISION = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.NEEDREVISION", "SMNEEDREVISION");
	/**
	 * 
	 * 
	 * REVISED
	 */
	public static final String REVISED = LCSProperties
			.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.REVISED", "SMREVISED");
	/**
	 * 
	 * 
	 * DEFAULT
	 */
	public static final String DEFAULT = LCSProperties
			.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.DEFAULT", "SMDEFAULT");
	/**
	 * 
	 * 
	 * REISSUE
	 */
	public static final String REISSUE = LCSProperties
			.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.REISSUE", "SMREISSUE");
	/**
	 * 
	 * 
	 * COMPLETE
	 */
	public static final String COMPLETE = LCSProperties
			.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.COMPLETE", "COMPLETE");
	/**
	 * 
	 * NEED_FORREVISION_WORKFLOW
	 * 
	 */
	public static final String NEED_FOR_REVISION_WORKFLOW = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.needForRevisionWorkflow",
			"Sportmaster SEPD Product Specification - Need For Revision Workflow");
	/**
	 * 
	 * REVISED_WORKFLOW
	 * 
	 */
	public static final String REVISED_WORKFLOW = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.revisedWorkflow",
			"Sportmaster SEPD Product Specification - Revised Workflow");
	/**
	 * 
	 * REISSUE_WORKFLOW
	 * 
	 */
	public static final String REISSUE_WORKFLOW = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.reissueWorkflow",
			"Sportmaster SEPD Product Specification - Reissue Workflow");
	/**
	 * 
	 * SPORTMASTER_SPECIFICATION_LIFECYCLE
	 * 
	 */
	public static final String SPORTMASTER_SPECIFICATION_LIFECYCLE = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.sportmasterSpecificationLifecycle",
			"Sportmaster Specification Lifecycle");
	/**
	 * 
	 * SPEC_CONFIRMATION_BEFORE_SAMPLE_PRODUCTION
	 * 
	 */
	public static final String SPEC_CONFIRMATION_BEFORE_SAMPLE_PRODUCTION = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.specConfirmationBeforeSampleProduction",
			"smSpecConfirmationBeforeSampleProduction");

	/**
	 * 
	 * SEPD_SPEC_STAGE
	 * 
	 */
	public static final String SEPD_SPEC_STAGE = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.smSEPDSpecStage", "smSEPDSpecStage");

	/**
	 * 
	 * APP_SPEC_STAGE
	 * 
	 */
	public static final String APP_SPEC_STAGE = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.smAPDSpecStage", "smAPDSpecStage");

	/**
	 * 
	 * SPEC_STATUS
	 * 
	 */
	public static final String SPEC_STATUS = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.vrdSpecStatus", "vrdSpecStatus");
	/**
	 * 
	 * SEPD
	 * 
	 */
	public static final String SEPD_SPEC = LCSProperties
			.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.SEPDSpecType", "SEPD");

	/**
	 * 
	 * SEPD
	 * 
	 */
	public static final String APPAREL_SPEC = LCSProperties
			.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.ApparelSpecType", "Apparel");

	/**
	 * 
	 * SPECIFICATION_FINAL_APPROVAL_ON_PPS_STAGE
	 * 
	 */
	public static final String SPECIFICATION_FINAL_APPROVAL_ON_PPS_STAGE = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.smSpecificationFinalApprovalOnPPSStage",
			"smSpecificationFinalApprovalOnPPSStage");

	/**
	 * 
	 * FINAL_SPECIFICATION_REISSUED
	 * 
	 */
	public static final String FINAL_SPECIFICATION_REISSUED = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.smFinalSpecificationReissued",
			"smFinalSpecificationReissued");

	public static final String ACC_SEPD_PDT_TYPE = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.AccSEPDPdtType",
			"Accessories\\SEPD");

	public static final String SEPD_PDT_TYPE = LCSProperties
			.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.SEPDPdtType", "SEPD");

	public static final String SEASONREFERENCE = LCSProperties.get(
			"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.smSeasonReference",
			"smSeasonReference");
	
	public static final String ENGGSPEC_CREATED_TASK = "Engineering Specification created";

	/*
	 * checkValidSpecType.
	 * 
	 * @param spec for spec.
	 * 
	 * @return Boolean.
	 */
	public static Boolean checkValidSpecType(FlexSpecification specObj) {
		LOGGER.debug("In SMProductSpecificationWorkflowPlugin: checkValidSpecType Method - Start");
		try {
			LCSSeason seasonObj = (LCSSeason) specObj.getValue(SEASONREFERENCE);
			// LCSProduct prodObj = (com.lcs.wc.product.LCSProduct) VersionHelper
			// .latestIterationOf(specObj.getSpecOwner());
			if (seasonObj != null) {
				LCSProductSeasonLink psLink = getProdSeasLink(seasonObj, specObj);
				FlexSpecToSeasonLink SpecSeasLink = FlexSpecQuery.findSpecToSeasonLink(specObj.getMaster(),
						seasonObj.getMaster());
				LOGGER.debug("SpecSeasLink=== " + SpecSeasLink);
				if (psLink != null && !psLink.isSeasonRemoved() && SpecSeasLink != null) {
					LCSProduct prodRev = SeasonProductLocator.getProductSeasonRev(psLink);
					// checking if its SEPD or ACC SEPD Product
					if ((specObj.getFlexType().getFullNameDisplay().startsWith(SEPD_SPEC)
							&& prodRev.getFlexType().getFullNameDisplay().startsWith(SEPD_PDT_TYPE))
							|| (specObj.getFlexType().getFullNameDisplay().startsWith(APPAREL_SPEC)
									&& prodRev.getFlexType().getFullNameDisplay().startsWith(ACC_SEPD_PDT_TYPE))) {
						return true;
					}
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in checkValidSpecType method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.debug("In SMProductSpecificationWorkflowPlugin: checkValidSpecType Method - End");
		return false;
	}

	/**
	 * @param seasonObj
	 * @param productRevA
	 * @param psLink
	 * @return
	 * @throws WTException
	 */
	private static LCSProductSeasonLink getProdSeasLink(LCSSeason seasonObj, FlexSpecification specObj)
			throws WTException {
		LCSProductSeasonLink psLink = null;

		LCSProduct prodObj = (com.lcs.wc.product.LCSProduct) VersionHelper.latestIterationOf(specObj.getSpecOwner());
		LCSProduct productRevA = (LCSProduct) VersionHelper.getVersion(prodObj, "A");
		LOGGER.debug("productRevA== " + productRevA);
		LOGGER.debug("seasonObj==== " + seasonObj);
		psLink = (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(productRevA, seasonObj);

		return psLink;
	}

	private static String getDeveloperOSOUser(LCSSourcingConfig sourceObj, LCSSourceToSeasonLink sourceToSeasonLink) {
		LOGGER.debug("Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--getDeveloperOSOUser - Start");
		String developerOSO = "";
		if (sourceObj.getFlexType().getFullName().startsWith(APPAREL_SOURCE_TYPE)) {
			// Getting OSO Developer value.
			developerOSO = teamAssignmentOSODeveloperAttributeForSpecWF(OSO_DEVELOPER_APPAREL, sourceToSeasonLink);
			LOGGER.debug("In getDeveloperOSOUser Method - OSO DEVELOPER Apparel " + developerOSO);
		} else if (sourceObj.getFlexType().getFullName().startsWith(SEPD_SOURCE_TYPE)) {
			// Getting OSO Developer value.
			developerOSO = teamAssignmentOSODeveloperAttributeForSpecWF(OSO_DEVELOPER_SEPD, sourceToSeasonLink);
			LOGGER.debug("In getDeveloperOSOUser Method - OSO DEVELOPER SEPD " + developerOSO);
		}
		LOGGER.debug("Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--getDeveloperOSOUser - End");
		return developerOSO;
	}

	public static String teamAssignmentOSODeveloperAttributeForSpecWF(String attName,
			LCSSourceToSeasonLink sourceToSeasonLink) {
		LOGGER.debug(
				"Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--teamAssignmentOSODeveloperAttributeForSpecWF - Start");
		String result = null;
		try {
			if (sourceToSeasonLink != null) {
				FlexObject fo = (FlexObject) sourceToSeasonLink.getValue(attName);
				if (fo != null && fo.containsKey(FO_NAME) && FormatHelper.hasContent((String) fo.getData(FO_NAME))) {
					LOGGER.debug("Attribute Value==" + fo.getData(FO_NAME));
					LOGGER.debug("In teamAssignmentOSODeveloperAttributeForSpecWF method - OSO DEVELOPER "
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
		LOGGER.debug(
				"Inside CLASS--SMProductSampleWorkflowPlugin and METHOD--teamAssignmentOSODeveloperAttributeForSpecWF - End");

		return result;
	}

	/*
	 * setSpecificationAttributes.
	 * 
	 * @param wtObject for wtObject. PRE PERSIST SSP
	 * 
	 * @return void.
	 */
	public static void setSpecificationAttributes(WTObject wtObject) throws WTException, WTPropertyVetoException {
		LOGGER.debug("In SMProductSpecificationWorkflowPlugin: setSpecificationAttributes Method - Start");
		if (wtObject instanceof FlexSpecification) {

			// Getting Specification object.
			FlexSpecification specObj = (FlexSpecification) wtObject;
			LOGGER.debug("specObj= " + specObj);
			try {
				// if (specObj != null) {
				if (!specObj.toString().contains(":")) {
					LOGGER.debug("sendMailForEngSampleOnSpecCreate start- " + specObj);
					sendMailForEngSampleOnSpecCreate(specObj);
					return;
				}
				// Calling Valid Specification type method.
				boolean bValid = checkValidSpecType(specObj);

				String currentTemplateName = specObj.getLifeCycleName();
				setSpecAttrsAndState(specObj, bValid, currentTemplateName);

				// }
			} catch (WTException e) {
				LOGGER.error("WTException in setSpecificationAttributes method: " + e.getMessage());
				e.printStackTrace();
			}
		}
		LOGGER.debug("In SMProductSpecificationWorkflowPlugin: setSpecificationAttributes Method - End");
	}

	
	
	/**
	 * Method to send email for engineering sample on create spec
	 * @param specObj
	 */
	private static void sendMailForEngSampleOnSpecCreate(FlexSpecification specObj) {
		boolean bValid = false;
		LCSProduct prodRev = null;
			LOGGER.debug("In SMProductSpecificationWorkflowPlugin: sendMailForEngSampleOnSpecCreate Method - Start");
			try {
				LCSSeason seasonObj = (LCSSeason) specObj.getValue(SEASONREFERENCE);
				// LCSProduct prodObj = (com.lcs.wc.product.LCSProduct) VersionHelper
				// .latestIterationOf(specObj.getSpecOwner());
				if (seasonObj != null) {
					LCSProductSeasonLink psLink = getProdSeasLink(seasonObj, specObj);					
					if (psLink != null && !psLink.isSeasonRemoved()) {
						prodRev = SeasonProductLocator.getProductSeasonRev(psLink);
						// checking if its SEPD or ACC SEPD Product
						if ((specObj.getFlexType().getFullNameDisplay().startsWith(SEPD_SPEC)
								&& prodRev.getFlexType().getFullNameDisplay().startsWith(SEPD_PDT_TYPE))
								|| (specObj.getFlexType().getFullNameDisplay().startsWith(APPAREL_SPEC)
										&& prodRev.getFlexType().getFullNameDisplay().startsWith(ACC_SEPD_PDT_TYPE))) {
							bValid = true;
						}
					}
				}
				if(bValid) {
					LOGGER.debug("bValid on spec create== " + bValid);
					sendMailForValidEngSampleOnSpecCreate(specObj, prodRev);			
				}
			} catch (WTException e) {
				LOGGER.error("WTException in sendMailForEngSampleOnSpecCreate method: " + e.getMessage());
				e.printStackTrace();
			}
			LOGGER.debug("In SMProductSpecificationWorkflowPlugin: sendMailForEngSampleOnSpecCreate Method - End");
		
	}

	/**
	 * @param specObj
	 * @param prodRev
	 * @throws WTException
	 */
	private static void sendMailForValidEngSampleOnSpecCreate(FlexSpecification specObj,
			LCSProduct prodRev) throws WTException {
		LOGGER.debug("In SMProductSpecificationWorkflowPlugin: sendMailForValidEngSampleOnSpecCreate Method - Start");
		String specStage = getSpecStageValue(specObj);
		LOGGER.debug("Spec Stage== " + specStage);
		
		String specStatus = (String) specObj.getValue(SPEC_STATUS);
		LOGGER.debug("Spec Status== " + specStatus);
		// For Template 2 Eng TR
		if (specStage.equals(ENGINEERING_SAMPLE) && specStatus.equals(APPROVED)) {
			String strProductionManager = SmWorkflowHelper.teamAssignmentAttribute(PRODUCTION_MANAGER, prodRev);
			ArrayList<WTPrincipal> toUser = new ArrayList<>();
			@SuppressWarnings("deprecation")
			WTUser wtUser2 = wt.org.OrganizationServicesHelper.manager.getUser(strProductionManager);
			if (wtUser2 != null) {
				toUser.add(wtUser2);
			}
			// Send Email to Production Manager
			LOGGER.debug("Send Email to Production Manager: " + strProductionManager);
			sendEmailToUser(specObj, toUser, ENGGSPEC_CREATED_TASK, "3");
		}
		LOGGER.debug("In SMProductSpecificationWorkflowPlugin: sendMailForValidEngSampleOnSpecCreate Method - End");
	}

	/**
	 * @param specObj
	 * @param bValid
	 * @param currentTemplateName
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void setSpecAttrsAndState(FlexSpecification specObj, boolean bValid, String currentTemplateName)
			throws WTException, WTPropertyVetoException {
		LCSSeason seasonObj = (LCSSeason) specObj.getValue(SEASONREFERENCE);
		if (seasonObj != null) {
			LCSProductSeasonLink psLink = getProdSeasLink(seasonObj, specObj);
			FlexSpecToSeasonLink SpecSeasLink = FlexSpecQuery.findSpecToSeasonLink(specObj.getMaster(),
					seasonObj.getMaster());
			if (psLink != null && !psLink.isSeasonRemoved() && SpecSeasLink != null) {
				LCSProduct prodRev = SeasonProductLocator.getProductSeasonRev(psLink);

				if (bValid && currentTemplateName.equals(SPORTMASTER_SPECIFICATION_LIFECYCLE)) {
					setSpecAttrsAndLCStates(specObj, seasonObj, prodRev);

				}
			}
		}
	}

	/**
	 * @param specObj
	 * @param seasonObj
	 * @param prodRev
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void setSpecAttrsAndLCStates(FlexSpecification specObj, LCSSeason seasonObj, LCSProduct prodRev)
			throws WTException, WTPropertyVetoException {
		String specStatus = (String) specObj.getValue(SPEC_STATUS);
		LOGGER.debug("Spec Status== " + specStatus);
		FlexSpecification previousSpecObj = (FlexSpecification) VersionHelper.predecessorOf(specObj);

		String specStage = getSpecStageValue(specObj);
		LOGGER.debug("Spec Stage== " + specStage);
		String previousSpecStatus = "";
		String previousSpecStage = "";
		String previousLCState = "";
		if (previousSpecObj != null) {
			LOGGER.debug("previousSpecObj== " + previousSpecObj);
			previousSpecStatus = (String) previousSpecObj.getValue(SPEC_STATUS);
			/*
			 * if (specObj.getFlexType().getFullNameDisplay().startsWith(SEPD_SPEC)) { //
			 * smSEPDSpecStage previousSpecStage = (String)
			 * previousSpecObj.getValue(SEPD_SPEC_STAGE); } else if
			 * (specObj.getFlexType().getFullNameDisplay().startsWith(APPAREL_SPEC)) {
			 * previousSpecStage = (String) previousSpecObj.getValue(APP_SPEC_STAGE); }
			 */
			previousSpecStage = getSpecStageValue(previousSpecObj);
			previousLCState = previousSpecObj.getState().getState().toString();
		}

		String specConfirmationBeforeSampleProduction = (String) specObj
				.getValue(SPEC_CONFIRMATION_BEFORE_SAMPLE_PRODUCTION);

		Boolean needToReissueFinalSpecification = (Boolean) specObj.getValue(SPECIFICATION_FINAL_APPROVAL_ON_PPS_STAGE);

		Boolean finalSpecificationReissued = (Boolean) specObj.getValue(FINAL_SPECIFICATION_REISSUED);

		String currentLCState = specObj.getState().getState().toString();
		LifeCycleTemplateReference ref = specObj.getLifeCycleTemplate();

		// Template 1 TR Revision
		// If specConfirmationBeforeSampleProduction equals NeedSMRevision
		if (FormatHelper.hasContent(specConfirmationBeforeSampleProduction)
				&& specConfirmationBeforeSampleProduction.equals(NEED_SM_REVISION)) {
			setStateNeedRevision(specObj, previousSpecObj, currentLCState, ref);
		} else if (FormatHelper.hasContent(specConfirmationBeforeSampleProduction)
				&& specConfirmationBeforeSampleProduction.equals(REVISED_BY_SM)) {
			setStateRevised(specObj, previousSpecObj, currentLCState, ref);
		} else if (FormatHelper.hasContent(specConfirmationBeforeSampleProduction)
				&& specConfirmationBeforeSampleProduction.equals(SPEC_CONFIRMED_BY_SUPPLIER)) {
			// Template 1 End Task
			terminateWorkflowTask(specObj);
		}

		sendMailForEnggSampleSpec(specObj, prodRev, specStatus, specStage, previousSpecStatus, previousSpecStage/*,
				previousLCState*/);

		setStateReissue(specObj, previousSpecObj, previousLCState, specConfirmationBeforeSampleProduction,
				needToReissueFinalSpecification, currentLCState, ref);

		setStateComplete(specObj, seasonObj, prodRev, previousSpecObj, previousLCState, finalSpecificationReissued,
				currentLCState, ref);
	}

	/**
	 * @param specObj
	 * @param seasonObj
	 * @param prodRev
	 * @param previousSpecObj
	 * @param previousLCState
	 * @param finalSpecificationReissued
	 * @param currentLCState
	 * @param ref
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	private static void setStateComplete(FlexSpecification specObj, LCSSeason seasonObj, LCSProduct prodRev,
			FlexSpecification previousSpecObj, String previousLCState, Boolean finalSpecificationReissued,
			String currentLCState, LifeCycleTemplateReference ref) throws WTPropertyVetoException, WTException {
		// PPS Specification - Final task complete
		if (finalSpecificationReissued && previousLCState.equals(REISSUE) && !currentLCState.equals(COMPLETE)) {
			// setSpecStateBasedOnAttributes(specObj);
			// Template 3 final task
			terminateCurrentWFAndSetNewState(specObj, ref, previousSpecObj, COMPLETE);
			LOGGER.debug("Current Spec state= " + specObj.getState().getState().toString());

			ArrayList<WTPrincipal> toUser = getUsersToSendMailForCompleteState(specObj, seasonObj, prodRev);
			sendEmailToUser(specObj, toUser, "PPS Specification", "5");
			specObj.setValue(SPECIFICATION_FINAL_APPROVAL_ON_PPS_STAGE, false);
		}

		
	}

	/**
	 * @param specObj
	 * @param seasonObj
	 * @param prodRev
	 * @return
	 * @throws WTException
	 */
	private static ArrayList<WTPrincipal> getUsersToSendMailForCompleteState(FlexSpecification specObj,
			LCSSeason seasonObj, LCSProduct prodRev) throws WTException {
		ArrayList<WTPrincipal> toUser = new ArrayList<>();

		String strProductionManager = SmWorkflowHelper.teamAssignmentAttribute(PRODUCTION_MANAGER, prodRev);
		@SuppressWarnings("deprecation")
		WTUser wtUser2 = wt.org.OrganizationServicesHelper.manager.getUser(strProductionManager);
		if (wtUser2 != null) {
			toUser.add(wtUser2);
		}
		LOGGER.debug(strProductionManager);

		// Getting SourceingConfig Master.
		LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) specObj.getSpecSource();
		LOGGER.debug("source Master == " + sourceMaster);
		if (sourceMaster != null) {
			// Getting Sourcing Config Object.
			LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);
			LOGGER.debug("source obj == " + sourceObj);
			// Getting season product link
			LCSSeasonProductLink spLink = SeasonProductLocator.getSeasonProductLink(prodRev);
			if (spLink != null) {
				LCSSourceToSeasonLink sourceToSeasonLink = new LCSSourcingConfigQuery().getSourceToSeasonLink(sourceObj,
						seasonObj);
				String developerOSO = getDeveloperOSOUser(sourceObj, sourceToSeasonLink);
				if (FormatHelper.hasContent(developerOSO)) {
					@SuppressWarnings("deprecation")
					WTUser user = (WTUser) OrganizationServicesHelper.manager.getPrincipal(developerOSO);
					LOGGER.debug("developer user== " + user);
					toUser.add(user);
				}
			}
			
			WTGroup group = getVendorGroup(sourceObj);
		
			LOGGER.debug("wt group== " + group);
			if (group != null) {
				toUser.add(group);
				LOGGER.debug(toUser);
			}			
		}
		return toUser;
	}

	/**
	 * @param specObj
	 * @param previousSpecObj
	 * @param previousLCState
	 * @param specConfirmationBeforeSampleProduction
	 * @param needToReissueFinalSpecification
	 * @param currentLCState
	 * @param ref
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	private static void setStateReissue(FlexSpecification specObj, FlexSpecification previousSpecObj,
			String previousLCState, String specConfirmationBeforeSampleProduction,
			Boolean needToReissueFinalSpecification, String currentLCState, LifeCycleTemplateReference ref)
			throws WTPropertyVetoException, WTException {
		// Template 3 Reissue PPS TR
		if (needToReissueFinalSpecification && (previousLCState.equals(DEFAULT) || previousLCState.equals(REVISED)
				|| previousLCState.equals(COMPLETE))) {

			// setSpecStateBasedOnAttributes(specObj);
			// Template 3 task1
			if (!(currentLCState.equals(REISSUE))
					&& (currentLCState.equals(DEFAULT) || currentLCState.equals(REVISED)
							|| currentLCState.equals(COMPLETE))
					&& !(specConfirmationBeforeSampleProduction.equals(NEED_SM_REVISION))) {

				// Setting Specification Lifecycle state as REISSUE.
				terminateCurrentWFAndSetNewState(specObj, ref, previousSpecObj, REISSUE);
				LOGGER.debug("Spec St==" + specObj.getState().getState().toString());

			}
			specObj.setValue(FINAL_SPECIFICATION_REISSUED, false);
		}
	}

	/**
	 * @param specObj
	 * @param prodRev
	 * @param specStatus
	 * @param specStage
	 * @param previousSpecStatus
	 * @param previousSpecStage
	 * @param previousLCState
	 * @throws WTException
	 */
	private static void sendMailForEnggSampleSpec(FlexSpecification specObj, LCSProduct prodRev, String specStatus,
			String specStage, String previousSpecStatus, String previousSpecStage/*, String previousLCState */)
			throws WTException {
		// For Template 2 Eng TR
		if (specStage.equals(ENGINEERING_SAMPLE) && specStatus.equals(APPROVED)
				&& (!(previousSpecStatus.equals(APPROVED)) || !(previousSpecStage.equals(ENGINEERING_SAMPLE)))
				/* && (previousLCState.equals(DEFAULT)) */) {

			String strProductionManager = SmWorkflowHelper.teamAssignmentAttribute(PRODUCTION_MANAGER, prodRev);
			ArrayList<WTPrincipal> toUser = new ArrayList<>();
			@SuppressWarnings("deprecation")
			WTUser wtUser2 = wt.org.OrganizationServicesHelper.manager.getUser(strProductionManager);
			if (wtUser2 != null) {
				toUser.add(wtUser2);
			}

			// Send Email to Technologist and Production Manager
			LOGGER.debug("Send Email to Production Manager: " + strProductionManager);
			sendEmailToUser(specObj, toUser, ENGGSPEC_CREATED_TASK, "3");
		}
	}

	/**
	 * @param specObj
	 * @param previousSpecObj
	 * @param currentLCState
	 * @param ref
	 * @throws WTPropertyVetoException
	 */
	private static void setStateRevised(FlexSpecification specObj, FlexSpecification previousSpecObj,
			String currentLCState, LifeCycleTemplateReference ref) throws WTPropertyVetoException {
		// Template 1 Task 2
		// setSpecStateBasedOnAttributes(specObj);
		// Template 1 Task 2
		if (!currentLCState.equals(REVISED)) {
			// Setting Specification Lifecycle state as REVISED.
			terminateCurrentWFAndSetNewState(specObj, ref, previousSpecObj, REVISED);
			LOGGER.debug("Spec Current State==" + specObj.getState().getState().toString());

		}
	}

	/**
	 * @param specObj
	 * @param previousSpecObj
	 * @param currentLCState
	 * @param ref
	 * @throws WTPropertyVetoException
	 */
	private static void setStateNeedRevision(FlexSpecification specObj, FlexSpecification previousSpecObj,
			String currentLCState, LifeCycleTemplateReference ref) throws WTPropertyVetoException {
		// Template 1 Task 1
		// setSpecStateBasedOnAttributes(specObj);
		// previousLCState = previousSpecObj.getState().getState().toString();
		// LOGGER.debug("previous lc state====" + previousLCState);
		// Template 1 Task 1
		if (!currentLCState.equals(NEED_REVISION)) {
			// Setting Specification Lifecycle state as SMNEEDREVISION.
			terminateCurrentWFAndSetNewState(specObj, ref, previousSpecObj, NEED_REVISION);
			LOGGER.debug("Spec State=" + specObj.getState().getState().toString());

		}
	}

	/**
	 * @param specObj
	 * @return
	 * @throws WTException
	 */
	private static String getSpecStageValue(FlexSpecification specObj) throws WTException {
		String specStage = "";
		if (specObj.getFlexType().getFullNameDisplay().startsWith(SEPD_SPEC)) {
			// smSEPDSpecStage
			specStage = (String) specObj.getValue(SEPD_SPEC_STAGE);
		} else if (specObj.getFlexType().getFullNameDisplay().startsWith(APPAREL_SPEC)) {
			specStage = (String) specObj.getValue(APP_SPEC_STAGE);
		}
		return specStage;
	}

	

	/**
	 * Send email
	 * 
	 * @param specObj
	 * @param designer
	 * @param statusMail
	 * @param from
	 * @throws WTException
	 */
	private static void sendEmailToUser(FlexSpecification specObj, ArrayList<WTPrincipal> toUser, String strTaskName, String strTaskNo)
			throws WTException {
		LOGGER.debug("start - Inside CLASS--SMProductSpecificationWorkflowPlugin and METHOD--sendEmailToUser");

		String styleName = "";
		String sourceName = "";
		String specName = "";
		try {
			// Getting SourcingConfig Master.
			LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) specObj.getSpecSource();
			if (sourceMaster != null) {

				// Getting Sourcing Config Object.
				LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);

				LCSProduct prodObj = (com.lcs.wc.product.LCSProduct) VersionHelper
						.latestIterationOf(specObj.getSpecOwner());
				styleName = prodObj.getName();

				sourceName = (String) sourceObj.getFlexType().getAttribute(BUSINESS_SUPPLIER)
						.getDisplayValue(sourceObj);

				specName = specObj.getName();

				ClientContext lcsContext = ClientContext.getContext();
				LOGGER.debug(lcsContext);
				// Get current user to set as email from
				wt.org.WTUser from = UserGroupHelper.getWTUser(lcsContext.getUserName());
				LOGGER.debug("from user== " + from);

				SMEmailHelper statusMail = new SMEmailHelper();
				LOGGER.debug("statusMail== " + statusMail);

				LOGGER.debug("toUser===" + toUser);

				// MAIL CONTENT
				String srMailHeader = LCSProperties
						.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.MailHeader");
				LOGGER.debug("srMailHeader==" + srMailHeader);

				String strSubject = String.format(
						LCSProperties.get(
								"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.MailSubject"),
						strTaskName);
				LOGGER.debug("strSubject==" + strSubject);
				if (strTaskName.equals("Check Specification comments from Supplier")) {
					strSubject = String.format(LCSProperties.get(
						"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.MailSubject.1"));
				}				
				else if (strTaskName.equals("PPS Specification")) {
					strSubject = String.format(LCSProperties.get(
						"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.MailSubject.2"));
				}
				else if (strTaskName.equals(ENGGSPEC_CREATED_TASK)) {
					strSubject = String.format(LCSProperties.get(
						"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.MailSubject.3"));
				}
				LOGGER.debug("strSubject=updated=" + strSubject);
				
				String strMailContent = LCSProperties
						.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.MailContent."+strTaskNo);
				LOGGER.debug("strTaskNo===" + strTaskNo);
				LOGGER.debug("strMailContent===" + strMailContent);
				/*
				if (strTaskName.equals("Check Specification comments from Supplier")) {
					strMailContent = LCSProperties
							.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.MailContent.1");
					LOGGER.debug("strMailContent for task 1===" + strMailContent);
				}

				if (strTaskName.equals("Check Specification updates from SM")) {
					strMailContent = LCSProperties
							.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.MailContent.2");
					LOGGER.debug("strMailContent for task 2==" + strMailContent);
				}
				if (strTaskName.equals("Engineering Specification created")) {
					strMailContent = LCSProperties
							.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.MailContent.3");
					LOGGER.debug("strMailContent for task 3==" + strMailContent);
				}
				if (strTaskName.equals("Update Specification for PPS according to PMs comments/Developer's report")) {
					strMailContent = LCSProperties
							.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.MailContent.4");
					LOGGER.debug("strMailContent for task 4==" + strMailContent);
				}
				if (strTaskName.equals("PPS Specification")) {
					strSubject = String.format(LCSProperties.get(
							"com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.MailSubject.2"));
					strMailContent = LCSProperties
							.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.MailContent.5");
					LOGGER.debug("strMailContent for task 5==" + strMailContent);
				}
				 */
				StringBuilder sb = new StringBuilder();
				sb.append(strMailContent);
				sb.append("<br><ol>");
				sb.append("<li>" + "Style Name: " + styleName + "<br>");
				sb.append("<li>" + "Source Name: " + sourceName + "<br>");
				sb.append("<li>" + "Spec Name: " + specName);
				sb.append("</ol>");

				statusMail.sendEmail(from, toUser, sb.toString(), strSubject, srMailHeader);
				LOGGER.debug("end - Inside CLASS--SMProductSpecificationWorkflowPlugin and METHOD--sendEmailToUser");
			}
		} catch (WTException e1) {
			e1.printStackTrace();
			LOGGER.error("WTException in sendEmailToUser=" + e1.getMessage());
		}

	}

	/**
	 * @param specObj
	 * @param ref
	 * @param previousSpecObj
	 * @param setState
	 * @throws WTPropertyVetoException
	 */
	private static void terminateCurrentWFAndSetNewState(FlexSpecification specObj, LifeCycleTemplateReference ref,
			FlexSpecification previousSpecObj, String setState) throws WTPropertyVetoException {

		LOGGER.debug(
				"start - Inside CLASS--SMProductSpecificationWorkflowPlugin and METHOD--terminateCurrentWFAndSetNewState");

		LifeCycleState st = new LifeCycleState();

		if (previousSpecObj != null) {

			// Terminate the WF task once LC State is SD Approved
			LOGGER.debug(
					"terminateCurrentWFAndSetNewState method - PrePersistSSP: Terminate WF task when LC State is --->>>"
							+ previousSpecObj.getLifeCycleState().toString());
			terminateWorkflowTask(previousSpecObj);
		}
		LOGGER.debug("Set state to--->>>" + setState);
		State stateChange = State.toState(setState);
		st.setLifeCycleId(ref);
		st.setState(stateChange);
		specObj.setState(st);
		LOGGER.debug("after change to state==" + specObj.getLifeCycleState().toString());
		LOGGER.debug(
				"end - Inside CLASS--SMProductSpecificationWorkflowPlugin and METHOD--terminateCurrentWFAndSetNewState");
	}

	/**
	 * Method to terminate the running workflow tasks
	 * 
	 * @param obj
	 * @throws WTPropertyVetoException
	 */
	public static void terminateWorkflowTask(WTObject obj) throws WTPropertyVetoException {
		LOGGER.debug("start - Inside CLASS--SMProductSpecificationWorkflowPlugin and METHOD--terminateWorkflowTask");

		StandardWfEngineService service = new StandardWfEngineService();
		@SuppressWarnings("rawtypes")
		Enumeration processes;
		try {
			// Get the current running workflow tasks
			processes = service.getAssociatedProcesses(obj, WfState.OPEN_RUNNING);
			// Auto complete the current WF task
			while (processes.hasMoreElements()) {
				WfProcess process = (WfProcess) processes.nextElement();
				LOGGER.debug(" Process--->>>>" + process);
				try {
					service.changeState(process, WfTransition.TERMINATE);
				} catch (WTException e) {
					e.printStackTrace();
					LOGGER.error("WTException in terminateWorkflowTask=" + e.getMessage());
				}
			}
		} catch (WTException e1) {
			e1.printStackTrace();
			LOGGER.error("WTException in terminateWorkflowTask=" + e1.getMessage());
		}
		LOGGER.debug("end - Inside CLASS--SMProductSpecificationWorkflowPlugin and METHOD--terminateWorkflowTask");
	}

	/*
	 * setSpecificationLCS. Post Persist on Specification to set LC and to terminate
	 * the tasks automatically.
	 * 
	 * @param wtObject for wtObject. POST PERSIST SSP
	 * 
	 * @return void.
	 */
	public static void setSpecificationLCS(WTObject wtObject) throws WTPropertyVetoException {
		LOGGER.debug("In SMProductSpecificationWorkflowPlugin: setSpecificationLCS Method - Start");
		// Getting Specification object.
		try {

			FlexSpecification specObj = (FlexSpecification) wtObject;
			LOGGER.debug("specObj==" + specObj);
			// Getting current LC State
			String currentLCState = specObj.getState().getState().toString();
			LOGGER.debug("current LC State==" + currentLCState);

			FlexSpecification previousSpecObj = (FlexSpecification) VersionHelper.predecessorOf(specObj);

			// Calling Valid Spec type method.
			boolean bValid = checkValidSpecType(specObj);
			String currentTemplateName = specObj.getLifeCycleName();
			LCSSeason seasonObj = (LCSSeason) specObj.getValue(SEASONREFERENCE);
			if (seasonObj != null) {
				LCSProductSeasonLink psLink = getProdSeasLink(seasonObj, specObj);
				FlexSpecToSeasonLink SpecSeasLink = FlexSpecQuery.findSpecToSeasonLink(specObj.getMaster(),
						seasonObj.getMaster());

				if (psLink != null && !psLink.isSeasonRemoved() && SpecSeasLink != null) {
					startWFTaskForSpecStates(specObj, currentLCState, previousSpecObj, bValid, currentTemplateName,
							psLink);
				}
			}
		} catch (WTException e) {

			LOGGER.error("WTException in setSpecificationLCS method: " + e.getMessage());
			e.printStackTrace();

		}

		LOGGER.debug("In SMProductSpecificationWorkflowPlugin: setSpecificationLCS Method - End");
	}

	/**
	 * @param specObj
	 * @param currentLCState
	 * @param previousSpecObj
	 * @param bValid
	 * @param currentTemplateName
	 * @param psLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void startWFTaskForSpecStates(FlexSpecification specObj, String currentLCState,
			FlexSpecification previousSpecObj, boolean bValid, String currentTemplateName, LCSProductSeasonLink psLink)
			throws WTException, WTPropertyVetoException {
		LCSProduct prodRev = SeasonProductLocator.getProductSeasonRev(psLink);

		LOGGER.debug("bvalid=" + bValid + "\n current template name===" + currentTemplateName);
		if (bValid && currentTemplateName.equals(SPORTMASTER_SPECIFICATION_LIFECYCLE)) {

			String prevLCState = "";
			if (previousSpecObj != null) {
				prevLCState = previousSpecObj.getState().getState().toString();
				LOGGER.debug("Previous LC State" + prevLCState);
			}

			String specConfirmationBeforeSampleProduction = (String) specObj
					.getValue(SPEC_CONFIRMATION_BEFORE_SAMPLE_PRODUCTION);
			LOGGER.debug("specConfirmationBeforeSampleProduction===" + specConfirmationBeforeSampleProduction);

			boolean needToReissueFinalSpecification = (boolean) specObj
					.getValue(SPECIFICATION_FINAL_APPROVAL_ON_PPS_STAGE);
			LOGGER.debug("need to reissue final spec==" + needToReissueFinalSpecification);

			// template 1 task 1
			if (FormatHelper.hasContent(specConfirmationBeforeSampleProduction)
					&& specConfirmationBeforeSampleProduction.equals(NEED_SM_REVISION)
					&& currentLCState.equals(NEED_REVISION) && !prevLCState.equals(NEED_REVISION)) {
				startNeedRevisionWorkflowTask(specObj, prodRev);

			}

			// template 1 task 2
			else if (currentLCState.equals(REVISED) && !prevLCState.equals(REVISED)
					&& FormatHelper.hasContent(specConfirmationBeforeSampleProduction)
					&& specConfirmationBeforeSampleProduction.equals(REVISED_BY_SM)) {
				startRevisedWorkflowTask(specObj);
			}
			// Template 1 end task
			else if ((currentLCState.equals(REVISED))
					&& (FormatHelper.hasContent(specConfirmationBeforeSampleProduction)
							&& specConfirmationBeforeSampleProduction.equals(SPEC_CONFIRMED_BY_SUPPLIER))) {
				terminateWorkflowTask(specObj);

				LOGGER.debug("LCS State Spec=" + specObj.getState().getState().toString());
			}
			// template 3 task 1
			else if (needToReissueFinalSpecification && currentLCState.equals(REISSUE)
					&& (prevLCState.equals(DEFAULT) || prevLCState.equals(REVISED) || prevLCState.equals(COMPLETE))) {

				startReissueWorkflowTask(specObj, prodRev);

			}

		}
	}

	/**
	 * @param specObj
	 * @param prodRev
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void startReissueWorkflowTask(FlexSpecification specObj, LCSProduct prodRev)
			throws WTException, WTPropertyVetoException {
		LOGGER.debug("template 3 task 1 post persist");

		// startWFTaskForEachState(specObj, previousSpecObj);
		// Call method to Start Task
		startWorkflow(specObj, REISSUE_WORKFLOW);

		// LCSProduct prodObj = (com.lcs.wc.product.LCSProduct) VersionHelper
		// .latestIterationOf(specObj.getSpecOwner());
		ArrayList<WTPrincipal> toUser = new ArrayList<>();
		LOGGER.debug("toUser==" + toUser);

		String strTechnologist = SmWorkflowHelper.teamAssignmentAttribute(PRODUCCT_TECHNOLOGIST, prodRev);
		@SuppressWarnings("deprecation")
		WTUser wtUser1 = wt.org.OrganizationServicesHelper.manager.getUser(strTechnologist);
		if (wtUser1 != null) {
			toUser.add(wtUser1);
		}
		LOGGER.debug("Send Email to Technologist and PM");
		sendEmailToUser(specObj, toUser, "Update Specification for PPS according to PMs comments/Developer's report", "4");
	}

	/**
	 * @param specObj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void startRevisedWorkflowTask(FlexSpecification specObj)
			throws WTException, WTPropertyVetoException {
		if (specObj.isLatestIteration()) {
			// startWFTaskForEachState(specObj, previousSpecObj);
			// Call method to Start Task
			startWorkflow(specObj, REVISED_WORKFLOW);
			// Send Email to Supplier SUPPLIER
			ArrayList<WTPrincipal> toUser = new ArrayList<>();

			// Getting SourceingConfig Master.
			LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) specObj.getSpecSource();
			LOGGER.debug("\n wt sourceMaster== " + sourceMaster);
			if (sourceMaster != null) {
				// Getting Sourcing Config Object.
				LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);
				LOGGER.debug("\n wt sourceob= " + sourceObj);
				WTGroup group = getVendorGroup(sourceObj);
				if (group != null) {
					toUser.add(group);
					LOGGER.debug(toUser);
					sendEmailToUser(specObj, toUser, "Check Specification updates from SM", "2");
					LOGGER.debug("SUPPLIER");
				}
			}
		}
	}

	public static WTGroup getVendorGroup(LCSSourcingConfig sourceObj) throws WTException {
		LCSSupplier vendorObj = (LCSSupplier) sourceObj.getValue(BUSINESS_SUPPLIER);
		LOGGER.debug("\n wt vendorobj=== " + vendorObj);
		WTGroup group = null;
		if (vendorObj != null) {
			// Getting Vendor Group from Supplier object.
			String vendorGroup = (String) vendorObj.getValue(SUPPLIER_VENDOR_GROUP);
			LOGGER.debug("\n wt vendorgroup == " + vendorGroup);

			if (FormatHelper.hasContent(vendorGroup)) {
				// Setting String passed to WT Group.
				
				group = (WTGroup) OrganizationServicesHelper.manager.getPrincipal(vendorGroup);
				LOGGER.debug("\n wt wtgroup = " + group);
			}
		}
		return group;
	}

	/**
	 * @param specObj
	 * @param prodRev
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void startNeedRevisionWorkflowTask(FlexSpecification specObj, LCSProduct prodRev)
			throws WTException, WTPropertyVetoException {
		if (specObj.isLatestIteration()) {
			// startWFTaskForEachState(specObj, previousSpecObj);
			startWorkflow(specObj, NEED_FOR_REVISION_WORKFLOW);
			// LCSProduct prodObj = (com.lcs.wc.product.LCSProduct) VersionHelper
			// .latestIterationOf(specObj.getSpecOwner());

			ArrayList<WTPrincipal> toUser = new ArrayList<>();
			LOGGER.debug("toUser==" + toUser);

			String strTechnologist = SmWorkflowHelper.teamAssignmentAttribute(PRODUCCT_TECHNOLOGIST, prodRev);
			@SuppressWarnings("deprecation")
			WTUser wtUser1 = wt.org.OrganizationServicesHelper.manager.getUser(strTechnologist);
			if (wtUser1 != null) {
				toUser.add(wtUser1);
			}
			// LOGGER.debug(toUser);
			LOGGER.debug(strTechnologist);
			String strProductionManager = SmWorkflowHelper.teamAssignmentAttribute(PRODUCTION_MANAGER, prodRev);
			@SuppressWarnings("deprecation")
			WTUser wtUser2 = wt.org.OrganizationServicesHelper.manager.getUser(strProductionManager);
			if (wtUser2 != null) {
				toUser.add(wtUser2);
			}
			LOGGER.debug(strProductionManager);
			sendEmailToUser(specObj, toUser, "Check Specification comments from Supplier", "1");

			LOGGER.debug("EMAIL SENT TO TECHNOLOGIST AND PRODUCTION MANAGER");
		}
	}

	/**
	 * @param obj
	 * @param wfTemplateName
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static FlexSpecification startWorkflow(WTObject obj, String wfTemplateName)
			throws WTException, WTPropertyVetoException {

		FlexSpecification specObj = (FlexSpecification) obj;
		FlexSpecification specObjFirstVersion;
		LOGGER.debug("SMProductSpecificationWorkflowPlugin startWorkflow method - Start");
		if (obj instanceof FlexSpecification) {
			WTContainerRef exchangeRef = WTContainerHelper.service.getExchangeRef();
			ExchangeContainer container = (ExchangeContainer) exchangeRef.getObject();
			DirectoryContextProvider dcp = container.getContextProvider();
			WTOrganization org = OrganizationServicesHelper.manager
					.getOrganization(FlexContainerHelper.getOrganizationContainerName(), dcp);
			WTContainerRef containerRef = WTContainerHelper.service.getOrgContainerRef(org);
			WfProcessDefinition wfProcd = WfDefinerHelper.service.getProcessDefinition(wfTemplateName, containerRef);
			LOGGER.debug("PersistenceHelper.isPersistent(wfProcd)=" + PersistenceHelper.isPersistent(wfProcd));
			LOGGER.debug("wfProcd---->>>" + wfProcd);
			LOGGER.debug("wfProcd.getName()---->>>" + wfProcd.getName());

			if (PersistenceHelper.isPersistent(wfProcd)) {
				LOGGER.debug("specObj---->>>" + specObj);

				// Get first version of Specification Object (only with the first version
				// object,
				// we
				// are able to start the workflow task properly, as this logic internally
				// assigns some team templates)
				specObjFirstVersion = (FlexSpecification) VersionHelper.getFirstVersion(specObj);
				PersistenceHelper.manager.refresh(specObjFirstVersion);

				LOGGER.debug("specObj.getContainerReference()=== " + specObj.getContainerReference());
				LOGGER.debug("specObjFirstVersion.getContainerReference()=First vers== "
						+ specObjFirstVersion.getContainerReference());

				LOGGER.debug("specObj first vetrsion---->>>" + specObjFirstVersion);
				try {
					WfProcessDefinition wfpd = WfDefinerHelper.service.getProcessDefinition(wfTemplateName,
							containerRef);

					LOGGER.debug("wfpd ID= " + FormatHelper.getObjectId(wfpd));
					WfProcessTemplate wfpt = wfpd.getProcessTemplate();
					WfProcess aProcess = WfEngineHelper.service.createProcess(wfpt, specObjFirstVersion,
							specObjFirstVersion.getContainerReference());

					LOGGER.debug("Object ID= " + FormatHelper.getObjectId(specObj));
					LOGGER.debug("aProcess ID= " + FormatHelper.getObjectId(aProcess));
					LOGGER.debug(" aProcess--->>>" + aProcess);
					// Set the process name
					String processName = wfpd.getName() + " " + specObj.getName();
					aProcess.setName(processName);
					LOGGER.debug("processName ==" + processName);

					ProcessData context = aProcess.getContext();

					// Set the sample in the primaryBusinessObject attribute WF.
					context.setValue("primaryBusinessObject", specObjFirstVersion);
					LOGGER.debug("aProcess.getName()---->>>" + aProcess.getName());
					aProcess = WfEngineHelper.service.startProcess(aProcess, context, 1);
					LOGGER.debug("Process==" + aProcess);
					// Refresh the object to avoid collection contains stale exception
					PersistenceHelper.manager.refresh(specObjFirstVersion);
					PersistenceHelper.manager.refresh(specObj);

				} catch (WTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LOGGER.error("WTException in SMProductSpecificationWorkflowPlugin - startWorkflow method---"
							+ e.getLocalizedMessage());

				}

			}
		}
		LOGGER.debug("startWorkflow method - End");
		return specObj;
	}
}