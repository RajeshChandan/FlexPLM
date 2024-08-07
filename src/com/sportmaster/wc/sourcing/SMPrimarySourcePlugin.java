package com.sportmaster.wc.sourcing;

import java.util.Collection;


import org.apache.log4j.Logger;

import wt.fc.WTObject;
import wt.fc.PersistenceServerHelper;
import wt.method.MethodContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;



/**
 * SMPrimarySourcePlugin.java
 * This class has methods for setting visual indicaotr "*" on primary source.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMPrimarySourcePlugin {

	/**
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMPrimarySourcePlugin.class);
	/**
	 * constructor.
	 */
	protected SMPrimarySourcePlugin() {
	}


	/**
	 * PRIMARY_SORUCE_VISUAL_INDICATOR.
	 */
	public static final String PRIMARY_SORUCE_VISUAL_INDICATOR =LCSProperties.get("com.sportmaster.wc.sourcing.SMPrimarySourcePlugin.primaryVisualIndicator");
	/**
	 * SM_PRIMARY_SOURCE_FLAG.
	 */
	private static final String SM_PRIMARY_SOURCE_FLAG = LCSProperties.get("com.sportmaster.wc.sourcing.SMPrimarySourcePlugin.primarySeasonFlag");

	/**
	 * Method removePrimaryIndicatorFromAllOtherSources - removePrimaryIndicatorFromAllOtherSources.
	 * @param oid the oid.
	 * @param obj the obj.
	 * @return boolean.
	 *	 */
	public static boolean setIndicatorForPrimarySourcetoSeason(String oid , WTObject obj) {
		LOGGER.debug("Inside Visual primary SourcingConfih Plugin");

		LCSSourceToSeasonLink sLink = null;
		try{
			//if sourcetoseasonlink object.
			if(obj instanceof LCSSourceToSeasonLink){
				//source  to season link object.
				sLink = (LCSSourceToSeasonLink) obj;

				//sourcing config master.
				LCSSourcingConfigMaster sourcingConfigMaster = sLink.getSourcingConfigMaster();
				//season master.
				LCSSeasonMaster seasonMaster = sLink.getSeasonMaster();
				//sourcing config.
				LCSSourcingConfig sConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourcingConfigMaster);
				//if soucing config not null.
				if(sConfig!=null){
					//sourcing config name.
					String sName  = PRIMARY_SORUCE_VISUAL_INDICATOR+sConfig.getValue("name");
					LOGGER.debug("Sourcing Config Name with Visal indicator"+sName);
					//part master.
					LCSPartMaster pMaster = sourcingConfigMaster.getProductMaster();

					// geting all sourcing config for product season.
					Collection<?> results;
					results = LCSSourcingConfigQuery.getSourcingConfigForProductSeason(pMaster, seasonMaster);


					//remove primary indicator from all other sources before setting for the current source season link.
					removePrimaryIndicatorFromAllOtherSources(results, sConfig,seasonMaster);
					persistSorcing(sConfig, sourcingConfigMaster,sLink, sName, true,false);
					LOGGER.debug("Saved >>>>>>>>>>>  "+sConfig.getSourcingConfigName());

				}
			}
		} catch (WTException e) {
			e.printStackTrace();
			LOGGER.error(e.getLocalizedMessage());
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
			LOGGER.error(e.getLocalizedMessage());
		}
		return true;
	}
	/**
	 * Method removePrimaryIndicatorFromAllOtherSources - To Remove all other source indicator when making new source to season link as primary.
	 * @param results - Collection
	 * @param sConfig - LCSSourcingConfig
	 * @param seasonMaster - LCSSeasonMaster
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 * @throws WTException - WTException
	 */
	private static void removePrimaryIndicatorFromAllOtherSources(
			Collection<?> results,LCSSourcingConfig sConfig,LCSSeasonMaster seasonMaster) throws WTPropertyVetoException, WTException {
		String sName;
		LCSSourcingConfigMaster sMaster;
		LCSSourceToSeasonLink sLink;
		//iterate through objects.
		for(Object s:results){
			//sourcing config.
			LCSSourcingConfig sCon = (LCSSourcingConfig) s;
			sCon=(LCSSourcingConfig) VersionHelper.latestIterationOf(sCon.getMaster());
			sMaster = (LCSSourcingConfigMaster) sCon.getMaster();
			//if sourcing config name contains the indicator.
			if(sCon.getSourcingConfigName().contains(PRIMARY_SORUCE_VISUAL_INDICATOR) && !sCon.equals(sConfig)){
				sName=sCon.getSourcingConfigName().replace(PRIMARY_SORUCE_VISUAL_INDICATOR, "");
				sLink=new LCSSourcingConfigQuery().getSourceToSeasonLink(sCon.getMaster(), seasonMaster);
				persistSorcing(sCon, sMaster,sLink,sName, false,false);
			}
		}
		LOGGER.debug("Reomve Primary indiactaor for all other sources done");

	}
	/**
	 * save sourcing configuration.
	 * @param sConfig - LCSSourcingConfig
	 * @param sMaster - LCSSourcingConfigMaster
	 * @param sLink - LCSSourceToSeasonLink
	 * @param sName - String
	 * @param flag - boolean
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	public static void persistSorcing(LCSSourcingConfig sConfig,LCSSourcingConfigMaster sMaster,LCSSourceToSeasonLink sLink,String sName, boolean flag,boolean useWcadmin) throws WTException, WTPropertyVetoException{
		LCSSourcingConfig sCon=sConfig;
		LOGGER.debug("Persisting Sourcing config start");
		sCon.setSourcingConfigName(sName);
		sCon.setValue("name", sName);
		//sLink.setPrimarySTSL(flag);
		sMaster.setSourcingConfigName(sName);

		sLink.setValue(SM_PRIMARY_SOURCE_FLAG, flag);

		//CR - SUGESTED BY sm Team :- don't want to see iteration on history tab
		PersistenceServerHelper.manager.update(sLink, false);
		PersistenceServerHelper.manager.update(sCon, false);
		PersistenceServerHelper.manager.update(sMaster, false);
		LOGGER.debug("Persisting Sourcing config end");
	}
	/**
	 * @Description : setIndicatorForDefaultSource - This method is to update the sourcing config name with indicator.
	 * @param obj the obj.
	 */
	public static void setIndicatorForDefaultSource(WTObject obj){

		LOGGER.debug("Source to Season Plugin starts");
		String dummy = "";
		LCSSourceToSeasonLink sLink = null;
		LCSSourcingConfigMaster sMaster = null;
		String sourcingName = null;
		LCSSourcingConfig sConfig = null;
		try {
			if(obj instanceof LCSSourceToSeasonLink){

				//source to season link
				sLink = (LCSSourceToSeasonLink) obj;
				//sourcing config master
				sMaster = sLink.getSourcingConfigMaster();
				//sourcing config name
				sourcingName = sMaster.getSourcingConfigName();
				LOGGER.debug("Default sourcing config name"+sourcingName);
				//get sourcing config.
				sConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf(sMaster);

			}else if(obj instanceof LCSSourcingConfig && FormatHelper.hasContent(String.valueOf(MethodContext.getContext().get("SM_SEASONLESS_SC"))) && !"No_Season".equalsIgnoreCase(dummy+ MethodContext.getContext().get("SM_SEASONLESS_SC")) ){

				sConfig=(LCSSourcingConfig) obj;
				sMaster = sConfig.getMaster();

				String oid = (String)MethodContext.getContext().get("SM_SEASONLESS_SC");
				
				
				
				LCSSeason season = (LCSSeason) LCSQuery.findObjectById(oid);

				sLink=new LCSSourcingConfigQuery().getSourceToSeasonLink(sConfig.getMaster(), season.getMaster());

				sourcingName = sMaster.getSourcingConfigName();
			}

			//if source to season link is primary, then change the name. 
			if(sLink != null && sLink.isPrimarySTSL() && FormatHelper.hasContent(sourcingName)&& !sourcingName.contains(PRIMARY_SORUCE_VISUAL_INDICATOR)){
				sourcingName=PRIMARY_SORUCE_VISUAL_INDICATOR+sourcingName;
				persistSorcing(sConfig, sMaster, sLink,sourcingName, true,false);
				LOGGER.debug("Added visual indicator on primary source");

			}
		} catch (WTException e) {
			e.printStackTrace();
			LOGGER.error(e.getLocalizedMessage());
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
			LOGGER.error(e.getLocalizedMessage());
		}
		LOGGER.debug("Source to Season Plugin ends");
	}


	/**
	 * add / remove visual indiacator from source name when a product is removed/added to a season. 
	 * @param obj - WTObject
	 */
	public static void removeIndicatorForDefaultSource(WTObject obj){
		LOGGER.debug("remove Indicator plugin starts");
		String sName;
		LCSSourcingConfig sConfig;
		LCSSourceToSeasonLink sstl;
		try {
			LCSSeasonProductLink seasonProductLink= (LCSSeasonProductLink) obj;
			LCSProduct prod=SeasonProductLocator.getProductSeasonRev(seasonProductLink);
			prod=(LCSProduct) VersionHelper.latestIterationOf(prod.getMaster());
			LCSPartMaster partMster=prod.getMaster();
			Collection<?> results;
			results = LCSSourcingConfigQuery.getSourcingConfigForProductSeason(partMster, seasonProductLink.getSeasonMaster());
			LOGGER.debug("Result for removing Default visal source"+results);
			if(seasonProductLink.isSeasonRemoved() && !FormatHelper.hasContent(String.valueOf(prod.getMovedFrom()))){
				for(Object object:results){
					sConfig= (LCSSourcingConfig) object;
					sConfig=(LCSSourcingConfig) VersionHelper.latestIterationOf(sConfig.getMaster());
					sstl=new LCSSourcingConfigQuery().getSourceToSeasonLink(sConfig.getMaster(), seasonProductLink.getSeasonMaster());
					sName=sConfig.getSourcingConfigName();
					if(sstl.isPrimarySTSL() && FormatHelper.hasContent(sName)&& sName.contains(PRIMARY_SORUCE_VISUAL_INDICATOR)){
						sName=sConfig.getSourcingConfigName().replace(PRIMARY_SORUCE_VISUAL_INDICATOR, "");
						persistSorcing(sConfig, sConfig.getMaster(),sstl, sName, false,false);
						LOGGER.debug("Visual indiactor removed from source name"+sConfig.getSourcingConfigName());
					}
				}
			}else{
				addVisulIndicator(results, seasonProductLink.getSeasonMaster());
			}
		} catch (WTException e) {
			e.printStackTrace();
			LOGGER.debug(e.getLocalizedMessage());
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
			LOGGER.debug(e.getLocalizedMessage());
		}

		LOGGER.debug("remove Indicator plugin ends");
	}

	/**
	 * adding visual indicator for primary source when a libary product is getting added to a season. 
	 * @param results - Collection
	 * @param seasonMaster - LCSSeason
	 * @throws WTPropertyVetoException -WTPropertyVetoException
	 * @throws WTException - WTException
	 */
	private static void addVisulIndicator(Collection<?> results,LCSSeasonMaster seasonMaster) throws WTPropertyVetoException, WTException{
		String sName;
		LCSSourcingConfig sConfig;
		LCSSourceToSeasonLink sstl;
		for(Object object:results){
			sConfig= (LCSSourcingConfig) object;
			sConfig=(LCSSourcingConfig) VersionHelper.latestIterationOf(sConfig.getMaster());
			sstl=new LCSSourcingConfigQuery().getSourceToSeasonLink(sConfig.getMaster(), seasonMaster);
			sName=sConfig.getSourcingConfigName();

			if(sstl.isPrimarySTSL() && FormatHelper.hasContent(sName)&& !sName.contains(PRIMARY_SORUCE_VISUAL_INDICATOR)){
				sName=PRIMARY_SORUCE_VISUAL_INDICATOR+sConfig.getSourcingConfigName();
				persistSorcing(sConfig, sConfig.getMaster(),sstl, sName, true,false);
				LOGGER.debug("Added a visual indiactor on sourece name"+sConfig.getSourcingConfigName());
			}
		}
	}

}
