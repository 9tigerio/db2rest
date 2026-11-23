package com.homihq.db2rest.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.homihq.db2rest.auth.data.RoleDataFilter;
import com.homihq.db2rest.auth.data.RoleFilterContext;
import com.homihq.db2rest.core.exception.GenericDataAccessException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Applies role data filters (tenant filters) to reads and writes.
 * <p>
 * A role data filter ties a role to a {@code dbId}, a table and a tenant column value. The
 * {@code dbId} and table name are matched case-insensitively everywhere, so {@code /Users} and
 * {@code /users} get the same filters.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiTenancy {

    /**
     * Combines the request filter with the tenant filters that apply to the current user.
     * <p>
     * The request filter is wrapped in parentheses before the tenant conditions are ANDed on,
     * because RSQL gives {@code ;} (AND) higher precedence than {@code ,} (OR). Without the
     * grouping, a filter such as {@code a==1,b==2} would become {@code a==1,(b==2;tenant==x)}
     * and match rows from other tenants.
     */
    public static String joinFilters(String filter, String dbId, String table,
            RoleFilterContext roleFilterContext) {
        if (roleFilterContext == null) {
            return filter;
        }

        List<String> tenantConditions = new ArrayList<>();
        for (RoleDataFilter roleDataFilter : getUserRoleBasedDataFilters(dbId, table, roleFilterContext)) {
            tenantConditions.add(roleDataFilter.value() == null
                    ? roleDataFilter.column() + "=isnull=true"
                    : roleDataFilter.column() + "==" + roleDataFilter.value());
        }

        if (tenantConditions.isEmpty()) {
            return filter;
        }

        String tenantFilter = String.join(";", tenantConditions);
        if (StringUtils.isBlank(filter)) {
            return tenantFilter;
        }

        requireBalanced(filter);
        return "(" + filter + ");" + tenantFilter;
    }

    /**
     * Makes sure the filter is a self-contained expression, so wrapping it in parentheses keeps
     * all of it inside one group. Parentheses inside quoted values are ignored.
     */
    static void requireBalanced(String filter) {
        int depth = 0;
        char quote = 0;
        for (int i = 0; i < filter.length(); i++) {
            char c = filter.charAt(i);
            if (quote != 0) {
                if (c == '\\') {
                    i++;
                } else if (c == quote) {
                    quote = 0;
                }
            } else if (c == '\'' || c == '"') {
                quote = c;
            } else if (c == '(') {
                depth++;
            } else if (c == ')' && --depth < 0) {
                break;
            }
        }
        if (depth != 0 || quote != 0) {
            throw new GenericDataAccessException("Invalid filter: unbalanced parentheses or quotes");
        }
    }

    /**
     * Returns the filters for this table that belong to the user's roles. When the table has a
     * tenant column but none of the user's roles match, returns a single filter that limits the
     * user to rows where the tenant column is null.
     */
    private static List<RoleDataFilter> getUserRoleBasedDataFilters(String dbId, String table,
            RoleFilterContext roleFilterContext) {
        List<RoleDataFilter> tableFilters = roleFilterContext.roleDataFilters().stream()
                .filter(rd -> appliesTo(rd, dbId, table))
                .toList();
        if (tableFilters.isEmpty()) {
            return List.of();
        }
        String filterTenantColumn = tableFilters.get(0).column();

        List<RoleDataFilter> retval = new ArrayList<>();
        for (String role : roleFilterContext.userRoles()) {
            retval.addAll(tableFilters.stream().filter(rd -> role.equalsIgnoreCase(rd.role())).toList());
        }
        if (retval.isEmpty()) {
            // when a tenant column is defined for the table, but the user has no roles, we add a filter to only see null value rows
            retval.add(new RoleDataFilter("see_null_values_only", dbId, table, filterTenantColumn, null));
        }
        return retval;
    }

    private static boolean appliesTo(RoleDataFilter roleDataFilter, String dbId, String table) {
        return StringUtils.equalsIgnoreCase(dbId, roleDataFilter.dbId())
                && StringUtils.equalsIgnoreCase(table, roleDataFilter.table());
    }

    public static void addTenantColumns(List<Map<String, Object>> data, String dbId, String table,
            RoleFilterContext roleFilterContext) {
        for (Map<String, Object> dataItem : data) {
            addTenantColumns(dataItem, dbId, table, roleFilterContext);
        }
    }

    public static void addTenantColumns(Map<String, Object> data, String dbId, String table,
            RoleFilterContext roleFilterContext) {
        if (roleFilterContext != null) {
            for (RoleDataFilter roleDataFilter : getUserRoleBasedDataFilters(dbId, table, roleFilterContext)) {
                data.put(roleDataFilter.column(), roleDataFilter.value());
            }
        }
    }
}
