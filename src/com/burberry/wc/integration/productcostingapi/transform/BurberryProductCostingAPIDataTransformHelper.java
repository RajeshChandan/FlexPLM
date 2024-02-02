package com.burberry.wc.integration.productcostingapi.transform;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.productcostingapi.bean.*;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.burberry.wc.integration.util.BurberryProductCostingAPIJsonDataUtil;
import com.lcs.wc.product.LCSProduct;

/**
 * A Helper class to handle Transformation activity. Class contain several
 * method to handle Extraction activity i.e. Extracting Data from different
 * objects and putting it to the bean
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */

public final class BurberryProductCostingAPIDataTransformHelper {

	/**
	 * BurberryProductCostingAPIDataTransformHelper.
	 */
	private BurberryProductCostingAPIDataTransformHelper() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductCostingAPIDataTransformHelper.class);

	/**
	 * @param productObj
	 * @param collProdToSKUIds
	 * @param collProdToSourceIds
	 * @param collProdToCostSheetIds
	 * @param deltaCriteria
	 * @param seasons 
	 * @param mapTrackedCostSheet 
	 * @return
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	public static Style getProductStyleBean(LCSProduct productObj,
			Collection<String> collProdToSKUIds,
			Collection<String> collProdToSourceIds,
			Collection<String> collProdToCostSheetIds, boolean deltaCriteria, Collection seasons, Map<String, Collection<HashMap>> mapTrackedCostSheet)
			throws WTException, IllegalAccessException,
			InvocationTargetException, IOException, NoSuchMethodException {

		String methodName = "getProductStyleBean() ";
		long prodStyleStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"prodStyleStartTime: ");

		logger.debug(methodName + " Product : " + productObj.getName());

		// Extracting Product Data
		Style styleBean = BurberryProductCostingAPIJsonDataUtil
				.getStyleBean(productObj);
		// Extracting Source List data
		List<Source> lstSources = BurberryProductCostingAPIDataTransform
				.getListProductCostingSourceBean(productObj, collProdToSourceIds,collProdToSKUIds,collProdToCostSheetIds,seasons, deltaCriteria,mapTrackedCostSheet);
		logger.debug(methodName + "List of Source Beans: " + lstSources);
		styleBean.setSource(lstSources);

		logger.debug(methodName + " Style Bean " + styleBean);

		long prodStyleEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"prodStyleEndTime: ");
		logger.debug(methodName + "Style Transform  Total Execution Time (ms): "
				+ (prodStyleEndTime - prodStyleStartTime));
		return styleBean;

	}

}
