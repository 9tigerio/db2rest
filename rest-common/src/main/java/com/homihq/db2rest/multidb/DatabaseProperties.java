package com.homihq.db2rest.multidb;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Data
@Component
@ConfigurationProperties(prefix = "app")
@Slf4j
public class DatabaseProperties {


    private List<DatabaseConnectionDetail> databases;

    public Optional<DatabaseConnectionDetail> getDatabase(String dbId) {
        return
        databases.stream()
                .filter(databaseConnectionDetail -> StringUtils.equalsIgnoreCase(dbId, databaseConnectionDetail.id()))
                .findFirst();
    }

    public boolean isRdbmsConfigured() {

        if(Objects.isNull(databases)) {
            log.info("No database configuration found");
            return false;
        }

        log.info("Database configuration found.");

        boolean jdbcUrlFound = databases.stream()
                .anyMatch(DatabaseConnectionDetail::isJdbcPresent);

        log.info("JDBC Url found : {}" ,  jdbcUrlFound);

        return jdbcUrlFound;
    }
}
