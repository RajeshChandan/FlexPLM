package com.lowes.cs.utils;

import com.lowes.web.util.AppUtil;
import org.json.simple.JSONObject;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.load.LoadServerHelper;
import wt.org.OrganizationServicesHelper;
import wt.org.WTUser;
import wt.org._WTPrincipal;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.Hashtable;
import java.util.Vector;

public class LoadUpdateUserEmails {

    private static final String CLASSNAME = LoadUpdateUserEmails.class.getName();

    public static boolean deleteAllAccessControlRule(Hashtable nv, Hashtable cmd_line, Vector return_objects) {
        boolean isDeleted = true;
        String ignoreUsers = LoadServerHelper.getValue("ignoreUsers", nv, cmd_line, 0);
        if (ignoreUsers == null) {
            return false;
        } else {

        }
        return  isDeleted;
    }

}
