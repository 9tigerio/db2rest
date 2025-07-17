package com.homihq.db2rest.rest.mssql;


import io.hosuaby.inject.resources.junit.jupiter.TestWithResources;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(501)
@TestWithResources
class MsSQLFindOneControllerTest extends MsSQLBaseIntegrationTest {

    @Test
    @DisplayName("Find one film")
    void findOneFilm() throws Exception {
        mockMvc.perform(get(getPrefixApiUrl() + "/film/one")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("fields", "title,film_id")
                        .param("filter", "film_id==1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.film_id", equalTo(1)))
                .andExpect(jsonPath("$.title", equalTo("ACADEMY DINOSAUR")))
                .andDo(print())
                .andDo(document(DB_NAME + "-get-on-film"));
    }
}
