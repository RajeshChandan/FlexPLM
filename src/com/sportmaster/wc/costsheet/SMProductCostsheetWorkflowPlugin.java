package com.sportmaster.wc.costsheet;

import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSCostSheet;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FlexContainerHelper;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.UserGroupHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.utils.SMEmailHelper;

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
import wt.org.WTPrincipalReference;
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

public class SMProductCostsheetWorkflowPlugin {

	/**
	 * Apparel ACC Type
	 */
	public static final String CSAPPACCTYPE = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSAppAccType");

	/**
	 * Apparel ACC MC Type
	 */
	public static final String CSAPPACCMCTYPE = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSAppAccMCType");

	/**
	 * SEPD CS Type
	 */
	public static final String CSSEPDALLTYPES = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSSEPDAllType");

	/**
	 * SEPD CS Type
	 */
	public static final String PRODUCTACCSEPDTYPE = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.ProdAccSEPDType");

	/**
	 * SEPD CS Type
	 */
	public static final String PRODUCTSEPDTYPE = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.ProdSEPDType");

	/**
	 * SEPD CS Type
	 */
	public static final String CSSEPDLCTEMPLATE = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSSEPDLCTEMPLATE");

	/**
	 * SEPD CS Type
	 */
	public static final String CSSEPDCOSTSHEETSTATUS = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSSEPDCOSTSHEETSTATUS");

	/**
	 * SEPD CS Type
	 */
	public static final String CSACCSEPDCOSTSHEETSTATUS = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSACCSEPDCOSTSHEETSTATUS");

	/**
	 * SEPD CS Type
	 */
	public static final String CSCOSTSHEETSTATUSQUOTED = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSCOSTSHEETSTATUSQUOTED");

	/**
	 * SEPD CS Type
	 */
	public static final String CSCOSTSHEETSTATUSINWORK = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSCOSTSHEETSTATUSINWORK");

	/**
	 * SEPD CS Type
	 */
	public static final String CSCOSTSHEETSTATUSAPPROVED = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSCOSTSHEETSTATUSAPPROVED");

	/**
	 * SEPD CS Type
	 */
	public static final String CSCOSTSHEETSTATUSREWORK = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSCOSTSHEETSTATUSREWORK");

	/**
	 * SEPD CS Type
	 */
	public static final String CSCOSTSHEETSTATUSCANCELLED = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSCOSTSHEETSTATUSCANCELLED");

	/**
	 * SEPD CS Type
	 */
	public static final String CSCOSTSHEETSTATUSCOSTER = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSCOSTSHEETSTATUSCOSTER");

	/**
	 * SEPD CS Type
	 */
	public static final String CSCOSTSHEETSTATUSCOSTERREWORK = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSCOSTSHEETSTATUSCOSTERREWORK");

	/**
	 * SEPD CS Type
	 */
	public static final String CSCOSTSHEETSTATUSCOSTERCHECKED = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSCOSTSHEETSTATUSCOSTERCHECKED");

	/**
	 * SEPD CS Type
	 */
	public static final String CSCOSTSHEETSTATUSVENDOR = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSCOSTSHEETSTATUSVENDOR");

	/**
	 * SEPD CS Type
	 */
	public static final String CSCOSTSHEETSTATUSVENDORSUBMITTED = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSCOSTSHEETSTATUSVENDORSUBMITTED");

	/**
	 * SEPD CS Type
	 */
	public static final String CSCOSTSHEETSTATUSVENDORRECEIVED = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSCOSTSHEETSTATUSVENDORRECEIVED");

	/**
	 * SEPD CS Type
	 */
	public static final String CSPURCHPRICECALCONTRACTCUR = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSPURCHPRICECALCONTRACTCUR");

	/**
	 * SEPD CS Type
	 */
	public static final String CSSEPDBLOCKEDCOSTSHEET = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSSEPDBLOCKEDCOSTSHEET");

	/**
	 * SEPD CS Type
	 */
	public static final String CSSEPDBLOCKEDCOSTSHEETYES = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSSEPDBLOCKEDCOSTSHEETYES");

	/**
	 * SEPD CS Type
	 */
	public static final String CSSEPDBLOCKEDCOSTSHEETNO = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSSEPDBLOCKEDCOSTSHEETNO");

	/**
	 * SEPD CS Type
	 */
	public static final String SCVENDOR = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.SCVENDOR");

	/**
	 * SEPD CS Type
	 */
	public static final String SUPPLIERVENDORGROUP = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.SUPPLIERVENDORGROUP");

	/**
	 * SEPD CS Type
	 */
	public static final String CSCOSTSHEETNUM = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSCOSTSHEETNUM");

	/**
	 * SEPD CS Type
	 */
	public static final String CSSEPDCOSTINGSTAGE = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSSEPDCOSTINGSTAGE");

	/**
	 * SEPD CS Type
	 */
	public static final String SMQUOTEDSTATE = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.SMQUOTEDSTATE");

	/**
	 * SEPD CS Type
	 */
	public static final String SUBMITTEDSTATE = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.SUBMITTEDSTATE");

	/**
	 * SEPD CS Type
	 */
	public static final String SMCHECKEDSTATE = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.SMCHECKEDSTATE");

	/**
	 * SEPD CS Type
	 */
	public static final String CSSEPDQUOTEDWFNAME = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSSEPDQUOTEDWFNAME");

