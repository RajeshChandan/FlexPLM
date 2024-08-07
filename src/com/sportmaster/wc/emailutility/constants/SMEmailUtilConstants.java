package com.sportmaster.wc.emailutility.constants;

import com.lcs.wc.util.LCSProperties;

/**
 * @author Priya
 * Class for constant variables
 *
 */
public class SMEmailUtilConstants {

	/**
	 * EXECUTE.
	 */
	public static final String EXECUTE="EXECUTE";
	
	/**
	 * SCHEDULE.
	 */
	public static final String SCHEDULE="SCHEDULE";	
	
	/**
	 * Declaration constants for time zone.
	 */
	public static final String TIME_ZONE=LCSProperties.get("com.sportmaster.wc.emailutility.timeZone");
	
	/**
	 * Declaration constants for Queue start ERROR CODE.
	 */
	public static final String QUEUE_START_ERROR_CODE=LCSProperties.get("com.sportmaster.wc.emailutility.errorcode.queuestart");
	
	/**
	 * Declaration constants for QUEUE START ERROR CODE.
	 */
	public static final String QUEUE_START_ERROR_MESSAGE=LCSProperties.get("com.sportmaster.wc.emailutility.queue.starterrormassage");
	
	/**
	 * Declaration constants for Queue RESCHEDULE ERROR CODE.
	 */
	public static final String QUEUE_RESCHEDULE_ERROR_CODE=LCSProperties.get("com.sportmaster.wc.emailutility.errorcode.queuereschedule");
	
	/**
	 * Declaration constants for Queue RESCHEDULE ERROR CODE message.
	 */
	public static final String QUEUE_RESCHEDULE_ERROR_MESSAGE=LCSProperties.get("com.sportmaster.wc.emailutility.queue.rescheduleerrormassage");
	
	/**
	 * SEPD Product Season WF Email Notification start time.
	 */
	public final static String SEPD_PS_WF_EMAIL_SCHEDULE_QUEUE_START_TIME = LCSProperties.get("com.sportmaster.wc.emailutility.SEPDProdSeasWFTaskNotification.scheduleQueueStartTime");
	
	/**
	 * AM or PM for SEPD Product Season WF Email Notification Queue.
	 */
	public final static String SEPD_PS_WF_EMAIL_SCHEDULE_QUEUE_START_AM = LCSProperties.get("com.sportmaster.wc.emailutility.SEPDProdSeasWFTaskNotification.AMorPM");
	
	/**
	 * Schedule Queue Name for SEPD Product Season WF Email Notification.
	 */
	public static final String SEPD_PS_WF_EMAIL_SCHEDULE_QUEUE_NAME = LCSProperties.get("com.sportmaster.wc.emailutility.SEPDProdSeasWFTaskEmailScheduleQueueName");
	
	/**
	 * Time Interval from which the tasks have to be sent in notification
	 */
	public final static long SEPD_PS_WF_EMAIL_SCHEDULE_PICKTASKSFROM_INTERVAL = LCSProperties.get("com.sportmaster.wc.emailutility.SEPDProdSeasWFTaskNotification.pickTasksFromLastHours",24);

	/**
	 * User List Object FO Name
	 */
	public final static String FO_NAME = "NAME";
	
	/**
	 * HTML_TD
	 */
	public final static String HTML_TD = "<td> ";
	
	/**
	 * HTML_SLASH_TD
	 */
	public final static String HTML_SLASH_TD = " </td>";
	
	/**
	 * HTML_SLASH_TD
	 */
	public final static String OBJECT_ID = "thePersistInfo.theObjectIdentifier.id";
	
	/**
	 * SCHEDULEJOBFORNOTIFICATION
	 */
	public final static String SCHEDULEJOBFORNOTIFICATION = "scheduleJobForNotification";
	
	/**
	 * HTML_TH
	 */
	public final static String HTML_TH = "<th>";
	
	/**
	 * HTML_SLASH_TH
	 */
	public final static String HTML_SLASH_TH = "</th>";
		
