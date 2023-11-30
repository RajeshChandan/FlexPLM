package com.hbi.wc.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import wt.enterprise.RevisionControlled;
import wt.fc.PersistenceServerHelper;
import wt.fc.WTObject;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.color.LCSPaletteToColorLink;
import com.lcs.wc.construction.LCSConstructionDetail;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.BOMOwner;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTypeQueryStatement;
import com.lcs.wc.flextype.FlexTypeScopeDefinition;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.flextype.ForiegnKeyDefinition;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.measurements.LCSPointsOfMeasure;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.util.FormatHelper;
import org.apache.log4j.Logger;
import   wt.log4j.LogR;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.whereused.FAWhereUsedQuery;

/**
 * @author UST
 * 
 * 
 */
public class HBIColorWhereUsedUtility {

	private static final Logger logger = LogR.getLogger("com.hbi.wc.utility.HBIColorWhereUsedUtility");
	private static String floderPhysicalLocation = "";
	
	static{
		try{
			WTProperties wtprops = WTProperties.getLocalProperties();
	        String home = wtprops.getProperty("wt.home");
	        floderPhysicalLocation = home + File.separator + "logs" + File.separator + "migration"+ File.separator;
	        if(!(new File(floderPhysicalLocation).exists())){
	        	new File(floderPhysicalLocation).mkdir();
	        }
		}catch (Exception exp){
			logger.debug("Exception in static block of the class HBIColorWhereUsedUtility is : "+ exp);
		}
	}
	/**
	 * 
	 * @param args
	 * @throws WTException
	 */
	//Called from plugin entry
	public static void checkWhereUsedColor(WTObject obj) {
		NumberFormat formatter = new DecimalFormat("#0.00000");
		long startTime = System.currentTimeMillis();
		try{
		if(obj instanceof LCSLifecycleManaged){
			
			LCSLifecycleManaged bo=(LCSLifecycleManaged)obj;
		
			//hbiInitiateColor
			String  boName = (String) bo.getValue("name");
			//String  replaceColor = (String) bo.getValue("hbiObjectDescription");
			logger.debug("checkWhereUsedColor boName "+boName);
			//ColorUtilityReplaceColor
			if(boName.equalsIgnoreCase("ColorUtilityReplaceColor")){
				
				replaceColorClearHistory();
					
			}
			if(boName.equalsIgnoreCase("ColorUtilityClearHistory")){
				clearHistory();
			}
			
		  }
		long totalTime   = System.currentTimeMillis();
		 
		
		logger.debug("Final duration: "+formatter.format((totalTime - startTime) / 1000d) + " seconds");
		logger.debug("Color history completed");
		
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
	}


	private static void replaceColorClearHistory()
			throws FileNotFoundException, IOException, WTException, WTPropertyVetoException {
		//Get color names from excel file and iterate each line.
		FileInputStream file = new FileInputStream(new File(floderPhysicalLocation + "colorReplace.xlsx"));
		
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		// Get first/desired sheet from the workbook
		XSSFSheet sheet = workbook.getSheetAt(0);
		
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();
		
		System.out.println("Started Color Replace.........");
		DataFormatter cellformatter = new DataFormatter();
		
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if(row.getRowNum()!=0){
				String colorName1 = cellformatter.formatCellValue(row.getCell(0));
				String colorName2 = cellformatter.formatCellValue(row.getCell(1));
				String listOfPlacesRemoved = "";
				LCSColor color1 = getColor(colorName1);
				
				LCSColor color2 = getColor(colorName2);

				//bo.setValue("colorhistoryLog", "Color record not found");
				if(color1 != null  ){
					//To clear history
					getColorHistory(color1);
					logger.debug("color1 ------>"+ color1.getName());
					//logger.debug("color2 ------>"+ color2.getName());
					Collection col =doCheckForObjectReferencesLatestOnly(color1);
					logger.debug("doCheckForObjectReferencesLatestOnly size------>"+ col.size());
					logger.debug("doCheckForObjectReferencesLatestOnly ------>"+ col);
					Iterator itr = col.iterator();
					int count = 0;
					String colorNamesFromRemoved="";
					while (itr.hasNext()) {

						FlexObject fob = (FlexObject) itr.next();
						
						if(fob.containsKey("LCSMOAOBJECT.IDA2A2")){
							String moaIDa2a2 = fob.getString("LCSMOAOBJECT.IDA2A2");

							if (moaIDa2a2 != null) {

								LCSMOAObject moa = (LCSMOAObject) LCSQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+ moaIDa2a2);

								logger.debug("MOA Owner identity-------->"+ moa.getOwner().getDisplayIdentity());

								logger.debug("MOA Owner identity-------->"+ moa.getOwner().getIdentity());

								if (moa.getOwner() != null&& moa.getOwner() instanceof LCSColor) {

									LCSColor colorObj = (LCSColor) moa.getOwner();

									logger.debug("colorObj OWNER --------->"	+ colorObj.getName());

									colorNamesFromRemoved=colorNamesFromRemoved+" , "+colorObj.getName();
									moa.setDropped(true);

									LCSMOAObjectLogic.deleteObject(moa);
									++count;
								}
							}
							listOfPlacesRemoved = count+ " Places reference found MOA and cleaned.Please check log for full details.";
						}else if(fob.containsKey("LCSSKU.IDA2A2")){

							System.out.println("Inside sku block-------");
							String attVarName=FlexTypeCache.getFlexTypeFromPath("Product").getAttribute("color").getColumnDescriptorName();
							System.out.println("attVarName----------->"+attVarName);
							
							//Collection skuCol=findColorWhereUsedInHistory(color1);
							Collection<FlexObject> objRef = (new FAWhereUsedQuery()).checkForObjectReferences(color1);
							for(FlexObject sjuFOB: objRef){
								if(sjuFOB.containsKey("LCSSKU.IDA2A2")){
									//String name = sjuFOB.getString("LCSKU.ATT1");							      
							        String skuIda2a2 = sjuFOB.getString("LCSSKU.IDA2A2");							            
							        System.out.println("skuIda2a2--------->"+skuIda2a2);
							        LCSSKU skuObj=(LCSSKU)LCSQuery.findObjectById("OR:com.lcs.wc.product.LCSSKU:"+skuIda2a2);								            								           
							        LCSSKU skuLatestObj= (LCSSKU) VersionHelper.latestIterationOf(VersionHelper.getVersion(skuObj, "A"));								            
							        LCSColor colorLatest=(LCSColor)skuLatestObj.getValue("color");			
									skuObj.setValue("color", colorLatest);
									listOfPlacesRemoved=listOfPlacesRemoved+" ,"+skuObj.getName();
									PersistenceServerHelper.manager.update(skuObj);													
							     }
							 }								        						
						}else{
							listOfPlacesRemoved.concat(fob.toString()+" , ");	
						}
					}
					//same as fawhereused
					//Collection col2 = new WhereUsedQuery().checkForObjectReferences(color1);
					
					System.out.println("Inside sku block- listOfPlacesRemoved------"+listOfPlacesRemoved);
					//logger.debug("WhereUsedQuery Collection------>"+ col2);
					listOfPlacesRemoved = listOfPlacesRemoved.replaceFirst(",", "").trim();
					//ClearedHistory
					row.createCell(2).setCellValue("yes");
					
					if(color2 !=null){
						setColorway(color1, color2 );
						setMaterialColor(color1, color2);
						setPaletteColor(color1, color2);
						setBOMColor(color1, color2);
						row.createCell(3).setCellValue("yes");
					}else{
						row.createCell(3).setCellValue("Color2 Not Found");	
					}
				}else{
					row.createCell(2).setCellValue("Color1 Not Found");
					
				}
			}
		}
		
		
	      
