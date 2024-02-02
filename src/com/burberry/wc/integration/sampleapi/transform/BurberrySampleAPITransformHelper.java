package com.burberry.wc.integration.sampleapi.transform;

/**
 * A Helper class to handle Transformation activity. Class contain several
 * method to handle Extraction activity i.e. Extracting Data from different
 * objects and putting it to the bean
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.sampleapi.bean.*;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.burberry.wc.integration.util.BurberrySampleAPIJsonDataUtil;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.sample.LCSSampleRequest;

public final class BurberrySampleAPITransformHelper {

	/**
	 * BurberrySampleAPIProductTransformHelper.
	 */
	private BurberrySampleAPITransformHelper() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberrySampleAPITransformHelper.class);

	/**
	 * This method is used to get Product Sample Request Bean Data.
	 * 
	 * @param sampleRequestObj
	 *            LCSSampleRequest
	 * @param productObj
	 *            LCSProduct
	 * @param colProdToSeasonLink
	 *            Collection<String>
	 * @param colSKUSamples
	 *            Collection<String>
	 * @param deltaCriteria
	 *            Boolean
	 * @return ProductSampleRequest Bean
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
	public static ProductSampleRequest getProductSampleRequestBean(
			LCSSampleRequest sampleRequestObj, LCSProduct productObj,
			Collection<String> colProdToSeasonLink,
			Collection<String> colSKUSamples, boolean deltaCriteria)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, WTException, IOException, PropertyVetoException {

		// Method Name
		String methodName = "getProductSampleRequestBean() ";

		// Method Start Time
		long prdSmpReqStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"prdSmpReqStartTime: ");

		// Get Product Sample Request Bean Data using Sample Request Object
		logger.debug(methodName + "Extracting Data from Sample Request: "
				+ sampleRequestObj.getName());

		ProductSampleRequest prodSampleRequestBean = BurberrySampleAPIJsonDataUtil
				.getProductSampleRequestBean(sampleRequestObj);
		logger.debug(methodName + "Product Sample Request Bean: "
				+ prodSampleRequestBean);

		// Get Product Bean Data
		Product productBean = BurberrySampleAPIProductTransform.getProductBean(
				sampleRequestObj, productObj, colProdToSeasonLink,
				deltaCriteria);
		logger.debug(methodName + "Product Bean: " + productBean);
		// Set Product Bean
		prodSampleRequestBean.setProduct(productBean);

		// Get Product Sample Bean Data
		List<ProdSample> colProdSampleBean = BurberrySampleAPIProductTransform
				.getListProductSampleBean(sampleRequestObj, colSKUSamples);
		logger.debug(methodName + "Collection of ProdSampleBean: "
				+ colProdSampleBean);
		// Set the Product Sample Info Bean
		prodSampleRequestBean.setProdSample(colProdSampleBean);

		// Final Product Sample Request Bean Data
		logger.debug(methodName + "Product Sample Request Bean: "
				+ prodSampleRequestBean);

		// Method End Time
		long prdSmpReqEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"prdSmpReqEndTime: ");
		logger.debug(methodName
				+ "Product Sample Request Transform  Total Execution Time (ms): "
				+ (prdSmpReqEndTime - prdSmpReqStartTime));

		// Return
		return prodSampleRequestBean;
	}

	/**
	 * This method is used to get Material Sample Request Bean Data.
	 * 
	 * @param sampleRequestObj
	 *            LCSSampleRequest
	 * @param materialObject
	 *            LCSMaterial
	 * @param colSamples
	 *            Collection<String>
	 * @return MaterialSampleRequest Bean
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
	public static MaterialSampleRequest getMaterialSampleRequestBean(
			LCSSampleRequest sampleRequestObj, LCSMaterial materialObject,
			Collection<String> colSamples) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, WTException,
			IOException, PropertyVetoException {

		// Method Name
		String methodName = "getMaterialSampleRequestBean() ";

		// Method Start Time
		long matSmpReqStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"matSmpReqStartTime: ");

		// Get Material Sample Request Bean Data using Sample Request Object
		logger.debug(methodName + "Extracting Data from Sample Request: "
				+ sampleRequestObj.getName());
		MaterialSampleRequest materialSampleRequestBean = BurberrySampleAPIJsonDataUtil
				.getMaterialSampleRequestBean(sampleRequestObj);
		logger.debug(methodName + "Material Sample Request Bean: "
				+ materialSampleRequestBean);

		// Get Material Bean Data
		Material materialBean = BurberrySampleAPIMaterialTransform
				.getMaterialBean(materialObject);
		logger.debug(methodName + "Material Bean: " + materialBean);
		// Set Material Bean to Material Sample Request Bean
		materialSampleRequestBean.setMaterial(materialBean);

		// Get Material Source Bean Data
		MatSource materialSourceBean = BurberrySampleAPIMaterialTransform
				.getMaterialSourceBean(sampleRequestObj);
		logger.debug(methodName + "Material Source Bean: " + materialSourceBean);
		// Set Material Source to Material Sample Request Bean
		materialSampleRequestBean.setMatSource(materialSourceBean);

		// Get Material Sample Bean Data
		List<MatSample> colMaterialSampleBean = BurberrySampleAPIMaterialTransform
				.getListMaterialSampleBean(sampleRequestObj,
						materialSampleRequestBean, colSamples);
		logger.debug(methodName + "Material Sample Bean: "
				+ colMaterialSampleBean);
		// Set the Material Sample Bean
		materialSampleRequestBean.setMatSample(colMaterialSampleBean);

		// Final Material Sample Request Bean Data
		logger.debug(methodName + "Material Sample Request Bean "
				+ materialSampleRequestBean);

		// Method End Time
		long matSmpReqEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"matSmpReqEndTime: ");
		logger.debug(methodName
				+ "Material Sample Request Transform  Total Execution Time (ms): "
				+ (matSmpReqEndTime - matSmpReqStartTime));
		// Return
		return materialSampleRequestBean;
	}

}
