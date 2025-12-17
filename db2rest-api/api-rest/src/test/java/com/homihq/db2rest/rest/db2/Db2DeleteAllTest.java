package com.homihq.db2rest.rest.db2;


import com.homihq.db2rest.DB2BaseIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.test.context.TestPropertySource;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(291)
@TestPropertySource(properties = {"db2rest.allowSafeDelete=false"})
class Db2DeleteAllTest extends DB2BaseIntegrationTest {

    @Test
    @DisplayName("Delete all records while allowSafeDelete=false")
    void deleteAllWithAllowSafeDeleteFalse() throws Exception {
        mockMvc.perform(delete(VERSION + "/db2b/COUNTRY")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", Matchers.equalTo(6)))
                .andDo(document("db2-delete-all"));
    }
}
