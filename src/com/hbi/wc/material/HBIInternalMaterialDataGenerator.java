package com.hbi.wc.material;

import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.client.web.pdf.PDFTableGenerator;
import wt.util.*;
import com.lcs.wc.util.*;
import wt.fc.WTObject;
//import wt.part.WTPartMaster;
import com.lcs.wc.db.*;
import com.lcs.wc.flextype.*;
import com.lcs.wc.client.web.*;
import com.lcs.wc.client.web.pdf.*;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lcs.wc.document.ImagePagePDFGenerator;
import com.lcs.wc.document.LCSDocument;
import java.util.*;


/* HBIInternalMaterialDataGenerator.java
 *
 * This file used to generates the All pages of data except header for a MaterialSpecification Report
 *
 */

public class HBIInternalMaterialDataGenerator {

        //private static String fontSize = "8";
        public static PDFGeneratorHelper pgh = new PDFGeneratorHelper();


		public static String HEADER_HEIGHT = "HEADER_HEIGHT";

	    public static String MATERIAL_MASTER_ID = "MATERIAL_MASTER_ID";
	    private static final String SPEC_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIInternalMaterialDataGenerator.SpecMOAColumnsOrder");
		private static final String ATHC_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIInternalMaterialDataGenerator.AthcMOAColumnsOrder");
	    private static final String LEGAL_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIInternalMaterialDataGenerator.LegalMOAColumnsOrder");
	    private static final String STRETCH_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIInternalMaterialDataGenerator.StretchMOAColumnsOrder");
		private static final String MKTCLAIMS_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIInternalMaterialDataGenerator.MrktClaimsMOAColumnsOrder");
		private static final String COLOR_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIInternalMaterialDataGenerator.ColorMOAColumnsOrder");
		private static final String WASHINST_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIInternalMaterialDataGenerator.WASHINSTMOAColumnsOrder");
		private static final String YARNCOMP_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIInternalMaterialDataGenerator.YarnCompMOAColumnsOrder");
		private static final String YARNSHORTCOMP_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIInternalMaterialDataGenerator.YarnShortCompMOAColumnsOrder");
		private static final String KNITTING_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIInternalMaterialDataGenerator.KnittingFinishingMOAColumnsOrder");
		private static final String FINISHATT_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIInternalMaterialDataGenerator.FinishAttMOAColumnsOrder");
		//private static final String FINISHATT_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIInternalMaterialDataGenerator.FinishAttMOAColumnsOrder");
		private static final String FINISHROUTING_MOA_ATT_COLUMN_ORDER = LCSProperties.get("com.hbi.wc.material.HBIInternalMaterialDataGenerator.FinishingRoutingMOAColumnsOrder");
		
		PDFTableGenerator ptg = null;
 
	   public  HBIInternalMaterialDataGenerator(){

       }

	  /* public Collection getPages(Map params, Document document) throws WTException {

			Collection allPages = new ArrayList();
			WTPartMaster materialMaster = (WTPartMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
    		LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);

			if("Fabric Make".equalsIgnoreCase(material)){


			}
			allPages.add(generatePage1IntMatMOATables(params, document));
			

	   }*/

 
	 /** returns All MOA tables for MaterialSpecification report
       * which can be added to a PDF Document page1
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 public PdfPTable generatePage1IntMatMOATables(Map params, Document document)throws WTException {

		  
		  //FabricMake page 1 
		  PdfPTable p1Table = new PdfPTable(1);
		  PdfPTable table;
		  PdfPCell cell ;
		  //WTPartMaster materialMaster = (WTPartMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
		  LCSMaterialMaster materialMaster = (LCSMaterialMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
    	  LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);


		  table = generateSpecMOAtable(params,document, material);
		  cell = new PdfPCell(table);
  		  cell.setPadding(4F);
		  p1Table.addCell(cell);
			
           // this is for adding for divide line between tables
		  //addSpaceBetweenRows(p1Table, 1);	

		  table = generateAestheticMOAtable(params, document, material);
		  cell = new PdfPCell(table);
  		  cell.setPadding(4F);
  		  p1Table.addCell(cell);

  		  //addSpaceBetweenRows(p1Table, 1);

		  table = generateStretchMOAtable(params, document, material);
		  cell = new PdfPCell(table);
  		  cell.setPadding(4F);
		  p1Table.addCell(cell);

		  
		  table = generateColorMOAtable(params, document, material);
		  cell = new PdfPCell(table);
  		  cell.setPadding(4F);
		  p1Table.addCell(cell);

		  table = generateLegalComplianceMOAtable(params, document, material);
		  cell = new PdfPCell(table);
  		  cell.setPadding(4F);
   		  p1Table.addCell(cell);

		  table = generateMarketingClaimsMOAtable(params, document, material);
		  cell = new PdfPCell(table);
  		  cell.setPadding(4F);
		  p1Table.addCell(cell);


		  table = generateWashInstructionsMOAtable(params, document, material);
		  cell = new PdfPCell(table);
  		  cell.setPadding(4F);
		  p1Table.addCell(cell);
		 		 
		  return p1Table;	
	 }

 	 /** returns All MOA tables for Internal Material Specification report
       * which can be added to a PDF Document page2
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 public PdfPTable generatePage2InteMatMOATables(Map params, Document document)throws WTException {

		  PdfPCell cell;
		  PdfPTable pTable = null;
		  //WTPartMaster materialMaster = (WTPartMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
		  LCSMaterialMaster materialMaster = (LCSMaterialMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
    	  LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);
		  //FabricMake page 2
		  PdfPTable p2Table = new PdfPTable(1);

		  //pTable = generateYarnShortComponentMOAtable(params, document,material);
		  PdfPTable pTable1 = generateKnittingComments(params, document,material);
		  //PdfPTable table = new PdfPTable(2);
		  //table.addCell(new PdfPCell(pTable));
		  //table.addCell(new PdfPCell(pTable1));
		  cell = new PdfPCell(pTable1);
  		  cell.setPadding(4F);
  		  p2Table.addCell(cell);

		  //Commented by UST to hide Yarn Components Attributes for Fabric and it its subtypes.
		  /*pTable = generateYarnComponentMOAtable(params, document,material);
		  cell = new PdfPCell(pTable);
   		  cell.setPadding(4F);
		  p2Table.addCell(cell);*/

