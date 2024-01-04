package com.homihq.db2rest.schema;

import com.homihq.db2rest.exception.InvalidColumnException;
import com.homihq.db2rest.exception.InvalidTableException;
import com.homihq.db2rest.mybatis.DB2RTable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import schemacrawler.schema.*;
import schemacrawler.schemacrawler.*;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Component
public final class SchemaManager {

  private List<Table> tables;

  private Map<String, Table> tableMap = new ConcurrentHashMap<>();

  private Map<String, DB2RTable> db2RTableMap;

  private final DataSource dataSource;

  @PostConstruct
  public void reload() {
    tables = createSchemaCache();
  }

  public List<Table> createSchemaCache() {

    // Create the options
    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();

    final LoadOptionsBuilder loadOptionsBuilder =
            LoadOptionsBuilder.builder()

                    // Set what details are required in the schema - this affects the
                    // time taken to crawl the schema
                    .withSchemaInfoLevel(SchemaInfoLevelBuilder.standard());

    final SchemaCrawlerOptions options =
            SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
                    .withLimitOptions(limitOptionsBuilder.toOptions())
                    .withLoadOptions(loadOptionsBuilder.toOptions());

    // Get the schema definition
    final Catalog catalog =  SchemaCrawlerUtility.getCatalog(DatabaseConnectionSources.fromDataSource(dataSource),
            options);

    List<Table> tableList = new ArrayList<>();

    for (final Schema schema : catalog.getSchemas()) {

      for (final Table table : catalog.getTables(schema)) {

        tableList.add(table);

        String key = table.getSchema().getCatalogName() + "." + table.getName();

        //log.info("Key - {}", key);
        //log.info("Full name - {}", table.getFullName());

        tableMap.put(key, table);

      }
    }

    return tableList;

  }

  public Column getColumn(String schemaName, String tableName, String columnName) {
    Table table = tableMap.get(schemaName + "." + tableName);

    if(table == null) throw new InvalidTableException(tableName);

    return
    table.getColumns().stream().filter(c -> StringUtils.equalsIgnoreCase(columnName, c.getName()))
            .findFirst().orElseThrow(() -> new InvalidColumnException(tableName, columnName));
  }

  public List<ForeignKey> getForeignKeysBetween(String schemaName, String rootTable, String childTable) {

    //1. first locate table in the cache
    Table table =
    this.tables.stream()
            .filter(t ->
                    StringUtils.equalsIgnoreCase(t.getSchema().getCatalogName() , schemaName)
                    &&
                            StringUtils.equalsIgnoreCase(t.getName() , rootTable)
                    ).findFirst().orElseThrow(() -> new InvalidTableException(schemaName + "." + rootTable));


      // if foreign keys = null, look for join table option

    return table.getImportedForeignKeys().stream().filter(fk ->
            StringUtils.equalsIgnoreCase(fk.getSchema().getCatalogName() , schemaName)
            &&
                    StringUtils.equalsIgnoreCase(fk.getReferencedTable().getName() , childTable)
    ).toList();
  }

}
