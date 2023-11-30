package com.hbi.wc.moa;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.util.LCSProperties;
import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import com.lcs.wc.flextype.*;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype. AttributeValueList;
import org.apache.commons.lang.StringUtils;



/******************************************************************************
 * GreigeFabricMOA.java
 *
 * Server Side Plugin for Calculating Cut field on Greige Fabric MOA
 *
 * @author  Anoop Sasikumar
 * Created on 
 *
 * Revision History    Author Name     Date           Change Description
 *    1.0              Anoop Sasikumar  
 *
 *
 *********************************************************************************************/
public class GreigeFabricMOA {
	////////////////////////////////////////////////////////////////////////////
	public static final boolean DEBUG;


	////////////////////////////////////////////////////////////////////////////

	static {
	DEBUG = LCSProperties.getBoolean("com.hbi.wc.moa.verbose");
	}


	/**********************************************************************************************************************************************
	*********************************  CODE BEGINS *********************************************************************************************
	***********************************************************************************************************************************************/

	public static void calcCutField(WTObject object) throws LCSException, WTException,WTPropertyVetoException{
		
		             
                          
		LCSMOAObject moaObject=(LCSMOAObject)object;
		if(moaObject != null)
		{
            //get cylinder values from moa object
			
		   	String strCylinderVal="";
			AttributeValueList  AttList = moaObject.getFlexType().getAttribute("hbiCylinder").getAttValueList();
			
			if(moaObject != null)
			{
				strCylinderVal = AttList.getValue((String)moaObject.getValue("hbiCylinder"), null);
				System.out.println("\t Value= "+strCylinderVal);
				
				if (StringUtils.isNotBlank(strCylinderVal))
				{
				int intCylinderVal = Integer.parseInt(strCylinderVal);
				moaObject.setValue("hbiCylinderInt",intCylinderVal);
				}
			}
			
		}
		

	 } 
}
 
