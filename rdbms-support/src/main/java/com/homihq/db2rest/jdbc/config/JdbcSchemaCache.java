package com.homihq.db2rest.jdbc.config;

import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.config.dialect.Dialect;
import com.homihq.db2rest.core.exception.InvalidTableException;
import com.homihq.db2rest.jdbc.config.model.DbTable;

import com.homihq.db2rest.jdbc.config.sql.DbMeta;
import com.homihq.db2rest.jdbc.config.sql.JdbcMetaDataProvider;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public final class JdbcSchemaCache {

    private final DataSource dataSource;
    private final boolean includeAllSchemas;
    private final List<String> includedSchemas;
    private final List<Dialect> availableDialects;

    private Map<String,DbTable> dbTableMap;
    private DbMeta dbMeta;

    @Getter
    private Dialect dialect;


    @PostConstruct
    private void reload() {

        this.dbTableMap = new ConcurrentHashMap<>();
        loadMetaData();
    }

    public List<DbTable> getTables() {
        return
        this.dbTableMap.values()
                .stream()
                .toList();
    }

    private void loadMetaData() {
        log.info("Loading meta data");
        try {

            this.dbMeta = JdbcUtils.extractDatabaseMetaData(dataSource,
                    new JdbcMetaDataProvider(includeAllSchemas, includedSchemas));

            for (final  DbTable dbTable : dbMeta.dbTables()) {
                dbTableMap.put(dbTable.name(), dbTable);
            }

            dialect = availableDialects.stream()
                            .filter(
                                    d ->
                                    d.isSupportedDb(dbMeta.productName(), dbMeta.majorVersion())
                            ).findFirst()
                            .orElseThrow(() -> new GenericDataAccessException("Dialect not found."));

        } catch (MetaDataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public DbTable getTable(String schemaName, String tableName) {

        if(StringUtils.isNotBlank(schemaName)) return findBySchemaNameAndTableName(schemaName, tableName);

        DbTable table = this.dbTableMap.get(tableName);

        log.info("Table retrieved - {}", table);

        if(Objects.isNull(table)) throw new InvalidTableException(tableName);

        return table;
    }

    private DbTable findBySchemaNameAndTableName(String schemaName, String tableName) {
        return
        dbMeta.dbTables()
                .stream()
                .filter(dbTable ->
                        StringUtils.equalsIgnoreCase(dbTable.schema(), schemaName)
                            &&
                                StringUtils.equalsIgnoreCase(dbTable.name(), tableName)
                ).findFirst()
                .orElseThrow(()->
                        new GenericDataAccessException("Missing table - schema : " + schemaName + " , table : " + tableName));
    }


}
