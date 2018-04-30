package com.consort.service

import com.consort.database.entities.Project
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import org.junit.Test;

class MetadataServiceTest extends Specification {

    MetadataService underTest

    @Test
    def "unit test of getAllProjectsmethod"() {

        given:
        underTest = Spy(MetadataService, useObjenesis: true)
        Connection connection = Mock()
        PreparedStatement preparedStatement = Mock()
        ResultSet resultSet = Mock()
        ObjectMapper mapper = Mock()

        when:
        List<Project> projects = underTest.getAllProjects()

        then:
        1 * underTest.getDBConnection() >> { connection }
        1 * connection.prepareStatement(_ as String) >> { preparedStatement }
        //preparedStatement.setString(_ as int, _ as String) >> {}
        1 * preparedStatement.executeQuery() >> { resultSet }
        2 * resultSet.next() >>> [true, false]
        1 * resultSet.getString("team") >> { '[{"name":"Nico König","roles": ["Frontend"],"email":"nico.konig@consort-group.com"}]' }
        1 * resultSet.getString("phases") >> { '[{"name" : "1", "services": [{"name" : "service", "description" : "description", "icon": "icon.jpg"}]}]' }

        then:
        assert projects != null
    }
}
