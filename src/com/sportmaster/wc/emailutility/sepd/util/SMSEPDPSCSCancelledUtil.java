package com.sportmaster.wc.emailutility.sepd.util;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.util.FormatHelper;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.sepd.bean.SMSEPDPSCSCancelledBean;
import com.sportmaster.wc.emailutility.sepd.constants.SMSEPDPSCSCancelledConstants;
import com.sportmaster.wc.emailutility.util.SMMultiObjectAttributeUtil;

import wt.util.WTException;

public class SMSEPDPSCSCancelledUtil {

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSEPDPSCSCancelledUtil.class);
	/**
	 * 
	 * CLASS_NAME.
	 */
	private static final String CLASS_NAME = "SMSEPDPSCSCancelledUtil";

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
	public static List<SMSEPDPSCSCancelledBean> sortObjectCollections(List<SMSEPDPSCSCancelledBean> notificationList) {
		String methodName = "sortObjectCollections()";
		LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_START);
		// sort based on Sales Newness attribute
		Comparator<SMSEPDPSCSCancelledBean> compareBySeason = Comparator
				.comparing(SMSEPDPSCSCancelledBean::getStrSeasonName);

		// sort based on Brand attribute
		Comparator<SMSEPDPSCSCancelledBean> compareByBrand = Comparator.comparing(SMSEPDPSCSCancelledBean::getStrBrand);

		// sort based on product attribute
		Comparator<SMSEPDPSCSCancelledBean> compareByProduct = Comparator
				.comparing(SMSEPDPSCSCancelledBean::getStrProductName);

		// sort based on level attribute
		Comparator<SMSEPDPSCSCancelledBean> compareByLevel = Comparator.comparing(SMSEPDPSCSCancelledBean::getStrLevel);

		// sort based on Colorway attribute
		Comparator<SMSEPDPSCSCancelledBean> compareByColorway = Comparator
				.comparing(SMSEPDPSCSCancelledBean::getStrColorwayName);

		// Compare and sort based on Season attribute, Brand attribute, Product
		// attribute
		// (multiple fields)
		Comparator<SMSEPDPSCSCancelledBean> compareByFull = compareBySeason.thenComparing(compareByBrand)
				.thenComparing(compareByProduct).thenComparing(compareByLevel).thenComparing(compareByColorway);

		// Use Comparator
		Collections.sort(notificationList, compareByFull);
		LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_END);
		return notificationList;
	}

	/**
	 * Method to group by users based on user id
	 * 
	 * @param notificationList
	 * @return
	 */
	public static HashMap groupObjectCollections(List<SMSEPDPSCSCancelledBean> notificationList) {
		HashMap<String, List<SMSEPDPSCSCancelledBean>> finalSortedCollectionMap;
		finalSortedCollectionMap = (HashMap<String, List<SMSEPDPSCSCancelledBean>>) notificationList.stream()
				.collect(Collectors.groupingBy(SMSEPDPSCSCancelledBean::getStrEmailUser));
		return (HashMap) finalSortedCollectionMap;
	}

	public static HashSet findMOACollectionBasedOnType() throws WTException {
		String methodName = "findMOACollectionBasedOnType()";
		LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_START);
		HashSet moaRowHashSet = new HashSet();
		// Get Business Object
		LCSLifecycleManaged businessObject = SMMultiObjectAttributeUtil.findBusinessObjectByName(
				SMSEPDPSCSCancelledConstants.BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_FLEX_TYPE,
				SMSEPDPSCSCancelledConstants.BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_NAME);
		LOGGER.debug("BusinessObject =" + businessObject.getName());
		FlexTypeAttribute moaAttribute = businessObject.getFlexType()
				.getAttribute(SMSEPDPSCSCancelledConstants.BO_SEPD_PRODUCT_SKU_SEASON_CANCELLED_MOA_ATTRIBUTE);
		@SuppressWarnings("unchecked")
		Collection<LCSMOAObject> moaRowCollection = LCSMOAObjectQuery.findMOACollection(businessObject, moaAttribute);
		LOGGER.debug("MOA Rows Based on Type Collection =" + moaRowCollection.size());
		// Iterator
		Iterator rowIterator = moaRowCollection.iterator();
		// Loop through
		while (rowIterator.hasNext()) {
			// Adds only the unique value to the hashset
			moaRowHashSet.add((LCSMOAObject) rowIterator.next());
		}
		// returning moa objects
		LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_END);
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
			List<SMSEPDPSCSCancelledBean> listTableData) {
		String methodName = "prepareHTMLTableContent()";
		LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_START);
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
		for (SMSEPDPSCSCancelledBean notificationData : listTableData) {
			sb.append("<tr>");
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getStrSeasonName()
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + notificationData.getStrBrand() + SMEmailUtilConstants.HTML_SLASH_TD);
			sb.append(SMEmailUtilConstants.HTML_TD + "<a href=\"" + SMMultiObjectAttributeUtil.viewFlexPLMURL("PRODUCT")
					+ notificationData.getStrProductId() + "\">" + notificationData.getStrProductName() + " </a> "
					+ SMEmailUtilConstants.HTML_SLASH_TD);
			if (FormatHelper.hasContent(notificationData.getStrColorwayId())) {
				sb.append(SMEmailUtilConstants.HTML_TD + "<a href=\"" + SMMultiObjectAttributeUtil.viewFlexPLMURL("SKU")
						+ notificationData.getStrColorwayId() + "\">" + notificationData.getStrColorwayName() + " </a> "
						+ SMEmailUtilConstants.HTML_SLASH_TD);
			} else {
				sb.append(SMEmailUtilConstants.HTML_TD + SMEmailUtilConstants.HTML_SLASH_TD);
			}

			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("</body>");
		sb.append("</html>");

		LOGGER.debug(CLASS_NAME + "--" + methodName + "--Final Email String= " + sb.toString());
		LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_END);
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
	public static boolean isSameAsUser(List<Object> userListArr, String strUserToExclude) {
		String methodName = "isSameAsUser()";
		LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_START);
		// To check if Technologist/Engineer is not same as Production Manager, if same,
		// do not add to user list, so notification will not be sent for that task
		LOGGER.debug("Assigned User=" + userListArr.get(1));
		LOGGER.debug("Technologist User=" + strUserToExclude);
		if ((!(strUserToExclude.equals(userListArr.get(1))))) {
			return true;
		}
		LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_END);
		return false;
	}

}
