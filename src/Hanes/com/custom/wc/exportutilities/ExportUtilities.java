/**
 * A collection of utilities to be used with the Export classes.
 *
 * @author David Boissey
 *
 */
package com.custom.wc.exportutilities;

import com.lcs.wc.db.*;

import com.lcs.wc.flextype.*;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.*;
import com.lcs.wc.util.XMLHelper;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExportUtilities {

    /**
     * Get the Flex Type Path ID from a name
     *
     * @author daboisse * t: Make
     */
    public static String getPathIdForTable(String tableName) throws Exception {
        FlexType nPath;

        nPath = FlexTypeCache.getFlexTypeFromPath(tableName);

        return nPath.getTypeIdPath();
    }

    /**
     * Creates a string for an xml file with the date and time appended.
     *
     * @author David Boissey
     * @param pathIn       The path where the file is to be created
     * @param baseFileName The base name of the file
     * @return
     * @throws Exception
     */
    public static String getFileName(String pathIn, String baseFileName) throws Exception {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");
        String fileName = pathIn + "\\" + baseFileName + sdf.format(cal.getTime()) + ".xml";
        return fileName;
    }

    /**
     * This function will open a file, creating it if necessary or erasing it if
     * it exists.
     *
     * @param , filename
     */
    public static PrintWriter openFile(String fileName) throws Exception {
        PrintWriter outputStream;
        outputStream = new PrintWriter(new FileWriter(fileName, false));
        return outputStream;
    }

    /**
     * Converts a Flex object to an XMLstring
     *
     * @param objIn a Flex Object
     * @return
     * @throws Exception
     */
    public static String objectToXml(wt.fc.WTObject objIn) throws Exception {
        XMLHelper xmlHelper = new XMLHelper();

        // Setting the second arg to true causes the MOAs to expand
        String result = xmlHelper.generateXML(objIn, (boolean) false);
        return result;
    }

    /**
     * Return the number of lines in a file.
     *
     * @author David Boissey
     * @param filePath
     * @return integer liner count
     * @throws Exception
     */
    public static int GetLineCount(String filePath) throws Exception {
        BufferedReader inFile = new BufferedReader(new FileReader(filePath));
        int lineCount = 0;

        while (inFile.readLine() != null) {
            lineCount++;
        }
        inFile.close();
        return lineCount;
    }

    /**
     * Get the attribute (att1, att2, etc.) name given a key value
     *
     * @author David Boissey
     * @param tableName param attributeName
     * @return the attribute name (String)
     * @throws Exception
     */
    public static String FindAttributeName(String tableName, String attributeName) throws Exception {

        FlexType flextype = FlexTypeCache.getFlexTypeFromPath(tableName);
        FlexTypeAttribute attribute = flextype.getAttribute(attributeName);
        return attribute.getVariableName();

    }

    public static String MoaObjectXml(FlexObject moaData, String moaTag) throws Exception {

        com.lcs.wc.util.XMLWriter xmlHelper = new com.lcs.wc.util.XMLWriter();
        Document xmlDoc = new DocumentImpl();
        Element root = xmlDoc.createElement("MoaRecords");
        root.setAttribute("moaType", moaTag);
        xmlDoc.appendChild(root);

        xmlHelper.flexObjectToXml(moaData, root, xmlDoc);

        String newXml = xmlHelper.getXMLString();
        return newXml;
    }

//    public static String relationshipRecordToXml(FlexObject moaData, String flexTag) throws Exception {
//
//        com.lcs.wc.util.XMLWriter xmlHelper = new com.lcs.wc.util.XMLWriter();
//        Document xmlDoc = new DocumentImpl();
//        Element root = xmlDoc.createElement("Connections");
//        root.setAttribute("moaType", flexTag);
//        xmlDoc.appendChild(root);
//
//        xmlHelper.flexObjectToXml(moaData, root, xmlDoc);
//
//        String newXml = xmlHelper.getXMLString();
//        return newXml;
//    }
        public static Vector moaObjectVector(FlexTyped object) throws Exception {
        FlexTypeAttribute attribute;
        Vector moaVector = new Vector();
   
        Vector attributes = (Vector) object.getFlexType().getAllAttributesUsedBy(object);
        for (int i = 0; i < attributes.size(); i++) {
            attribute = (FlexTypeAttribute) attributes.elementAt(i);
            if (attribute.getAttVariableType().contentEquals("multiobject")) {
                if (!(attribute.getValue(object).toString().isEmpty())) {
//                    System.out.println(attribute.getAttDisplay() + " = "
//                            + attribute.getValue(object)
//                            + " Key = " + attribute.getAttKey()
//                            + "\nType = " + attribute.getAttVariableType());
                    com.lcs.wc.moa.LCSMOATable moaTable = new com.lcs.wc.moa.LCSMOATable(object, attribute.getAttKey());
                    System.out.println("moaTable = " + attribute.getAttKey());
                    FlexObject obj;
                    Vector eachMoa = (Vector) moaTable.getRows();
                    for (int j = 0; j < eachMoa.size(); j++) {
                        obj = (FlexObject) eachMoa.elementAt(j);
                        obj.setData ("moaName", attribute.getAttKey());
                        moaVector.add (obj);

                    }

                }
            }
        }
        return moaVector;
    }
        
    public static LCSMaterial getMaterialObject(String keyName) throws Exception {
        String tableName = "LCSMaterial";
        PreparedQueryStatement statement = new PreparedQueryStatement();
        statement.appendFromTable(tableName);

        statement.appendSelectColumn(tableName, "classnamea2a2");
        statement.appendSelectColumn(tableName, "ida2a2");
        statement.appendSelectColumn(tableName, "*");
        //        Use the following to get the latest iteration
        statement.addLatestIterationClause(tableName);
//        statement.appendAndIfNeeded();
//        statement.appendCriteria(new Criteria(tableName, "flexTypeIdPath", "?", Criteria.EQUALS), ExportUtilities.getPathIdForTable("Material\\Fabric Make"));
        statement.appendAndIfNeeded();
        statement.appendCriteria(new Criteria(tableName, "att1", "?", Criteria.EQUALS), keyName);

        LCSMaterial material;
        Vector objects = (Vector) LCSQuery.getObjectsFromResults(statement, "OR:com.lcs.wc.material.LCSMaterial:", "LCSMaterial" + ".IDA2A2");
        
        if (objects.size() != 1) {
           throw new Exception ("LCSMaterial Keyname (" + keyName +") not found");
        }
        else {
            material = (LCSMaterial) objects.elementAt(0);
        }

        return material;
    }
    
    public static Boolean materialHasApprovedSupplier(LCSMaterial material) throws Exception {

        Boolean result = false;
        SearchResults mSupp = LCSMaterialSupplierQuery.findMaterialSuppliers(material);
        if (mSupp.getResultsFound() > 0) {

            Vector suppRes = mSupp.getResults();
            for (int j = 0; j < suppRes.size(); j++) {
                FlexObject obj = (FlexObject) suppRes.elementAt(j);
                LCSMaterialSupplier materialSupplier = (LCSMaterialSupplier) LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialSupplier:" + obj.getString("LCSMATERIALSUPPLIER.IDA2A2"));
                if (!materialSupplier.isPlaceholder()) {

                    // Business Rule:  Only Add if the Approval Spec Date is not null
                    if ((materialSupplier.getValue("hbiSupplyChainLoadPMAct") != null)) {
                        result = true;
                    }
                }
            }

        }
        return result;
    }
}
