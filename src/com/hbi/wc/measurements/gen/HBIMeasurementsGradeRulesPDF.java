/*
 * HBIMeasurementsGradeRulesPDF.java
 *
 * Created on May 8, 2019, 10.41 AM
 */

package com.hbi.wc.measurements.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.util.WTProperties;
import com.infoengine.object.factory.Att;
import com.infoengine.object.factory.Element;
import com.infoengine.object.factory.Group;
import com.infoengine.object.factory.Param;
import com.infoengine.object.factory.Task;
import com.infoengine.object.factory.Webject;

import com.lcs.wc.client.web.FlexTypeGenerator;
import com.lcs.wc.client.web.TableColumn;
import com.lcs.wc.client.web.pdf.PDFTableGenerator;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.measurements.LCSFindMeasurementsDelegate;
import com.lcs.wc.measurements.LCSMeasurements;
import com.lcs.wc.measurements.LCSMeasurementsQuery;
import com.lcs.wc.measurements.MeasurementsFlexTypeScopeDefinition;
import com.lcs.wc.measurements.gen.MeasurementsPDFContentGenerator;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.product.PDFProductSpecificationGenerator2;
import com.lcs.wc.product.PDFProductSpecificationMeasurements2;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.RB;
import com.lcs.wc.util.SortHelper;
import com.lcs.wc.util.VersionHelper;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;

import com.ptc.core.adapter.server.impl.TypeAwareWebjectDelegate;
import com.ptc.core.adapter.server.impl.WebjectDelegateFactory;
import com.lcs.wc.infoengine.client.web.IEClientHelper;
import java.io.IOException;




/** Writes the Measurements Grade Rules section of a PDF Product Specification
 * @author  UST
 */
public class HBIMeasurementsGradeRulesPDF extends MeasurementsPDFContentGenerator{
    
    public static final String PRODUCT_ID = "PRODUCT_ID";
    public static final String SPEC_ID = "SPEC_ID";
    public static final String MEASUREMENTS_ID = "MEASUREMENTS_ID";
    
    public static final String HEADER_HEIGHT = "HEADER_HEIGHT";
    
    public static final String REPEAT = "REPEAT";
    
    public float pageHeight = 0;
    
    public static final int MEASUREMENTS_DISTRIBUTION_THRESHOLD = FormatHelper.parseInt(LCSProperties.get("ProductSpecification.measurementsDistributionThreshold", "5"));
    public int measurementsSizeWrapLimit = 0;
    private static final boolean DEBUG = LCSProperties.getBoolean("com.lcs.wc.measurements.gen.MeasurementsGradingPDF.verbose");
	private static final int DEBUG_LEVEL = Integer.parseInt(LCSProperties.get("com.lcs.wc.measurements.gen.MeasurementsGradingPDF.verboseLevel", "1"));
    private static final int PRECISION = FormatHelper.parseInt(LCSProperties.get("com.lcs.wc.measurements.PRECISION", "3"));
	private static final String defaultUnitOfMeasure = LCSProperties.get("com.lcs.measurements.defaultUnitOfMeasure", "si.Length.in");


    private static final String MEASUREMENTS_GRADING_ATT_COLUMN_ORDER = LCSProperties.get("com.lcs.wc.measurements.gen.MeasurementsGradingPDF.AttributeColumnsOrder");
    private static final String MEASUREMENTS_DISPLAY_MODE = LCSProperties.get("com.hbi.wc.measurements.gen.HBIMeasurementsGradeRulesPDF.DisplayMode");	

    
    /** Creates a new instance of HBIMeasurementsGradeRulesPDF */
    public HBIMeasurementsGradeRulesPDF() {
    }
    
