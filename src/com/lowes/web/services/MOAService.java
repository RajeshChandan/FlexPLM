package com.lowes.web.services;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.lowes.exceptions.FlexObjectNotFoundException;
import com.lowes.exceptions.InputValidationException;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import wt.fc.Persistable;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.*;

/****
 * Insert, Update, get, Delete MOA Table records.
 *
 * @author Rajesh Chandan Sahu (rajeshchandan.sahu@lowes.com)
 */
public class MOAService {
    private static final Logger logger = LogR.getLogger(MOAService.class.getName());
    public static final String LCS_MOA_OBJECT = "OR:com.lcs.wc.moa.LCSMOAObject:";
    public static final String NOT_FOUND_FOR_OID = "Object not found for OID : {}";
    public static final String NOT_A_VALID_FLEX_OBJECT = " is not a valid flexObject";
    public static final String STATUS_CODE = "StatusCode";
    public static final String STATUS = "Status";

    /**
     * returns MOA Table records bases on object oid and attribute key.
     *
     * @param oid    - VR /OR id for object
     * @param attKey - MOATable attribute Key
     * @return -  json data
     * @throws WTException - WTException
     */
    public JSONObject getRecords(String oid, String attKey) throws WTException, FlexObjectNotFoundException {

        logger.debug("--------MOAService.getRecords() started--------");
        JSONObject json = new JSONObject();
        JSONArray data = new JSONArray();

        FlexTyped flexTyped = findObjectByID(oid);
        if (flexTyped == null) {
            logger.debug(NOT_FOUND_FOR_OID, oid);
            throw new FlexObjectNotFoundException(oid + NOT_A_VALID_FLEX_OBJECT);
        }
        Object attValue = flexTyped.getValue(attKey);

        if (Objects.nonNull(attValue) && attValue instanceof LCSMOATable) {

            LCSMOATable moaTable = (LCSMOATable) attValue;

            data = getMOATableData(moaTable);

        }

        json.put("DATA", data);
        json.put(STATUS_CODE, 200);
        json.put(STATUS, "Success");
        logger.debug("--------MOAService.getRecords() started--------");

        return json;
    }

    /**
     * Insert or Update MOA Table data.
     *
     * @param input -  json Input
     * @return - return status 200 - if data updated/Created , 400 - in case or error
     * @throws WTException - WTException
     */
    public JSONObject update(JSONObject input) throws WTException, InputValidationException, FlexObjectNotFoundException {

        logger.debug("--------MOAService.update() started--------");
        JSONObject response = new JSONObject();

        String oid = input.get("OID").toString();
        String attKey = input.get("attKey").toString();
        JSONArray inputData = (JSONArray) input.get("DATA");
        String search = input.get("search").toString();
        String status = "200";
        String massage = "MOA Records processed Successfully";

        if (!FormatHelper.hasContent(oid) || !FormatHelper.hasContent(attKey) || inputData == null) {
            logger.debug("Input data is not valid");
            throw new InputValidationException("Input Data Not Valid");
        }

        FlexTyped flexTyped = findObjectByID(oid);
        if (flexTyped == null) {
            logger.debug(NOT_FOUND_FOR_OID, oid);
            throw new FlexObjectNotFoundException(oid + NOT_A_VALID_FLEX_OBJECT);
        }

        Map<String, FlexObject> rowData = new HashMap<>();
        FlexType flexType = flexTyped.getFlexType();
        Object attValue = flexTyped.getValue(attKey);
        Collection<?> tableRows = null;
        LCSMOATable moaTable = null;

        if (attValue instanceof LCSMOATable) {

            moaTable = (LCSMOATable) attValue;
            tableRows = moaTable.getRows();

        }

        if (tableRows != null) {

            logger.debug("MOA Table found, total existing rows : {}", tableRows.size());
            createUpdateMOARows(tableRows, rowData, inputData, search);

            try {
                logger.debug("Updating records");
                // UPDATE/CREATE THE TABLE
                LCSMOAObjectLogic moaLogic = new LCSMOAObjectLogic();
                moaLogic.updateMOAObjectCollection((WTObject) flexTyped, flexType.getAttribute(attKey), new Hashtable(rowData));
                logger.debug("Records Updated");
            } catch (Exception e) {
                status = "400";
                massage = "Error Occurred: " + e.getLocalizedMessage();
                logger.error("", e);
            }
        }

        attValue = flexTyped.getValue(attKey);
        if (attValue instanceof LCSMOATable) {
            moaTable = (LCSMOATable) attValue;
        }

        response.put("Data", getMOATableData(moaTable));
        response.put(STATUS_CODE, status);
        response.put(STATUS, massage);
        logger.debug("--------MOAService.update() EXECUTED--------");
        return response;
    }

