package com.homihq.db2rest.rest.create;

import com.homihq.db2rest.core.dto.CreateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;

public interface CreateRestApi {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(VERSION + "/{dbId}/{tableName}")
    CreateResponse save(@PathVariable String dbId,
                        @RequestHeader(name = "Content-Profile", required = false) String schemaName,
                        @PathVariable String tableName,
                        @RequestParam(name = "columns", required = false) List<String> includeColumns,
                        @RequestParam(name = "sequences", required = false) List<String> sequences,
                        @RequestBody Map<String, Object> data,
                        @RequestParam(name = "tsIdEnabled", required = false, defaultValue = "false") boolean tsIdEnabled);
}