	/**
	 * Image Page WF Email Notification start time.
	 */
	public static final String SEPD_IMAGEPAGE_WF_EMAIL_SCHEDULE_QUEUE_START_TIME = LCSProperties.get("com.sportmaster.wc.emailutility.sepd.imagePageWFTaskNotification.scheduleQueueStartTime");
	
	/**
	 * AM or PM for Image Page WF Email Notification Queue.
	 */
	public static final String SEPD_IMAGEPAGE_WF_EMAIL_SCHEDULE_QUEUE_START_AM_OR_PM = LCSProperties.get("com.sportmaster.wc.emailutility.sepd.imagePageWFTaskNotification.AMorPM");
	
	/**
	 * Schedule Queue Name for Image Page WF Email Notification.
	 */
	public static final String SEPD_IMAGEPAGE_WF_EMAIL_SCHEDULE_QUEUE_NAME = LCSProperties.get("com.sportmaster.wc.emailutility.sepd.imagePageWFTaskNotification.scheduleQueueName");
	
	/**
	 * Image Page WF Email Notification time Interval from which the tasks have to be sent in notification
	 */
	public static final long SEPD_IMAGEPAGE_WF_EMAIL_SCHEDULE_PICKTASKSFROM_INTERVAL = LCSProperties.get("com.sportmaster.wc.emailutility.sepd.imagePageWFTaskNotification.pickTasksFromLastHours",24);
	
// Start Phase 13 - SEPD Product Sample Workflow
	/**
	 * SEPD Product Sample WF Email Notification start time.
	 */
	public static final String SEPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_QUEUE_START_TIME = LCSProperties
			.get("com.sportmaster.wc.emailutility.SEPDProdSampleWFTaskNotification.scheduleQueueStartTime");

	/**
	 * AM or PM for SEPD Product Sample WF Email Notification Queue.
	 */
	public static final String SEPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_QUEUE_START_AM = LCSProperties
			.get("com.sportmaster.wc.emailutility.SEPDProdSampleWFTaskNotification.AMorPM");

	/**
	 * Schedule Queue Name for SEPD Product Sample WF Email Notification.
	 */
	public static final String SEPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_QUEUE_NAME = LCSProperties
			.get("com.sportmaster.wc.emailutility.SEPDProdSampleWFTaskEmailScheduleQueueName");

	/**
	 * Time Interval from which the tasks have to be sent in notification
	 */
	public static final long SEPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_PICKTASKSFROM_INTERVAL = LCSProperties
			.get("com.sportmaster.wc.emailutility.SEPDProdSampleWFTaskNotification.pickTasksFromLastHours", 24);
	// End Phase 13 - SEPD Product Sample Workflow

