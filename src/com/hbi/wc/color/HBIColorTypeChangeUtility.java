package com.hbi.wc.color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;

import com.lcs.wc.color.LCSColor;
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
import com.lcs.wc.util.LCSProperties;

import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

public class HBIColorTypeChangeUtility implements RemoteAccess
{
	public static String sourceDataFileName = "ColorTypeChangeDataFile.xls";
	public static String targetDataFileName = "ColorTypeChangeDataFile_Sys.xls";

	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID",
			"prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD",
			"pass2014a");
	private static RemoteMethodServer remoteMethodServer;
	private static String floderPhysicalLocation = "";
	private static Logger log = LogManager.getLogger(HBIColorTypeChangeUtility.class);
	private static String colorSeqKey = LCSProperties.get("com.hbi.wc.color.HBIColorTypeChangeUtility.colorseqKey",
			"hbiColorSequence");

	static 
	{
		try
		{
			WTProperties wtprops = WTProperties.getLocalProperties();
			String home = wtprops.getProperty("wt.home");
			floderPhysicalLocation = home + File.separator + "logs" + File.separator + "migration";
			if (!(new File(floderPhysicalLocation).exists())) {
				new File(floderPhysicalLocation).mkdir();
			}
		} 
		catch (Exception exp) {
			log.error("Exception in static block of the class HBIColorTypeChangeUtility is : " + exp);
		}
	}

	/* Default executable function of the class HBIColorTypeChangeUtility */
	public static void main(String[] args) 
	{
		log.info("### START HBIColorTypeChangeUtility.main() ###");
		long start = System.currentTimeMillis();
		try {
			// These contexts are needed for establishing connection to method
			// server- Do not remove these 2 below lines
			MethodContext mcontext = new MethodContext((String) null, (Object) null);
			SessionContext sessioncontext = SessionContext.newContext();
			// These contexts are needed for establishing connection to method
			// server- Do not remove these 2 above lines

			remoteMethodServer = RemoteMethodServer.getDefault();
			remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);

			GatewayAuthenticator authenticator = new GatewayAuthenticator();
			authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setAuthenticator(authenticator);
			Class[] argumentClass = { String.class, String.class };
			Object[] argumentObject = { sourceDataFileName, targetDataFileName };
			remoteMethodServer.invoke("validateAndUpdateColorType", "com.hbi.wc.color.HBIColorTypeChangeUtility", null,
					argumentClass, argumentObject);
			// validateAndUpdateColorType(sourceDataFileName,
			// targetDataFileName);

			long end = System.currentTimeMillis();
			NumberFormat formatter = new DecimalFormat("#0.00000");
			System.out.print("Execution time is :-->" + formatter.format((end - start) / 1000d) + " seconds");

			System.out.println("\n####### Ended Remote method server connection, please check logs in migration #####");
			System.out.println("####### Successfully logged off #####");
			System.exit(0);

		} 
		catch (Exception exception) {
			exception.printStackTrace();
			System.exit(1);
		}

		log.info("### END ColorTypeChangeUtility.main() ###");
	}

	/**
	 * This function is invoking from the default executable function of the
	 * class to initiate the process of material and material-supplier object
	 * type change to the newly given type path
	 * 
	 * @param sourceDataFileName
	 *            - String
	 * @param targetDataFileName
	 *            - String
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws IOException
	 */
	public static void validateAndUpdateColorType(String sourceDataFileName, String targetDataFileName) throws WTException, WTPropertyVetoException, IOException 
	{
		FileInputStream fileInputStreamObj = null;
		FileOutputStream fileOutputStreamObj = null;

		try 
		{
			fileInputStreamObj = new FileInputStream(floderPhysicalLocation + File.separator + sourceDataFileName);
			fileOutputStreamObj = new FileOutputStream(floderPhysicalLocation + File.separator + targetDataFileName);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStreamObj);
			HSSFSheet worksheet = workbook.getSheetAt(0);

			HBIColorTypeChangeUtility.validateAndUpdateColorType(worksheet);

			workbook.write(fileOutputStreamObj);
		} 
		catch (IOException ioExp) {
			ioExp.printStackTrace();
		} 
		finally {
			if (fileInputStreamObj != null) {
				fileInputStreamObj.close();
				fileInputStreamObj = null;
			}

			if (fileOutputStreamObj != null) {
				fileOutputStreamObj.close();
				fileOutputStreamObj = null;
			}
		}
	}

	/**
	 * This function is using to read each line from the given document then
	 * fetching color object from the given color sequence validating the
	 * material object new color type path
	 * 
	 * @param worksheet
	 *            - HSSFSheet
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void validateAndUpdateColorType(HSSFSheet worksheet) throws WTException, WTPropertyVetoException 
	{
		HSSFRow row = null;
		HSSFCell statusCell = null;
		String colorTypePathNew = "";
		LCSColor colorObj = null;
		LCSColor newcolorObj = null;
		String colorCode = "";
		String colorTypePath = "";
		DataFormatter formatter = new DataFormatter();
		
		for (int i = 1; i <= 10000; i++) 
		{
			row = worksheet.getRow(i);
			if (row != null) {

				try 
				{
					colorCode = formatter.formatCellValue(row.getCell(1));
					colorTypePath = formatter.formatCellValue(row.getCell(2));
					colorTypePathNew = formatter.formatCellValue(row.getCell(3));

					System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Color Code = " + colorCode + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Count = " + i);
					if (!(FormatHelper.hasContent(colorCode) && FormatHelper.hasContent(colorTypePath) && FormatHelper.hasContent(colorTypePathNew))) {
						continue;
					}

					// Calling a function to get Color object from the given
					// type
					// path, validate the color object and invoke internal
					// functions to change the type path to new given path
					newcolorObj = getColorObjectForCriteria(colorCode, colorTypePathNew);
					System.out.println(
							"color exists in new type :: " + newcolorObj + ", new Color Type = " + colorTypePathNew);

					if (newcolorObj == null) {
						// Calling a function is using to get Color object from
						// the
						// given Color Sequence, validate the color object and
						// invoke internal function using for type change
						colorObj = getColorObjectForCriteria(colorCode, colorTypePath);
						System.out.println(
								"color from existing type :: " + colorObj + ", existing Color Type = " + colorTypePath);

						if (colorObj != null) {
							updateColorForTypeChange(colorObj, colorTypePathNew);

							statusCell = row.createCell((short) 4);
							statusCell.setCellValue("Type_Changed");
						} else {
							statusCell = row.createCell((short) 4);
							statusCell.setCellValue("Color not found");
						}
					} else {
						statusCell = row.createCell((short) 4);
						statusCell.setCellValue("Color Already Exists in the new Type");
					}

				}
				catch (Exception e) {
					System.out.println("!!!!! Completed Transactions for SEQUENCE :: [" + colorCode + "], with Errors !!!!");

					statusCell = row.createCell((short) 4);
					statusCell.setCellValue("FAILED");
					e.printStackTrace();
				}
			}
			else {
				break;
			}
		}
	}

	public static void updateColorForTypeChange(LCSColor colorObj, String colorTypePathNew)
			throws WTException, WTPropertyVetoException 
	{
		FlexType colorFlexTypeObj = getColorFlexType(colorTypePathNew);
		// Get Type OID from the given FlexType, using RetypeLogic API to change
		// the type of the Color object from an existing type to the newly
		// given type and persist the object
		String colorTypeOID = FormatHelper.getObjectId(colorFlexTypeObj);

		String colorOID = FormatHelper.getObjectId(colorObj);

		colorOID = RetypeLogic.changeType(colorOID, colorTypeOID);

	}

	public static LCSColor getColorObjectForCriteria(String colorCode, String colorTypePath)
			throws WTException, WTPropertyVetoException 
	{
		LCSColor colorObj = null;
		FlexType colorFlexTypeObj = getColorFlexType(colorTypePath);
		String typeIdPath = colorFlexTypeObj.getTypeIdPath();

		String colorCodeDBColumn = colorFlexTypeObj.getAttribute(colorSeqKey).getColumnDescriptorName();// .getVariableName()

		// Initializing the PreparedQueryStatement, which is using to get
		// LCSColor object based on the given set of parameters(like
		// FlexTypePath, unique id's)
		PreparedQueryStatement statement = new PreparedQueryStatement();

		statement.appendSelectColumn(new QueryColumn(LCSColor.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.appendFromTable(LCSColor.class);
		statement.appendCriteria(new Criteria(new QueryColumn(LCSColor.class, "flexTypeIdPath"), "?", "="), typeIdPath);
		statement.appendAndIfNeeded();
		statement.appendCriteria(
				new Criteria(new QueryColumn(LCSColor.class, colorCodeDBColumn), colorCode, Criteria.EQUALS));

		// Get SearchResults instance from the given PreparedQueryStatement
		// instance, which is using to form LCSColor instance/object and
		// returning the Collection of LCSColor
		SearchResults results = LCSQuery.runDirectQuery(statement);
		if (results != null && results.getResultsFound() > 0) {
			FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
			colorObj = (LCSColor) LCSQuery
					.findObjectById("OR:com.lcs.wc.color.LCSColor:" + flexObj.getString("LCSColor.IDA2A2"));
		}

		return colorObj;
	}

	public static FlexType getColorFlexType(String colorTypePath) throws WTException 
	{
		FlexType colorFlexTypeObj = null;
		Collection<FlexType> flexTypesColl = new ArrayList<FlexType>();

		// Initialize all FlexTypes
		FlexType rootFlexType = FlexTypeCache.getFlexTypeFromPath("Color");
		flexTypesColl.add(rootFlexType);
		if (rootFlexType.getAllChildren() != null) {
			flexTypesColl.addAll(rootFlexType.getAllChildren());
		}

		// Iterate FlexTypes collection to validate with the given FlexTypePath
		// and return the corresponding FlexType Object
		for (FlexType flexTypeObj : flexTypesColl) {

			if (colorTypePath.equalsIgnoreCase(flexTypeObj.getFullNameDisplay(true))) {
				colorFlexTypeObj = flexTypeObj;
				break;
			}
		}

		return colorFlexTypeObj;
	}
}
