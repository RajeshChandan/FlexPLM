package com.burberry.wc.integration.util;

import java.text.*;
import java.util.*;
import java.util.Map.Entry;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;

import wt.util.WTException;

import com.burberry.wc.integration.exception.*;
import com.burberry.wc.integration.productapi.constant.BurProductConstant;
import com.burberry.wc.integration.productapi.extraction.BurberryProductAPIDataExtraction;
import com.burberry.wc.integration.productbomapi.constant.BurProductBOMConstant;
import com.burberry.wc.integration.productbomapi.extraction.BurberryProductBOMAPIDataExtraction;
import com.burberry.wc.integration.productcostingapi.constant.BurProductCostingConstant;
import com.burberry.wc.integration.productcostingapi.extraction.BurberryProductCostingAPIDataExtraction;
import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.palettematerialapi.extraction.BurberryPaletteMaterialAPIDataExtraction;
import com.burberry.wc.integration.planningapi.extraction.BurberryPlanningAPIDataExtraction;
import com.burberry.wc.integration.sampleapi.extraction.BurberrySampleAPIDataExtraction;

import com.lcs.wc.db.*;
import com.lcs.wc.flextype.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.*;
import com.lcs.wc.sourcing.*;
import com.lcs.wc.specification.*;
import com.lcs.wc.util.*;

/**
 * Utility class for all APIs.
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */

public final class BurberryAPIUtil {

	/**
	 * Default Constructor.
	 */
	private BurberryAPIUtil() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger.getLogger(BurberryAPIUtil.class);

	/**
	 * STR_SOURCE_ID.
	 */
	private static final String STR_SOURCE_ID = "SOURCE_ID";

	/**
	 * STR_PALETTE_MATERIAL_API.
	 */
	private static final String STR_PALETTE_MATERIAL_API = "Palette Material";

	/**
	 * STR_PRODUCT_BOM_API.
	 */
	private static final String STR_PRODUCT_BOM_API = "Product BOM";

	/**
	 * STR_SAMPLE_API.
	 */
	private static final String STR_SAMPLE_API = "Sample";

	/**
	 * STR_PRODUCT_COSTING_API.
	 */
	private static final String STR_PRODUCT_COSTING_API = "Product Costing";

	/**
	 * STR_PLANNING_API.
	 */
	private static final String STR_PLANNING_API = "Planning";

	/**
	 * STR_PRODUCT_API.
	 */
	private static final String STR_PRODUCT_API = "Product";

	/**
	 * Method will spilt valid objects and return a map.
	 * 
	 * @param Valid objects string
	 * @return Map with valid objects
	 */
	public static Map<String, String> initializeValidObjects(String strValidObjects) {

		String methodName = "initializeValidObjects() ";
		// Initialisation map for valid Objects
		Map<String, String> apiValidObjects = new HashMap<String, String>();
		// Split the valid object string with comma
		String validObject[] = strValidObjects.split(BurConstant.STRING_COMMA);
		// Loop through each value
		for (String value : validObject) {
			logger.debug(methodName + "value: " + value);
			String temp[] = value.split("~");
			logger.debug(methodName + "temp[1]= " + temp[1] + " :: temp[0]=" + temp[0]);
			// Add the key and value into a map
			apiValidObjects.put(temp[1], temp[0]);
		}
		logger.debug(methodName + "Valid Objects Map: " + apiValidObjects);
		// Return Statement
		return apiValidObjects;
	}

	/**
	 * Method to check if passed parameters are valid InvalidInputException.
	 * 
	 * @param queryParams params
	 * @throws WTException           Exception
	 * @throws BurException          Exception
	 * @throws InvalidInputException Exception
	 * @throws ParseException        Exception
	 */
	public static void verifyPassedParameters(MultivaluedMap<String, String> queryParams, Map mapValidObjects)
			throws WTException, BurException, InvalidInputException, ParseException {

		String methodName = "verifyPassedParameters() ";

		// Check if the request parameters is not empty
		if (queryParams.isEmpty()) {
			throwBurException("", BurConstant.STR_ERROR_MSG_PRODUCT_API_NO_PARAMETERS);
		}

		// Check if the request parameters passed is less than maximum allowed
		if (queryParams.size() > 5) {
			throwBurException("", BurConstant.STR_ERROR_MSG_PRODUCT_API_MAXIMUM_PARAMTERS);
		}

		// Loop through each request parameters and verify if its valid objects
		for (Entry<String, List<String>> mapEntry : queryParams.entrySet()) {
			String strParamKey = mapEntry.getKey();
			logger.debug(methodName + "Param Key: " + strParamKey);
			List<String> lAttValue = queryParams.get(strParamKey);
			logger.debug(methodName + "List of Attributes: " + lAttValue);
			logger.debug(methodName + "Parameter Value(s) Size: " + lAttValue.size());

			// Initialisation of iterator
			Iterator itValues = lAttValue.iterator();
			// Loop through each parameter values
			while (itValues.hasNext()) {
				String strAttValue = (String) itValues.next();
				logger.debug(methodName + "Passed Attribute Value: " + strAttValue);
				String[] objAtt = strParamKey.split("~~");
				logger.debug(methodName + "Object Att: " + Arrays.toString(objAtt));
				if (objAtt.length > 1) {
					String strObjectName = objAtt[0].trim();
					logger.debug(methodName + "Passed Object Name: " + strObjectName);
					String strAttDisplayName = objAtt[1].trim();
					logger.debug(methodName + "Passed Display Name: " + strAttDisplayName);
					// Check if Object Name is not null or empty
					if (!FormatHelper.hasContent(strObjectName) || !mapValidObjects.containsKey(strObjectName)) {
						throwBurException(strObjectName, BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_OBJECT);
					}
				}
				// Object name is not valid
				else {
					throwBurException(strParamKey, BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_OBJECT);
				}
			}
		}
	}

	/**
	 * Method to get start and end date.
	 * 
	 * @param sDate date
	 * @param eDate date
	 * @return map
	 * @throws ParseException exception
	 * @throws BurException   exception
	 */
	public static Map<String, Date> getDates(String sDate, String eDate) throws ParseException, BurException {
		String methodName = "getDates() ";
		Map<String, Date> dates = new HashMap<String, Date>();
		final SimpleDateFormat sf = new SimpleDateFormat(BurConstant.dateFormat);
		sf.setLenient(false);
		Date startdate;
		Date enddate;

		// Check Start Date
		if (!FormatHelper.hasContent(sDate)) {
			throwBurException("", BurConstant.STR_ERROR_MSG_PRODUCT_API_MISSING_START_DATE);
		} else if (validDateFormat("START", sDate)) {
			startdate = sf.parse(sDate);
			dates.put("startdate", startdate);
			logger.debug(methodName + "startDate " + startdate);
		}

		// Check End Data
		if (!FormatHelper.hasContent(eDate)) {
			enddate = new Date();
			dates.put("enddate", enddate);
			logger.debug(methodName + "enddate " + enddate);
		} else if (validDateFormat("END", eDate)) {
			enddate = sf.parse(eDate);
			dates.put("enddate", enddate);
			logger.debug(methodName + "enddate " + enddate);
		}
		// Return Statement
		return dates;
	}

	/**
	 * Verify Date Length.
	 * 
	 * @param parameter
	 * @param stringDate
	 * @return
	 * @throws BurException
	 */
	private static boolean validDateFormat(String parameter, String stringDate) throws BurException {
		String methodName = "validDateFormat() ";
		if (stringDate.length() != 19) {
			logger.debug(methodName + "Inavlid Date Format");
			throwBurException(parameter + "=" + stringDate, BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_DATE);
		}
		return true;
	}

	/**
	 * Method to get Flex type attribute.
	 * 
	 * @param flexType          FlexType
	 * @param strAttDisplayName name
	 * @return flex type attribute
	 * @throws WTException  exception
	 * @throws BurException exception
	 */
	public static Collection<FlexTypeAttribute> getFlexTypeAttribute(FlexType flexType, String scope, String level,
			String strAttDisplayName) throws WTException, BurException {

		String methodName = "getFlexTypeAttribute() ";
		Collection<FlexTypeAttribute> colFlexTypeAttribute = new ArrayList<FlexTypeAttribute>();

		logger.debug(methodName + "Scope: " + scope);
		logger.debug(methodName + "Level: " + level);

		// Get all the attributes associated to flex type in a map
		HashMap<String, String> attMap = FlexType.getAttributeKeyDisplayMap(flexType.getAllAttributes(scope, level));
		logger.debug(methodName + "Flex Att Map: " + attMap);

		// Loop through each attribute
		for (Map.Entry<String, String> entry : attMap.entrySet()) {
			logger.debug(methodName + "entry: " + entry);
			if (entry.getValue().equalsIgnoreCase(strAttDisplayName)) {
				logger.debug(methodName + "attKey " + entry.getKey() + " type " + flexType.getFullName());
				logger.debug(methodName + "Flex Type Attribute: " + flexType.getAttribute(entry.getKey()));
				// Add to collection
				colFlexTypeAttribute.add(flexType.getAttribute(entry.getKey()));
			}
		}
		// Check for additional attributes from property file
		logger.debug(methodName + "Check for additional child node attributes from property file..");
		colFlexTypeAttribute.addAll(getChildNodeAttributes(flexType, strAttDisplayName));
		logger.debug(methodName + "colFlexTypeAttribute: " + colFlexTypeAttribute);

		// Check if collection is empty
		if (colFlexTypeAttribute.isEmpty()) {
			throwBurException(strAttDisplayName, BurConstant.STR_ERROR_MSG_PRODUCT_API_INVALID_ATTRIBUTE);
		}
		// Return Statement
		return colFlexTypeAttribute;
	}

