package com.sportmaster.wc.interfaces.webservices.outbound.product.processor;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductLogic;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.ProductHeaderQuery;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.client.SMProductOutboundClient;
import com.sportmaster.wc.interfaces.webservices.outbound.product.helper.SMProductOutboundHelper;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMLogEntryUtill;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundIntegrationBean;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundRequestXMLGenerator;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundUtil;
import com.sportmaster.wc.interfaces.webservices.outbound.product.util.SMProductOutboundWebServiceConstants;
import com.sportmaster.wc.interfaces.webservices.productbean.ProductEndpointService;

import wt.fc.WTObject;
import wt.method.MethodContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class SMProductPluginProcessor {

	private static final String MASS_COPY_PRODUCTS = "MASS_COPY_PRODUCTS";
	private static final String FALSE = "false";
	private static final String ADD_SKUS = "ADD_SKUS";
	/**
	 * LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMProductPluginProcessor.class);
	/**
	 * product class
	 */
	private static final String LCSPRODUCT_CLASS = "VR:com.lcs.wc.product.LCSProduct:";

	public SMProductPluginProcessor() {
		super();
	}

	/**
	 * @param obj
	 * @throws WTException
	 */
	public void triggerMDMRequestOnColorwaySeason(WTObject obj) throws WTException {
		SMProductOutboundIntegrationBean bean = constructIntegrationBean();
		SMProductOutboundClient client = new SMProductOutboundClient();

		String ativity = (String) MethodContext.getContext().get("activity");
		String massCopyProduct = (String) MethodContext.getContext().get(MASS_COPY_PRODUCTS);
		String addSkus = (String) MethodContext.getContext().get(ADD_SKUS);
		LOGGER.info("Activity SSL  >>>>>>>>>>    " + ativity);
		// trigger for sku season link
		LCSSKUSeasonLink ssl = (LCSSKUSeasonLink) obj;
		LOGGER.info("SSL Effective Sequence   >>>  " + ssl.getEffectSequence() + "    *******  SKU Season carried over from ??? "
				+ ssl.getCarriedOverFrom());
		LOGGER.debug("ssl.getCarriedOverFrom()  >>>>>>>>>>    " + ssl.getCarriedOverFrom());
		LOGGER.debug(" ssl.getMovedFrom() >>>>>>>>>>    " + ssl.getMovedFrom());
		LOGGER.debug(" ssl.isSeasonRemoved() >>>>>>>>>>    " + ssl.isSeasonRemoved());
		// Handle Copied product sequence
		if ("true".equals(String.valueOf(MethodContext.getContext().get(LCSProductLogic.COPY_IN_PROGRESS)))
				&& !MASS_COPY_PRODUCTS.equals(massCopyProduct)) {
			return;
		}
		if (!bean.getProdUtill().validateActiveDepartment(ssl)
				|| FormatHelper.hasContent((String) ssl.getValue(SMProductOutboundWebServiceConstants.COLORWAY_SEASON_LINK_MDM_ID))
				|| ssl.isSeasonRemoved()) {
			return;
		}

		if ((null == ssl.getCarriedOverFrom() && null == ssl.getMovedFrom()) || MASS_COPY_PRODUCTS.equals(massCopyProduct)) {

			// calling client if MDM ID is not present for Colorway Season Link.
			// Phase 13 | splitted fro Sonar fix - created
			// triggerColrowayRequest to trigger request for colorway season
			// object.
			triggerColrowayRequest(ssl, client, bean, addSkus, ativity);

		} else if (null != ssl.getMovedFrom() && !ssl.isSeasonRemoved()) {

			LOGGER.debug("Sending MDM ID request for moved colorways .............");
			client.productOutboundRequest(ssl, false, bean);

		} else if (null != ssl.getCarriedOverFrom()) {
			LOGGER.debug("Sending MDM ID request for Colorway Season Link on Carryover  ......");
			client.productOutboundRequest(ssl, false, bean);
		}

	}
	/**
	 * Phase 13 | calling client if MDM ID is not present for Colorway Season
	 * Link.
	 * 
	 * @param ssl
	 * @param client
	 * @param bean
	 * @param addSkus
	 * @param ativity
	 */
	private void triggerColrowayRequest(LCSSKUSeasonLink ssl, SMProductOutboundClient client, SMProductOutboundIntegrationBean bean,
			String addSkus, String ativity) {

		// calling client if MDM ID is not present for Colorway Season Link.
		if (ssl.getEffectSequence() != 0 || (ssl.getEffectSequence() == 0 && ADD_SKUS.equals(addSkus))) {
			LOGGER.debug("Sending MDM ID request for Colorway Season Link ......");
			client.productOutboundRequest(ssl, false, bean);
		} else if (ssl.getEffectSequence() == 0 && "CREATE_SKUS".equalsIgnoreCase(ativity)) {
			LOGGER.debug("Sending MDM ID request for multiple colorways .............");
			client.productOutboundRequest(ssl, false, bean);
		}
	}

	/**
	 * @param obj
	 * @throws WTException
	 * @throws SQLException
	 * @throws WTPropertyVetoException 
	 */
	public void triggerRequestForProductSeasonLink(WTObject obj) throws WTException, SQLException, WTPropertyVetoException {

		SMProductOutboundIntegrationBean bean = constructIntegrationBean();

		// trigger for product season link.
		String massCopyProduct = (String) MethodContext.getContext().get(MASS_COPY_PRODUCTS);


		LCSProductSeasonLink psl = (LCSProductSeasonLink) obj;

		boolean isCopyProduct = FormatHelper
				.parseBoolean(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.IS_COPY_PRODUCT)));

		// Handle Copied product sequence
		if ("true".equals(MethodContext.getContext().get(LCSProductLogic.COPY_IN_PROGRESS))
				&& !MASS_COPY_PRODUCTS.equals(massCopyProduct)) {
			psl.setValue(SMProductOutboundWebServiceConstants.IS_COPY_PRODUCT, "true");
			bean.getProdUtill().persistProductSeasonLinkObject(psl);
			return;
		}

		LOGGER.info("PSL Effective Sequence   >>>  " + psl.getEffectSequence() + "   ::::  Carried over season >>> "
				+ psl.getCarriedOverFrom());

		if (!bean.getProdUtill().validateActiveDepartment(psl) || psl.isSeasonRemoved()) {
			return;
		}

		// added for phasge -8 3.8.1.0 build starts
		if (processToIntegrate(psl, isCopyProduct, bean)) {
			return;
		}

		// added for phasge -8 3.8.1.0 build ends
		// Phase 13 | splitted for sonar fix | added
		// processTriggerRequestForProductSeasonLink function to trigger Request
		// For ProductSeasonLink.
		processTriggerRequestForProductSeasonLink(psl, bean, massCopyProduct);
	}

	/**
	 * Phase 13 | added processTriggerRequestForProductSeasonLink function to
	 * trigger Request For ProductSeasonLink.
	 * 
	 * @param psl
	 * @param bean
	 * @throws WTException
	 */
	private void processTriggerRequestForProductSeasonLink(LCSProductSeasonLink psl, SMProductOutboundIntegrationBean bean,
			String massCopyProduct)
			throws WTException {

		SMProductOutboundClient client = new SMProductOutboundClient();

		String activity = (String) MethodContext.getContext().get("activity");
		String addProdcutAction = (String) MethodContext.getContext().get("ADD_PRODUCTS");
		String addSkus = (String) MethodContext.getContext().get(ADD_SKUS);

		LOGGER.info("Activity PSL  >>>>>>>>>>    " + activity);

		boolean isCopyProduct = FormatHelper
				.parseBoolean(String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.IS_COPY_PRODUCT)));

		if ((psl.getEffectSequence() != 0 && !"COPY_PRODUCT".equalsIgnoreCase(activity) && !isCopyProduct)
				|| (psl.getEffectSequence() != 0 && MASS_COPY_PRODUCTS.equals(massCopyProduct))
				|| (psl.getEffectSequence() == 0 && ADD_SKUS.equals(addSkus))) {

			// calling client if MDM ID is not present for Product Season Link.
			LOGGER.debug("Sending MDM ID request for Product Season Link ......");
			client.productOutboundRequest(psl, true, bean);

		} else if ("ADD_PRODUCTS".equalsIgnoreCase(addProdcutAction)) {

			// calling client if MDM ID is not present for Product Season Link.
			LOGGER.debug("action:-ADD_PRODUCTS, Sending MDM ID request for Product Season Link ......");
			client.productOutboundRequest(psl, true, bean);

		} else if (psl.getMovedFrom() != null || psl.getCarriedOverFrom() != null && !"COPY_PRODUCT".equalsIgnoreCase(activity)) {

			// calling client if MDM ID is not present for Product Season Link.
			LOGGER.debug("Sending MDM ID request for move/carry over Product Season Link ......");
			client.productOutboundRequest(psl, true, bean);

		} else if (isCopyProduct && !"true".equals(MethodContext.getContext().get(LCSProductLogic.COPY_IN_PROGRESS))) {

			// Phase 13 | splitted for sonar fix | added
			// processTriggerforCopyProduct function to trigger Request
			// For ProductSeasonLink for copy product scenario.
			processTriggerforCopyProduct(client, psl, bean);
		}

	}

	/**
	 * Phase 13 | added processTriggerforCopyProduct function to trigger Request
	 * For ProductSeasonLink for copy product scenario.
	 * 
	 * @param client
	 * @param psl
	 * @param bean
	 * @throws WTException
	 */
	private void processTriggerforCopyProduct(SMProductOutboundClient client, LCSProductSeasonLink psl,
			SMProductOutboundIntegrationBean bean) throws WTException {

		try {
			LOGGER.debug("Sending MDM ID request for copy Product Season Link ......");
			// calling client if MDM ID is not present for Product Season
			// Link.
			client.productOutboundRequest(psl, true, bean);

			LCSSeason season = null;
			LCSSKU sku = null;
			LCSSKUSeasonLink ssl = null;

			java.util.List<LCSSKUSeasonLink> skuSeasonList = new java.util.ArrayList<>();
			season = VersionHelper.latestIterationOf(psl.getSeasonMaster());
			LCSProduct productSeasonRev = (LCSProduct) LCSQuery.findObjectById(LCSPRODUCT_CLASS + (int) psl.getProductSeasonRevId());
			LOGGER.info("season name >>>>>>>>>." + season.getName());
			Map<?, ?> tSkuMap = (new ProductHeaderQuery()).findSKUsMap(productSeasonRev, null, season, false);
			LOGGER.info("Product Colorway List Size  for copied product >>>>>   >>>>>     " + tSkuMap.size());
			java.util.Iterator<?> i = LCSQuery.getObjectsFromCollection(tSkuMap.keySet()).iterator();
			while (i.hasNext()) {
				sku = (LCSSKU) i.next();
				// calling colorway MDM id request
				triggerMDMRequestForColorway(sku);

				ssl = (LCSSKUSeasonLink) LCSSeasonQuery.findSeasonProductLink(sku, season);

				if (null != ssl && !ssl.isSeasonRemoved()) {
					skuSeasonList.add(ssl);
					LOGGER.info("Colorway Season List for copied Size   >>>>>     " + skuSeasonList.size());
					// Calling colorway-season MDM ID Request
					triggerMDMRequestOnColorwaySeason(ssl);

				}
			}
		} finally {
			String toIntegrate = String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.TO_INTEGRATE));
			if (FormatHelper.hasContent(toIntegrate) && "true".equalsIgnoreCase(toIntegrate)) {
				psl.setValue(SMProductOutboundWebServiceConstants.TO_INTEGRATE, FALSE);
			}
			psl.setValue(SMProductOutboundWebServiceConstants.IS_COPY_PRODUCT, FALSE);
			bean.getProdUtill().persistProductSeasonLinkObject(psl);
		}
	}

	/**
	 * @param obj
	 * @throws WTException
	 */
	public void triggerMDMRequestForColorway(WTObject obj) throws WTException {

		SMProductOutboundIntegrationBean bean = constructIntegrationBean();
		SMProductOutboundClient client = new SMProductOutboundClient();

		// trigger of colorway
		LCSSKU sku = (LCSSKU) obj;
		final String COPY_SKU = (String) MethodContext.getContext().get("COPY_SKU");
		String massCopyProduct = (String) MethodContext.getContext().get(MASS_COPY_PRODUCTS);

		LOGGER.info("Colorway Version ID  >>>>.   " + sku.getVersionIdentifier().getValue());
		LOGGER.info("SKU is Placeholder ??? " + sku.isPlaceholder() + "   SKU Season Master  >>>     " + sku.getSeasonMaster()
				+ "  ****  SKU carried over from  >>>  " + sku.getCarriedOverFrom());

		LOGGER.info("COPY_IN_PROGRESS>>>>>>>>>>>" + MethodContext.getContext().get(LCSProductLogic.COPY_IN_PROGRESS));
		LOGGER.info("COPY_SKU :>>>>>>>>>" + COPY_SKU);
		LOGGER.info("SKU MDM ID >>>>>>>>>>" + sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID));
		LOGGER.info("VALID DEPT :::>>>>>>>>>>>>>"+ bean.getProdUtill().validateActiveDepartment(sku));

		// phase -13, copy colorway issue SMPLM-1294 - There are no requests to downstream system for some Colorway creation
		if (!"true".equals(MethodContext.getContext().get(LCSProductLogic.COPY_IN_PROGRESS)) || "COPY_SKU".equals(COPY_SKU)) {
			// calling client if MDM ID is not present for Colorway.
			if (!sku.isPlaceholder() && bean.getProdUtill().validateActiveDepartment(sku)
					&& "A".equalsIgnoreCase(sku.getVersionIdentifier().getValue())
					&& !FormatHelper.hasContent((String) sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID))) {

				LOGGER.debug("Sending MDM ID request for Colorway ......");
				client.productOutboundRequest(sku, false, bean);
			}
		} else if (MASS_COPY_PRODUCTS.equals(massCopyProduct) && !sku.isPlaceholder() && bean.getProdUtill().validateActiveDepartment(sku)
				&& "A".equalsIgnoreCase(sku.getVersionIdentifier().getValue())
				&& !FormatHelper.hasContent((String) sku.getValue(SMProductOutboundWebServiceConstants.COLORWAY_MDM_ID))) {

					LOGGER.debug("Sending MDM ID request for Colorway for mass copy product......");
					client.productOutboundRequest(sku, false, bean);
				}
	}

	/**
	 * @param obj
	 * @throws WTException
	 */
	public void triggerMDMRequestForProduct(WTObject obj) throws WTException {

		SMProductOutboundIntegrationBean bean = constructIntegrationBean();
		SMProductOutboundClient client = new SMProductOutboundClient();

		// trigger for product.
		LCSProduct prod = (LCSProduct) obj;
		LOGGER.info("product branch id rev >>>   " + prod.getVersionIdentifier().getValue());
		LOGGER.info("Product Carried over from season  >>>>>>>   " + prod.getCarriedOverFrom());
		// calling client if MDM ID is not present for Product.
		if ("A".equalsIgnoreCase(prod.getVersionIdentifier().getValue())
				&& !FormatHelper.hasContent((String) prod.getValue(SMProductOutboundWebServiceConstants.PRODUCT_MDM_ID_KEY))
					&& bean.getProdUtill().validateActiveDepartment(prod)) {
				LOGGER.debug("Sending MDM ID request for Product ......");
				LOGGER.info("PRODUCT ID  >>>>>>>>>>>>>>*  " + FormatHelper.getNumericObjectIdFromObject(prod));
				client.productOutboundRequest(prod, false, bean);
			}
	}

	/**
	 * Query Log entry and set to collection.
	 */
	public SMProductOutboundIntegrationBean constructIntegrationBean() {

		SMProductOutboundIntegrationBean productPluginBean = new SMProductOutboundIntegrationBean();

		productPluginBean.setProdUtill(new SMProductOutboundUtil());
		productPluginBean.setLogentryUtil(new SMLogEntryUtill());
		productPluginBean.setLogEntryProcessor(new SMProductOutboundLogEntryProcessor());
		productPluginBean.setProdProcessor(new SMProductOutboundDataProcessor());
		productPluginBean.setProdHelper(new SMProductOutboundHelper());
		productPluginBean.setProdUtill(new SMProductOutboundUtil());
		productPluginBean.setXmlUtill(new SMProductOutboundRequestXMLGenerator());
		productPluginBean.setProdOutboundWS(new ProductEndpointService().getProductEndpointPort());
		productPluginBean.setProductSeasonOutboundLogEntry(new HashMap<>());
		productPluginBean.setProductSeasonOutboundMDMIDLogEntry(new HashMap<>());
		productPluginBean.setColorwaySeasonOutboundLogEntry(new HashMap<>());
		productPluginBean.setProductOutboundLogEntry(new HashMap<>());
		productPluginBean.setColorwayOutboundLogEntry(new HashMap<>());

		return productPluginBean;
	}

	/**
	 * this method handles new product out-bound integration trigger logic while
	 * 'To Integrate' set to 'true' on product season level.
	 *
	 * @param psl
	 *            - LCSProductSeasonLink
	 * @return - boolean
	 * @throws WTException
	 *             exception
	 * @throws SQLException
	 * @throws WTPropertyVetoException 
	 */
	private boolean processToIntegrate(LCSProductSeasonLink psl, boolean isCopyProduct, SMProductOutboundIntegrationBean bean)
			throws WTException, SQLException, WTPropertyVetoException {

		Map<String, String> pslMapToIntegrate;
		try {
			if (FormatHelper.hasContent((String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID))
					&& !isCopyProduct) {

				String toIntegrate = String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.TO_INTEGRATE));

				if (FormatHelper.hasContent(toIntegrate) && "true".equalsIgnoreCase(toIntegrate)) {

					LCSProduct product = (LCSProduct) LCSQuery.findObjectById(LCSPRODUCT_CLASS + (int) psl.getProductSeasonRevId());
					product = (LCSProduct) VersionHelper.latestIterationOf(product);

					String lifeCycleState = String.valueOf(product.getLifeCycleState());
					if (!"INWORK".equalsIgnoreCase(lifeCycleState)) {

						LOGGER.debug(
								"Product Season Lifecycle State is changed ---- Set data to processing queue and update log entry");
						pslMapToIntegrate = new HashMap<>();
						final Class<?>[] prodSeaonArgTypesx = {Map.class};
						final Object[] prodSeasonArgValuex = {pslMapToIntegrate};

						// add to map.
						if (FormatHelper.hasContent(String
								.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.AD_HOC_PLM_ID_PRODUCT_SEASON_LINK)))) {

							LOGGER.info("Setting Ad HOC PLM ID value in Process Queue ..........");
							pslMapToIntegrate.put(
									(String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID),
									String.valueOf(
											psl.getValue(SMProductOutboundWebServiceConstants.AD_HOC_PLM_ID_PRODUCT_SEASON_LINK)));

						} else {
							pslMapToIntegrate.put(
									(String) psl.getValue(SMProductOutboundWebServiceConstants.PRODUCT_SEASON_LINK_MDM_ID),
									bean.getProdHelper().getProductMasterReferencefromLink(psl));
						}

						// process on lifecycle change.
						SMLifeCylcePluginProcessor.processLifeCycleChangesOnProductSeason(prodSeaonArgTypesx, prodSeasonArgValuex);
					}
				}
				return true;
			}
		} finally {

			String toIntegrate = String.valueOf(psl.getValue(SMProductOutboundWebServiceConstants.TO_INTEGRATE));

			if (FormatHelper.hasContent(toIntegrate) && "true".equalsIgnoreCase(toIntegrate) && !isCopyProduct) {
				psl.setValue(SMProductOutboundWebServiceConstants.TO_INTEGRATE, FALSE);
				// Phase 14 - EMP-481 - Start
				psl.setValue(SMProductOutboundWebServiceConstants.PRODSEASON_TRIGGERED_BY, psl.getCreator().getFullName());
				psl.setValue(SMProductOutboundWebServiceConstants.PRODSEASON_TRIGGERED_ON, new Date());
				// Phase 14 - EMP-481 - End
				bean.getProdUtill().persistProductSeasonLinkObject(psl);
			}

		}
		return false;

	}
}
