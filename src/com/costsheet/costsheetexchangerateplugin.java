///*
// * CostSheetExchangeRatePlugin.java
// * @author Adelle
// * @version 1.8
// * Created on May 13, 2013  
// */
//
//package com.vrd.costsheet;
//
//import java.text.SimpleDateFormat;
//import java.util.Collection;
//import java.util.Date;
//import java.util.Iterator;
//
//import com.lcs.wc.db.*;
//import com.lcs.wc.flextype.*;
//import com.lcs.wc.foundation.*;
//import com.lcs.wc.season.LCSSeason;
//import com.lcs.wc.season.LCSSeasonMaster;
//import com.lcs.wc.sourcing.LCSProductCostSheet;
//import com.lcs.wc.util.*;
//import com.vrd.util.LCSPropertiesPluginHelper;
//
//import wt.fc.WTObject;
//import wt.util.WTException;
//import wt.util.WTPropertyVetoException;
//
///** Populates the Currency Conversion for a Cost Sheet based on Alternate Currency selected.
// *@author Adelle
// * 
// * @version 1.8
// */
//public class CostSheetExchangeRatePlugin{
//
//	/** Constructor.
//	 *
//	 */
//	protected CostSheetExchangeRatePlugin(){}
//	/** Print the Debugging statements.
//	 *
//	 */
//	private static final String CURRENCY=LCSProperties.get("com.vrd.costsheet.CostSheetExchangeRatePlugin.currencyOfBO", "vrdCurrency");
//	/** Reading key for currency Of BO from property file
//	 *
//	 */
//	private static final String EXCHANGE_RATE=LCSProperties.get("com.vrd.costsheet.CostSheetExchangeRatePlugin.exchangeRateOfBO", "vrdActualConvRate");
//	/** Reading key for start Date Of BO from property file
//	 *
//	 */
//	private static final String START_DATE_OF_BO=LCSProperties.get("com.vrd.costsheet.CostSheetExchangeRatePlugin.startDateOfBO", "vrdStartDate");
//	/** Reading key for end Date Of BO from property file
//	 *
//	 */
//	private static final String END_DATE_OF_BO=LCSProperties.get("com.vrd.costsheet.CostSheetExchangeRatePlugin.endDateOfBO", "vrdEndDate");
//	/** Reading key for start Date Of Season from property file
//	 *
//	 */
//	private static final String START_DATE_OF_SEASON=LCSProperties.get("com.vrd.costsheet.CostSheetExchangeRatePlugin.startDateOfSeason", "vrdStartDate");
//	/** Reading key for type Of BO from property file
//	 *
//	 */
//	private static final String TYPE_OF_BO=LCSProperties.get("com.vrd.costsheet.CostSheetExchangeRatePlugin.typeOfBO", "Business Object\\Lookup Tables\\Exchange Rates");
//
//	/** Populates the Currency Conversion for a Cost Sheet based on Alternate Currency selected.
//	 * @param wtobject
//	 * @return costsheet
//	 * @throws WTException
//	 * @throws WTPropertyVetoException
//	 */
//
//	public static final WTObject settingExchangeRateFromBO(WTObject wtobject)
//	throws WTException, WTPropertyVetoException
//	{
//		LCSProductCostSheet costsheet = null;
//		LCSSeasonMaster seasonMaster= new LCSSeasonMaster();
//		LCSSeason season = null;
//		try
//		{
//			LCSLog.debug("VRD>>>>>> CostSheetExchangeRatePlugin.settingExchangeRateFromBO: START ("+wtobject+")");
//			if(wtobject instanceof LCSProductCostSheet){
//				costsheet = (LCSProductCostSheet) wtobject;
//				FlexType flextype = costsheet.getFlexType();				
//				String flexCostSheetTypePath = flextype.getFullName(true);
//				seasonMaster = (LCSSeasonMaster)costsheet.getSeasonMaster();
//				season = (LCSSeason)VersionHelper.latestIterationOf(seasonMaster);
//				if(season != null){
//					Date seasonStartDate = (Date)season.getValue(START_DATE_OF_SEASON);
//					SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
//					String formattedSeasonStartDate = sdf.format(seasonStartDate);
//					String selectedCurrency = "";
//					String attList = "";
//					String altCurr = "";
//					String convFact = "";
//
//					FlexType xRateType = (new WTSFlexTypeCache()).getFlexTypeFromPath(TYPE_OF_BO);
//					String exchangeRateCol = xRateType.getAttribute(EXCHANGE_RATE).getColumnName();
//					String busObjStrtDtCol = xRateType.getAttribute(START_DATE_OF_BO).getColumnName();
//					String busObjStrtName = xRateType.getAttribute(START_DATE_OF_BO).getColumnDescriptorName();
//					String busObjEndName = xRateType.getAttribute(END_DATE_OF_BO).getColumnDescriptorName();
//
//					Collection attLists = LCSPropertiesPluginHelper.getAllPropertySettings("com.vrd.costsheet.CostSheetExchangeRatePlugin."+flexCostSheetTypePath);
//					if(attLists.size() > 0){
//						Object list[] = attLists.toArray();
//						for(int i = list.length; i > 0; i--)
//						{
//							attList = (String) list[i-1];
//							altCurr = attList.substring(0,attList.indexOf(','));
//							convFact = attList.substring(attList.lastIndexOf(',')+1,attList.length());
//							try
//							{
//								selectedCurrency = (String)costsheet.getValue(altCurr);
//								if(FormatHelper.hasContent(selectedCurrency)){
//
//								// We need to query the DB in Loop for the Alternate Currencies chosen 
//									FlexTypeQueryStatement statement = new FlexTypeQueryStatement();
//									statement.setType(xRateType);
//									statement.appendFlexSelectColumn(CURRENCY);	
//									statement.appendFlexSelectColumn(EXCHANGE_RATE);	
//									statement.appendFlexSelectColumn(START_DATE_OF_BO);
//
//									statement.appendAndIfNeeded();
//									statement.appendFlexCriteria(CURRENCY, selectedCurrency, Criteria.EQUALS);
//									statement.appendAndIfNeeded();
//									statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, busObjStrtName), "?", Criteria.LESS_THAN_EQUAL), formattedSeasonStartDate);
//									statement.appendAndIfNeeded();
//									statement.appendOpenParen();
//									statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, busObjEndName), "?", Criteria.GREATER_THAN_EQUAL), formattedSeasonStartDate);
//									statement.appendOrIfNeeded();
//									statement.appendCriteria(new Criteria(new QueryColumn(LCSLifecycleManaged.class, busObjEndName), "?", Criteria.IS_NULL));
//									statement.appendClosedParen();
//									LCSLog.debug("VRD>>>>>> CostSheetExchangeRatePlugin.settingExchangeRateFromBO: The Query is:"+statement);
//
//									Collection results = LCSQuery.runDirectQuery(statement).getResults();		
//									LCSLog.debug("VRD>>>>>> CostSheetExchangeRatePlugin.settingExchangeRateFromBO: The result of query is:"+results);
//									//Check if results are not empty
//									if(!results.isEmpty()) {
//										Iterator busObjIter = results.iterator();
//										Double exchange = 0.0;
//										Date startDate;
//										FlexObject latestObject = null;
//										Date latestDate = new Date();
//										int flag = 0;
//										// Finding out Exchange Rate BO with Latest Start Date.
//										while (busObjIter.hasNext()) 
//										{
//											FlexObject flexObject = (FlexObject)busObjIter.next();
//											startDate = FormatHelper.parseDate(flexObject.get("LCSLIFECYCLEMANAGED." + busObjStrtDtCol).toString());
//											
//											if( flag == 0){
//												latestDate = startDate;		
//												latestObject = flexObject;
//												flag ++;
//											}
//											if(startDate.compareTo(latestDate) > 0){
//												latestDate = startDate;
//												latestObject = flexObject;
//											}
//										}
//								
//										// Get the Excahnge rate from Latest BO
//										if(latestObject != null){
//											exchange = FormatHelper.parseDouble((String)latestObject.get("LCSLIFECYCLEMANAGED." + exchangeRateCol));
//											//Set it to Cost Sheet.
//											LCSLog.debug("Setting"+convFact+"="+exchange);
//													
//											costsheet.setValue(convFact,exchange);
//										}//END OF LOOP CHECKING IF THERE IS AN OBJECT WHICH IS BEING SET
//									}//END OF LOOP CHECKING IF THERE ARE BJECTS IN THE RESULT COLLECTION
//									else
//									{
//										LCSLog.error("VRD>>>>>> CostSheetExchangeRatePlugin.settingExchangeRateFromBO: No Business Objects which satisfy the required criteria.");
//										LCSLog.debug("VRD>>>>>> CostSheetExchangeRatePlugin.settingExchangeRateFromBO: Setting"+convFact+"= 0");
//										costsheet.setValue(convFact,"0");
//									}
//								}//END OF LOOP CHECKING IF THERE A ALTERNATE CURRENCY WHICH IS SELECTED
//								else
//								{
//									LCSLog.error("VRD>>>>>> CostSheetExchangeRatePlugin.settingExchangeRateFromBO: No Alternate Currency is chosen.");
//									LCSLog.debug("VRD>>>>>> CostSheetExchangeRatePlugin.settingExchangeRateFromBO: Setting"+convFact+"= 0");
//
//									costsheet.setValue(convFact,"0");
//								}
//							}
//							catch (Exception e){
//								LCSLog.stackTrace(e);
//							}
//						}//END OF LOOP WHICH CONTINUES TILL ALL THE PROPERTY ENTRIES ARE CHECKED FOR ALL THE ATTRIBUTE GROUPS		
//
//					}//END OF LOOP CHECKING IF THERE ARE ANY PROPERTY ENTRIES WHICH HAVE TO BE READ.
//					else
//					{
//						LCSLog.error("VRD>>>>>> CostSheetExchangeRatePlugin.settingExchangeRateFromBO: There are no Exchange rate attributes to set on cost sheet.");
//					}
//				}//END OF LOOP CHECKING IF THERE IS A SEASON FOR THE COST SHEET
//				else
//				{
//					LCSLog.error("VRD>>>>>> CostSheetExchangeRatePlugin.settingExchangeRateFromBO: Season is not attached to the product");
//					String attList ="";
//					String convFact="";
//
//					Collection attLists = LCSPropertiesPluginHelper.getAllPropertySettings("com.vrd.costsheet.CostSheetExchangeRatePlugin");
//					if(attLists.size() > 0)
//					{
//						Object list[] = attLists.toArray();
//						for(int i = list.length; i > 0; i--)
//						{
//							attList = (String) list[i-1];
//							convFact= attList.substring(attList.lastIndexOf(',')+1,attList.length());
//						}
//					}
//					LCSLog.debug("VRD>>>>>> CostSheetExchangeRatePlugin.settingExchangeRateFromBO: Setting"+convFact+"= 0");
//					costsheet.setValue(convFact,"0");
//				}
//			}//END OF LOOP CHECKING IF OBJECT IS AN INSTANCE OF COST SHEET
//			else
//			{
//				LCSLog.error("VRD>>>>>> CostSheetExchangeRatePlugin.settingExchangeRateFromBO: " + wtobject + " is not a CostSheet object.");
//			}
//
//		}//END OF TRY LOOP
//		catch (Exception e){
//			LCSLog.stackTrace(e);
//		}
//		LCSLog.debug("VRD>>>>>> CostSheetExchangeRatePlugin.settingExchangeRateFromBO: FINISH");
//
//		return costsheet;
//	}
//}
