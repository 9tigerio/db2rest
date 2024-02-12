package com.homihq.db2rest.rest.read.processor;


import com.homihq.db2rest.rest.read.dto.ReadContextV2;
import com.homihq.db2rest.rest.read.model.DbTable;
import com.homihq.db2rest.schema.SchemaManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Order(1)
public class RootTableProcessor implements ReadProcessor {

    private final SchemaManager schemaManager;
    @Override
    public void process(ReadContextV2 readContextV2) {
        log.info("Processing root table");
        DbTable table =
        schemaManager.getTableV2(readContextV2.getTableName());

        readContextV2.setRoot(table);
    }
}
