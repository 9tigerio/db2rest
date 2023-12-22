package com.homihq.db2rest.rest.query;

import com.homihq.db2rest.rest.query.QueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequiredArgsConstructor
public class QueryResource {

    private final QueryService queryService;


    @GetMapping("/{tableName}")
    public Object find(@PathVariable String tableName,
        @RequestParam(name = "select", required = false, defaultValue = "") String select,
        @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

        return queryService.findAll(tableName, select, filter);
    }

    @GetMapping("/{tableName}/{joinTable}")
    public Object findByJoinTable(@PathVariable String tableName,
                                  @PathVariable String joinTable,
                       @RequestParam(name = "select", required = false, defaultValue = "") String select,
                       @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

        return queryService.findAllByJoinTable(tableName,select, filter, joinTable);
    }



}
