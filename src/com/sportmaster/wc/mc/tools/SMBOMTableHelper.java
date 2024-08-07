package com.sportmaster.wc.mc.tools;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.util.FormatHelper;

import java.util.Set;

public class SMBOMTableHelper {

    public static int getNextID(Set ids) {
        int nextID = 0;
        for (Object obj : ids) {
            int id = FormatHelper.parseInt((String) obj);
            if (id > nextID) nextID = id;
        }
        return ++nextID;
    }
}
