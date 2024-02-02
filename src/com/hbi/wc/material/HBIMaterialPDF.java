
package com.hbi.wc.material;

import com.lcs.wc.util.*;
import com.lcs.wc.flextype.*;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.client.web.*;
import com.lcs.wc.client.web.pdf.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lowagie.text.*;
import java.util.*;
import wt.util.*;
//import wt.part.WTPartMaster;
import com.lcs.wc.client.web.pdf.PDFTableGenerator;
import com.hbi.wc.material.HBIInternalMaterialDataGenerator;
import com.hbi.wc.material.HBIExternalMaterialDataGenerator;
import com.lcs.wc.material.LCSMaterialSupplier;





public class HBIMaterialPDF implements PDFContentCollection {

   public static String MATERIAL_MASTER_ID = "MATERIAL_MASTER_ID";
   public static String MATERIALSUPPLIER__ID = "MATERIALSUPPLIER_OBJECT";
   //public static PDFGeneratorHelper pgh = new PDFGeneratorHelper();
   private static final int DEBUG_LEVEL = Integer.parseInt(LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationMeasurements2.verboseLevel", "1"));
   private static final boolean DEBUG = LCSProperties.getBoolean("com.lcs.wc.product.PDFProductSpecificationMeasurements2.verbose");
   private static final String MATERIAL_SPEC_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.MaterialSpecPDF.AttributeColumnsOrder");

	/** Creates a new instance of CWMatColorPDFContent */
   public HBIMaterialPDF() {

   }

   
   /** gets an Element for insertion into a PDF Document
     * @param params 
     * @param document 
     * @throws WTException 
     * @return an Element for insertion into a Document
     */
   public Collection getPDFContentCollection(Map params, Document document) throws WTException {
        try{
			Collection content = new ArrayList();
			String attValue = "";
			String matType = "";
			Boolean isImageAttached = false;
			//WTPartMaster materialMaster = (WTPartMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
			LCSMaterialMaster materialMaster = (LCSMaterialMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
    		LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);
			LCSMaterialSupplier materialSupplier=(LCSMaterialSupplier)params.get(MATERIALSUPPLIER__ID);
			matType = material.getFlexType().getFullNameDisplay(false);
			if("Fabric\\Fabric Buy".equalsIgnoreCase(matType)|| "Accessories".equalsIgnoreCase(matType)||
				"Yarn".equalsIgnoreCase(matType)||"Elastics".equalsIgnoreCase(matType)){
				HBIExternalMaterialDataGenerator hbiExtMatDataGen = new HBIExternalMaterialDataGenerator();
				//adding the first page PDF content
				content.add(hbiExtMatDataGen.generateExternalPDFFirstPage(params, document));
				//adding the second page PDF content
				if(!"Yarn".equalsIgnoreCase(matType)){
					content.add(hbiExtMatDataGen.generateExternalPDFSecondPage(params, document));
				}
				/*
				//added by sobabu for 131155-15
				//commented for 20173-17
				if("Fabric\\Fabric Buy".equalsIgnoreCase(matType)){
					content.add(hbiExtMatDataGen.generateExternalPDFThirdpage(params, document));
				}
				//ended by sobabu

				*/
				//isImageAttached = new Boolean((String)material.getValue("hbiImageAttached")).booleanValue();
				isImageAttached =  (Boolean) material.getValue("hbiImageAttached");
				if(isImageAttached){
					content.add(hbiExtMatDataGen.generateImagePage(params, document));
				}

			}else if("Fabric\\Finished".equalsIgnoreCase(matType) || "Fabric\\Greige".equalsIgnoreCase(matType)){
				HBIInternalMaterialDataGenerator hbiIntMatDataGen = new HBIInternalMaterialDataGenerator();
				//1st page
				content.add(hbiIntMatDataGen.generatePage1IntMatMOATables(params, document));
				if("Fabric\\Greige".equalsIgnoreCase(matType)) {
					// 2nd page (greige report)
					content.add(hbiIntMatDataGen.generatePage2InteMatMOATables(params, document));
				}else{
					//3rd page (finish report)
					content.add(hbiIntMatDataGen.generatePage3InteMatMOATables(params, document));
				}
				//isImageAttached = new Boolean((String)material.getValue("hbiImageAttached")).booleanValue();
				isImageAttached = (Boolean) material.getValue("hbiImageAttached");
				if(isImageAttached){
					content.add(hbiIntMatDataGen.generateImagePage(params, document));
				}
			}
			
			return content;

		}catch(Exception e){
			throw new WTException(e);
		}

	}

	/** gets columns for given key from custom.lcs.properties file
     * @param material 
     * @param params 
     * @throws WTException 
     * @return Collection
     */
   /*	public Collection getColumns(LCSMaterial material, Map params) throws WTException {

		    Collection columns = new Vector();
			TableColumn column = null;
			FlexTypeAttribute att = null;
			FlexType materialType = material.getFlexType();
			FlexTypeGenerator flexg = new FlexTypeGenerator();
	      	StringTokenizer parser = new StringTokenizer(MATERIAL_SPEC_ATT_COLUMN_ORDER, ",");
			while(parser.hasMoreTokens()){
				column = new TableColumn();
				flexg.setScope("MATERIAL");
				flexg.setLevel(null);
				String attName = parser.nextToken();
				try{
					att = materialType.getAttribute(attName);
					column = flexg.createTableColumn(att, materialType, false);
					columns.add(column);
				}catch(WTException et){
				 
				 flexg.setScope(null);
				 flexg.setLevel(null);

				 flexg.setScope("MATERIAL-SUPPLIER");
				 flexg.setLevel(null);
				 column = flexg.createTableColumn(att, materialType, false);
				 columns.add(column);

				 flexg.setScope(null);
				 flexg.setLevel(null);
				 
    			}
    		}
			return columns;
       }
	   */

   /////////////////////////////////////////////////////////////////////////////
   public static void debug(String msg){debug(msg, 1); }
   public static void debug(int i, String msg){debug(msg, i); }
   public static void debug(String msg, int i){
        if(DEBUG && i <= DEBUG_LEVEL) LCSLog.debug(msg);
   }
   
}  //end class