package com.homihq.db2rest.jdbc.sql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.MetaDataAccessException;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class JdbcMetaDataProvider implements DatabaseMetaDataCallback {
    @Override
    public Object processMetaData(DatabaseMetaData databaseMetaData) throws SQLException, MetaDataAccessException {

        log.info("Got database meta data - {}", databaseMetaData);

        try(ResultSet resultSet = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"})){
            while(resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                String catalog = resultSet.getString("TABLE_CAT");
                String schema = resultSet.getString("TABLE_SCHEM");
                String tableType = resultSet.getString("TABLE_TYPE");

                log.info("{} , {} , {}, {} ", catalog, schema, tableName, tableType);
            }
        }

        return null;
    }
}
