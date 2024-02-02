/*
 * HBISPPackCaseBOMGenerator.java
 *
 * Created on Dec 14, 2018, 12:58 PM
 */

package com.hbi.wc.flexbom.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wt.fc.ReferenceFactory;
//import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTMessage;

import com.lcs.wc.client.web.FlexTypeGenerator;
import com.lcs.wc.client.web.TableColumn;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.BOMColorTableColumn;
import com.lcs.wc.flexbom.BOMPartNameTableColumn;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.gen.BOMPDFContentGenerator;
import com.lcs.wc.flexbom.gen.BomDataGenerator;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.LCSSKUQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.RB;
import com.lcs.wc.util.SortHelper;
import com.lcs.wc.util.VersionHelper;

/**
 *
 * @author John S. Reeno From UST
 * @Date Dec 4, 2018, 12:58 PM
 * 
 *
 *       This class generates the Sales BOM specification details.
 *       It generates the table columns and the data that needs to be 
 *       displayed in the Pdf Page
 * 
 */
public class HBISPSalesBOMGenerator extends BomDataGenerator implements Comparator<String>{

	public static final String MATERIAL_TYPE_PATH = "MATERIAL_TYPE_PATH";

	protected static String SIZE_DISPLAY_COL = "";

	private static final String DISPLAY_VAL = "DISPLAY_VAL";
	private static final String Colorways = "Colorways";

	public String SIZES = "SIZES";
	public String sizeLabel = WTMessage.getLocalizedMessage ( RB.SOURCING, "sizesColumn_LBL", RB.objA ) ;

	public String BOMLINK_DIM_NAME = "FLEXBOMLINK.DIMENSIONNAME";
	public String BOMLINK_SIZE1 = "FLEXBOMLINK.SIZE1";
	public String SIZE1 = ":SIZE1";
	public String SKU = ":SKU";
	public String SKU_SIZE1 = ":SKU:SIZE1";
	public String BOMLINK_IDA3E5 = "FLEXBOMLINK.IDA3E5";
	public String BOMLINK_COLORWAYNAME = "FLEXBOMLINK.COLORWAYNAME";

	@SuppressWarnings("rawtypes")
	private Collection sizes = new ArrayList();
	private String source = "";

//	private String COMP_NAME_ATT = "ATT1";
//	private String COMP_NAME_DISPLAY = "ATT1";
//
//	private String COLOR_NAME_ATT = "ATT4";
	
	//Code Upgrade by Wipro Team
    private String COMP_NAME_ATT = "ptc_str_4typeInfoFlexBOMLink";
	private String COMP_NAME_DISPLAY = "ptc_str_4typeInfoFlexBOMLink";
	private String COLOR_NAME_ATT = "ptc_str_7typeInfoFlexBOMLink";

	boolean sizeBoolean = false;
	boolean colorBoolean = false;
	/** Creates a new instance of CWMatColorGenerator */
	private FlexType materialType = null;
	private FlexType bomType = null;
	private FlexType bomSalesType = null;
	private LCSProduct product = null;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Collection<Object>> valsets = new HashMap();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Collection<FlexObject> sortedCollection = new ArrayList();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<String> sortOrder = new ArrayList();
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Collection<FlexObject> mergedRowsColl = new ArrayList();

	private static final String COLORID = "COLORID";
	private static final String COLORHEX = "COLORHEX";
	private static final String COLORTHUMB = "COLORTHUMB";

	@SuppressWarnings("rawtypes")
	private Map <String, Collection> paramsGlobal = new HashMap<String, Collection>();

	public boolean USE_DEFAULT_COLUMNS = LCSProperties.getBoolean("com.lcs.wc.flexbom.gen.SizeGenerator.useDefaultColumns");

	public boolean DEBUG = LCSProperties.getBoolean("com.lcs.wc.flexbom.gen.HBISPPackCaseBOMGenerator.verbose");

	public float partNameWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.SizeGenerator.partNameWidth", "1.5"))).floatValue();
	public float materialNameWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.SizeGenerator.materialNameWidth", "1.25"))).floatValue();
	public float supplierNameWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.SizeGenerator.supplierNameWidth", "1.25"))).floatValue();
	public float sizeWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.SizeGenerator.sizeWidth", "0.75"))).floatValue();
	public float colorwayWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.colorwayWidth", "0.75"))).floatValue();

	public int imageWidth = Integer.parseInt(LCSProperties.get("com.lcs.wc.flexbom.gen.SizeGenerator.matThumbWidth", "75"));
	public int imageHeight = Integer.parseInt(LCSProperties.get("com.lcs.wc.flexbom.gen.SizeGenerator.matThumbHeight", "0"));

	public String SALES_BOM_TYPE = LCSProperties.get("bomreport.sales.salesproduct.bomtypes");
	public String ZPPK = LCSProperties.get("com.hbi.wc.product.zppkMaterialType","Shipper/Assorted Case/Pallet");
	public String ZFRT = LCSProperties.get("com.hbi.wc.product.zfrtMaterialType","Consumer Selling Unit");

	//Default constructor
	public HBISPSalesBOMGenerator() {
	}
	//Overloaded Constructor
	public HBISPSalesBOMGenerator(List<String> sortOrder) {
		this.sortOrder=sortOrder;
	}

