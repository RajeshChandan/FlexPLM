package com.burberry.wc.integration.util;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.util.WTException;
import wt.util.WTProperties;

import com.burberry.wc.integration.sampleapi.bean.*;
import com.burberry.wc.integration.sampleapi.constant.BurSampleConstant;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleRequest;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;

/**
 * A Helper class to handle Extraction activity. Class contain several method to
 * handle Extraction activity i.e. Extracting Data from different objects and
 * putting it to the bean
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberrySampleAPIJsonDataUtil {

	/**
	 * Private constructor.
	 */
	private BurberrySampleAPIJsonDataUtil() {

	}

	/**
	 * logger.
	 */
	public static final Logger logger = Logger
			.getLogger(BurberrySampleAPIJsonDataUtil.class);

	/**
	 * STR_WT_SERVER.
	 */
	private static final String STR_WT_SERVER = "wt.server.codebase";

	/**
	 * Method to Fetch Product Sample Request bean.
	 * 
	 * @param LCSSampleRequest
	 * @return ProductSample bean
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 */

	public static ProductSampleRequest getProductSampleRequestBean(
			LCSSampleRequest sampleRequest) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException {

		// Method Name
		String methodName = "getProductSampleRequestBean() ";
		logger.debug(methodName + "Extracting Data from Sample Request: "
				+ sampleRequest.getName());

		// Initialisation
		ProductSampleRequest prodSampleRequestBean = new ProductSampleRequest();

		// Get JSON mapping from keys
		Map<String, String> jsonMappingProdSampleRequest = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_PRODUCT_SAMPLE_REQUEST);
		logger.debug(methodName + "jsonMappingProdSampleRequest: "
				+ jsonMappingProdSampleRequest);

		// Get JSON mapping from System keys
		Map<String, String> systemAttjsonMappingPrdSamReq = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_SYSTEM_PRODUCT_SAMPLE_REQUEST_KEY);
		logger.debug(methodName + "systemAttjsonMappingPrdSamReq: "
				+ systemAttjsonMappingPrdSamReq);
		
		// BURBERRY-1348: Start
		if (systemAttjsonMappingPrdSamReq.containsKey(BurConstant.STR_IDA2A2)) {
			BeanUtils.setProperty(prodSampleRequestBean,
				systemAttjsonMappingPrdSamReq.get(BurConstant.STR_IDA2A2),
				FormatHelper.getNumericObjectIdFromObject(sampleRequest));
		}
		// BURBERRY-1348: End

		// Using JSON mapping attribute key and JSON key get data and set on
		// bean
		BurberryAPIBeanUtil.getObjectData(
				BurSampleConstant.PRODUCT_SAMPLE_REQUEST_IGNORE,
				prodSampleRequestBean, sampleRequest,
				BurSampleConstant.PRODUCT_SAMPLE_REQUEST_ATT,
				jsonMappingProdSampleRequest, systemAttjsonMappingPrdSamReq);
		logger.debug(methodName + "ProductSampleRequest Bean: "
				+ prodSampleRequestBean);

		// Validate Required Attributes
		//BurberryAPIBeanUtil.validateRequiredAttributes(prodSampleRequestBean,
		//		BurSampleConstant.PRODUCT_SAMPLE_REQUEST_REQ);

		// Return
		return prodSampleRequestBean;
	}

	/**
	 * Method to Fetch Product Sample Bean.
	 * 
	 * @param LCSSample
	 * @return ProductSample bean
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 */

	public static ProdSample getProductSampleBean(LCSSample productSample)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {

		// Method Name
		String methodName = "getProductSampleBean() ";
		logger.debug(methodName + "Extracting Data from Sample: "
				+ productSample.getName());

		// Initialisation
		ProdSample prodSampleBean = new ProdSample();

		// Get JSON mapping from keys
		Map<String, String> jsonMappingProdSample = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_PRODUCT_SAMPLE);
		logger.debug(methodName + "jsonMappingProdSampleRequest: "
				+ jsonMappingProdSample);

		// Get JSON mapping from System keys
		Map<String, String> systemAttjsonMappingPrdSam = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_SYSTEM_PRODUCT_SAMPLE_KEY);
		logger.debug(methodName + "systemAttjsonMappingPrdSam: "
				+ systemAttjsonMappingPrdSam);

		// Using JSON mapping attribute key and JSON key get data and set on
		// bean
		BurberryAPIBeanUtil.getObjectData(
				BurSampleConstant.PRODUCT_SAMPLE_IGNORE, prodSampleBean,
				productSample, BurSampleConstant.PRODUCT_SAMPLE_ATT,
				jsonMappingProdSample, systemAttjsonMappingPrdSam);

		// Get Sample Name
		if (jsonMappingProdSample.containsKey(BurSampleConstant.SAMPLE_NAME)) {
			BeanUtils.setProperty(prodSampleBean,
					jsonMappingProdSample.get(BurSampleConstant.SAMPLE_NAME),
					productSample.getName());
		}

		// Get Colourway Reference
		/*if (jsonMappingProdSample
				.containsKey(BurSampleConstant.SAMPLE_COLOURWAY_REF)) {
			LCSSKU sampleColourway = null;
			// Get Colourway Reference from Sample
			sampleColourway = (LCSSKU) productSample
					.getValue(BurSampleConstant.SAMPLE_COLOURWAY_REF);
			logger.debug(methodName + "sampleColourway: " + sampleColourway);
			if (sampleColourway != null) {
				logger.debug("sampleColourway: " + sampleColourway);
				BeanUtils.setProperty(prodSampleBean, jsonMappingProdSample
						.get(BurSampleConstant.SAMPLE_COLOURWAY_REF),
						sampleColourway.getValue(BurConstant.SKUNAME));
			}
		}*/
		// Get Sample State
		if (jsonMappingProdSample.containsKey(BurSampleConstant.SAMPLE_STATE)) {
			BeanUtils.setProperty(prodSampleBean,
					jsonMappingProdSample.get(BurSampleConstant.SAMPLE_STATE),
					productSample.getLifeCycleState().getFullDisplay());
		}

		logger.debug(methodName + "Prod Sample Bean: " + prodSampleBean);

		// Validate Required Attributes
		//BurberryAPIBeanUtil.validateRequiredAttributes(prodSampleBean,
			//	BurSampleConstant.PRODUCT_SAMPLE_REQ);

		// Return
		return prodSampleBean;
	}

	/**
	 * Method to Fetch Material Sample Request Bean.
	 * 
	 * @param LCSSampleRequest
	 * @return MaterialSample bean
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 */

	public static MaterialSampleRequest getMaterialSampleRequestBean(
			LCSSampleRequest materialSampleRequest)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {

		// Method Name
		String methodName = "getMaterialSampleRequestBean() ";
		logger.debug(methodName + "Extracting Data from Sample Request: "
				+ materialSampleRequest.getName());

		// Initialisation
		MaterialSampleRequest materialSampleRequestBean = new MaterialSampleRequest();

		// Get JSON mapping from keys
		Map<String, String> jsonMappingMatSampleRequest = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_MATERIAL_SAMPLE_REQUEST);
		logger.debug(methodName + "jsonMappingMatSampleRequest: "
				+ jsonMappingMatSampleRequest);

		// Get JSON mapping from System keys
		Map<String, String> systemAttjsonMappingMatSamReq = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_SYSTEM_MATERIAL_SAMPLE_REQUEST_KEY);
		logger.debug(methodName + "systemAttjsonMappingMatSamReq: "
				+ systemAttjsonMappingMatSamReq);
		
		// BURBERRY-1348: Start
		if (systemAttjsonMappingMatSamReq.containsKey(BurConstant.STR_IDA2A2)) {
				BeanUtils.setProperty(materialSampleRequestBean,
				systemAttjsonMappingMatSamReq.get(BurConstant.STR_IDA2A2),
				FormatHelper.getNumericObjectIdFromObject(materialSampleRequest));
		}
		// BURBERRY-1348: End

		// Using JSON mapping attribute key and JSON key get data and set on
		// bean
		BurberryAPIBeanUtil.getObjectData(
				BurSampleConstant.MATERIAL_SAMPLE_REQUEST_IGNORE,
				materialSampleRequestBean, materialSampleRequest,
				BurSampleConstant.MATERIAL_SAMPLE_REQUEST_ATT,
				jsonMappingMatSampleRequest, systemAttjsonMappingMatSamReq);

		logger.debug(methodName + "Material Sample Request Bean: "
				+ materialSampleRequestBean);

		// Validate Required Attributes
		//BurberryAPIBeanUtil.validateRequiredAttributes(
			//	materialSampleRequestBean,
			//	BurSampleConstant.MATERIAL_SAMPLE_REQUEST_REQ);

		// Return
		return materialSampleRequestBean;
	}

	/**
	 * Method to Fetch Material Sample Bean.
	 * 
	 * @param LCSSample
	 * @return MaterialSample bean
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 */

	public static MatSample getMaterialSampleBean(LCSSample materialSample)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {

		// Method Name
		String methodName = "getMaterialSampleBean() ";
		logger.debug(methodName + "Extracting Data from Sample: "
				+ materialSample.getName());

		// Initialisation
		MatSample materialSampleBean = new MatSample();

		// Get JSON mapping from keys
		Map<String, String> jsonMappingMatSample = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_MATERIAL_SAMPLE);
		logger.debug(methodName + "jsonMappingMatSample: "
				+ jsonMappingMatSample);

		// Get JSON mapping from System keys
		Map<String, String> systemAttjsonMappingMatSam = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_SYSTEM_MATERIAL_SAMPLE_KEY);
		logger.debug(methodName + "systemAttjsonMappingMatSam: "
				+ systemAttjsonMappingMatSam);

		// Using JSON mapping attribute key and JSON key get data and set on
		// bean
		BurberryAPIBeanUtil.getObjectData(
				BurSampleConstant.MATERIAL_SAMPLE_IGNORE, materialSampleBean,
				materialSample, BurSampleConstant.MATERIAL_SAMPLE_ATT,
				jsonMappingMatSample, systemAttjsonMappingMatSam);

		// Get Sample Name
		if (jsonMappingMatSample.containsKey(BurSampleConstant.SAMPLE_NAME)) {
			BeanUtils.setProperty(materialSampleBean,
					jsonMappingMatSample.get(BurSampleConstant.SAMPLE_NAME),
					materialSample.getName());
		}

		// Get Sample State
		// if (jsonMappingMatSample.containsKey(BurSampleConstant.SAMPLE_STATE))
		// {
		// BeanUtils.setProperty(materialSampleBean,
		// / jsonMappingMatSample.get(BurSampleConstant.SAMPLE_STATE),
		// materialSample.getLifeCycleState().getFullDisplay());
		// }

		logger.debug(methodName + "Material Sample Bean: " + materialSampleBean);

		// Validate Required Attributes
		//BurberryAPIBeanUtil.validateRequiredAttributes(materialSampleBean,
			//	BurSampleConstant.MATERIAL_SAMPLE_REQ);

		// Return
		return materialSampleBean;
	}

	/**
	 * Method to Fetch Product Bean Data.
	 * 
	 * @param LCSProduct
	 * @return Product bean
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 */

	public static Product getProductBean(LCSProduct productObj)
			throws WTException, IllegalAccessException,
			InvocationTargetException, IOException, NoSuchMethodException {

		// Method Name
		String methodName = "getProductBean() ";
		logger.debug(methodName + "Extracting Data from Product: "
				+ productObj.getName());

		// Initialisation
		Product productBean = new Product();

		// Get JSOON mapping from keys
		Map<String, String> jsonMappingProduct = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_PRODUCT_KEY);
		logger.debug(methodName + "jsonMappingProduct: " + jsonMappingProduct);

		// Get JSON mapping from System keys
		Map<String, String> systemAttjsonMappingProduct = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_SYSTEM_PRODUCT_KEY);
		logger.debug(methodName + "systemAttjsonMappingProduct: "
				+ systemAttjsonMappingProduct);

		// Using JSON mapping attribute key and JSON key get data and set on
		// bean
		BurberryAPIBeanUtil.getObjectData(BurSampleConstant.PRODUCT_IGNORE,
				productBean, productObj, BurSampleConstant.PRODUCT_ATT,
				jsonMappingProduct, systemAttjsonMappingProduct);

		// Get Product Name
		if (jsonMappingProduct.containsKey(BurConstant.NAME)) {
			BeanUtils.setProperty(productBean,
					jsonMappingProduct.get(BurConstant.NAME),
					productObj.getName());
		}

		logger.debug(methodName + "Product Bean: " + productBean);

		// Validate Required Attributes
		//BurberryAPIBeanUtil.validateRequiredAttributes(productBean,
			//	BurSampleConstant.PRODUCT_REQ);

		// Return
		return productBean;
	}

	/**
	 * Method to Fetch Source Supplier Bean Data.
	 * 
	 * @param supplierObject
	 *            LCSSupplier
	 * @param sourceBean
	 *            bean
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
	public static void getSourceSupplierBean(LCSSupplier supplierObject,
			Object sourceBean) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException {

		// Method Name
		String methodName = "getSourceSupplierBean() ";
		logger.debug(methodName + "Extracting Data from Source: "
				+ supplierObject.getName());

		// Get JSON mapping from keys
		Map<String, String> jsonMappingSource = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_SOURCE_SUPPLIER);
		logger.debug(methodName + "jsonMappingSource: " + jsonMappingSource);

		// Get JSON mapping from System keys
		Map<String, String> systemAttjsonMappingSource = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_SYSTEM_SOURCE_SUPPLIER_KEY);
		logger.debug(methodName + "systemAttjsonMappingSource: "
				+ systemAttjsonMappingSource);

		// Using JSON mapping attribute key and JSON key get data and set on
		// bean
		BurberryAPIBeanUtil.getObjectData(
				BurSampleConstant.SOURCE_SUPPLIER_IGNORE, sourceBean,
				supplierObject, BurSampleConstant.SOURCE_SUPPLIER_ATT,
				jsonMappingSource, systemAttjsonMappingSource);
		logger.debug(methodName + "Source Supplier Bean: " + sourceBean);

		// Validate Required Attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(sourceBean,
		// BurSampleConstant.SOURCE_SUPPLIER_REQ);
	}
	
	/**
	 * Method to Fetch Source Supplier Bean Data.
	 * 
	 * @param supplierObject
	 *            LCSSupplier
	 * @param sourceBean
	 *            bean
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
	public static void getSourceBean(LCSSourcingConfig sourceObject,
			Object sourceBean) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException {

		// Method Name
		String methodName = "getSourceSupplierBean() ";
		logger.debug(methodName + "Extracting Data from Source: "
				+ sourceObject.getName());

		// Get JSON mapping from keys
		Map<String, String> jsonMappingSource = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_SOURCE_ATT);
		logger.debug(methodName + "jsonMappingSource: " + jsonMappingSource);

		// Get JSON mapping from System keys
		Map<String, String> systemAttjsonMappingSource = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_SOURCE_SYSTEM_ATT);
		logger.debug(methodName + "systemAttjsonMappingSource: "
				+ systemAttjsonMappingSource);

		// Using JSON mapping attribute key and JSON key get data and set on
		// bean
		BurberryAPIBeanUtil.getObjectData(
				BurSampleConstant.SOURCE_IGNORE, sourceBean,
				sourceObject, BurSampleConstant.SOURCE_ATT,
				jsonMappingSource, systemAttjsonMappingSource);
		logger.debug(methodName + "Source Supplier Bean: " + sourceBean);

		if(jsonMappingSource.containsKey(BurSampleConstant.PRIMARY_SOURCE)){
			BeanUtils.setProperty(sourceBean, jsonMappingSource
					.get(BurSampleConstant.PRIMARY_SOURCE),
					sourceObject.isPrimarySource());
		}
		
		// BURBERRY-1485 New Attributes Additions post Sprint 8: Start
		if (systemAttjsonMappingSource.containsKey(BurConstant.BRANCHID)) {
			BeanUtils.setProperty(sourceBean,
					systemAttjsonMappingSource.get(BurConstant.BRANCHID),
					sourceObject.getBranchIdentifier());
		}
		// BURBERRY-1485 New Attributes Additions post Sprint 8: End
				
		// Validate Required Attributes
		// BurberryAPIBeanUtil.validateRequiredAttributes(sourceBean,
		// BurSampleConstant.SOURCE_SUPPLIER_REQ);
	}

	/**
	 * Method to Fetch Material Bean Data.
	 * 
	 * @param materialObject
	 *            LCSMaterial
	 * @return Material Bean
	 * @throws WTException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 */

	public static Material getMaterialBean(LCSMaterial materialObject)
			throws WTException, IllegalAccessException,
			InvocationTargetException, IOException, NoSuchMethodException {

		// Method Name
		String methodName = "getMaterialBean() ";
		logger.debug(methodName + "Extracting Data from Material: "
				+ materialObject.getName());

		// Initialisation
		Material materialBean = new Material();

		// Get JSON mapping from keys
		Map<String, String> jsonMappingMaterial = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_MATERIAL_ATT);
		logger.debug(methodName + "jsonMappingMaterial: " + jsonMappingMaterial);

		// Get JSON mapping from System keys
		Map<String, String> systemAttjsonMappingMaterial = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_SYSTEM_MATERIAL_KEY);
		logger.debug(methodName + "systemAttjsonMappingMaterial: "
				+ systemAttjsonMappingMaterial);

		// Using JSON mapping attribute key and JSON key get data and set on
		// bean
		BurberryAPIBeanUtil.getObjectData(BurSampleConstant.MATERIAL_IGNORE,
				materialBean, materialObject, BurSampleConstant.MATERIAL_ATT,
				jsonMappingMaterial, systemAttjsonMappingMaterial);

		logger.debug(methodName + "Material Bean: " + materialBean);

		// Validate required attributes
		//BurberryAPIBeanUtil.validateRequiredAttributes(materialBean,
			//	BurSampleConstant.MATERIAL_REQ);

		// Return Statement
		return materialBean;
	}

	/**
	 * Method to Fetch Material Colour Bean Data.
	 * 
	 * @param colour
	 *            LCSMaterialColor
	 * @return Material Colour
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
	public static Colour getMaterialColourBean(LCSColor colour)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException {

		// Method Name
		String methodName = "getMaterialColourBean() ";
		logger.debug(methodName + "Extracting Data from Color: "
				+ colour.getName());

		// Initialisation
		Colour colorBean = new Colour();

		// Get JSON mapping from keys
		Map<String, String> jsonMappingMatColour = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_MATERIAL_COLOUR_ATT);
		logger.debug(methodName + "jsonMappingMatColour: "
				+ jsonMappingMatColour);

		// Get JSON mapping from System keys
		Map<String, String> systemAttjsonMappingMatColour = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_SYSTEM_MATERIAL_COLOUR_KEY);
		logger.debug(methodName + "systemAttjsonMappingMatColour: "
				+ systemAttjsonMappingMatColour);

		// Using JSON mapping attribute key and JSON key get data and set on
		// bean
		BurberryAPIBeanUtil.getObjectData(
				BurSampleConstant.MATERIAL_COLOUR_IGNORE, colorBean, colour,
				BurSampleConstant.MATERIAL_COLOUR_ATT, jsonMappingMatColour,
				systemAttjsonMappingMatColour);

		logger.debug(methodName + "Material Colour: " + colorBean);

		// Validate required attributes
		//BurberryAPIBeanUtil.validateRequiredAttributes(colorBean,
			//	BurSampleConstant.MATERIAL_COLOUR_REQ);

		// Return Statement
		return colorBean;
	}

	/**
	 * Method to Fetch Document Bean Data.
	 * 
	 * @param docObject
	 *            LCSDocument
	 * @param documentBean
	 *            Object
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
	 * @throws PropertyVetoException 
	 */
	public static void getDocumentBean(LCSDocument docObject,
			Object documentBean) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException, PropertyVetoException {

		// Method Name
		String methodName = "getDocumentBean() ";
		logger.debug(methodName + "Extracting Data from Document: "
				+ docObject.getName());

		// Setting Code Base Path
		String codebase = WTProperties.getServerProperties().getProperty(
				STR_WT_SERVER);
		codebase = codebase.substring(0, codebase.lastIndexOf('/'));

		// Get JSON mapping from keys
		Map<String, String> jsonMappingDocument = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_DOCUMENT_ATT);
		logger.debug(methodName + "jsonMappingDocument: " + jsonMappingDocument);

		// Get JSON mapping from System keys
		Map<String, String> systemAttjsonMappingDocument = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_DOCUMENT_SYSTEM_ATT);
		logger.debug(methodName + "systemAttjsonMappingDocument: "
				+ systemAttjsonMappingDocument);

		// Using JSON mapping attribute key and JSON key get data and set on
		// bean
		BurberryAPIBeanUtil.getObjectData(BurSampleConstant.DOCUMENT_IGNORE,
				documentBean, docObject, BurSampleConstant.DOCUMENT_ATT,
				jsonMappingDocument, systemAttjsonMappingDocument);

		// Get Document Thumb nail Location
		if (jsonMappingDocument.containsKey(BurConstant.THUMBNAIL_LOCATION)) {
			if (FormatHelper.hasContent(docObject.getThumbnailLocation())) {
				BeanUtils
						.setProperty(documentBean, jsonMappingDocument
								.get(BurConstant.THUMBNAIL_LOCATION),
								(codebase + FormatHelper.formatImageUrl(docObject.getThumbnailLocation())));
			}
		}
		// Get Document Type
		if (jsonMappingDocument.containsKey(BurConstant.DOCUMENT_TYPE)) {
			BeanUtils.setProperty(documentBean,
					jsonMappingDocument.get(BurConstant.DOCUMENT_TYPE),
					(docObject.getFlexType().getFullNameDisplay()));
		}
		// Get primary content URL location
		if (jsonMappingDocument.containsKey(BurConstant.PRIMARY_CONTENT_URL)) {
			LCSDocument refdocument = (LCSDocument) ContentHelper.service
					.getContents(docObject);
			// Get application data from document
			ApplicationData primaryAppData = (ApplicationData) ContentHelper
					.getPrimary(refdocument);
			BeanUtils.setProperty(documentBean, jsonMappingDocument
					.get(BurConstant.PRIMARY_CONTENT_URL),
					BurberryProductAPIUtil.getStaticURL(primaryAppData,
							refdocument));
		}

		// CR-R32: Start
		// Get Document Branch Iteration Id
		if (jsonMappingDocument.containsKey(BurConstant.BRANCHID)) {
			BeanUtils.setProperty(documentBean,
					jsonMappingDocument.get(BurConstant.BRANCHID),
					docObject.getBranchIdentifier());
		}
		// CR-R32: End

		logger.debug(methodName + "Document Bean: " + documentBean);

		// Validate required attributes
		//BurberryAPIBeanUtil.validateRequiredAttributes(documentBean,
				//BurSampleConstant.DOCUMENT_REQ);

	}

	/**
	 * Method to Fetch Product Season Bean Data.
	 * 
	 * @param productSeasonLink
	 *            LCSProductSeasonLink
	 * @param sourceSeason
	 *            LCSSeason
	 * @return ProductSeason Bean
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws WTException
	 *             Exception
	 * @throws IOException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 */
	public static ProductSeason getProductSeasonBean(
			LCSProductSeasonLink productSeasonLink, LCSSeason sourceSeason)
			throws IllegalAccessException, InvocationTargetException,
			WTException, IOException, NoSuchMethodException {

		// Method Name
		String methodName = "getProductSeasonBean() ";
		logger.debug(methodName + "Extracting Data from ProductSeason Link: "
				+ productSeasonLink);

		// Initialisation
		ProductSeason prodSeasonBean = new ProductSeason();

		// Get JSON mapping from keys
		Map<String, String> jsonMappingProductSeason = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_PRODUCT_SEASON_KEY);
		logger.debug(methodName + "jsonMappingProductSeason: "
				+ jsonMappingProductSeason);

		// Get JSON mapping from System keys
		Map<String, String> systemAttjsonMappingProductSeason = BurberryAPIUtil
				.getJsonMapping(BurSampleConstant.JSON_SYSTEM_PRODUCT_SEASON_KEY);
		logger.debug(methodName + "systemAttjsonMappingProductSeason: "
				+ systemAttjsonMappingProductSeason);

		// Using JSON mapping attribute key and JSON key get data and set on
		// bean
		BurberryAPIBeanUtil.getObjectData(
				BurSampleConstant.PRODUCT_SEASON_IGNORE, prodSeasonBean,
				productSeasonLink, BurSampleConstant.PRODUCT_SEASON_ATT,
				jsonMappingProductSeason, systemAttjsonMappingProductSeason);

		// Get Season Name
		if (jsonMappingProductSeason
				.containsKey(BurSampleConstant.PRODUCT_SEASON_NAME)) {
			BeanUtils.setProperty(prodSeasonBean, jsonMappingProductSeason
					.get(BurSampleConstant.PRODUCT_SEASON_NAME), sourceSeason
					.getName());
		}

		logger.debug(methodName + "ProdSeasonBean: " + prodSeasonBean);

		// Validate required attributes
		//BurberryAPIBeanUtil.validateRequiredAttributes(prodSeasonBean,
			//	BurSampleConstant.PRODUCT_SEASON_REQ);

		// Return
		return prodSeasonBean;
	}

}
