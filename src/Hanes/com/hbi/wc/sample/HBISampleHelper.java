package com.hbi.wc.sample;

import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.sample.LCSSampleRequest;
import com.lcs.wc.sample.LCSSampleRequestClientModel;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.flextype. AttributeValueList;

import wt.log4j.LogR;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierMaster;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.VersionHelper;
import java.lang.*;

import org.apache.log4j.Logger;

import wt.util.*;
import java.util.Calendar;

public class HBISampleHelper 
{

 //public static final boolean DEBUG = LCSProperties.getBoolean("com.hbi.wc.sample.HBISamplePlugin.verbose");
 private static final Logger logger = LogR.getLogger("com.hbi.wc.sample.HBISampleHelper");   
 public static void updateSampleRequestName(LCSSample sample)throws WTException
 {              
		//Wipro Team Upgrade Added Loggers
	String strColorName = "";	
    String strMaterialName = "";
	String strReqDesc = "";
	String strSamSeq = "";
	String supplierName ="";
	int year = Calendar.getInstance().get(Calendar.YEAR);

	LCSMaterialColor lcsColor = (LCSMaterialColor)sample.getColor();

	if(lcsColor != null)
	{
      
    	   logger .debug("Retrieving the associated color and material");

	   strColorName = lcsColor.getColor().getColorName();
	 //Wipro Team Upgrade
	   //WTPartMaster materialMaster = (WTPartMaster) lcsColor.getMaterialMaster();
	   LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf (lcsColor.getMaterialMaster());
	   strMaterialName = (String) material.getName();
	   // addede for CA # 242192-16 : Change for appending supplier name to the Sample request name.
	   LCSMaterialSupplierMaster materialSupplierMaster = (LCSMaterialSupplierMaster) lcsColor.getMaterialSupplierMaster();
	   LCSMaterialSupplier materialSupplier = (LCSMaterialSupplier)VersionHelper.latestIterationOf (materialSupplierMaster);
	   LCSSupplier supObj = (LCSSupplier) VersionHelper.latestIterationOf(materialSupplier.getSupplierMaster());
	   supplierName= (String) supObj.getName();
	   //ended
	   if(strColorName == null)
		strColorName = "";

	  
	   LCSSampleRequest sampleReq = sample.getSampleRequest();
	   if(sampleReq != null)	{
	   strReqDesc = (String)sampleReq.getValue("hbiMaterialColorType");
	   AttributeValueList  sampleAttList = sampleReq.getFlexType().getAttribute("hbiMaterialColorType").getAttValueList();
				 strReqDesc = sampleAttList.getValue((String)sampleReq.getValue("hbiMaterialColorType"), null);
				 //strMaterialName = (String)sampleReq.getValue("hbiMaterialDescription");
				// Double dblSamSeq = (Double) sampleReq.getValue("hbiSampleSeq");
				//Wipro Team Upgrade
				 Long dblSamSeq = (Long) sampleReq.getValue("hbiSampleSeq");
				 int intSamSeq = dblSamSeq.intValue();
				 strSamSeq = Integer.toString(intSamSeq);
				 }
	   if(strMaterialName == null)
		strMaterialName = "";
		// addede for CA # 242192-16 : Change for appending supplier name to the Sample request name.
	   if(supplierName == null)
		supplierName = "";
		//ended
	   logger .debug("Retrieving the request description");	
			
	   if(strSamSeq == null)
		strSamSeq = "";

	   logger .debug("Retrieving the 7 digit Sample Sequence number");				


	   if(strReqDesc == null)
	    strReqDesc = "";

		
	   logger .debug("Concatenating the values for material, color and request description");	

		StringBuilder sb = new StringBuilder();
		sb.append(year)
		  .append(" - ")
		  .append(strSamSeq)
		  .append(" - ")		  
		  .append(strMaterialName)
		  .append(" - ")
		  .append(strColorName)	
		  .append(" - ")
		  .append(strReqDesc)
		  .append(" - ")		  
		  .append(supplierName);//addede for CA # 242192-16 : Change for appending supplier name to the Sample request name.
	
		LCSSampleRequestClientModel requestModel = new LCSSampleRequestClientModel();
					
		try
		{
			logger .debug("updating the request name value for the sample request");
			
			requestModel.load(sampleReq.toString());  
			requestModel.setValue("requestName",sb.toString().trim());
		    requestModel.save();			
		}
		catch(WTPropertyVetoException wtpve)
		{
			throw new WTException(wtpve);
			
		}	  	 		
     }
  }
}