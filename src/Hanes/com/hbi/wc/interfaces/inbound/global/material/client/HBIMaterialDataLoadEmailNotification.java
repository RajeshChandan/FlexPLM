package com.hbi.wc.interfaces.inbound.global.material.client;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import wt.method.MethodContext;
import wt.method.RemoteMethodServer;
import wt.session.SessionContext;
import wt.util.WTException;
import wt.util.WTProperties;

import com.lcs.wc.client.ClientContext;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;

/**
 * HBIMaterialDataLoadEmailNotification.java
 *
 * This class contains stand alone function as well as generic function, which are using to send email notifications to specific list of user and administrator
 * to notify about material data load issues as a part of material sync integration, this class contains functions which sends email notification for each file 
 * @author Vijayalaxmi.Shetty@Hanes.com
 * @since May-14-2018
 */
public class HBIMaterialDataLoadEmailNotification
{
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.interface.inbound.gobla.material.client.CLIENT_ADMIN_USER_ID", "Administrator");
    private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.interface.inbound.gobla.material.client.CLIENT_ADMIN_PASSWORD", "QAadmin");
    
	public static String RECIPIENT_EMAIL_TO = LCSProperties.get("com.hbi.wc.interface.inbound.gobla.material.client.HBIMaterialDataLoadEmailNotification.toRecipientList","vijayalaxmi.shetty@hanes.com");
	public static String RECIPIENT_EMAIL_BCC = LCSProperties.get("com.hbi.wc.interface.inbound.gobla.material.client.HBIMaterialDataLoadEmailNotification.bccRecipientList","vijayalaxmi.shetty@hanes.com");
    public static String SMTP_HOST = "";
	
    static 
	{
        try 
		{
            WTProperties wtproperties = WTProperties.getLocalProperties();
            SMTP_HOST = wtproperties.getProperty("wt.mail.mailhost");
        }
        catch (Exception exp) 
		{
        	LCSLog.debug("Exception in static block of the class HBIMaterialDataLoadEmailNotification is = "+exp);
            exp.printStackTrace();
        }
    }
    
	/**
	 * Default executable method of the class HBIMaterialDataLoadEmailNotification, this function begins sending email notification to users and administrator 
	 * @param args - String[]
	 */
	public static void main(String[] args)
	{
		LCSLog.debug("### START HBIMaterialDataLoadEmailNotification.main() ###");
		
		try
		{
			RemoteMethodServer remoteMethodServer = RemoteMethodServer.getDefault();
	        MethodContext mcontext = new MethodContext((String) null, (Object) null);
	        SessionContext sessioncontext = SessionContext.newContext();

			remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
	        remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);
	        
	        //Preparing input parameters (such as material data file object, using pre-defined path and hard-coded data file name to test email notifications
	        String materialDataFileLocation_HAA = HBIMaterialDataLoadProcessor.materialDataFileLocation_HAA;
	        String dataFileName = "MaterialDataFile_HAA _3.xls";
	        File materialDataFile = new File(materialDataFileLocation_HAA+File.separator+dataFileName);
			new HBIMaterialDataLoadEmailNotification().sendLoadFailureEmailNotification(materialDataFile, "HAA");
			
			System.exit(0);
		}
		catch (Exception exp)
		{
			LCSLog.debug("Exception in HBIMaterialDataLoadEmailNotification.main() = "+ exp);
			exp.printStackTrace();
			System.exit(1);
		}
		
