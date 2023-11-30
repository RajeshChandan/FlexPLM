package com.hbi.wc.specification;

import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.util.FormatHelper;

import wt.fc.ObjectReference;
import wt.fc.WTObject;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.specification.SpecToSpecLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.specification.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.SearchResults;
import wt.util.*;
import com.lcs.wc.util.*;
import com.lcs.wc.sourcing.*;
import wt.part.*;
import com.lcs.wc.sourcing.LCSSourcingConfigLogic;
import com.lcs.wc.supplier.LCSSupplier;
import java.util.*;
import com.lcs.wc.db.*;
import com.lcs.wc.product.*;
import com.lcs.wc.season.SeasonProductLocator;
import wt.util.WTException;
//import wt.part.WTPartMaster;
import wt.fc.PersistenceHelper;

import org.apache.log4j.Logger;
import   wt.log4j.LogR;


public class HBIPatternSpecPlugin 
{
	/**
	 * This function will invoke from custom plug-in entry registered on SpecToSpecLink for update of hbiPatternSpec attribute on Souring Config
	 * @param wtObj - WTObject
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static final String GARMENT_PRODUCT = "BASIC CUT & SEW - GARMENT";
	public static final String PATTERN_PRODUCT = "BASIC CUT & SEW - PATTERN";
	private static final Logger logger = LogR.getLogger("com.hbi.wc.specification.HBIPatternSpecPlugin");
	
	public static void setPatternSpec(WTObject wtObj) throws WTException, WTPropertyVetoException
	{
		String DELIM="-";
		SpecToSpecLink specLink = (SpecToSpecLink)wtObj;	
		logger.debug("ParentSpec = "+specLink.getParentSpec());
		FlexSpecMaster specMaster = (FlexSpecMaster)specLink.getParentSpec();
		FlexSpecMaster specChild = (FlexSpecMaster)specLink.getChildSpec();
		FlexSpecification childSpec = (FlexSpecification)VersionHelper.latestIterationOf(specChild);
		LCSProduct product = SeasonProductLocator.getProductARev(childSpec.getSpecOwner());
		if(product.getFlexType().getFullName().indexOf(GARMENT_PRODUCT) != -1)
		{
			
			FlexSpecification parentSpec = (FlexSpecification)VersionHelper.latestIterationOf(specMaster);
			System.out.println("ParentSpec name = "+parentSpec.getName());
			//String prodName = (String)SeasonProductLocator.getProductARev(parentSpec.getSpecOwner()).getValue("productName");
			//String patternNo = (String)SeasonProductLocator.getProductARev(parentSpec.getSpecOwner()).getValue("hbiPatternNo");
			
			//Wipro Team Upgrade
			
			   LCSProduct parentProduct=(LCSProduct) SeasonProductLocator.getProductARev((LCSPartMaster)parentSpec.getSpecOwner());
				
				if(product.getFlexType().getFullName().indexOf(PATTERN_PRODUCT ) != -1){
					
					String patternNo = (String)parentProduct.getValue("hbiPatternNo");
			System.out.println(" !!!!! patternNo !!!!!!!" +patternNo);
			String sourceName = (String)((LCSSourcingConfig)VersionHelper.latestIterationOf((LCSSourcingConfigMaster)parentSpec.getSpecSource())).getValue("name");
			System.out.println(" !!!!! sourceName !!!!!!!" +sourceName);
			String displayName = patternNo + " " + DELIM + " " + sourceName + " " + DELIM + " " + parentSpec.getName();
			
			WTObject obj = (WTObject)childSpec.getSpecSource();				
			LCSSourcingConfigMaster srcConfigMaster = (LCSSourcingConfigMaster)obj;
			LCSSourcingConfig srcConfig = (LCSSourcingConfig)VersionHelper.latestIterationOf(srcConfigMaster); 
			LCSSourcingConfigQuery scQuery = new LCSSourcingConfigQuery();
			LCSSourcingConfigLogic scToSl = new  LCSSourcingConfigLogic();			
				
			if(srcConfig != null)
			{
				srcConfig.setValue("hbiSpecificationPattern",displayName);	
				scToSl.save(srcConfig);
				
				//Collection<LCSSourceToSeasonLink> scToslList  = scQuery.getSourceToSeasonLinks(srcConfig);
				
				/*if(scToslList.size() > 0 || scToslList != null)
				{
					
					for(LCSSourceToSeasonLink scToslObj: scToslList)
					{
						if(scToslObj != null)
						{
							
							LCSLog.debug("displayName is = "+displayName);
							scToslObj.setValue("hbiSpecificationPattern",displayName);	
							scToSl.save(scToslObj);
											
							
						}
					}
				}	*/
			}}
		}
			
	}

	public static void setSourcingConfigName(WTObject wtObj) throws WTException, WTPropertyVetoException
	{
		FlexSpecification flexSpec = (FlexSpecification)wtObj;	
		System.out.println("!!!!!!!! FlexSpecification !!!!!!!!");
		
		LCSProduct product = (LCSProduct)VersionHelper.getVersion(flexSpec.getSpecOwner(), "A");
		System.out.println("!!!!!!!! product !!!!!!!!"+product.getIdentity());
		
		LCSSourcingConfigMaster sourcingConfigMaster = (LCSSourcingConfigMaster) flexSpec.getSpecSource();
		LCSSourcingConfig sourcingConfig = (LCSSourcingConfig) VersionHelper.latestIterationOf(sourcingConfigMaster);
		System.out.println("!!!!!!!! sourcingConfig !!!!!!!!"+sourcingConfig.getSourcingConfigName());
		String sourceNumber = (String)sourcingConfig.getValue("number");
		String productType = product.getFlexType().getFullName(true);
		if("Product\\BASIC CUT & SEW - GARMENT".equalsIgnoreCase(productType))
		{
			String patternSpec = (String)sourcingConfig.getValue("hbiSpecificationPattern");
			System.out.println("!!!!!!!!  patternSpec !!!!!!!!!" +patternSpec);
			
			LCSSupplier finishedSupplier = (LCSSupplier)sourcingConfig.getValue("hbiFinishGoodSupplier");
			
			
			if(finishedSupplier != null)
			{
				String supplierName = (String)finishedSupplier.getValue("name");
				System.out.println("!!!!!! supplierName  changed!!!!"+supplierName);
				System.out.println("!!!!!! supplierName  Not Null!!!!"+supplierName);
				if(!FormatHelper.hasContent(patternSpec))
				{
					System.out.println("Inside not null condition");
					sourcingConfig.setValue("name",supplierName);	
					sourcingConfig.setSourcingConfigName(supplierName);
					//LCSLogic.persist(sourcingConfig,true);
					new LCSSourcingConfigLogic().save(sourcingConfig);
					System.out.println("!!!!!! source  changed!!!!"+sourcingConfig.getSourcingConfigName());
				}
			}
			
		}	
	}
}	