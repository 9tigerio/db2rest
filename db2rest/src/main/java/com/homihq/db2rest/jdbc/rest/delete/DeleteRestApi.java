package com.homihq.db2rest.jdbc.rest.delete;

import com.homihq.db2rest.core.dto.DeleteResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

public interface DeleteRestApi {
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{tableName}")
    DeleteResponse delete(
                        @RequestHeader(name="Content-Profile", required = false) String schemaName,
                        @PathVariable String tableName,
                        @RequestParam(name = "filter", required = false, defaultValue = "") String filter);
}
