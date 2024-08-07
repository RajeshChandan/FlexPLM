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
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleQuery;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.sepd.processor.SMSEPDProdSampleEmailProcessor;
import com.sportmaster.wc.emailutility.sepd.processor.SMSEPDProdSampleWFEmailBean;

import wt.fc.WTObject;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.util.WTException;

/**
 * @author Priya Util class for email utility functionality
 *
 */
public class SMSEPDProdSampleEmailUtil {
	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSEPDProdSampleEmailUtil.class);

	/**
	 * Get unique tasks Some tasks for a single object will be assigned to multiple
	 * roles, iterating the tasks and removing the duplicate objects in these cases
	 * 
	 * @param worklist
	 * @return
	 * @throws WTException
	 */
	public static HashSet findUniqueTasks(ArrayList worklist) throws WTException {
		LOGGER.debug("### SMSEPDProdSampleEmailUtil.findUniqueTasks - START ###");
		HashSet uniqueWorkList = new HashSet();
		String workItemObject = "";
		WTObject wtObject;
		Iterator tableIterator = worklist.iterator();
		String createDate;
		LOGGER.debug("=findUniqueTasks size==" + worklist.size());
		FlexObject workItem;
		while (tableIterator.hasNext()) {
			workItem = (FlexObject) tableIterator.next();
			workItemObject = workItem.getString("WORKITEM.CLASSNAMEKEYB4");
			wtObject = (WTObject) LCSQuery.findObjectById(workItemObject);
			// Get Create Date.
			createDate = workItem.getString("WORKITEM.CREATESTAMPA2");
			LOGGER.debug("Task Create Date==" + createDate);
			// Adds only the unique value to the hashset
			uniqueWorkList.add(wtObject);
		}
		// returning worklist objects
		LOGGER.debug("### SMSEPDProdSampleEmailUtil.findUniqueTasks - END ###");
		return uniqueWorkList;
	}

	/**
	 * Method to create bean object
	 * 
	 * @param seasonName
	 * @param productStyle
	 * @param colorway
	 * @param businessSupplier
	 * @param factory
	 * @param sampleName
	 * @param supplierSampleStatus
	 * @param sampleStatus
	 * @param requestCreator
	 * @param wtEmailUserObj
	 * @param emailUser
	 * @param sampleObj
	 * @return
	 */
	public static SMSEPDProdSampleWFEmailBean getBeanObj(String seasonName, String productStyle, String colorway,
			String businessSupplier, String factory, String sampleName, String supplierSampleStatus,
			String sampleStatus, String requestCreator, WTPrincipal wtEmailUserObj, String emailUser,
			LCSSample sampleObj) {
		// String seasonName, String productStyle, String colorway, String
		// businessSupplier, String factory, String sampleName, String
		// supplierSampleStatus, String sampleStatus, String requestCreator, WTUser
		// email, sampleObj
		SMSEPDProdSampleWFEmailBean SMSEPDProdSampleWFEmailBean = new SMSEPDProdSampleWFEmailBean(seasonName,
				productStyle, colorway, businessSupplier, factory, sampleName, supplierSampleStatus, sampleStatus,
				requestCreator, wtEmailUserObj, emailUser, sampleObj);
		return SMSEPDProdSampleWFEmailBean;
	}

	/**
	 * Method to sort objects based on seasonName, productStyle, colorway,
	 * businessSupplier
	 * 
	 * @param notificationList
	 * @return
	 */
	public static List<SMSEPDProdSampleWFEmailBean> sortObjectCollections(
			List<SMSEPDProdSampleWFEmailBean> notificationList) {
		LOGGER.debug("### SMSEPDProdSampleEmailUtil.sortObjectCollections - START ###");
		// sort based on Season attribute
		Comparator<SMSEPDProdSampleWFEmailBean> compareBySeasonName = Comparator
				.comparing(SMSEPDProdSampleWFEmailBean::getSeasonName);

		// sort based on Style attribute
		Comparator<SMSEPDProdSampleWFEmailBean> compareByProductStyle = Comparator
				.comparing(SMSEPDProdSampleWFEmailBean::getProductStyle);

		// sort based on colorway attribute
		Comparator<SMSEPDProdSampleWFEmailBean> compareByColorway = Comparator
				.comparing(SMSEPDProdSampleWFEmailBean::getColorway);

		// sort based on Business Supplier attribute
		Comparator<SMSEPDProdSampleWFEmailBean> compareByBusinessSupplier = Comparator
				.comparing(SMSEPDProdSampleWFEmailBean::getBusinessSupplier);

		// Compare and sort based on seasonName, productStyle, colorway,
		// businessSupplier attribute
		// (multiple fields)
		Comparator<SMSEPDProdSampleWFEmailBean> compareByFull = compareBySeasonName.thenComparing(compareByProductStyle)
				.thenComparing(compareByColorway).thenComparing(compareByBusinessSupplier);

		// Use Comparator
		Collections.sort(notificationList, compareByFull);
		LOGGER.debug("### SMSEPDProdSampleEmailUtil.sortObjectCollections - END ###");
		return notificationList;
	}

	/**
	 * Method to group by users based on user id
	 * 
	 * @param notificationList
	 * @return
	 */
	public static HashMap groupObjectCollections(List<SMSEPDProdSampleWFEmailBean> notificationList) {
		HashMap<String, List<SMSEPDProdSampleWFEmailBean>> finalSortedCollectionMap;
		finalSortedCollectionMap = (HashMap<String, List<SMSEPDProdSampleWFEmailBean>>) notificationList.stream()
				.collect(Collectors.groupingBy(SMSEPDProdSampleWFEmailBean::getEmailUser));
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
			List<SMSEPDProdSampleWFEmailBean> listTableData, Boolean boolSupplier) {
		LOGGER.debug("### SMSEPDProdSampleEmailUtil.prepareHTMLTableContent - START ###");
		String strUrl = SMSEPDProdSampleEmailProcessor.constructFlexPLMURL();
		// Email table column iterator
		Iterator itMOA = moaCollection.iterator();
		String strTableHeader;
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("</head>");
		sb.append("<table border='1'>");
		LCSSample lcsSample;
		LCSSampleQuery sq = new LCSSampleQuery();
		String strOID = "";
		LOGGER.debug(" In prepareHTMLTableContent method - boolSupplier ============= " + boolSupplier);
		while (itMOA.hasNext()) {
			strTableHeader = (String) itMOA.next();
			if (((strTableHeader.equals(SMEmailUtilConstants.BUSINESS_SUPPLIER_GROUP)
					|| (strTableHeader.equals(SMEmailUtilConstants.FACTORY_HEADER_NAME))) && !boolSupplier)
					|| (!(strTableHeader.equals(SMEmailUtilConstants.BUSINESS_SUPPLIER_GROUP))
							&& !(strTableHeader.equals(SMEmailUtilConstants.FACTORY_HEADER_NAME)))) {
				LOGGER.debug("\n strTableHeader ========" + strTableHeader);
				sb.append("<th>" + strTableHeader + "</th>");
			}
		}
		for (SMSEPDProdSampleWFEmailBean notificationData : listTableData) {
			// Get Sample object id to set it on Sample URL in the email Table
			lcsSample = notificationData.getSample();
			strOID = sq.getNumericFromOid(FormatHelper.getNumericObjectIdFromObject(lcsSample));
			LOGGER.debug("\n strOID ========" + strOID);

			sb.append("<tr>");
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getSeasonName()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getProductStyle()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(
					SMEmailUtilConstants.HTML_TD + notificationData.getColorway() + SMEmailUtilConstants.HTML_SLASH_TD);
			if (!boolSupplier) {
				sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getBusinessSupplier()
						+ SMEmailUtilConstants.HTML_SLASH_TD);
				sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getFactory()
						+ SMEmailUtilConstants.HTML_SLASH_TD);
			}
			sb.append(SMEmailUtilConstants.HTML_TD + "<a href=\"" + strUrl + strOID + "\">"
					+ notificationData.getSampleName() + " </a> " + SMEmailUtilConstants.HTML_SLASH_TD);
			if (("2").equals(keyTaskNo) || ("3").equals(keyTaskNo)) {
				sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getSupplierSampleStatus()
						+ SMEmailUtilConstants.HTML_SLASH_TD);
			} else if (("4").equals(keyTaskNo)) {
				sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getSampleStatus()
						+ SMEmailUtilConstants.HTML_SLASH_TD);
			}
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getRequestCreator()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");

		LOGGER.debug("### SMSEPDProdSampleEmailUtil.prepareHTMLTableContent - END ###");
		return sb;
	}

	/**
	 * If Sample Requester is same as production manager user, then do not include
	 * that task in the email
	 * 
	 * @param keyTaskNo
	 * @param userListArr
	 * @param strUserToExclude
	 * @return
	 */
	public static boolean isSameAsUsers(List<Object> userListArr, String keyTaskNo, String strUserToExclude) {
		LOGGER.debug("### SMSEPDProdSampleEmailUtil.isSameAsUsers - START ###");
		LOGGER.debug("Assigned User=" + userListArr.get(1));
		LOGGER.debug("Assigned User Full Name=" + ((WTUser) userListArr.get(0)).getFullName());
		LOGGER.debug("SR User=" + strUserToExclude);
		LOGGER.debug("keyTaskNo=" + keyTaskNo);
		// To check if Sample Requester is not same as Production Manager, if same, do
		// not add to user list, so notification will not be sent for that task
		if (!(strUserToExclude.equalsIgnoreCase(((WTUser) userListArr.get(0)).getFullName()))) {
			return true;
		}
		LOGGER.debug("### SMSEPDProdSampleEmailUtil.isSameAsUsers - END ###");
		return false;
	}

}
