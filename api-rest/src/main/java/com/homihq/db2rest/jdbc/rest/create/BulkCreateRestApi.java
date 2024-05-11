package com.homihq.db2rest.jdbc.rest.create;

import com.homihq.db2rest.core.dto.CreateBulkResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface BulkCreateRestApi {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{dbName}/{tableName}/bulk",
            consumes = {"application/json", "text/csv"}
    )
    CreateBulkResponse save(@PathVariable String dbName,
                            @PathVariable String tableName,
                            @RequestHeader(name="Content-Profile", required = false) String schemaName,
                            @RequestParam(name = "columns", required = false) List<String> includeColumns,
                            @RequestParam(name = "sequences", required = false) List<String> sequences,
                            @RequestParam(name = "tsIdEnabled", required = false, defaultValue = "false") boolean tsIdEnabled,
                            HttpServletRequest request) throws Exception;
}
