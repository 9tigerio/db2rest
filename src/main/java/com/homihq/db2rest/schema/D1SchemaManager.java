package com.homihq.db2rest.schema;

import com.homihq.db2rest.dialect.Dialect;
import com.homihq.db2rest.model.DbTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(prefix = "db2rest.datasource", name = "type", havingValue = "d1")
public class D1SchemaManager implements SchemaManager{
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
