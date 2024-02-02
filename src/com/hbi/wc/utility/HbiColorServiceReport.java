package com.hbi.wc.utility;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

import com.lcs.wc.color.LCSColor;  
import com.lcs.wc.color.LCSColorQuery; 
import com.lcs.wc.color.LCSColorLogic;
import com.lcs.wc.color.LCSColorHelper;
import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.RetypeLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.color.LCSPalette;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOAObjectHelper;

import wt.fc.WTObject;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;
import wt.vc.VersionControlHelper;

import java.util.Date;
import java.io.BufferedWriter;
import java.text.DateFormat;
import java.io.File;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.io.FileWriter;
import java.util.TimeZone;
import java.io.IOException;

import com.lcs.wc.whereused.FAWhereUsedQuery;

/**
 * HbiColorServiceReport.java
 * 
 * This class contains a utility function for End of End Color Objects which are using to read the End Of End Color data 
 * and populating attributes data  in new Color type i.e. "Color\\Yarn Dye Wovens". 
 * @author soumya.babu@Hanes.com
 * @since Dec-22-2016
 */
public class HbiColorServiceReport implements RemoteAccess
{
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "pass2014a");
	private static RemoteMethodServer remoteMethodServer;
	private static String floderPhysicalLocation = "";
	static BufferedWriter logger=null;
	static DateFormat fullFormat=null;
	static Date objDate=null;
	
	
	/* Default executable function of the class HbiColorServiceReport */
	public static void main(String[] args) 
	{
		LCSLog.debug("### START HbiColorServiceReport.main() ###");
		
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
	        
	      //  validateAndWriteColorName();
	        System.exit(0);
		}
		catch (Exception exception) 
		{
			exception.printStackTrace();
			System.exit(1);
		}
		
		LCSLog.debug("### END HbiColorServiceReport.main() ###");
	}
	
	
	
	/**
	 * This function is invoking from the default executable function of the class to initiate the process of Solid Color  object.
	* @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void validateAndWriteColorName(WTObject wtObj) throws WTException,WTPropertyVetoException
	{
		//This method is used to get Solid Color Object from the PreparedQuertStatements from Solid Color Type.
		 Collection<LCSColor> CollOFSolidColorObjects = getSolidColorTypeObjects();
		 System.out.println("!!!!!!collectionOfSolidColorObj!!!!!!!" +CollOFSolidColorObjects.size());
		 for(LCSColor solidColorObj : CollOFSolidColorObjects)
		 {
			//System.out.println("!!!!!!color Name!!!!!!!" +solidColorObj.getName());
			//This method is used to process End of End Color Object to Yarn Dye Wovens color objects.
			getWhereUsed(solidColorObj);
		 }
	}
	
	
	/**
	 * This function is using to get collection of Solid color  objects. 
	 * @return collectionOfSolidColorObj - Collection<LCSColor>
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static Collection<LCSColor> getSolidColorTypeObjects() throws WTException,WTPropertyVetoException
	{
		Collection<LCSColor> collectionOfSolidColorObj = new ArrayList<LCSColor>();
		LCSColor solidColorObj = null;
		FlexType solidColorFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Color\\Solid"); 
		String id = solidColorFlexTypeObj.getTypeIdPath();
		Vector<FlexObject> listOfObjects = null;
		
		//Initializing the PreparedQueryStatement, which is using to get LCSColor object based on the given set of parameters(like FlexTypePath of the object). 
    	PreparedQueryStatement statement = new PreparedQueryStatement();
    	statement.appendFromTable(LCSColor.class);
    	statement.appendSelectColumn(new QueryColumn(LCSColor.class, "thePersistInfo.theObjectIdentifier.id"));
    	statement.appendAndIfNeeded();
		statement.appendCriteria(new Criteria(new QueryColumn(LCSColor.class, "flexTypeIdPath"), id, Criteria.EQUALS));
		
        SearchResults results = LCSQuery.runDirectQuery(statement);
        if(results != null && results.getResultsFound() > 0)
        {
			listOfObjects = results.getResults();
			for(FlexObject flexObj: listOfObjects)
			{
				solidColorObj = (LCSColor) LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSColor:"+flexObj.getString("LCSColor.IDA2A2"));
				collectionOfSolidColorObj.add(solidColorObj);
			}	
        }
		
		return collectionOfSolidColorObj;
	}
	/**
	 * This function is used to get color service value and then whereUsed of that
	 * @param endOfEndColorObj - LCSColor
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void getWhereUsed(LCSColor solidColorObj) throws WTException,WTPropertyVetoException
	{
		String colorService="pantone|~*~|";
		String colorServiceValue = (String)solidColorObj.getValue("hbiColorService");
		String colorName = (String)solidColorObj.getValue("name");
		if(colorService.equals(colorServiceValue))
		{
			System.out.println("!!!!!!inside where if!!!!!!!" +colorName);
			System.out.println("!!!!!!inside where if!!!!!!!" +colorServiceValue);
			Collection data = new Vector();
			data = new FAWhereUsedQuery().checkForObjectReferences(solidColorObj);
			if(data.size() == 0 )
			{
				System.out.println("!!!!!!after  if!!!!!!! No Object Refernce");
				Collection data1 = new Vector();
				SearchResults results = new LCSColorQuery().findMaterialsColorsForColor(solidColorObj);
				data1= results.getResults();
				if(data1.size() == 0 )
				{
					System.out.println("!!!!!!after  if!!!!!!! No Material Color Refernce");				
					Collection data2 = new Vector();
					data2 = new LCSColorQuery().findPalettesForColor(solidColorObj);
					if(data2.size() == 0 )
					{
						System.out.println("!!!!!!after  if!!!!!!! No Palette Refernce");
						logInfo(" >>> Color Names with colorservice is Pantone and where used in null :   " +colorName);
						//new LCSColorLogic().delete(solidColorObj);
						//logInfo(" >>> Color Deleted ");
					}
				}
			}
		
		}
	}	
	
	
	
	public static void logInfo(String infoMessage)
	{
		Date currentDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd-HHmmss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
		String date = dateFormat.format(new Date());
		String logFileName = "ColorNameWithoutWhereUsed-" + date;
		
		try
		{
			if(logger == null)
			{

				String location = "D:\\ColorName";
				
				//Creating custom log file using with the given filename, initializing writer to write/populate custom info and debug statement for the MasterJobProcessor.
				String strLogFile =  location + File.separator + logFileName +".log";
				LCSLog.debug("Log file Name = " + strLogFile);
				logger = new BufferedWriter(new FileWriter(strLogFile, true)); 
				fullFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG);
				objDate=new Date();
			}
			
			//Populating the given infoMessage into the custom log file along with the time stamp, flushing the content from the writer to the physical file which is needed to display
			logger.append(fullFormat.format(objDate)+"--->"+infoMessage);
			logger.newLine();
			logger.flush();
		}
		catch (IOException ioExp)
		{
			LCSLog.debug(" IOException in  custom log:: "+ ioExp);
		}

	}
	
	
}