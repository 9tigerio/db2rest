package com.homihq.db2rest.rest.delete;

import com.homihq.db2rest.auth.data.RoleFilterContext;
import com.homihq.db2rest.core.dto.DeleteResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.homihq.db2rest.auth.data.RoleFilterContext.REQUEST_ATTRIBUTE;
import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;

import java.util.List;

public interface DeleteRestApi {
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(VERSION + "/{dbId}/{tableName}")
    DeleteResponse delete(
            @RequestAttribute(name = REQUEST_ATTRIBUTE, required = false) RoleFilterContext roleFilterContext,
            @PathVariable String dbId,
            @RequestHeader(name = "Content-Profile", required = false) String schemaName,
            @PathVariable String tableName,
            @RequestParam(required = false, defaultValue = "") String filter);
}
