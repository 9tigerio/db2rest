package com.homihq.db2rest.rest.read;

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
public class ReadController {

    private final ReadService readService;

    @GetMapping("/{tableName}")
    public Object findByJoinTable(@PathVariable String tableName,
                                  @RequestHeader(name = "Accept-Profile") String schemaName,
                                  @RequestParam(name = "select", required = false, defaultValue = "") String select,
                                  @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                                  Sort sort,
                                  Pageable pageable, HttpServletRequest httpServletRequest) {

        log.debug("schemaName - {}", schemaName);
        log.debug("select - {}", select);
        log.debug("filter - {}", filter);
        log.debug("pageable - {}", pageable);


        if( getIntParameter(httpServletRequest, "page", -1) == -1 &&
                getIntParameter(httpServletRequest, "size", -1) == -1) {
            pageable = Pageable.unpaged(sort);
        }


        return readService.findAll(schemaName, tableName,select, filter, pageable, sort);
    }



}
