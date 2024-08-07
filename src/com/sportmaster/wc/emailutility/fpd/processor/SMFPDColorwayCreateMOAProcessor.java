package com.sportmaster.wc.emailutility.fpd.processor;

import java.util.*;
import org.apache.log4j.Logger;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.UserGroupHelper;
import com.sportmaster.wc.emailutility.fpd.bean.SMFPDColorwayCreateMOABean;
import com.sportmaster.wc.emailutility.fpd.constants.SMFPDColorwayCreateMOAConstants;
import com.sportmaster.wc.emailutility.fpd.util.SMFPDColorwayCreateMOAUtil;
import com.sportmaster.wc.emailutility.util.SMEmailNotificationUtil;
import com.sportmaster.wc.emailutility.util.SMMultiObjectAttributeUtil;

import wt.org.WTUser;
import wt.util.WTException;

/**
 * @author root
 *
 */
public class SMFPDColorwayCreateMOAProcessor {

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMFPDColorwayCreateMOAProcessor.class);
	/**
	 * CLASS_NAME.
	 */
	public static final String CLASS_NAME = "SMFPDColorwayCreateMOAProcessor";
	/**
	 * METHOD_START.
	 */
	public static final String METHOD_START = "--Start";
	/**
	 * METHOD_END.
	 */
	public static final String METHOD_END = "--End";

	/**
	 * EXCEPTION_OCCURED.
	 */
	public static final String EXCEPTION_OCCURED = "--Exception Ocuured: ";

	/**
	 * Constructor.
	 */
	protected SMFPDColorwayCreateMOAProcessor() {
		// constructor
	}

	/**
	 * processFPDColorwayCreateScheduleQueue.
	 */
	public static void processFPDColorwayCreateScheduleQueue() {
		String methodName = "processFPDColorwayCreateScheduleQueue()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		try {
			ClientContext lcsContext = null;
			wt.org.WTUser from = null;
			try {
				lcsContext = ClientContext.getContext();
				from = UserGroupHelper.getWTUser(lcsContext.getUserName());
			} catch (WTException e1) {
				LOGGER.error(CLASS_NAME + "--" + methodName + EXCEPTION_OCCURED + e1.getMessage());
				e1.printStackTrace();
			}
			// Get Colorway Create MOA Rows Data
			HashSet hsColorwayCreateMOACollection = SMFPDColorwayCreateMOAUtil.findMOACollectionBasedOnType();
			LOGGER.info(CLASS_NAME + "--" + methodName + "--Collection of MOA" + hsColorwayCreateMOACollection.size());

			// Get the user attributes/ groups for whom email has to be sent for current
			HashMap userListMap = SMEmailNotificationUtil.getUserAttributes(
					LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.fpd.cw.create.emailTo",
							"user^LCSProductSeasonLink$smProductionManager"));
			LOGGER.info(CLASS_NAME + "--" + methodName + "--userListMap=" + userListMap);
			ArrayList vUserAttributes = new ArrayList();
			if (userListMap.containsKey("user")) {
				vUserAttributes = (ArrayList) userListMap.get("user");
			}
			LOGGER.info(CLASS_NAME + "--" + methodName + "--Users attributes for sending email=" + vUserAttributes);
			// Initialization
			List<SMFPDColorwayCreateMOABean> notificationList = new ArrayList();
			// Get notification email properties
			String systemInfo = LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.SystemInfo");
			String strHeader = LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.MailHeader");
			SMEmailNotificationUtil smEmailNotificationUtil = new SMEmailNotificationUtil();
			String mailContent = String
					.format(LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.fpd.cw.create.mailContent",
							"The following Colorways were created." + ""));
			String mailSubject = String
					.format(LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.fpd.cw.create.mailSubject",
							"FlexPLM Notification: Colorways created - DO NOT REPLY"));
			// Prepare Notification Bean
			prepareNotificationBeanList(hsColorwayCreateMOACollection, vUserAttributes, notificationList);
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--notificationList.size()=" + notificationList.size());
			// Sort the list
			notificationList = SMFPDColorwayCreateMOAUtil.sortObjectCollections(notificationList);
			// group the list with key = user, value = bean object, so email can be sent
			// consolidated objects for a single user
			HashMap finalSortedCollectionMap = SMFPDColorwayCreateMOAUtil.groupObjectCollections(notificationList);
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Final Sorted Collection Map=" + finalSortedCollectionMap);
			// Initialization
			WTUser wtToEmailUser = null;
			//RCOM107 
			String emailTableColumns=LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.fpd.cw.create.emailColumns",
					"Season|~*~|Brand|~*~|Product|~*~|New Colorway");
			// Iterate the map, and for each user, construct the email and send mail
			Iterator itfinalSortedCollectionMap = finalSortedCollectionMap.keySet().iterator();
			while (itfinalSortedCollectionMap.hasNext()) {
				wtToEmailUser = sendEmailForMOARows(from, finalSortedCollectionMap, systemInfo, strHeader,
						smEmailNotificationUtil, mailContent, mailSubject, wtToEmailUser, itfinalSortedCollectionMap, emailTableColumns);
			}
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Delete MOA Rows-Start");
			// Remove all MOA Rows
			SMMultiObjectAttributeUtil.deleteMOACollection(hsColorwayCreateMOACollection);
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Delete MOA Rows-End");

		} catch (WTException e) {
			LOGGER.error(CLASS_NAME + "--" + methodName + EXCEPTION_OCCURED + e.getLocalizedMessage());
			e.printStackTrace();
		}
		LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * @param hsColorwayCreateMOACollection
	 * @param notificationList
	 * @throws WTException
	 */
	private static void prepareNotificationBeanList(HashSet hsColorwayCreateMOACollection, ArrayList vUserAttributes,
			List<SMFPDColorwayCreateMOABean> notificationList) throws WTException {
		String methodName = "prepareNotificationBeanList()";
		LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_START);
		// Initialization
		LCSMOAObject obj = null;
		String seasonId = "";
		String seasonName = "";
		LCSSeason seasonObj = null;
		String productId = "";
		String productName = "";
		LCSProduct prodObj = null;
		String smBrand = "";
		String newColorwayId = "";
		String newColorwayName = "";
		LCSSKU skuObj = null;
		LCSSeasonProductLink seasonProductLink = null;
		//RCOM107 
		String brandAttributeKey= LCSProperties.get("com.sportmaster.wc.emailutility.processor.product.lcsproduct.brand");
		// Get a Iterator
		Iterator moaRowIterator = hsColorwayCreateMOACollection.iterator();
		// Loop through iterator
		while (moaRowIterator.hasNext()) {
			// Get LCSMOAObect
			obj = (LCSMOAObject) moaRowIterator.next();
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--" + obj);
			// Get Season
			seasonId = String.valueOf(obj.getValue(SMFPDColorwayCreateMOAConstants.MOA_FPD_SEASON_ID));
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Season Id =" + seasonId);
			seasonObj = (LCSSeason) LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:" + seasonId);
			seasonName = String.valueOf(seasonObj.getValue(SMFPDColorwayCreateMOAConstants.SEASON_NAME));
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Season Name =" + seasonName);
			// Get Product
			productId = String.valueOf(obj.getValue(SMFPDColorwayCreateMOAConstants.MOA_FPD_PRODUCT_ID));
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Product Id =" + productId);
			prodObj = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:" + productId);
			productName = String.valueOf(prodObj.getName());
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Product Name =" + productName);
			// Get Brand
			//RCOM107 
			smBrand = SMEmailNotificationUtil.getObjectValue((LCSProduct) prodObj, brandAttributeKey,
					((LCSProduct) prodObj).getFlexType());
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Brand Name =" + smBrand);
			// Get Colorway
			newColorwayId = String.valueOf(obj.getValue(SMFPDColorwayCreateMOAConstants.MOA_FPD_CW_ID));
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Colorway Id =" + newColorwayId);
			skuObj = (LCSSKU) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:" + newColorwayId);
			newColorwayName = String.valueOf(skuObj.getValue(SMFPDColorwayCreateMOAConstants.SKU_NAME));
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Colorway Name =" + newColorwayName);
			// Check of Product Version
			if (!"A".equalsIgnoreCase(prodObj.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				seasonProductLink = SeasonProductLocator.getSeasonProductLink(prodObj);
				// User attributes iterator
				Iterator itvUserAttributes = vUserAttributes.iterator();
				// Method to prepare bean obj
				prepareBeanObjects(notificationList, seasonName, smBrand, productId, productName, newColorwayId,
						newColorwayName, itvUserAttributes, seasonProductLink);
			}
		}
		LOGGER.debug(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * @param notificationList
	 * @param strSeasonName
	 * @param strBrand
	 * @param strProductName
	 * @param strColorwayName
	 * @param itvUserAttributes
	 * @param spLink
	 */
	@SuppressWarnings("unchecked")
	private static void prepareBeanObjects(List<SMFPDColorwayCreateMOABean> notificationList, String strSeasonName,
			String strBrand, String strProductId, String strProductName, String strColorwayId, String strColorwayName,
			Iterator itvUserAttributes, LCSSeasonProductLink spLink) {
		String methodName = "prepareBeanObjects()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Get unique user list associated to Season Product Link
		HashMap hmUserList = getUniqueUserListMap(itvUserAttributes, spLink);
		// Only when user list is not empty, create the notification bean object
		if (!hmUserList.isEmpty()) {
			// Loop through each attribute
			Iterator iterator = hmUserList.entrySet().iterator();
			// Iterate user attributes, in case notification is to be sent for multiple
			// users , iterate and for each user create the SMSEPDPSCSCancelledBean
			// object, so email will be sent for all users.
			while (iterator.hasNext()) {
				Map.Entry me = (Map.Entry) iterator.next();
				// LOGGER.debug("Key: " + me.getKey() + " & Value: " + me.getValue());
				// Prepare Bean Object
				SMFPDColorwayCreateMOABean beanObject = new SMFPDColorwayCreateMOABean(strSeasonName, strBrand,
						strProductId, strProductName, strColorwayId, strColorwayName, (WTUser) me.getValue(),
						(String) me.getKey());
				// Set product name, season name, brand, colorway name, email to
				// bean obj and add that to a list
				notificationList.add(beanObject);
			}
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * @param itvUserAttributes
	 * @param spLink
	 * @return
	 */
	private static HashMap getUniqueUserListMap(Iterator itvUserAttributes, LCSSeasonProductLink spLink) {
		String methodName = "getAssociatedUniqueUserListMap()";
		HashMap hmUserList = new HashMap();
		String strAttributeUser;
		StringTokenizer stUserAttribute;
		while (itvUserAttributes.hasNext()) {
			strAttributeUser = (String) itvUserAttributes.next();
			// defined in property = objectType$attributekey, tokens and get user
			// attribute value
			stUserAttribute = new StringTokenizer(strAttributeUser, "$");
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--strUserAttribute= " + strAttributeUser);
			if (("LCSProductSeasonLink").equals(stUserAttribute.nextElement())) {
				// Method to get the user list attribute value and flexobject
				HashMap rowUserMap = SMMultiObjectAttributeUtil.getUserAssignedMap((LCSSeasonProductLink) spLink,
						(String) stUserAttribute.nextElement());
				hmUserList.putAll(rowUserMap);
			}
		}
		LOGGER.debug(CLASS_NAME + "--" + methodName + "--hmUserList= " + hmUserList);
		return hmUserList;
	}

	/**
	 * Method to send email
	 * 
	 * @param keyTaskNo
	 * @param from
	 * @param finalSortedCollectionMap
	 * @param systemInfo
	 * @param strHeader
	 * @param smEmailNotificationUtil
	 * @param mailContent
	 * @param mailSubject
	 * @param wtToEmailUser
	 * @param itfinalSortedCollectionMap
	 * @param emailTableColumns 
	 * @return
	 */
	private static WTUser sendEmailForMOARows(wt.org.WTUser from, HashMap finalSortedCollectionMap, String systemInfo,
			String strHeader, SMEmailNotificationUtil smEmailNotificationUtil, String mailContent, String mailSubject,
			WTUser wtToEmailUser, Iterator itfinalSortedCollectionMap, String emailTableColumns) {
		String methodName = "sendEmailForMOARows()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		StringBuilder sb;
		String toEmailUser;
		ArrayList vecToList = new ArrayList();
		toEmailUser = (String) itfinalSortedCollectionMap.next();
		try {
			wtToEmailUser = wt.org.OrganizationServicesHelper.manager.getUser(toEmailUser);
		} catch (WTException e) {
			LOGGER.error(CLASS_NAME + "--" + methodName + EXCEPTION_OCCURED + e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb = new StringBuilder();
		sb.append(mailContent).append("<br>").append("<br>");
		//RCOM107
		sb.append(SMFPDColorwayCreateMOAUtil.prepareHTMLTableContent(
				MOAHelper.getMOACollection(emailTableColumns),
				(List<SMFPDColorwayCreateMOABean>) (finalSortedCollectionMap.get(toEmailUser))));
		vecToList.add(wtToEmailUser);
		LOGGER.debug(CLASS_NAME + "--" + methodName + "--vecToList=" + vecToList);
		// send email method
		smEmailNotificationUtil.sendEmail(from, vecToList, sb.toString(), mailSubject, strHeader, systemInfo);
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
		return wtToEmailUser;
	}

}
