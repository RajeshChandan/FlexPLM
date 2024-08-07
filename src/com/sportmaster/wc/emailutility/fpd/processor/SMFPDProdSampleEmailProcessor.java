/**
 * 
 */
package com.sportmaster.wc.emailutility.fpd.processor;

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
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleRequest;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.UserGroupHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.queue.fpd.util.SMFPDProdSampleEmailUtil;
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
public class SMFPDProdSampleEmailProcessor {

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMFPDProdSampleEmailProcessor.class);

	/**
	 * To get the flexplm sample url
	 */
	private static String FLEXPLM_SAMPLE_URL = "";

	/**
	 * Constructor.
	 */
	protected SMFPDProdSampleEmailProcessor() {
		// constructor SMFPDProdSampleEmailProcessor()
	}

	/**
	 * Method to construct the flexplm sample url to provide link for Sample Request
	 * Name in email table
	 * 
	 * @return
	 */
	public static String constructFlexPLMURL() {
		LOGGER.debug("### SMFPDProdSampleEmailProcessor.constructFlexPLMURL - START ###");
		try {
			FLEXPLM_SAMPLE_URL = WTProperties.getServerProperties().getProperty("wt.server.codebase")
					+ "/rfa/jsp/main/Main.jsp?newWindowActivity=VIEW_SAMPLE&newWindowOid=OR%3Acom.lcs.wc.sample.LCSSample%3A";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.error("IOException in constructFlexPLMURL -" + e.getLocalizedMessage());
			// e.printStackTrace();
		}
		LOGGER.debug("FLEXPLM_SAMPLE_URL==" + FLEXPLM_SAMPLE_URL);
		LOGGER.debug("### SMFPDProdSampleEmailProcessor.constructFlexPLMURL - START ###");
		return FLEXPLM_SAMPLE_URL;
	}

	/**
	 * Process process Queue Entry.
	 * 
	 * @throws SQLException_Exception
	 */
	public static void processFPDProdSampleScheduleQueue() {
		LOGGER.debug("### SMFPDProdSampleEmailProcessor.processFPDProdSampleScheduleQueue - START ###");
		try {
			// Call this method to get the previous timestamp from which the tasks have to
			// be sent in notification
			Timestamp prevDate = SMEmailNotificationUtil.getPrevDate(SMEmailUtilConstants.TIME_ZONE,
					SMEmailUtilConstants.FPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_PICKTASKSFROM_INTERVAL);
			LOGGER.debug("prevDate from which task notification will be sent=" + prevDate);
			// Get the tasks for which notification has to be sent
			HashMap tasks = SMEmailNotificationUtil
					.getTasksFromProperties(LCSProperties.get("FPD.sampleWF.NotificationTasks"));
			LOGGER.debug("tasks=" + tasks);
			Iterator ittasks = tasks.keySet().iterator();
			String keyTaskNo = "";
			String taskName = "";
			HashMap userGroupsMap;
			ArrayList taskList;
			// Iterate the task map
			while (ittasks.hasNext()) {
				// Get the current task number
				keyTaskNo = (String) ittasks.next();
				LOGGER.debug("keyTaskNo=" + keyTaskNo);
				// Get the user attributes/ groups for whom email has to be sent for current
				// task number
				userGroupsMap = SMEmailNotificationUtil
						.getUserAttributes(LCSProperties.get("FPD.sampleWF.NotificationTasks.emailTo." + keyTaskNo));
				LOGGER.debug("userGroupsMap=" + userGroupsMap);
				taskName = (String) tasks.get(keyTaskNo);
				LOGGER.debug("taskName=" + taskName);

				// Find the list of tasks that are created from previous date and current date
				taskList = (ArrayList) SMEmailNotificationUtil.findOpenTasks(taskName, prevDate);
				LOGGER.debug("taskList=" + taskList);
				// Some tasks for a single object will be assigned to multiple roles, iterating
				// tasks and removing the duplicate objects in these cases
				HashSet uniqueWorkList = SMFPDProdSampleEmailUtil.findUniqueTasks(taskList);
				LOGGER.debug("uniqueWorkList size=" + uniqueWorkList.size());
				LOGGER.debug("uniqueWorkList=" + uniqueWorkList);

				// Call this method to send email for each task
				sendFPDProdSampleWFTaskEmail(uniqueWorkList, userGroupsMap, keyTaskNo, taskName);
			}

		} catch (WTException e) {
			LOGGER.error("WTException in processFPDprodSampleScheduleQueue method - " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		LOGGER.debug("### SMFPDProdSampleEmailProcessor.processFPDProdSampleScheduleQueue - END ###");
	}

	/**
	 * Method to sort, group the data and send email notifications to users
	 * 
	 * @param uniqueWorkList
	 * @param userList
	 * @param keyTaskNo
	 * @param taskName
	 * @throws WTException
	 */
	public static void sendFPDProdSampleWFTaskEmail(HashSet uniqueWorkList, Map userList, String keyTaskNo,
			String taskName) throws WTException {
		LOGGER.debug("### SMFPDProdSampleEmailProcessor.sendFPDprodSampleWFTaskEmail - START ###");
		ClientContext lcsContext = null;
		wt.org.WTUser from = null;
		try {
			lcsContext = ClientContext.getContext();
			from = UserGroupHelper.getWTUser(lcsContext.getUserName());
		} catch (WTException e1) {
			// TODO Auto-generated catch block
			LOGGER.error(
					"WTException in SMFPDProdSampleEmailProcessor.sendFPDprodSampleWFTaskEmail -" + e1.getMessage());
			e1.printStackTrace();
		}

		List<SMFPDProdSampleWFEmailBean> notificationList = new ArrayList();
		ArrayList vUserAttributes = new ArrayList();
		if (userList.containsKey("user")) {
			vUserAttributes = (ArrayList) userList.get("user");
		}
		if (userList.containsKey("group")) {
			// notificaiton is not sent to any Groups for FPD Product Sample WF Tasks
		}
		LOGGER.debug("Users attributes for sending email=" + vUserAttributes);
		LOGGER.debug("unique WorkList===" + uniqueWorkList);
		if (!uniqueWorkList.isEmpty()) {
			// Iterate the task list
			Iterator itTasks = uniqueWorkList.iterator();
			HashMap finalSortedCollectionMap;
			// Get notification email properties
			String systemInfo = LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.SystemInfo");
			String strHeader = LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.MailHeader");
			SMEmailNotificationUtil smEmailNotificationUtil = new SMEmailNotificationUtil();
			String mailContent = String.format(
					LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.fpdProductSample.MailContent"),
					taskName);
			String mailSubject = String.format(
					LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.fpdProductSample.MailSubject"),
					taskName);
			// Iterate each workflow task
			while (itTasks.hasNext()) {
				prepareNotificationBeanList(notificationList, vUserAttributes, itTasks);
			}
			LOGGER.debug("notificationList.size()=" + notificationList.size());
			// Sort the list
			notificationList = SMFPDProdSampleEmailUtil.sortObjectCollections(notificationList);
			// group the list with key = user, value = bean object, so email can be sent
			// consolidated objects for a single user
			finalSortedCollectionMap = SMFPDProdSampleEmailUtil.groupObjectCollections(notificationList);

			LOGGER.debug("Final Sorted Collection Map=" + finalSortedCollectionMap);
			WTPrincipal wtEmailToUser = null;
			// Iterate the map, and for each user, construct the email and send mail
			Iterator itfinalSortedCollectionMap = finalSortedCollectionMap.keySet().iterator();
			while (itfinalSortedCollectionMap.hasNext()) {
				wtEmailToUser = sendEmailForTasks(keyTaskNo, from, finalSortedCollectionMap, systemInfo, strHeader,
						smEmailNotificationUtil, mailContent, mailSubject, wtEmailToUser, itfinalSortedCollectionMap);
			}
		}
		LOGGER.debug("### SMFPDProdSampleEmailProcessor.sendFPDprodSampleWFTaskEmail - END ###");
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
	private static void prepareNotificationBeanList(List<SMFPDProdSampleWFEmailBean> notificationList,
			ArrayList vUserAttributes, Iterator itTasks) throws WTException {
		LOGGER.debug("### SMFPDProdSampleEmailProcessor.prepareNotificationBeanList - START ###");
		WTObject wtObj;
		String seasonName = "";
		String brand = "";
		String style = "";
		String colorway = "";
		String requestName = "";
		String sampleSize = "";
		String requestCreator = "";
		String supplierStatus = "";
		String prodType = "";
		WTGroup wtgroup = null;
		LCSProduct prodObj = null;
		Iterator itvUserAttributes;
		String businessSupplierName = "";
		List<Object> userListArr;
		wtObj = (WTObject) itTasks.next();
		LOGGER.debug(" wt Obj======" + wtObj);
		userListArr = new ArrayList<Object>();
		LOGGER.debug("Sample Type =" + ((LCSSample) wtObj).getFlexType().getFullName(true));
		// WF Task is instance of Sample
		if (wtObj instanceof LCSSample) {
			// Getting Sample object.
			LCSSample sampleObj = (LCSSample) wtObj;

			// Getting Sample Request Object from sample object.
			LCSSampleRequest sampleReqObj = sampleObj.getSampleRequest();

			// Getting Sourcing Config Master from Sample Request object.
			LCSSourcingConfigMaster sourceMaster = (LCSSourcingConfigMaster) sampleReqObj.getSourcingMaster();
			LOGGER.debug(" sourceMaster==" + sourceMaster);
			// Added this check to skip sourcing configs that are deleted from system (when
			// task is still open)
			if (isValidSource(sourceMaster)) {
				// Getting Sourcing Config Object from source master.
				LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourceMaster);
				LOGGER.debug(" sourceObj==" + sourceObj);
				// Getting Season.
				LCSSeason seasonObj = (LCSSeason) sampleReqObj.getValue(SMEmailUtilConstants.SEASON_REQUESTED);
				LOGGER.debug("seasonObj==" + seasonObj);
				// Getting part master.
				LCSPartMaster master = (LCSPartMaster) sampleObj.getOwnerMaster();

				// Getting Product Object.
				LCSProduct prodAbj = SeasonProductLocator.getProductARev(master);
				LOGGER.debug("prodAObj==" + prodAbj);
				// Get Season Product Link
				LCSSeasonProductLink spLink = LCSSeasonQuery.findSeasonProductLink(prodAbj, seasonObj);

				// Getting Product Object.
				if (spLink != null) {
					prodObj = SeasonProductLocator.getProductSeasonRev(spLink);
					LOGGER.debug(" prodType========" + prodObj.getFlexType().getFullName());
				}

				// Product Type is FPD and SampleObj type is FPD.
				if (prodObj != null && isFPDProductType(prodObj, sampleObj)) {
					itvUserAttributes = vUserAttributes.iterator();

					seasonName = seasonObj.getName();
					LOGGER.debug(" seasonName ======== " + seasonName);

					// Get brand attribute value
					brand = SMEmailNotificationUtil.getObjectValue((LCSProduct) prodObj,
							LCSProperties.get("com.sportmaster.wc.emailutility.processor.product.lcsproduct.brand"),
							prodObj.getFlexType());
					LOGGER.debug(" brand ======== " + brand);

					// Getting Style attribute value.
					style = prodObj.getName();
					LOGGER.debug(" style ======== " + style);

					// Getting Business Supplier attribute value.
					LCSSupplier vendorObj = (LCSSupplier) sourceObj
							.getValue(SMEmailUtilConstants.SOURCE_BUSINESS_SUPPLIER);
					if (vendorObj != null) {
						//businessSupplier = vendorObj.getName();
						businessSupplierName = getBusinessSupplierName(vendorObj);
						wtgroup = getVendorUsersForGroups(vendorObj);
					}
					else{
						// added this to set as empty string when value is null to avoid nullpointerexception in sort method
						businessSupplierName = "";
					}
					LOGGER.debug(" wtgroup ======== " + wtgroup);
					LOGGER.debug(" businessSupplierName ======== " + businessSupplierName);

					colorway = getSKUName(sampleObj);

					// Getting sample Request name.
					requestName = sampleReqObj.getName();
					LOGGER.debug(" requestName ======== " + requestName);

					// Getting sampleSize name.
					sampleSize = (String) FormatHelper
							.format((String) sampleObj.getValue(SMEmailUtilConstants.SAMPLE_SIZE));
					LOGGER.debug(" sampleSize ======== " + sampleSize);

					// Getting sample Request creator
					WTPrincipalReference creatorSR = sampleReqObj.getCreator();
					// Getting user.
					WTUser user = (WTUser) creatorSR.getPrincipal();
					requestCreator = user.getFullName();
					LOGGER.debug(" requestCreator ======== " + requestCreator);

					// Getting supplierStatus name.
					supplierStatus = SMEmailNotificationUtil.getObjectValue((LCSSample) sampleObj,
							SMEmailUtilConstants.SAMPLE_SUPPLIER_STATUS, sampleObj.getFlexType());
					LOGGER.debug(" supplierStatus ======== " + supplierStatus);

					// Method to prepare bean obj
					prepareBeanObjects(notificationList, seasonName, brand, style, colorway, businessSupplierName,
							requestName, sampleSize, requestCreator, supplierStatus, spLink, itvUserAttributes,
							userListArr, sampleObj);

					addNotificationListForVendorGroup(notificationList, seasonName, brand, style, colorway,
							businessSupplierName, requestName, sampleSize, requestCreator, supplierStatus, wtgroup,
							sampleObj);
				}
			}
		}
		LOGGER.debug("### SMFPDProdSampleEmailProcessor.prepareNotificationBeanList - END ###");
	}

	/**
	 * @param vendorObj
	 * @return
	 */
	private static String getBusinessSupplierName(LCSSupplier vendorObj) {
		LOGGER.debug(" In getBusinessSupplierName methd - vendorObj======" + vendorObj);
		String businessSupplier;
		businessSupplier = vendorObj.getName();
		if(businessSupplier == null || "null".equals(businessSupplier)){
			businessSupplier = "";
		}
		LOGGER.debug(" In getBusinessSupplierName methd - businessSupplier======" + businessSupplier);
		return businessSupplier;
	}
	
	/**
	 * @param notificationList
	 * @param seasonName
	 * @param brand
	 * @param style
	 * @param colorway
	 * @param businessSupplier
	 * @param requestName
	 * @param sampleSize
	 * @param requestCreator
	 * @param supplierStatus
	 * @param wtgroup
	 * @param sampleObj
	 */
	private static void addNotificationListForVendorGroup(List<SMFPDProdSampleWFEmailBean> notificationList,
			String seasonName, String brand, String style, String colorway, String businessSupplier, String requestName,
			String sampleSize, String requestCreator, String supplierStatus, WTGroup wtgroup, LCSSample sampleObj) {
		if (wtgroup != null) {
			notificationList.add(SMFPDProdSampleEmailUtil.getBeanObj(seasonName, brand, style, colorway,
					businessSupplier, requestName, sampleSize, requestCreator, supplierStatus, (WTGroup) wtgroup,
					(String) wtgroup.getName(), sampleObj));
			LOGGER.debug(" In prepareNotificationBeanList methd - notificationList======" + notificationList);

		}
	}
	
	/**
	 * @param sampleObj
	 * @return
	 */
	private static String getSKUName(LCSSample sampleObj) throws WTException {
		String colorway = "";
		// Getting colorway name.
		LCSSKU skuObj = (LCSSKU) sampleObj.getValue(SMEmailUtilConstants.SAMPLE_COLORWAY);
		if (skuObj != null) {
			colorway = (String) skuObj.getValue("skuName");
			// added this to set as empty string when value is null to avoid nullpointerexception in sort method
			if(colorway == null || "null".equals(colorway)){
				LOGGER.debug("In getSKUName method - colorway == " + colorway);
				colorway = "";
			}
		}
		else {
			LOGGER.debug("In getSKUName method - colorway in else == " + colorway);
			// added this to set as empty string when value is null to avoid nullpointerexception in sort method
			colorway = "";
		}
		LOGGER.debug(" Colorway ======== " + colorway);
		return colorway;
	}

	/**
	 * @param sourceMaster
	 * @return
	 */
	private static boolean isValidSource(LCSSourcingConfigMaster sourceMaster) {
		return sourceMaster != null && sourceMaster instanceof wt.vc.Mastered;
	}

	/**
	 * @param prodObj
	 * @param sampleObj
	 * @return
	 */
	private static boolean isFPDProductType(LCSProduct prodObj, LCSSample sampleObj) {
		return (prodObj.getFlexType().getFullName().startsWith(SMEmailUtilConstants.FPD_PRODUCT_TYPE))
				&& sampleObj.getFlexType().getFullName().startsWith(SMEmailUtilConstants.FOOTWEAR_SAMPLE_TYPE);
	}

	/**
	 * Method to get Vendor Users For Groups
	 * 
	 * @param vendorObj
	 * @return WTGroup
	 * @throws WTException
	 */
	public static WTGroup getVendorUsersForGroups(LCSSupplier vendorObj) throws WTException {
		// Getting vendor Group.
		String vendorGroup = (String) vendorObj.getValue(SMEmailUtilConstants.SUPPLIER_VENDOR_GROUP);

		if (FormatHelper.hasContent(vendorGroup)) {
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
	 * @param wtEmailToUser
	 * @param itfinalSortedCollectionMap
	 * @return
	 * @throws WTException
	 */
	private static WTPrincipal sendEmailForTasks(String keyTaskNo, wt.org.WTUser from, HashMap finalSortedCollectionMap,
			String systemInfo, String strHeader, SMEmailNotificationUtil smEmailNotificationUtil, String mailContent,
			String mailSubject, WTPrincipal wtEmailToUser, Iterator itfinalSortedCollectionMap) throws WTException {
		LOGGER.debug("### SMFPDProdSampleEmailProcessor.sendEmailForTasks - START ###");
		String toEmailUser;
		StringBuilder sb;
		ArrayList vecToList = new ArrayList();
		toEmailUser = (String) itfinalSortedCollectionMap.next();
		try {
			wtEmailToUser = (WTPrincipal) OrganizationServicesHelper.manager.getPrincipal(toEmailUser);
		} catch (WTException e) {
			LOGGER.error("WTException in SMFPDProdSampleEmailProcessor.sendEmailForTasks -" + e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb = new StringBuilder();
		sb.append(mailContent).append("<br>").append("<br>");

		LOGGER.debug("\n toEmailUser==" + toEmailUser);
		WTGroup wtgroup = UserGroupHelper.getWTGroup(toEmailUser);
		LOGGER.debug("\n wtgroup==" + wtgroup);
		Enumeration enumer;
		WTPrincipalReference wtParentGroup;
		Boolean bSupplier = false;

		if (wtgroup != null) {
			enumer = wtgroup.parentGroups();
			while (enumer.hasMoreElements()) {

				wtParentGroup = (WTPrincipalReference) enumer.nextElement();
				LOGGER.debug(wtParentGroup.getName());
				if (wtParentGroup.getName().equals(SMEmailUtilConstants.BUSINESS_SUPPLIER_GROUP)) {
					bSupplier = true;
					break;
				}
			}
		}

		sb.append(SMFPDProdSampleEmailUtil.prepareHTMLTableContent(
				MOAHelper.getMOACollection(
						LCSProperties.get("FPD.sampleWF.NotificationTasks.emailColumns." + keyTaskNo)),
				(List<SMFPDProdSampleWFEmailBean>) (finalSortedCollectionMap.get(toEmailUser)), bSupplier));

		vecToList.add(wtEmailToUser);
		LOGGER.debug("vecToList=" + vecToList);

		// send email method
		smEmailNotificationUtil.sendEmail(from, vecToList, sb.toString(), mailSubject, strHeader, systemInfo);
		LOGGER.debug("### SMFPDProdSampleEmailProcessor.sendEmailForTasks - END ###");
		return wtEmailToUser;
	}

	/**
	 * @param notificationList
	 * @param seasonName
	 * @param brand
	 * @param style
	 * @param colorway
	 * @param businessSupplier
	 * @param requestName
	 * @param sampleSize
	 * @param requestCreator
	 * @param supplierStatus
	 * @param spLink
	 * @param sourceToSeasonLink
	 * @param itvUserAttributes
	 * @param userListArr
	 * @param sampleObj
	 * @throws WTException
	 */
	private static void prepareBeanObjects(List<SMFPDProdSampleWFEmailBean> notificationList, String seasonName,
			String brand, String style, String colorway, String businessSupplier, String requestName, String sampleSize,
			String requestCreator, String supplierStatus, LCSSeasonProductLink spLink, Iterator itvUserAttributes,
			List<Object> userListArr, LCSSample sampleObj) throws WTException {
		String strUserAttribute;
		StringTokenizer stUserAtts;
		// Iterate user attributes, in case notification is to be sent for multiple
		// users , iterate and for each user create the SMFPDProdSampleWFEmailBean
		// object, so email will be sent for all users.
		while (itvUserAttributes.hasNext()) {
			strUserAttribute = (String) itvUserAttributes.next();
			// defined in property = objectType$attributekey, tokenize and get user
			// attribute value
			stUserAtts = new StringTokenizer(strUserAttribute, "$");
			LOGGER.debug(" stUserAtts===" + stUserAtts);
			if ((SMEmailUtilConstants.PS_KEY).equals(stUserAtts.nextElement())) {
				// Method to get the user list attribute value and flexobject
				userListArr = SMEmailNotificationUtil.getUser((LCSSeasonProductLink) spLink,
						(String) stUserAtts.nextElement());
				LOGGER.debug("PS userListArr ==" + userListArr);
			}
			// Only when user list is not empty, create the notification bean object (For
			// OSO Developer Users)
			if (!userListArr.isEmpty()) {
				// Set current task's seasonName, brand, style, colorway, businessSupplier,
				// requestName, sampleSize, requestCreator, supplierStatus, email to bean obj
				// and add that to a list
				notificationList.add(SMFPDProdSampleEmailUtil.getBeanObj(seasonName, brand, style, colorway,
						businessSupplier, requestName, sampleSize, requestCreator, supplierStatus,
						(WTUser) userListArr.get(0), (String) userListArr.get(1), sampleObj));
			}
		}
	}

}