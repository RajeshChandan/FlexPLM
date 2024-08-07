package com.sportmaster.wc.emailutility.sepd.processor;

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
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.UserGroupHelper;
import com.sportmaster.wc.emailutility.sepd.bean.SMSEPDPSCSCancelledBean;
import com.sportmaster.wc.emailutility.sepd.constants.SMSEPDPSCSCancelledConstants;
import com.sportmaster.wc.emailutility.sepd.util.SMSEPDPSCSCancelledUtil;
import com.sportmaster.wc.emailutility.util.SMEmailNotificationUtil;
import com.sportmaster.wc.emailutility.util.SMMultiObjectAttributeUtil;

import wt.org.WTUser;
import wt.util.WTException;

public class SMSEPDPSCSCancelledProcessor {

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSEPDPSCSCancelledProcessor.class);

	/**
	 * Declaration for CLASS_NAME.
	 */
	private static final String CLASS_NAME = "SMSEPDPSCSCancelledProcessor";
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
	protected SMSEPDPSCSCancelledProcessor() {
		// constructor
	}

	/**
	 * processSEPDPSCSCancelledScheduleQueue.
	 */
	public static void processSEPDPSCSCancelledScheduleQueue() {
		String methodName = "processSEPDPSCSCancelledScheduleQueue()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		try {
			ClientContext ccContext = null;
			wt.org.WTUser from = null;
			try {
				ccContext = ClientContext.getContext();
				from = UserGroupHelper.getWTUser(ccContext.getUserName());
			} catch (WTException e1) {
				LOGGER.error(CLASS_NAME + "--" + methodName + EXCEPTION_OCCURED + e1.getMessage());
				e1.printStackTrace();
			}
			// Get PS CS Cancelled MOA Rows Data
			HashSet hsPSCSCancelledMOACollection = SMSEPDPSCSCancelledUtil.findMOACollectionBasedOnType();
			// Get the user attributes/ groups for whom email has to be sent for current
			HashMap usersListMap = SMEmailNotificationUtil.getUserAttributes(LCSProperties.get(
					"com.sportmaster.wc.emailutility.sendEmail.sepd.ps.cs.cancelled.emailTo",
					"user^LCSProductSeasonLink$smProductionManager|~*~|LCSProductSeasonLink$vrdDesigner|~*~|LCSProductSeasonLink$smProducctTechnologist"));
			LOGGER.info(CLASS_NAME + "--" + methodName + "--userListMap=" + usersListMap);
			ArrayList alUserAttributes = new ArrayList();
			if (usersListMap.containsKey("user")) {
				alUserAttributes = (ArrayList) usersListMap.get("user");
			}
			LOGGER.info(CLASS_NAME + "--" + methodName + "--Users attributes for sending email=" + alUserAttributes);
			// Initialization
			List<SMSEPDPSCSCancelledBean> notificationLists = new ArrayList();
			// Get notification email properties
			String systemInfor = LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.SystemInfo");
			String mailHeader = LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.MailHeader");
			SMEmailNotificationUtil smEmailNotificationUtil = new SMEmailNotificationUtil();
			String eMailContent = String.format(
					LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.sepd.ps.cs.cancelled.mailContent",
							"The following Products/Colorways were cancelled."));
			String eMailSubject = String.format(
					LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.sepd.ps.cs.cancelled.mailSubject",
							"FlexPLM Notification: Products/Colorways cancelled - DO NOT REPLY"));
			// Prepare Notification Bean
			prepareNotificationBeanList(hsPSCSCancelledMOACollection, alUserAttributes, notificationLists);

			LOGGER.info(CLASS_NAME + "--" + methodName + "--notificationList.size()=" + notificationLists.size());
			// Sort the list
			notificationLists = SMSEPDPSCSCancelledUtil.sortObjectCollections(notificationLists);

			// group the list with key = user, value = bean object, so email can be sent
			// consolidated objects for a single user
			HashMap finalSortedColMap = SMSEPDPSCSCancelledUtil.groupObjectCollections(notificationLists);
			LOGGER.info(CLASS_NAME + "--" + methodName + "--Final Sorted Collection Map=" + finalSortedColMap);
			// Initialization
			WTUser wtToEmailUser = null;
			//RCOM104 
			String emailTableColumns=LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.sepd.ps.cs.cancelled.emailColumns",
					"Season|~*~|Brand|~*~|Product|~*~|Colorway");
			// Iterate the map, and for each user, construct the email and send mail
			Iterator itFinalSortedColMap = finalSortedColMap.keySet().iterator();
			while (itFinalSortedColMap.hasNext()) {
				wtToEmailUser = sendEmailForMOARows(from, finalSortedColMap, systemInfor, mailHeader,
						smEmailNotificationUtil, eMailContent, eMailSubject, wtToEmailUser, itFinalSortedColMap, emailTableColumns);
			}
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Delete MOA Rows-Start");
			// Remove all MOA Rows
			SMMultiObjectAttributeUtil.deleteMOACollection(hsPSCSCancelledMOACollection);
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Delete MOA Rows-End");

		} catch (WTException e) {
			LOGGER.error(CLASS_NAME + "--" + methodName + EXCEPTION_OCCURED + e.getLocalizedMessage());
			e.printStackTrace();
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * @param hsPSCSCancelledMOACollection
	 * @param vUserAttributes
	 * @param notificationList
	 * @throws WTException
	 */
	private static void prepareNotificationBeanList(HashSet hsPSCSCancelledMOACollection, ArrayList vUserAttributes,
			List<SMSEPDPSCSCancelledBean> notificationList) throws WTException {
		String methodName = "prepareNotificationBeanList()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Initialization
		LCSMOAObject object = null;
		String strSeasonId = "";
		String strSeasonName = "";
		LCSSeason seasonObject = null;
		String strProductId = "";
		String strProductName = "";
		LCSProduct productObject = null;
		String strBrand = "";
		String strColorwayId = "";
		String strColorwayName = "";
		LCSSKU skuObject = null;
		String strLevel = "";
		LCSSeasonProductLink seasonProductLink = null;
		//RCOM104
		String brandAttributeKey= LCSProperties.get("com.sportmaster.wc.emailutility.processor.product.lcsproduct.brand");
		// Get a Iterator
		Iterator itMOARowIterator = hsPSCSCancelledMOACollection.iterator();
		while (itMOARowIterator.hasNext()) {
			// Get LCSMOAObect
			object = (LCSMOAObject) itMOARowIterator.next();
			// Get Season
			strSeasonId = String.valueOf(object.getValue(SMSEPDPSCSCancelledConstants.MOA_SEPD_SEASON_ID));
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Season Id =" + strSeasonId);
			seasonObject = (LCSSeason) LCSQuery.findObjectById("VR:com.lcs.wc.season.LCSSeason:" + strSeasonId);
			strSeasonName = String.valueOf(seasonObject.getValue(SMSEPDPSCSCancelledConstants.SEASON_NAME));
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Season Name =" + strSeasonName);
			// Get Product
			strProductId = String.valueOf(object.getValue(SMSEPDPSCSCancelledConstants.MOA_SEPD_PRODUCT_ID));
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Product Id =" + strProductId);
			productObject = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:" + strProductId);
			strProductName = String.valueOf(productObject.getName());
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Product Name =" + strProductName);
			// Get Brand
			// RCOM104
			strBrand = SMEmailNotificationUtil.getObjectValue((LCSProduct) productObject, brandAttributeKey,
					((LCSProduct) productObject).getFlexType());
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Brand Name =" + strBrand);
			// Get Colorway Id
			strColorwayId = String.valueOf(object.getValue(SMSEPDPSCSCancelledConstants.MOA_SEPD_CW_ID));
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--Colorway Id =" + strColorwayId);
			// Check for Colorway Column
			if (FormatHelper.hasContent(strColorwayId)) {
				skuObject = (LCSSKU) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:" + strColorwayId);
				strColorwayName = String.valueOf(skuObject.getValue(SMSEPDPSCSCancelledConstants.SKU_NAME));
				LOGGER.debug(CLASS_NAME + "--" + methodName + "--Colorway Name =" + strColorwayName);
			} else {
				strColorwayId = "";
			}
			// Check for level
			strLevel = String.valueOf(object.getValue(SMSEPDPSCSCancelledConstants.MOA_SEPD_LEVEL));
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--strLevel =" + strLevel);
			// Check of Product Version
			if (!"A".equalsIgnoreCase(productObject.getVersionDisplayIdentifier().toString())) {
				// Getting season product link
				seasonProductLink = SeasonProductLocator.getSeasonProductLink(productObject);
				// User attributes iterator
				Iterator itvUserAttributes = vUserAttributes.iterator();
				// Method to prepare bean obj
				prepareBeanObjects(notificationList, strSeasonName, strBrand, strProductId, strProductName,
						strColorwayId, strColorwayName, strLevel, itvUserAttributes, seasonProductLink);
			}
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * @param notificationList
	 * @param strSeasonName
	 * @param strBrand
	 * @param strProductName
	 * @param strColorwayName
	 * @param itvUserAttribute
	 * @param spLink
	 */
	@SuppressWarnings("unchecked")
	private static void prepareBeanObjects(List<SMSEPDPSCSCancelledBean> notificationList, String strSeasonName,
			String strBrand, String strProductId, String strProductName, String strColorwayId, String strColorwayName,
			String strLevel, Iterator itvUserAttribute, LCSSeasonProductLink spLink) {
		String methodName = "prepareBeanObjects()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		// Get unique user list associated to Season Product Link
		HashMap hmUserList = getUniqueUserListMap(itvUserAttribute, spLink);
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
				SMSEPDPSCSCancelledBean beanObject = new SMSEPDPSCSCancelledBean(strSeasonName, strBrand, strProductId,
						strProductName, strColorwayId, strColorwayName, strLevel, (WTUser) me.getValue(),
						(String) me.getKey());
				// Set product name, season name, brand, colorway name, email to
				// bean obj and add that to a list
				notificationList.add(beanObject);
			}
		}
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
	}

	/**
	 * @param itvUserAttribute
	 * @param spLink
	 * @return
	 */
	private static HashMap getUniqueUserListMap(Iterator itvUserAttribute, LCSSeasonProductLink spLink) {
		String methodName = "getAssociatedUniqueUserListMap()";
		HashMap hmUserList = new HashMap();
		String strUserAttribute;
		StringTokenizer stUserAttribute;
		while (itvUserAttribute.hasNext()) {
			strUserAttribute = (String) itvUserAttribute.next();
			// defined in property = objectType$attributekey, tokens and get user
			// attribute value
			stUserAttribute = new StringTokenizer(strUserAttribute, "$");
			LOGGER.debug(CLASS_NAME + "--" + methodName + "--strUserAttribute= " + strUserAttribute);
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
	 * @param itFinalSortedColMap
	 * @param emailTableColumns 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static WTUser sendEmailForMOARows(wt.org.WTUser from, HashMap finalSortedCollectionMap, String systemInfo,
			String strHeader, SMEmailNotificationUtil smEmailNotificationUtil, String mailContent, String mailSubject,
			WTUser wtToEmailUser, Iterator itFinalSortedColMap, String emailTableColumns) {
		String methodName = "sendEmailForMOARows()";
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_START);
		StringBuilder stringBuilder;
		String toEmailUserName;
		ArrayList vecToListed = new ArrayList();
		toEmailUserName = (String) itFinalSortedColMap.next();
		try {
			wtToEmailUser = wt.org.OrganizationServicesHelper.manager.getUser(toEmailUserName);
			LOGGER.info(CLASS_NAME + "--" + methodName + "--wtToEmailUser=" + wtToEmailUser);
		} catch (WTException e) {
			LOGGER.error(CLASS_NAME + "--" + methodName + EXCEPTION_OCCURED + e.getMessage());
			e.printStackTrace();
		}
		stringBuilder = new StringBuilder();
		stringBuilder.append(mailContent).append("<br>").append("<br>");
		//RCOM104 
		stringBuilder.append(SMSEPDPSCSCancelledUtil.prepareHTMLTableContent(
				MOAHelper.getMOACollection(emailTableColumns),
				(List<SMSEPDPSCSCancelledBean>) (finalSortedCollectionMap.get(toEmailUserName))));
		vecToListed.add(wtToEmailUser);
		LOGGER.info(CLASS_NAME + "--" + methodName + "--vecToList=" + vecToListed);

		// send email method
		smEmailNotificationUtil.sendEmail(from, vecToListed, stringBuilder.toString(), mailSubject, strHeader,
				systemInfo);
		LOGGER.info(CLASS_NAME + "--" + methodName + METHOD_END);
		return wtToEmailUser;
	}

}
