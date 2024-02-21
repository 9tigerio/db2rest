package com.homihq.db2rest.rest.read;

import com.homihq.db2rest.rest.read.dto.ReadContext;
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


        log.info("tableName - {}", tableName);
        log.info("fields - {}", fields);
        log.info("filter - {}", filter);

        ReadContext readContext = ReadContext.builder()
                .tableName(tableName)
                .filter(filter)
                .fields(fields)
                .build();

        return this.findOneService.findOne(readContext);
    }


}
