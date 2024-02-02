package com.hbi.wc.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.color.LCSColorHelper;
import com.lcs.wc.color.LCSPalette;
import com.lcs.wc.color.LCSPaletteLogic;
import com.lcs.wc.color.LCSPaletteQuery;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierMaster;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

public class HBIMovePaletteDataUtility implements RemoteAccess
{
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "pass2014a");
	private static RemoteMethodServer remoteMethodServer;
	
	/* Default executable function of the class HBIMovePaletteDataUtility */
	public static void main(String[] args) 
	{
		LCSLog.debug("### START HBIMovePaletteDataUtility.main() ###");
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
	        
	        getUserListValue();
	        System.exit(0);
		}
		catch (Exception exception) 
		{
			exception.printStackTrace();
			System.exit(1);
		}
		
		LCSLog.debug("### END HBIMovePaletteDataUtility.main() ###");
	}
	
	public static void getUserListValue() throws WTException, WTPropertyVetoException
	{
		Vector<FlexObject> listOfObjects = null;
		String paletteMergeWith = "Activewear Hanes S3 2018 Mens";
		String paletteToBeMerged = "Activewear Hanes S3 2018 Mens DELETE";
		LCSPalette subPalette = null;
		Collection<String> listOfMatSuppIDs = null;
		Collection<LCSMaterialColor> listOfMatColorObjects = null;
		
		FlexType paletteFlexType = FlexTypeCache.getFlexTypeFromPath("Palette");
		LCSPalette masterPalette = new LCSPaletteQuery().findPaletteByNameType(paletteMergeWith, paletteFlexType); 
		System.out.println("!!!!!!!!!!!!!!!!!!!!! paletteMergeWith !!!!!!!!!!!!!!!!!!!!!" +masterPalette.getName());
		
		LCSPalette secondPalette = new LCSPaletteQuery().findPaletteByNameType(paletteToBeMerged, paletteFlexType); 
		//System.out.println("!!!!!!!!!!!!!!!!!!!!! paletteToBeMerged !!!!!!!!!!!!!!!!!!!!!" +secondPalette);
		System.out.println("!!!!!!!!!!!!!!!!!!!!! paletteToBeMerged !!!!!!!!!!!!!!!!!!!!!" +secondPalette.getName());
		
		if(secondPalette != null )
		{
			
			
				
				//getColorsFromSubOalette(secondPalette,masterPalette);
				
				
				listOfMatSuppIDs = getMaterialSuppliersFromSubPalette(secondPalette);
				new LCSPaletteLogic().addMaterials(listOfMatSuppIDs, masterPalette);
					
				//listOfMatColorObjects = getMaterialColorsFromSubPalette(secondPalette);
				
			//	new LCSPaletteLogic().addMaterialColorsToPalette(listOfMatColorObjects,masterPalette);
			//	LCSColorHelper.service.addMaterialColorsToPalette(listOfMatColorObjects,masterPalette);
	            
				//new LCSPaletteLogic().delete(secondPalette);
			
		}
	}
		
	
	

	public static void getColorsFromSubOalette(LCSPalette subPale,LCSPalette masterPalette) throws WTException, WTPropertyVetoException
	{
		Collection<FlexObject> colorPalettes =new LCSPaletteQuery().findColorsForPalette(subPale);
		for(FlexObject colorObject : colorPalettes)
		{
			LCSColor colorObj = (LCSColor) LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSColor:"+colorObject.getString("LCSColor.IDA2A2"));
			new LCSPaletteLogic().addColorToPalette(colorObj,masterPalette);
		}	

	}
	
	public static Collection<LCSMaterialColor> getMaterialColorsFromSubPalette(LCSPalette subPale) throws WTException, WTPropertyVetoException
	{
		Collection<LCSMaterialColor> listOfMatColorObjects =new ArrayList<LCSMaterialColor>();
		Collection<?> materialColPale = new LCSPaletteQuery().findMaterialColorsForPalette(subPale);
		for(Object obj : materialColPale)
		{
			FlexObject flexObj = (FlexObject) obj;
			LCSMaterialColor matcolorObj = (LCSMaterialColor) LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialColor:"+flexObj.getString("LCSMaterialColor.IDA2A2"));
			listOfMatColorObjects.add(matcolorObj);
		}
		System.out.println("!!!!!!!!!!!!!!!!!!!!! materialColPale !!!!!!!!!!!!!!!!!!!!!" +listOfMatColorObjects.size()); 
		return listOfMatColorObjects;	
		
	}

	
	public static Collection<String> getMaterialSuppliersFromSubPalette(LCSPalette subPale) throws WTException, WTPropertyVetoException
	{
		Collection<String> listOfMatSuppIds =new ArrayList<String>();
		Collection<?> materialSupp = new LCSPaletteQuery().findMaterialSupplierForPalette(subPale);
		String materialSupplierMasterID = "";
		LCSMaterialSupplier matSupplierObj = null;
		LCSMaterialSupplierMaster materialSupplierMasterObj = null;
		for(Object	obj : materialSupp)
		{
			FlexObject flexObject = (FlexObject) obj;
			matSupplierObj = (LCSMaterialSupplier) LCSQuery.findObjectById("VR:com.lcs.wc.material.LCSMaterialSupplier:"+flexObject.getString("LCSMaterialSupplier.BRANCHIDITERATIONINFO"));
			matSupplierObj = (LCSMaterialSupplier) VersionHelper.latestIterationOf(matSupplierObj);
			materialSupplierMasterObj = (LCSMaterialSupplierMaster)matSupplierObj.getMaster();
			materialSupplierMasterID = FormatHelper.getObjectId(materialSupplierMasterObj);
			listOfMatSuppIds.add(materialSupplierMasterID);
		}
		System.out.println("!!!!!!!!!!!!!!!!!!!!! listOfMatSuppIds !!!!!!!!!!!!!!!!!!!!!" +listOfMatSuppIds.size()); 
		return listOfMatSuppIds;	
	}
}