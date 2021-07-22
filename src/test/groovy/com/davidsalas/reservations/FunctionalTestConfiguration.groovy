package com.davidsalas.reservations

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.MSSQLServerContainer
import spock.lang.Specification

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(profiles = "test_functional")
abstract class FunctionalTestConfiguration extends Specification {

    private static sqlServerContainer = new MSSQLServerContainer("mcr.microsoft.com/mssql/server:2019-latest")

    static {
        sqlServerContainer.withInitScript("sql/model.sql")
        sqlServerContainer.acceptLicense()
        sqlServerContainer.start()

        System.setProperty("test.server.sql-host", "${sqlServerContainer.containerIpAddress}")
        System.setProperty("test.server.sql-port", "${sqlServerContainer.firstMappedPort}")
    }
}
