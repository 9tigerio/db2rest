package com.homihq.db2rest.rest.query;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.springframework.web.bind.ServletRequestUtils.*;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequiredArgsConstructor
public class QueryResource {

    private final QueryService queryService;

    @GetMapping("/{tableName}")
    public Object findByJoinTable(@PathVariable String tableName,
                                  @RequestHeader(name = "Accept-Profile") String schemaName,
                                  @RequestParam(name = "join", required = false, defaultValue = "") String join,
                                  @RequestParam(name = "select", required = false, defaultValue = "") String select,
                                  @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                                  Sort sort,
                                  Pageable pageable, HttpServletRequest httpServletRequest) {

        log.info("join - {}", join);
        log.info("schemaName - {}", schemaName);
        log.info("select - {}", select);
        log.info("filter - {}", filter);
        log.info("pageable - {}", pageable);

        log.info("sort - {}", sort);
        log.info("pageable 1 - {}", pageable.isPaged());
        if( getIntParameter(httpServletRequest, "page", -1) == -1 &&
                getIntParameter(httpServletRequest, "size", -1) == -1) {
            pageable = Pageable.unpaged(sort);
        }

        log.info("pageable 2 - {}", pageable.isPaged());
        return queryService.findAllByJoinTable(schemaName, tableName,select, filter, join, pageable);
    }



}
