package com.company.bankingapp;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@Testcontainers
@ContextConfiguration(initializers = BankingControllerTests.DataSourceInitializer.class)
public class DatabaseTestConfig {

    @Container
    static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withCopyFileToContainer(
                    MountableFile.forClasspathResource("/initial.sql"),
                    "/docker-entrypoint-initdb.d/"
            );

    public static class DataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.test.database.replace=none",// this prevents all extending tests from starting an in-memory database.
                    "spring.datasource.url=" + postgresContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgresContainer.getUsername(),
                    "spring.datasource.password=" + postgresContainer.getPassword()
            );
        }
    }

    @BeforeAll
    static void setUp() {
        postgresContainer.start();
    }
}
