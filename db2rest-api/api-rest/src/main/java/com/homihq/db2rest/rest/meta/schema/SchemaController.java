package com.homihq.db2rest.rest.meta.schema;

import com.homihq.db2rest.core.exception.GenericDataAccessException;
import com.homihq.db2rest.jdbc.JdbcManager;
import com.db2rest.jdbc.dialect.model.DbTable;
import com.homihq.db2rest.jdbc.sql.DbMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * provides methods to filter and retrieve schema objects from a database
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class SchemaController implements SchemaRestApi {

    private final JdbcManager jdbcManager;

    /**
     * @param dbId    database id from which to retrieve the schema objects
     * @param filter  filter conditions to match against a schema, name, or type
     * @param columns include the column information for a table
     * @return list of schema objects (schema, name, type) that match the filter conditions
     */
    @Override
    public List<? extends TableObject> getObjects(String dbId, String filter, Boolean columns) {

        log.debug("Filter - {}", filter);

        DbMeta dbMeta = jdbcManager.getDbMetaByDbId(dbId);



        if (Objects.isNull(dbMeta)) {
            return List.of();
        }

        SchemaFilter schemaFilter = getSchemaFilter(filter);

        List<DbTable> dbTables = dbMeta.dbTables();
        Function<DbTable, ? extends TableObject> tableMapper = columns
                ? TableWithColumnsObject::new
                : TableObject::new;

        if (Objects.isNull(schemaFilter)) {
            return dbTables.stream().map(tableMapper).toList();
        } else {
            log.info("schemaFilter - {}", schemaFilter);
            return dbTables.stream()
                    .filter(dbTable -> {

                        if (StringUtils.equals(schemaFilter.field, "schema")
                                && StringUtils.containsIgnoreCase(dbTable.schema(), schemaFilter.value)) {
                            return true;
                        } else if (StringUtils.equals(schemaFilter.field, "name")
                                && StringUtils.containsIgnoreCase(dbTable.name(), schemaFilter.value)) {
                            return true;
                        } else {
                            return StringUtils.equals(schemaFilter.field, "type")
                                    && StringUtils.containsIgnoreCase(dbTable.type(), schemaFilter.value);
                        }

                    })
                    .map(tableMapper).toList();
        }
    }

    private SchemaFilter getSchemaFilter(String filter) {
        if (StringUtils.isBlank(filter)) {
            return null;
        }

        String[] fragments = filter.split("==");

        if (fragments.length != 2) {
            throw new GenericDataAccessException("Invalid filter condition. Only == supported for schema filter using a single value only.");
        }

        return new SchemaFilter(fragments[0], fragments[1]);
    }


    private record SchemaFilter(String field, String value) {
    }

}
