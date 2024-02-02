/**
 * 
 */
package com.hbi.wc.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.RetypeLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialHelper;
import com.lcs.wc.util.ACLHelper;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

/**
 * @author U55219
 *
 */
public class HBIMaterialTypeChangeUtil extends StandardManager  implements RemoteAccess,Serializable{
	private static String CLIENT_ADMIN_USER_ID = com.lcs.wc.util.LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "pass2014a");
	private static RemoteMethodServer remoteMethodServer;
	private static SessionContext sessioncontext ;
	private static boolean VERBOSE=false;
	private static StringBuilder flextypes_Not_Found=new StringBuilder();
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		 try{

StringBuilder failed_rows=new StringBuilder();

	            BufferedReader buf = new BufferedReader(new FileReader("D:\\ptc\\Windchill_10.1\\Windchill\\src\\com\\hbi\\wc\\utility\\2CRG_march8.txt"));
	            
	            MethodContext mcontext = new MethodContext((String) null, (Object) null);
				sessioncontext = SessionContext.newContext();

				VERBOSE=Boolean.parseBoolean(args[0]);

				remoteMethodServer = RemoteMethodServer.getDefault();
				remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID); 
				remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);

				GatewayAuthenticator authenticator = new GatewayAuthenticator();
				authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID);
				remoteMethodServer.setAuthenticator(authenticator);
	            String lineJustFetched = null;
	            String[] wordsArray;
