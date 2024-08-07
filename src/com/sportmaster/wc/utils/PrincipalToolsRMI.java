package com.sportmaster.wc.utils;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.OrgContainer;
import wt.method.RemoteMethodServer;
import wt.org.DirectoryContextProvider;
import wt.org.OrganizationServicesHelper;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTProperties;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.StringTokenizer;


public class PrincipalToolsRMI {

    public PrincipalToolsRMI() {
    }

    public static String changeMail(WTUser user, String newMail) throws WTException, RemoteException, InvocationTargetException {
        return (String) RemoteMethodServer.getDefault().invoke(
                "changeMail",
                PrincipalTools.class.getName(),
                null,
                new Class[]{WTUser.class, String.class},
                new Object[]{user, newMail}
        );
    }

    public static String assignNewNames(WTUser user, String fName, String lName) throws WTException, RemoteException, InvocationTargetException {
        return (String) RemoteMethodServer.getDefault().invoke(
                "assignNewNames",
                PrincipalTools.class.getName(),
                null,
                new Class[]{WTUser.class, String.class, String.class},
                new Object[]{user, fName, lName}
        );
    }

    public static DirectoryContextProvider getOrgContextProvider() throws WTException, RemoteException, InvocationTargetException {
        DirectoryContextProvider result = null;
        QuerySpec qs = new QuerySpec();
        int orgIndex = qs.appendClassList(OrgContainer.class, true);
        SearchCondition sc = new SearchCondition(OrgContainer.class, "containerInfo.name", SearchCondition.EQUAL, "SportMaster");
        qs.appendWhere(sc);
        QueryResult qr = PersistenceHelper.manager.find(qs);
        OrgContainer org = null;
        if (qr.hasMoreElements()) {
            Persistable[] p = (Persistable[]) qr.nextElement();
            org = (OrgContainer) p[0];
        }
        if (org != null) result = org.getContextProvider();
        return result;
    }

    public static void processGroup(WTGroup group, String email) throws WTException, InvocationTargetException, RemoteException {
        if (group != null) {
            Enumeration members = OrganizationServicesHelper.manager.members(group);
            //  int counter = 0;
            //   System.out.println("Processing users of " + group.getName() + " group. New email for all users will be " + email);
            while (members.hasMoreElements()) {
                //    counter++;
                WTUser aUser = (WTUser) members.nextElement();
                //       System.out.println("i = " + counter + "  " + group.getName() + "  user = " + aUser.getName());
                changeMail(aUser, email);
            }
        }
    }

    public static void processGroup2(WTGroup group, String email) throws WTException, InvocationTargetException, RemoteException {
        if (group != null) {

            int i = 0;
            Enumeration qr = group.members();
            while (qr.hasMoreElements()) {
                Object obj = qr.nextElement();
                if (obj instanceof WTUser) {
                    WTUser theUser = (WTUser) obj;

                    i++;
                    System.out.println("N=" + i + " Processing user: " + theUser.getName() + ". New email for this users will be " + email);
                    changeMail(theUser, email);
                }
                if (obj instanceof WTGroup) {
                    WTGroup grp = (WTGroup) obj;
                    System.out.println("Found group: " + grp.getName());
                }

            }


        }
    }

    public static boolean isItProd() {
        String serverName = null;
        try {
            WTProperties props = WTProperties.getLocalProperties();
            serverName = props.getProperty("wt.server.codebase");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (serverName.contains("sportmaster-prod") || serverName.contains("plm-dev.")) return true;
        else return false;

    }

    public static void doChangeEmails()
            throws Exception {


        if (!isItProd()) {

            DirectoryContextProvider dcp = getOrgContextProvider();

            if (dcp != null) {

                // Process APD users
                WTGroup apd = OrganizationServicesHelper.manager.getGroup("APD", dcp);
                WTGroup apd_supplier = OrganizationServicesHelper.manager.getGroup("APD-Supplier", dcp);
                processGroup(apd, apd.getDescription().trim());
                processGroup(apd_supplier, apd_supplier.getDescription().trim());

                // Process FPD users
                WTGroup fpd = OrganizationServicesHelper.manager.getGroup("FPD", dcp);
                WTGroup fpd_supplier = OrganizationServicesHelper.manager.getGroup("FPD-Supplier", dcp);
                processGroup(fpd, fpd.getDescription().trim());
                processGroup(fpd_supplier, fpd_supplier.getDescription().trim());

                // Process SEPD users
                WTGroup sepd = OrganizationServicesHelper.manager.getGroup("SEPD", dcp);
                WTGroup sepd_supplier = OrganizationServicesHelper.manager.getGroup("SEPD-Supplier", dcp);
                processGroup(sepd, sepd.getDescription().trim());
                processGroup(sepd_supplier, sepd_supplier.getDescription().trim());
            }
        } else System.out.println("This script is not allowed to run on Prod Server.  So, do nothing....");
    }

    public static void doSwapNames() throws IOException, WTException, InvocationTargetException {
        String defaultfile = "users.txt";
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(defaultfile));
        } catch (IOException exception) {
            System.out.println("WARNING: file 'users.txt' not found!");
            System.exit(0);
        }

        String line;
        int i = 0;

        while ((line = in.readLine()) != null) {
            QuerySpec qs = new QuerySpec(WTUser.class);
            qs.appendWhere(new SearchCondition(WTUser.class, "name", "=", line.trim()));
            System.out.println("Looking for user " + line.trim());
            QueryResult qr = PersistenceHelper.manager.find(qs);

            while (qr.hasMoreElements()) {
                WTUser theUser = (WTUser) qr.nextElement();
                i++;
                System.out.print("N = " + i);
                System.out.print(", User Name = " + theUser.getName());
                System.out.print(", User Full Name = " + theUser.getFullName());
                System.out.println(", User Last Name = " + theUser.getLast());

                StringTokenizer st = new StringTokenizer(theUser.getFullName(), " ");
                String fName = st.nextToken();
                String lName = st.nextToken();
                if (lName.equals(theUser.getLast())) {
                    assignNewNames(theUser, lName, fName);
                }


            }
        }
        in.close();
    }

    public static void main(String[] num)
            throws Exception {
        if (num.length > 0) {

            switch (num[0]) {
                case "-changeEmails":
                    doChangeEmails();
                    break;
              /*  case "-swapNames":
                    doSwapNames();
                    break;*/
                default:
                    System.out.println("Use with key '-changeEmails'. Other keys are under development now");
            }
        } else System.out.println("Use with key '-changeEmails'. Other keys are under development now");

        System.exit(0);
    }
}

