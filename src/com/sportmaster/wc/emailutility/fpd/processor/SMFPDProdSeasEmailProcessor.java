package com.sportmaster.wc.emailutility.fpd.processor;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.UserGroupHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.emailutility.constants.SMEmailUtilConstants;
import com.sportmaster.wc.emailutility.queue.fpd.util.SMFPDProdSeasEmailUtil;
import com.sportmaster.wc.emailutility.queue.sepd.util.SMSEPDProdSeasEmailUtil;
import com.sportmaster.wc.emailutility.util.SMEmailNotificationUtil;

import wt.fc.WTObject;
import wt.org.OrganizationServicesHelper;
import wt.org.WTUser;
import wt.util.WTException;

public class SMFPDProdSeasEmailProcessor {

	/**
	 * Declaration for LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMFPDProdSeasEmailProcessor.class);


	/**
	 * Constructor.
	 */
	public SMFPDProdSeasEmailProcessor() {
		// constructor
	}

	/**
	 * method to email notification task.
	 */
	public void processFPDProdSeasScheduleQueue() {

		LOGGER.debug("### SMFPDProdSeasEmailProcessor.processFPDProdSeasScheduleQueue - START ###");

		// Call this method to get the previous timestamp from which the
		// tasks have to
		// be sent in notification
		Timestamp prevDate = SMEmailNotificationUtil.getPrevDate(SMEmailUtilConstants.TIME_ZONE,
				SMEmailUtilConstants.FPD_PS_WF_EMAIL_SCHEDULE_PICKTASKSFROM_INTERVAL);
		LOGGER.debug("prevDate from which task notification will be sent=" + prevDate);

		try {
			// Get the tasks for which notification has to be sent
			List<String> taskList = FormatHelper.commaSeparatedListToList(LCSProperties.get("FPD.PSWF.NotificationTasks"));

			for (String task : taskList) {

				LOGGER.debug("Task>>>>>>" + task);
				Map<?, ?> userGroupsMap;
				List<?> workTasklist;

				// Get the user attributes/ groups for whom email has to be sent
				// for current
				// task number
				userGroupsMap = SMEmailNotificationUtil
						.getUserAttributes(
								LCSProperties.get("FPD.PSWF.NotificationTasks.emailTo." + task.replaceAll("\\s", "").toLowerCase()));
				LOGGER.debug("userGroupsMap=" + userGroupsMap);

				// Find the list of tasks that are created from previous date
				// and current date
				workTasklist = SMEmailNotificationUtil.findOpenTasks(task, prevDate);
				LOGGER.debug("workTasklist=" + workTasklist);

				// Some tasks for a single object will be assigned to multiple
				// roles, iterating
				// tasks and removing the duplicate objects in these cases
				Set<WTObject> uniqueWorkList = SMFPDProdSeasEmailUtil.findUniqueTasks(workTasklist);
				LOGGER.debug("uniqueWorkList=" + uniqueWorkList.size());
				// LOGGER.debug("uniqueWorkList=" + uniqueWorkList);

				// Call this method to send email for each task
				sendFPDProdSeasWFTaskEmail(uniqueWorkList, userGroupsMap, task);

			}
		} catch (WTException | IOException e) {
			LOGGER.error("WTException in processFPDProdSeasScheduleQueue method - ", e);
		}
		LOGGER.debug("### SMFPDProdSeasEmailProcessor.processFPDProdSeasScheduleQueue - END ###");
	}

