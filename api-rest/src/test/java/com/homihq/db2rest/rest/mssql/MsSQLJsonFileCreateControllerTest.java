package com.homihq.db2rest.rest.mssql;

import com.adelean.inject.resources.junit.jupiter.TestWithResources;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(503)
@TestWithResources
class MsSQLJsonFileCreateControllerTest extends MsSQLBaseIntegrationTest {

    String actorFile = getClass().getResource("/testdata/actor.json").getPath();
    String filmFile = getClass().getResource("/testdata/BULK_CREATE_FILM_REQUEST.json").getPath();
    String nonArrayActorFile = getClass().getResource("/testdata/CREATE_ACTOR_REQUEST.json").getPath();
    String directorFile = getClass().getResource("/testdata/director.json").getPath();

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Create many actors via JSON file upload.")
    void uploadActorsFile() throws Exception {
        jdbcTemplate.execute("SET IDENTITY_INSERT dbo.actor ON");

        MockMultipartFile file = new MockMultipartFile("file", "actor.json",
                "application/json", Files.readAllBytes(Path.of(actorFile)));

        MvcResult mvcResult = mockMvc.perform(multipart(VERSION + "/mssql/actor/upload")
                        .file(file)
                        .contentType("multipart/form-data")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row").isNumber())
                .andExpect(jsonPath("$.row").value(10000))
                .andExpect(jsonPath("$.keys").value("Bulk insert completed successfully"))
                .andDo(document("mssql-create-actors-file-upload"));

        jdbcTemplate.execute("SET IDENTITY_INSERT dbo.actor OFF");
    }

    @Test
    @DisplayName("Error create actor via JSON file upload.")
    void uploadActorFileNonJsonArray() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", nonArrayActorFile.toString(),
                "application/json", Files.readAllBytes(Path.of(nonArrayActorFile)));

         mockMvc.perform(multipart(VERSION + "/mssql/actor/upload")
                        .file(file)
                        .contentType("multipart/form-data")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("mssql-error-create-actors-file-upload"));
    }

    @Test
    @DisplayName("Create many directors via file upload.")
    void createDirectorViaUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "bulkDirector.json",
                "application/json", Files.readAllBytes(Path.of(directorFile)));

        MvcResult mvcResult = mockMvc.perform(multipart(VERSION + "/mssql/director/upload")
                        .file(file)
                        .contentType("multipart/form-data")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row").isNumber())
                .andExpect(jsonPath("$.row").value(600))
                .andExpect(jsonPath("$.keys").value("Bulk insert completed successfully"))
                .andDo(document("mssql-create-directors-file-upload"));
    }

    @Test
    @DisplayName("Create many films via file upload.")
    void createFilmsViaUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "bulkFilm.json",
                "application/json", Files.readAllBytes(Path.of(filmFile)));

        MvcResult mvcResult = mockMvc.perform(multipart(VERSION + "/mssql/film/upload")
                        .file(file)
                        .contentType("multipart/form-data")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row").isNumber())
                .andExpect(jsonPath("$.row").value(2))
                .andExpect(jsonPath("$.keys").value("Bulk insert completed successfully"))
                .andDo(document("mssql-create-films-file-upload"));
    }
}
