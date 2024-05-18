package com.homihq.db2rest.jdbc.rest.rpc;

import com.homihq.db2rest.jdbc.core.service.ProcedureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
@RestController
@RequestMapping(VERSION + "/{dbId}/procedure")
@Slf4j
@RequiredArgsConstructor
public class ProcedureController {

    private final ProcedureService procedureService;

    @PostMapping("/{procId}")
    public ResponseEntity<Map<String, Object>> execute(
            @PathVariable String dbId,
            @PathVariable String procName,
                                                       @RequestBody Map<String,Object> inParams) {
        log.debug("Execute stored procedure {} with IN params {}", procName, inParams.entrySet());
        return ResponseEntity.ok(procedureService.execute(dbId,procName, inParams));
    }
}
