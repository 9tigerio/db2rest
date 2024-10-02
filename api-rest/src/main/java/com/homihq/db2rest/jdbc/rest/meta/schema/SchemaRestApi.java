package com.homihq.db2rest.jdbc.rest.meta.schema;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
@RequestMapping(VERSION + "/{dbId}/$schemas")
@Tag(name = "Schema Objects", description = "Details about schemas and tables")
public interface SchemaRestApi {


    @Operation(summary = "Get all database objects for a given dbId",
            description = "Get all database objects from all schemas/catalogs, tables, columns", tags = { "Schema Objects" })
    @GetMapping()
    List<TableObject> getObjects(
            @PathVariable String dbId,
            @RequestParam(name = "filter", required = false, defaultValue = "") String filter);



}
