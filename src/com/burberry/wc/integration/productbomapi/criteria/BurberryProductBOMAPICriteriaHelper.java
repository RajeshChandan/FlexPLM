package com.burberry.wc.integration.productbomapi.criteria;

import java.text.ParseException;
import java.util.*;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.BurException;
import com.burberry.wc.integration.util.BurberryAPICriteriaUtil;
import com.burberry.wc.integration.util.BurberryAPIUtil;
import com.lcs.wc.flexbom.*;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.MaterialSupplierFlexTypeScopeDefinition;
/**
 * A Helper class to handle Criteria activity. Class contain several
 * method to handle Extraction activity i.e. Extracting Data from different
 * objects and putting it to the bean
 * 
 * @version 'true' 1.0.1
 * @author 'true' ITC INFOTECH
 */
public final class BurberryProductBOMAPICriteriaHelper {

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryProductBOMAPICriteriaHelper.class);

	/**
	 * BurberryPaletteMaterialAPICriteriaHelper.
	 */
	private BurberryProductBOMAPICriteriaHelper() {

	}

	/**
	 * @param strAttDisplayName
	 * @param strAttValue
	 * @return
	 * @throws BurException
	 * @throws ParseException
	 * @throws WTException
	 */
	public static Collection<Map> getBomMaterialQueryCriteria(
			String strAttDisplayName, String strAttValue) throws BurException,
			ParseException, WTException {
		String methodName = "getBomMaterialQueryCriteria() ";
		// Initialisation of Flex Type
		FlexType materialType = FlexTypeCache
				.getFlexTypeRootByClass((LCSMaterial.class).getName());
		logger.debug(methodName + "Material Type: " + materialType);

		// Initialisation of collection
		Collection<Map> bomQueryCriteria = new ArrayList<Map>();

		// Step 1: Check if attribute display name is valid
		Collection<FlexTypeAttribute> colBomAtt = BurberryAPIUtil
				.getFlexTypeAttribute(materialType,
						MaterialSupplierFlexTypeScopeDefinition.MATERIAL_SCOPE,
						null, strAttDisplayName);
		logger.debug(methodName + "colBOM MaterialAtt: " + colBomAtt);

		for (FlexTypeAttribute bomFlexTypeAttribute : colBomAtt) {
			// Step 2: Check for attribute data type
			String dbColumnName = bomFlexTypeAttribute.getColumnName();
			logger.debug(methodName + "BOM Material Attribute Name: "
					+ strAttDisplayName);
			logger.debug(methodName + "BOM Material Table Column Name: "
					+ dbColumnName);
			logger.debug(methodName + "BOM Material Attribute Value: "
					+ strAttValue);

			// Step 3: Append criteria based on data type
			bomQueryCriteria.addAll(BurberryAPICriteriaUtil
					.appendCriteriaBasedOnDataType(
							(LCSMaterial.class).getSimpleName(), dbColumnName,
							bomFlexTypeAttribute, strAttValue));
		}
		logger.debug(methodName + "BOM MaterialQueryCriteria: "
				+ bomQueryCriteria);
		// Step 4: Return Criteria Collection
		// Return Statement
		return bomQueryCriteria;
	}

	/**
	 * @param strAttDisplayName
	 * @param strAttValue
	 * @param className
	 * @return
	 * @throws WTException
	 * @throws BurException
	 * @throws ParseException
	 */
	public static Collection<Map> getQueryCriteria(String strAttDisplayName,
			String strAttValue, Class<?> className) throws WTException,
			BurException, ParseException {
		String methodName = "getQueryCriteria() ";
		// Initialisation of Flex Type
		FlexType flextype = FlexTypeCache.getFlexTypeRootByClass(className
				.getName());
		flextype=FlexTypeCache.getFlexTypeFromPath("BOM\\Materials");
	
		logger.debug(methodName + "BOM Type: " + flextype);

		// Initialisation of collection
		Collection<Map> bomQueryCriteria = new ArrayList<Map>();
		// Setting scope based on class
		String scope = FlexBOMPart.class.equals(className) ? FlexBOMFlexTypeScopeDefinition.BOM_SCOPE
				: FlexBOMFlexTypeScopeDefinition.LINK_SCOPE;
		// Step 1: Check if attribute display name is valid
		Collection<FlexTypeAttribute> colBomAtt = BurberryAPIUtil
				.getFlexTypeAttribute(flextype, scope, null, strAttDisplayName);
		logger.debug(methodName + "colBomAtt: " + colBomAtt);

		for (FlexTypeAttribute bomFlexTypeAttribute : colBomAtt) {
			// Step 2: Check for attribute data type
			String dbColumnName = bomFlexTypeAttribute.getColumnName();
			logger.debug(methodName + "Attribute Name: " + strAttDisplayName);
			logger.debug(methodName + "Table Column Name: " + dbColumnName);
			logger.debug(methodName + "Attribute Value: " + strAttValue);

			// Step 3: Append criteria based on data type
			bomQueryCriteria.addAll(BurberryAPICriteriaUtil
					.appendCriteriaBasedOnDataType(
							className.getSimpleName(), dbColumnName,
							bomFlexTypeAttribute, strAttValue));
		}
		logger.debug(methodName + "QueryCriteria: " + bomQueryCriteria);
		// Step 4: Return Criteria Collection
		// Return Statement
		return bomQueryCriteria;
	}

}