	/**
	 * Method to get child node attributes.
	 * 
	 * @param flexType          Flex Type
	 * @param strAttDisplayName name
	 * @return collection
	 * @throws WTException exception
	 */
	private static Collection<FlexTypeAttribute> getChildNodeAttributes(FlexType flexType, String strAttDisplayName)
			throws WTException {
		String methodName = "getChildNodeAttributes() ";

		// Initialisation
		Collection<FlexTypeAttribute> colChildNodeFlexTypeAtt = new ArrayList<FlexTypeAttribute>();

		// Get FlexType Name
		String strFlexTypeName = flexType.getTypeName();
		logger.debug(methodName + "TYPE NAME: " + strFlexTypeName);
		logger.debug(methodName + "SCOPE NAME: " + flexType.getTypeScopeDefinition());

		// Append the flex type to generic child node attributes property key
		String childNodeAttributes = BurPaletteMaterialConstant.STR_GENERIC_CHILD_NODE_ATT
				+ flexType.getTypeName().toLowerCase();
		logger.debug(methodName + "Child Node Attributes: " + childNodeAttributes);
		if (FormatHelper.hasContent(childNodeAttributes)) {
			// Get all the child node attributes collection based on the flex
			// type
			colChildNodeFlexTypeAtt = getChildNodeAttributeBasedOnFlexType(strAttDisplayName, childNodeAttributes);
			logger.debug(methodName + "colChildNodeFlexTypeAtt: " + colChildNodeFlexTypeAtt);
		}
		// Return Statement
		return colChildNodeFlexTypeAtt;
	}

	/**
	 * Method to get child node attributes based on flex type.
	 * 
	 * @param strAttDisplayName         String
	 * @param strAllChildNodeAttributes String
	 * @return Collection
	 * @throws WTException Exception
	 */
	private static Collection<FlexTypeAttribute> getChildNodeAttributeBasedOnFlexType(String strAttDisplayName,
			String childNodeAttributes) throws WTException {
		String methodName = "getChildNodeAttributeBasedOnFlexType() ";
		Collection<FlexTypeAttribute> colChildNodeFlexTypeAttribute = new ArrayList<FlexTypeAttribute>();
		Map<String, String> mapChildNodeAttributes = new HashMap<String, String>();
		// Get the child node attribute property key string
		String childNodeAtt = LCSProperties.get(childNodeAttributes);
		// Check if child node attributes keys has content
		if (FormatHelper.hasContent(childNodeAtt)) {
			// Get all the child node attributes from property file
			String childAtts[] = LCSProperties.get(childNodeAttributes).split("\\|");
			// Check if child node attributes exists
			// Loop through all the child node attributes
			for (String value : childAtts) {
				logger.debug(methodName + "value: " + value);
				String temp[] = value.split("~");
				logger.debug(methodName + "temp[0]: " + temp[0] + " = temp[1]:" + temp[1]);
				mapChildNodeAttributes.put(temp[0], temp[1]);
			}
			logger.debug(methodName + "mapChildNodeAttributes: " + mapChildNodeAttributes);
			logger.debug(methodName + "strAttDisplayName: " + strAttDisplayName);

			// Check if attribute display name exists in map
			if (!mapChildNodeAttributes.isEmpty() && mapChildNodeAttributes.containsKey(strAttDisplayName)) {
				// Get the attribute key for the display name
				String strStringKey = mapChildNodeAttributes.get(strAttDisplayName);
				logger.debug(methodName + "strStringKey: " + strStringKey);
				// Split the attributes
				String temp[] = strStringKey.split(",");
				// Loop through each attribute
				for (String value : temp) {
					logger.debug(methodName + " Value:" + value);
					logger.debug(methodName + "Property Key:" + childNodeAttributes + "." + value);
					// Get the flextype path using the property entry
					String strFlexTypePath = LCSProperties.get(childNodeAttributes + "." + value);
					logger.debug(methodName + "Child Node Path: " + strFlexTypePath);
					// Split the flextype paths
					String slFlexTypePath[] = strFlexTypePath.split(",");
					// Loop through each flex type
					for (String strPath : slFlexTypePath) {
						logger.debug(methodName + "strPath: " + strPath);
						// Get the child node flex type
						FlexType childFlexType = FlexTypeCache.getFlexTypeFromPath(strPath);
						// Get the flex type attribute
						FlexTypeAttribute fta = childFlexType.getAttribute(value);
						// Add to the collection
						colChildNodeFlexTypeAttribute.add(fta);
					}
				}
			}
		}
		logger.debug(methodName + "colChildNodeFlexTypeAttribute: " + colChildNodeFlexTypeAttribute);
		// Return Statement
		return colChildNodeFlexTypeAttribute;
	}

	/**
	 * Method to Filter Criteria.
	 * 
	 * @param criteria collection
	 * @param key      key for filter
	 * @return collection
	 */
	public static Map<String, List> mapFilter(Collection<Map> criteria) {
		String methodName = "mapFilter() ";
		// Initialisation
		Map<String, List> objectMap = new HashMap<String, List>();
		// Loop through the map criteria
		for (Map<String, String> mapCriteria : criteria) {
			logger.debug(methodName + "mapCriteria: " + mapCriteria);
			// Loop through each map and get key and value
			for (Map.Entry<String, String> mapEntry : mapCriteria.entrySet()) {
				String mapKey = mapEntry.getKey();
				logger.debug(methodName + "mapKey: " + mapKey);
				List<String> mapIds = new ArrayList<String>();
				if (objectMap.get(mapKey) != null) {
					mapIds = objectMap.get(mapKey);
				}
				mapIds.add(mapCriteria.get(mapKey));
				objectMap.put(mapKey, mapIds);
			}
		}
		logger.debug(methodName + "mapIds: " + objectMap);
		// Return Statement
		return objectMap;
	}

	/**
	 * Method to print time stamp to track start and end of execution.
	 * 
	 * @param methodName String
	 * @param msg        Message
	 * @return long
	 */
	public static long printCurrentTime(String methodName, String msg) {
		DateFormat formatter = new SimpleDateFormat(BurConstant.dateFormat);
		Calendar calendar = Calendar.getInstance();
		long lStartTime = System.currentTimeMillis();
		calendar.setTimeInMillis(lStartTime);
		logger.debug(methodName + " " + msg + " " + formatter.format(calendar.getTime()));
		return lStartTime;
	}

	/**
	 * Method to get delta date map.
	 * 
	 * @param mapDeltaDateTime Map
	 * @return map
	 * @throws ParseException Exception
	 * @throws BurException   Exception
	 */
	public static Map getDeltaDateMap(Map<String, String> mapDeltaDateTime) throws ParseException, BurException {

		String methodName = "getDeltaDateMap() ";
		Map deltaMap = new HashMap();
		// Initialisation of string date variables
		String strStart = BurConstant.STRING_EMPTY;
		String strEnd = BurConstant.STRING_EMPTY;
		boolean modifyDate = false;

		// Loop through the request parameters for start and end dates
		for (Map.Entry<String, String> mapEntry : mapDeltaDateTime.entrySet()) {
			// Check if start create is provided
			if ("STARTCREATE".equalsIgnoreCase(mapEntry.getKey())) {
				strStart = mapEntry.getValue();
				logger.debug(methodName + "String Start Create Date: " + strStart);
				modifyDate = false;
			}
			// Check if start modify is provided
			else if ("STARTMODIFY".equalsIgnoreCase(mapEntry.getKey())) {
				strStart = mapEntry.getValue();
				logger.debug(methodName + "String Start Modify Date: " + strStart);
				modifyDate = true;
			}
			// Check for end create and modify date is provided
			else if ("ENDCREATE".equalsIgnoreCase(mapEntry.getKey())
					|| "ENDMODIFY".equalsIgnoreCase(mapEntry.getKey())) {
				strEnd = mapEntry.getValue();
				logger.debug(methodName + "String End Create/Modify Date: " + strEnd);
			}
		}

		// Format String Dates to date format
		Map<String, Date> dateMap = BurberryAPIUtil.getDates(strStart, strEnd);
		logger.debug(methodName + "Date Map: " + dateMap);
		Date startdate = dateMap.get("startdate");
		logger.debug(methodName + "Start Date: " + startdate);
		Date enddate = dateMap.get("enddate");
		logger.debug(methodName + "End Date: " + enddate);
		deltaMap.putAll(dateMap);
		deltaMap.put("modify", modifyDate);
		return deltaMap;

	}

