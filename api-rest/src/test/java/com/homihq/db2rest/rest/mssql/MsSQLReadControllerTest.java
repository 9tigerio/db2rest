package com.homihq.db2rest.rest.mssql;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(1)
class MsSQLReadControllerTest extends MsSQLBaseIntegrationTest {

    @Test
    @DisplayName("Find all films - all columns.")
    void findAllFilmsWithAllColumns() throws Exception {
        mockMvc.perform(get(getPrefixApiUrl() + "/film")
                        .contentType(APPLICATION_JSON).accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$[0].*", hasSize(14)))
                .andDo(document(DB_NAME + "-find-all-films-all-columns"));
    }

    @Test
    @DisplayName("Find all films - 3 columns")
    void findAllFilmsWithThreeCols() throws Exception {
        mockMvc.perform(get(getPrefixApiUrl() + "/film")
                        .contentType(APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("fields", "title,description,release_year")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$[0].*", hasSize(3)))
                .andDo(document(DB_NAME + "-find-all-films-3-columns"));
    }

    @Test
    @DisplayName("Find all films - with column alias")
    void findAllFilmsWithColumnAlias() throws Exception {
        mockMvc.perform(get(getPrefixApiUrl() + "/film")
                        .contentType(APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("fields", "title,description,release_year:releaseYear")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$[0].title", notNullValue()))
                .andExpect(jsonPath("$[0].description", notNullValue()))
                .andExpect(jsonPath("$[0].releaseYear", notNullValue()))
                .andDo(document(DB_NAME + "-find-all-films-with-column-alias"));
    }

    @Test
    @DisplayName("Find all films with sorting")
    void findAllFilmsWithSorting() throws Exception {
        mockMvc.perform(get(getPrefixApiUrl() + "/film")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("sort", "film_id;desc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andDo(print())
                .andExpect(jsonPath("$.*", hasSize(4)))
                .andExpect(jsonPath("$[0].film_id", equalTo(4)))
                .andExpect(jsonPath("$[1].film_id", equalTo(3)))
                .andExpect(jsonPath("$[2].film_id", equalTo(2)))
                .andExpect(jsonPath("$[3].film_id", equalTo(1)))
                .andDo(document(DB_NAME + "-find-all-films-with-sorting"));
    }

    @Test
    @DisplayName("Find all films with sorting and pagination")
    void findAllFilmsWithSortingAndPagination() throws Exception {
        mockMvc.perform(get(getPrefixApiUrl() + "/film")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("fields", "film_id")
                        .param("offset", "1")
                        .param("limit", "3")
                        .param("sort", "film_id;desc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andDo(print())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$[0].film_id", equalTo(3)))
                .andExpect(jsonPath("$[1].film_id", equalTo(2)))
                .andExpect(jsonPath("$[2].film_id", equalTo(1)))
                .andDo(document(DB_NAME + "-find-all-films-with-sorting-and-pagination"));
    }

    @Test
    @DisplayName("Find all films with pagination")
    void findAllFilmsWithPagination() throws Exception {
        mockMvc.perform(get(getPrefixApiUrl() + "/film")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("fields", "film_id,title")
                        .param("offset", "1")
                        .param("limit", "3")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andDo(print())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$[0].film_id", equalTo(2)))
                .andExpect(jsonPath("$[1].film_id", equalTo(3)))
                .andExpect(jsonPath("$[2].film_id", equalTo(4)))
                .andDo(document(DB_NAME + "-find-all-films-with-pagination"));
    }

    @Test
    @DisplayName("Find all films by filter")
    void findAllFilmsByFilter() throws Exception {
        mockMvc.perform(get(getPrefixApiUrl() + "/film")
                        .accept(MediaType.APPLICATION_JSON)
                        .param("fields", "title,film_id")
                        .param("filter", "title=like=\"ACADEMY%\";release_year==2006")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].film_id", equalTo(1)))
                .andExpect(jsonPath("$[0].title", equalTo("ACADEMY DINOSAUR")))
                .andDo(document(DB_NAME + "-find-films-by-filter"));
    }

}