    /** returns the Collection of PdfPTables containing the Measurements for the specification
     * @param params
     * @param document
     * @throws WTException
     * @return  */
    public Collection getPDFContentCollection(Map params, Document document) throws WTException {
        try{
            WTObject obj = (WTObject)LCSProductQuery.findObjectById((String)params.get(PRODUCT_ID));
            if(!(obj instanceof LCSProduct)){
                throw new WTException("Can not use PDFProductSpecificationMeasurements on a non-LCSProduct - " + obj);
            }
            
            WTObject obj2 =  (WTObject)LCSQuery.findObjectById((String)params.get(PDFProductSpecificationGenerator2.COMPONENT_ID));
            if(obj2 == null || !(obj2 instanceof LCSMeasurements)){
                throw new WTException("Can not use PDFProductSpecificationFitSpec on without a Measurements - " + obj2);
            }
            
            measurementsSizeWrapLimit = ((Integer)params.get(PDFProductSpecificationGenerator2.SIZES_PER_PAGE)).intValue();
            
            this.pageHeight = this.calcPageHeight(params, document);
            
            LCSMeasurements measurements = (LCSMeasurements)obj2;
            
            PDFTableGenerator tg = null;
            Collection content = new ArrayList();
            Collection spcontent = new ArrayList();
            Collection sectionPageTitles = new ArrayList();
            String pageTitle = WTMessage.getLocalizedMessage( RB.MEASUREMENTS, "gradingReport_TLE", RB.objA )+ " Rules >>> " +  measurements.getValue("name");
            
			
			Collection poms = getPOMs(measurements, MEASUREMENTS_DISPLAY_MODE);

            Map columnMap = getColumns(measurements, params);

            Collection columns = null;
            Vector keys = new Vector(columnMap.keySet());
            if(columnMap.size()>1)
            	keys.remove(REPEAT);

            Collections.sort(keys);
            Iterator k = keys.iterator();
            String title = WTMessage.getLocalizedMessage( RB.MEASUREMENTS, "measurement_LBL", RB.objA ) + "Grade Rules - " + measurements.getValue("name")+
            "    " +  WTMessage.getLocalizedMessage( RB.MEASUREMENTS, "sampleSizeColon_LBL", RB.objA ) +measurements.getSampleSize();
          
		    String uom = defaultUnitOfMeasure;
			uom = uom.substring(uom.lastIndexOf(".") + 1);
           
			if(measurements.getValue("uom") != null){
				uom = (String)measurements.getValue("uom");
			}


			if(params.get(PDFProductSpecificationMeasurements2.UOM) != null){
				String pdfUom = (String)params.get(PDFProductSpecificationMeasurements2.UOM);
				if(!(pdfUom).equals("none") && pdfUom.startsWith("si.Length")){
				    uom = pdfUom;
					uom = uom.substring(uom.lastIndexOf(".") + 1);
				}else if(pdfUom.equals(FormatHelper.FRACTION_FORMAT)){
                    String fractionLabel = WTMessage.getLocalizedMessage(RB.MEASUREMENTS, "fraction_LBL", RB.objA);
                    uom = fractionLabel + " " + defaultUnitOfMeasure.substring(defaultUnitOfMeasure.lastIndexOf(".") + 1);
				}
			}      
			
			title = title + "    " + measurements.getFlexType().getAttribute("uom").getAttDisplay() + ": " + uom;

            while(k.hasNext()){
                String key = (String)k.next();
                if(DEBUG){LCSLog.debug("key="+key);}
                columns = new ArrayList();
                if(columnMap.size()>1)
                	columns.addAll((Collection)columnMap.get(REPEAT));
                columns.addAll((Collection)columnMap.get(key));
                
                tg = new PDFTableGenerator(document);
                tg.cellClassLight = "RPT_TBL";
                tg.cellClassDark = "RPT_TBD";
                tg.tableSubHeaderClass = "RPT_HEADER";
                tg.tableHeaderClass = "TABLE-HEADERTEXT";
                
                tg.setTitle(title);
                spcontent.addAll(tg.drawTables(poms, columns));
                sectionPageTitles.add(pageTitle);
            }	
            PdfPTable fullTable = new PdfPTable(1);
            PdfPTable e = null;
            PdfPCell cell = null;
            
            //Header
            if(FormatHelper.parseBoolean((String)params.get(PDFProductSpecificationMeasurements2.PRINT_MEAS_HEADER))){
                Collection MeasHeaderAtts = (Collection)params.get(PDFProductSpecificationMeasurements2.MEAS_HEADER_ATTS);
                if(MeasHeaderAtts != null && !MeasHeaderAtts.isEmpty()){
                    if(PDFProductSpecificationMeasurements2.MEAS_HEADER_SAME_PAGE){
                        for(Iterator HeaderI = MeasHeaderAtts.iterator(); HeaderI.hasNext();){
                            e = (PdfPTable)HeaderI.next();
                            cell = new PdfPCell(e);
                            fullTable.addCell(cell);
                        }
                    } else {
                        content.addAll(MeasHeaderAtts);
                        this.pageTitles.addAll((Collection)params.get(PDFProductSpecificationMeasurements2.MEAS_HEADER_PAGE_TITLES));
                    }
                }
            }

            //Add report sections
            Collection Footer = (Collection)params.get(PDFProductSpecificationMeasurements2.MEAS_FOOTER_ATTS);
            boolean usingFooter = (Footer != null && !Footer.isEmpty() && FormatHelper.parseBoolean((String) params.get(PDFProductSpecificationMeasurements2.PRINT_MEAS_FOOTER)) );
            debug(2, "MEAS_ON_SINGLE_PAGE");
            Iterator sci = spcontent.iterator();
            while(sci.hasNext()){
                e = (PdfPTable )sci.next();
                cell = new PdfPCell(e);
                fullTable.addCell(cell);
            }
            
            //Add Footer
            if (usingFooter){
                debug(2, "usingFooter");
                if(PDFProductSpecificationMeasurements2.MEAS_FOOTER_SAME_PAGE){
                    for(Iterator footI = Footer.iterator();footI.hasNext();) {
                        e = (PdfPTable)footI.next();
                        cell =new PdfPCell(e);
                        fullTable.addCell(cell);
                    }
                    //Add BOM to content
                    content.add(fullTable);
                    this.pageTitles.add(pageTitle);
                }else{
                    //Add BOM to content
                    content.add(fullTable);
                    this.pageTitles.add(pageTitle);
                    //Add Footer to content
                    content.addAll(Footer);
                    this.pageTitles.addAll((Collection)params.get(PDFProductSpecificationMeasurements2.MEAS_FOOTER_PAGE_TITLES) );
                }
            } else {
                content.add(fullTable);
                this.pageTitles.add(pageTitle);
            }
            
            return content;
        }
        catch(Exception e){
            e.printStackTrace();
            throw new WTException(e);
        }
    }
    
