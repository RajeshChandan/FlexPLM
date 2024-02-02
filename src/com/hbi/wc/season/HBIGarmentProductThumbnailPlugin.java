package com.hbi.wc.season;

import java.util.HashMap;
import java.util.Map;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.SeasonGroupToProductLink;
import com.lcs.wc.util.FormatHelper;

import org.apache.log4j.Logger;
import   wt.log4j.LogR;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * HBIGarmentProductThumbnailPlugin.java
 * 
 * This class contains generic functions which are using to validate and cascade the 'Garment Product' Thumbnail from referencing garment product to the SeasonGroupToProductLink on change
 * @author Abdul.Patel@Hanes.com
 * @since May-02-2016
 */
public class HBIGarmentProductThumbnailPlugin
{
	private static String mappingAttributesKey = "hbiGroupProductLinkGPRef:hbiGPOption1Thumbnail,hbiGarmentProdRefOption2:hbiGPOption2Thumbnail,hbiGarmentProdRefOption3:hbiGPOption3Thumbnail,hbiGarmentProdRefOption4:hbiGPOption4Thumbnail,hbiGarmentProdRefOption5:hbiGPOption5Thumbnail,hbiGarmentProdRefOption6:hbiGPOption6Thumbnail,hbiGarmentProdRefOption7:hbiGPOption7Thumbnail,hbiGarmentProdRefOption8:hbiGPOption8Thumbnail,hbiGarmentProdRefOption9:hbiGPOption9Thumbnail,hbiGarmentProdRefOption10:hbiGPOption10Thumbnail";
	private static final Logger logger = LogR.getLogger("com.hbi.wc.season.HBIGarmentProductThumbnailPlugin");
	/**
	 * This function is using as a plug-in function which is registered on SeasonGroupToProductLink PRE_CREATE_PERSIST EVENT to cascade Garment Product Thumbnail to the SeasonGroup Link
	 * @param wtObj - WTObject
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void validateAndUpdateGarmentProductThumbnail(WTObject wtObj) throws WTException, WTPropertyVetoException
	{
		logger.debug("### START HBIGarmentProductThumbnailPlugin.validateAndUpdateGarmentProductThumbnail(WTObject wtObj) ###");
		
		//Validating the incoming WTObject and processing/returning based on the instance type of the WTObject (processing only if the instance is of type SeasonGroupToProductLink Object
		if(!(wtObj instanceof SeasonGroupToProductLink))
		{
		logger.debug("Returning without performing any action as the incoming object is not an instance of SeasonGroupToProductLink, this plug-in is on SeasonGroupToProductLink");
			return;
		}
		
		//Get Attributes Map(a map contains two attributes one is an object reference attribute (referring to 'Garment Product') and the other one is using to populate Product Thumbnail 
		SeasonGroupToProductLink seasonGroupToProductLinkObj = (SeasonGroupToProductLink) wtObj;
		Map<String, String> attributeKeysMap = new HBIGarmentProductThumbnailPlugin().getAttributeKeysMapfromPropertiesEntry(mappingAttributesKey);
		new HBIGarmentProductThumbnailPlugin().validateAndUpdateGarmentProductThumbnail(seasonGroupToProductLinkObj, attributeKeysMap);
		
		logger.debug("### END HBIGarmentProductThumbnailPlugin.validateAndUpdateGarmentProductThumbnail(WTObject wtObj) ###");
	}
	
	/**
	 * This function is using to iterate on each attributes to validate the associated garment production as options, get garment product Thumbnail image and populate on season group link
	 * @param seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @param attributeKeysMap - Map<String, String>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void validateAndUpdateGarmentProductThumbnail(SeasonGroupToProductLink seasonGroupToProductLinkObj, Map<String, String> attributeKeysMap) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIGarmentProductThumbnailPlugin.validateAndUpdateGarmentProductThumbnail(seasonGroupToProductLinkObj, Map<String, String> attributeKeysMap) ###");
		String seasonGroupToProdLinkImageAttKey = "";
		String garmentProductThumbnailImage = "";
		String seasonGroupToProdLinkImage = "";
		LCSProduct productObj = null;
		
		//Iterating on each attributes (this attribute contains two keys which are residing on SeasonGroupToProductLink, one is using to get linked Product object & other one is for Image
		for(String garmentProductKey : attributeKeysMap.keySet())
		{
			seasonGroupToProdLinkImageAttKey = attributeKeysMap.get(garmentProductKey);
			seasonGroupToProdLinkImage = ""+(String) seasonGroupToProductLinkObj.getValue(seasonGroupToProdLinkImageAttKey);
			productObj = (LCSProduct) seasonGroupToProductLinkObj.getValue(garmentProductKey);
			
			//Validate the 'Garment Product', get 'Thumbnail Image' from the Garment Product, validate the Image data with the SeasonGroupToProductLink data, update/populate new Thumbnail
			if(productObj != null)
			{
				garmentProductThumbnailImage = ""+productObj.getPartPrimaryImageURL();
				if(!garmentProductThumbnailImage.equals(seasonGroupToProdLinkImage))
				{
					seasonGroupToProductLinkObj.setValue(seasonGroupToProdLinkImageAttKey, garmentProductThumbnailImage);
				}
			}
			else if(FormatHelper.hasContent(seasonGroupToProdLinkImage))
			{
				seasonGroupToProductLinkObj.setValue(seasonGroupToProdLinkImageAttKey, "");
			}
		}
		
		// LCSLog.debug("### END HBIGarmentProductThumbnailPlugin.validateAndUpdateGarmentProductThumbnail(seasonGroupToProductLinkObj, Map<String, String> attributeKeysMap) ###");
	}
	
	/**
	 * This function is using to format the given string(looks like hbiGroupProductLinkGPRef:hbiGPOption1Thumbnail,hbiGarmentProdRefOption2:hbiGPOption2Thumbnail,etc) into a Map(Key-Value)
	 * @param propertiesEntry - String
	 * @return flexObjectDataMap - Map<String, String>
	 * @throws WTException
	 */
	public Map<String, String> getAttributeKeysMapfromPropertiesEntry(String propertiesEntry) throws WTException
	{
		// LCSLog.debug("### START HBIGarmentProductThumbnailPlugin.getAttributeKeysMapfromPropertiesEntry(propertiesEntry) ###");
		Map<String, String> attributeKeysMap = new HashMap<String, String>();
		String keyValueDataPairs[] = propertiesEntry.split(",");
		String garmentProductObjRefKey = "";
		String garmentProductThumbnailKey = "";
		
		if(keyValueDataPairs != null && keyValueDataPairs.length > 1)
		{
			//Iterating on each Key-Value entry(which is separated using : as delimiter), initialize attribute-key data and preparing Map object with the existing Key-value set
			for(String attributeKeys : keyValueDataPairs)
			{
				garmentProductObjRefKey = attributeKeys.split(":")[0];
				garmentProductThumbnailKey = attributeKeys.split(":")[1];
				attributeKeysMap.put(garmentProductObjRefKey, garmentProductThumbnailKey);
			}
		}
		
		// LCSLog.debug("### END HBIGarmentProductThumbnailPlugin.getAttributeKeysMapfromPropertiesEntry(propertiesEntry) ###");
		return attributeKeysMap;
	}
}