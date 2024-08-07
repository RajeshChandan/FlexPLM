package com.sportmaster.wc.emailutility.fpd.util;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.fpd.bean.SMFPDColorwayCreateMOABean;
import com.sportmaster.wc.emailutility.fpd.constants.SMFPDColorwayCreateMOAConstants;
import com.sportmaster.wc.emailutility.util.SMMultiObjectAttributeUtil;

import wt.util.WTException;

public class SMFPDColorwayCreateMOAUtil {

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMFPDColorwayCreateMOAUtil.class);

	/**
	 * CLASS_NAME.
	 */
	private static final String CLASS_NAME = "SMFPDColorwayCreateMOAUtil";
	/**
	 * METHOD_START.
	 */
	public static final String METHOD_START = "--Start";
	/**
	 * METHOD_END.
	 */
	public static final String METHOD_END = "--End";

	/**
	 * Methods to sort objects based on Season, Brand, Product,
	 * 
	 * @param notificationList
	 * @return
	 */
	public static List<SMFPDColorwayCreateMOABean> sortObjectCollections(
			List<SMFPDColorwayCreateMOABean> notificationList) {
		String methodName = "sortObjectCollections()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// sort based on Sales Newness attribute
		Comparator<SMFPDColorwayCreateMOABean> compareBySeason = Comparator
				.comparing(SMFPDColorwayCreateMOABean::getSeasonName);

		// sort based on Brand attribute
		Comparator<SMFPDColorwayCreateMOABean> compareByBrand = Comparator
				.comparing(SMFPDColorwayCreateMOABean::getBrand);

		// sort based on product attribute
		Comparator<SMFPDColorwayCreateMOABean> compareByProduct = Comparator
				.comparing(SMFPDColorwayCreateMOABean::getProductName);

		// sort based on colorway attribute
		Comparator<SMFPDColorwayCreateMOABean> compareByNewColorway = Comparator
				.comparing(SMFPDColorwayCreateMOABean::getNewColorwayName);

		// Compare and sort based on Season attribute, Brand attribute, Product,
		// Colorway
		// attribute
		// (multiple fields)
		Comparator<SMFPDColorwayCreateMOABean> compareByFull = compareBySeason.thenComparing(compareByBrand)
				.thenComparing(compareByProduct).thenComparing(compareByNewColorway);

		// Use Comparator
		Collections.sort(notificationList, compareByFull);
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
		return notificationList;
	}

	/**
	 * Method to group by users based on user id
	 * 
	 * @param notificationList
	 * @return
	 */
	public static HashMap groupObjectCollections(List<SMFPDColorwayCreateMOABean> notificationList) {
		String methodName = "groupObjectCollections()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		HashMap<String, List<SMFPDColorwayCreateMOABean>> finalSortedCollectionMap;
		finalSortedCollectionMap = (HashMap<String, List<SMFPDColorwayCreateMOABean>>) notificationList.stream()
				.collect(Collectors.groupingBy(SMFPDColorwayCreateMOABean::getEmailUser));
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
		return (HashMap) finalSortedCollectionMap;
	}

	/**
	 * @return
	 * @throws WTException
	 */
	public static HashSet findMOACollectionBasedOnType() throws WTException {
		String methodName = "findMOACollectionBasedOnType()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		HashSet moaRowHashSet = new HashSet();
		// Get Business Object
		LCSLifecycleManaged businessObject = SMMultiObjectAttributeUtil.findBusinessObjectByName(
				SMFPDColorwayCreateMOAConstants.BO_FPD_CREATE_COLOURWAY_FLEX_TYPE,
				SMFPDColorwayCreateMOAConstants.BO_FPD_CREATE_COLOURWAY_NAME);
		LOGGER.debug(CLASS_NAME + "--" + methodName + "--BusinessObject =" + businessObject.getName());
		FlexTypeAttribute moaAttribute = businessObject.getFlexType()
				.getAttribute(SMFPDColorwayCreateMOAConstants.BO_FPD_CREATE_COLOURWAY_MOA_ATTRIBUTE);
		@SuppressWarnings("unchecked")
		Collection<LCSMOAObject> moaRowCollection = LCSMOAObjectQuery.findMOACollection(businessObject, moaAttribute);
		LOGGER.debug(
				CLASS_NAME + "--" + methodName + "--MOA Rows Based on Type Collection =" + moaRowCollection.size());
		// Iterator
		Iterator rowIterator = moaRowCollection.iterator();
		// Loop through
		while (rowIterator.hasNext()) {
			// Adds only the unique value to the hashset
			moaRowHashSet.add((LCSMOAObject) rowIterator.next());
		}
		// returning MOA objects
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
		return moaRowHashSet;
	}

	/**
	 * Method to prepare HTML content for email body
	 * 
	 * @param keyTaskNo
	 * @param moaCollection
	 * @param listTableData
	 * @return
	 */
	public static StringBuilder prepareHTMLTableContent(Collection<String> moaCollection,
			List<SMFPDColorwayCreateMOABean> listTableData) {
		String methodName = "prepareHTMLTableContent()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Email table column iterator
		Iterator itMOA = moaCollection.iterator();
		String strTableHeader;
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		sb.append("<head>");
		sb.append("</head>");
		sb.append("<table border='1'>");
		// Header
		while (itMOA.hasNext()) {
			strTableHeader = (String) itMOA.next();
			sb.append("<th>" + strTableHeader + "</th>");
		}
		// Loop through rows
		for (SMFPDColorwayCreateMOABean notificationData : listTableData) {
			sb.append("<tr>");
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getSeasonName()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getBrand() + SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + "<a href=\"" + SMMultiObjectAttributeUtil.viewFlexPLMURL("PRODUCT")
					+ notificationData.getProductId() + "\">" + notificationData.getProductName() + " </a> "
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + "<a href=\"" + SMMultiObjectAttributeUtil.viewFlexPLMURL("SKU")
					+ notificationData.getNewColorwayId() + "\">" + notificationData.getNewColorwayName() + " </a> "
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");
		LOGGER.debug(CLASS_NAME + "--" + methodName + "--Final Email String= " + sb.toString());
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
		return sb;
	}

}
