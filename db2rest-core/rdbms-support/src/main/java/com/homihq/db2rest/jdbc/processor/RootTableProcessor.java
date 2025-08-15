package com.homihq.db2rest.jdbc.processor;


import com.homihq.db2rest.jdbc.JdbcManager;
import com.db2rest.jdbc.dialect.model.DbTable;
import com.homihq.db2rest.jdbc.dto.ReadContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

@Slf4j
@RequiredArgsConstructor
@Order(1)
public class RootTableProcessor implements ReadProcessor {

    private final JdbcManager jdbcManager;

    @Override
    public void process(ReadContext readContext) {
        log.debug("Processing root table");
        DbTable table =
                jdbcManager.getTable(
                        readContext.getDbId(),
                        readContext.getSchemaName(),
                        readContext.getTableName());

        readContext.setRoot(table);
    }
}
