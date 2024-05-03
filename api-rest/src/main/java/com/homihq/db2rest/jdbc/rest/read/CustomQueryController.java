package com.homihq.db2rest.jdbc.rest.read;


import com.homihq.db2rest.jdbc.core.service.CustomQueryService;
import com.homihq.db2rest.jdbc.dto.QueryRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequiredArgsConstructor
public class CustomQueryController {

    private final CustomQueryService customQueryService;

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> findByCustomQuery(@RequestBody @Valid QueryRequest queryRequest) {
        log.debug("Execute SQL statement {} with params {}", queryRequest.sql(), queryRequest.params());
        return ResponseEntity.ok(customQueryService.find(queryRequest));
    }


}
