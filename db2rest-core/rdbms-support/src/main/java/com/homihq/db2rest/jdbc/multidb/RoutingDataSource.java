package com.homihq.db2rest.jdbc.multidb;

import com.homihq.db2rest.multidb.DatabaseContextHolder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

@Slf4j
@Getter
public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {

        final String dbId = DatabaseContextHolder.getCurrentDbId();
        log.debug("Datasource Id - {}", dbId);


        return dbId;
    }


}
