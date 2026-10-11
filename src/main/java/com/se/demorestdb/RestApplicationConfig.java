package com.se.demorestdb;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class RestApplicationConfig extends ResourceConfig {
    public RestApplicationConfig() {
        packages("com.se.demorestdb.resource");
    }
}

