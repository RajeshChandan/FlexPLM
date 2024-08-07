package com.sportmaster.wc.reports;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.client.web.TableColumn;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.util.FormatHelper;
import com.ptc.core.meta.common.FloatingPoint;
import com.ptc.core.meta.container.common.LWCFormula;

/**
 * 
 * @author 'true' BSC -PTC.
 * @version 'true' 1.0 version number.
 */
public final class SMMaterialForecastReportDocumentHelper {

	private static final String EMPTY_STRING = "";
	/**
	 * LOGGER .
	 */
	private static final org.apache.log4j.Logger LOGGER=Logger.getLogger("MFDRLOG");

	//adding private constructor. 
	private SMMaterialForecastReportDocumentHelper(){

	}


	/**
	 * Method buildDataFromColIndex - buildDataFromColIndex.
	 * @param reportBean the reportBean.
	 * @param quantityAttColumn the quantityAttColumn.
	 * @param fo the fo.
	 * @param yield the yield.
	 * @param lsData the lsData.
	 * @throws WTException the WTException.
	 */
	public static void buildDataFromColIndex(
			SMMaterialForecastReportBean reportBean, String quantityAttColumn,
			FlexObject fo, double yield, FlexObject lsData,String destName,String dest2,String dest3) throws WTException {

		for (String colIndex : reportBean.getFormulaeBasedColKeys()) { 
			com.singularsys.jep.Jep jep = LWCFormula.getLWCFormulaJep();
			final Set<String> unwrappedVarNames = com.ptc.core.lwc.common.JepHelper.getVariables(colIndex);
			if(colIndex.contains("/")){
				double value=fo.getDouble(quantityAttColumn); 
				if(value==0.0 || value==-0.0){
					continue;
				}
			}

			executeColumnDataCalculation(
					reportBean, quantityAttColumn, fo, lsData,
					colIndex, jep, unwrappedVarNames,destName,dest2,dest3);

			fo.put(quantityAttColumn,yield);
		}

	}


	/**
	 * Method executeColumnDataCalculation.
	 * @param reportBean the reportBean.
	 * @param quantityAttColumn the quantityAttColumn.
	 * @param fo the fo.
	 * @param lsData the lsData.
	 * @param colIndex the colIndex.
	 * @param jep the jep.
	 * @param unwrappedVarNames the unwrappedVarNames.
	 */
	public static void executeColumnDataCalculation(
			SMMaterialForecastReportBean reportBean, String quantityAttColumn,
			FlexObject fo, FlexObject lsData, String colIndex, com.singularsys.jep.Jep jep,
			final Set<String> unwrappedVarNames,String destName1,String dest2,String dest3) {
		//Get the yield value for the overridden values by Destination

		for (final String unwrappedVarname : unwrappedVarNames) {
			String indx= unwrappedVarname;

			TableColumn column=  reportBean.getColumnsMap().get(indx);	
			if(column==null){
				continue;
			}

			String tableIndx=column.getTableIndex();
//			if(column.getHeaderLabel().equals(destName1) || column.getHeaderLabel().equals(dest2)
//					|| column.getHeaderLabel().equals(dest3)){

				Object value=fo.getDouble(tableIndx);		

				if (value != null) {					
					//Converting the value to float for accuracy in calculation
					value = new FloatingPoint(((Number) value).doubleValue(),0);
				
					Set<String> allVarnames;
					try {
						allVarnames = LWCFormula.getVariables(unwrappedVarname);

						//Set the value for the field in the formula
						for (final String varname : allVarnames) {
							if(indx.equals(varname)){
								jep.addVariable(varname, value);
							}
						}
					} catch (com.singularsys.jep.JepException e) {
						e.printStackTrace();
					}

				}
			//}
		} 

		try {
			jep.parse(colIndex);
			Object value = jep.evaluate();

			parseAndSetCalculatedValue(fo, colIndex, value);

		} catch (com.singularsys.jep.ParseException e) {
			LOGGER.debug("Error while parsing formula parameters - "+e.getMessage());
			fo.put(colIndex, 0);

		}catch(com.singularsys.jep.EvaluationException e){
			LOGGER.debug("Error while evaluating formula parameters - "+e.getMessage());
			fo.put(colIndex, 0);

		}catch(ArithmeticException e){
			LOGGER.debug("Error while executing formula parameters - "+e.getMessage());
			fo.put(colIndex, 0);

		}
	}


