package com.homihq.db2rest.auth.data;

import java.util.List;

/**
 * Row-level (tenant) filtering inputs for the current request.
 * <p>
 * {@code AuthFilter} stores one of these as a request attribute named {@link #REQUEST_ATTRIBUTE}
 * once the user is authenticated and authorized. Controllers read it and hand it to
 * {@code MultiTenancy}, which turns the filters that match the user's roles into RSQL conditions
 * for reads and into column values for writes. The attribute is absent when auth is disabled or
 * the path is excluded from auth, and no tenant filtering is applied then.
 *
 * @param roleDataFilters every role data filter configured for the deployment, for all roles
 * @param userRoles       the roles of the authenticated user
 */
public record RoleFilterContext(List<RoleDataFilter> roleDataFilters, String[] userRoles) {

    public static final String REQUEST_ATTRIBUTE = "roleFilterContext";

    public RoleFilterContext {
        roleDataFilters = roleDataFilters == null ? List.of() : List.copyOf(roleDataFilters);
        userRoles = userRoles == null ? new String[0] : userRoles.clone();
    }
}
