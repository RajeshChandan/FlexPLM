package com.lowes.web.controller;

import com.google.gson.Gson;
import com.lowes.web.exceptions.InputValidationException;
import com.lowes.web.services.VendorContactsService;
import com.lowes.web.util.AppUtil;
import io.swagger.annotations.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import wt.log4j.LogR;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
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
public class VendorUsersWebServiceController {

    private static final Logger logger = LogR.getLogger(VendorUsersWebServiceController.class.getName());

    public static final JSONParser parser = new JSONParser();
    public static final AppUtil util = new AppUtil();
    public static final Gson gson = new Gson();

    @ApiOperation(
            httpMethod = "POST",
            value = "Add or remove vendor users in Windchill",
            notes = "Add or remove vendor users in Windchill",
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
    @Path("/vendor/vendorContacts")
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public Response updateVendorContacts(@Context HttpHeaders var1, String var2) {
        JSONObject json;

        JSONObject var7;
        try {

            if (var2 == null || "".equalsIgnoreCase(var2)) {
                logger.log(Level.DEBUG, "input data (var2)>>>: {} ", var2);
                throw new InputValidationException("Enter a valid Input in the body");
            }

            JSONObject var4 = (JSONObject) parser.parse(var2);
            json = new VendorContactsService().addUpdateVendorContacts(var4);

        } catch (Exception var11) {
            logger.error(var11.getMessage(), var11);
            var7 = util.getExceptionJson(var11.getMessage());
            return Response.status(400).entity(gson.toJson(var7)).build();
        }

        return Response.status(200).entity(gson.toJson(json)).build();
    }

}
