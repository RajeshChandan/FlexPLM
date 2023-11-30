package com.hbi.wc.moa;
import com.lcs.wc.util.*;
import java.util.*;
import java.text.*;
import com.lcs.wc.moa.*;
import com.lcs.wc.foundation.*;
import com.lcs.wc.material.*;
import wt.fc.*;
import wt.util.*;



/******************************************************************************
 * PurchasingMOA.java
 *
 * Server Side Plugin for Purchasing Materials MOA - Setting Vendor Master Code
 *
 * @author  UST
 * Created on 
 *
 * Revision History    Author Name     Date           Change Description
 *    1.0                  UST 
 *
 *
 *********************************************************************************************/
public class PurchasingMOA {
	////////////////////////////////////////////////////////////////////////////
	public static final boolean DEBUG;


	////////////////////////////////////////////////////////////////////////////

	static {
	DEBUG = LCSProperties.getBoolean("com.hbi.wc.moa.verbose");
	}


	/**********************************************************************************************************************************************
	*********************************  CODE BEGINS *********************************************************************************************
	***********************************************************************************************************************************************/

	public static void setVendorMasterCode(WTObject object) throws LCSException, WTException,WTPropertyVetoException{
		
		LCSMOAObject moaObject=(LCSMOAObject)object;
		if(moaObject != null)
		{
			//Get vendor location from the MOA
			LCSLifecycleManaged vendorLocObj=(LCSLifecycleManaged)moaObject.getValue("hbiVendorLocation");
			if(vendorLocObj != null)
			{
				//setting values on moa object from the Business Object\Vendor Location - Vendor Master code
				moaObject.setValue("hbiVendorMasterCode", (String)vendorLocObj.getValue("hbiVendorMasterCd"));
			}
						
			if(!((Double)moaObject.getValue("hbiTieredPrice") > 0 ))
			{
			System.out.println("Tiered Price is not populated ");
			//Get the owner object from MOA and get the materialPrice attribute
			LCSMaterialSupplierMaster matSupObj = (LCSMaterialSupplierMaster)moaObject.getOwner();
			LCSMaterialSupplier matSup = (LCSMaterialSupplier) VersionHelper.latestIterationOf(matSupObj);
			//Double hbiPrice = (Double)matSup.getNum1();
			Double hbiPrice = (Double)matSup.getValue("materialPrice");
			System.out.println("materialPrice" +hbiPrice);
			
				if(hbiPrice > 0)
				{
				System.out.println("Material Price > 0 ");
				moaObject.setValue("hbiTieredPrice",hbiPrice);
				}
			
			}
			
		}

	}	
 }
