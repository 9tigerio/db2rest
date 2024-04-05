package com.homihq.db2rest.jdbc.rest.read;

import com.homihq.db2rest.core.service.CountQueryService;
import com.homihq.db2rest.jdbc.rest.read.dto.CountResponse;
import com.homihq.db2rest.jdbc.rest.read.dto.ReadContext;
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

        log.debug("tableName - {}", tableName);
        log.debug("filter - {}", filter);

        ReadContext readContext = ReadContext.builder()
                .tableName(tableName)
                .filter(filter)
                .build();

        return countQueryService.count(readContext);
    }
}