		LCSLog.debug("### END HBIMaterialDataLoadEmailNotification.main() ###");
	}
    
    /**
     * This function is using send email notification to business users and administrators to notify the material data load issue along with the data file name
     * @param materialDataFile - File
     * @param dataSourceName - String
     * @throws WTException
     * @throws IOException
     */
    public void sendLoadFailureEmailNotification(File materialDataFile, String dataSourceName) throws WTException, IOException
    {
    	// LCSLog.debug("### START HBIMaterialDataLoadEmailNotification.sendLoadFailureEmailNotification(File materialDataFile, String dataSourceName) ###");
    	ClientContext context = ClientContext.getContext();
    	String SENDER_NAME = "";
    	String SENDER_EMAIL = "";
    	
    	//Get context user and context user email, call internal functions to get email subject line and email content and using all parameters to send email
    	if(context != null && context.getUser() != null && FormatHelper.hasContent(context.getUser().getEMail()))
    	{
    		SENDER_NAME = context.getUserName();
    		SENDER_EMAIL = context.getUser().getEMail();
    	}
    	String EMAIL_SUBJECT = getLoadFailureEmailSubjectLine(materialDataFile, dataSourceName);
    	String EMAIL_CONTENT = getLoadFailureEmailContent(materialDataFile, dataSourceName);
    	
    	//validate 'sender name' and 'sender email', based on validation status, invoke internal function to send email notification along with the file name
    	if(FormatHelper.hasContent(SENDER_NAME) && FormatHelper.hasContent(SENDER_EMAIL))
    	{
    		try
    		{
    			LCSLog.debug("SENDER_NAME = "+ SENDER_NAME+" SENDER_EMAIL = "+ SENDER_EMAIL);
    			sendLoadFailureEmailNotification(SENDER_NAME, SENDER_EMAIL, EMAIL_SUBJECT, EMAIL_CONTENT);
    		}
    		catch (MessagingException messExp)
    		{
				messExp.printStackTrace();
			}
    	}
    	
    	// LCSLog.debug("### END HBIMaterialDataLoadEmailNotification.sendLoadFailureEmailNotification(File materialDataFile, String dataSourceName) ###");
    }
    
    /**
     * This function is using to validate and initialize valid email subject line using to include in email notification to user to notify material load issue
     * @param materialDataFile - File
     * @param dataSourceName - String
     * @return
     */
    public String getLoadFailureEmailSubjectLine(File materialDataFile, String dataSourceName)
    {
    	// LCSLog.debug("### START HBIMaterialDataLoadEmailNotification.getLoadFailureEmailSubjectLine(File materialDataFile, String dataSourceName) ###");
    	String emailSubjectLine = "";
    	String materialDataFileName = materialDataFile.getName();
    	
    	//Validating the dataSourceName, based on the data-source name initializing email subject line, which is using to send email notification to users
    	if("HAA".equalsIgnoreCase(dataSourceName))
    	{
    		emailSubjectLine = "HAA : Material Data Load Failed in PLM For File - "+ materialDataFileName;
    	}
    	else if("HEI".equalsIgnoreCase(dataSourceName))
    	{
    		emailSubjectLine = "HEI : Material Data Load Failed in PLM For File - "+ materialDataFileName;
    	}
    	else if("CHEU".equalsIgnoreCase(dataSourceName))
    	{
    		emailSubjectLine = "Champions Europe : Material Data Load Failed in PLM For File - "+ materialDataFileName;
    	}
    	else
    	{
    		emailSubjectLine = "Material Data Load Failed in PLM For File - "+ materialDataFileName;
    	}
    	
    	// LCSLog.debug("### END HBIMaterialDataLoadEmailNotification.getLoadFailureEmailSubjectLine(File materialDataFile, String dataSourceName) ###");
    	return emailSubjectLine;
    }
    
    /**
     * This function is using to prepare email content along with the material data file name, format the final content and return content to a calling method
     * @param materialDataFile - File
     * @param dataSourceName - String
     * @return
     */
    public String getLoadFailureEmailContent(File materialDataFile, String dataSourceName)
    {
    	// LCSLog.debug("### START HBIMaterialDataLoadEmailNotification.getLoadFailureEmailContent(File materialDataFile, String dataSourceName) ###");
    	String materialDataFileName = materialDataFile.getName();
    	
    	String emailContent = "Hi All, "+System.getProperty("line.separator")+System.getProperty("line.separator")+
    	materialDataFileName+ " File Failed while loading to FlexPLM, for details refer "+materialDataFileName+ " File, or contact FlexPLM Support Team " +
    	"to understand the issue and fix the Data File for a successful load to PLM. " +
		System.getProperty("line.separator")+System.getProperty("line.separator")+
		"Thanks & Regards,"+System.getProperty("line.separator")+
		"FlexPLM Administrator";
    	
    	LCSLog.debug(emailContent);
    	// LCSLog.debug("### END HBIMaterialDataLoadEmailNotification.getLoadFailureEmailContent(File materialDataFile, String dataSourceName) ###");
    	return emailContent;
    }
    
    /**
     * This function is using to prepare and send email notification to a set of users or administrators to notify about the failure of material data sync INT
     * @param SENDER_NAME - String
     * @param SENDER_EMAIL - String
     * @param EMAIL_SUBJECT - String
     * @param EMAIL_CONTENT - String
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     */
    public void sendLoadFailureEmailNotification(String SENDER_NAME, String SENDER_EMAIL, String EMAIL_SUBJECT, String EMAIL_CONTENT) throws UnsupportedEncodingException, MessagingException
    {
    	// LCSLog.debug("### START HBIMaterialDataLoadEmailNotification.sendLoadFailureEmailNotification(senderName, senderMail, subject, emailContent) ###");
        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", SMTP_HOST);
        
        //
        Session sessionMail = Session.getDefaultInstance(prop, null);
        MimeMessage msg = new MimeMessage(sessionMail);
        msg.setFrom(new InternetAddress(SENDER_EMAIL, MimeUtility.encodeText(SENDER_NAME, "UTF-8", "B")));
        
        //
        InternetAddress[] toAdressArray = InternetAddress.parse(RECIPIENT_EMAIL_TO);
        msg.setRecipients(Message.RecipientType.TO, toAdressArray);
        InternetAddress[] bccAdressArray = InternetAddress.parse(RECIPIENT_EMAIL_BCC);
        msg.setRecipients(Message.RecipientType.BCC, bccAdressArray);
        msg.setSubject(EMAIL_SUBJECT);
		msg.setContent(EMAIL_CONTENT, "text/html;charset=UTF-8");
		
		//
		if (msg.getRecipients(Message.RecipientType.TO) != null || msg.getRecipients(Message.RecipientType.CC) != null)
		{
			Transport.send(msg);
		}
		else
		{
			LCSLog.debug("Material Data Load Email Notification Failed, system could not found Email addresses to who it should Notify the Data Load Failure");
		}
		
    	// LCSLog.debug("### END HBIMaterialDataLoadEmailNotification.sendLoadFailureEmailNotification(senderName, senderMail, subject, emailContent) ###");
    }
}