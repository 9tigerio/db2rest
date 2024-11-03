package com.homihq.db2rest.jdbc.rest.meta.db;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;

@RequestMapping(VERSION + "/$dbs")
@Tag(name = "DB Info Objects", description = "Details the databases being managed by this instance of DB2Rest")
public interface DbInfoRestApi {


    @Operation(summary = "Get all database info details",
            description = "Get all database info details", tags = { "DB Info Objects" })
    @GetMapping()
    List<DbInfoObject> getObjects();


}
