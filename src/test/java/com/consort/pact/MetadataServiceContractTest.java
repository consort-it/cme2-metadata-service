package com.consort.pact;

import com.consort.controller.MetaDataRouteController;
import au.com.dius.pact.provider.junit.PactRunner;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactFolder;
import au.com.dius.pact.provider.junit.target.HttpTarget;
import au.com.dius.pact.provider.junit.Provider;

import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.junit.TargetRequestFilter;
import com.consort.database.ConnectionFactory;
import com.consort.security.AuthorizationFilter;
import org.apache.http.HttpRequest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import spark.Service;

import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.InjectMocks;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static spark.Service.ignite;


@RunWith(PactRunner.class)
@Provider("metadata-service")
@PactFolder("./src/test/resources/pacts")
public class MetadataServiceContractTest {

    //@InjectMocks private static ConnectionFactory connectionFactory;
    //@Mock private Connection connection;

    @BeforeClass
    public static void setupService() throws Exception {
        MetaDataRouteController metaDataRouteController = Mockito.spy(MetaDataRouteController.class);
        if (ConnectionFactory.isConnectionAndTableAvailable()) {
            metaDataRouteController.initRoutes();
        }
    }

    @State("provider has some existing projects")
    public void getProjects() {
        System.out.println("provider has some existing projects");
    }

    @State("provider has an existing project with id ID")
    public void putProject() throws Exception {
        System.out.println("provider has an existing project with id ID");
    }

    @State("provider has no existing project")
    public void getEmptyProjects() {
        System.out.println("provider has no existing project");

        try (Connection connection = ConnectionFactory.getRemoteConnection()) {
            if (connection != null) {
                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM project WHERE id>0")) {
                    statement.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TestTarget // Annotation denotes Target that will be used for tests
    public final Target target = new HttpTarget(8080); // Out-of-the-box implementation of Target (for more information take a look at Test Target section)

    @TargetRequestFilter
    public void exampleRequestFilter(HttpRequest request) {
        request.addHeader("Authorization", "Bearer eyJraWQiOiJJbUptODYyYkxQQ3RsaFFReTM2OEJBb0tQYXlEOURMcE53eXdXMmZoWlBJPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJkMmZkODdkNy03Y2E4LTRhMGYtOTM1MS00YWE2YjAwOTMyN2UiLCJldmVudF9pZCI6IjYxZTI3MjI4LTNmMWEtMTFlOC05OTFiLTFkYjI0ZTQ1NDVjMCIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE1MjM2MjQzMTgsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC5ldS1jZW50cmFsLTEuYW1hem9uYXdzLmNvbVwvZXUtY2VudHJhbC0xX000RnlDMEpQQSIsImV4cCI6MTUyMzYyNzkxOCwiaWF0IjoxNTIzNjI0MzE4LCJqdGkiOiI1OTk5ZjJiMy1lNWQxLTRlMmUtOGVkZS04Zjc3YWJlOTlhZTYiLCJjbGllbnRfaWQiOiI1NjdjZ3RqY2h2aDBhZG01dHYyZzB1NHBrNiIsInVzZXJuYW1lIjoibWhpZW1lciJ9.ek0KkDZSEUB2iIvlDo93gNjAbLYION_pr97toJrbqQP1y-xt5mcCJtEPzrDNTsFRwlsEM1DBKIhxJbFP3k05wjpuZmF6IsokdyOQis0mS9PIov4IjZggDx1VZOBc7qbq4qWI_G8x2sRug4gZX3eu3XwP4ILngJMRNuUqCw1RP89NMeyU1y2aayCcMqocPiywBGCSJX81KXZZCorz9I5Xb59xHhT5N24tDJbJx5WevyNv8KkuftJcKJu4jEYY-MZfHwJwblGKfy0lK1IZY0Y6xhmceyjhu80wMY0XFKQGpi3JxY2WF_ohFFNDDtKxFNMEif5BcumqkBm61UMIHikFnw");
    }

    @AfterClass
    public static void dropTable() {
        System.out.println("Dropping the Table");

        try (Connection connection = ConnectionFactory.getRemoteConnection()) {
            if (connection != null) {
                try (PreparedStatement statement = connection.prepareStatement("DROP TABLE project")) {
                    statement.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
