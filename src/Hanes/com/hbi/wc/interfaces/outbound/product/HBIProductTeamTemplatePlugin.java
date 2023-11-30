package com.hbi.wc.interfaces.outbound.product;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.util.FlexContainerHelper;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

import wt.fc.WTObject;
import wt.team.Team;
import wt.team.TeamException;
import wt.team.TeamHelper;
import wt.team.TeamManaged;
import wt.team.TeamTemplate;

import wt.team.TeamTemplateReference;
import wt.util.WTException;

/**
 * @author UST
 * This will set team for the product based on certain attributes matching with business object MOA for TeamTemaplate.
 * If no match found, HBI - Default team is set.
 * When Team having corrupted user, then it will set Back Up team. Ensure to keep teams clean.
 */

public class HBIProductTeamTemplatePlugin {
	private static String businessObjectType = LCSProperties.get("com.hbi.wc.moa.HBISAPTeamTemplate.businessObjectType",
			"Business Object\\SAP Team Template");
	private static String businessObjectName = LCSProperties.get("com.hbi.wc.moa.HBISAPTeamTemplate.businessObjectName",
			"SAP Team Template");
	private static String hbiErpBrandGroupKey = LCSProperties
			.get("com.hbi.wc.moa.HBISAPTeamTemplate.hbiErpBrandGroupKey", "hbiErpBrandGroup");
	private static String hbiErpGenderKey = LCSProperties.get("com.hbi.wc.moa.HBISAPTeamTemplate.hbiErpGenderKey",
			"hbiErpGender");

	private static String hbiSAPProductGroupKey = LCSProperties
			.get("com.hbi.wc.moa.HBISAPTeamTemplate.hbiSAPProductGroupKey", "hbiSAPProductGroup");
	private static String hbiTeamTemplateKey = LCSProperties.get("com.hbi.wc.moa.HBISAPTeamTemplate.hbiTeamTemplateKey",
			"hbiTeamTemplate");
	/*private static String hbiIsExclusiveKey = LCSProperties.get("com.hbi.wc.moa.HBISAPTeamTemplate.hbiIsExclusiveKey",
			"hbiIsExclusive");*/
	private static String hbiOmniSelectionKey = LCSProperties
			.get("com.hbi.wc.moa.HBISAPTeamTemplate.hbiOmniSelectionKey", "hbiOmniSelection");
	private static String hbiSAPTeamTemplateKey = LCSProperties
			.get("com.hbi.wc.moa.HBISAPTeamTemplate.hbiSAPTeamTemplateKey", "hbiSAPTeamTemplate");
	private static String Exclusive_team_name = LCSProperties.get("com.hbi.wc.moa.HBISAPTeamTemplate.hbiExclusiveTeam",
			"hbiExclusiveTeam");
	private static String Exclusive_default_team_name = LCSProperties
			.get("com.hbi.wc.moa.HBISAPTeamTemplate.hbiExclusiveDefaultTeam", "hbiExclusiveDefaultTeam");
	private static String Non_Exclusive_default_team_name = LCSProperties
			.get("com.hbi.wc.moa.HBISAPTeamTemplate.hbiNonExclusiveDefaultTeam", "hbiNonExclusiveDefaultTeam");

