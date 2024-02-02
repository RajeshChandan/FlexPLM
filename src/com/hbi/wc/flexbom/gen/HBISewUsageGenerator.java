package com.hbi.wc.flexbom.gen;

import wt.util.WTException;
import wt.util.WTMessage;

import java.util.*;
import com.lcs.wc.client.web.*;
import com.lcs.wc.flextype.*;
import com.lcs.wc.db.*;
import com.lcs.wc.flexbom.*;
import com.lcs.wc.product.*;
import com.lcs.wc.material.*;
import com.lcs.wc.util.*;

import com.lcs.wc.flexbom.gen.*;

/**
*
* @author Manoj From UST
* @Date Oct 4, 2018, 12:58 PM
* 
*
*       This class is implemented by taking the code from HBISizeGenerator and
*       modified as per requirement
*  
* 
*/
public class HBISewUsageGenerator extends BomDataGenerator {
    
    public static final String MATERIAL_TYPE_PATH = "MATERIAL_TYPE_PATH";
    
    private static final String MATERIALREFID = "IDA3B5";
    private static final String SUPPLIERREFID = "IDA3C5";
    protected static String SIZE_DISPLAY_COL = "";
    
    private static final String DISPLAY_VAL = "DISPLAY_VAL";
    
    public String SIZES = "SIZES";
    
    public String sizeLabel = WTMessage.getLocalizedMessage ( RB.SOURCING, "sizesColumn_LBL", RB.objA ) ;
    
    @SuppressWarnings("rawtypes")
	private Collection sizes = new ArrayList();
    private String source = "";
    
//    private String MAT_NAME_ATT = "ATT1";
//    private String COMP_NAME_ATT = "ATT1";
//    @SuppressWarnings("unused")
//	private String COMP_NAME_DISPLAY = "ATT1";
//    
//    private String SUP_NAME_ATT = "ATT1";
//    @SuppressWarnings("unused")
//	private String COLOR_NAME_ATT = "ATT4";
//    private String PRICE_ATT = "NUM1";
//    private String MAT_DESCRIPTION_ATT = "ATT3";
//
//	private String PRICE_OVR_ATT = "NUM2";
//	private String QUANTITY_ATT = "NUM1";
//	private String LOSS_ADJ_ATT = "NUM3";
//	private String ROW_TOTAL_ATT = "NUM4";
    private String MAT_NAME_ATT = "ptc_str_1typeInfoLCSMaterial";
    private String COMP_NAME_ATT = "ptc_str_4typeInfoFlexBOMLink";
    @SuppressWarnings("unused")
	private String COMP_NAME_DISPLAY = "ptc_str_4typeInfoFlexBOMLink";
    
    private String SUP_NAME_ATT = "ptc_str_1typeInfoLCSSupplier";
    @SuppressWarnings("unused")
	private String COLOR_NAME_ATT = "ptc_str_7typeInfoFlexBOMLink";
    private String PRICE_ATT = "ptc_dbl_1typeInfoLCSMaterial";
    private String MAT_DESCRIPTION_ATT = "ptc_str_5typeInfoFlexBOMLink";

	private String PRICE_OVR_ATT = "ptc_dbl_2typeInfoFlexBOMLink";
	private String QUANTITY_ATT = "ptc_dbl_1typeInfoFlexBOMLink";
	private String LOSS_ADJ_ATT = "ptc_dbl_3typeInfoFlexBOMLink";
	private String ROW_TOTAL_ATT = "ptc_dbl_5typeInfoFlexBOMLink";

	@SuppressWarnings("unused")
	private String priceKey = "LCSMATERIALSUPPLIER." + PRICE_ATT;
	@SuppressWarnings("unused")
	private String overrideKey = "FLEXBOMLINK." + PRICE_OVR_ATT;
	@SuppressWarnings("unused")
	private String quantityKey = "FLEXBOMLINK." + QUANTITY_ATT;
	@SuppressWarnings("unused")
	private String lossAdjustmentKey = "FLEXBOMLINK." + LOSS_ADJ_ATT;
	private String rowTotalKey = "FLEXBOMLINK." + ROW_TOTAL_ATT;

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
/* Added for CA # 318-13 -- Start */
	private String strGarmentUse = "";
	private String strPartCd = "";
	private String strPartCdBias = "";
	private String strPartCdTrimStght = "";
	private String strPartCdSpread = "";
	private String strSection = "";	
/* Added for CA # 318-13 -- End */

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
/* Added for CA # 318-13 -- Start */	
	@SuppressWarnings("unused")
	private String KeyGarmentUse = "";
	@SuppressWarnings("unused")
	private String KeyPartCd = "";
	@SuppressWarnings("unused")
	private String KeyPartCdBias = "";
	@SuppressWarnings("unused")
	private String KeyPartCdTrimStght = "";
	@SuppressWarnings("unused")
	private String KeyPartCdSpread = "";
	@SuppressWarnings("unused")
	private String KeySection = "";	
/* Added for CA # 318-13 -- End */
	/*Added for Hanes customization - end*/

    
    /** Creates a new instance of CWMatColorGenerator */
    private FlexType materialType = null;
    private FlexType supplierType = null;
    private FlexType bomType = null;
    private FlexType colorway_bomType = null;
    @SuppressWarnings("unused")
	private FlexType garment_boType = null;
    public boolean USE_DEFAULT_COLUMNS = LCSProperties.getBoolean("com.lcs.wc.flexbom.gen.SizeGenerator.useDefaultColumns");
    
    public boolean DEBUG = LCSProperties.getBoolean("com.lcs.wc.flexbom.gen.HBISewUsageGenerator.verbose");

