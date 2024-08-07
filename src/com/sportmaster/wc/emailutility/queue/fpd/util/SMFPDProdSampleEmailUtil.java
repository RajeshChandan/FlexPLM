package com.sportmaster.wc.emailutility.queue.fpd.util;

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
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleQuery;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.fpd.processor.SMFPDProdSampleEmailProcessor;
import com.sportmaster.wc.emailutility.fpd.processor.SMFPDProdSampleWFEmailBean;

import wt.fc.WTObject;
import wt.org.WTPrincipal;
import wt.util.WTException;

/**
 * @author Priya Util class for email utility functionality
 *
 */
public class SMFPDProdSampleEmailUtil {
	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMFPDProdSampleEmailUtil.class);

	/**
	 * Get unique tasks Some tasks for a single object will be assigned to multiple
	 * roles, iterating the tasks and removing the duplicate objects in these cases
	 * 
	 * @param worklist
	 * @return
	 * @throws WTException
	 */
	public static HashSet findUniqueTasks(ArrayList worklist) throws WTException {
		LOGGER.debug("### SMFPDProdSampleEmailUtil.findUniqueTasks - START ###");
		HashSet uniqueWorkList = new HashSet();
		String workItemObj = "";
		WTObject obj;
		Iterator tableIter = worklist.iterator();
		String taskCreateDate;
		LOGGER.debug("=findUniqueTasks size==" + worklist.size());
		FlexObject workitem;
		while (tableIter.hasNext()) {
			workitem = (FlexObject) tableIter.next();
			workItemObj = workitem.getString("WORKITEM.CLASSNAMEKEYB4");
			obj = (WTObject) LCSQuery.findObjectById(workItemObj);

			taskCreateDate = workitem.getString("WORKITEM.CREATESTAMPA2");
			LOGGER.debug("=Task Create Date==" + taskCreateDate);
			// Adds only the unique value to the hashset
			uniqueWorkList.add(obj);
		}
		// returning worklist objects
		LOGGER.debug("### SMFPDProdSampleEmailUtil.findUniqueTasks - END ###");
		return uniqueWorkList;
	}

	/**
	 * Method to create bean object
	 * 
	 * @param seasonName
	 * @param brand
	 * @param style
	 * @param colorway
	 * @param businessSupplier
	 * @param requestName
	 * @param sampleSize
	 * @param requestCreator
	 * @param supplierStatus
	 * @param wtEmailUserObj
	 * @param emailUser
	 * @param sampleObj
	 * @return
	 */
	public static SMFPDProdSampleWFEmailBean getBeanObj(String seasonName, String brand, String style, String colorway,
			String businessSupplier, String requestName, String sampleSize, String requestCreator,
			String supplierStatus, WTPrincipal wtEmailUserObj, String emailUser, LCSSample sampleObj) {
		// String seasonName, String brand, String style, String colorway, String
		// businessSupplier, String requestName, String sampleSize, String
		// requestCreator, String supplierStatus, sampleObj
		SMFPDProdSampleWFEmailBean SMFPDProdSampleWFEmailBean = new SMFPDProdSampleWFEmailBean(seasonName, brand, style,
				colorway, businessSupplier, requestName, sampleSize, requestCreator, supplierStatus, wtEmailUserObj,
				emailUser, sampleObj);
		return SMFPDProdSampleWFEmailBean;
	}

	/**
	 * Method to sort objects based on season, brand, style, colorway,
	 * businessSupplier
	 * 
	 * @param notificationList
	 * @return
	 */
	public static List<SMFPDProdSampleWFEmailBean> sortObjectCollections(
			List<SMFPDProdSampleWFEmailBean> notificationList) {
		LOGGER.debug("### SMFPDProdSampleEmailUtil.sortObjectCollections - START ###");
		// sort based on Season attribute
		Comparator<SMFPDProdSampleWFEmailBean> compareBySeason = Comparator
				.comparing(SMFPDProdSampleWFEmailBean::getSeasonName);

		// sort based on Brand attribute
		Comparator<SMFPDProdSampleWFEmailBean> compareByBrand = Comparator
				.comparing(SMFPDProdSampleWFEmailBean::getBrand);

		// sort based on Style attribute
		Comparator<SMFPDProdSampleWFEmailBean> compareByStyle = Comparator
				.comparing(SMFPDProdSampleWFEmailBean::getStyle);

		// sort based on Business Supplier attribute
		Comparator<SMFPDProdSampleWFEmailBean> compareByBusinessSupplier = Comparator
				.comparing(SMFPDProdSampleWFEmailBean::getBusinessSupplier);

		// Compare and sort based on season, brand, style, colorway, businessSupplier
		// (multiple fields)
		Comparator<SMFPDProdSampleWFEmailBean> compareByFull = compareBySeason.thenComparing(compareByBrand)
				.thenComparing(compareByStyle).thenComparing(compareByBusinessSupplier);

		// Use Comparator
		Collections.sort(notificationList, compareByFull);
		LOGGER.debug("### SMFPDProdSampleEmailUtil.sortObjectCollections - END ###");
		return notificationList;
	}

	/**
	 * Method to group by users based on user id
	 * 
	 * @param notificationList
	 * @return
	 */
	public static HashMap groupObjectCollections(List<SMFPDProdSampleWFEmailBean> notificationList) {
		HashMap<String, List<SMFPDProdSampleWFEmailBean>> finalSortedCollectionMap;
		finalSortedCollectionMap = (HashMap<String, List<SMFPDProdSampleWFEmailBean>>) notificationList.stream()
				.collect(Collectors.groupingBy(SMFPDProdSampleWFEmailBean::getEmailUser));
		return (HashMap) finalSortedCollectionMap;
	}

	/**
	 * Method to prepare HTML content for email body
	 * 
	 * @param keyTaskNo
	 * @param moaCollection
	 * @param listTableData
	 * @param bSupplier
	 * @return
	 */
	public static StringBuilder prepareHTMLTableContent(Collection<String> moaCollection,
			List<SMFPDProdSampleWFEmailBean> listTableData, Boolean bSupplier) {
		LOGGER.debug("### SMFPDProdSampleEmailUtil.prepareHTMLTableContent - START ###");
		String strUrl = SMFPDProdSampleEmailProcessor.constructFlexPLMURL();
		// Email table column iterator
		Iterator itMOA = moaCollection.iterator();
		String strTableHeader;
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("</head>");
		sb.append("<table border='1'>");
		String strOID = "";
		LCSSample lcsSample;
		LCSSampleQuery sq = new LCSSampleQuery();
		LOGGER.debug(" In prepareHTMLTableContent method - bSupplier ============= " + bSupplier);
		while (itMOA.hasNext()) {
			strTableHeader = (String) itMOA.next();
			if ((strTableHeader.equals(SMEmailUtilConstants.BUSINESS_SUPPLIER_GROUP) && !bSupplier)
					|| (!(strTableHeader.equals(SMEmailUtilConstants.BUSINESS_SUPPLIER_GROUP)))) {
				sb.append("<th>" + strTableHeader + "</th>");
			}
		}
		for (SMFPDProdSampleWFEmailBean notificationData : listTableData) {
			// Get Sample object id to set it on Sample URL in the email Table
			lcsSample = notificationData.getSample();

			strOID = sq.getNumericFromOid(FormatHelper.getNumericObjectIdFromObject(lcsSample));
			sb.append("<tr>");
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getSeasonName()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getBrand() + SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getStyle() + SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(
					SMEmailUtilConstants.HTML_TD + notificationData.getColorway() + SMEmailUtilConstants.HTML_SLASH_TD);
			if (!bSupplier) {
				sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getBusinessSupplier()
						+ SMEmailUtilConstants.HTML_SLASH_TD);
			}
			sb.append(SMEmailUtilConstants.HTML_TD + "<a href=\"" + strUrl + strOID + "\">"
					+ notificationData.getRequestName() + " </a> " + SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getSampleSize()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getRequestCreator()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getSupplierStatus()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");

		LOGGER.debug("### SMFPDProdSampleEmailUtil.prepareHTMLTableContent Final Email String= " + sb.toString());
		LOGGER.debug("### SMFPDProdSampleEmailUtil.prepareHTMLTableContent - END ###");
		return sb;
	}

}