	/**
	 * SEPD CS Type
	 */
	public static final String CSSEPDSUBMITTEDWFNAME = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSSEPDSUBMITTEDWFNAME");

	/**
	 * SEPD CS Type
	 */
	public static final String CSSEPDCHECKEDWFNAME = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSSEPDCHECKEDWFNAME");

	/**
	 * primaryBusinessObject
	 */
	static final String PRIMARY_BUSINESS_OBJECT = "primaryBusinessObject";
	/**
	 * LOGGER.
	 */
	public static final Logger logger = Logger.getLogger(SMProductCostsheetWorkflowPlugin.class);

	/**
	 * Method to set Costsheet LC States PRE_PERSIST SSP
	 * 
	 * @param wtobject
	 * @return
	 * @throws WTException
	 * 
	 */
	public static void setSEPDCostsheetData(WTObject obj) throws WTException {
		logger.debug("start - Inside CLASS--SMProductCostsheetWorkflowPlugin and METHOD--setSEPDCostsheetData");
		try {
			// Check if obj instanceof LCSCostSheet
			if (obj instanceof LCSCostSheet) {
				LCSCostSheet csObj = (LCSCostSheet) obj;
				logger.debug("csObj.getCheckoutInfo()====" + csObj.getCheckoutInfo());

				// Check if the object is the working copy
				// if (csObj.getCheckoutInfo() != null
				// && csObj.getCheckoutInfo().getState().toString().equalsIgnoreCase("wrk")
				if ((csObj.getCheckoutInfo() == null || (csObj.getCheckoutInfo() != null
						&& csObj.getCheckoutInfo().getState().toString().equalsIgnoreCase("wrk")))
						&& isValidCostSheetType(csObj)) {

					logger.debug("setSEPDCostsheetData obj=" + obj);

					String currentTemplateName = csObj.getLifeCycleName();
					logger.debug("In setSEPDCostsheetData method: currentTemplateName==" + currentTemplateName);

					logger.debug("setSEPDCostsheetData method: csObj name=" + csObj.getName());
					// Get Porduct object
					LCSProduct prodObj = getProduct(csObj);
					if (prodObj != null) {
						logger.debug("prodObj=" + prodObj);
						logger.debug("currentTemplateName=" + currentTemplateName);

						logger.debug("PRODUCTSEPDTYPE=" + PRODUCTSEPDTYPE);
						logger.debug("PRODUCTACCSEPDTYPE=" + PRODUCTACCSEPDTYPE);
						logger.debug("CSSEPDLCTEMPLATE=" + CSSEPDLCTEMPLATE);
						String strProductType = prodObj.getFlexType().getFullName();
						logger.debug("strProductType=" + strProductType);
						// Check if CS belongs to a Seasonal Product and the lifecycle tempalte
						// is "Sportmaster SEPD Product Costsheet Lifecycle"
						if (isValidProductType(strProductType) && currentTemplateName.equals(CSSEPDLCTEMPLATE)) {
							// logger.debug("IMGLCTEMPLATE=" + SEPDIMGLCTEMPLATE);
							// setCostsheetDataMethod(csObj);
							logger.debug("currentTemplateName inside bef setCSStateBasedOnCSAttributes="
									+ currentTemplateName);
							setLCSStateBasedOnCSAttributes(csObj);
						}
					}
				}
			}
		} catch (WTException e) {
			logger.error(
					"WTException in SMProductCostsheetWorkflowPlugin - setSEPDCostsheetData method: " + e.getMessage());
			e.printStackTrace();

		} catch (WTPropertyVetoException e) {
			logger.error(
					"WTPropertyVetoException in SMProductCostsheetWorkflowPlugin and METHOD--setSEPDCostsheetData -->> "
							+ e.getMessage());
			e.printStackTrace();
		}
		logger.debug("end - Inside CLASS--SMProductCostsheetWorkflowPlugin and METHOD--setSEPDCostsheetData");
	}

	/**
	 * @param strProductType
	 * @return
	 */
	private static boolean isValidProductType(String strProductType) {
		return (strProductType.startsWith(PRODUCTSEPDTYPE) || strProductType.startsWith(PRODUCTACCSEPDTYPE));
	}

	/**
	 * @param csObj
	 * @return
	 */
	private static boolean isValidCostSheetType(LCSCostSheet csObj) {
		return (csObj.getFlexType().getFullName().startsWith(CSSEPDALLTYPES)
				|| csObj.getFlexType().getFullName().startsWith(CSAPPACCTYPE)
				|| csObj.getFlexType().getFullName().startsWith(CSAPPACCMCTYPE));
	}

	/**
	 * getProduct.
	 * 
	 * @param csObj for csObj.
	 * 
	 * @return LCSProduct.
	 * @throws WTException.
	 */
	public static LCSProduct getProduct(LCSCostSheet csObj) throws WTException {

		// Getting Product RevA
		LCSProduct productRevA = (LCSProduct) VersionHelper.getVersion(csObj.getProductMaster(), "A");
		// Getting season object
		LCSSeason seasonObj = (LCSSeason) VersionHelper.latestIterationOf(csObj.getSeasonMaster());
		logger.debug("\n seasonObj == " + seasonObj);
		// Getting Product Season link.
		LCSSeasonProductLink spLink = LCSSeasonQuery.findSeasonProductLink(productRevA, seasonObj);
		logger.debug("\n spLink == " + spLink);

		LCSProduct prodRev = null;

		if (spLink != null && !(spLink.isSeasonRemoved())) {
			// Getting Product Object.
			prodRev = SeasonProductLocator.getProductSeasonRev(spLink);
			return prodRev;
		}
		return null;
	}

