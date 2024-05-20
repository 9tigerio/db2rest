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


    protected boolean authorizeCommon(UserDetail userDetail, String requestUri, String method,
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

            log.info("Role match - {}", roleMatch);

        }
        else{
            log.info("Failed to match resource role and/or HTTP method");
        }

        return false;
    }
}
