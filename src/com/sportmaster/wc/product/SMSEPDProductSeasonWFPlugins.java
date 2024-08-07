package com.sportmaster.wc.product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.LCSSKUQuery;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSCostSheetMaster;
import com.lcs.wc.sourcing.LCSCostSheetQuery;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.specification.FlexSpecMaster;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;

import wt.fc.WTObject;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * SMSEPDProductSeasonWFPlugins.
 * 
 * @author 'true'
 * @version 'true' 1.0
 * 
 */
public class SMSEPDProductSeasonWFPlugins {

	/**
	 * LOGGER.
	 */
	public static final Logger LOGGER = Logger.getLogger(SMCascadingPlugin.class);
	/**
	 * Product Season Status.
	 */
	public static final String PRODUCT_SEASON_STATUS = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingAttributes.productSeasonStatus");

	/**
	 * LCS cancelled state.
	 */
	public static final String LCS_CANCELLED_STATE = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingPlugin.lcsCancelledState");
	/**
	 * Cancelled Status Key.
	 */
	public static final String CANCELLED_STATUS_KEY = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingAttributes.cancelledStatusKey");
	/**
	 * Product Season Life Cycle State CANCELLED.
	 */
	public static final String PRODUCT_LIFECYCLE_STATE_CANCELLED = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingAttributes.productLifeCycleStateCancelled");
	/**
	 * SKU Season Status 'Cancelled' key.
	 */
	public static final String SKU_STATUS_CANCELLED = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingPlugin.skuStatusCancelled");

	/**
	 * Flex Object Name.
	 */
	public static final String FO_NAME = LCSProperties.get("com.sportmaster.wc.product.SmWorkflowHelper.fOName");

	public static final String COLON = "\" : ";
	/**
	 * SKU Season Status Key.
	 */
	public static final String SKU_SEASON_STATUS_KEY = LCSProperties
			.get("com.sportmaster.wc.product.SMCascadingAttributes.skuSeasonStatusKey");

	/**
	 * PS_USERGROUP_ATTS
	 */
	public static final String PS_USERGROUP_ATTS = "smProductionManager|~*~|smProducctTechnologist|~*~|vrdDesigner";

	/**
	 * PS_ATTS
	 */
	public static final String PS_ATTS = "smDevelopmentGroup|~*~|smProductionGroup|~*~|smMDMPSL";

	/**
	 * PS_ATTS
	 */
	public static final String PRODUCT_ATTS = "smMDMPRO|~*~|";

	/**
	 * PRODUCTSEAS_CANCEL_ATTS
	 */
	public static final String PRODUCTSEAS_CANCEL_ATTS = "smMDMPSL|~*~|";
	
	public static final String VENDOR_ATTKEY = "vendor";
	
	public static final String VENDOR_INACTIVE_KEY = "vrdInactive";

	/**
	 * Constructor.
	 */
	protected SMSEPDProductSeasonWFPlugins() {
		// protected constructor
	}

