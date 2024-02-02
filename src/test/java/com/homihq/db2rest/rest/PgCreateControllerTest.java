package com.homihq.db2rest.rest;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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
               // .andDo(print())
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
                //.andDo(print())
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
               // .andDo(print())
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
               // .andDo(print())
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
                //.andDo(print())
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
                //.andDo(print())
                .andDo(document("pg-create-a-director-with-tsid-given-tsid-type"));
    }
}
