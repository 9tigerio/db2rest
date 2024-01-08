package com.homihq.db2rest.rest.rpc;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("function")
@Slf4j
@RequiredArgsConstructor
public class FunctionController {

    private final FunctionService functionService;

    @PostMapping("/{funcName}")
    public ResponseEntity<Map<String, Object>> execute(@PathVariable String funcName,
                                                       @RequestBody Map<String,Object> inParams) throws SQLException {
        return ResponseEntity.ok(functionService.execute(funcName, inParams));
    }
}
