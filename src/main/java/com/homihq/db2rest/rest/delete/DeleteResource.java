package com.homihq.db2rest.rest.delete;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@RequiredArgsConstructor
public class DeleteResource {

    private final DeleteService deleteService;

    @DeleteMapping("/{tableName}")
    public void delete(@PathVariable String tableName,
                       @RequestHeader(name = "Content-Profile") String schemaName,
            @RequestParam(name = "filter", required = false, defaultValue = "") String filter) {

        deleteService.delete(schemaName, tableName, filter);
    }
}
