package com.hbi.wc.color;

import java.util.Map;
import java.util.HashMap;
import java.util.Vector;
import java.util.Collection;
import java.util.Locale;

import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.FormatHelper;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.color.LCSColorLogic;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectQuery;

import com.lcs.wc.db.FlexObject;

import com.hbi.wc.util.HBITableDataSorter;

import wt.fc.WTObject;

import wt.util.WTException;
import com.lcs.wc.util.LCSException;
import wt.util.WTPropertyVetoException;
import wt.method.MethodContext;

public class HBIColorNameDerivationPlugin
{
	private static String complexColorObjType = LCSProperties.get("com.hbi.wc.color.HBIColorNameDerivationPlugin.complexColorObjType", "Color\\Complex");
	private static String dunkStripeColorObjType = LCSProperties.get("com.hbi.wc.color.HBIColorNameDerivationPlugin.dunkStripeColorObjType", "Color\\Dunk Stripe");
	private static String heatherColorObjType = LCSProperties.get("com.hbi.wc.color.HBIColorNameDerivationPlugin.heatherColorObjType", "Color\\Heather");
	private static String  yarnDyedKnitColorObjType = LCSProperties.get("com.hbi.wc.color.HBIColorNameDerivationPlugin.yarnDyedKnitColorObjType", "Color\\Yarn Dyed Knit Stripes");
	private static String  yarnDyedWovenColorObjType = LCSProperties.get("com.hbi.wc.color.HBIColorNameDerivationPlugin.yarnDyedWovenColorObjType", "Color\\Yarn Dye");
	private static String  solidColorObjType = LCSProperties.get("com.hbi.wc.color.HBIColorNameDerivationPlugin.solidColorObjType", "Color\\Solid");
	private static String nonDippedColorObjType = LCSProperties.get("com.hbi.wc.color.HBIColorNameDerivationPlugin.nonDippedColorObjType", "Color\\Non-Dipped");
	private static String garmentDyeColorObjType = LCSProperties.get("com.hbi.wc.color.HBIColorNameDerivationPlugin.garmentDyeColorObjType", "Color\\Garment Dye");
	
	public static void setColorName(WTObject wtObj) throws WTException, WTPropertyVetoException
	{
		if(wtObj instanceof LCSColor)
		{
			LCSColor colorObj = (LCSColor) wtObj;
			
			
			
			
			
			if(complexColorObjType.equalsIgnoreCase(colorObj.getFlexType().getFullName(true)) || heatherColorObjType.equalsIgnoreCase(colorObj.getFlexType().getFullName(true)) || dunkStripeColorObjType.equalsIgnoreCase(colorObj.getFlexType().getFullName(true)) || yarnDyedKnitColorObjType.equalsIgnoreCase(colorObj.getFlexType().getFullName(true)) || yarnDyedWovenColorObjType.equalsIgnoreCase(colorObj.getFlexType().getFullNameDisplay(true))||garmentDyeColorObjType.equalsIgnoreCase(colorObj.getFlexType().getFullNameDisplay(true)))
			{
				
				
				LCSMOATable pitchSheetMOATable = (LCSMOATable) colorObj.getValue("hbiPitchSheet");
				
				if(pitchSheetMOATable != null)
				{
					new HBIColorNameDerivationPlugin().setColorNameWithPitchSheet(colorObj,pitchSheetMOATable);
				}
				else
				{
					new HBIColorNameDerivationPlugin().setColorNameWithoutPitchSheet(colorObj);
				}	
			}
			
		}
	}

	
	public  void setColorNameWithoutPitchSheet(LCSColor colorObj)throws WTException, WTPropertyVetoException
	{
		//Code Upgrade by Wipro Team
		//Integer complexColObjSeq = ((Double) colorObj.getValue("hbiColorSequence")).intValue();
		Integer complexColObjSeq = ((Long) colorObj.getValue("hbiColorSequence")).intValue();
		String colorSeq = Integer.toString(complexColObjSeq);
	
		
		String colorRefereceType = (String)colorObj.getValue("hbiColorStandardType");
		colorRefereceType = colorObj.getFlexType().getAttribute("hbiColorStandardType").getAttValueList().getValue(colorRefereceType,Locale.getDefault());
		if(!FormatHelper.hasContent(colorRefereceType))
		{
			colorRefereceType = "";
		}
		
		String systemGeneratedNo = colorRefereceType+" "+colorSeq;
		
		if(complexColorObjType.equalsIgnoreCase(colorObj.getFlexType().getFullName(true)))
		{
			colorObj.setColorName(systemGeneratedNo);
		}
		else if(dunkStripeColorObjType.equalsIgnoreCase(colorObj.getFlexType().getFullName(true)) || yarnDyedKnitColorObjType.equalsIgnoreCase(colorObj.getFlexType().getFullName(true)) || yarnDyedWovenColorObjType.equalsIgnoreCase(colorObj.getFlexType().getFullNameDisplay(true)))
		{
			String artNo = (String)colorObj.getValue("hbiColorServiceRef");
			if(!FormatHelper.hasContent(artNo))
			{
				artNo = "";
			}
			String colorName = systemGeneratedNo+" "+"-"+" "+artNo;
			colorObj.setColorName(colorName);
		}
		else if(heatherColorObjType.equalsIgnoreCase(colorObj.getFlexType().getFullName(true)))
		{
			LCSLifecycleManaged heatherTechObj = (LCSLifecycleManaged)colorObj.getValue("hbiHeatherTechnique");
			if(heatherTechObj != null)
			{
				String heatherTechnique = (String)heatherTechObj.getName();
				String colorName = systemGeneratedNo+" "+"-"+" "+heatherTechnique;
				colorObj.setColorName(colorName);
			}
			else
			{
				colorObj.setColorName(systemGeneratedNo);
			}
		}
		
		
		
		//Wipro Changes Start for ticket 771670
		
		
		else if(garmentDyeColorObjType.equalsIgnoreCase(colorObj.getFlexType().getFullNameDisplay(true)) )
		{
			
			String colorName = systemGeneratedNo;
			colorObj.setColorName(colorName);
		}
		
		
		//Wipro Changes end for ticket 771670
	}
	
