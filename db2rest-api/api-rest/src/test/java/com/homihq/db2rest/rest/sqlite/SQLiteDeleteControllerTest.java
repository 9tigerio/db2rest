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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(50)
class SQLiteDeleteControllerTest extends SQLiteBaseIntegrationTest {

    @Test
    @DisplayName("Delete a film by ID")
    void deleteFilmById() throws Exception {

        mockMvc.perform(delete(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id==1")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("sqlite-delete-film-by-id"));

        // Verify the deletion
        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id==1")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("sqlite-verify-film-deletion"));
    }

    @Test
    @DisplayName("Delete an actor by ID")
    void deleteActorById() throws Exception {

        mockMvc.perform(delete(VERSION + "/sqlitedb/actor")
                .param("filter", "actor_id==10")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("sqlite-delete-actor-by-id"));

        // Verify the deletion
        mockMvc.perform(get(VERSION + "/sqlitedb/actor")
                .param("filter", "actor_id==10")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("sqlite-verify-actor-deletion"));
    }


    @Test
    @DisplayName("Delete with filter")
    void deleteWithFilter() throws Exception {

        mockMvc.perform(delete(VERSION + "/sqlitedb/actor")
                        .param("filter", "first_name==JOE")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("sqlite-delete-with-filter"));

        // Verify the deletion
        mockMvc.perform(get(VERSION + "/sqlitedb/actor")
                        .param("filter", "first_name==JOE")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andDo(document("sqlite-verify-filter-deletion"));
    }

    @Test
    @DisplayName("Delete non-existent record")
    void deleteNonExistentRecord() throws Exception {

        mockMvc.perform(delete(VERSION + "/sqlitedb/film")
                        .param("filter", "film_id==999")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(0)))
                .andDo(document("sqlite-delete-non-existent"));
    }

    @Test
    @DisplayName("Delete from non-existent table")
    void deleteFromNonExistentTable() throws Exception {

        mockMvc.perform(delete(VERSION + "/sqlitedb/non_existent_table")
                        .param("filter", "film_id==1")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("sqlite-delete-non-existent-table"));
    }

    @Test
    @DisplayName("Delete with invalid filter")
    void deleteWithInvalidFilter() throws Exception {

        mockMvc.perform(delete(VERSION + "/sqlitedb/actor")
                        .param("filter", "invalid_column==value")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("sqlite-delete-invalid-filter"));
    }
}