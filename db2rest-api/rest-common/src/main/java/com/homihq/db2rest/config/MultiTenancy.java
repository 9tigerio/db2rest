package com.homihq.db2rest.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.homihq.db2rest.auth.data.RoleDataFilter;
import com.homihq.db2rest.core.exception.GenericDataAccessException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiTenancy {
    public static final String ROLEBASEDDATAFILTERS = "roleBasedDataFilters";

    /**
     * Combines the request filter with the tenant filters that apply to the current user.
     * <p>
     * The request filter is wrapped in parentheses before the tenant conditions are ANDed on,
     * because RSQL gives {@code ;} (AND) higher precedence than {@code ,} (OR). Without the
     * grouping, a filter such as {@code a==1,b==2} would become {@code a==1,(b==2;tenant==x)}
     * and match rows from other tenants.
     */
    public static String joinFilters(String filter, String dbId, String table,
            Pair<List<RoleDataFilter>, String[]> roleBasedDataFilters) {
        if (roleBasedDataFilters == null) {
            return filter;
        }

        List<String> tenantConditions = new ArrayList<>();
        for (RoleDataFilter roleDataFilter : getUserRoleBasedDataFilters(dbId, table,
                roleBasedDataFilters.getLeft(), roleBasedDataFilters.getRight())) {
            if (dbId.equalsIgnoreCase(roleDataFilter.dbId()) && table.equalsIgnoreCase(roleDataFilter.table())) {
                tenantConditions.add(roleDataFilter.value() == null
                        ? roleDataFilter.column() + "=isnull=" + roleDataFilter.value()
                        : roleDataFilter.column() + "==" + roleDataFilter.value());
            }
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

    private static List<RoleDataFilter> getUserRoleBasedDataFilters(String dbId, String table,
            List<RoleDataFilter> roleDataFilters,
            String[] userRoles) {
        String filterTenantColumn = null;
        for (RoleDataFilter roleDataFilter : roleDataFilters) {
            if (dbId.equalsIgnoreCase(roleDataFilter.dbId()) && table.equalsIgnoreCase(roleDataFilter.table())) {
                filterTenantColumn = roleDataFilter.column();
                break;
            }
        }
        List<RoleDataFilter> retval = new ArrayList<>();
        for (String role : userRoles) {
            retval.addAll(roleDataFilters.stream().filter(rd -> role.equalsIgnoreCase(rd.role())
                    && rd.dbId().equals(dbId) && rd.table().equals(table)).toList());
        }
        if (retval.isEmpty() && filterTenantColumn != null) {
            // when a tenant column is defined for the table, but the user has no roles, we add a filter to only see null value rows
            retval.add(new RoleDataFilter("see_null_values_only", dbId, table, filterTenantColumn, null));
        }
        return retval;
    }

    public static void addTenantColumns(List<Map<String, Object>> data, String dbId, String table,
            Pair<List<RoleDataFilter>, String[]> roleBasedDataFilters) {
        for (Map<String, Object> dataItem : data) {
            addTenantColumns(dataItem, dbId, table, roleBasedDataFilters);
        }
    }

    public static void addTenantColumns(Map<String, Object> data, String dbId, String table,
            Pair<List<RoleDataFilter>, String[]> roleBasedDataFilters) {
        if (roleBasedDataFilters != null) {
            for (RoleDataFilter roleDataFilter : getUserRoleBasedDataFilters(dbId, table,
                    roleBasedDataFilters.getLeft(), roleBasedDataFilters.getRight())) {
                data.put(roleDataFilter.column(), roleDataFilter.value());
            }
        }
    }
}
