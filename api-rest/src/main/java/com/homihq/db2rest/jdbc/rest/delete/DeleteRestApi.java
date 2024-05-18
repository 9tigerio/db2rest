package com.homihq.db2rest.jdbc.rest.delete;

import com.homihq.db2rest.core.dto.DeleteResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
public interface DeleteRestApi {
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(VERSION + "/{dbId}/{tableName}")
    DeleteResponse delete(
                         @PathVariable String dbId,
                        @RequestHeader(name="Content-Profile", required = false) String schemaName,
                        @PathVariable String tableName,
                        @RequestParam(name = "filter", required = false, defaultValue = "") String filter);
}
