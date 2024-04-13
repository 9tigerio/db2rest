package com.homihq.db2rest.jdbc.rest.schema;

import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.JdbcSchemaCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SchemaController implements SchemaRestApi{

    private final JdbcSchemaCache jdbcSchemaCache;
    @Override
    public List<TableObject> getObjects(String filter) {

        log.info("Filter - {}", filter);

        SchemaFilter schemaFilter = getSchemaFilter(filter);

        if(Objects.isNull(schemaFilter)) {

            return jdbcSchemaCache.getTables().stream()
                    .map(t -> new TableObject(t.schema(), t.name(), t.type())).toList();
        }
        else{
            log.info("schemaFilter - {}", schemaFilter);
            return jdbcSchemaCache.getTables().stream()
                    .filter(dbTable -> {

                       if(StringUtils.equals(schemaFilter.field, "schema")
                               && StringUtils.containsIgnoreCase(dbTable.schema(), schemaFilter.value)) {
                            return true;
                       }
                       else if(StringUtils.equals(schemaFilter.field, "name")
                               && StringUtils.containsIgnoreCase(dbTable.name(), schemaFilter.value)) {
                           return true;
                       }
                       else return StringUtils.equals(schemaFilter.field, "type")
                                   && StringUtils.containsIgnoreCase(dbTable.type(), schemaFilter.value);

                    })
                    .map(t -> new TableObject(t.schema(), t.name(), t.type())).toList();
        }

    }

    private SchemaFilter getSchemaFilter(String filter) {
        if(StringUtils.isBlank(filter))
            return null;

        String [] fragments = filter.split("==");

        if(fragments.length != 2) throw new GenericDataAccessException("Invalid filter condition. Only == supported for schema filter with a single condition only.");

        return new SchemaFilter(fragments[0], fragments[1]);

    }


    private record SchemaFilter(String field, String value){}




}
