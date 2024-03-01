package com.lowes.web.services;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lowes.web.exceptions.InputValidationException;
import com.lowes.web.model.product.ProductModel;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import wt.log4j.LogR;
import wt.util.WTException;

import java.util.*;
import java.util.stream.Collectors;

public class ProductService {

    private static final Logger logger = LogR.getLogger(ProductService.class.getName());
    public static final ProductModel productModel = new ProductModel();
    public static final String STATUS = "status";

    /**
     * Function to delete library product (product which are not linked with any season) Object based on input criteria.
     * @param input - in json
     * @return - in json
     * @throws InputValidationException - validation error
     * @throws WTException - wt exception
     */
    public JSONObject delete(JSONObject input) throws InputValidationException, WTException {

        logger.debug("--------ProductService.delete() started--------");

        JSONObject response = new JSONObject();
        JSONArray dataProcessed = new JSONArray();

        //stop exception for invalid data.
        if (!input.containsKey("data")) {
            logger.debug("Input data is not valid");
            throw new InputValidationException("Input Data Not Valid");
        }

        JSONArray data = (JSONArray) input.get("data");
        List<Map<String, String>> convertJson = convertJson(data);
        //stop exception for No valid data.
        if (convertJson.isEmpty()) {
            logger.debug("No valid data received");
            throw new InputValidationException("No Valid data received for delete");
        }

        //Query Product data.
        Map<String, LCSProduct> productMap = productModel.findProduct(convertJson);

        //Process db Query result.
        convertJson.forEach(entry -> {
            JSONObject processJson = new JSONObject();
            processJson.putAll(entry);
            LCSProduct product = findProduct(entry, productMap);
            try {
                if (Objects.nonNull(product)) {
                    productModel.deleteProduct(product);
                    processJson.put(STATUS, "Deleted Successfully");
                } else {
                    processJson.put(STATUS, "Product Not Found for input criteria");
                }
            } catch (WTException e) {
                logger.error(e);
                processJson.put(STATUS, "ERROR Occurred during deletion :- ".concat(e.getMessage()));
            }
            dataProcessed.add(processJson);
        });
        response.put("Data", dataProcessed);
        logger.debug("--------ProductService.delete() Executed--------");
        return response;
    }

    /**
     * Function to delete Season product (product which are linked with any season) Object based on input criteria.
     *      * @param input - in json
     *      * @return - in json
     *      * @throws InputValidationException - validation error
     *      * @throws WTException - wt exception
     */
    public JSONObject deleteSeasonRecords(JSONObject input) throws InputValidationException, WTException {

        logger.debug("--------ProductService.deleteSeasonRecords() started--------");

        JSONObject response = new JSONObject();
        JSONArray dataProcessed = new JSONArray();

        if (!input.containsKey("data")) {
            logger.debug("Input data is not valid");
            throw new InputValidationException("Input Data Not Valid");
        }

        JSONArray data = (JSONArray) input.get("data");
        List<Map<String, String>> convertJson = convertJson(data);
        if (convertJson.isEmpty()) {
            logger.debug("No valid data received");
            throw new InputValidationException("No Valid data received for delete");
        }

        Map<String, LCSProduct> productMap = productModel.findProduct(convertJson);

        convertJson.forEach(entry -> {
            JSONObject processJson = new JSONObject();
            processJson.putAll(entry);
            LCSProduct product = findProduct(entry, productMap);
            product = productModel.getLatestIteration(product);
            try {
                if (Objects.nonNull(product)) {
                    List<LCSSeason> seasons = productModel.findSeasons(product);
                    logger.log(Level.DEBUG, " Find seasons for product {}", seasons.size());
                    productModel.removeProductFromSeason(product, seasons);
                    logger.log(Level.DEBUG, " remove Product FromSeason Completed");
                    productModel.deleteProduct(product);
                    processJson.put(STATUS, "Deleted Successfully");
                } else {
                    processJson.put(STATUS, "Product Not Found for input criteria");
                }
            } catch (WTException e) {
                logger.error(e);
                processJson.put(STATUS, "ERROR Occurred during deletion :- ".concat(e.getMessage()));
            }
            dataProcessed.add(processJson);
        });
        response.put("Data", dataProcessed);
        logger.debug("--------ProductService.deleteSeasonRecords() Executed--------");
        return response;
    }

    private LCSProduct findProduct(Map<String, String> criteria, Map<String, LCSProduct> productMap) {
        LCSProduct product = null;
        List<LCSProduct> products = productMap.values().stream().filter(prod -> validateProduct(criteria, prod)).collect(Collectors.toList());

        if (!products.isEmpty()) {
            product = products.get(0);
        }
        return product;
    }

    private boolean validateProduct(Map<String, String> criteria, LCSProduct product) {
        String value;
        boolean valid = false;
        for (Map.Entry<String, String> cEntry : criteria.entrySet()) {
            try {
                value = String.valueOf(product.getValue(cEntry.getKey()));
                if (value.equalsIgnoreCase(cEntry.getValue())) {
                    valid = true;
                } else {
                    return false;
                }
            } catch (WTException e) {
                valid = false;
            }
        }
        return valid;
    }

    private List<Map<String, String>> convertJson(JSONArray data) {
        List<Map<String, String>> converted = new ArrayList<>();

        data.iterator().forEachRemaining(obj -> {
            if (obj instanceof JSONObject) {
                Map<String, String> jsonMap = new HashMap<>();
                JSONObject json = (JSONObject) obj;
                json.keySet().iterator().forEachRemaining(key ->
                        jsonMap.put((String) key, String.valueOf(json.get(key)))
                );
                converted.add(jsonMap);
            }
        });
        return converted;
    }
}
