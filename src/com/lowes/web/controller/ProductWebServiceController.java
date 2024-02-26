package com.lowes.web.controller;

import com.google.gson.Gson;
import com.lowes.web.exceptions.FlexObjectNotFoundException;
import com.lowes.web.exceptions.InputValidationException;
import com.lowes.web.services.ProductService;
import com.lowes.web.util.AppUtil;
import io.swagger.annotations.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import wt.log4j.LogR;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@SwaggerDefinition(
        info = @Info(
                description = "This is a sample server",
                version = "1.0.0",
                title = "Swagger Sample Servlet",
                termsOfService = "http://swagger.io/terms/",
                contact = @Contact(
                        name = "Sponge-Bob",
                        email = "apiteam@swagger.io",
                        url = "http://swagger.io"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        consumes = {"application/json", "application/xml"},
        produces = {"application/json", "application/xml"},
        schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS}
)
@Api(
        value = "/lowes",
        tags = {"Lowes custom Services"}
)
@Path("/lowes")
public class ProductWebServiceController {
    private static final Logger logger = LogR.getLogger(ProductWebServiceController.class.getName());
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
                logger.log(Level.DEBUG, "input data (var2)>>>: {} ", input);
                throw new InputValidationException("Enter a valid Input in the body");
            }

            JSONObject inputJson = (JSONObject) parser.parse(input);
            json = new ProductService().delete(inputJson);

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
