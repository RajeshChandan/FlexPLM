/*
 * HBIMatColorGenerator.java
 *
 * Created on Sept 13, 2007, 11:03 AM
 */

package com.hbi.wc.flexbom.gen;

import wt.util.*;



import java.util.*;

import com.hbi.wc.flexbom.util.HBITechPackUtil;
import com.lcs.wc.client.web.*;
import com.lcs.wc.flextype.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.db.*;
import wt.part.*;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.product.*;
import com.lcs.wc.sizing.ProductSizeCategory;
import com.lcs.wc.sourcing.*;
import com.lcs.wc.material.*;
import com.lcs.wc.util.*;

import com.lcs.wc.flexbom.*;
import com.lcs.wc.flexbom.gen.*;
import com.lcs.wc.part.LCSPartMaster;

/**
 *
 * @author  Chuck
 * Modified  by Quoc Pham. Added dimension column to the report
 */
public class HBIMatColorGenerator extends BomDataGenerator {

    public static final String MATERIAL_TYPE_PATH = "MATERIAL_TYPE_PATH";

    private static final String MATERIALREFID = "IDA3B5";
    private static final String SUPPLIERREFID = "IDA3C5";
    private static final String COLORREFID = "IDA3D5";

    private static final String COLORDIMCOLUMN = "IDA3E5";
    private static final String SOURCEDIMCOLUMN = "IDA3F5";
    private static final String DESTINATIONDIMCOLUMN = "IDA3H5";

    private static final String COLORS = "COLORS";
    private static final String SIZE1 = "SIZE1";
    private static final String SIZE2 = "SIZE2";
    private static final String DESTINATION = "DESTINATION";

    private static final String COLORIDS = "COLORIDS";
    private static final String COLORNAME = "COLORNAME";
    private static final String COLORDESC = "COLORDESC";
    private static final String COLORID = "COLORID";
    private static final String COLORHEX = "COLORHEX";
    private static final String COLORTHUMB = "COLORTHUMB";
    private static final String MATERIALCOLORID = "MATERIALCOLORID";

//    private String MAT_NAME_ATT = "ATT1";
//    private String COMP_NAME_ATT = "ATT1";
//    private String SUP_NAME_ATT = "ATT1";
//    private String COLOR_NAME_ATT = "ATT4";
//    private String PRICE_ATT = "NUM1";
//    private String COMP_NAME_DISPLAY = "ATT1";
//    private String MAT_DESCRIPTION_ATT = "ATT3";
//
//	private String PRICE_OVR_ATT = "NUM2";
//	private String QUANTITY_ATT = "NUM1";
//	private String LOSS_ADJ_ATT = "NUM3";
//	private String ROW_TOTAL_ATT = "NUM4";
	
	//Code Upgrade by Wipro Team
		 private String MAT_NAME_ATT = "ptc_str_1typeInfoLCSMaterial";
		    private String COMP_NAME_ATT = "ptc_str_4typeInfoFlexBOMLink";
			private String COMP_NAME_DISPLAY = "ptc_str_4typeInfoFlexBOMLink";
		    private String SUP_NAME_ATT = "ptc_str_1typeInfoLCSSupplier";
			private String COLOR_NAME_ATT = "ptc_str_7typeInfoFlexBOMLink";
		    private String PRICE_ATT = "ptc_dbl_1typeInfoLCSMaterial";
		    private String MAT_DESCRIPTION_ATT = "ptc_str_5typeInfoFlexBOMLink";
		    
			private String PRICE_OVR_ATT = "ptc_dbl_2typeInfoFlexBOMLink";
			private String QUANTITY_ATT = "ptc_dbl_1typeInfoFlexBOMLink";
			private String LOSS_ADJ_ATT = "ptc_dbl_3typeInfoFlexBOMLink";
			private String ROW_TOTAL_ATT = "ptc_dbl_5typeInfoFlexBOMLink";

	private String priceKey = "LCSMATERIALSUPPLIER." + PRICE_ATT;
	private String overrideKey = "FLEXBOMLINK." + PRICE_OVR_ATT;
	private String quantityKey = "FLEXBOMLINK." + QUANTITY_ATT;
	private String lossAdjustmentKey = "FLEXBOMLINK." + LOSS_ADJ_ATT;
	private String rowTotalKey = "FLEXBOMLINK." + ROW_TOTAL_ATT;
/*Added for Hanes customization - start*/
	//Strings constants for identifying Section Type
	public static final String CUT_PART_SPREAD = "cutPartSpread";				
	public static final String CUT_PART_TRIM_ST = "cutPartTrimSt"; 
	public static final String CUT_PART_TRIM_BI = "cutPartTrimBi";
	private String strUsageLB  = ""; 
	private String strMatContWidth  = ""; 
	private String strTotalMarkerLength  = ""; 
	private String strGmtsMkr  = ""; 
	private String strply  = ""; 
	private String strMatWght  = ""; 
	private String strTrimBiasUsageLB  = ""; 
	private String strTrimCutWidth  = ""; 
	private String strTotalLength  = ""; 
	private String strGmts  = ""; 
	private String strUsableCuts  = ""; 
	private String strMuLoss  = ""; 
	private String strRoundedUsbleCuts  = ""; 
	private String strTrimStrghtUsage  = ""; 
	private String strCOW  = ""; 
	private String strTTW  = ""; 
	private String strEND  = ""; 
	private String strUsage  = ""; 
	private String strStdWstFactor  = ""; 
	private String strUsagePrice  = ""; 
	private String strLength  = ""; 
	private String strRunoff  = ""; 
	private String strAllowance  = ""; 
/* Added for CA # 318-13 -- start*/
	private String strGarmentUse = "";
	private String strPartCd = "";
	private String strPartCdBias = "";
	private String strPartCdTrimStght = "";
	private String strPartCdSpread = "";
	private String strSection = "";
/* Added for CA # 318-13 -- end*/
	private String KeyUsageLB  = ""; 
	private String KeyMatContWidth  = "";
	private String KeyTotalMarkerLength  = ""; 
	private String KeyGmtsMkr  = "";
	private String Keyply  = ""; 
	private String KeyMatWght  = "";
	private String KeyTrimBiasUsageLB  = ""; 
	private String KeyTrimCutWidth  = "";
	private String KeyTotalLength  = "";
	private String KeyGmts  = ""; 
	private String KeyUsableCuts  = ""; 
	private String KeyMuLoss  = ""; 
	private String KeyRoundedUsbleCuts  = ""; 
	private String KeyTrimStrghtUsage  = ""; 
	private String KeyCOW  = ""; 
	private String KeyTTW  = ""; 
	private String KeyEND  = ""; 
	private String KeyUsage  = ""; 
	private String KeyStdWstFactor  = ""; 
	private String KeyUsagePrice  = ""; 
	private String KeyLength  = ""; 
	private String KeyRunoff  = ""; 
	private String KeyAllowance  = ""; 
/* Added for CA # 318-13 -- start*/
	private String KeyGarmentUse = "";
	private String KeyPartCd = "";
	private String KeyPartCdBias = "";
	private String KeyPartCdTrimStght = "";
	private String KeyPartCdSpread = "";
	private String KeySection = "";	
/* Added for CA # 318-13 -- end*/
	/*Added for Hanes customization - end*/

    protected String colorwaysLabel = WTMessage.getLocalizedMessage( RB.FLEXBOM, "colorwaysLabel", RB.objA );
    protected String colorLabel = WTMessage.getLocalizedMessage( RB.COLOR, "color_LBL", RB.objA ) ;
    protected String size1Label = WTMessage.getLocalizedMessage( RB.QUERYDEFINITION, "size1", RB.objA );
    protected String size2Label = WTMessage.getLocalizedMessage( RB.QUERYDEFINITION, "size2", RB.objA );
    

    //public float partNameWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.partNameWidth", "1.5"))).floatValue();
    //public float materialNameWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.materialNameWidth", "1.25"))).floatValue();
    public float supplierNameWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.supplierNameWidth", "1.25"))).floatValue();
    //public float colorwayWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.colorwayWidth", "0.75"))).floatValue();
	
	//Changed width of the columns in Tech Pack
    public float partNameWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.partNameWidth", "1.25"))).floatValue();
    public float materialNameWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.materialNameWidth", "1.0"))).floatValue();
    public float colorwayWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.colorwayWidth", "1.0"))).floatValue();
	//CC Task # 8 - Increase in Item Desc field to better display the data in the output report - Added by UST on 5 - Apr - 19
    public float itemDescWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.itemDescWidth", "2.0"))).floatValue();
    public float thumbNailWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.thumbNailWidth", "1.5"))).floatValue();    
	

