package com.lowes.web.controller;

import com.google.gson.Gson;
import com.lcs.wc.util.FormatHelper;
import com.lowes.email.Helper.EmailHelper;
import com.lowes.email.config.EmailConfig;
import com.lowes.email.model.EmailModel;
import com.lowes.email.template.EmailTemplateProcessor;
import com.lowes.exceptions.InputValidationException;
import com.lowes.web.util.AppUtil;
import com.ptc.rfa.rest.AbstractRFARestService;
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
        tags = {"Lowes custom Services"}
)
@Path("/lowes")
public class EmailWebServiceController extends AbstractRFARestService {
    private static final Logger logger = LogR.getLogger(EmailWebServiceController.class.getName());
    private static final JSONParser parser = new JSONParser();
    private static final AppUtil util = new AppUtil();
    private static final Gson gson = new Gson();

    @ApiOperation(
            httpMethod = "POST",
            value = "Send Email rest service",
            notes = "Send Email rest service",
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
    @Path("/email/sendEmail")
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public Response sendEmail(@Context HttpHeaders var1, String input) {
        JSONObject json;

        JSONObject var7;
        try {

            if (input == null || "".equalsIgnoreCase(input)) {
                logger.log(Level.DEBUG, "input data >>>: {} ", input);
                throw new InputValidationException("Enter a valid Input in the body");
            }

            JSONObject inputJson = (JSONObject) parser.parse(input);
            String to = inputJson.get("TO").toString();
            String cc = inputJson.get("CC").toString();
            String bcc = inputJson.get("BCC").toString();
            String subject = inputJson.get("SUBJECT").toString();
            String message = inputJson.get("MESSAGE").toString();

            EmailTemplateProcessor emailTemplateProcessor = new EmailTemplateProcessor();
            emailTemplateProcessor.buildTemplate(message);

            EmailModel emailModel = new EmailModel();
            emailModel.setSENDER_EMAIL(EmailConfig.SENDER_EMAIL);
            emailModel.setRECIPIENT_EMAIL(FormatHelper.commaSeparatedListToList(to));
            emailModel.setCC_RECIPIENT_EMAIL(FormatHelper.commaSeparatedListToList(cc));
            emailModel.setBCC_RECIPIENT_EMAIL(FormatHelper.commaSeparatedListToList(bcc));
            emailModel.setEMAIL_SUBJECT(subject);
            emailModel.setEMAIL_CONTENT(emailTemplateProcessor.getTemplateContent());

            boolean status = new EmailHelper().sendMail(emailModel);
            inputJson.put("STATUS", status);
            json = inputJson;

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