	/**
	 * Called from PRE_PERSIST SSP to set other states and its attribute values
	 * except In Work LC State
	 * 
	 * @param csObj
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void setLCSStateBasedOnCSAttributes(LCSCostSheet csObj) throws WTException, WTPropertyVetoException {
		logger.debug(
				"start - Inside CLASS--SMProductCostsheetWorkflowPlugin and METHOD--setCSStateBasedOnCSAttributes");

		LifeCycleTemplateReference ref = csObj.getLifeCycleTemplate();
		LifeCycleState st = new LifeCycleState();

		String csStatusKey = getCostSheetStatus(csObj);

		String csStatus = (String) csObj.getValue(csStatusKey);
		String csStatusCoster = (String) csObj.getValue(CSCOSTSHEETSTATUSCOSTER);
		String csStatusSupplier = (String) csObj.getValue(CSCOSTSHEETSTATUSVENDOR);

		LCSCostSheet previousCSObj = (LCSCostSheet) VersionHelper.predecessorOf(csObj);
		String previousCSStatus = "";
		String previousCSStatusCoster = "";
		String previousCSStatusSupplier = "";
		if (previousCSObj != null) {
			previousCSStatus = (String) previousCSObj.getValue(csStatusKey);
			previousCSStatusCoster = (String) previousCSObj.getValue(CSCOSTSHEETSTATUSCOSTER);
			previousCSStatusSupplier = (String) previousCSObj.getValue(CSCOSTSHEETSTATUSVENDOR);
		}

		if (isCSConditionsMatchToTerminateWF(csObj, csStatus, previousCSStatus)) {
			// Terminate all open WFs.
			terminateWorkflowTask(csObj);
			logger.debug("LCSState==" + csObj.getState().getState().toString());
		} else if (isCSStatusCosterChecked(csStatusCoster, previousCSStatusCoster)) {
			logger.debug(" SET LCS as Checked");
			// Setting Product Costsheet Lifecycle state as Checked.
			// setCostsheetLCS(csObj, SMCHECKEDSTATE);
			terminateCurrentWFAndSetNewState(csObj, ref, st, previousCSObj, SMCHECKEDSTATE);
			logger.debug("LC State CS=" + csObj.getState().getState().toString());
			// Blocked cost sheet = Yes;
			// Setting "Blocked cost sheet" attribute to Yes.
			csObj.setValue(CSSEPDBLOCKEDCOSTSHEET, CSSEPDBLOCKEDCOSTSHEETYES);
			logger.debug(
					" Attribute 'Cost Sheet Status (Coster)' is updated from [blank] or 'Rework' to 'Checked' then Blocked cost sheet"
							+ csObj.getValue(CSSEPDBLOCKEDCOSTSHEET));

		} else if (isCSStatusSupplierSubmitted(csStatusSupplier, previousCSStatusSupplier)) {
			logger.debug("SET LCS as Submitted");
			// Setting Product Costsheet Lifecycle state as Submitted.
			// setCostsheetLCS(csObj, SUBMITTED);
			terminateCurrentWFAndSetNewState(csObj, ref, st, previousCSObj, SUBMITTEDSTATE);
			logger.debug("Lifecycle State=" + csObj.getState().getState().toString());
			// Blocked cost sheet = Yes;
			// Setting "Blocked cost sheet" attribute to Yes.
			csObj.setValue(CSSEPDBLOCKEDCOSTSHEET, CSSEPDBLOCKEDCOSTSHEETYES);
			logger.debug(
					" Attribute 'Cost Sheet Status (Supplier)' is updated from 'Received' or 'Blank' to 'Submitted' then Blocked cost sheet"
							+ csObj.getValue(CSSEPDBLOCKEDCOSTSHEET));
		} else if (isCSStatusCosterRework(csStatusCoster, previousCSStatusCoster)) {
			logger.debug("SET LCS as QUOTED");
			// Setting Product Costsheet Lifecycle state as Quoted.
			// setCostsheetLCS(csObj, SMQUOTED);
			terminateCurrentWFAndSetNewState(csObj, ref, st, previousCSObj, SMQUOTEDSTATE);
			logger.debug("LCState=" + csObj.getState().getState().toString());
			// Cost Sheet Status (Supplier) = [blank];
			// - Blocked cost sheet = No
			// Setting "Blocked cost sheet" attribute to No.
			csObj.setValue(CSSEPDBLOCKEDCOSTSHEET, CSSEPDBLOCKEDCOSTSHEETNO);
			logger.debug(
					" Attribute 'Cost Sheet Status (Coster)' is updated from [blank] to 'Rework' then Blocked cost sheet"
							+ csObj.getValue(CSSEPDBLOCKEDCOSTSHEET));
			// Setting "Cost Sheet Status (Supplier)" attribute to blank.
			csObj.setValue(CSCOSTSHEETSTATUSVENDOR, "");
			logger.debug(
					" Attribute 'Cost Sheet Status (Coster)' is updated from [blank] to 'Rework' then Cost Sheet Status (Supplier)"
							+ csObj.getValue(CSCOSTSHEETSTATUSVENDOR));
			// send mail only for online supplier
			Boolean boolSupplier = checkOnlineOrOfflineVendor(csObj);
			logger.debug(" boolSupplier===" + boolSupplier);
			if (boolSupplier) {
				logger.debug(" ONLINE SUPPLIER - Sending mail");
				sendMail(csObj);
			}
		} else if (isCSStatusQuoted(csStatus, csStatusCoster, csStatusSupplier, previousCSObj, previousCSStatus)) {
			logger.debug("SET LCS as QUOTED");
			// Setting Product Costsheet Lifecycle state as Quoted.
			// setCostsheetLCS(csObj, SMQUOTED);
			terminateCurrentWFAndSetNewState(csObj, ref, st, previousCSObj, SMQUOTEDSTATE);
			logger.debug("State==" + csObj.getState().getState().toString());
		}

		logger.debug("end - Inside CLASS--SMProductCostsheetWorkflowPlugin and METHOD--setCSStateBasedOnCSAttributes");
	}

	/**
	 * @param csStatus
	 * @param csStatusCoster
	 * @param csStatusSupplier
	 * @param previousCSObj
	 * @param previousCSStatus
	 * @return
	 */
	private static boolean isCSStatusQuoted(String csStatus, String csStatusCoster, String csStatusSupplier,
			LCSCostSheet previousCSObj, String previousCSStatus) {
		// 1) If costsheet is created where:
		// (Cost sheet status = Quoted) & (Cost Sheet Status (Supplier) = [blank]) &
		// (Cost Sheet Status (Coster) = [blank])
		// 2) If costsheet is updated where:
		// Cost sheet status is changed from empty or ‘InWork’ to ‘Quoted’ & (Cost Sheet
		// Status (Supplier) = [blank]) & (Cost Sheet Status (Coster) = //[blank]). If
		// changed from other status – do nothing.
		// Enters this loop when CS status value is Quoted.
		return (!FormatHelper.hasContent(csStatusCoster) && !FormatHelper.hasContent(csStatusSupplier)
				&& ((previousCSObj == null && FormatHelper.hasContent(csStatus)
						&& csStatus.equals(CSCOSTSHEETSTATUSQUOTED))
						|| ((FormatHelper.hasContent(csStatus) && csStatus.equals(CSCOSTSHEETSTATUSQUOTED))
								&& ((FormatHelper.hasContent(previousCSStatus)
										&& (previousCSStatus.equals(CSCOSTSHEETSTATUSINWORK)))
										|| !FormatHelper.hasContent(previousCSStatus)))));
	}

