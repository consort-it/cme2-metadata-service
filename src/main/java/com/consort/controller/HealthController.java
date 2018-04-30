package com.consort.controller;

import com.consort.util.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Service;

import static spark.Service.ignite;

public class HealthController implements RouteController {

    private static final String HEALTH_PATH = "/api/v1/metadata-service/health";
    private static final String METRICS_PATH = "/api/v1/metadata-service/metrics";
    private static final String METRICS_NAME_PATH = "/api/v1/metadata-service/metrics/:name";

    public void initRoutes() {
        final Service http = ignite().port(8081);

        http.get(HEALTH_PATH, (req, res) -> {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(new Status("UP"));
        });

        http.get(METRICS_PATH, (req, res) -> {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(new Status("METRICS UP"));
        });

        http.get(METRICS_NAME_PATH, (req, res) -> {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(new Status(req.params("name")));
        });
    }
}