    public float partNameWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.SizeGenerator.partNameWidth", "1.5"))).floatValue();
    public float materialNameWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.SizeGenerator.materialNameWidth", "1.25"))).floatValue();
    public float supplierNameWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.SizeGenerator.supplierNameWidth", "1.25"))).floatValue();
    public float sizeWidth = (new Float(LCSProperties.get("com.lcs.wc.flexbom.gen.SizeGenerator.sizeWidth", "0.75"))).floatValue();
    
    public int imageWidth = Integer.parseInt(LCSProperties.get("com.lcs.wc.flexbom.gen.SizeGenerator.matThumbWidth", "75"));
    public int imageHeight = Integer.parseInt(LCSProperties.get("com.lcs.wc.flexbom.gen.SizeGenerator.matThumbHeight", "0"));
    
    public HBISewUsageGenerator() {
    }
    
    public Collection getBOMData() throws WTException {
        return this.dataSet;
    }
    
    //The below method returns columns which required for HBI sew usage bom report
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Collection getTableColumns() throws WTException {
        ArrayList viewAtts = new ArrayList();
        Map viewColumns = new HashMap();
        
        Collection columns = new ArrayList();
        this.view = null;
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
      /*  if(!viewAtts.contains("ColorDescription")){
        	viewAtts.add("ColorDescription");
        }*/
        // hbi don't want supplierName on tech pack
		//viewAtts.add(0, "supplierName");
        
		viewAtts.add(0, "materialDescription");
		
       /* if(this.sizes != null && this.sizes.size() > 0){
            viewAtts.add(0, SIZES);
        }*/
        /*viewAtts.add(0, "partName");
        if(this.useMatThumbnail){
            viewAtts.add(0, "MATERIAL.thumbnail");
        }*/
        
        /*if(this.view == null){
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
        }*/
        
        if(this.view == null){
          
                TableColumn column = null;
                FlexTypeGenerator flexg = new FlexTypeGenerator();
                FlexTypeAttribute att = null;
                
                
              /*  att = materialType.getAttribute("materialPrice");
                column = flexg.createTableColumn(null, att, materialType, false, "LCSMATERIALSUPPLIER");
                viewColumns.put("Material.price", column);
*/            
              //The below code is for columns Material.hbiItemDescription
                att = materialType.getAttribute("hbiItemDescription");
                column = flexg.createTableColumn(null, att, materialType, false, "LCSMATERIAL");
                viewColumns.put("Material.hbiItemDescription", column);
                viewAtts.add("Material.hbiItemDescription");
                debug("hbiItemDescription"+column);
             
              //The below code is for columns BOM.hbiGarmentUse
               att = colorway_bomType.getAttribute("hbiGarmentUse");
                
                String tableName = att.getRefDefinition().getRefTable() + FormatHelper.getObjectId(att) +att.getAttScope() +
          				att.getAttObjectLevel();
                debug("tableName::"+tableName);
          			tableName = "" + tableName.hashCode();
          			debug("tableName::"+tableName);
          			tableName = "T" + tableName.substring(1);
          			
                column = new TableColumn();
                column.setHeaderLabel(att.getAttDisplay());
                //column.setTableIndex(tableName+".ATT1");
                column.setTableIndex(tableName+".ptc_str_1typeInfoLCSLifecycl");
                column.setDisplayed(true);
                column.setSpecialClassIndex(att.getAttDisplay() + "_CLASS_OVERRIDE");
                column.setPdfColumnWidthRatio(materialNameWidth);
                //column.setAlign("center");
                debug("hbiGarmentUse::"+column);
                viewColumns.put("BOM.hbiGarmentUse", column);
                viewAtts.add("BOM.hbiGarmentUse"); 
               
              //The below code is for columns Material.hbiUsageUOM
                att = materialType.getAttribute("hbiUsageUOM");
                column = flexg.createTableColumn(null, att, materialType, false, "LCSMATERIAL");
                viewColumns.put("Material.hbiUsageUOM", column);
                viewAtts.add("Material.hbiUsageUOM");
                
                //The below code is for columns BOM.quantity Usage per Dozen
                att = colorway_bomType.getAttribute("quantity");
                column = flexg.createTableColumn(null, att, colorway_bomType, false, "FLEXBOMLINK");
                viewColumns.put("BOM.quantity", column);
				viewAtts.add("BOM.quantity"); 
                
                //viewAtts.add("Material.price");
               
          
            
           viewAtts.add("SIZE");
        }
        
        TableColumn column = new TableColumn();
        
    /*    column = new BOMPartNameTableColumn();
        column.setHeaderLabel(COMP_NAME_DISPLAY);
        column.setTableIndex("FLEXBOMLINK." + COMP_NAME_ATT);
        ((BOMPartNameTableColumn)column).setSubComponetIndex("FLEXBOMLINK.MASTERBRANCHID");
        ((BOMPartNameTableColumn)column).setComplexMaterialIndex("FLEXBOMLINK.MASTERBRANCH");
        ((BOMPartNameTableColumn)column).setLinkedBOMIndex("FLEXBOMLINK.LINKEDBOM");
        
        column.setDisplayed(true);
        column.setSpecialClassIndex("CLASS_OVERRIDE");
        column.setPdfColumnWidthRatio(partNameWidth);
        viewColumns.put("partName", column);*/
        
        /*
        column = new TableColumn();
        column.setHeaderLabel(sizeLabel);
        column.setTableIndex(SIZES);
        column.setDisplayed(true);
        column.setFormat(FormatHelper.MOA_FORMAT);
        viewColumns.put(SIZES, column);*/
        
        column = new BOMMaterialTableColumn();
        column.setHeaderLabel(this.materialLabel);
        column.setTableIndex("LCSMATERIAL." + MAT_NAME_ATT);
        column.setDisplayed(true);
        column.setPdfColumnWidthRatio(materialNameWidth);
        column.setLinkMethod("viewMaterial");
        column.setLinkTableIndex("childId");
        column.setLinkMethodPrefix("OR:com.lcs.wc.material.LCSMaterialMaster:");
        ((BOMMaterialTableColumn)column).setDescriptionIndex("FLEXBOMLINK." + MAT_DESCRIPTION_ATT);
        viewColumns.put("materialDescription", column);
        debug("materialDescription"+column);
             
      
        //hbi do't want supplierName on tech pack
		/*column = new TableColumn();
        column.setHeaderLabel(this.supplierLabel);
        column.setTableIndex("LCSSUPPLIERMASTER.SUPPLIERNAME");
        column.setFormat(FormatHelper.STRING_FORMAT);
        column.setPdfColumnWidthRatio(supplierNameWidth);
        column.setDisplayed(true);
        viewColumns.put("supplierName", column);
		*/
     
       /* column = new TableColumn();
        column.setDisplayed(true);
        column.setHeaderLabel("");
        column.setHeaderAlign("left");
        column.setLinkMethod("launchImageViewer");
        column.setLinkTableIndex("LCSMATERIAL.PRIMARYIMAGEURL");
        column.setTableIndex("LCSMATERIAL.PRIMARYIMAGEURL");
        column.setColumnWidth("1%");
        column.setLinkMethodPrefix("");
        column.setImage(true);
        column.setShowFullImage(this.useMatThumbnail);


        if(imageWidth > 0){
            column.setImageWidth(imageWidth);
        }
        if(imageHeight > 0){
            column.setImageHeight(imageHeight);
        }
        viewColumns.put("MATERIAL.thumbnail", column);*/
        
  /*      BOMColorTableColumn colorColumn = new BOMColorTableColumn();
        colorColumn.setDisplayed(true);
        colorColumn.setTableIndex("LCSCOLOR.COLORNAME");
        colorColumn.setDescriptionIndex("FLEXBOMLINK." + COLOR_NAME_ATT);
        colorColumn.setHeaderLabel(WTMessage.getLocalizedMessage( RB.COLOR, "color_LBL", RB.objA ));
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
        viewColumns.put("ColorDescription", colorColumn);

        */
        debug("Getting columns...sizes: " + sizes);
        if(this.sizes != null && this.sizes.size() > 0){
            Iterator sizeIter = this.sizes.iterator();
            String size = "";
            while(sizeIter.hasNext()){
                size = (String)sizeIter.next();
                debug("creating column for size: " + size);
                column = new TableColumn();
                column.setHeaderLabel(size);
                column.setTableIndex(size + "." + DISPLAY_VAL);
                column.setDisplayed(true);
                column.setSpecialClassIndex(size + "_CLASS_OVERRIDE");
                column.setPdfColumnWidthRatio(sizeWidth);
                column.setAlign("center");
                viewColumns.put(size + "." + DISPLAY_VAL, column);
            }
        }
        
        Iterator vi = viewAtts.iterator();
        String att = "";
        while(vi.hasNext()){
            att = (String)vi.next();
            if("SIZE".equals(att)){
                if(this.sizes != null && this.sizes.size() > 0){
                    Iterator sizeIter = this.sizes.iterator();
                    String size = "";
                    while(sizeIter.hasNext()){
                        size = (String)sizeIter.next();
                        if(viewColumns.get(size + "." + DISPLAY_VAL) != null){
                            columns.add(viewColumns.get(size + "." + DISPLAY_VAL));
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
    
    @SuppressWarnings("rawtypes")
	private void printOverrides(){
        Map orMap = getOverRideMap();
        Iterator ids = orMap.keySet().iterator();
        String tlId = "";
        FlexObject obj = null;
        Collection ors = null;
        while(ids.hasNext()){
            tlId = (String)ids.next();
            LCSLog.debug("tlid: " + tlId);
            ors = (Collection)orMap.get(tlId);
            Iterator t = ors.iterator();
            while(t.hasNext()){
                obj = (FlexObject)t.next();
                LCSLog.debug("\torId: " + obj.getString(HBISewUsageGenerator.DIMID_COL));
            }
        }
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void init(Map params) throws WTException {
        super.init(params);
        debug("params::"+params);
        if(params != null){
            if(params.get(MATERIAL_TYPE_PATH) != null){
                materialType = FlexTypeCache.getFlexTypeFromPath((String)params.get(MATERIAL_TYPE_PATH));
            }
            else{
                materialType = FlexTypeCache.getFlexTypeRoot("Material");
            }
            supplierType = FlexTypeCache.getFlexTypeRoot("Supplier");
            bomType = FlexTypeCache.getFlexTypeRoot("BOM");
            FlexBOMPart bomPart = (FlexBOMPart) params.get(BOMPDFContentGenerator.BOM_PART);
            colorway_bomType = FlexTypeCache.getFlexTypeFromPath(bomPart.getFlexType().getFullName(true));
            garment_boType = FlexTypeCache.getFlexTypeFromPath("Business Object\\Garment Use");
            MAT_NAME_ATT = materialType.getAttribute("name").getColumnName();//.getVariableName();
            SUP_NAME_ATT = supplierType.getAttribute("name").getColumnName();//.getVariableName();
            COMP_NAME_ATT = bomType.getAttribute("partName").getColumnName();//.getVariableName();
            PRICE_ATT = materialType.getAttribute("materialPrice").getColumnName();//.getVariableName();
            MAT_DESCRIPTION_ATT = bomType.getAttribute("materialDescription").getColumnName();//.getVariableName();
            COLOR_NAME_ATT = bomType.getAttribute("colorDescription").getColumnName();//.getVariableName();
            
            COMP_NAME_DISPLAY = bomType.getAttribute("partName").getAttDisplay();

			PRICE_OVR_ATT = bomType.getAttribute("priceOverride").getColumnName();//.getVariableName();
			QUANTITY_ATT = bomType.getAttribute("quantity").getColumnName();//.getVariableName();
			LOSS_ADJ_ATT = bomType.getAttribute("lossAdjustment").getColumnName();//.getVariableName();
			ROW_TOTAL_ATT = bomType.getAttribute("rowTotal").getColumnName();//.getVariableName();
			
			priceKey = "LCSMATERIALSUPPLIER." + PRICE_ATT;
			overrideKey = "FLEXBOMLINK." + PRICE_OVR_ATT;
			quantityKey = "FLEXBOMLINK." + QUANTITY_ATT;
			lossAdjustmentKey = "FLEXBOMLINK." + LOSS_ADJ_ATT;
			rowTotalKey = "FLEXBOMLINK." + ROW_TOTAL_ATT;
			//Added for Hanes Customization - start
			FlexBOMPart part = (FlexBOMPart)params.get(BOMPDFContentGenerator.BOM_PART);
			FlexType type = part.getFlexType();
			//Modified for CA # 318-13 - to consider colorway BOM attributes
			if(type.getFullName().equals("Materials\\HBI\\Spread") || type.getFullName().equals("Materials\\HBI\\Trim"))
			{			
				strUsageLB  = type.getAttribute("hbiUsageLbPerDoz").getColumnName();//.getVariableName();
				strTotalMarkerLength  = type.getAttribute("hbiTotalMarkerLength").getColumnName();//.getVariableName(); 
				strGmtsMkr  = type.getAttribute("hbiGmtsPerMkr").getColumnName();//.getVariableName(); 
				strply  = type.getAttribute("hbiPly").getColumnName();//.getVariableName(); 
				strTrimBiasUsageLB  = type.getAttribute("hbiTrimBiasLbUsage").getColumnName();//.getVariableName(); 
				strTrimCutWidth  = type.getAttribute("hbiTrimCutWidInch").getColumnName();//.getVariableName(); 
				strTotalLength  = type.getAttribute("hbiTotalLengthIn").getColumnName();//.getVariableName(); 
				strGmts  = type.getAttribute("hbiGmts").getColumnName();//.getVariableName(); 
				strUsableCuts  = type.getAttribute("hbiUsableCuts").getColumnName();//.getVariableName(); 
				strMuLoss  = type.getAttribute("hbiMuLossPct").getColumnName();//.getVariableName(); 
				strRoundedUsbleCuts  = type.getAttribute("hbiRoundedUsableCut").getColumnName();//.getVariableName(); 
				strTrimStrghtUsage  = type.getAttribute("hbiTrimStraightLbUsage").getColumnName();//.getVariableName(); 
				strCOW  = type.getAttribute("hbiWasteFactorCow").getColumnName();//.getVariableName(); 
				strTTW  = type.getAttribute("hbiWasteFactorTtw").getColumnName();//.getVariableName(); 
				strEND  = type.getAttribute("hbiWasteFactorEnd").getColumnName();//.getVariableName(); 
				strUsage  = type.getAttribute("hbiUsage").getColumnName();//.getVariableName(); 
				strStdWstFactor   = materialType.getAttribute("hbiStdWasteFactor").getColumnName();//.getVariableName();
				strMatContWidth   = materialType.getAttribute("hbiConWidth").getColumnName();//.getVariableName();
				strMatWght   = materialType.getAttribute("hbiWeight").getColumnName();//.getVariableName();
				strUsagePrice   = materialType.getAttribute("hbiUsagePrice").getColumnName();//.getVariableName();
				strLength  = type.getAttribute("hbiLengthIn").getColumnName();//.getVariableName(); 
				strRunoff  = type.getAttribute("hbiRunoffIn").getColumnName();//.getVariableName(); 
				strAllowance  = type.getAttribute("hbiAllowance").getColumnName();//.getVariableName(); 
			}
			else if(type.getFullName().equals("Materials\\HBI\\Colorway"))
			{
				strGarmentUse = type.getAttribute("hbiGarmentUse").getColumnName();//.getVariableName(); 
				strPartCd = type.getAttribute("hbiPartCode").getColumnName();//.getVariableName(); 
				strPartCdBias = type.getAttribute("hbiPartCodeBias").getColumnName();//.getVariableName(); 
				strPartCdTrimStght = type.getAttribute("hbiPartCodeTrimStraight").getColumnName();//.getVariableName(); 
				strPartCdSpread = type.getAttribute("hbiPartCodeSpread").getColumnName();//.getVariableName(); 
				strSection = type.getAttribute("section").getColumnName();//.getVariableName(); 
			}
						
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
				
				KeyGarmentUse = "FLEXBOMLINK." + strGarmentUse;
				KeyPartCd = "FLEXBOMLINK." + strPartCd;
				KeyPartCdBias = "FLEXBOMLINK." + strPartCdBias;
				KeyPartCdTrimStght = "FLEXBOMLINK." + strPartCdTrimStght;
				KeyPartCdSpread = "FLEXBOMLINK." + strPartCdSpread;
				KeySection = "FLEXBOMLINK." + strSection;
			//Added for Hanes Customization - end
            
            if(this.getSources() != null && this.getSources().size() > 0){
                this.source = (String)this.getSources().iterator().next();
            }
            
            this.sizes = new ArrayList();
            if("SIZE1".equalsIgnoreCase(this.sizeAtt)){
                sizes = this.getSizes1();
            }
            else if("SIZE2".equalsIgnoreCase(this.sizeAtt)){
                sizes = this.getSizes2();
            }
            
            // Added by Manoj - Tech Pack report to output the combined GP and PP requirement
            if (params.get("RAW_DATA") != null) {
				this.dataSet = new ArrayList();
				this.dataSet.addAll((Collection) params.get("RAW_DATA"));
            }
            
            if(this.dataSet != null){
                //printOverrides();
                this.dataSet = filterDataSet(this.dataSet, this.sizeAtt);
                this.dataSet = groupDataToBranchId(this.dataSet, "FLEXBOMLINK.BRANCHID", "FLEXBOMLINK.MASTERBRANCHID", "FLEXBOMLINK.SORTINGNUMBER");
                
                printOverrides();
                debug("\n\n");
                
                Map materialSizeMap = getMaterialSizeMapping();
                SIZE_DISPLAY_COL = this.displayAttCol;
                
                debug("materialSizeMap:\n " + materialSizeMap);
                
                debug("\n\n");
                
                Collection processedData = new ArrayList();
                
                Collection tls = this.getTLBranches();
                Iterator i = tls.iterator();
                FlexObject tlb = new FlexObject();
                while(i.hasNext()){
                    tlb = (FlexObject)i.next();
                    processedData.addAll(processBranch(tlb, materialSizeMap));
                }
                this.dataSet = processedData;
            }
            
        }
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


    @SuppressWarnings({ "rawtypes", "unused", "unchecked", "static-access" })
	private Collection processBranch(FlexObject topLevel, Map materialSizeMap) throws WTException{
        ArrayList data = new ArrayList();
        
        FlexObject tlDataRow = topLevel.dup();
        
		//modified for hanes Customization
        //calculatePrice(tlDataRow);
		calculateSectionAttributes(tlDataRow);

        String tlId = topLevel.getString(this.DIMID_COL);
        debug("\ntopLevelId: " +tlId);
        
        String tlMatId = topLevel.getString("FLEXBOMLINK." + MATERIALREFID);
        String tlMatSupId = topLevel.getString("LCSMATERIALSUPPLIERMASTER.IDA2A2");
		
        if(!FormatHelper.hasContent(tlMatId) || tlMatId.equals(FormatHelper.getNumericFromOid(LCSMaterialQuery.PLACEHOLDERID))){
            tlMatId = "";
        }
        
        Map sizeMap = (Map)materialSizeMap.get(tlId);
        Collection sizeIds = sizeMap.keySet();
        Iterator sizeIt = sizeIds.iterator();
        String size = null;
        MaterialColorInfo mci = null;
        
        FlexObject dr = null;
        
        boolean useTopLevel = false;
        boolean tlMatch = false;
        
        String colorstring = "";
        
        Iterator dataIter = null;
        
        LCSSKU sku = null;
        String skuName = "";
        FlexObject tRow = null;
        
        Collection sizeMaterials = null;
        //IF COLOR APPLIES TO ROW THEN ADD TO COLORS COLUMN, AND ADD DATA TO THE APPROPRIATE
        //SKU COLUMN....IF IT DOES NOT APPLY TO THE ROW, THEN MARK THE SKU COLUMN AS NOT APPLICABLE
        String allLabel = WTMessage.getLocalizedMessage (RB.MAIN, "all_LBL", RB.objA ) ;
        while(sizeIt.hasNext()){
            size = (String)sizeIt.next();
            debug("Processing size: " + size);
            sizeMaterials = (Collection)sizeMap.get(size);
            debug("\t # of material colors for size: " + sizeMaterials.size());
            if(sizeMaterials != null && sizeMaterials.size() > 0){
                debug("\tprocessing found materials");
                Iterator sci = sizeMaterials.iterator();
                while(sci.hasNext()){
                    mci = (MaterialColorInfo)sci.next();
                    if(mci != null){
						 debug("\t\tMCI not null...tlMatSupId: " + tlMatSupId + "      mci.materialSupplierId:" + mci.materialSupplierId);
						 if(tlMatSupId.equals(mci.materialSupplierId)){
                            debug("\t\tAre equal");
                            //matches top level...add to top level info
                            tlMatch = true;
                            FlexObject or = getOverrideRow(tlId, this.sizeAtt, size, mci.materialSupplierId);
                            if(or != null && FormatHelper.hasContent(or.getString(SIZE_DISPLAY_COL))){
                                addSizeData(tlDataRow, size, or.getString(SIZE_DISPLAY_COL));
                            }
                            else{
                                //No override for size/material...look for override independant of material..
                                or = getOverrideRow(tlId, this.sizeAtt, size, null);
                                if(or != null && FormatHelper.hasContent(or.getString(SIZE_DISPLAY_COL))){
                                    addSizeData(tlDataRow, size, or.getString(SIZE_DISPLAY_COL));
                                }
                                else{
                                    addSizeData(tlDataRow, size, tlDataRow.getString(SIZE_DISPLAY_COL));
                                }
                            }
                        }
                        else{
                            debug("\t\tAre NOT equal");
                            
                            //NEED TO LOOP OVER ALL EXISTING MATERIAL ROWS...
                            dataIter = data.iterator();
                            if(dataIter.hasNext()){
                                //There is already existing data...check to see if this material matches existing
                                boolean foundMat = false;
                                while(dataIter.hasNext()){
                                    tRow = (FlexObject)dataIter.next();
                                    String tRowMat = tRow.getString("FLEXBOMLINK." + MATERIALREFID);
                                    if(tRowMat.equals(mci.materialId)){
                                        //Found an existing row that matches the material...update for the color
                                        debug("\t\tfound matching material...adding size to it");
                                        foundMat = true;
                                        FlexObject or = getOverrideRow(tlId, this.sizeAtt, size, mci.materialSupplierId);
                                        if(or != null && FormatHelper.hasContent(or.getString(SIZE_DISPLAY_COL))){
                                            debug("\t\tFound or:" + or);
                                            addSizeData(tRow, size, or.getString(SIZE_DISPLAY_COL));
                                        }
                                        else{
                                            //No override for size...use Top Level value..
                                            debug("\t\tDid not find override record...");
                                            addSizeData(tRow, size, tlDataRow.getString(SIZE_DISPLAY_COL));
                                        }

										
                                    }
                                }
                                //Didn't find a row for the material already...create a new one
                                if(!foundMat){
                                    debug("\t\tDid not find matching material/size...getting override row as starting point.");
                                    
                                    FlexObject newRow = topLevel.dup();;
                                    FlexObject orRow = getOverrideRow(tlId, this.sizeAtt, size, mci.materialSupplierId);
                                    
                                    debug("\t\tFound or for new row:" + orRow);
                                    
                                    addMaterialData(newRow, mci, orRow);
                                    
                                    if(FormatHelper.hasContent(orRow.getString(SIZE_DISPLAY_COL))){
                                        addSizeData(newRow, size, orRow.getString(SIZE_DISPLAY_COL));
                                    }
                                    else{
                                        //No override for size...use Top Level value..
                                        addSizeData(newRow, size, tlDataRow.getString(SIZE_DISPLAY_COL));
                                    }
									//modified for hanes Customization
									//calculatePrice(newRow);
									calculateSectionAttributes(newRow);
                                    data.add(newRow);
                                }
                            }
                            else{
                                //There isn't any data yet...need new row
                                debug("\t\tThere is no data...using new override row.");
                                debug("MaterialName: " + mci.materialName);
                                debug("MaterialName: " + mci.supplierName);
                                FlexObject newRow = topLevel.dup();
                                FlexObject orRow = getOverrideRow(tlId, this.sizeAtt, size, mci.materialSupplierId);
                               
                                debug("\t\tFound or for new row:" + orRow);
                                
                                addMaterialData(newRow, mci, orRow);
                                if(FormatHelper.hasContent(orRow.getString(SIZE_DISPLAY_COL))){
                                    addSizeData(newRow, size, orRow.getString(SIZE_DISPLAY_COL));
                                }
                                else{
                                    //No override for size...use Top Level value..
                                    addSizeData(newRow, size, tlDataRow.getString(SIZE_DISPLAY_COL));
                                }
								//modified for hanes Customization
								//calculatePrice(newRow);
								calculateSectionAttributes(newRow);
                                data.add(newRow);
                            }
                        }
                    }
                    else{
                        //no material assigned...top level must have no material assigned as well...add to top level
                        debug("\tNo mci found");
                        tlMatch = true;
                        FlexObject or = getOverrideRow(tlId, this.sizeAtt, size, null);
                        
                        if(or == null){
                            debug("\n\nDIDN'T FIND OVERRIDE RECORD");
                        }
                        else{
                            debug("\tfound or record...size - "+ SIZE_DISPLAY_COL +  ": " + or.getString(SIZE_DISPLAY_COL));
                        }
                        
                        if(or != null && FormatHelper.hasContent(or.getString(SIZE_DISPLAY_COL))){
                            addSizeData(tlDataRow, size, or.getString(SIZE_DISPLAY_COL));
                        }
                        else{
                            addSizeData(tlDataRow, size, tlDataRow.getString(SIZE_DISPLAY_COL));
                        }
                    }
                }
            } else{
                debug("\tdidn't find colors...adding to top level");
                
                //no material assigned...top level must have no material assigned as well...add to top level
                debug("\touter No mci found");
                tlMatch = true;
                FlexObject or = getOverrideRow(tlId, this.sizeAtt, size, null);
                
                if(or == null){
                    debug("\n\nDIDN'T FIND OVERRIDE RECORD");
                }
                else{
                    debug("\tfound or record...size: " + or.getString(SIZE_DISPLAY_COL));
                }
                if(or != null && FormatHelper.hasContent(or.getString(SIZE_DISPLAY_COL))){
                    addSizeData(tlDataRow, size, or.getString(SIZE_DISPLAY_COL));
                }
                else{
                    addSizeData(tlDataRow, size, tlDataRow.getString(SIZE_DISPLAY_COL));
                }
            }
            
        }
        if(tlMatch){
			//calculatePrice(tlDataRow);
            data.add(0, tlDataRow);
        }
        
        if(data.size() > 0){
            FlexObject row = null;
            
            if(data.size() == 1 && this.sizes != null && this.sizes.size() > 0){
                row = (FlexObject)data.get(0);
                row.put(SIZES, allLabel);
            }
            else{            
                //need to format first column...
                //For now putting dashes...want blank but with different color formatting
                for(int i = 1; i < data.size(); i++){
                    row = (FlexObject)data.get(i);
                    row.put("FLEXBOMLINK." + COMP_NAME_ATT, " ");
                    row.put("CLASS_OVERRIDE", "BOM_OVERRIDE");
                }
            }
            
            //Format Color Columns that don't apply for the rows
            Iterator i  = this.sizes.iterator();
            Iterator di = null;
            String sid = "";
            while(i.hasNext()){
                sid = (String)i.next();
                di = data.iterator();
                while(di.hasNext()){
                    row = (FlexObject)di.next();
                    if(!FormatHelper.hasContent(row.getString(sid + "." + DISPLAY_VAL))){
                        blankSizeData(row, sid);
                    }
                }
            }
        }
        
        return data;
    }
    
    @SuppressWarnings("rawtypes")
	public void addMaterialData(FlexObject row, MaterialColorInfo mci, FlexObject orRow)throws WTException{
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
    
    public void addSizeData(FlexObject row, String size, String value)throws WTException{
        String sizeString = row.getString(SIZES);
        sizeString = addString(sizeString, size);
        row.put(SIZES, sizeString);

        
        value = getDisplayVal(value);        
        row.put(size + "." + DISPLAY_VAL, value);
    }

    public void blankSizeData(FlexObject row, String size)throws WTException{
        row.put(size + "." + DISPLAY_VAL, "X");
        row.put(size + "_CLASS_OVERRIDE", "BOM_OVERRIDE");
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	private Map getMaterialSizeMapping() throws WTException{
        LCSFlexBOMQuery bomQuery = new LCSFlexBOMQuery();
        Map smMap = new HashMap();
        
        Collection tls = this.getTLBranches();
        Map ovrRdMap = this.getOverRideMap();
        
        FlexObject topLevel = null;
        String branchId = "";
        
        Iterator i = tls.iterator();
        Collection fullBranch = null;
        Collection retMat = null;
        
        //Loop over all top level branches
        while(i.hasNext()){
            //Prepare the collectio nfor all overrides
            fullBranch = new ArrayList();
            
            //get top level branch
            topLevel = (FlexObject)i.next();
            //add top level to collection of branches to process
            fullBranch.add(topLevel);
            
            //get the top level branch id
            branchId = topLevel.getString(DIMID_COL);
            
            //Get the branch overrides for the given top level branch
            Collection branchOrs = (Collection)ovrRdMap.get(branchId);
            if(branchOrs != null && branchOrs.size() > 0){
                //if any overrides are found include them in the collection to process
                fullBranch.addAll(branchOrs);
            }
            Map sizeMaterialMap = new HashMap();
            
            Map materialColorMap = getMaterialColorsForBranch(fullBranch, this.sizeAtt);
            
            //If there are colorways to consider
            if(this.sizes != null && this.sizes.size() > 0){
                //Loop over the list of colorways for the BOM
                Iterator sizeIter = this.sizes.iterator();
                while(sizeIter.hasNext()){
                    String size = (String)sizeIter.next();
                    
                    //get each color id and add process it individually...this requires putting each element
                    //into a collection, as the following call requires collections
                    /*
                    Collection sizeIdCol = new ArrayList();
                    sizeIdCol.add(size);
                    
                    //Get the material(s) used for the given color way based on the top level and source and available overrides
                    if("SIZE1".equalsIgnoreCase(this.sizeAtt)){
                        retMat = bomQuery.getUniqueMaterialColorCombinationsForBranch(fullBranch, null, this.sources, sizeIdCol, null);
                    }
                    else if("SIZE2".equalsIgnoreCase(this.sizeAtt)){
                        retMat = bomQuery.getUniqueMaterialColorCombinationsForBranch(fullBranch, null, this.sources, null, sizeIdCol);
                    }
                     **/
                    
                    retMat = getMaterialColorForSize(materialColorMap, branchId, this.sizeAtt, size);
                    
                    //add the returned material to a map keyed by color id
                    sizeMaterialMap.put(size, retMat);
                }
            }
            //If there are no colorways to consider
            else{
                //material for toplevel?  is this required? needed for case where no colorways are passed in?
                //Get the material(s) used for the given top level based on the and source if one was passed
                //retMat = bomQuery.getUniqueMaterialColorCombinationsForBranch(fullBranch, null, this.sources, null, null);
                //retMat = bomQuery.getUniqueMaterialColorCombinationForBOM(fullBranch, null, this.sources, null, null).values();
                retMat = getMaterialColorForSize(materialColorMap, branchId, this.sizeAtt, "-");
                
                //add the returned material to a map keyed by color id...since no color is used, empty string is the key
                sizeMaterialMap.put("", retMat);
            }
            
            //Add the list of color/materials to a map keyed by top level branch id
            smMap.put(branchId, sizeMaterialMap);
        }
        
        return smMap;
    }
    
    protected FlexObject getOverrideRow(String tlId, String sAtt, String sVal, String matSupId){
        FlexObject fobj = null;
        sAtt = sAtt.toUpperCase();
        debug("looking for override: tlid: " + tlId + "   satt:sVal: " + sAtt + ":" + sVal + "    matSupId: " + matSupId);
        
        if(this.getSources() != null && this.getSources().size() > 0){
            //LOOK FOR SOURCE:SKU
            fobj = getOverrideRowForDim(tlId, sAtt + ":" + sVal, matSupId, ":SOURCE:" + sAtt);
            if(fobj != null) return fobj;
        }
        
        //LOOK FOR SKU
        fobj = getOverrideRowForDim(tlId, sAtt + ":" + sVal, matSupId,  ":" + sAtt);
        if(fobj != null) return fobj;
        
        if(this.getSources() != null && this.getSources().size() > 0){
            //LOOK FOR SOURCE
            //fobj = getOverrideRowForDim(tlId, this.source, matSupId, ":SOURCE:SKU");
            //if(fobj != null) return fobj;
            
            //LOOK FOR SOURCE
            fobj = getOverrideRowForDim(tlId, this.source, matSupId, ":SOURCE");
            if(fobj != null) return fobj;
        }
        return null;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
	protected Collection filterDataSet(Collection data, String sAtt){
        FlexObject fobj = null;
        Collection filteredData = new ArrayList();
		
		boolean useSource = this.getSources() != null && this.getSources().size() > 0;
		
        sAtt = sAtt.toUpperCase();
        
        Iterator i = data.iterator();
        while(i.hasNext()){
            fobj = (FlexObject)i.next();
            debug("sAtt: " + sAtt + "     dimCol: " + fobj.getString(DIM_COL));
            if(!FormatHelper.hasContent(fobj.getString(DIM_COL)) || ((useSource && ((":SOURCE:" + sAtt).equals(fobj.getString(DIM_COL)) || ":SOURCE".equals(fobj.getString(DIM_COL)))) || (":" + sAtt).equals(fobj.getString(DIM_COL)))){
                
                filteredData.add(fobj);
            }
        }
        
        return filteredData;
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	protected Collection getMaterialColorForSize(Map mciMap, String tlId, String sizeAtt, String sizeVal){
        sizeAtt = sizeAtt.toUpperCase();
		debug("tlId: " + tlId + "   sizeAtt:" + sizeAtt + "    sizeVal:" + sizeVal + "     source: " + this.source);
        Collection matCol = new ArrayList();
        MaterialColorInfo mci = null;
      
        MaterialColorInfo topLevel = null;
        if(mciMap.keySet().contains(tlId)){   
            topLevel = (MaterialColorInfo)mciMap.get(mciMap);
        }
//SIZE1:33
        String dimKey = "";
        String tKey = "";
        Iterator keys = mciMap.keySet().iterator();
		
		//Find the lowest level dimension override (if source:size and 
		//using source, then take source:size rather than source)
		
		//Looking for Source:Size overrides if using source
        while(keys.hasNext()){
            tKey = (String)keys.next();
            if(FormatHelper.hasContent(this.source)){
                if(tKey.indexOf(this.source) > -1 && tKey.endsWith(sizeAtt + ":" + sizeVal)){
				   debug("in source size");
                    dimKey = tKey;
                    break;
                }
            }
            
        }
		//If didn't find source level, look for sizeatt + size val
        if(!FormatHelper.hasContent(dimKey)){
            keys = mciMap.keySet().iterator();
            while(keys.hasNext()){
                tKey = (String)keys.next();
				if(tKey.endsWith(sizeAtt + ":" + sizeVal)){
				   debug("in size");
				  dimKey = tKey;
				  break;
				}

            }
        }
		//If didn't find source:size or size and using source, get source override
        if(!FormatHelper.hasContent(dimKey)){
            keys = mciMap.keySet().iterator();
            while(keys.hasNext()){
                tKey = (String)keys.next();
                if(FormatHelper.hasContent(this.source)){
                    if(tKey.indexOf(this.source) > -1){
						debug("in source");

                        dimKey = tKey;
                        break;
                    }
                }

            }
        }
        debug("dimKey: " + dimKey);
		
        if(FormatHelper.hasContent(dimKey)){
            mci = (MaterialColorInfo)mciMap.get(dimKey);
        }
        if(mci == null && topLevel != null){
            mci = topLevel;
        }
        if(mci != null){
            matCol.add(mci);
        }
               
        return matCol;
    }
    
    @SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
	protected Map getMaterialColorsForBranch(Collection rows, String sizeAtt) throws WTException{
        debug("\n\ngetMaterialColorsForBranch");
        sizeAtt = sizeAtt.toUpperCase();
        
        Map materialColors = new HashMap();
        MaterialColorInfo mci = null;
        
        LCSFlexBOMQuery bq = new LCSFlexBOMQuery();
        
        FlexObject row = null;
        
        String materialId = "";
        String dimensionId = "";
        String dimName = "";
        
        
        Iterator i = rows.iterator();
        while(i.hasNext()){
            row = (FlexObject)i.next();
            materialId = row.getString("FLEXBOMLINK." + MATERIALREFID);
            dimensionId = row.getString(this.DIMID_COL);
            
            debug("dimensionId: " + dimensionId + "     materialId: " + materialId);
            
			//row has material set && the material is not the placeholder && I haven't found the dimension already
            if(FormatHelper.hasContent(materialId) && !(FormatHelper.getNumericFromOid(LCSMaterialQuery.PLACEHOLDERID).equals(materialId)) && materialColors.get(dimensionId) == null){
                dimName = row.getString("FLEXBOMLINK.DIMENSIONNAME");
                debug("\tdimName: " + dimName);
                debug("\tsizeAtt: " + sizeAtt);
                debug("\tthis.sources: " + this.getSources());
                if(!FormatHelper.hasContent(dimName) || dimName.indexOf(":" + sizeAtt) > -1 || (this.getSources() != null && this.getSources().size() > 0 && dimName.equals(":SOURCE"))){
                    mci = bq.bomInfoToMaterialColorInfo(row);
                    debug("\tnew MCI: " + mci);
                    materialColors.put(dimensionId, mci);
                }
                
            }
        }
        debug("end getMaterialColorsForBranch\n\n");
        return materialColors;
    }

    
    
    private void debug(String msg){
        if(DEBUG){
            LCSLog.debug(msg);
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
	    rowTotal =  matPrice *usageLB* (1  + COW/100 + TTW/100 + END/100) ; 

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
	 @SuppressWarnings("unused")
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