    private float calcPageHeight(Map params, Document doc) throws WTException{
        float height = doc.top() - doc.bottom();
        
        if(params.get(HEADER_HEIGHT) != null){
            Object hh = params.get("HEADER_HEIGHT");
            if(hh instanceof Float){
                height = height - ((Float)hh).floatValue();
            }
            if(hh instanceof String){
                height = height - (new Float((String)hh)).floatValue();
            }
        }
        
        return height;
    }
    
    /** Gets the Collection of measurements objects for the given Product
     * @param product
     * @throws WTException
     * @return  */
    public Collection getMeasurements(LCSProduct product, FlexSpecification spec) throws WTException{
        Collection results = LCSMeasurementsQuery.findMeasurmentsForProduct(product, spec).getResults();
        
        Collection ms = LCSQuery.getObjectsFromResults(results, "VR:com.lcs.wc.measurements.LCSMeasurements:", "LCSMEASUREMENTS.BRANCHIDITERATIONINFO");
        
        return ms;
    }
    
    /** Gets the Points of Measure for the given Measurements object
     * @param measurements
     * @throws WTException
     * @return  */
    public Collection getPOMs(LCSMeasurements measurements, String displayMode) throws WTException{
        try{
            Collection data = findPOM(FormatHelper.getObjectId(measurements), null, displayMode);
            data = SortHelper.sortFlexObjectsByNumber(data, "sortingNumber");            
            return data;
        }
        catch(Exception e){
            throw new WTException(e);
        }
    }	

	public static Collection findPOM(String oid, String timestamp, String displayMode) throws Exception {
		Group POM = runFindPOMWebject(oid, timestamp, displayMode);
		Collection data = IEClientHelper.groupToTableData(POM);
		return data;
	}

	private static Group runFindPOMWebject(String oid, String timestamp, String displayMode) throws WTException {
		Group resultsGroup = null;

		try {
			String instance = WTProperties.getLocalProperties().getProperty("wt.federation.ie.VMName");
			Webject findPomWebject = new Webject("FIND_MEASUREMENTS");
			findPomWebject.addParam(new Param("INSTANCE", instance));
			findPomWebject.addParam(new Param("OID", oid));
			findPomWebject.addParam(new Param("EFFECTIVEDATE", timestamp));
			findPomWebject.addParam(new Param("GROUP_OUT", "results"));
			findPomWebject.addParam(new Param("DISPLAYMODE", displayMode));
			TypeAwareWebjectDelegate delegate = WebjectDelegateFactory.getDelegate(findPomWebject);
			Task inTask = new Task(findPomWebject);
			Task outTask = delegate.invoke(inTask);
			resultsGroup = outTask.getVdb("results");
		} catch (IOException var8) {
			var8.printStackTrace();
		}

		return resultsGroup;
	}
    
