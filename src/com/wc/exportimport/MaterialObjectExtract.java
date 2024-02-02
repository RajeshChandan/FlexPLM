package com.custom.wc.exportimport;

import com.custom.wc.exportutilities.ExportUtilities;

import com.lcs.wc.db.*;
import com.lcs.wc.flextype.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.*;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.supplier.LCSSupplierMaster;
import com.hbi.wc.util.*;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.util.LCSProperties;
import java.text.SimpleDateFormat;
import java.util.*;
import org.w3c.dom.*;
//import org.w3c.dom.Element;
import wt.fc.WTObject;
import wt.session.SessionContext;
import wt.util.*;
import wt.method.MethodContext;
import wt.method.RemoteMethodServer;
import wt.httpgw.GatewayAuthenticator;


/**
 * This class is used to extract a material object in response to a
 * create/update/delete.
 *
 * @author daboisse updated: 7/18/2012 to get around PTC problem with
 * expandReferences in generateXml
 *
 */
public class MaterialObjectExtract {

    protected static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(LCSProperties.get("com.custom.exportimport.timeFormat", "MMM dd yyyy hh:mm:ss"));
    private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "Administrator");
    private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "Administrator");
    
    static {
        DATE_FORMAT.setTimeZone(WTContext.getContext().getTimeZone());
    }

    public static String hbiMaterialExtractByObject(WTObject object, String materialAction) throws LCSException, WTException, WTPropertyVetoException, Exception {

        System.out.println("PTCGSO-MaterialObjectExtract.hbiMaterialExtractByObject Start of method " + DATE_FORMAT.format(new Date()));
        
        // The following 4 lines are needed to use an alternate user for running this code
        // This is to take advantage of security to prevent looking up unused MoAs
		
		 //MethodContext mcontext = new MethodContext((String) null, (Object) null);
        //SessionContext sessioncontext = SessionContext.newContext();
		
        RemoteMethodServer remoteMethodServer = RemoteMethodServer.getDefault();
        GatewayAuthenticator auth = new GatewayAuthenticator();
        auth.setRemoteUser(CLIENT_ADMIN_USER_ID);
        remoteMethodServer.setAuthenticator(auth);  // bypass
        
       

       // remoteMethodServer = RemoteMethodServer.getDefault();
       // remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
       // remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);
        

        LCSMaterial materialObject = (LCSMaterial) object;
        FlexType ftMaterial = materialObject.getFlexType();
        Vector objectsToExport = new Vector();
        LCSSupplier supplier;

        String materialType = "Material_" + ftMaterial.getFullName();
        
        // The sleep is to allow the Workflow process to finish, otherwise this code doesn't get the latest
        Thread.sleep(3000);
        System.out.println("PTCGSO-MaterialObjectExtract.hbiMaterialExtractByObject after thread sleep " + DATE_FORMAT.format(new Date()));

        SearchResults mSupp = LCSMaterialSupplierQuery.findMaterialSuppliers(materialObject);
        System.out.println("PTCGSO-MaterialObjectExtract.hbiMaterialExtractByObject after getting all Materialsupplier " + DATE_FORMAT.format(new Date()));

        // If there are no suppliers set up for this material, then bail out
        if (mSupp.getResultsFound() > 0) {

            objectsToExport.add(materialObject);
            Vector suppRes = mSupp.getResults();
            for (int j = 0; j < suppRes.size(); j++) {
                FlexObject obj = (FlexObject) suppRes.elementAt(j);
                LCSMaterialSupplier materialSupplier = (LCSMaterialSupplier) LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialSupplier:" + obj.getString("LCSMATERIALSUPPLIER.IDA2A2"));
                System.out.println("PTCGSO-MaterialObjectExtract.hbiMaterialExtractByObject after running query for each MaterialSupplier  " + DATE_FORMAT.format(new Date()));

                if (!materialSupplier.isPlaceholder()) {

                    // Business Rule:  Only Add if the Supply Chain Loaded Date is not null
                    if ((materialSupplier.getValue("hbiSupplyChainLoadPMAct") != null)) {
                        supplier = (LCSSupplier) VersionHelper.latestIterationOf((LCSSupplierMaster) LCSQuery.findObjectById("OR:com.lcs.wc.supplier.LCSSupplierMaster:" + obj.getString("LCSSUPPLIERMASTER.IDA2A2")));
                        System.out.println("PTCGSO-MaterialObjectExtract.hbiMaterialExtractByObject after running query for each Supplier  " + DATE_FORMAT.format(new Date()));

                        // Only add supplier and Material\Supplier if it is a Supplier\Supplier  -- Removed 10/12/12

                        //  FlexType suppFt = supplier.getFlexType();
                        //  if (suppFt.getTypeDisplayName().equalsIgnoreCase("Supplier")) {
                        objectsToExport.add(supplier);
                        objectsToExport.add(materialSupplier);
                        //  }
                    }
                }
                obj = null;
                materialSupplier = null;
                supplier = null;
            }
            suppRes = null;
        }
        String theXml = "";
        System.out.println("Object Count = " + objectsToExport.size());
        
        // If the only object in the Vector is a material, then bail out
		// Adexa needs the fabrics to flow to MDS. CA # 92821-15
        if (objectsToExport.size() >= 1) {
            HBIXMLHelper xmlHelper = new HBIXMLHelper();
            Document xmlDoc = new com.sun.org.apache.xerces.internal.dom.DocumentImpl();
            Element root = xmlDoc.createElement("MaterialRoot");

            root.setAttribute("matType", materialType);
            xmlDoc.appendChild(root);
            System.out.println("PTCGSO-******MaterialObjectExtract.hbiMaterialExtractByObject before generateXML  " + DATE_FORMAT.format(new Date()));

            theXml = xmlHelper.generateXML(objectsToExport, true, xmlDoc, root);
            System.out.println("PTCGSO-******MaterialObjectExtract.hbiMaterialExtractByObject after generateXML  " + DATE_FORMAT.format(new Date()));

            xmlDoc = null;
            xmlHelper = null;
            objectsToExport = null;
        }

        return theXml.toString();  // Web Service only takes UTF-16
    }

    public static String hbiMaterialSupplierExtractByObject(WTObject object, String materialAction) throws LCSException, WTException, WTPropertyVetoException, Exception {

        // Just find the LCSMaterial Object and call the material Extract routine
        System.out.println("PTCGSO-MaterialObjectExtract.hbiMaterialSupplierExtractByObject Start of method " + DATE_FORMAT.format(new Date()));

        LCSMaterialSupplier materialSupplier = (LCSMaterialSupplier) object;
        LCSMaterial materialObj = null;

        String matSuppXml = "";
        if (!materialSupplier.isPlaceholder()) {
            // Now find the material
           // wt.part.WTPartMaster matMaster = (wt.part.WTPartMaster) materialSupplier.getMaterialMaster();
//        	LCSMaterialSupplierMaster matMaster = (LCSMaterialSupplierMaster) materialSupplier.getMaterialMaster();
//materialObj = ExportUtilities.getMaterialObject(matMaster.getName());
//Code Change for 17 digit SKU - To get the material object from master
			materialObj = (LCSMaterial)VersionHelper.latestIterationOf(materialSupplier.getMaterialMaster());
            System.out.println("Material Name from Material Supplier = "  + materialObj.getValue("name"));
            matSuppXml = hbiMaterialExtractByObject(materialObj, materialAction);
        }
        System.out.println("PTCGSO-MaterialObjectExtract.hbiMaterialSupplierExtractByObject End of method " + DATE_FORMAT.format(new Date()));

        return matSuppXml;
    }
}
