/*
 * This is a custom PDF Generator class used to Rename the Generated PDF Report Name as per Hanes Brands Request Done on 02 August 2015 by BSC
 */
package com.hbi.wc.client.web;

import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Vector;

import wt.util.WTException;
import wt.util.WTMessage;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.client.web.PDFGeneratorHelper;
import com.lcs.wc.client.web.PDFTableHeaderGenerator;
import com.lcs.wc.client.web.TableColumn;
import com.lcs.wc.client.web.TableData;
import com.lcs.wc.client.web.TableGenerator;
import com.lcs.wc.client.web.TableSectionHeader;
import com.lcs.wc.client.web.UserTableColumn;
import com.hbi.wc.client.web.pdf.HBIPDFUtils;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.util.FileLocation;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.RB;
import com.lcs.wc.util.SortHelper;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class HBIPDFGenerator extends TableGenerator{
    
    ////////////////////////////////////////////////////////////////////////////
    String reportName;
    
    //private ClientContext context;
       
    private Document doc;
     
    private PdfPTable pdfTable;
    
    private PdfPCell titleCell;
    
    private String filename;
    
    public String tableCellClass = "";
    public String fontSize = "8";
    
    public float tableWidthPercentage = 95.0f;
    
    public PDFTableHeaderGenerator pthg;
    private PDFGeneratorHelper pgh = new PDFGeneratorHelper();
    
    //Print options
    private boolean landscape = false;
    private float pageHeight = 0;
    
    boolean headerSet = false;
    
    Font font;
    
    //Paper size options
    
    public static int A0 = 0;
    public static int A1 = 1;
    public static int A2 = 2;
    public static int A3 = 3;
    public static int A4 = 4;
    public static int A5 = 5;
    public static int A6 = 6;
    public static int A7 = 7;
    public static int A8 = 8;
    public static int A9 = 9;
    public static int A10 = 10;
    public static int ARCH_A = 11;
    public static int ARCH_B = 12;
    public static int ARCH_C = 13;
    public static int ARCH_D = 14;
    public static int ARCH_E = 15;
    public static int B0 = 16;
    public static int B1 = 17;
    public static int B2 = 18;
    public static int B3 = 19;
    public static int B4 = 20;
    public static int B5 = 21;
    public static int FLSA = 22;
    public static int FLSE = 23;
    public static int HALFLETTER = 24;
    public static int LEDGER = 25;
    public static int LEGAL = 26;
    public static int LETTER = 27;
    public static int NOTE = 28;
    public static int _11X17 = 29;
    
    private int paperSize = LETTER;
    
    int rowcount = 0;
    
    boolean showSubTotals;
    boolean showTotals;
    boolean indentSections;
    boolean spaceBetweenGroups;
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
   // public String drawTable(Collection<?> data, Collection<?> columns, ClientContext context, String reportName){
    public String drawTable(Collection<?> data, Collection<TableColumn> columns, ClientContext context, String reportName){
        //this.context = context;
        this.reportName = reportName;
        return this.drawTable(data, columns);
    }
    
  //  public String drawTable(Collection<?> data, Collection<?> columns){
    public String drawTable(Collection<?> data, Collection<TableColumn> columns){
        //Need to create the Excel file
        this.data = data;
        this.columns = columns;
        
        try{
            this.intializePDF();
        }
        catch(WTException e){
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
        this.setTableId();
        
        String tid = this.getTableId();
        
        /////////////////////////////////////////
        // SET ROW ID INDEX ON ALL COLUMNS
        /////////////////////////////////////////
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
        
        if(FormatHelper.hasContent(this.rowIdIndex) &&
        this.columns != null && this.columns.size() > 0){
            
            Iterator<?> i = this.columns.iterator();
            while(i.hasNext()){
                ((TableColumn)i.next()).setRowIdIndex(this.rowIdIndex);
            }
        }
        this.pdfTable = new PdfPTable(this.getDisplayColumnCount());
        this.pdfTable.setWidthPercentage(tableWidthPercentage);
        try{
            this.pdfTable.setWidths(this.getWidths());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        this.drawHeader();
        doc.open();
        this.drawContentColumnHeaders("T"+tid);
        
        if(this.data.size() < 1){
            for(int i = 0; i < this.getDisplayColumnCount(); i++){
                PdfPCell cell = new PdfPCell(pgh.multiFontPara(""));
                pdfTable.addCell(cell);
            }
        }
        else{
            this.drawContentData();
        }
        
        try{
            doc.add(pdfTable);
            doc.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        return HBIPDFUtils.getDownloadURL(this.filename);
    }
    
    public void drawHeader(){
        if(this.pthg != null){
            this.pthg.createHeader(this.doc, getDisplayColumnCount());
            if(FormatHelper.hasContent(this.pthg.time)||FormatHelper.hasContent(this.pthg.userName)){
            	font = pgh.getCellFont(this.tableHeaderClass, "right", fontSize);
            	
                PdfPCell tCell = new PdfPCell(pgh.multiFontPara(this.pthg.userName+"  "+this.pthg.time, font));
                tCell.setColspan(this.getDisplayColumnCount());
                tCell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.tableHeaderClass, null));
                tCell.setBorderColor(PDFGeneratorHelper.getCellBGColor(this.tableHeaderClass, null));
                tCell.setBorderWidth(0.0f);

                tCell.setMinimumHeight(15);
                tCell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
                
                this.pdfTable.addCell(tCell);
                this.pdfTable.setHeaderRows(1);

            }
        }
        if(this.titleCell != null){
            this.titleCell.setColspan(this.getDisplayColumnCount());
            this.pdfTable.addCell(this.titleCell);
        }
        else if(FormatHelper.hasContent(this.title)){
            font = pgh.getCellFont(this.tableHeaderClass, "left", fontSize);
            PdfPCell tCell = new PdfPCell(pgh.multiFontPara(this.title, font));
            tCell.setColspan(this.getDisplayColumnCount());
            tCell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.tableHeaderClass, null));
            tCell.setBorderColor(PDFGeneratorHelper.getCellBGColor(this.tableHeaderClass, null));
            tCell.setBorderWidth(0.0f);
            this.pdfTable.addCell(tCell);
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
        //HSSFRow row = this.ws.createRow(rowcount);
        rowcount++;
        
        short cellcount = 0;
        
        
        if(this.columns != null){
            Iterator<?> i = this.columns.iterator();
            while(i.hasNext()){
                TableColumn column = (TableColumn) i.next();
                if(column.isDisplayed()){
                    
                    font = pgh.getCellFont(this.tableSubHeaderClass, column.getAlign(), fontSize);
                    PdfPCell cell = new PdfPCell(pgh.multiFontPara(column.getHeaderLabel(), font));
                    cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.tableSubHeaderClass, null));
                    this.pdfTable.addCell(cell);
                    
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
                    font = pgh.getCellFont(this.tableSubHeaderClass, "left", fontSize);
                    PdfPCell cell = new PdfPCell(pgh.multiFontPara(key, font));
                    cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.tableSubHeaderClass, null));
                    this.pdfTable.addCell(cell);
                    cellcount++;
                }
            }
        }
        if(!headerSet){
            int currentRow = this.pdfTable.size();
            this.pdfTable.setHeaderRows(currentRow);
            headerSet = true;
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
        
        if(this.getGroupByColumns() == null || this.getGroupByColumns().isEmpty()){
            if(this.groups == null){
                this.groups = this.deriveGroupsList();
                this.groups.add("");
            }
            
            Iterator<?> allGroups = groups.iterator();
            Collection<?> groupRows;
            int groupNum = 0;
            
            while(allGroups.hasNext()){
                group = (String) allGroups.next();
                groupRows = this.getGroupRows(group);
                groupNum++;
                
                if(groupRows.size() > 0 && (groups.size() > 1 || (groups.size() == 1 && !"".equals(groups.iterator().next().toString())))){
                    
                    String groupLabel = group;
                    if(this.groupColumn != null){
                        groupLabel = this.groupColumn.getLocalizedData(group);
                        groupLabel = FormatHelper.applyFormat(groupLabel, groupColumn.getFormat());
                    }
                    
                    if(!FormatHelper.hasContent(groupLabel)){
                        groupLabel = WTMessage.getLocalizedMessage(RB.MAIN, "emptyLowerCase_LBL", RB.objA);
                    }
                    
                    
                    if(this.columns != null){
                        rowcount++;
                        font = pgh.getCellFont(this.tableSubHeaderClass, "left", fontSize);
                        PdfPCell cell = new PdfPCell(pgh.multiFontPara(groupLabel, font));
                        cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.tableSectionHeaderClass, null));
                        cell.setColspan(getDisplayColumnCount());
                        pdfTable.addCell(cell);
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
					if(this.showSubTotals || this.isShowDiscreteRows()){
	                    this.groupSubTotals.put(group, new Hashtable());
						this.groupDiscreteCounts.put(group, new Hashtable());	                    
	                }
					if (!this.showSubTotals && !this.isShowDiscreteRows() && allGroups.hasNext()) {
	                    
	                    // IF ANOTHER GROUP IS GOING TO BE PRINTED
	                    // THEN PUT IN A SEPERATOR
	                    if (this.spaceBetweenGroups) {
	                        PdfPCell cell = new PdfPCell(pgh.multiFontPara(""));
	                        cell.setColspan(getDisplayColumnCount());
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
        
        //Get the TableColumn for the particular level in tcgroups (this should occur in numeric order, from 0...)
        TableColumn column = (TableColumn)tcgroups.elementAt(level);
        this.currentColumn = column;
        String index = column.getTableIndex();
        //Get all of the possible values for that group
        Vector groupVals = (Vector)this.groupByGroups.get(index);
        //System.out.println("goupVals:" + groupVals);
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
        } else{
            // SHOULD BE SORTING BY VARIOUS MODES. IF GROUP IS BACKED BY AN
            // ATTRIBUTE VALUE LIST, THEN YOU SHOULD BE ABLE TO SORT BY THE
            // SORT ORDER.
            if(column.getList() != null && !"moaList".equals(column.getAttributeType()) && !"composite".equals(column.getAttributeType())){
                // SORT BY ORDERING OF ATTRIBUTES
                try {
                    //System.out.println("groupVals = " + groupVals);

                    // All keys regardless of whether they are currently selectable should be included
                    // They may have been selectable at one time so must include
                    Iterator<?> sortedKeyIter = column.getList().getOrderedKeys(null, false).iterator();

                    HashSet<String> newGroupVals = new LinkedHashSet<String>();
                    String key;
                    while(sortedKeyIter.hasNext()){
                        key = (String) sortedKeyIter.next();
                        key = column.getList().getValue(key, ClientContext.getContext().getLocale());
                        
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
        Collection<?> tempData;
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
            //System.out.println("tempData for " + val + " : " + tempData);
            if(this.columns != null && tempData.size() > 0 && column.isShowGroupByHeader()){
                
                if(!FormatHelper.hasContent(val)){
                    rowcount++;
                    font = pgh.getCellFont(this.tableSubHeaderClass, "left", fontSize);
                    PdfPCell cell = new PdfPCell(pgh.multiFontPara(getIndents(level) + WTMessage.getLocalizedMessage(RB.MAIN, "emptyLowerCase_LBL", RB.objA), font));
                    cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.tableSectionHeaderClass, null));
                    cell.setColspan(getDisplayColumnCount());
                    pdfTable.addCell(cell);
                }
                else{
                    rowcount++;
                    //String groupDisplay = column.getLocalizedData(val, true);
                    font = pgh.getCellFont(this.tableSubHeaderClass, "left", fontSize);
                    //PdfPCell cell = new PdfPCell(pgh.multiFontPara(getIndents(level) + groupDisplay, font));
                    if (column instanceof UserTableColumn && FormatHelper.hasContent(val)) {
                    	FlexObject data = (FlexObject)((Vector)tempData).elementAt(0);
                    	val = ((UserTableColumn)column).getDisplayValue2(data);
                    }
                    PdfPCell cell = new PdfPCell(pgh.multiFontPara(getIndents(level) + val, font));
                    cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.tableSectionHeaderClass, null));
                    cell.setColspan(getDisplayColumnCount());
                    pdfTable.addCell(cell);
                }
            }
            
            if(level == tcgroups.size() - 1){
                this.drawRows(tempData);
            }
            else{
                drawMultiGroupByTable(tcgroups, tempData, level + 1);
            }
            
            if(tempData.size() > 0){
                if (this.showSubTotals) {
                    if(column.isShowGroupSubTotal()){
                        this.deriveTotals(val, true);
                        this.drawTotal(val, true);
                    }
                }
                if (this.showDiscreteRows) {
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
                        PdfPCell cell = new PdfPCell(pgh.multiFontPara(""));
                        cell.setColspan(getDisplayColumnCount());
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
    
    public String drawRows(Collection groupRows){
        Iterator<?> rows = groupRows.iterator();
        Iterator<?> columnsIt = null;
        TableColumn column = null;
        Iterator<?> keys;
        boolean showColumn = true;
        boolean firstRow = true;
		String tableName;
		String uniqueColumn;
		
        while(rows.hasNext()){
            this.drawPageBreak();
            
            rowcount++;
            
            Object obj = rows.next();
            
            if(obj instanceof TableSectionHeader){
                
                TableSectionHeader header = (TableSectionHeader) obj;
                this.drawSectionHeader(header.getLabel());
                
                continue;
            }
            
            TableData td = (TableData) obj;
            
            if(this.columns != null){
                
                columnsIt = this.columns.iterator();
                while(columnsIt.hasNext()){
                	showColumn = true;
                    column = (TableColumn) columnsIt.next();

                    // look for an override column definition based on show criteria
                    if (column.isShowCriteriaOverride()) {
                        column = column.getOverrideColumn(td);
                    }

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
                    if(column.isDisplayed() && column.showCell(td)){
                        //String styleClass = this.getColumnStyleClass(td, column, this.getCellClass(this.darkRow));
                        //font = pgh.getCellFont(styleClass, column.getAlign(), fontSize);
                        //PdfPCell cell = new PdfPCell(pgh.multiFontPara(column.drawCSVCell(td), font));
                        //cell.setBackgroundColor(pgh.getCellBGColor(styleClass, null));
                        
                        String styleClass = this.getColumnStyleClass(td, column, this.getCellClass(this.darkRow), true);
                        PdfPCell cell = column.getPDFCell(td, styleClass, fontSize);

                        //CGriffin - If block put in so that if bg color is set and is image but no color specified then set to --. Ex: color column on material search page but no color on material.
						if((column.isImage() && FormatHelper.hasContent(column.getBgColorIndex())) && !FormatHelper.hasContent(td.getData(column.getBgColorIndex()))){
							font = pgh.getCellFont(styleClass, column.getAlign(), fontSize);
							cell = new PdfPCell(pgh.multiFontPara("--", font));
						}

						//CHUCK - If block put in so that if bg color was explicitly set by column definition, the style can not override that color
                        if(!FormatHelper.hasContent(column.getBgColorIndex()) || !FormatHelper.hasContent(td.getData(column.getBgColorIndex()))){
                            cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(styleClass, null));
                        }
                        /*
                        if (!showColumn){
                        	cell = new PdfPCell();//blank cell for RepeatedDataCell
                        } 
                        */                       
                        pdfTable.addCell(cell);
                        
                    } else if(column.isDisplayed()) {
                        String styleClass = this.getColumnStyleClass(td, column, this.getCellClass(this.darkRow));
                        font = pgh.getCellFont(styleClass, column.getAlign(), fontSize);
                        PdfPCell cell = new PdfPCell(pgh.multiFontPara(" ", font));

                        //CGriffin - If block put in so that if bg color is set and is image but no color specified then set to --. Ex: color column on material search page but no color on material.
						if((column.isImage() && FormatHelper.hasContent(column.getBgColorIndex())) && !FormatHelper.hasContent(td.getData(column.getBgColorIndex()))){
							cell = new PdfPCell(pgh.multiFontPara("--", font));
						}

						//CHUCK - If block put in so that if bg color was explicitly set by column definition, the style can not override that color
                        if(!FormatHelper.hasContent(column.getBgColorIndex()) || !FormatHelper.hasContent(td.getData(column.getBgColorIndex()))){
                            cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(styleClass, null));
                        }


                        /*
                        if (!showColumn){
                        	cell = new PdfPCell();//blank cell for RepeatedDataCell
                        } 
                        */                       
                        pdfTable.addCell(cell);
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
                        String styleClass = this.getColumnStyleClass(td, column, this.getCellClass(this.darkRow));
                        font = pgh.getCellFont(styleClass, column.getAlign(), fontSize);
                        PdfPCell cell = new PdfPCell(pgh.multiFontPara(flex.getString(key), font));
                        cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(styleClass, null));
                        pdfTable.addCell(cell);
                    }
                }
            }
            firstRow = false;
            this.darkRow = !this.darkRow;
            this.rowsDrawnSinceBreak++;
            
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
        if(!FormatHelper.hasContent(display)){
            display = "Total for " + column.getHeaderLabel() + ": ";
        }
        int total = this.getDiscreteTotalSetCount(column);
        
        this.drawPageBreak();
        
        rowcount++;
        this.rowsDrawnSinceBreak++;
        
        font = pgh.getCellFont(this.totalsClass, "left", fontSize);
        PdfPCell cell = new PdfPCell(pgh.multiFontPara(display + total, font));
        cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.totalsClass, null));
        
        pdfTable.addCell(cell);
        pdfTable.completeRow();
        return "";
    }
    
    public String drawDiscreteTotalRows(String group, TableColumn column){
        
        Iterator<?> subColumns = (new Vector(this.columns)).iterator();
        Iterator<?> subColumns2;
        TableColumn sColumn;
        TableColumn drawColumn;
        boolean reachedColumn = false;
        
        rowcount++;
        
        while(subColumns.hasNext()){
            sColumn = (TableColumn)subColumns.next();
            if(sColumn.isDiscreteCount()){
                this.drawPageBreak();
                String dClass = this.totalsClass;
                
                String display = sColumn.getDiscreteLabel();
                if(!FormatHelper.hasContent(display)){
                    display = "Total for " + sColumn.getHeaderLabel() + ": ";
                }
                int total = this.getDiscreteSetCount(sColumn, group);
                
                if(FormatHelper.hasContent(column.getSubTotalClass())){
                    dClass = column.getSubTotalClass();
                }
                
                //need to draw a row
                subColumns2 = (new Vector(this.columns)).iterator();
                reachedColumn = false;
                while(subColumns2.hasNext()){
                    drawColumn = (TableColumn)subColumns2.next();
                    if(drawColumn.isDisplayed()){
                        if(drawColumn.equals(column)){
                            font = pgh.getCellFont(dClass, "left", fontSize);
                            PdfPCell cell = new PdfPCell(pgh.multiFontPara(display + total, font));
                            cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(dClass, null));
                            
                            pdfTable.addCell(cell);
                            reachedColumn = true;
                        }
                        else{
                            //This if block is in place to allow for different style to
                            //be applied to the columns before the "reached" column and after
                            //Right now no style is applied anywhere...so it doesn't appear
                            //to make much sense to break it out....but it will
                            if(reachedColumn){
                                font = pgh.getCellFont(dClass, "left", fontSize);
                                PdfPCell cell = new PdfPCell(pgh.multiFontPara("", font));
                                cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(dClass, null));
                                
                                pdfTable.addCell(cell);
                            }
                            else{
                                font = pgh.getCellFont(this.totalsClass, "left", fontSize);
                                PdfPCell cell = new PdfPCell(pgh.multiFontPara("", font));
                                cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.totalsClass, null));
                                
                                pdfTable.addCell(cell);
                            }
                        }
                    }
                }
                this.rowsDrawnSinceBreak++;
            }
        }
        return "";
    }
    
    
    public String drawDiscreteTotal(String group){
        //Draw page header if rows has met max page size
        this.drawPageBreak();
        
        Iterator<?> columnsIt = null;
        TableColumn column = null;
        
        columnsIt = this.columns.iterator();

        String displayGroup = "";
        while (columnsIt.hasNext()) {
            
            column = (TableColumn) columnsIt.next();
            if (!column.isDisplayed()) {
                continue;
            }
            if(FormatHelper.hasContent(group)){
                //displayGroup = column.getLocalizedData(group, true);
                displayGroup = group;
            }
            if(this.groupByColumns == null || this.groupByColumns.contains(column)){
            	String subTotalDisplay = this.getSubTotalDisplay(column, group);
                if(column.isShowGroupSubTotal() && FormatHelper.hasContent(displayGroup) && displayGroup.equals(subTotalDisplay)){
                    //Now draw discrete rows
                    this.drawDiscreteTotalRows(group, column);
                }
            }
        }
        this.rowsDrawnSinceBreak++;
        return "";
    }
    
    //private String drawTotal(String group, boolean isSubTotal){
    public String drawTotal(String group, boolean isSubTotal){
    	if(!FormatHelper.hasContent(this.grandTotalLabel)){
    		this.grandTotalLabel = WTMessage.getLocalizedMessage(RB.TABLEGENERATOR, "grandTotal_LBL", RB.objA);
    	}    	
        this.drawPageBreak();
        Iterator<?> columnsIt = null;
        TableColumn column = null;
        
        if(!isSubTotal && this.drawTotalLabel){
            rowcount++;
            font = pgh.getCellFont(this.totalsClass, "left", fontSize);                      
            PdfPCell cell = new PdfPCell(pgh.multiFontPara(this.grandTotalLabel, font));
            pdfTable.addCell(cell);
            pdfTable.completeRow();
        }
        rowcount++;
        columnsIt = this.columns.iterator();
   
        while (columnsIt.hasNext()) {
            
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
                    font = pgh.getCellFont(this.totalsClass, "left", fontSize);
                    PdfPCell cell = new PdfPCell(pgh.multiFontPara(this.getSubTotalDisplay(column, group), font));
                    cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.totalsClass, null));
                    
                    pdfTable.addCell(cell);
                }
                else{
                    if(isSubTotal){
                        font = pgh.getCellFont(this.subTotalsClass, "left", fontSize);
                        PdfPCell cell = new PdfPCell(pgh.multiFontPara("", font));
                        cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.subTotalsClass, null));
                        
                        pdfTable.addCell(cell);
                    }
                    else{
                        font = pgh.getCellFont(this.totalsClass, "left", fontSize);
                        PdfPCell cell = new PdfPCell(pgh.multiFontPara("", font));
                        cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.totalsClass, null));
                        
                        pdfTable.addCell(cell);
                    }
                }
            }
            else if (column.isTotal()) {
                if(isSubTotal){
                    font = pgh.getCellFont(this.subTotalsClass, "left", fontSize);
                    PdfPCell cell = new PdfPCell(pgh.multiFontPara(this.getSubTotalDisplay(column, group), font));
                    cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.subTotalsClass, null));
                    
                    pdfTable.addCell(cell);
                }
                else{
                    font = pgh.getCellFont(this.totalsClass, "left", fontSize);
                    PdfPCell cell = new PdfPCell(pgh.multiFontPara(this.getTotalDisplay(column), font));
                    cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.totalsClass, null));
                    
                    pdfTable.addCell(cell);
                }
                
            } else {
                if(isSubTotal){
                    font = pgh.getCellFont(this.subTotalsClass, "left", fontSize);
                    PdfPCell cell = new PdfPCell(pgh.multiFontPara("", font));
                    cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.subTotalsClass, null));
                    
                    pdfTable.addCell(cell);
                }
                else{
                    font = pgh.getCellFont(this.totalsClass, "left", fontSize);
                    PdfPCell cell = new PdfPCell(pgh.multiFontPara("", font));
                    cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.totalsClass, null));
                    
                    pdfTable.addCell(cell);
                }
                
            }
        }
        
        this.subTotalsClass = this.totalsClass;
        //        this.rowsDrawnSinceBreak++;
        
        return "";
    }
    
    protected String drawCounts(String group, boolean isSubTotal){

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
        rowcount++;
        font = pgh.getCellFont(this.tableSectionHeaderClass, "left", fontSize);
        PdfPCell cell = new PdfPCell(pgh.multiFontPara(FormatHelper.format(label), font));
        cell.setBackgroundColor(PDFGeneratorHelper.getCellBGColor(this.tableSectionHeaderClass, null));
        
        cell.setColspan(getDisplayColumnCount());
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        pdfTable.addCell(cell);
        
        return "";
    }
    private void intializePDF() throws WTException{
        try{
            this.filename = HBIPDFUtils.generateStubName(this.reportName, ClientContext.getContext().getUser().getName().toString());
            this.filename = FormatHelper.formatRemoveProblemFileNameChars(this.filename);
            String fName = FileLocation.PDFDownloadLocationFiles + HBIPDFUtils.generateFileName(filename);
            
            FileOutputStream fos = new FileOutputStream(fName);
            
            doc = new Document();
            HBIPDFUtils.setPrintOptions(doc, this.paperSize, this.landscape);            
            pdfTable = new PdfPTable(getDisplayColumnCount());
            
            
            PdfWriter pw = PdfWriter.getInstance(doc, fos);       
        }
        catch(Exception e){
            throw new WTException(e);
        }
    }
 
    public void setPDFTableHeaderGenerator(PDFTableHeaderGenerator pthg){
        this.pthg = pthg;
    }
    
    public PDFTableHeaderGenerator getPDFTableHeaderGenerator(){
        return this.pthg;
    }
    
    public void setPDFGeneratorHelper(PDFGeneratorHelper pgh){
        this.pgh = pgh;
    }
    
    public PDFGeneratorHelper getPDFGeneratorHelper(){
        return this.pgh;
    }
    
    
    public void setLandscape(boolean landscape){
        this.landscape = landscape;
    }
    public boolean isLandscape(){
        return this.landscape;
    }
    
    public void setPaperSize(int paperSize){
        this.paperSize = paperSize;
    }
    public int getPaperSize(){
        return this.paperSize;
    }
    
    public float getPageHeight(){
        return pageHeight;
    }
    
    public void setPageHeight(float pageHeight){
        this.pageHeight = pageHeight;
    }
    
    protected String drawPageBreak(){
        if(this.usePageBreaks && (this.pageBreakRows > 0 && this.rowsDrawnSinceBreak >= this.pageBreakRows)){
            try{
                //draw page break
                //Close the current table/add it to the document
                doc.add(pdfTable);
                
                //put a page break in the document
                doc.newPage();
                
                //Add a new page header if necassary
                drawHeader();
                
                //create a new Table
                pdfTable = new PdfPTable(getDisplayColumnCount());
            }
            catch(Exception e){
                e.printStackTrace();
            }
            
            //add the table headers
            this.drawContentColumnHeaders("T"+this.getTableId());
            
            this.rowsDrawnSinceBreak = 0;
        }
        else if(this.repeatHeader && (this.repeatHeaderCount > 0 && this.rowsDrawnSinceBreak >= this.repeatHeaderCount)){
            //draw column headers
            this.drawContentColumnHeaders("T"+this.getTableId());
            this.rowsDrawnSinceBreak = 0;
        }
        return "";
    }
    
    public void setTitleCell(PdfPCell titleCell){
        this.titleCell = titleCell;
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
    
    public float[] getWidths(){
        float[] widths = new float[this.getDisplayColumnCount()];
        Iterator<?> i = this.columns.iterator();
        int place = 0;
        while(i.hasNext()){
            TableColumn column = (TableColumn) i.next();
            if(column.isDisplayed()){
                widths[place] = column.getPdfColumnWidthRatio();
                place++;
            }
        }
        
        return widths;
    }

    public String getColumnDisplay(TableColumn col, TableData td){
        return col.getPDFDisplayValue(td);
    }
    
}
