package com.homihq.db2rest.rest.mssql;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestClassOrder;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(506)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MsSQLDateTimeAllTest extends MsSQLBaseIntegrationTest {

    @ParameterizedTest
    @MethodSource("isoDateTimeFormats")
    @Order(1)
    @DisplayName("Test ISO Date Time formats")
    void createActorWithIsoDateTimeFormats(String isoDateTime) throws Exception {
        // Prepare the request with datetime fields
        Map<String, Object> actorRequestWithDateTime = new HashMap<>();
        actorRequestWithDateTime.put("first_name", "Graeme");
        actorRequestWithDateTime.put("last_name", "Smith");
        actorRequestWithDateTime.put("last_update", isoDateTime);

        var result = mockMvc.perform(post(VERSION + "/mssql/actor")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actorRequestWithDateTime)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andDo(document("mssql-create-an-actor-with-datetime"))
                .andReturn();

        var pk = JsonPath.read(result.getResponse().getContentAsString(), "$.keys.GENERATED_KEYS");
        assertTrue(deleteRow("actor", "actor_id", (int) pk));
    }
}
