package com.limited.services;

import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.Query;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.measurements.LCSMeasurements;
import com.lcs.wc.measurements.LCSMeasurementsQuery;
import com.lcs.wc.measurements.LCSPointsOfMeasure;
import com.lcs.wc.measurements.MeasurementValues;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MOAHelper;
import com.lcs.wc.util.VersionHelper;
import com.limited.exporter.helper.VSAttExtractHelper;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import wt.log4j.LogR;
import wt.util.WTException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;

@Path("/PSDService")
public class VSPSDService {
    private static final Logger logger = LogR.getLogger (VSPSDService.class.getName ());
    private static final String DATASOURCE = LCSProperties.get ("com.lcs.wc.db.DBConnectionManager.dataSource");

    @GET
    @Path("/ProductSizes")
    @Produces({"application/json"})
    public Response getProductSizes (@QueryParam("productIds") String var2) {


        List<String> pids = new ArrayList<> ();
        if (var2.contains (",")) {
            String[] data = var2.split (",");
            Collections.addAll (pids, data);
        } else {
            pids.add (var2);
        }
        JSONObject var3 = new JSONObject ();
        try {
            logger.debug ("input ids>>>" + pids.size () + "<<<>>>>" + var2);
            JSONArray var15 = doTheJob (pids);
            var3.put ("Products", var15);
        } catch (Exception e) {
            logger.error ("", e);
            throw new WebApplicationException (Response.status (Response.Status.NOT_FOUND).type ("text/plain").entity ("Porduct size values not found").build ());
        }
        return Response.status (Response.Status.OK).entity (var3).build ();
    }

    JSONArray doTheJob (List<String> pids) throws Exception {

        JSONArray var15 = new JSONArray ();
        StringBuilder header = new StringBuilder ();
        int headCount = 0;
        VSPSDService service = new VSPSDService ();

        Map<String, LCSProduct> prodcuts = service.getProdcut (pids);

        for (String id : pids) {
            logger.debug ("prodcut name>>>>>" + id);
            if (FormatHelper.hasContent (id)) {
                LCSProduct product = prodcuts.get (id);

                logger.debug ("prodcut >>>>>>" + product);

                if (product != null) {
                    Collection results = LCSMeasurementsQuery.findMeasurmentsForProduct (product).getResults ();
                    Iterator i = results.iterator ();
                    FlexObject obj = null;
                    LCSMeasurements measurement = null;
                    Map<Timestamp, LCSMeasurements> measurementsMap = new HashMap<> ();
                    while (i.hasNext ()) {
                        obj = (FlexObject) i.next ();

                        measurement = (LCSMeasurements) LCSQuery
                                .findObjectById ("VR:com.lcs.wc.measurements.LCSMeasurements:"
                                        + (String) obj.get ("LCSMEASUREMENTS.BRANCHIDITERATIONINFO"));

                        measurement = (LCSMeasurements) VersionHelper.latestIterationOf (measurement);
                        measurementsMap.put (measurement.getCreateTimestamp (), measurement);

                    }
                    TreeMap<Timestamp, LCSMeasurements> m1 = new TreeMap (measurementsMap);
                    if (m1.size () > 0) {
                        measurement = m1.lastEntry ().getValue ();
                        if (measurement != null) {
                            JSONObject var3 = new JSONObject ();
                            var3.put ("Product Id", id);
                            var3.put ("Measurement Set Name", measurement.getMeasurementsName ());
                            var3.put ("POM", this.findPOMs (measurement));

                            var15.add (var3);
                        }
                    }
                }
            }
        }
        return var15;
    }

