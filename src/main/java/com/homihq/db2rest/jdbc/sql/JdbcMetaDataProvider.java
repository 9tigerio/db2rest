package com.homihq.db2rest.jdbc.sql;

import com.homihq.db2rest.core.config.Db2RestConfigProperties;
import com.homihq.db2rest.core.model.DbColumn;
import com.homihq.db2rest.core.model.DbTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.homihq.db2rest.schema.AliasGenerator.getAlias;

@Slf4j
@RequiredArgsConstructor
public class JdbcMetaDataProvider implements DatabaseMetaDataCallback<DbMeta> {

    private final Db2RestConfigProperties db2RestConfigProperties;

    //TODO include schemas , tables , view,  filters filters
    @Override
    public DbMeta processMetaData(DatabaseMetaData databaseMetaData) throws SQLException {

        log.info("Preparing database meta-data - {}", databaseMetaData);

        String productName = databaseMetaData.getDatabaseProductName();
        int majorVersion = databaseMetaData.getDatabaseMajorVersion();
        String productVersion = databaseMetaData.getDatabaseProductVersion();
        String driverName = databaseMetaData.getDriverName();
        String driverVersion = databaseMetaData.getDriverVersion();

        log.info("Product - {}", productName);
        log.info("Version - {}", productVersion);
        log.info("Major Version - {}", majorVersion);
        log.info("Driver Name - {}", driverName);
        log.info("Driver Version - {}", driverVersion);


        log.info("IncludedSchemas - {}", db2RestConfigProperties.getIncludeSchemas());

        List<DbTable> dbTables = new ArrayList<>();

        if(db2RestConfigProperties.isAllSchema()) {
            log.info("Fetching all schema meta data.");
            List<DbTable> tables = getDbTables(databaseMetaData, null, productName, majorVersion);

            dbTables.addAll(tables);
        }
        else{
            log.info("Fetching meta data for selected schemas.");

            for(String schema : db2RestConfigProperties.getIncludeSchemas()) {

                log.info("Loading meta data for schema - {}", schema);

                List<DbTable> tables = getDbTables(databaseMetaData, schema, productName, majorVersion);

                dbTables.addAll(tables);
            }
        }


        log.info("Completed loading database meta-data : {} tables", dbTables.size());

        return new DbMeta(productName, majorVersion, driverName, driverVersion, dbTables);
    }

    private List<DbTable> getDbTables(DatabaseMetaData databaseMetaData, String schemaPattern, String productName, int majorVersion) throws SQLException {
        List<DbTable> dbTables = new ArrayList<>();

        try(ResultSet resultSet = databaseMetaData.getTables(
                null,
                schemaPattern,
                null,
                new String[]{"TABLE", "VIEW"})){
            while(resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                String catalog = resultSet.getString("TABLE_CAT");
                String schema = resultSet.getString("TABLE_SCHEM");
                String tableType = resultSet.getString("TABLE_TYPE");

                log.debug("{} , {} , {}, {} ", catalog, schema, tableName, tableType);

                String tableAlias = getAlias(tableName);

                List<DbColumn> columns =
                        getAllColumns(databaseMetaData, catalog, schema, tableName, tableAlias, productName, majorVersion);

                String schemaName = StringUtils.isNotBlank(schema) ? schema : catalog;

                DbTable dbTable = new DbTable(
                        schema, tableName ,schemaName + "." + tableName,
                        tableAlias,columns, tableType);

                dbTables.add(dbTable);

            }
        }
        return dbTables;
    }

    private List<DbColumn> getAllColumns(DatabaseMetaData databaseMetaData, String catalog, String schema,
                               String tableName, String tableAlias, String productName, int majorVersion) throws SQLException {

        List<String> pkColumns = new ArrayList<>();

        try(ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(catalog, schema, tableName)){
            while(primaryKeys.next()){
                String primaryKeyColumnName = primaryKeys.getString("COLUMN_NAME");
                //String primaryKeyName = primaryKeys.getString("PK_NAME");

                pkColumns.add(primaryKeyColumnName);
            }
        }

        List<DbColumn> dbColumns = new ArrayList<>();

        try(ResultSet columns = databaseMetaData
                .getColumns(catalog,schema, tableName, null)){
            while(columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                //String columnSize = columns.getString("COLUMN_SIZE");
                int datatype = columns.getInt("DATA_TYPE");
                String isNullable = columns.getString("IS_NULLABLE");
                String isAutoIncrement = columns.getString("IS_AUTOINCREMENT");

                //Note workaround for oracle 9i - change to strategy classes later?
                String isGenerated = "N";
                if(!(StringUtils.equalsIgnoreCase(productName, "Oracle") && majorVersion == 9)) {
                    isGenerated = columns.getString("IS_GENERATEDCOLUMN");
                }

                String typeName = columns.getString("TYPE_NAME");

                Class<?> javaType = JdbcTypeJavaClassMappings.INSTANCE.determineJavaClassForJdbcTypeCode(datatype);

                log.debug("{} , {} , {}, {}, {}", columnName, javaType, isNullable, isAutoIncrement, typeName);

                DbColumn dbColumn =
                new DbColumn(tableName, columnName,
                        "",
                        tableAlias,
                        pkColumns.contains(columnName),
                        typeName,
                        StringUtils.equalsAnyIgnoreCase(isGenerated,"YES"),
                        StringUtils.equalsAnyIgnoreCase(isAutoIncrement,"YES"),
                        javaType
                );

                dbColumns.add(dbColumn);
            }
        }

        return dbColumns;

    }
}
