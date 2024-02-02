/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl.util;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.AttributeValueList;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.ACLHelper;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.UserCache;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import wt.part.WTPartMaster;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 *
 * @author UST
 */
public class TransformUtil {

    public boolean transformFlexTyped(FlexTyped flexTypedObj) throws Exception {

        
            FlexType type;
            //System.out.println("flexTypedObj =" + flexTypedObj);
            type = flexTypedObj.getFlexType();
       
           // System.out.println("FlexType =" + type);
            //System.out.println("FlexTyped Name =" + flexTypedObj);
 
            Collection<FlexTypeAttribute> keyAttributes = type.getAllAttributesUsedBy(flexTypedObj);
            Iterator<FlexTypeAttribute> keys = keyAttributes.iterator();
            FlexTypeAttribute att;
            while (keys.hasNext()) {
                att = (FlexTypeAttribute) keys.next();
              //  System.out.println("inside transformFlexTyped "+att.getAttColumn()+" "+att.getAttKey()+" "+att.getAttDisplayColumn());
              //  System.out.println("inside transformFlexTyped second print "+att.getDisplayValue(flexTypedObj));
                String stringValue;
				
				//Commenting line because dont want to skip userList data
				if (att.getAttVariableType().equals("userList")) {
					continue;
				}
                stringValue = att.getStringValue(flexTypedObj);
                //System.out.println("inside transformFlexTyped third print "+att.getStoredValue(flexTypedObj));
                //System.out.println("inside transformFlexTyped fourth print "+att.getStringValue(flexTypedObj));
                if (!att.isAttEnabled()
                        || !ACLHelper.hasViewAccess(att)) {
                    continue;
                }
                if ("composite".equals(att.getAttVariableType())) {

                    AttributeValueList list = att.getAttValueList();
                    String value = MOAHelper.parseOutDelims(stringValue, true);
                    StringTokenizer parser = new StringTokenizer(value, ",");
                    String token;
                    String per;
                    String item;
                    String hold = "";
                    while (parser.hasMoreElements()) {
                        token = parser.nextToken();
                        int index = token.indexOf("%");
                        per = token.substring(0, index + 1);
                        item = token.substring(index + 1).trim();
                        try {
                            item = list.getValue(item, ClientContext.getContext().getLocale());
                        } catch (LCSException e) {
                        }

                        if (parser.hasMoreElements()) {
                            hold = hold + per + " " + item + ",";
                        } else {
                            hold = hold + per + " " + item;
                        }
                    }
                    String display = hold;
                    //Set the transformed attribute value to the supplier object        
                    flexTypedObj.setValue(att.getAttKey(), display);

                } else if ("choice".equals(att.getAttVariableType()) || "driven".equals(att.getAttVariableType())) {

                    String display = att.getDisplayValue(flexTypedObj);
                    flexTypedObj.setValue(att.getAttKey(), display);

                } else if ("boolean".equals(att.getAttVariableType())) {

                    String display = att.getStringValue(flexTypedObj);

                    if ("1".equals(display) || "true".equals(display)) {
                        display = "true";
                    } else {
                        display = "false";
                    }
                    flexTypedObj.setValue(att.getAttKey(), display);

                } else if (att.getAttVariableType().equals("userList")) {

                    String display = "";
                    stringValue = att.getStringValue(flexTypedObj);
                    if (FormatHelper.hasContent(stringValue)) {
                        FlexObject user = UserCache.getUser(stringValue);
                        if (user != null) {
                            display = user.getString(UserCache.OID);
                            if (user.getBoolean(UserCache.DELETED)) {
                                display = display;
                            }
                        }
                    }
                    flexTypedObj.setValue(att.getAttKey(), display);

                } else if ("moaList".equals(att.getAttVariableType())) {

                    String display = "";
                    stringValue = att.getStringValue(flexTypedObj);
                    display = MOAHelper.parseOutDelimsLocalized(stringValue, true, att.getAttValueList(), ClientContext.getContext().getLocale());
                    flexTypedObj.setValue(att.getAttKey(), display);

                } else if ("Text".equals(att.getAttVariableType())
                        || "Derived String".equals(att.getAttVariableType())
                        || "Text Area".equals(att.getAttVariableType())
                        || "Unit Of Measure".equals(att.getAttVariableType())
                        || "URL".equals(att.getAttVariableType())) {
                    String display = "";
                    if (FormatHelper.hasContent(stringValue)) {
                        display = stringValue;
                        flexTypedObj.setValue(att.getAttKey(), display);
                    }
                }
                /*else if ("object_ref".equals(att.getAttVariableType()) ||
                 "object_ref_list".equals(att.getAttVariableType())) {
                
                 ForiegnKeyDefinition fkDef = att.getRefDefinition();
                 String display = "";
                 if(fkDef.getFlexTypedDisplay() != null){
                 display = "" + (flexTypedObj).getValue(fkDef.getFlexTypedDisplay());
                 }
                 flexTypedObj.setValue(att.getAttKey(), display);
                
                 } 
                 else if ("image".equals(att.getAttVariableType())) {
                
                 String display = att.getStringValue(flexTypedObj);
                 flexTypedObj.setValue(att.getAttKey(), display);
                
                 }*/
            }
        
        return true;
    }

