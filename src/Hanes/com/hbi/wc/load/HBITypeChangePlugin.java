package com.hbi.wc.load;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.hbi.wc.color.HBIColorTypeChangeUtility;
import com.hbi.wc.material.HBIMaterialTypeChangeUtility;
import com.lcs.wc.foundation.LCSLifecycleManaged;

import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * HBITypeChangePlugins.java
 * 
 * This class has plug-in function to call the internal functions for type changes (material / material-supplier type change and color type change)
 * @author Abdul.Patel@Hanes.com
 * @since February-20-2023
 */
public class HBITypeChangePlugin
{
	private static Logger log = LogManager.getLogger(HBITypeChangePlugin.class);
	
	/**
	 * Plug-in function registered on LCSLifecycleManaged POST_UPDATE persist to validate the Business-Object status and invoke type change methods
	 * @param wtObj	- WTObject
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws IOException
	 */
	public static void invokeTypeChange(WTObject wtObj) throws WTException, WTPropertyVetoException, IOException
	{
		log.info("### HBITypeChangePlugin.invokeTypeChange(WTObject wtObj) START ###");
		
		if(!(wtObj instanceof LCSLifecycleManaged)) {
			return;
		}
		LCSLifecycleManaged businessObject = (LCSLifecycleManaged) wtObj;
		
		String boTypePath = businessObject.getFlexType().getFullName(true);
		if(!("Business Object\\PLM Job Administration".equalsIgnoreCase(boTypePath))) {
			return;
		}
		
		String businessObjectName = businessObject.getName();
		if("RunMaterialTypeChangeUtility".equalsIgnoreCase(businessObjectName)) {
			HBIMaterialTypeChangeUtility.validateAndUpdateMaterialAndMaterialSupplierType(HBIMaterialTypeChangeUtility.sourceDataFileName, HBIMaterialTypeChangeUtility.targetDataFileName);
		}
		else if("RunColorTypeChangeUtility".equalsIgnoreCase(businessObjectName)) {
			HBIColorTypeChangeUtility.validateAndUpdateColorType(HBIColorTypeChangeUtility.sourceDataFileName, HBIColorTypeChangeUtility.targetDataFileName);
		}
		
		log.info("### HBITypeChangePlugin.invokeTypeChange(WTObject wtObj) END ###");
	}
}
