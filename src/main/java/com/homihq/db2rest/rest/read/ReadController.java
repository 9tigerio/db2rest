package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.rest.read.v2.dto.JoinDetail;
import com.homihq.db2rest.rest.read.v2.dto.ReadContextV2;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.springframework.web.bind.ServletRequestUtils.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@Slf4j
@RequiredArgsConstructor
public class ReadController {

    private final ReadService readService;

    @GetMapping(value = "/{tableName}" , produces = "application/json")
    public Object findAll(@PathVariable String tableName,
                                  @RequestHeader(name = "Accept-Profile", required = false) String schemaName,
                                  @RequestParam(name = "select", required = false, defaultValue = "") String select,
                                  @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                                  Sort sort,
                                  Pageable pageable, HttpServletRequest httpServletRequest) {

        log.info("schemaName - {}", schemaName);
        log.info("select - {}", select);
        log.info("filter - {}", filter);
        log.info("pageable - {}", pageable);


        if( getIntParameter(httpServletRequest, "page", -1) == -1 &&
                getIntParameter(httpServletRequest, "size", -1) == -1) {
            pageable = Pageable.unpaged(sort);
        }


        return readService.findAll(schemaName, tableName,select, filter, pageable, sort);
    }

    @PostMapping(value = "/V2/{tableName}" , produces = "application/json")
    public Object find(@PathVariable String tableName,
                       @RequestParam(name = "fields", required = false, defaultValue = "*") String fields,
                       @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                       @RequestParam(name = "sort", required = false, defaultValue = "") List<String> sorts,
                       @RequestParam(name = "limit", required = false, defaultValue = "-1") int limit,
                       @RequestParam(name = "offset", required = false, defaultValue = "-1") long offset,
                       @RequestBody List<JoinDetail> joins
    ) {

        log.info("fields - {}", fields);
        log.info("filter - {}", filter);
        log.info("sort - {}", sorts);
        log.info("limit - {}", limit);
        log.info("offset - {}", offset);

        log.info("join - {}", joins);

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
