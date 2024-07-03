package com.homihq.db2rest.multidb;

import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class DatabaseProperties {


    private List<DatabaseConnectionDetail> databases;

    public Optional<DatabaseConnectionDetail> getDatabase(String dbId) {
        return
        databases.stream()
                .filter(databaseConnectionDetail -> StringUtils.equalsIgnoreCase(dbId, databaseConnectionDetail.id()))
                .findFirst();
    }
}
