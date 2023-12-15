package com.homihq.db2rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
}
