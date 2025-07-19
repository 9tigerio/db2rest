package com.homihq.db2rest.rest.sqlite;

import com.homihq.db2rest.SQLiteBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(150)
class SQLiteRSqlOperatorReadControllerTest extends SQLiteBaseIntegrationTest {

    @Test
    @DisplayName("RSQL - Equal operator")
    void testEqualOperator() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id==1")
                        .param("fields", "film_id,title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].film_id", equalTo(1)))
                .andDo(document("sqlite-rsql-equal"));
    }

    @Test
    @DisplayName("RSQL - Not equal operator")
    void testNotEqualOperator() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id!=1")
                        .param("fields", "film_id,title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(4)))
                .andDo(document("sqlite-rsql-not-equal"));
    }

    @Test
    @DisplayName("RSQL - Greater than operator")
    void testGreaterThanOperator() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id>3")
                        .param("fields", "film_id,title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(document("sqlite-rsql-greater-than"));
    }

    @Test
    @DisplayName("RSQL - Less than operator")
    void testLessThanOperator() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id<3")
                        .param("fields", "film_id,title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(document("sqlite-rsql-less-than"));
    }

    @Test
    @DisplayName("RSQL - Greater than or equal operator")
    void testGreaterThanOrEqualOperator() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id>=3")
                        .param("fields", "film_id,title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(3)))
                .andDo(document("sqlite-rsql-greater-than-equal"));
    }

    @Test
    @DisplayName("RSQL - Less than or equal operator")
    void testLessThanOrEqualOperator() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id<=3")
                        .param("fields", "film_id,title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(3)))
                .andDo(document("sqlite-rsql-less-than-equal"));
    }

    @Test
    @DisplayName("RSQL - In operator")
    void testInOperator() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id=in=(1,3,5)")
                        .param("fields", "film_id,title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(3)))
                .andDo(document("sqlite-rsql-in"));
    }

    @Test
    @DisplayName("RSQL - Not in operator")
    void testNotInOperator() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id=out=(1,2)")
                        .param("fields", "film_id,title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(3)))
                .andDo(document("sqlite-rsql-not-in"));
    }

    @Test
    @DisplayName("RSQL - Like operator")
    void testLikeOperator() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "title=like=ACADEMY")
                        .param("fields", "film_id,title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].title", equalTo("ACADEMY DINOSAUR")))
                .andDo(document("sqlite-rsql-like"));
    }

    @Test
    @DisplayName("RSQL - AND operator")
    void testAndOperator() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id>=1;film_id<=3")
                        .param("fields", "film_id,title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(3)))
                .andDo(document("sqlite-rsql-and"));
    }

    @Test
    @DisplayName("RSQL - OR operator")
    void testOrOperator() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id==1,film_id==5")
                        .param("fields", "film_id,title")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(document("sqlite-rsql-or"));
    }

    @Test
    @DisplayName("RSQL - Complex query with parentheses")
    void testComplexQueryWithParentheses() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "(film_id==1,film_id==2);rating==G")
                        .param("fields", "film_id,title,rating")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
                .andDo(document("sqlite-rsql-complex-parentheses"));
    }

    @Test
    @DisplayName("RSQL - Boolean field query")
    void testBooleanFieldQuery() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/employee")
                        .param("filter", "is_active==true")
                        .param("fields", "emp_id,first_name,is_active")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(2)))
                .andDo(document("sqlite-rsql-boolean"));
    }

    @Test
    @DisplayName("RSQL - String field with wildcards")
    void testStringFieldWithWildcards() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/actor")
                        .param("filter", "first_name=like=ETTE")
                        .param("fields", "actor_id,first_name,last_name")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].first_name", equalTo("BETTE")))
                .andDo(document("sqlite-rsql-string-wildcards"));
    }

    @Test
    @DisplayName("RSQL - Null value query")
    void testNullValueQuery() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "original_language_id=isnull=true")
                        .param("fields", "film_id,title,original_language_id")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(5)))
                .andDo(document("sqlite-rsql-null-value"));
    }

    @Test
    @DisplayName("RSQL - Invalid syntax")
    void testInvalidSyntax() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id===invalid")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("sqlite-rsql-invalid-syntax"));
    }

    @Test
    @DisplayName("RSQL - Non-existent field")
    void testNonExistentField() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "non_existent_field==value")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("sqlite-rsql-non-existent-field"));
    }
}