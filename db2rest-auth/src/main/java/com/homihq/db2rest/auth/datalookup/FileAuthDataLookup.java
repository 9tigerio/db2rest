package com.homihq.db2rest.auth.datalookup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.homihq.db2rest.auth.data.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
public class FileAuthDataLookup implements AuthDataLookup {

    private AuthData authData;

    public FileAuthDataLookup(String authFileFullPath) {

        try (InputStream inputStream = new FileInputStream(authFileFullPath.replace("file:", ""))) {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

            authData = objectMapper.readValue(inputStream, AuthData.class);

            log.debug("authDataSource - {}", authData);

        } catch (Exception e) {

            log.error("Unable to load auth data: ", e);
        }
    }

    @Override
    public List<ResourceRole> getApiResourceRoles() {
        return authData.resourceRoles();
    }

    @Override
    public List<ApiKey> getApiKeys() {
        return this.authData.apiKeys();
    }

    @Override
    public List<User> getUsers() {
        return authData.users();
    }

    @Override
    public List<ApiExcludedResource> getExcludedResources() {
        return authData.excludedResources();
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return getUsers().stream()
                .filter(u -> StringUtils.equals(u.username(), username)).findFirst();
    }

    @Override
    public List<RoleDataFilter> getRoleDataFilters() {
        return authData.roleDataFilters();
    }
}
