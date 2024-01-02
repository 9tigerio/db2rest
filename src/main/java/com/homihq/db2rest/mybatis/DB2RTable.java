package com.homihq.db2rest.mybatis;

import org.mybatis.dynamic.sql.AliasableSqlTable;

import java.util.function.Supplier;

public class DB2RTable extends AliasableSqlTable {
    public DB2RTable(String tableName, Supplier supplier) {
        super(tableName, supplier);
    }
}
