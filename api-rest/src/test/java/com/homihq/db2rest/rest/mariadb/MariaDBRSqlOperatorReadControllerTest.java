package com.homihq.db2rest.rest.mariadb;



import com.homihq.db2rest.MariaDBBaseIntegrationTest;
import io.hosuaby.inject.resources.junit.jupiter.TestWithResources;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(304)
@TestWithResources
class MariaDBRSqlOperatorReadControllerTest extends MariaDBBaseIntegrationTest {

    @Test
    @DisplayName("Test find with Equals Operator")
    void findWithEqualsOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mariadb/film")
                        .accept(APPLICATION_JSON)
                        .param("fields", "title,description,release_year")
                        .param("filter", "film_id==2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1), hasSize(1))))
                .andExpect(jsonPath("$[0].title", notNullValue()))
                .andExpect(jsonPath("$[0].description", notNullValue()))
                .andExpect(jsonPath("$[0].release_year", notNullValue()))
                .andDo(document("mariadb-find-films-with-equals-operator"));

    }

    @Test
    @DisplayName("Test find with Not Equals Operator")
    void findWithNotEqualsOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mariadb/film")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "film_id!=2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(3), hasSize(3))))
                .andExpect(jsonPath("$[0].film_id").value(1))
                .andExpect(jsonPath("$[1].film_id").value(3))
                .andExpect(jsonPath("$[2].film_id").value(4))
                .andDo(document("mariadb-find-films-with-not-equals-operator"));

    }

    @Test
    @DisplayName("Test find with Greater than Operator")
    void findWithGreaterThanOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mariadb/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "film_id=gt=2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(2), hasSize(2))))
                .andExpect(jsonPath("$[0].film_id").value(3))
                .andExpect(jsonPath("$[1].film_id").value(4))
                .andDo(document("mariadb-find-films-with-greater-operator"));

    }

    @Test
    @DisplayName("Test find with Greater than Equals Operator")
    void findWithGreaterThanEqualsOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mariadb/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "film_id=ge=2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(3), hasSize(3))))
                .andExpect(jsonPath("$[0].film_id").value(2))
                .andExpect(jsonPath("$[1].film_id").value(3))
                .andExpect(jsonPath("$[2].film_id").value(4))
                .andDo(document("mariadb-find-films-with-greater-equals-operator"));

    }

    @Test
    @DisplayName("Test find with Less than Operator")
    void findWithLessThanOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mariadb/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "film_id=lt=2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1), hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(1))
                .andDo(document("mariadb-find-films-with-less-operator"));
    }

    @Test
    @DisplayName("Test find with Less than Equals Operator")
    void findWithLessThanEqualsOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mariadb/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "film_id=le=2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(2), hasSize(2))))
                .andExpect(jsonPath("$[0].film_id").value(1))
                .andExpect(jsonPath("$[1].film_id").value(2))
                .andDo(document("mariadb-find-films-with-less-equals-operator"));
    }

    @Test
    @DisplayName("Test find with In Operator")
    void findWithInOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mariadb/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "film_id=in=(2,3,5)")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(2), hasSize(2))))
                .andExpect(jsonPath("$[0].film_id").value(2))
                .andExpect(jsonPath("$[1].film_id").value(3))
                .andDo(document("mariadb-find-films-with-in-operator"));
    }

    @Test
    @DisplayName("Test find with Not In Operator")
    void findWithNotInOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mariadb/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "film_id=out=(2,3,5)")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(2), hasSize(2))))
                .andExpect(jsonPath("$[0].film_id").value(1))
                .andExpect(jsonPath("$[1].film_id").value(4))
                .andDo(document("mariadb-find-films-with-not-in-operator"));
    }

    @Test
    @DisplayName("Test find with Like Operator")
    void findWithLikeOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mariadb/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "title=like=ACADEMY")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1), hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(1))
                .andDo(document("mariadb-find-films-with-like-operator"));
    }

    @Test
    @DisplayName("Test find with Starts With Operator")
    void findWithStartsWithOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mariadb/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "title=startWith=ACAD")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1), hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(1))
                .andDo(document("mariadb-find-films-starts-with-operator"));
    }

    @Test
    @DisplayName("Test find with Ends With Operator")
    void findWithEndsWithOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mariadb/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "title=endWith=DINOSAUR")
                )
                .andExpect(status().isOk())
              
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1), hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(1))
                .andDo(document("mariadb-find-films-ends-with-operator"));
    }


    @Test
    @DisplayName("Should return 4xx when using equals operator with an invalid column")
    void findWithEqualsOperator_givenInvalidColumn_shouldReturn4xx() throws Exception {
        mockMvc.perform(get(VERSION + "/mariadb/film")
                        .accept(APPLICATION_JSON)
                        .param("filter", "actor_id==206")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.detail").value("Failed to parse RQL - Column not found film.actor_id"))
              ;

    }
}
