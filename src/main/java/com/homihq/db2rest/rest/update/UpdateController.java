package com.homihq.db2rest.rest.update;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UpdateController {

    private final UpdateService updateService;
    @PatchMapping("/{tableName}")
    public void save(@PathVariable String tableName,
                     @RequestHeader(name = "Content-Profile") String schemaName,
                     @RequestBody Map<String,Object> data
        ,@RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

        updateService.patch(schemaName, tableName, data, filter);
    }
}
