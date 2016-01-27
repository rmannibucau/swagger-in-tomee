package com.github.rmannibucau.resource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

@ApplicationPath("api")
public class MyApplication extends Application {
    private Set<Class<?>> classes;

    @Override
    public Set<Class<?>> getClasses() { // explicitely listing classes we exclude io.swagger.jaxrs.listing.ApiListingResource
        if (classes == null) {
            classes = new HashSet<>(asList(HelloResource.class, YearResource.class));
        }
        return classes;
    }
}
