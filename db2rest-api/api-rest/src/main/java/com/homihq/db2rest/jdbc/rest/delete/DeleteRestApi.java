package com.homihq.db2rest.jdbc.rest.delete;

import com.homihq.db2rest.core.dto.DeleteResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;

public interface DeleteRestApi {
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(VERSION + "/{dbId}/{tableName}")
    DeleteResponse delete(
            @PathVariable String dbId,
            @RequestHeader(name = "Content-Profile", required = false) String schemaName,
            @PathVariable String tableName,
            @RequestParam(required = false, defaultValue = "") String filter);
}
