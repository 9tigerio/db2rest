package com.homihq.db2rest.config;

import com.homihq.db2rest.exception.InvalidSchemaException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "db2rest")
@Validated
@Slf4j
public class Db2RestConfigProperties {

    private boolean allowSafeDelete;

    private List<String> schemas; //TODO validate

    private MultiTenancy multiTenancy;

    public String [] getSchemas() {
        String[] schemaList = new String[schemas.size()];
        schemas.toArray(schemaList);

        return schemaList;
    }

    public void verifySchema(String schemaName) {

        if(!multiTenancy.isEnabled() && !schemas.contains(schemaName)) {
            log.error("Invalid schema found - {}", schemaName);
            throw new InvalidSchemaException(schemaName);
        }
    }



}
