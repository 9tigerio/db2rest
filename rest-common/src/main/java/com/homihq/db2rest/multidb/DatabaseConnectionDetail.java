package com.homihq.db2rest.multidb;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record DatabaseConnectionDetail(String id, String type, String url, String username,
                                       String password, String database
        , List<String> catalog, List<String> schemas, List<String> tables,
                                       Map<String, String> connectionProperties,
                                       EnvironmentProperties envProperties) {
    public boolean isMongo() {
        return StringUtils.equalsIgnoreCase(type, "MONGO");
    }

    public boolean isJdbcPresent() {
        return StringUtils.isNotBlank(url);
    }

    public boolean includeAllSchemas() {
        return Objects.isNull(schemas) || schemas.isEmpty();
    }
}
