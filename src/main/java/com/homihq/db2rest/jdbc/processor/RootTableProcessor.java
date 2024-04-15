package com.homihq.db2rest.jdbc.processor;


import com.homihq.db2rest.jdbc.JdbcSchemaCache;
import com.homihq.db2rest.jdbc.rest.read.dto.ReadContext;
import com.homihq.db2rest.jdbc.core.model.DbTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

@Slf4j
@RequiredArgsConstructor
@Order(1)
public class RootTableProcessor implements ReadProcessor {

    private final JdbcSchemaCache jdbcSchemaCache;
    @Override
    public void process(ReadContext readContext) {
        log.debug("Processing root table");
        DbTable table =
                jdbcSchemaCache.getTable(readContext.getTableName());

        readContext.setRoot(table);
    }
}
