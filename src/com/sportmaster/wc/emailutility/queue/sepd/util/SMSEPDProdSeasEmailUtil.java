package com.sportmaster.wc.emailutility.queue.sepd.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.sepd.processor.SMSEPDProdSeasEmailProcessor;
import com.sportmaster.wc.emailutility.sepd.processor.SMSEPDProdSeasWFEmailBean;

import wt.fc.WTObject;
import wt.org.WTUser;
import wt.util.WTException;

/**
 * @author Priya Util class for email utility functionality
 *
 */
public class SMSEPDProdSeasEmailUtil {
	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSEPDProdSeasEmailUtil.class);

	/**
	 * Get unique tasks Some tasks for a single object will be assigned to multiple
	 * roles, iterating the tasks and removing the duplicate objects in these cases
	 * 
	 * @param worklist
	 * @return
	 * @throws WTException
	 */
	public static HashSet findUniqueTasks(ArrayList worklist) throws WTException {
		LOGGER.debug("### SMSEPDProdSeasEmailUtil.findUniqueTasks - START ###");
		HashSet uniqueWorkList = new HashSet();
		String workItemObj = "";
		WTObject wtObj;
		Iterator tableIter = worklist.iterator();
		String createDate;
		LOGGER.debug("=findUniqueTasks size==" + worklist.size());
		FlexObject workitem;
		while (tableIter.hasNext()) {
			workitem = (FlexObject) tableIter.next();
			workItemObj = workitem.getString("WORKITEM.CLASSNAMEKEYB4");
			wtObj = (WTObject) LCSQuery.findObjectById(workItemObj);

			createDate = workitem.getString("WORKITEM.CREATESTAMPA2");
			LOGGER.debug("=Task Create Date==" + createDate);
			// Adds only the unique value to the hashset
			uniqueWorkList.add(wtObj);
		}
		// returning worklist objects
		LOGGER.debug("### SMSEPDProdSeasEmailUtil.findUniqueTasks - END ###");
		return uniqueWorkList;
	}

	/**
	 * Method to get Product Name value
	 * 
	 * @param wtObj
	 * @return
	 */
	public static String getProductName(LCSProduct wtObj) {
		LOGGER.debug("### SMSEPDProdSeasEmailUtil.getProductName - START ###");
		String strProductName;
		strProductName = wtObj.getName();
		LOGGER.debug("### SMSEPDProdSeasEmailUtil.getProductName - END ###");
		return strProductName;
	}

	/**
	 * Method to get Season Name value
	 * 
	 * @param wtObj
	 * @return
	 */
	public static String getSeasonName(LCSProduct wtObj) {
		LOGGER.debug("### SMSEPDProdSeasEmailUtil.getSeasonName - START ###");
		String strSeasonName = "";
		strSeasonName = wtObj.getSeasonMaster().getName();
		LOGGER.debug("### SMSEPDProdSeasEmailUtil.getSeasonName - END ###");
		return strSeasonName;
	}

	/**
	 * Method to create bean object
	 * 
	 * @param strProductName
	 * @param strSeasonName
	 * @param strBrand
	 * @param strSalesNewness
	 * @param wtEmailUserObj
	 * @param emailUser
	 * @return
	 */
	public static SMSEPDProdSeasWFEmailBean getBeanObj(String strProductName, String strSeasonName, String strBrand,
			String strSalesNewness, WTUser wtEmailUserObj, String emailUser) {
		// String seasoName, String salesNewNess, String brand, String product, WTUser
		// email
		SMSEPDProdSeasWFEmailBean SMSEPDProdSeasWFEmailBean = new SMSEPDProdSeasWFEmailBean(strSeasonName,
				strSalesNewness, strBrand, strProductName, wtEmailUserObj, emailUser);
		return SMSEPDProdSeasWFEmailBean;
	}

	/**
	 * MEthod to sort objets baased on Sales Newness, Brand, Product,
	 * 
	 * @param notificationList
	 * @return
	 */
	public static List<SMSEPDProdSeasWFEmailBean> sortObjectCollections(
			List<SMSEPDProdSeasWFEmailBean> notificationList) {
		LOGGER.debug("### SMSEPDProdSeasEmailUtil.sortObjectCollections - START ###");
		// sort based on Sales Newness attribute
		Comparator<SMSEPDProdSeasWFEmailBean> compareBySalesNewNess = Comparator
				.comparing(SMSEPDProdSeasWFEmailBean::getSalesNewNess);

		// sort based on Brand attribute
		Comparator<SMSEPDProdSeasWFEmailBean> compareByBrand = Comparator
				.comparing(SMSEPDProdSeasWFEmailBean::getBrand);

		// sort based on product attribute
		Comparator<SMSEPDProdSeasWFEmailBean> compareByProduct = Comparator
				.comparing(SMSEPDProdSeasWFEmailBean::getProduct);

		// Compare and sort based on Sales Newness, Brand attribute, product attribute
		// (multiple fields)
		Comparator<SMSEPDProdSeasWFEmailBean> compareByFull = compareBySalesNewNess.thenComparing(compareByBrand)
				.thenComparing(compareByProduct);

		// Use Comparator
		Collections.sort(notificationList, compareByFull);
		LOGGER.debug("### SMSEPDProdSeasEmailUtil.sortObjectCollections - END ###");
		return notificationList;
	}

	/**
	 * Method to group by users based on user id
	 * 
	 * @param notificationList
	 * @return
	 */
	public static HashMap groupObjectCollections(List<SMSEPDProdSeasWFEmailBean> notificationList) {
		HashMap<String, List<SMSEPDProdSeasWFEmailBean>> finalSortedCollectionMap;
		finalSortedCollectionMap = (HashMap<String, List<SMSEPDProdSeasWFEmailBean>>) notificationList.stream()
				.collect(Collectors.groupingBy(SMSEPDProdSeasWFEmailBean::getEmailUser));
		return (HashMap) finalSortedCollectionMap;
	}

	/**
	 * Method to prepare HTML content for email body
	 * 
	 * @param keyTaskNo
	 * @param moaCollection
	 * @param listTableData
	 * @return
	 */
	public static StringBuilder prepareHTMLTableContent(String keyTaskNo, Collection<String> moaCollection,
			List<SMSEPDProdSeasWFEmailBean> listTableData) {
		LOGGER.debug("### SMSEPDProdSeasEmailUtil.prepareHTMLTableContent - START ###");
		String strUrl = SMSEPDProdSeasEmailProcessor.constructFlexPLMURL();
		// Email table column iterator
		Iterator itMOA = moaCollection.iterator();
		String strTableHeader;
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("</head>");
		sb.append("<table border='1'>");
		LCSProduct lcsProd;
		LCSSeason season;
		String strOID = "";
		LCSProductQuery pq = new LCSProductQuery();
		LCSSeasonQuery sq = new LCSSeasonQuery();
		LCSProductSeasonLink lcsProdSeas;
		LCSProduct lcsProdSeasRev;
		while (itMOA.hasNext()) {
			strTableHeader = (String) itMOA.next();
			sb.append("<th>" + strTableHeader + "</th>");
		}
		for (SMSEPDProdSeasWFEmailBean notificationData : listTableData) {

			try {
				// Get product object id to set it on Product URL in the email Table
				lcsProd = pq.findProductByNameType(notificationData.getProduct(),
						FlexTypeCache.getFlexTypeFromPath("Product"));
				season = sq.findSeasonByNameType(notificationData.getSeasoName(),
						FlexTypeCache.getFlexTypeFromPath("Season"));

				// strOID = pq.getNumericFromOid(FormatHelper.getVersionId(lcsProd));
				lcsProd = SeasonProductLocator.getProductARev(lcsProd);
				LOGGER.debug("ProdARev= " + lcsProd);
				lcsProdSeas = (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(lcsProd, season);
				LOGGER.debug("lcsProdSeas= " + lcsProdSeas);
				if (lcsProdSeas != null) {
					lcsProdSeasRev = SeasonProductLocator.getProductSeasonRev(lcsProdSeas);
					strOID = pq.getNumericFromOid(FormatHelper.getVersionId(lcsProdSeasRev));
					LOGGER.debug("Product Seas Numeric oid=" + strOID);
				} else {
					strOID = pq.getNumericFromOid(FormatHelper.getVersionId(lcsProd));
					LOGGER.debug("Product Numeric oid=" + strOID);
				}

			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sb.append("<tr>");
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getSeasoName()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			if (!("1").equals(keyTaskNo)) {
				sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getSalesNewNess()
						+ SMEmailUtilConstants.HTML_SLASH_TD);
			}
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getBrand() + SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + "<a href=\"" + strUrl + strOID + "\">"
					+ notificationData.getProduct() + " </a> " + SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");

		LOGGER.debug("### SMSEPDProdSeasEmailUtil.prepareHTMLTableContent Final Email String= " + sb.toString());
		LOGGER.debug("### SMSEPDProdSeasEmailUtil.prepareHTMLTableContent - END ###");
		return sb;
	}

	/**
	 * If Technologist/Engineer is same as production manager user, then do not
	 * include that task in the email
	 * 
	 * @param userListArr
	 * @param keyTaskNo
	 * @param strUserToExclude
	 * @return
	 */
	public static boolean isSameAsUser(List<Object> userListArr, String keyTaskNo, String strUserToExclude) {
		LOGGER.debug("### SMSEPDProdSeasEmailUtil.isSameAsUser - START ###");
		// To check if Technologist/Engineer is not same as Production Manager, if same,
		// do not add to user list, so notification will not be sent for that task
		LOGGER.debug("Assigned User=" + userListArr.get(1));
		LOGGER.debug("Technologist User=" + strUserToExclude);
		LOGGER.debug("keyTaskNo=" + keyTaskNo);
		if ((!(strUserToExclude.equals(userListArr.get(1))) && !("1").equals(keyTaskNo)) || ("1").equals(keyTaskNo)) {
			return true;
		}
		LOGGER.debug("### SMSEPDProdSeasEmailUtil.isSameAsUser - END ###");
		return false;
	}

}
