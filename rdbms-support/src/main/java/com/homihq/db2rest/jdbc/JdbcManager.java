package com.homihq.db2rest.jdbc;

import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.config.dialect.Dialect;
import com.homihq.db2rest.core.exception.InvalidTableException;
import com.homihq.db2rest.jdbc.config.model.DbTable;


import com.homihq.db2rest.jdbc.multidb.DbDetailHolder;
import com.homihq.db2rest.jdbc.multidb.RoutingDataSource;
import com.homihq.db2rest.jdbc.sql.DbMeta;
import com.homihq.db2rest.jdbc.sql.JdbcMetaDataProvider;
import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public final class JdbcManager {

    private final DataSource dataSource;
    private final List<Dialect> availableDialects;

    private Map<String,DbTable> dbTableMap;

    private Map<String, DbDetailHolder> dbDetailHolderMap = new ConcurrentHashMap<>();
    private Map<String, NamedParameterJdbcTemplate> namedParameterJdbcTemplateMap = new ConcurrentHashMap<>();

    @PostConstruct
    private void reload() {

        this.dbTableMap = new ConcurrentHashMap<>();
        loadAllMetaData();
    }

    public List<DbTable> getTables() {
        return
        this.dbTableMap.values()
                .stream()
                .toList();
    }

    private void loadAllMetaData() {
        log.info("Loading meta data for all datasource.");

        if(dataSource instanceof RoutingDataSource) {
            Map<Object,DataSource> dataSourceMap = ((RoutingDataSource) dataSource).getResolvedDataSources();

            for(Object dbName : dataSourceMap.keySet()) {
                DataSource ds = dataSourceMap.get(dbName);
                loadMetaData((String) dbName, ds);
                this.namedParameterJdbcTemplateMap.put((String) dbName, new NamedParameterJdbcTemplate(ds));
            }
        }
        else{
            log.info("Not routing data source. Unable to load database metadata.");
            // loadMetaData();
        }
    }

    private void loadMetaData(String dbName, DataSource ds) {
        log.info("Loading meta data - {}", ds);
        try {
            Map<String,DbTable> dbTableMap = new ConcurrentHashMap<>();

            //TODO Get from db config
            DbMeta dbMeta = JdbcUtils.extractDatabaseMetaData(ds,
                    new JdbcMetaDataProvider(true, List.of()));

            for (final  DbTable dbTable : dbMeta.dbTables()) {
                dbTableMap.put(dbTable.name(), dbTable);
            }

            Dialect dialect = availableDialects.stream()
                            .filter(
                                    d ->
                                    d.isSupportedDb(dbMeta.productName(), dbMeta.majorVersion())
                            ).findFirst()
                            .orElseThrow(() -> new GenericDataAccessException("Dialect not found."));

            dbDetailHolderMap.put(dbName, new DbDetailHolder(dbName, dbMeta, dbTableMap, dialect));

        } catch (MetaDataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public DbTable getTable(String dbName, String schemaName, String tableName) {

        if(StringUtils.isNotBlank(schemaName)) return getBySchemaAndTableName(dbName, schemaName, tableName);

        DbDetailHolder dbDetailHolder = this.dbDetailHolderMap.get(dbName);

        if(Objects.isNull(dbDetailHolder)) throw new GenericDataAccessException("DB not found.");

        DbTable table = dbDetailHolder.dbTableMap().get(tableName);

        log.info("Table retrieved - {}", table);

        if(Objects.isNull(table)) throw new InvalidTableException(tableName);

        return table;
    }

    private DbTable getBySchemaAndTableName(String dbName, String schemaName, String tableName) {

        DbDetailHolder dbDetailHolder = this.dbDetailHolderMap.get(dbName);

        if(Objects.isNull(dbDetailHolder)) throw new GenericDataAccessException("DB not found.");

        DbMeta dbMeta = dbDetailHolder.dbMeta();

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

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(String dbName) {
        return this.namedParameterJdbcTemplateMap.get(dbName);
    }

    public Dialect getDialect(String dbName) {
        DbDetailHolder dbDetailHolder = this.dbDetailHolderMap.get(dbName);

        if(Objects.isNull(dbDetailHolder)) throw new GenericDataAccessException("DB not found.");

        return dbDetailHolder.dialect();
    }
}
