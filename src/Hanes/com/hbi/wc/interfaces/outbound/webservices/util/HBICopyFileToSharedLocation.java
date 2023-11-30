package com.hbi.wc.interfaces.outbound.webservices.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTProperties;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

public class HBICopyFileToSharedLocation {

	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "qaadmin");
	private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "QAadmin");
	private static RemoteMethodServer remoteMethodServer;
	public static final boolean WCPART_ENABLED = LCSProperties.getBoolean("com.lcs.wc.specification.parts.Enabled");
	
	private static String imagesSourceLocation = "";
	
	private static String destLocation =LCSProperties.get("com.hbi.wc.integration.imagessharedlocation", "\\\\wsplmappdev1\\measurements");
	
	static
	{
		try
		{
			WTProperties wtprops = WTProperties.getLocalProperties();
	        String wt_home = wtprops.getProperty("wt.home");
	        String images_home = LCSProperties.get("com.hbi.wc.interfaces.outbound.webservices.util.HBIIntegrationServerUtil.imagesHome", HBIProperties.imagesHome);
	        imagesSourceLocation = wt_home + images_home;
		}
		catch (IOException exp)
		{
			LCSLog.debug("IOException in static block of the class HBICopyFileToSharedLocation is : "+ exp);
		}
	}
	/**
	 * @param args
	 */
	

	public boolean uploadFile(String sourceFileName) throws WTException
	{
		LCSLog.debug("### START HBICopyFileToSharedLocation.uploadFile(fileName) ###");
		
		
		LCSLog.debug("imagesSourceLocation -----> "+imagesSourceLocation);
		
		
		
		
		String destinationFileName = destLocation + File.separator + sourceFileName;
		sourceFileName=imagesSourceLocation+File.separator + sourceFileName;
		//System.out.println("com.hbi.wc.interfaces.outbound.webservices.util.HBIIntegrationServerUtil.checkFileExists  destinationFileName "+destinationFileName);

		boolean fileDownloadStatus = false;

		try
		{
			File destFile = new File(destinationFileName);

			

			if(!destFile.exists()){

				File sourceFile = new File(sourceFileName);
			    				
				FileUtils.copyFile(sourceFile, destFile);
				
fileDownloadStatus=true;
				

			}


		}
		catch (IOException ioExp)
		{
			LCSLog.debug("IOException in HBICopyFileToSharedLocation.uploadFile(fileName, destinationLocation) :: "+ ioExp);
			
		}

		LCSLog.debug("### END HBICopyFileToSharedLocation.uploadFile(fileName, destinationLocation) ###");
		LCSLog.debug("### END FILE UPLOADED: "+destinationFileName);
		return fileDownloadStatus;
	}







}
