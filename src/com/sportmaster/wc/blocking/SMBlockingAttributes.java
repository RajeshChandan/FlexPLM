/**
 * 
 */
package com.sportmaster.wc.blocking;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.TreeMultimap;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSKUSeasonLink;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.util.FormatHelper;

import wt.fc.WTObject;
import wt.method.MethodContext;


/**
 * SMBlockingAttributes.java
 * This class contains all plugin methods for product,product-season,
 * colorway-season,colorway, sourcing config-season object.
 *
 * @author 'true' Zahiruddin Ansari
 * @author 'true' Rajesh Chandan - modified code foe system testing issues.
 * 
 * @version 'true' 1.1 version number
 */
public class SMBlockingAttributes {

	/**
	 * the LOGGER.
	 */
	private static final Logger LOGGER = Logger.getLogger(SMBlockingAttributes.class);

	/**
	 * Edit Line literal.
	 */
	private static final String EDIT_LINE = "EDIT_LINE";

	/**
	 * True Value.
	 */
	private static final String TRUE_VALUE = "true";

	/**
	 * create multimap object to store key and corresponding values to achieve pop
	 * up at front end.
	 * 
	 */
	public static final TreeMultimap<String, String> multiMap = TreeMultimap.create();

	/**
	 * protected constructor.
	 */
	protected SMBlockingAttributes(){
		//constructor.
	}

	/**
	 * Method for getting product blocking attributes information.
	 * @param wtObj - WTObject.
	 */
	public static void smProductAttributes(WTObject wtObj) {

		LOGGER.debug("################## Plugin Starts for Product Blocking Attributes #######################");
		String editLine = String.valueOf(MethodContext.getContext().get(EDIT_LINE));
		//this check will only activate plugin trigger only for edit linesheet action
		if(!FormatHelper.hasContent(editLine)) {
			multiMap.clear();
			return;
		}
		LCSProduct product = null;

		//checking wtObj is an instance of LCSProduct or not
		if (wtObj instanceof LCSProduct) {
			product = (LCSProduct) wtObj;
			LOGGER.info("executing blocking customisation for product:-"+product.getName());
			//calling helper method
			SMProductBlockingAttributesHelper.smProductAttributes(product);
		}
		if(!FormatHelper.hasContent(editLine) || !TRUE_VALUE.equalsIgnoreCase(editLine)) {
			multiMap.clear();
		}
		LOGGER.debug("################## Plugin End for Product Blocking Attributes #######################");
	}

	/**
	 * Method for getting product season blocking attributes information.
	 * @param wtObj - WTObject.
	 */
	public static void smProductSeasonAttributes(WTObject wtObj){

		LOGGER.debug("################## Plugin Starts for Product Season Blocking Attributes #######################");

		String editLine = String.valueOf(MethodContext.getContext().get(EDIT_LINE));
		//this check will only activate plugin trigger only for edit linesheet action
		if(!FormatHelper.hasContent(editLine)) {
			multiMap.clear();
			return;
		}
		LCSProductSeasonLink productSeasonLink = null;

		//checking wtObj is an instance of LCSProductSeasonLink or not
		if (wtObj instanceof LCSProductSeasonLink) {
			productSeasonLink = (LCSProductSeasonLink) wtObj;
			LOGGER.debug("Product season link Effective Sequence value:-" +productSeasonLink.getEffectSequence());
			if (productSeasonLink.getEffectSequence() > 0) {
				//calling helper method
				SMProductBlockingAttributesHelper.smProductSeasonAttributes(productSeasonLink);
			}
		}
		if(!FormatHelper.hasContent(editLine) || !TRUE_VALUE.equalsIgnoreCase(editLine)) {
			multiMap.clear();
		}
		LOGGER.debug("################## Plugin Ends for Product Season Blocking Attributes #######################");
	}

