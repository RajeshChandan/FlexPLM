/*
 * This is a custom Excel Generator class used to Rename the Generated PDF Report Name as per Hanes Brands Request Done on 02 August 2015 by BSC
 */

package com.hbi.wc.client.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFSimpleShape;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import wt.util.WTMessage;
import wt.util.WTProperties;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.client.web.CareWashTableColumn;
import com.lcs.wc.client.web.ExcelGeneratorHelper;
import com.lcs.wc.client.web.ExcelTableHeaderGenerator;
import com.lcs.wc.client.web.TableColumn;
import com.lcs.wc.client.web.TableData;
import com.lcs.wc.client.web.TableGenerator;
import com.lcs.wc.client.web.TableSectionHeader;
import com.lcs.wc.client.web.UserTableColumn;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flexbom.BOMColorTableColumn;
import com.lcs.wc.planning.ProductPlanTableColumn;
import com.lcs.wc.util.FileLocation;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.RB;
import com.lcs.wc.util.SortHelper;

public class HBIExcelGenerator extends TableGenerator{
     
    ////////////////////////////////////////////////////////////////////////////
    protected String reportName;
    static String downloadLocation = "";
    protected static String wt_home = "";
    
    static String  tempDownloadLocation = "";
    protected ClientContext context;
    
    
    protected HSSFWorkbook wb;
    protected HSSFSheet ws;
    protected HSSFDataFormat df;
    protected ExcelGeneratorHelper egh;
    protected HSSFPrintSetup ps;
    //Enable when POI supports more Image Formats...
    protected static HSSFPatriarch patriarch;
    
    boolean showSubTotals;
    boolean showTotals;
    boolean indentSections;
    boolean spaceBetweenGroups;

    protected String filename;
    
    public String tableCellClass = "";
    public String fontSize = "8";
    
    public ExcelTableHeaderGenerator ethg;
    
    //Print options
    protected boolean landscape = false;
    protected short paperSize = 0;
    protected int pageWidth = 0;
    protected int pageHeight = 0;
    
    //Paper size options
    public static final short LETTER_PAPERSIZE = 1;
    public static final short LEGAL_PAPERSIZE = 5;
    public static final short EXECUTIVE_PAPERSIZE = 7;
    public static final short A4_PAPERSIZE = 9;
    public static final short A5_PAPERSIZE = 11;
    public static final short ENVELOPE_10_PAPERSIZE = 20;
    public static final short ENVELOPE_DL_PAPERSIZE = 27;
    public static final short ENVELOPE_CS_PAPERSIZE = 28;
    public static final short ENVELOPE_MONARCH_PAPERSIZE = 37;
    private static final boolean DEBUG = LCSProperties.getBoolean("com.lcs.wc.client.web.CSVExcelGenerator.verbose");
    private static final int DEBUG_LEVEL = Integer.parseInt(LCSProperties.get("com.lcs.wc.client.web.CSVExcelGenerator.verboseLevel", "1"));
    public static final String defaultCharsetEncoding = LCSProperties.get("com.lcs.wc.util.CharsetFilter.Charset","UTF-8");
    public static final String imagefilePath = FileLocation.imageLocation;
	private static final int preferedImageColumnWidth = Integer.parseInt(LCSProperties.get("com.lcs.wc.client.web.ExcelGenerator.imageColumnWidth", "2000"));
	private static int preferedBOMColorColumnWidth = Integer.parseInt(LCSProperties.get("com.lcs.wc.client.web.ExcelGenerator.bomColorColumnWidth", "3000"));
	private static int preferedBOMColorThumbnailWidth = Integer.parseInt(LCSProperties.get("com.lcs.wc.client.web.ExcelGenerator.bomColorThumbnailWidth", "1200"));
	
	int rowcount = 0;
    
    public int getRowcount() {
		return rowcount;
	}

	public void setRowcount(int rowcount) {
		this.rowcount = rowcount;
	}
	static{
        try{
            tempDownloadLocation = FormatHelper.formatOSFolderLocation(LCSProperties.get("com.lcs.wc.client.web.ExcelGenerator.exportLocation"));
            
            WTProperties properties = WTProperties.getLocalProperties();
            wt_home =  properties.getProperty("wt.home");

            downloadLocation = wt_home + tempDownloadLocation;
            
        }
        catch(Exception e){}
    }
    
    ////////////////////////////////////////////////////////////////////////////
    /**
     *  (Full Control) Method that Draws the Table.
     *  This methods calls the full control drawTable method with the
     *  sorts via the HeaderLink
     *
     *  @param data a <code>Collection</code> of FlexObjects
     *  @param columns a <code>Collection</code> of TableColumn objects
     *  @param title a <code>String</code> The title of the Table to be rendered
     *  @param sortable a <code>String</code> Whether or not the columns are sortable
     *  @return  a <code>boolean</code> consisting of the html tag code required to render the table
     *
     *  @see com.lcs.wc.db.FlexObject
     *  @see com.lcs.wc.client.web.TableColumn
     **/
    public String drawTable(Collection data, Collection columns, ClientContext context, String reportName){
        this.context = context;
        this.reportName = reportName;
        return this.drawTable(data, columns);
    }
    
    public String drawTable(Collection data, Collection columns){
        //Need to create the Excel file
        this.intializeWorksheet();
        this.setTableId();
        this.data = data;
        this.columns = columns;
        
        String tid = this.getTableId();
        
        Iterator<?> tci = this.columns.iterator();
        while(tci.hasNext()){
            ((TableColumn)tci.next()).setFormatHTML(false);
        }
        if(this.groupByColumns != null){
            tci = this.groupByColumns.iterator();
            while(tci.hasNext()){
                ((TableColumn)tci.next()).setFormatHTML(false);
            }        
        }
        
        /////////////////////////////////////////
        // SET ROW ID INDEX ON ALL COLUMNS
        /////////////////////////////////////////
        if(FormatHelper.hasContent(this.rowIdIndex) &&
        this.columns != null && this.columns.size() > 0){
            
            Iterator<?> i = this.columns.iterator();
            while(i.hasNext()){
                ((TableColumn)i.next()).setRowIdIndex(this.rowIdIndex);
            }
        }
        
        this.drawHeader();
        
        this.drawContentColumnHeaders("T"+tid);
        
        this.ws.createFreezePane(0, this.rowcount);
        debug("---before drawContentData()");
        this.drawContentData();
        debug("---after drawContentData()");
        return printFile();
    }
    
    public void drawHeader(){
        if(this.ethg != null){
            //this.ethg.setLabel(this.reportName);
            this.rowcount = this.ethg.createHeader(this.wb, this.ws, this.columns.size());
        }
    }
    
