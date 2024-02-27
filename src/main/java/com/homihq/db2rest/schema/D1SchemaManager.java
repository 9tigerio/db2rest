package com.homihq.db2rest.schema;

import com.homihq.db2rest.d1.D1RestClient;
import com.homihq.db2rest.dialect.Dialect;
import com.homihq.db2rest.model.DbTable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class D1SchemaManager implements SchemaManager{

    private final D1RestClient d1RestClient;

    @PostConstruct
    private void reload() {
        createSchemaCache();
    }

    private void createSchemaCache() {
        d1RestClient.getMetaData();
    }

    @Override
    public DbTable getTable(String tableName) {
        return null;
    }

    @Override
    public List<DbTable> findTables(String tableName) {
        return null;
    }

    @Override
    public DbTable getOneTable(String schemaName, String tableName) {
        return null;
    }

    @Override
    public Dialect getDialect() {
        return null;
    }
}