	/**
	 * @param lsData
	 * @param colIndex
	 * @param value
	 */
	private static void parseAndSetCalculatedValue(FlexObject lsData,
			String colIndex, Object value) {
		Double dv = null;
		 

		//Parse the value for double
		if (value instanceof Number) {
			dv = Double.valueOf(""+value);
		}
		if (dv==null || Double.isNaN(dv) || Double.isInfinite(dv)) {
			lsData.put(colIndex, 0);
		}else{
			//Format the value for 2 decimal point.
			lsData.put(colIndex,dv);
		}
	}



	/**
	 * Method createReportDocumentInLibrary - createReportDocumentInLibrary.
	 * @param criteria the criteria.
	 * @param fileURL the fileURL.
	 * @param reportBean  the SMMaterialForecastReportBean
	 * @throws WTException the WTException.
	 * @throws WTPropertyVetoException the WTPropertyVetoException.
	 * @throws ParseException 
	 */
	public static void createReportDocumentInLibrary(Map criteria, String fileURL, SMMaterialForecastReportBean reportBean) throws WTException, WTPropertyVetoException, ParseException{
		String documentVault = (String) criteria.get("documentVault");
		String vaultDocumentTypeId = (String) criteria.get("vaultDocumentTypeId"); 

		//create document in library and then link the report
		if ("true".equals(documentVault) && FormatHelper.hasContent(vaultDocumentTypeId)) {
			com.lcs.wc.document.LCSDocumentClientModel documentModel = new com.lcs.wc.document.LCSDocumentClientModel();
			FlexType vaultDocumentType = null;

			if (FormatHelper.hasContent(vaultDocumentTypeId)) {
				vaultDocumentType = FlexTypeCache.getFlexType(vaultDocumentTypeId);
			}

			//Date will return local time in Java  
		     Date localTime = new Date();		    
		     //creating DateFormat for converting time from local timezone to GMT
		     SimpleDateFormat converter = new SimpleDateFormat("yyyyMMddHHmmss");		    
		     //getting MSK timezone
		     TimeZone tz = TimeZone.getTimeZone("Europe/Moscow");
		     converter.setTimeZone(tz);

			String ActualUser = (String)criteria.get("ActualUserName");
			
			//Framing the report name... (to display user who generated the report and to display the Time in MSK)
			String documentName = "MFDR_"+ActualUser+"_" +  converter.format(localTime); 
			
			
			
			LOGGER.debug("Season Name ::: "+reportBean.getSelectedSeasonDisplayName());
			LOGGER.debug("Material Name ::: "+reportBean.getSelectedMaterialTypeDisplayName());
			LOGGER.debug("Brand Name ::: "+reportBean.getSelectedBrandDisplayName());
			LOGGER.debug("Material Supplier Name ::: "+reportBean.getSelectedMatSupDisplayName());
			LOGGER.debug("Nominated Value ::: "+reportBean.getSelectedNominatedDisplay());			

			documentModel.setFlexType(vaultDocumentType);
			documentModel.setName(documentName );
			documentModel.setValue("name", documentName );
			
			
			// Added to display the filter criteria in document object - Start
			documentModel.setValue("smSeasonName", reportBean.getSelectedSeasonDisplayName() );
			documentModel.setValue("smMaterialType", reportBean.getSelectedMaterialTypeDisplayName() );
			documentModel.setValue("smBrandMFDR", reportBean.getSelectedBrandDisplayName() );
			documentModel.setValue("smMaterialSupplier", reportBean.getSelectedMatSupDisplayName() );
			documentModel.setValue("smNominated", reportBean.getSelectedNominatedDisplay());
			// Added to display the filter criteria in document object - End
			
			
			//START: Fixing the JIRA SMPLM-757
			com.lcs.wc.flextype.FlexTypeAttribute docStatusAtt = vaultDocumentType.getAttribute("smDocumentStatus");
			String docStatusDefValue = docStatusAtt.getAttDefaultValue();
			documentModel.setValue("smDocumentStatus", docStatusDefValue);
			
			//END: Fixing the JIRA SMPLM-757
			documentModel.save(); 
			//attaching generated report to the document.
			documentModel.associateContent(fileURL); 

			LOGGER.debug("Material aggreegation report is generated successfully and uploaded to document.."+documentName);
		}
	}

}