  		  pTable = generateKnittingFinishingMOAtable(params, document,material);
		  cell = new PdfPCell(pTable);
   		  cell.setPadding(4F);
  		  p2Table.addCell(cell);

		  return p2Table;
	 }


	 /** returns All MOA tables for Internal Material Specification report
       * which can be added to a PDF Document page3
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 public PdfPTable generatePage3InteMatMOATables(Map params, Document document)throws WTException {

		  PdfPCell cell;
		  PdfPTable pTable = null;
		  //WTPartMaster materialMaster = (WTPartMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
		  LCSMaterialMaster materialMaster = (LCSMaterialMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
    	  LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);
		  //FabricMake page 3
		  PdfPTable p3Table = new PdfPTable(1);

		  //pTable = generateYarnShortComponentMOAtable(params, document,material);
		  PdfPTable pTable1 = generateFinishingComments(params, document,material);
		  //PdfPTable table = new PdfPTable(2);
		  //table.addCell(new PdfPCell(pTable));
		  //table.addCell(new PdfPCell(pTable1));
		  cell = new PdfPCell(pTable1);
  		  cell.setPadding(4F);
  		  p3Table.addCell(cell);

		  pTable = generateFinishRoutingMOAtable(params, document,material);
		  cell = new PdfPCell(pTable);
   		  cell.setPadding(4F);
		  p3Table.addCell(cell);

		  pTable = generateFinishDimensionsMOAtable(params, document,material);
		  cell = new PdfPCell(pTable);
   		  cell.setPadding(4F);
  		  p3Table.addCell(cell);

		  return p3Table;
	  }

	  public Element generateImagePage(Map params, Document document)throws WTException {
		
		  Element ele = null;
		  //WTPartMaster materialMaster = (WTPartMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
		  LCSMaterialMaster materialMaster = (LCSMaterialMaster)LCSQuery.findObjectById((String)params.get(MATERIAL_MASTER_ID));
    	  LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(materialMaster);
		  ele = findInternalMatImage(params, document, material);
			return ele;
	  }




	 /** returns Specifications MOA table for MaterialSpecification report
       * which can be added to a PDF Document page1
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 private PdfPTable generateSpecMOAtable(Map params, Document document, LCSMaterial material ) throws WTException {

			PDFTableGenerator ptg = null;
 		    Collection pdfData = new ArrayList();
			TableData spectd = null;
			TableColumn column  = null;
			String specMOATypeName = "Multi-Object\\Fabrics";
			FlexType specMOAType = null;
			Collection specAttCols = new ArrayList();
			PdfPTable mainTable = new PdfPTable(1);
			StringTokenizer specAttParser = new StringTokenizer(SPEC_MOA_ATT_COLUMN_ORDER, ",");
			StringTokenizer specColParser = new StringTokenizer(SPEC_MOA_ATT_COLUMN_ORDER, ",");
			while(specAttParser.hasMoreTokens()){
    			String attkey = specAttParser.nextToken();
				specAttCols.add(attkey);

			}
 			PdfPTable table = new PdfPTable(specAttCols.size());
			//table.addCell(createLabelCell("Specifications:", specAttCols.size()));
			table.addCell(createLabelCell("SPECIFICATIONS", specAttCols.size()));
			try
            {
				FlexType materialType = material.getFlexType();
				FlexTypeAttribute fabricsAtt = materialType.getAttribute("hbiFabrics");
				specMOAType = FlexTypeCache.getFlexTypeFromPath(specMOATypeName);
				Collection specDataCollection = findHbiMOADataCollection(material, fabricsAtt, specMOAType, specAttCols);
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
				return mainTable;

			} catch (Exception e){
				 throw new WTException(e);
			}
		}

	 /** returns Aesthetics MOA table for MaterialSpecification report
       * which can be added to a PDF Document page1
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 private PdfPTable generateAestheticMOAtable(Map params, Document document, LCSMaterial material ) throws WTException {

			PDFTableGenerator ptg = null;
			TableData athctd = null;
			TableColumn column  = null;
			String aestheticMOATypeName = "Multi-Object\\Aesthetic";
			FlexType athcMOAType = null;
			Collection aestheticColl = new ArrayList() ;
			PdfPTable mainTable = new PdfPTable(1);
			StringTokenizer athcAttParser = new StringTokenizer(ATHC_MOA_ATT_COLUMN_ORDER, ",");
			StringTokenizer athcColParser = new StringTokenizer(ATHC_MOA_ATT_COLUMN_ORDER, ",");
			while(athcAttParser.hasMoreTokens()){
    			String attkey = athcAttParser.nextToken();
				aestheticColl.add(attkey);

			 }
 			PdfPTable table = new PdfPTable(aestheticColl.size());
			//table.addCell(createLabelCell("Aesthetics", aestheticColl.size()));
			table.addCell(createLabelCell("AESTHETICS", aestheticColl.size()));
            try
            {
				FlexType materialType = material.getFlexType();
				FlexTypeAttribute aestheticAtt = materialType.getAttribute("hbiAesthetic");
				athcMOAType = FlexTypeCache.getFlexTypeFromPath(aestheticMOATypeName);
				Collection athcDataCollection = findHbiMOADataCollection(material, aestheticAtt, athcMOAType, aestheticColl);
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
				return mainTable;

			 }catch(Exception e){
				throw new WTException(e);
			}

		}

	 /** returns Legal Compliance MOA table for MaterialSpecification report
       * which can be added to a PDF Document page1
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 private PdfPTable generateLegalComplianceMOAtable(Map params, Document document, LCSMaterial material ) throws WTException {

			PDFTableGenerator ptg = null;
			TableData legaltd = null;
			TableColumn column  = null;
			String legalcompMOATypeName = "Multi-Object\\Legal Compliance";
			FlexType legalcompMOAType = null;
			Collection legalcompColl = new ArrayList() ;
			PdfPTable mainTable = new PdfPTable(1);
			StringTokenizer legalcompAttParser = new StringTokenizer(LEGAL_MOA_ATT_COLUMN_ORDER, ",");
			StringTokenizer legalcompColParser = new StringTokenizer(LEGAL_MOA_ATT_COLUMN_ORDER, ",");
			while(legalcompAttParser.hasMoreTokens()){
    			String attkey = legalcompAttParser.nextToken();
				legalcompColl.add(attkey);

			 }
 			PdfPTable table = new PdfPTable(legalcompColl.size());
			//table.addCell(createLabelCell("Legal Compliance", legalcompColl.size()));
			table.addCell(createLabelCell("LEGAL COMPLIANCE", legalcompColl.size()));
            try
            {
				FlexType materialType = material.getFlexType();
				FlexTypeAttribute legalcompAtt = materialType.getAttribute("hbiLegalCompliance");
				legalcompMOAType = FlexTypeCache.getFlexTypeFromPath(legalcompMOATypeName);
				Collection legalDataCollection = findHbiMOADataCollection(material, legalcompAtt, legalcompMOAType, legalcompColl);
				Iterator legalDataIter = legalDataCollection.iterator();
			    Collection legalLabelColumns = getColumns(legalcompColParser, legalcompMOAType);
				Iterator legalLblColsIter = legalLabelColumns.iterator();
				while(legalLblColsIter.hasNext()){
					column = (TableColumn)legalLblColsIter.next();
					table.addCell(createLabelCell(column.getHeaderLabel()));
				}
				while(legalDataIter.hasNext()){
					 legaltd = (TableData)legalDataIter.next();
	 				 Iterator legalLblColsIter2 = legalLabelColumns.iterator();
					 while(legalLblColsIter2.hasNext()){
						column = (TableColumn)legalLblColsIter2.next();
						table.addCell(createDataCell(column.getPDFDisplayValue(legaltd)));		
					 }
				}
				PdfPCell cell = new PdfPCell(table);
			    cell.setBorder(0);
				mainTable.addCell(cell);
				return mainTable;

			}catch(Exception e) {
				throw new WTException(e);
			}

		}

	 /** returns Stretch MOA table for MaterialSpecification report
       * which can be added to a PDF Document page1
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 private PdfPTable generateStretchMOAtable(Map params, Document document, LCSMaterial material ) throws WTException {

			PDFTableGenerator ptg = null;
			TableData stretchtd = null;
			TableColumn column  = null;
			String stretchMOATypeName = "Multi-Object\\Stretch";
			FlexType stretchMOAType = null;
			Collection stretchColl = new ArrayList() ;
			PdfPTable mainTable = new PdfPTable(1);
			StringTokenizer stretchAttParser = new StringTokenizer(STRETCH_MOA_ATT_COLUMN_ORDER, ",");
			StringTokenizer stretchColParser = new StringTokenizer(STRETCH_MOA_ATT_COLUMN_ORDER, ",");
			while(stretchAttParser.hasMoreTokens()){
    			String attkey = stretchAttParser.nextToken();
				stretchColl.add(attkey);

			 }
 			PdfPTable table = new PdfPTable(stretchColl.size());
			//table.addCell(createLabelCell("Stretch", stretchColl.size()));
			table.addCell(createLabelCell("STRETCH", stretchColl.size()));
            try
            {
				FlexType materialType = material.getFlexType();
				FlexTypeAttribute stretchAtt = materialType.getAttribute("Stretch");
				stretchMOAType = FlexTypeCache.getFlexTypeFromPath(stretchMOATypeName);
				Collection stretchDataCollection = findHbiMOADataCollection(material, stretchAtt, stretchMOAType, stretchColl);
				Iterator stretchDataIter = stretchDataCollection.iterator();
			    Collection stretchLabelColumns = getColumns(stretchColParser, stretchMOAType);
				Iterator stretchLblColsIter = stretchLabelColumns.iterator();
				while(stretchLblColsIter.hasNext()){
					column = (TableColumn)stretchLblColsIter.next();
					table.addCell(createLabelCell(column.getHeaderLabel()));
				}
				while(stretchDataIter.hasNext()){
					 stretchtd = (TableData)stretchDataIter.next();
	 				 Iterator stretchLblColsIter2 = stretchLabelColumns.iterator();
					 while(stretchLblColsIter2.hasNext()){
						column = (TableColumn)stretchLblColsIter2.next();
						table.addCell(createDataCell(column.getPDFDisplayValue(stretchtd)));		
					 }
				}
				PdfPCell cell = new PdfPCell(table);
			    cell.setBorder(0);
				mainTable.addCell(cell);
				return mainTable;

			}catch(Exception e){
				throw new WTException(e);
			}

		}

	 /** returns Marketing Claims MOA table for MaterialSpecification report
       * which can be added to a PDF Document page1
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 private PdfPTable generateMarketingClaimsMOAtable(Map params, Document document, LCSMaterial material ) throws WTException {

			PDFTableGenerator ptg = null;
			TableData mktClaimstd = null;
			TableColumn column  = null;
			String mktClaimsMOATypeName = "Multi-Object\\Marketing Claims";
			FlexType mktClaimsMOAType = null;
			Collection mktClaimsColl = new ArrayList() ;
			PdfPTable mainTable = new PdfPTable(1);
			StringTokenizer mktClaimsAttParser = new StringTokenizer(MKTCLAIMS_MOA_ATT_COLUMN_ORDER, ",");
			StringTokenizer mktClaimsColParser = new StringTokenizer(MKTCLAIMS_MOA_ATT_COLUMN_ORDER, ",");
			while(mktClaimsAttParser.hasMoreTokens()){
    			String attkey = mktClaimsAttParser.nextToken();
				mktClaimsColl.add(attkey);

			 }
 			PdfPTable table = new PdfPTable(mktClaimsColl.size());
			//table.addCell(createLabelCell("Marketing Claims", mktClaimsColl.size()));
			table.addCell(createLabelCell("MARKETING CLAIMS", mktClaimsColl.size()));
            try
            {
				FlexType materialType = material.getFlexType();
				FlexTypeAttribute mktClaimsAtt = materialType.getAttribute("hbiMarketingClaims");
				mktClaimsMOAType = FlexTypeCache.getFlexTypeFromPath(mktClaimsMOATypeName);
				Collection mktClaimsDataCollection = findHbiMOADataCollection(material, mktClaimsAtt, mktClaimsMOAType, mktClaimsColl);
				Iterator mktClaimsDataIter = mktClaimsDataCollection.iterator();
			    Collection mktClaimsLblColumns = getColumns(mktClaimsColParser, mktClaimsMOAType);
				Iterator mktClaimsLblColsIter = mktClaimsLblColumns.iterator();
				while(mktClaimsLblColsIter.hasNext()){
					column = (TableColumn)mktClaimsLblColsIter.next();
					table.addCell(createLabelCell(column.getHeaderLabel()));
				}
				while(mktClaimsDataIter.hasNext()){
					 mktClaimstd = (TableData)mktClaimsDataIter.next();
	 				 Iterator mktClaimsLblColsIter2 = mktClaimsLblColumns.iterator();
					 while(mktClaimsLblColsIter2.hasNext()){
						column = (TableColumn)mktClaimsLblColsIter2.next();
						table.addCell(createDataCell(column.getPDFDisplayValue(mktClaimstd)));		
					 }
				}
				PdfPCell cell = new PdfPCell(table);
			    cell.setBorder(0);
				mainTable.addCell(cell);
				return mainTable;

			}catch(Exception e){
				throw new WTException(e);
			}
		}

				
	 /** returns Color MOA table for MaterialSpecification report
       * which can be added to a PDF Document page1
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 private PdfPTable generateColorMOAtable(Map params, Document document, LCSMaterial material ) throws WTException {

			PDFTableGenerator ptg = null;
			TableData colortd = null;
			TableColumn column  = null;
			String colorMOATypeName = "Multi-Object\\Color";
			FlexType colorMOAType = null;
			Collection colorColl = new ArrayList() ;
			PdfPTable mainTable = new PdfPTable(1);
			StringTokenizer colorAttParser = new StringTokenizer(COLOR_MOA_ATT_COLUMN_ORDER, ",");
			StringTokenizer colorColParser = new StringTokenizer(COLOR_MOA_ATT_COLUMN_ORDER, ",");
			while(colorAttParser.hasMoreTokens()){
    			String attkey = colorAttParser.nextToken();
				colorColl.add(attkey);
			 }
 			PdfPTable table = new PdfPTable(colorColl.size());
			//table.addCell(createLabelCell("Color", colorColl.size()));
			table.addCell(createLabelCell("COLOR", colorColl.size()));
            try
            {
				FlexType materialType = material.getFlexType();
				FlexTypeAttribute colorAtt = materialType.getAttribute("hbiColor");
				colorMOAType = FlexTypeCache.getFlexTypeFromPath(colorMOATypeName);
				Collection colorDataCollection = findHbiMOADataCollection(material, colorAtt, colorMOAType, colorColl);
				Iterator colorDataIter = colorDataCollection.iterator();
			    Collection colorLblColumns = getColumns(colorColParser, colorMOAType);
				Iterator colorLblColsIter = colorLblColumns.iterator();
				while(colorLblColsIter.hasNext()){
					column = (TableColumn)colorLblColsIter.next();
					table.addCell(createLabelCell(column.getHeaderLabel()));
				}
				while(colorDataIter.hasNext()){
					 colortd = (TableData)colorDataIter.next();
	 				 Iterator colorLblColsIter2 = colorLblColumns.iterator();
					 while(colorLblColsIter2.hasNext()){
						column = (TableColumn)colorLblColsIter2.next();
						table.addCell(createDataCell(column.getPDFDisplayValue(colortd)));		
					 }
				}
				PdfPCell cell = new PdfPCell(table);
			    cell.setBorder(0);
				mainTable.addCell(cell);
				return mainTable;

			}catch(Exception e) {
				throw new WTException(e);
			}
		}

		
	 /** returns Washing Instructions MOA table for MaterialSpecification report
       * which can be added to a PDF Document page1
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 private PdfPTable generateWashInstructionsMOAtable(Map params, Document document, LCSMaterial material ) throws WTException {

			PDFTableGenerator ptg = null;
			TableData washInsttd = null;
			TableColumn column  = null;
			String washInstMOATypeName = "Multi-Object\\Wash Instructions";
			FlexType washInstMOAType = null;
			Collection washInstColl = new ArrayList() ;
			PdfPTable mainTable = new PdfPTable(1);
			StringTokenizer washInstAttParser = new StringTokenizer(WASHINST_MOA_ATT_COLUMN_ORDER, ",");
			StringTokenizer washInstColParser = new StringTokenizer(WASHINST_MOA_ATT_COLUMN_ORDER, ",");
			while(washInstAttParser.hasMoreTokens()){
    			String attkey = washInstAttParser.nextToken();
				washInstColl.add(attkey);
			 }
 			PdfPTable table = new PdfPTable(washInstColl.size());
			//table.addCell(createLabelCell("Washing Instructions", washInstColl.size()));
			table.addCell(createLabelCell("WASHING INSTRUCTIONS", washInstColl.size()));
            try
            {
				FlexType materialType = material.getFlexType();
				FlexTypeAttribute washInstAtt = materialType.getAttribute("hbiWashInstructions");
				washInstMOAType = FlexTypeCache.getFlexTypeFromPath(washInstMOATypeName);
				Collection washInstDataCollection = findHbiMOADataCollection(material, washInstAtt, washInstMOAType, washInstColl);
				Iterator cwashInstDataIter = washInstDataCollection.iterator();
			    Collection washInstLblColumns = getColumns(washInstColParser, washInstMOAType);
				Iterator washInstLblColsIter = washInstLblColumns.iterator();
				while(washInstLblColsIter.hasNext()){
					column = (TableColumn)washInstLblColsIter.next();
					table.addCell(createLabelCell(column.getHeaderLabel()));
				}
				while(cwashInstDataIter.hasNext()){
					 washInsttd = (TableData)cwashInstDataIter.next();
	 				 Iterator washInstLblColsIter2 = washInstLblColumns.iterator();
					 while(washInstLblColsIter2.hasNext()){
						column = (TableColumn)washInstLblColsIter2.next();
						table.addCell(createDataCell(column.getPDFDisplayValue(washInsttd)));		
					 }
				}
				PdfPCell cell = new PdfPCell(table);
			    cell.setBorder(0);
				mainTable.addCell(cell);
				return mainTable;

			}catch(Exception e){
				throw new WTException(e);
			}

		}

	  /** returns Yarn Coponenets MOA table for MaterialSpecification report
       * which can be added to a PDF Document page2 and page3
	   * Note: only two columns need to print (Yarn Item, Yarn Description)
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	  private PdfPTable generateYarnShortComponentMOAtable(Map params, Document document, LCSMaterial material ) throws WTException {

			TableData yarnshorttd = null;
			TableColumn column  = null;
			//PdfPTable ptable =  null;
			String yarnCompMOATypeName = "Multi-Object\\Yarn Component Attributes";
			FlexType yarnCompMOAType = null;
			Collection yarnshortCompColl = new ArrayList() ;
			PdfPTable mainTable = new PdfPTable(1);
			StringTokenizer yarnshortCompAttParser = new StringTokenizer(YARNSHORTCOMP_MOA_ATT_COLUMN_ORDER, ",");
			StringTokenizer yarnshortCompColParser = new StringTokenizer(YARNSHORTCOMP_MOA_ATT_COLUMN_ORDER, ",");
			while(yarnshortCompAttParser.hasMoreTokens()){
				String attkey = yarnshortCompAttParser.nextToken();
				yarnshortCompColl.add(attkey);
			}
			PdfPTable table = new PdfPTable(yarnshortCompColl.size());
			//table.addCell(createLabelCell("Yarn Table", yarnshortCompColl.size()));
			table.addCell(createLabelCell("YARN TABLE", yarnshortCompColl.size()));
			FlexType materialType = material.getFlexType();
			FlexTypeAttribute fabricBomAtt = materialType.getAttribute("hbiFabricBom");
			yarnCompMOAType = FlexTypeCache.getFlexTypeFromPath(yarnCompMOATypeName);
			Collection yarnshortDataColl = findHbiMOADataCollection(material, fabricBomAtt, yarnCompMOAType, yarnshortCompColl);
			Iterator yarnshortDataIter = yarnshortDataColl.iterator();
			Collection yarnshortLabelColumns = getColumns(yarnshortCompColParser, yarnCompMOAType);
			Iterator yarnshortLblIter = yarnshortLabelColumns.iterator();
			while(yarnshortLblIter.hasNext()){
				column = (TableColumn)yarnshortLblIter.next();
				table.addCell(createLabelCell(column.getHeaderLabel()));
			}
			while(yarnshortDataIter.hasNext()){
				yarnshorttd = (TableData)yarnshortDataIter.next();
	 			Iterator yarnshortLblColsIter2 = yarnshortLabelColumns.iterator();
				while(yarnshortLblColsIter2.hasNext()){
					column = (TableColumn)yarnshortLblColsIter2.next();
					table.addCell(createDataCell(column.getPDFDisplayValue(yarnshorttd)));		
				}
			}
			PdfPCell cell = new PdfPCell(table);
			cell.setBorder(0);
			mainTable.addCell(cell);

			return mainTable;
		}

	 /** returns Knitting Spec comments for MaterialSpecification report
       * which can be added to a PDF Document page2
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 private PdfPTable generateKnittingComments(Map params, Document document, LCSMaterial material) throws WTException {

      PdfPTable mainTable = new PdfPTable(1);
	 // mainTable.addCell(createLabelCell("Knitting Comments", 1));
	  mainTable.addCell(createLabelCell("KNITTING COMMENTS", 1));
	  String knittingComm = (String)material.getValue("hbiKnittingComments");
	  PdfPCell cell = createDataCell(knittingComm); 
	  mainTable.addCell(cell);
	  return mainTable;

	 }
     
	 /** returns Yarn Coponenets MOA table for MaterialSpecification report
       * which can be added to a PDF Document page2
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 private PdfPTable generateYarnComponentMOAtable(Map params, Document document, LCSMaterial material ) throws WTException {

			TableData yarnComptd = null;
			TableColumn column  = null;
			PdfPTable ptable =  null;
			String yarnCompMOATypeName = "Multi-Object\\Yarn Component Attributes";
			FlexType yarnCompMOAType = null;
			Collection yarnCompColl = new ArrayList() ;
			PdfPTable mainTable = new PdfPTable(1);
			StringTokenizer yarnCompAttParser = new StringTokenizer(YARNCOMP_MOA_ATT_COLUMN_ORDER, ",");
			StringTokenizer yarnCompColParser = new StringTokenizer(YARNCOMP_MOA_ATT_COLUMN_ORDER, ",");
			while(yarnCompAttParser.hasMoreTokens()){
				String attkey = yarnCompAttParser.nextToken();
				yarnCompColl.add(attkey);
			}
			PdfPTable table = new PdfPTable(yarnCompColl.size());
			table.addCell(createLabelCell("YARN FEED TABLE", yarnCompColl.size()));
			FlexType materialType = material.getFlexType();
			FlexTypeAttribute fabricBomAtt = materialType.getAttribute("hbiFabricBom");
			yarnCompMOAType = FlexTypeCache.getFlexTypeFromPath(yarnCompMOATypeName);
			Collection yarnCompDataColl = findHbiMOADataCollection(material, fabricBomAtt, yarnCompMOAType, yarnCompColl);
			Iterator  yarnCompDataIter = yarnCompDataColl.iterator() ;
			Collection yarnCompLabelColumns = getColumns(yarnCompColParser, yarnCompMOAType);
			Iterator  yarnCompLblIter = yarnCompLabelColumns.iterator() ;
			while(yarnCompLblIter.hasNext()){
				column = (TableColumn)yarnCompLblIter.next();
				table.addCell(createLabelCell(column.getHeaderLabel()));
			}
			while(yarnCompDataIter.hasNext()){
				yarnComptd = (TableData)yarnCompDataIter.next();
	 			Iterator yarnCompLblIter2 = yarnCompLabelColumns.iterator();
				while(yarnCompLblIter2.hasNext()){
					column = (TableColumn)yarnCompLblIter2.next();
					table.addCell(createDataCell(column.getPDFDisplayValue(yarnComptd)));		
				}
			}
			PdfPCell cell = new PdfPCell(table);
			cell.setBorder(0);
			mainTable.addCell(cell);

			return mainTable;
		}
   
		
	 /** returns Knitting Finishing attributes MOA table for MaterialSpecification report
       * which can be added to a PDF Document page2 (Greige / Knitting Dimensions)
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 private PdfPTable generateKnittingFinishingMOAtable(Map params, Document document, LCSMaterial material ) throws WTException {

			TableData knittingFintd = null;
			TableColumn column  = null;
			String knittingMOATypeName = "Multi-Object\\Knitting Finishing attributes";
			FlexType knittingMOAType = null;
			Collection knittingFinAttColl = new ArrayList() ;
			PdfPTable mainTable = new PdfPTable(1);
			StringTokenizer knittingAttParser = new StringTokenizer(KNITTING_MOA_ATT_COLUMN_ORDER, ",");
			StringTokenizer knittingColParser = new StringTokenizer(KNITTING_MOA_ATT_COLUMN_ORDER, ",");
			while(knittingAttParser.hasMoreTokens()){
				String attkey = knittingAttParser.nextToken();
				knittingFinAttColl.add(attkey);
			}
			PdfPTable table = new PdfPTable(knittingFinAttColl.size());
			table.addCell(createLabelCell("GREIGE / KNITTING DIMENSIONS", knittingFinAttColl.size()));
			FlexType materialType = material.getFlexType();
			FlexTypeAttribute knittingAtt = materialType.getAttribute("hbiFabricKnitting");
			knittingMOAType = FlexTypeCache.getFlexTypeFromPath(knittingMOATypeName);
			Collection knittingFinishDataColl = findHbiMOADataCollection(material, knittingAtt, knittingMOAType, knittingFinAttColl);
			Iterator  knittingFinDataIter = knittingFinishDataColl.iterator() ;
			Collection knittingFinLabelColumns = getColumns(knittingColParser, knittingMOAType);
			Iterator  knittingFinLabelIter = knittingFinLabelColumns.iterator() ;
			while(knittingFinLabelIter.hasNext()){
				column = (TableColumn)knittingFinLabelIter.next();
				table.addCell(createLabelCell(column.getHeaderLabel()));
			}
			while(knittingFinDataIter.hasNext()){
				knittingFintd = (TableData)knittingFinDataIter.next();
	 			Iterator knittingFinLblIter2 = knittingFinLabelColumns.iterator();
				while(knittingFinLblIter2.hasNext()){
					column = (TableColumn)knittingFinLblIter2.next();
					table.addCell(createDataCell(column.getPDFDisplayValue(knittingFintd)));		
				}
			}
			PdfPCell cell = new PdfPCell(table);
			cell.setBorder(0);
			mainTable.addCell(cell);

			return mainTable;
	   }


	 /** returns Finishing Spec Comments for MaterialSpecification report
       * which can be added to a PDF Document page3
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 private PdfPTable generateFinishingComments(Map params, Document document, LCSMaterial material) throws WTException {

      PdfPTable mainTable = new PdfPTable(1);
  	  //mainTable.addCell(createLabelCell("Finishing Comments", 1));
	  mainTable.addCell(createLabelCell("FINISHING COMMENTS", 1));
	  String finisgingComm = (String)material.getValue("hbiFinishingComments");
	  PdfPCell cell = createDataCell(finisgingComm); 
	  mainTable.addCell(cell);
	  return mainTable;

	 }
		
	 /** returns Finish Routing MOA table for MaterialSpecification report
       * which can be added to a PDF Document page3
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 private PdfPTable generateFinishRoutingMOAtable(Map params, Document document, LCSMaterial material ) throws WTException {

			TableData finishRoutingtd = null;
			TableColumn column  = null;
			String finishRoutingMOATypeName = "Multi-Object\\Finish Routing";
			FlexType finishRoutingMOAType = null;
			Collection finishRoutingColl = new ArrayList() ;
			PdfPTable mainTable = new PdfPTable(1);
			StringTokenizer finishRoutingAttParser = new StringTokenizer(FINISHROUTING_MOA_ATT_COLUMN_ORDER, ",");
			StringTokenizer finishRoutingColParser = new StringTokenizer(FINISHROUTING_MOA_ATT_COLUMN_ORDER, ",");
			while(finishRoutingAttParser.hasMoreTokens()){
				String attkey = finishRoutingAttParser.nextToken();
				finishRoutingColl.add(attkey);
			}
			PdfPTable table = new PdfPTable(finishRoutingColl.size());
			//table.addCell(createLabelCell("Finishing Routing", finishRoutingColl.size()));
			table.addCell(createLabelCell("FINISHING ROUTING", finishRoutingColl.size()));
			FlexType materialType = material.getFlexType();
			FlexTypeAttribute finishRoutingAtt = materialType.getAttribute("hbiFabRouting");
			finishRoutingMOAType = FlexTypeCache.getFlexTypeFromPath(finishRoutingMOATypeName);
			Collection finishingRouthingColl = findHbiMOADataCollection(material, finishRoutingAtt, finishRoutingMOAType, finishRoutingColl);
			Iterator  finishingRouthingDataIter = finishingRouthingColl.iterator() ;
			Collection finishingLabelColumns = getColumns(finishRoutingColParser, finishRoutingMOAType);
			Iterator  finishingLblIter = finishingLabelColumns.iterator() ;
			while(finishingLblIter.hasNext()){
				column = (TableColumn)finishingLblIter.next();
				table.addCell(createLabelCell(column.getHeaderLabel()));
			}
			while(finishingRouthingDataIter.hasNext()){
				finishRoutingtd = (TableData)finishingRouthingDataIter.next();
	 			Iterator finishingLblIter2 = finishingLabelColumns.iterator();
				while(finishingLblIter2.hasNext()){
					column = (TableColumn)finishingLblIter2.next();
					table.addCell(createDataCell(column.getPDFDisplayValue(finishRoutingtd)));		
				}
			}
			PdfPCell cell = new PdfPCell(table);
			cell.setBorder(0);
			mainTable.addCell(cell);

			return mainTable;
	 }
	 
	 /** returns Finish Attribute MOA table for MaterialSpecification report
       * which can be added to a PDF Document page3 (Finish Dimensions)
       * @param params
	   * @pram document
       * @throws WTException
       * @return PdfPTable
     */
	 private PdfPTable generateFinishDimensionsMOAtable(Map params, Document document, LCSMaterial material ) throws WTException {

			TableData finishAtttd = null;
			TableColumn column  = null;
			String finishAttMOATypeName = "Multi-Object\\Finish Attribute MOA";
			FlexType finishAttMOAType = null;
			Collection finishAttColl = new ArrayList() ;
			PdfPTable mainTable = new PdfPTable(1);
			StringTokenizer finishAttParser = new StringTokenizer(FINISHATT_MOA_ATT_COLUMN_ORDER, ",");
			StringTokenizer finishColParser = new StringTokenizer(FINISHATT_MOA_ATT_COLUMN_ORDER, ",");
			while(finishAttParser.hasMoreTokens()){
				String attkey = finishAttParser.nextToken();
				finishAttColl.add(attkey);
			}
			PdfPTable table = new PdfPTable(finishAttColl.size());
			//table.addCell(createLabelCell("Finish Dimensions", finishAttColl.size()));
			table.addCell(createLabelCell("FINISH DIMENSIONS", finishAttColl.size()));
			FlexType materialType = material.getFlexType();
			FlexTypeAttribute finishAtt = materialType.getAttribute("hbiFinishAttributeMOA");
			finishAttMOAType = FlexTypeCache.getFlexTypeFromPath(finishAttMOATypeName);
			Collection finishAttDataColl = findHbiMOADataCollection(material, finishAtt, finishAttMOAType, finishAttColl);
			Iterator  finishAttDataIter = finishAttDataColl.iterator() ;
			Collection finishLabelColumns = getColumns(finishColParser, finishAttMOAType);
			Iterator  finishLabelIter = finishLabelColumns.iterator() ;

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

			return mainTable;
		}


		private Element findInternalMatImage(Map params, Document document, LCSMaterial material) throws WTException {

			boolean INCLUDE_COMMENTS = false;
			Collection imageColl = null;
			Element content = null;
			LCSMaterialQuery lcsMatQuery = new LCSMaterialQuery();
			imageColl = lcsMatQuery.getMaterialImages(material);
			if(imageColl.size()>0) {
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


	/** returns Collection of MOA table data
	  * @param material
	  * @param attribute
	  * @param moaType
	  * @param attcols
	  * @return Collection
	  */
     private Collection findHbiMOADataCollection(LCSMaterial material, FlexTypeAttribute attribute, FlexType moaType, Collection attCols )throws WTException {
		
			String sortKey = "LCSMOAObject.branchId";
			FlexTypeGenerator fg = new FlexTypeGenerator();
			PreparedQueryStatement moaQueryStmt = LCSMOAObjectQuery.findMOACollectionDataQuery(material, attribute, sortKey, false);
			moaQueryStmt = fg.createSearchResultsQueryColumns(attCols, moaType, moaQueryStmt);
			moaQueryStmt.appendSortBy(new QueryColumn(LCSMOAObject.class, "sortingNumber"));
			SearchResults searchresults = LCSQuery.runDirectQuery(moaQueryStmt);
			Collection Coll = searchresults.getResults();
			return Coll;
	   }

	/** Creates material header section label table
	  * @param parser
	  * @param materialType
      * @throws WTException
      * @return PdfPCelll
      */
	public Collection getColumns(StringTokenizer attParser, FlexType type) throws WTException {
		    
			Collection columns = new Vector();
			TableColumn column = null;
			FlexTypeAttribute att = null;
			FlexTypeGenerator flexg = new FlexTypeGenerator();
			while(attParser.hasMoreTokens()){
				column = new TableColumn();
				String attName = attParser.nextToken();
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
	

   /**returns the PdfPCell containing header label for  MOA table for the specification
     * @param label
     * @return  PdfPCell
	 */ 
	private PdfPCell createLabelCell(String label){
	    
		//Font font = pgh.getCellFont("RPT_HEADER","Left", fontSize);
		Font font = pgh.getCellFont("RPT_HEADER","Left", null);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(label, font));
	    pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_HEADER", null));           
	    return pdfpcell;
    }

	/* *returns the PdfPCell containing data for  MOA table for the specification
     * @param label
     * @return 
	 */ 
	private  PdfPCell createDataCell(String data){

	   // Font font = pgh.getCellFont("RPT_TBD","Left", fontSize);
	    Font font = pgh.getCellFont("RPT_TBD","Left", null);
        PdfPCell pdfpcell = new PdfPCell(pgh.multiFontPara(data, font));
        //pdfpcell.setBackgroundColor(pgh.getCellBGColor("RPT_TBD", null));
	    return pdfpcell;
    }

	private PdfPCell createLabelCell(String label, int colNumber){
	    
		//Font font = pgh.getCellFont("RPT_HEADER","Left", fontSize);
		Font font = pgh.getCellFont("RPT_HEADER","Left", null);
		PdfPCell cell = new PdfPCell(pgh.multiFontPara(label, font));
		cell.setColspan(colNumber);
		cell.setPadding(4F);
		cell.setBorder(0);
		cell.setHorizontalAlignment (Element.ALIGN_LEFT);
	    //cell.setVerticalAlignment(Element.ALIGN_TOP);    
		    
	    return cell;
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