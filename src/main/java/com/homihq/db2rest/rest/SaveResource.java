package com.homihq.db2rest.rest;

import com.homihq.db2rest.rest.service.SaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SaveResource {

    private final SaveService saveService;
    @PostMapping ("/{tableName}")
    public void save(@PathVariable String tableName,
                     @RequestBody Map<String,Object> data) {

        saveService.save(tableName, data);
    }

    @PostMapping ( "/{tableName}/bulk")
    public void saveBulk(@PathVariable String tableName,
                     @RequestParam(name = "batch", defaultValue = "false") boolean batch,
                     @RequestBody List<Map<String,Object>> data) {
        log.info("data -> {}", data);
        saveService.saveBulk(tableName,batch, data);
    }
}
