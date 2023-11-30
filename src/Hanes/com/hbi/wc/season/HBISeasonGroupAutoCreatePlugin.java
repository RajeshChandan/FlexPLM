package com.hbi.wc.season;

import java.util.ArrayList;
import java.util.Collection;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonLogic;
import com.lcs.wc.season.SeasonGroup;
import com.lcs.wc.season.SeasonGroupHelper;
import com.lcs.wc.season.SeasonGroupMaster;
import org.apache.log4j.Logger;
import   wt.log4j.LogR;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;

/**
 * HBISeasonGroupAutoCreatePlugin.java
 * 
 * This class contains a plug-in function and generic functions which are invoking on season creation to configure SeasonGroup TypePath for the given Season object and SeasonGroup TypePath
 * Creating SeasonGroup object for the given SeasonGroupName, Season object(Owner of SeasonGroup), populating the default values of SeasonGroup object, persisting and returning SeasonGroup
 * @author Abdul.Patel@Hanes.com
 * @since June-08-2016
 */
public class HBISeasonGroupAutoCreatePlugin
{
	private static String gpOptionsNameKey = LCSProperties.get("com.hbi.wc.season.HBISeasonGroupAutoCreatePlugin.gpOptionsNameKey", "hbiOptionName");
	private static final Logger logger = LogR.getLogger("com.hbi.wc.season.HBISeasonGroupAutoCreatePlugin");
	/**
	 * This function is using as a plug-in function which is registered on LCSSeason POST_CREATE_PERSIST EVENT to configure pre-defined SeasonGroup Type and create one SeasonGroup object
	 * @param wtObj - WTObject
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void configureAndCreateSeasonGroup(WTObject wtObj) throws WTException, WTPropertyVetoException
	{
		logger.debug("### START HBISeasonGroupAutoCreatePlugin.configureAndCreateSeasonGroup(WTObject wtObj) ###");
		String seasonGroupFlexTypePath = LCSProperties.get("com.hbi.wc.season.HBISeasonGroupAutoCreatePlugin.seasonGroupFlexTypePath", "Season Group\\Garment Development Options");
		String seasonGroupObjectName = LCSProperties.get("com.hbi.wc.season.HBISeasonGroupAutoCreatePlugin.seasonGroupObjectName", "GP Options");
		
		//Validating the incoming WTObject and processing/returning based on the instance type of the WTObject (processing only if the instance is of type LCSSeason instance/Object
		if(!(wtObj instanceof LCSSeason))
		{
			logger.debug("Returning without performing any action as the incoming object is not an instance of LCSSeason, this plug-in is specific to LCSSeason POST_CREATE_PERSIST Event");
			return;
		}
		
		//Initializing Season object from the given WTObject and Validating the Season Object. Processing only if the Season is of Selling type as we need SeasonGroups only for Selling
		LCSSeason seasonObj = (LCSSeason) wtObj;
		if(!(seasonObj.getFlexType().getFullName(true)).contains("Season\\Selling"))
		{
			logger.debug("Returning without performing any action as the incoming Season Object is not a Selling Season, Configuring the SeasonGroups only for Selling Seasons");
			return;
		}
		
		//Format SeasonGroup Type OID from the given SeasonGroup TypePath and adding SeasonGroup Type OID to a Collection<String>, which is using as an input parameter to configure group
		Collection<String> seasonGroupFlexTypeOIDList = new ArrayList<String>();
		String seasonGroupFlexTypeOID = "OR:com.lcs.wc.flextype.FlexType:"+(FlexTypeCache.getFlexTypeFromPath(seasonGroupFlexTypePath)).getIdNumber();
		seasonGroupFlexTypeOIDList.add(seasonGroupFlexTypeOID);
		
		//Format SeasonGroupFlexTypeOID Collection<String> into an MOAString which is using as an SeasonGroup IDS and persisting the updated Season object to configure SeasonGroup Type
		String seasonGroupFlexTypeOIDMOAString = MOAHelper.toMOAString(seasonGroupFlexTypeOIDList);
		seasonObj.setSeasonGroupIds(seasonGroupFlexTypeOIDMOAString);
		seasonObj = (LCSSeason) LCSSeasonLogic.persist(seasonObj, true);
		
		//Calling a function which is using to initialize/create SeasonGroup Object with the given parameters (like Season Object(Owner), SeasonGroup Object Name and SeasonGroup TypePath)
		SeasonGroup seasonGroupObj = new HBISeasonGroupAutoCreatePlugin().createSeasonGroupObject(seasonObj, seasonGroupFlexTypePath, seasonGroupObjectName);
		logger.debug("HBISeasonGroupAutoCreatePlugin.configureAndCreateSeasonGroup(WTObject wtObj) :: Newly Created SeasonGroup Object = "+ seasonGroupObj);
		
		logger.debug("### END HBISeasonGroupAutoCreatePlugin.configureAndCreateSeasonGroup(WTObject wtObj) ###");
	}
	
	/**
	 * This function is using to configure and create SeasonGroup object for the given parameters SeasonGroup Object Name, SeasonGroup FlexType Path & Season Object (Owner of SeasonGroup)
	 * @param seasonObj - LCSSeason
	 * @param seasonGroupFlexTypePath - String
	 * @param seasonGroupObjectName - String
	 * @return seasonGroupObj - SeasonGroup
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public SeasonGroup createSeasonGroupObject(LCSSeason seasonObj, String seasonGroupFlexTypePath, String seasonGroupObjectName) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBISeasonGroupAutoCreatePlugin.createSeasonGroupObject(LCSSeason seasonObj, String seasonGroupFlexTypePath, String seasonGroupObjectName) ###");
		FlexType seasonGroupFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(seasonGroupFlexTypePath);
		
		//Initializing a new SeasonGroup Instance/Object and update the necessary parameters needed as per the OOTB design of the SeasonGroups (like SeasonGroup Type, SeasonGroup Owner)
		SeasonGroup seasonGroupObj = SeasonGroup.newSeasonGroup();
		seasonGroupObj.setFlexType(seasonGroupFlexTypeObj);
		seasonGroupObj.setMaster(new SeasonGroupMaster());
		seasonGroupObj.setSeasonMaster( seasonObj.getMaster());
		
		//Updating SeasonGroup object with the specific attributes data(like SeasonGroup Object Name) and persisting the initiated/created SeasonGroup object and return from the header
		seasonGroupObj.setValue(gpOptionsNameKey, seasonGroupObjectName);
		seasonGroupObj = SeasonGroupHelper.service.saveSeasonGroup(seasonGroupObj);
		
		// LCSLog.debug("### END HBISeasonGroupAutoCreatePlugin.createSeasonGroupObject(LCSSeason seasonObj, String seasonGroupFlexTypePath, String seasonGroupObjectName) ###");
		return seasonGroupObj;
	}
}