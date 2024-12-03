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
public class OracleMetaDataExtraction implements MetaDataExtraction {


    List<String> excludedSchemas =
            Arrays.asList(
                    "ANONYMOUS", "APEX_050000", "APEX_PUBLIC_USER", "APPQOSSYS", "AUDSYS", "BI", "CTXSYS", "DBSFWUSER", "DBSNMP",
                    "DIP", "DVF", "DVSYS", "EXFSYS", "FLOWS_FILES", "GGSYS", "GSMADMIN_INTERNAL", "GSMCATUSER", "GSMUSER",
                    "HR", "IX", "LBACSYS", "MDDATA", "MDSYS", "MGMT_VIEW", "OE", "OLAPSYS", "OPS$ORACLE", "ORACLE_OCM", "ORDDATA", "RMAN",
                    "ORDPLUGINS", "ORDSYS", "OUTLN", "OWBSYS", "PDBADMIN", "PM", "RDSADMIN", "REMOTE_SCHEDULER_AGENT", "SCOTT", "SYSTEM",
                    "SH", "SI_INFORMTN_SCHEMA", "SPATIAL_CSW_ADMIN_USR", "SPATIAL_WFS_ADMIN_USR", "SYS", "SYS$UMF", "SYSBACKUP", "SYSDG",
                    "SYSKM", "SYSMAN", "SYSRAC", "\"SYSTEM\"", "TSMSYS", "WKPROXY", "WKSYS", "WK_TEST", "WMSYS", "XDB", "XS$NULL");


    List<String> excludedSchemaPatterns = Arrays.asList("APEX_[0-9]{6}", "FLOWS_[0-9]{5,6}");


    @Override
    public boolean canHandle(String database) {
        return StringUtils.equalsIgnoreCase(database, Database.ORACLE.getProductName());
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
                                .map(metaDataTable -> {
                                    try {
                                        return getDbTable(
                                                databaseMetaData,
                                                metaDataTable,
                                                schema, databaseMetaData.getDatabaseProductName(),
                                                databaseMetaData.getDatabaseMajorVersion()
                                        );
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                })
                                .toList();

                dbTables.addAll(tables);
            }


            return dbTables;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private DbTable getDbTable(DatabaseMetaData databaseMetaData, MetaDataTable metaDataTable,
                               String schema, String productName, int majorVersion) {
        try {
            List<DbColumn> columns =
                    getAllColumns(databaseMetaData, metaDataTable.catalog(), schema,
                            metaDataTable.tableName(),
                            metaDataTable.tableAlias(), productName, majorVersion);

            String schemaName = StringUtils.isNotBlank(schema) ? schema : metaDataTable.catalog();

            return new DbTable(
                    schema, metaDataTable.tableName(), schemaName + "." + metaDataTable.tableName(),
                    metaDataTable.tableAlias(), columns, metaDataTable.tableType(), "\"");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private List<DbColumn> getAllColumns(DatabaseMetaData databaseMetaData, String catalog, String schema,
                                         String tableName, String tableAlias, String productName, int majorVersion) throws SQLException {

        List<String> pkColumns = getAllPrimaryKeys(databaseMetaData, catalog, schema, tableName);

        List<DbColumn> dbColumns = new ArrayList<>();

        try (ResultSet columns = databaseMetaData
                .getColumns(catalog, schema, tableName, null)) {
            while (columns.next()) {
                String columnName = columns.getString(ColumnLabel.COLUMN_NAME.name());
                int datatype = columns.getInt(ColumnLabel.DATA_TYPE.name());
                String isAutoIncrement = columns.getString(ColumnLabel.IS_AUTOINCREMENT.name());

                //Note workaround for oracle 9i - change to strategy classes later?
                String isGenerated = "N";
                if (!(StringUtils.equalsIgnoreCase(productName, "Oracle") && majorVersion == 9)) {
                    isGenerated = columns.getString(ColumnLabel.IS_GENERATEDCOLUMN.name());
                }

                String typeName = columns.getString(ColumnLabel.TYPE_NAME.name());

                Class<?> javaType =
                        JdbcTypeJavaClassMappings.INSTANCE.determineJavaClassForJdbcTypeCode(datatype);

                DbColumn dbColumn =
                        new DbColumn(tableName, columnName,
                                "",
                                tableAlias,
                                pkColumns.contains(columnName),
                                typeName,
                                StringUtils.equalsAnyIgnoreCase(isGenerated, "YES"),
                                StringUtils.equalsAnyIgnoreCase(isAutoIncrement, "YES"),
                                javaType, "\"", ""
                        );
                dbColumns.add(dbColumn);
            }
        }

        return dbColumns;
    }
}
