/**
 ***************************************************************************************************
 * HBIExternalMaterialDataGenerator.java 

 * Data generator class for External material spec report 

 * Created on May 2, 2011
 ***************************************************************************************************
 */

package com.hbi.wc.material;

import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.util.*;
import wt.util.*;
import wt.fc.WTObject;
//import wt.part.WTPartMaster;
import com.lcs.wc.db.*;
import com.lcs.wc.flextype.*;
import com.lcs.wc.client.web.*;
import com.lcs.wc.client.web.pdf.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.util.*;
import com.lcs.wc.client.ClientContext;
import com.lcs.wc.document.ImagePagePDFGenerator;
import com.lcs.wc.document.LCSDocument;


public class HBIExternalMaterialDataGenerator 
{
	
	public static PDFGeneratorHelper pgh = new PDFGeneratorHelper();
	public static String MATERIAL_MASTER_ID = "MATERIAL_MASTER_ID";
	public static String MATERIALSUPPLIER_OBJECT = "MATERIALSUPPLIER_OBJECT";

	public static String HEADER_HEIGHT = "HEADER_HEIGHT";

	//External report subtypes
	private static final String TYPE_FABRIC_BUY = LCSProperties.get("com.lcs.wc.material.LCSMaterial.subType.FabricBuy"); 
	private static final String TYPE_ACCESSORIES = LCSProperties.get("com.lcs.wc.material.LCSMaterial.subType.Accessories"); 
	private static final String TYPE_YARN = LCSProperties.get("com.lcs.wc.material.LCSMaterial.subType.Yarn"); 
	private static final String TYPE_ELASTICS = LCSProperties.get("com.lcs.wc.material.LCSMaterial.subType.Elastics"); 

	//MOA Table attribute
	private static final String FABRIC_SPEC_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.FabricSpecMOAColumnsOrder");
	private static final String ACC_SPEC_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.AccSpecMOAColumnsOrder");
	private static final String ELAST_SPEC_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.ElastSpecMOAColumnsOrder");

	private static final String ATHC_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.AthcMOAColumnsOrder");
	private static final String STRTCH_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.StretchMOAColumnsOrder");
	private static final String COLOR_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.ColorMOAColumnsOrder");
	private static final String LGL_COMLNC_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.LglComplncMOAColumnsOrder");
	private static final String MARTNG_CLMS_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.MrkngClmsMOAColumnsOrder");
	private static final String PURCHED_YARN_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.PurYarnMOAColumnsOrder");  
	private static final String WASH_INSTRCTNS_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.WashInstrctnsMOAColumnsOrder");
	private static final String YARN_PRFMNCE_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.YarnPrmnceMOAColumnsOrder");
	//added by sobabu for 131155-15
	//commentted for 20173-17
	//private static final String FINISHATT_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.FinishDimBuyMOAColumnsOrder");
	//ended
	//Material subtype attributes
	private static final String MAT_DETAILS_FB_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.FabricBuyColumnsOrder");
	private static final String MAT_DETAILS_ACC_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.AccessoriesColumnsOrder");
	private static final String MAT_DETAILS_ELAST_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.ElasticsColumnsOrder");
	private static final String MAT_DETAILS_YARN_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.YarnColumnsOrder"); 
	
	//Yarn specific attributes
	private static final String YARN_SPUN_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.YarnSunColumnsOrder");
	private static final String YARN_FILAMENT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIExternalMaterialDataGenerator.YarnFilamentColumnsOrder");
	
	private static final String FOOTER_STATEMENT_KEY = LCSProperties.get("com.lcs.wc.material.specReport.FooterStatement.Key"); 
    private static java.util.List TYPE_LIST = new ArrayList();  
	private static int linesOfStatement = 4; //number of statement lines in the second page


	/** Creates a new instance of HBIExternalMaterialDataGenerator */		 
	public  HBIExternalMaterialDataGenerator()
	{    
		//collection of material subtypes 
		//These three types have similar reports

		TYPE_LIST.add(TYPE_FABRIC_BUY);
		TYPE_LIST.add(TYPE_ACCESSORIES);
		TYPE_LIST.add(TYPE_ELASTICS);
    }

	
   /** generating the report first page
	* @param params A Map of parameters to pass to the Object.  
	* @param document The PDF Document which the content is going to be added to.  The document is
	* passed in order to provide additional information related to the Document itself
    * incase it is not provided in the params
	* @throws WTException For any error
	* @return a PdfPTable for insertion into the Document
   */
    public PdfPTable generateExternalPDFFirstPage(Map params, Document document)throws WTException
    {
		PdfPTable allTables = new PdfPTable(1);
	    allTables.setWidthPercentage(100F);
		PdfPTable table;
	    PdfPCell cell;	
		
		try
		{
		  //WTPartMaster materialMaster = (WTPartMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
			LCSMaterialMaster materialMaster = (LCSMaterialMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
		  LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);
		  FlexType materialType = material.getFlexType();
		  String strMatType = materialType.getFullNameDisplay(false);
          
		  /* Fabric Buy, Accessories and Elastics material subtypes
		  *  have similar first page layout
		  */
		  if(TYPE_LIST.contains(strMatType))
		  {   
			  //generating specification MOA Table
              table = generateSpecMOAtable(params,document, material);
			  cell = new PdfPCell(table);
			  cell.setPadding(3F);
			  allTables.addCell(cell);
					  			 
              //generating Aesthetics MOA Table
			  table = generateAestheticMOAtable(params, document, material);
			  cell = new PdfPCell(table);
			  cell.setPadding(3F);
			  allTables.addCell(cell);
			              
			  //generating Stretch MOA Table
			  table = generateStretchMOAtable(params, document, material);
			  cell = new PdfPCell(table);
			  cell.setPadding(3F);
			  allTables.addCell(cell);
			 
              //generating Color MOA Table 
			  table = generateColorMOAtable(params, document, material);
			  cell = new PdfPCell(table);
			  cell.setPadding(3F);
			  allTables.addCell(cell);

			  //generating Legal Compliance MOA Table 
			  table = generateLglComplnceMOAtable(params, document, material);
			  cell = new PdfPCell(table);
			  cell.setPadding(3F);
			  allTables.addCell(cell);	

              //generating Marketing Claims MOA Table 
			  table = generateMrktngClmsMOAtable(params, document, material);
			  cell = new PdfPCell(table);
			  cell.setPadding(3F);
			  allTables.addCell(cell);

		  //yarn subtype has a different first page layout
		  }else if(strMatType.equalsIgnoreCase(TYPE_YARN))
		  {
		     //generating specification MOA Table
		     //table = generateYarnSpecMOAtable(params, document, material); 
			 table = generateSpecMOAtable(params, document, material);
			 cell = new PdfPCell(table);
		     cell.setPadding(3F);
		     allTables.addCell(cell);
				  
             //generating Material Details section
			 table = generateMatDetailsSection(params,document, materialType);
			 allTables.addCell(new PdfPCell(table));

             //generating Spun and Filament Details section	 
 			 table = generateYarnDetailsSection(params,document, materialType);
			 allTables.addCell(new PdfPCell(table));

			 //generating Spec Comments section	 
			 table = generateSpecCommentsSection(material);
			 cell = new PdfPCell(table);
			 cell.setPadding(3F);
			 allTables.addCell(cell);	
		  }		
		}catch(Exception e){
            throw new WTException(e);
		}

		 return allTables;			
	}

