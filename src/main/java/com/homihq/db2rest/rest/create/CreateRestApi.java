package com.homihq.db2rest.rest.create;

import com.homihq.db2rest.rest.create.dto.CreateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface CreateRestApi {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{tableName}")
    CreateResponse save(@PathVariable String tableName,
                        @RequestHeader(name = "Content-Profile") String schemaName,
                        @RequestParam(name = "columns", required = false) List<String> columns,
                        @RequestBody Map<String, Object> data,
                        @RequestParam(name = "tsid", required = false) String tsid,
                        @RequestParam(name = "tsidType", required = false, defaultValue = "number") String tsidType);
}
