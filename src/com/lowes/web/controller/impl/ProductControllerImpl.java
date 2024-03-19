package com.lowes.web.controller.impl;

import com.google.gson.Gson;
import com.lowes.web.controller.LowesWebServiceController;
import com.lowes.web.exceptions.InputValidationException;
import com.lowes.web.services.ProductService;
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

public class ProductControllerImpl extends LowesWebServiceController {

    private static final Logger logger = LogR.getLogger(ProductControllerImpl.class.getName());
    private static final JSONParser parser = new JSONParser();
    private static final AppUtil util = new AppUtil();
    private static final Gson gson = new Gson();

    @ApiOperation(
            httpMethod = "DELETE",
            value = "Delete records from system bases on input criteria",
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
    @Path("/product/deleteRecords")
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public Response deleteRecords(@Context HttpHeaders var1, String input) {
        JSONObject json;

        JSONObject var7;
        try {

            if (input == null || "".equalsIgnoreCase(input)) {
                logger.log(Level.DEBUG, "input records>>>: {} ", input);
                throw new InputValidationException("Enter a valid Input Record in the body");
            }

            JSONObject inputJson = (JSONObject) parser.parse(input);
            json = new ProductService().delete(inputJson);

        } catch (InputValidationException var9) {
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
            value = "Delete records from system bases on input criteria",
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
    @Path("/product/deleteSeasonRecords")
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public Response deleteSeasonRecords(@Context HttpHeaders var1, String input) {
        JSONObject json;

        JSONObject var7;
        try {

            if (input == null || "".equalsIgnoreCase(input)) {
                logger.log(Level.DEBUG, "input data >>>: {} ", input);
                throw new InputValidationException("Enter a valid Input in the body");
            }

            JSONObject inputJson = (JSONObject) parser.parse(input);
            json = new ProductService().deleteSeasonRecords(inputJson);

        } catch (InputValidationException var9) {
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
            httpMethod = "POST",
            value = "Update records from system bases on input criteria",
            notes = "Returns the results for the execution of a query builder report."
                    + "<br><br>Example"
                    + "<br>Sample payload for passing parameters: "
                    + "<br>{"
                    + "\"searchCriteria\": {"
                    + "\"key1\","
                    + "\"key2\""
                    + "},"
                    + "\"data\": ["
                    + "{"
                    + "\"key1\": \"value1\","
                    + "\"key2\": \"value2\","
                    + "\"key3\": \"value3\""
                    + "}"
                    + "]"
                    + "}",
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
            paramType = "HEADER"
    )})
    @POST
    @Path("/product/updateRecords")
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public Response updateRecords(@Context HttpHeaders var1, String input) {
        JSONObject json;

        JSONObject var7;
        try {

            if (input == null || "".equalsIgnoreCase(input)) {
                logger.log(Level.DEBUG, "input data >>>: {} ", input);
                throw new InputValidationException("Enter a valid Input in the body");
            }

            JSONObject inputJson = (JSONObject) parser.parse(input);
            json = new ProductService().update(inputJson);

        } catch (InputValidationException var9) {
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
