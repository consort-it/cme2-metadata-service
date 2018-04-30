package com.consort.controller;

import com.consort.database.entities.NewProject;
import com.consort.database.entities.Project;
import com.consort.security.AuthorizationFilter;
import com.consort.service.MetadataService;
import com.consort.util.ErrorMessage;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import static spark.Service.ignite;

public class MetaDataRouteController implements RouteController {

    private static final String METADATA_GET_ALL_PROJECTS_PATH = "/api/v1/metadata-service/projects";
    private static final String METADATA_GET_PROJECT_PATH = "/api/v1/metadata-service/projects/:id";
    private static final String METADATA_POST_PROJECT_PATH = "/api/v1/metadata-service/projects";
    private static final String METADATA_PUT_PROJECT_PATH = "/api/v1/metadata-service/projects/:id";
    private static final String METADATA_AUTHORIZATION_PATH = "/api/v1/metadata-service/*";

    private static final String AUTHORIZER_NAME = "scope";
    private static final String ROLE_ADMIN = "aws.cognito.signin.user.admin";

    private static final String RESPONSE_TYPE = "application/json";

    final static Logger logger = LoggerFactory.getLogger(MetaDataRouteController.class);

    public Service http = ignite().port(8080);

    public void initRoutes() {
        enableCORS(http, "*", "GET, POST, PUT, OPTIONS", "Content-Type, Authorization");

        logger.info("Routes have been initialized");
        final ObjectMapper mapper = new ObjectMapper();

        http.notFound((req, res) -> {
            res.type(RESPONSE_TYPE);
            return mapper.writeValueAsString(new ErrorMessage("MDS-1234", "404 URL not found"));
        });

        http.internalServerError((req, res) -> {
            res.type(RESPONSE_TYPE);
            return mapper.writeValueAsString(new ErrorMessage("MDS-1234", "500 Internal Server Error"));
        });

        http.before(METADATA_AUTHORIZATION_PATH, new AuthorizationFilter(AUTHORIZER_NAME, ROLE_ADMIN));

        http.get(METADATA_GET_ALL_PROJECTS_PATH, (req, res) -> {

        logger.info("The route /projects to get all projetcs has been requested");

        try {

            res.type(RESPONSE_TYPE);

            return mapper.writeValueAsString(MetadataService.getInstance().getAllProjects());

        } catch (Exception exception) {
            res.status(400);
            return mapper.writeValueAsString(new ErrorMessage("MDS-1234", "Something was wrong with the client request"));
        }
        });


        http.get(METADATA_GET_PROJECT_PATH, (req, res) -> {

            final int id = Integer.parseInt(req.params("id"));

            logger.info("The route /projects/" + id + " to get the specific project has been requested");

            try {

                res.type(RESPONSE_TYPE);

                return MetadataService.getInstance().getProjectById(id, res);

            } catch (Exception exception) {
                res.status(400);
                return mapper.writeValueAsString(new ErrorMessage("MDS-1234", "Something was wrong with the client request"));
            }

        });


        http.post(METADATA_POST_PROJECT_PATH, (req, res) -> {

            logger.info("Request to add new project " + req.body() + " has been made");

            try {

                res.type(RESPONSE_TYPE);
                NewProject newProject = new ObjectMapper().readValue(req.body(), NewProject.class);
                res.status(201);
                return MetadataService.getInstance().insertNewProject(newProject);

            } catch (JsonMappingException mappingException) {

                res.status(400);
                return mapper.writeValueAsString(new ErrorMessage("MDS-1234", "Something was wrong with the client request"));

            }

        });


        http.put(METADATA_PUT_PROJECT_PATH, (req, res) -> {

            final int id = Integer.parseInt(req.params("id"));

            logger.info("Request to update project with the following data " + req.body() + " has been made");

            try {

                res.type(RESPONSE_TYPE);
                Project project = new ObjectMapper().readValue(req.body(), Project.class);
                return MetadataService.getInstance().updateProject(id, project);

            } catch (JsonMappingException mappingException) {

                res.status(400);
                return mapper.writeValueAsString(new ErrorMessage("MDS-1234", "Something was wrong with the client request"));

            }
        });
    }

    private static void enableCORS(final Service http, final String origin, final String methods, final String headers) {

        http.options("/*", (req, res) -> {

            final String acRequestHeaders = req.headers("Access-Control-Request-Headers");
            if (acRequestHeaders != null) {
                res.header("Access-Control-Allow-Headers", acRequestHeaders);
            }

            final String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        http.before((req, res) -> {
            res.header("Access-Control-Allow-Origin", origin);
            res.header("Access-Control-Request-Method", methods);
            res.header("Access-Control-Allow-Headers", headers);
            res.header("Server", "-");
        });
    }
}
