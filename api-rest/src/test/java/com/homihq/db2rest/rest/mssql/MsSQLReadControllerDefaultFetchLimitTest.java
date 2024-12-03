package com.homihq.db2rest.rest.mssql;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(306)
@TestPropertySource(properties = {"db2rest.defaultFetchLimit=5"})
class MsSQLReadControllerDefaultFetchLimitTest extends MsSQLBaseIntegrationTest {

    @Test
    @DisplayName("Get all with default fetch limit set to 5")
    void findAllPersonsWithDefaultFetchLimit5() throws Exception {
        mockMvc.perform(get(getPrefixApiUrl() + "/person")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", anyOf(hasSize(5))))
                .andDo(document(DB_NAME + "-find-all-persons-with-default-fetch-limit-5"));
    }
}
