package com.homihq.db2rest.rest.read.v2.processor.pre;

import com.homihq.db2rest.mybatis.MyBatisTable;
import com.homihq.db2rest.rest.read.v2.dto.ReadContextV2;
import com.homihq.db2rest.rest.read.v2.processor.pre.ReadPreProcessor;
import com.homihq.db2rest.schema.SchemaManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Order(1)
public class RootTablePreProcessor implements ReadPreProcessor {

    private final SchemaManager schemaManager;
    @Override
    public void process(ReadContextV2 readContextV2) {
        MyBatisTable table =
        schemaManager.getTable(readContextV2.getTableName());

        readContextV2.setRootTable(table);
    }
}
