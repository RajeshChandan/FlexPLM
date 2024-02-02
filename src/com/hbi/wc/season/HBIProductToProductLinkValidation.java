package com.hbi.wc.season;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import wt.fc.PersistenceHelper;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductLogic;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.product.ProductToProductLink;
import com.lcs.wc.season.SeasonGroupToProductLink;
import com.lcs.wc.season.SeasonProductLocator;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

/**
 * HBIProductToProductLinkValidation.java
 * 
 * This class contains a plug-in function and generic functions which are invoking on SeasonGroupToProductLink update event to validate the 'GP Option 1' to 'GP Option 10' selection status
 * get 'GP Option' object where 'GP Options Selected Status' is selected, validate the linked object of a selling product and update the linked objects based on the selected 'GP Options '
 * @author Abdul.Patel@Hanes.com
 * @since June-20-2016
 */
public class HBIProductToProductLinkValidation
{
	private static String gpOptionsSelectedStatus = LCSProperties.get("com.hbi.wc.season.HBIGPOptionsUniquenessValidation.gpOptionsSelectedStatus", "selected");
	private static String garmentAndSellingRelationshipType = LCSProperties.get("com.hbi.wc.season.HBIGPOptionsUniquenessValidation.garmentAndSellingRelationshipType", "Garment-Selling");
	public static String gpOptionsMappingAttributeKeys = "hbiGroupProductLinkGPRef:hbiIsItSelectedGPOption1,hbiGarmentProdRefOption2:hbiIsItSelectedGPOption2,hbiGarmentProdRefOption3:hbiIsItSelectedGPOption3,hbiGarmentProdRefOption4:hbiIsItSelectedGPOption4,hbiGarmentProdRefOption5:hbiIsItSelectedGPOption5,hbiGarmentProdRefOption6:hbiIsItSelectedGPOption6,hbiGarmentProdRefOption7:hbiIsItSelectedGPOption7,hbiGarmentProdRefOption8:hbiIsItSelectedGPOption8,hbiGarmentProdRefOption9:hbiIsItSelectedGPOption9,hbiGarmentProdRefOption10:hbiIsItSelectedGPOption10";
	
