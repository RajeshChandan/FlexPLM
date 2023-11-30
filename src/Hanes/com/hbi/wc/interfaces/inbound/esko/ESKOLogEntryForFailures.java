package com.hbi.wc.interfaces.inbound.esko;

import java.sql.Timestamp;
import java.util.Date;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLogEntry;
import com.lcs.wc.foundation.LCSLogEntryLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.load.LoadCommon;

import wt.util.WTException;
import wt.util.WTPropertyVetoException;

public class ESKOLogEntryForFailures {

	
public static void logTransaction(Date date, String partnumber, String validationForAccessories, boolean accessories) throws WTException {
		
   // FlexType flextype = (FlexType) LoadCommon.getFlexTypeFromPath("Log Entry\\ESKO-PLM FAILURE LOG ENTRY");
	FlexType flextype = (FlexType) FlexTypeCache.getFlexTypeFromPath("Log Entry\\ESKO-PLM FAILURE LOG ENTRY");
    LCSLogEntry logEntry= checkForLogEntryExistance(partnumber,flextype);
		//Commented below two lines in 10.1 system-since invoking from rmi
        //new MethodContext(null, null);
    if(logEntry==null){
         logEntry = LCSLogEntry.newLCSLogEntry();
     	System.out.println("============creating log entry for =========="+partnumber);

    }
    else{
    	System.out.println("============Found log entry for =========="+partnumber);
    	System.out.println("============updating log entry for =========="+partnumber);

	
    }
       
       // com.lcs.wc.load.LoadCommon.SERVER_MODE = true;
        try {
            logEntry.setFlexType(flextype);
            logEntry.setValue("hbiPartNumber", partnumber);
            logEntry.setValue("hbiException", validationForAccessories);
            logEntry.setValue("hbiJobRanDate", date);
            if(accessories){
                logEntry.setValue("hbiPossibleSolution", "Correct the data in ESKO as per the rule : \n 1.The size should be BLANK \n 2.COO should be blank or “not included”, \n 3.Fiber code should be blank \n 4.Label format should be “brand”.");
            }
            else if(validationForAccessories.contains("HBI Material Sku")){
                logEntry.setValue("hbiPossibleSolution", "Material Already exists in PLM , \n Please go to the material and update the attributes Color code / Size Code");

            }
            else if(validationForAccessories.contains("Invalid SizeRange")){
                logEntry.setValue("hbiPossibleSolution", "Correct the data in ESKO as per the rule : \n number of sizes should match number of part numbers");
            }
            else{
                logEntry.setValue("hbiPossibleSolution", "Contact PLM Admin team");
	
            }
            LCSLogEntryLogic logLogic = new LCSLogEntryLogic();
            logEntry = (com.lcs.wc.foundation.LCSLogEntry) logLogic.saveLog(logEntry);

        } catch ( WTException ex) {
        	ex.printStackTrace();
        } catch (WTPropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

public static LCSLogEntry checkForLogEntryExistance(String partnumber, FlexType flextype) {
	// TODO Auto-generated method stub
    LCSLogEntry logentry=null;
	 PreparedQueryStatement statement=new PreparedQueryStatement();
     try {
		statement.appendSelectColumn(
					new QueryColumn(LCSLogEntry.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.appendFromTable(LCSLogEntry.class);
		statement.appendAndIfNeeded();
		statement.appendCriteria(
				new Criteria(new QueryColumn(LCSLogEntry.class, flextype.getAttribute("hbiPartNumber").getColumnDescriptorName()), "?", Criteria.LIKE),
				partnumber);
		//statement.appendCriteria(new Criteria("LCSLogEntry", flextype.getAttribute("hbiPartNumber").getColumnDescriptorName()), "%"+partnumber+"%", Criteria.LIKE));

		
		SearchResults results = LCSQuery.runDirectQuery(statement);

		//
		if(results != null && results.getResultsFound() > 0)
		{
			FlexObject flexObj = (FlexObject) results.getResults().firstElement();
			logentry = (LCSLogEntry) LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLogEntry:"+flexObj.getString("LCSLogEntry.IDA2A2"));
		}
	} catch (WTException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return logentry;
		
}
}