    /** Gets the Collection of TableColumns for displaying the Measurements in the Product Specification
     *
     * @param measurements
     * @throws WTException
     * @return  */
    public Map getColumns(LCSMeasurements measurements, Map params) throws WTException{
        HashMap columnsMap = new HashMap();
        
        TableColumn column;
        
        Collection sizeRun = (Collection)params.get(PDFProductSpecificationGenerator2.SIZES1);
        String sampleSize =  null;
        
        String doubleFormat = defaultUnitOfMeasure;
	   
        if(measurements.getValue("uom") != null){
                doubleFormat = (String)measurements.getValue("uom");
                doubleFormat = "si.Length." + doubleFormat;
        }


        if(params.get(PDFProductSpecificationMeasurements2.UOM) != null){
                if(!((String)params.get(PDFProductSpecificationMeasurements2.UOM)).equals("none")){
                        doubleFormat = (String)params.get(PDFProductSpecificationMeasurements2.UOM);
                }
        }      



        if (measurements.getSampleSize() != null) {
            sampleSize =  measurements.getSampleSize();
        }
        
        
        Collection repeatingColumns = new ArrayList();
        FlexTypeGenerator flexg = new FlexTypeGenerator();
        flexg.setScope(MeasurementsFlexTypeScopeDefinition.MEASUREMENT_SCOPE);

        //Populate Columns based on property entries
        
       StringTokenizer parser = new StringTokenizer(MEASUREMENTS_GRADING_ATT_COLUMN_ORDER, ",");
       FlexType measurementsType = measurements.getFlexType();
       FlexTypeAttribute placeholderRowAtt = measurementsType.getAttribute("placeholderRow");
       
        while(parser.hasMoreTokens()){
            String attString = parser.nextToken();
            StringTokenizer flexTypeToken = new StringTokenizer(attString, "|");
            String attName = flexTypeToken.nextToken();
            float columnSize = (new Double(flexTypeToken.nextToken())).floatValue();
            
            if("SIZES".equals(attName)){
                Iterator runs = getSizeRun(sizeRun, sampleSize).iterator();
                int sizeSet = 1;
                String sizePrefix = "SIZE";
                while(runs.hasNext()){
                    Collection run = (Collection)runs.next();
                    Iterator sizeRunIter = run.iterator();
                    String size;

                    Collection columns = new ArrayList();

                    while(sizeRunIter.hasNext()){
                        size = (String) sizeRunIter.next();
                        column = new TableColumn();
                        column.setPdfColumnWidthRatio(columnSize);

                        if(size.equals(sampleSize)){

                            // NULL_VALUE_PLACEHOLDER Substitution handled here
                            column.setShowCriteria(LCSMeasurementsQuery.NULL_VALUE_PLACEHOLDER);
                            column.setShowCriteriaNot(true);
                            column.setShowCriteriaTarget("size_"+size);
                            column.setShowCriteriaNumericCompare(true);

                            // Handle highlighting 
                            column.setColumnClassIndex("highLight");

                            column.setDisplayed(true);
                            column.setHeaderLabel(size);
                            column.setTableIndex("grade_" + size);
                            column.setColumnWidth("1%");
                            column.setWrapping(false);
                            column.setAlign("right");
                            column.setHeaderAlign("right");
                            column.setDecimalPrecision(3);
                            column.setAttributeType("float");
                            // Hi-lite the Sample Size Column
                            column.setColumnHeaderClass("TABLESUBHEADER_SPECIAL");
                            column.setColumnClass("sample");

                        } else {

                            // NULL_VALUE_PLACEHOLDER Substitution handled here
                            column.setShowCriteria(LCSMeasurementsQuery.NULL_VALUE_PLACEHOLDER);
                            column.setShowCriteriaNot(true);
                            column.setShowCriteriaTarget("size_"+size);
                            column.setShowCriteriaNumericCompare(true);

                            // Handle highlighting 
                            column.setColumnClassIndex("highLight");

                            column.setDisplayed(true);
                            column.setHeaderLabel(size);
                            column.setTableIndex("grade_" + size);
                            column.setColumnWidth("1%");
                            column.setWrapping(false);
                            column.setAlign("right");
                            column.setHeaderAlign("right");
                            column.setDecimalPrecision(3);
                            column.setAttributeType("float");
                        }
                        column.setDecimalPrecision(PRECISION);
                        if (doubleFormat.startsWith("si.Length.")) {
                            column.setOutputUom(doubleFormat);
                            column.setFormat(FormatHelper.MEASUREMENT_UNIT_FORMAT);
                        } else {
                            column.setFormat(doubleFormat);
                        }
                        columns.add(column);
                    }

                    columnsMap.put(sizePrefix + sizeSet, columns);
                    sizeSet++;
                }
            }else{
                FlexTypeAttribute att = measurementsType.getAttribute(attName);
                column = flexg.createTableColumn(att);
    			if("object_ref".equals(att.getAttVariableType()) || "object_ref_list".equals(att.getAttVariableType())){
    				column.setTableIndex(att.getAttKey() + "Display");
    				column.setLinkTableIndex(att.getAttKey());
    			}else{
    				column.setTableIndex(att.getAttKey());
    			}
                column.setColumnClassIndex("highLight"); // handle highlighting
                column.setPdfColumnWidthRatio(columnSize);
                column.setWrapping(true); 

				if("measurementName".equals(att.getAttKey())){
                    column.setColumnWidth("5%");
				}else{
					column.setShowCriteria("1.0");
					column.setShowCriteriaNot(true);
					column.setShowCriteriaTarget(placeholderRowAtt.getAttKey());
					column.setShowCriteriaNumericCompare(true);
				}
                
                if("number".equals(att.getAttKey())){
                    column.setColumnWidth("1%");
                    column.setWrapping(false);
                
                }else if("plusTolerance".equals(attName) || "minusTolerance".equals(attName)){
					// NULL_VALUE_PLACEHOLDER Substitution handled here
					column.setShowCriteria(LCSMeasurementsQuery.NULL_VALUE_PLACEHOLDER);
					column.setShowCriteriaNot(true);
					column.setShowCriteriaTarget(att.getAttKey());
					column.setShowCriteriaNumericCompare(true);
					column.setDecimalPrecision(PRECISION);
					if (doubleFormat.startsWith("si.Length.")) {
						column.setOutputUom(doubleFormat);
						column.setFormat(FormatHelper.MEASUREMENT_UNIT_FORMAT);
					} else {
						column.setFormat(doubleFormat);
					}				
				}
                
                if("image".equals(att.getAttVariableType())){
                	column.setShowFullImage(true);
                }

                
                repeatingColumns.add(column);
            }
        }
       
        columnsMap.put(REPEAT, repeatingColumns);

        
        return columnsMap;
    }
   
