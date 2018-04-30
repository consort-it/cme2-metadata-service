package com.consort.database;

import com.consort.util.EnvironmentContext;
import java.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionFactory {

    final static Logger logger = LoggerFactory.getLogger(ConnectionFactory.class);

    public static Connection getRemoteConnection() throws Exception {

        String jdbcUrl = getConnectionURL();
        Connection con = DriverManager.getConnection(jdbcUrl);
        logger.info("Remote DB connection successfully established.");
        return con;
    }

    private static String getConnectionURL() {

        try {
            Class.forName(EnvironmentContext.getInstance().getenv("dbdriver"));
            String dbName = EnvironmentContext.getInstance().getenv("dbname");
            String userName = EnvironmentContext.getInstance().getenv("user");
            String password = EnvironmentContext.getInstance().getenv("password");
            String hostname = EnvironmentContext.getInstance().getenv("hostname");
            String port = EnvironmentContext.getInstance().getenv("port");
            return "jdbc:postgresql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return "";
    }

    public static boolean isConnectionAndTableAvailable() throws SQLException {

        Connection connection = null;

        try {
            connection = getRemoteConnection();
            if (connection != null) {


                ResultSet rs = null;
                try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM project")) {

                    rs = statement.executeQuery();
                    return true;
                } finally {
                    if (rs != null) {
                        rs.close();
                    }
                }
            }
        } catch (SQLException sqlException) {


            if (sqlException.toString().contains("project")) {

                if (connection != null) {

                    try (PreparedStatement statementCreate = connection.prepareStatement("CREATE TABLE project (id SERIAL PRIMARY KEY, name VARCHAR NOT NULL, description VARCHAR, context VARCHAR, team jsonb, phases jsonb)");){

                        int updateCreate = statementCreate.executeUpdate();

                        try (PreparedStatement statementInsert = connection.prepareStatement("Insert into project (name, description, context, team, phases) VALUES (" + EnvironmentContext.getInstance().getenv("initialData") + ")");){
                            int updateInsert = statementInsert.executeUpdate();

                            if (updateCreate == 0 && updateInsert == 1) {
                                logger.info("New Table created");
                                return true;
                            }

                        } catch (Exception e) {
                            logger.error(e.toString());
                        } finally {
                            connection.close();
                        }

                    } catch (Exception e) {
                        logger.error(e.toString());
                    } finally {
                        connection.close();
                    }
                }
            } else {
                logger.error(sqlException.toString());
            }
        } catch (Exception e) {
            logger.error(e.toString());
        } finally {
            if (connection != null) {
                connection.close();
            }
        }

        return false;
    }
}