	/**
	 * This function is invoking from another plug-in function (validateAndUpdateGPOptionsAttribute) which is registered on SeasonGroupToProductLink PRE_CREATE_PERSIST EVENT for GP Option
	 * @param seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @return productToProductLinkObj - ProductToProductLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public ProductToProductLink validateAndCrateOrUpdateProductToProductLink(SeasonGroupToProductLink seasonGroupToProductLinkObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIProductToProductLinkValidation.validateAndCrateOrUpdateProductToProductLink(SeasonGroupToProductLink seasonGroupToProductLinkObj) ###");
		Map<String, String> attributeKeysMap = getAttributeKeysMapfromPropertiesEntry(gpOptionsMappingAttributeKeys);
		ProductToProductLink productToProductLinkObj = null;
		
		//Initialize 'Garment Product' and 'Selling Product' from the given SeasonGroupToProductLink object, validate the 'Garment Product' and 'Selling Product' for not null condition
		LCSProduct garmentProductObj = getSelectedGarmentProductForSelling(seasonGroupToProductLinkObj, attributeKeysMap);
		LCSProduct sellingProductObj = (LCSProduct) VersionHelper.latestIterationOf(seasonGroupToProductLinkObj.getProductMaster());
		if(garmentProductObj != null && sellingProductObj != null)
		{
			//Get 'A' version of the Garment and Selling Product using the latest version, this is the primary requirement for some of the out of the box functions to return existing data
			garmentProductObj = SeasonProductLocator.getProductARev(garmentProductObj);
			sellingProductObj = SeasonProductLocator.getProductARev(sellingProductObj);
		
			//Calling a function which is using to validate all the linked products for a given child product (validate all the associated garment product for a given selling product)
			validateAndRemoveProductToProductLink(garmentProductObj, sellingProductObj);
			
			//Calling a function which is using to validate the existing ProductToProductLink for the given Parent Product and Child Product, based on the validation status creating link
			productToProductLinkObj = validateAndCreateProductToProductLink(garmentProductObj, sellingProductObj);
		}
		else
		{
			//Calling a function which is using to validate the 'GP Options Selection Status' for all attributes (1 to 10), based on the selection status removing the linked object
			validateGPOptionStatusAndRemoveProductToProductLink(seasonGroupToProductLinkObj, attributeKeysMap);
		}
		
		// LCSLog.debug("### END HBIProductToProductLinkValidation.validateAndCrateOrUpdateProductToProductLink(SeasonGroupToProductLink seasonGroupToProductLinkObj) ###");
		return productToProductLinkObj;
	}
	
	/**
	 * This function is using to validate the existing ProductToProductLink for the given Parent Product and Child Product, based on the validation status creating new ProductToProductLink
	 * @param parentProductObj - LCSProduct
	 * @param childProductObj - LCSProduct
	 * @return productToProductLinkObj - ProductToProductLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("unchecked")
	public ProductToProductLink validateAndCreateProductToProductLink(LCSProduct parentProductObj, LCSProduct childProductObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIProductToProductLinkValidation.validateAndCreateProductToProductLink(LCSProduct parentProductObj, LCSProduct childProductObj) ###");
		ProductToProductLink productToProductLinkObj = null;
		
		//Initializing Parent and Child Product Master and calling an existing function to get the existing ProductToProductLink from the given Parent Master and Child Master Object
		LCSPartMaster parentMasterObj = (LCSPartMaster) parentProductObj.getMaster();
		LCSPartMaster childMasterObj = (LCSPartMaster) childProductObj.getMaster();
		Collection<FlexObject> existingGarmentandSellingLink = LCSProductQuery.findProductToProductLink(parentMasterObj, childMasterObj, garmentAndSellingRelationshipType);
		
		//Validating the Parent and Child Product and calling an existing function to create/establish link between two products (ProductToProductLink where linkType is siblings)
		if(parentProductObj != null && childProductObj != null && !(existingGarmentandSellingLink != null && existingGarmentandSellingLink.size() > 0))
		{
			productToProductLinkObj = new LCSProductLogic().createProductLink(parentProductObj, childProductObj, garmentAndSellingRelationshipType);
		}
		
		// LCSLog.debug("### END HBIProductToProductLinkValidation.validateAndCreateProductToProductLink(LCSProduct parentProductObj, LCSProduct childProductObj) ###");
		return productToProductLinkObj;
	}
	
	/**
	 * This function is using to validate all the linked products for a given child product (validate all the associated garment product for a given selling product) and update the link
	 * @param garmentProductObj - LCSProduct
	 * @param sellingProductObj - LCSProduct
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("unchecked")
	public void validateAndRemoveProductToProductLink(LCSProduct garmentProductObj, LCSProduct sellingProductObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIProductToProductLinkValidation.validateAndRemoveProductToProductLink(LCSProduct garmentProductObj, LCSProduct sellingProductObj) ###");
		LCSPartMaster sellingProductMasterObj = (LCSPartMaster) sellingProductObj.getMaster();
		ProductToProductLink productToProductLinkObj = null;
		LCSProduct parentProductObj = null;
		String linkType = "";
		
		//Calling a function to get all linked products for the given child product (in this case Selling Product is a child Product and trying to fetch all the linked Garment Products)
		Collection<FlexObject> linkedObjectsCollection = LCSProductQuery.getLinkedProducts(sellingProductMasterObj, false, true);
		if(linkedObjectsCollection != null && linkedObjectsCollection.size() > 0)
		{
			for(FlexObject flexObj : linkedObjectsCollection)
			{
				parentProductObj = (LCSProduct) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:"+flexObj.getString("PARENTPRODUCT.BRANCHIDITERATIONINFO"));
				parentProductObj = SeasonProductLocator.getProductARev(parentProductObj);
				linkType = flexObj.getString("PRODUCTTOPRODUCTLINK.LINKTYPE");
				
				//Validating the linked product with the current parent product and ProductToProductLink relationshipType, which are using as criteria to deLink the existing ProductLink
				if(!PersistenceHelper.isEquivalent(garmentProductObj, parentProductObj) && garmentAndSellingRelationshipType.equalsIgnoreCase(linkType))
				{
					productToProductLinkObj = (ProductToProductLink) LCSQuery.findObjectById("OR:com.lcs.wc.product.ProductToProductLink:"+flexObj.getString("PRODUCTTOPRODUCTLINK.IDA2A2"));
					new LCSProductLogic().deleteProductToProductLink(productToProductLinkObj, true, false);
				}
			}
		}
		
		// LCSLog.debug("### END HBIProductToProductLinkValidation.validateAndRemoveProductToProductLink(LCSProduct garmentProductObj, LCSProduct sellingProductObj) ###");
	}
	
	/**
	 * This function is using to get associated garment product from the a selling product based on the 'GP Options Selection Status' attribute flag from the given SeasonGroupToProductLink
	 * @param seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @param attributeKeysMap - Map<String, String>
	 * @return garmentProductObj - LCSProduct
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSProduct getSelectedGarmentProductForSelling(SeasonGroupToProductLink seasonGroupToProductLinkObj, Map<String, String> attributeKeysMap) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIProductToProductLinkValidation.getSelectedGarmentProductForSelling(SeasonGroupToProductLink seasonGroupToProductLinkObj) ###");
		LCSProduct garmentProductObj = null;
		LCSProduct oldGarmentProductObj = null;
		String currentObjGPOptionsSelectionStatus = "";
		String oldObjGPOptionsSelectionStatus = "";
		
		//Iterating on 'GP Options Selection Status' attribute (a total of 10 'GP Options' and corresponding 'GP Options Selection Status' attribute which are created at Selling Product
		for(String attributeKey : attributeKeysMap.keySet())
		{
			currentObjGPOptionsSelectionStatus = (String)seasonGroupToProductLinkObj.getValue(attributeKey);
			
			//Validating the 'GP Options Selection Status' attribute, if the selection status is 'selected' then validate the previous iteration status and update the given link object
			if(FormatHelper.hasContent(currentObjGPOptionsSelectionStatus) && gpOptionsSelectedStatus.equalsIgnoreCase(currentObjGPOptionsSelectionStatus))
			{
				String garmentProductRefAttKey = attributeKeysMap.get(attributeKey);
				garmentProductObj = (LCSProduct) seasonGroupToProductLinkObj.getValue(garmentProductRefAttKey);
				
				//Calling a function which is using to get the 'Previous iteration' of the given current object, which is using to validate the changeSet of the attribute data in comparison
				SeasonGroupToProductLink oldSeasonGroupToProductLinkObj = new HBIGPOptionsUniquenessValidation().getOldOrLatestSeasonGroupToProductLink(seasonGroupToProductLinkObj, false, false);
				if(oldSeasonGroupToProductLinkObj != null)
				{
					oldObjGPOptionsSelectionStatus = (String)oldSeasonGroupToProductLinkObj.getValue(attributeKey);
					oldGarmentProductObj = (LCSProduct) oldSeasonGroupToProductLinkObj.getValue(garmentProductRefAttKey);
				}
				
				//Validating the 'Garment Product' and 'Garment Product Selection Status' changeSet from two version of the product, based on the changeSet status re-initializing product
				if(PersistenceHelper.isEquivalent(garmentProductObj, oldGarmentProductObj) && currentObjGPOptionsSelectionStatus.equals(oldObjGPOptionsSelectionStatus))
				{
					garmentProductObj = null;
				}
				break;
			}
		}
		
		// LCSLog.debug("### END HBIProductToProductLinkValidation.getSelectedGarmentProductForSelling(SeasonGroupToProductLink seasonGroupToProductLinkObj) ###");
		return garmentProductObj;
	}
	
	/**
	 * This function is using to validate the 'GP Options Selection Status' for all attributes (1 to 10), based on the selection status removing the linked object (ProductToProductLink)
	 * @param seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @param attributeKeysMap - Map<String, String>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public void validateGPOptionStatusAndRemoveProductToProductLink(SeasonGroupToProductLink seasonGroupToProductLinkObj, Map<String, String> attributeKeysMap) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIProductToProductLinkValidation.validateGPOptionStatusAndRemoveProductToProductLink(seasonGroupToProductLinkObj, attributeKeysMap) ###");
		String gpOptionsSelectionStatus = "";
		
		//Iterating on 'GP Options Selection Status' attribute (a total of 10 'GP Options' and corresponding 'GP Options Selection Status' attribute which are created at Selling Product
		for(String attributeKey : attributeKeysMap.keySet())
		{
			gpOptionsSelectionStatus = (String)seasonGroupToProductLinkObj.getValue(attributeKey);
					
			//Validating the 'GP Options Selection Status' attribute, if the selection status is 'selected' then validate the previous iteration status and update the given link object
			if(FormatHelper.hasContent(gpOptionsSelectionStatus) && gpOptionsSelectedStatus.equalsIgnoreCase(gpOptionsSelectionStatus))
			{
				break;	
			}
		}
		
		//Validating the 'GP Options Selection Status' attribute, if none of the GP Options are selected for a selling product then deLink all the linked products from a selling
		if(!gpOptionsSelectedStatus.equalsIgnoreCase(gpOptionsSelectionStatus))
		{
			//Calling a function which is using to get all the linked objects from the given child product, validate the relationShip type and remove each of the ProductToProductLink
			validateGPOptionStatusAndRemoveProductToProductLink(seasonGroupToProductLinkObj);
		}
		
		// LCSLog.debug("### END HBIProductToProductLinkValidation.validateGPOptionStatusAndRemoveProductToProductLink(seasonGroupToProductLinkObj, attributeKeysMap) ###");
	}
	
	/**
	 * This function is using to get all the linked objects from the given child product (in this case Selling Product is the child), validate the relationShip type and remove the link
	 * @param seasonGroupToProductLinkObj - SeasonGroupToProductLink
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	@SuppressWarnings("unchecked")
	private void validateGPOptionStatusAndRemoveProductToProductLink(SeasonGroupToProductLink seasonGroupToProductLinkObj) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIProductToProductLinkValidation.validateGPOptionStatusAndRemoveProductToProductLink(SeasonGroupToProductLink seasonGroupToProductLinkObj) ###");
		LCSProduct sellingProductObj = (LCSProduct) VersionHelper.latestIterationOf(seasonGroupToProductLinkObj.getProductMaster());
		sellingProductObj = SeasonProductLocator.getProductARev(sellingProductObj);
		LCSPartMaster sellingProductMasterObj = (LCSPartMaster) sellingProductObj.getMaster();
		ProductToProductLink productToProductLinkObj = null;
		
		//Calling a function to get all linked products for the given child product (in this case Selling Product is a child Product and trying to fetch all the linked Garment Products)
		Collection<FlexObject> linkedObjectsCollection = LCSProductQuery.getLinkedProducts(sellingProductMasterObj, false, true);
		
		//Validating the given linked Object collection and iterating to get each linked object, validating the relationshipType, which are using as criteria to deLink the existing object
		if(linkedObjectsCollection != null && linkedObjectsCollection.size() > 0)
		{
			for(FlexObject flexObj : linkedObjectsCollection)
			{
				if(garmentAndSellingRelationshipType.equalsIgnoreCase(flexObj.getString("PRODUCTTOPRODUCTLINK.LINKTYPE")))
				{
					productToProductLinkObj = (ProductToProductLink) LCSQuery.findObjectById("OR:com.lcs.wc.product.ProductToProductLink:"+flexObj.getString("PRODUCTTOPRODUCTLINK.IDA2A2"));
					new LCSProductLogic().deleteProductToProductLink(productToProductLinkObj, true, false);
				}
			}
		}
				
		// LCSLog.debug("### END HBIProductToProductLinkValidation.validateGPOptionStatusAndRemoveProductToProductLink(SeasonGroupToProductLink seasonGroupToProductLinkObj) ###");
	}
	
	/**
	 * This function is using to format the given string(looks like hbiGroupProductLinkGPRef:hbiIsItSelectedGPOption1,hbiGarmentProdRefOption2:hbiIsItSelectedGPOption2, etc) into the Map
	 * @param propertiesEntry - String
	 * @return flexObjectDataMap - Map<String, String>
	 * @throws WTException
	 */
	public Map<String, String> getAttributeKeysMapfromPropertiesEntry(String propertiesEntry) throws WTException
	{
		// LCSLog.debug("### START HBIProductToProductLinkValidation.getAttributeKeysMapfromPropertiesEntry(propertiesEntry) ###");
		Map<String, String> attributeKeysMap = new HashMap<String, String>();
		String keyValueDataPairs[] = propertiesEntry.split(",");
		String garmentProductObjRefKey = "";
		String garmentProductSelectionStatusKey = "";
		
		if(keyValueDataPairs != null && keyValueDataPairs.length > 1)
		{
			//Iterating on each Key-Value entry(which is separated using : as delimiter), initialize attribute-key data and preparing Map object with the existing Key-value set
			for(String attributeKeys : keyValueDataPairs)
			{
				garmentProductObjRefKey = attributeKeys.split(":")[0];
				garmentProductSelectionStatusKey = attributeKeys.split(":")[1];
				attributeKeysMap.put(garmentProductSelectionStatusKey, garmentProductObjRefKey);
			}
		}
		
		// LCSLog.debug("### END HBIProductToProductLinkValidation.getAttributeKeysMapfromPropertiesEntry(propertiesEntry) ###");
		return attributeKeysMap;
	}
}