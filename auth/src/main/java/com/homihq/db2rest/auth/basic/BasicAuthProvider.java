package com.homihq.db2rest.auth.basic;

import com.homihq.db2rest.auth.common.AbstractAuthProvider;
import com.homihq.db2rest.auth.common.AuthDataProvider;
import com.homihq.db2rest.auth.common.User;
import com.homihq.db2rest.auth.common.UserDetail;
import com.homihq.db2rest.auth.data.FileAuthDataProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;


@RequiredArgsConstructor
@Slf4j
public class BasicAuthProvider extends AbstractAuthProvider {

    private static final String BASIC_AUTH = "Basic";

    private final AuthDataProvider authDataProvider;
    private final AntPathMatcher antPathMatcher;

    @Override
    public boolean canHandle(HttpServletRequest request) {
        String authHeader = this.getAuthHeader(request);
        return StringUtils.isNotBlank(authHeader) && authHeader.startsWith(BASIC_AUTH);
    }

    @Override
    public UserDetail authenticate(HttpServletRequest request) {
        String authHeader = this.getAuthHeader(request);
        String base64Credentials = authHeader.substring(String.format("%s ", BASIC_AUTH).length());
        byte[] decodedCredentials = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decodedCredentials, StandardCharsets.UTF_8);

        String[] parts = credentials.split(":");
        String username = parts[0];
        String password = parts[1];

        if(authDataProvider instanceof FileAuthDataProvider fileAuthDataProvider) {
            User user = fileAuthDataProvider.getUserByUsername(username).orElse(null);
            if(user != null && StringUtils.equals(password, user.password())) {
                return new UserDetail(username, user.roles());
            }
        }

        return null;
    }

    @Override
    public boolean authorize(UserDetail userDetail, String requestUri, String method) {

        return this.authorizeInternal(userDetail, requestUri, method, authDataProvider.getApiResourceRoles(), antPathMatcher);
    }

    @Override
    public boolean isExcluded(String requestUri, String method) {
        return super.isExcludedInternal(requestUri, method, authDataProvider.getExcludedResources(), antPathMatcher);
    }
}
