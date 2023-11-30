package com.hbi.wc.util;

import com.hbi.wc.util.HBIExtractVendorAgreement;
import com.lcs.wc.client.ClientContext;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.load.LoadFile;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.supplier.LCSSupplierQuery;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import wt.httpgw.GatewayAuthenticator;
import wt.method.MethodAuthenticator;
import wt.method.MethodContext;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTUser;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTProperties;

public class HBIExtractVendorAgreement implements RemoteAccess {
  private static final String INFILE = "D:\\ptc\\Windchill_11.2\\Windchill\\data\\Data_Vendor_Agreement.txt";
  
  private static final String OUTFILE = "D:\\ptc\\Windchill_11.2\\Windchill\\data\\Data_Vendor_Agreement_OUT.txt";
  
  private static final String MAPFILE = "D:\\ptc\\Windchill_11.2\\Windchill\\codebase\\com\\hbi\\wc\\util\\Map_Vendor_Agreement.txt";
  
  private static final String ERRFILE = "D:\\ptc\\Windchill_11.2\\Windchill\\data\\Data_Vendor_Agreement_err.txt";
  
  private static final String ERRFILE1 = "D:\\ptc\\Windchill_11.2\\Windchill\\data\\Vendor_Agreement_errored.txt";
  
  private static final String LOGFILE = "D:\\ptc\\Windchill_11.2\\Windchill\\logs\\LOAD_VEN_AGREEMENT.log";
  
  private static final String LOGFILE1 = "D:\\ptc\\Windchill_11.2\\Windchill\\logs\\LOAD_VEN_AGREEMENT1.log";
  
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
  
  private static HashMap venAgmtMap = new HashMap<>();
  
  public static String SMTP_HOST = "";
  
  private static String CLIENT_ADMIN_USER_ID = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_USER_ID", "Administrator");
  
  private static String CLIENT_ADMIN_PASSWORD = LCSProperties.get("com.hbi.wc.integration.CLIENT_ADMIN_PASSWORD", "Administrator");
  
  private static RemoteMethodServer remoteMethodServer;
  
