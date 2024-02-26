package com.homihq.db2rest.rest.read.processor;


import com.homihq.db2rest.rest.read.dto.ReadContext;
import com.homihq.db2rest.model.DbTable;
import com.homihq.db2rest.schema.JdbcSchemaManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Order(1)
public class RootTableProcessor implements ReadProcessor {

    private final JdbcSchemaManager jdbcSchemaManager;
    @Override
    public void process(ReadContext readContext) {
        log.info("Processing root table");
        DbTable table =
        jdbcSchemaManager.getTable(readContext.getTableName());

        readContext.setRoot(table);
    }
}
