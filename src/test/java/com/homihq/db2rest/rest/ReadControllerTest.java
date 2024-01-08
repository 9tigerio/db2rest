package com.homihq.db2rest.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReadControllerTest {


    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> DATABASE =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withUsername("postgres")
                    .withPassword("postgres")
                    .withInitScript("pg/postgres-sakila-schema.sql") // inside src/test/resources
                    .withDatabaseName("postgres");

    @LocalServerPort
    private Integer port;

    @BeforeAll
    static void beforeAll() {
        DATABASE.start();
    }

    @AfterAll
    static void afterAll() {
        DATABASE.stop();
    }



    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        RestAssured.defaultParser = Parser.JSON;

    }

    @Test
    void shouldSelectFields() {

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .headers("Accept-Profile", "public")
                .when()
                .get("/actor")
                .then()
                .statusCode(200)
                .body(".", hasSize(2));
    }
}