	/**
	 * @param csStatusCoster
	 * @param previousCSStatusCoster
	 * @return
	 */
	private static boolean isCSStatusCosterRework(String csStatusCoster, String previousCSStatusCoster) {
		// Attribute 'Cost Sheet Status (Coster)' is updated from [blank] to 'Rework'
		return (!FormatHelper.hasContent(previousCSStatusCoster) && FormatHelper.hasContent(csStatusCoster)
				&& csStatusCoster.equals(CSCOSTSHEETSTATUSCOSTERREWORK));
	}

	/**
	 * @param csStatusSupplier
	 * @param previousCSStatusSupplier
	 * @return
	 */
	private static boolean isCSStatusSupplierSubmitted(String csStatusSupplier, String previousCSStatusSupplier) {
		// Attribute 'Cost Sheet Status (Supplier)' is updated from 'Received' or
		// 'Blank' to 'Submitted'.
		// Enters this loop when Cost Sheet Status (Supplier) value is Submitted.
		return ((FormatHelper.hasContent(previousCSStatusSupplier)
				&& (previousCSStatusSupplier.equals(CSCOSTSHEETSTATUSVENDORRECEIVED))
				|| !FormatHelper.hasContent(previousCSStatusSupplier)) && FormatHelper.hasContent(csStatusSupplier)
				&& csStatusSupplier.equals(CSCOSTSHEETSTATUSVENDORSUBMITTED));
	}

	/**
	 * @param csStatusCoster
	 * @param previousCSStatusCoster
	 * @return
	 */
	private static boolean isCSStatusCosterChecked(String csStatusCoster, String previousCSStatusCoster) {
		// 'Attribute 'Cost Sheet Status (Coster)' is updated from [blank] or 'Rework'
		// to 'Checked'
		// Enters this loop when Cost Sheet Status (Coster) is Checked.
		return ((!FormatHelper.hasContent(previousCSStatusCoster)
				|| previousCSStatusCoster.equals(CSCOSTSHEETSTATUSCOSTERREWORK))
				&& FormatHelper.hasContent(csStatusCoster) && csStatusCoster.equals(CSCOSTSHEETSTATUSCOSTERCHECKED));
	}

