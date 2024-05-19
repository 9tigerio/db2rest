package com.homihq.db2rest.auth.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import com.homihq.db2rest.auth.common.AuthDataProvider;
import com.homihq.db2rest.auth.common.AuthDataSource;
import com.homihq.db2rest.auth.common.ResourceRole;
import com.homihq.db2rest.auth.common.User;
import lombok.extern.slf4j.Slf4j;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class FileAuthDataProvider implements AuthDataProvider {

    private AuthDataSource authDataSource;
    public FileAuthDataProvider(String authFileFullPath) {

        try(InputStream inputStream = new FileInputStream(authFileFullPath)) {
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
    public List<User> getUsers() {
        return null;
    }
}
