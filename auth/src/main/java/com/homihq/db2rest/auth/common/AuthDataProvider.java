package com.homihq.db2rest.auth.common;

import com.homihq.db2rest.auth.exception.AuthException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

public interface AuthDataProvider {

    List<ResourceRole> getApiResourceRoles();
    List<User> getUsers();
    List<ApiExcludedResource> getExcludedResources();

    Optional<User> getUserByUsername(String username);


}