	/**
	 * Method to sort, group the data and send email notifications to users
	 *
	 * @param uniqueWorkList
	 * @param userList
	 * @param strTaskName
	 * @throws WTException
	 * @throws IOException
	 */
	public void sendFPDProdSeasWFTaskEmail(Set<WTObject> uniqueWorkList, Map<?, ?> userList, String strTaskName)
			throws WTException, IOException {

		LOGGER.debug("### SMFPDProdSeasEmailProcessor.sendFPDProdSeasWFTaskEmail - START ###");
		ClientContext lcsContext = null;
		WTUser from = null;
		lcsContext = ClientContext.getContext();
		from = UserGroupHelper.getWTUser(lcsContext.getUserName());

		List<SMFPDProdSeasWFEmailBean> notificationList = new ArrayList<>();


		LOGGER.debug("Users attributes for sending email=" + userList.get("user"));
		if (!uniqueWorkList.isEmpty()) {

			Map<String, List<SMFPDProdSeasWFEmailBean>> finalSortedCollectionMap;
			// Get notification email properties
			String systemInfo = LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.SystemInfo");
			String strHeader = LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.MailHeader");


			String mailContent = String.format(LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.fpdProductSeason.MailContent"),
					strTaskName);
			String mailSubject = String.format(LCSProperties.get("com.sportmaster.wc.emailutility.sendEmail.fpdProductSeason.MailSubject"),
					strTaskName);

			// Iterate each workflow task
			for (WTObject task : uniqueWorkList) {
				notificationList.addAll(prepareNotificationBeanList((List<?>) userList.get("user"), task, strTaskName));
			}
			LOGGER.debug("notificationList.size()=" + notificationList.size());

			// Sort the list
			Comparator<SMFPDProdSeasWFEmailBean> comparator = Comparator.comparing(SMFPDProdSeasWFEmailBean::getSeasonName)
					.thenComparing(SMFPDProdSeasWFEmailBean::getBrand).thenComparing(SMFPDProdSeasWFEmailBean::getProduct)
					.thenComparing(SMFPDProdSeasWFEmailBean::getProductionManagerColorway);
			Collections.sort(notificationList, comparator);

			// group the list with key = user, value = bean object, so email can
			// be sent
			// consolidated objects for a single user
			finalSortedCollectionMap = notificationList.stream().collect(Collectors.groupingBy(SMFPDProdSeasWFEmailBean::getEmailUser));
			LOGGER.debug("Final Sorted Collection Map=" + finalSortedCollectionMap);

			sendEmailForTasks(strTaskName, finalSortedCollectionMap, mailContent, from, mailSubject, strHeader, systemInfo);


		}
		LOGGER.debug("### SMFPDProdSeasEmailProcessor.sendSEPDProdSeasWFTaskEmail - END ###");
	}

	/**
	 * Method to create bean object Method to create bean objects
	 *
	 * @param vUserAttributes
	 * @param task
	 * @param strTaskName
	 * @return
	 * @throws WTException
	 */
	private List<SMFPDProdSeasWFEmailBean> prepareNotificationBeanList(List<?> vUserAttributes, WTObject task, String strTaskName)
			throws WTException {
		LOGGER.debug("### SMFPDProdSeasEmailProcessor.prepareNotificationBeanList - START ###");

		String strProductName;
		String strSeasonName;
		String strBrand;
		String productionManagerColorway = "";
		LCSSeasonProductLink spLink = null;
		SMFPDProdSeasWFEmailBean smFPDProdSeasWFEmailBean = null;
		List<SMFPDProdSeasWFEmailBean> notificationList = new ArrayList<>();
		LCSProduct prodARev = null;
		List<?> userListArr;
		List<?> prodManagers;
		LOGGER.debug("Product Type Name=" + ((LCSProduct) task).getFlexType().getFullName(true));

		// WF Task is instance of Product and type is FPD
		if (task instanceof LCSProduct && ((LCSProduct) task).getFlexType().getFullName(true).startsWith("Product\\FPD")) {

			// Get Season Product Link for the product object
			spLink = SeasonProductLocator.getSeasonProductLink((LCSProduct) task);
			// Get Product A REV.
			prodARev = SeasonProductLocator.getProductARev((LCSProduct) task);
			prodARev = (LCSProduct) VersionHelper.latestIterationOf(prodARev);
			if (spLink != null) {
				// Get season name
				strSeasonName = SMSEPDProdSeasEmailUtil.getSeasonName((LCSProduct) task);
				// Get Product name
				strProductName = SMSEPDProdSeasEmailUtil.getProductName(prodARev);

				// Get brand attribute value to send in email
				strBrand = SMEmailNotificationUtil.getObjectValue(prodARev,
						LCSProperties.get("com.sportmaster.wc.emailutility.processor.product.lcsproduct.brand"),
						((LCSProduct) task).getFlexType());

				userListArr = getUser(vUserAttributes, spLink);

				prodManagers = getUser(Arrays.asList("LCSProductSeasonLink$smProductionManager"), spLink);

				if (!userListArr.isEmpty() && "Prepare Allocation".equals(strTaskName)) {

					smFPDProdSeasWFEmailBean = new SMFPDProdSeasWFEmailBean(strSeasonName, strBrand, strProductName, "",
							(WTUser) userListArr.get(0), (String) userListArr.get(1), null);
					notificationList.add(smFPDProdSeasWFEmailBean);

					for (LCSSKU sku : SMFPDProdSeasEmailUtil.getColorway(spLink)) {

						productionManagerColorway = sku.getName();
						LOGGER.debug("productionManagerColorway (CW Name)=" + productionManagerColorway);
						smFPDProdSeasWFEmailBean = new SMFPDProdSeasWFEmailBean(strSeasonName, strBrand, strProductName,
								productionManagerColorway, (WTUser) userListArr.get(0), (String) userListArr.get(1), sku);
						notificationList.add(smFPDProdSeasWFEmailBean);
						
					}

				} else if (!userListArr.isEmpty() && "Assign PM".equals(strTaskName) && !prodManagers.isEmpty()) {

					productionManagerColorway = ((WTUser) prodManagers.get(0)).getFullName();
					smFPDProdSeasWFEmailBean = new SMFPDProdSeasWFEmailBean(strSeasonName, strBrand, strProductName,
							productionManagerColorway,
							(WTUser) userListArr.get(0), (String) userListArr.get(1), null);
					notificationList.add(smFPDProdSeasWFEmailBean);
				}
			}
		}
		LOGGER.debug("final notification list size for " + strTaskName + " task >>>>>>" + notificationList.size());
		LOGGER.debug("### SMFPDProdSeasEmailProcessor.prepareNotificationBeanList - END ###");
		return notificationList;
	}

