package com.burberry.wc.integration.productapi.transform;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.palettematerialapi.transform.BurberryPaletteMaterialAPIDataTransform;
import com.burberry.wc.integration.productapi.bean.*;
import com.burberry.wc.integration.util.*;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.*;
import com.lcs.wc.sourcing.*;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecToSeasonLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;

/**
 * A Helper class to handle Transformation activity. Class contain several
 * method to handle Extraction activity i.e. Extracting Data from different
 * objects and putting it to the bean
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryProductAPIDataTransform {

	/**
	 * BurberryProductAPIDataExtraction.
	 */
	private BurberryProductAPIDataTransform() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger.getLogger(BurberryProductAPIDataTransform.class);
	
	
	/**
	 * STR_MOA_OBJECT_ID.
	 */
	private static final String STR_MOA_OBJECT_ID = "MOA_OBJECT_ID";
	
	/**
	 * STR_MOA_OWNER_ID.
	 */
	private static final String STR_MOA_OWNER_ID = "OWNER_ID";

	/**
	 * Method to get the List of Source Season Bean Data.
	 * 
	 * @param productObj                   LCSProduct
	 * @param source                       LCSSourcingConfig
	 * @param colProdToSeasonIds           Collection
	 * @param colProdToSeasonLinkIds       Collection
	 * @param deltaCriteria                boolean
	 * @param mapTrackedImagePage
	 * @param mapTrackedImageFromImagePage
	 * @param mapDeltaDateTime
	 * @return List
	 * @throws WTException               Exception
	 * @throws IllegalAccessException    Exception
	 * @throws InvocationTargetException Exception
	 * @throws NoSuchMethodException     Exception
	 * @throws IOException               Exception
	 * @throws PropertyVetoException
	 */
	public static List<SourceSeason> getListSourceSeason(LCSProduct productObj, LCSSourcingConfig source,
			Collection<String> colProdToSeasonIds, Collection<String> colProdToSeasonLinkIds, boolean deltaCriteria,
			Map<String, Collection<HashMap>> mapTrackedImagePage,
			Map<String, Collection<HashMap>> mapTrackedImageFromImagePage,
			Map<String, Collection<HashMap>> mapTrackedProdSpec, Map<String, Collection<HashMap>> mapTrackedDocument)
			throws WTException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException,
			PropertyVetoException {

		String methodName = "getListSourceSeason() ";
		long scSeasonStartTime = BurberryAPIUtil.printCurrentTime(methodName, "scSeasonStartTime: ");

		// Get the collection of source to season links using source master
		Collection<LCSSourceToSeasonLink> colSourceToSeasonLink = (new LCSSourcingConfigQuery())
				.getSourceToSeasonLinks((LCSSourcingConfigMaster) source.getMaster());
		logger.debug(methodName + "Collection of Source Season Links: " + colSourceToSeasonLink);

		// Initialisation
		List<SourceSeason> lstSourceSeason = new ArrayList<SourceSeason>();

		// Loop through each Source to Season Link
		for (LCSSourceToSeasonLink sourceToSeasonLink : colSourceToSeasonLink) {
			logger.debug(methodName + "SourceToSeasonLink: " + sourceToSeasonLink);
			// Check if latest iteration
			if (sourceToSeasonLink.isLatestIteration()) {
				// Get the season from source-season link
				LCSSeason sourceSeason = (LCSSeason) VersionHelper
						.latestIterationOf(sourceToSeasonLink.getSeasonMaster());
				logger.debug(methodName + "sourceSeason: " + sourceSeason);
				// Using product and season get product-season link
				LCSProductSeasonLink productSeasonLink = (LCSProductSeasonLink) LCSSeasonQuery
						.findSeasonProductLink(productObj, sourceSeason);
				logger.debug(methodName + "productSeasonLink: " + productSeasonLink);
				// Check if exists source season based on map filter
				boolean isValidSourceSeason = checkValidSourceSeason(colProdToSeasonLinkIds, colProdToSeasonIds,
						productSeasonLink, deltaCriteria);
				logger.debug(methodName + "isValidSourceSeason: " + isValidSourceSeason);
				// checking through each product season object under
				// product
				if (isValidSourceSeason) {
					// Get the source season bean data
					SourceSeason sourceSeasonBean = BurberryProductAPIUtil.getSourceSeasonBean(sourceSeason,
							sourceToSeasonLink);
					logger.debug(methodName + "sourceSeasonBean: " + sourceSeasonBean);
					// Get specification using source, season and product object
					// FlexSpecification spec = BurberryAPIUtil
					// .getPrimarySpecification(source,
					// sourceSeason.getMaster(), productObj);
					Collection<FlexSpecToSeasonLink> colSpecToSeasonLinks = BurberryAPIUtil
							.getAllSpecToSeasonLinks(source, sourceSeason.getMaster(), productObj);
					logger.debug(methodName + "Col Specification: " + colSpecToSeasonLinks);
					List<Specification> lstSpecification = new ArrayList<Specification>();
					for (FlexSpecToSeasonLink specToSeasonLink : colSpecToSeasonLinks) {
						lstSpecification.add(getSpecificationBean(productObj, source, specToSeasonLink,
								mapTrackedImagePage, mapTrackedImageFromImagePage, mapTrackedDocument));
					}
					// Get primary spec bean data and set on source season bean.

					// CR R26: Handle Removed Specification Customisation :
					// START
					lstSpecification.addAll(
							getListRemovedSpecificationBean(productObj, source, sourceSeason, mapTrackedProdSpec));
					// CR R26: Handle Removed Specification Customisation : END
					sourceSeasonBean.setSpecification(lstSpecification);
					logger.debug(methodName + "Source Season bean: " + sourceSeasonBean);
					lstSourceSeason.add(sourceSeasonBean);
				}
			}
		}
		logger.debug(methodName + "List of Source Season Beans: " + lstSourceSeason);

		long scSeasonEndTime = BurberryAPIUtil.printCurrentTime(methodName, "scSeasonEndTime: ");
		logger.debug(methodName + "Source Season Transform  Total Execution Time (ms): "
				+ (scSeasonEndTime - scSeasonStartTime));

		return lstSourceSeason;
		// CR: Add Specification: End
	}

	/**
	 * Method to check if valid source season object.
	 * 
	 * @param colProdToSeasonLinkIds Collection
	 * @param colProdToSeasonIds     Collection
	 * @param productSeasonLink      LCSProductSeasonLink
	 * @param deltaCriteria          boolean
	 * @return boolean
	 */
	private static boolean checkValidSourceSeason(Collection<String> colProdToSeasonLinkIds,
			Collection<String> colProdToSeasonIds, LCSProductSeasonLink productSeasonLink, boolean deltaCriteria) {
		String methodName = "checkValidSourceSeason() ";
		// checking through each product season object under
		// product
		if (productSeasonLink.isEffectLatest() && ((!productSeasonLink.isSeasonRemoved()) || deltaCriteria)) {
			// checking if product season criteria is given in
			// URL and
			// validating if product season object satisfies the
			// URL
			// criteria
			boolean productSeasonExists = BurberryAPIDBUtil.checkIfObjectExists(
					FormatHelper.getNumericObjectIdFromObject(productSeasonLink), colProdToSeasonLinkIds);
			logger.debug(methodName + "productSeasonExists: " + productSeasonExists);
			// checking if season criteria is given in URL and
			// validating if
			// season object satisfies the URL criteria
			boolean checkIfSeasonIdExist = BurberryAPIDBUtil.checkIfObjectExists(
					String.valueOf(((Double) productSeasonLink.getSeasonRevId()).intValue()), colProdToSeasonIds);
			logger.debug(methodName + "checkIfSeasonIdExist: " + checkIfSeasonIdExist);

			return (productSeasonExists && checkIfSeasonIdExist);
		}
		return false;
	}

	/**
	 * Method to get primary spec.
	 * 
	 * @param productObj                   LCSProduct
	 * @param source                       LCSSourcingConfig
	 * @param specToSeasonLink             FlexSpecification
	 * @param mapTrackedImagePage
	 * @param mapTrackedImageFromImagePage
	 * @param startDate                    Date
	 * @param endDate                      date
	 * @return PrimarySpec
	 * @throws IllegalAccessException    Exception
	 * @throws InvocationTargetException Exception
	 * @throws NoSuchMethodException     Exception
	 * @throws WTException               Exception
	 * @throws IOException               Exception
	 * @throws PropertyVetoException
	 */
	private static Specification getSpecificationBean(LCSProduct productObj, LCSSourcingConfig source,
			FlexSpecToSeasonLink specToSeasonLink, Map<String, Collection<HashMap>> mapTrackedImagePage,
			Map<String, Collection<HashMap>> mapTrackedImageFromImagePage,
			Map<String, Collection<HashMap>> mapTrackedDocument) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException, IOException, PropertyVetoException {
		String methodName = "getSpecificationBean() ";
		// Initialisation
		Specification primarySpecBean = new Specification();

		// Check if Spec is not null
		if (specToSeasonLink != null) {
			// Get Spec
			FlexSpecification specObject = (FlexSpecification) VersionHelper
					.latestIterationOf(specToSeasonLink.getSpecificationMaster());
			String season=specToSeasonLink.getSeasonMaster().getName();
			// Get Primary Spec Bean.
			primarySpecBean = BurberryProductAPIUtil.getSpecificationBean(specObject, specToSeasonLink);
			logger.debug(methodName + "Primary Specification Bean: " + primarySpecBean);
			// CR R26: Handle Remove Object Customisation : Start
			// Get the list of image pages bean and set on spec bean.
			// primarySpecBean.setImages(getListImagePages(colImagesPage));
			List<Image> lstImagePages = getListImagePages(productObj, source, specObject, mapTrackedImagePage,
					mapTrackedImageFromImagePage);
			logger.debug(methodName + "List of Associated Image Pages: " + lstImagePages);
			primarySpecBean.setImages(lstImagePages);
			// CR R26: Handle Remove Object Customisation : End

			// CR RD 75 : Document node on Specification : Start
			List<SpecDocument> lstSpecDocs = getSpecDocuments(productObj, specObject, mapTrackedDocument,season);
			logger.info(methodName + "List of Associated Documents: " + lstSpecDocs);
			primarySpecBean.setSpecDocuments(lstSpecDocs);
			// CR RD 75 : Document node on Specification : End

		}
		logger.debug(methodName + "primarySpecBean: " + primarySpecBean);
		return primarySpecBean;
	}

	/**
	 * @param productObj
	 * @param source
	 * @param specObject
	 * @param mapTrackedDocument
	 * @param season 
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws WTException
	 * @throws IOException
	 * @throws PropertyVetoException
	 */
	private static List<SpecDocument> getSpecDocuments(LCSProduct productObj,
			FlexSpecification specObject, Map<String, Collection<HashMap>> mapTrackedDocument, String season)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, WTException, IOException,
			PropertyVetoException {
		String methodName = "getSpecDocuments() ";

		// Initialisation
		List<SpecDocument> lstSpecDocs = new ArrayList<SpecDocument>();
		List<String> associatedDocuments = new ArrayList<String>();
		// Initialisation
		List<SpecDocument> lstRemovedMaterialDocsBean = new ArrayList<SpecDocument>();
		// Get collection of Documents using spec.
		Collection<LCSDocument> colDocuments = LCSDocumentQuery.getObjectsFromResults(
				new LCSDocumentQuery().findPartDocReferences(specObject), BurPaletteMaterialConstant.LCSDOCUMENT_PREFIX,
				BurPaletteMaterialConstant.DOCUMENT_ID);

		logger.info(methodName + "colDocuments: " + colDocuments);

		// Loop through image page document
		// collection
		for (LCSDocument document : colDocuments) {
			document = (LCSDocument) VersionHelper.latestIterationOf(document);
			logger.info(methodName + "Spec Document : " + document.getName());
			associatedDocuments.add(String.valueOf(document.getBranchIdentifier()));
			// Extract image page Document object
			// data
			SpecDocument specDocBean = new SpecDocument();
			
			BurberryProductAPIJsonDataUtil.getDocumentBean(document, specDocBean, season);
			
			logger.info(methodName + "SpecDoc Bean: " + specDocBean);

			// Set Image Page Doc bean to list
			lstSpecDocs.add(specDocBean);
		}
		// BURBERRY-1485 New Attributes Additions post Sprint 8: Start
		// CR R26: Handle Remove Object Customisation: Start
		List<String> lstComparedAndRemovedObjects = BurberryProductAPIDataTransform
				.getRemovedSpecDocumentIds(mapTrackedDocument, String.valueOf(productObj.getBranchIdentifier()),
						associatedDocuments);
		logger.info(methodName + "List of Removed Spec Documents: " + lstComparedAndRemovedObjects);

		// Loop through the complete collection of map criteria
		for (String strRemovedDocumentId : lstComparedAndRemovedObjects) {
			// Initialisation
			SpecDocument removedDocumentBean = new SpecDocument();
			BurberryPaletteMaterialAPIJsonDataUtil.getRemovedMOABean(removedDocumentBean,
					BurPaletteMaterialConstant.MATERIAL_DOCUMENT_JSON_UNIQUE_ID, strRemovedDocumentId);
			logger.info(methodName + "RemovedDocumentBean: " + removedDocumentBean);
			// Add to the list
			lstRemovedMaterialDocsBean.add(removedDocumentBean);
		}
		lstSpecDocs.addAll(lstRemovedMaterialDocsBean);
		logger.info(methodName + "List of specDocs: " + lstSpecDocs);
		return lstSpecDocs;
	}

	/**
	 * @param mapTrackedDocument
	 * @param productId
	 * @param associatedDocuments
	 * @return
	 */
	private static List<String> getRemovedSpecDocumentIds(Map<String, Collection<HashMap>> mapTrackedDocument,
			String productId, List<String> associatedDocuments) {
		
			//Method Name
			String methodName="getRemovedSpecDocumentIds() ";
			// Initialisation
			List<String> removedDocumentIdList = new ArrayList<String>();
			// Check tracked map contains material id
			if (mapTrackedDocument.containsKey(String.valueOf(productId))) {
				// Get the collection for this material
				Collection<HashMap> colMap = mapTrackedDocument.get(String
						.valueOf(productId));
				logger.info(" Documents Map >> "+colMap);
				// Loop through the collection
				for (HashMap hm : colMap) {
					// Get the removed document id
					String removedDocumentId = (String) hm.get(STR_MOA_OBJECT_ID);
					// Get Material Supplier Id
					String strSpecId = String.valueOf(hm.get(STR_MOA_OWNER_ID));
					// Check Material Id
					if (FormatHelper.hasContent(strSpecId)) {				
						// Add to list
						removedDocumentIdList.add(removedDocumentId);
					}
				}
			}
			// Remove Duplicate Document Id
			Set<String> hashSetRemoveDuplicate = new HashSet<String>();
			hashSetRemoveDuplicate.addAll(removedDocumentIdList);
			removedDocumentIdList.clear();
			removedDocumentIdList.addAll(hashSetRemoveDuplicate);
			logger.info("associatedDocuments"+associatedDocuments);
			// Compare and Removed / Added Objects
			List<String> lstComparedAndRemovedObjects = BurberryAPIUtil
					.compareAndRemoveSameObjects(associatedDocuments,
							removedDocumentIdList);
			logger.info(methodName + "List Removed Docuement Ids: "
					+ lstComparedAndRemovedObjects);

			return lstComparedAndRemovedObjects;
		
		
	}

	/**
	 * Method to get list of image bean.
	 * 
	 * @param spec
	 * @param source
	 * @param productObj
	 * @param mapTrackedImagePage
	 * @param mapTrackedImageFromImagePage
	 * 
	 * @param colImagesPage                Collection
	 * @param endModifyDate
	 * @param startModifyDate
	 * @return List
	 * @throws WTException               Exception
	 * @throws IllegalAccessException    Exception
	 * @throws InvocationTargetException Exception
	 * @throws NoSuchMethodException     Exception
	 * @throws IOException               Exception
	 * @throws PropertyVetoException
	 */

	private static List<Image> getListImagePages(LCSProduct productObj, LCSSourcingConfig source,
			FlexSpecification spec, Map<String, Collection<HashMap>> mapTrackedImagePage,
			Map<String, Collection<HashMap>> mapTrackedImageFromImagePage) throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException, PropertyVetoException {

		String methodName = "getListImagePages() ";

		// Initialisation
		List<Image> lstImage = new ArrayList<Image>();
		List<String> associatedImagePages = new ArrayList<String>();

		// Get collection of Image Pages using product, source and spec.
		Collection<LCSDocument> colImagesPage = LCSDocumentQuery.getObjectsFromResults(
				LCSDocumentQuery
						.runDirectQuery(FlexSpecQuery.getImagePageSpecToComponentLinkQuery(productObj, source, spec)),
				BurPaletteMaterialConstant.LCSDOCUMENT_PREFIX, BurPaletteMaterialConstant.DOCUMENT_ID);
		logger.debug(methodName + "colDocuments: " + colImagesPage);

		// Loop through image page document
		// collection
		for (LCSDocument imagePageDocument : colImagesPage) {
			imagePageDocument = (LCSDocument) VersionHelper.latestIterationOf(imagePageDocument);
			logger.debug(methodName + "Image Page: " + imagePageDocument.getName());
			associatedImagePages.add(imagePageDocument.getName());
			// Extract image page Document object
			// data
			Image imageDocBean = BurberryProductAPIUtil.getImagePageDocBean(imagePageDocument);
			logger.debug(methodName + "Image Bean: " + imageDocBean);

			// CR R26: Start
			List<String> associatedUniqueFileId = new ArrayList<String>();
			// CR R26: End

			// Get the list of Layout Bean
			List<Layout> lstLayout = BurberryProductAPIUtil.getLayoutBean(imagePageDocument,
					mapTrackedImageFromImagePage, associatedUniqueFileId);

			// CR R26: Start
			List<Layout> lstRemovedLayout = BurberryProductAPIUtil.getRemovedLayoutBean(productObj, source, spec,
					imagePageDocument, mapTrackedImageFromImagePage, associatedUniqueFileId);
			lstLayout.addAll(lstRemovedLayout);
			// CR R26: End
			logger.debug(methodName + "List of Layout: " + lstLayout);
			// Set on Image Doc Bean
			imageDocBean.setLayout(lstLayout);
			logger.debug(methodName + "imageDocBean: " + imageDocBean);
			// Set Image Page Doc bean to list
			lstImage.add(imageDocBean);
		}

		// CR R26: Handle Remove Image Page Customisation : Start
		List<Image> lstRemovedImagePages = getRemovedListImagePages(productObj, source, spec, associatedImagePages,
				mapTrackedImagePage);
		logger.debug(methodName + "List of Removed Image Pages: " + lstRemovedImagePages);
		lstImage.addAll(lstRemovedImagePages);
		// CR R26: Handle Remove Image Page Customisation : End

		logger.debug(methodName + "List of Images: " + lstImage);
		return lstImage;
	}

	/**
	 * Method to get removed image pages list
	 * 
	 * @param productObj           LCSProduct
	 * @param source               LCSSourcingConfig
	 * @param spec                 FlexSpecification
	 * @param associatedImagePages List<String>
	 * @param mapTrackedImagePage
	 * @param startModifyDate      Date
	 * @param endModifyDate        Date
	 * @return List<Image>
	 * @throws WTException               Exception
	 * @throws InvocationTargetException Exception
	 * @throws IllegalAccessException    Exception
	 */
	// CR R26: Handle Remove Image Page Customisation : Start
	private static List<Image> getRemovedListImagePages(LCSProduct productObj, LCSSourcingConfig sourceObj,
			FlexSpecification specObj, List<String> associatedImagePages,
			Map<String, Collection<HashMap>> mapTrackedImagePage)
			throws WTException, IllegalAccessException, InvocationTargetException {

		String methodName = "getListRemovedImagePages() ";
		// Track execution time
		long remImagePageStart = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove Image Page Transform Start Time: ");

		// Initialisation
		List<Image> lstRemovedImagePageBean = new ArrayList<Image>();
		List<String> removedImagePageList = new ArrayList<String>();

		// Check Product Id
		if (mapTrackedImagePage.containsKey(String.valueOf(productObj.getBranchIdentifier()))) {

			// Get the Collection
			Collection<HashMap> colMap = mapTrackedImagePage.get(String.valueOf(productObj.getBranchIdentifier()));

			// Loop through the collection
			for (HashMap hm : colMap) {
				// Get Source Id
				String strSourceId = String.valueOf(hm.get("SOURCE_ID"));
				// Get Specification Id
				String strSpecificationId = String.valueOf(hm.get("SPECIFICATION_ID"));

				// Check Source Id and Specification Id
				if (String.valueOf(sourceObj.getBranchIdentifier()).equalsIgnoreCase(strSourceId)
						&& String.valueOf(specObj.getBranchIdentifier()).equalsIgnoreCase(strSpecificationId)) {
					// Get Image Page Name
					String strRemovedImagePageName = (String) hm.get("IMAGE_PAGE_NAME");
					// Add to list
					removedImagePageList.add(strRemovedImagePageName);
				}
			}
		}

		// Remove Duplicate Palette Names
		Set<String> hashSetRemoveDuplicate = new HashSet<String>();
		hashSetRemoveDuplicate.addAll(removedImagePageList);
		removedImagePageList.clear();
		removedImagePageList.addAll(hashSetRemoveDuplicate);

		// Compare and Removed / Added Objects
		List<String> lstComparedAndRemovedObjects = BurberryAPIUtil.compareAndRemoveSameObjects(associatedImagePages,
				removedImagePageList);
		logger.debug(methodName + "List Removed Palette Names: " + lstComparedAndRemovedObjects);

		// Loop through the complete collection of map criteria
		for (String strRemovedImagePageName : lstComparedAndRemovedObjects) {
			logger.debug(methodName + "strRemovedImagePageName: " + strRemovedImagePageName);
			// Initialisation
			Image removedImagePageBean = new Image();
			logger.debug(methodName + "strRemovedImagePageName: " + strRemovedImagePageName);
			BurberryProductAPIJsonDataUtil.getRemovedImagePageBean(removedImagePageBean, "imageName",
					strRemovedImagePageName);
			logger.debug(methodName + "RemovedImagePageBean: " + removedImagePageBean);
			// Add to the list
			lstRemovedImagePageBean.add(removedImagePageBean);
		}

		// Track execution time
		long remImagePageEnd = BurberryAPIUtil.printCurrentTime(methodName, "Remove Image Page Transform End Time: ");
		logger.debug(methodName + "Remove Image Page Transform  Total Execution Time (ms): "
				+ (remImagePageEnd - remImagePageStart));
		return lstRemovedImagePageBean;

	}

	// CR R26: Handle Remove Image Page Customisation : End

	/**
	 * Method to get list of document bean data.
	 * 
	 * @param docSeasonName
	 * 
	 * @param materialObject material object
	 * @return List
	 * @throws WTException               Exception
	 * @throws IllegalAccessException    Exception
	 * @throws InvocationTargetException Exception
	 * @throws NoSuchMethodException     Exception
	 * @throws IOException               Exception
	 * @throws PropertyVetoException
	 */
	public static List<Document> getListDocumentsBean(LCSProduct productObject, String docSeasonName)
			throws WTException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException,
			PropertyVetoException {

		String methodName = "getDocumentsBean() ";
		// Track execution time
		long docStartTime = BurberryAPIUtil.printCurrentTime(methodName, "Document Transform Start Time: ");

		// Initialisation
		ArrayList<Document> listDocumentBean = new ArrayList<Document>();

		// Get collection of documents using material object
		Collection<LCSDocument> colDocuments = LCSDocumentQuery.getObjectsFromResults(
				new LCSDocumentQuery().findPartDocReferences(productObject),
				BurPaletteMaterialConstant.LCSDOCUMENT_PREFIX, BurPaletteMaterialConstant.DOCUMENT_ID);
		logger.debug(methodName + "colDocuments: " + colDocuments);

		// Loop through document collection
		for (LCSDocument document : colDocuments) {
			logger.debug(methodName + "Document: " + document.getName());
			document = (LCSDocument) VersionHelper.latestIterationOf(document);
			logger.debug(methodName + "Document: " + document);
			// Extract Document object data
			Document documentBean = new Document();
			BurberryProductAPIJsonDataUtil.getDocumentBean(document, documentBean, docSeasonName);

			logger.debug(methodName + "Document Bean: " + documentBean);
			// Set document bean to list
			listDocumentBean.add(documentBean);
		}
		// Track execution time
		long docEndTime = BurberryAPIUtil.printCurrentTime(methodName, "Document Transform End Time: ");
		logger.debug(methodName + "Document Transform  Total Execution Time (ms): " + (docEndTime - docStartTime));
		// Return Statement
		return listDocumentBean;
	}

	// CR R26: Handle Removed Specification Customisation : START
	/**
	 * Method to get Removed Specification bean.
	 * 
	 * @param product
	 * @param source
	 * @param season
	 * @param mapTrackedSpec
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static List<Specification> getListRemovedSpecificationBean(LCSProduct product, LCSSourcingConfig source,
			LCSSeason season, Map<String, Collection<HashMap>> mapTrackedSpec)
			throws IllegalAccessException, InvocationTargetException {
		// TODO Auto-generated method stub

		logger.debug("Removed Specification Name for SOURCE:: START ");
		String methodName = "getListRemovedSpecificationBean() ";

		// Track execution time
		long remSpecStart = BurberryAPIUtil.printCurrentTime(methodName, "Remove Specification Transform Start Time: ");
		logger.debug(
				methodName + " " + product.getName() + " ------Removed Specification DATA----- : " + mapTrackedSpec);
		// Initialisation
		List<Specification> lstRemovedSpecBean = new ArrayList<Specification>();
		List<String> removedSpecList = getRemovedSpecificationFromMap(product, source, season, mapTrackedSpec);

		// Loop through the complete collection of map criteria
		for (String strRemovedSpecificationName : removedSpecList) {
			logger.debug(methodName + "strRemovedSpecName: " + strRemovedSpecificationName);
			// Initialisation
			Specification removedSpecBean = new Specification();
			logger.debug(methodName + "strRemovedSpecName: " + strRemovedSpecificationName);
			BurberryProductAPIJsonDataUtil.getRemovedImagePageBean(removedSpecBean, "specName",
					strRemovedSpecificationName);
			logger.debug(methodName + "RemovedBOMBean: " + removedSpecBean);
			// Add to the list
			lstRemovedSpecBean.add(removedSpecBean);
		}

		// Track execution time
		long remSpecificationEnd = BurberryAPIUtil.printCurrentTime(methodName,
				"Remove Specification Transform End Time: ");
		logger.debug(methodName + "Remove Specificaiton Transform  Total Execution Time (ms): "
				+ (remSpecificationEnd - remSpecStart));

		logger.debug(methodName + " ------Removed Specification List-----\n" + lstRemovedSpecBean);
		return lstRemovedSpecBean;
	}

	// CR R26: Handle Remove Specification Customisation : END
	/**
	 * Get Removed Spec Name from Map.
	 * 
	 * @param product
	 * @param source
	 * @param season
	 * @param mapTrackedSpec
	 * @return
	 */
	public static List<String> getRemovedSpecificationFromMap(LCSProduct product, LCSSourcingConfig source,
			LCSSeason season, Map<String, Collection<HashMap>> mapTrackedSpec) {
		List<String> removedSpecList = new ArrayList<String>();

		// Check Product Id
		if (mapTrackedSpec.containsKey(String.valueOf(product.getBranchIdentifier()))) {

			// Get the Collection
			Collection<HashMap> colMap = mapTrackedSpec.get(String.valueOf(product.getBranchIdentifier()));
			// Loop through the collection
			for (HashMap hm : colMap) {
				// Get Source Id
				String strSourceId = String.valueOf(hm.get("SOURCE_ID"));
				// Get Season Id
				String strSeasonId = String.valueOf(hm.get("SEASON_ID"));
				
				//////////////// L2 Change ///////////////////////
				String strSpecId = String.valueOf(hm.get("SPEC_ID"));
				logger.debug("Spec ID  >>>>>>>>>   "+strSpecId);
				///////////////  L2 Change ///////////////////////
				
				// Check Source Id and Specification Id
				if (FormatHelper.hasContent(strSourceId)
						&& strSourceId.equalsIgnoreCase(String.valueOf(source.getBranchIdentifier()))
						&& FormatHelper.hasContent(strSeasonId)
						&& strSeasonId.equalsIgnoreCase(String.valueOf(season.getBranchIdentifier()))) {
					// Get Image Page Name
					String strRemovedSpecName = (String) hm.get("SPECIFICATION_NAME");
					
					//////////////// L2 Change ///////////////////////////
					logger.debug("Spec Name  --------->>>   "+strRemovedSpecName);
					String strRemovedSpecID = (String) hm.get("SPEC_ID");
					logger.debug("Spec ID    --------->>>   "+strRemovedSpecID);
					//////////////// L2 Change //////////////////////////
					
					// Add to list
					//removedSpecList.add(strRemovedSpecName);
					removedSpecList.add(strRemovedSpecID);			
				}
			}
		}
		return removedSpecList;
	}

}
