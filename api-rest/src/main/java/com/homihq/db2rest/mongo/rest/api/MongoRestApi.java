package com.homihq.db2rest.mongo.rest.api;

import com.homihq.db2rest.core.dto.CountResponse;
import com.homihq.db2rest.core.dto.CreateBulkResponse;
import com.homihq.db2rest.core.dto.CreateResponse;
import com.homihq.db2rest.core.dto.DeleteResponse;
import com.homihq.db2rest.core.dto.ExistsResponse;
import com.homihq.db2rest.core.dto.UpdateResponse;
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
    String VERSION = "/v1/mongo";

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(VERSION + "/{dbId}/{collectionName}")
    CreateResponse save(
            @PathVariable String dbId,
            @PathVariable String collectionName,
            @RequestParam(name = "fields", required = false) List<String> includeFields,
            @RequestBody Map<String, Object> data);

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(VERSION + "/{dbId}/{collectionName}/bulk")
    CreateBulkResponse saveAll(@PathVariable String dbId, @PathVariable String collectionName,
                               @RequestParam(name = "fields", required = false) List<String> includeFields,
                               @RequestBody List<Map<String, Object>> dataList);

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(VERSION + "/{dbId}/{collectionName}")
    UpdateResponse patch(@PathVariable String dbId, @PathVariable String collectionName,
                         @RequestBody Map<String, Object> data,
                         @RequestParam(required = false, defaultValue = "") String filter);

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(VERSION + "/{dbId}/{collectionName}")
    DeleteResponse delete(@PathVariable String dbId, @PathVariable String collectionName,
                          @RequestParam(required = false, defaultValue = "") String filter);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(VERSION + "/{dbId}/{collectionName}")
    Object findAll(@PathVariable String dbId, @PathVariable String collectionName,
                   @RequestParam(required = false, defaultValue = "*") String fields,
                   @RequestParam(required = false, defaultValue = "") String filter,
                   @RequestParam(name = "sort", required = false, defaultValue = "") List<String> sorts,
                   @RequestParam(required = false, defaultValue = "-1") int limit,
                   @RequestParam(required = false, defaultValue = "-1") long offset);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(VERSION + "/{dbId}/{collectionName}/one")
    Map<String, Object> findOne(@PathVariable String dbId, @PathVariable String collectionName,
                                @RequestParam(required = false, defaultValue = "*") String fields,
                                @RequestParam(required = false, defaultValue = "") String filter);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(VERSION + "/{dbId}/{collectionName}/count")
    CountResponse count(@PathVariable String dbId, @PathVariable String collectionName,
                        @RequestParam(required = false, defaultValue = "") String filter);

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = VERSION + "/{dbId}/{collectionName}/exists")
    ExistsResponse exists(@PathVariable String dbId, @PathVariable String collectionName,
                          @RequestParam(defaultValue = "") String filter);
}
