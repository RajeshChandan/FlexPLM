package com.burberry.wc.integration.util;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.burberry.wc.integration.bean.ErrorMessage;
import com.burberry.wc.integration.palettematerialapi.constant.BurPaletteMaterialConstant;
import com.burberry.wc.integration.productbomapi.constant.BurProductBOMConstant;
import com.lcs.wc.color.*;
import com.lcs.wc.document.LCSDocument;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterialSupplierMaster;
import com.lcs.wc.util.FormatHelper;

/**
 * Utility class for API Bean Data.
 *
 * @version 'true' 1.0.1
 *
 * @author 'true' ITC INFOTECH
 *
 */

public final class BurberryAPIBeanUtil {

	/**
	 * Default Constructor.
	 */
	private BurberryAPIBeanUtil() {

	}

	/**
	 * logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BurberryAPIBeanUtil.class);

	/**
	 * Method to return response in case of exception.
	 * 
	 * @param errorMsg
	 *            Error message
	 * @param status
	 *            Status
	 * @return Error Message Response
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static ErrorMessage getErrorResponseBean(final String errorMsg,
			final Status status,
			MultivaluedMap<String, String> queryParams, long lStartTime,
			String flexTypePath, String apiType)
			throws WTPropertyVetoException{
		final ErrorMessage errorMessage = new ErrorMessage(
				"Error occured while extracting " + apiType + " API data",
				status.getStatusCode(), errorMsg);
		// Call the method to create a log entry
		try{
			BurberryLogEntrySetup.createLogEntry(status.getStatusCode(),
					errorMsg, queryParams, lStartTime, flexTypePath);
		}catch(WTException e){
			logger.error(e.getLocalizedMessage(),e);
		}
		// Return Statement
		return errorMessage;
	}

	/**
	 * Method to get Object Data.
	 * 
	 * @param attKeys
	 * @param ignoreAttributes
	 * @param propertiesBean
	 * @param object
	 * @param attributes
	 * @param jsonMapping
	 * @throws WTException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 */
	public static void getObjectData(String ignoreAttributes,
			Object propertiesBean, WTObject object, String attributes,
			Map<String, String> jsonMapping,Map<String, String> systemAttjsonMapping) throws WTException,
			IllegalAccessException, InvocationTargetException, IOException,
			NoSuchMethodException {
		String methodName = "getObjectData() ";
		logger.debug(methodName + " Selected Attributes: " + attributes
				+ " Ignore Attributes: " + ignoreAttributes);

		// Get the collection of all attributes
		Collection<FlexTypeAttribute> atts = getFlexTypeAttributesCol(
				attributes, object);
		// logger.debug(methodName+"All Attributes: "+atts);
		updateAttributesOnBean(atts, jsonMapping, ignoreAttributes,
				propertiesBean, object);
		if(systemAttjsonMapping!=null){
		for (Map.Entry<String, String> mapEntry : systemAttjsonMapping.entrySet()) {
			String strAttKey = mapEntry.getKey();
			String strJsonKey = mapEntry.getValue();
			logger.debug(methodName + " strAttKey " + strAttKey
					+ " strJsonKey " + strJsonKey);
			// Get Name of the object
			if (BurConstant.NAME_ATTRIBUTES.contains(strAttKey)
					&& BurberryDataUtil.getFlexTyped(object).getFlexType()
							.attributeExist(strAttKey)) {
				BeanUtils.setProperty(propertiesBean,strJsonKey,
						BurberryDataUtil.getFlexTyped(object).getValue(
								strAttKey));
			}// Get Create Time Stamp of the object
			else if (BurPaletteMaterialConstant.CREATESTAMP
					.equalsIgnoreCase(strAttKey)) {
				BeanUtils.setProperty(propertiesBean, strJsonKey,
						getCreateStamp(object));
			}// Get Modify Time Stamp of the Object
			else if (BurConstant.MODIFYSTAMP.equalsIgnoreCase(strAttKey)) {
				BeanUtils.setProperty(propertiesBean, strJsonKey,
						getLastModify(object));
			}// Get Creator Name for the object
			else if (BurPaletteMaterialConstant.CREATOR
					.equalsIgnoreCase(strAttKey)) {
				BeanUtils.setProperty(propertiesBean, strJsonKey,
						BurberryDataUtil.getCreator(object));
			}// Get Modifier Name for the object
			else if (BurPaletteMaterialConstant.MODIFIER
					.equalsIgnoreCase(strAttKey)) {
				BeanUtils.setProperty(propertiesBean, strJsonKey,
						BurberryDataUtil.getModifier(object));
			} else if (BurProductBOMConstant.BOMTYPE
					.equalsIgnoreCase(strAttKey)) {
				BeanUtils.setProperty(propertiesBean,strJsonKey,
						((FlexBOMPart) object).getBomType());
			}
		}
		}
	}

