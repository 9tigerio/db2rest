package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.jdbc.core.service.FindOneService;
import com.homihq.db2rest.jdbc.dto.ReadContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.homihq.db2rest.rest.RdbmsRestApi.VERSION;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FindOneController {

    private final FindOneService findOneService;

    @GetMapping(VERSION + "/{dbId}/{tableName}/one")
    public Map<String, Object> findOne(@PathVariable String dbId,
                                       @PathVariable String tableName,
                                       @RequestHeader(name = "Accept-Profile", required = false) String schemaName,
                                       @RequestParam(name = "fields", required = false, defaultValue = "*") String fields,
                                       @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {


        log.debug("tableName - {}", tableName);
        log.debug("fields - {}", fields);
        log.debug("filter - {}", filter);

        ReadContext readContext = ReadContext.builder()
                .dbId(dbId)
                .defaultFetchLimit(100) //todo update with config
                .schemaName(schemaName)
                .tableName(tableName)
                .filter(filter)
                .fields(fields)
                .build();

        return this.findOneService.findOne(readContext);
    }


}
