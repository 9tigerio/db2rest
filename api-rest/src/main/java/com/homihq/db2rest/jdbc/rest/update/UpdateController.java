package com.homihq.db2rest.jdbc.rest.update;

import com.homihq.db2rest.jdbc.core.service.UpdateService;
import com.homihq.db2rest.core.dto.UpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UpdateController {

    private final UpdateService updateService;
    @PatchMapping("/{dbName}/{tableName}")
    public UpdateResponse save(@PathVariable String dbName,
                               @PathVariable String tableName,
                               @RequestHeader(name="Content-Profile", required = false) String schemaName,
                               @RequestBody Map<String,Object> data
        , @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {


        int rows = updateService.patch(dbName,schemaName, tableName, data, filter);
        return new UpdateResponse(rows);
    }
}
