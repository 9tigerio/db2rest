package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.rest.read.dto.FindOneResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequiredArgsConstructor
public class FindOneController {

    private final FindOneService findOneService;

    @GetMapping("/{tableName}/one")
    public FindOneResponse findOne(@PathVariable String tableName,
                                   @RequestParam(name = "select", required = false, defaultValue = "") String select,
                                   @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {


        log.info("tableName - {}", tableName);
        log.info("select - {}", select);
        log.info("filter - {}", filter);

        return this.findOneService.findOne(tableName, select, filter);
    }


}
