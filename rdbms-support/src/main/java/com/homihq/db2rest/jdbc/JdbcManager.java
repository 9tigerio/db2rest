package com.homihq.db2rest.jdbc;

import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.core.exception.InvalidTableException;
import com.homihq.db2rest.jdbc.config.dialect.Dialect;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public final class JdbcManager {

    private static final String DB_NOT_FOUND_ERROR = "DB not found.";

    private final DataSource dataSource;
    private final List<Dialect> availableDialects;
    private final DatabaseProperties databaseProperties;

    private final Map<String, DbDetailHolder> dbDetailHolderMap = new ConcurrentHashMap<>();
    private final Map<String, NamedParameterJdbcTemplate> namedParameterJdbcTemplateMap =
            new ConcurrentHashMap<>();
    private final Map<String, JdbcTransactionManager> jdbcTransactionManagerMap =
            new ConcurrentHashMap<>();

    private final Map<String, TransactionTemplate> transactionTemplateMap =
            new ConcurrentHashMap<>();

    @PostConstruct
    private void reload() {

        loadAllMetaData();

    }

    public DbMeta getDbMetaByDbId(String dbId) {
        Map<String, DbMeta> dbMetaMap =
                getDbMetaMap();

        return dbMetaMap.get(dbId);

    }

    public Map<String, DbMeta> getDbMetaMap() {
        Map<String, DbMeta> dbMetaMap = new HashMap<>();

        dbDetailHolderMap.forEach((k, v) -> dbMetaMap.put(k, v.dbMeta()));

        return dbMetaMap;
    }

    public List<DbTable> getTables() {
        return List.of();

    }

    private void loadAllMetaData() {
        log.info("Attempting to load meta-data for all relational data-sources.");

        if (dataSource instanceof RoutingDataSource routingDataSource) {
            Map<Object, DataSource> dataSourceMap = routingDataSource.getResolvedDataSources();

            if (dataSourceMap.isEmpty()) {
                log.info("**** No datasource to load.");
            }

            for (Map.Entry<Object, DataSource> dataSourceEntry : dataSourceMap.entrySet()) {
                DataSource ds = dataSourceEntry.getValue();
                String dbId = (String) dataSourceEntry.getKey();

                DatabaseConnectionDetail databaseConnectionDetail = null;
                Optional<DatabaseConnectionDetail> connectionDetail = this.databaseProperties
                        .getDatabase(dbId);

                if (connectionDetail.isPresent()) {
                    databaseConnectionDetail = connectionDetail.get();
                }

                log.debug("Database connection details - {}", databaseConnectionDetail);

                loadMetaData(dbId, ds, databaseConnectionDetail);

                this.namedParameterJdbcTemplateMap.put(dbId, new NamedParameterJdbcTemplate(ds));

                JdbcTransactionManager jdbcTransactionManager = new JdbcTransactionManager(ds);

                this.jdbcTransactionManagerMap.put(dbId, jdbcTransactionManager);
                this.transactionTemplateMap.put(dbId, new TransactionTemplate(jdbcTransactionManager));
            }
        } else {
            log.info("Not routing data source. Unable to load database metadata.");
        }
    }

    private void loadMetaData(String dbId, DataSource ds, DatabaseConnectionDetail databaseConnectionDetail) {
        log.debug("Loading meta data - {}", ds);
        try {
            Map<String, DbTable> dbTableMap = new ConcurrentHashMap<>();

            // assume database connection detail as null
            boolean includeAllSchemas = true;
            List<String> schemas = null;

            if (Objects.nonNull(databaseConnectionDetail)) {
                includeAllSchemas = databaseConnectionDetail.includeAllSchemas();
                schemas = databaseConnectionDetail.schemas();

                log.info("Include all schemas - {}", includeAllSchemas);
                log.info("Schemas - {}", schemas);
            }

            //TODO Get from db config
            DbMeta dbMeta = JdbcUtils.extractDatabaseMetaData(ds,
                    new JdbcMetaDataProvider(includeAllSchemas, schemas));

            for (final DbTable dbTable : dbMeta.dbTables()) {
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

        if (StringUtils.isNotBlank(schemaName)) {
            return getBySchemaAndTableName(dbId, schemaName, tableName);
        }

        DbDetailHolder dbDetailHolder = this.dbDetailHolderMap.get(dbId);

        if (Objects.isNull(dbDetailHolder)) {
            throw new GenericDataAccessException(DB_NOT_FOUND_ERROR);
        }

        DbTable table = dbDetailHolder.dbTableMap().get(tableName);

        log.debug("Table retrieved - {}", table);

        if (Objects.isNull(table)) {
            throw new InvalidTableException(tableName);
        }

        return table;
    }

    private DbTable getBySchemaAndTableName(String dbId, String schemaName, String tableName) {

        DbDetailHolder dbDetailHolder = this.dbDetailHolderMap.get(dbId);

        if (Objects.isNull(dbDetailHolder)) {
            throw new GenericDataAccessException(DB_NOT_FOUND_ERROR);
        }

        DbMeta dbMeta = dbDetailHolder.dbMeta();

        return
                dbMeta.dbTables()
                        .stream()
                        .filter(dbTable ->
                                StringUtils.equalsIgnoreCase(dbTable.schema(), schemaName)
                                        &&
                                        StringUtils.equalsIgnoreCase(dbTable.name(), tableName)
                        ).findFirst()
                        .orElseThrow(() ->
                                new GenericDataAccessException(
                                        "Missing table - schema : " + schemaName + " , table : "
                                                + tableName));
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(String dbId) {
        return this.namedParameterJdbcTemplateMap.get(dbId);
    }

    public TransactionTemplate getTxnTemplate(String dbId) {
        return this.transactionTemplateMap.get(dbId);
    }

    public Dialect getDialect(String dbId) {
        DbDetailHolder dbDetailHolder = this.dbDetailHolderMap.get(dbId);

        if (Objects.isNull(dbDetailHolder)) {
            throw new GenericDataAccessException(DB_NOT_FOUND_ERROR);
        }

        return dbDetailHolder.dialect();
    }
}
