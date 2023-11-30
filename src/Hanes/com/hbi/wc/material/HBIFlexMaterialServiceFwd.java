package com.hbi.wc.material;

import com.lcs.wc.sourcing.LCSSourcingConfig;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;
import wt.fc.WTObject;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
//import wt.part.WTPartMaster;
import wt.services.Manager;
import wt.services.ManagerService;
import wt.services.ManagerServiceFactory;
import wt.util.WTException;

public class HBIFlexMaterialServiceFwd implements RemoteAccess, HBIFlexMaterialService, Serializable {

	static final boolean SERVER = RemoteMethodServer.ServerFlag;
	private static final String FC_RESOURCE = "wt.fc.fcResource";
	private static final String CLASSNAME = com.hbi.wc.material.HBIFlexMaterialServiceFwd.class.getName();


	public HBIFlexMaterialServiceFwd() {
    }


	private static Manager getManager() throws WTException {
    
		//Manager manager = ManagerServiceFactory.getDefault().getManager(HBIFlexSpecService.class);
		Manager localmanager = ManagerServiceFactory.getDefault().getManager(HBIFlexMaterialService.class);
        if (localmanager == null) {
        Object[] param = { "com.hbi.wc.material.HBIFlexMaterialService" };
          throw new WTException("wt.fc.fcResource", "40", param);
        }
       return localmanager;
    }
// Changed for ticket 141702-15(added isGenericMatSpec )
	public String generateHbiMaterialTechPack(String timeToLive, String mSupplierId, String isGenericMatSpec) throws WTException {
		if (SERVER)   
	      return ((HBIFlexMaterialService)getManager()).generateHbiMaterialTechPack(timeToLive, mSupplierId, isGenericMatSpec);
		try	{
		//System.out.println("inside service fwd");
			Class[] argTypes = { String.class, String.class, String.class };
			Object[] args = { timeToLive, mSupplierId, isGenericMatSpec };
			return ((String)RemoteMethodServer.getDefault().invoke("generateHbiMaterialTechPack", null, this, argTypes, args));
		}catch (InvocationTargetException e){
			Throwable targetE = e.getTargetException();
			if (targetE instanceof WTException)
			throw ((WTException)targetE);
			Object[] param = { "generateHbiMaterialTechPack" };
			throw new WTException(targetE, "wt.fc.fcResource", "0", param);
		}catch (RemoteException rme) {
			Object[] param = { "generateHbiMaterialTechPack" };
			throw new WTException(rme, "wt.fc.fcResource", "0", param);
		}
   }
   
}// end class