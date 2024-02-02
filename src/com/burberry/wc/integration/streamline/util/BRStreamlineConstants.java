package com.burberry.wc.integration.streamline.util;

import java.io.File;

public class BRStreamlineConstants {
	
	public static final String PRODUCT_TYPE = "productType";
	
	public static final String LCS_FLEXSPECIFICATION_ROOT_ID = "OR:com.lcs.wc.specification.FlexSpecification:";
	/**
	 * LCS_FLEXSPECIFICATION_IDA2A2.
	 */
	public static final String LCS_FLEXSPECIFICATION_IDA2A2 = "FlexSpecification.IDA2A2";
	
	//Product
	
	public static final String STYLE_NUM="vrdStyleNum";
	public static final String STYLE_ID = "styleID";
	
	public static final String VENDOR ="vendor";
	public static final String TYPE ="Type";
	public static final String CREATED_BY ="createdBy";
	public static final String MODIFIED_BY ="modifiedBy";
	public static final String BUROPERATIONALCATEGORY ="burOperationalCategory";
	public static final String BUROPERATIONALCATEGORYAPP ="burOperationalCategoryApp";
	public static final String APP_ID ="appID";
	public static final String BURSTREAMLINEPRODUCTID ="burStreamlineProductID";
	public static final String SEASON_ID ="seasonID";
	public static final String BURDEVELOPMENTSEASON ="burDevelopmentSeason";
	public static final String BURAGEGROUP ="burAgeGroup";
	public static final String BRAND ="brand";
	public static final String VRDBRAND ="vrdBrand";
	public static final String BURLAST ="burLast";
	public static final String BURACCESSORIESGROUP ="burAccessoriesGroup";
	
	public static final String SOURCINGCONFIG = "SourcingConfig";
	public static final String IMAGEPAGE = "imagePage";
	public static final String PRODUCT = "Product";
	
	//SourcingConfig
	public static final String SUPPLIER_NAME ="name";
	public static final String VRD_COUNTRY_OF_ORIGIN ="vrdCountryOfOrigin";
	
	public static final String FLEXTYPE_PRODUCT_APPAREL_MENS = "Product\\Apparel\\Mens";
	public static final String FLEXTYPE_PRODUCT_APPAREL_WOMENS = "Product\\Apparel\\Womens";
	public static final String FLEXTYPE_CHILDRENS = "childrens";
	public static final String FLEXTYPE_FOOTWEAR = "footwear";
	public static final String FLEXTYPE_ACCESSORIES = "accessories";
	public static final String FLEXTYPE_APPAREL = "apparel";
	
	//Specification
	public static final String SPECFLEXTYPE_APPAREL = "Specification\\Apparel";
	public static final String SPECFLEXTYPE_ACCESSORIES = "Specification\\burAccessoriesSpec";
	public static final String SPECFLEXTYPE_FOOTWEAR = "Specification\\Footwear";
	public static final String SPECNAME_APPAREL = "Apparel Specification";
	public static final String SPECNAME_ACCESSORIES = "Accessories Specification";
	public static final String SPECNAME_FOOTWEAR = "Footwear Specification";
	
	//Image
	public static final String IMAGESPATH = System.getProperty("wt.home") + File.separator + "codebase" + File.separator + "images";
	public static final String IMAGEURL = "imageURL";
	public static final String IMAGENAME = "imageName";
	public static final String IMAGEID = "imageID";
	public static final String PAGETYPE = "pageType";
	public static final String PAGELAYOUT = "pageLayout";
	public static final String PAGEDESCRIPTION = "pageDescription";
	public static final String OWNERREFERENCE = "ownerReference";
	public static final String COVERPAGE = "coverpage";
	public static final String CRUD = "CRUD";
	
	public static final String CREATE_PRODUCT_REQ_ATTS ="com.burberry.wc.com.burberry.wc.integration.streamline.BRCreateProduct.atts";
	public static final String CREATE_IMAGE_REQ_ATTS ="com.burberry.wc.com.burberry.wc.integration.streamline.BRCreateImage.atts";
	public static final String UPDATE_PRODUCT_REQ_ATTS ="com.burberry.wc.com.burberry.wc.integration.streamline.BRUpdateProduct.atts";
	public static final String UPDATE_IMAGE_REQ_ATTS ="com.burberry.wc.com.burberry.wc.integration.streamline.BRUpdateImage.atts";
	
	
	public static final String PRODUCT_FLEX_TYPE="productFlexType";
	public static final String SAMPLE_FLEX_TYPE="SampleFlexType";
	public static final String BUR_STYLE_ID = "burStyleID";
	
	//Madhu changes-start
	
	//SAMPLE CONSTANTS
	public static final String SAMPLE_REQUEST_TYPE="vrdRequestType";
	public static final String SAMPLE_SEASON_REQUESTED="vrdSeasonRequested";
	public static final String SAMPLE_ORDER_UNITS="burSampleOrderUnits";
	public static final String SAMPLE_SHIP_TO_DESTINATION="burShipToDestination";
	public static final String SAMPLE_SEND_NOTIFICATION="burSendNotification";
	public static final String SAMPLE_COST_CURRENCY="burSampleCostCurrency";
	public static final String SAMPLE_COST="burSampleCost";
	public static final String SAMPLE_COST_STATUS="burSampleCostStatus";
	public static final String SAMPLE_REQUEST_COMMENTS="requestComments";
	public static final String SAMPLE_DUE_DATE="burSampleDueDate";
	public static final String SAMPLE_REQUEST_DATE="sampleRequestRequestDate";
	public static final String SAMPLE_REQUEST_DATE_JSON="sampleRequestDate";
	
	public static final String SIMPLE_DATE_FORMAT="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	public static final String CREATE_SAMPLE_REQ_ATTS="com.burberry.wc.integration.streamline.BRCreateSample.att";
	
	public static final String CREATE_MULTIPLE_SAMPLE_REQ_ATTS="com.burberry.wc.integration.streamline.BRCreateMultipleSample.att";
	//BOM CONSTANTS
	
	public static final String CREATE_BOM_REQ_ATTS="com.burberry.wc.integration.streamline.BRCreateBOM.att";
	public static final String APPAREL="apparel";
	public static final String ACCESSORIES="accessories";
	public static final String FOOTWEAR="footwear";
	
	public static final String PLACEMENT="burPlacement";
	public static final String PARTNAME="partName";
	public static final String PLACEMENTNOTES="placementNotes";
	public static final String MATERIAL_DESCRIPTION="materialDescription";
	public static final String COLOR_DESCRIPTION="colorDescription";
	
	//Measurement 
	
	public static final String MEASUREMENT_REQ_ATTS="com.burberry.wc.integration.streamline.BRStreamlineMeasurementAPI.att";
	//Madhu changes-end

}
