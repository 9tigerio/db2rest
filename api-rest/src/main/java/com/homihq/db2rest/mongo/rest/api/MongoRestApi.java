package com.homihq.db2rest.mongo.rest.api;

import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.core.dto.DeleteResponse;
import com.homihq.db2rest.jdbc.dto.UpdateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;

public interface MongoRestApi {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{collectionName}")
    CreateResponse save(@PathVariable String collectionName,
                        @RequestParam(name = "fields", required = false) List<String> includeFields,
                        @RequestBody Map<String, Object> data);

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{collectionName}")
    UpdateResponse patch(@PathVariable String collectionName,
                         @RequestBody Map<String, Object> data,
                         @RequestParam(name = "filter", required = false, defaultValue = "") String filter);

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{collectionName}")
    DeleteResponse delete(@PathVariable String collectionName,
                          @RequestParam(name = "filter", required = false, defaultValue = "") String filter);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{collectionName}")
    Object findAll(@PathVariable String collectionName,
                   @RequestParam(name = "fields", required = false, defaultValue = "*") String fields,
                   @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                   @RequestParam(name = "sort", required = false, defaultValue = "") List<String> sorts,
                   @RequestParam(name = "limit", required = false, defaultValue = "-1") int limit,
                   @RequestParam(name = "offset", required = false, defaultValue = "-1") long offset);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{collectionName}/one")
    Map<String, Object> findOne(@PathVariable String collectionName,
                                @RequestParam(name = "fields", required = false, defaultValue = "*") String fields,
                                @RequestParam(name = "filter", required = false, defaultValue = "") String filter);
}
