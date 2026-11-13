package com.homihq.db2rest.auth.datalookup;

import com.homihq.db2rest.auth.data.*;

import java.util.List;
import java.util.Optional;

public class NoAuthdataLookup implements AuthDataLookup {
    @Override
    public List<ResourceRole> getApiResourceRoles() {
        return List.of();
    }

    @Override
    public List<ApiExcludedResource> getExcludedResources() {
        return List.of();
    }

    @Override
    public List<ApiKey> getApiKeys() {
        return List.of();
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public List<RoleDataFilter> getRoleDataFilters() {
        return List.of();
    }

    @Override
    public List<User> getUsers() {
        return List.of();
    }
}