		  FileOutputStream colorHistory = new FileOutputStream(new File(floderPhysicalLocation+"colorReplaceResult.xlsx"));
		  workbook.write(colorHistory);
		  colorHistory.close();
		  file.close();
	}

	
	private static void clearHistory()
			throws FileNotFoundException, IOException, WTException, WTPropertyVetoException {
		//Get color names from excel file and iterate each line.
		FileInputStream colorHistoryFile = new FileInputStream(new File(floderPhysicalLocation + "colorHistory.xlsx"));
		
		XSSFWorkbook colorHistoryWorkbook = new XSSFWorkbook(colorHistoryFile);
		// Get first/desired sheet from the workbook
		XSSFSheet colorHistorySheet = colorHistoryWorkbook.getSheetAt(0);
		
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = colorHistorySheet.iterator();
		
		System.out.println("Started Color Replace.........");
		DataFormatter cellformatter = new DataFormatter();
		
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();
			if(row.getRowNum()!=0){
				String colorName1 = cellformatter.formatCellValue(row.getCell(0));
				String listOfPlacesRemoved = "";
				LCSColor color1 = getColor(colorName1);

				if(color1 != null  ){

					logger.debug("color1 ------>"+ color1.getName());
					//logger.debug("color2 ------>"+ color2.getName());
					Collection col = doCheckForObjectReferencesLatestOnly(color1);
					logger.debug("doCheckForObjectReferencesLatestOnly size------>"+ col.size());
				
					getColorHistory(color1);
					logger.debug("doCheckForObjectReferencesLatestOnly ------>"+ col);
					Iterator itr = col.iterator();
					int count = 0;
					String colorNamesFromRemoved="";
					while (itr.hasNext()) {

						FlexObject fob = (FlexObject) itr.next();
						
						if(fob.containsKey("LCSMOAOBJECT.IDA2A2")){
							String moaIDa2a2 = fob.getString("LCSMOAOBJECT.IDA2A2");

							if (moaIDa2a2 != null) {

								LCSMOAObject moa = (LCSMOAObject) LCSQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+ moaIDa2a2);

								logger.debug("MOA Owner identity-------->"+ moa.getOwner().getDisplayIdentity());

								logger.debug("MOA Owner identity-------->"+ moa.getOwner(). getIdentity());

								if (moa.getOwner() != null&& moa.getOwner() instanceof LCSColor) {

									LCSColor colorObj = (LCSColor) moa.getOwner();

									logger.debug("colorObj OWNER --------->"	+ colorObj.getName());

									colorNamesFromRemoved=colorNamesFromRemoved+" , "+colorObj.getName();
									moa.setDropped(true);

									LCSMOAObjectLogic.deleteObject(moa);
									++count;
								}
							}
							listOfPlacesRemoved = count+ " Places reference found MOA and cleaned.Please check log for full details.";
						}else if(fob.containsKey("LCSSKU.IDA2A2")){

							System.out.println("Inside sku block-------");
							String attVarName=FlexTypeCache.getFlexTypeFromPath("Product").getAttribute("color").getColumnDescriptorName();
							System.out.println("attVarName----------->"+attVarName);
							
							//Collection skuCol=findColorWhereUsedInHistory(color1);
							Collection<FlexObject> objRef = (new FAWhereUsedQuery()).checkForObjectReferences(color1);
							logger.debug("FAWhereUsedQuery objRef size "+objRef.size());
							logger.debug("FAWhereUsedQuery objRef coll "+objRef);
							for(FlexObject sjuFOB: objRef){
								if(sjuFOB.containsKey("LCSSKU.IDA2A2")){
									//String name = sjuFOB.getString("LCSKU.ATT1");							      
							        String skuIda2a2 = sjuFOB.getString("LCSSKU.IDA2A2");							            
							        System.out.println("skuIda2a2--------->"+skuIda2a2);
							        LCSSKU skuObj=(LCSSKU)LCSQuery.findObjectById("OR:com.lcs.wc.product.LCSSKU:"+skuIda2a2);	
							        logger.debug("FAWhereUsedQuery skuObjName "+skuObj.getName());
							        LCSSKU skuLatestObj= (LCSSKU) VersionHelper.latestIterationOf(VersionHelper.getVersion(skuObj, "A"));								            
							        LCSColor colorLatest=(LCSColor)skuLatestObj.getValue("color");			
									skuObj.setValue("color", colorLatest);
									listOfPlacesRemoved=listOfPlacesRemoved+" ,"+skuObj.getName();
									PersistenceServerHelper.manager.update(skuObj);													
							     }
							 }								        						
						}else{
							listOfPlacesRemoved.concat(fob.toString()+" , ");	
						}
					}
					//same as fawhereused
					//Collection col2 = new WhereUsedQuery().checkForObjectReferences(color1);
					
					System.out.println("Inside sku block- listOfPlacesRemoved------"+listOfPlacesRemoved);
					//logger.debug("WhereUsedQuery Collection------>"+ col2);
					listOfPlacesRemoved = listOfPlacesRemoved.replaceFirst(",", "").trim();
					//ClearedHistory
					row.createCell(1).setCellValue("yes");
					
					
					getColorway(color1,  row);
					getPaletteColor(color1, row);
					getMaterialColor(color1, row);
					getBOMColor(color1, row);
					
					
				}else{
					row.createCell(1).setCellValue("Color1 Not Found");
					
				}
			}
		}
		
		  colorHistoryFile.close();  
		  FileOutputStream colorHistory = new FileOutputStream(new File(floderPhysicalLocation+"colorHistoryResult.xlsx"));
		  colorHistoryWorkbook.write(colorHistory);
		  colorHistory.close();
	}
	
	private static void getColorway(LCSColor color1, Row row) {
		
		try {
			StringBuffer skuNameSB = new StringBuffer();
			Collection<FlexObject> objRef = (new FAWhereUsedQuery()).checkForObjectReferences(color1);
			logger.debug("FAWhereUsedQuery ------>"+ objRef);
			for(FlexObject fobj : objRef){
				
				if(fobj.containsKey("LCSSKU.IDA2A2")){
					LCSSKU lcsSKU = (LCSSKU) LCSQuery.findObjectById("com.lcs.wc.product.LCSSKU:"+fobj.getData("LCSSKU.IDA2A2"));
					if(lcsSKU!=null){
						skuNameSB.append(",");
						skuNameSB.append(lcsSKU.getName());
					}
					
				}
			}
			if(skuNameSB.length()!=0){
			skuNameSB.substring(1);
			row.createCell(2).setCellValue(skuNameSB.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void getPaletteColor(LCSColor color1, Row row) {
		Collection<FlexObject> palletColorCol = new ArrayList();
		try{
			StringBuffer palletSB = new StringBuffer();
		/* To get LCSPaletteToColorLink color
		 * SELECT LCSMaterialColor.idA2A2 FROM LCSMaterialColor, LCSColor 
		WHERE LCSMaterialColor.idA3B10 = LCSColor.idA2A2 AND LCSMaterialColor.idA3B10 = ?     bindings={ 122153312 }
		*/
		PreparedQueryStatement paletteColorQuery = new PreparedQueryStatement();
		paletteColorQuery.appendSelectColumn(new QueryColumn("LCSPaletteToColorLink", "IDA2A2"));
		paletteColorQuery.appendFromTable(LCSPaletteToColorLink.class);		
		paletteColorQuery.appendCriteria(new Criteria(new QueryColumn("LCSPaletteToColorLink","IDA3A5"), FormatHelper.getNumericObjectIdFromObject(color1),Criteria.EQUALS));
		logger.debug("Inside paletteColorQuery  method :Query is ------>"+ paletteColorQuery);
		SearchResults results = LCSQuery.runDirectQuery(paletteColorQuery);
		palletColorCol = results.getResults();
		logger.debug("Inside palletColorCol ------>"+ palletColorCol);
		
		for(FlexObject flexObj: palletColorCol){
			
				String palletColorId = flexObj.getString("LCSPALETTETOCOLORLINK.IDA2A2");							      
				LCSPaletteToColorLink palletColorObj = (LCSPaletteToColorLink)LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSPaletteToColorLink:"+palletColorId);
				if(palletColorObj!=null && palletColorObj.getPalette()!=null){
					palletSB.append(",");
					palletSB.append(palletColorObj.getPalette().getName());
				}
		}
		if(palletSB.length()!=0){
			palletSB.substring(1);
			row.createCell(3).setCellValue(palletSB.toString());
		}
		
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
private static void getColorHistory(LCSColor color1) {
		
		Collection<FlexObject> skuColl = new ArrayList();
		try {
			
			FlexType ft = FlexTypeCache.getFlexTypeFromPath("Product");
			FlexTypeAttribute fta = ft.getAttribute("color");
			String db = "num"+fta.getAttColumn();
			
			
		PreparedQueryStatement skuQuery = new PreparedQueryStatement();
		skuQuery.appendSelectColumn(new QueryColumn("LCSSKU", "IDA2A2"));
		skuQuery.appendFromTable(LCSSKU.class);		
		skuQuery.appendCriteria(new Criteria(new QueryColumn("LCSSKU",db), FormatHelper.getNumericObjectIdFromObject(color1),Criteria.EQUALS));
		logger.debug("skuQuery :Query is ------>"+ skuQuery);
		SearchResults results = LCSQuery.runDirectQuery(skuQuery);
		skuColl = results.getResults();
		logger.debug("Inside skuColl ------>"+ skuColl);
		
		for(FlexObject flexObj: skuColl){
			
				String skuID = flexObj.getString("LCSSKU.IDA2A2");							      
				LCSSKU lcsSKU = (LCSSKU)LCSQuery.findObjectById("OR:com.lcs.wc.product.LCSSKU:"+skuID);	
				logger.debug("lcsSKU getVersionInfo ------>"+ lcsSKU.getVersionDisplayIdentity());
				
				LCSSKU skuLatestObj= (LCSSKU) VersionHelper.latestIterationOf(VersionHelper.getVersion(lcsSKU, "A"));								            
			    LCSColor colorLatest=(LCSColor)skuLatestObj.getValue("color");			
			    lcsSKU.setValue("color", colorLatest);
			   // LCSLogic.persist(lcsSKU, true);
			    PersistenceServerHelper.manager.update(lcsSKU);		
		}
		
		
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
	private static void getMaterialColor(LCSColor color1, Row row) {
		
		Collection<FlexObject> materialColor = new ArrayList();
		try {
			StringBuffer matColorSB = new StringBuffer();
		/* To get Material color
		 * SELECT LCSMaterialColor.idA2A2 FROM LCSMaterialColor, LCSColor 
		WHERE LCSMaterialColor.idA3B10 = LCSColor.idA2A2 AND LCSMaterialColor.idA3B10 = ?     bindings={ 122153312 }
		*/
		PreparedQueryStatement materialColorQuery = new PreparedQueryStatement();
		materialColorQuery.appendSelectColumn(new QueryColumn("LCSMaterialColor", "IDA2A2"));
		materialColorQuery.appendFromTable(LCSMaterialColor.class);		
		materialColorQuery.appendCriteria(new Criteria(new QueryColumn("LCSMaterialColor","idA3B10"), FormatHelper.getNumericObjectIdFromObject(color1),Criteria.EQUALS));
		logger.debug("Inside findMaterialColor  method :Query is ------>"+ materialColorQuery);
		SearchResults results = LCSQuery.runDirectQuery(materialColorQuery);
		materialColor = results.getResults();
		logger.debug("Inside materialColor ------>"+ materialColor);
		
		for(FlexObject flexObj: materialColor){
			
				String materialColorId = flexObj.getString("LCSMATERIALCOLOR.IDA2A2");							      
		        LCSMaterialColor matColor = (LCSMaterialColor)LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialColor:"+materialColorId);	
		        if(matColor!=null){
		        	matColorSB.append(",");
		        	matColorSB.append(matColor.getName());
		        }
		}
		if(matColorSB.length()!=0){
			matColorSB.substring(1);
			row.createCell(4).setCellValue(matColorSB.toString());
		}
		
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	private static void getBOMColor(LCSColor color1, Row row) {
		Collection<FlexObject> bomLinkCol = new ArrayList();
		try{
			StringBuffer bomLinkSB = new StringBuffer();
			PreparedQueryStatement bomLinkQuery = new PreparedQueryStatement();
			bomLinkQuery.appendSelectColumn(new QueryColumn("FlexBOMLink", "IDA2A2"));
			bomLinkQuery.appendSelectColumn(new QueryColumn("FlexBOMLink", "IDA3A5"));
			bomLinkQuery.appendFromTable(FlexBOMLink.class);			
			bomLinkQuery.appendCriteria(new Criteria(new QueryColumn("FlexBOMLink","IDA3D5"), FormatHelper.getNumericObjectIdFromObject(color1),Criteria.EQUALS));
			logger.debug("Inside bomLinkQuery  method :Query is ------>"+ bomLinkQuery);
			SearchResults results = LCSQuery.runDirectQuery(bomLinkQuery);
			bomLinkCol = results.getResults();
			logger.debug("Inside getBOMColor ------>"+ bomLinkCol);
			
			for(FlexObject flexObj: bomLinkCol){
				
				String bomLinkId = flexObj.getString("FLEXBOMLINK.IDA2A2");							      
				FlexBOMLink flexbomLink = (FlexBOMLink)LCSQuery.findObjectById("com.lcs.wc.flexbom.FlexBOMLink:"+bomLinkId);
				logger.debug("Inside flexbomLinkgetDisplayIdentity ------>"+ flexbomLink.getDisplayIdentity());
				logger.debug("Inside getIdentity ------>"+ flexbomLink.getIdentity());
				logger.debug("Inside getMasterBranchId ------>"+ flexbomLink.getMasterBranchId());
				logger.debug("Inside getWcPart ------>"+ flexbomLink.getWcPart());
				String bomMasterId = flexObj.getString("FLEXBOMLINK.IDA3A5");		
				WTPartMaster bomPartMaster = (WTPartMaster)LCSQuery.findObjectById("wt.part.WTPartMaster:"+bomMasterId);
				FlexBOMPart flexBOMPart = (FlexBOMPart) VersionHelper.latestIterationOf(bomPartMaster);
				LCSPartMaster prodMaster = (LCSPartMaster)flexBOMPart.getOwnerMaster();
				if(prodMaster!=null && bomPartMaster.getName()!=null){
					bomLinkSB.append(",");
					bomLinkSB.append(prodMaster.getName()+":"+bomPartMaster.getName());
				}
				
			}
			if(bomLinkSB.length()!=0){
				bomLinkSB.substring(1);
				row.createCell(5).setCellValue(bomLinkSB.toString());
			}
			
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	private static void setBOMColor(LCSColor color1, LCSColor color2) {
	
	Collection<FlexObject> bomLinkCol = new ArrayList();
	try{
		/* To get LCSPaletteToColorLink color
		 * SELECT LCSMaterialColor.idA2A2 FROM LCSMaterialColor, LCSColor 
		WHERE LCSMaterialColor.idA3B10 = LCSColor.idA2A2 AND LCSMaterialColor.idA3B10 = ?     bindings={ 122153312 }
		*/
		PreparedQueryStatement bomLinkQuery = new PreparedQueryStatement();
		bomLinkQuery.appendSelectColumn(new QueryColumn("FlexBOMLink", "IDA2A2"));

		bomLinkQuery.appendFromTable(FlexBOMLink.class);

		
		bomLinkQuery.appendCriteria(new Criteria(new QueryColumn("FlexBOMLink","IDA3D5"), FormatHelper.getNumericObjectIdFromObject(color1),Criteria.EQUALS));
		

		
		logger.debug("Inside bomLinkQuery  method :Query is ------>"+ bomLinkQuery);
		SearchResults results = LCSQuery.runDirectQuery(bomLinkQuery);
		bomLinkCol = results.getResults();
		logger.debug("Inside palletColorCol ------>"+ bomLinkCol);
		
		for(FlexObject flexObj: bomLinkCol){
			
			String bomLinkId = flexObj.getString("FLEXBOMLINK.IDA2A2");							      
			FlexBOMLink flexbomLink = (FlexBOMLink)LCSQuery.findObjectById("com.lcs.wc.flexbom.FlexBOMLink:"+bomLinkId);
	        logger.debug("flexbomLink getColor1 ------>"+ flexbomLink.getColor());
	        flexbomLink.setColor(color2);
	        logger.debug("flexbomLink getColor2 ------>"+ flexbomLink.getColor());
	        //LCSLogic.deriveFlexTypeValues(palletObj);
	        LCSLogic.persist(flexbomLink, true);
		}
	}catch (WTPropertyVetoException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (WTException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}


private static void setPaletteColor(LCSColor color1, LCSColor color2) {
	Collection<FlexObject> palletColorCol = new ArrayList();
	try{
	/* To get LCSPaletteToColorLink color
	 * SELECT LCSMaterialColor.idA2A2 FROM LCSMaterialColor, LCSColor 
	WHERE LCSMaterialColor.idA3B10 = LCSColor.idA2A2 AND LCSMaterialColor.idA3B10 = ?     bindings={ 122153312 }
	*/
	PreparedQueryStatement paletteColorQuery = new PreparedQueryStatement();
	paletteColorQuery.appendSelectColumn(new QueryColumn("LCSPaletteToColorLink", "IDA2A2"));

	paletteColorQuery.appendFromTable(LCSPaletteToColorLink.class);

	
	paletteColorQuery.appendCriteria(new Criteria(new QueryColumn("LCSPaletteToColorLink","IDA3A5"), FormatHelper.getNumericObjectIdFromObject(color1),Criteria.EQUALS));
	

	
	logger.debug("Inside paletteColorQuery  method :Query is ------>"+ paletteColorQuery);
	SearchResults results = LCSQuery.runDirectQuery(paletteColorQuery);
	palletColorCol = results.getResults();
	logger.debug("Inside palletColorCol ------>"+ palletColorCol);
	
	for(FlexObject flexObj: palletColorCol){
		
			String palletColorId = flexObj.getString("LCSPALETTETOCOLORLINK.IDA2A2");							      
			LCSPaletteToColorLink palletObj = (LCSPaletteToColorLink)LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSPaletteToColorLink:"+palletColorId);
	        logger.debug("paletteColorQuery getColor1 ------>"+ palletObj.getColor());
	        palletObj.setColor(color2);
	        logger.debug("paletteColorQuery getColor2 ------>"+ palletObj.getColor());
	        //LCSLogic.deriveFlexTypeValues(palletObj);
	        LCSLogic.persist(palletObj, true);
		
	}
	
	} catch (WTException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (WTPropertyVetoException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}


private static void setColorway(LCSColor color1, LCSColor color2) {
	try {
		
		logger.debug("FAWhereUsedQuery ------>"+ (new FAWhereUsedQuery()).checkForObjectReferences(color1));
		Collection<FlexObject> objRef = (new FAWhereUsedQuery()).checkForObjectReferences(color1);
		for(FlexObject fobj : objRef){
			
			if(fobj.containsKey("LCSSKU.IDA2A2")){
				String attKey = fobj.getString("ATTKEY");
				logger.debug("ATTKEY ------>"+ attKey);
				LCSSKU lcsSKU = (LCSSKU) LCSQuery.findObjectById("com.lcs.wc.product.LCSSKU:"+fobj.getData("LCSSKU.IDA2A2"));
				
			
				logger.debug("lcsSKU 1st value ------>"+ lcsSKU.getValue(attKey));
				
				logger.debug("lcsSKU color2 ------>"+ color2);
				
					lcsSKU.setValue(attKey, FormatHelper.getNumericObjectIdFromObject(color2));
					LCSLogic.deriveFlexTypeValues(lcsSKU);
					LCSLogic.persist(lcsSKU,true);
				
				
				
				logger.debug("lcsSKU2 value ------>"+ lcsSKU.getValue(attKey));
				
				
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
}

public static Collection<FlexObject> setMaterialColor(LCSColor color1, LCSColor color2){
	Collection<FlexObject> materialColor = new ArrayList();
	try {
	/* To get Material color
	 * SELECT LCSMaterialColor.idA2A2 FROM LCSMaterialColor, LCSColor 
	WHERE LCSMaterialColor.idA3B10 = LCSColor.idA2A2 AND LCSMaterialColor.idA3B10 = ?     bindings={ 122153312 }
	*/
	PreparedQueryStatement materialColorQuery = new PreparedQueryStatement();
	materialColorQuery.appendSelectColumn(new QueryColumn("LCSMaterialColor", "IDA2A2"));

	materialColorQuery.appendFromTable(LCSMaterialColor.class);

	
	materialColorQuery.appendCriteria(new Criteria(new QueryColumn("LCSMaterialColor","idA3B10"), FormatHelper.getNumericObjectIdFromObject(color1),Criteria.EQUALS));
	

	
	logger.debug("Inside findMaterialColor  method :Query is ------>"+ materialColorQuery);
	SearchResults results = LCSQuery.runDirectQuery(materialColorQuery);
	materialColor = results.getResults();
	logger.debug("Inside materialColor ------>"+ materialColor);
	
	for(FlexObject flexObj: materialColor){
		
			String materialColorId = flexObj.getString("LCSMATERIALCOLOR.IDA2A2");							      
	        LCSMaterialColor matColor = (LCSMaterialColor)LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialColor:"+materialColorId);
	        logger.debug("matColor getColor1 ------>"+ matColor.getColor());
	        matColor.setColor(color2);
	        logger.debug("matColor getColor2 ------>"+ matColor.getColor());
	        LCSLogic.deriveFlexTypeValues(matColor);
	        LCSLogic.persist(matColor, true);
		
	}
	
	} catch (WTException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (WTPropertyVetoException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return materialColor;
	
	
}
	/**
	 * @param obj
	 * 
	 * @throws NamingException
	 * @throws IOException
	 * @throws WTException
	 */
	public static LCSColor getColor(String name) {
		LCSColor colorObj = null;
		try {
			FlexType colorType= FlexTypeCache.getFlexTypeFromPath("Color");
			
			PreparedQueryStatement colorQuery = new PreparedQueryStatement();
			colorQuery.appendFromTable(LCSColor.class);
			colorQuery.appendSelectColumn(new QueryColumn("LCSColor", "IDA2A2"));
	
			colorQuery.appendCriteria(new Criteria(new QueryColumn("LCSColor",colorType.getAttribute("name").getColumnDescriptorName()), name,Criteria.EQUALS));
	
			logger.debug("Inside getColor :Query------>"+ colorQuery);
			SearchResults results = LCSQuery.runDirectQuery(colorQuery);
			Iterator<FlexObject> colorItr = results.getResults().iterator();
			logger.debug("Inside getColor :results size------>"+ results.getResults().size());
			
			while (colorItr.hasNext()) {
				colorObj = (LCSColor) LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSColor:"+ colorItr.next().getString("LCSColor.IDA2A2"));
			}
		} catch (WTException e) {
			e.printStackTrace();
		}
		return colorObj;

	}

	public static Collection doCheckForObjectReferencesLatestOnly(WTObject obj)throws WTException{
		
		Collection blank=new ArrayList();

		if(!(obj instanceof FlexTyped))
		{
			return blank;
		}
		
		Collection candidateAttribtues = FlexTypeCache.findCandidateObjectRefAttributes((FlexTyped)obj);
		Iterator attIter = candidateAttribtues.iterator();
		ForiegnKeyDefinition fkDef = null;
		boolean inUse = false;
		while(attIter.hasNext()) 
		{
			FlexTypeAttribute att = (FlexTypeAttribute)attIter.next();
			fkDef = att.getRefDefinition();
			FlexTypeScopeDefinition typeDef = att.getFlexTypeViaCache().getFlexTypeScopeDefinition();
			Collection levelsUsed = getLevels(typeDef, att);
			String scope = att.getAttScope();
			String level = "";
			Iterator levelIter = levelsUsed.iterator();
			while(levelIter.hasNext()) 
			{
				level = (String)levelIter.next();
				FlexTypeQueryStatement statement = new FlexTypeQueryStatement();
				FlexType type = att.getFlexTypeViaCache();
				statement.setType(type, scope, level, true, false);
				try
				{
					FlexTypeScopeDefinition def = att.getFlexTypeViaCache().getFlexTypeScopeDefinition();
					Class srcClass = def.getClass(scope, level, type);
					FlexTyped typed = (FlexTyped)srcClass.newInstance();
					if(typed instanceof RevisionControlled)
					{
						String table = def.getTableName(scope, level, type);
						statement.appendAndIfNeeded();
						statement.appendCriteria(new Criteria(table, "latestiterationInfo", "1", "="));
					} else
						if(typed instanceof LCSSeasonProductLink)
						{
				
							statement.appendAndIfNeeded();
							statement.appendCriteria(new Criteria(new QueryColumn(LCSSeasonProductLink.class, "effectLatest"), "1", "="));
						} else
							if(typed instanceof FlexBOMLink)
							{
								statement.appendAndIfNeeded();
								statement.appendCriteria(new Criteria(new QueryColumn(FlexBOMLink.class, "outDate"), "", "IS NULL"));
							} else
								if(typed instanceof LCSConstructionDetail)
								{
									statement.appendAndIfNeeded();
									statement.appendCriteria(new Criteria(new QueryColumn(LCSConstructionDetail.class, "effectOutDate"), "", "IS NULL"));
								} else
									if(typed instanceof LCSPointsOfMeasure)
									{
										statement.appendAndIfNeeded();
										statement.appendCriteria(new Criteria(new QueryColumn(LCSPointsOfMeasure.class, "effectOutDate"), "", "IS NULL"));
									} else
										if(typed instanceof LCSMOAObject)
										{
											statement.appendAndIfNeeded();
											statement.appendCriteria(new Criteria(new QueryColumn(LCSMOAObject.class, "effectOutDate"), "", "IS NULL"));
										}
				}
				catch(Exception e)
				{
					throw new WTException(e);
				}
				statement.appendAndIfNeeded();
				statement.appendSelectColumn(statement.getPrimaryTable(), "idA2A2");
				if(fkDef.getRefType().equals("version"))
				{
					statement.appendFlexSelectColumn(att.getAttKey());
					statement.appendFlexCriteria(att.getAttKey(), FormatHelper.getNumericVersionIdFromObject((RevisionControlled)obj), "=");
				} else
				{
					statement.appendFlexSelectColumn(att.getAttKey());
					statement.appendFlexCriteria(att.getAttKey(), FormatHelper.getNumericObjectIdFromObject(obj), "=");
				}
				SearchResults results = LCSQuery.runDirectQuery(statement);
				logger.debug("doCheckForObjectReferencesLatestOnly results size: "+results.getResults().size());
				if(results.getResults().size() > 0)
				{
					return results.getResults();
				}
			}
		}
		return blank;
	}


	private static Collection getLevels(FlexTypeScopeDefinition typeDef, FlexTypeAttribute att)
	{
		Collection levelsUsed = new Vector();
		if(typeDef != null)
		{
			levelsUsed = typeDef.getDistinctAttributeLevelsUsed(att);
		} else
		{
			levelsUsed.add(null);
		}
		return levelsUsed;
	}
}