    /** generating the report second page
	  * @param params A Map of parameters to pass to the Object.  
	  * @param document The PDF Document which the content is going to be added to.  The document is
	  * passed in order to provide additional information related to the Document itself
	  * incase it is not provided in the params
	  * @throws WTException For any error
	  * @return a PdfPTable for insertion into the Document
	 */
	public PdfPTable generateExternalPDFSecondPage(Map params, Document document)throws WTException
    {
		 PdfPTable allTables = new PdfPTable(1);
		 allTables.setWidthPercentage(100F);
		 PdfPTable table;
		 PdfPCell cell;
		
		 try{		 

			  //WTPartMaster materialMaster = (WTPartMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
			 LCSMaterialMaster materialMaster = (LCSMaterialMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
			  LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);
			  LCSMaterialSupplier matSupplier = (LCSMaterialSupplier)params.get(MATERIALSUPPLIER_OBJECT);
			  FlexType materialType = material.getFlexType();
			  String strMatType = materialType.getFullNameDisplay(false);
              
			  /* Fabric Buy, Accessories and Elastics material subtypes
			   *  have similar second page layout
		      */
			  if(TYPE_LIST.contains(strMatType))
		      {
				  //generating Material Details section	
				  table = generateMatDetailsSection(params,document, materialType);
				  allTables.addCell(new PdfPCell(table));

				  //generating Spec Comments section	 
				  table = generateSpecCommentsSection(material);
				  cell = new PdfPCell(table);
				  cell.setPadding(4F);
				  allTables.addCell(cell);	
				  
				  //generating Fiber Content section					  
				  table = generateFiberMatContentSection(params, document, material, matSupplier);
				  allTables.addCell(new PdfPCell(table));

				  //generating Wash Instructions MOA Table					
				  table = generateWashInstMOAtable(params, document, material);
				  cell = new PdfPCell(table);
				  cell.setPadding(4F);
				  allTables.addCell(cell);

				  //generating Statements section	
				  table = generateStatementsSection();
				  allTables.addCell(new PdfPCell(table));

			  }/*else if(strMatType.equalsIgnoreCase(TYPE_YARN))
			  {
			     //image page
			  
			  }*/
		 
		 } catch(Exception e){
            throw new WTException(e);
        }
		 return allTables;	
	}
	/*
	//added by sobabu for 131155-15
	//commented for 20173-17
	
	public PdfPTable generateExternalPDFThirdpage(Map params, Document document)throws WTException
    {
		PdfPTable allTables = new PdfPTable(1);
	    allTables.setWidthPercentage(100F);
		PdfPTable table;
	    PdfPCell cell;	
		
		try
		{
		  WTPartMaster materialMaster = (WTPartMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));  
		  LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);
		  FlexType materialType = material.getFlexType();
		  String strMatType = materialType.getFullNameDisplay(false);
          
		  if(TYPE_LIST.contains(strMatType))
		  {   
			  
			  
				  //generating Fabric Dimension MOA Table					
				  table = generateFinishDimensionsMOAtable(params, document, material);
				  cell = new PdfPCell(table);
				  cell.setPadding(3F);
				  allTables.addCell(cell);
				              
		  }
		}  
		catch(Exception e){
            throw new WTException(e);
        }
		 return allTables;	
	}
	//ended
	*/


	public Element generateImagePage(Map params, Document document)throws WTException {
		
		  Element ele = null;
		  //WTPartMaster materialMaster = (WTPartMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
		  LCSMaterialMaster materialMaster = (LCSMaterialMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
    	  LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);
		  ele = findInternalMatImage(params, document, material);
			return ele;
	}

	private Element findInternalMatImage(Map params, Document document, LCSMaterial material) throws WTException {

			boolean INCLUDE_COMMENTS = false;
			Collection imageColl = null;
			Element content = null;
			LCSMaterialQuery lcsMatQuery = new LCSMaterialQuery();
			imageColl = lcsMatQuery.getMaterialImages(material);
			if(imageColl.size() >0){
				Iterator imageCollItr = imageColl.iterator();
				LCSDocument lcsDoc = (LCSDocument)imageCollItr.next();
				String lcsDocId = FormatHelper.getObjectId(lcsDoc);
				params.put(ImagePagePDFGenerator.DOCUMENT_ID,lcsDocId);
				params.put(HEADER_HEIGHT, "150F");
				ImagePagePDFGenerator ippg = new ImagePagePDFGenerator(lcsDoc);
				ippg.setIncludeComment(INCLUDE_COMMENTS);
				content = ippg.getPDFContent(params, document);
			}

			return content;
	}

	
     
    /** creating the Statement section
	  * @throws WTException For any error
	  * @return a PdfPTable for insertion into PDF page
	 */
	private PdfPTable generateStatementsSection()throws WTException 
	{
		PdfPTable mainTable = new PdfPTable(1);
		PdfPTable statementTable = new PdfPTable(1);
		statementTable.setWidthPercentage(100F);

		addSpaceBetweenRows(statementTable, 1);

		for(int i = 1; i <= linesOfStatement ; i++)
		{
		  statementTable.addCell(createMatDataCell(LCSProperties.get(FOOTER_STATEMENT_KEY + i)));	
		}

		addSpaceBetweenRows(statementTable, 1);

		PdfPCell cell = new PdfPCell(statementTable);
		cell.setBorder(0);
		mainTable.addCell(cell);

		return mainTable;
	 }

	 /** creating the Fiber Content section
	  * @param params A Map of parameters to pass to the Object.  
	  * @param document The PDF Document which the content is going to be added to.
	  * @param material The material object
	  * @param matSupp  The material supplier object
	  * @throws WTException For any error
	  * @return a PdfPTable for insertion into PDF page
	 */
	 private PdfPTable generateFiberMatContentSection(Map params, Document document, LCSMaterial material, LCSMaterialSupplier matSupp)throws WTException 
	 {
         
		 String strFiberCont = "";
		 String strFiberContKey = "";
		 String strMatType = "";
		 String strFiberContentDisplay = "";
		 PdfPCell cell;

		 try
		 {	        
			 strMatType = material.getFlexType().getFullNameDisplay(false);
			 if(strMatType.equalsIgnoreCase(TYPE_ELASTICS))
			{
				 strFiberContKey = "hbiFiberContentText";
				 strFiberCont = (String)material.getValue(strFiberContKey);				 
				 if(strFiberCont == null ||  "".equals(strFiberCont) ||  "null".equalsIgnoreCase(strFiberCont))	
					{			 							
					strFiberCont = "";
				 
					}		 
			}

			if(strMatType.equalsIgnoreCase(TYPE_FABRIC_BUY))
			{
				strFiberCont="";
				strFiberContKey = "hbiFiberContent";
				strFiberCont = (String)material.getValue(strFiberContKey);			
				if(strFiberCont != null && ! "".equals(strFiberCont) && ! "null".equalsIgnoreCase(strFiberCont))	
					{			 							
					AttributeValueList fiberContAttList = material.getFlexType().getAttribute(strFiberContKey).getAttValueList();			   
					strFiberCont = MOAHelper.parseCompositeString(strFiberCont, fiberContAttList, ClientContext.getContext().getLocale(), MOAHelper.DELIM);
				 
					}else
					strFiberCont = "";
				
			}else if (strMatType.equalsIgnoreCase(TYPE_ACCESSORIES))

			{
				 strFiberCont = "";
				 strFiberContKey = "hbiFiberContentText";
				 strFiberCont = (String)material.getValue(strFiberContKey);				 
				 if(strFiberCont == null ||  "".equals(strFiberCont) ||  "null".equalsIgnoreCase(strFiberCont))	
					{			 							
					strFiberCont = "";
				 
					}		 
			}else if(strMatType.equalsIgnoreCase("Fabric//Finished") || strMatType.equalsIgnoreCase("Fabric//Greige"))
			{
				strFiberCont="";
				strFiberContKey = "hbiFiberContent";
				strFiberCont = (String)material.getValue(strFiberContKey);			
				if(strFiberCont != null && ! "".equals(strFiberCont) && ! "null".equalsIgnoreCase(strFiberCont))	
					{			 							
					AttributeValueList fiberContAttList = material.getFlexType().getAttribute(strFiberContKey).getAttValueList();			   
					strFiberCont = MOAHelper.parseCompositeString(strFiberCont, fiberContAttList, ClientContext.getContext().getLocale(), MOAHelper.DELIM);
				 
					}else
					strFiberCont = "";
				 
			}else if(strMatType.equalsIgnoreCase(TYPE_YARN))
			{
				strFiberCont="";
				strFiberContKey = "hbiFiberContent";
				strFiberCont = (String)material.getValue(strFiberContKey);			
				if(strFiberCont != null && ! "".equals(strFiberCont) && ! "null".equalsIgnoreCase(strFiberCont))	
					{			 							
					AttributeValueList fiberContAttList = material.getFlexType().getAttribute(strFiberContKey).getAttValueList();			   
					strFiberCont = MOAHelper.parseCompositeString(strFiberCont, fiberContAttList, ClientContext.getContext().getLocale(), MOAHelper.DELIM);
				 
					}else
					strFiberCont = ""; 
			}
			 strFiberContKey = "hbiFiberContent";
			
			 

			
		 } catch(Exception e){
            throw new WTException(e);
         }

		PdfPTable mainTable = new PdfPTable(1);			
		PdfPTable fiberContTable = new PdfPTable(1);
		fiberContTable.setWidthPercentage(100F);

		//fiberContTable.addCell(createLabelCell("Fabric/Material Content", 1));
		fiberContTable.addCell(createLabelCell("FABRIC/MATERIAL CONTENT", 1));
		addSpaceBetweenRows(fiberContTable, 1);

		fiberContTable.addCell(createMatDataCell(strFiberCont));	

		addSpaceBetweenRows(fiberContTable, 1);

		PdfPTable table = generatePurchasedYarnMOAtable(params, document, matSupp);
		cell = new PdfPCell(table);
		cell.setPadding(4F);
		fiberContTable.addCell(cell);	

		cell = new PdfPCell(fiberContTable);
		cell.setBorder(0);
		mainTable.addCell(cell);

		return mainTable;
	 }

