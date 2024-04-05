package com.homihq.db2rest.mongo.rest.api;

import com.homihq.db2rest.rest.create.dto.CreateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

public interface MongoRestApi {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{collectionName}")
    CreateResponse save(@PathVariable String collectionName,
                        @RequestParam(name = "fields", required = false) List<String> includeFields,
                        @RequestBody Map<String, Object> data);
}
