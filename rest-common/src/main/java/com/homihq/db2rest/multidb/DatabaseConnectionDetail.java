package com.homihq.db2rest.multidb;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public record DatabaseConnectionDetail(String name, String type, String url, String username, String password, String database
                                        ,List<String> catalog, List<String>  schema, List<String>  tables,
    Map<String,String> connectionProperties, EnvironmentProperties envProperties) {
    public boolean isMongo() {
        return StringUtils.equalsIgnoreCase(type, "MONGO");
    }

    public boolean isJdbcPresent() {
        return StringUtils.isNoneBlank(url);
    }
}
