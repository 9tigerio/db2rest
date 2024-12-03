package com.homihq.db2rest.rest.mssql;

import com.adelean.inject.resources.junit.jupiter.GivenJsonResource;
import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(501)
@TestWithResources
class MsSQLInnerJoinMultiTableControllerTest extends MsSQLBaseIntegrationTest {

    @GivenJsonResource(TEST_JSON_FOLDER + "/INNER_JOIN_MULTI_TABLE.json")
    List<Map<String, Object>> innerJoinMultiTable;

    @Test
    @DisplayName("Inner multi-table Join")
    void innerMultiTable() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/film/_expand")
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(innerJoinMultiTable))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].*", hasSize(17)))
                .andExpect(jsonPath("$[0].film_id", equalTo(1)))
                .andExpect(jsonPath("$[0].language_id", equalTo(1)))
                .andExpect(jsonPath("$[0].actor_id", equalTo(1)))
                .andExpect(jsonPath("$[0].first_name", equalTo("PENELOPE")))
                .andExpect(jsonPath("$[0].last_name", equalTo("GUINESS")))

                .andDo(document(DB_NAME + "-inner-multi-table-join"));
    }

}
