package com.sportmaster.wc.interfaces.webservices.outbound.product.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.client.SMProductOutboundClient;
import com.sportmaster.wc.interfaces.webservices.outbound.product.helper.SMProductOutboundHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMLogEntryUtill;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundRequestXMLGenerator;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundUtil;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductEndpointService;

import wt.util.WTException;

/**
 * SMProcessingQueueEntryProcessor.java
 * This class used to process product season object data frfom processing queue.
 *
 * @author 'true' Rajesh Chandan
 * @version 'true' 1.0 version number
 */
public class SMProcessingQueueEntryProcessor {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProcessingQueueEntryProcessor.class);
	/**
	 * product class
	 */
	private static final String LCSPRODUCT_CLASS = "VR:com.lcs.wc.product.LCSProduct:";

	/**
	 * constructor.
	 */
	protected SMProcessingQueueEntryProcessor() {
	}

	/**
	 * Process data from Processing Queue.
	 * @param productSeasonLinkMap - Map<String, String>
	 * @throws WTException - WTException
	 */
	public static void processProductSeasonDataFromProcessingQueue(Map<String, String> productSeasonLinkMap) throws WTException{
		
		
		SMProductOutboundIntegrationBean bean = new SMCostructBean().constructProcessingQueueBean();
		
		Entry<String, String> prodSeasonMapEntry = productSeasonLinkMap.entrySet().iterator().next();

		//setting product season link.
		LOGGER.debug("PLM ID in Map >>>>      "+prodSeasonMapEntry.getValue());
		LCSProductSeasonLink psl = bean.getProdHelper().getProductSeasonLinkFromPLMID(prodSeasonMapEntry.getValue());

		if(null != psl){
			LCSProduct product = (LCSProduct) LCSQuery.findObjectById(LCSPRODUCT_CLASS+(int)psl.getProductSeasonRevId());
			product = (LCSProduct) VersionHelper.latestIterationOf(product);

			//geting product life cycle state.
			String lifeCycleState = String.valueOf(product.getLifeCycleState());
			//ignoring inwork Life cycle statte fom integration trigger.
			if(!"INWORK".equalsIgnoreCase(lifeCycleState)){

				product = SeasonProductLocator.getProductARev(psl);
				product = (LCSProduct) VersionHelper.latestIterationOf(product);

				bean.setProductSeasonLinkMDMID(
						(String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID));
				bean.setProductSeasonLink(psl);

				LCSLogEntry logEntry = bean.getProdHelper().queryProductSeasonOutboundLogEntry(
						SMProductOutboundWebServiceConstants.LOG_ENTRY_MDM_ID, bean.getProductSeasonLinkMDMID());


				if(logEntry!=null) {
					
					bean.getProductSeasonOutboundLogEntry().put(
							String.valueOf(logEntry.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID)),
							logEntry);
					LOGGER.debug("added found log entry to productSeasonOutboundLogEntry colection for plm id>>"
							+ logEntry.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID));
					LOGGER.debug("final collection data>>>"+bean.getProductSeasonOutboundLogEntry());

				}else {
					LOGGER.debug("Log entry not found, creating it");
					bean.getLogEntryProcessor().createLogEntryForProductSeason(prodSeasonMapEntry.getValue(),
							SMProductOutboundWebServiceConstants.LIFECYCLE_UPDATE_REQUEST, psl, bean);
				}
				bean.setLifecycleState(lifeCycleState);
				LOGGER.debug("Sending data for Product Season Lifecycle state ----->  "+lifeCycleState);
				//sending product season data to plm gate.
				new SMProductOutboundClient().productOutboundRequest(product, true, bean);

			}
		}else {
			LOGGER.debug("unable to find Prouct seazson object from plm id :-"+prodSeasonMapEntry.getValue());
		}
	}
}

class SMCostructBean{
	
	SMProductOutboundIntegrationBean constructProcessingQueueBean() {

		SMProductOutboundIntegrationBean processingQueuebean = new SMProductOutboundIntegrationBean();

		processingQueuebean.setProdUtill(new SMProductOutboundUtil());
		processingQueuebean.setLogentryUtil(new SMLogEntryUtill());
		processingQueuebean.setLogEntryProcessor(new SMProductOutboundLogEntryProcessor());
		processingQueuebean.setProdProcessor(new SMProductOutboundDataProcessor());
		processingQueuebean.setProdHelper(new SMProductOutboundHelper());
		processingQueuebean.setProdUtill(new SMProductOutboundUtil());
		processingQueuebean.setXmlUtill(new SMProductOutboundRequestXMLGenerator());
		processingQueuebean.setProdOutboundWS(new ProductEndpointService().getProductEndpointPort());
		processingQueuebean.setProductSeasonOutboundLogEntry(new HashMap<>());
		processingQueuebean.setProductSeasonOutboundMDMIDLogEntry(new HashMap<>());
		processingQueuebean.setColorwaySeasonOutboundLogEntry(new HashMap<>());
		processingQueuebean.setProductOutboundLogEntry(new HashMap<>());
		processingQueuebean.setColorwayOutboundLogEntry(new HashMap<>());

		return processingQueuebean;
	}

}
