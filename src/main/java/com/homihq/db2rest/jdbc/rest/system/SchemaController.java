package com.homihq.db2rest.jdbc.rest.system;

import com.homihq.db2rest.jdbc.JdbcSchemaCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SchemaController implements SchemaRestApi{

    private final JdbcSchemaCache jdbcSchemaCache;
    @Override
    public List<TableObject> getObjects() {
        return jdbcSchemaCache.getTables().stream()
                .map(t -> new TableObject(t.schema(), t.name(), t.type())).toList();
    }
}
