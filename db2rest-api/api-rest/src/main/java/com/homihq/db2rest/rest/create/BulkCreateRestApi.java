package com.homihq.db2rest.rest.create;

import com.homihq.db2rest.core.dto.CreateBulkResponse;
import com.homihq.db2rest.core.dto.CreateResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;

public interface BulkCreateRestApi {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = VERSION + "/{dbId}/{tableName}/bulk",
            consumes = {"application/json", "text/csv"}
    )
    CreateBulkResponse save(@PathVariable String dbId,
                            @PathVariable String tableName,
                            @RequestHeader(name = "Content-Profile", required = false) String schemaName,
                            @RequestParam(name = "columns", required = false) List<String> includeColumns,
                            @RequestParam(name = "sequences", required = false) List<String> sequences,
                            @RequestParam(name = "tsIdEnabled", required = false, defaultValue = "false") boolean tsIdEnabled,
                            HttpServletRequest request) throws Exception;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = VERSION + "/{dbId}/{tableName}/upload", consumes = {"multipart/form-data", "application/json"})
    CompletableFuture<CreateResponse> saveMultipartFile(
            @PathVariable String dbId,
            @PathVariable String tableName,
            @RequestHeader(name = "Content-Profile", required = false) String schemaName,
            @RequestParam(name = "columns", required = false) List<String> includeColumns,
            @RequestParam(name = "sequences", required = false) List<String> sequences,
            @RequestParam(name = "tsIdEnabled", required = false, defaultValue = "false") boolean tsIdEnabled,
            @RequestParam("file") MultipartFile file) throws Exception;
}
