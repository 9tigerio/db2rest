package com.homihq.db2rest.rest.db2;

import com.homihq.db2rest.DB2BaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
//@Order(203)
@TestPropertySource(properties = {"db2rest.defaultFetchLimit=5"})
class Db2ReadControllerDefaultFetchLimitTest extends DB2BaseIntegrationTest {

    @Test
    @DisplayName("Get all with default fetch limit set to 5")
    void findAllPersonsWithDefaultFetchLimit5() throws Exception {
        mockMvc.perform(get(VERSION + "/db2b/PERSON")
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", anyOf(hasSize(5))))
                .andDo(document("db2-find-all-persons-with-default-fetch-limit-5"));
    }
}
