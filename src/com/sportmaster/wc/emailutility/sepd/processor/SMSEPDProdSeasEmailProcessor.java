/**
 * 
 */
package com.sportmaster.wc.emailutility.sepd.processor;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.UserGroupHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.queue.sepd.util.SMSEPDProdSeasEmailUtil;
import com.sportmaster.wc.emailutility.util.SMEmailNotificationUtil;
import com.sportmaster.wc.interfaces.webservices.bean.SQLException_Exception;

import wt.fc.WTObject;
import wt.org.WTUser;
import wt.util.WTException;
import wt.util.WTProperties;

/**
 * @author 'true' Priya.
 *
 */
public class SMSEPDProdSeasEmailProcessor {

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSEPDProdSeasEmailProcessor.class);

	/**
	 * To get the flexplm url
	 */
	private static String FLEXPLM_URL = "";

	/**
	 * Constructor.
	 */
	protected SMSEPDProdSeasEmailProcessor() {
		// constructor
	}

	/**
	 * Method to construct the flexplm url to provide link for Product Name in email
	 * table
	 * 
	 * @return
	 */
	public static String constructFlexPLMURL() {
		LOGGER.debug("### SMSEPDProdSeasEmailProcessor.constructFlexPLMURL - START ###");
		try {
			FLEXPLM_URL = WTProperties.getServerProperties().getProperty("wt.server.codebase")
					+ "/rfa/jsp/main/Main.jsp?newWindowActivity=VIEW_PRODUCT&newWindowOid=VR%3Acom.lcs.wc.product.LCSProduct%3A";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("IOException in constructFlexPLMURL -" + e.getLocalizedMessage());
			//e.printStackTrace();
		}
		LOGGER.debug("FLEXPLM_URL==" + FLEXPLM_URL);
		LOGGER.debug("### SMSEPDProdSeasEmailProcessor.constructFlexPLMURL - START ###");
		return FLEXPLM_URL;
	}

	/**
	 * Process process Queue Entry.
	 * 
	 * @throws SQLException_Exception
	 */
	public static void processSEPDProdSeasScheduleQueue() {
		LOGGER.debug("### SMSEPDProdSeasEmailProcessor.processSEPDProdSeasScheduleQueue - START ###");
		try {
			// Call this method to get the previous timestamp from which the tasks have to
			// be sent in notification
			Timestamp prevDate = SMEmailNotificationUtil.getPrevDate(SMEmailUtilConstants.TIME_ZONE,
					SMEmailUtilConstants.SEPD_PS_WF_EMAIL_SCHEDULE_PICKTASKSFROM_INTERVAL);
			LOGGER.debug("prevDate from which task notification will be sent=" + prevDate);
			// Get the tasks for which notification has to be sent
			HashMap hmTasks = SMEmailNotificationUtil
					.getTasksFromProperties(LCSProperties.get("SEPD.PSWF.NotificationTasks"));
			LOGGER.debug("hmTasks=" + hmTasks);
			Iterator ithmTasks = hmTasks.keySet().iterator();
			String keyTaskNo = "";
			String strTaskName = "";
			HashMap userGroupsMap;
			ArrayList workTasklist;
			// Iterate the task map
			while (ithmTasks.hasNext()) {
				// Get the current task number
				keyTaskNo = (String) ithmTasks.next();
				LOGGER.debug("keyTaskNo=" + keyTaskNo);
				// Get the user attributes/ groups for whom email has to be sent for current
				// task number
				userGroupsMap = SMEmailNotificationUtil
						.getUserAttributes(LCSProperties.get("SEPD.PSWF.NotificationTasks.emailTo." + keyTaskNo));
				LOGGER.debug("userGroupsMap=" + userGroupsMap);
				strTaskName = (String) hmTasks.get(keyTaskNo);
				LOGGER.debug("strTaskName=" + strTaskName);

				// Find the list of tasks that are created from previous date and current date
				workTasklist = (ArrayList) SMEmailNotificationUtil.findOpenTasks(strTaskName, prevDate);
				LOGGER.debug("workTasklist=" + workTasklist);
				// Some tasks for a single object will be assigned to multiple roles, iterating
				// tasks and removing the duplicate objects in these cases
				HashSet uniqueWorkList = SMSEPDProdSeasEmailUtil.findUniqueTasks(workTasklist);
				LOGGER.debug("uniqueWorkList=" + uniqueWorkList.size());
				LOGGER.debug("uniqueWorkList=" + uniqueWorkList);

				// Call this method to send email for each task
				sendSEPDProdSeasWFTaskEmail(uniqueWorkList, userGroupsMap, keyTaskNo, strTaskName);
			}

		} catch (WTException e) {
			LOGGER.error("WTException in processSEPDProdSeasScheduleQueue method - " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		LOGGER.debug("### SMSEPDProdSeasEmailProcessor.processSEPDProdSeasScheduleQueue - END ###");
	}

	/**
	 * Method to sort, group the data and send email notifications to users
	 * 
	 * @param uniqueWorkList
	 * @param userList
	 * @param keyTaskNo
	 * @param strTaskName
	 */
	public static void sendSEPDProdSeasWFTaskEmail(HashSet uniqueWorkList, Map userList, String keyTaskNo,
			String strTaskName) {
		LOGGER.debug("### SMSEPDProdSeasEmailProcessor.sendSEPDProdSeasWFTaskEmail - START ###");
		ClientContext lcsContext = null;
		wt.org.WTUser from = null;
		try {
			lcsContext = ClientContext.getContext();
			from = UserGroupHelper.getWTUser(lcsContext.getUserName());
		} catch (WTException e1) {
			// TODO Auto-generated catch block
			LOGGER.error("WTException in SMSEPDProdSeasEmailProcessor.sendSEPDProdSeasWFTaskEmail -" + e1.getMessage());
			e1.printStackTrace();
		}

		List<SMSEPDProdSeasWFEmailBean> notificationList = new ArrayList();
		ArrayList vUserAttributes = new ArrayList();
		if (userList.containsKey("user")) {
			vUserAttributes = (ArrayList) userList.get("user");
		}
		if (userList.containsKey("group")) {
			// notificaiton is not sent to any Groups for SEPD PS WF Tasks
		}
		LOGGER.debug("Users attributes for sending email=" + vUserAttributes);
		if (!uniqueWorkList.isEmpty()) {			
			// Iterate the task list
			Iterator itTasks = uniqueWorkList.iterator();
			HashMap finalSortedCollectionMap;
			// Get notification email properties
			String systemInfo = LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.SystemInfo");
			String strHeader = LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.MailHeader");
			SMEmailNotificationUtil smEmailNotificationUtil = new SMEmailNotificationUtil();
			String mailContent = String.format(
					LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.sepdProductSeason.MailContent"),
					strTaskName);
			String mailSubject = String.format(
					LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.sepdProductSeason.MailSubject"),
					strTaskName);
			// Iterate each workflow task
			while (itTasks.hasNext()) {
				prepareNotificationBeanList(keyTaskNo, notificationList, vUserAttributes, itTasks);
			}
			LOGGER.debug("notificationList.size()=" + notificationList.size());
			// Sort the list
			notificationList = SMSEPDProdSeasEmailUtil.sortObjectCollections(notificationList);
			// group the list with key = user, value = bean object, so email can be sent
			// consolidated objects for a single user
			finalSortedCollectionMap = SMSEPDProdSeasEmailUtil.groupObjectCollections(notificationList);

			LOGGER.debug("Final Sorted Collection Map=" + finalSortedCollectionMap);
			WTUser wtToEmailUser = null;
			// Iterate the map, and for each user, construct the email and send mail
			Iterator itfinalSortedCollectionMap = finalSortedCollectionMap.keySet().iterator();
			while (itfinalSortedCollectionMap.hasNext()) {
				wtToEmailUser = sendEmailForTasks(keyTaskNo, from, finalSortedCollectionMap, systemInfo, strHeader,
						smEmailNotificationUtil, mailContent, mailSubject, wtToEmailUser, itfinalSortedCollectionMap);
			}
		}
		LOGGER.debug("### SMSEPDProdSeasEmailProcessor.sendSEPDProdSeasWFTaskEmail - END ###");
	}
	
	/**
	 * Method to create bean object
	 * Method to create bean objects
	 * 
	 * @param keyTaskNo
	 * @param notificationList
	 * @param vUserAttributes
	 * @param itTasks
	 */
	private static void prepareNotificationBeanList(String keyTaskNo, List<SMSEPDProdSeasWFEmailBean> notificationList,
			ArrayList vUserAttributes, Iterator itTasks) {
		LOGGER.debug("### SMSEPDProdSeasEmailProcessor.prepareNotificationBeanList - START ###");
		WTObject wtObj;
		String strProductName;
		String strSeasonName;
		String strSalesNewness = "";
		String strBrand;
		Iterator itvUserAttributes;
		LCSSeasonProductLink spLink = null;
		LCSProduct prodARev = null;
		List<Object> userListArr;
		wtObj = (WTObject) itTasks.next();
		userListArr = new ArrayList<Object>();
		LOGGER.debug("Product Type Name=" + ((LCSProduct) wtObj).getFlexType().getFullName(true));
		// WF Task is instance of Product and type is SEPD
		if (wtObj instanceof LCSProduct
				&& ((LCSProduct) wtObj).getFlexType().getFullName(true).startsWith("Product\\SEPD")) {
			itvUserAttributes = vUserAttributes.iterator();
			try {
				// Get Season Product Link for the product object
				spLink = SeasonProductLocator.getSeasonProductLink((LCSProduct) wtObj);
				// Get Product A REV.
				prodARev = SeasonProductLocator.getProductARev((LCSProduct) wtObj);
				prodARev = (LCSProduct) VersionHelper.latestIterationOf(prodARev);
			} catch (WTException e) {
				LOGGER.error("Error in sendSEPDProdSeasWFTaskEmail method=" + e.getMessage());
				e.printStackTrace();
			}
			if (spLink != null) {
				// Get season name
				strSeasonName = SMSEPDProdSeasEmailUtil.getSeasonName((LCSProduct) wtObj);
				// Get Product name
				strProductName = SMSEPDProdSeasEmailUtil.getProductName((LCSProduct) prodARev);
				// Get brand attribute value to send in email
				strBrand = SMEmailNotificationUtil.getObjectValue((LCSProduct) prodARev,
						LCSProperties.get("com.sportmaster.wc.emailutility.processor.product.lcsproduct.brand"),
						((LCSProduct) wtObj).getFlexType());
				// For task 1, sales newness attribute value is not required in email
				// Get sales newness attribute value to send in email for other tasks
				if (!("1").equals(keyTaskNo)) {
					strSalesNewness = SMEmailNotificationUtil.getObjectValue((LCSProductSeasonLink) spLink,
							LCSProperties.get(
									"com.sportmaster.wc.emailutility.processor.product.lcsproductSeasonLink.salesNewness"),
							((LCSProductSeasonLink) spLink).getFlexType());
				}
				// Method to prepare bean obj
				prepareBeanObjects(notificationList, strProductName, strSeasonName, strSalesNewness, strBrand,
						itvUserAttributes, spLink, userListArr, keyTaskNo);
			}
		}
		LOGGER.debug("### SMSEPDProdSeasEmailProcessor.prepareNotificationBeanList - END ###");
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
	 * @return
	 */
	private static WTUser sendEmailForTasks(String keyTaskNo, wt.org.WTUser from, HashMap finalSortedCollectionMap,
			String systemInfo, String strHeader, SMEmailNotificationUtil smEmailNotificationUtil, String mailContent,
			String mailSubject, WTUser wtToEmailUser, Iterator itfinalSortedCollectionMap) {
		LOGGER.debug("### SMSEPDProdSeasEmailProcessor.sendEmailForTasks - START ###");
		StringBuilder sb;
		String toEmailUser;
		ArrayList vecToList = new ArrayList();
		toEmailUser = (String) itfinalSortedCollectionMap.next();
		try {
			wtToEmailUser = wt.org.OrganizationServicesHelper.manager.getUser(toEmailUser);

		} catch (WTException e) {
			LOGGER.error("WTException in SMSEPDProdSeasEmailProcessor.sendEmailForTasks -" + e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb = new StringBuilder();

		sb.append(mailContent).append("<br>").append("<br>");
		sb.append(SMSEPDProdSeasEmailUtil.prepareHTMLTableContent(keyTaskNo,
				MOAHelper.getMOACollection(LCSProperties.get("SEPD.PSWF.NotificationTasks.emailColumns." + keyTaskNo)),
				(List<SMSEPDProdSeasWFEmailBean>) (finalSortedCollectionMap.get(toEmailUser))));

		vecToList.add(wtToEmailUser);
		LOGGER.debug("vecToList=" + vecToList);

		// send email method
		smEmailNotificationUtil.sendEmail(from, vecToList, sb.toString(), mailSubject, strHeader, systemInfo);
		LOGGER.debug("### SMSEPDProdSeasEmailProcessor.sendEmailForTasks - END ###");
		return wtToEmailUser;
	}

	

	/**
	 * @param notificationList
	 * @param strProductName
	 * @param strSeasonName
	 * @param strSalesNewness
	 * @param strBrand
	 * @param itvUserAttributes
	 * @param spLink
	 * @param userListArr
	 * @param keyTaskNo 
	 */
	private static void prepareBeanObjects(List<SMSEPDProdSeasWFEmailBean> notificationList, String strProductName,
			String strSeasonName, String strSalesNewness, String strBrand, Iterator itvUserAttributes,
			LCSSeasonProductLink spLink, List<Object> userListArr, String keyTaskNo) {
		String strUserAttribute;
		StringTokenizer stUserAtts;
		String strUserToExclude = "";
		boolean bNotSameUser;
		// Iterate user attributes, in case notification is to be sent for multiple
		// users , iterate and for each user create the SMSEPDProdSeasWFEmailBean
		// object, so email will be sent for all users.
		while (itvUserAttributes.hasNext()) {
			strUserAttribute = (String) itvUserAttributes.next();
			// defined in property = objectType$attributekey, tokenize and get user
			// attribute value
			stUserAtts = new StringTokenizer(strUserAttribute, "$");
			if (("LCSProductSeasonLink").equals(stUserAtts.nextElement())) {
				// Method to get user list attribute value
				strUserToExclude = SMEmailNotificationUtil.getUserToExcludeNotification((LCSSeasonProductLink) spLink,LCSProperties.get("SEPD.PSWF.NotificationTasks.userToExcludeFromNotification")); 
				// Method to get the user list attribute value and flexobject
				userListArr = SMEmailNotificationUtil.getUser((LCSSeasonProductLink) spLink,
						(String) stUserAtts.nextElement());
			}
			// Only when user list is not empty, create the notification bean object
			if (!userListArr.isEmpty()) {
				LOGGER.debug("userListArr=" + userListArr);
				// If Technologist/Engineer is same as production manager user, then do not include that task in the email
				bNotSameUser = SMSEPDProdSeasEmailUtil.isSameAsUser(userListArr,keyTaskNo, strUserToExclude );
				if (bNotSameUser) {
				// Set current task's product name, season name, brand, sales newness, email to
				// bean obj and add that to a list
				notificationList.add(SMSEPDProdSeasEmailUtil.getBeanObj(strProductName, strSeasonName, strBrand,
						strSalesNewness, (WTUser) userListArr.get(0), (String) userListArr.get(1)));
				}
			}
		}
	}

}