    /** gets the Collection of page titles for each of the Measurements pages returned
     * @return  */
    public Collection getPageHeaderCollection() {
        return pageTitles;
    }
    
    public Collection getSizeRun(Collection sizeRun, String sampleSize){
        Collection runs = new ArrayList();
        
        Collection frontHalfSizeRun = null;
        Collection secondHalfSizeRun = null;
        
        if(sizeRun.size() > measurementsSizeWrapLimit && (measurementsSizeWrapLimit !=0)){
            boolean passedSampleSize = false;
            frontHalfSizeRun = new ArrayList();
            secondHalfSizeRun = new ArrayList();
            Iterator sizeIter = sizeRun.iterator();
            String size;
            while(sizeIter.hasNext()){
                size = (String) sizeIter.next();
                if(size.equals(sampleSize)){
                    passedSampleSize = true;
                    frontHalfSizeRun.add(size);
                    secondHalfSizeRun.add(size);
                } else if(passedSampleSize){
                    secondHalfSizeRun.add(size);
                } else {
                    frontHalfSizeRun.add(size);
                }
            }
            
            // CHECK TO SEE IF THE DIST IS LOPSIDED
            
            int sizeDif = Math.abs(frontHalfSizeRun.size() - secondHalfSizeRun.size());
            if(sizeDif >= MEASUREMENTS_DISTRIBUTION_THRESHOLD){
                // IF LOPSIDED, DISTRIBUTE BASED ON THE MIDDLE SIZE
                // RATHER THAN THE SAMPLE SIZE
                frontHalfSizeRun = new ArrayList();
                secondHalfSizeRun = new ArrayList();
                sizeIter = sizeRun.iterator();
                int counter = 0;
                while(sizeIter.hasNext()){
                    counter++;
                    size = (String) sizeIter.next();
                    if(size.equals(sampleSize)){
                        secondHalfSizeRun.add(size);
                        frontHalfSizeRun.add(size);
                    }else{
	                    if(counter > (sizeRun.size()/2)){
	                        secondHalfSizeRun.add(size);
	                    } else {
	                        frontHalfSizeRun.add(size);
	                    }
                    }
                }
            }
            runs.add(frontHalfSizeRun);
            runs.add(secondHalfSizeRun);
        }
        else{
            runs.add(sizeRun);
        }
        
        return runs;
    }
    /////////////////////////////////////////////////////////////////////////////
   public static void debug(String msg){debug(msg, 1); }
   public static void debug(int i, String msg){debug(msg, i); }
   public static void debug(String msg, int i){
	  if(DEBUG && i <= DEBUG_LEVEL) LCSLog.debug(msg);
   }
    
}