	 /** creating the Spec Comments section
	  * @param material The material object
	  * @throws WTException For any error
	  * @return a PdfPTable for insertion into PDF page
	 */
	 private PdfPTable generateSpecCommentsSection(LCSMaterial material)throws WTException 
	 {         
		 String strSpecComm = "";
		 try{
			    strSpecComm = (String)material.getValue("hbiSpecComments");
				if(strSpecComm == null)
                  strSpecComm = "";

			 } catch(Exception e){
            throw new WTException(e);
         }

		PdfPTable mainTable = new PdfPTable(1);
		PdfPTable specCommTable = new PdfPTable(1);
		specCommTable.setWidthPercentage(100F);

		//specCommTable.addCell(createLabelCell("Spec Comments", 1));
		specCommTable.addCell(createLabelCell("SPEC COMMENTS", 1));
		addSpaceBetweenRows(specCommTable, 1);

		specCommTable.addCell(createDataCell(strSpecComm));	

		PdfPCell cell = new PdfPCell(specCommTable);
		cell.setBorder(0);
		mainTable.addCell(cell);

		return mainTable;
	 }

	 /** creating the Yarn specific Spun and Filament attributes section
	  * @param params A Map of parameters to pass to the Object.  
	  * @param document The PDF Document which the content is going to be added to.
	  * @param flextType The material flextType object
	  * @throws WTException For any error
	  * @return a PdfPTable for insertion into PDF page
	 */
	 private PdfPTable generateYarnDetailsSection(Map params, Document document,  FlexType flextType) throws WTException 
	 { 	 
		  
		 Collection spunDetailsAtt = new ArrayList();
		 Collection filDetailsAtt = new ArrayList();
		 TableColumn column = null;
		 StringTokenizer parser = null;
		 Collection labelColumns = null;
		 Iterator lblColsIter = null;
		 StringTokenizer attParser = null;
		 Collection rawDataCollection = null;
		 Iterator rawDataIter = null;
		 TableData td = null;
		 SearchResults results = null;

		 PdfPCell cell = new PdfPCell();
		 PdfPTable mainTable = new PdfPTable(1);
		 float [] colWidths = {50, 50};
		 PdfPTable yarnTable = new PdfPTable(colWidths);

		 PdfPTable spunTable = new PdfPTable(colWidths);
		 PdfPTable filamentTable = new PdfPTable(colWidths);
		
		 try
		 {
			 //Spun attributes
			 parser = new StringTokenizer(YARN_SPUN_COLUMN_ORDER, ",");
			 labelColumns = getMatColumns(parser, flextType);
			 lblColsIter = labelColumns.iterator();
			 attParser = new StringTokenizer(YARN_SPUN_COLUMN_ORDER, ",");

			 while(attParser.hasMoreTokens())
			 {
				String attkey = attParser.nextToken().trim();
				spunDetailsAtt.add(attkey);
			 }

			 results = new HBIMaterialSupplierQuery().findMaterialSupplierAttributes(params, flextType, spunDetailsAtt, null);
			 rawDataCollection =results.getResults();
			 rawDataIter = rawDataCollection.iterator();
			 td =(TableData)rawDataIter.next();
	 
			 //spunTable.addCell(createLabelCell("Spun Yarn Attributes", colWidths.length));
			 spunTable.addCell(createLabelCell("SPUN YARN ATTRIBUTES", colWidths.length));
			 addSpaceBetweenRows(spunTable, colWidths.length);
			 while(lblColsIter.hasNext()) 
			 {
				column = (TableColumn)lblColsIter.next();
				spunTable.addCell(createMatLabelCell(column.getHeaderLabel()));
				spunTable.addCell(createMatDataCell(column.getPDFDisplayValue(td)));						
			 }
	
			 //Filament attributes
			 parser = new StringTokenizer(YARN_FILAMENT_COLUMN_ORDER, ",");
			 labelColumns = getMatColumns(parser, flextType);
			 lblColsIter = labelColumns.iterator();
			 attParser = new StringTokenizer(YARN_FILAMENT_COLUMN_ORDER, ",");

			 while(attParser.hasMoreTokens())
			 {
				String attkey = attParser.nextToken().trim();
				filDetailsAtt.add(attkey);
			 }

			 results = new HBIMaterialSupplierQuery().findMaterialSupplierAttributes(params, flextType, filDetailsAtt, null);
			 rawDataCollection =results.getResults();
			 rawDataIter = rawDataCollection.iterator();
			 td =(TableData)rawDataIter.next();
	 
			 //filamentTable.addCell(createLabelCell("Filament Yarn Attributes", colWidths.length));
			 filamentTable.addCell(createLabelCell("FILAMENT YARN ATTRIBUTES", colWidths.length));
			 addSpaceBetweenRows(filamentTable, colWidths.length);
			 while(lblColsIter.hasNext()) 
			 {
				column = (TableColumn)lblColsIter.next();
				filamentTable.addCell(createMatLabelCell(column.getHeaderLabel()));
				filamentTable.addCell(createMatDataCell(column.getPDFDisplayValue(td)));						
			 }
			 cell = new PdfPCell(spunTable);
			 cell.setBorder(0);
			 yarnTable.addCell(cell);

			 cell = new PdfPCell(filamentTable);
			 cell.setBorder(0);
			 yarnTable.addCell(cell);

			 addSpaceBetweenRows(yarnTable, colWidths.length);

			 cell = new PdfPCell(yarnTable);
			 cell.setBorder(0);
			 mainTable.addCell(cell);
			
        }
        catch(Exception e){
            throw new WTException(e);
        }
		 return mainTable; 	
	 }
       
