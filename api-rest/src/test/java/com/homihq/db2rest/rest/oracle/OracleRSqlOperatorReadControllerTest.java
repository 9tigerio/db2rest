package com.homihq.db2rest.rest.oracle;



import com.homihq.db2rest.OracleBaseIntegrationTest;
import io.hosuaby.inject.resources.junit.jupiter.TestWithResources;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.http.MediaType;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(204)
@TestWithResources
class OracleRSqlOperatorReadControllerTest extends OracleBaseIntegrationTest {

    @Test
    @DisplayName("Test find with Equals Operator")
    void findWithEqualsOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM")
                        .accept(APPLICATION_JSON)
                        .param("fields", "TITLE,DESCRIPTION,RELEASE_YEAR")
                        .param("filter", "FILM_ID==2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1), hasSize(1))))
                .andExpect(jsonPath("$[0].TITLE", notNullValue()))
                .andExpect(jsonPath("$[0].DESCRIPTION", notNullValue()))
                .andExpect(jsonPath("$[0].RELEASE_YEAR", notNullValue()))
                .andDo(document("oracle-find-films-with-equals-operator"));

    }

    @Test
    @DisplayName("Test find with Not Equals Operator")
    void findWithNotEqualsOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM")
                        .accept(APPLICATION_JSON)
                        .param("fields", "FILM_ID")
                        .param("filter", "FILM_ID!=2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(3), hasSize(3))))
                .andExpect(jsonPath("$[0].FILM_ID").value(1))
                .andExpect(jsonPath("$[1].FILM_ID").value(3))
                .andExpect(jsonPath("$[2].FILM_ID").value(4))
                .andDo(document("oracle-find-films-with-not-equals-operator"));

    }

