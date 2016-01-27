package com.github.rmannibucau.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

import static java.util.Arrays.asList;

@Path("year")
@Api(value = "/year", tags = "year")
@Produces(MediaType.APPLICATION_JSON)
public class YearResource {
    @GET
    @ApiOperation("Returns the best year ever.")
    public Year best() {
        return new Year(1986);
    }

    @GET
    @Path("all")
    @ApiOperation("Returns all best years ever.")
    public Collection<Year> all() {
        return asList(new Year(1789), new Year(2016));
    }

    public static class Year {
        private final int value;

        public Year(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
