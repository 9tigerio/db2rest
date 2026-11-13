package com.homihq.db2rest.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.homihq.db2rest.auth.data.RoleDataFilter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MultiTenancy {
    public static final String ROLEBASEDDATAFILTERS = "roleBasedDataFilters";

    public static String joinFilters(String filter, String dbId, String table,
            Pair<List<RoleDataFilter>, String[]> roleBasedDataFilters) {
        StringBuilder filterBuilder = new StringBuilder(filter);
        if (roleBasedDataFilters != null) {
            for (RoleDataFilter roleDataFilter : getUserRoleBasedDataFilters(dbId, table,
                    roleBasedDataFilters.getLeft(), roleBasedDataFilters.getRight())) {
                if (dbId.equalsIgnoreCase(roleDataFilter.dbId()) && table.equalsIgnoreCase(roleDataFilter.table())) {
                    if (!filterBuilder.isEmpty())
                        filterBuilder.append(";");
                    filterBuilder.append(roleDataFilter.column());
                    if (roleDataFilter.value() == null) {
                        filterBuilder.append("=isnull=");
                    }
                    else {
                        filterBuilder.append("==");
                    }
                    filterBuilder.append(roleDataFilter.value());
                }
            }
        }
        return filterBuilder.toString();
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
