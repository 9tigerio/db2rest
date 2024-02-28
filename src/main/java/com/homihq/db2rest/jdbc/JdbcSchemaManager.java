package com.homihq.db2rest.jdbc;

import com.homihq.db2rest.dialect.Dialect;
import com.homihq.db2rest.exception.InvalidTableException;
import com.homihq.db2rest.model.DbColumn;
import com.homihq.db2rest.model.DbTable;
import com.homihq.db2rest.schema.AliasGenerator;
import com.homihq.db2rest.schema.SchemaManager;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import schemacrawler.schema.*;
import schemacrawler.schemacrawler.*;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public final class JdbcSchemaManager implements SchemaManager {

    private final DataSource dataSource;
    private final AliasGenerator aliasGenerator;
    private final List<Dialect> dialects;

    private Map<String, Table> tableMap;
    private List<Table> tableList;

    private List<DbTable> dbTableList;

    @Getter
    private Dialect dialect;

    @PostConstruct
    private void reload() {
        this.tableMap = new ConcurrentHashMap<>();
        this.tableList = new ArrayList<>();
        this.dbTableList = new ArrayList<>();
        createSchemaCache();
    }

    private void createSchemaCache() {

        // Create the options
        final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder().tableTypes("TABLE","VIEW","MATERIALIZED VIEW");

        final LoadOptionsBuilder loadOptionsBuilder = LoadOptionsBuilder.builder()
                // Set what details are required in the schema - this affects the
                // time taken to crawl the schema
                .withSchemaInfoLevel(SchemaInfoLevelBuilder.standard());

        final SchemaCrawlerOptions options = SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
                .withLimitOptions(limitOptionsBuilder.toOptions()).withLoadOptions(loadOptionsBuilder.toOptions());

        // Get the schema definition

        final Catalog catalog = SchemaCrawlerUtility.getCatalog(DatabaseConnectionSources.fromDataSource(dataSource), options);

        DatabaseInfo databaseInfo = catalog.getDatabaseInfo();

        log.info("Database - {}", databaseInfo);

        for(Dialect dialect : dialects) {

            if(dialect.canSupport(databaseInfo)) {
                this.dialect = dialect;
                break;
            }
        }

        for (final Schema schema : catalog.getSchemas()) {

            for (final Table table : catalog.getTables(schema)) {
                log.debug("{}", table.getFullName());
                String schemaName = getSchemaName(table);

                String fullName = schemaName + "." + table.getName();

                tableMap.put(fullName, table);
                tableList.add(table);

                dbTableList.add(createTable(table));

            }
        }
    }

    private DbColumn createDbColumn(String tableName, String tableAlias, Column column) {

        return new DbColumn(tableName, column.getName(),
                "", tableAlias, column.isPartOfPrimaryKey(),
                column.getColumnDataType().getName(),
                column.isGenerated(), column.isAutoIncremented()
                ,column.getType().getTypeMappedClass()
        );

    }

    public List<DbColumn> buildColumns(String tableAlias, Table table) {
        return table.getColumns()
                .stream()
                .map(column -> createDbColumn(table.getName(), tableAlias, column))
                .toList();
    }

    private DbTable createTable(Table table) {
        String tableAlias = aliasGenerator.getAlias("", 4, table.getName());
        List<DbColumn> columnList = buildColumns(tableAlias, table);

        return new DbTable(
                getSchemaName(table), table.getName(),table.getFullName(),
                tableAlias,columnList);
    }

    private String getSchemaName(Table table) {
        //TODO - move DB specific handling to Dialect class
        String schemaName = table.getSchema().getCatalogName();

        if(StringUtils.isBlank(schemaName)) {
            //POSTGRESQL

            schemaName = table.getSchema().getName();
        }
        log.debug("schemaName -> {}", schemaName);
        return schemaName;
    }


    @Override
    public DbTable getTable(String tableName) {
        List<DbTable> tables = findTables(tableName);

        log.debug("tables - {}", tables);

        if(tables.size() != 1) throw new InvalidTableException(tableName);

        return tables.get(0);
    }

    @Override
    public List<DbTable> findTables(String tableName) {
        return tableList.stream()
                .filter(t -> StringUtils.equalsIgnoreCase(t.getName(), tableName))
                .toList()
                .stream()
                .map(this::createTable)
                .toList();

    }

    private Optional<Table> getTable(String schemaName, String tableName) {
        Table table = tableMap.get(schemaName + "." + tableName);
        return Optional.of(table);
    }

    @Override
    public DbTable getOneTable(String schemaName, String tableName) {
        Table table = getTable(schemaName, tableName).orElseThrow(() -> new InvalidTableException(tableName));

        return createTable(table);

    }


}
