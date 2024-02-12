package com.homihq.db2rest.rest.read.processor.pre;

import com.homihq.db2rest.exception.InvalidColumnException;
import com.homihq.db2rest.rest.read.dto.ReadContextV2;
import static com.homihq.db2rest.schema.TypeMapperUtil.getJdbcType;

import com.homihq.db2rest.rest.read.model.DbColumn;
import com.homihq.db2rest.rest.read.model.DbTable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
@Slf4j
@Order(4)
public class RootTableFieldPreProcessor implements ReadPreProcessor {
    @Override
    public void process(ReadContextV2 readContextV2) {
        String fields = readContextV2.getFields();

        //There are 2 possibilities
        // - field can be *
        // - can be set of fields from the root table

        fields = StringUtils.trim(fields);

        log.info("Fields - {}", fields);
        List<DbColumn> columnList = new ArrayList<>();
        if(StringUtils.equals("*", fields)) {

            //include all fields of root table
            List<DbColumn> columns =
            readContextV2.getRoot()
                    .table().getColumns()
                            .stream()
                    .map(column ->
                            new DbColumn(readContextV2.getTableName(), column.getName(),getJdbcType(column) , column, "", readContextV2.getRoot().alias()))
                    .toList();

            columnList.addAll(columns);
        }
        else{ //query has specific columns so parse and map it.
            List<DbColumn> columns =
                    Arrays.stream(readContextV2.getFields().split(","))
                            .map(col -> getColumn(col, readContextV2.getRoot()))
                            .toList();
            columnList.addAll(columns);
        }

        readContextV2.setCols(columnList);

    }

    private DbColumn getColumn(String columnName, DbTable rootTable) {
        Column column = rootTable.lookupColumn(columnName);
        
        return new DbColumn(rootTable.name(), columnName, getJdbcType(column) , column, "", rootTable.alias());
    }
}
