package com.hbi.wc.moa;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.Locale;

import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.color.LCSColorLogic;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.color.LCSColorHelper;


import com.hbi.wc.util.HBITableDataSorter;

import wt.fc.WTObject;

import wt.util.WTException;
import com.lcs.wc.util.LCSException;
import wt.util.WTPropertyVetoException;
import wt.method.MethodContext;

public class HBIPitchSheetMOAPlugin

{
	private static String complexColorObjType = LCSProperties.get("com.hbi.wc.color.HBIColorNameDerivationPlugin.complexColorObjType", "Color\\Complex");
	private static String dunkStripeColorObjType = LCSProperties.get("com.hbi.wc.color.HBIColorNameDerivationPlugin.complexColorObjType", "Color\\Dunk Stripe");
	private static String heatherColorObjType = LCSProperties.get("com.hbi.wc.color.HBIColorNameDerivationPlugin.heatherColorObjType", "Color\\Heather");
	private static String  yarnDyedKnitColorObjType = LCSProperties.get("com.hbi.wc.color.HBIColorNameDerivationPlugin.complexColorObjType", "Color\\Yarn Dyed Knit Stripes");
	private static String  yarnDyedWovenColorObjType = LCSProperties.get("com.hbi.wc.color.HBIColorNameDerivationPlugin.complexColorObjType", "Color\\Yarn Dye");
	private static String  solidColorObjType = LCSProperties.get("com.hbi.wc.color.HBIColorNameDerivationPlugin.solidColorObjType", "Color\\Solid");
	
