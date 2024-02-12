package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.rest.read.dto.JoinDetail;
import com.homihq.db2rest.rest.read.dto.ReadContextV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReadController {

    private final ReadService readService;

    @GetMapping(value = "/{tableName}" , produces = "application/json")
    public Object findAll(@PathVariable String tableName,
                          @RequestParam(name = "fields", required = false, defaultValue = "*") String fields,
                          @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                          @RequestParam(name = "sort", required = false, defaultValue = "") List<String> sorts,
                          @RequestParam(name = "limit", required = false, defaultValue = "-1") int limit,
                          @RequestParam(name = "offset", required = false, defaultValue = "-1") long offset) {

        ReadContextV2 readContextV2 = ReadContextV2.builder()
                .tableName(tableName)
                .fields(fields)
                .filter(filter)
                .sorts(sorts)
                .limit(limit)
                .offset(offset)
                .build();


        return readService.findAll(readContextV2);
    }

    @PostMapping(value = "/{tableName}/expand" , produces = "application/json")
    public Object find(@PathVariable String tableName,
                       @RequestParam(name = "fields", required = false, defaultValue = "*") String fields,
                       @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                       @RequestParam(name = "sort", required = false, defaultValue = "") List<String> sorts,
                       @RequestParam(name = "limit", required = false, defaultValue = "-1") int limit,
                       @RequestParam(name = "offset", required = false, defaultValue = "-1") long offset,
                       @RequestBody List<JoinDetail> joins
    ) {

        ReadContextV2 readContextV2 = ReadContextV2.builder()
                .tableName(tableName)
                .fields(fields)
                .filter(filter)
                .sorts(sorts)
                .limit(limit)
                .offset(offset)
                .joins(joins)
                .build();


        return readService.findAll(readContextV2);

    }

}