    public int imageWidth = (new Integer(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.matThumbWidth", "75"))).intValue();
    public int imageHeight = (new Integer(LCSProperties.get("com.lcs.wc.flexbom.gen.MatColorGenerator.matThumbHeight", "0"))).intValue();

    public boolean USE_DEFAULT_COLUMNS = LCSProperties.getBoolean("com.lcs.wc.flexbom.gen.MatColorGenerator.useDefaultColumns");
    public boolean DEBUG = LCSProperties.getBoolean("com.lcs.wc.flexbom.gen.MatColorGenerator.verbose");

    private static final String DISPLAY_VAL = "DISPLAY_VAL";
    private Map dimensionMap;

    /** Creates a new instance of MatColorGenerator */
    private FlexType materialType = null;
    private FlexType supplierType = null;
    private FlexType bomType = null;
	private FlexType matColorType = null;
	
	 //Start HBI GP tech Pack Customizations
	 public static final String BASIC_CUT_AND_SEW_GARMENT = "BASIC CUT & SEW - GARMENT";
	 public static final String MATERIAL_HBI_Attribution = "Materials\\HBI\\Attribution";
	 public static final String MATERIAL_HBI_LABEL ="Materials\\HBI\\Label";
	 public static final String MATERIAL_HBI_Garment_Routing_Table="Materials\\HBI\\Garment Routing Table";
	 public String product_Oid= null;
	 public static final String PRINTS_AND_PATTERNS = "Prints and Patterns";
	 public static final String TRUE="true";
	 boolean isBOMGPRoutingTable =false;
	 boolean isBOMGPAttribution =false;
	 boolean isProductGP =false;
	 FlexBOMPart part =null;
	 private static final String PRODUCT_ID = "PRODUCT_ID";
	 private static final String GP_BOMREPORT_COLORSPEC_ENABLED = LCSProperties.get("hbi.bomreport.colorway.garmentproduct.colorspecific.functionality.enable","true");
	 //End HBI GP tech Pack Customizations
	
    public HBIMatColorGenerator() {
    }

    public Collection getBOMData() throws WTException {
        return this.dataSet;
    }

    private void debug(String msg){
        if(DEBUG){
           LCSLog.debug(msg);
        }
    }
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
			/*Code added to remove Material spec Version & Dye Code columns from Tech Pack CA # 25208-13 */
            if(viewAtts.contains("Material Color.hbiMaterialColorVersion")){
                viewAtts.remove("Material Color.hbiMaterialColorVersion");
            }
            if(viewAtts.contains("Material Color.hbiDyeCode")){
                viewAtts.remove("Material Color.hbiDyeCode");
            }			
			/*-- END -- */
			
            viewColumns.putAll(getViewColumns());
            
            if(!viewAtts.contains("Colorways")){
            	viewAtts.add("Colorways");
            }
            debug("viewColumn keys: " + viewColumns.keySet());

        }
         // for hbi don't want supplierName on Tech pack
		 //viewAtts.add(0, "supplierName");
		
         viewAtts.add(0, "materialDescription");

        if(this.getDestinations() != null && this.getDestinations().size() > 0){
            viewAtts.add(0, DESTINATION);
        }

        //Always dispaly Source Column. If it is null, display all sources
        viewAtts.add(0, SOURCES);

        if(this.getSizes2() != null && this.getSizes2().size() > 0){
            viewAtts.add(0, SIZE2);
        }
        if(this.getSizes1() != null && this.getSizes1().size() > 0){
            viewAtts.add(0, SIZE1);
        }
        if(this.getColorways() != null && this.getColorways().size() > 0){
            viewAtts.add(0, COLORS);
        }

        viewAtts.add(0, "partName");

        if(this.useMatThumbnail){
            viewAtts.add(0, "MATERIAL.thumbnail");
        }

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
                viewAtts.add("BOM.quantity");
            }
            viewAtts.add("Colorways");
        }

        TableColumn column = new TableColumn();
        FlexTypeGenerator flexg = new FlexTypeGenerator();
        FlexTypeAttribute flexatt = null;        

		//CC Task # 8 - Increase in Item Desc field to better display the data in the output report - Added by UST on 5 - Apr - 19  -- START
        flexatt = materialType.getAttribute("hbiItemDescription");
        column = flexg.createTableColumn(null, flexatt, materialType, false, "LCSMATERIAL");
        column.setPdfColumnWidthRatio(itemDescWidth);
        viewColumns.put("Material.hbiItemDescription", column);        
        //CC Task # 8 - Increase in Item Desc field to better display the data in the output report - Added by UST on 5 - Apr - 19  -- END		

        column = new BOMPartNameTableColumn();
        column.setHeaderLabel(COMP_NAME_DISPLAY);
        column.setTableIndex("FLEXBOMLINK." + COMP_NAME_ATT);
        column.setDisplayed(true);
        ((BOMPartNameTableColumn)column).setSubComponetIndex("FLEXBOMLINK.MASTERBRANCHID");
        ((BOMPartNameTableColumn)column).setComplexMaterialIndex("FLEXBOMLINK.MASTERBRANCH");
        ((BOMPartNameTableColumn)column).setLinkedBOMIndex("FLEXBOMLINK.LINKEDBOM");
        column.setSpecialClassIndex("CLASS_OVERRIDE");
        column.setPdfColumnWidthRatio(partNameWidth);
        viewColumns.put("partName", column);

		 /* Commented to remove Colorways & Sources Columns - CA # 25208-13 */
        /*column = new TableColumn();
        column.setHeaderLabel(WTMessage.getLocalizedMessage ( RB.SOURCING, "sourceColumn_LBL", RB.objA ));
        column.setTableIndex(SOURCES);
        column.setDisplayed(true);
        column.setFormat(FormatHelper.MOA_FORMAT);
        viewColumns.put(SOURCES, column);

        column = new TableColumn();
        column.setHeaderLabel(colorwaysLabel);
        column.setTableIndex(COLORS);
        column.setDisplayed(true);
        column.setFormat(FormatHelper.MOA_FORMAT);
        viewColumns.put(COLORS, column);*/
		/* -- END -- */

        column = new TableColumn();
        column.setHeaderLabel(size1Label);
        column.setTableIndex(SIZE1);
        column.setDisplayed(true);
        column.setFormat(FormatHelper.MOA_FORMAT);
        viewColumns.put(SIZE1, column);

        column = new TableColumn();
        column.setHeaderLabel(size2Label);
        column.setTableIndex(SIZE2);
        column.setDisplayed(true);
        column.setFormat(FormatHelper.MOA_FORMAT);
        viewColumns.put(SIZE2, column);

        column = new TableColumn();
        column.setHeaderLabel(WTMessage.getLocalizedMessage( RB.FLEXBOM, "destination_noColon_LBL", RB.objA ));
        column.setTableIndex(DESTINATION);
        column.setDisplayed(true);
        column.setFormat(FormatHelper.MOA_FORMAT);
        viewColumns.put(DESTINATION, column);

        column = new BOMMaterialTableColumn();
        column.setHeaderLabel(this.materialLabel);
        column.setTableIndex("LCSMATERIAL." + MAT_NAME_ATT);
        column.setDisplayed(true);
        column.setPdfColumnWidthRatio(materialNameWidth);
        column.setLinkMethod("viewMaterial");
        column.setLinkTableIndex("childId");
        //column.setLinkMethodPrefix("OR:wt.part.WTPartMaster:");
        column.setLinkMethodPrefix("OR:com.lcs.wc.material.LCSMaterialMaster:");
        ((BOMMaterialTableColumn)column).setDescriptionIndex("FLEXBOMLINK." + MAT_DESCRIPTION_ATT);
        viewColumns.put("materialDescription", column);

        // for hbi don't want supplierName on Tech pack
		/*column = new TableColumn();
        column.setHeaderLabel(this.supplierLabel);
        column.setTableIndex("LCSSUPPLIERMASTER.SUPPLIERNAME");
        column.setDisplayed(true);
        column.setPdfColumnWidthRatio(supplierNameWidth);
        column.setFormat(FormatHelper.STRING_FORMAT);
        viewColumns.put("supplierName", column);
		*/

        column = new TableColumn();
        column.setDisplayed(true);
        column.setHeaderLabel("");
        column.setHeaderAlign("left");
        column.setLinkMethod("launchImageViewer");
        column.setLinkTableIndex("LCSMATERIAL.PRIMARYIMAGEURL");
        column.setTableIndex("LCSMATERIAL.PRIMARYIMAGEURL");
        column.setPdfColumnWidthRatio(thumbNailWidth);
        column.setLinkMethodPrefix("");
        column.setImage(true);
        column.setShowFullImage(this.useMatThumbnail);

        if(imageWidth > 0){
            column.setImageWidth(imageWidth);
        }
        if(imageHeight > 0){
            column.setImageHeight(imageHeight);
        }
        viewColumns.put("MATERIAL.thumbnail", column);


        debug("Getting columns...colorways: " + getColorways());
        if(this.getColorways() != null && this.getColorways().size() > 0){
            Iterator cws = this.getColorways().iterator();
            String cwId = "";
            while(cws.hasNext()){
                cwId = (String)cws.next();

                BOMColorTableColumn colorColumn = new BOMColorTableColumn();
                colorColumn.setDisplayed(true);
                colorColumn.setTableIndex(cwId + "." + COLORNAME);
                colorColumn.setDescriptionIndex(cwId + "." + COLORDESC);
                colorColumn.setHeaderLabel((String)LCSSKUQuery.getSKURevA(cwId).getValue("skuName"));
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
                viewColumns.put(cwId + "." + DISPLAY_VAL, colorColumn);
            }
        }
        else{
            BOMColorTableColumn colorColumn = new BOMColorTableColumn();
            colorColumn.setDisplayed(true);
            colorColumn.setTableIndex("LCSCOLOR.COLORNAME");
            colorColumn.setDescriptionIndex("FLEXBOMLINK." + COLOR_NAME_ATT);
            colorColumn.setHeaderLabel(colorLabel);
            colorColumn.setLinkMethod("viewColor");
            colorColumn.setLinkTableIndex("LCSCOLOR.IDA2A2");
            colorColumn.setLinkMethodPrefix("OR:com.lcs.wc.color.LCSColor:");
            colorColumn.setColumnWidth("1%");
            colorColumn.setWrapping(false);
            colorColumn.setBgColorIndex("LCSCOLOR.COLORHEXIDECIMALVALUE");
            colorColumn.setUseColorCell(true);
            colorColumn.setAlign("center");
            colorColumn.setImageIndex("LCSCOLOR.THUMBNAIL");
            colorColumn.setUseColorCell(this.useColorSwatch);
            viewColumns.put(DISPLAY_VAL, colorColumn);
        }

        Iterator vi = viewAtts.iterator();
        String att = "";
        while(vi.hasNext()){
            att = (String)vi.next();
            if("Colorways".equals(att)){
                if(this.getColorways() != null && this.getColorways().size() > 0){
                    Iterator cws = this.getColorways().iterator();
                    String cwId = "";
                    while(cws.hasNext()){
                        cwId = (String)cws.next();
                        if(viewColumns.get(cwId + "." + DISPLAY_VAL) != null){
                            columns.add(viewColumns.get(cwId + "." + DISPLAY_VAL));
                        }
                    }
                }
                else{
                    columns.add(viewColumns.get(DISPLAY_VAL));
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

    public void init(Map params) throws WTException {

        super.init(params);

        if(DEBUG){
            HashMap tparams = new HashMap(params);
            tparams.remove(BomDataGenerator.RAW_BOM_DATA);
            LCSLog.debug("tparams: " + tparams);
        }

        if(params != null){
            if(params.get(MATERIAL_TYPE_PATH) != null){
                materialType = FlexTypeCache.getFlexTypeFromPath((String)params.get(MATERIAL_TYPE_PATH));
            }
            else{
                materialType = FlexTypeCache.getFlexTypeRoot("Material");
            }
            supplierType = FlexTypeCache.getFlexTypeRoot("Supplier");
            bomType = FlexTypeCache.getFlexTypeRoot("BOM");

            MAT_NAME_ATT = materialType.getAttribute("name").getVariableName();
            SUP_NAME_ATT = supplierType.getAttribute("name").getVariableName();
            COMP_NAME_ATT = bomType.getAttribute("partName").getVariableName();
            COLOR_NAME_ATT = bomType.getAttribute("colorDescription").getVariableName();
            PRICE_ATT = materialType.getAttribute("materialPrice").getVariableName();
            MAT_DESCRIPTION_ATT = bomType.getAttribute("materialDescription").getVariableName();
            COMP_NAME_DISPLAY = bomType.getAttribute("partName").getAttDisplay();
			
			PRICE_OVR_ATT = bomType.getAttribute("priceOverride").getVariableName();
			QUANTITY_ATT = bomType.getAttribute("quantity").getVariableName();
			LOSS_ADJ_ATT = bomType.getAttribute("lossAdjustment").getVariableName();
			ROW_TOTAL_ATT = bomType.getAttribute("rowTotal").getVariableName();
			
			priceKey = "LCSMATERIALSUPPLIER." + PRICE_ATT;
			overrideKey = "FLEXBOMLINK." + PRICE_OVR_ATT;
			quantityKey = "FLEXBOMLINK." + QUANTITY_ATT;
			lossAdjustmentKey = "FLEXBOMLINK." + LOSS_ADJ_ATT;
			rowTotalKey = "FLEXBOMLINK." + ROW_TOTAL_ATT;

			//Added for Hanes Customization - start
			FlexBOMPart part = (FlexBOMPart)params.get(BOMPDFContentGenerator.BOM_PART);
			FlexType type = part.getFlexType();
			
			//Start HBI GP tech pack customization - 09/07/18
			product_Oid= (String) params.get(PRODUCT_ID);
            debug("product_Oid..."+product_Oid);
            
			if (FormatHelper.hasContent(product_Oid)) {
				LCSProduct product = (LCSProduct) LCSProductQuery.findObjectById(product_Oid);
				//If product type is Garment, then set Gartment product required flags.
				if ((product != null) && product.getFlexType().getFullName().equals(BASIC_CUT_AND_SEW_GARMENT)) {
					
					//set GP flags
					setGPFlags(part);
				}
			}
          //End HBI GP tech pack customization - 09/07/18
            
			// Modified for CA # 318-13 for considering Colorway BOM attributes
			
            //Start Hbi GP tech pack customization
            //if(!type.getFullName().equals("Materials\\HBI\\Colorway") ){
            if(!(isBOMGPRoutingTable || type.getFullName().equals("Materials\\HBI\\Selling\\Pack Case BOM")
            		|| type.getFullName().equals("Materials\\HBI\\Selling\\Casing")
            		|| type.getFullName().equals("Materials\\HBI\\Selling\\Packaging")
            		|| type.getFullName().equals("Materials\\HBI\\Selling\\Routing")
            		)){
            	if(!(type.getFullName().equals("Materials\\HBI\\Colorway") || isBOMGPAttribution )){
			//End Hbi GP tech pack customization
				strUsageLB  = type.getAttribute("hbiUsageLbPerDoz").getVariableName();
				strTotalMarkerLength  = type.getAttribute("hbiTotalMarkerLength").getVariableName(); 
				strGmtsMkr  = type.getAttribute("hbiGmtsPerMkr").getVariableName(); 
				strply  = type.getAttribute("hbiPly").getVariableName(); 
				strTrimBiasUsageLB  = type.getAttribute("hbiTrimBiasLbUsage").getVariableName(); 
				strTrimCutWidth  = type.getAttribute("hbiTrimCutWidInch").getVariableName(); 
				strTotalLength  = type.getAttribute("hbiTotalLengthIn").getVariableName(); 
				strGmts  = type.getAttribute("hbiGmts").getVariableName(); 
				strUsableCuts  = type.getAttribute("hbiUsableCuts").getVariableName(); 
				strMuLoss  = type.getAttribute("hbiMuLossPct").getVariableName(); 
				strRoundedUsbleCuts  = type.getAttribute("hbiRoundedUsableCut").getVariableName(); 
				strTrimStrghtUsage  = type.getAttribute("hbiTrimStraightLbUsage").getVariableName(); 
				strCOW  = type.getAttribute("hbiWasteFactorCow").getVariableName(); 
				strTTW  = type.getAttribute("hbiWasteFactorTtw").getVariableName(); 
				strEND  = type.getAttribute("hbiWasteFactorEnd").getVariableName(); 
				strUsage  = type.getAttribute("hbiUsage").getVariableName(); 
				strStdWstFactor   = materialType.getAttribute("hbiStdWasteFactor").getVariableName();
				strMatContWidth   = materialType.getAttribute("hbiConWidth").getVariableName();
				strMatWght   = materialType.getAttribute("hbiWeight").getVariableName();
				strUsagePrice   = materialType.getAttribute("hbiUsagePrice").getVariableName();
				strLength  = type.getAttribute("hbiLengthIn").getVariableName(); 
				strRunoff  = type.getAttribute("hbiRunoffIn").getVariableName(); 
				strAllowance  = type.getAttribute("hbiAllowance").getVariableName(); 
			}
			else {
				strGarmentUse = type.getAttribute("hbiGarmentUse").getVariableName(); 
				strPartCd = type.getAttribute("hbiPartCode").getVariableName(); 
				strPartCdBias = type.getAttribute("hbiPartCodeBias").getVariableName(); 
				strPartCdTrimStght = type.getAttribute("hbiPartCodeTrimStraight").getVariableName(); 
				strPartCdSpread = type.getAttribute("hbiPartCodeSpread").getVariableName(); 
				strSection = type.getAttribute("section").getVariableName(); 
			}
				KeyGarmentUse = "FLEXBOMLINK." + strGarmentUse;
				KeyPartCd = "FLEXBOMLINK." + strPartCd;
				KeyPartCdBias = "FLEXBOMLINK." + strPartCdBias;
				KeyPartCdTrimStght = "FLEXBOMLINK." + strPartCdTrimStght;
				KeyPartCdSpread = "FLEXBOMLINK." + strPartCdSpread;
				KeySection = "FLEXBOMLINK." + strSection;
			
				KeyUsageLB = "FLEXBOMLINK." + strUsageLB; 
				KeyTotalMarkerLength = "FLEXBOMLINK." + strTotalMarkerLength; 
				KeyGmtsMkr = "FLEXBOMLINK." + strGmtsMkr;
				Keyply = "FLEXBOMLINK." + strply; 
				KeyTrimBiasUsageLB = "FLEXBOMLINK." + strTrimBiasUsageLB; 
				KeyTrimCutWidth = "FLEXBOMLINK." + strTrimCutWidth;
				KeyTotalLength = "FLEXBOMLINK." + strTotalLength;
				KeyGmts = "FLEXBOMLINK." + strGmts; 
				KeyUsableCuts = "FLEXBOMLINK." + strUsableCuts; 
				KeyMuLoss = "FLEXBOMLINK." + strMuLoss; 
				KeyRoundedUsbleCuts = "FLEXBOMLINK." + strRoundedUsbleCuts; 
				KeyTrimStrghtUsage = "FLEXBOMLINK." + strTrimStrghtUsage; 
				KeyCOW = "FLEXBOMLINK." + strCOW; 
				KeyTTW = "FLEXBOMLINK." + strTTW; 
				KeyEND = "FLEXBOMLINK." + strEND; 
				KeyUsage = "FLEXBOMLINK." + strUsage; 
				KeyStdWstFactor = "LCSMATERIAL." + strStdWstFactor; 
				KeyMatWght = "LCSMATERIAL." + strMatWght;
				KeyMatContWidth = "LCSMATERIAL." + strMatContWidth;
				KeyUsagePrice = "LCSMATERIAL." + strUsagePrice;
				KeyLength = "FLEXBOMLINK." + strLength; 
				KeyRunoff = "FLEXBOMLINK." + strRunoff; 
				KeyAllowance = "FLEXBOMLINK." + strAllowance; 
			
            }
	            
			//Added for Hanes Customization - end

            if(this.dataSet != null){
            	
                this.dataSet = groupDataToBranchId(this.dataSet, "FLEXBOMLINK.BRANCHID", "FLEXBOMLINK.MASTERBRANCHID", "FLEXBOMLINK.SORTINGNUMBER");
                Collection processedData = new ArrayList();
                Collection topLevelBranches = this.getTLBranches();
                Iterator branchIter = topLevelBranches.iterator();
                FlexObject tlb = null;
                while(branchIter.hasNext()){
                    FlexObject topLevelBranch = (FlexObject)branchIter.next();

                    //CMS - This code has been simplified.  Since the complex materials records are included, it isn't
                    //necessary to process them differently/seperately.  They can be processed as a regular top level branch.
                    /*
                    boolean isSubComponent = FormatHelper.hasContent(topLevelBranch.getString("FLEXBOMLINK.MASTERBRANCHID"));
                    boolean isComplexMaterial = FormatHelper.hasContent(topLevelBranch.getString("FLEXBOMLINK.MASTERBRANCH")) || FormatHelper.hasContent(topLevelBranch.getString("FLEXBOMLINK.LINKEDBOM"));

                    if (!isSubComponent)
                    {
                       if (isComplexMaterial)
                          processedData.addAll(processComplexBranch (topLevelBranch));
                       else
                          processedData.addAll(processBranch (topLevelBranch));
                    }
                    */

                    processedData.addAll(processBranch (topLevelBranch));
                }
                this.dataSet = processedData;
            }
            
            if(FormatHelper.hasContent((String)params.get(PDFProductSpecificationGenerator2.PRODUCT_SIZE_CAT_ID))){
            	ProductSizeCategory productSizeCategory = (ProductSizeCategory)LCSQuery.findObjectById((String)params.get(PDFProductSpecificationGenerator2.PRODUCT_SIZE_CAT_ID));
                size1Label = productSizeCategory.getSizeRange().getFullSizeRange().getSize1Label();
                size2Label = productSizeCategory.getSizeRange().getFullSizeRange().getSize2Label();
                
            }
            
            

        }
    }
   
    
    /**
    * @param part
    * @return 
    * @Date 09/07/18
    * If GP's BOM type is "Attribution" return 'true', else 'false'
    */
	private boolean isBOMGPAttribution(FlexBOMPart part) {
		if ((part != null) && part.getFlexType().getFullName().equals(MATERIAL_HBI_Attribution)) {
			return true;
		}
		return false;
	}

 /**
 * @param part
 * @return
 * @Date 09/07/18
 * If GP's BOM type is "RoutingTable" return 'true', else 'false'
 */
	private boolean isBOMGPRoutingTable(FlexBOMPart part) {
		if (part != null && part.getFlexType().getFullName().equals(MATERIAL_HBI_Garment_Routing_Table)) {
			return true;
		}
		return false;
	}   
   
   
/**
     * Process a BOM component record. We use the MCIdimensionHelper method to calculate the materials and colors data for each dimension set
     */
   private Collection processBranch(FlexObject topLevel) throws WTException{

		//modified for hanes Customization
        //calculatePrice(topLevel);
		calculateSectionAttributes(topLevel);

      //Initialize all the variables and data structures needed
      ArrayList data = new ArrayList();
      String partNameIndex = FlexTypeCache.getFlexTypeRoot("BOM").getAttribute("partName").getSearchResultIndex();
      String partName = topLevel.getString(partNameIndex);

      //If the dimension map hasn't been initialized, build the map
      if (dimensionMap == null)
         initDimensionMap(topLevel);
      
      MaterialColorInfo topLevelMci = MCIDimensionHelper.getMaterialColorInfo(topLevel);
      Map ovrMap = getOverRideMap();
      String branchKey = topLevel.getString (DIMID_COL);
      Collection ovrForBranch = (Collection) ovrMap.get(branchKey);
      
      if (ovrForBranch.size()>0)
      {
         //If the branch has any overrides, initialize a MCIDimensionHelper Object
         MCIDimensionHelper mciHelper = new MCIDimensionHelper(dimensionMap, topLevel, ovrForBranch);
         Map materialMciMap = mciHelper.groupMcisByMaterialSupplier(mciHelper.getAllUniqueMaterialColorInfo());
         //LCSLog.debug("materialMciMap: " + materialMciMap);
         for(Iterator mciIter = materialMciMap.keySet().iterator(); mciIter.hasNext();)
         {
            //Get material/Supplier info
            MaterialColorInfo materialMci = (MaterialColorInfo) mciIter.next();
            Collection matColorMciList = (Collection) materialMciMap.get(materialMci);
            Map dimensionMciMap = mciHelper.groupMcisByUniqueDimensionSet(matColorMciList);
            
            
            for(Iterator dimIter = dimensionMciMap.keySet().iterator(); dimIter.hasNext();)
            {
               Map dimensions = (Map) dimIter.next();

               Collection colorMciList = (Collection) dimensionMciMap.get(dimensions);
               //For each unique dimensions set, create a new row and populate material and dimensions data
               FlexObject newRow = topLevel.dup();
               newRow.put (partNameIndex, "");
               
               //CHUCK:  This addMaterialData method is only replacing minimal material level information
               //for the override record.  It does not currently account for the fact that other material
               //information could be displayed.  As it is now, any additonaly information always displays
               //the top level row information 
               //Added/Modified to account for need for additional info
               FlexObject ovr = getOverRideRecord(ovrForBranch, materialMci);
               addMaterialData(newRow, materialMci, ovr);
               
               addDimensionsData (newRow, mciHelper.getMergedDimensions(colorMciList));
               blankAllColorData(newRow);

               //For this component row, populate color data for every applicable colorway dimension
               for (Iterator colorIter = colorMciList.iterator(); colorIter.hasNext();)
               {
                  MaterialColorInfo colorMci = (MaterialColorInfo) colorIter.next();
                  Collection skus =  mciHelper.getColorwayDimension(colorMci);

                  for (Iterator skuIter = skus.iterator(); skuIter.hasNext();)
                  {
                     String skuId = (String) skuIter.next();
                     addColorData(newRow, skuId, colorMci);
                  }
                  if(skus == null || skus.size() < 1){
                      Collection sources =  mciHelper.getSourceDimension(colorMci);
                      for (Iterator sourceIter = sources.iterator(); sourceIter.hasNext();)
                      {
                         String sourceId = (String) sourceIter.next();
                         newRow.put("LCSCOLOR.COLORNAME", colorMci.colorName);
                         newRow.put("FLEXBOMLINK." + COLOR_NAME_ATT, colorMci.colorName);
                         newRow.put("LCSCOLOR.IDA2A2", colorMci.colorId);
                         newRow.put("LCSCOLOR.COLORHEXIDECIMALVALUE", colorMci.colorHexValue);
                         //addColorData(newRow, sourceId, colorMci);
                      }
                  }
               }
					//modified for hanes Customization

                    //calculatePrice(newRow);
			         calculateSectionAttributes(newRow);				 
               data.add(newRow);
            }
         }
      }
      else if (topLevelMci.hasMaterialInfo())
      {
         //If the branch doesn't have any overrides, populate top level material and color
         FlexObject newRow = topLevel.dup();
         addMaterialData(newRow, topLevelMci, null);
         addAllColorData(newRow, topLevelMci);
         addDimensionsData (newRow, dimensionMap);
			//modified for hanes Customization
            //calculatePrice(newRow);
			calculateSectionAttributes(newRow);
         data.add(newRow);
      }
      else if (topLevelMci.hasColorInfo())
      {
         //If the branch doesn't have any overrides, populate top level material and color
         FlexObject newRow = topLevel.dup();
         addAllColorData(newRow, topLevelMci);
         addDimensionsData (newRow, dimensionMap);
			//modified for hanes Customization
            //calculatePrice(newRow);
			calculateSectionAttributes(newRow);
         data.add(newRow);
      }
      else
      {
         //For undefined component, add a blank row
         FlexObject newRow = topLevel.dup();
			//modified for hanes Customization
            //calculatePrice(newRow);
			calculateSectionAttributes(newRow);
         data.add(newRow);
      }
      //Sort all rows and populate the partName only for the first Row
      Collection sortedData = sortBranchDataSet(data);
      if(sortedData.iterator().hasNext()) {
    	  FlexObject firstRow = (FlexObject)sortedData.iterator().next();
    	  firstRow.put (partNameIndex, partName);  
      }
      return sortedData;
    }

    //added for Hanes Customization
    private void calculateSectionAttributes(FlexObject row)
	{	   
		//calling different calculations based on the section val
		if(section.equalsIgnoreCase(CUT_PART_SPREAD))
		{		
			 calUsageLB(row);
			 calSpreadRowTotal(row);

		 }else if(section.equalsIgnoreCase(CUT_PART_TRIM_BI))	 
		 {				
			 calTrimBiasLB(row);
			 calBiasRowTotal(row);

		 }else if(section.equalsIgnoreCase(CUT_PART_TRIM_ST))
		 {	
			 calUsableCuts(row);
			 calMULoss(row);
			 calTotalLength(row);
			 calTrimStraightLB(row);
			 calStraightRowTotal(row);
		 }else
		 {
			 calOtherRowTotal(row);
		 }	
	}

   
    //commented for Hanes Customization
 /*

	private void calculatePrice(FlexObject row) throws WTException{
		
        double materialPrice = FormatHelper.parseDouble(row.getData(priceKey));
        double priceOverride = FormatHelper.parseDouble(row.getData(overrideKey));
        double quantity = FormatHelper.parseDouble(row.getData(quantityKey));
        double lossAdjustment = FormatHelper.parseDouble(row.getData(lossAdjustmentKey));


        if(priceOverride > 0){
            materialPrice = priceOverride;
        }
         

        if(lossAdjustment != 0){
            quantity = quantity + (quantity * lossAdjustment);
        }

        double rowTotal = quantity * materialPrice;

		row.put(rowTotalKey, "" + rowTotal);

	}
  */

    private static FlexObject getOverRideRecord(Collection records, MaterialColorInfo mci){
        Iterator i = records.iterator();
        FlexObject record = null;
        while(i.hasNext()){
            record = (FlexObject)i.next();
            if(mci.materialSupplierId.equals(record.getData("LCSMATERIALSUPPLIERMASTER.IDA2A2")))
                return record;
        }
        
        return null;
    }
    
    
    /**
     * Process a complex material row. We first find all the combinations for the top level using the processBranch method
     * We then loop through each resulting rows and find the related Material BOM.
     * We populate the applicable subcomponents from the Bom and determine the color data from the SKU overrides
     */
    private Collection processComplexBranch(FlexObject topLevel) throws WTException{
	   
	   //calculatePrice(topLevel);
       
       Collection processedData = new ArrayList();
       Vector sortKeys = new Vector();
       sortKeys.add("FLEXBOMLINK.SORTINGNUMBER");

       //Get all the top Level overrides
       Collection topComponents = processBranch (topLevel);

       for(Iterator topComponentsIter = topComponents.iterator(); topComponentsIter.hasNext();)
       {
            //For each complex Material Override, we find the material BOM
            FlexObject topComponent = (FlexObject) topComponentsIter.next();
            String materialId = "OR:com.lcs.wc.material.LCSMaterialMaster:" + topComponent.getString ("FLEXBOMLINK." + MATERIALREFID);

            if (materialId.equals(LCSMaterialQuery.PLACEHOLDERID))
               materialId = "OR:com.lcs.wc.material.LCSMaterialMaster:" + topComponent.getString ("LCSMATERIAL.IDA3MASTERREFERENCE");

            LCSMaterialMaster materialMaster = (LCSMaterialMaster) LCSQuery.findObjectById (materialId);
            LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf (materialMaster);

            FlexBOMPart subBomPart;
            Collection bomParts = new LCSFlexBOMQuery().findBOMPartsForOwner(material);

            if (!bomParts.isEmpty())
            {
               //If material Bom is found, get all the subcomponents top level branches
               subBomPart = (FlexBOMPart) bomParts.iterator().next();
               Map overrideMap = MCIDimensionHelper.getOverrideMap(subBomPart, materialType);
               Collection subComponentsList  = new ArrayList();
               Collection topLevelBranches = overrideMap.keySet();

               for (Iterator topLevelIter = topLevelBranches.iterator(); topLevelIter.hasNext();)
               {
                  FlexObject topLevelComplexBranch = (FlexObject) topLevelIter.next();
                  String branchId = topLevelComplexBranch.getString("FLEXBOMLINK.BRANCHID");
                  String partNameIndex = FlexTypeCache.getFlexTypeRoot("BOM").getAttribute("partName").getSearchResultIndex();
                  String partName = topLevelComplexBranch.getString(partNameIndex);

                  if (!FormatHelper.hasContent(topLevelComplexBranch.getString(DIM_COL)) &&
                      MCIDimensionHelper.getMaterialColorInfo(topLevelComplexBranch).hasMaterialInfo()/* &&
                      FormatHelper.hasContent(partName)*/)
                  {
                     //If the sub component top level is defined, create a new row and populate color data using only SKU overrides
                     FlexObject newRow = topLevelComplexBranch.dup();
                     newRow.put("FLEXBOMLINK.MASTERBRANCHID", topComponent.getString("FLEXBOMLINK.BRANCHID"));
                     blankAllColorData(newRow);
                     addAllSubComponentsColorData(newRow, topComponent, subBomPart);
					
                     //modified for hanes Customization
					//calculatePrice(newRow);
					calculateSectionAttributes(newRow);  

                     subComponentsList.add (newRow);
                  }
               }
               //Sort the subcomponents,
               subComponentsList = FlexObjectUtil.sortFlexObjects(subComponentsList,sortKeys);
               //add the topcomponent row and all its sub components
               processedData.add (topComponent);
               processedData.addAll(subComponentsList);
            }
            else
            {
               //If material Bom cannot be found, only add top component
               processedData.add (topComponent);
            }
       }
       return processedData;
   }
    /**
     * This is the sort order for the data on the report
     */
    private Collection sortBranchDataSet (Collection dataSet) {

         Vector sortKeys = new Vector();
         sortKeys.add(SOURCES);
         sortKeys.add(COLORS);
         sortKeys.add(SIZE1);
         sortKeys.add(SIZE2);
         sortKeys.add(DESTINATION);
         return FlexObjectUtil.sortFlexObjects(dataSet,sortKeys);
    }
    /**
     * Initialize the dimension map used to initialize the MCIDimensionHelper Class
     */
    private void initDimensionMap(FlexObject TopLevel) throws WTException {

      dimensionMap = new HashMap();
      dimensionMap.put (MCIDimensionHelper.SKU, this.getColorways());
      dimensionMap.put (MCIDimensionHelper.SIZE1, this.getSizes1());
      dimensionMap.put (MCIDimensionHelper.SIZE2, this.getSizes2());

      //If no sources have been selected on the User Interface, get all the sources for the product to display in the report
      if (this.getSources().size() == 0)
      {
         LCSLog.debug ("Source is NULL");
         String bomOid = "OR:com.lcs.wc.flexbom.FlexBOMPartMaster:" + TopLevel.getString ("FLEXBOMLINK.IDA3A5");
         FlexBOMPartMaster bomMaster = (FlexBOMPartMaster) LCSQuery.findObjectById (bomOid);
         FlexBOMPart bomPart = (FlexBOMPart) VersionHelper.latestIterationOf (bomMaster);
         LCSPartMaster productMaster = (LCSPartMaster) bomPart.getOwnerMaster();
         //Code change by Upgrade team
         //Collection allSources =  LCSSourcingConfigQuery.getSourcingConfigForProduct(productMaster);
         Collection allSources =  LCSSourcingConfigQuery.getSourcingConfigForProduct((LCSPartMaster) bomPart.getOwnerMaster());
         Collection allSourcesId = new ArrayList();

         for(Iterator iter = allSources.iterator(); iter.hasNext();)
         {
            LCSSourcingConfig source = (LCSSourcingConfig) iter.next();
            String sourceId = FormatHelper.getNumericObjectIdFromObject((LCSSourcingConfigMaster)source.getMaster());
            allSourcesId.add (sourceId);
         }
         dimensionMap.put (MCIDimensionHelper.SOURCE, allSourcesId);
      }
      else
      {
         dimensionMap.put (MCIDimensionHelper.SOURCE, this.getSources());
      }

      //We need to check if the format of the destination Id is correct before initializing the MCIdimenstionHelper.
      //This is needed because when we run the report from the linesheet, it seems like the destination Ids are passed in differently
      Collection destinationList = new ArrayList();

      for(Iterator iter = this.getDestinations().iterator(); iter.hasNext();)
      {
         String destinationId = (String) iter.next();

         if (destinationId.startsWith ("OR:"))
            destinationId = FormatHelper.getNumericFromOid(destinationId);

         destinationList.add (destinationId);
      }
      dimensionMap.put (MCIDimensionHelper.DESTINATION, destinationList);
    }

    /**
     * Populates material and supplier data for a row
     */
    private void addMaterialData(FlexObject row, MaterialColorInfo mci, FlexObject orRow) throws WTException{
        if(orRow != null){
            Iterator keys = orRow.keySet().iterator();
            String key = "";
            while(keys.hasNext()){
                key = (String)keys.next();
                if(key.startsWith("LCSMATERIAL") || key.startsWith("LCSSUPPLIER")){
                    row.put(key, orRow.get(key));
                }
            }
            if(!FormatHelper.hasContent(row.getString("FLEXBOMLINK." + COMP_NAME_ATT)) && FormatHelper.hasContent(orRow.getString("FLEXBOMLINK." + COMP_NAME_ATT))){
                row.put("FLEXBOMLINK." + COMP_NAME_ATT, orRow.get("FLEXBOMLINK." + COMP_NAME_ATT));
            }
        }

        row.put("FLEXBOMLINK." + MATERIALREFID, mci.materialId);
        row.put("LCSMATERIAL." + MAT_NAME_ATT, mci.materialName);
        row.put("LCSSUPPLIERMASTER.SUPPLIERNAME", mci.supplierName);
        row.put("LCSSUPPLER." + SUP_NAME_ATT, mci.supplierName);
        row.put("LCSMATERIALSUPPLIERMASTER.IDA2A2", mci.materialSupplierId);
        row.put("FLEXBOMLINK." + SUPPLIERREFID, mci.supplierId);
        row.put("LCSMATERIALCOLOR.IDA2A2", mci.materialColorId);

    }
    /**
     * Populates color data for all colorway dimensions in a row
     */
    private void addAllColorData(FlexObject row, MaterialColorInfo colorMci) throws WTException {

      Collection skus =  this.getColorways();
      for (Iterator skuIter = skus.iterator(); skuIter.hasNext();)
      {
         String skuId = (String) skuIter.next();
         addColorData(row, skuId, colorMci);
      }
    }
    /**
     * Populates "X" color data for all colorway dimensions in a row
     */
    private void blankAllColorData(FlexObject row) throws WTException {

      Collection skus =  this.getColorways();
      for (Iterator skuIter = skus.iterator(); skuIter.hasNext();)
      {
         String skuId = (String) skuIter.next();
         blankColorData (skuId, row);
      }
    }
    /**
     * Populates color data for all colorway dimensions in a complex material sub component row
     */
    private void addAllSubComponentsColorData(FlexObject subRow, FlexObject topRow, FlexBOMPart bomPart) throws WTException {

      Collection skus =  this.getColorways();
      for (Iterator skuIter = skus.iterator(); skuIter.hasNext();)
      {
         String skuId = (String) skuIter.next();
         String colorName = topRow.getString(skuId + "." + COLORNAME);
         String matColorId = topRow.getString(skuId + "." + MATERIALCOLORID);

         if (FormatHelper.hasContent (matColorId) && (!"X".equals(colorName)))
         {
            addComplexColorData(bomPart, subRow, skuId, matColorId);
         }
         else if (!FormatHelper.hasContent(matColorId) && (!"X".equals(colorName)))
         {
            addUnavailableData(skuId, subRow);
         }
         else
         {
            blankColorData(skuId, subRow);
         }
       }
    }
    /**
     * Populates "X" color data for a row given a colorway dimension
     */
    private void blankColorData(String skuId, FlexObject row) throws WTException {

         row.put(skuId + "." + COLORNAME, "X");
         row.put(skuId + "." + COLORID, "");
         row.put(skuId + "." + COLORHEX, "");
         row.put(skuId + "." + MATERIALCOLORID, "");
         row.put(skuId + "_CLASS_OVERRIDE", "BOM_OVERRIDE");
    }
    /**
     * Populates "N/A" color data for a row given a colorway dimension
     */
    private void addUnavailableData(String skuId, FlexObject row) throws WTException {

         row.put(skuId + "." + COLORNAME, "N/A");
         row.put(skuId + "." + COLORID, "");
         row.put(skuId + "." + COLORHEX, "");
         row.put(skuId + "." + MATERIALCOLORID, "");
         row.put(skuId + "_CLASS_OVERRIDE", "BOM_OVERRIDE");
    }
    /**
     * Populates color data for a row given a colorway dimension
     */
    private void addColorData(FlexObject row, String skuId, MaterialColorInfo colorMci) throws WTException {
        String colorName = colorMci.colorName;
        String colorId = colorMci.colorId;
        String colorDescription = colorMci.colorDescription;
        String materialColorId = colorMci.materialColorId;
		
		/* code added to concatenate Color spec Version & Dye Code to the color name CA # 25208-13 */
		/* code changed to add Print Code to the color name CA # 99953-13 */
		LCSMaterialColor matColor = null;
		LCSColor clr = null;
		String matColorOid = "OR:com.lcs.wc.material.LCSMaterialColor:" + materialColorId;
		String colorOid = "OR:com.lcs.wc.color.LCSColor:" + colorId;
		String dyeCode = "";
		String clrSpecVer = "";
		String printCode = "";
		
		if(!colorId.equals("0")){
			clr = (LCSColor) LCSQuery.findObjectById (colorOid);
			if(clr != null)
			printCode = (String) clr.getValue("hbiPrintCode");
		}
		if((!materialColorId.equals("0")) && (!materialColorId.equals(""))){
			matColor = (LCSMaterialColor) LCSQuery.findObjectById (matColorOid);
			LCSMaterial mat = null;
			
			mat = (LCSMaterial) matColor.getValue("hbiDyeCode");
			if(mat != null)	
				dyeCode = mat.getName();
				
			mat = (LCSMaterial) matColor.getValue("hbiMaterialColorVersion");
			LCSLog.debug("matColor.MatColorGen.hbiMaterialColorVersion: "+matColor.getValue("hbiMaterialColorVersion"));
			if(mat != null)
				clrSpecVer = mat.getName();
			LCSLog.debug("matColor.MatColorGen.clrSpecVer: "+clrSpecVer);

			clr = (LCSColor) matColor.getColor();
			//System.out.println(clr.getName());
			if(clr != null)
				printCode = (String) clr.getValue("hbiPrintCode");
		}
	     //Start HBI GP techpack customization - 09/07/18 
		
		//If color specific functionality enabled on GP, then get GP required color specific data
		if (TRUE.equalsIgnoreCase(GP_BOMREPORT_COLORSPEC_ENABLED)) {
        	colorName =HBITechPackUtil.getGPBOMColor(colorName,clrSpecVer,colorId,dyeCode,printCode);
			}
        
		//End HBI GP techpack customization - 09/07/18 
		
        else{  
		if(FormatHelper.hasContent(clrSpecVer))
			colorName = colorName + " - " + clrSpecVer;
			
		if(FormatHelper.hasContent(dyeCode))
			colorName = colorName + " - " + dyeCode;
			
		if(FormatHelper.hasContent(printCode))
			colorName = colorName + " - " + printCode;			
		/* -- END -- */
        }
        //LCSSKU sku = LCSSKUQuery.getSKURevA(skuId);
        String coloridstring = row.getString(COLORIDS);

        if(!FormatHelper.hasContent(coloridstring)){
            coloridstring = skuId;
        }
        else{
            coloridstring = coloridstring + MOAHelper.DELIM + skuId;
        }
        row.put(COLORIDS, coloridstring);
        row.put(skuId + "." + COLORNAME, colorName);
        row.put(skuId + "." + COLORDESC, colorDescription);
        row.put(skuId + "." + COLORID, colorId);
        row.put(skuId + "." + MATERIALCOLORID, materialColorId);
        //row.put(skuId + "." + COLORHEX, colorMci.colorHexValue);

        if(FormatHelper.hasContent(colorId)){
            LCSColor color = (LCSColor)LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSColor:" + colorId);
            if(color.getColorHexidecimalValue() != null){
                row.put(skuId + "." + COLORHEX, color.getColorHexidecimalValue());
            }
            if(color.getThumbnail() != null){
                row.put(skuId + "." + COLORTHUMB, color.getThumbnail());
            }
        }
    }
   
 
	/**
     * @param product_id
     * @param part
     * @throws WTException
     * @Date 09/07/18
     * The below method sets different flags for Garment products and on it BOM's.
     */
	private void setGPFlags(FlexBOMPart part) throws WTException {

		// Below flag used to check if the product is GP
		isProductGP = true;
	
		// Below flag used to check if garment product bom type is "RoutingTable"
		isBOMGPRoutingTable = isBOMGPRoutingTable(part);
		
		// Below flag used to check if garment product bom type is "RoutingTable"
		isBOMGPAttribution = isBOMGPAttribution(part);
		
	}

	/**
     * Populates color data for a complex material sub Component row given a materialColor dimension
     */
    private void addComplexColorData(FlexBOMPart bomPart, FlexObject row, String skuId, String matColorId) throws WTException {

       FlexType bomType =  FlexTypeCache.getFlexTypeRoot("BOM");
       String matColorOid = "OR:com.lcs.wc.material.LCSMaterialColor:" + matColorId;
       String branchId = row.getString("FLEXBOMLINK.BRANCHID");
       LCSMaterialColor matColor = (LCSMaterialColor) LCSQuery.findObjectById (matColorOid);
       HashMap SubComponentsColorMap = (HashMap) (new ComplexMaterialColorQuery()).findSubComponentsColorMap (bomPart, matColor, bomType);

       if (SubComponentsColorMap.containsKey (branchId))
       {
          FlexObject colorData = (FlexObject) SubComponentsColorMap.get(branchId);
          MaterialColorInfo subComponentColorMci = MCIDimensionHelper.getMaterialColorInfo(colorData);

          row.put(skuId + "." + COLORNAME, subComponentColorMci.colorName);
          row.put(skuId + "." + COLORDESC, subComponentColorMci.colorDescription);
          row.put(skuId + "." + COLORID, subComponentColorMci.colorId);
          row.put(skuId + "." + MATERIALCOLORID, subComponentColorMci.materialColorId);

          if(FormatHelper.hasContent(subComponentColorMci.colorId)){
             LCSColor color = (LCSColor)LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSColor:" + subComponentColorMci.colorId);
             if(color.getColorHexidecimalValue() != null){
                 row.put(skuId + "." + COLORHEX, color.getColorHexidecimalValue());
             }
             if(color.getThumbnail() != null){
                 row.put(skuId + "." + COLORTHUMB, color.getThumbnail());
             }
          }
       }
       else
       {
          addUnavailableData(skuId, row);
       }
    }
    /**
     * Populates dimensions data for a row
     */
    private void addDimensionsData(FlexObject row, Map dimensionSubSet) throws WTException{

         if(dimensionSubSet.get(MCIDimensionHelper.SKU) != null)
         {
             Collection skuList = (Collection)dimensionSubSet.get(MCIDimensionHelper.SKU);

             String colorstring = "";
             for (Iterator iter = skuList.iterator(); iter.hasNext();)
             {
                String skuId = (String) iter.next();
                LCSSKU sku = LCSSKUQuery.getSKURevA(skuId);
                String skuName = (String)sku.getValue("skuName");
                colorstring = addString(colorstring, skuName);
             }
             row.put(COLORS, colorstring);
         }

         if(dimensionSubSet.get(MCIDimensionHelper.SIZE1) != null)
         {
             Collection size1List = (Collection)dimensionSubSet.get(MCIDimensionHelper.SIZE1);
             String size1string = "";
             for (Iterator iter = size1List.iterator(); iter.hasNext();)
             {
                String size1 = (String) iter.next();
                size1string = addString(size1string, size1);
             }
             row.put(SIZE1, size1string);

         }
         if(dimensionSubSet.get(MCIDimensionHelper.SIZE2) != null)
         {
             Collection size2List = (Collection)dimensionSubSet.get(MCIDimensionHelper.SIZE2);
             String size2string = "";
             for (Iterator iter = size2List.iterator(); iter.hasNext();)
             {
                String size2 = (String) iter.next();
                size2string = addString(size2string, size2);
             }
             row.put(SIZE2, size2string);
         }
         if(dimensionSubSet.get(MCIDimensionHelper.SOURCE) != null)
         {
             Collection sourceList = (Collection)dimensionSubSet.get(MCIDimensionHelper.SOURCE);
             String sourcestring = "";
             for (Iterator iter = sourceList.iterator(); iter.hasNext();)
             {
                String sourceId = (String) iter.next();
                LCSSourcingConfigMaster  source = (LCSSourcingConfigMaster) LCSQuery.findObjectById("OR:com.lcs.wc.sourcing.LCSSourcingConfigMaster:" + sourceId);
                String sourceName = (String)source.getSourcingConfigName() ;
                sourcestring = addString(sourcestring, sourceName);
             }
             row.put(SOURCES, sourcestring);

         }
         if(dimensionSubSet.get(MCIDimensionHelper.DESTINATION) != null)
         {
             Collection destinationList = (Collection)dimensionSubSet.get(MCIDimensionHelper.DESTINATION);
             String destinationstring = "";
             for (Iterator iter = destinationList.iterator(); iter.hasNext();)
             {
                String destinationId = (String) iter.next();
                destinationId = "OR:com.lcs.wc.product.ProductDestination:" + destinationId;
                ProductDestination destination = (ProductDestination)LCSQuery.findObjectById(destinationId);
                String destinationName = (String)destination.getDestinationName();
                destinationstring = addString(destinationstring, destinationName);
             }
             row.put(DESTINATION, destinationstring);
         }
    }
	 /*Added for Hanes customization - start*/
     //calculating 'Usage(LB/DZ)' 
	 //inputs are 'Conditioned Width(material)','Total Marker Length(IN)', 'Gmts/Mkr','Ply' and 'Weight(material)'
	 protected void calUsageLB(FlexObject branch)
	 {	   
	   double usage = 0.0;
	   double conWidth = 0.0;
	   double totalMkrLngth = 0.0;
	   double gmtsMkr = 0.0;
	   double ply = 0.0;
	   double weight = 0.0;

	   String strUsageLB  = branch.getData(KeyUsageLB);
	   String strMatContWidth  = branch.getData(KeyMatContWidth);
	   String strTotalMarkerLength  = branch.getData(KeyTotalMarkerLength);
	   String strGmtsMkr  = branch.getData(KeyGmtsMkr);
	   String strply  = branch.getData(Keyply);
	   String strMatWght  = branch.getData(KeyMatWght);
	   
	   if(strUsageLB != null)
			usage = Double.parseDouble(strUsageLB);
		
		if(strMatContWidth != null)
			conWidth = Double.parseDouble(strMatContWidth);
		
		if(strTotalMarkerLength != null)
			totalMkrLngth = Double.parseDouble(strTotalMarkerLength);		
	 
	    if(strGmtsMkr != null)
			gmtsMkr = Double.parseDouble(strGmtsMkr);
		   	
        if(strply != null)
			ply = Double.parseDouble(strply);	
		 	
        if(strMatWght != null)
			weight = Double.parseDouble(strMatWght);	
        
		//applying the formula
	
        usage =  (conWidth * totalMkrLngth / gmtsMkr * 12 * ply / 36 / 36) * (weight / 16);
		
		 //setting the val	
		 branch.put(KeyUsageLB , (new StringBuilder()).append("").append(usage).toString());
	 }

    //calculating 'Trim Bias Usage(LB/DZ)' 
    //inputs are 'Trim Cut Width (IN)','Total Length (IN)', '#Gmts' and 'Weight(material)'

	 protected void calTrimBiasLB(FlexObject branch)
	 {		 	   
	   double trimBiasUsage = 0.0;
	   double trimCutWidth = 0.0;
	   double totalLngth = 0.0;
	   double gmts = 0.0;
	   double weight = 0.0;

	   String strTrimBiasUsageLB  = branch.getData(KeyTrimBiasUsageLB);
	   String strTrimCutWidth  = branch.getData(KeyTrimCutWidth);
	   String strTotalLength  = branch.getData(KeyTotalLength);
	   String strGmts  = branch.getData(KeyGmts);
	   String strMatWght  = branch.getData(KeyMatWght);
	   
	   if(strTrimBiasUsageLB != null)
			trimBiasUsage = Double.parseDouble(strTrimBiasUsageLB);
		
		if(strTrimCutWidth != null)
			trimCutWidth = Double.parseDouble(strTrimCutWidth);
		
		if(strTotalLength != null)
			totalLngth = Double.parseDouble(strTotalLength);
		
	    if(strGmts != null)
			gmts = Double.parseDouble(strGmts);
	  	
        if(strMatWght != null)
			weight = Double.parseDouble(strMatWght);	
        
		//applying the formula	
        trimBiasUsage =  trimCutWidth * totalLngth * 12 / gmts / 1296 * weight / 16 ;

		 //setting the val	
    
		  branch.put(KeyTrimBiasUsageLB, (new StringBuilder()).append("").append(trimBiasUsage).toString());
	 }

	 //calculating 'Usable Cuts' 
     //inputs are 'Conditioned Width(material)' and 'Trim Cut Width (IN)'

	 protected void calUsableCuts(FlexObject branch)
	 {	   
	   double usableCuts = 0.0;
	   double conWidth = 0.0;
	   double trimCutWidth = 0.0;
	  
	   String strUsableCuts  = branch.getData(KeyUsableCuts);
	   String strConWidth  = branch.getData(KeyMatContWidth);
	   String strTrimCutWidth  = branch.getData(KeyTrimCutWidth);
  
	   if(strUsableCuts != null)
			usableCuts = Double.parseDouble(strUsableCuts);		

		if(strConWidth != null)
			conWidth = Double.parseDouble(strConWidth);

		if(strTrimCutWidth != null){
			trimCutWidth = Double.parseDouble(strTrimCutWidth);
		}
		        
		//applying the formula	
		if(trimCutWidth != 0.0)
        usableCuts =  (conWidth - 1.5) / trimCutWidth;

		//setting the val	   
	    branch.put(KeyUsableCuts, (new StringBuilder()).append("").append(usableCuts).toString());
	 }

	 //calculating 'MU Loss (%)' 
	 //inputs are 'Conditioned Width(material)', 'Trim Cut Width (IN)', 'Rounded Usable Cuts' and 'Ply'
	 protected void calMULoss(FlexObject branch)
	 {   
	   double muLoss = 0.0;
	   double rndUsbleCuts = 0.0;
	   double conWidth = 0.0;
	   double trimCutWidth = 0.0;
	   double ply = 0.0;

	   String strMuLoss  = branch.getData(KeyMuLoss);
	   String strRoundedUsbleCuts  = branch.getData(KeyRoundedUsbleCuts);
	   String strConWidth  = branch.getData(KeyMatContWidth);
	   String strTrimCutWidth  = branch.getData(KeyTrimCutWidth);
	   String strPly  = branch.getData(Keyply);
	   
	   if(strMuLoss != null)
			muLoss = Double.parseDouble(strMuLoss);

		if(strRoundedUsbleCuts != null)
			rndUsbleCuts = Double.parseDouble(strRoundedUsbleCuts);

		if(strConWidth != null)
			conWidth = Double.parseDouble(strConWidth);	
	   
	    if(strTrimCutWidth != null)
			trimCutWidth = Double.parseDouble(strTrimCutWidth);	
	  	
        if(strPly != null)
			ply = Double.parseDouble(strPly);	
        
		//applying the formula	
        muLoss =  (1 - ((rndUsbleCuts * ply) * trimCutWidth * 36 / (conWidth * ply * 36))) * 100 ;

		//setting the val	
		 branch.put(KeyMuLoss, (new StringBuilder()).append("").append(muLoss).toString());
	 }

	 //calculating 'Total Length (IN)' 
	 //inputs are 'Allowance (%)', 'MU Loss (%)', 'Runoff (IN)' and 'Length (IN)'
	 protected void calTotalLength(FlexObject branch)
	 {   
	   double muLoss = 0.0;
	   double length = 0.0;
	   double runOff = 0.0;
	   double allowance = 0.0;
	   double totalLength = 0.0;

	   String strMuLoss  = branch.getData(KeyMuLoss);	  
	   String strLength  = branch.getData(KeyLength);  
	   String strRunOff  = branch.getData(KeyRunoff);	     
	   String strAllowance  = branch.getData(KeyAllowance);   	     
	   String strTotalLength  = branch.getData(KeyTotalLength);
   	   	     
	   if(strMuLoss != null)
			muLoss = Double.parseDouble(strMuLoss);

		if(strLength != null)
			length = Double.parseDouble(strLength);

		if(strRunOff != null)
			runOff = Double.parseDouble(strRunOff);	
	   
	    if(strAllowance != null)
			allowance = Double.parseDouble(strAllowance);	
	  	
        if(strTotalLength != null)
			totalLength = Double.parseDouble(strTotalLength);		 
        
		//applying the formula	
        totalLength =  (length + runOff) * (1 + (muLoss/100 + allowance/100)) ;

		//setting the val	
		branch.put(KeyTotalLength, (new StringBuilder()).append("").append(totalLength).toString());
	 }

     //calculating 'Trim Straight Usage' 
     //inputs are 'Usable Cuts', 'Total Length (IN)', '#Gmts' and 'Weight(material)'
	 protected void calTrimStraightLB(FlexObject branch)
	 {		   
	   double trimStrghtUsage = 0.0;
	   double trimCutWidth = 0.0;
	   double totalLngth = 0.0;
	   double gmts = 0.0;
	   double weight = 0.0;

	   String strTrimStrghtUsage  = branch.getData(KeyTrimStrghtUsage);
	   String strTrimCutWidth  = branch.getData(KeyTrimCutWidth);
	   String strTotalLength  = branch.getData(KeyTotalLength);
	   String strGmts  = branch.getData(KeyGmts);
	   String strMatWght  = branch.getData(KeyMatWght);
	   
	   if(strTrimStrghtUsage != null)
			trimStrghtUsage = Double.parseDouble(strTrimStrghtUsage);		

	   if(strTrimCutWidth != null)
			trimCutWidth = Double.parseDouble(strTrimCutWidth);	

	   if(strTotalLength != null)
			totalLngth = Double.parseDouble(strTotalLength);
		   
	   if(strGmts != null)
			gmts = Double.parseDouble(strGmts);	
	  	
       if(strMatWght != null)
			weight = Double.parseDouble(strMatWght);	
        
		//applying the formula
         trimStrghtUsage =  (trimCutWidth * totalLngth * 12 / gmts / 1296 * weight / 16) ;
		 
		 //setting the val	
		  branch.put(KeyTrimStrghtUsage, (new StringBuilder()).append("").append(trimStrghtUsage).toString());
	 }

	//calculating 'Row Total' for Spread section 
	//inputs are 'Price (material)', 'Usage (LB/DZ)' "Waste Factor Cow", 'Waste Factor TTW' and 'Waste Factor END'
	protected void calSpreadRowTotal(FlexObject branch)
	 {   
	   double rowTotal = 0.0;
	   double matPrice = 0.0;
	   double usageLB = 0.0;
	   double COW = 0.0;
	   double TTW = 0.0;
	   double END = 0.0;

	   String strRowTotal  = branch.getData(rowTotalKey);
	   String strMatPrice  = branch.getData(KeyUsagePrice);
	   String strUsageLB  = branch.getData(KeyUsageLB);
	   String strCOW  = branch.getData(KeyCOW);
       String strTTW  = branch.getData(KeyTTW);
	   String strEND  = branch.getData(KeyEND);
  
	   if(strRowTotal != null)
			rowTotal = Double.parseDouble(strRowTotal);	

		if(strMatPrice != null)
			matPrice = Double.parseDouble(strMatPrice);	

		if(strUsageLB != null)
			usageLB = Double.parseDouble(strUsageLB);		

		if(strCOW != null)
			COW = Double.parseDouble(strCOW);		

		if(strTTW != null)
			TTW = Double.parseDouble(strTTW);		

		if(strEND != null)
			END = Double.parseDouble(strEND);		
		   
		//applying the formula
		if(! Double.isNaN(usageLB) && usageLB != 1.0/0.0)
	    rowTotal =  matPrice *usageLB* ( 1 + COW/100 + TTW/100 + END/100) ; 

		//setting the val	
		 branch.put(rowTotalKey, (new StringBuilder()).append("").append(rowTotal).toString());
	 }

     //calculating 'Row Total' for Bias section 
     //inputs are 'Price (material)', 'Trim Bias Usage (LB/DZ)' "Waste Factor Cow", 'Waste Factor TTW' and 'Waste Factor END'
	 protected void calBiasRowTotal(FlexObject branch)
	 { 
	   double rowTotal = 0.0;
	   double matPrice = 0.0;
	   double trimBiasUsage = 0.0;
	   double COW = 0.0;
	   double TTW = 0.0;
	   double END = 0.0;
	  
	   String strRowTotal  = branch.getData(rowTotalKey);
	   String strMatPrice  = branch.getData(KeyUsagePrice);
	   String strTrimBiasUsage  = branch.getData(KeyTrimBiasUsageLB);
	   String strCOW  = branch.getData(KeyCOW);
       String strTTW  = branch.getData(KeyTTW);
	   String strEND  = branch.getData(KeyEND);

	   if(strRowTotal != null)
			rowTotal = Double.parseDouble(strRowTotal);
		
		if(strMatPrice != null)
			matPrice = Double.parseDouble(strMatPrice);
		
		if(strTrimBiasUsage != null)
			trimBiasUsage = Double.parseDouble(strTrimBiasUsage);

		if(strCOW != null)
			COW = Double.parseDouble(strCOW);		

		if(strTTW != null)
			TTW = Double.parseDouble(strTTW);		

		if(strEND != null)
			END = Double.parseDouble(strEND);	
		      
		//applying the formula
		if(! Double.isNaN(trimBiasUsage) && trimBiasUsage != 1.0/0.0)
	    rowTotal =  matPrice * trimBiasUsage*(1  + COW/100 + TTW/100 + END/100) ;
	 
		//setting the val	      
		branch.put(rowTotalKey, (new StringBuilder()).append("").append(rowTotal).toString());
	 }

     //calculating 'Row Total' for Straight section 
	 //inputs are 'Price (material)', 'Trim Straight Usage (LB/DZ)', "Waste Factor Cow", 'Waste Factor TTW' and 'Waste Factor END'
	 protected void calStraightRowTotal(FlexObject branch)
	 { 
	   double rowTotal = 0.0;
	   double matPrice = 0.0;
	   double trimStrghtUsage = 0.0;
	   double COW = 0.0;
	   double TTW = 0.0;
	   double END = 0.0;
	  
	   String strRowTotal  = branch.getData(rowTotalKey);
	   String strMatPrice  = branch.getData(KeyUsagePrice);
	   String strTrimStrghtUsage  = branch.getData(KeyTrimStrghtUsage);
       String strCOW  = branch.getData(KeyCOW);
       String strTTW  = branch.getData(KeyTTW);
	   String strEND  = branch.getData(KeyEND);
	   
	   if(strRowTotal != null)
			rowTotal = Double.parseDouble(strRowTotal);	

		if(strMatPrice != null)
			matPrice = Double.parseDouble(strMatPrice);

		if(strTrimStrghtUsage != null)
			trimStrghtUsage = Double.parseDouble(strTrimStrghtUsage);

		if(strCOW != null)
			COW = Double.parseDouble(strCOW);		

		if(strTTW != null)
			TTW = Double.parseDouble(strTTW);		

		if(strEND != null)
			END = Double.parseDouble(strEND);	
		  
		//applying the formula
		if(! Double.isNaN(trimStrghtUsage) && trimStrghtUsage != 1.0/0.0)
	    rowTotal =  matPrice * trimStrghtUsage*( 1 + COW/100 + TTW/100 + END/100) ;
		 
		//setting the val	       
		branch.put(rowTotalKey, (new StringBuilder()).append("").append(rowTotal).toString());
	 }

	 //calculating 'Row Total' for remaining sections 
	 //inputs are 'Price (material)', 'Usage' and 'Std Waste Factor (%)'
	 protected void calOtherRowTotal(FlexObject branch)
	 { 
	   double rowTotal = 0.0;
	   double matPrice = 0.0;
	   double usage = 0.0;
	   double stdWstFactor = 0.0;
	  
	   String strRowTotal  = branch.getData(rowTotalKey);
	   String strMatPrice  = branch.getData(KeyUsagePrice);
	   String strUsage  = branch.getData(KeyUsage);
       String strStdWstFactor  = branch.getData(KeyStdWstFactor);
  	   
	   if(strRowTotal != null)
			rowTotal = Double.parseDouble(strRowTotal);	

		if(strMatPrice != null)
			matPrice = Double.parseDouble(strMatPrice);

		if(strUsage != null)
			usage = Double.parseDouble(strUsage);

		if(strStdWstFactor != null)
			stdWstFactor = Double.parseDouble(strStdWstFactor);		

		//applying the formula
		if(! Double.isNaN(usage) && usage != 1.0/0.0)
	    rowTotal =  matPrice * usage ;
		 
		//setting the val	
		branch.put(rowTotalKey, (new StringBuilder()).append("").append(rowTotal).toString());
	 }

	/*Added for Hanes customization - end*/
 }