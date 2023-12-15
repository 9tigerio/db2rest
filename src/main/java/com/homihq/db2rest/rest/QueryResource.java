package com.homihq.db2rest.rest;


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

        List<String> columns = List.of();

        if(StringUtils.isNotBlank(select)) {
            columns = List.of(select.split(","));
        }

        return queryService.find(tableName, columns, rSql);
    }

}
