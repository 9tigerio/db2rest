package com.homihq.db2rest.auth.datalookup;

import com.homihq.db2rest.auth.data.*;

import java.util.List;
import java.util.Optional;

public interface AuthDataLookup {

    List<ResourceRole> getApiResourceRoles();

    List<User> getUsers();

    List<ApiExcludedResource> getExcludedResources();

    List<ApiKey> getApiKeys();

    Optional<User> getUserByUsername(String username);

    List<RoleDataFilter> getRoleDataFilters();
}
