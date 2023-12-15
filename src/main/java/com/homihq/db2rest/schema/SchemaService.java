package com.homihq.db2rest.schema;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import schemacrawler.schema.*;
import schemacrawler.schemacrawler.*;
import schemacrawler.tools.utility.SchemaCrawlerUtility;
import us.fatehi.utility.LoggingConfig;
import us.fatehi.utility.datasource.DatabaseConnectionSource;
import us.fatehi.utility.datasource.DatabaseConnectionSources;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Component
@Slf4j
@RequiredArgsConstructor
public final class SchemaService {

    private final DataSource dataSource;

    @Getter
    private List<Table> tableList;
    @PostConstruct
    public void load()  {

      log.info("Loading schema cache");

        // Set log level
        new LoggingConfig(Level.OFF);

        // Create the options
        final LimitOptionsBuilder limitOptionsBuilder =
                LimitOptionsBuilder.builder()
                        .includeTables(tableFullName -> !tableFullName.contains("_PK"));
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
        final DatabaseConnectionSource dataSource = getDataSource();
        final Catalog catalog = SchemaCrawlerUtility.getCatalog(dataSource, options);

        tableList = new ArrayList<>();

        for (final Schema schema : catalog.getSchemas()) {
            System.out.println(schema);
            for (final Table table : catalog.getTables(schema)) {
                System.out.print("o--> " + table);
                if (table instanceof View) {
                    System.out.println(" (VIEW)");
                } else {
                    System.out.println();
                }

                for (final Column column : table.getColumns()) {
                    System.out.printf("     o--> %s (%s)%n", column, column.getType());
                }

                tableList.add(table);
            }
        }
    }

    private DatabaseConnectionSource getDataSource() {
      return
      DatabaseConnectionSources.fromDataSource(dataSource);

    }
}