	/**
	 * Method to get Create timestamp.
	 * 
	 * @param obj
	 *            WTObject
	 * @return String
	 * @throws WTException
	 *             Exception
	 */
	public static String getCreateStamp(WTObject obj) throws WTException {
		// define variables
		String methodName = "getCreateStamp() ";
		SimpleDateFormat formatter = new SimpleDateFormat(
				BurConstant.dateFormat);
		Timestamp createTime = obj.getCreateTimestamp();
		logger.debug(methodName + "formatter " + formatter + " create "
				+ createTime);
		return formatter.format(createTime);
	}

	/**
	 * Method to Last Modify time stamp.
	 * 
	 * @param obj
	 *            WTObject
	 * @return String
	 * @throws WTException
	 *             Exception
	 */
	public static String getLastModify(WTObject obj) throws WTException {
		// define variables
		String methodName = "getLastModify() ";
		SimpleDateFormat formatter = new SimpleDateFormat(
				BurConstant.dateFormat);
		Timestamp modifyTime = obj.getModifyTimestamp();
		logger.debug(methodName + "formatter " + formatter + " modify "
				+ modifyTime);
		return formatter.format(modifyTime);
	}

	/**
	 * Method to update bean data using object.
	 * 
	 * @param atts
	 *            Collection
	 * @param jsonMapping
	 *            Map
	 * @param propertiesBean
	 *            Bean
	 * @param ignoreAttributes
	 *            String
	 * @param object
	 *            WTObject
	 * @throws WTException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 */
	static void updateAttributesOnBean(Collection<FlexTypeAttribute> atts,
			Map<String, String> jsonMapping, String ignoreAttributes,
			Object propertiesBean, WTObject object)
			throws IllegalAccessException, InvocationTargetException,
			WTException, NoSuchMethodException {
		String methodName = "updateAttributesOnBean() ";
		Collection<String> hiddenAttributes = new ArrayList<String>();
		for (FlexTypeAttribute att : atts) {
			String strAttKey = att.getAttKey();
			// logger.debug(methodName+"Compare AttKey:["+strAttKey+"]");
			if (jsonMapping.containsKey(strAttKey)
					&& (!ignoreAttributes.contains(strAttKey))) {
				if (!att.isAttHidden()) {
					logger.debug(methodName + " strAttKey " + strAttKey);
					getBeanData(strAttKey, jsonMapping.get(strAttKey),
							propertiesBean, object);

				} else {
					hiddenAttributes.add(strAttKey);
				}
			}
		}
		logger.debug(methodName + "Hidden attributes: " + hiddenAttributes);
	}

	/**
	 * Method to get FlexTypeAttribute.
	 * 
	 * @param attributes
	 *            String
	 * @param object
	 *            WTObject
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */
	public static Collection<FlexTypeAttribute> getFlexTypeAttributesCol(
			String attributes, WTObject object) throws WTException {

		String methodName = "getFlexTypeAttributesCol() ";
		Collection<FlexTypeAttribute> atts = new ArrayList<FlexTypeAttribute>();
		logger.debug("atts string " + attributes);
		// Check if all attributes need to be published
		if ("ALL".equalsIgnoreCase(attributes)) {
			atts = BurberryDataUtil.getFlexTyped(object).getFlexType()
					.getAllAttributes();
		} else {
			StringTokenizer attKey = new StringTokenizer(attributes,
					BurConstant.STRING_COMMA);
			while (attKey.hasMoreTokens()) {
				String key = attKey.nextToken();
				logger.debug(methodName + " key " + key);
				if (BurberryDataUtil.getFlexTyped(object).getFlexType()
						.attributeExist(key)) {
					atts.add(BurberryDataUtil.getFlexTyped(object)
							.getFlexType().getAttribute(key));
				}
			}
		}
		return atts;
	}

	/**
	 * Method to validate required attributes.
	 * 
	 * @param object
	 *            Object
	 * @param requiredAtts
	 *            String
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 *//*
	public static void validateRequiredAttributes(Object object,
			String requiredAtts) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		StringTokenizer requiredatt = new StringTokenizer(requiredAtts,
				BurConstant.STRING_COMMA);
		while (requiredatt.hasMoreTokens()) {
			String jsonKey = requiredatt.nextToken();
			String value = BeanUtils.getProperty(object, jsonKey);
			if (!FormatHelper.hasContent(value)) {
				BeanUtils
						.setProperty(object, jsonKey, BurConstant.STRING_EMPTY);
			}
		}
	}*/

