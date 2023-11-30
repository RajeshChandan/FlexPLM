package com.custom.wc.exportimport;

// To Execute:
//      windchill com.custom.wc.exportimport.ProductObjectExport 1688768
import com.custom.wc.exportutilities.ExportUtilities;
import com.lcs.wc.db.*;
import com.lcs.wc.flexbom.*;
import com.lcs.wc.flextype.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.*;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.*;
//import com.lcs.wc.sourcing.LCSSourcingConfig;
//import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
//import com.lcs.wc.specification.FlexSpecification;
//import com.lcs.wc.supplier.LCSSupplier;
//import com.lcs.wc.supplier.LCSSupplierMaster;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.util.LCSProperties;
//import com.lcs.wc.util.VersionHelper;
import java.text.SimpleDateFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import wt.fc.WTObject;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import java.util.*;
import com.lcs.wc.flextype.FlexTypeCache;

/**
 *
 * @author daboisse
 */
public class ProductObjectExport {

    protected static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(LCSProperties.get("com.custom.exportimport.timeFormat", "MMM dd yyyy hh:mm:ss"));
    protected static boolean DEBUG = LCSProperties.getBoolean("com.custom.wc.exportimport.debugFlag");

    static {
        DATE_FORMAT.setTimeZone(WTContext.getContext().getTimeZone());
    }

    public static String hbiProductExtractByObject(WTObject object, String materialAction) throws LCSException, WTException, WTPropertyVetoException, Exception {
        LCSProduct product = (LCSProduct) object;
      //  product.getSeasonMaster()
        String returnXml = "";
        FlexType ftProduct = product.getFlexType();
        Vector objectsToExport = new Vector();

        LCSSeason season = product.findSeasonUsed();

        if (!(season == null)) {
           // System.out.println("Season = " + season.getAtt1());
            LCSSeasonProductLink spl = LCSSeasonQuery.findSeasonProductLink(product, season);
            System.out.println("The Date = " + spl.getValue("hbiLabelBomFinalized"));
            if (spl.getValue("hbiLabelBomFinalized") != null) {
                objectsToExport.add(product);
                objectsToExport.add(spl);
                objectsToExport.add(season);
            }
        }

//
//        LCSSourcingConfigQuery SOURCE_QUERY = new LCSSourcingConfigQuery();
//
//        Collection col = SOURCE_QUERY.getSourcingConfigForProductSeason(product, season);
//
//        LCSSourcingConfig sCfg; //= SOURCE_QUERY.getPrimarySourceForProduct(product);
//        for (Object obj : col) {
//            sCfg = (LCSSourcingConfig) obj;
//            System.out.println("\t\tAdding Sourcing Config\n");
//            objectsToExport.add(sCfg);
//            if (sCfg.getValue("hbiPatternSpec") != null) {
//                FlexSpecification spec = (FlexSpecification) LCSQuery.findObjectById(sCfg.getValue("hbiPatternSpec").toString());
//                System.out.println("\t\tAdding Spec\n");
//                objectsToExport.add(spec);
//            }
//        }
        if (!(objectsToExport.isEmpty())) {
            LCSFlexBOMQuery lbq = new LCSFlexBOMQuery();
            Vector bomParts = (Vector) lbq.findBOMPartsForOwner(product);
            System.out.println("bomParts count = " + bomParts.size());

            for (int j = 0; j < bomParts.size(); j++) {
                FlexBOMPart bp = (FlexBOMPart) bomParts.elementAt(j);
                // Looking for "Garment"
                if (bp.getName().toString().contentEquals("Garment")) {
                    Vector flexBomLinks = (Vector) LCSFlexBOMQuery.findFlexBOMLinks(bp, null, null, null, null, null, "WIP_ONLY", null, true, null, null, null, null);

                    for (int k = 0; k < flexBomLinks.size(); k++) {
                        FlexBOMLink fbl = (FlexBOMLink) flexBomLinks.elementAt(k);
                        	
                        LCSMaterialMaster pm = fbl.getChild(); //(LCSMaterialMaster) materialObj.getMaster();
                        //wt.part.WTPartMaster pm = fbl.getChild();
                        LCSMaterial material;
                        if (pm != null) {
                            if (!(pm.getName().toString().contentEquals("material_placeholder"))) {
                                if (!objectsToExport.contains(pm)) {

                                //    System.out.println("\t\tAdding Flex BOM Link " + fbl.getAtt1() + "\n");
                                    material = (LCSMaterial) com.lcs.wc.util.VersionHelper.latestIterationOf(pm);
                                    objectsToExport.add(material);
                                    objectsToExport.add(fbl);
                                }
                            }
                        }
                    }
                }
            }
            returnXml = createTheXml(objectsToExport, "productType", ftProduct.toString());
        }
        FlexTypeCache.clearCache();

        return returnXml;
    }

    public static String createTheXml(Vector objectList, String rootName, String objectType) throws Exception {
        String theXml = "";
        System.out.println("Object Count = " + objectList.size());

        Document xmlDoc = new com.sun.org.apache.xerces.internal.dom.DocumentImpl();
        Element root = xmlDoc.createElement("rootName");
        root.setAttribute("objType", objectType);
        xmlDoc.appendChild(root);

        com.lcs.wc.util.XMLHelper xmlHelper = new com.lcs.wc.util.XMLHelper();
        //com.lcs.wc.util.XMLWriter xmlHelper = new com.lcs.wc.util.XMLWriter();
        System.out.println("XML Generation Started   At " + DATE_FORMAT.format(new Date()));

        theXml = xmlHelper.generateXML(objectList, true, xmlDoc, root);
        System.out.println("XML Generation Completed At " + DATE_FORMAT.format(new Date()));
        System.out.println("----------------------------------------------------------------");
        return theXml.replaceFirst("UTF-8", "UTF-16");  // Web Service only takes UTF-16
    }

    public static void main(String[] args) throws Exception {

        LCSProduct product = (LCSProduct) LCSQuery.findObjectById("com.lcs.wc.product.LCSProduct:" + args[0]);
        String theXml = hbiProductExtractByObject((WTObject) product, "Create");
        System.out.println("XML = " + theXml);
        System.exit(0);
    }
}
