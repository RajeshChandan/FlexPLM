package com.hbi.wc.interfaces.outbound.webservices.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import wt.util.WTException;
import wt.util.WTProperties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

/**
 * HBISFTPFunctionsUtil.java
 * 
 * This class contains generic functions which are using to send/share images, data files and map files from source server to target server using secured file transfer protocol(SFTP)
 * @author Abdul.Patel@Hanes.com
 * @since  May-9-2015
 */
public class HBISFTPFunctionsUtil 
{
    private static final String FTP_HOSTNAME = LCSProperties.get("com.hbi.stg.extractors.vrd.ftp");
    private static final String FTP_USERNAME = LCSProperties.get("com.hbi.stg.extractors.vrd.ftp.username");
    private static final String FTP_PASSWORD = LCSProperties.get("com.hbi.stg.extractors.vrd.ftp.password");
    private static final int FTP_PORTNUMBER = Integer.parseInt(LCSProperties.get("com.hbi.stg.extractors.vrd.ftp.port"));
    private static String imagesSourceLocation = "";
    private static ChannelSftp channelSftpObj = null;
    private static Session session = null;
	
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
			LCSLog.debug("IOException in static block of the class HBISFTPFunctionsUtil is : "+ exp);
		}
	}
	
	/**
	 * This function is using to initialize SFTPClient for the pre-defined host name and login details(username and password) and SFTP file type with the given complete file path/name  
	 * @param sftpFilePath - String
	 * @return channelSftpObj - ChannelSftp 
	 * @throws IOException
	 */
	public ChannelSftp getSFTPConnection(String sftpFilePath) throws IOException, JSchException, SftpException
	{
		LCSLog.debug("### START HBISFTPFunctionsUtil.getSFTPConnection(ftpFilePath) ###");
		
		//Validate an existing SFTLChannel connection(with the Pre-defined HostName and Login details), if connection exists then return the existing connection from a function header
		if(channelSftpObj != null && channelSftpObj.isConnected())
		{
			return channelSftpObj;
		}
		
		//Initializing secureConnection object, get current session from secure connection, provide server authentication details, set any default properties and connect to SFTP server
		JSch secureConnectionObj = new JSch();
        session = secureConnectionObj.getSession(FTP_USERNAME, FTP_HOSTNAME, FTP_PORTNUMBER);
        session.setPassword(FTP_PASSWORD);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        LCSLog.debug("### HBISFTPFunctionsUtil.getSFTPConnection(ftpFilePath) : SFTP Session Connected...");
        
        //Get Channel instance from the current session, connect to a channel, convert the instance from Channel to SFTPChannel and set SFTP FilePath to the SFTP Channel instance
        Channel channel = session.openChannel("sftp");
        channel.connect();
        channelSftpObj = (ChannelSftp) channel;
        channelSftpObj.cd(sftpFilePath);
        LCSLog.debug("### HBISFTPFunctionsUtil.getSFTPConnection(ftpFilePath) : SFTP Channel Connected...");
       
		LCSLog.debug("### END HBISFTPFunctionsUtil.getSFTPConnection(ftpFilePath) ###");
		return channelSftpObj;
	}
	
	/**
	 * This function is using to validate the SFTPClient(SFTP Channel) connectivity status within the context, if the SFTPClient is connected, then disconnect from the connected host 
	 * @throws IOException
	 */
	public void closeSFTPConnection() throws IOException
	{
		LCSLog.debug("### START HBISFTPFunctionsUtil.closeSFTPConnection() ###");
		
		//validate the ChannelSftp (SFTP Server) connectivity status, if the ChannelSftp (SFTP Server) is connected, then disconnect from the connected host.
		if (channelSftpObj != null && channelSftpObj.isConnected()) 
		{
			channelSftpObj.disconnect();
		}
		
		LCSLog.debug("### END HBISFTPFunctionsUtil.closeSFTPConnection() ###");
	}
	
	/**
	 * This function is using to initialize SFTPClient for predefined Host, initialize FileInputStream from the given File & store the file from local directory to the SFTP directory
	 * @param fileName - String
	 * @param ftpDestinationLocation - String
	 * @throws WTException
	 */
	public void uploadSFTPFile(String fileName, String ftpDestinationLocation) throws WTException
	{
		LCSLog.debug("### START HBISFTPFunctionsUtil.uploadSFTPFile(fileName, ftpDestinationLocation) ###");
		fileName = imagesSourceLocation + File.separator + fileName;
		File fileObj = new File(fileName);
		
		try
		{
			//Calling a function which is using to initialize SFTPClient for the pre-defined host name, login details and SFTP file type with the given complete file path/name
			ChannelSftp channelSftpObj = getSFTPConnection(ftpDestinationLocation);
			
			//validate the existence of an given image, store/move image from local directory to the SFTP directory only if the given image does not exists in SFTP location
			InputStream inputStreamObj = new FileInputStream(fileObj);
			channelSftpObj.put(inputStreamObj, fileObj.getName());
		}
		catch (IOException ioExp)
		{
			LCSLog.debug("IOException in HBISFTPFunctionsUtil.uploadSFTPFile(fileName, ftpDestinationLocation) is :: "+ ioExp);
			throw new WTException("IOException in HBISFTPFunctionsUtil.uploadSFTPFile(fileName, ftpDestinationLocation) is :: "+ ioExp.getMessage());
		}
		catch (JSchException jschExp)
		{
			LCSLog.debug("JSchException in HBISFTPFunctionsUtil.uploadSFTPFile(fileName, ftpDestinationLocation) is :: "+ jschExp);
			throw new WTException("JSchException in HBISFTPFunctionsUtil.uploadSFTPFile(fileName, ftpDestinationLocation) is :: "+ jschExp.getMessage());
		}
		catch (SftpException sftpExp)
		{
			LCSLog.debug("SftpException in HBISFTPFunctionsUtil.uploadSFTPFile(fileName, ftpDestinationLocation) is :: "+ sftpExp);
			throw new WTException("SftpException in HBISFTPFunctionsUtil.uploadSFTPFile(fileName, ftpDestinationLocation) is :: "+ sftpExp.getMessage());
		}
		
		LCSLog.debug("### END HBISFTPFunctionsUtil.uploadSFTPFile(fileName, ftpDestinationLocation) ###");
	}
}