    @Test
    @DisplayName("Test find with Greater than Operator")
    void findWithGreaterThanOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "FILM_ID")
                        .param("filter", "FILM_ID=gt=2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(2), hasSize(2))))
                .andExpect(jsonPath("$[0].FILM_ID").value(3))
                .andExpect(jsonPath("$[1].FILM_ID").value(4))
                .andDo(document("oracle-find-films-with-greater-operator"));

    }

    @Test
    @DisplayName("Test find with Greater than Equals Operator")
    void findWithGreaterThanEqualsOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "FILM_ID")
                        .param("filter", "FILM_ID=ge=2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(3), hasSize(3))))
                .andExpect(jsonPath("$[0].FILM_ID").value(2))
                .andExpect(jsonPath("$[1].FILM_ID").value(3))
                .andExpect(jsonPath("$[2].FILM_ID").value(4))
                .andDo(document("oracle-find-films-with-greater-equals-operator"));

    }

    @Test
    @DisplayName("Test find with Less than Operator")
    void findWithLessThanOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "FILM_ID")
                        .param("filter", "FILM_ID=lt=2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1), hasSize(1))))
                .andExpect(jsonPath("$[0].FILM_ID").value(1))
                .andDo(document("oracle-find-films-with-less-operator"));
    }

    @Test
    @DisplayName("Test find with Less than Equals Operator")
    void findWithLessThanEqualsOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "FILM_ID")
                        .param("filter", "FILM_ID=le=2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(2), hasSize(2))))
                .andExpect(jsonPath("$[0].FILM_ID").value(1))
                .andExpect(jsonPath("$[1].FILM_ID").value(2))
                .andDo(document("oracle-find-films-with-less-equals-operator"));
    }

    @Test
    @DisplayName("Test find with In Operator")
    void findWithInOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "FILM_ID")
                        .param("filter", "FILM_ID=in=(2,3,5)")
                )

                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(2), hasSize(2))))
                .andExpect(jsonPath("$[0].FILM_ID").value(2))
                .andExpect(jsonPath("$[1].FILM_ID").value(3))
                .andDo(document("oracle-find-films-with-in-operator"));
    }

    @Test
    @DisplayName("Test find with Not In Operator")
    void findWithNotInOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "FILM_ID")
                        .param("filter", "FILM_ID=out=(2,3,5)")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(2), hasSize(2))))
                .andExpect(jsonPath("$[0].FILM_ID").value(1))
                .andExpect(jsonPath("$[1].FILM_ID").value(4))
                .andDo(document("oracle-find-films-with-not-in-operator"));
    }

    @Test
    @DisplayName("Test find with Like Operator")
    void findWithLikeOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "FILM_ID")
                        .param("filter", "TITLE=like=ACADEMY")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1), hasSize(1))))
                .andExpect(jsonPath("$[0].FILM_ID").value(1))
                .andDo(document("oracle-find-films-with-like-operator"));
    }

    @Test
    @DisplayName("Test find with Starts With Operator")
    void findWithStartsWithOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "FILM_ID")
                        .param("filter", "TITLE=startWith=ACAD")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1), hasSize(1))))
                .andExpect(jsonPath("$[0].FILM_ID").value(1))
                .andDo(document("oracle-find-films-starts-with-operator"));
    }

    @Test
    @DisplayName("Test find with Ends With Operator")
    void findWithEndsWithOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM;desc")
                        .accept(APPLICATION_JSON)
                        .param("fields", "FILM_ID")
                        .param("filter", "TITLE=endWith=DINOSAUR")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", anyOf(hasSize(1), hasSize(1))))
                .andExpect(jsonPath("$[0].FILM_ID").value(1))
                .andDo(document("oracle-find-films-ends-with-operator"));
    }

    @Test
    @DisplayName("Should return 4xx when using equals operator with an invalid column")
    void findWithEqualsOperator_givenInvalidColumn_shouldReturn4xx() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM")
                        .accept(APPLICATION_JSON)
                        .param("filter", "ACTOR_ID==206")
                )
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.detail").value("Failed to parse RQL - Column not found FILM.ACTOR_ID"));

    }


    @Test
    @DisplayName("Test find with Not Like Operator")
    void findWithNotLikeOperator() throws Exception {
        mockMvc.perform(get( VERSION+"/oradb/FILM")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("fields", "TITLE,FILM_ID")
                        .param("filter", "TITLE=nk=ACADEMY")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$[0].FILM_ID", equalTo(2)))
                .andExpect(jsonPath("$[0].TITLE", equalTo("ACE GOLDFINGER")))
                .andExpect(jsonPath("$[1].FILM_ID", equalTo(3)))
                .andExpect(jsonPath("$[1].TITLE", equalTo("ADAPTATION HOLES")))
                .andExpect(jsonPath("$[2].FILM_ID", equalTo(4)))
                .andExpect(jsonPath("$[2].TITLE", equalTo("AFFAIR PREJUDICE")))
                .andDo(document("oracle-find-film-with-not-like-operator"));
    }


    @Test
    @DisplayName("Test find with Equals OR StartWith Operator")
    void findWithEqualsAndStartWithCombinationalOperator() throws Exception {
        mockMvc.perform(get( VERSION+"/oradb/FILM")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("fields", "TITLE,FILM_ID")
                        .param("filter", "FILM_ID==2")
                        .param("filter", "TITLE=like=ACADEMY")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].FILM_ID", equalTo(1)))
                .andExpect(jsonPath("$[0].TITLE", equalTo("ACADEMY DINOSAUR")))
                .andExpect(jsonPath("$[1].FILM_ID", equalTo(2)))
                .andExpect(jsonPath("$[1].TITLE", equalTo("ACE GOLDFINGER")))
                .andDo(document("oracle-find-film-with-equals-or-like-operator"));
    }

    @Test
    @DisplayName("Test find with Equals AND Like Operator")
    void findWithEqualsAndLikeCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM")
                        .accept(APPLICATION_JSON)
                        .param("fields", "TITLE,FILM_ID")
                        .param("filter", "FILM_ID==2;TITLE=like=ACE")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].FILM_ID", equalTo(2)))
                .andExpect(jsonPath("$[0].TITLE", equalTo("ACE GOLDFINGER")))
                .andDo(document("oracle-find-film-with-equals-and-like-operator"));
    }

    @Test
    @DisplayName("Test find with Equals and Like and In Operator")
    void findWithEqualsAndLikeAndInCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM")
                        .accept(APPLICATION_JSON)
                        .param("fields", "TITLE,FILM_ID")
                        .param("filter", "FILM_ID==2;TITLE=like=ACE;rating=in=(G, oradb)")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].FILM_ID", equalTo(2)))
                .andExpect(jsonPath("$[0].TITLE", equalTo("ACE GOLDFINGER")))
                .andDo(document("oracle-find-film-with-equals-and-like-and-in-operator"));
    }

    @Test
    @DisplayName("Test find with Equals OR Like OR In Operator")
    void findWithEqualsOrLikeOrInCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM")
                        .accept(APPLICATION_JSON)
                        .param("fields", "TITLE,FILM_ID")
                        .param("filter", "FILM_ID==2")
                        .param("filter", "TITLE=like=ACADEMY")
                        .param("filter", "rating=in=(G, oradb)")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$[0].FILM_ID", equalTo(1)))
                .andExpect(jsonPath("$[0].TITLE", equalTo("ACADEMY DINOSAUR")))
                .andExpect(jsonPath("$[1].FILM_ID", equalTo(2)))
                .andExpect(jsonPath("$[1].TITLE", equalTo("ACE GOLDFINGER")))
                .andExpect(jsonPath("$[2].FILM_ID", equalTo(4)))
                .andExpect(jsonPath("$[2].TITLE", equalTo("AFFAIR PREJUDICE")))
                .andDo(document("oracle-find-film-with-equals-or-like-or-in-operator"));
    }

    @Test
    @DisplayName("Test find with Equals And Like And Not In Operator")
    void findWithEqualsAndLikeAndNotInCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM")
                        .accept(APPLICATION_JSON)
                        .param("fields", "TITLE,FILM_ID")
                        .param("filter", "FILM_ID==2;TITLE=like=ACE;RATING=out=(G, oradb)")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isEmpty())
                .andDo(document("oracle-find-film-with-equals-and-like-and-out-operator"));
    }

    @Test
    @DisplayName("Test find with Equals OR Like OR Not In Operator")
    void findWithEqualsOrLikeOrNotInCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM")
                        .accept(APPLICATION_JSON)
                        .param("fields", "TITLE,FILM_ID")
                        .param("filter", "FILM_ID==2")
                        .param("filter", "TITLE=like=ACADEMY")
                        .param("filter", "RATING=out=(G, oradb)")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$[0].FILM_ID", equalTo(1)))
                .andExpect(jsonPath("$[0].TITLE", equalTo("ACADEMY DINOSAUR")))
                .andExpect(jsonPath("$[1].FILM_ID", equalTo(2)))
                .andExpect(jsonPath("$[1].TITLE", equalTo("ACE GOLDFINGER")))
                .andExpect(jsonPath("$[2].FILM_ID", equalTo(3)))
                .andExpect(jsonPath("$[2].TITLE", equalTo("ADAPTATION HOLES")))
                .andDo(document("oracle-find-film-with-equals-or-like-or-out-operator"));

    }

    @Test
    @DisplayName("Test find with Equals and Like and In And Greater Than Equals Operator")
    void findWithEqualsAndLikeAndInAbdGreaterThanEqualsCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM")
                        .accept(APPLICATION_JSON)
                        .param("fields", "TITLE,FILM_ID")
                        .param("filter", "FILM_ID==2;TITLE=like=ACE;RATING=in=(G, oradb);LANGUAGE_ID=ge=1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].FILM_ID", equalTo(2)))
                .andExpect(jsonPath("$[0].TITLE", equalTo("ACE GOLDFINGER")))
                .andDo(document("oracle-find-film-with-equals-and-like-and-in-and-greater-equal-operator"));
    }

    @Test
    @DisplayName("Test find with Equals and Like and In And Greater Than Operator")
    void findWithEqualsAndLikeAndInAbdGreaterThanCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM")
                        .accept(APPLICATION_JSON)
                        .param("fields", "TITLE,FILM_ID")
                        .param("filter", "FILM_ID==2;TITLE=like=ACE;RATING=in=(G, oradb);LANGUAGE_ID=gt=1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)))
                .andDo(document("oradb-find-film-with-equals-and-like-and-in-and-greater-operator"));
    }

    @Test
    @DisplayName("Test find with Equals and Like and In And Less Than Equals Operator")
    void findWithEqualsAndLikeAndInAndLessThanEqualsCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM")
                        .accept(APPLICATION_JSON)
                        .param("fields", "TITLE,FILM_ID")
                        .param("filter", "FILM_ID==2;TITLE=like=ACE;RATING=in=(G, oradb);LANGUAGE_ID=le=2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].FILM_ID", equalTo(2)))
                .andExpect(jsonPath("$[0].TITLE", equalTo("ACE GOLDFINGER")))
                .andDo(document("oracle-find-film-with-equals-and-like-and-in-and-less-equal-operator"));
    }

    @Test
    @DisplayName("Test find with Equals and Like and In And Less Than Operator")
    void findWithEqualsAndLikeAndInAndLessThanCombinationalOperator() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/FILM")
                        .accept(APPLICATION_JSON)
                        .param("fields", "TITLE,FILM_ID")
                        .param("filter", "FILM_ID==2;TITLE=like=ACE;RATING=in=(G, oradb);LANGUAGE_ID=lt=1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isEmpty())
                .andExpect(jsonPath("$.*", hasSize(0)))
                .andDo(document("oracle-find-film-with-equals-and-like-and-in-and-less-operator"));
    }

}
