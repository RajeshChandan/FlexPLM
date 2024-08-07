package com.sportmaster.wc.emailutility.queue.sepd.util;

import java.util.ArrayList;
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
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.sepd.processor.SMSEPDImagePageEmailProcessor;
import com.sportmaster.wc.emailutility.sepd.processor.SMSEPDImagePageWFEmailBean;

import wt.fc.WTObject;
import wt.org.WTUser;
import wt.util.WTException;

/**
 * @author Narasimha Bandla.
 *
 * Util class for Image Page email utility functionality.
 *
 */
public class SMSEPDImagePageEmailUtil {
	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSEPDImagePageEmailUtil.class);

	/**
	 * Get unique tasks, Some tasks for a single object will be assigned to multiple
	 * roles, iterating the tasks and removing the duplicate objects in these cases
	 * 
	 * @param worklist
	 * @return
	 * @throws WTException
	 */
	public static HashSet findUniqueTasks(ArrayList worklist) throws WTException {
		LOGGER.debug("### SMSEPDImagePageEmailUtil.findUniqueTasks - START ###");
		
		String workItemObj = "";
		String createDate;
		WTObject wtObj;
		HashSet uniqueWorkList = new HashSet();
		Iterator tableIter = worklist.iterator();
		
		LOGGER.debug("=findUniqueTasks size==" + worklist.size());
		FlexObject workitem;
		while (tableIter.hasNext()) {
			workitem = (FlexObject) tableIter.next();
			workItemObj = workitem.getString("WORKITEM.CLASSNAMEKEYB4");
			wtObj = (WTObject) LCSQuery.findObjectById(workItemObj);
			LOGGER.debug("=wtObj==" + wtObj);
			createDate = workitem.getString("WORKITEM.CREATESTAMPA2");
			LOGGER.debug("=Task Create Date==" + createDate);
			// Adds only the unique value to the hashset
			uniqueWorkList.add(wtObj);
		}
		LOGGER.debug("=uniqueWorkList==" + uniqueWorkList);
		// returning worklist objects
		LOGGER.debug("### SMSEPDImagePageEmailUtil.findUniqueTasks - END ###");
		return uniqueWorkList;
	}


	/**
	 * Method to get Season Name value
	 * 
	 * @param wtObj
	 * @return
	 */
	public static String getSeasonName(LCSProduct wtObj) {
		LOGGER.debug("### SMSEPDImagePageEmailUtil.getSeasonName - START ###");
		String strSeasonName = "";
		strSeasonName = wtObj.getSeasonMaster().getName();
		LOGGER.debug("### SMSEPDImagePageEmailUtil.getSeasonName - END ###");
		return strSeasonName;
	}
	
	/**
	 * Method to get LCSProduct object.
	 * 
	 * @param partMaster
	 * @return LCSProduct
	 * @throws WTException 
	 */
	public static LCSProduct getProductObj(String partMaster) throws WTException {
		LOGGER.debug("### SMSEPDImagePageEmailUtil.getProductObj - START ###");
		LCSProduct prodObj = null;
		LCSPartMaster partMasterObj;
		// Check if partMaster contains value
		if (FormatHelper.hasContent(partMaster) && partMaster.contains("com.lcs.wc.part.LCSPartMaster")) {
			partMasterObj = (LCSPartMaster) LCSQuery.findObjectById(partMaster);
			// Check if partMaster is not null
			if (partMasterObj != null) {
				prodObj = VersionHelper.latestIterationOf(partMasterObj);
				LOGGER.debug("Product Type Name=" + prodObj.getFlexType().getFullName(true));
			}
		}
		return prodObj;
	}

	/**
	 * Method to create bean object
	 * 
	 * @param strSeasonName
	 * @param strDocOID
	 * @param strImagePageName
	 * @param wtEmailUserObj
	 * @param emailUser
	 * @return
	 */
	public static SMSEPDImagePageWFEmailBean getBeanObj(String strSeasonName, String strDocOID, String strImagePageName, WTUser wtEmailUserObj, String emailUser) {
		SMSEPDImagePageWFEmailBean SMSEPDImagePageWFEmailBean = new SMSEPDImagePageWFEmailBean(strSeasonName, strDocOID, strImagePageName, wtEmailUserObj, emailUser);
		return SMSEPDImagePageWFEmailBean;
	}

	/**
	 * Method to sort objects based on Image Page Name
	 * 
	 * @param notificationList
	 * @return
	 */
	public static List<SMSEPDImagePageWFEmailBean> sortObjectCollections(
			List<SMSEPDImagePageWFEmailBean> notificationList) {
		LOGGER.debug("### SMSEPDImagePageEmailUtil.sortObjectCollections - START ###");
		// sort based on Sales Newness attribute
		Comparator<SMSEPDImagePageWFEmailBean> compareBySalesNewNess = Comparator
				.comparing(SMSEPDImagePageWFEmailBean::getImagePageName);

		// Use Comparator
		Collections.sort(notificationList, compareBySalesNewNess);
		LOGGER.debug("### SMSEPDImagePageEmailUtil.sortObjectCollections - END ###");
		return notificationList;
	}

	/**
	 * Method to group by users based on user id
	 * 
	 * @param notificationList
	 * @return
	 */
	public static HashMap groupObjectCollections(List<SMSEPDImagePageWFEmailBean> notificationList) {
		HashMap<String, List<SMSEPDImagePageWFEmailBean>> finalSortedCollectionMap;
		finalSortedCollectionMap = (HashMap<String, List<SMSEPDImagePageWFEmailBean>>) notificationList.stream()
				.collect(Collectors.groupingBy(SMSEPDImagePageWFEmailBean::getEmailUser));
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
	public static StringBuilder prepareHTMLTableContent(List<SMSEPDImagePageWFEmailBean> listTableData) {
		LOGGER.debug("### SMSEPDImagePageEmailUtil.prepareHTMLTableContent - START ###");
		String strUrl = SMSEPDImagePageEmailProcessor.constructFlexPLMURL();
		// Email table column iterator
		StringBuilder sb = new StringBuilder();
		String strOID = "";

		for (SMSEPDImagePageWFEmailBean notificationData : listTableData) {
			strOID = notificationData.getDocOID();
			sb.append("<tr>");
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getSeasoName()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + "<a href=\"" + strUrl + strOID + "\">"
					+ notificationData.getImagePageName() + " </a> " + SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append("</tr>");
		}
		
		LOGGER.debug("### SMSEPDImagePageEmailUtil.prepareHTMLTableContent Final Email String= " + sb.toString());
		LOGGER.debug("### SMSEPDImagePageEmailUtil.prepareHTMLTableContent - END ###");
		return sb;
	}

}
