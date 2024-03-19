package com.lowes.web.controller;

import com.ptc.core.appsec.CSRFProtector;
import com.ptc.core.common.model.Item;
import com.ptc.core.common.model.ItemList;
import com.ptc.core.rest.AbstractResource;
import io.swagger.annotations.*;
import wt.servlet.ServletState;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
public class LowesWebServiceController extends AbstractResource {

    @GET
    @Path("csrf")
    @ApiOperation(response = ItemList.class, value = "Get a CSRF nonce key and value.", notes = "Use this nonce in calls to non-GET REST endpoints in the same session. Use the key as an HTTP header and the value as the header value.")
    public Response getNonce() {
        ItemList localItemList = new ItemList();

        Item localItem = new Item();
        localItem.setId("csrf");
        localItem.addAttribute("nonce_key", "CSRF_NONCE");
        localItem.addAttribute("nonce", CSRFProtector.getNonce((HttpServletRequest) ServletState.getServletRequest()));

        localItemList.add(localItem);

        return buildResponse(localItemList).build();
    }
}
