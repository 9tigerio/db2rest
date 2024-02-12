package com.homihq.db2rest.rest.read.processor.pre;


import com.homihq.db2rest.rest.read.dto.JoinDetail;
import com.homihq.db2rest.rest.read.dto.ReadContextV2;
import com.homihq.db2rest.rest.read.model.DbColumn;
import com.homihq.db2rest.rest.read.model.DbTable;
import com.homihq.db2rest.schema.SchemaManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.homihq.db2rest.schema.TypeMapperUtil.getJdbcType;


@Component
@Slf4j
@Order(6)
@RequiredArgsConstructor
public class JoinTableFieldPreProcessor implements ReadPreProcessor {

    private final SchemaManager schemaManager;

    @Override
    public void process(ReadContextV2 readContextV2) {
        List<JoinDetail> joins = readContextV2.getJoins();

        if(Objects.isNull(joins) || joins.isEmpty()) return;

        for(JoinDetail joinDetail : joins) {
            String tableName = joinDetail.table();

            DbTable table = schemaManager.getTableV2(tableName);

            List<DbColumn> columnList = addColumns(table, joinDetail.fields());

           readContextV2.addColumns(columnList);
        }
    }

    private DbColumn createColumn(String columnName, DbTable table) {
        Column column = table.lookupColumn(columnName);

        return new DbColumn(table.name(), columnName, getJdbcType(column) , column, "", table.alias());
    }

    private List<DbColumn> addColumns(DbTable table, List<String> fields) {

        //There are 2 possibilities
        // - field can be *
        // - can be set of fields from the given table

        log.info("Fields - {}", fields);

        List<DbColumn> columnList = new ArrayList<>();

        if(Objects.isNull(fields)) {

            //include all fields of root table
            List<DbColumn> columns =
                    table.table()
                    .getColumns()
                            .stream()
                            .map(column -> createColumn(column.getName(), table))
                            .toList();

            columnList.addAll(columns);
        }
        else{ //query has specific columns so parse and map it.
            List<DbColumn> columns =
                    fields.stream()
                            .map(col -> createColumn(col, table))
                            .toList();
            columnList.addAll(columns);
        }

        return columnList;
    }


}
