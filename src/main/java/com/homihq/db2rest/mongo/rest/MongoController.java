package com.homihq.db2rest.mongo.rest;


import com.homihq.db2rest.mongo.repository.MongoRepository;
import com.homihq.db2rest.mongo.rest.api.MongoRestApi;
import com.homihq.db2rest.core.dto.CreateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MongoController implements MongoRestApi {

    private final MongoRepository mongoRepository;

    @Override
    public CreateResponse save(String collectionName,
                               List<String> includeFields,
                               Map<String, Object> data) {

        return mongoRepository
                .save( collectionName, includeFields, data);

    }

}