	/**
	 * Method to get criteria map.
	 * 
	 * @param apitype         API Type
	 * @param queryParams     Query Params
	 * @param mapValidObjects Valid Objects
	 * @return
	 * @throws ParseException
	 * @throws BurException
	 * @throws WTException
	 * @throws InvalidInputException
	 */
	public static Collection<Map> getCriteriaCollection(String apitype, MultivaluedMap<String, String> queryParams,
			Map<String, String> mapValidObjects)
					throws InvalidInputException, WTException, BurException, ParseException {
		String methodName = "getCriteriaCollection() ";

		// Initialisation of criteria map
		Collection<Map> criteriaMap = new ArrayList<Map>();
		boolean bProductDelta = false;
		boolean bMaterialDelta = false;
		// Initialisation of date delta map
		HashMap<String, String> mapDeltaDate = new HashMap<String, String>();
		// Initialisation of Hash Set (remove duplicate)
		HashSet<Map> hsMap = new HashSet<Map>();

		// Loop through each request parameters
		for (Entry<String, List<String>> mapEntry : queryParams.entrySet()) {
			String strParamKey = mapEntry.getKey();
			logger.debug(methodName + "Parameter Key: " + strParamKey);
			List<String> lAttValue = queryParams.get(strParamKey);
			logger.debug(methodName + "List of Values: " + lAttValue);
			// Initialisation of iterator
			Iterator itValues = lAttValue.iterator();
			// Loop through parameter values
			while (itValues.hasNext()) {
				String strAttValue = (String) itValues.next();
				logger.debug(methodName + "Parameter Value: " + strAttValue);
				String[] objAtt = strParamKey.split("~~");
				logger.debug(methodName + "Parameter Object and Display: " + Arrays.toString(objAtt));
				String strObjectName = objAtt[0].trim();
				logger.debug(methodName + "Parameter Object Name: " + strObjectName);
				String strAttDisplayName = objAtt[1].trim();
				logger.debug(methodName + "Parameter Display Name: " + strAttDisplayName);
				// if Object name is not Delta then regular object
				if (!strObjectName.contains("Delta")) {
					// Get Product API Query Statement's Search Criteria
					Collection<Map> queryCriteria = getAPIBasedQueryCritera(apitype, strObjectName, strAttDisplayName,
							strAttValue, mapValidObjects);
					logger.debug(methodName + "Query Criteria: " + queryCriteria);
					hsMap.addAll(queryCriteria);
				}
				// Delta object add it to map
				else {
					if (strObjectName.contains("Product")) {
						bProductDelta = true;
					} else if (strObjectName.contains("Material")) {
						bMaterialDelta = true;
					}
					// Passed Object is Delta
					mapDeltaDate.put(strAttDisplayName, strAttValue);
				}
			}
		}
		logger.debug(methodName + "Date Delta Map >>>>>>> " + mapDeltaDate);
		// Check of Data Delta Map is not empty
		if (!mapDeltaDate.isEmpty()) {
			Collection<Map> deltaCriteriaMap = getAPIBasedDeltaCriteria(apitype, mapDeltaDate, bProductDelta,
					bMaterialDelta);
			logger.debug(methodName + "Date Delta Criteria Map: " + deltaCriteriaMap);
			hsMap.addAll(deltaCriteriaMap);
		}
		logger.debug(methodName + "Hash Set Map: " + hsMap);
		criteriaMap.addAll(hsMap);
		logger.debug(methodName + "Criteria Map <<<<::::::::::::::::::::>>>> " + criteriaMap);
		return criteriaMap;
	}

	/**
	 * Method to get API based Delta Criteria Map.
	 * 
	 * @param apitype        API Type
	 * @param mapDeltaDate   Map
	 * @param bMaterialDelta
	 * @param bProductDelta
	 * @return Collection
	 * @throws BurException   Exception
	 * @throws ParseException Exception
	 * @throws WTException    Exception
	 */
	private static Collection<Map> getAPIBasedDeltaCriteria(String apitype, Map<String, String> mapDeltaDate,
			boolean bProductDelta, boolean bMaterialDelta) throws BurException, ParseException, WTException {
		String methodName = "getAPIBasedDeltaCriteria() ";
		// Initialisation of criteria map
		Collection<Map> deltaCriteriaMap = new ArrayList<Map>();

		// Product API
		if (STR_PRODUCT_API.equals(apitype)) {
			deltaCriteriaMap = BurberryProductAPIDataExtraction.getModifiedProductIds(mapDeltaDate);
			logger.debug(methodName + "Product API Delta Criteria Map: " + deltaCriteriaMap);
		}
		// Palette Material API
		else if (STR_PALETTE_MATERIAL_API.equals(apitype)) {
			deltaCriteriaMap = BurberryPaletteMaterialAPIDataExtraction.getModifiedMaterialIds(mapDeltaDate);
			logger.debug(methodName + "Palette Material API Delta Criteria Map: " + deltaCriteriaMap);
		} else if (STR_PRODUCT_BOM_API.equals(apitype)) {
			deltaCriteriaMap = BurberryProductBOMAPIDataExtraction.getModifiedProductIds(mapDeltaDate);
			logger.debug(methodName + "Product BOM API Delta Criteria Map: " + deltaCriteriaMap);
		} else if (STR_SAMPLE_API.equals(apitype)) {
			deltaCriteriaMap = BurberrySampleAPIDataExtraction.getModifiedSampleRequestIds(mapDeltaDate, bProductDelta,
					bMaterialDelta);
			logger.debug(methodName + "Sample API Delta Criteria Map: " + deltaCriteriaMap);
		} else if (STR_PRODUCT_COSTING_API.equals(apitype)) {
			deltaCriteriaMap = BurberryProductCostingAPIDataExtraction.getModifiedProductIds(mapDeltaDate);
			logger.debug(methodName + "Product Costing API Delta Criteria Map: " + deltaCriteriaMap);
		} else if (STR_PLANNING_API.equals(apitype)) {
			deltaCriteriaMap = BurberryPlanningAPIDataExtraction.getModifiedPlanIds(mapDeltaDate);
			logger.debug(methodName + "Planning API Delta Criteria Map: " + deltaCriteriaMap);
		}
		return deltaCriteriaMap;
	}

	/**
	 * Method get API based Query Criteria.
	 * 
	 * @param apitype           API Type
	 * @param strObjectName     Object Name
	 * @param strAttDisplayName Display Name
	 * @param strAttValue       Att Value
	 * @param mapValidObjects   Map
	 * @return Collection
	 * @throws InvalidInputException Exception
	 * @throws WTException           Exception
	 * @throws BurException          Exception
	 * @throws ParseException        Exception
	 */
	private static Collection<Map> getAPIBasedQueryCritera(String apitype, String strObjectName,
			String strAttDisplayName, String strAttValue, Map<String, String> mapValidObjects)
					throws InvalidInputException, WTException, BurException, ParseException {
		String methodName = "getAPIBasedQueryCritera() ";

		// Initialisation of criteria map
		Collection<Map> criteriaMap = new ArrayList<Map>();

		// Product API
		if (STR_PRODUCT_API.equals(apitype)) {
			// Get Product API Query Statement's Search Criteria
			criteriaMap = BurberryProductAPIDataExtraction.getObjectBasedQueryCriteria(strObjectName, strAttDisplayName,
					strAttValue, mapValidObjects);
			logger.debug(methodName + "Product API Criteria Map: " + criteriaMap);
		}
		// Palette Material API
		else if (STR_PALETTE_MATERIAL_API.equals(apitype)) {
			// Get Palette Material API Query Statement's Search
			// Criteria
			criteriaMap = BurberryPaletteMaterialAPIDataExtraction.getObjectBasedQueryCriteria(strObjectName,
					strAttDisplayName, strAttValue, mapValidObjects);
			logger.debug(methodName + "Palette Material API Criteria Map: " + criteriaMap);
		} else if (STR_PRODUCT_BOM_API.equals(apitype)) {
			// Get Palette Material API Query Statement's Search
			// Criteria
			criteriaMap = BurberryProductBOMAPIDataExtraction.getObjectBasedQueryCriteria(strObjectName,
					strAttDisplayName, strAttValue, mapValidObjects);
			logger.debug(methodName + "Product BOM API Criteria Map: " + criteriaMap);
		} else if (STR_SAMPLE_API.equals(apitype)) {
			// Get Sample API Query Statement's Search
			// Criteria
			criteriaMap = BurberrySampleAPIDataExtraction.getObjectBasedQueryCriteria(strObjectName, strAttDisplayName,
					strAttValue, mapValidObjects);
			logger.debug(methodName + "Sample API Criteria Map: " + criteriaMap);
		} else if (STR_PRODUCT_COSTING_API.equals(apitype)) {
			// Get Product Costing API Query Statement's Search
			// Criteria
			criteriaMap = BurberryProductCostingAPIDataExtraction.getObjectBasedQueryCriteria(strObjectName,
					strAttDisplayName, strAttValue, mapValidObjects);
			logger.debug(methodName + "Product Costing API Criteria Map: " + criteriaMap);
		} else if (STR_PLANNING_API.equals(apitype)) {
			// Get Planning API Query Statement's Search
			// Criteria
			criteriaMap = BurberryPlanningAPIDataExtraction.getObjectBasedQueryCriteria(strObjectName,
					strAttDisplayName, strAttValue, mapValidObjects);
			logger.debug(methodName + "Planning API Criteria Map: " + criteriaMap);
		}
		return criteriaMap;
	}

	/**
	 * @param sourceObject
	 * @param seasonMaster
	 * @param productObj
	 * @return
	 * @throws WTException
	 */
	public static Collection<FlexSpecToSeasonLink> getAllSpecToSeasonLinks(LCSSourcingConfig sourceObject,
			LCSSeasonMaster seasonMaster, LCSProduct productObj) throws WTException {

		String methodName = "getAllSpecToSeasonLinks()";

		Collection<FlexSpecToSeasonLink> colFlexSpecToSeasonLinks = new ArrayList<FlexSpecToSeasonLink>();
		// Get all Spec with season Links
		SearchResults searchResult = LCSQuery.runDirectQuery(FlexSpecQuery.findExistingSpecsWithSeasonLinksQuery(
				productObj, (LCSSeason) VersionHelper.latestIterationOf(seasonMaster),
				(LCSSourcingConfigMaster) sourceObject.getMaster()));

		Iterator specIter = LCSQuery.getObjectsFromResults(searchResult,
				"OR:com.lcs.wc.specification.FlexSpecToSeasonLink:", "FLEXSPECTOSEASONLINK.IDA2A2").iterator();
		while (specIter.hasNext()) {
			FlexSpecToSeasonLink specLink = (FlexSpecToSeasonLink) specIter.next();
			// check if link is primary
			// if (specLink.isPrimarySpec()) {
			// FlexSpecification associatedSpecification = (FlexSpecification)
			// VersionHelper
			// .latestIterationOf(specLink.getSpecificationMaster());
			colFlexSpecToSeasonLinks.add(specLink);
			// }

		}

		logger.debug(methodName + "Returing collection of Spec To Season Links " + colFlexSpecToSeasonLinks);
		// Returning Collection Specification
		return colFlexSpecToSeasonLinks;
	}

	/**
	 * Method to throw exception.
	 * 
	 * @param param string and message
	 * @param msg
	 * @throws BurException
	 */
	public static void throwBurException(String param, String msg) throws BurException {
		throw new BurException(" " + param + " " + msg);
	}

