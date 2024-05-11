package com.homihq.db2rest.rest.mongo;

import com.homihq.db2rest.MongoBaseIntegrationTest;
import com.homihq.db2rest.MongoContainerConfiguration;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(491)
@TestPropertySource(properties = {"db2rest.allowSafeDelete=false"})
class MongoDBDeleteAllTest extends MongoBaseIntegrationTest {


    @Test
    @DisplayName("Delete all documents while allowSafeDelete=false")
    void deleteAllWithAllowSafeDeleteFalse() throws Exception {
        mockMvc.perform(delete("/mongo/Sakila_actors")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$.rows", Matchers.equalTo(8)))
                .andDo(document("mongodb-delete-all-actors"));
    }
}