	/**
	 * @param csObj
	 * @param csStatus
	 * @param previousCSStatus
	 * @return
	 * @throws WTException
	 */
	private static boolean isCSConditionsMatchToTerminateWF(LCSCostSheet csObj, String csStatus,
			String previousCSStatus) throws WTException {
		// 1) ('Cost Sheet Status' is changed from 'Quoted' to ‘Approved’)& (Purchase
		// Price (Calc.)-ContractCur <> 0)
		// 2) 'Cost Sheet Status' is changed from 'Quoted' to ‘Rework’
		// Note: no matter which values are set in Cost Sheet Status (Supplier) and Cost
		// Sheet Status (Coster).
		// 'Attribute 'Cost Sheet Status' is updated from 'Quoted' to 'Cancelled'
		// Enters this loop when CS status value is either Approved or Rework or
		// Cancelled.
		// ADDED APPROVED to APPROVED condition also - to check with SM if it is
		// required or not
		logger.debug("Condition="+(FormatHelper.hasContent(previousCSStatus)
		&& (previousCSStatus.equals(CSCOSTSHEETSTATUSQUOTED)
				|| previousCSStatus.equals(CSCOSTSHEETSTATUSAPPROVED))
		&& FormatHelper.hasContent(csStatus) && csStatus.equals(CSCOSTSHEETSTATUSAPPROVED)
		&& !(0.0== (double) csObj.getValue(CSPURCHPRICECALCONTRACTCUR))));
		
		logger.debug("csStatus="+(csStatus.equals(CSCOSTSHEETSTATUSAPPROVED)));
		logger.debug("CSPURCHPRICECALCONTRACTCUR=="+csObj.getValue(CSPURCHPRICECALCONTRACTCUR));
		
		return ((FormatHelper.hasContent(previousCSStatus)
				&& (previousCSStatus.equals(CSCOSTSHEETSTATUSQUOTED)
						|| previousCSStatus.equals(CSCOSTSHEETSTATUSAPPROVED))
				&& FormatHelper.hasContent(csStatus) && csStatus.equals(CSCOSTSHEETSTATUSAPPROVED)
				&& !(0.0== (double) csObj.getValue(CSPURCHPRICECALCONTRACTCUR)))
				|| (FormatHelper.hasContent(previousCSStatus) && (previousCSStatus.equals(CSCOSTSHEETSTATUSQUOTED) || previousCSStatus.equals(CSCOSTSHEETSTATUSAPPROVED))
						&& FormatHelper.hasContent(csStatus) && csStatus.equals(CSCOSTSHEETSTATUSREWORK))
				|| (/*
					 * (FormatHelper.hasContent(previousCSStatus) &&
					 * previousCSStatus.equals(CSCOSTSHEETSTATUSQUOTED)) &&
					 */ FormatHelper.hasContent(csStatus) && csStatus.equals(CSCOSTSHEETSTATUSCANCELLED)));
	}

	/**
	 * @param csObj
	 * @return
	 */
	private static String getCostSheetStatus(LCSCostSheet csObj) {
		String csStatusKey = "";
		// Loop to check if Product costsheet type is SEPD.
		if (csObj.getFlexType().getFullName().startsWith(CSSEPDALLTYPES)) {
			// Method to set SEPD Product Costsheet Attributes.
			csStatusKey = CSSEPDCOSTSHEETSTATUS;
		} // Loop to check if Product costsheet type is Apparel\\Accessories or
			// Apparel\\AccMulticurrency.
		else if (csObj.getFlexType().getFullName().startsWith(CSAPPACCTYPE)
				|| csObj.getFlexType().getFullName().startsWith(CSAPPACCMCTYPE)) {
			logger.debug("APP - ACC/ APP - ACC MC type CS..");
			// Method to set SEPD Product Costsheet Attributes.
			csStatusKey = CSACCSEPDCOSTSHEETSTATUS;
		}
		return csStatusKey;
	}

	/**
	 * Method to terminate the running workflow tasks
	 * 
	 * @param obj
	 * @throws WTPropertyVetoException
	 */
	public static void terminateWorkflowTask(WTObject obj) throws WTPropertyVetoException {
		logger.debug("start - Inside CLASS--SMProductCostsheetWorkflowPlugin and METHOD--terminateWorkflowTask");

		StandardWfEngineService service = new StandardWfEngineService();
		Enumeration processes;
		try {
			// Get the current running workflow tasks
			processes = service.getAssociatedProcesses(obj, WfState.OPEN_RUNNING);
			// Auto complete the current WF task
			while (processes.hasMoreElements()) {
				WfProcess process = (WfProcess) processes.nextElement();
				try {
					logger.debug(" Process--->>>>" + process);
					// Terminate the current running task
					service.changeState(process, WfTransition.TERMINATE);
				} catch (WTException e) {
					logger.error("WTException in terminateWorkflowTask=" + e.getMessage());
					e.printStackTrace();
				}
			}
		} catch (WTException e1) {
			logger.error("WTException in terminateWorkflowTask=" + e1.getMessage());
			e1.printStackTrace();
		}
		logger.debug("end - Inside CLASS--SMProductCostsheetWorkflowPlugin and METHOD--terminateWorkflowTask");
	}

