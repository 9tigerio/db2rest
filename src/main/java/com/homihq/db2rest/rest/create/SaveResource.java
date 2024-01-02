package com.homihq.db2rest.rest.create;

import com.homihq.db2rest.rest.create.SaveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                     @RequestHeader(name = "Content-Profile") String schemaName,
                     @RequestBody Map<String,Object> data) {

        //saveService.save(schemaName, tableName, data);

        saveService.save2(schemaName, tableName, data);
    }

    @PostMapping ( "/{tableName}/bulk")
    public void saveBulk(@PathVariable String tableName,
                      @RequestHeader(name = "Content-Profile") String schemaName,
                     @RequestBody List<Map<String,Object>> data) {
        log.info("data -> {}", data);
        saveService.saveBulk(schemaName, tableName,data);
    }
}
