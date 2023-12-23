package com.homihq.db2rest.rest.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class QueryResource {

    private final QueryService queryService;


    @GetMapping("/{tableName}")
    public Object find(@PathVariable String tableName,
                       @RequestHeader(name = "Accept-Profile") String schemaName,
        @RequestParam(name = "select", required = false, defaultValue = "") String select,
        @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

        return queryService.findAll(schemaName, tableName, select, filter);
    }

    @GetMapping("/{tableName}/{joinTable}")
    public Object findByJoinTable(@PathVariable String tableName,
                                  @PathVariable String joinTable,
                                  @RequestHeader(name = "Accept-Profile") String schemaName,
                       @RequestParam(name = "select", required = false, defaultValue = "") String select,
                       @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

        return queryService.findAllByJoinTable(schemaName, tableName,select, filter, joinTable);
    }



}