     /** creating the Material Details section
	  * @param params A Map of parameters to pass to the Object.  
	  * @param document The PDF Document which the content is going to be added to.
	  * @param flextType The material flextType object
	  * @throws WTException For any error
	  * @return a PdfPTable for insertion into PDF page
	 */
	 private PdfPTable generateMatDetailsSection(Map params, Document document, FlexType flextType)throws WTException 
	 {
         String strAttKeys = "";
		 String strMatType = flextType.getFullNameDisplay(false);
	     /* Read a different property entry 
		  * based on the material subtype
		 */ 
		 if(strMatType.equalsIgnoreCase(TYPE_FABRIC_BUY)){			 
			 strAttKeys = MAT_DETAILS_FB_COLUMN_ORDER;
         }else if (strMatType.equalsIgnoreCase(TYPE_ACCESSORIES)){			 
			 strAttKeys = MAT_DETAILS_ACC_COLUMN_ORDER;
		 }else if(strMatType.equalsIgnoreCase(TYPE_ELASTICS)){
			 strAttKeys = MAT_DETAILS_ELAST_COLUMN_ORDER;
		 }else if(strMatType.equalsIgnoreCase(TYPE_YARN)){			 
			 strAttKeys = MAT_DETAILS_YARN_COLUMN_ORDER;	
		 }
		 Collection materialDetailsAtt = new ArrayList();
		 TableColumn column = null;
		 PdfPCell cell = new PdfPCell();
		 PdfPTable mainTable = new PdfPTable(1);
		 float [] colWidths = {25.0F, 25.0F, 25.0F, 25.0F};
		 PdfPTable matDetailsTable = new PdfPTable(colWidths);
		 matDetailsTable.getDefaultCell().setBorder(0); 
		 try
		 {				
			 StringTokenizer parser = new StringTokenizer(strAttKeys, ",");
			 Collection labelColumns = getMatColumns(parser, flextType);
			 Iterator lblColsIter = labelColumns.iterator();
			 StringTokenizer attParser = new StringTokenizer(strAttKeys, ",");
			 while(attParser.hasMoreTokens())
			 {
				String attkey = attParser.nextToken().trim();
				materialDetailsAtt.add(attkey);
			 }
			 SearchResults results = new HBIMaterialSupplierQuery().findMaterialSupplierAttributes(params, flextType, materialDetailsAtt, null);
			 Collection rawDataCollection =results.getResults();
			 Iterator rawDataIter = rawDataCollection.iterator();
			 TableData td =(TableData)rawDataIter.next();
         	 //calculate the empty cells
			 //to be filled in
			 int abc = colWidths.length/2;
			 int xyz = labelColumns.size() % abc;
			 int ijk = 0;
			 if(xyz != 0)
			 ijk =  abc - xyz;
			 //matDetailsTable.addCell(createLabelCell("Material Details", colWidths.length));
			 matDetailsTable.addCell(createLabelCell("MATERIAL DETAILS", colWidths.length));
			 addSpaceBetweenRows(matDetailsTable, colWidths.length);
			 while(lblColsIter.hasNext()) 
			 {
				column = (TableColumn)lblColsIter.next();
				if("Ply".equals(column.getHeaderLabel())){
					matDetailsTable.addCell("");
					matDetailsTable.addCell("");
					matDetailsTable.addCell(createMatLabelCell(column.getHeaderLabel()));
					matDetailsTable.addCell(createMatDataCell(column.getPDFDisplayValue(td)));					

				}else{
					matDetailsTable.addCell(createMatLabelCell(column.getHeaderLabel()));
					matDetailsTable.addCell(createMatDataCell(column.getPDFDisplayValue(td)));					
				}

				/*if(! lblColsIter.hasNext() && ijk > 0 )
				{
					for(int i = 1; i <= ijk; i++)
					{
					   matDetailsTable.addCell("");
					   matDetailsTable.addCell("");				
					}
				}*/								
			 }

			 addSpaceBetweenRows(matDetailsTable, colWidths.length);
			 cell = new PdfPCell(matDetailsTable);
			 cell.setBorder(0);
			 mainTable.addCell(cell);
			
        }
        catch(Exception e){
            throw new WTException(e);
        }

		 return mainTable; 	
	 }

     /** creating the Yarn specification MOA Table
	  * @param params A Map of parameters to pass to the Object.  
	  * @param document The PDF Document which the content is going to be added to.
	  * @param material The material object
	  * @throws WTException For any error
	  * @return a PdfPTable for insertion into PDF page
	 */
	 private PdfPTable generateYarnSpecMOAtable(Map params, Document document, LCSMaterial material) throws WTException 
	 {

		TableData spectd = null;
		TableColumn column  = null;
		String specMOATypeName = "Multi-Object\\Yarn Performance";
		FlexType specMOAType = null;
		Collection specAttCols = new ArrayList();
		SearchResults results = null;
		PdfPTable mainTable = new PdfPTable(1);
		StringTokenizer specAttParser = new StringTokenizer(YARN_PRFMNCE_MOA_ATT_COLUMN_ORDER, ",");
		StringTokenizer specColParser = new StringTokenizer(YARN_PRFMNCE_MOA_ATT_COLUMN_ORDER, ",");

		while(specAttParser.hasMoreTokens())
		{
			String attkey = specAttParser.nextToken();
			specAttCols.add(attkey);
		}

		int i =  specAttCols.size();
		PdfPTable table = new PdfPTable(i);
		//table.addCell(createLabelCell("SPECIFICATIONS", i));
		table.addCell(createLabelCell("SPECIFICATIONS", i));
		addSpaceBetweenRows(table, i);

		try
		{
			FlexType materialType = material.getFlexType();
			FlexTypeAttribute fabricsAtt = materialType.getAttribute("hbiYarnPerformance");
			specMOAType = FlexTypeCache.getFlexTypeFromPath(specMOATypeName);
			results = findHbiMOADataCollection(material, fabricsAtt, specMOAType, specAttCols);
			Collection specDataCollection =results.getResults();
			Iterator specDataIter = specDataCollection.iterator();
			Collection specLabelColumns = getColumns(specColParser, specMOAType);
			Iterator specLblColsIter = specLabelColumns.iterator();
   
			while(specLblColsIter.hasNext()){
				column = (TableColumn)specLblColsIter.next();
				table.addCell(createLabelCell(column.getHeaderLabel()));
			}
			while(specDataIter.hasNext()){
				 spectd = (TableData)specDataIter.next();
				 Iterator specLblColsIter2 = specLabelColumns.iterator();
				 while(specLblColsIter2.hasNext()){
					column = (TableColumn)specLblColsIter2.next();
					table.addCell(createDataCell(column.getPDFDisplayValue(spectd)));		
				 }
			}
		
			PdfPCell cell = new PdfPCell(table);
			cell.setBorder(0);
			mainTable.addCell(cell);
			
		} catch (Exception e){
			 throw new WTException(e);
		}

		return mainTable;
	}
	
