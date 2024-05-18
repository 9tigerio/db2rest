package com.homihq.db2rest.jdbc.rest.rpc;

import com.homihq.db2rest.jdbc.core.service.FunctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
@RestController
@RequestMapping(VERSION + "/{dbId}/function")
@Slf4j
@RequiredArgsConstructor
public class FunctionController {

    private final FunctionService functionService;

    @PostMapping("/{funcName}")
    public ResponseEntity<Map<String, Object>> execute(
                @PathVariable String dbId,
                @PathVariable String funcName,
                @RequestBody Map<String,Object> inParams) {

        log.debug("Execute function {} with IN params {}", funcName, inParams.entrySet());

        return ResponseEntity.ok(functionService.execute(dbId, funcName, inParams));
    }
}
