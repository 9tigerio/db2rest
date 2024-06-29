package com.homihq.db2rest.auth.common;


import java.util.List;

public record AuthDataSource(String name, List<ResourceRole> resourceRoles,
                             List<ApiExcludedResource> excludedResources, List<User> users) {

}
