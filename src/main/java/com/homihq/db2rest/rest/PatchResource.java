package com.homihq.db2rest.rest;

import com.homihq.db2rest.rest.service.PatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PatchResource {

    private final PatchService patchService;
    @PatchMapping("/{tableName}")
    public void save(@PathVariable String tableName,
                     @RequestBody Map<String,Object> data
        ,@RequestParam(name = "rSql", required = false, defaultValue = "") String rSql) {

        patchService.patch(tableName, data, rSql);
    }
}
