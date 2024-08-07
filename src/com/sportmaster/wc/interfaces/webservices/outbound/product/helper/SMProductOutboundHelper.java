/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSLogEntryLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

import wt.util.WTException;
import wt.util.WTRuntimeException;

/**
 * @author BSC
 *
 */
public class SMProductOutboundHelper {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductOutboundHelper.class);
	/**
	 * Log Entry class.
	 */
	private static final String LOG_ENTRY_CLASS = "com.lcs.wc.foundation.LCSLogEntry:";
	/**
	 * ida2a2.
	 */
	private static final String IDA2A2 = "IDA2A2";
	/**
	 * LogEntry ida2a2.
	 */
	private static final String LOG_ENTRY_IDA2A2 = "LCSLOGENTRY.IDA2A2";
	/**
	 * protected constructor.
	 */
	public SMProductOutboundHelper(){
		//public constructor 
	}

	/**
	 * Queries the Product Season Outbound Integration Log Entry.
	 * @param String flexPath
	 * @throws WTException exceptions
	 * @returns HashMap<String, LCSLogEntry>
	 */
	public LCSLogEntry queryProductSeasonOutboundLogEntry(String attribute,String mdmId){
		try{
			LCSLogEntry logEntry=null;
			
			com.lcs.wc.flextype.FlexType prodSeasonOutboundLogEntryType = com.lcs.wc.flextype.FlexTypeCache
					.getFlexTypeFromPath(SMProductOutboundWebServiceConstants.LOG_ENTRY_PRODUCT_SEASON_OUTBOUND_PATH);

			// Creating Statement.
			PreparedQueryStatement statement = new PreparedQueryStatement();
			statement.appendFromTable(LCSLogEntry.class);

			statement.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY,
					prodSeasonOutboundLogEntryType.getAttribute(attribute).getColumnName());// append column
			statement.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, IDA2A2);// append column

			statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY,
					SMOutboundWebServiceConstants.FLEXTYPEIDPATH, prodSeasonOutboundLogEntryType.getIdPath(),
					Criteria.EQUALS));// adding criteria
			
			statement.appendAndIfNeeded();
			statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY,
					prodSeasonOutboundLogEntryType.getAttribute(attribute).getColumnName(), mdmId, Criteria.EQUALS));

			com.lcs.wc.db.SearchResults results = null;
			//executing  statement
			results =LCSQuery.runDirectQuery(statement);
			FlexObject fo=null;
			List<?> resultColl = results.getResults();
			
			if(resultColl !=null && !resultColl.isEmpty()) {
				fo= (FlexObject) resultColl.get(0);
				logEntry=(LCSLogEntry) LCSQuery.findObjectById(LOG_ENTRY_CLASS+fo.getString(LOG_ENTRY_IDA2A2));
			}
					
			//returning log entry
			return logEntry;
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage(), we);
			return null;
		}
	}



	/**
	 * Queries Log Entry for Update_pending and returns Product Season Link Collection.
	 * @param attribute - String
	 * @return supplierObjectMap - Map<String, LCSProductSeasonLink>
	 * @throws WTException - WTException
	 */
	public Map<String, LCSLogEntry> queryProductSeasonLinkLogEntryForScheduleQueue(String attribute){
		try{
			//Hashmap to store Log entries
			Map<String, LCSLogEntry> productSeasonCollectionForScheduleQueue=new HashMap<>();
			com.lcs.wc.flextype.FlexType prodSeasonLinkLogType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMProductOutboundWebServiceConstants.LOG_ENTRY_PRODUCT_SEASON_OUTBOUND_PATH);
			PreparedQueryStatement prodSeassonStmt = new PreparedQueryStatement();//Creating Statement.
			prodSeassonStmt.appendFromTable(LCSLogEntry.class);
			prodSeassonStmt.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, prodSeasonLinkLogType.getAttribute(attribute).getColumnName());//append column
			prodSeassonStmt.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, IDA2A2);//append column
			prodSeassonStmt.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY, SMOutboundWebServiceConstants.FLEXTYPEIDPATH, prodSeasonLinkLogType.getIdPath(),Criteria.EQUALS));//adding criteria
			prodSeassonStmt.appendAndIfNeeded();
			prodSeassonStmt.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY, prodSeasonLinkLogType
					.getAttribute(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS).getColumnName(),
					SMProductOutboundWebServiceConstants.LOG_ENTRY_UPDATE_PENDING, Criteria.EQUALS));
			com.lcs.wc.db.SearchResults prodSeasonResults = null;
			//executing  statement
			
			prodSeasonResults =LCSQuery.runDirectQuery(prodSeassonStmt);
			List<?> prodSeasonLogEntryCollection= prodSeasonResults.getResults();
			FlexObject fo=null;
			LCSLogEntry logEntryObj;
			
			if (!prodSeasonLogEntryCollection.isEmpty()) {
				LOGGER.debug("Supplier Log Entry Outbound data collection Size >>>>>>>\t"+prodSeasonLogEntryCollection.size());
				for(Object obj:prodSeasonLogEntryCollection){
					fo= (FlexObject) obj;
					logEntryObj=(LCSLogEntry) LCSQuery.findObjectById(LOG_ENTRY_CLASS+fo.getString(LOG_ENTRY_IDA2A2));
					//add to map.
					productSeasonCollectionForScheduleQueue.put((String)logEntryObj.getValue(attribute), logEntryObj);
				}	
			}
			//returning log entry
			return productSeasonCollectionForScheduleQueue;
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage(), we);
			return null;
		}
	}

	/**
	 * Persists log entry object.
	 * @param logEntryObj - LCSLogEntry
	 */
	public void persistLogEntryObject(LCSLogEntry logEntryObj){
		LCSLogEntryLogic logEntryLogic = new LCSLogEntryLogic();
		try {
			LOGGER.info("Persisiting log entry object ....");
			//Save Log Entry object
			logEntryLogic.saveLog(logEntryObj, true);
		} catch (WTException e) {
			LOGGER.error("ERROR in persisting Log Entry Object !!!!!!  "+e.getLocalizedMessage(), e);
		}
	}

	/**
	 * Get Product Season details.
	 * @param psl - LCSProductSeasonLink.
	 * @return String.
	 */
	public String getProductSeasonLinkDetails(LCSProductSeasonLink psl){
		try{
			LCSProduct prodSeasonRev = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+(int)psl.getProductSeasonRevId());

			LCSSeason season = VersionHelper.latestIterationOf(psl.getSeasonMaster());

			return season.getName()+" , "+prodSeasonRev.getName();
		}catch(WTException exp){
			LOGGER.error(exp.getLocalizedMessage(), exp);
			return null;
		}
	}

	/**
	 * Get Colorway Season Deatils.
	 * @param ssl - LCSSKUSeasonLink.
	 * @return String.
	 */
	public String getColorwaySeasonLinkDetails(LCSSKUSeasonLink ssl){
		try{
			LCSSKU sku = (LCSSKU) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:"+(int)ssl.getSkuSeasonRevId());
			sku = (LCSSKU) VersionHelper.latestIterationOf(sku);
			LCSSeason season = VersionHelper.latestIterationOf(ssl.getSeasonMaster());
			LCSProduct product = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+(int)ssl.getProductARevId());
			product = (LCSProduct) VersionHelper.latestIterationOf(product);

			return season.getName()+" , "+product.getName()+" , "+sku.getName();

		}catch(WTException wtExcpt){
			LOGGER.error(wtExcpt.getLocalizedMessage(), wtExcpt);
			return null;
		}
	}


	/**
	 * Get product Season link from PLM ID.
	 * @param plmID
	 * @return
	 * @throws WTException 
	 */
	public LCSProductSeasonLink getProductSeasonLinkFromPLMID(String plmID){
		LCSSeason season;
		LCSProduct prod = null;
		String prodMasterReference;
		String seasonID;
		String[] ids = plmID.split("-");
		prodMasterReference = ids[0];
		seasonID = ids[1];
		try{
			LCSPartMaster partMaster = (LCSPartMaster) LCSProductQuery.findObjectById("OR:com.lcs.wc.part.LCSPartMaster:"+prodMasterReference);
			LCSProduct product = VersionHelper.latestIterationOf(partMaster);
			prod = product;
			LOGGER.info("Product  ::::::::   "+product.getName());
			LCSSeasonMaster seasonMaster = (LCSSeasonMaster) LCSSeasonQuery.findObjectById("OR:com.lcs.wc.season.LCSSeasonMaster:"+seasonID);
			season = VersionHelper.latestIterationOf(seasonMaster);
			LOGGER.info("Season ::::::::::   "+season.getName());
			return (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(product, season);
		}catch(WTRuntimeException objNotFound){
			LOGGER.debug("Could not restore Season Master !!!!!!!!!!!!!!");
			try {
				season = (LCSSeason) LCSSeasonQuery.findObjectById("OR:com.lcs.wc.season.LCSSeason:"+seasonID);

				season = (LCSSeason) VersionHelper.latestIterationOf(season);
				LOGGER.info("Season   >>>>>>>>>>>>>>      "+season.getName());

				if(null != prod){
					return (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(prod, season);
				}else{
					return null;
				}
			} catch (WTException wt) {
				LOGGER.error(wt.getLocalizedMessage(), wt);
				return null;
			}catch(WTRuntimeException runTimeExcpt){
				LOGGER.debug(runTimeExcpt.getLocalizedMessage(), runTimeExcpt);
				return null;
			}
		} catch (WTException e) {
			LOGGER.error(e.getLocalizedMessage(), e);
			return null;
		}
	}

	/**
	 * Get sku Season link from PLM ID.
	 * @param plmID
	 * @return
	 */
	public LCSSKUSeasonLink getSKUSeasonLinkFromPLMID(String plmID){
		try{
			String skuMasterReference ;
			String seasonID;
			String[] ids = plmID.split("-");
			skuMasterReference = ids[0];
			seasonID = ids[1];
			LCSPartMaster partMaster = (LCSPartMaster) LCSProductQuery.findObjectById("OR:com.lcs.wc.part.LCSPartMaster:"+skuMasterReference);
			LCSSKU sku = VersionHelper.latestIterationOf(partMaster);
			LCSSeasonMaster seasonMaster = (LCSSeasonMaster) LCSSeasonQuery.findObjectById("OR:com.lcs.wc.season.LCSSeasonMaster:"+seasonID);
			LCSSeason season = VersionHelper.latestIterationOf(seasonMaster);
			return (LCSSKUSeasonLink) LCSSeasonQuery.findSeasonProductLink(sku, season);
		}catch(WTException e){
			LOGGER.error(e.getLocalizedMessage(), e);
			return null;
		}
	}

	/**
	 * Returns Product Season link PLM ID.
	 * @param psl - LCSProductSeasonLink.
	 * @return - String.
	 */
	public String getProductMasterReferencefromLink(LCSProductSeasonLink psl){
		try{
			LCSProduct product = SeasonProductLocator.getProductARev(psl);
			LCSSeason season = SeasonProductLocator.getSeasonRev(psl);
			wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
			String refString = rf.getReferenceString(product.getMasterReference());
			LOGGER.info("rf prod ***********************     "+refString);
			char colon = ':';
			String ida3MasterReference = refString.substring(refString.lastIndexOf(colon) + 1);
			String seasonMasterID = rf.getReferenceString(season.getMasterReference());
			String seasonMasterReferenceID = seasonMasterID.substring(seasonMasterID.lastIndexOf(colon) + 1);
			return ida3MasterReference+"-"+seasonMasterReferenceID;
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage(), we);
			return null;
		}
	}

	/**
	 * Get PLM ID for Colorway Season Link.
	 * @param ssl - LCSColorwaySeasonLink.
	 * @return - String.
	 */
	public String getColorwayMasterReferenceFromLink(LCSSKUSeasonLink ssl){
		try{
			LCSSKU sku = SeasonProductLocator.getSKUARev(ssl);
			LCSSeason season = SeasonProductLocator.getSeasonRev(ssl);
			wt.fc.ReferenceFactory rf = new wt.fc.ReferenceFactory();
			String refString = rf.getReferenceString(sku.getMasterReference());
			LOGGER.info("rf sku ***********************     "+refString);
			char colon = ':';
			String ida3MasterRef = refString.substring(refString.lastIndexOf(colon) + 1);
			String skuSeasonMasterID = rf.getReferenceString(season.getMasterReference());
			String seasonMasterRefID = skuSeasonMasterID.substring(skuSeasonMasterID.lastIndexOf(colon) + 1);
			return ida3MasterRef+"-"+seasonMasterRefID;
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage(), we);
			return null;
		}
	}

	/**
	 * Get PLM ID for Source to Season Link.
	 * @param src - LCSSourcingConfig.
	 * @param seaon - LCSSeason.
	 * @return String.
	 */
	public String getSourcePLMID(LCSSourcingConfig src, LCSSeason season){
		LOGGER.debug("Getting Source to Season Link PLM ID ....................");
		String srcPLMID = FormatHelper.getNumericFromReference(src.getMasterReference());
		String seasonPLMID = FormatHelper.getNumericFromReference(season.getMasterReference());
		return srcPLMID+"-"+seasonPLMID;
	}
	/**
	 * removes special char 'and' from string.
	 * @param flexType
	 * @return string
	 */
	public String modifyObjectType(String flexType) {
		String type=flexType;
		type=type.replace("&", "and");
		return type;
	}
}
