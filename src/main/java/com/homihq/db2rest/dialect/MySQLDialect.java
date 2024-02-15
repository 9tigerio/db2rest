package com.homihq.db2rest.dialect;


import com.homihq.db2rest.model.DbTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.Table;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MySQLDialect implements Dialect{


    @Override
    public boolean canSupport(DatabaseInfo databaseInfo) {
        return StringUtils.equalsAnyIgnoreCase(databaseInfo.getDatabaseProductName(), "MYSQL");
    }

    @Override
    public void processTypes(DbTable table, List<String> insertableColumns, Map<String, Object> data) {

    }
}
