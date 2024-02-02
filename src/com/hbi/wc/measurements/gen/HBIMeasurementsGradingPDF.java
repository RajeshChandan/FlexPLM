 package com.hbi.wc.measurements.gen;

	import com.lcs.wc.measurements.LCSMeasurements;
	import com.lcs.wc.measurements.LCSMeasurementsQuery;
	import com.lcs.wc.product.LCSProduct;
	import com.lcs.wc.product.LCSProductQuery;
	import com.lcs.wc.product.PDFProductSpecificationGenerator2;
	import com.lcs.wc.product.PDFProductSpecificationMeasurements2;
	import com.lcs.wc.specification.FlexSpecification;
	import com.lcs.wc.util.FormatHelper;
	import com.lcs.wc.util.MOAHelper;
	import com.lcs.wc.flextype.FlexType;
	import com.lcs.wc.flextype.FlexTypeAttribute;
	import com.lcs.wc.foundation.LCSQuery;
	import com.lcs.wc.client.web.TableColumn;
	import com.lcs.wc.client.web.FlexTypeGenerator;
	
	import java.util.ArrayList;
	import java.util.Collection;
	import java.util.Collections;
	import java.util.HashMap;
	import java.util.Iterator;
	import java.util.Map;
	import java.util.StringTokenizer;
	import java.util.Vector;
	import wt.fc.WTObject;
	import wt.method.RemoteMethodServer;
	import wt.util.WTException;
	import wt.util.WTMessage;
	import com.lowagie.text.Document;
	import com.lcs.wc.util.LCSProperties;
	import com.lcs.wc.measurements.gen.*;
	import com.lcs.wc.measurements.MeasurementsFlexTypeScopeDefinition;



   // This is for Measurements columns on a PDF Product Specification
   //In this file Only "getColumns" is  method overriden for Hbi customization

   public class HBIMeasurementsGradingPDF extends MeasurementsGradingPDF {

		private static final int PRECISION = FormatHelper.parseInt(LCSProperties.get("com.lcs.wc.measurements.PRECISION", "3"));
		private static final String MEASUREMENTS_GRADING_ATT_COLUMN_ORDER = LCSProperties.get("com.lcs.wc.measurements.gen.MeasurementsGradingPDF.AttributeColumnsOrder");
		public int measurementsSizeWrapLimit = 0;
		private static final String defaultUnitOfMeasure = LCSProperties.get("com.lcs.measurements.defaultUnitOfMeasure", "si.Length.in");
	  
		public HBIMeasurementsGradingPDF (){
			super();
		}

		public Collection getPDFContentCollection(Map params, Document document) throws WTException {

			Collection coll = super.getPDFContentCollection(params, document);
			return coll;

		}

	/** Gets the Collection of TableColumns for displaying the Measurements in the Product Specification
      * @param measurements
	  * @throws WTException
	  * @return 
	  */
    public Map getColumns(LCSMeasurements measurements, Map params) throws WTException {
		
		HashMap columnsMap = new HashMap();
        TableColumn column;
        
        Collection sizeRun = (Collection)params.get(PDFProductSpecificationGenerator2.SIZES1);
        if(sizeRun == null || sizeRun.size() == 0){
            sizeRun = MOAHelper.getMOACollection(measurements.getSizeRun());
        }
        String sampleSize =  null;
        
        //String doubleFormat = FormatHelper.FLOAT_FORMAT_NO_PARENS;
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
        
        boolean needToSplit = false;
        boolean splitOnSample = false;
        if(sizeRun.size() > measurementsSizeWrapLimit && (measurementsSizeWrapLimit != 0 ) ){
            //Need to break the table
            needToSplit = true;
            
        }
              
        Collection repeatingColumns = new ArrayList();
        FlexTypeGenerator flexg = new FlexTypeGenerator();
        flexg.setScope(MeasurementsFlexTypeScopeDefinition.MEASUREMENT_SCOPE);

        //Populate Columns based on property entries
        
       StringTokenizer parser = new StringTokenizer(MEASUREMENTS_GRADING_ATT_COLUMN_ORDER, ",");
        while(parser.hasMoreTokens()){
            String attString = parser.nextToken();
            StringTokenizer flexTypeToken = new StringTokenizer(attString, "|");
            FlexType measurementsType = measurements.getFlexType();
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
                        String tempSize = FormatHelper.formatJavascriptObjectName(size);
                        column = new TableColumn();
                        column.setPdfColumnWidthRatio(columnSize);

                        if(tempSize.equals(sampleSize)){

                            // NULL_VALUE_PLACEHOLDER Substitution handled here
                            column.setShowCriteria(LCSMeasurementsQuery.NULL_VALUE_PLACEHOLDER);
                            column.setShowCriteriaNot(true);
                            column.setShowCriteriaTarget("size_"+size);
                            column.setShowCriteriaNumericCompare(true);

                            // Handle highlighting 
                            column.setColumnClassIndex("highLight");

                            column.setDisplayed(true);
                            column.setHeaderLabel(size);
                            column.setTableIndex("size_" + size);
                            column.setColumnWidth("1%");
                            column.setWrapping(false);
                            column.setAlign("right");
                            column.setHeaderAlign("right");
                            column.setDecimalPrecision(3);
                            //column.setFormat(FormatHelper.FLOAT_FORMAT_NO_PARENS);
                            //column.setFormat(doubleFormat);
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
                            column.setTableIndex("size_" + size);
                            column.setColumnWidth("1%");
                            column.setWrapping(false);
                            column.setAlign("right");
                            column.setHeaderAlign("right");
                            column.setDecimalPrecision(3);
                            //column.setFormat(FormatHelper.FLOAT_FORMAT_NO_PARENS);
                            //column.setFormat(doubleFormat);
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
                column = new TableColumn();
                //FlexTypeAttribute att = measurementsType.getAttribute(attName);
                //column = flexg.createTableColumn(att);

				// added for Hbi
				  //System.out.println("FullName***: "+ measurementsType.getFullNameDisplay(false));
				  FlexTypeAttribute att;
                  if("Pattern Measurements".equalsIgnoreCase(measurementsType.getFullNameDisplay(false)) ){
						att = measurementsType.getAttribute(attName);
						column = flexg.createTableColumn(att);
				  }else{
						
						if(attName.equalsIgnoreCase("hbiPatternPiece") || attName.equalsIgnoreCase("hbiPiecesPerGarment"))
							continue;
						att = measurementsType.getAttribute(attName);
						column = flexg.createTableColumn(att);
				  }

				  // end

                
                column.setTableIndex(att.getAttKey());
                column.setColumnClassIndex("highLight"); // handle highlighting
                column.setPdfColumnWidthRatio(columnSize);
                column.setWrapping(true); 
                
                if("number".equals(att.getAttKey())){
                    column.setColumnWidth("1%");
                    column.setWrapping(false);
                }else if("measurementName".equals(att.getAttKey())){
                    column.setColumnWidth("5%");
                }else if("plusTolerance".equals(attName) || "minusTolerance".equals(attName)){
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

  
   }// class end