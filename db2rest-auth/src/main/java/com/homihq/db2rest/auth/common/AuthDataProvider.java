package com.homihq.db2rest.auth.common;

import java.util.List;
import java.util.Optional;

public interface AuthDataProvider {

    List<ResourceRole> getApiResourceRoles();

    List<User> getUsers();

    List<ApiExcludedResource> getExcludedResources();

    List<ApiKey> getApiKeys();

    Optional<User> getUserByUsername(String username);


}
