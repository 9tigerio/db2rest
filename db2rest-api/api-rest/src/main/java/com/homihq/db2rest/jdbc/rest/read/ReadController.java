package com.homihq.db2rest.jdbc.rest.read;

import com.homihq.db2rest.config.Db2RestConfigProperties;
import com.homihq.db2rest.jdbc.core.service.ReadService;
import com.homihq.db2rest.jdbc.dto.JoinDetail;
import com.homihq.db2rest.jdbc.dto.ReadContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ReadController {

    private final ReadService readService;
    private final Db2RestConfigProperties db2RestConfigProperties;

    @GetMapping(value = VERSION + "/{dbId}/{tableName}", produces = "application/json")
    public Object findAll(
            @PathVariable String dbId,
            @PathVariable String tableName,
            @RequestHeader(name = "Accept-Profile", required = false) String schemaName,
            @RequestParam(required = false, defaultValue = "*") String fields,
            @RequestParam(required = false, defaultValue = "") String filter,
            @RequestParam(name = "sort", required = false, defaultValue = "") List<String> sorts,
            @RequestParam(required = false, defaultValue = "-1") int limit,
            @RequestParam(required = false, defaultValue = "-1") long offset) {

        log.debug("filter - {}", filter);

        ReadContext readContext = ReadContext.builder()
                .dbId(dbId)
                .schemaName(schemaName)
                .tableName(tableName)
                .fields(fields)
                .filter(filter)
                .sorts(sorts)
                .limit(limit)
                .defaultFetchLimit(db2RestConfigProperties.getDefaultFetchLimit())
                .offset(offset)
                .build();


        return readService.findAll(readContext);
    }

    @PostMapping(value = VERSION + "/{dbId}/{tableName}/_expand", produces = "application/json")
    public Object find(
            @PathVariable String dbId,
            @PathVariable String tableName,
            @RequestHeader(name = "Accept-Profile", required = false) String schemaName,
            @RequestParam(required = false, defaultValue = "*") String fields,
            @RequestParam(required = false, defaultValue = "") String filter,
            @RequestParam(name = "sort", required = false, defaultValue = "") List<String> sorts,
            @RequestParam(required = false, defaultValue = "-1") int limit,
            @RequestParam(required = false, defaultValue = "-1") long offset,
            @RequestBody List<JoinDetail> joins
    ) {
        ReadContext readContext = ReadContext.builder()
                .dbId(dbId)
                .schemaName(schemaName)
                .tableName(tableName)
                .fields(fields)
                .filter(filter)
                .sorts(sorts)
                .limit(limit)
                .defaultFetchLimit(db2RestConfigProperties.getDefaultFetchLimit())
                .offset(offset)
                .joins(joins)
                .build();

        return readService.findAll(readContext);
    }

}