	/**
	 * Method for getting colorway blocking attributes information.
	 * @param wtObj - WTObject.
	 */
	public static void smColorwayAttributes(WTObject wtObj) {

		LOGGER.debug("################## Plugin Starts for Colorway Blocking Attributes #######################");
		String editLine = String.valueOf(MethodContext.getContext().get(EDIT_LINE));
		LCSSKU skuObj = null;
		//this check will only activate plugin trigger only for edit linesheet action
		if(!FormatHelper.hasContent(editLine)) {
			multiMap.clear();
			return;
		}
		//checking wtObj is an instance of LCSSKU or not
		if (wtObj instanceof LCSSKU) {
			skuObj = (LCSSKU) wtObj;
			LOGGER.info("executing blocking customisation fpr colorway:-"+skuObj.getName());
			//calling helper method
			SMColorwayBlockingAttributesHelper.smColorwayAttributes(skuObj);

		}
		if(!FormatHelper.hasContent(editLine) || !TRUE_VALUE.equalsIgnoreCase(editLine)) {
			multiMap.clear();
		}
		LOGGER.debug("################## Plugin Ends for Colorway Blocking Attributes #######################");
	}

	/**
	 * Method for getting colorway season blocking attributes information.
	 * @param wtObj - WTObject.
	 */
	public static void smColorwaySeasonAttributes(WTObject wtObj) {

		LOGGER.debug("################## Plugin Starts for Colorway Season Blocking Attributes #######################");
		String editLine = String.valueOf(MethodContext.getContext().get(EDIT_LINE));
		LCSSKUSeasonLink skuSeasonObj = null;
		//this check will only activate plugin trigger only for edit linesheet action
		if(!FormatHelper.hasContent(editLine)) {
			multiMap.clear();
			return;
		}
		//checking wtObj is an instance of LCSSKUSeasonLink or not
		if (wtObj instanceof LCSSKUSeasonLink) {
			skuSeasonObj = (LCSSKUSeasonLink) wtObj;
			if(skuSeasonObj.getEffectSequence() > 0) {
				//calling helper method
				SMColorwayBlockingAttributesHelper.smColorwaySeasonAttributes(skuSeasonObj);
			}
		}
		if(!FormatHelper.hasContent(editLine) || !TRUE_VALUE.equalsIgnoreCase(editLine)) {
			multiMap.clear();
		}
		LOGGER.debug("################## Plugin Ends for Colorway Season Blocking Attributes #######################");
	}

	/**
	 * Method for getting sourcing season blocking attributes information.
	 * @param wtObj - WTObject.
	 */
	public static void smSourcingSeasonAttributes(WTObject wtObj) {

		LOGGER.debug("################## Plugin Starts for Source To Season Link Blocking Attributes #######################");
		String editLine = String.valueOf(MethodContext.getContext().get(EDIT_LINE));
		LCSSourceToSeasonLink ssl = null;
		//this check will only activate plugin trigger only for edit linesheet action
		if(!FormatHelper.hasContent(editLine)) {
			multiMap.clear();
			return;
		}
		//checking wtObj is an instance of LCSSourceToSeasonLink or not
		if (wtObj instanceof LCSSourceToSeasonLink) {
			ssl = (LCSSourceToSeasonLink) wtObj;
			LOGGER.info("executing blocking customisation for Sourcing configuration-season:-"+ssl.getName());
			//calling helper method
			SMSourcingBlockingAttributesHelper.smSourcingSeasonAttributes(ssl);
		}
		if(!FormatHelper.hasContent(editLine) || !TRUE_VALUE.equalsIgnoreCase(editLine)) {
			multiMap.clear();
		}

		LOGGER.debug("################## Plugin Ends for Source To Season Link Blocking Attributes #######################");
	}
	
