package com.burberry.wc.integration.sampleapi.transform;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.sampleapi.bean.*;
import com.burberry.wc.integration.sampleapi.constant.BurSampleConstant;
import com.burberry.wc.integration.util.BurberryAPIDBUtil;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.burberry.wc.integration.util.BurberrySampleAPIJsonDataUtil;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.document.LCSDocumentQuery;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sample.*;
import com.lcs.wc.season.*;
import com.lcs.wc.sourcing.*;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;

public final class BurberrySampleAPIProductTransform {

	/**
	 * BurberrySampleAPIProductTransform.
	 */
	private BurberrySampleAPIProductTransform() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberrySampleAPIProductTransform.class);

	/**
	 * This method is used to get Product Bean Data.
	 * 
	 * @param sampleRequestObj
	 *            LCSSampleRequest
	 * @param productObj
	 *            LCSProduct
	 * @param colProdToSeasonLink
	 *            Collection<String>
	 * @param deltaCriteria
	 *            boolean
	 * @return Product Bean
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
	public static Product getProductBean(LCSSampleRequest sampleRequestObj,
			LCSProduct productObj, Collection<String> colProdToSeasonLink,
			boolean deltaCriteria) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException {

		// Method Name
		String methodName = "getProductBean() ";

		// Track execution time
		long prodTransformStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Transform Start Time: ");

		// Get Product Data Bean using Product Object
		logger.debug(methodName + "Extracting Data from Product: "
				+ productObj.getName());

		Product productBean = BurberrySampleAPIJsonDataUtil
				.getProductBean(productObj);
		logger.debug(methodName + "Product Bean: " + productBean);

		// Get Product Source Bean Data using Sample Request Object
		ProdSource productSourceBean = getProductSourceBean(sampleRequestObj);
		logger.debug(methodName + "Product Source Bean: " + productSourceBean);
		// Set Product Source to Product Bean
		productBean.setProdSource(productSourceBean);

		// Get List of Product Season Bean Objects
		List<ProductSeason> colProductSeasonBean = getProductSeasonBeanData(
				productObj, sampleRequestObj, colProdToSeasonLink,
				deltaCriteria);
		logger.debug("Collection ProductSeasonBean: " + productBean);
		// Set Product Season to Product Bean
		productBean.setProductSeason(colProductSeasonBean);
		logger.debug("Product Bean: " + productBean);

		// Method End Time
		long prodTransformEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Transform End Time: ");
		logger.debug(methodName
				+ "Product Transform  Total Execution Time (ms): "
				+ (prodTransformEndTime - prodTransformStartTime));

		return productBean;

	}

	/**
	 * This method is used to get Product Source Bean Data.
	 * 
	 * @param sampleRequestObj
	 *            LCSSampleRequest
	 * @return ProdSource Bean
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
	private static ProdSource getProductSourceBean(
			LCSSampleRequest sampleRequestObj) throws WTException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, IOException {

		// Method Name
		String methodName = "getProductSourceBean() ";

		// Track execution time
		long sourceTransformStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Source Supplier Transform Start Time: ");

		// Get Sourcing Config using Sample Request Object
		LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper
				.latestIterationOf(sampleRequestObj.getSourcingMaster());
		// Get Product Source Bean Data
		ProdSource productSourceBean = new ProdSource();
		// Defect Fix:Start
		if (sourceObj != null) {
			logger.debug(methodName + "Extracting Data from Source: "
					+ sourceObj.getName());
			//Get Source detail - CR R40
			BurberrySampleAPIJsonDataUtil.getSourceBean(sourceObj,
					productSourceBean);
			logger.debug(methodName + "Product Source Bean: "
					+ productSourceBean);
			
		}
		// Defect Fix: End

		// Method End Time
		long sourceTransformEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Source Supplier Transform End Time: ");
		logger.debug(methodName
				+ "Source Supplier Transform  Total Execution Time (ms): "
				+ (sourceTransformEndTime - sourceTransformStartTime));

		// Return
		return productSourceBean;
	}

	/**
	 * This method is used to get Product Season Bean Data.
	 * 
	 * @param productObj
	 *            LCSProduct
	 * @param sampleRequestObj
	 *            LCSSampleRequest
	 * @param colProdToSeasonLink
	 *            Collection<String>
	 * @param deltaCriteria
	 *            boolean
	 * @return List<ProductSeason>
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
	private static List<ProductSeason> getProductSeasonBeanData(
			LCSProduct productObj, LCSSampleRequest sampleRequestObj,
			Collection<String> colProdToSeasonLink, boolean deltaCriteria)
			throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException {

		// Method Name
		String methodName = "getProductSeasonBeanData() ";

		// Track execution time
		long prodSeasonTransformStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Season Transform Start Time: ");

		// Initialisation
		List<ProductSeason> colProductSeasonBean = new ArrayList<ProductSeason>();

		// Get Sourcing Config using Sample Request
		LCSSourcingConfig sourceObj = (LCSSourcingConfig) VersionHelper
				.latestIterationOf(sampleRequestObj.getSourcingMaster());
		logger.debug(methodName + "Sample Request Source: "
				+ sourceObj.getName());

		// Get the collection of source to season links using source master
		Collection<LCSSourceToSeasonLink> colSourceToSeasonLink = (new LCSSourcingConfigQuery())
				.getSourceToSeasonLinks((LCSSourcingConfigMaster) sourceObj
						.getMaster());
		logger.debug(methodName + "Collection of Source Season Links: "
				+ colSourceToSeasonLink);
		// Loop through each Source to Season Link
		for (LCSSourceToSeasonLink sourceToSeasonLink : colSourceToSeasonLink) {
			logger.debug(methodName + "SourceToSeasonLink: "
					+ sourceToSeasonLink);
			sourceToSeasonLink = (LCSSourceToSeasonLink) VersionHelper
					.latestIterationOf(sourceToSeasonLink);
			// Get the season from source-season link
			LCSSeason sourceSeason = (LCSSeason) VersionHelper
					.latestIterationOf(sourceToSeasonLink.getSeasonMaster());
			logger.debug(methodName + "sourceSeason: " + sourceSeason);
			// Using product and season get product-season link
			LCSProductSeasonLink productSeasonLink = (LCSProductSeasonLink) LCSSeasonQuery
					.findSeasonProductLink(productObj, sourceSeason);
			logger.debug(methodName + "productSeasonLink: " + productSeasonLink);

			// checking through each product season object under product
			if (productSeasonLink.isEffectLatest()
					&& ((!productSeasonLink.isSeasonRemoved()) || deltaCriteria)) {
				// checking if product season criteria is given in URL and
				// validating if product season object satisfies the URL
				// criteria
				boolean prodSeasonLinkExists = BurberryAPIDBUtil
						.checkIfObjectExists(
								FormatHelper
										.getNumericObjectIdFromObject(productSeasonLink),
								colProdToSeasonLink);
				logger.debug("prodSeasonLinkExists: " + prodSeasonLinkExists);
				// If exists
				if (prodSeasonLinkExists) {
					logger.debug(methodName
							+ "Extracting Data from ProductSeason Link: "
							+ productSeasonLink);
					// Get Product Season Bean Data
					ProductSeason prodSeasonBean = BurberrySampleAPIJsonDataUtil
							.getProductSeasonBean(productSeasonLink,
									sourceSeason);
					logger.debug("Product Season Bean: " + prodSeasonBean);
					colProductSeasonBean.add(prodSeasonBean);
				}
			}
		}
		logger.debug("Collection of Product Season Bean: "
				+ colProductSeasonBean);

		// Method End Time
		long prodSeasonTransformEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Season Transform End Time: ");
		logger.debug(methodName
				+ "Product Season Transform  Total Execution Time (ms): "
				+ (prodSeasonTransformEndTime - prodSeasonTransformStartTime));

		// Return
		return colProductSeasonBean;

	}

	/**
	 * This method is used to get Product Sample Bean Data.
	 * 
	 * @param sampleRequestObj
	 *            LCSSampleRequest
	 * @param colSKUSamples
	 *            Collection<String>
	 * @return List<ProdSample>
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
	public static List<ProdSample> getListProductSampleBean(
			LCSSampleRequest sampleRequestObj, Collection<String> colSKUSamples)
			throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException, PropertyVetoException {

		// Method Name
		String methodName = "getListProductSampleBean() ";

		// Track execution time
		long prodSampleTransformStartTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Sample Transform Start Time: ");

		// Initialisation
		List<ProdSample> colProdSampleBean = new ArrayList<ProdSample>();

		// Get the collection of Sample Object using Sample Request
		Collection<LCSSample> colSamples = LCSQuery.getObjectsFromResults(
				(new LCSSampleQuery().findSamplesIdForSampleRequest(
						sampleRequestObj, false)),
				BurSampleConstant.LCSSAMPLE_PREFIX,
				BurSampleConstant.LCSSAMPLE_ID);
		logger.debug(methodName + "Collection of Samples: " + colSamples);

		// Loop through each Source to Season Link
		for (LCSSample sampleObject : colSamples) {
			logger.debug(methodName + "Sample Object: " + sampleObject);

			// checking if sample is passed a parameter in URL
			// criteria
			boolean prodSampleExists = BurberryAPIDBUtil.checkIfObjectExists(
					FormatHelper.getNumericObjectIdFromObject(sampleObject),
					colSKUSamples);
			logger.debug(methodName + "prodSampleExists: " + prodSampleExists);
			// If true
			if (prodSampleExists) {
				logger.debug(methodName + "Extracting Data from Sample: "
						+ sampleObject.getName());
				// Get Product Sample Bean Data
				ProdSample prodSampleBean = BurberrySampleAPIJsonDataUtil
						.getProductSampleBean(sampleObject);
				logger.debug(methodName + "Product Sample Info Bean: "
						+ prodSampleBean);
				colProdSampleBean.add(prodSampleBean);

				// Get collection of Product Sample Documents Bean
				List<ProdDocument> colProductSampleDocsBean = getListProductSampleDocument(sampleObject);
				logger.debug(methodName + "Collection ProductSampleDocsBean: "
						+ colProductSampleDocsBean);

				// Set Document Bean to Product Sample
				prodSampleBean.setProdDocuments(colProductSampleDocsBean);
			}
		}
		logger.debug(methodName + "List of Product Sample Info Bean: "
				+ colProdSampleBean);

		// Method End Time
		long prodSampleTransformEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Sample Transform End Time: ");
		logger.debug(methodName
				+ "Product Sample Transform  Total Execution Time (ms): "
				+ (prodSampleTransformEndTime - prodSampleTransformStartTime));
		// Return
		return colProdSampleBean;
	}

	/**
	 * This method is used to get Product Sample Document Bean Data.
	 * 
	 * @param sampleObject
	 *            LCSSample
	 * @return List<ProdDocument>
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
	private static List<ProdDocument> getListProductSampleDocument(
			LCSSample sampleObject) throws WTException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException, PropertyVetoException {

		// Method Name
		String methodName = "getListProductSampleDocument() ";

		// Track execution time
		long prodSampleDocsTransformStartTime = BurberryAPIUtil
				.printCurrentTime(methodName,
						"Product Sample Docs Transform Start Time: ");

		// Initialisation
		List<ProdDocument> colProductSampleDocsBean = new ArrayList<ProdDocument>();

		// Get Collection of Documents using Sample Object
		Collection<LCSDocument> colDocuments = LCSQuery.getObjectsFromResults(
				new LCSDocumentQuery().findPartDocReferences(sampleObject),
				BurPaletteMaterialConstant.LCSDOCUMENT_PREFIX,
				BurPaletteMaterialConstant.DOCUMENT_ID);
		logger.debug(methodName + "Collection of Documents = " + colDocuments);

		// Loop through document collection
		for (LCSDocument docObject : colDocuments) {
			// Get the latest iteration
			docObject = (LCSDocument) VersionHelper
					.latestIterationOf(docObject);
			logger.debug(methodName + "Extracting Data from Document: "
					+ docObject.getName());
			// Get Product Sample Document Bean
			ProdDocument documentBean = new ProdDocument();
			BurberrySampleAPIJsonDataUtil.getDocumentBean(docObject,
					documentBean);
			logger.debug(methodName + "Document Bean: " + documentBean);
			// Set document bean to list
			colProductSampleDocsBean.add(documentBean);
		}

		logger.debug(methodName + "Collection of Product Docs Bean: "
				+ colProductSampleDocsBean);

		// Method End Time
		long prodSampleDocsTransformEndTime = BurberryAPIUtil.printCurrentTime(
				methodName, "Product Sample Docs Transform End Time: ");
		logger.debug(methodName
				+ "Product Sample Docs Transform  Total Execution Time (ms): "
				+ (prodSampleDocsTransformEndTime - prodSampleDocsTransformStartTime));
		// Return
		return colProductSampleDocsBean;
	}
}