	/** creating the Wash Instructions MOA Table
	  * @param params A Map of parameters to pass to the Object.  
	  * @param document The PDF Document which the content is going to be added to.
	  * @param material The material object
	  * @throws WTException For any error
	  * @return a PdfPTable for insertion into PDF page
	*/ 
    private PdfPTable generateWashInstMOAtable(Map params, Document document, LCSMaterial material ) throws WTException 
	{

		TableData spectd = null;
		TableColumn column  = null;
		String specMOATypeName = "Multi-Object\\Wash Instructions";
		FlexType specMOAType = null;
		Collection specAttCols = new ArrayList();
		SearchResults results = null;
		PdfPTable mainTable = new PdfPTable(1);
		StringTokenizer specAttParser = new StringTokenizer(WASH_INSTRCTNS_MOA_ATT_COLUMN_ORDER, ",");
		StringTokenizer specColParser = new StringTokenizer(WASH_INSTRCTNS_MOA_ATT_COLUMN_ORDER, ",");

		while(specAttParser.hasMoreTokens())
		{
			String attkey = specAttParser.nextToken();
			specAttCols.add(attkey);
		}

		int i =  specAttCols.size();
		PdfPTable table = new PdfPTable(i);
		//table.addCell(createLabelCell("Wash Instructions", i));
		table.addCell(createLabelCell("WASH INSTRUCTIONS", i));
		addSpaceBetweenRows(table, i);

		try
		{
			FlexType materialType = material.getFlexType();
			FlexTypeAttribute fabricsAtt = materialType.getAttribute("hbiWashInstructions");
			specMOAType = FlexTypeCache.getFlexTypeFromPath(specMOATypeName);
			results = findHbiMOADataCollection(material, fabricsAtt, specMOAType, specAttCols);
			Collection specDataCollection =results.getResults();
			Iterator specDataIter = specDataCollection.iterator();
			Collection specLabelColumns = getColumns(specColParser, specMOAType);
			Iterator specLblColsIter = specLabelColumns.iterator();
   
			while(specLblColsIter.hasNext()){
				column = (TableColumn)specLblColsIter.next();
				table.addCell(createLabelCell(column.getHeaderLabel()));
			}
			while(specDataIter.hasNext()){
				 spectd = (TableData)specDataIter.next();
				 Iterator specLblColsIter2 = specLabelColumns.iterator();
				 while(specLblColsIter2.hasNext()){
					column = (TableColumn)specLblColsIter2.next();
					table.addCell(createDataCell(column.getPDFDisplayValue(spectd)));		
				 }
			}
		
			PdfPCell cell = new PdfPCell(table);
			cell.setBorder(0);
			mainTable.addCell(cell);
			
		} catch (Exception e){
			 throw new WTException(e);
		}

		return mainTable;
	}

	/** creating the Purchased Yarn MOA Table
	  * @param params A Map of parameters to pass to the Object.  
	  * @param document The PDF Document which the content is going to be added to.
	  * @param material The material object
	  * @throws WTException For any error
	  * @return a PdfPTable for insertion into PDF page
	*/ 
	private PdfPTable generatePurchasedYarnMOAtable(Map params, Document document, LCSMaterialSupplier material ) throws WTException 
	{

		TableData spectd = null;
		TableColumn column  = null;
		String specMOATypeName = "Multi-Object\\Supplier Table";
		FlexType specMOAType = null;
		Collection specAttCols = new ArrayList();
		SearchResults results = null;
		PdfPTable mainTable = new PdfPTable(1);
		StringTokenizer specAttParser = new StringTokenizer(PURCHED_YARN_MOA_ATT_COLUMN_ORDER, ",");
		StringTokenizer specColParser = new StringTokenizer(PURCHED_YARN_MOA_ATT_COLUMN_ORDER, ",");
		while(specAttParser.hasMoreTokens())
		{
			String attkey = specAttParser.nextToken();
			specAttCols.add(attkey);
		}		

		int i =  specAttCols.size();
		PdfPTable table = new PdfPTable(i);
	
		try
		{
			FlexType materialType = material.getFlexType();
			FlexTypeAttribute fabricsAtt = materialType.getAttribute("hbiSupplierTable");
			specMOAType = FlexTypeCache.getFlexTypeFromPath(specMOATypeName);
			results = findHbiMOADataCollection(material, fabricsAtt, specMOAType, specAttCols);
		 
			Collection specDataCollection =results.getResults();
			Iterator specDataIter = specDataCollection.iterator();
			Collection specLabelColumns = getColumns(specColParser, specMOAType);
			Iterator specLblColsIter = specLabelColumns.iterator();       

			while(specLblColsIter.hasNext()){
				column = (TableColumn)specLblColsIter.next();
				table.addCell(createLabelCell(column.getHeaderLabel()));
			}
			while(specDataIter.hasNext()){
				 spectd = (TableData)specDataIter.next();					
				 Iterator specLblColsIter2 = specLabelColumns.iterator();
				 while(specLblColsIter2.hasNext()){
					column = (TableColumn)specLblColsIter2.next();
					table.addCell(createDataCell(column.getPDFDisplayValue(spectd)));	
				 }
			}			
			PdfPCell cell = new PdfPCell(table);
			cell.setBorder(0);
			mainTable.addCell(cell);
			
		} catch (Exception e){
			 throw new WTException(e);
		}

		return mainTable;
	}

	/** creating the Specifications MOA Table
	  * @param params A Map of parameters to pass to the Object.  
	  * @param document The PDF Document which the content is going to be added to.
	  * @param material The material object
	  * @throws WTException For any error
	  * @return a PdfPTable for insertion into PDF page
	*/  
	private PdfPTable generateSpecMOAtable(Map params, Document document, LCSMaterial material ) throws WTException 
	{	
		TableData spectd = null;
		TableColumn column  = null;
		String specMOATypeName = "";
		String specMOAAtt = "";
		String strSpecPropertyKey = "";
		FlexType specMOAType = null;
		Collection specAttCols = new ArrayList();
		SearchResults results = null;
		PdfPTable mainTable = new PdfPTable(1);

		try
		{
			String strMatType = material.getFlexType().getFullNameDisplay(false);

			/* invoke the spec MOA table based
			 * on the material subtype
			*/ 
			if(strMatType.equalsIgnoreCase(TYPE_FABRIC_BUY))
			{
				 specMOATypeName = "Multi-Object\\Fabrics";
				 specMOAAtt = "hbiFabrics";
				 strSpecPropertyKey = FABRIC_SPEC_MOA_ATT_COLUMN_ORDER;
				
			}else if (strMatType.equalsIgnoreCase(TYPE_ACCESSORIES))
			{
				 specMOATypeName = "Multi-Object\\Accessories";
				 specMOAAtt = "hbiAccessories";
				 strSpecPropertyKey = ACC_SPEC_MOA_ATT_COLUMN_ORDER;  
				
			}else if(strMatType.equalsIgnoreCase(TYPE_ELASTICS))
			{
				 specMOATypeName = "Multi-Object\\Elastics";
				 specMOAAtt = "hbiElastics";
				 strSpecPropertyKey = ELAST_SPEC_MOA_ATT_COLUMN_ORDER;
				 
			}else if(strMatType.equalsIgnoreCase(TYPE_YARN))
			{
				 specMOATypeName = "Multi-Object\\Yarn Performance";
				 specMOAAtt = "hbiYarnPerformance";
				 strSpecPropertyKey = YARN_PRFMNCE_MOA_ATT_COLUMN_ORDER;
			}

			StringTokenizer specAttParser = new StringTokenizer(strSpecPropertyKey, ",");
			StringTokenizer specColParser = new StringTokenizer(strSpecPropertyKey, ",");

			while(specAttParser.hasMoreTokens())
			{
				String attkey = specAttParser.nextToken();
				specAttCols.add(attkey);
			 }

			int i =  specAttCols.size();
			PdfPTable table = new PdfPTable(i);
			//table.addCell(createLabelCell("Specifications", i));
			table.addCell(createLabelCell("SPECIFICATIONS", i));
			addSpaceBetweenRows(table, i);	
			FlexType materialType = material.getFlexType();
			FlexTypeAttribute fabricsAtt = materialType.getAttribute(specMOAAtt);
			specMOAType = FlexTypeCache.getFlexTypeFromPath(specMOATypeName);
			results = findHbiMOADataCollection(material, fabricsAtt, specMOAType, specAttCols);
			Collection specDataCollection =results.getResults();
			Iterator specDataIter = specDataCollection.iterator();
			Collection specLabelColumns = getColumns(specColParser, specMOAType);
			Iterator specLblColsIter = specLabelColumns.iterator();          

			while(specLblColsIter.hasNext()){
				column = (TableColumn)specLblColsIter.next();
				table.addCell(createLabelCell(column.getHeaderLabel()));
			}
			while(specDataIter.hasNext()){
				 spectd = (TableData)specDataIter.next();
				 Iterator specLblColsIter2 = specLabelColumns.iterator();
				 while(specLblColsIter2.hasNext()){
					column = (TableColumn)specLblColsIter2.next();
					table.addCell(createDataCell(column.getPDFDisplayValue(spectd)));		
				 }
			}
			
			PdfPCell cell = new PdfPCell(table);
			cell.setBorder(0);
			mainTable.addCell(cell);
			
		} catch (Exception e){
			 throw new WTException(e);
		}

		return mainTable;
	}
	
