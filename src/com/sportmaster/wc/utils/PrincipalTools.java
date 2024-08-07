package com.sportmaster.wc.utils;

import wt.fc.PersistenceServerHelper;
import wt.method.RemoteAccess;
import wt.org.WTUser;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;


public class PrincipalTools implements RemoteAccess
{


    public static void changeMail(WTUser user, String newMail) throws WTException, WTPropertyVetoException {
        user.setEMail(newMail);
        PersistenceServerHelper.manager.update(user);
    }

    public static void assignNewNames (WTUser user, String fName, String lName) throws WTException {
        user.setFullName(fName + " " + lName);
        PersistenceServerHelper.manager.update(user);
    }


}

