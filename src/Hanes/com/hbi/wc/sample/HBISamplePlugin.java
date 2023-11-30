package com.hbi.wc.sample;

import org.apache.log4j.Logger;

import com.lcs.wc.sample.LCSSample;

import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.util.*;


public class HBISamplePlugin {

 
 //public static final boolean DEBUG = LCSProperties.getBoolean("com.hbi.wc.sample.HBISamplePlugin.verbose");
 private static final Logger logger = LogR.getLogger("com.hbi.wc.sample.HBISamplePlugin");   
	public static void updateSampleRequestName(WTObject obj )throws WTException
    {
      	//Wipro Team Upgrade Added Loggers
		logger .debug((new StringBuilder()).append("HBISamplePlugin.updateSampleRequestName: ").append(obj).toString());

        if(!(obj instanceof LCSSample))
            throw new WTException("HBISamplePlugin.updateSampleRequestName: object must be a LCSSample instance");
       
		LCSSample sample = (LCSSample)obj;

     
        logger .debug(sample);
        
		String strSampleType = sample.getFlexType().getFullName(true);
      
        if(strSampleType.contains("Sample\\Material"))
        {
		
			   logger .debug("The sample object is of material type");
        
            HBISampleHelper.updateSampleRequestName(sample);
		}
		
	  }
}