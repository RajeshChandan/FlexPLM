package com.hbi.wc.moa;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author UST
 * Plugin is for Add Component Description to Dye Formula BOM (MOA) user story - 101289
 *
 */
public class HBILNMOAPlugin {
	private static String DYE_FORMULA_COMP = LCSProperties.get("com.hbi.wc.moa.hbiDyeFormulaComponent", "hbiDyeFormulaComponent");
	private static String ITEM_DESC = LCSProperties.get("com.hbi.wc.material.hbiItemDescription", "hbiItemDescription");
	private static String COMP_DESC = LCSProperties.get("com.hbi.wc.moa.hbiCompDesc", "hbiCompDesc");

	
	/**
	 * @param obj
	 */
	public static void setComponentDescOnMOA(WTObject obj) 
	{
		LCSLog.debug("PLUGIN TRIGGERED for 	Dye Formula Components only");
		if (obj instanceof LCSMOAObject)
		{
			LCSMOAObject moa=(LCSMOAObject)obj;
			try {
				if(moa.getValue(DYE_FORMULA_COMP)!=null) {
				LCSMaterial component=(LCSMaterial)moa.getValue(DYE_FORMULA_COMP);
				String compDesc=(String)component.getValue(ITEM_DESC);
				moa.setValue(COMP_DESC, compDesc);
				}
			} catch (WTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*catch (WTPropertyVetoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
		}
		
	}
	
}
