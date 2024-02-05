package com.homihq.db2rest.rest;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PgCreateControllerTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @DisplayName("Create a film.")
    void create() throws Exception {

        var json = """ 
                       {
                       "title" : "Dunki",
                        "description" : "Film about illegal immigration" ,
                        "release_year" : 2023,
                        "language_id" : 1,
                        "original_language_id" : null,
                        "rental_duration" : 6,
                        "rental_rate" : 0.99 ,
                        "length" : 150,
                        "replacement_cost" : 20.99 ,
                        "rating" : "PG-13" ,
                        "special_features" : "Commentaries"
                	
                }
                """;


        mockMvc.perform(post("/film").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .header("Content-Profile", "public")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", Matchers.equalTo(1)))
                .andExpect(jsonPath("$.generated_key").exists())
                .andDo(print())
                .andDo(document("pg-create-a-film"));

    }

    @Test
    @DisplayName("Create a film with error.")
    void createError() throws Exception {

        var json = """ 
                       {
                       "title" : "Dunki",
                        "description" : "Film about illegal immigration" ,
                        "release_year" : 2023,
                        "language_id" : 1,
                        "original_language_id" : null,
                        "rental_duration" : 6,
                        "rental_rate" : 0.99 ,
                        "length" : 150,
                        "replacement_cost" : 20.99 ,
                        "rating" : "PG-13" ,
                        "special_features" : "Commentaries",
                     "country" : "USA"
                }
                """;


        mockMvc.perform(post("/film").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .header("Content-Profile", "public")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("pg-create-a-film-error"));

    }

    @Test
    @DisplayName("Create a film - non existent table.")
    void createNonExistentTable() throws Exception {

        var json = """ 
                       {
                       "title" : "Dunki",
                        "description" : "Film about illegal immigration" ,
                        "release_year" : 2023,
                        "language_id" : 1,
                        "original_language_id" : null,
                        "rental_duration" : 6,
                        "rental_rate" : 0.99 ,
                        "length" : 150,
                        "replacement_cost" : 20.99 ,
                        "rating" : "PG-13" ,
                        "special_features" : "Commentaries"
                	
                }
                """;

        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .header("Content-Profile", "public")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("pg-create-a-film-no-table"));

    }

    @Test
    @DisplayName("Create a director - number tsid type")
    void createDirectorWithGivenTsidType() throws Exception {

        var json = """
                {
                "first_name": "Rajkumar",
                "last_name": "Hirani"
                }
                """;

        mockMvc.perform(post("/director").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .param("tsid", "director_id")
                        .param("tsidType", "number")
                        .header("Content-Profile", "public")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("pg-create-a-director-with-tsid-given-tsid-type"));

    }

    @Test
    @DisplayName("Create a director - with wrong tsid type")
    void createDirectorWithWrongTsidType() throws Exception {
        var json = """
                {
                    "first_name": "Rohit",
                    "last_name": "Shetty"
                }
                """;
        mockMvc.perform(post("/director").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .param("tsid", "director_id")
                        .param("tsidType", "float") //only support string, number
                        .header("Content-Profile", "public")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("pg-create-a-director-with-wrong-tsid-type"));

    }

    @Test
    @DisplayName("Create a director - with default tsid type")
    void createDirectorWithDefaultTsidType() throws Exception {
        var json = """
                {
                    "first_name": "Anurag",
                    "last_name": "Kashyap"
                }
                """;
        mockMvc.perform(post("/director").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .param("tsid", "director_id")
                        .header("Content-Profile", "public")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("pg-create-a-director-with-tsid-given-tsid-type"));
    }

    @Test
    @DisplayName("Create a film with subset of columns")
    void create_a_film_with_columns_specified_in_query_param() throws Exception {
        var json = """ 
                       {
                       "title" : "Dunki",
                        "description" : "Film about illegal immigration",
                        "release_year" : 2023,
                        "language_id" : 1,
                        "original_language_id" : null,
                        "rental_duration" : 6,
                        "rental_rate" : 0.99 ,
                        "length" : 150,
                        "replacement_cost" : 20.99 ,
                        "rating" : "PG-13" ,
                        "special_features" : "Commentaries"
                }
                """;

        var result = mockMvc.perform(post("/film")
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .header("Content-Profile", "public")
                        .queryParam("columns", "title,description,language_id")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.generated_key").exists())
                .andDo(print())
                .andDo(document("pg-create-a-film"))
                .andReturn();

        var primary_key = JsonPath.read(result.getResponse().getContentAsString(), "$.generated_key");

        mockMvc.perform(get("/film")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("select", "title,release_year")
                        .queryParam("filter", String.format("film_id==%s", primary_key)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", equalTo("Dunki")))
                .andExpect(jsonPath("$[0].release_year").doesNotExist())
                .andDo(print());

        // cleanup data
        assertTrue(deleteRow("film", "film_id", (int)primary_key));
    }

    @Test
    @DisplayName("Ignore if columns parameter is blank")
    void should_ignore_when_columns_query_param_is_empty() throws Exception {
        var json = """ 
                       {
                       "title" : "Dunki",
                        "description" : "Film about illegal immigration",
                        "release_year" : 2023,
                        "language_id" : 1,
                        "original_language_id" : null,
                        "rental_duration" : 6,
                        "rental_rate" : 0.99 ,
                        "length" : 150,
                        "replacement_cost" : 20.99 ,
                        "rating" : "PG-13" ,
                        "special_features" : "Commentaries"
                }
                """;

        var result = mockMvc.perform(post("/film")
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .header("Content-Profile", "public")
                        .queryParam("columns", "")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.generated_key").exists())
                .andDo(print())
                .andDo(document("pg-create-a-film"))
                .andReturn();

        var primary_key = JsonPath.read(result.getResponse().getContentAsString(), "$.generated_key");

        mockMvc.perform(get("/film")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("select", "title,release_year")
                        .queryParam("filter", String.format("film_id==%s", primary_key)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", equalTo("Dunki")))
                .andExpect(jsonPath("$[0].release_year", equalTo(2023)))
                .andDo(print());

        // cleanup data
        assertTrue(deleteRow("film", "film_id", (int)primary_key));
    }

    @Test
    @DisplayName("Column is present in columns param but not in payload")
    void column_is_present_in_columns_query_param_but_not_in_payload() throws Exception {
        var json = """ 
                       {
                       "title" : "Dunki",
                        "release_year" : 2023,
                        "language_id" : 1,
                        "original_language_id" : null,
                        "rental_duration" : 6,
                        "rental_rate" : 0.99 ,
                        "length" : 150,
                        "replacement_cost" : 20.99 ,
                        "rating" : "PG-13" ,
                        "special_features" : "Commentaries"
                }
                """;

        var result = mockMvc.perform(post("/film").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .header("Content-Profile", "public")
                        .queryParam("columns", "title,description,language_id")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row", equalTo(1)))
                .andExpect(jsonPath("$.generated_key").exists())
                .andDo(print())
                .andDo(document("pg-create-a-film"))
                .andReturn();

        var primary_key = JsonPath.read(result.getResponse().getContentAsString(), "$.generated_key");

        mockMvc.perform(get("/film")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParam("select", "title,description")
                        .queryParam("filter", String.format("film_id==%s", primary_key)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title", equalTo("Dunki")))
                .andExpect(jsonPath("$[0].description").doesNotExist())
                .andDo(print());

        // cleanup data
        assertTrue(deleteRow("film", "film_id", (int)primary_key));
    }

    @Test
    @DisplayName("Column violates not-null constraint")
    void column_violates_not_null_constraint() throws Exception {
        var json = """ 
                       {
                       "title" : "Dunki",
                        "description" : "Film about illegal immigration",
                        "release_year" : 2023,
                        "language_id" : 1,
                        "original_language_id" : null,
                        "rental_duration" : 6,
                        "rental_rate" : 0.99 ,
                        "length" : 150,
                        "replacement_cost" : 20.99 ,
                        "rating" : "PG-13" ,
                        "special_features" : "Commentaries"
                }
                """;

        mockMvc.perform(post("/film").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .header("Content-Profile", "public")
                        .queryParam("columns", "title,description")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail",
                        containsString("null value in column \"language_id\" of relation \"film\" violates not-null constraint")))
                .andDo(print())
                .andDo(document("pg-create-a-film"));
    }
}
