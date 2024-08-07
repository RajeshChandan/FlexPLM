/**
 *
 */
package com.sportmaster.wc.interfaces.webservices.inbound.product.util;

import java.util.List;
import java.util.Objects;

import org.apache.log4j.Logger;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSLogEntryLogic;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.SortHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.inbound.product.client.SMProductInboundDataRequestWebClient;

import wt.util.WTException;
import wt.util.WTRuntimeException;

/**
 * @author Carrier
 *
 */
public class SMProductInboundUtil {

	private static final String ERROR_OCCURED = "ERROR OCCURED :-";
	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductInboundUtil.class);
	/**
	 * Setting default initial value of Request ID.
	 */
	public static final int REQUEST_ID_INITIAL = Integer
			.parseInt(SMProductInboundWebServiceConstants.PRODUCT_INBOUND_INTEGRATION_REQUEST_ID);
	/**
	 * Protected constructor.
	 */
	public SMProductInboundUtil(){
		//protected constructor
	}

	/**
	 * Returns Prod-SeasonLink after validation
	 * @param prodSeasonLinkPLMID - String
	 * @return LCSProductSeasonLink
	 */
	public static LCSProductSeasonLink getProductSeasonLink(String prodSeasonLinkPLMID){
		try {
			LOGGER.info("Product Season Link  PLM ID received  >>>>>>  "+prodSeasonLinkPLMID);
			LCSProductSeasonLink spl = SMProductInboundUtil.getProductSeasonLinkFromPLMID(prodSeasonLinkPLMID);

			if(null != spl){
				LCSProduct productSeasonRev = SeasonProductLocator.getProductSeasonRev(spl);
				//Get latest iteration.
				productSeasonRev = (LCSProduct) VersionHelper.latestIterationOf(productSeasonRev);
				//get Season-product Link.
				LCSProductSeasonLink latestSPL = (LCSProductSeasonLink) SeasonProductLocator.getSeasonProductLink(productSeasonRev);

				if(null != latestSPL && !latestSPL.isSeasonRemoved()){
					return latestSPL;
				}
			}
		} catch(WTException wtExcpt) {
			LOGGER.error(wtExcpt.getLocalizedMessage());
			wtExcpt.printStackTrace();
		}catch(WTRuntimeException wtRunTime){
			wtRunTime.printStackTrace();
		}
		return null;
	}

	/**
	 * Generates Product inbound Request ID.
	 * @return int.
	 * @throws WTException - WTException
	 */
	public int  getProductInboundRequestID() throws WTException{
		int producInboundtRequestID = REQUEST_ID_INITIAL;
		String lcsLogEntry=SMProductInboundWebServiceConstants.LCSLOGENTRY;
		FlexType inboundLogType = FlexTypeCache.getFlexTypeFromPath(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_PATH);
		FlexTypeAttribute att = inboundLogType.getAttribute(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_PRODUCT_SEASON_LINK_REQUEST_ID);
		SearchResults results = null;
		PreparedQueryStatement statement = new PreparedQueryStatement();//Creating Statement.

		statement.appendFromTable(lcsLogEntry);
		statement.appendSelectColumn(new QueryColumn(lcsLogEntry, "idA2A2"));
		statement.appendSelectColumn(lcsLogEntry, att.getColumnName());
		//add tables
		statement.appendFromTable(lcsLogEntry);

		statement.appendCriteria(new Criteria(lcsLogEntry, "flexTypeIdPath", inboundLogType.getIdPath(),Criteria.EQUALS));
		statement.appendOrIfNeeded();
		inboundLogType = FlexTypeCache.getFlexTypeFromPath(SMProductInboundWebServiceConstants.LOG_ENTRY_INBOUND_COLORWAY_SEASON_LINK_INTEGRATION_PATH);
		statement.appendCriteria(new Criteria(lcsLogEntry, "flexTypeIdPath", inboundLogType.getIdPath(),Criteria.EQUALS));

		results=LCSQuery.runDirectQuery(statement);
		@SuppressWarnings("unchecked")
		List<FlexObject> data=results.getResults();
		int reqId;
		data=(List<FlexObject>) SortHelper.sortFlexObjects(data, lcsLogEntry+"."+att.getColumnName());

		if(!data.isEmpty()){
			reqId=data.get(data.size()-1).getInt( lcsLogEntry+"."+att.getColumnName());
			producInboundtRequestID =reqId+1;
			return producInboundtRequestID;
		}
		return producInboundtRequestID+1;
	}

	/**
	 * Returns Colorway-Season Link after validation.
	 * @param colorwaySeasonLinkPLMID - String
	 * @return
	 */
	public static LCSSKUSeasonLink getColorwaySeasonLink(String colorwaySeasonLinkPLMID){
		try {
			LOGGER.info("SKU PLM ID --------------->   "+colorwaySeasonLinkPLMID);
			//get SKU_Season Link.
			LCSSKUSeasonLink ssl = SMProductInboundUtil.getSKUSeasonLinkFromPLMID(colorwaySeasonLinkPLMID);
			if( null != ssl && !ssl.isSeasonRemoved()){
				//get SKU-Season rev.
				LCSSKU skuSeasonRev = SeasonProductLocator.getSKUSeasonRev(ssl);
				//Get latest iteration.
				skuSeasonRev = (LCSSKU) VersionHelper.latestIterationOf(skuSeasonRev);
				//Get latest SKU-Season link.
				LCSSKUSeasonLink latestSSL = (LCSSKUSeasonLink) SeasonProductLocator.getSeasonProductLink(skuSeasonRev);
				if(latestSSL != null && !latestSSL.isSeasonRemoved()){
					return latestSSL;
				}
			}

		} catch (WTException wtExcpt) {
			LOGGER.error(wtExcpt.getLocalizedMessage());
			wtExcpt.printStackTrace();
		}
		return null;
	}

	/**
	 * Persisting Product Season Link Object.
	 * @param productSeasonLink - LCSProductSeasonLink
	 */
	public static void persistProductSeasonLink(LCSProductSeasonLink productSeasonLink){
		try {
			LCSLogic.deriveFlexTypeValues(productSeasonLink, true);
			LCSLogic.persist(productSeasonLink, true);
		} catch (WTException ex) {
			LOGGER.error("ERROR in saving LCSProductSeasonLink Object  !!!!  "+ex.getLocalizedMessage());
			ex.printStackTrace();
			SMProductInboundDataRequestWebClient.setIntegartionFailurePSL(true);
		}
	}

	/**
	 * Persisting SKU Season Link Object.
	 * @param skuSeasonLink - LCSSKUSeasonLink
	 */
	public static void persistColorwaySeasonLink(LCSSKUSeasonLink skuSeasonLink){
		try {
			LCSLogic.deriveFlexTypeValues(skuSeasonLink, true);
			LCSLogic.persist(skuSeasonLink, true);
		} catch (WTException exp) {
			LOGGER.error("ERROR in saving LCSSKUSEASONLINK Object  !!!!  "+exp.getLocalizedMessage());
			exp.printStackTrace();
			SMProductInboundDataRequestWebClient.setIntegrationFailureSSL(true);
		}
	}

	/**
	 * Persists log entry object.
	 * @param logEntryObj - LCSLogEntry
	 */
	public static void persistInboundLogEntry(LCSLogEntry logEntryObj){
		LCSLogEntryLogic logEntryInboundLogic = new LCSLogEntryLogic();
		try {
			LOGGER.info("Persisting log entry object !!!!");
			//Save Log Entry object
			logEntryInboundLogic.saveLog(logEntryObj, true);
		} catch (WTException e) {
			LOGGER.error("ERROR in persisting Log Entry Object !!!!!!  "+e.getLocalizedMessage());
			e.printStackTrace();
		}
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
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Getting SKU Season Link Details.
	 * @param skuSeasonLink
	 * @return
	 */
	public static String skuSeasonLinkDetails(LCSSKUSeasonLink skuSeasonLink){
		try{
			//Getting SKU Oject.
			LCSSKU skuobj =(LCSSKU) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:"+(int)skuSeasonLink.getSkuARevId());
			//Getting latest iteration of LCSSKU obj.
			skuobj = (LCSSKU) VersionHelper.latestIterationOf(skuobj);
			//Getting Season object.
			LCSSeason season = (LCSSeason) LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:"+(int)skuSeasonLink.getSeasonRevId());
			//Getting product.
			LCSProduct prod = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+(int)skuSeasonLink.getProductARevId());
			//getting latest iteration of Product.
			prod = (LCSProduct) VersionHelper.latestIterationOf(prod);

			return season.getName()+" , "+prod.getName()+" , "+skuobj.getName();

		}catch(WTException exp){
			exp.printStackTrace();
			return null;
		}

	}

	/**
	 * Get product Season link from PLM ID.
	 * @param plmID
	 * @return
	 */
	public static LCSProductSeasonLink getProductSeasonLinkFromPLMID(String plmID){
		LOGGER.info("PLM ID received for PSL  ###################   "+plmID);
		LCSSeason season;
		LCSProduct prod = null;
		String prodMasterReference;
		String seasonID;
		String[] ids = plmID.split("-");
		LOGGER.info("Array size >>>>>>>>  "+ids.length);
		if(ids.length == 2){
			prodMasterReference = ids[0];
			seasonID = ids[1];
			try{
				//getting Part master.
				LCSPartMaster partMaster = (LCSPartMaster) LCSQuery.findObjectById("OR:com.lcs.wc.part.LCSPartMaster:"+prodMasterReference);
				//getting latest product iteration.
				LCSProduct product = (LCSProduct) VersionHelper.latestIterationOf(partMaster);
				prod = product;
				//getting season master.
				LCSSeasonMaster seasonMaster = (LCSSeasonMaster) LCSQuery.findObjectById("OR:com.lcs.wc.season.LCSSeasonMaster:"+seasonID);
				//getting season.
				season = (LCSSeason) VersionHelper.latestIterationOf(seasonMaster);
				return (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(product, season);
			}catch(WTRuntimeException wtRunTime){
				LOGGER.debug("Could Not restore Season Master ................!!!!! ");
				LOGGER.debug("Product ***************   " + prod);
				try{
					//getting season from IDA2A2.
					season = (LCSSeason) LCSQuery.findObjectById("OR:com.lcs.wc.season.LCSSeason:"+seasonID);
					//getting latest iteration for season.
					season = (LCSSeason) VersionHelper.latestIterationOf(season);
					LOGGER.info("Season  ****************  "+season.getName());
					if (Objects.nonNull(prod)) {
						return (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(prod, season);
					}else{
						return null;
					}
				} catch (WTException | WTRuntimeException wex) {
					LOGGER.error(ERROR_OCCURED, wex);
					return null;
				}
			}catch(WTException e){
				LOGGER.error(ERROR_OCCURED, e);
				return null;
			}
		}else{
			return null;
		}

	}

	/**
	 * Get sku Season link from PLM ID.
	 * @param plmID
	 * @return
	 */
	public static LCSSKUSeasonLink getSKUSeasonLinkFromPLMID(String plmID){
		LOGGER.info("PLM ID received for SSL  ###################   "+plmID);
		LCSSeason season;
		LCSSKU colorway = null;
		String skuMasterReference;
		String seasonID;
		//splitting PLM IDs
		String[] ids = plmID.split("-");
		LOGGER.info("Array size >>>>>>>>  "+ids.length);
		if(ids.length == 2){
			skuMasterReference = ids[0];
			seasonID = ids[1];
			try{
				//getting SK Master.
				LCSPartMaster partMaster = (LCSPartMaster) LCSQuery.findObjectById("OR:com.lcs.wc.part.LCSPartMaster:"+skuMasterReference);
				//getting latest iteration of SKU.
				LCSSKU sku = (LCSSKU) VersionHelper.latestIterationOf(partMaster);
				colorway = sku;
				//Getting season master.
				LCSSeasonMaster seasonMaster = (LCSSeasonMaster) LCSQuery.findObjectById("OR:com.lcs.wc.season.LCSSeasonMaster:"+seasonID);
				//getting season.
				season = (LCSSeason) VersionHelper.latestIterationOf(seasonMaster);
				return (LCSSKUSeasonLink) LCSSeasonQuery.findSeasonProductLink(sku, season);
			}catch(WTRuntimeException wtRunTime){
				LOGGER.debug("Could not restore Season Master ............... !!!!!");
				try{
					//getting season from IDA2A2.
					season = (LCSSeason) LCSQuery.findObjectById("OR:com.lcs.wc.season.LCSSeason:"+seasonID);
					//getting latest iteration.
					season = (LCSSeason) VersionHelper.latestIterationOf(season);
					LOGGER.info("Season >>>>>>>>>    " + season.getName());
					if(null != colorway){
						return (LCSSKUSeasonLink) LCSSeasonQuery.findSeasonProductLink(colorway, season);
					}else{
						return null;
					}
				} catch (WTException | WTRuntimeException wexp) {
					LOGGER.error(ERROR_OCCURED, wexp);
					return null;
				}
			}catch(WTException e){
				LOGGER.error(ERROR_OCCURED, e);
				return null;
			}
		}else{
			return null;
		}

	}
}

