package com.sportmaster.wc.mc.tools;

import com.lcs.wc.util.VersionHelper;
import wt.fc.WTObject;
import wt.util.WTException;
import wt.vc.Mastered;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import java.util.Collection;

public class SMVersionHelper {

    public static <T extends WTObject> T  latestIterationOf(Mastered master) throws WTException {
        // Finds all of the versions to the very first one created associated with the given master.
        // The result is an ordered list of versions (i.e., latest iterations) from the most recent one to the first one created.
        Collection collection = VersionHelper.allVersionsOf(master);
        for (Object object : collection) {
            if ( ! WorkInProgressHelper.isWorkingCopy((Workable)object)) {
                return (T) object;
            }
        }

        return null;
    }
}
