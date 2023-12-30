package com.homihq.db2rest.schema;

import com.homihq.db2rest.exception.InvalidTableException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.*;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public final class SchemaManager {

  private List<Table> tables;


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
        log.info("Cache table/view info -> {}" , table);
        tableList.add(table);

      }
    }

    return tableList;

  }

  /**
   * Returns a list because different schemas can have table with same name
   * @param name
   * @return
   */

  public List<Table> getAllTablesByName(String name) {
    return
    this.tables.stream().filter(t -> StringUtils.equalsIgnoreCase(name, t.getName()))
            .toList();
  }

  public List<ForeignKey> getForeignKeysBetween(String schemaName, String rootTable, String childTable) {

    //1. first locate table in the cache
    Table table =
    this.tables.stream()
            .peek(table1 -> log.info("schema - {} , table - {}", table1.getSchema().getCatalogName(), table1.getName()))
            .filter(t ->
                    StringUtils.equalsIgnoreCase(t.getSchema().getCatalogName() , schemaName)
                    &&
                            StringUtils.equalsIgnoreCase(t.getName() , rootTable)
                    ).findFirst().orElseThrow(() -> new InvalidTableException(schemaName + "." + rootTable));


    List<ForeignKey> foreignKeys =
    table.getImportedForeignKeys().stream().filter(fk ->
            StringUtils.equalsIgnoreCase(fk.getSchema().getCatalogName() , schemaName)
            &&
                    StringUtils.equalsIgnoreCase(fk.getReferencedTable().getName() , childTable)
    ).toList();

    log.info("foreignKeys -> {}", foreignKeys);

    // if foreign keys = null, look for join table option

    return foreignKeys;
  }

}
