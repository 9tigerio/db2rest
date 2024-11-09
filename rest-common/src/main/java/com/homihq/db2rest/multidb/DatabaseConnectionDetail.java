package com.homihq.db2rest.multidb;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public record DatabaseConnectionDetail(String id, String type, String url, String username, String password, String database
                                        ,List<String> catalog, List<String>  schemas, List<String>  tables,
    Map<String,String> connectionProperties, EnvironmentProperties envProperties) {
    public boolean isMongo() {
        return StringUtils.equalsIgnoreCase(type, "MONGO");
    }

    public boolean isJdbcPresent() {

        System.out.println("StringUtils.isNotBlank(url) -- " + StringUtils.isNotBlank(url));
        System.out.println("!StringUtils.equalsIgnoreCase -- {}" + !StringUtils.equalsIgnoreCase(url, "${DB_URL}"));

        return StringUtils.isNotBlank(url)
                && !StringUtils.equalsIgnoreCase(url, "${DB_URL}");
    }

    public boolean includeAllSchemas() {
        return Objects.isNull(schemas) || schemas.isEmpty();
    }
}