  static {
    try {
      WTProperties wtproperties = WTProperties.getLocalProperties();
      SMTP_HOST = wtproperties.getProperty("wt.mail.mailhost");
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
  
  public static void loadVendorAgreement(String file) {
    BufferedReader br = null;
    System.out.println(">>>>>>>>>>>>>>>>inside loadVendorAgreement<<<<<<<<<<<<<<<<<<<");
    try {
      br = new BufferedReader(new FileReader(file));
      String line;
      while ((line = br.readLine()) != null) {
        String[] arrtokens = splitTotokens(line, "\t");
        String token = "";
        HashMap<Object, Object> vendorMap = new HashMap<>();
        Integer I = new Integer(1);
        for (int i = 0; i < arrtokens.length; i++) {
          token = arrtokens[i];
          if (token != null) {
            vendorMap.put(I, token);
          } else {
            vendorMap.put(I, "");
          } 
          I = Integer.valueOf(I.intValue() + 1);
        } 
        venAgmtMap.put((new StringBuilder()).append(vendorMap.get(Integer.valueOf(3))).append(" - ").append(vendorMap.get(Integer.valueOf(1))).toString(), vendorMap);
      } 
    } catch (Exception ex) {
     // String line;
      ex.printStackTrace();
      System.out.println("Exception = " + ex.getLocalizedMessage());
    } 
  }
  
  private static String[] splitTotokens(String line, String delim) {
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
  
  private static boolean validateVendor(String VendorName) throws Exception {
    LCSSupplierQuery SUPPLIER_QUERY = new LCSSupplierQuery();
    if (VendorName.equals(""))
      return false; 
    FlexType supplierType = FlexTypeCache.getFlexTypeFromPath("Supplier\\Supplier");
    try {
      LCSSupplier supplier = SUPPLIER_QUERY.findSupplierByNameType(VendorName, supplierType);
      if (supplier != null)
        return true; 
      return false;
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Exception = " + e.getLocalizedMessage());
      return false;
    } 
  }
  
  private static boolean validateAgmtRef(String agmtref) throws Exception {
    if (agmtref.equals(""))
      return false; 
    if (agmtref.indexOf("MH-") != -1)
      return false; 
    return true;
  }
  
  public static void generateDataFile() {
    HashMap<Object, Object> venMap = new HashMap<>();
    PrintWriter outfile = null;
    PrintWriter errfile = null;
    String vendorMasterCd = "";
    String venagmtref = "";
    String location = "";
    String suppliername = "";
    String effectivedtStr = "";
    String expiredtStr = "";
    String key = "";
    StringBuffer data = null;
    try {
      outfile = new PrintWriter(new BufferedWriter(new FileWriter("D:\\ptc\\Windchill_11.2\\Windchill\\data\\Data_Vendor_Agreement_OUT.txt")));
      errfile = new PrintWriter(new BufferedWriter(new FileWriter("D:\\ptc\\Windchill_11.2\\Windchill\\data\\Data_Vendor_Agreement_err.txt")));
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
      SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy");
      Iterator<String> iterator = venAgmtMap.keySet().iterator();
      while (iterator.hasNext()) {
        key = iterator.next();
        venMap = (HashMap<Object, Object>)venAgmtMap.get(key);
        data = new StringBuffer();
        venagmtref = (String)venMap.get(new Integer(1));
        location = (String)venMap.get(new Integer(2));
        suppliername = (String)venMap.get(new Integer(3));
        effectivedtStr = (String)venMap.get(new Integer(4));
        String effectivedt = sdf.format(sdf1.parse(effectivedtStr));
        expiredtStr = (String)venMap.get(new Integer(5));
        String expiredt = sdf.format(sdf1.parse(expiredtStr));
        vendorMasterCd = (String)venMap.get(new Integer(6));
        if (validateAgmtRef(venagmtref)) {
          data.append("LCSLifecycleManaged");
          data.append("\t");
          data.append(key);
          data.append("\t");
          data.append(venagmtref);
          data.append("\t");
          data.append(location);
          data.append("\t");
          data.append(suppliername);
          data.append("\t");
          data.append(effectivedt);
          data.append("\t");
          data.append(expiredt);
          data.append("\t");
          data.append(vendorMasterCd);
          outfile.println(data.toString());
          continue;
        } 
        data.append(key);
        data.append("\t");
        data.append(venagmtref);
        data.append("\t");
        data.append(location);
        data.append("\t");
        data.append(suppliername);
        data.append("\t");
        data.append(effectivedt);
        data.append("\t");
        data.append(expiredt);
        data.append("\t");
        data.append(vendorMasterCd);
        errfile.println(data.toString());
        if (!validateVendor(suppliername)) {
          errfile.println("!!!!!INVALID VENDOR!!!!!");
          continue;
        } 
        if (!validateAgmtRef(venagmtref))
          errfile.println("!!!!!INVALID VENDOR AGREEMENT REF!!!!!"); 
      } 
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Exception = " + ex.getLocalizedMessage());
    } finally {
      if (outfile != null)
        outfile.close(); 
      if (errfile != null)
        errfile.close(); 
    } 
  }
  
  public static void sendErrorFiles() throws Exception {
    ClientContext lcsContext = ClientContext.getContext();
    String SENDER_NAME = lcsContext.getUserName();
    String SENDER_EMAIL = "";
    try {
      WTUser wtuserToName = lcsContext.getUser();
      if (wtuserToName != null && FormatHelper.hasContent(wtuserToName.getEMail())) {
        SENDER_EMAIL = wtuserToName.getEMail().toString();
      } else {
        SENDER_EMAIL = lcsContext.getUserName();
      } 
    } catch (NullPointerException wte) {
      SENDER_EMAIL = lcsContext.getUserName();
      wte.printStackTrace();
    } 
    String RECIPIENT_EMAIL = "HBI_UST_FLEXPLM_SUPPORT@hanes.com";
    String EMAIL_SUBJECT = "Vendor Agreement - Rejected records";
    String EMAIL_TEXT = "Attached files contain the error records which got rejected while loading to Flex PLM";
    int SMTP_PORT = 25;
    Properties prop = System.getProperties();
    prop.put("mail.smtp.host", SMTP_HOST);
    Session sessionMail = Session.getDefaultInstance(prop, null);
    MimeMessage msg = new MimeMessage(sessionMail);
    msg.setFrom((Address)new InternetAddress(SENDER_EMAIL, MimeUtility.encodeText(SENDER_NAME, "UTF-8", "B")));
    msg.setRecipient(Message.RecipientType.TO, (Address)new InternetAddress(RECIPIENT_EMAIL));
    try {
      File file1 = new File("D:\\ptc\\Windchill_11.2\\Windchill\\data\\Data_Vendor_Agreement_err.txt");
      File file2 = new File("D:\\ptc\\Windchill_11.2\\Windchill\\data\\Vendor_Agreement_errored.txt");
      if (file1.exists() || file2.exists()) {
        MimeBodyPart mimeBodyPart1 = new MimeBodyPart();
        mimeBodyPart1.setContent(EMAIL_TEXT, "text/html;charset=utf-8");
        MimeBodyPart mbp2 = new MimeBodyPart();
        FileDataSource fds = null;
        MimeMultipart mimeMultipart = new MimeMultipart("mixed");
        mimeMultipart.addBodyPart((BodyPart)mimeBodyPart1);
        if (file1.exists()) {
          fds = new FileDataSource("D:\\ptc\\Windchill_11.2\\Windchill\\data\\Data_Vendor_Agreement_err.txt");
          mbp2 = new MimeBodyPart();
          mbp2.setDataHandler(new DataHandler(fds));
          mbp2.setFileName(fds.getName());
          mimeMultipart.addBodyPart((BodyPart)mbp2, mimeMultipart.getCount());
        } 
        if (file2.exists()) {
          fds = new FileDataSource("D:\\ptc\\Windchill_11.2\\Windchill\\data\\Vendor_Agreement_errored.txt");
          mbp2 = new MimeBodyPart();
          mbp2.setDataHandler(new DataHandler(fds));
          mbp2.setFileName(fds.getName());
          mimeMultipart.addBodyPart((BodyPart)mbp2, mimeMultipart.getCount());
        } 
        msg.setContent((Multipart)mimeMultipart);
      } else {
        msg.setContent(EMAIL_TEXT, "text/html;charset=utf-8");
      } 
      if (msg.getRecipients(Message.RecipientType.TO) != null || msg.getRecipients(Message.RecipientType.CC) != null) {
        Transport.send((Message)msg);
      } else {
        System.out.println("Sending Failed : NO Email Addresses");
      } 
    } catch (Exception te) {
      System.out.println("Transport Exception: " + te.getLocalizedMessage());
    } 
  }
  
  public static void mainold(String[] argv) {
    try {
      System.out.println("<<<<<RemoteMethodServer.ServerFlag>>>>>" + RemoteMethodServer.ServerFlag);
      String serverUrl = "http://wsflexplm14.res.hbi.net/Windchill/";
      String serviceName = "MethodServer";
      URL url = new URL(serverUrl);
      SessionContext.newContext();
      SessionContext.getContext();
      SessionHelper.manager.setAdministrator();
      RemoteMethodServer remoteMethodServer = RemoteMethodServer.getDefault();
      MethodContext methodContext = MethodContext.getContext();
      //remoteMethodServer.setUserName("prodadmin"); 
      //remoteMethodServer.setPassword("admin2021");
      remoteMethodServer.setUserName(CLIENT_ADMIN_USER_ID);
      remoteMethodServer.setPassword(CLIENT_ADMIN_PASSWORD);
      System.out.println(">>>>>>>>>>>>>>>>>>>>" + remoteMethodServer.getInfo());
      System.out.println(">>>>>>>>>>>>>>>>>>>>" + SessionHelper.manager.getPrincipal().getName());
      System.out.println(">>>>>>>>>>>>>>>>>>>>" + SessionHelper.manager.getPrincipal().getRepository());
      GatewayAuthenticator authenticator = new GatewayAuthenticator();
      authenticator.setRemoteUser("IntegrationUser");
      remoteMethodServer.setAuthenticator((MethodAuthenticator)authenticator);
      Class[] loadVendorClass = { String.class };
      Object[] loadVendorObject = { "D:\\ptc\\Windchill_11.2\\Windchill\\data\\Data_Vendor_Agreement.txt" };
      remoteMethodServer.invoke("loadVendorAgreement", "com.hbi.wc.util.HBIExtractVendorAgreement", null, loadVendorClass, loadVendorObject);
      Class[] dataFileClass = new Class[0];
      Object[] dataFileObject = new Object[0];
      remoteMethodServer.invoke("generateDataFile", "com.hbi.wc.util.HBIExtractVendorAgreement", null, dataFileClass, dataFileObject);
      Class[] sendErrorFileClass = new Class[0];
      Object[] sendErrorFileObject = new Object[0];
      remoteMethodServer.invoke("sendErrorFiles", "com.hbi.wc.util.HBIExtractVendorAgreement", null, sendErrorFileClass, sendErrorFileObject);
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Exception = " + ex.getLocalizedMessage());
    } 
    System.exit(0);
  }
  
  public static void main(String[] args) {
    String ADMIN_USER = "IntegrationUser";
    String ADMIN_PASSWORD = "hbiIntPass";
    try {
      System.out.println("Execution Started !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
      if (RemoteMethodServer.ServerFlag) {
        triggerLoadFile();
        System.exit(0);
      } else {
        System.out.println("RMI Call Started !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
        /*remoteMethodServer = RemoteMethodServer.getDefault();
        remoteMethodServer.setUserName(ADMIN_USER);
        remoteMethodServer.setPassword(ADMIN_PASSWORD);*/
        
        MethodContext mcontext = new MethodContext((String) null, (Object) null);
		SessionContext sessioncontext = SessionContext.newContext();
		RemoteMethodServer remoteMethodServer = RemoteMethodServer.getDefault();
		GatewayAuthenticator auth = new GatewayAuthenticator();
		//auth.setRemoteUser(CLIENT_ADMIN_USER_ID);
		auth.setRemoteUser("prodadmin");
		remoteMethodServer.setAuthenticator(auth);
        
        Class[] paramClass = new Class[0];
        Object[] params = new Object[0];
        remoteMethodServer.invoke("triggerLoadFile", "com.hbi.wc.util.HBIExtractVendorAgreement", null, paramClass, params);
        System.exit(0);
        System.out.println("RMI Call Completed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
      } 
      System.out.println("Execution Completed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
    } catch (Exception ex) {
      ex.printStackTrace();
      System.out.println("Exception = " + ex.getLocalizedMessage());
      System.exit(1);
    } 
  }
  
  public static void triggerLoadFile() throws Exception {
    System.out.println("triggerLoadFile Execution Started !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
    loadVendorAgreement("D:\\ptc\\Windchill_11.2\\Windchill\\data\\Data_Vendor_Agreement.txt");
    generateDataFile();
    LoadFile.performLoad("D:\\ptc\\Windchill_11.2\\Windchill\\data\\Data_Vendor_Agreement_OUT.txt", "D:\\ptc\\Windchill_11.2\\Windchill\\codebase\\com\\hbi\\wc\\util\\Map_Vendor_Agreement.txt", "TAB", "D:\\ptc\\Windchill_11.2\\Windchill\\logs\\LOAD_VEN_AGREEMENT1.log");
    sendErrorFiles();
    System.out.println("triggerLoadFile Execution Completed !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ");
  }
}
