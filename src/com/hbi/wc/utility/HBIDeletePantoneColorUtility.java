package com.hbi.wc.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.hbi.wc.sample.HBISampleHelper;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.color.LCSColorLogic;
import com.lcs.wc.color.LCSColorQuery;
import com.lcs.wc.color.LCSColorHelper;
import com.lcs.wc.color.LCSPalette;
import com.lcs.wc.color.LCSPaletteLogic;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.Query;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterialColorQuery;
import com.lcs.wc.material.LCSMaterialHelper;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.product.LCSProductHelper;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.sample.LCSSample;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;
import com.lcs.wc.whereused.FAWhereUsedQuery;

import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPartMaster;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

import java.util.Date;
import java.io.BufferedWriter;
import java.text.DateFormat;
import java.io.File;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.io.FileWriter;
import java.util.TimeZone;
import java.io.IOException;


/**
 * HBIDeletePantoneColorUtility.java
 *
 * This class contains generic functions to fetch color references using where used function, iterate through each references to update color reference from one color to another for all
 * references (like material-color, sample, colorway and FlexBOMLink), using this utility we can replace multiple colors in a single execution and this utility will handle of existing data
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since May-04-2017
 */
public class HBIDeletePantoneColorUtility implements RemoteAccess
{
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "prodadmin");
    private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "pass2014a");
    private static RemoteMethodServer remoteMethodServer; 
	private static String floderPhysicalLocation = "";
	private static String colorDataFileName = "PantoneColorData.xls";
	static BufferedWriter logger=null;
	static DateFormat fullFormat=null;
	static Date objDate=null;

	static
	{
		try
		{
			WTProperties wtprops = WTProperties.getLocalProperties();
	        String home = wtprops.getProperty("wt.home");
	        floderPhysicalLocation = home + File.separator + "logs" + File.separator + "migration";
	        if(!(new File(floderPhysicalLocation).exists()))
	        {
	        	new File(floderPhysicalLocation).mkdir();
	        }
		}
		catch (Exception exp)
		{
			LCSLog.debug("Exception in static block of the class HBIColorReplaceUtility is : "+ exp);
		}
	}

        
    /** Default executable function of the class HBIDeletePantoneColorUtility */
    public static void main(String[] args) 
    {
        LCSLog.debug("### START HBIDeletePantoneColorUtility.main() ###");
        
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
			
			deletePantoneColor();
            System.exit(0);
        }
        catch(Exception exp) 
        {
        	exp.printStackTrace();
            System.exit(1);
        }

        LCSLog.debug("### END HBIDeletePantoneColorUtility.main() ###");
    }
    
    /**
     * This function is using as a plug-in function which is registered on LCSLifecycleManaged PRE_PERSIST EVENT to validate the business object type, invoke color reference change utility
     * @param wtObj - WTObject
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws SQLException
     */
    public static void deletePantoneColor( ) throws WTException, WTPropertyVetoException, SQLException,IOException
    {
		FileInputStream fileInputStreamObj = null;
		try
		{
			fileInputStreamObj = new FileInputStream(floderPhysicalLocation+File.separator+colorDataFileName);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStreamObj);
			HSSFSheet worksheet = workbook.getSheetAt(0);

			new HBIDeletePantoneColorUtility().deletePantoneColor(worksheet);
		}
		finally
    	{
    		if(fileInputStreamObj != null)
    		{
    			fileInputStreamObj.close();
    			fileInputStreamObj = null;
    		}
    	}
    }
	
	/**
     * This function is using to read all rows from the given data file to get Source Color unique identifier and 'Target Color' unique identifier to initialize and replace all references
     * @param worksheet - HSSFSheet
     * @throws WTException
     * @throws WTPropertyVetoException
     * @throws IOException
     * @throws SQLException
     */
    @SuppressWarnings("deprecation")
    public void deletePantoneColor(HSSFSheet worksheet) throws WTException, WTPropertyVetoException, IOException, SQLException
    {
    	HSSFRow row = null;
		int sourceColorSequenceNo = 0;
		int targetColorSequenceNo = 0;
		LCSColor sourceColorObj = null;
		LCSColor targetColorObj = null;
		
		for(int i=1; i<=100000; i++)
		{
			row = worksheet.getRow(i);
			if(row == null)
				break;
			
	    	sourceColorSequenceNo = (int) row.getCell((short) 0).getNumericCellValue();
			System.out.println(" <<<<  sourceColorSequenceNo >>>>"+sourceColorSequenceNo);
			
			//Calling internal function to get color object for the given unique parameters, using these two colors (source color and target color) to invoke color replace function 
			new HBIDeletePantoneColorUtility().deletePantoneColor(sourceColorSequenceNo, 0, "");
	    }
	}
	
	/**
     * This function is using to get color object using PreparedQueryStatement for the given criteria's ('Color Sequence', 'Color Service No' and Color Service Name) and return from header
     * @param colorSequnceNo - int
     * @param colorServiceNo - int
     * @param colorServiceName - String
     * @return colorObj - LCSColor
     * @throws WTException
     * @throws WTPropertyVetoException
     */
    public void deletePantoneColor(int colorSequenceNo, int colorServiceNo, String colorServiceName) throws WTException, WTPropertyVetoException
    {
    	// LCSLog.debug("### START HBIColorReplaceUtility.getColorObjectForCriteria(int colorSequenceNo, int colorServiceNo, String colorServiceName) ###");
    	FlexType colorFlexTypeObj = FlexTypeCache.getFlexTypeFromPath("Color");
    	String colorSequenceDBColumn = colorFlexTypeObj.getAttribute("hbiColorSequence").getVariableName();
		
		LCSColor colorObj = null;
		
    	//Initializing the PreparedQueryStatement, which is using to get LCSColor object based on the given set of parameters(like FlexTypePath of the object data and unique parameters)
    	PreparedQueryStatement statement = new PreparedQueryStatement();
    	statement.appendSelectColumn(new QueryColumn(LCSColor.class, "thePersistInfo.theObjectIdentifier.id"));
    	statement.appendFromTable(LCSColor.class);
    	statement.appendCriteria(new Criteria(new QueryColumn(LCSColor.class, colorSequenceDBColumn), "?", "="), new Long(colorSequenceNo));
    	
    	//Get SearchResults instance from the given PreparedQueryStatement instance, which is using to form LCSColor instance/object and returning the Collection of LCSColor objects
        SearchResults results = LCSQuery.runDirectQuery(statement);
        if(results != null && results.getResultsFound() > 0)
        {
        	FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
        	colorObj = (LCSColor) LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSColor:"+flexObj.getString("LCSColor.IDA2A2"));
			logInfo(" <<<< Color Name  >>>>"+colorObj.getIdentity());
			LCSColorHelper.service.deleteColor(colorObj);
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
	
    
    