package com.lowes.web.services;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.util.FormatHelper;
import com.lowes.exceptions.InputValidationException;
import com.lowes.model.product.ProductModel;
import com.lowes.web.util.ObjectUtil;
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
     * Function to update library product Object based on input criteria.
     * {
     * "searchCriteria":{
     * "key1",
     * "key2"
     * },
     * "data":[{}]
     * }
     *
     * @param input - in json
     * @return - in json
     * @throws InputValidationException - validation error
     * @throws WTException              - wt exception
     */
    public JSONObject update(JSONObject input) throws InputValidationException, WTException {

        logger.debug("--------ProductService.update() started--------");

        JSONObject productUpdateResponse = new JSONObject();
        JSONArray recordsProcessed = new JSONArray();

        //stop exception for invalid data.
        if (!input.containsKey("data") || !input.containsKey("searchCriteria")) {
            logger.debug("Input Records are inValid");
            throw new InputValidationException("Input Records are inValid");
        }
        List<String> searchFields = getSearchCriteria(input.get("searchCriteria"));
        JSONArray data = (JSONArray) input.get("data");
        List<Map<String, String>> processedJson = convertJson(data);
        List<LCSProduct> productList = new ArrayList<>();
        //stop exception for No valid data.
        if (processedJson.isEmpty() || searchFields.isEmpty()) {
            logger.debug("inValid records received");
            throw new InputValidationException("inValid records received for Update");
        }

        //Query Product data.
        Map<String, LCSProduct> productSearchResults = productModel.findProduct(processedJson, searchFields);
        processedJson.forEach(entry -> {
            JSONObject processingJson = new JSONObject();
            processingJson.putAll(entry);
            LCSProduct product = findProduct(entry, productSearchResults, searchFields);
            if (Objects.nonNull(product)) {
                updateProductDataValues(product, processingJson);
                productList.add(product);
            } else {
                processingJson.put(STATUS, "Record Not Found for input criteria on system");
            }

            recordsProcessed.add(processingJson);
        });

        boolean status = productModel.saveAllProducts(productList);

        productUpdateResponse.put("Status", "Update Records Status :".concat(String.valueOf(status)));
        productUpdateResponse.put("Data", recordsProcessed);
        logger.debug("--------ProductService.update() Executed--------");
        return productUpdateResponse;
    }

    /**
     * Function to delete library product (product which are not linked with any season) Object based on input criteria.
     *
     * @param input - in json
     * @return - in json
     * @throws InputValidationException - validation error
     * @throws WTException              - wt exception
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
            LCSProduct product = findProduct(entry, productMap, null);
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
     * * @param input - in json
     * * @return - in json
     * * @throws InputValidationException - validation error
     * * @throws WTException - wt exception
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
            LCSProduct product = findProduct(entry, productMap, null);
            product = ObjectUtil.getLatestIteration(product);
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

    private LCSProduct findProduct(Map<String, String> criteria, Map<String, LCSProduct> productMap, List<String> searchFields) {
        LCSProduct product = null;
        List<LCSProduct> products = productMap.values().stream().filter(prod -> validateProduct(criteria, prod, searchFields)).collect(Collectors.toList());

        if (!products.isEmpty()) {
            product = products.get(0);
        }
        return product;
    }

    private boolean validateProduct(Map<String, String> criteria, LCSProduct product, List<String> searchFields) {
        String value;
        boolean valid = false;
        List<String> searchEntries = new ArrayList<>(criteria.keySet());
        if (Objects.nonNull(searchFields) && !searchFields.isEmpty()) {
            searchEntries = searchFields;
        }

        for (Map.Entry<String, String> cEntry : criteria.entrySet()) {
            try {
                for (String key : searchEntries) {
                    if (key.equals(cEntry.getKey())) {
                        value = String.valueOf(product.getValue(key));
                        if (value.equalsIgnoreCase(cEntry.getValue())) {
                            valid = true;
                        } else {
                            return false;
                        }
                    }
                }
            } catch (WTException e) {
                valid = false;
            }
        }
        return valid;
    }

    private List<Map<String, String>> convertJson(JSONArray data) {
        List<Map<String, String>> convertedRecords = new ArrayList<>();

        data.iterator().forEachRemaining(obj -> {
            if (obj instanceof JSONObject) {
                Map<String, String> jsonMap = new HashMap<>();
                JSONObject json = (JSONObject) obj;
                json.keySet().iterator().forEachRemaining(key ->
                        jsonMap.put((String) key, String.valueOf(json.get(key)))
                );
                convertedRecords.add(jsonMap);
            }
        });
        return convertedRecords;
    }


    private List<String> getSearchCriteria(Object input) {

        List<String> criteria = new ArrayList<>();
        if (input != null) {
            JSONObject temp = (JSONObject) input;
            criteria.addAll(temp.keySet());
        }

        return criteria;

    }

    private void updateProductDataValues(LCSProduct product, JSONObject data) {

        data.keySet().forEach(key -> {
            if (Objects.nonNull(data.get(key)) && FormatHelper.hasContent(String.valueOf(data.get(key)))) {
                try {
                    product.setValue(String.valueOf(key), data.get(key));
                } catch (Exception e) {
                    logger.error(e);
                }
            }

        });

    }
}
