package com.sportmaster.wc.interfaces.webservices.outbound.product.processor;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSQuery;
import com.sportmaster.wc.interfaces.webservices.outbound.product.client.SMProductOutboundClient;
import com.sportmaster.wc.interfaces.webservices.outbound.product.helper.SMProductOutboundHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMCancelRequestUtil;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMLogEntryUtill;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundRequestXMLGenerator;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundUtil;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductEndpointService;

import wt.util.WTException;

/**
 * SMCancelRequestProcessor.java
 * This class used to process CANCEL Request data retrevied from log entry object.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMCancelRequestProcessor {


	/*
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMCancelRequestProcessor.class);


	/**
	 * this method queries product, colorway, colorway season link log entry object
	 * whose status is 'CANCEL_PENDING', and process retrieved data on PLM Gate one by one.
	 * on successful transaction respective log entry status value will be changed to 'CANCEL_PROCESSED'.
	 */
	public void executeQueueEntry() {

		LOGGER.debug("calling query method.");
		//quering 'cancel_pendding' log entries.
		SearchResults results= new SMCancelRequestUtil().getCancelRequestData();

		if(results != null) {
			List<?> resultColl=results.getResults();

			if(resultColl == null || resultColl.isEmpty()) {

				LOGGER.debug("NO Results found to process.");
				return;
			}

			LOGGER.debug("total record count with status CANCELED_PENDING retrived is :-"+resultColl.size());
			//Iterating retrieved log entry data.
			resultColl.stream().forEach(entry -> {
				//converting to Flex Object.
				FlexObject fo=(FlexObject) entry;

				try {
					
					SMProductOutboundIntegrationBean bean = constructCancelIntegrationBean();
					//log entry object.
					LCSLogEntry logEntryObj = (LCSLogEntry) LCSQuery
							.findObjectById("com.lcs.wc.foundation.LCSLogEntry:" + fo.getString("LCSLOGENTRY.IDA2A2"));

					bean.setCancelRequest(true);

					LOGGER.debug("sending caneled request for log entry having PLM ID :-"
							+ logEntryObj.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID));
					// sending cancel request to PLM Gate.
					new SMProductOutboundClient().productOutboundRequest(
							SMCancelRequestUtil.getObjectFromPLMID(logEntryObj), false, bean);

					bean.setCancelRequest(false);

				} catch (WTException e) {
					LOGGER.error("ERROR FOUND:-", e);
				}
			});

		}else {
			//looger
			LOGGER.debug("DB Reuruens Search Result as null");
		}
	}

	private SMProductOutboundIntegrationBean constructCancelIntegrationBean() {

		SMProductOutboundIntegrationBean canclReqBean = new SMProductOutboundIntegrationBean();

		canclReqBean.setProdUtill(new SMProductOutboundUtil());
		canclReqBean.setLogentryUtil(new SMLogEntryUtill());
		canclReqBean.setLogEntryProcessor(new SMProductOutboundLogEntryProcessor());
		canclReqBean.setProdProcessor(new SMProductOutboundDataProcessor());
		canclReqBean.setProdHelper(new SMProductOutboundHelper());
		canclReqBean.setProdUtill(new SMProductOutboundUtil());
		canclReqBean.setXmlUtill(new SMProductOutboundRequestXMLGenerator());
		canclReqBean.setProdOutboundWS(new ProductEndpointService().getProductEndpointPort());
		canclReqBean.setProductSeasonOutboundLogEntry(new HashMap<>());
		canclReqBean.setProductSeasonOutboundMDMIDLogEntry(new HashMap<>());
		canclReqBean.setColorwaySeasonOutboundLogEntry(new HashMap<>());
		canclReqBean.setProductOutboundLogEntry(new HashMap<>());
		canclReqBean.setColorwayOutboundLogEntry(new HashMap<>());

		return canclReqBean;
	}
}
