package com.sportmaster.wc.mc.tools;

import com.lcs.wc.moa.LCSMOATable;
import com.ptc.core.meta.common.FloatingPoint;
import wt.util.WTException;

import java.util.*;

public class SMFormatHelper {

    public static List<String> toList(String values) {
        List<String> result = new ArrayList<>();
        String[] mass = values.replace(";",",").split(",");
        for (String value : mass)
            result.add(value.trim());
        return result;
    }

    public static Map<String,String> toMap(String values) {
        Map<String,String> result = new HashMap<>();
        String[] mass = values.replace(";",",").split(",");
        for (String value : mass) {
            String[] keyValue = value.split(":");
            if (keyValue.length > 1)
                result.put(keyValue[0].trim(), keyValue[1].trim());
            else result.put(keyValue[0].trim(), "");
        }
        return result;
    }

    public static long getLong(Object value) throws WTException {
        if(value == null)
            return 0;
        if(value instanceof Long)
            return (Long) value;
        throw new WTException("SMFormatHelper.getLong(): Type '" + value.getClass().getName() + "' is not supported.");
    }

    public static double getDouble(Object value) throws WTException {
        if(value == null)
            return 0;
        if(value instanceof Double)
            return (Double) value;
        if(value instanceof FloatingPoint)
            return ((FloatingPoint)value).doubleValue();
        throw new WTException("SMFormatHelper.getDouble(): Type '" + value.getClass().getName() + "' is not supported.");
    }

    public static boolean getBoolean(Object value) {
        if (value == null) return false;
        return (Boolean) value;
    }

    public static Collection getLCSMOATableRows(LCSMOATable lcsmoaTable) throws WTException {
        if (lcsmoaTable != null) {
            Collection rows = lcsmoaTable.getRows();
            if (rows != null) {
                return rows;
            }
        }
        return new ArrayList();
    }

    public static boolean isEmpty(String text) {
        if (text == null) return true;

        //32 -> " "
        //160 -> &nbsp;
        String result = text.replace(String.valueOf((char)32),"")
                .replace(String.valueOf((char)160),"");

        return result.trim().isEmpty();
    }

    public static String format(String s) {
        if (s == null) {
            s = "";
        }

        if ("placeholder".equals(s)) {
            s = "<placeholder>";
        }

        if ("material_placeholder".equals(s)) {
            s = "<material_placeholder>";
        }

        if ("null".equals(s)) {
            s = "";
        }

        return s.trim();
    }

    public static boolean isPlaceholder(String s) {
        if (s == null) {
            return false;
        }

        if ("placeholder".equals(s)) {
            return true;
        }

        if ("material_placeholder".equals(s)) {
            return true;
        }

        return false;
    }
}
