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

@Slf4j
public class DB2DataExtraction implements MetaDataExtraction {

    List<String> excludedSchemas = Arrays.asList(
            "SYSCAT", "SYSIBM", "SYSCTLG", "SYSCLG", "SYSFUN", "SYSPROC", "SYSIBMADM",
            "SYSTOOLS", "NULLID", "SQLJ", "CDSTOOLSCHEMA", "EDTTOLS",
            "SYSTOOLSPACE", "XSYS", "SYSIBMTS", "SYSRSYNC", "DRDAAS", "SYSSTAT",
            "SCHEMA1", "SCHEMA2", "APPDEFAULTS", "INFOCATSYS", "SYSCONTEXT",
            "SYSPUBDEF", "SYSIBMINTERNAL", "SYSIBMADM", "SYSCS_DIAG", "SYSCS_UTIL",
            "SYSCS_VIEW", "SYSCL", "SYSDTL", "SYSGK", "SYSIBMTS", "SYSIBMUL",
            "SYSIBMINTERNAL", "SYSSCRT", "SYSERROR");

    @Override
    public boolean canHandle(String database) {
        return StringUtils.equalsIgnoreCase(database, Database.DB2.getProductName()) ||
               StringUtils.containsIgnoreCase(database, "DB2") ||
               StringUtils.containsIgnoreCase(database, "UDB");
    }

    @Override
    public List<DbTable> getTables(DatabaseMetaData databaseMetaData, boolean includeAllSchemas,
                                   List<String> includeSchemas) {
        try {
            List<String> includedSchemas = includeSchemas;

            if (includeAllSchemas)
                includedSchemas = getAllSchemas(databaseMetaData, excludedSchemas);

            List<DbTable> dbTables = new ArrayList<>();

            for (String schema : includedSchemas) {

                log.info("Loading meta tables for schema - {}", schema);

                List<MetaDataTable> metaDataTables = getMetaTables(databaseMetaData, null, schema);

                List<DbTable> tables =
                        metaDataTables.stream()
                                .map(metaDataTable -> getDbTable(
                                        databaseMetaData,
                                        metaDataTable

                                ))
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

                Class<?> javaType =
                        JdbcTypeJavaClassMappings.INSTANCE.determineJavaClassForJdbcTypeCode(datatype);

                DbColumn dbColumn =
                        new DbColumn(tableName, columnName,
                                "",
                                tableAlias,
                                pkColumns.contains(columnName),
                                typeName,
                                false,
                                StringUtils.equalsAnyIgnoreCase(isAutoIncrement, "YES"),
                                javaType, "\"", ""
                        );

                dbColumns.add(dbColumn);
            }
        }

        return dbColumns;

    }

    private DbTable getDbTable(DatabaseMetaData databaseMetaData, MetaDataTable metaDataTable) {
        try {
            List<DbColumn> columns = getAllColumns(databaseMetaData, metaDataTable.catalog(),
                    metaDataTable.schema(),
                    metaDataTable.tableName(),
                    metaDataTable.tableAlias());

            String schemaName = StringUtils.isNotBlank(metaDataTable.schema())
                    ? metaDataTable.schema() : metaDataTable.catalog();

            return new DbTable(
                    schemaName, metaDataTable.tableName(),
                    schemaName + "." + metaDataTable.tableName(),
                    metaDataTable.tableAlias(), columns, metaDataTable.tableType(), "\"");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
