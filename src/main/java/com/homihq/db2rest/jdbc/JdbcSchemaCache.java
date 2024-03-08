package com.homihq.db2rest.jdbc;

import com.homihq.db2rest.exception.InvalidTableException;
import com.homihq.db2rest.core.model.DbColumn;
import com.homihq.db2rest.core.model.DbTable;
import com.homihq.db2rest.schema.AliasGenerator;
import com.homihq.db2rest.schema.SchemaCache;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import schemacrawler.schema.*;
import schemacrawler.schemacrawler.*;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

import javax.sql.DataSource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public final class JdbcSchemaCache implements SchemaCache {

    private final DataSource dataSource;
    private final AliasGenerator aliasGenerator;

    private Map<String,DbTable> dbTableMap;

    @PostConstruct
    private void reload() {

        this.dbTableMap = new ConcurrentHashMap<>();
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

        for (final Schema schema : catalog.getSchemas()) {

            for (final Table table : catalog.getTables(schema)) {
                log.debug("{}", table.getFullName());

                DbTable dbTable = createTable(table);
                dbTableMap.put(dbTable.name(), dbTable);
            }
        }

    }

    private DbTable createTable(Table table) {
        String tableAlias = aliasGenerator.getAlias(table.getName());
        List<DbColumn> columnList = buildColumns(tableAlias, table);

        return new DbTable(
                getSchemaName(table), table.getName(),table.getFullName(),
                tableAlias,columnList);
    }

    private DbColumn createDbColumn(String tableName, String tableAlias, Column column) {

        return new DbColumn(tableName, column.getName(),
                "", tableAlias, column.isPartOfPrimaryKey(),
                column.getColumnDataType().getName(),
                column.isGenerated(), column.isAutoIncremented()
                ,column.getType().getTypeMappedClass()
        );

    }

    private List<DbColumn> buildColumns(String tableAlias, Table table) {
        return table.getColumns()
                .stream()
                .map(column -> createDbColumn(table.getName(), tableAlias, column))
                .toList();
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

        DbTable table = this.dbTableMap.get(tableName);

        if(Objects.isNull(table)) throw new InvalidTableException(tableName);

        return table;
    }


}
