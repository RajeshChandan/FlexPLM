///**
// * CopyProductAttributesToCostSheet.java
// * @author Prabhaker
// * @version
// * Created on April 23, 2013, 11:00 AM
// */
//
//package com.vrd.costsheet;
//
//import java.util.Collection;
//
//import com.lcs.wc.flextype.FlexType;
//import com.lcs.wc.product.LCSProduct;
//import com.lcs.wc.season.LCSSeason;
//import com.lcs.wc.season.LCSProductSeasonLink;
//import com.lcs.wc.season.LCSSeasonProductLink;
//import com.lcs.wc.season.LCSSeasonQuery;
//import com.lcs.wc.sourcing.LCSProductCostSheet;
//import com.lcs.wc.util.FormatHelper;
//import com.lcs.wc.util.LCSLog;
//import com.lcs.wc.util.LCSProperties;
//import com.lcs.wc.util.VersionHelper;
//import com.vrd.util.LCSPropertiesPluginHelper;
//
//import wt.fc.WTObject;
//import wt.util.WTException;
//import wt.util.WTPropertyVetoException;
//
///** Copies the Product-Season attributes to cost sheet.
// * @author Prabhaker
// *
// */
//public class CopyProductAttributesToCostSheet
//{
//	/** Constructor.
//	 *
//	 */
//	protected CopyProductAttributesToCostSheet() {}
//	/** Print the Debugging statements.
//	 *
//	 */
//	private static final String RollUp = LCSProperties.get("com.vrd.costsheet.CopyProductAttributesToCostSheet.RollUp", "vrdCopyTargets");
//
//
//	/** Copies the product-season attributes to cost sheet.
//	 * @param wtObject
//	 * @return
//	 * @throws WTException
//	 * @throws WTPropertyVetoException
//	 */
//
//	public static final WTObject copyProdAttrToCostSheet(WTObject wtObject)
//	throws WTException
//	{
//
//		LCSLog.debug("VRD>>>>>> CopyProductAttributesToCostSheet.copyProdAttrToCostSheet: START " + wtObject);
//		LCSProductCostSheet cs = null;
//		if (wtObject instanceof LCSProductCostSheet){	
//			cs = (LCSProductCostSheet)wtObject;
//		}
//		else {	
//			throw new WTException("VRD>>>>>> CopyProductAttributesToCostSheet.copyProdAttrToCostSheet: Object is not instance of LCSProductCostSheet");
//		}
//		
//		FlexType cType = cs.getFlexType();
//		boolean doRollup = false;
//		if(cType.getAttributeKeyList().contains(RollUp.toUpperCase())){
//			if(cs.getValue(RollUp) != null) {
//				doRollup = ((Boolean)cs.getValue(RollUp)).booleanValue();
//			}
//		}
//
//		try{
//			cs.setValue(RollUp,false);
//		}
//		catch(Exception e){
//			LCSLog.stackTrace(e);
//			LCSLog.error(">>>>>> >>>>>> Cannot set roll up to false ");
//			return cs;
//		}
//		
//		if (!doRollup)	{
//			LCSLog.debug("VRD>>>>>> CopyProductAttributesToCostSheet.copyProdAttrToCostSheet: FINISH -- doRollup is FALSE");
//			LCSLog.error("VRD>>>>>> CopyProductAttributesToCostSheet.copyProdAttrToCostSheet: Copy Product Attributes is FALSE ");
//			return cs;
//		}
//				
//		LCSLog.debug("VRD>>>>>> CopyProductAttributesToCostSheet.copyProdAttrToCostSheet: "
//				+ " -- ProductARevId: "+ cs.getProductARevId() +" -- Product Master:"+cs.getProductMaster()
//				+ " -- SeasonRevId: "+ cs.getSeasonRevId() +" -- Season Master:"+cs.getSeasonMaster() );		
//		//product = (LCSProduct)LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSProduct:" + FormatHelper.format(costSheet.getProductARevId()));
//		LCSProduct prod = null;
//		LCSSeason ssn = null;
//		LCSProductSeasonLink ssnProdLink = null;
//		
//		try	 {
//			prod = (LCSProduct) VersionHelper.getVersion(cs.getProductMaster(), "A");		
//			ssn = (LCSSeason) VersionHelper.latestIterationOf(cs.getSeasonMaster());
//			ssnProdLink = ssn == null? null : (LCSProductSeasonLink) LCSSeasonQuery.findSeasonProductLink(prod, ssn);	
//		}
//		catch (Exception e){
//			LCSLog.debug("VRD>>>>>> CopyProductAttributesToCostSheet.copyProdAttrToCostSheet: FINISH -- Exception trying to get SeasonProductLink");
//			LCSLog.error("VRD>>>>>> CopyProductAttributesToCostSheet.copyProdAttrToCostSheet: Exception trying to get SeasonProductLink");
//			return cs;
//		}
//		
//		if (ssnProdLink == null){
//			LCSLog.debug("VRD>>>>>> CopyProductAttributesToCostSheet.copyProdAttrToCostSheet: FINISH -- SeasonProductLink is NULL");
//			LCSLog.error("VRD>>>>>> CopyProductAttributesToCostSheet.copyProdAttrToCostSheet: SeasonProductLink is NULL ");
//			return cs;
//		}
//		
//				
//		try
//		{
//			Collection<String> attLists = LCSPropertiesPluginHelper.getAllPropertySettings("com.vrd.costsheet.CopyProductAttributesToCostSheet");
//			String csAttKey = null;
//			String prodAttKey = null;
//			
//			for (String att: attLists)	{
//				csAttKey = att.substring(att.lastIndexOf(',')+1, att.length());					
//				prodAttKey = att.substring(0,att.indexOf(','));					
//				
//				if (FormatHelper.hasContent(prodAttKey))	{
//					cs.setValue(csAttKey, ssnProdLink.getValue(prodAttKey));
//				}				
//			}			
//		}
//
//		catch(Exception e)
//		{
//			LCSLog.stackTrace(e);
//			LCSLog.error("VRD>>>>>> CopyProductAttributesToCostSheet.copyProdAttrToCostSheet: Error in copying product attributes to cost sheet ");
//		}	
//		
//
//		LCSLog.debug("VRD>>>>>> CopyProductAttributesToCostSheet.copyProdAttrToCostSheet: FINISH ");
//		return cs;	
//	}
//}