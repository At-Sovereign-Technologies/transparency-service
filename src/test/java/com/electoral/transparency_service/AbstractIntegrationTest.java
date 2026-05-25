package com.electoral.transparency_service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assumptions;
import org.testcontainers.DockerClientFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractIntegrationTest {

    @BeforeAll
    public static void assumeDockerAvailable() {
        Assumptions.assumeTrue(DockerClientFactory.instance().isDockerAvailable(), "Docker not available - skipping integration tests");
    }
    @Container
    static PostgreSQLContainer<?> postgresql = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("transparency_test")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresql::getJdbcUrl);
        registry.add("spring.datasource.username", postgresql::getUsername);
        registry.add("spring.datasource.password", postgresql::getPassword);

        // also register legacy env names used across workflows
        registry.add("DB_URL", postgresql::getJdbcUrl);
        registry.add("DB_USER", postgresql::getUsername);
        registry.add("DB_PASSWORD", postgresql::getPassword);
    }

}