	/** creating the Aesthetics MOA Table
	  * @param params A Map of parameters to pass to the Object.  
	  * @param document The PDF Document which the content is going to be added to.
	  * @param material The material object
	  * @throws WTException For any error
	  * @return a PdfPTable for insertion into PDF page
	*/  	
	private PdfPTable generateAestheticMOAtable(Map params, Document document, LCSMaterial material ) throws WTException 
	{

		TableData athctd = null;
		TableColumn column  = null;
		String aestheticMOATypeName = "Multi-Object\\Aesthetic";
		FlexType athcMOAType = null;
		Collection aestheticColl = new ArrayList() ;
		SearchResults results = null;
		PdfPTable mainTable = new PdfPTable(1);
		StringTokenizer athcAttParser = new StringTokenizer(ATHC_MOA_ATT_COLUMN_ORDER, ",");
		StringTokenizer athcColParser = new StringTokenizer(ATHC_MOA_ATT_COLUMN_ORDER, ",");

		while(athcAttParser.hasMoreTokens())
		{
			String attkey = athcAttParser.nextToken().trim();
			aestheticColl.add(attkey);
		}
		
		int i =  aestheticColl.size();
		PdfPTable table = new PdfPTable(i);
		//table.addCell(createLabelCell("Aesthetics", i));
		table.addCell(createLabelCell("AESTHETICS", i));
		addSpaceBetweenRows(table, i);

		try
		{
			FlexType materialType = material.getFlexType();
			FlexTypeAttribute aestheticAtt = materialType.getAttribute("hbiAesthetic");
			athcMOAType = FlexTypeCache.getFlexTypeFromPath(aestheticMOATypeName);
			results = findHbiMOADataCollection(material, aestheticAtt, athcMOAType, aestheticColl);
			Collection athcDataCollection =results.getResults();
			Iterator athcDataIter = athcDataCollection.iterator();
			Collection athcLabelColumns = getColumns(athcColParser, athcMOAType);
			Iterator athcLblColsIter = athcLabelColumns.iterator();

			while(athcLblColsIter.hasNext()){
				column = (TableColumn)athcLblColsIter.next();
				table.addCell(createLabelCell(column.getHeaderLabel()));
			}

			while(athcDataIter.hasNext()){
				 athctd = (TableData)athcDataIter.next();
				 Iterator athcLblColsIter2 = athcLabelColumns.iterator();
				 while(athcLblColsIter2.hasNext()){
					column = (TableColumn)athcLblColsIter2.next();
					table.addCell(createDataCell(column.getPDFDisplayValue(athctd)));		
				 }
			}
		  
			PdfPCell cell = new PdfPCell(table);
			cell.setBorder(0);
			mainTable.addCell(cell);
			
		 }catch(Exception e){
			throw new WTException(e);
		}
		return mainTable;
	}

	/** creating the Stretch MOA Table
	  * @param params A Map of parameters to pass to the Object.  
	  * @param document The PDF Document which the content is going to be added to.
	  * @param material The material object
	  * @throws WTException For any error
	  * @return a PdfPTable for insertion into PDF page
	*/ 
	private PdfPTable generateStretchMOAtable(Map params, Document document, LCSMaterial material ) throws WTException 
	{

		TableData strtchtd = null;
		TableColumn column  = null;
		String stretchMOATypeName = "Multi-Object\\Stretch";
		FlexType strchMOAType = null;
		Collection stretchColl = new ArrayList() ;
		SearchResults results = null;
		PdfPTable mainTable = new PdfPTable(1);
		StringTokenizer strtchAttParser = new StringTokenizer(STRTCH_MOA_ATT_COLUMN_ORDER, ",");
		StringTokenizer strtchColParser = new StringTokenizer(STRTCH_MOA_ATT_COLUMN_ORDER, ",");

		while(strtchAttParser.hasMoreTokens())
		{
			String attkey = strtchAttParser.nextToken().trim();
			stretchColl.add(attkey);
		}

		int i = stretchColl.size();
		PdfPTable table = new PdfPTable(i);
		//table.addCell(createLabelCell("Stretch", i));
		table.addCell(createLabelCell("STRETCH", i));
		addSpaceBetweenRows(table, i);

		try
		{
			FlexType materialType = material.getFlexType();
			FlexTypeAttribute stretchAtt = materialType.getAttribute("Stretch");
			strchMOAType = FlexTypeCache.getFlexTypeFromPath(stretchMOATypeName);
			results = findHbiMOADataCollection(material, stretchAtt, strchMOAType, stretchColl);
			Collection strchDataCollection =results.getResults();
			Iterator strchDataIter = strchDataCollection.iterator();
			Collection strchLabelColumns = getColumns(strtchColParser, strchMOAType);
			Iterator strtchLblColsIter = strchLabelColumns.iterator();

			while(strtchLblColsIter.hasNext()){
				column = (TableColumn)strtchLblColsIter.next();
				table.addCell(createLabelCell(column.getHeaderLabel()));
			}

			while(strchDataIter.hasNext()){
				 strtchtd = (TableData)strchDataIter.next();
				 Iterator strtchLblColsIter2 = strchLabelColumns.iterator();
				 while(strtchLblColsIter2.hasNext()){
					column = (TableColumn)strtchLblColsIter2.next();
					table.addCell(createDataCell(column.getPDFDisplayValue(strtchtd)));		
				 }
			}
			PdfPCell cell = new PdfPCell(table);
			cell.setBorder(0);
			mainTable.addCell(cell);
			

		 }catch(Exception e){
			throw new WTException(e);
		}
		return mainTable;
	}

	/** creating the Color MOA Table
	  * @param params A Map of parameters to pass to the Object.  
	  * @param document The PDF Document which the content is going to be added to.
	  * @param material The material object
	  * @throws WTException For any error
	  * @return a PdfPTable for insertion into PDF page
	*/ 	
	private PdfPTable generateColorMOAtable(Map params, Document document, LCSMaterial material ) throws WTException 
	{

		TableData clrtd = null;
		TableColumn column  = null;
		String colorMOATypeName = "Multi-Object\\Color";
		FlexType colorMOAType = null;
		Collection colorColl = new ArrayList() ;
		SearchResults results = null;
		PdfPTable mainTable = new PdfPTable(1);
		StringTokenizer colorAttParser = new StringTokenizer(COLOR_MOA_ATT_COLUMN_ORDER, ",");
		StringTokenizer colorColParser = new StringTokenizer(COLOR_MOA_ATT_COLUMN_ORDER, ",");

		while(colorAttParser.hasMoreTokens())
		{
			String attkey = colorAttParser.nextToken().trim();
			colorColl.add(attkey);
		}

		int i = colorColl.size();
		PdfPTable table = new PdfPTable(i);
		//table.addCell(createLabelCell("Color", i));
		table.addCell(createLabelCell("COLOR", i));
		addSpaceBetweenRows(table, i);

		try
		{
			FlexType materialType = material.getFlexType();
			FlexTypeAttribute colAtt = materialType.getAttribute("hbiColor");
			colorMOAType = FlexTypeCache.getFlexTypeFromPath(colorMOATypeName);
			results = findHbiMOADataCollection(material, colAtt, colorMOAType, colorColl);
			Collection colDataCollection =results.getResults();
			Iterator colDataIter = colDataCollection.iterator();
			Collection colLabelColumns = getColumns(colorColParser, colorMOAType);
			Iterator colLblColsIter = colLabelColumns.iterator();

			while(colLblColsIter.hasNext()){
				column = (TableColumn)colLblColsIter.next();
				table.addCell(createLabelCell(column.getHeaderLabel()));
			}

			while(colDataIter.hasNext()){
				 clrtd = (TableData)colDataIter.next();
				 Iterator colLblColsIter2 = colLabelColumns.iterator();
				 while(colLblColsIter2.hasNext()){
					column = (TableColumn)colLblColsIter2.next();
					table.addCell(createDataCell(column.getPDFDisplayValue(clrtd)));		
				 }
			}

			PdfPCell cell = new PdfPCell(table);
			cell.setBorder(0);
			mainTable.addCell(cell);
			
		 }catch(Exception e){
			throw new WTException(e);
		}
		return mainTable;
	}

