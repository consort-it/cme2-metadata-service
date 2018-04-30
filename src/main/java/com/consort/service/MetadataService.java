package com.consort.service;

import com.consort.controller.MetaDataRouteController;
import com.consort.database.ConnectionFactory;
import com.consort.database.entities.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.consort.util.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Response;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MetadataService {

    private static MetadataService instance = null;
    final static Logger logger = LoggerFactory.getLogger(MetaDataRouteController.class);

    private MetadataService() {
    }

    public static MetadataService getInstance() {
        if (instance == null) {
            instance = new MetadataService();
        }

        return instance;
    }

    Connection getDBConnection() throws Exception {
        return ConnectionFactory.getRemoteConnection();
    }

    public List<Project> getAllProjects() throws SQLException, JsonProcessingException {

        String sqlQuery = "SELECT * FROM project";
        final ObjectMapper mapper = new ObjectMapper();

        try (Connection connection = getDBConnection()) {

            if (connection != null) {

                List<Project> projectList = new ArrayList<>();

                try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

                    try (ResultSet resultSet = statement.executeQuery()) {

                        while (resultSet.next()) {

                            Project project = new Project();
                            project.setId(resultSet.getString("id"));
                            project.setName(resultSet.getString("name"));
                            project.setDescription(resultSet.getString("description"));
                            project.setContext(resultSet.getString("context"));
                            project.setTeam(mapper.readValue(resultSet.getString("team"), new TypeReference<List<Person>>() {
                            }));
                            project.setPhases(mapper.readValue(resultSet.getString("phases"), new TypeReference<List<Phase>>() {
                            }));
                            projectList.add(project);
                        }
                    }

                } catch (Exception e) {
                    logger.error(e.toString());
                    return null;
                } finally {
                    connection.close();
                }

                return projectList;
            }

        } catch (Exception e) {
            logger.error(e.toString());
        }

        return null;
    }

    public String getProjectById(final int id, Response res) throws SQLException, JsonProcessingException {

        String sqlQuery = "SELECT * FROM project WHERE id = ?";
        final ObjectMapper mapper = new ObjectMapper();

        // p.a. doing a more advanced query https://stackoverflow.com/questions/22736742/query-for-array-elements-inside-json-type
        // SELECT * FROM appdata a, jsonb_array_elements(a.data->'contentDataList') obj WHERE obj->>'isDeleted' = '0';

        try (Connection connection = getDBConnection()) {

            try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

                statement.setInt(1, id);

                if (connection != null) {

                    Project project = new Project();

                    try (ResultSet resultSet = statement.executeQuery()) {

                        if (!resultSet.isBeforeFirst()) {
                            res.status(404);
                            return mapper.writeValueAsString(new ErrorMessage("MDS-1234", "The Project with the given id does not exist."));
                        }

                        while (resultSet.next()) {

                            project.setId(resultSet.getString("id"));
                            project.setName(resultSet.getString("name"));
                            project.setDescription(resultSet.getString("description"));
                            project.setContext(resultSet.getString("context"));
                            project.setTeam(mapper.readValue(resultSet.getString("team"), new TypeReference<List<Person>>() {
                            }));
                            project.setPhases(mapper.readValue(resultSet.getString("phases"), new TypeReference<List<Phase>>() {
                            }));
                        }
                    }

                    return mapper.writeValueAsString(project);

                }

                } catch (Exception e) {
                    logger.error(e.toString());
                    return null;
                }

            } catch (Exception e) {
                logger.error(e.toString());
                return mapper.writeValueAsString(new ErrorMessage("MDS-1234", "General Error"));
            }

            return mapper.writeValueAsString(new ErrorMessage("MDS-1234", "General Error"));
        }

        public String insertNewProject(NewProject newProject) throws JsonProcessingException, SQLException {

        String sqlQuery = "INSERT into project (name, description, context, team, phases) VALUES (?, ?, ?, ?::JSON, ?::JSON)";
        final ObjectMapper mapper = new ObjectMapper();

        try (Connection connection = getDBConnection()) {

            if (connection != null) {

                try (PreparedStatement statement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {

                    statement.setString(1, newProject.getName());
                    statement.setString(2, (newProject.getDescription() == null) ? "" : newProject.getDescription());
                    statement.setString(3, "");
                    statement.setString(4, "[]");
                    statement.setString(5, "[]");

                    statement.executeUpdate();
                    String id;

                    try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            id = generatedKeys.getString(1);
                        } else {
                            throw new SQLException("Creating new Project failed, no ID obtained.");
                        }
                    }

                    Project newCreatedProject = new Project();
                    newCreatedProject.setId(id);
                    newCreatedProject.setName(newProject.getName());
                    newCreatedProject.setDescription((newProject.getDescription() == null) ? "" : newProject.getDescription());
                    newCreatedProject.setContext("");
                    newCreatedProject.setTeam(new ArrayList<>());
                    newCreatedProject.setPhases(new ArrayList<>());

                    return mapper.writeValueAsString(newCreatedProject);

                } catch (Exception e) {
                    logger.error(e.toString());
                    return mapper.writeValueAsString(new ErrorMessage("MDS-1234", "Failed to insert new Project"));

                }

            }

        } catch (Exception e) {
            logger.error(e.toString());
            return mapper.writeValueAsString(new ErrorMessage("MDS-1234", "General Error"));
        }

        return mapper.writeValueAsString(new ErrorMessage("MDS-1234", "General Error"));
    }

    public String updateProject(int id, Project project) throws JsonProcessingException, SQLException {

        String sqlQuery = "UPDATE project SET name = ?, description = ?, context = ?, team = ?::JSON, phases = ?::JSON WHERE id = ?";
        final ObjectMapper mapper = new ObjectMapper();

        try (Connection connection = getDBConnection()) {

            if (connection != null) {

                try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

                    statement.setString(1, project.getName());
                    statement.setString(2, (project.getDescription() == null) ? "" : project.getDescription());
                    statement.setString(3, (project.getContext() == null) ? "" : project.getContext());
                    statement.setString(4, (project.getTeam() == null) ? "[]" : mapper.writeValueAsString(project.getTeam()));
                    statement.setString(5, (project.getPhases() == null) ? "[]" : mapper.writeValueAsString(project.getPhases()));
                    statement.setInt(6, id);

                    statement.executeUpdate();

                    return mapper.writeValueAsString(project);

                } catch (Exception e) {
                    logger.error(e.toString());
                    return mapper.writeValueAsString(new ErrorMessage("MDS-1234", "Failure while updating project"));
                }
            }

        } catch (Exception e) {
            logger.error(e.toString());
            return mapper.writeValueAsString(new ErrorMessage("MDS-1234", "General Error"));
        }
        return mapper.writeValueAsString(new ErrorMessage("MDS-1234", "General Error"));
    }
}
