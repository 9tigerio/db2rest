package com.homihq.db2rest.auth.datalookup;


import com.homihq.db2rest.auth.data.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
public class ApiAuthDataLookup implements AuthDataLookup {

    private final AuthData authData;

    public ApiAuthDataLookup(String apiEndPoint, String apiKey) {
        RestClient restClient = RestClient.builder()
                .baseUrl(apiEndPoint)
                .defaultHeader("x-api-key", apiKey)
                .build();

        authData =
                restClient.get()
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(AuthData.class);

        log.debug("Auth data - {}", authData);
    }

    @Override
    public List<ResourceRole> getApiResourceRoles() {
        return authData.resourceRoles();
    }

    @Override
    public List<ApiKey> getApiKeys() {
        return List.of();
    }

    @Override
    public List<User> getUsers() {
        return List.of();
    }

    @Override
    public List<ApiExcludedResource> getExcludedResources() {
        return List.of();
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public List<RoleDataFilter> getRoleDataFilters() {
        return authData.roleDataFilters();
    }
}
