package com.github.rmannibucau.resource.swagger2.provider;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.config.DefaultJaxrsScanner;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.models.Swagger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.beans.Introspector;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;

import static java.util.Optional.ofNullable;

@PreMatching
public class Swagger2ContainerRequestFilter extends ApiListingResource implements ContainerRequestFilter {
    @Context
    private UriInfo ui;

    @Context
    private ServletContext servletContext;

    @Context
    private HttpHeaders httpHeaders;

    @Context
    private Application application;

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        final String path = ui.getPath();
        if (path.endsWith("swagger.json")) {
            requestContext.abortWith(Response.fromResponse(getListingJson(application, null, httpHeaders, ui)).type(MediaType.APPLICATION_JSON_TYPE).build());
        }
    }

    @Override
    protected synchronized Swagger scan(final Application app, final ServletConfig sc) {
        initScanning();
        return super.scan(app, sc);
    }

    private void initScanning() {
        final BeanConfig config = new BeanConfig() {
            @Override
            public Set<Class<?>> classes() { // don't use reflections to scan the classpath since we have an app
                final DefaultJaxrsScanner scanner = new DefaultJaxrsScanner();
                scanner.setPrettyPrint(getPrettyPrint());
                return scanner.classesFromContext(application, null);
            }
        };

        for (final Method method : BeanConfig.class.getMethods()) { // simple generic factory to avoid to handle all attributes
            if (!Modifier.isStatic(method.getModifiers()) && method.getName().startsWith("set") && String.class == method.getParameterTypes()[0]) {
                // try to read from servlet context init paams first otherwise check system property or fallback on default value.
                final String key = "swagger2." + Introspector.decapitalize(method.getName().substring("set".length()));
                final String value = ofNullable(servletContext.getAttribute(key)).map(Object::toString)
                    .orElseGet(() -> System.getProperty(key));
                if (value != null) {
                    try {
                        method.invoke(config, value);
                    } catch (final IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    } catch (final InvocationTargetException e) {
                        throw new IllegalStateException(e.getCause());
                    }
                }
            }
        }
        config.setScan(false); // just to force the info init

        servletContext.setAttribute("reader", config);
    }
}
