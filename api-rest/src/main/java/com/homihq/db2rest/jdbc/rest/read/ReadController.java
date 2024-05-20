package com.homihq.db2rest.jdbc.rest.read;

import com.homihq.db2rest.jdbc.core.service.ReadService;
import com.homihq.db2rest.jdbc.dto.JoinDetail;
import com.homihq.db2rest.jdbc.dto.ReadContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
@RestController
@Slf4j
@RequiredArgsConstructor
public class ReadController {

    private final ReadService readService;

    @GetMapping(value =VERSION + "/{dbId}/{tableName}" , produces = "application/json")
    public Object findAll(
                @PathVariable String dbId,
                        @PathVariable String tableName,
                          @RequestHeader(name="Accept-Profile", required = false) String schemaName,
                          @RequestParam(name = "fields", required = false, defaultValue = "*") String fields,
                          @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                          @RequestParam(name = "sort", required = false, defaultValue = "") List<String> sorts,
                          @RequestParam(name = "limit", required = false, defaultValue = "-1") int limit,
                          @RequestParam(name = "offset", required = false, defaultValue = "-1") long offset) {

        log.debug("filter - {}", filter);

        ReadContext readContext = ReadContext.builder()
                .dbId(dbId)
                .schemaName(schemaName)
                .tableName(tableName)
                .fields(fields)
                .filter(filter)
                .sorts(sorts)
                .limit(limit)
                .offset(offset)
                .build();


        return readService.findAll(readContext);
    }

    @PostMapping(value =VERSION +  "/{dbId}/{tableName}/_expand" , produces = "application/json")
    public Object find(
            @PathVariable String dbId,
            @PathVariable String tableName,
            @RequestHeader(name="Accept-Profile", required = false) String schemaName,
                       @RequestParam(name = "fields", required = false, defaultValue = "*") String fields,
                       @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                       @RequestParam(name = "sort", required = false, defaultValue = "") List<String> sorts,
                       @RequestParam(name = "limit", required = false, defaultValue = "-1") int limit,
                       @RequestParam(name = "offset", required = false, defaultValue = "-1") long offset,
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
                .offset(offset)
                .joins(joins)
                .build();


        return readService.findAll(readContext);

    }

}