	@SuppressWarnings("rawtypes")
	public Collection getBOMData()throws WTException {
		return this.dataSet;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection getTableColumns() throws WTException {
		ArrayList viewAtts = new ArrayList();
		Map viewColumns = new HashMap();

		Collection columns = new ArrayList();

		if(this.view != null){
			debug("view attributes: " + this.view.getAttributes());
			viewAtts.addAll(this.view.getAttributes());
			if(viewAtts.contains("partName")){
				viewAtts.remove("partName");
			}
			if(viewAtts.contains("BOM.partName")){
				viewAtts.remove("BOM.partName");
			}
			if(viewAtts.contains("supplierName")){
				viewAtts.remove("supplierName");
			}
			if(viewAtts.contains("materialDescription")){
				viewAtts.remove("materialDescription");
			}
			if(viewAtts.contains("Material.hbiComment")){
				viewAtts.remove("Material.hbiComment");
			}
			String colName = "";
			boolean hasSizeColumn = false;
			for(int i = 0; i < viewAtts.size(); i++){
				colName = (String)viewAtts.get(i);
				if("size1Dim".equals(colName) || "size2Dim".equals(colName)){
					viewAtts.remove(i);
					viewAtts.add(i, "SIZE");
					hasSizeColumn = true;
					break;
				}
			}

			if(!hasSizeColumn){
				viewAtts.add("SIZE");

			}

			viewColumns.putAll(getViewColumns());

			debug("viewColumn keys: " + viewColumns.keySet());

		}
		if(!viewAtts.contains("Colorways")){
			viewAtts.add("Colorways");
		}
		if(this.sizes != null && this.sizes.size() > 0){
			viewAtts.add(0, SIZES);
		}
		viewAtts.add(0, "partName");

		if(this.view == null){
			if(USE_DEFAULT_COLUMNS){
				TableColumn column = null;
				FlexTypeGenerator flexg = new FlexTypeGenerator();
				FlexTypeAttribute att = null;

				att = materialType.getAttribute("materialPrice");
				column = flexg.createTableColumn(null, att, materialType, false, "LCSMATERIALSUPPLIER");
				viewColumns.put("Material.price", column);


				att = materialType.getAttribute("unitOfMeasure");
				column = flexg.createTableColumn(null, att, materialType, false, "LCSMATERIAL");
				viewColumns.put("Material.unitOfMeasure", column);

				att = bomType.getAttribute("quantity");
				column = flexg.createTableColumn(null, att, bomType, false, "FLEXBOMLINK");
				viewColumns.put("BOM.quantity", column);

				viewAtts.add("Material.price");
				viewAtts.add("Material.unitOfMeasure");
			}

			viewAtts.add("SIZE");
		}

		TableColumn column = new TableColumn();

		column = new BOMPartNameTableColumn();
		column.setHeaderLabel(COMP_NAME_DISPLAY);
		column.setTableIndex("FLEXBOMLINK." + COMP_NAME_ATT);
		((BOMPartNameTableColumn)column).setSubComponetIndex("FLEXBOMLINK.MASTERBRANCHID");
		((BOMPartNameTableColumn)column).setComplexMaterialIndex("FLEXBOMLINK.MASTERBRANCH");
		((BOMPartNameTableColumn)column).setLinkedBOMIndex("FLEXBOMLINK.LINKEDBOM");

		column.setDisplayed(true);
		column.setSpecialClassIndex("CLASS_OVERRIDE");
		column.setPdfColumnWidthRatio(partNameWidth);
		viewColumns.put("partName", column);
		if(!(section.equals("components")))  { 
			viewAtts.clear();
			viewAtts.add("BOM.partName");
		}
		column = new TableColumn();
		column.setHeaderLabel("Size Variation");
		column.setTableIndex(BOMLINK_SIZE1);
		column.setDisplayed(true);
		column.setFormat(FormatHelper.MOA_FORMAT);
		viewColumns.put(SIZES, column);

		if(this.getColorways() != null && this.getColorways().size() > 0){
			ReferenceFactory rf = new ReferenceFactory();
			Map<String, LCSSKU> cwId_SKU_map = new HashMap<String, LCSSKU>();
			if(!this.getColorways().isEmpty()){
				Collection<LCSSKU> skus = LCSSKUQuery.getSKURevA(this.getColorways());
				for (LCSSKU sku : skus){
					String refString = rf.getReferenceString(sku.getMasterReference());
					String idString = refString.substring(refString.lastIndexOf(":")+1);
					cwId_SKU_map.put(idString, sku);
				}
			}
			column = new TableColumn();
			column.setHeaderLabel("Color Variation");
			column.setTableIndex(BOMLINK_COLORWAYNAME);
			column.setDisplayed(true);
			column.setFormat(FormatHelper.MOA_FORMAT);
			viewColumns.put("Colorways", column);

			for(String cwId:this.getColorways()){
				BOMColorTableColumn colorColumn = new BOMColorTableColumn();
				colorColumn.setDisplayed(true);
				colorColumn.setTableIndex("LCSCOLOR.COLORNAME");
				colorColumn.setDescriptionIndex(cwId + "." + COLOR_NAME_ATT);
				colorColumn.setHeaderLabel((String)cwId_SKU_map.get(cwId).getValue("skuName"));
				colorColumn.setLinkMethod("viewColor");
				colorColumn.setLinkTableIndex(cwId + "." + COLORID);
				colorColumn.setLinkMethodPrefix("OR:com.lcs.wc.color.LCSColor:");
				colorColumn.setColumnWidth("1%");
				colorColumn.setWrapping(false);
				colorColumn.setBgColorIndex(cwId + "." + COLORHEX);
				colorColumn.setUseColorCell(true);
				colorColumn.setAlign("center");
				colorColumn.setImageIndex(cwId + "." + COLORTHUMB);
				colorColumn.setSpecialClassIndex(cwId + "_CLASS_OVERRIDE");
				colorColumn.setPdfColumnWidthRatio(colorwayWidth);
				colorColumn.setUseColorCell(this.useColorSwatch);
				colorColumn.setFormatHTML(false);
				viewColumns.put(cwId + "." + DISPLAY_VAL, colorColumn);
			}
		}
		debug("Getting columns...sizes: " + sizes);
		Iterator vi = viewAtts.iterator();
		String att = "";
		while(vi.hasNext()){
			att = (String)vi.next();
			if("Colorways".equals(att)){
				if(this.getColorways() != null && this.getColorways().size() > 0){
					columns.add(viewColumns.get(att));
					for(String cwId:this.getColorways()){
						if(viewColumns.get(cwId + "." + DISPLAY_VAL) != null){
							columns.add(viewColumns.get(cwId + "." + DISPLAY_VAL));
						}
					}
				}
			}
			else{
				if(viewColumns.get(att) != null){
					columns.add(viewColumns.get(att));
				}
			}
		}

		return columns;
	}

	@SuppressWarnings({ "rawtypes", "static-access" })
	private void printOverrides(){
		Map orMap = getOverRideMap();
		Iterator ids = orMap.keySet().iterator();
		String tlId = "";
		FlexObject obj = null;
		Collection ors = null;
		while(ids.hasNext()){
			tlId = (String)ids.next();
			//LCSLog.debug("tlid: " + tlId);
			ors = (Collection)orMap.get(tlId);
			Iterator t = ors.iterator();
			while(t.hasNext()){
				obj = (FlexObject)t.next();
				//LCSLog.debug("\torId: " + obj.getString(this.DIMID_COL));
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void init(Map params) throws WTException {
		super.init(params);
		//debug("----params" + params);        
		if(params != null){
			if(params.get(MATERIAL_TYPE_PATH) != null){
				materialType = FlexTypeCache.getFlexTypeFromPath((String)params.get(MATERIAL_TYPE_PATH));
			}
			else{
				materialType = FlexTypeCache.getFlexTypeRoot("Material");
			}
			bomType = FlexTypeCache.getFlexTypeRoot("BOM");
			bomSalesType = FlexTypeCache.getFlexTypeFromPath(SALES_BOM_TYPE);

			COLOR_NAME_ATT = bomType.getAttribute("colorDescription").getVariableName();
			COMP_NAME_DISPLAY = bomSalesType.getAttribute("partName").getAttDisplay();
			COMP_NAME_ATT = bomSalesType.getAttribute("partName").getVariableName();

			paramsGlobal.putAll(params);
			FlexBOMPart part = (FlexBOMPart)params.get(BOMPDFContentGenerator.BOM_PART);
			product = (LCSProduct)VersionHelper.latestIterationOf(part.getOwnerMaster());
			if(this.getSources() != null && this.getSources().size() > 0){
				this.source = (String)this.getSources().iterator().next();
			}

			this.sizes = new ArrayList();
			if("SIZE1".equalsIgnoreCase(this.sizeAtt)){
				sizes = this.getSizes1();
			}
			if(this.dataSet != null){
				//printOverrides();
				this.dataSet = filterDataSet(this.dataSet, this.sizeAtt);
				this.dataSet = groupDataToBranchId(this.dataSet, "FLEXBOMLINK.BRANCHID", "FLEXBOMLINK.MASTERBRANCHID", "FLEXBOMLINK.SORTINGNUMBER");

				//printOverrides();
				

				/*            Map materialSizeMap = getMaterialSizeMapping();
                SIZE_DISPLAY_COL = this.displayAttCol;

                debug("materialSizeMap:\n " + materialSizeMap);

                debug("\n\n"); */

				Collection processedData = new ArrayList();

				Collection tls = this.getTLBranches();
				Iterator i = tls.iterator();
				FlexObject tlb = null;
				while(i.hasNext()){
					tlb = (FlexObject)i.next();
					//debug("\n\n");
					//debug("tlb:  " + tlb);
					//debug("materialSizeMap:  " + materialSizeMap);
					//processedData.addAll(processBranch(tlb, materialSizeMap));
					processedData.addAll(processBranch1(tlb, paramsGlobal));
				}

				this.dataSet = processedData;
			}
		}
	}
	/**
	 * @param topLevel
	 * @return
	 * @throws WTException
	 * @author John S. Reeno
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection processBranch1(FlexObject topLevel, Map <String, Collection> params) throws WTException{
		String dimId = topLevel.getString("FLEXBOMLINK.DIMENSIONID");

		Collection<String> sizeArray = (Collection)params.get("SIZES1");
		Collection<String> colorArray = (Collection)params.get("COLORWAYS");
		Collection allRows = new ArrayList();
		//top Row
		allRows.add(topLevel);
		Map ovr= this.getOverRideMap();
		Collection<FlexObject> dimRows = (Collection<FlexObject>) ovr.get(dimId);
		if(dimRows.isEmpty()){
			if((section).equals("components") && (sizeArray.size()>0) && !(colorArray.size()>0)){
				Collection<FlexObject> extraSizes = new ArrayList();
				extraSizes = extraSizeRows(topLevel,sizeArray);
				allRows.addAll(extraSizes);
			}else
			if((section).equals("components") && (colorArray.size()>0) && !(sizeArray.size()>0)){
				Collection<FlexObject> extraColors = new ArrayList();
				extraColors = extraColorRows(topLevel,colorArray);
				allRows.addAll(extraColors);
			}
			if((section).equals("components") && (colorArray.size()>0) && (sizeArray.size()>0)){
				Collection<FlexObject> extraSizeNColor = new ArrayList();
				extraSizeNColor = extraSizeAndColorRows(topLevel,sizeArray,colorArray);
				allRows.addAll(extraSizeNColor);
			}
		}
		if (!dimRows.isEmpty()) {
			//Dim over rows of a top row
			Iterator itr = dimRows.iterator();
			Collection<String> remainingSizes = new ArrayList(sizeArray);
			Collection<String> remainingColors = new ArrayList(colorArray);
			Collection<FlexObject> sizeDimRows = new ArrayList();
			while(itr.hasNext()){
				FlexObject fob = (FlexObject)itr.next();
				if((section).equals("components") && (sizeArray.size()>0) && !(colorArray.size()>0)){
					if(sizeArray!=null){
						Iterator sizeItr = sizeArray.iterator();
						String size = null;
						while(sizeItr.hasNext()){
							size = (String)sizeItr.next();
							if (fob.get(BOMLINK_DIM_NAME).equals(SIZE1) && fob.get(BOMLINK_SIZE1).equals(size)){
								fob=mergeTopLevelAndDimRows(topLevel,fob);
								sizeDimRows.add(fob);
								remainingSizes.remove(size);
							}
						}
					}
				}else
					if((section).equals("components") && (colorArray.size()>0) && !(sizeArray.size()>0)){
						if(colorArray != null){
							Iterator colorItr = colorArray.iterator();
							String skuid = null;
							while(colorItr.hasNext()){
								skuid = (String)colorItr.next();
								if (fob.get(BOMLINK_DIM_NAME).equals(SKU) && fob.getString(BOMLINK_IDA3E5).equals(skuid)){
									String colorwayDesc = fob.getString("FLEXBOMLINK."+COLOR_NAME_ATT);
									String Oid = fob.getString(BOMLINK_IDA3E5);
									fob.setData(Oid+"."+COLOR_NAME_ATT,colorwayDesc);
									Collection<String> otherColorsColl = new ArrayList<String>();
									otherColorsColl.addAll(colorArray);
									/**
									 * Place dashed lines on other colorway overrides
									 */
									for(String otherColor:otherColorsColl){
										if(!Oid.equals(otherColor)){
											fob.setData(otherColor+"."+COLOR_NAME_ATT,"----------");
										}
									}

									topLevel.setData(Oid+"."+COLOR_NAME_ATT,colorwayDesc);
									if(FormatHelper.hasContent(Oid)){
										LCSPartMaster skuPart = (LCSPartMaster)LCSQuery.findObjectById("com.lcs.wc.part.LCSPartMaster:"+Oid);
										if(skuPart != null){
											String skuName = skuPart.getIdentity();
											String [] str = skuName.split(",");
											skuName = str[1];
											fob.setData(BOMLINK_COLORWAYNAME,skuName.trim());
											fob=mergeTopLevelAndDimRows(topLevel,fob);
											allRows.add(fob);
											remainingColors.remove(skuid);
										}
									}
								}
							}
						}
					}
			}

			if(remainingSizes.size()>0 || remainingColors.size()>0){
				Collection<FlexObject> sizesExtra = new ArrayList<FlexObject>();
				Collection<FlexObject> colorsExtra = new ArrayList<FlexObject>();
				List<FlexObject> sortedFlexObject = new ArrayList<FlexObject>();
				if((section).equals("components") && (sizeArray.size()>0) && !(colorArray.size()>0)){
					if(sizeDimRows.size()>0){
						sortedFlexObject.addAll(sizeDimRows);
					}
					sizesExtra = getColorwayOrSizeRemainingRows(remainingSizes,null,topLevel,null);
					sortedFlexObject.addAll(sizesExtra);
					sortOrder = new ArrayList();
					sortOrder.addAll(sizeArray);
					if(sortedFlexObject.size()>0 && sortOrder.size()>0){
						for(String size:sortOrder){
							for(FlexObject flexOb:sortedFlexObject){
								if(size.equals(flexOb.getString(BOMLINK_SIZE1))){
									allRows.add(flexOb);
								}
							}
						}
					}
				}else if((section).equals("components") && (colorArray.size()>0) && !(sizeArray.size()>0)){
					colorsExtra = getColorwayOrSizeRemainingRows(null,remainingColors,topLevel,colorArray);
					allRows.addAll(colorsExtra);
				}
			}
			if((section).equals("components") && (sizeArray.size()>0) && (colorArray.size()>0)){
				sortOrder = new ArrayList();
				sortOrder.addAll(sizeArray);
				sortedCollection = new ArrayList<FlexObject>();
				mergedRowsColl = new ArrayList<FlexObject>();
				mergedRowsColl = getSizeAndColorMergedRows(topLevel, dimRows, sizeArray,colorArray);
				String colorwayName = BOMLINK_COLORWAYNAME;
				String sizeName = BOMLINK_SIZE1;
				List<String> sortList = new ArrayList<String>();
				sortList.add(colorwayName);
				sortList.add(sizeName);
				valsets = new HashMap();
				valsets = buildUniqueValueTable(mergedRowsColl, sortList);
				sortFlexObjects(mergedRowsColl,sortList,0);
				allRows.addAll(sortedCollection);
			}
		}
		return allRows;
	}

	/**
	 * @method Sort the rows in Colorway and then by Sizes
	 * @param objs
	 * @param sortList
	 * @param level
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sortFlexObjects(Collection<FlexObject> objs, List<String> sortList, int level) {
		if (objs.size() > 0) {
			boolean reverseOrder = false;
			String fullAtt = (String)sortList.get(level);
			String att = fullAtt;
			if (att.endsWith(":ASC")) {
				att = att.substring(0, att.lastIndexOf(':'));
			} else if (att.endsWith(":DESC")) {
				att = att.substring(0, att.lastIndexOf(':'));
				reverseOrder = true;
			}
			List gvals = new ArrayList();
			if (valsets.get(att) != null) {
				gvals = new ArrayList((Collection)valsets.get(att));
			}
			if (att.indexOf(".SIZE1") > -1) {
				gvals = (List)SortHelper.sortStringsNonLex(gvals);
				Collections.sort(gvals, new HBISPSalesBOMGenerator(sortOrder));
			}
			if(att.indexOf(".COLORWAYNAME")>-1){
				Collections.sort(gvals);
			}
			for (Object cval : gvals) {
				Collection<FlexObject> rows = getObjsMatchingCriteria(objs, att, cval);
				if (level == sortList.size() - 1)
				{
					sortedCollection.addAll(rows);
				}
				else
				{
					sortFlexObjects(rows, sortList, level + 1);
				}
			}
		} 
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<FlexObject> getObjsMatchingCriteria(Collection<FlexObject> data, String index, Object val)
	{
		Collection<FlexObject> result = new ArrayList();

		for (FlexObject row : data) {
			Object tval = row.get(index);
			if (((tval != null) && (tval.equals(val))) || ((val == null) && (tval == null))) {
				result.add(row);
			}
		}
		return result;
	}

	/**
	 * @method Sort the sizes in custom order
	 */
	public int compare(String s1, String s2)
	{
		return sortOrder.indexOf(s1) - sortOrder.indexOf(s2);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Collection<Object>> buildUniqueValueTable(Collection<FlexObject> objs, Collection<String> list)
	{
		Map<String, Collection<Object>> valset = new HashMap();
		String group;
		for (Iterator i$ = list.iterator(); i$.hasNext();) { group = (String)i$.next();
		if ((group.endsWith(":ASC")) || (group.endsWith(":DESC"))) {
			group = group.substring(0, group.lastIndexOf(':'));
		}
		for (FlexObject row : objs)
			if (row.get(group) != null) {
				Object val = row.get(group);
				Collection<Object> values = (Collection)valset.get(group);
				if (values == null) {
					values = new ArrayList();
				}
				if (!values.contains(val)) {
					values.add(val);
				}
				valset.put(group, values);
			}
		}
		return valset;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Collection filterDataSet(Collection data, String sAtt){
		FlexObject fobj = null;
		Collection filteredData = new ArrayList();

		boolean useSource = this.getSources() != null && this.getSources().size() > 0;

		sAtt = sAtt.toUpperCase();

		Iterator i = data.iterator();
		while(i.hasNext()){
			fobj = (FlexObject)i.next();
			debug("sAtt: " + sAtt + "     dimCol: " + fobj.getString(DIM_COL));
			if(!FormatHelper.hasContent(fobj.getString(DIM_COL)) || ((useSource && ((":SOURCE:" + sAtt).equals(fobj.getString(DIM_COL)) || ":SOURCE".equals(fobj.getString(DIM_COL)))) || (":" + sAtt).equals(fobj.getString(DIM_COL))) || 
					(SKU).equals(fobj.getString(DIM_COL))||
					(":SKU:"+sAtt).equals(fobj.getString(DIM_COL))){
				filteredData.add(fobj);
			}
		}
		return filteredData;
	}

	private FlexObject mergeSizeAndColorRows(FlexObject dimObj, Collection<FlexObject> dimRows, String color) throws WTException
	{
		FlexObject foundFL = new FlexObject();
		for(FlexObject fl: dimRows){
			if(fl.getString(BOMLINK_IDA3E5).equals(color)){
				foundFL = fl;
			}
		}
		FlexObject fobMerged = dimObj.dup();
		String dimName=fobMerged.getString(BOMLINK_DIM_NAME);
		String colorwayName = getColorwayName(color);

		if(FormatHelper.hasContent(dimName) && dimName.equals(SKU_SIZE1) &&
				!FormatHelper.hasContent(fobMerged.getString("FLEXBOMLINK."+COLOR_NAME_ATT))){
			fobMerged.setData(BOMLINK_COLORWAYNAME, colorwayName);
			fobMerged.setData(color+"."+COLOR_NAME_ATT,foundFL.getString("FLEXBOMLINK."+COLOR_NAME_ATT));
		}else if(FormatHelper.hasContent(dimName) && dimName.equals(SKU_SIZE1) &&
				FormatHelper.hasContent(fobMerged.getString("FLEXBOMLINK."+COLOR_NAME_ATT))){
			fobMerged.setData(BOMLINK_COLORWAYNAME, colorwayName);
			fobMerged.setData(color+"."+COLOR_NAME_ATT,fobMerged.getString("FLEXBOMLINK."+COLOR_NAME_ATT));
		}
		if(FormatHelper.hasContent(dimName) && dimName.equals(SIZE1)){
			fobMerged.setData(color+"."+COLOR_NAME_ATT, foundFL.getString("FLEXBOMLINK."+COLOR_NAME_ATT));
			fobMerged.setData(BOMLINK_COLORWAYNAME, colorwayName);
		}
		return fobMerged;
	}
	@SuppressWarnings("rawtypes")
	private boolean findSizeDimensionInBranches(Collection<FlexObject> dimRows){
		Iterator dimIter = dimRows.iterator();
		boolean sizeExists = false;
		while(dimIter.hasNext()){
			FlexObject flex = (FlexObject)dimIter.next();
			if(flex.getString(BOMLINK_DIM_NAME).equals(SIZE1)){
				sizeExists = true;
			}
		}
		return sizeExists;
	}
	@SuppressWarnings("rawtypes")
	private boolean findColorDimensionInBranches(Collection<FlexObject> dimRows){
		Iterator dimIter = dimRows.iterator();
		boolean colorExists = false;
		while(dimIter.hasNext()){
			FlexObject flex = (FlexObject)dimIter.next();
			if(flex.getString(BOMLINK_DIM_NAME).equals(SKU)){
				colorExists = true;
			}
		}
		return colorExists;
	}

	private FlexObject mergeTopLevelAndDimRows(FlexObject topLevel, FlexObject dimRow) throws WTException{
		for (String topKey : topLevel.keySet()) {
			for(String dimRowKey : dimRow.keySet()) {
				if(!dimRowKey.equalsIgnoreCase("FLEXBOMLINK."+COMP_NAME_ATT) && topKey.equals(dimRowKey)){
					String value = (String)dimRow.getString(dimRowKey);
					if(!FormatHelper.hasContent(value)){
						dimRow.put(dimRowKey, topLevel.getString(topKey));
					}
				}
			}
		}
		return dimRow;
	}
	private FlexObject mergeSizeAndColorwaySizeRows(FlexObject skusizeFlexObj,Collection<FlexObject> dimRows) throws WTException{
		String size = skusizeFlexObj.getString(BOMLINK_SIZE1);
		for(FlexObject flb:dimRows){
			String dimSize = flb.getString(BOMLINK_SIZE1);			
			if(FormatHelper.hasContent(size) && FormatHelper.hasContent(dimSize) && size.equals(dimSize)){
				for (String skuSizeKey : skusizeFlexObj.keySet()) {
					for(String onlySizeKey : flb.keySet()) {
						if(!onlySizeKey.equalsIgnoreCase("FLEXBOMLINK."+COMP_NAME_ATT) && skuSizeKey.equals(onlySizeKey)){
							String skuSizevalue = (String)skusizeFlexObj.getString(skuSizeKey);
							String dimSizeValue = (String)flb.getString(onlySizeKey);
							String skuSizeValueCheck = (String)skusizeFlexObj.getString(onlySizeKey);
							if(!FormatHelper.hasContent(skuSizevalue)){
								skusizeFlexObj.put(skuSizeKey, flb.getString(onlySizeKey));
							}
							if(FormatHelper.hasContent(dimSizeValue)&&!FormatHelper.hasContent(skuSizeValueCheck)){
								skusizeFlexObj.put(onlySizeKey, dimSizeValue);
							}
						}
					}
				}
			}
		}
		return skusizeFlexObj;
	}
	/**
	 * @param topLevel
	 * @param dimRows
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection getSizeAndColorMergedRows(FlexObject topLevel, Collection<FlexObject> dimRows, Collection<String> sizeArray, 
			Collection<String> colorArray) throws WTException{
		Collection mergedRows = new ArrayList();
		Collection<FlexObject> sortedDimRows = sortDimensionRows(dimRows);
		boolean colorwayVariationExists = findColorDimensionInBranches(dimRows);
		boolean sizeVariationExists = findSizeDimensionInBranches(dimRows);
		Collection<String> remainingCombination = new ArrayList();
		for(String clrStr:colorArray){
			for(String sizeStr:sizeArray){
				String combStr = sizeStr+":"+clrStr;
				remainingCombination.add(combStr);
			}
		}
		for(FlexObject dimRow:sortedDimRows){
			for(String size: sizeArray){
				for(String color: colorArray){
					FlexObject sizeFlexObj = new FlexObject();
					FlexObject colorFlexObj = new FlexObject();
					String skuOid = dimRow.getString(BOMLINK_IDA3E5);
					if(FormatHelper.hasContent(skuOid)){
						skuOid = skuOid.trim();
					}
					String dimName = dimRow.getString(BOMLINK_DIM_NAME);
					if(FormatHelper.hasContent(dimName)){
						dimName = dimName.trim();
					}
					String dimSize = dimRow.getString(BOMLINK_SIZE1);
					if(FormatHelper.hasContent(dimSize)){
						dimSize = dimSize.trim();
					}
					size = size.trim();
					color=color.trim();
					if(FormatHelper.hasContent(dimName) && FormatHelper.hasContent(dimSize) && 
							dimName.equals(SKU_SIZE1) && dimSize.equals(size) && skuOid.equals(color)
							&& !colorwayVariationExists){
						String combStr = size+":"+color;
						if(remainingCombination.contains(combStr)){
							sizeFlexObj = dimRow.dup();
							if(sizeVariationExists){
								sizeFlexObj = mergeSizeAndColorwaySizeRows(sizeFlexObj,dimRows);
							}
							sizeFlexObj = mergeTopLevelAndDimRows(topLevel,sizeFlexObj);
							sizeFlexObj.setData(BOMLINK_SIZE1, size);
							sizeFlexObj.setData(BOMLINK_COLORWAYNAME, getColorwayName(color));
							if(!FormatHelper.hasContent(sizeFlexObj.getString("FLEXBOMLINK."+COLOR_NAME_ATT))){
								Collection<String> otherColorsColl = new ArrayList<String>();
								otherColorsColl.addAll(colorArray);
								/**
								 * Place dashed lines on other colorway overrides
								 */
								for(String otherColor:otherColorsColl){
									if(!color.equals(otherColor)){
										sizeFlexObj.setData(otherColor+"."+COLOR_NAME_ATT,"----------");
									}
								}

							}else{
								sizeFlexObj.setData(color+"."+COLOR_NAME_ATT,sizeFlexObj.getString("FLEXBOMLINK."+COLOR_NAME_ATT));
								topLevel.setData(color+"."+COLOR_NAME_ATT,sizeFlexObj.getString("FLEXBOMLINK."+COLOR_NAME_ATT));
							}
							mergedRows.add(sizeFlexObj); 
							remainingCombination.remove(combStr);
						}
					}
					else
						if(FormatHelper.hasContent(dimName) && FormatHelper.hasContent(dimSize) && 
								dimName.equals(SKU_SIZE1) && dimSize.equals(size) && skuOid.equals(color)
								&& colorwayVariationExists){
							String combStr = size+":"+color;
							if(remainingCombination.contains(combStr)){
								sizeFlexObj = dimRow.dup();
								sizeFlexObj = mergeSizeAndColorRows(sizeFlexObj,sortedDimRows,color);
								topLevel.setData(color+"."+COLOR_NAME_ATT, sizeFlexObj.getString(color+"."+COLOR_NAME_ATT));
								sizeFlexObj = mergeTopLevelAndDimRows(topLevel,sizeFlexObj);
								Collection<String> otherColorsColl = new ArrayList<String>();
								otherColorsColl.addAll(colorArray);
								/**
								 * Place dashed lines on other colorway overrides
								 */
								for(String otherColor:otherColorsColl){
									if(!color.equals(otherColor)){
										sizeFlexObj.setData(otherColor+"."+COLOR_NAME_ATT,"----------");
									}
								}
								mergedRows.add(sizeFlexObj); 
								remainingCombination.remove(combStr);
							}
						}
						else
							if(FormatHelper.hasContent(dimName) && FormatHelper.hasContent(dimSize) && 
									dimName.equals(SIZE1) && dimSize.equals(size) && !colorwayVariationExists){
								String combStr = size+":"+color;
								if(remainingCombination.contains(combStr)){
									String clrName = getColorwayName(color);
									sizeFlexObj = dimRow.dup();
									sizeFlexObj = mergeTopLevelAndDimRows(topLevel,sizeFlexObj);
									sizeFlexObj.setData(BOMLINK_SIZE1, size);
									sizeFlexObj.setData(BOMLINK_COLORWAYNAME, clrName);
									Collection<String> otherColorsColl = new ArrayList<String>();
									otherColorsColl.addAll(colorArray);
									/**
									 * Place dashed lines on other colorway overrides
									 */
									for(String otherColor:otherColorsColl){
										if(!color.equals(otherColor)){
											sizeFlexObj.setData(otherColor+"."+COLOR_NAME_ATT,"----------");
										}
									}
									mergedRows.add(sizeFlexObj); 
									remainingCombination.remove(combStr);
								}
							}

							else
								if(FormatHelper.hasContent(dimName) && FormatHelper.hasContent(dimSize) && 
										dimName.equals(SIZE1) && dimSize.equals(size) && colorwayVariationExists){
									String combStr = size+":"+color;
									if(remainingCombination.contains(combStr)){
										sizeFlexObj = dimRow.dup();
										sizeFlexObj = mergeSizeAndColorRows(sizeFlexObj,sortedDimRows,color);
										sizeFlexObj = mergeTopLevelAndDimRows(topLevel,sizeFlexObj);
										Collection<String> otherColorsColl = new ArrayList<String>();
										otherColorsColl.addAll(colorArray);
										/**
										 * Place dashed lines on other colorway overrides
										 */
										for(String otherColor:otherColorsColl){
											if(!color.equals(otherColor)){
												sizeFlexObj.setData(otherColor+"."+COLOR_NAME_ATT,"----------");
											}
										}
										mergedRows.add(sizeFlexObj); 
										remainingCombination.remove(combStr);
									}
								}

								else
									//Only colorway variations
									if(FormatHelper.hasContent(dimName) && FormatHelper.hasContent(skuOid) && dimName.equals(SKU) && 
											skuOid.equals(color)){
										String dimColrDesc = dimRow.getString("FLEXBOMLINK."+COLOR_NAME_ATT);
										String combStr = size+":"+color; 
										if(remainingCombination.contains(combStr)){
											colorFlexObj=dimRow.dup();
											colorFlexObj = mergeTopLevelAndDimRows(topLevel,colorFlexObj);
											colorFlexObj.setData(BOMLINK_SIZE1, size);
											colorFlexObj.setData(skuOid+"."+COLOR_NAME_ATT, dimColrDesc);
											topLevel.setData(skuOid+"."+COLOR_NAME_ATT, dimColrDesc);
											String colorwayName = getColorwayName(skuOid);
											if(FormatHelper.hasContent(colorwayName)){
												colorFlexObj.setData(BOMLINK_COLORWAYNAME,colorwayName.trim());
											}
											Collection<String> otherColorsColl = new ArrayList<String>();
											otherColorsColl.addAll(colorArray);
											/**
											 * Place dashed lines on other colorway overrides
											 */
											for(String otherColor:otherColorsColl){
												if(!color.equals(otherColor)){
													colorFlexObj.setData(otherColor+"."+COLOR_NAME_ATT,"----------");
												}
											}
											mergedRows.add(colorFlexObj);
											remainingCombination.remove(combStr);
										}
									}
				}
			}
		}
		if (remainingCombination.size()>0){
			Collection remainderSizes = createTopLevelAndSizeRows(topLevel,remainingCombination,colorArray);
			mergedRows.addAll(remainderSizes);
		}
		return mergedRows;
	}
	/**
	 * @method This  method returns the Colorway Name from the Object ID.
	 * @param Oid
	 * @return
	 * @throws WTException
	 */
	private String getColorwayName(String Oid) throws WTException{
		String skuName ="";
		if(FormatHelper.hasContent(Oid)){
			LCSPartMaster skuPart = (LCSPartMaster)LCSQuery.findObjectById("com.lcs.wc.part.LCSPartMaster:"+Oid);
			if(skuPart != null){
				skuName = skuPart.getIdentity();
				String [] str = skuName.split(",");
				skuName = str[1];
				skuName = skuName.trim();
			}
		}
		return skuName;
	}
	/**
	 * @method This method creates extra combinations rows apart from the Dimension rows to be displayed in report
	 * @param topLevel
	 * @param combLeft
	 * @param colorArray
	 * @return
	 * @throws WTException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection createTopLevelAndSizeRows(FlexObject topLevel, Collection<String> combLeft,Collection<String> colorArray) throws WTException{
		Collection nonDimRows = new ArrayList();
		for(String combStr:combLeft){
			String [] str = combStr.split(":");
			String size = str[0];
			String color = str[1];
			String clrName = getColorwayName(color);
			Collection<String> otherColorsColl = new ArrayList<String>();
			otherColorsColl.addAll(colorArray);
			FlexObject topLvlCopy = new FlexObject();
			topLvlCopy = topLevel.dup();
			topLvlCopy.setData("FLEXBOMLINK."+COMP_NAME_ATT, "");
			topLvlCopy.setData(BOMLINK_SIZE1, size);
			topLvlCopy.setData(BOMLINK_COLORWAYNAME, clrName);
			/**
			 * Place dashed lines on other colorway overrides
			 */
			for(String otherColor:otherColorsColl){
				if(!color.equals(otherColor)){
					topLvlCopy.setData(otherColor+"."+COLOR_NAME_ATT,"----------");
				}
			}
			nonDimRows.add(topLvlCopy);
		}
		return nonDimRows;
	}
	@SuppressWarnings("rawtypes")
	private Collection getColorwayOrSizeRemainingRows(Collection<String> remainingSizes,Collection<String> remainingColors,FlexObject topLevel,Collection<String>colorArray) throws WTException{
		Collection<FlexObject> nonDimRows = new ArrayList<FlexObject>();
		if(remainingSizes!=null && remainingSizes.size()>0){
			for(String size:remainingSizes){
				FlexObject topLvlCopySize = new FlexObject();
				topLvlCopySize = topLevel.dup();
				topLvlCopySize.setData("FLEXBOMLINK."+COMP_NAME_ATT, "");
				topLvlCopySize.setData(BOMLINK_SIZE1, size);
				nonDimRows.add(topLvlCopySize);
			}
		}
		if(remainingColors!=null && remainingColors.size()>0){
			for(String color:remainingColors){
				FlexObject topLvlCopyColor = new FlexObject();
				String clrName = getColorwayName(color);
				topLvlCopyColor = topLevel.dup();
				topLvlCopyColor.setData("FLEXBOMLINK."+COMP_NAME_ATT, "");
				topLvlCopyColor.setData(BOMLINK_COLORWAYNAME, clrName);
				topLvlCopyColor.setData(color+"."+COLOR_NAME_ATT,"");
				Collection<String> otherColorsColl = new ArrayList<String>();
				otherColorsColl.addAll(colorArray);
				/**
				 * Place dashed lines on other colorway overrides
				 */
				for(String otherColor:otherColorsColl){
					if(!color.equals(otherColor)){
						topLvlCopyColor.setData(otherColor+"."+COLOR_NAME_ATT,"----------");
					}
				}
				nonDimRows.add(topLvlCopyColor);
			}
		}
		return nonDimRows;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Collection sortDimensionRows(Collection<FlexObject> dimRows) throws WTException{
		Collection<FlexObject> sortedRows = new ArrayList();
		Collection<FlexObject> skuSizeColl = new ArrayList();
		Collection<FlexObject> sizeColl = new ArrayList();
		Collection<FlexObject> skuColl = new ArrayList();
		for(FlexObject flo: dimRows){
			FlexObject segFlexObj = flo.dup();
			if(flo.getString(BOMLINK_DIM_NAME).equals(SKU_SIZE1)){
				skuSizeColl.add(segFlexObj);
			}
			if(flo.getString(BOMLINK_DIM_NAME).equals(SIZE1)){
				sizeColl.add(segFlexObj);
			}

			if(flo.getString(BOMLINK_DIM_NAME).equals(SKU)){
				skuColl.add(segFlexObj);
			}

		}
		sortedRows.addAll(skuSizeColl);
		sortedRows.addAll(sizeColl);
		sortedRows.addAll(skuColl);

		return sortedRows;
	}
	
	@SuppressWarnings("rawtypes")
	private Collection extraSizeRows(FlexObject topLevel,Collection<String>sizeArray) throws WTException {
		Collection<FlexObject> coll = new ArrayList<FlexObject>();
		for(String size:sizeArray){
			FlexObject flexObj = new FlexObject();
			flexObj=topLevel.dup();
			flexObj.setData("FLEXBOMLINK."+COMP_NAME_ATT, "");
			flexObj.setData(BOMLINK_SIZE1, size);
			coll.add(flexObj);
		}
		return coll;
	}
	@SuppressWarnings("rawtypes")
	private Collection extraColorRows(FlexObject topLevel,Collection<String>colorArray) throws WTException{
		Collection<FlexObject> coll = new ArrayList<FlexObject>();
		for(String color:colorArray){
			FlexObject flexObj = new FlexObject();
			flexObj=topLevel.dup();	
			flexObj.setData("FLEXBOMLINK."+COMP_NAME_ATT, "");
			flexObj.setData(BOMLINK_COLORWAYNAME, getColorwayName(color));
			Collection<String> otherColorsColl = new ArrayList<String>();
			otherColorsColl.addAll(colorArray);
			/**
			 * Place dashed lines on other colorway overrides
			 */
			for(String otherColor:otherColorsColl){
				if(!color.equals(otherColor)){
					flexObj.setData(otherColor+"."+COLOR_NAME_ATT,"----------");
				}
			}
			coll.add(flexObj);
		}
		return coll;
	}
	
	private Collection	extraSizeAndColorRows(FlexObject topLevel,Collection<String>sizeArray,Collection<String>colorArray) throws WTException{
		Collection<FlexObject> coll = new ArrayList<FlexObject>();
		for(String size:sizeArray){
			for(String color:colorArray){
				FlexObject flexObj = new FlexObject();
				flexObj=topLevel.dup();
				flexObj.setData("FLEXBOMLINK."+COMP_NAME_ATT, "");
				flexObj.setData(BOMLINK_SIZE1, size);
				flexObj.setData(BOMLINK_COLORWAYNAME, getColorwayName(color));
//				flexObj.setData(color+"."+COLOR_NAME_ATT,"----------");
				Collection<String> otherColorsColl = new ArrayList<String>();
				otherColorsColl.addAll(colorArray);
				/**
				 * Place dashed lines on other colorway overrides
				 */
				for(String otherColor:otherColorsColl){
					if(!color.equals(otherColor)){
						flexObj.setData(otherColor+"."+COLOR_NAME_ATT,"----------");
					}
				}
				coll.add(flexObj);
			}
		}
		sortOrder = new ArrayList();
		sortOrder.addAll(sizeArray);
		sortedCollection = new ArrayList<FlexObject>();
		String colorwayName = BOMLINK_COLORWAYNAME;
		String sizeName = BOMLINK_SIZE1;
		List<String> sortList = new ArrayList<String>();
		sortList.add(colorwayName);
		sortList.add(sizeName);
		valsets = new HashMap();
		valsets = buildUniqueValueTable(coll, sortList);
		sortFlexObjects(coll,sortList,0);
		coll.clear();
		coll.addAll(sortedCollection);
		return coll;
	}
	private void debug(String msg){
		if(DEBUG){
			LCSLog.debug(msg);
		}
	}
}
