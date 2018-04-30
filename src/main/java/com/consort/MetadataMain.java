package com.consort;

import com.consort.controller.HealthController;
import com.consort.controller.MetaDataRouteController;
import com.consort.controller.RouteController;
import com.consort.database.ConnectionFactory;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class MetadataMain {

    private static Set<RouteController> routeControllers = new HashSet<>();

    public static void main(String[] args) throws SQLException {
            registerRouteControllers();

            ConnectionFactory cf = new ConnectionFactory();

            if (ConnectionFactory.isConnectionAndTableAvailable()) {
                initRoutes();
            }
    }

    private static void registerRouteControllers() {
        routeControllers.add(new HealthController());
        routeControllers.add(new MetaDataRouteController());
    }

    private static void initRoutes() {
        for(final RouteController routeController : routeControllers) {
            routeController.initRoutes();
        }
    }
}
