package com.homihq.db2rest.jdbc.multidb;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Map;

@Slf4j
@Getter
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {

        final String dbName = DatabaseContextHolder.getCurrentDbId();
        log.info("Datasource Id - {}", dbName);


        return dbName;
    }


}
