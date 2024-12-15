package com.homihq.db2rest.auth.data;


import com.homihq.db2rest.auth.common.ApiExcludedResource;
import com.homihq.db2rest.auth.common.ApiKey;
import com.homihq.db2rest.auth.common.AuthDataProvider;
import com.homihq.db2rest.auth.common.AuthDataSource;
import com.homihq.db2rest.auth.common.ResourceRole;
import com.homihq.db2rest.auth.common.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
public class ApiAuthDataProvider implements AuthDataProvider {

    private final AuthDataSource authDataSource;

    public ApiAuthDataProvider(String apiEndPoint, String apiKey) {
        RestClient restClient = RestClient.builder()
                .baseUrl(apiEndPoint)
                .defaultHeader("x-api-key", apiKey)
                .build();

        authDataSource =
                restClient.get()
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(AuthDataSource.class);

        log.debug("Auth data - {}", authDataSource);
    }

    @Override
    public List<ResourceRole> getApiResourceRoles() {
        return authDataSource.resourceRoles();
    }

    @Override
    public List<ApiKey> getApiKeys() {
        return List.of();
    }

    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public List<ApiExcludedResource> getExcludedResources() {
        return List.of();
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return Optional.empty();
    }

}
