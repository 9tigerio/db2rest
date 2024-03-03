package com.homihq.db2rest.rest.rpc;

import com.homihq.db2rest.jdbc.service.JdbcFunctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("function")
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "db2rest.datasource", name = "type", havingValue = "jdbc")
public class FunctionController {

    private final JdbcFunctionService jdbcFunctionService;

    @PostMapping("/{funcName}")
    public ResponseEntity<Map<String, Object>> execute(@PathVariable String funcName,
                                                       @RequestBody Map<String,Object> inParams) {
        log.debug("Execute function {} with IN params {}", funcName, inParams.entrySet());
        return ResponseEntity.ok(jdbcFunctionService.execute(funcName, inParams));
    }
}
