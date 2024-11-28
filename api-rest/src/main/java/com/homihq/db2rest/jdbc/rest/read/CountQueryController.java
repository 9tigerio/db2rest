package com.homihq.db2rest.jdbc.rest.read;

import com.homihq.db2rest.core.dto.CountResponse;
import com.homihq.db2rest.jdbc.core.service.CountQueryService;
import com.homihq.db2rest.jdbc.dto.ReadContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;

@RestController
@Slf4j
@RequiredArgsConstructor
public class CountQueryController {

    private final CountQueryService countQueryService;

    @GetMapping(VERSION + "/{dbId}/{tableName}/count")
    public CountResponse count(@PathVariable String dbId,
                               @PathVariable String tableName,
                               @RequestHeader(name = "Accept-Profile", required = false) String schemaName,
                               @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

        log.debug("tableName - {}", tableName);
        log.debug("filter - {}", filter);

        ReadContext readContext = ReadContext.builder()
                .dbId(dbId)
                .schemaName(schemaName)
                .tableName(tableName)
                .filter(filter)
                .build();

        return countQueryService.count(readContext);
    }
}
