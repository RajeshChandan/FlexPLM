//Package Definition
package com.hbi.wc.util;

//Flex Import Statements
import com.lcs.wc.country.LCSCountryQuery;
import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.supplier.LCSSupplierQuery;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.client.ClientContext;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;

import wt.method.RemoteMethodServer;
import wt.util.WTProperties;
import wt.org.WTPrincipal;
import wt.org.WTUser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import wt.httpgw.*;
import javax.mail.*;
import javax.mail.internet.*;

import javax.activation.*;

import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.session.SessionContext;
import wt.session.SessionHelper;


public class HBIExtractVendorDetail implements RemoteAccess{
	//D:\ptc\Windchill_11.2\Windchill\codebase\com\hbi\wc\material
	private static final String INFILE = "D:\\ptc\\Windchill_11.2\\Windchill\\data\\Data_Vendor_Detail.txt";
	private static final String OUTFILE = "D:\\ptc\\Windchill_11.2\\Windchill\\data\\Data_Vendor_Detail_OUT.txt";
	private static final String MAPFILE = "D:\\ptc\\Windchill_11.2\\Windchill\\codebase\\com\\hbi\\wc\\util\\Map_Vendor_Detail.txt";
	private static final String ERRFILE = "D:\\ptc\\Windchill_11.2\\Windchill\\data\\Data_Vendor_Detail_err.txt";
	private static final String ERRFILE1 = "D:\\ptc\\Windchill_11.2\\Windchill\\data\\Vendor_Detail_errored.txt";	
	private static final String LOGFILE = "D:\\ptc\\Windchill_11.2\\Windchill\\logs\\LOAD_VEN_DETAIL.log";
	/**
	 * This static field contains path of the exception log file added by Wipro team for INCIDENT 820679
	 */
	private static  final String EXCEPTION_LOGFILE_NAME = LCSProperties.get("com.hbi.wc.util.HBIExtractVendorDetail.EXCEPTION_LOGFILE","LOAD_VEN_DETAIL_Exception.txt");
	/**
	 * To be used to store exception file path for INCIDENT 820679
	 */
	private static  String EXCEPTION_LOGFILE = "";
	//private static final String LOGFILE1 = "D:\\ptc\\Windchill_11.2\\Windchill\\logs\\LOAD_VEN_DETAIL1.log";
	private static HashMap venDetMap = new HashMap();
	public static String SMTP_HOST =  "";
	private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "Administrator");
    //private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "Administrator");

    /**
     * Below static field defined to add multiple email addresses  by Wipro team for INCIDENT 820679
     */
    private static String RECIPIENT_EMAIL = LCSProperties.get("com.hbi.wc.util.HBIExtractVendorDetail.RECIPIENT_EMAIL", "abdul.wajid@hanes.com");
    /**
     * Below field is used  to define the server name in the emails for INCIDENT 820679.
     */
    private static String SERVER_NAME = "";
	
    static 
    {
        try
        {
            WTProperties wtproperties = WTProperties.getLocalProperties();
            SMTP_HOST =  wtproperties.getProperty("wt.mail.mailhost");
            //below code added by wipro team for INCIDENT 820679
            SERVER_NAME=  wtproperties.getProperty("wt.rmi.server.hostname");
            EXCEPTION_LOGFILE = wtproperties.getProperty("wt.home") + File.separator + "logs" + File.separator + EXCEPTION_LOGFILE_NAME;
            
        } catch(Exception e){
            e.printStackTrace();
        }
    }	
	
	
	public static void loadVendorDetail(String file) {
		BufferedReader br = null;

			try {
				String line;
				HashMap vendorMap;
 
				br = new BufferedReader(new FileReader(file));
 
				while ((line = br.readLine()) != null) {
					
					String[] arrtokens = splitTotokens(line, "\t");
					String token = "";
			
					vendorMap = new HashMap();
					Integer I = new Integer(1);
			
					for(int i=0; i<arrtokens.length; i++){
						token = arrtokens[i];
						System.out.println("token " + I + " : " + token);
						if(token != null)
							vendorMap.put(I, token.trim());
						else
							vendorMap.put(I, "");
						I++;
					}
			
					venDetMap.put(vendorMap.get(1)+" - "+vendorMap.get(2), vendorMap);
				}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Exception = " + ex.getLocalizedMessage());
		}
	}
	
	private static String[] splitTotokens(String line, String delim){
		String s = line;
		int i = 0;

		while (s.contains(delim)) {
			s = s.substring(s.indexOf(delim) + delim.length());
			i++;
		}

		String token = null;
		String remainder = null;
		String[] tokens = new String[i + 1];

		for (int j = 0; j < i; j++) {
			token = line.substring(0, line.indexOf(delim));
			tokens[j] = token;
			remainder = line.substring(line.indexOf(delim) + delim.length());
			line = remainder;
		}
		tokens[i] = line;
		return tokens; 
	 }
 
	private static boolean validateVendor(String VendorName) throws Exception{
		System.out.println(">>>>>>inside validateVendor>>>>>");
		if(VendorName.equals(""))
			return false;
			
		LCSSupplierQuery SUPPLIER_QUERY = new LCSSupplierQuery();
		FlexType supplierType = FlexTypeCache.getFlexTypeFromPath("Supplier\\Supplier");
		try{
		LCSSupplier supplier = SUPPLIER_QUERY.findSupplierByNameType(VendorName, supplierType);
			if(supplier != null)
				return true;
			else
				return false;
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception = " + e.getLocalizedMessage());
			return false;
		}
		
		
	}
	
	private static boolean validateCountry(String Country) throws Exception{
		System.out.println(">>>>>>>>>>>>inside validateCountry>>>>>>>>");
		System.out.println(">>>>>>>>>>>>>>>>>>>validateCountry Country"+Country);
		if(Country.equals("") | Country.length() <= 0)
			return true;
		
		LCSCountryQuery COUNTRY_QUERY = new LCSCountryQuery();
		//FlexType countryType = FlexTypeCache.getFlexTypeRoot("Country");//.getFlexTypeFromPath("Country");
		FlexType countryType = new LCSCountry().getFlexType();
		System.out.println(">>>>>>>>>>>>countryType>>>>>>>>>"+Country+" : "+countryType);
		LCSCountry country = COUNTRY_QUERY.findCountryByNameType(Country, countryType);
		
		if(country != null)
			return true;
		else
			return false;
	}

	private static boolean validateLocation(String location) throws Exception{
		System.out.println(">>>>>>>>>>>>inside validateLocation>>>>>>>>");
		if(location.equals(""))
			return false;
		else
			return true;
	}

	public static void generateDataFile() {
	
	HashMap venMap = new HashMap();
	PrintWriter outfile = null;
	PrintWriter errfile = null;
	
	String vendorMasterCd = "";
	String suppliername = "";
	String location = "";
	String city = "";
	String locnum = "";
	String country = "";
	String key = "";
	
	StringBuffer data = null;
	
	try {
	outfile = new PrintWriter(new BufferedWriter(new FileWriter(OUTFILE)));
	errfile = new PrintWriter(new BufferedWriter(new FileWriter(ERRFILE)));
	Iterator iterator = venDetMap.keySet().iterator();
	while ( iterator.hasNext() ){
		
		key = (String) iterator.next();
		venMap = (HashMap) venDetMap.get(key);
		data = new StringBuffer();

		suppliername = (String) venMap.get(new Integer(1));
		location = (String) venMap.get(new Integer(2));
		city = (String) venMap.get(new Integer(3));
		locnum = (String) venMap.get(new Integer(4));
		country = (String) venMap.get(new Integer(5));
		vendorMasterCd = (String) venMap.get(new Integer(6));		
		
		System.out.println("Ven Map " + venMap);
		System.out.println("suppliername " + suppliername );
		System.out.println("country " + country );
		System.out.println(">>>>>>>>>>>location "+location+" "+city+" "+locnum+" "+country+" "+vendorMasterCd);
		//if(validateVendor(suppliername) && validateCountry(country) && validateLocation(location)) {
		if(validateCountry(country) && validateLocation(location)) {
			data.append("LCSLifecycleManaged");
			data.append("\t");
			//data.append(key);
			//data.append("\t");
			data.append(suppliername);
			data.append("\t");
			data.append(location);
			data.append("\t");
			data.append(city);
			data.append("\t");
			data.append(locnum);
			data.append("\t");
			data.append(country);
			data.append("\t");
			data.append(vendorMasterCd);			
			
			outfile.println(data.toString());
		
		} else {
			//data.append(key);	
			//data.append("\t");
			data.append(suppliername);
			data.append("\t");
			data.append(location);
			data.append("\t");
			data.append(city);
			data.append("\t");
			data.append(locnum);
			data.append("\t");
			data.append(country);
			data.append("\t");
			data.append(vendorMasterCd);			
			System.out.println("data " + data.toString());
			errfile.println(data.toString());
			/*if(!validateVendor(suppliername)) {
				errfile.println("!!!!!INVALID VENDOR!!!!!");
			} else */
			if(!validateCountry(country)) {
				errfile.println("!!!!!INVALID COUNTRY CODE!!!!!");
			} else if(!validateLocation(location)) {
				errfile.println("!!!!!BLANK LOCATION!!!!!");
			} else {
				errfile.println("!!!!!ERROR!!!!!");	
			}			
		}
		
	}
	} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Exception = " + ex.getLocalizedMessage());
	} finally {
		if (outfile != null) {
			outfile.close();
		}
		if (errfile != null) {
			errfile.close();
		}
	}
	}
	
	public static void sendErrorFiles() throws Exception{
		ClientContext lcsContext = ClientContext.getContext();
		String SENDER_NAME       = lcsContext.getUserName();
		String SENDER_EMAIL = "";
	
		try{
			WTUser wtuserToName = lcsContext.getUser();
//			System.out.println("wtuserToName>>>>>>>>>>>>>>>>"+wtuserToName);
//			System.out.println("wtuserToName>>>>>>>>>>>>>>>>"+wtuserToName.getEMail());
			if(wtuserToName != null && FormatHelper.hasContent(wtuserToName.getEMail())){
				SENDER_EMAIL = wtuserToName.getEMail().toString();
			}else {
				SENDER_EMAIL = lcsContext.getUserName();
			}
		}catch(java.lang.NullPointerException wte){
			SENDER_EMAIL = lcsContext.getUserName();
			wte.printStackTrace();
		}
		//Commented by Wipro Team for INCIDENT 820679
		
		//String RECIPIENT_EMAIL = "HBI_UST_FLEXPLM_SUPPORT@hanes.com";
		
		//String CCRECIPIENT_EMAIL = "anoop.sasikumar@hanesbrands.com";
		String EMAIL_SUBJECT   = SERVER_NAME +": Vendor Detail - Rejected records" ; 
		String EMAIL_TEXT      = "Attached files contain the error records which got rejected while loading to Flex PLM on "+SERVER_NAME;
		int    SMTP_PORT       = 25;

		// Create the message and transport objects:
		Properties prop = System.getProperties();
		prop.put("mail.smtp.host", SMTP_HOST);
		Session sessionMail = Session.getDefaultInstance(prop,null);
		MimeMessage msg = new MimeMessage(sessionMail);	   
		//System.out.println("RECIPIENT_EMAIL>>>>>>>>>"+RECIPIENT_EMAIL);
		msg.setFrom( new InternetAddress( SENDER_EMAIL,MimeUtility.encodeText( SENDER_NAME,"UTF-8","B")));
		
		//below code added by wipro for INCIDENT 820679
		//msg.setRecipient( Message.RecipientType.TO, new InternetAddress( RECIPIENT_EMAIL ))	;
		msg.setRecipients(Message.RecipientType.TO, RECIPIENT_EMAIL);
		
		//msg.setSubject(EMAIL_SUBJECT);
		//msg.setRecipient( Message.RecipientType.CC, new InternetAddress( CCRECIPIENT_EMAIL ) );	
        // Send it! Catch the TransportException.
        try {

			// Attach file with message
			File file1 =  new File(ERRFILE);
			File file2 =  new File(ERRFILE1);
			File exceptionLogFile = new File(EXCEPTION_LOGFILE);
			//Wipro Team Changed the below code to send the successful notification for INCIDENT 820679.
			if ((file1.exists() && file1.length()!=0L )|| (file2.exists() && file2.length()!=0L) || (exceptionLogFile.exists() && exceptionLogFile.length()!=0L)) {
				// create and fill the first message part
				javax.mail.Part mbp1 = new MimeBodyPart();	   
				mbp1.setContent(EMAIL_TEXT,"text/html;charset=utf-8");
				msg.setSubject(EMAIL_SUBJECT);

				// create the second message part
				MimeBodyPart mbp2 = new MimeBodyPart();

				// attach the file to the message
				FileDataSource fds = null;

				// create the Multipart and its parts to it
				Multipart mp = new MimeMultipart("mixed");
				mp.addBodyPart((MimeBodyPart)mbp1);
				if (file1.exists())
				{
					fds = new FileDataSource(ERRFILE);
					mbp2 = new MimeBodyPart();
					mbp2.setDataHandler(new DataHandler(fds));
					mbp2.setFileName(fds.getName());
					mp.addBodyPart(mbp2,mp.getCount());
				}
				if (file2.exists())
				{
					fds = new FileDataSource(ERRFILE1);
					mbp2 = new MimeBodyPart();
					mbp2.setDataHandler(new DataHandler(fds));
					mbp2.setFileName(fds.getName());
					mp.addBodyPart(mbp2,mp.getCount());
				}
				if (exceptionLogFile.exists() && exceptionLogFile.length()!=0L)
				{
					//System.out.println("logFile>>>>>>>>>>>>>"+exceptionLogFile.getName());
					fds = new FileDataSource(EXCEPTION_LOGFILE);
					mbp2 = new MimeBodyPart();
					mbp2.setDataHandler(new DataHandler(fds));
					mbp2.setFileName(fds.getName());
					mp.addBodyPart(mbp2,mp.getCount());
				}
				// add the Multipart to the message
				msg.setContent(mp);

			}
			else
			{
				 EMAIL_SUBJECT   = SERVER_NAME +": Vendor Detail - JOB Successful" ; 
				 EMAIL_TEXT      = "All the Vendor Location Objects were loaded successfully into FlexPLM on " +SERVER_NAME;
				 msg.setSubject(EMAIL_SUBJECT);
				 msg.setContent(EMAIL_TEXT,"text/html;charset=utf-8");
			}

            if(msg.getRecipients(Message.RecipientType.TO) !=null || msg.getRecipients(Message.RecipientType.CC) !=null){
                Transport.send( msg );
            }else{
				System.out.println("Sending Failed : NO Email Addresses" );
            }
        }
        catch( Exception te ) {
            System.out.println( "Transport Exception: " + te.getLocalizedMessage() );  
        }			
	}
  
	public static void mainOld(String argv[]) {
				
		try {
			MethodContext mcontext = new MethodContext((String) null, (Object) null);
			SessionContext sessioncontext = SessionContext.newContext();
			RemoteMethodServer remoteMethodServer = RemoteMethodServer.getDefault();
	        GatewayAuthenticator authenticator = new GatewayAuthenticator();
			authenticator.setRemoteUser(CLIENT_ADMIN_USER_ID);
			remoteMethodServer.setAuthenticator(authenticator);
			WTPrincipal principal = SessionHelper.manager.getPrincipal();
			
			/*loadVendorDetail(INFILE);
			generateDataFile();
			com.lcs.wc.load.LoadFile.performLoad(OUTFILE,MAPFILE,"TAB",LOGFILE);
			sendErrorFiles();*/
			//loadVendorAgreement(INFILE);
			Class[] loadVendorClass = {String.class};
			Object[] loadVendorObject = {INFILE};
			remoteMethodServer.invoke("loadVendorDetail", "com.hbi.wc.util.HBIExtractVendorDetail", null, loadVendorClass, loadVendorObject);
			
			//generateDataFile();
			Class[] dataFileClass = {};
			Object[] dataFileObject = {};
			remoteMethodServer.invoke("generateDataFile", "com.hbi.wc.util.HBIExtractVendorDetail", null, dataFileClass, dataFileObject);
			
			com.lcs.wc.load.LoadFile.performLoad(OUTFILE,MAPFILE,"TAB",LOGFILE);
			
			/*Class[] argumentClass = {String.class,String.class,String.class,String.class};
			Object[] argumentObject = {OUTFILE,MAPFILE,"TAB",LOGFILE};
			remoteMethodServer.invoke("performLoad", "com.lcs.wc.load.LoadFile", null, argumentClass, argumentObject);*/
			//sendErrorFiles();
			Class[] sendErrorFileClass = {};
			Object[] sendErrorFileObject = {};
			remoteMethodServer.invoke("sendErrorFiles", "com.hbi.wc.util.HBIExtractVendorDetail", null, sendErrorFileClass, sendErrorFileObject);
			
			
			
		} catch (Exception ex) {
			System.out.println("Exception = " + ex.getLocalizedMessage());
		} 
		System.exit(0);
	}
	

	
	public static void main(String[] args) 
	{
//		String ADMIN_USER = "IntegrationUser";
//		String ADMIN_PASSWORD = "hbiIntPass";
		
		try
		{
			System.out.println("Execution Started !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
			
			if(RemoteMethodServer.ServerFlag)
			{
				triggerLoadFile();
				System.exit(0);
			}
			else
			{
				System.out.println("RMI Call Started !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
				/* remoteMethodServer = RemoteMethodServer.getDefault();
			        remoteMethodServer.setUserName(ADMIN_USER);
			        remoteMethodServer.setPassword(ADMIN_PASSWORD);
			        Class[] paramClass = new Class[0];
			        Object[] params = new Object[0];
			        remoteMethodServer.invoke("triggerLoadFile", "com.hbi.wc.util.HBIExtractVendorAgreement", null, paramClass, params);
				*/
				
				MethodContext mcontext = new MethodContext((String) null, (Object) null);
				SessionContext sessioncontext = SessionContext.newContext();
				RemoteMethodServer remoteMethodServer = RemoteMethodServer.getDefault();
				 
				//System.out.println("SERVER_NAME>>>>>>>>>>>>>>>>>>>>>"+SERVER_NAME);
				
				/*remoteMethodServer = RemoteMethodServer.getDefault();
				remoteMethodServer.setUserName(ADMIN_USER);
				remoteMethodServer.setPassword(ADMIN_PASSWORD);*/
				
				GatewayAuthenticator auth = new GatewayAuthenticator();
				//auth.setRemoteUser(CLIENT_ADMIN_USER_ID);
				auth.setRemoteUser("prodadmin");
				remoteMethodServer.setAuthenticator(auth);
				
				Class<?> paramClass[] = {};
				Object params[] = {};
				//System.out.println("method called from else block");
				remoteMethodServer.invoke("triggerLoadFile", "com.hbi.wc.util.HBIExtractVendorDetail", null, paramClass, params);
				
				
				System.out.println("RMI Call Completed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
				System.exit(0);
			}
			
			System.out.println("Execution Completed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
			System.out.println("Exception = " + ex.getLocalizedMessage());
			System.exit(1);
		} 
		
	}
	
	public static void triggerLoadFile() throws Exception
	{
		System.out.println("triggerLoadFile Execution Started !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
		
		//
		loadVendorDetail(INFILE);
		
		//
		generateDataFile();
		//System.out.println("before *******************com.lcs.wc.load.LoadFile.performLoad method call");
		com.lcs.wc.load.LoadFile.performLoad(OUTFILE,MAPFILE,"TAB",LOGFILE);
		//System.out.println("After ********************com.lcs.wc.load.LoadFile.performLoad method call");
		//
		findErrorInLogs(LOGFILE,EXCEPTION_LOGFILE);
		sendErrorFiles();
		
		System.out.println("triggerLoadFile Execution Completed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
	}
	/**
	 * @param path for the original LOGFILE
	 * @param path for the EXCEPTION_LOGFILE
	 * @throws IOException
	 * This method finds any exceptions in the Logs and writes it to the allocated exception log file
	 * as a requirement of INCIDENT 820679
	 */
	public static void findErrorInLogs(String LOGFILE, String EXCEPTION_LOGFILE) throws IOException{
		//String exception = "";
		 DataInputStream in = null;
		 BufferedWriter out = null;
		try {
            // input from log file
            FileInputStream fstream = new FileInputStream(LOGFILE);
            in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            // output file to write exception
            out = new BufferedWriter(new FileWriter(EXCEPTION_LOGFILE));
 
            String strLine;
            Boolean broken = false;
            int line = 0;
 
            while ((strLine = br.readLine()) != null)
            {
                if (strLine.contains("Exception")) {
                    broken = true;
                }
                if (broken)
                {
                   //exception += strLine + "\n";
                	out.write(strLine);
                    out.newLine();
                    line++;
                }
                if (line == 10) {    // print next 10 lines after exception
                    broken = false;
                    line = 0;
                    out.newLine();
                    out.newLine();
                    //break;
                }
            }
            
            System.out.println(" Exceptions to the file written successfully");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }finally {
        	in.close();
            out.close();
		}
    }
		
	
}
