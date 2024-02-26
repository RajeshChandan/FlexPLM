package com.lowes.web.services;

import com.google.gwt.dev.util.collect.HashMap;
import com.lcs.wc.product.LCSProduct;
import com.lowes.web.exceptions.FlexObjectNotFoundException;
import com.lowes.web.exceptions.InputValidationException;
import com.lowes.web.model.group.GroupModel;
import com.lowes.web.model.product.ProductModel;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import wt.log4j.LogR;
import wt.util.WTException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductService {

    private static final Logger logger = LogR.getLogger(ProductService.class.getName());
    public static final ProductModel productModel = new ProductModel();

    public JSONObject delete(JSONObject input) throws InputValidationException, FlexObjectNotFoundException, WTException, JSONException {

        logger.debug("--------ProductService.delete() started--------");
        JSONArray data = (JSONArray) input.get("data");
        List<Map<String, String>> convertJson = convertJson(data);

        Map<String, LCSProduct> productMap = productModel.findProduct(convertJson);
        logger.debug("--------ProductService.delete() started--------");
        return null;
    }

    private List<Map<String, String>> convertJson(JSONArray data) throws JSONException {
        List<Map<String, String>> converted = new ArrayList<>();

        data.iterator().forEachRemaining(obj -> {
            if (obj instanceof JSONObject) {
                Map<String, String> jsonMap = new HashMap<>();
                JSONObject json = (JSONObject) obj;
                json.keySet().iterator().forEachRemaining(key -> {
                    jsonMap.put((String) key, (String) json.get(key));
                });
                converted.add(jsonMap);
            }
        });
        return converted;
    }
}
