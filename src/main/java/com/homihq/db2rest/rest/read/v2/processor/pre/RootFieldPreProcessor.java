package com.homihq.db2rest.rest.read.v2.processor.pre;

import com.homihq.db2rest.exception.InvalidColumnException;
import com.homihq.db2rest.mybatis.MyBatisTable;
import com.homihq.db2rest.rest.read.v2.dto.ReadContextV2;
import static com.homihq.db2rest.schema.TypeMapperUtil.getJdbcType;

import com.homihq.db2rest.rest.read.v2.processor.pre.ReadPreProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlColumn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import schemacrawler.schema.Column;

import java.util.Arrays;
import java.util.List;


@Component
@Slf4j
@Order(2)
public class RootFieldPreProcessor implements ReadPreProcessor {
    @Override
    public void process(ReadContextV2 readContextV2) {
        String fields = readContextV2.getFields();

        //There are 2 possibilities
        // - field can be *
        // - can be set of fields from the root table

        fields = StringUtils.trim(fields);

        log.info("Fields - {}", fields);
        List<BasicColumn> columns;
        if(StringUtils.equals("*", fields)) {

            //include all fields of root table
            columns =
            readContextV2.getRootTable()
                    .getTable().getColumns()
                            .stream()
                            .map(column -> (BasicColumn)SqlColumn.of(column.getName(), readContextV2.getRootTable(),
                                    getJdbcType(column)))
                            .toList();


        }
        else{ //query has specific columns so parse and map it.
            columns =
                    Arrays.stream(readContextV2.getFields().split(","))
                            .map(col -> getColumn(col, readContextV2.getRootTable()))
                            .toList();
            
        }

        readContextV2.setColumns(columns);

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
