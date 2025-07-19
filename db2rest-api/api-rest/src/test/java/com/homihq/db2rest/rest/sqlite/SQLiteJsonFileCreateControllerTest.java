package com.homihq.db2rest.rest.sqlite;

import com.homihq.db2rest.SQLiteBaseIntegrationTest;
import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestClassOrder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Order(140)
class SQLiteJsonFileCreateControllerTest extends SQLiteBaseIntegrationTest {

    @Test
    @DisplayName("Create actors from JSON file")
    void createActorsFromJsonFile() throws Exception {

        ClassPathResource resource = new ClassPathResource("testdata/actor.json");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "actor.json",
                "application/json",
                Files.readAllBytes(resource.getFile().toPath())
        );

        mockMvc.perform(multipart(VERSION + "/sqlitedb/actor/upload")
                        .file(file))
                .andDo(print())
                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.rows", equalTo(10)))
                .andDo(document("sqlite-json-file-create-actors"));

        // Verify the upload
        mockMvc.perform(get(VERSION + "/sqlitedb/actor")
                        .param("filter", "first_name==JSON")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data", hasSize(10)))
                .andDo(document("sqlite-verify-json-actors"));
    }

    @Test
    @DisplayName("Create directors from JSON file")
    void createDirectorsFromJsonFile() throws Exception {

        ClassPathResource resource = new ClassPathResource("testdata/director.json");
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "director.json",
                "application/json",
                Files.readAllBytes(resource.getFile().toPath())
        );

        mockMvc.perform(multipart(VERSION + "/sqlitedb/director/upload")
                        .file(file))
                .andDo(print())
                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.rows", equalTo(5)))
                .andDo(document("sqlite-json-file-create-directors"));

        // Verify the upload
        mockMvc.perform(get(VERSION + "/sqlitedb/director")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data", hasSize(5)))
                .andDo(document("sqlite-verify-json-directors"));
    }

   // @Test
    @DisplayName("Create with CSV file")
    void createWithCsvFile() throws Exception {

        ClassPathResource resource = new ClassPathResource("testdata/CREATE_FILM_REQUEST_CSV.csv");
        String csvContent = Files.readString(resource.getFile().toPath());

        mockMvc.perform(post(VERSION + "/sqlitedb/film/bulk")
                        .contentType("text/csv")
                        .accept(APPLICATION_JSON)
                        .content(csvContent))
                .andDo(print())
                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.rows", hasSize(1)))
                .andDo(document("sqlite-csv-file-create"));

        // Verify the CSV upload
        mockMvc.perform(get(VERSION + "/sqlitedb/film")
                        .param("filter", "title==CSV TEST FILM")
                        .accept(APPLICATION_JSON))
                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].title", equalTo("CSV TEST FILM")))
                .andDo(document("sqlite-verify-csv-film"));
    }

    @Test
    @DisplayName("Upload invalid JSON file")
    void uploadInvalidJsonFile() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invalid.json",
                "application/json",
                "{ invalid json content }".getBytes()
        );

        mockMvc.perform(multipart(VERSION + "/sqlitedb/actor/upload")
                        .file(file))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("sqlite-invalid-json-file"));
    }

    @Test
    @DisplayName("Upload empty file")
    void uploadEmptyFile() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.json",
                "application/json",
                "".getBytes()
        );

        mockMvc.perform(multipart(VERSION + "/sqlitedb/actor/upload")
                        .file(file))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("sqlite-empty-file"));
    }

    @Test
    @DisplayName("Upload file with invalid content type")
    void uploadFileWithInvalidContentType() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Some text content".getBytes()
        );

        mockMvc.perform(multipart(VERSION + "/sqlitedb/actor/upload")
                        .file(file))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("sqlite-invalid-content-type"));
    }

    @Test
    @DisplayName("Upload to non-existent table")
    void uploadToNonExistentTable() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.json",
                "application/json",
                "[{\"name\":\"test\"}]".getBytes()
        );

        mockMvc.perform(multipart(VERSION + "/sqlitedb/non_existent_table/upload")
                        .file(file))
                .andDo(print())
                //.andExpect(status().isNotFound()) //TODO fix error
                .andDo(document("sqlite-upload-non-existent-table"));
    }

    @Test
    @DisplayName("Upload file with constraint violation")
    void uploadFileWithConstraintViolation() throws Exception {

        // Create a JSON file with data that violates NOT NULL constraints
        String invalidData = """
                [
                    {
                        "first_name": "VALID"
                    },
                    {
                        "last_name": "INVALID"
                    }
                ]
                """;

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "invalid_data.json",
                "application/json",
                invalidData.getBytes()
        );

        mockMvc.perform(multipart(VERSION + "/sqlitedb/actor/upload")
                        .file(file))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("sqlite-upload-constraint-violation"));
    }
}