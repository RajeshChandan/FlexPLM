/**
 * 
 */
package com.sportmaster.wc.interfaces.webservices.outbound.product.feedback.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.client.SMProductOutboundClient;
import com.sportmaster.wc.interfaces.webservices.outbound.product.helper.SMProductOutboundHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.processor.SMProductOutboundDataProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.product.processor.SMProductOutboundLogEntryProcessor;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMLogEntryUtill;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundRequestXMLGenerator;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundUtil;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductEndpointService;

import wt.util.WTException;
import wt.util.WTRuntimeException;

/**
 * @author BSC
 *
 */
public class SMProductOutboundFeedbackProcessor {

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductOutboundFeedbackProcessor.class);
	/**
	 * Product season link object.
	 */
	/**
	 * Protected constructor.
	 */
	public SMProductOutboundFeedbackProcessor(){
		//protected constructor.
	}

	/**
	 * Process data for Product Season Schedule Queue.
	 */
	public void processProductSeasonOutboundIntegrationScheduleQueue(){
		LOGGER.debug("#############    Starting data processing for Product Season outbound Integartion Schedule Queue    ############");

		Map<String, LCSLogEntry> prodSeasonLinkCollection;
		SMProductOutboundIntegrationBean bean = constructFeedBackQueueBean();
		
		prodSeasonLinkCollection = bean.getProdHelper()
				.queryProductSeasonLinkLogEntryForScheduleQueue(SMProductOutboundWebServiceConstants.LOG_ENTRY_PLM_ID);

		LOGGER.info("getProdSeasonLinkCollection()>>>>>>>>>>"+prodSeasonLinkCollection);
		LOGGER.info("SMProductOutboundLogEntryProcessor.getProductSeasonOutboundLogEntry()>>>>>"
				+ bean.getProductSeasonOutboundLogEntry());

		bean.setProductSeasonOutboundLogEntry(prodSeasonLinkCollection);

		//iterating map.
		if(null != prodSeasonLinkCollection && prodSeasonLinkCollection.size() > 0){
			LOGGER.info(" Log Entry Size   >>>>>>>>>>   "+prodSeasonLinkCollection.size());
			prodSeasonLinkCollection.forEach((key, value) -> {

				LCSLogEntry logEntry;
				try {
					LOGGER.info("entry>>>>>>>>>>>>" + key + "--" + value);
					LOGGER.info("entry values>>>>>>>>>>>>" + key + "--" + value);

					// iterating map.
					logEntry = bean.getProductSeasonOutboundLogEntry().get(key);
					LOGGER.info("logentry>>>>>>" + logEntry);
					if (logEntry != null) {
						// Phase 13 | splitted for sonar fix | added
						// processProductSeasonOutboundIntegration function tot
						// process feedback integration.
						processProductSeasonOutboundIntegration(bean, logEntry, key);
					}
				} catch (WTException we) {
					LOGGER.error("ERROR FOUND:-", we);
				} catch (WTRuntimeException wer) {
					LOGGER.error("ERROR FOUND:-", wer);
				}
			});
		} else {
			LOGGER.info("No log entry found with status as UPDATE_PENDING");
		}

	}

	/**
	 * Phase 13 | added processProductSeasonOutboundIntegration function tot
	 * process feedback integration.
	 * 
	 * @param bean
	 * @param logEntry
	 * @param key
	 * @throws WTException
	 */
	private void processProductSeasonOutboundIntegration(SMProductOutboundIntegrationBean bean,
			LCSLogEntry logEntry, String key) throws WTException {

		LCSProduct product;
		List<String> lcStates;
		String lifecycleStates;
		LCSProductSeasonLink psl;
		LOGGER.info("logentry name>>>>>>" + logEntry.getName());
		// getting lifecycle states.
		lifecycleStates = String.valueOf(logEntry.getValue(SMProductOutboundWebServiceConstants.LOG_ENTRY_LIFECYCLE_STATES));
		LOGGER.info("fetched lifecycle states>>>>" + lifecycleStates);
		lcStates = FormatHelper.commaSeparatedListToList(lifecycleStates);
		LOGGER.info("PLM ID  from Log Entry  >>>>>>>>>>   " + key);
		// set product season link.
		psl = bean.getProdHelper().getProductSeasonLinkFromPLMID(key);
		if (null != psl) {
			bean.setProductSeasonLink(psl);
			bean.setProductSeasonLinkMDMID((String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID));
			product = SeasonProductLocator.getProductARev(psl);
			product = (LCSProduct) VersionHelper.latestIterationOf(product);
			for (String lifecyclestate : lcStates) {
				if (!"INWORK".equalsIgnoreCase(lifecyclestate)) {
					bean.setLifecycleState(lifecyclestate);
					LOGGER.debug("Sending data for Product Season Lifecycle state ----->  " + bean.getLifecycleState());
					// validate product object and product
					// season.
					new SMProductOutboundClient().productOutboundRequest(product, true, bean);
				}
			}
		}
	}

	/**
	 * constructs SMProductOutboundIntegrationBean bean object for feedback
	 * process.
	 * 
	 * @return
	 */
	private SMProductOutboundIntegrationBean constructFeedBackQueueBean() {

		SMProductOutboundIntegrationBean beanObject = new SMProductOutboundIntegrationBean();

		beanObject.setProdUtill(new SMProductOutboundUtil());
		beanObject.setLogentryUtil(new SMLogEntryUtill());
		beanObject.setLogEntryProcessor(new SMProductOutboundLogEntryProcessor());
		beanObject.setProdProcessor(new SMProductOutboundDataProcessor());
		beanObject.setProdHelper(new SMProductOutboundHelper());
		beanObject.setProdUtill(new SMProductOutboundUtil());
		beanObject.setXmlUtill(new SMProductOutboundRequestXMLGenerator());
		beanObject.setProdOutboundWS(new ProductEndpointService().getProductEndpointPort());
		beanObject.setProductSeasonOutboundLogEntry(new HashMap<>());
		beanObject.setProductSeasonOutboundMDMIDLogEntry(new HashMap<>());
		beanObject.setColorwaySeasonOutboundLogEntry(new HashMap<>());
		beanObject.setProductOutboundLogEntry(new HashMap<>());
		beanObject.setColorwayOutboundLogEntry(new HashMap<>());

		return beanObject;
	}
}
