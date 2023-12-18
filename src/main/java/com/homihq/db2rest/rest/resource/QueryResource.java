package com.homihq.db2rest.rest.resource;


import com.homihq.db2rest.rest.service.QueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class QueryResource {

    private final QueryService queryService;


    @GetMapping("/{tableName}")
    public Object find(@PathVariable String tableName,
        @RequestParam(name = "select", required = false, defaultValue = "") String select,
        @RequestParam(name = "rSql", required = false, defaultValue = "") String rSql) {

        log.info("select - {}", select);

        log.info("rSql - {}", rSql);

        return queryService.find(tableName, select, rSql);
    }

    @GetMapping("/{tableName}/{keys}/{joinTable}")
    public Object findByJoinTable(@PathVariable String tableName,
                                  @PathVariable String keys,
                                  @PathVariable String joinTable,
                       @RequestParam(name = "select", required = false, defaultValue = "") String select,
                       @RequestParam(name = "rSql", required = false, defaultValue = "") String rSql) {

        log.info("tableName - {}", tableName);
        log.info("keys - {}", keys);
        log.info("joinTable - {}", joinTable);

        return queryService.findByJoinTable(tableName, keys, joinTable,select, rSql);
    }

}
