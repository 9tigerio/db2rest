package com.homihq.db2rest.jdbc.multidb;

import com.homihq.db2rest.jdbc.config.dialect.Dialect;
import com.homihq.db2rest.jdbc.config.model.DbTable;
import com.homihq.db2rest.jdbc.sql.DbMeta;

import java.util.Map;

public record DbDetailHolder (String dbName, DbMeta dbMeta, Map<String, DbTable> dbTableMap, Dialect dialect){

}
