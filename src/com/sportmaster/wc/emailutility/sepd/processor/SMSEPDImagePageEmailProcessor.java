/**
 * @author 'true' Narasimha Bandla.
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
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.UserGroupHelper;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.queue.sepd.util.SMSEPDImagePageEmailUtil;
import com.sportmaster.wc.emailutility.util.SMEmailNotificationUtil;

import wt.fc.WTObject;
import wt.org.WTUser;
import wt.util.WTException;
import wt.util.WTProperties;

/**
 * @author 'true' Narasimha Bandla.
 *
 */
public class SMSEPDImagePageEmailProcessor {

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSEPDImagePageEmailProcessor.class);

	/**
	 * To get the flexplm url
	 */
	private static String FLEXPLM_URL = "";
	
	
	/**
	 * ownerReference key
	 */
	public static final String OWNERREFERENCE = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.ownerReference");
			
	/**
	 * smTechnologistApprovalSEPD key
	 */
	public static final String TECHNOLOGIST_APPROVAL_SEPD = LCSProperties
			.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.smTechnologistApprovalSEPD");
			
	/**
	 * Email Table Columns.
	 */
	public static final String EMAIL_TABLE_COLUMNS = LCSProperties.get("SEPD.IPWF.NotificationTasks.emailColumns");
	
	/**
	* Technologist Approval - Checked no Problem key
	*/
	public static final String TECHAPPROVALCHECKNOPROBLEM = LCSProperties.get("com.sportmaster.wc.document.SMSEPDImagePageWFPlugin.smTechnologistApprovalSEPD.smApprovedByTechnologist");
	

	/**
	 * Season key
	 */
	static final String SM_IMAGEPAGE_SEASON = LCSProperties
			.get("com.sportmaster.wc.document.SMImagePageWFPlugin.smSeason", "smSeason");
	/**
	 * Constructor.
	 */
	protected SMSEPDImagePageEmailProcessor() {
		// constructor
	}

	/**
	 * Method to construct the flexplm url to provide link for Document Imaage Page Name in email
	 * table
	 * 
	 * @return
	 */
	public static String constructFlexPLMURL() {
		LOGGER.debug("### SMSEPDImagePageEmailProcessor.constructFlexPLMURL - START ###");
		try {
			FLEXPLM_URL = WTProperties.getServerProperties().getProperty("wt.server.codebase")
					+ "/rfa/jsp/main/Main.jsp?newWindowActivity=VIEW_DOCUMENT&newWindowOid=OR%3Acom.lcs.wc.document.LCSDocument%3A";
		} catch (IOException e) {
			LOGGER.error("IOException in constructFlexPLMURL -" + e.getLocalizedMessage());
		}
		LOGGER.debug("### SMSEPDImagePageEmailProcessor.constructFlexPLMURL - END ###");
				LOGGER.debug("FLEXPLM_URL==" + FLEXPLM_URL);

		return FLEXPLM_URL;
	}

	/**
	 * Process - process Queue Entry.
	 * 
	 */
	public static void processSEPDImagePagecheduleQueue() {
		LOGGER.debug("### SMSEPDImagePageEmailProcessor.processSEPDImagePagecheduleQueue - START ###");
		try {
			String keyTaskNo = "";
			String strTaskName = "";
			HashMap userGroupsMap;
			ArrayList workTasklist;
			// Call this method to get the previous timestamp from which the tasks have to
			// be sent in notification
			Timestamp prevDate = SMEmailNotificationUtil.getPrevDate(SMEmailUtilConstants.TIME_ZONE,
					SMEmailUtilConstants.SEPD_IMAGEPAGE_WF_EMAIL_SCHEDULE_PICKTASKSFROM_INTERVAL);
			LOGGER.debug("prevDate from which task notification will be sent=" + prevDate);
			// Get the tasks for which notification has to be sent
			HashMap hmTasks = SMEmailNotificationUtil
					.getTasksFromProperties(LCSProperties.get("SEPD.ImagePage.NotificationTasks"));
			Iterator ithmTasks = hmTasks.keySet().iterator();
			LOGGER.debug("hmTasks=" + hmTasks);
			
			// Iterate the task map
			while (ithmTasks.hasNext()) {
				// Get the current task number
				keyTaskNo = (String) ithmTasks.next();
				// Get the user attributes/ groups for whom email has to be sent for current
				// task number
				userGroupsMap = SMEmailNotificationUtil
						.getUserAttributes(LCSProperties.get("SEPD.ImagePage.NotificationTasks.emailTo." + keyTaskNo));
				
				strTaskName = (String) hmTasks.get(keyTaskNo);
				LOGGER.debug("strTaskName=" + strTaskName);
				LOGGER.debug("keyTaskNo=" + keyTaskNo);
				LOGGER.debug("userGroupsMap=" + userGroupsMap);
				// Find the list of tasks that are created from previous date and current date
				workTasklist = (ArrayList) SMEmailNotificationUtil.findOpenTasks(strTaskName, prevDate);
				LOGGER.debug("workTasklist=" + workTasklist);
				
				// Some tasks for a single object will be assigned to multiple roles, iterating
				// tasks and removing the duplicate objects in these cases
				HashSet uniqueWorkList = SMSEPDImagePageEmailUtil.findUniqueTasks(workTasklist);
				LOGGER.debug("uniqueWorkList=" + uniqueWorkList.size());
				LOGGER.debug("uniqueWorkList=" + uniqueWorkList);

				// Call this method to send email for each task
				sendSEPDImagePageWFTaskEmail(uniqueWorkList, userGroupsMap, keyTaskNo, strTaskName);
			}

		} catch (WTException e) {
			LOGGER.error("WTException in processSEPDImagePagecheduleQueue method - " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		LOGGER.debug("### SMSEPDImagePageEmailProcessor.processSEPDImagePagecheduleQueue - END ###");
	}

	/**
	 * Method to sort, group the data and send email notifications to users
	 * 
	 * @param uniqueWorkList
	 * @param userList
	 * @param keyTaskNo
	 * @param strTaskName
	 */
	public static void sendSEPDImagePageWFTaskEmail(HashSet uniqueWorkList, Map userList, String keyTaskNo,
			String strTaskName) {
		LOGGER.debug("### SMSEPDImagePageEmailProcessor.sendSEPDImagePageWFTaskEmail - START ###");
		ClientContext lcsContext = null;
		wt.org.WTUser from = null;
		try {
			lcsContext = ClientContext.getContext();
			from = UserGroupHelper.getWTUser(lcsContext.getUserName());
		} catch (WTException e1) {
			// TODO Auto-generated catch block
			LOGGER.error("WTException in SMSEPDImagePageEmailProcessor.sendSEPDImagePageWFTaskEmail -" + e1.getMessage());
			e1.printStackTrace();
		}

		List<SMSEPDImagePageWFEmailBean> notificationList = new ArrayList();
		ArrayList vUserAttributes = new ArrayList();
		if (userList.containsKey("user")) {
			vUserAttributes = (ArrayList) userList.get("user");
		}
		if (userList.containsKey("group")) {
			// notificaiton is not sent to any Groups for SEPD Image Page WF Tasks
		}
		LOGGER.debug("Users attributes for sending email=" + vUserAttributes);
		if (!uniqueWorkList.isEmpty()) {			
			// Iterate the task list
			Iterator itTasks = uniqueWorkList.iterator();
			HashMap finalSortedCollectionMap;
			SMEmailNotificationUtil smEmailNotificationUtil = new SMEmailNotificationUtil();
			// Get notification email properties
			String strHeader = LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.MailHeader");
			String systemInfo = LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.SystemInfo");
			String mailSubject = String.format(
					LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.sepdImagePage.MailSubject"),
					strTaskName);			
			String mailContent = String.format(
					LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.sepdImagePage.MailContent"),
					strTaskName);
			LOGGER.debug("mailContent=" + mailContent);
			LOGGER.debug("mailSubject=" + mailSubject);
			// Iterate each workflow task
			while (itTasks.hasNext()) {
				prepareNotificationBeanList(keyTaskNo, notificationList, vUserAttributes, itTasks);
			}
			
			
			// Sort the list
			notificationList = SMSEPDImagePageEmailUtil.sortObjectCollections(notificationList);
			if(!(notificationList.size()>0))
				return;
			
			// group the list with key = user, value = bean object, so email can be sent
			// consolidated objects for a single user
			finalSortedCollectionMap = SMSEPDImagePageEmailUtil.groupObjectCollections(notificationList);

			LOGGER.debug("Final Sorted Collection Map=" + finalSortedCollectionMap);
			
			// Iterate the map, and for each user, construct the email and send mail
			Iterator itfinalSortedCollectionMap = finalSortedCollectionMap.keySet().iterator();

			sendEmailForTasks(from, finalSortedCollectionMap, systemInfo, strHeader,
						smEmailNotificationUtil, mailContent, mailSubject, itfinalSortedCollectionMap);
		}
		LOGGER.debug("### SMSEPDImagePageEmailProcessor.sendSEPDImagePageWFTaskEmail - END ###");
	}
	
	/**
	 * Method to create bean objects
	 * 
	 * @param keyTaskNo
	 * @param notificationList
	 * @param vUserAttributes
	 * @param itTasks
	 */
	private static void prepareNotificationBeanList(String keyTaskNo, List<SMSEPDImagePageWFEmailBean> notificationList,
			ArrayList vUserAttributes, Iterator itTasks) {
		LOGGER.debug("### SMSEPDImagePageEmailProcessor.prepareNotificationBeanList - START ###");
		LCSDocument docObj;

		WTObject wtObj;
		String strSeasonName;
		Iterator itvUserAttributes;
		LCSSeasonProductLink spLink = null;
		LCSProduct prodObj = null;
		List<Object> userListArr;
		wtObj = (WTObject) itTasks.next();
		userListArr = new ArrayList<Object>();
		LOGGER.debug("LCSDocument Type Name=" + ((LCSDocument) wtObj).getFlexType().getFullName(true));
		
		try {
			// WF Task is instance of LCSDocument and type is Document\\Images Page
			if (wtObj instanceof LCSDocument
					&& ((LCSDocument) wtObj).getFlexType().getFullName(true).startsWith("Document\\Images Page")) {
				docObj = (LCSDocument) wtObj;
				if (("3").equals(keyTaskNo)) {
					//DEF:139 - Fix.
					String strTechApproval = (String) docObj.getValue(TECHNOLOGIST_APPROVAL_SEPD);
					LOGGER.debug("Technologits Approval Value :"+strTechApproval);
					if(FormatHelper.hasContent(strTechApproval) && !strTechApproval.equalsIgnoreCase(TECHAPPROVALCHECKNOPROBLEM)){
						return;
					}
				}
				String partMaster = (String) docObj.getValue(OWNERREFERENCE);
				itvUserAttributes = vUserAttributes.iterator();
				prodObj = SMSEPDImagePageEmailUtil.getProductObj(partMaster);
				LCSSeason season = (LCSSeason) docObj.getValue(SM_IMAGEPAGE_SEASON);
				// Check Product is of type SEPD.
				if (null!=prodObj && prodObj.getFlexType().getFullName(true).startsWith("Product\\SEPD")) {
					// Get Season Product Link for the product object
					spLink = (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(prodObj, season);	
					strSeasonName = season.getName();
					String strImagePageName = docObj.getName();
					String strDocOID = FormatHelper.getNumericObjectIdFromObject(docObj);
					// Method to prepare bean obj.
					prepareBeanObjects(notificationList, strSeasonName, strDocOID, strImagePageName, spLink, itvUserAttributes,
							userListArr);
				}
				
			}
		} catch (WTException e) {
			LOGGER.error("Error in prepareNotificationBeanList method=" + e.getMessage());
			e.printStackTrace();
		}
		LOGGER.debug("### SMSEPDImagePageEmailProcessor.prepareNotificationBeanList - END ###");
	}

	/**
	 * Method to send email
	 * 
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
	private static void sendEmailForTasks(wt.org.WTUser from, HashMap finalSortedCollectionMap,
			String systemInfo, String strHeader, SMEmailNotificationUtil smEmailNotificationUtil, String mailContent,
			String mailSubject, Iterator itfinalSortedCollectionMap) {
		LOGGER.debug("### SMSEPDImagePageEmailProcessor.sendEmailForTasks - START ###");
		WTUser wtToEmailUser = null;
		StringBuilder sb;
		String strTableHeader;
		String toEmailUser;
		ArrayList vecToList = new ArrayList();
		while (itfinalSortedCollectionMap.hasNext()) {
			sb = new StringBuilder();
	
			sb.append(mailContent).append("<br>").append("<br>");
			
			sb.append("<html>");
			sb.append("<head>");
			sb.append("</head>");
			sb.append("<table border='1'>");
			
			Iterator itMOA = MOAHelper.getMOACollection(EMAIL_TABLE_COLUMNS).iterator();
			
			while (itMOA.hasNext()) {
				strTableHeader = (String) itMOA.next();
				sb.append("<th>" + strTableHeader + "</th>");
			}
			
			//while (itfinalSortedCollectionMap.hasNext()) {
				toEmailUser = (String) itfinalSortedCollectionMap.next();
				try {
					wtToEmailUser = wt.org.OrganizationServicesHelper.manager.getUser(toEmailUser);
				} catch (WTException e) {
					LOGGER.error("WTException in SMSEPDImagePageEmailProcessor.sendEmailForTasks -" + e.getMessage());
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				sb.append(SMSEPDImagePageEmailUtil.prepareHTMLTableContent(
						(List<SMSEPDImagePageWFEmailBean>) (finalSortedCollectionMap.get(toEmailUser))));
				//DEF:138 - Fix Start.
				vecToList.add(wtToEmailUser);
				//DEF:138 - Fix End.
				LOGGER.debug("vecToList=" + vecToList);
			//} 
			sb.append("</table>");
			sb.append("</body>");
			sb.append("</html>");
	
			// send email method
			smEmailNotificationUtil.sendEmail(from, vecToList, sb.toString(), mailSubject, strHeader, systemInfo);
			vecToList.clear();
		}
		LOGGER.debug("### SMSEPDImagePageEmailProcessor.sendEmailForTasks - END ###");
	}

	/**
	 * @param notificationList
	 * @param strSeasonName
	 * @param strImagePageName
	 * @param itvUserAttributes
	 * @param userListArr
	 * @param keyTaskNo 
	 */
	private static void prepareBeanObjects(List<SMSEPDImagePageWFEmailBean> notificationList,
			String strSeasonName, String strDocOID, String strImagePageName, LCSSeasonProductLink spLink, Iterator itvUserAttributes, List<Object> userListArr) {
		String strUserAttribute;
		StringTokenizer stUserAtts;
		// Iterate user attributes, in case notification is to be sent for multiple
		// users , iterate and for each user create the SMSEPDProdSeasWFEmailBean
		// object, so email will be sent for all users.
		while (itvUserAttributes.hasNext()) {
			strUserAttribute = (String) itvUserAttributes.next();
			// defined in property = objectType$attributekey, tokenize and get user
			// attribute value
			stUserAtts = new StringTokenizer(strUserAttribute, "$");
			if (("LCSProductSeasonLink").equals(stUserAtts.nextElement())) {
				// Method to get the user list attribute value and flexobject
				userListArr = SMEmailNotificationUtil.getUser((LCSSeasonProductLink) spLink,
						(String) stUserAtts.nextElement());
			}
			// Only when user list is not empty, create the notification bean object
			if (!userListArr.isEmpty()) {
				LOGGER.debug("userListArr=" + userListArr);
				// Set current task's product name, season name,image Page Name, email 
				// to  bean obj and add that to a list
				notificationList.add(SMSEPDImagePageEmailUtil.getBeanObj(strSeasonName, strDocOID, strImagePageName, (WTUser) userListArr.get(0), (String) userListArr.get(1)));
			}
		}
	}

}
