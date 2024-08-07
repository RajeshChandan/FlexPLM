package com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.pricesupdate.processor.SMPricesUpdateProcessor;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonPricesInformation;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTRuntimeException;

/**
 * SMPriceInboundUtil.java
 * This class handles DB queery . object create \ update process, XML file generation activities.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMPriceInboundUtil{
	private static final String FLEX_TYPE_ID_PATH = "flexTypeIdPath";
	/*
	 * REQ_LOGGER.
	 */
	private static final Logger REQ_LOGGER = Logger.getLogger("priceUpdateRequest");
	/*
	 * FEEDBACK_LOGGER.
	 */
	private static final Logger FEEDBACK_LOGGER = Logger.getLogger("priceUpdateFeedback");

	/**
	 * Generates Price update integration Request ID.
	 * @return int.
	 * @throws WTException - WTException
	 */
	public int  getProductInboundRequestID() throws WTException{
		int producInboundtRequestID = SMPricesInboundWebServiceConstants.INTEGRATION_REQUEST_ID;

		String lcsLogEntry=SMPricesInboundWebServiceConstants.LCSLOGENTRY;

		FlexType inboundLogType = FlexTypeCache.getFlexTypeFromPath(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_PATH);
		com.lcs.wc.flextype.FlexTypeAttribute att = inboundLogType.getAttribute(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_REQUEST_ID);

		SearchResults results = null;
		//Creating Statement.
		com.lcs.wc.db.PreparedQueryStatement statement = new com.lcs.wc.db.PreparedQueryStatement();

		statement.appendFromTable(lcsLogEntry);
		statement.appendSelectColumn(new QueryColumn(lcsLogEntry, "idA2A2"));
		statement.appendSelectColumn(lcsLogEntry, att.getColumnName());

		//add tables
		statement.appendFromTable(lcsLogEntry);

		statement.appendCriteria(new Criteria(lcsLogEntry, FLEX_TYPE_ID_PATH, inboundLogType.getIdPath(),Criteria.EQUALS));


		results=LCSQuery.runDirectQuery(statement);

		@SuppressWarnings("unchecked")
		List<FlexObject> data=results.getResults();
		int reqId;
		data=(List<FlexObject>) com.lcs.wc.util.SortHelper.sortFlexObjects(data, lcsLogEntry+"."+att.getColumnName());

		if(!data.isEmpty()){
			reqId=data.get(data.size()-1).getInt( lcsLogEntry+"."+att.getColumnName());
			producInboundtRequestID =reqId+1;
			return producInboundtRequestID;
		}
		return producInboundtRequestID+1;
	}




	/**
	 * Get product Season link from PLM ID.
	 * @param plmID
	 * @return
	 */
	public static LCSProductSeasonLink getProductSeasonLinkFromPLMID(String plmID){
		REQ_LOGGER.info("PLM ID received for PSL  ###################   "+plmID);
		LCSSeason season;
		com.lcs.wc.product.LCSProduct prod = null;
		String prodMasterReference;
		String seasonID;
		String[] ids = plmID.split("-");
		REQ_LOGGER.debug("Array size >>>>>>>>  "+ids.length);
		if(ids.length == 2){
			prodMasterReference = ids[0];
			seasonID = ids[1];
			try{
				//getting Part master.
				com.lcs.wc.part.LCSPartMaster partMaster = (com.lcs.wc.part.LCSPartMaster) LCSQuery
						.findObjectById("OR:com.lcs.wc.part.LCSPartMaster:" + prodMasterReference);
				//getting latest product iteration.
				com.lcs.wc.product.LCSProduct product = (com.lcs.wc.product.LCSProduct) VersionHelper.latestIterationOf(partMaster);
				prod = product;
				//getting season master.
				com.lcs.wc.season.LCSSeasonMaster seasonMaster = (com.lcs.wc.season.LCSSeasonMaster) LCSSeasonQuery
						.findObjectById("OR:com.lcs.wc.season.LCSSeasonMaster:" + seasonID);
				//getting season.
				season = (LCSSeason)VersionHelper.latestIterationOf(seasonMaster);
				return (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(product, season);
			}catch(WTRuntimeException wtRunTime){
				REQ_LOGGER.debug("Could Not restore Season Master ................!!!!! ");
				try{
					//getting season from IDA2A2.
					season = (LCSSeason) LCSSeasonQuery.findObjectById("OR:com.lcs.wc.season.LCSSeason:"+seasonID);
					//getting latest iteration for season.
					season = (LCSSeason) VersionHelper.latestIterationOf(season);
					REQ_LOGGER.debug("Season  ****************  "+season.getName());

					return (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(prod, season);

				}catch(WTException wex){
					REQ_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,wex);
					return null;
				}catch(WTRuntimeException runTimeExcpt){
					REQ_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,runTimeExcpt);
					return null;
				}
			}catch(WTException e){
				REQ_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,e);
				return null;
			}
		}else{
			return null;
		}

	}
	/**
	 * Persisting Product Season Link Object.
	 * @param productSeasonLink - LCSProductSeasonLink
	 */
	public static void persistProductSeasonLink(LCSProductSeasonLink productSeasonLink){
		try {
			com.lcs.wc.foundation.LCSLogic.deriveFlexTypeValues(productSeasonLink, true);
			com.lcs.wc.foundation.LCSLogic.persist(productSeasonLink, true);
			SMPricesUpdateProcessor.setIntegartionFailurePSL(false);
		} catch (WTException ex) {
			REQ_LOGGER.error("ERROR in saving LCSProductSeasonLink Object  !!!!  "+ex);
			SMPricesUpdateProcessor.setIntegartionFailurePSL(true);
		}
	}
	/**
	 * Queries the Product Season Log Entry.
	 * @param flexPath - String
	 * @throws WTException - WTException
	 * @returns hashMapLogEntry - HashMap<String, LCSLogEntry>
	 */
	public static Map<String, LCSLogEntry> queryPricesUpdateInboundLogEntry() throws WTException{
		REQ_LOGGER.debug("Inside Query Prices Update Inbound Log Entry method !!!!!!!!!!");
		//Hashmap to store Log entries
		Map<String, LCSLogEntry> hashMapProductSeasonLinkLogEntry = new HashMap<>();
		com.lcs.wc.flextype.FlexType logType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_PATH);
		com.lcs.wc.db.PreparedQueryStatement pricesUpdateStatement = new com.lcs.wc.db.PreparedQueryStatement();//Creating Statement.
		pricesUpdateStatement.appendFromTable(LCSLogEntry.class);
		pricesUpdateStatement.appendSelectColumn(SMPricesInboundWebServiceConstants.LCSLOGENTRY, logType.getAttribute(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_PLM_ID).getColumnName());//append column
		pricesUpdateStatement.appendSelectColumn(SMPricesInboundWebServiceConstants.LCSLOGENTRY, "IDA2A2");//append column
		pricesUpdateStatement.appendCriteria(new Criteria(SMPricesInboundWebServiceConstants.LCSLOGENTRY, FLEX_TYPE_ID_PATH, logType.getIdPath(),Criteria.EQUALS));//adding criteria
		SearchResults pricesUpdateResults = null;
		//executing  statement
		pricesUpdateResults =LCSQuery.runDirectQuery(pricesUpdateStatement);
		List<?> proddataCollection= pricesUpdateResults.getResults();
		FlexObject prodfo=null;
		REQ_LOGGER.debug("Log Entry data collection Size for Prod Inbound >>>>>>>\t"+proddataCollection.size());
		for(Object obj:proddataCollection){
			prodfo= (FlexObject) obj;
			LCSLogEntry priceslogEntry=(LCSLogEntry) LCSQuery.findObjectById("com.lcs.wc.foundation.LCSLogEntry:"+prodfo.getString("LCSLOGENTRY.IDA2A2"));
			//storing in hashmap
			hashMapProductSeasonLinkLogEntry.put((String)priceslogEntry.getValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_PLM_ID), priceslogEntry);
		}
		//returning log entry
		return hashMapProductSeasonLinkLogEntry;
	}

	/**
	 * Creating Log Entry for Product Inbound.
	 * @param prodSeasonLink - LCSProductSeasonLink
	 * @param pricesInfo - ProductSeasonLinkInformationItem
	 */
	public static LCSLogEntry createLogEntryPriceUpdateInbound(LCSProductSeasonLink prodSeasonLink,
			ProductSeasonPricesInformation pricesInfo) {
		REQ_LOGGER.debug("Inside create Log Entry for Product Season Link");
		try{
			com.lcs.wc.flextype.FlexType priceUpdateInboundCreateLogType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_PATH);
			LCSLogEntry priceUpdateLogEntry = LCSLogEntry.newLCSLogEntry();
			//Setting attribute values
			priceUpdateLogEntry.setFlexType(priceUpdateInboundCreateLogType);

			if(null != prodSeasonLink){

				priceUpdateLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_REQUEST_ID, String.valueOf(SMPricesUpdateProcessor.getRequestId()));
				priceUpdateLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_OBJECT_TYPE, "PRODUCT SEASON LINK");
				priceUpdateLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_MDM_ID, prodSeasonLink.getValue(SMPricesInboundWebServiceConstants.PSL_MDM_ID));
				priceUpdateLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_OBJECT_NAME, productSeasonLinkDetails(prodSeasonLink));
				priceUpdateLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_PLM_ID, pricesInfo.getPlmId());
				priceUpdateLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_FEEDBACK_STATUS, SMPricesInboundWebServiceConstants.PENDING);
				priceUpdateLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_ERROR_REASON, SMPricesUpdateProcessor.getErrorMsg());
				priceUpdateLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_ITTERATION_ID, pricesInfo.getIterationId());

				if(!FormatHelper.hasContent(SMPricesUpdateProcessor.getErrorMsg())&& !SMPricesUpdateProcessor.isIntegartionFailurePSL()){
					REQ_LOGGER.info("INTEGARTION SUCCESS on PSL");
					priceUpdateLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_INTEGRATION_STATUS, SMPricesInboundWebServiceConstants.INTEGRATED);
				}else {
					REQ_LOGGER.info("INTEGRATION FAILED on PSL");
					priceUpdateLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_INTEGRATION_STATUS, SMPricesInboundWebServiceConstants.NOT_INTEGRATED);
				}
				//persist log Entry.
				persistPricesLogEntry(priceUpdateLogEntry);
				return priceUpdateLogEntry;

			}else{

				LCSLogEntry priceUpdateLogEntryInvalidPLMID = LCSLogEntry.newLCSLogEntry();
				priceUpdateLogEntryInvalidPLMID.setFlexType(priceUpdateInboundCreateLogType);
				priceUpdateLogEntryInvalidPLMID.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_REQUEST_ID, String.valueOf(SMPricesUpdateProcessor.getRequestId()));
				priceUpdateLogEntryInvalidPLMID.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_OBJECT_TYPE, "PRODUCT SEASON LINK");
				priceUpdateLogEntryInvalidPLMID.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_MDM_ID, pricesInfo.getMdmId());
				priceUpdateLogEntryInvalidPLMID.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_OBJECT_NAME, "");
				priceUpdateLogEntryInvalidPLMID.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_PLM_ID, pricesInfo.getPlmId());
				priceUpdateLogEntryInvalidPLMID.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_FEEDBACK_STATUS, SMPricesInboundWebServiceConstants.PENDING);
				priceUpdateLogEntryInvalidPLMID.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_ERROR_REASON, "Product Season Link DOES NOT EXIST !!! INVALID PLM ID RECEIVED !!!");
				priceUpdateLogEntryInvalidPLMID.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_INTEGRATION_STATUS, SMPricesInboundWebServiceConstants.NOT_INTEGRATED);
				priceUpdateLogEntryInvalidPLMID.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_ITTERATION_ID, pricesInfo.getIterationId());
				//persist log Entry.
				persistPricesLogEntry(priceUpdateLogEntryInvalidPLMID);
				return priceUpdateLogEntryInvalidPLMID;
			}
		}catch(WTException wtExc){
			REQ_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,wtExc);
		} catch (WTPropertyVetoException ex) {
			REQ_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,ex);
		}
		return null;
	}

	/**
	 * Updates Existing Log Entry.
	 * @param productSeasonLinkLogEntry - LCSLogEntry
	 * @param prodSeasonLink - LCSProductSeasonLink
	 * @param pricesUpdateInfo - ProductSeasonLinkInformationItem
	 */
	public static void updateLogEntryProductSeasonLinkInbound(LCSLogEntry productSeasonLinkLogEntry,
			LCSProductSeasonLink prodSeasonLink,
			com.sportmaster.wc.interfaces.webservices.productbean.ProductSeasonPricesInformation pricesUpdateInfo) {
		REQ_LOGGER.debug("Inside UPDATE Log Entry for Product Season Link");
		try{
			//Setting attribute Values.
			if(null != prodSeasonLink){

				productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_REQUEST_ID, String.valueOf(SMPricesUpdateProcessor.getRequestId()));
				productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_MDM_ID, prodSeasonLink.getValue(SMPricesInboundWebServiceConstants.PSL_MDM_ID));
				productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_OBJECT_NAME, productSeasonLinkDetails(prodSeasonLink));
				productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_PLM_ID, pricesUpdateInfo.getPlmId());
				productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_FEEDBACK_STATUS, SMPricesInboundWebServiceConstants.PENDING);
				productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_ERROR_REASON, SMPricesUpdateProcessor.getErrorMsg());
				productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_ITTERATION_ID, pricesUpdateInfo.getIterationId());

				if(!FormatHelper.hasContent(SMPricesUpdateProcessor.getErrorMsg()) && !SMPricesUpdateProcessor.isIntegartionFailurePSL()){
					REQ_LOGGER.info("INTEGARTION SUCCESS on PSL");
					productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_INTEGRATION_STATUS, SMPricesInboundWebServiceConstants.INTEGRATED);
				}else {
					REQ_LOGGER.info("INTEGARTION FAILED on PSL");
					productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_INTEGRATION_STATUS, SMPricesInboundWebServiceConstants.NOT_INTEGRATED);
				}
			}else{

				productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_REQUEST_ID, String.valueOf(SMPricesUpdateProcessor.getRequestId()));
				productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_MDM_ID, pricesUpdateInfo.getMdmId());
				productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_OBJECT_NAME, "");
				productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_PLM_ID, pricesUpdateInfo.getPlmId());
				productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_FEEDBACK_STATUS, SMPricesInboundWebServiceConstants.PENDING);
				productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_ERROR_REASON, "Product Season Link DOES NOT EXIST !!! INVALID PLM ID RECEIVED !!!");
				productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_INTEGRATION_STATUS, SMPricesInboundWebServiceConstants.NOT_INTEGRATED);
				productSeasonLinkLogEntry.setValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_ITTERATION_ID, pricesUpdateInfo.getIterationId());

			}
			//Persisting updated log entry.
			persistPricesLogEntry(productSeasonLinkLogEntry);
		}catch(WTException wtEx){
			REQ_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL, wtEx);
		} catch (WTPropertyVetoException wtPv) {
			REQ_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL, wtPv);
		}
	}

	/**
	 * Persists log entry object.
	 * @param logEntryObj - LCSLogEntry
	 * @throws WTException
	 */
	public static void persistPricesLogEntry(LCSLogEntry logEntryObj) throws WTException{
		com.lcs.wc.foundation.LCSLogEntryLogic logEntryInboundLogic = new com.lcs.wc.foundation.LCSLogEntryLogic();
		//Save Log Entry object
		logEntryInboundLogic.saveLog(logEntryObj, true);
	}


	/**
	 * Getting Product Season Link Details.
	 * @param prodSeasonLink
	 * @return
	 */
	public static String productSeasonLinkDetails(LCSProductSeasonLink prodSeasonLink){
		String season;
		try {
			//Getting Season Name.
			season = SeasonProductLocator.getSeasonRev(prodSeasonLink).getName();

			//Getting Product Name.
			String prod = SeasonProductLocator.getProductSeasonRev(prodSeasonLink).getName();

			return season+" , "+prod;
		} catch (WTException e) {
			REQ_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,e);
			return null;
		}
	}

	/**
	 * Queries the Product Season Log Entry.
	 * @param flexPath - String
	 * @throws WTException - WTException
	 * @returns hashMapLogEntry - HashMap<String, LCSLogEntry>
	 */
	public static Map<String, LCSLogEntry> queryPriceUpdateFeedbackLogEntry(){
		try{
			FEEDBACK_LOGGER.debug("Inside Query Product Season Link Inbound Log Entry method !!!!!!!!!!");
			//Hashmap to store Log entries
			Map<String, LCSLogEntry> hashMapProductSeasonLinkLogEntry = new HashMap<>();
			com.lcs.wc.flextype.FlexType logType= com.lcs.wc.flextype.FlexTypeCache.getFlexTypeFromPath(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_PATH);
			com.lcs.wc.db.PreparedQueryStatement prodSeasonStatement = new com.lcs.wc.db.PreparedQueryStatement();//Creating Statement.
			prodSeasonStatement.appendFromTable(LCSLogEntry.class);
			prodSeasonStatement.appendSelectColumn(SMPricesInboundWebServiceConstants.LCSLOGENTRY, logType.getAttribute(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_PLM_ID).getColumnName());//append column
			prodSeasonStatement.appendSelectColumn(SMPricesInboundWebServiceConstants.LCSLOGENTRY, "IDA2A2");//append column
			prodSeasonStatement.appendCriteria(new Criteria(SMPricesInboundWebServiceConstants.LCSLOGENTRY, FLEX_TYPE_ID_PATH, logType.getIdPath(),Criteria.EQUALS));//adding criteria
			prodSeasonStatement.appendAndIfNeeded();
			prodSeasonStatement.appendCriteria(new Criteria(SMPricesInboundWebServiceConstants.LCSLOGENTRY,logType.getAttribute(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_FEEDBACK_STATUS).getColumnName(),SMPricesInboundWebServiceConstants.PENDING,Criteria.EQUALS));
			com.lcs.wc.db.SearchResults results = null;
			//executing  statement
			results =LCSQuery.runDirectQuery(prodSeasonStatement);
			List<?> dataCollection= results.getResults();
			FlexObject fo=null;
			FEEDBACK_LOGGER.debug("Log Entry data collection Size >>>>>>>\t"+dataCollection.size());

			for(Object obj:dataCollection){
				fo= (FlexObject) obj;
				LCSLogEntry logEntry=(LCSLogEntry) LCSQuery.findObjectById("com.lcs.wc.foundation.LCSLogEntry:"+fo.getString("LCSLOGENTRY.IDA2A2"));
				//storing in hashmap
				hashMapProductSeasonLinkLogEntry.put((String)logEntry.getValue(SMPricesInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRICE_UPDATE_PLM_ID), logEntry);
			}

			//returning log entry
			return  hashMapProductSeasonLinkLogEntry;
		}catch(WTException exp){
			FEEDBACK_LOGGER.error(SMPricesInboundWebServiceConstants.ERROR_LITERAL,exp);
			return null;
		}
	}

	/**
	 * Phase - 13 changes Get sku Season link from PLM ID.
	 *
	 * @param plmID
	 * @return
	 * @throws WTException
	 */
	public static LCSSKUSeasonLink getSKUSeasonLinkFromPLMID(String plmID) throws WTException {

		REQ_LOGGER.info("PLM ID received for SSL  ###################   " + plmID);
		LCSSeason season;
		LCSSKUSeasonLink skuSeason = null;
		String skuMasterReference;
		String seasonID;
		// splitting PLM IDs
		String[] ids = plmID.split("-");
		REQ_LOGGER.info("Array size >>>>>>>>  " + ids.length);
		if (ids.length == 2) {
			skuMasterReference = ids[0];
			seasonID = ids[1];
			// getting SK Master.
			LCSPartMaster partMaster = (LCSPartMaster) LCSQuery
					.findObjectById("OR:com.lcs.wc.part.LCSPartMaster:" + skuMasterReference);
			// getting latest iteration of SKU.
			LCSSKU sku = (LCSSKU) VersionHelper.latestIterationOf(partMaster);
			// Getting season master.
			LCSSeasonMaster seasonMaster = (LCSSeasonMaster) LCSQuery
					.findObjectById("OR:com.lcs.wc.season.LCSSeasonMaster:" + seasonID);
			// getting season.
			season = (LCSSeason) VersionHelper.latestIterationOf(seasonMaster);
			skuSeason = (LCSSKUSeasonLink) LCSSeasonQuery.findSeasonProductLink(sku, season);
		}
		return skuSeason;
	}

	/**
	 * Phase - 13 Changes. Persisting SKU Season Link Object.
	 *
	 * @param skuSeasonLink
	 *            - LCSSKUSeasonLink
	 * @throws WTException
	 */
	public static void persistColorwaySeasonLink(LCSSKUSeasonLink skuSeasonLink) throws WTException {

		LCSLogic.deriveFlexTypeValues(skuSeasonLink, true);
		LCSLogic.persist(skuSeasonLink, true);
	}

}