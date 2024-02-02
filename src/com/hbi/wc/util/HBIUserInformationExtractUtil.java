package com.hbi.wc.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.UserGroupHelper;

import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTGroup;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

public class HBIUserInformationExtractUtil implements RemoteAccess{
	
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.util.HBIUserInformationExtractUtil.userID","prodadmin");
	private static RemoteMethodServer remoteMethodServer;

	
	private static Logger log = LogManager.getLogger(HBIUserInformationExtractUtil.class);
	public static String VENDORGROUP = LCSProperties.get("com.lcs.wc.vendor.vendorGroup", "VENDORGROUP");
	public static String PTC_INTERNAL_LICENSE_GROUP = LCSProperties.get("com.hbi.wc.util.HBIUserInformationExtractUtil.internalLicenseGroup", "PTC FlexPLM Internal License");
	public static String PTC_EXTERNAL_LICENSE_GROUP = LCSProperties.get("com.hbi.wc.util.HBIUserInformationExtractUtil.externalLicenseGroup", "PTC FlexPLM External License");
	private static final String AUDIT_RECORD_TABLE = LCSProperties.get("com.hbi.wc.util.HBIUserInformationExtractUtil.AuditRecordTableName","AUDITRECORD");
	private static final String EXPORT_FILE_NAME = LCSProperties.get("com.hbi.wc.util.HBIUserInformationExtractUtil.exportFileName","HanesUserInformationExport.csv");
	private static String FOLDER_LOCATION = "";
	private static String COLUMN_HEADERS = LCSProperties.get("com.hbi.wc.util.HBIUserInformationExtractUtil.columnHeaders","Username,First Name,Last Name,Full Name,User Creation Date,Last Login Attempt Date,Status,Is User Licensed?,User Type, Groups");
	static{
		try{
			WTProperties wtprops = WTProperties.getLocalProperties();
	        String home = wtprops.getProperty("wt.home");
	        FOLDER_LOCATION = home + File.separator + "logs" + File.separator + "migration"+ File.separator;
	        if(!(new File(FOLDER_LOCATION).exists())){
	        	new File(FOLDER_LOCATION).mkdir();
	        }
		}catch (Exception exp){
			log.debug("Exception in static block of the class HBIUserInformationExtractUtil is : "+ exp);
		}
	}
	
	
	

