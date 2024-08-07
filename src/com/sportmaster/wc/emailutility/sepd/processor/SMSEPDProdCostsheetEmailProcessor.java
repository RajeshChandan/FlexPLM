/**
 * 
 */
package com.sportmaster.wc.emailutility.sepd.processor;

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
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSCostSheet;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.UserGroupHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.queue.sepd.util.SMSEPDProdCostsheetEmailUtil;
import com.sportmaster.wc.emailutility.util.SMEmailNotificationUtil;
import com.sportmaster.wc.interfaces.webservices.bean.SQLException_Exception;

import wt.fc.WTObject;
import wt.org.WTUser;
import wt.util.WTException;

/**
 * @author 'true' Priya.
 *
 */
public class SMSEPDProdCostsheetEmailProcessor {

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSEPDProdCostsheetEmailProcessor.class);
	
	/**
	 * SEPD CS Type
	 */
	public static final String SCVENDOR = LCSProperties
			.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.SCVENDOR");
	
	public static final String SEASONNAME = "seasonName";

	public static final String PRODUCTNAME = "productName";
	/**
	 * Constructor.
	 */
	protected SMSEPDProdCostsheetEmailProcessor() {
		// constructor
	}

	/**
	 * Process process Queue Entry.
	 * 
	 * @throws SQLException_Exception
	 */
	public static void processSEPDProdCostsheetScheduleQueue() {
		LOGGER.debug("### SMSEPDProdCostsheetEmailProcessor.processSEPDProdCostsheetScheduleQueue - START ###");
		try {
			// Call this method to get the previous timestamp from which the tasks have to
			// be sent in notification
			Timestamp prevDate = SMEmailNotificationUtil.getPrevDate(SMEmailUtilConstants.TIME_ZONE,
					SMEmailUtilConstants.SEPD_PRODUCT_COSTSHEET_WF_EMAIL_SCHEDULE_PICKTASKSFROM_INTERVAL);
			LOGGER.debug("prevDate from which task notification will be sent=" + prevDate);
			// Get the tasks for which notification has to be sent
			HashMap hmTasks = SMEmailNotificationUtil
					.getTasksFromProperties(LCSProperties.get("SEPD.ProdCostsheetWF.NotificationTasks"));
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
				userGroupsMap = SMEmailNotificationUtil.getUserAttributes(
						LCSProperties.get("SEPD.ProdCostsheetWF.NotificationTasks.emailTo." + keyTaskNo));
				LOGGER.debug("userGroupsMap=" + userGroupsMap);
				strTaskName = (String) hmTasks.get(keyTaskNo);
				LOGGER.debug("strTaskName=" + strTaskName);

				// Find the list of tasks that are created from previous date and current date
				workTasklist = (ArrayList) SMEmailNotificationUtil.findOpenTasks(strTaskName, prevDate);
				LOGGER.debug("workTasklist=" + workTasklist);
				// Some tasks for a single object will be assigned to multiple roles, iterating
				// tasks and removing the duplicate objects in these cases
				HashSet uniqueWorkList = SMSEPDProdCostsheetEmailUtil.findUniqueTasks(workTasklist);
				LOGGER.debug("uniqueWorkList=" + uniqueWorkList.size());
				LOGGER.debug("uniqueWorkList=" + uniqueWorkList);

				// Call this method to send email for each task
				sendSEPDProdCostsheetWFTaskEmail(uniqueWorkList, userGroupsMap, keyTaskNo, strTaskName);
			}

		} catch (WTException e) {
			LOGGER.error("WTException in processSEPDProdCostsheetScheduleQueue method - " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		LOGGER.debug("### SMSEPDProdCostsheetEmailProcessor.processSEPDProdCostsheetScheduleQueue - END ###");
	}

	/**
	 * Method to sort, group the data and send email notifications to users
	 * 
	 * @param uniqueWorkList
	 * @param userList
	 * @param keyTaskNo
	 * @param strTaskName
	 * @throws WTException
	 */
	public static void sendSEPDProdCostsheetWFTaskEmail(HashSet uniqueWorkList, Map userList, String keyTaskNo,
			String strTaskName) throws WTException {
		LOGGER.debug("### SMSEPDProdCostsheetEmailProcessor.sendSEPDProdCostsheetWFTaskEmail - START ###");
		ClientContext lcsContext = null;
		wt.org.WTUser from = null;
		try {
			lcsContext = ClientContext.getContext();
			from = UserGroupHelper.getWTUser(lcsContext.getUserName());
		} catch (WTException e1) {
			// TODO Auto-generated catch block
			LOGGER.error("WTException in SMSEPDProdCostsheetEmailProcessor.sendSEPDProdCostsheetWFTaskEmail -"
					+ e1.getMessage());
			e1.printStackTrace();
		}

		List<SMSEPDProdCostsheetWFEmailBean> notificationList = new ArrayList();
		ArrayList vUserAttributes = new ArrayList();
		if (userList.containsKey("user")) {
			vUserAttributes = (ArrayList) userList.get("user");
		}
		if (userList.containsKey("group")) {
			// notificaiton is not sent to any Groups for SEPD product Costsheet WF Tasks
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
					LCSProperties.get(
							"com.sportmaster.wc.emailutility.sendEmail.sepdProductCostsheet.MailContent." + keyTaskNo),
					strTaskName);
			String mailSubject = String.format(
					LCSProperties.get(
							"com.sportmaster.wc.emailutility.sendEmail.sepdProductCostsheet.MailSubject." + keyTaskNo),
					strTaskName);
			// Iterate each workflow task
			while (itTasks.hasNext()) {
				prepareNotificationBeanList(notificationList, vUserAttributes, itTasks);
			}
			LOGGER.debug("notificationList.size()=" + notificationList.size());
			// Sort the list
			notificationList = SMSEPDProdCostsheetEmailUtil.sortObjectCollections(notificationList);
			// group the list with key = user, value = bean object, so email can be sent
			// consolidated objects for a single user
			finalSortedCollectionMap = SMSEPDProdCostsheetEmailUtil.groupObjectCollections(notificationList);

			LOGGER.debug("Final Sorted Collection Map=" + finalSortedCollectionMap);
			WTUser wtToEmailUser = null;
			// Iterate the map, and for each user, construct the email and send mail
			Iterator itfinalSortedCollectionMap = finalSortedCollectionMap.keySet().iterator();
			while (itfinalSortedCollectionMap.hasNext()) {
				wtToEmailUser = sendEmailForTasks(keyTaskNo, from, finalSortedCollectionMap, systemInfo, strHeader,
						smEmailNotificationUtil, mailContent, mailSubject, wtToEmailUser, itfinalSortedCollectionMap);
			}
		}
		LOGGER.debug("### SMSEPDProdCostsheetEmailProcessor.sendSEPDProdCostsheetWFTaskEmail - END ###");
	}

	/**
	 * Method to create bean object Method to create bean objects
	 * 
	 * @param keyTaskNo
	 * @param notificationList
	 * @param vUserAttributes
	 * @param itTasks
	 * @throws WTException
	 */
	private static void prepareNotificationBeanList(List<SMSEPDProdCostsheetWFEmailBean> notificationList,
			ArrayList vUserAttributes, Iterator itTasks) throws WTException {
		LOGGER.debug("### SMSEPDProdCostsheetEmailProcessor.prepareNotificationBeanList - START ###");
		WTObject wtObj;
		String seasonName = "";

		LCSProduct prodObj = null;
		LCSCostSheet csObj = null;
		LCSSourcingConfigMaster sourceMaster = null;
		LCSSourcingConfig sourceObj = null;
		LCSSeason seasonObj = null;
		LCSSourceToSeasonLink sourceToSeasonLink = null;
		LCSSeasonProductLink spLink = null;
		wtObj = (WTObject) itTasks.next();
		LCSProduct productRevA = null;
		LOGGER.debug("In prepareNotificationBeanList method - wtObj ==" + wtObj);
		LOGGER.debug("In prepareNotificationBeanList method - Costsheet Type =="
				+ ((LCSCostSheet) wtObj).getFlexType().getFullName(true));
		// WF Task is instance of Costsheet
		if (wtObj instanceof LCSCostSheet) {
			// Getting Sample object.
			csObj = (LCSCostSheet) wtObj;

			// Getting SourcingConfig Master.
			sourceMaster = (LCSSourcingConfigMaster) csObj.getSourcingConfigMaster();

			// Added this check to ignore the sourcing config that are created and deleted
			// from system (when task is still active)
			if (sourceMaster != null && sourceMaster instanceof wt.vc.Mastered) {

				// Getting Sourcing Config Object.
				sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);

				// Getting Product RevA
				productRevA = (LCSProduct) VersionHelper.getVersion(csObj.getProductMaster(), "A");
				// Getting season object
				seasonObj = (LCSSeason) VersionHelper.latestIterationOf(csObj.getSeasonMaster());

				// Getting Source to season link.
				sourceToSeasonLink = new LCSSourcingConfigQuery().getSourceToSeasonLink(sourceObj, seasonObj);

				// Getting Product Season link.
				spLink = LCSSeasonQuery.findSeasonProductLink(productRevA, seasonObj);
				LOGGER.debug("\n spLink == " + spLink);

				if (spLink != null && !(spLink.isSeasonRemoved())) {
					// Getting Product Object.
					prodObj = SeasonProductLocator.getProductSeasonRev(spLink);
					// Check if product type is either ACC\\SEPD or SEPD.
					if (prodObj != null) {
						prepareBeanList(notificationList, vUserAttributes, seasonName, prodObj, csObj, sourceObj,
								seasonObj, sourceToSeasonLink);
					}
				}
			}
		}
		LOGGER.debug("### SMSEPDProdCostsheetEmailProcessor.prepareNotificationBeanList - END ###");
	}

	private static void prepareBeanList(List<SMSEPDProdCostsheetWFEmailBean> notificationList,
			ArrayList vUserAttributes, String seasonName, LCSProduct prodObj, LCSCostSheet csObj,
			LCSSourcingConfig sourceObj, LCSSeason seasonObj, LCSSourceToSeasonLink sourceToSeasonLink) {
		String styleName;
		String sourceName = "";
		String costingStage;
		Long csNo;
		String csName;
		String prodType;
		Iterator itvUserAttributes;
		String csStatusCoster;
		prodType = prodObj.getFlexType().getFullName();

		if ((prodType.startsWith(
				LCSProperties.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.ProdSEPDType"))
				|| prodType.startsWith(LCSProperties
						.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.ProdAccSEPDType")))
				&& (csObj.getFlexType().getFullName()
						.startsWith(LCSProperties
								.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSSEPDAllType"))
						|| csObj.getFlexType().getFullName()
								.startsWith(LCSProperties.get(
										"com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSAppAccType"))
						|| csObj.getFlexType().getFullName().startsWith(LCSProperties.get(
								"com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSAppAccMCType")))) {
			itvUserAttributes = vUserAttributes.iterator();
			LOGGER.debug("vUserAttributes====" + vUserAttributes);
			try {
				if (seasonObj != null) {
					seasonName = (String) seasonObj.getValue(SEASONNAME);
					LOGGER.debug("\n seasonName===" + seasonName);
				}
				LOGGER.debug("\n seasonObj == " + seasonObj);

				// Getting styleName.
				styleName = (String) prodObj.getValue(PRODUCTNAME);
				LOGGER.debug(" styleName==" + styleName);

				LCSSupplier vendorObj = (LCSSupplier) sourceObj.getValue(SCVENDOR);
				if (vendorObj != null) {					
					sourceName = vendorObj.getName();					
				}
				//sourceName = (String) sourceObj.getValue("name");
				LOGGER.debug("sourceName=" + sourceName);

				costingStage = (String) csObj.getFlexType()
						.getAttribute(LCSProperties.get(
								"com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSSEPDCOSTINGSTAGE"))
						.getDisplayValue(csObj);
				LOGGER.debug("costingStage==" + costingStage);

				csNo = (Long) csObj.getValue(LCSProperties
						.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSCOSTSHEETNUM"));
				LOGGER.debug("csNo==" + csNo);

				csName = (String) csObj.getValue("name");
				LOGGER.debug("csName==" + csName);

				csStatusCoster = (String) csObj.getValue(LCSProperties
						.get("com.sportmaster.wc.costsheet.SMProductCostsheetWorkflowPlugin.CSCOSTSHEETSTATUSCOSTER"));
				// to trigger only when task is created first time, when task is triggered again
				// when coster is set from blank to rework, notification should not be
				// triggered. In this case immediate notification is sent via workflow.
				if (!FormatHelper.hasContent(csStatusCoster)) {
					LOGGER.debug("csStatusCoster====" + csStatusCoster);
					// Method to prepare bean obj
					prepareBeanObjects(notificationList, seasonName, styleName, sourceName, costingStage, csNo, csName,
							csObj, sourceToSeasonLink, itvUserAttributes, prodType);
					LOGGER.debug("In getAttributesValue method - Group notificationList ==" + notificationList);
				}
			} catch (WTException e) {
				LOGGER.error("Error in sendSEPDProdCostsheetWFTaskEmail method=" + e.getMessage());
				e.printStackTrace();
			}
		}
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
		LOGGER.debug("### SMSEPDProdCostsheetEmailProcessor.sendEmailForTasks - START ###");
		StringBuilder sb;
		String toEmailUser;
		ArrayList vecToList = new ArrayList();
		toEmailUser = (String) itfinalSortedCollectionMap.next();
		try {
			wtToEmailUser = wt.org.OrganizationServicesHelper.manager.getUser(toEmailUser);

		} catch (WTException e) {
			LOGGER.error("WTException in SMSEPDProdCostsheetEmailProcessor.sendEmailForTasks -" + e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb = new StringBuilder();

		sb.append(mailContent).append("<br>").append("<br>").append("Details:").append("<br>");
		sb.append(SMSEPDProdCostsheetEmailUtil.prepareHTMLTableContent(
				MOAHelper.getMOACollection(
						LCSProperties.get("SEPD.ProdCostsheetWF.NotificationTasks.emailColumns." + keyTaskNo)),
				(List<SMSEPDProdCostsheetWFEmailBean>) (finalSortedCollectionMap.get(toEmailUser))));
		vecToList.add(wtToEmailUser);
		LOGGER.debug("vecToList=" + vecToList);
		LOGGER.debug(" \n\nfrom==========" + from + "\n\n strHeader===" + strHeader + "\n\n vecToList===" + vecToList
				+ "\n\n mailSubject==============" + mailSubject + "\n\n body=====" + sb.toString() + "\n\n");
		// send email method
		smEmailNotificationUtil.sendEmail(from, vecToList, sb.toString(), mailSubject, strHeader, systemInfo);
		LOGGER.debug("### SMSEPDProdCostsheetEmailProcessor.sendEmailForTasks - END ###");
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
	private static void prepareBeanObjects(List<SMSEPDProdCostsheetWFEmailBean> notificationList, String seasonName,
			String styleName, String sourceName, String costingStage, Long csNo, String csName, LCSCostSheet csObj,
			LCSSourceToSeasonLink sourceToSeasonLink, Iterator itvUserAttributes, String prodType) {

		String strUserAttribute;
		StringTokenizer stUserAtts;
		List<Object> userListArr;
		String strUserToExclude = "";
		boolean bNotSameUser;
		String objtype = "";
		String attribute = "";
		// Iterate user attributes, in case notification is to be sent for multiple
		// users , iterate and for each user create the SMSEPDProdCostsheetWFEmailBean
		// object, so email will be sent for all users.
		while (itvUserAttributes.hasNext()) {
			strUserAttribute = (String) itvUserAttributes.next();
			// defined in property = objectType$attributekey, tokenize and get user
			// attribute value
			stUserAtts = new StringTokenizer(strUserAttribute, "$");
			objtype = (String) stUserAtts.nextElement();
			attribute = (String) stUserAtts.nextElement();
			LOGGER.debug("In prepareBeanObjects method - objtype ==" + objtype + "\nattribute ==" + attribute);

			if ((SMEmailUtilConstants.SOURCE_TO_SEASON_KEY).equals(objtype)) {
				if ((prodType.startsWith(SMEmailUtilConstants.ACC_SEPD_PRODUCT_TYPE)
						&& attribute.equals(SMEmailUtilConstants.OSO_APP_COSTER)
						|| (prodType.startsWith(SMEmailUtilConstants.SEPD_PRODUCT_TYPE)
								&& attribute.equals(SMEmailUtilConstants.OSO_COSTER)))) {
					// Method to get the user list attribute value and flexobject
					userListArr = SMEmailNotificationUtil.getUser((LCSSourceToSeasonLink) sourceToSeasonLink,
							(String) attribute);
					// Only when user list is not empty, create the notification bean object
					if (!userListArr.isEmpty()) {
						// Set current task's seasonName, styleName, sourceName, businessSupplier,
						// factory, sampleName, supplierSampleStatus, sampleStatus, requestCreator,
						// email to bean obj and add that to a list

						notificationList.add(
								SMSEPDProdCostsheetEmailUtil.getBeanObj(seasonName, styleName, sourceName, costingStage,
										csNo, csName, (WTUser) userListArr.get(0), (String) userListArr.get(1), csObj));
						LOGGER.debug(" In prepareAddNotificationBean method - notificationList == " + notificationList);
					}
				}
			}
		}
	}

}
