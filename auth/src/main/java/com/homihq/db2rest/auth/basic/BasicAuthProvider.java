package com.homihq.db2rest.auth.basic;

import com.homihq.db2rest.auth.common.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


@RequiredArgsConstructor
@Slf4j
public class BasicAuthProvider extends AbstractAuthProvider {

    private final AuthDataProvider authDataProvider;
    private final AntPathMatcher antPathMatcher;
    @Override
    public boolean canHandle(String authHeader) {
        return StringUtils.isNotBlank(authHeader) && authHeader.startsWith("Basic ");
    }

    @Override
    public UserDetail authenticate(String authHeader) {
        String base64Credentials = authHeader.substring("Basic ".length());
        byte[] decodedCredentials = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decodedCredentials, StandardCharsets.UTF_8);

        String[] parts = credentials.split(":");
        String username = parts[0];
        String password = parts[1];

        User user = authDataProvider.validate(username, password);

        return new UserDetail(username, user.roles());
    }

    @Override
    public boolean authorize(UserDetail userDetail, String requestUri, String method) {
        return false;
    }

    @Override
    public boolean isExcluded(String requestUri, String method) {
        return super.isExcludedInternal(requestUri, method, authDataProvider.getExcludedResources(), antPathMatcher);
    }
}
