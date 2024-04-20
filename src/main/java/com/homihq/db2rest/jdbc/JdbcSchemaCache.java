package com.homihq.db2rest.jdbc;

import com.homihq.db2rest.jdbc.dialect.*;
import com.homihq.db2rest.core.config.Db2RestConfigProperties;
import com.homihq.db2rest.core.exception.InvalidTableException;
import com.homihq.db2rest.jdbc.core.model.DbTable;

import com.homihq.db2rest.jdbc.sql.DbMeta;
import com.homihq.db2rest.jdbc.sql.JdbcMetaDataProvider;
import com.homihq.db2rest.jdbc.core.schema.SchemaCache;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;


import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public final class JdbcSchemaCache implements SchemaCache {

    private final DataSource dataSource;
    private final Db2RestConfigProperties db2RestConfigProperties;
    private Map<String,DbTable> dbTableMap;

    @Getter
    @Deprecated
    private String productName;

    @Getter
    @Deprecated
    private int productVersion;

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

            DbMeta dbMeta = JdbcUtils.extractDatabaseMetaData(dataSource, new JdbcMetaDataProvider(db2RestConfigProperties));

            for (final  DbTable dbTable : dbMeta.dbTables()) {
                dbTableMap.put(dbTable.name(), dbTable);
            }

            this.productName = dbMeta.productName();
            this.productVersion = dbMeta.majorVersion();

            dialect = DialectFactory.getDialect(this.productName, this.productVersion);



        } catch (MetaDataAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public DbTable getTable(String tableName) {

        DbTable table = this.dbTableMap.get(tableName);

        if(Objects.isNull(table)) throw new InvalidTableException(tableName);

        return table;
    }


}
