package com.homihq.db2rest.rest.read.v2.processor;

import com.homihq.db2rest.rest.read.v2.dto.ReadContextV2;
import static com.homihq.db2rest.schema.TypeMapperUtil.getJdbcType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.SqlColumn;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@Order(2)
public class RootFieldProcessor implements ReadProcessor{
    @Override
    public void process(ReadContextV2 readContextV2) {
        String fields = readContextV2.getFields();

        //There are 2 possibilities
        // - field can be *
        // - can be set of fields from the root table

        fields = StringUtils.trim(fields);

        log.info("Fields - {}", fields);

        if(StringUtils.equals("*", fields)) {

            System.out.println("column - " + readContextV2.getRootTable()
                    .getTable().getColumns());

            //include all fields of root table
            List<BasicColumn> columns =
            readContextV2.getRootTable()
                    .getTable().getColumns()
                            .stream()
                            /*
                            .peek(column -> {
                                System.out.println("column - " + column);
                                System.out.println("column - " + column.getColumnDataType().getName());
                                System.out.println("column jdbc type - " + getJdbcType(column));
                            })*/
                            .map(column -> (BasicColumn)SqlColumn.of(column.getName(), readContextV2.getRootTable(),
                                    getJdbcType(column)))
                            .toList();

            readContextV2.setColumns(columns);
        }

    }
}