	// Start Phase 13 - FPD & SEPD Product Sample Workflow
	/**
	 * User List Object FO Full Name
	 */
	public final static String FO_FULLNAME = "FULLNAME";
	/**
	 * Sample - Task 1.
	 */
	public static final String SAMPLE_TASK_1 = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sample.task1");
	/**
	 * Sample - Task 1a.
	 */
	public static final String SAMPLE_TASK_1A = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sample.task1a");
	/**
	 * Sample - Task 2.
	 */
	public static final String SAMPLE_TASK_2 = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sample.task2");
	/**
	 * Sample - Task 3.
	 */
	public static final String SAMPLE_TASK_3 = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sample.task3");
	/**
	 * Sample - Task 4.
	 */
	public static final String SAMPLE_TASK_4 = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sample.task4");
	/**
	 * Product - ACC \\ SEPD Type.
	 */
	public static final String ACC_SEPD_PRODUCT_TYPE = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.product.accSEPDType");
	/**
	 * Product - SEPD Type.
	 */
	public static final String SEPD_PRODUCT_TYPE = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.product.sepdType");
	/**
	 * Sample - SEPD Type.
	 */
	public static final String SEPD_SAMPLE_TYPE = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sample.sepdType");
	/**
	 * Sample - SampleConfirmationList.
	 */
	public static final String SAMPLE_CONFIRMATION_LIST = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sample.smSampleConfirmationList");
	/**
	 * Sample - smRejected.
	 */
	public static final String SAMPLE_CONFIRMATION_LIST_REJECTED = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sample.smSampleConfirmationList.smRejected");
	/**
	 * Sample - Colorway.
	 */
	public static final String SAMPLE_COLORWAY = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sample.vrdColorwayRef");
	/**
	 * Source - Business Supplier.
	 */
	public static final String SOURCE_BUSINESS_SUPPLIER = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.source.businessSupplier");
	/**
	 * SourceToSeason - Factory.
	 */
	public static final String SOURCE_TO_SEASON_LINK_FACTORY = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sourceToSeasonLink.factory");
	/**
	 * Sample - Sample Supplier Status.
	 */
	public static final String SAMPLE_SUPPLIER_STATUS = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sample.smSampleSupplierStatus");
	/**
	 * Sample - Sample Status.
	 */
	public static final String SAMPLE_STATUS = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sample.sampleStatus");
	/**
	 * Sample - Submitted For Review.
	 */
	public static final String SAMPLE_STATUS_SUBMITTED_FOR_REVIEW = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sample.sampleStatus.vrdSubmittedForReview");
	/**
	 * Supplier - Vendor Group.
	 */
	public static final String SUPPLIER_VENDOR_GROUP = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.supplier.vrdVendorGroup");
	/**
	 * LCSProductSeasonLink.
	 */
	public static final String PS_KEY = LCSProperties.get("com.sportmaster.wc.emailutility.processor.psLink.psKey");
	/**
	 * LCSSourceToSeasonLink.
	 */
	public static final String SOURCE_TO_SEASON_KEY = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sourceToSeasonLink.sourceToSeasonKey");
	/**
	 * OSOAppDeveloper.
	 */
	public static final String OSO_APP_DEVELOPER = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sourceToSeasonLink.smOSOAppDeveloper");
	/**
	 * OSOAppCoster.
	 */
	public static final String OSO_APP_COSTER = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sourceToSeasonLink.smOSOAppCoster");
	/**
	 * OsoDeveloper.
	 */
	public static final String OSO_DEVELOPER = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sourceToSeasonLink.smOsoDeveloper");
	/**
	 * OsoCoster.
	 */
	public static final String OSO_COSTER = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sourceToSeasonLink.smOsoCoster");
	/**
	 * Product Type - FPD.
	 */
	public static final String FPD_PRODUCT_TYPE = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.product.fpdType");
	/**
	 * Sample Type - Footwear.
	 */
	public static final String FOOTWEAR_SAMPLE_TYPE = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sample.footwearType");
	/**
	 * Group - Business Supplier.
	 */
	public static final String BUSINESS_SUPPLIER_GROUP = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.group.bSupplier");
	/**
	 * Header Name - Factory.
	 */
	public static final String FACTORY_HEADER_NAME = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.emailHeaderName.factory");		
	/**
	 * Season Requested.
	 */
	public static final String SEASON_REQUESTED = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sampleRequest.vrdSeasonRequested");
	/**
	 * Sample request - size.
	 */
	public static final String SAMPLE_SIZE = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.sampleRequest.size");
	/**
	 * ProductSeason Link - Production Manager user.
	 */
	public static final String PRODUCTION_MANAGER_USER = LCSProperties
			.get("com.sportmaster.wc.emailutility.processor.psLink.pmUser");
	// End Phase 13 - FPD & SEPD Product Sample Workflow

	// Start Phase 13 - FPD Product Sample Workflow
	/**
	 * FPD Product Sample WF Email Notification start time.
	 */
	public static final String FPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_QUEUE_START_TIME = LCSProperties
			.get("com.sportmaster.wc.emailutility.FPDProdSampleWFTaskNotification.scheduleQueueStartTime");

