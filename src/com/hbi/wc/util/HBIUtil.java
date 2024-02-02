package com.hbi.wc.util;

import java.util.Date;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.MOAHelper;

import wt.enterprise.Managed;
import wt.fc.WTObject;
import wt.util.WTException;

/**
 * @author Manoj Konakalla
 * @Date April 3rd, 2019 Created this util class for HBI Customizations
 */
public class HBIUtil {

	/**
	 * @param flexTyped
	 * @param attrKey
	 * @return
	 * @throws WTException
	 * @author Manoj Konakalla
	 * @Date April 3, 2019 This method is developed for HBI Custom techpack
	 *       reports and tested only for the following FlexType attributes
	 *       boolean,text,textarea,driven,choice,moaList,
	 *       object_ref,object_ref_list,integer,float. It may or may not work
	 *       for other Flex attribute types.
	 */
	public static String getAttributeTypeValue(FlexTyped flexTyped, String attrKey) throws WTException {
		String attrValue = "";
		FlexType flextype = flexTyped.getFlexType();
		FlexTypeAttribute att = flextype.getAttribute(attrKey);
		String attVariableType = att.getAttVariableType();
		Object prodAttrValue = flexTyped.getValue(attrKey);
		//System.out.println("Attr Key :: " + attrKey);
		//System.out.println("getAttVariableType :: " + attVariableType);
		//System.out.println("prodAttrValue :: " + prodAttrValue);

		if (prodAttrValue != null) {
			if ("driven".equals(attVariableType) || "choice".equals(attVariableType)) {
				attrValue = (String) prodAttrValue;
				attrValue = flextype.getAttribute(attrKey).getAttValueList().getValue((String) attrValue, null);
			} else if ("moaList".equals(attVariableType)) {
				attrValue = att.getStringValue(flexTyped);
				attrValue = MOAHelper.parseOutDelimsLocalized(attrValue, true, att.getAttValueList(),
						ClientContext.getContext().getResolvedLocale());

			} else if ("object_ref".equals(attVariableType) || "object_ref_list".equals(attVariableType)) {
				Object obj = prodAttrValue;
				if (obj instanceof String) {
					attrValue = (String) obj;
				} else if (obj instanceof LCSLifecycleManaged) {
					LCSLifecycleManaged bo = (LCSLifecycleManaged) obj;
					attrValue = bo.getName();
				} else if (obj instanceof FlexSpecification) {
					attrValue = ((FlexSpecification) obj).getName();
				} else if (obj instanceof LCSProduct) {
					attrValue = ((LCSProduct) obj).getName();
				} else if (obj instanceof LCSSourcingConfig) {
					attrValue = ((LCSSourcingConfig) obj).getName();
				} else if (obj instanceof LCSSeason) {
					attrValue = ((LCSSeason) obj).getName();
				} else if (obj instanceof LCSMaterial) {
					attrValue = ((LCSMaterial) obj).getName();
				} else if (obj instanceof FlexBOMPart) {
					attrValue = ((FlexBOMPart) obj).getName();
				} else if (obj instanceof LCSSKU) {
					attrValue = ((LCSSKU) obj).getName();
				} else if (obj instanceof LCSColor) {
					attrValue = ((LCSColor) obj).getName();
				} else if (obj instanceof LCSSupplier) {
					attrValue = ((LCSSupplier) obj).getName();
				} else if (obj instanceof Managed) {
					attrValue = ((Managed) obj).getName();
				} else {
					throw new WTException("Object not found for [attribute key :: " + attrKey + "]");
				}
			} else if ("integer".equals(attVariableType)) {

				// attrValue =
				// Integer.toString(FormatHelper.parseInt(prodAttrValue.toString()));
				Long iValue = (Long) prodAttrValue;
				//int iValue = (int) dValue;
				attrValue = Long.toString(iValue);

			} else if (("float").equals(attVariableType)) {
				attrValue = (String) prodAttrValue;
			} else if (("date").equals(attVariableType)) {
				Date dateObj = (Date) flexTyped.getValue(attrKey);
				String date = "";
				if (dateObj != null) {
					date = FormatHelper.applyFormat(dateObj, "MM/dd/yyyy");
				}
				attrValue = (String) date;
			} else if (("boolean").equals(attVariableType)) {
				attrValue = String.valueOf(prodAttrValue);
				// System.out.println("boolean attrValue" + attrValue);
				if (!FormatHelper.hasContent((String) attrValue)) {
					attrValue = "";
				} else if ("true".equals(attrValue)) {
					attrValue = "Yes";
				} else if ("false".equals(attrValue)) {
					attrValue = "No";
				}
			} else {
				attrValue = (String) prodAttrValue;
			}
		} else if (("boolean").equals(attVariableType)) {
			attrValue = "No";
		} else {
			attrValue = "";
		}
		// System.out.println("attrValue :: " + attrValue);

		return attrValue;
	}

	/**
	 * @param type
	 * @param attrKey
	 * @return
	 * @throws WTException
	 * @author Manoj Konakalla
	 * @Date April 2019
	 */
	public static String getAttDisplayValue(String flextype_path, String attrKey) throws WTException {

		String attrDisplayName = "";
		FlexType type = FlexTypeCache.getFlexTypeFromPath(flextype_path);

		FlexTypeAttribute attribute = type.getAttribute(attrKey);
		attrDisplayName = attribute.getAttDisplay(true);

		return attrDisplayName;
	}

	/**
	 * @param owner
	 * @return
	 */
	public static FlexType getFlexObjectType(WTObject obj) {
		FlexType flexType = null;
		if (obj instanceof LCSProduct) {
			LCSProduct flexObj = (LCSProduct) obj;
			flexType = flexObj.getFlexType();

		} else if (obj instanceof LCSLifecycleManaged) {
			LCSLifecycleManaged flexObj = (LCSLifecycleManaged) obj;
			flexType = flexObj.getFlexType();

		} else if (obj instanceof FlexSpecification) {
			FlexSpecification flexObj = (FlexSpecification) obj;
			flexType = flexObj.getFlexType();

		} else if (obj instanceof LCSSourcingConfig) {
			LCSSourcingConfig flexObj = (LCSSourcingConfig) obj;
			flexType = flexObj.getFlexType();

		} else if (obj instanceof LCSSeason) {
			LCSSeason flexObj = (LCSSeason) obj;
			flexType = flexObj.getFlexType();
			
		} else if (obj instanceof LCSMaterial) {
			LCSMaterial flexObj = (LCSMaterial) obj;
			flexType = flexObj.getFlexType();
			
		} else if (obj instanceof FlexBOMPart) {
			FlexBOMPart flexObj = (FlexBOMPart) obj;
			flexType = flexObj.getFlexType();
			
		} else if (obj instanceof LCSSKU) {
			LCSSKU flexObj = (LCSSKU) obj;
			flexType = flexObj.getFlexType();
			
		} else if (obj instanceof LCSColor) {
			LCSColor flexObj = (LCSColor) obj;
			flexType = flexObj.getFlexType();
			
		} else if (obj instanceof LCSSupplier) {
			LCSSupplier flexObj = (LCSSupplier) obj;
			flexType = flexObj.getFlexType();
		}

		return flexType;
	}

}
