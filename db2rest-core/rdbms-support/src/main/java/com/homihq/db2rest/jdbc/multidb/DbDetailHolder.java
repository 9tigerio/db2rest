package com.homihq.db2rest.jdbc.multidb;

import com.db2rest.jdbc.dialect.Dialect;
import com.db2rest.jdbc.dialect.model.DbTable;
import com.homihq.db2rest.jdbc.sql.DbMeta;

import java.util.Map;

public record DbDetailHolder(
        String dbId,
        DbMeta dbMeta,
        Map<String, DbTable> dbTableMap,
        Dialect dialect
) {
}
