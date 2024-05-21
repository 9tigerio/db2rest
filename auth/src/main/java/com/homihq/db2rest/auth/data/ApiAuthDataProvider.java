package com.homihq.db2rest.auth.data;


import com.homihq.db2rest.auth.common.AuthDataProvider;
import com.homihq.db2rest.auth.common.AuthDataSource;
import com.homihq.db2rest.auth.common.ResourceRole;
import com.homihq.db2rest.auth.common.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
public class ApiAuthDataProvider implements AuthDataProvider {

    private AuthDataSource authDataSource;

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

        log.info("Auth data - {}", authDataSource);
    }

    @Override
    public List<ResourceRole> getApiResourceRoles() {
        return authDataSource.resourceRoles();
    }

    @Override
    public List<User> getUsers() {
        return null;
    }
}
