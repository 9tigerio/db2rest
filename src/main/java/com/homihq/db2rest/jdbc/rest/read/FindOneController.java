package com.homihq.db2rest.jdbc.rest.read;

import com.homihq.db2rest.jdbc.core.service.FindOneService;
import com.homihq.db2rest.jdbc.rest.read.dto.ReadContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@Slf4j
@RequiredArgsConstructor
public class FindOneController {

    private final FindOneService findOneService;

    @GetMapping("/{tableName}/one")
    public Map<String,Object> findOne(@PathVariable String tableName,
                                      @RequestParam(name = "fields", required = false, defaultValue = "*") String fields,
                                      @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {


        log.debug("tableName - {}", tableName);
        log.debug("fields - {}", fields);
        log.debug("filter - {}", filter);

        ReadContext readContext = ReadContext.builder()
                .tableName(tableName)
                .filter(filter)
                .fields(fields)
                .build();

        return this.findOneService.findOne(readContext);
    }


}
