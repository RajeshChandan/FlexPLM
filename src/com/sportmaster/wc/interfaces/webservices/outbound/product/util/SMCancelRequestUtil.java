package com.sportmaster.wc.interfaces.webservices.outbound.product.util;

import java.util.ArrayList;
import org.apache.log4j.Logger;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.util.SMOutboundWebServiceConstants;

import wt.util.WTException;
import wt.util.WTRuntimeException;

/**
 * SMCancelRequestUtil.java
 * This class used to query log entry obehct whose status is "CANCEL_PENDING".
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMCancelRequestUtil {

	public SMCancelRequestUtil() {
		super();
	}

	/*
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMCancelRequestUtil.class);
	/**
	 * ida2a2.
	 */
	private static final String IDA2A2 = "IDA2A2";

	/**
	 * this method Queries all records from DB :
	 * criteria:-
	 * 	-Queries only log-entry object.
	 * 	-Queries only one column ida2a2 value(helps to retrieve log entry object)
	 * 	-ALL DTAA Will be pulled if INTEGRATION STATUS is CANCELLED PENDING
	 * 	-pulled all records from loentry\outbound\colorway, product, colorway season link nodes.
	 * 
	 * @return Search result object
	 */
	public SearchResults getCancelRequestData() {

		LOGGER.debug("creating query to retrieve log entry data");
		try{
			//flex type object
			FlexType logType= FlexTypeCache.getFlexTypeFromPath(SMProductOutboundWebServiceConstants.LOG_ENTRY_PRODUCT_OUT_BOUND_PATH);

			//query statement.
			PreparedQueryStatement prodSeassonStmt = new PreparedQueryStatement();//Creating Statement.
			//adding table name
			prodSeassonStmt.appendFromTable(LCSLogEntry.class);
			prodSeassonStmt.appendSelectColumn(SMOutboundWebServiceConstants.LCSLOGENTRY, IDA2A2);//append column

			//adding criteria for integration status
			prodSeassonStmt.appendCriteria(new Criteria(SMOutboundWebServiceConstants.LCSLOGENTRY, logType
					.getAttribute(SMProductOutboundWebServiceConstants.LOG_ENTRY_INTEGRATION_STATUS).getColumnName(),
					SMProductOutboundWebServiceConstants.LOG_ENTRY_CANCELLED_PENDING, Criteria.EQUALS));

			//creating collection for required logentry nodes.
			ArrayList<String> values =new ArrayList<>();
			values.add(logType.getIdPath());
			logType=FlexTypeCache.getFlexTypeFromPath(SMProductOutboundWebServiceConstants.LOG_ENTRY_COLORWAY_OUTBOUND_PATH); 
			values.add(logType.getIdPath());
			logType=FlexTypeCache.getFlexTypeFromPath(SMProductOutboundWebServiceConstants.LOG_ENTRY_COLORWAY_SEASON_OUTBOUND_PATH); 
			values.add(logType.getIdPath());

			//adding criteria for log entry nodes.
			prodSeassonStmt.appendInCriteria(new QueryColumn(SMOutboundWebServiceConstants.LCSLOGENTRY,SMOutboundWebServiceConstants.FLEXTYPEIDPATH), values);

			LOGGER.debug("executing query and returning search result");
			//executing  statement
			return LCSQuery.runDirectQuery(prodSeassonStmt);

		}catch(WTException we){
			we.printStackTrace();
		}

		return null;
	}

	/**
	 * THIS METHOD IS responsible in retrieve valid product, colorway, colorway
	 * Season link object from log entry based on PLM id value.
	 * 
	 * @param logEntry - LCSLogEntry.
	 * @return Object
	 */
	public static Object getObjectFromPLMID(LCSLogEntry logEntry){

		String plmIDValue;
		LCSSeason season;
		String prodMasterReferenceValue;
		String seasonIDValue;
		String[] idsArray;

		Object obj = null;

		try{
			LOGGER.debug("retrieving object from log entry for plm id:-"+logEntry.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID));	

			//Retrieving product object.
			if(logEntry.getFlexType().getFullName().equals("smOutbound\\smProduct")) {

				plmIDValue = String.valueOf(logEntry.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID));
				LCSProduct product = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+plmIDValue);
				obj = product;

				//Retrieving colorway object
			}else if(logEntry.getFlexType().getFullName().equals("smOutbound\\smColorway")) {

				plmIDValue = String.valueOf(logEntry.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID));
				obj = (LCSSKU) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:" + plmIDValue);

				//Retrieving colorway season link object
			}else if(logEntry.getFlexType().getFullName().equals("smOutbound\\smColorwaySeasonLinkOutbound")) {

				plmIDValue = String.valueOf(logEntry.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID));
				idsArray = plmIDValue.split("-");
				prodMasterReferenceValue = idsArray[0];
				seasonIDValue = idsArray[1];

				LCSPartMaster partMaster = (LCSPartMaster) LCSProductQuery
						.findObjectById("OR:com.lcs.wc.part.LCSPartMaster:" + prodMasterReferenceValue);
				LCSSKU sku = (LCSSKU) VersionHelper.latestIterationOf(partMaster);

				LCSSeasonMaster seasonMaster = (LCSSeasonMaster) LCSSeasonQuery
						.findObjectById("OR:com.lcs.wc.season.LCSSeasonMaster:" + seasonIDValue);
				season = (LCSSeason) VersionHelper.latestIterationOf(seasonMaster);

				sku = LCSSeasonQuery.getSKUForSeason(sku, season.getMaster());
				LCSSKUSeasonLink ssl = (LCSSKUSeasonLink) SeasonProductLocator.getSeasonProductLink(sku);
				obj = ssl;
			}

			//adding logic to try again if object not found.
		}catch(WTRuntimeException objNotFound){
			LOGGER.warn("Could not restore Season Master !!!!!!!!!!!!!!");
			//calling same method again if error occured.
			String plmIDVal;
			LCSSeason seasonObj;
			String prodMasterReferenceVal;
			String seasonIDVal;
			String[] ids;
			try {

				//Retrieving product object.
				if(logEntry.getFlexType().getFullName().equals("smOutbound\\smProduct")) {

					plmIDVal = String.valueOf(logEntry.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID));
					LCSProduct productObj = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:" + plmIDVal);
					obj = productObj;

					//Retrieving colorway object
				}else if(logEntry.getFlexType().getFullName().equals("smOutbound\\smColorway")) {

					plmIDVal = String.valueOf(logEntry.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID));
					LCSSKU sku = (LCSSKU) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:" + plmIDVal);
					obj = sku;

					//Retrieving colorway season link object
				}else if(logEntry.getFlexType().getFullName().equals("smOutbound\\smColorwaySeasonLinkOutbound")) {

					plmIDVal = String.valueOf(logEntry.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID));
					ids = plmIDVal.split("-");
					prodMasterReferenceVal = ids[0];
					seasonIDVal = ids[1];

					LCSPartMaster partMaster = (LCSPartMaster) LCSProductQuery
							.findObjectById("OR:com.lcs.wc.part.LCSPartMaster:" + prodMasterReferenceVal);
					LCSSKU sku = (LCSSKU) VersionHelper.latestIterationOf(partMaster);

					LCSSeasonMaster seasonMaster = (LCSSeasonMaster) LCSSeasonQuery
							.findObjectById("OR:com.lcs.wc.season.LCSSeasonMaster:" + seasonIDVal);
					seasonObj = (LCSSeason) VersionHelper.latestIterationOf(seasonMaster);

					sku = LCSSeasonQuery.getSKUForSeason(sku, seasonObj.getMaster());
					LCSSKUSeasonLink ssl = (LCSSKUSeasonLink) SeasonProductLocator.getSeasonProductLink(sku);
					obj = ssl;
				}
			}catch (WTException e) {
				LOGGER.error("ERROR FOUND:-",e);
				return null;
			}catch(WTRuntimeException objNotFoundExp){
				LOGGER.warn("Could not restore Season Master??>>>>>", objNotFoundExp);
			}
		} catch (WTException e) {
			LOGGER.error("ERROR FOUND:-",e);
			return null;
		}
		LOGGER.debug("Returning retrivied object:-"+obj);	
		return obj;
	}
}
