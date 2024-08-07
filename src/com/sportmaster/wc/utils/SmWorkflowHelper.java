package com.sportmaster.wc.utils;

import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleRequest;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSCostSheet;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.UserGroupHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.document.SMImagePagePlugins;
import com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin;

import wt.fc.ObjectNoLongerExistsException;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.lifecycle.LifeCycleException;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.project.Role;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.team.TeamReference;
import wt.util.WTException;

/**
 * SmWorkflowHelper.
 * 
 * @author 'true' ITC_Infotech
 * @version 'true' 1.0
 */
public class SmWorkflowHelper {

	/**
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SmWorkflowHelper.class);
	/**
	 * Flex Object Name.
	 */
	public static final String FO_NAME = LCSProperties.get("com.sportmaster.wc.product.SmWorkflowHelper.fOName",
			"NAME");
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
	 * Sample request - Season.
	 */
	public static final String SEASON_REQUESTED = LCSProperties
			.get("com.sportmaster.wc.sample.SMProductSampleWorkflowPlugin.vrdSeasonRequested");
	// Phase 13 - Start
	/**
	 * Sourcing Config - OSO Developer Apparel.
	 */
	public static final String OSO_DEVELOPER_APPAREL = LCSProperties
			.get("com.sportmaster.wc.sample.SmWorkflowHelper.smOSOAppDeveloper", "smOSOAppDeveloper");
	/**
	 * Sourcing Config - OSO Developer SEPD.
	 */
	public static final String OSO_DEVELOPER_SEPD = LCSProperties
			.get("com.sportmaster.wc.sample.SmWorkflowHelper.smOSODeveloper", "smOSODeveloper");
	/**
	 * Sourcing Config Type - Apparel.
	 */
	public static final String APPAREL_SOURCE_TYPE = LCSProperties
			.get("com.sportmaster.wc.sample.SmWorkflowHelper.apparelSourceType", "Apparel");
	/**
	 * Sourcing Config - SEPD.
	 */
	public static final String SEPD_SOURCE_TYPE = LCSProperties
			.get("com.sportmaster.wc.sample.SmWorkflowHelper.sepdSourceType", "smSportsEquipement");
	/**
	 * Season Requested - SEPD.
	 */
	public static final String SEPD_SEASON_REQUESTED = LCSProperties
			.get("com.sportmaster.wc.sample.SmWorkflowHelper.vrdSeasonRequested", "vrdSeasonRequested");
	
	public static final String SEASONREFERENCE = LCSProperties
			.get("com.sportmaster.wc.specification.SMProductSpecificationWorkflowPlugin.smSeasonReference", "smSeasonReference");

	// Phase 13 - End
	/**
	 * Constructor.
	 */
	protected SmWorkflowHelper() {
		// protected constructor
	}