	/** creating the Legal Compliance MOA Table
	  * @param params A Map of parameters to pass to the Object.  
	  * @param document The PDF Document which the content is going to be added to.
	  * @param material The material object
	  * @throws WTException For any error
	  * @return a PdfPTable for insertion into PDF page
	*/ 	
    private PdfPTable generateLglComplnceMOAtable(Map params, Document document, LCSMaterial material ) throws WTException 
	{

		TableData clrtd = null;
		TableColumn column  = null;
		String colorMOATypeName = "Multi-Object\\Legal Compliance";
		FlexType colorMOAType = null;
		Collection colorColl = new ArrayList() ;
		SearchResults results = null;
		PdfPTable mainTable = new PdfPTable(1);
		StringTokenizer colorAttParser = new StringTokenizer(LGL_COMLNC_MOA_ATT_COLUMN_ORDER, ",");
		StringTokenizer colorColParser = new StringTokenizer(LGL_COMLNC_MOA_ATT_COLUMN_ORDER, ",");

		while(colorAttParser.hasMoreTokens())
		{
			String attkey = colorAttParser.nextToken().trim();
			colorColl.add(attkey);
		}

		int i = colorColl.size();
		PdfPTable table = new PdfPTable(i);
		//table.addCell(createLabelCell("Legal Compliance", i));
		table.addCell(createLabelCell("LEGAL COMPLIANCE", i));
		addSpaceBetweenRows(table, i);

		try
		{
			FlexType materialType = material.getFlexType();
			FlexTypeAttribute colAtt = materialType.getAttribute("hbiLegalCompliance");
			colorMOAType = FlexTypeCache.getFlexTypeFromPath(colorMOATypeName);
			results = findHbiMOADataCollection(material, colAtt, colorMOAType, colorColl);
			Collection colDataCollection =results.getResults();
			Iterator colDataIter = colDataCollection.iterator();
			Collection colLabelColumns = getColumns(colorColParser, colorMOAType);
			Iterator colLblColsIter = colLabelColumns.iterator();

			while(colLblColsIter.hasNext()){
				column = (TableColumn)colLblColsIter.next();
				table.addCell(createLabelCell(column.getHeaderLabel()));
			}

			while(colDataIter.hasNext()){
				 clrtd = (TableData)colDataIter.next();
				 Iterator colLblColsIter2 = colLabelColumns.iterator();
				 while(colLblColsIter2.hasNext()){
					column = (TableColumn)colLblColsIter2.next();
					table.addCell(createDataCell(column.getPDFDisplayValue(clrtd)));		
				 }
			}

			PdfPCell cell = new PdfPCell(table);
			cell.setBorder(0);
			mainTable.addCell(cell);
			
		 }catch(Exception e){
			throw new WTException(e);
		}
		return mainTable;
	}

	/** creating the Marketing Claims MOA Table
	  * @param params A Map of parameters to pass to the Object.  
	  * @param document The PDF Document which the content is going to be added to.
	  * @param material The material object
	  * @throws WTException For any error
	  * @return a PdfPTable for insertion into PDF page
	*/ 	
    private PdfPTable generateMrktngClmsMOAtable(Map params, Document document, LCSMaterial material ) throws WTException 
	{

		TableData clrtd = null;
		TableColumn column  = null;
		String colorMOATypeName = "Multi-Object\\Marketing Claims";
		FlexType colorMOAType = null;
		Collection colorColl = new ArrayList() ;
		SearchResults results = null;
		PdfPTable mainTable = new PdfPTable(1);
		StringTokenizer colorAttParser = new StringTokenizer(MARTNG_CLMS_MOA_ATT_COLUMN_ORDER, ",");
		StringTokenizer colorColParser = new StringTokenizer(MARTNG_CLMS_MOA_ATT_COLUMN_ORDER, ",");

		while(colorAttParser.hasMoreTokens())
		{
			String attkey = colorAttParser.nextToken().trim();
			colorColl.add(attkey);
		}

		int i = colorColl.size();
		PdfPTable table = new PdfPTable(i);
		//table.addCell(createLabelCell("Marketing Claims", i));
		table.addCell(createLabelCell("MARKETING CLAIMS", i));
		addSpaceBetweenRows(table, i);

		try
		{
			FlexType materialType = material.getFlexType();
			FlexTypeAttribute colAtt = materialType.getAttribute("hbiMarketingClaims");
			colorMOAType = FlexTypeCache.getFlexTypeFromPath(colorMOATypeName);
			results = findHbiMOADataCollection(material, colAtt, colorMOAType, colorColl);
			Collection colDataCollection =results.getResults();
			Iterator colDataIter = colDataCollection.iterator();
			Collection colLabelColumns = getColumns(colorColParser, colorMOAType);
			Iterator colLblColsIter = colLabelColumns.iterator();

			while(colLblColsIter.hasNext()){
				column = (TableColumn)colLblColsIter.next();
				table.addCell(createLabelCell(column.getHeaderLabel()));
			}

			while(colDataIter.hasNext()){
				 clrtd = (TableData)colDataIter.next();
				 Iterator colLblColsIter2 = colLabelColumns.iterator();
				 while(colLblColsIter2.hasNext()){
					column = (TableColumn)colLblColsIter2.next();
					table.addCell(createDataCell(column.getPDFDisplayValue(clrtd)));		
				 }
			}

			PdfPCell cell = new PdfPCell(table);
			cell.setBorder(0);
			mainTable.addCell(cell);
			
		 }catch(Exception e){
			throw new WTException(e);
		}
		return mainTable;
	}
	
	//added by sobabu for 131155-15
	//commented for 20173-17
	/*private PdfPTable generateFinishDimensionsMOAtable(Map params, Document document, LCSMaterial material ) throws WTException 
	{

		TableData finishAtttd = null;
		TableColumn column  = null;
		String finishAttMOATypeName = "Multi-Object\\Finish Attribute MOA";;
		FlexType finishAttMOAType = null;
		Collection finishAttColl = new ArrayList();
		SearchResults results = null;
		PdfPTable mainTable = new PdfPTable(1);
		StringTokenizer finishAttParser = new StringTokenizer(FINISHATT_MOA_ATT_COLUMN_ORDER, ",");
		StringTokenizer finishColParser = new StringTokenizer(FINISHATT_MOA_ATT_COLUMN_ORDER, ",");

		while(finishAttParser.hasMoreTokens())
		{
			String attkey = finishAttParser.nextToken();
			finishAttColl.add(attkey);
		}

		int i =  finishAttColl.size();
		PdfPTable table = new PdfPTable(i);
		table.addCell(createLabelCell("FINISH DIMENSIONS", i));
		addSpaceBetweenRows(table, i);

		try
		{
			FlexType materialType = material.getFlexType();
			FlexTypeAttribute finishAtt = materialType.getAttribute("hbiFinishAttributeMOA");
			finishAttMOAType = FlexTypeCache.getFlexTypeFromPath(finishAttMOATypeName);
			results = findHbiMOADataCollection(material, finishAtt, finishAttMOAType, finishAttColl);
			Collection finishAttDataColl =results.getResults();
			Iterator finishAttDataIter = finishAttDataColl.iterator();
			Collection finishLabelColumns = getColumns(finishColParser, finishAttMOAType);
			Iterator finishLabelIter = finishLabelColumns.iterator();
   
			while(finishLabelIter.hasNext()){
				column = (TableColumn)finishLabelIter.next();
				table.addCell(createLabelCell(column.getHeaderLabel()));
			}
			while(finishAttDataIter.hasNext()){
				 finishAtttd = (TableData)finishAttDataIter.next();
				 Iterator finishLabelIter2 = finishLabelColumns.iterator();
				 while(finishLabelIter2.hasNext()){
					column = (TableColumn)finishLabelIter2.next();
					table.addCell(createDataCell(column.getPDFDisplayValue(finishAtttd)));		
				 }
			}
		
			PdfPCell cell = new PdfPCell(table);
			cell.setBorder(0);
			mainTable.addCell(cell);
			
		} catch (Exception e){
			 throw new WTException(e);
		}

		return mainTable;
	}
	//ended
	*/

