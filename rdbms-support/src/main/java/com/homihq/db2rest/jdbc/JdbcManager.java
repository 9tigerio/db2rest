package com.homihq.db2rest.jdbc;

import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.config.dialect.Dialect;
import com.homihq.db2rest.core.exception.InvalidTableException;
import com.homihq.db2rest.jdbc.config.model.DbTable;


import com.homihq.db2rest.jdbc.multidb.DbDetailHolder;
import com.homihq.db2rest.jdbc.multidb.RoutingDataSource;
import com.homihq.db2rest.jdbc.sql.DbMeta;
import com.homihq.db2rest.jdbc.sql.JdbcMetaDataProvider;
import com.homihq.db2rest.multidb.DatabaseConnectionDetail;
import com.homihq.db2rest.multidb.DatabaseProperties;
import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public final class JdbcManager {

    private final DataSource dataSource;
    private final List<Dialect> availableDialects;
    private final DatabaseProperties databaseProperties;
    private Map<String,DbTable> dbTableMap;

    private final Map<String, DbDetailHolder> dbDetailHolderMap = new ConcurrentHashMap<>();
    private final Map<String, NamedParameterJdbcTemplate> namedParameterJdbcTemplateMap = new ConcurrentHashMap<>();
    private final Map<String, JdbcTransactionManager> jdbcTransactionManagerMap = new ConcurrentHashMap<>();

    private final Map<String, TransactionTemplate> transactionTemplateMap = new ConcurrentHashMap<>();

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
        log.info("Attempting to load meta-data for all relational data-sources.");

        if(dataSource instanceof RoutingDataSource) {


            Map<Object,DataSource> dataSourceMap = ((RoutingDataSource) dataSource).getResolvedDataSources();

            if(dataSourceMap.isEmpty()) log.info("**** No datasource to load.");

            for(Object dbId : dataSourceMap.keySet()) {
                DataSource ds = dataSourceMap.get(dbId);

                DatabaseConnectionDetail databaseConnectionDetail = this.databaseProperties.getDatabase((String) dbId).get();

                log.info("Database connection details - {}", databaseConnectionDetail);

                loadMetaData((String) dbId, ds, databaseConnectionDetail);
                this.namedParameterJdbcTemplateMap.put((String) dbId, new NamedParameterJdbcTemplate(ds));

                JdbcTransactionManager jdbcTransactionManager = new JdbcTransactionManager(ds);

                this.jdbcTransactionManagerMap.put((String) dbId, jdbcTransactionManager);
                this.transactionTemplateMap.put((String) dbId, new TransactionTemplate(jdbcTransactionManager));

            }
        }
        else{
            log.info("Not routing data source. Unable to load database metadata.");
        }
    }

    private void loadMetaData(String dbId, DataSource ds, DatabaseConnectionDetail databaseConnectionDetail) {
        log.info("Loading meta data - {}", ds);
        try {
            Map<String,DbTable> dbTableMap = new ConcurrentHashMap<>();

            //TODO Get from db config
            DbMeta dbMeta = JdbcUtils.extractDatabaseMetaData(ds,
                    new JdbcMetaDataProvider(databaseConnectionDetail.includeAllSchemas(),
                            databaseConnectionDetail.schemas()));

            for (final  DbTable dbTable : dbMeta.dbTables()) {
                dbTableMap.put(dbTable.name(), dbTable);
            }

            Dialect dialect = availableDialects.stream()
                            .filter(
                                    d ->
                                    d.isSupportedDb(dbMeta.productName(), dbMeta.majorVersion())
                            ).findFirst()
                            .orElseThrow(() -> new GenericDataAccessException("Dialect not found."));

            dbDetailHolderMap.put(dbId, new DbDetailHolder(dbId, dbMeta, dbTableMap, dialect));

        } catch (MetaDataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public DbTable getTable(String dbId, String schemaName, String tableName) {

        if(StringUtils.isNotBlank(schemaName)) return getBySchemaAndTableName(dbId, schemaName, tableName);

        DbDetailHolder dbDetailHolder = this.dbDetailHolderMap.get(dbId);

        if(Objects.isNull(dbDetailHolder)) throw new GenericDataAccessException("DB not found.");

        DbTable table = dbDetailHolder.dbTableMap().get(tableName);

        log.debug("Table retrieved - {}", table);

        if(Objects.isNull(table)) throw new InvalidTableException(tableName);

        return table;
    }

    private DbTable getBySchemaAndTableName(String dbId, String schemaName, String tableName) {

        DbDetailHolder dbDetailHolder = this.dbDetailHolderMap.get(dbId);

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

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(String dbId) {
        return this.namedParameterJdbcTemplateMap.get(dbId);
    }

    public TransactionTemplate getTxnTemplate(String dbId) {
        return this.transactionTemplateMap.get(dbId);
    }

    public Dialect getDialect(String dbId) {
        DbDetailHolder dbDetailHolder = this.dbDetailHolderMap.get(dbId);

        if(Objects.isNull(dbDetailHolder)) throw new GenericDataAccessException("DB not found.");

        return dbDetailHolder.dialect();
    }
}
