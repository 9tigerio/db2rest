package com.homihq.db2rest.rest.mssql;


import io.hosuaby.inject.resources.junit.jupiter.TestWithResources;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.http.MediaType;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(504)
@TestWithResources
class MsSQLRSqlOperatorReadControllerTest extends MsSQLBaseIntegrationTest {

    @Test
    @DisplayName("Test find with Equals Operator")
    void findWithEqualsOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film")
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
                .andDo(document("mssql-find-films-with-equals-operator"));

    }

    @Test
    @DisplayName("Test find with Equals Operator")
    void findWithNotEqualsOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film")
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
                .andDo(document("mssql-find-films-with-not-equals-operator"));

    }

    @Test
    @DisplayName("Test find with Greater than Operator")
    void findWithGreaterThanOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "film_id=gt=2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(2), hasSize(2))))
                .andExpect(jsonPath("$[0].film_id").value(3))
                .andExpect(jsonPath("$[1].film_id").value(4))
                .andDo(document("mssql-find-films-with-greater-operator"));

    }

    @Test
    @DisplayName("Test find with Greater than EqualsOperator")
    void findWithGreaterThanEqualsOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film;desc")
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
                .andDo(document("mssql-find-films-with-greater-equals-operator"));

    }

    @Test
    @DisplayName("Test find with Less than Operator")
    void findWithLessThanOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "film_id=lt=2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1), hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(1))
                .andDo(document("mssql-find-films-with-less-operator"));
    }

    @Test
    @DisplayName("Test find with Less than Equals Operator")
    void findWithLessThanEqualsOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "film_id=le=2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(2), hasSize(2))))
                .andExpect(jsonPath("$[0].film_id").value(1))
                .andExpect(jsonPath("$[1].film_id").value(2))
                .andDo(document("mssql-find-films-with-less-equals-operator"));
    }

    @Test
    @DisplayName("Test find with In Operator")
    void findWithInOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "film_id=in=(2,3,5)")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(2), hasSize(2))))
                .andExpect(jsonPath("$[0].film_id").value(2))
                .andExpect(jsonPath("$[1].film_id").value(3))
                .andDo(document("mssql-find-films-with-in-operator"));
    }

    @Test
    @DisplayName("Test find with Not In Operator")
    void findWithNotInOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "film_id=out=(2,3,5)")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(2), hasSize(2))))
                .andExpect(jsonPath("$[0].film_id").value(1))
                .andExpect(jsonPath("$[1].film_id").value(4))
                .andDo(document("mssql-find-films-with-not-in-operator"));
    }

    @Test
    @DisplayName("Test find with Like Operator")
    void findWithLikeOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "title=like=ACADEMY")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1), hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(1))
                .andDo(document("mssql-find-films-with-like-operator"));
    }


    @Test
    @DisplayName("Test find with Starts With Operator")
    void findWithStartsWithOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "title=startWith=ACAD")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1), hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(1))
                .andDo(document("mssql-find-films-starts-with-operator"));
    }

    @Test
    @DisplayName("Test find with Ends With Operator")
    void findWithEndsWithOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("filter", "title=endWith=DINOSAUR")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1), hasSize(1))))
                .andExpect(jsonPath("$[0].film_id").value(1))
                .andDo(document("mssql-find-films-ends-with-operator"));
    }

    @Test
    @DisplayName("Should return 4xx when using equals operator with an invalid column")
    void findWithEqualsOperator_givenInvalidColumn_shouldReturn4xx() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film")
                        .accept(APPLICATION_JSON)
                        .param("filter", "actor_id==206")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.detail").value("Failed to parse RQL - Column not found film.actor_id"));

    }

    @Test
    @DisplayName("Test find with Not Like Operator")
    void findWithNotLikeOperator() throws Exception {
        mockMvc.perform(get( VERSION+"/mssql/film")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("fields", "title,film_id")
                        .param("filter", "title=nk=ACADEMY")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$[0].film_id", equalTo(2)))
                .andExpect(jsonPath("$[0].title", equalTo("ACE GOLDFINGER")))
                .andExpect(jsonPath("$[1].film_id", equalTo(3)))
                .andExpect(jsonPath("$[1].title", equalTo("ADAPTATION HOLES")))
                .andExpect(jsonPath("$[2].film_id", equalTo(4)))
                .andExpect(jsonPath("$[2].title", equalTo("AFFAIR PREJUDICE")))
                .andDo(document("mssql-find-films-with-not-like-operator"));
    }


    @Test
    @DisplayName("Test find with Equals OR StartWith Operator")
    void findWithEqualsAndStartWithCombinationalOperator() throws Exception {
        mockMvc.perform(get( VERSION+"/mssql/film")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("fields", "title,film_id")
                        .param("filter", "film_id==2")
                        .param("filter", "title=like=ACADEMY")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].film_id", equalTo(1)))
                .andExpect(jsonPath("$[0].title", equalTo("ACADEMY DINOSAUR")))
                .andExpect(jsonPath("$[1].film_id", equalTo(2)))
                .andExpect(jsonPath("$[1].title", equalTo("ACE GOLDFINGER")))
                .andDo(document("mssql-find-films-with-equals-or-like-operator"));
    }

    @Test
    @DisplayName("Test find with Equals AND Like Operator")
    void findWithEqualsAndLikeCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film")
                        .accept(APPLICATION_JSON)
                        .param("fields", "title,film_id")
                        .param("filter", "film_id==2;title=like=ACE")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].film_id", equalTo(2)))
                .andExpect(jsonPath("$[0].title", equalTo("ACE GOLDFINGER")))
                .andDo(document("mssql-find-films-with-equals-and-like-operator"));
    }

    @Test
    @DisplayName("Test find with Equals and Like and In Operator")
    void findWithEqualsAndLikeAndInCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film")
                        .accept(APPLICATION_JSON)
                        .param("fields", "title,film_id")
                        .param("filter", "film_id==2;title=like=ACE;rating=in=(G, mssql)")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].film_id", equalTo(2)))
                .andExpect(jsonPath("$[0].title", equalTo("ACE GOLDFINGER")))
                .andDo(document("mssql-find-films-with-equals-and-like-and-in-operator"));
    }

    @Test
    @DisplayName("Test find with Equals OR Like OR In Operator")
    void findWithEqualsOrLikeOrInCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film")
                        .accept(APPLICATION_JSON)
                        .param("fields", "title,film_id")
                        .param("filter", "film_id==2")
                        .param("filter", "title=like=ACADEMY")
                        .param("filter", "rating=in=(G, mssql)")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$[0].film_id", equalTo(1)))
                .andExpect(jsonPath("$[0].title", equalTo("ACADEMY DINOSAUR")))
                .andExpect(jsonPath("$[1].film_id", equalTo(2)))
                .andExpect(jsonPath("$[1].title", equalTo("ACE GOLDFINGER")))
                .andExpect(jsonPath("$[2].film_id", equalTo(4)))
                .andExpect(jsonPath("$[2].title", equalTo("AFFAIR PREJUDICE")))
                .andDo(document("mssql-find-films-with-equals-or-like-or-in-operator"));
    }

    @Test
    @DisplayName("Test find with Equals And Like And Not In Operator")
    void findWithEqualsAndLikeAndNotInCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film")
                        .accept(APPLICATION_JSON)
                        .param("fields", "title,film_id")
                        .param("filter", "film_id==2;title=like=ACE;rating=out=(G, mssql)")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isEmpty())
                .andDo(document("mssql-find-films-with-equals-and-like-and-out-operator"));
    }

    @Test
    @DisplayName("Test find with Equals OR Like OR Not In Operator")
    void findWithEqualsOrLikeOrNotInCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film")
                        .accept(APPLICATION_JSON)
                        .param("fields", "title,film_id")
                        .param("filter", "film_id==2")
                        .param("filter", "title=like=ACADEMY")
                        .param("filter", "rating=out=(G, mssql)")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$[0].film_id", equalTo(1)))
                .andExpect(jsonPath("$[0].title", equalTo("ACADEMY DINOSAUR")))
                .andExpect(jsonPath("$[1].film_id", equalTo(2)))
                .andExpect(jsonPath("$[1].title", equalTo("ACE GOLDFINGER")))
                .andExpect(jsonPath("$[2].film_id", equalTo(3)))
                .andExpect(jsonPath("$[2].title", equalTo("ADAPTATION HOLES")))
                .andDo(document("mssql-find-films-with-equals-or-like-or-out-operator"));

    }

    @Test
    @DisplayName("Test find with Equals and Like and In And Greater Than Equals Operator")
    void findWithEqualsAndLikeAndInAbdGreaterThanEualsCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film")
                        .accept(APPLICATION_JSON)
                        .param("fields", "title,film_id")
                        .param("filter", "film_id==2;title=like=ACE;rating=in=(G, mssql);language_id=ge=1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].film_id", equalTo(2)))
                .andExpect(jsonPath("$[0].title", equalTo("ACE GOLDFINGER")))
                .andDo(document("mssql-find-films-with-equals-and-like-and-in-and-greater-equal-operator"));
    }

    @Test
    @DisplayName("Test find with Equals and Like and In And Greater Than Operator")
    void findWithEqualsAndLikeAndInAbdGreaterThanCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film")
                        .accept(APPLICATION_JSON)
                        .param("fields", "title,film_id")
                        .param("filter", "film_id==2;title=like=ACE;rating=in=(G, mssql);language_id=gt=1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)))
                .andDo(document("mssql-find-films-with-equals-and-like-and-in-and-greater-operator"));
    }

    @Test
    @DisplayName("Test find with Equals and Like and In And Less Than Equals Operator")
    void findWithEqualsAndLikeAndInAndLessThanEqualsCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film")
                        .accept(APPLICATION_JSON)
                        .param("fields", "title,film_id")
                        .param("filter", "film_id==2;title=like=ACE;rating=in=(G, mssql);language_id=le=2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].film_id", equalTo(2)))
                .andExpect(jsonPath("$[0].title", equalTo("ACE GOLDFINGER")))
                .andDo(document("mssql-find-films-with-equals-and-like-and-in-and-less-equal-operator"));
    }

    @Test
    @DisplayName("Test find with Equals and Like and In And Less Than Operator")
    void findWithEqualsAndLikeAndInAndLessThanCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/mssql/film")
                        .accept(APPLICATION_JSON)
                        .param("fields", "title,film_id")
                        .param("filter", "film_id==2;title=like=ACE;rating=in=(G, mssql);language_id=lt=1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isEmpty())
                .andExpect(jsonPath("$.*", hasSize(0)))
                .andDo(document("mssql-find-films-with-equals-and-like-and-in-and-less-operator"));
    }

}
