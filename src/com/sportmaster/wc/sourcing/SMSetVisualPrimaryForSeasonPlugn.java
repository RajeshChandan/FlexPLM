package com.sportmaster.wc.sourcing;

import java.util.Collection;

import org.apache.log4j.Logger;

import wt.fc.PersistenceServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductClientModel;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeasonProductLinkClientModel;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.util.FormatHelper;

/**
 * SMSetVisualPrimaryForSeasonPlugn.java
 * This class has methods for setting visual indicator "*" on primary source according to season selected.
 * for Integration.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMSetVisualPrimaryForSeasonPlugn {

	/**
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMSetVisualPrimaryForSeasonPlugn.class);
	/**
	 * ERROR_OCCURED.
	 */
	private static final String ERROR_OCCURED = "ERROR OCCURED : ";
	/**
	 * constructor.
	 */
	protected SMSetVisualPrimaryForSeasonPlugn() {
		
	}

	/** 
	 * set visual indicator according to season/prodcut.
	 * @param oid - String
	 * @return boolean
	 */
	public static boolean setVisualIndicatorToseaon(String oid){
		LOGGER.debug("SM PLUGIN :- VISUAL PRIMARY FOR SEASON STARTS");
		LOGGER.debug("oid  ->  "+oid);
		LCSProductSeasonLink prodSeasonLink;
		LCSProduct prod;
		Collection<?> results;
		LCSProductClientModel prodModel=new LCSProductClientModel();
		LCSSeasonProductLinkClientModel prodSeasonLinkModel=new LCSSeasonProductLinkClientModel();
		try {
			//if instance of product season
			if(oid!=null && oid.indexOf("LCSProductSeasonLink")>-1){
				//create product season object
				prodSeasonLinkModel.load(oid);
				//get product season from client model
				prodSeasonLink=(LCSProductSeasonLink) prodSeasonLinkModel.getBusinessObject();
				LOGGER.debug("selected season is : "+prodSeasonLink.getSeasonMaster().getName());
				//get product from product season
				prod=SeasonProductLocator.getProductSeasonRev(prodSeasonLink);
				// geting all sourcing config for product season.
				results = LCSSourcingConfigQuery.getSourcingConfigForProductSeason(prod.getMaster(), prodSeasonLink.getSeasonMaster());
				LOGGER.debug("total no of sorce attached to season : "+results.size());
				//product season source data
				updateSourceNmaeToSeason(results, prodSeasonLink);
		
			//if instance of product	
			}else if(oid!=null && oid.indexOf("LCSProduct:")>-1){
				prodModel.load(oid);
				prod=prodModel.getBusinessObject();
				LOGGER.debug("Prod >>>>>>>>>>  "+prod);
				//if product has season
				if(prod.getSeasonMaster()!=null){
					//LOGGER.debug("inside if !!!!!!");
					//getting product season link
					prodSeasonLink=(LCSProductSeasonLink) SeasonProductLocator.getSeasonProductLink(prod);
					//getting season product sourcing list
					results = LCSSourcingConfigQuery.getSourcingConfigForProductSeason(prod.getMaster(), prodSeasonLink.getSeasonMaster());
					updateSourceNmaeToSeason(results, prodSeasonLink);
					return true;
				}
				
				results=LCSSourcingConfigQuery.getSourcingConfigsForProduct(prod);
				
				//process product source data
				updateSourcingName(results,prod);
			}
		} catch (WTPropertyVetoException e) {
			LOGGER.error(ERROR_OCCURED+e.getLocalizedMessage());
			e.printStackTrace();
		} catch (WTException e) {
			LOGGER.error(ERROR_OCCURED+e.getLocalizedMessage());
			e.printStackTrace();
		}
		LOGGER.debug("SM PLUGIN :- VISUAL PRIMARY FOR SEASON ENDS");
		return true;
	}

	/**
	 * update sourcing config name according to season.
	 * @param results - Collection
	 * @param prodSeasonLink - LCSProductSeasonLink
	 */
	private static void updateSourceNmaeToSeason(Collection<?> results,LCSProductSeasonLink prodSeasonLink){
		LCSSourcingConfig sourcing;
		LCSSourceToSeasonLink sourceSeasonLink;
		String sName;
		for(Object obj: results){
			try{
				sourcing=(LCSSourcingConfig) obj;
				sName=sourcing.getSourcingConfigName();
				sourceSeasonLink=new LCSSourcingConfigQuery().getSourceToSeasonLink(sourcing.getMaster(), prodSeasonLink.getSeasonMaster());
				LOGGER.debug(sourceSeasonLink.getSeasonMaster().getName()+"-----"+prodSeasonLink.getSeasonMaster().getName());
				LOGGER.debug("SoURCE TO SEASON LINK IS PRIMARY : "+sourceSeasonLink.isPrimarySTSL());
				System.out.println("sName>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+sName);
				if(sourceSeasonLink.isPrimarySTSL() && FormatHelper.hasContent(sName)){
					if(!sName.contains(SMPrimarySourcePlugin.PRIMARY_SORUCE_VISUAL_INDICATOR)){
						//adding visual indicator "*"
						sName=SMPrimarySourcePlugin.PRIMARY_SORUCE_VISUAL_INDICATOR+sName;
					}
					System.out.println("sName>>>>>>>>>>>>>>>>>>111>>>>>>>>>>>>>>"+sName);

					//persist sourcing
					SMPrimarySourcePlugin.persistSorcing(sourcing, sourcing.getMaster(), sourceSeasonLink, sName, true,true);
				}else if(!sourceSeasonLink.isPrimarySTSL() && FormatHelper.hasContent(sName)){
					sName=sName.replace(SMPrimarySourcePlugin.PRIMARY_SORUCE_VISUAL_INDICATOR, "");
					System.out.println("sName>>>>>>>>>>>>222>>>>>>>>>>>>>>>>>>>"+sName);

					SMPrimarySourcePlugin.persistSorcing(sourcing, sourcing.getMaster(), sourceSeasonLink, sName, false,true);
				}
				LOGGER.debug("UPDATED SUORCING CONFIG NAME : "+sName);
			}catch(WTException e){
				LOGGER.error(ERROR_OCCURED+e.getLocalizedMessage());
				e.printStackTrace();
			} catch (WTPropertyVetoException e) {
				LOGGER.error(ERROR_OCCURED+e.getLocalizedMessage());
				e.printStackTrace();
			}
		}
	}
	/**
	 * update sourcing config name according to product.
	 * @param results - Collection
	 * @param prod - LCSProduct
	 */
	private static void updateSourcingName(Collection<?> results,LCSProduct prod){
		LCSSourcingConfig sourcing;
		String sName;
		LOGGER.debug("here .........");
		if(prod.getSeasonMaster()==null){
			LOGGER.debug("product with no season selected");
			for(Object obj: results){
				try {
					sourcing=(LCSSourcingConfig) obj;
					LOGGER.debug("total no of source attached to product : "+results.size());
					sName=sourcing.getSourcingConfigName();
					sName=sName.replace(SMPrimarySourcePlugin.PRIMARY_SORUCE_VISUAL_INDICATOR, "");
					persistSource(sourcing, sName);
				//	SMPrimarySourcePlugin.persistSorcing(sourcing, sourcing.getMaster(), null, sName, false,true);
				} catch (WTPropertyVetoException e) {
					LOGGER.error(ERROR_OCCURED+e.getLocalizedMessage());
					e.printStackTrace();
				} catch (WTException e) {
					LOGGER.error(ERROR_OCCURED+e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		}

	}
	/**
	 * persisting source.
	 * @param sConfig - LCSSourcingConfig
	 * @param sName - String
	 * @throws WTException - WTException
	 * @throws WTPropertyVetoException - WTPropertyVetoException
	 */
	private static void persistSource(LCSSourcingConfig sConfig,String sName) throws WTException, WTPropertyVetoException{
		LCSSourcingConfig sCon=sConfig;
		LCSSourcingConfigMaster sMaster;
		LOGGER.debug("Persisting Sourcing config start");
		sMaster=sCon.getMaster();
		sCon.setSourcingConfigName(sName);
		sCon.setValue("name", sName);
		sMaster.setSourcingConfigName(sName);

		//CR - SUGESTED BY SM Team :- don't want to see iteration on history tab
		PersistenceServerHelper.manager.update(sCon, false);
		PersistenceServerHelper.manager.update(sMaster, false);
		LOGGER.debug("Persisting Sourcing config end");

	}
}
