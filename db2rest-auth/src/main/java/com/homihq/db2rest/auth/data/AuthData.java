package com.homihq.db2rest.auth.data;

import java.util.List;

public record AuthData(
        String name,
        List<ResourceRole> resourceRoles,
        List<ApiExcludedResource> excludedResources,
        List<User> users,
        List<ApiKey> apiKeys,
        List<RoleDataFilter> roleDataFilters
) {
    @Override
    public List<ApiExcludedResource> excludedResources() {
        return excludedResources == null ? List.of() : excludedResources;
    }

    @Override
    public List<RoleDataFilter> roleDataFilters() {
        return roleDataFilters == null ? List.of() : roleDataFilters;
    }
}
