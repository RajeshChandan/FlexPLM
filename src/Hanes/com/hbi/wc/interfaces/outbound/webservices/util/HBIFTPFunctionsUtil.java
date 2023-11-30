package com.hbi.wc.interfaces.outbound.webservices.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import wt.util.WTException;
import wt.util.WTProperties;

import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

/**
 * HBIFTPFunctionsUtil.java
 * 
 * This class contains generic functions which are using to send/share images, data files and map files from source server to target server using secured file transfer protocol(SFTP)
 * @author Abdul.Patel@Hanes.com
 * @since  June-10-2015
 */
public class HBIFTPFunctionsUtil 
{
    private static final String FTP_HOSTNAME = LCSProperties.get("com.hbi.stg.extractors.vrd.ftp");
    private static final String FTP_USERNAME = LCSProperties.get("com.hbi.stg.extractors.vrd.ftp.username");
    private static final String FTP_PASSWORD = LCSProperties.get("com.hbi.stg.extractors.vrd.ftp.password");
    private static String imagesSourceLocation = "";
	private static FTPClient ftpClientObj = null;
	
	static
	{
		try
		{
			WTProperties wtprops = WTProperties.getLocalProperties();
	        String wt_home = wtprops.getProperty("wt.home");
	        String images_home = LCSProperties.get("com.lcs.wc.content.imagefilePath");
	        imagesSourceLocation = wt_home + images_home;
		}
		catch (IOException exp)
		{
			LCSLog.debug("IOException in static block of the class HBIFTPFunctionsUtil is : "+ exp);
		}
	}
	
	/**
	 * This function is using to initialize FTPClient for the pre-defined host name and login details(username and password) and FTP file type with the given complete file path/name  
	 * @param ftpFilePath - String
	 * @return ftpClientObj - FTPClient 
	 * @throws IOException
	 */
	public FTPClient getSFTPConnection(String ftpFilePath) throws IOException
	{
		LCSLog.debug("### START HBIFTPFunctionsUtil.getSFTPConnection(ftpFilePath) ###");
		
		//Validate an existing FTLClient connection(with the Pre-defined HostName, Login details), if connection exists then return the existing connection from the function header
		if(ftpClientObj != null && ftpClientObj.isConnected())
		{
			return ftpClientObj;
		}
		
		ftpClientObj = new FTPClient();
		ftpClientObj.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		ftpClientObj.connect(FTP_HOSTNAME);
		LCSLog.debug("### HBIFTPFunctionsUtil.getSFTPConnection() :: FTP URL is :" +ftpClientObj.getDefaultPort());
		
		//Initialized FTPClient and connected with the given Host, checking the connectivity status, and performing the action based on FTP client reply code
		int reply = ftpClientObj.getReplyCode();
		LCSLog.debug(" HBISFTPFunctionsUtil.getSFTPConnection() :: FTP Connection Status :" +reply);
        if (!FTPReply.isPositiveCompletion(reply)) 
        {
        	ftpClientObj.disconnect();
            throw new IOException("Exception in connecting to FTP Server, for the Host : "+ FTP_HOSTNAME);
        }
        
        //After successfully connecting to FTP for the given HostName, provide login details(username and password) for authentication & set other parameters(Like File Type, Directory)
        ftpClientObj.login(FTP_USERNAME, FTP_PASSWORD);
        ftpClientObj.changeWorkingDirectory(ftpFilePath);
        ftpClientObj.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClientObj.enterLocalPassiveMode();    
		
		LCSLog.debug("### END HBIFTPFunctionsUtil.getSFTPConnection(ftpFilePath) ###");
		return ftpClientObj;
	}
	
	/**
	 * This function is using to validate the FTPClient connectivity status within the context, if the FTPClient is connected, then disconnect from the connected host to process request
	 * @throws IOException
	 */
	public void closeSFTPConnection() throws IOException
	{
		LCSLog.debug("### START HBIFTPFunctionsUtil.closeSFTPConnection() ###");
		
		//validate the FTPClient connectivity status, if the FTPClient is connected, then disconnect from the connected host.
		if (ftpClientObj != null && this.ftpClientObj.isConnected()) 
		{
			this.ftpClientObj.disconnect();
		}
		
		LCSLog.debug("### END HBIFTPFunctionsUtil.closeSFTPConnection() ###");
	}
	
	/**
	 * This function is using to to initialize FTPClient for predefined Host, initialize FileInputStream from the given File & store the file from local directory to the FTP directory
	 * @param fileName - String
	 * @param ftpDestinationLocation - String
	 * @throws WTException
	 */
	public void uploadSFTPFile(String fileName, String ftpDestinationLocation) throws WTException
	{
		LCSLog.debug("### START HBIFTPFunctionsUtil.uploadSFTPFile(fileName, ftpDestinationLocation) ###");
		fileName = imagesSourceLocation + File.separator + fileName;
		File fileObj = new File(fileName);
		InputStream inputStreamObj = null;
		
		try
		{
			//Calling a function which is using to initialize FTPClient for the pre-defined host name, login details and FTP file type with the given complete file path/name
			FTPClient ftpClientObj = getSFTPConnection(ftpDestinationLocation);
			
			//validate the existence of an given image, store/move image from local directory to the SFTP directory only if the given image does not exists in SFTP location
			inputStreamObj = ftpClientObj.retrieveFileStream(fileObj.getName());
			if(inputStreamObj == null || ftpClientObj.getReplyCode() == 550)
			{
				//initialize FileInputStream from the given File, passing FileInputStream instance and file name to store the file from local directory to the FTP directory
				inputStreamObj = new FileInputStream(fileObj);
	            ftpClientObj.storeFile(fileObj.getName(), inputStreamObj);
	            
	            //Passing command to the FTPClient instance to set file permissions(allowing other users to read/write the files which are transferred from SFTP user)
	            ftpClientObj.sendSiteCommand("chmod "+ "775 "+ ftpDestinationLocation+"/"+fileObj.getName());
			}
		}
		catch (IOException ioExp)
		{
			LCSLog.debug("IOException in HBIFTPFunctionsUtil.uploadSFTPFile(fileName, ftpDestinationLocation) is :: "+ ioExp);
			throw new WTException("IOException in HBIFTPFunctionsUtil.uploadSFTPFile(fileName, ftpDestinationLocation) is :: "+ ioExp.getMessage());
		}
		finally
		{
			try
			{
				if(inputStreamObj != null)
				{
					inputStreamObj.close();
					inputStreamObj = null;
				}
			}
			catch (IOException ioExp)
			{
				LCSLog.debug("IOException in HBIFTPFunctionsUtil.uploadSFTPFile(fileName, ftpDestinationLocation) finally block is :: "+ ioExp);
			}
		}
		
		LCSLog.debug("### END HBIFTPFunctionsUtil.uploadSFTPFile(fileName, ftpDestinationLocation) ###");
	}
}