	private static TeamTemplateReference getTeamTemplateReference(String teamName) {
		Vector teams;
		TeamTemplateReference teamReference = null;

		try {
			teams = TeamHelper.service.findTeamTemplates(FlexContainerHelper.getFlexContainer());
			TeamTemplate ttemp;
			TeamTemplateReference tref;
			for (int i = 0; i < teams.size(); i++) {
				tref = (TeamTemplateReference) teams.elementAt(i);
				ttemp = (TeamTemplate) tref.getObject();
				if (ttemp.isEnabled()) {
					if (tref.getName().equalsIgnoreCase(teamName)) {
						teamReference = tref;
					}
				}
			}
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return teamReference;
	}

	/**
	 * @param obj
	 * @throws TeamException
	 * @throws WTException
	 * @throws Exception
	 */
	/*public static void setTeam(WTObject obj) throws TeamException, WTException, Exception {
		LCSProduct product = (LCSProduct) obj;
		
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<Inside setTeam>>>>>>>>>>>>>>>>>>>>>>>>>" + product.getTeamName());
		String prodObjoid = com.lcs.wc.util.FormatHelper.getNumericVersionIdFromObject(product);
		wt.team.TeamManaged managed;
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<prodObjoid>>>>>>>>>>>>>>>>>>>>>>>>>" + prodObjoid);
		managed = (wt.team.TeamManaged) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:" + prodObjoid);
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<managed>>>>>>>>>>>>>>>>>>>>>>>>>" + managed.getTeamName());
		String teamName = getTeamTemplateForProduct(product);
		LCSLog.debug("<<<<<<<<<<<<<<<<<<<<<<<<<teamName>>>>>>>>>>>>>>>>>>>>>>>>>" + teamName);
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<setTeam teamName>>>>>>>>>>>>>>>>>>>>>>>>>" + teamName);
		if (FormatHelper.hasContent(teamName)) {
			if (!isSameTeam(managed, teamName)) {
				wt.team.TeamHelper.service.reteam(managed, getTeamTemplateReference(teamName));
			}
		}

		else {
			if (!isSameTeam(managed, "HBI - Backup Team")) {
				wt.team.TeamHelper.service.reteam(managed, getTeamTemplateReference("HBI - Backup Team"));
			}

		}

	}*/
	
	public static void setTeam(WTObject obj) throws TeamException, WTException, Exception {
		LCSProduct product = (LCSProduct) obj;
	//	System.out.println("product.getBranchIdentifier >>>>>>>>>>>> "+product.getBranchIdentifier());
	//	System.out.println("product.getFlexTypeIdPath >>>>>>>>>>>>>>> "+product.getFlexTypeIdPath());
		
	//	System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<Inside setTeam>>>>>>>>>>>>>>>>>>>>>>>>>" + product.getTeamName());
		String prodObjoid = com.lcs.wc.util.FormatHelper.getNumericVersionIdFromObject(product);
		wt.team.TeamManaged managed;
	//	System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<prodObjoid>>>>>>>>>>>>>>>>>>>>>>>>>" + prodObjoid);
		managed = (wt.team.TeamManaged) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:" + prodObjoid);
	//	System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<managed>>>>>>>>>>>>>>>>>>>>>>>>>" + managed.getTeamName());
		String teamName = getTeamTemplateForProduct(product);
		LCSLog.debug("<<<<<<<<<<<<<<<<<<<<<<<<<teamName>>>>>>>>>>>>>>>>>>>>>>>>>" + teamName);
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<teamName>>>>>>>>>>>>>>>>>>>>>>>>>" + teamName);
		if (FormatHelper.hasContent(teamName)) {
			if (!isSameTeam(managed, teamName)) {
				wt.team.TeamHelper.service.reteam(managed, getTeamTemplateReference(teamName));
			}
		}

		else {
			if (!isSameTeam(managed, "HBI - Backup Team")) {
				wt.team.TeamHelper.service.reteam(managed, getTeamTemplateReference("HBI - Backup Team"));
			}

		}

	}

	/**
	 * @param product
	 * @return
	 */
	private static String getTeamTemplateForProduct(LCSProduct product) {
		String teamName = null;
		String businessObjectType = "Business Object\\SAP Team Template";
		String businessObjectName = "SAP Team Template";
		String hbiErpBrandGroupKey = "hbiErpBrandGroup";
		String hbiErpGenderKey = "hbiErpGender";
		String hbiSAPProductGroupKey = "hbiSAPProductGroup";
		String hbiTeamTemplateKey = "hbiTeamTemplate";
		String hbiOmniSelectionKey = "hbiOmniSelection";
		String hbiSAPTeamTemplateKey = "hbiSAPTeamTemplate";
		String Exclusive_team_name = "hbiExclusiveTeam";
		String Exclusive_default_team_name = "hbiExclusiveDefaultTeam";
		String Non_Exclusive_default_team_name = "hbiNonExclusiveDefaultTeam";
		try {  
			LCSProduct prodObj = com.lcs.wc.season.SeasonProductLocator.getProductARev(product);
			//System.out.println(">>>>>>>>>>>>>>>getTeamTemplateForProduct prodobj"+prodObj.getValue(hbiErpGenderKey));
			String genderCategory = "";
			//if (prodObj.getValue(hbiErpGenderKey) != null) {
			if (prodObj.getValue(hbiErpGenderKey) != null) {
				genderCategory = (String) prodObj.getValue(hbiErpGenderKey);
				System.out.println("HBIProductTeamTemplatePlugin genderCategory "+genderCategory);
				// LCSLog.debug("HBIProductTeamTemplatePlugin genderCategory "+genderCategory);
			}

			String sapProductGroup = "";
			if (prodObj.getValue(hbiSAPProductGroupKey) != null) {
				sapProductGroup = (String) prodObj.getValue(hbiSAPProductGroupKey);
				// LCSLog.debug("HBIProductTeamTemplatePlugin sapProductGroup
				// "+sapProductGroup);
		//		System.out.println("HBIProductTeamTemplatePlugin sapProductGroup "+sapProductGroup);
			}

			String erpBrandGroup = "";
			if (prodObj.getValue(hbiErpBrandGroupKey) != null) {
				erpBrandGroup = (String) prodObj.getValue(hbiErpBrandGroupKey);
				// LCSLog.debug("HBIProductTeamTemplatePlugin erpBrandGroup "+erpBrandGroup);
			//	System.out.println("HBIProductTeamTemplatePlugin erpBrandGroup "+erpBrandGroup);
			}
			Boolean omniSelection = null;
			if (prodObj.getValue(hbiOmniSelectionKey) != null) {
						
				omniSelection = (Boolean) prodObj.getValue(hbiOmniSelectionKey);
				LCSLog.debug("HBIProductTeamTemplatePlugin hbiOmniSelection " + omniSelection);
				//System.out.println("HBIProductTeamTemplatePlugin hbiOmniSelection " + omniSelection);
			}
			LCSLifecycleManaged businessObject = new HBIInterfaceUtil().getLifecycleManagedByNameType("name",
					businessObjectName, businessObjectType);
			/*
			System.out.println(">>>>>>>>>>>>>businessObject toString"+businessObject.toString());
			System.out.println("------------------------------------------------------------------------");
			System.out.println(">>>>businessObject.getFlexTypeIdPath>>>>>>>>>>>>>>>"+businessObject.getFlexTypeIdPath());
			System.out.println(">>>>>>>>>>>>>>>>>>>>>businessObject.getTeamName>>>>>>>>>>>"+businessObject.getTeamName());
			*/
			System.out.println("------------------------------------------------------------------------");
			if (businessObjectType != null) {
				LCSMOATable sapteamMoa = (LCSMOATable) businessObject.getValue(hbiSAPTeamTemplateKey);
				Collection<FlexObject> teams1 = sapteamMoa.getRows();
				/*for(FlexObject flexObj : teams1)
				{
					System.out.println(">>>>>>>>>>>>>>>>Start>>>>>>>>>>>>>>>>.");
					System.out.println(flexObj.getString(hbiSAPProductGroupKey));
					System.out.println(flexObj.getString(hbiErpBrandGroupKey));
					System.out.println(flexObj.getString(hbiErpGenderKey));
					System.out.println(">>>>>>>>>>>>>>>End>>>>>>>>>>>>>>>>>");
			//		System.out.println(flexObj.getMOAString());
				}*/
				Map searchMap = new HashMap();
				searchMap.put(hbiErpGenderKey, genderCategory);
				searchMap.put(hbiErpBrandGroupKey, erpBrandGroup);
				searchMap.put(hbiSAPProductGroupKey, sapProductGroup);

				if (!"true".equals(omniSelection)) {

					Collection teams = sapteamMoa.getRows(searchMap);
				//	System.out.println(">>>>>>>>>>>>>>>>>>.teams size"+teams.size());
					// Iterator teamsItr=teams.iterator();
					FlexObject flexObj = null;
					if (teams.size() > 0) {
						flexObj = (FlexObject) teams.iterator().next();
					//	System.out.println(">>>>>>flexObj.getString(hbiTeamTemplateKey.toUpperCase())>>>>>>>> "+flexObj.getString(hbiTeamTemplateKey.toUpperCase()));
						teamName = flexObj.getString(hbiTeamTemplateKey.toUpperCase());
					}

					else {
					//	System.out.println(">>>>>>(String) businessObject.getValue(Non_Exclusive_default_team_name)>>>>>>>> "+(String) businessObject.getValue(Non_Exclusive_default_team_name));
						teamName = (String) businessObject.getValue(Non_Exclusive_default_team_name);

					}
				} else {
				//	System.out.println(">>>>>>businessObject.getValue(Exclusive_team_name)>>>>>>>> "+(String)businessObject.getValue(Exclusive_team_name));
					teamName = (String) businessObject.getValue(Exclusive_team_name);

					if (!FormatHelper.hasContent(teamName)) {

						teamName = (String) businessObject.getValue(Exclusive_default_team_name);
					}

				}
			} else {

				teamName = null;
			}
		} catch (WTException e) {
			e.printStackTrace();

		}
		return teamName;
	}

	/**
	 * @param obj
	 * @throws Exception
	 */
	public static void setDummyTeam(WTObject obj) throws Exception {
		//When no combination of team found in Business Object and also Default team having issues like corrupted user, then use this backup team
		LCSProduct product = (LCSProduct) obj;
		String teamName = "HBI - Backup Team";
		String prodObjoid = com.lcs.wc.util.FormatHelper.getNumericVersionIdFromObject(product);
		wt.team.TeamManaged managed;

		managed = (wt.team.TeamManaged) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:" + prodObjoid);
		if (!isSameTeam(managed, teamName)) {
			wt.team.TeamHelper.service.reteam(managed, getTeamTemplateReference(teamName));
		}

	}

	/**
	 * @param managed
	 * @param teamName
	 * @return
	 */
	private static boolean isSameTeam(TeamManaged managed, String teamName) {
		// TODO Auto-generated method stub
		boolean sameteam = false;

		Team currentTeam = null;
		TeamTemplate tt = null;
		String currentTeamName = "";
		try {
			currentTeam = wt.team.TeamHelper.service.getTeam(managed);

			tt = currentTeam.getTemplate();
		} catch (TeamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (tt != null) {
			currentTeamName = tt.getName();
		}
		if (teamName.equals(currentTeamName)) {

			sameteam = true;
			LCSLog.debug("---------------NEW and OLD Team are same hence not setting----------");
			System.out.println("---------------NEW and OLD Team are same hence not setting----------");
		}

		return sameteam;
	}
}
