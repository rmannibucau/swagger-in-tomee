package com.github.rmannibucau.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("hello")
public class HelloResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hi() {
        return "hi";
    }

    @GET
    @Path("me")
    @Produces(MediaType.TEXT_PLAIN)
    public String hiToMe(@QueryParam("me") final String me) {
        return "hi " + me;
    }
}