    public String getTableName(String oid) throws WTException {
        String tableName = null;
        if (oid.contains("LCSColor")) {
            tableName = "HBICOLOR";
        }
        if (oid.contains("LCSCountry")) {
            tableName = "HBICOUNTRY";
        }
        if(oid.contains("LCSMaterialMaster" )) {
        	System.out.println("@@@@@@@@@@@@@@@@@@@@LCSMaterialMaster OID@@@@@@@@@@@@@@@ "+ oid);
        	tableName = "HBIMATERIAL";
        	
        }
        if(oid.contains("LCSPartMaster" )) {
        	System.out.println("%%%%%%%%%%%%%%%%%%%LCSPartMaster OID@@@@@@@@@@@@@@@ "+ oid);
        	tableName = "HBIPRODUCT";
        	
        }
        if (oid.contains("WTPartMaster")) {
       
     //Notes for Karthik
               // Here , if the owner object is WTPartMaster, the tableName can be LCSMaterial or LCSProduct etc.
               // We need to get the child(LCSMaterial or LCSProduct or any other childs) from the master object and set the tablename accordingly.  
        	
            WTPartMaster obj = (WTPartMaster)LCSQuery.findObjectById("OR:"+oid);
            String identityObj = obj.getIdentificationObject().toString();
            if(identityObj.contains("MATERIAL")) {
                tableName = "HBIMATERIAL";
            }
            if(identityObj.contains("PRODUCT")) {
                tableName = "HBIPRODUCT";
            }
        }
        if (oid.contains("LCSMaterialSupplierMaster")) {
            tableName = "HBIMATERIALSUPPLIER";
        }
        if (oid.contains("LCSSupplierMaster")) {
            tableName = "HBISUPPLIER";
        }
        if (oid.contains("LCSLifecycleManaged")) {
            tableName = "HBIBUSINESSOBJECT";
        }
        if (oid.contains("LCSMOAObject")) {
            tableName = "HBIMOAOBJECT";
        }
        return tableName;
    }