    /**
     *  This method draws the text column headers for each column.
     *  The format of the Column header Content is specified by the
     *  html style class 'TABLESUBHEADER'. The following class properties
     *  control how the headers are rendered.
     *<ul> This method accesses the following TableGenerator class properties:
     * <li>&nbsp; this.wrapColumnHeaders - default = false
     * <li>&nbsp; this.sortable - default = false
     *</ul>
     *<ul> This method accesses the following TableColumn class properties:
     * <li>&nbsp; TableColumn.displayed
     * <li>&nbsp; TableColumn.columnWidth , if not null
     * <li>&nbsp; TableColumn.headerAlign
     * <li>&nbsp; TableColumn.headerLink , if has content
     * <li>&nbsp; TableColumn.headerLabel
     *</ul>
     *
     *  @return  a <code>String</code> representing the html tag code
     *  @see com.lcs.wc.client.web.TableColumn
     **/
    public String drawContentColumnHeaders(String tid){
        
        HSSFRow row = this.ws.createRow(rowcount);
        rowcount++;
        HSSFCell cell;
        int cellcount = 0;
        HSSFCellStyle cellstyle;
        if(indentSections && FormatHelper.hasContent(this.groupIndex)){
            cell = row.createCell(cellcount);
            cell.setCellValue("");
            cellcount++;
        }
        
        if(this.columns != null){
            Iterator<?> i = this.columns.iterator();
            while(i.hasNext()){
                TableColumn column = (TableColumn) i.next();
                if(column.isDisplayed()){
                    cell = row.createCell(cellcount);
                    cellstyle = egh.getCellStyle(this.tableSubHeaderClass, column.getAlign(), fontSize, HSSFDataFormat.getBuiltinFormat("text"), column.isExcelHeaderWrapping());
                    int width =column.getExcelMinCharWidth(); 
					//FX22 work, POI latest no longer needs encoding set, handles it on its own now so api was removed
                    //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell.setCellStyle(cellstyle);
                    cell.setCellValue(column.getHeaderLabel());
                    if(FormatHelper.hasContent(column.getHeaderLabel())){
	                    String[] header = column.getHeaderLabel().split("\\s");
	                    for(String h : header){
	                    	if(h.length() > width){
	                    		width = h.length();
	                    	}
	                    }
                    } 

                    if(column.isImage()){
                    	this.ws.setColumnWidth(cell.getColumnIndex(), preferedImageColumnWidth);
                    } else {
                	if(column.isExcelColumnWidthAutoFitContent()) {
                		this.ws.autoSizeColumn(cell.getColumnIndex());
                	} else {
	                    	//8.43 is default column size
	                    	if(width>8.43)
	                    		this.ws.setColumnWidth(cell.getColumnIndex(), (int)(width*256*1.1));
                	}
                    }
                    cellcount++;
                }
            }
            
            // FOR UNDEFINED PRESENTATION
        } else if(this.data.size() > 0){
            Object o = this.data.iterator().next();
            if(o instanceof FlexObject){
                FlexObject flex = (FlexObject) o;
                Iterator<?> e = flex.keySet().iterator();
                while(e.hasNext()){
                    String key = (String) e.next();
                    cell = row.createCell(cellcount);
                    cellstyle = egh.getCellStyle(this.tableSubHeaderClass, "left", fontSize);
					//FX22 work, POI latest no longer needs encoding set, handles it on its own now so api was removed
                    //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell.setCellStyle(cellstyle);
                    cell.setCellValue(key);
                    cellcount++;
                }
            }
        }
        return "";
    }
    ////////////////////////////////////////////////////////////////////////////
    /**
     *  This method draws the text content for each column.
     *  The following class properties control how the content is rendered in the
     *  columns.
     *
     *<ul> <B>This method accesses the following TableColumn class properties:</B>
     * <li>&nbsp; TableColumn.size
     * <li>&nbsp; TableColumn.displayed
     *</ul>
     *
     *  This method calls {@link TableColumn#drawCell} to render the cell content.
     *  <p> Refer to the drawCell method to understand the properties list that
     *  is required to be set before the table can be properly generated.
     *
     *  @return  a <code>String</code> representing the html tag code
     *  @see com.lcs.wc.client.web.TableColumn
     **/
    public String drawContentData(){
        String group = "";
        HSSFCellStyle cellstyle;
        
        if(this.getGroupByColumns() == null || this.getGroupByColumns().isEmpty()){
            if(this.groups == null){
                this.groups = this.deriveGroupsList();
                this.groups.add("");
            }
            
            Iterator<?> allGroups = groups.iterator();
            Collection groupRows;
            debug(2, "--drawContentDAta()-before while");
            while(allGroups.hasNext()){
                group = (String) allGroups.next();
				//if(DEBUG){ System.out.println("group.toString()" + group.toString());}
                groupRows = this.getGroupRows(group);
                
                if(groupRows.size() > 0 && (groups.size() > 1 || (groups.size() == 1 && !"".equals(groups.iterator().next().toString())))){
                    String groupLabel = group;
                    if(this.groupColumn != null){
                        groupLabel = this.groupColumn.getLocalizedData(group);
                        groupLabel = FormatHelper.applyFormat(groupLabel, groupColumn.getFormat());
                    }
                    
                    if(!FormatHelper.hasContent(groupLabel)){
                        groupLabel = WTMessage.getLocalizedMessage (RB.MAIN, "empty_LBL", RB.objA ) ;
                    }
                    
                    
                    if(this.columns != null){
                        HSSFRow row = this.ws.createRow(rowcount);
                        rowcount++;
                        HSSFCell cell = row.createCell(0);
                        cellstyle = egh.getCellStyle(this.tableSectionHeaderClass, "left", fontSize);
                        //FX22 work, POI latest no longer needs encoding set, handles it on its own now so api was removed
						//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        cell.setCellStyle(cellstyle);
                        cell.setCellValue(groupLabel);
                    }
                }
                groupFamily.add(group);
                
                drawRows(groupRows);
                
                if (groupRows.size() > 0){
	                // DRAW SUB-TOTALS ROW
	                if (this.showSubTotals && groupRows.size() > 0) {
                            this.deriveTotals(group, true);
	                    this.drawTotal(group, true);
	                }
	                if (this.isShowDiscreteRows()){
	                	this.drawCounts(group, true);
	                }
	                if (this.showSubTotals || this.isShowDiscreteRows()){	                    
	                    this.groupSubTotals.put(group, new Hashtable());
	                    this.groupDiscreteCounts.put(group, new Hashtable());
	                }
	                if (!this.showSubTotals && !this.isShowDiscreteRows() && allGroups.hasNext()) {
	                    
	                    // IF ANOTHER GROUP IS GOING TO BE PRINTED
	                    // THEN PUT IN A SEPERATOR
	                    if (this.spaceBetweenGroups) {
	                        HSSFRow row = this.ws.createRow(rowcount);
	                        rowcount++;
	                    }
	                }
                }
            } // END HAS MORE GROUPS:
        }
        else{
            this.drawMultiGroupByTable();
        }
        // DRAW TOTALS ROW
        if (this.showTotals) {
            this.deriveTotals(null, false);
            this.drawTotal(group, false);
            this.rowcount++;
        }
        // DRAW COUNT ROW
        if (this.showDiscreteRows) {
            this.drawCounts(group, false);
        }                
        return "";
    }
    ////////////////////////////////////////////////////////////////////////////
    public String drawMultiGroupByTable(){
        this.populateGroupByGroups();
        if(this.groupByGroups != null){
            drawMultiGroupByTable(new Vector(this.getGroupByColumns()), this.data, 0);
        }
        return "";
    }
    ////////////////////////////////////////////////////////////////////////////
    public String drawMultiGroupByTable(Vector tcgroups, Collection rowData, int level){
        
        //Added the following check against rowData.  If rowData is empty, then
        //just return and do not continue, since continuing will just allow this
        //method to recursively call itself again, with empty data.
        if (rowData.size() == 0)
            return "";
        //return buffer.toString();
        
        HSSFCellStyle cellstyle;
        //Get the TableColumn for the particular level in tcgroups (this should occur in numeric order, from 0...)
        TableColumn column = (TableColumn)tcgroups.elementAt(level);
        this.currentColumn = column;
        String index = column.getTableIndex();
        //Get all of the possible values for that group
        Vector groupVals = (Vector)this.groupByGroups.get(index);
        if(FormatHelper.CURRENCY_FORMAT.equals(column.getFormat())){
            groupVals = new Vector(SortHelper.sortCurrencies(groupVals, false));
        }
        else if(FormatHelper.FLOAT_FORMAT.equals(column.getFormat()) ||
        FormatHelper.DOUBLE_FORMAT.equals(column.getFormat())){
            
            groupVals = new Vector(SortHelper.sortStringsAsDoubles(groupVals, false));
        }
        else if(FormatHelper.INT_FORMAT.equals(column.getFormat()) ||
        FormatHelper.LONG_FORMAT.equals(column.getFormat())){
            
            groupVals = new Vector(SortHelper.sortStringsAsIntegers(groupVals, false));
        }
        //else if(column.getLinkMethod() != null){
        //    groupVals = new Vector(SortHelper.sortLinksByDisplay(groupVals, true));
        //}
        else{
            // SHOULD BE SORTING BY VARIOUS MODES. IF GROUP IS BACKED BY AN
            // ATTRIBUTE VALUE LIST, THEN YOU SHOULD BE ABLE TO SORT BY THE
            // SORT ORDER.
            if(column.getList() != null && !"moaList".equals(column.getAttributeType()) && !"composite".equals(column.getAttributeType())){
                // SORT BY ORDERING OF ATTRIBUTES
                try {
                    //System.out.println("groupVals = " + groupVals);
                    
                    // All keys regardless of whether they are currently selectable should be included
                    // They may have been selectable at one time so must include
                    Iterator sortedKeyIter = column.getList().getOrderedKeys(null, false).iterator();

                    HashSet<String> newGroupVals = new LinkedHashSet<String>();
                    String key;
                    while(sortedKeyIter.hasNext()){
                        key = (String) sortedKeyIter.next();
						if(FormatHelper.hasContent(column.getAttValListDisplay())){
							key = column.getList().get(key, column.getAttValListDisplay());
						}
						else{
							key = column.getList().getValue(key, ClientContext.getContext().getLocale());
						}
                        //System.out.println("sorting key = " + key);
                        if(groupVals.contains(key)){
                            newGroupVals.add(key);
                        }
                    }
					if(groupVals.contains("") && !newGroupVals.contains("")){
						newGroupVals.add("");
					}
					groupVals = new Vector();
                    groupVals.addAll(newGroupVals);
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                Collections.sort(groupVals);
            }
        }
        //System.out.println("--groupVals from groupByGroups to sort on: "+groupVals);
        //groupVals.add("");
        Iterator<?> it = groupVals.iterator();
        String val;
        Collection tempData;
        //Loop thru groupVals, ie, the possible values for the particular sort group
        while(it.hasNext()){
            tempData = new Vector(rowData);
            val = (String)it.next();
            //For each element of groupVals (ie, 1 possible value in that sort group)
            //add the element to groupFamily
            this.groupFamily.add(val);
            //Now only get the records where the record has a value that matches the element val
            //tempData = this.getGroupRows(tempData, index, val);
            tempData = this.getGroupRows(tempData, column, val);            

            if(this.columns != null && tempData.size() > 0 && column.isShowGroupByHeader()){
                if(!FormatHelper.hasContent(val)){
                    HSSFRow row = this.ws.createRow(rowcount);
                    rowcount++;
                    HSSFCell cell = row.createCell(0);
                    cellstyle = egh.getCellStyle(this.tableSectionHeaderClass, "left", fontSize);
					//FX22 work, POI latest no longer needs encoding set, handles it on its own now so api was removed
                    //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell.setCellStyle(cellstyle);
                    cell.setCellValue(getIndents(level) + WTMessage.getLocalizedMessage ( RB.MAIN, "empty_LBL", RB.objA ) );
                }
                else{
                    //String groupDisplay = column.getLocalizedData(val, true);
                    String groupDisplay = null;
                    if (column instanceof UserTableColumn && FormatHelper.hasContent(val)) {
                    	FlexObject data = (FlexObject)((Vector)tempData).elementAt(0);
                    	groupDisplay = ((UserTableColumn)column).getDisplayValue2(data);
                    } else {
                    	groupDisplay = val;
                    }
                    HSSFRow row = this.ws.createRow(rowcount);
                    rowcount++;
                    HSSFCell cell = row.createCell(0);
                    cellstyle = egh.getCellStyle(this.tableSectionHeaderClass, "left", fontSize);
					//FX22 work, POI latest no longer needs encoding set, handles it on its own now so api was removed
                    //cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell.setCellStyle(cellstyle);
                    cell.setCellValue(getIndents(level) + groupDisplay);
                }
            }
            
            if(level == tcgroups.size() - 1){
                this.drawRows(tempData);
            } else{
                drawMultiGroupByTable(tcgroups, tempData, level + 1);
            }
            
            if(tempData.size() > 0){
                if (this.showSubTotals) {
                    if(column.isShowGroupSubTotal()){
                        this.deriveTotals(val, true);
                        this.drawTotal(val, true);
                    }
                }
                if (this.showDiscreteRows){
                	this.drawCounts(val, true);
                }
                if (this.showSubTotals || this.showDiscreteRows) {
                    this.groupSubTotals.put(val, new Hashtable());
                    this.groupDiscreteCounts.put(val, new Hashtable());
                }
                if (!this.showSubTotals && !this.showDiscreteRows && it.hasNext()) {
                    // IF ANOTHER GROUP IS GOING TO BE PRINTED
                    // THEN PUT IN A SEPERATOR
                    if (this.spaceBetweenGroups) {
                        HSSFRow row = this.ws.createRow(rowcount);
                        rowcount++;
                    }
                }
                
            }
            this.groupFamily.remove(val);
        }
        
        
        return "";
    }
    ////////////////////////////////////////////////////////////////////////////
    protected String getIndents(int indents){
        String indent = "    ";
        String totalIndent = "";
        for(int i = 0; i < indents; i++){
            totalIndent = totalIndent + indent;
        }
        return totalIndent;
    }
    ////////////////////////////////////////////////////////////////////////////
    private String getFloatFormat(TableColumn column){
        //.getDecimalPrecision()
        String stub = "#,##0";
        int prec = column.getDecimalPrecision();
        if(prec > 0){
            stub = stub + ".";
            for(int i = 0; i < prec; i++){
                stub = stub + "0";
            }
        }
        return stub;
    }
    
    public String drawRows(Collection groupRows){
        Iterator<?> rows = groupRows.iterator();
        Iterator<?> columnsIt = null;
        TableColumn column = null;
        Iterator<?> keys;
        HSSFRow row;
        HSSFCell cell;
        HSSFCellStyle cellstyle;
        short cellFormat = HSSFDataFormat.getBuiltinFormat("text");
        debug(2, "--drawRows(Collection)- ");
        boolean showColumn = true;
		String tableName;
		String uniqueColumn;        
        boolean firstRow = true;
        while(rows.hasNext()){
            row = this.ws.createRow(rowcount);
            rowcount++;
            
            Object obj = rows.next();
            debug(4, "---Row:  " + obj);
            if(obj instanceof TableSectionHeader){
                
                TableSectionHeader header = (TableSectionHeader) obj;
                this.drawSectionHeader(header.getLabel());
                
                continue;
            }
            
            TableData td = (TableData) obj;
            
            int cellcount = 0;
            if(this.columns != null){
                
                columnsIt = this.columns.iterator();
                while(columnsIt.hasNext()){
                	showColumn = true;
                    column = (TableColumn) columnsIt.next();

                    // Determine if the column definition is overridden based on show Criteria
                    if (column.isShowCriteriaOverride()) {
                        column = column.getOverrideColumn(td);
                    }

                    debug(4, "column :  " + column);

                    if(column.isByGroup() && !firstRow){
                        td.setData(column.getTableIndex(), "");
                    }
                    if (!showRepeatedData) {
                    	String tableIndex = column.getTableIndex();
						if(tableIndex!=null && tableIndex.indexOf(".") > -1){
							tableName = tableIndex.substring(0, tableIndex.indexOf(".")).toUpperCase();
							uniqueColumn = (String)repeatedDataColumnMap.get(tableName);

							if(uniqueColumn!=null && td.getData(uniqueColumn)!=null){
								if(repeatedDataIds.get(uniqueColumn) != null && repeatedDataIds.get(uniqueColumn).equals(td.getData(uniqueColumn))){
									showColumn = false;	
								}
							}
						}                    	
                    }
                    if (column.isDisplayed()) {
                        if (column.showCell(td)){
                            cell = row.createCell(cellcount);

                            // jc - Make sure we have the correct override column definition
                            column = column.getOverrideColumn(td);

                            //if("currency".equals(column.getAttributeType())){
                            //    cell.setCellStyle(ExcelGeneratorHelper.getCellStyleCurrency("", this.wb));
                            //}
                            String styleClass = this.getColumnStyleClass(td, column, this.getCellClass(this.darkRow));
                            debug(3, "---columnDisplayed, column.showcell--" + column.getAttributeType());
                            cellFormat  = HSSFDataFormat.getBuiltinFormat("text");
                            if( "float".equals(column.getAttributeType()) || "currency".equals(column.getAttributeType()) || "constant".equals(column.getAttributeType()) ){
                            	cellFormat = df.getFormat(getFloatFormat(column));
                            } else if ( "integer".equals(column.getAttributeType()) ) {
                            	cellFormat =df.getFormat("#,##0");
                            } else if ( "date".equals(column.getAttributeType()) ) {
                            	cellFormat =df.getFormat("m/d/yy");
                            }
                            
                            cellstyle = egh.getCellStyle(styleClass, column.getAlign(), fontSize, cellFormat, column.isExcelWrapping());
                            cell.setCellStyle(cellstyle);
                            if(column.getExcelMinCharWidth()>0)
                            this.ws.setColumnWidth(cell.getColumnIndex(), column.getExcelMinCharWidth()*256);
                            
                            //System.out.println((short)(column.getExcelMinCharWidth()*256));

                            debug(3, "---another columnDisplayed, column.showcell--");
           					//FX22 work, POI latest no longer needs encoding set, handles it on its own now so api was removed
							//cell.setEncoding(HSSFCell.ENCODING_UTF_16);


                            //Because of some weird formatting issue, the cell value may not match the column type
                            //We may display a character instead of what is expected. 
                            //It would probably be a good idea to move all this formatting code to the TableColumns to prevent this kind of problem

                            if(column instanceof com.lcs.wc.planning.ProductPlanTableColumn){
                            	String value = column.drawExcelCell(td);
                            	if("\u2717".equals(value) || "\u2713".equals(value)){
                            		if("\u2717".equals(value) ){
                            			value = ProductPlanTableColumn.UNCHECKED_STRING;
                            		}else if("\u2717".equals(value) ){
                            			value = ProductPlanTableColumn.CHECKED_STRING;
                            		}
                            		cellFormat  = HSSFDataFormat.getBuiltinFormat("text");
                            		cellstyle = egh.getCellStyle(styleClass, column.getAlign(), fontSize, cellFormat);
                                    cell.setCellStyle(cellstyle);
                                    cell.setCellValue(value);
                                    cellcount++;
                                    continue;
                            	}
                            	
                            }
                            if (column instanceof CareWashTableColumn) {
                            	// Force non-dark style since carewash images have white background.
                            	styleClass = this.getColumnStyleClass(td, column, this.getCellClass(false));
                            	cellstyle = egh.getCellStyle(styleClass, column.getAlign(), fontSize, cellFormat, column.isExcelWrapping());
                                cell.setCellStyle(cellstyle);

                            	((CareWashTableColumn)column).drawCareWashImages(td, wb, ws, patriarch, row, cell);
                            } else if (column instanceof BOMColorTableColumn) {
                            	String imageIndex = ((BOMColorTableColumn)column).getImageIndex();
                            	if(FormatHelper.hasContent((imageIndex)) && FormatHelper.hasContent(td.getData(imageIndex))){
                            		String value = td.getData(imageIndex);
                                    String imagePath = "";
                                    StringTokenizer st = new StringTokenizer(value,"/");

                                    while(st.hasMoreTokens()) {
                                       imagePath = st.nextToken();
                                    }

                                    imagePath = imagefilePath + File.separator + imagePath;
                                    File imageFile = new File(imagePath);
                                    if(imageFile.exists() && imageFile.isFile()){
                                        try{
                                            //Grab image and get height/width and determine compression based on lcs.properties
                                            BufferedImage image=javax.imageio.ImageIO.read(imageFile);
                                            int imageWidth = image.getWidth(null);
                                            int imageHeight = image.getHeight(null);                                               
                                            double imageRatio = (double)imageWidth / (double)imageHeight;
                                            int thumbWidth = preferedBOMColorThumbnailWidth;
                                            // if BOM color image width setting is wider than the whole BOM color column width
                                            // set it to 40% of the whole column  
                                            if (preferedBOMColorColumnWidth < preferedBOMColorThumbnailWidth){
                                            	thumbWidth = (int)(preferedBOMColorColumnWidth * 0.4);
                                            }
                                            int thumbHeight = (int)(thumbWidth / imageRatio / 2);
                                            if(thumbHeight > row.getHeight()) {
                                            	row.setHeight((short)(thumbHeight)); // row height can only be enlarged	
                                            }
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                        // set BOM color column width
                                        ws.setColumnWidth(cell.getColumnIndex(), preferedBOMColorColumnWidth);
                                        //attach the image to worksheet in a particular column and row
                                        if (preferedBOMColorThumbnailWidth>0){
                                        	addBOMImageToWorkSheet(cell, imagePath,row.getRowNum(),cell.getColumnIndex(), preferedBOMColorColumnWidth, preferedBOMColorThumbnailWidth);
                                        }
                                        
                                    }    
                            	} else {
                            		 ws.setColumnWidth(cell.getColumnIndex(), preferedBOMColorColumnWidth);
                            		 if (preferedBOMColorThumbnailWidth>0 && FormatHelper.hasContent(td.getData(column.getBgColorIndex()))){
                            			 addBOMColorToWorkSheet(cell, row.getRowNum(), cell.getColumnIndex(), td.getData(column.getBgColorIndex()), preferedBOMColorColumnWidth, preferedBOMColorThumbnailWidth);
                            		 }
                            	}	 
                            }
                            
                            if(column.isImage()){
                                if(!FormatHelper.hasContent(column.getBgColorIndex()) || !FormatHelper.hasContent(td.getData(column.getBgColorIndex())) || (FormatHelper.hasContent(column.getTableIndex()) && "LCSCOLOR.thumbnail".equals(column.getTableIndex()) && td.getData(column.getTableIndex())!=null && FormatHelper.hasContent(td.getData(column.getTableIndex())))){
                                	if(td.getData(column.getTableIndex()) != null && FormatHelper.hasContent(td.getData(column.getTableIndex()))){
                                        String value = td.getData(column.getTableIndex());
                                        String imagePath = "";
                                        StringTokenizer st = new StringTokenizer(value,"/");

                                        while(st.hasMoreTokens()) {
                                           imagePath = st.nextToken();
                                        }

                                        
                                        /*
                                         *Image stored in DB URL is exact name of iamge on server so no need to decode it as it was encoded to save and if decode name will be wrong.
                                        try{
                                            //need to decode the image path as it is gotten from the web url stored on the object.
                                            System.out.println("******************image Path before decode is=" + imagePath);

                                            imagePath= java.net.URLDecoder.decode(imagePath, defaultCharsetEncoding);
                                            System.out.println("******************image Path after decode is=" + imagePath);
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }*/

                                        imagePath = imagefilePath + File.separator + imagePath;
                                        File imageFile = new File(imagePath);
                                        if(imageFile.exists() && imageFile.isFile()){
                                            try{
                                                //Grab image and get height/width and determine compression based on lcs.properties
                                                BufferedImage image=javax.imageio.ImageIO.read(imageFile);
                                                int imageWidth = image.getWidth(null);
                                                int imageHeight = image.getHeight(null);                                               
                                                double imageRatio = (double)imageWidth / (double)imageHeight;
                                                int thumbWidth = preferedImageColumnWidth;
                                                int thumbHeight = (int)(thumbWidth / imageRatio / 2);
                                                if(thumbHeight > row.getHeight()) {
                                                	row.setHeight((short)(thumbHeight)); // row height can only be enlarged	
                                                }
                                            }catch(Exception e){
                                                e.printStackTrace();
                                            }
                                            //attach the image to worksheet in a particular column and row
                                            addImageToWorkSheet(imagePath,row.getRowNum(),cell.getColumnIndex());
                                            //griffin
                                        }else{
                                            System.out.println("Could not find image to attach, possibly file does not exist in the images folder or a folder was passed in. " + imagePath);
                                        }
                                    }else{
                                        // CG: Column Data does not match up with result set objects so dont do anything!
                                    }
                                }else{
                                    //could set the width and height but not going to right now. Just going to take default. If someone complains can cahnge easily with lines below.
                                    //this.ws.setColumnWidth((short)cell.getCellNum(), (short)1200);
                                    //row.setHeight((short)300);  

                                    //Since its a color but is overridden by an image we are not going to show it so check that does not have data so be sure color and no image is present.
                                    if(!FormatHelper.hasContent(td.getData(column.getTableIndex()))){
                                    //assuming must be a color then if a background color is set and column is image so need to add as a shape.
                                        addColorToWorkSheet(row.getRowNum(), cell.getColumnIndex(), td.getData(column.getBgColorIndex()));
                                    }
                                }
                            }
                            

                            
                            // jc - Call the proper setCellValue() to ensure excel data type is not loaded as text 
                            if ( ! FormatHelper.hasContent(column.getAttributeType()))  {
                                //cellstyle.setDataFormat(df.getFormat("text"));
                                //cell.setCellStyle(cellstyle);

								//CGriffin - If block put in so that if bg color is set and is image but no color specified then set to --. Ex: color column on material search page but no color on material.
								if((column.isImage() && FormatHelper.hasContent(column.getBgColorIndex())) && !FormatHelper.hasContent(td.getData(column.getBgColorIndex()))){
									cell.setCellValue(new HSSFRichTextString("--"));
								}else{
									cell.setCellValue(column.drawExcelCell(td));   
								}

                            } else if ( "float".equals(column.getAttributeType()) || "currency".equals(column.getAttributeType()) || "constant".equals(column.getAttributeType()) ) {
                                //cellstyle.setDataFormat(df.getFormat("#,###.##"));
                                //cellstyle.setDataFormat(df.getFormat(getFloatFormat(column)));
                                
                                //cell.setCellStyle(cellstyle);

                                column.setFormat(FormatHelper.FLOAT_FORMAT_NO_SYMBOLS);
                                String cellValue = column.drawExcelCell(td);
                                if(FormatHelper.hasContentAllowZero(cellValue)){
                                    cell.setCellValue(new Double(cellValue).doubleValue());
                                    
									int cw = (int)Math.round((1.1 *cellValue.length() * 256 ));
                                    if(cw  > this.ws.getColumnWidth(cell.getColumnIndex())){
                                    	this.ws.setColumnWidth(cell.getColumnIndex(), cw);
                                    }
                                }else{
                                	//Added this to allow putting a blank row
                                	cell.setCellValue(new HSSFRichTextString(""));
                                }
                            } else if ( "integer".equals(column.getAttributeType()) ) {
                                //cellstyle.setDataFormat(df.getFormat("#,##0"));
                                //cell.setCellStyle(cellstyle);

                                column.setFormat(FormatHelper.FLOAT_FORMAT_NO_SYMBOLS);
                                String cellValue = column.drawExcelCell(td);
                                if(FormatHelper.hasContentAllowZero(cellValue)){
                                    cell.setCellValue(new Integer(cellValue).intValue());
                                    
									int cw = (int)Math.round((1.1 *cellValue.length() * 256 ));
                                    if(cw  > this.ws.getColumnWidth(cell.getColumnIndex())){
                                    	this.ws.setColumnWidth(cell.getColumnIndex(), cw);
                                    }
                                }else{
                                	//Added this to allow putting a blank row
                                	cell.setCellValue(new HSSFRichTextString(""));
                                }
                            } else if ( "date".equals(column.getAttributeType()) ) {
                                //cellstyle.setDataFormat(df.getFormat("m/d/yy"));
								//cell.setCellStyle(cellstyle);
								String dateString = column.drawExcelCell(td);
								if (FormatHelper.hasContent(dateString)) {
								    cell.setCellValue(new HSSFRichTextString(FormatHelper.applyFormat(dateString, FormatHelper.DATE_ONLY_IGNORE_TZ_STRING_FORMAT)));
								} else {
								    cell.setCellValue(new HSSFRichTextString(""));
								}
                            } else {
                                //System.out.println("********************AttType is=" + column.getAttributeType());
                                //cellstyle.setDataFormat(df.getFormat("text"));
                                //cellstyle.setWrapText(true);
                                //cell.setCellStyle(cellstyle);
                            	String cellValue = column.drawExcelCell(td);
                            	if(FormatHelper.hasContent(cellValue) && cellValue.length() > 255){
	                        		cellFormat  = 0;
	                        		cellstyle = egh.getCellStyle(styleClass, column.getAlign(), fontSize, cellFormat, column.isExcelWrapping());
	                                cell.setCellStyle(cellstyle);
                            	}

                                cell.setCellValue(cellValue);
                            }
                            cellcount++;
                        } else {
					        debug(4, "---columnDisplayed--");
                            cell = row.createCell(cellcount);
                            cellstyle = egh.getCellStyle(this.getCellClass(this.darkRow), column.getAlign(), fontSize);
                            cell.setCellStyle(cellstyle);
                            cell.setCellValue("");
                            cellcount++;
                        }
                    }

                    // && ((column.isByGroup() && firstRow) || !column.isByGroup())
                    if(showColumn && (this.showTotals || this.showSubTotals) && ((column.isTotal() || column.isSubTotal()) || (this.groupByColumns != null && this.groupByColumns.contains(column)))){
                        this.addToTotals(td, column);
                    }
                    if(showColumn && this.showDiscreteRows && column.isDiscreteCount()){
                        this.addToDiscreteSet(td, column);
                    }
                }
                
            } else {
                if(td instanceof FlexObject){
                    FlexObject flex = (FlexObject) td;
                    keys = flex.keySet().iterator();
                    String key;
                    while(keys.hasNext()){
                        key = (String) keys.next();
                        cell = row.createCell(cellcount);
                        cellstyle = egh.getCellStyle(this.getCellClass(this.darkRow), "left", fontSize);
                        //FX22 work, POI latest no longer needs encoding set, handles it on its own now so api was removed
						//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                        cell.setCellStyle(cellstyle);
                        cell.setCellValue(flex.getString(key));
                        cellcount++;
                    }
                }
            }
            firstRow = false;
            this.darkRow = !this.darkRow;
            if (!showRepeatedData){
				Iterator<?> tableNameIterator= repeatedDataColumnMap.keySet().iterator();
				
				while(tableNameIterator.hasNext()){
					tableName = (String)tableNameIterator.next();
					uniqueColumn = (String)repeatedDataColumnMap.get(tableName);
					if(FormatHelper.hasContent(td.getData(uniqueColumn))){
						repeatedDataIds.put(uniqueColumn, td.getData(uniqueColumn));
					}else{
						repeatedDataIds.remove(uniqueColumn);
					}
				}
            }            
        }
        return "";
    }
    
	public String drawDiscreteGrandTotal(TableColumn column){
        String display = column.getDiscreteLabel();
        HSSFCellStyle cellstyle;
        if(!FormatHelper.hasContent(display)){
            Object[] objB = {column.getHeaderLabel(), Integer.toString(this.getDiscreteTotalSetCount(column) ) };
            display = WTMessage.getLocalizedMessage ( RB.TABLEGENERATOR, "totalFor_LBL", objB) ;
        }
        int total = this.getDiscreteTotalSetCount(column);
        HSSFRow row = this.ws.createRow(rowcount);
        rowcount++;
        HSSFCell cell = row.createCell(0);
        cellstyle = egh.getCellStyle(this.totalsClass, "left", fontSize);
        //FX22 work, POI latest no longer needs encoding set, handles it on its own now so api was removed
		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellStyle(cellstyle);
        cell.setCellValue(display+" "+total );
        return "";
    }
    
    public String drawDiscreteTotalRows(String group, TableColumn column){
        
        Iterator<?> subColumns = (new Vector(this.columns)).iterator();
        Iterator<?> subColumns2;
        TableColumn sColumn;
        TableColumn drawColumn;
        boolean reachedColumn = false;

        HSSFCell cell;
        HSSFCellStyle cellstyle;
        
        
        while(subColumns.hasNext()){
            sColumn = (TableColumn)subColumns.next();
            if(sColumn.isDiscreteCount()){
                String dClass = this.totalsClass;
                
                String display = sColumn.getDiscreteLabel();
                if(!FormatHelper.hasContent(display)){
                    Object[] objB = {sColumn.getHeaderLabel(), Integer.toString(this.getDiscreteSetCount(sColumn, group)) };
                    display = WTMessage.getLocalizedMessage ( RB.TABLEGENERATOR, "totalFor_LBL", objB) ;
                }
                int total = this.getDiscreteSetCount(sColumn, group);
                if(FormatHelper.hasContent(column.getSubTotalClass())){
                    dClass = column.getSubTotalClass();
                }
                
                //need to draw a row  
                HSSFRow row = this.ws.createRow(rowcount);                
                rowcount++;                
                subColumns2 = (new Vector(this.columns)).iterator();
                reachedColumn = false;
                int cellcount = 0;
                while(subColumns2.hasNext()){
                    drawColumn = (TableColumn)subColumns2.next();
                    if(drawColumn.isDisplayed()){
                        if(drawColumn.equals(column)){
                            cell = row.createCell(cellcount);
                            cellcount++;
                            cellstyle = egh.getCellStyle(dClass, "left", fontSize);
                            //FX22 work, POI latest no longer needs encoding set, handles it on its own now so api was removed
							//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                            cell.setCellStyle(cellstyle);
                            cell.setCellValue(display + total);
                            reachedColumn = true;
                        }
                        else{
                            if(reachedColumn){
                                cell = row.createCell(cellcount);
                                cellcount++;
                                cellstyle = egh.getCellStyle(dClass, "left", fontSize);
                                cell.setCellStyle(cellstyle);
                                cell.setCellValue("");
                            }
                            else{
                                cell = row.createCell(cellcount);
                                cellcount++;
                                cellstyle = egh.getCellStyle(this.totalsClass, "left", fontSize);
                                cell.setCellStyle(cellstyle);
                                cell.setCellValue("");
                            }
                        }
                    }
                }
            }
        }
        return "";
    }
    
    
    public String drawDiscreteTotal(String group){
        //Draw page header if rows has met max page size
        
        Iterator<?> columnsIt = null;
        TableColumn column = null;
        
        columnsIt = this.columns.iterator();
        int idx = 0;
        String displayGroup = "";
        while (columnsIt.hasNext()) {
            
            idx++;
            column = (TableColumn) columnsIt.next();
            if (!column.isDisplayed()) {
                continue;
            }
            if(FormatHelper.hasContent(group)){
                //displayGroup = column.getLocalizedData(group, true);
                displayGroup = group;
            }
            if(this.groupByColumns == null || this.groupByColumns.contains(column)){
                if(column.isShowGroupSubTotal() && FormatHelper.hasContent(displayGroup) && displayGroup.equals(this.getSubTotalDisplay(column, group))){
                    //Now draw discrete rows
                    this.drawDiscreteTotalRows(group, column);
                }
            }
        }
        return "";
    }
    
    //private String drawTotal(String group, boolean isSubTotal){
    public String drawTotal(String group, boolean isSubTotal){
        Iterator<?> columnsIt = null;
        TableColumn column = null;
        HSSFRow row;
        HSSFCell cell;
        HSSFCellStyle cellstyle;
    	if(!FormatHelper.hasContent(this.grandTotalLabel)){
    		this.grandTotalLabel = WTMessage.getLocalizedMessage(RB.TABLEGENERATOR, "grandTotal_LBL", RB.objA);
    	}    	        
        if(!isSubTotal && this.drawTotalLabel){
            row = this.ws.createRow(rowcount);
            rowcount++;
            cell = row.createCell(0);
            cellstyle = egh.getCellStyle(this.totalsClass, "left", fontSize);
            //FX22 work, POI latest no longer needs encoding set, handles it on its own now so api was removed
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
            cell.setCellStyle(cellstyle);
            cell.setCellValue(this.grandTotalLabel);
        }
        row = this.ws.createRow(rowcount);
        rowcount++;
        int cellcount = 0;
        columnsIt = this.columns.iterator();
        int idx = 0;
        while (columnsIt.hasNext()) {
            
            idx++;
            column = (TableColumn) columnsIt.next();
            if (!column.isDisplayed()) {
                continue;
            }
            
            if(this.groupByColumns != null && this.groupByColumns.contains(column)){
                Vector colGroups = new Vector((Collection)this.groupByGroups.get(column.getTableIndex()));
                int gIndex = colGroups.indexOf(group);
                
                if(isSubTotal && column.isShowGroupSubTotal() && FormatHelper.hasContent(group) && gIndex > -1){
                    if(FormatHelper.hasContent(column.getSubTotalClass())){
                        this.subTotalsClass = column.getSubTotalClass();
                    }
                    else{
                        this.subTotalsClass = this.totalsClass;
                    }
              
                    cell = row.createCell(cellcount);
                    cellcount++;
                    cellstyle = egh.getCellStyle(this.subTotalsClass, column.getAlign(), fontSize);
                    //FX22 work, POI latest no longer needs encoding set, handles it on its own now so api was removed
					//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell.setCellStyle(cellstyle);                                        
                    cell.setCellValue(this.getSubTotalDisplay(column, group));
                }
                else{
                    if(isSubTotal){
                        cell = row.createCell(cellcount);
                        cellcount++;
                        cellstyle = egh.getCellStyle(this.subTotalsClass, column.getAlign(), fontSize);
                        cell.setCellStyle(cellstyle);
                        cell.setCellValue("");
                    }
                    else{
                        cell = row.createCell(cellcount);
                        cellcount++;
                        cellstyle = egh.getCellStyle(this.totalsClass, column.getAlign(), fontSize);
                        cell.setCellStyle(cellstyle);
                        cell.setCellValue("");
                    }
                }
            }
            else if (column.isTotal()) {
                if(isSubTotal){
                    cell = row.createCell(cellcount);
                    cellcount++;
                    cellstyle = egh.getCellStyle(this.subTotalsClass, column.getAlign(), fontSize);
                    //FX22 work, POI latest no longer needs encoding set, handles it on its own now so api was removed
					//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell.setCellStyle(cellstyle);
                    cell.setCellValue(this.getSubTotalDisplay(column, group));
                }
                else{
                    cell = row.createCell(cellcount);
                    cellcount++;
                    cellstyle = egh.getCellStyle(this.totalsClass, column.getAlign(), fontSize);
                    //FX22 work, POI latest no longer needs encoding set, handles it on its own now so api was removed
					//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
                    cell.setCellStyle(cellstyle);
                    cell.setCellValue(this.getTotalDisplay(column));
                }
                
            } else {
                if(isSubTotal){
                    cell = row.createCell(cellcount);
                    cellcount++;
                    cellstyle = egh.getCellStyle(this.subTotalsClass, column.getAlign(), fontSize);
                    cell.setCellStyle(cellstyle);
                    cell.setCellValue("");
                }
                else{
                    cell = row.createCell(cellcount);
                    cellcount++;
                    cellstyle = egh.getCellStyle(this.totalsClass, column.getAlign(), fontSize);
                    cell.setCellStyle(cellstyle);
                    cell.setCellValue("");
                }
                
            }
        }
        
        this.subTotalsClass = this.totalsClass;        
        return "";
    }

    protected String drawCounts(String group, boolean isSubTotal){
        if (this.isShowDiscreteRows()) {
            if(isSubTotal){
               this.drawDiscreteTotal(group);
            }
            else{
                Iterator<?> columnCount = this.columns.iterator();
                TableColumn t;
                while(columnCount.hasNext()){
                    t = (TableColumn)columnCount.next();
                    if(t.isDiscreteCount()){
                        this.drawDiscreteGrandTotal(t);
                    }
                }
            }
            
        }
        return "";
    }    
    ////////////////////////////////////////////////////////////////////////////
    /**
     *  Creates the tag code to start an header label that spans multiple table columns. The
     *  Column Span is based on the 'columns Collection' or the 'data' size.
     *  The format of the Group Content section is specified by the
     *  html style class 'TABLESECTIONHEADER'. This method works in conjunction with
     *  endContentTable.
     *  Class Dependancies:
     *<ul>
     * <li> columns     -   if columns is null zero is used
     * <li> data.size   -   if data.size is null zero is used
     *</ul>
     *
     *  @param  label  - a <code>String</code> label placed in the Header of the section
     *
     *  @return    a <code>String</code>, html tag code
     *  @see  #endContentTable()
     *  @see  #setColumns(Collection columns)
     *  @see  #getData()
     *  @see  #setData(Collection data)
     */
    protected String drawSectionHeader(String label){
        HSSFRow row = this.ws.createRow(rowcount);
        rowcount++;
        HSSFCell cell = row.createCell(0);
        HSSFCellStyle cellstyle;
        cellstyle = egh.getCellStyle(this.tableSectionHeaderClass, "left", fontSize);
        //FX22 work, POI latest no longer needs encoding set, handles it on its own now so api was removed
		//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
        cell.setCellStyle(cellstyle);
        cell.setCellValue(FormatHelper.format(label));
        return "";
    }
    
    protected String printFile(){
        String fileName = this.generateFileName();
        String fileOutName = FormatHelper.formatRemoveProblemFileNameChars(fileName);

        String fName = downloadLocation + fileOutName;
        try{
            FileOutputStream out = new FileOutputStream(fName);
            this.wb.write(out);
            out.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        
        String url;
        try{
            fileOutName = java.net.URLEncoder.encode(fileOutName, defaultCharsetEncoding);

           // has trailing file.separator
            String filepath = FormatHelper.formatOSFolderLocation(FileLocation.CsvExcelDownloadLocation);

            // Set the URL for this generated temp file
            url = filepath + fileOutName;
        } catch(java.io.UnsupportedEncodingException ex){
            ex.printStackTrace();
            url = this.filename + ".xls";
        }
        
        return url;
    }
    
    protected void intializeWorksheet(){
    	this.intializeWorksheet("");
    }
    
    protected void intializeWorksheet(String sheetName){
    	this.wb = new HSSFWorkbook();
        
        if(FormatHelper.hasContent(sheetName)){
        	
        	sheetName = formatSheetName(sheetName);
        	this.ws = this.wb.createSheet(sheetName);
        }
        else{
        	this.ws = this.wb.createSheet();
        }
        this.df = this.wb.createDataFormat();
        //Set footer pages
        HSSFFooter footer = this.ws.getFooter();
        footer.setRight( WTMessage.getLocalizedMessage ( RB.MAIN, "pageOf_LBL", RB.objA ) + HSSFFooter.numPages() );
        
        
        this.filename = generateStubName();
        if(this.filename.length() > 31){
			//fx22, no longer need to set encoding for poi so they removed it
            //this.wb.setSheetName(0, this.filename.substring(0,30), HSSFCell.ENCODING_UTF_16);
            this.wb.setSheetName(0, this.filename.substring(0,30));
        } else {
            //this.wb.setSheetName(0, this.filename, HSSFCell.ENCODING_UTF_16);
            this.wb.setSheetName(0, this.filename);
        }
        this.egh = new ExcelGeneratorHelper(this.wb);
        this.ps = this.ws.getPrintSetup();
        this.setPrintOptions();
        //Enable when POI supports more Pic formats
        patriarch = this.ws.createDrawingPatriarch();

    }
    
    protected String formatSheetName(String sheetName) {
    	
 	   sheetName = sheetName.replaceAll("\\[", "_");//forward slash
	   sheetName = sheetName.replaceAll("\\]", "_");//forward slash
	   sheetName = sheetName.replaceAll("/", "_");//forward slash
	   sheetName = sheetName.replaceAll("\\u003f", "_");//question mark
	   sheetName = sheetName.replaceAll("\\u002a", "_");//splat
	   sheetName = sheetName.replaceAll("\\u005c", "_");//back slash

    	if(sheetName.length() > 31){
    		sheetName = sheetName.substring(0,30);
    	}
		return sheetName;
	}

	protected void setPrintOptions(){
        if(this.landscape){
            this.ps.setLandscape(true);
            debug("setting to landscape");
        }
        else{
            this.ps.setLandscape(false);
            debug("setting to normal orientation");
        }
        if(this.paperSize > 0){
            this.ps.setPaperSize(this.paperSize);
            debug("setting paper size to " + this.paperSize);
        }
        if(this.pageHeight > 0 || this.pageWidth > 0){
            this.ws.setAutobreaks(true);
        }
        if(this.pageHeight > 0){
            this.ps.setFitHeight((short)this.pageHeight);
            debug("setting pageHeight " + this.pageHeight);
        }
        if(this.pageWidth > 0){
            this.ps.setFitWidth((short)this.pageWidth);
            debug("setting pageWidth " + this.pageWidth);
        }
    }
    
    protected String generateStubName(){
        SimpleDateFormat format = new SimpleDateFormat("MMddyyyy_hhmm");
        String time = format.format(new java.util.Date());
        
		String userName = "-";
		try{
			userName = this.context.getUser().getName().toString();
		}catch(wt.util.WTException wte){
			wte.printStackTrace();
		}
        String fname = this.reportName + "_" + time;
        fname = FormatHelper.formatRemoveProblemFileNameChars(fname);
        //Remove [ and ] because they cause problems in sheet names. // NOPMD by jpuented on 12/3/10 11:35 AM
        fname = FormatHelper.removeCharacter(fname, "[");
        fname = FormatHelper.removeCharacter(fname, "]");
        return fname;
    }

    private String generateFileName(){
        int count = 1;
        
        String temp = this.filename;
    File file = new File(downloadLocation + temp + ".xls");
        while(file.exists()){
            temp = temp + "_" + count;
            count++;
            
            file = new File(downloadLocation + temp + ".xls");
        }
        this.filename = temp;
        return this.filename + ".xls";
        
        
    }
    
    ////////////////////////////////////////////////////////////////////////////
   /** private String formatForCSV(String text){
        
        if(null == text || "".equals(text)){
            text = "";
        }
        else{
            boolean endsWith = text.endsWith("\"");
            
            StringTokenizer parser = new StringTokenizer(text, "\"");
            
            if(text.startsWith("\"")){
                text = "\"\"";
            }
            else{
                text = "";
            }
            if(parser.countTokens() > 1){
                while(parser.hasMoreTokens()){
                    String token = parser.nextToken();
                    if(token.indexOf(",") > -1){
                        token = "\"" + token + "\"";
                    }
                    if(parser.hasMoreTokens()){
                        text = text + token + "\"\"";
                    }
                    else{
                        text = text + token;
                    }
                }
            }
            else{
                text = text + parser.nextToken();
                if(text.indexOf(",") > -1){
                    text = "\"" + text + "\"";
                }
            }
            
            if(endsWith){
                text = text + "\"\"";
            }
        }
        return text;
    }
    **//*
    protected void addToTotals(TableData td, TableColumn col){
        
        
        if(!(this.showTotals || this.showSubTotals)){
            return;
        }
        Double dObj;
        
        if(groupByColumns != null && this.groupByColumns.contains(col)){
            Iterator family = this.groupFamily.iterator();
            String group;
            Hashtable grpST;
            while(family.hasNext()){
                group = (String)family.next();
                grpST = (Hashtable)this.groupSubTotals.get(group);
                if(grpST == null){
                    grpST = new Hashtable();
                }
                String storedVal = (String) grpST.get(col);
                //if(storedVal != null){
                //    return;
                //}
                //storedVal = td.getData(col.getTableIndex());
                //String dataVal = col.getDisplayValue(td);
                String dataVal = col.drawExcelCell(td);
                if(!FormatHelper.hasContent(storedVal))
                    grpST.put(col, dataVal);
                this.groupSubTotals.put(group, grpST);
            }
            return;
        }
        
        if(col.getTotalMathFunctions() != null){
            Collection mathFunctions = col.getTotalMathFunctions();
            Iterator it = mathFunctions.iterator();
            Hashtable operation;
            
            TableColumn column1 = null;
            double d1 = 0;
            
            TableColumn column2 = null;
            double d2 = 0;
            
            String func;
            while(it.hasNext()){
                d1 = 0;
                d2 = 0;
                column1 = null;
                column2 = null;
                operation = (Hashtable)it.next();
                column1 = (TableColumn)operation.get(TableColumn.MATHCOLUMN1);
                if(operation.get(TableColumn.MATHCOLUMN2) instanceof TableColumn){
                    column2 = (TableColumn)operation.get(TableColumn.MATHCOLUMN2);
                }
                else if(operation.get(TableColumn.MATHCOLUMN2) instanceof Double){
                    d2 = ((Double)operation.get(TableColumn.MATHCOLUMN2)).doubleValue();
                }
                func = (String)operation.get(TableColumn.MATHFUNCTION);
                
                if(this.showTotals){
                    // ADD TO TOTALS
                    dObj = (Double) this.totals.get(column1);
                    if(dObj == null || dObj.isNaN()){
                        dObj = new Double(0);
                    }
                    d1 = dObj.doubleValue();
                    
                    if(column2 != null){
                        dObj = (Double) this.totals.get(column2);
                        if(dObj == null || dObj.isNaN()){
                            dObj = new Double(0);
                        }
                        d2 = dObj.doubleValue();
                    }
                    dObj = new Double(doStringMath(d1, d2, func));
                    
                    
                    this.totals.put(col, dObj);
                }
                if(this.showSubTotals){
                    // ADD TO SUB-TOTALS
                    Iterator family = this.groupFamily.iterator();
                    String group;
                    Hashtable grpST;
                    while(family.hasNext()){
                        group = (String)family.next();
                        grpST = (Hashtable)this.groupSubTotals.get(group);
                        if(grpST == null){
                            grpST = new Hashtable();
                        }
                        
                        dObj = (Double) grpST.get(column1);
                        if(dObj == null || dObj.isNaN()){
                            dObj = new Double(0);
                        }
                        d1 = dObj.doubleValue();
                        
                        if(column2 != null){
                            dObj = (Double) grpST.get(column2);
                            if(dObj == null || dObj.isNaN()){
                                dObj = new Double(0);
                            }
                            d2 = dObj.doubleValue();
                        }
                        
                        dObj = new Double(doStringMath(d1, d2, func));
                        
                        grpST.put(col, dObj);
                        this.groupSubTotals.put(group, grpST);
                    }
                }
            }
            return;
        }
        
        // GET THE NUMERIC
        String s = td.getData(col.getTableIndex());
        double d = FormatHelper.parseDouble(s);
        
        if(this.showTotals){
            // ADD TO TOTALS
            dObj = (Double) this.totals.get(col);
            if(dObj == null){
                dObj = new Double(0);
            }
            dObj = new Double(dObj.doubleValue() +  d);
            this.totals.put(col, dObj);
        }
        
        if(this.showSubTotals){
            // ADD TO SUB-TOTALS
            Iterator family = this.groupFamily.iterator();
            String group;
            Hashtable grpST;
            while(family.hasNext()){
                group = (String)family.next();
                grpST = (Hashtable)this.groupSubTotals.get(group);
                if(grpST == null){
                    grpST = new Hashtable();
                }
                dObj = (Double) grpST.get(col);
                if(dObj == null){
                    dObj = new Double(0);
                }
                dObj = new Double(dObj.doubleValue() +  d);
                grpST.put(col, dObj);
                this.groupSubTotals.put(group, grpST);
            }
        }
    }
    */
    public void setExcelTableHeaderGenerator(ExcelTableHeaderGenerator ethg){
        this.ethg = ethg;
    }
    
    public ExcelTableHeaderGenerator getExcelTableHeaderGenerator(){
        return this.ethg;
    }
    
    
    public void setLandscape(boolean landscape){
        this.landscape = landscape;
    }
    public boolean isLandscape(){
        return this.landscape;
    }
    
    public void setPaperSize(short paperSize){
        this.paperSize = paperSize;
    }
    public short getPaperSize(){
        return this.paperSize;
    }
    
    public void setPageWidth(int pageWidth){
        this.pageWidth = pageWidth;
    }
    public int getPageWidth(){
        return this.pageWidth;
    }
    
    public void setPageHeight(int pageHeight){
        this.pageHeight = pageHeight;
    }
    public int getPageHeight(){
        return this.pageHeight;
    }
    
    protected void addImageToWorkSheet(String image, int cellRow, int cellColumn){
            //griffin
            
            //Looks at image extension and based on that sets image type for the POI API
            int pictureType = -1;
            if((image.toUpperCase().endsWith("BMP"))){
                //This should work but doesnt yet!!!!
                //pictureType = HSSFWorkbook.PICTURE_TYPE_DIB;
            }
            
            if((image.toUpperCase().endsWith("JPG") || image.toUpperCase().endsWith("JPEG"))){
                pictureType = HSSFWorkbook.PICTURE_TYPE_JPEG;
            }
            
            if((image.toUpperCase().endsWith("PNG")) || (image.toUpperCase().endsWith("GIF"))){
                pictureType = HSSFWorkbook.PICTURE_TYPE_PNG;
            }
            
        try{
            if(pictureType > -1){
                HSSFClientAnchor anchor;
                anchor = new HSSFClientAnchor( 0, 0, 1023, 255, (short) cellColumn, cellRow, (short) cellColumn, cellRow );
                //anchor.setAnchorType( 0 );
                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                patriarch.createPicture(anchor, loadPicture( image, wb, pictureType));        
            }else{
                //throw exception if image is not supported which will cause try to fail on this and will not create the picture on excel
                throw new LCSException("This image type is not currently supported for export to excel and can not be added to the excel workseet !");
                
            }
           
        }catch(Exception e){
               System.out.println("A Exception has occured adding image (" + image + ") to the Excel Worksheet." + "  \n  " + e);
        }
    }
    
    protected void addBOMImageToWorkSheet(HSSFCell cell, String image, int cellRow, int cellColumn, int totalColorWidth, int thumbnailWidth){
        
        //Looks at image extension and based on that sets image type for the POI API
        int pictureType = -1;
        if((image.toUpperCase().endsWith("BMP"))){
            //This should work but doesnt yet!!!!
            //pictureType = HSSFWorkbook.PICTURE_TYPE_DIB;
        }
        
        if((image.toUpperCase().endsWith("JPG") || image.toUpperCase().endsWith("JPEG"))){
            pictureType = HSSFWorkbook.PICTURE_TYPE_JPEG;
        }
        
        if((image.toUpperCase().endsWith("PNG")) || (image.toUpperCase().endsWith("GIF"))){
            pictureType = HSSFWorkbook.PICTURE_TYPE_PNG;
        }
        
    try{
        if(pictureType > -1){
            HSSFClientAnchor anchor;
            // if totalColorWidth is less than thumbnailWidth, set thumbnail width to 40% of the totalColorWidth 
            int colorNameWidth = totalColorWidth - thumbnailWidth;
            if (colorNameWidth < 0){
            	colorNameWidth = (int)(totalColorWidth * 0.6);
            }
            anchor = new HSSFClientAnchor((int)(1023.0/totalColorWidth*(colorNameWidth)), 0, 1023, 255, (short) cellColumn, cellRow, (short) cellColumn, cellRow );
            //anchor.setAnchorType( 0 );
            anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
            patriarch.createPicture(anchor, loadPicture( image, wb, pictureType));        
        }else{
        	// in case of unsupported thumbnail format, add comment for the color cell
     		HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) cellColumn, cellRow, (short) (cellColumn + 2), cellRow + 2));
    		comment.setString(new HSSFRichTextString(WTMessage.getLocalizedMessage (RB.FLEXBOM, "unsupportedImageFormat_MSG", RB.objA )));
    		cell.setCellComment(comment);
    		//throw exception if image is not supported which will cause try to fail on this and will not create the picture on excel
            throw new LCSException("This image type is not currently supported for export to excel and can not be added to the excel worksheet !");            
        }
       
    }catch(Exception e){
           System.out.println("A Exception has occured adding image (" + image + ") to the Excel Worksheet." + "  \n  " + e);
    }
}

