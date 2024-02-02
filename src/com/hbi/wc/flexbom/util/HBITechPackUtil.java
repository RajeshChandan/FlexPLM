package com.hbi.wc.flexbom.util;

import java.util.ArrayList;
import java.util.List;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.MOAHelper;

import wt.util.WTException;

public class HBITechPackUtil {
	 public static final String PRINTS_AND_PATTERNS = "Prints and Patterns";	
	/*For Sizes to Sort in same order as in PSD in Tech Pack */
	
	public static String addStringSizes(String collection, String newStr) {
		String newString = "";
		if (!FormatHelper.hasContentAllowZero(collection)) {
			newString = newStr;
		} else {
			List<String> strs = new ArrayList<String>(MOAHelper.getMOACollection(collection));
			strs.add(newStr);
			newString = MOAHelper.toMOAString(strs);
		}

		return newString;
	}
	
    /**
     * @param gp_ColorName
     * @param gp_hbiColorUniquenessFormula
     * @param gp_clrSpecVer
     * @param gp_hbiGroundColor
     * @param gp_colorId 
     * @return gp_ColorName
     * @throws WTException 
     * @Date 09/07/18
     * This method returns color value, that has to be print on GP tech pack, 
     * for colorway and color cells in all GP BOM reports.
     * Color specific functinality:
     * 1.If color having "hbiColorUniquenessFormula" then replace it with color full name.
     * 2.If color having "color specific version" on Material-Color object, 
     * 	 then append it with color name.
     * 3. If color type is "Prints and Patterns", then get "hbiGroundColor" from Color object, 
     * 	and append it as follow "colorname on hbiGroundColor".
     * 
     */
	public static String getGPBOMColor(String gp_ColorName, String gp_clrSpecVer, String gp_colorId,String dyeCode, String printCode) throws WTException {
		LCSColor color = null;
		String colorOid = "OR:com.lcs.wc.color.LCSColor:" + gp_colorId;
		LCSColor groundColorObject = null;
		String gp_hbiGroundColor = "";
		String gp_hbiColorUniquenessFormula = "";

		if (!gp_colorId.equals("0")) {
			color = (LCSColor) LCSQuery.findObjectById(colorOid);
			if (color != null) {
				gp_hbiColorUniquenessFormula = (String) color.getValue("hbiColorUniquenessFormula");
				// If color type is "Prints and patterns" then get "ground color"
				if (color.getFlexType().getFullName().equals(PRINTS_AND_PATTERNS)) {
					groundColorObject = (LCSColor) color.getValue("hbiGroundColor");
					if (groundColorObject != null) {
					//Changed to Solid Uniqueness Formula of Ground Color
						gp_hbiGroundColor = (String) groundColorObject.getValue("hbiColorUniquenessFormula");
						if(!FormatHelper.hasContent(gp_hbiGroundColor)) {
							gp_hbiGroundColor = "";
						}
					}
				}

			}
		}
		// 1.If color having "hbiColorUniquenessFormula" then replace it with color full
		// name.
		if (FormatHelper.hasContent(gp_hbiColorUniquenessFormula)) {

			gp_ColorName = gp_hbiColorUniquenessFormula;
		}
		// 2.If color having "color specific version" on Material-Color object, then
		// append it with color name.
		if (FormatHelper.hasContent(gp_clrSpecVer)) {
			gp_ColorName = gp_ColorName + " - " + gp_clrSpecVer;
		}


		if (FormatHelper.hasContent(printCode)) {
			gp_ColorName = gp_ColorName + " - " + printCode;
		}
		// 3.If color having "hbiGroundColor" on Color object, then append it like
		// "colorname on hbiGroundColor".
		if (FormatHelper.hasContent(gp_hbiGroundColor)) {
			gp_ColorName = gp_ColorName + " on " + gp_hbiGroundColor;
		}
		if (FormatHelper.hasContent(dyeCode)) {
			gp_ColorName = gp_ColorName + " - " + dyeCode;

		}
		return gp_ColorName;

	}
	
	
}
