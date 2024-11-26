package com.homihq.db2rest.jdbc.rest;

import com.homihq.db2rest.jdbc.core.service.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Slf4j
public abstract class RpcApi {
    private final RpcService service;

    protected RpcApi(RpcService service) {
        this.service = service;
    }

    public ResponseEntity<Map<String, Object>> execute(
            String dbId,
            String funcName,
            Map<String, Object> inParams) {
        log.debug("Execute function {} with IN params {}", funcName, inParams.entrySet());
        return ResponseEntity.ok(service.execute(dbId, funcName, inParams));
    }
}
