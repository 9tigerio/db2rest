package com.homihq.db2rest.jdbc.processor;


import com.homihq.db2rest.jdbc.rest.read.dto.ReadContext;
import com.homihq.db2rest.jdbc.core.model.DbColumn;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


//@Component
@Slf4j
@Order(4)
public class RootTableFieldProcessor implements ReadProcessor {
    @Override
    public void process(ReadContext readContext) {
        String fields = readContext.getFields();

        //There are 2 possibilities
        // - field can be *
        // - can be set of fields from the root table

        fields = StringUtils.trim(fields);

        log.debug("Fields - {}", fields);

        if(Objects.isNull(fields)) { //most likely count query
            return;
        }

        List<DbColumn> columnList = new ArrayList<>();
        if(StringUtils.equals("*", fields)) {

            //include all fields of root table
            columnList.addAll(readContext.getRoot().buildColumns());
        }
        else{ //query has specific columns so parse and map it.
            List<DbColumn> columns =
                    Arrays.stream(readContext.getFields().split(","))
                            .map(col -> readContext.getRoot().buildColumn(col))
                            .toList();
            columnList.addAll(columns);
        }
        log.debug("Column List - {}" , columnList);
        readContext.setCols(columnList);

    }

}
