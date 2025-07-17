package com.homihq.db2rest.rest.docsSwagger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.options;

import com.homihq.db2rest.config.Db2RestConfigProperties;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
@Slf4j
public class OpenApiSpecificationWithSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    protected Db2RestConfigProperties db2RestConfigProperties;

    private static final List<String> postgresScripts = List.of("pg/postgres-sakila.sql",
            "pg/postgres-sakila-data.sql", "pg/pg-sakila-functions.sql");

    private static final PostgreSQLContainer testPostgres =
            (PostgreSQLContainer) new PostgreSQLContainer("postgres:15.2-alpine")
                    .withDatabaseName("postgres")
                    .withUsername("postgres")
                    .withPassword("postgres")
                    .withReuse(true);
    
    static {
        testPostgres.start();
        var containerDelegate = new JdbcDatabaseDelegate(testPostgres, "");
        postgresScripts.forEach(initScript -> ScriptUtils.runInitScript(containerDelegate, initScript));
    }

    // private final String testOrigin = "http://example.com";

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("DB_URL", () -> testPostgres.getJdbcUrl());
        registry.add("DB_USER", () -> testPostgres.getUsername());
        registry.add("DB_PASSWORD", () -> testPostgres.getPassword());
        registry.add("ENABLE_AUTH", () -> "true");
        registry.add("AUTH_PROVIDER", () -> "apiKey");
        registry.add("AUTH_DATA_SOURCE" , () -> "classpath:/auth-apiKey-test.yaml");
        registry.add("ENABLE_CORS", () -> "true");

        /*

            - mapping: "/actor/**"
              allowedOrigin: "http://localhost:3000"
              allowedHeader: "*"
              allowedMethod: "*"
            - mapping: "/v1/rdbms/db/**"
              allowedOrigin: "http://localhost:4200, http://localhost:3000"
              allowedHeader: "*"
              allowedMethod: "GET,POST"

         */

        registry.add("cors.mappings[0].mapping", () -> "/actor/**");
        registry.add("cors.mappings[0].allowedOrigins", () -> "http://localhost:3000");
        registry.add("cors.mappings[0].allowedHeaders", () -> "*");
        registry.add("cors.mappings[0].allowedMethods", () -> "*");


        registry.add("cors.mappings[1].mapping", () -> "/v1/rdbms/db/**");
        registry.add("cors.mappings[1].allowedOrigins", () -> "http://localhost:4200, http://localhost:3000");
        registry.add("cors.mappings[1].allowedHeaders", () -> "*");
        registry.add("cors.mappings[1].allowedMethods", () -> "GET,POST");


    }

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void shouldBeSuccessfullForOpeningDocs() throws Exception{
        mockMvc.perform(options("/swagger-ui/index.html")).andExpect(status().isOk());
    }

    @Test
    public void shouldBeSuccessfullForDocsJsonData() throws Exception{
        mockMvc.perform(options("/v3/api-docs")).andExpect(status().isOk());
    } 
}
