package com.burberry.wc.integration.util;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import com.burberry.wc.integration.productapi.bean.*;
import com.burberry.wc.integration.productapi.constant.BurProductConstant;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.foundation.LCSRevisableEntity;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.specification.FlexSpecToSeasonLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.DownloadURLHelper;

/**
 * A Helper class to handle JSON data transform activity for Product API. Class
 * contain several method to handle transform of object data putting it to the
 * beans.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryProductAPIUtil {

	/**
	 * Default Constructor.
	 */
	private BurberryProductAPIUtil() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductAPIUtil.class);

	/**
	 * STR_IMAGE_URL_UNIQUE_ID.
	 */
	private static final String STR_IMAGE_URL_UNIQUE_ID = "imageURLUniqId";

	/**
	 * STR_WT_SERVER.
	 */
	private static final String STR_WT_SERVER = "wt.server.codebase";

	/**
	 * This method is to get Operational value.
	 * 
	 * @param product
	 * @param attKey
	 * @return
	 * @throws WTException
	 */
	public static String getOperationalValue(LCSProduct product, String attKey)
			throws WTException {
		String value = null;
		//JIRA 1347: Defect Fix
		// Check if product is apparel
		if (product.getFlexType().getFullName().contains(BurConstant.Apparel)
				&& !product.getFlexType().getFullName(true).contains(BurConstant.APPAREL_CHILDREN_TYPE)) {
			//JIRA 1347: Defect Fix
			// Defect Fix: JIRA BURBERRY-1289: Start
			value = BurberryDataUtil.getData(product,
					BurConstant.BUR_OPERATIONAL_CATEGORY_APP, null);
			// Defect Fix: JIRA BURBERRY-1289: End
		} else {
			value = BurberryDataUtil.getData(product,
					BurConstant.BUR_OPERATIONAL_CATEGORY, null);
		}
		// Return Statement
		return value;
	}

	/**
	 * This method is to get brand value.
	 * 
	 * @param product
	 * @param attKey
	 * @return
	 * @throws WTException
	 */
	public static String getBrandValue(LCSProduct product, String attKey)
			throws WTException {
		String value = null;
		String methodName = "getBrandValue()";
		// Check if product is apparel children
		if (!product.getFlexType().getFullName(true)
				.contains(BurConstant.APPAREL_CHILDREN_TYPE)
				&& product.getFlexType().getFullName(true)
						.contains(BurConstant.Apparel)) {
			value = BurberryDataUtil.getData(product, attKey, null);
		} else {
			value = BurberryDataUtil.getData(product, BurConstant.VRD_BRAND,
					null);
		}
		logger.debug(methodName + " brand value returned " + value);
		// Return statement
		return value;
	}

	/**
	 * This method is to get material type value.
	 * 
	 * @param materialObj
	 * @param strAttKey
	 * @return
	 * @throws WTException
	 */
	public static Object getTypeValue(LCSMaterial materialObj, String strAttKey)
			throws WTException {
		String value = null;
		String methodName = "getTypeValue()";
		// Check if material is sole type
		if (!materialObj.getFlexType().getFullName(true)
				.contains(BurConstant.SOLE_TYPE)) {
			value = BurberryDataUtil.getData(materialObj, strAttKey, null);
		} else {
			value = BurberryDataUtil.getData(materialObj,
					BurConstant.BUR_MAT_TYPE, null);
		}
		logger.debug(methodName + " Material Type value returned " + value);
		// Return statement
		return value;
	}

	/**
	 * This method is to get specification bean.
	 * 
	 * @param spec
	 * @param specToSeasonLink 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	public static Specification getSpecificationBean(FlexSpecification spec, FlexSpecToSeasonLink specToSeasonLink)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		String methodName = "getSpecificationBean() ";
		logger.debug(methodName + "Extracting data from specification " + spec);
		
		Specification primarySpecBean = new Specification();

		// Generate json mapping from keys
		Map<String, String> jsonSpecificationMapping = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_SPECIFICATION);
		logger.debug(methodName + "jsonSpecificationMapping: "
				+ jsonSpecificationMapping);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonSpecificationMapping = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_SPECIFICATION_SYSTEM_KEY);
		logger.debug(methodName + "systemAttjsonSpecificationMapping: "
				+ systemAttjsonSpecificationMapping);
		
		BurberryAPIBeanUtil.getObjectData(BurConstant.SPECIFICATION_IGNORE,
				primarySpecBean, spec, BurConstant.SPECIFICATION_ATT,
				jsonSpecificationMapping, systemAttjsonSpecificationMapping);
		
		//Get the primary
		if (systemAttjsonSpecificationMapping.containsKey(BurConstant.PRIMARY)) {
			BeanUtils.setProperty(primarySpecBean,
					systemAttjsonSpecificationMapping.get(BurConstant.PRIMARY),
					specToSeasonLink.isPrimarySpec());
		}
		
		if (systemAttjsonSpecificationMapping.containsKey(BurConstant.BRANCHID)) {
			BeanUtils.setProperty(primarySpecBean,
					systemAttjsonSpecificationMapping.get(BurConstant.BRANCHID),
					String.valueOf(spec.getBranchIdentifier()));
		}

				
		// BurberryAPIBeanUtil.validateRequiredAttributes(primarySpecBean,
		// BurConstant.SPECIFICATION_REQ);
		// Return statement
		return primarySpecBean;
	}

	/**
	 * This method is to get source season bean.
	 * 
	 * @param sourceSeason
	 * @param sourceToSeasonLink
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	public static SourceSeason getSourceSeasonBean(LCSSeason sourceSeason,
			LCSSourceToSeasonLink sourceToSeasonLink)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		String methodName = "getSourceSeasonBean() ";
		logger.debug(methodName + "Extracting data from source season "
				+ sourceSeason);
		SourceSeason sourceSeasonBean = new SourceSeason();

		// Generate json mapping from keys
		Map<String, String> jsonSourceSeasonMapping = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_SOURCE_SEASON);
		logger.debug(methodName + "jsonSourceSeasonMapping: "
				+ jsonSourceSeasonMapping);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonSourceSeasonMapping = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_SOURCE_SEASON_SYSTEM_KEY);
		logger.debug(methodName + "systemAttjsonSourceSeasonMapping: "
				+ systemAttjsonSourceSeasonMapping);

		BurberryAPIBeanUtil.getObjectData(BurConstant.SOURCE_SEASON_IGNORE,
				sourceSeasonBean, sourceToSeasonLink,
				BurConstant.SOURCE_SEASON_ATT, jsonSourceSeasonMapping,
				systemAttjsonSourceSeasonMapping);

		if (jsonSourceSeasonMapping.containsKey(BurConstant.SEASON_NAM)) {
			BeanUtils.setProperty(sourceSeasonBean,
					jsonSourceSeasonMapping.get(BurConstant.SEASON_NAM),
					sourceSeason.getName());
		}

		// BurberryAPIBeanUtil.validateRequiredAttributes(sourceSeasonBean,
		// BurConstant.SOURCE_SEASON_REQ);
		// Return Statement
		return sourceSeasonBean;
	}

	/**
	 * This method is to get image page bean.
	 * 
	 * @param imagePageDocument
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	public static Image getImagePageDocBean(LCSDocument imagePageDocument)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		String methodName = "getImagePageDocBean() ";
		logger.debug(methodName + "Extracting data from image page "
				+ imagePageDocument);
		Image imagePage = new Image();
		/*
		 * final StringTokenizer attKeys = new StringTokenizer(
		 * BurConstant.JSON_IMAGE_PAGE, BurConstant.STRING_COMMA); Map<String,
		 * String> jsonImagePageMapping = BurberryAPIUtil
		 * .getJsonMapping(attKeys);
		 */

		// Generate json mapping from keys
		Map<String, String> jsonMappingImagePage = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_IMAGE_PAGE);
		logger.debug(methodName + "jsonMappingImagePage: "
				+ jsonMappingImagePage);

		// Generate json mapping from System keys
		Map<String, String> systemAttjsonMappingImagePage = BurberryAPIUtil
				.getJsonMapping(BurConstant.JSON_IMAGE_PAGE_SYSTEM_KEY);
		logger.debug(methodName + "systemAttjsonMappingImagePage: "
				+ systemAttjsonMappingImagePage);

		BurberryAPIBeanUtil.getObjectData(BurConstant.IMAGE_PAGE_IGNORE,
				imagePage, imagePageDocument, BurConstant.IMAGE_PAGE_ATT,
				jsonMappingImagePage, systemAttjsonMappingImagePage);
		// BurberryAPIBeanUtil.validateRequiredAttributes(imagePage,
		// BurConstant.IMAGE_PAGE_REQ);
		// Return statement
		return imagePage;
	}

	/**
	 * This method is to get Layout Bean.
	 * 
	 * @param imagePageDocument
	 * @param mapTrackedImageFromImagePage
	 * @param associatedUniqueFileId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 */
	public static List<Layout> getLayoutBean(LCSDocument imagePageDocument,
			Map<String, Collection<HashMap>> mapTrackedImageFromImagePage,
			List<String> associatedUniqueFileId) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException {

		String methodName = "getLayoutBean() ";
		logger.debug(methodName + "Extracting data from image page "
				+ imagePageDocument);
		List<Layout> lstLayout = new ArrayList<Layout>();
		logger.debug(methodName + "Image Page Document Name: "
				+ imagePageDocument.getName());
		try {
			logger.debug(methodName + "Handle Primary Contents...");
			// Handle Primary Contents
			LCSDocument imagePageDoc = (LCSDocument) ContentHelper.service
					.getContents(imagePageDocument);
			logger.debug(methodName + "Image Page Document Name: "
					+ imagePageDoc.getName());
			// Get application data from document
			ApplicationData primaryAppData = (ApplicationData) ContentHelper
					.getPrimary(imagePageDoc);

			// Initialisation
			Layout layoutPrimaryBean = new Layout();
			// Check if not null
			if (primaryAppData != null) {
				// Get the URL
				if (!isAiFile(primaryAppData.getFileName())) {
					String strFileUniqId = BurberryAPICriteriaUtil
							.getFileUniqueId(primaryAppData);
					associatedUniqueFileId.add(strFileUniqId);
					BeanUtils.setProperty(layoutPrimaryBean,
							STR_IMAGE_URL_UNIQUE_ID, strFileUniqId);
					// Get the file url
					String fileDocUrl = getStaticURL(primaryAppData,
							imagePageDoc);
					logger.debug(methodName + "File URL: " + fileDocUrl);
					// Set on bean property
					BeanUtils.setProperty(layoutPrimaryBean, "imagePageDocUrl",
							fileDocUrl);
					// Add to list
					lstLayout.add(layoutPrimaryBean);
				}
			}
			logger.debug(methodName + "Handle Secondary Contents...");
			// Handle Secondary Contents
			ApplicationData secondaryAppData = null;
			// Get the list of application data
			List<ApplicationData> appData = ContentHelper
					.getApplicationData(imagePageDoc);
			logger.debug(methodName + "Handle Secondary Contents: " + appData);
			logger.debug(methodName + "Secondary Content Size: "
					+ appData.size());
			// Loop through application data
			for (int i = 0; i < appData.size(); i++) {
				secondaryAppData = (ApplicationData) appData.get(i);
				if (!isAiFile(secondaryAppData.getFileName())) {
					Layout layoutSecondaryBean = new Layout();
					logger.debug(methodName + "Individual AD: "
							+ secondaryAppData);
					// Get the file url
					String fileDocUrl = getStaticURL(secondaryAppData,
							imagePageDoc);
					logger.debug(methodName + "File URL: " + fileDocUrl);
					String strFileUniqId = BurberryAPICriteriaUtil
							.getFileUniqueId(secondaryAppData);
					associatedUniqueFileId.add(strFileUniqId);
					BeanUtils.setProperty(layoutSecondaryBean,
							STR_IMAGE_URL_UNIQUE_ID, strFileUniqId);
					// Set bean property
					BeanUtils.setProperty(layoutSecondaryBean, "imageURL",
							fileDocUrl);
					// Add to list
					lstLayout.add(layoutSecondaryBean);
				}
			}

			// Loop through to get image or reference document
			for (int z = 1; z < 10; z++) {
				// Initialisation
				Layout layoutImageDocBean = new Layout();
				// Handle a referenced Document Object
				if (imagePageDoc.getValue("documentRef" + z) != null) {
					// Get Document object
					LCSDocument refdoc = (LCSDocument) imagePageDoc
							.getValue("documentRef" + z);
					logger.debug(methodName + "Ref Document" + z + ": "
							+ refdoc);
					// Get content
					refdoc = (LCSDocument) ContentHelper.service
							.getContents(refdoc);
					logger.debug(methodName + "Get Doc Contents: " + refdoc);

					// Get application data from document
					ApplicationData primaryDocAppData = (ApplicationData) ContentHelper
							.getPrimary(refdoc);
					String fileDocUrl = getStaticURL(primaryDocAppData, refdoc);
					logger.debug(methodName + "Document Ref File URL: "
							+ fileDocUrl);
					// Set bean property
					BeanUtils.setProperty(layoutImageDocBean, "imageURL",
							fileDocUrl);
					// CR R26: Start

					BeanUtils.setProperty(layoutImageDocBean, STR_IMAGE_URL_UNIQUE_ID,
							String.valueOf(refdoc.getBranchIdentifier()));
					logger.debug(methodName + "imageAppData: "
							+ String.valueOf(refdoc.getBranchIdentifier()));
					associatedUniqueFileId.add(String.valueOf(refdoc
							.getBranchIdentifier()));

					// CR R26: End
					lstLayout.add(layoutImageDocBean);
				}
			}
		} catch (PropertyVetoException ex) {
			ex.printStackTrace();
		}
		logger.debug(methodName + "lstLayout: " + lstLayout.size());
		// Return statement
		return lstLayout;
	}

	/**
	 * @param applicationData
	 * @param document
	 * @return
	 * @throws IOException
	 */
	public static String getStaticURL(ApplicationData applicationData,
			LCSDocument document) throws IOException {

		String methodName = "getStaticURL() ";
		String url = null;
		String codebase = WTProperties.getServerProperties().getProperty(
				STR_WT_SERVER);
		String imageURLStatic = codebase+"/rfa/jsp/main/";

		logger.debug(methodName + "imageURLStatic: " + imageURLStatic);
		if (applicationData != null) {
			url = imageURLStatic
					+ DownloadURLHelper.getReusableAuthenticatedDownloadURL(
							applicationData, document);
		}
		logger.debug(methodName + " url for document : " + url);
		return url;
	}

	/**
	 * @param fileName
	 * @return
	 */
	private static boolean isAiFile(String fileName) {

		boolean aiFile = false;
		String type = fileName.substring(fileName.lastIndexOf('.') + 1);
		if ("ai".equalsIgnoreCase(type)) {
			aiFile = true;
		}
		return aiFile;
	}

	/**
	 * @param productObj
	 * @param sourceObj
	 * @param specObj
	 * @param imagePageObject
	 * @param mapTrackedImageFromImagePage
	 * @param associatedUniqueFileId
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws WTException
	 * @throws PropertyVetoException
	 */
	public static List<Layout> getRemovedLayoutBean(LCSProduct productObj,
			LCSSourcingConfig sourceObj, FlexSpecification specObj,
			LCSDocument imagePageObject,
			Map<String, Collection<HashMap>> mapTrackedImageFromImagePage,
			List<String> associatedUniqueFileId) throws IllegalAccessException,
			InvocationTargetException, WTException, PropertyVetoException {

		String methodName = "getRemovedLayoutBean() ";
		// Track execution time
		long remImageFromImagePageStart = BurberryAPIUtil.printCurrentTime(
				methodName,
				"Remove Image From Image Page Transform Start Time: ");

		// Initialisation
		List<Layout> lstRemovedImageLayoutBean = new ArrayList<Layout>();
		List<String> removedFileList = new ArrayList<String>();

		// Check Product Id
		if (mapTrackedImageFromImagePage.containsKey(String.valueOf(productObj
				.getBranchIdentifier()))) {

			// Get the Collection
			Collection<HashMap> colMap = mapTrackedImageFromImagePage
					.get(String.valueOf(productObj.getBranchIdentifier()));

			// Loop through the collection
			for (HashMap hm : colMap) {

				// Get Source Id
				String strSourceId = String.valueOf(hm.get("SOURCE_ID"));
				logger.debug(methodName + "strSourceId: " + strSourceId);
				logger.debug(methodName + "Object Id:"
						+ String.valueOf(sourceObj.getBranchIdentifier()));

				// Get Specification Id
				String strSpecificationId = String.valueOf(hm
						.get("SPECIFICATION_ID"));
				logger.debug(methodName + "strSpecificationId: "
						+ strSpecificationId);
				logger.debug(methodName + "Object Id:"
						+ String.valueOf(specObj.getBranchIdentifier()));

				// Get Image Page Id
				String strImagePageId = String.valueOf(hm.get("IMAGE_PAGE_ID"));
				logger.debug(methodName + "strImagePageId: " + strImagePageId);
				logger.debug(methodName + "Object Id:"
						+ String.valueOf(imagePageObject.getBranchIdentifier()));

				// Check Source Id and Specification Id
				if (String.valueOf(sourceObj.getBranchIdentifier())
						.equalsIgnoreCase(strSourceId)
						&& String.valueOf(specObj.getBranchIdentifier())
								.equalsIgnoreCase(strSpecificationId)
						&& String
								.valueOf(imagePageObject.getBranchIdentifier())
								.equalsIgnoreCase(strImagePageId)) {
					// Get Image File Name
					String strRemovedFileId = (String) hm.get("UNIQUE_FILE_ID");
					// Add to list
					removedFileList.add(strRemovedFileId);
				}
			}
		}
		// Remove Duplicate Palette Names
		Set<String> hashSetRemoveDuplicate = new HashSet<String>();
		hashSetRemoveDuplicate.addAll(removedFileList);
		removedFileList.clear();
		removedFileList.addAll(hashSetRemoveDuplicate);

		// Compare and Removed / Added Objects
		List<String> lstComparedAndRemovedObjects = BurberryAPIUtil
				.compareAndRemoveSameObjects(associatedUniqueFileId,
						removedFileList);

		logger.debug(methodName + "List Removed Image from Image Page: "
				+ lstComparedAndRemovedObjects);

		// Loop through the complete collection of map criteria
		for (String strRemovedImageFileId : lstComparedAndRemovedObjects) {
			logger.debug("strRemovedImageFileId: " + strRemovedImageFileId);
			// Initialisation
			Layout removedLayoutBean = new Layout();
			logger.debug(methodName + "strRemovedImageFileId: "
					+ strRemovedImageFileId);
			BurberryProductAPIJsonDataUtil.getRemovedImagePageBean(
					removedLayoutBean, STR_IMAGE_URL_UNIQUE_ID, strRemovedImageFileId);
			logger.debug(methodName + "RemovedImagePageBean: "
					+ removedLayoutBean);
			// Add to the list
			lstRemovedImageLayoutBean.add(removedLayoutBean);
		}

		// Track execution time
		long remImagePageEnd = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove Image Page Transform End Time: ");
		logger.debug(methodName
				+ "Remove Image Page Transform  Total Execution Time (ms): "
				+ (remImagePageEnd - remImageFromImagePageStart));
		return lstRemovedImageLayoutBean;

	}
	
	//BURBERRY-1485: Append Price Library Retail : Start
		/**
		 * Method to add proposed retail prices
		 * 
		 * @param spl
		 *            LCSProductSeasonLink
		 * @param productSeasonBean
		 *            ProductSeason bean
		 * @throws WTException
		 * @throws InvocationTargetException
		 * @throws IllegalAccessException
		 * @throws IOException 
		 * @throws NoSuchMethodException 
		 */
		public static void appendProposedRetailPrice(LCSProductSeasonLink spl,
				ProductSeason productSeasonBean) throws WTException,
				IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException {
			// Method Name
			String methodName = "appendProposedRetailPrice() ";
			// Get Initial Price Band
			LCSRevisableEntity priceLibraryObject = (LCSRevisableEntity) spl
					.getValue(BurProductConstant.INITIAL_PRICE_BAND);
			// Check for null
			if (priceLibraryObject != null) {
				logger.debug(methodName+"PriceLibraryObject: "+priceLibraryObject);
				// Generate json mapping from keys
				Map<String, String> jsonMappingPriceLibrary = BurberryAPIUtil
						.getJsonMapping(BurProductConstant.JSON_PRICELIBRARY_ATT);
				logger.debug(methodName + "jsonMappingPriceLibrary: "
						+ jsonMappingPriceLibrary);
				// Generate json mapping from System keys
				Map<String, String> systemAttjsonMappingPriceLibrary = BurberryAPIUtil
						.getJsonMapping(BurProductConstant.JSON_PRICELIBRARY_SYSTEM_ATT);
				logger.debug(methodName + "systemAttjsonMappingPriceLibrary: "
						+ systemAttjsonMappingPriceLibrary);

				BurberryAPIBeanUtil.getObjectData(BurProductConstant.PRICELIBRARY_IGNORE,
						productSeasonBean, priceLibraryObject, BurProductConstant.PRICELIBRARY_ATT,
						jsonMappingPriceLibrary, systemAttjsonMappingPriceLibrary);
			}
		}
		//BURBERRY-1485: Append Price Library Retail : End
}
