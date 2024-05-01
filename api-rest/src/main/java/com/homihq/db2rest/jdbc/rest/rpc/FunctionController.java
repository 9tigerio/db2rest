package com.homihq.db2rest.jdbc.rest.rpc;

import com.homihq.db2rest.jdbc.config.core.service.FunctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("function")
@Slf4j
@RequiredArgsConstructor
public class FunctionController {

    private final FunctionService functionService;

    @PostMapping("/{funcName}")
    public ResponseEntity<Map<String, Object>> execute(@PathVariable String funcName,
                                                       @RequestBody Map<String,Object> inParams) {
        log.debug("Execute function {} with IN params {}", funcName, inParams.entrySet());

        return ResponseEntity.ok(functionService.execute(funcName, inParams));
    }
}
