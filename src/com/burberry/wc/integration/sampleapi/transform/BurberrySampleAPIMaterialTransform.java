package com.burberry.wc.integration.sampleapi.transform;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.sampleapi.bean.*;
import com.burberry.wc.integration.sampleapi.constant.BurSampleConstant;
import com.burberry.wc.integration.util.BurberryAPIDBUtil;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.burberry.wc.integration.util.BurberrySampleAPIJsonDataUtil;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentQuery;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.*;
import com.lcs.wc.sample.*;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;

public final class BurberrySampleAPIMaterialTransform {

	/**
	 * BurberrySampleAPIMaterialTransform.
	 */
	private BurberrySampleAPIMaterialTransform() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberrySampleAPIMaterialTransform.class);

	/**
	 * This method is used to get Material Bean Data.
	 * 
	 * @param materialObject
	 *            LCSMaterial
	 * @return Material Bean
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 */
	public static Material getMaterialBean(LCSMaterial materialObject)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {
		// Method Name
		String methodName = "getMaterialBean() ";
		// Track execution time
		long matTransformStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Material Transform Start Time: ");

		// Get Material Bean Data using Material Object
		logger.debug(methodName + "Extracting Data from Material: "
				+ materialObject.getName());

		Material materialBean = BurberrySampleAPIJsonDataUtil
				.getMaterialBean(materialObject);
		logger.debug(methodName + "Material Bean: " + materialBean);

		// Method End Time
		long matTransformEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Material Transform End Time: ");
		logger.debug(methodName
				+ "Material Transform  Total Execution Time (ms): "
				+ (matTransformEndTime - matTransformStartTime));

		return materialBean;
	}

	/**
	 * This method is used to get Material Source Bean Data.
	 * 
	 * @param sampleRequestObj
	 *            LCSSampleRequest
	 * @return MatSource Bean
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
	 */
	public static MatSource getMaterialSourceBean(
			LCSSampleRequest sampleRequestObj) throws WTException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, IOException {

		// Method Name
		String methodName = "getMaterialSourceBean() ";

		// Track execution time
		long matSourceTransformStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Material Supplier Transform Start Time: ");

		// Get Material Supplier Object
		LCSMaterialSupplier materialSupplier = (LCSMaterialSupplier) VersionHelper
				.latestIterationOf((LCSMaterialSupplierMaster) sampleRequestObj
						.getSourcingMaster());
		logger.debug(methodName + "Material Supplier: "
				+ materialSupplier.getName());

		// Initialisation
		MatSource materialSourceBean = new MatSource();

		// Get Supplier Object
		LCSSupplier supplierObject = (LCSSupplier) VersionHelper
				.latestIterationOf(materialSupplier.getSupplierMaster());
		
		// Check if not null
		if (supplierObject != null) {
			logger.debug(methodName + "Extracting Data from Supplier: "
					+ supplierObject.getName());
			// Get Mat Source Bean Data
			BurberrySampleAPIJsonDataUtil.getSourceSupplierBean(supplierObject,
					materialSourceBean);
			logger.debug(methodName + "Material Source Bean: "
					+ materialSourceBean);
		}

		// Method End Time
		long matSourceTransformEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Material Supplier Transform End Time: ");
		logger.debug(methodName
				+ "Material Supplier Transform  Total Execution Time (ms): "
				+ (matSourceTransformEndTime - matSourceTransformStartTime));
		// Return
		return materialSourceBean;
	}

	/**
	 * This method is used to get Material Sample Bean Data.
	 * 
	 * @param sampleRequestObj
	 *            LCSSampleRequest
	 * @param materialSampleRequestBean
	 *            Bean Data
	 * @param colMapFilterSamples
	 *            Collection<String>
	 * @return List<MatSample>
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
	public static List<MatSample> getListMaterialSampleBean(
			LCSSampleRequest sampleRequestObj,
			MaterialSampleRequest materialSampleRequestBean,
			Collection<String> colMapFilterSamples) throws WTException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, IOException, PropertyVetoException {

		// Method Name
		String methodName = "getListMaterialSampleBean() ";

		// Track execution time
		long matSampleTransformStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Material Sample Transform Start Time: ");

		// Initialisation
		List<MatSample> colMaterialSampleBean = new ArrayList<MatSample>();

		// Get the collection of Samples from Sample Request Object
		Collection<LCSSample> colSamples = LCSQuery.getObjectsFromResults(
				(new LCSSampleQuery().findSamplesIdForSampleRequest(
						sampleRequestObj, false)),
				BurSampleConstant.LCSSAMPLE_PREFIX,
				BurSampleConstant.LCSSAMPLE_ID);
		logger.debug(methodName + "Collection of Samples: " + colSamples);

		// Loop through each Source to Season Link
		for (LCSSample sampleObject : colSamples) {
			logger.debug(methodName + "sampleObject: " + sampleObject);

			// checking if colourway or colourway season provide in URL
			// criteria
			boolean sampleIdExists = BurberryAPIDBUtil.checkIfObjectExists(
					FormatHelper.getNumericObjectIdFromObject(sampleObject),
					colMapFilterSamples);
			logger.debug(methodName + "sampleIdExists: " + sampleIdExists);
			// if exists
			if (sampleIdExists) {
				// Get Material Sample Bean Data using Sample Object
				logger.debug(methodName + "Extracting Data from Sample: "
						+ sampleObject.getName());
				MatSample materialSampleBean = BurberrySampleAPIJsonDataUtil
						.getMaterialSampleBean(sampleObject);
				logger.debug(methodName + "MaterialSampleBean: "
						+ materialSampleBean);
				colMaterialSampleBean.add(materialSampleBean);
				// Get Material Colour Object using Sample Object
				LCSMaterialColor materialColorObject = (LCSMaterialColor) sampleObject
						.getColor();
				// Check if Material Colour Object is not null
				if (materialColorObject != null) {
					logger.debug(methodName + "materialColorObject: "
							+ materialColorObject);
					// Get Colour Object from Material Colour
					LCSColor colour = materialColorObject.getColor();
					logger.debug(methodName + "Extracting Data from Color: "
							+ colour.getColorName());
					// Get Colour Bean Data using Colour Object
					Colour colourBean = BurberrySampleAPIJsonDataUtil
							.getMaterialColourBean(colour);
					logger.debug(methodName + "colourBean: " + colourBean);
					// Set on Material Sample Request Bean Object
					materialSampleRequestBean.setColour(colourBean);
				}
				// Get Material Sample Documents Bean Data using Sample Object
				List<MatDocument> colMaterialSampleDocsBean = getListMaterialSampleDocument(sampleObject);
				// Set Material Sample Documents
				materialSampleBean.setMatDocuments(colMaterialSampleDocsBean);
			}
		}

		logger.debug(methodName + "Material Sample Bean:"
				+ colMaterialSampleBean);

		// Method End Time
		long matSampleTransformEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Material Sample Transform End Time: ");
		logger.debug(methodName
				+ "Material Sample Transform  Total Execution Time (ms): "
				+ (matSampleTransformEndTime - matSampleTransformStartTime));

		// Return
		return colMaterialSampleBean;

	}

	/**
	 * This method is used to get Material Sample Document Bean Data.
	 * 
	 * @param sampleObject
	 *            LCSSample
	 * @return List<MatDocument>
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

	private static List<MatDocument> getListMaterialSampleDocument(
			LCSSample sampleObject) throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException, PropertyVetoException {

		// Method Name
		String methodName = "getListMaterialSampleDocument() ";

		// Track execution time
		long matSampleDocsTransformStartTime = BurberryAPIUtil
				.printCurrentTime(methodName,
						"Material Sample Docs Transform Start Time: ");

		// Initialisation
		List<MatDocument> colMaterialSampleDocsBean = new ArrayList<MatDocument>();

		// Get collection of Documents associated to Sample Object
		Collection<LCSDocument> colDocuments = LCSQuery.getObjectsFromResults(
				new LCSDocumentQuery().findPartDocReferences(sampleObject),
				BurPaletteMaterialConstant.LCSDOCUMENT_PREFIX,
				BurPaletteMaterialConstant.DOCUMENT_ID);
		logger.debug(methodName + "Collection of Documents: " + colDocuments);

		// Loop through document collection
		for (LCSDocument docObject : colDocuments) {
			// Get the latest version of document
			docObject = (LCSDocument) VersionHelper
					.latestIterationOf(docObject);
			logger.debug(methodName + "Extracting Data from Document: "
					+ docObject.getName());
			// Get Material Sample Document Bean
			MatDocument documentBean = new MatDocument();
			BurberrySampleAPIJsonDataUtil.getDocumentBean(docObject,
					documentBean);
			logger.debug(methodName + "Document Bean: " + documentBean);
			// Set document bean to list
			colMaterialSampleDocsBean.add(documentBean);
		}

		logger.debug(methodName + "Material Sample Document Bean:"
				+ colMaterialSampleDocsBean);

		// Method End Time
		long matSampleDocsTransformEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Material Sample Docs Transform End Time: ");
		logger.debug(methodName
				+ "Material Sample Transform Docs Total Execution Time (ms): "
				+ (matSampleDocsTransformEndTime - matSampleDocsTransformStartTime));

		// Return
		return colMaterialSampleDocsBean;
	}
}