	/** searching for MOA type attribute data
	  * @param owner The Material or Material Supplier object 
	  * @param attribute The MOA attribute
	  * @param moaType The MOA flextype object
	  * @param attCols The collection of attributes belonging to MOA table
	  * @throws WTException For any error
	  * @return SearchResults 
	*/ 	
       
	private SearchResults findHbiMOADataCollection(WTObject owner, FlexTypeAttribute attribute, FlexType moaType, Collection attCols )throws WTException 
	{
		String sortKey = "LCSMOAObject.branchId";
		FlexTypeGenerator fg = new FlexTypeGenerator();			
		PreparedQueryStatement moaQueryStmt = LCSMOAObjectQuery.findMOACollectionDataQuery(owner, attribute, sortKey, false);
		moaQueryStmt = fg.createSearchResultsQueryColumns(attCols, moaType, moaQueryStmt);
		moaQueryStmt.appendSortBy(new QueryColumn(LCSMOAObject.class, "sortingNumber"));
		SearchResults searchresults = LCSQuery.runDirectQuery(moaQueryStmt);
		return searchresults;
	}

	/** Creates MOA Table header row label table
	  * @param attParser The tokenizer for MOA data attributes
	  * @param type The MOA type
      * @throws WTException
      * @return Collection of columns
     */
	public Collection getColumns(StringTokenizer attParser, FlexType type) throws WTException 
	{		    
		Collection columns = new Vector();
		TableColumn column = null;
		FlexTypeAttribute att = null;
		FlexTypeGenerator flexg = new FlexTypeGenerator();
		while(attParser.hasMoreTokens())
		{
			column = new TableColumn();
			String attName = attParser.nextToken().trim();
			try{
				att = type.getAttribute(attName);
				column = flexg.createTableColumn(att, type, false);
				columns.add(column);
			}catch(Exception et){
				throw new WTException(et);		 			 
			}
		}

		return columns;
    }

	/** Creates Material Details header row label table
	  * @param attParser The tokenizer for material attributes
	  * @param type The material type
      * @throws WTException
      * @return Collection of columns
    */
	public Collection getMatColumns(StringTokenizer attParser, FlexType materialType) throws WTException 
	{
		Collection columns = new Vector();
		TableColumn column = null;
		FlexTypeAttribute att = null;
		FlexTypeGenerator flexg = new FlexTypeGenerator();

		while(attParser.hasMoreTokens())
		{
			column = new TableColumn();
			String attName = attParser.nextToken().trim();
			//changed by sobabu for new material supplier att to come in material spec
			if("hbiCylSize".equals(attName) || "hbiFinWidth".equals(attName) || "hbiConWidth".equals(attName) || "hbiCutWidth".equals(attName) || "hbiFabricConstruction".equals(attName) || "hbiDyeMachineType".equals(attName)|| "hbiDyeTypes".equals(attName)|| "hbiFinishingMachineType".equals(attName)|| "hbiFinishingType".equals(attName)|| "hbiPrintMachineTypes".equals(attName)|| "hbiPrintType".equals(attName)){
				flexg.setScope("MATERIAL-SUPPLIER");
				flexg.setLevel(null);
			} else {
				flexg.setScope("MATERIAL");
				flexg.setLevel(null);
			}
			try{
				att = materialType.getAttribute(attName);
				column = flexg.createTableColumn(att, materialType, false);
				columns.add(column);
			}catch(WTException et){
			 
				throw new WTException(et);		
			}
		}

		return columns;
     }

    /**returns the PdfPCell containing header label for  MOA table 
     * @param label
     * @return  
	*/ 
	private PdfPCell createLabelCell(String label)
	{	    
		Font font = pgh.getCellFont("RPT_HEADER","Left", null);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(label, font));
	    pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_HEADER", null));  
		pdfpcell.setHorizontalAlignment (Element.ALIGN_LEFT);
	    pdfpcell.setVerticalAlignment(Element.ALIGN_TOP);
		
	    return pdfpcell;
    }

	/**returns the PdfPCell containing data for  MOA table 
     * @param label
     * @return 
	 */ 
	private  PdfPCell createDataCell(String data)
   {
	    Font font = pgh.getCellFont("RPT_TBD","Left", null);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(data, font));
		pdfpcell.setHorizontalAlignment (Element.ALIGN_LEFT);
	    pdfpcell.setVerticalAlignment(Element.ALIGN_TOP);
	    return pdfpcell;
    }

	/**returns the PdfPCell containing header label for material details 
      * @param label
      * @return  
	*/ 
	private PdfPCell createMatLabelCell(String label)
	{	    
		Font font = pgh.getCellFont("RPT_HEADER","Left", null);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(label, font));
		pdfpcell.setBorder(0);
		//pdfpcell.setPadding(4F);
	    pdfpcell.setHorizontalAlignment (Element.ALIGN_LEFT);
	    pdfpcell.setVerticalAlignment(Element.ALIGN_TOP); 
	    return pdfpcell;
    }

	/**returns the PdfPCell containing header dispaly value for material details 
     * @param label
     * @return  
	*/
	private PdfPCell createMatDataCell(String label)
	{	    
		Font font = pgh.getCellFont("DISPLAYTEXT","Left", null);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(label, font));
		pdfpcell.setBorder(0);
		//pdfpcell.setPadding(4F);
	    pdfpcell.setHorizontalAlignment (Element.ALIGN_LEFT);
	    pdfpcell.setVerticalAlignment(Element.ALIGN_TOP);       
	    return pdfpcell;
    }

	/**returns the PdfPCell containing label for each section
      * @param label
	  * @param colNumber
      * @return  
	*/ 
	private PdfPCell createLabelCell(String label, int colNumber)
	{	    
		Font font = pgh.getCellFont("RPT_HEADER","Left", null);       
		PdfPCell Label = new PdfPCell(pgh.multiFontPara(label, font));
		Label.setColspan(colNumber);
		Label.setUseBorderPadding(true);
		//Label.setPadding(4F);
		Label.setBorder(0);
		Label.setHorizontalAlignment (Element.ALIGN_LEFT);
	    Label.setVerticalAlignment(Element.ALIGN_TOP);    
		    
	    return Label;
    }

	private void addSpaceBetweenRows(PdfPTable table,  int colNumber) throws WTException
    {
		PdfPCell spacerCell = new PdfPCell(pgh.multiFontPara(" "));
		spacerCell.setColspan(colNumber);
		spacerCell.setFixedHeight(3.0F); 
		spacerCell.setBorder(0);
		table.addCell(spacerCell);
    }

}// class