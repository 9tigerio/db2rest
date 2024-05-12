package com.homihq.db2rest.jdbc.rest.schema;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
@RequestMapping(VERSION + "/$schemas")
@Tag(name = "Schema Objects", description = "Details about schemas and tables")
public interface SchemaRestApi {


    @Operation(summary = "Get all database objects",
            description = "Get all database objects from all schemas/catalogs, tables, columns", tags = { "schemas" })
    @GetMapping()
    List<TableObject> getObjects(@RequestParam(name = "filter", required = false, defaultValue = "") String filter);


}
