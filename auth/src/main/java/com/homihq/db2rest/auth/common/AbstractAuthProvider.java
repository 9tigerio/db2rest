package com.homihq.db2rest.auth.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractAuthProvider{

    public abstract boolean canHandle(String authHeader);

    public abstract UserDetail authenticate(String authHeader);

    public abstract boolean authorize(UserDetail userDetail, String resourceUri, String method);

    public abstract boolean isExcluded(String requestUri, String method);

    protected boolean isExcludedInternal(String requestUri, String method,  List<ApiExcludedResource> excludedResources,
                                 AntPathMatcher antPathMatcher) {

       return
                excludedResources
                        .stream()
                        .anyMatch(r -> antPathMatcher.match(r.resource(), requestUri)
                                && StringUtils.equalsIgnoreCase(r.method(), method));
    }


    protected boolean authorizeInternal(UserDetail userDetail, String requestUri, String method,
                                   List<ResourceRole> resourceRoleList, AntPathMatcher antPathMatcher) {

        //resource mapping
        Optional<ResourceRole> resourceRole =
                resourceRoleList
                        .stream()
                        .filter(r -> antPathMatcher.match(r.resource(), requestUri))
                        .findFirst();

        //resource to role mapping
        if(resourceRole.isPresent() && StringUtils.equalsIgnoreCase(method, resourceRole.get().method())) {
            ResourceRole rr = resourceRole.get();
            boolean roleMatch =
                    rr.roles()
                            .stream()
                            .anyMatch(role -> StringUtils.equalsAnyIgnoreCase(role, userDetail.getRoles()));

            log.info("Role match result - {}", roleMatch);

            return roleMatch;

        }

        log.info("Failed to match resource role and/or HTTP method");

        return false;
    }
}