	/**
	 * AM or PM for FPD Product Sample WF Email Notification Queue.
	 */
	public static final String FPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_QUEUE_START_AM = LCSProperties
			.get("com.sportmaster.wc.emailutility.FPDProdSampleWFTaskNotification.AMorPM");

	/**
	 * Schedule Queue Name for FPD Product Sample WF Email Notification.
	 */
	public static final String FPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_QUEUE_NAME = LCSProperties
			.get("com.sportmaster.wc.emailutility.FPDProdSampleWFTaskEmailScheduleQueueName");

	/**
	 * Time Interval from which the tasks have to be sent in notification
	 */
	public static final long FPD_PRODUCT_SAMPLE_WF_EMAIL_SCHEDULE_PICKTASKSFROM_INTERVAL = LCSProperties
			.get("com.sportmaster.wc.emailutility.FPDProdSampleWFTaskNotification.pickTasksFromLastHours", 24);
	// End Phase 13 - FPD Product Sample Workflow
	
	/**
	 * FPD Product Season WF Email Notification start time.
	 */
	public static final String FPD_PS_WF_EMAIL_SCHEDULE_QUEUE_START_TIME = LCSProperties
			.get("com.sportmaster.wc.emailutility.FPDProdSeasWFTaskNotification.scheduleQueueStartTime");

	/**
	 * AM or PM for FPD Product Season WF Email Notification Queue.
	 */
	public static final String FPD_PS_WF_EMAIL_SCHEDULE_QUEUE_START_AM = LCSProperties
			.get("com.sportmaster.wc.emailutility.FPDProdSeasWFTaskNotification.AMorPM");

	/**
	 * Schedule Queue Name for FPD Product Season WF Email Notification.
	 */
	public static final String FPD_PS_WF_EMAIL_SCHEDULE_QUEUE_NAME = LCSProperties
			.get("com.sportmaster.wc.emailutility.FPDProdSeasWFTaskEmailScheduleQueueName");

	/**
	 * Time Interval from which the tasks have to be sent in notification
	 */
	public static final long FPD_PS_WF_EMAIL_SCHEDULE_PICKTASKSFROM_INTERVAL = LCSProperties
			.get("com.sportmaster.wc.emailutility.FPDProdSeasWFTaskNotification.pickTasksFromLastHours", 24);
	
	// Start Phase 14 - SEPD Product Costsheet Workflow
	/**
	 * SEPD Product Costsheet WF Email Notification start time.
	 */
	public static final String SEPD_PRODUCT_COSTSHEET_WF_EMAIL_SCHEDULE_QUEUE_START_TIME = LCSProperties
			.get("com.sportmaster.wc.emailutility.SEPDProdCostsheetWFTaskNotification.scheduleQueueStartTime");

	/**
	 * AM or PM for SEPD Product Costsheet WF Email Notification Queue.
	 */
	public static final String SEPD_PRODUCT_COSTSHEET_WF_EMAIL_SCHEDULE_QUEUE_START_AM = LCSProperties
			.get("com.sportmaster.wc.emailutility.SEPDProdCostsheetWFTaskNotification.AMorPM");

	/**
	 * Schedule Queue Name for SEPD Product Costsheet WF Email Notification.
	 */
	public static final String SEPD_PRODUCT_COSTSHEET_WF_EMAIL_SCHEDULE_QUEUE_NAME = LCSProperties
			.get("com.sportmaster.wc.emailutility.SEPDProdCostsheetWFTaskEmailScheduleQueueName");

	/**
	 * Time Interval from which the tasks have to be sent in notification
	 */
	public static final long SEPD_PRODUCT_COSTSHEET_WF_EMAIL_SCHEDULE_PICKTASKSFROM_INTERVAL = LCSProperties
			.get("com.sportmaster.wc.emailutility.SEPDProdCostsheetWFTaskNotification.pickTasksFromLastHours", 24);
	// End Phase 14 - SEPD Product Costsheet Workflow

}
