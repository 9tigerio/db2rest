package com.homihq.db2rest.rest.read.v2;

import com.homihq.db2rest.rest.read.ReadService;

import com.homihq.db2rest.rest.read.v2.dto.ReadContextV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
public class ReadControllerV2 {

    private final ReadServiceV2 readServiceV2;


    @GetMapping(value = "/V2/{tableName}" , produces = "application/json")
    public Object find(@PathVariable String tableName,
                                  @RequestParam(name = "fields", required = false, defaultValue = "*") String fields,
                                  @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                                  @RequestParam(name = "sort", required = false, defaultValue = "") List<String> sorts,
                       @RequestParam(name = "limit", required = false, defaultValue = "-1") int limit,
                       @RequestParam(name = "offset", required = false, defaultValue = "-1") long offset
                        ) {

        log.info("fields - {}", fields);
        log.info("filter - {}", filter);
        log.info("sort - {}", sorts);
        log.info("limit - {}", limit);
        log.info("offset - {}", offset);

        ReadContextV2 readContextV2 = ReadContextV2.builder()
                .tableName(tableName)
                .fields(fields)
                .filter(filter)
                .sorts(sorts)
                .limit(limit)
                .offset(offset)
                .build();


        return readServiceV2.find(readContextV2);

    }



}
