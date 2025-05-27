package com.dbp.legalcheck;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class PostgresTestContainersConfiguration {
    @Bean
    @ServiceConnection
    @SuppressWarnings("resource")
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest")).withDatabaseName("legalcheck")
                .withUsername("user").withPassword("password");
    }
}
