package com.lowes.web.controller;

import com.google.gson.Gson;
import com.lowes.web.exceptions.FlexObjectNotFoundException;
import com.lowes.web.exceptions.InputValidationException;
import com.lowes.web.services.MOAService;
import com.lowes.web.util.AppUtil;
import io.swagger.annotations.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import wt.log4j.LogR;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;


public class MOATableWebServiceController {
    private static final Logger logger = LogR.getLogger(MOATableWebServiceController.class.getName());
    private static final JSONParser parser = new JSONParser();
    private static final AppUtil util = new AppUtil();
    private static final Gson gson = new Gson();

    @ApiOperation(
            httpMethod = "GET",
            value = "GET MOA Table details bases on OID and attribute key key",
            response = JSONObject.class
    )
    @ApiResponses({@ApiResponse(
            code = 200,
            message = "Success",
            response = JSONObject.class
    ), @ApiResponse(
            code = 404,
            message = "Invalid uri"
    ), @ApiResponse(
            code = 500,
            message = "Unexpected error"
    )})
    @GET
    @Path("/moa/getRecordData")
    @Produces({"application/json"})
    public Response getMOADetails(@QueryParam("oid") @ApiParam(name = "oid", value = "FlexPLM Object id", required = true) String oid, @QueryParam("tableKey") @ApiParam(name = "tableKey", value = "FlexPLM MOA Table attribute Key", required = true) String tableKey) {
        JSONObject json;
        JSONObject var7;

        try {

            if (oid == null || "".equalsIgnoreCase(oid) || tableKey == null || "".equalsIgnoreCase(tableKey)) {
                logger.log(Level.DEBUG, "input data (oid, tableKey)>>>: {}, {}", oid, tableKey);
                throw new InputValidationException("Enter a valid Input");
            }

            json = new MOAService().getRecords(oid, tableKey);

        } catch (InputValidationException var9) {
            var7 = util.getExceptionJson(var9.getMessage());
            return Response.status(400).entity(gson.toJson(var7)).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            var7 = MOATableWebServiceController.util.getExceptionJson(e.getMessage());
            return Response.status(400).entity(gson.toJson(var7)).build();

        }
        return Response.status(200).entity(gson.toJson(json)).build();
    }


    @ApiOperation(
            httpMethod = "POST",
            value = "Update or Insert records into MOA Table details bases on search fields",
            response = JSONObject.class
    )
    @ApiResponses({@ApiResponse(
            code = 200,
            message = "Success",
            response = JSONObject.class
    ), @ApiResponse(
            code = 404,
            message = "Invalid uri"
    ), @ApiResponse(
            code = 500,
            message = "Unexpected error"
    )})
    @ApiImplicitParams({@ApiImplicitParam(
            name = "CSRF_NONCE",
            value = "The CSRF nonce as returned from the /security/csrf endpoint.  See the Swagger documentation titled CSRF Protection for more information.",
            required = true,
            dataType = "string",
            paramType = "header"
    )})
    @POST
    @Path("/moa/updateRecords")
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public Response updateRecords(@Context HttpHeaders var1, String input) {
        JSONObject json;

        JSONObject var7;
        try {

            if (input == null || "".equalsIgnoreCase(input)) {
                logger.log(Level.DEBUG, "input data (var2)>>>: {} ", input);
                throw new InputValidationException("Enter a valid Input in the body");
            }

            JSONObject inputJson = (JSONObject) parser.parse(input);
            json = new MOAService().update(inputJson);

        } catch (InputValidationException | FlexObjectNotFoundException var9) {
            var7 = util.getExceptionJson(var9.getMessage());
            return Response.status(400).entity(gson.toJson(var7)).build();
        } catch (Exception var11) {
            logger.error(var11.getMessage(), var11);
            var7 = util.getExceptionJson(var11.getMessage());
            return Response.status(400).entity(gson.toJson(var7)).build();
        }

        return Response.status(200).entity(gson.toJson(json)).build();
    }

    @ApiOperation(
            httpMethod = "DELETE",
            value = "Update records into MOA Table details bases on Search fields",
            response = JSONObject.class
    )
    @ApiResponses({@ApiResponse(
            code = 200,
            message = "Success",
            response = JSONObject.class
    ), @ApiResponse(
            code = 404,
            message = "Invalid uri"
    ), @ApiResponse(
            code = 500,
            message = "Unexpected error"
    )})
    @ApiImplicitParams({@ApiImplicitParam(
            name = "CSRF_NONCE",
            value = "The CSRF nonce as returned from the /security/csrf endpoint.  See the Swagger documentation titled CSRF Protection for more information.",
            required = true,
            dataType = "string",
            paramType = "header"
    )})
    @DELETE
    @Path("/moa/deleteRecords")
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public Response deleteRecords(@Context HttpHeaders var1, String input) {
        JSONObject json;

        JSONObject var7;
        try {

            if (input == null || "".equalsIgnoreCase(input)) {
                logger.log(Level.DEBUG, "input data (var2)>>>: {} ", input);
                throw new InputValidationException("Enter a valid Input in the body");
            }

            JSONObject inputJson = (JSONObject) parser.parse(input);
            json = new MOAService().delete(inputJson);

        } catch (InputValidationException | FlexObjectNotFoundException var9) {
            var7 = util.getExceptionJson(var9.getMessage());
            return Response.status(400).entity(gson.toJson(var7)).build();
        } catch (Exception var11) {
            logger.error(var11.getMessage(), var11);
            var7 = util.getExceptionJson(var11.getMessage());
            return Response.status(400).entity(gson.toJson(var7)).build();
        }

        return Response.status(200).entity(gson.toJson(json)).build();
    }
}
