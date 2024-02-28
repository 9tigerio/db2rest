package com.homihq.db2rest.d1;

import com.homihq.db2rest.d1.D1RestClient;
import com.homihq.db2rest.d1.model.D1Column;
import com.homihq.db2rest.d1.model.D1Table;
import com.homihq.db2rest.dialect.Dialect;
import com.homihq.db2rest.model.DbTable;
import com.homihq.db2rest.schema.SchemaManager;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class D1SchemaManager implements SchemaManager {

    private final D1RestClient d1RestClient;

    @PostConstruct
    private void reload() {
        createSchemaCache();
    }

    private void createSchemaCache() {
        List<D1Table> d1Tables =
        d1RestClient.getMetaDataAllTables();

        //now for each table get column info
        for(D1Table d1Table : d1Tables) {
            log.info("D1 Table - {}", d1Table);
            if(StringUtils.startsWith(d1Table.name(), "_cf")) continue;
            List<D1Column> d1Columns = d1RestClient.getMetaDataAllColumns(d1Table.name());

            log.info("D1 Columns - {}", d1Columns);
        }
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