    public JSONArray findPOMs (LCSMeasurements var7) {
        LCSMeasurementsQuery var2 = new LCSMeasurementsQuery ();
        JSONArray var4 = new JSONArray ();

        try {
            Collection var8 = MOAHelper.getMOACollection (var7.getSizeRun ());
            Collection var9 = LCSMeasurementsQuery.findPointsOfMeasure (var7);
            Iterator var10 = var9.iterator ();
            Iterator var11 = var8.iterator ();

            while (var10.hasNext ()) {
                LCSPointsOfMeasure var12 = (LCSPointsOfMeasure) var10.next ();
                String var13 = FormatHelper.getObjectId (var12);
                JSONObject var14 = new JSONObject ();

                String value = String.valueOf (var12.getValue ("measurementName"));
                value = String.valueOf (VSAttExtractHelper.getAttributeValues (var12, null, "measurementName", value));
                if (FormatHelper.hasContent (value)) {
                    var14.put ("Measurement Name", value);
                } else {
                    var14.put ("Measurement Name", "");
                }

                value = String.valueOf (var12.getValue ("number"));
                if (FormatHelper.hasContent (value)) {
                    var14.put ("Section", value);
                } else {
                    var14.put ("Section", "");
                }

                JSONArray var15 = new JSONArray ();
                Hashtable var16 = var12.getMeasurementValues ();

                JSONObject var17;
                for (var11 = var8.iterator (); var11.hasNext (); var15.add (var17)) {
                    var17 = new JSONObject ();
                    String var5 = (String) var11.next ();
                    MeasurementValues var6 = (MeasurementValues) var16.get (var5);
                    if (var6 != null) {
                        var17.put ("Size", var5);
                        var17.put ("Value", new DecimalFormat ("#.####").format (2.54 * var6.getValue ()));
                    } else {
                        var17.put ("Size", var5);
                        var17.put ("Value", 0.0);
                    }
                }

                var14.put ("Sizes", var15);
                var4.add (var14);
            }
        } catch (Exception var18) {
            logger.error ("", var18);
        }

        return var4;
    }

    public Map<String, LCSProduct> getProdcut (List<String> pids) {
        Map<String, LCSProduct> prdts = new HashMap<> ();
        Set<String> oids = new HashSet<> ();
        LCSProduct product = null;

        StringBuilder pIdsFormatted = new StringBuilder ();
        for (String str : pids) {

            pIdsFormatted.append ("'").append (str).append ("'");
            if (!pids.get (pids.size () - 1).equals (str)) {
                pIdsFormatted.append (",");
            }
        }
        logger.debug ("ddd??" + pIdsFormatted.toString ());
        pIdsFormatted.append (")");
        Query query = new Query (DATASOURCE);
        String sqlStmt = "SELECT DISTINCT LCSProduct.branchIditerationInfo FROM LCSProduct WHERE LCSProduct.statecheckoutInfo <> 'wrk' AND LCSProduct.latestiterationInfo = '1' AND LCSProduct.versionIdA2versionInfo = 'A' AND LCSProduct.ptc_str_1typeInfoLCSProduct IN (" + pIdsFormatted.toString ();
        try {

            logger.debug ("sql ss>>>>>>>>>" + sqlStmt);
            query.prepareForQuery ();
            ResultSet rs = query.runQuery (sqlStmt);
            logger.debug ("fetched result size>>>>" + rs.getFetchSize () + "<<>>>" + rs);

            while (rs.next ()) {

                String oid = rs.getString (1);
                oids.add (oid);
            }
        } catch (SQLException var9) {
            logger.error ("", var9);
        } finally {
            try {
                query.cleanUpQuery ();
            } catch (SQLException e) {
                logger.error ("", e);
            }
        }
        logger.debug ("input id size>>>>" + pids.size ());
        logger.debug ("returning oid size>>>>" + oids.size ());
        for (String id : oids) {
            try {
                product = (LCSProduct) LCSQuery.findObjectById ("VR:com.lcs.wc.product.LCSProduct:" + id);
                prdts.put (product.getName (), product);
            } catch (WTException e) {
                logger.error (id + ">>>>??>>>>>" + e.getLocalizedMessage ());
            }

        }
        logger.debug ("input data size>>>>" + pids.size ());
        logger.debug ("returning data size>>>>" + prdts.size ());
        return prdts;
    }

}