	private static String garmentDyeColorObjType = LCSProperties.get("com.hbi.wc.color.HBIColorNameDerivationPlugin.garmentDyeColorObjType", "Color\\Garment Dye");
	public static void setPitchSheetMOADataOnColor(LCSColor Color) throws LCSException,WTException, WTPropertyVetoException
	{
		
					//-- added fix for color issue #77423 removed Plugin call and calling this from controller

			System.out.println("Called from JSP::::::::::::"+Color);
				String colorService="";
				LCSColor ownerObj=null;
				String ownerName="";
				String colorName ="";
				boolean flag = false;
				String colorServiceNo="";
				String colorServiceName="";
				
				String colorObjName ="";
				
				Vector sortList = new Vector();  
				sortList.add("SORTINGNUMBER");
		
				Vector numericSortList = new Vector();   
				numericSortList.add("SORTINGNUMBER");

				ownerObj =Color;
				System.out.println("!!!!!ownerObj!!!!!!!" +ownerObj.getName());
				
				Integer complexColObjSeq = ((Long) ownerObj.getValue("hbiColorSequence")).intValue();
				String colorSeq = Integer.toString(complexColObjSeq);
	
				String colorRefereceType = (String)ownerObj.getValue("hbiColorStandardType");
				colorRefereceType = ownerObj.getFlexType().getAttribute("hbiColorStandardType").getAttValueList().getValue(colorRefereceType,Locale.getDefault());
				if(!FormatHelper.hasContent(colorRefereceType))
				{
					colorRefereceType = "";
				}
		
				String systemGeneratedNo = colorRefereceType+" "+colorSeq;

				LCSMOATable pitchSheetMOATable = (LCSMOATable) ownerObj.getValue("hbiPitchSheet");
				if(pitchSheetMOATable != null)
				{
					new HBITableDataSorter().setNumericSort(numericSortList);
					Collection<FlexObject> rowIdColl =pitchSheetMOATable.getRows();
					
					rowIdColl = new HBITableDataSorter().sortTableDataObjects(rowIdColl, sortList);  
					
					for(FlexObject flexObj : rowIdColl)
					{
						String key = flexObj.getString("OID");
						LCSMOAObject moaObject = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+key);
						if(moaObject != null)
						{
							LCSColor moaColorObj = (LCSColor)moaObject.getValue("color");
							if(moaColorObj != null)
							{
								colorServiceNo = (String)moaColorObj.getValue("hbiColorServiceRef");
								colorServiceName = (String)moaColorObj.getValue("hbiColorServiceName");
								colorObjName = (String)moaColorObj.getValue("name");
								System.out.println("colorObjName:::::::::::"+colorObjName);
								if(solidColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullName(true)))
								{
									colorService = new HBIPitchSheetMOAPlugin().getColorNameForSolid(moaColorObj,colorServiceNo,colorServiceName);
									colorName=colorName.concat(colorService);
								}
								else if(heatherColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullName(true)) || dunkStripeColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullName(true))|| yarnDyedKnitColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullName(true)) || yarnDyedWovenColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullNameDisplay(true)) || complexColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullName(true))|| garmentDyeColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullNameDisplay(true)))
								{
									
									
									colorService = new HBIPitchSheetMOAPlugin().getColorNameForheatherColorObjType(moaColorObj,colorObjName);
									colorName=colorName.concat(colorService);
								}
							}	
						}	
					}
					if(complexColorObjType.equalsIgnoreCase(ownerObj.getFlexType().getFullName(true)))
					{	
						if(colorName.contains("/"))
						{
							colorName=colorName.substring(0, colorName.lastIndexOf("/"));
						}
						colorName = systemGeneratedNo.concat(" ").concat("-").concat(" ").concat(colorName);
						flag = true;
					}	
				
					if(dunkStripeColorObjType.equalsIgnoreCase(ownerObj.getFlexType().getFullName(true)) )
					{
						if(colorName.contains("/"))
						{
							colorName=colorName.substring(0, colorName.lastIndexOf("/"));
						}
						String artNo = (String)ownerObj.getValue("hbiColorServiceRef");
						if(!FormatHelper.hasContent(artNo))
						{
							artNo = "";
						}
						colorName = systemGeneratedNo.concat(" ").concat("-").concat(" ").concat(colorName).concat(" ").concat(artNo);
						flag=true;
					}
					
					
					if(yarnDyedWovenColorObjType.equalsIgnoreCase(ownerObj.getFlexType().getFullNameDisplay(true)))	
					{
						
						if(colorName.contains("/"))
						{
							colorName=colorName.substring(0, colorName.lastIndexOf("/"));
						}
						String artNo = (String)ownerObj.getValue("hbiColorServiceRef");
						if(!FormatHelper.hasContent(artNo))
						{
							artNo = "";
						}
						//colorName = systemGeneratedNo.concat(" ").concat("-").concat(" ").concat(colorName).concat(" ").concat(artNo);
						//Wipro Changes start for ticket 771670
						colorName = systemGeneratedNo.concat(" ").concat("-").concat(" ").concat(artNo).concat(" ").concat(colorName);
						//Wipro Changes end for ticket 771670
						flag=true;
					}
					
					
					
					
					
					if(heatherColorObjType.equalsIgnoreCase(ownerObj.getFlexType().getFullName(true)))
					{
						if(colorName.contains("/"))
						{
							colorName=colorName.substring(0, colorName.lastIndexOf("/"));
						}
						LCSLifecycleManaged heatherTechObj = (LCSLifecycleManaged)ownerObj.getValue("hbiHeatherTechnique");
						String heatherTechnique = "";
						if(heatherTechObj != null)
						{
							heatherTechnique = (String)heatherTechObj.getName();
							colorName = systemGeneratedNo.concat(" ").concat("-").concat(" ").concat(colorName).concat(" ").concat(heatherTechnique);
							flag=true;
						}
						else
						{
							colorName = systemGeneratedNo.concat(" ").concat("-").concat(" ").concat(colorName);
							flag=true;
						}	
					}
					//Wipro Changes start for ticket 771670
					
					
					if(garmentDyeColorObjType.equalsIgnoreCase(ownerObj.getFlexType().getFullNameDisplay(true)))
					{
						
						
						if(colorName.contains("/"))
						{
							colorName=colorName.substring(0, colorName.lastIndexOf("/"));
						}
						
							colorName = systemGeneratedNo.concat(" ").concat("-").concat(" ").concat(colorName);
							flag=true;
							
					}
					
					
					//Wipro Changes end for ticket 771670	
					
					
					
					
					if(yarnDyedKnitColorObjType.equalsIgnoreCase(ownerObj.getFlexType().getFullName(true))) 
					{
						if(colorName.contains("/"))
						{
							colorName=colorName.substring(0, colorName.lastIndexOf("/"));
						}
						String artNo = (String)ownerObj.getValue("hbiColorServiceRef");
						if(!FormatHelper.hasContent(artNo))
						{
							artNo = "";
						}
						colorName = systemGeneratedNo.concat(" ").concat("-").concat(" ").concat(artNo).concat(" , ").concat(colorName);
						flag=true;
					}
					
					if(flag)
					{
						ownerObj.setValue("name",colorName);
						ownerObj.setColorName(colorName);
						System.out.println("colorName:::::::::::"+colorName);
						LCSLogic.persist(ownerObj);
					}
				}
			
		
	}
		
	public static void deletePitchSheetMOADataOnColor(WTObject wtObj) throws WTException, WTPropertyVetoException
	{
		if(!(wtObj instanceof LCSMOAObject))
		{
			return;
		}
		
		String objectAction = (String)MethodContext.getContext().get("hbi");
		System.out.println(" !!!   objectAction !!!!!!" +objectAction);
		
		if(!"COLORDELETE".equalsIgnoreCase(objectAction))
		{
			LCSMOAObject pitchSheetMOA = (LCSMOAObject) wtObj;
			if(pitchSheetMOA != null)
			{
				String colorService="";
				LCSColor ownerObj=null;
				String ownerName="";
				String colorName ="";
				boolean flag = false;
				
				Vector sortList = new Vector();  
				sortList.add("SORTINGNUMBER");
		
				Vector numericSortList = new Vector();   
				numericSortList.add("SORTINGNUMBER");

				ownerObj =(LCSColor) pitchSheetMOA.getOwner();
				
				colorName = (String)ownerObj.getColorName();
				
				LCSMOATable pitchSheetMOATable = (LCSMOATable) ownerObj.getValue("hbiPitchSheet");
				if(pitchSheetMOATable != null)
				{
					new HBITableDataSorter().setNumericSort(numericSortList);
					Collection<FlexObject> rowIdColl =pitchSheetMOATable.getRows();
					rowIdColl = new HBITableDataSorter().sortTableDataObjects(rowIdColl, sortList);  
					
					if(rowIdColl.size() >= 0)
					{
						for(FlexObject flexObj : rowIdColl)
						{
							String key = flexObj.getString("OID");
							LCSMOAObject moaObject = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+key);
							if(moaObject != null)
							{
								LCSColor moaColorObj = (LCSColor)moaObject.getValue("color");
								if(moaColorObj != null)
								{
									String colorServiceNo = (String)moaColorObj.getValue("hbiColorServiceRef");
									String colorServiceName = (String)moaColorObj.getValue("hbiColorServiceName");
									String colorNameMOA = (String)moaColorObj.getValue("name");
									if(solidColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullName(true)))
									{
										if(!FormatHelper.hasContent(colorServiceNo))
										{
												colorServiceNo = "";
										}
										
										if(!FormatHelper.hasContent(colorServiceName))
										{
											colorServiceName = "";
										}
										
										colorService = colorServiceNo+" "+colorServiceName+"/";
										colorName=colorName+" "+colorService;
									}
									else if(heatherColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullName(true)) || dunkStripeColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullName(true))|| yarnDyedKnitColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullName(true)) || yarnDyedWovenColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullNameDisplay(true))|| garmentDyeColorObjType.equalsIgnoreCase(moaColorObj.getFlexType().getFullNameDisplay(true)))
									{
										colorService=new HBIPitchSheetMOAPlugin().deleteColorNameForNonSolid(moaColorObj,colorNameMOA);
										colorService = colorServiceNo+" "+colorServiceName+"/";
										colorName=colorName+" "+colorService;
									}
								}
							}	
						}
						
						if(complexColorObjType.equalsIgnoreCase(ownerObj.getFlexType().getFullName(true)))
						{	
							colorName = colorName.replaceAll(colorService, "");
							flag = true;
						}	
					
						if(dunkStripeColorObjType.equalsIgnoreCase(ownerObj.getFlexType().getFullName(true)) || yarnDyedKnitColorObjType.equalsIgnoreCase(ownerObj.getFlexType().getFullName(true)) || yarnDyedWovenColorObjType.equalsIgnoreCase(ownerObj.getFlexType().getFullNameDisplay(true)))	
						{
							colorName = colorName.replaceAll(colorService, "");
							flag = true;
						}
						else if(heatherColorObjType.equalsIgnoreCase(ownerObj.getFlexType().getFullName(true))|| garmentDyeColorObjType.equalsIgnoreCase(ownerObj.getFlexType().getFullNameDisplay(true)))
						{
							
							
							
							colorName = colorName.replaceAll(colorService, "");
							flag = true;	
						}

						if(flag)
						{
							ownerObj.setValue("name",colorName);
							ownerObj.setColorName(colorName);
							LCSLogic.persist(ownerObj);
						}
					}
				}
			}
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
			colorService = moaColorObjName.substring(moaColorObjName.indexOf("-",1)+2)+"/";
			
		//}	
		return colorService;					
		
	}
	
	
	public String deleteColorNameForNonSolid(LCSColor moaColorObj,String moaColorObjName) throws WTException, WTPropertyVetoException
	{
		String colorService = "";
		moaColorObjName = (String)moaColorObj.getValue("name");
		moaColorObjName = moaColorObjName.trim();
		colorService = moaColorObjName.substring(moaColorObjName.indexOf("-",1)+2) +"/"; 
		return colorService;						
	}
	
	
	
}	