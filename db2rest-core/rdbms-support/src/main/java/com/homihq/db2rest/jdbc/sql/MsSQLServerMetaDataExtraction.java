package com.homihq.db2rest.jdbc.sql;

import com.db2rest.jdbc.dialect.model.Database;
import com.db2rest.jdbc.dialect.model.DbColumn;
import com.db2rest.jdbc.dialect.model.DbTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Slf4j
public class MsSQLServerMetaDataExtraction implements MetaDataExtraction {

    private static final List<String> SKIP_SCHEMAS = Arrays.asList(
            "db_accessadmin",
            "db_backupoperator",
            "db_datareader",
            "db_datawriter",
            "db_ddladmin",
            "db_denydatareader",
            "db_denydatawriter",
            "db_owner",
            "db_securityadmin",
            "guest",
            "INFORMATION_SCHEMA",
            "sys"
    );

    private static final Set<String> SKIP_TABLES = Set.of(
            "MSreplication_options",
            "spt_fallback_db",
            "spt_fallback_dev",
            "spt_fallback_usg",
            "spt_monitor",
            "spt_values"
    );

    @Override
    public boolean canHandle(String database) {
        return StringUtils.equalsIgnoreCase(database, Database.MSSQL.getProductName());
    }

    @Override
    public List<DbTable> getTables(
            DatabaseMetaData databaseMetaData,
            boolean includeAllSchemas,
            List<String> includeSchemas
    ) {
        try {
            List<String> includedSchemas = includeAllSchemas
                    ? getAllSchemas(databaseMetaData, SKIP_SCHEMAS)
                    : includeSchemas;

            List<DbTable> dbTables = new ArrayList<>();

            for (String schema : includedSchemas) {
                log.info("Loading meta tables for schema - {}", schema);

                List<MetaDataTable> metaDataTables = getMetaTables(databaseMetaData, null, schema);

                List<DbTable> tables =
                        metaDataTables
                                .stream()
                                .filter(t -> !SKIP_TABLES.contains(t.tableName()))
                                .map(metaDataTable -> getDbTable(databaseMetaData, metaDataTable))
                                .toList();

                dbTables.addAll(tables);
                log.info("Loaded tables {} for the schema {}", tables, schema);
            }

            return dbTables;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<DbColumn> getAllColumns(
            DatabaseMetaData databaseMetaData,
            MetaDataTable metaDataTable
    ) throws SQLException {
        List<String> pkColumns = getAllPrimaryKeys(
                databaseMetaData,
                metaDataTable.catalog(),
                metaDataTable.schema(),
                metaDataTable.tableName()
        );

        List<DbColumn> dbColumns = new ArrayList<>();

        try (ResultSet columns = databaseMetaData.getColumns(
                metaDataTable.catalog(),
                metaDataTable.schema(),
                metaDataTable.tableName(),
                "")
        ) {

            while (columns.next()) {
                String columnName = columns.getString(ColumnLabel.COLUMN_NAME.name());
                int datatype = columns.getInt(ColumnLabel.DATA_TYPE.name());
                String isAutoIncrement = columns.getString(ColumnLabel.IS_AUTOINCREMENT.name());
                String typeName = columns.getString(ColumnLabel.TYPE_NAME.name());

                Class<?> javaType =
                        JdbcTypeJavaClassMappings.INSTANCE.determineJavaClassForJdbcTypeCode(datatype);

                DbColumn dbColumn =
                        new DbColumn(
                                metaDataTable.tableName(),
                                columnName,
                                "",
                                metaDataTable.tableAlias(),
                                pkColumns.contains(columnName),
                                typeName,
                                false,
                                StringUtils.equalsAnyIgnoreCase(isAutoIncrement, "YES"),
                                javaType,
                                "\"",
                                ""
                        );

                dbColumns.add(dbColumn);
            }
        }

        return dbColumns;
    }

    private DbTable getDbTable(DatabaseMetaData databaseMetaData, MetaDataTable metaDataTable) {
        try {
            List<DbColumn> columns = getAllColumns(databaseMetaData, metaDataTable);

            String schemaName = StringUtils.isNotBlank(metaDataTable.schema())
                    ? metaDataTable.schema() : metaDataTable.catalog();

            return new DbTable(
                    schemaName,
                    metaDataTable.tableName(),
                    schemaName + "." + metaDataTable.tableName(),
                    metaDataTable.tableAlias(),
                    columns,
                    metaDataTable.tableType(),
                    "\""
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
