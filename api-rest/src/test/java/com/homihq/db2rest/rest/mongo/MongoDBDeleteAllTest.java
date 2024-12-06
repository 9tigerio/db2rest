package com.homihq.db2rest.rest.mongo;

import com.homihq.db2rest.MongoBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.test.context.TestPropertySource;

import static com.homihq.db2rest.mongo.rest.api.MongoRestApi.VERSION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(491)
@TestPropertySource(properties = {"db2rest.allowSafeDelete=false"})
class MongoDBDeleteAllTest extends MongoBaseIntegrationTest {

    @Test
    @DisplayName("Delete all documents while allowSafeDelete=false")
    void deleteAllWithAllowSafeDeleteFalse() throws Exception {
        mockMvc.perform(delete(VERSION + "/mongo/Sakila_actors")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.rows", Matchers.equalTo(8)))
                .andDo(document("mongodb-delete-all-actors"));
    }
}
