package com.homihq.db2rest.rest.read.processor;


import com.homihq.db2rest.rest.read.dto.ReadContext;
import com.homihq.db2rest.model.DbTable;
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
    public void process(ReadContext readContext) {
        log.info("Processing root table");
        DbTable table =
        schemaManager.getTableV2(readContext.getTableName());

        readContext.setRoot(table);
    }
}
