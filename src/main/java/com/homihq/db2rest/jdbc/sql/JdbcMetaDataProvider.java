package com.homihq.db2rest.jdbc.sql;

import com.homihq.db2rest.core.config.Db2RestConfigProperties;
import com.homihq.db2rest.core.model.DbColumn;
import com.homihq.db2rest.core.model.DbTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.MetaDataAccessException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.homihq.db2rest.schema.AliasGenerator.getAlias;

@Slf4j
@RequiredArgsConstructor
public class JdbcMetaDataProvider implements DatabaseMetaDataCallback<DbMeta> {

    private final Db2RestConfigProperties db2RestConfigProperties;

    //TODO include schemas , tables , view,  filters filters
    @Override
    public DbMeta processMetaData(DatabaseMetaData databaseMetaData) throws SQLException, MetaDataAccessException {

        log.info("Preparing database meta-data - {}", databaseMetaData);
        log.debug("Properties - {}", db2RestConfigProperties.getDatasource());

        String productName = databaseMetaData.getDatabaseProductName();
        String productVersion = databaseMetaData.getDatabaseProductVersion();
        String driverName = databaseMetaData.getDriverName();
        String driverVersion = databaseMetaData.getDriverVersion();

        log.info("productName - {}", productName);
        log.info("productVersion - {}", productVersion);
        log.info("driverName - {}", driverName);
        log.info("driverVersion - {}", driverVersion);

        List<DbTable> dbTables = new ArrayList<>();

        //String schemaPattern  = StringUtils.isEmpty(db2RestConfigProperties.getDatasource().getIncludeSchemas()) ? db2RestConfigProperties.getDatasource().getIncludeSchemas() : null;
        log.debug("schemaPattern - {}", db2RestConfigProperties.getDatasource().getSchemaPattern());

        try(ResultSet resultSet = databaseMetaData.getTables(
                null,
                null,
                null,
                new String[]{"TABLE"})){
            while(resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                String catalog = resultSet.getString("TABLE_CAT");
                String schema = resultSet.getString("TABLE_SCHEM");
                String tableType = resultSet.getString("TABLE_TYPE");

                log.debug("{} , {} , {}, {} ", catalog, schema, tableName, tableType);

                String tableAlias = getAlias(tableName);

                List<DbColumn> columns =
                        getAllColumns(databaseMetaData, catalog, schema, tableName, tableAlias);

                String schemaName = StringUtils.isNotBlank(schema) ? schema : catalog;

                DbTable dbTable = new DbTable(
                        schema, tableName ,schemaName + "." + tableName,
                        tableAlias,columns);

                dbTables.add(dbTable);

            }
        }

        log.info("Completed loading database meta-data : {} tables", dbTables.size());

        return new DbMeta(productName, productVersion, driverName, driverVersion, dbTables);
    }

    private List<DbColumn> getAllColumns(DatabaseMetaData databaseMetaData, String catalog, String schema,
                               String tableName, String tableAlias) throws SQLException, MetaDataAccessException {

        List<String> pkColumns = new ArrayList<>();

        try(ResultSet primaryKeys = databaseMetaData.getPrimaryKeys(catalog, schema, tableName)){
            while(primaryKeys.next()){
                String primaryKeyColumnName = primaryKeys.getString("COLUMN_NAME");
                String primaryKeyName = primaryKeys.getString("PK_NAME");

                pkColumns.add(primaryKeyColumnName);
            }
        }

        List<DbColumn> dbColumns = new ArrayList<>();

        try(ResultSet columns = databaseMetaData
                .getColumns(catalog,schema, tableName, null)){
            while(columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String columnSize = columns.getString("COLUMN_SIZE");
                int datatype = columns.getInt("DATA_TYPE");
                String isNullable = columns.getString("IS_NULLABLE");
                String isAutoIncrement = columns.getString("IS_AUTOINCREMENT");
                String isGenerated = columns.getString("IS_GENERATEDCOLUMN");
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
