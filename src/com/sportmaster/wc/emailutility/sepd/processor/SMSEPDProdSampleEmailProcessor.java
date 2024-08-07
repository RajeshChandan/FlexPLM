/**
 * 
 */
package com.sportmaster.wc.emailutility.sepd.processor;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleRequest;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
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
import com.sportmaster.wc.emailutility.queue.sepd.util.SMSEPDProdSampleEmailUtil;
import com.sportmaster.wc.emailutility.util.SMEmailNotificationUtil;
import com.sportmaster.wc.interfaces.webservices.bean.SQLException_Exception;

import wt.fc.WTObject;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTPrincipalReference;
import wt.org.WTUser;
import wt.util.WTException;
import wt.util.WTProperties;

/**
 * @author 'true' Priya.
 *
 */
public class SMSEPDProdSampleEmailProcessor {

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMSEPDProdSampleEmailProcessor.class);

	/**
	 * To get the sample flexplm url
	 */
	private static String SAMPLE_FLEXPLM_URL = "";

	/**
	 * Constructor.
	 */
	protected SMSEPDProdSampleEmailProcessor() {
		// constructor SMSEPDProdSampleEmailProcessor()
	}

	/**
	 * Method to construct the sample flexplm url to provide link for Sample Name in
	 * email table
	 * 
	 * @return
	 */
	public static String constructFlexPLMURL() {
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.constructFlexPLMURL - START ###");
		try {
			SAMPLE_FLEXPLM_URL = WTProperties.getServerProperties().getProperty("wt.server.codebase")
					+ "/rfa/jsp/main/Main.jsp?newWindowActivity=VIEW_SAMPLE&newWindowOid=OR%3Acom.lcs.wc.sample.LCSSample%3A";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("IOException in constructFlexPLMURL -" + e.getLocalizedMessage());
			// e.printStackTrace();
		}
		LOGGER.debug("SAMPLE_FLEXPLM_URL==" + SAMPLE_FLEXPLM_URL);
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.constructFlexPLMURL - START ###");
		return SAMPLE_FLEXPLM_URL;
	}

	/**
	 * Process process Queue Entry.
	 * 
	 * @throws SQLException_Exception
	 */
	public static void processSEPDProdSampleScheduleQueue() {
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.processSEPDProdSampleScheduleQueue - START ###");
		try {
			// Call this method to get the previous timestamp from which the tasks have to
			// be sent in notification
			Timestamp prevDate = SMEmailNotificationUtil.getPrevDate(SMEmailUtilConstants.TIME_ZONE,
					SMEmailUtilConstants.SEPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_PICKTASKSFROM_INTERVAL);
			LOGGER.debug("prevDate from which task notification will be sent=" + prevDate);
			// Get the tasks for which notification has to be sent
			HashMap hmTasks = SMEmailNotificationUtil
					.getTasksFromProperties(LCSProperties.get("SEPD.sampleWF.NotificationTasks"));
			LOGGER.debug("hmTasks=" + hmTasks);
			Iterator ithmTasks = hmTasks.keySet().iterator();
			String keyTaskNo = "";
			String strTaskName = "";
			HashMap userGroupsMap;
			ArrayList workTasklist;
			Iterator itTasks1UniqueWorklist;
			Iterator itTasks1aUniqueWorklist;
			WTObject wtObj;
			LCSSample sampleObj;
			List<Object> userListArr;
			HashSet uniqueWorkListTask1 = new HashSet();
			// Iterate the task map
			while (ithmTasks.hasNext()) {
				// Get the current task number
				keyTaskNo = (String) ithmTasks.next();
				LOGGER.debug("keyTaskNo=" + keyTaskNo);
				// Get the user attributes/ groups for whom email has to be sent for current
				// task number
				userGroupsMap = SMEmailNotificationUtil
						.getUserAttributes(LCSProperties.get("SEPD.sampleWF.NotificationTasks.emailTo." + keyTaskNo));
				LOGGER.debug("userGroupsMap=" + userGroupsMap);
				strTaskName = (String) hmTasks.get(keyTaskNo);
				LOGGER.debug("strTaskName=" + strTaskName);

				// Find the list of tasks that are created from previous date and current date
				workTasklist = (ArrayList) SMEmailNotificationUtil.findOpenTasks(strTaskName, prevDate);
				LOGGER.debug("workTasklist=" + workTasklist);
				// Some tasks for a single object will be assigned to multiple roles, iterating
				// tasks and removing the duplicate objects in these cases
				HashSet uniqueWorkList = SMSEPDProdSampleEmailUtil.findUniqueTasks(workTasklist);
				LOGGER.debug("uniqueWork List===" + uniqueWorkList.size());

				// Call this method to check unique worklist for task 1 and Task 1a.
				uniqueWorkList = checkUniqueWorkList(keyTaskNo, uniqueWorkListTask1, uniqueWorkList);

				// Call this method to send email for each task
				sendSEPDProdSampleWFTaskEmail(uniqueWorkList, userGroupsMap, keyTaskNo, strTaskName);
			}

		} catch (WTException e) {
			LOGGER.error("WTException in processSEPDProdSampleScheduleQueue method - " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.processSEPDProdSampleScheduleQueue - END ###");
	}

	/**
	 * Method to check unique worklist for task 1 and Task 1a
	 * 
	 * @param keyTaskNo
	 * @param uniqueWorkListTask1
	 * @param uniqueWorkList
	 * @param strTaskName
	 * @return HashSet
	 * @throws WTException
	 */
	private static HashSet checkUniqueWorkList(String keyTaskNo, HashSet uniqueWorkListTask1, HashSet uniqueWorkList)
			throws WTException {
		Iterator itTasks1UniqueWorklist;
		Iterator itTasks1aUniqueWorklist;
		WTObject wtObj;
		LCSSample sampleObj;
		List<Object> userListArr;
		if ((SMEmailUtilConstants.SAMPLE_TASK_1).equals(keyTaskNo)) {
			itTasks1UniqueWorklist = uniqueWorkList.iterator();
			while (itTasks1UniqueWorklist.hasNext()) {
				wtObj = (WTObject) itTasks1UniqueWorklist.next();
				LOGGER.debug(" wt Obj=======" + wtObj);
				LOGGER.debug("SampleType Name=" + ((LCSSample) wtObj).getFlexType().getFullName(true));
				// WF Task is instance of Sample
				if (wtObj instanceof LCSSample) {
					sampleObj = (LCSSample) wtObj;
					// Getting Sample object.
					if (!FormatHelper
							.hasContent((String) sampleObj.getValue(SMEmailUtilConstants.SAMPLE_CONFIRMATION_LIST))) {
						uniqueWorkListTask1.add(wtObj);
					}
				}
			}
			uniqueWorkList.clear();
			LOGGER.debug("unique WorkList should be 0=" + uniqueWorkList.size());
			uniqueWorkList.addAll(uniqueWorkListTask1);
			LOGGER.debug("uniqueWorkList should be only empty sample count=" + uniqueWorkList.size());
		} else if ((SMEmailUtilConstants.SAMPLE_TASK_1A).equals(keyTaskNo)) {
			itTasks1aUniqueWorklist = uniqueWorkList.iterator();
			while (itTasks1aUniqueWorklist.hasNext()) {
				wtObj = (WTObject) itTasks1aUniqueWorklist.next();
				LOGGER.debug(" wtObject ======" + wtObj);
				LOGGER.debug("Sample Type Name=" + ((LCSSample) wtObj).getFlexType().getFullName(true));
				uniqueWorkListTask1 = addUniqueWorkListTask1(uniqueWorkListTask1, wtObj);
			}
			uniqueWorkList.clear();
			LOGGER.debug("uniqueWorkList should be 0=" + uniqueWorkList.size());
			uniqueWorkList.addAll(uniqueWorkListTask1);
			LOGGER.debug("uniqueWorkList should be only sample = cancel=" + uniqueWorkList.size());
		}

		LOGGER.debug("unique WorkList size=" + uniqueWorkList.size());
		LOGGER.debug("unique work list=" + uniqueWorkList);
		return uniqueWorkList;
	}

	/**
	 * Method to add UniqueWorkList Task1
	 * 
	 * @param uniqueWorkListTask1
	 * @param wtObj
	 * @return HashSet
	 * @throws WTException
	 */
	private static HashSet addUniqueWorkListTask1(HashSet uniqueWorkListTask1, WTObject wtObj) throws WTException {
		LCSSample sampleObj;
		// WF Task is instance of Sample
		if (wtObj instanceof LCSSample) {
			// Getting Sample object.
			sampleObj = (LCSSample) wtObj;
			// Enter this loop when Sample Confirmation is Rejected.
			if (FormatHelper.hasContent((String) sampleObj.getValue(SMEmailUtilConstants.SAMPLE_CONFIRMATION_LIST))
					&& (SMEmailUtilConstants.SAMPLE_CONFIRMATION_LIST_REJECTED)
							.equals(sampleObj.getValue(SMEmailUtilConstants.SAMPLE_CONFIRMATION_LIST))) {
				uniqueWorkListTask1.add(wtObj);
			}
		}
		return uniqueWorkListTask1;
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
	public static void sendSEPDProdSampleWFTaskEmail(HashSet uniqueWorkList, Map userlist, String keyTaskNo,
			String strTaskName) throws WTException {
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.sendSEPDProdSampleWFTaskEmail - START ###");
		ClientContext lcsContext = null;
		wt.org.WTUser fromUser = null;
		try {
			lcsContext = ClientContext.getContext();
			fromUser = UserGroupHelper.getWTUser(lcsContext.getUserName());
		} catch (WTException e1) {
			// TODO Auto-generated catch block
			LOGGER.error(
					"WTException in SMSEPDProdSampleEmailProcessor.sendSEPDProdSampleWFTaskEmail -" + e1.getMessage());
			e1.printStackTrace();
		}

		List<SMSEPDProdSampleWFEmailBean> notificationList = new ArrayList();
		ArrayList vUserAttributes = new ArrayList();
		if (userlist.containsKey("user")) {
			vUserAttributes = (ArrayList) userlist.get("user");
		}
		if (userlist.containsKey("group")) {
			// notification is not sent to any Groups for SEPD Product Sample WF Tasks
		}
		LOGGER.debug("Users - attributes for sending email=" + vUserAttributes);
		if (!uniqueWorkList.isEmpty()) {
			// Iterate the task list
			Iterator itTasks = uniqueWorkList.iterator();
			HashMap finalSortedCollectionMap;
			// Get email notification properties
			String strSystemInfo = LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.SystemInfo");
			String header = LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.MailHeader");
			SMEmailNotificationUtil smEmailNotificationUtil = new SMEmailNotificationUtil();
			String mailContent = String.format(
					LCSProperties.get(
							"com.sportmaster.wc.emailutility.sendEmail.sepdProductSample.MailContent." + keyTaskNo),
					strTaskName);
			String mailSubject = String.format(
					LCSProperties.get(
							"com.sportmaster.wc.emailutility.sendEmail.sepdProductSample.MailSubject." + keyTaskNo),
					strTaskName);
			// Iterate each workflow task
			while (itTasks.hasNext()) {
				LOGGER.debug(" Iterate each workflow task ");
				prepareNotificationBeanList(keyTaskNo, notificationList, vUserAttributes, itTasks);
			}
			LOGGER.debug("notificationList size =" + notificationList.size());
			// Sort the list
			notificationList = SMSEPDProdSampleEmailUtil.sortObjectCollections(notificationList);
			// group the list with key = user, value = bean object, so email can be sent
			// consolidated objects for a single user
			finalSortedCollectionMap = SMSEPDProdSampleEmailUtil.groupObjectCollections(notificationList);

			LOGGER.debug("Final Sorted Collection Map=" + finalSortedCollectionMap);
			WTPrincipal wtToEmailUser = null;
			// Iterate the map, and for each user, construct the email and send mail
			Iterator itfinalSortedCollectionMap = finalSortedCollectionMap.keySet().iterator();
			while (itfinalSortedCollectionMap.hasNext()) {
				wtToEmailUser = sendEmailForTasks(keyTaskNo, fromUser, finalSortedCollectionMap, strSystemInfo, header,
						smEmailNotificationUtil, mailContent, mailSubject, wtToEmailUser, itfinalSortedCollectionMap);
			}
		}
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.sendSEPDProdSampleWFTaskEmail - END ###");
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
	private static void prepareNotificationBeanList(String keyTaskNo,
			List<SMSEPDProdSampleWFEmailBean> notificationList, ArrayList vUserAttributes, Iterator itTasks)
			throws WTException {

		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.prepareNotificationBeanList - START ###");
		WTObject wtObj;
		String seasonName = "";
		String colorway = "";
		String factory = "";
		String supplierSampleStatus = "";
		String sampleStatus = "";
		String prodType = "";
		LCSProduct prodObj = null;
		LCSSample sampleObj = null;
		LCSSampleRequest sampleReqObj = null;
		LCSSourcingConfigMaster sourceMaster = null;
		LCSSourcingConfig sourceObj = null;
		LCSSeason seasonObj = null;
		LCSSourceToSeasonLink sourceToSeasonLink = null;
		LCSPartMaster master = null;
		LCSProduct prodAObj = null;
		LCSSeasonProductLink spLink = null;
		WTGroup wtgroup = null;
		Iterator itvUserAttributes;
		wtObj = (WTObject) itTasks.next();
		LOGGER.debug("In prepareNotificationBeanList method - wtObj ==" + wtObj);
		LOGGER.debug("In prepareNotificationBeanList method - Sample Type =="
				+ ((LCSSample) wtObj).getFlexType().getFullName(true));
		// WF Task is instance of Sample
		if (wtObj instanceof LCSSample) {
			// Getting Sample object.
			sampleObj = (LCSSample) wtObj;

			// Getting Sample Request Object from sample object.
			sampleReqObj = sampleObj.getSampleRequest();

			// Getting SourceingConfig Master from Sample Request object.
			sourceMaster = (LCSSourcingConfigMaster) sampleReqObj.getSourcingMaster();
			LOGGER.debug(" sourceMaster==" + sourceMaster);
			// Added this check to ignore the sourcing config that are created and deleted
			// from system (when task is still active)
			if (sourceMaster != null && sourceMaster instanceof wt.vc.Mastered) {
				// Getting Sourcing Config Object from source master.
				sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);
				LOGGER.debug(" sourceObj==" + sourceObj);

				// Getting Season.
				seasonObj = (LCSSeason) sampleReqObj.getValue(SMEmailUtilConstants.SEASON_REQUESTED);
				LOGGER.debug("seasonObj==" + seasonObj);

				// Getting Source to season link.
				sourceToSeasonLink = new LCSSourcingConfigQuery().getSourceToSeasonLink(sourceObj, seasonObj);

				// Getting part master.
				master = (LCSPartMaster) sampleObj.getOwnerMaster();

				// Getting ProductARev Object.
				prodAObj = SeasonProductLocator.getProductARev(master);
				LOGGER.debug("prodAObj==" + prodAObj);

				// Get Season Product Link
				spLink = LCSSeasonQuery.findSeasonProductLink(prodAObj, seasonObj);
				LOGGER.debug("spLink==" + spLink);
				// Getting Product Object.
				if (spLink != null) {
					prodObj = SeasonProductLocator.getProductSeasonRev(spLink);
					LOGGER.debug("prodObj==" + prodObj);
					prodType = prodObj.getFlexType().getFullName();
					LOGGER.debug("In prepareNotificationBeanList method - prodType ==" + prodType);
				}
				// Check if product type is either ACC\\SEPD or SEPD.
				if (isACCSEPDOrSEPDProductType(prodType, prodObj, sampleObj)) {
					itvUserAttributes = vUserAttributes.iterator();
					try {
						getAttributesValue(keyTaskNo, notificationList, seasonName, colorway, factory,
								supplierSampleStatus, sampleStatus, prodType, prodObj, sampleObj, sampleReqObj,
								sourceObj, seasonObj, sourceToSeasonLink, spLink, wtgroup, itvUserAttributes);

					} catch (WTException e) {
						LOGGER.error("Error in sendSEPDProdSampleWFTaskEmail method=" + e.getMessage());
						e.printStackTrace();
					}
				}
			}
		}
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.prepareNotificationBeanList - END ###");
	}

	/**
	 * @param prodType
	 * @param prodObj
	 * @param sampleObj
	 * @return
	 */
	private static boolean isACCSEPDOrSEPDProductType(String prodType, LCSProduct prodObj, LCSSample sampleObj) {
		return prodObj != null
				&& (prodType.startsWith(SMEmailUtilConstants.ACC_SEPD_PRODUCT_TYPE)
						|| prodType.startsWith(SMEmailUtilConstants.SEPD_PRODUCT_TYPE))
				&& sampleObj.getFlexType().getFullName().startsWith(SMEmailUtilConstants.SEPD_SAMPLE_TYPE);
	}

	/**
	 * @param keyTaskNo
	 * @param notificationList
	 * @param seasonName
	 * @param colorway
	 * @param businessSupplier
	 * @param factory
	 * @param supplierSampleStatus
	 * @param sampleStatus
	 * @param prodType
	 * @param prodObj
	 * @param sampleObj
	 * @param sourceObj
	 * @param seasonObj
	 * @param sourceToSeasonLink
	 * @param spLink
	 * @param wtgroup
	 * @param itvUserAttributes
	 * @param userListArr
	 * @throws WTException
	 */
	private static void getAttributesValue(String keyTaskNo, List<SMSEPDProdSampleWFEmailBean> notificationList,
			String seasonName, String colorway, String factory, String supplierSampleStatus,
			String sampleStatus, String prodType, LCSProduct prodObj, LCSSample sampleObj,
			LCSSampleRequest sampleReqObj, LCSSourcingConfig sourceObj, LCSSeason seasonObj,
			LCSSourceToSeasonLink sourceToSeasonLink, LCSSeasonProductLink spLink, WTGroup wtgroup,
			Iterator itvUserAttributes) throws WTException {
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.getAttributesValue - START ###");
		String productStyle;
		String sampleName;
		String requestCreator;
		String sampleConfirmation;
		String businessSupplierName = "";
		// Getting season Name.
		if (seasonObj != null) {
			seasonName = seasonObj.getName();
		}
		LOGGER.debug("In getAttributesValue method - seasonName == " + seasonName);

		// Getting Style attribute value.
		productStyle = prodObj.getName();
		LOGGER.debug("In getAttributesValue method - productStyle == " + productStyle);

		colorway = getColorwayName(colorway, sampleObj);
		LOGGER.debug("In getAttributesValue method - colorway == " + colorway);

		// Getting Business Supplier attribute value.
		LCSSupplier vendorObj = (LCSSupplier) sourceObj.getValue(SMEmailUtilConstants.SOURCE_BUSINESS_SUPPLIER);
		LOGGER.debug("In getAttributesValue method - vendorObj == " + vendorObj);
		if (vendorObj != null) {
			wtgroup = getVendorUsersForGroups(vendorObj);
			businessSupplierName = getBusinessSupplierName(vendorObj);
		}
		else{
			// added this to set as empty string when value is null to avoid nullpointerexception in sort method
			businessSupplierName = "";
		}
		LOGGER.debug("In getAttributesValue method - businessSupplierName == " + businessSupplierName);
		LOGGER.debug("In getAttributesValue method - wtgroup == " + wtgroup);

		// Getting Factory.
		LCSSupplier factoryObj = (LCSSupplier) sourceToSeasonLink
				.getValue(SMEmailUtilConstants.SOURCE_TO_SEASON_LINK_FACTORY);
		if (factoryObj != null) {
			factory = factoryObj.getName();
		}
		LOGGER.debug("In getAttributesValue method - factory == " + factory);

		// Getting sample name.
		sampleName = sampleObj.getName();
		LOGGER.debug("In getAttributesValue method - sampleName == " + sampleName);

		// Getting sample Request creator
		WTPrincipalReference creatorSR = sampleReqObj.getCreator();
		// Getting user.
		WTUser user = (WTUser) creatorSR.getPrincipal();
		requestCreator = user.getFullName();
		LOGGER.debug("In getAttributesValue method - requestCreator == " + requestCreator);

		// For task 2 or task 3,
		if ((SMEmailUtilConstants.SAMPLE_TASK_2).equals(keyTaskNo)
				|| (SMEmailUtilConstants.SAMPLE_TASK_3).equals(keyTaskNo)) {
			// Getting supplier sample status
			supplierSampleStatus = SMEmailNotificationUtil.getObjectValue((LCSSample) sampleObj,
					SMEmailUtilConstants.SAMPLE_SUPPLIER_STATUS, sampleObj.getFlexType());
			LOGGER.debug("In getAttributesValue method - supplierSampleStatus ==" + supplierSampleStatus);

			Boolean boolNotSameUser = false;
			boolNotSameUser = isSameAsPMUsers(spLink, requestCreator);
			LOGGER.debug(" IN getAttributesValue method - boolNotSameUser ============ " + boolNotSameUser);
			if (boolNotSameUser) {
				notificationList.add(SMSEPDProdSampleEmailUtil.getBeanObj(seasonName, productStyle, colorway,
						businessSupplierName, factory, sampleName, supplierSampleStatus, sampleStatus, requestCreator,
						(WTUser) user, (String) user.getName(), sampleObj));
			}
		}
		// For task 4,
		else if ((SMEmailUtilConstants.SAMPLE_TASK_4).equals(keyTaskNo)) {
			// Getting sample status
			sampleStatus = SMEmailNotificationUtil.getObjectValue((LCSSample) sampleObj,
					SMEmailUtilConstants.SAMPLE_STATUS, sampleObj.getFlexType());
			LOGGER.debug("In getAttributesValue method - sampleStatus ==" + sampleStatus);
		}
		// Method to prepare bean obj
		prepareBeanObjects(notificationList, seasonName, productStyle, colorway, businessSupplierName, factory, sampleName,
				supplierSampleStatus, sampleStatus, requestCreator, sampleObj, spLink, sourceToSeasonLink,
				itvUserAttributes, keyTaskNo, prodType);

		sampleConfirmation = (String) sampleObj.getValue(SMEmailUtilConstants.SAMPLE_CONFIRMATION_LIST);
		LOGGER.debug("In getAttributesValue method - sampleConfirmation ==" + sampleConfirmation);

		if (wtgroup != null && (((SMEmailUtilConstants.SAMPLE_TASK_1).equals(keyTaskNo)
				&& !FormatHelper.hasContent(sampleConfirmation))
				|| ((SMEmailUtilConstants.SAMPLE_TASK_1A).equals(keyTaskNo)
						&& FormatHelper.hasContent(sampleConfirmation)
						&& (SMEmailUtilConstants.SAMPLE_CONFIRMATION_LIST_REJECTED).equals(sampleConfirmation)))) {
			notificationList.add(SMSEPDProdSampleEmailUtil.getBeanObj(seasonName, productStyle, colorway,
					businessSupplierName, factory, sampleName, supplierSampleStatus, sampleStatus, requestCreator,
					(WTGroup) wtgroup, (String) wtgroup.getName(), sampleObj));
			LOGGER.debug("In getAttributesValue method - Group notificationList ==" + notificationList);
		}
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.getAttributesValue - END ###");
	}

	/**
	 * @param vendorObj
	 * @return
	 */
	private static String getBusinessSupplierName(LCSSupplier vendorObj) {
		String businessSupplier;
		businessSupplier = vendorObj.getName();
		if(businessSupplier == null || "null".equals(businessSupplier)){
			businessSupplier = "";
		}
		return businessSupplier;
	}

	private static String getColorwayName(String colorway, LCSSample sampleObj) throws WTException {
		// Getting colorway name.
		LCSSKU skuObj = (LCSSKU) sampleObj.getValue(SMEmailUtilConstants.SAMPLE_COLORWAY);
		LOGGER.debug("In getAttributesValue method - skuObj == " + skuObj);
		if (skuObj != null) {
			colorway = (String) skuObj.getValue("skuName");
			// added this to set as empty string when value is null to avoid nullpointerexception in sort method
			if(colorway == null || "null".equals(colorway)){
				LOGGER.debug("In getAttributesValue method - colorway == " + colorway);
				colorway = "";
			}
		}
		else{
			LOGGER.debug("In getAttributesValue method - colorway in else == " + colorway);
			// added this to set as empty string when value is null to avoid nullpointerexception in sort method
			colorway = "";
		}
		return colorway;
	}

	/**
	 * If Sample Requester is same as production manager user, then do not include
	 * that task in the email
	 * 
	 * @param spLink
	 * @param requestCreator
	 * @return boolean
	 */
	public static boolean isSameAsPMUsers(LCSSeasonProductLink spLink, String requestCreator) throws WTException {
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.isSameAsPMUsers ###");
		FlexObject fo = null;
		String userAttName = "";
		WTUser user = null;
		fo = (FlexObject) spLink.getValue(SMEmailUtilConstants.PRODUCTION_MANAGER_USER);
		LOGGER.debug("In isSameAsPMUsers method - fo == " + fo);
		if (fo != null && fo.containsKey(SMEmailUtilConstants.FO_FULLNAME)
				&& FormatHelper.hasContent((String) fo.getData(SMEmailUtilConstants.FO_FULLNAME))) {
			userAttName = (String) fo.getData(SMEmailUtilConstants.FO_FULLNAME);
			LOGGER.debug("In isSameAsPMUsers method - userAttName ==" + userAttName);
			if (FormatHelper.hasContent(userAttName)) {
				if (!(requestCreator.equalsIgnoreCase(userAttName))) {
					LOGGER.debug(
							"In isSameAsPMUsers method - Sample Request creator is not same as production Manager");
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Method to get group
	 * 
	 * @param vendorObj
	 * @return WTGroup
	 */
	public static WTGroup getVendorUsersForGroups(LCSSupplier vendorObj) throws WTException {
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.getVendorUsersForGroups ###");
		// Getting vendor Group.
		String vendorGroup = (String) vendorObj.getValue(SMEmailUtilConstants.SUPPLIER_VENDOR_GROUP);

		if (FormatHelper.hasContent(vendorGroup)) {
			// Adding vendorGroup group to vector.
			WTGroup wtgroup = (WTGroup) OrganizationServicesHelper.manager.getPrincipal(vendorGroup);
			return wtgroup;
		}
		return null;
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
	 * @throws WTException
	 */
	private static WTPrincipal sendEmailForTasks(String keyTaskNo, wt.org.WTUser from, HashMap finalSortedCollectionMap,
			String systemInfo, String strHeader, SMEmailNotificationUtil smEmailNotificationUtil, String mailContent,
			String mailSubject, WTPrincipal wtToEmailUser, Iterator itfinalSortedCollectionMap) throws WTException {
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.sendEmailForTasks - START ###");
		StringBuilder sb;
		String emailToUser;
		ArrayList vecToList = new ArrayList();
		LOGGER.debug("In sendEmailForTasks method - itfinalSortedCollectionMap ==" + itfinalSortedCollectionMap);
		emailToUser = (String) itfinalSortedCollectionMap.next();
		LOGGER.debug("In sendEmailForTasks method - emailToUser ==" + emailToUser);
		try {
			wtToEmailUser = (WTPrincipal) OrganizationServicesHelper.manager.getPrincipal(emailToUser);
		} catch (WTException e) {
			LOGGER.error("WTException in SMSEPDProdSampleEmailProcessor.sendEmailForTasks -" + e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb = new StringBuilder();
		sb.append(mailContent).append("<br>").append("<br>");

		LOGGER.debug("\n emailToUser==" + emailToUser);
		WTGroup wtGroup = UserGroupHelper.getWTGroup(emailToUser);
		LOGGER.debug("\n wtGroup==" + wtGroup);
		Enumeration enumer;
		WTPrincipalReference wtParentGroup;
		Boolean boolSupplier = false;

		if (wtGroup != null) {
			enumer = wtGroup.parentGroups();
			while (enumer.hasMoreElements()) {
				wtParentGroup = (WTPrincipalReference) enumer.nextElement();
				LOGGER.debug(wtParentGroup.getName());
				if (wtParentGroup.getName().equals(SMEmailUtilConstants.BUSINESS_SUPPLIER_GROUP)) {
					boolSupplier = true;
					break;
				}
			}
		}
		sb.append(SMSEPDProdSampleEmailUtil.prepareHTMLTableContent(keyTaskNo,
				MOAHelper.getMOACollection(
						LCSProperties.get("SEPD.sampleWF.NotificationTasks.emailColumns." + keyTaskNo)),
				(List<SMSEPDProdSampleWFEmailBean>) (finalSortedCollectionMap.get(emailToUser)), boolSupplier));
		vecToList.add(wtToEmailUser);
		LOGGER.debug("In sendEmailForTasks method - vecToList=" + vecToList);

		// send email method
		smEmailNotificationUtil.sendEmail(from, vecToList, sb.toString(), mailSubject, strHeader, systemInfo);
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.sendEmailForTasks - END ###");
		return wtToEmailUser;
	}

	/**
	 * @param notificationList
	 * @param seasonName
	 * @param productStyle
	 * @param colorway
	 * @param businessSupplier
	 * @param factory
	 * @param sampleName
	 * @param supplierSampleStatus
	 * @param sampleStatus
	 * @param requestCreator
	 * @param requestCreator
	 * @param sampleObj
	 * @param spLink
	 * @param sampleObj
	 * @param sourceToSeasonLink
	 * @param keyTaskNo
	 * @param prodType
	 * @throws WTException
	 */
	private static void prepareBeanObjects(List<SMSEPDProdSampleWFEmailBean> notificationList, String seasonName,
			String productStyle, String colorway, String businessSupplier, String factory, String sampleName,
			String supplierSampleStatus, String sampleStatus, String requestCreator, LCSSample sampleObj,
			LCSSeasonProductLink spLink, LCSSourceToSeasonLink sourceToSeasonLink, Iterator itvUserAttributes,
			String keyTaskNo, String prodType) throws WTException {
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.prepareBeanObjects - START ###");
		String strUserAttribute;
		StringTokenizer stUserAtts;
		String strUserToExclude = "";
		boolean bNotSameUser = false;
		String sampleConfirmation = "";
		String objtype = "";
		String attribute = "";

		// Getting Sample confirmation attribute value.
		sampleConfirmation = (String) sampleObj.getValue(SMEmailUtilConstants.SAMPLE_CONFIRMATION_LIST);
		LOGGER.debug("In prepareBeanObjects method - sampleConfirmation==========" + sampleConfirmation);

		while (itvUserAttributes.hasNext()) {
			// Iterate user attributes, in case notification is to be sent for multiple
			// users , iterate and for each user create the SMSEPDProdSampleWFEmailBean
			// object, so email will be sent for all users.
			strUserAttribute = (String) itvUserAttributes.next();
			LOGGER.debug("In prepareBeanObjects method - keyTaskNo ==" + keyTaskNo);
			LOGGER.debug("In prepareBeanObjects method - strUserAttribute ==" + strUserAttribute);
			// defined in property = objectType$attributekey, tokenize and get user
			// attribute value
			stUserAtts = new StringTokenizer(strUserAttribute, "$");
			LOGGER.debug("In prepareBeanObjects method - stUserAtts ==" + stUserAtts);
			objtype = (String) stUserAtts.nextElement();
			attribute = (String) stUserAtts.nextElement();
			LOGGER.debug("In prepareBeanObjects method - objtype ==" + objtype + "\nattribute ==" + attribute);

			// Enters this loop when keyTaskNo is 1.
			if ((SMEmailUtilConstants.SAMPLE_TASK_1).equals(keyTaskNo)) {
				strUserToExclude = requestCreator;
				// Enters this loop when Sample Confirmation attribute is empty.
				if (!FormatHelper.hasContent(sampleConfirmation)) {
					// Method to add required PS users to notification list
					getPSUserWithStrToExclude(notificationList, seasonName, productStyle, colorway, businessSupplier,
							factory, sampleName, supplierSampleStatus, sampleStatus, requestCreator, sampleObj, spLink,
							keyTaskNo, strUserToExclude, objtype, attribute);
					// Method to add required SourceToSeason users to notification list
					getSourceToSeasonUser(notificationList, seasonName, productStyle, colorway, businessSupplier,
							factory, sampleName, supplierSampleStatus, sampleStatus, requestCreator, sampleObj,
							sourceToSeasonLink, prodType, objtype, attribute);
				}
			}
			// Enters this loop when keyTaskNo is 1a.
			else if ((SMEmailUtilConstants.SAMPLE_TASK_1A).equals(keyTaskNo)) {
				// Enters this loop when Sample confirmation attribute is Rejected.
				if (FormatHelper.hasContent(sampleConfirmation)
						&& (SMEmailUtilConstants.SAMPLE_CONFIRMATION_LIST_REJECTED).equals(sampleConfirmation)) {
					LOGGER.debug("In prepareBeanObjects method -  LCSProductSeasonLink==" + objtype);
					// Method to add required PS users to notification list
					getPSUser(notificationList, seasonName, productStyle, colorway, businessSupplier, factory,
							sampleName, supplierSampleStatus, sampleStatus, requestCreator, sampleObj, spLink, objtype,
							attribute);
					// Method to add required SourceToSeason users to notification list
					getSourceToSeasonUser(notificationList, seasonName, productStyle, colorway, businessSupplier,
							factory, sampleName, supplierSampleStatus, sampleStatus, requestCreator, sampleObj,
							sourceToSeasonLink, prodType, objtype, attribute);
				}
			}
			// Enters this loop when keyTaskNo is either 2 or 3.
			else if ((SMEmailUtilConstants.SAMPLE_TASK_2).equals(keyTaskNo)
					|| (SMEmailUtilConstants.SAMPLE_TASK_3).equals(keyTaskNo)) {
				// Method to add PS users to notification list
				getPSUser(notificationList, seasonName, productStyle, colorway, businessSupplier, factory, sampleName,
						supplierSampleStatus, sampleStatus, requestCreator, sampleObj, spLink, objtype, attribute);
				// Method to add SourceToSeason users to notification list
				getSourceToSeasonUser(notificationList, seasonName, productStyle, colorway, businessSupplier, factory,
						sampleName, supplierSampleStatus, sampleStatus, requestCreator, sampleObj, sourceToSeasonLink,
						prodType, objtype, attribute);
			}
			// Enters this loop when keyTaskNo is 4.
			else if ((SMEmailUtilConstants.SAMPLE_TASK_4).equals(keyTaskNo)) {
				// Method to get users for Task 4.
				getUsersForTask4(notificationList, seasonName, productStyle, colorway, businessSupplier, factory,
						sampleName, supplierSampleStatus, sampleStatus, requestCreator, sampleObj, spLink, objtype,
						attribute);
			}
		}
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.prepareBeanObjects - END ###");
	}

	/**
	 * @param notificationList
	 * @param seasonName
	 * @param productStyle
	 * @param colorway
	 * @param businessSupplier
	 * @param factory
	 * @param sampleName
	 * @param supplierSampleStatus
	 * @param sampleStatus
	 * @param requestCreator
	 * @param sampleObj
	 * @param spLink
	 * @param objtype
	 * @param attribute
	 * @throws WTException
	 */
	private static void getUsersForTask4(List<SMSEPDProdSampleWFEmailBean> notificationList, String seasonName,
			String productStyle, String colorway, String businessSupplier, String factory, String sampleName,
			String supplierSampleStatus, String sampleStatus, String requestCreator, LCSSample sampleObj,
			LCSSeasonProductLink spLink, String objtype, String attribute) throws WTException {
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.getUsersForTask4 - START ###");
		String sampleStatusValue = (String) sampleObj.getValue(SMEmailUtilConstants.SAMPLE_STATUS);
		LOGGER.debug("sampleStatusValue ====" + sampleStatusValue);
		// Enters this loop when sampleStatus attribute is Submitted For Review.
		if (FormatHelper.hasContent(sampleStatusValue)
				&& (SMEmailUtilConstants.SAMPLE_STATUS_SUBMITTED_FOR_REVIEW).equals(sampleStatusValue)) {
			// Method to add required PS users to notification list
			getPSUser(notificationList, seasonName, productStyle, colorway, businessSupplier, factory, sampleName,
					supplierSampleStatus, sampleStatus, requestCreator, sampleObj, spLink, objtype, attribute);
		}
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.getUsersForTask4 - END ###");
	}

	/**
	 * @param notificationList
	 * @param seasonName
	 * @param productStyle
	 * @param colorway
	 * @param businessSupplier
	 * @param factory
	 * @param sampleName
	 * @param supplierSampleStatus
	 * @param sampleStatus
	 * @param requestCreator
	 * @param sampleObj
	 * @param spLink
	 * @param keyTaskNo
	 * @param strUserToExclude
	 * @param objtype
	 * @param attribute
	 */
	private static void getPSUserWithStrToExclude(List<SMSEPDProdSampleWFEmailBean> notificationList, String seasonName,
			String productStyle, String colorway, String businessSupplier, String factory, String sampleName,
			String supplierSampleStatus, String sampleStatus, String requestCreator, LCSSample sampleObj,
			LCSSeasonProductLink spLink, String keyTaskNo, String strUserToExclude, String objtype, String attribute) {
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.getPSUserWithStrToExclude - START ###");
		List<Object> userListArr;
		boolean bNotSameUser = false;
		if ((SMEmailUtilConstants.PS_KEY).equals(objtype)) {
			// Method to get the user list attribute value and flexobject
			userListArr = SMEmailNotificationUtil.getUser((LCSSeasonProductLink) spLink, (String) attribute);
			bNotSameUser = SMSEPDProdSampleEmailUtil.isSameAsUsers(userListArr, keyTaskNo, strUserToExclude);
			LOGGER.debug(" IN getPSUserWithStrToExclude method - bNotSameUser ============ " + bNotSameUser);
			if (bNotSameUser) {
				prepareAddNotificationBean(notificationList, seasonName, productStyle, colorway, businessSupplier,
						factory, sampleName, supplierSampleStatus, sampleStatus, requestCreator, sampleObj,
						userListArr);
			}
		}
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.getPSUserWithStrToExclude - END ###");
	}

	/**
	 * @param notificationList
	 * @param seasonName
	 * @param productStyle
	 * @param colorway
	 * @param businessSupplier
	 * @param factory
	 * @param sampleName
	 * @param supplierSampleStatus
	 * @param sampleStatus
	 * @param requestCreator
	 * @param sampleObj
	 * @param sourceToSeasonLink
	 * @param prodType
	 * @param objtype
	 * @param attribute
	 */
	private static void getSourceToSeasonUser(List<SMSEPDProdSampleWFEmailBean> notificationList, String seasonName,
			String productStyle, String colorway, String businessSupplier, String factory, String sampleName,
			String supplierSampleStatus, String sampleStatus, String requestCreator, LCSSample sampleObj,
			LCSSourceToSeasonLink sourceToSeasonLink, String prodType, String objtype, String attribute) {
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.getSourceToSeasonUser - START ###");
		List<Object> userListArr;
		if ((SMEmailUtilConstants.SOURCE_TO_SEASON_KEY).equals(objtype)) {
			if ((prodType.startsWith(SMEmailUtilConstants.ACC_SEPD_PRODUCT_TYPE)
					&& (attribute.equals(SMEmailUtilConstants.OSO_APP_DEVELOPER)
							|| attribute.equals(SMEmailUtilConstants.OSO_APP_COSTER)))
					|| (prodType.startsWith(SMEmailUtilConstants.SEPD_PRODUCT_TYPE)
							&& (attribute.equals(SMEmailUtilConstants.OSO_DEVELOPER)
									|| attribute.equals(SMEmailUtilConstants.OSO_COSTER)))) {
				// Method to get the user list attribute value and flexobject
				userListArr = SMEmailNotificationUtil.getUser((LCSSourceToSeasonLink) sourceToSeasonLink,
						(String) attribute);
				prepareAddNotificationBean(notificationList, seasonName, productStyle, colorway, businessSupplier,
						factory, sampleName, supplierSampleStatus, sampleStatus, requestCreator, sampleObj,
						userListArr);
			}
		}
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.getSourceToSeasonUser - END ###");
	}

	/**
	 * @param notificationList
	 * @param seasonName
	 * @param productStyle
	 * @param colorway
	 * @param businessSupplier
	 * @param factory
	 * @param sampleName
	 * @param supplierSampleStatus
	 * @param sampleStatus
	 * @param requestCreator
	 * @param sampleObj
	 * @param spLink
	 * @param objtype
	 * @param attribute
	 */
	private static void getPSUser(List<SMSEPDProdSampleWFEmailBean> notificationList, String seasonName,
			String productStyle, String colorway, String businessSupplier, String factory, String sampleName,
			String supplierSampleStatus, String sampleStatus, String requestCreator, LCSSample sampleObj,
			LCSSeasonProductLink spLink, String objtype, String attribute) {
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.getPSUser - START ###");
		List<Object> userListArr;
		if ((SMEmailUtilConstants.PS_KEY).equals(objtype)) {
			// Method to get the user list attribute value and flexobject
			userListArr = SMEmailNotificationUtil.getUser((LCSSeasonProductLink) spLink, (String) attribute);
			prepareAddNotificationBean(notificationList, seasonName, productStyle, colorway, businessSupplier, factory,
					sampleName, supplierSampleStatus, sampleStatus, requestCreator, sampleObj, userListArr);
		}
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.getPSUser - END ###");
	}

	/**
	 * @param notificationList
	 * @param seasonName
	 * @param productStyle
	 * @param colorway
	 * @param businessSupplier
	 * @param factory
	 * @param sampleName
	 * @param supplierSampleStatus
	 * @param sampleStatus
	 * @param requestCreator
	 * @param sampleObj
	 * @param userListArr
	 */
	private static void prepareAddNotificationBean(List<SMSEPDProdSampleWFEmailBean> notificationList,
			String seasonName, String productStyle, String colorway, String businessSupplier, String factory,
			String sampleName, String supplierSampleStatus, String sampleStatus, String requestCreator,
			LCSSample sampleObj, List<Object> userListArr) {
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.prepareAddNotificationBean - START ###");
		// Only when user list is not empty, create the notification bean object
		if (!userListArr.isEmpty()) {
			// Set current task's seasonName, productStyle, colorway, businessSupplier,
			// factory, sampleName, supplierSampleStatus, sampleStatus, requestCreator,
			// email to bean obj and add that to a list
			notificationList.add(SMSEPDProdSampleEmailUtil.getBeanObj(seasonName, productStyle, colorway,
					businessSupplier, factory, sampleName, supplierSampleStatus, sampleStatus, requestCreator,
					(WTUser) userListArr.get(0), (String) userListArr.get(1), sampleObj));
			LOGGER.debug(" In prepareAddNotificationBean method - notificationList == " + notificationList);
		}
		LOGGER.debug("### SMSEPDProdSampleEmailProcessor.prepareAddNotificationBean - END ###");
	}

}