	/**
	 * Method for getting sourcing configuration blocking attributes information.
	 * @param wtObj - WTObject.
	 */
	public static void smSourcingConfigAttributes(WTObject wtObj) {

		LOGGER.debug("################## Plugin Starts for Sourcing Configuration Blocking Attributes #######################");
		String editLine = String.valueOf(MethodContext.getContext().get(EDIT_LINE));
		LCSSourcingConfig sConfig = null;
		//this check will only activate plugin trigger only for edit linesheet action
		if(!FormatHelper.hasContent(editLine)) {
			multiMap.clear();
			return;
		}
		//checking wtObj is an instance of LCSSourceToSeasonLink or not
		if (wtObj instanceof LCSSourcingConfig) {
			sConfig = (LCSSourcingConfig) wtObj;
			LOGGER.info("executing blocking customisation for Sourcing configuration:-"+sConfig.getName());
			
			//calling helper method
			SMSourcingBlockingAttributesHelper.smSourcingConfigAttributes(sConfig);
		}
		if(!FormatHelper.hasContent(editLine) || !TRUE_VALUE.equalsIgnoreCase(editLine)) {
			multiMap.clear();
		}

		LOGGER.debug("################## Plugin Ends for Sourcing configuration Blocking Attributes #######################");
	}

	/**
	 * getting error message to show on UI as a pop up window.
	 * @return returns string field.
	 */
	public static String getError() {
		LOGGER.info("################## SMBlockingAttributes.getError() STARTS #######################");
		//if map is entry then return
		if(multiMap.isEmpty()) {
			return null;
		}
		//cretaing json object
		JSONObject jsonObj = new JSONObject();
		//creating message string
		StringBuilder msgBuffer = new StringBuilder();
		//creating temo string
		StringBuilder temp = new StringBuilder();
		Set<?> keySet = multiMap.keySet();
		Iterator<?> keyIterator = keySet.iterator();
		LOGGER.info("retriving error map....");
		//iterating all blocked attrtibutes
		while (keyIterator.hasNext()) {
			String key = (String) keyIterator.next();
			SortedSet<String> values = multiMap.get(key);
			if (FormatHelper.hasContent(msgBuffer.toString())) {
				msgBuffer.append("<br>");
			}
			//formatting error message 
			msgBuffer.append("Attempted to update blocked attributes: " + key + ", objects not updated: ");
			for (String val : values) {
				if (!FormatHelper.hasContent(temp.toString())) {
					temp.append(val);
				} else {
					temp.append(","+val);

				}
			}
			msgBuffer.append(temp);
			//deleting temp value
			temp.delete(0, temp.length());
		}
		try {
			LOGGER.info("creting/formatinng json string....");
			//adding entries in json string
			jsonObj.put("error", msgBuffer.toString());
			jsonObj.put("success", false);
		} catch (JSONException e) {
			LOGGER.error("ERROR FOUND:-",e);
		}
		LOGGER.info("final error messgae:-"+ msgBuffer.toString());
		multiMap.clear();
		LOGGER.info("################## SMBlockingAttributes.getError() ENDS   #######################");
		return jsonObj.toJSONString();
	}


	/**
	 * This method for library search returning error message.
	 * @param isLibrary - a boolean field
	 * @return returns string
	 */
	public static String getError(boolean isLibrary) {

		if(multiMap.isEmpty()) {
			return null;
		}

		StringBuffer msgBuffer = new StringBuffer();
		String temp = null;
		Set keySet = multiMap.keySet();
		LOGGER.debug("Key Set >>>>>>>>>>>>>>" + keySet);
		Iterator keyIterator = keySet.iterator();
		while (keyIterator.hasNext()) {
			String key = (String) keyIterator.next();
			SortedSet<String> values = multiMap.get(key);
			if (FormatHelper.hasContent(msgBuffer.toString())) {
				msgBuffer.append("<br>");
			}
			msgBuffer.append("Attempted to update blocked attributes: " + key + ", objects not updated: ");
			for (String val : values) {
				if (temp == null) {
					temp = val;
				} else {
					temp = temp + "," + val;
				}
			}
			msgBuffer.append(temp);
			temp = null;
		}
		LOGGER.info("MSG>>>>>>>>>>>>" + msgBuffer.toString());

		multiMap.clear();
		return msgBuffer.toString();
	}

}
