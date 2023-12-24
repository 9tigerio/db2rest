package com.homihq.db2rest.rest.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class QueryResource {

    private final QueryService queryService;

    /*
    @GetMapping("/{tableName}")
    public Object findAll(@PathVariable String tableName,
                       @RequestHeader(name = "Accept-Profile") String schemaName,
        @RequestParam(name = "select", required = false, defaultValue = "") String select,
        @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

        return queryService.findAll(schemaName, tableName, select, filter);
    }

     */

    @GetMapping("/{tableName}")
    public Object findByJoinTable(@PathVariable String tableName,
                                  @RequestHeader(name = "Accept-Profile") String schemaName,
                                  @RequestParam(name = "join", required = false, defaultValue = "") String join,
                       @RequestParam(name = "select", required = false, defaultValue = "") String select,
                       @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

        log.info("join - {}", join);

        return queryService.findAllByJoinTable(schemaName, tableName,select, filter, join);
    }



}
