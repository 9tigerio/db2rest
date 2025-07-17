package com.homihq.db2rest.auth.apikey;

import com.homihq.db2rest.auth.common.AbstractAuthProvider;
import com.homihq.db2rest.auth.common.ApiKey;
import com.homihq.db2rest.auth.common.AuthDataProvider;
import com.homihq.db2rest.auth.common.UserDetail;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

@RequiredArgsConstructor
public class ApiKeyAuthProvider extends AbstractAuthProvider {

    private static final String API_KEY_HEADER = "X-API-KEY";

    private final AuthDataProvider authDataProvider;
    private final AntPathMatcher antPathMatcher;

    @Override
    public boolean canHandle(HttpServletRequest request) {
        String apiKey = request.getHeader(API_KEY_HEADER);
        return StringUtils.isNotBlank(apiKey);
    }

    @Override
    public UserDetail authenticate(HttpServletRequest request) {
        String apiKey = request.getHeader(API_KEY_HEADER);
        return authDataProvider.getApiKeys()
                .stream()
                .filter(a -> a.key().equals(apiKey))
                .filter(ApiKey::active)
                .map(a -> new UserDetail(a.key(), a.roles()))
                .findFirst()
                .orElse(null);
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
