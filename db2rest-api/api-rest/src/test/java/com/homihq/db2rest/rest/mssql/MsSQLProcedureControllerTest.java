package com.homihq.db2rest.rest.mssql;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Order(501)
class MsSQLProcedureControllerTest extends MsSQLBaseIntegrationTest {

    @Test
    @DisplayName("Execute stored procedure on microsoft sql server")
    void execute() throws Exception {
        var json = """ 
                       {
                           "movieTitle": "ACADEMY DINOSAUR"
                       }
                """;

        mockMvc.perform(post(getPrefixApiUrl() + "/procedure/GetMovieRentalRateProc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", instanceOf(Map.class)))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.rentalRate", equalTo(0.99)))
                .andDo(print())
                .andDo(document(DB_NAME + "-execute-procedure"));
    }

    @Test
    @DisplayName("Execute GetActorByIdProc on MSSQL")
    void executeGetActorByIdProc() throws Exception {
        var json = """
                   {
                       "actorId": 1
                   }
            """;

        mockMvc.perform(post(getPrefixApiUrl() + "/procedure/GetActorByIdProc")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .content(json))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$['#result-set-1']", hasSize(1)))
        .andExpect(jsonPath("$['#result-set-1'][0].actor_id", equalTo(1)))
        .andExpect(jsonPath("$['#result-set-1'][0].first_name").exists())
        .andExpect(jsonPath("$['#result-set-1'][0].last_name").exists());
    }

    @Test
    @DisplayName("Execute GetActorByIdProc on MSSQL with resultSetKeys")
    void executeGetActorByIdProcWithResultSetKeys() throws Exception {
        var json = """
        {
            "actorId": 1
        }
    """;

        mockMvc.perform(post(getPrefixApiUrl() + "/procedure/GetActorByIdProc?resultSetKeys=actors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actors", hasSize(1)))
                .andExpect(jsonPath("$.actors[0].actor_id", equalTo(1)))
                .andExpect(jsonPath("$.actors[0].first_name").exists())
                .andExpect(jsonPath("$.actors[0].last_name").exists());
    }

    @Test
    @DisplayName("Execute GetActorsAndFilmsProc on MSSQL without resultSetKeys")
    void executeGetActorsAndFilmsProcWithoutResultSetKeys() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/procedure/GetActorsAndFilmsProc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$['#result-set-1']").isArray())
                .andExpect(jsonPath("$['#result-set-2']").isArray())
                .andExpect(jsonPath("$['#result-set-1'][0].actor_id").exists())
                .andExpect(jsonPath("$['#result-set-2'][0].film_id").exists());
    }

    @Test
    @DisplayName("Execute GetActorsAndFilmsProc on MSSQL with resultSetKeys")
    void executeGetActorsAndFilmsProcWithResultSetKeys() throws Exception {
        mockMvc.perform(post(getPrefixApiUrl() + "/procedure/GetActorsAndFilmsProc?resultSetKeys=actors,films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actors").isArray())
                .andExpect(jsonPath("$.films").isArray())
                .andExpect(jsonPath("$.actors[0].actor_id").exists())
                .andExpect(jsonPath("$.films[0].film_id").exists());
    }

    @Test
    @DisplayName("Execute UpdateUserProc on MSSQL")
    void updateUser() throws Exception {
        var json = """ 
                       {
                           "user_id": 2
    }                """;
        mockMvc.perform(post(getPrefixApiUrl() + "/procedure/UpdateUserProc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$['#update-count-1']", equalTo(1)));
    }
}
