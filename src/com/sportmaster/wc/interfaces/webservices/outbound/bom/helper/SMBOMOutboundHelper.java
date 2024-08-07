/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.bom.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.flexbom.FlexBOMPart;
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
import com.sportmaster.wc.interfaces.webservices.outbound.bom.util.SMBOMOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTRuntimeException;

/**
 * @author BSC
 *
 */
public class SMBOMOutboundHelper {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMBOMOutboundHelper.class);
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
	public SMBOMOutboundHelper(){
		//public constructor 
	}

	/**
	 * Queries the BOM Outbound Integration Log Entry.
	 * @param String flexPath
	 * @throws WTException exceptions
	 * @returns HashMap<String, LCSLogEntry>
	 */
	public LCSLogEntry queryBOMOutboundLogEntry(String attribute,String mdmId){
		try{
			LCSLogEntry logEntry=null;
			
			com.lcs.wc.flextype.FlexType bomOutboundLogEntryType = com.lcs.wc.flextype.FlexTypeCache
					.getFlexTypeFromPath(SMBOMOutboundWebServiceConstants.LOG_ENTRY_BOM_OUT_BOUND_PATH);

			// Creating Statement.
			PreparedQueryStatement statement = new PreparedQueryStatement();
			statement.appendFromTable(LCSLogEntry.class);

			statement.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY,
					bomOutboundLogEntryType.getAttribute(attribute).getColumnName());// append column
			statement.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, IDA2A2);// append column

			statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY,
					SMOutboundWebServiceConstants.FLEXTYPEIDPATH, bomOutboundLogEntryType.getIdPath(),
					Criteria.EQUALS));// adding criteria
			
			statement.appendAndIfNeeded();
			statement.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY,
					bomOutboundLogEntryType.getAttribute(attribute).getColumnName(), mdmId, Criteria.EQUALS));

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
	 * Queries Log Entry for Update_pending and returns BOM Collection.
	 * @param attribute - String
	 * @return supplierObjectMap - Map<String, LCSProductSeasonLink>
	 * @throws WTException - WTException
	 */
	public Map<String, LCSLogEntry> queryBOMLogEntryForScheduleQueue(String attribute){
		try{
			//Hashmap to store Log entries
			Map<String, LCSLogEntry> bomCollectionForScheduleQueue=new HashMap<>();
			com.lcs.wc.flextype.FlexType bomLogType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMBOMOutboundWebServiceConstants.LOG_ENTRY_BOM_OUT_BOUND_PATH);
			PreparedQueryStatement bomStmt = new PreparedQueryStatement();//Creating Statement.
			bomStmt.appendFromTable(LCSLogEntry.class);
			bomStmt.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, bomLogType.getAttribute(attribute).getColumnName());//append column
			bomStmt.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, IDA2A2);//append column
			bomStmt.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY, SMOutboundWebServiceConstants.FLEXTYPEIDPATH, bomLogType.getIdPath(),Criteria.EQUALS));//adding criteria
			bomStmt.appendAndIfNeeded();
			bomStmt.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY, bomLogType
					.getAttribute(SMBOMOutboundWebServiceConstants.BOM_LOG_ENTRY_INTEGRATION_STATUS).getColumnName(),
					SMBOMOutboundWebServiceConstants.LOG_ENTRY_PENDING, Criteria.EQUALS));
			com.lcs.wc.db.SearchResults bomResults = null;
			//executing  statement
			
			bomResults =LCSQuery.runDirectQuery(bomStmt);
			List<?> bomLogEntryCollection= bomResults.getResults();
			FlexObject fo=null;
			LCSLogEntry logEntryObj;
			
			if (!bomLogEntryCollection.isEmpty()) {
				LOGGER.debug("BOM Log Entry Outbound data collection Size >>>>>>>\t"+bomLogEntryCollection.size());
				for(Object obj:bomLogEntryCollection){
					fo= (FlexObject) obj;
					logEntryObj=(LCSLogEntry) LCSQuery.findObjectById(LOG_ENTRY_CLASS+fo.getString(LOG_ENTRY_IDA2A2));
					//add to map.
					bomCollectionForScheduleQueue.put((String)logEntryObj.getValue(attribute), logEntryObj);
				}	
			}
			//returning log entry
			return bomCollectionForScheduleQueue;
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
	 * Get BOM details.
	 * @param flexbompart - FlexBOMPart.
	 * @return String.
	 */
	public String getBOMDetails(FlexBOMPart flexbompart){
		try{
			LOGGER.info("Getting  BOM Details ....");
			//LCSProduct prodSeasonRev = (LCSProduct) flexbompart.getOwnerMaster();
			
			WTPartMaster wtpartMaster= (WTPartMaster) flexbompart.getOwnerMaster();
			
			if (wtpartMaster!=null) {
				LCSProduct product = (LCSProduct) VersionHelper.latestIterationOf(wtpartMaster);			
				LCSProduct productRevA = SeasonProductLocator.getProductARev(product);
				LOGGER.info("product name ...."+productRevA.getName());
				return flexbompart.getName()+" , "+productRevA.getName();

			}

			return flexbompart.getName();
		}catch(WTException exp){
			LOGGER.error(exp.getLocalizedMessage(), exp);
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
