package com.homihq.db2rest.rest.create;

import com.homihq.db2rest.rest.create.dto.CreateBulkResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

public interface BulkCreateRestApi {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{tableName}/bulk",
            consumes = {"application/json", "text/csv"}
    )
    CreateBulkResponse save(@PathVariable String tableName,
                            @RequestParam(name = "tsid", required = false) String tsid,
                            @RequestParam(name = "tsidType", required = false, defaultValue = "number") String tsidType,
                            HttpServletRequest request) throws Exception;
}