    public String getOwnerKey(String ownerRef) {

        String ownerKey= null;
        Integer ida3a5 = Integer.parseInt(ownerRef.substring(ownerRef.lastIndexOf(":") + 1, ownerRef.length()));
       // System.out.println("ida3a5 in Util =" + ida3a5);

        if (ownerRef.contains("LCSMaterialSupplierMaster")) {
          
            
            try {
                PreparedQueryStatement statement = new PreparedQueryStatement();
                statement.appendFromTable(LCSMaterialSupplier.class);
                statement.appendSelectColumn(new QueryColumn(LCSMaterialSupplier.class, "iterationInfo.branchId"));
                statement.appendAndIfNeeded();
                statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterialSupplier.class, "masterReference.key.id"), ida3a5.toString(), "="));
                statement.appendAndIfNeeded();
                statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterialSupplier.class, "iterationInfo.latest"), "1", "="));
                SearchResults results = LCSQuery.runDirectQuery(statement);
                Vector expObjects = results.getResults();
                FlexObject flexObj = (FlexObject) expObjects.get(0);

                ownerKey = flexObj.getString("LCSMATERIALSUPPLIER.BRANCHIDITERATIONINFO");
                
            } catch (WTException ex) {
                Logger.getLogger(TransformUtil.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Exception" + ex);
            }
        }
         if (ownerRef.contains("LCSSupplierMaster")) {
            try {
               // System.out.println("Inside loop");
                PreparedQueryStatement statement = new PreparedQueryStatement();
                statement.appendFromTable(LCSSupplier.class);
                statement.appendSelectColumn(new QueryColumn(LCSSupplier.class, "iterationInfo.branchId"));
                statement.appendAndIfNeeded();
                statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "masterReference.key.id"), ida3a5.toString(), "="));
                statement.appendAndIfNeeded();
                statement.appendCriteria(new Criteria(new QueryColumn(LCSSupplier.class, "iterationInfo.latest"), "1", "="));
                SearchResults results = LCSQuery.runDirectQuery(statement);
                Vector expObjects = results.getResults();
               // System.out.println("statement =" + statement);
               // System.out.println("expObjects =" + expObjects);
                FlexObject flexObj = (FlexObject) expObjects.get(0);

                ownerKey = flexObj.getString("LCSSUPPLIER.BRANCHIDITERATIONINFO");
               //System.out.println("ownerKey in Util =" + ownerKey);
            } catch (WTException ex) {
                Logger.getLogger(TransformUtil.class.getName()).log(Level.SEVERE, null, ex);
               // System.out.println("Exception" + ex);
            }
        }
         
            if (ownerRef.contains("LCSPartMaster")) {
                System.out.println("I am LCSPartMaster Wipro");
//Notes for Karthik
               // Here , if the owner object is WTPArtMaster, the child can be LCSMaterial or LCSProduct etc.
               // We need to get the child(LCSMaterial or LCSProduct or any other childs) from the master object and query the branchid accordingly.  

            try {
               // System.out.println("Inside loop");
                LCSPartMaster obj = (LCSPartMaster)LCSQuery.findObjectById("OR:"+ownerRef);
                String identityObj = obj.getIdentificationObject().toString();
                if(identityObj.contains("PRODUCT")) {
                    PreparedQueryStatement statement = new PreparedQueryStatement();
                    statement.appendFromTable(LCSProduct.class);
                    statement.appendSelectColumn(new QueryColumn(LCSProduct.class, "iterationInfo.branchId"));
                    statement.appendAndIfNeeded();
                    statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, "masterReference.key.id"), ida3a5.toString(), "="));
                    statement.appendAndIfNeeded();
                    statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, "iterationInfo.latest"), "1", "="));
					statement.appendAndIfNeeded();
                    statement.appendCriteria(new Criteria(new QueryColumn(LCSProduct.class, "versionInfo.identifier.versionId"), "A", "="));
                    SearchResults results = LCSQuery.runDirectQuery(statement);
                    Vector expObjects = results.getResults();
                   // System.out.println("statement =" + statement);
                   // System.out.println("expObjects =" + expObjects);
                    FlexObject flexObj = (FlexObject) expObjects.get(0);

                    ownerKey = flexObj.getString("LCSPRODUCT.BRANCHIDITERATIONINFO");
                }
                System.out.println("ownerKey in Util =" + ownerKey);
            } catch (WTException ex) {
                Logger.getLogger(TransformUtil.class.getName()).log(Level.SEVERE, null, ex);
               // System.out.println("Exception" + ex);
            }
        }
            if (ownerRef.contains("LCSMaterialMaster")) {
                
            	//Notes for Karthik
            	               // Here , if the owner object is WTPArtMaster, the child can be LCSMaterial or LCSProduct etc.
            	               // We need to get the child(LCSMaterial or LCSProduct or any other childs) from the master object and query the branchid accordingly.  

            	            try {
            	               // System.out.println("Inside loop");
            	            	LCSMaterialMaster obj = (LCSMaterialMaster)LCSQuery.findObjectById("OR:"+ownerRef);
            	                String identityObj = obj.getIdentificationObject().toString();
            	                if(identityObj.contains("MATERIAL")) {
            	                    PreparedQueryStatement statement = new PreparedQueryStatement();
            	                    statement.appendFromTable(LCSMaterial.class);
            	                    statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "iterationInfo.branchId"));
            	                    statement.appendAndIfNeeded();
            	                    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "masterReference.key.id"), ida3a5.toString(), "="));
            	                    statement.appendAndIfNeeded();
            	                    statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "iterationInfo.latest"), "1", "="));
            	                    SearchResults results = LCSQuery.runDirectQuery(statement);
            	                    Vector expObjects = results.getResults();
            	                  // System.out.println("statement =" + statement);
            	                   // System.out.println("expObjects =" + expObjects);
            	                    FlexObject flexObj = (FlexObject) expObjects.get(0);

            	                    ownerKey = flexObj.getString("LCSMATERIAL.BRANCHIDITERATIONINFO");
            	                }

            	                System.out.println("ownerKey in Util =" + ownerKey);
            	            } catch (WTException ex) {
            	                Logger.getLogger(TransformUtil.class.getName()).log(Level.SEVERE, null, ex);
            	               // System.out.println("Exception" + ex);
            	            }
            	        
			}
            
            if (ownerRef.contains("LCSColor")) {
            
                ownerKey = ownerRef.substring(ownerRef.lastIndexOf(":") + 1, ownerRef.length());
        }
            
            if (ownerRef.contains("LCSMaterialColor")) {
            
                ownerKey = ownerRef.substring(ownerRef.lastIndexOf(":") + 1, ownerRef.length());
        }
        return ownerKey;

    }

}
