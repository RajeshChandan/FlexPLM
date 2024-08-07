package com.sportmaster.wc.flexbom.gen;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.MCIDimensionHelper;
import com.lcs.wc.flexbom.MaterialColorInfo;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.*;/*LCSMaterialColor;
								import com.lcs.wc.material.LCSMaterialMaster;
								import com.lcs.wc.material.LCSMaterialSupplierMaster;
								import com.lcs.wc.material.LCSMaterialSupplierQuery;
								import com.lcs.wc.material.MaterialPriceList;*/
import com.lcs.wc.supplier.LCSSupplierMaster;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.MOAHelper;

import wt.util.WTException;

/*This class is helper class, used get the data for BOM variation report.
 * 
 */

public class SMBOMVariationsUtil {

	private static final String COLOR_IDS = "COLORIDS";
	private static final String COLOR_NAME = "COLORNAME";
	private static final String COLOR_DESC = "COLORDESC";
	private static final String COLOR_ID = "COLORID";
	private static final String COLOR_HEX = "COLORHEX";
	private static final String COLOR_THUMB = "COLORTHUMB";
	private static final String MATERIALCOLOR_ID = "MATERIALCOLORID";
	protected static final String DIMID_COL = "FLEXBOMLINK.DIMENSIONID";
	protected static final String PERSIST_INFO_OID = "thePersistInfo.theObjectIdentifier.id";
	protected static final String FLEXBOMLINK = "FLEXBOMLINK.";

	private static final Logger LOGGER = Logger.getLogger(SMBOMVariationsUtil.class);

	/**
	 * Populates color data for a row given a colorway dimension
	 * 
	 * @param row
	 * @param skuId
	 * @param colorMci
	 * @throws WTException
	 */
	public void addColorData(FlexObject row, String skuId, MaterialColorInfo colorMci) throws WTException {
		LOGGER.debug("In addColorData method - Start ");
		String colorName = colorMci.colorName;
		String colorId = colorMci.colorId;
		String colorDescription = colorMci.colorDescription;
		String materialColorId = colorMci.materialColorId;

		String coloridstring = row.getString(COLOR_IDS);

		if (!FormatHelper.hasContent(coloridstring)) {
			coloridstring = skuId;
		} else {
			coloridstring = coloridstring + MOAHelper.DELIM + skuId;
		}
		LOGGER.debug("In addColorData method - coloridstring= " + coloridstring);
		row.put(COLOR_IDS, coloridstring);
		row.put(skuId + "." + COLOR_NAME, colorName);
		row.put(skuId + "." + COLOR_DESC, colorDescription);
		row.put(skuId + "." + COLOR_ID, colorId);
		row.put(skuId + "." + MATERIALCOLOR_ID, materialColorId);
		// row.put(skuId + "." + COLORHEX, colorMci.colorHexValue);
		// Add color level information to fo
		if (FormatHelper.hasContent(colorId)) {
			LCSColor color = (LCSColor) LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSColor:" + colorId);
			if (color.getColorHexidecimalValue() != null) {
				row.put(skuId + "." + COLOR_HEX, color.getColorHexidecimalValue());
			}
			if (color.getThumbnail() != null) {
				row.put(skuId + "." + COLOR_THUMB, color.getThumbnail());
			}
		}
		LOGGER.debug("In addColorData method - End ");
	}

	/**
	 * This method merging strings between tow objects.
	 * 
	 * @param collection
	 * @param newStr
	 * @return
	 */
	public String addMergString(String collection, String newStr) {
		String newString = "";
		if (!FormatHelper.hasContentAllowZero(collection)) {
			newString = newStr;
		} else {
			HashSet<String> strs = new HashSet<String>(MOAHelper.getMOACollection(collection));
			if (FormatHelper.hasContentAllowZero(newStr) && !strs.contains(newStr)) {
				strs.add(newStr);
			}
			List<String> sortedList = new ArrayList<String>(strs);
			// Sort the merged string
			java.util.Collections.sort(sortedList);
			newString = MOAHelper.toMOAString(sortedList);
			LOGGER.debug("In addMergString method - newString== " + newString);
		}
		return newString;
	}

	/**
	 * This method used sort string.
	 * 
	 * @param collection
	 * @param newStr
	 * @return
	 */
	protected String addString(String collection, String newStr) {
		String newString = "";
		if (!FormatHelper.hasContentAllowZero(collection)) {
			newString = newStr;
		} else {
			List<String> strs = new ArrayList<String>(MOAHelper.getMOACollection(collection));
			strs.add(newStr);
			java.util.Collections.sort(strs);
			newString = MOAHelper.toMOAString(strs);
		}
		// Return newString
		return newString;
	}