	/**
	 * assignGroupToRole.
	 * 
	 * @param primaryBusinessObject for primaryBusinessObject.
	 * @param targetRole            for targetRole.
	 * @param targetGroup           for targetGroup.
	 * @return void.
	 */
	public static void assignGroupToRole(WTObject primaryBusinessObject, String targetRole, String targetGroup) {

		LOGGER.info("start - Inside CLASS--SmWorkflowHelper and METHOD--assignGroupToRole");
		try {

			Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);

			Role role = wt.project.Role.toRole(targetRole);

			rolesEnum(team, role);

			// Setting String passed to WT Group.
			@SuppressWarnings("deprecation")
			WTGroup group = (WTGroup) OrganizationServicesHelper.manager.getPrincipal(targetGroup);

			// Adding Role to Group.
			@SuppressWarnings("unchecked")
			Enumeration<WTGroup> enumMembers = group.members();
			while (enumMembers.hasMoreElements()) {
				team.addPrincipal(role, enumMembers.nextElement());
			}

			team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
			TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
			TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject, targetTeamReference);
		} catch (TeamException e) {
			LOGGER.error("TeamException in assignGroupToRole method: " + e.getMessage());
			e.printStackTrace();
		} catch (WTException e) {
			LOGGER.error("WTException in assignGroupToRole method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SmWorkflowHelper and METHOD--assignGroupToRole");
	}

	/**
	 * assignFPDUserToRole.
	 * 
	 * @param team for team.
	 * @param role for role.
	 * @return void.
	 */
	public static void rolesEnum(Team team, Role role) {
		@SuppressWarnings("unchecked")
		Enumeration<WTPrincipalReference> enumRoles;
		try {
			enumRoles = team.getPrincipalTarget(role);

			WTPrincipalReference next;

			while (enumRoles.hasMoreElements()) {
				next = enumRoles.nextElement();
				team.deletePrincipalTarget(role, next.getPrincipal());
			}
		} catch (WTException e) {
			LOGGER.error("WTException in rolesEnum method: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * assignFPDUserToRole.
	 * 
	 * @param primaryBusinessObject for primaryBusinessObject.
	 * @param targetRole            for targetRole.
	 * @param attName               for attName.
	 * @return void.
	 */
	public static void assignFPDUserToRole(WTObject primaryBusinessObject, String targetRole, String attName) {

		LOGGER.info("start - Inside CLASS--SmWorkflowHelper and METHOD--assignFPDUserToRole");
		try {

			if (primaryBusinessObject instanceof LCSProduct) {

				// Getting Product Object.
				LCSProduct productObj = (LCSProduct) primaryBusinessObject;

				Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);

				Role role = wt.project.Role.toRole(targetRole);

				// Calling teamAssignmentAttribute() to get attribute value from Product Season.
				String psAttName = teamAssignmentAttribute(attName, productObj);

				if (FormatHelper.hasContent(psAttName)) {

					rolesEnum(team, role);

					// Setting String passed to WT User.
					@SuppressWarnings("deprecation")
					WTUser user = (WTUser) OrganizationServicesHelper.manager.getPrincipal(psAttName);

					// Adding User to Role.
					team.addPrincipal(role, user);

					team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
					TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
					TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,
							targetTeamReference);
				}

			}
			if (primaryBusinessObject instanceof LCSSKU) {

				// Getting SKU Object.
				LCSSKU skuObj = (LCSSKU) primaryBusinessObject;

				Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);

				Role role = wt.project.Role.toRole(targetRole);

				// Getting Product Object.
				LCSProduct product = SeasonProductLocator.getProductSeasonRev(skuObj);

				// Calling teamAssignmentAttribute() to get attribute value from Product Season.
				String psAttName = teamAssignmentAttribute(attName, product);

				if (FormatHelper.hasContent(psAttName)) {

					rolesEnum(team, role);

					// Setting String passed to WT User.
					@SuppressWarnings("deprecation")
					WTUser user = (WTUser) OrganizationServicesHelper.manager.getPrincipal(psAttName);

					// Adding User to Role.
					team.addPrincipal(role, user);

					team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
					TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
					TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,
							targetTeamReference);
				}
			}
			if (primaryBusinessObject instanceof LCSSample) {
				assignUserToRoleForSampleObj(primaryBusinessObject, targetRole, attName);
			}

			if (primaryBusinessObject instanceof LCSDocument) {
				assignUserToRoleForDocumentObj(primaryBusinessObject, targetRole, attName);
			}
		} catch (TeamException e) {
			LOGGER.error("TeamException in assignFPDUserToRole method: " + e.getMessage());
			e.printStackTrace();
		} catch (WTException e) {
			LOGGER.error("WTException in assignFPDUserToRole method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SmWorkflowHelper and METHOD--assignFPDUserToRole");
	}

	/**
	 * @param primaryBusinessObject
	 * @param targetRole
	 * @param attName
	 * @throws WTException
	 * @throws TeamException
	 * @throws ObjectNoLongerExistsException
	 * @throws LifeCycleException
	 */
	private static void assignUserToRoleForSampleObj(WTObject primaryBusinessObject, String targetRole, String attName)
			throws WTException, TeamException, ObjectNoLongerExistsException, LifeCycleException {
		// This functionality is called from Sportmaster Footwear Product Sample
		// Workflows.

		// Getting Sample Object.
		LCSSample sampleObj = (LCSSample) primaryBusinessObject;

		// Getting Sample Request Object.
		LCSSampleRequest sampleRequestObject = sampleObj.getSampleRequest();

		// Getting Season Object.
		LCSSeason seasonObj = (LCSSeason) sampleRequestObject.getValue(SEASON_REQUESTED);

		if (seasonObj != null && sampleObj.getOwnerMaster() instanceof LCSPartMaster) {

			// Getting Part Master Object.
			LCSPartMaster prodMaster = (LCSPartMaster) sampleObj.getOwnerMaster();

			// Getting Product Object.
			LCSProduct prodObj = VersionHelper.latestIterationOf(prodMaster);

			Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);

			Role role = wt.project.Role.toRole(targetRole);

			// Calling teamAssignmentAttribute() to get attribute value from Product Season.
			String psAttName = teamAssignmentAttributeForSampleWF(attName, prodObj, seasonObj);

			if (FormatHelper.hasContent(psAttName)) {

				rolesEnum(team, role);

				// Setting String passed to WT User.
				@SuppressWarnings("deprecation")
				WTUser user = (WTUser) OrganizationServicesHelper.manager.getPrincipal(psAttName);

				// Adding User to Role.
				team.addPrincipal(role, user);

				team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
				TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
				TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,
						targetTeamReference);
			}
		}
	}

	/**
	 * @param primaryBusinessObject
	 * @param targetRole
	 * @param attName
	 * @throws WTException
	 * @throws TeamException
	 * @throws ObjectNoLongerExistsException
	 * @throws LifeCycleException
	 */
	private static void assignUserToRoleForDocumentObj(WTObject primaryBusinessObject, String targetRole,
			String attName) throws WTException, TeamException, ObjectNoLongerExistsException, LifeCycleException {
		// This functionality is called from Sportmaster Image Page Workflow.
		LOGGER.debug("\n Document ====" + primaryBusinessObject);
		// Phase 13 - Start (Change to use common method for different product types)
		// Getting Product Object.
		// LCSProductSeasonLink lcsSeasonalProd = (LCSProductSeasonLink)
		// SMImagePageWFPlugin
		// .getSeasonalProductsForImagePages((LCSDocument) primaryBusinessObject);
		LCSProductSeasonLink lcsSeasonalProd = (LCSProductSeasonLink) SMImagePagePlugins
				.getSeasonalProductsForImagePages((LCSDocument) primaryBusinessObject);
		// Phase 13 - Ends
 
		Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);
		LOGGER.debug("\n  Document Team====" + team.getName());
		Role role = wt.project.Role.toRole(targetRole);

		// Calling teamAssignmentAttribute() to get attribute value from Product Season.
		String psAttName = teamAssignmentAttributeForImagePageWF(attName, lcsSeasonalProd);
		if (FormatHelper.hasContent(psAttName)) {
			rolesEnum(team, role);
			// Setting String passed to WT User.
			@SuppressWarnings("deprecation")
			WTUser user = (WTUser) OrganizationServicesHelper.manager.getPrincipal(psAttName);
			LOGGER.debug("\n user.getName() Document ====" + user.getName());
			// Adding User to Role.
			team.addPrincipal(role, user);

			team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
			LOGGER.debug("\n team ====" + team);
			TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
			LOGGER.debug("\n targetTeamReference ====" + targetTeamReference);
			// Phase 13, add refresh api before augmentRoles as recommended by PTC
			// (article:CS114198) to avoid assigning multiple same tasks for user - Start
			primaryBusinessObject = (WTObject) PersistenceHelper.manager.refresh(primaryBusinessObject);
			LOGGER.debug("\n primaryBusinessObject ====" + primaryBusinessObject);
			// Phase 13 - End
			TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject, targetTeamReference);
		}
	}

	/**
	 * teamAssignmentAttributeForImagePageWF.
	 * 
	 * @param attName for attName.
	 * @param product for product.
	 * @return String.
	 */
	public static String teamAssignmentAttributeForImagePageWF(String attName, LCSProductSeasonLink spLink) {
		String strName = "";
		try {
			if (spLink != null) {
				FlexObject fo = (FlexObject) spLink.getValue(attName);
				if (fo != null && fo.containsKey(FO_NAME) && FormatHelper.hasContent((String) fo.getData(FO_NAME))) {
					LOGGER.debug("\n Attribute Value====" + fo.getData(FO_NAME));
					strName = (String) fo.getData(FO_NAME);
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in teamAssignmentAttributeForImagePageWF method: " + e.getMessage());
			e.printStackTrace();
		}
		return strName;
	}

	/**
	 * teamAssignmentAttribute.
	 * 
	 * @param attName for attName.
	 * @param product for product.
	 * @return String.
	 */
	public static String teamAssignmentAttribute(String attName, LCSProduct product) {

		String result = "";
		try {
			// Getting season product link
			LCSSeasonProductLink spLink = SeasonProductLocator.getSeasonProductLink(product);
			if (spLink != null) {
				FlexObject foFlexObject = (FlexObject) spLink.getValue(attName);
				if (foFlexObject != null && foFlexObject.containsKey(FO_NAME)
						&& FormatHelper.hasContent((String) foFlexObject.getData(FO_NAME))) {
					result = (String) foFlexObject.getData(FO_NAME);
				}
			}
			LOGGER.debug("\n Attribute Value====" + result);

		} catch (WTException e) {
			LOGGER.error("WTException in teamAssignmentAttribute method: " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * assignFPDCreatorUserToRole.
	 * 
	 * @param primaryBusinessObject for primaryBusinessObject.
	 * @param targetRole            for targetRole.
	 * @return void.
	 */
	public static void assignFPDCreatorUserToRole(WTObject primaryBusinessObject, String targetRole) {

		LOGGER.info("start - Inside CLASS--SmWorkflowHelper and METHOD--assignFPDCreatorUserToRole");
		try {
			if (primaryBusinessObject instanceof LCSProduct) {

				// Getting Product Object.
				LCSProduct productObj = (LCSProduct) primaryBusinessObject;

				Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);

				Role role = wt.project.Role.toRole(targetRole);

				// Getting season product link
				LCSSeasonProductLink spLink = SeasonProductLocator.getSeasonProductLink(productObj);
				WTPrincipalReference creator = spLink.getCreator();
				LOGGER.debug("\n creator.getName()====" + creator.getName());

				rolesEnum(team, role);

				WTUser user = (WTUser) creator.getPrincipal();
				LOGGER.debug("\n user.getName()====" + user.getName());

				// Adding User to Role.
				team.addPrincipal(role, user);

				team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
				TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
				TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,
						targetTeamReference);
			}
			if (primaryBusinessObject instanceof LCSSKU) {

				// Getting SKU Object.
				LCSSKU skuObj = (LCSSKU) primaryBusinessObject;

				Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);

				Role role = wt.project.Role.toRole(targetRole);
				// Getting sku season link
				LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) LCSSeasonQuery
						.findSeasonProductLink(skuObj.getMaster(), skuObj.getSeasonMaster());
				WTPrincipalReference creator = skuSeasonLink.getCreator();
				LOGGER.debug("\n creator.getName() SKU====" + creator.getName());

				rolesEnum(team, role);

				WTUser user = (WTUser) creator.getPrincipal();
				LOGGER.debug("\n user.getName() SKU====" + user.getName());

				// Adding User to Role.
				team.addPrincipal(role, user);

				team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
				TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
				TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,
						targetTeamReference);
			}
			if (primaryBusinessObject instanceof LCSSample) {
				// This functionality is called from Sportmaster Apparel Product Sample
				// Evaluate Product Sample Workflow and Sole Material Sample Workflow.

				// Getting Sample Object.
				LCSSample sampleObj = (LCSSample) primaryBusinessObject;

				Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);

				Role role = wt.project.Role.toRole(targetRole);
				// Getting sample creator
				WTPrincipalReference creator = sampleObj.getCreator();
				LOGGER.debug("\n creator.getName()====" + creator.getName());

				rolesEnum(team, role);

				WTUser user = (WTUser) creator.getPrincipal();
				LOGGER.debug("\n user.getName()====" + user.getName());

				// Adding User to Role.
				team.addPrincipal(role, user);

				team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
				TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
				TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,
						targetTeamReference);
			}
		} catch (WTException e) {
			LOGGER.error("WTException in assignFPDCreatorUserToRole method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SmWorkflowHelper and METHOD--assignFPDCreatorUserToRole");
	}

	/**
	 * assignSupplierUserToRole.
	 * 
	 * @param primaryBusinessObject for primaryBusinessObject.
	 * @param targetRole            for targetRole.
	 * @return void.
	 */
	public static void assignSupplierUserToRole(WTObject primaryBusinessObject, String targetRole) {

		LOGGER.info("start - Inside CLASS--SmWorkflowHelper and METHOD--assignSupplierUserToRole");
		try {
			if (primaryBusinessObject instanceof LCSSample) {
				// Getting Sample Object.
				LCSSample sampleObj = (LCSSample) primaryBusinessObject;

				// Getting Sample Request Object.
				LCSSampleRequest sampleReqObj = sampleObj.getSampleRequest();

				// Getting SourceingConfig Master.
				LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) sampleReqObj.getSourcingMaster();

				// Getting Sourcing Config Object.
				LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);

				// String vendor = (String) sourceObj.getValue(attName);
				LCSSupplier vendorObj = (LCSSupplier) sourceObj.getValue(BUSINESS_SUPPLIER);

				// Getting Vendor Group from Supplier object.
				String vendorGroup = (String) vendorObj.getValue(SUPPLIER_VENDOR_GROUP);

				Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);

				Role role = wt.project.Role.toRole(targetRole);

				rolesEnum(team, role);

				if (FormatHelper.hasContent(vendorGroup)) {

					// Setting String passed to WT Group.
					@SuppressWarnings("deprecation")
					WTGroup group = (WTGroup) OrganizationServicesHelper.manager.getPrincipal(vendorGroup);

					// Adding Role to Group.
					@SuppressWarnings("unchecked")
					Enumeration<WTGroup> enumMembers = group.members();
					while (enumMembers.hasMoreElements()) {
						team.addPrincipal(role, enumMembers.nextElement());
					}

					team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
					TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
					TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,
							targetTeamReference);
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in assignSupplierUserToRole method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SmWorkflowHelper and METHOD--assignSupplierUserToRole");
	}

	/**
	 * assignSupplierDeveloperUserGroupToRole.
	 * 
	 * @param primaryBusinessObject for primaryBusinessObject.
	 * @param targetRole            for targetRole.
	 * @param attName               for attName.
	 * @return void.
	 */
	public static void assignSupplierDeveloperUserGroupToRole(WTObject primaryBusinessObject, String targetRole,
			String attName) {

		LOGGER.info("start - Inside CLASS--SmWorkflowHelper and METHOD--assignSupplierDeveloperUserGroupToRole");
		try {
			// This functionality is called from Sportmaster FPD Product Sample Accept
			// Sample Workflow and Sportmaster FPD Product Sample Ship Sample Workflow.

			Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);

			Role role = wt.project.Role.toRole(targetRole);

			assignSupplierUserToRole(primaryBusinessObject, targetRole);

			if (primaryBusinessObject instanceof LCSSample) {

				// Getting Sample Object.
				LCSSample sampleObj = (LCSSample) primaryBusinessObject;

				// Getting Sample Request Object.
				LCSSampleRequest sampleRequestObject = sampleObj.getSampleRequest();

				// Getting Season Object.
				LCSSeason seasonObj = (LCSSeason) sampleRequestObject.getValue(SEASON_REQUESTED);

				if (seasonObj != null && sampleObj.getOwnerMaster() instanceof LCSPartMaster) {

					// Getting Part Master Object.
					LCSPartMaster prodMaster = (LCSPartMaster) sampleObj.getOwnerMaster();

					// Getting Product Object.
					LCSProduct prodObj = VersionHelper.latestIterationOf(prodMaster);

					// Calling teamAssignmentAttribute() to get attribute value from Product Season.
					String psAttName = teamAssignmentAttributeForSampleWF(attName, prodObj, seasonObj);

					if (FormatHelper.hasContent(psAttName)) {
						// Setting String passed to WT User.
						@SuppressWarnings("deprecation")
						WTUser user = (WTUser) OrganizationServicesHelper.manager.getPrincipal(psAttName);

						// Adding User to Role.
						team.addPrincipal(role, user);
					}

					team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
					TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
					TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,
							targetTeamReference);
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in assignSupplierDeveloperUserGroupToRole method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SmWorkflowHelper and METHOD--assignSupplierDeveloperUserGroupToRole");
	}

	/**
	 * teamAssignmentAttributeForSampleWF.
	 * 
	 * @param attName for attName.
	 * @param product for product.
	 * @param season  for season.
	 * @return String.
	 */
	public static String teamAssignmentAttributeForSampleWF(String attName, LCSProduct product, LCSSeason season) {

		String result = "";
		try {
			// Getting season product link
			LCSSeasonProductLink spLink = LCSSeasonQuery.findSeasonProductLink(product, season);
			if (spLink != null) {
				FlexObject fo = (FlexObject) spLink.getValue(attName);
				if (fo != null && fo.containsKey(FO_NAME) && FormatHelper.hasContent((String) fo.getData(FO_NAME))) {
					result = (String) fo.getData(FO_NAME);
					LOGGER.debug("\n Att Value====" + fo.getData(FO_NAME));
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in teamAssignmentAttributeForSampleWF method: " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	// Added for Phase 13 -SEPD Product Sample WF - Start
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
					LOGGER.debug("\n Attribute  Value=" + fo.getData(FO_NAME));
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

	/**
	 * getOSODeveloperAttributeBasedOnSourceType.
	 * 
	 * @param sourceObj          for sourceObj.
	 * @param sourceToSeasonLink for sourceToSeasonLink.
	 * @return String.
	 */
	public static String getOSODeveloperAttributeBasedOnSourceType(LCSSourcingConfig sourceObj,
			LCSSourceToSeasonLink sourceToSeasonLink) {

		LOGGER.info("start - Inside CLASS--SmWorkflowHelper and METHOD--getOSODeveloperAttributeBasedOnSourceType");
		String osoDeveloper = null;

		if (sourceObj.getFlexType().getFullName().startsWith(APPAREL_SOURCE_TYPE)) {
			// Getting OSO Developer value.
			osoDeveloper = teamAssignmentOSODeveloperAttributeForSampleWF(OSO_DEVELOPER_APPAREL, sourceToSeasonLink);
		} else if (sourceObj.getFlexType().getFullName().startsWith(SEPD_SOURCE_TYPE)) {
			// Getting OSO Developer value.
			osoDeveloper = teamAssignmentOSODeveloperAttributeForSampleWF(OSO_DEVELOPER_SEPD, sourceToSeasonLink);
		}
		LOGGER.debug("In getOSODeveloperAttributeBasedOnSourceType method ---- osoDeveloper -----" + osoDeveloper);
		LOGGER.info("end - Inside CLASS--SmWorkflowHelper and METHOD--getOSODeveloperAttributeBasedOnSourceType");
		return osoDeveloper;
	}

	/**
	 * assignSourceOSODeveloperUserToRole.
	 * 
	 * @param primaryBusinessObject for primaryBusinessObject.
	 * @param targetRole            for targetRole.
	 * @return void.
	 */
	public static void assignSourceOSODeveloperUserToRole(WTObject primaryBusinessObject, String targetRole) {

		LOGGER.info("start - Inside CLASS--SmWorkflowHelper and METHOD--assignSourceOSODeveloperUserToRole");
		try {
			if (primaryBusinessObject instanceof LCSSample) {
				// This functionality is called from Sportmaster SEPD Product Sample
				// Workflows.

				// Getting Sample Object.
				LCSSample sampleObj = (LCSSample) primaryBusinessObject;

				// Getting Sample Request Object from sample object.
				LCSSampleRequest sampleReqObj = sampleObj.getSampleRequest();

				// Getting SourceingConfig Master from Sample Request object.
				LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) sampleReqObj.getSourcingMaster();

				// Getting Sourcing Config Object from source master.
				LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);

				// Getting Season object from season requested attribute.
				LCSSeason season = (LCSSeason) sampleReqObj.getValue(SEPD_SEASON_REQUESTED);

				// Getting Source to season link.
				LCSSourceToSeasonLink sourceToSeasonLink = new LCSSourcingConfigQuery().getSourceToSeasonLink(sourceObj,
						season);

				String osoDeveloper = null;

				LOGGER.debug("In assignSourceOSODeveloperUserToRole method - Source TYPE "
						+ sourceObj.getFlexType().getFullName());

				// Calling method to get OSO Developer attribute value based on Source object
				// type.
				osoDeveloper = getOSODeveloperAttributeBasedOnSourceType(sourceObj, sourceToSeasonLink);

				LOGGER.debug("In assignSourceOSODeveloperUserToRole method - osoDeveloper " + osoDeveloper);

				Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);

				Role role = wt.project.Role.toRole(targetRole);

				// Enter this loop only when OSO developer attribute has value.
				if (FormatHelper.hasContent(osoDeveloper)) {

					// Assigning role to team.
					rolesEnum(team, role);

					// Setting String passed to WT User.
					@SuppressWarnings("deprecation")
					WTUser user = (WTUser) OrganizationServicesHelper.manager.getPrincipal(osoDeveloper);

					// Adding User to Role.
					team.addPrincipal(role, user);

					team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
					TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
					TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,
							targetTeamReference);
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in assignSourceOSODeveloperUserToRole method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SmWorkflowHelper and METHOD--assignSourceOSODeveloperUserToRole");
	}

	/**
	 * assignSEPDSupplierOrDeveloperUserGroupToRole.
	 * 
	 * @param primaryBusinessObject for primaryBusinessObject.
	 * @param targetRoleSupplier    for targetRoleSupplier.
	 * @param targetRoleDeveloper   for targetRoleDeveloper.
	 * @return void.
	 */
	public static void assignSEPDSupplierOrDeveloperUserGroupToRole(WTObject primaryBusinessObject,
			String targetRoleSupplier, String targetRoleDeveloper) throws WTException {

		LOGGER.info("start - Inside CLASS--SmWorkflowHelper and METHOD--assignSEPDSupplierOrDeveloperUserGroupToRole");
		try {
			if (primaryBusinessObject instanceof LCSSample) {
				// This functionality is called from Sportmaster SEPD Product Sample
				// Workflows.

				// Getting Sample Object.
				LCSSample sampleObj = (LCSSample) primaryBusinessObject;

				// Getting Sample Request Object.
				LCSSampleRequest sampleReqObj = sampleObj.getSampleRequest();
				LOGGER.debug(" In assignSEPDSupplierOrDeveloperUserGroupToRole method ---- sampleReqObj -----"
						+ sampleReqObj);

				// Getting Season.
				LCSSeason season = (LCSSeason) sampleReqObj.getValue(SEPD_SEASON_REQUESTED);
				LOGGER.debug("In assignSEPDSupplierOrDeveloperUserGroupToRole Method ---- SEASON -----" + season);

				// Getting SourceingConfig Master.
				LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) sampleReqObj.getSourcingMaster();

				// Getting Sourcing Config Object.
				LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);

				// Getting Vendor object.
				LCSSupplier vendorObj = (LCSSupplier) sourceObj.getValue(BUSINESS_SUPPLIER);

				// Enters this loop only when vendor object has value.
				if (vendorObj != null) {
					assignVendorOrDeveloperGroupToRole(primaryBusinessObject, targetRoleSupplier, targetRoleDeveloper,
							season, sourceObj, vendorObj);
				}
			}
		} catch (TeamException e) {
			LOGGER.error("TeamException in assignSEPDSupplierOrDeveloperUserGroupToRole method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SmWorkflowHelper and METHOD--assignSEPDSupplierOrDeveloperUserGroupToRole");
	}

	/**
	 * @param primaryBusinessObject
	 * @param targetRoleSupplier
	 * @param targetRoleDeveloper
	 * @param season
	 * @param sourceObj
	 * @param vendorObj
	 * @throws WTException
	 * @throws TeamException
	 * @throws ObjectNoLongerExistsException
	 * @throws LifeCycleException
	 */
	private static void assignVendorOrDeveloperGroupToRole(WTObject primaryBusinessObject, String targetRoleSupplier,
			String targetRoleDeveloper, LCSSeason season, LCSSourcingConfig sourceObj, LCSSupplier vendorObj)
			throws WTException, TeamException, ObjectNoLongerExistsException, LifeCycleException {
		// Getting Source to season link.
		LCSSourceToSeasonLink sourceToSeasonLink = new LCSSourcingConfigQuery().getSourceToSeasonLink(sourceObj,
				season);

		String osoDeveloper = "";
		boolean bVendorUserPresent = false;
		// Getting Vendor Group from Supplier object.
		String vendorGroup = (String) vendorObj.getValue(SUPPLIER_VENDOR_GROUP);

		LOGGER.debug("In assignSEPDSupplierOrDeveloperUserGroupToRole method - vendorGroup " + vendorGroup);
		LOGGER.debug("In assignSEPDSupplierOrDeveloperUserGroupToRole method - vendorGroup " + vendorGroup);
		// Getting teams
		Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);
		LOGGER.debug(" TEAM ====" + team);
		// Getting supplier role
		Role role = wt.project.Role.toRole(targetRoleSupplier);
		LOGGER.debug(" targetRoleSupplier ====" + targetRoleSupplier);
		LOGGER.debug(" role ====" + role);
		rolesEnum(team, role);
		
		// Getting Developer role
		Role developerRole = wt.project.Role.toRole(targetRoleDeveloper);
		LOGGER.debug(" targetRoleDeveloper ====" + targetRoleDeveloper);
		LOGGER.debug(" developerRole ====" + developerRole);
		rolesEnum(team, developerRole);
			
		if (FormatHelper.hasContent(vendorGroup)) {

			/*// Getting role
			Role role = wt.project.Role.toRole(targetRoleSupplier);
			LOGGER.debug(" targetRoleSupplier ====" + targetRoleSupplier);
			LOGGER.debug(" role ====" + role);

			rolesEnum(team, role);*/

			// Setting String passed to WT Group.
			@SuppressWarnings("deprecation")
			WTGroup group = (WTGroup) OrganizationServicesHelper.manager.getPrincipal(vendorGroup);
			LOGGER.debug(" group ====" + group);

			WTPrincipal wtPrincipal = null;
			if (group != null) {
				LOGGER.debug(" INSIDE LOOP group ====" + group.getName());
				// Adding Role to Group.
				@SuppressWarnings("unchecked")
				Enumeration<WTGroup> enumMembers = group.members();
				while (enumMembers.hasMoreElements()) {
					bVendorUserPresent = true;
					wtPrincipal = enumMembers.nextElement();
					LOGGER.debug(" wtPrincipal before====" + wtPrincipal);
					team.addPrincipal(role, wtPrincipal);
					LOGGER.debug(" wtPrincipal After====" + wtPrincipal);
				}
				LOGGER.debug(" bVendorUserPresent ====" + bVendorUserPresent);

				team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
				TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
				TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,
						targetTeamReference);
			}
		}
		if (!bVendorUserPresent) {
			LOGGER.debug(" NOT bVendorUserPresent ");
			assginDeveloperGroupToRole(primaryBusinessObject, sourceObj, sourceToSeasonLink, developerRole);
		}
	}

	/**
	 * @param primaryBusinessObject
	 * @param targetRoleDeveloper
	 * @param sourceObj
	 * @param sourceToSeasonLink
	 * @throws WTException
	 * @throws TeamException
	 * @throws ObjectNoLongerExistsException
	 * @throws LifeCycleException
	 */
	private static void assginDeveloperGroupToRole(WTObject primaryBusinessObject,
			LCSSourcingConfig sourceObj, LCSSourceToSeasonLink sourceToSeasonLink, Role role)
			throws WTException, TeamException, ObjectNoLongerExistsException, LifeCycleException {
		String osoDeveloper;
		// Calling this method to get OSO Developer attribute based on Source type.
		osoDeveloper = getOSODeveloperAttributeBasedOnSourceType(sourceObj, sourceToSeasonLink);

		Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);
		LOGGER.debug(" OSO team ====" + team);

		//Role role = wt.project.Role.toRole(targetRoleDeveloper);
		LOGGER.debug(" OSO role ====" + role);

		if (FormatHelper.hasContent(osoDeveloper)) {

			// Assign role to team.
			//rolesEnum(team, role);

			// Setting String passed to WT User.
			@SuppressWarnings("deprecation")
			WTUser user = (WTUser) OrganizationServicesHelper.manager.getPrincipal(osoDeveloper);
			LOGGER.debug(" OSO user ====" + user);

			// Adding User to Role.
			team.addPrincipal(role, user);

			// Refresh
			team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
			TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
			TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject, targetTeamReference);
		}
	}

	/**
	 * assignSEPDSampleRequestCreatorUserToRole.
	 * 
	 * @param primaryBusinessObject for primaryBusinessObject.
	 * @param targetRole            for targetRole.
	 * @return void.
	 */
	public static void assignSEPDSampleRequestCreatorUserToRole(WTObject primaryBusinessObject, String targetRole) {

		LOGGER.info("start - Inside CLASS--SmWorkflowHelper and METHOD--assignSEPDSampleRequestCreatorUserToRole");
		try {
			if (primaryBusinessObject instanceof LCSSample) {
				// This functionality is called from Sportmaster SEPD Product Sample Workflow.

				// Getting Sample Object.
				LCSSample sampleObj = (LCSSample) primaryBusinessObject;

				// Getting Sample Request Object.
				LCSSampleRequest sampleReqObj = sampleObj.getSampleRequest();

				// Get Team
				Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);

				// Get Role
				Role role = wt.project.Role.toRole(targetRole);

				// Getting sample Request creator
				WTPrincipalReference creatorSR = sampleReqObj.getCreator();
				LOGGER.debug("\n In assignSEPDSampleRequestCreatorUserToRole method -- creatorSR.getName()===="
						+ creatorSR.getName());

				// Role is assigned to team
				rolesEnum(team, role);

				// Getting user.
				WTUser user = (WTUser) creatorSR.getPrincipal();
				LOGGER.debug(
						"\n In assignSEPDSampleRequestCreatorUserToRole method -- user.getName()====" + user.getName());

				// Adding User to Role.
				team.addPrincipal(role, user);

				// Refreshed
				team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
				TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
				TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,
						targetTeamReference);
			}
		} catch (WTException e) {
			LOGGER.error("WTException in assignSEPDSampleRequestCreatorUserToRole method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SmWorkflowHelper and METHOD--assignSEPDSampleRequestCreatorUserToRole");
	}
	// Added for Phase 13 -SEPD Product Sample WF - End
	
	
	// Added for Phase 14 - SEPD Product Costsheet WF - Start
	public static Boolean checkOnlineOrOfflineVendor(WTObject primaryBusinessObject) {
		boolean bVendorUserPresent = false;
		try {
			if (primaryBusinessObject instanceof LCSCostSheet) {
				// This functionality is called from Sportmaster SEPD Product Costsheet
				// Workflows.

				// Getting Costsheet Object.
				LCSCostSheet csObj = (LCSCostSheet) primaryBusinessObject;

				// Getting SourcingConfig Master.
				LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) csObj.getSourcingConfigMaster();

				// Getting Sourcing Config Object.
				LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);

				// Getting Vendor object.
				LCSSupplier vendorObj = (LCSSupplier) sourceObj.getValue(BUSINESS_SUPPLIER);

				// Enters this loop only when vendor object has value.
				if (vendorObj != null) {
					bVendorUserPresent = isVendorUserPresent(vendorObj);
				}
				LOGGER.debug(" bVendorUserPresent==========" + bVendorUserPresent);
			}
		} catch (WTException e) {
			LOGGER.error("WTException in checkOnlineOrOfflineVendor method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SmWorkflowHelper and METHOD--assignSEPDSampleRequestCreatorUserToRole");
		return bVendorUserPresent;
	}

	private static boolean isVendorUserPresent(LCSSupplier vendorObj) throws WTException {
		boolean bVendorUserPresent = false;
		// Getting Vendor Group from Supplier object.
		String vendorGroup = (String) vendorObj.getValue(SUPPLIER_VENDOR_GROUP);
		if (FormatHelper.hasContent(vendorGroup)) {
			// bVendorUserPresent = true;
			WTGroup wtGroup = UserGroupHelper.getWTGroup(vendorGroup);
			LOGGER.debug("\n wtGroup==" + wtGroup);
			Enumeration enumer;
			WTPrincipalReference wtParentGroup;

			if (wtGroup != null) {
				enumer = wtGroup.parentGroups();
				while (enumer.hasMoreElements()) {
					wtParentGroup = (WTPrincipalReference) enumer.nextElement();
					LOGGER.debug(wtParentGroup.getName());
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
	 * assignCosterUserToRole.
	 * 
	 * @param primaryBusinessObject for primaryBusinessObject.
	 * @param targetRole            for targetRole.
	 * @return void.
	 */
	public static void assignCosterUserToRole(WTObject primaryBusinessObject, String targetRole) {

		LOGGER.info("start - Inside CLASS--SmWorkflowHelper and METHOD--assignCosterUserToRole");
		try {
			if (primaryBusinessObject instanceof LCSCostSheet) {
				// This functionality is called from Sportmaster SEPD Product Costsheet
				// Workflows.

				// Getting Costsheet Object.
				LCSCostSheet csObj = (LCSCostSheet) primaryBusinessObject;

				// Getting SourcingConfig Master from costsheet object.
				LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) csObj.getSourcingConfigMaster();

				// Getting Sourcing Config Object from source master.
				LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);

				// Getting season object
				LCSSeason seasonObj = (LCSSeason) VersionHelper.latestIterationOf(csObj.getSeasonMaster());

				// Getting Source to season link.
				LCSSourceToSeasonLink sourceToSeasonLink = new LCSSourcingConfigQuery().getSourceToSeasonLink(sourceObj,
						seasonObj);

				String coster = null;

				LOGGER.debug(
						"In assignCosterUserToRole method - Source TYPE " + sourceObj.getFlexType().getFullName());

				// Calling method to get Coster attribute value based on Source object
				// type.
				coster = getCosterAttributeBasedOnSourceType(sourceObj, sourceToSeasonLink);

				LOGGER.debug("In assignCosterUserToRole method - coster " + coster);

				Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);

				Role role = wt.project.Role.toRole(targetRole);

				// Enter this loop only when Coster attribute has value.
				if (FormatHelper.hasContent(coster)) {

					// Assigning role to team.
					rolesEnum(team, role);

					// Setting String passed to WT User.
					@SuppressWarnings("deprecation")
					WTUser user = (WTUser) OrganizationServicesHelper.manager.getPrincipal(coster);

					// Adding User to Role.
					team.addPrincipal(role, user);

					team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
					TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
					TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,
							targetTeamReference);
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in assignCosterUserToRole method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SmWorkflowHelper and METHOD--assignCosterUserToRole");
	}

	/**
	 * getCosterAttributeBasedOnSourceType.
	 * 
	 * @param sourceObj          for sourceObj.
	 * @param sourceToSeasonLink for sourceToSeasonLink.
	 * @return String.
	 */
	public static String getCosterAttributeBasedOnSourceType(LCSSourcingConfig sourceObj,
			LCSSourceToSeasonLink sourceToSeasonLink) {

		LOGGER.info("start - Inside CLASS--SmWorkflowHelper and METHOD--getCosterAttributeBasedOnSourceType");
		String coster = null;

		if (sourceObj.getFlexType().getFullName().startsWith(APPAREL_SOURCE_TYPE)) {
			// Getting Apparel coster value.
			coster = teamAssignmentSourceToSeasonAttribute("smOSOAppCoster", sourceToSeasonLink);
		} else if (sourceObj.getFlexType().getFullName().startsWith(SEPD_SOURCE_TYPE)) {
			// Getting SEPD coster value.
			coster = teamAssignmentSourceToSeasonAttribute("smOsoCoster", sourceToSeasonLink);
		}
		LOGGER.debug("In getCosterAttributeBasedOnSourceType method ---- coster -----" + coster);
		LOGGER.info("end - Inside CLASS--SmWorkflowHelper and METHOD--getCosterAttributeBasedOnSourceType");
		return coster;
	}

	/**
	 * teamAssignmentSourceToSeasonAttribute.
	 * 
	 * @param attName            for attName.
	 * @param sourceToSeasonLink for sourceToSeasonLink.
	 * @return String.
	 */
	public static String teamAssignmentSourceToSeasonAttribute(String attName,
			LCSSourceToSeasonLink sourceToSeasonLink) {

		String result = "";
		try {
			if (sourceToSeasonLink != null) {
				FlexObject fo = (FlexObject) sourceToSeasonLink.getValue(attName);
				if (fo != null && fo.containsKey(FO_NAME) && FormatHelper.hasContent((String) fo.getData(FO_NAME))) {
					result = (String) fo.getData(FO_NAME);
					LOGGER.debug("\n Attribute  Value=" + fo.getData(FO_NAME));
				}
			} else {
				result = "";
			}
		} catch (WTException e) {
			LOGGER.error("WTException in teamAssignmentSourceToSeasonAttribute method: " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param primaryBusinessObject
	 * @param targetRole
	 * @param attName
	 * @throws WTException
	 */
	public static void assignUserToRoleForCSObj(WTObject primaryBusinessObject, String targetRole, String attName)
			throws WTException {
		// This functionality is called from Sportmaster SEPD Product Costsheet
		// Workflows.
		if (primaryBusinessObject instanceof LCSCostSheet) {
			// Getting Costsheet Object.
			LCSCostSheet csObj = (LCSCostSheet) primaryBusinessObject;

			// Getting season object
			LCSSeason seasonObj = (LCSSeason) VersionHelper.latestIterationOf(csObj.getSeasonMaster());
			LOGGER.debug("\n seasonObj == " + seasonObj);

			if (seasonObj != null) {

				// Getting Product RevA
				LCSProduct productRevA = (LCSProduct) VersionHelper.getVersion(csObj.getProductMaster(), "A");

				// Getting Product Season link.
				LCSSeasonProductLink spLink = LCSSeasonQuery.findSeasonProductLink(productRevA, seasonObj);
				LOGGER.debug("\n spLink == " + spLink);
				String psAttName = "";

				if (spLink != null && !(spLink.isSeasonRemoved())) {
					FlexObject fo = (FlexObject) spLink.getValue(attName);
					if (fo != null && fo.containsKey(FO_NAME)
							&& FormatHelper.hasContent((String) fo.getData(FO_NAME))) {
						psAttName = (String) fo.getData(FO_NAME);
						LOGGER.debug("\n Att Value====" + fo.getData(FO_NAME));
					}
				}

				Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);

				Role role = wt.project.Role.toRole(targetRole);

				if (FormatHelper.hasContent(psAttName)) {

					rolesEnum(team, role);

					// Setting String passed to WT User.
					@SuppressWarnings("deprecation")
					WTUser user = (WTUser) OrganizationServicesHelper.manager.getPrincipal(psAttName);

					// Adding User to Role.
					team.addPrincipal(role, user);

					team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
					TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
					TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,
							targetTeamReference);
				}
			}
		}
	}

	/**
	 * assignSupplierGroupToRole.
	 * 
	 * @param primaryBusinessObject for primaryBusinessObject.
	 * @param targetRole            for targetRole.
	 * @return void.
	 */
	public static void assignSupplierGroupToRole(WTObject primaryBusinessObject, String targetRole) {

		LOGGER.info("start - Inside CLASS--SmWorkflowHelper and METHOD--assignSupplierGroupToRole");
		try {
			// This functionality is called from Sportmaster SEPD Product Costsheet
			// Workflows.
			if (primaryBusinessObject instanceof LCSCostSheet) {
				// Getting Costsheet Object.
				LCSCostSheet csObj = (LCSCostSheet) primaryBusinessObject;

				// Getting SourcingConfig Master from costsheet object.
				LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) csObj.getSourcingConfigMaster();

				// Getting Sourcing Config Object.
				LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);

				// String vendor = (String) sourceObj.getValue(attName);
				LCSSupplier vendorObj = (LCSSupplier) sourceObj.getValue(BUSINESS_SUPPLIER);
				String vendorGroup = "";
				if (vendorObj != null) {
					// Getting Vendor Group from Supplier object.
					vendorGroup = (String) vendorObj.getValue(SUPPLIER_VENDOR_GROUP);
				}

				Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);
				Role role = wt.project.Role.toRole(targetRole);
				rolesEnum(team, role);

				if (FormatHelper.hasContent(vendorGroup)) {
					// Setting String passed to WT Group.
					@SuppressWarnings("deprecation")
					WTGroup wtGroup = (WTGroup) OrganizationServicesHelper.manager.getPrincipal(vendorGroup);

					// Adding Role to Group.
					@SuppressWarnings("unchecked")
					Enumeration<WTGroup> enumMembers = wtGroup.members();
					while (enumMembers.hasMoreElements()) {
						team.addPrincipal(role, enumMembers.nextElement());
					}
					LOGGER.debug("team="+team);
					team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
					TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
					TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,
							targetTeamReference);
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in assignSupplierGroupToRole method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SmWorkflowHelper and METHOD--assignSupplierGroupToRole");
	}
	// Added for Phase 14 - SEPD Product Costsheet WF - END


	//Added for Phase 14 - Product Specification WF - START
	/**
	 * @param primaryBusinessObject
	 * @param targetRole
	 * @param attName
	 * @throws WTException 
	 */
	public static void assignTechnologistUserToRole(WTObject primaryBusinessObject, String targetRole, String attName) throws WTException{
		// This functionality is called from Sportmaster Product Specification Workflow Task 1 to assign Technologist User     
		LOGGER.debug("start - Inside CLASS--SmWorkflowHelper and METHOD--assignTechnologistUserToRole");
		try {
			if (primaryBusinessObject instanceof FlexSpecification) {
				// Getting Specification Object.
				FlexSpecification specObj = (FlexSpecification) primaryBusinessObject;
	
				// Getting season object
				LCSProduct prodObj = (com.lcs.wc.product.LCSProduct) VersionHelper
						.latestIterationOf(specObj.getSpecOwner());				
				LCSProduct productRevA = (LCSProduct) VersionHelper.getVersion(prodObj, "A");
				LCSSeason seasonObj = (LCSSeason) specObj.getValue(SEASONREFERENCE);
				LCSProductSeasonLink psLink = (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(productRevA, seasonObj);
				LCSProduct prodRev = SeasonProductLocator.getProductSeasonRev(psLink);
				
				LOGGER.debug("\n prodRev == " + prodRev);
				
				Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);
				LOGGER.debug("\n team == " + team);

				Role role = wt.project.Role.toRole(targetRole);
				LOGGER.debug("\n role == " + role);

				// Calling teamAssignmentAttribute() to get attribute value from Product Season.
				String psAttName = teamAssignmentAttribute(attName, prodRev);
				LOGGER.debug("\n psatt == " + psAttName);

				if (FormatHelper.hasContent(psAttName)) {
					rolesEnum(team, role);

					// Setting String passed to WT User.
					@SuppressWarnings("deprecation")
					WTUser user = (WTUser) OrganizationServicesHelper.manager.getPrincipal(psAttName);
					LOGGER.debug("\n wt user== "+user);

					// Adding User to Role.
					team.addPrincipal(role, user);
					team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
					LOGGER.debug("\n wtteam == "+team);
					
					TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
					LOGGER.debug("\n wt targetTeamReference = "+targetTeamReference);
					
					TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,targetTeamReference);
				}			

				}
			}catch (WTException e) {
				LOGGER.error("WTException in assignTechnologistUserToRole method: " + e.getMessage());
				e.printStackTrace();
			}
		LOGGER.debug("end - Inside CLASS--SmWorkflowHelper and METHOD--assignTechnologistUserToRole");
	}
	
	/**
	 * assignSupplierUserToRoleForSpecObj
	 * 
	 * @param primaryBusinessObject for primaryBusinessObject.
	 * @param targetRole            for targetRole.
	 * @return void.
	 * @throws WTException 
	 */
	public static void assignSupplierUserToRoleForSpecObj(WTObject primaryBusinessObject, String targetRole)throws WTException{

		LOGGER.info("start - Inside CLASS--SmWorkflowHelper and METHOD--assignSupplierUserToRoleForSpecObj");
		try {
			if (primaryBusinessObject instanceof FlexSpecification) {
				// Getting Spec Object. 
				FlexSpecification specObj = (FlexSpecification) primaryBusinessObject;

				// Getting SourceingConfig Master.
				LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) specObj.getSpecSource();
				LOGGER.debug("\n wtsourceMaster == "+sourceMaster);

				// Getting Sourcing Config Object.
				LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);
				LOGGER.debug("\n wtsourceobj == "+sourceObj);

				
				
				
				/*// String vendor = (String) sourceObj.getValue(attName);
				LCSSupplier vendorObj = (LCSSupplier) sourceObj.getValue(BUSINESS_SUPPLIER);
				LOGGER.debug("\n wtvendorobj === "+vendorObj);

				// Getting Vendor Group from Supplier object.
				String vendorGroup = (String) vendorObj.getValue(SUPPLIER_VENDOR_GROUP);
				LOGGER.debug("\n wtvendorgroup == "+vendorGroup);*/

				Team team = TeamHelper.service.getTeam((TeamManaged) primaryBusinessObject);
				LOGGER.debug("Team calue === "+team);

				Role role = wt.project.Role.toRole(targetRole);
				LOGGER.debug("\n WTRole == "+role);

				rolesEnum(team, role);
				WTGroup group = SMProductSpecificationWorkflowPlugin.getVendorGroup(sourceObj);
			//	if (FormatHelper.hasContent(vendorGroup)) {

					// Setting String passed to WT Group.
					//WTGroup group = (WTGroup) OrganizationServicesHelper.manager.getPrincipal(vendorGroup);
					LOGGER.debug("\n wtgroup = "+group);
					
					if(group!=null){					
						// Adding Role to Group.
						@SuppressWarnings("unchecked")
						Enumeration<WTGroup> enumGroupMembers = group.members();
						LOGGER.debug("\n enumGroupMembers = "+enumGroupMembers);
						while (enumGroupMembers.hasMoreElements()) {
							team.addPrincipal(role, enumGroupMembers.nextElement());
						}
						team = (wt.team.Team) wt.fc.PersistenceHelper.manager.refresh(team);
						LOGGER.debug("\n wt team == "+team);
						
						TeamReference targetTeamReference = wt.team.TeamReference.newTeamReference(team);
						LOGGER.debug("\n wt targetTeamReference=== "+targetTeamReference);
						
						TeamHelper.service.augmentRoles((wt.lifecycle.LifeCycleManaged) primaryBusinessObject,
								targetTeamReference);
					}
			//	}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in assignSupplierUserToRole method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SmWorkflowHelper and METHOD--assignSupplierUserToRole");
	}
	
	//Added for Phase 14 - Product Specification WF - END
}