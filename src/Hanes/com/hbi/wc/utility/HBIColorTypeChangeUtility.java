package com.hbi.wc.utility;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

import com.lcs.wc.color.LCSColor;   
import com.lcs.wc.color.LCSColorHelper;
import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.RetypeLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.color.LCSPalette;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOAObjectHelper;

import wt.fc.WTObject;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;

/**
 * HBIColorTypeChangeUtility.java
 * 
 * This class contains a utility function for End of End Color Objects which are using to read the End Of End Color data 
 * and populating attributes data  in new Color type i.e. "Color\\Yarn Dye Wovens". 
 * @author vijayalaxmi.shetty@Hanes.com
 * @since Nov-22-2016
 */
public class HBIColorTypeChangeUtility implements RemoteAccess
{
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "pass2014a");
	private static RemoteMethodServer remoteMethodServer;
	private static String floderPhysicalLocation = "";
	
	
	/* Default executable function of the class HBIColorTypeChangeUtility */
	public static void main(String[] args) 
	{
		LCSLog.debug("### START HBIColorTypeChangeUtility.main() ###");
		
		try
		{
			MethodContext mcontext = new MethodContext((String) null, (Object) null);
			SessionContext sessioncontext = SessionContext.newContext();

			remoteMethodServer = RemoteMethodServer.getDefault();
	        remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
	        remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);
	        
	        GatewayAuthenticator authenticator = new GatewayAuthenticator();
			authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setAuthenticator(authenticator);
	        
	        validateAndUpdateColorType();
	        System.exit(0);
		}
		catch (Exception exception) 
		{
			exception.printStackTrace();
			System.exit(1);
		}
		
		LCSLog.debug("### END HBIColorTypeChangeUtility.main() ###");
	}
	
	
	
	/**
	 * This function is invoking from the default executable function of the class to initiate the process of End Of End Color  object.
	* @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void validateAndUpdateColorType() throws WTException,WTPropertyVetoException
	{
		//This method is used to get End Of End Color Object from the PreparedQuertStatements from End Of End Color Type.
		 Collection<LCSColor> CollOFEndOfEndColorObjects = getEndOnEndColorTypeObjects();
		 System.out.println("!!!!!!collectionOfEndOfEndColorObj!!!!!!!" +CollOFEndOfEndColorObjects.size());
		 for(LCSColor endOfEndColorObj : CollOFEndOfEndColorObjects)
		 {
			System.out.println("!!!!!!color Name!!!!!!!" +endOfEndColorObj.getName());
			//This method is used to process End of End Color Object to Yarn Dye Wovens color objects.
			updateEndOnEndColorTypeChange(endOfEndColorObj);
		 }
	}
	
	
	/**
	 * This function is using to get collection of End OF End color  objects. 
	 * @return collectionOfEndOfEndColorObj - Collection<LCSColor>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static Collection<LCSColor> getEndOnEndColorTypeObjects() throws WTException,WTPropertyVetoException
	{
		Collection<LCSColor> collectionOfEndOfEndColorObj = new ArrayList<LCSColor>();
		LCSColor endOfEndColorObj = null;
		FlexType endOfEndColorFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Color\\End on End"); 
		String id = endOfEndColorFlexTypeObj.getTypeIdPath();
		Vector<FlexObject> listOfObjects = null;
		
		//Initializing the PreparedQueryStatement, which is using to get LCSColor object based on the given set of parameters(like FlexTypePath of the object). 
    	PreparedQueryStatement statement = new PreparedQueryStatement();
    	statement.appendFromTable(LCSColor.class);
    	statement.appendSelectColumn(new QueryColumn(LCSColor.class, "thePersistInfo.theObjectIdentifier.id"));
    	statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSColor.class, "flexTypeIdPath"), id, Criteria.EQUALS));
		
        SearchResults results = LCSQuery.runDirectQuery(statement);
        if(results != null && results.getResultsFound() > 0)
        {
			listOfObjects = results.getResults();
			for(FlexObject flexObj: listOfObjects)
			{
				endOfEndColorObj = (LCSColor) LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSColor:"+flexObj.getString("LCSColor.IDA2A2"));
				collectionOfEndOfEndColorObj.add(endOfEndColorObj);
			}	
        }
		
		return collectionOfEndOfEndColorObj;
	}
	
	/**
	 * This function is using to get process End Of End Color objects data to Yarn Dye Wovens Color type
	 * @param endOfEndColorObj - LCSColor
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void updateEndOnEndColorTypeChange(LCSColor endOfEndColorObj) throws WTException,WTPropertyVetoException
	{
		// New Color type flextype object.
		FlexType newColorFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Color\\Yarn Dye Wovens");
	
		LCSColor masterColorObj = (LCSColor)endOfEndColorObj.getValue("hbiMasterColorStdReference");
		
		String baseFabric = (String) endOfEndColorObj.getValue("hbiBaseFabric");
		
		LCSPalette paletteObj = (LCSPalette)endOfEndColorObj.getValue("hbiPalette");
		
		LCSSeason seasonObj = (LCSSeason)endOfEndColorObj.getValue("hbiSeason"); 
		
		String designName = (String)endOfEndColorObj.getValue("hbiDesignName");
		
		String delivery = (String)endOfEndColorObj.getValue("hbiDelivery");
		delivery = endOfEndColorObj.getFlexType().getAttribute("hbiDelivery").getAttValueList().getValue(delivery,Locale.getDefault());
		
		String designColorway = (String)endOfEndColorObj.getValue("hbiDesignColorway");
		
		String assortment = (String)endOfEndColorObj.getValue("hbiAssortment");
		
		String productLine = (String)endOfEndColorObj.getValue("hbiProductLine");
		productLine = endOfEndColorObj.getFlexType().getAttribute("hbiProductLine").getAttValueList().getValue(productLine,Locale.getDefault());
		
		double repeatWidth = (Double)endOfEndColorObj.getValue("hbiDesignWidth");
		
		double repeatLength = (Double)endOfEndColorObj.getValue("hbiDesignLength");   
		
		String program = (String)endOfEndColorObj.getValue("hbiProgram");
		
		String garmentProducts = (String)endOfEndColorObj.getValue("hbiGarmentProducts");
		
		//This method will give MOA Objects exisitng in Pitch Sheet data of End Of End Color object.
		Collection<LCSMOAObject> moaObjectCollection = getMOAObjectsFromEndOfEndColor(endOfEndColorObj);
		
		//Get Type OID from the given FlexType, using RetypeLogic API to change the type of the Material object from an existing type to the newly given type and persist the object
		String colorTypeOID = FormatHelper.getObjectId(newColorFlexTypeObj);
		endOfEndColorObj = (LCSColor)RetypeLogic.changeType(endOfEndColorObj, colorTypeOID, false);
		
		//This method is using to set End of End Color data on New Color type
		endOfEndColorObj = updateEndOnEndColorAttributesData(endOfEndColorObj,masterColorObj,baseFabric,paletteObj,seasonObj,designName,delivery,designColorway,assortment,productLine,repeatWidth,repeatLength,program,garmentProducts);
		
		//This method is using to set End of End Color Pitch Sheet data on New Color type Pitch Sheet MOA Data.
		updatePitchSheetMOAData(moaObjectCollection,endOfEndColorObj);
		
		LCSColorHelper.service.saveColor(endOfEndColorObj);
		//LCSLogic.persist(endOfEndColorObj);
	
	}
	
	/**
	 * This function is using to get  MOA Pitch Sheet of End of End Color object data.
	 * @param endOfEndColorObj - LCSColor
	 * @return moaObjectCollections - Collection<LCSMOAObject>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static Collection<LCSMOAObject> getMOAObjectsFromEndOfEndColor(LCSColor endOfEndColorObj)throws WTException,WTPropertyVetoException
	{
		Collection<LCSMOAObject> moaObjectCollections = new ArrayList<LCSMOAObject> ();
		LCSMOATable boMOATable = (LCSMOATable) endOfEndColorObj.getValue("hbiPitchSheet");
		if(boMOATable != null)	
		{
			Collection<FlexObject> rowIdColl = boMOATable.getRows();
			for(FlexObject flexObj : rowIdColl)
			{
				String key = flexObj.getString("OID");
				LCSMOAObject moaObject = (LCSMOAObject) LCSMOAObjectQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:"+key);
				moaObjectCollections.add(moaObject);
			}
			System.out.println("!!!!!!!!!moaObjectCollections!!!!!!!"+moaObjectCollections.size());
		}
		return moaObjectCollections;
	}	
	
	/**
	 * This function is using to set  End of End Color objects data on New Color type.
	 * @param endOfEndColorObj - LCSColor
	 * @return moaObjectCollections - Collection<LCSMOAObject>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static LCSColor updateEndOnEndColorAttributesData(LCSColor endOfEndColorObj,LCSColor masterColorObj,String baseFabric,LCSPalette paletteObj,LCSSeason seasonObj,String designName,String deliveryName,String designColorway,String assortment,String productLineName,double repeatWidth,double repeatLength,String program,String garmentProducts) throws WTException,WTPropertyVetoException
	{
		if(masterColorObj != null)
		{
			endOfEndColorObj.setValue("hbiMasterColorStdReference",masterColorObj);
		}
		
		if(FormatHelper.hasContent(baseFabric))
		{
			endOfEndColorObj.setValue("hbiBaseFabric",baseFabric);
		}
		
		if(paletteObj != null)
		{
			endOfEndColorObj.setValue("hbiPalette",paletteObj);
		}
		
		if(seasonObj != null)
		{
			endOfEndColorObj.setValue("hbiSeason",seasonObj);
		}
		
		if(FormatHelper.hasContent(designName))
		{
			endOfEndColorObj.setValue("hbiDesignName",designName);
		}
		
		if(FormatHelper.hasContent(deliveryName))
		{
			endOfEndColorObj.setValue("hbiDelivery",deliveryName);
		}
		
		if(FormatHelper.hasContent(designColorway))
		{
			endOfEndColorObj.setValue("hbiDesignColorway",designColorway);
		}
		
		if(FormatHelper.hasContent(assortment))
		{
			endOfEndColorObj.setValue("hbiAssortment",assortment);
		}
		
		if(FormatHelper.hasContent(productLineName))
		{
			endOfEndColorObj.setValue("hbiProductLine",productLineName);
		}
		
		if(repeatWidth != 0.0)
		{
			endOfEndColorObj.setValue("hbiDesignWidth",repeatWidth);
		}
		
		if(repeatLength != 0.0)
		{
			endOfEndColorObj.setValue("hbiDesignLength",repeatLength);
		}
		
		if(FormatHelper.hasContent(program))
		{
			endOfEndColorObj.setValue("hbiProgram",program);
		}

		if(FormatHelper.hasContent(garmentProducts))
		{
			endOfEndColorObj.setValue("hbiGarmentProducts",garmentProducts);
		}
		
		return endOfEndColorObj;
	}

	public static void updatePitchSheetMOAData(Collection<LCSMOAObject> moaObjectCollections,LCSColor endOfEndColorObj)throws WTException,WTPropertyVetoException
	{
		String moaComments = "";
		LCSColor moaColorObj = null;
		String sortingNumber = "1";
		for(LCSMOAObject moaObject : moaObjectCollections)
		{
			moaComments = (String)moaObject.getValue("hbiComments");
			moaColorObj = (LCSColor)moaObject.getValue("color");
			moaObject.setFlexType(endOfEndColorObj.getFlexType().getAttribute("hbiPitchSheet").getRefType());
			moaObject.setOwner(endOfEndColorObj);
			//moaObject.getOwningAttribute()setOwnerAttribute(endOfEndColorObj.getFlexType().getAttribute("hbiPitchSheet"));
		
			moaObject.setBranchId(Integer.parseInt(sortingNumber));
			moaObject.setDropped(false);
			moaObject.setSortingNumber(Integer.parseInt(sortingNumber));
			moaObject.getFlexType().getAttribute("hbiComments").setValue(moaObject, moaComments);
			if(moaColorObj != null)
			{
				moaObject.getFlexType().getAttribute("color").setValue(moaObject, moaColorObj);
			}	
			LCSMOAObjectLogic.persist(moaObject);
		}	
	
	}
	
	
}