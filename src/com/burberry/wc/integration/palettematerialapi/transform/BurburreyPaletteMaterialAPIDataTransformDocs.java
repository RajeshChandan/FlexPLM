package com.burberry.wc.integration.palettematerialapi.transform;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.palettematerialapi.bean.Document;
import com.burberry.wc.integration.palettematerialapi.bean.MaterialSupplierDocument;
import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.burberry.wc.integration.util.BurberryPaletteMaterialAPIJsonDataUtil;
import com.burberry.wc.integration.util.BurberryProductAPIJsonDataUtil;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierMaster;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentQuery;
import com.lcs.wc.util.VersionHelper;

/**
 * A class to handle Transformation activity. Class contain several method to
 * handle transformation activity i.e. Transforming Data from different objects
 * and putting it to the bean.
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurburreyPaletteMaterialAPIDataTransformDocs {

	/**
	 * Default Constructor.
	 */
	private BurburreyPaletteMaterialAPIDataTransformDocs() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurburreyPaletteMaterialAPIDataTransformDocs.class);

	// BURBERRY-1485 New Attributes Additions post Sprint 8: Start
	/**
	 * Method to get Material Supplier Document Bean
	 * 
	 * @param materialObject
	 * 
	 * @param materialSupplierObject
	 *            LCSMaterialSupplier
	 * @return List<MaterialSupplierDocument>
	 * @throws WTException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws PropertyVetoException
	 *             Exception
	 */
	public static List<MaterialSupplierDocument> getListMaterialSupplierDocumentsBean(
			LCSMaterial materialObject,
			LCSMaterialSupplier materialSupplierObject,
			Map<String, Collection<HashMap>> mapTrackedMaterialSupplierDocument)
			throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException,
			PropertyVetoException {

		String methodName = "getListMaterialSupplierDocumentsBean() ";
		// Track execution time
		long docStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Material Supplier Document Transform Start Time: ");

		// Initialisation
		ArrayList<MaterialSupplierDocument> lstMatSupDocumentBean = new ArrayList<MaterialSupplierDocument>();
		// Initialisation
		List<MaterialSupplierDocument> lstRemovedMaterialSupplierDocsBean = new ArrayList<MaterialSupplierDocument>();

		// Initialisation
		List<String> associatedDocumentIds = new ArrayList<String>();

		// Get Material Supplier Master
		LCSMaterialSupplierMaster matSupplierMaster = (LCSMaterialSupplierMaster) materialSupplierObject
				.getMaster();

		// Get collection of documents using Material Supplier object
		Collection<LCSDocument> colDocuments = LCSDocumentQuery
				.getObjectsFromResults(new LCSDocumentQuery()
						.findPartDocReferences(matSupplierMaster),
						BurPaletteMaterialConstant.LCSDOCUMENT_PREFIX,
						BurPaletteMaterialConstant.DOCUMENT_ID);
		logger.debug(methodName + "Material Supplier Document Collection: "
				+ colDocuments.size());

		// Loop through document collection
		for (LCSDocument document : colDocuments) {
			logger.debug(methodName + "Document: " + document.getName());
			document = (LCSDocument) VersionHelper.latestIterationOf(document);
			logger.debug(methodName + "Material Supplier Document: " + document);
			// Get associsted document Ids:
			associatedDocumentIds.add(String.valueOf(document
					.getBranchIdentifier()));
			// Extract Document object data
			MaterialSupplierDocument matSupDocumentBean = new MaterialSupplierDocument();
			BurberryProductAPIJsonDataUtil.getDocumentBean(document,
					matSupDocumentBean, null);
			logger.debug(methodName + "Material Supplier Document Bean: "
					+ matSupDocumentBean);
			// Set document bean to list
			lstMatSupDocumentBean.add(matSupDocumentBean);
		}

		// Get removed and compared document Ids
		List<String> lstComparedAndRemovedObjects = BurberryPaletteMaterialAPIDataTransform
				.getRemovedMaterialSupplierDocumentIds(
						mapTrackedMaterialSupplierDocument, String
								.valueOf(materialObject.getBranchIdentifier()),
						String.valueOf(materialSupplierObject
								.getBranchIdentifier()), associatedDocumentIds);
		logger.debug(methodName
				+ "List of Removed Material Supplier Documents: "
				+ lstComparedAndRemovedObjects);

		// Loop through the complete collection of map criteria
		for (String strRemovedDocumentId : lstComparedAndRemovedObjects) {
			// Initialisation
			MaterialSupplierDocument removedDocumentBean = new MaterialSupplierDocument();
			BurberryPaletteMaterialAPIJsonDataUtil
					.getRemovedMOABean(
							removedDocumentBean,
							BurPaletteMaterialConstant.MATERIAL_DOCUMENT_JSON_UNIQUE_ID,
							strRemovedDocumentId);
			logger.debug(methodName + "RemovedDocumentBean: "
					+ removedDocumentBean);
			// Add to the list
			lstRemovedMaterialSupplierDocsBean.add(removedDocumentBean);
		}
		lstMatSupDocumentBean.addAll(lstRemovedMaterialSupplierDocsBean);

		// Track execution time
		long docEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Material Supplier Document Transform End Time: ");
		logger.debug(methodName
				+ "Material Supplier Document Transform  Total Execution Time (ms): "
				+ (docEndTime - docStartTime));
		// Return Statement
		return lstMatSupDocumentBean;
	}

	// BURBERRY-1485 New Attributes Additions post Sprint 8: End

	/**
	 * Method to get list of document bean data.
	 * 
	 * @param materialObject
	 *            material object
	 * @param mapTrackedMaterialDocument
	 * @return List
	 * @throws WTException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws PropertyVetoException
	 */
	public static List<Document> getListDocumentsBean(
			LCSMaterial materialObject,
			Map<String, Collection<HashMap>> mapTrackedMaterialDocument)
			throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException,
			PropertyVetoException {

		String methodName = "getDocumentsBean() ";
		// Track execution time
		long docStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Document Transform Start Time: ");

		// Initialisation
		ArrayList<Document> listDocumentBean = new ArrayList<Document>();
		// Initialisation
		List<Document> lstRemovedMaterialDocsBean = new ArrayList<Document>();

		// Get collection of documents using material object
		Collection<LCSDocument> colDocuments = LCSDocumentQuery
				.getObjectsFromResults(new LCSDocumentQuery()
						.findPartDocReferences(materialObject),
						BurPaletteMaterialConstant.LCSDOCUMENT_PREFIX,
						BurPaletteMaterialConstant.DOCUMENT_ID);
		logger.debug(methodName + "Document Collection: " + colDocuments);
		// CR R26: Handle Remove Object Customisation:
		List<String> associatedDocumentIds = new ArrayList<String>();

		// Loop through document collection
		for (LCSDocument document : colDocuments) {
			logger.debug(methodName + "Document: " + document.getName());
			document = (LCSDocument) VersionHelper.latestIterationOf(document);
			logger.debug(methodName + "Document: " + document);
			// CR R26: Handle Remove Object Customisation:
			associatedDocumentIds.add(String.valueOf(document
					.getBranchIdentifier()));
			// Extract Document object data
			Document documentBean = new Document();
			BurberryProductAPIJsonDataUtil.getDocumentBean(document,
					documentBean, null);
			logger.debug(methodName + "Document Bean: " + documentBean);
			// Set document bean to list
			listDocumentBean.add(documentBean);
		}
		// BURBERRY-1485 New Attributes Additions post Sprint 8: Start
		// CR R26: Handle Remove Object Customisation: Start
		List<String> lstComparedAndRemovedObjects = BurberryPaletteMaterialAPIDataTransform
				.getRemovedMaterialDocumentIds(mapTrackedMaterialDocument,
						String.valueOf(materialObject.getBranchIdentifier()),
						associatedDocumentIds);
		logger.debug(methodName + "List of Removed Material Documents: "
				+ lstComparedAndRemovedObjects);

		// Loop through the complete collection of map criteria
		for (String strRemovedDocumentId : lstComparedAndRemovedObjects) {
			// Initialisation
			Document removedDocumentBean = new Document();
			BurberryPaletteMaterialAPIJsonDataUtil
					.getRemovedMOABean(
							removedDocumentBean,
							BurPaletteMaterialConstant.MATERIAL_DOCUMENT_JSON_UNIQUE_ID,
							strRemovedDocumentId);
			logger.debug(methodName + "RemovedDocumentBean: "
					+ removedDocumentBean);
			// Add to the list
			lstRemovedMaterialDocsBean.add(removedDocumentBean);
		}
		listDocumentBean.addAll(lstRemovedMaterialDocsBean);
		// CR R26: Handle Remove Object Customisation: End
		// BURBERRY-1485 New Attributes Additions post Sprint 8: End

		// Track execution time
		long docEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Document Transform End Time: ");
		logger.debug(methodName
				+ "Document Transform  Total Execution Time (ms): "
				+ (docEndTime - docStartTime));
		// Return Statement
		return listDocumentBean;
	}

}
