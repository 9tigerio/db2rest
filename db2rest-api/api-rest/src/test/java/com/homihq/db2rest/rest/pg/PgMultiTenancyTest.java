package com.homihq.db2rest.rest.pg;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.templates.TemplateFormats;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.homihq.db2rest.PostgreSQLBaseIntegrationTest;
import com.homihq.db2rest.auth.AuthFilter;

import io.hosuaby.inject.resources.junit.jupiter.GivenTextResource;
import io.hosuaby.inject.resources.junit.jupiter.TestWithResources;

@Order(200)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("it-pg-mutlitenancy")
@TestWithResources
public class PgMultiTenancyTest extends PostgreSQLBaseIntegrationTest {

    private static final String USER_TOM_BASIC_AUTH_VALUE = "Basic "
            + Base64.getEncoder().encodeToString("tom:32113".getBytes());
    private static final String USER_ROOT_BASIC_AUTH_VALUE = "Basic "
            + Base64.getEncoder().encodeToString("root:23456".getBytes());

    @Autowired
    private AuthFilter authFilter;

    @GivenTextResource("/testdata/CREATE_USER_REQUEST.json")
    String CREATE_USER_REQUEST;

    protected void createMockMvc(WebApplicationContext webApplicationContext,
            RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation).snippets()
                        .withTemplateFormat(TemplateFormats.markdown()))
                .addFilter(authFilter)
                .build();
    }

    @Test
    @Order(1)
    @DisplayName("Create users expect tenant_id to be filled")
    void create() throws Exception {
        mockMvc.perform(post(VERSION + "/pgsqldb/users/bulk")
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .content(CREATE_USER_REQUEST)
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rows").isArray())
                .andExpect(jsonPath("$.rows", hasSize(2)))
                .andDo(document("pg-create-a-user-with-tenant-id"));
    }

    @Test
    @Order(2)
    @DisplayName("Query all users for tenant_id, validate tenant_id")
    void findAllUsers() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/users")
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$[0].*", hasSize(6)))
                .andExpect(jsonPath("$[0].tenant_id", equalTo(15)))
                .andDo(document("pg-find-all-tenants"));
    }

    @Test
    @Order(3)
    @DisplayName("Query all users for tenant_id")
    void countAllUsers() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/users/count")
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE))
                // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").isArray())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.count", equalTo(2)))
                .andDo(document("pg-count-all-users-from-tenant"));
    }

    @Test
    @Order(4)
    @DisplayName("update a user, tenant_id is ignored")
    void updateUsers() throws Exception {
        mockMvc.perform(patch(VERSION + "/pgsqldb/users?filter=auid==10")
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE)
                .content("""
                                {
                                  "password": "blah",
                                  "tenant_id": 999
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.rows", equalTo(1)))
                .andDo(document("pg-count-update-users-but-tenant-is-ignored"));

        // validate tenant_id is not changed
        mockMvc.perform(get(VERSION + "/pgsqldb/users?filter=auid==10")
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE))
                // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$[0].tenant_id", not(equalTo(999))))
                .andDo(document("pg-validate-tenant-is-ignored"));
    }

    @Test
    @Order(5)
    @DisplayName("Delete all users for tenant_id")
    void deleteAllUsersFromTenant() throws Exception {
        mockMvc.perform(delete(VERSION + "/pgsqldb/users")
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE))
                // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.rows", equalTo(2)))
                .andDo(document("pg-delete-all-users-from-tenant"));
    }

    @Test
    @Order(6)
    @DisplayName("Query users without a TenantRole using roleDataFilter - MUST only return rows without any tenant_id")
    void userWithNoTenantRoleShouldSeeRowsWithoutTenantId() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/users")
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .header("Authorization", USER_ROOT_BASIC_AUTH_VALUE))
                // .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)));
    }
}
