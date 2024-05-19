package com.homihq.db2rest.auth.common;

import com.homihq.db2rest.auth.exception.AuthException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public interface AuthDataProvider {

    List<ResourceRole> getApiResourceRoles();
    List<User> getUsers();

    default User validate(String username, String password) {
        return
        getUsers().stream()
                .filter(u -> StringUtils.equals(u.username(), username)
                && StringUtils.equals(u.username(), password)).findFirst().orElseThrow(() ->
                        new AuthException("Invalid username or password."));
    }
}