    private static int loadPicture( String path, HSSFWorkbook wb, int pictureType ) throws IOException {
        //load the picture and get the pictureindex for POI api to use and attach to the worksheet in the end
        int pictureIndex;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;

        try {
            fis = new FileInputStream( path);
            bos = new ByteArrayOutputStream( );
            int c;

            while ( (c = fis.read()) != -1) {
                bos.write( c );
            }

            pictureIndex = wb.addPicture( bos.toByteArray(), pictureType  );
        } finally {
            if (fis != null) fis.close();
            if (bos != null) bos.close();
        }
        return pictureIndex;
    }

        
        
    private void addColorToWorkSheet(int cellRow, int cellColumn, String hexValue){
        //griffin

        int redInt = 0;
        int greenInt = 0;
        int blueInt = 0;

        if(hexValue != null){
           com.lcs.wc.color.LCSColorLogic logic = new com.lcs.wc.color.LCSColorLogic();
           redInt = logic.toDex(hexValue.substring(0,2));
           greenInt = logic.toDex(hexValue.substring(2,4));
           blueInt = logic.toDex(hexValue.substring(4,6));
        }
            
        try{
                HSSFClientAnchor anchor;
                anchor = new HSSFClientAnchor( 0, 0, 1023, 255, (short) cellColumn, cellRow, (short) cellColumn, cellRow );
                //anchor.setAnchorType( 2 );
                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);
                

                HSSFSimpleShape s = patriarch.createSimpleShape(anchor);
                s.setShapeType(HSSFSimpleShape.OBJECT_TYPE_RECTANGLE);
                s.setFillColor(redInt,greenInt,blueInt);
           
        }catch(Exception e){
               System.out.println("A Exception has occured adding a color to the Excel Worksheet. - " + e);
        }
    }
    
    protected void addBOMColorToWorkSheet(HSSFCell cell, int cellRow, int cellColumn, String hexValue, int totalColorWidth, int thumbnailWidth) {
    	int redInt = 0;
        int greenInt = 0;
        int blueInt = 0;

        if(FormatHelper.hasContent(hexValue)){
           com.lcs.wc.color.LCSColorLogic logic = new com.lcs.wc.color.LCSColorLogic();
           redInt = logic.toDex(hexValue.substring(0,2));
           greenInt = logic.toDex(hexValue.substring(2,4));
           blueInt = logic.toDex(hexValue.substring(4,6));
        }
            
        try{
                HSSFClientAnchor anchor;
                // if totalColorWidth is less than thumbnailWidth, set thumbnail width to 40% of the totalColorWidth 
                int colorNameWidth = totalColorWidth - thumbnailWidth;
                if (colorNameWidth < 0){
                	colorNameWidth = (int)(totalColorWidth * 0.6);
                }
                anchor = new HSSFClientAnchor( (int)(1023.0/totalColorWidth*(colorNameWidth)), 0, 1023, 255, (short) cellColumn, cellRow, (short) cellColumn, cellRow );
                //anchor.setAnchorType( 2 );
                anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_DONT_RESIZE);

                HSSFSimpleShape s = patriarch.createSimpleShape(anchor);
                s.setShapeType(HSSFSimpleShape.OBJECT_TYPE_RECTANGLE);
                s.setFillColor(redInt,greenInt,blueInt);
           
        }catch(Exception e){
               System.out.println("A Exception has occured adding a color to the Excel Worksheet. - " + e);
        }
		
	}
     
    public String getColumnStyleClass(TableData td, TableColumn column, String cellClass){
        return getColumnStyleClass(td, column, cellClass, false);
    }
    
    public String getColumnStyleClass(TableData td, TableColumn column, String cellClass, boolean ignoreSpecialClass){
        String style = null;
        if(!ignoreSpecialClass && FormatHelper.hasContent(column.getSpecialClassIndex())){
            String specialFormat = td.getData(column.getSpecialClassIndex());
            if(FormatHelper.hasContent(specialFormat)){
                if (FormatHelper.hasContent(column.getColumnClassIndex()) && FormatHelper.hasContent(td.getData(column.getColumnClassIndex()))) {
                    style = specialFormat + "_" + td.getData(column.getColumnClassIndex());
                } else {
                    style = specialFormat + "_" + cellClass;
                }
            } else {
                if (FormatHelper.hasContent(column.getColumnClassIndex()) && FormatHelper.hasContent(td.getData(column.getColumnClassIndex()))) {
                    style = td.getData(column.getColumnClassIndex());
                } else if (FormatHelper.hasContent(column.getColumnClass())) {
                    style = column.getColumnClass();
                } else {
                    style = cellClass;
                }
            }
        } else {
            if (FormatHelper.hasContent(column.getColumnClassIndex()) && FormatHelper.hasContent(td.getData(column.getColumnClassIndex()))) {
                style = td.getData(column.getColumnClassIndex());
            } else if (FormatHelper.hasContent(column.getColumnClass())) {
                style = column.getColumnClass();
            } else {
                style = cellClass;
            }
        }
        return style;
    }
    /////////////////////////////////////////////////////////////////////////////
   public static void debug(String msg){debug(msg, 1); }
   public static void debug(int i, String msg){debug(msg, i); }
   public static void debug(String msg, int i){
	  if(DEBUG && i <= DEBUG_LEVEL) System.out.println(msg);
   }

    public String getColumnDisplay(TableColumn col, TableData td){
        return col.drawExcelCell(td);
    }

}
