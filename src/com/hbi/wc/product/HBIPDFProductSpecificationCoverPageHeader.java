
package com.hbi.wc.product;

import com.lcs.wc.util.*;
import com.lcs.wc.specification.*;
import com.lcs.wc.flextype.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.client.web.*;
import com.lcs.wc.client.web.pdf.*;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.io.*;
import wt.util.*;
import wt.vc.Mastered;
import com.lcs.wc.sourcing.*;
import com.lcs.wc.season.*;
import com.lcs.wc.product.*;
import com.lcs.wc.foundation.*;
//import wt.part.WTPartMaster;
import wt.fc.WTObject;
import com.lcs.wc.part.LCSPart;
import com.lcs.wc.part.LCSPartMaster;

/*******************************************************************************************************************************
 * HBIPDFProductSpecificationCoverPageHeader.java
 *
 * This file used to generates the Cover pahe header for a ProductSpecification Report
 *
 */
public class HBIPDFProductSpecificationCoverPageHeader extends PdfPTable implements PDFHeader{

	static float fixedHeight = 140.0F;
	public static String PRODUCT_ID = "PRODUCT_ID";
	public static String SPEC_ID = "SPEC_ID";
	public static String SEASONMASTER_ID = "SEASONMASTER_ID";
	//Added by UST tfor tech pack correction - 07/20/2012
	public static String HBI_SUPPORTING = "HBI-SUPPORTING";
	public static final String BASIC_CUT_AND_SEW_COLORWAY = "BASIC CUT & SEW - COLORWAY";
	public static final String BASIC_CUT_AND_SEW_PATTERN = "BASIC CUT & SEW - PATTERN";
	public static final String BASIC_CUT_AND_SEW_GARMENT = "BASIC CUT & SEW - GARMENT";
	public static final String BASIC_CUT_AND_SEW_GARMENT_P = "Product\\BASIC CUT & SEW - GARMENT"; 
	public static final String HBI_SUPPORTING_IMAGE = "HBI-SUPPORTING\\IMAGE";
	//Added by UST tfor tech pack correction - 09/05/2012
	public static String LABEL = "LABEL";		
	static String IMAGE_URL = LCSProperties.get("com.lcs.wc.content.imageURL");
	public static final String webHomeLocation = LCSProperties.get("flexPLM.webHome.location");
	// CC Task # 5 -- Fix for image pages producing a second ?blank? page --
	// Added by UST 8th Apr 2019
	public static final float THUMBNAIL_IMAGE_HEIGHT = (new Float(
			LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationHeader.thumbNailHeight", "60")));
	public static final float THUMBNAIL_IMAGE_WIDTH = (new Float(
			LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationHeader.thumbNailWidth", "60")));	
	static PDFGeneratorHelper pgh = new PDFGeneratorHelper();
	static String imageFile = "";
	static String wthome = "";
	static{
		try{
			imageFile = LCSProperties.get("com.lcs.wc.product.PDFProductSpecificationHeader.headerImage");
			wthome = WTProperties.getServerProperties().getProperty("wt.home");
			imageFile = wthome + File.separator + imageFile;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/** Creates a new instance of PDFProductSpecificationHeader
	 */
	public HBIPDFProductSpecificationCoverPageHeader() {
	}

	public HBIPDFProductSpecificationCoverPageHeader(int cols) {
		super(cols);
	}

	/** returns another instance of PDFProductSpecificationHeader with the table filled,
	 * which can be added to a Document
	 * @param params
	 * @throws WTException
	 * @return
	 */    
	public Element getPDFHeader(Map params) throws WTException {

		try {
			PDFProductSpecificationHeader ppsh = new PDFProductSpecificationHeader(1);
			ppsh.setWidthPercentage(95.0f);
			float[] widths = { 100.0f };
			ppsh.setWidths(widths);
			WTObject obj = (WTObject) LCSProductQuery.findObjectById((String) params.get(PRODUCT_ID));
			if (!(obj instanceof LCSProduct)) {
				throw new WTException("Can not use PDFProductSpecification on a non-LCSProduct - " + obj);
			}
			LCSProduct prod = (LCSProduct) obj;
			PdfPCell tableData =  null;
			if (prod.getFlexType().getFullName(true).equals(BASIC_CUT_AND_SEW_GARMENT_P)) {
				tableData = createGPDataCell(params);
			} else {
				tableData = createDataCell(params);
			}
			ppsh.addCell(tableData);
			return ppsh;
		} catch (Exception e) {
			throw new WTException(e);
		}

	}

	/** gets the Height for this header
	 * @return
	 */    
	public float getHeight(){

		return fixedHeight;

	}


	/** Creates product thumnail
	 * @param params
	 * @throws WTException
	 * @return PdfPCell
	 */
	/*
	    private PdfPCell createImageCellProductThumbnail(Map params) throws WTException{
          try{
			LCSProduct product = (LCSProduct)LCSProductQuery.findObjectById((String)params.get(PRODUCT_ID));
			String fileSeperator = FileLocation.fileSeperator;
			String productThumbnail = "";
			String imageNotAvailable = FileLocation.imageNotAvailable;
	     try{

			productThumbnail = product.getPartPrimaryImageURL();		
	 		if(FormatHelper.hasContent(productThumbnail)) {
                // Trim off the leading /LCSWImages/
                productThumbnail = FileLocation.imageLocation + FileLocation.fileSeperator + productThumbnail.substring((IMAGE_URL.length() + 1));
            }else{
               productThumbnail = imageNotAvailable;
            }
	     }
	     catch(Exception e){
		    e.printStackTrace();
	     }
            Image img = Image.getInstance(productThumbnail);
            PdfPCell cell = new PdfPCell(img, true);
            cell.setBorderWidth(0.0f);
            cell.setFixedHeight(42.0f);
            return cell;
        }
        catch(Exception e){
            throw new WTException(e);
         }
        }
	 */

	//Added this method on 10.1 to handle Images to fit inside the header cell.
	private PdfPCell createImageCellProductThumbnail(Map params) throws WTException{

		try{
			LCSProduct product = (LCSProduct)LCSProductQuery.findObjectById((String)params.get(PRODUCT_ID));
			String fileSeperator = FileLocation.fileSeperator;
			String productThumbnail = "";
			String imageNotAvailable = FileLocation.imageNotAvailable;
			PdfPCell cell=null;
			try{

				productThumbnail = product.getPartPrimaryImageURL();		
				if(FormatHelper.hasContent(productThumbnail)) {
					// Trim off the leading /LCSWImages/
					productThumbnail = FileLocation.imageLocation + FileLocation.fileSeperator + productThumbnail.substring((IMAGE_URL.length() + 1));

					if (new File(productThumbnail).exists()) {

						Image image = Image.getInstance(productThumbnail);
						// CC Task # 5 -- Fix for image pages producing a second
						// ?blank? page -- Added by UST 8th Apr 2019
						image.scaleToFit(THUMBNAIL_IMAGE_HEIGHT, THUMBNAIL_IMAGE_WIDTH);
						cell = new PdfPCell(image);
					} else {
						imageNotAvailable = FileLocation.imageNotAvailable;
						Image img = Image.getInstance(imageNotAvailable);
						img.scaleToFit(60,93);
						cell = new PdfPCell(img);
					}

				}		
				else {

					imageNotAvailable = FileLocation.imageNotAvailable;
					Image img = Image.getInstance(imageNotAvailable);
					img.scaleToFit(60,93);
					cell = new PdfPCell(img);
				}
			}
			catch(Exception e){

				cell = new PdfPCell(pgh.multiFontPara("",
						pgh.getCellFont("DISPLAYTEXT", null, null)));
			}
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			cell.setPadding(2);
			return cell;       


		}
		catch(Exception e){
			throw new WTException(e);
		}
	}


	/** Creates HBI logo
	 * @ throws WTException
	 * @ returns PdfPCell
	 */
	private PdfPCell createHbiLogoCell() throws WTException{
		try{
			Image img = Image.getInstance(imageFile);
			PdfPCell cell = new PdfPCell(img, true);
			cell.setUseBorderPadding(true);
			cell.setPadding(2.0F);
			cell.setBorderWidthRight(0.0f);
			cell.setFixedHeight(15.0F);
			return cell;
		}
		catch(Exception e){
			throw new WTException(e);
		}
	}

	/** Creates product header section table
	 * @param map
	 * @throws WTException
	 * @return PdfPCelll
	 * @author AshokGeorge
	 */
	private PdfPCell createGPDataCell(Map params) throws WTException{

		try{
			if(!FormatHelper.hasContent((String)params.get(PRODUCT_ID))){
				throw new WTException("Can not create PDFProductSpecificationHeader without PRODUCT_ID");
			}
			LCSProduct product = (LCSProduct)LCSProductQuery.findObjectById((String)params.get(PRODUCT_ID));
			LCSProduct patProduct = findPatternProdLinkedToGP(product);
			//HBI added for Pattern Version By John Reeno- Start - 2/10/2019
			String patternVersion = "";
			if(FormatHelper.hasContent((String)params.get(SPEC_ID))){
			WTObject obj = (WTObject) LCSQuery.findObjectById((String) params.get(SPEC_ID));
			if(obj != null){
				FlexSpecification spec = (FlexSpecification)obj;
				Collection<FlexObject> specLinks = FlexSpecQuery.findSpecToSpecLinks(product,null,null,spec,true,true);
				if(specLinks.size()>0){
					for(FlexObject flexObj:specLinks){
						String linkedSpecID = flexObj.getString("LINKEDSPECID");
						linkedSpecID.trim();
						if(FormatHelper.hasContent(linkedSpecID)){
							FlexSpecification linkedSpec = (FlexSpecification)LCSQuery.findObjectById("VR:com.lcs.wc.specification.FlexSpecification:"+linkedSpecID);
							linkedSpec = (FlexSpecification)VersionHelper.latestIterationOf(linkedSpec);
							if(linkedSpec != null){
								LCSSourcingConfig srcConfig = (LCSSourcingConfig)VersionHelper.latestIterationOf((LCSSourcingConfigMaster)linkedSpec.getSpecSource());
								if(srcConfig!=null){
									patternVersion = getAttValue(srcConfig, "hbiSrcPatternVer");
								}
							}
						}
					}
				}
			}
		}
			//HBI added for Pattern Version, By John Reeno - End - 2/10/2019
			String pPattrenNumber = "";
			if(patProduct != null){
				pPattrenNumber = getAttValue(patProduct, "hbiPatternNo");
			}

			//HbiLogoCell
			PdfPTable mainTable = new PdfPTable(1);
			float [] colWidths = {95.0F, 10.0F};
			PdfPTable headerTable = new PdfPTable(colWidths);
			float [] rowWidths = {8.0F,30.0F,8.0F,14.0F,12.0F,18.0F};
			if(product.getFlexType().getFullName().indexOf(LABEL) > -1){
				rowWidths = new float[] {20.0F,35.0F,12.0F,25.0F};
			}			
			PdfPTable table = new PdfPTable(rowWidths);
			PdfPCell cell = new PdfPCell();
			cell = createHbiLogoCell();
			cell.setColspan(1);
			cell.setBorder(0);
			table.addCell(cell);
			cell = new PdfPCell();
			cell.setColspan(5);
			cell.setBorder(0);
			table.addCell(cell);

			// Label
			tableText(table, getAttributeLabel(product, "productName"), getAttValue(product, "productName"));
			tableText(table, "Pattern # and Version: ", pPattrenNumber+"-"+patternVersion);
			//HBI added for Pattern Version,By John Reeno - Start - 2/10/2019
			//tableText(table, "Pattern Version: ", patternVersion);
			//HBI added for Pattern Version,By John Reeno - End - 2/10/2019
			tableText(table, "Date Issued: ", new SimpleDateFormat("MM/dd/yyyy").format(new Date()));

			// ImageCellProductThumbnail
			cell = new PdfPCell(table);
			cell.setBorder(0);
			headerTable.addCell(cell);

			cell = createImageCellProductThumbnail(params);
			cell.setBorder(0);
			cell.setColspan(5);
			headerTable.addCell(cell);

			cell = new PdfPCell(headerTable);
			cell.setBorder(0);
			mainTable.addCell(cell);

			PdfPCell tableCell = new PdfPCell(mainTable);
			tableCell.setPadding(3.0F);
			tableCell.setBackgroundColor(pgh.getCellBGColor("BORDERED_BLOCK", null));
			tableCell.setBorderColor(pgh.getColor("HEX669999"));
			tableCell.setBorderColorLeft(pgh.getCellBGColor("BORDERED_BLOCK", null));
			tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			tableCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

			return tableCell;
		}catch(Exception e){
			throw new WTException(e);
		}
	}

	private void tableText(PdfPTable table, String s1, String s2) {
		PdfPCell cell;
		cell = new PdfPCell();
		cell = createCell(s1, "FORMLABEL");
		cell.setColspan(1); 
		table.addCell(cell);
		cell = new PdfPCell();
		cell = createCell(s2, "DISPLAYTEXT");
		cell.setColspan(5); 
		table.addCell(cell);
	}

	public static LCSProduct findPatternProdLinkedToGP(LCSProduct garmentProduct) throws WTException {
		LCSProductQuery prodquery = new LCSProductQuery();
		LCSProduct linkedPatternProduct = null;
		String linktype = "Pattern-Garment";

		Vector<FlexObject> linkedProducts = (Vector<FlexObject>) prodquery.getLinkedProducts(
				FormatHelper.getObjectId((LCSPartMaster) garmentProduct.getMaster()), false, true, linktype);
		// Check if only one Pattern product linked
		if (linkedProducts != null && linkedProducts.size() == 1 && linkedProducts.get(0) != null) {
			FlexObject linkproduct = linkedProducts.get(0);
			linkedPatternProduct = findProduct(linkproduct.getString("PARENTPRODUCT.IDA3MASTERREFERENCE"));
			return linkedPatternProduct;
		}
		return linkedPatternProduct;

	}

	private static LCSProduct findProduct(String productIda3MasterRef) throws WTException {
		LCSProduct product = null;
		PreparedQueryStatement stmt = new PreparedQueryStatement();
		stmt.appendFromTable("LCSProduct", "product");
		stmt.appendSelectColumn("product", "ida2a2");
		stmt.appendOpenParen();
		stmt.appendCriteria(new Criteria("product", "ida3Masterreference", productIda3MasterRef, Criteria.EQUALS));
		stmt.appendAnd();
		stmt.appendCriteria(new Criteria("product", "latestIterationInfo", "1", Criteria.EQUALS));
		stmt.appendAnd();
		stmt.appendCriteria(new Criteria("product", "versionida2versioninfo", "A", Criteria.EQUALS));
		stmt.appendClosedParen();

		Vector output = LCSQuery.runDirectQuery(stmt).getResults();
		if (output.size() == 1) {
			FlexObject obj = (FlexObject) output.get(0);
			product = (LCSProduct) LCSQuery
					.findObjectById("OR:com.lcs.wc.product.LCSProduct:" + obj.getData("PRODUCT.IDA2A2"));
			return product;
		}
		return product;

	}


	private PdfPCell createDataCell(Map params) throws WTException{

		String technicalDesigner = "";
		String productManger = "";
		String pPattrenNumber = "";
		LCSLifecycleManaged labelCallOut = null;
		String labelCallOutName ="";		  
		String hbiBrandCatLbl = "";
		String brandCatValue = "";
		String hbiBrandCatStr = "";

		try{
			if(!FormatHelper.hasContent((String)params.get(PRODUCT_ID))){
				throw new WTException("Can not create PDFProductSpecificationHeader without PRODUCT_ID");
			}

			//product
			LCSProduct product = (LCSProduct)LCSProductQuery.findObjectById((String)params.get(PRODUCT_ID));
			// pack size commented as part of change request
			//packSize = FormatHelper.format((Double)product.getValue("hbiPackSize")); 

			String pCreateDateLbl = "Created Date:";
			Date pCreateDate = (Date)product.getCreateTimestamp();
			String pCreateDateVal = "";
			if(pCreateDate != null){
				pCreateDateVal =  FormatHelper.applyFormat(pCreateDate, "MM/dd/yyyy");
			}

			String pUpdateDateLbl = "Updated Date:";
			Date pUpdateDate = (Date)product.getModifyTimestamp();
			String pUpdateDateVal = "";
			if(pUpdateDate != null){
				pUpdateDateVal =  FormatHelper.applyFormat(pUpdateDate, "MM/dd/yyyy");
			}
			// Changed for HBI by Karthik from UST Start on 24/01/13

			String hbiDivLbl = "";
			String hbiDivStr = "";
			String hbiDivValue = "";
			Collection hbiDivKeyValue =null;
			Collection hbiDivCatKey =null;    	
			hbiDivLbl = product.getFlexType().getAttribute("hbiDivision").getAttDisplay() + ":";
			Object hbiDivObj = product.getValue("hbiDivision");
			if(hbiDivObj != null)
				hbiDivStr = product.getValue("hbiDivision").toString();	
			if(hbiDivStr!=null)
			{
				hbiDivKeyValue =product.getFlexType().getAttribute("hbiDivision").getAttValueList().getSelectableValues(null,true);
				hbiDivCatKey =product.getFlexType().getAttribute("hbiDivision").getAttValueList().getSelectableKeys(null,true);
			}
			Iterator hbiDivKeyIter = hbiDivCatKey.iterator();
			Iterator hbiDivKeyValueIter = hbiDivKeyValue.iterator();
			HashMap<String,String> hbiDivHashKeys = new HashMap<String,String>();
			while(hbiDivKeyIter.hasNext() && hbiDivKeyValueIter.hasNext())
			{    		
				String keyVal1 =hbiDivKeyIter.next().toString().trim();
				String tmpVal1= hbiDivKeyValueIter.next().toString().trim();
				hbiDivHashKeys.put(keyVal1, tmpVal1);    		
			}
			hbiDivValue = hbiDivHashKeys.get(hbiDivStr);
			// Changed for HBI by Karthik from UST End on 24/01/13

			if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_GARMENT) != -1)
			{
				Collection brandCatKeyValue =null;
				Collection brandCatKey =null;    	
				hbiBrandCatLbl = product.getFlexType().getAttribute("hbiBrandCategory").getAttDisplay() + ":";
				Object hbiBrandCatObj = product.getValue("hbiBrandCategory");
				if(hbiBrandCatObj != null)
					hbiBrandCatStr = product.getValue("hbiBrandCategory").toString();	
				if(hbiBrandCatStr!=null)
				{
					brandCatKeyValue =product.getFlexType().getAttribute("hbiBrandCategory").getAttValueList().getSelectableValues(null,true);
					brandCatKey =product.getFlexType().getAttribute("hbiBrandCategory").getAttValueList().getSelectableKeys(null,true);
				}
				Iterator brandKeyIter = brandCatKey.iterator();
				Iterator brandKeyValueIter = brandCatKeyValue.iterator();
				HashMap<String,String> brandHashKeys = new HashMap<String,String>();
				while(brandKeyIter.hasNext() && brandKeyValueIter.hasNext())
				{    		
					String keyVal1 =brandKeyIter.next().toString().trim();
					String tmpVal1= brandKeyValueIter.next().toString().trim();
					brandHashKeys.put(keyVal1, tmpVal1);    		
				}
				brandCatValue = brandHashKeys.get(hbiBrandCatStr);
			}
			//SPL
			LCSSeasonMaster seasonMaster = null;
			LCSSeasonProductLink spLink = null;
			LCSSeason season = null;
			String seasonNameAtt = "";
			String seasonName = "";

			if(FormatHelper.hasContent((String)params.get(SEASONMASTER_ID))){
				seasonMaster = (LCSSeasonMaster)LCSQuery.findObjectById((String)params.get(SEASONMASTER_ID));
				season = (LCSSeason)VersionHelper.latestIterationOf(seasonMaster);
				seasonNameAtt = season.getFlexType().getAttribute("seasonName").getAttDisplay() + ":";
				seasonName = (String)season.getValue("seasonName");
				//spLink = LCSSeasonQuery.findSeasonProductLink(product, season);
			}

			//Spec
			LCSSourcingConfig sConfig = null;
			LCSSourceToSeasonLink sslink = null;	
			String sNameAtt = "";
			String sName  = "";
			String sConfigNameAtt = "";
			String sConfigName    ="";
			String pattVersionLbl = "";
			String pattVersion = "";
			String activeSpec = "";
			String hbiActiveSpecLbl = "Active Specification:";
			String activeSpecValue = "";
			String hbiActiveSpecStr = "";

			String specId = (String)params.get(SPEC_ID);
			if(FormatHelper.hasContent(specId)){
				FlexSpecification spec = (FlexSpecification)LCSProductQuery.findObjectById(specId);

				sNameAtt = spec.getFlexType().getAttribute("specName").getAttDisplay() + ":";
				sName = (String)spec.getValue("specName");

				sConfig = (LCSSourcingConfig)VersionHelper.latestIterationOf((Mastered)spec.getSpecSource());

				if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_PATTERN) != -1)
				{
					pattVersionLbl = sConfig.getFlexType().getAttribute("hbiSrcPatternVer").getAttDisplay() + ":";
					pattVersion = (String)sConfig.getValue("hbiSrcPatternVer");
				}
				if(season != null)
				{
					sslink  = (new LCSSourcingConfigQuery()).getSourceToSeasonLink(sConfig, season);
					if(sslink != null)
					{
						Collection actSpecKeyValue =null;
						Collection actSpecKey =null;    	
						Object hbiActivSpecObj = sslink.getValue("hbiActiveSpec");
						if(hbiActivSpecObj != null)
							hbiActiveSpecStr = sslink.getValue("hbiActiveSpec").toString();	
						if(hbiActiveSpecStr!=null)
						{
							actSpecKeyValue =sslink.getFlexType().getAttribute("hbiActiveSpec").getAttValueList().getSelectableValues(null,true);
							actSpecKey =sslink.getFlexType().getAttribute("hbiActiveSpec").getAttValueList().getSelectableKeys(null,true);
						}
						Iterator actSpecKeyIter = actSpecKey.iterator();
						Iterator actSpecKeyValueIter = actSpecKeyValue.iterator();
						HashMap<String,String> actSpecHashKeys = new HashMap<String,String>();
						while(actSpecKeyIter.hasNext() && actSpecKeyValueIter.hasNext())
						{    		
							String keyVal1 =actSpecKeyIter.next().toString().trim();
							String tmpVal1= actSpecKeyValueIter.next().toString().trim();
							actSpecHashKeys.put(keyVal1, tmpVal1);    		
						}
						activeSpecValue = actSpecHashKeys.get(hbiActiveSpecStr);
					}
				}
				sConfigNameAtt = sConfig.getFlexType().getAttribute("name").getAttDisplay() + ":";
				sConfigName = (String)sConfig.getValue("name");		
			}
			if((product.getFlexType().getFullName().indexOf(HBI_SUPPORTING) == -1) 
					&& (product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_COLORWAY) == -1)){			 
				//
				try{
					String hbiTechDesLbl = "";
					String hbiTechDesStr = "";
					String hbiTechDesValue = "";
					Collection hbiTechDesKeyValue =null;
					Collection hbiTechDesCatKey =null;    	
					hbiTechDesLbl = product.getFlexType().getAttribute("hbiTechnicalDesigner").getAttDisplay() + ":";
					Object hbiTechDesObj = product.getValue("hbiTechnicalDesigner");
					if(hbiTechDesObj != null)
						hbiTechDesStr = product.getValue("hbiTechnicalDesigner").toString();	
					if(hbiTechDesStr!=null)
					{
						hbiTechDesKeyValue =product.getFlexType().getAttribute("hbiTechnicalDesigner").getAttValueList().getSelectableValues(null,true);
						hbiTechDesCatKey =product.getFlexType().getAttribute("hbiTechnicalDesigner").getAttValueList().getSelectableKeys(null,true);
					}
					Iterator hbiTechDesKeyIter = hbiTechDesCatKey.iterator();
					Iterator hbiTechDesKeyValueIter = hbiTechDesKeyValue.iterator();
					HashMap<String,String> hbiTechDesHashKeys = new HashMap<String,String>();
					while(hbiTechDesKeyIter.hasNext() && hbiTechDesKeyValueIter.hasNext())
					{    		
						String keyVal1 =hbiTechDesKeyIter.next().toString().trim();
						String tmpVal1= hbiTechDesKeyValueIter.next().toString().trim();
						hbiTechDesHashKeys.put(keyVal1, tmpVal1);    		
					}
					hbiTechDesValue = hbiTechDesHashKeys.get(hbiTechDesStr);
					technicalDesigner = hbiTechDesValue;


					/*FlexObject flexobj = (FlexObject)product.getValue("hbiTechnicalDesigner");
				String userId = (String)flexobj.get("OID");
				if (!userId.equals("")) {
					 WTUser user = (WTUser) LCSQuery.findObjectById("wt.org.WTUser:" + userId);
                    technicalDesigner = user.getFullName();
					System.out.println("technicalDesigner :" + technicalDesigner);
			    }*/

				}catch(Exception e) {
					System.out.println(e);
				}

				//
				if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_GARMENT) != -1)
				{  //
					try{
						String hbiProdManLbl = "";
						String hbiProdManStr = "";
						String hbiProdManValue = "";
						Collection hbiProdManKeyValue =null;
						Collection hbiProdManCatKey =null;    	
						hbiProdManLbl = product.getFlexType().getAttribute("hbiProductManager").getAttDisplay() + ":";
						Object hbiProdManObj = product.getValue("hbiProductManager");
						if(hbiProdManObj != null)
							hbiProdManStr = product.getValue("hbiProductManager").toString();	
						if(hbiProdManStr!=null)
						{
							hbiProdManKeyValue =product.getFlexType().getAttribute("hbiProductManager").getAttValueList().getSelectableValues(null,true);
							hbiProdManCatKey =product.getFlexType().getAttribute("hbiProductManager").getAttValueList().getSelectableKeys(null,true);
						}
						Iterator hbiProdManKeyIter = hbiProdManCatKey.iterator();
						Iterator hbiProdManKeyValueIter = hbiProdManKeyValue.iterator();
						HashMap<String,String> hbiProdManHashKeys = new HashMap<String,String>();
						while(hbiProdManKeyIter.hasNext() && hbiProdManKeyValueIter.hasNext())
						{    		
							String keyVal1 =hbiProdManKeyIter.next().toString().trim();
							String tmpVal1= hbiProdManKeyValueIter.next().toString().trim();
							hbiProdManHashKeys.put(keyVal1, tmpVal1);    		
						}
						hbiProdManValue = hbiProdManHashKeys.get(hbiProdManStr);
						productManger = hbiProdManValue;


						/*FlexObject flexobj = (FlexObject)product.getValue("hbiProductManager");
					String userId = (String)flexobj.get("OID");
					if (!userId.equals("")) {
						WTUser user = (WTUser) LCSQuery.findObjectById("wt.org.WTUser:" + userId);
						productManger = user.getFullName();
					}*/

					}catch(Exception e) {
						System.out.println(e);
					}
				}
				//pPattrenNumber = getAttValue(product, "hbiPatternNumber");
				//Commented by UST for tech pack correction on 07/18/2012
				/*String pPattrenVersion = getAttValue(product, "hbiPatternVersion");
			pPattrenNumber = pPattrenNumber + " " + pPattrenVersion;*/
			}
			PdfPTable mainTable = new PdfPTable(1);
			float [] colWidths = {95.0F, 10.0F};
			PdfPTable headerTable = new PdfPTable(colWidths);


			//float [] rowWidths = {6.0F,9.0F,6.0F,9.0F,6.0F,9.0F};
			//float [] rowWidths = {6.0F,9.0F,4.0F,11.0F,5.0F,10.0F};
			//float [] rowWidths = {6.0F,20.0F,4.0F,20.0F,10.0F,20.0F};
			float [] rowWidths = {8.0F,30.0F,8.0F,14.0F,12.0F,18.0F};
			if(product.getFlexType().getFullName().indexOf(LABEL) > -1){
				rowWidths = new float[] {20.0F,35.0F,12.0F,25.0F};
			}			
			PdfPTable table = new PdfPTable(rowWidths);
			PdfPCell cell = new PdfPCell();

			cell = createHbiLogoCell();
			cell.setColspan(1);
			cell.setBorder(0);
			table.addCell(cell);

			cell = new PdfPCell();
			cell.setColspan(5);
			PdfPCell cell1 = new PdfPCell();
			cell1 = createHbiLogoCell();
			cell1.setColspan(8); 
			PdfPCell cell2 = new PdfPCell();
			cell2 = createHbiLogoCell();
			cell2.setColspan(4); 
			if(product.getFlexType().getFullName().indexOf(LABEL) > -1){
				cell.setColspan(1);
				cell.setBorder(0);
				table.addCell(cell);
			}			
			cell.setBorder(0);
			table.addCell(cell);



			// All product types other than HBI-Supporting
			if((product.getFlexType().getFullName().indexOf(HBI_SUPPORTING) == -1)
					&& (product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_COLORWAY) == -1)){

				cell1 = createCell(getAttributeLabel(product, "productName"), "FORMLABEL");
				table.addCell(cell1);
				cell1 = createCell(getAttValue(product, "productName"), "DISPLAYTEXT");
				table.addCell(cell1);

				if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_PATTERN) != -1)
				{
					cell2 = new PdfPCell();
					cell2.setBorder(0);
					table.addCell(cell2);

					cell2 = new PdfPCell();
					cell2.setBorder(0);
					table.addCell(cell2);	
				}
				if(product.getFlexType().getFullName().indexOf(HBI_SUPPORTING_IMAGE) == -1)
				{
					cell = createCell("Season Name:", "FORMLABEL"); 
					table.addCell(cell);
					cell = createCell(seasonName, "DISPLAYTEXT");
					table.addCell(cell);
				}
				if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_GARMENT) != -1)
				{
					cell = createCell(getAttributeLabel(product, "hbiTechnicalDesigner"), "FORMLABEL"); 
					table.addCell(cell);
					cell = createCell(getAttListValue(product.getFlexType().getAttribute("hbiTechnicalDesigner"), product), "DISPLAYTEXT"); 
					table.addCell(cell);
				}

				//Changed by UST - to display the value in Tech Pack 01/01/2013
				if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_GARMENT) != -1)
				{
					cell = createCell("Product Type:", "FORMLABEL"); 
					table.addCell(cell);
					cell = createCell("Garment", "DISPLAYTEXT");
					table.addCell(cell);			
				}
				/*cell = createCell(getAttributeLabel(product, "hbiProductClass"), "FORMLABEL"); 
				table.addCell(cell);
				cell = createCell(getAttListValue(product.getFlexType().getAttribute("hbiProductClass"), product), "DISPLAYTEXT"); 
				table.addCell(cell);*/

				if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_PATTERN) != -1)
				{
					cell = createCell("Product Type:", "FORMLABEL"); 
					table.addCell(cell);
					cell = createCell("Pattern", "DISPLAYTEXT");
					table.addCell(cell);			
				}

				if(product.getFlexType().getFullName().indexOf(HBI_SUPPORTING_IMAGE) == -1)
				{
					cell = createCell("Source Name:", "FORMLABEL");
					table.addCell(cell);
					cell = createCell(sConfigName, "DISPLAYTEXT");
					table.addCell(cell);
				}
				//Changed by UST - to display the value in Tech Pack 01/01/2013
				if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_PATTERN) == -1)
				{
					cell = createCell(getAttributeLabel(product, "hbiProductManager"), "FORMLABEL"); 
					table.addCell(cell);
					cell = createCell(getAttListValue(product.getFlexType().getAttribute("hbiProductManager"), product), "DISPLAYTEXT"); 
					table.addCell(cell);	
				}
				if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_GARMENT) != -1)
				{
					cell = createCell(hbiBrandCatLbl, "FORMLABEL"); 
					table.addCell(cell);
					cell = createCell(brandCatValue, "DISPLAYTEXT"); 
					table.addCell(cell);				
				}
				if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_PATTERN) != -1)
				{
					cell = createCell(getAttributeLabel(product, "hbiTechnicalDesigner"), "FORMLABEL"); 
					table.addCell(cell);
					cell = createCell(getAttListValue(product.getFlexType().getAttribute("hbiTechnicalDesigner"), product), "DISPLAYTEXT"); 
					table.addCell(cell);
				}
				if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_PATTERN) != -1)
				{
					String hbiPattNumLbl = "";
					String hbiPattNumStr = "";
					hbiPattNumLbl = product.getFlexType().getAttribute("hbiPatternNo").getAttDisplay() + ":";
					Object hbiPattNum = ((LCSPart)product).getValue("hbiPatternNo");	
					if(hbiPattNum!=null)
						hbiPattNumStr = hbiPattNum.toString();

					cell = createCell(hbiPattNumLbl, "FORMLABEL");
					table.addCell(cell);
					cell = createCell(hbiPattNumStr, "DISPLAYTEXT");
					table.addCell(cell);
				}
				if(product.getFlexType().getFullName().indexOf(HBI_SUPPORTING_IMAGE) == -1)
				{
					cell = createCell("Spec Name:", "FORMLABEL");
					table.addCell(cell);
					cell = createCell(sName, "DISPLAYTEXT");
					table.addCell(cell);
				}
				cell = createCell(pCreateDateLbl, "FORMLABEL");
				table.addCell(cell);
				cell = createCell(pCreateDateVal, "DISPLAYTEXT");
				table.addCell(cell);

				if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_GARMENT) != -1)
				{
					cell = createCell(hbiDivLbl, "FORMLABEL");
					table.addCell(cell);
					cell = createCell(hbiDivValue, "DISPLAYTEXT");
					table.addCell(cell); 
				}
				if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_PATTERN) != -1)
				{
					cell = createCell(pattVersionLbl, "FORMLABEL");
					table.addCell(cell);
					cell = createCell(pattVersion, "DISPLAYTEXT");
					table.addCell(cell);
				}

				cell = createCell(hbiActiveSpecLbl, "FORMLABEL");
				table.addCell(cell);
				cell = createCell(activeSpecValue, "DISPLAYTEXT");
				table.addCell(cell);

				cell = createCell(pUpdateDateLbl, "FORMLABEL");
				table.addCell(cell);
				cell = createCell(pUpdateDateVal, "DISPLAYTEXT");
				table.addCell(cell);

			}
			else if(product.getFlexType().getFullName().indexOf(LABEL) == -1){ //All HBI-Supporting product types other than Label Products
				cell = createCell(getAttributeLabel(product, "productName"), "FORMLABEL");
				table.addCell(cell);
				cell = createCell(getAttValue(product, "productName"), "DISPLAYTEXT");
				table.addCell(cell);

				if(product.getFlexType().getFullName().indexOf(HBI_SUPPORTING_IMAGE) == -1)
				{
					cell = createCell("Season Name:", "FORMLABEL"); 
					table.addCell(cell);
					cell = createCell(seasonName, "DISPLAYTEXT");
					table.addCell(cell);				

					cell = createCell("Source Name:", "FORMLABEL");
					table.addCell(cell);
					cell = createCell(sConfigName, "DISPLAYTEXT");
					table.addCell(cell);	

					cell = createCell("Spec Name:", "FORMLABEL");
					table.addCell(cell);
					cell = createCell(sName, "DISPLAYTEXT");
					table.addCell(cell);				
				}
				cell = createCell(pCreateDateLbl, "FORMLABEL");
				table.addCell(cell);
				cell = createCell(pCreateDateVal, "DISPLAYTEXT");
				table.addCell(cell);

				cell = createCell(pUpdateDateLbl, "FORMLABEL");
				table.addCell(cell);
				cell = createCell(pUpdateDateVal, "DISPLAYTEXT");
				table.addCell(cell);

				// Changed for HBI by Karthik from UST Start on 24/01/13
				if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_COLORWAY) != -1)
				{
					String colorwayProdFinalLbl = "";
					String colorwayProdFinal = "";
					colorwayProdFinalLbl = product.getFlexType().getAttribute("hbiCPColorwayProductFinalAct").getAttDisplay() + ":";
					Date colorwayProdFinalVal = (Date)product.getValue("hbiCPColorwayProductFinalAct");				
					if(colorwayProdFinalVal != null){
						colorwayProdFinal =  FormatHelper.applyFormat(colorwayProdFinalVal, "MM/dd/yyyy");
					}

					cell = createCell(colorwayProdFinalLbl, "FORMLABEL");
					table.addCell(cell);
					cell = createCell(colorwayProdFinal, "DISPLAYTEXT");
					table.addCell(cell);

					if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_GARMENT) == -1)
					{
						String descLbl = "";
						String desc = "";
						descLbl = product.getFlexType().getAttribute("hbiDescription").getAttDisplay() + ":";
						desc = (String)product.getValue("hbiDescription");				

						cell = createCell(descLbl, "FORMLABEL");
						table.addCell(cell);
						cell = createCell(desc, "DISPLAYTEXT");
						table.addCell(cell);
					}

					cell = createCell(hbiDivLbl, "FORMLABEL");
					table.addCell(cell);
					cell = createCell(hbiDivStr, "DISPLAYTEXT");
					table.addCell(cell); 
				}
				// Changed for HBI by Karthik from UST End on 24/01/13

			}else{ //For Label Productss
				cell = createCell(getAttributeLabel(product, "productName"), "FORMLABEL");
				table.addCell(cell);
				cell = createCell(getAttValue(product, "productName"), "DISPLAYTEXT");
				table.addCell(cell);

				cell = createCell(getAttributeLabel(product, "hbiLblApplication"), "FORMLABEL"); 
				table.addCell(cell);
				cell = createCell(getAttListValue(product.getFlexType().getAttribute("hbiLblApplication"), product), "DISPLAYTEXT"); 
				table.addCell(cell);

				if(product.getFlexType().getFullName().indexOf(HBI_SUPPORTING_IMAGE) == -1)
				{
					cell = createCell("Season Name:", "FORMLABEL"); 
					table.addCell(cell);
					cell = createCell(seasonName, "DISPLAYTEXT");
					table.addCell(cell);
				}
				cell = createCell(getAttributeLabel(product, "hbiLblBrand"), "FORMLABEL"); 
				table.addCell(cell);
				cell = createCell(getAttListValue(product.getFlexType().getAttribute("hbiLblBrand"), product), "DISPLAYTEXT"); 
				table.addCell(cell);

				cell = createCell(getAttributeLabel(product, "hbiStylGrpLbl"), "FORMLABEL"); 
				table.addCell(cell);
				cell = createCell(getAttListValue(product.getFlexType().getAttribute("hbiStylGrpLbl"), product), "DISPLAYTEXT"); 
				table.addCell(cell);

				labelCallOut = (LCSLifecycleManaged)product.getValue("hbiLblSubBrand");
				if(labelCallOut != null){
					labelCallOutName = labelCallOut.getIdentity();
				}
				cell = createCell(getAttributeLabel(product, "hbiLblSubBrand"), "FORMLABEL");
				table.addCell(cell);
				cell = createCell(labelCallOutName, "DISPLAYTEXT");
				table.addCell(cell);				

				cell = createCell(getAttributeLabel(product, "hbiGmtProducts"), "FORMLABEL");
				table.addCell(cell);
				cell = createCell(getAttValue(product, "hbiGmtProducts"), "DISPLAYTEXT");
				table.addCell(cell);

				cell = createCell(pCreateDateLbl, "FORMLABEL");
				table.addCell(cell);
				cell = createCell(pCreateDateVal, "DISPLAYTEXT");
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setBorder(0);
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setBorder(0);
				table.addCell(cell);	

				cell = createCell(pUpdateDateLbl, "FORMLABEL");
				table.addCell(cell);
				cell = createCell(pUpdateDateVal, "DISPLAYTEXT");
				table.addCell(cell); 
				/*hbiStylGrpLbl - Single list
hbiGmtProducts - text area
hbiLblApplication - single list
hbiLblBrand - singlelist
hbiLblSubBrand - object reference*/
				// Changed for HBI by Karthik from UST Start on 24/01/13
				if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_COLORWAY) != -1)
				{
					String colorwayProdFinalLbl = "";
					String colorwayProdFinal = ""; 
					cell = createCell(colorwayProdFinalLbl, "FORMLABEL");
					table.addCell(cell);
					cell = createCell(colorwayProdFinal, "DISPLAYTEXT");
					table.addCell(cell); 

					if(product.getFlexType().getFullName().indexOf(BASIC_CUT_AND_SEW_GARMENT) == -1)
					{
						String descLbl = "";
						String desc = "";
						descLbl = product.getFlexType().getAttribute("hbiDescription").getAttDisplay() + ":";
						desc = (String)product.getValue("hbiDescription");				

						cell = createCell(descLbl, "FORMLABEL");
						table.addCell(cell);
						cell = createCell(desc, "DISPLAYTEXT");
						table.addCell(cell);
					}

					cell = createCell(hbiDivLbl, "FORMLABEL");
					table.addCell(cell);
					cell = createCell(hbiDivStr, "DISPLAYTEXT");
					table.addCell(cell);
				}				
				// Changed for HBI by Karthik from UST End on 24/01/13

			}

			cell = new PdfPCell(table);
			cell.setBorder(0);
			headerTable.addCell(cell);

			cell = createImageCellProductThumbnail(params);
			cell.setBorder(0);
			cell.setColspan(5);
			headerTable.addCell(cell);

			cell = new PdfPCell(headerTable);
			cell.setBorder(0);
			mainTable.addCell(cell);

			PdfPCell tableCell = new PdfPCell(mainTable);
			tableCell.setPadding(3.0F);
			tableCell.setBackgroundColor(pgh.getCellBGColor("BORDERED_BLOCK", null));
			tableCell.setBorderColor(pgh.getColor("HEX669999"));
			tableCell.setBorderColorLeft(pgh.getCellBGColor("BORDERED_BLOCK", null));
			tableCell.setHorizontalAlignment(Element.ALIGN_LEFT);
			tableCell.setVerticalAlignment(Element.ALIGN_MIDDLE);


			return tableCell;

		}catch(Exception e){
			throw new WTException(e);
		}

	}

	/** Get attribute Label
	 * @param typed
	 * @parem keyName
	 * @return String
	 */
	private  String getAttributeLabel(FlexTyped typed, String keyName)throws WTException{

		String attLable = typed.getFlexType().getAttribute(keyName).getAttDisplay() + ":";
		return attLable;
	}


	/** Get attribute (text) value of objects like product, season, source
	 * @parem typed
	 * @return String
	 */
	private String getAttValue(FlexTyped typed, String attKeyName){

		//attKeyName is key name
		String attValue = " ";
		try{
			attValue = (String)typed.getValue(attKeyName);
			if(!FormatHelper.hasContent(attValue)){
				attValue = " ";
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return attValue;
	}


	/** Get attribute(single list or driven) value of objects like product, season, source
	 * @param att, typed
	 * @return String
	 */
	private String getAttListValue(FlexTypeAttribute att, FlexTyped typed)
	{
		String key = ""; 
		String value = "";
		try{
			key = att.getAttKey();
			value = (String)typed.getValue(key);
			AttributeValueList valueList = att.getAttValueList();
			if(valueList !=null){
				value = valueList.getValue(value,null);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * @param text
	 * @param textType
	 * @return PdfPCelll
	 */
	private PdfPCell createCell(String text, String textType){
		if( "null".equalsIgnoreCase(text))
			text = "";
		PdfPCell cell = new PdfPCell(pgh.multiFontPara(text, pgh.getCellFont(textType, null, null )));
		cell.setBackgroundColor(pgh.getCellBGColor("BORDERED_BLOCK", null));
		cell.setBorderColor(pgh.getCellBGColor("BORDERED_BLOCK", null));
		cell.setBorderWidth(0.0f);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		return cell;
	}


}// class end