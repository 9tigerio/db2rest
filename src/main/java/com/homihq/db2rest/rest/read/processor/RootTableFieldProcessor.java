package com.homihq.db2rest.rest.read.processor;


import com.homihq.db2rest.rest.read.dto.ReadContextV2;
import com.homihq.db2rest.model.DbColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
@Slf4j
@Order(4)
public class RootTableFieldProcessor implements ReadProcessor {
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
            columnList.addAll(readContextV2.getRoot().buildColumns());
        }
        else{ //query has specific columns so parse and map it.
            List<DbColumn> columns =
                    Arrays.stream(readContextV2.getFields().split(","))
                            .map(col -> readContextV2.getRoot().buildColumn(col))
                            .toList();
            columnList.addAll(columns);
        }

        readContextV2.setCols(columnList);

    }

}
