package com.lowes.web.controller;

import com.google.gson.Gson;
import com.lowes.exceptions.InputValidationException;
import com.lowes.notification.util.TriggerConfigUtil;
import com.lowes.web.util.AppUtil;
import com.ptc.rfa.rest.AbstractRFARestService;
import io.swagger.annotations.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
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
                termsOfService = "https://swagger.io/terms/",
                contact = @Contact(
                        name = "Sponge-Bob",
                        email = "apiteam@swagger.io",
                        url = "https://swagger.io"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0.html"
                )
        ),
        consumes = {"application/json", "application/xml"},
        produces = {"application/json", "application/xml"},
        schemes = {SwaggerDefinition.Scheme.HTTP, SwaggerDefinition.Scheme.HTTPS}
)
@Api(
        value = "/lowes",
        tags = {"Lowes Services"}
)
@Path("/lowes")
public class WTGroupWebServiceController extends AbstractRFARestService {

    private static final Logger logger = LogR.getLogger(WTGroupWebServiceController.class.getName());
    private static final AppUtil util = new AppUtil();
    private static final Gson gson = new Gson();


    @ApiOperation(
            httpMethod = "POST",
            value = "Resource to save trigger configuration which is used as input for triggers",
            notes = "Only used by tech Team",
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
    @Path("/notification/triggerConfig")
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public Response triggerConfig(@Context HttpHeaders var1, String input) {
        JSONObject json;
        JSONObject var7;
        try {

            if (input == null || "".equalsIgnoreCase(input)) {
                logger.log(Level.DEBUG, "input data >>>: {} ", input);
                throw new InputValidationException("Enter a valid Input in the body");
            }
            json = new JSONObject();
            boolean status = new TriggerConfigUtil().updateConfig(input);
            if (status) {
                json.put("message", "Configuration successfully updated");
            }
            if (!status) {
                json.put("message", "Configuration not updated");
            }
            json.put("STATUS", status);

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
