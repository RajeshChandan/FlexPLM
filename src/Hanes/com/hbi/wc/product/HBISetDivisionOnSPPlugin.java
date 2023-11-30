package com.hbi.wc.product;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hbi.wc.interfaces.outbound.product.translation.HBISellingProductTransformationProcessor;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductLogic;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class HBISetDivisionOnSPPlugin {

	private static final Logger logger = LogR.getLogger("com.hbi.wc.product.HBISetDivisionOnSPPlugin");

	/**
	 * This function will invoke from custom plug-in entry which is registered
	 * on PRE_PERSIST event of LCSProduct of type 'Selling Product' to populate
	 * Division
	 * 
	 * @param wtObj
	 *            - WTObject
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */

	public static void setDivisiononSP(WTObject wtObj) throws WTException, WTPropertyVetoException,
			NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		logger.debug("### START HBISetDivisionOnSPPlugin.setDivisiononSP()###");

		if (wtObj instanceof LCSProduct) {
			
			LCSProduct prodObj = (LCSProduct) wtObj;
			System.out.println("This plugin was called ");
			System.out.println("This plugin was called ");

			Map divisionMap = new HBISellingProductTransformationProcessor().findDivisonFromDerivationTable(prodObj,
					null, false);
			System.out.println("divisionMap::::::::::::"+divisionMap);
			String existingDivision = (String) prodObj.getValue("hbiErpDivision");
			String value = (String) divisionMap.get("hbiSAPDivision");

			LCSLog.debug("Division Value based on SAP Derivation should be ::::::::::::::::" + value);
			LCSLog.debug("Division Value based on SAP Derivation should be ::::::::::::::::" + value);
			LCSLog.debug("Division Value Existing Division ::::::::::::::::" + existingDivision);

			System.out.println("existingDivision::::::::::::"+existingDivision);

			 System.out.println("divisionMap::::value::::::::"+value);

			if (FormatHelper.hasContent(value)) {
				if (!(FormatHelper.hasContent(existingDivision) && value.contains(existingDivision))) {
					if (!divisionMap.containsKey("Error")) {
						prodObj.setValue("hbiErpDivision", value);
					}
				}

			}

		}
		logger.debug("###HBISetDivisionOnSPPlugin.setDivisiononSP()# ###");
	}

}