	/**
	 * return user details.
	 *
	 * @param vUserAttributes
	 * @param spLink
	 * @return
	 */
	private static List<?> getUser(List<?> vUserAttributes, LCSSeasonProductLink spLink) {

		String strUserAttribute;
		StringTokenizer stUserAtts;
		List<?> userListArr = new ArrayList<>();

		// Iterate user attributes, in case notification is to be sent for
		// multiple
		// users , iterate and for each user create the
		// SMSEPDProdSeasWFEmailBean
		// object, so email will be sent for all users.
		for (Object userAttr : vUserAttributes) {

			strUserAttribute = (String) userAttr;

			// defined in property = objectType$attributekey, tokenize and get
			// user
			// attribute value
			stUserAtts = new StringTokenizer(strUserAttribute, "$");

			if (("LCSProductSeasonLink").equals(stUserAtts.nextElement())) {
				// Method to get the user list attribute value and flex object
				userListArr = SMEmailNotificationUtil.getUser(spLink, (String) stUserAtts.nextElement());
			}
		}
		return userListArr;
	}

	/**
	 * Method to send email.
	 *
	 * @param task
	 * @param finalSortedCollectionMap
	 * @param mailContent
	 * @param from
	 * @param mailSubject
	 * @param strHeader
	 * @param systemInfo
	 * @throws WTException
	 * @throws IOException
	 */
	private void sendEmailForTasks(String task, Map<String, List<SMFPDProdSeasWFEmailBean>> finalSortedCollectionMap,
			String mailContent, WTUser from, String mailSubject, String strHeader, String systemInfo) throws WTException, IOException {

		LOGGER.debug("### SMFPDProdSeasEmailProcessor.sendEmailForTasks - START ###");
		StringBuilder sb;
		WTUser wtToEmailUser = null;
		for (String entry : finalSortedCollectionMap.keySet()) {

			wtToEmailUser = OrganizationServicesHelper.manager.getUser(entry,
					OrganizationServicesHelper.ldapManager.getDirectoryContextProvider((String[]) null));

			sb = new StringBuilder();

			sb.append(mailContent).append("<br>").append("<br>");

			sb.append(SMFPDProdSeasEmailUtil.prepareHTMLTableContent(
					MOAHelper.getMOACollection(
							LCSProperties.get("FPD.PSWF.NotificationTasks.emailColumns." + task.replaceAll("\\s", "").toLowerCase())),
					finalSortedCollectionMap.get(entry)));

			ArrayList<WTUser> vecToList = new ArrayList<>();
			vecToList.add(wtToEmailUser);
			LOGGER.debug("vecToList=" + vecToList);

			// send email method
			new SMEmailNotificationUtil().sendEmail(from, vecToList, sb.toString(), mailSubject, strHeader, systemInfo);
		}
		LOGGER.debug("### SMFPDProdSeasEmailProcessor.sendEmailForTasks - END ###");
	}


}
