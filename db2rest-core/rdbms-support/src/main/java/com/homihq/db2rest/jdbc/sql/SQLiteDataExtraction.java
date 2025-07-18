package com.homihq.db2rest.jdbc.sql;

import com.homihq.db2rest.jdbc.config.model.Database;
import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class SQLiteDataExtraction implements MetaDataExtraction {

    List<String> excludedSchemas = Arrays.asList("information_schema", "sqlite_master", "sqlite_sequence");

    @Override
    public boolean canHandle(String database) {
        return StringUtils.equalsIgnoreCase(database, Database.SQLITE.getProductName());
    }

    @Override
    public List<DbTable> getTables(DatabaseMetaData databaseMetaData, boolean includeAllSchemas,
                                   List<String> includeSchemas) {
        try {
            List<String> includedSchemas = includeSchemas;

            if (includeAllSchemas) {
                // For SQLite, we typically work with the main database
                includedSchemas = List.of("main");
            }

            List<DbTable> dbTables = new ArrayList<>();

            for (String schema : includedSchemas) {
                log.info("Loading meta tables for schema - {}", schema);

                List<MetaDataTable> metaDataTables = getMetaTables(databaseMetaData, null, schema);

                List<DbTable> tables = metaDataTables.parallelStream()
                        .map(metaDataTable -> getDbTable(databaseMetaData, metaDataTable))
                        .toList();

                dbTables.addAll(tables);
            }

            // If no schema-specific tables found, try without schema
            if (dbTables.isEmpty()) {
                log.info("No tables found with schema, trying without schema");
                List<MetaDataTable> metaDataTables = getMetaTables(databaseMetaData, null, null);

                List<DbTable> tables = metaDataTables.parallelStream()
                        .map(metaDataTable -> getDbTable(databaseMetaData, metaDataTable))
                        .toList();

                dbTables.addAll(tables);
            }

            return dbTables;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<DbColumn> getAllColumns(DatabaseMetaData databaseMetaData, String catalog, String schema,
                                         String tableName, String tableAlias) throws SQLException {

        List<String> pkColumns = getAllPrimaryKeys(databaseMetaData, catalog, schema, tableName);

        List<DbColumn> dbColumns = new ArrayList<>();

        try (ResultSet columns = databaseMetaData.getColumns(catalog, schema, tableName, null)) {
            while (columns.next()) {
                String columnName = columns.getString(ColumnLabel.COLUMN_NAME.name());
                int datatype = columns.getInt(ColumnLabel.DATA_TYPE.name());
                String isAutoIncrement = columns.getString(ColumnLabel.IS_AUTOINCREMENT.name());
                String typeName = columns.getString(ColumnLabel.TYPE_NAME.name());

                Class<?> javaType = JdbcTypeJavaClassMappings.INSTANCE.determineJavaClassForJdbcTypeCode(datatype);

                // Handle SQLite-specific type mapping
                if (StringUtils.equalsIgnoreCase(typeName, "BOOLEAN")) {
                    javaType = Boolean.class;
                } else if (StringUtils.equalsIgnoreCase(typeName, "TEXT")) {
                    javaType = String.class;
                } else if (StringUtils.equalsIgnoreCase(typeName, "INTEGER")) {
                    javaType = Integer.class;
                } else if (StringUtils.equalsIgnoreCase(typeName, "REAL")) {
                    javaType = Double.class;
                }

                DbColumn dbColumn = new DbColumn(tableName, columnName,
                        "",
                        tableAlias,
                        pkColumns.contains(columnName),
                        typeName,
                        false,
                        StringUtils.equalsAnyIgnoreCase(isAutoIncrement, "YES"),
                        javaType, "`", ""
                );

                dbColumns.add(dbColumn);
            }
        }

        return dbColumns;
    }

    private DbTable getDbTable(DatabaseMetaData databaseMetaData, MetaDataTable metaDataTable) {
        try {
            List<DbColumn> columns = getAllColumns(databaseMetaData,
                    metaDataTable.catalog(),
                    metaDataTable.schema(),
                    metaDataTable.tableName(),
                    metaDataTable.tableAlias());

            // For SQLite, we don't use schema prefixes in table names
            String schemaName = StringUtils.isNotBlank(metaDataTable.schema()) ?
                    metaDataTable.schema() : "main";

            return new DbTable(
                    schemaName, metaDataTable.tableName(),
                    metaDataTable.tableName(), // SQLite doesn't use schema.table format
                    metaDataTable.tableAlias(),
                    columns,
                    metaDataTable.tableType(),
                    "`"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}