package com.sportmaster.wc.reports;
import wt.util.WTException;
import wt.org.WTUser;
import wt.org.WTGroup;

import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import wt.fc.WTObject;
import java.lang.Exception;

public class ReportTools {
	
	public static String getGroups(WTUser user){
		String AllNames = "";
	
		try {			
				Enumeration groups = user.parentGroupNames() ;
				while (groups.hasMoreElements())
				{    String aGroup  = (String)groups.nextElement();
			
					if (!"SportMaster|teamMembers|MEMBERS|Profile Groups|Retail|PDM|PRODUCT MANAGER|250_ORG|roleGroups|PRODUCT CREATOR|LIBRARY CREATOR|LIBRARY MANAGER|ORG ADMIN|All Participating Members|248_ORG|PROJECT CREATOR|Unrestricted Organizations|orgs|This Org".contains(aGroup)) {
					AllNames = AllNames + aGroup + "\n";
					}
				}
				
			return AllNames;
					
		} catch (WTException e) {

			e.printStackTrace();
		}
		
		return null;
			
	}

	public static String getUsers(WTGroup group){
		String AllNames = "";
	
		try {			
				Enumeration users = group.members() ;
				while (users.hasMoreElements())
				{   WTObject obj = (WTObject)users.nextElement();
            if (obj instanceof WTUser)
               { 
                 WTUser aUser  = (WTUser)obj;
				         String aUserName = aUser.getFullName();
			           AllNames = AllNames + aUserName + "\n";
               }
				}
				
			return AllNames;
					
		} catch (WTException e) {

			e.printStackTrace();
		}
		
		return null;
			
	}
	
	public static String convertMultiEntry(String src){
		String result = "";
	
		try {		
                StringTokenizer st = new StringTokenizer(src,"|~*~|");		
				while (st.hasMoreTokens()) {
					if (!result.equals("")) result = result + ",";
					String next = st.nextToken() ;
					if (next.startsWith("sm")) next = next.substring(2); else 
					   if (next.startsWith("vrd")) next = next.substring(3);
						result = result + next ;
						
						}
				
			return result;
					
		} catch (Exception e) {

			e.printStackTrace();
		}
		
		return null;
			
	}

	public static Date latestDate(Date date1, Date date2){
		Date result;
		if (date1.compareTo(date2) >0) result = date1; else result = date2;

			return result;


	}
	public static Date latestDate(Date date1, Date date2, Date date3){
		Date result;
		if (date1.compareTo(date2) >0) result = date1;
		     else if (date3.compareTo(date2)>0) result = date3; else result = date2;
		return result;


	}

}
