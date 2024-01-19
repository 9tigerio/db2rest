package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.rest.read.model.CountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CountQueryController {
    private final CountQueryService countQueryService;
    @GetMapping("/{tableName}/count")
    public CountResponse count(@PathVariable String tableName
                                    , @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

        log.info("tableName - {}", tableName);
        log.info("filter - {}", filter);

        return countQueryService.count(tableName, filter);
    }
}
