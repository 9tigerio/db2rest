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
                        @RequestParam(name = "columns", required = false) List<String> includeColumns,
                        @RequestParam(name = "sequences", required = false) List<String> sequences,
                        @RequestBody Map<String, Object> data,
                        @RequestParam(name = "tsIdEnabled", required = false, defaultValue = "false") boolean tsIdEnabled);
}
