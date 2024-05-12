package com.homihq.db2rest.multidb;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public record DatabaseConnectionDetail(String name, String type, String url, String username, String password, String database
    ,Map<String,String> connectionProperties, EnvironmentProperties envProperties) {
    public boolean isMongo() {
        return StringUtils.equalsIgnoreCase(type, "MONGO");
    }
}
