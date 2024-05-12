package com.homihq.db2rest.multidb;

import org.apache.commons.lang3.StringUtils;

public record DatabaseConnectionDetail(String name, String type, String url, String username, String password, String database) {
    public boolean isMongo() {
        return StringUtils.equalsIgnoreCase(type, "MONGO");
    }
}
