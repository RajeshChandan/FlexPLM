package com.hbi.wc.moa;

import java.util.Collection;
import java.util.Iterator;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductLogic;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;
//import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author UST, Feb 2020 go live
 * To clear certain Selling Product MOA values while copying product.
 * Set plant sync status to false while copying product
 */
public class HBIClearMOA {
	private static String hbiPutUpCode = LCSProperties.get("com.hbi.wc.product.hbiPutUpCode", "hbiPutUpCode");
	private static String hbiReferenceSpecification = LCSProperties.get("com.hbi.wc.moa.hbiReferenceSpecification", "hbiReferenceSpecification");
	private static String hbiMaterialNumber = LCSProperties.get("com.hbi.wc.moa.hbiMaterialNumber", "hbiMaterialNumber");
	private static String hbiErpPlantExtensions = LCSProperties.get("com.hbi.wc.moa.hbiErpPlantExtensions", "hbiErpPlantExtensions");
	private static String hbiSynchedStatus = LCSProperties.get("com.hbi.wc.moa.hbiSynchedStatus", "hbiSynchedStatus");

	/**
	 * @param WtObj -LCSProduct
	 */
	public static void clearMOA(WTObject WtObj) {
		LCSProduct productObj = (LCSProduct) WtObj;
		try {
		
		boolean clear =false;

			if(productObj!=null && productObj.getCopiedFrom()!=null )
			{
				clear=true;
				
				
			}
			else {
				System.out.println("---------------not the copied product----------------------- hence not clearing MOA");

				
			}
			
			productObj = (LCSProduct) VersionHelper.getVersion(productObj, "A");
			

			if (productObj != null && clear) {
				
				//Clear out material number and ref spec from putcode MOA while copying product
				//Do not delete Put Up Code rows
				//Code changes by Wipro Upgrade Team
				SearchResults moaPUC_SR = LCSMOAObjectQuery.findMOACollectionData(productObj.getMaster(),  
						productObj.getFlexType().getAttribute(hbiPutUpCode), "LCSMOAObject.createStampA2", true);
				if(moaPUC_SR != null && moaPUC_SR.getResultsFound() > 0){
		        	Collection<FlexObject> moaPUC_Collection = moaPUC_SR.getResults();
		        	Iterator moaItr = moaPUC_Collection.iterator();
		        	while(moaItr.hasNext()){
		        		FlexObject moaPUC_FO = (FlexObject) moaItr.next();
		    			String moaPUC_IDA2A2 = moaPUC_FO.getString("LCSMOAOBJECT.IDA2A2");
		    			LCSMOAObject moaPUC_Obj = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+moaPUC_IDA2A2);
		    			
		    			moaPUC_Obj.setValue(hbiMaterialNumber, "");
		    			moaPUC_Obj.setValue(hbiReferenceSpecification, "");
		    			LCSLogic.persist(moaPUC_Obj,true);
		        	}
				}
				
				//Set plant sync status to false while copying product
				//Code changes by Wipro Upgrade Team
				SearchResults moaPlant_SR = LCSMOAObjectQuery.findMOACollectionData(productObj.getMaster(),  
						productObj.getFlexType().getAttribute(hbiErpPlantExtensions), "LCSMOAObject.createStampA2", true);
				if(moaPlant_SR != null && moaPlant_SR.getResultsFound() > 0){
		        	Collection<FlexObject> moaPlant_Collection = moaPlant_SR.getResults();
		        	
		        	Iterator moaPlantItr = moaPlant_Collection.iterator();
		        	while(moaPlantItr.hasNext()){
		        		FlexObject moaPlant_FO = (FlexObject) moaPlantItr.next();
		    			String moaPlant_IDA2A2 = moaPlant_FO.getString("LCSMOAOBJECT.IDA2A2");
		    			LCSMOAObject moaPlant_Obj = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+moaPlant_IDA2A2);
		    			moaPlant_Obj.setValue(hbiSynchedStatus, "false");
		    			LCSLogic.persist(moaPlant_Obj,true);
		        	}
				}

				//Clear Integration Log MOATable while copying product
				LCSMOAObjectLogic logic = new LCSMOAObjectLogic();
				FlexTypeAttribute fta = productObj.getFlexType().getAttribute("hbiIntegratinLogs");
				LCSMOATable moaTable = LCSMOATable.getLCSMOATable(productObj, fta);
				Collection rows = moaTable.getRows();
				Iterator rowsItr = rows.iterator();
				while (rowsItr.hasNext()) {
					FlexObject fObj = new FlexObject();
					fObj = (FlexObject) rowsItr.next();
					// Delete all rows
					moaTable.dropRow(fObj.getData("OID"));
				}
				logic.updateMOAObjectCollection(productObj, fta, moaTable.getMOAString());
				LCSProductLogic.persist(productObj, true);
			}
		} catch (WTException e) {
			e.printStackTrace();
		} /*catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}*/
	}
}
