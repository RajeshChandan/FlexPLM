package com.sportmaster.wc.interfaces.webservices.outbound.product.processor;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.interfaces.queue.service.SMProcessingQueueService;
import com.sportmaster.wc.interfaces.webservices.outbound.product.client.SMProductOutboundClient;
import com.sportmaster.wc.interfaces.webservices.outbound.product.helper.SMProductOutboundHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMLogEntryUtill;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundRequestXMLGenerator;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundUtil;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductEndpointService;

import wt.fc.WTObject;
import wt.util.WTException;

public class SMLifeCylcePluginProcessor {

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMLifeCylcePluginProcessor.class);

	/**
	 * Trigger plugin on lifecycle state change.
	 * @param obj - WTObject
	 * @throws SQLException 
	 */
	public void triggerStaeChange(WTObject obj) throws SQLException{
		try{
			
			SMProductOutboundIntegrationBean bean = constructLifeCycleBean();
			SMProductOutboundClient client = new SMProductOutboundClient();
			
			if(obj instanceof LCSProduct){
				LCSProduct product = (LCSProduct) obj;
				//trigger plugin when product lifecycle state to Cancelled.
				if ("A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())
						&& bean.getProdUtill().validateActiveDepartment(product)) {

					if (product.getLifeCycleState().toString()
							.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CANCELLED)
							&& FormatHelper.hasContent((String) product
									.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY))) {

						LOGGER.debug("Product Lifecycle State set to CANCELLED, Starting web client for Product");
						// trigger outbound web service request.
						client.productOutboundRequest(product, false, bean);
					}
				}else{

					//trigger plugin on product season lifecycle state change.
					//update log entry for PRODUCT SEASON.
					triggerLifecyclePluginForPSL(product, bean);

				}

			}else if(obj instanceof LCSSKU){
				triggerLifecyclePluginForSKU(obj,  bean, client);
			}
		}catch(WTException we){
			LOGGER.error(we.getLocalizedMessage(), we);
		}
	}
	/**
	 * @param product
	 * @throws WTException
	 * @throws SQLException
	 */
	private void triggerLifecyclePluginForPSL(LCSProduct product, SMProductOutboundIntegrationBean bean)
			throws WTException, SQLException {

		Map<String, String> lifeCyclePSLMap;
		LCSProductSeasonLink psl = (LCSProductSeasonLink) SeasonProductLocator.getSeasonProductLink(product);
		LOGGER.info("Product Revision  *******   >>>>>>>>>>>>  ************  "+product.getVersionDisplayIdentifier());
		if(bean.getProdUtill().validateActiveDepartment(psl) && !"INWORK".equalsIgnoreCase(product.getLifeCycleState().toString())){
			LOGGER.info("LIFE CYCLE STATE #######################################   "+product.getLifeCycleState());

			//check if MDM ID is present.
			if(FormatHelper.hasContent((String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID))){

				LOGGER.debug("Product Season Lifecycle State is changed ---- Set data to processing queue and update log entry");
				lifeCyclePSLMap= new HashMap<>();
				final Class<?>[] prodSeaonArgTypesx = { Map.class };
				final Object[] prodSeasonArgValuex = { lifeCyclePSLMap };

				//add to map.
				if (FormatHelper.hasContent(String.valueOf(
						psl.getValue(SMProductOutboundWebServiceConstants.AD_HOC_PLM_ID_PRODUCT_SEASON_LINK)))) {

					LOGGER.info("Setting Ad HOC PLM ID value in Process Queue ..........");
					lifeCyclePSLMap.put(
							(String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID),
							String.valueOf(psl
									.getValue(SMProductOutboundWebServiceConstants.AD_HOC_PLM_ID_PRODUCT_SEASON_LINK)));

				}else{
					lifeCyclePSLMap.put(
							(String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID),
							bean.getProdHelper().getProductMasterReferencefromLink(psl));
				}

				//process on lifecycle change.
				processLifeCycleChangesOnProductSeason(prodSeaonArgTypesx, prodSeasonArgValuex);
			}
		}
	}
	/**
	 * @param obj
	 * @throws WTException
	 */
	private void triggerLifecyclePluginForSKU(WTObject obj, SMProductOutboundIntegrationBean bean, SMProductOutboundClient client)
			throws WTException {
		LCSSKU sku = (LCSSKU) obj;
		//trigger plugin on colorway object on cancelled state.
		if("A".equalsIgnoreCase(sku.getVersionDisplayIdentifier().toString()) && bean.getProdUtill().validateActiveDepartment(sku)){
			//check lifecycle state.
			if (FormatHelper.hasContent((String) sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID))
					&& sku.getLifeCycleState().toString()
					.equalsIgnoreCase(SMProductOutboundWebServiceConstants.CANCELLED)) {

				LOGGER.error("Colorway Lifecycle State set to CANCELLED   -----  Starting web client for Colorway");
				// trigger web service request for colorway.
				client.productOutboundRequest(sku, false, bean);
			}
		}else{
			//trigger plugin for LCSSKU Season on cancelled.
			LCSSKUSeasonLink ssl = (LCSSKUSeasonLink) SeasonProductLocator.getSeasonProductLink(sku);
			if(bean.getProdUtill().validateActiveDepartment(ssl)){
				if(sku.getLifeCycleState().toString().equalsIgnoreCase(SMProductOutboundWebServiceConstants.CANCELLED) && FormatHelper.hasContent((String) ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_MDM_ID))){
					LOGGER.error("Colorway Season Lifecycle State set to CANCELLED   -----  Starting web client for Colorway Season");
					//trigger web service request for sku season link.
					client.productOutboundRequest(ssl, false, bean);
				}
			}
		}
	}
	/**
	 * processing on life cycle change.
	 * @param prodSeaonArgTypesx
	 * @param prodSeasonArgValuex
	 * @throws SQLException 
	 * @throws WTException 
	 */
	public static void processLifeCycleChangesOnProductSeason(Class<?>[] prodSeaonArgTypesx,
			Object[] prodSeasonArgValuex) throws WTException, SQLException {

		//Updated for phasge -8 3.8.1.0 build
		//adding entry for process queue.
		SMProcessingQueueService.addQueueEntry(
				SMProductOutboundWebServiceConstants.PRODUCT_OUTBOUND_INTEGRATION_PROCESSING_QUEUE, prodSeaonArgTypesx,
				prodSeasonArgValuex, "processProductSeasonDataFromProcessingQueue",
				"com.sportmaster.wc.interfaces.webservices.outbound.product.processor.SMProcessingQueueEntryProcessor");

	}

	private SMProductOutboundIntegrationBean constructLifeCycleBean() {

		SMProductOutboundIntegrationBean lifeCycleBean = new SMProductOutboundIntegrationBean();

		lifeCycleBean.setProdUtill(new SMProductOutboundUtil());
		lifeCycleBean.setLogentryUtil(new SMLogEntryUtill());
		lifeCycleBean.setLogEntryProcessor(new SMProductOutboundLogEntryProcessor());
		lifeCycleBean.setProdProcessor(new SMProductOutboundDataProcessor());
		lifeCycleBean.setProdHelper(new SMProductOutboundHelper());
		lifeCycleBean.setProdUtill(new SMProductOutboundUtil());
		lifeCycleBean.setXmlUtill(new SMProductOutboundRequestXMLGenerator());
		lifeCycleBean.setProdOutboundWS(new ProductEndpointService().getProductEndpointPort());
		lifeCycleBean.setProductSeasonOutboundLogEntry(new HashMap<>());
		lifeCycleBean.setProductSeasonOutboundMDMIDLogEntry(new HashMap<>());
		lifeCycleBean.setColorwaySeasonOutboundLogEntry(new HashMap<>());
		lifeCycleBean.setProductOutboundLogEntry(new HashMap<>());
		lifeCycleBean.setColorwayOutboundLogEntry(new HashMap<>());

		return lifeCycleBean;
	}
}
