package com.homihq.db2rest.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "db2rest")
@Validated
public class Db2RestConfigProperties {

    private boolean allowSafeDelete;

    private List<String> schemas; //TODO validate

    public String [] getSchemas() {
        String[] schemaList = new String[schemas.size()];
        schemas.toArray(schemaList);

        return schemaList;
    }

    public boolean isValidSchema(String schemaName) {
        return schemas.contains(schemaName);
    }
}