	/**
	 * @param csObj
	 * @param ref
	 * @param st
	 * @param predCS
	 * @param setState
	 * @throws WTPropertyVetoException
	 */
	private static void terminateCurrentWFAndSetNewState(LCSCostSheet csObj, LifeCycleTemplateReference ref,
			LifeCycleState st, LCSCostSheet predCS, String setState) throws WTPropertyVetoException {
		logger.debug(
				"start - Inside CLASS--SMProductCostsheetWorkflowPlugin and METHOD--terminateCurrentWFAndSetNewState");
		if (predCS != null) {
			// Terminate the WF task once LC State is SD Approved
			logger.debug(
					"terminateCurrentWFAndSetNewState method - PrePersistSSP: Terminate WF task when LC State is --->>>"
							+ predCS.getLifeCycleState().toString());
			terminateWorkflowTask(predCS);
		}
		logger.debug("Set state to--->>>" + setState);
		State stateChange = State.toState(setState);
		st.setLifeCycleId(ref);
		st.setState(stateChange);
		csObj.setState(st);
		logger.debug("after change to state==" + csObj.getLifeCycleState().toString());
		logger.debug(
				"end - Inside CLASS--SMProductCostsheetWorkflowPlugin and METHOD--terminateCurrentWFAndSetNewState");
	}

