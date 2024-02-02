package com.hbi.wc.utility;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;

import com.lcs.wc.foundation.LCSQuery;

import com.lcs.wc.util.FormatHelper;

import com.lcs.wc.util.LCSProperties;

import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.util.WTPropertyVetoException;

public class HBIDisableUser implements RemoteAccess {

	private static String sourceDataFileName = "UserInformation.xls";
	private static String targetDataFileName = "UserInformation_Sys.xls";
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID",
			"prodadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD",
			"pass2014a");
	private static RemoteMethodServer remoteMethodServer;

	private static Logger log = LogManager.getLogger(HBIDisableUser.class);

	private static String folderPhysicalLocation = "";

	static {
		try {
			WTProperties wtprops = WTProperties.getLocalProperties();
			String home = wtprops.getProperty("wt.home");
			folderPhysicalLocation = home + File.separator + "logs" + File.separator + "migration" + File.separator;
			if (!(new File(folderPhysicalLocation).exists())) {
				new File(folderPhysicalLocation).mkdir();
			}
		} catch (Exception exp) {
			log.debug("Exception in static block of the class : " + exp);
		}
	}

	/* Default executable function of the class HBIDisableUser */
	public static void main(String[] args) {
		log.info("### START HBIDisableUser.main() ###");
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
			remoteMethodServer.invoke("validateAndUpdateUserInformation", "com.hbi.wc.utility.HBIDisableUser", null,
					argumentClass, argumentObject);

			long end = System.currentTimeMillis();
			NumberFormat formatter = new DecimalFormat("#0.00000");
			System.out.print("Execution time is :-->" + formatter.format((end - start) / 1000d) + " seconds");

			System.out.println("\n####### Ended Remote method server connection, please check logs in migration #####");
			System.out.println("####### Successfully logged off #####");
			System.exit(0);

		} catch (Exception exception) {
			exception.printStackTrace();
			System.exit(1);
		}

		log.info("### END HBIDisableUser.main() ###");
		
	}
		
		
		
		/**
		 * This function is invoking from the default executable function of the
		 * class to initiate the process to disable WTuser object 
		 * 
		 * 
		 * @param sourceDataFileName
		 *            - String
		 * @param targetDataFileName
		 *            - String
		 * @throws WTException
		 * @throws WTPropertyVetoException
		 * @throws IOException
		
		*/
	

	public static void validateAndUpdateUserInformation(String sourceDataFileName, String targetDataFileName)
			throws WTException, WTPropertyVetoException, IOException {

		FileInputStream fileInputStreamObj = null;
		FileOutputStream fileOutputStreamObj = null;

		try {
			fileInputStreamObj = new FileInputStream(folderPhysicalLocation + File.separator + sourceDataFileName);
			fileOutputStreamObj = new FileOutputStream(folderPhysicalLocation + File.separator + targetDataFileName);
			HSSFWorkbook workbook = new HSSFWorkbook(fileInputStreamObj);
			HSSFSheet worksheet = workbook.getSheetAt(0);

			HBIDisableUser.disableUser(worksheet);

			workbook.write(fileOutputStreamObj);
		} catch (IOException ioExp) {
			ioExp.printStackTrace();
		} finally {
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
	 * fetching WTUser object from the given userName after validating the
	 * user name and performing disable user and setting results
	 * 
	 * @param worksheet
	 *            - HSSFSheet
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws IOException 
	 */
	
	
	
	
	public static void disableUser(HSSFSheet worksheet) throws WTException, WTPropertyVetoException, IOException {

		HSSFRow row = null;
		HSSFCell statusCell = null;
		String userName = "";

		DataFormatter formatter = new DataFormatter();
		for (int i = 0; i <= 10000; i++) {
			row = worksheet.getRow(i);
			if (row != null) {

				try {

					userName = formatter.formatCellValue(row.getCell(0));

					System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! User Name = " + userName
							+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Count = " + i);
					if (!(FormatHelper.hasContent(userName)

					)) {
						continue;
					}

					WTUser user = getWTUserForCriteria(userName);
					System.out.println("User is " + user);
					if (user != null) {

						user = (WTUser) OrganizationServicesHelper.manager.disablePrincipal((WTPrincipal) user);

						statusCell = row.createCell((short) 2);
						statusCell.setCellValue("User_Disabled");

					} else {
						statusCell = row.createCell((short) 2);
						statusCell.setCellValue("User was not found or is null");
					}

				} catch (Exception e) {
					System.out.println(
							"!!!!! Completed Transactions for SEQUENCE :: [" + userName + "], with Errors !!!!");

					statusCell = row.createCell((short) 2);
					statusCell.setCellValue("FAILED");

					e.printStackTrace();
				}

			}

			else {
				break;
			}
		}

	}
	
	
	
	
	
	/**
	 * This function is using to get WTUser
	 * @param userName - String
	
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */

	public static WTUser getWTUserForCriteria(String userName) throws WTException, WTPropertyVetoException {

		WTUser user = null;

		PreparedQueryStatement statement = new PreparedQueryStatement();
		statement.appendFromTable(WTUser.class);
		statement.appendSelectColumn(new QueryColumn(WTUser.class, "thePersistInfo.theObjectIdentifier.id"));
		statement.appendSelectColumn(new QueryColumn(WTUser.class, "name"));
		statement.appendCriteria(new Criteria(new QueryColumn(WTUser.class, "name"), "?", Criteria.EQUALS), userName);

		SearchResults results = LCSQuery.runDirectQuery(statement);

		if (results != null && results.getResultsFound() > 0) {
			FlexObject flexObj = (FlexObject) results.getResults().iterator().next();
			user = (WTUser) LCSQuery.findObjectById("wt.org.WTUser:" + flexObj.getString("WTUSER.IDA2A2"));

		}

		return user;
	}

}