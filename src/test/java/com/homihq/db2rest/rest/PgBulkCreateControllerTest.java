package com.homihq.db2rest.rest;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PgBulkCreateControllerTest extends PostgreSQLBaseIntegrationTest {

    @Test
    @DisplayName("Create many films.")
    void create() throws Exception {

       var json = """ 
         [
               {
                   "title" : "Dunki",
                    "description" : "Film about illegal immigration" ,
                    "release_year" : 2023,
                    "language_id" : 6,
                    "original_language_id" : 6,
                    "rental_duration" : 6,
                    "rental_rate" : 0.99 ,
                    "length" : 150,
                    "replacement_cost" : 20.99 ,
                    "rating" : "PG-13" ,
                    "special_features" : "Commentaries"
	
                },
        
               {
                   "title" : "Jawan",
                    "description" : "Socio-econmic problems and corruption" ,
                    "release_year" : 2023,
                    "language_id" : 6,
                    "original_language_id" : 6,
                    "rental_duration" : 5,
                    "rental_rate" : 0.99 ,
                    "length" : 160,
                    "replacement_cost" : 20.99 ,
                    "rating" : "PG-13" ,
                    "special_features" : "Commentaries"
	
                }
            ]
        """;


        mockMvc.perform(post("/film/bulk").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .header("Content-Profile", "public")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                //.andExpect(jsonPath("$.rows", Matchers.equalTo(1)))
                .andDo(print())
                .andDo(document("pg-bulk-create-films"));

    }


    @Test
    @DisplayName("Create many films with failure.")
    void createError() throws Exception {

        var json = """ 
         [
               {
                   "title" : "Dunki",
                    "description" : "Film about illegal immigration" ,
                    "release_year" : 2023,
                    "language_id" : 6,
                    "original_language_id" : 6,
                    "rental_duration" : 6,
                    "rental_rate" : 0.99 ,
                    "length" : 150,
                    "replacement_cost" : 20.99 ,
                    "rating" : "PG-13" ,
                    "special_features" : "Commentaries"
                    "country" : "INDIA"
	
                },
        
               {
                   "title" : "Jawan",
                    "description" : "Socio-econmic problems and corruption" ,
                    "release_year" : 2023,
                    "language_id" : 6,
                    "original_language_id" : 6,
                    "rental_duration" : 5,
                    "rental_rate" : 0.99 ,
                    "length" : 160,
                    "replacement_cost" : 20.99 ,
                    "rating" : "PG-13" ,
                    "special_features" : "Commentaries"
	
                }
            ]
        """;


        mockMvc.perform(post("/film/bulk").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .header("Content-Profile", "public")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("pg-bulk-create-films-error"));

    }


    @Test
    @DisplayName("Create many directors.")
    void createDirector() throws Exception {
        var json = """
                [
                   {
                      "first_name": "Anurag",
                      "last_name": "Kashyap"
                   },
                   {
                      "first_name": "Rajkumar",
                      "last_name": "Hirani"
                   } 
                ]
                """;

        mockMvc.perform(post("/director/bulk").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .param("tsid", "director_id")
                        .param("tsidType", "number")
                        .header("Content-Profile", "public")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("pg-bulk-create-directors"));

    }

    @Test
    @DisplayName("Create many directors with wrong tsid type.")
    void createDirectorWithWrongTsidType() throws Exception {
        var json = """
                [
                   {
                      "first_name": "Anurag",
                      "last_name": "Kashyap"
                   },
                   {
                      "first_name": "Rajkumar",
                      "last_name": "Hirani"
                   } 
                ]
                """;

        mockMvc.perform(post("/director/bulk").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .param("tsid", "director_id")
                        .param("tsidType", "string")
                        .header("Content-Profile", "public")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(document("pg-bulk-create-directors-with-wrong-tsid-type"));

    }

    @Test
    @DisplayName("Create reviews with default tsid type.")
    void createReviewWithDefaultTsidType() throws Exception {
        var json = """
                [
                   {
                      "message": "Very long film.",
                      "rating": 3,
                      "film_id" : 1
                   },
                   {
                      "message": "Very exciting film.",
                      "rating": 4,
                      "film_id" : 2
                   } 
                ]
                """;

        mockMvc.perform(post("/review/bulk").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .param("tsid", "review_id")
                        .header("Content-Profile", "public")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("pg-bulk-create-reviews-with-default-tsid-type"));

    }


}