	/**
	 * Method to get JSON Mapping.
	 * 
	 * @param attKeys StringTokenizer
	 * @return Map
	 */
	public static Map<String, String> getJsonMapping(String attrString) {
		// Get all the attributes keys
		final StringTokenizer attKeys = new StringTokenizer(attrString, BurConstant.STRING_COMMA);
		Map<String, String> jsonMapping = new HashMap<String, String>();
		String methodName = "getJsonMapping() ";
		while (attKeys.hasMoreTokens()) {
			String pair = attKeys.nextToken();
			String[] keyValue = pair.split("_alias_");
			logger.debug(methodName + " attKey=" + keyValue[0]);
			logger.debug(methodName + " jsonKey=" + keyValue[1]);
			jsonMapping.put(keyValue[0], keyValue[1]);
		}
		// Return Statement
		return jsonMapping;
	}

	// CR R26: Handle Remove Object Customisation : Start
	/**
	 * Method to get database column name
	 * 
	 * @param moaTable String
	 * @param moaName  String
	 * @return String
	 * @throws WTException Exception
	 */
	public static String getObjectColumnName(String moaTable, String moaColumnName) throws WTException {

		String methodName = "getObjectColumnName() ";
		String strObjectColumnName = BurConstant.STRING_EMPTY;

		// Track Palette Deletions
		if (moaTable.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_PALATTE_MATERIAL_COLOR_NAME)) {
			logger.debug(methodName + "Track Palette Deletions Object Name: " + moaTable);
			FlexType moaTrackImagePageFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurPaletteMaterialConstant.MOA_TRACK_PALATTE_MATERIAL_COLOR_FLEX_TYPE);
			strObjectColumnName = moaTrackImagePageFlexType.getAttribute(moaColumnName).getColumnName();
		}

		// Track Image Page
		else if (moaTable.equalsIgnoreCase(BurProductConstant.BO_TRACK_SPECIFICATION_IMAGE_PAGE_NAME)) {
			logger.debug(methodName + "Track Image Object Name: " + moaTable);
			FlexType moaTrackImagePageFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurProductConstant.MOA_TRACK_SPECIFICATION_IMAGE_PAGE_FLEXTYPE);
			strObjectColumnName = moaTrackImagePageFlexType.getAttribute(moaColumnName).getColumnName();
		}
		// Track Image From Image Page
		else if (moaTable.equalsIgnoreCase(BurProductConstant.BO_TRACK_IMAGE_FROM_IMAGE_PAGE_NAME)) {
			logger.debug(methodName + "Track Image From Image Page Object Name: " + moaTable);
			FlexType moaTrackImageFromImagePageFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurProductConstant.MOA_TRACK_IMAGE_FROM_IMAGE_PAGE_FLEXTYPE);
			strObjectColumnName = moaTrackImageFromImagePageFlexType.getAttribute(moaColumnName).getColumnName();
		}

		// Track Risk Management
		else if (moaTable.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_RISK_MANAGEMENT_NAME)) {
			logger.debug(methodName + "Track Risk Managment Object Name: " + moaTable);
			FlexType moaTrackRiskManagementFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurPaletteMaterialConstant.MOA_TRACK_RISK_MANAGEMENT_FLEX_TYPE);
			strObjectColumnName = moaTrackRiskManagementFlexType.getAttribute(moaColumnName).getColumnName();
		}
		// Track Yarn Details
		else if (moaTable.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_YARN_DETAILS_NAME)) {
			logger.debug(methodName + "Track Yarn Details Object Name: " + moaTable);
			FlexType moaTrackYarnDetailsFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurPaletteMaterialConstant.MOA_TRACK_YARN_DETAILS_FLEX_TYPE);
			strObjectColumnName = moaTrackYarnDetailsFlexType.getAttribute(moaColumnName).getColumnName();
		}
		// Track Material Price Management
		else if (moaTable.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_MATERIAL_PRICE_MANAGEMENT_NAME)) {
			logger.debug(methodName + "Track Price Management Object Name: " + moaTable);
			FlexType moaTrackMatPriceMgmtFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_PRICE_MANAGEMENT_FLEX_TYPE);
			strObjectColumnName = moaTrackMatPriceMgmtFlexType.getAttribute(moaColumnName).getColumnName();
		}
		// Track Material Price Entry
		else if (moaTable.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_MATERIAL_PRICING_ENTRY_NAME)) {
			logger.debug(methodName + "Track Material Price Entry Object Name: " + moaTable);
			FlexType moaTrackMatPriceEntryFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_PRICING_ENTRY_FLEX_TYPE);
			strObjectColumnName = moaTrackMatPriceEntryFlexType.getAttribute(moaColumnName).getColumnName();
		} else {
			strObjectColumnName = getObjectColumnNameForOthers(moaTable, moaColumnName);
		}
		return strObjectColumnName;
	}

	// CR R26: Handle Remove Object Customisation : End

	/**
	 * @param moaTable
	 * @param moaColumnName
	 * @return
	 * @throws WTException
	 */
	private static String getObjectColumnNameForOthers(String moaTable, String moaColumnName) throws WTException {

		String methodName = "getObjectColumnNameForOthers() ";
		String strObjectColumnName = BurConstant.STRING_EMPTY;

		// Track Material Document
		if (moaTable.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_MATERIAL_DOCUMENT_NAME)) {
			logger.debug(methodName + "Track Material Document Object Name: " + moaTable);
			FlexType moaTrackMatDocFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_DOCUMENT_FLEX_TYPE);
			strObjectColumnName = moaTrackMatDocFlexType.getAttribute(moaColumnName).getColumnName();
		}
		// BURBERRY-1485 RD 74: Material Supplier Documents - Start
		// Track Material Supplier Document
		else if (moaTable.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_MATERIAL_SUPPLIER_DOCUMENT_NAME)) {
			logger.debug(methodName + "Track Material Supplier Document Object Name: " + moaTable);
			FlexType moaTrackMatSuppDocFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_SUPPLIER_DOCUMENT_FLEX_TYPE);
			strObjectColumnName = moaTrackMatSuppDocFlexType.getAttribute(moaColumnName).getColumnName();
		}
		// BURBERRY-1485 RD 74: Material Supplier Documents - End
		// Track BOM
		else if (moaTable.equalsIgnoreCase(BurProductBOMConstant.BO_TRACK_BOM_NAME)) {
			logger.debug(methodName + "Track BOM Object Name: " + moaTable);
			FlexType moaTrackBOMFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurProductBOMConstant.MOA_TRACK_BOM_FLEX_TYPE);
			strObjectColumnName = moaTrackBOMFlexType.getAttribute(moaColumnName).getColumnName();
		}
		// Track Cost Sheet Document
		else if (moaTable.equalsIgnoreCase(BurProductCostingConstant.BO_TRACK_COST_SHEET_NAME)) {
			logger.debug(methodName + "Track Cost Sheet Object Name: " + moaTable);
			FlexType moaTrackCostSheetFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurProductCostingConstant.MOA_TRACK_COST_SHEET_FLEX_TYPE);
			strObjectColumnName = moaTrackCostSheetFlexType.getAttribute(moaColumnName).getColumnName();
		}
		// Track Product Specification
		else if (moaTable.equalsIgnoreCase(BurProductConstant.BO_TRACK_SPECIFICATION_NAME)) {
			logger.debug(methodName + "Track Product Specification Name: " + moaTable);
			FlexType moaTrackCostSheetFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurProductConstant.MOA_TRACK_SPECIFICATION_FLEX_TYPE);
			strObjectColumnName = moaTrackCostSheetFlexType.getAttribute(moaColumnName).getColumnName();
		}

		/////////////////////////////////////////////////  L2 Changes Start  ///////////////////////////////////////////////////////////////////////////////////

		// Get Product Specification ID
		/*else if (moaTable.equalsIgnoreCase(BurProductConstant.BO_TRACK_SPECIFICATION_NAME)) {
			logger.debug(methodName + "Track Product Specification ID: " + moaTable);
			FlexType moaTrackSpecificationFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurProductConstant.MOA_TRACK_SPECIFICATION_FLEX_TYPE);
			strObjectColumnName = moaTrackSpecificationFlexType.getAttribute(moaColumnName).getColumnName();
		}*/

		///////////////////////////////////////////////////  L2 Changes END    ///////////////////////////////////////////////////////////////////////////////

		// Track Product Specification Document
		else if (moaTable.equalsIgnoreCase(BurProductConstant.BO_TRACK_SPECIFICATION_DOCUMENT_NAME)) {
			logger.debug(methodName + "Track Product Specification Document Name: " + moaTable);
			FlexType moaTrackDocumentFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurProductConstant.MOA_TRACK_SPECIFICATION_DOC_FLEX_TYPE);
			strObjectColumnName = moaTrackDocumentFlexType.getAttribute(moaColumnName).getColumnName();
		}

		return strObjectColumnName;
	}

	// CR R26: Handle Remove Object Customisation : Start
	/**
	 * Method to get Flex Type Id Path
	 * 
	 * @param moaTableName String
	 * @param moaName      String
	 * @return String
	 * @throws WTException Exception
	 */
	public static String getFlexTypeIdPath(String moaTableName) throws WTException {

		// Method Name
		String methodName = "getFlexTypeIdPath() ";
		String strFlexTypeIdPath = BurConstant.STRING_EMPTY;

		// Track Palette Deletions
		if (moaTableName.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_PALATTE_MATERIAL_COLOR_NAME)) {
			logger.debug(methodName + " Track Palette Deletions Name: " + moaTableName);
			FlexType moaTrackPaletteFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurPaletteMaterialConstant.MOA_TRACK_PALATTE_MATERIAL_COLOR_FLEX_TYPE);
			strFlexTypeIdPath = moaTrackPaletteFlexType.getTypeIdPath();
		}

		// Track Image Page
		else if (moaTableName.equalsIgnoreCase(BurProductConstant.BO_TRACK_SPECIFICATION_IMAGE_PAGE_NAME)) {
			logger.debug(methodName + " Track Image Page Name: " + moaTableName);
			FlexType moaTrackImgPageFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurProductConstant.MOA_TRACK_SPECIFICATION_IMAGE_PAGE_FLEXTYPE);
			strFlexTypeIdPath = moaTrackImgPageFlexType.getTypeIdPath();
		}

		// Track Image From Image Page
		else if (moaTableName.equalsIgnoreCase(BurProductConstant.BO_TRACK_IMAGE_FROM_IMAGE_PAGE_NAME)) {
			logger.debug(methodName + " Track Image From Image Page Name: " + moaTableName);
			FlexType moaTrackImgFromImgPageFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurProductConstant.MOA_TRACK_IMAGE_FROM_IMAGE_PAGE_FLEXTYPE);
			strFlexTypeIdPath = moaTrackImgFromImgPageFlexType.getTypeIdPath();
		}

		// Track Risk Management
		else if (moaTableName.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_RISK_MANAGEMENT_NAME)) {
			logger.debug(methodName + " Track Risk Management Name: " + moaTableName);
			FlexType moaTrackRiskMgmtFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurPaletteMaterialConstant.MOA_TRACK_RISK_MANAGEMENT_FLEX_TYPE);
			strFlexTypeIdPath = moaTrackRiskMgmtFlexType.getTypeIdPath();
		}

		// Track Yarn Details
		else if (FormatHelper.hasContent(moaTableName)
				&& moaTableName.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_YARN_DETAILS_NAME)) {
			logger.debug(methodName + " Track Yarn Details Name: " + moaTableName);
			FlexType moaTrackYarnDetailsFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurPaletteMaterialConstant.MOA_TRACK_YARN_DETAILS_FLEX_TYPE);
			strFlexTypeIdPath = moaTrackYarnDetailsFlexType.getTypeIdPath();
		}

		// Track Material Price Management
		else if (moaTableName.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_MATERIAL_PRICE_MANAGEMENT_NAME)) {
			logger.debug(methodName + " Track Material Price Managment Name: " + moaTableName);
			FlexType moaTrackMatPriceMgmtFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_PRICE_MANAGEMENT_FLEX_TYPE);
			strFlexTypeIdPath = moaTrackMatPriceMgmtFlexType.getTypeIdPath();
		}

		// Track Material Price Entry
		else if (moaTableName.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_MATERIAL_PRICING_ENTRY_NAME)) {
			logger.debug(methodName + " Track Materia Pricing Name: " + moaTableName);
			FlexType moaTrackMaterialPriceEntryFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_PRICING_ENTRY_FLEX_TYPE);
			strFlexTypeIdPath = moaTrackMaterialPriceEntryFlexType.getTypeIdPath();
		} else {
			strFlexTypeIdPath = getFlexTypeIdPathForOthers(moaTableName);
		}

		return strFlexTypeIdPath;
	}

	/**
	 * @param moaTableName
	 * @return
	 * @throws WTException
	 */
	public static String getFlexTypeIdPathForOthers(String moaTableName) throws WTException {
		// Method Name
		String methodName = "getFlexTypeIdPathForOthers() ";
		String strFlexTypeIdPath = BurConstant.STRING_EMPTY;
		// Track Product Specification
		if (moaTableName.equalsIgnoreCase(BurProductConstant.BO_TRACK_SPECIFICATION_NAME)) {
			logger.debug(methodName + " Track SPECIFICATION Name: " + moaTableName);
			FlexType moaTrackSpecFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurProductConstant.MOA_TRACK_SPECIFICATION_FLEX_TYPE);
			return moaTrackSpecFlexType.getTypeIdPath();
		}
		// Track BOM
		else if (moaTableName.equalsIgnoreCase(BurProductBOMConstant.BO_TRACK_BOM_NAME)) {
			logger.debug(methodName + " Track BOM Name: " + moaTableName);
			FlexType moaTrackBOMFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurProductBOMConstant.MOA_TRACK_BOM_FLEX_TYPE);
			strFlexTypeIdPath = moaTrackBOMFlexType.getTypeIdPath();
		}

		// Track Material Document
		else if (moaTableName.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_MATERIAL_DOCUMENT_NAME)) {
			logger.debug(methodName + " Track Material Document Name: " + moaTableName);
			FlexType moaTrackMaterialDocFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_DOCUMENT_FLEX_TYPE);
			strFlexTypeIdPath = moaTrackMaterialDocFlexType.getTypeIdPath();
		}
		// BURBERRY-1485 RD 74: Material Supplier Documents - Start
		// Track Material Supplier Document
		else if (moaTableName.equalsIgnoreCase(BurPaletteMaterialConstant.BO_TRACK_MATERIAL_SUPPLIER_DOCUMENT_NAME)) {
			logger.debug(methodName + " Track Material Supplier Document Name: " + moaTableName);
			FlexType moaTrackMaterialSuppDocFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurPaletteMaterialConstant.MOA_TRACK_MATERIAL_SUPPLIER_DOCUMENT_FLEX_TYPE);
			strFlexTypeIdPath = moaTrackMaterialSuppDocFlexType.getTypeIdPath();
		}
		// BURBERRY-1485 RD 74: Material Supplier Documents - End
		// Track Product Cost Sheet
		else if (moaTableName.equalsIgnoreCase(BurProductCostingConstant.BO_TRACK_COST_SHEET_NAME)) {
			logger.debug(methodName + " Track Cost Sheet Name: " + moaTableName);
			FlexType moaTrackCostSheetFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurProductCostingConstant.MOA_TRACK_COST_SHEET_FLEX_TYPE);
			strFlexTypeIdPath = moaTrackCostSheetFlexType.getTypeIdPath();
		} else if (moaTableName.equalsIgnoreCase(BurProductConstant.BO_TRACK_SPECIFICATION_DOCUMENT_NAME)) {
			logger.debug(methodName + " Track Document Name: " + moaTableName);
			FlexType moaTrackSpecDocFlexType = FlexTypeCache
					.getFlexTypeFromPath(BurProductConstant.MOA_TRACK_SPECIFICATION_DOC_FLEX_TYPE);
			strFlexTypeIdPath = moaTrackSpecDocFlexType.getTypeIdPath();
		}
		return strFlexTypeIdPath;
	}

	/**
	 * Method to compare and remove duplicate objects
	 * 
	 * @param associatedObjects List<String>
	 * @param lstRemovedObjects List<String>
	 * @return List <String>
	 */

	public static List<String> compareAndRemoveSameObjects(List<String> associatedObjects,
			List<String> lstRemovedObjects) {
		// Method Name
		String methodName = "compareAndRemoveDuplicateObjects() ";
		// Prepare an intersection
		List<String> intersection = new ArrayList<String>(associatedObjects);
		intersection.retainAll(lstRemovedObjects);
		// Subtract the intersection from the union
		lstRemovedObjects.removeAll(intersection);
		logger.debug(methodName + "Final Removed List: " + lstRemovedObjects);
		// Return the list
		return lstRemovedObjects;
	}

	/**
	 * Method to get removed palette data
	 * 
	 * @param criteria     Collection
	 * @param moaTableName String
	 * @return Map
	 * @throws WTException Exception
	 */
	public static Map<String, Collection<HashMap>> getPaletteDeletionsMapData(Collection<Map> criteria,
			String moaTableName) throws WTException {

		// Method Name
		String methodName = "getPaletteDeletionsMapData() ";
		// Initialisation
		Map<String, Collection<HashMap>> trackPaletteDeletionsMap = new HashMap<String, Collection<HashMap>>();

		// Loop through criteria map
		for (Map<String, String> resultMap : criteria) {
			// Get Flex Type Path
			String flexTypePath = BurberryAPIUtil.getFlexTypeIdPath(moaTableName);
			logger.debug(methodName + "Track Palette Flex Type Path: " + flexTypePath);
			// Get Material Colour Column
			String materialColourColumn = BurberryAPIUtil.getObjectColumnName(
					BurPaletteMaterialConstant.BO_TRACK_PALATTE_MATERIAL_COLOR_NAME,
					BurPaletteMaterialConstant.MOA_TRACK_PALATTE_MATERIAL_COLOR_ATT_MATERIAL_COLOR_ID);
			logger.debug(methodName + "materialColourColumn: " + materialColourColumn);
			// Get Palette Name Column
			String paletteNameColumn = BurberryAPIUtil.getObjectColumnName(
					BurPaletteMaterialConstant.BO_TRACK_PALATTE_MATERIAL_COLOR_NAME,
					BurPaletteMaterialConstant.MOA_TRACK_PALATTE_MATERIAL_COLOR_ATT_PALETTE_NAME);
			logger.debug(methodName + "paletteNameColumn: " + BurPaletteMaterialConstant.MOA_TRACK_SUPPLIER_NAME);

			// Get Supplier Column
			String materialSupplierColumn = BurberryAPIUtil.getObjectColumnName(
					BurPaletteMaterialConstant.BO_TRACK_PALATTE_MATERIAL_COLOR_NAME,
					BurPaletteMaterialConstant.MOA_TRACK_SUPPLIER_NAME);
			logger.debug(methodName + "materialSupplierColumn: " + materialSupplierColumn);
			// Get Track Palette Flex Type Path
			String trackedPaletteFlexTypeId = String.valueOf(resultMap.get(BurConstant.LCSMOAOBJ_FLEXTYPEID_PATH));
			logger.debug(methodName + "trackedPaletteFlexTypeId: " + trackedPaletteFlexTypeId);
			// If it has MOA Object and Track Palette Flex Type
			if (FormatHelper.hasContent(String.valueOf(resultMap.get(BurConstant.STR_LCSMOAOBJ_IDA2A2)))
					&& flexTypePath.equals(trackedPaletteFlexTypeId)) {
				Collection<HashMap> colRemovedPaletteData = new ArrayList<HashMap>();
				HashMap hmRemovedPaletteData = new HashMap();
				// Put Material Colour
				hmRemovedPaletteData.put("MATERIAL_COLOUR_ID", String
						.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + materialColourColumn.toUpperCase())));
				// Put Palette Name
				hmRemovedPaletteData.put("PALETTE_NAME", String
						.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + paletteNameColumn.toUpperCase())));
				//put Supplier Name
				if(FormatHelper.hasContent(resultMap.get(BurConstant.LCSMOAOBJECT + "." + materialSupplierColumn.toUpperCase()))){
					hmRemovedPaletteData.put("SUPPLIER_NAME", String
							.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + materialSupplierColumn.toUpperCase())));
				}
				logger.debug(methodName + "hmRemovedPaletteData: " + hmRemovedPaletteData);
				// Add to collection
				colRemovedPaletteData.add(hmRemovedPaletteData);
				// Check if same material exists
				if (trackPaletteDeletionsMap
						.containsKey(resultMap.get(BurPaletteMaterialConstant.LCSMATERIAL_BRANCHIDITERATIONINFO))) {
					colRemovedPaletteData.addAll(trackPaletteDeletionsMap
							.get(resultMap.get(BurPaletteMaterialConstant.LCSMATERIAL_BRANCHIDITERATIONINFO)));
				}
				// Put Material Id
				trackPaletteDeletionsMap.put(
						resultMap.get(BurPaletteMaterialConstant.LCSMATERIAL_BRANCHIDITERATIONINFO),
						colRemovedPaletteData);
			}
		}
		logger.debug(methodName + "Track Palette Deletion Map: " + trackPaletteDeletionsMap);
		// Return
		return trackPaletteDeletionsMap;

	}

	/**
	 * Method to get removed moa rows
	 * 
	 * @param criteria         Collection
	 * @param ownerBranchId    String
	 * @param moaTableName     String
	 * @param moaOwnerIdColumn String
	 * @param moaObjectId      String
	 * @return Map
	 * @throws WTException Exception
	 */
	public static Map<String, Collection<HashMap>> getRemovedMOARowsDeleted(Collection<Map> criteria,
			String ownerBranchId, String moaTableName, String moaOwnerIdColumn, String moaObjectId) throws WTException {

		// Method Name
		String methodName = "getRemovedMOARowsDeleted() ";
		// Initialisation
		Map<String, Collection<HashMap>> trackMaterialMOARowsDeletionsMap = new HashMap<String, Collection<HashMap>>();

		logger.debug(methodName + "MOA TABLE NAME: " + moaTableName);

		// Get Flex Type Path
		String moaFlexTypeIdPath = BurberryAPIUtil.getFlexTypeIdPath(moaTableName);
		logger.debug(methodName + "Flex Type Path: " + moaFlexTypeIdPath);
		// Get Owner Id Column
		String ownerIdColumn = BurberryAPIUtil.getObjectColumnName(moaTableName, moaOwnerIdColumn).toUpperCase();
		logger.debug(methodName + "ownerIdColumn: " + ownerIdColumn);
		// Get the Object ID DB Column
		String strObjectIdColumnName = BurberryAPIUtil.getObjectColumnName(moaTableName, moaObjectId).toUpperCase();
		logger.debug(methodName + "strObjectIdColumnName: " + strObjectIdColumnName);
		// Loop through criteria map
		for (Map<String, String> resultMap : criteria) {
			// Get Track MOA Type Path
			String trackMOAFlexTypeId = String.valueOf(resultMap.get(BurConstant.LCSMOAOBJ_FLEXTYPEID_PATH));
			logger.debug(methodName + "trackMOAFlexTypeId: " + trackMOAFlexTypeId);
			// If it has MOA Object and Track Palette Flex Type
			if (FormatHelper.hasContent(String.valueOf(resultMap.get(BurConstant.STR_LCSMOAOBJ_IDA2A2)))
					&& moaFlexTypeIdPath.equals(trackMOAFlexTypeId)) {
				Collection<HashMap> colRemovedMOARows = new ArrayList<HashMap>();
				HashMap hmRemovedMOAData = new HashMap();
				logger.debug(methodName + "Result Map: " + resultMap);
				// Put Owner Id
				hmRemovedMOAData.put("OWNER_ID",
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + ownerIdColumn)));
				// Put MOA Object Id
				hmRemovedMOAData.put("MOA_OBJECT_ID",
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strObjectIdColumnName)));
				logger.debug(methodName + "hmRemovedMOAData: " + hmRemovedMOAData);
				// Add to collection
				colRemovedMOARows.add(hmRemovedMOAData);
				// Check if same material exists
				if (trackMaterialMOARowsDeletionsMap.containsKey(resultMap.get(ownerBranchId))) {
					colRemovedMOARows.addAll(trackMaterialMOARowsDeletionsMap.get(resultMap.get(ownerBranchId)));
				}
				// Put Material Id
				trackMaterialMOARowsDeletionsMap.put(resultMap.get(ownerBranchId), colRemovedMOARows);
			}
		}
		logger.debug(methodName + "Track MOA Row Deletion Map: " + trackMaterialMOARowsDeletionsMap);
		// Return
		return trackMaterialMOARowsDeletionsMap;
	}

	/**
	 * Method to get removed Image page data
	 * 
	 * @param criteria                Collection
	 * @param ownerBranchId           String
	 * @param moaTableName            String
	 * @param moaTrackProductId       String
	 * @param moaTrackSourcingId      String
	 * @param moaTrackSpecificationId String
	 * @param moaTrackImagePageName   String
	 * @return Map
	 * @throws WTException Exception
	 */
	public static Map<String, Collection<HashMap>> getRemovedImagePageData(Collection<Map> criteria,
			String ownerBranchId, String moaTableName, String moaTrackProductId, String moaTrackSourcingId,
			String moaTrackSpecificationId, String moaTrackImagePageName) throws WTException {

		// Method Name
		String methodName = "getRemovedImagePageData() ";
		// Initialisation
		Map<String, Collection<HashMap>> trackImageDeletionsMap = new HashMap<String, Collection<HashMap>>();
		// Loop through criteria map
		for (Map<String, String> resultMap : criteria) {
			// Get Flex Type Path
			String moaImagePageFlexType = BurberryAPIUtil.getFlexTypeIdPath(moaTableName);
			logger.debug(methodName + "Track Image Page Flex Type Path: " + moaImagePageFlexType);
			// Get Source Column Name
			String strSourceIdColumnName = BurberryAPIUtil
					.getObjectColumnName(BurProductConstant.BO_TRACK_SPECIFICATION_IMAGE_PAGE_NAME,
							BurProductConstant.MOA_TRACK_SOURCING_ID)
					.toUpperCase();
			logger.debug(methodName + "MOA Source Config Id Column Name: " + strSourceIdColumnName);
			// Get Specification Column Name
			String strSpecIdColumnName = BurberryAPIUtil
					.getObjectColumnName(BurProductConstant.BO_TRACK_SPECIFICATION_IMAGE_PAGE_NAME,
							BurProductConstant.MOA_TRACK_SPECIFICATION_ID)
					.toUpperCase();
			logger.debug(methodName + "MOA Spec Id Column Name: " + strSpecIdColumnName);
			// Get Image Page Name Database Column Name
			String strImagePageNameColumnName = BurberryAPIUtil
					.getObjectColumnName(BurProductConstant.BO_TRACK_SPECIFICATION_IMAGE_PAGE_NAME,
							BurProductConstant.MOA_TRACK_IMAGE_PAGE_NAME)
					.toUpperCase();
			logger.debug(methodName + "MOA Image Page Column Name: " + strImagePageNameColumnName);
			// Get Track Image Page Flex Type Path
			String trackedImagePageFlexTypeId = String.valueOf(resultMap.get(BurConstant.LCSMOAOBJ_FLEXTYPEID_PATH));
			logger.debug(methodName + "trackedImagePageFlexTypeId: " + trackedImagePageFlexTypeId);
			// If it has MOA Object and Track Image Page Type
			if (FormatHelper.hasContent(String.valueOf(resultMap.get(BurConstant.STR_LCSMOAOBJ_IDA2A2)))
					&& moaImagePageFlexType.equals(trackedImagePageFlexTypeId)) {
				Collection<HashMap> colRemovedImagePageData = new ArrayList<HashMap>();
				HashMap hmRemovedImagePageData = new HashMap();
				// Put Source Id
				hmRemovedImagePageData.put(STR_SOURCE_ID,
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strSourceIdColumnName)));
				// Put Specification Id
				hmRemovedImagePageData.put("SPECIFICATION_ID",
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strSpecIdColumnName)));
				// Put Image Page Name
				hmRemovedImagePageData.put("IMAGE_PAGE_NAME",
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strImagePageNameColumnName)));
				logger.debug(methodName + "hmRemovedImagePageData: " + hmRemovedImagePageData);
				// Add to collection
				colRemovedImagePageData.add(hmRemovedImagePageData);

				// Check if same product exists
				if (trackImageDeletionsMap.containsKey(resultMap.get(ownerBranchId))) {
					colRemovedImagePageData.addAll(trackImageDeletionsMap.get(resultMap.get(ownerBranchId)));
				}
				// Put Product Id
				trackImageDeletionsMap.put(resultMap.get(ownerBranchId), colRemovedImagePageData);
			}
		}
		logger.debug(methodName + "Track Image Page Deletion Map: " + trackImageDeletionsMap);
		// Return
		return trackImageDeletionsMap;

	}

	// CR R26: Handle Remove Object Customisation : End

	@SuppressWarnings("unchecked")
	public static Map<String, Collection<HashMap>> getBOMDeletionsMapData(Collection<Map> criteria,
			String ownerBranchId, String moaTableName, String moaTrackProductId, String moaTrackSourcingId,
			String moaTrackSpecificationId, String moaTrackBOMPartId, String moaTrackBOMName) throws WTException {

		// Method Name
		String methodName = "getBOMDeletionsMapData() ";
		// Initialisation
		Map<String, Collection<HashMap>> trackBOMDeletionsMap = new HashMap<String, Collection<HashMap>>();
		// Loop through criteria map
		for (Map<String, String> resultMap : criteria) {
			// Get Flex Type Path
			String moaBOMFlexType = BurberryAPIUtil.getFlexTypeIdPath(moaTableName);
			logger.debug(methodName + "Track BOM Flex Type Path: " + moaBOMFlexType);
			// Get Source Column Name
			String strSourceIdColumnName = BurberryAPIUtil.getObjectColumnName(BurProductBOMConstant.BO_TRACK_BOM_NAME,
					BurProductConstant.MOA_TRACK_SOURCING_ID).toUpperCase();
			logger.debug(methodName + "MOA Source Id Column Name: " + strSourceIdColumnName);
			// Get Specification Column Name
			String strSpecIdColumnName = BurberryAPIUtil.getObjectColumnName(BurProductBOMConstant.BO_TRACK_BOM_NAME,
					BurProductConstant.MOA_TRACK_SPECIFICATION_ID).toUpperCase();
			logger.debug(methodName + "MOA Spec Id Column Name: " + strSpecIdColumnName);

			// BURBERRY-1420: BOM header Unique ID in CRUD Flag for BOM API Output - Start
			// Get BOM Part Id Database Column Name
			String strBOMPartIdColumnName = BurberryAPIUtil.getObjectColumnName(BurProductBOMConstant.BO_TRACK_BOM_NAME,
					BurProductBOMConstant.MOA_TRACK_BOM_PART_ID).toUpperCase();
			logger.debug(methodName + "MOA BOM Part Id Column Name: " + strBOMPartIdColumnName);
			// BURBERRY-1420: BOM header Unique ID in CRUD Flag for BOM API Output - End

			// Get BOM Name Database Column Name
			String strBOMNameColumnName = BurberryAPIUtil.getObjectColumnName(BurProductBOMConstant.BO_TRACK_BOM_NAME,
					BurProductBOMConstant.MOA_TRACK_BOM_NAME).toUpperCase();
			logger.debug(methodName + "MOA BOM Column Name: " + strBOMNameColumnName);
			// Get Track BOM Flex Type Path
			String trackedBOMFlexTypeId = String.valueOf(resultMap.get(BurConstant.LCSMOAOBJ_FLEXTYPEID_PATH));
			logger.debug(methodName + "trackedBOMFlexTypeId: " + trackedBOMFlexTypeId);
			// If it has MOA Object and Track Image Page Type
			if (FormatHelper.hasContent(String.valueOf(resultMap.get(BurConstant.STR_LCSMOAOBJ_IDA2A2)))
					&& moaBOMFlexType.equals(trackedBOMFlexTypeId)) {
				Collection<HashMap> colRemovedBOMData = new ArrayList<HashMap>();
				HashMap hmRemovedBOMData = new HashMap();
				// Put Source Id
				hmRemovedBOMData.put(STR_SOURCE_ID,
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strSourceIdColumnName)));
				// Put Specification Id
				hmRemovedBOMData.put("SPECIFICATION_ID",
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strSpecIdColumnName)));
				// BURBERRY-1420:BOM header Unique ID in CRUD Flag for BOM API Output - Start
				// Put BOM Part Id
				hmRemovedBOMData.put("BOM_PART_ID",
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strBOMPartIdColumnName)));
				// BURBERRY-1420:BOM header Unique ID in CRUD Flag for BOM API Output - End
				// Put BOM Name
				hmRemovedBOMData.put("BOM_NAME",
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strBOMNameColumnName)));
				logger.debug(methodName + "hmRemovedBOMData: " + hmRemovedBOMData);
				// Add to collection
				colRemovedBOMData.add(hmRemovedBOMData);

				// Check if same product exists
				if (trackBOMDeletionsMap.containsKey(resultMap.get(ownerBranchId))) {
					colRemovedBOMData.addAll(trackBOMDeletionsMap.get(resultMap.get(ownerBranchId)));
				}
				// Put Product Id
				trackBOMDeletionsMap.put(resultMap.get(ownerBranchId), colRemovedBOMData);
			}
		}
		logger.debug(methodName + "Track BOM Deletion Map: " + trackBOMDeletionsMap);
		// Return
		return trackBOMDeletionsMap;
	}

	/**
	 * Method to get removed Image From Image Page data
	 * 
	 * @param criteria                Collection
	 * @param ownerBranchId           String
	 * @param moaTableName            String
	 * @param moaTrackProductId       String
	 * @param moaTrackSourcingId      String
	 * @param moaTrackSpecificationId String
	 * @param moaTrackImagePageId     String
	 * @param moaTrackImageFileName   String
	 * @return Map
	 * @throws WTException Exception
	 */
	public static Map<String, Collection<HashMap>> getRemovedImageFromImagePageData(Collection<Map> criteria,
			String ownerBranchId, String moaTableName, String moaTrackProductId, String moaTrackSourcingId,
			String moaTrackSpecificationId, String moaTrackImagePageId, String moaTrackImageFileName)
					throws WTException {

		// Method Name
		String methodName = "getRemovedImageFromImagePageData() ";
		// Initialisation
		Map<String, Collection<HashMap>> trackImageFromImagePageDeletionsMap = new HashMap<String, Collection<HashMap>>();
		// Loop through criteria map
		for (Map<String, String> resultMap : criteria) {
			// Get Flex Type Path
			String moaImageFromImagePageFlexType = BurberryAPIUtil.getFlexTypeIdPath(moaTableName);
			logger.debug(methodName + "Track Image From Image Page Flex Type Path: " + moaImageFromImagePageFlexType);

			// Get Source Column Name
			String strSourceIdColumnName = BurberryAPIUtil
					.getObjectColumnName(moaTableName, BurProductConstant.MOA_TRACK_SOURCING_ID).toUpperCase();
			logger.debug(methodName + "MOA Source Id Column Name: " + strSourceIdColumnName);

			// Get Specification Column Name
			String strSpecIdColumnName = BurberryAPIUtil
					.getObjectColumnName(moaTableName, BurProductConstant.MOA_TRACK_SPECIFICATION_ID).toUpperCase();
			logger.debug(methodName + "MOA Spec Id Column Name: " + strSpecIdColumnName);

			// Get Image Page Id Database Column Name
			String strImagePageIdColumnName = BurberryAPIUtil
					.getObjectColumnName(moaTableName, BurProductConstant.MOA_TRACK_IMAGE_PAGE_ID).toUpperCase();
			logger.debug(methodName + "MOA Image Page Id Name: " + strImagePageIdColumnName);

			// Get Image Page Id Database Column Name
			String strUniqueFileIdColumnName = BurberryAPIUtil
					.getObjectColumnName(moaTableName, BurProductConstant.MOA_TRACK_IMAGE_FILE_UNIQUE_ID).toUpperCase();
			logger.debug(methodName + "MOA Image File Name: " + strUniqueFileIdColumnName);

			// Get Track Image From Image Page Flex Type Path
			String trackedImageFromImagePageFlexTypeId = String
					.valueOf(resultMap.get(BurConstant.LCSMOAOBJ_FLEXTYPEID_PATH));
			logger.debug(methodName + "trackedImagePageFlexTypeId: " + trackedImageFromImagePageFlexTypeId);

			// If it has MOA Object and Track Image Page Type
			if (FormatHelper.hasContent(String.valueOf(resultMap.get(BurConstant.STR_LCSMOAOBJ_IDA2A2)))
					&& moaImageFromImagePageFlexType.equals(trackedImageFromImagePageFlexTypeId)) {
				Collection<HashMap> colRemovedImageFromImagePageData = new ArrayList<HashMap>();
				HashMap hmRemovedImageFromImagePageData = new HashMap();
				// Put Source Id
				hmRemovedImageFromImagePageData.put(STR_SOURCE_ID,
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strSourceIdColumnName)));
				// Put Specification Id
				hmRemovedImageFromImagePageData.put("SPECIFICATION_ID",
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strSpecIdColumnName)));
				// Put Image Page Id
				hmRemovedImageFromImagePageData.put("IMAGE_PAGE_ID",
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strImagePageIdColumnName)));
				logger.debug(methodName + "hmRemovedImagePageData: " + hmRemovedImageFromImagePageData);
				// Put Image Page Name
				hmRemovedImageFromImagePageData.put("UNIQUE_FILE_ID",
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strUniqueFileIdColumnName)));
				logger.debug(methodName + "hmRemovedImagePageData: " + hmRemovedImageFromImagePageData);
				// Add to collection
				colRemovedImageFromImagePageData.add(hmRemovedImageFromImagePageData);

				// Check if same product exists
				if (trackImageFromImagePageDeletionsMap.containsKey(resultMap.get(ownerBranchId))) {
					colRemovedImageFromImagePageData
					.addAll(trackImageFromImagePageDeletionsMap.get(resultMap.get(ownerBranchId)));
				}
				// Put Product Id
				trackImageFromImagePageDeletionsMap.put(resultMap.get(ownerBranchId), colRemovedImageFromImagePageData);
			}
		}
		logger.debug(methodName + "Track Image From Image Page Deletion Map: " + trackImageFromImagePageDeletionsMap);
		// Return
		return trackImageFromImagePageDeletionsMap;

	}

	/**
	 * @param criteria
	 * @param ownerID
	 * @param boTrackSpecificationTblName
	 * @param moaTrackProductId
	 * @param moaTrackSourcingId
	 * @param moaTrackSeasonId
	 * @param moaTrackSpecificationSpecName
	 * @return
	 * @throws WTException
	 */
	public static Map<String, Collection<HashMap>> getSpecDeletionsMapData(Collection<Map> criteria, String ownerID,
			String boTrackSpecificationTblName, String moaTrackProductId, String moaTrackSourcingId,
			String moaTrackSeasonId, String moaTrackSpecificationSpecName) throws WTException {

		// Method Name
		String methodName = "getSpecDeletionsMapData() ";
		// Initialisation
		Map<String, Collection<HashMap>> trackSpecDeletionsMap = new HashMap<String, Collection<HashMap>>();
		logger.debug(methodName + "Track Product Specification Collection : " + criteria);
		// Loop through criteria map
		for (Map<String, String> resultMap : criteria) {
			// Get Flex Type Path
			String moaBOMFlexType = BurberryAPIUtil.getFlexTypeIdPath(boTrackSpecificationTblName);
			logger.debug(methodName + "Track Product Specification Flex Type Path: " + moaBOMFlexType);
			// Get Source Column Name
			String strSourceIdColumnName = BurberryAPIUtil
					.getObjectColumnName(BurProductConstant.BO_TRACK_SPECIFICATION_NAME, moaTrackSourcingId)
					.toUpperCase();
			logger.debug(methodName + "MOA Source Id Column Name: " + strSourceIdColumnName);
			// Get Specification Column Name
			String strSeasonIdColumnName = BurberryAPIUtil
					.getObjectColumnName(BurProductConstant.BO_TRACK_SPECIFICATION_NAME, moaTrackSeasonId)
					.toUpperCase();
			logger.debug(methodName + "MOA Season Id Column Name: " + strSeasonIdColumnName);
			// Get Specification Name Database Column Name
			String strSpecNameColumnName = BurberryAPIUtil
					.getObjectColumnName(BurProductConstant.BO_TRACK_SPECIFICATION_NAME, moaTrackSpecificationSpecName)
					.toUpperCase();
			logger.debug(methodName + "MOA Specification Column Name: " + strSpecNameColumnName);

			/////////////////////////////// L2 Update Start  /////////////////////////////////////////////////////////////
			String strSpecIDColumnName = BurberryAPIUtil
					.getObjectColumnName(BurProductConstant.BO_TRACK_SPECIFICATION_NAME, BurProductConstant.MOA_TRACK_SPECIFICATION_ID)
					.toUpperCase();
			logger.debug(methodName + "MOA Specification ID Column Name >>>>>>>>>>>> " + strSpecIDColumnName);
			////////////////////////////////L2 Update End   /////////////////////////////////////////////////////////////

			// Get Track BOM Flex Type Path
			String trackedSPECFlexTypeId = String.valueOf(resultMap.get(BurConstant.LCSMOAOBJ_FLEXTYPEID_PATH));
			logger.debug(methodName + "trackedSPECFlexTypeId: " + trackedSPECFlexTypeId);
			// If it has MOA Object and Track BOM Type
			if (FormatHelper.hasContent(String.valueOf(resultMap.get(BurConstant.STR_LCSMOAOBJ_IDA2A2)))
					&& moaBOMFlexType.equals(trackedSPECFlexTypeId)) {
				Collection<HashMap> colRemovedSPECData = new ArrayList<HashMap>();
				logger.debug(
						" moaBOMFlexType-- " + moaBOMFlexType + " trackedSPECFlexTypeId-- " + trackedSPECFlexTypeId);
				HashMap hmRemovedBOMData = new HashMap();
				logger.debug(methodName + "STR_LCSMOAOBJ_IDA2A2: " + resultMap.get(BurConstant.STR_LCSMOAOBJ_IDA2A2));
				// Put Source Id
				hmRemovedBOMData.put(STR_SOURCE_ID,
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strSourceIdColumnName)));
				// Put Season Id
				hmRemovedBOMData.put("SEASON_ID",
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strSeasonIdColumnName)));
				// Put BOM Name
				hmRemovedBOMData.put("SPECIFICATION_NAME",
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strSpecNameColumnName)));
				
				/////////////////////////////////////////  L2 Change  /////////////////////////////////////////////////////////////////
				// Put Specification Id
				hmRemovedBOMData.put("SPEC_ID",
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strSpecIDColumnName)));
				
				/////////////////////////////////////////// L2 Change ////////////////////////////////////////////////////////////////
				
				logger.debug(methodName + "hmRemovedSpecData: " + hmRemovedBOMData);
				// Add to collection
				colRemovedSPECData.add(hmRemovedBOMData);

				// Check if same product exists
				if (trackSpecDeletionsMap.containsKey(resultMap.get(ownerID))) {
					colRemovedSPECData.addAll(trackSpecDeletionsMap.get(resultMap.get(ownerID)));
				}
				// Put Product Id
				trackSpecDeletionsMap.put(resultMap.get(ownerID), colRemovedSPECData);
			}
		}
		logger.debug("Track SPECIFICATION Deletion Map: " + trackSpecDeletionsMap);
		// Return
		return trackSpecDeletionsMap;
	}

	/**
	 * @param criteria
	 * @param ownerBranchId
	 * @param moaTableName
	 * @param moaTrackProductId
	 * @param moaTrackSourcingId
	 * @param moaTrackSpecificationId
	 * @param moaTrackDocumentID
	 * @return
	 * @throws WTException
	 */
	public static Map<String, Collection<HashMap>> getRemovedSpecDocumentData(Collection<Map> criteria,
			String ownerBranchId, String moaTableName, String moaTrackProductId, String moaTrackSourcingId,
			String moaTrackSpecificationId, String moaTrackDocumentID) throws WTException {

		// Method Name
		String methodName = "getRemovedSpecDocumentData() ";
		// Initialisation
		Map<String, Collection<HashMap>> trackDocumentDeletionsMap = new HashMap<String, Collection<HashMap>>();
		System.out.println("check " + criteria);
		// Loop through criteria map
		for (Map<String, String> resultMap : criteria) {

			// Get Flex Type Path
			String moaSpecDoclexType = BurberryAPIUtil.getFlexTypeIdPath(moaTableName);
			logger.debug(methodName + "Track Spec Doc Flex Type Path: " + moaSpecDoclexType);
			// Get Source Column Name
			String strSourceIdColumnName = BurberryAPIUtil
					.getObjectColumnName(BurProductConstant.BO_TRACK_SPECIFICATION_DOCUMENT_NAME,
							BurProductConstant.MOA_TRACK_SOURCING_ID)
					.toUpperCase();
			logger.debug(methodName + "MOA Source Config Id Column Name: " + strSourceIdColumnName);
			// Get Specification Column Name
			String strSpecIdColumnName = BurberryAPIUtil
					.getObjectColumnName(BurProductConstant.BO_TRACK_SPECIFICATION_DOCUMENT_NAME,
							BurProductConstant.MOA_TRACK_SPECIFICATION_ID)
					.toUpperCase();
			logger.debug(methodName + "MOA Spec Id Column Name: " + strSpecIdColumnName);
			// Get Image Page Name Database Column Name
			String strDocumentIDColumnName = BurberryAPIUtil
					.getObjectColumnName(BurProductConstant.BO_TRACK_SPECIFICATION_DOCUMENT_NAME,
							BurPaletteMaterialConstant.MOA_TRACK_DOCUMENT_ID)
					.toUpperCase();
			logger.info(methodName + "MOA Document Column Name: " + strDocumentIDColumnName);
			// Get Track Image Page Flex Type Path
			String trackedDocumentFlexTypeId = String.valueOf(resultMap.get(BurConstant.LCSMOAOBJ_FLEXTYPEID_PATH));
			logger.debug(methodName + "trackedDocuFlexTypeId: " + trackedDocumentFlexTypeId);
			// If it has MOA Object and Track Image Page Type
			if (FormatHelper.hasContent(String.valueOf(resultMap.get(BurConstant.STR_LCSMOAOBJ_IDA2A2)))
					&& moaSpecDoclexType.equals(trackedDocumentFlexTypeId)) {
				Collection<HashMap> colRemovedDocumentData = new ArrayList<HashMap>();
				HashMap hmRemovedDocumentData = new HashMap();
				// Put Source Id
				hmRemovedDocumentData.put(STR_SOURCE_ID,
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strSourceIdColumnName)));
				// Put Specification Id
				hmRemovedDocumentData.put("SPECIFICATION_ID",
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strSpecIdColumnName)));
				// Put Image Page Name
				hmRemovedDocumentData.put("DOCUMENT_NAME",
						String.valueOf(resultMap.get(BurConstant.LCSMOAOBJECT + "." + strDocumentIDColumnName)));
				logger.debug(methodName + "hmRemovedImagePageData: " + hmRemovedDocumentData);
				// Add to collection
				colRemovedDocumentData.add(hmRemovedDocumentData);

				// Check if same product exists
				if (trackDocumentDeletionsMap.containsKey(resultMap.get(ownerBranchId))) {
					colRemovedDocumentData.addAll(trackDocumentDeletionsMap.get(resultMap.get(ownerBranchId)));
				}
				// Put Product Id
				trackDocumentDeletionsMap.put(resultMap.get(ownerBranchId), colRemovedDocumentData);
			}
		}
		logger.info(methodName + "Track Document Deletion Map: " + trackDocumentDeletionsMap);
		// Return
		return trackDocumentDeletionsMap;

	}

	/**
	 * @param criteria
	 * @param string
	 * @return
	 */
	public static List<String> getUniqueObjectIds(List<Map> criteria, String objectId) {

		String methodName = "getUniqueObjectIds() ";

		HashSet hashSetFlexObjectIds = new HashSet<String>();

		Map<String, List> flexObjectMap = BurberryAPIUtil.mapFilter(criteria);
		List<String> listofObjects = flexObjectMap.get(objectId);
		// Check if data exits
		if (listofObjects != null && !listofObjects.isEmpty()) {
			// Remove duplicate
			hashSetFlexObjectIds.addAll(listofObjects);
			listofObjects.clear();
			listofObjects.addAll(hashSetFlexObjectIds);
			logger.debug(methodName + "Unique List of Objects Ids: " + listofObjects);
			logger.debug(methodName + "Number of Objects to Process: " + listofObjects.size());
		}

		return listofObjects;
	}
}
