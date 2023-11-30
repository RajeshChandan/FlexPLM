package com.hbi.wc.interfaces.inbound.aps.bom.query;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductQuery;
import com.lcs.wc.util.LCSProperties;

/**
 * HBIGarmentProductQuery.java
 * 
 * This class contains specific and generic functions, which are using to query garment product object using a garment product name, query linked season from a garment (with the assumption
 * that each garment product is linked with a 'A' season (Placeholder season)), using product object and season object as referencing parameter to create and update BOMPart and FlexBOMLink.
 * @author Abdul.Patel@Hanes.com
 * @since November-22-2017
 */
public class HBIGarmentProductQuery
{
	private static String garmentProductTypePath = LCSProperties.get("com.hbi.wc.interfaces.inbound.aps.bom.query.HBIGarmentProductQuery.garmentProductTypePath", "Product\\BASIC CUT & SEW - GARMENT");
	
	/**
	 * This function is internally calling an out of the box function to get product object for the given 'Product Name' and 'Product Type Path' and returning object to the calling function
	 * @param garmentProductName - String
	 * @return productObj - LCSProduct
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public LCSProduct getGarmentProductByName(String garmentProductName) throws WTException, WTPropertyVetoException
	{
		// LCSLog.debug("### START HBIGarmentProductQuery.getGarmentProductByName(String garmentProductName) ###");
		FlexType productFlexTypeObj = FlexTypeCache.getFlexTypeFromPath(garmentProductTypePath);
		
		//Calling out of the box function to return Product object for the given 'Product Name' and 'Product Type Path', returning product object from function header to a calling function
		garmentProductName = garmentProductName.trim();
		LCSProduct productObj = new LCSProductQuery().findProductByNameType(garmentProductName, productFlexTypeObj);
		
		// LCSLog.debug("### END HBIGarmentProductQuery.getGarmentProductByName(String garmentProductName) ###");
		return productObj;
	}
}