	public  void setColorNameWithPitchSheet(LCSColor colorObject,LCSMOATable pitchSheetMOATable)throws WTException, WTPropertyVetoException
	{
		String colorService="";
		String colorName ="";
		boolean flag = false;
		String key="";
		LCSMOAObject moaObject=null;
		LCSColor moaColorObj=null;
		String colorServiceNo="";
		String colorServiceName="";
		String colorObjName ="";
		String uniqueCheck ="";
		Vector sortList = new Vector();  
		sortList.add("SORTINGNUMBER");
		
		Vector numericSortList = new Vector();   
		numericSortList.add("SORTINGNUMBER");
		
		
		//Changes done by wipro upgrade team
		//Integer complexColObjSeq = ((Double) colorObject.getValue("hbiColorSequence")).intValue();
		Long complexColObjSeq = (Long) colorObject.getValue("hbiColorSequence");
		String colorSeq = Long.toString(complexColObjSeq);
	
		String colorRefereceType = (String)colorObject.getValue("hbiColorStandardType");
		colorRefereceType = colorObject.getFlexType().getAttribute("hbiColorStandardType").getAttValueList().getValue(colorRefereceType,Locale.getDefault());
		if(!FormatHelper.hasContent(colorRefereceType))
		{
			colorRefereceType = "";
		}
		
		String systemGeneratedNo = colorRefereceType+" "+colorSeq;

		new HBITableDataSorter().setNumericSort(numericSortList);
		Collection<FlexObject> rowIdColl =pitchSheetMOATable.getRows();
		rowIdColl = new HBITableDataSorter().sortTableDataObjects(rowIdColl, sortList);  
					
		System.out.println("   !!!!!!!  rowIdColl  !!!!!!!! " +rowIdColl.size());
		for(FlexObject flexObj : rowIdColl)
		{
			key = flexObj.getString("OID");
			moaObject = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+key);
			if(moaObject != null)
			{
				moaColorObj = (LCSColor)moaObject.getValue("color");
				if(moaColorObj != null)
				{
					colorServiceNo = (String)moaColorObj.getValue("hbiColorServiceRef");
					colorServiceName = (String)moaColorObj.getValue("hbiColorServiceName");
					colorObjName = (String)moaColorObj.getValue("name");
					if(solidColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullName(true)))
					{
						colorService = getColorNameForSolid(moaColorObj,colorServiceNo,colorServiceName);
						colorName=colorName.concat(colorService);
					}
					else if(heatherColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullName(true)) || dunkStripeColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullName(true))|| yarnDyedKnitColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullName(true)) || yarnDyedWovenColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullNameDisplay(true)) || complexColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullName(true)) || nonDippedColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullName(true)))
					{
						colorService = getColorNameForheatherColorObjType(moaColorObj,colorObjName);
						colorName=colorName.concat(colorService);
					}
					
					
					//Wipro Changes Start for ticket 771670
					
					else if(garmentDyeColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullNameDisplay(true))) 
					{
						colorService = getColorNameForGarmentDyeColorObjType(moaColorObj,colorObjName);
						colorName=colorName.concat(colorService);
					}
					
					
					//Wipro Changes End for ticket 771670
				}	
			}	
		}
		if(complexColorObjType.equalsIgnoreCase(colorObject.getFlexType().getFullName(true)))
		{
			
			if(colorName.contains("/"))
			{
				colorName=colorName.substring(0, colorName.lastIndexOf("/"));
			}
			uniqueCheck=colorName;
			String hbiColorNameFromPSForComplex=uniqueCheck;
			colorObject.setValue("hbiColorUniquenessFormula",hbiColorNameFromPSForComplex.trim());
			colorName = systemGeneratedNo.concat(" ").concat("-").concat(" ").concat(colorName);
			flag = true;
		}	
		else if(dunkStripeColorObjType.equalsIgnoreCase(colorObject.getFlexType().getFullName(true)) || yarnDyedWovenColorObjType.equalsIgnoreCase(colorObject.getFlexType().getFullNameDisplay(true)))	
		{
			if(colorName.contains("/"))
			{
				colorName=colorName.substring(0, colorName.lastIndexOf("/"));
			}
			uniqueCheck=colorName;
			String hbiColorNameFromPSForDunkStrip="";		
			//String hbiColorNameFromPSForYDK="";
			String hbiColorNameFromPSForYDW="";
			String artNo = (String)colorObject.getValue("hbiColorServiceRef");
			if(!FormatHelper.hasContent(artNo))
			{
				artNo = "";
			}
			
			if(dunkStripeColorObjType.equalsIgnoreCase(colorObject.getFlexType().getFullName(true)))
			{
				if(!FormatHelper.hasContent(artNo))
				{
					artNo = "";
					hbiColorNameFromPSForDunkStrip=uniqueCheck;
				}
				else 
				{
					hbiColorNameFromPSForDunkStrip=uniqueCheck+" "+artNo;
				}
					colorObject.setValue("hbiColorUniquenessFormula",hbiColorNameFromPSForDunkStrip.trim());
				
			}
			
			if(yarnDyedWovenColorObjType.equalsIgnoreCase(colorObject.getFlexType().getFullNameDisplay(true)))
			{
				if(!FormatHelper.hasContent(artNo))
				{
					artNo = "";
					hbiColorNameFromPSForYDW=uniqueCheck;
				}
				else 
				{
					hbiColorNameFromPSForYDW=uniqueCheck+" "+artNo;
				}
				colorObject.setValue("hbiColorUniquenessFormula",hbiColorNameFromPSForYDW.trim());
				
				//Wipro Changes Start for ticket 771670
				colorName = systemGeneratedNo.concat(" ").concat("-").concat(" ").concat(artNo).concat(" ").concat(colorName);
				//Wipro Changes End for ticket 771670
			}
			if(dunkStripeColorObjType.equalsIgnoreCase(colorObject.getFlexType().getFullName(true)))
			colorName = systemGeneratedNo.concat(" ").concat("-").concat(" ").concat(colorName).concat(" ").concat(artNo);
			
			
			flag=true;
		}
		else if(heatherColorObjType.equalsIgnoreCase(colorObject.getFlexType().getFullName(true)))
		{
			if(colorName.contains("/"))
			{
				colorName=colorName.substring(0, colorName.lastIndexOf("/"));
			}
			uniqueCheck=colorName;
			String heatherTechnique="";
			String hbiColorNameFromPS="";
			LCSLifecycleManaged heatherTechObj = (LCSLifecycleManaged)colorObject.getValue("hbiHeatherTechnique");
			if(heatherTechObj != null)
			{
				heatherTechnique = (String)heatherTechObj.getName();
				hbiColorNameFromPS=uniqueCheck+" "+heatherTechnique;
				colorName = systemGeneratedNo.concat(" ").concat("-").concat(" ").concat(colorName).concat(" ").concat(heatherTechnique);
				flag=true;
			}
			else
			{
				hbiColorNameFromPS=uniqueCheck;
				colorName = systemGeneratedNo.concat(" ").concat("-").concat(" ").concat(colorName);
				flag=true;
			}
			colorObject.setValue("hbiColorUniquenessFormula",hbiColorNameFromPS.trim());
		}
		
		
		//Wipro Changes Start for ticket 771670
		else if(garmentDyeColorObjType.equalsIgnoreCase(colorObject.getFlexType().getFullNameDisplay(true)))
		{
			
			
			
			if(colorName.contains("/"))
			{
				colorName=colorName.substring(0, colorName.lastIndexOf("/"));
				
				
				
			}
			uniqueCheck=colorName;
			
		     String hbiColorNameFromPS="";
			
				hbiColorNameFromPS=uniqueCheck;
				colorName = systemGeneratedNo.concat(" ").concat("-").concat(" ").concat(colorName);
				flag=true;
			
			colorObject.setValue("hbiColorUniquenessFormula",hbiColorNameFromPS.trim());
		}
		
		
		
		
		//Wipro Changes end for ticket 771670
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		else if(yarnDyedKnitColorObjType.equalsIgnoreCase(colorObject.getFlexType().getFullName(true)))
		{
			if(colorName.contains("/"))
			{
				colorName=colorName.substring(0, colorName.lastIndexOf("/"));
			}
			uniqueCheck=colorName;
			String hbiColorNameFromPSForYDK="";
			
			String artNo = (String)colorObject.getValue("hbiColorServiceRef");
			if(!FormatHelper.hasContent(artNo))
			{
				artNo = "";
			}
			if(!FormatHelper.hasContent(artNo))
				{
					artNo = "";
					hbiColorNameFromPSForYDK=uniqueCheck;
				}
				else 
				{
					hbiColorNameFromPSForYDK=uniqueCheck+" "+artNo;
				}
				colorObject.setValue("hbiColorUniquenessFormula",hbiColorNameFromPSForYDK.trim());
				colorName = systemGeneratedNo.concat(" ").concat("-").concat(" ").concat(artNo).concat(", ").concat(colorName);
			flag=true;
				
		}

		if(flag)
		{
			colorObject.setValue("name",colorName);
			colorObject.setColorName(colorName);
		}	
	}

	public String getColorNameForSolid(LCSColor moaColorObj,String colorServiceNo,String colorServiceName)throws WTException, WTPropertyVetoException
	{
		String colorService = "";
		
		if(!FormatHelper.hasContent(colorServiceNo))
		{
			colorServiceNo = "";
		}
								
		if(!FormatHelper.hasContent(colorServiceName))
		{
			colorServiceName = "";
		}
		colorService = colorServiceNo+" "+colorServiceName+"/";
		return colorService;
	
	}
	
	
	public String getColorNameForheatherColorObjType(LCSColor moaColorObj,String moaColorObjName) throws WTException, WTPropertyVetoException
	{
		String colorService = "";
		moaColorObjName = (String)moaColorObj.getValue("name");
		moaColorObjName = moaColorObjName.trim();
		//if(moaColorObjName.length() > 12)
		//{
			//colorService = moaColorObjName.substring(moaColorObjName.indexOf(" ",3)+1) +"/"; 
			colorService = moaColorObjName.substring(moaColorObjName.indexOf("-",1)+2) +"/"; 
		//}	
		return colorService;						
	}
	
	
	
	//Wipro Changes Start for ticket 771670
	
	public String getColorNameForGarmentDyeColorObjType(LCSColor moaColorObj,String moaColorObjName) throws WTException, WTPropertyVetoException
	{
		String colorService = "";
		moaColorObjName = (String)moaColorObj.getValue("name");
		moaColorObjName = moaColorObjName.trim();
		//if(moaColorObjName.length() > 12)
		//{
			//colorService = moaColorObjName.substring(moaColorObjName.indexOf(" ",3)+1) +"/"; 
			colorService = moaColorObjName.substring(moaColorObjName.indexOf("-",1)+2) +"/"; 
		//}	
		return colorService;						
	}
	
	
	//Wipro Changes end for ticket 771670
	
	public static void colorNameDelete(WTObject wtObj) throws WTException, WTPropertyVetoException
	{
		if(wtObj instanceof LCSColor)
		{
			MethodContext.getContext().put("hbi","COLORDELETE");
	
		}
	}


		
}
	
