package com.homihq.db2rest.jdbc.rest.update;

import com.homihq.db2rest.jdbc.core.service.UpdateService;
import com.homihq.db2rest.jdbc.dto.UpdateResponse;
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
    public UpdateResponse save(@PathVariable String tableName,
                               @RequestBody Map<String,Object> data
        , @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

        int rows = updateService.patch(null, tableName, data, filter);
        return new UpdateResponse(rows);
    }
}
