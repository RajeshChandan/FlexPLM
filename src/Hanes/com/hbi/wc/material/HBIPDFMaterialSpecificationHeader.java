
package com.hbi.wc.material;

import com.lcs.wc.util.*;
import com.lcs.wc.document.*;
import com.lcs.wc.specification.*;
import com.lcs.wc.flextype.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.db.*;
import com.lcs.wc.client.web.*;
import com.lcs.wc.client.web.pdf.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.util.*;
import java.io.*;
import wt.util.*;
//import wt.part.WTPartMaster;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.product.PDFProductSpecificationHeader;

 /*******************************************************************************************************************************
  * HBIPDFMaterialSpecificationHeader.java
  *
  * This file used to generates the Header for a MaterialSpecification Report
  *
  */
 public class HBIPDFMaterialSpecificationHeader extends PdfPTable implements PDFHeader{
	    
     //private static String fontSize = "8";
     private static final String MATERIAL_HEADER_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.MaterialSpecPDF.InternalHeaderAttColumnsOrder");
	 private static final String MATERIAL_FINISHED_HEADER_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.MaterialSpecPDF.FabricFinishedColumnsOrder");
	 private static final String MATERIAL_GREIGE_HEADER_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.MaterialSpecPDF.FabricGreigeColumnsOrder");
     private static final String MATERIAL_EXT_HEADER_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.MaterialSpecPDF.ExternalHeaderAttColumnsOrder"); 
	 //added for ticket 141702-15
	 private static final String MATERIAL_EXT_HEADER_ATT_COLUMN_ORDER_GEN = LCSProperties.get("com.hbi.wc.material.MaterialSpecPDF.ExternalHeaderAttColumnsOrderGen");
	 public static String IS_GENERIC = "IS_GENERIC";
     public static String MATERIAL_MASTER_ID = "MATERIAL_MASTER_ID";
     static String IMAGE_URL = LCSProperties.get("com.lcs.wc.content.imageURL");
     public static final String webHomeLocation = LCSProperties.get("flexPLM.webHome.location");
     static String wthome = "";
     static String codebase = "";
     static String imageFile = "";
     static PDFGeneratorHelper pgh = new PDFGeneratorHelper();
     static float fixedHeight = 70.0F;
     static{
        try{
            imageFile = LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationHeader.headerImage", FormatHelper.formatOSFolderLocation(webHomeLocation) +"/images/flexplm_black.gif");
            wthome = WTProperties.getServerProperties().getProperty("wt.home");
            imageFile = wthome + File.separator + imageFile;
        }
        catch(Exception e){
            System.out.println("Error initializing cache for ExcelGeneratorHelper");
            e.printStackTrace();
        }
     }
       
     /** Creates a new instance of PDFProductSpecificationHeader
     */
     public HBIPDFMaterialSpecificationHeader() {
     }
    
     /** Constructor that specifies how many columns to have in the header
       * Call the super(int) which initializes the header as a PdfPTable
       * @param cols number columns will be in the header
     */    
     public HBIPDFMaterialSpecificationHeader(int cols) {
        super(cols);
		//super();
     }
       
     /** returns another instance of PDFMaterialSpecificationHeader with the table filled,
       * which can be added to a Document
       * @param params
       * @throws WTException
       * @return
      */    
     public Element getPDFHeader(Map params) throws WTException {
        
			PDFProductSpecificationHeader ppsh = new PDFProductSpecificationHeader(1);
			PdfPCell tableData = null;
			String strMatType = "";
			//System.out.println("inside header generateSpec params" + params);
			//generic = "NO";
			try{ 
			
               	 //WTPartMaster materialMaster = (WTPartMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
				LCSMaterialMaster materialMaster = (LCSMaterialMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
				 LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);	
				 strMatType = material.getFlexType().getFullNameDisplay(false);
			     ppsh.setWidthPercentage(95.0f);
                 float[] widths = {100.0f};
                 ppsh.setWidths(widths);
				 if(strMatType.equalsIgnoreCase("Fabric\\Finished") || strMatType.equalsIgnoreCase("Fabric\\Greige")) 
				 {						
					tableData = createInternalDataCell(params);				
				 }
				 else
				 {
					tableData = createExternalDataCell(params);
				 }
                 ppsh.addCell(tableData);	
				 return ppsh;
            }
            catch(Exception e){
             throw new WTException(e);
           }

		   
     } 
	 
	 

	 /** Creates product thumnail
	   * @param params
	   * @throws WTException
       * @return PdfPCell
     */
	 private PdfPCell createImageCellMaterialThumbnail(Map params) throws WTException{
          try{
				if(!FormatHelper.hasContent((String)params.get(MATERIAL_MASTER_ID))){
					throw new WTException("Can not create PDFProductSpecificationHeader without MATERIAL_MASTER_ID");
				}
				//WTPartMaster materialMaster = (WTPartMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
				LCSMaterialMaster materialMaster = (LCSMaterialMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
				LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);
				String fileSeperator = FileLocation.fileSeperator;
				String materialThumbnail = "";
				String imageNotAvailable = FileLocation.imageNotAvailable;
	     try{
		    
//			materialThumbnail = material.getPartPrimaryImageURL();
	    	materialThumbnail = material.getPrimaryImageURL();
	    	 
	 		if(FormatHelper.hasContent(materialThumbnail)) {
                // Trim off the leading /LCSWImages/
                materialThumbnail = FileLocation.imageLocation + FileLocation.fileSeperator + materialThumbnail.substring((IMAGE_URL.length() + 1));
            }else{
               materialThumbnail = imageNotAvailable;
            }
	     }
	     catch(Exception e){
		    e.printStackTrace();
	     }
            Image img = Image.getInstance(materialThumbnail);
            PdfPCell cell = new PdfPCell(img, true);
            cell.setBorderWidth(0.0f);
			cell.setPadding(4F);
            cell.setFixedHeight(45.0f);
			cell.setHorizontalAlignment (Element.ALIGN_MIDDLE);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);   
            return cell;
        }
        catch(Exception e){
            throw new WTException(e);
         }
	}
    
    /** gets the Height for this header
     * @return
     */    
    public float getHeight(){
        return fixedHeight;
    }

	/** Creates External material header section table
	  * @param params
      * @throws WTException
      * @return PdfPCelll
    */
	private PdfPCell createExternalDataCell(Map params) throws WTException{
        try{
 			 Collection materialHeaderAtt = new ArrayList();
			 TableColumn column = null;
			 //String displayValue = "";
			 PdfPCell cell = new PdfPCell();
			 PdfPTable mainTable = new PdfPTable(1);
			 float [] colWidths = {80.0F, 20.0F};
			 PdfPTable headerTable = new PdfPTable(colWidths);
	         //int tableCells  = 4;
			 float [] hWidths = {15.0f,25.f,18.0f,22.0f};
			 PdfPTable table = new PdfPTable(hWidths);
			 table.getDefaultCell().setBorder(0); 
			
			 //WTPartMaster materialMaster = (WTPartMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
			 LCSMaterialMaster materialMaster = (LCSMaterialMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
			 LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);
			 FlexType materialType = material.getFlexType();
			 //System.out.println("before generic params " + params);
			 //added for ticket 141702-15
			 String is_Generic = (String)params.get(IS_GENERIC);
			 StringTokenizer parser = null;
			 if("true".equalsIgnoreCase(is_Generic))
				parser = new StringTokenizer(MATERIAL_EXT_HEADER_ATT_COLUMN_ORDER_GEN, ",");
			 else 
				parser = new StringTokenizer(MATERIAL_EXT_HEADER_ATT_COLUMN_ORDER, ",");
			//ended for ticket 141702-15
             Collection labelColumns = getColumns(parser, materialType);

		     Iterator lblColsIter = labelColumns.iterator();
			 
			 StringTokenizer attParser = null;
			 if("true".equalsIgnoreCase(is_Generic))
				attParser = new StringTokenizer(MATERIAL_EXT_HEADER_ATT_COLUMN_ORDER_GEN, ",");
			 else
				attParser = new StringTokenizer(MATERIAL_EXT_HEADER_ATT_COLUMN_ORDER, ",");
			
			 while(attParser.hasMoreTokens()){
    			String attkey = attParser.nextToken().trim();
				materialHeaderAtt.add(attkey);
			 }

			 SearchResults results = new HBIMaterialSupplierQuery().findMaterialSupplierAttributes(params, materialType, materialHeaderAtt, null);
 		     Collection rawDataCollection =results.getResults();
			 Iterator rawDataIter = rawDataCollection.iterator();
			 TableData td =(TableData)rawDataIter.next();
			 td.setData("WTTYPEDEFINITION.NAME", materialType.getFullNameDisplay(false));
			 //add a Space
			// addSpaceBetweenRows(headerTable, 2);
			 while(lblColsIter.hasNext()) 
			 {
				column = (TableColumn)lblColsIter.next();
				table.addCell(createLabelCell(column.getHeaderLabel()));
				if("LCSMATERIAL.CREATESTAMPA2".equalsIgnoreCase(column.getTableIndex()) ){
					table.addCell(createDataCell(FormatHelper.formatDateString(column.getPDFDisplayValue(td))));
				}else if("LCSMATERIAL.MODIFYSTAMPA2".equalsIgnoreCase(column.getTableIndex())){
					PdfPCell scell = createDataCell(FormatHelper.formatDateString(column.getPDFDisplayValue(td)));
					scell.setColspan(4);
					scell.setBorder(0);
					table.addCell(scell);
				}else{
					table.addCell(createDataCell(column.getPDFDisplayValue(td)));	
				}
			 }

			 cell = new PdfPCell(table);
			 cell.setBorder(0);
			 headerTable.addCell(cell);

			 cell = createImageCellMaterialThumbnail(params);
			 cell.setBorder(0);
			 headerTable.addCell(cell);

			 //add a Space	
			 addSpaceBetweenRows(headerTable, 2);

			 cell = new PdfPCell(headerTable);
			 cell.setBorder(0);
           	 mainTable.addCell(cell);
						
			 PdfPCell tableCell = new PdfPCell(mainTable);
    		 cell.setBorder(0);
		     tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
             tableCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			 return tableCell; 
		
        }
        catch(Exception e){
            throw new WTException(e);
        }
    }

    
    /** Creates internal material header section table
	  * @param map
      * @throws WTException
      * @return PdfPCelll
      */
	private PdfPCell createInternalDataCell(Map params) throws WTException{
        try{
            
 			 Collection materialHeaderAtt = new ArrayList();
			 TableColumn column = null;
			 PdfPCell cell = new PdfPCell();
			 PdfPTable mainTable = new PdfPTable(1);
             float[] widths = {12.0f,20.0f,12.0f,20.0f,12.0f,20.0f};
			 PdfPTable headerTable = new PdfPTable(widths);
			
			 //WTPartMaster materialMaster = (WTPartMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
			 LCSMaterialMaster materialMaster = (LCSMaterialMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
			 LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);
			 FlexType materialType = material.getFlexType();
			 StringTokenizer parser = new StringTokenizer(MATERIAL_HEADER_ATT_COLUMN_ORDER, ",");
			 StringTokenizer attParser = new StringTokenizer(MATERIAL_HEADER_ATT_COLUMN_ORDER, ",");
			 if("Fabric\\Greige".equalsIgnoreCase(materialType.getFullName())){
				parser = new StringTokenizer(MATERIAL_GREIGE_HEADER_ATT_COLUMN_ORDER, ",");
				attParser = new StringTokenizer(MATERIAL_GREIGE_HEADER_ATT_COLUMN_ORDER, ",");
			 } else if ("Fabric\\Finished".equalsIgnoreCase(materialType.getFullName())){
				parser = new StringTokenizer(MATERIAL_FINISHED_HEADER_ATT_COLUMN_ORDER, ",");
				attParser = new StringTokenizer(MATERIAL_FINISHED_HEADER_ATT_COLUMN_ORDER, ",");			 
			 }
             Collection labelColumns = getColumns(parser, materialType);
		     Iterator lblColsIter = labelColumns.iterator();
	 
			while(attParser.hasMoreTokens()){
    			String attkey = attParser.nextToken();
				materialHeaderAtt.add(attkey);

			 }
			 SearchResults results = new HBIMaterialSupplierQuery().findMaterialSupplierAttributes(params, materialType, materialHeaderAtt, null);
 		     Collection rawDataCollection =results.getResults();
		     Iterator rawDataIter = rawDataCollection.iterator();
			 TableData td =(TableData)rawDataIter.next();
			 //setting Display name for material type
			 td.setData("WTTYPEDEFINITION.NAME", materialType.getFullNameDisplay(false));
			 while(lblColsIter.hasNext()) {
				column = (TableColumn)lblColsIter.next();
				headerTable.addCell(createLabelCell(column.getHeaderLabel()));
				if("LCSMATERIAL.CREATESTAMPA2".equalsIgnoreCase(column.getTableIndex()) || "LCSMATERIAL.MODIFYSTAMPA2".equalsIgnoreCase(column.getTableIndex())){
					headerTable.addCell(createDataCell(FormatHelper.formatDateString(column.getPDFDisplayValue(td))));
				}else{
					headerTable.addCell(createDataCell(column.getPDFDisplayValue(td)));	
				}

			 }
			 cell = new PdfPCell(headerTable);
			 cell.setBorder(0);
           	 mainTable.addCell(cell);
					
			 PdfPCell tableCell = new PdfPCell(mainTable);
    		 cell.setBorder(0);
		     tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
             tableCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			 return tableCell; 
		
        }
        catch(Exception e){
            throw new WTException(e);
        }
    }

	/** gets columns for given key from custom.lcs.properties file for material header section label table
	  * @param parser
	  * @param materialType
      * @throws WTException
      * @return PdfPCelll
      */
	public Collection getColumns(StringTokenizer attParser, FlexType materialType) throws WTException {

		    Collection columns = new Vector();
			TableColumn column = null;
			FlexTypeAttribute att = null;
			//FlexTypeGenerator flexg = null;
			FlexTypeGenerator flexg = new FlexTypeGenerator();
			FlexTypeGenerator flexg1 = new FlexTypeGenerator();
			while(attParser.hasMoreTokens()){
				flexg.setScope("MATERIAL");
				flexg.setLevel(null);
			String attName = attParser.nextToken().trim();
				if("LCSMATERIAL.CREATESTAMPA2".equalsIgnoreCase(attName) ){
					column = new TableColumn();
					column.setTableIndex("LCSMATERIAL.CREATESTAMPA2");
					column.setHeaderLabel("Issue Date");
					columns.add(column);
				}else if("LCSMATERIAL.MODIFYSTAMPA2".equalsIgnoreCase(attName)){
					column = new TableColumn();
					column.setTableIndex("LCSMATERIAL.MODIFYSTAMPA2");
					column.setHeaderLabel("Revised Date");
					columns.add(column);
				}else if ("FLEXTYPE.TYPENAME".equalsIgnoreCase(attName)){
					column = new TableColumn();
					//column.setTableIndex("FLEXTYPE.TYPENAME");
					column.setTableIndex("WTTYPEDEFINITION.NAME");
					column.setHeaderLabel("Material Type");
					columns.add(column);
				}else if ("LCSSUPPLIERMASTER.SUPPLIERNAME".equalsIgnoreCase(attName)) {
					column = new TableColumn();
					column.setTableIndex("LCSSUPPLIERMASTER.SUPPLIERNAME");
					column.setHeaderLabel("Vendor Name");
					columns.add(column);					
				}
                else{
					try{
						att = materialType.getAttribute(attName);
						if("MATERIAL-SUPPLIER".equalsIgnoreCase(att.getAttScope())){
							flexg1.setScope("MATERIAL-SUPPLIER");
							flexg1.setLevel(null);
							att = materialType.getAttribute(attName);
							column = flexg1.createTableColumn(att, materialType, false);
							columns.add(column);
						}else{
							column = flexg.createTableColumn(att, materialType, false);
							columns.add(column);
						}

					}catch(WTException et){
						throw new WTException(et);
					}
				 }
    		}

			return columns;
     }

	/**returns the PdfPCell containing header label for  header table for the specification
      * @param label
      * @return  
	  */ 
	private PdfPCell createLabelCell(String label){
	    
		//Font font = pgh.getCellFont("RPT_HEADER","Left", fontSize);
		Font font = pgh.getCellFont("RPT_HEADER","Left", null);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(label, font));
		pdfpcell.setBorder(0);
	    //pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_HEADER", null));           
	    return pdfpcell;
    }

	/* *returns the PdfPCell containing header dispaly value for  header table for the specification
     * @param label
     * @return  
	 */
	private PdfPCell createDataCell(String label){
	    
		//Font font = pgh.getCellFont("DISPLAYTEXT","Left", fontSize);
		Font font = pgh.getCellFont("DISPLAYTEXT","Left", null);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(label, font));
		pdfpcell.setBorder(0);
	    //pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_HEADER", null));           
	    return pdfpcell;
    }

	private void addSpaceBetweenRows(PdfPTable table, int colNumber) throws WTException
    {		
		PdfPCell spacerCell = new PdfPCell(pgh.multiFontPara(" "));
		spacerCell.setColspan(colNumber);
		spacerCell.setFixedHeight(3.0F); 
		spacerCell.setBorder(0);
		table.addCell(spacerCell);
    } 

			 
}
