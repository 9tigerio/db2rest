package com.homihq.db2rest.rest.sqlite;

import com.homihq.db2rest.SQLiteBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(30)
class SQLiteCountControllerTest extends SQLiteBaseIntegrationTest {

    @Test
    @DisplayName("Count all films")
    void countAllFilms() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film/count")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", equalTo(5)))
                .andDo(document("sqlite-count-all-films"));
    }

    @Test
    @DisplayName("Count all actors")
    void countAllActors() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/actor/count")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", equalTo(10)))
                .andDo(document("sqlite-count-all-actors"));
    }

    @Test
    @DisplayName("Count all employees")
    void countAllEmployees() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/employee/count")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", equalTo(3)))
                .andDo(document("sqlite-count-all-employees"));
    }

    @Test
    @DisplayName("Count films with filter")
    void countFilmsWithFilter() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film/count")
                        .param("filter", "language_id==1")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", equalTo(5)))
                .andDo(document("sqlite-count-films-with-filter"));
    }

    @Test
    @DisplayName("Count actors with filter")
    void countActorsWithFilter() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/actor/count")
                        .param("filter", "first_name==PENELOPE")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", equalTo(1)))
                .andDo(document("sqlite-count-actors-with-filter"));
    }

    @Test
    @DisplayName("Count employees with boolean filter")
    void countEmployeesWithBooleanFilter() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/employee/count")
                        .param("filter", "is_active==true")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", equalTo(2)))
                .andDo(document("sqlite-count-employees-boolean-filter"));
    }

    @Test
    @DisplayName("Count with multiple filters")
    void countWithMultipleFilters() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film/count")
                        .param("filter", "language_id==1;rating==G")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", equalTo(3)))
                .andDo(document("sqlite-count-multiple-filters"));
    }

    @Test
    @DisplayName("Count with non-existent filter")
    void countWithNonExistentFilter() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film/count")
                        .param("filter", "title==NON_EXISTENT")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", equalTo(0)))
                .andDo(document("sqlite-count-non-existent-filter"));
    }

    @Test
    @DisplayName("Count from non-existent table")
    void countFromNonExistentTable() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/non_existent_table/count")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("sqlite-count-non-existent-table"));
    }

    @Test
    @DisplayName("Count with invalid filter")
    void countWithInvalidFilter() throws Exception {

        mockMvc.perform(get(VERSION + "/sqlitedb/film/count")
                        .param("filter", "invalid_column==value")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("sqlite-count-invalid-filter"));
    }
}