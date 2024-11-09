package com.homihq.db2rest.rest.oracle;

import com.adelean.inject.resources.junit.jupiter.WithJacksonMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.homihq.db2rest.OracleBaseIntegrationTest;
import com.homihq.db2rest.rest.DateTimeUtil;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.HashMap;
import java.util.Map;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(292)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OracleDateTimeAllTest extends OracleBaseIntegrationTest {

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final String dateTime = "2024-03-15T10:30:45.000";

    @Test
    @Order(1)
    @DisplayName("Test Create a actor with datetime fields")
    void createActorWithDateTimeFields() throws Exception {
        // Prepare the request with datetime fields
        Map<String, Object> actorRequestWithDateTime = new HashMap<>();
        actorRequestWithDateTime.put("first_name", "Collective");
        actorRequestWithDateTime.put("last_name", "Unconscious");
        actorRequestWithDateTime.put("last_update", "2020-03-15T14:30:45.000");
        mockMvc.perform(post(VERSION + "/oradb/ACTOR")
                        .queryParam("sequences", "actor_id:actor_sequence")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorRequestWithDateTime)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andDo(document("oracle-create-an-actor-with-datetime"));
    }

    @Test
    @Order(1)
    @DisplayName("Test Create an actor with error timestamp field")
    void createActorWithErrorDateTimeField() throws Exception {
        Map<String, Object> actorRequestWithErrorDateTime = new HashMap<>();
        actorRequestWithErrorDateTime.put("first_name", "Hero");
        actorRequestWithErrorDateTime.put("last_name", "shadow");
        actorRequestWithErrorDateTime.put("last_update", "2023-15-35T14:75:90");

        mockMvc.perform(post(VERSION + "/oradb/ACTOR")
                        .queryParam("sequences", "actor_id:actor_sequence")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorRequestWithErrorDateTime)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("oracle-create-an-actor-with-error-timestamp"));
    }

    @Test
    @Order(2)
    @DisplayName("Test update an actor with datetime field")
    void updateActorWithDateTimeField() throws Exception {
        // Prepare the request with datetime fields
        Map<String, Object> updateActorRequestWithDateTime = new HashMap<>();
        updateActorRequestWithDateTime.put("last_update", dateTime);

        mockMvc.perform(patch(VERSION + "/oradb/ACTOR")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "last_name == Unconscious")
                        .content(objectMapper.writeValueAsString(updateActorRequestWithDateTime)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("oracle-update-an-actor-with-datetime"));
    }

    @Test
    @Order(3)
    @DisplayName("Test get an actor with datetime fields")
    void getActorWithDateTimeFields() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/ACTOR")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "first_name == Collective"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("[0].LAST_NAME", equalTo("Unconscious")))
                .andDo(result -> assertEquals(dateTime, DateTimeUtil.utcToLocalTimestampStringOracle(result)))
                .andDo(document("oracle-get-an-actor-with-datetime"));
    }

    @Test
    @Order(3)
    @DisplayName("Test get an actor filter by timestamp")
    void getActorFilterByTimeStamp() throws Exception {
        mockMvc.perform(get(VERSION + "/oradb/ACTOR")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "last_update <= \"2024-03-15T10:30:45.00Z\""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("[0].LAST_NAME", equalTo("Unconscious")))
                .andDo(result -> assertEquals(dateTime, DateTimeUtil.utcToLocalTimestampStringOracle(result)))
                .andDo(document("oracle-get-an-actor-filter-by-timestamp"));
    }


    @Test
    @Order(4)
    @DisplayName("Test delete an actor by timestamp")
    void deleteActorByTimeStamp() throws Exception {
        mockMvc.perform(delete(VERSION + "/oradb/ACTOR")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .param("filter", "last_update <= \"2024-03-15T10:30:45.00Z\""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("oracle-delete-an-actor-by-timestamp"));
    }
}