	/**
	 * @param csObj
	 * @return
	 */
	public static Boolean checkOnlineOrOfflineVendor(LCSCostSheet csObj) {
		boolean bVendorUserPresent = false;
		try {
			// Getting SourcingConfig Master.
			LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) csObj.getSourcingConfigMaster();
			if (sourceMaster != null) {
				// Getting Sourcing Config Object.
				LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);

				// Getting Vendor object.
				LCSSupplier vendorObj = (LCSSupplier) sourceObj.getValue(SCVENDOR);

				// Enters this loop only when vendor object has value.
				if (vendorObj != null) {
					// Getting Vendor Group from Supplier object.
					String vendorGroup = (String) vendorObj.getValue(SUPPLIERVENDORGROUP);
					bVendorUserPresent = isVendorUserPresent(vendorGroup);
				}
			}
			logger.debug(" bVendorUserPresent==========" + bVendorUserPresent);
		} catch (WTException e) {
			logger.error("WTException in assignSEPDSampleRequestCreatorUserToRole method: " + e.getMessage());
			e.printStackTrace();
		}
		logger.info("end - Inside CLASS--SmWorkflowHelper and METHOD--assignSEPDSampleRequestCreatorUserToRole");
		return bVendorUserPresent;
	}

	/**
	 * @param vendorGroup
	 * @return
	 * @throws WTException
	 */
	private static boolean isVendorUserPresent(String vendorGroup) throws WTException {
		boolean bVendorUserPresent = false;
		if (FormatHelper.hasContent(vendorGroup)) {
			// bVendorUserPresent = true;
			WTGroup wtGroup = UserGroupHelper.getWTGroup(vendorGroup);
			logger.debug("\n wtGroup==" + wtGroup);
			Enumeration enumer;
			WTPrincipalReference wtParentGroup;

			if (wtGroup != null) {
				enumer = wtGroup.parentGroups();
				while (enumer.hasMoreElements()) {
					wtParentGroup = (WTPrincipalReference) enumer.nextElement();
					logger.debug("Parent Group-=" + wtParentGroup.getName());
					if (wtParentGroup.getName().equals("Business Supplier")) {
						bVendorUserPresent = true;
						break;
					}
				}
			}
		}
		return bVendorUserPresent;
	}

	/**
	 * sendMail.
	 * 
	 * @param csObj for csObj.
	 * @return void.
	 * @throws WTException
	 */
	public static void sendMail(LCSCostSheet csObj) throws WTException {
		String seasonName = "";
		String styleName = "";
		String sourceName = "";
		String costingStage = "";
		Long csNo;
		String csName = "";

		// Getting SourcingConfig Master.
		LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) csObj.getSourcingConfigMaster();
		if (sourceMaster != null) {
			// Getting Sourcing Config Object.
			LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);

			// Getting Vendor object.
			LCSSupplier vendorObj = (LCSSupplier) sourceObj.getValue(SCVENDOR);

			// Getting season object
			LCSSeason seasonObj = (LCSSeason) VersionHelper.latestIterationOf(csObj.getSeasonMaster());
			if (seasonObj != null) {
				seasonName = seasonObj.getName();
				logger.debug("\n seasonName=" + seasonName);
			}
			logger.debug("\n seasonObj = " + seasonObj);

			// Getting Product RevA
			// LCSProduct productRevA = (LCSProduct)
			// VersionHelper.getVersion(csObj.getProductMaster(), "A");

			LCSProduct prodRev = getProduct(csObj);
			if (prodRev != null) {
				styleName = prodRev.getName();
			}
			logger.debug(" \n styleName=" + styleName);

			// Getting Product Season link.
			// LCSSeasonProductLink spLink =
			// LCSSeasonQuery.findSeasonProductLink(productRevA, seasonObj);
			// System.out.println("\n spLink == " + spLink);
			// LCSProduct prodRev = null;

			// if (spLink != null && !(spLink.isSeasonRemoved())) {
			// Getting Product Object.
			// prodRev = SeasonProductLocator.getProductSeasonRev(spLink);
			// Getting styleName.
			// styleName = prodRev.getName();
			// System.out.println(" \n styleName==============" + styleName);
			// }

			// sourceName = sourceObj.getName();
			// logger.debug("sourceName=" + sourceName);

			costingStage = (String) csObj.getFlexType().getAttribute(CSSEPDCOSTINGSTAGE).getDisplayValue(csObj);
			logger.debug("\n costingStage==" + costingStage);

			csNo = (Long) csObj.getValue(CSCOSTSHEETNUM);
			logger.debug("\n csNo==" + csNo);

			csName = csObj.getName();
			logger.debug("csName======" + csName);

			// Enters this loop only when vendor object has value.
			if (vendorObj != null) {

				sourceName = vendorObj.getName();
				logger.debug("sourceName=" + sourceName);

				// Getting Vendor Group from Supplier object.
				String vendorGroup = (String) vendorObj.getValue(SUPPLIERVENDORGROUP);

				// Send notification (IMMEDIATE notif.) to Supplier (if online) (For all users
				// with Supplier’s Vendor group)

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
				ClientContext lcsContext = ClientContext.getContext();

				// Getting From user details.
				wt.org.WTUser from = UserGroupHelper.getWTUser(lcsContext.getUserName());

				String mailHeader = "Dear FlexPLM User,<br><br>";
				// Email body content.
				String body = ("The following Cost Sheet should be reworked by you. Please, update tha data in the system and submit Cost Sheet once again.<br><br>Details: <br>Season Name: "
						+ seasonName + "<br>Style Name: " + styleName + "<br>Source Name: " + sourceName + "<br>Costing Stage: "
						+ costingStage + "<br>Cost Sheet#: " + csNo + "<br>Cost Sheet Name: " + csName);

				// Email Subject content.
				String strSubject = "FlexPLM Notification: Cost Sheet should be reworked. Task is created for you - DO NOT REPLY";
				// Calling sendEmail function of Class SMEmailHelper.
				logger.debug(" \nfrom===" + from + "\nstrSubject===" + strSubject + "\n to===" + to + "\nmailHeader==="
						+ mailHeader + "\nbody=====" + body);
				statusMail.sendEmail(from, to, body, strSubject, mailHeader);
			}
		}
	}

	/**
	 * Post Update Persist SSP - to trigger wf task and send email notification
	 * Method to start the workflow task for all LC States
	 * 
	 * @param obj
	 * @throws WTException
	 */
	public static void setSEPDCostsheetStatePostUpdatePersist(WTObject obj) throws WTException {

		logger.debug(
				"start - Inside CLASS--SMProductCostsheetWorkflowPlugin and METHOD--setSEPDCostsheetStatePostUpdatePersist");
		try {
			// Check if obj instanceof LCSCostSheet
			if (obj instanceof LCSCostSheet) {
				LCSCostSheet csObj = (LCSCostSheet) obj;
				logger.debug("In setSEPDCostsheetStatePostUpdatePersist checkedout state=="
						+ csObj.getCheckoutInfo().getState().toString());
				LCSProduct prodObj = getProduct(csObj);
				String currentTemplateName = csObj.getLifeCycleName();
				LCSCostSheet predCS = (LCSCostSheet) VersionHelper.predecessorOf(csObj);
				if (prodObj != null) {
					String strProductType = prodObj.getFlexType().getFullName();
					logger.debug("currentTemplateName==" + currentTemplateName);
					// If product type starts with sepd and template name = sportmaster sepd image
					// page lifecycle Template
					if (isValidProductType(strProductType) && currentTemplateName.equals(CSSEPDLCTEMPLATE)) {
						// FlexObject fo = (FlexObject) spLink.getValue(DESIGNERKEY);
						logger.debug("CS Name--->>>" + csObj.getName());
						logger.debug("isCheckedOut==" + VersionHelper.isCheckedOut(csObj));
						logger.debug("VersionHelper.isCheckedOut(csObj)=" + VersionHelper.isCheckedOut(csObj));
						logger.debug("co info==" + csObj.getCheckoutInfo().getState().toString());

						// CS ALL TYPES
						if (csObj.isLatestIteration() && isValidCostSheetType(csObj)
						/* && csObj.getCheckoutInfo().getState().toString().equalsIgnoreCase("wrk") */) {
							logger.debug("before calling startSEPDCostsheetWorkflowTaskforEachState...");
							// startSEPDCostsheetWorkflowTaskforEachState(csObj);
							startWFTaskForEachState(csObj, predCS);
						}
					}
				}
			}
		} catch (WTException e) {
			logger.error(
					"WTException in SMProductCostsheetWorkflowPlugin - setSEPDCostsheetStatePostUpdatePersist method: "
							+ e.getMessage());
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			logger.error(
					"WTPropertyVetoException in SMProductCostsheetWorkflowPlugin - setSEPDCostsheetStatePostUpdatePersist method: "
							+ e.getMessage());
			e.printStackTrace();
		}
		logger.debug(
				"end - Inside CLASS--SMProductCostsheetWorkflowPlugin and METHOD--setSEPDCostsheetStatePostUpdatePersist");

	}

	/**
	 * @param csObj
	 * @param predCS
	 * @param designerApproval
	 * @param strTechApproval
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	private static void startWFTaskForEachState(LCSCostSheet csObj, LCSCostSheet predCS)
			throws WTException, WTPropertyVetoException {
		logger.debug("start - Inside CLASS--SMProductCostsheetWorkflowPlugin and METHOD--startWFTaskForEachState");
		logger.debug("==== csObj==" + csObj);
		logger.debug("==== predCS==" + predCS);
		logger.debug("in method startWFTaskForEachState csObj==" + csObj.getLifeCycleState().toString());

		// Check if previous Image Page object's state not INWORK, so the task is not
		// assigned multiple times
		if (csObj.getLifeCycleState().toString().equals(SMQUOTEDSTATE)
				&& (predCS == null || !predCS.getLifeCycleState().toString().equals(SMQUOTEDSTATE))) {
			// Call method to Start Task
			startWorkflow(csObj, CSSEPDQUOTEDWFNAME);
		} else if (csObj.getLifeCycleState().toString().equals(SUBMITTEDSTATE) && predCS != null
				&& !predCS.getLifeCycleState().toString().equals(SUBMITTEDSTATE)) {
			logger.debug("in method startWFTaskForEachState predCS==" + predCS.getLifeCycleState().toString());
			// Call method to Start Task
			startWorkflow(csObj, CSSEPDSUBMITTEDWFNAME);
		} else if (csObj.getLifeCycleState().toString().equals(SMCHECKEDSTATE) && predCS != null
				&& !predCS.getLifeCycleState().toString().equals(SMCHECKEDSTATE)) {
			logger.debug("in method startWFTaskForEachState predCS==" + predCS.getLifeCycleState().toString());
			// Call method to Start Task
			startWorkflow(csObj, CSSEPDCHECKEDWFNAME);
			// String designer = getEmailUser(csObj, DESIGNERKEY);
			// logger.debug("designer===" + designer);
			// Send email to Designer from Team Assignment
			// sendEmailToUser(csObj, designer, "Update Design");
		}
		logger.debug("end - Inside CLASS--SMProductCostsheetWorkflowPlugin and METHOD--startWFTaskForEachState");
	}

	/**
	 * @param obj
	 * @param wfTemplateName
	 * @return
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static LCSCostSheet startWorkflow(WTObject obj, String wfTemplateName)
			throws WTException, WTPropertyVetoException {
		LCSCostSheet csObj = (LCSCostSheet) obj;
		LCSCostSheet csObjFirstVersion;
		logger.debug("SMImagePagePlugins startWorkflow method - Start");
		if (obj instanceof LCSCostSheet) {
			WTContainerRef exchangeRef = WTContainerHelper.service.getExchangeRef();
			ExchangeContainer container = (ExchangeContainer) exchangeRef.getObject();
			DirectoryContextProvider dcp = container.getContextProvider();

			WTOrganization org = OrganizationServicesHelper.manager
					.getOrganization(FlexContainerHelper.getOrganizationContainerName(), dcp);
			logger.debug("org---->>>" + org);
			WTContainerRef containerRef = WTContainerHelper.service.getOrgContainerRef(org);
			WfProcessDefinition wfProcd = WfDefinerHelper.service.getProcessDefinition(wfTemplateName, containerRef);
			logger.debug("wfProcd.getName()---->>>" + wfProcd.getName());
			logger.debug("wfProcd---->>>" + wfProcd);
			logger.debug("PersistenceHelper.isPersistent(wfProcd)=" + PersistenceHelper.isPersistent(wfProcd));

			if (PersistenceHelper.isPersistent(wfProcd)) {
				logger.debug("csObj---->>>" + csObj);

				// Get first version of Image Page Object (only with the first version object,
				// we
				// are able to start the workflow task properly, as this logic internally
				// assigns some team templates)
				csObjFirstVersion = (LCSCostSheet) VersionHelper.getFirstVersion(csObj);
				PersistenceHelper.manager.refresh(csObjFirstVersion);

				logger.debug("csObj.getContainerReference()=== " + csObj.getContainerReference());
				logger.debug("csObjFirstVersion.getContainerReference()=First vers== "
						+ csObjFirstVersion.getContainerReference());

				logger.debug("csObj first vetrsion---->>>" + csObjFirstVersion);
				try {
					WfProcessDefinition wfpd = WfDefinerHelper.service.getProcessDefinition(wfTemplateName,
							containerRef);

					logger.debug("wfpd ID= " + FormatHelper.getObjectId(wfpd));
					WfProcessTemplate wfpt = wfpd.getProcessTemplate();
					WfProcess aProcess = WfEngineHelper.service.createProcess(wfpt, csObjFirstVersion,
							csObjFirstVersion.getContainerReference());

					logger.debug("Object ID= " + FormatHelper.getObjectId(csObj));
					logger.debug("aProcess ID= " + FormatHelper.getObjectId(aProcess));
					logger.debug(" aProcess--->>>" + aProcess);
					// Set the process name
					String processName = wfpd.getName() + " " + csObj.getName();
					aProcess.setName(processName);
					logger.debug("processName ==" + processName);

					ProcessData context = aProcess.getContext();

					// Set the sample in the primaryBusinessObject attribute WF.
					context.setValue(PRIMARY_BUSINESS_OBJECT, csObjFirstVersion);
					logger.debug("aProcess.getName()---->>>" + aProcess.getName());
					aProcess = WfEngineHelper.service.startProcess(aProcess, context, 1);
					logger.debug("Process==" + aProcess);
					// Refresh the object to avoid collection contains stale exception
					PersistenceHelper.manager.refresh(csObjFirstVersion);
					PersistenceHelper.manager.refresh(csObj);

				} catch (WTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.error(
							"WTException in SMImagePagePlugins - startWorkflow method---" + e.getLocalizedMessage());

				}

			}
		}
		logger.debug("startWorkflow method - End");
		return csObj;
	}

}