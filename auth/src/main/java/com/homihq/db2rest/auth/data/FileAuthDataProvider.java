package com.homihq.db2rest.auth.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.homihq.db2rest.auth.common.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
public class FileAuthDataProvider implements AuthDataProvider {

    private AuthDataSource authDataSource;
    public FileAuthDataProvider(String authFileFullPath) {

        try(InputStream inputStream = new FileInputStream(authFileFullPath.replace("file:",""))) {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

            authDataSource = objectMapper.readValue(inputStream, AuthDataSource.class);

            log.info("authDataSource - {}", authDataSource);


        } catch (Exception e) {

            log.error("Unable to load auth data: " , e);
        }
    }

    @Override
    public List<ResourceRole> getApiResourceRoles() {
        return authDataSource.resourceRoles();
    }

    @Override
    public List<ApiKey> getApiKeys() {
        return this.authDataSource.apiKeys();
    }

    @Override
    public List<User> getUsers() {
        return authDataSource.users();
    }

    @Override
    public List<ApiExcludedResource> getExcludedResources() {
        return authDataSource.excludedResources();
    }

    public Optional<User> getUserByUsername(String username) {
        return getUsers().stream()
                .filter(u -> StringUtils.equals(u.username(), username)).findFirst();
    }
}
