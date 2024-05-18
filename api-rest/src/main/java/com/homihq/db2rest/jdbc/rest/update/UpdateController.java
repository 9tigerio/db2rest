package com.homihq.db2rest.jdbc.rest.update;

import com.homihq.db2rest.jdbc.core.service.UpdateService;
import com.homihq.db2rest.core.dto.UpdateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
@RestController
@Slf4j
@RequiredArgsConstructor
public class UpdateController {

    private final UpdateService updateService;
    @PatchMapping(VERSION + "/{dbId}/{tableName}")
    public UpdateResponse save(@PathVariable String dbId,
                               @PathVariable String tableName,
                               @RequestHeader(name="Content-Profile", required = false) String schemaName,
                               @RequestBody Map<String,Object> data
        , @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {


        int rows = updateService.patch(dbId,schemaName, tableName, data, filter);
        return new UpdateResponse(rows);
    }
}
