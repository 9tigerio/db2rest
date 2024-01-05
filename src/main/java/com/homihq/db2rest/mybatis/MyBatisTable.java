package com.homihq.db2rest.mybatis;

import lombok.*;
import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class MyBatisTable extends SqlTable{

    String alias;
    String tableName;
    String schemaName;

    List<SqlColumn<?>> sqlColumnList;

    Table table;

    public MyBatisTable(String schemaName, String tableName, Table table) {
        super(tableName);
        this.tableName = tableName;
        this.schemaName = schemaName;
        this.table = table;
        sqlColumnList = new ArrayList<>();
    }

    public void addAllColumns() {
        for(Column column : table.getColumns()) addColumn(column);
    }

    public void addColumn(Column column) {
        sqlColumnList.add(column(column.getName()));
    }

    public void addColumn(String columnName, String alias) {
        sqlColumnList.add(column(columnName).as(alias));
    }
}