    public JSONObject delete(JSONObject input) throws InputValidationException, FlexObjectNotFoundException, WTException {

        logger.debug("--------MOAService.delete() started--------");
        String oid = input.get("OID").toString();
        String attKey = input.get("attKey").toString();
        JSONArray inputData = (JSONArray) input.get("DATA");
        String search = input.get("search").toString();

        JSONObject response = new JSONObject();
        LCSMOATable moaTable = null;
        String status = "200";
        String massage = "MOA Records processed Successfully";

        if (!FormatHelper.hasContent(oid) || !FormatHelper.hasContent(attKey) || inputData == null) {
            logger.debug("Input data is not valid");
            throw new InputValidationException("Input Data Not Valid");
        }

        FlexTyped flexTyped = findObjectByID(oid);
        if (flexTyped == null) {
            logger.debug(NOT_FOUND_FOR_OID, oid);
            throw new FlexObjectNotFoundException(oid + NOT_A_VALID_FLEX_OBJECT);
        }
        try {
            Object attValue = flexTyped.getValue(attKey);
            Collection<?> tableRows = null;
            if (attValue instanceof LCSMOATable) {

                moaTable = (LCSMOATable) attValue;
                tableRows = moaTable.getRows();

            }
            if (tableRows != null) {
                logger.debug("MOA Table found, total existing rows :{}", tableRows.size());
                for (Object obj : tableRows) {
                    FlexObject fo = (FlexObject) obj;
                    String id = fo.getString("OID");

                    if (FormatHelper.hasContent(id)) {
                        LCSMOAObject moaObject;
                        moaObject = (LCSMOAObject) LCSQuery.findObjectById(LCS_MOA_OBJECT + fo.getString("OID"));

                        moaObject = getLatestIteration(moaObject);
                        deleteMOARecord(moaObject, inputData, search);
                    }

                }
            }
            attValue = flexTyped.getValue(attKey);
            if (attValue instanceof LCSMOATable) {
                moaTable = (LCSMOATable) attValue;
            }
            response.put("Data", getMOATableData(moaTable));
        } catch (Exception e) {
            status = "400";
            massage = "Error Occurred: " + e.getLocalizedMessage();
            logger.error("", e);
        }


        response.put(STATUS_CODE, status);
        response.put(STATUS, massage);
        logger.debug("--------MOAService.delete() EXECUTED--------");
        return response;
    }

    private void createUpdateMOARows(Collection<?> tableRows, Map<String, FlexObject> rowData, JSONArray inputData, String search) {
        FlexObject row;
        int id;
        int maxId = 0;
        int sortingOrder;
        int maxSortingOrder = 0;
        boolean matchFound;
        Map<String, JSONObject> records = new HashMap<>();

        for (Object tableRow : tableRows) {
            row = (FlexObject) tableRow;

            id = FormatHelper.parseInt((String) row.get("ID"));
            if (id > maxId) maxId = id;
            sortingOrder = FormatHelper.parseInt((String) row.get("SORTINGNUMBER"));
            if (sortingOrder > maxSortingOrder) maxSortingOrder = sortingOrder;

            for (Object inputDatum : inputData) {

                JSONObject data = (JSONObject) inputDatum;
                matchFound = matchMOA(search, data, row);

                if (matchFound) {
                    updateMOARow(data, row);
                    records.put(data.toJSONString(), data);
                }
            }
            rowData.put("" + id, row);
        }

        id = maxId;
        sortingOrder = maxSortingOrder;
        for (Object inputDatum : inputData) {
            JSONObject data = (JSONObject) inputDatum;
            if (!records.containsKey(data.toJSONString())) {

                id++;
                sortingOrder++;

                row = new FlexObject();
                row.put("DROPPED", "false");
                row.put("ID", "" + id);
                row.put("SORTINGNUMBER", "" + sortingOrder);

                updateMOARow(data, row);
                rowData.put("" + id, row);

            }
        }
    }