	/**
	 * Method to get Bean Data.
	 * 
	 * @param strAttKey
	 *            String
	 * @param strJsonKey
	 *            String
	 * @param bean
	 *            String
	 * @param obj
	 *            String
	 * @throws WTException
	 *             Exception
	 * @throws IllegalAccessException
	 *             Exception
	 * @throws InvocationTargetException
	 *             Exception
	 * @throws NoSuchMethodException
	 *             Exception
	 */
	public static void getBeanData(String strAttKey, String strJsonKey,
			Object bean, WTObject obj) throws WTException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		String methodName = "getBeanData() ";
		String value = BurberryDataUtil.getData(obj, strAttKey, null);
		logger.debug(methodName + "AttributeKey:" + strAttKey + "  JsonKey:"
				+ strJsonKey + " Value:" + value);
		BeanUtils.setProperty(bean, strJsonKey, value);
		//logger.info("check value "+value+" "+BeanUtils.getProperty(bean, strJsonKey));
	}

	/**
	 * Method to get check in comment.
	 * 
	 * @param document
	 *            LCSDocument
	 * @return String
	 * @throws WTException
	 *             Exception
	 * @throws PropertyVetoException
	 */
	public static String getDocumentCheckinComment(LCSDocument document)
			throws WTException {
		String methodName = "getDocumentCheckinComment() ";
		try {
			LCSDocument docObject = (LCSDocument) ContentHelper.service
					.getContents(document);
			logger.debug(methodName + "Document: " + docObject);
			ApplicationData applicationdata = (ApplicationData) ContentHelper
					.getPrimary(docObject);
			logger.debug(methodName + "ApplicationData: " + applicationdata);
			if (applicationdata != null
					&& FormatHelper
							.hasContent(applicationdata.getDescription())) {
				logger.debug(methodName + "Applicationdata: getDescription="
						+ applicationdata.getDescription());
				// Return Statement
				return applicationdata.getDescription();
			}
		} catch (PropertyVetoException ex) {
			ex.printStackTrace();
		}
		// Return Statement
		return null;
	}

	/**
	 * Method to get Palette Material Colour Links based on Palette and.
	 * MaterialSupplierMaster.
	 * 
	 * @param palette
	 *            LCSPalette
	 * @param materialObject
	 *            LCSMaterial
	 * @param materialSupplierObject
	 *            LCSMaterialSupplier
	 * @return Collection
	 * @throws WTException
	 *             Exception
	 */
	public static Collection<LCSPaletteMaterialColorLink> getPaleteMaterialColourLink(
			LCSPalette palette, LCSMaterialSupplierMaster materialSupplierMaster)
			throws WTException {

		String methodName = "getPaleteMaterialColourLinks() ";
		// Track execution time
		long palMatColStartTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Palette Material Color Link Start Time: ");
		Collection<LCSPaletteMaterialColorLink> paletteMaterialColourLinks = new ArrayList<LCSPaletteMaterialColorLink>();
		logger.debug(methodName + "materialSupplierMaster: "
				+ materialSupplierMaster);

		// Get Palette Material Link from Palette and MaterialSupplierMaster
		LCSPaletteMaterialLink paletteMaterialLink = new LCSPaletteQuery()
				.findPaletteMaterialLink(palette, materialSupplierMaster);
		logger.debug(methodName + "PaletteMaterialLink: " + paletteMaterialLink);

		// Check if palette material link exists
		if (paletteMaterialLink != null) {
			// Get all the Palette Material Colour Links using Palette Material
			// Link
			paletteMaterialColourLinks = LCSQuery
					.getObjectsFromResults(
							new LCSPaletteQuery()
									.findPaletteMaterialColorLinkDataForPaletteMaterialLink(paletteMaterialLink),
							BurPaletteMaterialConstant.PALETTE_MATERIAL_COLOUR_PREFIX,
							BurPaletteMaterialConstant.PALETTE_MATERIAL_COLOUR_LINK_ID);
			logger.debug(methodName + "palMaterialColourLinks: "
					+ paletteMaterialColourLinks);
		}
		// Track execution time
		long palMatColEndTime = BurberryAPIUtil.printCurrentTime(methodName,
				"Palette Material Color Link End Time: ");
		logger.debug(methodName
				+ "Palette Material Color Link Total Execution Time (ms): "
				+ (palMatColEndTime - palMatColStartTime));
		// Return Statement
		return paletteMaterialColourLinks;
	}
	
	
	/**
	 * @param list
	 * 			list			
	 */
	public static void sendNoRecordFoundException(
			final Collection list) {
		String methodName="sendNoRecordFoundException() ";
		// throw exception if matches no record fetched.
		if (list.size() == 0) {
			logger.error(methodName+BurConstant.STR_NO_MATCHING_RECORD_FOUND);
			/*throw new NoRecordFoundException(
					BurConstant.STR_NO_MATCHING_RECORD_FOUND);*/
		}
	}

}