	public static void main(String[] args) {
		
		
		log.info("### START HBIUserInformationExtractUtil.main() ###");
		long start = System.currentTimeMillis();
		try {
			// These contexts are needed for establishing connection to method
			// server- Do not remove these 2 below lines
			MethodContext mcontext = new MethodContext((String) null, (Object) null);
			SessionContext sessioncontext = SessionContext.newContext();
			// These contexts are needed for establishing connection to method
			// server- Do not remove these 2 above lines

			remoteMethodServer = RemoteMethodServer.getDefault();
//			remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
//			remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);

			GatewayAuthenticator authenticator = new GatewayAuthenticator();
			authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setAuthenticator(authenticator);
			Class[] argumentClass = {};
			Object[] argumentObject = {};
			remoteMethodServer.invoke("getUserInformation", "com.hbi.wc.util.HBIUserInformationExtractUtil", null,
					argumentClass, argumentObject);

			long end = System.currentTimeMillis();
			NumberFormat formatter = new DecimalFormat("#0.00000");
			System.out.print("Execution time is :-->" + formatter.format((end - start) / 1000d) + " seconds");

			System.out.println("\n####### Ended Remote method server connection, please check the exported user list in migration folder#####");
			System.out.println("####### Successfully logged off #####");
			log.info("### END HBIUserInformationExtractUtil.main() ###");
			System.exit(0);

		} catch (Exception exception) {
			exception.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void getUserInformation()	throws WTException, WTPropertyVetoException {
		log.info("### START HBIUserInformationExtractUtil.getUserInformation() ###");
		FileWriter writer=null;
		
		try{
			writer=new FileWriter(FOLDER_LOCATION + EXPORT_FILE_NAME);
			writer.append(COLUMN_HEADERS);
			writer.append( "\n");
			
			WTGroup vendorGroup = UserGroupHelper.getWTGroup(VENDORGROUP);
			WTGroup internalLicenseUserGroup = UserGroupHelper.getWTGroup(PTC_INTERNAL_LICENSE_GROUP);
			WTGroup externalLicenseUserGroup = UserGroupHelper.getWTGroup(PTC_EXTERNAL_LICENSE_GROUP);
			Map<String, String> lastLoginDetailsOfAllUsers = getLastLoginDetailsOfAllUsers();
			
			//below are added counters
			int countUsers = 0;
			int countLicensedUsers = 0;
			int countNonLicensedUsers = 0;
			
			ArrayList<String> userIdsFromDB = getUserIdsFromDB();
			
			for (String userId : userIdsFromDB) {
					countUsers++;
					WTUser user = (WTUser) LCSQuery.findObjectById("wt.org.WTUser:"+userId);
					//Check if Internal user or Vendor User
					String internalStatus = "";
						if (vendorGroup != null && vendorGroup.isMember((WTPrincipal)user))
							internalStatus = "Vendor User";
						else
							internalStatus = "Internal User";
						
						//Get User Information details
					String userAuthenticationName=formatUserName(user.getAuthenticationName());
					log.info("username>>>>>>>>>>>>>>>>"+userAuthenticationName);
					
					String userFullName=handleSpecialCharacters(user.getFullName());
					String userLastName=handleSpecialCharacters(user.getLast());
					
					java.sql.Timestamp objCreateTimeStamp = null;
					
					objCreateTimeStamp = user.getCreateTimestamp();
					String pCreateDateVal = "";
					if(objCreateTimeStamp != null){
						pCreateDateVal =  FormatHelper.applyFormat(objCreateTimeStamp, FormatHelper.DATE_TIME_STRING_FORMAT);
					}
					
					//Get user associated groups    
					StringBuilder groupNames = new StringBuilder();
					Enumeration parentGroupNames = user.parentGroups(false);
					while (parentGroupNames.hasMoreElements()) {
						wt.org.WTPrincipalReference parentGroupName = (wt.org.WTPrincipalReference) parentGroupNames.nextElement();
						groupNames.append(parentGroupName.getName()).append(",");
					}
					
					//Getting last login details
					String lastLoginTime = "";
					lastLoginTime =lastLoginDetailsOfAllUsers.get(userId);
						if (!FormatHelper.hasContent(lastLoginTime)) {
							lastLoginTime = "No Login found";
						}
					

					//Get if the User is disabled or not.
					String status = "";
					if (user.isDisabled()) {
						status = "Disabled";
					}else {
						status= "Active";
					}
					
					//Check for licensing
					String isLicensed = "";			
					if (internalLicenseUserGroup.isMember(user)||externalLicenseUserGroup.isMember(user)) {
						isLicensed="Yes";
						countLicensedUsers++;
					}else{
						isLicensed="No"	;
						countNonLicensedUsers++;
					}
					//Write data to the file
					writer.append(userAuthenticationName);//1
					writer.append( ",");
					writer.append(" ");//First Name blank //2
					writer.append( ",");
					writer.append(userLastName);//3
					writer.append( ",");
					writer.append(userFullName);//4
					writer.append( ",");
					writer.append(pCreateDateVal);//5
					writer.append( ",");
					writer.append(lastLoginTime);//6
					writer.append( ",");
					writer.append(status);//7
					writer.append( ",");
					writer.append(isLicensed);//8
					writer.append( ",");
					writer.append(internalStatus);//9
					writer.append( ",");
					writer.append(handleSpecialCharacters(groupNames.toString()));//10
					writer.append( "\n");
					
					
				}
			log.info("usersCount>>>>>>>>>>>>>>>>"+countUsers);
			log.info("countLicensedUsers>>>>>>>>>>>>>>>>"+countLicensedUsers);
			log.info("countNonLicensedUsers>>>>>>>>>>>>>>>>"+countNonLicensedUsers);
			log.info("### END HBIUserInformationExtractUtil.getUserInformation() ###");
		}
		catch(IOException e){
			e.printStackTrace();
			
			
		}
		
		finally{
			
			
			try{
				writer.flush();
				writer.close();
				
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		

	}
	

	public static String handleSpecialCharacters(String data) {
		if (data==null)
			return "";
	    String escapedData = data.replaceAll("\\n", " ");
	    if (data.contains(",") || data.contains("\"") || data.contains("'")) {
	        data = data.replace("\"", "\"\"");
	        escapedData = "\"" + data + "\"";
	    }
	    return escapedData;
	}
	

public static Map<String, String> getLastLoginDetailsOfAllUsers(){
	log.info("### START HBIUserInformationExtractUtil.getLastLoginDetailsOfAllUsers() ###");
	Map<String, String> userLoginDetails = new HashMap<String,String>();
		SearchResults results = null;
		PreparedQueryStatement statement = new PreparedQueryStatement();
        statement.appendFromTable(AUDIT_RECORD_TABLE);
        statement.appendSelectColumn(new QueryColumn(AUDIT_RECORD_TABLE, "IDB5"));
        statement.appendSelectColumn(new QueryColumn(AUDIT_RECORD_TABLE, "EVENTTIME"),"MAX");
        statement.appendAndIfNeeded();
        statement.appendCriteria(new Criteria(new QueryColumn(AUDIT_RECORD_TABLE, "EVENTLABEL"), "Login%", Criteria.LIKE));
        statement.appendGroupBy(new QueryColumn(AUDIT_RECORD_TABLE, "IDB5"));
		try {
			results = LCSQuery.runDirectQuery(statement);
		} catch (WTException e) {
			
			e.printStackTrace();
		}
		Vector resultsVector = results.getResults();
		
		for (Object object : resultsVector) {
			FlexObject loginDetailsFO = (FlexObject) object;
			String userID = loginDetailsFO.getString(AUDIT_RECORD_TABLE+".IDB5");
			String lastLoginTime = loginDetailsFO.getString("MAX(AuditRecord.EVENTTIME)");
			userLoginDetails.put(userID, lastLoginTime);
			
		}
		
		log.info("### END HBIUserInformationExtractUtil.getLastLoginDetailsOfAllUsers() ###");
		
	return userLoginDetails;
}

public static ArrayList<String> getUserIdsFromDB() throws WTException{
	log.info("### START HBIUserInformationExtractUtil.getUserIdsFromDB() ###");
	String wtUserTableName = "WTUser";
	ArrayList<String> userList = new ArrayList<String>();
	PreparedQueryStatement statement = new PreparedQueryStatement();
	statement.appendFromTable(WTUser.class);
	statement.appendSelectColumn(new QueryColumn(wtUserTableName, "IDA2A2"));
	
	SearchResults results = LCSQuery.runDirectQuery(statement);
	log.info("no. of user ids found from DB>>>>>>>>>>>>>>>>"+results.getResultsFound());
	Vector resultsVector = results.getResults();
	for (Object object : resultsVector) {
		FlexObject userFlexObject = (FlexObject)object;
		String userId = userFlexObject.getString("WTUSER.IDA2A2");
			if(FormatHelper.hasContent(userId))
			userList.add(userId);
	}
	log.info("### END HBIUserInformationExtractUtil.getUserIdsFromDB() ###");
	return userList;
	
	
}
public static String formatUserName(String userAuthenticationName){
	log.info("### START HBIUserInformationExtractUtil.formatUserName() ###");
	if(userAuthenticationName.contains("}")){
		log.debug("userAuthenticationName before>>>>>>>>"+userAuthenticationName);	
		userAuthenticationName = userAuthenticationName.substring(userAuthenticationName.lastIndexOf("}")+1);
	    log.debug("userAuthenticationName after>>>>>>>>"+userAuthenticationName);
	}
	log.info("### END HBIUserInformationExtractUtil.formatUserName() ###");
	return userAuthenticationName;
}
}
