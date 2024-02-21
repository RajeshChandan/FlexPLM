package com.lowes.web.util;

import org.json.simple.JSONObject;
/****
 * Util class for APIs.
 *
 * @author Rajesh Chandan Sahu (rajeshchandan.sahu@lowes.com)
 */
public class AppUtil {

    public JSONObject getExceptionJson(String var1) {
        JSONObject json = new JSONObject();
        json.put("status", "400");
        json.put("message", var1);
        json.put("statusCode", 400);
        return json;
    }

    public static String queryLikeValueFormat(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        // Only add "*" or "%" to begin or end.
        if (value.startsWith("*") || value.startsWith("%")) {
            value = "%" + value.substring(1);
        }
        if (value.endsWith("*") || value.endsWith("%")) {
            value = value.substring(0, value.length() - 1) + "%";
        }

        return value;
    }
}
