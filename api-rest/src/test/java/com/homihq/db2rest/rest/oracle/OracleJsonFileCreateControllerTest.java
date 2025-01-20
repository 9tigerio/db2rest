package com.homihq.db2rest.rest.oracle;


import com.homihq.db2rest.OracleBaseIntegrationTest;
import io.hosuaby.inject.resources.junit.jupiter.TestWithResources;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.FileSystems;
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
@Order(281)
@TestWithResources
class OracleJsonFileCreateControllerTest extends OracleBaseIntegrationTest {

    Path dir = FileSystems.getDefault().getPath("src/test/resources/testdata");

    Path actorFile = dir.resolve("actor.json");
    Path filmFile = dir.resolve("BULK_CREATE_FILM_REQUEST.json");
    Path nonArrayActorFile = dir.resolve("CREATE_ACTOR_REQUEST.json");
    Path directorFile = dir.resolve("director.json");

    @Test
    @DisplayName("Create many actors via JSON file upload.")
    void uploadActorsFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "actor.json",
                "application/json", Files.readAllBytes(actorFile));

        MvcResult mvcResult = mockMvc.perform(multipart(VERSION + "/oradb/ACTOR/upload")
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
                .andDo(document("oracle-create-actors-file-upload"));
    }

    @Test
    @DisplayName("Error create actor via JSON file upload.")
    void uploadActorFileNonJsonArray() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", nonArrayActorFile.toString(),
                "application/json", Files.readAllBytes(nonArrayActorFile));

        mockMvc.perform(multipart(VERSION + "/oradb/ACTOR/upload")
                        .file(file)
                        .contentType("multipart/form-data")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("oracle-error-create-actors-file-upload"));
    }

    @Test
    @DisplayName("Create many directors via file upload.")
    void createDirectorViaUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "bulkDirector.json",
                "application/json", Files.readAllBytes(directorFile));

        MvcResult mvcResult = mockMvc.perform(multipart(VERSION + "/oradb/DIRECTOR/upload")
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
                .andDo(document("oracle-create-directors-file-upload"));
    }

    @Test
    @DisplayName("Create many films via file upload.")
    void createFilmsViaUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "bulkFilm.json",
                "application/json", Files.readAllBytes(filmFile));

        MvcResult mvcResult = mockMvc.perform(multipart(VERSION + "/oradb/FILM/upload")
                        .file(file)
                        .queryParam("sequences", "film_id:film_sequence")
                        .contentType("multipart/form-data")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andReturn();
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.row").isNumber())
                .andExpect(jsonPath("$.row").value(2))
                .andExpect(jsonPath("$.keys").value("Bulk insert completed successfully"))
                .andDo(document("oracle-create-films-file-upload"));
    }
}