	/**
	 * setSEPDSKUSeasonAttribute.
	 * 
	 * @param wtObj for wtObj.
	 * @return void.
	 * @throws WTPropertyVetoException 
	 */
	public static void setSEPDSKUSeasonAttribute(WTObject wtObj) throws WTPropertyVetoException {
		LOGGER.debug("start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--setSEPDSKUSeasonAttribute");

		try {
			if (wtObj instanceof LCSSeasonProductLink) {
				// Getting season product link
				LCSSeasonProductLink seasonProductLink = (LCSSeasonProductLink) wtObj;

				// Getting Product Object.
				LCSProduct lcsProd = SeasonProductLocator.getProductSeasonRev(seasonProductLink);

				// Getting value of Product Status Style Seasonal attribute.
				String productStatusStyleSeasonal = seasonProductLink.getValue(PRODUCT_SEASON_STATUS).toString();
				LOGGER.debug("productStatusStyleSeasonal="+productStatusStyleSeasonal);
				if (productStatusStyleSeasonal != null && FormatHelper.hasContent(productStatusStyleSeasonal)
						&& productStatusStyleSeasonal.equalsIgnoreCase(CANCELLED_STATUS_KEY)) {

					setProductCancelState(lcsProd);
					
					// Phase 14 - Fix for SMPLM-1355 Cancelled LCS Not shown in Line Sheet - Start
					LOGGER.debug("\n **** seasonProductLink= bef=="+seasonProductLink);
					seasonProductLink.setProdState(PRODUCT_LIFECYCLE_STATE_CANCELLED);
					// using persist API to prevent plugin from re-triggering.
					LCSLogic.persist(seasonProductLink, true);
					LOGGER.debug("\n **** seasonProductLink= aft=="+seasonProductLink);
					// Phase 14 - Fix for SMPLM-1355 Cancelled LCS Not shown in Line Sheet - End
					
					
					// Getting Product Object.
					LCSProduct lcsProd1 = SeasonProductLocator.getProductSeasonRev(seasonProductLink);

					final List<LCSSKU> skuCollection = (List<LCSSKU>) LCSSKUQuery.findSKUs(lcsProd1);
					LOGGER.debug("skuCollection="+skuCollection);
					setSEPDSKUsCancelled(skuCollection);
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in setSEPDSKUSeasonAttribute Method -" + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.debug("end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--setSEPDSKUSeasonAttribute");
	}

	private static void setProductCancelState(LCSProduct lcsProd) throws WTException, LifeCycleException {
		LOGGER.debug("start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--setProductCancelState");
		// creating state object with Cancelled state.
		State cancelledState = State.toState(PRODUCT_LIFECYCLE_STATE_CANCELLED);
		LOGGER.debug("cancelledState="+cancelledState);
		final boolean old_enforced = SessionServerHelper.manager.setAccessEnforced(false);
		try {
			// Setting Product Lifecycle state as cancelled.
			LifeCycleHelper.service.setLifeCycleState(lcsProd, cancelledState, true);
		} finally {
			SessionServerHelper.manager.setAccessEnforced(old_enforced);
		}
		LOGGER.debug("end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--setProductCancelState");
	}

	/**
	 * setSEPDSKUsCancelled.
	 * 
	 * @param skuCollection for skuCollection.
	 * @return void.
	 */
	public static void setSEPDSKUsCancelled(final List<LCSSKU> skuCollection) {
		LOGGER.debug("start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--setSEPDSKUsCancelled");
		try {
			LCSSKUSeasonLink skuSeasonLink;
			LCSSKU skuObj;
			String currentCWLCS;

			if (skuCollection != null && skuCollection.size() > 0) {
				for (Object obj : skuCollection) {
					// Get the sku Object.
					skuObj = (LCSSKU) obj;
					// Getting Current Colorway Season Lifecycle state.
					currentCWLCS = skuObj.getLifeCycleState().getFullDisplay();
					LOGGER.debug("currentCWLCS="+currentCWLCS);
					// Getting sku Season Link
					skuSeasonLink = (LCSSKUSeasonLink) LCSSeasonQuery.findSeasonProductLink(skuObj.getMaster(),
							skuObj.getSeasonMaster());

					if (!currentCWLCS.equals(LCS_CANCELLED_STATE)) {
						// Setting LCS - CW cancelled afterSKU Season status attribute to Cancelled.
						skuSeasonLink.setValue(SKU_SEASON_STATUS_KEY, CANCELLED_STATUS_KEY);
						// creating Cancelled state obj
						State cancelledSKUState = State.toState(SKU_STATUS_CANCELLED);
						// Persist here to set the lifecycle state on latest SKUSeason link
						// object.
						// saving cw-season status attribute
						LCSLogic.persist(skuSeasonLink);
						// Bypass the security to change state as the users usually don't have access to
						// Change State from UI
						final boolean old_enforced = SessionServerHelper.manager.setAccessEnforced(false);
						try {
							// Setting SKU Season Lifecycle state as cancelled.
							LifeCycleHelper.service.setLifeCycleState(skuObj, cancelledSKUState, true);
						} finally {
							SessionServerHelper.manager.setAccessEnforced(old_enforced);
						}
					}
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in setSEPDSKUsCancelled Method -" + e.getMessage());
		}
		LOGGER.debug("end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--setSEPDSKUsCancelled");
	}

	/**
	 * setSEPDCWSeasonalStateCancelled.
	 * 
	 * @param wtObj for wtObj.
	 * @return void.
	 */
	public static void setSEPDCWSeasonalStateCancelled(WTObject wtObj) {
		LOGGER.debug("start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--setSEPDCWSeasonalStateCancelled");

		try {
			if (wtObj instanceof LCSSKUSeasonLink) {
				// Getting season product link.
				LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) wtObj;
				// Getting sku Object.
				LCSSKU skuObj = SeasonProductLocator.getSKUSeasonRev(skuSeasonLink);
				// Getting Status CW Seasonal attribute value.
				String skuStatusCWSeasonal = skuSeasonLink.getValue(SKU_SEASON_STATUS_KEY).toString();
				// Getting Current SKU LifeCycle state.
				String currentLCS = skuObj.getLifeCycleState().getFullDisplay();
				LOGGER.debug("currentLCS SKU="+currentLCS);
				LOGGER.debug("skuStatusCWSeasonal=" + skuStatusCWSeasonal);
				if (!currentLCS.equals(LCS_CANCELLED_STATE) && FormatHelper.hasContent(skuStatusCWSeasonal)
						&& skuStatusCWSeasonal.equalsIgnoreCase(CANCELLED_STATUS_KEY)) {
					// creating state object with Cancelled state.
					State cancelledSKUState = State.toState(SKU_STATUS_CANCELLED);
					LOGGER.debug("cancelledState=" + cancelledSKUState);
					// Bypass the security to change state as the users usually don't have access to
					// Change State from UI
					final boolean old_enforced = SessionServerHelper.manager.setAccessEnforced(false);
					try {
						// Setting SKU Season Lifecycle state as cancelled.
						LifeCycleHelper.service.setLifeCycleState(skuObj, cancelledSKUState, true);
					} finally {
						SessionServerHelper.manager.setAccessEnforced(old_enforced);
					}
				}

			}
		} catch (WTException e) {
			LOGGER.error("WTException in setSEPDCWSeasonalStateCancelled Method -" + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.debug("end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--setSEPDCWSeasonalStateCancelled");
	}

	// Check mandatory attributes

	/**
	 * confirmAttributesOnTaskPrepareAssortmentForTOPCalculation.
	 * 
	 * @param object for object.
	 * @return void.
	 */
	public static String confirmAttributesOnTaskPrepareAssortmentForTOPCalculation(WTObject object, String action) {
		LOGGER.debug(
				"start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--confirmAttributesOnTaskPrepareAssortmentForTOPCalculation");
		StringBuilder sbCons = new StringBuilder();
		StringBuilder sbFinal = new StringBuilder();
		StringBuilder sbProdSeas = new StringBuilder();
		StringBuilder sbProdSeasUserList = new StringBuilder();
		StringBuilder sbProd = new StringBuilder();
		StringBuilder sbSkuAndSkuSeas = new StringBuilder();
		String sbFinalString = "";
		try {
			if (object instanceof LCSProduct) {
				// Getting Product Object.
				LCSProduct product = (LCSProduct) object;
				LCSSeasonProductLink seasonProductLink = getProductSeasonLink(product);
				LCSProduct prodARev = SeasonProductLocator.getProductARev(product);
				LOGGER.debug("ACTION =" + action);
				atributeValidationForAction(action, sbProdSeas, sbProdSeasUserList, sbProd, seasonProductLink,
						prodARev);
				sbSkuAndSkuSeas.append(validateSkuAndSkuSeasonAttrs(product));
				sbCons.append(sbProd.toString()).append(sbProdSeas.toString()).append(sbProdSeasUserList.toString())
						.append(sbSkuAndSkuSeas.toString());
				if (FormatHelper.hasContent(sbCons.toString())) {
					sbFinal.append("The following attributes are empty and mandatory to fill in for Product : \""
							+ prodARev.getName()).append("\"").append(lineSeparator()).append(sbCons);
					sbFinalString = sbFinal.toString();
				}
			}
		} catch (WTException e) {
			LOGGER.error("WTException in confirmAttributesOnTaskPrepareAssortmentForTOPCalculation method: "
					+ e.getMessage());
			e.printStackTrace();
		}
		LOGGER.debug("sbFinalString in confirmAttributesOnTaskPrepareAssortmentForTOPCalculation =" + sbFinalString);
		LOGGER.debug(
				"end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--confirmAttributesOnTaskPrepareAssortmentForTOPCalculation");
		return sbFinalString;
	}

	/**
	 * @param action
	 * @param sbProdSeas
	 * @param sbProdSeasUserList
	 * @param sbProd
	 * @param seasonProductLink
	 * @param prodARev
	 */
	private static void atributeValidationForAction(String action, StringBuilder sbProdSeas,
			StringBuilder sbProdSeasUserList, StringBuilder sbProd, LCSSeasonProductLink seasonProductLink,
			LCSProduct prodARev) {
		LOGGER.debug(
				"start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--atributeValidationForAction");
		if (action.equals("confirm")) {
			sbProdSeas.append(validateObjAttrs(seasonProductLink, MOAHelper.getMOACollection(PS_ATTS),
					seasonProductLink.getFlexType()));
			sbProdSeasUserList.append(validateUserGroupAttribute(seasonProductLink,
					MOAHelper.getMOACollection(PS_USERGROUP_ATTS), seasonProductLink.getFlexType()));
			sbProd.append(validateObjAttrs(prodARev, MOAHelper.getMOACollection(PRODUCT_ATTS), prodARev.getFlexType()));
		} else if (action.equals("cancel")) {
			sbProdSeas.append(validateObjAttrs(seasonProductLink, MOAHelper.getMOACollection(PRODUCTSEAS_CANCEL_ATTS),
					seasonProductLink.getFlexType()));
			sbProd.append(validateObjAttrs(prodARev, MOAHelper.getMOACollection(PRODUCT_ATTS), prodARev.getFlexType()));
		}
		LOGGER.debug(
				"end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--atributeValidationForAction");
	}

	/**
	 * @param seasonProductLink
	 * @param moaCollection
	 * @param ft
	 * @return
	 */
	private static StringBuilder validateUserGroupAttribute(LCSSeasonProductLink seasonProductLink,
			Collection<String> moaCollection, FlexType ft) {
		LOGGER.debug(
				"start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateUserGroupAttribute");
		StringBuilder sbProdSeas = new StringBuilder();
		String result = null;
		String attKey = "";
		FlexObject fo;
		String strUserName = "";
		LOGGER.debug("moaCollection==" + moaCollection);
		Iterator itmoaCollection = moaCollection.iterator();
		while (itmoaCollection.hasNext()) {
			fo = null;
			attKey = (String) itmoaCollection.next();
			try {
				fo = (FlexObject) seasonProductLink.getValue(attKey);
				if (fo == null || fo.isEmpty()) {
					LOGGER.debug("User attribute is empty:::" + attKey);
					sbProdSeas.append("Product Season : ").append(ft.getAttribute(attKey).getAttDisplay());
						sbProdSeas.append(lineSeparator());
				}
			} catch (WTException e) {
				LOGGER.error("WTException in validateUserGroupAttribute method: " + e.getMessage());
				e.printStackTrace();
			}
		}
		LOGGER.debug(
				"end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateUserGroupAttribute");
		return sbProdSeas;
	}

	/**
	 * @param wtObj
	 * @param moaCollection
	 * @param ft
	 * @return
	 */
	private static StringBuilder validateObjAttrs(Object wtObj, Collection<String> moaCollection, FlexType ft) {
		LOGGER.debug(
				"start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateObjAttrs");
		StringBuilder sbObjStr = new StringBuilder();
		Iterator itmoaCollection = moaCollection.iterator();

		String attKey = "";
		String attValue;
		String constant = "";
		while (itmoaCollection.hasNext()) {
			attValue = "";
			attKey = (String) itmoaCollection.next();
			try {
				if (wtObj instanceof LCSSeasonProductLink) {
					attValue = (String) ((LCSProductSeasonLink) wtObj).getValue(attKey);
					constant = "Product Season : ";
				} else if (wtObj instanceof LCSProduct) {
					attValue = (String) ((LCSProduct) wtObj).getValue(attKey);
					constant = "Product : ";
				}
				LOGGER.debug("attValue:::" + attValue);
				if (!FormatHelper.hasContent(attValue)) {
					sbObjStr.append(constant).append(ft.getAttribute(attKey).getAttDisplay());
						sbObjStr.append(lineSeparator());
				}
			} catch (WTException e) {
				LOGGER.error("WTException in validateObjAttrs method: " + e.getMessage());
				e.printStackTrace();
			}

		}
		LOGGER.debug("sbObjStr:::" + sbObjStr.toString());
		LOGGER.debug(
				"end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateObjAttrs");
		return sbObjStr;
	}

	// Get current product season link
	/**
	 * @param obj
	 * @return
	 * @throws WTException
	 */
	public static LCSProductSeasonLink getProductSeasonLink(WTObject obj) throws WTException {
		LOGGER.debug(
				"start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--getProductSeasonLink");
		LCSProductSeasonLink smSeasonProdLink = null;
		if (obj instanceof LCSProduct) {
			// Getting Product Object.
			LCSProduct product = (LCSProduct) obj;
			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				smSeasonProdLink = (LCSProductSeasonLink) SeasonProductLocator.getSeasonProductLink(product);
			}
		}
		LOGGER.debug(
				"end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--getProductSeasonLink");
		return smSeasonProdLink;
	}

	/**
	 * @param product
	 * @return
	 */
	private static StringBuilder validateSkuAndSkuSeasonAttrs(LCSProduct product) {
		LOGGER.debug(
				"start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateSkuAndSkuSeasonAttrs");
		StringBuilder sbObjStr = new StringBuilder();
		List skuSeasLinks = getAllSkuSeasonLinks(product);
		sbObjStr.append(validateSKUSeasonAttributes(skuSeasLinks));
		LOGGER.debug(
				"end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateSkuAndSkuSeasonAttrs");
		return sbObjStr;
	}

	// get all sku season links for this product season
	/**
	 * @param obj
	 * @return
	 */
	public static List getAllSkuSeasonLinks(WTObject obj) {
		LOGGER.debug(
				"start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--getAllSkuSeasonLinks");
		List<LCSSKUSeasonLink> skuSeasCollection = new ArrayList();
		if (obj instanceof LCSProduct) {
			// Getting Product Object.
			LCSProduct product = (LCSProduct) obj;
			List<LCSSKU> skuCollection = new ArrayList();
			LCSSKUSeasonLink skuSeasonLink;
			LCSSKU smSKU;
			if (!"A".equalsIgnoreCase(product.getVersionDisplayIdentifier().toString())) {
				// Getting season product link

				try {
					skuCollection = (List<LCSSKU>) LCSSKUQuery.findSKUs(product);
					LOGGER.debug("skuCollection="+skuCollection);
				} catch (WTException e1) {
					LOGGER.error("WTException in getAllSkuSeasonLinks method: " + e1.getMessage());
					e1.printStackTrace();
				}
				for (Object skuObj : skuCollection) {
					LOGGER.debug("skuObj="+skuObj);
					smSKU = (LCSSKU) skuObj;

					// Getting SKU Season Link
					try {
						skuSeasonLink = (LCSSKUSeasonLink) LCSSeasonQuery.findSeasonProductLink(smSKU.getMaster(),
								smSKU.getSeasonMaster());
						skuSeasCollection.add(skuSeasonLink);
					} catch (WTException e) {
						LOGGER.error("WTException in getAllSkuSeasonLinks method: " + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
		LOGGER.debug(
				"end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--getAllSkuSeasonLinks");
		return skuSeasCollection;
	}

	/**
	 * @param skuSeasCollection
	 * @return
	 */
	public static StringBuilder validateSKUSeasonAttributes(List<LCSSKUSeasonLink> skuSeasCollection) {
		LOGGER.debug(
				"end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateSKUSeasonAttributes");
		StringBuilder sbSKU = new StringBuilder();
		StringBuilder sbSKUSeas = new StringBuilder();
		StringBuilder sb = new StringBuilder();
		Iterator<LCSSKUSeasonLink> itskuSeasCollection = skuSeasCollection.iterator();
		LCSSKUSeasonLink skuSeas = null;
		String skuSeasMDM = "";
		String skuMDM = ""; 
		LCSSKU skuARev = null;
		while (itskuSeasCollection.hasNext()) {
			skuSeas = itskuSeasCollection.next();
			try {
				skuSeasMDM = (String) skuSeas.getValue("smMDMSSL");
				LOGGER.debug("skuSeasMDM="+skuSeasMDM);
				skuARev = SeasonProductLocator.getSKUARev(skuSeas);
				skuMDM = (String) skuARev.getValue("smMDMSKU");
				LOGGER.debug("skuMDM="+skuMDM);
				if (!FormatHelper.hasContent(skuMDM)) {
					sbSKU.append("Colorway : ").append(skuARev.getValue("skuName")).append(" : ")
							.append(skuARev.getFlexType().getAttribute("smMDMSKU").getAttDisplay());
						sbSKU.append(lineSeparator());
				}
				if (!FormatHelper.hasContent(skuSeasMDM)) {
					sbSKUSeas.append("Colorway Season : ").append(skuARev.getValue("skuName")).append(" : ")
							.append(skuSeas.getFlexType().getAttribute("smMDMSSL").getAttDisplay());
						sbSKUSeas.append(lineSeparator());
				}
			} catch (WTException e) {
				LOGGER.error("WTException in validateSKUSeasonAttributes method: " + e.getMessage());
				e.printStackTrace();
			}
		}
		if (FormatHelper.hasContent(sbSKU.toString())) {
			sb.append(sbSKU.toString());
		}
		if (FormatHelper.hasContent(sbSKUSeas.toString())) {
			//sb.append(lineSeparator()).append(sbSKUSeas.toString());
			sb.append(sbSKUSeas.toString());
		}
		LOGGER.debug("sb="+sb.toString());
		LOGGER.debug(
				"end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateSKUSeasonAttributes");
		return sb;

	}

	/**
	 * @param object
	 * @return
	 */
	public static String validateAttributesOnAllocation(WTObject object) {
		LOGGER.debug("start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateAttributesOnAllocation");
		StringBuilder sbCons = new StringBuilder();
		StringBuilder sbFinal = new StringBuilder();

		String sbFinalString = "";
		try {
			if (object instanceof LCSProduct) {
				// Getting Product Object.
				LCSProduct product = (LCSProduct) object;
				LCSProduct prodARev = SeasonProductLocator.getProductARev(product);
				LCSSeason seas = SeasonProductLocator.getSeasonRev(product);
				Collection srcToSeasCollection = LCSSourcingConfigQuery.getSourcingConfigForProductSeason(product,
						seas);
				LOGGER.debug("srcToSeasCollection==" + srcToSeasCollection);
				sbCons.append(validateSourcingConfigAttributes(srcToSeasCollection, seas));
				if (FormatHelper.hasContent(sbCons.toString())) {
					sbFinal.append(
							"The following Sourcing Config attributes are empty and mandatory to fill in for Product : \""
									+ prodARev.getName())
							.append("\"").append(lineSeparator()).append(sbCons);
				}
				sbFinalString = sbFinal.toString();
			}
		} catch (WTException e) {
			LOGGER.error("WTException in validateAttributesOnAllocation method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.debug("sb="+sbFinalString.toString());
		LOGGER.debug("end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateAttributesOnAllocation");
		return sbFinalString;
	}

	/**
	 * @param srcToSeasCollection
	 * @param seas
	 * @return
	 */
	public static StringBuilder validateSourcingConfigAttributes(Collection srcToSeasCollection, LCSSeason seas) {
		LOGGER.debug("start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateSourcingConfigAttributes");
		StringBuilder sbSrcConf = new StringBuilder();
		Iterator itsrcToSeasCollection = srcToSeasCollection.iterator();
		LCSSourcingConfigQuery srcQuery = new LCSSourcingConfigQuery();
		PreparedQueryStatement pq;
		LCSSourcingConfig src;
		SearchResults results;
		Collection cSrcSeas;
		while (itsrcToSeasCollection.hasNext()) {
			src = (LCSSourcingConfig) itsrcToSeasCollection.next();

			try {
				// if business supplier is null and sourcing allocation status is not cancelled,
				// then throw error from workflow
				if (src.getValue(VENDOR_ATTKEY) == null) {
					pq = srcQuery.getSourceToSeasonQuery(src.getMaster(), seas.getMaster());
					pq.appendAndIfNeeded();
					pq.appendCriteria(new Criteria("LCSSourceToSeasonLink",
							src.getFlexType().getAttribute("vrdSourcingStatus").getColumnName(), VENDOR_INACTIVE_KEY,
							Criteria.NOT_EQUAL_TO));
					results = LCSQuery.runDirectQuery(pq);
					LOGGER.debug("results==" + results);
					cSrcSeas = LCSQuery.getObjectsFromResults(results, "VR:com.lcs.wc.sourcing.LCSSourceToSeasonLink:",
							"LCSSOURCETOSEASONLINK.BRANCHIDITERATIONINFO");

					// if business supplier is null and sourcing allocation status is not cancelled,
					// then throw error from workflow
					if (cSrcSeas.size() > 0) {
						sbSrcConf.append("\"").append(src.getValue("name")).append(COLON)
								.append(src.getFlexType().getAttribute(VENDOR_ATTKEY).getAttDisplay());
						if (itsrcToSeasCollection.hasNext()) {
							sbSrcConf.append(lineSeparator());
						}
					}
				}
			} catch (WTException e) {
				LOGGER.error("WTException in validateSourcingConfigAttributes method: " + e.getMessage());
				e.printStackTrace();

			}
		}
		LOGGER.debug("sbSrcConf==" + sbSrcConf.toString());
		LOGGER.debug("end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateSourcingConfigAttributes");
		return sbSrcConf;

	}

	/**
	 * @param object
	 * @return
	 */
	public static String validateAttributesOnLineReview(WTObject object) {
		LOGGER.debug("start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateAttributesOnLineReview");
		StringBuilder sbCSAttrs = new StringBuilder();
		StringBuilder sbCSDestAttrs = new StringBuilder();
		StringBuilder sbCSFinalAttrs = new StringBuilder();
		try {
			if (object instanceof LCSProduct) {
				// Getting Product Object.
				LCSProduct product = (LCSProduct) object;
				LCSProduct prodARev = SeasonProductLocator.getProductARev(product);
				LCSSeason seas = SeasonProductLocator.getSeasonRev(product);
				LCSCostSheetQuery lcsCSQuery = new LCSCostSheetQuery();

				Collection<?> costSheetFlexObj = LCSCostSheetQuery.getCostSheetsForProduct(new HashMap(), product, null,
						seas, new ArrayList(), false, false);

				LOGGER.debug("costSheetFlexObj==" + costSheetFlexObj);
				Iterator itcostSheetList = costSheetFlexObj.iterator();

				FlexType ft = FlexTypeCache.getFlexTypeFromPath("Product Destination");

				// CostSheetd
				while (itcostSheetList.hasNext()) {
					validateCostSheetAttributes(sbCSAttrs, sbCSDestAttrs, lcsCSQuery, itcostSheetList, ft);
				}
				if (FormatHelper.hasContent(sbCSAttrs.toString())) {
					sbCSFinalAttrs.append(
							"The following Cost Sheet attributes are empty and mandatory to fill in for product: \""
									+ prodARev.getName() + "\"")
							.append(lineSeparator()).append(sbCSAttrs.toString());
				}
				if (FormatHelper.hasContent(sbCSDestAttrs.toString())) {
					sbCSFinalAttrs
							.append("The following Cost Sheet attributes contain duplicates for Product : \""
									+ prodARev.getName() + "\"")
							.append(lineSeparator()).append(sbCSDestAttrs.toString());
				}

			}
		} catch (WTException e) {
			LOGGER.error("WTException in validateAttributesOnAllocation method: " + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.debug("end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateAttributesOnLineReview");
		return sbCSFinalAttrs.toString();
	}

	/**
	 * @param sbCSAttrs
	 * @param sbCSDestAttrs
	 * @param lcsCSQuery
	 * @param itcostSheetList
	 * @param ft
	 * @throws WTException
	 */
	private static void validateCostSheetAttributes(StringBuilder sbCSAttrs, StringBuilder sbCSDestAttrs,
			LCSCostSheetQuery lcsCSQuery, Iterator itcostSheetList, FlexType ft) throws WTException {
		LOGGER.debug("end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateCostSheetAttributes");
		LCSProductCostSheet cs;
		FlexSpecMaster specMaster;
		String strCostSheetName;
		Double smPurchasePriceCalcContractCur;
		FlexObject foCS;
		Collection uniqueDestSet;
		// foDestColl = null;
		uniqueDestSet = new ArrayList();

		foCS = (FlexObject) itcostSheetList.next();
		if (!foCS.isEmpty()) {
			cs = (LCSProductCostSheet) LCSQuery.findObjectById("VR:com.lcs.wc.sourcing.LCSProductCostSheet:"
					+ foCS.getString("LCSCOSTSHEET.BRANCHIDITERATIONINFO"));
			strCostSheetName = (String) cs.getValue("name");
			LOGGER.debug("strCostSheetName==" + strCostSheetName);
			if (cs.getValue("vrdCSStatus").equals("smApproved")) {

				smPurchasePriceCalcContractCur = (Double) cs.getValue("smPurchasePriceCalcContractCur");
				LOGGER.debug("smPurchasePriceCalcContractCur==" + smPurchasePriceCalcContractCur);
				if (smPurchasePriceCalcContractCur == 0.0) {
					sbCSAttrs.append("\"").append(strCostSheetName).append(COLON)
							.append(cs.getFlexType().getAttribute("smPurchasePriceCalcContractCur").getAttDisplay());
					sbCSAttrs.append(lineSeparator());
				}
				specMaster = cs.getSpecificationMaster();
				if (specMaster == null) {
					sbCSAttrs.append("\"").append(strCostSheetName).append(COLON).append(" Specification");
					sbCSAttrs.append(lineSeparator());
				}
				validateDestinations(sbCSDestAttrs, lcsCSQuery, ft, cs, strCostSheetName, uniqueDestSet);
			}
			/*if (itcostSheetList.hasNext()) {
				sbCSAttrs.append(lineSeparator());
				sbCSDestAttrs.append(lineSeparator());
			}*/
		}
		LOGGER.debug("end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateCostSheetAttributes");
	}

	/**
	 * @param sbCSDestAttrs
	 * @param lcsCSQuery
	 * @param ft
	 * @param cs
	 * @param strCostSheetName
	 * @param uniqueDestSet
	 * @throws WTException
	 */
	private static void validateDestinations(StringBuilder sbCSDestAttrs, LCSCostSheetQuery lcsCSQuery, FlexType ft,
			LCSProductCostSheet cs, String strCostSheetName, Collection uniqueDestSet) throws WTException {
		LOGGER.debug("start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateDestinations");
		Collection foDestColl;
		Iterator itFo;
		FlexObject foDest;
		String strProdDestName;
		foDestColl = (Collection) lcsCSQuery.getDestinationLinks((LCSCostSheetMaster) cs.getMaster());
		itFo = foDestColl.iterator();
		while (itFo.hasNext()) {
			foDest = (FlexObject) itFo.next();
			LOGGER.debug("foDest==" + foDest);
			strProdDestName = foDest.getString("PRODUCTDESTINATION." + ft.getAttribute("name").getColumnName());
			if (!uniqueDestSet.isEmpty()) {
				if (!uniqueDestSet.contains(strProdDestName.toLowerCase())) {
					uniqueDestSet.add(strProdDestName.toLowerCase());
				} else {
					LOGGER.debug("Pro Dest Name already exist");
					sbCSDestAttrs.append("\"").append(strCostSheetName).append(COLON).append(" Destination");
					sbCSDestAttrs.append(lineSeparator());
					break;
				}
			} else {
				uniqueDestSet.add(strProdDestName.toLowerCase());
			}

		}
		LOGGER.debug("end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--validateDestinations");
	}

	public static String lineSeparator() {
		return System.lineSeparator();
	}

	/**
	 * checkSEPDColorwaySeasonLCS.
	 * 
	 * @param wtObj for wtObj.
	 * @return void.
	 */
	public static void checkSEPDColorwaySeasonLCS(WTObject wtObj) {
		LOGGER.info("start - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--checkSEPDColorwaySeasonLCS");

		try {
			boolean bSKURemovedAndAdded = false;
			if (wtObj instanceof LCSSKUSeasonLink) {
				// Getting season product link.
				LCSSKUSeasonLink skuSeasonLink = (LCSSKUSeasonLink) wtObj;
				if (skuSeasonLink.getEffectOutDate() == null) {
				LCSSKUSeasonLink priorLink;
				if(skuSeasonLink.getEffectSequence()>0) {
					LOGGER.info("skuSeasonLink="+skuSeasonLink);
					priorLink = (LCSSKUSeasonLink) LCSSeasonQuery.getPriorSeasonProductLink(skuSeasonLink);
					LOGGER.info("priorLink="+priorLink);
					LOGGER.info("prior SKU Season Link Season Removed="+priorLink.isSeasonRemoved());
					LOGGER.info("prior SKU Season Link Season Removed="+skuSeasonLink.isSeasonRemoved());
					bSKURemovedAndAdded = isSeasRemovedAndAdded(skuSeasonLink, priorLink);
				}
				LOGGER.info("SKU Removed And Added="+bSKURemovedAndAdded);
				LOGGER.info("SKU Removed And Added="+skuSeasonLink.getEffectSequence());
				if (skuSeasonLink.getEffectSequence() < 2 || bSKURemovedAndAdded) {
					// Getting sku Object.
					LCSSKU skuObj = SeasonProductLocator.getSKUSeasonRev(skuSeasonLink);
					// Getting product Object.
					LCSProduct product = SeasonProductLocator.getProductSeasonRev(skuObj);
					// Getting current Product Lifecycle state.
					String currentProductLCS = product.getState().getState().toString();
					LOGGER.info("currentProductLCS=="+currentProductLCS);
					// creating state object with Product Season Lifecycle state.
					State productState = State.toState(currentProductLCS);

					// Bypass the security to change state as the users usually don't have access to
					// Change State from UI
					final boolean old_enforced = SessionServerHelper.manager.setAccessEnforced(false);
					try {
						// Setting Product Season Lifecycle state to SKU Season Lifecycle state.
						LifeCycleHelper.service.setLifeCycleState(skuObj, productState);
					} finally {
						SessionServerHelper.manager.setAccessEnforced(old_enforced);
					}

				}
				}

			}
		} catch (WTException e) {
			LOGGER.error("WTException in checkSEPDColorwaySeasonLCS Method -" + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.info("end - Inside CLASS--SMSEPDProductSeasonWFPlugins and METHOD--checkSEPDColorwaySeasonLCS");
	}

	/**
	 * @param skuSeasonLink
	 * @param priorLink
	 * @return
	 */
	private static boolean isSeasRemovedAndAdded(LCSSKUSeasonLink skuSeasonLink,
			LCSSKUSeasonLink priorLink) {
		boolean bSKURemovedAndAdded = false;
		if(priorLink.isSeasonRemoved() && !skuSeasonLink.isSeasonRemoved()) {
			bSKURemovedAndAdded = true;
		}
		return bSKURemovedAndAdded;
	}

}
