package com.homihq.db2rest.jdbc.rest.rpc;

import com.homihq.db2rest.jdbc.core.service.RpcService;
import com.homihq.db2rest.jdbc.rest.RpcApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import static com.homihq.db2rest.jdbc.rest.RdbmsRestApi.VERSION;
@RestController
@RequestMapping(VERSION + "/{dbId}/procedure")
@Slf4j
public class ProcedureController extends RpcApi {
    public ProcedureController(RpcService procedureService) {
        super(procedureService);
    }

    @Override
    @PostMapping("/{procName}")
    public ResponseEntity<Map<String, Object>> execute(
            @PathVariable String dbId,
            @PathVariable String procName,
            @RequestBody Map<String,Object> inParams) {
        return super.execute(dbId, procName, inParams);
    }
}