int lineNumber=1;
	            while(true){
	            	ArrayList<String> words = new ArrayList<String>();
	                lineJustFetched = buf.readLine();
	              
	                if(lineJustFetched == null){  
	                    break; 
	                }else{
	                    wordsArray = lineJustFetched.split("\t");
	                    for(String each : wordsArray){
	                       
	                            words.add(each);
	                        
	                    }
	                    LCSLog.debug("MATERIAL TYPE CHANGE UTIL: lineNumber------------->"+lineNumber);
	                    LCSLog.debug("MATERIAL TYPE CHANGE UTIL: failed_rows------------->"+failed_rows);
	                    failed_rows=getMaterialTypeObjects(failed_rows,lineNumber,words) ;
	                    ++lineNumber;
	                   
	                }
	            }

	           

	            buf.close();
	            
	            LCSLog.debug("MATERIAL TYPE CHANGE UTIL: FAILED_ROWS------------->"+failed_rows);	
	            LCSLog.debug("MATERIAL TYPE CHANGE UTIL: flextypes_Not_Found------------->"+flextypes_Not_Found);	
	            System.exit(0);

	        }catch(Exception e){
	            e.printStackTrace();
	            System.exit(1);
	        }
		
		
		
		
	}
	
	
	/**
	 * This function is using to get collection of End OF End color  objects. 
	 * @return collectionOfEndOfEndColorObj - Collection<LCSColor>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static StringBuilder getMaterialTypeObjects(StringBuilder failed_rows,int lineNo,ArrayList<String> words) throws WTException,WTPropertyVetoException
	{
		Collection<LCSMaterial> listOfMaterialObjs = new ArrayList<LCSMaterial>();
		
		
		try {
	
		FlexType sourceMatFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Material\\Material SKU");
		FlexType targetMatFlexTypeObj = null;
		
		FlexTypeAttribute matCodeAtt=sourceMatFlexTypeObj.getAttribute("hbiMaterialCode");
		FlexTypeAttribute colorCodeAtt=sourceMatFlexTypeObj.getAttribute("hbiColorCode");
		FlexTypeAttribute attCodeAtt=sourceMatFlexTypeObj.getAttribute("hbiAttrCode");
		FlexTypeAttribute sizeCodeAtt=sourceMatFlexTypeObj.getAttribute("hbiSizeCode");
		FlexTypeAttribute itemDescAtt=sourceMatFlexTypeObj.getAttribute("hbiItemDescription");
		FlexTypeAttribute inventUOMAtt=sourceMatFlexTypeObj.getAttribute("hbiInventoryUOM");
		FlexTypeAttribute uageUOMAtt=sourceMatFlexTypeObj.getAttribute("hbiUsageUOM");
		FlexTypeAttribute convFactAtt=sourceMatFlexTypeObj.getAttribute("hbiConvFactor");
		FlexTypeAttribute buyUOMAtt=sourceMatFlexTypeObj.getAttribute("hbiBuyUom");
		FlexTypeAttribute majorCatAtt=sourceMatFlexTypeObj.getAttribute("hbiMajorCategory");
		FlexTypeAttribute minorCatAtt=sourceMatFlexTypeObj.getAttribute("hbiMinorCategory");
		
				
		
		String id = sourceMatFlexTypeObj.getTypeIdPath();
		Vector<FlexObject> listOfObjects = null;
		
		
		
		
		//Initializing the PreparedQueryStatement, which is using to get LCSColor object based on the given set of parameters(like FlexTypePath of the object). 
    	PreparedQueryStatement statement = new PreparedQueryStatement();
    	statement.appendFromTable(LCSMaterial.class);
    	statement.appendSelectColumn(new QueryColumn(LCSMaterial.class, "thePersistInfo.theObjectIdentifier.id"));
    	statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSMaterial.class, "flexTypeIdPath"), id, Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn("LCSMaterial", "statecheckoutInfo"), "wrk", Criteria.NOT_EQUAL_TO));
	
			/*	statement.appendAndIfNeeded();
		*/
		//statement.appendCriteria(new Criteria(new QueryColumn("LCSMaterial", "idA3A11"), sourceMatFlexTypeObj.idA2A2, Criteria.NOT_EQUAL_TO));
				statement.appendAndIfNeeded();			
				statement.appendCriteria(new Criteria(new QueryColumn("LCSMaterial", "ATT1"), "material_placeholder", Criteria.NOT_EQUAL_TO));
				
				
				
				
		
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn("LCSMaterial", matCodeAtt.getVariableName()), words.get(1), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn("LCSMaterial", colorCodeAtt.getVariableName()), words.get(2), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn("LCSMaterial", attCodeAtt.getVariableName()), words.get(3), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn("LCSMaterial", sizeCodeAtt.getVariableName()), words.get(4), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn("LCSMaterial", itemDescAtt.getVariableName()), words.get(5), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn("LCSMaterial", inventUOMAtt.getVariableName()), (String)getKeyFromValue(inventUOMAtt.getAttValueList().getList(),words.get(6)), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn("LCSMaterial", uageUOMAtt.getVariableName()), (String)getKeyFromValue(uageUOMAtt.getAttValueList().getList(),words.get(7)), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn("LCSMaterial", convFactAtt.getVariableName()), words.get(8), Criteria.EQUALS));
		//statement.appendAndIfNeeded();
		//statement.appendCriteria(new Criteria(new QueryColumn("LCSMaterial", buyUOMAtt.getVariableName()), (String)getKeyFromValue(buyUOMAtt.getAttValueList().getList(),words.get(9)), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn("LCSMaterial", majorCatAtt.getVariableName()), (String)getKeyFromValue(majorCatAtt.getAttValueList().getList(),words.get(10)), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn("LCSMaterial", minorCatAtt.getVariableName()), (String)getKeyFromValue(minorCatAtt.getAttValueList().getList(),words.get(11)), Criteria.EQUALS));
		statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn("LCSMaterial", "latestiterationinfo"), "1", Criteria.EQUALS));
	
		if("Printed Paperboard".equalsIgnoreCase(words.get(10))) {
			
			targetMatFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Material\\Packaging\\Paperboard");
			
		}else if("Polybags".equalsIgnoreCase(words.get(10))) {
			targetMatFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Material\\Packaging\\Polybag");
			
		}else if("Accessories".equalsIgnoreCase(words.get(10))) {
			targetMatFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Material\\Packaging\\Accessories");
			
		}else if("Stickers".equalsIgnoreCase(words.get(10))) {
			targetMatFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Material\\Packaging\\Sticker");
			
		}else if("Tags".equalsIgnoreCase(words.get(10))) {
			targetMatFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Material\\Packaging\\Tag");
			
		}else {
			
			flextypes_Not_Found.append(lineNo+" -->"+words.get(10)+" , ");
		}
		
		
		SearchResults results = LCSQuery.runDirectQuery(statement);
		if(VERBOSE) {
			LCSLog.debug("MATERIAL TYPE CHANGE UTIL: target Flextype Path is :-------->"+targetMatFlexTypeObj.getTypeDisplayName());
			
			}
		
		if(results.getResultsFound() == 0) {
		
			failed_rows.append(" "+lineNo+" , ");	LCSLog.debug("MATERIAL TYPE CHANGE UTIL: inside zero------------->"+failed_rows);	
			
			 
			LCSLog.debug("MATERIAL TYPE CHANGE UTIL: Query Statement for the row number"+lineNo+" ----------STMT--->"+statement);
			LCSLog.debug("MATERIAL TYPE CHANGE UTIL: Results------------->"+results);	
		
			
		}else if(results != null && results.getResultsFound() > 0)
        {
			listOfObjects = results.getResults();
			for(FlexObject flexObj: listOfObjects)
			{
				LCSMaterial materialObj = (LCSMaterial) LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterial:"+flexObj.getString("LCSMATERIAL.IDA2A2"));
				


				String matTypeOID = FormatHelper.getObjectId(targetMatFlexTypeObj);
				 Collection<FlexTypeAttribute> keyAttributes = sourceMatFlexTypeObj.getAllAttributesUsedBy(materialObj);
				 Iterator<FlexTypeAttribute> keys = keyAttributes.iterator();
		            FlexTypeAttribute att = null;
		            
		            Map<String, Object> sourceMatValues=new HashMap<String, Object>();
		            
		            
		         
		            
		            
		            while(keys.hasNext()){
		                att = (FlexTypeAttribute) keys.next();

		               	if( !att.isAttEnabled() || att.isAttHidden() ||
		                    !ACLHelper.hasViewAccess(att)){
		                    continue;
		                }
		               	
		                sourceMatValues.put(att.getAttKey(), materialObj.getValue(att.getAttKey()));
		               	
		               	
		            }
		            
		            
				 
				 
				materialObj= (LCSMaterial) RetypeLogic.changeType(materialObj, FormatHelper.getObjectId(targetMatFlexTypeObj),false);
				
				 Collection<FlexTypeAttribute> targetAttributes =	materialObj.getFlexType().getAllAttributesUsedBy(materialObj);
				 Iterator<FlexTypeAttribute> targetKeys = keyAttributes.iterator();
				 
				 while(targetKeys.hasNext()){
		                FlexTypeAttribute att1 = (FlexTypeAttribute) targetKeys.next();

		               	if( !att1.isAttEnabled() || att1.isAttHidden() ||
		                    !ACLHelper.hasViewAccess(att1)){
		                    continue;
		                }
		               	
		                sourceMatValues.put(att.getAttKey(), materialObj.getValue(att.getAttKey()));
		               	
		               	
		            }
				
				 
				Collection col= materialObj.getFlexType().getAllAttributes();
				
				Iterator<FlexTypeAttribute> itrFTA=col.iterator();
				
				
				while(itrFTA.hasNext()) {
					
					
					String key=itrFTA.next().getAttKey();
					
					if(sourceMatValues.get(key)!= null) {
					
						
						
						materialObj.setValue(key, sourceMatValues.get(key));
					}
				}
				
				
				materialObj.setValue("hbiBuyUom","EA");
				 LCSMaterialHelper.service.saveMaterial(materialObj);
				
			}	
        }
		
		
		
		}catch(Exception e) {
			
			LCSLog.debug("Exception occured at line number "+lineNo+" and data for the same row is "+words.get(1)+" "+words.get(2)+" "+words.get(3)+" "+words.get(4)+" "+words.get(5)+" "+words.get(6)+" "+words.get(7)+" "+words.get(8)+" "+words.get(9)+" "+words.get(10)+" "+words.get(11));
			e.printStackTrace();
			failed_rows.append(" "+lineNo+" , ");
		}
		LCSLog.debug("MATERIAL TYPE CHANGE UTIL: Return value ------------->"+failed_rows);	
		 LCSLog.debug("MATERIAL TYPE CHANGE UTIL: Materials got updated successfully.Please check for 'Exception occured at Line number' to check failed rows");
			return failed_rows; 
	}

	
	 /**
	 * 
	 * Returns the key value of single list by taking display value for the single list attributes
	 * 
	 * @param hm
	 * @param value
	 * @return
	 */
	public static Object getKeyFromValue(Map hm, String value) {
		
         for (Object o : hm.keySet()) {
        	 
        	
        	 if(((HashMap)(hm.get(o))).get("VALUE")!= null) {
           if (((String)((HashMap)(hm.get(o))).get("VALUE")).contains(value)) {
        	   
        	   
        	   return o;
           }
        	 }
         }
         return null;
       }
}
