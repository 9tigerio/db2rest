package com.homihq.db2rest.rest.pg;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
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

    @Test
    @Order(7)
    @DisplayName("An OR in the user filter must not escape the tenant filter")
    void orFilterCannotReadOtherTenants() throws Exception {
        // auid 1 belongs to tenant 0, tom is tenant 15
        mockMvc.perform(get(VERSION + "/pgsqldb/users?filter=auid==1,auid==999")
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(0)));
    }

    @Test
    @Order(8)
    @DisplayName("An OR in the filter must not let a tenant count, update or delete other tenants' rows")
    void orFilterCannotCountUpdateOrDeleteOtherTenants() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/users/count?filter=auid==2,auid==999")
                .accept(APPLICATION_JSON)
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", equalTo(0)));

        mockMvc.perform(patch(VERSION + "/pgsqldb/users?filter=auid==2,auid==999")
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE)
                .content("""
                        { "password": "changed" }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(0)));

        mockMvc.perform(delete(VERSION + "/pgsqldb/users?filter=auid==2,auid==999")
                .accept(APPLICATION_JSON)
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(0)));
    }

    @Test
    @Order(9)
    @DisplayName("A filter with unbalanced parentheses is rejected for tenant users")
    void unbalancedFilterIsRejected() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/users?filter=auid==1),(auid==2")
                .accept(APPLICATION_JSON)
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(10)
    @DisplayName("A user without a tenant role sees exactly the rows whose tenant column is null")
    void userWithoutTenantRoleSeesNullTenantRows() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/tenant_notes")
                .accept(APPLICATION_JSON)
                .header("Authorization", USER_ROOT_BASIC_AUTH_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", equalTo(1)))
                .andExpect(jsonPath("$[0].tenant_id", nullValue()));

        mockMvc.perform(get(VERSION + "/pgsqldb/tenant_notes/count")
                .accept(APPLICATION_JSON)
                .header("Authorization", USER_ROOT_BASIC_AUTH_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", equalTo(1)));
    }

    @Test
    @Order(11)
    @DisplayName("A tenant user sees only their own rows, not null-tenant or other tenants' rows")
    void tenantUserSeesOnlyOwnRows() throws Exception {
        mockMvc.perform(get(VERSION + "/pgsqldb/tenant_notes")
                .accept(APPLICATION_JSON)
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", equalTo(2)))
                .andExpect(jsonPath("$[0].tenant_id", equalTo(15)));
    }

    @Test
    @Order(12)
    @DisplayName("Tenant filters apply when the table name in the URL uses a different case")
    void tenantFilterAppliesToTableNameCaseVariants() throws Exception {
        // Before the fix, the role lookup was case-sensitive: the write got a null tenant_id
        // (rejected by the NOT NULL column) and reads fell back to null-tenant rows only.
        mockMvc.perform(post(VERSION + "/pgsqldb/Users")
                .contentType(APPLICATION_JSON).accept(APPLICATION_JSON)
                .header("Content-Profile", "public")
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE)
                .content("""
                        { "auid": 30, "username": "CASE", "password": "VARIANT" }
                        """))
                .andExpect(status().isCreated());

        mockMvc.perform(get(VERSION + "/pgsqldb/Users?filter=auid==30")
                .accept(APPLICATION_JSON)
                .header("Accept-Profile", "public")
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tenant_id", equalTo(15)));

        mockMvc.perform(get(VERSION + "/pgsqldb/TENANT_NOTES")
                .accept(APPLICATION_JSON)
                .header("Accept-Profile", "public")
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", equalTo(2)));

        mockMvc.perform(delete(VERSION + "/pgsqldb/users?filter=auid==30")
                .accept(APPLICATION_JSON)
                .header("Authorization", USER_TOM_BASIC_AUTH_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rows", equalTo(1)));
    }
}