    private JSONArray getMOATableData(LCSMOATable moaTable) throws WTException {

        JSONArray data = new JSONArray();

        Collection<?> tableRows = null;
        if (Objects.nonNull(moaTable)) {
            tableRows = moaTable.getRows();
        }

        if (tableRows != null) {
            logger.debug("Found MOA data Total record fetched : {}", tableRows.size());
            for (Object obj : tableRows) {
                FlexObject fo = (FlexObject) obj;
                String id = fo.getString("OID");

                if (FormatHelper.hasContent(id)) {
                    JSONObject row = getTableRowData(id);
                    data.add(row);
                }
            }
        }
        return data;
    }

    private JSONObject getTableRowData(String oid) throws WTException {
        LCSMOAObject moaObject;
        moaObject = (LCSMOAObject) LCSQuery.findObjectById(LCS_MOA_OBJECT + oid);
        moaObject = getLatestIteration(moaObject);

        JSONObject data = new JSONObject();

        if (moaObject != null) {
            Collection<FlexTypeAttribute> var6 = moaObject.getFlexType().getAllAttributes();
            for (FlexTypeAttribute attribute : var6) {

                String attKey = attribute.getAttKey();

                if (moaObject.getValue(attKey) == null) {
                    data.put(attKey, "");
                } else {
                    data.put(attKey, String.valueOf(moaObject.getValue(attKey)));
                }

            }
        }
        return data;
    }

    private void deleteMOARecord(LCSMOAObject lcsmoaObject, JSONArray inputData, String searchFields) throws WTException, WTPropertyVetoException {

        boolean matchFound;
        for (Object inputDatum : inputData) {
            JSONObject row = (JSONObject) inputDatum;
            matchFound = matchMOA(searchFields, row, lcsmoaObject);


            if (matchFound) {
                lcsmoaObject.setDropped(true);
                LCSLogic.deleteObject(lcsmoaObject);
                return;
            }
        }
    }

    private boolean matchMOA(String search, JSONObject data, FlexObject row) {
        boolean matchFound = true;
        for (String attKey : FormatHelper.commaSeparatedListToList(search)) {
            String temp1 = String.valueOf(data.get(attKey));
            String temp2 = row.getString(attKey);

            if (!temp1.equalsIgnoreCase(temp2)) {
                return false;
            }
        }

        return matchFound;
    }

    private boolean matchMOA(String search, JSONObject data, LCSMOAObject moaObject) throws WTException {
        boolean matchFound = true;
        for (String attKey : FormatHelper.commaSeparatedListToList(search)) {
            String temp1 = String.valueOf(data.get(attKey));
            String temp2 = String.valueOf(moaObject.getValue(attKey));

            if (!temp1.equalsIgnoreCase(temp2)) {
                return false;
            }
        }

        return matchFound;
    }

    private void updateMOARow(JSONObject data, FlexObject row) {

        for (Object o : data.keySet()) {
            String attributeKey = (String) o;
            row.put(attributeKey, data.get(attributeKey));

        }
    }

    private LCSMOAObject getLatestIteration(LCSMOAObject moaObject) {
        try {
            moaObject = (LCSMOAObject) VersionHelper.latestIterationOf(moaObject);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }

        return moaObject;
    }

    private FlexTyped findObjectByID(String oid) throws FlexObjectNotFoundException {
        FlexTyped flexTyped;
        try {
            flexTyped = (FlexTyped) LCSQuery.findObjectById(oid);

        } catch (WTException e) {
            throw new FlexObjectNotFoundException(oid + NOT_A_VALID_FLEX_OBJECT);
        }
        try {
            flexTyped = (FlexTyped) VersionHelper.latestIterationOf((Persistable) flexTyped);
        } catch (Exception e) {
            logger.debug(e.getLocalizedMessage());
        }
        return flexTyped;
    }
}