	/**
	 * Method to set sort criteria For BOM Varaiation report - sort based on
	 * material, supplier followed by consumption in descending order
	 * 
	 * @return
	 * @throws WTException
	 */
	public Collection getSortCriteria() throws WTException {
		FlexType bomType = FlexTypeCache.getFlexTypeRoot("BOM");
		List<String> sortList = new ArrayList();
		sortList.add("FLEXBOMLINK.SORTINGNUMBER");
		sortList.add("FLEXBOMLINK.DIMENSIONID");
		//sortList.add("COLORWAYCOLUMN:ASC");
		
		/*sortList.add(FLEXBOMLINK + bomType.getAttribute("materialDescription").getColumnName());
		sortList.add("LCSSUPPLIERMASTER.SUPPLIERNAME");
		sortList.add(FLEXBOMLINK + bomType.getAttribute("quantity").getColumnName() + ":DESC");*/
		// Return SortList
		return sortList;
	}

	/**
	 * Method to create all possible variation combinations that user select while
	 * generating TP and return collection of each possible combination as a list.
	 * 
	 * @param dimensionMap
	 * @return
	 */
	public Collection createVariationCombination(Map<String, Collection<String>> dimensionMap) {
		LOGGER.debug("In createVariationCombination method - Start ");
		// Initializations
		Collection cVariations = new ArrayList();
		Collection skuList = dimensionMap.get(MCIDimensionHelper.SKU);
		Collection size1List = dimensionMap.get(MCIDimensionHelper.SIZE1);
		Collection destList = dimensionMap.get(MCIDimensionHelper.DESTINATION);

		String[] skuArray;
		String[] size1Array;
		String[] destArray;

		skuArray = getVariationArray(skuList);
		size1Array = getVariationArray(size1List);
		destArray = getVariationArray(destList);

		String[][] combinations = new String[][] { skuArray, size1Array, destArray };
		int[] indices = new int[combinations.length];
		int currentIndex = indices.length - 1;
		String s = "";
		StringBuilder sb = new StringBuilder();
		outerProcess: while (true) {
			for (int i = 0; i < combinations.length; i++) {
				s = combinations[i][indices[i]] + "|~*~|";
				sb.append(s);
			}
			cVariations.add(sb.toString());
			while (true) {
				// Increase current index
				indices[currentIndex]++;
				// If index too big, set itself and everything right of it to 0 and move left
				if (indices[currentIndex] >= combinations[currentIndex].length) {
					for (int j = currentIndex; j < indices.length; j++) {
						indices[j] = 0;
					}
					currentIndex--;
				} else {
					// If index is allowed, move as far right as possible and process next
					// combination
					while (currentIndex < indices.length - 1) {
						// Increment the index to move to next element
						currentIndex++;
					}
					break;
				}
				// If we cannot move left anymore, we're finished
				if (currentIndex == -1) {
					break outerProcess;
				}
			}
			sb = new StringBuilder();
		}
		LOGGER.debug("In createVariationCombination method - cVariations= " + cVariations);
		LOGGER.debug("In createVariationCombination method - End ");
		// Return the variation combinations
		return cVariations;
	}

	/**
	 * Method to get variation array
	 * 
	 * @param varList
	 * @return
	 */
	private String[] getVariationArray(Collection varList) {
		String[] varArray;
		if (!varList.isEmpty()) {
			varArray = (String[]) varList.toArray(new String[varList.size()]);
		} else {
			varArray = new String[] { "-" };
		}
		return varArray;
	}

