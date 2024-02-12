package com.homihq.db2rest.rest.read.processor.pre;

import com.homihq.db2rest.exception.InvalidColumnException;
import com.homihq.db2rest.mybatis.MyBatisTable;
import com.homihq.db2rest.rest.read.dto.JoinDetail;
import com.homihq.db2rest.rest.read.dto.ReadContextV2;
import com.homihq.db2rest.schema.SchemaManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlColumn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.homihq.db2rest.schema.TypeMapperUtil.getJdbcType;


//@Component
@Slf4j
@Order(3)
@RequiredArgsConstructor
public class JoinTableFieldPreProcessor implements ReadPreProcessor {

    private final SchemaManager schemaManager;

    @Override
    public void process(ReadContextV2 readContextV2) {
        List<JoinDetail> joins = readContextV2.getJoins();

        for(JoinDetail joinDetail : joins) {
            String t = joinDetail.table();

            //TODO - Same tables looked up again to set join relations - can be optimized

            MyBatisTable myBatisTable = schemaManager.getTable(t);
            List<BasicColumn> columnList =
            addColumns(myBatisTable, joinDetail.fields());

           readContextV2.addColumns(columnList);
        }
    }

    private List<BasicColumn> addColumns(MyBatisTable table, List<String> fields) {

        //There are 2 possibilities
        // - field can be *
        // - can be set of fields from the given table

        log.info("Fields - {}", fields);

        List<BasicColumn> columnList = new ArrayList<>();

        if(Objects.isNull(fields)) {

            //include all fields of root table
            List<BasicColumn> columns =
                    table.getTable()
                    .getColumns()
                            .stream()
                            .map(column -> (BasicColumn)SqlColumn.of(column.getName(), table,
                                    getJdbcType(column)))
                            .toList();

            columnList.addAll(columns);
        }
        else{ //query has specific columns so parse and map it.
            List<BasicColumn> columns =
                    fields.stream()
                            .map(col -> getColumn(col, table))
                            .toList();
            columnList.addAll(columns);
        }

        return columnList;
    }

    private BasicColumn getColumn(String col, MyBatisTable rootTable) {
        Column c =
        rootTable.getTable()
                .getColumns()
                .stream()
                .filter(column -> StringUtils.equalsIgnoreCase(col, column.getName()))
                .findFirst()
                .orElseThrow(()-> new InvalidColumnException(rootTable.getTableName(), col));
        
        return (BasicColumn)SqlColumn.of(c.getName(), rootTable,
                getJdbcType(c));
    }
}
