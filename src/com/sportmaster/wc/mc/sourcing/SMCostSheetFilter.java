package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.util.FormatHelper;

import java.util.*;

public class SMCostSheetFilter {

    public static List<FlexObject> filter(Collection collection, String attName, boolean value) {
        List<FlexObject> result = new ArrayList<>();
        if (collection == null) return result;
        for (Object object : collection) {
            FlexObject row = (FlexObject) object;
            Boolean currentValue = FormatHelper.parseBoolean( row.getData(attName) );
            if (currentValue == value) {
                result.add(row);
            }
        }
        return result;
    }

    public static List<FlexObject> filterInclude(Collection collection, String attName, String value) {
        List<String> values = new ArrayList();
        values.add(value);
        return filter(collection, attName, values, true);
    }

    public static List<FlexObject> filterExclude(Collection collection, String attName, String value) {
        List<String> values = new ArrayList();
        values.add(value);
        return filter(collection, attName, values, false);
    }

    public static List<FlexObject> filterInclude(Collection collection, String attName, List<String> values) {
        return filter(collection, attName, values, true);
    }

    public static List<FlexObject> filterExclude(Collection collection, String attName, List<String> values) {
        return filter(collection, attName, values, false);
    }

    public static List<FlexObject> filter(Collection collection, String attName, List<String> values, boolean include) {
        List<FlexObject> result = new ArrayList<>();
        if (collection == null) return result;
        for (Object object : collection) {
            FlexObject row = (FlexObject) object;
            String currentValue = FormatHelper.format( row.getData(attName) );
            boolean found = false;
            for (String value : values) {
                if ( currentValue.equals( value ) ) {
                    found = true;
                    break;
                }
            }
            if ( found == include )
                result.add(row);
        }
        return result;
    }

    public static Map<String, Double> filter(Map<String, Double> map, List<String> keys, boolean include) {
        Map<String, Double> result = new HashMap<>();
        if (map == null) return result;
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            String currentKey = FormatHelper.format( entry.getKey() );
            boolean found = false;
            for (String key : keys) {
                if ( currentKey.equals( key ) ) {
                    found = true;
                    break;
                }
            }
            if ( found == include )
                result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
