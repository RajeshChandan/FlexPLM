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
import com.lcs.wc.sourcing.LCSCostSheet;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.sepd.processor.SMSEPDProdCostsheetWFEmailBean;

import wt.fc.WTObject;
import wt.org.WTPrincipal;
import wt.util.WTException;

/**
 * @author Priya Util class for email utility functionality
 *
 */
public class SMSEPDProdCostsheetEmailUtil {
	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSEPDProdCostsheetEmailUtil.class);

	/**
	 * Get unique tasks Some tasks for a single object will be assigned to multiple
	 * roles, iterating the tasks and removing the duplicate objects in these cases
	 * 
	 * @param worklist
	 * @return
	 * @throws WTException
	 */
	public static HashSet findUniqueTasks(ArrayList worklist) throws WTException {
		LOGGER.debug("### SMSEPDProdCostsheetEmailUtil.findUniqueTasks - START ###");
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
		LOGGER.debug("### SMSEPDProdCostsheetEmailUtil.findUniqueTasks - END ###");
		return uniqueWorkList;
	}

	/**
	 * Method to create bean object
	 * 
	 * @param seasonName
	 * @param styleName
	 * @param sourceName
	 * @param costingStage
	 * @param costsheetNo
	 * @param costsheetName
	 * @param wtEmailUserObj
	 * @param emailUser
	 * @param costsheet
	 * @return
	 */
	public static SMSEPDProdCostsheetWFEmailBean getBeanObj(String seasonName, String styleName, String sourceName,
			String costingStage, Long costsheetNo, String costsheetName, WTPrincipal emailUserObj, String emailUser,
			LCSCostSheet costsheet) {
		// String seasonName, String styleName, String sourceName, String costingStage,
		// Long costsheetNo, String costsheetName, WTPrincipal emailUserObj, String
		// emailUser, LCSCostSheet costsheet
		SMSEPDProdCostsheetWFEmailBean SMSEPDProdCostsheetWFEmailBean = new SMSEPDProdCostsheetWFEmailBean(seasonName,
				styleName, sourceName, costingStage, costsheetNo, costsheetName, emailUserObj, emailUser, costsheet);
		return SMSEPDProdCostsheetWFEmailBean;
	}

	/**
	 * Method to sort objects based on seasonName, styleName, sourceName
	 * 
	 * @param notificationList
	 * @return
	 */
	public static List<SMSEPDProdCostsheetWFEmailBean> sortObjectCollections(
			List<SMSEPDProdCostsheetWFEmailBean> notificationList) {
		LOGGER.debug("### SMSEPDProdCostsheetEmailUtil.sortObjectCollections - START ###");
		// sort based on Season attribute
		Comparator<SMSEPDProdCostsheetWFEmailBean> compareBySeasonName = Comparator
				.comparing(SMSEPDProdCostsheetWFEmailBean::getSeasonName);

		// sort based on Style attribute
		Comparator<SMSEPDProdCostsheetWFEmailBean> compareByStyleName = Comparator
				.comparing(SMSEPDProdCostsheetWFEmailBean::getStyleName);

		// sort based on source attribute
		Comparator<SMSEPDProdCostsheetWFEmailBean> compareBySourceName = Comparator
				.comparing(SMSEPDProdCostsheetWFEmailBean::getSourceName);

		// Compare and sort based on seasonName, styleName, sourceName attribute
		// (multiple fields)
		Comparator<SMSEPDProdCostsheetWFEmailBean> compareByFull = compareBySeasonName.thenComparing(compareByStyleName)
				.thenComparing(compareBySourceName);

		// Use Comparator
		Collections.sort(notificationList, compareByFull);
		LOGGER.debug("### SMSEPDProdCostsheetEmailUtil.sortObjectCollections - END ###");
		return notificationList;
	}

	/**
	 * Method to group by users based on user id
	 * 
	 * @param notificationList
	 * @return
	 */
	public static HashMap groupObjectCollections(List<SMSEPDProdCostsheetWFEmailBean> notificationList) {
		HashMap<String, List<SMSEPDProdCostsheetWFEmailBean>> finalSortedCollectionMap;
		finalSortedCollectionMap = (HashMap<String, List<SMSEPDProdCostsheetWFEmailBean>>) notificationList.stream()
				.collect(Collectors.groupingBy(SMSEPDProdCostsheetWFEmailBean::getEmailUser));
		return (HashMap) finalSortedCollectionMap;
	}

	/**
	 * Method to prepare HTML content for email body
	 * 
	 * @param moaCollection
	 * @param listTableData
	 * @return
	 */
	public static StringBuilder prepareHTMLTableContent(Collection<String> moaCollection,
			List<SMSEPDProdCostsheetWFEmailBean> listTableData) {
		LOGGER.debug("### SMSEPDProdCostsheetEmailUtil.prepareHTMLTableContent - START ###");
		// String strUrl = SMSEPDProdCostsheetEmailProcessor.constructFlexPLMURL();
		// Email table column iterator
		Iterator itMOA = moaCollection.iterator();
		String strTableHeader;
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("</head>");
		sb.append("<table border='1'>");
		while (itMOA.hasNext()) {
			strTableHeader = (String) itMOA.next();
			sb.append("<th>" + strTableHeader + "</th>");
		}
		for (SMSEPDProdCostsheetWFEmailBean notificationData : listTableData) {
			sb.append("<tr>");
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getSeasonName()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getStyleName()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getSourceName()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getCostingStage()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getCostsheetNo()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getCostsheetName()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");

		LOGGER.debug("### SMSEPDProdCostsheetEmailUtil.prepareHTMLTableContent - END ###");
		return sb;
	}

}
