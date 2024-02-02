///*
// * CostSheetHTSCodePlugin.java
// * @author Adelle
// * @version 1.8
// * Created on April 22, 2013  
// */
//
//package com.vrd.costsheet;
//
//import java.util.Collection;
//
//import com.lcs.wc.flextype.FlexType;
//import com.lcs.wc.flextype.FlexTyped;
//import com.lcs.wc.util.FormatHelper;
//import com.lcs.wc.util.LCSLog;
//import com.lcs.wc.util.LCSProperties;
//import com.vrd.util.LCSPropertiesPluginHelper;
//
//import wt.fc.WTObject;
//import wt.util.WTException;
//import wt.util.WTPropertyVetoException;
//
///** Populates the Duty Rate(%) for a Cost Sheet based on HTS Code selected.
// * @author Adelle
// * 
// * @version 1.8
// */ 
//
//public class CostSheetHTSCodePlugin
//{
//	/** Constructor.
//	 *
//	 */
//	protected CostSheetHTSCodePlugin(){}
//	/** Print the Debugging statements.
//	 *
//	 */
//
//	/** Reading key for duty Of BO from property file
//	 *
//	 */
//	private static final String DUTY_OF_BO=LCSProperties.get("com.vrd.costsheet.CostSheetHTSCodePlugin.dutyOfBO");
//
//	public static final WTObject setHTSCodeFromBO(WTObject wtobject)
//	throws WTException, WTPropertyVetoException
//	{
//		try
//		{
//			LCSLog.debug("VRD>>>>>> CostSheetHTSCodePlugin.setHTSCodeFromBO: START ("+wtobject+")");     
//			if(wtobject instanceof FlexTyped)
//			{
//				FlexTyped flexObj = (FlexTyped) wtobject;
//				FlexType flextype = flexObj.getFlexType();
//				String flexCostSheetPath = flextype.getFullName(true);
//				String attList="";
//				String tariffCodeAtt="";
//				String duyAttOfBO = DUTY_OF_BO ;
//				String dutyRateAtt="";
//				FlexTyped tariffCodeValue ;
//				Double  valueOfDutyRateInBO=0.0;
//
//				Collection attLists = LCSPropertiesPluginHelper.getAllPropertySettings("com.vrd.costsheet.CostSheetHTSCodePlugin."+flexCostSheetPath);
//				if(attLists.size() > 0)
//				{
//					Object list[] = attLists.toArray();
//					for(int i = list.length; i > 0; i--)
//					{
//						attList = (String) list[i-1];
//						tariffCodeAtt = attList.substring(0,attList.indexOf(','));
//						dutyRateAtt = attList.substring(attList.lastIndexOf(',')+1,attList.length());
//						if(FormatHelper.hasContent(tariffCodeAtt))
//						{
//							if(flextype.getAttributeKeyList().contains(tariffCodeAtt.toUpperCase()))
//							{
//
//								tariffCodeValue = (FlexTyped) flexObj.getValue(tariffCodeAtt);
//								if(tariffCodeValue != null)
//								{
//									valueOfDutyRateInBO = (Double)tariffCodeValue.getValue(duyAttOfBO);
//									flexObj.setValue(dutyRateAtt,valueOfDutyRateInBO);
//									LCSLog.debug("VRD>>>>>> CostSheetHTSCodePlugin.setHTSCodeFromBO: Setting"+dutyRateAtt+"="+valueOfDutyRateInBO);
//									
//								}//END OF LOOP CHECKING IF THERE IS A DUTY RAT VALUE IN A BUSINESS OBJECT
//								else
//								{
//									flexObj.setValue(dutyRateAtt,"0");
//									LCSLog.debug("VRD>>>>>> CostSheetHTSCodePlugin.setHTSCodeFromBO: Setting"+dutyRateAtt+"=0");
//								}
//							}//END OF LOOP CHECKING IF THE COST SHEET HAS THE CORESPOONDING KEYS OF TARIFF CODE
//							else
//							{
//								LCSLog.error("VRD>>>>>> CostSheetHTSCodePlugin.setHTSCodeFromBO: " + wtobject + " does not have a object valueOfDutyRateInBOerence attribute (" + tariffCodeAtt + ").");
//							}
//						}//END OF LOOP CHECKING IF USER  HAS SELECTED A TARIFF CODE
//						else
//						{
//							flexObj.setValue(dutyRateAtt,"0");
//							LCSLog.debug("VRD>>>>>> CostSheetHTSCodePlugin.setHTSCodeFromBO: Setting"+dutyRateAtt+"=0");
//						}
//					}//END OF LOOPING WHICH CONTINUES TILL ALL THE ATTRIBUTE KEYS ARE READ
//				}//END OF LOOP CHECKING IF THERE ARE ATTRIBUTE KEYS BEING READ FROM PROPERTY FILE.
//				else
//				{
//					LCSLog.error("VRD>>>>>> CostSheetHTSCodePlugin.setHTSCodeFromBO: " + wtobject + " does not have any attribute copy settings (" + flexCostSheetPath + ").");
//				}
//			}//END OF LOOP CHECKING IF THE OBJECT IS AN INSTANCE OF FLEXTYPED OBJECT
//			else
//			{
//				LCSLog.error("VRD>>>>>> CostSheetHTSCodePlugin.setHTSCodeFromBO: " + wtobject + " is not a flextype object.");
//			}
//		}//END OF TRY LOOP
//		catch(Exception e)
//		{
//			LCSLog.stackTrace(e);
//		}
//		LCSLog.debug("VRD>>>>>> CostSheetHTSCodePlugin.setHTSCodeFromBO: FINISH");
//		return wtobject;
//	}
//}
