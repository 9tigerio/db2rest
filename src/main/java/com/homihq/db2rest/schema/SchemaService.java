package com.homihq.db2rest.schema;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Component
@Slf4j
@RequiredArgsConstructor
public final class SchemaService {


    private final DSLContext dslContext;

    private List<Table<?>> tables;


    public Optional<Table<?>> getTableByName(String name) {
        return tables.stream().filter( t ->
                name.equals(t.getName())
        ).findFirst();
    }

    public Optional<Field<?>> getByTableNameAndColumnName(String tableName, String columnName) {
        return
        Arrays.stream(getTableByName(tableName)
                .orElseThrow(() -> new RuntimeException("Table - " + tableName + " not found"))
                .fields()).filter(field -> columnName.equals(columnName)).findFirst();
    }

    @PostConstruct
    public void load() {
        //TODO add a property to cache or not as this can be lot of data in memory , on demand caching
        //may be better
         tables = dslContext.meta().getTables();
    }


}