	/**
	 * This method to use calculate Price based on the precedence
	 * 
	 * @param row
	 * @param bomPart
	 * @throws WTException
	 */
	public void calculatePrice(FlexObject row, Date reqDate, String priceKey, String overrideKey, String quantityKey,
			String lossAdjustmentKey, String rowTotalKey, String cifPercentKey, String cifPriceKey, FlexBOMPart bomPart)
			throws WTException {
		LOGGER.debug("In calculatePrice method - Start ");

		double materialPrice = FormatHelper.parseDouble(row.getData(priceKey));
		double priceOverride = FormatHelper.parseDouble(row.getData(overrideKey));
		double quantity = FormatHelper.parseDouble(row.getData(quantityKey));
		double lossAdjustment = FormatHelper.parseDouble(row.getData(lossAdjustmentKey));
		double cifPercent = FormatHelper.parseDouble(row.getData(FLEXBOMLINK + cifPercentKey));
		double cifPrice;

		Collection matSups = new ArrayList();
		Collection matSupColors = new ArrayList();
		Map matSupColor = new HashMap();
		Map matSup = new HashMap();
		String materialSupplierMasterId = "";
		String materialColorId;

		materialColorId = FormatHelper.format(row.getData("FLEXBOMLINK.IDA3G5"));
		materialSupplierMasterId = FormatHelper.format(row.getData("LCSMATERIALSUPPLIERMASTER.IDA2A2"));

		// Get the material supplier master object from flexobject
		LCSMaterialSupplierMaster matSupMaster = (LCSMaterialSupplierMaster) LCSMaterialSupplierQuery
				.findObjectById("com.lcs.wc.material.LCSMaterialSupplierMaster:" + materialSupplierMasterId);
		// Get the material supplier master object for the materialColorId from
		// flexobject
		String strMatSupIDForMatCol = findMaterialSupplierIdForMaterialColor(materialColorId);
		// Check if mateiral color's material supplier master and material supplier
		// master in flexobject matches
		// If, no then blank the material color id, else this will pull wrong material
		// color price value
		// Ex scenarion - base row - add mat1 - sup1 - col1, and override and change
		// material supplier to mat2 - sup2
		// without changing the material color,
		// in this case if we don't blank the material color id, then it pulls the price
		// from base row mat 1-sup1-col1's price
		if (FormatHelper.hasContent(strMatSupIDForMatCol)) {
			if (!matSupMaster.toString().equals(strMatSupIDForMatCol)) {
				materialColorId = "";
			}
		}

		matSup.put("materialSupplierMasterId", materialSupplierMasterId);
		matSupColor.put("materialSupplierMasterId", materialSupplierMasterId);
		matSupColor.put("materialColorId", materialColorId);
		matSups.add(matSup);
		matSupColors.add(matSupColor);
		MaterialPriceList mpl = new MaterialPriceList(matSups, matSupColors, reqDate);
		materialPrice = mpl.getPrice(materialSupplierMasterId, materialColorId);

		LOGGER.debug("In calculatePrice method - materialPrice= " + materialPrice);

		// convert the materialPrice to a BigDecimal
		BigDecimal mpbd = new BigDecimal(materialPrice);
		row.setData(priceKey, mpbd.toString());

		// price override is not less than 0, then take material price from priceOver
		if (priceOverride > 0) {
			materialPrice = priceOverride;
		}

		// SPORTMASTER EXTENSION OF VRD START
		cifPrice = materialPrice * (1.0d + (cifPercent / 100));
		LOGGER.debug("In calculatePrice method - cifPrice= " + cifPrice);
		// Calculate the price based on lossadjustment
		if (lossAdjustment != 0) {
			// quantity = quantity + (quantity * lossAdjustment);
			quantity = quantity + (quantity * lossAdjustment / 100);
		}
		LOGGER.debug("In calculatePrice method - quantity= " + quantity);
		// Row total calculation
		double rowTotal = quantity * cifPrice;
		LOGGER.debug("In calculatePrice method - rowTotal= " + rowTotal);

		row.put(rowTotalKey, String.valueOf(rowTotal));

		row.put(FLEXBOMLINK + cifPriceKey.toUpperCase(), cifPrice);
		LOGGER.debug("In calculatePrice method - End ");
	}

	/**
	 * Method to get material supplier id for material color
	 * 
	 * @param materialColorId
	 * @return
	 * @throws WTException
	 */
	public static String findMaterialSupplierIdForMaterialColor(String materialColorId) throws WTException {
		String numeric = FormatHelper.getNumericFromOid(materialColorId);
		// This is same as ootb LCSMaterialColorQuery.findMaterialSupplierIdForMaterialColor query, as OOTB
		// Query was throwing error, copied the code explicitly
		// id = "OR:com.lcs.wc.material.LCSMaterialSupplierMaster: " because of the extra space in OOTB Query
		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendFromTable(LCSMaterialColor.class);
		statement.appendFromTable(LCSMaterialMaster.class);
		statement.appendFromTable(LCSSupplierMaster.class);
		statement.appendFromTable(LCSMaterialSupplierMaster.class);

		statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplierMaster.class, PERSIST_INFO_OID));

		statement.appendJoin(new QueryColumn(LCSMaterialColor.class, "materialMasterReference.key.id"),
				new QueryColumn(LCSMaterialMaster.class, PERSIST_INFO_OID));
		statement.appendJoin(new QueryColumn(LCSMaterialColor.class, "supplierMasterReference.key.id"),
				new QueryColumn(LCSSupplierMaster.class, PERSIST_INFO_OID));

		statement.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class, "materialMasterReference.key.id"),
				new QueryColumn(LCSMaterialMaster.class, PERSIST_INFO_OID));
		statement.appendJoin(new QueryColumn(LCSMaterialSupplierMaster.class, "supplierMasterReference.key.id"),
				new QueryColumn(LCSSupplierMaster.class, PERSIST_INFO_OID));

		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterialColor.class, PERSIST_INFO_OID), "?", "="),
				new Long(numeric));
		
		Collection<?> results = LCSQuery.runDirectQuery(statement).getResults();
		String id = null;
		if (results.size() > 0) {
			id = "com.lcs.wc.material.LCSMaterialSupplierMaster:"
					+ ((FlexObject) results.iterator().next()).getData("LCSMATERIALSUPPLIERMASTER.IDA2A2");
		}

		return id;
	}
}
