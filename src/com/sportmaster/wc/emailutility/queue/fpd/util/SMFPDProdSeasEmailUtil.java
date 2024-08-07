package com.sportmaster.wc.emailutility.queue.fpd.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.LCSSKUQuery;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.fpd.processor.SMFPDProdSeasWFEmailBean;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTProperties;

public class SMFPDProdSeasEmailUtil {

	/**
	 * constructor.
	 */
	private SMFPDProdSeasEmailUtil() {
	}

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMFPDProdSeasEmailUtil.class);

	/**
	 * Get unique tasks Some tasks for a single object will be assigned to
	 * multiple roles, iterating the tasks and removing the duplicate objects in
	 * these cases
	 *
	 * @param worklist
	 * @return
	 * @throws WTException
	 */
	public static Set<WTObject> findUniqueTasks(List<?> worklist) throws WTException {

		LOGGER.debug("### SMFPDProdSeasEmailUtil.findUniqueTasks - START ###");
		Set<WTObject> uniqueWorkListFPD = new HashSet<>();
		String workItemObjFPD = "";
		WTObject wtObject;
		String createDate;
		LOGGER.debug("=findUniqueTasks size==" + worklist.size());
		for (Object work : worklist) {
			FlexObject workitem = (FlexObject) work;
			workItemObjFPD = workitem.getString("WORKITEM.CLASSNAMEKEYB4");
			wtObject = (WTObject) LCSQuery.findObjectById(workItemObjFPD);

			createDate = workitem.getString("WORKITEM.CREATESTAMPA2");
			LOGGER.debug("=Task Create Date==" + createDate);
			// Adds only the unique value to the hashset
			if (wtObject instanceof LCSProduct) {
				uniqueWorkListFPD.add(wtObject);
			}
		}
		LOGGER.debug("### SMFPDProdSeasEmailUtil.findUniqueTasks - END ###");
		return uniqueWorkListFPD;
	}

	/**
	 * Revives season skus for the product.
	 *
	 * @param prod
	 * @return
	 * @throws WTException
	 */
	public static List<LCSSKU> getColorway(LCSSeasonProductLink sPL) throws WTException {
		LOGGER.debug("### SMFPDProdSeasEmailUtil.getColorway - Start ###");
		LCSProduct prodSeasonRev = SeasonProductLocator.getProductSeasonRev(sPL);
		Collection<?> allSkus = LCSSKUQuery.findSKUs(prodSeasonRev);
		LOGGER.debug("allSkus="+allSkus);
		ArrayList<LCSSKU> skuList = (ArrayList) allSkus.stream().filter(Objects::nonNull).filter(LCSSKU.class::isInstance).map(LCSSKU.class::cast)
				.filter(s -> !((s.getLifeCycleState().toString()).equalsIgnoreCase("CANCELLED"))).collect(Collectors.toList());
		ArrayList<LCSSKU> finalSKUList = new ArrayList();
		LCSSKUSeasonLink skuSeasonRev;
		for (LCSSKU sku : skuList) {
			LOGGER.debug("sku=" + sku);
			skuSeasonRev = (LCSSKUSeasonLink) SeasonProductLocator.getSeasonProductLink(sku);
			LOGGER.debug("skuSeasonRev.isSeasonRemoved()=" + skuSeasonRev.isSeasonRemoved());
			if(skuSeasonRev.isEffectLatest() && !skuSeasonRev.isSeasonRemoved()) {
				finalSKUList.add(sku);
			}
		}
		LOGGER.debug("finalSKUList="+finalSKUList);
		LOGGER.debug("### SMFPDProdSeasEmailUtil.getColorway - End ###");
		return finalSKUList;
	}


	/**
	 * Method to prepare HTML content for email body
	 *
	 * @param keyTaskNo
	 * @param moaCollection
	 * @param listTableData
	 * @return
	 * @throws WTException
	 * @throws IOException
	 */
	public static StringBuilder prepareHTMLTableContent(Collection<String> moaCollection, List<SMFPDProdSeasWFEmailBean> listTableData)
			throws WTException, IOException {
		LOGGER.debug("### SMFPDProdSeasEmailUtil.prepareHTMLTableContent - START ###");
		String strUrl = constructFlexPLMURL();
		// Email table column iterator
		StringBuilder sbFPD = new StringBuilder();
		sbFPD.append("<html>");
		sbFPD.append("<head>");
		sbFPD.append("</head>");
		sbFPD.append("<table border='1'>");
		LCSProduct lcsProd;
		LCSSeason season;
		String strOID = "";
		LCSProductQuery pq = new LCSProductQuery();
		LCSSeasonQuery sq = new LCSSeasonQuery();
		LCSProductSeasonLink lcsProdSeas;
		LCSProduct lcsProdSeasRev;
		for (String strTableHeader : moaCollection) {
			sbFPD.append("<th>" + strTableHeader + "</th>");
		}
		for (SMFPDProdSeasWFEmailBean notificationData : listTableData) {

			// Get product object id to set it on Product URL in the email
			// Table

			lcsProd = pq.findProductByNameType(notificationData.getProduct(), FlexTypeCache.getFlexTypeFromPath("Product"));
			season = sq.findSeasonByNameType(notificationData.getSeasonName(), FlexTypeCache.getFlexTypeFromPath("Season"));

			lcsProd = SeasonProductLocator.getProductARev(lcsProd);
			LOGGER.debug("ProdARev= " + lcsProd);
			lcsProdSeas = (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(lcsProd, season);
			LOGGER.debug("lcsProdSeas= " + lcsProdSeas);
			if (lcsProdSeas != null) {
				lcsProdSeasRev = SeasonProductLocator.getProductSeasonRev(lcsProdSeas);
				strOID = LCSQuery.getNumericFromOid(FormatHelper.getVersionId(lcsProdSeasRev));
				LOGGER.debug("Product Seas Numeric oid=" + strOID);
			} else {
				strOID = LCSQuery.getNumericFromOid(FormatHelper.getVersionId(lcsProd));
				LOGGER.debug("Product Numeric oid=" + strOID);
			}

			sbFPD.append("<tr>");
			sbFPD.append(SMEmailUtilConstants.HTML_TD + notificationData.getSeasonName() + SMEmailUtilConstants.HTML_SLASH_TD);
			sbFPD.append(SMEmailUtilConstants.HTML_TD + notificationData.getBrand() + SMEmailUtilConstants.HTML_SLASH_TD);
			sbFPD.append(SMEmailUtilConstants.HTML_TD + "<a href=\"" + strUrl + strOID + "\">" + notificationData.getProduct() + " </a> "
					+ SMEmailUtilConstants.HTML_SLASH_TD);

			if (notificationData.getSku() != null) {
				sbFPD.append(SMEmailUtilConstants.HTML_TD + "<a href=\"" + getSkuFlexPLMURL()
				+ getSkuId(notificationData.getSku(),season) + "\">"
				+ notificationData.getProductionManagerColorway() + " </a> " + SMEmailUtilConstants.HTML_SLASH_TD);
			} else {
				sbFPD.append(SMEmailUtilConstants.HTML_TD + notificationData.getProductionManagerColorway()
				+ SMEmailUtilConstants.HTML_SLASH_TD);
			}

			sbFPD.append("</tr>");
		}
		sbFPD.append("</table>");
		sbFPD.append("</body>");
		sbFPD.append("</html>");

		LOGGER.debug("### SMFPDProdSeasEmailUtil.prepareHTMLTableContent Final Email String= " + sbFPD.toString());
		LOGGER.debug("### SMFPDProdSeasEmailUtil.prepareHTMLTableContent - END ###");
		return sbFPD;
	}
	
	/**
	 * get sku oid fro lcssku.
	 * @param sku
	 * @param season
	 * @return sku oid
	 * @throws WTException
	 */
	private static String getSkuId(LCSSKU sku,LCSSeason season) throws WTException {
		String strOID;
		LCSSKU skuSeasonRev;
		LCSSKU skuArev = SeasonProductLocator.getSKUARev(sku);
		LOGGER.debug("ProdARev= " + skuArev);
		LCSSKUSeasonLink skuSeason = (LCSSKUSeasonLink) LCSSeasonQuery.findSeasonProductLink(skuArev, season);
		LOGGER.debug("lcsProdSeas= " + skuSeason);
		if (skuSeason != null) {
			skuSeasonRev = SeasonProductLocator.getSKUSeasonRev(skuSeason);
			strOID = LCSQuery.getNumericFromOid(FormatHelper.getVersionId(skuSeasonRev));
			LOGGER.debug("Product Seas Numeric oid=" + strOID);
		} else {
			strOID = LCSQuery.getNumericFromOid(FormatHelper.getVersionId(skuArev));
			LOGGER.debug("Product Numeric oid=" + strOID);
		}
		return strOID;
	}
	/**
	 * Method to construct the flexplm url to provide link for Product Name in
	 * email table
	 *
	 * @return
	 * @throws IOException
	 */
	public static String constructFlexPLMURL() throws IOException {
		String fLEXPLMURL;
		LOGGER.debug("### SMFPDProdSeasEmailProcessor.constructFlexPLMURL - START ###");

		fLEXPLMURL = WTProperties.getServerProperties().getProperty("wt.server.codebase")
				+ "/rfa/jsp/main/Main.jsp?newWindowActivity=VIEW_PRODUCT&newWindowOid=VR%3Acom.lcs.wc.product.LCSProduct%3A";

		LOGGER.debug("FLEXPLM_URL==" + fLEXPLMURL);

		LOGGER.debug("### SMFPDProdSeasEmailProcessor.constructFlexPLMURL - END ###");
		return fLEXPLMURL;
	}

	/**
	 * Method to construct the flexplm url to provide link for sku Name in email
	 * table
	 *
	 * @return
	 * @throws IOException
	 */
	public static String getSkuFlexPLMURL() throws IOException {
		String fLEXPLMURL;
		LOGGER.debug("### SMFPDProdSeasEmailProcessor.constructFlexPLMURL - START ###");

		fLEXPLMURL = WTProperties.getServerProperties().getProperty("wt.server.codebase")
				+ "/rfa/jsp/main/Main.jsp?activity=VIEW_SEASON_PRODUCT_LINK&action=INIT&tabPage=PRODUCT&templateType=FRAMES&oid=VR:com.lcs.wc.product.LCSSKU%3A";

		LOGGER.debug("FLEXPLM_URL==" + fLEXPLMURL);

		LOGGER.debug("### SMFPDProdSeasEmailProcessor.constructFlexPLMURL - END ###");
		return fLEXPLMURL;

	}
}
